/*
 * @(#)AdjustmentListener.java	1.16 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving adjustment events. 
 *
 * @author Amy Fowler
 * @version 1.16 03/23/10
 * @since 1.1
 */
public interface AdjustmentListener extends EventListener {

    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e);

}
