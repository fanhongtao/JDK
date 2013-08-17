/*
 * @(#)SoundArea.java	1.11 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 	1.11, 12/03/01
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
