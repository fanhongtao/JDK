/*
 * @(#)AudioSystem.java	1.66 03/03/21
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.sampled;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

/**
 * The <code>AudioSystem</code> class acts as the entry point to the
 * sampled-audio system resources. This class lets you query and
 * access the mixers that are installed on the system.
 * <code>AudioSystem</code> includes a number of
 * methods for converting audio data between different formats, and for
 * translating between audio files and streams. It also provides a method
 * for obtaining a <code>{@link Line}</code> directly from the
 * <code>AudioSystem</code> without dealing explicitly
 * with mixers.
 *
 * @author Kara Kytle
 * @version 1.66, 03/03/21
 *
 * @see AudioFormat
 * @see AudioInputStream
 * @see Mixer
 * @see Line
 * @see Line.Info
 * @since 1.3
 */
public class AudioSystem {
    
    /**
     * An integer that stands for an unknown numeric value.
     * This value is appropriate only for signed quantities that do not
     * normally take negative values.  Examples include file sizes, frame
     * sizes, buffer sizes, and sample rates.
     * A number of Java Sound constructors accept
     * a value of <code>NOT_SPECIFIED</code> for such parameters.  Other
     * methods may also accept or return this value, as documented.
     */
    public static final int NOT_SPECIFIED = -1;
    
    
    /**
     * Private strings for services classes and methods
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
     * debugging
     */
    private final static boolean DEBUG = false;

    /**
     * Private no-args constructor for ensuring against instantiation.
     */
    private AudioSystem() {
    }
    
    
    /**
     * Obtains an array of mixer info objects that represents
     * the set of audio mixers that are currently installed on the system.
     * @return an array of info objects for the currently installed mixers.  If no mixers
     * are available on the system, an array of length 0 is returned.
     * @see #getMixer
     */
    public static Mixer.Info[] getMixerInfo() {
	
	int i;
	int j;
	
	Vector providers = getMixerProviders();
	Vector infos = new Vector();
	
	Mixer.Info[] someInfos;	// per-mixer
	Mixer.Info[] allInfos;	// for all mixers
	
	//$$fb 2002-11-07: addendum fix for 
	//    bug 4487550: Service providers cannot be used to replace existing providers
	for(i = providers.size() - 1; i >= 0; i-- ) {
	    
	    someInfos = (Mixer.Info[])
		((MixerProvider)providers.elementAt(i)).getMixerInfo();
	    
	    for (j = 0; j < someInfos.length; j++) {
		infos.addElement(someInfos[j]);
	    }
	}
	
	allInfos = new Mixer.Info[infos.size()];
	
	for (i = 0; i < allInfos.length; i++) {
	    
	    allInfos[i] = (Mixer.Info)infos.elementAt(i);
	}
	
	return allInfos;
    }
    
