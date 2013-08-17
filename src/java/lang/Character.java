// This file was generated AUTOMATICALLY from a template file Tue Jan 08 00:50:43 PST 2002
/* @(#)Character.java	1.80 02/01/08
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package java.lang;

/**
 * The <code>Character</code> class wraps a value of the primitive
 * type <code>char</code> in an object. An object of type
 * <code>Character</code> contains a single field whose type is
 * <code>char</code>.
 * <p>
 * In addition, this class provides several methods for determining
 * a character's category (lowercase letter, digit, etc.) and for converting
 * characters from uppercase to lowercase and vice versa.
 * <p>
 * Character information is based on the Unicode Standard, version 3.0.
 * <p>
 * The methods and data of class <code>Character</code> are defined by
 * the information in the <i>UnicodeData</i> file that is part of the
 * Unicode Character Database maintained by the Unicode
 * Consortium. This file specifies various properties including name
 * and general category for every defined Unicode code point or
 * character range.
 * <p>
 * The file and its description are available from the Unicode Consortium at:

 * <ul>
 * <li><a href="http://www.unicode.org">http://www.unicode.org</a>
 * </ul>
 *
 * @author  Lee Boynton
 * @author  Guy Steele
 * @author  Akira Tanaka
 * @since   1.0
 */
public final
class Character extends Object implements java.io.Serializable, Comparable {
    /**
     * The minimum radix available for conversion to and from strings.
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
     */
    public static final int MIN_RADIX = 2;

    /**
     * The maximum radix available for conversion to and from strings.
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
     */
    public static final int MAX_RADIX = 36;

    /**
     * The constant value of this field is the smallest value of type
     * <code>char</code>, <code>'&#92;u0000'</code>.
     *
     * @since   1.0.2
     */
    public static final char   MIN_VALUE = '\u0000';

    /**
     * The constant value of this field is the largest value of type
     * <code>char</code>, <code>'&#92;uFFFF'</code>.
     *
     * @since   1.0.2
     */
    public static final char   MAX_VALUE = '\uffff';

    /**
     * The <code>Class</code> instance representing the primitive type
     * <code>char</code>.
     *
     * @since   1.1
     */
    public static final Class TYPE = Class.getPrimitiveClass("char");

   /*
    * Normative general types
    */

   /**
    * General category "Lu" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        UPPERCASE_LETTER            = 1;

   /**
    * General category "Ll" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        LOWERCASE_LETTER            = 2;

   /**
    * General category "Lt" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        TITLECASE_LETTER            = 3;

   /**
    * General category "Mn" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        NON_SPACING_MARK            = 6;

   /**
    * General category "Mc" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        COMBINING_SPACING_MARK      = 8;

   /**
    * General category "Me" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        ENCLOSING_MARK              = 7;

   /**
    * General category "Nd" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        DECIMAL_DIGIT_NUMBER        = 9;

   /**
    * General category "Nl" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        LETTER_NUMBER               = 10;

   /**
    * General category "No" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        OTHER_NUMBER                = 11;

   /**
    * General category "Zs" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        SPACE_SEPARATOR             = 12;

   /**
    * General category "Zl" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        LINE_SEPARATOR              = 13;

   /**
    * General category "Zp" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        PARAGRAPH_SEPARATOR         = 14;

   /**
    * General category "Cc" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        CONTROL                     = 15;
   /**
    * General category "Cf" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        FORMAT                      = 16;
   /**
    * General category "Cs" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        SURROGATE                   = 19;
   /**
    * General category "Co" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        PRIVATE_USE                 = 18;

   /**
    * General category "Cn" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        UNASSIGNED                  = 0;

   /*
    * Informative general types
    */

   /**
    * General category "Lm" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        MODIFIER_LETTER             = 4;

   /**
    * General category "Lo" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        OTHER_LETTER                = 5;

   /**
    * General category "Pc" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        CONNECTOR_PUNCTUATION       = 23;

   /**
    * General category "Pd" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        DASH_PUNCTUATION            = 20;

   /**
    * General category "Ps" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        START_PUNCTUATION           = 21;

   /**
    * General category "Pe" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        END_PUNCTUATION             = 22;

   /**
    * General category "Pi" in the Unicode specification.
    * @since   1.4
    */
    public static final byte
        INITIAL_QUOTE_PUNCTUATION   = 29;

   /**
    * General category "Pf" in the Unicode specification.
    * @since   1.4
    */
    public static final byte
        FINAL_QUOTE_PUNCTUATION     = 30;

   /**
    * General category "Po" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        OTHER_PUNCTUATION           = 24;

   /**
    * General category "Sm" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        MATH_SYMBOL                 = 25;

   /**
    * General category "Sc" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        CURRENCY_SYMBOL             = 26;

   /**
    * General category "Sk" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        MODIFIER_SYMBOL             = 27;

   /**
    * General category "So" in the Unicode specification.
    * @since   1.1
    */
    public static final byte
        OTHER_SYMBOL                = 28;

    /**
     * Error or non-char flag
     * @since 1.4
     */
     static final char CHAR_ERROR = '\uFFFF';


    /**
     * Undefined bidirectional character type. Undefined <code>char</code>
     * values have undefined directionality in the Unicode specification.
     * @since 1.4
     */
     public static final byte DIRECTIONALITY_UNDEFINED = -1;

    /**
     * Strong bidirectional character type "L" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;

    /**
     * Strong bidirectional character type "R" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;

    /**
    * Strong bidirectional character type "AL" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;

    /**
     * Weak bidirectional character type "EN" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;

    /**
     * Weak bidirectional character type "ES" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;

    /**
     * Weak bidirectional character type "ET" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;

    /**
     * Weak bidirectional character type "AN" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;

    /**
     * Weak bidirectional character type "CS" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;

    /**
     * Weak bidirectional character type "NSM" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;

    /**
     * Weak bidirectional character type "BN" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;

    /**
     * Neutral bidirectional character type "B" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;

    /**
     * Neutral bidirectional character type "S" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;

    /**
     * Neutral bidirectional character type "WS" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_WHITESPACE = 12;

    /**
     * Neutral bidirectional character type "ON" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;

    /**
     * Strong bidirectional character type "LRE" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;

    /**
     * Strong bidirectional character type "LRO" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;

    /**
     * Strong bidirectional character type "RLE" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;

    /**
     * Strong bidirectional character type "RLO" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;

    /**
     * Weak bidirectional character type "PDF" in the Unicode specification.
     * @since 1.4
     */
    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;

    /**
     * Instances of this class represent particular subsets of the Unicode
     * character set.  The only family of subsets defined in the
     * <code>Character</code> class is <code>{@link Character.UnicodeBlock
     * UnicodeBlock}</code>.  Other portions of the Java API may define other
     * subsets for their own purposes.
     *
     * @since 1.2
     */
    public static class Subset  {

        private String name;

        /**
         * Constructs a new <code>Subset</code> instance.
         *
         * @exception NullPointerException if name is <code>null</code>
         * @param  name  The name of this subset
         */
        protected Subset(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
        }

        /**
         * Compares two <code>Subset</code> objects for equality.
         * This method returns <code>true</code> if and only if
         * <code>this</code> and the argument refer to the same
         * object; since this method is <code>final</code>, this
         * guarantee holds for all subclasses.
         */
        public final boolean equals(Object obj) {
            return (this == obj);
        }

        /**
         * Returns the standard hash code as defined by the
         * <code>{@link Object#hashCode}</code> method.  This method
         * is <code>final</code> in order to ensure that the
         * <code>equals</code> and <code>hashCode</code> methods will
         * be consistent in all subclasses.
         */
        public final int hashCode() {
            return super.hashCode();
        }

        /**
         * Returns the name of this subset.
         */
        public final String toString() {
            return name;
        }
    }

    /**
     * A family of character subsets representing the character blocks in the
     * Unicode specification. Character blocks generally define characters
     * used for a specific script or purpose. A character is contained by
     * at most one Unicode block.
     *
     * @since 1.2
     */
    public static final class UnicodeBlock extends Subset {

        private UnicodeBlock(String name) {
            super(name);
        }

        /**
         * Constant for the Unicode character block of the same name.
         */
        public static final UnicodeBlock
            BASIC_LATIN
                = new UnicodeBlock("BASIC_LATIN"),
            LATIN_1_SUPPLEMENT
                = new UnicodeBlock("LATIN_1_SUPPLEMENT"),
            LATIN_EXTENDED_A
                = new UnicodeBlock("LATIN_EXTENDED_A"),
            LATIN_EXTENDED_B
                = new UnicodeBlock("LATIN_EXTENDED_B"),
            IPA_EXTENSIONS
                = new UnicodeBlock("IPA_EXTENSIONS"),
            SPACING_MODIFIER_LETTERS
                = new UnicodeBlock("SPACING_MODIFIER_LETTERS"),
            COMBINING_DIACRITICAL_MARKS
                = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS"),
            GREEK
                = new UnicodeBlock("GREEK"),
            CYRILLIC
                = new UnicodeBlock("CYRILLIC"),
            ARMENIAN
                = new UnicodeBlock("ARMENIAN"),
            HEBREW
                = new UnicodeBlock("HEBREW"),
            ARABIC
                = new UnicodeBlock("ARABIC"),
            DEVANAGARI
                = new UnicodeBlock("DEVANAGARI"),
            BENGALI
                = new UnicodeBlock("BENGALI"),
            GURMUKHI
                = new UnicodeBlock("GURMUKHI"),
            GUJARATI
                = new UnicodeBlock("GUJARATI"),
            ORIYA
                = new UnicodeBlock("ORIYA"),
            TAMIL
                = new UnicodeBlock("TAMIL"),
            TELUGU
                = new UnicodeBlock("TELUGU"),
            KANNADA
                = new UnicodeBlock("KANNADA"),
            MALAYALAM
                = new UnicodeBlock("MALAYALAM"),
            THAI
                = new UnicodeBlock("THAI"),
            LAO
                = new UnicodeBlock("LAO"),
            TIBETAN
                = new UnicodeBlock("TIBETAN"),
            GEORGIAN
                = new UnicodeBlock("GEORGIAN"),
            HANGUL_JAMO
                = new UnicodeBlock("HANGUL_JAMO"),
            LATIN_EXTENDED_ADDITIONAL
                = new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL"),
            GREEK_EXTENDED
                = new UnicodeBlock("GREEK_EXTENDED"),
            GENERAL_PUNCTUATION
                = new UnicodeBlock("GENERAL_PUNCTUATION"),
            SUPERSCRIPTS_AND_SUBSCRIPTS
                = new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS"),
            CURRENCY_SYMBOLS
                = new UnicodeBlock("CURRENCY_SYMBOLS"),
            COMBINING_MARKS_FOR_SYMBOLS
                = new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS"),
            LETTERLIKE_SYMBOLS
                = new UnicodeBlock("LETTERLIKE_SYMBOLS"),
            NUMBER_FORMS
                = new UnicodeBlock("NUMBER_FORMS"),
            ARROWS
                = new UnicodeBlock("ARROWS"),
            MATHEMATICAL_OPERATORS
                = new UnicodeBlock("MATHEMATICAL_OPERATORS"),
            MISCELLANEOUS_TECHNICAL
                = new UnicodeBlock("MISCELLANEOUS_TECHNICAL"),
            CONTROL_PICTURES
                = new UnicodeBlock("CONTROL_PICTURES"),
            OPTICAL_CHARACTER_RECOGNITION
                = new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION"),
            ENCLOSED_ALPHANUMERICS
                = new UnicodeBlock("ENCLOSED_ALPHANUMERICS"),
            BOX_DRAWING
                = new UnicodeBlock("BOX_DRAWING"),
            BLOCK_ELEMENTS
                = new UnicodeBlock("BLOCK_ELEMENTS"),
            GEOMETRIC_SHAPES
                = new UnicodeBlock("GEOMETRIC_SHAPES"),
            MISCELLANEOUS_SYMBOLS
                = new UnicodeBlock("MISCELLANEOUS_SYMBOLS"),
            DINGBATS
                = new UnicodeBlock("DINGBATS"),
            CJK_SYMBOLS_AND_PUNCTUATION
                = new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION"),
            HIRAGANA
                = new UnicodeBlock("HIRAGANA"),
            KATAKANA
                = new UnicodeBlock("KATAKANA"),
            BOPOMOFO
                = new UnicodeBlock("BOPOMOFO"),
            HANGUL_COMPATIBILITY_JAMO
                = new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO"),
            KANBUN
                = new UnicodeBlock("KANBUN"),
            ENCLOSED_CJK_LETTERS_AND_MONTHS
                = new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS"),
            CJK_COMPATIBILITY
                = new UnicodeBlock("CJK_COMPATIBILITY"),
            CJK_UNIFIED_IDEOGRAPHS
                = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS"),
            HANGUL_SYLLABLES
                = new UnicodeBlock("HANGUL_SYLLABLES"),
            SURROGATES_AREA
                = new UnicodeBlock("SURROGATES_AREA"),
            PRIVATE_USE_AREA
                = new UnicodeBlock("PRIVATE_USE_AREA"),
            CJK_COMPATIBILITY_IDEOGRAPHS
                = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS"),
            ALPHABETIC_PRESENTATION_FORMS
                = new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS"),
            ARABIC_PRESENTATION_FORMS_A
                = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A"),
            COMBINING_HALF_MARKS
                = new UnicodeBlock("COMBINING_HALF_MARKS"),
            CJK_COMPATIBILITY_FORMS
                = new UnicodeBlock("CJK_COMPATIBILITY_FORMS"),
            SMALL_FORM_VARIANTS
                = new UnicodeBlock("SMALL_FORM_VARIANTS"),
            ARABIC_PRESENTATION_FORMS_B
                = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B"),
            HALFWIDTH_AND_FULLWIDTH_FORMS
                = new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS"),
            SPECIALS
                = new UnicodeBlock("SPECIALS");

