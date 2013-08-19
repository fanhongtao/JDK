/*
 * @(#)MidiSystem.java	1.49 03/01/27
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;

import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.midi.spi.MidiDeviceProvider;

/**
 * The <code>MidiSystem</code> class provides access to the installed MIDI
 * system resources, including devices such as synthesizers, sequencers, and
 * MIDI input and output ports.  A typical simple MIDI application might
 * begin by invoking one or more <code>MidiSystem</code> methods to learn
 * what devices are installed and to obtain the ones needed in that
 * application.
 * <p>
 * The class also has methods for reading files, streams, and  URLs that
 * contain standard MIDI file data or soundbanks.  You can query the
 * <code>MidiSystem</code> for the format of a specified MIDI file.
 * <p>
 * You cannot instantiate a <code>MidiSystem</code>; all the methods are
 * static.
 *
 * @version 1.49, 03/01/27
 * @author Kara Kytle
 */
public class MidiSystem {

    /**
     * Private strings for default services class and method
     */
    private static final String defaultServicesClassName =
	"com.sun.media.sound.DefaultServices";
    private static final String jdk13ServicesClassName =
	"com.sun.media.sound.JDK13Services";

    private static final String servicesMethodName =
	"getProviders";
    private static final Class[] servicesParamTypes =
	new Class[] { String.class };


    /**
     * Private no-args constructor for ensuring against instantiation.
     */
    private MidiSystem() {
    }


    /**
     * Obtains an array of information objects representing
     * the set of all MIDI devices available on the system.
     * A returned information object can then be used to obtain the
     * corresponding device object, by invoking
     * {@link #getMidiDevice(MidiDevice.Info) getMidiDevice}.
     *
     * @return an array of <code>MidiDevice.Info</code> objects, one
     * for each installed MIDI device.  If no such devices are installed,
     * an array of length 0 is returned.
     */
    public static MidiDevice.Info[] getMidiDeviceInfo() {
	return getGenericDeviceInfo( MidiDevice.class );
    }


    /**
     * Obtains the requested MIDI device.
     *
     * @param info a device information object representing the desired device.
     * @return the requested device
     * @throws MidiUnavailableException if the requested device is not available
     * due to resource restrictions
     * @throws IllegalArgumentException if the info object does not represent
     * a MIDI device installed on the system
     * @see #getMidiDeviceInfo
     */
    public static MidiDevice getMidiDevice(MidiDevice.Info info) throws MidiUnavailableException {
	return (MidiDevice)getGenericDevice( null, info );
    }


    /*
     * re-deleted
     */
    /*
     * un-deleted, AbstractPlayer.addPlatformSynth() conflict
     */
    /**
     * Obtains an array of information objects representing
     * the set of MIDI devices available on the system that support
     * one or more receivers.
     * A returned information object can then be used to obtain a
     * receiver from the corresponding device object, by invoking
     * {@link #getReceiver(MidiDevice.Info) getReceiver}.
     *
     * @return an array of <code>MidiDevice.Info</code> objects, one
     * for each installed device that supports one or more
     * {@link Receiver Receivers}.
     * If no such devices are installed, an array of length
     * 0 is returned.
     */
    /*
      public static MidiDevice.Info[] getReceiverInfo() {
      return getGenericDeviceInfo( Receiver.class );
      }
    */


    /*
     * re-changed
     */
    /*
     * un-changed, AbstractPlayer.addPlatformSynth() conflict
     */
    /**
     * Obtains a MIDI receiver from an external MIDI port
     * or other default source.
     * @return the default MIDI receiver
     * @throws MidiUnavailableException if the default receiver is not
     * available due to resource restrictions
     */
    public static Receiver getReceiver() throws MidiUnavailableException {
	return ( ((MidiDevice)getGenericDevice(Receiver.class, null)).getReceiver() );
    }
    /*
      public static Receiver getReceiver(MidiDevice.Info info) throws MidiUnavailableException {
      return ( ((MidiDevice)getGenericDevice(Receiver.class, info)).getReceiver() );
      }
    */

