/*
 * @(#)SoundbankReader.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi.spi;		  	 

import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.net.URL;

import javax.sound.midi.Soundbank;
import javax.sound.midi.InvalidMidiDataException;


/**
 * A <code>SoundbankReader</code> supplies soundbank file-reading services.
 * Concrete subclasses of <code>SoundbankReader</code> parse a given 
 * soundbank file, producing a {@link javax.sound.midi.Soundbank} 
 * object that can be loaded into a {@link javax.sound.midi.Synthesizer}.
 *
 * @since 1.3
 * @version 1.16 03/01/23
 * @author Kara Kytle
 */
public abstract class SoundbankReader {


    /**
     * Obtains a soundbank object from the URL provided.
     * @param url URL representing the soundbank.
     * @return soundbank object
     * @throws InvalidMidiDataException if the URL does not point to 
     * valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(URL url) throws InvalidMidiDataException, IOException;


    /**
     * Obtains a soundbank object from the <code>InputStream</code> provided.
     * @param stream <code>InputStream</code> representing the soundbank
     * @return soundbank object
     * @throws InvalidMidiDataException if the stream does not point to 
     * valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(InputStream stream) throws InvalidMidiDataException, IOException;


    /**
     * Obtains a soundbank object from the <code>File</code> provided.
     * @param file the <code>File</code> representing the soundbank
     * @return soundbank object
     * @throws InvalidMidiDataException if the file does not point to 
     * valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(File file) throws InvalidMidiDataException, IOException;


    // OLD

    /**
     * Obtains the soundbank type on which this parser operates.
     * @return bank type
     */
    //public abstract Soundbank.BankType getBankType();


    /**
     * Obtains a soundbank object from the input stream provided.
     * @param stream input stream representing the soundbank file
     * @return soundbank object, or null if a soundbank 
     * cannot be obtained from the input stream provided.
     * @throws IOException if an I/O error occurs
     */
    //public abstract Soundbank getSoundbank(InputStream stream) throws IOException;
}
