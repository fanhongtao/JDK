
// $Id: SchemaFactoryFinder.java,v 1.14 2004/11/24 23:32:37 jsuttor Exp $

/*
 * @(#)SchemaFactoryFinder.java	1.12 05/01/04
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package javax.xml.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Implementation of {@link SchemaFactory#newInstance(String)}.
 * 
 * @author <a href="Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.14 $, $Date: 2004/11/24 23:32:37 $
 * @since 1.5
 */
class SchemaFactoryFinder  {

    /** debug support code. */
    private static boolean debug = false;
    /**
     *<p> Take care of restrictions imposed by java security model </p>
     */
    private static SecuritySupport ss = new SecuritySupport();
    /**
     * <p>Cache properties for performance.</p>
     */
	private static Properties cacheProps = new Properties();
    
	/**
	 * <p>First time requires initialization overhead.</p>
	 */
	private static boolean firstTime = true;
    
    static {
        // Use try/catch block to support applets
        try {
            debug = ss.getSystemProperty("jaxp.debug") != null;
        } catch (Exception _) {
            debug = false;
        }
    }

    /**
     * <p>Conditional debug printing.</p>
     * 
     * @param msg to print
     */
    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }
    
    /**
     * <p><code>ClassLoader</code> to use to find <code>SchemaFactory</code>.</p>
     */
    private final ClassLoader classLoader;
    
    /**
     * <p>Constructor that specifies <code>ClassLoader</code> to use
     * to find <code>SchemaFactory</code>.</p>
     * 
     * @param loader
     *      to be used to load resource, {@link SchemaFactory}, and
     *      {@link SchemaFactoryLoader} implementations during
     *      the resolution process.
     *      If this parameter is null, the default system class loader
     *      will be used.
     */
    public SchemaFactoryFinder(ClassLoader loader) {
        this.classLoader = loader;
        if( debug ) {
            debugDisplayClassLoader();
        }
    }
    
    private void debugDisplayClassLoader() {
        try {
            if( classLoader == ss.getContextClassLoader() ) {
                debugPrintln("using thread context class loader ("+classLoader+") for search");
                return;
            }
        } catch( Throwable _ ) {
            ; // getContextClassLoader() undefined in JDK1.1 
        }
        
        if( classLoader==ClassLoader.getSystemClassLoader() ) {
            debugPrintln("using system class loader ("+classLoader+") for search");
            return;
        }

        debugPrintln("using class loader ("+classLoader+") for search");
    }
    
    /**
     * <p>Creates a new {@link SchemaFactory} object for the specified
     * schema language.</p>
     * 
     * @param schemaLanguage
     *      See {@link SchemaFactory Schema Language} table in <code>SchemaFactory</code>
     *      for the list of available schema languages.
     * 
     * @return <code>null</code> if the callee fails to create one.
     * 
     * @throws NullPointerException
     *      If the <tt>schemaLanguage</tt> parameter is null.
     */
    public SchemaFactory newFactory(String schemaLanguage) {
        if(schemaLanguage==null)        throw new NullPointerException();
        SchemaFactory f = _newFactory(schemaLanguage);
        if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + schemaLanguage);
        } else {
            debugPrintln("unable to find a factory for " + schemaLanguage);
        }
        return f;
    }
    
    /**
     * <p>Lookup a <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.</p>
     * 
     * @param schemaLanguage Schema language to lookup <code>SchemaFactory</code> for.
     *  
     * @return <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.
     */
    private SchemaFactory _newFactory(String schemaLanguage) {
        SchemaFactory sf;
        
        String propertyName = SERVICE_CLASS.getName() + ":" + schemaLanguage;
        
        // system property look up
        try {
            debugPrintln("Looking up system property '"+propertyName+"'" );
            String r = ss.getSystemProperty(propertyName);
            if(r!=null) {
                debugPrintln("The value is '"+r+"'");
                sf = createInstance(r);
                if(sf!=null)    return sf;
            } else
                debugPrintln("The property is undefined.");
        } catch( Throwable t ) {
            if( debug ) {
                debugPrintln("failed to look up system property '"+propertyName+"'" );
                t.printStackTrace();
            }
        }

        String javah = ss.getSystemProperty( "java.home" );
        String configFile = javah + File.separator +
        "lib" + File.separator + "jaxp.properties";

        String factoryClassName = null ;

        // try to read from $java.home/lib/jaxp.properties
        try {
            if(firstTime){
                synchronized(cacheProps){
                    if(firstTime){
                        File f=new File( configFile );
                        firstTime = false;
                        if(ss.doesFileExist(f)){
                            debugPrintln("Read properties file " + f);                                
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            factoryClassName = cacheProps.getProperty(propertyName);            
            debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties"); 

            if (factoryClassName != null) {
                sf = createInstance(factoryClassName);
                if(sf != null){
                    return sf;
                }
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            } 
        }

        /**
        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah = ss.getSystemProperty( "java.home" );
            String configFile = javah + File.separator +
            "lib" + File.separator + "jaxp.properties";
            File f = new File( configFile );
            if( ss.doesFileExist(f)) {
                sf = loadFromProperty(
                        propertyName,f.getAbsolutePath(), new FileInputStream(f));
                if(sf!=null)    return sf;
            } else {
                debugPrintln("Tried to read "+ f.getAbsolutePath()+", but it doesn't exist.");
            }
        } catch(Throwable e) {
            if( debug ) {
                debugPrintln("failed to read $java.home/lib/jaxp.properties");
                e.printStackTrace();
            }
        }
         */
        
        // try META-INF/services files
        Iterator sitr = createServiceFileIterator();
        while(sitr.hasNext()) {
            URL resource = (URL)sitr.next();
            debugPrintln("looking into " + resource);
            try {
                //sf = loadFromProperty(schemaLanguage,resource.toExternalForm(),resource.openStream());
                sf = loadFromProperty(schemaLanguage,resource.toExternalForm(),ss.getURLInputStream(resource));
                if(sf!=null)    return sf;
            } catch(IOException e) {
                if( debug ) {
                    debugPrintln("failed to read "+resource);
                    e.printStackTrace();
                }
            }
        }
        
        // platform default
        if(schemaLanguage.equals("http://www.w3.org/2001/XMLSchema")) {
            debugPrintln("attempting to use the platform default XML Schema validator");
            return createInstance("com.sun.org.apache.xerces.internal.jaxp.validation.xs.SchemaFactoryImpl");
        }
        
        debugPrintln("all things were tried, but none was found. bailing out.");
        return null;
    }
    
    /**
     * <p>Creates an instance of the specified and returns it.</p>
     * 
     * @param className
     *      fully qualified class name to be instanciated.
     * 
     * @return null
     *      if it fails. Error messages will be printed by this method. 
     */
    private SchemaFactory createInstance( String className ) {
        try {
            debugPrintln("instanciating "+className);
            Class clazz;
            if( classLoader!=null )
                clazz = classLoader.loadClass(className);
            else
                clazz = Class.forName(className);
            if(debug)       debugPrintln("loaded it from "+which(clazz));
            Object o = clazz.newInstance();
            
            if( o instanceof SchemaFactory )
                return (SchemaFactory)o;
            
            debugPrintln(className+" is not assignable to "+SERVICE_CLASS.getName());
        } catch( Throwable t ) {
            debugPrintln("failed to instanciate "+className);
            if(debug)   t.printStackTrace();
        }
        return null;
    }
    
    /** Iterator that lazily computes one value and returns it. */
    private static abstract class SingleIterator implements Iterator {
        private boolean seen = false;
        
        public final void remove() { throw new UnsupportedOperationException(); }
        public final boolean hasNext() { return !seen; }
        public final Object next() {
            if(seen)    throw new NoSuchElementException();
            seen = true;
            return value();
        }
        
        protected abstract Object value();
    }
    
    /**
     * Looks up a value in a property file
     * while producing all sorts of debug messages.
     * 
     * @return null
     *      if there was an error.
     */
    private SchemaFactory loadFromProperty( String keyName, String resourceName, InputStream in )
        throws IOException {
        debugPrintln("Reading "+resourceName );
        
        Properties props=new Properties();
        props.load(in);
        in.close();
        String factoryClassName = props.getProperty(keyName);
        if(factoryClassName != null){
            debugPrintln("found "+keyName+" = " + factoryClassName);
            return createInstance(factoryClassName);
        } else {
            debugPrintln(keyName+" is not in the property file");
            return null;
        }
    }
    
    /**
     * Returns an {@link Iterator} that enumerates all 
     * the META-INF/services files that we care.
     */
    private Iterator createServiceFileIterator() {
        if (classLoader == null) {
            return new SingleIterator() {
                protected Object value() {
                    ClassLoader classLoader = SchemaFactoryFinder.class.getClassLoader();
                    //return (ClassLoader.getSystemResource( SERVICE_ID ));
                    return ss.getResourceAsURL(classLoader, SERVICE_ID);
                }
            };
        } else {
            try {
                //final Enumeration e = classLoader.getResources(SERVICE_ID);
                final Enumeration e = ss.getResources(classLoader, SERVICE_ID);
                if(!e.hasMoreElements()) {
                    debugPrintln("no "+SERVICE_ID+" file was found");
                }
                
                // wrap it into an Iterator.
                return new Iterator() {
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    public Object next() {
                        return e.nextElement();
                    }
                };
            } catch (IOException e) {
                debugPrintln("failed to enumerate resources "+SERVICE_ID);
                if(debug)   e.printStackTrace();
                return new ArrayList().iterator();  // empty iterator
            }
        }
    }
    
    private static final Class SERVICE_CLASS = SchemaFactory.class;
    private static final String SERVICE_ID = "META-INF/services/" + SERVICE_CLASS.getName();
    
    
    
    private static String which( Class clazz ) {
        return which( clazz.getName(), clazz.getClassLoader() );
    }

    /**
     * <p>Search the specified classloader for the given classname.</p>
     *
     * @param classname the fully qualified name of the class to search for
     * @param loader the classloader to search
     * 
     * @return the source location of the resource, or null if it wasn't found
     */
    private static String which(String classname, ClassLoader loader) {

        String classnameAsResource = classname.replace('.', '/') + ".class";
        
        if( loader==null )  loader = ClassLoader.getSystemClassLoader();
        
        //URL it = loader.getResource(classnameAsResource);
        URL it = ss.getResourceAsURL(loader, classnameAsResource);
        if (it != null) {
            return it.toString();
        } else {
            return null;
        }
    }
}
