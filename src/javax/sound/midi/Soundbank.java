/*
 * @(#)Soundbank.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.midi;	

import java.net.URL;


/**
 * A <code>Soundbank</code> contains a set of <code>Instruments</code> 
 * that can be loaded into a <code>Synthesizer</code>.
 * Note that a Java Sound <code>Soundbank</code> is different from a MIDI bank.  
 * MIDI permits up to 16383 banks, each containing up to 128 instruments
 * (also sometimes called programs, patches, or timbres).
 * However, a <code>Soundbank</code> can contain 16383 times 128 instruments,
 * because the instruments within a <code>Soundbank</code> are indexed by both
 * a MIDI program number and a MIDI bank number (via a <code>Patch</code> 
 * object). Thus, a <code>Soundbank</code> can be thought of as a collection
 * of MIDI banks.
 * <p>
 * <code>Soundbank</code> includes methods that return <code>String</code> 
 * objects containing the sound bank's name, manufacturer, version number, and
 * description.  The precise content and format of these strings is left
 * to the implementor.
 * <p>
 * Different synthesizers use a variety of synthesis techniques.  A common
 * one is wavetable synthesis, in which a segment of recorded sound is
 * played back, often with looping and pitch change.  The Downloadable Sound
 * (DLS) format uses segments of recorded sound, as does the Headspace Engine.
 * <code>Soundbanks</code> and <code>Instruments</code> that are based on 
 * wavetable synthesis (or other uses of stored sound recordings) should 
 * typically implement the <code>getResources()</code>
 * method to provide access to these recorded segments.  This is optional, 
 * however; the method can return an zero-length array if the synthesis technique
 * doesn't use sampled sound (FM synthesis and physical modeling are examples
 * of such techniques), or if it does but the implementor chooses not to make the 
 * samples accessible.
 *
 * @see Synthesizer#getDefaultSoundbank
 * @see Synthesizer#isSoundbankSupported
 * @see Synthesizer#loadInstruments(Soundbank, Patch[])
 * @see Patch
 * @see Instrument
 * @see SoundbankResource
 *
 * @version 1.22, 03/01/23
 * @author David Rivas
 * @author Kara Kytle
 */

public interface Soundbank {


    /**
     * Obtains the name of the sound bank.
     * @return a <code>String</code> naming the sound bank
     */
    public String getName();

    /**
     * Obtains the version string for the sound bank.
     * @return a <code>String</code> that indicates the sound bank's version
     */
    public String getVersion();

    /**
     * Obtains a <code>string</code> naming the company that provides the 
     * sound bank
     * @return the vendor string
     */
    public String getVendor();

    /**
     * Obtains a textual description of the sound bank, suitable for display.
     * @return a <code>String</code> that describes the sound bank 
     */
    public String getDescription();


    /**
     * Extracts a list of non-Instrument resources contained in the sound bank.
     * @return an array of resources, exclusing instruments.  If the sound bank contains
     * no resources (other than instruments), returns an array of length 0.
     */
    public SoundbankResource[] getResources();


    /**
     * Obtains a list of instruments contained in this sound bank.
     * @return an array of the <code>Instruments</code> in this 
     * <code>SoundBank</code>
     * If the sound bank contains no instruments, returns an array of length 0.
     *
     * @see Synthesizer#getLoadedInstruments
     * @see #getInstrument(Patch)
     */
    public Instrument[] getInstruments();

    /**
     * Obtains an <code>Instrument</code> from the given <code>Patch</code>.
     * @param patch a <code>Patch</code> object specifying the bank index 
     * and program change number
     * @return the requested instrument, or <code>null</code> if the
     * sound bank doesn't contain that instrument
     *
     * @see #getInstruments
     * @see Synthesizer#loadInstruments(Soundbank, Patch[])
     */
    public Instrument getInstrument(Patch patch);


    // OLD

    /**
     * Obtains the names of all samples contained in the sound bank.
     * @return sample names or identifiers.
     * If the sound bank contains no samples, returns an array with length 0.
     */
    //public String[] getSampleNames();

    /**
     * Extracts a sample contained in the sound bank.
     * @param name name identifying the sample to be extracted.
     * @throws IllegalArgumentException thrown if the requested sample does
     * not exist in the soundbank.
     * @return requested sample, or null if the sample could not be obtained from
     * the sound bank.
     */
    //public AudioInputStream getSample(String name);


    /**
     * Indicates that the sound bank is of type DLS.
     */
    //public static final String DLS = "DLS";

    /**
     * Indicates that the sound bank is of type HSB.
     */
    //public static final String HSB = "HSB";


    /** 
     * Obtains the sound bank type.
     * @return Sound bank type.
     * @see BankType#DLS
     * @see BankType#HSB
     */
    //public BankType getType();

    /**
     * Obtains a URL representing the default location of the sound bank,
     * or a location where more information may be available.
     * @return Locator for this sound bank, or <code>null</code> if the sound
     * bank does not have a locator.
     * <p>
     */
    //public URL getLocator();

    /**
     * Obtains the names of all sequences contained in the sound bank.
     * @return sequence names or identifiers.
     * If the sound bank contains no sequences, returns an array with length 0.
     */
    //public String[] getSequenceNames();

    /**
     * Extracts a sequence contained in the sound bank.
     * @param name name identifying the sequence to be extracted.
     * @throws IllegalArgumentException thrown if the requested sequence does
     * not exist in the soundbank.
     * @return requested sequence, or null if the sequence could not be obtained from
     * the sound bank.
     */
    //public Sequence getSequence(String name);

    /**
     * Obtains a URL representing the default location of the sound bank,
     * or a location where more information may be available.
     * @return Locator for this sound bank, or <code>null</code> if the sound
     * bank does not have a locator.
     * <p>
     */
    //public URL getLocator();

    /**
     * Obtains the names of all sequences contained in the sound bank.
     * @return sequence names or identifiers.
     * If the sound bank contains no sequences, returns an array with length 0.
     */
    //public String[] getSequenceNames();

    /**
     * Extracts a sequence contained in the sound bank.
     * @param name name identifying the sequence to be extracted.
     * @throws IllegalArgumentException thrown if the requested sequence does
     * not exist in the soundbank.
     * @return requested sequence, or null if the sequence could not be obtained from
     * the sound bank.
     */
    //public Sequence getSequence(String name);


    /**
     * Represents MIDI soundbank types.
     */
    /*
      public static class BankType {
    */
    /**
     * Bank type name.
     */
    //		private String name;
			
    /**
     * Constructs a bank type.
     * @param name name of the bank type
     */
    /*
      protected BankType(String name) {

      this.name = name;
      }
    */

    /**
     * Determines whether two objects are equal.
     * Returns true if the objects are identical.
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    /*
      public final boolean equals(Object obj) {

      return super.equals(obj);
      }
    */

    /**
     * Finalizes the hashcode method.
     */
    /*
      public final int hashCode() {

      return super.hashCode();
      }
    */

    /**
     * Provides the element type name as the String representation of the type.
     * @return type name
     */
    /*
      public final String toString() {
				
      return name;
      }	
    */

    /**
     * DLS bank type
     */
    //		public static final BankType DLS = new BankType("DLS");

    /**
     * HSB bank type.
     */
    //		public static final BankType HSB = new BankType("HSB");
    /*
      } // class BankType
    */
}
