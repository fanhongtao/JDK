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

import org.apache.xml.utils.StringToIntTable;

/**
 * <meta name="usage" content="internal"/>
 * This class is in support of SerializerToHTML, and acts as a sort
 * of element representative for HTML elements.
 */
class ElemDesc
{

  /** Bit flags to tell about this element type. */
  int m_flags;

  /**
   * Table of attribute names to integers, which contain bit flags telling about
   *  the attributes.
   */
  StringToIntTable m_attrs = null;

  /** Bit position if this element type is empty. */
  static final int EMPTY = (1 << 1);

  /** Bit position if this element type is a flow. */
  static final int FLOW = (1 << 2);

  /** Bit position if this element type is a block. */
  static final int BLOCK = (1 << 3);

  /** Bit position if this element type is a block form. */
  static final int BLOCKFORM = (1 << 4);

  /** Bit position if this element type is a block form field set (?? -sb). */
  static final int BLOCKFORMFIELDSET = (1 << 5);

  /** Bit position if this element type is CDATA. */
  static final int CDATA = (1 << 6);

  /** Bit position if this element type is PCDATA. */
  static final int PCDATA = (1 << 7);

  /** Bit position if this element type is should be raw characters. */
  static final int RAW = (1 << 8);

  /** Bit position if this element type should be inlined. */
  static final int INLINE = (1 << 9);

  /** Bit position if this element type is INLINEA (?? -sb). */
  static final int INLINEA = (1 << 10);

  /** Bit position if this element type is an inline label. */
  static final int INLINELABEL = (1 << 11);

  /** Bit position if this element type is a font style. */
  static final int FONTSTYLE = (1 << 12);

  /** Bit position if this element type is a phrase. */
  static final int PHRASE = (1 << 13);

  /** Bit position if this element type is a form control. */
  static final int FORMCTRL = (1 << 14);

  /** Bit position if this element type is ???. */
  static final int SPECIAL = (1 << 15);

  /** Bit position if this element type is ???. */
  static final int ASPECIAL = (1 << 16);

  /** Bit position if this element type is an odd header element. */
  static final int HEADMISC = (1 << 17);

  /** Bit position if this element type is a head element (i.e. H1, H2, etc.) */
  static final int HEAD = (1 << 18);

  /** Bit position if this element type is a list. */
  static final int LIST = (1 << 19);

  /** Bit position if this element type is a preformatted type. */
  static final int PREFORMATTED = (1 << 20);

  /** Bit position if this element type is whitespace sensitive. */
  static final int WHITESPACESENSITIVE = (1 << 21);

  /** Bit position if this element type is a header element (i.e. HEAD). */
  static final int HEADELEM = (1 << 22);

  /** Bit position if this attribute type is a URL. */
  static final int ATTRURL = (1 << 1);

  /** Bit position if this attribute type is an empty type. */
  static final int ATTREMPTY = (1 << 2);

  /**
   * Construct an ElemDesc from a set of bit flags.
   *
   *
   * @param flags Bit flags that describe the basic properties of this element type.
   */
  ElemDesc(int flags)
  {
    m_flags = flags;
  }

  /**
   * Tell if this element type has the basic bit properties that are passed
   * as an argument.
   *
   * @param flags Bit flags that describe the basic properties of interest.
   *
   * @return true if any of the flag bits are true.
   */
  boolean is(int flags)
  {

    // int which = (m_flags & flags);
    return (m_flags & flags) != 0;
  }

  /**
   * Set an attribute name and it's bit properties.
   *
   *
   * @param name non-null name of attribute, in upper case.
   * @param flags flag bits.
   */
  void setAttr(String name, int flags)
  {

    if (null == m_attrs)
      m_attrs = new StringToIntTable();

    m_attrs.put(name, flags);
  }

  /**
   * Tell if any of the bits of interest are set for a named attribute type.
   *
   * @param name non-null reference to attribute name, in any case.
   * @param flags flag mask.
   *
   * @return true if any of the flags are set for the named attribute.
   */
  boolean isAttrFlagSet(String name, int flags)
  {
    return (null != m_attrs) ? ((m_attrs.getIgnoreCase(name) & flags) != 0) : false;
  }
}
