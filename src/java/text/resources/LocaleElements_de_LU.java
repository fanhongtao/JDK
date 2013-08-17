/*
 * @(#)LocaleElements_de_LU.java	1.1 98/07/07
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

/**
 * Table of Java supplied standard locale elements
 * Created 19 May 1998
 */

// WARNING : the format of this file will change in the future!

package java.text.resources;

import java.util.ListResourceBundle;

public class LocaleElements_de_LU extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "de_LU" }, // locale id based on iso codes
            { "ShortCountry", "LUX" }, // iso-3 abbrev country name
            { "NumberPatterns",
                new String[] {
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "#,##0.00 F;-#,##0.00 F", // currency pattern
                    "#,##0%" // percent pattern
                }
            }
        };
    }
}
