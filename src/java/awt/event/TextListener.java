/*
 * @(#)TextListener.java	1.6 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * The listener interface for receiving text events. 
 * 
 * The class that is interested in processing a text event
 * implements this interface. The object created with that 
 * class is then registered with a component using the 
 * component's <code>addTextListener</code> method. When the
 * component's text changes, the listener object's 
 * <code>textValueChanged</code> method is invoked.
 *
 * @see TextEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/textlistener.html">Tutorial: Writing a Text Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @version 1.6 09/21/98
 * @author Georges Saab
 */
public interface TextListener extends EventListener {

    /**
     * Invoked when the value of the text has changed.
     * The code written for this method performs the operations
     * that need to occur when text changes.
     */   
    public void textValueChanged(TextEvent e);

}
