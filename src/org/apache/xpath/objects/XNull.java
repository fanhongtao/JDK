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

//import org.w3c.dom.traversal.NodeIterator;
//import org.w3c.dom.DocumentFragment;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSetDTM;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath null object, and is capable of
 * converting the null to other types, such as a string.
 */
public class XNull extends XNodeSet
{

  /**
   * Create an XObject.
   */
  public XNull()
  {
    super();
  }

  /**
   * Tell what kind of class this is.
   *
   * @return type CLASS_NULL
   */
  public int getType()
  {
    return CLASS_NULL;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#CLASS_NULL"
   */
  public String getTypeString()
  {
    return "#CLASS_NULL";
  }

  /**
   * Cast result object to a number.
   * 
   * @return 0.0
   */

  public double num()
  {
    return 0.0;
  }

  /**
   * Cast result object to a boolean.
   *
   * @return false
   */
  public boolean bool()
  {
    return false;
  }

  /**
   * Cast result object to a string.
   *
   * @return empty string ""
   */
  public String str()
  {
    return "";
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @param support XPath context to use for the conversion
   *
   * @return The object as a result tree fragment.
   */
  public int rtf(XPathContext support)
  {
    // DTM frag = support.createDocumentFragment();
    // %REVIEW%
    return DTM.NULL;
  }

//  /**
//   * Cast result object to a nodelist.
//   *
//   * @return null
//   */
//  public DTMIterator iter()
//  {
//    return null;
//  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if the given object is of type CLASS_NULL
   */
  public boolean equals(XObject obj2)
  {
    return obj2.getType() == CLASS_NULL;
  }
}
