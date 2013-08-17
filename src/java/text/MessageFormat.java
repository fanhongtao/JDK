/*
 * @(#)MessageFormat.java   1.15 97/01/29
 *
 * (C) Copyright Taligent, Inc. 1996,1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996,1997 - All Rights Reserved
 *
 * Portions copyright (c) 1996-1997 Sun Microsystems, Inc. All Rights Reserved.
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

import java.util.Date;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.Utility;
/**
 * <code>MessageFormat</code> provides a means to produce concatenated
 * messages in language-neutral way. Use this to construct messages
 * displayed for end users.
 *
 * <p>
 * <code>MessageFormat</code> takes a set of objects, formats them, then
 * inserts the formatted strings into the pattern at the appropriate places.
 *
 * <p>
 * <strong>Note:</strong>
 * <code>MessageFormat</code> differs from the other <code>Format</code>
 * classes in that you create a <code>MessageFormat</code> object with one
 * of its constructors (not with a <code>getInstance</code> style factory
 * method). The factory methods aren't necessary because <code>MessageFormat</code>
 * doesn't require any complex setup for a given locale. In fact,
 * <code>MessageFormat</code> doesn't implement any locale specific behavior
 * at all. It just needs to be set up on a sentence by sentence basis.
 *
 * <p>
 * Here are some examples of usage:
 * <blockquote>
 * <pre>
 * Object[] arguments = {
 *     new Integer(7),
 *     new Date(System.currentTimeMillis()),
 *     "a disturbance in the Force"
 * };
 *
 * String result = MessageFormat.format(
 *     "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
 *     arguments);
 *
 * <output>: At 12:30 PM on Jul 3, 2053, there was a disturbance
 *           in the Force on planet 7.
 *
 * </pre>
 * </blockquote>
 * Typically, the message format will come from resources, and the
 * arguments will be dynamically set at runtime.
 *
 * <p>
 * Example 2:
 * <blockquote>
 * <pre>
 * Object[] testArgs = {new Long(3), "MyDisk"};
 *
 * MessageFormat form = new MessageFormat(
 *     "The disk \"{1}\" contains {0} file(s).");
 *
 * System.out.println(form.format(testArgs));
 *
 * // output, with different testArgs
 * <output>: The disk "MyDisk" contains 0 file(s).
 * <output>: The disk "MyDisk" contains 1 file(s).
 * <output>: The disk "MyDisk" contains 1,273 file(s).
 * </pre>
 * </blockquote>
 *
 * <p>
 * The pattern is of the form:
 * <blockquote>
 * <pre>
 * messageFormatPattern := string ( "{" messageFormatElement "}" string )*
 *
 * messageFormatElement := argument { "," elementFormat }
 *
 * elementFormat := "time" { "," datetimeStyle }
 *                | "date" { "," datetimeStyle }
 *                | "number" { "," numberStyle }
 *                | "choice" { "," choiceStyle }
 *
 * datetimeStyle := "short"
 *                  | "medium"
 *                  | "long"
 *                  | "full"
 *                  | dateFormatPattern
 *
 * numberStyle := "currency"
 *               | "percent"
 *               | "integer"
 *               | numberFormatPattern
 *
 * choiceStyle := choiceFormatPattern
 * </pre>
 * </blockquote>
 * If there is no <code>elementFormat</code>,
 * then the argument must be a string, which is substituted. If there is
 * no <code>dateTimeStyle</code> or <code>numberStyle</code>, then the
 * default format is used (for example, <code>NumberFormat.getInstance</code>,
 * <code>DateFormat.getTimeInstance</code>, or <code>DateFormat.getInstance</code>).
 *
 * <p>
 * In strings, single quotes can be used to quote the "{"
 * (curly brace) if necessary. A real single quote is represented by ''.
 * Inside a <code>messageFormatElement</code>, quotes are <strong>not</strong>
 * removed. For example, {1,number,$'#',##} will produce a number format
 * with the pound-sign quoted, with a result such as: "$#31,45".
 *
 * <p>
 * If a pattern is used, then unquoted braces in the pattern, if any, must match:
 * that is, "ab {0} de" and "ab '}' de" are ok, but "ab {0'}' de" and "ab } de" are
 * not.
 *
 * <p>
 * The argument is a number from 0 to 9, which corresponds to the
 * arguments presented in an array to be formatted.
 *
 * <p>
 * It is ok to have unused arguments in the array.
 * With missing arguments or arguments that are not of the right class for
 * the specified format, a <code>ParseException</code> is thrown.
 * First, <code>format</code> checks to see if a <code>Format</code> object has been
 * specified for the argument with the <code>setFormats</code> method.
 * If so, then <code>format</code> uses that <code>Format</code> object to format the
 * argument. Otherwise, the argument is formatted based on the object's
 * type. If the argument is a <code>Number</code>, then <code>format</code>
 * uses <code>NumberFormat.getInstance</code> to format the argument; if the
 * argument is a <code>Date</code>, then <code>format</code> uses
 * <code>DateFormat.getDateTimeInstance</code> to format the argument.
 * Otherwise, it uses the <code>toString</code> method.
 *
 * <p>
 * For more sophisticated patterns, you can use a <code>ChoiceFormat</code> to get
 * output such as:
 * <blockquote>
 * <pre>
 * MessageFormat form = new MessageFormat("The disk \"{1}\" contains {0}.");
 * double[] filelimits = {0,1,2};
 * String[] filepart = {"no files","one file","{0,number} files"};
 * ChoiceFormat fileform = new ChoiceFormat(filelimits, filepart);
 * form.setFormat(1,fileform); // NOT zero, see below
 *
 * Object[] testArgs = {new Long(12373), "MyDisk"};
 *
 * System.out.println(form.format(testArgs));
 *
 * // output, with different testArgs
 * output: The disk "MyDisk" contains no files.
 * output: The disk "MyDisk" contains one file.
 * output: The disk "MyDisk" contains 1,273 files.
 * </pre>
 * </blockquote>
 * You can either do this programmatically, as in the above example,
 * or by using a pattern (see
 * <a href="java.text.ChoiceFormat.html"><code>ChoiceFormat</code></a>
 * for more information) as in:
 * <blockquote>
 * <pre>
 * form.applyPattern(
 *    "There {0,choice,0#are no files|1#is one file|1#are {0,number,integer} files}.");
 * </pre>
 * </blockquote>
 * <p>
 * <strong>Note:</strong> As we see above, the string produced
 * by a <code>ChoiceFormat</code> in <code>MessageFormat</code> is treated specially;
 * occurances of '{' are used to indicated subformats, and cause recursion.
 * If you create both a <code>MessageFormat</code> and <code>ChoiceFormat</code>
 * programmatically (instead of using the string patterns), then be careful not to
 * produce a format that recurses on itself, which will cause an infinite loop.
 * <p>
 * <strong>Note:</strong> formats are numbered by order of
 * variable in the string.
 * This is <strong>not</strong> the same as the argument numbering!
 * For example: with "abc{2}def{3}ghi{0}...",
 * <ul>
 * <li>format0 affects the first variable {2}
 * <li>format1 affects the second variable {3}
 * <li>format2 affects the second variable {0}
 * <li>and so on.
 * </ul>
 * <p>
 * You can use <code>setLocale</code> followed by <code>applyPattern</code>
 * (and then possibly <code>setFormat</code>) to re-initialize a
 * <code>MessageFormat</code> with a different locale.
 *
 * @see          java.util.Locale
 * @see          Format
 * @see          NumberFormat
 * @see          DecimalFormat
 * @see          ChoiceFormat
 * @version      1.15 29 Jan 1997
 * @author       Mark Davis
 */

