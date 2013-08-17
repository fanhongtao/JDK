/*
 * @(#)ResourceBundle.java  1.24 98/01/15
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1998 Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package java.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.lang.ref.SoftReference;

/**
 *
 * Resource bundles contain locale-specific objects.
 * When your program needs a locale-specific resource,
 * a <code>String</code> for example, your program can load it
 * from the resource bundle that is appropriate for the
 * current user's locale. In this way, you can write
 * program code that is largely independent of the user's
 * locale isolating most, if not all, of the locale-specific
 * information in resource bundles.
 *
 * <p>
 * This allows you to write programs that can:
 * <UL type=SQUARE>
 * <LI> be easily localized, or translated, into different languages
 * <LI> handle multiple locales at once
 * <LI> be easily modified later to support even more locales
 * </UL>
 *
 * <P>
 * One resource bundle is, conceptually, a set of related classes that
 * inherit from <code>ResourceBundle</code>. Each related subclass of
 * <code>ResourceBundle</code> has the same base name plus an additional
 * component that identifies its locale. For example, suppose your resource
 * bundle is named <code>MyResources</code>. The first class you are likely
 * to write is the default resource bundle which simply has the same name as
 * its family--<code>MyResources</code>. You can also provide as
 * many related locale-specific classes as you need: for example, perhaps
 * you would provide a German one named <code>MyResources_de</code>.
 *
 * <P>
 * Each related subclass of <code>ResourceBundle</code> contains the same
 * items, but the items have been translated for the locale represented by that
 * <code>ResourceBundle</code> subclass. For example, both <code>MyResources</code>
 * and <code>MyResources_de</code> may have a <code>String</code> that's used
 * on a button for confirming operations. In <code>MyResources</code> the
 * <code>String</code> may contain <code>OK</code> and in
 * <code>MyResources_de</code> it may contain <code>Gut</code>.
 *
 * <P>
 * If there are different resources for different countries, you
 * can make specializations: for example, <code>MyResources_de_CH</code>
 * is the German language (de) in Switzerland (CH). If you want to only
 * modify some of the resources
 * in the specialization, you can do so.
 *
 * <P>
 * When your program needs a locale-specific object, it loads
 * the <code>ResourceBundle</code> class using the <code>getBundle</code>
 * method:
 * <blockquote>
 * <pre>
 * ResourceBundle myResources =
 *      ResourceBundle.getBundle("MyResources", currentLocale);
 * </pre>
 * </blockquote>
 * The first argument specifies the family name of the resource
 * bundle that contains the object in question. The second argument
 * indicates the desired locale. <code>getBundle</code>
 * uses these two arguments to construct the name of the
 * <code>ResourceBundle</code> subclass it should load as follows.
 *
 * <P>
 * The resource bundle lookup searches for classes with various suffixes
 * on the basis of (1) the desired locale and (2) the current default locale
 * as returned by Locale.getDefault(), and (3) the root resource bundle (baseclass),
 * in the following order from lower-level (more specific) to parent-level
 * (less specific):
 * <p> baseclass + "_" + language1 + "_" + country1 + "_" + variant1
 * <BR> baseclass + "_" + language1 + "_" + country1
 * <BR> baseclass + "_" + language1
 * <BR> baseclass + "_" + language2 + "_" + country2 + "_" + variant2
 * <BR> baseclass + "_" + language2 + "_" + country2
 * <BR> baseclass + "_" + language2
 * <BR> baseclass
 *
 * <P>
 * For example, if the current default locale is <TT>en_US</TT>, the locale the caller
 * is interested in is <TT>fr_CH</TT>, and the resource bundle name is <TT>MyResources</TT>,
 * resource bundle lookup will search for the following classes, in order:
 * <BR> <TT>MyResources_fr_CH
 * <BR> MyResources_fr
 * <BR> MyResources_en_US
 * <BR> MyResources_en
 * <BR> MyResources</TT>
 *
 * <P>
 * The result of the lookup is a class, but that class may be
 * backed by a property file on disk. If a lookup fails,
 * <code>getBundle()</code> throws a <code>MissingResourceException</code>.
 *
 * <P>
 * The baseclass <strong>must</strong> be fully
 * qualified (for example, <code>myPackage.MyResources</code>, not just
 * <code>MyResources</code>). It must
 * also be accessable by your code; it cannot be a class that is private
 * to the package where <code>ResourceBundle.getBundle</code> is called.
 *
 * <P>
 * Note: <code>ResourceBundle</code> are used internally in accessing
 * <code>NumberFormat</code>s, <code>Collation</code>s, and so on.
 * The lookup strategy is the same.
 *
 * <P>
 * Resource bundles contain key/value pairs. The keys uniquely
 * identify a locale-specific object in the bundle. Here's an
 * example of a <code>ListResourceBundle</code> that contains
 * two key/value pairs:
 * <blockquote>
 * <pre>
 * class MyResource extends ListResourceBundle {
 *      public Object[][] getContents() {
 *              return contents;
 *      }
 *      static final Object[][] contents = {
 *      // LOCALIZE THIS
 *              {"OkKey", "OK"},
 *              {"CancelKey", "Cancel"},
 *      // END OF MATERIAL TO LOCALIZE
 *      };
 * }
 * </pre>
 * </blockquote>
 * Keys are always <code>String</code>s.
 * In this example, the keys are <code>OkKey</code> and <code>CancelKey</code>.
 * In the above example, the values
 * are also <code>String</code>s--<code>OK</code> and <code>Cancel</code>--but
 * they don't have to be. The values can be any type of object.
 *
 * <P>
 * You retrieve an object from resource bundle using the appropriate
 * getter method. Because <code>OkKey</code> and <code>CancelKey</code>
 * are both strings, you would use <code>getString</code> to retrieve them:
 * <blockquote>
 * <pre>
 * button1 = new Button(myResourceBundle.getString("OkKey"));
 * button2 = new Button(myResourceBundle.getString("CancelKey"));
 * </pre>
 * </blockquote>
 * The getter methods all require the key as an argument and return
 * the object if found. If the object is not found, the getter method
 * throws a <code>MissingResourceException</code>.
 *
 * <P>
 * Besides <code>getString</code>; ResourceBundle supports a number
 * of other methods for getting different types of objects such as
 * <code>getStringArray</code>. If you don't have an object that
 * matches one of these methods, you can use <code>getObject</code>
 * and cast the result to the appropriate type. For example:
 * <blockquote>
 * <pre>
 * int[] myIntegers = (int[]) myResources.getObject("intList");
 * </pre>
 * </blockquote>
 *
 * <P>
 * <STRONG>NOTE:</STRONG> You should always supply a baseclass with
 * no suffixes. This will be the class of "last resort", if a locale
 * is requested that does not exist. In fact, you must provide <I>all</I>
 * of the classes in any given inheritance chain that you provide a resource
 * for.  For example, if you provide <TT>MyResources_fr_BE</TT>, you must provide
 * <I>both</I> <TT>MyResources</TT> <I>and</I> <TT>MyResources_fr</TT> or
 * the resource bundle lookup won't work right.
 *
 * <P>
 * The JDK provides two subclasses of <code>ResourceBundle</code>,
 * <code>ListResourceBundle</code> and <code>PropertyResourceBundle</code>,
 * that provide a fairly simple way to create resources. (Once serialization
 * is fully integrated, we will provide another
 * way.) As you saw briefly in a previous example, <code>ListResourceBundle</code>
 * manages its resource as a List of key/value pairs.
 * <code>PropertyResourceBundle</code> uses a properties file to manage
 * its resources.
 *
 * <p>
 * If <code>ListResourceBundle</code> or <code>PropertyResourceBundle</code>
 * do not suit your needs, you can write your own <code>ResourceBundle</code>
 * subclass.  Your subclasses must override two methods: <code>handleGetObject</code>
 * and <code>getKeys()</code>.
 *
 * <P>
 * The following is a very simple example of a <code>ResourceBundle</code>
 * subclass, MyResources, that manages two resources (for a larger number of
 * resources you would probably use a <code>Hashtable</code>). Notice that if
 * the key is not found, <code>handleGetObject</code> must return null. Notice
 * also that you don't need to supply a value if a "parent-level"
 * <code>ResourceBundle</code> handles the same
 * key with the same value (as in United Kingdom below).  Also notice that because
 * you specify an <TT>en_GB</TT> resource bundle, you also have to provide a default <TT>en</TT>
 * resource bundle even though it inherits all its data from the root resource bundle.
 * <p><strong>Example:</strong>
 * <blockquote>
 * <pre>
 * // default (English language, United States)
 * abstract class MyResources extends ResourceBundle {
 *     public Object handleGetObject(String key) {
 *         if (key.equals("okKey")) return "Ok";
 *         if (key.equals("cancelKey")) return "Cancel";
 *     return null;
 *     }
 * }
 *
 * // German language
 * public class MyResources_de extends MyResources {
 *     public Object handleGetObject(String key) {
 *         if (key.equals("okKey")) return "Gut";
 *         if (key.equals("cancelKey")) return "Vernichten";
 *         return null;
 *     }
 * }
 *
 * // English language, default (must provide even though all the data is
 * // in the root locale)
 * public class MyResources_en extends MyResources {
 *     public Object handleGetObject(String key) {
 *         return null;
 *     }
 * }
 *
 * // English language, United Kingdom (Great Britain)
 * public class MyResources_en_GB extends MyResources {
 *     public Object handleGetObject(String key) {
 *         // don't need okKey, since parent level handles it.
 *         if (key.equals("cancelKey")) return "Dispose";
 *         return null;
 *     }
 * }
 * </pre>
 * </blockquote>
 * You do not have to restrict yourself to using a single family of
 * <code>ResourceBundle</code>s. For example, you could have a set of bundles for
 * exception messages, <code>ExceptionResources</code>
 * (<code>ExceptionResources_fr</code>, <code>ExceptionResources_de</code>, ...),
 * and one for widgets, <code>WidgetResource</code> (<code>WidgetResources_fr</code>,
 * <code>WidgetResources_de</code>, ...); breaking up the resources however you like.
 *
 * @see ListResourceBundle
 * @see PropertyResourceBundle
 * @see MissingResourceException
 */
