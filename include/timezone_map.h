/*
 * @(#)timezone_map.h	1.3 99/01/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

enum EHasDST { kAny, kDaylight, kStandard };

/* OS- and language-independent time zone lookup table.  This is used to
 * determine the host time zone and set the user.timezone property.  This table
 * relies only on the raw offset, presence or absence of DST, and the country
 * code to determine the time zone.  Because it uses the country code, it can
 * differentiate between some zones that are otherwise identical, such as
 * Asia/Tokyo ("JST") and Asia/Seoul ("KST").
 *
 * Modified table derived from 1.2, with entries not present in 1.1 removed.
 */
static struct {
    int          offset;
    enum EHasDST hasDST;
    char*        zone;
} timezone_map[] = {
    /* Offset - the offset, in minutes, from GMT, in the standard Java
     * convention with zones to the east of Greenwich having positive
     * offsets.  Note that this is the reverse of the Win32
     * convention.  Entries are listed in order of offset.
     *
     * Country - the two-letter ISO 3166 country code.
     *
     * Daylight - indicates whether this is a Standard-only zone or
     * whether an alternate Daylight zone may exist. The alternate
     * zone may be historical! A setting of kAny indicates that we
     * don't care.
     *
     * Zone - the name of the Java zone to be assigned for this
     * offset, DST flag, and country. */

    /* Notes: This table was taken for JDK 1.2 and edited, partly by script and
     * partly by hand.  The script removed those zones which are not supported
     * by JDK 1.1.  I resolved duplicate entries which the script couldn't
     * handle.  The result has no need for country entries, since nowhere in the
     * table are zones differentiated by country.  Also, two new zones,
     * previously missing from the 1.1 idMap lookup table, are added to this
     * table (marked N).  The only 1.1 zone not accounted for here is
     * Europe/Istanbul, which cannot be differentiated from Africa/Cairo, and in
     * fact has a conflicting compatibilityMap/idMap name.  */
    
    /* Off. Ctry  Daylight    Zone */
    { -660, /*WS,*/ kAny,      "MIT" /*Pacific/Apia*/              },
    { -600, /*US,*/ kAny,      "HST" /*Pacific/Honolulu*/          },
    { -540, /*US,*/ kAny,      "AST" /*America/Anchorage*/         },
    { -480, /*US,*/ kAny,      "PST" /*America/Los_Angeles*/       },
    { -420, /*US,*/ kDaylight, "MST" /*America/Denver*/            },
    { -420, /*US,*/ kStandard, "PNT" /*America/Phoenix*/           }, /*N*/
    { -360, /*US,*/ kAny,      "CST" /*America/Chicago*/           },
    { -300, /*US,*/ kDaylight, "EST" /*America/New_York*/          },
    { -300, /*US,*/ kStandard, "IET" /*America/Indianapolis*/      }, /*N*/
    { -240, /*CA,*/ kAny,      "PRT" /*America/Halifax*/           },
    { -210, /*CA,*/ kAny,      "CNT" /*America/St_Johns*/          },
    { -180, /*BR,*/ kDaylight, "BET" /*America/Sao_Paulo*/         },
    { -180, /*AR,*/ kStandard, "AGT" /*America/Buenos_Aires*/      },
    {  -60, /*PT,*/ kAny,      "CAT" /*Atlantic/Azores*/           },
    {    0, /*MA,*/ kAny,      "GMT" /*Africa/Casablanca*/         },
    {   60, /*FR,*/ kAny,      "ECT" /*Europe/Paris*/              },
    {  120, /*EG,*/ kAny,      "EET" /*Africa/Cairo*/              },
    {  180, /*SA,*/ kAny,      "EAT" /*Asia/Riyadh*/               },
    {  210, /*IR,*/ kAny,      "MET" /*Asia/Tehran*/               },
    {  240, /*AM,*/ kAny,      "NET" /*Asia/Yerevan*/              },
    {  300, /*PK,*/ kAny,      "PLT" /*Asia/Karachi*/              },
    {  330, /*IN,*/ kAny,      "IST" /*Asia/Calcutta*/             },
    {  360, /*BD,*/ kAny,      "BST" /*Asia/Dacca*/                },
    {  420, /*TH,*/ kAny,      "VST" /*Asia/Bangkok*/              },
    {  480, /*CN,*/ kAny,      "CTT" /*Asia/Shanghai*/             },
    {  540, /*JP,*/ kAny,      "JST" /*Asia/Tokyo*/                },
    {  570, /*AU,*/ kAny,      "ACT" /*Australia/Darwin*/          },
    {  600, /*AU,*/ kAny,      "AET" /*Australia/Sydney*/          },
    {  660, /*SB,*/ kAny,      "SST" /*Pacific/Guadalcanal*/       },
    {  720, /*NZ,*/ kAny,      "NST" /*Pacific/Auckland*/          },
};

#define timezone_map_length (sizeof(timezone_map) / sizeof(timezone_map[0]))
