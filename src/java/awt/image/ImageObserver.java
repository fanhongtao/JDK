/*
 * @(#)ImageObserver.java	1.18 98/07/01
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

import java.awt.Image;

/**
 * An asynchronous update interface for receiving notifications about
 * Image information as the Image is constructed.
 *
 * @version 	1.18 07/01/98
 * @author 	Jim Graham
 */
public interface ImageObserver {
    /**
     * This method is called when information about an image which was
     * previously requested using an asynchronous interface becomes
     * available.  Asynchronous interfaces are method calls such as
     * getWidth(ImageObserver) and drawImage(img, x, y, ImageObserver)
     * which take an ImageObserver object as an argument.  Those methods
     * register the caller as interested either in information about
     * the overall image itself (in the case of getWidth(ImageObserver))
     * or about an output version of an image (in the case of the
     * drawImage(img, x, y, [w, h,] ImageObserver) call).  

     * <p>This method
     * should return true if further updates are needed or false if the
     * required information has been acquired.  The image which was being
     * tracked is passed in using the img argument.  Various constants
     * are combined to form the infoflags argument which indicates what
     * information about the image is now available.  The interpretation
     * of the x, y, width, and height arguments depends on the contents
     * of the infoflags argument.
     * @see Image#getWidth
     * @see Image#getHeight
     * @see java.awt.Graphics#drawImage
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height);

    /**
     * The width of the base image is now available and can be taken
     * from the width argument to the imageUpdate callback method.
     * @see Image#getWidth
     * @see #imageUpdate
     */
    public static final int WIDTH = 1;

    /**
     * The height of the base image is now available and can be taken
     * from the height argument to the imageUpdate callback method.
     * @see Image#getHeight
     * @see #imageUpdate
     */
    public static final int HEIGHT = 2;

    /**
     * The properties of the image are now available.
     * @see Image#getProperty
     * @see #imageUpdate
     */
    public static final int PROPERTIES = 4;

    /**
     * More pixels needed for drawing a scaled variation of the image
     * are available.  The bounding box of the new pixels can be taken
     * from the x, y, width, and height arguments to the imageUpdate
     * callback method.
     * @see java.awt.Graphics#drawImage
     * @see #imageUpdate
     */
    public static final int SOMEBITS = 8;

    /**
     * Another complete frame of a multi-frame image which was previously
     * drawn is now available to be drawn again.  The x, y, width, and height
     * arguments to the imageUpdate callback method should be ignored.
     * @see java.awt.Graphics#drawImage
     * @see #imageUpdate
     */
    public static final int FRAMEBITS = 16;

    /**
     * A static image which was previously drawn is now complete and can
     * be drawn again in its final form.  The x, y, width, and height
     * arguments to the imageUpdate callback method should be ignored.
     * @see java.awt.Graphics#drawImage
     * @see #imageUpdate
     */
    public static final int ALLBITS = 32;

    /**
     * An image which was being tracked asynchronously has encountered
     * an error.  No further information will become available and
     * drawing the image will fail.
     * As a convenience, the ABORT flag will be indicated at the same
     * time to indicate that the image production was aborted.
     * @see #imageUpdate
     */
    public static final int ERROR = 64;

    /**
     * An image which was being tracked asynchronously was aborted before
     * production was complete.  No more information will become available
     * without further action to trigger another image production sequence.
     * If the ERROR flag was not also set in this image update, then
     * accessing any of the data in the image will restart the production
     * again, probably from the beginning.
     * @see #imageUpdate
     */
    public static final int ABORT = 128;
}
