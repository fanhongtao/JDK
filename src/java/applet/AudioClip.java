/*
 * @(#)AudioClip.java	1.15 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.applet;

/**
 * The <code>AudioClip</code> interface is a simple abstraction for
 * playing a sound clip. Multiple <code>AudioClip</code> items can be
 * playing at the same time, and the resulting sound is mixed
 * together to produce a composite.
 *
 * @author 	Arthur van Hoff
 * @version     1.15, 02/02/00
 * @since       JDK1.0
 */
public interface AudioClip {
    /**
     * Starts playing this audio clip. Each time this method is called,
     * the clip is restarted from the beginning.
     */
    void play();

    /**
     * Starts playing this audio clip in a loop.
     */
    void loop();

    /**
     * Stops playing this audio clip.
     */
    void stop();
}
