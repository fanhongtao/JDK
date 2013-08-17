/*
 * @(#)Caret.java	1.23 98/08/26
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
package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import javax.swing.Action;
import javax.swing.event.ChangeListener;

/**
 * A place within a document view that represents where
 * things can be inserted into the document model.  It gives a 
 * way to navigate through the document view while abstracting
 * away the details of how the view is arranged.  This can
 * be useful because some views may filter out portions of
 * the associated model, and some views may not allow navigation
 * in certain areas such as read-only areas.
 *
 * @author  Timothy Prinzing
 * @version 1.23 08/26/98
 */
public interface Caret {

    /**
     * Called when the UI is being installed into the
     * interface of a JTextComponent.  This can be used
     * to gain access to the model that is being navigated
     * by the implementation of this interface. 
     *
     * @param c the JTextComponent
     */
    public void install(JTextComponent c);

    /**
     * Called when the UI is being removed from the
     * interface of a JTextComponent.  This is used to 
     * unregister any listeners that were attached.
     *
     * @param c the JTextComponent
     */
    public void deinstall(JTextComponent c);

    /**
     * Renders the caret.
     *
     * @param g the graphics context
     */
    public void paint(Graphics g);

    /**
     * Adds a listener to track whenever the caret position
     * has been changed.
     *
     * @param l the change listener
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Removes a listener that was tracking caret position changes.
     *
     * @param l the change listener
     */
    public void removeChangeListener(ChangeListener l);

    /**
     * Determines if the caret is currently visible.
     *
     * @return true if the caret is visible else false
     */
    public boolean isVisible();

    /**
     * Sets the visibility of the caret.
     *
     * @param v  true if the caret should be shown,
     *  and false if the caret should be hidden
     */
    public void setVisible(boolean v);

    /**
     * Determines if the selection is currently visible.
     *
     * @return true if the caret is visible else false
     */
    public boolean isSelectionVisible();

    /**
     * Sets the visibility of the selection
     *
     * @param v  true if the caret should be shown,
     *  and false if the caret should be hidden
     */
    public void setSelectionVisible(boolean v);

    /**
     * Saves the current caret position.  This is used when 
     * caret up or down actions occur, moving between lines
     * that have uneven end positions.
     *
     * @param p  the Point to use for the saved position
     */
    public void setMagicCaretPosition(Point p);

    /**
     * Gets the current caret position. 
     *
     * @return the position
     * @see #setMagicCaretPosition
     */
    public Point getMagicCaretPosition();

    /**
     * Sets the blink rate of the caret.  This determines if
     * and how fast the caret blinks, commonly used as one
     * way to attract attention to the caret.
     *
     * @param rate  the delay in milliseconds >= 0.  If this is
     *  zero the caret will not blink.
     */
    public void setBlinkRate(int rate);

    /**
     * Gets the blink rate of the caret.  This determines if
     * and how fast the caret blinks, commonly used as one
     * way to attract attention to the caret.
     *
     * @returns the delay in milliseconds >= 0.  If this is
     *  zero the caret will not blink.
     */
    public int getBlinkRate();

    /**
     * Fetches the current position of the caret.
     *
     * @return the position >= 0
     */
    public int getDot();

    /**
     * Fetches the current position of the mark.  If there
     * is a selection, the mark will not be the same as
     * the dot.
     *
     * @return the position >= 0
     */
    public int getMark();

    /**
     * Sets the caret position to some position.  This
     * causes the mark to become the same as the dot,
     * effectively setting the selection range to zero.
     *
     * @param dot  the new position to set the caret to >= 0
     */
    public void setDot(int dot);

    /**
     * Moves the caret position to some other position,
     * leaving behind the mark.  This is useful for
     * making selections.
     *
     * @param dot  the new position to move the caret to >= 0
     */
    public void moveDot(int dot);

};
    
