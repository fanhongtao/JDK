/*
 * @(#)SwingUtilities2.java	1.2 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

/**
 * A collection of utility methods for Swing.
 * <p>
 * <b>WARNING:</b> While this class is public, it should not be treated as
 * public API and its API may change in incompatable ways between dot dot
 * releases and even patch releases. You should not rely on this class even
 * existing.
 *
 * @version 1.2 01/23/03
 */
public class SwingUtilities2 {
    // Maintain a cache of CACHE_SIZE fonts and the left side bearing
    // of the characters falling into the range MIN_CHAR_INDEX to
    // MAX_CHAR_INDEX. The values in lsbCache are created as needed.
    private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(
                             null, false, false);
    private static final byte UNSET = Byte.MAX_VALUE;
    // getLeftSideBearing will consult all characters that fall in the
    // range MIN_CHAR_INDEX to MAX_CHAR_INDEX.
    private static final int MIN_CHAR_INDEX = (int)'W';
    private static final int MAX_CHAR_INDEX = (int)'W' + 1;
    // Windows defines 6 font desktop properties, we will therefore only
    // cache the metrics for 6 fonts.
    private static final int CACHE_SIZE = 6;
    // nextIndex in lsbCache and fontCache to insert a font into.
    private static int nextIndex;
    // Cache of left side bearnings.
    private static byte[][] lsbCache;
    // Caches of fonts
    private static Font[] fontCache;
    private static final char[] oneChar = new char[1];

    static {
        fontCache = new Font[CACHE_SIZE];
        lsbCache = new byte[CACHE_SIZE][];
        for (int counter = 0; counter < CACHE_SIZE; counter++) {
            lsbCache[counter] = new byte[MAX_CHAR_INDEX - MIN_CHAR_INDEX];
            reset(lsbCache[counter]);
        }
    }

    /**
     * Marks the values in the byte array as needing to be recalculated.
     */
    private static void reset(byte[] data) {
        for (int counter = data.length - 1; counter >= 0; counter--) {
            data[counter] = UNSET;
        }
    }

    /**
     * Returns the left side bearing of the first character of string. The
     * left side bearing is calculated from the passed in font assuming
     * a FontRenderContext with the identity transform.
     * If the passed in String is less than one character, this
     * will throw a StringIndexOutOfBoundsException exception.
     */
    public static int getLeftSideBearing(Font f, String string) {
        char firstChar = string.charAt(0);

        return getLeftSideBearing(f, string.charAt(0));
    }

    /**
     * Returns the left side bearing of the first character of string. The
     * left side bearing is calculated from the passed in font assuming
     * a FontRenderContext with the identity transform.
     * If the passed in String is less than one character, this
     * will throw a StringIndexOutOfBoundsException exception.
     */
    public static int getLeftSideBearing(Font f, char firstChar) {
        int charIndex = (int)firstChar;

        if (charIndex < MAX_CHAR_INDEX && charIndex >= MIN_CHAR_INDEX) {
            byte[] lsbs = null;

            charIndex -= MIN_CHAR_INDEX;
            synchronized(SwingUtilities2.class) {
                for (int counter = CACHE_SIZE - 1; counter >= 0;
                     counter--) {
                    if (fontCache[counter] == null) {
                        fontCache[counter] = f;
                        lsbs = lsbCache[counter];
                        break;
                    }
                    else if (fontCache[counter].equals(f)) {
                        lsbs = lsbCache[counter];
                        break;
                    }
                }
                if (lsbs == null) {
                    // no more room
                    lsbs = lsbCache[nextIndex];
                    reset(lsbs);
                    fontCache[nextIndex] = f;
                    nextIndex = (nextIndex + 1) % CACHE_SIZE;
                }
                if (lsbs[charIndex] == UNSET) {
                    lsbs[charIndex] = (byte)_getLeftSideBearing(
                                                 f, firstChar);
                }
                return lsbs[charIndex];
            }
        }
        return 0;
    }

    /**
     * Computes and returns the left side bearing of the specified
     * character.
     */
    private static int _getLeftSideBearing(Font f, char aChar) {
        oneChar[0] = aChar;
        GlyphVector gv = f.createGlyphVector(DEFAULT_FRC, oneChar);
        Rectangle bounds = gv.getGlyphPixelBounds(0, DEFAULT_FRC, 0f, 0f);
        return bounds.x;
    }
}
