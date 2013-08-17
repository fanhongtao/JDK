/*
 * @(#)Label.java	1.25 97/01/27
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

import java.awt.peer.LabelPeer;

/**
 * A component that displays a single line of read-only text.
 *
 * @version	1.25, 01/27/97
 * @author 	Sami Shaio
 */
public class Label extends Component {

    /**
     * The left alignment.
     */
    public static final int LEFT 	= 0;

    /** 
     * The center alignment.
     */
    public static final int CENTER 	= 1;

    /**
     * The right alignment.
     */
    public static final int RIGHT 	= 2;

    /**
     * The text of this label.
     */
    String text;
    
    /**
     * The label's alignment.  The default alignment is set
     * to be left justified.
     */
    int	   alignment = LEFT;

    private static final String base = "label";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 3094126758329070636L;

    /**
     * Constructs an empty label.
     */
    public Label() {
	this("", LEFT);
    }

    /**
     * Constructs a new label with the specified String of text.
     * @param text the text that makes up the label
     */
    public Label(String text) {
        this(text, LEFT);
    }

    /**
     * Constructs a new label with the specified String of 
     * text and the specified alignment.
     * @param text the String that makes up the label
     * @param alignment the alignment value
     */
    public Label(String text, int alignment) {
	this.name = base + nameCounter++;
	this.text = text;
	setAlignment(alignment);
    }

    /**
     * Creates the peer for this label.  The peer allows us to
     * modify the appearance of the label without changing its 
     * functionality.
     */
    public void addNotify() {
	peer = getToolkit().createLabel(this);
	super.addNotify();
    }

    /** 
     * Gets the current alignment of this label. 
     * @see #setAlignment
     */
    public int getAlignment() {
	return alignment;
    }

    /** 
     * Sets the alignment for this label to the specified 
     * alignment.
     * @param alignment the alignment value
     * @exception IllegalArgumentException If an improper alignment was given. 
     * @see #getAlignment
     */
    public synchronized void setAlignment(int alignment) {
	switch (alignment) {
	  case LEFT:
	  case CENTER:
	  case RIGHT:
	    this.alignment = alignment;
    	    LabelPeer peer = (LabelPeer)this.peer;
	    if (peer != null) {
		peer.setAlignment(alignment);
	    }
	    return;
	}
	throw new IllegalArgumentException("improper alignment: " + alignment);
    }

    /** 
     * Gets the text of this label. 
     * @see #setText
     */
    public String getText() {
	return text;
    }

    /** 
     * Sets the text for this label to the specified text.
     * @param text the text that makes up the label 
     * @see #getText
     */
    public synchronized void setText(String text) {
	if (text != this.text && (this.text == null
				    || !this.text.equals(text))) {
	    this.text = text;
    	    LabelPeer peer = (LabelPeer)this.peer;
	    if (peer != null) {
		peer.setText(text);
	    }
	}
    }

    /**
     * Returns the parameter String of this label.
     */
    protected String paramString() {
	String str = ",align=";
	switch (alignment) {
	  case LEFT:   str += "left"; break;
	  case CENTER: str += "center"; break;
	  case RIGHT:  str += "right"; break;
	}
	return super.paramString() + str + ",text=" + text;
    }
}
