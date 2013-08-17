/*
 * @(#)TextListener.java	1.3 98/07/01
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
 * @version 1.3 07/01/98
 * @author Georges Saab
 */
public interface TextListener extends EventListener {

    /**
     * Invoked when the value of the text has changed.
     */   
    public void textValueChanged(TextEvent e);

}
