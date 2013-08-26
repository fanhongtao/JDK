/*
 * @(#)HyperlinkListener.java	1.12 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;

/**
 * HyperlinkListener
 *
 * @version 1.12 03/23/10
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

