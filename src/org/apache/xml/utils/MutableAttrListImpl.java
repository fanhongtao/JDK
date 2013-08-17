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
package org.apache.xml.utils;

import org.apache.xml.utils.StringVector;

import org.xml.sax.Attributes;

import java.io.Serializable;

import org.xml.sax.helpers.AttributesImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Mutable version of AttributesImpl.
 */
public class MutableAttrListImpl extends AttributesImpl
        implements Serializable
{

/**
 * Construct a new, empty AttributesImpl object.
 */

public MutableAttrListImpl()
  {
    super();
  }

  /**
   * Copy an existing Attributes object.
   *
   * <p>This constructor is especially useful inside a start
   * element event.</p>
   *
   * @param atts The existing Attributes object.
   */
  public MutableAttrListImpl(Attributes atts)
  {
    super(atts);
  }

  /**
   * Add an attribute to the end of the list.
   *
   * <p>For the sake of speed, this method does no checking
   * to see if the attribute is already in the list: that is
   * the responsibility of the application.</p>
   *
   * @param uri The Namespace URI, or the empty string if
   *        none is available or Namespace processing is not
   *        being performed.
   * @param localName The local name, or the empty string if
   *        Namespace processing is not being performed.
   * @param qName The qualified (prefixed) name, or the empty string
   *        if qualified names are not available.
   * @param type The attribute type as a string.
   * @param value The attribute value.
   */
  public void addAttribute(String uri, String localName, String qName,
                           String type, String value)
  {

    if (null == uri)
      uri = "";

    // getIndex(qName) seems to be more reliable than getIndex(uri, localName), 
    // in the case of the xmlns attribute anyway.
    int index = this.getIndex(qName);
    // int index = this.getIndex(uri, localName);
   
    // System.out.println("MutableAttrListImpl#addAttribute: "+uri+":"+localName+", "+index+", "+qName+", "+this);

    if (index >= 0)
      this.setAttribute(index, uri, localName, qName, type, value);
    else
      super.addAttribute(uri, localName, qName, type, value);
  }

  /**
   * Add the contents of the attribute list to this list.
   *
   * @param atts List of attributes to add to this list
   */
  public void addAttributes(Attributes atts)
  {

    int nAtts = atts.getLength();

    for (int i = 0; i < nAtts; i++)
    {
      String uri = atts.getURI(i);

      if (null == uri)
        uri = "";

      String localName = atts.getLocalName(i);
      String qname = atts.getQName(i);
      int index = this.getIndex(uri, localName);
      // System.out.println("MutableAttrListImpl#addAttributes: "+uri+":"+localName+", "+index+", "+atts.getQName(i)+", "+this);
      if (index >= 0)
        this.setAttribute(index, uri, localName, qname, atts.getType(i),
                          atts.getValue(i));
      else
        addAttribute(uri, localName, qname, atts.getType(i),
                     atts.getValue(i));
    }
  }

  /**
   * Return true if list contains the given (raw) attribute name.
   *
   * @param name Raw name of attribute to look for 
   *
   * @return true if an attribute is found with this name
   */
  public boolean contains(String name)
  {
    return getValue(name) != null;
  }
}

// end of MutableAttrListImpl.java
