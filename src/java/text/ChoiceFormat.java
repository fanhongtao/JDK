/*
 * @(#)ChoiceFormat.java	1.11 97/02/06
 *
 * (C) Copyright Taligent, Inc. 1996-1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1997 - All Rights Reserved
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
import java.text.Utility;

/**
 * A <code>ChoiceFormat</code> allows you to attach a format to a range of numbers.
 * It is generally used in a <code>MessageFormat</code> for handling plurals.
 * The choice is specified with an ascending list of doubles, where each item
 * specifies a half-open interval up to the next item:
 * <blockquote>
 * <pre>
 * X matches j if and only if limit[j] <= X < limit[j+1]
 * </pre>
 * </blockquote>
 * If there is no match, then either the first or last index is used, depending
 * on whether the number (X) is too low or too high.
 *
 * <p>
 * <strong>Note:</strong>
 * <code>ChoiceFormat</code> differs from the other <code>Format</code>
 * classes in that you create a <code>ChoiceFormat</code> object with a
 * constructor (not with a <code>getInstance</code> style factory
 * method). The factory methods aren't necessary because <code>ChoiceFormat</code>
 * doesn't require any complex setup for a given locale. In fact,
 * <code>ChoiceFormat</code> doesn't implement any locale specific behavior.
 *
 * <p>
 * When creating a <code>ChoiceFormat</code>, you must specify an array of formats
 * and an array of limits. The length of these arrays must be the same.
 * For example,
 * <ul>
 * <li>
 *     <em>limits</em> = {1,2,3,4,5,6,7}<br>
 *     <em>formats</em> = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"}
 * <lie>
 *     <em>limits</em> = {0, 1, ChoiceFormat.nextDouble(1)}<br>
 *     <em>formats</em> = {"no files", "one file", "many files"}<br>
 *     (<code>nextDouble</code> can be used to get the next higher double, to
 *     make the half-open interval.)
 * </ul>
 *
 * <p>
 * Here is a simple example that shows formatting and parsing:
 * <blockquote>
 * <pre>
 * double[] limits = {1,2,3,4,5,6,7};
 * String[] monthNames = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
 * ChoiceFormat form = new ChoiceFormat(limits, monthNames);
 * ParsePosition status = new ParsePosition(0);
 * for (double i = 0.0; i <= 8.0; ++i) {
 *     status.setIndex(0);
 *     System.out.println(i + " -> " + form.format(i) + " -> "
 *                              + form.parse(form.format(i),status));
 * }
 * </pre>
 * </blockquote>
 * Here is a more complex example, with a pattern format:
 * <blockquote>
 * <pre>
 * double[] filelimits = {0,1,2};
 * String[] filepart = {"are no files","is one file","are {2} files"};
 * ChoiceFormat fileform = new ChoiceFormat(filelimits, filepart);
 * Format[] testFormats = {fileform, null, NumberFormat.getInstance()};
 * MessageFormat pattform = new MessageFormat("There {0} on {1}");
 * pattform.setFormats(testFormats);
 * Object[] testArgs = {null, "ADisk", null};
 * for (int i = 0; i < 4; ++i) {
 *     testArgs[0] = new Integer(i);
 *     testArgs[2] = testArgs[0];
 *     System.out.println(pattform.format(testArgs));
 * }
 * </pre>
 * </blockquote>
 * @see          DecimalFormat
 * @see          MessageFormat
 * @version      1.11 02/06/97
 * @author       Mark Davis
 */