public class MessageFormat extends Format {
    /**
     * Constructs with the specified pattern.
     * @see MessageFormat#applyPattern
     */
    public MessageFormat(String pattern) {
        applyPattern(pattern);
    }

    /**
     * Constructs with the specified pattern and formats for the
     * arguments in that pattern.
     * @see MessageFormat#setPattern
     */
    public void setLocale(Locale theLocale) {
        locale = theLocale;
    }

    /**
     * Gets the locale. This locale is used for fetching default number or date
     * format information.
     */
    public Locale getLocale() {
        return locale;
    }


    /**
     * Sets the pattern. See the class description.
     */

    public void applyPattern(String newPattern) {
            StringBuffer[] segments = new StringBuffer[4];
            for (int i = 0; i < segments.length; ++i) {
                segments[i] = new StringBuffer();
            }
            int part = 0;
            int formatNumber = 0;
            boolean inQuote = false;
            int braceStack = 0;
            maxOffset = -1;
            for (int i = 0; i < newPattern.length(); ++i) {
                char ch = newPattern.charAt(i);
                if (part == 0) {
                    if (ch == '\'') {
                        if (i + 1 < newPattern.length()
                            && newPattern.charAt(i+1) == '\'') {
                            segments[part].append(ch);  // handle doubles
                            ++i;
                        } else {
                            inQuote = !inQuote;
                        }
                    } else if (ch == '{' && !inQuote) {
                        part = 1;
                    } else {
                        segments[part].append(ch);
                    }
                } else  if (inQuote) {              // just copy quotes in parts
                    segments[part].append(ch);
                    if (ch == '\'') {
                        inQuote = false;
                    }
                } else {
                    switch (ch) {
                    case ',':
                        if (part < 3)
                            part += 1;
                        else
                            segments[part].append(ch);
                        break;
                    case '{':
                        ++braceStack;
                        segments[part].append(ch);
                        break;
                    case '}':
                        if (braceStack == 0) {
                            part = 0;
                            makeFormat(i, formatNumber, segments);
                            formatNumber++;
                        } else {
                            --braceStack;
                            segments[part].append(ch);
                        }
                        break;
                    case '\'':
                        inQuote = true;
                        // fall through, so we keep quotes in other parts
                    default:
                        segments[part].append(ch);
                        break;
                    }
                }
            }
            pattern = segments[0].toString();
    }


