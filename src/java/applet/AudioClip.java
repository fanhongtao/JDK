/*
 * @(#)AudioClip.java	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.applet;

/**
 * The <code>AudioClip</code> interface is a simple abstraction for 
 * playing a sound clip. Multiple <code>AudioClip</code> items can be 
 * playing at the same time, and the resulting sound is mixed 
 * together to produce a composite. 
 *
 * @author 	Arthur van Hoff
 * @version     1.11, 12/10/01
 * @since       JDK1.0
 */
public interface AudioClip {
    /**
     * Starts playing this audio clip. Each time this method is called, 
     * the clip is restarted from the beginning. 
     *
     * @since   JDK1.0
     */
    void play();

    /**
     * Starts playing this audio clip in a loop. 
     *
     * @since   JDK1.0
     */
    void loop();

    /**
     * Stops playing this audio clip. 
     *
     * @since   JDK1.0
     */
    void stop();
}
