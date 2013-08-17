/*
 * @(#)AdjustmentListener.java	1.11 00/02/02
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
 * @author Amy Fowler
 * @version 1.11 02/02/00
 * @since 1.1
 */
public interface AdjustmentListener extends EventListener {

    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e);

}
