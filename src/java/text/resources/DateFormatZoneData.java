 /*
 * @(#)DateFormatZoneData.java	1.6 97/10/28
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
 * @version      1.6 10/28/97
 * @author       Chen-Lieh Huang
 */
//  US DateFormatZoneData
//
public final class DateFormatZoneData extends ListResourceBundle
{
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return contents;
    }

    private static final String kZoneStrings[][] = {
        {"PST", "Pacific Standard Time", "PST",
        "Pacific Daylight Time", "PDT", "San Francisco"},
        {"MST", "Mountain Standard Time", "MST",
        "Mountain Daylight Time", "MDT", "Denver"},
        {"PNT", "Mountain Standard Time", "MST",
        "Mountain Standard Time", "MST", "Phoenix"},
        {"CST", "Central Standard Time", "CST",
        "Central Daylight Time", "CDT", "Chicago"},
        {"EST", "Eastern Standard Time", "EST",
        "Eastern Daylight Time", "EDT", "New York"},
        // IET is the ID for Indiana Eastern Standard Time timezone.
        {"IET", "Eastern Standard Time", "EST",
        "Eastern Standard Time", "EST", "Indianapolis"},
        // PRT is the ID for Puerto Rico and US Virgin Islands Time timezone.
        {"PRT", "Atlantic Standard Time", "AST",
        "Atlantic Daylight Time", "ADT", "Halifax"},
        {"HST", "Hawaii Standard Time", "HST",
        "Hawaii Daylight Time", "HDT", "Honolulu"},
        {"AST", "Alaska Standard Time", "AST",
        "Alaska Daylight Time", "ADT", "Anchorage"}
    };

    private static final String kLocalPatternChars = "GyMdkHmsSEDFwWahKz";

    static final Object[][] contents = {
        {"zoneStrings",         kZoneStrings},
        {"localPatternChars",   kLocalPatternChars},
    };
}
