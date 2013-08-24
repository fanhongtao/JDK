/*
 * @(#)SoundArea.java	1.17 06/02/22
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
 * @(#)SoundArea.java	1.17 06/02/22
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
 * @version 	1.17, 02/22/06
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
