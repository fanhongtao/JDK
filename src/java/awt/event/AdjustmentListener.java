/*
 * @(#)AdjustmentListener.java	1.6 98/07/01
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

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving adjustment events. 
 *
 * @version 1.6 07/01/98
 * @author Amy Fowler
 */
public interface AdjustmentListener extends EventListener {

    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e);

}
