/*
 * @(#)GraphicsEnvironment.java	1.55 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java.awt;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.io.InputStream;
import sun.java2d.HeadlessGraphicsEnvironment;
import sun.java2d.SunGraphicsEnvironment;

/**
 *
 * The <code>GraphicsEnvironment</code> class describes the collection
 * of {@link GraphicsDevice} objects and {@link java.awt.Font} objects
 * available to a Java(tm) application on a particular platform.
 * The resources in this <code>GraphicsEnvironment</code> might be local
 * or on a remote machine.  <code>GraphicsDevice</code> objects can be
 * screens, printers or image buffers and are the destination of
 * {@link Graphics2D} drawing methods.  Each <code>GraphicsDevice</code>
 * has a number of {@link GraphicsConfiguration} objects associated with
 * it.  These objects specify the different configurations in which the
 * <code>GraphicsDevice</code> can be used.  
 * @see GraphicsDevice
 * @see GraphicsConfiguration
 * @version 	1.55, 01/23/03
 */

public abstract class GraphicsEnvironment {
    private static GraphicsEnvironment localEnv;

    /**
     * The headless state of the Toolkit and GraphicsEnvironment
     */
    private static Boolean headless;

    /**
     * This is an abstract class and cannot be instantiated directly.
     * Instances must be obtained from a suitable factory or query method.
     */
    protected GraphicsEnvironment() {
    }

    /**
     * Returns the local <code>GraphicsEnvironment</code>.
     * @return the local <code>GraphicsEnvironment</code>
     */
    public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
	if (localEnv == null) {
	    String nm = (String) java.security.AccessController.doPrivileged
		(new sun.security.action.GetPropertyAction
		 ("java.awt.graphicsenv", null));

	    try {
		localEnv =
		    (GraphicsEnvironment) Class.forName(nm).newInstance();
                if (isHeadless()) {
                    localEnv = new HeadlessGraphicsEnvironment(localEnv);
                }
	    } catch (ClassNotFoundException e) {
                throw new Error("Could not find class: "+nm);
            } catch (InstantiationException e) {
                throw new Error("Could not instantiate Graphics Environment: "
				+ nm);
            } catch (IllegalAccessException e) {
                throw new Error ("Could not access Graphics Environment: "
				 + nm);
            }
        }

