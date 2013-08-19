/*
 * @(#)WordBreakData.java	1.19 03/01/23
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
 * The WordBreakData contains data used by SimpleTextBoundary
 * to determine word breaks.
 * @see #BreakIterator
 */
final class WordBreakData extends TextBoundaryData
{
    // THEORY OF OPERATION:  This class contains all the tables necessary to do
    // character-break iteration.  This class descends from TextBoundaryData, which
    // is abstract.  This class doesn't define any non-static members; it inherits the
    // non-static members from TextBoundaryData and fills them in with pointers to
    // the static members defined here.
    //   There are two main parts to a TextBoundaryData object: the state-transition
    // tables and the character-mapping tables.  The forward state table defines the
    // transitions for a deterministic finite state machine that locates character
    // boundaries.  The rows are the states and the columns are character categories.
    // The cell values consist of two parts: The first is the row number of the next
    // state to transition to, or a "stop" value (0).  (Because 0 is the stop value
    // rather than a valid state number, row 0 of the array isn't ever looked at; we
    // fill it with STOP values by convention.)  The second part is a flag indicating
    // whether the iterator should update its break position on this transition.  When
    // the flag is set, the sign bit of the value is turned on (SI is used to represent
    // the flag bit being turned on-- we do it this way rather than just using negative
    // numbers because we still need to see the SI flag when the value of the transition
    // is STOP.  SI_STOP is used to denote this.)  The starting state in all state tables
    // is 1.
    //   The backward state table works the same way as the forward state table, but is
    // usually simplified.  The iterator uses the backward state table only to find a
    // "safe place" to start iterating forward.  It then seeks forward from the "safe
    // place" to the actual break position using the forward table.  A "safe place" is
    // a spot in the text that is guaranteed to be a break position.
    //   The character-category mapping tables are split into several pieces, one for
    // each stage of the category-mapping process: 1) kRawMapping maps generic Unicode
    // character categories to the character categories used by this break iterator.
    // The index of the array is the Unicode category number as returned by
    // Character.getType().  2) The kExceptionFlags table is a table of Boolean values
    // indicating whether all the characters in the Unicode category have the
    // raw-mapping value.  The rows correspond to the rows of the raw-mapping table.  If
    // an entry is true, then we find the right category using...  3) The kExceptionChar
    // table.  This table is a sorted list of SpecialMapping objects.  Each entry defines
    // a range of contiguous characters that share the same category and the category
    // number.  This list is binary-searched to find an entry corresponding to the 
    // charactre being mapped.  Only characters whose breaking category is different from
    // the raw-mapping value (the breaking category for their Unicode category) are
    // listed in this table.  4) The kAsciiValues table is a fast-path table for characters
    // in the Latin1 range.  This table maps straight from a character value to a
    // category number, bypassing all the other tables.  The programmer must take care
    // that all of the different category-mapping tables are consistent.
    //   In the current implementation, all of these tables are created and maintained
    // by hand, not using a tool.
    
    private static final byte BREAK = 0;    // characters not listed in any other category
    private static final byte letter = 1;   // letters
    private static final byte number = 2;   // digits
    private static final byte midLetter = 3;// punctuation that can occur within a word
    private static final byte midLetNum = 4;// punctuation that can occur inside a wors or a number
    private static final byte preNum = 5;   // characters that may serve as a prefix to a number
    private static final byte postNum = 6;  // characters that may serve as a suffix to a number
    private static final byte midNum = 7;   // punctuation that can occur inside a number
    private static final byte preMidNum = 8;// punctuation that can occur either at the beginning
                                            //  of or inside a number
    private static final byte blank = 9;    // white space (other than always-break characters)
    private static final byte lf = 10;      // the ASCII LF character
    private static final byte kata = 11;    // Katakana
    private static final byte hira = 12;    // Hiragana
    private static final byte kanji = 13;   // all CJK ideographs
    private static final byte diacrit = 14; // CJK diacriticals
    private static final byte cr = 15;      // the ASCII CR character
    private static final byte nsm = 16;     // Unicode non-spacing marks
    private static final byte EOS = 17;     // end of string
    private static final int COL_COUNT = 18;// number of categories

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    public WordBreakData() {
        super(kWordForward, kWordBackward, kWordMap);
    }

