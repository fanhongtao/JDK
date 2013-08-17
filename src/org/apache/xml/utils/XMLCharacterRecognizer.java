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

/**
 * <meta name="usage" content="internal"/>
 * Class used to verify whether the specified <var>ch</var> 
 * conforms to the XML 1.0 definition of whitespace. 
 */
public class XMLCharacterRecognizer
{

  /**
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * @param ch Character to check as XML whitespace.
   * @return =true if <var>ch</var> is XML whitespace; otherwise =false.
   */
  public static boolean isWhiteSpace(char ch)
  {
    return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
  }

  /**
   * Tell if the string is whitespace.
   *
   * @param ch Character array to check as XML whitespace.
   * @param start Start index of characters in the array
   * @param length Number of characters in the array 
   * @return True if the characters in the array are 
   * XML whitespace; otherwise, false.
   */
  public static boolean isWhiteSpace(char ch[], int start, int length)
  {

    int end = start + length;

    for (int s = start; s < end; s++)
    {
      if (!isWhiteSpace(ch[s]))
        return false;
    }

    return true;
  }

  /**
   * Tell if the string is whitespace.
   *
   * @param buf StringBuffer to check as XML whitespace.
   * @return True if characters in buffer are XML whitespace, false otherwise
   */
  public static boolean isWhiteSpace(StringBuffer buf)
  {

    int n = buf.length();

    for (int i = 0; i < n; i++)
    {
      if (!isWhiteSpace(buf.charAt(i)))
        return false;
    }

    return true;
  }
  
  /**
   * Tell if the string is whitespace.
   *
   * @param buf StringBuffer to check as XML whitespace.
   * @return True if characters in buffer are XML whitespace, false otherwise
   */
  public static boolean isWhiteSpace(String s)
  {

    if(null != s)
    {
      int n = s.length();
  
      for (int i = 0; i < n; i++)
      {
        if (!isWhiteSpace(s.charAt(i)))
          return false;
      }
    }

    return true;
  }

}
