/*
 * @(#)AudioFileFormat.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.sampled;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An instance of the <code>AudioFileFormat</code> class describes
 * an audio file, including the file type, the file's length in bytes,
 * the length in sample frames of the audio data contained in the file,
 * and the format of the audio data.
 * <p>
 * The <code>{@link AudioSystem}</code> class includes methods for determining the format
 * of an audio file, obtaining an audio input stream from an audio file, and
 * writing an audio file from an audio input stream.
 *
 * @author David Rivas
 * @author Kara Kytle
 * @version 1.19 03/01/23
 * @see AudioInputStream
 * @since 1.3
 */
public class AudioFileFormat {
    
    
    // INSTANCE VARIABLES
    
    
    /**
     * File type.
     */
    private Type type;
    
    /**
     * File length in bytes
     */
    private int byteLength;
    
    /**
     * Format of the audio data contained in the file.
     */
    private AudioFormat format;
    
    /**
     * Audio data length in sample frames
     */
    private int frameLength;
    
    
    /**
     * Constructs an audio file format object.
     * This protected constructor is intended for use by providers of file-reading
     * services when returning information about an audio file or about supported audio file
     * formats.
     * @param type the type of the audio file
     * @param byteLength the length of the file in bytes, or <code>AudioSystem.NOT_SPECIFIED</code>
     * @param format the format of the audio data contained in the file
     * @param frameLength the audio data length in sample frames, or <code>AudioSystem.NOT_SPECIFIED</code>
     *
     * @see #getType
     */
    protected AudioFileFormat(Type type, int byteLength, AudioFormat format, int frameLength) {
	
	this.type = type;
	this.byteLength = byteLength;
	this.format = format;
	this.frameLength = frameLength;
    }
    
    
    /**
     * Constructs an audio file format object.
     * This public constructor may be used by applications to describe the
     * properties of a requested audio file.
     * @param type the type of the audio file
     * @param format the format of the audio data contained in the file
     * @param frameLength the audio data length in sample frames, or <code>AudioSystem.NOT_SPECIFIED</code>
     */
    public AudioFileFormat(Type type, AudioFormat format, int frameLength) {
	
	
	this(type,AudioSystem.NOT_SPECIFIED,format,frameLength);
    }
    
    /**
     * Obtains the audio file type, such as <code>WAVE</code> or <code>AU</code>.
     * @return the audio file type
     *
     * @see Type#WAVE
     * @see Type#AU
     * @see Type#AIFF
     * @see Type#AIFC
     * @see Type#SND
     */
    public Type getType() {
	return type;
    }
    
    /**
     * Obtains the size in bytes of the entire audio file (not just its audio data).
     * @return the audio file length in bytes
     * @see AudioSystem#NOT_SPECIFIED
     */
    public int getByteLength() {
	return byteLength;
    }
    
    /**
     * Obtains the format of the audio data contained in the audio file.
     * @return the audio data format
     */
    public AudioFormat getFormat() {
	return format;
    }
    
    /**
     * Obtains the length of the audio data contained in the file, expressed in sample frames.
     * @return the number of sample frames of audio data in the file
     * @see AudioSystem#NOT_SPECIFIED
     */
    public int getFrameLength() {
	return frameLength;
    }
    
    /**
     * Provides a string representation of the file format.
     * @return the file format as a string
     */
    public String toString() {
	
	StringBuffer buf = new StringBuffer();
	
	//$$fb2002-11-01: fix for 4672864: AudioFileFormat.toString() throws unexpected NullPointerException
	if (type != null) {
	    buf.append(type.toString() + " (." + type.getExtension() + ") file");
	} else {
	    buf.append("unknown file format");
	}
	
	if (byteLength != AudioSystem.NOT_SPECIFIED) {
	    buf.append(", byte length: " + byteLength);
	}
	
	buf.append(", data format: " + format);
	
	if (frameLength != AudioSystem.NOT_SPECIFIED) {
	    buf.append(", frame length: " + frameLength);
	}
	
	return new String(buf);
    }
    
    
    /**
     * An instance of the <code>Type</code> class represents one of the
     * standard types of audio file.  Static instances are provided for the
     * common types.
     */
    public static class Type {
	
	// FILE FORMAT TYPE DEFINES
	
	/**
	 * Specifies a WAVE file.
	 */
	public static final Type WAVE		= new Type("WAVE", "wav");
	
	/**
	 * Specifies an AU file.
	 */
	public static final Type AU			= new Type("AU", "au");
	
	/**
	 * Specifies an AIFF file.
	 */
	public static final Type AIFF		= new Type("AIFF", "aif");
	
	/**
	 * Specifies an AIFF-C file.
	 */
	public static final Type AIFC		= new Type("AIFF-C", "aifc");
	
	/**
	 * Specifies a SND file.
	 */
	public static final Type SND		= new Type("SND", "snd");
	
	
	// INSTANCE VARIABLES
	
	/**
	 * File type name.
	 */
	private final String name;
	
	/**
	 * File type extension.
	 */
	private final String extension;
	
	
	// CONSTRUCTOR
	
	/**
	 * Constructs a file type.
	 * @param name the string that names the file type
	 * @param extension the string that commonly marks the file type
	 */
	protected Type(String name, String extension) {
	    
	    this.name = name;
	    this.extension = extension;
	}
	
	
	// METHODS
	
	/**
	 * Finalizes the equals method
	 */
	public final boolean equals(Object obj) {
	    return super.equals(obj);
	}
	
	/**
	 * Finalizes the hashCode method
	 */
	public final int hashCode() {
	    return super.hashCode();
	}
	
	/**
	 * Provides the file type's name as the <code>String</code> representation
	 * of the file type.
	 * @return the file type's name
	 */
	public final String toString() {
	    return name;
	}
	
	/**
	 * Obtains the common file name extension for this file type.
	 * @return file type extension
	 */
	public String getExtension() {
	    return extension;
	}
	
    } // class Type
    
} // class AudioFileFormat
