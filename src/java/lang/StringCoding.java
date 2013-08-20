/*
 * @(#)StringCoding.java	1.15	05/03/03
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnsupportedCharsetException;
import sun.io.ByteToCharConverter;
import sun.io.CharToByteConverter;
import sun.io.Converters;
import sun.misc.MessageUtils;
import sun.nio.cs.HistoricallyNamedCharset;


/**
 * Utility class for string encoding and decoding.
 */

class StringCoding {

    private StringCoding() { }

    /* The cached coders for each thread
     */
    private static ThreadLocal decoder = new ThreadLocal();
    private static ThreadLocal encoder = new ThreadLocal();

    private static boolean warnUnsupportedCharset = true;

    private static Object deref(ThreadLocal tl) {
        SoftReference sr = (SoftReference)tl.get();
	if (sr == null)
	    return null;
	return sr.get();
    }

    private static void set(ThreadLocal tl, Object ob) {
	tl.set(new SoftReference(ob));
    }

    // Trim the given byte array to the given length
    //
    private static byte[] trim(byte[] ba, int len) {
	if (len == ba.length)
	    return ba;
	byte[] tba = new byte[len];
	System.arraycopy(ba, 0, tba, 0, len);
	return tba;
    }

    // Trim the given char array to the given length
    //
    private static char[] trim(char[] ca, int len) {
	if (len == ca.length)
	    return ca;
	char[] tca = new char[len];
	System.arraycopy(ca, 0, tca, 0, len);
	return tca;
    }

    private static int scale(int len, float expansionFactor) {
      // We need to perform double, not float, arithmetic; otherwise
      // we lose low order bits when len is larger than 2**24.
      return (int)(len * (double)expansionFactor);
    }

    private static Charset lookupCharset(String csn) {
	if (Charset.isSupported(csn)) {
	    try {
		return Charset.forName(csn);
	    } catch (UnsupportedCharsetException x) {
		throw new Error(x);
	    }
	}
	return null;
    }

    private static void warnUnsupportedCharset(String csn) {
	if (warnUnsupportedCharset) {
	    // Use sun.misc.MessageUtils rather than the Logging API or
	    // System.err since this method may be called during VM
	    // initialization before either is available.
	    MessageUtils.err("WARNING: Default charset " + csn +
			     " not supported, using ISO-8859-1 instead");
	    warnUnsupportedCharset = false;
	}
    }


    // -- Decoding --

    // Encapsulates either a ByteToCharConverter or a CharsetDecoder
    //
    private static abstract class StringDecoder {
	private final String requestedCharsetName;
	protected StringDecoder(String requestedCharsetName) {
	    this.requestedCharsetName = requestedCharsetName;
	}
	final String requestedCharsetName() {
	    return requestedCharsetName;
	}
	abstract String charsetName();
	abstract char[] decode(byte[] ba, int off, int len);
    }

    // A string decoder based upon a ByteToCharConverter
    //
    private static class ConverterSD
	extends StringDecoder
    {
	private ByteToCharConverter btc;

	private ConverterSD(ByteToCharConverter btc, String rcn) {
	    super(rcn);
	    this.btc = btc;
	}

	String charsetName() {
	    return btc.getCharacterEncoding();
	}

	char[] decode(byte[] ba, int off, int len) {
	    int en = scale(len, btc.getMaxCharsPerByte());
	    char[] ca = new char[en];
	    if (len == 0)
		return ca;
	    btc.reset();
	    int n = 0;
	    try {
		n = btc.convert(ba, off, off + len, ca, 0, en);
		n += btc.flush(ca, btc.nextCharIndex(), en);
	    } catch (CharConversionException x) {
		// Yes, this is what we've always done
		n = btc.nextCharIndex();
	    }
	    return trim(ca, n);
	}

    }

    // A string decoder based upon a CharsetDecoder
    //
    private static class CharsetSD
	extends StringDecoder
    {
	private final Charset cs;
	private final CharsetDecoder cd;

	private CharsetSD(Charset cs, String rcn) {
	    super(rcn);
	    this.cs = cs;
	    this.cd = cs.newDecoder()
		.onMalformedInput(CodingErrorAction.REPLACE)
		.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}

	String charsetName() {
	    if (cs instanceof HistoricallyNamedCharset)
		return ((HistoricallyNamedCharset)cs).historicalName();
	    return cs.name();
	}

	char[] decode(byte[] ba, int off, int len) {
	    int en = scale(len, cd.maxCharsPerByte());
	    char[] ca = new char[en];
	    if (len == 0)
		return ca;
	    cd.reset();
	    ByteBuffer bb = ByteBuffer.wrap(ba, off, len);
	    CharBuffer cb = CharBuffer.wrap(ca);
	    try {
		CoderResult cr = cd.decode(bb, cb, true);
		if (!cr.isUnderflow())
		    cr.throwException();
		cr = cd.flush(cb);
		if (!cr.isUnderflow())
		    cr.throwException();
	    } catch (CharacterCodingException x) {
		// Substitution is always enabled,
		// so this shouldn't happen
		throw new Error(x);
	    }
	    return trim(ca, cb.position());
	}

    }

    static char[] decode(String charsetName, byte[] ba, int off, int len)
	throws UnsupportedEncodingException
    {
	StringDecoder sd = (StringDecoder)deref(decoder);
	String csn = (charsetName == null) ? "ISO-8859-1" : charsetName;
	if ((sd == null) || !(csn.equals(sd.requestedCharsetName())
			      || csn.equals(sd.charsetName()))) {
	    sd = null;
	    try {
		Charset cs = lookupCharset(csn);
		if (cs != null)
		    sd = new CharsetSD(cs, csn);
		else
		    sd = null;
	    } catch (IllegalCharsetNameException x) {
		// FALL THROUGH to ByteToCharConverter, for compatibility
	    }
	    if (sd == null)
		sd = new ConverterSD(ByteToCharConverter.getConverter(csn),
				     csn);
	    set(decoder, sd);
	}
	return sd.decode(ba, off, len);
    }

