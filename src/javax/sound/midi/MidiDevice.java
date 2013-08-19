/*
 * @(#)MidiDevice.java	1.34 03/01/27
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;


/**
 * <code>MidiDevice</code> is the base interface for all MIDI devices.
 * Common devices include synthesizers, sequencers, MIDI input ports, and MIDI
 * output ports.  A <code>MidiDevice</code>
 * can be a transmitter or a receiver of MIDI events, or both.  To this end, it
 * typically also implements the <code>{@link Transmitter}</code> or
 * <code>{@link Receiver}</code> interface (or both), or has access to objects that do.
 * <p>
 * A <code>MidiDevice</code> includes a <code>{@link MidiDevice.Info}</code> object
 * to provide manufacturer information and so on.
 *
 * @see Synthesizer
 * @see Sequencer
 * @see MidiChannel#setMono(boolean)
 *
 * @version 1.34, 03/01/27
 * @author Kara Kytle
 */

public interface MidiDevice {


    /**
     * Obtains information about the device, including its Java class and
     * <code>Strings</code> containing its name, vendor, and description.
     *
     * @return device info
     */
    public Info getDeviceInfo();


    /**
     * Opens the device, indicating that it should now acquire any
     * system resources it requires and become operational.
     * <p>
     * Note that some devices, once closed, cannot be reopened.  Attempts
     * to reopen such a device will always result in a MidiUnavailableException.
     *
     * @throws MidiUnavailableException thrown if the device cannot be
     * opened due to resource restrictions.
     * @throws SecurityException thrown if the device cannot be
     * opened due to security restrictions.
     *
     * @see #close
     * @see #isOpen
     */
    public void open() throws MidiUnavailableException;


    /**
     * Closes the device, indicating that the device should now release
     * any system resources it is using.
     *
     * @see #open
     * @see #isOpen
     */
    public void close();


    /**
     * Reports whether the device is open.  The mechanism for
     * opening particular devices is defined by subinterfaces
     * and/or by classes implementing this interface.
     *
     * @return <code>true</code> if the device is open, otherwise
     * <code>false</code>
     * @see #close
     */
    public boolean isOpen();


    /**
     * Obtains the current time-stamp of the device, in microseconds.
     * If a device supports time-stamps, it should start counting at
     * 0 when the device is opened and continue incrementing its
     * time-stamp in microseconds until the device is closed.
     * If it does not support time-stamps, it should always return
     * -1.
     * @return the current time-stamp of the device in microseconds,
     * or -1 if time-stamping is not supported by the device.
     */
    public long getMicrosecondPosition();


    /**
     * Obtains the maximum number of MIDI IN connections available on this
     * MIDI device for receiving MIDI data.
     * @return maximum number of MIDI IN connections, 
     * or -1 if an unlimited number of connections is available.
     */
    public int getMaxReceivers();


    /**
     * Obtains the maximum number of MIDI OUT connections available on this
     * MIDI device for transmitting MIDI data.
     * @return maximum number of MIDI OUT connections,
     * or -1 if an unlimited number of connections is available.
     */
    public int getMaxTransmitters();


    /**
     * Obtains the maximum number of MIDI THRU connections available on this
     * MIDI device for transmitting MIDI data.
     * @return maximum number of MIDI THRU connections
     */
    //public int getMaxThruTransmitters();


    /**
     * Obtains a MIDI IN receiver through which the MIDI device may receive
     * MIDI data.  The returned receiver must be closed when the application
     * has finished using it.
     * @return a receiver for the device.
     * @throws MidiUnavailableException thrown if a receiver is not available
     * due to resource restrictions
     * @see Receiver#close()
     */
    public Receiver getReceiver() throws MidiUnavailableException;


    /**
     * Obtains a MIDI OUT connection from which the MIDI device will transmit
     * MIDI data  The returned transmitter must be closed when the application
     * has finished using it.
     * @return a MIDI OUT transmitter for the device.
     * @throws MidiUnavailableException thrown if a transmitter is not available
     * due to resource restrictions
     * @see Transmitter#close()
     */
    public Transmitter getTransmitter() throws MidiUnavailableException;


