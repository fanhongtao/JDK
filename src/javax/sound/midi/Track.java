/*
 * @(#)Track.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;

import java.util.Vector;

/**
 * A MIDI track is an independent stream of MIDI events (time-stamped MIDI
 * data) that can be stored along with other tracks in a standard MIDI file.
 * The MIDI specification allows only 16 channels of MIDI data, but tracks
 * are a way to get around this limitation.  A MIDI file can contain any number
 * of tracks, each containing its own stream of up to 16 channels of MIDI data.
 * <p>
 * A <code>Track</code> occupies a middle level in the hierarchy of data played
 * by a <code>{@link Sequencer}</code>: sequencers play sequences, which contain tracks,
 * which contain MIDI events.  A sequencer may provide controls that mute
 * or solo individual tracks.
 * <p>
 * The timing information and resolution for a track is controlled by and stored
 * in the sequence containing the track. A given <code>Track</code>
 * is considered to belong to the particular <code>{@link Sequence}</code> that
 * maintains its timing. For this reason, a new (empty) track is created by calling the
 * <code>{@link Sequence#createTrack}</code> method, rather than by directly invoking a
 * <code>Track</code> constructor.
 * <p>
 * The <code>Track</code> class provides methods to edit the track by adding
 * or removing <code>MidiEvent</code> objects from it.  These operations keep
 * the event list in the correct time order.  Methods are also
 * included to obtain the track's size, in terms of either the number of events
 * it contains or its duration in ticks.
 *
 * @see Sequencer#setTrackMute
 * @see Sequencer#setTrackSolo
 *
 * @version 1.19, 03/01/23
 * @author Kara Kytle
 */
public class Track {

    /**
     * The list of <code>MidiEvents</code> contained in this track.
     */
    protected Vector events = new Vector();
    
    // $$fb 2002-07-17: use a hashset to detect same events in add(MidiEvent)
    // this requires at least JDK1.2
    private java.util.HashSet set = new java.util.HashSet();


    /**
     * Package-private constructor.  Constructs a new, empty Track object,
     * which initially contains one event, the meta-event End of Track.
     */
    Track() {

	// $$jb: 10.18.99: start with the end of track event

	MetaMessage eot = new MetaMessage();
	try {
	    eot.setMessage( 0x2F, new byte[0], 0 );
	} catch (InvalidMidiDataException e) {
	    // this should never happen.
	}
	MidiEvent eotevent = new MidiEvent( eot, 0 );
	events.addElement( eotevent );
	set.add(eotevent);
    }


    /**
     * Adds a new event to the track.  However, if the event is already
     * contained in the track, it is not added again.  The list of events
     * is kept in time order, meaning that this event inserted at the
     * appropriate place in the list, not necessarily at the end.
     *
     * @param event the event to add
     * @return <code>true</code> if the event did not already exist in the
     * track and was added, otherwise <code>false</code>
     */
    public boolean add(MidiEvent event) {

	// $$kk: 01.21.99: i guess i will refuse to add the event if
	// it already exists in the event vector.  otherwise people will
	// do event.setData(...) and add(event) and create a disaster.

	int i;

	synchronized(events) {

	    if ( !set.contains(event) ) {

		// $$jb: 10.18.99: first see if we are trying to add
		// and endoftrack event.  since this event is useful
		// for delays at the end of a track, we want to keep
		// the tick value requested here if it is greater
		// than the one on the eot we are maintaining.  otherwise,
		// we only want a single eot event, so ignore.

		if( event.getMessage().getStatus() == 0xff ) {
		    MetaMessage mm = (MetaMessage)(event.getMessage());
		    if (mm.getType() == 0x2f) {

			MidiEvent eot = (MidiEvent) events.elementAt( events.size()-1 );
			if( event.getTick() > eot.getTick() ) {
			    eot.setTick( event.getTick() );
			}
			return true;
		    }
		}

		// insert event such that events is sorted in increasing
		// tick order

	        set.add(event);
		if(events.size()==0) {
		    events.addElement(event);
		    return true;
		} else {
		    for( i=events.size(); i > 0; i-- ) {

			if( event.getTick() >= ((MidiEvent)events.elementAt(i-1)).getTick() ) {
			    break;
			}
		    }
		    if( i==events.size() ) {
			// $$jb: 10.18.99: we're adding an event after the
			// tick value of our eot, so push the eot out
			((MidiEvent)events.elementAt(i-1)).setTick( event.getTick() );
			events.insertElementAt(event, (i-1) );
		    } else {
			events.insertElementAt(event,i);
		    }
		    return true;
		}
	    }
	}

	return false;
    }


    /**
     * Removes the specified event from the track.
     * @param event the event to remove
     * @return <code>true</code> if the event existed in the track and was removed,
     * otherwise <code>false</code>
     */
    public boolean remove(MidiEvent event) {

	synchronized(events) {
	    set.remove(event);
	    return events.removeElement(event);
	}
    }


    /**
     * Obtains the event at the specified index.
     * @param index the location of the desired event in the event vector
     * @throws <code>ArrayIndexOutOfBoundsException</code>  if the
     * specified index is negative or not less than the current size of
     * this track.
     * @see #size
     */
    public MidiEvent get(int index) throws ArrayIndexOutOfBoundsException {

	return (MidiEvent)events.elementAt(index);		// can throw ArrayIndexOutOfBoundsException
    }


    /**
     * Obtains the number of events in this track.
     * @return the size of the track's event vector
     */
    public int size() {

	return events.size();
    }


    /**
     * Obtains the length of the track, expressed in MIDI ticks.  (The
     * duration of a tick in seconds is determined by the timing resolution
     * of the <code>Sequence</code> containing this track, and also by
     * the tempo of the music as set by the sequencer.)
     * @return the duration, in ticks
     * @see Sequence#Sequence(float, int)
     * @see Sequencer#setTempoInBPM(float)
     * @see Sequencer#getTickPosition()
     */
    public long ticks() {

	return ((MidiEvent)events.lastElement()).getTick();
    }
}
