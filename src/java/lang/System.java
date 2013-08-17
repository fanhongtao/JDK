/*
 * @(#)System.java	1.73 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.io.*;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The <code>System</code> class contains several useful class fields 
 * and methods. It cannot be instantiated. 
 * <p>
 * Among the facilities provided by the <code>System</code> class 
 * are standard input, standard output, and error output streams; 
 * access to externally defined "properties"; a means of 
 * loading files and libraries; and a utility method for quickly 
 * copying a portion of an array. 
 *
 * @author  Arthur van Hoff 
 * @version 1.73, 07/01/98
 * @since   JDK1.0
 */
public final
class System {
    /** Don't let anyone instantiate this class */
    private System() {
    }

    /**
     * The "standard" input stream. This stream is already 
     * open and ready to supply input data. Typically this stream 
     * corresponds to keyboard input or another input source specified by 
     * the host environment or user. 
     *
     * @since   JDK1.0
     */
    public final static InputStream in = nullInputStream();

    /**
     * The "standard" output stream. This stream is already 
     * open and ready to accept output data. Typically this stream 
     * corresponds to display output or another output destination 
     * specified by the host environment or user. 
     * <p>
     * For simple stand-alone Java applications, a typical way to write 
     * a line of output data is: 
     * <ul><code>System.out.println(data)</code></ul>
     * <p>
     * See the <code>println</code> methods in class <code>PrintStream</code>. 
     *
     * @see     java.io.PrintStream#println()
     * @see     java.io.PrintStream#println(boolean)
     * @see     java.io.PrintStream#println(char)
     * @see     java.io.PrintStream#println(char[])
     * @see     java.io.PrintStream#println(double)
     * @see     java.io.PrintStream#println(float)
     * @see     java.io.PrintStream#println(int)
     * @see     java.io.PrintStream#println(long)
     * @see     java.io.PrintStream#println(java.lang.Object)
     * @see     java.io.PrintStream#println(java.lang.String)
     * @since   JDK1.0
     */
    public final static PrintStream out = nullPrintStream();

    /**
     * The "standard" error output stream. This stream is already 
     * open and ready to accept output data. 
     * <p>
     * Typically this stream corresponds to display output or another 
     * output destination specified by the host environment or user. By 
     * convention, this output stream is used to display error messages 
     * or other information that should come to the immediate attention 
     * of a user even if the principal output stream, the value of the 
     * variable <code>out</code>, has been redirected to a file or other 
     * destination that is typically not continuously monitored. 
     *
     * @since   JDK1.0
     */
    public final static PrintStream err = nullPrintStream();

    /* The security manager for the system.
     */
    private static SecurityManager security = null;

    /**
     * Reassigns the "standard" input stream.
     *
     * @since   JDK1.1
     */
    public static void setIn(InputStream in) {
	checkIO();
	setIn0(in);
    }

    /**
     * Reassigns the "standard" output stream.
     *
     * @since   JDK1.1
     */
    public static void setOut(PrintStream out) {
	checkIO();
	setOut0(out);
    }

    /**
     * Reassigns the "standard" error output stream.
     *
     * @since   JDK1.1
     */
    public static void setErr(PrintStream err) {
	checkIO();
	setErr0(err);
    }

    private static void checkIO() {
	if (security != null) {
	    /* REMIND: this should have its own security check call */
	    security.checkExec("setio");
	}
    }

    private static native void setIn0(InputStream in);
    private static native void setOut0(PrintStream out);
    private static native void setErr0(PrintStream err);

    /**
     * Sets the System security.
     * If a security manager has already been established for the 
     * currently running Java application, a <code>SecurityException</code> 
     * is thrown. Otherwise, the argument is established as the current 
     * security manager. If the argument is <code>null</code> and no 
     * security manager has been established, then no action is taken and 
     * the method simply returns. 
     *
     * @param      s   the security manager.
     * @exception  SecurityException  if the security manager has already
     *               been set.
     * @since   JDK1.0
     */
    public static void setSecurityManager(SecurityManager s) {
	if (security != null) {
	    throw new SecurityException("SecurityManager already set");
	}
	security = s;
    }

