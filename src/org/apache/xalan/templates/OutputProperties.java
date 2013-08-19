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
package org.apache.xalan.templates;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;

import java.lang.Cloneable;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.w3c.dom.Document;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xalan.serialize.Method;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;

/**
 * This class provides information from xsl:output elements. It is mainly
 * a wrapper for {@link java.util.Properties}, but can not extend that class
 * because it must be part of the {@link org.apache.xalan.templates.ElemTemplateElement}
 * heararchy.
 * <p>An OutputProperties list can contain another OutputProperties list as
 * its "defaults"; this second property list is searched if the property key
 * is not found in the original property list.</p>
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#output">xsl:output in XSLT Specification</a>
 * @
 */
public class OutputProperties extends ElemTemplateElement
        implements Cloneable
{

  /**
   * Creates an empty OutputProperties with no default values.
   */
  public OutputProperties()
  {
    this(Method.XML);
  }

  /**
   * Creates an empty OutputProperties with the specified defaults.
   *
   * @param   defaults   the defaults.
   */
  public OutputProperties(Properties defaults)
  {
    m_properties = new Properties(defaults);
  }

  /**
   * Creates an empty OutputProperties with the defaults specified by
   * a property file.  The method argument is used to construct a string of
   * the form output_[method].properties (for instance, output_html.properties).
   * The output_xml.properties file is always used as the base.
   * <p>At the moment, anything other than 'text', 'xml', and 'html', will
   * use the output_xml.properties file.</p>
   *
   * @param   method non-null reference to method name.
   */
  public OutputProperties(String method)
  {
    m_properties = new Properties(getDefaultMethodProperties(method));
  }
  
  static final String S_XSLT_PREFIX = "xslt.output.";
  static final int S_XSLT_PREFIX_LEN = S_XSLT_PREFIX.length();
  static final String S_XALAN_PREFIX = "org.apache.xslt.";
  static final int S_XALAN_PREFIX_LEN = S_XALAN_PREFIX.length();
  
  /** Built-in extensions namespace, reexpressed in {namespaceURI} syntax
   * suitable for prepending to a localname to produce a "universal
   * name".
   */
  static final String S_BUILTIN_EXTENSIONS_UNIVERSAL=
        "{"+Constants.S_BUILTIN_EXTENSIONS_URL+"}";
  
  /**
   * The old built-in extension namespace
   */
  static final String S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL=
        "{"+Constants.S_BUILTIN_OLD_EXTENSIONS_URL+"}";
  
  static final int S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN = S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL.length();
  
  /**
   * Fix up a string in an output properties file according to 
   * the rules of {@link #loadPropertiesFile}.
   * 
   * @param s non-null reference to string that may need to be fixed up.
   * @return A new string if fixup occured, otherwise the s argument.
   */
  static private String fixupPropertyString(String s, boolean doClipping)
  {
    int index;
    if (doClipping && s.startsWith(S_XSLT_PREFIX))
    {
      s = s.substring(S_XSLT_PREFIX_LEN);
    }
    if (s.startsWith(S_XALAN_PREFIX))
    {
      s = S_BUILTIN_EXTENSIONS_UNIVERSAL + s.substring(S_XALAN_PREFIX_LEN);
    }
    if ((index = s.indexOf("\\u003a")) > 0)
    {
      String temp = s.substring(index+6);
      s = s.substring(0, index) + ":" + temp;

    }
    return s;
  }
  
  /**
   * Load the properties file from a resource stream.  If a 
   * key name such as "org.apache.xslt.xxx", fix up the start of 
   * string to be a curly namespace.  If a key name starts with 
   * "xslt.output.xxx", clip off "xslt.output.".  If a key name *or* a 
   * key value is discovered, check for \u003a in the text, and 
   * fix it up to be ":", since earlier versions of the JDK do not 
   * handle the escape sequence (at least in key names).
   * 
   * @param resourceName non-null reference to resource name.
   * @param defaults Default properties, which may be null.
   */
  static private Properties loadPropertiesFile(final String resourceName, Properties defaults)
    throws IOException
  {

    // This static method should eventually be moved to a thread-specific class
    // so that we can cache the ContextClassLoader and bottleneck all properties file
    // loading throughout Xalan.

    Properties props = new Properties(defaults);

    InputStream is = null;
    BufferedInputStream bis = null;

    try {
      try {
	// Using doPrivileged to be able to read property file without opening
        // up secured container permissions like J2EE container
        is =(InputStream)AccessController.doPrivileged( new PrivilegedAction() {
          public Object run() {
            try {
              java.lang.reflect.Method getCCL = Thread.class.getMethod(
                  "getContextClassLoader", NO_CLASSES);
              if (getCCL != null) {
                ClassLoader contextClassLoader = (ClassLoader)
                    getCCL.invoke(Thread.currentThread(), NO_OBJS);
                return ( contextClassLoader.getResourceAsStream (
                    "org/apache/xalan/templates/" + resourceName) );
              }
            }
            catch ( Exception e ) { }

            return null;
            
          } 
        });
      }
      catch (Exception e) {}

      if ( is == null ) {
        is = (InputStream)AccessController.doPrivileged( new PrivilegedAction(){
	  public Object run() {
            return OutputProperties.class.getResourceAsStream(resourceName);
          }
        });

      }
      
      bis = new BufferedInputStream(is);
      props.load(bis);
    } 
    catch (IOException ioe) {
      if ( defaults == null ) {
        throw ioe;
      }
      else {
        throw new WrappedRuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_LOAD_RESOURCE, new Object[]{resourceName}), ioe); //"Could not load '"+resourceName+"' (check CLASSPATH), now using just the defaults ", ioe);
      }
    }
    catch (SecurityException se) {
      // Repeat IOException handling for sandbox/applet case -sc
      if ( defaults == null ) {
        throw se;
      }
      else {
        throw new WrappedRuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_LOAD_RESOURCE, new Object[]{resourceName}), se); //"Could not load '"+resourceName+"' (check CLASSPATH, applet security), now using just the defaults ", se);
      }
    } 
    finally {
      if ( bis != null ) {
        bis.close();
      }
      if (is != null ) {
        is.close();
      }
    }
    
    // Note that we're working at the HashTable level here, 
    // and not at the Properties level!  This is important 
    // because we don't want to modify the default properties.
    // NB: If fixupPropertyString ends up changing the property
    // name or value, we need to remove the old key and re-add
    // with the new key and value.  However, then our Enumeration
    // could lose its place in the HashTable.  So, we first
    // clone the HashTable and enumerate over that since the
    // clone will not change.  When we migrate to Collections,
    // this code should be revisited and cleaned up to use
    // an Iterator which may (or may not) alleviate the need for
    // the clone.  Many thanks to Padraig O'hIceadha
    // <padraig@gradient.ie> for finding this problem.  Bugzilla 2000.

    Enumeration keys = ((Properties) props.clone()).keys();
    while(keys.hasMoreElements())
    {
      String key = (String)keys.nextElement();
      // Now check if the given key was specified as a 
      // System property. If so, the system property 
      // overides the default value in the propery file.
      String value = null;
      try {
        value = System.getProperty(key);
      }
      catch (SecurityException se) {
        // No-op for sandbox/applet case, leave null -sc
      }
      if (value == null)      
        value = (String)props.get(key);                       
      
      String newKey = fixupPropertyString(key, true);
      String newValue = null;
      try {
        newValue = System.getProperty(newKey);
      }
      catch (SecurityException se) {
        // No-op for sandbox/applet case, leave null -sc
      }
      if (newValue == null)
        newValue = fixupPropertyString(value, false);
      else
        newValue = fixupPropertyString(newValue, false);
       
      if(key != newKey || value != newValue)
      {
        props.remove(key);
        props.put(newKey, newValue);
      }
      
    }
    
    return props;
  }

  /**
   * Creates an empty OutputProperties with the defaults specified by
   * a property file.  The method argument is used to construct a string of
   * the form output_[method].properties (for instance, output_html.properties).
   * The output_xml.properties file is always used as the base.
   * <p>At the moment, anything other than 'text', 'xml', and 'html', will
   * use the output_xml.properties file.</p>
   *
   * @param   method non-null reference to method name.
   *
   * @return Properties object that holds the defaults for the given method.
   */
  static public Properties getDefaultMethodProperties(String method)
  {
    String fileName = null;
    Properties defaultProperties = null;
    // According to this article : Double-check locking does not work
    // http://www.javaworld.com/javaworld/jw-02-2001/jw-0209-toolbox.html
    try
    {
      synchronized (m_synch_object)
      {
        if (null == m_xml_properties)  // double check
        {
          fileName = "output_xml.properties";
          m_xml_properties = loadPropertiesFile(fileName, null);
        }
      }

      if (method.equals(Method.XML))
      {
        defaultProperties = m_xml_properties;
      }
      else if (method.equals(Method.HTML))
      {
            if (null == m_html_properties)  // double check
            {
              fileName = "output_html.properties";
              m_html_properties = loadPropertiesFile(fileName,
                                                     m_xml_properties);
            }

        defaultProperties = m_html_properties;
      }
      else if (method.equals(Method.Text))
      {
            if (null == m_text_properties)  // double check
            {
              fileName = "output_text.properties";
              m_text_properties = loadPropertiesFile(fileName,
                                                     m_xml_properties);
              if(null == m_text_properties.getProperty(OutputKeys.ENCODING))
              {
                String mimeEncoding = org.apache.xalan.serialize.Encodings.getMimeEncoding(null);
                m_text_properties.put(OutputKeys.ENCODING, mimeEncoding);
              }
            }

        defaultProperties = m_text_properties;
      }
      else
      {

        // TODO: Calculate res file from name.
        defaultProperties = m_xml_properties;
      }
    }
    catch (IOException ioe)
    {
      throw new WrappedRuntimeException(
            "Output method is "+method+" could not load "+fileName+" (check CLASSPATH)",
             ioe);
    }

    return defaultProperties;
  }

  /**
   * Clone this OutputProperties, including a clone of the wrapped Properties
   * reference.
   *
   * @return A new OutputProperties reference, mutation of which should not
   *         effect this object.
   */
  public Object clone()
  {

    try
    {
      OutputProperties cloned = (OutputProperties) super.clone();

      cloned.m_properties = (Properties) cloned.m_properties.clone();

      return cloned;
    }
    catch (CloneNotSupportedException e)
    {
      return null;
    }
  }

  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setProperty(QName key, String value)
  {
    setProperty(key.toNamespacedString(), value);
  }

  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setProperty(String key, String value)
  {
    if(key.equals(OutputKeys.METHOD))
    {
      setMethodDefaults(value);
    }
    
    if (key.startsWith(S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL))
      key = S_BUILTIN_EXTENSIONS_UNIVERSAL + key.substring(S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN);
    
    m_properties.put(key, value);
  }

  /**
   * Searches for the property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list with the specified key value.
   */
  public String getProperty(QName key)
  {
    return m_properties.getProperty(key.toNamespacedString());
  }

  /**
   * Searches for the property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list with the specified key value.
   */
  public String getProperty(String key)
  {
    if (key.startsWith(S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL))
      key = S_BUILTIN_EXTENSIONS_UNIVERSAL + key.substring(S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN);
    return m_properties.getProperty(key);
  }

  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setBooleanProperty(QName key, boolean value)
  {
    m_properties.put(key.toNamespacedString(), value ? "yes" : "no");
  }

  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setBooleanProperty(String key, boolean value)
  {
    m_properties.put(key, value ? "yes" : "no");
  }

  /**
   * Searches for the boolean property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @return  the value in this property list as a boolean value, or false
   * if null or not "yes".
   */
  public boolean getBooleanProperty(QName key)
  {
    return getBooleanProperty(key.toNamespacedString());
  }

  /**
   * Searches for the boolean property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @return  the value in this property list as a boolean value, or false
   * if null or not "yes".
   */
  public boolean getBooleanProperty(String key)
  {
    return getBooleanProperty(key, m_properties);
  }

  /**
   * Searches for the boolean property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @param   props   the list of properties that will be searched.
   * @return  the value in this property list as a boolean value, or false
   * if null or not "yes".
   */
  public static boolean getBooleanProperty(String key, Properties props)
  {

    String s = props.getProperty(key);

    if (null == s ||!s.equals("yes"))
      return false;
    else
      return true;
  }
  
  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setIntProperty(QName key, int value)
  {
    setIntProperty(key.toNamespacedString(), value);
  }

  /**
   * Set an output property.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setIntProperty(String key, int value)
  {
    m_properties.put(key, Integer.toString(value));
  }

  /**
   * Searches for the int property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @return  the value in this property list as a int value, or false
   * if null or not a number.
   */
  public int getIntProperty(QName key)
  {
    return getIntProperty(key.toNamespacedString());
  }

  /**
   * Searches for the int property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @return  the value in this property list as a int value, or false
   * if null or not a number.
   */
  public int getIntProperty(String key)
  {
    return getIntProperty(key, m_properties);
  }

  /**
   * Searches for the int property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>false</code> if the property is not found, or if the value is other
   * than "yes".
   *
   * @param   key   the property key.
   * @param   props   the list of properties that will be searched.
   * @return  the value in this property list as a int value, or 0
   * if null or not a number.
   */
  public static int getIntProperty(String key, Properties props)
  {

    String s = props.getProperty(key);

    if (null == s)
      return 0;
    else
      return Integer.parseInt(s);
  }

  /**
   * Set an output property with a QName value.  The QName will be turned
   * into a string with the namespace in curly brackets.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setQNameProperty(QName key, QName value)
  {
    setQNameProperty(key.toNamespacedString(), value);
  }
  
  /**
   * Reset the default properties based on the method.
   *
   * @param method the method value.
   * @see javax.xml.transform.OutputKeys
   */
  public void setMethodDefaults(String method)
  {
    String defaultMethod = m_properties.getProperty(OutputKeys.METHOD);
    if((null == defaultMethod) || !defaultMethod.equals(method))
    {
      Properties savedProps = m_properties;
      Properties newDefaults = getDefaultMethodProperties(method);
      m_properties = new Properties(newDefaults);
      copyFrom(savedProps, false);
    }
  }
  

  /**
   * Set an output property with a QName value.  The QName will be turned
   * into a string with the namespace in curly brackets.
   *
   * @param key the key to be placed into the property list.
   * @param value the value corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setQNameProperty(String key, QName value)
  {
    setProperty(key, value.toNamespacedString());
  }

  /**
   * Searches for the qname property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list as a QName value, or false
   * if null or not "yes".
   */
  public QName getQNameProperty(QName key)
  {
    return getQNameProperty(key.toNamespacedString());
  }

  /**
   * Searches for the qname property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list as a QName value, or false
   * if null or not "yes".
   */
  public QName getQNameProperty(String key)
  {
    return getQNameProperty(key, m_properties);
  }

  /**
   * Searches for the qname property with the specified key in the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @param props the list of properties to search in.
   * @return  the value in this property list as a QName value, or false
   * if null or not "yes".
   */
  public static QName getQNameProperty(String key, Properties props)
  {

    String s = props.getProperty(key);

    if (null != s)
      return QName.getQNameFromString(s);
    else
      return null;
  }

  /**
   * Set an output property with a QName list value.  The QNames will be turned
   * into strings with the namespace in curly brackets.
   *
   * @param key the key to be placed into the property list.
   * @param v non-null list of QNames corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setQNameProperties(QName key, Vector v)
  {
    setQNameProperties(key.toNamespacedString(), v);
  }

  /**
   * Set an output property with a QName list value.  The QNames will be turned
   * into strings with the namespace in curly brackets.
   *
   * @param key the key to be placed into the property list.
   * @param v non-null list of QNames corresponding to <tt>key</tt>.
   * @see javax.xml.transform.OutputKeys
   */
  public void setQNameProperties(String key, Vector v)
  {

    int s = v.size();

    // Just an initial guess at reasonable tuning parameters
    FastStringBuffer fsb = new FastStringBuffer(9,9);

    for (int i = 0; i < s; i++)
    {
      QName qname = (QName) v.elementAt(i);

      fsb.append(qname.toNamespacedString());
      // Don't append space after last value
      if (i < s-1) 
        fsb.append(' ');
    }

    m_properties.put(key, fsb.toString());
  }

  /**
   * Searches for the list of qname properties with the specified key in
   * the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list as a vector of QNames, or false
   * if null or not "yes".
   */
  public Vector getQNameProperties(QName key)
  {
    return getQNameProperties(key.toNamespacedString());
  }

  /**
   * Searches for the list of qname properties with the specified key in
   * the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @return  the value in this property list as a vector of QNames, or false
   * if null or not "yes".
   */
  public Vector getQNameProperties(String key)
  {
    return getQNameProperties(key, m_properties);
  }

  /**
   * Searches for the list of qname properties with the specified key in
   * the property list.
   * If the key is not found in this property list, the default property list,
   * and its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param   key   the property key.
   * @param props the list of properties to search in.
   * @return  the value in this property list as a vector of QNames, or false
   * if null or not "yes".
   */
  public static Vector getQNameProperties(String key, Properties props)
  {

    String s = props.getProperty(key);

    if (null != s)
    {
      Vector v = new Vector();
      int l = s.length();
      boolean inCurly = false;
      FastStringBuffer buf = new FastStringBuffer();

      // parse through string, breaking on whitespaces.  I do this instead 
      // of a tokenizer so I can track whitespace inside of curly brackets, 
      // which theoretically shouldn't happen if they contain legal URLs.
      for (int i = 0; i < l; i++)
      {
        char c = s.charAt(i);

        if (Character.isWhitespace(c))
        {
          if (!inCurly)
          {
            if (buf.length() > 0)
            {
              QName qname = QName.getQNameFromString(buf.toString());
              v.addElement(qname);
              buf.reset();
            }
            continue;
          }
        }
        else if ('{' == c)
          inCurly = true;
        else if ('}' == c)
          inCurly = false;

        buf.append(c);
      }

      if (buf.length() > 0)
      {
        QName qname = QName.getQNameFromString(buf.toString());
        v.addElement(qname);
        buf.reset();
      }

      return v;
    }
    else
      return null;
  }

  /**
   * This function is called to recompose all of the output format extended elements.
   *
   * @param root non-null reference to the stylesheet root object.
   */
  public void recompose(StylesheetRoot root)
    throws TransformerException
  {
    root.recomposeOutput(this);
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    m_propertiesLevels = null;
  }

  /**
   * Get the Properties object that this class wraps.
   *
   * @return non-null reference to Properties object.
   */
  public Properties getProperties()
  {
    return m_properties;
  }
  
  /**
   * Copy the keys and values from the source to this object.  This will
   * not copy the default values.  This is meant to be used by going from
   * a higher precedence object to a lower precedence object, so that if a
   * key already exists, this method will not reset it.
   *
   * @param src non-null reference to the source properties.
   */
  public void copyFrom(Properties src)
  {
    copyFrom(src, true);
  }

  /**
   * Copy the keys and values from the source to this object.  This will
   * not copy the default values.  This is meant to be used by going from
   * a higher precedence object to a lower precedence object, so that if a
   * key already exists, this method will not reset it.
   *
   * @param src non-null reference to the source properties.
   * @param shouldResetDefaults true if the defaults should be reset based on 
   *                            the method property.
   */
  public void copyFrom(Properties src, boolean shouldResetDefaults)
  {

    Enumeration enum = src.keys();

    while (enum.hasMoreElements())
    {
      String key = (String) enum.nextElement();
    
      if (!isLegalPropertyKey(key))
        throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_OUTPUT_PROPERTY_NOT_RECOGNIZED, new Object[]{key})); //"output property not recognized: "
      
      Object oldValue = m_properties.get(key);
      if (null == oldValue)
      {
        String val = (String) src.get(key);
        
        if(shouldResetDefaults && key.equals(OutputKeys.METHOD))
        {
          setMethodDefaults(val);
        }

        m_properties.put(key, val);
      }
      else if (key.equals(OutputKeys.CDATA_SECTION_ELEMENTS))
      {
        m_properties.put(key, (String) oldValue + " " + (String) src.get(key));
      }
    }
  }

  /**
   * Copy the keys and values from the source to this object.  This will
   * not copy the default values.  This is meant to be used by going from
   * a higher precedence object to a lower precedence object, so that if a
   * key already exists, this method will not reset it.
   *
   * @param opsrc non-null reference to an OutputProperties.
   */
  public void copyFrom(OutputProperties opsrc)
    throws TransformerException
  {
    checkDuplicates(opsrc);
    copyFrom(opsrc.getProperties());
  }

  /**
   * Check to see if a set of properties is at the same import level as the
   * last set of properties set that was passed as an argument to this method.
   * This operation assumes that the OutputProperties are being called
   * from most important to least important, in document order.
   *
   * @param newProps non-null reference to OutputProperties that is about to
   *                 be added to this set.
   */
  private void checkDuplicates(OutputProperties newProps)
    throws TransformerException
  {

    if (null == m_propertiesLevels)
      m_propertiesLevels = new Hashtable();

    // This operation assumes that the OutputProperties are being called 
    // from most important to least important, in reverse document order.

    int newPrecedence = newProps.getStylesheetComposed().getImportCountComposed();

    Properties p = newProps.getProperties();
    Enumeration enum = p.keys();

    while (enum.hasMoreElements())
    {
      String key = (String) enum.nextElement();

      if (key.equals(OutputKeys.CDATA_SECTION_ELEMENTS))
        continue;

      // Do we already have this property? Call hashtable operation, 
      // since we don't want to look at default properties.
      Integer oldPrecedence = (Integer) m_propertiesLevels.get(key);
      if (null == oldPrecedence)
      {
        m_propertiesLevels.put(key, new Integer(newPrecedence));
      }
      else if (newPrecedence >= oldPrecedence.intValue())
      {
        String oldValue = (String) this.m_properties.get(key);
        String newValue = (String) newProps.m_properties.get(key);
        if ( ((oldValue == null) && (newValue != null)) || !oldValue.equals(newValue) )
        {
          String msg = key + " can not be multiply defined at the same "
                       + "import level! Old value = " 
                       + oldValue + "; New value = " + newValue;
          throw new TransformerException(msg, newProps);
        }
      }
    }
}

  /**
   * Report if the key given as an argument is a legal xsl:output key.
   *
   * @param key non-null reference to key name.
   *
   * @return true if key is legal.
   */
  public boolean isLegalPropertyKey(String key)
  {

    return (key.equals(OutputKeys.CDATA_SECTION_ELEMENTS)
            || key.equals(OutputKeys.DOCTYPE_PUBLIC)
            || key.equals(OutputKeys.DOCTYPE_SYSTEM)
            || key.equals(OutputKeys.ENCODING)
            || key.equals(OutputKeys.INDENT)
            || key.equals(OutputKeys.MEDIA_TYPE)
            || key.equals(OutputKeys.METHOD)
            || key.equals(OutputKeys.OMIT_XML_DECLARATION)
            || key.equals(OutputKeys.STANDALONE)
            || key.equals(OutputKeys.VERSION)
            || (key.length() > 0) && (key.charAt(0) == '{'));
  }

  /**
   *  This ugly field is used during recomposition to track the import precedence
   *  at which each attribute was first specified, so we can flag errors about values being
   *  set multiple time at the same precedence level. Note that this field is only used
   *  during recomposition, with the OutputProperties object owned by the
   *  {@link org.apache.xalan.templates.StylesheetRoot} object.
   */
  private transient Hashtable m_propertiesLevels;

  /** The output properties.
   *  @serial */
  private Properties m_properties = null;

  // Some special Xalan keys.

  /** The number of whitespaces to indent by, if indent="yes". */
  public static String S_KEY_INDENT_AMOUNT =
    S_BUILTIN_EXTENSIONS_UNIVERSAL+"indent-amount";

  /**
   * Fully qualified name of class with a default constructor that
   *  implements the ContentHandler interface, where the result tree events
   *  will be sent to.      
   */
  public static String S_KEY_CONTENT_HANDLER =
    S_BUILTIN_EXTENSIONS_UNIVERSAL+"content-handler";

  /** File name of file that specifies character to entity reference mappings. */
  public static String S_KEY_ENTITIES =
    S_BUILTIN_EXTENSIONS_UNIVERSAL+"entities";

  /** Use a value of "yes" if the href values for HTML serialization should 
   *  use %xx escaping. */
  public static String S_USE_URL_ESCAPING =
    S_BUILTIN_EXTENSIONS_UNIVERSAL+"use-url-escaping";

  /** Use a value of "yes" if the META tag should be omitted where it would
   *  otherwise be supplied.
   */
  public static String S_OMIT_META_TAG =
    S_BUILTIN_EXTENSIONS_UNIVERSAL+"omit-meta-tag";

  /** The default properties of all output files. */
  private static Properties m_xml_properties = null;

  /** The default properties when method="html". */
  private static Properties m_html_properties = null;

  /** The default properties when method="text". */
  private static Properties m_text_properties = null;

  /** Synchronization object for lazy initialization of the above tables. */
  private static Integer m_synch_object = new Integer(1);

  /** a zero length Class array used in loadPropertiesFile() */
  private static final Class[] NO_CLASSES = new Class[0];

  /** a zero length Object array used in loadPropertiesFile() */
  private static final Object[] NO_OBJS = new Object[0];

}
