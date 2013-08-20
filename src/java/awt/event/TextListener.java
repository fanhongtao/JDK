/*
 * @(#)TextListener.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @author Georges Saab
 * @version 1.12 12/19/03
 *
 * @see TextEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/textlistener.html">Tutorial: Writing a Text Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @since 1.1
 */
public interface TextListener extends EventListener {

    /**
     * Invoked when the value of the text has changed.
     * The code written for this method performs the operations
     * that need to occur when text changes.
     */   
    public void textValueChanged(TextEvent e);

}
