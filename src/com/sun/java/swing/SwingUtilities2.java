/*
 * @(#)SwingUtilities2.java	1.4 05/08/30
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing;

import java.security.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import sun.awt.AppContext;
import sun.security.util.SecurityConstants;

/**
 * A collection of utility methods for Swing.
 * <p>
 * <b>WARNING:</b> While this class is public, it should not be treated as
 * public API and its API may change in incompatable ways between dot dot
 * releases and even patch releases. You should not rely on this class even
 * existing.
 *
 * @version 1.4 08/30/05
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

    //security stuff
    private static Field inputEvent_CanAccessSystemClipboard_Field = null;
    private static final String UntrustedClipboardAccess =
      "UNTRUSTED_CLIPBOARD_ACCESS_KEY";

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


    /*
     * here goes the fix for 4856343 [Problem with applet interaction
     * with system selection clipboard]
     * 
     * NOTE. In case isTrustedContext() no checking
     * are to be performed
     */


    /**
     * checks the security permissions for accessing system clipboard
     * 
     * for untrusted context (see isTrustedContext) checks the
     * permissions for the current event being handled
     *
     */
    public static boolean canAccessSystemClipboard() {
        boolean canAccess = false;
        if (!GraphicsEnvironment.isHeadless()) {
            SecurityManager sm = System.getSecurityManager();
            if (sm == null) {
                canAccess = true;
            } 
	    else {
                try {
                    sm.checkSystemClipboardAccess();
                    canAccess = true;  
                } catch (SecurityException e) {
                }
                if (canAccess && ! isTrustedContext()) {
                    canAccess = canCurrentEventAccessSystemClipboard(true);
                }
            }
        }
        return canAccess;
    }

    /**
     * Returns true if EventQueue.getCurrentEvent() has the permissions to
     * access the system clipboard
     */
    public static boolean canCurrentEventAccessSystemClipboard() {
        return  isTrustedContext()
            || canCurrentEventAccessSystemClipboard(false);
    }
    
    /**
     * Returns true if the given event has permissions to access the
     * system clipboard
     * 
     * @param e AWTEvent to check
     */
    public static boolean canEventAccessSystemClipboard(AWTEvent e) {
        return isTrustedContext() 
            || canEventAccessSystemClipboard(e, false);
    }
    
    /**
     * returns canAccessSystemClipboard field from InputEvent
     *
     * @param ie InputEvent to get the field from 
     */
    private static synchronized boolean inputEvent_canAccessSystemClipboard(InputEvent ie) {
        if (inputEvent_CanAccessSystemClipboard_Field == null) { 
            inputEvent_CanAccessSystemClipboard_Field =
                (Field)AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public Object run() {
                            Field field = null;
                            try {
                                field = InputEvent.class.
                                    getDeclaredField("canAccessSystemClipboard");
                                field.setAccessible(true);
                                return field;
                            } catch (SecurityException e) {
                            } catch (NoSuchFieldException e) {
                            }
                            return null;
                        }
                    });
        }
        if (inputEvent_CanAccessSystemClipboard_Field == null) { 
            return false;
        }
        boolean ret = false;
        try {
            ret = inputEvent_CanAccessSystemClipboard_Field.
                getBoolean(ie);
        } catch(IllegalAccessException e) {
        } 
        return ret;
    }

    /**
     * Returns true if the given event is corrent gesture for
     * accessing clipboard
     * 
     * @param ie InputEvent to check
     */

    private static boolean isAccessClipboardGesture(InputEvent ie) {
        boolean allowedGesture = false;
        if (ie instanceof KeyEvent) { //we can validate only keyboard gestures
            KeyEvent ke = (KeyEvent)ie;
            int keyCode = ke.getKeyCode();
            int keyModifiers = ke.getModifiers();
            switch(keyCode) {
            case KeyEvent.VK_C:
            case KeyEvent.VK_V:
            case KeyEvent.VK_X:
                allowedGesture = (keyModifiers == InputEvent.CTRL_MASK);
                break;
            case KeyEvent.VK_INSERT:
                allowedGesture = (keyModifiers == InputEvent.CTRL_MASK ||
                                  keyModifiers == InputEvent.SHIFT_MASK);
                break;
            case KeyEvent.VK_COPY:
            case KeyEvent.VK_PASTE:
            case KeyEvent.VK_CUT:
                allowedGesture = true;
                break;
            case KeyEvent.VK_DELETE:
                allowedGesture = ( keyModifiers == InputEvent.SHIFT_MASK);
                break;
            }
        } 
        return allowedGesture;
    }

    /**
     * Returns true if e has the permissions to
     * access the system clipboard and if it is allowed gesture (if
     * checkGesture is true)
     *
     * @param e AWTEvent to check
     * @param checkGesture boolean
     */
    private static boolean canEventAccessSystemClipboard(AWTEvent e, 
                                                        boolean checkGesture) {
        if (EventQueue.isDispatchThread()) { 
            /*
             * Checking event permissions makes sense only for event
             * dispathing thread 
             */
            if (e instanceof InputEvent 
                && (! checkGesture || isAccessClipboardGesture((InputEvent)e))) {
                return inputEvent_canAccessSystemClipboard((InputEvent)e);
            } else { 
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Returns true if EventQueue.getCurrentEvent() has the permissions to
     * access the system clipboard and if it is allowed gesture (if
     * checkGesture true)
     * 
     * @param checkGesture boolean 
     */
    private static boolean canCurrentEventAccessSystemClipboard(boolean 
                                                               checkGesture) {
        AWTEvent event = EventQueue.getCurrentEvent();
        return canEventAccessSystemClipboard(event, checkGesture);
    }
  
      /**
       * see RFE 5012841 [Per AppContext security permissions] for the
       * details
       *
       */ 
      private static boolean isTrustedContext() {
          return (System.getSecurityManager() == null) 
              || (AppContext.getAppContext().
                  get(UntrustedClipboardAccess) == null);
      }
  
}
