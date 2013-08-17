/*
 * @(#)String.java	1.87 99/01/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

import java.util.Hashtable;
import java.util.Locale;
import sun.io.ByteToCharConverter;
import sun.io.CharToByteConverter;
import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

/**
 * The <code>String</code> class represents character strings. All 
 * string literals in Java programs, such as <code>"abc"</code>, are 
 * implemented as instances of this class. 
 * <p>
 * Strings are constant; their values cannot be changed after they 
 * are created. String buffers support mutable strings.
 * Because String objects are immutable they can be shared. For example:
 * <p><blockquote><pre>
 *     String str = "abc";
 * </pre></blockquote><p>
 * is equivalent to:
 * <p><blockquote><pre>
 *     char data[] = {'a', 'b', 'c'};
 *     String str = new String(data);
 * </pre></blockquote><p>
 * Here are some more examples of how strings can be used:
 * <p><blockquote><pre>
 *     System.out.println("abc");
 *     String cde = "cde";
 *     System.out.println("abc" + cde);
 *     String c = "abc".substring(2,3);
 *     String d = cde.substring(1, 2);
 * </pre></blockquote>
 * <p>
 * The class <code>String</code> includes methods for examining 
 * individual characters of the sequence, for comparing strings, for 
 * searching strings, for extracting substrings, and for creating a 
 * copy of a string with all characters translated to uppercase or to 
 * lowercase. 
 * <p>
 * The Java language provides special support for the string 
 * concatentation operator (&nbsp;+&nbsp;), and for conversion of 
 * other objects to strings. String concatenation is implemented 
 * through the <code>StringBuffer</code> class and its 
 * <code>append</code> method.
 * String conversions are implemented through the method 
 * <code>toString</code>, defined by <code>Object</code> and 
 * inherited by all classes in Java. For additional information on 
 * string concatenation and conversion, see Gosling, Joy, and Steele, 
 * <i>The Java Language Specification</i>. 
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.87, 01/22/99
 * @see     java.lang.Object#toString()
 * @see     java.lang.StringBuffer
 * @see     java.lang.StringBuffer#append(boolean)
 * @see     java.lang.StringBuffer#append(char)
 * @see     java.lang.StringBuffer#append(char[])
 * @see     java.lang.StringBuffer#append(char[], int, int)
 * @see     java.lang.StringBuffer#append(double)
 * @see     java.lang.StringBuffer#append(float)
 * @see     java.lang.StringBuffer#append(int)
 * @see     java.lang.StringBuffer#append(long)
 * @see     java.lang.StringBuffer#append(java.lang.Object)
 * @see     java.lang.StringBuffer#append(java.lang.String)
 * @since   JDK1.0
 */
public final
class String implements java.io.Serializable {
    /** The value is used for character storage. */
    private char value[];

    /** The offset is the first index of the storage that is used. */
    private int offset;

    /** The count is the number of characters in the String. */
    private int count;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * Allocates a new <code>String</code> containing no characters. 
     */
    public String() {
	value = new char[0];
    }

    /**
     * Allocates a new string that contains the same sequence of 
     * characters as the string argument. 
     *
     * @param   value   a <code>String</code>.
     */
    public String(String value) {
	count = value.length();
	this.value = new char[count];
	value.getChars(0, count, this.value, 0);
    }

    /**
     * Allocates a new <code>String</code> so that it represents the 
     * sequence of characters currently contained in the character array 
     * argument. 
     *
     * @param  value   the initial value of the string.
     */
    public String(char value[]) {
	this.count = value.length;
	this.value = new char[count];
	System.arraycopy(value, 0, this.value, 0, count);
    }

    /**
     * Allocates a new <code>String</code> that contains characters from 
     * a subarray of the character array argument. The <code>offset</code> 
     * argument is the index of the first character of the subarray and 
     * the <code>count</code> argument specifies the length of the 
     * subarray. 
     *
     * @param      value    array that is the source of characters.
     * @param      offset   the initial offset.
     * @param      count    the length.
     * @exception  StringIndexOutOfBoundsException  if the <code>offset</code>
     *               and <code>count</code> arguments index characters outside
     *               the bounds of the <code>value</code> array.
     */
    public String(char value[], int offset, int count) {
	if (offset < 0) {
	    throw new StringIndexOutOfBoundsException(offset);
	}
	if (count < 0) {
	    throw new StringIndexOutOfBoundsException(count);
	}
	// Note: offset or count might be near -1>>>1.
	if (offset > value.length - count) {
	    throw new StringIndexOutOfBoundsException(offset + count);
	}

	this.value = new char[count];
	this.count = count;
	System.arraycopy(value, offset, this.value, 0, count);
    }

