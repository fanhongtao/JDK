/*
 * @(#)AdjustmentListener.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving adjustment events. 
 *
 * @version 1.8 09/21/98
 * @author Amy Fowler
 */
public interface AdjustmentListener extends EventListener {

    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e);

}
