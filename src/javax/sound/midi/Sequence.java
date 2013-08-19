/*
 * @(#)Sequence.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;

import java.util.Vector;


/**
 * A <code>Sequence</code> is a data structure containing musical
 * information (often an entire song or composition) that can be played
 * back by a <code>{@link Sequencer}</code> object. Specifically, the
 * <code>Sequence</code> contains timing
 * information and one or more tracks.  Each <code>{@link Track track}</code> consists of a
 * series of MIDI events (such as note-ons, note-offs, program changes, and meta-events).
 * The sequence's timing information specifies the type of unit that is used
 * to time-stamp the events in the sequence.
 * <p>
 * A <code>Sequence</code> can be created from a MIDI file by reading the file
 * into an input stream and invoking one of the <code>getSequence</code> methods of
 * {@link MidiSystem}.  A sequence can also be built from scratch by adding new
 * <code>Tracks</code> to an empty <code>Sequence</code>, and adding
 * <code>{@link MidiEvent}</code> objects to these <code>Tracks</code>.
 *
 * @see Sequencer#setSequence(java.io.InputStream stream)
 * @see Sequencer#setSequence(Sequence sequence)
 * @see Track#add(MidiEvent)
 * @see MidiFileFormat
 *
 * @version 1.24, 03/01/23
 * @author Kara Kytle
 */
public class Sequence {


    // Timing types

    /**
     * The tempo-based timing type, for which the resolution is expressed in pulses (ticks) per quarter note.
     * @see #Sequence(float, int)
     */
    public static final float PPQ							= 0.0f;

    /**
     * The SMPTE-based timing type with 24 frames per second (resolution is expressed in ticks per frame).
     * @see #Sequence(float, int)
     */
    public static final float SMPTE_24						= 24.0f;

    /**
     * The SMPTE-based timing type with 25 frames per second (resolution is expressed in ticks per frame).
     * @see #Sequence(float, int)
     */
    public static final float SMPTE_25						= 25.0f;

    /**
     * The SMPTE-based timing type with 29.97 frames per second (resolution is expressed in ticks per frame).
     * @see #Sequence(float, int)
     */
    public static final float SMPTE_30DROP					= 29.97f;

    /**
     * The SMPTE-based timing type with 30 frames per second (resolution is expressed in ticks per frame).
     * @see #Sequence(float, int)
     */
    public static final float SMPTE_30						= 30.0f;


    // Variables

    /**
     * The timing division type of the sequence.
     * @see #PPQ
     * @see #SMPTE_24
     * @see #SMPTE_25
     * @see #SMPTE_30DROP
     * @see #SMPTE_30
     * @see #getDivisionType
     */
    protected float divisionType;

    /**
     * The timing resolution of the sequence.
     * @see #getResolution
     */
    protected int resolution;

    /**
     * The MIDI tracks in this sequence.
     * @see #getTracks
     */
    protected Vector tracks = new Vector();


    /**
     * Constructs a new MIDI sequence with the specified timing division
     * type and timing resolution.  The division type must be one of the
     * recognized MIDI timing types.  For tempo-based timing,
     * <code>divisionType</code> is PPQ (pulses per quarter note) and
     * the resolution is specified in ticks per beat.  For SMTPE timing,
     * <code>divisionType</code> specifies the number of frames per
     * second and the resolution is specified in ticks per frame.
     * The sequence will contain no initial tracks.  Tracks may be
     * added to or removed from the sequence using <code>{@link #createTrack}</code>
     * and <code>{@link #deleteTrack}</code>.
     *
     * @param divisionType the timing division type (PPQ or one of the SMPTE types)
     * @param resolution the timing resolution
     * @throws InvalidMidiDataException if <code>divisionType</code> is not valid
     *
     * @see #PPQ
     * @see #SMPTE_24
     * @see #SMPTE_25
     * @see #SMPTE_30DROP
     * @see #SMPTE_30
     * @see #getDivisionType
     * @see #getResolution
     * @see #getTracks
     */
    public Sequence(float divisionType, int resolution) throws InvalidMidiDataException {

	if (divisionType == PPQ)
	    this.divisionType = PPQ;
	else if (divisionType == SMPTE_24)
	    this.divisionType = SMPTE_24;
	else if (divisionType == SMPTE_25)
	    this.divisionType = SMPTE_25;
	else if (divisionType == SMPTE_30DROP)
	    this.divisionType = SMPTE_30DROP;
	else if (divisionType == SMPTE_30)
	    this.divisionType = SMPTE_30;
	else throw new InvalidMidiDataException("Unsupported division type: " + divisionType);

	this.resolution = resolution;
    }


