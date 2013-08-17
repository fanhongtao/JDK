/*
 * @(#)AudioClip.java	1.10 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.applet;

/**
 * The <code>AudioClip</code> interface is a simple abstraction for 
 * playing a sound clip. Multiple <code>AudioClip</code> items can be 
 * playing at the same time, and the resulting sound is mixed 
 * together to produce a composite. 
 *
 * @author 	Arthur van Hoff
 * @version     1.10, 07/01/98
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
