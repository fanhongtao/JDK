/*
 * @(#)FilteredImageSource.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.Image;
import java.awt.image.ImageFilter;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import java.awt.image.ColorModel;

/**
 * This class is an implementation of the ImageProducer interface which
 * takes an existing image and a filter object and uses them to produce
 * image data for a new filtered version of the original image.
 * Here is an example which filters an image by swapping the red and
 * blue compents:
 * <pre>
 * 
 *	Image src = getImage("doc:///demo/images/duke/T1.gif");
 *	ImageFilter colorfilter = new RedBlueSwapFilter();
 *	Image img = createImage(new FilteredImageSource(src.getSource(),
 *							colorfilter));
 * 
 * </pre>
 *
 * @see ImageProducer
 *
 * @version	1.25 01/23/03
 * @author 	Jim Graham
 */
public class FilteredImageSource implements ImageProducer {
    ImageProducer src;
    ImageFilter filter;

    /**
     * Constructs an ImageProducer object from an existing ImageProducer
     * and a filter object.
     * @param orig the specified <code>ImageProducer</code>
     * @param imgf the specified <code>ImageFilter</code>
     * @see ImageFilter
     * @see java.awt.Component#createImage
     */
    public FilteredImageSource(ImageProducer orig, ImageFilter imgf) {
	src = orig;
	filter = imgf;
    }

    private Hashtable proxies;

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void addConsumer(ImageConsumer ic) {
	if (proxies == null) {
	    proxies = new Hashtable();
	}
	if (!proxies.containsKey(ic)) {
	    ImageFilter imgf = filter.getFilterInstance(ic);
	    proxies.put(ic, imgf);
	    src.addConsumer(imgf);
	}
    }

    /**
     * Determines whether an ImageConsumer is on the list of consumers 
     * currently interested in data for this image.
     * @param ic the specified <code>ImageConsumer</code>
     * @return true if the ImageConsumer is on the list; false otherwise
     * @see ImageConsumer
     */
    public synchronized boolean isConsumer(ImageConsumer ic) {
	return (proxies != null && proxies.containsKey(ic));
    }

    /**
     * Removes an ImageConsumer from the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void removeConsumer(ImageConsumer ic) {
	if (proxies != null) {
	    ImageFilter imgf = (ImageFilter) proxies.get(ic);
	    if (imgf != null) {
		src.removeConsumer(imgf);
		proxies.remove(ic);
		if (proxies.isEmpty()) {
		    proxies = null;
		}
	    }
	}
    }

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image, and immediately starts delivery of the
     * image data through the ImageConsumer interface.
     * @see ImageConsumer
     */
    public void startProduction(ImageConsumer ic) {
	if (proxies == null) {
	    proxies = new Hashtable();
	}
	ImageFilter imgf = (ImageFilter) proxies.get(ic);
	if (imgf == null) {
	    imgf = filter.getFilterInstance(ic);
	    proxies.put(ic, imgf);
	}
	src.startProduction(imgf);
    }

    /**
     * Requests that a given ImageConsumer have the image data delivered
     * one more time in top-down, left-right order.  The request is
     * handed to the ImageFilter for further processing, since the
     * ability to preserve the pixel ordering depends on the filter.
     * @see ImageConsumer
     */
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	if (proxies != null) {
	    ImageFilter imgf = (ImageFilter) proxies.get(ic);
	    if (imgf != null) {
		imgf.resendTopDownLeftRight(src);
	    }
	}
    }
}
