/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils.synthetic;

import org.apache.xml.utils.synthetic.SynthesisException;
import org.apache.xml.utils.synthetic.reflection.Constructor;
import org.apache.xml.utils.synthetic.reflection.Method;
import org.apache.xml.utils.synthetic.reflection.Field;

import java.lang.reflect.Modifier;

/* WORK NEEDED:
    Factories/Libraries: We currently have forClass and
    forName(request reified, complain if no real class),
    and declareClass (request unreified, create unreified
    if it doesn't exist). What about ther user expectations
    -- should we have a full matrix, rather than asking
    users to write wrappers?

    Reflection doesn't tell us about deprecation. If we want
    that info, MFC advises mousing our way into the bytecodes.
    (Ugh). Should we at least model that for synthetics?
*/

/**
 * <meta name="usage" content="internal"/>
 * org.apache.xml.utils.synthetic.Class is a mutable equivalent of java.lang.Class.
 * Instances represent classes and interfaces in a running Java
 * application, or class descriptions under construction. In the
 * former case, org.apache.xml.utils.synthetic.Class operates as a proxy for the
 * "real" java.lang.Class object; in the latter, it consults
 * data structures defined in the org.apache.xml.utils.synthetic.reflection.* package.
 * <p>
 * Unlike java.lang.Class, org.apache.xml.utils.synthetic.Class has a pair of factories
 * (fromName and fromClass). It can also be switched from synthetic
 * to proxy operation after construction, by setting the realClass
 * property; this is intended to allow these definitions to be
 * "compiled in place".
 * <p>
 * For convenient use, org.apache.xml.utils.synthetic.Class implements an extended
 * version of the java.lang.Class API -- but is not a subclass
 * thereof, since java.lang.Class is Final (presumably for
 * security reasons).
 * <p>
 * DEVELOPMENT NOTE: Methods not yet implemented will throw
 * IllegalStateException
 * <p>
 *   I've added code to convert primitive names into their TYPEs,
 *   to accept foo[] as a synonym for [Lfoo, and to generate
 *   the right thing on output (getJava[Short]Name).
 *   Useful extension for code generation from Java-like
 *   source. We may want to factor these and toSource out, making
 *   org.apache.xml.utils.synthetic.Class addess only the JVM level and providing
 *   subclasses or access tools that handle language syntax
 *   (Java source, NetRexx source, etc.)
 *
 * @since  2000/2/10
 */
public class Class extends Object implements java.io.Serializable
{

  /** Class descriptions currently existing. */
  private static java.util.Hashtable global_classtable =
    new java.util.Hashtable();

  /** fully-qualified path.classname.
   *  @serial */
  private java.lang.String name;

  /**
   * Actual Java class object. When present, all interactions
   * are redirected to it. Allows our Class to function as a
   * wrapper for the Java version (in lieu of subclassing or
   * a shared Interface), and allows "in-place compilation"
   * to replace a generated description with an
   * directly runnable class.
   * @serial
   */
  private java.lang.Class realclass = null;

  /** Field modifiers: Java language modifiers for this class 
   * or interface, encoded in an integer.
   * @serial
   */
  private int modifiers;

  /** Field isInterface: True if the Class object represents
   *  an interface type.
   * @serial */
  private boolean isInterface = false;

  /** Field superclass:  If this object represents the class
   * Object, this is null. Otherwise, the Class object that 
   * represents the superclass of that class. In proxy mode this
   * is determined when needed. In synthesis mode it's explicitly
   * set by the user, and if null the superclass will be assumed
   * to be Object.
   * @serial */
  private Class superclass = null;

  /** Field declaringclass: If this object represents an inner class,
   * the Class object that represents the class that declared it.
   * Otherwise null.
   * @serial
   * */
  private Class declaringclass = null;

  /** Field interfaces: A list of all interfaces implemented by the class 
   * or interface represented by this object.
   * @serial
   *  */
  private Class[] interfaces = new Class[0];

  /** Field allclasses:  an array containing Class objects representing all 
   * the public classes and interfaces that are members of the class 
   * represented by this Class object. 
   * @serial
   */
  private Class[] allclasses = new Class[0];

  /** Field declaredclasses: an array of Class objects reflecting all the 
   * classes and interfaces declared as members of the class represented 
   * by this Class object. Excludes inherited classes and interfaces.
   * @serial
   */
  private Class[] declaredclasses = new Class[0];

  /** Field allconstructors: an array containing Constructor objects
   * reflecting all the constructors of the class represented 
   * by this Class object. An array of length 0 is returned if the
   * class has no public constructors. In proxy mode only public
   * constructors will be displayed; in synthesis mode, all declared
   * constructors will be displayed.
   * @serial
   * */
  private Constructor[] allconstructors = new Constructor[0];

  /** Field declaredconstructors: an array of Constructor objects 
   * reflecting all the constructors declared by the class 
   * represented by this Class object. Includes non-public
   * constructors, but excludes inherited ones.
   * @serial
   *  */
  private Constructor[] declaredconstructors = new Constructor[0];

  /** Field allmethods.
   *  @serial          */
  private Method[] allmethods = new Method[0];

  /** Field declaredmethods.
   *  @serial          */
  private Method[] declaredmethods = new Method[0];

  /** Field allfields.
   *  @serial          */
  private Field[] allfields = new Field[0];

  /** Field declaredfields.
   *  @serial          */
  private Field[] declaredfields = new Field[0];

  /** Field innerclasses.
   *  @serial          */
  private Class[] innerclasses = new Class[0];

