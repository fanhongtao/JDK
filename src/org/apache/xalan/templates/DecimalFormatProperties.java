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

import org.w3c.dom.*;

import org.xml.sax.*;

import org.apache.xpath.*;

import java.util.*;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.xml.utils.QName;
import org.apache.xalan.res.*;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:decimal-format.
 * <pre>
 * <!ELEMENT xsl:decimal-format EMPTY>
 * <!ATTLIST xsl:decimal-format
 *   name %qname; #IMPLIED
 *   decimal-separator %char; "."
 *   grouping-separator %char; ","
 *   infinity CDATA "Infinity"
 *   minus-sign %char; "-"
 *   NaN CDATA "NaN"
 *   percent %char; "%"
 *   per-mille %char; "&#x2030;"
 *   zero-digit %char; "0"
 *   digit %char; "#"
 *   pattern-separator %char; ";"
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#format-number">format-number in XSLT Specification</a>
 */
public class DecimalFormatProperties extends ElemTemplateElement
{

  /** An instance of DecimalFormatSymbols for this element.
   *  @serial       */
  DecimalFormatSymbols m_dfs;

  /**
   * Constructor DecimalFormatProperties
   *
   */
  public DecimalFormatProperties(int docOrderNumber)
  {

    m_dfs = new java.text.DecimalFormatSymbols();

    // Set default values, they can be overiden if necessary.  
    m_dfs.setInfinity(Constants.ATTRVAL_INFINITY);
    m_dfs.setNaN(Constants.ATTRVAL_NAN);

    m_docOrderNumber = docOrderNumber;
  }

  /**
   * Return the decimal format Symbols for this element.
   * <p>The xsl:decimal-format element declares a decimal-format,
   * which controls the interpretation of a format pattern used by
   * the format-number function. If there is a name attribute, then
   * the element declares a named decimal-format; otherwise, it
   * declares the default decimal-format. The value of the name
   * attribute is a QName, which is expanded as described in [2.4 Qualified Names].
   * It is an error to declare either the default decimal-format or a
   * decimal-format with a given name more than once (even with different
   * import precedence), unless it is declared every time with the same
   * value for all attributes (taking into account any default values).</p>
   * <p>The other attributes on xsl:decimal-format correspond to the
   * methods on the JDK 1.1 DecimalFormatSymbols class. For each get/set
   * method pair there is an attribute defined for the xsl:decimal-format
   * element.</p>
   *
   * @return the decimal format Symbols for this element.
   */
  public DecimalFormatSymbols getDecimalFormatSymbols()
  {
    return m_dfs;
  }

  /**
   * If there is a name attribute, then the element declares a named
   * decimal-format; otherwise, it declares the default decimal-format.
   * @serial
   */
  private QName m_qname = null;

  /**
   * Set the "name" attribute.
   * If there is a name attribute, then the element declares a named
   * decimal-format; otherwise, it declares the default decimal-format.
   *
   * @param qname The name to set as the "name" attribute.
   */
  public void setName(QName qname)
  {
    m_qname = qname;
  }

  /**
   * Get the "name" attribute.
   * If there is a name attribute, then the element declares a named
   * decimal-format; otherwise, it declares the default decimal-format.
   *
   * @return the value of the "name" attribute.
   */
  public QName getName()
  {

    if (m_qname == null)
      return new QName("");
    else
      return m_qname;
  }

  /**
   * Set the "decimal-separator" attribute.
   * decimal-separator specifies the character used for the decimal sign;
   * the default value is the period character (.).
   *
   * @param ds Character to set as decimal separator 
   */
  public void setDecimalSeparator(char ds)
  {
    m_dfs.setDecimalSeparator(ds);
  }

  /**
   * Get the "decimal-separator" attribute.
   * decimal-separator specifies the character used for the decimal sign;
   * the default value is the period character (.).
   *
   * @return the character to use as decimal separator
   */
  public char getDecimalSeparator()
  {
    return m_dfs.getDecimalSeparator();
  }

  /**
   * Set the "grouping-separator" attribute.
   * grouping-separator specifies the character used as a grouping
   * (e.g. thousands) separator; the default value is the comma character (,).
   *
   * @param gs Character to use a grouping separator 
   */
  public void setGroupingSeparator(char gs)
  {
    m_dfs.setGroupingSeparator(gs);
  }

  /**
   * Get the "grouping-separator" attribute.
   * grouping-separator specifies the character used as a grouping
   * (e.g. thousands) separator; the default value is the comma character (,).
   *
   * @return Character to use a grouping separator 
   */
  public char getGroupingSeparator()
  {
    return m_dfs.getGroupingSeparator();
  }

