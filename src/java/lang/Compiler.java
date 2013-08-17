/*
 * @(#)Compiler.java	1.5 98/07/01
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

/**
 * The <code>Compiler</code> class is provided to support 
 * Java-to-native-code compilers and related services. By design, the 
 * <code>Compiler</code> class does nothing; it serves as a 
 * placeholder for a JIT compiler implementation. 
 * <p>
 * When the Java Virtual Machine first starts, it determines if the 
 * system property <code>java.compiler</code> exists. (System 
 * properties are accessible through <code>getProperty</code>  and , 
 * a method defined by the <code>System</code> class.) If so, it is 
 * assumed to be the name of a library (with a platform-dependent 
 * exact location and type); the <code>loadLibrary</code> method in 
 * class <code>System</code> is called to load that library. If this 
 * loading succeeds, the function named 
 * <code>java_lang_Compiler_start()</code> in that library is called. 
 * <p>
 * If no compiler is available, these methods do nothing. 
 *
 * @author  Frank Yellin
 * @version 1.5, 07/01/98
 * @see     java.lang.System#getProperty(java.lang.String)
 * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
 * @see     java.lang.System#loadLibrary(java.lang.String)
 * @since   JDK1.0
 */
public final class Compiler  {
    private Compiler() {}		// don't make instances
    
    private static native void initialize();

    static { 
	String library = null;
	try { 
	    library = System.getProperty("java.compiler");
	    if ((library != null) && (!library.equals("")) && 
		/* to enable turning off the jit using an env var
		   on win32, it is not possible to set an env var
		   to an empty string on win32. */
		(!library.equals("NONE"))) { 
		System.loadLibrary(library);
		initialize();
		}
	} catch (UnsatisfiedLinkError e) {
	    System.err.println("Warning: JIT compiler \"" + library +
			       "\" not found. Will use interpreter.");
	}
    }

    /**
     * Compiles the specified class. 
     *
     * @param   clazz   a class.
     * @return  <code>true</code> if the compilation succeeded;
     *          <code>false</code> if the compilation failed or no compiler
     *          is available.
     * @since   JDK1.0
     */
    public static native boolean compileClass(Class clazz);

    /**
     * Compiles all classes whose name matches the specified string. 
     *
     * @param   string   the name of the classes to compile.
     * @return  <code>true</code> if the compilation succeeded;
     *          <code>false</code> if the compilation failed or no compiler
     *          is available.
     * @since   JDK1.0
     */
    public static native boolean compileClasses(String string);

    /**
     * Examines the argument type and its fields and perform some documented
     * operation. No specific operations are required. 
     *
     * @param   any   an argument.
     * @return  a compiler-specific value, or <code>null</code> if no compiler
     *          is available.
     * @since   JDK1.0
     */
    public static native Object command(Object any);

    /**
     * Cause the Compiler to resume operation. 
     *
     * @since   JDK1.0
     */
    public static native void enable();

    /**
     * Cause the Compiler to cease operation. 
     *
     * @since   JDK1.0
     */
    public static native void disable();
}
