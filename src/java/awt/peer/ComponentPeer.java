/*
 * @(#)ComponentPeer.java	1.22 96/12/16 Sami Shaio
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
