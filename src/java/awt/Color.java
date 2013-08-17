/*
 * @(#)Color.java	1.29 97/02/17
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.io.*;
import java.lang.*;

/**
 * A class to encapsulate RGB Colors.
 *
 * @version 	1.29, 02/17/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Color implements java.io.Serializable {
    
    /**
     * The color white.
     */
    public final static Color white 	= new Color(255, 255, 255);

    /**
     * The color light gray.
     */
    public final static Color lightGray = new Color(192, 192, 192);

    /**
     * The color gray.
     */
    public final static Color gray 	= new Color(128, 128, 128);

    /**
     * The color dark gray.
     */
    public final static Color darkGray 	= new Color(64, 64, 64);

    /**
     * The color black.
     */
    public final static Color black 	= new Color(0, 0, 0);
    
    /**
     * The color red.
     */
    public final static Color red 	= new Color(255, 0, 0);

    /**
     * The color pink.
     */
    public final static Color pink 	= new Color(255, 175, 175);

    /**
     * The color orange.
     */
    public final static Color orange 	= new Color(255, 200, 0);

    /**
     * The color yellow.
     */
    public final static Color yellow 	= new Color(255, 255, 0);

    /**
     * The color green.
     */
    public final static Color green 	= new Color(0, 255, 0);

    /**
     * The color magneta.
     */
    public final static Color magenta	= new Color(255, 0, 255);

    /**
     * The color cyan.
     */
    public final static Color cyan 	= new Color(0, 255, 255);

    /**
     * The color blue.
     */
    public final static Color blue 	= new Color(0, 0, 255);

    /**
     * Private data.
     */
    transient private int pData;

    /**
     * The color value.
     */
    int value;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 118526816881161077L;

    /**
     * Checks the color integer components supplied for validity.
     * Throws an IllegalArgumentException if the value is out of range.
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     **/
    private static void testColorValueRange(int r, int g, int b) {
        boolean rangeError = false;
	String badComponentString = "";
	if ( r < 0 || r > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Red";
	}
	if ( g < 0 || g > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Green";
	}
	if ( b < 0 || b > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Blue";
	}
	if ( rangeError == true ) {
	throw new IllegalArgumentException("Color parameter outside of expected range:"
					   + badComponentString);
	}
    }

    /**
     * Checks the color float components supplied for validity.
     * Throws an IllegalArgumentException if the value is out of range.
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     **/
    private static void testColorValueRange(float r, float g, float b) {
        boolean rangeError = false;
	String badComponentString = "";
	if ( r < 0.0 || r > 1.0) {
	    rangeError = true;
	    badComponentString = badComponentString + " Red";
	}
	if ( g < 0.0 || g > 1.0) {
	    rangeError = true;
	    badComponentString = badComponentString + " Green";
	}
	if ( b < 0.0 || b > 1.0) {
	    rangeError = true;
	    badComponentString = badComponentString + " Blue";
	}
	if ( rangeError == true ) {
	throw new IllegalArgumentException("Color parameter outside of expected range:"
					   + badComponentString);
	}
    }

    /**
     * Creates a color with the specified red, green, and blue values in
     * the range (0 - 255).  The actual color used in rendering will depend
     * on finding the best match given the color space available for a
     * given output device.
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public Color(int r, int g, int b) {
        this(((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0));
	testColorValueRange(r,g,b);
    }

    /**
     * Creates a color with the specified combined RGB value consisting of
     * the red component in bits 16-23, the green component in bits 8-15,
     * and the blue component in bits 0-7.  The actual color used in
     * rendering will depend on finding the best match given the color space
     * available for a given output device.
     * @param rgb the combined RGB components
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public Color(int rgb) {
      value = 0xff000000 | rgb;
    }

    /**
     * Creates a color with the specified red, green, and blue values in the
     * range (0.0 - 1.0). The actual color
     * used in rendering will depend on finding the best match given the
     * color space available for a given output device.
     * @param r the red component
     * @param g the red component
     * @param b the red component
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public Color(float r, float g, float b) {
      this( (int) (r * 255), (int) (g * 255), (int) (b * 255));
      testColorValueRange(r,g,b);
    }

    /**
     * Gets the red component.
     * @see #getRGB
     */
    public int getRed() {
	return (getRGB() >> 16) & 0xFF;
    }

    /**
     * Gets the green component.
     * @see #getRGB
     */
    public int getGreen() {
	return (getRGB() >> 8) & 0xFF;
    }

    /**
     * Gets the blue component.
     * @see #getRGB
     */
    public int getBlue() {
	return (getRGB() >> 0) & 0xFF;
    }

    /**
     * Gets the RGB value representing the color in the default RGB ColorModel.
     * (Bits 24-31 are 0xff, 16-23 are red, 8-15 are green, 0-7 are blue).
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     */
    public int getRGB() {
	return value;
    }

    private static final double FACTOR = 0.7;

    /**
     * Returns a brighter version of this color.
     */
    public Color brighter() {
	return new Color(Math.min((int)(getRed()  *(1/FACTOR)), 255), 
			 Math.min((int)(getGreen()*(1/FACTOR)), 255),
			 Math.min((int)(getBlue() *(1/FACTOR)), 255));
    }

    /**
     * Returns a darker version of this color.
     */
    public Color darker() {
	return new Color(Math.max((int)(getRed()  *FACTOR), 0), 
			 Math.max((int)(getGreen()*FACTOR), 0),
			 Math.max((int)(getBlue() *FACTOR), 0));
    }

    /**
     * Computes the hash code.
     */
    public int hashCode() {
	return value;
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to compare with.
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
        return obj instanceof Color && ((Color)obj).value == this.value;
    }

    /**
     * Returns the String representation of this Color's values.
     */
    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }

    /**
     * Gets the specified Color.
     * @param nm representation of the color as a 24-bit integer
     * @return the new color
     */
    public static Color decode(String nm) throws NumberFormatException {
	Integer intval = Integer.decode(nm);
	int i = intval.intValue();
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    /**
     * Gets the specified Color property.
     * @param nm the name of the color property
     */
    public static Color getColor(String nm) {
	return getColor(nm, null);
    }

    /**
     * Gets the specified Color property of the specified Color.
     * @param nm the name of the color property
     * @param v the specified color
     * @return the new color.
     */
    public static Color getColor(String nm, Color v) {
	Integer intval = Integer.getInteger(nm);
	if (intval == null) {
	    return v;
	}
	int i = intval.intValue();
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    /**
     * Gets the specified Color property of the color value.
     * @param nm the name of the color property
     * @param v the color value
     * @return the new color.
     */
    public static Color getColor(String nm, int v) {
	Integer intval = Integer.getInteger(nm);
	int i = (intval != null) ? intval.intValue() : v;
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, (i >> 0) & 0xFF);
    }

    /**
     * Returns the RGB value defined by the default RGB ColorModel, of
     * the color corresponding to the given HSB color components.
     * @param hue the hue component of the color
     * @param saturation the saturation of the color
     * @param brightness the brightness of the color
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRGB
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
	int r = 0, g = 0, b = 0;
    	if (saturation == 0) {
	    r = g = b = (int) (brightness * 255);
	} else {
	    double h = (hue - Math.floor(hue)) * 6.0;
	    double f = h - java.lang.Math.floor(h);
	    double p = brightness * (1.0 - saturation);
	    double q = brightness * (1.0 - saturation * f);
	    double t = brightness * (1.0 - (saturation * (1.0 - f)));
	    switch ((int) h) {
	    case 0:
		r = (int) (brightness * 255);
		g = (int) (t * 255);
		b = (int) (p * 255);
		break;
	    case 1:
		r = (int) (q * 255);
		g = (int) (brightness * 255);
		b = (int) (p * 255);
		break;
	    case 2:
		r = (int) (p * 255);
		g = (int) (brightness * 255);
		b = (int) (t * 255);
		break;
	    case 3:
		r = (int) (p * 255);
		g = (int) (q * 255);
		b = (int) (brightness * 255);
		break;
	    case 4:
		r = (int) (t * 255);
		g = (int) (p * 255);
		b = (int) (brightness * 255);
		break;
	    case 5:
		r = (int) (brightness * 255);
		g = (int) (p * 255);
		b = (int) (q * 255);
		break;
	    }
	}
	return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

    /**
     * Returns the HSB values corresponding to the color defined by the
     * red, green, and blue components.
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     * @param hsbvals the array to be used to return the 3 HSB values, or null
     * @return the array used to store the results [hue, saturation, brightness]
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRGB
     */
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
	float hue, saturation, brightness;
	if (hsbvals == null) {
	    hsbvals = new float[3];
	}
    	int cmax = (r > g) ? r : g;
	if (b > cmax) cmax = b;
	int cmin = (r < g) ? r : g;
	if (b < cmin) cmin = b;

	brightness = ((float) cmax) / 255.0f;
	if (cmax != 0)
	    saturation = ((float) (cmax - cmin)) / ((float) cmax);
	else
	    saturation = 0;
	if (saturation == 0)
	    hue = 0;
	else {
	    float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
	    float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
	    float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
	    if (r == cmax)
		hue = bluec - greenc;
	    else if (g == cmax)
	        hue = 2.0f + redc - bluec;
            else
		hue = 4.0f + greenc - redc;
	    hue = hue / 6.0f;
	    if (hue < 0)
		hue = hue + 1.0f;
	}
	hsbvals[0] = hue;
	hsbvals[1] = saturation;
	hsbvals[2] = brightness;
	return hsbvals;
    }

    /**
     * A static Color factory for generating a Color object from HSB
     * values.
     * @param h the hue component
     * @param s the saturation of the color
     * @param b the brightness of the color
     * @return the Color object for the corresponding RGB color
     */
    public static Color getHSBColor(float h, float s, float b) {
	return new Color(HSBtoRGB(h, s, b));
    }
}