public class ChoiceFormat extends NumberFormat {
    /**
     * Sets the pattern.
     * @param newPattern See the class description.
     */
    public void applyPattern(String newPattern) {
            StringBuffer[] segments = new StringBuffer[2];
            for (int i = 0; i < segments.length; ++i) {
                segments[i] = new StringBuffer();       // later, use single
            }
            double[] newChoiceLimits = new double[30];	// current limit
            String[] newChoiceFormats = new String[30];   // later, use Vectors
            int count = 0;
            int part = 0;
            double startValue = 0;
            double oldStartValue = Double.NaN;
            boolean inQuote = false;
            for (int i = 0; i < newPattern.length(); ++i) {
                char ch = newPattern.charAt(i);
                if (ch == '<' || ch == '#' || ch == '\u2264') {
                    if (segments[0].equals("")) {
                        throw new IllegalArgumentException();
                    }
                    try {
                        startValue = Double.valueOf(segments[0].toString()).doubleValue();
                    } catch (Exception e) {
                        throw new IllegalArgumentException();
                    }
                    if (ch == '<') {
                        startValue = nextDouble(startValue);
                    }
                    if (startValue <= oldStartValue) {
                        throw new IllegalArgumentException();
                    }
                    segments[0].setLength(0);
                    part = 1;
                } else if (ch == '|') {
				//System.out.println("***" + startValue + "," + segments[1].toString());
                    newChoiceLimits[count] = startValue;
                    newChoiceFormats[count] = segments[1].toString();
                    ++count;
                    oldStartValue = startValue;
                    segments[1].setLength(0);
                    part = 0;
                } else {
                    segments[part].append(ch);
                }
            }
            // clean up last one
            if (part == 1) {
                newChoiceLimits[count] = startValue;
     		newChoiceFormats[count] = segments[1].toString();
                //System.out.println("***" + newChoiceLimits[count] + "," + newChoiceFormats[count]);
                ++count;
            }
            // compact arrays
            //System.out.println("***" + count);
            choiceLimits = new double[count];
            System.arraycopy(newChoiceLimits, 0, choiceLimits, 0, count);
            choiceFormats = new String[count];
            System.arraycopy(newChoiceFormats, 0, choiceFormats, 0, count);
            //for (int i = 0; i < choiceLimits.length; ++i) {
            //System.out.println("&&<" + choiceLimits[i]);
            //System.out.println("&&>" + choiceFormats[i]);
            //}
    }
    /**
     * Gets the pattern.
     */

    public String toPattern() {
        StringBuffer result = new StringBuffer();
        //System.out.println("&&&" + choiceLimits.length);
        for (int i = 0; i < choiceLimits.length; ++i) {
            //System.out.println("&&<" + choiceLimits[i] + ";"
            //	+ Long.toString(Double.doubleToLongBits(choiceLimits[i]),16)
            //	+ ";" + choiceFormats[i]);
            if (i != 0) {
                result.append('|');
            }
            // choose based upon which has less precision
            // approximate that by choosing the closest one to an integer.
            // could do better, but it's not worth it.
            double less = previousDouble(choiceLimits[i]);
            double tryLessOrEqual = Math.abs(Math.IEEEremainder(choiceLimits[i], 1.0d));
            double tryLess = Math.abs(Math.IEEEremainder(less, 1.0d));
            if (tryLessOrEqual < tryLess) {
                result.append(""+choiceLimits[i]);
                result.append('#');
            } else {
                result.append(""+less);
                result.append('<');
            }
            result.append(choiceFormats[i].toString());
        }
        return result.toString();
    }

    /**
     * Constructs with limits and corresponding formats based on the pattern.
     */
    public ChoiceFormat(String newPattern)  {
        applyPattern(newPattern);
    }
    /**
     * Constructs with the limits and the corresponding formats.
     * @see #setChoices
     */
    public ChoiceFormat(double[] limits, String[] formats) {
        setChoices(limits, formats);
    }
    /**
     * Set the choices to be used in formatting.
     * @param limits contains the top value that you want
     * parsed with that format,and should be in ascending sorted order. When
     * formatting X, the choice will be the i, where limit[i] <= X < limit[i+1].
     * @param formats are the formats you want to use for each limit.
     * They can be either Format objects or Strings.
     * When formatting with object Y,
     * if the object is a NumberFormat, then ((NumberFormat) Y).format(X)
     * is called. Otherwise Y.toString() is called.
     */
    public void setChoices(double[] limits, String formats[]) {
        choiceLimits = limits;
        choiceFormats = formats;
    }
    /**
     * Get the limits passed in the constructor.
     * @return the limits.
     */
    public double[] getLimits() {
        return choiceLimits;
    }
    /**
     * Get the formats passed in the constructor.
     * @return the formats.
     */
    public Object[] getFormats() {
        return choiceFormats;
    }

    // Overrides

    /**
     * Specialization of format. This method really calls
     * <code>format(double, StringBuffer, FieldPosition)</code>
     * thus the range of longs that are supported is only equal to
     * the range that can be stored by double. This will never be
     * a practical limitation.
     */
    public StringBuffer format(long number, StringBuffer toAppendTo,
                               FieldPosition status) {
        return format((double)number, toAppendTo, status);
    }

    public StringBuffer format(double number, StringBuffer toAppendTo,
                               FieldPosition status) {
        // find the number
        int i;
        for (i = 0; i < choiceLimits.length; ++i) {
            if (!(number >= choiceLimits[i])) {
                // same as number < choiceLimits, except catchs NaN
                break;
            }
        }
        --i;
        if (i < 0) i = 0;
        // return either a formatted number, or a string
        return toAppendTo.append(choiceFormats[i]);
    }

