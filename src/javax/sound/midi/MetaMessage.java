/*
 * @(#)MetaMessage.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * A <code>MetaMessage</code> is a <code>{@link MidiMessage}</code> that is not meaningful to synthesizers, but
 * that can be stored in a MIDI file and interpreted by a sequencer program.
 * (See the discussion in the <code>MidiMessage</code>
 * class description.)  The Standard MIDI Files specification defines
 * various types of meta-events, such as sequence number, lyric, cue point,
 * and set tempo.  There are also meta-events
 * for such information as lyrics, copyrights, tempo indications, time and key
 * signatures, markers, etc.  For more information, see the Standard MIDI Files 1.0
 * specification, which is part of the Complete MIDI 1.0 Detailed Specification
 * published by the MIDI Manufacturer's Association
 * (<a href = http://www.midi.org>http://www.midi.org</a>).
 *
 * <p>
 * When data is being transported using MIDI wire protocol,
 * a <code>{@link ShortMessage}</code> with the status value <code>0xFF</code> represents
 * a system reset message.  In MIDI files, this same status value denotes a <code>MetaMessage</code>.
 * The types of meta-message are distinguished from each other by the first byte
 * that follows the status byte <code>0xFF</code>.  The subsequent bytes are data
 * bytes.  As with system exclusive messages, there are an arbitrary number of
 * data bytes, depending on the type of <code>MetaMessage</code>.
 *
 * @see MetaEventListener
 *
 * @version 1.22, 03/01/23
 * @author David Rivas
 * @author Kara Kytle
 */

public class MetaMessage extends MidiMessage {


    // Status byte defines

    /**
     * Status byte for <code>MetaMessage</code> (0xFF, or 255), which is used
     * in MIDI files.  It has the same value as SYSTEM_RESET, which
     * is used in the real-time "MIDI wire" protocol.
     * @see MidiMessage#getStatus
     */
    public static final int META						= 0xFF; // 255


    // Default meta message data: just the META status byte value
    // $$kk: 09.09.99: need a real event here!!

    private static byte[] defaultMessage				= { (byte)META, 0 };



    // Instance variables

    /**
     * The length of the actual message in the data array.
     * This is used to determine how many bytes of the data array
     * is the message, and how many are the status byte, the
     * type byte, and the variable-length-int describing the
     * length of the message.
     */
    private int dataLength = 0;


    /**
     * Constructs a new <code>MetaMessage</code>. The contents of
     * the message are not set here; use
     * {@link #setMessage(int, byte[], int) setMessage}
     * to set them subsequently.
     */
    public MetaMessage() {
	//super(defaultMessage);
	this(defaultMessage);
    }


    /**
     * Constructs a new <code>MetaMessage</code>.
     * @param data an array of bytes containing the complete message.
     * The message data may be changed using the <code>setMessage</code>
     * method.
     * @see #setMessage
     */
    protected MetaMessage(byte[] data) {
	super(data);
	//$$fb 2001-10-06: need to calculate dataLength. Fix for bug #4511796
	if (data.length>=3) {
	    dataLength=data.length-3;
	    int pos=2;
	    while (pos<data.length && (data[pos] & 0x80)!=0) {
		dataLength--; pos++;
	    }
	}
    }


