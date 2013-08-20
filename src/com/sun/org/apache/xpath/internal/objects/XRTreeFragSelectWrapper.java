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
 * $Id: XRTreeFragSelectWrapper.java,v 1.11 2004/02/17 04:34:38 minchau Exp $
 */
package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

/**
 * This class makes an select statement act like an result tree fragment.
 */
public class XRTreeFragSelectWrapper extends XRTreeFrag implements Cloneable
{
  XObject m_selected;

  public XRTreeFragSelectWrapper(Expression expr)
  {
    super(expr);
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    ((Expression)m_obj).fixupVariables(vars, globalsSize);
  }
  
  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   *
   * @return the result of executing the select expression
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
     m_selected = ((Expression)m_obj).execute(xctxt);
     m_selected.allowDetachToRelease(m_allowRelease);
     if (m_selected.getType() == CLASS_STRING)
       return m_selected;
     else
       return new XString(m_selected.str());
  }
    
  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   * 
   * In general, detach should only be called once on the object.
   */
  public void detach()
  {
    if(m_allowRelease)
    {
      m_selected.detach();
      m_selected = null;
    }
    
    super.detach();
  }
  
  /**
   * Cast result object to a number.
   *
   * @return The result tree fragment as a number or NaN
   */
  public double num()
    throws javax.xml.transform.TransformerException
  {

    return m_selected.num();
  }

  
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    return m_selected.xstr();
  }

  /**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    return m_selected.str();
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return the string type
   */
  public int getType()
  {
    return CLASS_STRING;
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @return The document fragment this wraps
   */
  public int rtf()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"rtf() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to a DTMIterator.
   *
   * @return The document fragment as a DTMIterator
   */
  public DTMIterator asNodeIterator()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"asNodeIterator() not supported by XRTreeFragSelectWrapper!");
  }

}
