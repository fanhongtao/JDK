/*
 * @(#)ImageMapArea.java	1.19 06/02/22
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)ImageMapArea.java	1.19 06/02/22
 */

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.*;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * The base ImageArea class.
 * This class performs the basic functions that most ImageArea
 * classes will need and delegates specific actions to the subclasses.
 *
 * @author 	Jim Graham
 * @version 	1.19, 02/22/06
 */
class ImageMapArea implements ImageObserver {
    /** The applet parent that contains this ImageArea. */
    ImageMap parent;
    /** The X location of the area (if rectangular). */
    int X;
    /** The Y location of the area (if rectangular). */
    int Y;
    /** The size().width of the area (if rectangular). */
    int W;
    /** The size().height of the area (if rectangular). */
    int H;
    /**
     * This flag indicates whether the user was in this area during the
     * last scan of mouse locations.
     */
    boolean entered = false;
    /** This flag indicates whether the area is currently highlighted. */
    boolean active = false;

    /**
     * This is the default highlight image if no special effects are
     * needed to draw the highlighted image.  It is created by the
     * default "makeImages()" method.
     */
    Image hlImage;

    /**
     * This is the status string requested by this area.  Only the
     * status string from the topmost area which has requested one
     * will be displayed.
     */
    String status;

    /**
     * Initialize this ImageArea as called from the applet.
     * If the subclass does not override this initializer, then it
     * will perform the basic functions of setting the parent applet
     * and parsing out 4 numbers from the argument string which specify
     * a rectangular region for the ImageArea to act on.
     * The remainder of the argument string is passed to the handleArg()
     * method for more specific handling by the subclass.
     */
    public void init(ImageMap parent, String args) {
	this.parent = parent;
	StringTokenizer st = new StringTokenizer(args, ", ");
	X = Integer.parseInt(st.nextToken());
	Y = Integer.parseInt(st.nextToken());
	W = Integer.parseInt(st.nextToken());
	H = Integer.parseInt(st.nextToken());
	if (st.hasMoreTokens()) {
	    handleArg(st.nextToken(","));
	} else {
	    handleArg(null);
	}
	makeImages();
    }

    /**
     * This method handles the remainder of the argument string after
     * the standard initializer has parsed off the 4 rectangular
     * parameters.  If the subclass does not override this method,
     * the remainder will be ignored.
     */
    public void handleArg(String s) {
    }

    /**
     * This method loads any additional media that the ImageMapArea
     * may need for its animations.
     */
    public void getMedia() {
    }

    /**
     * This method is called every animation cycle if there are any
     * active animating areas.
     * @return true if this area requires further animation notifications
     */
    public boolean animate() {
	return false;
    }

    /**
     * This method sets the image to be used to render the ImageArea
     * when it is highlighted.
     */
    public void setHighlight(Image img) {
	hlImage = img;
    }

    /**
     * This method handles the construction of the various images
     * used to highlight this particular ImageArea when the user
     * interacts with it.
     */
    public void makeImages() {
	setHighlight(parent.getHighlight(X, Y, W, H));
    }

    /**
     * The repaint method causes the area to be repainted at the next
     * opportunity.
     */
    public void repaint() {
	parent.repaint(0, X, Y, W, H);
    }

    /**
     * This method tests to see if a point is inside this ImageArea.
     * The standard method assumes a rectangular area as parsed by
     * the standard initializer.  If a more complex area is required
     * then this method will have to be overridden by the subclass.
     */
    public boolean inside(int x, int y) {
	return (x >= X && x < (X + W) && y >= Y && y < (Y + H));
    }

    /**
     * This utility method draws a rectangular subset of a highlight
     * image.
     */
    public void drawImage(Graphics g, Image img, int imgx, int imgy,
			  int x, int y, int w, int h) {
	Graphics ng = g.create();
	try {
	    ng.clipRect(x, y, w, h);
	    ng.drawImage(img, imgx, imgy, this);
	} finally {
	    ng.dispose();
	}
    }

    /**
     * This method handles the updates from drawing the images.
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if (img == hlImage) {
	    return parent.imageUpdate(img, infoflags, x + X, y + Y,
				      width, height);
	} else {
	    return (infoflags & (ALLBITS | ERROR)) == 0;
	}
    }

    /**
     * This utility method records a string to be shown in the status bar.
     */
    public void showStatus(String msg) {
	status = msg;
	parent.newStatus();
    }

