/*
 * @(#)ClassLoader.java	1.62 00/02/11
 *
 * Copyright 1995-2000 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

import java.io.InputStream;
import java.util.Hashtable;

/**
 * The class <code>ClassLoader</code> is an abstract class. 
 * Applications implement subclasses of <code>ClassLoader</code> in 
 * order to extend the manner in which the Java Virtual Machine 
 * dynamically loads classes. 
 * <p>
 * Normally, the Java Virtual Machine loads classes from the local 
 * file system in a platform-dependent manner. For example, on UNIX 
 * systems, the Virtual Machine loads classes from the directory 
 * defined by the <code>CLASSPATH</code> environment variable. 
 * <p>
 * However, some classes may not originate from a file; they may 
 * originate from other sources, such as the network, or they could 
 * be constructed by an application. The method 
 * <code>defineClass</code> converts an array of bytes into an 
 * instance of class <code>Class</code>. Instances of this newly 
 * defined class can be created using the <code>newInstance</code> 
 * method in class <code>Class</code>. 
 * <p>
 * The methods and constructors of objects created by a class loader 
 * may reference other classes. To determine the class(es) referred 
 * to, the Java Virtual Machine calls the <code>loadClass</code> 
 * method of the class loader that originally created the class. If 
 * the Java Virtual Machine only needs to determine if the class 
 * exists and if it does exist to know its superclass, the 
 * <code>resolve</code> flag is set to <code>false</code>. However, 
 * if an instance of the class is being created or any of its methods 
 * are being called, the class must also be resolved. In this case 
 * the <code>resolve</code> flag is set to <code>true</code>, and the 
 * <code>resolveClass</code> method should be called. 
 * <p>
 * For example, an application could create a network class loader 
 * to download class files from a server. Sample code might look like:
 * <ul><code>
 *   ClassLoader loader&nbsp;= new NetworkClassLoader(host,&nbsp;port);<br>
 *   Object main&nbsp;= loader.loadClass("Main", true).newInstance();<br>
 *	 &nbsp;.&nbsp;.&nbsp;.
 * </code></ul>
 * <p>
 * The network class loader subclass must define the method 
 * <code>loadClass</code> to load a class from the network. Once it 
 * has downloaded the bytes that make up the class, it should use the 
 * method <code>defineClass</code> to create a class instance. A 
 * sample implementation is: 
 * <p><hr><blockquote><pre>
 *     class NetworkClassLoader {
 *         String host;
 *         int port;
 *         Hashtable cache = new Hashtable();
 *         private byte loadClassData(String name)[] {
 *         // load the class data from the connection
 *         &nbsp;.&nbsp;.&nbsp;.
 *         }
 * 
 *         public synchronized Class loadClass(String name,
 *                                             boolean resolve) {
 *             Class c = cache.get(name);
 *             if (c == null) {
 *                 byte data[] = loadClassData(name);
 *                 c = defineClass(data, 0, data.length);
 *                 cache.put(name, c);
 *             }
 *             if (resolve)
 *                 resolveClass(c);
 *             return c;
 *         }
 *     }
 * </pre></blockquote><hr>
 *
 * @author  Arthur van Hoff
 * @version 1.62, 02/11/00
 * @see     java.lang.Class
 * @see     java.lang.Class#newInstance()
 * @see     java.lang.ClassLoader#defineClass(byte[], int, int)
 * @see     java.lang.ClassLoader#loadClass(java.lang.String, boolean)
 * @see     java.lang.ClassLoader#resolveClass(java.lang.Class)
 * @since   JDK1.0
 */
public abstract class ClassLoader {
    /**
     * If initialization succeed this is set to true and security checks will
     * succeed. Otherwise the object is not initialized and the object is
     * useless.
     */
    private boolean initialized = false;

    /**
     * This is a mapping of all String => Class done by this classloader.
     * Each Class in the table will either have a null class loader, or 
     * "this" as its classloader
     */
    private Hashtable classes = new Hashtable();

