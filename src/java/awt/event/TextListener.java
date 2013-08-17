/*
 * @(#)TextListener.java	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving adjustment events. 
 *
 * @version 1.4 12/10/01
 * @author Georges Saab
 */
public interface TextListener extends EventListener {

    /**
     * Invoked when the value of the text has changed.
     */   
    public void textValueChanged(TextEvent e);

}
