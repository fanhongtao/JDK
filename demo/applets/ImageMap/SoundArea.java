/*
 * @(#)SoundArea.java	1.9 98/03/18
 *
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.Graphics;
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * An audio feedback ImageArea class.
 * This class extends the basic ImageArea Class to play a sound when
 * the user enters the area.
 *
 * @author 	Jim Graham
 * @author 	Chuck McManis
 * @version 	1.9, 03/18/98
 */
class SoundArea extends ImageMapArea {
    /** The URL of the sound to be played. */
    URL sound;
    AudioClip soundData = null;
    boolean hasPlayed; 
    boolean isReady = false;
    long	lastExit = 0;
    final static int HYSTERESIS = 1500;

    /**
     * The argument is the URL of the sound to be played.
     */
    public void handleArg(String arg) {
	try {
	    sound = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    sound = null;
	}
	hasPlayed = false;
    }

    /**
     * The applet thread calls the getMedia() method when the applet
     * is started.
     */
    public void getMedia() {
	if (sound != null && soundData == null) {
	    soundData = parent.getAudioClip(sound);
	}
	if (soundData == null) {
	    System.out.println("SoundArea: Unable to load data "+sound);
	}
	isReady = true;
    }

    /**
     * The enter method is called when the mouse enters the area.
     * The sound is played if the mouse has been outside of the
     * area for more then the delay indicated by HYSTERESIS.
     */
    public void enter() {
	// is the sound sample loaded?
	if (! isReady) {
	    parent.showStatus("Loading media file...");
	    return;
	}

	/*
 	 * So we entered the selection region, play the sound if
	 * we need to. Track the mouse entering and exiting the
	 * the selection box. If it doesn't stay out for more than
	 * "HYSTERESIS" millis, then don't re-play the sound.
	 */
	long now = System.currentTimeMillis();
	if (Math.abs(now - lastExit) < HYSTERESIS) {
	    // if within the window pretend that it was played.
	    hasPlayed = true;
    	    return;
	}

	// Else play the sound.
	if (! hasPlayed && (soundData != null)) {
	    hasPlayed = true;
	    soundData.play();
	}
    }

    /**
     * The exit method is called when the mouse leaves the area.
     */
    public void exit() {
	if (hasPlayed) {
	    hasPlayed = false;
	    lastExit = System.currentTimeMillis(); // note the time of exit
	}
    }
}