  /**
   * Construct a synthetic class as proxy/wrapper for an existing
   * Java Class. Non-public; most folks should use
   * .forName and .forClass to request these wrappers, so they
   * get the shared instances.
   * <p>
   * Creation date: (12-25-99 12:16:15 PM)
   * @param realclass java.lang.Class
   */
  Class(java.lang.Class realclass)
  {

    this(realclass.getName());

    try
    {
      setRealClass(realclass);
    }
    catch (SynthesisException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Construct a named-but-empty synthetic Class object.
   * Non-public; most folks should use
   * .forName and .forClass to request these wrappers, so they
   * get the shared instances.
   * <p>
   * Creation date: (12-25-99 12:15:23 PM)
   *
   * @param fullname full name of the class that is synthetized.
   */
  Class(String fullname)
  {

    this.name = fullname;

    global_classtable.put(fullname, this);
  }

  /**
   * Returns the synthetic Class object associated with the "real"
   * class specified, creating one if it didn't already exist.
   * <p>
   * For example, the following code fragment returns
   * the runtime Class descriptor for the class named
   * mypackage.MyClass.
   * <code>
   * Class t =
   * Class.forName(java.lang.Class.forName("mypackage.MyClass"))
   * </code>
   * <p>
   * Note that if the user has manually created a org.apache.xml.utils.synthetic.Class
   * with the same name before this call is issued, that object
   * will be found instead. See also the declareClass call.
   * <p>
   *  We need a better way to declare/define array classes,
   * given a class object (synthetic or not).
   *
   * @param cls the desired Java class.
   * @return the synthetic Class descriptor for the specified class.
   */
  public static Class forClass(java.lang.Class cls)
  {

    if (cls == null)
      return null;

    Class ret = (Class) (global_classtable.get(cls.getName()));

    if (null == ret)
      ret = new Class(cls);

    return ret;
  }

  /**
   * Like forName, but if the classname doesn't have a package
   * prefix we first attempt to look it up as one of our own
   * inner clases. As with forName, if this can not be resolved
   * we throw an exception.
   *
   * @param classname the full or partial class name.
   *
   * @return The Class name that matches the argument.
   *
   * @throws ClassNotFoundException
   */
  public Class forNameInContext(String classname)
          throws ClassNotFoundException
  {

    for (int i = innerclasses.length - 1; i >= 0; --i)
    {
      if (classname.equals(innerclasses[i].getShortName()))
        return innerclasses[i];
    }

    return forName(classname);
  }

  /**
   * Returns the synthetic Class object associated with the class
   * with the given fully-qualified name. If there isn't one, this
   * method attempts to locate, load and link the standard java Class.
   * If it succeeds, it returns a wrapped version of the Class object
   * representing the class. If it fails, the method throws a
   * ClassNotFoundException.
   * <p>
   * For example, the following code fragment returns
   * the runtime Class descriptor for the class named
   * mypackage.MyClass -- either as a synthetic or as
   * a standard Java class.
   * <code>
   * Class t =
   * Class.forName("mypackage.MyClass")
   * </code>
   * <p>
   *  I've added support for arrays -- assuming any name
   * that ends with ']' is an array. It probably needs to be
   * made smarter, possibly via a subclass of org.apache.xml.utils.synthetic.Class.
   *
   * @param className the fully qualified name of the desired class.
   * @return the synthetic Class descriptor for the class with the specified name.
   * @throws ClassNotFoundException if the class could not be found.
   */
  public static Class forName(String className) throws ClassNotFoundException
  {

    // ***** Experimental support for array syntax expressed
    // per Java source rather than per JVM type formalism.
    // Simpleminded, asssumes balanced []'s.
    if (className.endsWith("]"))
    {
      StringBuffer arrayname = new StringBuffer();

      for (int i = className.indexOf('['); i != -1;
              i = className.indexOf('[', i + 1))
      {
        arrayname.append('[');
      }

      // Convert the classname to array-formalism
      // Primitives have letters; objects are Lname;
      // (Don't ask why long is spelled with a J and
      // object is spelled with an L...)
      String classname = className.substring(0, className.indexOf('['));

      if ("byte".equals(classname))
        arrayname.append('B');
      else if ("char".equals(classname))
        arrayname.append('C');
      else if ("double".equals(classname))
        arrayname.append('D');
      else if ("float".equals(classname))
        arrayname.append('F');
      else if ("int".equals(classname))
        arrayname.append('I');
      else if ("long".equals(classname))
        arrayname.append('J');
      else if ("short".equals(classname))
        arrayname.append('S');
      else if ("boolean".equals(classname))
        arrayname.append('Z');
      else
        arrayname.append('L').append(classname).append(';');

      // Tail-call.
      return forName(arrayname.toString());
    }

    Class ret = (Class) (global_classtable.get(className));

    if (null == ret)
    {

      // ***** Experimental support for Java primitives
      // Seems to me that mapping them into the "Type" is
      // probably most useful
      if ("boolean".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Boolean.TYPE;
      }
      else if ("byte".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Byte.TYPE;
      }
      else if ("char".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Character.TYPE;
      }
      else if ("short".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Short.TYPE;
      }
      else if ("int".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Integer.TYPE;
      }
      else if ("long".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Long.TYPE;
      }
      else if ("float".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Float.TYPE;
      }
      else if ("double".equals(className))
      {
        ret = new Class(className);
        ret.realclass = java.lang.Double.TYPE;
      }
      else if ("void".equals(className))
      {

        // ***** Void is an "absence of type". We might want to create
        // a special object to represent it. This is a placeholder.
        ret = new Class(className);
        ret.realclass = java.lang.Class.forName("java.lang.Object");
      }

      // Other classes are just wrappered. Unknown classes throw a
      // ClassNotFoundException; the user can switch to declareClass()
      // if they're sure that an unreified class is OK. 
      else
        ret = new Class(java.lang.Class.forName(className));
    }

    return ret;
  }

  /**
   * Start to create a synthetic Class with the given fully-qualified
   * name.  If a Class by that name already exists, and it is not
   * reified, it will be returned instead. If a reified Class _does_
   * exist, we throw a synthesis exception.
   *
   * @param className the fully qualified name of the desired class.
   * @return the synthetic Class descriptor for the class with the specified name.
   * @throws SynthesisException if the class has been reified.  
   */
  public static Class declareClass(String className) throws SynthesisException
  {

    Class ret = (Class) (global_classtable.get(className));

    if (null == ret)
      ret = new Class(className);

    if (ret.realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    return ret;
  }

  /**
   * Start to create a synthetic Class with the given fully-qualified
   * name.  If a Class by that name already exists,whether reified or
   * not, it will be removed from the table and replaced by the new synthesis.
   *
   *  NOTE THAT the replacement will not affect classes which
   * have already refernced the old version. We could change that by
   * having everyone reference everyone else via an indirection table.
   *
   * @param className the fully qualified name of the desired class.
   * @return the synthetic Class descriptor for the class with the specified name.
   */
  public static Class reallyDeclareClass(String className)
  {

    Class ret = (Class) (global_classtable.get(className));

    if (null != ret)
      global_classtable.remove(ret);

    ret = new Class(className);

    return ret;
  }

  /**
   * Returns an array containing Class objects
   * representing all the public classes and interfaces
   * that are members of the class represented by this
   * Class object. This includes public class and
   * interface members inherited from superclasses and
   * public class and interface members declared by the
   * class. Returns an array of length 0 if the class has
   * no public member classes or interfaces, or if this
   * Class object represents a primitive type.
   * <p>
   * NOTE: In a significant number of existing Java environments,
   * this method is not implemented by the official Class object
   * and always returns an empty array. So if you don't get any
   * useful information from a proxied java.lang.Class, don't
   * be surprised. I'm not sure if someone decided it was a
   * potential security issue, or if Sun was lazy and everyone
   * else followed suit.
   * <p>
   * ALSO NOTE: The above spec, as taken from java.lang.Class,
   * doesn't provide any good way to distinguish the immediate
   * superclass from all other superclasses. That makes it only
   * marginally useful, which is no doubt one of the reasons folks
   * have declined to implement it.
   *
   * @return an array of classes.
   */
  public Class[] getClasses()
  {

    if (realclass != null && allclasses == null)
    {
      java.lang.Class[] realDE = realclass.getClasses();

      allclasses = new Class[realDE.length];

      for (int i = 0; i < realDE.length; ++i)
      {
        allclasses[i] = forClass(realDE[i]);
      }
    }

    return allclasses;
  }

  /**
   * Determines the class loader for the class.
   *
   * the class loader that created the class or
   * interface represented by this object, or null
   * if the org.apache.xml.utils.synthetic.Class was not created by a class loader.
   */
  public ClassLoader getClassLoader()
  {
    return (realclass == null) ? null : realclass.getClassLoader();
  }

  /**
   * If this class represents an array type, returns the
   * Class object representing the component type of
   * the array; otherwise returns null.
   * <p>
   * NOTE: Since org.apache.xml.utils.synthetic.Class doesn't yet attempt to model array
   * types, this will currently return false unless we are
   * proxying such a type.
   *
   * @return the Class object representing the component type of
   * the array, otherwise returns null.
   */
  public Class getComponentType()
  {
    return realclass == null ? null : new Class(realclass.getComponentType());
  }

  /**
   * Returns a Constructor object that reflects the
   * specified public constructor of the class
   * represented by this Class object. The
   * parameterTypes parameter is an array of Class
   * objects that identify the constructor's formal
   * parameter types, in declared order.
   * <p>
   * The constructor to reflect is located by searching
   * all the constructors of the class represented by this
   * Class object for a public constructor with the
   * exactly the same formal parameter types.
   *
   *
   * @param parameterTypes array of Class
   * objects that identify the constructor's formal
   * parameter types, in declared order.
   *
   * @return a Constructor object that reflects the
   * specified public constructor of the class
   * represented by this Class object.
   * 
   * @throws NoSuchMethodException
   * if a matching method is not found.
   * @throws SecurityException
   * if access to the information is denied.
   * @throws SynthesisException
   */
  public Constructor getConstructor(Class parameterTypes[])
          throws NoSuchMethodException, SecurityException, SynthesisException
  {

    if (realclass == null)
      throw new SynthesisException(SynthesisException.UNREIFIED);

    java.lang.Class[] real = new java.lang.Class[parameterTypes.length];

    for (int i = 0; i < parameterTypes.length; ++i)
    {
      if ((real[i] = parameterTypes[i].getRealClass()) == null)
        throw new SynthesisException(SynthesisException.UNREIFIED);
    }

    return new Constructor(realclass.getConstructor(real), this);
  }

  /**
   * Returns an array containing Constructor objects
   * reflecting all the public constructors of the class
   * represented by this Class object. An array of length
   * 0 is returned if the class has no public
   * constructors.
   *
   *
   * @return an array containing Constructor objects
   * reflecting all the public constructors of the class
   * represented by this Class object.
   * 
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Constructor[] getConstructors() throws SecurityException
  {

    if (realclass != null && allconstructors == null)
    {
      java.lang.reflect.Constructor[] realDC = realclass.getConstructors();

      allconstructors = new Constructor[realDC.length];

      for (int i = 0; i < realDC.length; ++i)
      {
        allconstructors[i] = new Constructor(realDC[i], this);
      }
    }

    return allconstructors;
  }

  /**
   * This method is not implemented in VAJAVA 3.0
   * <p>
   * Returns an array of Class objects reflecting all the
   * classes and interfaces declared as members of the
   * class represented by this Class object. This
   * includes public, protected, default (package)
   * access, and private classes and interfaces declared
   * by the class, but excludes inherited classes and
   * interfaces. Returns an array of length 0 if the class
   * declares no classes or interfaces as members, or if
   * this Class object represents a primitive type.
   *
   *
   * @return an array of Class objects reflecting all the
   * classes and interfaces declared as members of the
   * class represented by this Class object.
   * 
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Class[] getDeclaredClasses() throws SecurityException
  {

    // ***** This should really be a single class plus declared interfaces.
    if (realclass != null && declaredclasses == null)
    {
      java.lang.Class[] realDE = realclass.getDeclaredClasses();

      declaredclasses = new Class[realDE.length];

      for (int i = 0; i < realDE.length; ++i)
      {
        declaredclasses[i] = forClass(realDE[i]);

        if (!realDE[i].isInterface())
          superclass = declaredclasses[i];
      }
    }

    return declaredclasses;
  }

  /**
   * Adds an "extends" description for the class or
   * interface represented by this Class object
   *
   * @param newclass The class that this class extends.
   * @throws SynthesisException
   * if the class has been reified.
   */
  public void addExtends(Class newclass) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    Class[] scratch = new Class[declaredclasses.length + 1];

    System.arraycopy(declaredclasses, 0, scratch, 0, declaredclasses.length);

    scratch[declaredclasses.length] = newclass;
    declaredclasses = scratch;
  }

  /**
   * Returns a Constructor object that reflects the
   * specified declared constructor of the class or
   * interface represented by this Class object. The
   * parameterTypes parameter is an array of Class
   * objects that identify the constructor's formal
   * parameter types, in declared order.
   *
   *
   * @param parameterTypes array of Class
   * objects that identify the constructor's formal
   * parameter types, in declared order.
   *
   * @return a Constructor object that reflects the
   * specified declared constructor of the class or
   * interface represented by this Class object.
   * 
   * @throws NoSuchMethodException
   * if a matching method is not found.
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Constructor getDeclaredConstructor(Class parameterTypes[])
          throws NoSuchMethodException, SecurityException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Adds a Constructor description for  the class or
   * interface represented by this Class object
   *
   * @return The constructor object.
   * 
   * @throws SynthesisException
   * if the class has been reified.
   */
  public Constructor declareConstructor() throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    Constructor newctor = new Constructor(this);
    Constructor[] scratch = new Constructor[declaredconstructors.length + 1];

    System.arraycopy(declaredconstructors, 0, scratch, 0,
                     declaredconstructors.length);

    scratch[declaredconstructors.length] = newctor;
    declaredconstructors = scratch;
    scratch = new Constructor[allconstructors.length + 1];

    System.arraycopy(allconstructors, 0, scratch, 0, allconstructors.length);

    scratch[allconstructors.length] = newctor;
    allconstructors = scratch;

    return newctor;
  }

  /**
   * State that this class implements a specified interface.
   * This does not yet update allMethods or otherwise
   * attempt to inherit data.
   *
   * @param newifce org.apache.xml.utils.synthetic.Class representing the interface we want to add.
   *
   * @return The new interface class.
   * 
   * @throws org.apache.xml.utils.synthetic.SynthesisException if the Class isn't an interface
   */
  public Class declareInterface(Class newifce) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    if (!newifce.isInterface())
      throw new SynthesisException(SynthesisException.SYNTAX,
                                   newifce.getName() + " isn't an interface");

    Class[] scratch = new Class[interfaces.length + 1];

    System.arraycopy(interfaces, 0, scratch, 0, interfaces.length);

    scratch[interfaces.length] = newifce;
    interfaces = scratch;
    scratch = new Class[allclasses.length + 1];

    System.arraycopy(allclasses, 0, scratch, 0, allclasses.length);

    scratch[allclasses.length] = newifce;
    allclasses = scratch;

    return newifce;
  }

