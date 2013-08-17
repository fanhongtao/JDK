/*
 * @(#)SampleData.java	1.3 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Color;
import java.awt.Font;

/**
  * @version 1.3 04/23/99
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
