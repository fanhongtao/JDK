/*
 * @(#)MidiFileFormat.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;

import java.io.InputStream;
import java.io.IOException;

/**
 * A <code>MidiFileFormat</code> object encapsulates a MIDI file's
 * type, as well as its length and timing information.
 *
 * @see MidiSystem#getMidiFileFormat(java.io.File)
 * @see Sequencer#setSequence(java.io.InputStream stream)
 *
 * @version 1.15, 03/01/23
 * @author Kara Kytle
 */

public class MidiFileFormat {


    /**
     * Represents unknown length.
     * @see #getByteLength
     * @see #getMicrosecondLength
     */
    public static final int UNKNOWN_LENGTH = -1;


    /**
     * The type of MIDI file.
     */
    protected int type;

    /**
     * The division type of the MIDI file.
     *
     * @see Sequence#PPQ
     * @see Sequence#SMPTE_24
     * @see Sequence#SMPTE_25
     * @see Sequence#SMPTE_30DROP
     * @see Sequence#SMPTE_30
     */
    protected float divisionType;

    /**
     * The timing resolution of the MIDI file.
     */
    protected int resolution;

    /**
     * The length of the MIDI file in bytes.
     */
    protected int byteLength;

    /**
     * The duration of the MIDI file in microseconds.
     */
    protected long microsecondLength;


    /**
     * Constructs a <code>MidiFileFormat</code>.
     *
     * @param type the MIDI file type (0, 1, or 2)
     * @param divisionType the timing division type (PPQ or one of the SMPTE types)
     * @param resolution the timing resolution
     * @param bytes the length of the MIDI file in bytes, or UNKNOWN_LENGTH if not known
     * @param microseconds the duration of the file in microseconds, or UNKNOWN_LENGTH if not known
     * @see #UNKNOWN_LENGTH
     * @see Sequence#PPQ
     * @see Sequence#SMPTE_24
     * @see Sequence#SMPTE_25
     * @see Sequence#SMPTE_30DROP
     * @see Sequence#SMPTE_30
     */
    public MidiFileFormat(int type, float divisionType, int resolution, int bytes, long microseconds) {

	this.type = type;
	this.divisionType = divisionType;
	this.resolution = resolution;
	this.byteLength = bytes;
	this.microsecondLength = microseconds;
    }


    /**
     * Obtains the MIDI file type.
     * @return the file's type (0, 1, or 2)
     */
    public int getType() {
	return type;
    }

    /**
     * Obtains the timing division type for the MIDI file.
     *
     * @return the division type (PPQ or one of the SMPTE types)
     *
     * @see Sequence#Sequence(float, int)
     * @see Sequence#PPQ
     * @see Sequence#SMPTE_24
     * @see Sequence#SMPTE_25
     * @see Sequence#SMPTE_30DROP
     * @see Sequence#SMPTE_30
     * @see Sequence#getDivisionType()
     */
    public float getDivisionType() {
	return divisionType;
    }


    /**
     * Obtains the timing resolution for the MIDI file.
     * If the division type is PPQ, the resolution is specified in ticks per beat.
     * For SMTPE timing, the resolution is specified in ticks per frame.
     *
     * @return the number of ticks per beat (PPQ) or per frame (SMPTE)
     * @see #getDivisionType
     * @see Sequence#getResolution()
     */
    public int getResolution() {
	return resolution;
    }


    /**
     * Obtains the length of the MIDI file, expressed in 8-bit bytes.
     * @return the number of bytes in the file, or UNKNOWN_LENGTH if not known
     * @see #UNKNOWN_LENGTH
     */
    public int getByteLength() {
	return byteLength;
    }

    /**
     * Obtains the length of the MIDI file, expressed in microseconds.
     * @return the file's duration in microseconds, or UNKNOWN_LENGTH if not known
     * @see Sequence#getMicrosecondLength()
     * @see #getByteLength
     * @see #UNKNOWN_LENGTH
     */
    public long getMicrosecondLength() {
	return microsecondLength;
    }
}


