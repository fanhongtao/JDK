/*
 * @(#)LineBreakData.java	1.5 97/01/17
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

/**
 * The LineBreakData contains data used by SimpleTextBoundary
 * to determine line breaks.
 * @see #BreakIterator
 */
final class LineBreakData extends TextBoundaryData
{
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
    private static final byte nami                  = 5;
    //namigata (indicates range or section to be filled in) ACTS
    //like non-breaking character except when next to a break or CR
    private static final byte kata                  = 6;
    //large katakana
    private static final byte smlKata               = 7;
    //small katakana (except 'tsu')
    private static final byte smlKtsu               = 8;
    //small katakana tsu
    private static final byte choon                 = 9;
    //choon mark (katakana vowel extender)
    private static final byte hira                  = 10;
    //large hiragana
    private static final byte smlHira               = 11;
    //small hiragana (except 'tsu')
    private static final byte smlHtsu               = 12;
    //small hiragana tsu
    private static final byte hiraDitto             = 13;
    //hiragana dittos
    private static final byte diacrit               = 14;
    // kana diacriticals (dakuten, han-dakuten)
    private static final byte kanji                 = 15;
    private static final byte kanjiDitto            = 16;
    //kanji ditto
    private static final byte preJwrd               = 17;
    //characters that bind to the beginning of a Japanese word
    private static final byte postJwrd              = 18;
    //characters that bind to the end of a Japanese word
    private static final int COL_COUNT = 19;
    private static final byte SI = (byte)0x80;

