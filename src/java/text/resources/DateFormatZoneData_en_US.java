/*
 * @(#)DateFormatZoneData_en_US.java	1.3 01/01/11 
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * Portions copyright (c) 1997, 1998 Sun Microsystems, Inc.
 * All Rights Reserved.
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
 * @version      1.4 01/27/97
 * @author       Chen-Lieh Huang
 * @author       Alan Liu
 */
//  US DateFormatZoneData
//
public final class DateFormatZoneData_en_US extends ListResourceBundle
{
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            {"zoneStrings",
                new String[][] {
                    // Zones should have unique names and abbreviations within this locale.
                    // Names and abbreviations may be identical if the corresponding zones
                    // really are identical.  E.g.: America/Phoenix and America/Denver both
                    // use MST; these zones differ only in that America/Denver uses MDT as
                    // well.
                    //
                    // We list both short and long IDs.  Short IDs come first so that they
                    // are chosen preferentially during parsing of zone names.
                    //
                    // ar
                    {"Africa/Cairo", "Eastern European Time", "EET",
                     "Eastern European Summer Time", "EEST" /*Cairo*/},
                    // be
                    {"Europe/Moscow", "Moscow Standard Time", "MSK",
                     "Moscow Daylight Time", "MSD" /*Moscow*/},
                    //{"Europe/Minsk", "Eastern European Standard Time", "EET",
                    // "Eastern European Daylight Time", "EEST" /*Minsk*/}
                    // bg
                    //{"Europe/Bucharest", "Eastern European Standard Time", "EET",
                    // "Eastern European Daylight Time", "EEST" /*Sofia*/}
                    // ca
                    //{"Europe/Paris", "Central European Standard Time", "CET",
                    // "Central European Daylight Time", "CEST" /*Madrid*/}
                    // cs
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Prague*/},
                    // da
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Copenhagen*/},
                    // de
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Berlin*/},
                    // de_AT
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Vienna*/},
                    // de_CH
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Zurich*/},
                    // el
                    //{"Europe/Istanbul", "Eastern European Standard Time", "EET",
                    // "Eastern European Daylight Time", "EEST" /*Athens*/},
                    // en
                    {"PST" /*--America/Los_Angeles--*/, "Pacific Standard Time", "PST",
                     "Pacific Daylight Time", "PDT" /*San Francisco*/},
                    {"America/Los_Angeles", "Pacific Standard Time", "PST",
                     "Pacific Daylight Time", "PDT" /*San Francisco*/},
                    {"MST" /*--America/Denver--*/, "Mountain Standard Time", "MST",
                     "Mountain Daylight Time", "MDT" /*Denver*/},
                    {"America/Denver", "Mountain Standard Time", "MST",
                     "Mountain Daylight Time", "MDT" /*Denver*/},
                    {"PNT" /*--America/Phoenix--*/, "Mountain Standard Time", "MST",
                     "Mountain Standard Time", "MST" /*Phoenix*/},
                    {"America/Phoenix", "Mountain Standard Time", "MST",
                     "Mountain Standard Time", "MST" /*Phoenix*/},
                    {"CST" /*--America/Chicago--*/, "Central Standard Time", "CST",
                     "Central Daylight Time", "CDT" /*Chicago*/},
                    {"America/Chicago", "Central Standard Time", "CST",
                     "Central Daylight Time", "CDT" /*Chicago*/},
                    {"EST" /*--America/New_York--*/, "Eastern Standard Time", "EST",
                     "Eastern Daylight Time", "EDT" /*New York*/},
                    {"America/New_York", "Eastern Standard Time", "EST",
                     "Eastern Daylight Time", "EDT" /*New York*/},
                    {"America/Montreal", "Eastern Standard Time", "EST",
                     "Eastern Standard Time", "EST" /*Montreal*/},
                    {"IET" /*--America/Indianapolis--*/, "Eastern Standard Time", "EST",
                     "Eastern Standard Time", "EST" /*Indianapolis*/},
                    {"America/Indianapolis", "Eastern Standard Time", "EST",
                     "Eastern Standard Time", "EST" /*Indianapolis*/},
                    {"HST" /*--Pacific/Honolulu--*/, "Hawaii Standard Time", "HST",
                     "Hawaii Standard Time", "HST" /*Honolulu*/},
                    {"Pacific/Honolulu", "Hawaii Standard Time", "HST",
                     "Hawaii Standard Time", "HST" /*Honolulu*/},
                    {"AST" /*--America/Anchorage--*/, "Alaska Standard Time", "AKST",
                     "Alaska Daylight Time", "AKDT" /*Anchorage*/},
                    {"America/Anchorage", "Alaska Standard Time", "AKST",
                     "Alaska Daylight Time", "AKDT" /*Anchorage*/},
                    //{"AMT", "Amazon Standard Time", "AMT",
                    // "Amazon Daylight Time", "AMT" /*Manaus*/},
                    {"America/Manaus", "Amazon Standard Time", "AMT",
                     "Amazon Daylight Time", "AMT" /*Manaus*/},
                    {"America/Halifax", "Atlantic Standard Time", "AST",
                     "Atlantic Daylight Time", "ADT" /*Halifax*/},
                    {"CNT" /*--America/St_Johns--*/, "Newfoundland Standard Time",
                     "NST", "Newfoundland Daylight Time", "NDT" /*St. John's*/},
                    {"America/St_Johns", "Newfoundland Standard Time",
                     "NST", "Newfoundland Daylight Time", "NDT" /*St. John's*/},
                    // es
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Madrid*/},
                    // et
                    //{"EET", "Eastern European Standard Time", "EEST",
                    // "Eastern European Daylight Time", "EEDT" /*Tallinn*/},
                    // fi
                    {"EET", "Eastern European Time", "EET",
                     "Eastern European Summer Time", "EEST" /*Helsinki*/},
                    {"Europe/Helsinki", "Eastern European Time", "EET",
                     "Eastern European Summer Time", "EEST" /*Helsinki*/},
                    // fr
                    {"ECT" /*--Europe/Paris--*/, "Central European Time",
                     "CET", "Central European Summer Time", "CEST" /*Paris*/},
                    {"Europe/Paris", "Central European Time", "CET",
                     "Central European Summer Time", "CEST" /*Paris*/},
                    {"Europe/Berlin", "Central European Time", "CET",
                     "Central European Summer Time", "CEST" /*Berlin*/},
                    // fr_BE
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Brussels*/},
                    // fr_CA
                    // fr_CH
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Geneva*/},
                    // hr
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Zagreb*/},
                    // hu
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Budapest*/},
                    // is
                    {"GMT", "Greenwich Mean Time", "GMT",
                     "Greenwich Mean Time", "GMT" /*Reykjavik*/},
                    {"Europe/London", "Greenwich Mean Time", "GMT",
                     "British Summer Time", "BST" /*London*/},
                    {"Africa/Casablanca", "Greenwich Mean Time", "GMT",
                     "Greenwich Mean Time", "GMT" /*Casablanca*/},
                    // it
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Rome*/},
                    // it_CH
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Zurich*/},
                    // iw
                    {"IST", "Israel Standard Time", "IST",
                     "Israel Daylight Time", "IDT" /*Tel Aviv*/},
                    {"Asia/Jerusalem", "Israel Standard Time", "IST",
                     "Israel Daylight Time", "IDT" /*Tel Aviv*/},
                    // ja
                    {"JST" /*--Asia/Tokyo--*/, "Japan Standard Time",
                     "JST", "Japan Standard Time", "JST" /*Tokyo*/},
                    {"Asia/Tokyo", "Japan Standard Time", "JST",
                     "Japan Standard Time", "JST" /*Tokyo*/},
                    {"Australia/Darwin", 
                     "Central Standard Time (Northern Territory)", "CST",
                     "Central Standard Time (Northern Territory)", "CST"},
                    {"Australia/Adelaide", 
                     "Central Standard Time (South Australia)", "CST",
                     "Central Standard Time (South Australia)", "CST"},
                    {"Australia/Broken_Hill", 
                     "Central Standard Time (South Australia/New South Wales)", "CST",
                     "Central Standard Time (South Australia/New South Wales)", "CST"},
                    {"Australia/Hobart", 
                     "Eastern Standard Time (Tasmania)", "EST",
                     "Eastern Standard Time (Tasmania)", "EST"},
                    // ko
                    {"Australia/Brisbane", 
                     "Eastern Standard Time (Queensland)", "EST",
                     "Eastern Standard Time (Queensland)", "EST"},
                    {"Australia/Sydney", 
                     "Eastern Standard Time (New South Wales)", "EST",
                     "Eastern Standard Time (New South Wales)", "EST"},
                    // ko
                    //{"JST" /*--Asia/Tokyo--*/, "Korea Standard Time",
                    // "KST", "Korea Standard Time", "KST" /*Seoul*/},
                    // lt
                    //{"EET", "Eastern European Standard Time", "EEST",
                    // "Eastern European Daylight Time", "EEDT" /*Vilnius*/},
                    // lv
                    //{"Europe/Riga", "Eastern European Standard Time",
                    // "EET", "Eastern European Daylight Time", "EEST" /*Riga*/},
                    // mk
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Skopje*/},
                    // nl
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Amsterdam*/},
                    // nl_BE
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Brussels*/},
                    // no
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Oslo*/},
                    // no_NO_NY
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Oslo*/},
                    // pl
                    //{"Europe/Warsaw", "Central European Standard Time",
                    // "CET", "Central European Daylight Time", "CEST" /*Warsaw*/},
                    // pt
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Lisbon*/},
                    // ro
                    {"Europe/Bucharest", "Eastern European Standard Time",
                     "EET", "Eastern European Daylight Time", "EEST" /*Bucharest*/},
                    // ru
                    //{"EET", "Eastern European Standard Time", "EEST",
                    // "Eastern European Daylight Time", "EEDT" /*Unknown*/},
                    // sh
                    //{"Europe/Paris", "Central European Standard Time",
                    // "CET", "Central European Daylight Time", "CEST" /*Paris*/}
                    // sk
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Paris*/},
                    // sl
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Ljubljana*/},
                    // sq
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Tirana*/},
                    // sr
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Paris*/},
                    // sv
                    //{"ECT", "Central European Standard Time", "CEST",
                    // "Central European Daylight Time", "CEDT" /*Stockholm*/},
                    // tr
                    //{"EET", "Eastern European Standard Time", "EEST",
                    // "Eastern European Daylight Time", "EEDT" /*Ankara*/},
                    // uk
                    //{"EET", "Eastern European Standard Time", "EEST",
                    // "Eastern European Daylight Time", "EEDT" /*Kiev*/},
                    // zh
                    {"CTT", "China Standard Time", "CST",
                     "China Standard Time", "CDT" /*Peking*/},
                    {"Asia/Shanghai", "China Standard Time", "CST",
                     "China Standard Time", "CST" /*Peking*/},
                    // zh_TW
                    //{"CTT", "Taiwan Standard Time", "TST",
                    // "Taiwan Standard Time", "TST" /*Taipei*/},
                }
            },
            {"localPatternChars", "GyMdkHmsSEDFwWahKz"},
        };
    }
}
