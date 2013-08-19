/*
 * @(#)CodeSetConversion.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.core;

import java.util.Map;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import sun.io.*;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/**
 * Collection of classes, interfaces, and factory methods for
 * CORBA code set conversion.
 *
 * This is mainly used to shield other code from the sun.io
 * converters which might change, as well as provide some basic
 * translation from conversion to CORBA error exceptions.  Some
 * extra work is required here to facilitate the way CORBA
 * says it uses UTF-16 as of the 00-11-03 spec.
 */
public class CodeSetConversion
{
    /**
     * Abstraction for char to byte conversion.
     *
     * Must be used in the proper sequence:
     *
     * 1)  convert
     * 2)  Optional getNumBytes and/or getAlignment (if necessary)
     * 3)  getBytes (see warning)
     */
    public abstract static class CTBConverter
    {
        // Perform the conversion of the provided char or String,
        // allowing the caller to query for more information
        // before writing.
        public abstract void convert(char chToConvert);
        public abstract void convert(String strToConvert);

        // How many bytes resulted from the conversion?
        public abstract int getNumBytes();

        // What's the maximum number of bytes per character?
        public abstract int getMaxBytesPerChar();

        public abstract boolean isFixedWidthEncoding();

        // What byte boundary should the stream align to before
        // calling writeBytes?  For instance, a fixed width
        // encoding with 2 bytes per char in a stream which
        // doesn't encapsulate the char's bytes should align
        // on a 2 byte boundary.  (Ex:  UTF16 in GIOP1.1)
        //
        // Note: This has no effect on the converted bytes.  It
        // is just information available to the caller.
        public abstract int getAlignment();

        // Get the resulting bytes.  Warning:  You must use getNumBytes()
        // to determine the end of the data in the byte array instead
        // of array.length!  The array may be used internally, so don't
        // save references.
        public abstract byte[] getBytes();
    }
    
    /**
     * Abstraction for byte to char conversion.
     */
    public abstract static class BTCConverter
    {
        // In GIOP 1.1, interoperability can only be achieved with
        // fixed width encodings like UTF-16.  This is because wstrings
        // specified how many code points follow rather than specifying
        // the length in octets.
        public abstract boolean isFixedWidthEncoding();
        public abstract int getFixedCharWidth();

        // Called after getChars to determine the true size of the
        // converted array.
        public abstract int getNumChars();

        // Perform the conversion using length bytes from the given
        // input stream.  Warning:  You must use getNumChars() to
        // determine the correct length of the resulting array.
        // The same array may be used internally over multiple
        // calls.
        public abstract char[] getChars(byte[] bytes, int offset, int length);
    }

    /**
     * Implementation of CTBConverter which uses a sun.io.CharToByteConverter
     * to do the real work.  Handles translation of exceptions to the 
     * appropriate CORBA versions.
     */
    private class JavaCTBConverter extends CTBConverter
    {
        // sun.io.CharToByteConverter which actually does the work.  We
        // have to use it directly rather than through String's interface
        // because we want to know when errors occur during the conversion.
        // It also allows us to do one less allocation and copy.
        private CharToByteConverter ctb;

        // Proper alignment for this type of converter.  For instance,
        // ASCII has alignment of 1 (1 byte per char) but UTF16 has
        // alignment of 2 (2 bytes per char)
        private int alignment;

        // Char buffer to hold the input.  Maintained across multiple
        // conversions to save a memory allocation (maybe a non-issue)
        // and make single char conversion faster.
        private char[] chars = null;

        // How many bytes are generated from the conversion?
        private int numBytes = 0;

        // How many characters were converted (temporary variable
        // for cross method communication)
        private int numChars = 0;

        // Byte buffer holding the converted input.  This is necessary
        // since we have to do calculations that require the conversion
        // before writing the array to the stream.  It's reused
        // across multiple conversions, so don't do another conversion
        // until you've disposed of your reference!
        private byte[] buffer;

        // What code set are we using?
        private OSFCodeSetRegistry.Entry codeset;

