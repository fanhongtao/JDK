/*
 * @(#)Format.java	1.15 97/02/12
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.text;
import java.io.Serializable;

/**
 * <code>Format</code> is an abstract base class for formatting locale-sensitive
 * information such as dates, messages, and numbers.
 *
 * <p>
 * <code>Format</code> defines the programming interface for formatting
 * locale-sensitive objects into <code>String</code>s (the
 * <code>format</code> method) and for parsing <code>String</code>s back
 * into objects (the <code>parseObject</code> method). Any <code>String</code>
 * formatted by <code>format</code> is guaranteed to be parseable by
 * <code>parseObject</code>.
 *
 * <p>
 * If formatting is unsuccessful because the <code>Format</code> object
 * cannot format the type of object specified, <code>format</code> throws an
 * <code>IllegalArgumentException</code>. Otherwise, if there is something
 * illformed about the object, <code>format</code> returns the Unicode
 * replacement character <code>\\uFFFD</code>.
 *
 * <p>
 * If there is no match when parsing,
 * <code>parseObject(String)</code> throws a <code>ParseException</code>,
 * and <code>parseObject(String, ParsePosition)</code> leaves the
 * <code>ParsePosition</code> <code>index</code> member unchanged and
 * returns <code>null</code>.
 *
 * <p>
 * <STRONG>Subclassing:</STRONG>
 * The JDK provides three concrete subclasses of <code>Format</code>--
 * <code>DateFormat</code>, <code>MessageFormat</code>, and
 * <code>NumberFormat</code>--for formatting dates, messages, and numbers,
 * respectively.
 * <p>
 * Concrete subclasses <em>must</em> implement these two methods:
 * <ol>
 * <li> <code>format(Object obj, StringBuffer toAppendTo, FieldPosition pos)</code>
 * <li> <code>parseObject (String source, ParsePosition pos)</code>
 * </ol>
 *
 * <p>
 * Most subclasses will also implement the following two methods:
 * <ol>
 * <li>
 * <code>getInstance</code> for getting a useful format object appropriate
 * for the current locale
 * <li>
 * <code>getInstance(Locale)</code> for getting a useful format
 * object appropriate for the specified locale
 * </ol>
 * In addition, some subclasses may also choose to implement other
 * <code>getXxxxInstance</code> methods for more specialized control. For
 * example, the <code>NumberFormat</code> class provides
 * <code>getPercentInstance</code> and <code>getCurrencyInstance</code>
 * methods for getting specialized number formatters.
 *
 * <p>
 * Subclasses of <code>Format</code> that allow programmers to create objects
 * for locales (with <code>getInstance(Locale)</code> for example)
 * must also implement the following class method:
 * <blockquote>
 * <pre>
 * public static Locale[] getAvailableLocales()
 * </pre>
 * </blockquote>
 *
 * <p>
 * And finally subclasses may define a set of constants to identify the various
 * fields in the formatted output. These constants are used to create a FieldPosition
 * object which identifies what information is contained in the field and its
 * position in the formatted result. These constants should be named
 * <code><em>item</em>_FIELD</code> where <code><em>item</em></code> identifies
 * the field. For examples of these constants, see <code>ERA_FIELD</code> and its
 * friends in <a href="java.text.DateFormat.html"><code>DateFormat</code></a>.
 *
 * @see          java.text.ParsePosition
 * @see          java.text.FieldPosition
 * @see          java.text.NumberFormat
 * @see          java.text.DateFormat
 * @see          java.text.MessageFormat
 * @version      1.15 02/12/97
 * @author       Mark Davis
 */
public abstract class Format implements Serializable, Cloneable {
    /**
     * Formats an object to produce a string.
     * <p>Subclasses will override the StringBuffer version of format.
     * @param obj    The object to format
     * @return       Formatted string.
     * @exception IllegalArgumentException when the Format cannot format the
     * type of object.
     * @see          MessageFormat
     * @see java.text.Format#format
     */
    public final String format (Object obj) {
        return format(obj, new StringBuffer(), new FieldPosition(0)).toString();
    }

    /**
     * Formats an object to produce a string.
     * Subclasses will implement for particular object, such as:
     * <pre>
     * StringBuffer format (Number obj, StringBuffer toAppendTo)
     * Number parse (String str)
     * </pre>
     * These general routines allow polymorphic parsing and
     * formatting for objects such as the MessageFormat.
     * @param obj    The object to format
     * @param toAppendTo    where the text is to be appended
     * @param status    On input: an alignment field, if desired.
     * On output: the offsets of the alignment field.
     * @return       the value passed in as toAppendTo (this allows chaining,
     * as with StringBuffer.append())
     * @exception IllegalArgumentException when the Format cannot format the
     * given object.
     * @see  MessageFormat
     * @see java.text.FieldPosition
     */
    public abstract StringBuffer format(Object obj,
					StringBuffer toAppendTo,
					FieldPosition pos);

    /**
     * Parses a string to produce an object.
     * Subclasses will typically implement for particular object, such as:
     * <pre>
     *       String format (Number obj);
     *       String format (long obj);
     *       String format (double obj);
     *       Number parse (String str);
     * </pre>
     * @param ParsePosition Input-Output parameter.
     * <p>Before calling, set status.index to the offset you want to start
     * parsing at in the source.
     * After calling, status.index is the end of the text you parsed.
     * If error occurs, index is unchanged.
     * <p>When parsing, leading whitespace is discarded
     * (with successful parse),
     * while trailing whitespace is left as is.
     * <p>Example:
     * Parsing "_12_xy" (where _ represents a space) for a number,
     * with index == 0 will result in
     * the number 12, with status.index updated to 3
     * (just before the second space).
     * Parsing a second time will result in a ParseException
     * since "xy" is not a number, and leave index at 3.
     * <p>Subclasses will typically supply specific parse methods that
     * return different types of values. Since methods can't overload on
     * return types, these will typically be named "parse", while this
     * polymorphic method will always be called parseObject.
     * Any parse method that does not take a status should
     * throw ParseException when no text in the required format is at
     * the start position.
     * @return Object parsed from string. In case of error, returns null.
     * @see java.text.ParsePosition
     */
    public abstract Object parseObject (String source, ParsePosition status);

    /**
     * Parses a string to produce an object.
     *
     * @exception ParseException if the specified string is invalid.
     */
    public Object parseObject(String source) throws ParseException {
        ParsePosition status = new ParsePosition(0);
        Object result = parseObject(source, status);
        if (status.index == 0) {
            throw new ParseException("Format.parseObject(String) failed", 0);
        }
        return result;
    }

    public Object clone() {
        try {
            Format other = (Format) super.clone();
            return other;
        } catch (CloneNotSupportedException e) {
            // will never happen
            return null;
        }
    }
}
