/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.event.KeyEvent;

/**
 * A class which represents a keyboard accelerator for a MenuItem.
 *
 * @version 1.19, 02/06/02
 * @author Thomas Ball
 */
public class MenuShortcut implements java.io.Serializable
{
    /**
     * This is indicates the virtual keycode for the menu shortcut.
     * It is the key code with which the menu short cut will be created.
     * In 1.1.2 you must use setActionCommand() on a menu item
     * in order for its shortcut to work, otherwise it will fire a null
     * action command.
     * Must use KeyEvent virtual keys - eg : VK_A.
     *
     * @serial
     * @see getKey()
     * @see usesShiftModifier()
     */
    int key;
    /**
     * Indicates whether the shft key was pressed.
     * If true, the shift key was pressed.
     * If false, the shift key was not pressed
     *
     * @serial
     * @see usesShiftModifier()
     */
    boolean usesShift;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = 143448358473180225L;

    /**
     * Constructs a new MenuShortcut for the specified key.
     * @param key the raw keycode for this MenuShortcut, as would be returned
     * in the keyCode field of a {@link java.awt.event.KeyEvent KeyEvent} if 
     * this key were pressed.
     **/
    public MenuShortcut(int key) {
        this(key, false);
    }

    /**
     * Constructs a new MenuShortcut for the specified key.
     * @param key the raw keycode for this MenuShortcut, as would be returned
     * in the keyCode field of a {@link java.awt.event.KeyEvent KeyEvent} if 
     * this key were pressed.
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
     * Returns whether this MenuShortcut is the same as another:
     * equality is defined to mean that both MenuShortcuts use the same key
     * and both either use or don't use the SHIFT key.
     * @param obj the Object to compare with this.
     */
    public boolean equals(Object obj) {
        if (obj instanceof MenuShortcut) {
            return equals( (MenuShortcut) obj );
        }
        return false;
    }

    /**
     * Returns the hashcode for this MenuShortcut.
     */
    public int hashCode() {
        return (usesShift) ? (~key) : key;
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
