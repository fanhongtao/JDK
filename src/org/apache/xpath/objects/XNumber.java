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
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath number, and is capable of
 * converting the number to other types, such as a string.
 */
public class XNumber extends XObject
{

  /** Value of the XNumber object.
   *  @serial         */
  double m_val;

  /**
   * Construct a XNodeSet object.
   *
   * @param d Value of the object
   */
  public XNumber(double d)
  {
    super();

    m_val = d;
  }
  
  /**
   * Construct a XNodeSet object.
   *
   * @param d Value of the object
   */
  public XNumber(Number num)
  {

    super();

    m_val = num.doubleValue();
    m_obj = num;
  }

  /**
   * Tell that this is a CLASS_NUMBER.
   *
   * @return node type CLASS_NUMBER 
   */
  public int getType()
  {
    return CLASS_NUMBER;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#NUMBER" 
   */
  public String getTypeString()
  {
    return "#NUMBER";
  }

  /**
   * Cast result object to a number.
   *
   * @return the value of the XNumber object
   */
  public double num()
  {
    return m_val;
  }
  
  /**
   * Evaluate expression to a number.
   *
   * @return 0.0
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt) 
    throws javax.xml.transform.TransformerException
  {

    return m_val;
  }

  /**
   * Cast result object to a boolean.
   *
   * @return false if the value is NaN or equal to 0.0
   */
  public boolean bool()
  {
    return (Double.isNaN(m_val) || (m_val == 0.0)) ? false : true;
  }

//  /**
//   * Cast result object to a string.
//   *
//   * @return "NaN" if the number is NaN, Infinity or -Infinity if
//   * the number is infinite or the string value of the number.
//   */
//  private static final int PRECISION = 16;
//  public String str()
//  {
//
//    if (Double.isNaN(m_val))
//    {
//      return "NaN";
//    }
//    else if (Double.isInfinite(m_val))
//    {
//      if (m_val > 0)
//        return "Infinity";
//      else
//        return "-Infinity";
//    }
//
//    long longVal = (long)m_val;
//    if ((double)longVal == m_val)
//      return Long.toString(longVal);
//
//
//    String s = Double.toString(m_val);
//    int len = s.length();
//
//    if (s.charAt(len - 2) == '.' && s.charAt(len - 1) == '0')
//    {
//      return s.substring(0, len - 2);
//    }
//
//    int exp = 0;
//    int e = s.indexOf('E');
//    if (e != -1)
//    {
//      exp = Integer.parseInt(s.substring(e + 1));
//      s = s.substring(0,e);
//      len = e;
//    }
//
//    // Calculate Significant Digits:
//    // look from start of string for first digit
//    // look from end for last digit
//    // significant digits = end - start + (0 or 1 depending on decimal location)
//
//    int decimalPos = -1;
//    int start = (s.charAt(0) == '-') ? 1 : 0;
//    findStart: for( ; start < len; start++ )
//    {
//      switch (s.charAt(start))
//      {
//      case '0':
//        break;
//      case '.':
//        decimalPos = start;
//        break;
//      default:
//        break findStart;
//      }
//    }
//    int end = s.length() - 1;
//    findEnd: for( ; end > start; end-- )
//    {
//      switch (s.charAt(end))
//      {
//      case '0':
//        break;
//      case '.':
//        decimalPos = end;
//        break;
//      default:
//        break findEnd;
//      }
//    }
//
//    int sigDig = end - start;
//
//    // clarify decimal location if it has not yet been found
//    if (decimalPos == -1)
//      decimalPos = s.indexOf('.');
//
//    // if decimal is not between start and end, add one to sigDig
//    if (decimalPos < start || decimalPos > end)
//      ++sigDig;
//
//    // reduce significant digits to PRECISION if necessary
//    if (sigDig > PRECISION)
//    {
//      // re-scale BigDecimal in order to get significant digits = PRECISION
//      BigDecimal num = new BigDecimal(s);
//      int newScale = num.scale() - (sigDig - PRECISION);
//      if (newScale < 0)
//        newScale = 0;
//      s = num.setScale(newScale, BigDecimal.ROUND_HALF_UP).toString();
//
//      // remove trailing '0's; keep track of decimalPos
//      int truncatePoint = s.length();
//      while (s.charAt(--truncatePoint) == '0')
//        ;
//
//      if (s.charAt(truncatePoint) == '.')
//      {
//        decimalPos = truncatePoint;
//      }
//      else
//      {
//        decimalPos = s.indexOf('.');
//        truncatePoint += 1;
//      }
//
//      s = s.substring(0, truncatePoint);
//      len = s.length();
//    }
//
//    // Account for exponent by adding zeros as needed 
//    // and moving the decimal place
//
//    if (exp == 0)
//       return s;
//
//    start = 0;
//    String sign;
//    if (s.charAt(0) == '-')
//    {
//      sign = "-";
//      start++;
//    }
//    else
//      sign = "";
//
//    String wholePart = s.substring(start, decimalPos);
//    String decimalPart = s.substring(decimalPos + 1);
//
//    // get the number of digits right of the decimal
//    int decimalLen = decimalPart.length();
//
//    if (exp >= decimalLen)
//      return sign + wholePart + decimalPart + zeros(exp - decimalLen);
//
//    if (exp > 0)
//      return sign + wholePart + decimalPart.substring(0, exp) + "."
//             + decimalPart.substring(exp);
//
//    return sign + "0." + zeros(-1 - exp) + wholePart + decimalPart;
//  }

  /**
   * Cast result object to a string.
   *
   * @return "NaN" if the number is NaN, Infinity or -Infinity if
   * the number is infinite or the string value of the number.
   */
  public String str()
  {

    if (Double.isNaN(m_val))
    {
      return "NaN";
    }
    else if (Double.isInfinite(m_val))
    {
      if (m_val > 0)
        return "Infinity";
      else
        return "-Infinity";
    }

    double num = m_val;
    String s = Double.toString(num);
    int len = s.length();

    if (s.charAt(len - 2) == '.' && s.charAt(len - 1) == '0')
    {
      s = s.substring(0, len - 2);

      if (s.equals("-0"))
        return "0";

      return s;
    }

    int e = s.indexOf('E');

    if (e < 0)
      return s;

    int exp = Integer.parseInt(s.substring(e + 1));
    String sign;

    if (s.charAt(0) == '-')
    {
      sign = "-";
      s = s.substring(1);

      --e;
    }
    else
      sign = "";

    int nDigits = e - 2;

    if (exp >= nDigits)
      return sign + s.substring(0, 1) + s.substring(2, e)
             + zeros(exp - nDigits);

    if (exp > 0)
      return sign + s.substring(0, 1) + s.substring(2, 2 + exp) + "."
             + s.substring(2 + exp, e);

    return sign + "0." + zeros(-1 - exp) + s.substring(0, 1)
           + s.substring(2, e);
  }


  /**
   * Return a string of '0' of the given length
   *
   *
   * @param n Length of the string to be returned
   *
   * @return a string of '0' with the given length
   */
  static private String zeros(int n)
  {
    if (n < 1)
      return "";

    char[] buf = new char[n];

    for (int i = 0; i < n; i++)
    {
      buf[i] = '0';
    }

    return new String(buf);
  }

  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The value of this XNumber as a Double object
   */
  public Object object()
  {
    if(null == m_obj)
      m_obj = new Double(m_val);
    return m_obj;
  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return true if the two objects are equal 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    int t = obj2.getType();
    try
    {
	    if (t == XObject.CLASS_NODESET)
	      return obj2.equals(this);
	    else if(t == XObject.CLASS_BOOLEAN)
	      return obj2.bool() == bool();
		else
	       return m_val == obj2.num();
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }
  
  /**
   * Tell if this expression returns a stable number that will not change during 
   * iterations within the expression.  This is used to determine if a proximity 
   * position predicate can indicate that no more searching has to occur.
   * 
   *
   * @return true if the expression represents a stable number.
   */
  public boolean isStableNumber()
  {
    return true;
  }
  
  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	visitor.visitNumberLiteral(owner, this);
  }


}
