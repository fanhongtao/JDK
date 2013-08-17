/*
 * @(#)KeyStroke.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.Serializable;

/**
 * A KeyStroke instance represents a key being typed on the keyboard -- it
 * contains both a char code for the key and a modifier (alt, shift, ctrl, 
 * meta, or a combination). 
 * <p>
 * KeyStroke objects are used to define high-level (semantic) action events.
 * Instead of trapping every keystroke and throwing away the ones you are
 * not interested in, those keystrokes you care about automatically initiate
 * actions on the components they are registered with. 
 * <p>
 * KeyStroke objects handle both character-code generating keystrokes you 
 * would trap with a KeyTyped event handler and key-code generating keystrokes
 * (like Enter or F1) that you would trap with a KeyPressed event handler.
 * <p>
 * KeyStroke objects are immutable and unique.
 * <p>
 * All KeyStroke objects are cached. To get one, use <code>getKeyStroke</code>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JComponent#registerKeyboardAction
 * @see #getKeyStroke
 *
 * @version 1.31 11/10/98
 * @author Arnaud Weber
 */
public class KeyStroke implements Serializable {
    private static final Object pressedCharCacheKey = 
        new StringBuffer("KeyStroke.pressedCharCacheKey");
    private static final Object releasedCharCacheKey = 
        new StringBuffer("KeyStroke.releasedCharCacheKey");
    private static final Object pressedCodeCacheKey = 
        new StringBuffer("KeyStroke.pressedCodeCacheKey");
    private static final Object releasedCodeCacheKey = 
        new StringBuffer("KeyStroke.releasedCodeCacheKey");

    char keyChar;
    int  keyCode;
    int  modifiers;
    boolean onKeyRelease;

    /* We cache 114 key stroke with keyChar (1368 bytes) */
    /* We cache 114 * 8 key stroke with keyCode and popular modifiers (10944 bytes) */ 
    /* Total cache is around 11K */

    static final int MIN_ASCII_CACHE_INDEX = '\n'; 
    static final int MAX_ASCII_CACHE_INDEX = 0x7F;

    /* Lock object used in place of class object for synchronization. 
     * (4187686)
     */
    private static final Object classLock = new Object();

    /* It is impossible to instantiate a KeyStroke.  Use getKeyStroke() 
     * instead */
    private KeyStroke() {
        
    }

    static KeyStroke getCachedKeyCharKeyStroke(char keyChar,boolean onKeyRelease) {
        KeyStroke result = null;
        if(keyChar >= MIN_ASCII_CACHE_INDEX && keyChar < MAX_ASCII_CACHE_INDEX) {
            synchronized(classLock) {
                KeyStroke cache[];
                if(onKeyRelease) 
                    cache = (KeyStroke[])SwingUtilities.appContextGet(
                        releasedCharCacheKey);
                else
                    cache = (KeyStroke[])SwingUtilities.appContextGet(
                        pressedCharCacheKey);
                if(cache != null)
                    result = cache[((int)keyChar) - MIN_ASCII_CACHE_INDEX];
            }
        }
        return result;
    }

    static void cacheKeyCharKeyStroke(KeyStroke ks,boolean onKeyRelease) {
        if(ks.keyChar >= MIN_ASCII_CACHE_INDEX && ks.keyChar < MAX_ASCII_CACHE_INDEX) {
            synchronized(classLock) {
                if(onKeyRelease) {
                    KeyStroke releasedKeyCharKeyStrokeCache[] = (KeyStroke[])
                        SwingUtilities.appContextGet(releasedCharCacheKey);
                    if(releasedKeyCharKeyStrokeCache == null) {
                        releasedKeyCharKeyStrokeCache = new KeyStroke[MAX_ASCII_CACHE_INDEX - MIN_ASCII_CACHE_INDEX];
                        SwingUtilities.appContextPut(
                            releasedCharCacheKey, releasedKeyCharKeyStrokeCache);
                    }
                    releasedKeyCharKeyStrokeCache[((int)ks.keyChar) - MIN_ASCII_CACHE_INDEX] = ks;
                } else {
                    KeyStroke pressedKeyCharKeyStrokeCache[] = (KeyStroke[])
                        SwingUtilities.appContextGet(pressedCharCacheKey);
                    if(pressedKeyCharKeyStrokeCache == null) {
                        pressedKeyCharKeyStrokeCache = new KeyStroke[MAX_ASCII_CACHE_INDEX - MIN_ASCII_CACHE_INDEX];
                        SwingUtilities.appContextPut(
                            pressedCharCacheKey, pressedKeyCharKeyStrokeCache);
                    }
                    pressedKeyCharKeyStrokeCache[((int)ks.keyChar) - MIN_ASCII_CACHE_INDEX] = ks;
                }
            }
        }
    }