    /*
     * deleted
     */
    /**
     * Obtains an array of information objects representing
     * the set of MIDI devices available on the system that support
     * one or more transmitters.
     * A returned information object can then be used to obtain a
     * transmitter from the corresponding device object, by invoking
     * {@link #getTransmitter(MidiDevice.Info) getTransmitter}.
     *
     * @return an array of <code>MidiDevice.Info</code> objects, one
     * for each installed device that supports one or more
     * {@link Transmitter Transmitters}.
     * If no such devices are installed, an array of length
     * 0 is returned.
     */
    /*	public static MidiDevice.Info[] getTransmitterInfo() {
	return getGenericDeviceInfo( Transmitter.class );
	}
    */


    /**
     * Obtains a MIDI transmitter from an external MIDI port
     * or other default source.
     * @return the default MIDI transmitter
     * @throws MidiUnavailableException if the default transmitter is not
     * available due to resource restrictions
     */
    public static Transmitter getTransmitter() throws MidiUnavailableException {
	return ( ((MidiDevice)getGenericDevice(Transmitter.class, null)).getTransmitter() );
    }
    /*	public static Transmitter getTransmitter(MidiDevice.Info info) throws MidiUnavailableException {
	return ( ((MidiDevice)getGenericDevice(Transmitter.class, info)).getTransmitter() );
	}
    */

    /*
     * deleted
     */
    /**
     * Obtains an array of information objects representing
     * the set of synthesizers available on the system.  A
     * returned information object can then be used to obtain the corresponding
     * synthesizer, by invoking
     * {@link #getSynthesizer(MidiDevice.Info) getSynthesizer}.
     *
     * @return an array of <code>MidiDevice.Info</code> objects, one
     * for each installed device that is a {@link Synthesizer}.
     * If no synthesizers are installed, an array of length
     * 0 is returned.
     */
    /*	public static MidiDevice.Info[] getSynthesizerInfo() {
	return getGenericDeviceInfo( Synthesizer.class );
	}
    */


    /**
     * Obtains the default synthesizer.
     * @return the default synthesizer
     * @throws MidiUnavailableException if the synthesizer is not
     * available due to resource restrictions
     */
    public static Synthesizer getSynthesizer() throws MidiUnavailableException {
	return (Synthesizer) getGenericDevice( Synthesizer.class, null );
    }
    /*	public static Synthesizer getSynthesizer(MidiDevice.Info info) {
	return (Synthesizer) getGenericDevice( Synthesizer.class, info );
	}
    */

    /*
     * deleted
     */
    /**
     * Obtains an array of information objects representing
     * the set of sequencers available on the system.  A
     * returned information object can then be used to obtain the corresponding
     * sequencer, by invoking
     * {@link #getSequencer(MidiDevice.Info) getSequencer}.
     *
     * @return an array of <code>MidiDevice.Info</code> objects, one
     * for each installed device that is a {@link Sequencer}.
     * If no sequencers are installed, an array of length
     * 0 is returned.
     */
    /*	public static MidiDevice.Info[] getSequencerInfo() {
	return getGenericDeviceInfo( Sequencer.class );
	}
    */


    /**
     * Obtains the default sequencer.
     * @return the default sequencer
     * @throws MidiUnavailableException if the sequencer is not
     * available due to resource restrictions
     */
    public static Sequencer getSequencer() throws MidiUnavailableException {
	return (Sequencer) getGenericDevice( Sequencer.class, null );
    }
    /*	public static Sequencer getSequencer(MidiDevice.Info info) {
	return (Sequencer) getGenericDevice( Sequencer.class, info );
	}
    */


