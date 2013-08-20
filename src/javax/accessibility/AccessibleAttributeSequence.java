/*
 * @(#)AccessibleAttributeSequence.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
 * @version      1.2 12/19/03
 * @author       Lynn Monsanto
 */
 
/**
 * Information about a contiguous sequence of text attributes
 */
public class AccessibleAttributeSequence {
    public int startIndex;  // the start index of the text attribute sequence
    public int endIndex;    // the end index of the text attribute sequence
    public AttributeSet attributes;  // the text attributes
};

