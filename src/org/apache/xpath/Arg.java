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
package org.apache.xpath;

import org.apache.xml.utils.QName;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="internal"/>
 * This class holds an instance of an argument on
 * the stack. The value of the argument can be either an
 * XObject or a String containing an expression.
 */
public class Arg
{

  /** Field m_qname: The name of this argument, expressed as a QName
   * (Qualified Name) object.
   * @see getQName
   * @see setQName
   *  */
  private QName m_qname;

  /**
   * Get the qualified name for this argument.
   *
   * @return QName object containing the qualified name
   */
  public final QName getQName()
  {
    return m_qname;
  }

  /**
   * Set the qualified name for this argument.
   *
   * @param name QName object representing the new Qualified Name.
   */
  public final void setQName(QName name)
  {
    m_qname = name;
  }

  /** Field m_val: Stored XObject value of this argument
   * @see #getVal()
   * @see #setVal()
   */
  private XObject m_val;

  /**
   * Get the value for this argument.
   *
   * @return the argument's stored XObject value.
   * @see #setVal(XObject)
   */
  public final XObject getVal()
  {
    return m_val;
  }

  /**
   * Set the value of this argument.
   *
   * @param val an XObject representing the arguments's value.
   * @see #getVal()
   */
  public final void setVal(XObject val)
  {
    m_val = val;
  }
  
  /**
   * Have the object release it's resources.
   * Call only when the variable or argument is going out of scope.
   */
  public void detach()
  {
    if(null != m_val)
    {
      m_val.allowDetachToRelease(true);
      m_val.detach();
    }
  }


  /** Field m_expression: Stored expression value of this argument.
   * @see #setExpression
   * @see #getExpression
   * */
  private String m_expression;

  /**
   * Get the value expression for this argument.
   *
   * @return String containing the expression previously stored into this
   * argument
   * @see #setExpression
   */
  public String getExpression()
  {
    return m_expression;
  }

  /**
   * Set the value expression for this argument.
   *
   * @param expr String containing the expression to be stored as this
   * argument's value.
   * @see #getExpression
   */
  public void setExpression(String expr)
  {
    m_expression = expr;
  }

  /** 
   * True if this variable was added with an xsl:with-param or
   * is added via setParameter.
   */
  private boolean m_isFromWithParam;

  /**
   * Tell if this variable is a parameter passed with a with-param or as 
   * a top-level parameter.
   */
   public boolean isFromWithParam()
   {
    return m_isFromWithParam;
   }

  /** 
   * True if this variable is currently visible.  To be visible,
   * a variable needs to come either from xsl:variable or be 
   * a "received" parameter, ie one for which an xsl:param has
   * been encountered.
   * Set at the time the object is constructed and updated as needed.
   */
  private boolean m_isVisible;

  /**
   * Tell if this variable is currently visible.
   */
   public boolean isVisible()
   {
    return m_isVisible;
   }
   
  /**
   * Update visibility status of this variable.
   */
   public void setIsVisible(boolean b)
   {
    m_isVisible = b;
   }

  /**
   * Construct a dummy parameter argument, with no QName and no
   * value (either expression string or value XObject). isVisible
   * defaults to true.
   */
  public Arg()
  {

    m_qname = new QName("");
    ;  // so that string compares can be done.
    m_val = null;
    m_expression = null;
    m_isVisible = true;
    m_isFromWithParam = false;
  }

  /**
   * Construct a parameter argument that contains an expression.
   *
   * @param qname Name of the argument, expressed as a QName object.
   * @param expression String to be stored as this argument's value expression.
   * @param isFromWithParam True if this is a parameter variable.
   */
  public Arg(QName qname, String expression, boolean isFromWithParam)
  {

    m_qname = qname;
    m_val = null;
    m_expression = expression;
    m_isFromWithParam = isFromWithParam;
    m_isVisible = !isFromWithParam;
  }

  /**
   * Construct a parameter argument which has an XObject value.
   * isVisible defaults to true.
   *
   * @param qname Name of the argument, expressed as a QName object.
   * @param val Value of the argument, expressed as an XObject
   */
  public Arg(QName qname, XObject val)
  {

    m_qname = qname;
    m_val = val;
    m_isVisible = true;
    m_isFromWithParam = false;
    m_expression = null;
  }
  
  /**
   * Equality function specialized for the variable name.  If the argument 
   * is not a qname, it will deligate to the super class.
   * 
   * @param   obj   the reference object with which to compare.
   * @return  <code>true</code> if this object is the same as the obj
   *          argument; <code>false</code> otherwise.
   */
  public boolean equals(Object obj) 
  {
    if(obj instanceof QName)
    {
      return m_qname.equals(obj);
    }
    else
      return super.equals(obj);
  }

  /**
   * Construct a parameter argument.
   *
   * @param qname Name of the argument, expressed as a QName object.
   * @param val Value of the argument, expressed as an XObject
   * @param isFromWithParam True if this is a parameter variable.
   */
  public Arg(QName qname, XObject val, boolean isFromWithParam)
  {

    m_qname = qname;
    m_val = val;
    m_isFromWithParam = isFromWithParam;
    m_isVisible = !isFromWithParam;
    m_expression = null;
  }
}
