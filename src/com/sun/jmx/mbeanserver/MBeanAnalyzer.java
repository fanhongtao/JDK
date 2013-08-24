/*
 * @(#)MBeanAnalyzer.java	1.11 06/06/20
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import static com.sun.jmx.mbeanserver.Util.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.NotCompliantMBeanException;

/**
 * <p>An analyzer for a given MBean interface.  The analyzer can
 * be for Standard MBeans or MXBeans, depending on the MBeanIntrospector
 * passed at construction.
 *
 * <p>The analyzer can
 * visit the attributes and operations of the interface, calling
 * a caller-supplied visitor method for each one.</p>
 *
 * @param <M> Method or ConvertingMethod according as this is a
 * Standard MBean or an MXBean.
 *
 * @since 1.6
 */
class MBeanAnalyzer<M> {
    
    static interface MBeanVisitor<M> {
        public void visitAttribute(String attributeName,
                M getter,
                M setter);
        public void visitOperation(String operationName,
                M operation);
    }
    
    void visit(MBeanVisitor<M> visitor) {
        // visit attributes
        for (Map.Entry<String, AttrMethods<M>> entry : attrMap.entrySet()) {
            String name = entry.getKey();
            AttrMethods<M> am = entry.getValue();
            visitor.visitAttribute(name, am.getter, am.setter);
        }
        
        // visit operations
        for (Map.Entry<String, List<M>> entry : opMap.entrySet()) {
            for (M m : entry.getValue())
                visitor.visitOperation(entry.getKey(), m);
        }
    }
    
    /* Map op name to method */
    private Map<String, List<M>> opMap = newSortedMap();
    /* Map attr name to getter and/or setter */
    private Map<String, AttrMethods<M>> attrMap = newSortedMap();
    
    private static class AttrMethods<M> {
        M getter;
        M setter;
    }
    
    /**
     * <p>Return an MBeanAnalyzer for the given MBean interface and
     * MBeanIntrospector.  Calling this method twice with the same
     * parameters may return the same object or two different but
     * equivalent objects.
     */
    // Currently it's two different but equivalent objects.  This only
    // really impacts proxy generation.  For MBean creation, the
    // cached PerInterface object for an MBean interface means that
    // an analyzer will not be recreated for a second MBean using the
    // same interface.
    static <M> MBeanAnalyzer<M> analyzer(Class<?> mbeanInterface,
            MBeanIntrospector<M> introspector)
            throws NotCompliantMBeanException {
        return new MBeanAnalyzer<M>(mbeanInterface, introspector);
    }
    
    private MBeanAnalyzer(Class<?> mbeanInterface,
            MBeanIntrospector<M> introspector)
            throws NotCompliantMBeanException {
        if (!mbeanInterface.isInterface()) {
            throw new NotCompliantMBeanException("Not an interface: " +
                    mbeanInterface.getName());
        }
   
        try {
            initMaps(mbeanInterface,introspector);
        } catch (Exception x) {
            throw Introspector.throwException(mbeanInterface,x);
        }
    }
    
    // Introspect the mbeanInterface and initialize this object's maps.
    //
    private void initMaps(Class<?> mbeanInterface,
            MBeanIntrospector<M> introspector) throws Exception {
        final Method[] methodArray = mbeanInterface.getMethods();
        
        final List<Method> methods = eliminateCovariantMethods(methodArray);
        
           /* Run through the methods to detect inconsistencies and to enable
           us to give getter and setter together to visitAttribute. */
        for (Method m : methods) {
            String name = m.getName();
            
            final M cm = introspector.mFrom(m);
            
            String attrName = "";
            if (name.startsWith("get"))
                attrName = name.substring(3);
            else if (name.startsWith("is")
            && m.getReturnType() == boolean.class)
                attrName = name.substring(2);
            
            if (attrName.length() != 0 && m.getParameterTypes().length == 0
                    && m.getReturnType() != void.class) {
                // It's a getter
                // Check we don't have both isX and getX
                AttrMethods am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods();
                else {
                    if (am.getter != null) {
                        final String msg = "Attribute " + attrName +
                                " has more than one getter";
                        throw new NotCompliantMBeanException(msg);
                    }
                }
                am.getter = cm;
                attrMap.put(attrName, am);
            } else if (name.startsWith("set") && name.length() > 3
                    && m.getParameterTypes().length == 1 &&
                    m.getReturnType() == void.class) {
                // It's a setter
                attrName = name.substring(3);
                AttrMethods am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods();
                else if (am.setter != null) {
                    final String msg = "Attribute " + attrName +
                            " has more than one setter";
                    throw new NotCompliantMBeanException(msg);
                }
                am.setter = cm;
                attrMap.put(attrName, am);
            } else {
                // It's an operation
                List<M> cms = opMap.get(name);
                if (cms == null)
                    cms = newList();
                cms.add(cm);
                opMap.put(name, cms);
            }
        }
        /* Check that getters and setters are consistent. */
        for (Map.Entry<String, AttrMethods<M>> entry : attrMap.entrySet()) {
            AttrMethods<M> am = entry.getValue();
            if (!introspector.consistent(am.getter, am.setter)) {
                final String msg = "Getter and setter for " + entry.getKey() +
                        " have inconsistent types";
                throw new NotCompliantMBeanException(msg);
            }
        }
    }
    
    /* Eliminate methods that are overridden with a covariant return type.
       Reflection will return both the original and the overriding method
       but only the overriding one is of interest.  We return the methods
       in the same order they arrived in.  This isn't required by the spec
       but existing code may depend on it and users may be used to seeing
       operations or attributes appear in a particular order.  */
    static List<Method>
            eliminateCovariantMethods(Method[] methodArray) {
        // We are assuming that you never have very many methods with the
        // same name, so it is OK to use algorithms that are quadratic
        // in the number of methods with the same name.
        Map<String, Collection<Method>> map = newMap();
        for (Method m : methodArray) {
            Collection<Method> others = map.get(m.getName());
            if (others == null) {
                others = newList();
                map.put(m.getName(), others);
            }
            others.add(m);
        }
        Set<Method> overridden = newSet();
        for (Collection<Method> sameName : map.values()) {
            for (Method a : sameName) {
                for (Method b : sameName) {
                    if (a != b && overrides(a, b))
                        overridden.add(b);
                }
            }
        }
        List<Method> methods = newList(Arrays.asList(methodArray));
        methods.removeAll(overridden);
        return methods;
    }
    
    /* Return true if a overrides b. */
    private static boolean overrides(Method a, Method b) {
        if (!a.getName().equals(b.getName()))
            return false;
        Class aclass = a.getDeclaringClass();
        Class bclass = b.getDeclaringClass();
        if (!bclass.isAssignableFrom(aclass) ||
                !b.getReturnType().isAssignableFrom(a.getReturnType()))
            return false;
        Class[] ap = a.getParameterTypes();
        Class[] bp = b.getParameterTypes();
        if (ap.length != bp.length)
            return false;
        for (int i = 0; i < ap.length; i++) {
            if (ap[i] != bp[i])
                return false;
        }
        return true;
    }
}
