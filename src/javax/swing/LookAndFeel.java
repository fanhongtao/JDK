/*
 * @(#)LookAndFeel.java	1.19 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import java.net.URL;
import java.io.*;

import java.util.StringTokenizer;


/**
 * Completely characterizes a look and feel from the point of view
 * of the pluggable look and feel components.  
 * 
 * @version 1.19 11/29/01
 * @author Tom Ball
 * @author Hans Muller
 */
public abstract class LookAndFeel 
{

    /**
     * Convenience method for initializing a component's foreground
     * and background color properties with values from the current
     * defaults table.  The properties are only set if the current
     * value is either null or a UIResource.
     * 
     * @param c the target component for installing default color/font properties
     * @param defaultBgName the key for the default background
     * @param defaultFgName the key for the default foreground
     * 
     * @see #installColorsAndFont
     * @see UIManager#getColor
     */
    public static void installColors(JComponent c,
				     String defaultBgName,
                                     String defaultFgName)
    {
        Color bg = c.getBackground();
	if (bg == null || bg instanceof UIResource) {
	    c.setBackground(UIManager.getColor(defaultBgName));
	}

        Color fg = c.getForeground();
	if (fg == null || fg instanceof UIResource) {
	    c.setForeground(UIManager.getColor(defaultFgName));
	} 
    }


    /**
     * Convenience method for initializing a components foreground
     * background and font properties with values from the current
     * defaults table.  The properties are only set if the current
     * value is either null or a UIResource.
     * 
     * @param c the target component for installing default color/font properties
     * @param defaultBgName the key for the default background
     * @param defaultFgName the key for the default foreground
     * @param defaultFontName the key for the default font
     * 
     * @see #installColors
     * @see UIManager#getColor
     * @see UIManager#getFont
     */
    public static void installColorsAndFont(JComponent c,
                                         String defaultBgName,
                                         String defaultFgName,
                                         String defaultFontName) {
        Font f = c.getFont();
	if (f == null || f instanceof UIResource) {
	    c.setFont(UIManager.getFont(defaultFontName));
	}

	installColors(c, defaultBgName, defaultFgName);
    }


    /**
     * Convenience method for installing a component's default Border 
     * object on the specified component if either the border is 
     * currently null or already an instance of UIResource.
     * @param c the target component for installing default border
     * @param defaultBorderName the key specifying the default border
     */
    public static void installBorder(JComponent c, String defaultBorderName) {
        Border b = c.getBorder();
        if (b == null || b instanceof UIResource) {
            c.setBorder(UIManager.getBorder(defaultBorderName));
        }
    }


    /**
     * Convenience method for un-installing a component's default 
     * border on the specified component if the border is 
     * currently an instance of UIResource.
     * @param c the target component for uninstalling default border
     */
    public static void uninstallBorder(JComponent c) {
        if (c.getBorder() instanceof UIResource) {
            c.setBorder(null);
        }
    }


