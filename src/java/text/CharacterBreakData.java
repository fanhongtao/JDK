/*
 * @(#)CharacterBreakData.java	1.17 03/01/23
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
 * The CharacterBreakData contains data used by SimpleTextBoundary
 * to determine character breaks.
 * @see #BreakIterator
 */
final class CharacterBreakData extends TextBoundaryData
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
    
    // constant names for the category numbers
    private static final byte accent_diacritic = 0; // all Unicode non-spacing marks
    private static final byte baseForm = 1;   // everything that isn't accounted for elsewhere
    private static final byte baseCR = 2;     // the ASCII carriage return
    private static final byte baseLF = 3;     // all other line/paragraph separators
    private static final byte choseong = 4;   // Korean initial consonant
    private static final byte jungseong = 5;  // Korean vowel
    private static final byte jongseong = 6;  // Korean final consonant
    private static final byte EOS = 7;        // end of string
    private static final int COL_COUNT = 8;   // the number of items in this list (and therefore,
                                              // the number of columns in the state tables)

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    public CharacterBreakData() {
        super(kCharacterForwardTable, kCharacterBackwardTable, kCharacterMap);
    }

    // This table locates logical character ("grapheme") boundaries.  A logical
    // character is a sequence of Unicode code-point values that are seen as a single
    // character by the user.  This table implements the following logic:
    //  1) Unless otherwise mentioned, each individual code point is a character.
    //  2) A regular character followed by one or more Unicode non-spacing marks is
    //     treated as a single character.
    //  3) The CR-LF sequence is treated as a single character.
    //  4) A Hangul syllable spelled out with individual jamos is treated as a single
    //     character, according to the rules specified under "Conjoining Jamo Behavior"
    //     in the Unicode standard.
    // UTF-16 surrogate pairs are NOT trated as single characters in this version of the
    // character-breaking tables.  Rule 1 is implemented by state 2, rule 2 is implemented
    // by rules 3 and 7 (line/paragraph separators are NOT kept together with any non-
    // spacing marks that follow them!).  Rule 4 is implemented with states 4, 5, and 6.
    private static final byte kCharacterForwardData[] =
    {
        // acct        base             cr              lf
        // cho         jung             jong            EOS
        STOP,          STOP,            STOP,           STOP,
        STOP,          STOP,            STOP,           STOP,

        // 1 - main dispatch state
        (byte)(SI+2),  (byte)(SI+2),    (byte)(SI+3),   (byte)(SI+7),
        (byte)(SI+4),  (byte)(SI+5),    (byte)(SI+6),   SI_STOP,

        // 2 - if the character is regular base or accent, we end up in this
        // state, which eats accents until it sees something else
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,

        // 3 - a CR character causes a transition.  If the next character is
        // an LF, it transitions to state 7; otherwise, it does exactly
        // the same thing as state 7
        SI_STOP,       SI_STOP,         SI_STOP,        (byte)(SI+7),
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,

        // 4 - this state eats Korean initial consonants and uses
        // states 5 and 6 to take care of the other parts of the syllable
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        (byte)(SI+4),  (byte)(SI+5),    (byte)(SI+6),   SI_STOP,

        // 5 - this state eats Korean vowels
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,      (byte)(SI+5),    (byte)(SI+6),    SI_STOP,

        // 6 - this state eats Korean final consonants
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         (byte)(SI+6),   SI_STOP,

        // 7 - This state is reached when an LF or other line separator
        // is seen.  It eats the LF and stops.
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP
    };
    private static final WordBreakTable kCharacterForwardTable =
    new WordBreakTable(COL_COUNT, kCharacterForwardData);
    
    // This table implements the backward-seeking logic.  Here, we merely
    // eat characters until we see a Hangul syllable-initial consonant,
    // an ASCII carriage return, a "base" character (most characters), or
    // the end of the string.  These characters all represent unambiguous
    // break positions.
    private static final byte kCharacterBackwardData[] =
    {
        // acct         base            cr              lf
        // cho          jung            jong            EOS
        STOP,           STOP,           STOP,           STOP,
        STOP,           STOP,           STOP,           STOP,

        // 1
        (byte)(SI+1),   SI_STOP,        SI_STOP,        (byte)(SI+1),
        SI_STOP,        (byte)(SI+1),   (byte)(SI+1),   SI_STOP
    };

    private static final WordBreakTable kCharacterBackwardTable =
    new WordBreakTable(COL_COUNT, kCharacterBackwardData);
    private static final int kRawMapping[] =
    {
        baseForm, //UNASSIGNED      = 0,
        baseForm, //UPPERCASE_LETTER    = 1,
        baseForm, //LOWERCASE_LETTER    = 2,
        baseForm, //TITLECASE_LETTER    = 3,
        baseForm, //MODIFIER_LETTER     = 4,
        baseForm, //OTHER_LETTER        = 5,
        accent_diacritic, //NON_SPACING_MARK    = 6,
        accent_diacritic, //ENCLOSING_MARK      = 7,
        baseForm, //COMBINING_SPACING_MARK  = 8,
        baseForm, //DECIMAL_DIGIT_NUMBER    = 9,
        baseForm, //LETTER_NUMBER       = 10,
        baseForm, //OTHER_NUMBER        = 11,
        baseForm, //SPACE_SEPARATOR     = 12,
        baseForm, //LINE_SEPARATOR      = 13,
        baseForm, //PARAGRAPH_SEPARATOR = 14,
        baseForm, //CONTROL         = 15,
        baseForm, //FORMAT      = 16,
        baseForm, //????            = 17,
        baseForm, //PRIVATE_USE     = 18,
        baseForm, //SURROGATE        = 19,
        baseForm, //DASH_PUNCTUATION    = 20,
        baseForm, //START_PUNCTUATION    = 21,
        baseForm, //END_PUNCTUATION     = 22,
        baseForm, //CONNECTOR_PUNCTUATION   = 23,
        baseForm, //OTHER_PUNCTUATION   = 24,
        baseForm, //MATH_SYMBOL     = 25,
        baseForm, //CURRENCY_SYMBOL     = 26,
        baseForm, //MODIFIER_SYMBOL     = 27,
        baseForm, //OTHER_SYMBOL        = 28;
        baseForm, //INITIAL_QUOTE_PUNCTUATION = 29;
        baseForm, //FINAL_QUOTE_PUNCTUATION   = 30;
    };

    private static final SpecialMapping kExceptionChar[] = //{};
    {
        new SpecialMapping(ASCII_LINEFEED, baseLF),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, baseCR),
        new SpecialMapping(HANGUL_CHOSEONG_LOW, HANGUL_CHOSEONG_HIGH, choseong),
        new SpecialMapping(HANGUL_JUNGSEONG_LOW, HANGUL_JUNGSEONG_HIGH, jungseong),
        new SpecialMapping(HANGUL_JONGSEONG_LOW, HANGUL_JONGSEONG_HIGH, jongseong),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR, PUNCTUATION_PARAGRAPH_SEPARATOR, baseLF),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean CharacterExceptionFlags[] = {
        false,          // kNonCharacter            = 0,
        false,          // kUppercaseLetter         = 1,
        false,          // kLowercaseLetter         = 2,
        false,          // kTitlecaseLetter         = 3,
        false,          // kModifierLetter          = 4,
        true,           // kOtherLetter             = 5,
        false,          // kNonSpacingMark          = 6,
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
        false,          // kDashPunctuation         = 20,
        false,          // kOpenPunctuation         = 21,
        false,          // kClosePunctuation        = 22,
        false,          // kConnectorPunctuation    = 23,
        false,          // kOtherPunctuation        = 24,
        false,          // kMathSymbol              = 25,
        false,          // kCurrencySymbol          = 26,
        false,          // kModifierSymbol          = 27,
        false,          // kOtherSymbol             = 28,
        false,          // kInitialQuotePunctuation = 29,
        false,          // kFinalQuotePunctuation   = 30,
    };

    private static final int kCharacterAsciiValues[] = {
        //  null      soh       stx       etx       eot       enq       ask       bell
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  bs        ht        lf      vt        ff        cr      so        si
            baseForm, baseForm, baseLF, baseForm, baseForm, baseCR, baseForm, baseForm,
        //  dle       dc1       dc2       dc3       dc4       nak       syn       etb
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  can       em        sub       esc       fs        gs        rs        us
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  sp        !         "         #         $         %         &         '
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  (         )         *         +         ,         -         .         /
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  0         1         2         3         4         5         6         7
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  8         9         :         ;         <         =         >         ?
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  @         A         B         C         D         E         F         G
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  H         I         J         K         L         M         N         O
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  P         Q         R         S         T         U         V         W
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  X         Y         Z         [         \         ]         ^         _
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  `         a         b         c         d         e         f         g
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  h         i         j         k         l         m         n         o
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  p         q         r         s         t         u         v         w
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  x         y         z         {         |         }         ~         del
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  nbsp      inv-!     cents     pounds    currency  yen       broken-bar  section
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  umlaut    copyright super-a   gui-left  not       soft-hyph registered  macron
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  degree    +/-       super-2   super-3   acute     micro     paragraph  bullet
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  cedilla   super-1   super-o   gui-right 1/4       1/2       3/4      inv-?
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  A-grave   A-acute   A-hat     A-tilde   A-umlaut A-ring    AE        C-cedilla
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  E-grave   E-acute   E-hat     E-umlaut  I-grave   I-acute   I-hat    I-umlaut
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  Edh       N-tilde   O-grave   O-acute   O-hat     O-tilde   O-umlaut times
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  O-slash   U-grave   U-acute   U-hat     U-umlaut  Y-acute   Thorn    ess-zed
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  a-grave   a-acute   a-hat     a-tilde   a-umlaut  a-ring    ae       c-cedilla
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  e-grave   e-acute   e-hat     e-umlaut  i-grave   i-acute   i-hat    i-umlaut
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  edh       n-tilde   o-grave   o-acute   o-hat     o-tilde   o-umlaut  over
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  o-slash   u-grave   u-acute   u-hat     u-umlaut  y-acute   thorn    y-umlaut
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm
    };

    private static final UnicodeClassMapping kCharacterMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, CharacterExceptionFlags,
        kCharacterAsciiValues);
}

