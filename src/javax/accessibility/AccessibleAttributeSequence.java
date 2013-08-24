/*
 * @(#)AccessibleAttributeSequence.java	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.accessibility;
 
import javax.swing.text.AttributeSet;
 
 
/**
 * <P>The AccessibleAttributeSequence provides information about
 * a contiguous sequence of text attributes
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleContext#getAccessibleText
 * @see AccessibleTextSequence
 *
 * @version      1.5 11/17/05
 * @author       Lynn Monsanto
 */
 
/**
 * This class collects together the span of text that share the same
 * contiguous set of attributes, along with that set of attributes.  It
 * is used by implementors of the class <code>AccessibleContext</code> in
 * order to generate <code>ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED</code> events.
 *
 * @see javax.accessibility.AccessibleContext
 * @see javax.accessibility.AccessibleContext#ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED
 */
public class AccessibleAttributeSequence {
    /** The start index of the text sequence */
    public int startIndex;

    /** The end index of the text sequence */
    public int endIndex;

    /** The text attributes */
    public AttributeSet attributes;

    /**
     * Constructs an <code>AccessibleAttributeSequence</code> with the given
     * parameters.
     *
     * @param start the beginning index of the span of text
     * @param end the ending index of the span of text
     * @param attr the <code>AttributeSet</code> shared by this text span
     *
     * @since 1.6
     */
    public AccessibleAttributeSequence(int start, int end, AttributeSet attr) {
        startIndex = start;
        endIndex = end;
        attributes = attr;
    }

};

