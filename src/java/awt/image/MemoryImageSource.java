/*
 * @(#)MemoryImageSource.java	1.19 98/07/01
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

package java.awt.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This class is an implementation of the ImageProducer interface which
 * uses an array to produce pixel values for an Image.  Here is an example
 * which calculates a 100x100 image representing a fade from black to blue
 * along the X axis and a fade from black to red along the Y axis:
 * <pre>
 * 
 *	int w = 100;
 *	int h = 100;
 *	int pix[] = new int[w * h];
 *	int index = 0;
 *	for (int y = 0; y < h; y++) {
 *	    int red = (y * 255) / (h - 1);
 *	    for (int x = 0; x < w; x++) {
 *		int blue = (x * 255) / (w - 1);
 *		pix[index++] = (255 << 24) | (red << 16) | blue;
 *	    }
 *	}
 *	Image img = createImage(new MemoryImageSource(w, h, pix, 0, w));
 * 
 * </pre>
 * The MemoryImageSource is also capable of managing a memory image which
 * varies over time to allow animation or custom rendering.  Here is an
 * example showing how to set up the animation source and signal changes
 * in the data (adapted from the MemoryAnimationSourceDemo by Garth Dickie):
 * <pre>
 *
 *	int pixels[];
 *	MemoryImageSource source;
 *
 *	public void init() {
 *	    int width = 50;
 *	    int height = 50;
 *	    int size = width * height;
 *	    pixels = new int[size];
 *
 *	    int value = getBackground().getRGB();
 *	    for (int i = 0; i < size; i++) {
 *		pixels[i] = value;
 *	    }
 *
 *	    source = new MemoryImageSource(width, height, pixels, 0, width);
 *	    source.setAnimated(true);
 *	    image = createImage(source);
 *	}
 *
 *	public void run() {
 *	    Thread me = Thread.currentThread( );
 *	    me.setPriority(Thread.MIN_PRIORITY);
 *
 *	    while (true) {
 *		try {
 *		    thread.sleep(10);
 *		} catch( InterruptedException e ) {
 *		    return;
 *		}
 *
 *		// Modify the values in the pixels array at (x, y, w, h)
 *
 *		// Send the new data to the interested ImageConsumers
 *		source.newPixels(x, y, w, h);
 *	    }
 *	}
 *
 * </pre>
 *
 * @see ImageProducer
 *
 * @version	1.19 07/01/98
 * @author 	Jim Graham
 * @author	Animation capabilities inspired by the
 *		MemoryAnimationSource class written by Garth Dickie
 */
public class MemoryImageSource implements ImageProducer {
    int width;
    int height;
    ColorModel model;
    Object pixels;
    int pixeloffset;
    int pixelscan;
    Hashtable properties;
    Vector theConsumers = new Vector();
    boolean animating;
    boolean fullbuffers;

