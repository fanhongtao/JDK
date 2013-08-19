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



/**
 * <meta name="usage" content="internal"/>
 * The Cyrillic resource bundle.
 */
public class XResources_cy extends XResourceBundle
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

 /** The association list. */
  static final Object[][] contents =
  {
    { "ui_language", "cy" }, { "help_language", "cy" }, { "language", "cy" },
    { "alphabet",
      new char[]{ 0x0430, 0x0432, 0x0433, 0x0434, 0x0435, 0x0437, 0x0438,
                  0x0439, 0x04A9, 0x0457, 0x043A, 0x043B, 0x043C, 0x043D,
                  0x046F, 0x043E, 0x043F, 0x0447, 0x0440, 0x0441, 0x0442,
                  0x0443, 0x0444, 0x0445, 0x0470, 0x0460, 0x0446 } },
    { "tradAlphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },

    //language orientation
    { "orientation", "LeftToRight" },

    //language numbering 
    //{"numbering", "additive"},
    { "numbering", "multiplicative-additive" },
    { "multiplierOrder", "precedes" },

    // largest numerical value
    //{"MaxNumericalValue", new Integer(10000000000)},
    //These would not be used for EN. Only used for traditional numbering   
    { "numberGroups", new int[]{ 100, 10, 1 } },

    //These only used for mutiplicative-additive numbering
    { "multiplier", new long[]{ 1000 } },
    { "multiplierChar", new char[]{ 0x03D9 } },

    // chinese only??
    { "zero", new char[0] },

    //{"digits", new char[]{'a','b','c','d','e','f','g','h','i'}},
    { "digits",
      new char[]{ 0x0430, 0x0432, 0x0433, 0x0434, 0x0435, 0x0437, 0x0438,
                  0x0439, 0x04A9 } },
    { "tens",
      new char[]{ 0x0457, 0x043A, 0x043B, 0x043C, 0x043D, 0x046F, 0x043E,
                  0x043F, 0x0447 } },
    { "hundreds",
      new char[]{ 0x0440, 0x0441, 0x0442, 0x0443, 0x0444, 0x0445, 0x0470,
                  0x0460, 0x0446 } },
    { "tables", new String[]{ "hundreds", "tens", "digits" } }
  };
}
