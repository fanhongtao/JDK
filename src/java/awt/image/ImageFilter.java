/*
 * @(#)ImageFilter.java	1.26 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version	1.26 01/23/03
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
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere
     * with the filtering operation.
     * @param ic the specified <code>ImageConsumer</code>
     * @return an <code>ImageFilter</code> used to perform the 
     *         filtering for the specified <code>ImageConsumer</code>.
     */
    public ImageFilter getFilterInstance(ImageConsumer ic) {
	ImageFilter instance = (ImageFilter) clone();
	instance.consumer = ic;
	return instance;
    }

    /**
     * Filters the information provided in the setDimensions method
     * of the ImageConsumer interface.
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
     * @see ImageConsumer#setDimensions
     */
    public void setDimensions(int width, int height) {
	consumer.setDimensions(width, height);
    }

    /**
     * Passes the properties from the source object along after adding a
     * property indicating the stream of filters it has been run through.
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
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
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
     * @see ImageConsumer#setColorModel
     */
    public void setColorModel(ColorModel model) {
	consumer.setColorModel(model);
    }

    /**
     * Filters the information provided in the setHints method
     * of the ImageConsumer interface.
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
     * @see ImageConsumer#setHints
     */
    public void setHints(int hints) {
	consumer.setHints(hints);
    }

    /**
     * Filters the information provided in the setPixels method of the
     * ImageConsumer interface which takes an array of bytes.
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
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
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
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
     * <p>
     * Note: This method is intended to be called by the ImageProducer
     * of the Image whose pixels are being filtered.  Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere      
     * with the filtering operation.  
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