    static int subIndexForModifier(int modifiers) {
        if(modifiers == 0)
            return 0;
        else if(modifiers == InputEvent.SHIFT_MASK)
            return 1;
        else if(modifiers == InputEvent.CTRL_MASK)
            return 2;
        else if(modifiers == InputEvent.ALT_MASK)
            return 3;
        
        return -1;
    }

    static KeyStroke getCachedKeyStroke(int keyCode,int modifiers,boolean onKeyRelease) {
        int subIndex;
        KeyStroke result = null;
        if(keyCode >= MIN_ASCII_CACHE_INDEX && keyCode < MAX_ASCII_CACHE_INDEX &&
           (subIndex = subIndexForModifier(modifiers)) != -1) {
            synchronized(classLock) {
                KeyStroke cache[][];
                if(onKeyRelease) 
                    cache = (KeyStroke[][])SwingUtilities.appContextGet(
                        pressedCodeCacheKey);
                else
                    cache = (KeyStroke[][])SwingUtilities.appContextGet(
                        releasedCodeCacheKey);

                if(cache != null)
                    result = cache[subIndex][keyCode - MIN_ASCII_CACHE_INDEX];
            }
        }
        return result;
    }

    static void cacheKeyStroke(KeyStroke ks) {
        int subIndex = -1;
        if(ks.keyCode >= MIN_ASCII_CACHE_INDEX && ks.keyCode < MAX_ASCII_CACHE_INDEX &&
           (subIndex = subIndexForModifier(ks.modifiers)) != -1) {
            synchronized(classLock) {
                KeyStroke cache[][] = null;
                if(ks.onKeyRelease) {
                    KeyStroke[][] pressedKeyCodeKeyStrokeCache = (KeyStroke[][])
                        SwingUtilities.appContextGet(pressedCodeCacheKey);
                    if(pressedKeyCodeKeyStrokeCache == null) {
                        pressedKeyCodeKeyStrokeCache = new KeyStroke[4][MAX_ASCII_CACHE_INDEX - 
                                                                         MIN_ASCII_CACHE_INDEX];
                        SwingUtilities.appContextPut(
                            pressedCodeCacheKey, pressedKeyCodeKeyStrokeCache);
                    }
                    cache = pressedKeyCodeKeyStrokeCache;
                } else {
                    KeyStroke[][] releasedKeyCodeKeyStrokeCache = (KeyStroke[][])
                        SwingUtilities.appContextGet(releasedCodeCacheKey);
                    if(releasedKeyCodeKeyStrokeCache == null) {
                        releasedKeyCodeKeyStrokeCache = new KeyStroke[4][MAX_ASCII_CACHE_INDEX - 
                                                                          MIN_ASCII_CACHE_INDEX];
                        SwingUtilities.appContextPut(
                            releasedCodeCacheKey, releasedKeyCodeKeyStrokeCache);
                    }
                    cache = releasedKeyCodeKeyStrokeCache;
                }

                cache[subIndex][ks.keyCode - MIN_ASCII_CACHE_INDEX] = ks;
            }
        }
    }
    
    /**
     * Return a shared instance of a key stroke that is
     * activated when the key is pressed (i.e. a KeyStroke
     * for the KeyEvent.KEY_TYPED event).
     *
     * @param keyChar the character value for a keyboard key
     * @return a KeyStroke object for that key
     */
    public static KeyStroke getKeyStroke(char keyChar) {
        return getKeyStroke(keyChar,false);
    }

    /**
     * Return a shared instance of a key stroke, specifying
     * whether the key is considered to be activated when it is 
     * pressed or when it is released.
     *
     * @param keyChar the character value for a keyboard key
     * @param onKeyRelease a boolean value. When true, specifies that
     *        the key is active when it is released.
     * @return a KeyStroke object for that key
     * @deprecated use getKeyStroke(char)
     */
    public static KeyStroke getKeyStroke(char keyChar,boolean onKeyRelease) {
        KeyStroke result = getCachedKeyCharKeyStroke(keyChar,onKeyRelease);

        if(result == null) {
            result = new KeyStroke();
            result.keyChar = keyChar;
            result.modifiers = 0;
            result.onKeyRelease = onKeyRelease;
            cacheKeyCharKeyStroke(result,onKeyRelease);
        }
        return result;
    }

