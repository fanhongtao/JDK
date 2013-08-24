/*
 * @(#)HyperlinkListener.java	1.11 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;

/**
 * HyperlinkListener
 *
 * @version 1.11 11/17/05
 * @author  Timothy Prinzing
 */
public interface HyperlinkListener extends EventListener {

    /**
     * Called when a hypertext link is updated.
     *
     * @param e the event responsible for the update
     */
    void hyperlinkUpdate(HyperlinkEvent e);
}

