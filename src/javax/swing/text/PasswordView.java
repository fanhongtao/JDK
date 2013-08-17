/*
 * @(#)PasswordView.java	1.9 98/10/29
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

import java.awt.*;
import javax.swing.JPasswordField;

/**
 * Implements a View suitable for use in JPasswordField
 * UI implementations.  This is basically a field ui that
 * renders its contents as the echo character specified
 * in the associated component (if it can narrow the
 * component to a JPasswordField).
 *
 * @author  Timothy Prinzing
 * @version 1.9 10/29/98
 * @see     View
 */
public class PasswordView extends FieldView {

    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public PasswordView(Element elem) {
	super(elem);
    }

    /**
     * Renders the given range in the model as normal unselected
     * text.  This sets the foreground color and echos the characters
     * using the value returned by getEchoChar().
     *
     * @param g the graphics context
     * @param x the starting X coordinate >= 0
     * @param y the starting Y coordinate >= 0
     * @param p0 the starting offset in the model >= 0
     * @param p1 the ending offset in the model >= p0
     * @returns the X location of the end of the range >= 0
     * @exception BadLocationException if p0 or p1 are out of range
     */
    protected int drawUnselectedText(Graphics g, int x, int y,
				     int p0, int p1) throws BadLocationException {

	Container c = getContainer();
	if (c instanceof JPasswordField) {
	    JPasswordField f = (JPasswordField) c;
	    g.setColor(f.getForeground());
	    char echoChar = f.getEchoChar();
	    int n = p1 - p0;
	    for (int i = 0; i < n; i++) {
		x = drawEchoCharacter(g, x, y, echoChar);
	    }
	}
	return x;
    }

    /**
     * Renders the given range in the model as selected text.  This
     * is implemented to render the text in the color specified in
     * the hosting component.  It assumes the highlighter will render
     * the selected background.  Uses the result of getEchoChar() to
     * display the characters.
     *
     * @param g the graphics context
     * @param x the starting X coordinate >= 0
     * @param y the starting Y coordinate >= 0
     * @param p0 the starting offset in the model >= 0
     * @param p1 the ending offset in the model >= p0
     * @returns the X location of the end of the range >= 0.
     * @exception BadLocationException if p0 or p1 are out of range
     */
    protected int drawSelectedText(Graphics g, int x,
				   int y, int p0, int p1) throws BadLocationException {
	g.setColor(selected);
	Container c = getContainer();
	if (c instanceof JPasswordField) {
	    JPasswordField f = (JPasswordField) c;
	    g.setColor(f.getSelectedTextColor());
	    char echoChar = f.getEchoChar();
	    int n = p1 - p0;
	    for (int i = 0; i < n; i++) {
		x = drawEchoCharacter(g, x, y, echoChar);
	    }
	}
	return x;
    }

    /**
     * Renders the echo character, or whatever graphic should be used
     * to display the password characters.  The color in the Graphics
     * object is set to the appropriate foreground color for selected
     * or unselected text.
     *
     * @param g the graphics context
     * @param x the starting X coordinate >= 0
     * @param y the starting Y coordinate >= 0
     * @param c the echo character
     * @return the updated X position >= 0
     */
    protected int drawEchoCharacter(Graphics g, int x, int y, char c) {
	ONE[0] = c;
	g.drawChars(ONE, 0, 1, x, y);
	return x + g.getFontMetrics().charWidth(c);
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	Container c = getContainer();
	if (c instanceof JPasswordField) {
	    JPasswordField f = (JPasswordField) c;
	    char echoChar = f.getEchoChar();
	    FontMetrics m = f.getFontMetrics(f.getFont());
	    
	    Rectangle alloc = adjustAllocation(a).getBounds();
	    int dx = (pos - getStartOffset()) * m.charWidth(echoChar);
	    alloc.x += dx;
	    alloc.width = 1;
	    return alloc;
	}
	return null;
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param fx the X coordinate >= 0.0f
     * @param fy the Y coordinate >= 0.0f
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point in the view
     * @see View#viewToModel
     */
    public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
	bias[0] = Position.Bias.Forward;
	int n = 0;
	Container c = getContainer();
	if (c instanceof JPasswordField) {
	    JPasswordField f = (JPasswordField) c;
	    char echoChar = f.getEchoChar();
	    FontMetrics m = f.getFontMetrics(f.getFont());
	    a = adjustAllocation(a);
	    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
                              a.getBounds();
	    n = ((int)fx - alloc.x) / m.charWidth(echoChar);
	    if (n < 0) {
		n = 0;
	    }
	    else if (n > (getStartOffset() + getDocument().getLength())) {
		n = getDocument().getLength() - getStartOffset();
	    }
	}
	return getStartOffset() + n;
    }

    static char[] ONE = new char[1];
}
