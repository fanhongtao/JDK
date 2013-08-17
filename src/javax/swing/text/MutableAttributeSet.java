/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Enumeration;

/**
 * A generic interface for a mutable collection of unique attributes.
 *
 * Implementations will probably want to provide a constructor of the
 * form:<tt>
 * public XXXAttributeSet(ConstAttributeSet source);</tt>
 *
 * @version 1.13 02/06/02
 */
public interface MutableAttributeSet extends AttributeSet {

    /**
     * Creates a new attribute set similar to this one except that it contains
     * an attribute with the given name and value.  The object must be
     * immutable, or not mutated by any client.
     *
     * @param name the name
     * @param value the value
     */
    public void addAttribute(Object name, Object value);

    /**
     * Creates a new attribute set similar to this one except that it contains
     * the given attributes and values.
     *
     * @param attributes the set of attributes
     */
    public void addAttributes(AttributeSet attributes);

    /**
     * Creates a new attribute set similar to this one except that it contains
     * no attribute with the given name.
     *
     * @param name the attribute name
     */
    public void removeAttribute(Object name);

    /**
     * Creates a new attribute set similar to this one except that it contains
     * no attribute with any of the given names.
     *
     * @param names the set of names
     */
    public void removeAttributes(Enumeration names);

    /**
     * Creates a new attribute set similar to this one except that it contains
     * no attribute with any of the given names and values.  Existing
     * attributes with the same name and different value will remain.
     *
     * @param attributes the set of attributes
     */
    public void removeAttributes(AttributeSet attributes);

    /**
     * Sets the resolving parent.  This is the set
     * of attributes to resolve through if an attribute
     * isn't defined locally.
     *
     * @param parent the parent
     */
    public void setResolveParent(AttributeSet parent);

}











