/*
 * @(#)$RCSfile$ $Revision$ $Date$
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

import java.util.Locale;

/**
 * This class used to be the base class for all of the LocaleElements classes.  This has
 * been changed so that all of the LocaleElements classes descend from ListResourceBundle.
 * This class now exists only to allow a way to get the list of available resources.  Even
 * this will be changing in the future.
 *
 * @author Asmus Freytag
 * @author Mark Davis
 * @version $Revision: 1.15 $ 03/31/98
 */

public class LocaleData {
    // use to get the list of locales that have this data
    public static Locale[] getAvailableLocales(String key)
    {
        return localeList;  // hard-coded for now
    }

    // ========== privates ==========

    // for now, we hard-code the enumeration
    private static Locale[] localeList = {
        new Locale("ar", "EG", ""),
        new Locale("be", "BY", ""),
        new Locale("bg", "BG", ""),
        new Locale("ca", "ES", ""),
        new Locale("cs", "CZ", ""),
        new Locale("da", "DK", ""),
        new Locale("de", "DE", ""),
        new Locale("de", "AT", ""),
        new Locale("de", "CH", ""),
        new Locale("el", "GR", ""),
        new Locale("en", "US", ""),
        new Locale("en", "AU", ""),
        new Locale("en", "CA", ""),
        new Locale("en", "GB", ""),
        new Locale("en", "IE", ""),
        new Locale("en", "NZ", ""),
        new Locale("en", "ZA", ""),
        new Locale("es", "ES", ""),
        new Locale("es", "AR", ""),
        new Locale("es", "BO", ""),
        new Locale("es", "CL", ""),
        new Locale("es", "CO", ""),
        new Locale("es", "CR", ""),
        new Locale("es", "DO", ""),
        new Locale("es", "EC", ""),
        new Locale("es", "GT", ""),
        new Locale("es", "HN", ""),
        new Locale("es", "MX", ""),
        new Locale("es", "NI", ""),
        new Locale("es", "PA", ""),
        new Locale("es", "PY", ""),
        new Locale("es", "PE", ""),
        new Locale("es", "PR", ""),
        new Locale("es", "SV", ""),
        new Locale("es", "UY", ""),
        new Locale("es", "VE", ""),
        new Locale("et", "EE", ""),
        new Locale("fi", "FI", ""),
        new Locale("fr", "FR", ""),
        new Locale("fr", "BE", ""),
        new Locale("fr", "CA", ""),
        new Locale("fr", "CH", ""),
        new Locale("hr", "HR", ""),
        new Locale("hu", "HU", ""),
        new Locale("is", "IS", ""),
        new Locale("it", "IT", ""),
        new Locale("it", "CH", ""),
        new Locale("iw", "IL", ""),
        new Locale("ja", "JP", ""),
        new Locale("ko", "KR", ""),
        new Locale("lt", "LT", ""),
        new Locale("lv", "LV", ""),
        new Locale("mk", "MK", ""),
        new Locale("nl", "NL", ""),
        new Locale("nl", "BE", ""),
        new Locale("no", "NO", "B"),
        new Locale("no", "NO", "NY"),
        new Locale("pl", "PL", ""),
        new Locale("pt", "PT", ""),
        new Locale("pt", "BR", ""),
        new Locale("ro", "RO", ""),
        new Locale("ru", "RU", ""),
        new Locale("sh", "YU", ""),
        new Locale("sk", "SK", ""),
        new Locale("sl", "SI", ""),
        new Locale("sq", "AL", ""),
        new Locale("sr", "YU", ""),
        new Locale("sv", "SE", ""),
        new Locale("th", "TH", ""),
        new Locale("tr", "TR", ""),
        new Locale("uk", "UA", ""),
        new Locale("zh", "CN", ""),
        new Locale("zh", "TW", "")
    };
}
