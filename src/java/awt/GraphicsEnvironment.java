/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java.awt;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.io.InputStream;

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
 * @version 	1.43, 02/06/02
 */

public abstract class GraphicsEnvironment {
    private static GraphicsEnvironment localEnv;

    /**
     * This is an abstract class and cannot be instantiated directly.
     * Instances must be obtained from a suitable factory or query method.
     */
    protected GraphicsEnvironment() {
    }

    /**
     * Returns the local <code>GraphicsEnvironment</code>.
     * @return this <code>GraphicsEnvironment</code>.
     */
    public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
	if (localEnv == null) {
	    String nm = (String) java.security.AccessController.doPrivileged
		(new sun.security.action.GetPropertyAction
		 ("java.awt.graphicsenv", null));

	    try {
		localEnv =
		    (GraphicsEnvironment) Class.forName(nm).newInstance();
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
     * Returns an array of all of the screen <code>GraphicsDevice</code>
     * objects.
     * @return an array containing all the <code>GraphicsDevice</code>
     * objects that represent screen devices.
     */
    public abstract GraphicsDevice[] getScreenDevices();

    /**
     * Returns the default screen <code>GraphicsDevice</code>.
     * @return the <code>GraphicsDevice</code> that represents the
     * default screen device.
     */
    public abstract GraphicsDevice getDefaultScreenDevice();

    /**
     * Returns a <code>Graphics2D</code> object for rendering into the
     * specified {@link BufferedImage}.
     * @param img the specified <code>BufferedImage</code>
     * @return a <code>Graphics2D</code> to be used for rendering into
     * the specified <code>BufferedImage</code>.
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
     * @return an array of <code>Font</code> objects.
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
     * @return an array of <code>String</code> containing names of font
     * families.
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
     * @param l a {@link Locale} object that represents a
     * particular geographical, political, or cultural region
     * @return an array of <code>String</code> objects containing names of
     * font families specific to the specified <code>Locale</code>.
     * @see #getAllFonts
     * @see java.awt.Font
     * @see java.awt.Font#getFamily
     * @since 1.2
     */
    public abstract String[] getAvailableFontFamilyNames(Locale l);

}
