/*
 * @(#)AccessibleTextSequence.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.accessibility;
 
 
/**
 * <P>The AccessibleTextSequence provides information about
 * a contiguous sequence of text.
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleContext#getAccessibleText
 * @see AccessibleAttributeSequence
 *
 * @version      1.2 12/19/03
 * @author       Lynn Monsanto
 */
 
/**
 * Information about a contiguous sequence of text.
 */
public class AccessibleTextSequence {
    public int startIndex;  // the start index of the text sequence
    public int endIndex;    // the end index of the text sequence
    public String text;     // the text
};