    static char[] decode(byte[] ba, int off, int len) {
	String csn = Converters.getDefaultEncodingName();
	try {
	    return decode(csn, ba, off, len);
	} catch (UnsupportedEncodingException x) {
	    Converters.resetDefaultEncodingName();
	    warnUnsupportedCharset(csn);
	}
	try {
	    return decode("ISO-8859-1", ba, off, len);
	} catch (UnsupportedEncodingException x) {
	    // If this code is hit during VM initialization, MessageUtils is
	    // the only way we will be able to get any kind of error message.
	    MessageUtils.err("ISO-8859-1 charset not available: "
			     + x.toString());
	    // If we can not find ISO-8859-1 (a required encoding) then things
	    // are seriously wrong with the installation.
	    System.exit(1);
	    return null;
	}
    }




    // -- Encoding --

    // Encapsulates either a CharToByteConverter or a CharsetEncoder
    //
    private static abstract class StringEncoder {
	private final String requestedCharsetName;
	protected StringEncoder(String requestedCharsetName) {
	    this.requestedCharsetName = requestedCharsetName;
	}
	final String requestedCharsetName() {
	    return requestedCharsetName;
	}
	abstract String charsetName();
	abstract byte[] encode(char[] cs, int off, int len);
    }

    // A string encoder based upon a CharToByteConverter
    //
    private static class ConverterSE
	extends StringEncoder
    {
	private CharToByteConverter ctb;

	private ConverterSE(CharToByteConverter ctb, String rcn) {
	    super(rcn);
	    this.ctb = ctb;
	}

	String charsetName() {
	    return ctb.getCharacterEncoding();
	}

	byte[] encode(char[] ca, int off, int len) {
	    int en = scale(len, ctb.getMaxBytesPerChar());
	    byte[] ba = new byte[en];
	    if (len == 0)
		return ba;

	    ctb.reset();
	    int n;
	    try {
		n = ctb.convertAny(ca, off, (off + len),
				   ba, 0, en);
		n += ctb.flushAny(ba, ctb.nextByteIndex(), en);
	    } catch (CharConversionException x) {
		throw new Error("Converter malfunction: " +
				ctb.getClass().getName(),
				x);
	    }
	    return trim(ba, n);
	}

    }

    // A string encoder based upon a CharsetEncoder
    //
    private static class CharsetSE
	extends StringEncoder
    {
	private Charset cs;
	private CharsetEncoder ce;

	private CharsetSE(Charset cs, String rcn) {
	    super(rcn);
	    this.cs = cs;
	    this.ce = cs.newEncoder()
		.onMalformedInput(CodingErrorAction.REPLACE)
		.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}

	String charsetName() {
	    if (cs instanceof HistoricallyNamedCharset)
		return ((HistoricallyNamedCharset)cs).historicalName();
	    return cs.name();
	}

	byte[] encode(char[] ca, int off, int len) {
	    int en = scale(len, ce.maxBytesPerChar());
	    byte[] ba = new byte[en];
	    if (len == 0)
		return ba;

	    ce.reset();
	    ByteBuffer bb = ByteBuffer.wrap(ba);
	    CharBuffer cb = CharBuffer.wrap(ca, off, len);
	    try {
		CoderResult cr = ce.encode(cb, bb, true);
		if (!cr.isUnderflow())
		    cr.throwException();
		cr = ce.flush(bb);
		if (!cr.isUnderflow())
		    cr.throwException();
	    } catch (CharacterCodingException x) {
		// Substitution is always enabled,
		// so this shouldn't happen
		throw new Error(x);
	    }
	    return trim(ba, bb.position());
	}

    }

    static byte[] encode(String charsetName, char[] ca, int off, int len)
	throws UnsupportedEncodingException
    {
	StringEncoder se = (StringEncoder)deref(encoder);
	String csn = (charsetName == null) ? "ISO-8859-1" : charsetName;
	if ((se == null) || !(csn.equals(se.requestedCharsetName())
			      || csn.equals(se.charsetName()))) {
	    se = null;
	    try {
		Charset cs = lookupCharset(csn);
		if (cs != null)
		    se = new CharsetSE(cs, csn);
	    } catch (IllegalCharsetNameException x) {
		// FALL THROUGH to CharToByteConverter, for compatibility
	    }
	    if (se == null)
		se = new ConverterSE(CharToByteConverter.getConverter(csn),
				     csn);
	    set(encoder, se);
	}
	return se.encode(ca, off, len);
    }

    static byte[] encode(char[] ca, int off, int len) {
	String csn = Converters.getDefaultEncodingName();
	try {
	    return encode(csn, ca, off, len);
	} catch (UnsupportedEncodingException x) {
	    Converters.resetDefaultEncodingName();
	    warnUnsupportedCharset(csn);
	}
	try {
	    return encode("ISO-8859-1", ca, off, len);
	} catch (UnsupportedEncodingException x) {
	    // If this code is hit during VM initialization, MessageUtils is
	    // the only way we will be able to get any kind of error message.
	    MessageUtils.err("ISO-8859-1 charset not available: "
			     + x.toString());
	    // If we can not find ISO-8859-1 (a required encoding) then things
	    // are seriously wrong with the installation.
	    System.exit(1);
	    return null;
	}
    }

}
