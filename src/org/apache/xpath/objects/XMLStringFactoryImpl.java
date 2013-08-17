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

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xml.utils.FastStringBuffer;

/**
 * <meta name="usage" content="internal"/>
 * Class XMLStringFactoryImpl creates XString versions of XMLStrings.
 */
public class XMLStringFactoryImpl extends XMLStringFactory
{
  /** The XMLStringFactory to pass to DTM construction.   */
  private static XMLStringFactory m_xstringfactory =
    new XMLStringFactoryImpl();

  /**
   * Get the XMLStringFactory to pass to DTM construction.
   *
   *
   * @return A never-null static reference to a String factory.
   */
  public static XMLStringFactory getFactory()
  {
    return m_xstringfactory;
  }

  /**
   * Create a new XMLString from a Java string.
   *
   *
   * @param string Java String reference, which must be non-null.
   *
   * @return An XMLString object that wraps the String reference.
   */
  public XMLString newstr(String string)
  {
    return new XString(string);
  }

  /**
   * Create a XMLString from a FastStringBuffer.
   *
   *
   * @param string FastStringBuffer reference, which must be non-null.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @return An XMLString object that wraps the FastStringBuffer reference.
   */
  public XMLString newstr(FastStringBuffer fsb, int start, int length)
  {
    return new XStringForFSB(fsb, start, length);
  }
  
  /**
   * Create a XMLString from a FastStringBuffer.
   *
   *
   * @param string FastStringBuffer reference, which must be non-null.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @return An XMLString object that wraps the FastStringBuffer reference.
   */
  public XMLString newstr(char[] string, int start, int length)
  {
    return new XStringForChars(string, start, length);
  }
  
  /**
   * Get a cheap representation of an empty string.
   * 
   * @return An non-null reference to an XMLString that represents "".
   */
  public XMLString emptystr()
  {
    return XString.EMPTYSTRING;
  }

}
