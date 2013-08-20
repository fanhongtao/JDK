/*
 * @(#)LookAndFeel.java	1.38 05/01/04
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import java.net.URL;
import com.sun.java.swing.SwingUtilities2;

import java.util.StringTokenizer;


/**
 * Completely characterizes a look and feel from the point of view
 * of the pluggable look and feel components.  
 * 
 * @version 1.38 01/04/05
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

    /**
     * Convenience method for installing a property with the specified name
     * and value on a component if that property has not already been set
     * by the client program.  This method is intended to be used by
     * UI delegate instances that need to specify a default value for a
     * property of primitive type (boolean, int, ..), but do not wish
     * to override a value set by the client.  Since primitive property
     * values cannot be wrapped with the UIResource marker, this method
     * uses private state to determine whether the property has been set
     * by the client.
     * @throws IllegalArgumentException if the specified property is not
     *         one which can be set using this method
     * @throws ClassCastException may be thrown if the property value
     *         specified does not match the property's type
     * @throws NullPointerException may be thrown if c or propertyValue is null
     * @param c the target component for installing the property
     * @param propertyName String containing the name of the property to be set
     * @param propertyValue Object containing the value of the property
     */
    public static void installProperty(JComponent c,
				       String propertyName, Object propertyValue) {
        c.setUIProperty(propertyName, propertyValue);
    }

    /**
     * Convenience method for building lists of KeyBindings.
     * <p>
     * Return an array of KeyBindings, one for each KeyStroke,Action pair
     * in <b>keyBindingList</b>.  A KeyStroke can either be a string in
     * the format specified by the <code>KeyStroke.getKeyStroke</code> 
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
		: KeyStroke.getKeyStroke((String)keyBindingList[i]);
	    String action = (String)keyBindingList[i+1];
	    rv[i / 2] = new JTextComponent.KeyBinding(keystroke, action);
	}

	return rv;
    }

    /**
     * Creates a InputMap from <code>keys</code>. <code>keys</code>
     * describes the InputMap, with every even number item being a String
     * giving the KeyStroke as speced in
     * <code>KeyStroke.getKeyStroke(String)</code>
     * (or a KeyStroke), and every odd number item the Object
     * used to determine the associated Action in an ActionMap.
     *
     * @since 1.3
     */
    public static InputMap makeInputMap(Object[] keys) {
	InputMap retMap = new InputMapUIResource();
	loadKeyBindings(retMap, keys);
	return retMap;
    }

    /**
     * Creates a ComponentInputMap from <code>keys</code>. <code>keys</code>
     * describes the InputMap, with every even number item being a String
     * giving
     * the KeyStroke as speced in <code>KeyStroke.getKeyStroke(String)</code>
     * (or a KeyStroke), and every odd number item the Object
     * used to determine the associated Action in an ActionMap.
     *
     * @since 1.3
     */
    public static ComponentInputMap makeComponentInputMap(JComponent c,
							  Object[] keys) {
	ComponentInputMap retMap = new ComponentInputMapUIResource(c);
	loadKeyBindings(retMap, keys);
	return retMap;
    }


    /**
     * Loads the bindings in <code>keys</code> into <code>retMap</code>.
     * This does not remove any existing bindings in <code>retMap</code>.
     * <code>keys</code>
     * describes the InputMap, with every even number item being a String
     * giving
     * the KeyStroke as speced in <code>KeyStroke.getKeyStroke(String)</code>
     * (or a KeyStroke), and every odd number item the Object
     * used to determine the associated Action in an ActionMap.
     *
     * @since 1.3
     */
    public static void loadKeyBindings(InputMap retMap, Object[] keys) {
	if (keys != null) {
	    for (int counter = 0, maxCounter = keys.length;
		 counter < maxCounter; counter++) {
		Object keyStrokeO = keys[counter++];
		KeyStroke ks = (keyStrokeO instanceof KeyStroke) ?
		                (KeyStroke)keyStrokeO :
		                KeyStroke.getKeyStroke((String)keyStrokeO);
		retMap.put(ks, keys[counter]);
	    }
	}
    }

    /**
     * Utility method that creates a UIDefaults.LazyValue that creates
     * an ImageIcon UIResource for the specified <code>gifFile</code>
     * filename.
     */
    public static Object makeIcon(final Class<?> baseClass, final String gifFile) {
        return SwingUtilities2.makeIcon(baseClass, baseClass, gifFile);
    }

    /**
     * Invoked when the user attempts an invalid operation, 
     * such as pasting into an uneditable <code>JTextField</code> 
     * that has focus. The default implementation beeps. Subclasses 
     * that wish different behavior should override this and provide 
     * the additional feedback.
     *
     * @param component the <code>Component</code> the error occurred in,
     *                  may be <code>null</code>
     *			indicating the error condition is not directly 
     *			associated with a <code>Component</code>
     * @since 1.4
     */
    public void provideErrorFeedback(Component component) {
	Toolkit toolkit = null;
	if (component != null) {
	    toolkit = component.getToolkit();
	} else {
	    toolkit = Toolkit.getDefaultToolkit();
	}
	toolkit.beep();
    } // provideErrorFeedback()

    /**
     * Returns the value of the specified system desktop property by
     * invoking <code>Toolkit.getDefaultToolkit().getDesktopProperty()</code>.
     * If the current value of the specified property is null, the 
     * fallbackValue is returned.
     * @param systemPropertyName the name of the system desktop property being queried
     * @param fallbackValue the object to be returned as the value if the system value is null
     * @return the current value of the desktop property
     *
     * @see java.awt.Toolkit#getDesktopProperty
     *
     */
    public static Object getDesktopPropertyValue(String systemPropertyName, Object fallbackValue) {
	Object value = Toolkit.getDefaultToolkit().getDesktopProperty(systemPropertyName);
	if (value == null) {
	    return fallbackValue;
	} else if (value instanceof Color) {
	    return new ColorUIResource((Color)value);
	} else if (value instanceof Font) {
	    return new FontUIResource((Font)value);
	}
	return value;
    }

    /**
     * Returns an <code>Icon</code> with a disabled appearance.
     * This method is used to generate a disabled <code>Icon</code> when
     * one has not been specified.  For example, if you create a
     * <code>JButton</code> and only specify an <code>Icon</code> via
     * <code>setIcon</code> this method will be called to generate the
     * disabled <code>Icon</code>. If null is passed as <code>icon</code>
     * this method returns null. 
     * <p>
     * Some look and feels might not render the disabled Icon, in which
     * case they will ignore this.
     *
     * @param component JComponent that will display the Icon, may be null
     * @param icon Icon to generate disable icon from.
     * @return Disabled icon, or null if a suitable Icon can not be
     *         generated.
     * @since 1.5
     */
    public Icon getDisabledIcon(JComponent component, Icon icon) {
        if (icon instanceof ImageIcon) {
            return new IconUIResource(new ImageIcon(GrayFilter.
                   createDisabledImage(((ImageIcon)icon).getImage())));
        }
        return null;
    }                       

    /**
     * Returns an <code>Icon</code> for use by disabled
     * components that are also selected. This method is used to generate an
     * <code>Icon</code> for components that are in both the disabled and
     * selected states but do not have a specific <code>Icon</code> for this
     * state.  For example, if you create a <code>JButton</code> and only
     * specify an <code>Icon</code> via <code>setIcon</code> this method
     * will be called to generate the disabled and selected
     * <code>Icon</code>. If null is passed as <code>icon</code> this method
     * returns null. 
     * <p>
     * Some look and feels might not render the disabled and selected Icon,
     * in which case they will ignore this.
     *
     * @param component JComponent that will display the Icon, may be null
     * @param icon Icon to generate disabled and selected icon from.
     * @return Disabled and Selected icon, or null if a suitable Icon can not
     *         be generated.
     * @since 1.5
     */
    public Icon getDisabledSelectedIcon(JComponent component, Icon icon) {
        return getDisabledIcon(component, icon);
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
     * Returns true if the <code>LookAndFeel</code> returned
     * <code>RootPaneUI</code> instances support providing Window decorations
     * in a <code>JRootPane</code>.
     * <p>
     * The default implementation returns false, subclasses that support
     * Window decorations should override this and return true.
     *
     * @return True if the RootPaneUI instances created support client side
     *              decorations
     * @see JDialog#setDefaultLookAndFeelDecorated
     * @see JFrame#setDefaultLookAndFeelDecorated
     * @see JRootPane#setWindowDecorationStyle
     * @since 1.4
     */
    public boolean getSupportsWindowDecorations() {
        return false;
    }

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
