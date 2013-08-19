/*
 * @(#)SpecialMapping.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

/**
 * This class represents exceptions to the normal unicode category
 * mapping provided by Character. It is internal only.  It represents
 * a range of characters that don't follow the category mapping.
 */
final class SpecialMapping
{
    /**
     * The first character in exception range
     */
    public char startChar;
    /**
     * The last character in the exception range
     */
    public char endChar;
    /**
     * The category for characters in the range
     */
    public int newValue;

    /**
     * Construct a mapping for a single character
     * @param ch the character
     * @param newValue the new category for the character
     */
    public SpecialMapping(char ch, int newValue)
    {
        this(ch, ch, newValue);
    }

    /**
     * Construct a mapping for a range of characters
     * @param startChar the first character in the range
     * @param endChar the last character in the range
     * @param newValue the category for the range
     */
    public SpecialMapping(char startChar, char endChar, int newValue)
    {
        this.startChar = startChar;
        this.endChar = endChar;
        this.newValue = newValue;
    }
}

