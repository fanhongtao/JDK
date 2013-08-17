/*
 * @(#)LocaleElements_it_IT_EURO.java	1.2 98/10/09
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

/*
 * EURO locale for it_IT
 */

// WARNING : the format of this file will change in the future!

package java.text.resources;

import java.util.ListResourceBundle;

public class LocaleElements_it_IT_EURO extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "NumberPatterns",
                new String[] {
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "\u20AC #,##0.00;-\u20AC #,##0.00", // currency pattern
                    "#,##0%" // percent pattern
                }
            }
        };
    }
}