    /**
     * Gets the system security interface.
     *
     * @return  if a security manager has already been established for the
     *          current application, then that security manager is returned;
     *          otherwise, <code>null</code> is returned.
     * @since   JDK1.0
     */
    public static SecurityManager getSecurityManager() {
	return security;
    }

    /**
     * Returns the current time in milliseconds.
     * <p>
     * See the description of the class <code>Date</code> for a discussion 
     * of slight discrepancies that may arise between "computer 
     * time" and coordinated universal time (UTC). 
     *
     * @return  the difference, measured in milliseconds, between the current
     *          time and midnight, January 1, 1970 UTC.
     * @see     java.util.Date
     * @since   JDK1.0
     */
    public static native long currentTimeMillis();

    /** 
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * A subsequence of array components are copied from the source 
     * array referenced by <code>src</code> to the destination array 
     * referenced by <code>dst</code>. The number of components copied is 
     * equal to the <code>length</code> argument. The components at 
     * positions <code>srcOffset</code> through 
     * <code>srcOffset+length-1</code> in the source array are copied into 
     * positions <code>dstOffset</code> through 
     * <code>dstOffset+length-1</code>, respectively, of the destination 
     * array. 
     * <p>
     * If the <code>src</code> and <code>dst</code> arguments refer to the 
     * same array object, then the copying is performed as if the 
     * components at positions <code>srcOffset</code> through 
     * <code>srcOffset+length-1</code> were first copied to a temporary 
     * array with <code>length</code> components and then the contents of 
     * the temporary array were copied into positions 
     * <code>dstOffset</code> through <code>dstOffset+length-1</code> of the 
     * argument array. 
     * <p>
     * If any of the following is true, an 
     * <code>ArrayStoreException</code> is thrown and the destination is 
     * not modified: 
     * <ul>
     * <li>The <code>src</code> argument refers to an object that is not an 
     *     array. 
     * <li>The <code>dst</code> argument refers to an object that is not an 
     *     array. 
     * <li>The <code>src</code> argument and <code>dst</code> argument refer to 
     *     arrays whose component types are different primitive types. 
     * <li>The <code>src</code> argument refers to an array with a primitive 
     *     component type and the <code>dst</code> argument refers to an array 
     *     with a reference component type. 
     * <li>The <code>src</code> argument refers to an array with a reference 
     *     component type and the <code>dst</code> argument refers to an array 
     *     with a primitive component type. 
     * </ul>
     * <p>
     * Otherwise, if any of the following is true, an 
     * <code>ArrayIndexOutOfBoundsException</code> is 
     * thrown and the destination is not modified: 
     * <ul>
     * <li>The <code>srcOffset</code> argument is negative. 
     * <li>The <code>dstOffset</code> argument is negative. 
     * <li>The <code>length</code> argument is negative. 
     * <li><code>srcOffset+length</code> is greater than 
     *     <code>src.length</code>, the length of the source array. 
     * <li><code>dstOffset+length</code> is greater than 
     *     <code>dst.length</code>, the length of the destination array. 
     * </ul>
     * <p>
     * Otherwise, if any actual component of the source array from 
     * position <code>srcOffset</code> through 
     * <code>srcOffset+length-1</code> cannot be converted to the component 
     * type of the destination array by assignment conversion, an 
     * <code>ArrayStoreException</code> is thrown. In this case, let 
     * <b><i>k</i></b> be the smallest nonnegative integer less than 
     * length such that <code>src[srcOffset+</code><i>k</i><code>]</code> 
     * cannot be converted to the component type of the destination 
     * array; when the exception is thrown, source array components from 
     * positions <code>srcOffset</code> through
     * <code>srcOffset+</code><i>k</i><code>-1</code> 
     * will already have been copied to destination array positions 
     * <code>dstOffset</code> through
     * <code>dstOffset+</code><i>k</I><code>-1</code> and no other 
     * positions of the destination array will have been modified. 
     *
     * @param      src:      the source array.
     * @param      srcpos    start position in the source array.
     * @param      dest      the destination array.
     * @param      destpos   start position in the destination data.
     * @param      length    the number of array elements to be copied.
     * @exception  ArrayIndexOutOfBoundsException  if copying would cause
     *               access of data outside array bounds.
     * @exception  ArrayStoreException  if an element in the <code>src</code>
     *               array could not be stored into the <code>dest</code> array
     *               because of a type mismatch.
     * @since      JDK1.0
     */
    public static native void arraycopy(Object src, int src_position,
                                        Object dst, int dst_position,
                                        int length);

