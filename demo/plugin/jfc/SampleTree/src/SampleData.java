/*
 * @(#)SampleData.java	1.5 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Color;
import java.awt.Font;

/**
  * @version 1.5 12/03/01
  * @author Scott Violet
  */

public class SampleData extends Object
{
    /** Font used for drawing. */
    protected Font          font;

    /** Color used for text. */
    protected Color         color;

    /** Value to display. */
    protected String        string;


    /**
      * Constructs a new instance of SampleData with the passed in
      * arguments.
      */
    public SampleData(Font newFont, Color newColor, String newString) {
	font = newFont;
	color = newColor;
	string = newString;
    }

    /**
      * Sets the font that is used to represent this object.
      */
    public void setFont(Font newFont) {
	font = newFont;
    }

    /**
      * Returns the Font used to represent this object.
      */
    public Font getFont() {
	return font;
    }

    /**
      * Sets the color used to draw the text.
      */
    public void setColor(Color newColor) {
	color = newColor;
    }

    /**
      * Returns the color used to draw the text.
      */
    public Color getColor() {
	return color;
    }

    /**
      * Sets the string to display for this object.
      */
    public void setString(String newString) {
	string = newString;
    }

    /**
      * Returnes the string to display for this object.
      */
    public String string() {
	return string;
    }

    public String toString() {
	return string;
    }
}