  /**
   * Returns an array of Constructor objects reflecting
   * all the constructors declared by the class
   * represented by this Class object. These are public,
   * protected, default (package) access, and private
   * constructors. Returns an array of length 0 if this
   * Class object represents an interface or a primitive
   * type.
   * <p>
   * See The Java Language Specification, section 8.2.
   *
   *
   * @return an array of Constructor objects reflecting
   * all the constructors declared by the class
   * represented by this Class object.
   * 
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Constructor[] getDeclaredConstructors() throws SecurityException
  {

    if (realclass != null && declaredconstructors == null)
    {
      java.lang.reflect.Constructor[] realDC =
        realclass.getDeclaredConstructors();

      declaredconstructors = new Constructor[realDC.length];

      for (int i = 0; i < realDC.length; ++i)
      {
        declaredconstructors[i] = new Constructor(realDC[i], this);
      }
    }

    return declaredconstructors;
  }

  /**
   * Returns a Field object that reflects the specified
   * declared field of the class or interface represented
   * by this Class object. The name parameter is a
   * String that specifies the simple name of the desired
   * field.
   *
   *
   * @param name String that specifies the simple name of the desired
   * field.
   *
   * @return a Field object that reflects the specified
   * declared field of the class or interface represented
   * by this Class object.
   * 
   * @throws NoSuchFieldException
   * if a field with the specified name is not found.
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Field getDeclaredField(String name)
          throws NoSuchFieldException, SecurityException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Adds a Field description for  the class or
   * interface represented by this Class object
   *
   *
   * @param name The name of the field.
   *
   * @return The field description.
   * 
   * @throws SynthesisException
   * if the class has been reified.
   */
  public Field declareField(String name) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    Field newfield = new Field(name, this);
    Field[] scratch = new Field[declaredfields.length + 1];