    // This table locates word boundaries, as this is defined for "find whole words"
    // searches and often for double-click selection.  In this case, "words" are kept
    // separate from whitespace and punctuation.
    // The rules implemented here are as follows:
    // 1) Unless mentioned below, all characters are treated as "words" unto themselves
    //    and have break positions on both sides (state 14)
    // 2) A "word" is kept together, and consists of a sequence of letters.  Certain
    //    punctuation marks, such as apostrophes and hyphens, are allowed inside a "word"
    //    without causing a break, but only if they're flanked on both sides by letters.
    //    (states 2 and 7)
    // 3) A "number" is kept together, and consists of an optional prefix character (such
    //    as a minus, decimal point, or currency symbol), followed by a sequence of digits,
    //    followed by an optional suffix character (such as a percent sign).  The sequence
    //    of digits may contain certain punctuation characters (such as commas and periods),
    //    but only if they're flanked on both sides by digits. (states 3, 8, and 14)
    // 4) If a "number" and  "word" occur in succession without any intervening characters,
    //    they are kept together.  This allows sequences like "$30F3" or "ascii2ebcdic" to
    //    be treated as single units.  (transitions between states 2 and 3)
    // 5) Sequences of whitespace are kept together.  (state 6)
    // 6) The CR-LF sequence is kept together.  (states 4 and 13)
    // 7) A sequence of Kanji is kept together.  (state 12)
    // 8) Sequences of Hiragana and Katakana are kept together, and may include their
    //    common diacritical marks.  (states 10 and 11)
    // [The logic for Kanji and Kana characters is an approximation.  There is no way
    // to detect real Japanese word boundaries without a dictionary.]
    // 9) Unicode non-spacing marks are completely transparent to the algorithm.
    //    (see the "nsm" column)
    private static final byte kWordForwardData[] =
    {
        // brk         let            num            mLe            mLN
        // prN         poN            mNu            pMN            blk
        // lf          kat            hir            kan            dia
        // cr          nsm            EOS

        // 0 - dummy state
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1 - main dispatch state
        (byte)(SI+14), (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+14), (byte)(SI+14),
        (byte)(SI+5),  (byte)(SI+14), (byte)(SI+14), (byte)(SI+5),  (byte)(SI+6),
        (byte)(SI+4),  (byte)(SI+10), (byte)(SI+11), (byte)(SI+12), (byte)(SI+9),
        (byte)(SI+13), (byte)(1),     SI_STOP,

        // 2 - This state eats letters, advances to state 3 for numbers, and
        // goes to state 7 for mid-word punctuation.
        SI_STOP,       (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+7),  (byte)(SI+7),
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(2),     SI_STOP,

        // 3 - This state eats digits, advances to state 2 for letters, uses
        // state 8 to handle mid-number punctuation, and goes to state 14 for
        // number-suffix characters.
        SI_STOP,       (byte)(SI+2),  (byte)(SI+3),  SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+14), (byte)(SI+8),  (byte)(SI+8),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(3),     SI_STOP,

        // 4 - This state handles LFs by eating the LF and stopping.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,

        // 5 - This state handles number-prefix characters.  If the next character
        // is a digit, it goes to state 3; otherwise, it stops (the character is
        // a "word" by itself).
        SI_STOP,       SI_STOP,       (byte)(SI+3),  SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(5),     SI_STOP,

