/*
 * @(#)DateFormatZoneData.java	1.15 00/07/06
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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

package java.text.resources;

import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Supplement package private date-time formatting zone data for DateFormat.
 * DateFormatData used in DateFormat will be initialized by loading the data
 * from LocaleElements and DateFormatZoneData resources. The zone data are
 * represented with the following form:
 * {ID, new String[] {ID, long zone string, short zone string, long daylight 
 * string, short daylight string, representative city of zone}}, where ID is
 * NOT localized, but is used to look up the localized timezone data
 * internally. Localizers can localize any zone strings except
 * for the ID of the timezone.
 * Also, localizer should not touch "localPatternChars" entry.
 *
 * The <CODE>DateFormatZoneData</CODE> bundles of all locales must extend
 * this base class. This class implements similar to 
 * <CODE>ListResourceBundle</CODE>, except that it preserves the order of 
 * the keys that it obtained from <CODE>getContents</CODE> and return through
 * <CODE>getKeys</CODE>. As in <CODE>ListResourceBundle</CODE>,
 * <CODE>getKeys</CODE> appends keys from the parent bundle's
 * <CODE>getKeys</CODE> to this bundle's keys unless they duplicate
 * one of this bundle's keys.
 *
 * @see          ListResourceBundle
 * @see          Format
 * @see          DateFormatData
 * @see          LocaleElements
 * @see          SimpleDateFormat
 * @see          TimeZone
 * @version      1.15 07/06/00
 * @author       Chen-Lieh Huang
 */
//  ROOT DateFormatZoneData
//
public class DateFormatZoneData extends ResourceBundle
{

    /**
     * Override ResourceBundle. Same semantics.
     */
    public Object handleGetObject(String key) {
	if (lookup == null || keys == null) {
	    loadLookup();
	}
	return lookup.get(key);
    }

    /**
     * Implmentation of ResourceBundle.getKeys. Unlike the implementation in
     * <CODE>ListResourceBundle</CODE>, this implementation preserves 
     * the order of keys obtained from <CODE>getContents</CODE>.
     */
    public Enumeration getKeys() {
	if (lookup == null || keys == null) {
	    loadLookup();
	}
	Enumeration result = null;
	if (parent != null) {
	    final Enumeration myKeys = keys.elements();
	    final Enumeration parentKeys = parent.getKeys();
	    
            result = new Enumeration() {
                public boolean hasMoreElements() {
                    if (temp == null) {
                        nextElement();
		    }
                    return (temp != null);
                }

                public Object nextElement() {
                    Object returnVal = temp;
                    if (myKeys.hasMoreElements()) {
                        temp = myKeys.nextElement();
                    } else {
                        temp = null;
                        while ((temp == null) && (parentKeys.hasMoreElements())) {
                            temp = parentKeys.nextElement();
                            if (lookup.containsKey(temp))
                                temp = null;
                        }
                    }
                    return returnVal;
                }
                Object temp = null;
            };
        } else {
            result = keys.elements();
        }
        return result;
    }

    /**
     * Create hashtable and key vector while loading resources.
     */
    private synchronized void loadLookup() {
	Object[][] contents = getContents();
	Hashtable temp = new Hashtable(contents.length);
	Vector tempVec = new Vector(contents.length);
        for (int i = 0; i < contents.length; ++i) {
            temp.put(contents[i][0],contents[i][1]);
	    tempVec.add(contents[i][0]);
        }
        lookup = temp;
	keys = tempVec;
    }

