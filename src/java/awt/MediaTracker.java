/*
 * @(#)MediaTracker.java	1.25 97/03/13
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

import java.awt.Component;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 * A utility class to track the status of a number of media objects.
 * Media objects could include images as well as audio clips, though
 * currently only images are supported.  To use it, simply create an
 * instance and then call addImage() for each image to be tracked.
 * Each image can be assigned a unique ID for indentification purposes.
 * The IDs control the priority order in which the images are fetched
 * as well as identifying unique subsets of the images that can be
 * waited on independently.  Here is an example:
 * <pre>
 *
 * import java.applet.Applet;
 * import java.awt.Color;
 * import java.awt.Image;
 * import java.awt.Graphics;
 * import java.awt.MediaTracker;
 *
 * public class ImageBlaster extends Applet implements Runnable {
 *	MediaTracker tracker;
 *	Image bg;
 *	Image anim[] = new Image[5];
 *	int index;
 *	Thread animator;
 *
 *	// Get the images for the background (id == 0) and the animation
 *	// frames (id == 1) and add them to the MediaTracker
 *	public void init() {
 *	    tracker = new MediaTracker(this);
 *	    bg = getImage(getDocumentBase(), "images/background.gif");
 *	    tracker.addImage(bg, 0);
 *	    for (int i = 0; i < 5; i++) {
 *		anim[i] = getImage(getDocumentBase(), "images/anim"+i+".gif");
 *		tracker.addImage(anim[i], 1);
 *	    }
 *	}
 *	// Start the animation thread.
 *	public void start() {
 *	    animator = new Thread(this);
 *	    animator.start();
 *	}
 *	// Stop the animation thread.
 *	public void stop() {
 *	    animator.stop();
 *	    animator = null;
 *	}
 *	// Run the animation thread.
 *	// First wait for the background image to fully load and paint.
 *	// Then wait for all of the animation frames to finish loading.
 *	// Finally loop and increment the animation frame index.
 *	public void run() {
 *	    try {
 *		tracker.waitForID(0);
 *		tracker.waitForID(1);
 *	    } catch (InterruptedException e) {
 *		return;
 *	    }
 *	    Thread me = Thread.currentThread();
 *	    while (animator == me) {
 *		try {
 *		    Thread.sleep(100);
 *		} catch (InterruptedException e) {
 *		    break;
 *		}
 *		synchronized (this) {
 *		    index++;
 *		    if (index >= anim.length) {
 *			index = 0;
 *		    }
 *		}
 *		repaint();
 *	    }
 *	}
 *	// The background image fills our frame so we don't need to clear
 *	// the applet on repaints, just call the paint method.
 *	public void update(Graphics g) {
 *	    paint(g);
 *	}
 *	// Paint a large red rectangle if there are any errors loading the
 *	// images.  Otherwise always paint the background so that it appears
 *	// incrementally as it is loading.  Finally, only paint the current
 *	// animation frame if all of the frames (id == 1) are done loading
 *	// so that we don't get partial animations.
 *	public void paint(Graphics g) {
 *	    if ((tracker.statusAll(false) & MediaTracker.ERRORED) != 0) {
 *		g.setColor(Color.red);
 *		g.fillRect(0, 0, size().width, size().height);
 *		return;
 *	    }
 *	    g.drawImage(bg, 0, 0, this);
 *	    if (tracker.statusID(1, false) == MediaTracker.COMPLETE) {
 *		g.drawImage(anim[index], 10, 10, this);
 *	    }
 *	}
 * }
 *
 * </pre>
 *
 * @version 	1.25, 03/13/97
 * @author 	Jim Graham
 */
public class MediaTracker implements java.io.Serializable {
    Component target;
    MediaEntry head;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -483174189758638095L;

    /**
     * Creates a Media tracker to track images for a given Component.
     * @param comp the component on which the images will eventually be drawn
     */
    public MediaTracker(Component comp) {
	target = comp;
    }

    /**
     * Adds an image to the list of images being tracked.  The image
     * will eventually be rendered at its default (unscaled) size.
     * @param image the image to be tracked
     * @param id the identifier used to later track this image
     */
    public void addImage(Image image, int id) {
	addImage(image, id, -1, -1);
    }

