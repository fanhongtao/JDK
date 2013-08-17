/*
 * @(#)Image.java	1.22 97/02/12
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
package java.awt;

import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.ReplicateScaleFilter;

/**
 * The image class is an abstract class. The image must be obtained in a 
 * platform specific way.
 *
 * @version 	1.22, 02/12/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public abstract class Image {
    /**
     * Gets the actual width of the image.  If the width is not known
     * yet then the ImageObserver will be notified later and -1 will
     * be returned.
     * @see #getHeight
     * @see ImageObserver
     */
    public abstract int getWidth(ImageObserver observer);

    /**
     * Gets the actual height of the image.  If the height is not known
     * yet then the ImageObserver will be notified later and -1 will
     * be returned.
     * @see #getWidth
     * @see ImageObserver
     */
    public abstract int getHeight(ImageObserver observer);

    /**
     * Gets the object that produces the pixels for the image.
     * This is used by the Image filtering classes and by the
     * image conversion and scaling code.
     * @see ImageProducer
     */
    public abstract ImageProducer getSource();

    /**
     * Gets a graphics object to draw into this image.
     * This will only work for off-screen images.
     * @see Graphics
     */
    public abstract Graphics getGraphics();

    /**
     * Gets a property of the image by name.  Individual property names
     * are defined by the various image formats.  If a property is not
     * defined for a particular image, this method will return the
     * UndefinedProperty object.  If the properties for this image are
     * not yet known, then this method will return null and the ImageObserver
     * object will be notified later.  The property name "comment" should
     * be used to store an optional comment which can be presented to
     * the user as a description of the image, its source, or its author.
     * @see ImageObserver
     * @see #UndefinedProperty
     */
    public abstract Object getProperty(String name, ImageObserver observer);

    /**
     * The UndefinedProperty object should be returned whenever a
     * property which was not defined for a particular image is
     * fetched.
     */
    public static final Object UndefinedProperty = new Object();

    /**
     * Returns a scaled version of this image.
     * A new Image object is returned which will render the image at
     * the specified width and height by default.  The new Image object
     * may be loaded asynchronously even if the original source image
     * has already been loaded completely.  If either the width or
     * height is a negative number then a value is substituted to
     * maintain the aspect ratio of the original image dimensions.
     * @param width the width to stretch the image to
     * @param height the height to stretch the image to
     * @param hints flags to indicate the type of algorithm to use
     * for image resampling
     */
    public Image getScaledInstance(int width, int height, int hints) {
	ImageFilter filter;
	if ((hints & (SCALE_SMOOTH | SCALE_AREA_AVERAGING)) != 0) {
	    filter = new AreaAveragingScaleFilter(width, height);
	} else {
	    filter = new ReplicateScaleFilter(width, height);
	}
	ImageProducer prod;
	prod = new FilteredImageSource(getSource(), filter);
	return Toolkit.getDefaultToolkit().createImage(prod);
    }

    /**
     * Use the default image scaling algorithm.
     */
    public static final int SCALE_DEFAULT = 1;

    /**
     * Choose an image scaling algorithm that gives higher priority
     * to scaling speed than smoothness of the scaled image.
     */
    public static final int SCALE_FAST = 2;

    /**
     * Choose an image scaling algorithm that gives higher priority
     * to image smoothness than scaling speed.
     */
    public static final int SCALE_SMOOTH = 4;

    /**
     * Use the ReplicateScaleFilter image scaling algorithm.  The
     * image object is free to substitute a different filter that
     * performs the same algorithm yet integrates more efficiently
     * into the image infrastructure supplied by the toolkit.
     * @see java.awt.image.ReplicateScaleFilter
     */
    public static final int SCALE_REPLICATE = 8;

    /**
     * Use the Area Averaging image scaling algorithm.  The
     * image object is free to substitute a different filter that
     * performs the same algorithm yet integrates more efficiently
     * into the image infrastructure supplied by the toolkit.
     * @see java.awt.image.AreaAveragingScaleFilter
     */
    public static final int SCALE_AREA_AVERAGING = 16;

    /**
     * Flushes all resources being used by this Image object.  This
     * includes any pixel data that is being cached for rendering to
     * the screen as well as any system resources that are being used
     * to store data or pixels for the image.  The image is reset to
     * a state similar to when it was first created so that if it is
     * again rendered, the image data will have to be recreated or
     * fetched again from its source.
     */
    public abstract void flush();
}
