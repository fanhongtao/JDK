/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

/**
 * <code>FieldPosition</code> is a simple class used by <code>Format</code>
 * and its subclasses to identify fields in formatted output. Fields are
 * identified by constants, whose names typically end with <code>_FIELD</code>,
 * defined in the various subclasses of <code>Format</code>. See
 * <code>ERA_FIELD</code> and its friends in <code>DateFormat</code> for
 * an example.
 *
 * <p>
 * <code>FieldPosition</code> keeps track of the position of the
 * field within the formatted output with two indices: the index
 * of the first character of the field and the index of the last
 * character of the field.
 *
 * <p>
 * One version of the <code>format</code> method in the various
 * <code>Format</code> classes requires a <code>FieldPosition</code>
 * object as an argument. You use this <code>format</code> method
 * to perform partial formatting or to get information about the
 * formatted output (such as the position of a field).
 *
 * @version     1.17 02/06/02
 * @author      Mark Davis
 * @see         java.text.Format
 */
public class FieldPosition {

    /**
     * Input: Desired field to determine start and end offsets for.
     * The meaning depends on the subclass of Format.
     */
    int field = 0;

    /**
     * Output: End offset of field in text.
     * If the field does not occur in the text, 0 is returned.
     */
    int endIndex = 0;

    /**
     * Output: Start offset of field in text.
     * If the field does not occur in the text, 0 is returned.
     */
    int beginIndex = 0;

    /**
     * Creates a FieldPosition object for the given field.  Fields are
     * identified by constants, whose names typically end with _FIELD,
     * in the various subclasses of Format.
     *
     * @see java.text.NumberFormat#INTEGER_FIELD
     * @see java.text.NumberFormat#FRACTION_FIELD
     * @see java.text.DateFormat#YEAR_FIELD
     * @see java.text.DateFormat#MONTH_FIELD
     */
    public FieldPosition(int field) {
        this.field = field;
    }

    /**
     * Retrieves the field identifier.
     */
    public int getField() {
        return field;
    }

    /**
     * Retrieves the index of the first character in the requested field.
     */
    public int getBeginIndex() {
        return beginIndex;
    }

    /**
     * Retrieves the index of the character following the last character in the
     * requested field.
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Sets the begin index.  For use by subclasses of Format.
     */
    public void setBeginIndex(int bi) {
        beginIndex = bi;
    }

    /**
     * Sets the end index.  For use by subclasses of Format.
     */
    public void setEndIndex(int ei) {
        endIndex = ei;
    }
    /**
     * Overrides equals
     */
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof FieldPosition))
            return false;
        FieldPosition other = (FieldPosition) obj;
        return (beginIndex == other.beginIndex
            && endIndex == other.endIndex
            && field == other.field);
    }

    /**
     * Returns a hash code for this FieldPosition.
     * @return a hash code value for this object
     */
    public int hashCode() {
        return (field << 24) | (beginIndex << 16) | endIndex;
    }

    /**
     * Return a string representation of this FieldPosition.
     * @return  a string representation of this object
     */
    public String toString() {
        return getClass().getName() +
            "[field=" + field +
            ",beginIndex=" + beginIndex +
            ",endIndex=" + endIndex + ']';
    }
}