    /**
     * Adds a scaled image to the list of images being tracked.  The
     * image will eventually be rendered at the indicated size.
     * @param image the image to be tracked
     * @param id the identifier used to later track this image
     * @param w the width that the image will be rendered at
     * @param h the height that the image will be rendered at
     */
    public synchronized void addImage(Image image, int id, int w, int h) {
	head = MediaEntry.insert(head,
				 new ImageMediaEntry(this, image, id, w, h));
    }

    /**
     * Flag indicating some media is currently being loaded.
     * @see #statusAll
     * @see #statusID
     */
    public static final int LOADING = 1;

    /**
     * Flag indicating the download of some media was aborted.
     * @see #statusAll
     * @see #statusID
     */
    public static final int ABORTED = 2;

    /**
     * Flag indicating the download of some media encountered an error.
     * @see #statusAll
     * @see #statusID
     */
    public static final int ERRORED = 4;

    /**
     * Flag indicating the download of media completed successfully.
     * @see #statusAll
     * @see #statusID
     */
    public static final int COMPLETE = 8;

    static final int DONE = (ABORTED | ERRORED | COMPLETE);

    /**
     * Checks to see if all images have finished loading but does not start
     * loading the images if they are not already loading.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use isErrorAny() or isErrorID() to check for errors.
     * @return true if all images have finished loading, were aborted or
     * encountered an error
     * @see #checkAll(boolean)
     * @see #checkID
     * @see #isErrorAny
     * @see #isErrorID
     */
    public boolean checkAll() {
	return checkAll(false, true);
    }

    /**
     * Checks to see if all images have finished loading. If load is
     * true, starts loading any images that are not yet being loaded.
     * If there is an error while loading or scaling an image then
     * that image is considered "complete."  Use isErrorAny() or
     * isErrorID() to check for errors.

     * @param load start loading the images if this parameter is true
     * @return true if all images have finished loading, were aborted or
     * encountered an error
     * @see #isErrorAny
     * @see #isErrorID
     * @see #checkID(int, boolean)
     * @see #checkAll()
     */
    public boolean checkAll(boolean load) {
	return checkAll(load, true);
    }

    private synchronized boolean checkAll(boolean load, boolean verify) {
	MediaEntry cur = head;
	boolean done = true;
	while (cur != null) {
	    if ((cur.getStatus(load, verify) & DONE) == 0) {
		done = false;
	    }
	    cur = cur.next;
	}
	return done;
    }

    /**
     * Checks the error status of all of the images.
     * @return true if any of the images had an error during loading
     * @see #isErrorID
     * @see #getErrorsAny
     */
    public synchronized boolean isErrorAny() {
	MediaEntry cur = head;
	while (cur != null) {
	    if ((cur.getStatus(false, true) & ERRORED) != 0) {
		return true;
	    }
	    cur = cur.next;
	}
	return false;
    }

    /**
     * Returns a list of all media that have encountered an error.
     * @return an array of media objects or null if there are no errors
     * @see #isErrorAny
     * @see #getErrorsID
     */
    public synchronized Object[] getErrorsAny() {
	MediaEntry cur = head;
	int numerrors = 0;
	while (cur != null) {
	    if ((cur.getStatus(false, true) & ERRORED) != 0) {
		numerrors++;
	    }
	    cur = cur.next;
	}
	if (numerrors == 0) {
	    return null;
	}
	Object errors[] = new Object[numerrors];
	cur = head;
	numerrors = 0;
	while (cur != null) {
	    if ((cur.getStatus(false, false) & ERRORED) != 0) {
		errors[numerrors++] = cur.getMedia();
	    }
	    cur = cur.next;
	}
	return errors;
    }

    /**
     * Starts loading all images. Waits until they have finished loading,
     * are aborted, or it receives an error.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use isErrorAny() or statusAll() to check for errors.
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     * @see #waitForID
     * @see #waitForAll(long)
     * @see #isErrorAny
     * @see #isErrorID
     */
    public void waitForAll() throws InterruptedException {
	waitForAll(0);
    }

