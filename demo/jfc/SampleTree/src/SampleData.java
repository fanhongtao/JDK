/*
 * @(#)SampleData.java	1.9 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)SampleData.java	1.9 04/07/26
 */

import java.awt.Color;
import java.awt.Font;

/**
  * @version 1.9 07/26/04
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
