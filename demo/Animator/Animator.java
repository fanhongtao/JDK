/*
 * @(#)Animator.java	1.10 97/02/05
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An applet that plays a sequence of images, as a loop or a one-shot.
 * Can have a soundtrack and/or sound effects tied to individual frames.
 * See the <a href="http://java.sun.com/applets/applets/Animator/">Animator
 * home page</a> for details and updates.
 *
 * @author Herb Jellinek
 * @version 1.10, 02/05/97
 */

public class Animator extends Applet
                      implements Runnable,
				 MouseListener {
    
    /**
     * The images, in display order (Images).
     */
    Vector images = null;

    /**
     * A table that maps images to image names - for error messages.
     */
    Hashtable imageNames = new Hashtable(10);

    /**
     * Duration of each image (Integers, in milliseconds).
     */
    Hashtable durations = null;

    /**
     * Sound effects for each image (AudioClips).
     */
    Hashtable sounds = null;

    /**
     * Position of each image (Points).
     */
    Hashtable positions = null;

    /**
     * MediaTracker 'class' ID numbers.
     */
    static final int STARTUP_ID    = 0;
    static final int BACKGROUND_ID = 1;
    static final int ANIMATION_ID  = 2;

    /**
     * Start-up image URL, if any.
     */
    URL startUpImageURL = null;

    /**
     * Start-up image, if any.
     */
    Image startUpImage = null;

    /**
     * Background image URL, if any.
     */
    URL backgroundImageURL = null;

    /**
     * Background image, if any.
     */
    Image backgroundImage = null;

    /**
     * Background color, if any.
     */
    Color backgroundColor = null;
    
    /**
     * The soundtrack's URL.
     */
    URL soundtrackURL = null;

    /**
     * The soundtrack.
     */
    AudioClip soundtrack = null;

    /**
     * URL to link to, if any.
     */
    URL hrefURL = null;

    /**
     * Frame target for that URL, if any.
     */
    String hrefTarget = null;

    /**
     * Our width.
     */
    int appWidth = 0;

    /**
     * Our height.
     */
    int appHeight = 0;

    /**
     * The directory or URL from which the images are loaded
     */
    URL imageSource = null;

    /**
     * The directory or URL from which the sounds are loaded
     */
    URL soundSource = null;

    /**
     * The thread animating the images.
     */
    Thread engine = null;

    /**
     * The current loop slot - index into 'images.'
     */
    int frameNum;

    /**
     * frameNum as an Object - suitable for use as a Hashtable key.
     */
    Integer frameNumKey;
    
    /**
     * The current X position (for painting).
     */
    int xPos = 0;
    
    /**
     * The current Y position (for painting).
     */
    int yPos = 0;
    
    /**
     * The default number of milliseconds to wait between frames.
     */
    public static final int defaultPause = 3900;
    
    /**
     * The global delay between images, which can be overridden by
     * the PAUSE parameter.
     */
    int globalPause = defaultPause;

    /**
     * Whether or not the thread has been paused by the user.
     */
    boolean userPause = false;

    /**
     * Repeat the animation?  If false, just play it once.
     */
    boolean repeat;

    /**
     * The offscreen image, used in double buffering
     */
    Image offScrImage;

    /**
     * The offscreen graphics context, used in double buffering
     */
    Graphics offScrGC;

    /**
     * The MediaTracker we use to load our images.
     */
    MediaTracker tracker;
    
    /**
     * Can we paint yet?
     */
    boolean loaded = false;

    /**
     * Was there an initialization error?
     */
    boolean error = false;

    /**
     * What we call an image file in messages.
     */
    static final String imageLabel = "image";
    
    /**
     * What we call a sound file in messages.
     */
    static final String soundLabel = "sound";
    
    /**
     * Print silly debugging info?
     */
    static final boolean debug = false;

    /**
     * Where to find the source code and documentation - for informational
     * purposes.
     */
    static final String sourceLocation =
                   "http://java.sun.com/applets/applets/Animator/";

    /**
     * Instructions to the user: how to produce the popup.
     */
    static final String userInstructions = "shift-click for errors, info";

    /**
     * Applet info.
     */
    public String getAppletInfo() {
	return "Animator v1.10 (02/05/97), by Herb Jellinek";
    }

    /**
     * Parameter info.
     */
    public String[][] getParameterInfo() {
	String[][] info = {
	    {"imagesource", 	"URL", 		"a directory"},
	    {"startup", 	"URL", 		"image displayed at start-up"},
	    {"backgroundcolor", "int",          "color (24-bit RGB number) displayed as background"},
	    {"background", 	"URL", 		"image displayed as background"},
	    {"startimage", 	"int", 		"index of first image"},
	    {"endimage", 	"int", 		"index of last image"},
	    {"namepattern",     "URL",          "generates indexed names"},
	    {"images",          "URLs",         "list of image indices"},
	    {"href",		"URL",		"page to visit on mouse-click"},
	    {"target",		"name",		"frame to put that page in"},
	    {"pause", 	        "int", 		"global pause, milliseconds"},
	    {"pauses", 	        "ints", 	"individual pauses, milliseconds"},
	    {"repeat", 	        "boolean", 	"repeat? true or false"},
	    {"positions",	"coordinates", 	"path images will follow"},
	    {"soundsource",	"URL", 		"audio directory"},
	    {"soundtrack",	"URL", 		"background music"},
	    {"sounds",		"URLs",		"list of audio samples"},
	};
	return info;
    }

    /**
     * Show a crude "About" box.  Displays credits, errors (if any), and
     * parameter values and documentation.
     */
    void showDescription() {
	DescriptionFrame description = new DescriptionFrame();
	    
	description.tell("\t\t"+getAppletInfo()+"\n");
	description.tell("Updates, documentation at "+sourceLocation+"\n\n");
	description.tell("Document base: "+getDocumentBase()+"\n");
	description.tell("Code base: "+getCodeBase()+"\n\n");
	
	Object errors[] = tracker.getErrorsAny();
	if (errors != null) {
	    description.tell("Applet image errors:\n");
	    for (int i = 0; i < errors.length; i++) {
		if (errors[i] instanceof Image) {
		    URL url = (URL)imageNames.get((Image)errors[i]);
		    if (url != null) {
			description.tell(" "+url+" not loaded\n");
		    }
		}
	    }
	    description.tell("\n");
	}
	
	if (images == null || images.size() == 0) {
	    description.tell("\n** No images loaded **\n\n");
	}

	description.tell("Applet parameters:\n");
	description.tell(" width = "+getParameter("WIDTH")+"\n");
	description.tell(" height = "+getParameter("HEIGHT")+"\n");
	
	String params[][] = getParameterInfo();
	for (int i = 0; i < params.length; i++) {
	    String name = params[i][0];
	    description.tell(" "+name+" = "+getParameter(name)+
			     "\t ["+params[i][2]+"]\n");
	}
	
	description.show();
    }

    /**
     * Print silly debugging info to the standard output.
     */
    void dbg(String s) {
	if (debug) {
	    System.out.println("> "+s);
	}
    }

    /**
     * Local version of getParameter for debugging purposes.
     */
    public String getParam(String key) {
	String result = getParameter(key);
	dbg("getParameter("+key+") = "+result);
	return result;
    }

    final int setFrameNum(int newFrameNum) {
	frameNumKey = new Integer(frameNum = newFrameNum);
	return frameNum;
    }
    
    /**
     * Parse the IMAGES parameter.  It looks like
     * 1|2|3|4|5, etc., where each number (item) names a source image.
     *
     * @return a Vector of (URL) image file names.
     */
    Vector parseImages(String attr, String pattern)
    throws MalformedURLException {
	if (pattern == null) {
	    pattern = "T%N.gif";
	}
	Vector result = new Vector(10);
	for (int i = 0; i < attr.length(); ) {
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();
	    String file = attr.substring(i, next);
	    result.addElement(new URL(imageSource, doSubst(pattern, file)));
	    i = next + 1;
	}
	return result;
    }

    /**
     * Fetch an image and wait for it to come in.  Used to enforce a load
     * order for background and startup images.
     * Places URL and image in imageNames table.
     */
    Image fetchImageAndWait(URL imageURL, int trackerClass) 
    throws InterruptedException {
	Image image = getImage(imageURL);
	tracker.addImage(image, trackerClass);
	imageNames.put(image, imageURL);
	tracker.waitForID(trackerClass);
	return image;
    }

    /**
     * Fetch the images named in the argument.  Is restartable.
     *
     * @param images a Vector of URLs
     * @return true if all went well, false otherwise.
     */
    boolean fetchImages(Vector images) {
	if (images == null) {
	    return true;
	}

	int size = images.size();
	for (int i = 0; i < size; i++) {
	    Object o = images.elementAt(i);
	    if (o instanceof URL) {
		URL url = (URL)o;
		tellLoadingMsg(url, imageLabel);
		Image im = getImage(url);
		tracker.addImage(im, ANIMATION_ID);
		images.setElementAt(im, i);
		imageNames.put(im, url);
	    }
	}

	try {
	    tracker.waitForID(ANIMATION_ID);
	} catch (InterruptedException e) {}
	if (tracker.isErrorID(ANIMATION_ID)) {
	    return false;
	}

	return true;
    }

    /**
     * Parse the SOUNDS parameter.  It looks like
     * train.au||hello.au||stop.au, etc., where each item refers to a
     * source image.  Empty items mean that the corresponding image
     * has no associated sound.
     *
     * @return a Hashtable of SoundClips keyed to Integer frame numbers.
     */
    Hashtable parseSounds(String attr, Vector images)
    throws MalformedURLException {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < attr.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();
	    
	    String sound = attr.substring(i, next);
	    if (sound.length() != 0) {
		result.put(new Integer(imageNum),
			   new URL(soundSource, sound));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }

    /**
     * Fetch the sounds named in the argument.
     * Is restartable.
     *
     * @return URL of the first bogus file we hit, null if OK.
     */
    URL fetchSounds(Hashtable sounds) {
	for (Enumeration e = sounds.keys() ; e.hasMoreElements() ;) {
	    Integer num = (Integer)e.nextElement();
	    Object o = sounds.get(num);
	    if (o instanceof URL) {
		URL file = (URL)o;
		tellLoadingMsg(file, soundLabel);
		try {
		    sounds.put(num, getAudioClip(file));
		} catch (Exception ex) {
		    return file;
		}
	    }
	}
	return null;
    }

    /**
     * Parse the PAUSES parameter.  It looks like
     * 1000|500|||750, etc., where each item corresponds to a
     * source image.  Empty items mean that the corresponding image
     * has no special duration, and should use the global one.
     *
     * @return a Hashtable of Integer pauses keyed to Integer
     * frame numbers.
     */
    Hashtable parseDurations(String attr, Vector images) {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < attr.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();

	    if (i != next) {
		int duration = Integer.parseInt(attr.substring(i, next));
		result.put(new Integer(imageNum), new Integer(duration));
	    } else {
		result.put(new Integer(imageNum),
			   new Integer(globalPause));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }

    /**
     * Parse a String of form xxx@yyy and return a Point.
     */
    Point parsePoint(String s) throws ParseException {
	int atPos = s.indexOf('@');
	if (atPos == -1) throw new ParseException("Illegal position: "+s);
	return new Point(Integer.parseInt(s.substring(0, atPos)),
			 Integer.parseInt(s.substring(atPos + 1)));
    }


    /**
     * Parse the POSITIONS parameter.  It looks like
     * 10@30|11@31|||12@20, etc., where each item is an X@Y coordinate
     * corresponding to a source image.  Empty items mean that the
     * corresponding image has the same position as the preceding one.
     *
     * @return a Hashtable of Points keyed to Integer frame numbers.
     */
    Hashtable parsePositions(String param, Vector images)
    throws ParseException {
	Hashtable result = new Hashtable();

	int imageNum = 0;
	int numImages = images.size();
	for (int i = 0; i < param.length(); ) {
	    if (imageNum >= numImages) break;
	    
	    int next = param.indexOf('|', i);
	    if (next == -1) next = param.length();

	    if (i != next) {
		result.put(new Integer(imageNum),
			   parsePoint(param.substring(i, next)));
	    }
	    i = next + 1;
	    imageNum++;
	}

	return result;
    }
    
    /**
     * Get the dimensions of an image.
     * @return the image's dimensions.
     */
    Dimension getImageDimensions(Image im) {
	return new Dimension(im.getWidth(null), im.getHeight(null));
    }

    /**
     * Substitute an integer some number of times in a string, subject to
     * parameter strings embedded in the string.
     * Parameter strings:
     *   %N - substitute the integer as is, with no padding.
     *   %<digit>, for example %5 - substitute the integer left-padded with
     *        zeros to <digits> digits wide.
     *   %% - substitute a '%' here.
     * @param inStr the String to substitute within
     * @param theInt the int to substitute, as a String.
     */
    String doSubst(String inStr, String theInt) {
	String padStr = "0000000000";
	int length = inStr.length();
	StringBuffer result = new StringBuffer(length);
	
	for (int i = 0; i < length;) {
	    char ch = inStr.charAt(i);
	    if (ch == '%') {
		i++;
		if (i == length) {
		    result.append(ch);
		} else {
		    ch = inStr.charAt(i);
		    if (ch == 'N' || ch == 'n') {
			// just stick in the number, unmolested
			result.append(theInt);
			i++;
		    } else {
			int pad;
			if ((pad = Character.digit(ch, 10)) != -1) {
			    // we've got a width value
			    String numStr = theInt;
			    String scr = padStr+numStr;
			    result.append(scr.substring(scr.length() - pad));
			    i++;
			} else {
			    result.append(ch);
			    i++;
			}
		    }
		}
	    } else {
		result.append(ch);
		i++;
	    }
	}
	return result.toString();
    }	

    /**
     * Stuff a range of image names into a Vector.
     *
     * @return a Vector of image URLs.
     */
    Vector prepareImageRange(int startImage, int endImage, String pattern)
    throws MalformedURLException {
	Vector result = new Vector(Math.abs(endImage - startImage) + 1);
	if (pattern == null) {
	    pattern = "T%N.gif";
	}
	if (startImage > endImage) {
	    for (int i = startImage; i >= endImage; i--) {
		result.addElement(new URL(imageSource, doSubst(pattern, i+"")));
	    }
	} else {
	    for (int i = startImage; i <= endImage; i++) {
		result.addElement(new URL(imageSource, doSubst(pattern, i+"")));
	    }
	}

	return result;
    }

    
    /**
     * Initialize the applet.  Get parameters.
     */
    public void init() {


	tracker = new MediaTracker(this);

	appWidth = getSize().width;
	appHeight = getSize().height;

	try {
	    String param = getParam("IMAGESOURCE");	
	    imageSource = (param == null) ? getDocumentBase() : new URL(getDocumentBase(), param + "/");
	
	    String href = getParam("HREF");
	    if (href != null) {
		try {
		    hrefURL = new URL(getDocumentBase(), href);
		} catch (MalformedURLException e) {
		    showParseError(e);
		}
	    }

	    hrefTarget = getParam("TARGET");
	    if (hrefTarget == null) {
		hrefTarget = "_top";
	    }

	    param = getParam("PAUSE");
	    globalPause =
		(param != null) ? Integer.parseInt(param) : defaultPause;

	    param = getParam("REPEAT");
	    repeat = (param == null) ? true : (param.equalsIgnoreCase("yes") ||
					       param.equalsIgnoreCase("true"));

	    int startImage = 1;
	    int endImage = 1;
	    param = getParam("ENDIMAGE");
	    if (param != null) {
		endImage = Integer.parseInt(param);
		param = getParam("STARTIMAGE");
		if (param != null) {
		    startImage = Integer.parseInt(param);
		}
		param = getParam("NAMEPATTERN");
		images = prepareImageRange(startImage, endImage, param);
	    } else {
		param = getParam("STARTIMAGE");
		if (param != null) {
		    startImage = Integer.parseInt(param);
		    param = getParam("NAMEPATTERN");
		    images = prepareImageRange(startImage, endImage, param);
		} else {
		    param = getParam("IMAGES");
		    if (param == null) {
			showStatus("No legal IMAGES, STARTIMAGE, or ENDIMAGE "+
				   "specified.");
			error = true;
			return;
		    } else {
			images = parseImages(param, getParam("NAMEPATTERN"));
		    }
		}
	    }

	    param = getParam("BACKGROUND");
	    if (param != null) {
		backgroundImageURL = new URL(imageSource, param);
	    }

	    param = getParam("BACKGROUNDCOLOR");
	    if (param != null) {
		backgroundColor = decodeColor(param);
	    }

	    param = getParam("STARTUP");
	    if (param != null) {
		startUpImageURL = new URL(imageSource, param);
	    }

	    param = getParam("SOUNDSOURCE");
	    soundSource = (param == null) ? imageSource : new URL(getDocumentBase(), param + "/");
	
	    param = getParam("SOUNDS");
	    if (param != null) {
		sounds = parseSounds(param, images);
	    }

	    param = getParam("PAUSES");
	    if (param != null) {
		durations = parseDurations(param, images);
	    }

	    param = getParam("POSITIONS");
	    if (param != null) {
		positions = parsePositions(param, images);
	    }

	    param = getParam("SOUNDTRACK");
	    if (param != null) {
		soundtrackURL = new URL(soundSource, param);
	    }
	} catch (MalformedURLException e) {
	    showParseError(e);
	} catch (ParseException e) {
	    showParseError(e);
	}

	setFrameNum(0);
	addMouseListener(this);
    }

    Color decodeColor(String s) {
	int val = 0;
	try {
	    if (s.startsWith("0x")) {
		val = Integer.parseInt(s.substring(2), 16);
	    } else if (s.startsWith("#")) {
		val = Integer.parseInt(s.substring(1), 16);
	    } else if (s.startsWith("0") && s.length() > 1) {
		val = Integer.parseInt(s.substring(1), 8);
	    } else {
		val = Integer.parseInt(s, 10);
	    }
	    return new Color(val);
	} catch (NumberFormatException e) {
	    return null;
	}
    }

    void tellLoadingMsg(String file, String fileType) {
	showStatus("Animator: loading "+fileType+" "+file);
    }

    void tellLoadingMsg(URL url, String fileType) {
	tellLoadingMsg(url.toExternalForm(), fileType);
    }

    void clearLoadingMessage() {
	showStatus("");
    }
    
    void loadError(String fileName, String fileType) {
	String errorMsg = "Animator: Couldn't load "+fileType+" "+
	    fileName;
	showStatus(errorMsg);
	System.err.println(errorMsg);
	error = true;
	repaint();
    }

    void loadError(URL badURL, String fileType) {
	loadError(badURL.toExternalForm(), fileType);
    }

    void loadErrors(Object errors[], String fileType) {
	for (int i = 0; i < errors.length; i++) {
	    if (errors[i] instanceof Image) {
		URL url = (URL)imageNames.get((Image)errors[i]);
		if (url != null) {
		    loadError(url, fileType);
		}
	    }
	}
    }

    void showParseError(Exception e) {
	String errorMsg = "Animator: Parse error: "+e;
	showStatus(errorMsg);
	System.err.println(errorMsg);
	error = true;
	repaint();
    }

    void startPlaying() {
	if (soundtrack != null) {
	    soundtrack.loop();
	}
    }

    void stopPlaying() {
	if (soundtrack != null) {
	    soundtrack.stop();
	}
    }

    /**
     * Run the animation. This method is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();
	URL badURL;
	
	me.setPriority(Thread.MIN_PRIORITY);

	if (! loaded) {
	    try {
		// ... to do a bunch of loading.
		if (startUpImageURL != null) {
		    tellLoadingMsg(startUpImageURL, imageLabel);
		    startUpImage = fetchImageAndWait(startUpImageURL,
						     STARTUP_ID);
		    if (tracker.isErrorID(STARTUP_ID)) {
			loadError(startUpImageURL, "start-up image");
		    }
		    repaint();
		}
	    
		if (backgroundImageURL != null) {
		    tellLoadingMsg(backgroundImageURL, imageLabel);
		    backgroundImage = fetchImageAndWait(backgroundImageURL, 
							BACKGROUND_ID);
		    if (tracker.isErrorID(BACKGROUND_ID)) {
			loadError(backgroundImageURL, "background image");
		    }
		    repaint();
		}

		// Fetch the animation frames
		if (!fetchImages(images)) {
		    // get images in error, tell user about first of them
		    Object errors[] = tracker.getErrorsAny();
		    loadErrors(errors, imageLabel);
		    return;
		}

		if (soundtrackURL != null && soundtrack == null) {
		    tellLoadingMsg(soundtrackURL, imageLabel);
		    soundtrack = getAudioClip(soundtrackURL);
		    if (soundtrack == null) {
			loadError(soundtrackURL, "soundtrack");
			return;
		    }
		}

		if (sounds != null) {
		    badURL = fetchSounds(sounds);
		    if (badURL != null) {
			loadError(badURL, soundLabel);
			return;
		    }
		}

		clearLoadingMessage();

		offScrImage = createImage(appWidth, appHeight);
		offScrGC = offScrImage.getGraphics();
		offScrGC.setColor(Color.lightGray);

		loaded = true;
		error = false;
	    } catch (Exception e) {
		error = true;
		e.printStackTrace();
	    }
	}

	if (userPause) {
	    return;
	}

	if (repeat || frameNum < images.size()) {
	    startPlaying();
	}

	try {
	    if (images != null && images.size() > 1) {
		while (appWidth > 0 && appHeight > 0 && engine == me) {
		    if (frameNum >= images.size()) {
			if (!repeat) {
			    return;
			}
			setFrameNum(0);
		    }
		    repaint();

		    if (sounds != null) {
			AudioClip clip =
			    (AudioClip)sounds.get(frameNumKey);
			if (clip != null) {
			    clip.play();
			}
		    }

		    try {
			Integer pause = null;
			if (durations != null) {
			    pause = (Integer)durations.get(frameNumKey);
			}
			if (pause == null) {
			    Thread.sleep(globalPause);
			} else {
			    Thread.sleep(pause.intValue());
			}
		    } catch (InterruptedException e) {
			// Should we do anything?
		    }
		    setFrameNum(frameNum+1);
		}
	    }
	} finally {
	    stopPlaying();
	}
    }

    /**
     * No need to clear anything; just paint.
     */
    public void update(Graphics g) {
	paint(g);
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	if (error || !loaded) {
	    if (startUpImage != null) {
		if (tracker.checkID(STARTUP_ID)) {
		    if (backgroundColor != null) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, appWidth, appHeight);
		    }
		    g.drawImage(startUpImage, 0, 0, this);
		}
	    } else {
		if (backgroundImage != null) {
		    if (tracker.checkID(BACKGROUND_ID)) {
			g.drawImage(backgroundImage, 0, 0, this);
		    }
		} else {
		    g.clearRect(0, 0, appWidth, appHeight);
		}
	    }
	} else {
	    if ((images != null) && (images.size() > 0) &&
		tracker.checkID(ANIMATION_ID)) {
		if (frameNum < images.size()) {
		    if (backgroundImage == null) {
			offScrGC.clearRect(0, 0, appWidth, appHeight);
		    } else {
			offScrGC.drawImage(backgroundImage, 0, 0, this);
		    }

		    Image image = (Image)images.elementAt(frameNum);
		    
		    Point pos = null;
		    if (positions != null) {
			pos = (Point)positions.get(frameNumKey);
		    }
		    if (pos != null) {
			xPos = pos.x;
			yPos = pos.y;
		    }
		    if (backgroundColor != null) {
			offScrGC.setColor(backgroundColor);
			offScrGC.fillRect(0, 0, appWidth, appHeight);
			offScrGC.drawImage(image, xPos, yPos, backgroundColor,
					   this);
		    } else {
			offScrGC.drawImage(image, xPos, yPos, this);
		    }
		    g.drawImage(offScrImage, 0, 0, this);
		} else {
		    // no more animation, but need to draw something
		    dbg("No more animation; drawing last image.");
		    if (backgroundImage == null) {
			g.fillRect(0, 0, appWidth, appHeight);
		    } else {
			g.drawImage(backgroundImage, 0, 0, this);
		    }
		    g.drawImage((Image)images.lastElement(), 0, 0, this);
		}
	    }
	}
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (engine == null) {
	    engine = new Thread(this);
	    engine.start();
	}
	showStatus(getAppletInfo());
    }

    /**
     * Stop the insanity, um, applet.
     */
    public void stop() {
	if (engine != null && engine.isAlive()) {
	    engine.stop();
	}
	engine = null;
    }

    /**
     * Pause the thread when the user clicks the mouse in the applet.
     * If the thread has stopped (as in a non-repeat performance),
     * restart it.
     */
    public void mousePressed(MouseEvent event) {
	if ((event.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
	    showDescription();
	    return;
	} else if (hrefURL != null) {
	    //Let mouseClicked handle this.
	    return;
        } else if (loaded) {
	    if (engine != null && engine.isAlive()) {
		if (userPause) {
		    engine.resume();
		    startPlaying();
	        } else {
	    	    engine.suspend();
	    	    stopPlaying();
	        }
	        userPause = !userPause;
	    } else {
	        userPause = false;
	        setFrameNum(0);
	        engine = new Thread(this);
	        engine.start();
	    }
	}
    }

    public void mouseClicked(MouseEvent event) {
	if ((hrefURL != null) &&
	        ((event.getModifiers() & InputEvent.SHIFT_MASK) == 0)) {
	    getAppletContext().showDocument(hrefURL, hrefTarget);
	}
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
	showStatus(getAppletInfo()+" -- "+userInstructions);
    }

    public void mouseExited(MouseEvent event) {
        showStatus("");
    }
    
}


/**
 * ParseException: signals a parameter parsing problem.
 */
class ParseException extends Exception {
    ParseException(String s) {
	super(s);
    }
}


/**
 * DescriptionFrame: implements a pop-up "About" box.
 */
class DescriptionFrame extends Frame 
		       implements ActionListener {

    static final int rows = 27;
    static final int cols = 70;
    
    TextArea info;
    Button cancel;

    DescriptionFrame() {
	super("Animator v1.10");
	add("Center", info = new TextArea(rows, cols));
	info.setEditable(false);
	info.setBackground(Color.white);
	Panel buttons = new Panel();
	add("South", buttons);
	buttons.add(cancel = new Button("Cancel"));
	cancel.addActionListener(this);
	
	pack();
    }

    public void show() {
	info.select(0,0);
	super.show();
    }

    void tell(String s) {
	info.append(s);
    }

    public void actionPerformed(ActionEvent e) {
	setVisible(false);
    }
}
