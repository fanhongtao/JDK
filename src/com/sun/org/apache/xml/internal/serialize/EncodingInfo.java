/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


package com.sun.org.apache.xml.internal.serialize;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
/**
 * This class represents an encoding.
 *
 * @version $Id: EncodingInfo.java,v 1.2 2003/12/05 10:08:55 vk112360 Exp $
 */
public class EncodingInfo {

    // Method: sun.io.CharToByteConverter.getConverter(java.lang.String)
    private static java.lang.reflect.Method fgGetConverterMethod = null; 
    
    // Method: sun.io.CharToByteConverter.canConvert(char)
    private static java.lang.reflect.Method fgCanConvertMethod = null;
    
    // Flag indicating whether or not sun.io.CharToByteConverter is available.
    private static boolean fgConvertersAvailable = false;
    
    // An array to hold the argument for a method of CharToByteConverter.
    private Object [] fArgsForMethod = null;
    
    // name of encoding as registered with IANA;
    // preferably a MIME name, but aliases are fine too.
    String ianaName;
    String javaName;
    int lastPrintable;
    
    // The charToByteConverter with which we test unusual characters.
    Object fCharToByteConverter = null;
    
    // Is the converter null because it can't be instantiated
    // for some reason (perhaps we're running with insufficient authority as 
    // an applet?
    boolean fHaveTriedCToB = false;
	Charset nioCharset = null;
	CharsetEncoder nioCharEncoder = null;
    /**
     * Creates new <code>EncodingInfo</code> instance.
     */
    public EncodingInfo(String ianaName, String javaName, int lastPrintable) {
        this.ianaName = ianaName;
        this.javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        this.lastPrintable = lastPrintable;
		try{
			 nioCharset = Charset.forName(this.javaName);
			 if(nioCharset.canEncode())
			 	nioCharEncoder = nioCharset.newEncoder();
		}catch(IllegalCharsetNameException ie){
			nioCharset = null;
			nioCharEncoder = null;
		}catch(UnsupportedCharsetException ue){
			nioCharset = null;
			nioCharEncoder = null;
		}
    }

    /**
     * Returns a MIME charset name of this encoding.
     */
    public String getIANAName() {
        return this.ianaName;
    }

    /**
     * Returns a writer for this encoding based on
     * an output stream.
     *
     * @return A suitable writer
     * @exception UnsupportedEncodingException There is no convertor
     *  to support this encoding
     */
    public Writer getWriter(OutputStream output)
        throws UnsupportedEncodingException {
        // this should always be true!
        if (javaName != null) 
            return new OutputStreamWriter(output, javaName);
        javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        if(javaName == null) 
            // use UTF-8 as preferred encoding
            return new OutputStreamWriter(output, "UTF8");
        return new OutputStreamWriter(output, javaName);
    }
    /**
     * Checks whether the specified character is printable or not
     * in this encoding.
     *
     * @param ch a code point (0-0x10ffff)
     */
    public boolean isPrintable(char ch) {
        if(ch <= this.lastPrintable) 
            return true;
		if(nioCharEncoder != null)
			return nioCharEncoder.canEncode(ch);

		//We should not reach here , if we reach due to 
		//charset not supporting encoding then fgConvertersAvailable 
		//should take care of returning false.

        if(fCharToByteConverter == null) {
            if(fHaveTriedCToB || !fgConvertersAvailable) {
                // forget it; nothing we can do...
                return false;
            }
            if (fArgsForMethod == null) {
                fArgsForMethod = new Object [1];
            }
            // try and create it:
            try {
                fArgsForMethod[0] = javaName;
                fCharToByteConverter = fgGetConverterMethod.invoke(null, fArgsForMethod);
            } catch(Exception e) {   
                // don't try it again...
                fHaveTriedCToB = true;
                return false;
            }
        }
        try {
            fArgsForMethod[0] = new Character(ch);
            return ((Boolean) fgCanConvertMethod.invoke(fCharToByteConverter, fArgsForMethod)).booleanValue();
        } catch (Exception e) {
            // obviously can't use this converter; probably some kind of
            // security restriction
            fCharToByteConverter = null;
            fHaveTriedCToB = false;
            return false;
        }
    }

    // is this an encoding name recognized by this JDK?
    // if not, will throw UnsupportedEncodingException
    public static void testJavaEncodingName(String name)  throws UnsupportedEncodingException {
        final byte [] bTest = {(byte)'v', (byte)'a', (byte)'l', (byte)'i', (byte)'d'};
        String s = new String(bTest, name);
    }
    
    // Attempt to get methods for char to byte 
    // converter on class initialization.
    static {
        try {
            Class clazz = Class.forName("sun.io.CharToByteConverter");
            fgGetConverterMethod = clazz.getMethod("getConverter", new Class [] {String.class});
            fgCanConvertMethod = clazz.getMethod("canConvert", new Class [] {Character.TYPE});
            fgConvertersAvailable = true;
        }
        // ClassNotFoundException, NoSuchMethodException or SecurityException
        // Whatever the case, we cannot use sun.io.CharToByteConverter.
        catch (Exception exc) {
            fgGetConverterMethod = null;
            fgCanConvertMethod = null;
            fgConvertersAvailable = false;
        }
    }
}
