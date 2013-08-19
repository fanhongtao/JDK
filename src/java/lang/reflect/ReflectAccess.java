/*
 * @(#)ReflectAccess.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

import sun.reflect.MethodAccessor;
import sun.reflect.ConstructorAccessor;

/** Package-private class implementing the
    sun.reflect.LangReflectAccess interface, allowing the java.lang
    package to instantiate objects in this package. */

class ReflectAccess implements sun.reflect.LangReflectAccess {
    public Field newField(Class declaringClass,
                          String name,
                          Class type,
                          int modifiers,
                          int slot)
    {
        return new Field(declaringClass,
                         name,
                         type,
                         modifiers,
                         slot);
    }

    public Method newMethod(Class declaringClass,
                            String name,
                            Class[] parameterTypes,
                            Class returnType,
                            Class[] checkedExceptions,
                            int modifiers,
                            int slot)
    {
        return new Method(declaringClass,
                          name,
                          parameterTypes,
                          returnType,
                          checkedExceptions,
                          modifiers,
                          slot);
    }

    public Constructor newConstructor(Class declaringClass,
                                      Class[] parameterTypes,
                                      Class[] checkedExceptions,
                                      int modifiers,
                                      int slot)
    {
        return new Constructor(declaringClass,
                               parameterTypes,
                               checkedExceptions,
                               modifiers,
                               slot);
    }

    public MethodAccessor getMethodAccessor(Method m) {
        return m.getMethodAccessor();
    }

    public void setMethodAccessor(Method m, MethodAccessor accessor) {
        m.setMethodAccessor(accessor);
    }

    public ConstructorAccessor getConstructorAccessor(Constructor c) {
        return c.getConstructorAccessor();
    }

    public void setConstructorAccessor(Constructor c,
                                       ConstructorAccessor accessor)
    {
        c.setConstructorAccessor(accessor);
    }

    public int getConstructorSlot(Constructor c) {
        return c.getSlot();
    }

    //
    // Copying routines, needed to quickly fabricate new Field,
    // Method, and Constructor objects from templates
    //
    public Method      copyMethod(Method arg) {
        return arg.copy();
    }

    public Field       copyField(Field arg) {
        return arg.copy();
    }

    public Constructor copyConstructor(Constructor arg) {
        return arg.copy();
    }
}