        public JavaCTBConverter(OSFCodeSetRegistry.Entry codeset,
                                int alignmentForEncoding) {

            try {
                ctb = cache.getCharToByteConverter(codeset.getName());
                if (ctb == null) {
                    ctb = CharToByteConverter.getConverter(codeset.getName());
                    cache.setConverter(codeset.getName(), ctb);

                    // When substitution mode is false, the converter
                    // will throw exceptions when it encounters illegal
                    // encodings.
                    ctb.setSubstitutionMode(false);
                }
            } catch(UnsupportedEncodingException uee) {
                // This can only happen if one of our Entries has
                // an invalid name.
                throw new INTERNAL("Invalid converter name: " + codeset.getName());
            }

            this.codeset = codeset;
            alignment = alignmentForEncoding;
        }

        public final int getMaxBytesPerChar() {
            return ctb.getMaxBytesPerChar();
        }

        public void convert(char chToConvert) {
            if (chars == null)
                chars = new char[1];
            
            // The CharToByteConverter only takes a char[]
            chars[0] = chToConvert;
            numChars = 1;

            convertCharArray();
        }

        public void convert(String strToConvert) {
            // Try to save a memory allocation if possible.  Usual
            // space/time trade off.  If we could get the char[] out of
            // the String without copying, that would be great, but
            // it's forbidden since String is immutable.
            if (chars == null || chars.length < strToConvert.length())
                chars = new char[strToConvert.length()];

            numChars = strToConvert.length();
            
            strToConvert.getChars(0, numChars, chars, 0);

            convertCharArray();
        }
        
        public final int getNumBytes() {
            return numBytes;
        }
        
        public final int getAlignment() {
            return alignment;
        }

        public final boolean isFixedWidthEncoding() {
            return codeset.isFixedWidth();
        }

        public byte[] getBytes() {
            // Note that you can't use buffer.length since the byte array might
            // be larger than the actual number of converted bytes depending
            // on the encoding.
            //
            // Warning 2:  The byte array is reused across multiple calls to the
            // converter!
            return buffer;
        }

        private void convertCharArray() {
            try {
                
                // Possible optimization of directly converting into the CDR buffer.
                // However, that means the CDR code would have to reserve
                // a 4 byte string length ahead of time, and we'd need a
                // confusing partial conversion scheme for when we couldn't
                // fit everything in the buffer but needed to know the
                // converted length before proceeding due to fragmentation.
                // Then there's the issue of the chunking code.
                //
                // For right now, this is less messy and basic tests don't
                // show more than a 1 ms penalty worst case.  Less than a
                // factor of 2 increase.
                if (buffer == null || buffer.length < numChars * ctb.getMaxBytesPerChar())
                    buffer = new byte[numChars * ctb.getMaxBytesPerChar()];

                // Return the converter to its initial state
                ctb.reset();

                // Convert the characters
                numBytes = ctb.convert(chars, 0, numChars,
                                       buffer, 0, buffer.length);

                // Converters must be flushed to finish up
                numBytes += ctb.flush(buffer, 0, buffer.length);

            } catch (MalformedInputException mie) {
                // There were illegal Unicode char pairs
                throw new DATA_CONVERSION(mie.getMessage(),
                                          MinorCodes.BAD_UNICODE_PAIR,
                                          CompletionStatus.COMPLETED_NO);
            } catch (UnknownCharacterException uce) {
                // A character doesn't map to the desired code set
                // CORBA formal 00-11-03.
                throw new DATA_CONVERSION(uce.getMessage(),
                                          MinorCodes.CHAR_NOT_IN_CODESET,
                                          CompletionStatus.COMPLETED_NO);
            } catch (ConversionBufferFullException cbfe) {
                // If this happens, then the CharToByteConverter was lying
                // about the maximum bytes per char.
                throw new INTERNAL(cbfe.getMessage(),
                                   MinorCodes.CTB_CONVERTER_FAILURE,
                                   CompletionStatus.COMPLETED_NO);
            }
        }
    }

    /**
     * Special UTF16 converter which can either always write a BOM
     * or use a specified byte order without one.
     */
    private class UTF16CTBConverter extends JavaCTBConverter
    {
        // Using this constructor, we will always write a BOM
        public UTF16CTBConverter() {
            super(OSFCodeSetRegistry.UTF_16, 2);
        }

        // Using this constructor, we don't use a BOM and use the
        // byte order specified
        public UTF16CTBConverter(boolean littleEndian) {
            super(littleEndian ? 
                  OSFCodeSetRegistry.UTF_16LE : 
                  OSFCodeSetRegistry.UTF_16BE, 
                  2);
        }
    }

