/*
 * @(#)ComponentPeer.java	1.41 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;
import java.awt.event.PaintEvent;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.GraphicsConfiguration;

import java.awt.dnd.peer.DropTargetPeer;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface ComponentPeer {
    boolean isObscured();
    boolean canDetermineObscurity();
    void    	    	setVisible(boolean b);
    void    	    	setEnabled(boolean b);
    void		paint(Graphics g);
    void		repaint(long tm, int x, int y, int width, int height);
    void		print(Graphics g);
    void		setBounds(int x, int y, int width, int height);
    void                handleEvent(AWTEvent e);
    void                coalescePaintEvent(PaintEvent e);
    Point		getLocationOnScreen();
    Dimension		getPreferredSize();
    Dimension		getMinimumSize();
    ColorModel		getColorModel();
    Toolkit		getToolkit();
    Graphics		getGraphics();
    FontMetrics		getFontMetrics(Font font);
    void		dispose();
    void		setForeground(Color c);
    void		setBackground(Color c);
    void		setFont(Font f);
    void 		updateCursorImmediately();
    boolean		requestFocus(Component lightweightChild,
                                     boolean temporary,
				     boolean focusedWindowChangeAllowed,
                                     long time);
    boolean		isFocusable();

    Image 		createImage(ImageProducer producer);
    Image 		createImage(int width, int height);
    VolatileImage 	createVolatileImage(int width, int height);
    boolean		prepareImage(Image img, int w, int h, ImageObserver o);
    int			checkImage(Image img, int w, int h, ImageObserver o);
    GraphicsConfiguration getGraphicsConfiguration();
    boolean     handlesWheelScrolling();
    void createBuffers(int numBuffers, BufferCapabilities caps)
        throws AWTException;
    Image getBackBuffer();
    void flip(BufferCapabilities.FlipContents flipAction);
    void destroyBuffers();

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