    /**
     * Returns the same hashcode for the given object as
     * would be returned by the default method hashCode(),
     * whether or not the given object's class overrides
     * hashCode().
     * The hashcode for the null reference is zero.
     *
     * @since   JDK1.1
     */
    public static native int identityHashCode(Object x);

    /**
     * System properties. The following properties are guaranteed to be defined:
     * <dl>
     * <dt>java.version		<dd>Java version number
     * <dt>java.vendor		<dd>Java vendor specific string
     * <dt>java.vendor.url	<dd>Java vendor URL
     * <dt>java.home		<dd>Java installation directory
     * <dt>java.class.version	<dd>Java class version number
     * <dt>java.class.path	<dd>Java classpath
     * <dt>os.name		<dd>Operating System Name
     * <dt>os.arch		<dd>Operating System Architecture
     * <dt>os.version		<dd>Operating System Version
     * <dt>file.separator	<dd>File separator ("/" on Unix)
     * <dt>path.separator	<dd>Path separator (":" on Unix)
     * <dt>line.separator	<dd>Line separator ("\n" on Unix)
     * <dt>user.name		<dd>User account name
     * <dt>user.home		<dd>User home directory
     * <dt>user.dir		<dd>User's current working directory
     * </dl>
     */

    private static Properties props;
    private static native Properties initProperties(Properties props);

    /**
     * Determines the current system properties. 
     * <p>
     * If there is a security manager, its 
     * <code>checkPropertiesAccess</code> method is called with no 
     * arguments. This may result in a security exception. 
     * <p>
     * The current set of system properties is returned as a 
     * <code>Properties</code> object. If there is no current set of 
     * system properties, a set of system properties is first created and 
     * initialized. 
     * <p>
     * This set of system properties always includes values for the 
     * following keys: 
     * <table>
     * <tr><th>Key</th>
     *     <th>Description of Associated Value</th></tr>
     * <tr><td><code>java.version</code></td>
     *     <td>Java version number</td></tr>
     * <tr><td><code>java.vendor</code></td>
     *     <td>Java vendor-specific string</td></tr>
     * <tr><td><code>java.vendor.url</code></td>
     *     <td>Java vendor URL</td></tr>
     * <tr><td><code>java.home</code></td>
     *     <td>Java installation directory</td></tr>
     * <tr><td><code>java.class.version</code></td>
     *     <td>Java class format version number</td></tr>
     * <tr><td><code>java.class.path</code></td>
     *     <td>Java class path</td></tr>
     * <tr><td><code>os.name</code></td>
     *     <td>Operating system name</td></tr>
     * <tr><td><code>os.arch</code></td>
     *     <td>Operating system architecture</td></tr>
     * <tr><td><code>os.version</code></td>
     *     <td>Operating system version</td></tr>
     * <tr><td><code>file.separator</code></td>
     *     <td>File separator ("/" on UNIX)</td></tr>
     * <tr><td><code>path.separator</code></td>
     *     <td>Path separator (":" on UNIX)</td></tr>
     * <tr><td><code>line.separator</code></td>
     *     <td>Line separator ("\n" on UNIX)</td></tr>
     * <tr><td><code>user.name</code></td>
     *     <td>User's account name</td></tr>
     * <tr><td><code>user.home</code></td>
     *     <td>User's home directory</td></tr>
     * <tr><td><code>user.dir</code></td>
     *     <td>User's current working directory</td></tr>
     * </table>
     *
     * @exception  SecurityException  if the current thread cannot access the
     *               system properties.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @see        java.util.Properties
     * @since      JDK1.0
     */
    public static Properties getProperties() {
	if (security != null) {
	    security.checkPropertiesAccess();
	}
	return props;
    }

