/*
 * @(#)LineBreakData.java	1.20 03/01/23
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
 * The LineBreakData contains data used by SimpleTextBoundary
 * to determine line breaks.
 * @see #BreakIterator
 */
final class LineBreakData extends TextBoundaryData
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
    
    private static final byte BREAK                 = 0;
    //always breaks (must be present as first item)
    private static final byte blank                 = 1;
    //spaces, tabs, nulls.
    private static final byte cr                    = 2;
    //carriage return
    private static final byte nonBlank              = 3;
    //everything not included elsewhere
    private static final byte op                    = 4;
    //hyphens....
    private static final byte jwrd                  = 5;
    //hiragana, katakana, and kanji
    private static final byte preJwrd               = 6;
    //characters that bind to the beginning of a Japanese word
    private static final byte postJwrd              = 7;
    //characters that bind to the end of a Japanese word
    private static final byte digit                 = 8;
    //digits
    private static final byte numPunct              = 9;
    //punctuation that can appear within a number
    private static final byte currency              = 10;
    //currency symbols that can precede a number
    private static final byte quote                 = 11;
    // the ASCII quotation mark
    private static final byte nsm                   = 12;
    // non-spacing marks
    private static final byte nbsp                  = 13;
    // non-breaking characters
    private static final byte EOS                   = 14;
    private static final int COL_COUNT = 15;

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    public LineBreakData() {
        super(kLineForward, kLineBackward, kLineMap);
    }

    // This table locates legal line-break positions.  i.e., a process that word-wraps a line of
    // text can use this version of the BreakIterator to tell it where the legal places for
    // breaking a line are.
    // The rules implemented here are as follows:
    // 1) There is always a legal break position after a line or paragraph separator, but
    //    one can occur before only when the preceding character is also a line or paragraph
    //    separator.  (The CR-LF sequence is also kept together.)  (states 4 and 7)
    // 2) There is never a break before a non-spacing mark, unless it's preceded by a line
    //    or paragraph separator.  (the nsm column)
    // 3) There is never a break on either side of a non-breaking space (or other non-breaking
    //    chartacters).  (the nbsp column, and state 1)
    // 4) There is always a break before and after Kanji and Kana characters, except for certain
    //    punctuation that must be kept with the following character and certain punctuation
    //    and diacritic marks that must be kept with the preceding character.  (states 5 and 8)
    // 5) There is always a legal break position following a dash, except when it is followed
    //    by a digit, a line/paragraph separator, or whitespace. (state 6)
    // 6) There is never a break before a whitespace character.  There is a break after a
    //    whitespace character, except when it's followed by a line/paragraph separator.
    //    (state 2)
    // 7) Breaks don't occur anywhere else.  (state 1)
    private static final byte kLineForwardData[] =
    {
        // brk         bl             cr             nBl
        // op          kan            prJ            poJ
        // dgt         np             curr           quote
        // nsm         nbsp           EOS
        // 00 - dummy state
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,
        // 01 - main dispatch state.  This state eats pre-Kanji punctuation,
        // non-breaking spaces, and non-spacing diacritics without transitioning
        // to other states.
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  (byte)(SI+5),  (byte)(SI+1),  (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  (byte)(SI+1),  (byte)(SI+3),
        (byte)(SI+1),  (byte)(SI+1),  SI_STOP,
        // 02 - This state eats whitespce and stops on almost anything else
        // (the exceptions are non-breaking spaces, which go back to 1,
        // and CRs and LFs)
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+2),  (byte)(SI+1),  SI_STOP,
        // 03 - This state eats non-whitespace characters that aren't
        // otherwise accounted for.  The only difference between
        // this and state 1 is that it stops on Kanji (you can break
        // between any two Kanji characters)
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  SI_STOP,       (byte)(SI+1),  (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  (byte)(SI+1),  (byte)(SI+3),
        (byte)(SI+3),  (byte)(SI+1),  SI_STOP,
        // 04 - this is the state you go to when you see a hard line-
        // breaking character.  It eats that character and stops.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,
        // 05 - this is the state that handles Kanji.  It handles
        // post-Kanji punctuation, whitespace, non-breaking spaces,
        // and line terminators, but stops on everything else
        // (including more Kanji)
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+8),  SI_STOP,       SI_STOP,
        (byte)(SI+5),  (byte)(SI+1),  SI_STOP,
        // 06 - This state handles dashes.  It'll continue on
        // whitespace, more dashes, line terminators, and digits
        // (the dash is a minus sign), but stops on everything else
        // (unless there's an nbsp, a dash is always a legal
        // break position).
        (byte)(SI+4),  SI_STOP,       (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+9),  SI_STOP,       (byte)(SI+11), SI_STOP,
        (byte)(SI+6),  (byte)(SI+1),  SI_STOP,
        // 07 - This state handles CRs.  A CR is a line terminator
        // when it appears alone, and considered "half" a line
        // terminator when it occurs right before any other line
        // terminator (except another CR).
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,
        // 08 - This state eats post-Kanji punctuation, and passes
        // whitespace, non-breaking characters, dashes, line terminators,
        // etc.  It stops on almost everything else.
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+8),  SI_STOP,       (byte)(SI+3),
        (byte)(SI+8),  (byte)(SI+1),  SI_STOP,
        // 09 - This state is the main "number" state.  It eats
        // digits.
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  SI_STOP,       SI_STOP,       (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+10), (byte)(SI+10), (byte)(SI+3),
        (byte)(SI+9),  (byte)(SI+1),  SI_STOP,
        // 10 - This state is the secondary "number" state.  It
        // easts punctuation that can occur inside a number.
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  SI_STOP,       SI_STOP,
        (byte)(SI+10), (byte)(SI+1),  SI_STOP,
        // 11 - This state is here to allow a dash to go before a
        // currency symbol and still be treated as a minus sign
        // (if the character after the currency symbol is a digit).
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        (byte)(SI+9),  STOP,          STOP,          STOP,
        (byte)(11),    (byte)(SI+1),  STOP
    };

    private static final WordBreakTable kLineForward
        = new WordBreakTable(COL_COUNT, kLineForwardData);

    // This table locates unambiguous break positions when iterating backward.
    // It implements the following rules:
    // 1) For most characters, there is a break before them if they're preceded
    //    by whitespace, Kanji, or a line/paragraph separator. (CR-LF is kept together)
    // 2) There is a break before a Kanji character, except when it's preceded by
    //    a Kanji-prefix character.  (state 4)
    // 3) There is NOT a break before a Kanji-suffix character, except when preceded
    //    by whitespace, a line/paragraph separator, or a dash. (state 3)
    // 4) There is never a break on either side of a non-break character.  (the nbsp column)
    // 5) There is never a break before a non-spacing mark (the nsm column)
    // [In this set of rules, "break" means "unambiguous break position".  There may sometimes
    // be actual breaks in positions this table always skips.]
    private static final byte kLineBackwardData[] =
    {
        // brk         bl             cr             nBl
        // op          kan            prJ            poJ
        // dgt         np             curr           quote
        // nsm         nbsp           EOS
        /*00*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,
        /*01*/
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+1),  (byte)(SI+2),  STOP,
        /*02*/
        STOP,          STOP,          STOP,          (byte)(SI+2),
        (byte)(SI+2),  STOP,          (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  STOP,
        /*03*/
        STOP,          STOP,          STOP,          (byte)(SI+2),
        STOP,          (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+3),  (byte)(SI+2),  STOP,
        /*04*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          (byte)(SI+2),  STOP,
        STOP,          STOP,          (byte)(SI+2),  STOP,
        (byte)(SI+4),  (byte)(SI+4),  STOP
    };

    private static final WordBreakTable kLineBackward
        = new WordBreakTable(COL_COUNT, kLineBackwardData);

    private static final int kRawMapping[] =
    {
        nonBlank, //UNASSIGNED             = 0,
        nonBlank, //UPPERCASE_LETTER       = 1,
        nonBlank, //LOWERCASE_LETTER       = 2,
        nonBlank, //TITLECASE_LETTER       = 3,
        nonBlank, //MODIFIER_LETTER        = 4,
        nonBlank, //OTHER_LETTER           = 5,
        nsm,      //NON_SPACING_MARK       = 6,
        nsm,      //ENCLOSING_MARK         = 7,
        nonBlank, //COMBINING_SPACING_MARK = 8,
        digit, //DECIMAL_DIGIT_NUMBER      = 9,
        nonBlank, //LETTER_NUMBER          = 10,
        digit, //OTHER_NUMBER              = 11,
        blank, //SPACE_SEPARATOR           = 12,
        blank, //LINE_SEPARATOR            = 13,
        blank, //PARAGRAPH_SEPARATOR       = 14,     ???????????
        blank, //CONTROL                   = 15,
        nonBlank, //PRIVATE_USE            = 16,
        nonBlank, //FORMAT                 = 17
        nonBlank, //????                   = 18,
        nonBlank, //SURROGATE              = 19,
        op, //DASH_PUNCTUATION             = 20,
        preJwrd, //START_PUNCTUATION       = 21,
        postJwrd, //END_PUNCTUATION        = 22,
        nonBlank, //CONNECTOR_PUNCTUATION  = 23,
        nonBlank, //OTHER_PUNCTUATION      = 24,
        nonBlank, //MATH_SYMBOL            = 25,
        preJwrd, //CURRENCY_SYMBOL         = 26,
        nonBlank, //MODIFIER_SYMBOL        = 27,
        nonBlank, //OTHER_SYMBOL           = 28,
        preJwrd, //INITIAL_QUOTE_PUNCTUATION = 29,
        postJwrd, //FINAL_QUOTE_PUNCTUATION = 30,
    };

    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order as
        //      required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_END_OF_TEXT, BREAK),
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION,
                           ASCII_FORM_FEED, BREAK),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, cr),
        new SpecialMapping(ASCII_EXCLAMATION_MARK, postJwrd),
        new SpecialMapping(ASCII_QUOTATION_MARK, quote),
        new SpecialMapping(ASCII_DOLLAR_SIGN, preJwrd),
        new SpecialMapping(ASCII_PERCENT, postJwrd),
        new SpecialMapping(ASCII_COMMA, numPunct),
        new SpecialMapping(ASCII_FULL_STOP, numPunct),
        new SpecialMapping(ASCII_COLON, ASCII_SEMICOLON, postJwrd),
        new SpecialMapping(ASCII_QUESTION_MARK, postJwrd),
        new SpecialMapping(ASCII_NONBREAKING_SPACE, nbsp),
        new SpecialMapping(ASCII_CENT_SIGN, postJwrd),
        new SpecialMapping(LATIN1_SOFTHYPHEN, op),
        new SpecialMapping(LATIN1_DEGREE_SIGN, postJwrd),
        new SpecialMapping(ARABIC_PERCENT_SIGN, postJwrd),
        new SpecialMapping(FIGURE_SPACE, nbsp),
        new SpecialMapping(NONBREAKING_HYPHEN, nbsp),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR,
                           PUNCTUATION_PARAGRAPH_SEPARATOR, BREAK),
        new SpecialMapping(PER_MILLE_SIGN, postJwrd),
        new SpecialMapping(PER_TEN_THOUSAND_SIGN, postJwrd),
        new SpecialMapping(PRIME, TRIPLE_PRIME, postJwrd),
        new SpecialMapping(DEGREE_CELSIUS, postJwrd),
        new SpecialMapping(DEGREE_FAHRENHEIT, postJwrd),
        new SpecialMapping(PUNCTUATION_IDEOGRAPHIC_COMMA,
                           PUNCTUATION_IDEOGRAPHIC_FULL_STOP, postJwrd),
        new SpecialMapping(IDEOGRAPHIC_ITERATION_MARK, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_A, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_I, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_I, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_U, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_U, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_E, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_E, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_O, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_O, HIRAGANA_LETTER_DI, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_TU, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_TU, HIRAGANA_LETTER_MO, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YA, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YA, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YU, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YU, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YO, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YO, HIRAGANA_LETTER_RO, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_WA, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_WA, HIRAGANA_LETTER_VU, jwrd),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, postJwrd),
        new SpecialMapping(HIRAGANA_ITERATION_MARK, HIRAGANA_VOICED_ITERATION_MARK, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_A, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_A, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_I, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_I, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_U, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_U, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_E, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_E, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_O, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_O, KATAKANA_LETTER_DI, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_TU, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_TU, KATAKANA_LETTER_MO, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YA, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YA, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YU, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YU, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YO, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YO, KATAKANA_LETTER_RO, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_WA, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_WA, KATAKANA_LETTER_VU, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_KA, KATAKANA_LETTER_SMALL_KE, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_VA, KATAKANA_LETTER_VO, jwrd),
        new SpecialMapping(KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK, postJwrd),
        new SpecialMapping(KATAKANA_ITERATION_MARK, KATAKANA_VOICED_ITERATION_MARK, postJwrd),
        new SpecialMapping(UNICODE_LOW_BOUND_HAN,UNICODE_HIGH_BOUND_HAN,jwrd),
        new SpecialMapping(CJK_COMPATIBILITY_F900,
                           CJK_COMPATIBILITY_FA2D, jwrd),
        new SpecialMapping(UNICODE_ZERO_WIDTH_NON_BREAKING_SPACE, nbsp),
        new SpecialMapping(FULLWIDTH_EXCLAMATION_MARK, postJwrd),
        new SpecialMapping(FULLWIDTH_COMMA, postJwrd),
        new SpecialMapping(FULLWIDTH_FULL_STOP, postJwrd),
        new SpecialMapping(FULLWIDTH_QUESTION_MARK, postJwrd),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean LineExceptionFlags[] = {
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
        true,           // kSpaceSeparator          = 12,
        true,           // kLineSeparator           = 13,
        true,           // kParagraphSeparator      = 14,
        true,           // kControlCharacter        = 15,
        true,           // kFormatCharacter         = 16,
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
        true,           // kOtherSymbol             = 28,
        false,          // kInitialQuotePunctuation = 29,
        false,          // kFinalQuotePunctuation   = 30,
    };

    private static final int kLineAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            blank,  blank,  blank,  BREAK,  blank,  blank,  blank,  blank,
        //  bs      ht      lf      vt      ff      cr      so      si
            blank,  BREAK,  BREAK,  BREAK,  BREAK,  cr,     blank,  blank,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  can     em      sub     esc     fs      gs      rs      us
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  sp      !         "      #         $         %         &         '
            blank,  postJwrd, quote, nonBlank, currency, postJwrd, nonBlank, nonBlank,
        //  (       )          *         +         ,         -   .         /
            preJwrd, postJwrd, nonBlank, nonBlank, numPunct, op, numPunct, nonBlank,
        //  0         1         2         3         4         5         6         7
            digit,    digit,    digit,    digit,    digit,    digit,    digit,    digit,
        //  8         9         :         ;         <         =         >         ?
            digit,    digit,    postJwrd, postJwrd, nonBlank, nonBlank, nonBlank, postJwrd,
        //  @         A         B         C         D         E         F         G
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  H         I         J         K         L         M         N         O
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  P         Q         R         S         T         U         V         W
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  X         Y         Z         [        \         ]         ^         _
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, postJwrd, nonBlank, nonBlank,
        //  `         a         b         c         d         e         f         g
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  h         i         j         k         l         m         n         o
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  p         q         r         s         t         u         v         w
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  x         y         z         {        |         }         ~         del
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, postJwrd, nonBlank, blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  nbsp      inv-!     cents     pounds    currency  yen       broken-bar  section
            nbsp,  nonBlank, postJwrd, currency, currency, currency, nonBlank, nonBlank,
        //  umlaut    copyright super-a   gui-left  not       soft-hyph registered  macron
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, op, nonBlank, nonBlank,
        //  degree    +/-       super-2   super-3   acute     micro     paragraph  bullet
            postJwrd, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  cedilla   super-1   super-o   gui-right 1/4       1/2       3/4      inv-?
            nonBlank, nonBlank, nonBlank, postJwrd, digit,    digit,    digit,    nonBlank,
        //  A-grave   A-acute   A-hat     A-tilde   A-umlaut A-ring    AE        C-cedilla
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  E-grave   E-acute   E-hat     E-umlaut  I-grave   I-acute   I-hat    I-umlaut
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  Edh       N-tilde   O-grave   O-acute   O-hat     O-tilde   O-umlaut times
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  O=slash   U-grave   U-acute   U-hat     U-umlaut  Y-acute   Thorn    ess-zed
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  a-grave   a-acute   a-hat     a-tilde   a-umlaut  a-ring    ae       c-cedilla
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  e-grave   e-acute   e-hat     e-umlaut  i-grave   i-acute   i-hat    i-umlaut
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  edh       n-tilde   o-grave   o-acute   o-hat     o-tilde   o-umlaut  over
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  o-slash   u-grave   u-acute   u-hat     u-umlaut  y-acute   thorn    y=umlaut
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank
    };

    private static final UnicodeClassMapping kLineMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, LineExceptionFlags,
        kLineAsciiValues);
}