    /**
     * Gets the pattern. See the class description.
     */

    public String toPattern() {
        // later, make this more extensible
        int lastOffset = 0;
        StringBuffer result = new StringBuffer();
        for (int i = 0; i <= maxOffset; ++i) {
            copyAndFixQuotes(pattern, lastOffset, offsets[i],result);
            lastOffset = offsets[i];
            result.append('{');
            result.append(argumentNumbers[i]);
            if (formats[i] == null) {
                // do nothing, string format
            } else if (formats[i] instanceof DecimalFormat) {
                if (formats[i].equals(NumberFormat.getInstance(locale))) {
                    result.append(",number");
                } else if (formats[i].equals(
                                             NumberFormat.getCurrencyInstance(locale))) {
                    result.append(",number,currency");
                } else if (formats[i].equals(
                                             NumberFormat.getPercentInstance(locale))) {
                    result.append(",number,percent");
                } else if (formats[i].equals(getIntegerFormat(locale))) {
                    result.append(",number,integer");
                } else {
                    result.append(",number," +
                                  ((DecimalFormat)formats[i]).toPattern());
                }
            } else if (formats[i] instanceof SimpleDateFormat) {
                if (formats[i].equals(DateFormat.getDateInstance(
                                                               DateFormat.DEFAULT,locale))) {
                    result.append(",date");
                } else if (formats[i].equals(DateFormat.getDateInstance(
                                                                      DateFormat.SHORT,locale))) {
                    result.append(",date,short");
                } else if (formats[i].equals(DateFormat.getDateInstance(
                                                                      DateFormat.DEFAULT,locale))) {
                    result.append(",date,medium");
                } else if (formats[i].equals(DateFormat.getDateInstance(
                                                                      DateFormat.LONG,locale))) {
                    result.append(",date,long");
                } else if (formats[i].equals(DateFormat.getDateInstance(
                                                                      DateFormat.FULL,locale))) {
                    result.append(",date,full");
                } else if (formats[i].equals(DateFormat.getTimeInstance(
                                                                      DateFormat.DEFAULT,locale))) {
                    result.append(",time");
                } else if (formats[i].equals(DateFormat.getTimeInstance(
                                                                      DateFormat.SHORT,locale))) {
                    result.append(",time,short");
                } else if (formats[i].equals(DateFormat.getTimeInstance(
                                                                      DateFormat.DEFAULT,locale))) {
                    result.append(",time,medium");
                } else if (formats[i].equals(DateFormat.getTimeInstance(
                                                                      DateFormat.LONG,locale))) {
                    result.append(",time,long");
                } else if (formats[i].equals(DateFormat.getTimeInstance(
                                                                      DateFormat.FULL,locale))) {
                    result.append(",time,full");
                } else {
                    result.append(",date,"
                                  + ((SimpleDateFormat)formats[i]).toPattern());
                }
            } else if (formats[i] instanceof ChoiceFormat) {
                result.append(",choice,"
                              + ((ChoiceFormat)formats[i]).toPattern());
            } else {
                //result.append(", unknown");
            }
            result.append('}');
        }
        copyAndFixQuotes(pattern, lastOffset, pattern.length(), result);
        return result.toString();
    }