    private static final byte kLineForwardData[] =
    {
        // brk        bl            cr            nBl
        // op         nmi           kat           smK
        // sKT        cho           hir           smH
        // sHT        hDi           dia           kan
        // kDi        prJ           poJ
        /*00*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),
        /*01*/
        (byte)(SI+4), (byte)(SI+2), (byte)(SI+4), (byte)(SI+3),
        (byte)(SI+17),(byte)(SI+10), (byte)(SI+5), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+6), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+9), (byte)(SI+7),
        (byte)(SI+9), (byte)(SI+8), (byte)(SI+3),
        /*02*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+ 0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        /*03*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+0), (byte)(SI+3),
        (byte)(SI+17),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+8), (byte)(SI+3),
        /*04*/
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+ 0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        /*05*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+12),
        (byte)(SI+11),(byte)(SI+13), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0),(byte)(SI+12), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*06*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),(byte)(SI+15),
        (byte)(SI+14),(byte)(SI+16),(byte)(SI+15), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*07*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+7), (byte)(SI+0), (byte)(SI+3),
        /*08*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+3),
        (byte)(SI+17),(byte)(SI+10), (byte)(SI+5), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+6), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+9), (byte)(SI+7),
        (byte)(SI+9), (byte)(SI+8), (byte)(SI+3),
        /*09*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*10*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+3),
        (byte)(SI+17),(byte)(SI+10), (byte)(SI+5), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+6), (byte)(SI+9),
        (byte)(SI+9), (byte)(SI+9), (byte)(SI+9), (byte)(SI+7),
        (byte)(SI+9), (byte)(SI+8), (byte)(SI+3),
        /*11*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+5), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*12*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+10), (byte)(SI+0),(byte)(SI+12),
        (byte)(SI+11),(byte)(SI+13),(byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*13*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+11),(byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*14*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+6), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*15*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),(byte)(SI+15),
        (byte)(SI+14),(byte)(SI+16), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*16*/
        (byte)(SI+0), (byte)(SI+2), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+14),(byte)(SI+16), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3),
        /*17*/
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+4), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0)
    };

    private static final WordBreakTable kLineForward
        = new WordBreakTable(COL_COUNT, kLineForwardData);

    private static final byte kLineBackwardData[] =
    {
        // brk         bl            cr            nBl
        // op          nmi           kat           smK
        // sKT         cho           hir           smH
        // sHT         hDi           dia           kan
        // kDi         prJ           poJ
        /*00*/
        (byte)(0),     (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),     (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),     (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),     (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),     (byte)(0),    (byte)(0),
        /*01*/
        (byte)(SI+4),  (byte)(SI+1), (byte)(SI+2), (byte)(SI+3),
        (byte)(SI+3),  (byte)(SI+6),(byte)(SI+16), (byte)(SI+9),
        (byte)(SI+10), (byte)(SI+9),(byte)(SI+17),(byte)(SI+11),
        (byte)(SI+12),(byte)(SI+12), (byte)(SI+8), (byte)(SI+7),
        (byte)(SI+13), (byte)(SI+3), (byte)(SI+5),
        /*02*/
        (byte)(SI+4),  (byte)(SI+2),    (byte)(0), (byte)(SI+3),
        (byte)(SI+3),  (byte)(SI+6),(byte)(SI+16), (byte)(SI+9),
        (byte)(SI+10), (byte)(SI+9),(byte)(SI+17),(byte)(SI+11),
        (byte)(SI+12),(byte)(SI+12), (byte)(SI+8), (byte)(SI+7),
        (byte)(SI+13),(byte)(SI+3),  (byte)(SI+5),
        /*03*/
        (byte)(0),    (byte)(0),    (byte)(0), (byte)(SI+3),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3), (byte)(SI+5),
        /*04*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),
        /*05*/
        (byte)(0),    (byte)(0),    (byte)(0), (byte)(SI+3),
        (byte)(0), (byte)(SI+6),(byte)(SI+16), (byte)(SI+9),
        (byte)(SI+10), (byte)(SI+9),(byte)(SI+17),(byte)(SI+11),
        (byte)(SI+12),(byte)(SI+12), (byte)(SI+8), (byte)(SI+7),
        (byte)(SI+13), (byte)(SI+3), (byte)(SI+5),
        /*06*/
        (byte)(SI+4),    (byte)(0),    (byte)(0), (byte)(SI+3),
        (byte)(SI+3), (byte)(SI+6),(byte)(SI+16), (byte)(SI+9),
        (byte)(SI+10), (byte)(SI+9),(byte)(SI+17),(byte)(SI+11),
        (byte)(SI+12),(byte)(SI+12), (byte)(SI+8), (byte)(SI+7),
        (byte)(SI+13), (byte)(SI+3), (byte)(SI+5),
        /*07*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*08*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),(byte)(SI+16),  (byte)(0),
        (byte)(0),    (byte)(0),(byte)(SI+17),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*09*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),(byte)(SI+16),  (byte)(9),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),   (byte)(14),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*10*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),(byte)(SI+16),  (byte)(9),
        (byte)(0),    (byte)(9),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),   (byte)(14),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*11*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),(byte)(SI+17),   (byte)(11),
        (byte)(0),    (byte)(0),   (byte)(15),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*12*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),(byte)(SI+17),   (byte)(11),
        (byte)(0),   (byte)(12),   (byte)(15),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*13*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0), (byte)(SI+7),
        (byte)(13),(byte)(SI+3),    (byte)(0),
        /*14*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),(byte)(SI+16),  (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*15*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),(byte)(SI+17),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*16*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(10),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0),
        /*17*/
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+6),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(12),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+3),    (byte)(0)
    };

    private static final WordBreakTable kLineBackward
        = new WordBreakTable(COL_COUNT, kLineBackwardData);

	private static final int kRawMapping[] =
	{
		nonBlank, //UNASSIGNED		= 0,
		nonBlank, //UPPERCASE_LETTER	= 1,
		nonBlank, //LOWERCASE_LETTER	= 2,
		nonBlank, //TITLECASE_LETTER	= 3,
		nonBlank, //MODIFIER_LETTER		= 4,
		nonBlank, //OTHER_LETTER		= 5,
		nonBlank, //NON_SPACING_MARK	= 6,
		nonBlank, //ENCLOSING_MARK		= 7,
		nonBlank, //COMBINING_SPACING_MARK	= 8,
		nonBlank, //DECIMAL_DIGIT_NUMBER	= 9,
		nonBlank, //LETTER_NUMBER		= 10,
		nonBlank, //OTHER_NUMBER		= 11,
		blank, //SPACE_SEPARATOR		= 12,
		blank, //LINE_SEPARATOR		= 13,
		blank, //PARAGRAPH_SEPARATOR	= 14,		???????????
		blank, //CONTROL			= 15,
		nonBlank, //PRIVATE_USE		= 16,
		nonBlank, //FORMAT		= 17
		nonBlank, //????		= 18,
		nonBlank, //SURROGATE		= 19,
		op, //DASH_PUNCTUATION	= 20,
		preJwrd, //START_PUNCTUATION	= 21,
		postJwrd, //END_PUNCTUATION		= 22,
		nonBlank, //CONNECTOR_PUNCTUATION	= 23,
		nonBlank, //OTHER_PUNCTUATION	= 24,
		nonBlank, //MATH_SYMBOL		= 25,
		nonBlank, //CURRENCY_SYMBOL		= 26,
		nonBlank, //MODIFIER_SYMBOL		= 27,
		nonBlank  //OTHER_SYMBOL		= 28;
	};