    /**
     * Allocates a new <code>String</code> constructed from a subarray 
     * of an array of 8-bit integer values. 
     * <p>
     * The <code>offset</code> argument is the index of the first byte 
     * of the subarray, and the <code>count</code> argument specifies the 
     * length of the subarray. 
     * <p>
     * Each <code>byte</code> in the subarray is converted to a 
     * <code>char</code> as specified in the method above. 
     *
     * @deprecated This method does not properly convert bytes into characters.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * <code>String</code> constructors that take a character-encoding name or
     * that use the platform's default encoding.
     *
     * @param      ascii     the bytes to be converted to characters.
     * @param      hibyte    the top 8 bits of each 16-bit Unicode character.
     * @param      offset    the initial offset.
     * @param      count     the length.
     * @exception  StringIndexOutOfBoundsException  if the <code>offset</code>
     *               or <code>count</code> argument is invalid.
     * @see        java.lang.String#String(byte[], int)
     * @see        java.lang.String#String(byte[], int, int, java.lang.String)
     * @see        java.lang.String#String(byte[], int, int)
     * @see        java.lang.String#String(byte[], java.lang.String)
     * @see        java.lang.String#String(byte[])
     */
    public String(byte ascii[], int hibyte, int offset, int count) {
	if (offset < 0) {
	    throw new StringIndexOutOfBoundsException(offset);
	}
	if (count < 0) {
	    throw new StringIndexOutOfBoundsException(count);
	}
	// Note: offset or count might be near -1>>>1.
	if (offset > ascii.length - count) {
	    throw new StringIndexOutOfBoundsException(offset + count);
	}

	char value[] = new char[count];
	this.count = count;
	this.value = value;

	if (hibyte == 0) {
	    for (int i = count ; i-- > 0 ;) {
		value[i] = (char) (ascii[i + offset] & 0xff);
	    }
	} else {
	    hibyte <<= 8;
	    for (int i = count ; i-- > 0 ;) {
		value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
	    }
	}
    }

    /**
     * Allocates a new <code>String</code> containing characters 
     * constructed from an array of 8-bit integer values. Each character 
     * <i>c</i>in the resulting string is constructed from the 
     * corresponding component <i>b</i> in the byte array such that:
     * <p><blockquote><pre>
     *     <b><i>c</i></b> == (char)(((hibyte &amp; 0xff) &lt;&lt; 8)
     *                         | (<b><i>b</i></b> &amp; 0xff))
     * </pre></blockquote>
     *
     * @deprecated This method does not properly convert bytes into characters.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * <code>String</code> constructors that take a character-encoding name or
     * that use the platform's default encoding.
     *
     * @param      ascii    the bytes to be converted to characters.
     * @param      hibyte   the top 8 bits of each 16-bit Unicode character.
     * @see        java.lang.String#String(byte[], int, int, java.lang.String)
     * @see        java.lang.String#String(byte[], int, int)
     * @see        java.lang.String#String(byte[], java.lang.String)
     * @see        java.lang.String#String(byte[])
     */
    public String(byte ascii[], int hibyte) {
	this(ascii, hibyte, 0, ascii.length);
    }

    /**
     * Construct a new <code>String</code> by converting the specified
     * subarray of bytes using the specified character-encoding converter.  The
     * length of the new <code>String</code> is a function of the encoding, and
     * hence may not be equal to the length of the subarray.
     *
     * @param  bytes   The bytes to be converted into characters
     * @param  offset  Index of the first byte to convert
     * @param  length  Number of bytes to convert
     * @param  btc     A ByteToCharConverter
     */
    private String(byte bytes[], int offset, int length,
		   ByteToCharConverter btc)
    {
	int estCount = btc.getMaxCharsPerByte() * length;
	value = new char[estCount];

        try {
	    count = btc.convert(bytes, offset, offset+length,
				value, 0, estCount);
	    count += btc.flush(value, btc.nextCharIndex(), estCount);
	}
	catch (CharConversionException x) {
	    count = btc.nextCharIndex();
	}

	if (count < estCount) {
	    // A multi-byte format was used:  Trim the char array.
	    char[] trimValue = new char[count];
	    System.arraycopy(value, 0, trimValue, 0, count);
	    value = trimValue;
	}
    }

    /**
     * Construct a new <code>String</code> by converting the specified
     * subarray of bytes using the specified character encoding.  The length of
     * the new <code>String</code> is a function of the encoding, and hence may
     * not be equal to the length of the subarray.
     *
     * @param  bytes   The bytes to be converted into characters
     * @param  offset  Index of the first byte to convert
     * @param  length  Number of bytes to convert
     * @param  enc     The name of a character encoding
     *
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     * @since      JDK1.1
     */
    public String(byte bytes[], int offset, int length, String enc)
	throws UnsupportedEncodingException
    {
	this(bytes, offset, length, ByteToCharConverter.getConverter(enc));
    }

    /**
     * Construct a new <code>String</code> by converting the specified array
     * of bytes using the specified character encoding.  The length of the new
     * <code>String</code> is a function of the encoding, and hence may not be
     * equal to the length of the byte array.
     *
     * @param  bytes   The bytes to be converted into characters
     * @param  enc     A character-encoding name
     *
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     * @since      JDK1.1
     */
    public String(byte bytes[], String enc)
	throws UnsupportedEncodingException
    {
	this(bytes, 0, bytes.length, enc);
    }

    /**
     * Construct a new <code>String</code> by converting the specified
     * subarray of bytes using the platform's default character encoding.  The
     * length of the new <code>String</code> is a function of the encoding, and
     * hence may not be equal to the length of the subarray.
     *
     * @param  bytes   The bytes to be converted into characters
     * @param  offset  Index of the first byte to convert
     * @param  length  Number of bytes to convert
     * @since  JDK1.1
     */
    public String(byte bytes[], int offset, int length) {
	this(bytes, offset, length, ByteToCharConverter.getDefault());
    }

