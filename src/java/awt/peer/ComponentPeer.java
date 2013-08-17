/*
 * @(#)ComponentPeer.java	1.23 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.peer;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ColorModel;

public interface ComponentPeer {
    void    	    	setVisible(boolean b);
    void    	    	setEnabled(boolean b);
    void		paint(Graphics g);
    void		repaint(long tm, int x, int y, int width, int height);
    void		print(Graphics g);
    void		setBounds(int x, int y, int width, int height);
    void                handleEvent(AWTEvent e);
    Point		getLocationOnScreen();
    Dimension		getPreferredSize();
    Dimension		getMinimumSize();
    ColorModel		getColorModel();
    java.awt.Toolkit	getToolkit();
    Graphics		getGraphics();
    FontMetrics		getFontMetrics(Font font);
    void		dispose();
    void		setForeground(Color c);
    void		setBackground(Color c);
    void		setFont(Font f);
    void 		setCursor(Cursor cursor);
    void		requestFocus();
    boolean		isFocusTraversable();

    Image 		createImage(ImageProducer producer);
    Image 		createImage(int width, int height);
    boolean		prepareImage(Image img, int w, int h, ImageObserver o);
    int			checkImage(Image img, int w, int h, ImageObserver o);

    /**
     * DEPRECATED:  Replaced by getPreferredSize().
     */
    Dimension		preferredSize();

    /**
     * DEPRECATED:  Replaced by getMinimumSize().
     */
    Dimension		minimumSize();

    /**
     * DEPRECATED:  Replaced by setVisible(boolean).
     */
    void		show();

    /**
     * DEPRECATED:  Replaced by setVisible(boolean).
     */
    void		hide();

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    void		enable();

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    void		disable();

    /**
     * DEPRECATED:  Replaced by setBounds(int, int, int, int).
     */
    void		reshape(int x, int y, int width, int height);

}