    /**
     * Implementation of BTCConverter which uses a sun.io.ByteToCharConverter
     * for the real work.  Handles translation of exceptions to the 
     * appropriate CORBA versions.
     */
    private class JavaBTCConverter extends BTCConverter
    {
        protected ByteToCharConverter btc;
        private char[] buffer;
        private int resultingNumChars;
        private OSFCodeSetRegistry.Entry codeset;

        public JavaBTCConverter(OSFCodeSetRegistry.Entry codeset) {
            
            // Obtain a ByteToCharConverter
            btc = getConverter(codeset.getName());

            this.codeset = codeset;
        }

        public final boolean isFixedWidthEncoding() {
            return codeset.isFixedWidth();
        }

        // Should only be called if isFixedWidthEncoding is true
        public final int getFixedCharWidth() {
            return codeset.getMaxBytesPerChar();
        }

        public final int getNumChars() {
            return resultingNumChars;
        }

        public char[] getChars(byte[] bytes, int offset, int numBytes) {

            // Possible optimization of reading directly from the CDR
            // byte buffer.  The sun.io converter supposedly can handle
            // incremental conversions in which a char is broken across
            // two convert calls.
            //
            // Basic tests didn't show more than a 1 ms increase
            // worst case.  It's less than a factor of 2 increase.
            // Also makes the interface more difficult.

            if (buffer == null || buffer.length < numBytes * btc.getMaxCharsPerByte())
                buffer = new char[numBytes * btc.getMaxCharsPerByte()];

            try {
                btc.reset();

                // WARNING:  The signature for convert is
                // bytes[], offset, endPosition
                // not
                // bytes[], offset, total length
                resultingNumChars = btc.convert(bytes, offset, offset + numBytes,
                                                buffer, 0, buffer.length);

                resultingNumChars += btc.flush(buffer, 0, buffer.length);

                return buffer;

            } catch (MalformedInputException mie) {
                // There were illegal Unicode char pairs
                throw new DATA_CONVERSION(mie.getMessage(),
                                          MinorCodes.BAD_UNICODE_PAIR,
                                          CompletionStatus.COMPLETED_NO);
            } catch (UnknownCharacterException uce) {
                // A character doesn't map to the desired code set.
                // CORBA formal 00-11-03.
                throw new DATA_CONVERSION(uce.getMessage(),
                                          MinorCodes.CHAR_NOT_IN_CODESET,
                                          CompletionStatus.COMPLETED_NO);
            } catch (ConversionBufferFullException cbfe) {
                // If this happens, then the CharToByteConverter was lying
                // about the maximum chars per byte.
                throw new INTERNAL(cbfe.getMessage(),
                                   MinorCodes.BTC_CONVERTER_FAILURE,
                                   CompletionStatus.COMPLETED_NO);
            }
        }

        /**
         * Utility method to find a ByteToCharConverter in the
         * cache or create a new one if necessary.  Throws an
         * INTERNAL if the code set is unknown.
         */
        protected ByteToCharConverter getConverter(String javaCodeSetName) {
         
            try {
                ByteToCharConverter result
                    = cache.getByteToCharConverter(javaCodeSetName);

                if (result == null) {
                    result = ByteToCharConverter.getConverter(javaCodeSetName);
                    cache.setConverter(javaCodeSetName, result);

                    // When substitution mode is false, the converter
                    // will throw exceptions when it encounters illegal
                    // encodings.
                    result.setSubstitutionMode(false);
                }

                return result;

            } catch(UnsupportedEncodingException uee) {
                // This can only happen if one of our entries has
                // an invalid name.
                throw new INTERNAL("Invalid converter name: " + javaCodeSetName);
            }
        }
    }

    /**
     * Special converter for UTF16 since it's required to optionally
     * support a byte order marker while the internal Java converters
     * either require it or require that it isn't there.
     *
     * The solution is to check for the byte order marker, and if we
     * need to do something differently, switch internal converters.
     */
    private class UTF16BTCConverter extends JavaBTCConverter
    {
        private boolean defaultToLittleEndian;
        private boolean converterUsesBOM = true;

        private static final char UTF16_BE_MARKER = (char) 0xfeff;
        private static final char UTF16_LE_MARKER = (char) 0xfffe;