        // 6 - This state eats whitespace and stops on everything else.
        // (Except for CRs and LFs, which are kept together with the whitespace.)
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+6),
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+13), (byte)(6),     SI_STOP,

        // 7 - This state handles mid-word punctuation: If the next character is a
        // letter, we're still in the word and we keep going.  Otherwise, we stop,
        // and the break was actually before this character.
        STOP,          (byte)(SI+2),  STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(7),     STOP,

        // 8 - This state handles mid-number punctuation: If the next character is a
        // digit, we're still in the word and we keep going.  Otherwise, we stop,
        // and the break position is actually before this character.
        STOP,          STOP,          (byte)(SI+3),  STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(8),     STOP,

        // 9 - This state handles CJK diacritics.  It'll keep going if the next
        // character is CJK; otherwise, it stops.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(SI+10), (byte)(SI+11), SI_STOP,       (byte)(SI+9),
        SI_STOP,       (byte)(9),     SI_STOP,

        // 10 - This state eats Katakana and CJK discritics.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(SI+10), SI_STOP,       SI_STOP,       (byte)(SI+10),
        SI_STOP,       (byte)(10),    SI_STOP,

        // 11 - This state eats Hiragana and CJK diacritics.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       (byte)(SI+11), SI_STOP,       (byte)(SI+11),
        SI_STOP,       (byte)(11),    SI_STOP,

        // 12 - This state eats Kanji.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+12), SI_STOP,
        SI_STOP,       (byte)(12),    SI_STOP,

        // 13 - This state handles CRs, which are "words" unto themselves (or
        // with preceding whitespace) unless followed by an LFs.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,

        // 14 - This state handles LFs and number-suffix characters (when they
        // actually end a number) by eating the character and stopping.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(14),    SI_STOP
    };
    private static final WordBreakTable kWordForward =
    new WordBreakTable(COL_COUNT, kWordForwardData);

    // This table is a completely-reversed version of the forward table.
    private static final byte kWordBackwardData[] =
    {
        // brk         let            num            mLe            mLN
        // prN         poN            mNu            pMN            blk
        // lf          kat            hir            kan            dia
        // cr          nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1
        (byte)(SI+6),  (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+4),  (byte)(SI+5),
        (byte)(SI+6),  (byte)(SI+7),  (byte)(SI+7),  (byte)(SI+5),  (byte)(SI+8),
        (byte)(SI+8),  (byte)(SI+9),  (byte)(SI+10), (byte)(SI+12), (byte)(SI+11),
        (byte)(SI+8),  (byte)(1),     STOP,

        // 2
        STOP,          (byte)(SI+2), (byte)(SI+3),   (byte)(4),     (byte)(4),
        STOP,          STOP,          STOP,          (byte)(4),     STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(2),     STOP,

        // 3
        STOP,          (byte)(SI+2),  (byte)(SI+3),  STOP,          (byte)(7),
        SI_STOP,       STOP,          (byte)(7),     (byte)(SI+7),  STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(3),     STOP,

        // 4
        STOP,          (byte)(SI+2),  STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(4),     STOP,

        // 5
        STOP,          (byte)(SI+2), (byte)(SI+3), STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(5),     STOP,

        // 6
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(6),     STOP,

        // 7
        STOP,          STOP,          (byte)(SI+3),  STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(7),     STOP,

        // 8
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          (byte)(SI+8),
        (byte)(SI+8),  STOP,          STOP,          STOP,          STOP,
        (byte)(SI+8),  (byte)(8),     STOP,

        // 9
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(SI+9),  STOP,          STOP,          (byte)(9),
        STOP,          (byte)(9),     STOP,

        // 10
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          (byte)(SI+10),STOP,          (byte)(10),
        STOP,          (byte)(10),    STOP,

        // 11
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(SI+9), (byte)(SI+10),  STOP,          (byte)(SI+11),
        STOP,          (byte)(11),    STOP,

        // 12
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          (byte)(SI+12), STOP,
        STOP,          (byte)(12),    STOP
    };
    private static final WordBreakTable kWordBackward =
    new WordBreakTable(COL_COUNT, kWordBackwardData);
    private static final int kRawMapping[] =
    {
        BREAK,     // UNASSIGNED             = 0,
        letter,    // UPPERCASE_LETTER       = 1,
        letter,    // LOWERCASE_LETTER       = 2,
        letter,    // TITLECASE_LETTER       = 3,
        letter,    // MODIFIER_LETTER        = 4,
        letter,    // OTHER_LETTER           = 5,
        nsm,       // NON_SPACING_MARK       = 6,
        nsm,       // ENCLOSING_MARK         = 7,
        BREAK,     // COMBINING_SPACING_MARK = 8,
        number,    // DECIMAL_DIGIT_NUMBER   = 9,
        letter,    // LETTER_NUMBER          = 10,
        number,    // OTHER_NUMBER           = 11,
        blank,     // SPACE_SEPARATOR        = 12,
        BREAK,     // LINE_SEPARATOR         = 13,
        BREAK,     // PARAGRAPH_SEPARATOR    = 14,
        BREAK,     // CONTROL                = 15,
        BREAK,     // FORMAT                 = 16
        BREAK,     // ????                   = 17,
        BREAK,     // PRIVATE_USE            = 18,
        BREAK,     // SURROGATE              = 19,
        midLetter, // DASH_PUNCTUATION       = 20,
        BREAK,     // START_PUNCTUATION      = 21,
        BREAK,     // END_PUNCTUATION        = 22,
        BREAK,     // CONNECTOR_PUNCTUATION  = 23,
        BREAK,     // OTHER_PUNCTUATION      = 24,
        BREAK,     // MATH_SYMBOL            = 25,
        preNum,    // CURRENCY_SYMBOL        = 26,
        BREAK,     // MODIFIER_SYMBOL        = 27,
        BREAK,     // OTHER_SYMBOL           = 28,
        BREAK,     // INITIAL_QUOTE_PUNCTUATION = 29,
        BREAK,     // FINAL_QUOTE_PUNCTUATION = 30,
    };
    private static final SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, blank),
        new SpecialMapping(ASCII_LINEFEED, lf),
        new SpecialMapping(ASCII_FORM_FEED, lf),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, cr),
        new SpecialMapping(ASCII_QUOTATION_MARK, midLetNum),
        new SpecialMapping(ASCII_NUMBER_SIGN, preNum),
        new SpecialMapping(ASCII_PERCENT, postNum),
        new SpecialMapping(ASCII_AMPERSAND, postNum),
        new SpecialMapping(ASCII_APOSTROPHE, midLetNum),
        new SpecialMapping(ASCII_COMMA, midNum),
        new SpecialMapping(ASCII_FULL_STOP, preMidNum),
        new SpecialMapping(ASCII_CENT_SIGN, postNum),
        new SpecialMapping(LATIN1_SOFTHYPHEN, midLetter),
        new SpecialMapping(ARABIC_PERCENT_SIGN, postNum),
        new SpecialMapping(ARABIC_DECIMAL_SEPARATOR, midNum),
        new SpecialMapping(PUNCTUATION_HYPHENATION_POINT, midLetter),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR,
                           PUNCTUATION_PARAGRAPH_SEPARATOR, lf),
        new SpecialMapping(PER_MILLE_SIGN, postNum),
        new SpecialMapping(PER_TEN_THOUSAND_SIGN, postNum),
        new SpecialMapping(IDEOGRAPHIC_ITERATION_MARK, kanji),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, HIRAGANA_LETTER_VU, hira),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, diacrit),
        new SpecialMapping(HIRAGANA_ITERATION_MARK, HIRAGANA_VOICED_ITERATION_MARK, hira),
        new SpecialMapping(KATAKANA_LETTER_SMALL_A,
                           KATAKANA_LETTER_SMALL_KE, kata),
        new SpecialMapping(KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK, diacrit),
        new SpecialMapping(KATAKANA_ITERATION_MARK, KATAKANA_VOICED_ITERATION_MARK, kata),
        new SpecialMapping(UNICODE_LOW_BOUND_HAN,
                           UNICODE_HIGH_BOUND_HAN, kanji),
        new SpecialMapping(HANGUL_SYL_LOW, HANGUL_SYL_HIGH, letter),
        new SpecialMapping(CJK_COMPATIBILITY_F900,
                           CJK_COMPATIBILITY_FA2D, kanji),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean WordExceptionFlags[] = {
        false,          // kNonCharacter            = 0,
        false,          // kUppercaseLetter         = 1,
        false,          // kLowercaseLetter         = 2,
        false,          // kTitlecaseLetter         = 3,
        true,           // kModifierLetter          = 4,
        true,           // kOtherLetter             = 5,
        true,           // kNonSpacingMark          = 6,
        false,          // kEnclosingMark           = 7,
        false,          // kCombiningSpacingMark    = 8,
        false,          // kDecimalNumber           = 9,
        false,          // kLetterNumber            = 10,
        false,          // kOtherNumber             = 11,
        false,          // kSpaceSeparator          = 12,
        true,           // kLineSeparator           = 13,
        true,           // kParagraphSeparator      = 14,
        true,           // kControlCharacter        = 15,
        false,          // kFormatCharacter         = 16,
        false,          // UNDEFINED                = 17,
        false,          // kPrivateUseCharacter     = 18,
        false,          // kSurrogate               = 19,
        true,           // kDashPunctuation         = 20,
        false,          // kOpenPunctuation         = 21,
        false,          // kClosePunctuation        = 22,
        false,          // kConnectorPunctuation    = 23,
        true,           // kOtherPunctuation        = 24,
        false,          // kMathSymbol              = 25,
        true,           // kCurrencySymbol          = 26,
        false,          // kModifierSymbol          = 27,
        false,          // kOtherSymbol             = 28,
        false,          // kInitialQuotePunctuation = 29,
        false,          // kFinalQuotePunctuation   = 30,
    };

    private static final int kWordAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  bs      ht      lf      vt      ff      cr      so      si
            BREAK,  blank,  lf,     BREAK,  lf,     cr,     BREAK,  BREAK,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  can     em      sub     esc     fs      gs      rs      us
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  sp      !       "          #       $       %        &        '
            blank,  BREAK,  midLetNum, preNum, preNum, postNum, postNum, midLetNum,
        //  (       )       *       +       ,       -          .          /
            BREAK,  BREAK,  BREAK,  BREAK,  midNum, midLetter, preMidNum, BREAK,
        //  0       1       2       3       4       5       6       7
            number, number, number, number, number, number, number, number,
        //  8       9       :       ;       <       =       >       ?
            number, number, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  @       A       B       C       D       E       F       G
            BREAK,  letter, letter, letter, letter, letter, letter, letter,
        //  H       I       J       K       L       M       N       O
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  P       Q       R       S       T       U       V       W
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  X       Y       Z       [       \       ]       ^       _
            letter, letter, letter, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  `       a       b       c       d       e       f       g
            BREAK,  letter, letter, letter, letter, letter, letter, letter,
        //  h       i       j       k       l       m       n       o
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  p       q       r       s       t       u       v       w
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  x       y       z       {       |       }       ~       del
            letter, letter, letter, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  nbsp      inv-!     cents     pounds    currency  yen       broken-bar  section
            blank,  BREAK,  postNum, preNum, preNum, preNum, BREAK,  BREAK,
        //  umlaut    copyright super-a   gui-left  not       soft-hyph registered  macron
            BREAK,  BREAK,  letter, BREAK,  BREAK,  midLetter, BREAK, BREAK,
        //  degree    +/-       super-2   super-3   acute     micro     paragraph  bullet
            BREAK,  BREAK,  number, number, BREAK,  letter, BREAK,  BREAK,
        //  cedilla   super-1   super-o   gui-right 1/4       1/2       3/4      inv-?
            BREAK,  letter, BREAK,  BREAK,  number, number, number, BREAK,
        //  A-grave   A-acute   A-hat     A-tilde   A-umlaut A-ring    AE        C-cedilla
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  E-grave   E-acute   E-hat     E-umlaut  I-grave   I-acute   I-hat    I-umlaut
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  Edh       N-tilde   O-grave   O-acute   O-hat     O-tilde   O-umlaut times
            letter, letter, letter, letter, letter, letter, letter, BREAK,
        //  O-slash   U-grave   U-acute   U-hat     U-umlaut  Y-acute   Thorn    ess-zed
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  a-grave   a-acute   a-hat     a-tilde   a-umlaut  a-ring    ae       c-cedilla
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  e-grave   e-acute   e-hat     e-umlaut  i-grave   i-acute   i-hat    i-umlaut
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  edh       n-tilde   o-grave   o-acute   o-hat     o-tilde   o-umlaut  over
            letter, letter, letter, letter, letter, letter, letter, BREAK,
        //  o-slash   u-grave   u-acute   u-hat     u-umlaut  y-acute   thorn    y-umlaut
            letter, letter, letter, letter, letter, letter, letter, letter
    };

    private static final UnicodeClassMapping kWordMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, WordExceptionFlags,
        kWordAsciiValues);
}

