/*
 * @(#)SentenceBreakData.java	1.23 03/01/23
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
 * The SentenceBreakData contains data used by SimpleTextBoundary
 * to determine sentence breaks.
 * @see #BreakIterator
 */
final class SentenceBreakData extends TextBoundaryData
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
    
    private static final byte other = 0;        // characters not otherwise mentioned
    private static final byte space = 1;        // whitespace
    private static final byte terminator = 2;   // characters that always mark the end of a
                                                //  sentence (? ! etc.)
    private static final byte ambiguosTerm = 3; // characters that may mark the end of a
                                                //  sentence (periods)
    private static final byte openBracket = 4;  // Opening punctuation that may occur before
                                                //  the beginning of a sentence
    private static final byte closeBracket = 5; // Closing punctuation that may occur after
                                                //  the end of a sentence
    private static final byte cjk = 6;          // Characters where the previous sentence
                                                //  does not have a space after a terminator.
                                                //  Common in Japanese, Chinese, and Korean
    private static final byte paragraphBreak = 7;
                                                // the Unicode paragraph-break character
    private static final byte lowerCase = 8;    // lower-case letters
    private static final byte upperCase = 9;    // upper-case letters
    private static final byte number = 10;      // digits
    private static final byte quote = 11;       // the ASCII quote mark, which may be
                                                //  either opening or closing punctuation
    private static final byte nsm = 12;         // Unicode non-spacing marks
    private static final byte EOS = 13;         // end of string

    private static final int COL_COUNT = 14;    // number of categories

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    public SentenceBreakData() {
        super(kSentenceForward, kSentenceBackward, kSentenceMap);
    }

    // This table implements a relative simple heuristic for locating sentence
    // boundaries.  It doesn't always work right (one common case is "Mr. Smith",
    // where it'll break between "Mr." and "Smith"), but is a pretty close
    // approximation.
    // The table implements these rules:
    // 1) Unless otherwise mentioned, don't break between characters. (state 1)
    // 2) If you see an unambiguous sentence terminator, continue seeking past more
    //    terminators (if there are any), closing punctuation (if any), whitespace
    //    (if any), and one paragraph separator (if any), in that order.  The first
    //    time you see an unexpected character, that's where the break goes.
    //    (states 2 and 3)
    // 3) If you see a period followed by a Kanji character, there's a sentence break
    //    after the period.  If you see a period followed by whitespace or opening
    //    punctuation, there's a break after the whitespace or before the opening
    //    punctuation unless the next character is a lower-case letter,
    //    a digit, closing punctuation, or a paragraph separator.  If you see a
    //    period followed by whitespace, followed by opening punctuation, there's a
    //    break after the whitespace if the first character after the opening punctuation
    //    is a capital letter, and a break after the opening punctuation if the next
    //    character is anything other than a lower-case letter.  (states 5, 6, and 7)
    // 4) There is ALWAYS a sentence break after a paragraph separator. (state 4)
    // 5) Non-spacing marks are transparent to the algorithm.  (the nsm column)
    private static final byte kSentenceForwardData[] =
    {
        // other       space          terminator     ambTerm
        // open        close          CJK            PB
        // lower       upper          digit          Quote
        // nsm            EOS

        // 0 - dummy state
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,

        // 1 - this is the main state, which just eats characters
        // until it sees a paragraph break or a sentence-terminating
        // character  (all states loop back to here if they
        // don't see the right sequence of things that denotes the
        // end of a sentence).
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),  (byte)(SI+5),
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+4),
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),
        (byte)(SI+1),  SI_STOP,

        // 2 - This state is triggered when we pass an unambiguous
        // sentence terminator.  It eats terminating characters
        // and closing punctuation, passes whitespace and paragraph
        // separators, switches to state 5 on periods, and stops
        // on everything else.
        SI_STOP,       (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+5),
        SI_STOP,       (byte)(SI+2),  SI_STOP,       (byte)(SI+4),
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+2),
        (byte)(SI+2),  SI_STOP,

        // 3 - This state eats trailing whitespace after a sentence.
        // It passes paragraph separators, but stops on anything else.
        SI_STOP,       (byte)(SI+3),  SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+4),
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+3),  SI_STOP,

        // 4 - This state handles paragraph separators by eating them
        // and then stopping.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,

        // 5 - This state handles periods and other ambiguous sentence
        // terminators.  It'll go back to state 2 on an unambiguous
        // terminator.  It'll eat trailing punctuation and additional
        // periods.  It stops on Kanji (a sentence in Kanji doesn't
        // have to be followed by whitespace), advances to state 6
        // on whitespace, and loops back to the starting state
        // on anything else (i.e., this wasn't actually the end
        // of a sentence).
        (byte)(SI+1),  (byte)(SI+6),  (byte)(SI+2),  (byte)(SI+5),
        (byte)(SI+7),  (byte)(SI+5),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+5),
        (byte)(SI+5),  SI_STOP,

        // 6 - This state handles whitespace after a period.  It eats
        // any additional whitespace and passes paragraph breaks.
        // It'll loop back on lower-case letters and digits (not the
        // end of a sentence) and stop (yes the end of a sentence)
        // on most other characters.  Opening punctuation requires
        // more lookahead and transitions to state 7.
        SI_STOP,       (byte)(SI+6),  SI_STOP,       SI_STOP,
        (byte)(SI+7),  (byte)(SI+1),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  SI_STOP,       (byte)(SI+1),  SI_STOP,
        (byte)(SI+6),  SI_STOP,

        // 7 - This state handles opening punctuation after whitespace
        // after a period.  It stops unless the next character is a
        // lower-case letter (it rewinds back to before the sequence
        // opening punctuation and THEN stops if the character is an
        // upper-case letter).  It loops (without advancing the break
        // position while eating additional opening punctuation.
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(7),     SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+1),  STOP,          SI_STOP,       SI_STOP,
        (byte)(SI+7),  SI_STOP
    };

    private static final WordBreakTable kSentenceForward
        = new WordBreakTable(COL_COUNT, kSentenceForwardData);

    // This table locates a safe place for backward or random-access iterator
    // to turn around and seek forward.
    // 1) There is never a safe place to turn around before a non-spacing
    //    mark. (state 1)
    // 2) There is always a sentence break after a paragraph separator.
    //    (the PB column)
    // 3) If you see a closing punctuation mark or a Kanji character preceded
    //    by whitespace, we can turn around and seek forward when we see a
    //    sentence terminator.
    private static final byte kSentenceBackwardData[] =
    {
        // other       space          terminator     ambTerm
        // open        close          CJK            PB
        // lower       upper          digit          quote
        // nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,

        // 1
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+1),  STOP,

        // 2
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  STOP,

        // 3
        (byte)(SI+2),  (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+3),  STOP,

        // 4
        (byte)(SI+2),  (byte)(SI+4),  SI_STOP,       SI_STOP,
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+4),  STOP
    };

    private static final WordBreakTable kSentenceBackward
        = new WordBreakTable(COL_COUNT, kSentenceBackwardData);

    private static final int kRawMapping[] =
    {
        other,        // UNASSIGNED             = 0,
        upperCase,    // UPPERCASE_LETTER       = 1,
        lowerCase,    // LOWERCASE_LETTER       = 2,
        other,        // TITLECASE_LETTER       = 3,
        other,        // MODIFIER_LETTER        = 4,
        other,        // OTHER_LETTER           = 5,
        nsm,          // NON_SPACING_MARK       = 6,
        nsm,          // ENCLOSING_MARK         = 7,
        other,        // COMBINING_SPACING_MARK = 8,
        number,       // DECIMAL_DIGIT_NUMBER   = 9,
        number,       // LETTER_NUMBER          = 10,
        number,       // OTHER_NUMBER           = 11,
        space,        // SPACE_SEPARATOR        = 12,
        space,        // LINE_SEPARATOR         = 13,
        space,        // PARAGRAPH_SEPARATOR    = 14,            ???????
        other,        // CONTROL                = 15,
        other,        // PRIVATE_USE            = 16,
        other,        // FORMAT                 = 17,
        other,        // ????                   = 18,
        other,        // SURROGATE              = 19,
        other,        // DASH_PUNCTUATION       = 20,
        openBracket,  // START_PUNCTUATION      = 21,
        closeBracket, // END_PUNCTUATION        = 22,
        other,        // CONNECTOR_PUNCTUATION  = 23,
        other,        // OTHER_PUNCTUATION      = 24,
        other,        // MATH_SYMBOL            = 25,
        other,        // CURRENCY_SYMBOL        = 26,
        other,        // MODIFIER_SYMBOL        = 27,
        other,        // OTHER_SYMBOL           = 28;
        openBracket,  // INITIAL_QUOTE_PUNCTUATION = 29,
        closeBracket, // FINAL_QUOTE_PUNCTUATION = 30,
    };

    private static final SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, space),
        new SpecialMapping(ASCII_LINEFEED, space),
        new SpecialMapping(ASCII_FORM_FEED, terminator),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, space),

        new SpecialMapping(ASCII_EXCLAMATION_MARK, terminator),
        new SpecialMapping(ASCII_QUOTATION_MARK, quote),

        new SpecialMapping(ASCII_APOSTROPHE, quote),

        new SpecialMapping(ASCII_FULL_STOP, ambiguosTerm),
        new SpecialMapping(ASCII_QUESTION_MARK, terminator),
        new SpecialMapping(ASCII_NONBREAKING_SPACE, other),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR, space),
        new SpecialMapping(PUNCTUATION_PARAGRAPH_SEPARATOR, paragraphBreak),
        new SpecialMapping(PUNCTUATION_IDEOGRAPHIC_FULL_STOP, terminator),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, HIRAGANA_LETTER_VU, cjk),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, cjk),         // cjk
        new SpecialMapping(KATAKANA_LETTER_SMALL_A, KATAKANA_LETTER_SMALL_KE,
                           cjk),   // cjk
        new SpecialMapping(UNICODE_LOW_BOUND_HAN, UNICODE_HIGH_BOUND_HAN, cjk),
        new SpecialMapping(CJK_COMPATIBILITY_F900, CJK_COMPATIBILITY_FA2D,cjk),
        new SpecialMapping(UNICODE_ZERO_WIDTH_NON_BREAKING_SPACE, other),
        new SpecialMapping(FULLWIDTH_EXCLAMATION_MARK, terminator),
        new SpecialMapping(FULLWIDTH_FULL_STOP, terminator),
        new SpecialMapping(FULLWIDTH_QUESTION_MARK, terminator),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean SentenceExceptionFlags[] = {
        false,            // kNonCharacter         = 0,
        false,            // kUppercaseLetter      = 1,
        false,            // kLowercaseLetter      = 2,
        false,            // kTitlecaseLetter      = 3,
        false,            // kModifierLetter       = 4,
        true,             // kOtherLetter          = 5,
        true,             // kNonSpacingMark       = 6,
        false,            // kEnclosingMark        = 7,
        false,            // kCombiningSpacingMark = 8,
        false,            // kDecimalNumber        = 9,
        false,            // kLetterNumber         = 10,
        false,            // kOtherNumber          = 11,
        true,             // kSpaceSeparator       = 12,
        true,             // kLineSeparator        = 13,
        true,             // kParagraphSeparator   = 14,
        true,             // kControlCharacter     = 15,
        true,             // kFormatCharacter      = 16,
        false,            // UNDEFINED             = 17,
        false,            // kPrivateUseCharacter  = 18,
        false,            // kSurrogate            = 19,
        false,            // kDashPunctuation      = 20,
        false,            // kOpenPunctuation      = 21,
        false,            // kClosePunctuation     = 22,
        false,            // kConnectorPunctuation = 23,
        true,             // kOtherPunctuation     = 24,
        false,            // kMathSymbol           = 25,
        false,            // kCurrencySymbol       = 26,
        false,            // kModifierSymbol       = 27,
        false,            // kOtherSymbol          = 28,
        false,            // kInitialQuotePunctuation = 29,
        false,            // kFinalQuotePunctuation = 30,
    };

    private static final int kSentenceAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            other,  other,  other,  other,  other,  other,  other,  other,
        //  bs      ht      lf     vt     ff          cr     so     si
            other,  space,  space, other, terminator, space, other, other,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            other,  other,  other,  other,  other,  other,  other,  other,
        //  can     em      sub     esc     fs      gs      rs      us
            other,  other,  other,  other,  other,  other,  other,  other,
        //  sp      !           "      #      $      %      &      '
            space,  terminator, quote, other, other, other, other, quote,
        //  (            )             *      +      ,      -      .             /
            openBracket, closeBracket, other, other, other, other, ambiguosTerm, other,
        //  0       1       2       3       4       5       6       7
            number, number, number, number, number, number, number, number,
        //  8       9       :       ;       <       =       >       ?
            number, number, other,  other,  other,  other,  other,  terminator,
        //  @       A          B          C          D          E          F          G
            other,  upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  H          I          J          K          L          M          N          O
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  P          Q          R          S          T          U          V          W
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  X          Y          Z          [            \      ]             ^      _
            upperCase, upperCase, upperCase, openBracket, other, closeBracket, other, other,
        //  `       a          b          c          d          e          f          g
            other,  lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  h          i          j          k          l          m          n          o
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  p          q          r          s          t          u          v          w
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  x          y          z          {            |      }             ~      del
            lowerCase, lowerCase, lowerCase, openBracket, other, closeBracket, other, other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  nbsp      inv-!     cents     pounds    currency  yen       broken-bar  section
            other,  other,  other,  other,  other,  other,  other,  other,
        //  umlaut    copyright super-a   gui-left  not       soft-hyph registered  macron
            other,  other,  lowerCase, openBracket, other, other, other, other,
        //  degree    +/-       super-2   super-3   acute     micro     paragraph  bullet
            other,  other,  number, number, other,  lowerCase, other, other,
        //  cedilla   super-1   super-o   gui-right 1/4       1/2       3/4      inv-?
            other,  lowerCase, other, closeBracket, number, number, number, other,
        //  A-grave   A-acute   A-hat     A-tilde   A-umlaut A-ring    AE        C-cedilla
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  E-grave   E-acute   E-hat     E-umlaut  I-grave   I-acute   I-hat    I-umlaut
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  Edh       N-tilde   O-grave   O-acute   O-hat     O-tilde   O-umlaut times
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, other,
        //  O=slash   U-grave   U-acute   U-hat     U-umlaut  Y-acute   Thorn    ess-zed
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, lowerCase,
        //  a-grave   a-acute   a-hat     a-tilde   a-umlaut  a-ring    ae       c-cedilla
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  e-grave   e-acute   e-hat     e-umlaut  i-grave   i-acute   i-hat    i-umlaut
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  edh       n-tilde   o-grave   o-acute   o-hat     o-tilde   o-umlaut  over
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, other,
        //  o-slash   u-grave   u-acute   u-hat     u-umlaut  y-acute   thorn    y=umlaut
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase
    };

    private static final UnicodeClassMapping kSentenceMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, SentenceExceptionFlags,
        kSentenceAsciiValues);
}

