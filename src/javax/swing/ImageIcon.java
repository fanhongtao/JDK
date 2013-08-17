/*
 * @(#)ImageIcon.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of the Icon interface that paints Icons
 * from Images. Images that are created from a URL or filename
 * are preloaded using MediaTracker to monitor the loaded state
 * of the image.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 * 
 * @version 1.33 08/28/98
 * @author Jeff Dinkins
 */
public class ImageIcon implements Icon, Serializable
{
    transient Image image;
    transient int loadStatus = 0;
    ImageObserver imageObserver;
    String description = null;

    protected final static Component component = new Component() {};
    protected final static MediaTracker tracker = new MediaTracker(component);

    int width = -1;
    int height = -1;

    /**
     * Creates an ImageIcon from the specified file. The image will
     * be preloaded by using MediaTracker to monitor the loading state
     * of the image.
     * @param filename the name of the file containing the image
     * @param description a brief textual description of the image
     * @see #ImageIcon(String)
     */
    public ImageIcon(String filename, String description) {
	image = Toolkit.getDefaultToolkit().getImage(filename);
        this.description = description;
	loadImage(image);
    }

    /**
     * Creates an ImageIcon from the specified file. The image will
     * be preloaded by using MediaTracker to monitor the loading state
     * of the image. The specified String can be a file name or a
     * file path. When specifying a path, use the Internet-standard
     * forward-slash ("/") as a separator. For example, specify:<pre>
     *    new ImageIcon("images/myImage.gif")
     * </pre>
     * (The string is converted to an URL, so the forward-slash works
     * on all systems.)
     *
     * @param filename a String specifying a filename or path
     */
    public ImageIcon (String filename) {
        this(filename, filename);
    }

    /**
     * Creates an ImageIcon from the specified URL. The image will
     * be preloaded by using MediaTracker to monitor the loaded state
     * of the image.
     * @param URL the URL for the image
     * @param description a brief textual description of the image
     * @see #ImageIcon(String)
     */
    public ImageIcon(URL location, String description) {
	image = Toolkit.getDefaultToolkit().getImage(location);
        this.description = description;
	loadImage(image);
    }

    /**
     * Creates an ImageIcon from the specified URL. The image will
     * be preloaded by using MediaTracker to monitor the loaded state
     * of the image.
     */
    public ImageIcon (URL location) {
        this(location, location.toExternalForm());
    }

    /**
     * Creates an ImageIcon from the image. 
     * @param image the image
     * @param description a brief textual description of the image
     */
    public ImageIcon(Image image, String description) {
        this(image);
        this.description = description;
    }

    /**
     * Creates an ImageIcon from an image object. 
     */
    public ImageIcon (Image image) {
	this.image = image;
        Object o = image.getProperty("comment", imageObserver);
        if (o instanceof String) {
            description = (String) o;
        }
	loadImage(image);
    }

    /**
     * Creates an ImageIcon from an array of bytes which were
     * read from an image file containing a supported image format,
     * such as GIF or JPEG.  Normally this array is created
     * by reading an image using Class.getResourceAsStream(), but
     * the byte array may also be statically stored in a class.
     *
     * @param  imageData an array of pixels in an image format supported
     *         by the AWT Toolkit, such as GIF or JPEG.
     * @param  description a brief textual description of the image
     * @see    java.awt.Toolkit#createImage
     */
    public ImageIcon (byte[] imageData, String description) {
	this.image = Toolkit.getDefaultToolkit().createImage(imageData);
        if (image == null) {
            return;
        }
        this.description = description;
	loadImage(image);
    }

    /**
     * Creates an ImageIcon from an array of bytes which were
     * read from an image file containing a supported image format,
     * such as GIF or JPEG.  Normally this array is created
     * by reading an image using Class.getResourceAsStream(), but
     * the byte array may also be statically stored in a class.
     *
     * @param  an array of pixels in an image format supported by
     *         the AWT Toolkit, such as GIF or JPEG.
     * @see    java.awt.Toolkit#createImage
     */
    public ImageIcon (byte[] imageData) {
	this.image = Toolkit.getDefaultToolkit().createImage(imageData);
        if (image == null) {
            return;
        }
        Object o = image.getProperty("comment", imageObserver);
        if (o instanceof String) {
            description = (String) o;
        }
	loadImage(image);
    }

