/*
 * @(#)Dimension.java	1.12 97/01/27
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

/**
 * A class to encapsulate a width and a height Dimension.
 * 
 * @version 	1.12, 01/27/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Dimension implements java.io.Serializable {
    
    /**
     * The width dimension.
     */
    public int width;

    /**
     * The height dimension.
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 4723952579491349524L;

    /** 
     * Constructs a Dimension object with 0 width and 0 height.
     */
    public Dimension() {
	this(0, 0);
    }

    /** 
     * Constructs a Dimension and initializes it to the specified value.
     * @param d the specified dimension for the width and height values
     */
    public Dimension(Dimension d) {
	this(d.width, d.height);
    }

    /** 
     * Constructs a Dimension and initializes it to the specified width and
     * specified height.
     * @param width the specified width dimension
     * @param height the specified height dimension
     */
    public Dimension(int width, int height) {
	this.width = width;
	this.height = height;
    }

    /**
     * Returns the size of this Dimension object.
     * This method is included for completeness, to parallel the
     * getSize method of Component.
     */
    public Dimension getSize() {
	return new Dimension(width, height);
    }	

    /**
     * Set the size of this Dimension object to match the specified size.
     * This method is included for completeness, to parallel the
     * getSize method of Component.
     * @param d  the new size for the Dimension object
     */
    public void setSize(Dimension d) {
	setSize(d.width, d.height);
    }	

    /**
     * Set the size of this Dimension object to the specified width
     * and height.
     * This method is included for completeness, to parallel the
     * getSize method of Component.
     * @param width  the new width for the Dimension object
     * @param height  the new height for the Dimension object
     */
    public void setSize(int width, int height) {
    	this.width = width;
    	this.height = height;
    }	

    /**
     * Checks whether two dimension objects have equal values.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Dimension) {
	    Dimension d = (Dimension)obj;
	    return (width == d.width) && (height == d.height);
	}
	return false;
    }

    /**
     * Returns the String representation of this Dimension's values.
     */
    public String toString() {
	return getClass().getName() + "[width=" + width + ",height=" + height + "]";
    }
}