    /**
     * Construct a new <code>String</code> by converting the specified array
     * of bytes using the platform's default character encoding.  The length of
     * the new <code>String</code> is a function of the encoding, and hence may
     * not be equal to the length of the byte array.
     *
     * @param  bytes   The bytes to be converted into characters
     * @since  JDK1.1
     */
    public String(byte bytes[]) {
	this(bytes, 0, bytes.length, ByteToCharConverter.getDefault());
    }

    /**
     * Allocates a new string that contains the sequence of characters 
     * currently contained in the string buffer argument. 
     *
     * @param   buffer   a <code>StringBuffer</code>.
     */
    public String (StringBuffer buffer) { 
	synchronized(buffer) { 
	    buffer.setShared();
	    this.value = buffer.getValue();
	    this.offset = 0;
	    this.count = buffer.length();
	}
    }
    
    // Private constructor which shares value array for speed.
    private String(int offset, int count, char value[]) {
	this.value = value;
	this.offset = offset;
	this.count = count;
    }

    /**
     * Returns the length of this string.
     * The length is equal to the number of 16-bit
     * Unicode characters in the string.
     *
     * @return  the length of the sequence of characters represented by this
     *          object.
     */
    public int length() {
	return count;
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from <code>0</code> to <code>length() - 1</code>.
     *
     * @param      index   the index of the character.
     * @return     the character at the specified index of this string.
     *             The first character is at index <code>0</code>.
     * @exception  StringIndexOutOfBoundsException  if the index is out of
     *               range.
     */
    public char charAt(int index) {
	if ((index < 0) || (index >= count)) {
	    throw new StringIndexOutOfBoundsException(index);
	}
	return value[index + offset];
    }

    /**
     * Copies characters from this string into the destination character array. 
     * <p>
     * The first character to be copied is at index <code>srcBegin</code>; 
     * the last character to be copied is at index <code>srcEnd-1</code> 
     * (thus the total number of characters to be copied is 
     * <code>srcEnd-srcBegin</code>). The characters are copied into the 
     * subarray of <code>dst</code> starting at index <code>dstBegin</code> 
     * and ending at index: 
     * <p><blockquote><pre>
     *     dstbegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param      srcBegin   index of the first character in the string
     *                        to copy.
     * @param      srcEnd     index after the last character in the string
     *                        to copy.
     * @param      dst        the destination array.
     * @param      dstBegin   the start offset in the destination array.
     * @exception StringIndexOutOfBoundsException If srcBegin or srcEnd is out 
     *              of range, or if srcBegin is greater than the srcEnd.
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	if (srcBegin < 0) {
	    throw new StringIndexOutOfBoundsException(srcBegin);
	} 
	if (srcEnd > count) {
	    throw new StringIndexOutOfBoundsException(srcEnd);
	} 
	if (srcBegin > srcEnd) {
	    throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
	}
	System.arraycopy(value, offset + srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * Copies characters from this string into the destination byte 
     * array. Each byte receives the 8 low-order bits of the 
     * corresponding character. 
     * <p>
     * The first character to be copied is at index <code>srcBegin</code>; 
     * the last character to be copied is at index <code>srcEnd-1</code>. 
     * The total number of characters to be copied is 
     * <code>srcEnd-srcBegin</code>. The characters, converted to bytes, 
     * are copied into the subarray of <code>dst</code> starting at index 
     * <code>dstBegin</code> and ending at index: 
     * <p><blockquote><pre>
     *     dstbegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @deprecated This method does not properly convert characters into bytes.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * <code>getBytes(String enc)</code> method, which takes a
     * character-encoding name, or the <code>getBytes()</code> method, which
     * uses the platform's default encoding.
     *
     * @param      srcBegin   index of the first character in the string
     *                        to copy.
     * @param      srcEnd     index after the last character in the string
     *                        to copy.
     * @param      dst        the destination array.
     * @param      dstBegin   the start offset in the destination array.
     * @exception StringIndexOutOfBoundsException  if srcBegin or srcEnd is out 
     *              of range, or if srcBegin is greater than srcEnd.
     */
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
	if (srcBegin < 0) {
	    throw new StringIndexOutOfBoundsException(srcBegin);
	} 
	if (srcEnd > count) {
	    throw new StringIndexOutOfBoundsException(srcEnd);
	} 
	if (srcBegin > srcEnd) {
	    throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
	}
 	int j = dstBegin;
 	int n = offset + srcEnd;
 	int i = offset + srcBegin;
	char[] val = value;   /* avoid getfield opcode */

 	while (i < n) {
 	    dst[j++] = (byte)val[i++];
 	}
    }

    /**
     * Apply the specified character-encoding converter to this String,
     * storing the resulting bytes into a new byte array.
     *
     * @param  ctb  A CharToByteConverter
     * @return      The resultant byte array
     */
    private byte[] getBytes(CharToByteConverter ctb) {
	ctb.reset();
	int estLength = ctb.getMaxBytesPerChar() * count;
	byte[] result = new byte[estLength];
	int length;

	try {
	    length = ctb.convert(value, offset, offset + count,
				 result, 0, estLength);
	    length += ctb.flush(result, ctb.nextByteIndex(), estLength);
	} catch (CharConversionException e) {
	    length = ctb.nextByteIndex();
	}

	if (length < estLength) {
	    // A short format was used:  Trim the byte array.
	    byte[] trimResult = new byte[length];
	    System.arraycopy(result, 0, trimResult, 0, length);
	    return trimResult;
	}
	else {
	    return result;
	}
    }

