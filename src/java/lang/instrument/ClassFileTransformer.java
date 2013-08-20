/*
 * @(#)ClassFileTransformer.java	1.5 04/05/05
 *
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package java.lang.instrument;


import  java.security.ProtectionDomain;

/*
 * Copyright 2003 Wily Technology, Inc.
 */

/**
 * An agent provides an implementation of this interface in order
 * to transform class files.  
 * The transformation occurs before the class is defined by the JVM.
 * <P>
 * Note the term <i>class file</i> is used as defined in the chapter
 * <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#80959">The class File Format</a>
 * of <i>The Java Virtual Machine Specification</i>, to mean a sequence
 * of bytes in class file format, whether or not they reside in a file.
 *
 * @see     java.lang.instrument.Instrumentation
 * @see     java.lang.instrument.Instrumentation#addTransformer
 * @see     java.lang.instrument.Instrumentation#removeTransformer
 * @since   JDK1.5
 */

public interface ClassFileTransformer { 
    /**
     * The implementation of this method may transform the supplied class file and 
     * return a new replacement class file.
     *
     * <P>
     * Once a transformer has been registered with
     * {@link java.lang.instrument.Instrumentation#addTransformer Instrumentation.addTransformer},
     * the transformer will be called for every new class definition and every class redefinition.
     * The request for a new class definition is made with
     * {@link java.lang.ClassLoader#defineClass ClassLoader.defineClass}.
     * The request for a class redefinition is made with
     * {@link java.lang.instrument.Instrumentation#redefineClasses Instrumentation.redefineClasses}
     * or its native equivalents.
     * The transformer is called during the processing of the request, before the class file bytes
     * have been verified or applied.
     *
     * <P>
     * If the implementing method determines that no transformations are needed,
     * it should return <code>null</code>. 
     * Otherwise, it should create a new <code>byte[]</code> array,
     * copy the input <code>classfileBuffer</code> into it,
     * along with all desired transformations, and return the new array. 
     * The input <code>classfileBuffer</code> must not be modified.
     *
     * <P>
     * In the redefine case, the transformer must support the redefinition semantics.
     * If a class that the transformer changed during initial definition is later redefined, the
     * transformer must insure that the second class output class file is a legal
     * redefinition of the first output class file.
     *
     * <P>
     * If the transformer believes the <code>classFileBuffer</code> does not
     * represent a validly formatted class file, it should throw
     * an <code>IllegalClassFormatException</code>.  Subsequent transformers
     * will still be called and the load or redefine will still
     * be attempted.  Throwing an <code>IllegalClassFormatException</code> thus
     * has the same effect as returning null but facilitates the
     * logging or debugging of format corruptions.
     *
     * @param loader                the defining loader of the class to be transformed,
     *                              may be <code>null</code> if the bootstrap loader
     * @param className             the name of the class in the internal form of fully
     *                              qualified class and interface names as defined in
     *                              <i>The Java Virtual Machine Specification</i>.  
     *                              For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined   if this is a redefine, the class being redefined, 
     *                              otherwise <code>null</code>
     * @param protectionDomain      the protection domain of the class being defined or redefined
     * @param classfileBuffer       the input byte buffer in class file format - must not be modified
     *
     * @throws IllegalClassFormatException if the input does not represent a well-formed class file
     * @return  a well-formed class file buffer (the result of the transform), 
                or <code>null</code> if no transform is performed.
     * @see Instrumentation#redefineClasses
     */
    byte[]
    transform(  ClassLoader         loader,
                String              className,
                Class<?>            classBeingRedefined,
                ProtectionDomain    protectionDomain,
                byte[]              classfileBuffer)
        throws IllegalClassFormatException;
} 
