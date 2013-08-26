/*
 * @(#)CaretListener.java	1.11 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;
 
/**
 * Listener for changes in the caret position of a text 
 * component.
 *
 * @version 1.11 03/23/10
 * @author  Timothy Prinzing
 */
public interface CaretListener extends EventListener {

    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    void caretUpdate(CaretEvent e);
}

