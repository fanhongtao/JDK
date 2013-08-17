/*
 * @(#)AudioClip.java	1.6 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
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
 * @version     1.9, 01/16/97
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
