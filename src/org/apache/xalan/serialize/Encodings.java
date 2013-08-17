/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.serialize;

import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Provides information about encodings. Depends on the Java runtime
 * to provides writers for the different encodings, but can be used
 * to override encoding names and provide the last printable character
 * for each encoding.
 *
 * @version $Revision: 1.7 $ $Date: 2001/03/11 23:55:39 $
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 */
public class Encodings extends Object
{

  /**
   * The last printable character for unknown encodings.
   */
  static final int m_defaultLastPrintable = 0x7F;

  /**
   * Returns a writer for the specified encoding based on
   * an output stream.
   *
   * @param output The output stream
   * @param encoding The encoding
   * @return A suitable writer
   * @throws UnsupportedEncodingException There is no convertor
   *  to support this encoding
   */
  public static Writer getWriter(OutputStream output, String encoding)
          throws UnsupportedEncodingException
  {

    for (int i = 0; i < _encodings.length; ++i)
    {
      if (_encodings[i].name.equalsIgnoreCase(encoding))
      {
        try
        {
          return new OutputStreamWriter(output, _encodings[i].javaName);
        }
        catch( java.lang.IllegalArgumentException iae) // java 1.1.8
        {
          // keep trying
        }
        catch (UnsupportedEncodingException usee)
        {

          // keep trying
        }
      }
    }

    try
    {
      return new OutputStreamWriter(output, encoding);
    }
    catch( java.lang.IllegalArgumentException iae) // java 1.1.8
    {
      throw new UnsupportedEncodingException(encoding);
    }
  }

  /**
   * Returns the last printable character for the specified
   * encoding.
   *
   * @param encoding The encoding
   * @return The last printable character
   */
  public static int getLastPrintable(String encoding)
  {

    for (int i = 0; i < _encodings.length; ++i)
    {
      if (_encodings[i].name.equalsIgnoreCase(encoding)
              || _encodings[i].javaName.equalsIgnoreCase(encoding))
        return _encodings[i].lastPrintable;
    }

    return m_defaultLastPrintable;
  }

  /**
   * Returns the last printable character for an unspecified
   * encoding.
   *
   * @return the default size
   */
  public static int getLastPrintable()
  {
    return m_defaultLastPrintable;
  }

  /** The default encoding, ISO style, ISO style.   */
  public static final String DEFAULT_MIME_ENCODING = "UTF-8";

  /**
   * Get the proper mime encoding.  From the XSLT recommendation: "The encoding
   * attribute specifies the preferred encoding to use for outputting the result
   * tree. XSLT processors are required to respect values of UTF-8 and UTF-16.
   * For other values, if the XSLT processor does not support the specified
   * encoding it may signal an error; if it does not signal an error it should
   * use UTF-8 or UTF-16 instead. The XSLT processor must not use an encoding
   * whose name does not match the EncName production of the XML Recommendation
   * [XML]. If no encoding attribute is specified, then the XSLT processor should
   * use either UTF-8 or UTF-16."
   *
   * @param encoding Reference to java-style encoding string, which may be null, 
   * in which case a default will be found.
   *
   * @return The ISO-style encoding string, or null if failure.
   */
  public static String getMimeEncoding(String encoding)
  {

    if (null == encoding)
    {
      try
      {

        // Get the default system character encoding.  This may be 
        // incorrect if they passed in a writer, but right now there 
        // seems to be no way to get the encoding from a writer.
        encoding = System.getProperty("file.encoding", "UTF8");

        if (null != encoding)
        {

          /*
          * See if the mime type is equal to UTF8.  If you don't
          * do that, then  convertJava2MimeEncoding will convert
          * 8859_1 to "ISO-8859-1", which is not what we want,
          * I think, and I don't think I want to alter the tables
          * to convert everything to UTF-8.
          */
          String jencoding =
            (encoding.equalsIgnoreCase("Cp1252") || encoding.equalsIgnoreCase(
            "ISO8859_1") || encoding.equalsIgnoreCase("8859_1") || encoding.equalsIgnoreCase("UTF8")) ? DEFAULT_MIME_ENCODING
                                                                                                      : convertJava2MimeEncoding(
                                                                                                        encoding);

          encoding = (null != jencoding) ? jencoding : DEFAULT_MIME_ENCODING;
        }
        else
        {
          encoding = DEFAULT_MIME_ENCODING;
        }
      }
      catch (SecurityException se)
      {
        encoding = DEFAULT_MIME_ENCODING;
      }
    }
    else
    {
      encoding = convertJava2MimeEncoding(encoding);
    }

    return encoding;
  }