        /**
         * Constant for the Unicode character block of the same name.
         *
         * @since 1.4
         */
        public static final UnicodeBlock
            SYRIAC
                = new UnicodeBlock("SYRIAC"),
            THAANA
                = new UnicodeBlock("THAANA"),
            SINHALA
                = new UnicodeBlock("SINHALA"),
            MYANMAR
                = new UnicodeBlock("MYANMAR"),
            ETHIOPIC
                = new UnicodeBlock("ETHIOPIC"),
            CHEROKEE
                = new UnicodeBlock("CHEROKEE"),
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS
                = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS"),
            OGHAM
                = new UnicodeBlock("OGHAM"),
            RUNIC
                = new UnicodeBlock("RUNIC"),
            KHMER
                = new UnicodeBlock("KHMER"),
            MONGOLIAN
                = new UnicodeBlock("MONGOLIAN"),
            BRAILLE_PATTERNS
                = new UnicodeBlock("BRAILLE_PATTERNS"),
            CJK_RADICALS_SUPPLEMENT
                = new UnicodeBlock("CJK_RADICALS_SUPPLEMENT"),
            KANGXI_RADICALS
                = new UnicodeBlock("KANGXI_RADICALS"),
            IDEOGRAPHIC_DESCRIPTION_CHARACTERS =
                new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS"),
            BOPOMOFO_EXTENDED
                = new UnicodeBlock("BOPOMOFO_EXTENDED"),
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A"),
            YI_SYLLABLES
                = new UnicodeBlock("YI_SYLLABLES"),
            YI_RADICALS
                = new UnicodeBlock("YI_RADICALS");

        private static final char blockStarts[] = {
            '\u0000', // Basic Latin
            '\u0080', // Latin-1 Supplement
            '\u0100', // Latin Extended-A
            '\u0180', // Latin Extended-B
            '\u0250', // IPA Extensions
            '\u02B0', // Spacing Modifier Letters
            '\u0300', // Combining Diacritical Marks
            '\u0370', // Greek
            '\u0400', // Cyrillic
            '\u0500', // unassigned
            '\u0530', // Armenian
            '\u0590', // Hebrew
            '\u0600', // Arabic
            '\u0700', // Syriac
            '\u0750', // unassigned
            '\u0780', // Thaana
            '\u07C0', // unassigned
            '\u0900', // Devanagari
            '\u0980', // Bengali
            '\u0A00', // Gurmukhi
            '\u0A80', // Gujarati
            '\u0B00', // Oriya
            '\u0B80', // Tamil
            '\u0C00', // Telugu
            '\u0C80', // Kannada
            '\u0D00', // Malayalam
            '\u0D80', // Sinhala
            '\u0E00', // Thai
            '\u0E80', // Lao
            '\u0F00', // Tibetan
            '\u1000', // Myanmar
            '\u10A0', // Georgian
            '\u1100', // Hangul Jamo
            '\u1200', // Ethiopic
            '\u1380', // unassigned
            '\u13A0', // Cherokee
            '\u1400', // Unified Canadian Aboriginal Syllabics
            '\u1680', // Ogham
            '\u16A0', // Runic
            '\u1700', // unassigned
            '\u1780', // Khmer
            '\u1800', // Mongolian
            '\u18B0', // unassigned
            '\u1E00', // Latin Extended Additional
            '\u1F00', // Greek Extended
            '\u2000', // General Punctuation
            '\u2070', // Superscripts and Subscripts
            '\u20A0', // Currency Symbols
            '\u20D0', // Combining Marks for Symbols
            '\u2100', // Letterlike Symbols
            '\u2150', // Number Forms
            '\u2190', // Arrows
            '\u2200', // Mathematical Operators
            '\u2300', // Miscellaneous Technical
            '\u2400', // Control Pictures
            '\u2440', // Optical Character Recognition
            '\u2460', // Enclosed Alphanumerics
            '\u2500', // Box Drawing
            '\u2580', // Block Elements
            '\u25A0', // Geometric Shapes
            '\u2600', // Miscellaneous Symbols
            '\u2700', // Dingbats
            '\u27C0', // unassigned
            '\u2800', // Braille Patterns
            '\u2900', // unassigned
            '\u2E80', // CJK Radicals Supplement
            '\u2F00', // Kangxi Radicals
            '\u2FE0', // unassigned
            '\u2FF0', // Ideographic Description Characters
            '\u3000', // CJK Symbols and Punctuation
            '\u3040', // Hiragana
            '\u30A0', // Katakana
            '\u3100', // Bopomofo
            '\u3130', // Hangul Compatibility Jamo
            '\u3190', // Kanbun
            '\u31A0', // Bopomofo Extended
            '\u31C0', // unassigned
            '\u3200', // Enclosed CJK Letters and Months
            '\u3300', // CJK Compatibility
            '\u3400', // CJK Unified Ideographs Extension A
            '\u4DB6', // unassigned
            '\u4E00', // CJK Unified Ideographs
            '\uA000', // Yi Syllables
            '\uA490', // Yi Radicals
            '\uA4D0', // unassigned
            '\uAC00', // Hangul Syllables
            '\uD7A4', // unassigned
            '\uD800', // Surrogates
            '\uE000', // Private Use
            '\uF900', // CJK Compatibility Ideographs
            '\uFB00', // Alphabetic Presentation Forms
            '\uFB50', // Arabic Presentation Forms-A
            '\uFE00', // unassigned
            '\uFE20', // Combining Half Marks
            '\uFE30', // CJK Compatibility Forms
            '\uFE50', // Small Form Variants
            '\uFE70', // Arabic Presentation Forms-B
            '\uFEFF', // Specials
            '\uFF00', // Halfwidth and Fullwidth Forms
            '\uFFF0', // Specials
            '\uFFFE', // non-characters
        };

        private static final UnicodeBlock[] blocks = {
            BASIC_LATIN,
            LATIN_1_SUPPLEMENT,
            LATIN_EXTENDED_A,
            LATIN_EXTENDED_B,
            IPA_EXTENSIONS,
            SPACING_MODIFIER_LETTERS,
            COMBINING_DIACRITICAL_MARKS,
            GREEK,
            CYRILLIC,
            null,
            ARMENIAN,
            HEBREW,
            ARABIC,
            SYRIAC,
            null,
            THAANA,
            null,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            LAO,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            HANGUL_JAMO,
            ETHIOPIC,
            null,
            CHEROKEE,
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
            OGHAM,
            RUNIC,
            null,
            KHMER,
            MONGOLIAN,
            null,
            LATIN_EXTENDED_ADDITIONAL,
            GREEK_EXTENDED,
            GENERAL_PUNCTUATION,
            SUPERSCRIPTS_AND_SUBSCRIPTS,
            CURRENCY_SYMBOLS,
            COMBINING_MARKS_FOR_SYMBOLS,
            LETTERLIKE_SYMBOLS,
            NUMBER_FORMS,
            ARROWS,
            MATHEMATICAL_OPERATORS,
            MISCELLANEOUS_TECHNICAL,
            CONTROL_PICTURES,
            OPTICAL_CHARACTER_RECOGNITION,
            ENCLOSED_ALPHANUMERICS,
            BOX_DRAWING,
            BLOCK_ELEMENTS,
            GEOMETRIC_SHAPES,
            MISCELLANEOUS_SYMBOLS,
            DINGBATS,
            null,
            BRAILLE_PATTERNS,
            null,
            CJK_RADICALS_SUPPLEMENT,
            KANGXI_RADICALS,
            null,
            IDEOGRAPHIC_DESCRIPTION_CHARACTERS,
            CJK_SYMBOLS_AND_PUNCTUATION,
            HIRAGANA,
            KATAKANA,
            BOPOMOFO,
            HANGUL_COMPATIBILITY_JAMO,
            KANBUN,
            BOPOMOFO_EXTENDED,
            null,
            ENCLOSED_CJK_LETTERS_AND_MONTHS,
            CJK_COMPATIBILITY,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            null,
            CJK_UNIFIED_IDEOGRAPHS,
            YI_SYLLABLES,
            YI_RADICALS,
            null,
            HANGUL_SYLLABLES,
            null,
            SURROGATES_AREA,
            PRIVATE_USE_AREA,
            CJK_COMPATIBILITY_IDEOGRAPHS,
            ALPHABETIC_PRESENTATION_FORMS,
            ARABIC_PRESENTATION_FORMS_A,
            null,
            COMBINING_HALF_MARKS,
            CJK_COMPATIBILITY_FORMS,
            SMALL_FORM_VARIANTS,
            ARABIC_PRESENTATION_FORMS_B,
            SPECIALS,
            HALFWIDTH_AND_FULLWIDTH_FORMS,
            SPECIALS,
            null,
        };

        /**
         * Returns the object representing the Unicode block containing the
         * given character, or <code>null</code> if the character is not a
         * member of a defined block.
         *
         * @param   c  The character in question
         * @return  The <code>UnicodeBlock</code> instance representing the
         *          Unicode block of which this character is a member, or
         *          <code>null</code> if the character is not a member of any
         *          Unicode block
         */
        public static UnicodeBlock of(char c) {
            int top, bottom, current;
            bottom = 0;
            top = blockStarts.length;
            current = top/2;
            // invariant: top > current >= bottom && ch >= unicodeBlockStarts[bottom]
            while (top - bottom > 1) {
                if (c >= blockStarts[current]) {
                    bottom = current;
                } else {
                    top = current;
                }
                current = (top + bottom) / 2;
            }
            return blocks[current];
        }
    }

    /**
     * The value of the <code>Character</code>.
     *
     * @serial
     */
    private char value;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 3786198910865385080L;

    /**
     * Constructs a newly allocated <code>Character</code> object that
     * represents the specified <code>char</code> value.
     *
     * @param  value   the value to be represented by the 
     *			<code>Character</code> object.
     */
    public Character(char value) {
        this.value = value;
    }

    /**
     * Returns the value of this <code>Character</code> object.
     * @return  the primitive <code>char</code> value represented by
     *          this object.
     */
    public char charValue() {
        return value;
    }

    /**
     * Returns a hash code for this <code>Character</code>.
     * @return  a hash code value for this object.
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
     */
    public boolean equals(Object obj) {
        if (obj instanceof Character) {
            return value == ((Character)obj).charValue();
        }
        return false;
    }

    /**
     * Returns a <code>String</code> object representing this
     * <code>Character</code>'s value.  The result is a string of
     * length 1 whose sole component is the primitive
     * <code>char</code> value represented by this
     * <code>Character</code> object.
     *
     * @return  a string representation of this object.
     */
    public String toString() {
        char buf[] = {value};
        return String.valueOf(buf);
    }

    /**
     * Returns a <code>String</code> object representing the
     * specified <code>char</code>.  The result is a string of length
     * 1 consisting solely of the specified <code>char</code>.
     *
     * @param c the <code>char</code> to be converted
     * @return the string representation of the specified <code>char</code>
     * @since 1.4
     */
    public static String toString(char c) {
        return String.valueOf(new char[] {c});
    }