    /**
     * Starts loading all images. Waits until they have finished loading,
     * are aborted, it receives an error, or until the specified timeout has
     * elapsed.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use isErrorAny() or statusAll() to check for errors.
     * @param ms the length of time to wait for the loading to complete
     * @return true if all images were successfully loaded
     * @see #waitForID
     * @see #waitForAll()
     * @see #isErrorAny
     * @see #isErrorID
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public synchronized boolean waitForAll(long ms)
	throws InterruptedException
    {
	long end = System.currentTimeMillis() + ms;
	boolean first = true;
	while (true) {
	    int status = statusAll(first, first);
	    if ((status & LOADING) == 0) {
		return (status == COMPLETE);
	    }
	    first = false;
	    long timeout;
	    if (ms == 0) {
		timeout = 0;
	    } else {
		timeout = end - System.currentTimeMillis();
		if (timeout <= 0) {
		    return false;
		}
	    }
	    wait(timeout);
	}
    }

    /**
     * Returns the boolean OR of the status of all of the media being
     * tracked.
     * @param load specifies whether to start the media loading
     * @see #statusID
     * @see #LOADING
     * @see #ABORTED
     * @see #ERRORED
     * @see #COMPLETE
     */
    public int statusAll(boolean load) {
	return statusAll(load, true);
    }

    private synchronized int statusAll(boolean load, boolean verify) {
	MediaEntry cur = head;
	int status = 0;
	while (cur != null) {
	    status = status | cur.getStatus(load, verify);
	    cur = cur.next;
	}
	return status;
    }

    /**
     * Checks to see if all images tagged with the indicated ID have
     * finished loading, but does not start loading the images if they
     * are not already loading.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use isErrorAny() or isErrorID() to check for errors.
     * @param id the identifier used to determine which images to check
     * @return true if all tagged images have finished loading, were
     * aborted, or an error occurred.
     * @see #checkID(int, boolean)
     * @see #checkAll
     * @see #isErrorAny
     * @see #isErrorID
     */
    public boolean checkID(int id) {
	return checkID(id, false, true);
    }

    /**
     * Checks to see if all images tagged with the indicated ID have
     * finished loading. If load is true, starts loading any images
     * with that ID that are not yet being loaded.  If there is an
     * error while loading or scaling an image then that image is
     * considered "complete."  Use isErrorAny() or isErrorID() to
     * check for errors.

     * @param id the identifier used to determine which images to check
     * @param load start loading the images if this parameter is true
     * @return true if all tagged images have finished loading, were
     * aborted, or an error occurred
     * @see #checkID(int)
     * @see #checkAll
     * @see #isErrorAny
     * @see #isErrorID
     */
    public boolean checkID(int id, boolean load) {
	return checkID(id, load, true);
    }

    private synchronized boolean checkID(int id, boolean load, boolean verify)
    {
	MediaEntry cur = head;
	boolean done = true;
	while (cur != null) {
	    if (cur.getID() == id
		&& (cur.getStatus(load, verify) & DONE) == 0)
	    {
		done = false;
	    }
	    cur = cur.next;
	}
	return done;
    }

    /**
     * Checks the error status of all of the images with the specified ID.
     * @param id the identifier used to determine which images to check
     * @return true if any of the tagged images had an error during loading
     * @see #isErrorAny
     * @see #getErrorsID
     */
    public synchronized boolean isErrorID(int id) {
	MediaEntry cur = head;
	while (cur != null) {
	    if (cur.getID() == id
		&& (cur.getStatus(false, true) & ERRORED) != 0)
	    {
		return true;
	    }
	    cur = cur.next;
	}
	return false;
    }

    /**
     * Returns a list of media with the specified ID that have encountered
     * an error.
     * @param id the identifier used to determine which images to return
     * @return an array of media objects or null if there are no errors
     * @see #isErrorID
     * @see #getErrorsAny
     */
    public synchronized Object[] getErrorsID(int id) {
	MediaEntry cur = head;
	int numerrors = 0;
	while (cur != null) {
	    if (cur.getID() == id
		&& (cur.getStatus(false, true) & ERRORED) != 0)
	    {
		numerrors++;
	    }
	    cur = cur.next;
	}
	if (numerrors == 0) {
	    return null;
	}
	Object errors[] = new Object[numerrors];
	cur = head;
	numerrors = 0;
	while (cur != null) {
	    if (cur.getID() == id
		&& (cur.getStatus(false, false) & ERRORED) != 0)
	    {
		errors[numerrors++] = cur.getMedia();
	    }
	    cur = cur.next;
	}
	return errors;
    }

