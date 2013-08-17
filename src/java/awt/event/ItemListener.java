/*
 * @(#)ItemListener.java	1.7 98/07/01
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
 * The listener interface for receiving item events.
 * @see ItemSelectable 
 *
 * @version 1.7 07/01/98
 * @author Amy Fowler
 */
public interface ItemListener extends EventListener {

    /**
     * Invoked when an item's state has been changed.
     */    
    void itemStateChanged(ItemEvent e);

}
