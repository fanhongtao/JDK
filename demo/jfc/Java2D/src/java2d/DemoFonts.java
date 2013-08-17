/*
 * @(#)DemoFonts.java	1.8 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java2d;

import java.awt.Font;
import java.util.Hashtable;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;


/**
 * A cache of the dynamically loaded fonts found in the fonts directory.
 */
public class DemoFonts {

    private String[] names =  { "A.ttf" };
    private static Hashtable cache;


    public DemoFonts() {
        cache = new Hashtable(names.length);
        for (int i = 0; i < names.length; i++) {
            cache.put(names[i], getFont(names[i]));
        }
    }


    public static Font getFont(String name) {
        Font font = null;
        if (cache != null) {
            if ((font = (Font) cache.get(name)) != null) {
                return font;
            }
        }
        String fName = "/fonts/" + name;
        try {
            InputStream is = DemoFonts.class.getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            System.err.println(fName + " not loaded.  Using serif font.");
            font = new Font("serif", Font.PLAIN, 24);
        }
        return font;
    }
}
