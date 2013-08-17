 /*
 * @(#)DateFormatZoneData_sq.java	1.5 97/12/05
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

package java.text.resources;

import java.util.ListResourceBundle;

/**
 * Supplement package private date-time formatting zone data for DateFormat.
 * DateFormatData used in DateFormat will be initialized by loading the data
 * from LocaleElements and DateFormatZoneData resources. The zone data are
 * represented with the following form:
 * {ID, long zone string, short zone string, long daylight string,
 * short daylight string, representative city of zone}, where ID is
 * NOT localized, but is used to look up the localized timezone data
 * internally. Localizers can localize any zone strings except
 * for the ID of the timezone.

 * @see          ListResourceBundle
 * @see          Format
 * @see          DateFormatData
 * @see          LocaleElements
 * @see          SimpleDateFormat
 * @see          TimeZone
 * @version      1.5 12/05/97
 * @author       Chen-Lieh Huang
 */
//  Albania DateFormatZoneData
//
public final class DateFormatZoneData_sq extends ListResourceBundle
{
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return contents;
    }

    private static final String kZoneStrings[][] = {
        // The first row below is the default TimeZone for this locale.
        // Zones should have unique names and abbreviations within
        // this locale.  Names and abbreviations may be identical
        // if the corresponding zones really are identical.  E.g.:
        // America/Phoenix and America/Denver both use MST; these
        // zones differ only in that America/Denver uses MDT as well.
        {"Africa/Casablanca", "GMT", "GMT", "GMT", "GMT"} // To be filled in with localized time zone names
    };

    private static final String kLocalPatternChars = "GanjkHmsSEDFwWxhKz";

    static final Object[][] contents = {
        {"zoneStrings",         kZoneStrings},
        {"localPatternChars",   kLocalPatternChars},
    };
}
