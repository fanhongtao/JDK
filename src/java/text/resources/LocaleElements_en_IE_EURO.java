/*
 * @(#)LocaleElements_en_IE_EURO.java	1.1 98/07/07
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

/*
 * EURO locale for en_IE, machine generated on 18 May 1998
 */

// WARNING : the format of this file will change in the future!

package java.text.resources;

import java.util.ListResourceBundle;

public class LocaleElements_en_IE_EURO extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "NumberPatterns",
                new String[] {
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "\u20AC#,##0.00;-\u20AC#,##0.00", // currency pattern
                    "#,##0%" // percent pattern
                }
            }
        };
    }
}
