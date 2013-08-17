/*
 * @(#)SwingConstants.java	1.9 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing;


/**
 * A collection of constants generally used for positioning and orienting
 * components on the screen.
 *
 * @version 1.9 08/26/98
 * @author Jeff Dinkins
 * @author Ralph Kar (orientation support)
 */
public interface SwingConstants {

        /** 
         * The central position in an area. Used for
         * both compass-direction constants (NORTH, etc.)
         * and box-orientation constants (TOP, etc.).
         */
        public static final int CENTER  = 0;

        // 
        // Box-orientation constant used to specify locations in a box.
        //
        /** 
         * Box-orientation constant used to specify the top of a box.
         */
        public static final int TOP     = 1;
        /** 
         * Box-orientation constant used to specify the left side of a box.
         */
        public static final int LEFT    = 2;
        /** 
         * Box-orientation constant used to specify the bottom of a box.
         */
        public static final int BOTTOM  = 3;
        /** 
         * Box-orientation constant used to specify the right side of a box.
         */
        public static final int RIGHT   = 4;

        // 
        // Compass-direction constants used to specify a position.
        //
        /** 
         * Compass-direction North (up).
         */
        public static final int NORTH      = 1;
        /** 
         * Compass-direction north-east (upper right).
         */
        public static final int NORTH_EAST = 2;
        /** 
         * Compass-direction east (right).
         */
        public static final int EAST       = 3;
        /** 
         * Compass-direction south-east (lower right).
         */
        public static final int SOUTH_EAST = 4;
        /** 
         * Compass-direction south (down).
         */
        public static final int SOUTH      = 5;
        /** 
         * Compass-direction south-west (lower left).
         */
        public static final int SOUTH_WEST = 6;
        /** 
         * Compass-direction west (left).
         */
        public static final int WEST       = 7;
        /** 
         * Compass-direction north west (upper left).
         */
        public static final int NORTH_WEST = 8;

        //
        // These constants specify a horizontal or 
        // vertical orientation. For example, they are
        // used by scrollbars and sliders.
        //
        /** Horizontal orientation. Used for scrollbars and sliders. */
        public static final int HORIZONTAL = 0;
        /** Vertical orientation. Used for scrollbars and sliders. */
        public static final int VERTICAL   = 1; 

        //
        // Constants for orientation support, since some languages are
        // left-to-right oriented and some are right-to-left oriented.
        // This orientation is currently used by buttons and labels.
        //
        /**
         * Identifies the leading edge of text for use with left-to-right
         * and right-to-left languages. Used by buttons and labels.
         */
        public static final int LEADING  = 10;
        /**
         * Identifies the trailing edge of text for use with left-to-right
         * and right-to-left languages. Used by buttons and labels.
         */
        public static final int TRAILING = 11;
}