    /**
     * Creates an uninitialized image icon.
     */
    public ImageIcon() {
    }

    /**
     * Wait for the image to load
     */
    protected void loadImage(Image image) {
	synchronized(tracker) {
	    tracker.addImage(image, 0);
	    try {
		tracker.waitForID(0, 5000);
	    } catch (InterruptedException e) {
		System.out.println("INTERRUPTED while loading Image");
	    }
            loadStatus = tracker.statusID(0, false);
	    tracker.removeImage(image, 0);

	    width = image.getWidth(imageObserver);
	    height = image.getHeight(imageObserver);
	}
    }

    /**
     * Returns the status of the image loading operation.
     * @return the loading status as defined by java.awt.MediaTracker.
     * @see java.awt.MediaTracker#ABORTED
     * @see java.awt.MediaTracker#ERRORED
     * @see java.awt.MediaTracker#COMPLETE
     */
    public int getImageLoadStatus() {
        return loadStatus;
    }

    /**
     * Returns the Icon's Image
     */
    public Image getImage() {
	return image;
    }

    /**
     * Set the image displayed by this icon.
     */
    public void setImage(Image image) {
	this.image = image;
	loadImage(image);
    }

    /**
     * Get the description of the image.  This is meant to be a brief
     * textual description of the object.  For example, it might be
     * presented to a blind user to give an indication of the purpose
     * of the image.
     */
    public String getDescription() {
	return description;
    }

    /**
     * Set the description of the image.  This is meant to be a brief
     * textual description of the object.  For example, it might be
     * presented to a blind user to give an indication of the purpose
     * of the image.
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Paints the Icon
     */
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        if(imageObserver == null) {
           g.drawImage(image, x, y, c);
        } else {
	   g.drawImage(image, x, y, imageObserver);
        }
    }

    /**
     * Get the width of the Icon
     */
    public int getIconWidth() {
	return width;
    }

    /**
     * Get the height of the Icon
     */
    public int getIconHeight() {
	return height;
    }

    /** 
     * Set the image observer for the image.  Set this
     * property if the ImageIcon contains an animated GIF, so
     * the observer is notified to update its display.
     * For example:
     * <pre>
     *     icon = new ImageIcon(...)
     *     button.setIcon(icon);
     *     icon.setImageObserver(button);
     * </pre>
     */
    public void setImageObserver(ImageObserver observer) {
        imageObserver = observer;
    }

    /**
     *  Return the umage observer for the image 
     */
    public ImageObserver getImageObserver() {
        return imageObserver;
    }


    private void readObject(ObjectInputStream s)
	throws ClassNotFoundException, IOException 
    {
	s.defaultReadObject();
    
	int w = s.readInt();
	int h = s.readInt();
	int[] pixels = (int[])(s.readObject());

        if (pixels != null) {
	    Toolkit tk = Toolkit.getDefaultToolkit();
	    ColorModel cm = ColorModel.getRGBdefault();
	    image = tk.createImage(new MemoryImageSource(w, h, cm, pixels, 0, w));
        } 
    }


    private void writeObject(ObjectOutputStream s) 
	throws IOException 
    {
	s.defaultWriteObject();

	int w = getIconWidth();
	int h = getIconHeight();
	int[] pixels = image != null? new int[w * h] : null;

        if (image != null) {
	    try {
	        PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
	        pg.grabPixels();
	        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
		    throw new IOException("failed to load image contents");
	        }
	    }
	    catch (InterruptedException e) {
	        throw new IOException("image load interrupted");
	    }
        }
    
	s.writeInt(w);
	s.writeInt(h);
	s.writeObject(pixels);
    }
}