    public Number parse(String text, ParsePosition status) {
        // find the best number (defined as the one with the longest parse)
        int start = status.index;
        int furthest = start;
        double bestNumber = Double.NaN;
        double tempNumber = 0.0;
        for (int i = 0; i < choiceFormats.length; ++i) {
            String tempString = choiceFormats[i];
            if (text.regionMatches(start, tempString, 0, tempString.length())) {
                status.index = tempString.length();
                tempNumber = choiceLimits[i];
                if (status.index > furthest) {
                    furthest = status.index;
                    bestNumber = tempNumber;
                    if (furthest == text.length()) break;
                }
            }
        }
        status.index = furthest;
        return new Double(bestNumber);
    }

    /**
     * Finds the least double greater than d.
     * If NaN, returns same value.
     * <p>Used to make half-open intervals.
     * @see #previousDouble
     */
    public static final double nextDouble (double d) {
        return nextDouble(d,true);
    }

    /**
     * Finds the greatest double less than d.
     * If NaN, returns same value.
     * @see #nextDouble
     */
    public static final double previousDouble (double d) {
        return nextDouble(d,false);
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        ChoiceFormat other = (ChoiceFormat) super.clone();
        // for primitives or immutables, shallow clone is enough
        other.choiceLimits = (double[]) choiceLimits.clone();
        other.choiceFormats = (String[]) choiceFormats.clone();
        return other;
    }

    /**
     * Generates a hash code for the message format object.
     */
    public int hashCode() {
        int result = choiceLimits.length;
        if (choiceFormats.length > 0) {
            // enough for reasonable distribution
            result ^= choiceFormats[choiceFormats.length-1].hashCode();
        }
        return result;
    }

    /**
     * Equality comparision between two
     */
    public boolean equals(Object obj) {
        if (this == obj)                      // quick check
            return true;
        if (getClass() != obj.getClass())
            return false;
        ChoiceFormat other = (ChoiceFormat) obj;
        return (Utility.arrayEquals(choiceLimits,other.choiceLimits)
        	&& Utility.arrayEquals(choiceFormats,other.choiceFormats));
    }
    // ===============privates===========================
    private double[] choiceLimits;
    private String[] choiceFormats;

    /*
    static final long SIGN          = 0x8000000000000000L;
    static final long EXPONENT      = 0x7FF0000000000000L;
    static final long SIGNIFICAND   = 0x000FFFFFFFFFFFFFL;

    private static double nextDouble (double d, boolean positive) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
                return d;
            }
        long bits = Double.doubleToLongBits(d);
        long significand = bits & SIGNIFICAND;
        if (bits < 0) {
            significand |= (SIGN | EXPONENT);
        }
        long exponent = bits & EXPONENT;
        if (positive) {
            significand += 1;
            // FIXME fix overflow & underflow
        } else {
            significand -= 1;
            // FIXME fix overflow & underflow
        }
        bits = exponent | (significand & ~EXPONENT);
        return Double.longBitsToDouble(bits);
    }
    */

    /*
     * Finds the least double greater than d (if positive == true),
     * or the greatest double less than d (if positive == false).
     * If NaN, returns same value.
     *
     * Does not affect floating-point flags,
     *  provided these member functions do not:
     *          Double.longBitsToDouble ()
     *          Double.doubleToLongBits ()
     *          Double.IsNaN ()
     */

    static final long SIGN                = 0x8000000000000000L;
    static final long EXPONENT            = 0x7FF0000000000000L;
    static final long POSITIVEINFINITY    = 0x7FF0000000000000L;

    public static double nextDouble (double d, boolean positive) {

        /* filter out NaN's */
        if (Double.isNaN(d)) {
            return d;
        }

        /* zero's are also a special case */
        if (d == 0.0) {
            double smallestPositiveDouble = Double.longBitsToDouble(1L);
            if (positive) {
                return smallestPositiveDouble;
            } else {
                return -smallestPositiveDouble;
            }
        }

        /* if entering here, d is a nonzero value */

        /* hold all bits in a long for later use */
        long bits = Double.doubleToLongBits(d);

        /* strip off the sign bit */
        long magnitude = bits & ~SIGN;

        /* if next double away from zero, increase magnitude */
        if ((bits > 0) == positive) {
            if (magnitude != POSITIVEINFINITY) {
                magnitude += 1;
            }
        }
        /* else decrease magnitude */
        else {
            magnitude -= 1;
        }

        /* restore sign bit and return */
        long signbit = bits & SIGN;
        return Double.longBitsToDouble (magnitude | signbit);
    }

}

