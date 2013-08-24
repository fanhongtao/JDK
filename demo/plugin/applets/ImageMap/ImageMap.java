/*
 * @(#)ImageMap.java	1.21 05/11/17
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
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
 * @(#)ImageMap.java	1.21 05/11/17
 */

import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.MediaTracker;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.net.URL;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.net.MalformedURLException;

/**
 * An extensible ImageMap applet class.
 * The active areas on the image are controlled by ImageArea classes
 * that can be dynamically loaded over the net.
 *
 * @author 	Jim Graham
 * @version 	1.21, 11/17/05
 */
public class ImageMap
    extends Applet
    implements Runnable, MouseListener, MouseMotionListener {
    /**
     * The unhighlighted image being mapped.
     */
    Image baseImage;

    /**
     * The list of image area handling objects;
     */
    ImageMapArea areas[];

    /**
     * The primary highlight mode to be used.
     */
    static final int BRIGHTER = 0;
    static final int DARKER = 1;

    int hlmode = BRIGHTER;

    /**
     * The percentage of highlight to apply for the primary highlight mode.
     */
    int hlpercent = 50;

    /**
     * The MediaTracker for loading and constructing the various images.
     */
    MediaTracker tracker;

    /**
     * Get a rectangular region of the baseImage highlighted according to
     * the primary highlight specification.
     */
    Image getHighlight(int x, int y, int w, int h) {
	return getHighlight(x, y, w, h, hlmode, hlpercent);
    }

    /**
     * Get a rectangular region of the baseImage with a specific highlight.
     */
    Image getHighlight(int x, int y, int w, int h, int mode, int percent) {
	return getHighlight(x, y, w, h, new HighlightFilter(mode == BRIGHTER,
							    percent));
    }

    /**
     * Get a rectangular region of the baseImage modified by an image filter.
     */
    Image getHighlight(int x, int y, int w, int h, ImageFilter filter) {
	ImageFilter cropfilter = new CropImageFilter(x, y, w, h);
	ImageProducer prod = new FilteredImageSource(baseImage.getSource(),
						     cropfilter);
	return makeImage(prod, filter, 0);
    }

    /**
     * Make a filtered image based on another image.
     */
    Image makeImage(Image orig, ImageFilter filter) {
	return makeImage(orig.getSource(), filter);
    }

    /**
     * Make a filtered image based on another ImageProducer.
     */
    Image makeImage(ImageProducer prod, ImageFilter filter) {
	return makeImage(prod, filter,
			 (prod == baseImage.getSource()) ? 1 : 0);
    }

    /**
     * Make a filtered image based on another ImageProducer.
     * Add it to the media tracker using the indicated ID.
     */
    Image makeImage(ImageProducer prod, ImageFilter filter, int ID) {
	Image filtered = createImage(new FilteredImageSource(prod, filter));
	tracker.addImage(filtered, ID);
	return filtered;
    }

    /**
     * Add an image to the list of images to be tracked.
     */
    void addImage(Image img) {
	tracker.addImage(img, 1);
    }

    /**
     * Parse a string representing the desired highlight to be applied.
     */
    void parseHighlight(String s) {
	if (s == null) {
	    return;
	}
	if (s.startsWith("brighter") || s.startsWith("BRIGHTER")) {
	    hlmode = BRIGHTER;
	    if (s.length() > "brighter".length()) {
		hlpercent = Integer.parseInt(s.substring("brighter".length()));
	    }
	} else if (s.startsWith("darker") || s.startsWith("DARKER")) {
	    hlmode = DARKER;
	    if (s.length() > "darker".length()) {
		hlpercent = Integer.parseInt(s.substring("darker".length()));
	    }
	}
    }

    /**
     * Initialize the applet. Get attributes.
     *
     * Initialize the ImageAreas.
     * Each ImageArea is a subclass of the class ImageArea, and is
     * specified with an attribute of the form:
     * 		areaN=ImageAreaClassName,arguments...
     * The ImageAreaClassName is parsed off and a new instance of that
     * class is created.  The initializer for that class is passed a
     * reference to the applet and the remainder of the attribute
     * string, from which the class should retrieve any information it
     * needs about the area it controls and the actions it needs to
     * take within that area.
     */
    public void init() {
	String s;

	tracker = new MediaTracker(this);
	parseHighlight(getParameter("highlight"));
	introTune = getParameter("startsound");
	baseImage = getImage(getDocumentBase(), getParameter("img"));
	Vector areaVec = new Vector();
	int num = 1;
	while (true) {
	    ImageMapArea newArea;
	    s = getParameter("area"+num);
	    if (s == null) {
		// Try rect for backwards compatibility.
		s = getParameter("rect"+num);
		if (s == null) {
		    break;
		}
		try {
		    newArea = new HighlightArea();
		    newArea.init(this, s);
		    areaVec.addElement(newArea);
		    String url = getParameter("href"+num);
		    if (url != null) {
			s += "," + url;
			newArea = new LinkArea();
			newArea.init(this, s);
			areaVec.addElement(newArea);
		    }
		} catch (Exception e) {
		    System.out.println("error processing: "+s);
		    e.printStackTrace();
		    break;
		}
	    } else {
		try {
		    int classend = s.indexOf(",");
		    String name = s.substring(0, classend);
		    newArea = (ImageMapArea) Class.forName(name).newInstance();
		    s = s.substring(classend+1);
		    newArea.init(this, s);
		    areaVec.addElement(newArea);
		} catch (Exception e) {
		    System.out.println("error processing: "+s);
		    e.printStackTrace();
		    break;
		}
	    }
	    num++;
	}
	areas = new ImageMapArea[areaVec.size()];
	areaVec.copyInto(areas);
	checkSize();
	addMouseListener(this);
	addMouseMotionListener(this);
    }

    public void destroy() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    Thread aniThread = null;
    String introTune = null;

    public void start() {
	if (introTune != null)
	    try {
		play(new URL(getDocumentBase(), introTune));
	    } catch (MalformedURLException e) {}
	if (aniThread == null) {
            aniThread = new Thread(this);
            aniThread.setName("ImageMap Animator");
            aniThread.start();
	}
    }

    public void run() {
	Thread me = Thread.currentThread();
	tracker.checkAll(true);
	for (int i = areas.length; --i >= 0; ) {
	    areas[i].getMedia();
	}
	me.setPriority(Thread.MIN_PRIORITY);
	while (aniThread == me) {
	    boolean animating = false;
	    for (int i = areas.length; --i >= 0; ) {
		animating = areas[i].animate() || animating;
	    }
	    try {
		synchronized(this) {
		    wait(animating ? 100 : 0);
		}
	    } catch (InterruptedException e) {
		break;
	    }
	}
    }

    public synchronized void startAnimation() {
	notify();
    }

    public synchronized void stop() {
	aniThread = null;
	notify();
	for (int i = 0; i < areas.length; i++) {
	    areas[i].exit();
	}
    }

    /**
     * Check the size of this applet while the image is being loaded.
     */
    void checkSize() {
	int w = baseImage.getWidth(this);
	int h = baseImage.getHeight(this);
	if (w > 0 && h > 0) {
	    resize(w, h);
	    synchronized(this) {
		fullrepaint = true;
	    }
	    repaint(0, 0, w, h);
	}
    }

    private boolean fullrepaint = false;
    private final static long UPDATERATE = 100;

    /**
     * Handle updates from images being loaded.
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if ((infoflags & (WIDTH | HEIGHT)) != 0) {
	    checkSize();
	}
	if ((infoflags & (SOMEBITS | FRAMEBITS | ALLBITS)) != 0) {
	    synchronized(this) {
		fullrepaint = true;
	    }
	    repaint(((infoflags & (FRAMEBITS | ALLBITS)) != 0)
		    ? 0 : UPDATERATE,
		    x, y, width, height);
	}
	return (infoflags & (ALLBITS | ERROR)) == 0;
    }

    /**
     * Paint the image and all active highlights.
     */
    public void paint(Graphics g) {
	synchronized(this) {
	    fullrepaint = false;
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas != null) {
	    for (int i = areas.length; --i >= 0; ) {
		areas[i].highlight(g);
	    }
	}
    }

    /**
     * Update the active highlights on the image.
     */
    public void update(Graphics g) {
	boolean full;
	synchronized(this) {
	    full = fullrepaint;
	}
	if (full) {
	    paint(g);
	    return;
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas == null) {
	    return;
	}
	// First unhighlight all of the deactivated areas
	for (int i = areas.length; --i >= 0; ) {
	    areas[i].highlight(g);
	}
    }

      int pressX;
      int pressY;

  public void mouseClicked(MouseEvent e)
  {}

      /**
       * Inform all active ImageAreas of a mouse press.
       */
  public void mousePressed(MouseEvent e)
  {
    pressX = e.getX();
    pressY = e.getY();

    for (int i = 0; i < areas.length; i++) {
      if (areas[i].inside(pressX, pressY)) {
	if (areas[i].press(pressX, pressY)) {
	  break;
	}
      }
    }
    e.consume();
  }

      /**
       * Inform all active ImageAreas of a mouse release.
       * Only those areas that were inside the original mousePressed()
       * are informed of the mouseReleased.
       */
  public void mouseReleased(MouseEvent e)
  {
    for (int i = 0; i < areas.length; i++) {
      if (areas[i].inside(pressX, pressY)) {
	if (areas[i].lift(e.getX(), e.getY())) {
	  break;
	}
      }
    }
    e.consume();
  }

  public void mouseEntered(MouseEvent e)
  {}

      /**
       * Make sure that no ImageAreas are highlighted.
       */
  public void mouseExited(MouseEvent e) {
    for (int i = 0; i < areas.length; i++) {
      areas[i].checkExit();
    }
    e.consume();
  }


      /**
       * Inform all active ImageAreas of a mouse drag.
       * Only those areas that were inside the original mouseDown()
       * are informed of the mouseUp.
       */

  public void mouseDragged(MouseEvent e)
  {
    mouseMoved(e);
    for (int i = 0; i < areas.length; i++) {
      if (areas[i].inside(pressX, pressY)) {
	if (areas[i].drag(e.getX(), e.getY())) {
	  break;
	}
      }
    }
    e.consume();
  }

      /**
       * Find the ImageAreas that the mouse is in.
       */
  public void mouseMoved(MouseEvent e) {
    boolean eaten = false;

    for (int i = 0; i < areas.length; i++) {
      if (!eaten && areas[i].inside(e.getX(), e.getY())) {
	eaten = areas[i].checkEnter(e.getX(), e.getY());
      } else {
	areas[i].checkExit();
      }
    }
    e.consume();
  }

    /**
     * Scan all areas looking for the topmost status string.
     */
    public void newStatus() {
	String msg = null;
	for (int i = 0; i < areas.length; i++) {
	    msg = areas[i].getStatus(msg);
	}
	showStatus(msg);
    }

  public String getAppletInfo() {
    return "Title: ImageMap \nAuthor: Jim Graham \nAn extensible ImageMap applet class. \nThe active areas on the image are controlled by ImageArea \nclasses that can be dynamically loaded over the net.";
  }

  public String[][] getParameterInfo() {
    String[][] info = {
      {"area[n]", "delimited string",
"This parameter takes the form of <ImageAreaClassName>, <ul>, <ur>, <ll>, <lr>, <action> where ImageAreaClassName is the name of the class from which this feedback area is controlled, the next four arguments are the four corners of the "
+ " feedback zone, and the final argument is that action that should be taken on click or mouseover.  That action can be 1) display text in the status bar (if you provide a string argument), 2) play a sound (if you provide the path to a sound file), or 3) load a page (if you provide a URL)."},
      {"rect[n]", "delimited string", "Deprecated: use area[n]"},
      {"href[n]", "URL string", "Pass in a URL to create a LinkArea which will point to this URL.  Not used in these examples."},
      {"highlight", "string/int", "Pass the word 'brighter' followed by an integer 'n' to change the highlight mode to brighter and the hightlight percentage to n.  Pass the word 'darker' followed by an integer 'm' to change the highlight mode to darker and the highlight percentage to m.  Anything else will be ignored.  The default highlight mode is BRIGHTER and the default highlight percentage is 50."},
      {"startsound", "path string", "The path of a soundclip to play when the image is first displayed."},
      {"img", "path string", "The path to the image to be displayed as a live feedback image map."}
    };
    return info;
  }
}