abstract public class ResourceBundle {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    public ResourceBundle() {
    }

    /**
     * Get an object from a ResourceBundle.
     * <BR>Convenience method to save casting.
     * @param key see class description.
     */
    public final String getString(String key) throws MissingResourceException {
        return (String) getObject(key);
    }

    /**
     * Get an object from a ResourceBundle.
     * <BR>Convenience method to save casting.
     * @param key see class description.
     */
    public final String[] getStringArray(String key)
        throws MissingResourceException {
        return (String[]) getObject(key);
    }

    /**
     * Get an object from a ResourceBundle.
     * @param key see class description.
     */
    public final Object getObject(String key) throws MissingResourceException {
        Object obj = handleGetObject(key);
        if (obj == null) {
            if (parent != null) {
                obj = parent.getObject(key);
            }
            if (obj == null)
                throw new MissingResourceException("Can't find resource",
                                                   this.getClass().getName(),
                                                   key);
        }
        return obj;
    }


    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     */
    public static final ResourceBundle getBundle(String baseName)
        throws MissingResourceException
    {
        return getBundle(baseName, Locale.getDefault(),
        /* must determine loader here, else we break stack invariant */
        getLoader());
    }


    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     * @param locale   see class description.
     */
    public static final ResourceBundle getBundle(String baseName,
                                                         Locale locale)
    {
        return getBundle(baseName, locale, getLoader());
    }

