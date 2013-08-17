/*
 * @(#)Character.java	1.46 99/01/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * The Character class wraps a value of the primitive type <code>char</code> 
 * in an object. An object of type <code>Character</code> contains a 
 * single field whose type is <code>char</code>. 
 * <p>
 * In addition, this class provides several methods for determining 
 * the type of a character and converting characters from uppercase 
 * to lowercase and vice versa. 
 * <p>
 * Many of the methods of class <code>Character</code> are defined 
 * in terms of a "Unicode attribute table" that specifies 
 * a name for every defined Unicode code point. The table also 
 * includes other attributes, such as a decimal value, an uppercase 
 * equivalent, a lowercase equivalent, and/or a titlecase equivalent. 
 * The Unicode attribute table is available on the World Wide Web as 
 * the file:
 * <ul><code>
 *   ftp://unicode.org/pub/MappingTables/UnicodeData1.1.5.txt
 * </code></ul>
 * <p>
 * For a more detailed specification of the <code>Character</code> 
 * class, one that encompasses the exact behavior of methods such as 
 * <code>isDigit</code>, <code>isLetter</code>, 
 * <code>isLowerCase</code>, and <code>isUpperCase</code> over the 
 * full range of Unicode values, see Gosling, Joy, and Steele, <i>The 
 * Java Language Specification</i>. 
 *
 * @author  Lee Boynton
 * @author  Guy Steele
 * @author  Akira Tanaka
 * @version 1.46 01/22/99
 * @since   JDK1.0
 */
public final
class Character extends Object implements java.io.Serializable {
    /**
     * The minimum radix available for conversion to and from Strings.  
     * The constant value of this field is the smallest value permitted 
     * for the radix argument in radix-conversion methods such as the 
     * <code>digit</code> method, the <code>forDigit</code>
     * method, and the <code>toString</code> method of class 
     * <code>Integer</code>. 
     *
     * @see     java.lang.Character#digit(char, int)
     * @see     java.lang.Character#forDigit(int, int)
     * @see     java.lang.Integer#toString(int, int)
     * @see     java.lang.Integer#valueOf(java.lang.String)
     * @since   JDK1.0
     */
    public static final int MIN_RADIX = 2;

    /**
     * The maximum radix available for conversion to and from Strings.
     * The constant value of this field is the largest value permitted 
     * for the radix argument in radix-conversion methods such as the 
     * <code>digit</code> method, the <code>forDigit</code>
     * method, and the <code>toString</code> method of class 
     * <code>Integer</code>. 
     *
     * @see     java.lang.Character#digit(char, int)
     * @see     java.lang.Character#forDigit(int, int)
     * @see     java.lang.Integer#toString(int, int)
     * @see     java.lang.Integer#valueOf(java.lang.String)
     * @since   JDK1.0
     */
    public static final int MAX_RADIX = 36;

    /**
     * The constant value of this field is the smallest value of type 
     * <code>char</code>.
     *
     * @since   JDK1.0.2
     */
    public static final char   MIN_VALUE = '\u0000';

    /**
     * The constant value of this field is the largest value of type 
     * <code>char</code>.
     *
     * @since   JDK1.0.2
     */
    public static final char   MAX_VALUE = '\uffff';
    
    /**
     * The Class object representing the primitive type char.
     *
     * @since   JDK1.1
     */
    public static final Class   TYPE = Class.getPrimitiveClass("char");
    
    /*
     * Public data for enumerated Unicode general category types
     *
     * @since   JDK1.1
     */
    public static final byte
        UNASSIGNED              = 0,
        UPPERCASE_LETTER        = 1,
        LOWERCASE_LETTER        = 2,
        TITLECASE_LETTER        = 3,
        MODIFIER_LETTER         = 4,
        OTHER_LETTER            = 5,
        NON_SPACING_MARK        = 6,
        ENCLOSING_MARK          = 7,
        COMBINING_SPACING_MARK  = 8,
        DECIMAL_DIGIT_NUMBER    = 9,
        LETTER_NUMBER           = 10,
        OTHER_NUMBER            = 11,
        SPACE_SEPARATOR         = 12,
        LINE_SEPARATOR          = 13,
        PARAGRAPH_SEPARATOR     = 14,
        CONTROL                 = 15,
        FORMAT                  = 16,
        PRIVATE_USE             = 18,
        SURROGATE               = 19,
        DASH_PUNCTUATION        = 20,
        START_PUNCTUATION       = 21,
        END_PUNCTUATION         = 22,
        CONNECTOR_PUNCTUATION   = 23,
        OTHER_PUNCTUATION       = 24,
        MATH_SYMBOL             = 25,
        CURRENCY_SYMBOL         = 26,
        MODIFIER_SYMBOL         = 27,
        OTHER_SYMBOL            = 28;

    /**
     * The value of the Character.
     */
    private char value;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 3786198910865385080L;

    /**
     * Constructs a <code>Character</code> object and initializes it so 
     * that it represents the primitive <code>value</code> argument. 
     *
     * @param  value   value for the new <code>Character</code> object.
     * @since   JDK1.0
     */
    public Character(char value) {
        this.value = value;
    }

    /**
     * Returns the value of this Character object.
     * @return  the primitive <code>char</code> value represented by
     *          this object.
     * @since   JDK1.0
     */
    public char charValue() {
        return value;
    }

