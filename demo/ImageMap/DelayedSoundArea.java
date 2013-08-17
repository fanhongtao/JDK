/*
 * @(#)DelayedSoundArea.java	1.9 98/03/18
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
import java.util.StringTokenizer;

/**
 * This ImageArea Class will play a sound each time the user enters the 
 * area. It is different from SoundArea in that it accepts a delay (in 
 * tenths of a second) before it plays the sound. If the mouse leaves
 * the area before the time delay, the sound is not played.
 *
 * This allows you to have one piece of audio when the button is "hit"
 * via SoundArea and another if the user stays on the button.
 *
 * @author 	Chuck McManis
 * @version 	1.9, 03/18/98
 */
class DelayedSoundArea extends ImageMapArea {
    /** The URL of the sound to be played. */
    URL 	sound;
    AudioClip	soundData;
    boolean 	hasPlayed; 
    int	    	delay;
    int		countDown;

    /**
     * The argument is the URL of the sound to be played.
     * This method also sets this type of area to be non-terminal.
     */
    public void handleArg(String arg) {
	Thread soundLoader;
	StringTokenizer st = new StringTokenizer(arg, ", ");
	
	delay = Integer.parseInt(st.nextToken());
	try {
	    sound = new URL(parent.getDocumentBase(), st.nextToken());
	} catch (MalformedURLException e) {
	    sound = null;
	}
    }

    public void getMedia() {
	if (sound != null) {
	    soundData = parent.getAudioClip(sound);
	}
	if (soundData == null) {
	    System.out.println("DelayedSoundArea: Unable to load data "+sound);
	}
    }

    /**
     * The highlight method plays the sound in addition to the usual
     * graphical highlight feedback.
     */
    public void enter() {
	hasPlayed = false;
	countDown = delay;
	parent.startAnimation();
    }

    /**
     * This method is called every animation cycle if there are any
     * active animating areas.
     * @return true if this area requires further animation notifications
     */
    public boolean animate() {
	if (entered && ! hasPlayed) {
	    if (countDown > 0) {
		countDown--;
		return true;
	    }
	    hasPlayed = true;
	    if (soundData != null) {
	        soundData.play();
	    }
	}
	return false;
    }
}