    System.arraycopy(declaredfields, 0, scratch, 0, declaredfields.length);

    scratch[declaredfields.length] = newfield;
    declaredfields = scratch;
    scratch = new Field[allfields.length + 1];

    System.arraycopy(allfields, 0, scratch, 0, allfields.length);

    scratch[allfields.length] = newfield;
    allfields = scratch;

    return newfield;
  }

  /**
   * Returns an array of Field objects reflecting all the
   * fields declared by the class or interface represented
   * by this Class object. This includes public,
   * protected, default (package) access, and private
   * fields, but excludes inherited fields. Returns an
   * array of length 0 if the class or interface declares
   * no fields, or if this Class object represents a
   * primitive type. See The Java Language
   * Specification, sections 8.2 and 8.3.
   *
   *
   * @return array of Field objects reflecting all the
   * fields declared by the class or interface represented
   * by this Class object.
   * 
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Field[] getDeclaredFields() throws SecurityException
  {

    if (realclass != null && declaredfields == null)
    {
      java.lang.reflect.Field[] realDF = realclass.getDeclaredFields();

      declaredfields = new Field[realDF.length];

      for (int i = 0; i < realDF.length; ++i)
      {
        declaredfields[i] = new Field(realDF[i], this);
      }
    }

    return declaredfields;
  }

  /**
   * Returns a Method object that reflects the specified
   * declared method of the class or interface
   * represented by this Class object. The name
   * parameter is a String that specifies the simple
   * name of the desired method, and the
   * parameterTypes parameter is an array of Class
   * objects that identify the method's formal parameter
   * types, in declared order.
   *
   *
   * @param name String that specifies the simple
   * name of the desired method.
   * 
   * @param parameterTypes array of Class
   * objects that identify the method's formal parameter
   * types, in declared order.
   *
   * @return Method object that reflects the specified
   * declared method of the class or interface
   * represented by this Class object.
   * 
   * @throws NoSuchMethodException
   * if a matching method is not found.
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Method getDeclaredMethod(String name, Class parameterTypes[])
          throws NoSuchMethodException, SecurityException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Adds a Method description for  the class or
   * interface represented by this Class object
   *
   *
   * @param name Name of method.
   *
   * @return The method object.
   * 
   * @throws SynthesisException
   * if the class has been reified.
   */
  public Method declareMethod(String name) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    Method newMethod = new Method(name, this);
    Method[] scratch = new Method[declaredmethods.length + 1];

    System.arraycopy(declaredmethods, 0, scratch, 0, declaredmethods.length);

    scratch[declaredmethods.length] = newMethod;
    declaredmethods = scratch;
    scratch = new Method[allmethods.length + 1];

    System.arraycopy(allmethods, 0, scratch, 0, allmethods.length);

    scratch[allmethods.length] = newMethod;
    allmethods = scratch;

    return newMethod;
  }

  /**
   * Returns an array of Method objects reflecting all
   * the methods declared by the class or interface
   * represented by this Class object. This includes
   * public, protected, default (package) access, and
   * private methods, but excludes inherited methods.
   * Returns an array of length 0 if the class or interface
   * declares no methods, or if this Class object
   * represents a primitive type.
   * <p>
   * See The Java Language Specification, section 8.2.
   *
   * @return array of Method objects reflecting all
   * the methods declared by the class or interface
   * represented by this Class object.
   * 
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Method[] getDeclaredMethods() throws SecurityException
  {

    if (realclass != null && declaredmethods == null)
    {
      java.lang.reflect.Method[] realDM = realclass.getDeclaredMethods();

      declaredmethods = new Method[realDM.length];

      for (int i = 0; i < realDM.length; ++i)
      {
        declaredmethods[i] = new Method(realDM[i], this);
      }
    }

    return declaredmethods;
  }

  /**
   * This method is not implemented in VAJava 3.0
   * <p>
   * If the class or interface represented by this Class
   * object is a member of another class, returns the
   * Class object representing the class of which it is a
   * member (its declaring class). Returns null if this
   * class or interface is not a member of any other
   * class.
   *
   */
  public Class getDeclaringClass()
  {

    if (realclass != null && declaringclass == null)
    {
      java.lang.Class dc = realclass.getDeclaringClass();

      if (dc == null)
        declaringclass = null;
      else
        declaringclass = forClass(dc);
    }

    return declaringclass;
  }

  /**
   * Declare that this class is an inner class of another.
   *
   * @param newclass
   *
   * @throws SynthesisException
   */
  private void addInnerClass(Class newclass) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    if (newclass.getDeclaringClass() != this)
      throw new SynthesisException(SynthesisException.WRONG_OWNER);

    Class[] scratch = new Class[innerclasses.length + 1];

    System.arraycopy(innerclasses, 0, scratch, 0, innerclasses.length);

    scratch[innerclasses.length] = newclass;
    innerclasses = scratch;
  }

  /**
   * Declare a class contained within this class. This doesn't
   * address anonymous classes (those go inside method bodies
   * and similar code), just local classes.
   * <p>
   * ***** This requires lookup methods that operate in the
   * context of a specific class, and per-class registries!
   *
   * @param className Local name of inner class to create. This should _not_ be a
   * qualified name, unlike the normal forName() call. Its
   * hierarchy is established by the class within which it is
   * created.
   * @return org.apache.xml.utils.synthetic.Class object for the contained class.
   * @throws org.apache.xml.utils.synthetic.SynthesisException if class could not be created.
   * @since 2/2000
   *
   * @throws SynthesisException
   */
  public Class declareInnerClass(String className) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    String relativeName = getName() + "$" + className;
    Class newclass = (Class) (global_classtable.get(relativeName));

    if (newclass != null)
      throw new SynthesisException(SynthesisException.SYNTAX,
                                   "Inner class " + name + " already exists");

    newclass = new Class(className);
    newclass.declaringclass = this;

    Class[] scratch = new Class[innerclasses.length + 1];

    System.arraycopy(innerclasses, 0, scratch, 0, innerclasses.length);

    scratch[innerclasses.length] = newclass;
    innerclasses = scratch;

    return newclass;
  }

  /**
   * Fetch a list of classes contained within this class.
   * This doesn't address anonymous classes (those go
   * inside method bodies and similar code), just local classes.
   *
   * @return org.apache.xml.utils.synthetic.Class[] object for the contained classes.
   * This may be empty if none such exist, or if the class is
   * reified (since reflection doesn't report this information).
   * @since 3/2000
   */
  public Class[] getInnerClasses()
  {
    return innerclasses;
  }

  /**
   * Returns a Field object that reflects the specified
   * public member field of the class or interface
   * represented by this Class object. The name
   * parameter is a String specifying the simple name of
   * the desired field.
   * <p>
   * The field to be reflected is located by searching all
   * the member fields of the class or interface
   * represented by this Class object for a public field
   * with the specified name.
   * <p>
   * See The Java Language Specification, sections 8.2
   * and 8.3.
   *
   *
   * @param name
   *
   * @throws NoSuchFieldException
   * if a field with the specified name is not
   * found.
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Field getField(String name)
          throws NoSuchFieldException, SecurityException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns an array containing Field objects
   * reflecting all the accessible public fields of the
   * class or interface represented by this Class object.
   * Returns an array of length 0 if the class or interface
   * has no accessible public fields, or if it represents
   * an array type or a primitive type.
   * <p>
   * Specifically, if this Class object represents a class,
   * returns the public fields of this class and of all its
   * superclasses. If this Class object represents an
   * interface, returns the fields of this interface and of
   * all its superinterfaces. If this Class object
   * represents an array type or a primitive type, returns
   * an array of length 0.
   * <p>
   * The implicit length field for array types is not
   * reflected by this method. User code should use the
   * methods of class Array to manipulate arrays.
   * <p>
   * See The Java Language Specification, sections 8.2
   * and 8.3.
   *
   *
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Field[] getFields() throws SecurityException
  {

    if (realclass != null && allfields == null)
    {
      java.lang.reflect.Field[] realDF = realclass.getFields();

      allfields = new Field[realDF.length];

      for (int i = 0; i < realDF.length; ++i)
      {
        allfields[i] = new Field(realDF[i], this);
      }
    }

    return allfields;
  }

  /**
   * Determines the interfaces implemented by the
   * class or interface represented by this object.
   * <p>
   * If this object represents a class, the return value is
   * an array containing objects representing all
   * interfaces implemented by the class. The order of
   * the interface objects in the array corresponds to the
   * order of the interface names in the implements
   * clause of the declaration of the class represented by
   * this object.
   * <p>
   * If this object represents an interface, the array
   * contains objects representing all interfaces
   * extended by the interface. The order of the
   * interface objects in the array corresponds to the
   * order of the interface names in the extends clause
   * of the declaration of the interface represented by
   * this object.
   * <p>
   * If the class or interface implements no interfaces,
   * the method returns an array of length 0.
   *
   * an array of interfaces implemented by this
   * class.
   */
  public Class[] getInterfaces()
  {

    if (realclass != null && interfaces == null)
    {
      java.lang.Class[] realI = realclass.getInterfaces();

      interfaces = new Class[realI.length];

      for (int i = 0; i < realI.length; ++i)
      {
        interfaces[i] = forClass(realI[i]);
      }
    }

    return interfaces;
  }

  /**
   * Adds an "implements" description for the class or
   * interface represented by this Class object
   *
   *
   * @param newclass
   * @throws SynthesisException
   * if the class has been reified.
   */
  public void addImplements(Class newclass) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    Class[] scratch = new Class[interfaces.length + 1];

    System.arraycopy(interfaces, 0, scratch, 0, interfaces.length);

    scratch[interfaces.length] = newclass;
    interfaces = scratch;
  }

  /**
   * Returns a Method object that reflects the specified
   * public member method of the class or interface
   * represented by this Class object. The name
   * parameter is a String specifying the simple name
   * the desired method, and the parameterTypes
   * parameter is an array of Class objects that identify
   * the method's formal parameter types, in declared
   * order.
   * <p>
   * The method to reflect is located by searching all
   * the member methods of the class or interface
   * represented by this Class object for a public
   * method with the specified name and exactly the
   * same formal parameter types.
   * <p>
   * See The Java Language Specification, sections 8.2
   * and 8.4.
   *
   *
   * @param name
   * @param parameterTypes
   *
   * @throws NoSuchMethodException
   * if a matching method is not found.
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Method getMethod(String name, Class parameterTypes[])
          throws NoSuchMethodException, SecurityException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns an array containing Method objects
   * reflecting all the public member methods of the
   * class or interface represented by this Class object,
   * including those declared by the class or interface
   * and and those inherited from superclasses and
   * superinterfaces. Returns an array of length 0 if the
   * class or interface has no public member methods.
   * <p>
   * See The Java Language Specification, sections 8.2
   * and 8.4.
   *
   *
   * @throws SecurityException
   * if access to the information is denied.
   */
  public Method[] getMethods() throws SecurityException
  {

    if (realclass != null && allmethods == null)
    {
      java.lang.reflect.Method[] realDM = realclass.getMethods();

      allmethods = new Method[realDM.length];

      for (int i = 0; i < realDM.length; ++i)
      {
        allmethods[i] = new Method(realDM[i], this);
      }
    }

    return allmethods;
  }

  /**
   * Returns the Java language modifiers for this class
   * or interface, encoded in an integer. The modifiers
   * consist of the Java Virtual Machine's constants for
   * public, protected, private, final, and interface; they
   * should be decoded using the methods of class
   * Modifier.
   *
   * The modifier encodings are defined in The Java
   * Virtual Machine Specification, table 4.1.
   *
   * See Also:
   * java.lang.reflect.Modifier
   *
   */
  public int getModifiers()
  {
    return modifiers;
  }

  /**
   *   Set the Java language modifiers for this class
   *   or interface, encoded in an integer. The modifiers
   *   consist of the Java Virtual Machine's constants for
   *   public, protected, private, final, and interface; they
   *   should be decoded using the methods of class
   *   Modifier.
   *
   *   The modifier encodings are defined in The Java
   *   Virtual Machine Specification, table 4.1.
   *
   *   See Also:
   *   java.lang.reflect.Modifier
   *
   * @param modifiers
   *
   * @throws SynthesisException
   */
  public void setModifiers(int modifiers) throws SynthesisException
  {

    if (this.realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.modifiers = modifiers;
  }

  /**
   * Retrieve the fully-qualified classname. If it's an array,
   * it will be returned in JVM syntax, not Java syntax.
   *
   * @return java.lang.String
   * @since 12/95
   */
  public java.lang.String getName()
  {
    return name;
  }

  /**
   * Like getName, but back-convert array notation escapes.
   * ***** DOESN'T YET HANDLE ARRAYS OF PRIMITIVES!
   *
   * @return java.lang.String
   * @since 3/2000
   */
  public java.lang.String getJavaName()
  {

    if (name.charAt(0) != '[')
      return name;

    // Object array syntax is [Ltypename; 
    // add another [ for each level of array
    int count = name.lastIndexOf('[');
    StringBuffer jname = new StringBuffer(name.substring(count + 2));

    // Trim the trailing ';'
    jname.setLength(jname.length() - 1);

    while (count-- >= 0)
    {
      jname.append("[]");
    }

    return jname.toString();
  }

  /**
   * Extract just the local name of this class, minus the package
   * prefix.
   *
   * ***** I don't think this handles array types properly yet.
   *
   * @return java.lang.String
   * @since 12/99
   */
  public java.lang.String getShortName()
  {

    int start = name.lastIndexOf(".");

    if (start != 0 || name.charAt(0) == '.')
      ++start;

    if (declaringclass != null)
    {
      int d = name.lastIndexOf('$', start);

      if (d != 0)
        start = d + 1;
    }

    return name.substring(start);
  }

  /**
   * Like getShortName, but back-convert array notation escapes.
   * ***** DOESN'T YET HANDLE ARRAYS OF PRIMITIVES!
   *
   * @return java.lang.String
   * @since 3/2000
   */
  public java.lang.String getJavaShortName()
  {

    String shortname = getShortName();

    if (shortname.charAt(0) != '[')
      return shortname;

    // Object array syntax is [Ltypename; 
    // add another [ for each level of array
    int count = shortname.lastIndexOf('[');
    StringBuffer jname = new StringBuffer(shortname.substring(count + 2));

    // Trim the trailing ';'
    jname.setLength(jname.length() - 1);

    while (count-- >= 0)
    {
      jname.append("[]");
    }

    return jname.toString();
  }

  /**
   * Extract the package name for this class.
   * ***** I don't think this handles array classes properly yet.
   *
   * @return java.lang.String
   * @since 12/95
   */
  public java.lang.String getPackageName()
  {

    int start = name.lastIndexOf(".");

    return name.substring(0, start);
  }

  /**
   * If this synthetic class is a wrapper for a "real"
   * java.lang.Class -- either because it was instantiated as such
   * or because it has been compiled -- this method will return
   * that class. Otherwise it returns null.
   * Creation date: (12-25-99 12:26:01 PM)
   * @return org.apache.xml.utils.synthetic.Class
   */
  public java.lang.Class getRealClass()
  {
    return realclass;
  }

  /**
   * This call is intended to allow an existing org.apache.xml.utils.synthetic.Class
   * to be switched from purely descriptive mode to proxy mode
   * ("reified").
   * The primary intent is to allow a org.apache.xml.utils.synthetic.Class to be
   * "compiled in place"
   * <p>
   * This should have the side-effect of limiting further mutation
   * of the org.apache.xml.utils.synthetic.Class to things which can not be obtained
   * from the real Class object, to avoid "lying" to the user
   * <p>
   * NOTE: Not all information defined by the Java libraries is
   * in fact available in all Java environments. We assume the
   * calls will work; if they return null or empty lists, there's
   * nothing we can do about it. Note that this may mean that a
   * reified class tells us less about itself than the
   * synthetic description used to generate it.
   * <p>
   * Creation date: (12-25-99 12:26:01 PM)
   * @param java.lang.class realclass nonsynthetic Class object to proxy
   *
   * @param realclass
   *
   * @throws SynthesisException
   */
  public void setRealClass(java.lang.Class realclass)
          throws SynthesisException
  {

    if (this.realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.realclass = realclass;
    this.modifiers = realclass.getModifiers();
    this.isInterface = realclass.isInterface();

    // DEFERRED -- set them null now, reconstruct when requested
    this.declaringclass = null;
    this.interfaces = null;
    this.declaredconstructors = null;
    this.allconstructors = null;
    this.declaredmethods = null;
    this.allmethods = null;
    this.declaredfields = null;
    this.allfields = null;
    this.declaredclasses = null;
    this.allclasses = null;
    this.superclass = null;
  }

  /**
   * Set the superclass for this synthetic class.
   * Object is equivalent to Null.
   * Creation date: (12-25-99 12:26:01 PM)
   *
   * @param superclass
   * @return org.apache.xml.utils.synthetic.Class
   *
   * @throws SynthesisException
   */
  public void setSuperClass(Class superclass) throws SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.superclass = superclass;
  }

  /**
   * Set the superclass for this synthetic class.
   * Creation date: (12-25-99 12:26:01 PM)
   *
   * @param superclass
   * @return org.apache.xml.utils.synthetic.Class
   *
   * @throws ClassNotFoundException
   * @throws SynthesisException
   */
  public void setSuperClass(java.lang.Class superclass)
          throws ClassNotFoundException, SynthesisException
  {

    if (realclass != null)
      throw new SynthesisException(SynthesisException.REIFIED);

    this.superclass = Class.forClass(superclass);
  }

  /**
   * Finds a resource with the specified name. The
   * rules for searching for resources associated with a
   * given class are implemented by the class loader of
   * the class.
   * <p>
   * The Class methods delegate to ClassLoader
   * methods, after applying a naming convention: if
   * the resource name starts with "/", it is used as is.
   * Otherwise, the name of the package is prepended,
   * after converting "." to "/".
   *
   * @param
   * name - the string representing the resource to
   * be found.
   * the URL object having the specified name, or
   * null if no resource with the specified name
   * is found.
   */
  public java.net.URL getResource(String name)
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Finds a resource with a given name. Will return
   * null if no resource with this name is found. The
   * rules for searching a resources associated with a
   * given class are implemented by the ClassLoader of
   * the class.
   * <p>
   * The Class methods delegate to ClassLoader
   * methods, after applying a naming convention: if
   * the resource name starts with "/", it is used as is.
   * Otherwise, the name of the package is prepended,
   * after converting "." to "/".
   *
   * @param
   * name - the string representing the resource to
   * be found
   * the InputStream object having the
   * specified name, or null if no resource with
   * the specified name is found.
   */
  public java.io.InputStream getResourceAsStream(String name)
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Get the signers of this class.
   *
   */
  public Object[] getSigners()
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * If this object represents any class other than the
   * class Object, then the object that represents the
   * superclass of that class is returned.
   * <p>
   * If this object is the one that represents the class
   * Object or this object represents an interface, null is
   * returned.
   *
   * the superclass of the class represented by this
   * object.
   */
  public Class getSuperclass()
  {

    if (realclass != null && superclass == null)
    {
      superclass = forClass(realclass.getSuperclass());

      // getDeclaredClasses(); // Sets superclass as a side-effect
    }

    if (superclass == null)
      superclass = forClass(Object.class);

    return superclass;
  }

  /**
   * If this Class object represents an array type, returns
   * true, otherwise returns false.
   *
   */
  public boolean isArray()
  {
    return realclass != null && realclass.isArray();
  }

  /**
   * Determines if the class or interface represented by
   * this Class object is either the same as, or is a
   * superclass or superinterface of, the class or
   * interface represented by the specified Class
   * parameter. It returns true if so, false otherwise. If
   * this Class object represents a primitive type,
   * returns true if the specified Class parameter is
   * exactly this Class object, false otherwise.
   * <p>
   * Specifically, this method tests whether the type
   * represented by the specified Class parameter can
   * be converted to the type represented by this Class
   * object via an identity conversion or via a widening
   * reference conversion. See The Java Language
   * Specification, sections 5.1.1 and 5.1.4 , for details.
   *
   *
   * @param cls
   *
   * @throws NullPointerException if the specified Class parameter is null.
   */
  public boolean isAssignableFrom(Class cls)
  {

    if (realclass != null && cls.realclass != null)
      return realclass.isAssignableFrom(cls.realclass);

    throw new java.lang.IllegalStateException();
  }

  /**
   * Determines if the class or interface represented by
   * this Class object is either the same as, or is a
   * superclass or superinterface of, the class or
   * interface represented by the specified Class
   * parameter. It returns true if so, false otherwise. If
   * this Class object represents a primitive type,
   * returns true if the specified Class parameter is
   * exactly this Class object, false otherwise.
   * <p>
   * Specifically, this method tests whether the type
   * represented by the specified Class parameter can
   * be converted to the type represented by this Class
   * object via an identity conversion or via a widening
   * reference conversion. See The Java Language
   * Specification, sections 5.1.1 and 5.1.4 , for details.
   *
   *
   * @param cls
   *
   * @throws NullPointerException if the specified Class parameter is null.
   */
  public boolean isAssignableFrom(java.lang.Class cls)
  {

    if (realclass != null)
      return realclass.isAssignableFrom((java.lang.Class) cls);

    throw new java.lang.IllegalStateException();
  }

  /**
   * This method is the dynamic equivalent of the Java
   * language instanceof operator. The method
   * returns true if the specified Object argument is
   * non-null and can be cast to the reference type
   * represented by this Class object without raising a
   * ClassCastException. It returns false otherwise.
   * <p>
   * Specifically, if this Class object represents a
   * declared class, returns true if the specified Object
   * argument is an instance of the represented class (or
   * of any of its subclasses); false otherwise. If this
   * Class object represents an array class, returns true
   * if the specified Object argument can be converted
   * to an object of the array type by an identity
   * conversion or by a widening reference conversion;
   * false otherwise. If this Class object represents an
   * interface, returns true if the class or any superclass
   * of the specified Object argument implements this
   * interface; false otherwise. If this Class object
   * represents a primitive type, returns false.
   *
   * @param obj The object to check
   *
   */
  public boolean isInstance(Object obj)
  {

    if (realclass != null)
      return realclass.isInstance(obj);

    // Scan inheritances? (reliable).
    // Check name? (not reliable).
    throw new java.lang.IllegalStateException();
  }

  /**
   * Determines if the specified Class object represents
   * an interface type.
   *
   * true if this object represents an interface;
   * false otherwise.
   */
  public boolean isInterface()
  {
    return (realclass != null) ? realclass.isInterface() : isInterface;
  }

  /**
   * Assert that the specified Class object represents
   * an interface type. Can't be changed after real class loaded.
   *
   * @param
   * true if this object represents an interface;
   * false otherwise.
   *
   * @param isInterface
   *
   * @throws SynthesisException
   */
  public void isInterface(boolean isInterface) throws SynthesisException
  {

    if (realclass == null)
      this.isInterface = isInterface;
    else if (realclass.isInterface() != isInterface)
      throw new SynthesisException(SynthesisException.REIFIED);
  }

  /**
   * Determines if the specified Class object represents
   * a primitive Java type.
   * <p>
   * There are nine predefined Class objects to
   * represent the eight primitive Java types and void.
   * These are created by the Java Virtual Machine, and
   * have the same names as the primitive types that
   * they represent, namely boolean, byte, char, short,
   * int, long, float, and double, and void.
   * <p>
   * These objects may only be accessed via the
   * following public static final variables, and are the
   * only Class objects for which this method returns
   * true.
   *
   */
  public boolean isPrimitive()
  {
    return realclass != null && realclass.isPrimitive();
  }

  /**
   * Creates a new instance of a class.
   *
   * a newly allocated instance of the class
   * represented by this object. This is done
   * exactly as if by a new expression with an
   * empty argument list.
   * @throws IllegalAccessException
   * if the class or initializer is not accessible.
   * @throws InstantiationException
   * if an application tries to instantiate an
   * abstract class or an interface, or if the
   * instantiation fails for some other reason.
   */
  public Object newInstance()
          throws InstantiationException, IllegalAccessException
  {
    throw new java.lang.IllegalStateException();
  }

  /**
   * Converts the object to a string. The string
   * representation is the string "class" or
   * "interface" followed by a space and then the
   * fully qualified name of the class. If this Class
   * object represents a primitive type, returns the
   * name of the primitive type.
   * <p>
   *  Should this say "synthetic" as well as "class" or
   * "interface"? Or should that be gated on whether we're proxy
   * to a realclass?
   *
   * @return a string representation of this class object.
   */
  public String toString()
  {

    if (realclass != null)
      return realclass.toString();
    else if (isInterface())
      return "interface " + name;
    else
      return "class " + name;
  }

  /**
   * Convenience for writing to, eg, System.out 
   *
   * @param out
   * @param depth
   */
  public void toSource(java.io.OutputStream out, int depth)
  {

    java.io.PrintWriter writer = new java.io.PrintWriter(out);

    toSource(writer, depth);
  }

  /**
   * Converts the object to a Java code stream. The string
   * representation is as full a Java definition of the class
   * as we are able to achieve. If this Class
   * object represents a primitive type, returns the
   * name of the primitive type.
   *
   * @param out
   * @param depth
   */
  public void toSource(java.io.PrintWriter out, int depth)
  {

    String tab = tabset(depth);

    if (realclass != null)
      out.println(
        tab
        + "/** Code back-generated from a \"real\" Class; accuracy limited by reflection APIs. */");
    else
      out.println(
        tab
        + "/** Code generated via org.apache.xml.utils.synthetic.Class */");

    /* Package should not be printed for inner classes */
    if (getDeclaringClass() == null)
      out.println(tab + "package " + getPackageName() + ";");

    out.print(tab + Modifier.toString(getModifiers()));

    if (isInterface())
      out.print(" interface ");
    else
      out.print(" class ");

    out.println(getJavaShortName());

    if (superclass != null)
    {
      out.print('\n' + tab + " extends " + superclass.getJavaName());
    }

    Class[] ext = getInterfaces();

    if (ext != null & ext.length > 0)
    {

      // Interfaces extend other interfaces,
      // Classes implement interfaces.
      out.print('\n' + tab + (isInterface ? " extends " : " implements ")
                + ext[0].getName());

      for (int i = 1; i < ext.length; ++i)
      {
        out.print(", " + ext[i].getJavaName());
      }

      out.print("\n");
    }

    out.print(tab + "{\n");

    tab = tabset(++depth);

    // Fields--------------------------------
    Field[] fields = null;

    try
    {
      fields = getDeclaredFields();
    }
    catch (SecurityException e)
    {
      out.println(tab + "//SecurityException retrieving fields");
    }

    if (fields != null)
    {
      for (int i = 0; i < fields.length; ++i)
      {
        out.println(tab + fields[i].toSource());
      }
    }

    // Constructors--------------------------
    Constructor[] ctors = null;

    try
    {
      ctors = getDeclaredConstructors();
    }
    catch (SecurityException e)
    {
      out.println(tab + "//SecurityException retrieving ctors");
    }

    if (ctors != null)
    {
      for (int i = 0; i < ctors.length; ++i)
      {
        out.print(ctors[i].toSource(tab));
      }
    }

    // Methods-------------------------------
    Method[] methods = null;

    try
    {
      methods = getDeclaredMethods();
    }
    catch (SecurityException e)
    {
      out.println(tab + "//SecurityException retrieving methods");
    }

    if (methods != null)
    {
      for (int i = 0; i < methods.length; ++i)
      {
        out.print('\n');
        out.print(methods[i].toSource(tab));
      }
    }

    // Inner classes --------------------------------
    Class[] inners = getInnerClasses();

    if (inners != null)
    {
      for (int i = 0; i < inners.length; ++i)
      {
        out.print('\n');
        inners[i].toSource(out, depth);
      }
    }

    // Done------------------------------
    tab = tabset(--depth);

    out.print(tab + "}\n");
    out.flush();
  }

  /**
   * Method tabset 
   *
   *
   * @param depth
   *
   * (tabset) @return
   */
  private String tabset(int depth)
  {

    StringBuffer t = new StringBuffer();

    while (depth-- > 0)
    {
      t.append("    ");
    }

    return t.toString();
  }

  // Ignores any keywords we don't recognize

  /** Field val          */
  static final int[] val = { Modifier.ABSTRACT, Modifier.FINAL,
                             Modifier.INTERFACE, Modifier.NATIVE,
                             Modifier.PRIVATE, Modifier.PROTECTED,
                             Modifier.PUBLIC, Modifier.STATIC,
                             Modifier.SYNCHRONIZED, Modifier.TRANSIENT,
                             Modifier.VOLATILE };

  /** Field kwd          */
  static final String[] kwd = { "abstract", "final", "interface", "native",
                                "private", "protected", "public", "static",
                                "synchronized", "transient", "volatile" };

  /**
   * Method modifierFromString 
   *
   *
   * @param t
   *
   * (modifierFromString) @return
   */
  static public int modifierFromString(String t)
  {

    for (int i = 0; i < kwd.length; ++i)
    {
      if (kwd[i].equals(t))
        return val[i];
    }

    return 0;
  }

  /**
   * Method modifiersFromString 
   *
   *
   * @param s
   *
   * (modifiersFromString) @return
   */
  static public int modifiersFromString(String s)
  {

    int mods = 0;
    java.util.StringTokenizer parts = new java.util.StringTokenizer(s);

    while (parts.hasMoreTokens())
    {
      String t = parts.nextToken();

      mods |= modifierFromString(t);
    }

    return mods;
  }
}
