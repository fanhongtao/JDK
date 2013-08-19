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

import java.io.InputStream;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.URL;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Provides information about encodings. Depends on the Java runtime
 * to provides writers for the different encodings, but can be used
 * to override encoding names and provide the last printable character
 * for each encoding.
 *
 * @version $Revision: 1.12 $ $Date: 2002/10/20 02:11:04 $
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 */
public class Encodings extends Object
{

  /**
   * The last printable character for unknown encodings.
   */
  static final int m_defaultLastPrintable = 0x7F;

  /**
   * Standard filename for properties file with encodings data.
   */
  static final String ENCODINGS_FILE = "org/apache/xalan/serialize/Encodings.properties";
  
  /** a zero length Class array used in loadPropertyFile() */
  private static final Class[] NO_CLASSES = new Class[0];

  /** a zero length Object array used in loadPropertyFile() */
  private static final Object[] NO_OBJS = new Object[0];



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
   * Returns an opaque CharToByte converter for the specified encoding.
   *
   * @param encoding The encoding
   * @return An object which should be a sun.io.CharToByteConverter, or null.
   */
  public static Object getCharToByteConverter(String encoding)
  {

    Class charToByteConverterClass = null;
    java.lang.reflect.Method getConverterMethod = null;

    try
    {
      charToByteConverterClass = Class.forName("sun.io.CharToByteConverter");
      Class argTypes[] = new Class[1];
      argTypes[0] = String.class;
      getConverterMethod
        = charToByteConverterClass.getMethod("getConverter", argTypes);
    }
    catch(Exception e)
    {
      System.err.println("Warning: Could not get charToByteConverterClass!");
      return null;
    }
    Object args[] = new Object[1];
    for (int i = 0; i < _encodings.length; ++i)
    {
      if (_encodings[i].name.equalsIgnoreCase(encoding))
      {
        try
        {
          args[0] = _encodings[i].javaName;
          Object converter = getConverterMethod.invoke(null, args);
          if(null != converter)
            return converter;
        }
        catch( Exception iae)
        {
          // keep trying
        }
      }
    }

    return null;
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
            "ISO8859_1") || encoding.equalsIgnoreCase("8859_1")
            || encoding.equalsIgnoreCase("UTF8")) ? DEFAULT_MIME_ENCODING
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
   * Load a list of all the supported encodings.
   *
   * System property "org.apache.xalan.serialize.encodings"
   * formatted using URL syntax may define an external encodings list.
   * Thanks to Sergey Ushakov for the code contribution!
   */
  private static EncodingInfo[] loadEncodingInfo()
  {
    URL url = null;
    try {
      String urlString = null;
      try {
        urlString = System.getProperty("org.apache.xalan.serialize.encodings", "");
      }
      catch (SecurityException e) {}
      
      if (urlString != null && urlString.length() > 0)
        url = new URL (urlString);
      if (url == null) {
        ClassLoader cl = null;          
        try{
          java.lang.reflect.Method getCCL = Thread.class.getMethod("getContextClassLoader", NO_CLASSES);
          if (getCCL != null) {
            cl = (ClassLoader) getCCL.invoke(Thread.currentThread(), NO_OBJS);
          }
        }
        catch (Exception e) {}
        if (cl != null) {
          url = cl.getResource(ENCODINGS_FILE);
        }
      }
      if (url == null)
        url = ClassLoader.getSystemResource(ENCODINGS_FILE);

      Properties props = new Properties ();
      if (url != null) {
        InputStream is = url.openStream();
        props.load(is);
        is.close();
      }
      else {
      // Seems to be no real need to force failure here, let the system
      //   do its best... The issue is not really very critical, and the
      //   output will be in any case _correct_ though maybe not always
      //   human-friendly... :)
      // But maybe report/log the resource problem?
      // Any standard ways to report/log errors in Xalan (in static context)?
      }

      int totalEntries = props.size();
      EncodingInfo[] ret = new EncodingInfo[totalEntries];
      Enumeration keys = props.keys();
      for (int i = 0; i < totalEntries; ++i) {
        String javaName = (String) keys.nextElement();
        String val = props.getProperty(javaName);
        int pos = val.indexOf(' ');
        String mimeName;
        int lastPrintable;
        if (pos < 0)
        {
          // Maybe report/log this problem?
          //  "Last printable character not defined for encoding " +
          //  mimeName + " (" + val + ")" ...
          mimeName = val;
          lastPrintable = 0x00FF;
        }
        else
        {
          mimeName = val.substring(0, pos);
          lastPrintable =
                         Integer.decode(val.substring(pos).trim()).intValue();
        }
        ret [i] = new EncodingInfo (mimeName, javaName, lastPrintable);
      }
      return ret;
    } catch (java.net.MalformedURLException mue) {
      throw new org.apache.xml.utils.WrappedRuntimeException(mue);
    }
    catch (java.io.IOException ioe) {
      throw new org.apache.xml.utils.WrappedRuntimeException(ioe);
    }
  }

  private static final EncodingInfo[] _encodings = loadEncodingInfo();
}
