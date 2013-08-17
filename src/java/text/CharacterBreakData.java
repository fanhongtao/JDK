/*
 * @(#)CharacterBreakData.java	1.5 97/01/17
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
 * The CharacterBreakData contains data used by SimpleTextBoundary
 * to determine character breaks.
 * @see #BreakIterator
 */
final class CharacterBreakData extends TextBoundaryData
{
    private static final byte accent_diacritic = 0;
    private static final byte baseForm = 1;
    private static final int COL_COUNT = 2;
    private static final byte SI = (byte)0x80;
    private static final byte kCharacterForwardData[] =
    {
        //    acct            base
        (byte)(     0), (byte)(     0),
        (byte)(0x80+2), (byte)(0x80+2),
        (byte)(0x80+2), (byte)(0x80+0)
    };
    private static final WordBreakTable kCharacterForwardTable =
    new WordBreakTable(COL_COUNT, kCharacterForwardData);
    private static final byte kCharacterBackwardData[] =
    {
        (byte)(     0), (byte)(     0),
        (byte)(0x80+1), (byte)(0x80+0)
    };
    private static final WordBreakTable kCharacterBackwardTable =
    new WordBreakTable(COL_COUNT, kCharacterBackwardData);
    private static final int kRawMapping[] =
    {
		baseForm, //UNASSIGNED		= 0,
		baseForm, //UPPERCASE_LETTER	= 1,
		baseForm, //LOWERCASE_LETTER	= 2,
		baseForm, //TITLECASE_LETTER	= 3,
		baseForm, //MODIFIER_LETTER		= 4,
		baseForm, //OTHER_LETTER		= 5,
		accent_diacritic, //NON_SPACING_MARK	= 6,
		accent_diacritic, //ENCLOSING_MARK		= 7,
		baseForm, //COMBINING_SPACING_MARK	= 8,
		baseForm, //DECIMAL_DIGIT_NUMBER	= 9,
		baseForm, //LETTER_NUMBER		= 10,
		baseForm, //OTHER_NUMBER		= 11,
		baseForm, //SPACE_SEPARATOR		= 12,
		baseForm, //LINE_SEPARATOR		= 13,
		baseForm, //PARAGRAPH_SEPARATOR	= 14,
		baseForm, //CONTROL			= 15,
		baseForm, //FORMAT		= 16,
		baseForm, //????			= 17,
		baseForm, //PRIVATE_USE		= 18,
		baseForm, //SURROGATE		= 19,
		baseForm, //DASH_PUNCTUATION	= 20,
		baseForm, //START_PUNCTUATION	= 21,
		baseForm, //END_PUNCTUATION		= 22,
		baseForm, //CONNECTOR_PUNCTUATION	= 23,
		baseForm, //OTHER_PUNCTUATION	= 24,
		baseForm, //MATH_SYMBOL		= 25,
		baseForm, //CURRENCY_SYMBOL		= 26,
		baseForm, //MODIFIER_SYMBOL		= 27,
		baseForm, //OTHER_SYMBOL		= 28;
    };
    /*
    private static final int kRawMapping[] =
    {
        baseForm, //00   Invalid
        baseForm, //01   SpaceWhitespace
        baseForm, //02   ZerowidthSpaceWhitespace
        baseForm, //03   ISOcontrol
        baseForm, //04   WhitespaceISOcontrol
        baseForm, //05   Dash
        baseForm, //06   Punctuation
        baseForm, //07   DashPunctuation
        baseForm, //08   HyphenPunctuation
        baseForm, //09   DashHyphenPunctuation
        baseForm, //10   PunctuationQuotationmark
        baseForm, //11   PunctuationTerminalpunctuation
        baseForm, //12   Currencysymbol
        baseForm, //13   PunctuationPairedpunctuation
        baseForm, //14   PunctuationQuotationmarkPairedpunctuation
        baseForm, //15   PunctuationPairedpunctuationLeftofpair
        baseForm, //16   PunctuationQuotationmarkPairedpunctuationLeftofpair
        baseForm, //17   PunctuationPairedpunctuationCombining
        baseForm, //18   PunctuationPairedpunctuationLeftofpairCombining
        baseForm, //19   Composite
        baseForm, //20   Numeric
        baseForm, //21   CompositeNumeric
        baseForm, //22   PunctuationAlphabetic
        baseForm, //23   Diacritic
        baseForm, //24   CompositeDiacritic
        baseForm, //25   PunctuationIdentifierpart
        baseForm, //26   DecimaldigitNumericIdentifierpart
        baseForm, //27   HexdigitDecimaldigitNumericIdentifierpart
        baseForm, //28   AlphabeticIdentifierpart
        baseForm, //29   CombiningAlphabeticIdentifierpart
        baseForm, //30   CompositeAlphabeticIdentifierpart
        baseForm, //31   CombiningCompositeAlphabeticIdentifierpart
        baseForm, //32   NumericAlphabeticIdentifierpart
        baseForm, //33   CompositeNumericAlphabeticIdentifierpart
        baseForm, //34   IdeographicIdentifierpart
        baseForm, //35   NumericIdeographicIdentifierpart
        baseForm, //36   CombiningDiacriticIdentifierpart
        baseForm, //37   ExtenderIdentifierpart
        baseForm, //38   CompositeExtenderIdentifierpart
        baseForm, //39   DiacriticExtenderIdentifierpart
        baseForm, //40   PunctuationDiacriticExtenderIdentifierpart
        baseForm, //41   ZerowidthWhitespaceBidicontrolIgnorablecontrol
        baseForm, //42   ZerowidthWhitespaceJoincontrolIgnorablecontrol
        baseForm, //43   ZerowidthWhitespaceFormatcontrolIgnorablecontrol
        baseForm, //44   AlphabeticIdentifierpartLower
        baseForm, //45   CompositeAlphabeticIdentifierpartLower
        baseForm, //46   HexdigitAlphabeticIdentifierpartLower
        baseForm, //47   AlphabeticIdentifierpartUpper
        baseForm, //48   CompositeAlphabeticIdentifierpartUpper
        baseForm, //49   HexdigitAlphabeticIdentifierpartUpper
        baseForm, //50   CompositeAlphabeticIdentifierpartTitle
        accent_diacritic, //51   Marknonspacing
        accent_diacritic, //52   CombiningMarknonspacing
        accent_diacritic, //53   CombiningIdentifierpartMarknonspacing
        accent_diacritic, //54   AlphabeticIdentifierpartMarknonspacing
        accent_diacritic, //55 CombiningAlphabeticIdentifierpartMarknonspacing
        accent_diacritic, //56 CompositeAlphabeticIdentifierpartMarknonspacing
        accent_diacritic, //57 CombiningCompositeAlphabeticIdentifierpartMarknonspacing
        accent_diacritic, //58 CombiningDiacriticIdentifierpartMarknonspacing
        accent_diacritic, //59 CombiningCompositeDiacriticIdentifierpartMarknonspacing
        baseForm, //60 WhitespaceNongraphicSeparator
        baseForm, //61 WhitespaceISOcontrolNongraphicSeparator
        baseForm, //62 SpaceWhitespaceNongraphicNobreak
        baseForm, //63 ZerowidthSpaceWhitespaceIgnorablecontrolNongraphicNobreak
    };*/

    private static final SpecialMapping kExceptionChar[] = {};
    private static final UnicodeClassMapping kCharacterMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar);

    public WordBreakTable forward()
    {
        return kCharacterForwardTable;
    }

    public WordBreakTable backward()
    {
        return kCharacterBackwardTable;
    }

    public UnicodeClassMapping map()
    {
        return kCharacterMap;
    }
}