	return localEnv;
    }

    /**
     * Tests whether or not a display, keyboard, and mouse can be
     * supported in this environment.  If this method returns true,
     * a HeadlessException is thrown from areas of the Toolkit
     * and GraphicsEnvironment that are dependent on a display,
     * keyboard, or mouse.
     * @return <code>true</code> if this environment cannot support 
     * a display, keyboard, and mouse; <code>false</code> 
     * otherwise
     * @see java.awt.HeadlessException
     * @since 1.4
     */
    public static boolean isHeadless() {
        return getHeadlessProperty();
    }

    /**
     * @return the value of the property "java.awt.headless"
     * @since 1.4
     */
    private static boolean getHeadlessProperty() {
        if (headless == null) {
            String nm = (String)java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(
                    "java.awt.headless", "false"));
            if (nm.equals("true")) {
                headless = Boolean.TRUE;
            } else {
                headless = Boolean.FALSE;
            }
        }
        return headless.booleanValue();
    }

    /**
     * Check for headless state and throw HeadlessException if headless
     * @since 1.4
     */
    static void checkHeadless() throws HeadlessException {
        if (isHeadless()) {
            throw new HeadlessException();
        }
    }

    /**
     * Returns whether or not a display, keyboard, and mouse can be 
     * supported in this graphics environment.  If this returns true,
     * <code>HeadlessException</code> will be thrown from areas of the
     * graphics environment that are dependent on a display, keyboard, or
     * mouse.
     * @return <code>true</code> if a display, keyboard, and mouse 
     * can be supported in this environment; <code>false</code>
     * otherwise
     * @see java.awt.HeadlessException
     * @see #isHeadless
     * @since 1.4
     */
    public boolean isHeadlessInstance() {
        // By default (local graphics environment), simply check the
        // headless property.
        return getHeadlessProperty();
    }

    /**
     * Returns an array of all of the screen <code>GraphicsDevice</code>
     * objects.
     * @return an array containing all the <code>GraphicsDevice</code>
     * objects that represent screen devices
     * @exception HeadlessException if isHeadless() returns true
     * @see #isHeadless()
     */
    public abstract GraphicsDevice[] getScreenDevices()
        throws HeadlessException;

    /**
     * Returns the default screen <code>GraphicsDevice</code>.
     * @return the <code>GraphicsDevice</code> that represents the
     * default screen device
     * @exception HeadlessException if isHeadless() returns true
     * @see #isHeadless()
     */
    public abstract GraphicsDevice getDefaultScreenDevice()
        throws HeadlessException;

    /**
     * Returns a <code>Graphics2D</code> object for rendering into the
     * specified {@link BufferedImage}.
     * @param img the specified <code>BufferedImage</code>
     * @return a <code>Graphics2D</code> to be used for rendering into
     * the specified <code>BufferedImage</code>
     */
    public abstract Graphics2D createGraphics(BufferedImage img);

    /**
     * Returns an array containing a one-point size instance of all fonts
     * available in this <code>GraphicsEnvironment</code>.  Typical usage
     * would be to allow a user to select a particular font.  Then, the
     * application can size the font and set various font attributes by
     * calling the <code>deriveFont</code> method on the choosen instance.
     * <p>
     * This method provides for the application the most precise control
     * over which <code>Font</code> instance is used to render text.
     * If a font in this <code>GraphicsEnvironment</code> has multiple
     * programmable variations, only one
     * instance of that <code>Font</code> is returned in the array, and
     * other variations must be derived by the application.
     * <p>
     * If a font in this environment has multiple programmable variations,
     * such as Multiple-Master fonts, only one instance of that font is
     * returned in the <code>Font</code> array.  The other variations
     * must be derived by the application.
     *
     * @return an array of <code>Font</code> objects
     * @see #getAvailableFontFamilyNames
     * @see java.awt.Font
     * @see java.awt.Font#deriveFont
     * @see java.awt.Font#getFontName
     * @since 1.2
     */
    public abstract Font[] getAllFonts();

    /**
     * Returns an array containing the names of all font families available
     * in this <code>GraphicsEnvironment</code>.
     * Typical usage would be to allow a user to select a particular family
     * name and allow the application to choose related variants of the
     * same family when the user specifies style attributes such
     * as Bold or Italic.
     * <p>
     * This method provides for the application some control over which
     * <code>Font</code> instance is used to render text, but allows the 
     * <code>Font</code> object more flexibility in choosing its own best
     * match among multiple fonts in the same font family.
     *
     * @return an array of <code>String</code> containing names of font
     * families
     * @see #getAllFonts
     * @see java.awt.Font
     * @see java.awt.Font#getFamily
     * @since 1.2
     */
    public abstract String[] getAvailableFontFamilyNames();

    /**
     * Returns an array containing the localized names of all font families
     * available in this <code>GraphicsEnvironment</code>.
     * Typical usage would be to allow a user to select a particular family
     * name and allow the application to choose related variants of the
     * same family when the user specifies style attributes such
     * as Bold or Italic.
     * <p>
     * This method provides for the application some control over which
     * <code>Font</code> instance used to render text, but allows the 
     * <code>Font</code> object more flexibility in choosing its own best
     * match among multiple fonts in the same font family.
     * If <code>l</code> is <code>null</code>, this method returns an 
     * array containing all font family names available in this
     * <code>GraphicsEnvironment</code>.
     *
     * @param l a {@link Locale} object that represents a
     * particular geographical, political, or cultural region
     * @return an array of <code>String</code> objects containing names of
     * font families specific to the specified <code>Locale</code>
     * @see #getAllFonts
     * @see java.awt.Font
     * @see java.awt.Font#getFamily
     * @since 1.2
     */
    public abstract String[] getAvailableFontFamilyNames(Locale l);

    /**
     * Returns the Point where Windows should be centered.
     * It is recommended that centered Windows be checked to ensure they fit
     * within the available display area using getMaximumWindowBounds().
     * @return the point where Windows should be centered
     *
     * @exception HeadlessException if isHeadless() returns true
     * @see #getMaximumWindowBounds
     * @since 1.4
     */
    public Point getCenterPoint() throws HeadlessException {
    // Default implementation: return the center of the usable bounds of the
    // default screen device.
        Rectangle usableBounds = 
         SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice());
        return new Point((usableBounds.width / 2) + usableBounds.x,
                         (usableBounds.height / 2) + usableBounds.y);    
    }

    /**
     * Returns the maximum bounds for centered Windows.
     * These bounds account for objects in the native windowing system such as
     * task bars and menu bars.  The returned bounds will reside on a single
     * display with one exception: on multi-screen systems where Windows should
     * be centered across all displays, this method returns the bounds of the
     * entire display area.
     * <p>
     * To get the usable bounds of a single display, use 
     * <code>GraphicsConfiguration.getBounds()</code> and
     * <code>Toolkit.getScreenInsets()</code>.
     * @return  the maximum bounds for centered Windows
     *
     * @exception HeadlessException if isHeadless() returns true
     * @see #getCenterPoint
     * @see GraphicsConfiguration#getBounds
     * @see Toolkit#getScreenInsets
     * @since 1.4
     */
    public Rectangle getMaximumWindowBounds() throws HeadlessException {
    // Default implementation: return the usable bounds of the default screen
    // device.  This is correct for Microsoft Windows and non-Xinerama X11.
        return SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice());
    }
}

