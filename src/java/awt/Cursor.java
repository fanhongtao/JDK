/*
 * @(#)Cursor.java	1.6 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;


/**
 * A class to encapsulate the bitmap representation of the mouse cursor.
 *
 * @see Component#setCursor
 * @version 	1.6, 07/01/98
 * @author 	Amy Fowler
 */
public class Cursor implements java.io.Serializable {

    /**
     * The default cursor type (gets set if no cursor is defined).
     */
    public static final int	DEFAULT_CURSOR   		= 0;

    /**
     * The crosshair cursor type.
     */
    public static final int	CROSSHAIR_CURSOR 		= 1;

    /**
     * The text cursor type.
     */
    public static final int	TEXT_CURSOR 	 		= 2;

    /**
     * The wait cursor type.
     */
    public static final int	WAIT_CURSOR	 		= 3;

    /**
     * The south-west-resize cursor type.
     */
    public static final int	SW_RESIZE_CURSOR	 	= 4;

    /**
     * The south-east-resize cursor type.
     */
    public static final int	SE_RESIZE_CURSOR	 	= 5;

    /**
     * The north-west-resize cursor type.
     */
    public static final int	NW_RESIZE_CURSOR		= 6;

    /**
     * The north-east-resize cursor type.
     */
    public static final int	NE_RESIZE_CURSOR	 	= 7;

    /**
     * The north-resize cursor type.
     */
    public static final int	N_RESIZE_CURSOR 		= 8;

    /**
     * The south-resize cursor type.
     */
    public static final int	S_RESIZE_CURSOR 		= 9;

    /**
     * The west-resize cursor type.
     */
    public static final int	W_RESIZE_CURSOR	 		= 10;

    /**
     * The east-resize cursor type.
     */
    public static final int	E_RESIZE_CURSOR			= 11;

    /**
     * The hand cursor type.
     */
    public static final int	HAND_CURSOR			= 12;

    /**
     * The move cursor type.
     */
    public static final int	MOVE_CURSOR			= 13;

    protected static Cursor predefined[] = new Cursor[14];

    int type = DEFAULT_CURSOR;

     /*
      * JDK 1.1 serialVersionUID 
      */
     private static final long serialVersionUID = 8028237497568985504L;

    /**
     * Returns a cursor object with the specified predefined type.
     * @param type the type of predefined cursor
     */
    static public Cursor getPredefinedCursor(int type) {
	if (type < Cursor.DEFAULT_CURSOR || type > Cursor.MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	if (predefined[type] == null) {
	    predefined[type] = new Cursor(type);
	}
	return predefined[type];
    }

    /**
     * Return the system default cursor.
     */
    static public Cursor getDefaultCursor() {
        return getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Creates a new cursor object with the specified type.
     * @param type the type of cursor
     */
    public Cursor(int type) {
	if (type < Cursor.DEFAULT_CURSOR || type > Cursor.MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	this.type = type;
    }

    /**
     * Returns the type for this cursor.
     */
    public int getType() {
	return type;
    }	
    
}
