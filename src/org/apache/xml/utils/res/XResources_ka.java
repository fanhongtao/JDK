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
package org.apache.xml.utils.res;

import org.apache.xml.utils.res.XResourceBundle;

import java.util.*;

//
//  LangResources_en.properties
//

/**
 * <meta name="usage" content="internal"/>
 * The Georgian resource bundle.
 */
public class XResources_ka extends XResourceBundle
{

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }

  /** The association list.          */
  static final Object[][] contents =
  {
    { "ui_language", "ka" }, { "help_language", "ka" }, { "language", "ka" },
    { "alphabet",
      new char[]{ 0x10D0, 0x10D1, 0x10D2, 0x10D3, 0x10D4, 0x10D5, 0x10D6,
                  0x10f1, 0x10D7, 0x10D8, 0x10D9, 0x10DA, 0x10DB, 0x10DC,
                  0x10f2, 0x10DD, 0x10DE, 0x10DF, 0x10E0, 0x10E1, 0x10E2,
                  0x10E3, 0x10E4, 0x10E5, 0x10E6, 0x10E7, 0x10E8, 0x10E9,
                  0x10EA, 0x10EB, 0x10EC, 0x10ED, 0x10EE, 0x10F4, 0x10EF,
                  0x10F0 } },
    { "tradAlphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },

    //language orientation
    { "orientation", "LeftToRight" },

    //language numbering   
    { "numbering", "additive" },

    // largest numerical value
    //{"MaxNumericalValue", new Integer(10000000000)},
    //These would not be used for EN. Only used for traditional numbering   
    { "numberGroups", new int[]{ 1000, 100, 10, 1 } },

    //These used for additive numbering
    { "digits",
      new char[]{ 0x10D0, 0x10D1, 0x10D2, 0x10D3, 0x10D4, 0x10D5, 0x10D6,
                  0x10f1, 0x10D7 } },
    { "tens",
      new char[]{ 0x10D8, 0x10D9, 0x10DA, 0x10DB, 0x10DC, 0x10f2, 0x10DD,
                  0x10DE, 0x10DF } },
    { "hundreds",
      new char[]{ 0x10E0, 0x10E1, 0x10E2, 0x10E3, 0x10E4, 0x10E5, 0x10E6,
                  0x10E7, 0x10E8 } },
    { "thousands",
      new char[]{ 0x10E9, 0x10EA, 0x10EB, 0x10EC, 0x10ED, 0x10EE, 0x10F4,
                  0x10EF, 0x10F0 } },
    { "tables", new String[]{ "thousands", "hundreds", "tens", "digits" } }
  };
}
