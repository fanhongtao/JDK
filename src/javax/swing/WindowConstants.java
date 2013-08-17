/*
 * @(#)WindowConstants.java	1.7 98/08/26
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
 * Constants used to control the window-closing operation.
 * For example, see {@link JFrame#setDefaultCloseOperation}
 *
 * @version 1.7 08/26/98
 * @author Amy Fowler
 */
public interface WindowConstants
{
    /**
     * The do-nothing default window close operation
     */
    public static final int DO_NOTHING_ON_CLOSE = 0;

    /**
     * The hide-window default window close operation
     */
    public static final int HIDE_ON_CLOSE = 1;

    /**
     * The dispose-window default window close operation
     */
    public static final int DISPOSE_ON_CLOSE = 2;
}

