/*
 * @(#)Transmitter.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;		  	 


/**
 * A <code>Transmitter</code> sends <code>{@link MidiEvent}</code> objects to one or more 
 * <code>{@link Receiver Receivers}</code>. Common MIDI transmitters include sequencers
 * and MIDI input ports.
 *
 * @see Receiver
 *
 * @version 1.20, 03/01/23
 * @author Kara Kytle
 */
public interface Transmitter {


    /**
     * Sets the receiver to which this transmitter will deliver MIDI messages.
     * If a receiver is currently set, it is replaced with this one.
     * @param receiver the desired receiver.
     */
    public void setReceiver(Receiver receiver);


    /**
     * Obtains the current receiver to which this transmitter will deliver MIDI messages. 
     * @return the current receiver.  If no receiver is currently set, 
     * returns <code>null</code>
     */
    public Receiver getReceiver();

	
    /**
     * Indicates that the application has finished using the transmitter, and
     * that limited resources it requires may be released or made available.
     * Invoking methods on a receiver which has been closed may cause an 
     * <code>IllegalArgumentException</code> or other exception to be thrown.
     */
    public void close();
}
