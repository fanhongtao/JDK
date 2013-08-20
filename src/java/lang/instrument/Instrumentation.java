/*
 * @(#)Instrumentation.java	1.7 04/06/08
 *
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package java.lang.instrument;

import  java.io.File;
import  java.io.IOException;

/*
 * Copyright 2003 Wily Technology, Inc.
 */

/**
 * This class provides services needed to instrument Java
 * programming language code.
 * Instrumentation is the addition of byte-codes to methods for the
 * purpose of gathering data to be utilized by tools. 
 * Since the changes are purely additive, these tools do not modify
 * application state or behavior.
 * Examples of such benign tools include monitoring agents, profilers,
 * coverage analyzers, and event loggers.
 *
 * <P>
 * The only way to access an instance of the <code>Instrumentation</code>
 * interface is for the JVM to be launched in a way that indicates
 * the agent class - see 
 * {@linkplain java.lang.instrument the package specification}.
 * The <code>Instrumentation</code> instance is passed
 * to the <code>premain</code> method of the agent class.
 * Once an agent acquires the <code>Instrumentation</code> instance,
 * the agent may call methods on the instance at any time.
 *
 * @since   JDK1.5
 */
public interface Instrumentation {
    /**
     * Registers the supplied transformer. All future class definitions
     * will be seen by the transformer, except definitions of classes upon which any
     * registered transformer is dependent. If multiple transformers are
     * registered, they will be called in the order added. If a transformer throws
     * during execution, the JVM will still call the other registered transformers in order.
     * The same transformer may be added more than once.
     * All transformers registered with <code>addTransformer</code>
     * will always see the class files before any external JVMTI ClassFileLoadHook event listener does.
     * <P>
     * This method is intended for use in instrumentation, as described in the
     * {@linkplain Instrumentation class specification}.
     *
     * @param transformer          the transformer to register
     * @throws java.lang.NullPointerException if passed a <code>null</code> transformer
     */
    void
    addTransformer(ClassFileTransformer transformer);

    /**
     * Unregisters the supplied transformer. Future class definitions will
     * not be shown to the transformer. Removes the most-recently-added matching
     * instance of the transformer. Due to the multi-threaded nature of
     * class loading, it is possible for a transformer to receive calls
     * after it has been removed. Transformers should be written defensively
     * to expect this situation. 
     *
     * @param transformer          the transformer to unregister
     * @return  true if the transformer was found and removed, false if the
     *           transformer was not found
     * @throws java.lang.NullPointerException if passed a <code>null</code> transformer
     */
    boolean
    removeTransformer(ClassFileTransformer transformer);

    /**
     * Returns whether or not the current JVM configuration supports redefinition of classes.
     * The ability to redefine an already loaded class is an optional capability
     * of a JVM.
     * During a single instantiation of a single JVM, multiple calls to this
     * method will always return the same answer.
     * @return  true if the current JVM configuration supports redefinition of classes,
     * false if not.
     * @see #redefineClasses
     */
    boolean
    isRedefineClassesSupported();

    /**
     * Redefine the supplied set of classes using the supplied class files. Operates on
     * a set in order to allow interlocked changes to more than one class at the same time
     * (a redefinition of class A can require a redefinition of class B).
     *
     * <P>
     * If a redefined method has active stack frames, those active frames continue to
     * run the bytecodes of the original method. 
     * The redefined method will be used on new invokes.
     *
     * <P>
     * This method does not cause any initialization except that which would occur
     * under the customary JVM semantics. In other words, redefining a class
     * does not cause its initializers to be run. The values of static variables
     * will remain as they were prior to the call.
     *
     * <P>
     * Instances of the redefined class are not affected.
     *
     * <P>
     * Registered transformers will be called before the redefine operation is applied.
     *
     * <P>
     * The redefinition may change method bodies, the constant pool and attributes.
     * The redefinition must not add, remove or rename fields or methods, change the 
     * signatures of methods, or change inheritance.  These restrictions maybe be
     * lifted in future versions.
     *
     * <P>
     * A zero-length <code>definitions</code> array is allowed, in this case, this
     * method does nothing.
     *
     * <P>
     * If this method throws an exception, no classes have been redefined.
     * <P>
     * This method is intended for use in instrumentation, as described in the
     * {@linkplain Instrumentation class specification}.
     *
     * @param definitions array of classes to redefine with corresponding definitions
     * @throws java.lang.ClassNotFoundException if a specified class cannot be found
     * @throws java.lang.instrument.UnmodifiableClassException if a specified class cannot be modified
     * @throws java.lang.UnsupportedOperationException if the current configuration of the JVM does not allow 
     * redefinition ({@link #isRedefineClassesSupported} is false) or the redefinition made unsupported changes
     * @throws java.lang.ClassFormatError if the data did not contain a valid class
     * @throws java.lang.NoClassDefFoundError if the name in the class file is not equal to the name of the class
     * @throws java.lang.UnsupportedClassVersionError if the class file version numbers are not supported
     * @throws java.lang.ClassCircularityError if the new classes contain a circularity
     * @throws java.lang.LinkageError if a linkage error occurs
     * @throws java.lang.NullPointerException if the supplied definitions array or any of its components is <code>null</code>.
     *
     * @see #isRedefineClassesSupported
     * @see #addTransformer
     * @see java.lang.instrument.ClassFileTransformer
     */
    void
    redefineClasses(ClassDefinition[] definitions)
        throws  ClassNotFoundException, UnmodifiableClassException;

    /**
     * Returns an array of all classes currently loaded by the JVM.
     *
     * @return an array containing all the classes loaded by the JVM, zero-length if there are none
     */
    Class[]
    getAllLoadedClasses();

    /**
     * Returns an array of all classes for which <code>loader</code> is an initiating loader.
     * If the supplied loader is <code>null</code>, classes initiated by the bootstrap class
     * loader are returned.
     *
     * @param loader          the loader whose initiated class list will be returned
     * @return an array containing all the classes for which loader is an initiating loader, 
     *          zero-length if there are none
     */
    Class[]
    getInitiatedClasses(ClassLoader loader);

    /**
     * Returns an implementation-specific approximation of the amount of storage consumed by
     * the specified object. The result may include some or all of the object's overhead,
     * and thus is useful for comparison within an implementation but not between implementations.
     *
     * The estimate may change during a single invocation of the JVM.
     *
     * @param objectToSize     the object to size
     * @return an implementation-specific approximation of the amount of storage consumed by the specified object
     * @throws java.lang.NullPointerException if the supplied Object is <code>null</code>.
     */
    long
    getObjectSize(Object objectToSize);
}