    /**
     * This utility method returns the status string this area wants to
     * put into the status bar.  If no previous area (higher in the
     * stacking order) has yet returned a status message, prevmsg will
     * be null and this area will then return its own message, otherwise
     * it will leave the present message alone.
     */
    public String getStatus(String prevmsg) {
	return (prevmsg == null) ? status : prevmsg;
    }

    /**
     * This utility method tells the browser to visit a URL.
     */
    public void showDocument(URL u) {
	parent.getAppletContext().showDocument(u);
    }

    /**
     * This method highlights the specified area when the user enters
     * it with his mouse.  The standard highlight method is to replace
     * the indicated rectangular area of the image with the primary
     * highlighted image.
     */
    public void highlight(Graphics g) {
    }

    /**
     * The checkEnter method is called when the mouse is inside the
     * region to see if the area needs to have its enter method called.
     * The default implementation simply checks if the entered flag is
     * set and only calls enter if it is false.
     */
    public boolean checkEnter(int x, int y) {
	if (!entered) {
	    entered = true;
	    enter(x, y);
	}
	return isTerminal();
    }

    /**
     * The checkExit method is called when the mouse is outside the
     * region to see if the area needs to have its exit method called.
     * The default implementation simply checks if the entered flag is
     * set and only calls exit if it is true.
     */
    public void checkExit() {
	if (entered) {
	    entered = false;
	    exit();
	}
    }

    /**
     * The isTerminal method controls whether events propagate to the
     * areas which lie beneath this one.
     * @return true if the events should be propagated to the underlying
     * areas.
     */
    public boolean isTerminal() {
	return false;
    }

    /**
     * The enter method is called when the mouse enters the region.
     * The location is supplied, but the standard implementation is
     * to call the overloaded method with no arguments.
     */
    public void enter(int x, int y) {
	enter();
    }

    /**
     * The overloaded enter method is called when the mouse enters
     * the region.  This method can be overridden if the ImageArea
     * does not need to know where the mouse entered.
     */
    public void enter() {
    }

    /**
     * The exit method is called when the mouse leaves the region.
     */
    public void exit() {
    }

    /**
     * The press method is called when the user presses the mouse
     * button inside the ImageArea.  The location is supplied, but
     * the standard implementation is to call the overloaded method
     * with no arguments.
     * @return true if this ImageMapArea wants to prevent any underlying
     * areas from seeing the press
     */
    public boolean press(int x, int y) {
	return press();
    }

    /**
     * The overloaded press method is called when the user presses the
     * mouse button inside the ImageArea.  This method can be overridden
     * if the ImageArea does not need to know the location of the press.
     * @return true if this ImageMapArea wants to prevent any underlying
     * areas from seeing the press
     */
    public boolean press() {
	return isTerminal();
    }

    /**
     * The lift method is called when the user releases the mouse button.
     * The location is supplied, but the standard implementation is to
     * call the overloaded method with no arguments.  Only those ImageAreas
     * that were informed of a press will be informed of the corresponding
     * release.
     * @return true if this ImageMapArea wants to prevent any underlying
     * areas from seeing the lift
     */
    public boolean lift(int x, int y) {
	return lift();
    }

    /**
     * The overloaded lift method is called when the user releases the
     * mouse button.  This method can be overridden if the ImageArea
     * does not need to know the location of the release.
     * @return true if this ImageMapArea wants to prevent any underlying
     * areas from seeing the lift
     */
    public boolean lift() {
	return isTerminal();
    }

    /**
     * The drag method is called when the user moves the mouse while
     * the button is pressed.  Only those ImageAreas that were informed
     * of a press will be informed of the corresponding mouse movements.
     * @return true if this ImageMapArea wants to prevent any underlying
     * areas from seeing the drag
     */
    public boolean drag(int x, int y) {
	return isTerminal();
    }

  public String getAppletInfo() {
    return "Title: ImageArea \nAuthor: Jim Graham \nThis class performs the basic functions that most ImageArea classes will need and delegates specific actions to the subclasses.";
  }
}