    /**
     * Obtains the requested audio mixer.
     * @param info a <code>Mixer.Info</code> object representing the desired
     * mixer, or <code>null</code> for the system default mixer
     * @return the requested mixer
     * @throws SecurityException if the requested mixer
     * is unavailable because of security restrictions
     * @throws IllegalArgumentException if the info object does not represent
     * a mixer installed on the system
     * @see #getMixerInfo
     */
    public static Mixer getMixer(Mixer.Info info) {
	
	Mixer mixer = null;
	Vector providers = getMixerProviders();
	
	//$$fb 2002-11-07: addendum fix for 
	//    bug 4487550: Service providers cannot be used to replace existing providers
	for(int i = providers.size() -1; i >= 0; i-- ) {
	    
	    try {
		return ((MixerProvider)providers.elementAt(i)).getMixer(info);
		
	    } catch (IllegalArgumentException e) {
	    } catch (NullPointerException e) {
		// $$jb 08.20.99:  If the strings in the info object aren't
		// set, then Netscape (using jdk1.1.5) tends to throw
		// NPE's when doing some string manipulation.  This is
		// probably not the best fix, but is solves the problem
		// of the NPE in Netscape using local classes
		// $$jb 11.01.99: Replacing this patch.
	    }
	}

	//$$fb if looking for default mixer, and not found yet, add a round of looking
	if (info == null) {
	    for(int i = providers.size() -1; i >= 0; i-- ) {
		try {
		    MixerProvider provider = (MixerProvider) providers.elementAt(i);
		    Mixer.Info[] infos = provider.getMixerInfo();
		    // start from 0 to last device (do not reverse this order)
		    for (int ii = 0; ii < infos.length; ii++) {
			try {
			    return provider.getMixer(infos[ii]);
			} catch (IllegalArgumentException e) {
			    // this is not a good default device :)
			}
		    }
		} catch (IllegalArgumentException e) {
		} catch (NullPointerException e) {
		}
	    }
	}

	
	throw new IllegalArgumentException("Mixer not supported: "
					   + (info!=null?info.toString():"null"));
    }
    
    
    //$$fb 2002-11-26: fix for 4757930: DOC: AudioSystem.getTarget/SourceLineInfo() is ambiguous
    /**
     * Obtains information about all source lines of a particular type that are supported
     * by the installed mixers.
     * @param info a <code>Line.Info</code> object that specifies the kind of
     * lines about which information is requested
     * @return an array of <code>Line.Info</code> objects describing source lines matching
     * the type requested.  If no matching source lines are supported, an array of length 0
     * is returned.
     *
     * @see Mixer#getSourceLineInfo(Line.Info)
     */
    public static Line.Info[] getSourceLineInfo(Line.Info info) {
	
	Vector vector = new Vector();
	Line.Info[] currentInfoArray;
	
	Mixer mixer;
	Line.Info fullInfo = null;
	Mixer.Info[] infoArray = getMixerInfo();
	
	for (int i = 0; i < infoArray.length; i++) {
	    
	    mixer = getMixer(infoArray[i]);
	    
	    currentInfoArray = mixer.getSourceLineInfo(info);
	    for (int j = 0; j < currentInfoArray.length; j++) {
		vector.addElement(currentInfoArray[j]);
	    }
	}
	
	Line.Info[] returnedArray = new Line.Info[vector.size()];
	
	for (int i = 0; i < returnedArray.length; i++) {
	    returnedArray[i] = (Line.Info)vector.elementAt(i);
	}
	
	return returnedArray;
    }
    
    
    /**
     * Obtains information about all target lines of a particular type that are supported
     * by the installed mixers.
     * @param info a <code>Line.Info</code> object that specifies the kind of
     * lines about which information is requested
     * @return an array of <code>Line.Info</code> objects describing target lines matching
     * the type requested.  If no matching target lines are supported, an array of length 0
     * is returned.
     *
     * @see Mixer#getTargetLineInfo(Line.Info)
     */
    public static Line.Info[] getTargetLineInfo(Line.Info info) {
	
	Vector vector = new Vector();
	Line.Info[] currentInfoArray;
	
	Mixer mixer;
	Line.Info fullInfo = null;
	Mixer.Info[] infoArray = getMixerInfo();
	
	for (int i = 0; i < infoArray.length; i++) {
	    
	    mixer = getMixer(infoArray[i]);
	    
	    currentInfoArray = mixer.getTargetLineInfo(info);
	    for (int j = 0; j < currentInfoArray.length; j++) {
		vector.addElement(currentInfoArray[j]);
	    }
	}
	
	Line.Info[] returnedArray = new Line.Info[vector.size()];
	
	for (int i = 0; i < returnedArray.length; i++) {
	    returnedArray[i] = (Line.Info)vector.elementAt(i);
	}
	
	return returnedArray;
    }
    
    
    /**
     * Indicates whether the system supports any lines that match
     * the specified <code>Line.Info</code> object.  A line is supported if
     * any installed mixer supports it.
     * @param info a <code>Line.Info</code> object describing the line for which support is queried
     * @return <code>true</code> if at least one matching line is
     * supported, otherwise <code>false</code>
     *
     * @see Mixer#isLineSupported(Line.Info)
     */
    public static boolean isLineSupported(Line.Info info) {
	
	Mixer mixer;
	Mixer.Info[] infoArray = getMixerInfo();
	
	for (int i = 0; i < infoArray.length; i++) {
	    
	    if( infoArray[i] != null ) {
		mixer = getMixer(infoArray[i]);
		if (mixer.isLineSupported(info)) {
		    return true;
		}
	    }
	}
	
	return false;
    }
    