    /**
     * Return a shared instance of a key stroke given a numeric keycode and a set
     * of modifiers, specifying whether the key is activated when it is pressed
     * or released.
     * <p>
     * The "virtual key" constants defined in java.awt.event.KeyEvent can be 
     * used to specify the key code. For example:<ul>
     * <li>java.awt.event.KeyEvent.VK_ENTER 
     * <li>java.awt.event.KeyEvent.VK_TAB
     * <li>java.awt.event.KeyEvent.VK_SPACE
     * </ul>
     * The modifiers consist of any combination of:<ul>
     * <li>java.awt.Event.SHIFT_MASK (1)
     * <li>java.awt.Event.CTRL_MASK (2)
     * <li>java.awt.Event.META_MASK (4)
     * <li>java.awt.Event.ALT_MASK (8)
     * </ul>
     * Since these numbers are all different powers of two, any combination of
     * them is an integer in which each bit represents a different
     * modifier key.
     *
     * @param keyCode an int specifying the numeric code for a keyboard key
     * @param modifiers an int specifying any combination of the key modifiers.
     * @param onKeyRelease a boolean value. When true, specifies that
     *        the key is active when it is released.
     * @return a KeyStroke object for that key
     *
     * @see java.awt.event.KeyEvent
     * @see java.awt.Event
     */
    public static KeyStroke getKeyStroke(int keyCode,int modifiers,boolean onKeyRelease) {
        KeyStroke result = getCachedKeyStroke(keyCode,modifiers,onKeyRelease);

        if(result == null) {
            result = new KeyStroke();
            result.keyCode = keyCode;
            result.modifiers = modifiers;
            result.onKeyRelease = onKeyRelease;
            cacheKeyStroke(result);
        }

        return result;
    }

    /**
     * Return a shared instance of a key stroke given a char code and a set
     * of modifiers -- the key is activated when it is pressed.
     * <p>
     * <p>
     * The "virtual key" constants defined in java.awt.event.KeyEvent can be 
     * used to specify the key code. For example:<ul>
     * <li>java.awt.event.KeyEvent.VK_ENTER 
     * <li>java.awt.event.KeyEvent.VK_TAB
     * <li>java.awt.event.KeyEvent.VK_SPACE
     * </ul>
     * The modifiers consist of any combination of:<ul>
     * <li>java.awt.Event.SHIFT_MASK (1)
     * <li>java.awt.Event.CTRL_MASK (2)
     * <li>java.awt.Event.META_MASK (4)
     * <li>java.awt.Event.ALT_MASK (8)
     * </ul>
     * Since these numbers are all different powers of two, any combination of
     * them is an integer in which each bit represents a different
     * modifier key.
     *
     * @param keyCode an int specifying the numeric code for a keyboard key
     * @param modifiers an int specifying any combination of the key modifiers.
     * @return a KeyStroke object for that key
     * @see java.awt.event.KeyEvent
     */
    public static KeyStroke getKeyStroke(int keyCode,int modifiers) {
        return getKeyStroke(keyCode,modifiers,false);
    }

    /**
     * Return a keystroke from an event.
     * <p>
     * This method obtains the keyChar from a KeyTyped event,
     * and the keyCode from a KeyPressed or KeyReleased event,
     * so you the type of event doesn't matter.
     *
     * @param anEvent the KeyEvent to obtain the KeyStroke from
     * @return the KeyStroke that precipitated the event
     */
    public static KeyStroke getKeyStrokeForEvent(KeyEvent anEvent) {
        KeyStroke ks = null;
        switch(anEvent.getID()) {
        case KeyEvent.KEY_PRESSED:
            ks = getKeyStroke(anEvent.getKeyCode(),anEvent.getModifiers(),false);
            break;
        case KeyEvent.KEY_RELEASED:
            ks = getKeyStroke(anEvent.getKeyCode(),anEvent.getModifiers(),true);
            break;
        case KeyEvent.KEY_TYPED:
            ks = getKeyStroke(anEvent.getKeyChar());
            break;
        }
        return ks;
    }

    /**
     * Parse a string with the following syntax and return an a KeyStroke:
     * <pre>
     *    "&lt;modifiers&gt;* &lt;key&gt;"
     *    modifiers := shift | control | meta | alt | button1 | button2 | button3
     *    key := KeyEvent keycode name, i.e. the name following "VK_".
     * </pre>
     * Here are some examples:
     * <pre>
     *     "INSERT" => new KeyStroke(0, KeyEvent.VK_INSERT);
     *     "control DELETE" => new KeyStroke(InputEvent.CTRL_MASK, KeyEvent.VK_DELETE);
     *     "alt shift X" => new KeyStroke(InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_X);
     * </pre>
     */
    public static KeyStroke getKeyStroke(String s) {
	StringTokenizer st = new StringTokenizer(s);
	String token;
	int mask = 0;

	while((token = st.nextToken()) != null) {
	    int tokenMask = 0;

	    /* if token matches a modifier keyword update mask and continue */

	    for(int i = 0; (tokenMask == 0) && (i < modifierKeywords.length); i++) {
		tokenMask = modifierKeywords[i].getModifierMask(token);
	    }

	    if (tokenMask != 0) {
		mask |= tokenMask;
		continue;
	    }

	    /* otherwise token is the keycode name less the "VK_" prefix */

	    String keycodeName = versionMap("VK_" + token);
	    int keycode;	    
	    try {
		keycode = KeyEvent.class.getField(keycodeName).getInt(KeyEvent.class);
	    }
	    catch (Exception e) {
		e.printStackTrace();
		throw new Error("Unrecognized keycode name: " + keycodeName);
	    }

	    return KeyStroke.getKeyStroke(keycode, mask);
	}

	throw new Error("Can't parse KeyStroke: \"" + s + "\"");
    }