    /**
     * Sets formats to use on parameters.
     * See the class description about format numbering.
     */
    public void setFormats(Format[] newFormats) {
        try {
            formats = (Format[]) newFormats.clone();
        } catch (Exception e) {
            return; // should never occur!
        }
    }

    /**
     * Sets formats individually to use on parameters.
     * See the class description about format numbering.
     */
    public void setFormat(int variable, Format newFormat) {
        formats[variable] = newFormat;
    }

    /**
     * Gets formats that were set with setFormats.
     * See the class description about format numbering.
     */
    public Format[] getFormats() {
        try {
            return (Format[]) formats.clone();
        } catch (Exception e) {
            return formats; // should never occur!
        }
    }

    /**
     * Returns pattern with formatted objects.
     * @param source an array of objects to be formatted & substituted.
     * @param result where text is appended.
     * @param ignore no useful status is returned.
     */
    public final StringBuffer format(Object[] source, StringBuffer result,
                                     FieldPosition ignore)
    {
        return format(source,result,ignore, 0);
    }

    /**
     * Convenience routine.
     * Avoids explicit creation of MessageFormat,
     * but doesn't allow future optimizations.
     */
    public static String format(String pattern, Object[] arguments) {
            MessageFormat temp = new MessageFormat(pattern);
            return temp.format(arguments);
    }

    // Overrides
    public final StringBuffer format(Object source, StringBuffer result,
                                     FieldPosition ignore)
    {
        return format((Object[])source,result,ignore, 0);
    }

    /**
     * Parses the string.
     *
     * <p>Caveats: The parse may fail in a number of circumstances.
     * For example:
     * <ul>
     * <li>If one of the arguments does not occur in the pattern.
     * <li>If the format of an argument is loses information, such as
     *     with a choice format where a large number formats to "many".
     * <li>Does not yet handle recursion (where
     *     the substituted strings contain {n} references.)
     * <li>Will not always find a match (or the correct match)
     *     if some part of the parse is ambiguous.
     *     For example, if the pattern "{1},{2}" is used with the
     *     string arguments {"a,b", "c"}, it will format as "a,b,c".
     *     When the result is parsed, it will return {"a", "b,c"}.
     * <li>If a single argument is formatted twice in the string,
     *     then the later parse wins.
     * </ul>
     */
    public Object[] parse(String source, ParsePosition status) {
        Object[] resultArray = new Object[10];
        int patternOffset = 0;
        int sourceOffset = status.index;
        ParsePosition tempStatus = new ParsePosition(0);
        for (int i = 0; i <= maxOffset; ++i) {
            // match up to format
            int len = offsets[i] - patternOffset;
            if (len == 0 || pattern.regionMatches(patternOffset,
                                                  source, sourceOffset, len)) {
                sourceOffset += len;
                patternOffset += len;
            } else {
                return null; // leave index as is to signal error
            }

            // now use format
            if (formats[i] == null) {   // string format
                // if at end, use longest possible match
                // otherwise uses first match to intervening string
                // does NOT recursively try all possibilities
                int tempLength = (i != maxOffset) ? offsets[i+1] : pattern.length();

                int next;
                if (patternOffset >= tempLength) {
                    next = source.length();
                }else{
                    next = source.indexOf( pattern.substring(patternOffset,tempLength), sourceOffset);
                }

                if (next < 0) {
                    return null; // leave index as is to signal error
                } else {
                    resultArray[argumentNumbers[i]]
                        = source.substring(sourceOffset,next);
                    sourceOffset = next;
                }
            } else {
                tempStatus.index = sourceOffset;
                resultArray[argumentNumbers[i]]
                    = formats[i].parseObject(source,tempStatus);
                if (tempStatus.index == sourceOffset) {
                    return null; // leave index as is to signal error
                }
                sourceOffset = tempStatus.index; // update
            }
        }
        int len = pattern.length() - patternOffset;
        if (len == 0 || pattern.regionMatches(patternOffset,
                                              source, sourceOffset, len)) {
            status.index = sourceOffset + len;
        } else {
            return null; // leave index as is to signal error
        }
        return resultArray;
    }

    /**
     * Parses the string. Does not yet handle recursion (where
     * the substituted strings contain {n} references.)
     * @exception ParseException if the string can't be parsed.
     */
    public Object[] parse(String source) throws ParseException {
        ParsePosition status  = new ParsePosition(0);
        Object[] result = parse(source, status);
        if (status.index == 0)  // unchanged, returned object is null
            throw new ParseException("MessageFormat parse error!", 0);

        return result;
    }