    /**
     * Sets the system properties to the <code>Properties</code> 
     * argument. 
     * <p>
     * First, if there is a security manager, its 
     * <code>checkPropertiesAccess</code> method is called with no 
     * arguments. This may result in a security exception. 
     * <p>
     * The argument becomes the current set of system properties for use 
     * by the <code>getProperty</code> method. If the argument is 
     * <code>null</code>, then the current set of system properties is 
     * forgotten. 
     *
     * @param      props   the new system properties.
     * @exception  SecurityException  if the current thread cannot set the
     *               system properties.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @since      JDK1.0
     */
    public static void setProperties(Properties props) {
	if (security != null) {
	    security.checkPropertiesAccess();
	}
	System.props = props;
    }
    
    /**
     * Gets the system property indicated by the specified key. 
     * <p>
     * First, if there is a security manager, its 
     * <code>checkPropertyAccess</code> method is called with the key as 
     * its argument. This may result in a system exception. 
     * <p>
     * If there is no current set of system properties, a set of system 
     * properties is first created and initialized in the same manner as 
     * for the <code>getProperties</code> method. 
     *
     * @param      key   the name of the system property.
     * @return     the string value of the system property,
     *             or <code>null</code> if there is no property with that key.
     * @exception  SecurityException  if the current thread cannot access the
     *               system properties or the specified property.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     * @since      JDK1.0
     */
    public static String getProperty(String key) {
	if (security != null) {
	    security.checkPropertyAccess(key);
	}
	return props.getProperty(key);
    }
    
    /**
     * Gets the system property indicated by the specified key. 
     * <p>
     * First, if there is a security manager, its 
     * <code>checkPropertyAccess</code> method is called with the 
     * <code>key</code> as its argument. 
     * <p>
     * If there is no current set of system properties, a set of system 
     * properties is first created and initialized in the same manner as 
     * for the <code>getProperties</code> method. 
     *
     * @param      key   the name of the system property.
     * @param      def   a default value.
     * @return     the string value of the system property,
     *             or the default value if there is no property with that key.
     * @exception  SecurityException  if the current thread cannot access the
     *               system properties or the specified property.
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     * @since      JDK1.0
     */
    public static String getProperty(String key, String def) {
	if (security != null) {
	    security.checkPropertyAccess(key); 
	}
	return props.getProperty(key, def);
    }
    
    /**
     * Gets an environment variable. An environment variable is a
     * system dependent external variable that has a string value.
     * 
     * @param   the name of the environment variable.
     * @return 	the value of the variable, or null if the variable is
     *		not defined.
     * @since   JDK1.0
     * @deprecated
     */
    public static String getenv(String name) {
	throw new Error("getenv no longer supported, use properties and -D instead: " + name);
    }

    /**
     * Terminates the currently running Java Virtual Machine. The 
     * argument serves as a status code; by convention, a nonzero status 
     * code indicates abnormal termination. 
     * <p>
     * This method calls the <code>exit</code> method in class 
     * <code>Runtime</code>. This method never returns normally. 
     *
     * @param      status   exit status.
     * @exception  SecurityException  if the current thread cannot exit with
     *               the specified status.
     * @see        java.lang.Runtime#exit(int)
     * @since      JDK1.0
     */
    public static void exit(int status) {
	Runtime.getRuntime().exit(status);
    }