    /**
     * Convert this <code>String</code> into bytes according to the specified
     * character encoding, storing the result into a new byte array.
     *
     * @param  enc  A character-encoding name
     * @return      The resultant byte array
     *
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     * @since      JDK1.1
     */
    public byte[] getBytes(String enc)
	throws UnsupportedEncodingException
    {
	return getBytes(CharToByteConverter.getConverter(enc));
    }

    /**
     * Convert this <code>String</code> into bytes according to the platform's
     * default character encoding, storing the result into a new byte array.
     *
     * @return  the resultant byte array.
     * @since   JDK1.1
     */
    public byte[] getBytes() {
	return getBytes(CharToByteConverter.getDefault());
    }

    /**
     * Compares this string to the specified object.
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>String</code> object that represents 
     * the same sequence of characters as this object. 
     *
     * @param   anObject   the object to compare this <code>String</code>
     *                     against.
     * @return  <code>true</code> if the <code>String </code>are equal;
     *          <code>false</code> otherwise.
     * @see     java.lang.String#compareTo(java.lang.String)
     * @see     java.lang.String#equalsIgnoreCase(java.lang.String)
     */
    public boolean equals(Object anObject) {
	if (this == anObject) {
	    return true;
	}
	if ((anObject != null) && (anObject instanceof String)) {
	    String anotherString = (String)anObject;
	    int n = count;
	    if (n == anotherString.count) {
		char v1[] = value;
		char v2[] = anotherString.value;
		int i = offset;
		int j = anotherString.offset;
		while (n-- != 0) {
		    if (v1[i++] != v2[j++]) {
			return false;
		    }
		}
		return true;
	    }
	}
	return false;
    }

    /**
     * Compares this String to another object.
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>String</code> object that represents 
     * the same sequence of characters as this object, where case is ignored. 
     * <p>
     * Two characters are considered the same, ignoring case, if at 
     * least one of the following is true: 
     * <ul>
     * <li>The two characters are the same (as compared by the <code>==</code> 
     *     operator). 
     * <li>Applying the method <code>Character.toUppercase</code> to each 
     *     character produces the same result. 
     * <li>Applying the method <code>Character.toLowercase</code> to each 
     *     character produces the same result. 
     * </ul>
     * <p>
     * Two sequences of characters are the same, ignoring case, if the 
     * sequences have the same length and corresponding characters are 
     * the same, ignoring case. 
     *
     * @param   anotherString   the <code>String</code> to compare this
     *                          <code>String</code> against.
     * @return  <code>true</code> if the <code>String</code>s are equal,
     *          ignoring case; <code>false</code> otherwise.
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.Character#toUpperCase(char)
     */
    public boolean equalsIgnoreCase(String anotherString) {
	return (anotherString != null) && (anotherString.count == count) &&
		regionMatches(true, 0, anotherString, 0, count);
    }

    /**
     * Compares two strings lexicographically. 
     * The comparison is based on the Unicode value of each character in
     * the strings. 
     *
     * @param   anotherString   the <code>String</code> to be compared.
     * @return  the value <code>0</code> if the argument string is equal to
     *          this string; a value less than <code>0</code> if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than <code>0</code> if this string is
     *          lexicographically greater than the string argument.
     */
    public int compareTo(String anotherString) {
	int len1 = count;
	int len2 = anotherString.count;
	int n = Math.min(len1, len2);
	char v1[] = value;
	char v2[] = anotherString.value;
	int i = offset;
	int j = anotherString.offset;

	while (n-- != 0) {
	    char c1 = v1[i++];
	    char c2 = v2[j++];
	    if (c1 != c2) {
		return c1 - c2;
	    }
	}
	return len1 - len2;
    }