    /**
     * Constructs an ImageProducer object which uses an array of bytes
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     byte[] pix, int off, int scan) {
	initialize(w, h, cm, (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of bytes
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     byte[] pix, int off, int scan, Hashtable props) {
	initialize(w, h, cm, (Object) pix, off, scan, props);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     int[] pix, int off, int scan) {
	initialize(w, h, cm, (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     int[] pix, int off, int scan, Hashtable props) {
	initialize(w, h, cm, (Object) pix, off, scan, props);
    }

    private void initialize(int w, int h, ColorModel cm,
			    Object pix, int off, int scan, Hashtable props) {
	width = w;
	height = h;
	model = cm;
	pixels = pix;
	pixeloffset = off;
	pixelscan = scan;
	if (props == null) {
	    props = new Hashtable();
	}
	properties = props;
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * in the default RGB ColorModel to produce data for an Image object.
     * @see java.awt.Component#createImage
     * @see ColorModel#getRGBdefault
     */
    public MemoryImageSource(int w, int h, int pix[], int off, int scan) {
	initialize(w, h, ColorModel.getRGBdefault(),
		   (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * in the default RGB ColorModel to produce data for an Image object.
     * @see java.awt.Component#createImage
     * @see ColorModel#getRGBdefault
     */
    public MemoryImageSource(int w, int h, int pix[], int off, int scan,
			     Hashtable props) {
	initialize(w, h, ColorModel.getRGBdefault(),
		   (Object) pix, off, scan, props);
    }

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void addConsumer(ImageConsumer ic) {
	if (theConsumers.contains(ic)) {
	    return;
	}
	theConsumers.addElement(ic);
	try {
	    initConsumer(ic);
	    sendPixels(ic, 0, 0, width, height);
	    if (isConsumer(ic)) {
		ic.imageComplete(animating
				 ? ImageConsumer.SINGLEFRAMEDONE
				 : ImageConsumer.STATICIMAGEDONE);
		if (!animating && isConsumer(ic)) {
		    ic.imageComplete(ImageConsumer.IMAGEERROR);
		    removeConsumer(ic);
		}
	    }
	} catch (Exception e) {
	    if (isConsumer(ic)) {
		ic.imageComplete(ImageConsumer.IMAGEERROR);
	    }
	}
    }

    /**
     * Determine if an ImageConsumer is on the list of consumers currently
     * interested in data for this image.
     * @return true if the ImageConsumer is on the list; false otherwise
     * @see ImageConsumer
     */
    public synchronized boolean isConsumer(ImageConsumer ic) {
	return theConsumers.contains(ic);
    }

    /**
     * Remove an ImageConsumer from the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void removeConsumer(ImageConsumer ic) {
	theConsumers.removeElement(ic);
    }

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image, and immediately start delivery of the
     * image data through the ImageConsumer interface.
     * @see ImageConsumer
     */
    public void startProduction(ImageConsumer ic) {
	addConsumer(ic);
    }

    /**
     * Requests that a given ImageConsumer have the image data delivered
     * one more time in top-down, left-right order.
     * @see ImageConsumer
     */
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	// Ignored.  The data is either single frame and already in TDLR
	// format or it is multi-frame and TDLR resends aren't critical.
    }

    /**
     * Change this memory image into a multi-frame animation or a
     * single-frame static image depending on the animated parameter.
     * <p>This method should be called immediately after the
     * MemoryImageSource is constructed and before an image is
     * created with it to ensure that all ImageConsumers will
     * receive the correct multi-frame data.  If an ImageConsumer
     * is added to this ImageProducer before this flag is set then
     * that ImageConsumer will see only a snapshot of the pixel
     * data that was available when it connected.
     * @param animated true if the image is a multi-frame animation
     */
    public synchronized void setAnimated(boolean animated) {
	this.animating = animated;
	if (!animating) {
	    Enumeration enum = theConsumers.elements();
	    while (enum.hasMoreElements()) {
	    	ImageConsumer ic = (ImageConsumer) enum.nextElement();
		ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
		if (isConsumer(ic)) {
		    ic.imageComplete(ImageConsumer.IMAGEERROR);
		}
	    }
	    theConsumers.removeAllElements();
	}
    }

    /**
     * Specify whether this animated memory image should always be
     * updated by sending the complete buffer of pixels whenever
     * there is a change.
     * This flag is ignored if the animation flag is not turned on
     * through the setAnimated() method.
     * <p>This method should be called immediately after the
     * MemoryImageSource is constructed and before an image is
     * created with it to ensure that all ImageConsumers will
     * receive the correct pixel delivery hints.
     * @param fullbuffers true if the complete pixel buffer should always
     * be sent
     * @see #setAnimated
     */
    public synchronized void setFullBufferUpdates(boolean fullbuffers) {
	if (this.fullbuffers == fullbuffers) {
	    return;
	}
	this.fullbuffers = fullbuffers;
	if (animating) {
	    Enumeration enum = theConsumers.elements();
	    while (enum.hasMoreElements()) {
	    	ImageConsumer ic = (ImageConsumer) enum.nextElement();
		ic.setHints(fullbuffers
			    ? (ImageConsumer.TOPDOWNLEFTRIGHT |
			       ImageConsumer.COMPLETESCANLINES)
			    : ImageConsumer.RANDOMPIXELORDER);
	    }
	}
    }

    /**
     * Send a whole new buffer of pixels to any ImageConsumers that
     * are currently interested in the data for this image and notify
     * them that an animation frame is complete.
     * This method only has effect if the animation flag has been
     * turned on through the setAnimated() method.
     * @see ImageConsumer
     * @see #setAnimated
     */
    public void newPixels() {
	newPixels(0, 0, width, height, true);
    }

    /**
     * Send a rectangular region of the buffer of pixels to any
     * ImageConsumers that are currently interested in the data for
     * this image and notify them that an animation frame is complete.
     * This method only has effect if the animation flag has been
     * turned on through the setAnimated() method.
     * If the full buffer update flag was turned on with the
     * setFullBufferUpdates() method then the rectangle parameters
     * will be ignored and the entire buffer will always be sent.
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to be sent
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to be sent
     * @param w the width of the rectangle of pixels to be sent
     * @param h the height of the rectangle of pixels to be sent
     * @see ImageConsumer
     * @see #setAnimated
     * @see #setFullBufferUpdates
     */
    public synchronized void newPixels(int x, int y, int w, int h) {
	newPixels(x, y, w, h, true);
    }

    /**
     * Send a rectangular region of the buffer of pixels to any
     * ImageConsumers that are currently interested in the data for
     * this image.
     * If the framenotify parameter is true then the consumers are
     * also notified that an animation frame is complete.
     * This method only has effect if the animation flag has been
     * turned on through the setAnimated() method.
     * If the full buffer update flag was turned on with the
     * setFullBufferUpdates() method then the rectangle parameters
     * will be ignored and the entire buffer will always be sent.
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to be sent
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to be sent
     * @param w the width of the rectangle of pixels to be sent
     * @param h the height of the rectangle of pixels to be sent
     * @param framenotify true if the consumers should be sent a
     * SINGLEFRAMEDONE notification
     * @see ImageConsumer
     * @see #setAnimated
     * @see #setFullBufferUpdates
     */
    public synchronized void newPixels(int x, int y, int w, int h,
				       boolean framenotify) {
	if (animating) {
	    if (fullbuffers) {
		x = y = 0;
		w = width;
		h = height;
	    } else {
		if (x < 0) {
		    w += x;
		    x = 0;
		}
		if (x + w > width) {
		    w = width - x;
		}
		if (y < 0) {
		    h += y;
		    y = 0;
		}
		if (y + h > height) {
		    h = height - y;
		}
	    }
	    if ((w <= 0 || h <= 0) && !framenotify) {
		return;
	    }
	    Enumeration enum = theConsumers.elements();
	    while (enum.hasMoreElements()) {
	    	ImageConsumer ic = (ImageConsumer) enum.nextElement();
		if (w > 0 && h > 0) {
		    sendPixels(ic, x, y, w, h);
		}
		if (framenotify && isConsumer(ic)) {
		    ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
	    }
	}
    }

    /**
     * Change to a new byte array to hold the pixels for this image.
     * If the animation flag has been turned on through the setAnimated()
     * method, then the new pixels will be immediately delivered to any
     * ImageConsumers that are currently interested in the data for
     * this image.
     * @see #setAnimated
     */
    public synchronized void newPixels(byte[] newpix, ColorModel newmodel,
				       int offset, int scansize) {
	this.pixels = newpix;
	this.model = newmodel;
	this.pixeloffset = offset;
	this.pixelscan = scansize;
	newPixels();
    }

    /**
     * Change to a new int array to hold the pixels for this image.
     * If the animation flag has been turned on through the setAnimated()
     * method, then the new pixels will be immediately delivered to any
     * ImageConsumers that are currently interested in the data for
     * this image.
     * @see #setAnimated
     */
    public synchronized void newPixels(int[] newpix, ColorModel newmodel,
				       int offset, int scansize) {
	this.pixels = newpix;
	this.model = newmodel;
	this.pixeloffset = offset;
	this.pixelscan = scansize;
	newPixels();
    }

    private void initConsumer(ImageConsumer ic) {
	if (isConsumer(ic)) {
	    ic.setDimensions(width, height);
	}
	if (isConsumer(ic)) {
	    ic.setProperties(properties);
	}
	if (isConsumer(ic)) {
	    ic.setColorModel(model);
	}
	if (isConsumer(ic)) {
	    ic.setHints(animating
			? (fullbuffers
			   ? (ImageConsumer.TOPDOWNLEFTRIGHT |
			      ImageConsumer.COMPLETESCANLINES)
			   : ImageConsumer.RANDOMPIXELORDER)
			: (ImageConsumer.TOPDOWNLEFTRIGHT |
			   ImageConsumer.COMPLETESCANLINES |
			   ImageConsumer.SINGLEPASS |
			   ImageConsumer.SINGLEFRAME));
	}
    }

    private void sendPixels(ImageConsumer ic, int x, int y, int w, int h) {
	int off = pixeloffset + pixelscan * y + x;
	if (isConsumer(ic)) {
	    if (pixels instanceof byte[]) {
		ic.setPixels(x, y, w, h, model,
			     ((byte[]) pixels), off, pixelscan);
	    } else {
		ic.setPixels(x, y, w, h, model,
			     ((int[]) pixels), off, pixelscan);
	    }
	}
    }
}