    /**
     * Runs the garbage collector.
     * <p>
     * Calling the <code>gc</code> method suggests that the Java Virtual 
     * Machine expend effort toward recycling unused objects in order to 
     * make the memory they currently occupy available for quick reuse. 
     * When control returns from the method call, the Java Virtual 
     * Machine has made a best effort to reclaim space from all unused 
     * objects.
     *
     * @see     java.lang.Runtime#gc()
     * @since   JDK1.0
     */
    public static void gc() {
	Runtime.getRuntime().gc();
    }

    /**
     * Runs the finalization methods of any objects pending finalization.
     * <p>
     * Calling this method suggests that the Java Virtual Machine expend 
     * effort toward running the <code>finalize</code> methods of objects 
     * that have been found to be discarded but whose <code>finalize</code> 
     * methods have not yet been run. When control returns from the 
     * method call, the Java Virtual Machine has made a best effort to 
     * complete all outstanding finalizations. 
     *
     * @see     java.lang.Runtime#runFinalization()
     * @since   JDK1.0
     */
    public static void runFinalization() {
	Runtime.getRuntime().runFinalization();
    }

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.
     * @see     java.lang.Runtime#exit(int)
     * @see     java.lang.Runtime#gc()
     * @since   JDK1.1
     */
    public static void runFinalizersOnExit(boolean value) {
	Runtime.getRuntime().runFinalizersOnExit(value);
    }

    /**
     * Loads the specified filename as a dynamic library. The filename 
     * argument must be a complete pathname. 
     * <p>
     * This method calls the <code>load</code> method in class 
     * <code>Runtime. </code> 
     *
     * @param      filename   the file to load.
     * @exception  SecurityException  if the current thread cannot load the
     *               specified dynamic library.
     * @exception  UnsatisfiedLinkError  if the file does not exist.
     * @see        java.lang.Runtime#load(java.lang.String)
     * @since      JDK1.0
     */
    public static void load(String filename) {
	Runtime.getRuntime().load(filename);
    }

    /**
     * Loads the system library specified by the <code>libname</code> 
     * argument. The manner in which a library name is mapped to the 
     * actual system library is system dependent. 
     *
     * @param      libname   the name of the library.
     * @exception  SecurityException  if the current thread cannot load the
     *               specified dynamic library.
     * @exception  UnsatisfiedLinkError  if the library does not exist.
     * @see        java.lang.Runtime#loadLibrary(java.lang.String)
     * @since      JDK1.0
     */
    public static void loadLibrary(String libname) {
	Runtime.getRuntime().loadLibrary(libname);
    }

    /**
     * The following two methods exist because in, out, and err must be
     * initialized to null.  The compiler, however, cannot be permitted to
     * inline access to them, since they are later set to more sensible values
     * by initializeSystemClass().
     */
    private static InputStream nullInputStream() throws NullPointerException {
	if (currentTimeMillis() > 0)
	    return null;
	throw new NullPointerException();
    }

    private static PrintStream nullPrintStream() throws NullPointerException {
	if (currentTimeMillis() > 0)
	    return null;
	throw new NullPointerException();
    }

    /**
     * Initialize the system class.  Called after thread initialization.
     */
    private static void initializeSystemClass() {
	props = new Properties();
	initProperties(props);
	FileInputStream fdIn = new FileInputStream(FileDescriptor.in);
	FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
	FileOutputStream fdErr = new FileOutputStream(FileDescriptor.err);
	setIn0(new BufferedInputStream(fdIn));
	setOut0(new PrintStream(new BufferedOutputStream(fdOut, 128), true));
	setErr0(new PrintStream(new BufferedOutputStream(fdErr, 128), true));
    }

}