    /**
     * Parses the string. Does not yet handle recursion (where
     * the substituted strings contain %n references.)
     */
    public Object parseObject (String text, ParsePosition status) {
        return parse(text, status);
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        MessageFormat other = (MessageFormat) super.clone();

        // clone arrays. Can't do with utility because of bug in Cloneable
        other.formats = (Format[]) formats.clone(); // shallow clone
        for (int i = 0; i < formats.length; ++i) {
            if (formats[i] != null)
                other.formats[i] = (Format)formats[i].clone();
        }
        // for primitives or immutables, shallow clone is enough
        other.offsets = (int[]) offsets.clone();
        other.argumentNumbers = (int[]) argumentNumbers.clone();

        return other;
    }

    /**
     * Equality comparision between two message format objects
     */
    public boolean equals(Object obj) {
        if (this == obj)                      // quick check
            return true;
        if (getClass() != obj.getClass())
            return false;
        MessageFormat other = (MessageFormat) obj;
        return (maxOffset == other.maxOffset
                && pattern.equals(other.pattern)
            && Utility.objectEquals(locale, other.locale)   // does null check
                && Utility.arrayEquals(offsets,other.offsets)
            && Utility.arrayEquals(argumentNumbers,other.argumentNumbers)
            && Utility.arrayEquals(formats,other.formats));
    }

    /**
     * Generates a hash code for the message format object.
     */
    public int hashCode() {
        return pattern.hashCode(); // enough for reasonable distribution
    }


    // ===========================privates============================

    // Mark : Is this the right fix?  (HS)
    private Locale locale = Locale.getDefault();
    private String pattern = "";
    // later, allow more than ten items
    private Format[] formats = new Format[10];
    private int[] offsets = new int[10];
    private int[] argumentNumbers = new int[10];
    private int maxOffset = -1;

    /**
     * Constructs with the specified pattern.
     * @see MessageFormat#applyPattern
     */
    private MessageFormat(String pattern, Locale loc) {
        locale = (Locale)loc.clone();
        applyPattern(pattern);
    }

    /**
     * Internal routine used by format.
     * @param recursionProtection Initially zero. Bits 0..9 are used to indicate
     * that a parameter has already been seen, to avoid recursion.  Currently
     * unused.
     */

