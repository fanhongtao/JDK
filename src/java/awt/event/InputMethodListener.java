/*
 * @(#)InputMethodListener.java	1.5 98/09/21
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

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving input method events. A text editing
 * component has to install an input method event listener in order to work
 * with input methods.
 *
 * <p>
 * The text editing component also has to provide an instance of InputMethodRequests.
 *
 * @see InputMethodEvent
 * @see java.awt.im.InputMethodRequests
 *
 * @version 1.5 09/21/98
 * @author JavaSoft Asia/Pacific
 */

public interface InputMethodListener extends EventListener {

    /**
     * Invoked when the text entered through an input method has changed.
     */
    void inputMethodTextChanged(InputMethodEvent event);

    /**
     * Invoked when the caret within composed text has changed.
     */
    void caretPositionChanged(InputMethodEvent event);

}
