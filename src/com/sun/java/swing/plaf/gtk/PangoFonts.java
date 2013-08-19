/*
 * @(#)PangoFonts.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.plaf.FontUIResource;
import java.util.StringTokenizer;
import sun.java2d.SunGraphicsEnvironment;

/**
 * @author Shannon Hickey
 * @author Leif Samuelsson
 * @version 1.6 01/23/03
 */
class PangoFonts {

    // A simple array for now, but this could be a HashMap if
    // many more mappings are added
    private static final String[][] nameMap = {{"sans", "sansserif"},
                                               {"monospace", "monospaced"}};

    /**
     * Amount to scale fonts by.
     */
    private static double fontScale;

    static {
        GraphicsEnvironment ge =
           GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc =
            ge.getDefaultScreenDevice().getDefaultConfiguration();
        AffineTransform at = gc.getNormalizingTransform();
        fontScale = at.getScaleY();
    }
    
    private static String mapName(String name) {
        for (int i = 0; i < nameMap.length; i++) {
            if (name.equals(nameMap[i][0])) {
                return nameMap[i][1];
            }
        }
        
        return null;
    }

    /**
     * Parses a String containing a pango font description and returns
     * a Font object.
     *
     * @param pangoName a String describing a pango font
     *                  e.g. "Sans Italic 10"
     * @return a Font object as a FontUIResource
     *         or null if no suitable font could be created.
     */    
    static Font lookupFont(String pangoName) {
        String family = "";
        int style = Font.PLAIN;
        int size = 10;

        StringTokenizer tok = new StringTokenizer(pangoName);

        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (word.equalsIgnoreCase("italic")) {
                style |= Font.ITALIC;
            } else if (word.equalsIgnoreCase("bold")) {
                style |= Font.BOLD;
            } else if (GTKScanner.CHARS_DIGITS.indexOf(word.charAt(0)) != -1) {
                try {
                    size = Integer.parseInt(word);
                } catch (NumberFormatException ex) {
                }
            } else {
                if (family.length() > 0) {
                    family += " ";
                }
                
                family += word;
            }
        }

        // Scale the font
        size = (int)(size * fontScale);

        String mappedName = mapName(family.toLowerCase());
        if (mappedName != null) {
            family = mappedName;
        }

        Font font = new FontUIResource(family, style, size);
        if (!SunGraphicsEnvironment.isLogicalFont(font) &&
                !SunGraphicsEnvironment.fontSupportsDefaultEncoding(font)) {
            // Font does not contain enough glyphs for this locale, fallback
            // to SansSerif.
            // PENDING: should create a composite Font here.
            font = new FontUIResource("sansserif", style, size);
        }
        return font;
    }

}