        // When there isn't a byte order marker, used the byte
        // order specified.
        public UTF16BTCConverter(boolean defaultToLittleEndian) {
            super(OSFCodeSetRegistry.UTF_16);

            this.defaultToLittleEndian = defaultToLittleEndian;
        }

        public char[] getChars(byte[] bytes, int offset, int numBytes) {

            if (hasUTF16ByteOrderMarker(bytes, offset, numBytes)) {
                if (!converterUsesBOM)
                    switchToConverter(OSFCodeSetRegistry.UTF_16);

                converterUsesBOM = true;

                return super.getChars(bytes, offset, numBytes);
            } else {
                if (converterUsesBOM) {
                    if (defaultToLittleEndian)
                        switchToConverter(OSFCodeSetRegistry.UTF_16LE);
                    else
                        switchToConverter(OSFCodeSetRegistry.UTF_16BE);

                    converterUsesBOM = false;
                }

                return super.getChars(bytes, offset, numBytes);
            }
        }

        /**
         * Utility method for determining if a UTF-16 byte order marker is present.
         */
        private boolean hasUTF16ByteOrderMarker(byte[] array, int offset, int length) {
            // If there aren't enough bytes to represent the marker and data,
            // return false.
            if (length >= 4) {

                int b1 = array[offset] & 0x00FF;
                int b2 = array[offset + 1] & 0x00FF;

                char marker = (char)((b1 << 8) | (b2 << 0));
                
                return (marker == UTF16_BE_MARKER || marker == UTF16_LE_MARKER);
            } else
                return false;
        }

        /**
         * The current solution for dealing with UTF-16 in CORBA
         * is that if our sun.io converter requires byte order markers,
         * and then we see a CORBA wstring/wchar without them, we 
         * switch to the sun.io converter that doesn't require them.
         */
        private void switchToConverter(OSFCodeSetRegistry.Entry newCodeSet) {

            // Use the getConverter method from our superclass.
            btc = getConverter(newCodeSet.getName());
        }
    }

