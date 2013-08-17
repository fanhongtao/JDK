/*
 * @(#)ScrollPaneConstants.java	1.11 98/08/26
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
 * Constants used with the JScrollPane component.
 *
 * @version 1.11 08/26/98
 * @author Hans Muller
 */
public interface ScrollPaneConstants
{
    /**
     * Identifies a "viewport" or display area, within which
     * scrolled contents are visible.
     */
    String VIEWPORT = "VIEWPORT";
    /** Identifies a vertical scrollbar. */
    String VERTICAL_SCROLLBAR = "VERTICAL_SCROLLBAR";
    /** Identifies a horizonal scrollbar. */
    String HORIZONTAL_SCROLLBAR = "HORIZONTAL_SCROLLBAR";
    /** 
     * Identifies the area along the left side of the viewport between the
     * upper left corner and the lower left corner.
     */
    String ROW_HEADER = "ROW_HEADER";
    /** 
     * Identifies the area at the top the viewport between the
     * upper left corner and the upper right corner.
     */
    String COLUMN_HEADER = "COLUMN_HEADER";
    /** Identifies the lower left corner of the viewport. */
    String LOWER_LEFT_CORNER = "LOWER_LEFT_CORNER";
    /** Identifies the lower right corner of the viewport. */
    String LOWER_RIGHT_CORNER = "LOWER_RIGHT_CORNER";
    /** Identifies the upper left corner of the viewport. */
    String UPPER_LEFT_CORNER = "UPPER_LEFT_CORNER";
    /** Identifies the upper right corner of the viewport. */
    String UPPER_RIGHT_CORNER = "UPPER_RIGHT_CORNER";

    /** Identifies the vertical scroll bar policy property. */
    String VERTICAL_SCROLLBAR_POLICY = "VERTICAL_SCROLLBAR_POLICY";
    /** Identifies the horizontal scroll bar policy property. */
    String HORIZONTAL_SCROLLBAR_POLICY = "HORIZONTAL_SCROLLBAR_POLICY";

    /**
     * Used to set the vertical scroll bar policy so that 
     * vertical scrollbars are displayed only when needed.
     */
    int VERTICAL_SCROLLBAR_AS_NEEDED = 20;
    /**
     * Used to set the vertical scroll bar policy so that 
     * vertical scrollbars are never displayed.
     */
    int VERTICAL_SCROLLBAR_NEVER = 21;
    /**
     * Used to set the vertical scroll bar policy so that 
     * vertical scrollbars are always displayed.
     */
    int VERTICAL_SCROLLBAR_ALWAYS = 22;

    /**
     * Used to set the horizontal scroll bar policy so that 
     * horizontal scrollbars are displayed only when needed.
     */
    int HORIZONTAL_SCROLLBAR_AS_NEEDED = 30;
    /**
     * Used to set the horizontal scroll bar policy so that 
     * horizontal scrollbars are never displayed.
     */
    int HORIZONTAL_SCROLLBAR_NEVER = 31;
    /**
     * Used to set the horizontal scroll bar policy so that 
     * horizontal scrollbars are always displayed.
     */
    int HORIZONTAL_SCROLLBAR_ALWAYS = 32;
}

