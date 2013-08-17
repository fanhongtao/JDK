/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * @version      1.21 02/06/02
 * @author       Chen-Lieh Huang
 */
//  ROOT DateFormatZoneData
//
public class DateFormatZoneData extends ResourceBundle
{
    private static final String	LOCALPATTERNCHARS = "localPatternChars";

    /**
     * Override ResourceBundle. Same semantics.
     */
    public Object handleGetObject(String key) {
	if (lookup == null) {
	    loadLookup();
	}
	if (LOCALPATTERNCHARS.equals(key)) {
	    return lookup.get(key);
	}

	String[] contents = (String [])lookup.get(key);
	if (contents == null) { 
	    return null;	
	}

	int	clen = contents.length;
	String[] tmpobj = new String[clen+1];
	tmpobj[0] = key;
	for (int i = 0; i < clen; i++) {
	    tmpobj[i+1] = contents[i];
	}
	return tmpobj;
    }

    /**
     * Implmentation of ResourceBundle.getKeys. Unlike the implementation in
     * <CODE>ListResourceBundle</CODE>, this implementation preserves 
     * the order of keys obtained from <CODE>getContents</CODE>.
     */
    public Enumeration getKeys() {
	if (lookup == null) {
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
	if (lookup != null) {
	    return;
	}
	Object[][] contents = getContents();
	Hashtable temp = new Hashtable(contents.length);
	Vector tempVec = new Vector(contents.length);
        for (int i = 0; i < contents.length; ++i) {
            temp.put(contents[i][0],contents[i][1]);
	    tempVec.add(contents[i][0]);
        }
	keys = tempVec;
        lookup = temp;
    }

    Hashtable lookup = null;
    Vector keys = null;

    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
	String AGT[] = new String[] {"Argentine Time", "ART",
				     "Argentine Summer Time", "ARST"};
	String AKST[] = new String[] {"Alaska Standard Time", "AKST",
				      "Alaska Daylight Time", "AKDT"};
	String AMT[] = new String[] {"Amazon Standard Time", "AMT",
				     "Amazon Summer Time", "AMST"};
	String ARAST[] = new String[] {"Arabia Standard Time", "AST",
				       "Arabia Daylight Time", "ADT"};
	String ARMT[] = new String[] {"Armenia Time", "AMT",
				      "Armenia Summer Time", "AMST"};
	String AST[] = new String[] {"Atlantic Standard Time", "AST",
				     "Atlantic Daylight Time", "ADT"};
	String BDT[] = new String[] {"Bangladesh Time", "BDT",
				     "Bangladesh Summer Time", "BDST"};
	String BRT[] = new String[] {"Brazil Time", "BRT",
				     "Brazil Summer Time", "BRST"};
	String BTT[] = new String[] {"Bhutan Time", "BTT",
				     "Bhutan Summer Time", "BTST"};
	String CAT[] = new String[] {"Central African Time", "CAT",
				     "Central African Summer Time", "CAST"};
	String CET[] = new String[] {"Central European Time", "CET",
				     "Central European Summer Time", "CEST"};
	String ChST[] = new String[] {"Chamorro Standard Time", "ChST",
				      "Chamorro Daylight Time", "ChDT"};
	String CLT[] = new String[] {"Chile Time", "CLT",
				     "Chile Summer Time", "CLST"};
	String CST[] = new String[] {"Central Standard Time", "CST",
				     "Central Daylight Time", "CDT"};
	String CTT[] = new String[] {"China Standard Time", "CST",
				     "China Daylight Time", "CDT"};
	String EAT[] = new String[] {"Eastern African Time", "EAT",
				     "Eastern African Summer Time", "EAST"};
	String EET[] = new String[] {"Eastern European Time", "EET",
				     "Eastern European Summer Time", "EEST"};
	String EGT[] = new String[] {"Eastern Greenland Time", "EGT",
				     "Eastern Greenland Summer Time", "EGST"};
	String EST[] = new String[] {"Eastern Standard Time", "EST",
				     "Eastern Daylight Time", "EDT"};
	String EST_NSW[] = new String[] {"Eastern Standard Time (New South Wales)", "EST",
					 "Eastern Summer Time (New South Wales)", "EST"};
	String GMT[] = new String[] {"Greenwich Mean Time", "GMT",
				   "Greenwich Mean Time", "GMT"};
	String GST[] = new String[] {"Gulf Standard Time", "GST",
				     "Gulf Daylight Time", "GDT"};
	String HAST[] = new String[] {"Hawaii-Aleutian Standard Time", "HAST",
				      "Hawaii-Aleutian Daylight Time", "HADT"};
	String HST[] = new String[] {"Hawaii Standard Time", "HST",
				     "Hawaii Daylight Time", "HDT"};
	String ICT[] = new String[] {"Indochina Time", "ICT",
				     "Indochina Summer Time", "ICST"};
	String IRT[] = new String[] {"Iran Time", "IRT",
				     "Iran Sumer Time", "IRST"};
	String IST[] = new String[] {"India Standard Time", "IST",
				     "India Daylight Time", "IDT"};
	String JST[] = new String[] {"Japan Standard Time", "JST",
				     "Japan Daylight Time", "JDT"};
	String KST[] = new String[] {"Korea Standard Time", "KST",
				     "Korea Daylight Time", "KDT"};
	String MST[] = new String[] {"Mountain Standard Time", "MST",
				     "Mountain Daylight Time", "MDT"};
	String NST[] = new String[] {"Newfoundland Standard Time", "NST",
				     "Newfoundland Daylight Time", "NDT"};
	String NZST[] = new String[] {"New Zealand Standard Time", "NZST",
				      "New Zealand Daylight Time", "NZDT"};
	String PKT[] = new String[] {"Pakistan Time", "PKT",
				     "Pakistan Summer Time", "PKST"};
	String PST[] = new String[] {"Pacific Standard Time", "PST",
				     "Pacific Daylight Time", "PDT"};
	String SAST[] = new String[] {"South Africa Standard Time", "SAST",
				      "South Africa Summer Time", "SAST"};
	String SBT[] = new String[] {"Solomon Is. Time", "SBT",
				     "Solomon Is. Summer Time", "SBST"};
	String TMT[] = new String[] {"Turkmenistan Time", "TMT",
				     "Turkmenistan Summer Time", "TMST"};
	String ULAT[]= new String[] {"Ulaanbaatar Time", "ULAT",
				     "Ulaanbaatar Summer Time", "ULAST"};
	String WAT[] = new String[] {"Western African Time", "WAT",
				     "Western African Summer Time", "WAST"};
	String WET[] = new String[] {"Western European Time", "WET",
				     "Western European Summer Time", "WEST"};
	String WST_AUS[] = new String[] {"Western Standard Time (Australia)", "WST",
					 "Western Summer Time (Australia)", "WST"};
	String WST_SAMOA[] = new String[] {"West Samoa Time", "WST",
					   "West Samoa Summer Time", "WSST"};

	return new Object[][] {
	    {"PST", PST},
	    {"America/Los_Angeles", PST},
	    {"MST", MST},
	    {"America/Denver", MST},
	    {"PNT", MST},
	    {"America/Phoenix", MST},
	    {"CST", CST},
	    {"America/Chicago", CST},
	    {"EST", EST},
	    {"America/New_York", EST},
	    {"IET", EST},
	    {"America/Indianapolis", EST},
	    {"HST", HST},
	    {"Pacific/Honolulu", HST},
	    {"AST", AKST},
	    {"America/Anchorage", AKST},
	    {"America/Halifax", AST},
	    {"CNT", NST},
	    {"America/St_Johns", NST},
	    {"ECT", CET},
	    {"Europe/Paris", CET},
	    {"GMT", GMT},
	    {"Africa/Casablanca", WET},
	    {"Asia/Jerusalem", new String[] {"Israel Standard Time", "IST",
					     "Israel Daylight Time", "IDT"}},
	    {"JST", JST},
	    {"Asia/Tokyo", JST},
	    {"Europe/Bucharest", EET},
	    {"CTT", CTT},
	    {"Asia/Shanghai", CTT},
	    /* Don't change the order of the above zones
	     * to keep compatibility with the previous version.
	     */

	    {"ACT", new String[] {"Central Standard Time (Northern Territory)", "CST",
				  "Central Daylight Time (Northern Territory)", "CDT"}},
	    {"AET", EST_NSW},
	    {"AGT", AGT},
	    {"ART", EET},
	    {"Africa/Abidjan", GMT},
	    {"Africa/Accra", GMT},
	    {"Africa/Addis_Ababa", EAT},
	    {"Africa/Algiers", CET},
	    {"Africa/Asmera", EAT},
	    {"Africa/Bangui", WAT},
	    {"Africa/Banjul", GMT},
	    {"Africa/Bissau", GMT},
	    {"Africa/Blantyre", CAT},
	    {"Africa/Bujumbura", CAT},
	    {"Africa/Cairo", EET},
	    {"Africa/Conakry", GMT},
	    {"Africa/Dakar", GMT},
	    {"Africa/Dar_es_Salaam", EAT},
	    {"Africa/Djibouti", EAT},
	    {"Africa/Douala", WAT},
	    {"Africa/Freetown", GMT},
	    {"Africa/Gaborone", CAT},
	    {"Africa/Harare", CAT},
	    {"Africa/Johannesburg", SAST},
	    {"Africa/Kampala", EAT},
	    {"Africa/Khartoum", EAT},
	    {"Africa/Kigali", CAT},
	    {"Africa/Kinshasa", WAT},
	    {"Africa/Lagos", WAT},
	    {"Africa/Libreville", WAT},
	    {"Africa/Lome", GMT},
	    {"Africa/Luanda", WAT},
	    {"Africa/Lubumbashi", CAT},
	    {"Africa/Lusaka", CAT},
	    {"Africa/Malabo", WAT},
	    {"Africa/Maputo", CAT},
	    {"Africa/Maseru", SAST},
	    {"Africa/Mbabane", SAST},
	    {"Africa/Mogadishu", EAT},
	    {"Africa/Monrovia", GMT},
	    {"Africa/Nairobi", EAT},
	    {"Africa/Ndjamena", WAT},
	    {"Africa/Niamey", WAT},
	    {"Africa/Nouakchott", GMT},
	    {"Africa/Ouagadougou", GMT},
	    {"Africa/Porto-Novo", WAT},
	    {"Africa/Sao_Tome", GMT},
	    {"Africa/Timbuktu", GMT},
	    {"Africa/Tripoli", EET},
	    {"Africa/Tunis", CET},
	    {"Africa/Windhoek", WAT},
	    {"America/Adak", HAST},
	    {"America/Anguilla", AST},
	    {"America/Antigua", AST},
	    {"America/Aruba", AST},
	    {"America/Asuncion", new String[] {"Paraguay Time", "PYT",
					       "Paraguay Summer Time", "PYST"}},
	    {"America/Barbados", AST},
	    {"America/Belize", CST},
	    {"America/Bogota", new String[] {"Colombia Time", "COT",
					     "Colombia Summer Time", "COST"}},
	    {"America/Buenos_Aires", AGT},
	    {"America/Caracas", new String[] {"Venezuela Time", "VET",
					      "Venezuela Summer Time", "VEST"}},
	    {"America/Cayenne", new String[] {"French Guiana Time", "GFT",
					      "French Guiana Summer Time", "GFST"}},
	    {"America/Cayman", EST},
	    {"America/Costa_Rica", CST},
	    {"America/Cuiaba", AMT},
	    {"America/Curacao", AST},
	    {"America/Dawson_Creek", MST},
	    {"America/Dominica", AST},
	    {"America/Edmonton", MST},
	    {"America/El_Salvador", CST},
	    {"America/Fortaleza", BRT},
	    {"America/Godthab", new String[] {"Western Greenland Time", "WGT",
					      "Western Greenland Summer Time", "WGST"}},
	    {"America/Grand_Turk", EST},
	    {"America/Grenada", AST},
	    {"America/Guadeloupe", AST},
	    {"America/Guatemala", CST},
	    {"America/Guayaquil", new String[] {"Ecuador Time", "ECT",
						"Ecuador Summer Time", "ECST"}},
	    {"America/Guyana", new String[] {"Guyana Time", "GYT",
					     "Guyana Summer Time", "GYST"}},
	    {"America/Havana", CST},
	    {"America/Jamaica", EST},
	    {"America/La_Paz", new String[] {"Bolivia Time", "BOT",
					     "Bolivia Summer Time", "BOST"}},
	    {"America/Lima", new String[] {"Peru Time", "PET",
					   "Peru Summer Time", "PEST"}},
	    {"America/Managua", CST},
	    {"America/Manaus", AMT},
	    {"America/Martinique", AST},
	    {"America/Mazatlan", MST},
	    {"America/Mexico_City", CST},
	    {"America/Miquelon", new String[] {"Pierre & Miquelon Standard Time", "PMST",
					       "Pierre & Miquelon Daylight Time", "PMDT"}},
	    {"America/Montevideo", new String[] {"Uruguay Time", "UYT",
						 "Uruguay Summer Time", "UYST"}},
	    {"America/Montreal", EST},
	    {"America/Montserrat", AST},
	    {"America/Nassau", EST},
	    {"America/Noronha", new String[] {"Fernando de Noronha Time", "FNT",
					      "Fernando de Noronha Summer Time", "FNST"}},
	    {"America/Panama", EST},
	    {"America/Paramaribo", new String[] {"Suriname Time", "SRT",
						 "Suriname Summer Time", "SRST"}},
	    {"America/Port-au-Prince", EST},
	    {"America/Port_of_Spain", AST},
	    {"America/Porto_Acre", new String[] {"Acre Time", "ACT",
						 "Acre Summer Time", "ACST"}},
	    {"America/Puerto_Rico", AST},
	    {"America/Regina", CST},
	    {"America/Santiago", CLT},
	    {"America/Santo_Domingo", AST},
	    {"America/Sao_Paulo", BRT},
	    {"America/Scoresbysund", EGT},
	    {"America/St_Kitts", AST},
	    {"America/St_Lucia", AST},
	    {"America/St_Thomas", AST},
	    {"America/St_Vincent", AST},
	    {"America/Tegucigalpa", CST},
	    {"America/Thule", AST},
	    {"America/Tijuana", PST},
	    {"America/Tortola", AST},
	    {"America/Vancouver", PST},
	    {"America/Winnipeg", CST},
	    {"Antarctica/Casey", WST_AUS},
	    {"Antarctica/DumontDUrville", new String[] {"Dumont-d'Urville Time", "DDUT",
							"Dumont-d'Urville Summer Time", "DDUST"}},
	    {"Antarctica/Mawson", new String[] {"Mawson Time", "MAWT",
						"Mawson Summer Time", "MAWST"}},
	    {"Antarctica/McMurdo", NZST},
	    {"Antarctica/Palmer", CLT},
	    {"Asia/Aden", ARAST},
	    {"Asia/Almaty", new String[] {"Alma-Ata Time", "ALMT",
					  "Alma-Ata Summer Time", "ALMST"}},
	    {"Asia/Amman", EET},
	    {"Asia/Anadyr", new String[] {"Anadyr Time", "ANAT",
					  "Anadyr Summer Time", "ANAST"}},
	    {"Asia/Aqtau", new String[] {"Aqtau Time", "AQTT",
					 "Aqtau Summer Time", "AQTST"}},
	    {"Asia/Aqtobe", new String[] {"Aqtobe Time", "AQTT",
					  "Aqtobe Summer Time", "AQTST"}},
	    {"Asia/Ashgabat", TMT},
	    {"Asia/Ashkhabad", TMT},
	    {"Asia/Baghdad", ARAST},
	    {"Asia/Bahrain", ARAST},
	    {"Asia/Baku", new String[] {"Azerbaijan Time", "AZT",
					"Azerbaijan Summer Time", "AZST"}},
	    {"Asia/Bangkok", ICT},
	    {"Asia/Beirut", EET},
	    {"Asia/Bishkek", new String[] {"Kirgizstan Time", "KGT",
					   "Kirgizstan Summer Time", "KGST"}},
	    {"Asia/Brunei", new String[] {"Brunei Time", "BNT",
					  "Brunei Summer Time", "BNST"}},
	    {"Asia/Calcutta", IST},
	    {"Asia/Colombo", new String[] {"Sri Lanka Time", "LKT",
					   "Sri Lanka Summer Time", "LKST"}},
	    {"Asia/Dacca", BDT},
	    {"Asia/Dhaka", BDT},
	    {"Asia/Damascus", EET},
	    {"Asia/Dubai", GST},
	    {"Asia/Dushanbe", new String[] {"Tajikistan Time", "TJT",
					    "Tajikistan Summer Time", "TJST"}},
	    {"Asia/Hong_Kong", new String[] {"Hong Kong Time", "HKT",
					     "Hong Kong Summer Time", "HKST"}},
	    {"Asia/Irkutsk", new String[] {"Irkutsk Time", "IRKT",
					   "Irkutsk Summer Time", "IRKST"}},
	    {"Asia/Jakarta", new String[] {"Java Time", "JAVT",
					   "Java Summer Time", "JAVST"}},
	    {"Asia/Jayapura", new String[] {"Jayapura Time", "JAYT",
					    "Jayapura Summer Time", "JAYST"}},
	    {"Asia/Kabul", new String[] {"Afghanistan Time", "AFT",
					 "Afghanistan Summer Time", "AFST"}},
	    {"Asia/Kamchatka", new String[] {"Petropavlovsk-Kamchatski Time", "PETT",
					     "Petropavlovsk-Kamchatski Summer Time", "PETST"}},
	    {"Asia/Karachi", PKT},
	    {"Asia/Katmandu", new String[] {"Nepal Time", "NPT",
					    "Nepal Summer Time", "NPST"}},
	    {"Asia/Krasnoyarsk", new String[] {"Krasnoyarsk Time", "KRAT",
					       "Krasnoyarsk Summer Time", "KRAST"}},
	    {"Asia/Kuala_Lumpur", new String[] {"Malaysia Time", "MYT",
						"Malaysia Summer Time", "MYST"}},
	    {"Asia/Kuwait", ARAST},
	    {"Asia/Macao", CTT},
	    {"Asia/Magadan", new String[] {"Magadan Time", "MAGT",
					   "Magadan Summer Time", "MAGST"}},
	    {"Asia/Manila", new String[] {"Philippines Time", "PHT",
					  "Philippines Summer Time", "PHST"}},
	    {"Asia/Muscat", GST},
	    {"Asia/Nicosia", EET},
	    {"Asia/Novosibirsk", new String[] {"Novosibirsk Time", "NOVT",
					       "Novosibirsk Summer Time", "NOVST"}},
	    {"Asia/Phnom_Penh", ICT},
	    {"Asia/Pyongyang", KST},
	    {"Asia/Qatar", ARAST},
	    {"Asia/Rangoon", new String[] {"Myanmar Time", "MMT",
					   "Myanmar Summer Time", "MMST"}},
	    {"Asia/Riyadh", ARAST},
	    {"Asia/Saigon", ICT},
	    {"Asia/Seoul", KST},
	    {"Asia/Singapore", new String[] {"Singapore Time", "SGT",
					     "Singapore Summer Time", "SGST"}},
	    {"Asia/Taipei", CTT},
	    {"Asia/Tashkent", new String[] {"Uzbekistan Time", "UZT",
					    "Uzbekistan Summer Time", "UZST"}},
	    {"Asia/Tbilisi", new String[] {"Georgia Time", "GET",
					   "Georgia Summer Time", "GEST"}},
	    {"Asia/Tehran", IRT},
	    {"Asia/Thimbu", BTT},
	    {"Asia/Thimphu", BTT},
	    {"Asia/Ujung_Pandang", new String[] {"Borneo Time", "BORT",
						 "Borneo Summer Time", "BORST"}},
	    {"Asia/Ulaanbaatar", ULAT},
	    {"Asia/Ulan_Bator", ULAT},
	    {"Asia/Vientiane", ICT},
	    {"Asia/Vladivostok", new String[] {"Vladivostok Time", "VLAT",
					       "Vladivostok Summer Time", "VLAST"}},
	    {"Asia/Yakutsk", new String[] {"Yakutsk Time", "YAKT",
					   "Yaktsk Summer Time", "YAKST"}},
	    {"Asia/Yekaterinburg", new String[] {"Yekaterinburg Time", "YEKT",
						 "Yekaterinburg Summer Time", "YEKST"}},
	    {"Asia/Yerevan", ARMT},
	    {"Atlantic/Azores", new String[] {"Azores Time", "AZOT",
					      "Azores Summer Time", "AZOST"}},
	    {"Atlantic/Bermuda", AST},
	    {"Atlantic/Canary", WET},
	    {"Atlantic/Cape_Verde", new String[] {"Cape Verde Time", "CVT",
						  "Cape Verde Summer Time", "CVST"}},
	    {"Atlantic/Faeroe", WET},
	    {"Atlantic/Jan_Mayen", EGT},
	    {"Atlantic/Reykjavik", GMT},
	    {"Atlantic/South_Georgia", new String[] {"South Georgia Standard Time", "GST",
						     "South Georgia Daylight Time", "GDT"}},
	    {"Atlantic/St_Helena", GMT},
	    {"Atlantic/Stanley", new String[] {"Falkland Is. Time", "FKT",
					       "Falkland Is. Summer Time", "FKST"}},
	    {"Australia/Adelaide", new String[] {"Central Standard Time (South Australia)", "CST",
						 "Central Summer Time (South Australia)", "CST"}},
	    {"Australia/Brisbane", new String[] {"Eastern Standard Time (Queensland)", "EST",
						 "Eastern Summer Time (Queensland)", "EST"}},
	    {"Australia/Broken_Hill", new String[] {"Central Standard Time (South Australia/New South Wales)", "CST",
						    "Central Summer Time (South Australia/New South Wales)", "CST"}},
	    {"Australia/Darwin", new String[] {"Central Standard Time (Northern Territory)", "CST",
					       "Central Summer Time (Northern Territory)", "CST"}},
	    {"Australia/Hobart", new String[] {"Eastern Standard Time (Tasmania)", "EST",
					       "Eastern Summer Time (Tasmania)", "EST"}},
	    {"Australia/Lord_Howe", new String[] {"Load Howe Standard Time", "LHST",
						  "Load Howe Summer Time", "LHST"}},
	    {"Australia/Perth", WST_AUS},
	    {"Australia/Sydney", EST_NSW},
	    {"BET", BRT},
	    {"BST", BDT},
	    {"CAT", CAT},
	    {"EAT", EAT},
	    {"EET", EET},
	    {"Europe/Amsterdam", CET},
	    {"Europe/Andorra", CET},
	    {"Europe/Athens", EET},
	    {"Europe/Belgrade", CET},
	    {"Europe/Berlin", CET},
	    {"Europe/Brussels", CET},
	    {"Europe/Budapest", CET},
	    {"Europe/Chisinau", EET},
	    {"Europe/Copenhagen", CET},
	    {"Europe/Dublin", new String[] {"Greenwich Mean Time", "GMT",
					    "Irish Summer Time", "IST"}},
	    {"Europe/Gibraltar", CET},
	    {"Europe/Helsinki", EET},
	    {"Europe/Istanbul", EET},
	    {"Europe/Kaliningrad", EET},
	    {"Europe/Kiev", EET},
	    {"Europe/Lisbon", WET},
	    {"Europe/London", new String[] {"Greenwich Mean Time", "GMT",
					    "British Summer Time", "BST"}},
	    {"Europe/Luxembourg", CET},
	    {"Europe/Madrid", CET},
	    {"Europe/Malta", CET},
	    {"Europe/Minsk", EET},
	    {"Europe/Monaco", CET},
	    {"Europe/Moscow", new String[] {"Moscow Standard Time", "MSK",
					    "Moscow Daylight Time", "MSD"}},
	    {"Europe/Oslo", CET},
	    {"Europe/Prague", CET},
	    {"Europe/Riga", EET},
	    {"Europe/Rome", CET},
	    {"Europe/Samara", new String[] {"Samara Time", "SAMT",
					    "Samara Summer Time", "SAMST"}},
	    {"Europe/Simferopol", EET},
	    {"Europe/Sofia", EET},
	    {"Europe/Stockholm", CET},
	    {"Europe/Tallinn", EET},
	    {"Europe/Tirane", CET},
	    {"Europe/Vaduz", CET},
	    {"Europe/Vienna", CET},
	    {"Europe/Vilnius", EET},
	    {"Europe/Warsaw", CET},
	    {"Europe/Zurich", CET},
	    {"IST", IST},
	    {"Indian/Antananarivo", EAT},
	    {"Indian/Chagos", new String[] {"Indian Ocean Territory Time", "IOT",
					    "Indian Ocean Territory Summer Time", "IOST"}},
	    {"Indian/Christmas", new String[] {"Christmas Island Time", "CXT",
					       "Christmas Island Summer Time", "CXST"}},
	    {"Indian/Cocos", new String[] {"Cocos Islands Time", "CCT",
					   "Cocos Islands Summer Time", "CCST"}},
	    {"Indian/Comoro", EAT},
	    {"Indian/Kerguelen", new String[] {"French Southern & Antarctic Lands Time", "TFT",
					       "French Southern & Antarctic Lands Summer Time", "TFST"}},
	    {"Indian/Mahe", new String[] {"Seychelles Time", "SCT",
					  "Seychelles Summer Time", "SCST"}},
	    {"Indian/Maldives", new String[] {"Maldives Time", "MVT",
					      "Maldives Summer Time", "MVST"}},
	    {"Indian/Mauritius", new String[] {"Mauritius Time", "MUT",
					       "Mauritius Summer Time", "MUST"}},
	    {"Indian/Mayotte", EAT},
	    {"Indian/Reunion", new String[] {"Reunion Time", "RET",
					     "Reunion Summer Time", "REST"}},
	    {"MET", IRT},
	    {"MIT", WST_SAMOA},
	    {"NET", ARMT},
	    {"NST", NZST},
	    {"PLT", PKT},
	    {"PRT", AST},
	    {"Pacific/Apia", WST_SAMOA},
	    {"Pacific/Auckland", NZST},
	    {"Pacific/Chatham", new String[] {"Chatham Standard Time", "CHAST",
					      "Chatham Daylight Time", "CHADT"}},
	    {"Pacific/Easter", new String[] {"Easter Is. Time", "EAST",
					     "Easter Is. Summer Time", "EASST"}},
	    {"Pacific/Efate", new String[] {"Vanuatu Time", "VUT",
					    "Vanuatu Summer Time", "VUST"}},
	    {"Pacific/Enderbury", new String[] {"Phoenix Is. Time", "PHOT",
						"Phoenix Is. Summer Time", "PHOST"}},
	    {"Pacific/Fakaofo", new String[] {"Tokelau Time", "TKT",
					      "Tokelau Summer Time", "TKST"}},
	    {"Pacific/Fiji", new String[] {"Fiji Time", "FJT",
					   "Fiji Summer Time", "FJST"}},
	    {"Pacific/Funafuti", new String[] {"Tuvalu Time", "TVT",
					       "Tuvalu Summer Time", "TVST"}},
	    {"Pacific/Galapagos", new String[] {"Galapagos Time", "GALT",
						"Galapagos Summer Time", "GALST"}},
	    {"Pacific/Gambier", new String[] {"Gambier Time", "GAMT",
					      "Gambier Summer Time", "GAMST"}},
	    {"Pacific/Guadalcanal", SBT},
	    {"Pacific/Guam", ChST},
	    {"Pacific/Kiritimati", new String[] {"Line Is. Time", "LINT",
						 "Line Is. Summer Time", "LINST"}},
	    {"Pacific/Kosrae", new String[] {"Kosrae Time", "KOST",
					     "Kosrae Summer Time", "KOSST"}},
	    {"Pacific/Majuro", new String[] {"Marshall Islands Time", "MHT",
					     "Marshall Islands Summer Time", "MHST"}},
	    {"Pacific/Marquesas", new String[] {"Marquesas Time", "MART",
						"Marquesas Summer Time", "MARST"}},
	    {"Pacific/Nauru", new String[] {"Nauru Time", "NRT",
					    "Nauru Summer Time", "NRST"}},
	    {"Pacific/Niue", new String[] {"Niue Time", "NUT",
					   "Niue Summer Time", "NUST"}},
	    {"Pacific/Norfolk", new String[] {"Norfolk Time", "NFT",
					      "Norfolk Summer Time", "NFST"}},
	    {"Pacific/Noumea", new String[] {"New Caledonia Time", "NCT",
					     "New Caledonia Summer Time", "NCST"}},
	    {"Pacific/Pago_Pago", new String[] {"Samoa Standard Time", "SST",
						"Samoa Daylight Time", "SDT"}},
	    {"Pacific/Palau", new String[] {"Palau Time", "PWT",
					    "Palau Summer Time", "PWST"}},
	    {"Pacific/Pitcairn", new String[] {"Pitcairn Standard Time", "PST",
					       "Pitcairn Daylight Time", "PDT"}},
	    {"Pacific/Ponape", new String[] {"Ponape Time", "PONT",
					     "Ponape Summer Time", "PONST"}},
	    {"Pacific/Port_Moresby", new String[] {"Papua New Guinea Time", "PGT",
						   "Papua New Guinea Summer Time", "PGST"}},
	    {"Pacific/Rarotonga", new String[] {"Cook Is. Time", "CKT",
						"Cook Is. Summer Time", "CKST"}},
	    {"Pacific/Saipan", ChST},
	    {"Pacific/Tahiti", new String[] {"Tahiti Time", "TAHT",
					     "Tahiti Summer Time", "TAHST"}},
	    {"Pacific/Tarawa", new String[] {"Gilbert Is. Time", "GILT",
					     "Gilbert Is. Summer Time", "GILST"}},
	    {"Pacific/Tongatapu", new String[] {"Tonga Time", "TOT",
						"Tonga Summer Time", "TOST"}},
	    {"Pacific/Truk", new String[] {"Truk Time", "TRUT",
					   "Truk Summer Time", "TRUST"}},
	    {"Pacific/Wake", new String[] {"Wake Time", "WAKT",
					   "Wake Summer Time", "WAKST"}},
	    {"Pacific/Wallis", new String[] {"Wallis & Futuna Time", "WFT",
					     "Wallis & Futuna Summer Time", "WFST"}},
	    {"SST", SBT},
	    {"UTC", new String[] {"Coordinated Universal Time", "UTC",
			 	  "Coordinated Universal Time", "UTC"}},
	    {"VST", ICT},
	    {"WET", WET},

            {LOCALPATTERNCHARS, "GyMdkHmsSEDFwWahKz"},
        };
    }
}