  /**
   * Set the "infinity" attribute.
   * infinity specifies the string used to represent infinity;
   * the default value is the string Infinity.
   *
   * @param inf String to use as the "infinity" attribute.
   */
  public void setInfinity(String inf)
  {
    m_dfs.setInfinity(inf);
  }

  /**
   * Get the "infinity" attribute.
   * infinity specifies the string used to represent infinity;
   * the default value is the string Infinity.
   *
   * @return String to use as the "infinity" attribute.
   */
  public String getInfinity()
  {
    return m_dfs.getInfinity();
  }

  /**
   * Set the "minus-sign" attribute.
   * minus-sign specifies the character used as the default minus sign; the
   * default value is the hyphen-minus character (-, #x2D).
   *
   * @param v Character to use as minus sign
   */
  public void setMinusSign(char v)
  {
    m_dfs.setMinusSign(v);
  }

  /**
   * Get the "minus-sign" attribute.
   * minus-sign specifies the character used as the default minus sign; the
   * default value is the hyphen-minus character (-, #x2D).
   *
   * @return Character to use as minus sign
   */
  public char getMinusSign()
  {
    return m_dfs.getMinusSign();
  }

  /**
   * Set the "NaN" attribute.
   * NaN specifies the string used to represent the NaN value;
   * the default value is the string NaN.
   *
   * @param v String to use as the "NaN" attribute.
   */
  public void setNaN(String v)
  {
    m_dfs.setNaN(v);
  }

  /**
   * Get the "NaN" attribute.
   * NaN specifies the string used to represent the NaN value;
   * the default value is the string NaN.
   *
   * @return String to use as the "NaN" attribute.
   */
  public String getNaN()
  {
    return m_dfs.getNaN();
  }

  /**
   * Set the "percent" attribute.
   * percent specifies the character used as a percent sign; the default
   * value is the percent character (%).
   *
   * @param v Character to use as percent 
   */
  public void setPercent(char v)
  {
    m_dfs.setPercent(v);
  }

  /**
   * Get the "percent" attribute.
   * percent specifies the character used as a percent sign; the default
   * value is the percent character (%).
   *
   * @return Character to use as percent 
   */
  public char getPercent()
  {
    return m_dfs.getPercent();
  }

  /**
   * Set the "per-mille" attribute.
   * per-mille specifies the character used as a per mille sign; the default
   * value is the Unicode per-mille character (#x2030).
   *
   * @param v Character to use as per-mille
   */
  public void setPerMille(char v)
  {
    m_dfs.setPerMill(v);
  }

  /**
   * Get the "per-mille" attribute.
   * per-mille specifies the character used as a per mille sign; the default
   * value is the Unicode per-mille character (#x2030).
   *
   * @return Character to use as per-mille 
   */
  public char getPerMille()
  {
    return m_dfs.getPerMill();
  }

  /**
   * Set the "zero-digit" attribute.
   * zero-digit specifies the character used as the digit zero; the default
   * value is the digit zero (0).
   *
   * @param v Character to use as the digit zero
   */
  public void setZeroDigit(char v)
  {
    m_dfs.setZeroDigit(v);
  }

  /**
   * Get the "zero-digit" attribute.
   * zero-digit specifies the character used as the digit zero; the default
   * value is the digit zero (0).
   *
   * @return Character to use as the digit zero
   */
  public char getZeroDigit()
  {
    return m_dfs.getZeroDigit();
  }

  /**
   * Set the "digit" attribute.
   * digit specifies the character used for a digit in the format pattern;
   * the default value is the number sign character (#).
   *
   * @param v Character to use for a digit in format pattern
   */
  public void setDigit(char v)
  {
    m_dfs.setDigit(v);
  }

  /**
   * Get the "digit" attribute.
   * digit specifies the character used for a digit in the format pattern;
   * the default value is the number sign character (#).
   *
   * @return Character to use for a digit in format pattern
   */
  public char getDigit()
  {
    return m_dfs.getDigit();
  }

  /**
   * Set the "pattern-separator" attribute.
   * pattern-separator specifies the character used to separate positive
   * and negative sub patterns in a pattern; the default value is the
   * semi-colon character (;).
   *
   * @param v Character to use as a pattern separator
   */
  public void setPatternSeparator(char v)
  {
    m_dfs.setPatternSeparator(v);
  }

  /**
   * Get the "pattern-separator" attribute.
   * pattern-separator specifies the character used to separate positive
   * and negative sub patterns in a pattern; the default value is the
   * semi-colon character (;).
   *
   * @return Character to use as a pattern separator
   */
  public char getPatternSeparator()
  {
    return m_dfs.getPatternSeparator();
  }

  /**
   * This function is called to recompose() all of the decimal format properties elements.
   * 
   * @param root Stylesheet root
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeDecimalFormats(this);
  }

}