    /**
     * Tests if two string regions are equal. 
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or 
     * if <code>toffset</code>+<code>length</code> is greater than the 
     * length of this string, or if 
     * <code>ooffset</code>+<code>length</code> is greater than the 
     * length of the string argument, then this method returns 
     * <code>false</code>. 
     *
     * @param   toffset   the starting offset of the subregion in this string.
     * @param   other     the string argument.
     * @param   ooffset   the starting offset of the subregion in the string
     *                    argument.
     * @param   len       the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          exactly matches the specified subregion of the string argument;
     *          <code>false</code> otherwise.
     */
    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	int po = other.offset + ooffset;
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((ooffset < 0) || (toffset < 0) || (toffset > count - len) || (ooffset > other.count - len)) {
	    return false;
	}
	while (len-- > 0) {
	    if (ta[to++] != pa[po++]) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Tests if two string regions are equal. 
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or 
     * if <code>toffset</code>+<code>length</code> is greater than the 
     * length of this string, or if 
     * <code>ooffset</code>+<code>length</code> is greater than the 
     * length of the string argument, then this method returns 
     * <code>false</code>. 
     *
     * @param   ignoreCase   if <code>true</code>, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          <code>false</code> otherwise. Whether the matching is exact
     *          or case insensitive depends on the <code>ignoreCase</code>
     *          argument.
     */
    public boolean regionMatches(boolean ignoreCase,
				         int toffset,
			               String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	int po = other.offset + ooffset;
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((ooffset < 0) || (toffset < 0) || (toffset > count - len) || (ooffset > other.count - len)) {
	    return false;
	}
	while (len-- > 0) {
	    char c1 = ta[to++];
	    char c2 = pa[po++];
	    if (c1 == c2)
		continue;
	    if (ignoreCase) {
		// If characters don't match but case may be ignored,
		// try converting both characters to uppercase.
		// If the results match, then the comparison scan should
		// continue. 
		char u1 = Character.toUpperCase(c1);
		char u2 = Character.toUpperCase(c2);
		if (u1 == u2)
		    continue;
		// Unfortunately, conversion to uppercase does not work properly
		// for the Georgian alphabet, which has strange rules about case
		// conversion.  So we need to make one last check before 
		// exiting.
		if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
		    continue;
	    }
	    return false;
	}
	return true;
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param   prefix    the prefix.
     * @param   toffset   where to begin looking in the string.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a prefix of the substring of this object starting
     *          at index <code>toffset</code>; <code>false</code> otherwise.
     */
    public boolean startsWith(String prefix, int toffset) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = prefix.value;
	int po = prefix.offset;
	int pc = prefix.count;
	// Note: toffset might be near -1>>>1.
	if ((toffset < 0) || (toffset > count - pc)) {
	    return false;
	}
	while (--pc >= 0) {
	    if (ta[to++] != pa[po++]) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param   prefix   the prefix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a prefix of the character sequence represented by
     *          this string; <code>false</code> otherwise.
     * @since   JDK1. 0
     */
    public boolean startsWith(String prefix) {
	return startsWith(prefix, 0);
    }

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param   suffix   the suffix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a suffix of the character sequence represented by
     *          this object; <code>false</code> otherwise.
     */
    public boolean endsWith(String suffix) {
	return startsWith(suffix, count - suffix.count);
    }

    /**
     * Returns a hashcode for this string.
     *
     * @return  a hash code value for this object. 
     */
    public int hashCode() {
	int h = 0;
	int off = offset;
	char val[] = value;
	int len = count;

	if (len < 16) {
 	    for (int i = len ; i > 0; i--) {
 		h = (h * 37) + val[off++];
 	    }
 	} else {
 	    // only sample some characters
 	    int skip = len / 8;
 	    for (int i = len ; i > 0; i -= skip, off += skip) {
 		h = (h * 39) + val[off];
 	    }
 	}

	return h;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character.
     *
     * @param   ch   a character.
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     */
    public int indexOf(int ch) {
	return indexOf(ch, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     *
     * @param   ch          a character.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object that is greater
     *          than or equal to <code>fromIndex</code>, or <code>-1</code>
     *          if the character does not occur.
     */
    public int indexOf(int ch, int fromIndex) {
	int max = offset + count;
	char v[] = value;

	if (fromIndex < 0) {
	    fromIndex = 0;
	} else if (fromIndex >= count) {
	    // Note: fromIndex might be near -1>>>1.
	    return -1;
	}
	for (int i = offset + fromIndex ; i < max ; i++) {
	    if (v[i] == ch) {
		return i - offset;
	    }
	}
	return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character.
     * The String is searched backwards starting at the last character.
     *
     * @param   ch   a character.
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     */
    public int lastIndexOf(int ch) {
	return lastIndexOf(ch, count - 1);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character, searching backward starting at the specified index.
     *
     * @param   ch          a character.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object that is less
     *          than or equal to <code>fromIndex</code>, or <code>-1</code>
     *          if the character does not occur before that point.
     */
    public int lastIndexOf(int ch, int fromIndex) {
	int min = offset;
	char v[] = value;
	
	for (int i = offset + ((fromIndex >= count) ? count - 1 : fromIndex) ; i >= min ; i--) {
	    if (v[i] == ch) {
		return i - offset;
	    }
	}
	return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     *
     * @param   str   any string.
     * @return  if the string argument occurs as a substring within this
     *          object, then the index of the first character of the first
     *          such substring is returned; if it does not occur as a
     *          substring, <code>-1</code> is returned.
     */
    public int indexOf(String str) {
	return indexOf(str, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  If the string argument occurs as a substring within this
     *          object at a starting index no smaller than
     *          <code>fromIndex</code>, then the index of the first character
     *          of the first such substring is returned. If it does not occur
     *          as a substring starting at <code>fromIndex</code> or beyond,
     *          <code>-1</code> is returned.
     */
    public int indexOf(String str, int fromIndex) {
    	char v1[] = value;
    	char v2[] = str.value;
    	int max = offset + (count - str.count);
	if (fromIndex >= count) {
	    /* Note: fromIndex might be near -1>>>1 */
	    return -1;
	}
    	if (fromIndex < 0) {
    	    fromIndex = 0;
    	}
	if (str.count == 0) {
	    return fromIndex;
	}

    	int strOffset = str.offset;
        char first  = v2[strOffset];
        int i = offset + fromIndex;

    startSearchForFirstChar:
        while (true) {

	    /* Look for first character. */
	    while (i <= max && v1[i] != first) {
		i++;
	    }
	    if (i > max) {
		return -1;
	    }

	    /* Found first character, now look at the rest of v2 */
	    int j = i + 1;
	    int end = j + str.count - 1;
	    int k = strOffset + 1;
	    while (j < end) {
		if (v1[j++] != v2[k++]) {
		    i++;
		    /* Look for str's first char again. */
		    continue startSearchForFirstChar;
		}
	    }
	    return i - offset;	/* Found whole string. */
        }
    }

    /**
     * Returns the index within this string of the rightmost occurrence
     * of the specified substring.  The rightmost empty string "" is
     * considered to occur at the index value <code>this.length()</code>.
     *
     * @param   str   the substring to search for.
     * @return  if the string argument occurs one or more times as a substring
     *          within this object, then the index of the first character of
     *          the last such substring is returned. If it does not occur as
     *          a substring, <code>-1</code> is returned.
     */
    public int lastIndexOf(String str) {
	return lastIndexOf(str, count);
    }

    /**
     * Returns the index within this string of the last occurrence of
     * the specified substring.
     * The returned index indicates the start of the substring, and it
     * must be equal to or less than <code>fromIndex</code>.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  If the string argument occurs one or more times as a substring
     *          within this object at a starting index no greater than
     *          <code>fromIndex</code>, then the index of the first character of
     *          the last such substring is returned. If it does not occur as a
     *          substring starting at <code>fromIndex</code> or earlier,
     *          <code>-1</code> is returned.
     */
    public int lastIndexOf(String str, int fromIndex) {
        /* 
	 * Check arguments; return immediately where possible. For
	 * consistency, don't check for null str.
	 */
        int rightIndex = count - str.count;
	if (fromIndex < 0) {
	    return -1;
	}
	if (fromIndex > rightIndex) {
	    fromIndex = rightIndex;
	}
	/* Empty string always matches. */
	if (str.count == 0) {
	    return fromIndex;
	}

	char v1[] = value;
	char v2[] = str.value;
	int strLastIndex = str.offset + str.count - 1;
	char strLastChar = v2[strLastIndex];
	int min = offset + str.count - 1;
	int i = min + fromIndex;

    startSearchForLastChar:
	while (true) {

	    /* Look for the last character */
	    while (i >= min && v1[i] != strLastChar) {
		i--;
	    }
	    if (i < min) {
		return -1;
	    }

	    /* Found last character, now look at the rest of v2. */
	    int j = i - 1;
	    int start = j - (str.count - 1);
	    int k = strLastIndex - 1;

	    while (j > start) {
	        if (v1[j--] != v2[k--]) {
		    i--;
		    /* Look for str's last char again. */
		    continue startSearchForLastChar;
		}
	    }

	    return start - offset + 1;    /* Found whole string. */
	}
    }

    /**
     * Returns a new string that is a substring of this string. The 
     * substring begins at the specified index and extends to the end of 
     * this string. 
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @return     the specified substring.
     * @exception  StringIndexOutOfBoundsException  if the
     *             <code>beginIndex</code> is out of range.
     */
    public String substring(int beginIndex) {
	return substring(beginIndex, length());
    }

    /**
     * Returns a new string that is a substring of this string. The 
     * substring begins at the specified <code>beginIndex</code> and 
     * extends to the character at index <code>endIndex - 1</code>. 
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return     the specified substring.
     * @exception  StringIndexOutOfBoundsException  if the
     *             <code>beginIndex</code> or the <code>endIndex</code> is
     *             out of range.
     */
    public String substring(int beginIndex, int endIndex) {
	if (beginIndex < 0) {
	    throw new StringIndexOutOfBoundsException(beginIndex);
	} 
	if (endIndex > count) {
	    throw new StringIndexOutOfBoundsException(endIndex);
	}
	if (beginIndex > endIndex) {
	    throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
	}
	return ((beginIndex == 0) && (endIndex == count)) ? this :
	    new String(offset + beginIndex, endIndex - beginIndex, value);
    }

    /**
     * Concatenates the specified string to the end of this string. 
     * <p>
     * If the length of the argument string is <code>0</code>, then this 
     * object is returned. 
     *
     * @param   str   the <code>String</code> that is concatenated to the end
     *                of this <code>String</code>.
     * @return  a string that represents the concatenation of this object's
     *          characters followed by the string argument's characters.
     */
    public String concat(String str) {
	int otherLen = str.length();
	if (otherLen == 0) {
	    return this;
	}
	char buf[] = new char[count + otherLen];
	getChars(0, count, buf, 0);
	str.getChars(0, otherLen, buf, count);
	return new String(0, count + otherLen, buf);
    }

    /**
     * Returns a new string resulting from replacing all occurrences of 
     * <code>oldChar</code> in this string with <code>newChar</code>. 
     * <p>
     * If the character <code>oldChar</code> does not occur in the 
     * character sequence represented by this object, then this string is 
     * returned. 
     *
     * @param   oldChar   the old character.
     * @param   newChar   the new character.
     * @return  a string derived from this string by replacing every
     *          occurrence of <code>oldChar</code> with <code>newChar</code>.
     */
    public String replace(char oldChar, char newChar) {
	if (oldChar != newChar) {
	    int len = count;
	    int i = -1;
	    char[] val = value; /* avoid getfield opcode */
	    int off = offset;   /* avoid getfield opcode */

	    while (++i < len) {
		if (val[off + i] == oldChar) {
		    break;
		}
	    }
	    if (i < len) {
		char buf[] = new char[len];
		for (int j = 0 ; j < i ; j++) {
		    buf[j] = val[off+j];
		}
		while (i < len) {
		    char c = val[off + i];
		    buf[i] = (c == oldChar) ? newChar : c;
		    i++;
		}
		return new String(0, len, buf);
	    }
	}
	return this;
    }

    /**
     * Converts all of the characters in this <code>String</code> to lower
     * case using the rules of the given locale.
     * @param locale use the case transformation rules for this locale
     * @return the String, converted to lowercase.
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.String#toUpperCase()
     * @since   JDK1.1
     */
    public String toLowerCase(Locale locale) {
        char[] result = new char[count];
        int i;
        int len = count;
	int off = offset;	   /* avoid getfield opcode */
	char[] val = value;        /* avoid getfield opcode */
      
        if (locale.getLanguage().equals("tr")) {
            // special loop for Turkey
	    for (i = 0; i < len; ++i) {
                char ch = val[off+i];
                if (ch == 'I') {
                    result[i] = '\u0131'; // dotless small i
                    continue;
                }
                if (ch == '\u0130') { 	  // dotted I
                    result[i] = 'i';	  // dotted i
                    continue;
                }
                result[i] = Character.toLowerCase(ch);
            }
        } else {
            // normal, fast loop
            for (i = 0; i < len; ++i) {
                result[i] = Character.toLowerCase(val[off+i]);
            }
        }
        return new String(result);
    }

    /**
     * Converts this <code>String</code> to lowercase. 
     * <p>
     * If no character in the string has a different lowercase version, 
     * based on calling the <code>toLowerCase</code> method defined by 
     * <code>Character</code>, then the original string is returned. 
     * <p>
     * Otherwise, a new string is allocated, whose length is identical 
     * to this string, and such that each character that has a different 
     * lowercase version is mapped to this lowercase equivalent. 
     *
     * @return  the string, converted to lowercase.
     * @see     java.lang.Character#toLowerCase(char)
     * @see     java.lang.String#toUpperCase()
     */
    public String toLowerCase() {
        return toLowerCase( Locale.getDefault() );
    }

    /**
     * Converts all of the characters in this <code>String</code> to upper
     * case using the rules of the given locale.
     * @param locale use the case transformation rules for this locale
     * @return the String, converted to uppercase.
     * @see     java.lang.Character#toUpperCase(char)
     * @see     java.lang.String#toLowerCase(char)
     * @since   JDK1.1
     */
    public String toUpperCase(Locale locale) {
        char[] result = new char[count]; /* warning: might grow! */
        int i;
	int resultOffset = 0;  /* result might grow, so i+resultOffset
				* gives correct write location in result
				*/
	int len = count;
        int off = offset;	   /* avoid getfield opcode */
	char[] val = value;        /* avoid getfield opcode */
        if (locale.getLanguage().equals("tr")) {
            // special loop for Turkey
	    for (i = 0; i < len; ++i) {
                char ch = val[off+i];
                if (ch == 'i') {
		    result[i+resultOffset] = '\u0130';  // dotted cap i
                    continue;
                }
                if (ch == '\u0131') {                   // dotless i
                    result[i+resultOffset] = 'I';       // cap I
                    continue;
                }
                if (ch == '\u00DF') {                   // sharp s
		    /* Grow result. */
		    char[] result2 = new char[result.length + 1];
		    System.arraycopy(result, 0, result2, 0,
				     i + 1 + resultOffset);
                    result2[i+resultOffset] = 'S';
		    resultOffset++;
		    result2[i+resultOffset] = 'S';
		    result = result2;
                    continue;
                }
                result[i+resultOffset] = Character.toUpperCase(ch);
            }
        } else {
            // normal, fast loop
            for (i = 0; i < len; ++i) {
                char ch = val[off+i];
                if (ch == '\u00DF') { // sharp s
		    /* Grow result. */
		    char[] result2 = new char[result.length + 1];
		    System.arraycopy(result, 0, result2, 0,
				     i + 1 + resultOffset);
                    result2[i+resultOffset] = 'S';
		    resultOffset++;
		    result2[i+resultOffset] = 'S';
		    result = result2;
                    continue;
                }
                result[i+resultOffset] = Character.toUpperCase(ch);
            }
        }
        return new String(result);
    }
    
    /**
     * Converts this string to uppercase. 
     * <p>
     * If no character in this string has a different uppercase version, 
     * based on calling the <code>toUpperCase</code> method defined by 
     * <code>Character</code>, then the original string is returned. 
     * <p>
     * Otherwise, a new string is allocated, whose length is identical 
     * to this string, and such that each character that has a different 
     * uppercase version is mapped to this uppercase equivalent. 
     *
     * @return  the string, converted to uppercase.
     * @see     java.lang.Character#toUpperCase(char)
     * @see     java.lang.String#toLowerCase()
     */
    public String toUpperCase() {
        return toUpperCase( Locale.getDefault() );
    }

    /**
     * Removes white space from both ends of this string. 
     * <p>
     * All characters that have codes less than or equal to 
     * <code>'&#92;u0020'</code> (the space character) are considered to be 
     * white space. 
     *
     * @return  this string, with white space removed from the front and end.
     */
    public String trim() {
	int len = count;
	int st = 0;
	int off = offset;      /* avoid getfield opcode */
	char[] val = value;    /* avoid getfield opcode */

	while ((st < len) && (val[off + st] <= ' ')) {
	    st++;
	}
	while ((st < len) && (val[off + len - 1] <= ' ')) {
	    len--;
	}
	return ((st > 0) || (len < count)) ? substring(st, len) : this;
    }

    /**
     * This object (which is already a string!) is itself returned. 
     *
     * @return  the string itself.
     */
    public String toString() {
	return this;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return  a newly allocated character array whose length is the length
     *          of this string and whose contents are initialized to contain
     *          the character sequence represented by this string.
     */
    public char[] toCharArray() {
	int max = length();
	char result[] = new char[max];
	getChars(0, max, result, 0);
	return result;
    }

    /**
     * Returns the string representation of the <code>Object</code> argument. 
     *
     * @param   obj   an <code>Object</code>.
     * @return  if the argument is <code>null</code>, then a string equal to
     *          <code>"null"</code>; otherwise, the value of
     *          <code>obj.toString()</code> is returned.
     * @see     java.lang.Object#toString()  
     */
    public static String valueOf(Object obj) {
	return (obj == null) ? "null" : obj.toString();
    }

    /**
     * Returns the string representation of the <code>char</code> array
     * argument. 
     *
     * @param   data   a <code>char</code> array.
     * @return  a newly allocated string representing the same sequence of
     *          characters contained in the character array argument.
     */
    public static String valueOf(char data[]) {
	return new String(data);
    }

    /**
     * Returns the string representation of a specific subarray of the 
     * <code>char</code> array argument. 
     * <p>
     * The <code>offset</code> argument is the index of the first 
     * character of the subarray. The <code>count</code> argument 
     * specifies the length of the subarray. 
     *
     * @param   data     the character array.
     * @param   offset   the initial offset into the value of the
     *                  <code>String</code>.
     * @param   count    the length of the value of the <code>String</code>.
     * @return  a newly allocated string representing the sequence of
     *          characters contained in the subarray of the character array
     *          argument.
     */
    public static String valueOf(char data[], int offset, int count) {
	return new String(data, offset, count);
    }
    
    /**
     * Returns a String that is equivalent to the specified character array.
     * It creates a new array and copies the characters into it.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a <code>String</code> that contains the characters of the
     *          specified subarray of the character array.
     */
    public static String copyValueOf(char data[], int offset, int count) {
	// All public String constructors now copy the data.
	return new String(data, offset, count);
    }

    /**
     * Returns a String that is equivalent to the specified character array.
     * It creates a new array and copies the characters into it.
     *
     * @param   data   the character array.
     * @return  a <code>String</code> that contains the characters of the
     *          character array.
     */
    public static String copyValueOf(char data[]) {
	return copyValueOf(data, 0, data.length);
    }

    /**
     * Returns the string representation of the <code>boolean</code> argument. 
     *
     * @param   b   a <code>boolean</code>.
     * @return  if the argument is <code>true</code>, a string equal to
     *          <code>"true"</code> is returned; otherwise, a string equal to
     *          <code>"false"</code> is returned.
     */
    public static String valueOf(boolean b) {
	return b ? "true" : "false";
    }

    /**
     * Returns the string representation of the <code>char</code> argument. 
     *
     * @param   c   a <code>char</code>.
     * @return  a newly allocated string of length <code>1</code> containing
     *          as its single character the argument <code>c</code>.
     */
    public static String valueOf(char c) {
	char data[] = {c};
	return new String(0, 1, data);
    }

    /**
     * Returns the string representation of the <code>int</code> argument. 
     * <p>
     * The representation is exactly the one returned by the 
     * <code>Integer.toString</code> method of one argument. 
     *
     * @param   i   an <code>int</code>.
     * @return  a newly allocated string containing a string representation of
     *          the <code>int</code> argument.
     * @see     java.lang.Integer#toString(int, int)
     */
    public static String valueOf(int i) {
        return Integer.toString(i, 10);
    }

    /**
     * Returns the string representation of the <code>long</code> argument. 
     * <p>
     * The representation is exactly the one returned by the 
     * <code>Long.toString</code> method of one argument. 
     *
     * @param   l   a <code>long</code>.
     * @return  a newly allocated string containing a string representation of
     *          the <code>long</code> argument.
     * @see     java.lang.Long#toString(long)
     */
    public static String valueOf(long l) {
        return Long.toString(l, 10);
    }

    /**
     * Returns the string representation of the <code>float</code> argument. 
     * <p>
     * The representation is exactly the one returned by the 
     * <code>Float.toString</code> method of one argument. 
     *
     * @param   f   a <code>float</code>.
     * @return  a newly allocated string containing a string representation of
     *          the <code>float</code> argument.
     * @see     java.lang.Float#toString(float)
     */
    public static String valueOf(float f) {
	return Float.toString(f);
    }

    /**
     * Returns the string representation of the <code>double</code> argument. 
     * <p>
     * The representation is exactly the one returned by the 
     * <code>Double.toString</code> method of one argument. 
     *
     * @param   d   a <code>double</code>.
     * @return  a newly allocated string containing a string representation of
     *          the <code>double</code> argument.
     * @see     java.lang.Double#toString(double)
     */
    public static String valueOf(double d) {
	return Double.toString(d);
    }

    /**
     * Returns a canonical representation for the string object. 
     * <p>
     * If <code>s</code> and <code>t</code> are strings such that 
     * <code>s.equals(t)</code>, it is guaranteed that<br>
     * <code>s.intern() == t.intern(). </code> 
     *
     * @return  a string that has the same contents as this string, but is
     *          guaranteed to be from a pool of unique strings.
     */
    public native String intern();

    /**
     * Returns the length of this string's UTF encoded form.
     */
    int utfLength() {
	int limit = offset + count;
	int utflen = 0;
	char[] val = value;

	for (int i = offset; i < limit; i++) {
	    int c = val[i];
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}
	return utflen;
    }

}