   /**
     * Determines if the specified character is a lowercase character.
     * <p>
     * A character is lowercase if its general category type, provided
     * by <code>Character.getType(ch)</code>, is
     * <code>LOWERCASE_LETTER</code>.
     * <p>
     * The following are examples of lowercase characters:
     * <p><blockquote><pre>
     * a b c d e f g h i j k l m n o p q r s t u v w x y z
     * '&#92;u00DF' '&#92;u00E0' '&#92;u00E1' '&#92;u00E2' '&#92;u00E3' '&#92;u00E4' '&#92;u00E5' '&#92;u00E6' 
     * '&#92;u00E7' '&#92;u00E8' '&#92;u00E9' '&#92;u00EA' '&#92;u00EB' '&#92;u00EC' '&#92;u00ED' '&#92;u00EE'
     * '&#92;u00EF' '&#92;u00F0' '&#92;u00F1' '&#92;u00F2' '&#92;u00F3' '&#92;u00F4' '&#92;u00F5' '&#92;u00F6'
     * '&#92;u00F8' '&#92;u00F9' '&#92;u00FA' '&#92;u00FB' '&#92;u00FC' '&#92;u00FD' '&#92;u00FE' '&#92;u00FF'
     * </pre></blockquote>
     * <p> Many other Unicode characters are lowercase too.
     * <p>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is lowercase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#getType(char)
     */
    public static boolean isLowerCase(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F) == LOWERCASE_LETTER;
    }

   /**
     * Determines if the specified character is an uppercase character.
     * <p>
     * A character is uppercase if its general category type, provided by
     * <code>Character.getType(ch)</code>, is <code>UPPERCASE_LETTER</code>.
     * <p>
     * The following are examples of uppercase characters:
     * <p><blockquote><pre>
     * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
     * '&#92;u00C0' '&#92;u00C1' '&#92;u00C2' '&#92;u00C3' '&#92;u00C4' '&#92;u00C5' '&#92;u00C6' '&#92;u00C7'
     * '&#92;u00C8' '&#92;u00C9' '&#92;u00CA' '&#92;u00CB' '&#92;u00CC' '&#92;u00CD' '&#92;u00CE' '&#92;u00CF'
     * '&#92;u00D0' '&#92;u00D1' '&#92;u00D2' '&#92;u00D3' '&#92;u00D4' '&#92;u00D5' '&#92;u00D6' '&#92;u00D8'
     * '&#92;u00D9' '&#92;u00DA' '&#92;u00DB' '&#92;u00DC' '&#92;u00DD' '&#92;u00DE'
     * </pre></blockquote>
     * <p> Many other Unicode characters are uppercase too.<p>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is uppercase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     * @see     java.lang.Character#getType(char)
     * @since   1.0
     */
    public static boolean isUpperCase(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F) == UPPERCASE_LETTER;
    }

    /**
     * Determines if the specified character is a titlecase character.
     * <p> 
     * A character is a titlecase character if its general
     * category type, provided by <code>Character.getType(ch)</code>,
     * is <code>TITLECASE_LETTER</code>.
     * <p>
     * Some characters look like pairs of Latin letters. For example, there
     * is an uppercase letter that looks like "LJ" and has a corresponding
     * lowercase letter that looks like "lj". A third form, which looks like "Lj",
     * is the appropriate form to use when rendering a word in lowercase
     * with initial capitals, as for a book title.
     * <p>
     * These are some of the Unicode characters for which this method returns
     * <code>true</code>:
     * <ul>
     * <li><code>LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON</code>
     * <li><code>LATIN CAPITAL LETTER L WITH SMALL LETTER J</code>
     * <li><code>LATIN CAPITAL LETTER N WITH SMALL LETTER J</code>
     * <li><code>LATIN CAPITAL LETTER D WITH SMALL LETTER Z</code>
     * </ul>
     * <p> Many other Unicode characters are titlecase too.<p>
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is titlecase;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @see     java.lang.Character#getType(char)
     * @since   1.0.2
     */
    public static boolean isTitleCase(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F) == TITLECASE_LETTER;
    }

    /**
     * Determines if the specified character is a digit.
     * <p>
     * A character is a digit if its general category type, provided
     * by <code>Character.getType(ch)</code>, is
     * <code>DECIMAL_DIGIT_NUMBER</code>.
     * <p>
     * Some Unicode character ranges that contain digits:
     * <ul>
     * <li><code>'&#92;u0030'</code> through <code>'&#92;u0039'</code>, 
     *	   ISO-LATIN-1 digits (<code>'0'</code> through <code>'9'</code>)
     * <li><code>'&#92;u0660'</code> through <code>'&#92;u0669'</code>,
     *	   Arabic-Indic digits
     * <li><code>'&#92;u06F0'</code> through <code>'&#92;u06F9'</code>,
     * 	   Extended Arabic-Indic digits
     * <li><code>'&#92;u0966'</code> through <code>'&#92;u096F'</code>,
     *	   Devanagari digits
     * <li><code>'&#92;uFF10'</code> through <code>'&#92;uFF19'</code>,
     *	   Fullwidth digits
     * </ul>
     *
     * Many other character ranges contain digits as well.
     *
     * @param   ch   the character to be tested.
     * @return  <code>true</code> if the character is a digit;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#digit(char, int)
     * @see     java.lang.Character#forDigit(int, int)
     * @see     java.lang.Character#getType(char)
     */
    public static boolean isDigit(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F) == DECIMAL_DIGIT_NUMBER;
    }

    /**
     * Determines if a character is defined in Unicode.
     * <p>
     * A character is defined if at least one of the following is true:
     * <ul>
     * <li>It has an entry in the UnicodeData file.
     * <li>It has a value in a range defined by the UnicodeData file.
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
     * @since   1.0.2
     */
    public static boolean isDefined(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F) != UNASSIGNED;
    }

    /**
     * Determines if the specified character is a letter.
     * <p>
     * A character is considered to be a letter if its general
     * category type, provided by <code>Character.getType(ch)</code>,
     * is any of the following:
     * <ul>
     * <li> <code>UPPERCASE_LETTER</code>
     * <li> <code>LOWERCASE_LETTER</code>
     * <li> <code>TITLECASE_LETTER</code>
     * <li> <code>MODIFIER_LETTER</code>
     * <li> <code>OTHER_LETTER</code>
     * </ul>
     *
     * Not all letters have case. Many characters are
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
     */
    public static boolean isLetter(char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER))
                  >> (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is a letter or digit.
     * <p>
     * A character is considered to be a letter or digit if either
     * <code>Character.isLetter(char ch)</code> or
     * <code>Character.isDigit(char ch)</code> returns
     * <code>true</code> for the character.
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
     * @since   1.0.2
     */
    public static boolean isLetterOrDigit(char ch) {
        return (((((1 << UPPERCASE_LETTER) |
                   (1 << LOWERCASE_LETTER) |
                   (1 << TITLECASE_LETTER) |
                   (1 << MODIFIER_LETTER) |
                   (1 << OTHER_LETTER) |
                   (1 << DECIMAL_DIGIT_NUMBER))
                  >> (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is permissible as the first
     * character in a Java identifier.
     * <p>
     * A character may start a Java identifier if and only if
     * one of the following is true:
     * <ul>
     * <li> {@link #isLetter(char) isLetter(ch)} returns <code>true</code>
     * <li> {@link #getType(char) getType(ch)} returns <code>LETTER_NUMBER</code>
     * <li> ch is a currency symbol (such as "$")
     * <li> ch is a connecting punctuation character (such as "_").
     * </ul>
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character may start a Java
     *          identifier; <code>false</code> otherwise.
     * @see     java.lang.Character#isJavaLetterOrDigit(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   1.02
     * @deprecated Replaced by isJavaIdentifierStart(char).
     */
    public static boolean isJavaLetter(char ch) {
        return isJavaIdentifierStart(ch);
    }

    /**
     * Determines if the specified character may be part of a Java
     * identifier as other than the first character.
     * <p>
     * A character may be part of a Java identifier if and only if any
     * of the following are true:
     * <ul>
     * <li>  it is a letter
     * <li>  it is a currency symbol (such as <code>'$'</code>)
     * <li>  it is a connecting punctuation character (such as <code>'_'</code>)
     * <li>  it is a digit
     * <li>  it is a numeric letter (such as a Roman numeral character)
     * <li>  it is a combining mark
     * <li>  it is a non-spacing mark
     * <li> <code>isIdentifierIgnorable</code> returns
     * <code>true</code> for the character.
     * </ul>
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character may be part of a
     *          Java identifier; <code>false</code> otherwise.
     * @see     java.lang.Character#isJavaLetter(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @since   1.02
     * @deprecated Replaced by isJavaIdentifierPart(char).
     */
    public static boolean isJavaLetterOrDigit(char ch) {
        return isJavaIdentifierPart(ch);
    }

    /**
     * Determines if the specified character is
     * permissible as the first character in a Java identifier.
     * <p>
     * A character may start a Java identifier if and only if
     * one of the following conditions is true:
     * <ul>
     * <li> {@link #isLetter(char) isLetter(ch)} returns <code>true</code>
     * <li> {@link #getType(char) getType(ch)} returns <code>LETTER_NUMBER</code>
     * <li> ch is a currency symbol (such as "$")
     * <li> ch is a connecting punctuation character (such as "_").
     * </ul>
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character may start a Java identifier;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   1.1
     */
    public static boolean isJavaIdentifierStart(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x00007000) >= 0x00005000;
    }

    /**
     * Determines if the specified character may be part of a Java
     * identifier as other than the first character.
     * <p>
     * A character may be part of a Java identifier if any of the following
     * are true:
     * <ul>
     * <li>  it is a letter
     * <li>  it is a currency symbol (such as <code>'$'</code>)
     * <li>  it is a connecting punctuation character (such as <code>'_'</code>)
     * <li>  it is a digit
     * <li>  it is a numeric letter (such as a Roman numeral character)
     * <li>  it is a combining mark
     * <li>  it is a non-spacing mark
     * <li> <code>isIdentifierIgnorable</code> returns
     * <code>true</code> for the character
     * </ul>
     *
     * @param   ch	the character to be tested.
     * @return <code>true</code> if the character may be part of a
     * 		Java identifier; <code>false</code> otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   1.1
     */
    public static boolean isJavaIdentifierPart(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x00003000) != 0;
    }

    /**
     * Determines if the specified character is permissible as the
     * first character in a Unicode identifier.
     * <p>
     * A character may start a Unicode identifier if and only if
     * one of the following conditions is true:
     * <ul>
     * <li> {@link #isLetter(char) isLetter(ch)} returns <code>true</code>
     * <li> {@link #getType(char) getType(ch)} returns 
     *      <code>LETTER_NUMBER</code>.
     * </ul>
     * @param   ch	the character to be tested.
     * @return  <code>true</code> if the character may start a Unicode 
     *          identifier; <code>false</code> otherwise.
     * @see     java.lang.Character#isJavaIdentifierStart(char)
     * @see     java.lang.Character#isLetter(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   1.1
     */
    public static boolean isUnicodeIdentifierStart(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x00007000) == 0x00007000;
    }

    /**
     * Determines if the specified character may be part of a Unicode
     * identifier as other than the first character.
     * <p>
     * A character may be part of a Unicode identifier if and only if
     * one of the following statements is true:
     * <ul>
     * <li>  it is a letter
     * <li>  it is a connecting punctuation character (such as <code>'_'</code>)
     * <li>  it is a digit
     * <li>  it is a numeric letter (such as a Roman numeral character)
     * <li>  it is a combining mark
     * <li>  it is a non-spacing mark
     * <li> <code>isIdentifierIgnorable</code> returns
     * <code>true</code> for this character.
     * </ul>
     *
     * @param   ch	the character to be tested.
     * @return  <code>true</code> if the character may be part of a 
     *          Unicode identifier; <code>false</code> otherwise.
     * @see     java.lang.Character#isIdentifierIgnorable(char)
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isLetterOrDigit(char)
     * @see     java.lang.Character#isUnicodeIdentifierStart(char)
     * @since   1.1
     */
    public static boolean isUnicodeIdentifierPart(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)]& 0x00001000) != 0;
    }

    /**
     * Determines if the specified character should be regarded as
     * an ignorable character in a Java identifier or a Unicode identifier.
     * <p>
     * The following Unicode characters are ignorable in a Java identifier
     * or a Unicode identifier:
     * <ul>
     * <li>ISO control characters that are not whitespace
     * <ul>
     * <li><code>'&#92;u0000'</code> through <code>'&#92;u0008'</code>
     * <li><code>'&#92;u000E'</code> through <code>'&#92;u001B'</code>
     * <li><code>'&#92;u007F'</code> through <code>'&#92;u009F'</code>
     * </ul>
     *
     * <li>all characters that have the <code>FORMAT</code> general
     * category value
     * </ul>
     *
     * @param   ch	the character to be tested.
     * @return 	<code>true</code> if the character is an ignorable control 
     *          character that may be part of a Java or Unicode identifier;
     *		 <code>false</code> otherwise.
     * @see     java.lang.Character#isJavaIdentifierPart(char)
     * @see     java.lang.Character#isUnicodeIdentifierPart(char)
     * @since   1.1
     */
    public static boolean isIdentifierIgnorable(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x00007000) == 0x00001000;
    }

    /**
     * Converts the character argument to lowercase using case
     * mapping information from the UnicodeData file.
     * <p>
     * Note that
     * <code>Character.isLowerCase(Character.toLowerCase(ch))</code>
     * does not always return <code>true</code> for some ranges of
     * characters, particularly those that are symbols or ideographs.
     *
     * @param   ch   the character to be converted.
     * @return  the lowercase equivalent of the character, if any;
     *          otherwise, the character itself.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     */
    public static char toLowerCase(char ch) {
        char mapChar = ch;
        int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];

        if ((val & 0x00020000) != 0) {
            if ((val & 0x07FC0000) == 0x07FC0000) {
                switch(ch) {
                    // map the offset overflow chars
                    case '\u2126' : mapChar = '\u03C9'; break;
                    case '\u212A' : mapChar = '\u006B'; break;
                    case '\u212B' : mapChar = '\u00E5'; break;
                    // map the titlecase chars with both a 1:M uppercase map
                    // and a lowercase map
                    case '\u1F88' : mapChar = '\u1F80'; break;
                    case '\u1F89' : mapChar = '\u1F81'; break;
                    case '\u1F8A' : mapChar = '\u1F82'; break;
                    case '\u1F8B' : mapChar = '\u1F83'; break;
                    case '\u1F8C' : mapChar = '\u1F84'; break;
                    case '\u1F8D' : mapChar = '\u1F85'; break;
                    case '\u1F8E' : mapChar = '\u1F86'; break;
                    case '\u1F8F' : mapChar = '\u1F87'; break;
                    case '\u1F98' : mapChar = '\u1F90'; break;
                    case '\u1F99' : mapChar = '\u1F91'; break;
                    case '\u1F9A' : mapChar = '\u1F92'; break;
                    case '\u1F9B' : mapChar = '\u1F93'; break;
                    case '\u1F9C' : mapChar = '\u1F94'; break;
                    case '\u1F9D' : mapChar = '\u1F95'; break;
                    case '\u1F9E' : mapChar = '\u1F96'; break;
                    case '\u1F9F' : mapChar = '\u1F97'; break;
                    case '\u1FA8' : mapChar = '\u1FA0'; break;
                    case '\u1FA9' : mapChar = '\u1FA1'; break;
                    case '\u1FAA' : mapChar = '\u1FA2'; break;
                    case '\u1FAB' : mapChar = '\u1FA3'; break;
                    case '\u1FAC' : mapChar = '\u1FA4'; break;
                    case '\u1FAD' : mapChar = '\u1FA5'; break;
                    case '\u1FAE' : mapChar = '\u1FA6'; break;
                    case '\u1FAF' : mapChar = '\u1FA7'; break;
                    case '\u1FBC' : mapChar = '\u1FB3'; break;
                    case '\u1FCC' : mapChar = '\u1FC3'; break;
                    case '\u1FFC' : mapChar = '\u1FF3'; break;
                    // default mapChar is already set, so no
                    // need to redo it here.
                    // default       : mapChar = ch;
                }
            }
            else {
                int offset = val << 5 >> (5+18);
                mapChar = (char)(ch + offset);
            }
        }
        return mapChar;
    }

    /**
     * Converts the character argument to uppercase using case mapping
     * information from the UnicodeData file.
     * <p>
     * Note that
     * <code>Character.isUpperCase(Character.toUpperCase(ch))</code>
     * does not always return <code>true</code> for some ranges of
     * characters, particularly those that are symbols or ideographs.
     *
     * @param   ch   the character to be converted.
     * @return  the uppercase equivalent of the character, if any;
     *          otherwise, the character itself.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     */
    public static char toUpperCase(char ch) {
        char mapChar = ch;
        int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];

        if ((val & 0x00010000) != 0) {
            if ((val & 0x07FC0000) == 0x07FC0000) {
                switch(ch) {
                    // map chars with overflow offsets
                    case '\u00B5' : mapChar = '\u039C'; break;
                    case '\u017F' : mapChar = '\u0053'; break;
                    case '\u1FBE' : mapChar = '\u0399'; break;
                    // map char that have both a 1:1 and 1:M map
                    case '\u1F80' : mapChar = '\u1F88'; break;
                    case '\u1F81' : mapChar = '\u1F89'; break;
                    case '\u1F82' : mapChar = '\u1F8A'; break;
                    case '\u1F83' : mapChar = '\u1F8B'; break;
                    case '\u1F84' : mapChar = '\u1F8C'; break;
                    case '\u1F85' : mapChar = '\u1F8D'; break;
                    case '\u1F86' : mapChar = '\u1F8E'; break;
                    case '\u1F87' : mapChar = '\u1F8F'; break;
                    case '\u1F90' : mapChar = '\u1F98'; break;
                    case '\u1F91' : mapChar = '\u1F99'; break;
                    case '\u1F92' : mapChar = '\u1F9A'; break;
                    case '\u1F93' : mapChar = '\u1F9B'; break;
                    case '\u1F94' : mapChar = '\u1F9C'; break;
                    case '\u1F95' : mapChar = '\u1F9D'; break;
                    case '\u1F96' : mapChar = '\u1F9E'; break;
                    case '\u1F97' : mapChar = '\u1F9F'; break;
                    case '\u1FA0' : mapChar = '\u1FA8'; break;
                    case '\u1FA1' : mapChar = '\u1FA9'; break;
                    case '\u1FA2' : mapChar = '\u1FAA'; break;
                    case '\u1FA3' : mapChar = '\u1FAB'; break;
                    case '\u1FA4' : mapChar = '\u1FAC'; break;
                    case '\u1FA5' : mapChar = '\u1FAD'; break;
                    case '\u1FA6' : mapChar = '\u1FAE'; break;
                    case '\u1FA7' : mapChar = '\u1FAF'; break;
                    case '\u1FB3' : mapChar = '\u1FBC'; break;
                    case '\u1FC3' : mapChar = '\u1FCC'; break;
                    case '\u1FF3' : mapChar = '\u1FFC'; break;
                    // ch must have a 1:M case mapping, but we
                    // can't handle it here. Return ch.
                    // since mapChar is already set, no need
                    // to redo it here.
                    //default       : mapChar = ch;
                }
            }
            else {
                int offset = val  << 5 >> (5+18);
                mapChar =  (char)(ch - offset);
            }
        }
        return mapChar;
    }

    /**
     * Converts the character argument to titlecase using case mapping
     * information from the UnicodeData file. If a character has no
     * explicit titlecase mapping and is not itself a titlecase char
     * according to UnicodeData, then the uppercase mapping is
     * returned as an equivalent titlecase mapping. If the
     * <code>char</code> argument is already a titlecase
     * <code>char</code>, the same <code>char</code> value will be
     * returned.
     * <p>
     * Note that
     * <code>Character.isTitleCase(Character.toTitleCase(ch))</code>
     * does not always return <code>true</code> for some ranges of
     * characters.
     *
     * @param   ch   the character to be converted.
     * @return  the titlecase equivalent of the character, if any;
     *          otherwise, the character itself.
     * @see     java.lang.Character#isTitleCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     * @since   1.0.2
     */
    public static char toTitleCase(char ch) {
        char mapChar = ch;
        int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];

        if ((val & 0x00008000) != 0) {
            // There is a titlecase equivalent.  Perform further checks:
            if ((val & 0x00010000) == 0) {
                // The character does not have an uppercase equivalent, so it must
                // already be uppercase; so add 1 to get the titlecase form.
                mapChar = (char)(ch + 1);
            }
            else if ((val & 0x00020000) == 0) {
                // The character does not have a lowercase equivalent, so it must
                // already be lowercase; so subtract 1 to get the titlecase form.
                mapChar = (char)(ch - 1);
            }
            // else {
            // The character has both an uppercase equivalent and a lowercase
            // equivalent, so it must itself be a titlecase form; return it.
            // return ch;
            //}
        }
        else if ((val & 0x00010000) != 0) {
            // This character has no titlecase equivalent but it does have an
            // uppercase equivalent, so use that (subtract the signed case offset).
            mapChar = Character.toUpperCase(ch);
        }
        return mapChar;
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
     * <li>The method <code>isDigit</code> is <code>true</code> of the character
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
     */
    public static int digit(char ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
            int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];
            int kind = val & 0x1F;
            if (kind == DECIMAL_DIGIT_NUMBER) {
                value = ch + ((val & 0x3E0) >> 5) & 0x1F;
            }
            else if ((val & 0xC00) == 0x00000C00) {
                // Java supradecimal digit
                value = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            }
        }
        return (value < radix) ? value : -1;
    }

    /**
     * Returns the <code>int</code> value that the specified Unicode
     * character represents. For example, the character
     * <code>'&#92;u216C'</code> (the roman numeral fifty) will return
     * an int with a value of 50.
     * <p>
     * The letters A-Z in their uppercase (<code>'&#92;u0041'</code> through
     * <code>'&#92;u005A'</code>), lowercase
     * (<code>'&#92;u0061'</code> through <code>'&#92;u007A'</code>), and
     * full width variant (<code>'&#92;uFF21'</code> through
     * <code>'&#92;uFF3A'</code> and <code>'&#92;uFF41'</code> through
     * <code>'&#92;uFF5A'</code>) forms have numeric values from 10
     * through 35. This is independent of the Unicode specification,
     * which does not assign numeric values to these <code>char</code>
     * values.
     * <p>
     * If the character does not have a numeric value, then -1 is returned.
     * If the character has a numeric value that cannot be represented as a
     * nonnegative integer (for example, a fractional value), then -2
     * is returned.
     *
     * @param   ch	the character to be converted.
     * @return  the numeric value of the character, as a nonnegative <code>int</code>
     *           value; -2 if the character has a numeric value that is not a
     *          nonnegative integer; -1 if the character has no numeric value.
     * @see     java.lang.Character#forDigit(int, int)
     * @see     java.lang.Character#isDigit(char)
     * @since   1.1
     */
    public static int getNumericValue(char ch) {
        int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];
        int retval = -1;

        switch (val & 0xC00) {
        default: // cannot occur
        case (0x00000000):         // not numeric
            retval = -1;
            break;
        case (0x00000400):              // simple numeric
            retval = ch + ((val & 0x3E0) >> 5) & 0x1F;
            break;
        case (0x00000800)      :       // "strange" numeric
            switch (ch) {
                case '\u0BF1': retval = 100; break;         // TAMIL NUMBER ONE HUNDRED
                case '\u0BF2': retval = 1000; break;        // TAMIL NUMBER ONE THOUSAND
                case '\u1375': retval = 40; break;          // ETHIOPIC NUMBER FORTY
                case '\u1376': retval = 50; break;          // ETHIOPIC NUMBER FIFTY
                case '\u1377': retval = 60; break;          // ETHIOPIC NUMBER SIXTY
                case '\u1378': retval = 70; break;          // ETHIOPIC NUMBER SEVENTY
                case '\u1379': retval = 80; break;          // ETHIOPIC NUMBER EIGHTY
                case '\u137A': retval = 90; break;          // ETHIOPIC NUMBER NINETY
                case '\u137B': retval = 100; break;         // ETHIOPIC NUMBER HUNDRED
                case '\u137C': retval = 10000; break;       // ETHIOPIC NUMBER TEN THOUSAND
                case '\u215F': retval = 1; break;           // FRACTION NUMERATOR ONE
                case '\u216C': retval = 50; break;          // ROMAN NUMERAL FIFTY
                case '\u216D': retval = 100; break;         // ROMAN NUMERAL ONE HUNDRED
                case '\u216E': retval = 500; break;         // ROMAN NUMERAL FIVE HUNDRED
                case '\u216F': retval = 1000; break;        // ROMAN NUMERAL ONE THOUSAND
                case '\u217C': retval = 50; break;          // SMALL ROMAN NUMERAL FIFTY
                case '\u217D': retval = 100; break;         // SMALL ROMAN NUMERAL ONE HUNDRED
                case '\u217E': retval = 500; break;         // SMALL ROMAN NUMERAL FIVE HUNDRED
                case '\u217F': retval = 1000; break;        // SMALL ROMAN NUMERAL ONE THOUSAND
                case '\u2180': retval = 1000; break;        // ROMAN NUMERAL ONE THOUSAND C D
                case '\u2181': retval = 5000; break;        // ROMAN NUMERAL FIVE THOUSAND
                case '\u2182': retval = 10000; break;       // ROMAN NUMERAL TEN THOUSAND
                default:       retval = -2; break;
            }
            break;
        case (0x00000C00):           // Java supradecimal
            retval = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            break;
        }
        return retval;

    }

    /**
     * Determines if the specified character is ISO-LATIN-1 white space.
     * This method returns <code>true</code> for the following five
     * characters only:
     * <table>
     * <tr><td><code>'\t'</code></td>            <td><code>'&#92;u0009'</code></td>
     *     <td><code>HORIZONTAL TABULATION</code></td></tr>
     * <tr><td><code>'\n'</code></td>            <td><code>'&#92;u000A'</code></td>
     *     <td><code>NEW LINE</code></td></tr>
     * <tr><td><code>'\f'</code></td>            <td><code>'&#92;u000C'</code></td>
     *     <td><code>FORM FEED</code></td></tr>
     * <tr><td><code>'\r'</code></td>            <td><code>'&#92;u000D'</code></td>
     *     <td><code>CARRIAGE RETURN</code></td></tr>
     * <tr><td><code>'&nbsp;'</code></td>  <td><code>'&#92;u0020'</code></td>
     *     <td><code>SPACE</code></td></tr>
     * </table>
     *
     * @param      ch   the character to be tested.
     * @return     <code>true</code> if the character is ISO-LATIN-1 white
     *             space; <code>false</code> otherwise.
     * @see        java.lang.Character#isSpaceChar(char)
     * @see        java.lang.Character#isWhitespace(char)
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
     * it is specified to be a space character by the Unicode standard. This
     * method returns true if the character's general category type is any of
     * the following:
     * <ul>
     * <li> <code>SPACE_SEPARATOR</code>
     * <li> <code>LINE_SEPARATOR</code>
     * <li> <code>PARAGRAPH_SEPARATOR</code>
     * </ul>
     *
     * @param   ch	the character to be tested.
     * @return 	<code>true</code> if the character is a space character; 
     *		<code>false</code> otherwise.
     * @see     java.lang.Character#isWhitespace(char)
     * @since   1.1
     */
    public static boolean isSpaceChar(char ch) {
        return (((((1 << SPACE_SEPARATOR) |
                   (1 << LINE_SEPARATOR) |
                   (1 << PARAGRAPH_SEPARATOR))
                    >> (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F)) & 1) != 0);
    }

    /**
     * Determines if the specified character is white space according to Java.
     * A character is a Java whitespace character if and only if it satisfies
     * one of the following criteria:
     * <ul>
     * <li> It is a Unicode space character (<code>SPACE_SEPARATOR</code>,
     * 	    <code>LINE_SEPARATOR</code>, or <code>PARAGRAPH_SEPARATOR</code>) 
     *      but is not also a non-breaking space (<code>'&#92;u00A0'</code>,
     *      <code>'&#92;u2007'</code>, <code>'&#92;u202F'</code>).
     * <li> It is <code>'&#92;u0009'</code>, HORIZONTAL TABULATION.
     * <li> It is <code>'&#92;u000A'</code>, LINE FEED.
     * <li> It is <code>'&#92;u000B'</code>, VERTICAL TABULATION.
     * <li> It is <code>'&#92;u000C'</code>, FORM FEED.
     * <li> It is <code>'&#92;u000D'</code>, CARRIAGE RETURN.
     * <li> It is <code>'&#92;u001C'</code>, FILE SEPARATOR.
     * <li> It is <code>'&#92;u001D'</code>, GROUP SEPARATOR.
     * <li> It is <code>'&#92;u001E'</code>, RECORD SEPARATOR.
     * <li> It is <code>'&#92;u001F'</code>, UNIT SEPARATOR.
     * </ul>
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character is a Java whitespace
     *          character; <code>false</code> otherwise.
     * @see     java.lang.Character#isSpaceChar(char)
     * @since   1.1
     */
    public static boolean isWhitespace(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x00007000) == 0x00004000;
    }

    /**
     * Determines if the specified character is an ISO control
     * character.  A character is considered to be an ISO control
     * character if its code is in the range <code>'&#92;u0000'</code>
     * through <code>'&#92;u001F'</code> or in the range
     * <code>'&#92;u007F'</code> through <code>'&#92;u009F'</code>.
     *
     * @param   ch	the character to be tested.
     * @return  <code>true</code> if the character is an ISO control character;
     *          <code>false</code> otherwise.
     *
     * @see     java.lang.Character#isSpaceChar(char)
     * @see     java.lang.Character#isWhitespace(char)
     * @since   1.1
     */
    public static boolean isISOControl(char ch) {
        return (ch <= 0x009F) && ((ch <= 0x001F) || (ch >= 0x007F));
    }

    /**
     * Returns a value indicating a character's general category.
     *
     * @param   ch      the character to be tested.
     * @return  a value of type <code>int</code> representing the 
     *		character's general category.
     * @see     java.lang.Character#COMBINING_SPACING_MARK
     * @see     java.lang.Character#CONNECTOR_PUNCTUATION
     * @see     java.lang.Character#CONTROL
     * @see     java.lang.Character#CURRENCY_SYMBOL
     * @see     java.lang.Character#DASH_PUNCTUATION
     * @see     java.lang.Character#DECIMAL_DIGIT_NUMBER
     * @see     java.lang.Character#ENCLOSING_MARK
     * @see     java.lang.Character#END_PUNCTUATION
     * @see     java.lang.Character#FINAL_QUOTE_PUNCTUATION
     * @see     java.lang.Character#FORMAT
     * @see     java.lang.Character#INITIAL_QUOTE_PUNCTUATION
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
     * @since   1.1
     */
    public static int getType(char ch) {
        return (A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)] & 0x1F);
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
     * <code>0&nbsp;&lt;=digit&nbsp;&lt;&nbsp;radix</code>.
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

    /**
     * Returns the Unicode directionality property for the given
     * character.  Character directionality is used to calculate the
     * visual ordering of text. The directionality value of undefined
     * <code>char</code> values is <code>DIRECTIONALITY_UNDEFINED</code>.
     *
     * @param  c <code>char</code> for which the directionality property 
     *		is requested.
     * @return the directionality property of the <code>char</code> value.
     *
     * @see Character#DIRECTIONALITY_UNDEFINED
     * @see Character#DIRECTIONALITY_LEFT_TO_RIGHT
     * @see Character#DIRECTIONALITY_RIGHT_TO_LEFT
     * @see Character#DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
     * @see Character#DIRECTIONALITY_EUROPEAN_NUMBER
     * @see Character#DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR
     * @see Character#DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR
     * @see Character#DIRECTIONALITY_ARABIC_NUMBER
     * @see Character#DIRECTIONALITY_COMMON_NUMBER_SEPARATOR
     * @see Character#DIRECTIONALITY_NONSPACING_MARK
     * @see Character#DIRECTIONALITY_BOUNDARY_NEUTRAL
     * @see Character#DIRECTIONALITY_PARAGRAPH_SEPARATOR
     * @see Character#DIRECTIONALITY_SEGMENT_SEPARATOR
     * @see Character#DIRECTIONALITY_WHITESPACE
     * @see Character#DIRECTIONALITY_OTHER_NEUTRALS
     * @see Character#DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING
     * @see Character#DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE
     * @see Character#DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING
     * @see Character#DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
     * @see Character#DIRECTIONALITY_POP_DIRECTIONAL_FORMAT
     * @since 1.4
     */
    public static byte getDirectionality(char c) {
        int val = A[Y[((X[c>>5]&0xFF)<<4)|((c>>1)&0xF)]|(c&0x1)];
        byte directionality = (byte)((val & 0x78000000) >> 27);
        if (directionality == 0xF ) {
            switch(c) {
                case '\u202A' :
                    // This is the only char with LRE
                    directionality = DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING;
                    break;
                case '\u202B' :
                    // This is the only char with RLE
                    directionality = DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING;
                    break;
                case '\u202C' :
                    // This is the only char with PDF
                    directionality = DIRECTIONALITY_POP_DIRECTIONAL_FORMAT;
                    break;
                case '\u202D' :
                    // This is the only char with LRO
                    directionality = DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE;
                    break;
                case '\u202E' :
                    // This is the only char with RLO
                    directionality = DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE;
                    break;
                default :
                    directionality = DIRECTIONALITY_UNDEFINED;
                    break;
            }
        }
        return directionality;
    }

    /**
     * Determines whether the character is mirrored according to the
     * Unicode specification.  Mirrored characters should have their
     * glyphs horizontally mirrored when displayed in text that is
     * right-to-left.  For example, <code>'&#92;u0028'</code> LEFT
     * PARENTHESIS is semantically defined to be an <i>opening
     * parenthesis</i>.  This will appear as a "(" in text that is
     * left-to-right but as a ")" in text that is right-to-left.
     *
     * @param  c <code>char</code> for which the mirrored property is requested
     * @return <code>true</code> if the char is mirrored, <code>false</code>
     *         if the <code>char</code> is not mirrored or is not defined.
     * @since 1.4
     */
    public static boolean isMirrored(char c) {
        int val = A[Y[((X[c>>5]&0xFF)<<4)|((c>>1)&0xF)]|(c&0x1)];
        return ((val & 0x80000000) != 0);
    }

    /**
     * Compares two <code>Character</code> objects numerically.
     *
     * @param   anotherCharacter   the <code>Character</code> to be compared.

     * @return  the value <code>0</code> if the argument <code>Character</code> 
     *          is equal to this <code>Character</code>; a value less than 
     *          <code>0</code> if this <code>Character</code> is numerically less 
     *          than the <code>Character</code> argument; and a value greater than 
     *          <code>0</code> if this <code>Character</code> is numerically greater 
     *	        than the <code>Character</code> argument (unsigned comparison).  
     *	        Note that this is strictly a numerical comparison; it is not 
     *		locale-dependent.
     * @since   1.2
     */
    public int compareTo(Character anotherCharacter) {
        return this.value - anotherCharacter.value;
    }

    /**
     * Compares this <code>Character</code> object to another object.
     * If the object is a <code>Character</code>, this function
     * behaves like <code>compareTo(Character)</code>.  Otherwise, it
     * throws a <code>ClassCastException</code> (as
     * <code>Character</code> objects are comparable only to other
     * <code>Character</code> objects).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a <code>Character</code>
     *		numerically equal to this <code>Character</code>; a value less than
     *		<code>0</code> if the argument is a <code>Character</code> numerically
     *		greater than this <code>Character</code>; and a value greater than
     *		<code>0</code> if the argument is a <code>Character</code> numerically
     *		less than this <code>Character</code>.
     * @exception <code>ClassCastException</code> if the argument is not a
     *		  <code>Character</code>.
     * @see     java.lang.Comparable
     * @since 1.2 */
    public int compareTo(Object o) {
        return compareTo((Character)o);
    }


    /**
     * Converts the character argument to uppercase using case mapping
     * information from the UnicodeData file.
     * <p>
     *
     * @param   ch   the <code>char</code> to be converted.
     * @return  either the uppercase equivalent of the character, if 
     *          any, or an error flag (<code>Character.CHAR_ERROR</code>) 
     *          that indicates that a 1:M <code>char</code> mapping exists.
     * @see     java.lang.Character#isLowerCase(char)
     * @see     java.lang.Character#isUpperCase(char)
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toTitleCase(char)
     * @since 1.4
     */
    static char toUpperCaseEx(char ch) {
        char mapChar = ch;
        int val = A[Y[((X[ch>>5]&0xFF)<<4)|((ch>>1)&0xF)]|(ch&0x1)];

        if ((val & 0x00010000) != 0) {
            if ((val & 0x07FC0000) != 0x07FC0000) {
                int offset = val  << 5 >> (5+18);
                mapChar =  (char)(ch - offset);
            }
            else {
                switch(ch) {
                    // map overflow characters
                    case '\u00B5' : mapChar = '\u039C'; break;
                    case '\u017F' : mapChar = '\u0053'; break;
                    case '\u1FBE' : mapChar = '\u0399'; break;
                    default       : mapChar = CHAR_ERROR; break;
                }
            }
        }
        return mapChar;
    }

    /**
     * Converts the <code>char</code> argument to uppercase using case
     * mapping information from the SpecialCasing file in the Unicode
     * specification. If a character has no explicit uppercase
     * mapping, then the <code>char</code> itself is returned in the
     * <code>char[]</code>.
     *
     * @param ch the <code>char</code> to uppercase
     * @return a <code>char[]</code> with the uppercased character.
     * @since 1.4
     */
    static char[] toUpperCaseCharArray(char ch) {
        char[] upperMap = {ch};
        int location = findInCharMap(ch);
        if (location != -1) {
            upperMap = charMap[location][1];
        }
        return upperMap;
    }


    /**
     * Finds the character in the uppercase mapping table.
     *
     * @param ch the <code>char</code> to search
     * @return the index location ch in the table or -1 if not found
     * @since 1.4
     */
    static  int findInCharMap(char ch) {
        int top, bottom, current;
        bottom = 0;
        top = charMap.length;
        current = top/2;
        // invariant: top > current >= bottom && ch >= charMap[bottom][0]
        while (top - bottom > 1) {
            if (ch >= charMap[current][0][0]) {
                bottom = current;
            } else {
                top = current;
            }
            current = (top + bottom) / 2;
        }
        if (ch == charMap[current][0][0]) return current;
        else return -1;
    }


    /* The character properties are currently encoded into 32 bits in the following manner:
        1 bit   mirrored property
        4 bits  directionality property
        9 bits  signed offset used for converting case
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
        5 bits  character type

        The encoding of character properties is subject to change at any time.
     */

    // The following tables and code generated using:
  // java GenerateCharacter -string 11 4 1 [-spec UnicodeData.txt] [-specialcasing SpecialCasing.txt] [-template Character.java.template] [-o Character.java]
      static char[][][] charMap = {
        { {'\u00DF'}, {'\u0053', '\u0053', } },
        { {'\u0149'}, {'\u02BC', '\u004E', } },
        { {'\u01F0'}, {'\u004A', '\u030C', } },
        { {'\u0390'}, {'\u0399', '\u0308', '\u0301', } },
        { {'\u03B0'}, {'\u03A5', '\u0308', '\u0301', } },
        { {'\u0587'}, {'\u0535', '\u0552', } },
        { {'\u1E96'}, {'\u0048', '\u0331', } },
        { {'\u1E97'}, {'\u0054', '\u0308', } },
        { {'\u1E98'}, {'\u0057', '\u030A', } },
        { {'\u1E99'}, {'\u0059', '\u030A', } },
        { {'\u1E9A'}, {'\u0041', '\u02BE', } },
        { {'\u1F50'}, {'\u03A5', '\u0313', } },
        { {'\u1F52'}, {'\u03A5', '\u0313', '\u0300', } },
        { {'\u1F54'}, {'\u03A5', '\u0313', '\u0301', } },
        { {'\u1F56'}, {'\u03A5', '\u0313', '\u0342', } },
        { {'\u1F80'}, {'\u1F08', '\u0399', } },
        { {'\u1F81'}, {'\u1F09', '\u0399', } },
        { {'\u1F82'}, {'\u1F0A', '\u0399', } },
        { {'\u1F83'}, {'\u1F0B', '\u0399', } },
        { {'\u1F84'}, {'\u1F0C', '\u0399', } },
        { {'\u1F85'}, {'\u1F0D', '\u0399', } },
        { {'\u1F86'}, {'\u1F0E', '\u0399', } },
        { {'\u1F87'}, {'\u1F0F', '\u0399', } },
        { {'\u1F88'}, {'\u1F08', '\u0399', } },
        { {'\u1F89'}, {'\u1F09', '\u0399', } },
        { {'\u1F8A'}, {'\u1F0A', '\u0399', } },
        { {'\u1F8B'}, {'\u1F0B', '\u0399', } },
        { {'\u1F8C'}, {'\u1F0C', '\u0399', } },
        { {'\u1F8D'}, {'\u1F0D', '\u0399', } },
        { {'\u1F8E'}, {'\u1F0E', '\u0399', } },
        { {'\u1F8F'}, {'\u1F0F', '\u0399', } },
        { {'\u1F90'}, {'\u1F28', '\u0399', } },
        { {'\u1F91'}, {'\u1F29', '\u0399', } },
        { {'\u1F92'}, {'\u1F2A', '\u0399', } },
        { {'\u1F93'}, {'\u1F2B', '\u0399', } },
        { {'\u1F94'}, {'\u1F2C', '\u0399', } },
        { {'\u1F95'}, {'\u1F2D', '\u0399', } },
        { {'\u1F96'}, {'\u1F2E', '\u0399', } },
        { {'\u1F97'}, {'\u1F2F', '\u0399', } },
        { {'\u1F98'}, {'\u1F28', '\u0399', } },
        { {'\u1F99'}, {'\u1F29', '\u0399', } },
        { {'\u1F9A'}, {'\u1F2A', '\u0399', } },
        { {'\u1F9B'}, {'\u1F2B', '\u0399', } },
        { {'\u1F9C'}, {'\u1F2C', '\u0399', } },
        { {'\u1F9D'}, {'\u1F2D', '\u0399', } },
        { {'\u1F9E'}, {'\u1F2E', '\u0399', } },
        { {'\u1F9F'}, {'\u1F2F', '\u0399', } },
        { {'\u1FA0'}, {'\u1F68', '\u0399', } },
        { {'\u1FA1'}, {'\u1F69', '\u0399', } },
        { {'\u1FA2'}, {'\u1F6A', '\u0399', } },
        { {'\u1FA3'}, {'\u1F6B', '\u0399', } },
        { {'\u1FA4'}, {'\u1F6C', '\u0399', } },
        { {'\u1FA5'}, {'\u1F6D', '\u0399', } },
        { {'\u1FA6'}, {'\u1F6E', '\u0399', } },
        { {'\u1FA7'}, {'\u1F6F', '\u0399', } },
        { {'\u1FA8'}, {'\u1F68', '\u0399', } },
        { {'\u1FA9'}, {'\u1F69', '\u0399', } },
        { {'\u1FAA'}, {'\u1F6A', '\u0399', } },
        { {'\u1FAB'}, {'\u1F6B', '\u0399', } },
        { {'\u1FAC'}, {'\u1F6C', '\u0399', } },
        { {'\u1FAD'}, {'\u1F6D', '\u0399', } },
        { {'\u1FAE'}, {'\u1F6E', '\u0399', } },
        { {'\u1FAF'}, {'\u1F6F', '\u0399', } },
        { {'\u1FB2'}, {'\u1FBA', '\u0399', } },
        { {'\u1FB3'}, {'\u0391', '\u0399', } },
        { {'\u1FB4'}, {'\u0386', '\u0399', } },
        { {'\u1FB6'}, {'\u0391', '\u0342', } },
        { {'\u1FB7'}, {'\u0391', '\u0342', '\u0399', } },
        { {'\u1FBC'}, {'\u0391', '\u0399', } },
        { {'\u1FC2'}, {'\u1FCA', '\u0399', } },
        { {'\u1FC3'}, {'\u0397', '\u0399', } },
        { {'\u1FC4'}, {'\u0389', '\u0399', } },
        { {'\u1FC6'}, {'\u0397', '\u0342', } },
        { {'\u1FC7'}, {'\u0397', '\u0342', '\u0399', } },
        { {'\u1FCC'}, {'\u0397', '\u0399', } },
        { {'\u1FD2'}, {'\u0399', '\u0308', '\u0300', } },
        { {'\u1FD3'}, {'\u0399', '\u0308', '\u0301', } },
        { {'\u1FD6'}, {'\u0399', '\u0342', } },
        { {'\u1FD7'}, {'\u0399', '\u0308', '\u0342', } },
        { {'\u1FE2'}, {'\u03A5', '\u0308', '\u0300', } },
        { {'\u1FE3'}, {'\u03A5', '\u0308', '\u0301', } },
        { {'\u1FE4'}, {'\u03A1', '\u0313', } },
        { {'\u1FE6'}, {'\u03A5', '\u0342', } },
        { {'\u1FE7'}, {'\u03A5', '\u0308', '\u0342', } },
        { {'\u1FF2'}, {'\u1FFA', '\u0399', } },
        { {'\u1FF3'}, {'\u03A9', '\u0399', } },
        { {'\u1FF4'}, {'\u038F', '\u0399', } },
        { {'\u1FF6'}, {'\u03A9', '\u0342', } },
        { {'\u1FF7'}, {'\u03A9', '\u0342', '\u0399', } },
        { {'\u1FFC'}, {'\u03A9', '\u0399', } },
        { {'\uFB00'}, {'\u0046', '\u0046', } },
        { {'\uFB01'}, {'\u0046', '\u0049', } },
        { {'\uFB02'}, {'\u0046', '\u004C', } },
        { {'\uFB03'}, {'\u0046', '\u0046', '\u0049', } },
        { {'\uFB04'}, {'\u0046', '\u0046', '\u004C', } },
        { {'\uFB05'}, {'\u0053', '\u0054', } },
        { {'\uFB06'}, {'\u0053', '\u0054', } },
        { {'\uFB13'}, {'\u0544', '\u0546', } },
        { {'\uFB14'}, {'\u0544', '\u0535', } },
        { {'\uFB15'}, {'\u0544', '\u053B', } },
        { {'\uFB16'}, {'\u054E', '\u0546', } },
        { {'\uFB17'}, {'\u0544', '\u053D', } },
    };
