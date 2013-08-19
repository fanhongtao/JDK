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
 * The Japanese (Hiragana) resource bundle.
 */
public class XResources_ja_JP_HA extends XResourceBundle
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

  /** The association list.        */
  static final Object[][] contents =
  {
    { "ui_language", "ja" }, { "help_language", "ja" }, { "language", "ja" },
    { "alphabet",
      new char[]{ 0x3042, 0x3044, 0x3046, 0x3048, 0x304a, 0x304b, 0x304d,
                  0x304f, 0x3051, 0x3053, 0x3055, 0x3057, 0x3059, 0x305b,
                  0x305d, 0x305f, 0x3061, 0x3064, 0x3066, 0x3068, 0x306a,
                  0x306b, 0x306c, 0x306d, 0x306e, 0x306f, 0x3072, 0x3075,
                  0x3078, 0x307b, 0x307e, 0x307f, 0x3080, 0x3081, 0x3082,
                  0x3084, 0x3086, 0x3088, 0x3089, 0x308a, 0x308b, 0x308c,
                  0x308d, 0x308f, 0x3090, 0x3091, 0x3092, 0x3093 } },
    { "tradAlphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },

    //language orientation 
    { "orientation", "LeftToRight" },

    //language numbering   
    { "numbering", "multiplicative-additive" },
    { "multiplierOrder", "follows" },

    // largest numerical value
    //{"MaxNumericalValue", new Integer(10000000000)},
    //These would not be used for EN. Only used for traditional numbering   
    { "numberGroups", new int[]{ 1 } },

    //These only used for mutiplicative-additive numbering
    // Note that we are using longs and that the last two 
    // multipliers are not supported. This is a known limitation.
    { "multiplier",
      new long[]{ Long.MAX_VALUE, Long.MAX_VALUE, 100000000, 10000, 1000, 100, 10 } },
    { "multiplierChar",
      new char[]{ 0x4EAC, 0x5146, 0x5104, 0x4E07, 0x5343, 0x767e, 0x5341 } },

    // chinese only? 
    { "zero", new char[0] },
    { "digits",
      new char[]{ 0x4E00, 0x4E8C, 0x4E09, 0x56DB, 0x4E94, 0x516D, 0x4E03,
                  0x516B, 0x4E5D } }, { "tables", new String[]{ "digits" } }
  };
}
