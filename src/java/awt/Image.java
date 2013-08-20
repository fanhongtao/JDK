/*
 * @(#)Image.java	1.39 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.ReplicateScaleFilter;

/**
 * The abstract class <code>Image</code> is the superclass of all 
 * classes that represent graphical images. The image must be 
 * obtained in a platform-specific manner.
 *
 * @version 	1.39, 12/19/03
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public abstract class Image {
    
    /**
     * convenience object; we can use this single static object for 
     * all images that do not create their own image caps; it holds the
     * default (unaccelerated) properties.
     */
    private static ImageCapabilities defaultImageCaps = 
	new ImageCapabilities(false);

    /**
     * Priority for accelerating this image.  Subclasses are free to
     * set different default priorities and applications are free to
     * set the priority for specific images via the
     * <code>setAccelerationPriority(float)</code> method.
     * @since 1.5
     */
    protected float accelerationPriority = .5f;

    /**
     * Determines the width of the image. If the width is not yet known, 
     * this method returns <code>-1</code> and the specified   
     * <code>ImageObserver</code> object is notified later.
     * @param     observer   an object waiting for the image to be loaded.
     * @return    the width of this image, or <code>-1</code> 
     *                   if the width is not yet known.
     * @see       java.awt.Image#getHeight
     * @see       java.awt.image.ImageObserver
     */
    public abstract int getWidth(ImageObserver observer);

    /**
     * Determines the height of the image. If the height is not yet known, 
     * this method returns <code>-1</code> and the specified  
     * <code>ImageObserver</code> object is notified later.
     * @param     observer   an object waiting for the image to be loaded.
     * @return    the height of this image, or <code>-1</code> 
     *                   if the height is not yet known.
     * @see       java.awt.Image#getWidth
     * @see       java.awt.image.ImageObserver
     */
    public abstract int getHeight(ImageObserver observer);

    /**
     * Gets the object that produces the pixels for the image.
     * This method is called by the image filtering classes and by 
     * methods that perform image conversion and scaling.
     * @return     the image producer that produces the pixels 
     *                                  for this image.
     * @see        java.awt.image.ImageProducer
     */
    public abstract ImageProducer getSource();

    /**
     * Creates a graphics context for drawing to an off-screen image. 
     * This method can only be called for off-screen images. 
     * @return  a graphics context to draw to the off-screen image. 
     * @exception UnsupportedOperationException if called for a 
     *            non-off-screen image.
     * @see     java.awt.Graphics
     * @see     java.awt.Component#createImage(int, int)
     */
    public abstract Graphics getGraphics();

    /**
     * Gets a property of this image by name. 
     * <p>
     * Individual property names are defined by the various image 
     * formats. If a property is not defined for a particular image, this 
     * method returns the <code>UndefinedProperty</code> object. 
     * <p>
     * If the properties for this image are not yet known, this method 
     * returns <code>null</code>, and the <code>ImageObserver</code> 
     * object is notified later. 
     * <p>
     * The property name <code>"comment"</code> should be used to store 
     * an optional comment which can be presented to the application as a 
     * description of the image, its source, or its author. 
     * @param       name   a property name.
     * @param       observer   an object waiting for this image to be loaded.
     * @return      the value of the named property.
     * @throws      <code>NullPointerException<code> if the property name is null.
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.Image#UndefinedProperty
     */
    public abstract Object getProperty(String name, ImageObserver observer);

    /**
     * The <code>UndefinedProperty</code> object should be returned whenever a
     * property which was not defined for a particular image is fetched.
     */
    public static final Object UndefinedProperty = new Object();

    /**
     * Creates a scaled version of this image.
     * A new <code>Image</code> object is returned which will render 
     * the image at the specified <code>width</code> and 
     * <code>height</code> by default.  The new <code>Image</code> object
     * may be loaded asynchronously even if the original source image
     * has already been loaded completely.  
     *
     * <p>
     * 
     * If either <code>width</code> 
     * or <code>height</code> is a negative number then a value is 
     * substituted to maintain the aspect ratio of the original image 
     * dimensions. If both <code>width</code> and <code>height</code>
     * are negative, then the original image dimensions are used.
     *
     * @param width the width to which to scale the image.
     * @param height the height to which to scale the image.
     * @param hints flags to indicate the type of algorithm to use
     * for image resampling.
     * @return     a scaled version of the image.
     * @exception IllegalArgumentException if <code>width</code>
     *             or <code>height</code> is zero.
     * @see        java.awt.Image#SCALE_DEFAULT
     * @see        java.awt.Image#SCALE_FAST 
     * @see        java.awt.Image#SCALE_SMOOTH
     * @see        java.awt.Image#SCALE_REPLICATE
     * @see        java.awt.Image#SCALE_AREA_AVERAGING
     * @since      JDK1.1
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
     * Use the default image-scaling algorithm.
     * @since JDK1.1
     */
    public static final int SCALE_DEFAULT = 1;

    /**
     * Choose an image-scaling algorithm that gives higher priority
     * to scaling speed than smoothness of the scaled image.
     * @since JDK1.1
     */
    public static final int SCALE_FAST = 2;

    /**
     * Choose an image-scaling algorithm that gives higher priority
     * to image smoothness than scaling speed.
     * @since JDK1.1
     */
    public static final int SCALE_SMOOTH = 4;

    /**
     * Use the image scaling algorithm embodied in the 
     * <code>ReplicateScaleFilter</code> class.  
     * The <code>Image</code> object is free to substitute a different filter 
     * that performs the same algorithm yet integrates more efficiently
     * into the imaging infrastructure supplied by the toolkit.
     * @see        java.awt.image.ReplicateScaleFilter
     * @since      JDK1.1
     */
    public static final int SCALE_REPLICATE = 8;

    /**
     * Use the Area Averaging image scaling algorithm.  The
     * image object is free to substitute a different filter that
     * performs the same algorithm yet integrates more efficiently
     * into the image infrastructure supplied by the toolkit.
     * @see java.awt.image.AreaAveragingScaleFilter
     * @since JDK1.1
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
     * <p>
     * This method always leaves the image in a state such that it can 
     * be reconstructed.  This means the method applies only to cached 
     * or other secondary representations of images such as those that 
     * have been generated from an <tt>ImageProducer</tt> (read from a 
     * file, for example). It does nothing for off-screen images that 
     * have only one copy of their data.
     */
    public abstract void flush();

    /**
     * Returns an ImageCapabilities object which can be
     * inquired as to the capabilities of this
     * Image on the specified GraphicsConfiguration.
     * This allows programmers to find
     * out more runtime information on the specific Image
     * object that they have created.  For example, the user
     * might create a BufferedImage but the system may have
     * no video memory left for creating an image of that
     * size on the given GraphicsConfiguration, so although the object
     * may be acceleratable in general, it is
     * does not have that capability on this GraphicsConfiguration.
     * @param gc a <code>GraphicsConfiguration</code> object.  A value of null
     * for this parameter will result in getting the image capabilities
     * for the default <code>GraphicsConfiguration</code>.
     * @return an <code>ImageCapabilities</code> object that contains
     * the capabilities of this <code>Image</code> on the specified
     * GraphicsConfiguration.
     * @see #java.awt.image.VolatileImage.getCapabilities()
     * VolatileImage.getCapabilities()
     * @since 1.5
     */
    public ImageCapabilities getCapabilities(GraphicsConfiguration gc) {
	// Note: this is just a default object that gets returned by the
	// base Image object.  Subclasses of Image should override this
	// method and return an ImageCapabilities object that is appropriate
	// for a given instance of that subclass.
	return defaultImageCaps;
    }
    
    /**
     * Sets a hint for this image about how important acceleration is.
     * This priority hint is used to compare to the priorities of other
     * Image objects when determining how to use scarce acceleration
     * resources such as video memory.  When and if it is possible to
     * accelerate this Image, if there are not enough resources available
     * to provide that acceleration but enough can be freed up by
     * de-acceleration some other image of lower priority, then that other
     * Image may be de-accelerated in deference to this one.  Images
     * that have the same priority take up resources on a first-come,
     * first-served basis.
     * @param priority a value between 0 and 1, inclusive, where higher
     * values indicate more importance for acceleration.  A value of 0
     * means that this Image should never be accelerated.  Other values
     * are used simply to determine acceleration priority relative to other
     * Images.
     * @throws IllegalArgumentException if <code>priority</code> is less
     * than zero or greater than 1.
     * @since 1.5
     */
    public void setAccelerationPriority(float priority) {
        if (priority < 0 || priority > 1) {
            throw new IllegalArgumentException("Priority must be a value " +
					       "between 0 and 1, inclusive");
        }
	accelerationPriority = priority;
    }

    /**
     * Returns the current value of the acceleration priority hint.
     * @see #setAccelerationPriority(float priority) setAccelerationPriority
     * @return value between 0 and 1, inclusive, which represents the current
     * priority value
     * @since 1.5
     */
    public float getAccelerationPriority() {
	return accelerationPriority;
    }
}