    /**
     * Sets the message parameters for a <code>MetaMessage</code>.
     * Since only one status byte value, <code>0xFF</code>, is allowed for meta-messages,
     * it does not need to be specified here.  Calls to <code>{@link MidiMessage#getStatus getStatus}</code> return
     * <code>0xFF</code> for all meta-messages.
     * <p>
     * The <code>type</code> argument should be a valid value for the byte that
     * follows the status byte in the <code>MetaMessage</code>.  The <code>data</code> argument
     * should contain all the subsequent bytes of the <code>MetaMessage</code>.  In other words,
     * the byte that specifies the type of <code>MetaMessage</code> is not considered a data byte.
     *
     * @param type		meta-message type (must be less than 128)
     * @param data		the data bytes in the MIDI message
     * @param length	the number of bytes in the <code>data</code>
     * byte array
     * @throws			<code>InvalidMidiDataException</code>  if the
     * parameter values do not specify a valid MIDI meta message
     */
    public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException {

	if (type >= 128 || type < 0) {
	    throw new InvalidMidiDataException("Invalid meta event with type " + type);
	}
	if ((length > 0 && length > data.length) || length < 0) {
	    throw new InvalidMidiDataException("length out of bounds: "+length);
	}
	
	/*
	  int oldLength=0;
	  int oldDataLength=0;
	  byte[] oldData=null;
	  try {
	  //$$fb 2001-10-06: this is a very inefficient implementation !
	  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	  DataOutputStream dos = new DataOutputStream(bos);

	  dos.writeByte(0xFF);	// status value for MetaMessages (meta events)
	  dos.writeByte(type);	// MetaMessage type

	  writeVarInt( length, dos );   // write the length as a
	  // variable int
	  dos.write(data, 0, length);

	  //this.data = bos.toByteArray();
	  //this.length = this.data.length;
	  //this.dataLength = length;
	  oldData = bos.toByteArray();
	  oldLength = oldData.length;
	  oldDataLength = length;
	  } catch (IOException e) {
	  }
	*/
	
	this.length = 2 + getVarIntLength(length) + length;
	this.dataLength = length;
	this.data = new byte[this.length];
	this.data[0] = (byte) META;        // status value for MetaMessages (meta events)
	this.data[1] = (byte) type;        // MetaMessage type
	writeVarInt(this.data, 2, length); // write the length as a variable int
	if (length > 0) {
	    System.arraycopy(data, 0, this.data, this.length - this.dataLength, this.dataLength);
	}
	
	/*
	  // check equality
	  if (this.length != oldLength) {
	  System.out.println("length = "+length+"   oldLength = "+oldLength);
	  } else
	  if (this.dataLength != oldDataLength) {
	  System.out.println("dataLength = "+dataLength+"   oldDataLength = "+oldDataLength);
	  } else
	  if (this.data.length != oldData.length) {
	  System.out.println("this.data.length = "+this.data.length+"   oldData.length = "+oldData.length);
	  } else {
	  boolean succ=true;
	  for (int i=0; i<this.data.length; i++) {
	  if (this.data[i]!=oldData[i]) {
	  succ=false;
	  System.out.println(" this.data["+i+"] = "+this.data[i]+"   oldData["+i+"] = "+oldData[i]);
	  }
	  }
	  if (succ) {
	  System.out.println("Message with length = "+length+" is equal.");
	  }
	  }
	*/
    }


    /**
     * Obtains the type of the <code>MetaMessage</code>.
     * @return an integer representing the <code>MetaMessage</code> type
     */
    public int getType() {
	if (length>=2) {
	    return data[1] & 0xFF;
	}
	return 0;
    }



    /**
     * Obtains a copy of the data for the meta message.  The returned
     * array of bytes does not include the status byte or the message
     * length data.  The length of the data for the meta message is
     * the length of the array.  Note that the length of the entire
     * message includes the status byte and the meta message type
     * byte, and therefore may be longer than the returned array.
     * @return array containing the meta message data.
     * @see MidiMessage#getLength
     */
    public byte[] getData() {
	byte[] returnedArray = new byte[dataLength];
	System.arraycopy(data, (length - dataLength), returnedArray, 0, dataLength);
	return returnedArray;
    }


    /**
     * Creates a new object of the same class and with the same contents
     * as this object.
     * @return a clone of this instance
     */
    public Object clone() {
	byte[] newData = new byte[length];
	System.arraycopy(data, 0, newData, 0, newData.length);

	MetaMessage event = new MetaMessage(newData);
	return event;
    }

    // HELPER METHODS

    /*
      private void writeVarInt(int value, DataOutputStream dos ) throws InvalidMidiDataException, IOException {

      int MAX_LENGTH = 6;
      byte bytes[] = new byte[MAX_LENGTH];
      int length = 0;
      int currentByte = 0;

      // first make sure our array is zeroed
      for(int i=0; i<MAX_LENGTH; i++) bytes[i] = 0;

      // we fill this array up from the end so that bytes will be
      // written to the stream msb first
      for(int i=MAX_LENGTH-1; i >= 0; i--) {

      bytes[i] = (byte) (value & 0x7F);
      value = value >>> 7;
      length++;

      if( length>1 ) {
      // this is not the last byte to be written to the stream,
      // so change the msb to 1
      bytes[i] |= 0x80;
      }
      if( value==0 ) {
				// we don't need any more bytes to store this integer
				break;
				}
				}
				// make sure we terminated properly
				if ( (bytes[MAX_LENGTH-1] & 0x80) != 0 ) {

				throw new InvalidMidiDataException("Unable to create variable-length integer");
				}

				// now write our bytes, msb first, to the stream
				dos.write(bytes, (MAX_LENGTH-length), length);
				}
    */

    private int getVarIntLength(long value) {
	int length = 0;
	do {
	    value = value >> 7;
	    length++;
	} while (value > 0);
	return length;
    }
    
    private final static long mask = 0x7F;

    private void writeVarInt(byte[] data, int off, long value) {
    	int shift=63; // number of bitwise left-shifts of mask
    	// first screen out leading zeros
    	while ((shift > 0) && ((value & (mask << shift)) == 0)) shift-=7;
    	// then write actual values
    	while (shift > 0) {
	    data[off++]=(byte) (((value & (mask << shift)) >> shift) | 0x80);
	    shift-=7;
    	}
    	data[off] = (byte) (value & mask);
    }

}