// The X table has 2048 entries for a total of 2048 bytes.

  private static final byte X[] = new byte[2048];
  private static final String X_DATA =
    "\u0100\u0302\u0504\u0706\u0908\u0B0A\u0D0C\u0F0E\u1008\u1211\u1413\u1615\u1717"+
    "\u1918\u1B1A\u1D1C\u1F1E\u0820\u0821\u2322\u2524\u2726\u2928\u2B2A\u2D2C\u2F2E"+
    "\u3030\u3231\u3433\u2435\u3630\u2424\u2424\u2424\u2424\u2424\u3837\u3A39\u3C3B"+
    "\u3E3D\u403F\u4241\u4443\u4645\u473B\u4948\u4B4A\u4D4C\u4F4E\u5150\u5352\u5154"+
    "\u5552\u5156\u5857\u5A59\u5C5B\u245D\u5F5E\u2460\u6261\u6463\u6665\u2467\u6968"+
    "\u246A\u6B24\u6D6C\u6868\u686E\u6F68\u7068\u6871\u6872\u7473\u7675\u6874\u7877"+
    "\u6824\u7968\u685B\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u7A68\u687B"+
    "\u7C68\u2424\u2424\u7D68\u7F7E\u6880\u8281\u8368\u2424\u2424\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u0808\u0808\u0884\u8508\u8786\u8988\u8B8A\u8D8C\u8F8E\u9190"+
    "\u9392\u9594\u9796\u9998\u9B9A\u9D9C\u9F9E\uA1A0\uA3A2\uA5A4\uA7A6\uA9A8\u24AA"+
    "\u2424\uACAB\uAEAD\uB0AF\uB2B1\uABAB\uABAB\uB4B3\uB6B5\uABB7\uB8AB\u2424\u2424"+
    "\uBAB9\uBCBB\uBEBD\u2424\uABAB\uABAB\uABAB\uABAB\u2424\u2424\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u2424\uABBF\u9DAB\uABAB\uABAB\uABAB\uC0B3\uC2C1\u685B\u5BC3"+
    "\uC468\uC6C5\u6868\u82C7\u2424\uC9C8\uCBCA\uCDCC\uCFCE\uA8A8\uD0A8\uA8A8\uCFD1"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\uD268\u2424"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\uD368\u2424\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\uD5D4\u24D6\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424"+
    "\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u2424\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868\u6868"+
    "\u6868\u6868\u6868\u6868\uD768\u2424\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8"+
    "\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8"+
    "\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD8D8\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9"+
    "\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\uD9D9\u6868\u6868\u6868\u6868\uDA68"+
    "\u2424\u2424\u2424\uDCDB\u30DD\uDE30\u30DF\u3030\u3030\u3030\u3030\uE030\u30E1"+
    "\u30E2\uE4E3\uE524\uE7E6\u3030\uE830\uEAE9\uECEB\uEEED\uF0EF";

  // The Y table has 3856 entries for a total of 7712 bytes.

  private static final short Y[] = new short[3856];
  private static final String Y_DATA =
    "\000\000\000\000\002\004\006\000\000\000\000\000\000\000\010\004\012\014\016"+
    "\020\022\024\026\030\032\032\032\032\032\034\036\040\042\044\044\044\044\044"+
    "\044\044\044\044\044\044\044\046\050\052\054\056\056\056\056\056\056\056\056"+
    "\056\056\056\056\060\062\064\000\000\066\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\070\072\072\074\076\100\102\104\106\110\112\114\116\120\122"+
    "\124\126\126\126\126\126\126\126\126\126\126\126\130\126\126\126\132\134\134"+
    "\134\134\134\134\134\134\134\134\134\136\134\134\134\140\142\142\142\142\142"+
    "\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142"+
    "\144\142\142\142\146\150\150\150\150\150\150\150\152\142\142\142\142\142\142"+
    "\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\142\154\150"+
    "\150\152\156\142\142\160\162\164\166\170\172\162\174\176\142\200\202\204\142"+
    "\142\142\206\210\200\142\206\212\214\150\216\142\220\142\222\224\224\226\230"+
    "\232\226\234\150\150\150\150\150\150\150\236\142\142\142\142\142\142\142\142"+
    "\142\240\232\142\242\142\142\142\142\244\142\142\142\142\142\142\142\142\142"+
    "\244\244\244\244\244\244\244\244\244\244\244\244\244\244\200\246\250\252\254"+
    "\256\200\200\260\262\200\200\264\200\200\266\200\270\272\200\200\200\200\200"+
    "\274\276\200\200\274\300\200\200\200\302\200\200\200\200\200\200\200\200\200"+
    "\200\200\200\200\244\304\304\304\304\306\310\304\304\304\312\312\312\312\312"+
    "\312\312\304\312\312\312\312\312\312\312\304\304\306\312\312\312\312\314\244"+
    "\244\244\244\244\244\244\244\316\316\316\316\316\316\316\316\316\316\316\316"+
    "\316\316\316\316\316\316\320\316\316\316\316\322\244\244\244\244\244\244\244"+
    "\244\316\322\244\244\244\244\244\244\244\244\312\244\244\314\244\324\244\244"+
    "\312\326\330\332\334\336\340\126\126\126\126\126\126\126\126\342\126\126\126"+
    "\126\344\346\350\134\134\134\134\134\134\134\134\352\134\134\134\134\354\356"+
    "\360\362\364\366\244\142\142\142\142\142\142\142\142\142\142\142\370\372\244"+
    "\244\244\244\244\244\374\374\374\374\374\374\374\374\126\126\126\126\126\126"+
    "\126\126\126\126\126\126\126\126\126\126\134\134\134\134\134\134\134\134\134"+
    "\134\134\134\134\134\134\134\376\376\376\376\376\376\376\376\142\u0100\316"+
    "\322\u0102\244\142\142\142\142\142\142\142\142\142\142\u0104\150\u0106\u0108"+
    "\u0106\u0108\u0106\244\142\142\142\142\142\142\142\142\142\142\142\142\142"+
    "\142\142\142\142\142\142\244\142\244\244\244\244\244\244\244\244\244\244\244"+
    "\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\u010A\u010C"+
    "\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C\u010C"+
    "\u010C\u010C\u010C\u010C\u010E\u0110\u0112\u0112\u0112\u0114\u0116\u0116\u0116"+
    "\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116\u0116"+
    "\u0116\u0116\u0118\u011A\u011C\244\244\u011E\316\316\316\316\316\316\316\316"+
    "\u011E\316\316\316\316\316\316\316\316\316\316\316\u011E\316\u0120\u0120\u0122"+
    "\322\244\244\244\244\244\u0124\u0124\u0124\u0124\u0124\u0124\u0124\u0124\u0124"+
    "\u0124\u0124\u0124\u0124\u0126\244\244\u0124\u0128\u012A\244\244\244\244\244"+
    "\244\244\244\244\244\244\u012C\244\244\244\244\244\244\u012E\244\u012E\u0130"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0134"+
    "\244\244\u0136\u0132\u0132\u0132\u0132\u0138\316\316\316\316\316\244\244\244"+
    "\244\244\u013A\u013A\u013A\u013A\u013A\u013C\u013E\244\u0140\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0142\316\316\316\u0144\u0146\316\316\u0148\u014A"+
    "\u014C\316\316\244\032\032\032\032\032\u0132\u014E\u0150\u0152\u0152\u0152"+
    "\u0152\u0152\u0152\u0152\u0154\u0138\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0134\244\316\316\316\316\316\316\316"+
    "\316\316\316\316\316\316\322\244\244\244\244\244\244\244\244\244\244\u0132"+
    "\u0132\u0132\316\316\316\316\316\322\244\244\244\244\244\244\244\u011E\u0156"+
    "\u0158\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\244\u015A\u015C\u015E\316\316\316\u0156"+
    "\u015C\u015E\244\u0160\316\322\244\224\224\224\224\224\316\u0112\u0162\u0162"+
    "\u0162\u0162\u0162\u0164\244\244\244\244\244\244\244\u011E\u015C\u0158\224"+
    "\224\224\u0166\u0158\u0166\u0158\224\224\224\224\224\224\224\224\224\224\u0166"+
    "\224\224\224\u0166\u0166\244\224\224\244\322\u015C\u015E\316\322\u0168\u016A"+
    "\u0168\u015E\244\244\244\244\u0168\244\244\224\u0158\224\316\244\u0162\u0162"+
    "\u0162\u0162\u0162\224\072\u016C\u016C\u016E\u0170\244\244\244\322\u0158\224"+
    "\224\u0166\244\u0158\u0166\u0158\224\224\224\224\224\224\224\224\224\224\u0166"+
    "\224\224\224\u0166\224\u0158\u0166\224\244\322\u015C\u015E\322\244\u011E\322"+
    "\u011E\316\244\244\244\244\244\u0158\224\u0166\u0166\244\244\244\u0162\u0162"+
    "\u0162\u0162\u0162\316\224\u0166\244\244\244\244\244\u011E\u0156\u0158\224"+
    "\224\224\u0158\u0158\224\u0158\224\224\224\224\224\224\224\224\224\224\u0166"+
    "\224\224\224\u0166\224\u0158\224\224\244\u015A\u015C\u015E\316\316\u011E\u0156"+
    "\u0168\u015E\244\u0166\244\244\244\244\244\244\244\u0166\244\244\u0162\u0162"+
    "\u0162\u0162\u0162\244\244\244\244\244\244\244\244\224\224\224\224\u0166\224"+
    "\224\224\u0166\224\244\224\224\244\u015A\u015E\u015E\316\244\u0168\u016A\u0168"+
    "\u015E\244\244\244\244\u0156\244\244\224\u0158\224\244\244\u0162\u0162\u0162"+
    "\u0162\u0162\u0170\244\244\244\244\244\244\244\244\u0156\u0158\224\224\u0166"+
    "\244\224\u0166\224\224\244\u0158\u0166\u0166\224\244\u0158\u0166\244\224\u0166"+
    "\244\224\224\224\224\u0158\224\244\244\u015C\u0156\u016A\244\u015C\u016A\u015C"+
    "\u015E\244\244\244\244\u0168\244\244\244\244\244\244\244\u0172\u0162\u0162"+
    "\u0162\u0162\u0174\u0176\244\244\244\244\244\244\u0168\u015C\u0158\224\224"+
    "\224\u0166\224\u0166\224\224\224\224\224\224\224\224\224\224\224\u0166\224"+
    "\224\224\224\224\u0158\224\224\244\244\316\u0156\u015C\u016A\316\322\316\316"+
    "\244\244\244\u011E\322\244\244\244\244\224\244\244\u0162\u0162\u0162\u0162"+
    "\u0162\244\244\244\244\244\244\244\244\244\u015C\u0158\224\224\224\u0166\224"+
    "\u0166\224\224\224\224\224\224\224\224\224\224\224\u0166\224\224\224\224\224"+
    "\u0158\224\224\244\244\u015E\u015C\u015C\u016A\u0156\u016A\u015C\316\244\244"+
    "\244\u0168\u016A\244\244\244\u0166\224\224\224\224\u0166\224\224\224\224\224"+
    "\224\224\224\244\244\u015C\u015E\316\244\u015C\u016A\u015C\u015E\244\244\244"+
    "\244\u0168\244\244\244\244\244\u015C\u0158\224\224\224\224\224\224\224\224"+
    "\u0166\244\224\224\224\224\224\224\224\224\224\224\224\224\u0158\224\224\224"+
    "\224\u0158\244\224\224\224\u0166\244\322\244\u0168\u015C\316\322\322\u015C"+
    "\u015C\u015C\u015C\244\244\244\244\244\244\244\244\244\u015C\u0164\244\244"+
    "\244\244\244\u0158\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\u0160\224\316\316\316\322\244\u0178\224"+
    "\224\224\u017A\316\316\316\u017C\u017E\u017E\u017E\u017E\u017E\u0112\244\244"+
    "\u0158\u0166\u0166\u0158\u0166\u0166\u0158\244\244\244\224\224\u0158\224\224"+
    "\224\u0158\224\u0158\u0158\244\224\u0158\224\u0160\224\316\316\316\u011E\u015A"+
    "\244\224\224\u0166\314\316\316\316\244\u017E\u017E\u017E\u017E\u017E\244\224"+
    "\244\u0180\u0182\u0112\u0112\u0112\u0112\u0112\u0112\u0112\u0184\u0182\u0182"+
    "\316\u0182\u0182\u0182\u0186\u0186\u0186\u0186\u0186\u0188\u0188\u0188\u0188"+
    "\u0188\u0100\u0100\u0100\u018A\u018A\u015C\224\224\224\224\u0158\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\224\224\224\u0166\244\244\u011E\316"+
    "\316\316\316\316\316\u0156\316\316\u017C\316\224\224\244\244\316\316\316\316"+
    "\u011E\316\316\316\316\316\316\316\316\316\316\316\316\316\316\316\316\316"+
    "\322\u0182\u0182\u0182\u0182\u018C\u0182\u0182\u0170\u018E\244\244\244\244"+
    "\244\244\244\244\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\u0158\224\224\u0158\u0166\u015E\316\u0156\322\244\316\u015E\244\244"+
    "\244\u0186\u0186\u0186\u0186\u0186\u0112\u0112\u0112\224\224\224\u015C\316"+
    "\244\244\244\362\362\362\362\362\362\362\362\362\362\362\362\362\362\362\362"+
    "\362\362\362\244\244\244\244\244\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\u0166\244\u011A\244\244\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\244\244\u0158\224\u0166\244\244\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\244\244\244\224\224\224\u0166\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\u0166\u0166\224\224\244\224\224\224\u0166\u0166"+
    "\224\224\244\224\224\224\u0166\u0166\224\224\244\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\u0166\u0166\224\224\244\224\224\224\u0166"+
    "\u0166\224\224\244\224\224\224\u0166\224\224\224\u0166\224\224\224\224\224"+
    "\224\224\224\224\224\224\u0166\224\224\224\224\224\224\224\224\224\224\224"+
    "\u0166\224\224\224\224\224\224\224\224\224\u0166\244\244\u011A\u0112\u0112"+
    "\u0112\u0190\u0192\u0192\u0192\u0192\u0194\u0196\u0198\u0198\u0198\u0176\244"+
    "\224\224\224\224\224\224\224\224\224\224\u0166\244\244\244\244\244\224\224"+
    "\224\224\224\224\u019A\u019C\224\224\224\u0166\244\244\244\244\u019E\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\u01A0\u01A2\244\224\224\224\224\224"+
    "\u019A\u0112\u01A4\u01A6\244\244\244\244\244\244\244\224\224\224\224\224\224"+
    "\224\224\224\224\u015C\u015E\316\316\316\u015C\u015C\u015C\u015C\u0156\u015E"+
    "\316\316\316\316\316\u0112\u0112\u0112\u01A8\u0164\244\u0186\u0186\u0186\u0186"+
    "\u0186\244\244\244\244\244\244\244\244\244\244\244\020\020\020\u01AA\020\u01AC"+
    "\u01AE\u01B0\u017E\u017E\u017E\u017E\u017E\244\244\244\224\u01B2\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\244\244\244\244\224\224\224\224\u0160\244\244\244\244\244"+
    "\244\244\244\244\244\244\142\142\142\142\142\142\142\142\142\142\142\u01B4"+
    "\u01B4\u01B6\244\244\142\142\142\142\142\142\142\142\142\142\142\142\142\244"+
    "\244\244\u01B8\u01B8\u01B8\u01B8\u01BA\u01BA\u01BA\u01BA\u01B8\u01B8\u01B8"+
    "\244\u01BA\u01BA\u01BA\244\u01B8\u01B8\u01B8\u01B8\u01BA\u01BA\u01BA\u01BA"+
    "\u01B8\u01B8\u01B8\u01B8\u01BA\u01BA\u01BA\u01BA\u01B8\u01B8\u01B8\244\u01BA"+
    "\u01BA\u01BA\244\u01BC\u01BC\u01BC\u01BC\u01BE\u01BE\u01BE\u01BE\u01B8\u01B8"+
    "\u01B8\u01B8\u01BA\u01BA\u01BA\u01BA\u01C0\u01C2\u01C2\u01C4\u01C6\u01C8\u01CA"+
    "\244\u01B4\u01B4\u01B4\u01B4\u01CC\u01CC\u01CC\u01CC\u01B4\u01B4\u01B4\u01B4"+
    "\u01CC\u01CC\u01CC\u01CC\u01B4\u01B4\u01B4\u01B4\u01CC\u01CC\u01CC\u01CC\u01B8"+
    "\u01B4\u01CE\u01B4\u01BA\u01D0\u01D2\u01D4\312\u01B4\u01CE\u01B4\u01D6\u01D6"+
    "\u01D2\312\u01B8\u01B4\244\u01B4\u01BA\u01D8\u01DA\312\u01B8\u01B4\u01DC\u01B4"+
    "\u01BA\u01DE\u01E0\312\244\u01B4\u01CE\u01B4\u01E2\u01E4\u01D2\u01E6\u01E8"+
    "\u01E8\u01E8\u01EA\u01E8\u01EC\u01AE\u01EE\u01F0\u01F0\u01F0\020\u01F2\u01F4"+
    "\u01F2\u01F4\020\020\020\020\u01F6\u01F8\u01F8\u01FA\u01FC\u01FC\u01FE\020"+
    "\u0200\u0202\020\u0204\u0206\020\u0208\u020A\020\020\020\244\244\244\244\244"+
    "\244\244\244\244\244\244\244\244\244\u01AE\u01AE\u01AE\u020C\244\110\110\110"+
    "\u020E\u0208\u0210\u0212\u0212\u0212\u0212\u0212\u020E\u0208\u020A\244\244"+
    "\244\244\244\244\244\244\072\072\072\072\072\072\072\072\244\244\244\244\244"+
    "\244\244\244\244\244\244\244\244\244\244\244\316\316\316\316\316\316\u0144"+
    "\u0102\u0146\u0102\244\244\244\244\244\244\244\244\244\244\244\244\244\244"+
    "\074\u0214\074\u0216\074\u0218\362\200\362\u021A\u0216\074\u0216\362\362\074"+
    "\074\074\u0214\u021C\u0214\u021E\362\u0220\362\u0216\220\224\u0222\u0224\244"+
    "\244\244\244\244\244\244\244\244\244\244\u0226\122\122\122\122\122\122\u0228"+
    "\u0228\u0228\u0228\u0228\u0228\u022A\u022A\u022C\u022C\u022C\u022C\u022C\u022C"+
    "\u022E\u022E\u0230\u0232\244\244\244\244\244\244\u0234\u0234\u0236\074\074"+
    "\u0234\074\074\u0236\u0238\074\u0236\074\074\074\u0236\074\074\074\074\074"+
    "\074\074\074\074\074\074\074\074\074\074\u0234\074\u0236\u0236\074\074\074"+
    "\074\074\074\074\074\074\074\074\074\074\074\074\244\244\244\244\244\244\u023A"+
    "\u023C\036\u0234\u023C\u023C\u023C\u0234\u023A\u020E\u023A\036\u0234\u023C"+
    "\u023C\u023A\u023C\036\036\036\u0234\u023A\u023C\u023C\u023C\u023C\u0234\u0234"+
    "\u023A\u023A\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\036\u0234\u0234"+
    "\u023C\u023C\u0234\u0234\u0234\u0234\u023A\036\036\u023C\u023C\u023C\u023C"+
    "\u0234\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C"+
    "\u023C\u023C\u023C\036\u023A\u023C\036\u0234\u0234\036\u0234\u0234\u0234\u0234"+
    "\u023C\u0234\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\036\u0234"+
    "\u0234\u023C\u0234\u0234\u0234\u0234\u023A\u023C\u023C\u0234\u023C\u0234\u0234"+
    "\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u023C\u0234"+
    "\u023C\244\244\244\244\244\244\244\074\074\074\074\u023C\u023C\074\074\074"+
    "\074\074\074\074\074\074\074\u023C\074\074\074\u023E\u0240\074\074\074\074"+
    "\074\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0242\u0244\074\074"+
    "\074\074\074\074\074\074\074\074\074\u0246\074\074\u0224\244\244\074\074\074"+
    "\074\074\074\074\074\074\074\074\074\074\074\074\074\074\074\074\u0224\244"+
    "\244\244\244\244\244\244\244\244\244\244\244\074\074\074\074\074\u0224\244"+
    "\244\244\244\244\244\244\244\244\244\u0248\u0248\u0248\u0248\u0248\u0248\u0248"+
    "\u0248\u0248\u0248\u024A\u024A\u024A\u024A\u024A\u024A\u024A\u024A\u024A\u024A"+
    "\u024C\u024C\u024C\u024C\u024C\u024C\u024C\u024C\u024C\u024C\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u024E\u024E\u024E"+
    "\u024E\u024E\u024E\u024E\u024E\u024E\u024E\u024E\u024E\u024E\u0250\u0250\u0250"+
    "\u0250\u0250\u0250\u0250\u0250\u0250\u0250\u0250\u0250\u0250\u0252\244\244"+
    "\244\244\244\244\244\244\244\244\074\074\074\074\074\074\074\074\074\074\074"+
    "\244\244\244\244\244\074\074\074\074\074\074\074\074\074\074\074\u0238\074"+
    "\074\074\074\u0238\074\074\074\074\074\074\074\074\074\074\074\074\074\074"+
    "\074\074\074\074\074\074\074\074\074\074\074\074\074\244\244\244\244\074\074"+
    "\074\074\074\074\074\074\074\074\244\244\u0244\074\074\074\074\074\074\074"+
    "\074\074\074\u0238\074\244\244\244\244\244\244\244\u0244\074\u0224\074\074"+
    "\244\074\074\074\074\074\074\074\074\074\074\074\074\074\074\u0244\074\074"+
    "\074\074\074\074\074\074\074\074\074\074\074\074\074\074\074\u0244\u0244\074"+
    "\u0224\244\u0224\074\074\074\u0224\u0244\074\074\074\244\244\244\244\244\244"+
    "\244\u0254\u0254\u0254\u0254\u0254\u0256\u0256\u0256\u0256\u0256\u0258\u0258"+
    "\u0258\u0258\u0258\u0224\244\074\074\074\074\074\074\074\074\074\074\074\074"+
    "\u0244\074\074\074\074\074\074\u0224\074\074\074\074\074\074\074\074\074\074"+
    "\074\074\074\u0244\074\074\244\244\244\244\244\244\244\244\074\074\074\074"+
    "\074\074\244\244\012\020\u025A\u025C\022\022\022\022\022\074\022\022\022\022"+
    "\u025E\u0260\u0262\u0264\u0264\u0264\u0264\316\316\316\u0266\304\304\074\u0268"+
    "\u026A\244\074\224\224\224\224\224\224\224\224\224\224\u0166\244\u011E\u026C"+
    "\310\314\224\224\224\224\224\224\224\224\224\224\224\224\224\u026E\304\314"+
    "\244\244\u0158\224\224\224\224\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\u0166\244\u0158\224\224\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\u0166\u0182\u0188\u0188\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0170"+
    "\244\u0188\u0188\u0188\u0188\u0188\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\244\244\244\244\244\244\244\244\244\244"+
    "\244\244\244\244\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\244\u018E\u0270\u0270\u0270\u0270\u0270\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0170\244\244\244\244\244\244\244\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\244\244\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0170\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0170\244\u018E\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182"+
    "\u0182\u0182\u0182\u0182\u0182\u0182\u0182\u0182\244\224\224\224\224\224\224"+
    "\224\224\224\224\224\244\244\244\244\244\224\224\224\244\244\244\244\244\244"+
    "\244\244\244\244\244\244\244\224\224\224\224\224\224\u0166\244\074\074\074"+
    "\074\074\074\074\074\074\244\074\074\074\074\074\074\074\074\u0244\074\074"+
    "\074\074\074\u0224\074\u0224\u0224\244\244\244\244\244\244\244\244\244\244"+
    "\244\244\224\224\244\244\244\244\244\244\244\244\244\244\244\244\244\244\u0272"+
    "\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272\u0272"+
    "\u0272\u0272\u0274\u0274\u0274\u0274\u0274\u0274\u0274\u0274\u0274\u0274\u0274"+
    "\u0274\u0274\u0274\u0274\u0274\224\224\224\224\224\224\224\244\244\244\244"+
    "\244\244\244\244\244\u01B4\u01B4\u01B4\u01CE\244\244\244\244\244\u0276\u01B4"+
    "\u01B4\244\244\u0278\u027A\u0124\u0124\u0124\u0124\u027C\u0124\u0124\u0124"+
    "\u0124\u0124\u0124\u0126\u0124\u0124\u0126\u0126\u0124\u0278\u0126\u0124\u0124"+
    "\u0124\u0124\u0124\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\244\244\244\244\244\244\244\244"+
    "\244\244\244\244\244\244\244\244\u0130\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u018A\244\244\244\244\244\244\244\244\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\244"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\244\244"+
    "\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\244\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\244\244\316\316\244\244\244\244\244\244\u027E"+
    "\u0280\u0282\u0284\u0284\u0284\u0284\u0284\u0284\u0284\u01A2\244\u0286\020"+
    "\u0204\u0288\034\u012C\u028A\020\u025E\u0284\u0284\u028C\020\u028E\u0234\u0290"+
    "\u0292\u01FE\244\244\u0132\u0134\u0134\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132\u0132"+
    "\u0134\u0154\u0286\014\016\020\u018A\024\026\030\032\032\032\032\032\034\u0234"+
    "\u0294\042\044\044\044\044\044\044\044\044\044\044\044\044\u0296\u0298\052"+
    "\054\056\056\056\056\056\056\056\056\056\056\056\056\u029A\u029C\u0290\u0286"+
    "\u018A\u0204\224\224\224\224\224\u029E\224\224\224\224\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\224\224\224\304\224\224\224\224\224"+
    "\224\224\224\224\224\224\224\224\224\224\u0166\244\224\224\224\244\224\224"+
    "\224\244\224\224\224\244\224\u0166\244\072\u02A0\u02A2\u02A4\u0238\u0234\u0236"+
    "\u0224\244\244\244\244\u0154\u01AE\074\244";

  // The A table has 678 entries for a total of 2712 bytes.

  private static final int A[] = new int[678];
  private static final String A_DATA =
    "\u4800\u100F\u4800\u100F\u4800\u100F\u5800\u400F\u5000\u400F\u5800\u400F\u6000"+
    "\u400F\u5000\u400F\u5000\u400F\u5000\u400F\u6000\u400C\u6800\030\u6800\030"+
    "\u2800\030\u2800\u601A\u2800\030\u6800\030\u6800\030\uE800\025\uE800\026\u6800"+
    "\030\u2800\031\u3800\030\u2800\024\u3800\030\u2000\030\u1800\u3609\u1800\u3609"+
    "\u3800\030\u6800\030\uE800\031\u6800\031\uE800\031\u6800\030\u6800\030\202"+
    "\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\uE800\025\u6800\030\uE800\026\u6800\033"+
    "\u6800\u5017\u6800\033\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\uE800\025\u6800"+
    "\031\uE800\026\u6800\031\u4800\u100F\u4800\u100F\u5000\u100F\u3800\014\u6800"+
    "\030\u2800\u601A\u2800\u601A\u6800\034\u6800\034\u6800\033\u6800\034\000\u7002"+
    "\uE800\035\u6800\031\u6800\024\u6800\034\u6800\033\u2800\034\u2800\031\u1800"+
    "\u060B\u1800\u060B\u6800\033\u07FD\u7002\u6800\034\u6800\030\u6800\033\u1800"+
    "\u050B\000\u7002\uE800\036\u6800\u080B\u6800\u080B\u6800\u080B\u6800\030\202"+
    "\u7001\202\u7001\202\u7001\u6800\031\202\u7001\u07FD\u7002\201\u7002\201\u7002"+
    "\201\u7002\u6800\031\201\u7002\u061D\u7002\006\u7001\005\u7002\u04E6\u7001"+
    "\u03A1\u7002\000\u7002\006\u7001\005\u7002\006\u7001\005\u7002\u07FD\u7002"+
    "\u061E\u7001\006\u7001\000\u7002\u034A\u7001\u033A\u7001\006\u7001\005\u7002"+
    "\u0336\u7001\u0336\u7001\006\u7001\005\u7002\000\u7002\u013E\u7001\u032A\u7001"+
    "\u032E\u7001\006\u7001\u033E\u7001\u067D\u7002\u034E\u7001\u0346\u7001\000"+
    "\u7002\000\u7002\u034E\u7001\u0356\u7001\000\u7002\u035A\u7001\u036A\u7001"+
    "\006\u7001\005\u7002\u036A\u7001\005\u7002\u0366\u7001\u0366\u7001\006\u7001"+
    "\005\u7002\u036E\u7001\000\u7002\000\u7005\000\u7002\u0721\u7002\000\u7005"+
    "\000\u7005\012\uF001\007\uF003\011\uF002\012\uF001\007\uF003\011\uF002\011"+
    "\uF002\006\u7001\005\u7002\u013D\u7002\u07FD\u7002\012\uF001\u067E\u7001\u0722"+
    "\u7001\u7800\000\u7800\000\000\u7002\u0349\u7002\u0339\u7002\000\u7002\u0335"+
    "\u7002\u0335\u7002\000\u7002\u0329\u7002\000\u7002\u032D\u7002\u0335\u7002"+
    "\000\u7002\000\u7002\u033D\u7002\u0345\u7002\u034D\u7002\000\u7002\u034D\u7002"+
    "\u0355\u7002\000\u7002\000\u7002\u0359\u7002\u0369\u7002\000\u7002\000\u7002"+
    "\u0369\u7002\u0365\u7002\u0365\u7002\u036D\u7002\000\u7002\000\u7004\000\u7004"+
    "\000\u7004\u6800\033\u6800\033\000\u7004\u6800\033\u6800\033\000\u7004\u7800"+
    "\000\u4000\u3006\u4000\u3006\u4000\u3006\u46B1\u3006\u4000\u3006\u7800\000"+
    "\u6800\030\u7800\000\232\u7001\u6800\030\226\u7001\226\u7001\226\u7001\u7800"+
    "\000\u0102\u7001\u7800\000\376\u7001\376\u7001\u07FD\u7002\202\u7001\u7800"+
    "\000\202\u7001\231\u7002\225\u7002\225\u7002\225\u7002\u07FD\u7002\201\u7002"+
    "\175\u7002\201\u7002\u0101\u7002\375\u7002\375\u7002\u7800\000\371\u7002\345"+
    "\u7002\000\u7001\000\u7001\000\u7001\275\u7002\331\u7002\000\u7002\u0159\u7002"+
    "\u0141\u7002\u013D\u7002\000\u7002\u0142\u7001\u0142\u7001\u0141\u7002\u0141"+
    "\u7002\000\034\u4000\u3006\u4000\007\u4000\007\000\u7001\006\u7001\005\u7002"+
    "\u7800\000\u7800\000\006\u7001\u7800\000\302\u7001\302\u7001\302\u7001\302"+
    "\u7001\u7800\000\u7800\000\000\u7004\000\030\000\030\u7800\000\301\u7002\301"+
    "\u7002\301\u7002\301\u7002\u07FD\u7002\u7800\000\000\030\u6800\024\u7800\000"+
    "\u7800\000\u4000\u3006\u0800\030\u4000\u3006\u4000\u3006\u0800\030\u0800\u7005"+
    "\u0800\u7005\u0800\u7005\u7800\000\u0800\u7005\u0800\030\u0800\030\u7800\000"+
    "\u3800\030\u7800\000\u7800\000\u1000\030\u7800\000\u1000\u7005\u1000\u7005"+
    "\u1000\u7005\u1000\u7005\u7800\000\u1000\u7004\u1000\u7005\u1000\u7005\u4000"+
    "\u3006\u3000\u3409\u3000\u3409\u2800\030\u3000\030\u3000\030\u1000\030\u4000"+
    "\u3006\u1000\u7005\u1000\030\u1000\u7005\u4000\u3006\u4000\007\u4000\007\u4000"+
    "\u3006\u4000\u3006\u1000\u7004\u1000\u7004\u4000\u3006\u4000\u3006\u6800\034"+
    "\u1000\u7005\u1000\034\u1000\034\u7800\000\u1000\030\u1000\030\u7800\000\u4800"+
    "\u1010\u4000\u3006\000\u3008\u7800\000\000\u7005\u4000\u3006\000\u7005\000"+
    "\u3008\000\u3008\000\u3008\u4000\u3006\000\u7005\u4000\u3006\000\u3749\000"+
    "\u3749\000\030\u7800\000\000\u7005\u7800\000\u7800\000\000\u3008\000\u3008"+
    "\u7800\000\000\u05AB\000\u05AB\000\013\000\u06EB\000\034\u7800\000\u7800\000"+
    "\000\u3749\000\u074B\000\u080B\000\u080B\u7800\000\u7800\000\u2800\u601A\000"+
    "\u7004\u4000\u3006\u4000\u3006\000\030\000\u3609\000\u3609\000\u7005\000\034"+
    "\000\034\000\034\000\030\000\034\000\u3409\000\u3409\000\013\000\013\u6800"+
    "\025\u6800\026\u4000\u3006\000\034\u7800\000\000\034\000\030\000\u3709\000"+
    "\u3709\000\u3709\000\u070B\000\u042B\000\u054B\000\u080B\000\u080B\000\u080B"+
    "\000\u7005\000\030\000\030\000\u7005\u6000\u400C\000\u7005\000\u7005\u6800"+
    "\025\u6800\026\u7800\000\000\u046B\000\u046B\000\u046B\u7800\000\000\030\u2800"+
    "\u601A\u6800\024\u6800\030\u6800\030\u4800\u1010\u4800\u1010\u4800\u1010\u4800"+
    "\u1010\u7800\000\000\u7005\000\u7004\u07FD\u7002\u07FD\u7002\u07FD\u7002\355"+
    "\u7002\u07E1\u7002\u07E1\u7002\u07E2\u7001\u07E2\u7001\u07FD\u7002\u07E1\u7002"+
    "\u7800\000\u07E2\u7001\u06D9\u7002\u06D9\u7002\u06A9\u7002\u06A9\u7002\u0671"+
    "\u7002\u0671\u7002\u0601\u7002\u0601\u7002\u0641\u7002\u0641\u7002\u0609\u7002"+
    "\u0609\u7002\u07FF\uF003\u07FF\uF003\u07FD\u7002\u7800\000\u06DA\u7001\u06DA"+
    "\u7001\u07FF\uF003\u6800\033\u07FD\u7002\u6800\033\u06AA\u7001\u06AA\u7001"+
    "\u0672\u7001\u0672\u7001\u7800\000\u6800\033\u07FD\u7002\u07E5\u7002\u0642"+
    "\u7001\u0642\u7001\u07E6\u7001\u6800\033\u0602\u7001\u0602\u7001\u060A\u7001"+
    "\u060A\u7001\u6800\033\u7800\000\u6000\u400C\u6000\u400C\u6000\u400C\u6000"+
    "\014\u6000\u400C\u4800\u400C\000\u1010\u0800\u1010\u6800\024\u6800\024\u6800"+
    "\035\u6800\036\u6800\025\u6800\035\u6000\u400D\u5000\u400E\u7800\u1010\u7800"+
    "\u1010\u7800\u1010\u6000\014\u2800\030\u2800\030\u2800\030\u6800\030\u6800"+
    "\030\uE800\035\uE800\036\u6800\030\u6800\030\u6800\u5017\u6800\u5017\u6800"+
    "\030\u6800\031\uE800\025\uE800\026\u7800\000\u1800\u060B\u7800\000\u2800\031"+
    "\u2800\031\uE800\026\000\u7002\u1800\u040B\u1800\u040B\000\u7001\u6800\034"+
    "\u6800\034\000\u7001\000\u7002\000\u7001\000\u7001\000\u7002\u07FE\u7001\u6800"+
    "\034\u07FE\u7001\u07FE\u7001\u2800\034\000\u7002\000\u7005\000\u7002\u6800"+
    "\034\u7800\000\u7800\000\u6800\u080B\102\u742A\102\u742A\102\u780A\102\u780A"+
    "\101\u762A\101\u762A\101\u780A\101\u780A\000\u780A\000\u780A\000\u780A\000"+
    "\u700A\u6800\031\u6800\031\u6800\031\u6800\034\u6800\034\u6800\031\u6800\031"+
    "\uE800\031\uE800\031\uE800\031\u6800\034\uE800\025\uE800\026\u6800\034\000"+
    "\034\u6800\034\u7800\000\u6800\034\u6800\034\000\034\u1800\u042B\u1800\u042B"+
    "\u1800\u05AB\u1800\u05AB\u1800\u072B\u1800\u072B\152\034\152\034\151\034\151"+
    "\034\u1800\u06CB\u7800\000\u6800\u056B\u6800\u056B\u6800\u042B\u6800\u042B"+
    "\u6800\u06EB\u6800\u06EB\u6800\034\000\u7004\000\u7005\000\u772A\u6800\024"+
    "\u6800\025\u6800\026\u6800\026\u6800\034\000\u740A\000\u740A\000\u740A\u6800"+
    "\024\000\u7004\000\u764A\000\u776A\000\u748A\u7800\000\u4000\u3006\u6800\033"+
    "\000\u7005\u6800\u5017\000\u042B\000\u042B\000\023\000\023\000\022\000\022"+
    "\u7800\000\u07FD\u7002\u7800\000\u0800\u7005\u4000\u3006\u0800\u7005\u0800"+
    "\u7005\u2800\031\u6800\030\u6800\024\u6800\024\u6800\u5017\u6800\u5017\u6800"+
    "\025\u6800\026\u6800\025\u7800\000\u6800\030\u6800\u5017\u6800\u5017\u6800"+
    "\030\u3800\030\u6800\026\u2800\030\u2800\031\u2800\024\u6800\031\u7800\000"+
    "\u6800\030\u2800\u601A\u6800\031\u6800\030\202\u7FE1\u6800\025\u6800\030\u6800"+
    "\026\201\u7FE2\u6800\025\u6800\031\u6800\026\000\u7004\000\u7005\u6800\031"+
    "\u6800\033\u6800\034\u2800\u601A\u2800\u601A\u7800\000";

  // In all, the character property tables require 12472 bytes.

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
            if (j != 2048) throw new RuntimeException();
        }
        { // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
            if (Y_DATA.length() != 3856) throw new RuntimeException();
            for (int i=0; i<3856; ++i)
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
            if (j != 678) throw new RuntimeException();
        }

    }
}