    /**
     * Constructs a new class loader and initializes it. 
     * <p>
     * If there is a security manager, its 
     * <code>checkCreateClassLoader</code> method is called. This may 
     * result in a security exception. 
     *
     * @exception  SecurityException  if the current thread does not have
     *               permission to create a new class loader.
     * @see       java.lang.SecurityException
     * @see       java.lang.SecurityManager#checkCreateClassLoader()
     * @since     JDK1.0
     */
    protected ClassLoader() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkCreateClassLoader();
	}
	init();
	initialized = true;
    }

    /**
     * Requests the class loader to load and resolve a class with the specified 
     * name. The <code>loadClass</code> method is called by the Java 
     * Virtual Machine when a class loaded by a class loader first 
     * references another class. Every subclass of class 
     * <code>ClassLoader</code> must define this method. 
     * <p>
     * Class loaders should use a hashtable or other cache to avoid 
     * defining classes with the same name multiple times. 
     *
     * @param      name      the name of the desired <code>Class</code>.
     * @return     the resulting <code>Class</code>, or <code>null</code>
     *             if it was not found.
     * @exception  ClassNotFoundException  if the class loader cannot find
     *               a definition for the class.
     * @since      JDK1.1
     */
    public Class loadClass(String name) throws ClassNotFoundException
    {
	return loadClass(name, true);
    }

    /**
     * Resolves the specified name to a Class. The method loadClass() is 
     * called by the virtual machine.
     * <p>
     * If the <code>resolve</code> flag is true, the method should call 
     * the <code>resolveClass</code> method on the resulting class object.
     * <p>
     * As an abstract method, loadClass() must be defined in a subclass of 
     * ClassLoader. By using a Hashtable, you can avoid loading the same 
     * Class more than once.
     * 
     * @param	   name     the name of the desired Class.
     * @param      resolve  true if the Class needs to be resolved.
     * @return	   the resulting Class, or null if it was not found.
     * @exception  ClassNotFoundException  if the class loader cannot find
     *               a definition for the class.
     * @see	   java.util.Hashtable
     * @since      JDK1.0
     */
    protected abstract Class loadClass(String name, boolean resolve)
    throws ClassNotFoundException;

    /**
     * Converts an array of bytes into an instance of class 
     * <code>Class</code>. 
     * Before the Class can be used it must be resolved.  This
     * method is deprecated in favor of the version that takes a
     * "name" as a first argument, and is more secure.
     *
     * @param      data     the bytes that make up the <code>Class</code>.
     * @param      offset   the start offset of the <code>Class</code> data.
     * @param      length   the length of the <code>Class</code> data.
     * @return     the <code>Class</code> object that was created from the data.
     * @exception  ClassFormatError  if the data does not contain a valid Class.
     * @see        ClassLoader#loadClass(java.lang.String, boolean)
     * @see        ClassLoader#resolveClass(java.lang.Class)
     * @since      JDK1.0
     * @deprecated Replaced by defineClass(java.lang.String, byte[], int, int).
     */
    protected final Class defineClass(byte data[], int offset, int length) { 
	return defineClass(null, data, offset, length);
    }

    /**
     * Converts an array of bytes to an instance of class
     * Class. Before the Class can be used it must be resolved.
     *
     * @param	   name     the expected name of the class; null if unknown;
     *                      using '.' and not '/' as separator, and without
     *                      a trailing ".class" suffix.
     * @param      data     the bytes that make up the <code>Class</code>.
     * @param      offset   the start offset of the <code>Class</code> data.
     * @param      length   the length of the <code>Class</code> data.
     * @return     the <code>Class</code> object that was created from the data.
     * @exception  ClassFormatError  if the data does not contain a valid Class.
     * @see        ClassLoader#loadClass(java.lang.String, boolean)
     * @see        ClassLoader#resolveClass(java.lang.Class)
     * @since      JDK1.1
     */
    protected final Class defineClass(String name,
				      byte data[], int offset, int length) { 
	check();
	Class result = defineClass0(name, data, offset, length);
	if (result != null) 
	    classes.put(result.getName(), result);
	return result;
    }

    /**
     * Resolves the class so that an instance of the class can be 
     * created, or so that one of its methods can be called. This method 
     * should be called by <code>loadClass</code> if the resolve flag is 
     * <code>true</code>. 
     *
     * @param   c   the <code>Class</code> instance to be resolved.
     * @see 	java.lang.ClassLoader#defineClass(java.lang.String, byte[], int, int)
     * @since   JDK1.0
     */
    protected final void resolveClass(Class c) { 
	check();
	resolveClass0(c);
    }

    /**
     * Finds the system class with the specified name, loading it in if 
     * necessary. 
     * <p>
     * A system class is a class loaded from the local file system in a 
     * platform- dependent way. It has no class loader. 
     *
     * @param      name   the name of the system <code>class</code>.
     * @return     a system class with the given name.
     * @exception  ClassNotFoundException  if it could not find a definition
     *               for the class.
     * @exception  NoClassDefFoundError    if the class is not found.
     * @since      JDK1.0
     */
    protected final Class findSystemClass(String name) 
    throws ClassNotFoundException {
	check();
	return findSystemClass0(name);
    }

    /**
     * Sets the signers of a class. This is called after defining a class,
     * by signature-aware class loading code.
     *
     * @since   JDK1.1
     */
    protected final void setSigners(Class cl, Object[] signers) {
        check();
	// make a check which will take cl.getClassLoader and
	// check if it is != this.
	cl.setSigners(signers);
    }

    /**
     * Initializes the Class loader.
     */
    private native void init();
    private native Class defineClass0(String name, 
				      byte data[], int offset, int length);
    private native void resolveClass0(Class c);
    private native Class findSystemClass0(String name) 
        throws ClassNotFoundException;

    private void check() { 
	if (initialized == true) 
	    return;
	throw new SecurityException("ClassLoader object not initialized.");
    }

    /**
     * @since   JDK1.1
     */
    final protected Class findLoadedClass(String name) { 
	return (Class)classes.get(name);
    }

    /**
     * Load and resolve a class.
     */
    final Class loadClassInternal(String name, boolean resolve) 
                    throws ClassNotFoundException {
	Class cl;
	name = name.replace('/', '.');
        checkLoadClass(name);
	cl = (Class)classes.get(name);
	if (cl == null) {
	    cl = loadClass(name, false);
	    if (cl == null) 
		throw new ClassNotFoundException(name);
	    String realName = cl.getName();
	    if (!realName.equals(name)) { 
		throw new ClassNotFoundException(name);
	    }
	    classes.put(realName, cl);
	}
	if (resolve) 
	    resolveClass(cl);
	return cl;
    }

    private void checkLoadClass(String name)
    {
        int i = name.lastIndexOf('.');

        if (i != -1) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPackageAccess(name.substring(0, i));
            }
        }
    }

    /**
     * A resource is some data (images, audio, text, etc) that wants to be
     * accessed by some class code in a way that is independent of the
     * location of the code.  Resources are found with cooperation of the
     * class loaders, since they are the only ones who know where the class
     * actually came from. <p>
     *
     * System resources are those that are handled by the host implemenation
     * directly.  For example, they may be located in the CLASSPATH.<p>
     *
     * The name of a resource is a "/"-separated sequence of identifiers.
     * The class Class provides convenience methods for accessing resources;
     * the methods implement a convention where the package name is prefixed
     * to the short name of the resource.<p>
     * 
     * Resources can be accessed as an InputStream, or as a URL.
     *
     * @see	Class
     */

    /**
     * Get an InputStream on a given resource..  Will return null if no
     * resource with this name is found. <p>
     *
     * The resource name may be any system resource (e.g. follows CLASSPATH
     * order).
     *
     * @param	name	the name of the resource, to be used as is.
     * @return	an InputStream on the resource, or null if not found.
     * @since   JDK1.1
     */
    public static final InputStream getSystemResourceAsStream(String name) {
	// REMIND - This is equivalent to getSystemResource() call plus a openStream()
	return getSystemResourceAsStream0(name);
    }

    /**
     * Find a resource with a given name.  The return is a URL to the resource
     * Doing a getContent() on the URL may return an Image, an AudioClip, or
     * an InputStream.<p>
     *
     * The resource name may be any system resource (e.g. follows CLASSPATH
     * order).
     *
     * @param	name	the name of the resource, to be used as is.
     * @return	the URL on the resource, or null if not found.
     * @since   JDK1.1
     */
    public static final java.net.URL getSystemResource(String name) {
	String s = getSystemResourceAsName0(name);
	java.net.URL back;
	try {
	    back = new java.net.URL(s);
	} catch (Exception ex) {
	    back = null;
	}
	return back;
    }

    /**
     * Get an InputStream on a given resource.  Will return null if no
     * resource with this name is found. <p>
     *
     * The class loader can choose what to do to locate the resource.
     *
     * @param	name	the name of the resource, to be used as is.
     * @return	an InputStream on the resource, or null if not found.
     * @since   JDK1.1
     */
    public InputStream getResourceAsStream(String name) {
	return null;
    }

    /**
     * Find a resource with a given name.  The return is a URL to the resource.
     * Doing a getContent() on the URL may return an Image, an AudioClip,
     * or an InputStream.<p>
     *
     * The class loader can choose what to do to locate the resource.
     *
     * @param	name	the name of the resource, to be used as is.
     * @return	an InputStream on the resource, or null if not found.
     * @since   JDK1.1
     */
    public java.net.URL getResource(String name) {
	return null;
    }

    private static native InputStream getSystemResourceAsStream0(String name);
    private static native String getSystemResourceAsName0(String name);
}