    private StringBuffer format(Object[] arguments, StringBuffer result,
                                FieldPosition status, int recursionProtection) {
        // note: this implementation assumes a fast substring & index.
        // if this is not true, would be better to append chars one by one.
        int lastOffset = 0;
        for (int i = 0; i <= maxOffset; ++i) {
            result.append(pattern.substring(lastOffset, offsets[i]));
            lastOffset = offsets[i];
            int argumentNumber = argumentNumbers[i];
            if (argumentNumber >= arguments.length)
                throw new IllegalArgumentException("Argument # > Arg length");
            // int argRecursion = ((recursionProtection >> (argumentNumber*2)) & 0x3);
            if (false) { // if (argRecursion == 3){
                // prevent loop!!!
                result.append('\uFFFD');
            } else {
                Object obj = arguments[argumentNumber];
                String arg;
                boolean tryRecursion = false;
                if (formats[i] != null) {
                    arg = formats[i].format(obj);
                    tryRecursion = formats[i] instanceof ChoiceFormat;
                } else if (obj instanceof Number) {
                    // format number if can
                    arg = NumberFormat.getInstance(locale).format(obj); // fix
                } else if (obj instanceof Date) {
                    // format a Date if can
                    arg = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                                       DateFormat.SHORT,
                                                       locale).format(obj);//fix
                } else if (obj instanceof String) {
                    arg = (String) obj;

                } else {
                    arg = obj.toString();
                    if (arg == null) arg = "null";
                }

                // recurse if necessary
                if (tryRecursion && arg.indexOf('{') >= 0) {
                    MessageFormat temp = new MessageFormat(arg, locale);
                    temp.format(arguments,result,status,recursionProtection);
                } else {
                    result.append(arg);
                }
            }
        }
        result.append(pattern.substring(lastOffset, pattern.length()));
        return result;
    }
    private static final String[] typeList =
    {"", "", "number", "", "date", "", "time", "", "choice"};
    private static final String[] modifierList =
    {"", "", "currency", "", "percent", "", "integer"};
    private static final String[] dateModifierList =
    {"", "", "short", "", "medium", "", "long", "", "full"};

    private void makeFormat(int position, int offsetNumber,
                            StringBuffer[] segments)
    {

        // get the number
        int argumentNumber;
        try {
            argumentNumber = Integer.parseInt(segments[1].toString()); // always unlocalized!
            if (argumentNumber < 0 || argumentNumber > 9) {
                throw new NumberFormatException();
            }
            maxOffset = offsetNumber;
            offsets[offsetNumber] = segments[0].length();
            argumentNumbers[offsetNumber] = argumentNumber;
        } catch (Exception e) {
            throw new IllegalArgumentException("argument number too large at ");
        }

        // now get the format
        Format newFormat = null;
        switch (findKeyword(segments[2].toString(), typeList)) {
        case 0:
            // string format
            /*if (!segments[3].equals(""))
              throw new IllegalArgumentException("can't modify string format, at ");
              //*/
        break;
        case 1: case 2:// number
            switch (findKeyword(segments[3].toString(), modifierList)) {
            case 0: // default;
                newFormat = NumberFormat.getInstance(locale);
                break;
            case 1: case 2:// currency
                newFormat = NumberFormat.getCurrencyInstance(locale);
                break;
            case 3: case 4:// percent
                newFormat = NumberFormat.getPercentInstance(locale);
                break;
            case 5: case 6:// integer
                newFormat = getIntegerFormat(locale);
                break;
            default: // pattern
                newFormat = NumberFormat.getInstance(locale);
                try {
                    ((DecimalFormat)newFormat).applyPattern(segments[3].toString());
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                                             "Pattern incorrect or locale does not support formats, error at ");
                }
                break;
            }
            break;
        case 3: case 4: // date
            switch (findKeyword(segments[3].toString(), dateModifierList)) {
            case 0: // default
                newFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                break;
            case 1: case 2: // short
                newFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
                break;
            case 3: case 4: // medium
                newFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                break;
            case 5: case 6: // long
                newFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
                break;
            case 7: case 8: // full
                newFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);
                break;
            default:
                newFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                try {
                    ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                                             "Pattern incorrect or locale does not support formats, error at ");
                }
                break;
            }
            break;
        case 5: case 6:// time
            switch (findKeyword(segments[3].toString(), dateModifierList)) {
            case 0: // default
                newFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                break;
            case 1: case 2: // short
                newFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
                break;
            case 3: case 4: // medium
                newFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                break;
            case 5: case 6: // long
                newFormat = DateFormat.getTimeInstance(DateFormat.LONG, locale);
                break;
            case 7: case 8: // full
                newFormat = DateFormat.getTimeInstance(DateFormat.FULL, locale);
                break;
            default:
                newFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                try {
                    ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                                             "Pattern incorrect or locale does not support formats, error at ");
                }
                break;
            }
            break;
        case 7: case 8:// choice
            try {
                newFormat = new ChoiceFormat(segments[3].toString());
            } catch (Exception e) {
                throw new IllegalArgumentException(
                                         "Choice Pattern incorrect, error at ");
            }
            break;
        default:
            throw new IllegalArgumentException("unknown format type at ");
        }
        formats[offsetNumber] = newFormat;
        segments[1].setLength(0);   // throw away other segments
        segments[2].setLength(0);
        segments[3].setLength(0);
    }

    private static final int findKeyword(String s, String[] list) {
        s = s.trim().toLowerCase();
        for (int i = 0; i < list.length; ++i) {
            if (s.equals(list[i]))
                return i;
        }
        return -1;
    }

    /**
     * Convenience method that ought to be in NumberFormat
     */
    NumberFormat getIntegerFormat(Locale locale) {
        NumberFormat temp = NumberFormat.getInstance(locale);
        if (temp instanceof DecimalFormat) {
            DecimalFormat temp2 = (DecimalFormat) temp;
            temp2.setMaximumFractionDigits(0);
            temp2.setDecimalSeparatorAlwaysShown(false);
            temp2.setParseIntegerOnly(true);
        }
        return temp;
    }

    private static final void copyAndFixQuotes(
                                               String source, int start, int end, StringBuffer target) {
        for (int i = start; i < end; ++i) {
            char ch = source.charAt(i);
            if (ch == '{') {
                target.append("'{'");
            } else if (ch == '\'') {
                target.append("''");
            } else {
                target.append(ch);
            }
        }
    }

}