    /**
     * Constructs a new MIDI sequence with the specified timing division
     * type, timing resolution, and number of tracks.  The division type must be one of the
     * recognized MIDI timing types.  For tempo-based timing,
     * <code>divisionType</code> is PPQ (pulses per quarter note) and
     * the resolution is specified in ticks per beat.  For SMTPE timing,
     * <code>divisionType</code> specifies the number of frames per
     * second and the resolution is specified in ticks per frame.
     * The sequence will be initialized with the number of tracks specified by
     * <code>numTracks</code>. These tracks are initially empty (i.e.
     * they contain only the meta-event End of Track).
     * The tracks may be retrieved for editing using the <code>{@link #getTracks}</code>
     * method.  Additional tracks may be added, or existing tracks removed,
     * using <code>{@link #createTrack}</code> and <code>{@link #deleteTrack}</code>.
     *
     * @param divisionType the timing division type (PPQ or one of the SMPTE types)
     * @param resolution the timing resolution
     * @param numTracks the initial number of tracks in the sequence.
     * @throws InvalidMidiDataException if <code>divisionType</code> is not valid
     *
     * @see #PPQ
     * @see #SMPTE_24
     * @see #SMPTE_25
     * @see #SMPTE_30DROP
     * @see #SMPTE_30
     * @see #getDivisionType
     * @see #getResolution
     */
    public Sequence(float divisionType, int resolution, int numTracks) throws InvalidMidiDataException {

	if (divisionType == PPQ)
	    this.divisionType = PPQ;
	else if (divisionType == SMPTE_24)
	    this.divisionType = SMPTE_24;
	else if (divisionType == SMPTE_25)
	    this.divisionType = SMPTE_25;
	else if (divisionType == SMPTE_30DROP)
	    this.divisionType = SMPTE_30DROP;
	else if (divisionType == SMPTE_30)
	    this.divisionType = SMPTE_30;
	else throw new InvalidMidiDataException("Unsupported division type: " + divisionType);

	this.resolution = resolution;

	for (int i = 0; i < numTracks; i++) {
	    tracks.addElement(new Track());
	}
    }


    /**
     * Obtains the timing division type for this sequence.
     * @return the division type (PPQ or one of the SMPTE types)
     *
     * @see #PPQ
     * @see #SMPTE_24
     * @see #SMPTE_25
     * @see #SMPTE_30DROP
     * @see #SMPTE_30
     * @see #Sequence(float, int)
     * @see MidiFileFormat#getDivisionType()
     */
    public float getDivisionType() {
	return divisionType;
    }


    /**
     * Obtains the timing resolution for this sequence.
     * If the sequence's division type is PPQ, the resolution is specified in ticks per beat.
     * For SMTPE timing, the resolution is specified in ticks per frame.
     *
     * @return the number of ticks per beat (PPQ) or per frame (SMPTE)
     * @see #getDivisionType
     * @see #Sequence(float, int)
     * @see MidiFileFormat#getResolution()
     */
    public int getResolution() {
	return resolution;
    }


    /**
     * Creates a new, initially empty track as part of this sequence.
     * The track initially contains the meta-event End of Track.
     * The newly created track is returned.  All tracks in the sequence
     * may be retrieved using <code>{@link #getTracks}</code>.  Tracks may be
     * removed from the sequence using <code>{@link #deleteTrack}</code>.
     * @return the newly created track
     */
    public Track createTrack() {

	Track track = new Track();
	tracks.addElement(track);

	return track;
    }


    /**
     * Removes the specified track from the sequence.
     * @param track the track to remove
     * @return <code>true</code> if the track existed in the track and was removed,
     * otherwise <code>false</code>.
     *
     * @see #createTrack
     * @see #getTracks
     */
    public boolean deleteTrack(Track track) {

	synchronized(tracks) {

	    return tracks.removeElement(track);
	}
    }