    /**
     * Return the Locale for this ResourceBundle.  (This function can be used after a
     * call to getBundle() to determine whether the ResourceBundle returned really
     * corresponds to the requested locale or is a fallback.)
     */
    public Locale getLocale() {
        String className = getClass().getName();
        int pos = className.indexOf('_');
        if (pos == -1)
            return new Locale("", "", "");

        className = className.substring(pos + 1);
        pos = className.indexOf('_');
        if (pos == -1)
            return new Locale(className, "", "");

        String language = className.substring(0, pos);
        className = className.substring(pos + 1);
        pos = className.indexOf('_');
        if (pos == -1)
            return new Locale(language, className, "");

        String country = className.substring(0, pos);
        className = className.substring(pos + 1);

        return new Locale(language, country, className);
    }

    /*
     * Automatic determination of the ClassLoader to be used to load
     * resources on behalf of the client.  N.B. The client is getLoader's
     * caller's caller.
     */
    private static ClassLoader getLoader() {
        Class[] stack = getClassContext();
        /* Magic number 2 identifies our caller's caller */
        Class c = stack[2];
        ClassLoader cl = (c == null) ? null : c.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    private static native Class[] getClassContext();

    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     * @param locale see class description.
     * @param loader the ClassLoader to load the resource from
     */
    public static ResourceBundle getBundle(String baseName, Locale locale,
                                           ClassLoader loader)
        throws MissingResourceException
    {
        StringBuffer localeName
            = new StringBuffer("_").append(locale.toString());
        if (locale.toString().equals(""))
            localeName.setLength(0);

        ResourceBundle lookup = findBundle(baseName,localeName,loader,false);
        if(lookup == null) {
            localeName.setLength(0);
            localeName.append("_").append( Locale.getDefault().toString() );
            lookup = findBundle(baseName, localeName, loader, true);
            if( lookup == null ) {
                throw new MissingResourceException("can't find resource for "
                                                   + baseName + "_" + locale,
                                                   baseName + "_" + locale,"");
            }
        }

        // Setup lookup's ancestry. If we find an ancestor whose parent is null,
        // we set up the ancestor's parent as well.
        ResourceBundle child = lookup;
        while( (child != null) && (child.parent == null) ) {
            // Chop off the last component of the locale name and search for that
            // as the parent locale.  Use it to set the parent of current child.
            int lastUnderbar = localeName.toString().lastIndexOf('_');
            if( lastUnderbar != -1 ) {
                localeName.setLength(lastUnderbar);
                child.setParent( findBundle(baseName,localeName,loader,true) );
            }
            child = child.parent;
        }

        return lookup;
    }


    /**
     * Set the parent bundle of this bundle.  The parent bundle is
     * searched by getObject when this bundle does not contain a
     * particular resource.
     * @param parent this bundle's parent bundle.
     */
    protected void setParent( ResourceBundle parent ) {
        this.parent = parent;
    }


    /**
     * The internal routine that does the real work of finding and loading
     * the right ResourceBundle for a given name and locale.
     */
    private static ResourceBundle findBundle(String baseName,
                                             StringBuffer localeName,
                                             final ClassLoader loader,
                                             boolean includeBase)
    {
        String localeStr = localeName.toString();
        String baseFileName = baseName.replace('.', '/');
        Object lookup = null;
        String searchName;
        Vector cacheCandidates = new Vector();
        int lastUnderbar;
        InputStream stream;

        searchLoop:
        while (true) {
            searchName = baseName + localeStr;
            String cacheName;
            if (loader != null) {
                cacheName = "["+Integer.toString(loader.hashCode())+"]";
            } else {
                cacheName = "";
            }
            cacheName += searchName;

            // First, look in the cache.  We may either find the bundle we're
            // looking for or we may find that the bundle was not found by a
            // previous search.
            synchronized (cacheList) {
                SoftReference ref = (SoftReference)(cacheList.get(cacheName));
                if (ref != null)
                    lookup = ref.get();
                else
                    lookup = null;
            }
            if( lookup == NOTFOUND ) {
                localeName.setLength(0);
                break searchLoop;
            }
            if( lookup != null ) {
                localeName.setLength(0);
                break searchLoop;
            }
            cacheCandidates.addElement( cacheName );

            // Next search for a class
            try {
                if (loader != null) {
                    lookup = (ResourceBundle)(loader.loadClass(searchName).newInstance());
                } else {
                    lookup = (ResourceBundle)(Class.forName(searchName).newInstance());
                }
                break searchLoop;
            } catch( Exception e ){}

            // Next search for a Properties file.
            searchName = baseFileName + localeStr + ".properties";
	    final String resName = searchName;
	    stream = (InputStream)java.security.AccessController.doPrivileged
		(new java.security.PrivilegedAction() {
		    public Object run() {
			if (loader != null) {
			    return loader.getResourceAsStream
							(resName);
			} else {
			    return ClassLoader.getSystemResourceAsStream
							(resName);
			}
		    }
		});
            if( stream != null ) {
                // make sure it is buffered
                stream = new java.io.BufferedInputStream(stream);
                try {
                    lookup = (Object)new PropertyResourceBundle( stream );
                    break searchLoop;
                }
                catch (Exception e) {
                }
                finally {
                    try {
                        stream.close();
                    }
                    catch (Exception e) {
                        // to avoid propagating an IOException back into the caller
                        // (I'm assuming this is never going to happen, and if it does,
                        // I'm obeying the precedent of swallowing exceptions set by the
                        // existing code above)
                    }
                }
            }

            //Chop off the last part of the locale name string and try again.
            lastUnderbar = localeStr.lastIndexOf('_');
            if( ((lastUnderbar==0)&&(!includeBase)) || (lastUnderbar == -1) ) {
                break;
            }
            localeStr = localeStr.substring(0,lastUnderbar);
            localeName.setLength(lastUnderbar);
        }



        // If we searched all the way to the base, then we can add
        // the NOTFOUND result to the cache.  Otherwise we can say
        // nothing.
        if (lookup == null && includeBase == true)
            lookup = NOTFOUND;

        if( lookup != null )
            synchronized (cacheList) {
                // Add a positive result to the cache. The result may include
                // NOTFOUND
                for( int i=0; i<cacheCandidates.size(); i++ ) {
                    cacheList.put(cacheCandidates.elementAt(i), new SoftReference(lookup));
                }
            }

        if( (lookup == NOTFOUND) || (lookup == null) )
            return null;
        else
            return (ResourceBundle)lookup;
    }


    /** Get an object from a ResourceBundle.
     * <STRONG>NOTE: </STRONG>Subclasses must override.
     * @param key see class description.
     */
    protected abstract Object handleGetObject(String key)
        throws MissingResourceException;

    /**
     * Return an enumeration of the keys.
     * <STRONG>NOTE: </STRONG>Subclasses must override.
     */
    public abstract Enumeration getKeys();

    /**
     * For printf debugging.
     */
    private static final boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("ResourceBundle: " + str);
        }
    }

    /**
     * The parent bundle is consulted by getObject when this bundle
     * does not contain a particular resource.
     */
    protected ResourceBundle parent = null;

    private static final Integer NOTFOUND = new Integer(-1);
    private static Hashtable cacheList = new Hashtable();
}