    /**
     * Obtains a MIDI THRU connection from which the MIDI device will transmit
     * MIDI data  The returned transmitter must be closed when the application
     * has finished using it.
     * @return a MIDI THRU transmitter for the device.
     * @throws MidiUnavailableException thrown if a transmitter is not available
     * due to resource restrictions
     * @see Transmitter#close()
     */
    //public Transmitter getThruTransmitter() throws MidiUnavailableException;


    /**
     * A <code>MidiDevice.Info</code> object contains assorted
     * data about a <code>{@link MidiDevice}</code>, including its
     * name, the company who created it, and descriptive text.
     *
     * @see MidiDevice#getDeviceInfo
     */
    public static class Info {

	/**
	 * The device's name.
	 */
	private String name;

	/**
	 * The name of the company who provides the device.
	 */
	private String vendor;

	/**
	 * A description of the device.
	 */
	private String description;

	/**
	 * Device version.
	 */
	private String version;


	/**
	 * Constructs a device info object.
	 *
	 * @param name the name of the device
	 * @param vendor the name of the company who provides the device
	 * @param description a description of the device
	 * @param version version information for the device
	 */
	protected Info(String name, String vendor, String description, String version) {

	    this.name = name;
	    this.vendor = vendor;
	    this.description = description;
	    this.version = version;
	}


	/**
	 * Reports whether two objects are equal.
	 * Returns <code>true</code> if the objects are identical.
	 * @param obj the reference object with which to compare this
	 * object
	 * @return <code>true</code> if this object is the same as the
	 * <code>obj</code> argument; <code>false</code> otherwise
	 */
	public final boolean equals(Object obj) {
	    return super.equals(obj);
	}


	/**
	 * Finalizes the hashcode method.
	 */
	public final int hashCode() {
	    return super.hashCode();
	}


	/**
	 * Obtains the name of the device.
	 *
	 * @return a string containing the device's name
	 */
	public final String getName() {
	    return name;
	}


	/**
	 * Obtains the name of the company who supplies the device.
	 * @return device the vendor's name
	 */
	public final String getVendor() {
	    return vendor;
	}


	/**
	 * Obtains the description of the device.
	 * @return a description of the device
	 */
	public final String getDescription() {
	    return description;
	}


	/**
	 * Obtains the version of the device.
	 * @return textual version information for the device.
	 */
	public final String getVersion() {
	    return version;
	}


	/**
	 * Provides a string representation of the device information.

	 * @return a description of the info object
	 */
	public final String toString() {
	    return name;
	}
    } // class Info


    // OLD


    /**
     * MIDI Mode 1: Omni On/Poly.
     *
     * @see #setMode(int)
     */
    //public static final int OMNI_ON_POLY	= 1;


    /**
     * MIDI Mode 2: Omni On/Mono.
     *
     * @see #setMode(int)
     */
    //public static final int OMNI_ON_MONO	= 2;


    /**
     * MIDI Mode 3: Omni Off/Poly.
     *
     * @see #setMode(int)
     */
    //public static final int OMNI_OFF_POLY	= 3;


    /**
     * MIDI Mode 4: Omni Off/Mono.
     *
     * @see #setMode(int)
     */
    //public static final int OMNI_OFF_MONO	= 4;


    /**
     * Sets the current omni and mono/poly mode.  The argument should be
     * one of the integers returned by <code>getModes()</code>.  Any other
     * value will be ignored, leaving the current mode unchanged.
     *
     * @param mode the desired new mode
     *
     * @see #OMNI_ON_POLY
     * @see #OMNI_ON_MONO
     * @see #OMNI_OFF_POLY
     * @see #OMNI_OFF_MONO
     * @see #getMode
     * @see #getModes
     */
    //public void setMode(int mode);


    /**
     * Obtains the current omni and mono/poly mode.
     *
     * @return the current mode
     *
     * @see #OMNI_ON_POLY
     * @see #OMNI_ON_MONO
     * @see #OMNI_OFF_POLY
     * @see #OMNI_OFF_MONO
     * @see #setMode(int)
     * @see #getModes
     */
    //public int getMode();


    /**
     * Obtains the set of omni and mono/poly modes supported by this device.
     *
     * @return the list of supported modes
     *
     * @see #OMNI_ON_POLY
     * @see #OMNI_ON_MONO
     * @see #OMNI_OFF_POLY
     * @see #OMNI_OFF_MONO
     * @see #getMode
     * @see #setMode(int)
     */
    //public int[] getModes();

}
