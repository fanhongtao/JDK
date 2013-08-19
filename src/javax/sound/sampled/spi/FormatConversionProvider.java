/*
 * @(#)FormatConversionProvider.java	1.27 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.sampled.spi;		  	 

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * A format conversion provider provides format conversion services
 * from one or more input formats to one or more output formats. 
 * Converters include codecs, which encode and/or decode audio data,
 * as well as transcoders, etc.  Format converters provide methods for
 * determining what conversions are supported and for obtaining an audio
 * stream from which converted data can be read.
 * <p>
 * The source format represents the format of the incoming
 * audio data, which will be converted.
 * <p>  
 * The target format represents the format of the processed, converted 
 * audio data.  This is the format of the data that can be read from 
 * the stream returned by one of the <code>getAudioInputStream</code> methods.
 *
 * @author Kara Kytle
 * @version 1.27, 03/01/23
 * @since 1.3
 */
public abstract class FormatConversionProvider {


    // NEW METHODS

    /**
     * Obtains the set of source format encodings from which format
     * conversion services are provided by this provider.
     * @return array of source format encodings.  The array will always 
     * have a length of at least 1.
     */
    public abstract AudioFormat.Encoding[] getSourceEncodings();
	

    /**
     * Obtains the set of target format encodings to which format
     * conversion services are provided by this provider.
     * @return array of target format encodings.  The array will always 
     * have a length of at least 1.
     */
    public abstract AudioFormat.Encoding[] getTargetEncodings();


    /**
     * Indicates whether the format converter supports conversion from the
     * specified source format encoding.
     * @param sourceEncoding the source format encoding for which support is queried
     * @return <code>true</code> if the encoding is supported, otherwise <code>false</code>
     */
    public boolean isSourceEncodingSupported(AudioFormat.Encoding sourceEncoding){

	AudioFormat.Encoding sourceEncodings[] = getSourceEncodings();

	for(int i=0; i<sourceEncodings.length; i++) {
	    if( sourceEncoding.equals( sourceEncodings[i]) ) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Indicates whether the format converter supports conversion to the
     * specified target format encoding.
     * @param targetEncoding the target format encoding for which support is queried
     * @return <code>true</code> if the encoding is supported, otherwise <code>false</code>
     */
    public boolean isTargetEncodingSupported(AudioFormat.Encoding targetEncoding){
	
	AudioFormat.Encoding targetEncodings[] = getTargetEncodings();

	for(int i=0; i<targetEncodings.length; i++) {
	    if( targetEncoding.equals( targetEncodings[i]) ) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Obtains the set of target format encodings supported by the format converter 
     * given a particular source format.  
     * If no target format encodings are supported for this source format, 
     * an array of length 0 is returned.
     * @return array of supported target format encodings. 
     */
    public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat);

	
    /**
     * Indicates whether the format converter supports conversion to a particular encoding
     * from a particular format.
     * @param targetEncoding desired encoding of the outgoing data
     * @param sourceFormat format of the incoming data
     * @return <code>true</code> if the conversion is supported, otherwise <code>false</code>
     */
    public boolean isConversionSupported(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat){

	AudioFormat.Encoding targetEncodings[] = getTargetEncodings(sourceFormat);

	for(int i=0; i<targetEncodings.length; i++) {
	    if( targetEncoding.equals( targetEncodings[i]) ) {
		return true;
	    }
	}
	return false;	
    }


    /**
     * Obtains the set of target formats with the encoding specified 
     * supported by the format converter 
     * If no target formats with the specified encoding are supported 
     * for this source format, an array of length 0 is returned.
     * @return array of supported target formats. 
     */
    public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat);


    /**
     * Indicates whether the format converter supports conversion to one
     * particular format from another.
     * @param targetFormat desired format of outgoing data
     * @param sourceFormat format of the incoming data
     * @return <code>true</code> if the conversion is supported, otherwise <code>false</code>
     */
    public boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat){

	AudioFormat targetFormats[] = getTargetFormats( targetFormat.getEncoding(), sourceFormat );

	for(int i=0; i<targetFormats.length; i++) {
	    if( targetFormat.matches( targetFormats[i] ) ) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Obtains an audio input stream with the specified encoding from the given audio
     * input stream.
     * @param targetEncoding desired encoding of the stream after processing
     * @param sourceStream stream from which data to be processed should be read
     * @return stream from which processed data with the specified target encoding may be read
     * @throws IllegalArgumentException if the format combination supplied is
     * not supported.
     */
    public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream);


    /**
     * Obtains an audio input stream with the specified format from the given audio
     * input stream.
     * @param targetFormat desired data format of the stream after processing
     * @param sourceStream stream from which data to be processed should be read
     * @return stream from which processed data with the specified format may be read
     * @throws IllegalArgumentException if the format combination supplied is
     * not supported.
     */
    public abstract AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream);



    // OLD METHODS


    /**
     * Obtains the set of input encodings supported by the format converter.
     * If none is supported, an array of length 0 is returned.
     * @return array of supported input encodings. 
     */
    /*	public abstract AudioFormat.Encoding[] getInputEncodings();
     */

    /**
     * Obtains the set of input encodings supported by the format converter,
     * given a particular output encoding.
     * If none is supported, an array of length 0 is returned.
     * @return array of supported input encodings. 
     */
    /*	public abstract AudioFormat.Encoding[] getInputEncodings(AudioFormat.Encoding outputEncoding);
     */
    /**
     * Obtains the set of input formats supported by the format converter 
     * given a particular output format.  
     * If no input formats are supported for this output format, 
     * an array of length 0 is returned.
     * @param outputFormat the desired format of the converted audio
     * @return array of supported input formats. 
     */
    /*	public abstract AudioFormat[] getInputFormats(AudioFormat outputFormat);
     */


    /**
     * Obtains the set of output encodings supported by the format converter.
     * If none is supported, an array of length 0 is returned.
     * @return array of supported output encodings. 
     */
    /*	public abstract AudioFormat.Encoding[] getOutputEncodings();
     */	
    /**
     * Obtains the set of output encodings supported by the format converter,
     * given a particular input encoding.
     * If none is supported, an array of length 0 is returned.
     * @return array of supported output encodings. 
     */
    /*	public abstract AudioFormat.Encoding[] getOutputEncodings(AudioFormat.Encoding inputEncoding);
     */
	
    /**
     * Obtains the set of output formats supported by the format converter 
     * given a particular input format.  
     * If no output formats are supported for this input format, 
     * an array of length 0 is returned.
     * @return array of supported output formats. 
     */
    /*	public abstract AudioFormat[] getOutputFormats(AudioFormat inputFormat);
     */
    /**
     * Indicates whether the format converter supports conversion from one
     * particular format to another.
     * @param inputFormat format of the incoming data
     * @param desired format of outgoing data
     * @return <code>true</code> if the conversion is supported, otherwise <code>false</code>
     */
    /*	public abstract boolean isConversionSupported(AudioFormat inputFormat, AudioFormat outputFormat);
     */
    /**
     * Obtains an audio input stream with the specified format from the given audio
     * stream.
     * @param outputFormat desired data format of the stream after processing
     * @param stream stream from which data to be processed should be read
     * @return stream from which processed data may be read
     * @throws IllegalArgumentException if the format combination supplied is
     * not supported.
     */
    /*	public abstract AudioInputStream getConvertedStream(AudioFormat outputFormat, AudioInputStream stream);
     */

}
