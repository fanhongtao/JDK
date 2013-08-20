/*
 * @(#)CircularIdentityList.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.util.*;

/**
 * An circular linked list like data structure that uses identity for equality
 * testing.
 *
 * @version 1.5, 12/19/03
 * @author Scott Violet
 */
class CircularIdentityList implements Cloneable {
    private Property property;

    /**
     * Sets a particular value.
     */
    public synchronized void set(Object key, Object value) {
        if (property == null) {
            property = new Property(key, value, null);
        }
        else {
            Property p = property;
            Property last = p;

            do {
                if (p.key == key) {
                    p.value = value;
                    property = p;
                    return;
                }
                last = p;
                p = p.next;
            } while (p != property && p != null);
            // not defined
            if (value != null) {
                if (p == null) {
                    // Only one element
                    p = property;
                }
                property = new Property(key, value, p);
                last.next = property;
            }
        }
    }

    /**
     * Returns the value currently being referenced.
     */
    public synchronized Object get() {
        if (property == null) {
            return null;
        }
        return property.value;
    }

    /**
     * Returns the value for a specific key.
     */
    public synchronized Object get(Object key) {
        if (property == null) {
            return null;
        }
        Property p = property;

        do {
            if (p.key == key) {
                return p.value;
            }
            p = p.next;
        } while (p != property && p != null);
        return null;
    }

    /**
     * Advanced the list returning the next key. This will only return
     * null if the list is empty.
     */
    public synchronized Object next() {
        if (property == null) {
            return null;
        }
        if (property.next == null) {
            return property.key;
        }
        property = property.next;
        return property.key;
    }

    public synchronized Object clone() {
        try {
            CircularIdentityList list = (CircularIdentityList)super.clone();

            if (property != null) {
                list.property = (Property)property.clone();

                Property last = list.property;

                while (last.next != null && last.next != property) {
                    last.next = (Property)last.next.clone();
                    last = last.next;
                }
                last.next = list.property;
            }
            return list;
        } catch (CloneNotSupportedException cnse) {
        }
        return null;
    }


    static class Property implements Cloneable {
        Object key;
        Object value;
        Property next;

        Property(Object key, Object value, Property next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException cnse) {
            }
            return null;
        }
    }
}