    /**
     * Obtains a line that matches the description in the specified
     * <code>Line.Info</code> object.
     *
     * @param info a <code>Line.Info</code> object describing the desired kind of line
     * @return a line of the requested kind
     *
     * @throws LineUnavailableException if a matching line
     * is not available due to resource restrictions
     * @throws SecurityException if a matching line
     * is not available due to security restrictions
     * @throws IllegalArgumentException if the system does not
     * support at least one line matching the specified <code>Line.Info</code> object
     * through any installed mixer
     */
    public static Line getLine(Line.Info info)
	throws LineUnavailableException {
	
	Mixer mixer;
	Mixer.Info[] infoArray = getMixerInfo();
	
	LineUnavailableException lue = null;
	
	for (int i = 0; i < infoArray.length; i++) {
	    
	    mixer = getMixer(infoArray[i]);
	    
	    if (mixer.isLineSupported(info)) {
		
		try {
		    return mixer.getLine(info);
		} catch (LineUnavailableException e) {
		    lue = e;
		}
	    }
	}
	
	// if this line was supported but was not available, throw the last
	// LineUnavailableException we got (??).
	if (lue != null) {
	    throw lue;
	}
	
	// otherwise, the requested line was not supported, so throw
	// an Illegal argument exception
	throw new IllegalArgumentException("No line matching " +
					   info.toString() + " is supported.");
    }
    
    
    // $$fb 2002-04-12: fix for 4662082: behavior of AudioSystem.getTargetEncodings() methods doesn't match the spec
    /**
     * Obtains the encodings that the system can obtain from an
     * audio input stream with the specified encoding using the set
     * of installed format converters.
     * @param sourceEncoding the encoding for which conversion support
     * is queried
     * @return array of encodings.  If <code>sourceEncoding</code>is not supported,
     * an array of length 0 is returned. Otherwise, the array will have a length
     * of at least 1, representing <code>sourceEncoding</code> (no conversion).
     */
    public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat.Encoding sourceEncoding) {
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	Vector encodings = new Vector();
	
	int size = 0;
	int index = 0;
	AudioFormat.Encoding encs[] = null;
	
	// gather from all the codecs
	for(int i=0; i<codecs.length; i++ ) {
	    if( codecs[i].isSourceEncodingSupported( sourceEncoding ) ) {
		encs = codecs[i].getTargetEncodings();
		size += encs.length;
		encodings.addElement( encs );
	    }
	}
	
	// now build a new array
	
	AudioFormat.Encoding encs2[] = new AudioFormat.Encoding[size];
	for(int i=0; i<encodings.size(); i++ ) {
	    encs = (AudioFormat.Encoding [])(encodings.elementAt(i));
	    for(int j=0; j<encs.length; j++ ) {
		encs2[index++] = encs[j];
	    }
	}
	return encs2;
    }
    
    
    
    // $$fb 2002-04-12: fix for 4662082: behavior of AudioSystem.getTargetEncodings() methods doesn't match the spec
    /**
     * Obtains the encodings that the system can obtain from an
     * audio input stream with the specified format using the set
     * of installed format converters.
     * @param sourceFormat the audio format for which conversion
     * is queried
     * @return array of encodings. If <code>sourceFormat</code>is not supported,
     * an array of length 0 is returned. Otherwise, the array will have a length
     * of at least 1, representing the encoding of <code>sourceFormat</code> (no conversion).
     */
    public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
	
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	Vector encodings = new Vector();
	
	int size = 0;
	int index = 0;
	AudioFormat.Encoding encs[] = null;
	
	// gather from all the codecs
	
	for(int i=0; i<codecs.length; i++ ) {
	    encs = codecs[i].getTargetEncodings(sourceFormat);
	    size += encs.length;
	    encodings.addElement( encs );
	}
	
	// now build a new array
	
	AudioFormat.Encoding encs2[] = new AudioFormat.Encoding[size];
	for(int i=0; i<encodings.size(); i++ ) {
	    encs = (AudioFormat.Encoding [])(encodings.elementAt(i));
	    for(int j=0; j<encs.length; j++ ) {
		encs2[index++] = encs[j];
	    }
	}
	return encs2;
    }
    
    
    /**
     * Indicates whether an audio input stream of the specified encoding
     * can be obtained from an audio input stream that has the specified
     * format.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceFormat the audio format before conversion
     * @return <code>true</code> if the conversion is supported,
     * otherwise <code>false</code>
     */
    public static boolean isConversionSupported(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
	
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	
	for(int i=0; i<codecs.length; i++ ) {
	    if(codecs[i].isConversionSupported(targetEncoding,sourceFormat) ) {
		return true;
	    }
	}
	return false;
    }
    
    
    /**
     * Obtains an audio input stream of the indicated encoding, by converting the
     * provided audio input stream.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated encoding
     * @throws IllegalArgumentException if the conversion is not supported
     * @see #getTargetEncodings(AudioFormat.Encoding)
     * @see #getTargetEncodings(AudioFormat)
     * @see #isConversionSupported(AudioFormat.Encoding, AudioFormat)
     * @see #getAudioInputStream(AudioFormat, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding,
						       AudioInputStream sourceStream) {
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = codecs.length-1; i >= 0; i-- ) {
	    if( codecs[i].isConversionSupported( targetEncoding, sourceStream.getFormat() ) ) {
		return codecs[i].getAudioInputStream( targetEncoding, sourceStream );
	    }
	}
	// we ran out of options, throw an exception
	throw new IllegalArgumentException("Unsupported conversion: " + targetEncoding + " from " + sourceStream.getFormat());
    }
    
    
    /**
     * Obtains the formats that have a particular encoding and that the system can
     * obtain from a stream of the specified format using the set of
     * installed format converters.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceFormat the audio format before conversion
     * @return array of formats.  If no formats of the specified
     * encoding are supported, an array of length 0 is returned.
     */
    public static AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	Vector formats = new Vector();
	
	int size = 0;
	int index = 0;
	AudioFormat fmts[] = null;
	
	// gather from all the codecs
	
	for(int i=0; i<codecs.length; i++ ) {
	    fmts = codecs[i].getTargetFormats(targetEncoding, sourceFormat);
	    size += fmts.length;
	    formats.addElement( fmts );
	}
	
	// now build a new array
	
	AudioFormat fmts2[] = new AudioFormat[size];
	for(int i=0; i<formats.size(); i++ ) {
	    fmts = (AudioFormat [])(formats.elementAt(i));
	    for(int j=0; j<fmts.length; j++ ) {
		fmts2[index++] = fmts[j];
	    }
	}
	return fmts2;
    }
    
    
    /**
     * Indicates whether an audio input stream of a specified format
     * can be obtained from an audio input stream of another specified format.
     * @param targetFormat the desired audio format after conversion
     * @param sourceFormat the audio format before conversion
     * @return <code>true</code> if the conversion is supported,
     * otherwise <code>false</code>
     */
    
    public static boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat) {
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	
	for(int i=0; i<codecs.length; i++ ) {
	    if(codecs[i].isConversionSupported(targetFormat, sourceFormat) ) {
		return true;
	    }
	}
	return false;
    }
    
    
    /**
     * Obtains an audio input stream of the indicated format, by converting the
     * provided audio input stream.
     * @param targetFormat the desired audio format after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated format
     * @throws IllegalArgumentException if the conversion is not supported
     * #see #getTargetEncodings(AudioFormat)
     * @see #getTargetFormats(AudioFormat.Encoding, AudioFormat)
     * @see #isConversionSupported(AudioFormat, AudioFormat)
     * @see #getAudioInputStream(AudioFormat.Encoding, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat targetFormat,
						       AudioInputStream sourceStream) {
	
	if (sourceStream.getFormat().matches(targetFormat)) {
	    return sourceStream;
	}
	
	FormatConversionProvider codecs[] = getFormatConversionProviders();
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = codecs.length-1; i >= 0; i-- ) {
	    
	    if(codecs[i].isConversionSupported(targetFormat,sourceStream.getFormat()) ) {
		return codecs[i].getAudioInputStream(targetFormat,sourceStream);
	    }
	}
	
	// we ran out of options...
	throw new IllegalArgumentException("Unsupported conversion: " + targetFormat + " from " + sourceStream.getFormat());
    }
    
    
    /**
     * Obtains the audio file format of the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may require
     * multiple parsers to examine the stream to determine whether they support it.
     * These parsers must be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operations, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which file format information should be
     * extracted
     * @return an <code>AudioFileFormat</code> object describing the stream's audio file format
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an input/output exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static AudioFileFormat getAudioFileFormat(InputStream stream)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioFileFormat format = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getAudioFileFormat( stream ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( format==null ) {
	    throw new UnsupportedAudioFileException("file is not a supported file type");
	} else {
	    return format;
	}
    }
    
    /**
     * Obtains the audio file format of the specified URL.  The URL must
     * point to valid audio file data.
     * @param url the URL from which file format information should be
     * extracted
     * @return an <code>AudioFileFormat</code> object describing the audio file format
     * @throws UnsupportedAudioFileException if the URL does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an input/output exception occurs
     */
    public static AudioFileFormat getAudioFileFormat(URL url)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioFileFormat format = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getAudioFileFormat( url ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( format==null ) {
	    throw new UnsupportedAudioFileException("file is not a supported file type");
	} else {
	    return format;
	}
    }
    
    /**
     * Obtains the audio file format of the specified <code>File</code>.  The <code>File</code> must
     * point to valid audio file data.
     * @param file the <code>File</code> from which file format information should be
     * extracted
     * @return an <code>AudioFileFormat</code> object describing the audio file format
     * @throws UnsupportedAudioFileException if the <code>File</code> does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static AudioFileFormat getAudioFileFormat(File file)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioFileFormat format = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		format = providers[i].getAudioFileFormat( file ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( format==null ) {
	    throw new UnsupportedAudioFileException("file is not a supported file type");
	} else {
	    return format;
	}
    }
    
    
    /**
     * Obtains an audio input stream from the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may
     * require multiple parsers to
     * examine the stream to determine whether they support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operation, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data contained
     * in the input stream.
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static AudioInputStream getAudioInputStream(InputStream stream)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioInputStream audioStream = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		audioStream = providers[i].getAudioInputStream( stream ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( audioStream==null ) {
	    throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
	} else {
	    return audioStream;
	}
    }
    
    /**
     * Obtains an audio input stream from the URL provided.  The URL must
     * point to valid audio file data.
     * @param url the URL for which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the URL
     * @throws UnsupportedAudioFileException if the URL does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static AudioInputStream getAudioInputStream(URL url)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioInputStream audioStream = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		audioStream = providers[i].getAudioInputStream( url ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( audioStream==null ) {
	    throw new UnsupportedAudioFileException("could not get audio input stream from input URL");
	} else {
	    return audioStream;
	}
    }
    
    /**
     * Obtains an audio input stream from the provided <code>File</code>.  The <code>File</code> must
     * point to valid audio file data.
     * @param file the <code>File</code> for which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the <code>File</code>
     * @throws UnsupportedAudioFileException if the <code>File</code> does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static AudioInputStream getAudioInputStream(File file)
	throws UnsupportedAudioFileException, IOException {
	
	AudioFileReader providers[] = getAudioFileReaders();
	AudioInputStream audioStream = null;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		audioStream = providers[i].getAudioInputStream( file ); // throws IOException
		break;
	    } catch (UnsupportedAudioFileException e) {
		continue;
	    }
	}
	
	if( audioStream==null ) {
	    throw new UnsupportedAudioFileException("could not get audio input stream from input file");
	} else {
	    return audioStream;
	}
    }
    
    
    /**
     * Obtains the file types for which file writing support is provided by the system.
     * @return array of file types.  If no file types are supported,
     * an array of length 0 is returned.
     */
    public static AudioFileFormat.Type[] getAudioFileTypes() {
	//$$fb TODO: this implementation may return duplicates!
	AudioFileWriter providers[] = getAudioFileWriters();
	AudioFileFormat.Type fileTypes[][] = new AudioFileFormat.Type[ providers.length ][];
	AudioFileFormat.Type returnTypes[] = null;
	int numTypes = 0;
	int index = 0;
	
	// Get all file types
	for(int i=0; i < providers.length; i++) {
	    fileTypes[i] = providers[i].getAudioFileTypes();
	    numTypes += fileTypes[i].length;
	}
	// Now put them in a 1-D array
	returnTypes = new AudioFileFormat.Type[ numTypes ];
	for(int i=0; i < providers.length; i++) {
	    for(int j=0; j < fileTypes[i].length; j++) {
		returnTypes[ index ] = fileTypes[i][j];
		index++;
	    }
	}
	
	return returnTypes;
    }
    
    
    /**
     * Indicates whether file writing support for the specified file type is provided
     * by the system.
     * @param fileType the file type for which write capabilities are queried
     * @return <code>true</code> if the file type is supported,
     * otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(AudioFileFormat.Type fileType) {
	
	AudioFileWriter providers[] = getAudioFileWriters();
	
	boolean isSupported = false;
	
	for(int i=0; i < providers.length; i++ ) {
	    isSupported = providers[i].isFileTypeSupported(fileType);
	    if(isSupported==true) {
		return isSupported;
	    }
	}
	return isSupported;
    }
    
    
    /**
     * Obtains the file types that the system can write from the
     * audio input stream specified.
     * @param stream the audio input stream for which audio file type support
     * is queried
     * @return array of file types.  If no file types are supported,
     * an array of length 0 is returned.
     */
    public static AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
	
	AudioFileWriter providers[] = getAudioFileWriters();
	AudioFileFormat.Type fileTypes[][] = new AudioFileFormat.Type[ providers.length ][];
	AudioFileFormat.Type returnTypes[] = null;
	int numTypes = 0;
	int index = 0;
	
	// Get all file types
	for(int i=0; i < providers.length; i++) {
	    fileTypes[i] = providers[i].getAudioFileTypes(stream);
	    numTypes += fileTypes[i].length;
	}
	// Now put them in a 1-D array
	returnTypes = new AudioFileFormat.Type[ numTypes ];
	for(int i=0; i < providers.length; i++) {
	    for(int j=0; j < fileTypes[i].length; j++) {
		returnTypes[ index ] = fileTypes[i][j];
		index++;
	    }
	}
	
	return returnTypes;
    }
    
    
    /**
     * Indicates whether an audio file of the specified file type can be written
     * from the indicated audio input stream.
     * @param fileType the file type for which write capabilities are queried
     * @param stream the stream for which file-writing support is queried
     * @return <code>true</code> if the file type is supported for this audio input stream,
     * otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(AudioFileFormat.Type fileType,
					      AudioInputStream stream) {
	
	AudioFileWriter providers[] = getAudioFileWriters();
	
	boolean isSupported = false;
	
	for(int i=0; i < providers.length; i++ ) {
	    isSupported = providers[i].isFileTypeSupported(fileType, stream);
	    if(isSupported==true) {
		return isSupported;
	    }
	}
	return isSupported;
    }
    
    
    /**
     * Writes a stream of bytes representing an audio file of the specified file type
     * to the output stream provided.  Some file types require that
     * the length be written into the file header; such files cannot be written from
     * start to finish unless the length is known in advance.  An attempt
     * to write a file of such a type will fail with an IOException if the length in
     * the audio file type is <code>AudioSystem.NOT_SPECIFIED</code>.
     *
     * @param stream the audio input stream containing audio data to be
     * written to the file
     * @param fileType the kind of audio file to write
     * @param out the stream to which the file data should be written
     * @return the number of bytes written to the output stream
     * @throws IOException if an input/output exception occurs
     * @throws IllegalArgumentException if the file type is not supported by
     * the system
     * @see #isFileTypeSupported
     * @see	#getAudioFileTypes
     */
    public static int write(AudioInputStream stream, AudioFileFormat.Type fileType,
			    OutputStream out) throws IOException {
	
	AudioFileWriter providers[] = getAudioFileWriters();
	int bytesWritten = 0;
	boolean flag = false;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		bytesWritten = providers[i].write( stream, fileType, out ); // throws IOException
		flag = true;
		break;
	    } catch (IllegalArgumentException e) {
		// thrown if this provider cannot write the sequence, try the next
		continue;
	    }
	}
	if( flag==false ) {
	    throw new IllegalArgumentException("could not write audio file: file type not supported: " + fileType);
	} else {
	    return bytesWritten;
	}
    }
    
    
    /**
     * Writes a stream of bytes representing an audio file of the specified file type
     * to the external file provided.
     * @param stream the audio input stream containing audio data to be
     * written to the file
     * @param fileType the kind of audio file to write
     * @param out the external file to which the file data should be written
     * @return the number of bytes written to the file
     * @throws IOException if an I/O exception occurs
     * @throws IllegalArgumentException if the file type is not supported by
     * the system
     * @see #isFileTypeSupported
     * @see	#getAudioFileTypes
     */
    public static int write(AudioInputStream stream, AudioFileFormat.Type fileType,
			    File out) throws IOException {
	
	AudioFileWriter providers[] = getAudioFileWriters();
	int bytesWritten = 0;
	boolean flag = false;
	
	//$$fb 2001-08-03: reverse this loop to give external providers more priority (Bug #4487550)
	for(int i = providers.length-1; i >= 0; i-- ) {
	    try {
		bytesWritten = providers[i].write( stream, fileType, out ); // throws IOException
		flag = true;
		break;
	    } catch (IllegalArgumentException e) {
		// thrown if this provider cannot write the sequence, try the next
		continue;
	    }
	}
	if( flag==false ) {
	    throw new IllegalArgumentException("could not write audio file: file type not supported: " + fileType);
	} else {
	    return bytesWritten;
	}
    }
    
    
    // METHODS FOR INTERNAL IMPLEMENTATION USE
    
    /**
     * Obtains the set of MixerProviders on the system.
     */
    private static Vector getMixerProviders() {
	
	Vector providers = null;
	
	try {
	    
	    Class.forName( "sun.misc.Service" );
	    providers = getJDK13Services("javax.sound.sampled.spi.MixerProvider");
	    
	} catch (Exception e) {
	    
	    if (DEBUG) e.printStackTrace();

	    // we're not running with 1.3's SPI mechanism
	    providers = getDefaultServices("javax.sound.sampled.spi.MixerProvider");
	}
	
	return providers;
    }
    
    /**
     * Obtains the set of format converters (codecs, transcoders, etc.)
     * that are currently installed on the system.
     * @return an array of
     * {@link javax.sound.sampled.spi.FormatConversionProvider
     * FormatConversionProvider}
     * objects representing the available format converters.  If no format
     * converters readers are available on the system, an array of length 0 is
     * returned.
     */
    private static FormatConversionProvider[] getFormatConversionProviders() {
	
	Vector v = new Vector();
	FormatConversionProvider varray[];
	
	try {
	    
	    Class.forName( "sun.misc.Service" );
	    
	    v = getJDK13Services("javax.sound.sampled.spi.FormatConversionProvider");
	    
	} catch (Exception e) {
	    
	    if (DEBUG) e.printStackTrace();

	    // we're not running with 1.3's SPI mechanism
	    v = getDefaultServices("javax.sound.sampled.spi.FormatConversionProvider");
	}
	
	varray = new FormatConversionProvider[ v.size() ];
	for( int i=0; i < varray.length; i++ ) {
	    varray[i] = (FormatConversionProvider)(v.elementAt(i));
	}
	return varray;
    }
    
    /**
     * Obtains the set of audio file readers that are currently installed on the system.
     * @return an array of
     * {@link javax.sound.sampled.spi.AudioFileReader
     * AudioFileReader}
     * objects representing the installed audio file readers.  If no audio file
     * readers are available on the system, an array of length 0 is returned.
     */
    private static AudioFileReader[] getAudioFileReaders() {
	
	Vector v = new Vector();
	AudioFileReader varray[];
	
	try {
	    
	    Class.forName( "sun.misc.Service" );
	    
	    v = getJDK13Services("javax.sound.sampled.spi.AudioFileReader");
	    
	} catch (Exception e) {
	    
	    if (DEBUG) e.printStackTrace();

	    // we're not running with 1.3's SPI mechanism
	    v = getDefaultServices("javax.sound.sampled.spi.AudioFileReader");
	}
	
	varray = new AudioFileReader[ v.size() ];
	for( int i=0; i < varray.length; i++ ) {
	    varray[i] = (AudioFileReader)(v.elementAt(i));
	}
	return varray;
    }
    
    /**
     * Obtains the set of audio file writers that are currently installed on the system.
     * @return an array of
     * {@link javax.sound.samples.spi.AudioFileWriter AudioFileWriter}
     * objects representing the available audio file writers.  If no audio file
     * writers are available on the system, an array of length 0 is returned.
     */
    private static AudioFileWriter[] getAudioFileWriters() {
	
	Vector v = new Vector();
	AudioFileWriter varray[];
	
	try {
	    
	    Class.forName( "sun.misc.Service" );
	    
	    v = getJDK13Services("javax.sound.sampled.spi.AudioFileWriter");
	    
	} catch (Exception e) {
	    
	    
	    if (DEBUG) e.printStackTrace();

	    // we're not running with 1.3's SPI mechanism
	    v = getDefaultServices("javax.sound.sampled.spi.AudioFileWriter");
	}
	
	
	varray = new AudioFileWriter[ v.size() ];
	for( int i=0; i < varray.length; i++ ) {
	    
	    varray[i] = (AudioFileWriter)(v.elementAt(i));
	}
	return varray;
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
	    if (DEBUG) e1.printStackTrace();
	    v = new Vector();
	} catch(ClassNotFoundException e2) {
	    if (DEBUG) e2.printStackTrace();
	    v = new Vector();
	} catch(IllegalAccessException e3) {
	    if (DEBUG) e3.printStackTrace();
	    v = new Vector();
	} catch(NoSuchMethodException e4) {
	    if (DEBUG) e4.printStackTrace();
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
	    if (DEBUG) e1.printStackTrace();
	    v = new Vector();
	} catch(ClassNotFoundException e2) {
	    if (DEBUG) e2.printStackTrace();
	    v = new Vector();
	} catch(IllegalAccessException e3) {
	    if (DEBUG) e3.printStackTrace();
	    v = new Vector();
	} catch(NoSuchMethodException e4) {
	    if (DEBUG) e4.printStackTrace();
	    v = new Vector();
	}
	return v;
    }
    
}
