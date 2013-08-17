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
package org.apache.xalan.processor;

import org.apache.xml.utils.StringToIntTable;

import java.lang.IllegalAccessException;
import java.lang.IndexOutOfBoundsException;
import java.lang.InstantiationException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.StringBuffer;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.StringVector;
import org.apache.xpath.XPath;

import javax.xml.transform.TransformerException;

/**
 * This class defines an attribute for an element in a XSLT stylesheet,
 * is meant to reflect the structure defined in http://www.w3.org/TR/xslt#dtd, and the
 * mapping between Xalan classes and the markup attributes in the element.
 */
public class XSLTAttributeDef
{

  /**
   * Construct an instance of XSLTAttributeDef.
   *
   * @param namespace The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param type One of T_CDATA, T_URL, T_AVT, T_PATTERN, T_EXPR, T_CHAR,
   * T_PRIORITY, T_YESNO, T_QNAME, T_QNAMES, T_ENUM, T_SIMPLEPATTERNLIST,
   * T_NMTOKEN, T_STRINGLIST, T_PREFIX_URLLIST.
   * @param required true if this is attribute is required by the XSLT specification.
   */
  XSLTAttributeDef(String namespace, String name, int type, boolean required)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = type;
    this.m_required = required;
  }

  /**
   * Construct an instance of XSLTAttributeDef.
   *
   * @param namespace The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param type One of T_CDATA, T_URL, T_AVT, T_PATTERN, T_EXPR,
   * T_CHAR, T_PRIORITY, T_YESNO, T_QNAME, T_QNAMES, T_ENUM,
   * T_SIMPLEPATTERNLIST, T_NMTOKEN, T_STRINGLIST, T_PREFIX_URLLIST.
   * @param defaultVal The default value for this attribute.
   */
  XSLTAttributeDef(String namespace, String name, int type, String defaultVal)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = type;
    this.m_required = false;
    this.m_default = defaultVal;
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses two
   * enumerated values.
   *
   * @param namespace The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param required true if this attribute is required by the XSLT specification.
   * @param k1 The XSLT name of the enumerated value.
   * @param v1 An integer representation of k1.
   * @param k2 The XSLT name of the enumerated value.
   * @param v2 An integer representation of k2.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1, String k2, int v2)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(2);

    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses three
   * enumerated values.
   *
   * @param namespace The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param required true if this attribute is required by the XSLT specification.
   * @param k1 The XSLT name of the enumerated value.
   * @param v1 An integer representation of k1.
   * @param k2 The XSLT name of the enumerated value.
   * @param v2 An integer representation of k2.
   * @param k3 The XSLT name of the enumerated value.
   * @param v3 An integer representation of k3.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1, String k2, int v2, String k3, int v3)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(3);

    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
    m_enums.put(k3, v3);
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses three
   * enumerated values.
   *
   * @param namespace The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param required true if this attribute is required by the XSLT specification.
   * @param k1 The XSLT name of the enumerated value.
   * @param v1 An integer representation of k1.
   * @param k2 The XSLT name of the enumerated value.
   * @param v2 An integer representation of k2.
   * @param k3 The XSLT name of the enumerated value.
   * @param v3 An integer representation of k3.
   * @param k4 The XSLT name of the enumerated value.
   * @param v4 An integer representation of k4.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1, String k2, int v2, String k3, int v3,
                   String k4, int v4)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(4);

    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
    m_enums.put(k3, v3);
    m_enums.put(k4, v4);
  }

  /** Type values that represent XSLT attribute types. */
  static final int T_CDATA = 1,

  // <!-- Used for the type of an attribute value that is a URI reference.-->
  T_URL = 2,

  // <!-- Used for the type of an attribute value that is an
  // attribute value template.-->
  T_AVT = 3,  // Attribute Value Template

  // <!-- Used for the type of an attribute value that is a pattern.-->
  T_PATTERN = 4,

  // <!-- Used for the type of an attribute value that is an expression.-->
  T_EXPR = 5,

  // <!-- Used for the type of an attribute value that consists
  // of a single character.-->
  T_CHAR = 6,

  // <!-- Used for the type of an attribute value that is a priority. -->
  T_PRIORITY = 7,

  // Used for boolean values
  T_YESNO = 8,

  // <!-- Used for the type of an attribute value that is a QName; the prefix
  // gets expanded by the XSLT processor. -->
  T_QNAME = 9,

  // <!-- Like qname but a whitespace-separated list of QNames. -->
  T_QNAMES = 10,

  // <!-- Used for enumerated values -->
  T_ENUM = 11,

  // Used for simple match patterns, i.e. xsl:strip-space spec.
  T_SIMPLEPATTERNLIST = 12,

  // Used for a known token.
  T_NMTOKEN = 13,

  // Used for a list of white-space delimited strings.
  T_STRINGLIST = 14,

  // Used for a list of white-space delimited strings.
  T_PREFIX_URLLIST = 15;

  /** Representation for an attribute in a foreign namespace. */
  static XSLTAttributeDef m_foreignAttr = new XSLTAttributeDef("*", "*",
                                            XSLTAttributeDef.T_CDATA, false);

  /** Method name that objects may implement if they wish to have forein attributes set. */
  static String S_FOREIGNATTR_SETTER = "setForeignAttr";

  /**
   * The allowed namespace for this element.
   */
  private String m_namespace;

  /**
   * Get the allowed namespace for this attribute.
   *
   * @return The allowed namespace for this attribute, which may be null, or may be "*".
   */
  String getNamespace()
  {
    return m_namespace;
  }

  /**
   * The name of this element.
   */
  private String m_name;

  /**
   * Get the name of this attribute.
   *
   * @return non-null reference to the name of this attribute, which may be "*".
   */
  String getName()
  {
    return m_name;
  }

  /**
   * The type of this attribute value.
   */
  private int m_type;

  /**
   * Get the type of this attribute value.
   *
   * @return One of T_CDATA, T_URL, T_AVT, T_PATTERN, T_EXPR, T_CHAR,
   * T_PRIORITY, T_YESNO, T_QNAME, T_QNAMES, T_ENUM, T_SIMPLEPATTERNLIST,
   * T_NMTOKEN, T_STRINGLIST, T_PREFIX_URLLIST.
   */
  int getType()
  {
    return m_type;
  }

  /**
   * If this element is of type T_ENUM, this will contain
   * a map from the attribute string to the Xalan integer
   * value.
   */
  private StringToIntTable m_enums;

  /**
   * If this element is of type T_ENUM, this will return
   * a map from the attribute string to the Xalan integer
   * value.
   * @param key The XSLT attribute value.
   *
   * @return The integer representation of the enumerated value for this attribute.
   * @throws Throws NullPointerException if m_enums is null.
   */
  private int getEnum(String key)
  {
    return m_enums.get(key);
  }

  /**
   * The default value for this attribute.
   */
  private String m_default;

  /**
   * Get the default value for this attribute.
   *
   * @return The default value for this attribute, or null.
   */
  String getDefault()
  {
    return m_default;
  }

  /**
   * Set the default value for this attribute.
   *
   * @param def String representation of the default value for this attribute.
   */
  void setDefault(String def)
  {
    m_default = def;
  }

  /**
   * If true, this is a required attribute.
   */
  private boolean m_required;

  /**
   * Get whether or not this is a required attribute.
   *
   * @return true if this is a required attribute.
   */
  boolean getRequired()
  {
    return m_required;
  }

  /**
   * String that should represent the setter method which which
   * may be used on objects to set a value that represents this attribute  
   */
  String m_setterString = null;

  /**
   * Return a string that should represent the setter method.
   * The setter method name will be created algorithmically the
   * first time this method is accessed, and then cached for return
   * by subsequent invocations of this method.
   *
   * @return String that should represent the setter method which which
   * may be used on objects to set a value that represents this attribute,
   * of null if no setter method should be called.
   */
  public String getSetterMethodName()
  {

    if (null == m_setterString)
    {
      if (m_foreignAttr == this)
      {
        return S_FOREIGNATTR_SETTER;
      }
      else if (m_name.equals("*"))
      {
        m_setterString = "addLiteralResultAttribute";

        return m_setterString;
      }

      StringBuffer outBuf = new StringBuffer();

      outBuf.append("set");

      if ((m_namespace != null)
              && m_namespace.equals(Constants.S_XMLNAMESPACEURI))
      {
        outBuf.append("Xml");
      }

      int n = m_name.length();

      for (int i = 0; i < n; i++)
      {
        char c = m_name.charAt(i);

        if ('-' == c)
        {
          i++;

          c = m_name.charAt(i);
          c = Character.toUpperCase(c);
        }
        else if (0 == i)
        {
          c = Character.toUpperCase(c);
        }

        outBuf.append(c);
      }

      m_setterString = outBuf.toString();
    }

    return m_setterString;
  }

  /**
   * Process an attribute string of type T_AVT into
   * a AVT value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value Should be an Attribute Value Template string.
   *
   * @return An AVT object that may be used to evaluate the Attribute Value Template.
   *
   * @throws org.xml.sax.SAXException which will wrap a
   * {@link javax.xml.transform.TransformerException}, if there is a syntax error
   * in the attribute value template string.
   */
  AVT processAVT(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    try
    {
      AVT avt = new AVT(handler, uri, name, rawName, value);

      return avt;
    }
    catch (TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Process an attribute string of type T_CDATA into
   * a String value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value non-null string reference.
   *
   * @return The value argument.
   */
  Object processCDATA(StylesheetHandler handler, String uri, String name,
                      String rawName, String value)
  {
    return value;
  }

  /**
   * Process an attribute string of type T_CHAR into
   * a Character value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value Should be a string with a length of 1.
   *
   * @return Character object.
   *
   * @throws org.xml.sax.SAXException if the string is not a length of 1.
   */
  Object processCHAR(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    if (value.length() != 1)
    {
      handler.error(
        "An XSLT attribute of type T_CHAR must be only 1 character!", null);
    }

    return new Character(value.charAt(0));
  }

  /**
   * Process an attribute string of type T_ENUM into
   * a int value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value non-null string that represents an enumerated value that is
   * valid for this element.
   *
   * @return An Integer representation of the enumerated value.
   */
  Object processENUM(StylesheetHandler handler, String uri, String name,
                     String rawName, String value)
  {

    int enum = this.getEnum(value);

    return new Integer(enum);
  }

  /**
   * Process an attribute string of type T_EXPR into
   * an XPath value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value An XSLT expression string.
   *
   * @return an XPath object that may be used for evaluation.
   *
   * @throws org.xml.sax.SAXException that wraps a
   * {@link javax.xml.transform.TransformerException} if the expression
   * string contains a syntax error.
   */
  Object processEXPR(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    try
    {
      XPath expr = handler.createXPath(value);

      return expr;
    }
    catch (TransformerException te)
    {
      org.xml.sax.SAXException se = new org.xml.sax.SAXException(te);
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Process an attribute string of type T_NMTOKEN into
   * a String value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A NMTOKEN string.
   *
   * @return the value argument.
   */
  Object processNMTOKEN(StylesheetHandler handler, String uri, String name,
                        String rawName, String value)
  {
    return value;
  }

  /**
   * Process an attribute string of type T_PATTERN into
   * an XPath match pattern value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A match pattern string.
   *
   * @return An XPath pattern that may be used to evaluate the XPath.
   *
   * @throws org.xml.sax.SAXException that wraps a
   * {@link javax.xml.transform.TransformerException} if the match pattern
   * string contains a syntax error.
   */
  Object processPATTERN(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    try
    {
      XPath pattern = handler.createMatchPatternXPath(value);

      return pattern;
    }
    catch (TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Process an attribute string of type T_PRIORITY into
   * a double value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A string that can be parsed into a double value.
   *
   * @return A Double object.
   *
   * @throws org.xml.sax.SAXException that wraps a
   * {@link javax.xml.transform.TransformerException}
   * if the string does not contain a parsable number.
   */
  Object processPRIORITY(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    try
    {
      return Double.valueOf(value);
    }
    catch (NumberFormatException nfe)
    {
      handler.error(XSLTErrorResources.ER_PRIORITY_NOT_PARSABLE, null, nfe);//"Priority value does not contain a parsable number.",
                    //nfe);

      return new Double(0.0);
    }
  }

  /**
   * Process an attribute string of type T_QNAME into
   * a QName value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A string that represents a potentially prefix qualified name.
   *
   * @return A QName object.
   *
   * @throws org.xml.sax.SAXException if the string contains a prefix that can not be
   * resolved, or the string contains syntax that is invalid for a qualified name.
   */
  Object processQNAME(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {
    return new QName(value, handler);
  }

  /**
   * Process an attribute string of type T_QNAMES into
   * a vector of QNames.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A whitespace delimited list of qualified names.
   *
   * @return a Vector of QName objects.
   *
   * @throws org.xml.sax.SAXException if the one of the qualified name strings
   * contains a prefix that can not be
   * resolved, or a qualified name contains syntax that is invalid for a qualified name.
   */
  Vector processQNAMES(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nQNames = tokenizer.countTokens();
    Vector qnames = new Vector(nQNames);

    for (int i = 0; i < nQNames; i++)
    {
      // Fix from Alexander Rudnev
      qnames.addElement(new QName(tokenizer.nextToken(), handler));
    }

    return qnames;
  }

  /**
   * Process an attribute string of type T_SIMPLEPATTERNLIST into
   * a vector of XPath match patterns.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A whitespace delimited list of simple match patterns.
   *
   * @return A Vector of XPath objects.
   *
   * @throws org.xml.sax.SAXException that wraps a
   * {@link javax.xml.transform.TransformerException} if one of the match pattern
   * strings contains a syntax error.
   */
  Vector processSIMPLEPATTERNLIST(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    try
    {
      StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
      int nPatterns = tokenizer.countTokens();
      Vector patterns = new Vector(nPatterns);

      for (int i = 0; i < nPatterns; i++)
      {
        XPath pattern =
          handler.createMatchPatternXPath(tokenizer.nextToken());

        patterns.addElement(pattern);
      }

      return patterns;
    }
    catch (TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Process an attribute string of type T_STRINGLIST into
   * a vector of XPath match patterns.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value a whitespace delimited list of string values.
   *
   * @return A StringVector of the tokenized strings.
   */
  StringVector processSTRINGLIST(StylesheetHandler handler, String uri,
                                 String name, String rawName, String value)
  {

    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nStrings = tokenizer.countTokens();
    StringVector strings = new StringVector(nStrings);

    for (int i = 0; i < nStrings; i++)
    {
      strings.addElement(tokenizer.nextToken());
    }

    return strings;
  }

  /**
   * Process an attribute string of type T_URLLIST into
   * a vector of prefixes that may be resolved to URLs.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A list of whitespace delimited prefixes.
   *
   * @return A vector of strings that may be resolved to URLs.
   *
   * @throws org.xml.sax.SAXException if one of the prefixes can not be resolved.
   */
  StringVector processPREFIX_URLLIST(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nStrings = tokenizer.countTokens();
    StringVector strings = new StringVector(nStrings);

    for (int i = 0; i < nStrings; i++)
    {
      String prefix = tokenizer.nextToken();
      String url = handler.getNamespaceForPrefix(prefix);

      strings.addElement(url);
    }

    return strings;
  }

  /**
   * Process an attribute string of type T_URL into
   * a URL value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value non-null string that conforms to the URL syntax.
   *
   * @return The non-absolutized URL argument, in other words, the value argument.
   *
   * @throws org.xml.sax.SAXException if the URL does not conform to the URL syntax.
   */
  String processURL(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    // TODO: syntax check URL value.
    // return SystemIDResolver.getAbsoluteURI(value, 
    //                                         handler.getBaseIdentifier());
    return value;
  }

  /**
   * Process an attribute string of type T_YESNO into
   * a Boolean value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value A string that should be "yes" or "no".
   *
   * @return Boolean object representation of the value.
   *
   * @throws org.xml.sax.SAXException
   */
  private Boolean processYESNO(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    // Is this already checked somewhere else?  -sb
    if (!(value.equals("yes") || value.equals("no")))
      handler.error(XSLTErrorResources.ER_VALUE_SHOULD_EQUAL, new Object[]{name}, null);//"Value for " + name + " should equal 'yes' or 'no'",
                    //null);

    return new Boolean(value.equals("yes") ? true : false);
  }

  /**
   * Process an attribute value.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param name The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param value The unprocessed string value of the attribute.
   *
   * @return The processed Object representation of the attribute.
   *
   * @throws org.xml.sax.SAXException if the attribute value can not be processed.
   */
  Object processValue(
          StylesheetHandler handler, String uri, String name, String rawName, String value)
            throws org.xml.sax.SAXException
  {

    int type = getType();
    Object processedValue = null;

    switch (type)
    {
    case T_AVT :
      processedValue = processAVT(handler, uri, name, rawName, value);
      break;
    case T_CDATA :
      processedValue = processCDATA(handler, uri, name, rawName, value);
      break;
    case T_CHAR :
      processedValue = processCHAR(handler, uri, name, rawName, value);
      break;
    case T_ENUM :
      processedValue = processENUM(handler, uri, name, rawName, value);
      break;
    case T_EXPR :
      processedValue = processEXPR(handler, uri, name, rawName, value);
      break;
    case T_NMTOKEN :
      processedValue = processNMTOKEN(handler, uri, name, rawName, value);
      break;
    case T_PATTERN :
      processedValue = processPATTERN(handler, uri, name, rawName, value);
      break;
    case T_PRIORITY :
      processedValue = processPRIORITY(handler, uri, name, rawName, value);
      break;
    case T_QNAME :
      processedValue = processQNAME(handler, uri, name, rawName, value);
      break;
    case T_QNAMES :
      processedValue = processQNAMES(handler, uri, name, rawName, value);
      break;
    case T_SIMPLEPATTERNLIST :
      processedValue = processSIMPLEPATTERNLIST(handler, uri, name, rawName,
                                                value);
      break;
    case T_URL :
      processedValue = processURL(handler, uri, name, rawName, value);
      break;
    case T_YESNO :
      processedValue = processYESNO(handler, uri, name, rawName, value);
      break;
    case T_STRINGLIST :
      processedValue = processSTRINGLIST(handler, uri, name, rawName, value);
      break;
    case T_PREFIX_URLLIST :
      processedValue = processPREFIX_URLLIST(handler, uri, name, rawName,
                                             value);
      break;
    default :
    }

    return processedValue;
  }

  /**
   * Set the default value of an attribute.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param elem The object on which the property will be set.
   *
   * @throws org.xml.sax.SAXException wraps an invocation exception if the
   * setter method can not be invoked on the object.
   */
  void setDefAttrValue(StylesheetHandler handler, Object elem)
          throws org.xml.sax.SAXException
  {
    setAttrValue(handler, this.getNamespace(), this.getName(),
                 this.getName(), this.getDefault(), elem);
  }

  /**
   * Get the primative type for the class, if there
   * is one.  If the class is a Double, for instance,
   * this will return double.class.  If the class is not one
   * of the 9 primative types, it will return the same
   * class that was passed in.
   *
   * @param obj The object which will be resolved to a primative class object if possible.
   *
   * @return The most primative class representation possible for the object, never null.
   */
  private Class getPrimativeClass(Object obj)
  {

    if (obj instanceof XPath)
      return XPath.class;

    Class cl = obj.getClass();

    if (cl == Double.class)
    {
      cl = double.class;
    }

    if (cl == Float.class)
    {
      cl = float.class;
    }
    else if (cl == Boolean.class)
    {
      cl = boolean.class;
    }
    else if (cl == Byte.class)
    {
      cl = byte.class;
    }
    else if (cl == Character.class)
    {
      cl = char.class;
    }
    else if (cl == Short.class)
    {
      cl = short.class;
    }
    else if (cl == Integer.class)
    {
      cl = int.class;
    }
    else if (cl == Long.class)
    {
      cl = long.class;
    }

    return cl;
  }

  /**
   * Set a value on an attribute.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param attrUri The Namespace URI of the attribute, or an empty string.
   * @param attrLocalName The local name (without prefix), or empty string if not namespace processing.
   * @param attrRawName The raw name of the attribute, including possible prefix.
   * @param attrValue The attribute's value.
   * @param elem The object that should contain a property that represents the attribute.
   *
   * @throws org.xml.sax.SAXException
   */
  void setAttrValue(
          StylesheetHandler handler, String attrUri, String attrLocalName, String attrRawName, String attrValue, Object elem)
            throws org.xml.sax.SAXException
  {
    if(attrRawName.equals("xmlns") || attrRawName.startsWith("xmlns:"))
      return;
      
    String setterString = getSetterMethodName();

    // If this is null, then it is a foreign namespace and we 
    // do not process it.
    if (null != setterString)
    {
      try
      {
        Method meth;
        Object[] args;

        if(setterString.equals(S_FOREIGNATTR_SETTER))
        {
          // workaround for possible crimson bug
          if( attrUri==null) attrUri="";
          // First try to match with the primative value.
          Class sclass = attrUri.getClass();
          Class[] argTypes = new Class[]{ sclass, sclass,
                                      sclass, sclass };
  
          meth = elem.getClass().getMethod(setterString, argTypes);
  
          args = new Object[]{ attrUri, attrLocalName,
                                      attrRawName, attrValue };
        }
        else
        {
          Object value = processValue(handler, attrUri, attrLocalName,
                                      attrRawName, attrValue);
                                      
          // First try to match with the primative value.
          Class[] argTypes = new Class[]{ getPrimativeClass(value) };
  
          try
          {
            meth = elem.getClass().getMethod(setterString, argTypes);
          }
          catch (NoSuchMethodException nsme)
          {
            Class cl = ((Object) value).getClass();
  
            // If this doesn't work, try it with the non-primative value;
            argTypes[0] = cl;
            meth = elem.getClass().getMethod(setterString, argTypes);
          }
  
          args = new Object[]{ value };
        }

        meth.invoke(elem, args);
      }
      catch (NoSuchMethodException nsme)
      {
        if (!setterString.equals(S_FOREIGNATTR_SETTER))
          handler.error(XSLTErrorResources.ER_FAILED_CALLING_METHOD, new Object[]{setterString}, nsme);//"Failed calling " + setterString + " method!", nsme);
      }
      catch (IllegalAccessException iae)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CALLING_METHOD, new Object[]{setterString}, iae);//"Failed calling " + setterString + " method!", iae);
      }
      catch (InvocationTargetException nsme)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CALLING_METHOD, new Object[]{setterString}, nsme);//"Failed calling " + setterString + " method!", nsme);
      }
    }
  }
}
