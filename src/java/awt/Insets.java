/*
 * @(#)Insets.java	1.12 97/01/27
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
 * The insets of a container.
 * This class is used to layout containers.
 *
 * @see LayoutManager
 * @see Container
 *
 * @version 	1.12, 01/27/97
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public class Insets implements Cloneable, java.io.Serializable {

    /**
     * The inset from the top.
     */
    public int top;

    /**
     * The inset from the left.
     */
    public int left;

    /**
     * The inset from the bottom.
     */
    public int bottom;

    /**
     * The inset from the right.
     */
    public int right;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -2272572637695466749L;

    /**
     * Constructs and initializes a new Inset with the specified top,
     * left, bottom, and right insets.
     * @param top the inset from the top
     * @param left the inset from the left
     * @param bottom the inset from the bottom
     * @param right the inset from the right
     */
    public Insets(int top, int left, int bottom, int right) {
	this.top = top;
	this.left = left;
	this.bottom = bottom;
	this.right = right;
    }

    /**
     * Checks whether two insets objects are equal.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Insets) {
	    Insets insets = (Insets)obj;
	    return ((top == insets.top) && (left == insets.left) &&
		    (bottom == insets.bottom) && (right == insets.right));
	}
	return false;
    }

    /**
     * Returns a String object representing this Inset's values.
     */
    public String toString() {
	return getClass().getName() + "[top="  + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
    }

    public Object clone() { 
	try { 
	    return super.clone();
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
