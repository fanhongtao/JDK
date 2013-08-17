/* @(#)Character.java	1.42 97/01/30
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 *  */

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
 * @version 1.42 01/30/97
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
    public static final Class	TYPE = Class.getPrimitiveClass("char");
    
    /*
     * Public data for enumerated Unicode general category types
     *
     * @since   JDK1.1
     */
    public static final byte
	UNASSIGNED		= 0,
	UPPERCASE_LETTER	= 1,
	LOWERCASE_LETTER	= 2,
	TITLECASE_LETTER	= 3,
	MODIFIER_LETTER		= 4,
	OTHER_LETTER		= 5,
	NON_SPACING_MARK	= 6,
	ENCLOSING_MARK		= 7,
	COMBINING_SPACING_MARK	= 8,
	DECIMAL_DIGIT_NUMBER	= 9,
	LETTER_NUMBER		= 10,
	OTHER_NUMBER		= 11,
	SPACE_SEPARATOR		= 12,
	LINE_SEPARATOR		= 13,
	PARAGRAPH_SEPARATOR	= 14,
	CONTROL			= 15,
	FORMAT			= 16,
	PRIVATE_USE		= 18,
	SURROGATE		= 19,
	DASH_PUNCTUATION	= 20,
	START_PUNCTUATION	= 21,
	END_PUNCTUATION		= 22,
	CONNECTOR_PUNCTUATION	= 23,
	OTHER_PUNCTUATION	= 24,
	MATH_SYMBOL		= 25,
	CURRENCY_SYMBOL		= 26,
	MODIFIER_SYMBOL		= 27,
	OTHER_SYMBOL		= 28;

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
	return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F) == LOWERCASE_LETTER;
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
	return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F) == UPPERCASE_LETTER;
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
	return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F) == TITLECASE_LETTER;
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
	return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F) == DECIMAL_DIGIT_NUMBER;
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
	return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F) != UNASSIGNED;
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
		  >> (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F)) & 1) != 0);
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
		  >> (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F)) & 1) != 0);
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
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00070000) >= 0x00050000;
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
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00030000) != 0;
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
     * @param   ch	the character to be tested.
     * @return  true if the character may start a Java identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   JDK1.1
     */
    public static boolean isJavaIdentifierStart(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00070000) >= 0x00050000;
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
     * @param   ch	the character to be tested.
     * @return  true if the character may be part of a Unicode identifier; 
     *          false otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isJavaIdentifierPart(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00030000) != 0;
    }

    /**
     * Determines if the specified character is
     * permissible as the first character in a Unicode identifier.
     * A character may start a Unicode identifier if and only if
     * it is a letter.
     *
     * @param   ch	the character to be tested.
     * @return  true if the character may start a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isUnicodeIdentifierStart(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00070000) == 0x00070000;
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
     * @param   ch	the character to be tested.
     * @return  true if the character may be part of a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   JDK1.1
     */
    public static boolean isUnicodeIdentifierPart(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00010000) != 0;
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
     * @param   ch	the character to be tested.
     * @return 	true if the character may be part of a Unicode identifier;
     *          false otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   JDK1.1
     */
    public static boolean isIdentifierIgnorable(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00070000) == 0x00010000;
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
	int val = A[Y[(X[ch>>6]<<6)|(ch&0x3F)]];
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
	int val = A[Y[(X[ch>>6]<<6)|(ch&0x3F)]];
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
	int val = A[Y[(X[ch>>6]<<6)|(ch&0x3F)]];
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
	  int val = A[Y[(X[ch>>6]<<6)|(ch&0x3F)]];
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
     * @param   ch	the character to be converted.
     * @param   radix 	the radix.
     * @return  the numeric value of the character, as a nonnegative int value;
     *          -2 if the character has a numeric value that is not a
     *          nonnegative integer; -1 if the character has no numeric value.
     * @see     java.lang.Character#forDigit(char)
     * @see     java.lang.Character#isDigit(char)
     * @since   JDK1.1
     */
    public static int getNumericValue(char ch) {
	int val = A[Y[(X[ch>>6]<<6)|(ch&0x3F)]];
	switch ((val >> 14) & 0x3) {
	default: // cannot occur
	case (0x00000000 >> 14):		// not numeric
	    return -1;
	case (0x00004000 >> 14):		// simple numeric
	    return (ch + (val >> 9)) & 0x1F;
	case (0x00008000 >> 14)	:	// "strange" numeric
	    switch (ch) {
	    case '\u0BF1': return 100;		// TAMIL NUMBER ONE HUNDRED
	    case '\u0BF2': return 1000;		// TAMIL NUMBER ONE THOUSAND
	    case '\u216C': return 50;		// ROMAN NUMERAL FIFTY
	    case '\u216D': return 100;		// ROMAN NUMERAL ONE HUNDRED
	    case '\u216E': return 500;		// ROMAN NUMERAL FIVE HUNDRED
	    case '\u216F': return 1000;		// ROMAN NUMERAL ONE THOUSAND
	    case '\u217C': return 50;		// SMALL ROMAN NUMERAL FIFTY
	    case '\u217D': return 100;		// SMALL ROMAN NUMERAL ONE HUNDRED
	    case '\u217E': return 500;		// SMALL ROMAN NUMERAL FIVE HUNDRED
	    case '\u217F': return 1000;		// SMALL ROMAN NUMERAL ONE THOUSAND
	    case '\u2180': return 1000;		// ROMAN NUMERAL ONE THOUSAND C D
	    case '\u2181': return 5000;		// ROMAN NUMERAL FIVE THOUSAND
	    case '\u2182': return 10000;	// ROMAN NUMERAL TEN THOUSAND
	    default:	   return -2;
	    }
	case (0x0000C000 >> 14):		// Java supradecimal
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
     * @param   ch	the character to be tested.
     * @return 	true if the character is a space character; false otherwise.
     * @see     java.lang.Character#isWhitespace(char)
     * @since   JDK1.1
     */
    public static boolean isSpaceChar(char ch) {
	return (((((1 << SPACE_SEPARATOR) |
		   (1 << LINE_SEPARATOR) |
		   (1 << PARAGRAPH_SEPARATOR))
		  >> (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F)) & 1) != 0);
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
     * @param   ch	the character to be tested.
     * @return  true if the character is a Java whitespace character;
     *          false otherwise.
     * @see     java.lang.Character#isSpaceChar(char)
     * @since   JDK1.1
     */
    public static boolean isWhitespace(char ch) {
      return (A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x00070000) == 0x00040000;
    }

    /**
     * Determines if the specified character is an ISO control character.
     * A character is considered to be an ISO control character if its
     * code is in the range &#92;u0000 through &#92;u001F or in the range
     * &#92;u007F through &#92;u009F.
     *
     * @param   ch	the character to be tested.
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
        return A[Y[(X[ch>>6]<<6)|(ch&0x3F)]] & 0x1F;
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
       10 bits	signed offset used for converting case
	1 bit	if 1, adding the signed offset converts the character to lowercase
	1 bit	if 1, subtracting the signed offset converts the character to uppercase
	1 bit   if 1, this character has a titlecase equivalent (possibly itself)
	3 bits	0  may not be part of an identifier
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
	2 bits	0  this character has no numeric property
		1  adding the digit offset to the character code and then
		   masking with 0x1F will produce the desired numeric value
		2  this character has a "strange" numeric value
		3  a Java supradecimal digit: adding the digit offset to the
		   character code, then masking with 0x1F, then adding 10
		   will produce the desired numeric value
	5 bits  digit offset
	4 bits	reserved for future use
	5 bits	character type
     */

    // The X table has 1024 entries for a total of 1024 bytes.

  private static final byte X[] = {
      0,   1,   2,   3,   4,   5,   6,   7,  // 0x0000
      8,   9,  10,  11,  12,  13,  14,  15,  // 0x0200
     16,  17,  18,  19,  20,  21,  22,  23,  // 0x0400
     24,  25,  26,  27,  28,  28,  28,  28,  // 0x0600
     28,  28,  28,  28,  29,  30,  31,  32,  // 0x0800
     33,  34,  35,  36,  37,  38,  39,  40,  // 0x0A00
     41,  42,  43,  44,  45,  46,  28,  28,  // 0x0C00
     47,  48,  49,  50,  51,  52,  53,  28,  // 0x0E00
     28,  28,  54,  55,  56,  57,  58,  59,  // 0x1000
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1200
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1400
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1600
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1800
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1A00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x1C00
     60,  60,  61,  62,  63,  64,  65,  66,  // 0x1E00
     67,  68,  69,  70,  71,  72,  73,  74,  // 0x2000
     75,  75,  75,  76,  77,  78,  28,  28,  // 0x2200
     79,  80,  81,  82,  83,  83,  84,  85,  // 0x2400
     86,  85,  28,  28,  87,  88,  89,  28,  // 0x2600
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x2800
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x2A00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x2C00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x2E00
     90,  91,  92,  93,  94,  56,  95,  28,  // 0x3000
     96,  97,  98,  99,  83, 100,  83, 101,  // 0x3200
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3400
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3600
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3800
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3A00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3C00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x3E00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4000
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4200
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4400
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4600
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4800
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4A00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0x4C00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x4E00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5A00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5C00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x5E00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6A00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6C00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x6E00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7A00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7C00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x7E00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8A00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8C00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x8E00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9A00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0x9C00
     56,  56,  56,  56,  56,  56, 102,  28,  // 0x9E00
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xA000
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xA200
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xA400
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xA600
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xA800
     28,  28,  28,  28,  28,  28,  28,  28,  // 0xAA00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xAC00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xAE00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xB000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xB200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xB400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xB600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xB800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xBA00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xBC00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xBE00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xC000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xC200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xC400
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xC600
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xC800
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xCA00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xCC00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xCE00
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xD000
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xD200
     56,  56,  56,  56,  56,  56,  56,  56,  // 0xD400
     56,  56,  56,  56,  56,  56, 103,  28,  // 0xD600
    104, 104, 104, 104, 104, 104, 104, 104,  // 0xD800
    104, 104, 104, 104, 104, 104, 104, 104,  // 0xDA00
    104, 104, 104, 104, 104, 104, 104, 104,  // 0xDC00
    104, 104, 104, 104, 104, 104, 104, 104,  // 0xDE00
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xE000
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xE200
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xE400
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xE600
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xE800
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xEA00
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xEC00
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xEE00
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xF000
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xF200
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xF400
    105, 105, 105, 105, 105, 105, 105, 105,  // 0xF600
    105, 105, 105, 105,  56,  56,  56,  56,  // 0xF800
    106,  28,  28,  28, 107, 108, 109, 110,  // 0xFA00
     56,  56,  56,  56, 111, 112, 113, 114,  // 0xFC00
    115, 116,  56, 117, 118, 119, 120, 121   // 0xFE00
  };

  // The Y table has 7808 entries for a total of 7808 bytes.

  private static final byte Y[] = {
      0,   0,   0,   0,   0,   0,   0,   0,  //   0
      0,   1,   1,   1,   1,   1,   0,   0,  //   0
      0,   0,   0,   0,   0,   0,   0,   0,  //   0
      0,   0,   0,   0,   1,   1,   1,   1,  //   0
      2,   3,   3,   3,   4,   3,   3,   3,  //   0
      5,   6,   3,   7,   3,   8,   3,   3,  //   0
      9,   9,   9,   9,   9,   9,   9,   9,  //   0
      9,   9,   3,   3,   7,   7,   7,   3,  //   0
      3,  10,  10,  10,  10,  10,  10,  10,  //   1
     10,  10,  10,  10,  10,  10,  10,  10,  //   1
     10,  10,  10,  10,  10,  10,  10,  10,  //   1
     10,  10,  10,   5,   3,   6,  11,  12,  //   1
     11,  13,  13,  13,  13,  13,  13,  13,  //   1
     13,  13,  13,  13,  13,  13,  13,  13,  //   1
     13,  13,  13,  13,  13,  13,  13,  13,  //   1
     13,  13,  13,   5,   7,   6,   7,   0,  //   1
      0,   0,   0,   0,   0,   0,   0,   0,  //   2
      0,   0,   0,   0,   0,   0,   0,   0,  //   2
      0,   0,   0,   0,   0,   0,   0,   0,  //   2
      0,   0,   0,   0,   0,   0,   0,   0,  //   2
     14,   3,   4,   4,   4,   4,  15,  15,  //   2
     11,  15,  16,   5,   7,   8,  15,  11,  //   2
     15,   7,  17,  17,  11,  16,  15,   3,  //   2
     11,  18,  16,   6,  19,  19,  19,   3,  //   2
     20,  20,  20,  20,  20,  20,  20,  20,  //   3
     20,  20,  20,  20,  20,  20,  20,  20,  //   3
     20,  20,  20,  20,  20,  20,  20,   7,  //   3
     20,  20,  20,  20,  20,  20,  20,  16,  //   3
     21,  21,  21,  21,  21,  21,  21,  21,  //   3
     21,  21,  21,  21,  21,  21,  21,  21,  //   3
     21,  21,  21,  21,  21,  21,  21,   7,  //   3
     21,  21,  21,  21,  21,  21,  21,  22,  //   3
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     23,  24,  23,  24,  23,  24,  23,  24,  //   4
     25,  26,  23,  24,  23,  24,  23,  24,  //   4
     16,  23,  24,  23,  24,  23,  24,  23,  //   4
     24,  23,  24,  23,  24,  23,  24,  23,  //   5
     24,  16,  23,  24,  23,  24,  23,  24,  //   5
     23,  24,  23,  24,  23,  24,  23,  24,  //   5
     23,  24,  23,  24,  23,  24,  23,  24,  //   5
     23,  24,  23,  24,  23,  24,  23,  24,  //   5
     23,  24,  23,  24,  23,  24,  23,  24,  //   5
     23,  24,  23,  24,  23,  24,  23,  24,  //   5
     27,  23,  24,  23,  24,  23,  24,  28,  //   5
     16,  29,  23,  24,  23,  24,  30,  23,  //   6
     24,  31,  31,  23,  24,  16,  32,  32,  //   6
     33,  23,  24,  31,  34,  16,  35,  36,  //   6
     23,  24,  16,  16,  35,  37,  16,  38,  //   6
     23,  24,  23,  24,  23,  24,  38,  23,  //   6
     24,  39,  40,  16,  23,  24,  39,  23,  //   6
     24,  41,  41,  23,  24,  23,  24,  42,  //   6
     23,  24,  16,  40,  23,  24,  40,  40,  //   6
     40,  40,  40,  40,  43,  44,  45,  43,  //   7
     44,  45,  43,  44,  45,  23,  24,  23,  //   7
     24,  23,  24,  23,  24,  23,  24,  23,  //   7
     24,  23,  24,  23,  24,  16,  23,  24,  //   7
     23,  24,  23,  24,  23,  24,  23,  24,  //   7
     23,  24,  23,  24,  23,  24,  23,  24,  //   7
     16,  43,  44,  45,  23,  24,  46,  46,  //   7
     46,  46,  23,  24,  23,  24,  23,  24,  //   7
     23,  24,  23,  24,  23,  24,  23,  24,  //   8
     23,  24,  23,  24,  23,  24,  23,  24,  //   8
     23,  24,  23,  24,  23,  24,  23,  24,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   8
     46,  46,  46,  46,  46,  46,  46,  46,  //   9
     46,  46,  46,  46,  46,  46,  46,  46,  //   9
     16,  16,  16,  47,  48,  16,  49,  49,  //   9
     50,  50,  16,  51,  16,  16,  16,  16,  //   9
     49,  16,  16,  52,  16,  16,  16,  16,  //   9
     53,  54,  16,  16,  16,  16,  16,  54,  //   9
     16,  16,  55,  16,  16,  16,  16,  16,  //   9
     16,  16,  16,  16,  16,  16,  16,  16,  //   9
     16,  16,  16,  56,  16,  16,  16,  16,  //  10
     56,  16,  57,  57,  16,  16,  16,  16,  //  10
     16,  16,  58,  16,  16,  16,  16,  16,  //  10
     16,  16,  16,  16,  16,  16,  16,  16,  //  10
     16,  16,  16,  16,  16,  16,  16,  16,  //  10
     16,  46,  46,  46,  46,  46,  46,  46,  //  10
     59,  59,  59,  59,  59,  59,  59,  59,  //  10
     59,  11,  11,  59,  59,  59,  59,  59,  //  10
     59,  59,  11,  11,  11,  11,  11,  11,  //  11
     11,  11,  11,  11,  11,  11,  11,  11,  //  11
     59,  59,  11,  11,  11,  11,  11,  11,  //  11
     11,  11,  11,  11,  11,  11,  11,  46,  //  11
     59,  59,  59,  59,  59,  11,  11,  11,  //  11
     11,  11,  46,  46,  46,  46,  46,  46,  //  11
     46,  46,  46,  46,  46,  46,  46,  46,  //  11
     46,  46,  46,  46,  46,  46,  46,  46,  //  11
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  60,  60,  //  12
     60,  60,  60,  60,  60,  60,  46,  46,  //  13
     46,  46,  46,  46,  46,  46,  46,  46,  //  13
     46,  46,  46,  46,  46,  46,  46,  46,  //  13
     46,  46,  46,  46,  46,  46,  46,  46,  //  13
     60,  60,  46,  46,  46,  46,  46,  46,  //  13
     46,  46,  46,  46,  46,  46,  46,  46,  //  13
     46,  46,  46,  46,   3,   3,  46,  46,  //  13
     46,  46,  59,  46,  46,  46,   3,  46,  //  13
     46,  46,  46,  46,  11,  11,  61,   3,  //  14
     62,  62,  62,  46,  63,  46,  64,  64,  //  14
     16,  20,  20,  20,  20,  20,  20,  20,  //  14
     20,  20,  20,  20,  20,  20,  20,  20,  //  14
     20,  20,  46,  20,  20,  20,  20,  20,  //  14
     20,  20,  20,  20,  65,  66,  66,  66,  //  14
     16,  21,  21,  21,  21,  21,  21,  21,  //  14
     21,  21,  21,  21,  21,  21,  21,  21,  //  14
     21,  21,  16,  21,  21,  21,  21,  21,  //  15
     21,  21,  21,  21,  67,  68,  68,  46,  //  15
     69,  70,  38,  38,  38,  71,  72,  46,  //  15
     46,  46,  38,  46,  38,  46,  38,  46,  //  15
     38,  46,  23,  24,  23,  24,  23,  24,  //  15
     23,  24,  23,  24,  23,  24,  23,  24,  //  15
     73,  74,  16,  40,  46,  46,  46,  46,  //  15
     46,  46,  46,  46,  46,  46,  46,  46,  //  15
     46,  75,  75,  75,  75,  75,  75,  75,  //  16
     75,  75,  75,  75,  75,  46,  75,  75,  //  16
     20,  20,  20,  20,  20,  20,  20,  20,  //  16
     20,  20,  20,  20,  20,  20,  20,  20,  //  16
     20,  20,  20,  20,  20,  20,  20,  20,  //  16
     20,  20,  20,  20,  20,  20,  20,  20,  //  16
     21,  21,  21,  21,  21,  21,  21,  21,  //  16
     21,  21,  21,  21,  21,  21,  21,  21,  //  16
     21,  21,  21,  21,  21,  21,  21,  21,  //  17
     21,  21,  21,  21,  21,  21,  21,  21,  //  17
     46,  74,  74,  74,  74,  74,  74,  74,  //  17
     74,  74,  74,  74,  74,  46,  74,  74,  //  17
     23,  24,  23,  24,  23,  24,  23,  24,  //  17
     23,  24,  23,  24,  23,  24,  23,  24,  //  17
     23,  24,  23,  24,  23,  24,  23,  24,  //  17
     23,  24,  23,  24,  23,  24,  23,  24,  //  17
     23,  24,  15,  60,  60,  60,  60,  46,  //  18
     46,  46,  46,  46,  46,  46,  46,  46,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     23,  24,  23,  24,  23,  24,  23,  24,  //  18
     40,  23,  24,  23,  24,  46,  46,  23,  //  19
     24,  46,  46,  23,  24,  46,  46,  46,  //  19
     23,  24,  23,  24,  23,  24,  23,  24,  //  19
     23,  24,  23,  24,  23,  24,  23,  24,  //  19
     23,  24,  23,  24,  23,  24,  23,  24,  //  19
     23,  24,  23,  24,  46,  46,  23,  24,  //  19
     23,  24,  23,  24,  23,  24,  46,  46,  //  19
     23,  24,  46,  46,  46,  46,  46,  46,  //  19
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  46,  46,  46,  46,  46,  46,  46,  //  20
     46,  76,  76,  76,  76,  76,  76,  76,  //  20
     76,  76,  76,  76,  76,  76,  76,  76,  //  20
     76,  76,  76,  76,  76,  76,  76,  76,  //  21
     76,  76,  76,  76,  76,  76,  76,  76,  //  21
     76,  76,  76,  76,  76,  76,  76,  46,  //  21
     46,  59,   3,   3,   3,   3,   3,   3,  //  21
     46,  77,  77,  77,  77,  77,  77,  77,  //  21
     77,  77,  77,  77,  77,  77,  77,  77,  //  21
     77,  77,  77,  77,  77,  77,  77,  77,  //  21
     77,  77,  77,  77,  77,  77,  77,  77,  //  21
     77,  77,  77,  77,  77,  77,  77,  16,  //  22
     46,   3,  46,  46,  46,  46,  46,  46,  //  22
     46,  60,  60,  60,  60,  60,  60,  60,  //  22
     60,  60,  60,  60,  60,  60,  60,  60,  //  22
     60,  60,  46,  60,  60,  60,  60,  60,  //  22
     60,  60,  60,  60,  60,  60,  60,  60,  //  22
     60,  60,  60,  60,  60,  60,  60,  60,  //  22
     60,  60,  46,  60,  60,  60,   3,  60,  //  22
      3,  60,  60,   3,  60,  46,  46,  46,  //  23
     46,  46,  46,  46,  46,  46,  46,  46,  //  23
     40,  40,  40,  40,  40,  40,  40,  40,  //  23
     40,  40,  40,  40,  40,  40,  40,  40,  //  23
     40,  40,  40,  40,  40,  40,  40,  40,  //  23
     40,  40,  40,  46,  46,  46,  46,  46,  //  23
     40,  40,  40,   3,   3,  46,  46,  46,  //  23
     46,  46,  46,  46,  46,  46,  46,  46,  //  23
     46,  46,  46,  46,  46,  46,  46,  46,  //  24
     46,  46,  46,  46,   3,  46,  46,  46,  //  24
     46,  46,  46,  46,  46,  46,  46,  46,  //  24
     46,  46,  46,   3,  46,  46,  46,   3,  //  24
     46,  40,  40,  40,  40,  40,  40,  40,  //  24
     40,  40,  40,  40,  40,  40,  40,  40,  //  24
     40,  40,  40,  40,  40,  40,  40,  40,  //  24
     40,  40,  40,  46,  46,  46,  46,  46,  //  24
     59,  40,  40,  40,  40,  40,  40,  40,  //  25
     40,  40,  40,  60,  60,  60,  60,  60,  //  25
     60,  60,  60,  46,  46,  46,  46,  46,  //  25
     46,  46,  46,  46,  46,  46,  46,  46,  //  25
     78,  78,  78,  78,  78,  78,  78,  78,  //  25
     78,  78,   3,   3,   3,   3,  46,  46,  //  25
     60,  40,  40,  40,  40,  40,  40,  40,  //  25
     40,  40,  40,  40,  40,  40,  40,  40,  //  25
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  26
     46,  46,  40,  40,  40,  40,  40,  46,  //  26
     40,  40,  40,  40,  40,  40,  40,  40,  //  27
     40,  40,  40,  40,  40,  40,  40,  46,  //  27
     40,  40,  40,  40,   3,  40,  60,  60,  //  27
     60,  60,  60,  60,  60,  79,  79,  60,  //  27
     60,  60,  60,  60,  60,  59,  59,  60,  //  27
     60,  15,  60,  60,  60,  60,  46,  46,  //  27
      9,   9,   9,   9,   9,   9,   9,   9,  //  27
      9,   9,  46,  46,  46,  46,  46,  46,  //  27
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  46,  46,  46,  46,  46,  46,  46,  //  28
     46,  60,  60,  80,  46,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  40,  40,  40,  40,  40,  40,  //  29
     40,  40,  46,  46,  60,  40,  80,  80,  //  29
     80,  60,  60,  60,  60,  60,  60,  60,  //  30
     60,  80,  80,  80,  80,  60,  46,  46,  //  30
     15,  60,  60,  60,  60,  46,  46,  46,  //  30
     40,  40,  40,  40,  40,  40,  40,  40,  //  30
     40,  40,  60,  60,   3,   3,  81,  81,  //  30
     81,  81,  81,  81,  81,  81,  81,  81,  //  30
      3,  46,  46,  46,  46,  46,  46,  46,  //  30
     46,  46,  46,  46,  46,  46,  46,  46,  //  30
     46,  60,  80,  80,  46,  40,  40,  40,  //  31
     40,  40,  40,  40,  40,  46,  46,  40,  //  31
     40,  46,  46,  40,  40,  40,  40,  40,  //  31
     40,  40,  40,  40,  40,  40,  40,  40,  //  31
     40,  40,  40,  40,  40,  40,  40,  40,  //  31
     40,  46,  40,  40,  40,  40,  40,  40,  //  31
     40,  46,  40,  46,  46,  46,  40,  40,  //  31
     40,  40,  46,  46,  60,  46,  80,  80,  //  31
     80,  60,  60,  60,  60,  46,  46,  80,  //  32
     80,  46,  46,  80,  80,  60,  46,  46,  //  32
     46,  46,  46,  46,  46,  46,  46,  80,  //  32
     46,  46,  46,  46,  40,  40,  46,  40,  //  32
     40,  40,  60,  60,  46,  46,  81,  81,  //  32
     81,  81,  81,  81,  81,  81,  81,  81,  //  32
     40,  40,   4,   4,  82,  82,  82,  82,  //  32
     19,  83,  15,  46,  46,  46,  46,  46,  //  32
     46,  46,  60,  46,  46,  40,  40,  40,  //  33
     40,  40,  40,  46,  46,  46,  46,  40,  //  33
     40,  46,  46,  40,  40,  40,  40,  40,  //  33
     40,  40,  40,  40,  40,  40,  40,  40,  //  33
     40,  40,  40,  40,  40,  40,  40,  40,  //  33
     40,  46,  40,  40,  40,  40,  40,  40,  //  33
     40,  46,  40,  40,  46,  40,  40,  46,  //  33
     40,  40,  46,  46,  60,  46,  80,  80,  //  33
     80,  60,  60,  46,  46,  46,  46,  60,  //  34
     60,  46,  46,  60,  60,  60,  46,  46,  //  34
     46,  46,  46,  46,  46,  46,  46,  46,  //  34
     46,  40,  40,  40,  40,  46,  40,  46,  //  34
     46,  46,  46,  46,  46,  46,  81,  81,  //  34
     81,  81,  81,  81,  81,  81,  81,  81,  //  34
     60,  60,  40,  40,  40,  46,  46,  46,  //  34
     46,  46,  46,  46,  46,  46,  46,  46,  //  34
     46,  60,  60,  80,  46,  40,  40,  40,  //  35
     40,  40,  40,  40,  46,  40,  46,  40,  //  35
     40,  40,  46,  40,  40,  40,  40,  40,  //  35
     40,  40,  40,  40,  40,  40,  40,  40,  //  35
     40,  40,  40,  40,  40,  40,  40,  40,  //  35
     40,  46,  40,  40,  40,  40,  40,  40,  //  35
     40,  46,  40,  40,  46,  40,  40,  40,  //  35
     40,  40,  46,  46,  60,  40,  80,  80,  //  35
     80,  60,  60,  60,  60,  60,  46,  60,  //  36
     60,  80,  46,  80,  80,  60,  46,  46,  //  36
     15,  46,  46,  46,  46,  46,  46,  46,  //  36
     46,  46,  46,  46,  46,  46,  46,  46,  //  36
     40,  46,  46,  46,  46,  46,  81,  81,  //  36
     81,  81,  81,  81,  81,  81,  81,  81,  //  36
     46,  46,  46,  46,  46,  46,  46,  46,  //  36
     46,  46,  46,  46,  46,  46,  46,  46,  //  36
     46,  60,  80,  80,  46,  40,  40,  40,  //  37
     40,  40,  40,  40,  40,  46,  46,  40,  //  37
     40,  46,  46,  40,  40,  40,  40,  40,  //  37
     40,  40,  40,  40,  40,  40,  40,  40,  //  37
     40,  40,  40,  40,  40,  40,  40,  40,  //  37
     40,  46,  40,  40,  40,  40,  40,  40,  //  37
     40,  46,  40,  40,  46,  46,  40,  40,  //  37
     40,  40,  46,  46,  60,  40,  80,  60,  //  37
     80,  60,  60,  60,  46,  46,  46,  80,  //  38
     80,  46,  46,  80,  80,  60,  46,  46,  //  38
     46,  46,  46,  46,  46,  46,  60,  80,  //  38
     46,  46,  46,  46,  40,  40,  46,  40,  //  38
     40,  40,  46,  46,  46,  46,  81,  81,  //  38
     81,  81,  81,  81,  81,  81,  81,  81,  //  38
     15,  46,  46,  46,  46,  46,  46,  46,  //  38
     46,  46,  46,  46,  46,  46,  46,  46,  //  38
     46,  46,  60,  80,  46,  40,  40,  40,  //  39
     40,  40,  40,  46,  46,  46,  40,  40,  //  39
     40,  46,  40,  40,  40,  40,  46,  46,  //  39
     46,  40,  40,  46,  40,  46,  40,  40,  //  39
     46,  46,  46,  40,  40,  46,  46,  46,  //  39
     40,  40,  40,  46,  46,  46,  40,  40,  //  39
     40,  40,  40,  40,  40,  40,  46,  40,  //  39
     40,  40,  46,  46,  46,  46,  80,  80,  //  39
     60,  80,  80,  46,  46,  46,  80,  80,  //  40
     80,  46,  80,  80,  80,  60,  46,  46,  //  40
     46,  46,  46,  46,  46,  46,  46,  80,  //  40
     46,  46,  46,  46,  46,  46,  46,  46,  //  40
     46,  46,  46,  46,  46,  46,  46,  81,  //  40
     81,  81,  81,  81,  81,  81,  81,  81,  //  40
     84,  19,  19,  46,  46,  46,  46,  46,  //  40
     46,  46,  46,  46,  46,  46,  46,  46,  //  40
     46,  80,  80,  80,  46,  40,  40,  40,  //  41
     40,  40,  40,  40,  40,  46,  40,  40,  //  41
     40,  46,  40,  40,  40,  40,  40,  40,  //  41
     40,  40,  40,  40,  40,  40,  40,  40,  //  41
     40,  40,  40,  40,  40,  40,  40,  40,  //  41
     40,  46,  40,  40,  40,  40,  40,  40,  //  41
     40,  40,  40,  40,  46,  40,  40,  40,  //  41
     40,  40,  46,  46,  46,  46,  60,  60,  //  41
     60,  80,  80,  80,  80,  46,  60,  60,  //  42
     60,  46,  60,  60,  60,  60,  46,  46,  //  42
     46,  46,  46,  46,  46,  60,  60,  46,  //  42
     46,  46,  46,  46,  46,  46,  46,  46,  //  42
     40,  40,  46,  46,  46,  46,  81,  81,  //  42
     81,  81,  81,  81,  81,  81,  81,  81,  //  42
     46,  46,  46,  46,  46,  46,  46,  46,  //  42
     46,  46,  46,  46,  46,  46,  46,  46,  //  42
     46,  46,  80,  80,  46,  40,  40,  40,  //  43
     40,  40,  40,  40,  40,  46,  40,  40,  //  43
     40,  46,  40,  40,  40,  40,  40,  40,  //  43
     40,  40,  40,  40,  40,  40,  40,  40,  //  43
     40,  40,  40,  40,  40,  40,  40,  40,  //  43
     40,  46,  40,  40,  40,  40,  40,  40,  //  43
     40,  40,  40,  40,  46,  40,  40,  40,  //  43
     40,  40,  46,  46,  46,  46,  80,  60,  //  43
     80,  80,  80,  80,  80,  46,  60,  80,  //  44
     80,  46,  80,  80,  60,  60,  46,  46,  //  44
     46,  46,  46,  46,  46,  80,  80,  46,  //  44
     46,  46,  46,  46,  46,  46,  40,  46,  //  44
     40,  40,  46,  46,  46,  46,  81,  81,  //  44
     81,  81,  81,  81,  81,  81,  81,  81,  //  44
     46,  46,  46,  46,  46,  46,  46,  46,  //  44
     46,  46,  46,  46,  46,  46,  46,  46,  //  44
     46,  46,  80,  80,  46,  40,  40,  40,  //  45
     40,  40,  40,  40,  40,  46,  40,  40,  //  45
     40,  46,  40,  40,  40,  40,  40,  40,  //  45
     40,  40,  40,  40,  40,  40,  40,  40,  //  45
     40,  40,  40,  40,  40,  40,  40,  40,  //  45
     40,  46,  40,  40,  40,  40,  40,  40,  //  45
     40,  40,  40,  40,  40,  40,  40,  40,  //  45
     40,  40,  46,  46,  46,  46,  80,  80,  //  45
     80,  60,  60,  60,  46,  46,  80,  80,  //  46
     80,  46,  80,  80,  80,  60,  46,  46,  //  46
     46,  46,  46,  46,  46,  46,  46,  80,  //  46
     46,  46,  46,  46,  46,  46,  46,  46,  //  46
     40,  40,  46,  46,  46,  46,  81,  81,  //  46
     81,  81,  81,  81,  81,  81,  81,  81,  //  46
     46,  46,  46,  46,  46,  46,  46,  46,  //  46
     46,  46,  46,  46,  46,  46,  46,  46,  //  46
     46,  40,  40,  40,  40,  40,  40,  40,  //  47
     40,  40,  40,  40,  40,  40,  40,  40,  //  47
     40,  40,  40,  40,  40,  40,  40,  40,  //  47
     40,  40,  40,  40,  40,  40,  40,  40,  //  47
     40,  40,  40,  40,  40,  40,  40,  40,  //  47
     40,  40,  40,  40,  40,  40,  40,   3,  //  47
     40,  60,  40,  40,  60,  60,  60,  60,  //  47
     60,  60,  60,  46,  46,  46,  46,   4,  //  47
     40,  40,  40,  40,  40,  40,  59,  60,  //  48
     60,  60,  60,  60,  60,  60,  60,  15,  //  48
      9,   9,   9,   9,   9,   9,   9,   9,  //  48
      9,   9,   3,   3,  46,  46,  46,  46,  //  48
     46,  46,  46,  46,  46,  46,  46,  46,  //  48
     46,  46,  46,  46,  46,  46,  46,  46,  //  48
     46,  46,  46,  46,  46,  46,  46,  46,  //  48
     46,  46,  46,  46,  46,  46,  46,  46,  //  48
     46,  40,  40,  46,  40,  46,  46,  40,  //  49
     40,  46,  40,  46,  46,  40,  46,  46,  //  49
     46,  46,  46,  46,  40,  40,  40,  40,  //  49
     46,  40,  40,  40,  40,  40,  40,  40,  //  49
     46,  40,  40,  40,  46,  40,  46,  40,  //  49
     46,  46,  40,  40,  46,  40,  40,   3,  //  49
     40,  60,  40,  40,  60,  60,  60,  60,  //  49
     60,  60,  46,  60,  60,  40,  46,  46,  //  49
     40,  40,  40,  40,  40,  46,  59,  46,  //  50
     60,  60,  60,  60,  60,  60,  46,  46,  //  50
      9,   9,   9,   9,   9,   9,   9,   9,  //  50
      9,   9,  46,  46,  40,  40,  46,  46,  //  50
     46,  46,  46,  46,  46,  46,  46,  46,  //  50
     46,  46,  46,  46,  46,  46,  46,  46,  //  50
     46,  46,  46,  46,  46,  46,  46,  46,  //  50
     46,  46,  46,  46,  46,  46,  46,  46,  //  50
     15,  15,  15,  15,   3,   3,   3,   3,  //  51
      3,   3,   3,   3,   3,   3,   3,   3,  //  51
      3,   3,   3,  15,  15,  15,  15,  15,  //  51
     60,  60,  15,  15,  15,  15,  15,  15,  //  51
     78,  78,  78,  78,  78,  78,  78,  78,  //  51
     78,  78,  85,  85,  85,  85,  85,  85,  //  51
     85,  85,  85,  85,  15,  60,  15,  60,  //  51
     15,  60,   5,   6,   5,   6,  80,  80,  //  51
     40,  40,  40,  40,  40,  40,  40,  40,  //  52
     46,  40,  40,  40,  40,  40,  40,  40,  //  52
     40,  40,  40,  40,  40,  40,  40,  40,  //  52
     40,  40,  40,  40,  40,  40,  40,  40,  //  52
     40,  40,  40,  40,  40,  40,  40,  40,  //  52
     40,  40,  46,  46,  46,  46,  46,  46,  //  52
     46,  60,  60,  60,  60,  60,  60,  60,  //  52
     60,  60,  60,  60,  60,  60,  60,  80,  //  52
     60,  60,  60,  60,  60,   3,  60,  60,  //  53
     60,  60,  60,  60,  46,  46,  46,  46,  //  53
     60,  60,  60,  60,  60,  60,  46,  60,  //  53
     46,  60,  60,  60,  60,  60,  60,  60,  //  53
     60,  60,  60,  60,  60,  60,  60,  60,  //  53
     60,  60,  60,  60,  60,  60,  46,  46,  //  53
     46,  60,  60,  60,  60,  60,  60,  60,  //  53
     46,  60,  46,  46,  46,  46,  46,  46,  //  53
     46,  46,  46,  46,  46,  46,  46,  46,  //  54
     46,  46,  46,  46,  46,  46,  46,  46,  //  54
     46,  46,  46,  46,  46,  46,  46,  46,  //  54
     46,  46,  46,  46,  46,  46,  46,  46,  //  54
     76,  76,  76,  76,  76,  76,  76,  76,  //  54
     76,  76,  76,  76,  76,  76,  76,  76,  //  54
     76,  76,  76,  76,  76,  76,  76,  76,  //  54
     76,  76,  76,  76,  76,  76,  76,  76,  //  54
     76,  76,  76,  76,  76,  76,  46,  46,  //  55
     46,  46,  46,  46,  46,  46,  46,  46,  //  55
     16,  16,  16,  16,  16,  16,  16,  16,  //  55
     16,  16,  16,  16,  16,  16,  16,  16,  //  55
     16,  16,  16,  16,  16,  16,  16,  16,  //  55
     16,  16,  16,  16,  16,  16,  16,  16,  //  55
     16,  16,  16,  16,  16,  16,  16,  46,  //  55
     46,  46,  46,   3,  46,  46,  46,  46,  //  55
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  56
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  46,  46,  46,  46,  46,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  57
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  46,  46,  46,  46,  46,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  58
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  40,  40,  40,  40,  40,  40,  //  59
     40,  40,  46,  46,  46,  46,  46,  46,  //  59
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  60
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  16,  16,  //  61
     16,  16,  16,  16,  46,  46,  46,  46,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  61
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  23,  24,  23,  24,  23,  24,  //  62
     23,  24,  46,  46,  46,  46,  46,  46,  //  62
     86,  86,  86,  86,  86,  86,  86,  86,  //  63
     87,  87,  87,  87,  87,  87,  87,  87,  //  63
     86,  86,  86,  86,  86,  86,  46,  46,  //  63
     87,  87,  87,  87,  87,  87,  46,  46,  //  63
     86,  86,  86,  86,  86,  86,  86,  86,  //  63
     87,  87,  87,  87,  87,  87,  87,  87,  //  63
     86,  86,  86,  86,  86,  86,  86,  86,  //  63
     87,  87,  87,  87,  87,  87,  87,  87,  //  63
     86,  86,  86,  86,  86,  86,  46,  46,  //  64
     87,  87,  87,  87,  87,  87,  46,  46,  //  64
     16,  86,  16,  86,  16,  86,  16,  86,  //  64
     46,  87,  46,  87,  46,  87,  46,  87,  //  64
     86,  86,  86,  86,  86,  86,  86,  86,  //  64
     87,  87,  87,  87,  87,  87,  87,  87,  //  64
     88,  88,  89,  89,  89,  89,  90,  90,  //  64
     91,  91,  92,  92,  93,  93,  46,  46,  //  64
     86,  86,  86,  86,  86,  86,  86,  86,  //  65
     87,  87,  87,  87,  87,  87,  87,  87,  //  65
     86,  86,  86,  86,  86,  86,  86,  86,  //  65
     87,  87,  87,  87,  87,  87,  87,  87,  //  65
     86,  86,  86,  86,  86,  86,  86,  86,  //  65
     87,  87,  87,  87,  87,  87,  87,  87,  //  65
     86,  86,  16,  94,  16,  46,  16,  16,  //  65
     87,  87,  95,  95,  96,  11,  38,  11,  //  65
     11,  11,  16,  94,  16,  46,  16,  16,  //  66
     97,  97,  97,  97,  96,  11,  11,  11,  //  66
     86,  86,  16,  16,  46,  46,  16,  16,  //  66
     87,  87,  98,  98,  46,  11,  11,  11,  //  66
     86,  86,  16,  16,  16,  99,  16,  16,  //  66
     87,  87, 100, 100, 101,  11,  11,  11,  //  66
     46,  46,  16,  94,  16,  46,  16,  16,  //  66
    102, 102, 103, 103,  96,  11,  11,  46,  //  66
      2,   2,   2,   2,   2,   2,   2,   2,  //  67
      2,   2,   2,   2, 104, 104, 104, 104,  //  67
      8,   8,   8,   8,   8,   8,   3,   3,  //  67
      5,   6,   5,   5,   5,   6,   5,   5,  //  67
      3,   3,   3,   3,   3,   3,   3,   3,  //  67
    105, 106, 104, 104, 104, 104, 104,  46,  //  67
      3,   3,   3,   3,   3,   3,   3,   3,  //  67
      3,   5,   6,   3,   3,   3,   3,  12,  //  67
     12,   3,   3,   3,   7,   5,   6,  46,  //  68
     46,  46,  46,  46,  46,  46,  46,  46,  //  68
     46,  46,  46,  46,  46,  46,  46,  46,  //  68
     46,  46,  46,  46,  46,  46,  46,  46,  //  68
     46,  46,  46,  46,  46,  46,  46,  46,  //  68
     46,  46, 104, 104, 104, 104, 104, 104,  //  68
     17,  46,  46,  46,  17,  17,  17,  17,  //  68
     17,  17,   7,   7,   7,   5,   6,  16,  //  68
    107, 107, 107, 107, 107, 107, 107, 107,  //  69
    107, 107,   7,   7,   7,   5,   6,  46,  //  69
     46,  46,  46,  46,  46,  46,  46,  46,  //  69
     46,  46,  46,  46,  46,  46,  46,  46,  //  69
      4,   4,   4,   4,   4,   4,   4,   4,  //  69
      4,   4,   4,   4,  46,  46,  46,  46,  //  69
     46,  46,  46,  46,  46,  46,  46,  46,  //  69
     46,  46,  46,  46,  46,  46,  46,  46,  //  69
     46,  46,  46,  46,  46,  46,  46,  46,  //  70
     46,  46,  46,  46,  46,  46,  46,  46,  //  70
     60,  60,  60,  60,  60,  60,  60,  60,  //  70
     60,  60,  60,  60,  60,  79,  79,  79,  //  70
     79,  60,  46,  46,  46,  46,  46,  46,  //  70
     46,  46,  46,  46,  46,  46,  46,  46,  //  70
     46,  46,  46,  46,  46,  46,  46,  46,  //  70
     46,  46,  46,  46,  46,  46,  46,  46,  //  70
     15,  15,  38,  15,  15,  15,  15,  38,  //  71
     15,  15,  16,  38,  38,  38,  16,  16,  //  71
     38,  38,  38,  16,  15,  38,  15,  15,  //  71
     38,  38,  38,  38,  38,  38,  15,  15,  //  71
     15,  15,  15,  15,  38,  15,  38,  15,  //  71
     38,  15,  38,  38,  38,  38,  16,  16,  //  71
     38,  38,  15,  38,  16,  40,  40,  40,  //  71
     40,  46,  46,  46,  46,  46,  46,  46,  //  71
     46,  46,  46,  46,  46,  46,  46,  46,  //  72
     46,  46,  46,  46,  46,  46,  46,  46,  //  72
     46,  46,  46,  19,  19,  19,  19,  19,  //  72
     19,  19,  19,  19,  19,  19,  19, 108,  //  72
    109, 109, 109, 109, 109, 109, 109, 109,  //  72
    109, 109, 109, 109, 110, 110, 110, 110,  //  72
    111, 111, 111, 111, 111, 111, 111, 111,  //  72
    111, 111, 111, 111, 112, 112, 112, 112,  //  72
    113, 113, 113,  46,  46,  46,  46,  46,  //  73
     46,  46,  46,  46,  46,  46,  46,  46,  //  73
      7,   7,   7,   7,   7,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  73
     15,  15,  15,  15,  15,  15,  15,  15,  //  74
     15,  15,  15,  15,  15,  15,  15,  15,  //  74
     15,  15,   7,  15,   7,  15,  15,  15,  //  74
     15,  15,  15,  15,  15,  15,  15,  15,  //  74
     15,  15,  15,  15,  15,  15,  15,  15,  //  74
     15,  15,  15,  46,  46,  46,  46,  46,  //  74
     46,  46,  46,  46,  46,  46,  46,  46,  //  74
     46,  46,  46,  46,  46,  46,  46,  46,  //  74
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  75
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,   7,   7,   7,   7,   7,   7,  //  76
      7,   7,  46,  46,  46,  46,  46,  46,  //  76
     46,  46,  46,  46,  46,  46,  46,  46,  //  76
     15,  46,  15,  15,  15,  15,  15,  15,  //  77
      7,   7,   7,   7,  15,  15,  15,  15,  //  77
     15,  15,  15,  15,  15,  15,  15,  15,  //  77
     15,  15,  15,  15,  15,  15,  15,  15,  //  77
      7,   7,  15,  15,  15,  15,  15,  15,  //  77
     15,   5,   6,  15,  15,  15,  15,  15,  //  77
     15,  15,  15,  15,  15,  15,  15,  15,  //  77
     15,  15,  15,  15,  15,  15,  15,  15,  //  77
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  78
     15,  15,  15,  46,  46,  46,  46,  46,  //  78
     15,  15,  15,  15,  15,  15,  15,  15,  //  79
     15,  15,  15,  15,  15,  15,  15,  15,  //  79
     15,  15,  15,  15,  15,  15,  15,  15,  //  79
     15,  15,  15,  15,  15,  15,  15,  15,  //  79
     15,  15,  15,  15,  15,  46,  46,  46,  //  79
     46,  46,  46,  46,  46,  46,  46,  46,  //  79
     46,  46,  46,  46,  46,  46,  46,  46,  //  79
     46,  46,  46,  46,  46,  46,  46,  46,  //  79
     15,  15,  15,  15,  15,  15,  15,  15,  //  80
     15,  15,  15,  46,  46,  46,  46,  46,  //  80
     46,  46,  46,  46,  46,  46,  46,  46,  //  80
     46,  46,  46,  46,  46,  46,  46,  46,  //  80
    114, 114, 114, 114, 114, 114, 114, 114,  //  80
    114, 114, 114, 114, 114, 114, 114, 114,  //  80
    114, 114, 114, 114,  82,  82,  82,  82,  //  80
     82,  82,  82,  82,  82,  82,  82,  82,  //  80
     82,  82,  82,  82,  82,  82,  82,  82,  //  81
    115, 115, 115, 115, 115, 115, 115, 115,  //  81
    115, 115, 115, 115, 115, 115, 115, 115,  //  81
    115, 115, 115, 115,  15,  15,  15,  15,  //  81
     15,  15,  15,  15,  15,  15,  15,  15,  //  81
     15,  15,  15,  15,  15,  15,  15,  15,  //  81
     15,  15,  15,  15,  15,  15, 116, 116,  //  81
    116, 116, 116, 116, 116, 116, 116, 116,  //  81
    116, 116, 116, 116, 116, 116, 116, 116,  //  82
    116, 116, 116, 116, 116, 116, 116, 116,  //  82
    117, 117, 117, 117, 117, 117, 117, 117,  //  82
    117, 117, 117, 117, 117, 117, 117, 117,  //  82
    117, 117, 117, 117, 117, 117, 117, 117,  //  82
    117, 117, 118,  46,  46,  46,  46,  46,  //  82
     46,  46,  46,  46,  46,  46,  46,  46,  //  82
     46,  46,  46,  46,  46,  46,  46,  46,  //  82
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  83
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  46,  46,  //  84
     46,  46,  46,  46,  46,  46,  46,  46,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  84
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  85
     46,  46,  46,  46,  46,  46,  46,  46,  //  85
     46,  46,  46,  46,  46,  46,  46,  46,  //  85
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  46,  46,  46,  46,  //  86
     46,  46,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     15,  15,  15,  15,  15,  15,  15,  15,  //  86
     46,  15,  15,  15,  15,  46,  15,  15,  //  87
     15,  15,  46,  46,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  87
     46,  15,  15,  15,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  87
     15,  15,  15,  15,  15,  15,  15,  15,  //  88
     15,  15,  15,  15,  46,  15,  46,  15,  //  88
     15,  15,  15,  46,  46,  46,  15,  46,  //  88
     15,  15,  15,  15,  15,  15,  15,  46,  //  88
     46,  15,  15,  15,  15,  15,  15,  15,  //  88
     46,  46,  46,  46,  46,  46,  46,  46,  //  88
     46,  46,  46,  46,  46,  46, 119, 119,  //  88
    119, 119, 119, 119, 119, 119, 119, 119,  //  88
    114, 114, 114, 114, 114, 114, 114, 114,  //  89
    114, 114,  83,  83,  83,  83,  83,  83,  //  89
     83,  83,  83,  83,  15,  46,  46,  46,  //  89
     15,  15,  15,  15,  15,  15,  15,  15,  //  89
     15,  15,  15,  15,  15,  15,  15,  15,  //  89
     15,  15,  15,  15,  15,  15,  15,  15,  //  89
     46,  15,  15,  15,  15,  15,  15,  15,  //  89
     15,  15,  15,  15,  15,  15,  15,  46,  //  89
      2,   3,   3,   3,  15,  59,   3, 120,  //  90
      5,   6,   5,   6,   5,   6,   5,   6,  //  90
      5,   6,  15,  15,   5,   6,   5,   6,  //  90
      5,   6,   5,   6,   8,   5,   6,   5,  //  90
     15, 121, 121, 121, 121, 121, 121, 121,  //  90
    121, 121,  60,  60,  60,  60,  60,  60,  //  90
      8,  59,  59,  59,  59,  59,  15,  15,  //  90
     46,  46,  46,  46,  46,  46,  46,  15,  //  90
     46,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  91
     40,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  46,  46,  46,  //  92
     46,  60,  60,  59,  59,  59,  59,  46,  //  92
     46,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  40,  40,  40,  //  92
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,  40,  40,  40,  40,  40,  //  93
     40,  40,  40,   3,  59,  59,  59,  46,  //  93
     46,  46,  46,  46,  46,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  46,  46,  46,  //  94
     46,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  94
     40,  40,  40,  40,  40,  40,  40,  40,  //  95
     40,  40,  40,  40,  40,  40,  40,  46,  //  95
     15,  15,  85,  85,  85,  85,  15,  15,  //  95
     15,  15,  15,  15,  15,  15,  15,  15,  //  95
     46,  46,  46,  46,  46,  46,  46,  46,  //  95
     46,  46,  46,  46,  46,  46,  46,  46,  //  95
     46,  46,  46,  46,  46,  46,  46,  46,  //  95
     46,  46,  46,  46,  46,  46,  46,  46,  //  95
     15,  15,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  15,  46,  46,  46,  //  96
     85,  85,  85,  85,  85,  85,  85,  85,  //  96
     85,  85,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  15,  15,  15,  15,  //  96
     15,  15,  15,  15,  46,  46,  46,  46,  //  97
     46,  46,  46,  46,  46,  46,  46,  46,  //  97
     46,  46,  46,  46,  46,  46,  46,  46,  //  97
     46,  46,  46,  46,  46,  46,  46,  46,  //  97
     15,  15,  15,  15,  15,  15,  15,  15,  //  97
     15,  15,  15,  15,  15,  15,  15,  15,  //  97
     15,  15,  15,  15,  15,  15,  15,  15,  //  97
     15,  15,  15,  15,  46,  46,  46,  15,  //  97
    114, 114, 114, 114, 114, 114, 114, 114,  //  98
    114, 114,  15,  15,  15,  15,  15,  15,  //  98
     15,  15,  15,  15,  15,  15,  15,  15,  //  98
     15,  15,  15,  15,  15,  15,  15,  15,  //  98
     15,  15,  15,  15,  15,  15,  15,  15,  //  98
     15,  15,  15,  15,  15,  15,  15,  15,  //  98
     15,  46,  46,  46,  46,  46,  46,  46,  //  98
     46,  46,  46,  46,  46,  46,  46,  46,  //  98
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  46,  46,  46,  46,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  //  99
     15,  15,  15,  15,  15,  15,  15,  46,  //  99
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  46,  // 100
     46,  46,  46,  15,  15,  15,  15,  15,  // 100
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  46,  46,  // 101
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  15,  15,  // 101
     15,  15,  15,  15,  15,  15,  15,  46,  // 101
     40,  40,  40,  40,  40,  40,  40,  40,  // 102
     40,  40,  40,  40,  40,  40,  40,  40,  // 102
     40,  40,  40,  40,  40,  40,  40,  40,  // 102
     40,  40,  40,  40,  40,  40,  40,  40,  // 102
     40,  40,  40,  40,  40,  40,  46,  46,  // 102
     46,  46,  46,  46,  46,  46,  46,  46,  // 102
     46,  46,  46,  46,  46,  46,  46,  46,  // 102
     46,  46,  46,  46,  46,  46,  46,  46,  // 102
     40,  40,  40,  40,  40,  40,  40,  40,  // 103
     40,  40,  40,  40,  40,  40,  40,  40,  // 103
     40,  40,  40,  40,  40,  40,  40,  40,  // 103
     40,  40,  40,  40,  40,  40,  40,  40,  // 103
     40,  40,  40,  40,  46,  46,  46,  46,  // 103
     46,  46,  46,  46,  46,  46,  46,  46,  // 103
     46,  46,  46,  46,  46,  46,  46,  46,  // 103
     46,  46,  46,  46,  46,  46,  46,  46,  // 103
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    122, 122, 122, 122, 122, 122, 122, 122,  // 104
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
    123, 123, 123, 123, 123, 123, 123, 123,  // 105
     40,  40,  40,  40,  40,  40,  40,  40,  // 106
     40,  40,  40,  40,  40,  40,  40,  40,  // 106
     40,  40,  40,  40,  40,  40,  40,  40,  // 106
     40,  40,  40,  40,  40,  40,  40,  40,  // 106
     40,  40,  40,  40,  40,  40,  40,  40,  // 106
     40,  40,  40,  40,  40,  40,  46,  46,  // 106
     46,  46,  46,  46,  46,  46,  46,  46,  // 106
     46,  46,  46,  46,  46,  46,  46,  46,  // 106
     16,  16,  16,  16,  16,  16,  16,  46,  // 107
     46,  46,  46,  46,  46,  46,  46,  46,  // 107
     46,  46,  46,  16,  16,  16,  16,  16,  // 107
     46,  46,  46,  46,  46,  46,  60,  40,  // 107
     40,  40,  40,  40,  40,  40,  40,  40,  // 107
     40,   7,  40,  40,  40,  40,  40,  40,  // 107
     40,  40,  40,  40,  40,  40,  40,  46,  // 107
     40,  40,  40,  40,  40,  46,  40,  46,  // 107
     40,  40,  46,  40,  40,  46,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 108
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  40,  40,  40,  40,  40,  40,  // 109
     40,  40,  46,  46,  46,  46,  46,  46,  // 109
     46,  46,  46,  46,  46,  46,  46,  46,  // 109
     46,  46,  46,  46,  46,  46,  46,  46,  // 110
     46,  46,  46,  46,  46,  46,  46,  46,  // 110
     46,  46,  46,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 110
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,  40,  40,  // 111
     40,  40,  40,  40,  40,  40,   5,   6,  // 111
     46,  46,  46,  46,  46,  46,  46,  46,  // 112
     46,  46,  46,  46,  46,  46,  46,  46,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 112
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     46,  46,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 113
     40,  40,  40,  40,  40,  40,  40,  40,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 114
     40,  40,  40,  40,  40,  40,  40,  40,  // 114
     40,  40,  40,  40,  46,  46,  46,  46,  // 114
     46,  46,  46,  46,  46,  46,  46,  46,  // 115
     46,  46,  46,  46,  46,  46,  46,  46,  // 115
     46,  46,  46,  46,  46,  46,  46,  46,  // 115
     46,  46,  46,  46,  46,  46,  46,  46,  // 115
     60,  60,  60,  60,  46,  46,  46,  46,  // 115
     46,  46,  46,  46,  46,  46,  46,  46,  // 115
      3,   8,   8,  12,  12,   5,   6,   5,  // 115
      6,   5,   6,   5,   6,   5,   6,   5,  // 115
      6,   5,   6,   5,   6,  46,  46,  46,  // 116
     46,   3,   3,   3,   3,  12,  12,  12,  // 116
      3,   3,   3,  46,   3,   3,   3,   3,  // 116
      8,   5,   6,   5,   6,   5,   6,   3,  // 116
      3,   3,   7,   8,   7,   7,   7,  46,  // 116
      3,   4,   3,   3,  46,  46,  46,  46,  // 116
     40,  40,  40,  46,  40,  46,  40,  40,  // 116
     40,  40,  40,  40,  40,  40,  40,  40,  // 116
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  40,  40,  40,  // 117
     40,  40,  40,  40,  40,  46,  46, 104,  // 117
     46,   3,   3,   3,   4,   3,   3,   3,  // 118
      5,   6,   3,   7,   3,   8,   3,   3,  // 118
      9,   9,   9,   9,   9,   9,   9,   9,  // 118
      9,   9,   3,   3,   7,   7,   7,   3,  // 118
      3,  10,  10,  10,  10,  10,  10,  10,  // 118
     10,  10,  10,  10,  10,  10,  10,  10,  // 118
     10,  10,  10,  10,  10,  10,  10,  10,  // 118
     10,  10,  10,   5,   3,   6,  11,  12,  // 118
     11,  13,  13,  13,  13,  13,  13,  13,  // 119
     13,  13,  13,  13,  13,  13,  13,  13,  // 119
     13,  13,  13,  13,  13,  13,  13,  13,  // 119
     13,  13,  13,   5,   7,   6,   7,  46,  // 119
     46,   3,   5,   6,   3,   3,  40,  40,  // 119
     40,  40,  40,  40,  40,  40,  40,  40,  // 119
     59,  40,  40,  40,  40,  40,  40,  40,  // 119
     40,  40,  40,  40,  40,  40,  40,  40,  // 119
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  59,  59,  // 120
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  40,  40,  // 120
     40,  40,  40,  40,  40,  40,  40,  46,  // 120
     46,  46,  40,  40,  40,  40,  40,  40,  // 121
     46,  46,  40,  40,  40,  40,  40,  40,  // 121
     46,  46,  40,  40,  40,  40,  40,  40,  // 121
     46,  46,  40,  40,  40,  46,  46,  46,  // 121
      4,   4,   7,  11,  15,   4,   4,  46,  // 121
      7,   7,   7,   7,   7,  15,  15,  46,  // 121
     46,  46,  46,  46,  46,  46,  46,  46,  // 121
     46,  46,  46,  46,  46,  15,  46,  46   // 121
  };

  // The A table has 124 entries for a total of 496 bytes.

  private static final int A[] = {
    0x0001000F,  //   0   Cc, ignorable
    0x0004000F,  //   1   Cc, whitespace
    0x0004000C,  //   2   Zs, whitespace
    0x00000018,  //   3   Po
    0x0006001A,  //   4   Sc, currency
    0x00000015,  //   5   Ps
    0x00000016,  //   6   Pe
    0x00000019,  //   7   Sm
    0x00000014,  //   8   Pd
    0x00036009,  //   9   Nd, identifier part, decimal 16
    0x0827FE01,  //  10   Lu, hasLower (add 32), identifier start, supradecimal 31
    0x0000001B,  //  11   Sk
    0x00050017,  //  12   Pc, underscore
    0x0817FE02,  //  13   Ll, hasUpper (subtract 32), identifier start, supradecimal 31
    0x0000000C,  //  14   Zs
    0x0000001C,  //  15   So
    0x00070002,  //  16   Ll, identifier start
    0x0000600B,  //  17   No, decimal 16
    0x0000500B,  //  18   No, decimal 8
    0x0000800B,  //  19   No, strange
    0x08270001,  //  20   Lu, hasLower (add 32), identifier start
    0x08170002,  //  21   Ll, hasUpper (subtract 32), identifier start
    0xE1D70002,  //  22   Ll, hasUpper (subtract -121), identifier start
    0x00670001,  //  23   Lu, hasLower (add 1), identifier start
    0x00570002,  //  24   Ll, hasUpper (subtract 1), identifier start
    0xCE670001,  //  25   Lu, hasLower (add -199), identifier start
    0x3A170002,  //  26   Ll, hasUpper (subtract 232), identifier start
    0xE1E70001,  //  27   Lu, hasLower (add -121), identifier start
    0x4B170002,  //  28   Ll, hasUpper (subtract 300), identifier start
    0x34A70001,  //  29   Lu, hasLower (add 210), identifier start
    0x33A70001,  //  30   Lu, hasLower (add 206), identifier start
    0x33670001,  //  31   Lu, hasLower (add 205), identifier start
    0x32A70001,  //  32   Lu, hasLower (add 202), identifier start
    0x32E70001,  //  33   Lu, hasLower (add 203), identifier start
    0x33E70001,  //  34   Lu, hasLower (add 207), identifier start
    0x34E70001,  //  35   Lu, hasLower (add 211), identifier start
    0x34670001,  //  36   Lu, hasLower (add 209), identifier start
    0x35670001,  //  37   Lu, hasLower (add 213), identifier start
    0x00070001,  //  38   Lu, identifier start
    0x36A70001,  //  39   Lu, hasLower (add 218), identifier start
    0x00070005,  //  40   Lo, identifier start
    0x36670001,  //  41   Lu, hasLower (add 217), identifier start
    0x36E70001,  //  42   Lu, hasLower (add 219), identifier start
    0x00AF0001,  //  43   Lu, hasLower (add 2), hasTitle, identifier start
    0x007F0003,  //  44   Lt, hasUpper (subtract 1), hasLower (add 1), hasTitle, identifier start
    0x009F0002,  //  45   Ll, hasUpper (subtract 2), hasTitle, identifier start
    0x00000000,  //  46   unassigned
    0x34970002,  //  47   Ll, hasUpper (subtract 210), identifier start
    0x33970002,  //  48   Ll, hasUpper (subtract 206), identifier start
    0x33570002,  //  49   Ll, hasUpper (subtract 205), identifier start
    0x32970002,  //  50   Ll, hasUpper (subtract 202), identifier start
    0x32D70002,  //  51   Ll, hasUpper (subtract 203), identifier start
    0x33D70002,  //  52   Ll, hasUpper (subtract 207), identifier start
    0x34570002,  //  53   Ll, hasUpper (subtract 209), identifier start
    0x34D70002,  //  54   Ll, hasUpper (subtract 211), identifier start
    0x35570002,  //  55   Ll, hasUpper (subtract 213), identifier start
    0x36970002,  //  56   Ll, hasUpper (subtract 218), identifier start
    0x36570002,  //  57   Ll, hasUpper (subtract 217), identifier start
    0x36D70002,  //  58   Ll, hasUpper (subtract 219), identifier start
    0x00070004,  //  59   Lm, identifier start
    0x00030006,  //  60   Mn, identifier part
    0x09A70001,  //  61   Lu, hasLower (add 38), identifier start
    0x09670001,  //  62   Lu, hasLower (add 37), identifier start
    0x10270001,  //  63   Lu, hasLower (add 64), identifier start
    0x0FE70001,  //  64   Lu, hasLower (add 63), identifier start
    0x09970002,  //  65   Ll, hasUpper (subtract 38), identifier start
    0x09570002,  //  66   Ll, hasUpper (subtract 37), identifier start
    0x10170002,  //  67   Ll, hasUpper (subtract 64), identifier start
    0x0FD70002,  //  68   Ll, hasUpper (subtract 63), identifier start
    0x0F970002,  //  69   Ll, hasUpper (subtract 62), identifier start
    0x0E570002,  //  70   Ll, hasUpper (subtract 57), identifier start
    0x0BD70002,  //  71   Ll, hasUpper (subtract 47), identifier start
    0x0D970002,  //  72   Ll, hasUpper (subtract 54), identifier start
    0x15970002,  //  73   Ll, hasUpper (subtract 86), identifier start
    0x14170002,  //  74   Ll, hasUpper (subtract 80), identifier start
    0x14270001,  //  75   Lu, hasLower (add 80), identifier start
    0x0C270001,  //  76   Lu, hasLower (add 48), identifier start
    0x0C170002,  //  77   Ll, hasUpper (subtract 48), identifier start
    0x00034009,  //  78   Nd, identifier part, decimal 0
    0x00000007,  //  79   Me
    0x00030008,  //  80   Mc, identifier part
    0x00037409,  //  81   Nd, identifier part, decimal 26
    0x00005A0B,  //  82   No, decimal 13
    0x00006E0B,  //  83   No, decimal 23
    0x0000740B,  //  84   No, decimal 26
    0x0000000B,  //  85   No
    0xFE170002,  //  86   Ll, hasUpper (subtract -8), identifier start
    0xFE270001,  //  87   Lu, hasLower (add -8), identifier start
    0xED970002,  //  88   Ll, hasUpper (subtract -74), identifier start
    0xEA970002,  //  89   Ll, hasUpper (subtract -86), identifier start
    0xE7170002,  //  90   Ll, hasUpper (subtract -100), identifier start
    0xE0170002,  //  91   Ll, hasUpper (subtract -128), identifier start
    0xE4170002,  //  92   Ll, hasUpper (subtract -112), identifier start
    0xE0970002,  //  93   Ll, hasUpper (subtract -126), identifier start
    0xFDD70002,  //  94   Ll, hasUpper (subtract -9), identifier start
    0xEDA70001,  //  95   Lu, hasLower (add -74), identifier start
    0xFDE70001,  //  96   Lu, hasLower (add -9), identifier start
    0xEAA70001,  //  97   Lu, hasLower (add -86), identifier start
    0xE7270001,  //  98   Lu, hasLower (add -100), identifier start
    0xFE570002,  //  99   Ll, hasUpper (subtract -7), identifier start
    0xE4270001,  // 100   Lu, hasLower (add -112), identifier start
    0xFE670001,  // 101   Lu, hasLower (add -7), identifier start
    0xE0270001,  // 102   Lu, hasLower (add -128), identifier start
    0xE0A70001,  // 103   Lu, hasLower (add -126), identifier start
    0x00010010,  // 104   Cf, ignorable
    0x0004000D,  // 105   Zl, whitespace
    0x0004000E,  // 106   Zp, whitespace
    0x0000400B,  // 107   No, decimal 0
    0x0000440B,  // 108   No, decimal 2
    0x0427420A,  // 109   Nl, hasLower (add 16), identifier start, decimal 1
    0x0427800A,  // 110   Nl, hasLower (add 16), identifier start, strange
    0x0417620A,  // 111   Nl, hasUpper (subtract 16), identifier start, decimal 17
    0x0417800A,  // 112   Nl, hasUpper (subtract 16), identifier start, strange
    0x0007800A,  // 113   Nl, identifier start, strange
    0x0000420B,  // 114   No, decimal 1
    0x0000720B,  // 115   No, decimal 25
    0x06A0001C,  // 116   So, hasLower (add 26)
    0x0690001C,  // 117   So, hasUpper (subtract 26)
    0x00006C0B,  // 118   No, decimal 22
    0x0000560B,  // 119   No, decimal 11
    0x0007720A,  // 120   Nl, identifier start, decimal 25
    0x0007400A,  // 121   Nl, identifier start, decimal 0
    0x00000013,  // 122   Cs
    0x00000012   // 123   Co
  };

  // In all, the character property tables require 9328 bytes.

}
