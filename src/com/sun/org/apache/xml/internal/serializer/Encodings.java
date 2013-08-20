/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: Encodings.java,v 1.8 2004/02/23 10:29:37 aruny Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.security.PrivilegedAction;
import java.security.AccessController;


/**
 * Provides information about encodings. Depends on the Java runtime
 * to provides writers for the different encodings, but can be used
 * to override encoding names and provide the last printable character
 * for each encoding.
 *
 * @version $Revision: 1.8 $ $Date: 2004/02/23 10:29:37 $
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
    static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";

    /**
     * Standard filename for properties file with encodings data.
     */
    static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";

    /** SUN JVM internal ByteToChar converter method */
    private static final Method
        SUN_CHAR2BYTE_CONVERTER_METHOD = findCharToByteConverterMethod();

    private static Method findCharToByteConverterMethod() {
        try
        {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        Class charToByteConverterClass = (Class) 
                            Class.forName("sun.io.CharToByteConverter");
                        Class argTypes[] = {String.class};
                        return charToByteConverterClass.getMethod("getConverter", argTypes);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.toString());
                    }
                }});
        }
        catch (Exception e)
        {
            System.err.println(
                "Warning: Could not get charToByteConverterClass!");
        }

        return null;
    }

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
                    return new OutputStreamWriter(
                        output,
                        _encodings[i].javaName);
                }
                catch (java.lang.IllegalArgumentException iae) // java 1.1.8
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
        catch (java.lang.IllegalArgumentException iae) // java 1.1.8
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
        if (SUN_CHAR2BYTE_CONVERTER_METHOD == null) {
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
                    Object converter =
                        SUN_CHAR2BYTE_CONVERTER_METHOD.invoke(null, args);
                    if (null != converter) 
                        return converter;
                }
                catch (Exception iae)
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
        EncodingInfo ei;

        String normalizedEncoding = encoding.toUpperCase();
        ei = (EncodingInfo) _encodingTableKeyJava.get(normalizedEncoding);
        if (ei == null)
            ei = (EncodingInfo) _encodingTableKeyMime.get(normalizedEncoding);
        if (ei != null)
            return ei.lastPrintable;
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
                        (encoding.equalsIgnoreCase("Cp1252")
                            || encoding.equalsIgnoreCase("ISO8859_1")
                            || encoding.equalsIgnoreCase("8859_1")
                            || encoding.equalsIgnoreCase("UTF8"))
                            ? DEFAULT_MIME_ENCODING
                            : convertJava2MimeEncoding(encoding);

                    encoding =
                        (null != jencoding) ? jencoding : DEFAULT_MIME_ENCODING;
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
        EncodingInfo enc =
            (EncodingInfo) _encodingTableKeyJava.get(encoding.toUpperCase());
        if (null != enc)
            return enc.name;
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
     * System property "encodings" formatted using URL syntax may define an
     * external encodings list. Thanks to Sergey Ushakov for the code
     * contribution!
     */
    private static EncodingInfo[] loadEncodingInfo()
    {
        URL url = null;
        try
        {
            String urlString = null;
            InputStream is = null;

            try
            {
                urlString = System.getProperty(ENCODINGS_PROP, "");
            }
            catch (SecurityException e)
            {
            }

            if (urlString != null && urlString.length() > 0) {
                url = new URL(urlString);
                is = url.openStream();
            }

            if (is == null) {
                SecuritySupport ss = SecuritySupport.getInstance();
                is = ss.getResourceAsStream(ObjectFactory.findClassLoader(),
                                            ENCODINGS_FILE);
            }

            Properties props = new Properties();
            if (is != null) {
                props.load(is);
                is.close();
            } else {
                // Seems to be no real need to force failure here, let the
                // system do its best... The issue is not really very critical,
                // and the output will be in any case _correct_ though maybe not
                // always human-friendly... :)
                // But maybe report/log the resource problem?
                // Any standard ways to report/log errors (in static context)?
            }

            int totalEntries = props.size();
            int totalMimeNames = 0;
            Enumeration keys = props.keys();
            for (int i = 0; i < totalEntries; ++i)
            {
                String javaName = (String) keys.nextElement();
                String val = props.getProperty(javaName);
                totalMimeNames++;
                int pos = val.indexOf(' ');
                for (int j = 0; j < pos; ++j)
                    if (val.charAt(j) == ',')
                        totalMimeNames++;
            }
            EncodingInfo[] ret = new EncodingInfo[totalMimeNames];
            int j = 0;
            keys = props.keys();
            for (int i = 0; i < totalEntries; ++i)
            {
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
                    lastPrintable =
                        Integer.decode(val.substring(pos).trim()).intValue();
                    StringTokenizer st =
                        new StringTokenizer(val.substring(0, pos), ",");
                    for (boolean first = true;
                        st.hasMoreTokens();
                        first = false)
                    {
                        mimeName = st.nextToken();
                        ret[j] =
                            new EncodingInfo(mimeName, javaName, lastPrintable);
                        _encodingTableKeyMime.put(
                            mimeName.toUpperCase(),
                            ret[j]);
                        if (first)
                            _encodingTableKeyJava.put(
                                javaName.toUpperCase(),
                                ret[j]);
                        j++;
                    }
                }
            }
            return ret;
        }
        catch (java.net.MalformedURLException mue)
        {
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(mue);
        }
        catch (java.io.IOException ioe)
        {
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(ioe);
        }
    }

    private static final Hashtable _encodingTableKeyJava = new Hashtable();
    private static final Hashtable _encodingTableKeyMime = new Hashtable();
    private static final EncodingInfo[] _encodings = loadEncodingInfo();
}