    /**
     * CTB converter factory for single byte or variable length encodings.
     */
    public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry codeset) {
        int alignment = (!codeset.isFixedWidth() ?
                         1 :
                         codeset.getMaxBytesPerChar());
            
        return new JavaCTBConverter(codeset, alignment);
    }

    /**
     * CTB converter factory for multibyte (mainly fixed) encodings.
     *
     * Because of the awkwardness with byte order markers and the possibility of 
     * using UCS-2, you must specify both the endianness of the stream as well as 
     * whether or not to use byte order markers if applicable.  UCS-2 has no byte 
     * order markers.  UTF-16 has optional markers.
     *
     * If you select useByteOrderMarkers, there is no guarantee that the encoding
     * will use the endianness specified.
     *
     */
    public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry codeset,
                                        boolean littleEndian,
                                        boolean useByteOrderMarkers) {

        // UCS2 doesn't have byte order markers, and we're encoding it
        // as UTF-16 since UCS2 isn't available in all Java platforms.
        // They should be identical with only minor differences in
        // negative cases.
        if (codeset == OSFCodeSetRegistry.UCS_2)
            return new UTF16CTBConverter(littleEndian);

        // We can write UTF-16 with or without a byte order marker.
        if (codeset == OSFCodeSetRegistry.UTF_16) {
            if (useByteOrderMarkers)
                return new UTF16CTBConverter();
            else
                return new UTF16CTBConverter(littleEndian);
        }

        // Everything else uses the generic JavaCTBConverter.
        //
        // Variable width encodings are aligned on 1 byte boundaries.
        // A fixed width encoding with a max. of 4 bytes/char should
        // align on a 4 byte boundary.  Note that UTF-16 is a special
        // case because of the optional byte order marker, so it's
        // handled above.
        //
        // This doesn't matter for GIOP 1.2 wchars and wstrings
        // since the encoded bytes are treated as an encapsulation.
        int alignment = (!codeset.isFixedWidth() ?
                         1 :
                         codeset.getMaxBytesPerChar());
        
        return new JavaCTBConverter(codeset, alignment);
    }

    /**
     * BTCConverter factory for single byte or variable width encodings.
     */
    public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry codeset) {
        return new JavaBTCConverter(codeset);
    }

    /**
     * BTCConverter factory for fixed width multibyte encodings.
     */
    public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry codeset,
                                        boolean defaultToLittleEndian) {

        if (codeset == OSFCodeSetRegistry.UTF_16 ||
            codeset == OSFCodeSetRegistry.UCS_2) {

            return new UTF16BTCConverter(defaultToLittleEndian);
        } else {
            return new JavaBTCConverter(codeset);
        }
    }

    /** 
     * Follows the code set negotiation algorithm in CORBA formal 99-10-07 13.7.2.
     *
     * Returns the proper negotiated OSF character encoding number or
     * CodeSetConversion.FALLBACK_CODESET.
     */
    private int selectEncoding(CodeSetComponentInfo.CodeSetComponent client,
                               CodeSetComponentInfo.CodeSetComponent server) {

        // A "null" value for the server's nativeCodeSet means that
        // the server desired not to indicate one.  We'll take that
        // to mean that it wants the first thing in its conversion list.
        // If it's conversion list is empty, too, then use the fallback
        // codeset.
        int serverNative = server.nativeCodeSet;

        if (serverNative == 0) {
            if (server.conversionCodeSets.length > 0)
                serverNative = server.conversionCodeSets[0];
            else
                return CodeSetConversion.FALLBACK_CODESET;
        }

        if (client.nativeCodeSet == serverNative) {
            // Best case -- client and server don't have to convert
            return serverNative;
        }

        // Is this client capable of converting to the server's
        // native code set?
        for (int i = 0; i < client.conversionCodeSets.length; i++) {
            if (serverNative == client.conversionCodeSets[i]) {
                // The client will convert to the server's
                // native code set.
                return serverNative;
            }
        }

        // Is the server capable of converting to the client's
        // native code set?
        for (int i = 0; i < server.conversionCodeSets.length; i++) {
            if (client.nativeCodeSet == server.conversionCodeSets[i]) {
                // The server will convert to the client's
                // native code set.
                return client.nativeCodeSet;
            }
        }

        // See if there are any code sets that both the server and client
        // support (giving preference to the server).  The order
        // of conversion sets is from most to least desired.
        for (int i = 0; i < server.conversionCodeSets.length; i++) {
            for (int y = 0; y < client.conversionCodeSets.length; y++) {
                if (server.conversionCodeSets[i] == client.conversionCodeSets[y]) {
                    return server.conversionCodeSets[i];
                }
            }
        }

        // Before using the fallback codesets, the spec calls for a
        // compatibility check on the native code sets.  It doesn't make
        // sense because loss free communication is always possible with
        // UTF8 and UTF16, the fall back code sets.  It's also a lot
        // of work to implement.  In the case of incompatibility, the
        // spec says to throw a CODESET_INCOMPATIBLE exception.
        
        // Use the fallback
        return CodeSetConversion.FALLBACK_CODESET;
    }

    /**
     * Perform the code set negotiation algorithm and come up with
     * the two encodings to use.
     */
    public CodeSetComponentInfo.CodeSetContext negotiate(CodeSetComponentInfo client,
                                                         CodeSetComponentInfo server) {
        int charData
            = selectEncoding(client.getCharComponent(),
                             server.getCharComponent());

        if (charData == CodeSetConversion.FALLBACK_CODESET) {
            charData = OSFCodeSetRegistry.UTF_8.getNumber();
        }

        int wcharData
            = selectEncoding(client.getWCharComponent(),
                             server.getWCharComponent());

        if (wcharData == CodeSetConversion.FALLBACK_CODESET) {
            wcharData = OSFCodeSetRegistry.UTF_16.getNumber();
        }

        return new CodeSetComponentInfo.CodeSetContext(charData,
                                                       wcharData);
    }

    // No one should instantiate a CodeSetConversion but the singleton
    // instance method
    private CodeSetConversion() {}

    // initialize-on-demand holder
    private static class CodeSetConversionHolder {
	static final CodeSetConversion csc = new CodeSetConversion() ;
    }

    /**
     * CodeSetConversion is a singleton, and this is the access point.
     */
    public final static CodeSetConversion impl() {
	return CodeSetConversionHolder.csc ;
    }

    // Singleton instance
    private static CodeSetConversion implementation;

    // Number used internally to indicate the fallback code
    // set.
    private static final int FALLBACK_CODESET = 0;

    // Provides a thread local cache for the sun.io
    // converters.
    private CodeSetCache cache = new CodeSetCache();
}