/*
    private static final int kRawMapping[] =
    {
        nonBlank, //00   Invalid
        blank,    //01   SpaceWhitespace
        blank,    //02   ZerowidthSpaceWhitespace
        nonBlank, //03   ISOcontrol
        blank,    //04   WhitespaceISOcontrol
        nonBlank, //05   Dash
        nonBlank, //06   Punctuation
        op,       //07   DashPunctuation
        nonBlank, //08   HyphenPunctuation
        op,       //09   DashHyphenPunctuation
        nonBlank, //10   PunctuationQuotationmark
        nonBlank, //11   PunctuationTerminalpunctuation
        nonBlank, //12   Currencysymbol
        postJwrd, //13   PunctuationPairedpunctuation
        postJwrd, //14   PunctuationQuotationmarkPairedpunctuation
        preJwrd,  //15   PunctuationPairedpunctuationLeftofpair
        preJwrd,  //16   PunctuationQuotationmarkPairedpunctuationLeftofpair
        nonBlank, //17   PunctuationPairedpunctuationCombining
        nonBlank, //18   PunctuationPairedpunctuationLeftofpairCombining
        nonBlank, //19   Composite
        nonBlank, //20   Numeric
        nonBlank, //21   CompositeNumeric
        nonBlank, //22   PunctuationAlphabetic
        nonBlank, //23   Diacritic
        nonBlank, //24   CompositeDiacritic
        nonBlank, //25   PunctuationIdentifierpart
        nonBlank, //26   DecimaldigitNumericIdentifierpart
        nonBlank, //27   HexdigitDecimaldigitNumericIdentifierpart
        nonBlank, //28   AlphabeticIdentifierpart
        nonBlank, //29   CombiningAlphabeticIdentifierpart
        nonBlank, //30   CompositeAlphabeticIdentifierpart
        nonBlank, //31   CombiningCompositeAlphabeticIdentifierpart
        nonBlank, //32   NumericAlphabeticIdentifierpart
        nonBlank, //33   CompositeNumericAlphabeticIdentifierpart
        nonBlank, //34   IdeographicIdentifierpart
        nonBlank, //35   NumericIdeographicIdentifierpart
        nonBlank, //36   CombiningDiacriticIdentifierpart
        nonBlank, //37   ExtenderIdentifierpart
        nonBlank, //38   CompositeExtenderIdentifierpart
        nonBlank, //39   DiacriticExtenderIdentifierpart
        nonBlank, //40   PunctuationDiacriticExtenderIdentifierpart
        blank,    //41   ZerowidthWhitespaceBidicontrolIgnorablecontrol
        blank,    //42   ZerowidthWhitespaceJoincontrolIgnorablecontrol
        blank,    //43   ZerowidthWhitespaceFormatcontrolIgnorablecontrol
        nonBlank, //44   AlphabeticIdentifierpartLower
        nonBlank, //45   CompositeAlphabeticIdentifierpartLower
        nonBlank, //46   HexdigitAlphabeticIdentifierpartLower
        nonBlank, //47   AlphabeticIdentifierpartUpper
        nonBlank, //48   CompositeAlphabeticIdentifierpartUpper
        nonBlank, //49   HexdigitAlphabeticIdentifierpartUpper
        nonBlank, //50   CompositeAlphabeticIdentifierpartTitle
        nonBlank, //51   Marknonspacing
        nonBlank, //52   CombiningMarknonspacing
        nonBlank, //53   CombiningIdentifierpartMarknonspacing
        nonBlank, //54   AlphabeticIdentifierpartMarknonspacing
        nonBlank, //55   CombiningAlphabeticIdentifierpartMarknonspacing
        nonBlank, //56   CompositeAlphabeticIdentifierpartMarknonspacing
        nonBlank, //57 CombiningCompositeAlphabeticIdentifierpartMarknonspacing
        nonBlank, //58   CombiningDiacriticIdentifierpartMarknonspacing
        nonBlank, //59  CombiningCompositeDiacriticIdentifierpartMarknonspacing
        blank,    //60   WhitespaceNongraphicSeparator
        nonBlank, //61   WhitespaceISOcontrolNongraphicSeparator
        nonBlank, //62   SpaceWhitespaceNongraphicNobreak
        nonBlank, //63 ZerowidthSpaceWhitespaceIgnorablecontrolNongraphicNobreak
    }; */

    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order as
        //      required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_END_OF_TEXT, BREAK),
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION,
                           ASCII_CARRIAGE_RETURN, BREAK),
        new SpecialMapping(ASCII_EXCLAMATION_MARK, postJwrd),
        new SpecialMapping(ASCII_PERCENT, postJwrd),
        new SpecialMapping(ASCII_COMMA, postJwrd),
        new SpecialMapping(ASCII_FULL_STOP, postJwrd),
        new SpecialMapping(ASCII_COLON, ASCII_SEMICOLON, postJwrd),
        new SpecialMapping(ASCII_QUESTION_MARK, postJwrd),
        new SpecialMapping(LATIN1_SOFTHYPHEN, nonBlank),
        new SpecialMapping(ARABIC_PERCENT_SIGN, postJwrd),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR,
                           PUNCTUATION_PARAGRAPH_SEPARATOR, BREAK),
        new SpecialMapping(PUNCTUATION_IDEOGRAPHIC_FULL_STOP, postJwrd),
        new SpecialMapping(PER_MILLE_SIGN, postJwrd),
        new SpecialMapping(PER_TEN_THOUSAND_SIGN, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, HIRAGANA_LETTER_VU, hira),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, diacrit),
        new SpecialMapping(KATAKANA_LETTER_SMALL_A,
                           KATAKANA_LETTER_SMALL_KE, kata),
        new SpecialMapping(UNICODE_LOW_BOUND_HAN,UNICODE_HIGH_BOUND_HAN,kanji),
        new SpecialMapping(CJK_COMPATIBILITY_F900,
                           CJK_COMPATIBILITY_FA2D, kanji),
    };

    private static final UnicodeClassMapping kLineMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar);

    public WordBreakTable forward()
    {
        return kLineForward;
    }

    public WordBreakTable backward()
    {
        return kLineBackward;
    }

    public UnicodeClassMapping map()
    {
        return kLineMap;
    }
}
