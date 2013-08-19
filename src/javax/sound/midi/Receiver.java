/*
 * @(#)Receiver.java	1.19 03/01/27
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;		  	 


/**
 * A <code>Receiver</code> receives <code>{@link MidiEvent}</code> objects and
 * typically does something useful in response, such as interpreting them to
 * generate sound or raw MIDI output.  Common MIDI receivers include 
 * synthesizers and MIDI Out ports.  
 *
 * @see MidiDevice
 * @see Synthesizer
 * @see Transmitter
 *
 * @version 1.19, 03/01/27
 * @author Kara Kytle
 */
public interface Receiver {


    //$$fb 2002-04-12: fix for 4662090: Contradiction in Receiver specification
    /**
     * Sends a MIDI message and time-stamp to this receiver.
     * If time-stamping is not supported by this receiver, the time-stamp 
     * value should be -1.
     * @param message the MIDI message to send
     * @param timeStamp the time-stamp for the message, in microseconds.
     * @throws IllegalStateException if the receiver is closed
     */ 
    public void send(MidiMessage message, long timeStamp);													 
	
    /**
     * Indicates that the application has finished using the receiver, and
     * that limited resources it requires may be released or made available.
     * Invoking methods on a receiver which has been closed may cause an 
     * <code>IllegalArgumentException</code> or other exception to be thrown.
     */
    public void close();
}