    Hashtable lookup = null;
    Vector keys = null;

    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
	    {"AST", new String[] {"AST" /*--America/Anchorage--*/, "Alaska Standard Time", "AKST",
				  "Alaska Daylight Time", "AKDT" /*Anchorage*/}},
	    {"Africa/Cairo", new String[] {"Africa/Cairo", "Eastern European Time", "EET",
					   "Eastern European Summer Time", "EEST" /*Cairo*/}},
	    {"Africa/Casablanca", new String[] {"Africa/Casablanca", "Greenwich Mean Time", "GMT",
						"Greenwich Mean Time", "GMT" /*Reykjavik*/}},
	    {"Africa/Tripoli", new String[] {"Africa/Tripoli", "Eastern European Time", "EET",
					     "Eastern European Time", "EET" /*Tripoli*/}},
	    {"America/Anchorage", new String[] {"America/Anchorage", "Alaska Standard Time", "AKST",
						"Alaska Daylight Time", "AKDT" /*Anchorage*/}},
	    {"America/Chicago", new String[] {"America/Chicago", "Central Standard Time", "CST",
					      "Central Daylight Time", "CDT" /*Chicago*/}},
	    {"America/Denver", new String[] {"America/Denver", "Mountain Standard Time", "MST",
					     "Mountain Daylight Time", "MDT" /*Denver*/}},
	    {"America/Halifax", new String[] {"America/Halifax", "Atlantic Standard Time", "AST",
					      "Atlantic Daylight Time", "ADT" /*Halifax*/}},
	    {"America/Indianapolis", new String[] {"America/Indianapolis", "Eastern Standard Time", "EST",
						   "Eastern Standard Time", "EST" /*Indianapolis*/}},
	    {"America/Los_Angeles", new String[] {"America/Los_Angeles", "Pacific Standard Time", "PST",
						  "Pacific Daylight Time", "PDT" /*San Francisco*/}},
	    {"America/Manaus", new String[] {"America/Manaus", "Amazon Standard Time", "AMT",
					     "Amazon Daylight Time", "AMT" /*Manaus*/}},
	    {"America/Montreal", new String[] {"America/Montreal", "Eastern Standard Time", "EST",
					       "Eastern Standard Time", "EST" /*Montreal*/}},
	    {"America/New_York", new String[] {"America/New_York", "Eastern Standard Time", "EST",
					       "Eastern Daylight Time", "EDT" /*New York*/}},
	    {"America/Phoenix", new String[] {"America/Phoenix", "Mountain Standard Time", "MST",
					      "Mountain Standard Time", "MST" /*Phoenix*/}},
	    {"America/St_Johns", new String[] {"America/St_Johns", "Newfoundland Standard Time", "NST",
					       "Newfoundland Daylight Time", "NDT" /*St. John's*/}},
	    {"Asia/Jerusalem", new String[] {"Asia/Jerusalem", "Israel Standard Time", "IST",
					     "Israel Daylight Time", "IDT" /*Tel Aviv*/}},
	    {"Asia/Seoul", new String[] {"Asia/Seoul", "Korean Standard Time", "KST",
					 "Korean Standard Time", "KST"}},
	    {"Asia/Shanghai", new String[] {"Asia/Shanghai", "China Standard Time", "CST",
					    "China Standard Time", "CDT" /*Peking*/}},
	    {"Asia/Tehran", new String[] {"Asia/Tehran", "Iran Standard Time", "IRT",
					  "Iran Summer Time", "IRST" /*Tehran*/}},
	    {"Asia/Tokyo", new String[] {"Asia/Tokyo", "Japan Standard Time", "JST",
					 "Japan Standard Time", "JST" /*Tokyo*/}},
	    {"Australia/Adelaide", new String[] {"Australia/Adelaide", "Central Standard Time (South Australia)",
						 "CST", "Central Summer Time (South Australia)", "CST"}},
	    {"Australia/Brisbane", new String[] {"Australia/Brisbane", "Eastern Standard Time (Queensland)", "EST",
						 "Eastern Standard Time (Queensland)", "EST"}},
	    {"Australia/Broken_Hill", new String[] {"Australia/Broken_Hill", "Central Standard Time (South Australia/New South Wales)", "CST",
						    "Central Summer Time (South Australia/New South Wales)", "CST"}},
	    {"Australia/Darwin", new String[] {"Australia/Darwin", "Central Standard Time (Northern Territory)", "CST",
					       "Central Standard Time (Northern Territory)", "CST"}},
	    {"Australia/Hobart", new String[] {"Australia/Hobart", "Eastern Standard Time (Tasmania)", "EST",
					       "Eastern Summer Time (Tasmania)", "EST"}},
	    {"Australia/Sydney", new String[] {"Australia/Sydney", "Eastern Standard Time (New South Wales)", "EST",
					       "Eastern Summer Time (New South Wales)", "EST"}},
	    {"CNT", new String[] {"CNT" /*--America/St_Johns--*/, "Newfoundland Standard Time", "NST",
				  "Newfoundland Daylight Time", "NDT" /*St. John's*/}},
	    {"CST", new String[] {"CST" /*--America/Chicago--*/, "Central Standard Time", "CST",
				  "Central Daylight Time", "CDT" /*Chicago*/}},
	    {"CTT", new String[] {"CTT", "China Standard Time", "CST",
				  "China Standard Time", "CDT" /*Peking*/}},
	    {"ECT", new String[] {"ECT" /*--Europe/Paris--*/, "Central European Standard Time", "CET",
				  "Central European Daylight Time", "CEST" /*Paris*/}},
	    {"EET", new String[] {"EET", "Eastern European Time", "EET",
				  "Eastern European Summer Time", "EEST" /*Helsinki*/}},
	    {"EST", new String[] {"EST" /*--America/New_York--*/, "Eastern Standard Time", "EST",
				  "Eastern Daylight Time", "EDT" /*New York*/}},
	    {"Europe/Berlin", new String[] {"Europe/Berlin", "Central European Time", "CET",
					    "Central European Summer Time", "CEST" /*Berlin*/}},
	    {"Europe/Bucharest", new String[] {"Europe/Bucharest", "Eastern European Time", "EET",
					       "Eastern European Summer Time", "EEST" /*Bucharest*/}},
	    {"Europe/Helsinki", new String[] {"Europe/Helsinki", "Eastern European Time", "EET",
					      "Eastern European Summer Time", "EEST" /*Helsinki*/}},
	    {"Europe/Istanbul", new String[] {"Europe/Istanbul", "Eastern European Time", "EET",
					      "Eastern European Summer Time", "EEST"}},
	    {"Europe/Lisbon", new String[] {"Europe/Lisbon", "West European Time", "WET",
					    "West European Summer Time", "WEST" /*Lisbon*/}},
	    {"Europe/London", new String[] {"Europe/London", "Greenwich Mean Time", "GMT",
					    "British Summer Time", "BST" /*London*/}},
	    {"Europe/Moscow", new String[] {"Europe/Moscow", "Moscow Standard Time", "MSK",
					    "Moscow Daylight Time", "MSD" /*Moscow*/}},
	    {"Europe/Paris", new String[] {"Europe/Paris", "Central European Time", "CET",
					   "Central European Summer Time", "CEST" /*Paris*/}},
	    {"GMT", new String[] {"GMT", "Greenwich Mean Time", "GMT",
				  "Greenwich Mean Time", "GMT" /*Reykjavik*/}},
	    {"HST", new String[] {"HST" /*--Pacific/Honolulu--*/, "Hawaii Standard Time", "HST",
				  "Hawaii Standard Time", "HST" /*Honolulu*/}},
	    {"IET", new String[] {"IET" /*--America/Indianapolis--*/, "Eastern Standard Time", "EST",
				  "Eastern Standard Time", "EST" /*Indianapolis*/}},
	    {"IST", new String[] {"IST", "Israel Standard Time", "IST",
				  "Israel Daylight Time", "IDT" /*Tel Aviv*/}},
	    {"JST", new String[] {"JST" /*--Asia/Tokyo--*/, "Japan Standard Time", "JST",
				  "Japan Standard Time", "JST" /*Tokyo*/}},
	    {"MST", new String[] {"MST" /*--America/Denver--*/, "Mountain Standard Time", "MST",
				  "Mountain Daylight Time", "MDT" /*Denver*/}},
	    {"PNT", new String[] {"PNT" /*--America/Phoenix--*/, "Mountain Standard Time", "MST",
				  "Mountain Standard Time", "MST" /*Phoenix*/}},
	    {"PST", new String[] {"PST" /*--America/Los_Angeles--*/, "Pacific Standard Time", "PST",
				  "Pacific Daylight Time", "PDT" /*San Francisco*/}},
	    {"Pacific/Honolulu", new String[] {"Pacific/Honolulu", "Hawaii Standard Time", "HST",
					       "Hawaii Standard Time", "HST" /*Honolulu*/}},
	    {"WET", new String[] {"WET", "Western European Time", "WET",
				  "Western European Summer Time", "WEST"}},

            {"localPatternChars", "GyMdkHmsSEDFwWahKz"},
        };
    }
}