  /**
   * Try the best we can to convert a Java encoding to a XML-style encoding.
   *
   * @param encoding non-null reference to encoding string, java style.
   *
   * @return ISO-style encoding string.
   */
  public static String convertJava2MimeEncoding(String encoding)
  {

    for (int i = 0; i < _encodings.length; ++i)
    {
      if (_encodings[i].javaName.equalsIgnoreCase(encoding))
      {
        return _encodings[i].name;
      }
    }

    return encoding;
  }
  
  /**
   * Try the best we can to convert a Java encoding to a XML-style encoding.
   *
   * @param encoding non-null reference to encoding string, java style.
   *
   * @return ISO-style encoding string.
   */
  public static String convertMime2JavaEncoding(String encoding)
  {

    for (int i = 0; i < _encodings.length; ++i)
    {
      if (_encodings[i].name.equalsIgnoreCase(encoding))
      {
        return _encodings[i].javaName;
      }
    }

    return encoding;
  }


  /**
   * Constructs a list of all the supported encodings.
   */
  private static final EncodingInfo[] _encodings = new EncodingInfo[]
  {

    //    <preferred MIME name>, <Java encoding name>
    // new EncodingInfo( "ISO 8859-1", "CP1252"); // Close enough, I guess
    new EncodingInfo("WINDOWS-1250", "Cp1250", 0x00FF),  // Peter Smolik
    // Patch attributed to havardw@underdusken.no (Håvard Wigtil)
    new EncodingInfo("WINDOWS-1252", "Cp1252", 0x00FF),
    new EncodingInfo("UTF-8", "UTF8", 0xFFFF),
    new EncodingInfo("US-ASCII", "ISO8859_1", 0x7F),
    new EncodingInfo("ISO-8859-1", "ISO8859_1", 0x00FF),
    // Patch attributed to havardw@underdusken.no (Håvard Wigtil)
    new EncodingInfo("ISO-8859-1", "ISO8859-1", 0x00FF),
    new EncodingInfo("ISO-8859-2", "ISO8859_2", 0x00FF),
    // I'm going to apply "ISO8859-X" variant to all these, to be safe.
    new EncodingInfo("ISO-8859-2", "ISO8859-2", 0x00FF),
    new EncodingInfo("ISO-8859-3", "ISO8859_3", 0x00FF),
    new EncodingInfo("ISO-8859-3", "ISO8859-3", 0x00FF),
    new EncodingInfo("ISO-8859-4", "ISO8859_4", 0x00FF),
    new EncodingInfo("ISO-8859-4", "ISO8859-4", 0x00FF),
    new EncodingInfo("ISO-8859-5", "ISO8859_5", 0x00FF),
    new EncodingInfo("ISO-8859-5", "ISO8859-5", 0x00FF),
    new EncodingInfo("ISO-8859-6", "ISO8859_6", 0x00FF),
    new EncodingInfo("ISO-8859-6", "ISO8859-6", 0x00FF),
    new EncodingInfo("ISO-8859-7", "ISO8859_7", 0x00FF),
    new EncodingInfo("ISO-8859-7", "ISO8859-7", 0x00FF),
    new EncodingInfo("ISO-8859-8", "ISO8859_8", 0x00FF),
    new EncodingInfo("ISO-8859-8", "ISO8859-8", 0x00FF),
    new EncodingInfo("ISO-8859-9", "ISO8859_9", 0x00FF),
    new EncodingInfo("ISO-8859-9", "ISO8859-9", 0x00FF),
    new EncodingInfo("US-ASCII", "8859_1", 0x00FF),  // ?
    new EncodingInfo("ISO-8859-1", "8859_1", 0x00FF),
    new EncodingInfo("ISO-8859-2", "8859_2", 0x00FF),
    new EncodingInfo("ISO-8859-3", "8859_3", 0x00FF),
    new EncodingInfo("ISO-8859-4", "8859_4", 0x00FF),
    new EncodingInfo("ISO-8859-5", "8859_5", 0x00FF),
    new EncodingInfo("ISO-8859-6", "8859_6", 0x00FF),
    new EncodingInfo("ISO-8859-7", "8859_7", 0x00FF),
    new EncodingInfo("ISO-8859-8", "8859_8", 0x00FF),
    new EncodingInfo("ISO-8859-9", "8859_9", 0x00FF),
    new EncodingInfo("ISO-8859-1", "8859-1", 0x00FF),
    new EncodingInfo("ISO-8859-2", "8859-2", 0x00FF),
    new EncodingInfo("ISO-8859-3", "8859-3", 0x00FF),
    new EncodingInfo("ISO-8859-4", "8859-4", 0x00FF),
    new EncodingInfo("ISO-8859-5", "8859-5", 0x00FF),
    new EncodingInfo("ISO-8859-6", "8859-6", 0x00FF),
    new EncodingInfo("ISO-8859-7", "8859-7", 0x00FF),
    new EncodingInfo("ISO-8859-8", "8859-8", 0x00FF),
    new EncodingInfo("ISO-8859-9", "8859-9", 0x00FF),
    new EncodingInfo("ISO-2022-JP", "JIS", 0xFFFF),
    new EncodingInfo("SHIFT_JIS", "SJIS", 0xFFFF),
    new EncodingInfo("EUC-JP", "EUC_JP", 0xFFFF),
    new EncodingInfo("EUC-KR", "EUC_KR", 0xFFFF),
    new EncodingInfo("EUC-CN", "EUC_CN", 0xFFFF),
    new EncodingInfo("EUC-TW", "EUC_TW", 0xFFFF),
    new EncodingInfo("GB2312", "EUC_CN", 0xFFFF),
    new EncodingInfo("EUC-JP", "EUC-JP", 0xFFFF),
    new EncodingInfo("EUC-KR", "EUC-KR", 0xFFFF),
    new EncodingInfo("EUC-CN", "EUC-CN", 0xFFFF),
    new EncodingInfo("EUC-TW", "EUC-TW", 0xFFFF),
    new EncodingInfo("GB2312", "EUC-CN", 0xFFFF),
    new EncodingInfo("GB2312", "GB2312", 0xFFFF),
    new EncodingInfo("BIG5", "Big5", 0xFFFF),
    new EncodingInfo("EUC-JP", "EUCJIS", 0xFFFF),
    new EncodingInfo("EUC-KR", "KSC5601", 0xFFFF),
    new EncodingInfo("ISO-2022-KR", "ISO2022KR", 0xFFFF),
    new EncodingInfo("KOI8-R", "KOI8_R", 0xFFFF),
    new EncodingInfo("EBCDIC-CP-US", "Cp037", 0x00FF),
    new EncodingInfo("EBCDIC-CP-CA", "Cp037", 0x00FF),
    new EncodingInfo("EBCDIC-CP-NL", "Cp037", 0x00FF),
    new EncodingInfo("EBCDIC-CP-DK", "Cp277", 0x00FF),
    new EncodingInfo("EBCDIC-CP-NO", "Cp277", 0x00FF),
    new EncodingInfo("EBCDIC-CP-FI", "Cp278", 0x00FF),
    new EncodingInfo("EBCDIC-CP-SE", "Cp278", 0x00FF),
    new EncodingInfo("EBCDIC-CP-IT", "Cp280", 0x00FF),
    new EncodingInfo("EBCDIC-CP-ES", "Cp284", 0x00FF),
    new EncodingInfo("EBCDIC-CP-GB", "Cp285", 0x00FF),
    new EncodingInfo("EBCDIC-CP-FR", "Cp297", 0x00FF),
    new EncodingInfo("EBCDIC-CP-AR1", "Cp420", 0x00FF),
    new EncodingInfo("EBCDIC-CP-HE", "Cp424", 0x00FF),
    new EncodingInfo("EBCDIC-CP-CH", "Cp500", 0x00FF),
    new EncodingInfo("EBCDIC-CP-ROECE", "Cp870", 0x00FF),
    new EncodingInfo("EBCDIC-CP-YU", "Cp870", 0x00FF),
    new EncodingInfo("EBCDIC-CP-IS", "Cp871", 0x00FF),
    new EncodingInfo("EBCDIC-CP-AR2", "Cp918", 0x00FF),
    new EncodingInfo("MacRoman", "MacTEC", 0xFF),
    new EncodingInfo("ASCII", "ASCII", 0x7F),
    new EncodingInfo("ISO-Latin-1", "ASCII", 0xFF),
    new EncodingInfo("UTF-8", "UTF8", 0xFFFF),
    new EncodingInfo("UNICODE", "Unicode", 0xFFFF),
    new EncodingInfo("UTF-16", "Unicode", 0xFFFF)
  };
}