    /**
     * Starts loading all images with the specified ID and waits until they
     * have finished loading or receive an error.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use statusID() or isErrorID() to check for errors.
     * @param id the identifier used to determine which images to wait for
     * @see #waitForAll
     * @see #waitForID(int, long)
     * @see #isErrorAny
     * @see #isErrorID
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public void waitForID(int id) throws InterruptedException {
	waitForID(id, 0);
    }

    /**
     * Starts loading all images with the specified ID. Waits until they
     * have finished loading, an error occurs, or the specified
     * timeout has elapsed.
     * If there is an error while loading or scaling an image then that
     * image is considered "complete."
     * Use statusID or isErrorID to check for errors.
     * @param id the identifier used to determine which images to wait for
     * @param ms the length of time to wait for the loading to complete
     * @see #waitForAll
     * @see #waitForID(int)
     * @see #isErrorAny
     * @see #isErrorID
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public synchronized boolean waitForID(int id, long ms)
	throws InterruptedException
    {
	long end = System.currentTimeMillis() + ms;
	boolean first = true;
	while (true) {
	    int status = statusID(id, first, first);
	    if ((status & LOADING) == 0) {
		return (status == COMPLETE);
	    }
	    first = false;
	    long timeout;
	    if (ms == 0) {
		timeout = 0;
	    } else {
		timeout = end - System.currentTimeMillis();
		if (timeout <= 0) {
		    return false;
		}
	    }
	    wait(timeout);
	}
    }

    /**
     * Returns the boolean OR of the status of all of the media with
     * a given ID.
     * @param id the identifier used to determine which images to check
     * @param load specifies whether to start the media loading
     * @see #statusAll
     * @see #LOADING
     * @see #ABORTED
     * @see #ERRORED
     * @see #COMPLETE
     */
    public int statusID(int id, boolean load) {
	return statusID(id, load, true);
    }

    private synchronized int statusID(int id, boolean load, boolean verify) {
	MediaEntry cur = head;
	int status = 0;
	while (cur != null) {
	    if (cur.getID() == id) {
		status = status | cur.getStatus(load, verify);
	    }
	    cur = cur.next;
	}
	return status;
    }

    /**
     * Remove the specified Image from this MediaTracker.
     * All instances of the image are removed regardless of scale or ID.
     * @param image the image to be removed
     * @see #removeImage(java.awt.Image, int id)
     * @see #removeImage(java.awt.Image, int id, int width, int height)
     */
    public synchronized void removeImage(Image image) {
	MediaEntry cur = head;
	MediaEntry prev = null;
	while (cur != null) {
	    MediaEntry next = cur.next;
	    if (cur.getMedia() == image) {
		if (prev == null) {
		    head = next;
		} else {
		    prev.next = next;
		}
		cur.cancel();
	    } else {
		prev = cur;
	    }
	    cur = next;
	}
	notifyAll();	// Notify in case remaining images are "done".
    }

    /**
     * Remove the specified Image from the specified ID of this MediaTracker.
     * All instances of the image being tracked under the given ID are
     * removed regardless of scale.
     * @param image the image to be removed
     * @param id the tracking ID to remove the image from
     * @see #removeImage(java.awt.Image)
     * @see #removeImage(java.awt.Image, int id, int width, int height)
     */
    public synchronized void removeImage(Image image, int id) {
	MediaEntry cur = head;
	MediaEntry prev = null;
	while (cur != null) {
	    MediaEntry next = cur.next;
	    if (cur.getID() == id && cur.getMedia() == image) {
		if (prev == null) {
		    head = next;
		} else {
		    prev.next = next;
		}
		cur.cancel();
	    } else {
		prev = cur;
	    }
	    cur = next;
	}
	notifyAll();	// Notify in case remaining images are "done".
    }

