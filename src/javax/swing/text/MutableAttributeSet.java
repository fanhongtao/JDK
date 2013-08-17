/*
 * @(#)MutableAttributeSet.java	1.10 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @version 1.10 08/26/98
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