    /**
     * Constructs a MIDI sound bank by reading it from the specified stream.
     * The stream must point to
     * a valid MIDI soundbank file.  In general, MIDI soundbank providers may
     * need to read some data from the stream before determining whether they
     * support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to
     * its original position.  If the input stream does not support this,
     * this method may fail with an IOException.
     * @param stream the source of the sound bank data.
     * @return the sound bank
     * @throws InvalidMidiDataException if the stream does not point to
     * valid MIDI soundbank data recognized by the system
     * @throws IOException if an I/O error occurred when loading the soundbank
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static Soundbank getSoundbank(InputStream stream)
	throws InvalidMidiDataException, IOException {

	SoundbankReader sp = null;
	Soundbank s = null;

	Vector providers = getSoundbankReaders();

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.size()-1; i >= 0; i-- ) {
	    sp = (SoundbankReader)providers.elementAt(i);
	    s = sp.getSoundbank(stream);

	    if( s!= null) {
		return s;
	    }
	}
	throw new InvalidMidiDataException("cannot get soundbank from stream");

    }


    /**
     * Constructs a <code>Soundbank</code> by reading it from the specified URL.
     * The URL must point to a valid MIDI soundbank file.
     *
     * @param url the source of the sound bank data
     * @return the sound bank
     * @throws InvalidMidiDataException if the URL does not point to valid MIDI
     * soundbank data recognized by the system
     * @throws IOException if an I/O error occurred when loading the soundbank
     */
    public static Soundbank getSoundbank(URL url)
	throws InvalidMidiDataException, IOException {

	SoundbankReader sp = null;
	Soundbank s = null;

	Vector providers = getSoundbankReaders();

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.size()-1; i >= 0; i-- ) {
	    sp = (SoundbankReader)providers.elementAt(i);
	    s = sp.getSoundbank(url);

	    if( s!= null) {
		return s;
	    }
	}
	throw new InvalidMidiDataException("cannot get soundbank from stream");

    }


    /**
     * Constructs a <code>Soundbank</code> by reading it from the specified
     * <code>File</code>.
     * The <code>File</code> must point to a valid MIDI soundbank file.
     *
     * @param file the source of the sound bank data
     * @return the sound bank
     * @throws InvalidMidiDataException if the <code>File</code> does not
     * point to valid MIDI soundbank data recognized by the system
     * @throws IOException if an I/O error occurred when loading the soundbank
     */
    public static Soundbank getSoundbank(File file)
	throws InvalidMidiDataException, IOException {


	FileInputStream fis = new FileInputStream( file );
	return getSoundbank( fis );
    }



    /**
     * Obtains the MIDI file format of the data in the specified input stream.
     * The stream must point to valid MIDI file data for a file type recognized
     * by the system.
     * <p>
     * This method and/or the code it invokes may need to read some data from
     * the stream to determine whether its data format is supported.  The
     * implementation may therefore
     * need to mark the stream, read enough data to determine whether it is in
     * a supported format, and reset the stream's read pointer to its original
     * position.  If the input stream does not permit this set of operations,
     * this method may fail with an <code>IOException</code>.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while determining the file format.
     *
     * @param stream the input stream from which file format information
     * should be extracted
     * @return an <code>MidiFileFormat</code> object describing the MIDI file
     * format
     * @throws InvalidMidiDataException if the stream does not point to valid
     * MIDI file data recognized by the system
     * @throws IOException if an I/O exception occurs while accessing the
     * stream
     * @see #getMidiFileFormat(URL)
     * @see #getMidiFileFormat(File)
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static MidiFileFormat getMidiFileFormat(InputStream stream)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	MidiFileFormat format = null;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getMidiFileFormat( stream ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( format==null ) {
	    throw new InvalidMidiDataException("input stream is not a supported file type");
	} else {
	    return format;
	}
    }


    /**
     * Obtains the MIDI file format of the data in the specified URL.  The URL
     * must point to valid MIDI file data for a file type recognized
     * by the system.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while determining the file format.
     *
     * @param url the URL from which file format information should be
     * extracted
     * @return a <code>MidiFileFormat</code> object describing the MIDI file
     * format
     * @throws InvalidMidiDataException if the URL does not point to valid MIDI
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs while accessing the URL
     *
     * @see #getMidiFileFormat(InputStream)
     * @see #getMidiFileFormat(File)
     */
    public static MidiFileFormat getMidiFileFormat(URL url)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	MidiFileFormat format = null;


	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getMidiFileFormat( url ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( format==null ) {
	    throw new InvalidMidiDataException("url is not a supported file type");
	} else {
	    return format;
	}
    }


    /**
     * Obtains the MIDI file format of the specified <code>File</code>.  The
     * <code>File</code> must point to valid MIDI file data for a file type
     * recognized by the system.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while determining the file format.
     *
     * @param file the <code>File</code> from which file format information
     * should be extracted
     * @return a <code>MidiFileFormat</code> object describing the MIDI file
     * format
     * @throws InvalidMidiDataException if the <code>File</code> does not point
     *  to valid MIDI file data recognized by the system
     * @throws IOException if an I/O exception occurs while accessing the file
     *
     * @see #getMidiFileFormat(InputStream)
     * @see #getMidiFileFormat(URL)
     */
    public static MidiFileFormat getMidiFileFormat(File file)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	MidiFileFormat format = null;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getMidiFileFormat( file ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( format==null ) {
	    throw new InvalidMidiDataException("file is not a supported file type");
	} else {
	    return format;
	}
    }


    /**
     * Obtains a MIDI sequence from the specified input stream.  The stream must
     * point to valid MIDI file data for a file type recognized
     * by the system.
     * <p>
     * This method and/or the code it invokes may need to read some data
     * from the stream to determine whether
     * its data format is supported.  The implementation may therefore
     * need to mark the stream, read enough data to determine whether it is in
     * a supported format, and reset the stream's read pointer to its original
     * position.  If the input stream does not permit this set of operations,
     * this method may fail with an <code>IOException</code>.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while constructing the <code>Sequence</code>
     * object from the file data.
     *
     * @param stream the input stream from which the <code>Sequence</code>
     * should be constructed
     * @return a <code>Sequence</code> object based on the MIDI file data
     * contained in the input stream
     * @throws InvalidMidiDataException if the stream does not point to
     * valid MIDI file data recognized by the system
     * @throws IOException if an I/O exception occurs while accessing the
     * stream
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static Sequence getSequence(InputStream stream)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	Sequence sequence = null;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		sequence = providers[i].getSequence( stream ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( sequence==null ) {
	    throw new InvalidMidiDataException("could not get sequence from input stream");
	} else {
	    return sequence;
	}
    }


    /**
     * Obtains a MIDI sequence from the specified URL.  The URL must
     * point to valid MIDI file data for a file type recognized
     * by the system.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while constructing the <code>Sequence</code>
     * object from the file data.
     *
     * @param url the URL from which the <code>Sequence</code> should be
     * constructed
     * @return a <code>Sequence</code> object based on the MIDI file data
     * pointed to by the URL
     * @throws InvalidMidiDataException if the URL does not point to valid MIDI
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs while accessing the URL
     */
    public static Sequence getSequence(URL url)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	Sequence sequence = null;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		sequence = providers[i].getSequence( url ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( sequence==null ) {
	    throw new InvalidMidiDataException("could not get sequence from URL");
	} else {
	    return sequence;
	}
    }


    /**
     * Obtains a MIDI sequence from the specified <code>File</code>.
     * The <code>File</code> must point to valid MIDI file data
     * for a file type recognized by the system.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while constructing the <code>Sequence</code>
     * object from the file data.
     *
     * @param file the <code>File</code> from which the <code>Sequence</code>
     * should be constructed
     * @return a <code>Sequence</code> object based on the MIDI file data
     * pointed to by the File
     * @throws InvalidMidiDataException if the File does not point to valid MIDI
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static Sequence getSequence(File file)
	throws InvalidMidiDataException, IOException {

	MidiFileReader providers[] = getMidiFileReaders();
	Sequence sequence = null;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		sequence = providers[i].getSequence( file ); // throws IOException
		break;
	    } catch (InvalidMidiDataException e) {
		continue;
	    }
	}

	if( sequence==null ) {
	    throw new InvalidMidiDataException("could not get sequence from file");
	} else {
	    return sequence;
	}
    }


    /**
     * Obtains the set of MIDI file types for which file writing support is
     * provided by the system.
     * @return array of file types.  If no file types are supported,
     * an array of length 0 is returned.
     */
    public static int[] getMidiFileTypes() {

	MidiFileWriter providers[] = getMidiFileWriters();
	Vector allTypes = new Vector();

	int size = 0;
	int index = 0;
	int types[] = null;

	// gather from all the providers

	for(int i=0; i<providers.length; i++ ) {
	    types = providers[i].getMidiFileTypes();
	    size += types.length;
	    allTypes.addElement( types );
	}

	// now build a new array

	int types2[] = new int[size];
	for(int i=0; i<allTypes.size(); i++ ) {
	    types = (int [])(allTypes.elementAt(i));
	    for(int j=0; j<types.length; j++ ) {
		types2[index++] = types[j];
	    }
	}
	return types2;
    }


    /**
     * Indicates whether file writing support for the specified MIDI file type
     * is provided by the system.
     * @param fileType the file type for which write capabilities are queried
     * @return <code>true</code> if the file type is supported,
     * otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(int fileType) {

	MidiFileWriter providers[] = getMidiFileWriters();

	for(int i=0; i<providers.length; i++) {
	    if( providers[i].isFileTypeSupported(fileType) == true ) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Obtains the set of MIDI file types that the system can write from the
     * sequence specified.
     * @param sequence the sequence for which MIDI file type support
     * is queried
     * @return the set of supported file types.  If no file types are supported,
     * returns an array of length 0.
     */
    public static int[] getMidiFileTypes(Sequence sequence) {

	MidiFileWriter providers[] = getMidiFileWriters();
	int types[][] = new int[ providers.length ][];
	int returnTypes[] = null;
	int numTypes = 0;
	int index = 0;

	// Get all supported file types
	for(int i=0; i < providers.length; i++) {
	    types[i] = providers[i].getMidiFileTypes(sequence);
	    numTypes += types[i].length;
	}
	// Now put them in a 1-D array
	returnTypes = new int[ numTypes ];
	for(int i=0; i < providers.length; i++) {
	    for(int j=0; j < types[i].length; j++) {
		returnTypes[ index ] = types[i][j];
		index++;
	    }
	}

	return returnTypes;
    }


    /**
     * Indicates whether a MIDI file of the file type specified can be written
     * from the sequence indicated.
     * @param fileType the file type for which write capabilities
     * are queried
     * @param sequence the sequence for which file writing support is queried
     * @return <code>true</code> if the file type is supported for this
     * sequence, otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(int fileType, Sequence sequence) {

	MidiFileWriter providers[] = getMidiFileWriters();

	for(int i=0; i<providers.length; i++) {
	    if( providers[i].isFileTypeSupported(fileType,sequence) == true ) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Writes a stream of bytes representing a file of the MIDI file type
     * indicated to the output stream provided.
     * @param in sequence containing MIDI data to be written to the file
     * @param fileType the file type of the file to be written to the output stream
     * @param out stream to which the file data should be written
     * @return the number of bytes written to the output stream
     * @throws IOException if an I/O exception occurs
     * @throws IllegalArgumentException if the file format is not supported by
     * the system
     * @see #isFileTypeSupported(int, Sequence)
     * @see	#getMidiFileTypes(Sequence)
     */
    public static int write(Sequence in, int fileType, OutputStream out) throws IOException {

	MidiFileWriter providers[] = getMidiFileWriters();
	//$$fb 2002-04-17: Fix for 4635287: Standard MidiFileWriter cannot write empty Sequences
	int bytesWritten = -2;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    if( providers[i].isFileTypeSupported( fileType, in ) == true ) {

		bytesWritten = providers[i].write(in, fileType, out);
		break;
	    }
	}
	if (bytesWritten == -2) {
	    throw new IllegalArgumentException("MIDI file type is not supported");
	}
	return bytesWritten;
    }


    /**
     * Writes a stream of bytes representing a file of the MIDI file type
     * indicated to the external file provided.
     * @param in sequence containing MIDI data to be written to the file
     * @param type the file type of the file to be written to the output stream
     * @param out external file to which the file data should be written
     * @return the number of bytes written to the file
     * @throws IOException if an I/O exception occurs
     * @throws IllegalArgumentException if the file type is not supported by
     * the system
     * @see #isFileTypeSupported(int, Sequence)
     * @see	#getMidiFileTypes(Sequence)
     */
    public static int write(Sequence in, int type, File out) throws IOException {

	MidiFileWriter providers[] = getMidiFileWriters();
	//$$fb 2002-04-17: Fix for 4635287: Standard MidiFileWriter cannot write empty Sequences
	int bytesWritten = -2;

	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    if( providers[i].isFileTypeSupported( type, in ) == true ) {

		bytesWritten = providers[i].write(in, type, out);
		break;
	    }
	}
	if (bytesWritten == -2) {
	    throw new IllegalArgumentException("MIDI file type is not supported");
	}
	return bytesWritten;
    }


    // HELPER METHODS

    private static synchronized boolean isA( Class testClass, Class targetClass ) {

	return (targetClass.isAssignableFrom(testClass)) ? true : false;
    }


    private static Vector getMidiDeviceProviders() {

	Vector providers = null;

	try {

	    Class.forName( "sun.misc.Service" );

	    providers = getJDK13Services("javax.sound.midi.spi.MidiDeviceProvider");

	} catch (Exception e) {

	    // we're not running with 1.3's SPI mechanism

	    providers = getDefaultServices("javax.sound.midi.spi.MidiDeviceProvider");
	}

	return providers;
    }

    private static Vector getSoundbankReaders() {

	Vector providers = null;

	try {
	    Class.forName( "sun.misc.Service" );

	    providers = getJDK13Services("javax.sound.midi.spi.SoundbankReader");

	} catch (Exception e) {

	    // we're not running with 1.3's SPI mechanism

	    providers = getDefaultServices("javax.sound.midi.spi.SoundbankReader");
	}

	return providers;

    }

    private static MidiFileWriter[] getMidiFileWriters() {

	Vector v = new Vector();
	MidiFileWriter varray[];

	try {

	    Class.forName( "sun.misc.Service" );

	    v = getJDK13Services("javax.sound.midi.spi.MidiFileWriter");

	} catch (Exception e) {

	    // we're not running with 1.3's SPI mechanism

	    v = getDefaultServices("javax.sound.midi.spi.MidiFileWriter");
	}

	varray = new MidiFileWriter[ v.size() ];
	for( int i=0; i < varray.length; i++ ) {
	    varray[i] = (MidiFileWriter)(v.elementAt(i));
	}
	return varray;
    }


    private static MidiFileReader[] getMidiFileReaders() {

	Vector v = new Vector();
	MidiFileReader varray[];

	try {

	    Class.forName( "sun.misc.Service" );

	    v = getJDK13Services("javax.sound.midi.spi.MidiFileReader");

	} catch (Exception e) {

	    // we're not running with 1.3's SPI mechanism

	    v = getDefaultServices("javax.sound.midi.spi.MidiFileReader");
	}

	varray = new MidiFileReader[ v.size() ];
	for( int i=0; i < varray.length; i++ ) {
	    varray[i] = (MidiFileReader)(v.elementAt(i));
	}
	return varray;
    }


    private static MidiDevice.Info[] getGenericDeviceInfo( Class deviceClass ) {

	int i;
	int j;
	Vector v = new Vector();
	MidiDevice.Info[] tmpinfo;
	MidiDevice.Info[] varray;
	Vector v1;

	v1 = getMidiDeviceProviders();

	for(i = 0; i < v1.size(); i++) {

	    tmpinfo = ((MidiDeviceProvider) v1.elementAt(i)).getDeviceInfo();

	    for (j = 0; j < tmpinfo.length; j++) {

				// $$kk: 10.12.99: ugh! i have wrecked this!!
				//if( isA( tmpinfo[j].getDeviceClass(), deviceClass ) ) {

		MidiDevice device = ((MidiDeviceProvider) v1.elementAt(i)).getDevice(tmpinfo[j]);

		if (isA(deviceClass, Receiver.class)) {
		    if(device.getMaxReceivers() != 0) {
			v.addElement( tmpinfo[j] );
		    }
		} else if (isA(deviceClass, Transmitter.class)) {
		    if(device.getMaxTransmitters() != 0) {
			v.addElement( tmpinfo[j] );
		    }
		} else if (deviceClass.isInstance(device)) {
		    v.addElement( tmpinfo[j] );
		}
	    }
	}

	varray = new MidiDevice.Info[ v.size() ];

	for(i = 0; i < varray.length; i++ ) {
	    varray[i] = (MidiDevice.Info)(v.elementAt(i));
	}

	return varray;
    }


    /**
     * Attempts to locate and return the specified device
     * @throws  IllegalArgumentException on failure.
     */
    private static Object getGenericDevice( Class deviceClass, MidiDevice.Info info) {

	int i;
	int j;
	MidiDevice s = null;
	MidiDevice.Info[] tmpinfo = null;
	Vector v1 = new Vector();

	v1 = getMidiDeviceProviders();

	for(i = 0; i < v1.size(); i++) {

	    tmpinfo = ((MidiDeviceProvider) v1.elementAt(i)).getDeviceInfo();

	    if( info == null ) {

				// $$jb: 06.04.99: Should we do more to guarantee which
				//                 device is the default?

		for (j = 0; j < tmpinfo.length; j++) {

		    // $$kk: 10.12.99: ugh! i have wrecked this!!
		    //if( isA(tmpinfo[j].getDeviceClass(), deviceClass ) ) {
		    //	return ((MidiDeviceProvider) v1.elementAt(i)).getDevice(tmpinfo[j]);
		    //}
		    MidiDevice device = ((MidiDeviceProvider) v1.elementAt(i)).getDevice(tmpinfo[j]);

		    if (isA(deviceClass, Receiver.class)) {
			if(device.getMaxReceivers() != 0) {
			    return device;
			}
		    } else if (isA(deviceClass, Transmitter.class)) {
			if(device.getMaxTransmitters() != 0) {
			    return device;
			}
		    } else if (deviceClass.isInstance(device)) {
			return device;
		    }
		}

	    } else {

		for (j = 0; j < tmpinfo.length; j++) {

		    if( tmpinfo[j].equals( info ) ) {

			// $$kk: 10.12.99: ugh! i have wrecked this!!

			// $$kk: 10.12.99: shouldn't need this check?
			//if( isA(tmpinfo[j].getDeviceClass(), deviceClass ) ) {
			//	return ((MidiDeviceProvider) v1.elementAt(i)).getDevice(tmpinfo[j]);
			//}

			MidiDevice device = ((MidiDeviceProvider) v1.elementAt(i)).getDevice(tmpinfo[j]);

			if (deviceClass == null) {
			    return device;
			}

			if (isA(deviceClass, Receiver.class)) {
			    if(device.getMaxReceivers() != 0) {
				return device;
			    }
			} else if (isA(deviceClass, Transmitter.class)) {
			    if(device.getMaxTransmitters() != 0) {
				return device;
			    }
			}
		    }
		}
	    }
	}

	throw new IllegalArgumentException("Requested device not installed: " + info);
    }

    /**
     * Obtains the set of services currently installed on the system
     * using sun.misc.Service, the SPI mechanism in 1.3.
     * @return a Vector of instances of providers for the requested service.
     * If no providers are available, a vector of length 0 will be returned.
     */

    private static Vector getJDK13Services( String serviceName ) {

	Vector v = null;

	try {
	    Class jdk13Services =
		Class.forName( jdk13ServicesClassName );

	    Method m = jdk13Services.getMethod(
					       servicesMethodName,
					       servicesParamTypes);

	    Object[] arguments = new Object[] { serviceName };

	    v = (Vector) m.invoke(jdk13Services,arguments);

	} catch(InvocationTargetException e1) {
	    v = new Vector();
	} catch(ClassNotFoundException e2) {
	    v = new Vector();
	} catch(IllegalAccessException e3) {
	    v = new Vector();
	} catch(NoSuchMethodException e4) {
	    v = new Vector();
	}
	return v;
    }
    /**
     * Obtains the default set of services currently installed on the system.
     * This method is only invoked if sun.misc.Service is not available.
     * @return a Vector of instances of providers for the requested service.
     * If no providers are available, a vector of length 0 will be returned.
     */

    private static Vector getDefaultServices( String serviceName ) {

	Vector v = null;

	try {
	    Class defaultServices =
		Class.forName( defaultServicesClassName );

	    Method m = defaultServices.getMethod(
						 servicesMethodName,
						 servicesParamTypes);

	    Object[] arguments = new Object[] { serviceName };

	    v = (Vector) m.invoke(defaultServices,arguments);

	} catch(InvocationTargetException e1) {
	    v = new Vector();
	} catch(ClassNotFoundException e2) {
	    v = new Vector();
	} catch(IllegalAccessException e3) {
	    v = new Vector();
	} catch(NoSuchMethodException e4) {
	    v = new Vector();
	}
	return v;
    }
}
