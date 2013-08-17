/*
 * @(#)ImageFilter.java	1.18 98/07/01
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

import java.util.Hashtable;

/**
 * This class implements a filter for the set of interface methods that
 * are used to deliver data from an ImageProducer to an ImageConsumer.
 * It is meant to be used in conjunction with a FilteredImageSource
 * object to produce filtered versions of existing images.  It is a
 * base class that provides the calls needed to implement a "Null filter"
 * which has no effect on the data being passed through.  Filters should
 * subclass this class and override the methods which deal with the
 * data that needs to be filtered and modify it as necessary.
 *
 * @see FilteredImageSource
 * @see ImageConsumer
 *
 * @version	1.18 07/01/98
 * @author 	Jim Graham
 */
public class ImageFilter implements ImageConsumer, Cloneable {
    /**
     * The consumer of the particular image data stream for which this
     * instance of the ImageFilter is filtering data.  It is not
     * initialized during the constructor, but rather during the
     * getFilterInstance() method call when the FilteredImageSource
     * is creating a unique instance of this object for a particular
     * image data stream.
     * @see #getFilterInstance
     * @see ImageConsumer
     */
    protected ImageConsumer consumer;

    /**
     * Returns a unique instance of an ImageFilter object which will
     * actually perform the filtering for the specified ImageConsumer.
     * The default implementation just clones this object.
     */
    public ImageFilter getFilterInstance(ImageConsumer ic) {
	ImageFilter instance = (ImageFilter) clone();
	instance.consumer = ic;
	return instance;
    }

    /**
     * Filters the information provided in the setDimensions method
     * of the ImageConsumer interface.
     * @see ImageConsumer#setDimensions
     */
    public void setDimensions(int width, int height) {
	consumer.setDimensions(width, height);
    }

    /**
     * Passes the properties from the source object along after adding a
     * property indicating the stream of filters it has been run through.
     */
    public void setProperties(Hashtable props) {
	props = (Hashtable) props.clone();
	Object o = props.get("filters");
	if (o == null) {
	    props.put("filters", toString());
	} else if (o instanceof String) {
	    props.put("filters", ((String) o)+toString());
	}
	consumer.setProperties(props);
    }

    /**
     * Filter the information provided in the setColorModel method
     * of the ImageConsumer interface.
     * @see ImageConsumer#setColorModel
     */
    public void setColorModel(ColorModel model) {
	consumer.setColorModel(model);
    }

    /**
     * Filters the information provided in the setHints method
     * of the ImageConsumer interface.
     * @see ImageConsumer#setHints
     */
    public void setHints(int hints) {
	consumer.setHints(hints);
    }

    /**
     * Filters the information provided in the setPixels method of the
     * ImageConsumer interface which takes an array of bytes.
     * @see ImageConsumer#setPixels
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	consumer.setPixels(x, y, w, h, model, pixels, off, scansize);
    }

    /**
     * Filters the information provided in the setPixels method of the
     * ImageConsumer interface which takes an array of integers.
     * @see ImageConsumer#setPixels
     */
    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, int pixels[], int off,
			  int scansize) {
	consumer.setPixels(x, y, w, h, model, pixels, off, scansize);
    }

    /**
     * Filters the information provided in the imageComplete method of
     * the ImageConsumer interface.
     * @see ImageConsumer#imageComplete
     */
    public void imageComplete(int status) {
	consumer.imageComplete(status);
    }

    /**
     * Responds to a request for a TopDownLeftRight (TDLR) ordered resend
     * of the pixel data from an ImageConsumer.
     * The ImageFilter can respond to this request in one of three ways.
     * <ol>
     * <li>If the filter can determine that it will forward the pixels in
     * TDLR order if its upstream producer object sends them
     * in TDLR order, then the request is automatically forwarded by
     * default to the indicated ImageProducer using this filter as the
     * requesting ImageConsumer, so no override is necessary.
     * <li>If the filter can resend the pixels in the right order on its
     * own (presumably because the generated pixels have been saved in
     * some sort of buffer), then it can override this method and
     * simply resend the pixels in TDLR order as specified in the
     * ImageProducer API.  <li>If the filter simply returns from this
     * method then the request will be ignored and no resend will
     * occur.  </ol> 
     * @see ImageProducer#requestTopDownLeftRightResend
     * @param ip The ImageProducer that is feeding this instance of
     * the filter - also the ImageProducer that the request should be
     * forwarded to if necessary.
     */
    public void resendTopDownLeftRight(ImageProducer ip) {
	ip.requestTopDownLeftRightResend(this);
    }
    
    /**
     * Clones this object.
     */
    public Object clone() { 
	try { 
	    return super.clone();
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