    private static String versionMap(String s) {
	if (!SwingUtilities.is1dot2) {
	    if (s.equals("VK_KP_UP")) {
		s = "VK_UP";
	    }
	    if (s.equals("VK_KP_DOWN")) {
		s = "VK_DOWN";
	    }
	    if (s.equals("VK_KP_LEFT")) {
		s = "VK_LEFT";
	    }
	    if (s.equals("VK_KP_RIGHT")) {
		s = "VK_RIGHT";
	    }
	}

	return s;
    }

    /*
     * // see getKeyStroke (String)
     */
    private static class ModifierKeyword {
	final String keyword;
	final int mask;
	ModifierKeyword(String keyword, int mask) {
	    this.keyword = keyword;
	    this.mask = mask;
	}
	int getModifierMask(String s) {
	    return (s.equals(keyword)) ? mask : 0;
	}
    };


    /*
     * // see getKeyStroke (String)
     */
    private static ModifierKeyword[] modifierKeywords = {
	new ModifierKeyword("shift", InputEvent.SHIFT_MASK),
	new ModifierKeyword("control", InputEvent.CTRL_MASK),
	new ModifierKeyword("ctrl", InputEvent.CTRL_MASK),
	new ModifierKeyword("meta", InputEvent.META_MASK),
	new ModifierKeyword("alt", InputEvent.ALT_MASK),
	new ModifierKeyword("button1", InputEvent.BUTTON1_MASK),
	new ModifierKeyword("button2", InputEvent.BUTTON2_MASK),
	new ModifierKeyword("button3", InputEvent.BUTTON3_MASK)
    };

    /**
     * Returns the character defined by this KeyStroke object.
     * @return a char value
     * @see #getKeyStroke(char)
     */
    public char getKeyChar() { return keyChar; }
    /**
     * Returns the numeric keycode defined by this KeyStroke object.
     * @return an int containing the keycode value
     * @see #getKeyStroke(int,int)
     */
    public int  getKeyCode() { return keyCode; }
    /**
     * Returns the modifier keys defined by this KeyStroke object.
     * @return an int containing the modifiers
     * @see #getKeyStroke(int,int)
     */
    public int getModifiers() { return modifiers; }
    /**
     * Returns true if this keystroke is active on key release.
     * @return true if active on key release, false if active on key press
     * @see #getKeyStroke(int,int,boolean)
     */
    public boolean isOnKeyRelease() { return onKeyRelease; }

    private static String getStringRepresentation(char keyChar,int modifiers,boolean kr) {
        return "keyChar " + KeyEvent.getKeyModifiersText(modifiers) + keyChar +  
            (kr?"-R":"-P");
    }

    private static String getStringRepresentation(int keyCode,int modifiers,boolean kr) {
        return "keyCode " + KeyEvent.getKeyModifiersText(modifiers) + KeyEvent.getKeyText(keyCode) +
            (kr?"-R":"-P");
    }

    /**
     * Returns a numeric value for this object that is likely to be
     * reasonably unique, so it can be used as the index value in a
     * Hashtable.
     *
     * @return an int that "represents" this object
     * @see java.util.Hashtable
     */
    public int hashCode() {
        return (((int) keyChar) + 1) * (2 * (keyCode + 1)) * (modifiers+1) +
            (onKeyRelease ? 1 : 2);
    }

    /**
     * Returns true if this object is identical to the specified object.
     *
     * @param anObject the Object to compare this object to
     * @return true if the objects are identical
     */
    public boolean equals(Object anObject) {
        if(anObject instanceof KeyStroke) {
            KeyStroke ks = (KeyStroke) anObject;
            if(ks.keyChar == keyChar && ks.keyCode == keyCode && 
               ks.onKeyRelease == onKeyRelease && ks.modifiers == modifiers)
                return true;
        }
        return false;
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        if(keyChar == 0)
            return getStringRepresentation(keyCode,modifiers,onKeyRelease);
        else
            return getStringRepresentation(keyChar,0,onKeyRelease);
    }
}