    /*
     * // see parseKeyStroke (private)
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
     * // see parseKeyStroke (private)
     */
    private static ModifierKeyword[] modifierKeywords = {
	new ModifierKeyword("shift", InputEvent.SHIFT_MASK),
	new ModifierKeyword("control", InputEvent.CTRL_MASK),
	new ModifierKeyword("meta", InputEvent.META_MASK),
	new ModifierKeyword("alt", InputEvent.ALT_MASK),
	new ModifierKeyword("button1", InputEvent.BUTTON1_MASK),
	new ModifierKeyword("button2", InputEvent.BUTTON2_MASK),
	new ModifierKeyword("button3", InputEvent.BUTTON3_MASK)
    };


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
     *     "alt shift X" => new KeyStroke(InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_X);
     * </pre>
     */
    private static KeyStroke parseKeyStroke(String s) 
    {
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

	    String keycodeName = "VK_" + token; 
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


    /**
     * Convenience method for building lists of KeyBindings.
     * <p>
     * Return an array of KeyBindings, one for each KeyStroke,Action pair
     * in <b>keyBindingList</b>.  A KeyStroke can either be a string in
     * the format specified by the private <code>parseKeyStroke</code> 
     * method or a KeyStroke object.
     * <p>
     * Actions are strings.  Here's an example:
     * <pre>
     * 	JTextComponent.KeyBinding[] multilineBindings = makeKeyBindings( new Object[] {
     *          "UP", DefaultEditorKit.upAction,
     *        "DOWN", DefaultEditorKit.downAction,
     *     "PAGE_UP", DefaultEditorKit.pageUpAction,
     *   "PAGE_DOWN", DefaultEditorKit.pageDownAction,
     *       "ENTER", DefaultEditorKit.insertBreakAction,
     *         "TAB", DefaultEditorKit.insertTabAction
     *  });
     * </pre>
     *
     * @param keyBindingList an array of KeyStroke,Action pairs
     * @return an array of KeyBindings
     */
    public static JTextComponent.KeyBinding[] makeKeyBindings(Object[] keyBindingList) 
    {
	JTextComponent.KeyBinding[] rv = new JTextComponent.KeyBinding[keyBindingList.length / 2];

	for(int i = 0; i < keyBindingList.length; i += 2) {
	    KeyStroke keystroke = (keyBindingList[i] instanceof KeyStroke)
		? (KeyStroke)keyBindingList[i]
		: parseKeyStroke((String)keyBindingList[i]);
	    String action = (String)keyBindingList[i+1];
	    rv[i / 2] = new JTextComponent.KeyBinding(keystroke, action);
	}

	return rv;
    }


    /**
     * Utility method that creates a UIDefaults.LazyValue that creates
     * an ImageIcon UIResource for the specified <code>gifFile</code>
     * filename.
     */
    public static Object makeIcon(final Class baseClass, final String gifFile) {
	return new UIDefaults.LazyValue() {
	    public Object createValue(UIDefaults table) {
		/* Copy resource into a byte array.  This is
		 * necessary because several browsers consider
		 * Class.getResource a security risk because it
		 * can be used to load additional classes.
		 * Class.getResourceAsStream just returns raw
		 * bytes, which we can convert to an image.
		 */
                final byte[][] buffer = new byte[1][];
		SwingUtilities.doPrivileged(new Runnable() {
		    public void run() {
			try {
			    InputStream resource = 
				baseClass.getResourceAsStream(gifFile);
			    if (resource == null) {
				return; 
			    }
			    BufferedInputStream in = 
				new BufferedInputStream(resource);
			    ByteArrayOutputStream out = 
				new ByteArrayOutputStream(1024);
			    buffer[0] = new byte[1024];
			    int n;
			    while ((n = in.read(buffer[0])) > 0) {
				out.write(buffer[0], 0, n);
			    }
			    in.close();
			    out.flush();
			    buffer[0] = out.toByteArray();
			} catch (IOException ioe) {
			    System.err.println(ioe.toString());
			    return;
			}
		    }
		});

		if (buffer[0] == null) {
		    System.err.println(baseClass.getName() + "/" + 
				       gifFile + " not found.");
		    return null;
		}
		if (buffer[0].length == 0) {
		    System.err.println("warning: " + gifFile + 
				       " is zero-length");
		    return null;
		}

                return new IconUIResource(new ImageIcon(buffer[0]));
	    }
	};
    }

    /**
     * Return a short string that identifies this look and feel, e.g.
     * "CDE/Motif".  This string should be appropriate for a menu item.
     * Distinct look and feels should have different names, e.g. 
     * a subclass of MotifLookAndFeel that changes the way a few components
     * are rendered should be called "CDE/Motif My Way"; something
     * that would be useful to a user trying to select a L&F from a list
     * of names.
     */
    public abstract String getName();


    /**
     * Return a string that identifies this look and feel.  This string 
     * will be used by applications/services that want to recognize
     * well known look and feel implementations.  Presently
     * the well known names are "Motif", "Windows", "Mac", "Metal".  Note 
     * that a LookAndFeel derived from a well known superclass 
     * that doesn't make any fundamental changes to the look or feel 
     * shouldn't override this method.
     */
    public abstract String getID();


    /** 
     * Return a one line description of this look and feel implementation, 
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for 
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    public abstract String getDescription();


    /**
     * If the underlying platform has a "native" look and feel, and this
     * is an implementation of it, return true.  For example a CDE/Motif
     * look and implementation would return true when the underlying 
     * platform was Solaris.
     */
    public abstract boolean isNativeLookAndFeel();


    /**
     * Return true if the underlying platform supports and or permits
     * this look and feel.  This method returns false if the look 
     * and feel depends on special resources or legal agreements that
     * aren't defined for the current platform.  
     * 
     * @see UIManager#setLookAndFeel
     */
    public abstract boolean isSupportedLookAndFeel();


    /**
     * UIManager.setLookAndFeel calls this method before the first
     * call (and typically the only call) to getDefaults().  Subclasses
     * should do any one-time setup they need here, rather than 
     * in a static initializer, because look and feel class objects
     * may be loaded just to discover that isSupportedLookAndFeel()
     * returns false.
     *
     * @see #uninitialize
     * @see UIManager#setLookAndFeel
     */
    public void initialize() {
    }


    /**
     * UIManager.setLookAndFeel calls this method just before we're
     * replaced by a new default look and feel.   Subclasses may 
     * choose to free up some resources here.
     *
     * @see #initialize
     */
    public void uninitialize() {
    }

    /**
     * This method is called once by UIManager.setLookAndFeel to create
     * the look and feel specific defaults table.  Other applications,
     * for example an application builder, may also call this method.
     *
     * @see #initialize
     * @see #uninitialize
     * @see UIManager#setLookAndFeel
     */
    public UIDefaults getDefaults() {
        return null;
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
	return "[" + getDescription() + " - " + getClass().getName() + "]";
    }
}
