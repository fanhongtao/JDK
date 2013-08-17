/*
 * @(#)MenuShortcut.java	1.10 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;

import java.awt.event.KeyEvent;

/**
 * A class which represents a keyboard accelerator for a MenuItem.
 *
 * @version 1.10, 07/01/98
 * @author Thomas Ball
 */
public class MenuShortcut implements java.io.Serializable 
{

    int key;
    boolean usesShift;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 143448358473180225L;

    /**
     * Constructs a new MenuShortcut for the specified key.
     * @param key the raw keycode for this MenuShortcut, as would be returned
     * in the keyCode field of a KeyEvent if this key were pressed.
     **/
    public MenuShortcut(int key) {
        this(key, false);
    }

    /**
     * Constructs a new MenuShortcut for the specified key.
     * @param key the raw keycode for this MenuShortcut, as would be returned
     * in the keyCode field of a KeyEvent if this key were pressed.
     * @param useShiftModifier indicates whether this MenuShortcut is invoked
     * with the SHIFT key down.
     **/
    public MenuShortcut(int key, boolean useShiftModifier) {
        // Convenience conversion for programmers who confuse key posts with
        // ASCII characters -- do not internationalize!  They *should* be
        // using KeyEvent virtual keys, such as VK_A.
        if (key >= 'a' && key <= 'z') {
            key = (int)Character.toUpperCase((char)key);
        }
        this.key = key;
        this.usesShift = useShiftModifier;
    }

    /**
     * Return the raw keycode of this MenuShortcut.
     */
    public int getKey() {
        return key;
    }

    /**
     * Return whether this MenuShortcut must be invoked using the SHIFT key.
     */
    public boolean usesShiftModifier() {
        return usesShift;
    }

    /**
     * Returns whether this MenuShortcut is the same as another:
     * equality is defined to mean that both MenuShortcuts use the same key
     * and both either use or don't use the SHIFT key.
     * @param s the MenuShortcut to compare with this.
     */
    public boolean equals(MenuShortcut s) {
	return (s != null && (s.getKey() == key) && 
                (s.usesShiftModifier() == usesShift));
    }

    /**
     * Returns an internationalized description of the MenuShortcut.
     */
    public String toString() {
        int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (usesShiftModifier()) {
            modifiers |= Event.SHIFT_MASK;
        }
	return KeyEvent.getKeyModifiersText(modifiers) + "+" + 
               KeyEvent.getKeyText(key);
    }

    protected String paramString() {
        String str = "key=" + key;
	if (usesShiftModifier()) {
	    str += ",usesShiftModifier";
	}
	return str;
    }
}