    /**
     * Returns a hash code for this Character.
     * @return  a hash code value for this object. 
     * @since   JDK1.0
     */
    public int hashCode() {
        return (int)value;
    }

    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>Character</code> object that 
     * represents the same <code>char</code> value as this object. 
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof Character)) {
            return value == ((Character)obj).charValue();
        } 
        return false;
    }

    /**
     * Returns a String object representing this character's value.
     * Converts this <code>Character</code> object to a string. The 
     * result is a string whose length is <code>1</code>. The string's 
     * sole component is the primitive <code>char</code> value represented 
     * by this object. 
     *
     * @return  a string representation of this object.
     * @since   JDK1.0
     */
    public String toString() {
        char buf[] = {value};
        return String.valueOf(buf);
    }
 
   /**
     * Determines if the specified character is a lowercase character. 
     * A character is lowercase if it is not in the range 
     * <code>'&#92;u2000'</code> through <code>'&#92;u2FFF'</code>, the Unicode 
     * attribute table does not specify a mapping to lowercase for the 
     * character, and at least one of the following is true: 
     * <ul>
     * <li>The attribute table specifies a mapping to uppercase for the 
     *     character. 
     * <li>The name for the character contains the words "<code>SMALL 
     *     LETTER</code>". 
     * <li>The name for the character contains the words "<code>SMALL 
     *     LIGATURE</code>". 
     * </ul>
     * <p> A character is considered to be lowercase if and only if
     * it is specified to be lowercase by the Unicode 2.0 standard
     * (category "Ll" in the Unicode specification data file).
     * <p>
     * Of the ISO-LATIN-1 characters (character codes 0x0000 through 0x00FF),
     * the following are lowercase:
     * <p><blockquote><pre>
     * a b c d e f g h i j k l m n o p q r s t u v w x y z
     * &#92;u00DF &#92;u00E0 &#92;u00E1 &#92;u00E2 &#92;u00E3 &#92;u00E4 &#92;u00E5 &#92;u00E6 &#92;u00E7
     * &#92;u00E8 &#92;u00E9 &#92;u00EA &#92;u00EB &#92;u00EC &#92;u00ED &#92;u00EE &#92;u00EF &#92;u00F0
     * &#92;u00F1 &#92;u00F2 &#92;u00F3 &#92;u00F4 &#92;u00F5 &#92;u00F6 &#92;u00F8 &#92;u00F9 &#92;u00FA
     * &#92;u00FB &#92;u00FC &#92;u00FD &#92;u00FE &#92;u00FF
     * </pre></blockquote>
     * <p> Many other Unicode characters are lowercase, too.
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is lowercase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @since   JDK1.0
     */
    public static boolean isLowerCase(char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) == LOWERCASE_LETTER;
    }

   /**
     * Determines if the specified character is an uppercase character. 
     * A character is uppercase if it is not in the range 
     * <code>'&#92;u2000'</code> through <code>'&#92;u2FFF'</code>, the Unicode 
     * attribute table does not specify a mapping to uppercase for the 
     * character, and at least one of the following is true: 
     * <ul>
     * <li>The attribute table specifies a mapping to lowercase for the
     *     character. 
     * <li>The name for the character contains the words 
     *     "<code>CAPITAL LETTER</code>".
     * <li>The name for the character contains the words
     *     "<code>CAPITAL LIGATURE</code>".
     * </ul>
     * <p>
     * Of the ISO-LATIN-1 characters (character codes 0x0000 through 0x00FF),
     * the following are uppercase:
     * <p><blockquote><pre>
     * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
     * &#92;u00C0 &#92;u00C1 &#92;u00C2 &#92;u00C3 &#92;u00C4 &#92;u00C5 &#92;u00C6 &#92;u00C7
     * &#92;u00C8 &#92;u00C9 &#92;u00CA &#92;u00CB &#92;u00CC &#92;u00CD &#92;u00CE &#92;u00CF &#92;u00D0
     * &#92;u00D1 &#92;u00D2 &#92;u00D3 &#92;u00D4 &#92;u00D5 &#92;u00D6 &#92;u00D8 &#92;u00D9 &#92;u00DA
     * &#92;u00DB &#92;u00DC &#92;u00DD &#92;u00DE
     * </pre></blockquote>
     * <p> Many other Unicode characters are uppercase, too.
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is uppercase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     * @since   1.0
     */
    public static boolean isUpperCase(char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) == UPPERCASE_LETTER;
    }

    /**
     * Determines if the specified character is a titlecase character.
     * A character is considered to be titlecase if and only if
     * it is specified to be titlecase by the Unicode 2.0 standard
     * (category "Lt" in the Unicode specification data file).
     * <p>
     * The printed representations of four Unicode characters look like 
     * pairs of Latin letters. For example, there is an uppercase letter 
     * that looks like "LJ" and has a corresponding lowercase letter that 
     * looks like "lj". A third form, which looks like "Lj", 
     * is the appropriate form to use when rendering a word in lowercase 
     * with initial capitals, as for a book title.
     * <p>
     * These are the Unicode characters for which this method returns 
     * <code>true</code>: 
     * <ul>
     * <li><code>LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON</code> 
     * <li><code>LATIN CAPITAL LETTER L WITH SMALL LETTER J</code> 
     * <li><code>LATIN CAPITAL LETTER N WITH SMALL LETTER J</code> 
     * <li><code>LATIN CAPITAL LETTER D WITH SMALL LETTER Z</code> 
     * </ul>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is titlecase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @since   JDK1.0.2
     */
    public static boolean isTitleCase(char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) == TITLECASE_LETTER;
    }

    /**
     * Determines if the specified character is a digit.
     * A character is considered to be a digit if it is not in the range 
     * <code>'&#92;u2000'&nbsp;&lt;=&nbsp;ch&nbsp;&lt;=&nbsp;'&#92;u2FFF'</code>
     * and its Unicode name contains the word 
     * "<code>DIGIT</code>". For a more complete 
     * specification that encompasses all Unicode characters that are 
     * defined as digits, see Gosling, Joy, and Steele, <i>The Java 
     * Language Specification</i>.
     * <p>
     * These are the ranges of Unicode characters that are considered digits:
     * <table>
     * <tr><td>0x0030 through 0x0039</td>
     *                        <td>ISO-LATIN-1 digits ('0' through '9')</td></tr>
     * <tr><td>0x0660 through 0x0669</td>  <td>Arabic-Indic digits</td></tr>
     * <tr><td>0x06F0 through 0x06F9</td>
     *                                <td>Extended Arabic-Indic digits</td></tr>
     * <tr><td>0x0966 through 0x096F</td>  <td>Devanagari digits</td></tr>
     * <tr><td>0x09E6 through 0x09EF</td>  <td>Bengali digits</td></tr>
     * <tr><td>0x0A66 through 0x0A6F</td>  <td>Gurmukhi digits</td></tr>
     * <tr><td>0x0AE6 through 0x0AEF</td>  <td>Gujarati digits</td></tr>
     * <tr><td>0x0B66 through 0x0B6F</td>  <td>Oriya digits</td></tr>
     * <tr><td>0x0BE7 through 0x0BEF</td>  <td>Tamil digits</td></tr>
     * <tr><td>0x0C66 through 0x0C6F</td>  <td>Telugu digits</td></tr>
     * <tr><td>0x0CE6 through 0x0CEF</td>  <td>Kannada digits</td></tr>
     * <tr><td>0x0D66 through 0x0D6F</td>  <td>Malayalam digits</td></tr>
     * <tr><td>0x0E50 through 0x0E59</td>  <td>Thai digits</td></tr>
     * <tr><td>0x0ED0 through 0x0ED9</td>  <td>Lao digits</td></tr>
     * <tr><td>0x0F20 through 0x0F29</td>  <td>Tibetan digits</td></tr>
     * <tr><td>0xFF10 through 0xFF19</td>  <td>Fullwidth digits</td></tr>
     * </table>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a digit;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#digit(char, int)
     * @see     java.lang.Character#forDigit(int, int)
     * @since   JDK1.0
     */
    public static boolean isDigit(char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) == DECIMAL_DIGIT_NUMBER;
    }

    /**
     * Determines if a character has a defined meaning in Unicode.
     * A character is defined if at least one of the following is true: 
     * <ul>
     * <li>It has an entry in the Unicode attribute table. 
     * <li>Its value is in the range 
     *     <code>
     *     '&#92;u3040'&nbsp;&lt;=&nbsp;ch&nbsp;&lt;=&nbsp;'&#92;u9FA5'</code>. 
     * <li>Its value is in the range 
     *     <code>
     *     '&#92;uF900'&nbsp;&lt;=&nbsp;ch&nbsp;&lt;=&nbsp;'&#92;uFA2D'</code>. 
     * </ul>
     * 
     * @param   ch   the character to be tested
     * @return  <code>true</code> if the character has a defined meaning
     *          in Unicode; <code>false</code> otherwise.
     * @see     java.lang.Character#isDigit(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @since   JDK1.0.2
     */
    public static boolean isDefined(char ch) {
        return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F) != UNASSIGNED;
    }

    /**
     * Determines if the specified character is a letter. For a 
     * more complete specification that encompasses all Unicode 
     * characters, see Gosling, Joy, and Steele, <i>The Java Language 
     * Specification</i>. 
     *
     * <p> A character is considered to be a letter if and only if
     * it is specified to be a letter by the Unicode 2.0 standard
     * (category "Lu", "Ll", "Lt", "Lm", or "Lo" in the Unicode
     * specification data file).
     *
     * <p> Note that most ideographic characters are considered
     * to be letters (category "Lo") for this purpose.
     *
     * <p> Note also that not all letters have case: many Unicode characters are
     * letters but are neither uppercase nor lowercase nor titlecase.
     * 
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a letter;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isDigit(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isJavaLetter(char)
     * @see     java.lang.Character#isJavaLetterOrDigit(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @since   JDK1.0
     */
    public static boolean isLetter(char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER))
                  >> (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is a letter or digit. 
     * For a more complete specification that encompasses all Unicode 
     * characters, see Gosling, Joy, and Steele, <i>The Java Language 
     * Specification</i>. 
     *
     * <p> A character is considered to be a letter if and only if
     * it is specified to be a letter or a digit by the Unicode 2.0 standard
     * (category "Lu", "Ll", "Lt", "Lm", "Lo", or "Nd" in the Unicode
     * specification data file).  In other words, isLetterOrDigit is true
     * of a character if and only if either isLetter is true of the character
     * or isDigit is true of the character.
     * 
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a letter or digit;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isDigit(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isJavaLetter(char)
     * @see     java.lang.Character#isJavaLetterOrDigit(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.0.2
     */
    public static boolean isLetterOrDigit(char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER) |
                   (1 << DECIMAL_DIGIT_NUMBER))
                  >> (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is a 
     * "Java" letter, that is, the character is permissible 
     * as the first character in an identifier in the Java language. 
     * <p>
     * A character is considered to be a Java letter if and only if it 
     * is a letter, the ASCII dollar sign character <code>'$'</code>, or 
     * the underscore character <code>'_'</code>. 
     *
     * @param      ch   the character to be tested.
     * @return     <code>true</code> if the character is a Java letter;
     *             <code>false</code> otherwise.
     * @see        java.lang.Character#isJavaIdentifierStart(char)
     * @see        java.lang.Character#isJavaLetterOrDigit(char)
     * @see        java.lang.Character#isLetter(char)
     * @see        java.lang.Character#isLetterOrDigit(char)
     * @see        java.lang.Character#isUnicodeIdentifierStart(char)
     * @since      JDK1.0.2
     * @deprecated Replaced by isJavaIdentifierStart(char).
     */
    public static boolean isJavaLetter(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00070000) >= 0x00050000;
    }

    /**
     * Determines if the specified character is a 
     * "Java" letter or digit, that is, the character is 
     * permissible as a non-initial character in an identifier in the 
     * Java language. 
     * <p>
     * A character is considered to be a Java letter or digit if and 
     * only if it is a letter, a digit, the ASCII dollar sign character 
     * <code>'$'</code>, or the underscore character <code>'_'</code>. 
     *
     * @param      ch   the character to be tested.
     * @return     <code>true</code> if the character is a Java letter or digit;
     *             <code>false</code> otherwise.
     * @see        java.lang.Character#isJavaIdentifierPart(char)
     * @see        java.lang.Character#isJavaLetter(char)
     * @see        java.lang.Character#isLetter(char)
     * @see        java.lang.Character#isLetterOrDigit(char)
     * @see        java.lang.Character#isUnicodeIdentifierPart(char)
     * @since      JDK1.0.2
     * @deprecated Replaced by isJavaIdentifierPart(char).
     */
    public static boolean isJavaLetterOrDigit(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00030000) != 0;
    }

    /**
     * Determines if the specified character is
     * permissible as the first character in a Java identifier.
     * A character may start a Java identifier if and only if
     * it is one of the following:
     * <ul>
     * <li>  a letter
     * <li>  a currency symbol (such as "$")
     * <li>  a connecting punctuation character (such as "_").
     * </ul>
     *
     * @param   ch      the character to be tested.
     * @return  true if the character may start a Java identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   JDK1.1
     */
    public static boolean isJavaIdentifierStart(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00070000) >= 0x00050000;
    }

    /**
     * Determines if the specified character may be part of a Java
     * identifier as other than the first character.
     * A character may be part of a Java identifier if and only if
     * it is one of the following:
     * <ul>
     * <li>  a letter
     * <li>  a currency symbol (such as "$")
     * <li>  a connecting punctuation character (such as "_").
     * <li>  a digit
     * <li>  a numeric letter (such as a Roman numeral character)
     * <li>  a combining mark
     * <li>  a non-spacing mark
     * <li>  an ignorable control character
     * </ul>
     * 
     * @param   ch      the character to be tested.
     * @return  true if the character may be part of a Unicode identifier; 
     *          false otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isJavaIdentifierPart(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00030000) != 0;
    }

    /**
     * Determines if the specified character is
     * permissible as the first character in a Unicode identifier.
     * A character may start a Unicode identifier if and only if
     * it is a letter.
     *
     * @param   ch      the character to be tested.
     * @return  true if the character may start a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isUnicodeIdentifierStart(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00070000) == 0x00070000;
    }

    /**
     * Determines if the specified character may be part of a Unicode
     * identifier as other than the first character.
     * A character may be part of a Unicode identifier if and only if
     * it is one of the following:
     * <ul>
     * <li>  a letter
     * <li>  a connecting punctuation character (such as "_").
     * <li>  a digit
     * <li>  a numeric letter (such as a Roman numeral character)
     * <li>  a combining mark
     * <li>  a non-spacing mark
     * <li>  an ignorable control character
     * </ul>
     * 
     * @param   ch      the character to be tested.
     * @return  true if the character may be part of a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   JDK1.1
     */
    public static boolean isUnicodeIdentifierPart(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00010000) != 0;
    }

    /**
     * Determines if the specified character should be regarded as
     * an ignorable character in a Java identifier or a Unicode identifier.
     * The following Unicode characters are ignorable in a Java identifier
     * or a Unicode identifier:
     * <table>
     * <tr><td>0x0000 through 0x0008,</td>
     *                                 <td>ISO control characters that</td></tr>
     * <tr><td>0x000E through 0x001B,</td> <td>are not whitespace</td></tr>
     * <tr><td>and 0x007F through 0x009F</td></tr>
     * <tr><td>0x200C through 0x200F</td>  <td>join controls</td></tr>
     * <tr><td>0x200A through 0x200E</td>  <td>bidirectional controls</td></tr>
     * <tr><td>0x206A through 0x206F</td>  <td>format controls</td></tr>
     * <tr><td>0xFEFF</td>               <td>zero-width no-break space</td></tr>
     * </table>
     * 
     * @param   ch      the character to be tested.
     * @return  true if the character may be part of a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isIdentifierIgnorable(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00070000) == 0x00010000;
    }

    /**
     * The given character is mapped to its lowercase equivalent; if the 
     * character has no lowercase equivalent, the character itself is 
     * returned. 
     * <p>
     * A character has a lowercase equivalent if and only if a lowercase 
     * mapping is specified for the character in the Unicode attribute 
     * table. 
     * <p>
     * Note that some Unicode characters in the range 
     * <code>'&#92;u2000'</code> to <code>'&#92;u2FFF'</code> have lowercase 
     * mappings; this method does map such characters to their lowercase 
     * equivalents even though the method <code>isUpperCase</code> does 
     * not return <code>true</code> for such characters. 
     *
     * @param   ch   the character to be converted.
     * @return  the lowercase equivalent of the character, if any;
     *          otherwise the character itself.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     * @since   JDK1.0
     */
    public static char toLowerCase(char ch) {
        int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];
        if ((val & 0x00200000) != 0)
          return (char)(ch + (val >> 22));
        else
          return ch;
    }

    /**
     * Converts the character argument to uppercase. A character has an 
     * uppercase equivalent if and only if an uppercase mapping is 
     * specified for the character in the Unicode attribute table. 
     * <p>
     * Note that some Unicode characters in the range 
     * <code>'&#92;u2000'</code> to <code>'&#92;u2000FFF'</code> have uppercase 
     * mappings; this method does map such characters to their titlecase 
     * equivalents even though the method <code>isLowerCase</code> does 
     * not return <code>true</code> for such characters. 
     *
     * @param   ch   the character to be converted.
     * @return  the uppercase equivalent of the character, if any;
     *          otherwise the character itself.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @since   JDK1.0
     */
    public static char toUpperCase(char ch) {
        int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];
        if ((val & 0x00100000) != 0)
          return (char)(ch - (val >> 22));
        else
          return ch;
    }

    /**
     * Converts the character argument to titlecase. A character has a 
     * titlecase equivalent if and only if a titlecase mapping is 
     * specified for the character in the Unicode attribute table. 
     * <p>
     * Note that some Unicode characters in the range 
     * <code>'&#92;u2000'</code> through <code>'&#92;u2FFF'</code> have titlecase 
     * mappings; this method does map such characters to their titlecase 
     * equivalents even though the method <code>isTitleCase</code> does 
     * not return <code>true</code> for such characters.
     * <p>
     * There are only four Unicode characters that are truly titlecase forms
     * that are distinct from uppercase forms.  As a rule, if a character has no
     * true titlecase equivalent but does have an uppercase mapping, then the
     * Unicode 2.0 attribute table specifies a titlecase mapping that is the
     * same as the uppercase mapping.
     *
     * @param   ch   the character to be converted.
     * @return  the titlecase equivalent of the character, if any;
     *          otherwise the character itself.
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     * @since   JDK1.0.2
     */
    public static char toTitleCase(char ch) {
        int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];
        if ((val & 0x00080000) != 0) {
          // There is a titlecase equivalent.  Perform further checks:
          if ((val & 0x00100000) == 0) {
            // The character does not have an uppercase equivalent, so it must
            // already be uppercase; so add 1 to get the titlecase form.
            return (char)(ch + 1);
          }
          else if ((val & 0x00200000) == 0) {
            // The character does not have a lowercase equivalent, so it must
            // already be lowercase; so subtract 1 to get the titlecase form.
            return (char)(ch - 1);
          }
          else {
            // The character has both an uppercase equivalent and a lowercase
            // equivalent, so it must itself be a titlecase form; return it.
            return ch;
          }
        }
        else if ((val & 0x00100000) != 0) {
          // This character has no titlecase equivalent but it does have an
          // uppercase equivalent, so use that (subtract the signed case offset).
          return (char)(ch - (val >> 22));
        }
        else
          return ch;
    }

    /**
     * Returns the numeric value of the character <code>ch</code> in the 
     * specified radix. 
     * <p>
     * If the radix is not in the range <code>MIN_RADIX</code>&nbsp;&lt;= 
     * <code>radix</code>&nbsp;&lt;= <code>MAX_RADIX</code> or if the 
     * value of <code>ch</code> is not a valid digit in the specified 
     * radix, <code>-1</code> is returned. A character is a valid digit 
     * if at least one of the following is true:
     * <ul>
     * <li>The method <code>isDigit</code> is true of the character 
     *     and the Unicode decimal digit value of the character (or its 
     *     single-character decomposition) is less than the specified radix. 
     *     In this case the decimal digit value is returned. 
     * <li>The character is one of the uppercase Latin letters 
     *     <code>'A'</code> through <code>'Z'</code> and its code is less than
     *     <code>radix&nbsp;+ 'A'&nbsp;-&nbsp;10</code>. 
     *     In this case, <code>ch&nbsp;- 'A'&nbsp;+&nbsp;10</code> 
     *     is returned. 
     * <li>The character is one of the lowercase Latin letters 
     *     <code>'a'</code> through <code>'z'</code> and its code is less than
     *     <code>radix&nbsp;+ 'a'&nbsp;-&nbsp;10</code>. 
     *     In this case, <code>ch&nbsp;- 'a'&nbsp;+&nbsp;10</code> 
     *     is returned. 
     * </ul>
     *
     * @param   ch      the character to be converted.
     * @param   radix   the radix.
     * @return  the numeric value represented by the character in the
     *          specified radix.
     * @see     java.lang.Character#forDigit(int, int)
     * @see     java.lang.Character#isDigit(char)
     * @since   JDK1.0
     */
    public static int digit(char ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
          int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];
          int kind = val & 0x1F;
          if (kind == DECIMAL_DIGIT_NUMBER) {
            value = ((ch + (val >> 9)) & 0x1F);
          }
          else if ((val & 0x0000C000) == 0x0000C000) {
            // Java supradecimal digit
            value = ((ch + (val >> 9)) & 0x1F) + 10;
          }
        }
        return (value < radix) ? value : -1;
    }

    /**
     * Returns the Unicode numeric value of the character as a
     * nonnegative integer.
     * If the character does not have a numeric value, then -1 is returned.
     * If the character has a numeric value that cannot be represented as a
     * nonnegative integer (for example, a fractional value), then -2
     * is returned.
     *
     * @param   ch      the character to be converted.
     * @return  the numeric value of the character, as a nonnegative int value;
     *          -2 if the character has a numeric value that is not a
     *          nonnegative integer; -1 if the character has no numeric value.
     * @see     java.lang.Character#forDigit(char)
     * @see     java.lang.Character#isDigit(char)
     * @since   JDK1.1
     */
    public static int getNumericValue(char ch) {
        int val = A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)];
        switch ((val >> 14) & 0x3) {
        default: // cannot occur
        case (0x00000000 >> 14):         // not numeric
            return -1;
        case (0x00004000 >> 14):              // simple numeric
            return (ch + (val >> 9)) & 0x1F;
        case (0x00008000 >> 14)      :       // "strange" numeric
            switch (ch) {
            case '\u0BF1': return 100;          // TAMIL NUMBER ONE HUNDRED
            case '\u0BF2': return 1000;         // TAMIL NUMBER ONE THOUSAND
            case '\u216C': return 50;           // ROMAN NUMERAL FIFTY
            case '\u216D': return 100;          // ROMAN NUMERAL ONE HUNDRED
            case '\u216E': return 500;          // ROMAN NUMERAL FIVE HUNDRED
            case '\u216F': return 1000;         // ROMAN NUMERAL ONE THOUSAND
            case '\u217C': return 50;           // SMALL ROMAN NUMERAL FIFTY
            case '\u217D': return 100;          // SMALL ROMAN NUMERAL ONE HUNDRED
            case '\u217E': return 500;          // SMALL ROMAN NUMERAL FIVE HUNDRED
            case '\u217F': return 1000;         // SMALL ROMAN NUMERAL ONE THOUSAND
            case '\u2180': return 1000;         // ROMAN NUMERAL ONE THOUSAND C D
            case '\u2181': return 5000;         // ROMAN NUMERAL FIVE THOUSAND
            case '\u2182': return 10000;        // ROMAN NUMERAL TEN THOUSAND
            default:       return -2;
            }
        case (0x0000C000 >> 14):           // Java supradecimal
            return ((ch + (val >> 9)) & 0x1F) + 10;
        }
    }

    /**
     * Determines if the specified character is ISO-LATIN-1 white space. 
     * This method returns <code>true</code> for the following five 
     * characters only: 
     * <table><code>
     * <tr><td>'\t'</td>  <td>&#92;u0009</td>  <td>HORIZONTAL TABULATION</td></tr>
     * <tr><td>'\n'</td>  <td>&#92;u000A</td>  <td>NEW LINE</td></tr>
     * <tr><td>'\f'</td>  <td>&#92;u000C</td>  <td>FORM FEED</td></tr>
     * <tr><td>'\r'</td>  <td>&#92;u000D</td>  <td>CARRIAGE RETURN</td></tr>
     * <tr><td>'&nbsp;&nbsp;'</td>
     *                    <td>&#92;u0020</td>  <td>SPACE</td></tr>
     * </code></table>
     *
     * @param      ch   the character to be tested.
     * @return     <code>true</code> if the character is ISO-LATIN-1 white
     *             space; <code>false</code> otherwise.
     * @see        java.lang.Character#isSpaceChar(char)
     * @see        java.lang.Character#isWhitespace(char)
     * @since      JDK1.0
     * @deprecated Replaced by isWhitespace(char).
     */
    public static boolean isSpace(char ch) {
      return (ch <= 0x0020) &&
             (((((1L << 0x0009) |
                 (1L << 0x000A) |
                 (1L << 0x000C) |
                 (1L << 0x000D) |
                 (1L << 0x0020)) >> ch) & 1L) != 0);
    }

    /**
     * Determines if the specified character is a Unicode space character.
     * A character is considered to be a space character if and only if
     * it is specified to be a space character by the Unicode 2.0 standard
     * (category "Zs", "Zl, or "Zp" in the Unicode specification data file).
     * 
     * @param   ch      the character to be tested.
     * @return  true if the character is a space character; false otherwise.
     * @see     java.lang.Character#isWhitespace(char)
     * @since   JDK1.1
     */
    public static boolean isSpaceChar(char ch) {
        return (((((1 << SPACE_SEPARATOR) |
                   (1 << LINE_SEPARATOR) |
                   (1 << PARAGRAPH_SEPARATOR))
                  >> (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is white space according to Java.
     * A character is considered to be a Java whitespace character if and only
     * if it satisfies one of the following criteria:
     * <ul>
     * <li> It is a Unicode space separator (category "Zs"), but is not
     *      a no-break space (&#92;u00A0 or &#92;uFEFF).
     * <li> It is a Unicode line separator (category "Zl").
     * <li> It is a Unicode paragraph separator (category "Zp").
     * <li> It is &#92;u0009, HORIZONTAL TABULATION.
     * <li> It is &#92;u000A, LINE FEED.
     * <li> It is &#92;u000B, VERTICAL TABULATION.
     * <li> It is &#92;u000C, FORM FEED.
     * <li> It is &#92;u000D, CARRIAGE RETURN.
     * <li> It is &#92;u001C, FILE SEPARATOR.
     * <li> It is &#92;u001D, GROUP SEPARATOR.
     * <li> It is &#92;u001E, RECORD SEPARATOR.
     * <li> It is &#92;u001F, UNIT SEPARATOR.
     * </ul>
     *
     * @param   ch      the character to be tested.
     * @return  true if the character is a Java whitespace character;
     *          false otherwise.
     * @see     java.lang.Character#isSpaceChar(char)
     * @since   JDK1.1
     */
    public static boolean isWhitespace(char ch) {
      return (A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x00070000) == 0x00040000;
    }

    /**
     * Determines if the specified character is an ISO control character.
     * A character is considered to be an ISO control character if its
     * code is in the range &#92;u0000 through &#92;u001F or in the range
     * &#92;u007F through &#92;u009F.
     *
     * @param   ch      the character to be tested.
     * @return  true if the character is an ISO control character;
     *          false otherwise.
     *
     * @see     java.lang.Character#isSpaceChar(char)
     * @see     java.lang.Character#isWhitespace(char)
     * @since   JDK1.1
     */
    public static boolean isISOControl(char ch) {
      return (ch <= 0x009F) && ((ch <= 0x001F) || (ch >= 0x007F));
    }

    /**
     * Returns a value indicating a character category.
     *
     * @param   ch      the character to be tested.
     * @return  a value of type int, the character category.
     * @see     java.lang.Character#COMBINING_SPACING_MARK
     * @see     java.lang.Character#CONNECTOR_PUNCTUATION
     * @see     java.lang.Character#CONTROL
     * @see     java.lang.Character#CURRENCY_SYMBOL
     * @see     java.lang.Character#DASH_PUNCTUATION
     * @see     java.lang.Character#DECIMAL_DIGIT_NUMBER
     * @see     java.lang.Character#ENCLOSING_MARK
     * @see     java.lang.Character#END_PUNCTUATION
     * @see     java.lang.Character#FORMAT
     * @see     java.lang.Character#LETTER_NUMBER
     * @see     java.lang.Character#LINE_SEPARATOR
     * @see     java.lang.Character#LOWERCASE_LETTER
     * @see     java.lang.Character#MATH_SYMBOL
     * @see     java.lang.Character#MODIFIER_LETTER
     * @see     java.lang.Character#MODIFIER_SYMBOL
     * @see     java.lang.Character#NON_SPACING_MARK
     * @see     java.lang.Character#OTHER_LETTER
     * @see     java.lang.Character#OTHER_NUMBER
     * @see     java.lang.Character#OTHER_PUNCTUATION
     * @see     java.lang.Character#OTHER_SYMBOL
     * @see     java.lang.Character#PARAGRAPH_SEPARATOR
     * @see     java.lang.Character#PRIVATE_USE
     * @see     java.lang.Character#SPACE_SEPARATOR
     * @see     java.lang.Character#START_PUNCTUATION
     * @see     java.lang.Character#SURROGATE
     * @see     java.lang.Character#TITLECASE_LETTER
     * @see     java.lang.Character#UNASSIGNED
     * @see     java.lang.Character#UPPERCASE_LETTER
     * @since   JDK1.1
     */
    public static int getType(char ch) {
        return A[Y[(X[ch>>6]<<5)|((ch>>1)&0x1F)]|(ch&0x1)] & 0x1F;
    }

    /**
     * Determines the character representation for a specific digit in 
     * the specified radix. If the value of <code>radix</code> is not a 
     * valid radix, or the value of <code>digit</code> is not a valid 
     * digit in the specified radix, the null character 
     * (<code>'&#92;u0000'</code>) is returned. 
     * <p>
     * The <code>radix</code> argument is valid if it is greater than or 
     * equal to <code>MIN_RADIX</code> and less than or equal to 
     * <code>MAX_RADIX</code>. The <code>digit</code> argument is valid if
     * <code>0&nbsp;&lt;= digit&nbsp;&lt;=&nbsp;radix</code>. 
     * <p>
     * If the digit is less than 10, then 
     * <code>'0'&nbsp;+ digit</code> is returned. Otherwise, the value 
     * <code>'a'&nbsp;+ digit&nbsp;-&nbsp;10</code> is returned. 
     *
     * @param   digit   the number to convert to a character.
     * @param   radix   the radix.
     * @return  the <code>char</code> representation of the specified digit
     *          in the specified radix. 
     * @see     java.lang.Character#MIN_RADIX
     * @see     java.lang.Character#MAX_RADIX
     * @see     java.lang.Character#digit(char, int)
     * @since   JDK1.0
     */
    public static char forDigit(int digit, int radix) {
        if ((digit >= radix) || (digit < 0)) {
            return '\0';
        }
        if ((radix < MIN_RADIX) || (radix > MAX_RADIX)) {
            return '\0';
        }
        if (digit < 10) {
            return (char)('0' + digit);
        } 
        return (char)('a' - 10 + digit);
    }

    /* The character properties are currently encoded into 32 bits in the following manner:
       10 bits  signed offset used for converting case
        1 bit   if 1, adding the signed offset converts the character to lowercase
        1 bit   if 1, subtracting the signed offset converts the character to uppercase
        1 bit   if 1, this character has a titlecase equivalent (possibly itself)
        3 bits  0  may not be part of an identifier
                1  ignorable control; may continue a Unicode identifier or Java identifier
                2  may continue a Java identifier but not a Unicode identifier (unused)
                3  may continue a Unicode identifier or Java identifier
                4  is a Java whitespace character
                5  may start or continue a Java identifier;
                   may continue but not start a Unicode identifier (underscores)
                6  may start or continue a Java identifier but not a Unicode identifier ($)
                7  may start or continue a Unicode identifier or Java identifier
                Thus:
                   5, 6, 7 may start a Java identifier
                   1, 2, 3, 5, 6, 7 may continue a Java identifier
                   7 may start a Unicode identifier
                   1, 3, 5, 7 may continue a Unicode identifier
                   1 is ignorable within an identifier
                   4 is Java whitespace
        2 bits  0  this character has no numeric property
                1  adding the digit offset to the character code and then
                   masking with 0x1F will produce the desired numeric value
                2  this character has a "strange" numeric value
                3  a Java supradecimal digit: adding the digit offset to the
                   character code, then masking with 0x1F, then adding 10
                   will produce the desired numeric value
        5 bits  digit offset
        4 bits  reserved for future use
        5 bits  character type
     */

  // The following tables and code generated using:
  // java GenerateCharacter -string -o Character.java.x [10 5 1] [-spec UnicodeData-2.1.2.txt] [-template Character.java.template]
  // The X table has 1024 entries for a total of 1024 bytes.

  private static final byte X[] = new byte[1024];
  private static final String X_DATA =
    "\u0100\u0302\u0504\u0706\u0908\u0B0A\u0D0C\u0F0E\u1110\u1312\u1514\u1716\u1918"+
    "\u1B1A\u1C1C\u1C1C\u1C1C\u1C1C\u1E1D\u201F\u2221\u2423\u2625\u2827\u2A29\u2C2B"+
    "\u2E2D\u1C1C\u302F\u3231\u3433\u1C35\u1C1C\u3736\u3938\u3B3A\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u3C3C\u3E3D\u403F\u4241\u4443"+
    "\u4645\u4847\u4A49\u4C4B\u4E4D\u504F\u1C1C\u5251\u5453\u5555\u5756\u5758\u1C1C"+
    "\u5A59\u1C5B\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u5D5C\u5F5E\u3860\u1C61\u6362\u6564\u6766\u6866"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u1C69\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C"+
    "\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u1C1C\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838\u3838"+
    "\u3838\u3838\u1C6A\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B"+
    "\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6B6B\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C"+
    "\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C"+
    "\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C"+
    "\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C\u6C6C"+
    "\u6C6C\u6C6C\u6C6C\u6C6C\u3838\u3838\u1C6D\u1C1C\u6F6E\u7170\u7272\u7272\u7473"+
    "\u7675\u7877\u7972\u7B7A\u7D7C";

  // The Y table has 4032 entries for a total of 8064 bytes.

  private static final short Y[] = new short[4032];
  private static final String Y_DATA =
    "\000\000\000\000\002\004\004\000\000\000\000\000\000\000\004\004\006\010\012"+
    "\014\016\020\022\024\026\026\026\026\026\030\032\034\036\040\040\040\040\040"+
    "\040\040\040\040\040\040\040\042\044\046\050\052\052\052\052\052\052\052\052"+
    "\052\052\052\052\054\056\060\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\062\064\064\066\070\072\074\076\100\102\104\106\110\112\114"+
    "\116\120\120\120\120\120\120\120\120\120\120\120\122\120\120\120\124\126\126"+
    "\126\126\126\126\126\126\126\126\126\130\126\126\126\132\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\136\134\134\134\140\142\142\142\142\142\142\142\144\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\146\142"+
    "\142\150\152\134\134\154\156\160\144\162\164\156\166\170\134\172\174\176\134"+
    "\134\134\200\202\204\134\206\210\212\142\214\134\216\134\220\220\220\222\224"+
    "\226\222\230\142\142\142\142\142\142\142\232\134\134\134\134\134\134\134\134"+
    "\134\234\226\134\236\236\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\172\240\242\244\246\250\172\172"+
    "\252\254\172\172\256\172\172\260\172\262\264\172\172\172\172\172\172\266\172"+
    "\172\270\272\172\172\172\274\172\172\172\172\172\172\172\172\172\172\276\236"+
    "\236\236\300\300\300\300\302\304\300\300\300\306\306\306\306\306\306\306\300"+
    "\306\306\306\306\306\306\310\300\300\302\306\306\236\236\236\236\236\236\236"+
    "\236\236\236\236\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312"+
    "\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312\312"+
    "\312\236\236\236\236\236\236\236\236\236\236\236\236\236\312\236\236\236\236"+
    "\236\236\236\236\236\314\236\236\316\236\320\236\236\306\322\324\326\330\332"+
    "\334\120\120\120\120\120\120\120\120\336\120\120\120\120\340\342\344\126\126"+
    "\126\126\126\126\126\126\346\126\126\126\126\350\352\354\356\360\362\236\364"+
    "\364\364\364\134\134\134\134\134\134\134\366\216\236\236\236\236\236\236\370"+
    "\372\372\372\372\372\374\372\120\120\120\120\120\120\120\120\120\120\120\120"+
    "\120\120\120\120\126\126\126\126\126\126\126\126\126\126\126\126\126\126\126"+
    "\126\376\u0100\u0100\u0100\u0100\u0100\u0102\u0100\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\u0104\312\u0106\236\236\236\236"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\134\u0108\142\u010A\u010C\u010A\u010C\u010A\236\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\236\134\134\134\134\236\134\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\u010E\u0110\u0110\u0110\u0110\u0110\u0110\u0110"+
    "\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0112\u0114"+
    "\314\314\314\u0116\u0118\u0118\u0118\u0118\u0118\u0118\u0118\u0118\u0118\u0118"+
    "\u0118\u0118\u0118\u0118\u0118\u0118\u0118\u0118\u011A\u011C\236\236\236\u011E"+
    "\u0120\u0120\u0120\u0120\u0120\u0120\u0120\u0120\u011E\u0120\u0120\u0120\u0120"+
    "\u0120\u0120\u0120\u0120\u0120\u0120\u0120\u011E\u0120\u0122\u0122\u0124\u0126"+
    "\236\236\236\236\236\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u012A\236\236\u0128\u012C\u012E\236\236\236\236\236"+
    "\236\236\236\236\236\236\u012E\236\236\236\236\236\236\u0130\236\u0130\u0132"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u012A"+
    "\236\236\u0134\u0128\u0128\u0128\u0128\u0136\u0120\u0120\u0120\u0126\236\236"+
    "\236\236\236\236\u0138\u0138\u0138\u0138\u0138\u013A\u013C\236\u013E\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\236\u0128\u0128\u012A\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u012A\u0128\u0128\u0140\u0120\u0120\u0120"+
    "\u0142\u0144\u0120\u0120\u0146\u0148\u014A\u0120\u0120\236\026\026\026\026"+
    "\026\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\u014C"+
    "\u014E\u0150\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\236\u0152\u0154\u0156\312\312\312"+
    "\u014E\u0154\u0156\236\u0104\312\u0106\236\220\220\220\220\220\312\314\u0158"+
    "\u0158\u0158\u0158\u0158\320\236\236\236\236\236\236\236\u014C\u0154\u0150"+
    "\220\220\220\u015A\u0150\u015A\u0150\220\220\220\220\220\220\220\220\220\220"+
    "\u015A\220\220\220\u015A\u015A\236\220\220\236\u0106\u0154\u0156\312\u0106"+
    "\u015C\u015E\u015C\u0156\236\236\236\236\u015C\236\236\220\u0150\220\312\236"+
    "\u0158\u0158\u0158\u0158\u0158\220\u0160\u0162\u0162\u0164\u0166\236\236\236"+
    "\u0106\u0150\220\220\u015A\236\u0150\u015A\u0150\220\220\220\220\220\220\220"+
    "\220\220\220\u015A\220\220\220\u015A\220\u0150\u015A\220\236\u0106\u0154\u0156"+
    "\u0106\236\u014C\u0106\u014C\312\236\236\236\236\236\u0150\220\u015A\u015A"+
    "\236\236\236\u0158\u0158\u0158\u0158\u0158\312\220\u015A\236\236\236\236\236"+
    "\u014C\u014E\u0150\220\220\220\u0150\u0150\220\u0150\220\220\220\220\220\220"+
    "\220\220\220\220\u015A\220\220\220\u015A\220\u0150\220\220\236\u0152\u0154"+
    "\u0156\312\312\u014C\u014E\u015C\u0156\236\u0166\236\236\236\236\236\236\236"+
    "\u015A\236\236\u0158\u0158\u0158\u0158\u0158\236\236\236\236\236\236\236\236"+
    "\u014C\u0154\u0150\220\220\220\u015A\u0150\u015A\u0150\220\220\220\220\220"+
    "\220\220\220\220\220\u015A\220\220\220\u015A\220\236\220\220\236\u0152\u0156"+
    "\u0156\312\236\u015C\u015E\u015C\u0156\236\236\236\236\u014E\236\236\220\u0150"+
    "\220\236\236\u0158\u0158\u0158\u0158\u0158\u0166\236\236\236\236\236\236\236"+
    "\236\u014E\u0150\220\220\u015A\236\220\u015A\220\220\236\u0150\u015A\u015A"+
    "\220\236\u0150\u015A\236\220\u015A\236\220\220\220\220\u0150\220\236\236\u0154"+
    "\u014E\u015E\236\u0154\u015E\u0154\u0156\236\236\236\236\u015C\236\236\236"+
    "\236\236\236\236\u0168\u0158\u0158\u0158\u0158\u016A\u016C\236\236\236\236"+
    "\236\236\u015C\u0154\u0150\220\220\220\u015A\220\u015A\220\220\220\220\220"+
    "\220\220\220\220\220\220\u015A\220\220\220\220\220\u0150\220\220\236\236\312"+
    "\u014E\u0154\u015E\312\u0106\312\312\236\236\236\u014C\u0106\236\236\236\236"+
    "\220\236\236\u0158\u0158\u0158\u0158\u0158\236\236\236\236\236\236\236\236"+
    "\236\u0154\u0150\220\220\220\u015A\220\u015A\220\220\220\220\220\220\220\220"+
    "\220\220\220\u015A\220\220\220\220\220\u0150\220\220\236\236\u0156\u0154\u0154"+
    "\u015E\u014E\u015E\u0154\312\236\236\236\u015C\u015E\236\236\236\u015A\220"+
    "\236\236\u0158\u0158\u0158\u0158\u0158\236\236\236\236\236\236\236\236\236"+
    "\u0154\u0150\220\220\220\u015A\220\u015A\220\220\220\220\220\220\220\220\220"+
    "\220\220\u015A\220\220\220\220\220\220\220\220\236\236\u0154\u0156\312\236"+
    "\u0154\u015E\u0154\u0156\236\236\236\236\u015C\236\236\236\236\220\236\236"+
    "\u0158\u0158\u0158\u0158\u0158\236\236\236\236\236\236\236\236\u0150\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\u016E\u0170\220\312\312\312\u0106\236\u0172\220\220\220\u0174\312\312"+
    "\312\u0176\u0178\u0178\u0178\u0178\u0178\314\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\u0150\u015A\u015A\u0150\u015A\u015A"+
    "\u0150\236\236\236\220\220\u0150\220\220\220\u0150\220\u0150\u0150\236\220"+
    "\u0150\u016E\u0170\220\312\312\312\u014C\u0152\236\220\220\u015A\316\312\312"+
    "\312\236\u0178\u0178\u0178\u0178\u0178\236\220\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\u017A\u017A\314\314\314\314\314\314"+
    "\314\u017C\u017A\u017A\312\u017A\u017A\u017A\u017E\u017E\u017E\u017E\u017E"+
    "\u0180\u0180\u0180\u0180\u0180\u0104\u0104\u0104\u0182\u0182\u0154\220\220"+
    "\220\220\u0150\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\236\236\236\u014C\312\312\312\312\312\312\u014E\312\312\u0184\312\312"+
    "\312\236\236\312\312\312\u014C\u014C\312\312\312\312\312\312\312\312\312\312"+
    "\236\u014C\312\312\312\u014C\236\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110"+
    "\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\u0110\236\236"+
    "\236\236\236\172\172\172\172\172\172\172\172\172\172\172\172\172\172\172\172"+
    "\172\172\172\276\236\u011C\236\236\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\236\236\u0150"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\u015A\236\236\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\236\236\236\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\172\172\u0186\236\236\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\236\236\236\u0188\u0188\u0188\u0188\u018A\u018A\u018A\u018A"+
    "\u0188\u0188\u0188\236\u018A\u018A\u018A\236\u0188\u0188\u0188\u0188\u018A"+
    "\u018A\u018A\u018A\u0188\u0188\u0188\u0188\u018A\u018A\u018A\u018A\u0188\u0188"+
    "\u0188\236\u018A\u018A\u018A\236\u018C\u018C\u018C\u018C\u018E\u018E\u018E"+
    "\u018E\u0188\u0188\u0188\u0188\u018A\u018A\u018A\u018A\u0190\u0192\u0192\u0194"+
    "\u0196\u0198\u019A\236\u0188\u0188\u0188\u0188\u018A\u018A\u018A\u018A\u0188"+
    "\u0188\u0188\u0188\u018A\u018A\u018A\u018A\u0188\u0188\u0188\u0188\u018A\u018A"+
    "\u018A\u018A\u0188\u019C\276\172\u018A\u019E\u01A0\u01A2\306\u019C\276\172"+
    "\u01A4\u01A4\u01A0\306\u0188\172\236\172\u018A\u01A6\u01A8\306\u0188\172\u01AA"+
    "\172\u018A\u01AC\u01AE\306\236\u019C\276\172\u01B0\u01B2\u01A0\310\u01B4\u01B4"+
    "\u01B4\u01B6\u01B4\u01B4\u01B8\u01BA\u01BC\u01BC\u01BC\014\u01BE\u01C0\u01BE"+
    "\u01C0\014\014\014\014\u01C2\u01B8\u01B8\u01C4\u01C6\u01C6\u01C8\014\u01CA"+
    "\u01CC\014\u01CE\u01D0\014\u01D2\u01D4\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\u01B8\u01B8\u01B8\u01D6\236\102\102\102\u01D8"+
    "\u01D2\u01DA\u01DC\u01DC\u01DC\u01DC\u01DC\u01D8\u01D2\u01D4\236\236\236\236"+
    "\236\236\236\236\064\064\064\064\064\064\u01DE\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\312\312\312\312\312\312\u01E0\u01E2"+
    "\u01E4\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\066\u01E6"+
    "\066\u01E8\066\u01EA\u01EC\u01EE\u01EC\u01F0\u01E8\066\u01EC\u01EC\u01EC\066"+
    "\066\066\u01E6\u01E6\u01E6\u01EC\u01EC\u01EE\u01EC\u01E8\u01F2\u01F4\u01F6"+
    "\236\236\236\236\236\236\236\236\236\236\236\236\u01F8\114\114\114\114\114"+
    "\u01FA\u01FC\u01FC\u01FC\u01FC\u01FC\u01FC\u01FE\u01FE\u0200\u0200\u0200\u0200"+
    "\u0200\u0200\u0202\u0202\u0204\u0206\236\236\236\236\236\236\u0208\u0208\u020A"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\u020A\u020A\066\066\066\066\066"+
    "\066\066\066\066\066\u020C\236\236\236\236\236\236\236\236\236\236\u020E\u0210"+
    "\032\u0208\u0210\u0210\u0210\u0208\u020E\u01D8\u020E\032\u0208\u0210\u0210"+
    "\u020E\u0210\032\032\032\u0208\u020E\u0210\u0210\u0210\u0210\u0208\u0208\u020E"+
    "\u020E\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\032\u0208\u0208\u0210"+
    "\u0210\u0208\u0208\u0208\u0208\u020E\032\032\u0210\u0210\u0210\u0210\u0208"+
    "\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210"+
    "\u0210\u0210\032\u020E\u0210\032\u0208\u0208\032\u0208\u0208\u0208\u0208\u0210"+
    "\u0208\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\032\u0208\u0208"+
    "\u0210\u0208\u0208\u0208\u0208\u020E\u0210\u0210\u0208\u0210\u0208\u0208\u0210"+
    "\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0210\u0208\u0210"+
    "\236\236\236\236\236\236\236\u020C\066\066\066\u0210\u0210\066\066\066\066"+
    "\066\066\066\066\066\066\u0210\066\066\066\u0212\u0214\066\066\066\066\066"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u0166\236\236\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\u020C\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\066\066\066\066\066\u020C\236"+
    "\236\236\236\236\236\236\236\236\236\u0216\u0216\u0216\u0216\u0216\u0216\u0216"+
    "\u0216\u0216\u0216\u0218\u0218\u0218\u0218\u0218\u0218\u0218\u0218\u0218\u0218"+
    "\u021A\u021A\u021A\u021A\u021A\u021A\u021A\u021A\u021A\u021A\066\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\u021C\u021C\u021C\u021C\u021C\u021C\u021C"+
    "\u021C\u021C\u021C\u021C\u021C\u021C\u021E\u021E\u021E\u021E\u021E\u021E\u021E"+
    "\u021E\u021E\u021E\u021E\u021E\u021E\u0220\236\236\236\236\236\236\236\236"+
    "\236\236\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\066\066\236\236\236\236\236\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\236\236\236\236\236"+
    "\236\236\236\066\066\066\066\066\066\066\066\066\066\236\236\236\066\066\066"+
    "\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\u0222\066"+
    "\u020C\066\066\236\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\u0222\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066\066"+
    "\u0222\u0222\066\u020C\236\u020C\066\066\066\u020C\u0222\066\066\066\236\236"+
    "\236\236\236\236\236\u0224\u0224\u0224\u0224\u0224\u0216\u0216\u0216\u0216"+
    "\u0216\u0226\u0226\u0226\u0226\u0226\u020C\236\066\066\066\066\066\066\066"+
    "\066\066\066\066\066\u0222\066\066\066\066\066\066\u020C\006\014\u0228\u022A"+
    "\016\016\016\016\016\066\016\016\016\016\u022C\u022E\u0230\u0232\u0232\u0232"+
    "\u0232\312\312\312\u0234\u0236\u0236\066\236\236\236\u0222\u0150\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\u015A\236\u014C\u0238\300\316\u0150\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\u016E\300\316"+
    "\236\236\u0150\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\u015A\236\u0150\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\u015A\u017A\u0180\u0180\u017A\u017A\u017A\u017A\u017A\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\236\236\236\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u0166\236"+
    "\u0180\u0180\u0180\u0180\u0180\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\236\u023A\u023C\u023C\u023C\u023C\u023C\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u0166\236\236\236\236\236\236\236\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\236\236\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u0166\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u0166\236\u023A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\236\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A\u017A"+
    "\u017A\u017A\u017A\u017A\u017A\u017A\u0166\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\236\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\236\236\236\236\236\236\236\236\236\236\236\236\236\236\u023E"+
    "\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E"+
    "\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E\u023E"+
    "\u023E\u023E\u023E\u023E\u023E\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240"+
    "\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240"+
    "\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\u0240\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\236\236\236\236\236\236\236\236\236\172\172\172\276\236\236\236\236"+
    "\236\u0242\172\172\236\236\236\u013E\u0128\u0128\u0128\u0128\u0244\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u012A\u0128\u0128\u012A\u012A\u0128\u0132\u012A\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\u0132"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u01BE\236\236\236\236\236\236\236"+
    "\236\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\236\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\236\236\236\236\236"+
    "\236\236\236\236\236\236\236\236\236\236\236\236\236\236\236\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\236\236\236\236\236\236\236\236\236\236\236\236\236\236"+
    "\236\236\236\236\u0246\u0246\236\236\236\236\236\236\u0248\u024A\u024C\u024E"+
    "\u024E\u024E\u024E\u024E\u024E\u024E\u0250\236\u0252\014\u01CE\u0254\014\u0256"+
    "\014\014\u022C\u024E\u024E\u01CC\014\074\u0208\u0258\u025A\014\236\236\u0128"+
    "\u012A\u012A\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128"+
    "\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u0128\u012A\u025C"+
    "\u0252\014\u025E\014\u01BE\u0260\u0248\014\026\026\026\026\026\014\u0208\u0262"+
    "\036\040\040\040\040\040\040\040\040\040\040\040\040\u0264\u0266\046\050\052"+
    "\052\052\052\052\052\052\052\052\052\052\052\u0268\u026A\u0258\u0252\u01BE"+
    "\u026C\220\220\220\220\220\u026E\220\220\220\220\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\220\220\220\300\220\220\220\220\220\220\220"+
    "\220\220\220\220\220\220\220\220\u015A\236\220\220\220\236\220\220\220\236"+
    "\220\220\220\236\220\u015A\236\u0270\u0272\u0274\u0276\u0208\u0208\u020A\u020C"+
    "\236\236\236\236\236\236\066\236";

  // The A table has 632 entries for a total of 2528 bytes.

  private static final int A[] = new int[632];
  private static final String A_DATA =
    "\001\u018F\001\u018F\001\u018F\004\u012F\004\u018F\004\u018F\004\u014C\000"+
    "\u0198\000\u0198\000\270\006\272\000\270\000\u0198\000\u0198\000\u0175\000"+
    "\u0176\000\u0198\000\271\000\370\000\264\000\370\000\230\003\u6069\003\u6069"+
    "\000\370\000\u0198\000\u0179\000\u0199\000\u0179\000\u0198\000\u0198\u0827"+
    "\uFE21\u0827\uFE21\u0827\uFE21\u0827\uFE21\000\u0175\000\u0198\000\u0176\000"+
    "\u019B\005\u0197\000\u019B\u0817\uFE22\u0817\uFE22\u0817\uFE22\u0817\uFE22"+
    "\000\u0175\000\u0199\000\u0176\000\u0199\001\u018F\000\u014C\000\u0198\006"+
    "\272\006\272\000\u019C\000\u019C\000\u019B\000\u019C\007\u0182\000\u0195\000"+
    "\u0199\000\u0194\000\u019C\000\u019B\000\274\000\271\000\u606B\000\u606B\000"+
    "\u019B\007\u0182\000\u019C\000\u0198\000\u019B\000\u506B\007\u0182\000\u0196"+
    "\000\u818B\000\u818B\000\u818B\000\u0198\u0827\041\u0827\041\u0827\041\000"+
    "\u0199\u0827\041\007\042\u0817\042\u0817\042\u0817\042\000\u0199\u0817\042"+
    "\uE1D7\042\147\041\127\042\uCE67\041\u3A17\042\007\042\147\041\127\042\147"+
    "\041\127\042\007\042\uE1E7\041\147\041\127\042\u4B17\042\007\042\u34A7\041"+
    "\u33A7\041\147\041\127\042\u3367\041\u3367\041\147\041\u13E7\041\u32A7\041"+
    "\u32E7\041\147\041\u33E7\041\007\042\u34E7\041\u3467\041\007\042\007\042\u34E7"+
    "\041\u3567\041\007\042\u35A7\041\007\041\147\041\127\042\u36A7\041\007\045"+
    "\007\042\u36A7\041\147\041\127\042\u3667\041\u3667\041\147\041\127\042\u36E7"+
    "\041\007\042\007\045\007\045\007\045\257\041\177\043\237\042\257\041\177\043"+
    "\237\042\237\042\147\041\127\042\u13D7\042\007\042\257\041\000\000\000\000"+
    "\007\042\u3497\042\u3397\042\007\042\u3357\042\u3357\042\007\042\u3297\042"+
    "\007\042\u32D7\042\u3357\042\007\042\007\042\u33D7\042\u3457\042\u34D7\042"+
    "\007\042\u34D7\042\u3557\042\007\042\007\042\uCAA7\042\007\042\u3697\042\u3697"+
    "\042\007\042\u3657\042\u3657\042\u36D7\042\007\042\007\042\000\000\007\044"+
    "\007\044\007\044\000\073\000\073\007\044\000\073\000\073\000\073\000\000\003"+
    "\046\003\046\000\070\000\070\007\044\000\000\000\070\000\000\u09A7\041\000"+
    "\070\u0967\041\u0967\041\u0967\041\000\000\u1027\041\000\000\u0FE7\041\u0FE7"+
    "\041\007\042\u0827\041\000\000\u0827\041\u0997\042\u0957\042\u0957\042\u0957"+
    "\042\007\042\u0817\042\u07D7\042\u0817\042\u1017\042\u0FD7\042\u0FD7\042\000"+
    "\000\u0F97\042\u0E57\042\007\041\007\041\007\041\u0BD7\042\u0D97\042\000\000"+
    "\007\041\000\000\u1597\042\u1417\042\000\000\u1427\041\u1427\041\u1427\041"+
    "\u1427\041\000\000\000\000\u1417\042\u1417\042\u1417\042\u1417\042\000\000"+
    "\000\074\003\046\003\046\000\000\007\045\147\041\127\042\000\000\000\000\147"+
    "\041\000\000\u0C27\041\u0C27\041\u0C27\041\u0C27\041\000\000\000\000\007\044"+
    "\000\000\u0C17\042\u0C17\042\u0C17\042\u0C17\042\007\042\000\000\000\070\000"+
    "\000\003\106\003\106\003\106\000\130\003\106\003\106\000\130\003\106\000\000"+
    "\007\105\007\105\007\105\000\000\007\105\000\130\000\130\000\000\000\000\000"+
    "\130\000\000\007\105\007\104\007\105\007\105\003\106\003\u40C9\003\u40C9\000"+
    "\270\000\330\000\330\000\130\003\106\007\105\000\130\007\105\003\106\000\107"+
    "\000\107\003\106\003\106\007\104\007\104\003\106\003\106\000\134\000\000\003"+
    "\046\003\046\003\050\000\000\007\045\003\046\007\045\003\050\003\050\003\050"+
    "\003\046\003\u7429\003\u7429\007\045\000\000\000\000\003\050\003\050\000\000"+
    "\006\072\006\072\000\u5A2B\000\u5A2B\000\u802B\000\u6E2B\000\074\000\000\000"+
    "\000\003\u7429\000\u742B\000\u802B\000\u802B\000\000\007\045\000\070\007\045"+
    "\003\046\000\000\006\072\007\044\003\046\003\046\000\074\003\u6029\003\u6029"+
    "\000\074\000\074\000\070\000\074\003\u4029\003\u4029\000\053\000\053\000\065"+
    "\000\066\003\046\000\070\007\042\u0ED7\042\uFE17\042\uFE17\042\uFE27\041\uFE27"+
    "\041\007\042\uFE17\042\000\000\uFE27\041\uED97\042\uED97\042\uEA97\042\uEA97"+
    "\042\uE717\042\uE717\042\uE017\042\uE017\042\uE417\042\uE417\042\uE097\042"+
    "\uE097\042\007\042\uFDD7\042\uEDA7\041\uEDA7\041\uFDE7\041\000\073\007\041"+
    "\000\073\uEAA7\041\uEAA7\041\uE727\041\uE727\041\000\000\000\073\007\042\uFE57"+
    "\042\uE427\041\uE427\041\uFE67\041\000\073\uE027\041\uE027\041\uE0A7\041\uE0A7"+
    "\041\004\u014C\004\u014C\004\u014C\004\354\001\u0190\001\u0190\001\060\001"+
    "\120\000\u0194\000\u0194\000\u0195\000\u0196\000\u0195\000\u0195\004\u010D"+
    "\004\u010E\001\u0190\000\000\000\270\000\270\000\270\000\u0198\000\u0198\000"+
    "\u0195\000\u0196\000\u0198\000\u0198\005\u0197\005\u0197\000\u0198\000\u0199"+
    "\000\u0175\000\u0176\000\000\000\u606B\000\000\000\271\000\271\000\u0176\007"+
    "\u0182\000\u406B\000\u406B\006\272\000\000\003\046\000\047\000\047\000\047"+
    "\000\047\003\046\007\u0181\000\u019C\000\u019C\007\u0181\007\u0182\007\u0181"+
    "\007\u0181\007\u0181\007\u0182\007\u0182\007\u0181\007\u0182\007\u0182\007"+
    "\u0185\007\u0185\007\u0185\007\u0185\000\000\000\000\000\u818B\000\u818B\000"+
    "\u458B\u0427\u422A\u0427\u422A\u0427\u802A\u0427\u802A\u0417\u622A\u0417\u622A"+
    "\u0417\u802A\u0417\u802A\007\u802A\007\u802A\007\u802A\000\000\000\u0199\000"+
    "\u0199\000\u0199\000\u019C\000\u019C\000\000\000\u0199\000\u0179\000\u0179"+
    "\000\u0179\000\u019C\000\u0175\000\u0176\000\u019C\000\u438B\000\u438B\000"+
    "\u5B8B\000\u5B8B\000\u738B\000\u738B\u06A0\u019C\u06A0\u019C\u0690\u019C\u0690"+
    "\u019C\000\u6D8B\000\000\000\000\000\u019C\000\u578B\000\u578B\000\u6F8B\000"+
    "\u6F8B\000\u019C\007\u0184\000\u0198\007\u738A\000\u0194\000\u0195\000\u0196"+
    "\000\u0196\000\u019C\007\u402A\007\u402A\007\u402A\000\u0194\007\u0184\007"+
    "\u0184\007\u0184\003\046\007\044\000\000\000\074\000\u422B\000\u422B\000\063"+
    "\000\063\000\062\000\062\000\000\007\042\007\105\000\131\003\u0186\003\u0186"+
    "\000\u0198\000\u0194\000\u0194\005\u0197\005\u0197\000\u0195\000\u0196\000"+
    "\u0195\000\u0196\000\000\000\000\000\u0198\005\u0197\005\u0197\000\u0198\000"+
    "\000\000\u0199\000\000\000\u0198\006\u019A\000\000\001\u0190\006\u019A\000"+
    "\u0198\000\u0198\000\u0199\000\u0199\000\u0198\u0827\uFE21\000\u0195\000\u0198"+
    "\000\u0196\u0817\uFE22\000\u0195\000\u0199\000\u0196\000\u0198\000\070\007"+
    "\044\007\045\006\u019A\006\u019A\000\u0199\000\u019B\000\u019C\006\u019A\006"+
    "\u019A\000\000";

  // In all, the character property tables require 11616 bytes.

    static {
        { // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
            int len = X_DATA.length();
            int j=0;
            for (int i=0; i<len; ++i) {
                int c = X_DATA.charAt(i);
                for (int k=0; k<2; ++k) {
                    X[j++] = (byte)c;
                    c >>= 8;
                }
            }
            if (j != 1024) throw new RuntimeException();
        }
        { // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
            if (Y_DATA.length() != 4032) throw new RuntimeException();
            for (int i=0; i<4032; ++i)
                Y[i] = (short)Y_DATA.charAt(i);
        }
        { // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
            int len = A_DATA.length();
            int j=0;
            int charsInEntry=0;
            int entry=0;
            for (int i=0; i<len; ++i) {
                entry |= A_DATA.charAt(i);
                if (++charsInEntry == 2) {
                    A[j++] = entry;
                    entry = 0;
                    charsInEntry = 0;
                }
                else {
                    entry <<= 16;
                }
            }
            if (j != 632) throw new RuntimeException();
        }

    }
}