    /**
     * Remove the specified image with the specified size and ID from
     * this MediaTracker.
     * Only the specific instance (with any duplicates) is removed.
     * @param image the image to be removed
     * @param id the tracking ID to remove the image from
     * @param width the width to remove (-1 for unscaled)
     * @param height the height to remove (-1 for unscaled)
     * @see #removeImage(java.awt.Image)
     * @see #removeImage(java.awt.Image, int id)
     */
    public synchronized void removeImage(Image image, int id,
					 int width, int height) {
	MediaEntry cur = head;
	MediaEntry prev = null;
	while (cur != null) {
	    MediaEntry next = cur.next;
	    if (cur.getID() == id && cur instanceof ImageMediaEntry
		&& ((ImageMediaEntry) cur).matches(image, width, height))
	    {
		if (prev == null) {
		    head = next;
		} else {
		    prev.next = next;
		}
		cur.cancel();
	    } else {
		prev = cur;
	    }
	    cur = next;
	}
	notifyAll();	// Notify in case remaining images are "done".
    }

    synchronized void setDone() {
	notifyAll();
    }
}

abstract class MediaEntry {
    MediaTracker tracker;
    int ID;
    MediaEntry next;

    int status;
    boolean cancelled;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -2924957284304726459L;

    MediaEntry(MediaTracker mt, int id) {
	tracker = mt;
	ID = id;
    }

    abstract Object getMedia();

    static MediaEntry insert(MediaEntry head, MediaEntry me) {
	MediaEntry cur = head;
	MediaEntry prev = null;
	while (cur != null) {
	    if (cur.ID > me.ID) {
		break;
	    }
	    prev = cur;
	    cur = cur.next;
	}
	me.next = cur;
	if (prev == null) {
	    head = me;
	} else {
	    prev.next = me;
	}
	return head;
    }

    int getID() {
	return ID;
    }

    abstract void startLoad();

    void cancel() {
	cancelled = true;
    }

    static final int LOADING = MediaTracker.LOADING;
    static final int ABORTED = MediaTracker.ABORTED;
    static final int ERRORED = MediaTracker.ERRORED;
    static final int COMPLETE = MediaTracker.COMPLETE;

    static final int LOADSTARTED = (LOADING | ERRORED | COMPLETE);
    static final int DONE = (ABORTED | ERRORED | COMPLETE);

    synchronized int getStatus(boolean doLoad, boolean doVerify) {
	if (doLoad && ((status & LOADSTARTED) == 0)) {
	    status = (status & ~ABORTED) | LOADING;
	    startLoad();
	}
	return status;
    }

    void setStatus(int flag) {
	synchronized (this) {
	    status = flag;
	}
	tracker.setDone();
    }
}

class ImageMediaEntry extends MediaEntry implements ImageObserver, 
java.io.Serializable {
    Image image;
    int width;
    int height;

    ImageMediaEntry(MediaTracker mt, Image img, int c, int w, int h) {
	super(mt, c);
	image = img;
	width = w;
	height = h;
    }

    boolean matches(Image img, int w, int h) {
	return (image == img && width == w && height == h);
    }

    Object getMedia() {
	return image;
    }

    int getStatus(boolean doLoad, boolean doVerify) {
	if (doVerify) {
	    int flags = tracker.target.checkImage(image, width, height, this);
	    int s = parseflags(flags);
	    if (s == 0) {
		if ((status & (ERRORED | COMPLETE)) != 0) {
		    setStatus(ABORTED);
		}
	    } else if (s != status) {
		setStatus(s);
	    }
	}
	return super.getStatus(doLoad, doVerify);
    }

    void startLoad() {
	if (tracker.target.prepareImage(image, width, height, this)) {
	    setStatus(COMPLETE);
	}
    }

    int parseflags(int infoflags) {
	if ((infoflags & ERROR) != 0) {
	    return ERRORED;
	} else if ((infoflags & ABORT) != 0) {
	    return ABORTED;
	} else if ((infoflags & (ALLBITS | FRAMEBITS)) != 0) {
	    return COMPLETE;
	}
	return 0;
    }

    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int w, int h) {
	if (cancelled) {
	    return false;
	}
	int s = parseflags(infoflags);
	if (s != 0 && s != status) {
	    setStatus(s);
	}
	return ((status & LOADING) != 0);
    }
}
