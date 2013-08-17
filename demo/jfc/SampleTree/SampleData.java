/*
 * @(#)SampleData.java	1.2 98/03/18
 *
 * Copyright 1997 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import java.awt.Color;
import java.awt.Font;

/**
  * @version 1.2 03/18/98
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