    /**
     * Obtains an array containing all the tracks in this sequence.
     * If the sequence contains no tracks, an array of length 0 is returned.
     * @return the array of tracks
     *
     * @see #createTrack
     * @see #deleteTrack
     */
    public Track[] getTracks() {

	synchronized(tracks) {

	    Track[] trackArray = new Track[tracks.size()];

	    for (int i = 0; i < trackArray.length; i++) {
		trackArray[i] = (Track)tracks.elementAt(i);
	    }

	    return trackArray;
	}
    }


    /**
     * Obtains the duration of this sequence, expressed in microseconds.
     * @return this sequence's duration in microseconds.
     */
    public long getMicrosecondLength() {

	long ticks = getTickLength();

	double seconds;

	// now convert ticks to time

	if( divisionType != PPQ ) {

	    seconds = ( (double)getTickLength() / (double)( divisionType * resolution ) );

	    //$$fb 2002-10-30: fix for 4702328: Wrong time in sequence for SMPTE based types
	    return (long) (1000000 * seconds);
	} else {

	    Track tempos           = new Track();
	    Track tmpTrack         = null;
	    MidiEvent tmpEvent     = null;
	    MidiMessage tmpMessage = null;
	    MetaMessage tmpMeta    = null;
	    byte[] data;

	    // find all tempo events
	    synchronized(tracks) {
		for(int i=0; i<tracks.size(); i++ ) {
		    tmpTrack = (Track)tracks.elementAt(i);
		    for(int j=0; j < tmpTrack.size(); j++) {
			tmpEvent=(MidiEvent)tmpTrack.get(j);
			tmpMessage=tmpEvent.getMessage();
			if( tmpMessage instanceof MetaMessage ) {

			    if( ((MetaMessage)tmpMessage).getType() == 0x51 ) {
				tempos.add(tmpEvent);
			    }
			}
		    }
		}
	    }
	    // now add up chunks of time
	    int tempo = (60 * 1000000) / 120;	// default 120 bpm, converted to uSec/beat
	    long microseconds = 0;
	    long runningTick = 0;
	    long tmpTick = 0;

	    // loop through the tempo changes, but don't
	    // include the last event, which is track end
	    for(int i=0; i<(tempos.size()-1); i++) {

		tmpEvent = (MidiEvent)tempos.get(i);
		tmpTick = tmpEvent.getTick();

		if(tmpTick>=runningTick) {
		    microseconds += ((tmpTick-runningTick) * tempo / resolution);
		    runningTick = tmpTick;
		    data = ((MetaMessage)(tmpEvent.getMessage())).getMessage();

		    // data[0] => status, 0xFF
		    // data[1] => type,   0x51
		    // data[2] => length, 0x03
		    // data[3] -> data[5] => tempo data
		    tempo = (int) 0xff&data[5];
		    tempo = tempo | ( (0xff&data[4]) << 8 );
		    tempo = tempo | ( (0xff&data[3]) << 16 );
		}
	    }
	    tmpTick = getTickLength();
	    if( tmpTick>runningTick ) {
		microseconds += ((tmpTick-runningTick) * tempo / resolution);
	    }

	    // return in microseconds
	    return (microseconds);
	}

    }


    /**
     * Obtains the duration of this sequence, expressed in MIDI ticks.
     *
     * @return this sequence's length in ticks
     *
     * @see #getMicrosecondLength
     */
    public long getTickLength() {

	long length = 0;

	synchronized(tracks) {

	    for(int i=0; i<tracks.size(); i++ ) {
		long temp = ((Track)tracks.elementAt(i)).ticks();
		if( temp>length ) {
		    length = temp;
		}
	    }
	    return length;
	}
    }


    /**
     * Obtains a list of patches referenced in this sequence.
     * This patch list may be used to load the required
     * <code>{@link Instrument}</code> objects
     * into a <code>{@link Synthesizer}</code>.
     *
     * @return an array of <code>{@link Patch}</code> objects used in this sequence
     *
     * @see Synthesizer#loadInstruments(Soundbank, Patch[])
     */
    public Patch[] getPatchList() {

	// $$kk: 04.09.99: need to implement!!
	return new Patch[0];
    }
}
