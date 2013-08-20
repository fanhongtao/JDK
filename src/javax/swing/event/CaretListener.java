/*
 * @(#)CaretListener.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;
 
/**
 * Listener for changes in the caret position of a text 
 * component.
 *
 * @version 1.9 12/19/03
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

