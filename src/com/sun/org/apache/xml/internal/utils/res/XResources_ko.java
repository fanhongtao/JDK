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
 * $Id: XResources_ko.java,v 1.6 2004/02/17 04:22:15 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.res;

//
//  LangResources_ko.properties
//

/**
 * The Korean resource bundle.
 * @xsl.usage internal
 */
public class XResources_ko extends XResourceBundle
{

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  protected Object[][] getContents() {
      // return a copy of contents; in theory we want a deep clone
      // of contents, but since it only contains (immutable) Strings,
      // this shallow copy is sufficient
      Object[][] msgCopy = new Object[contents.length][2];
      for (int i = 0; i < contents.length; i++) {
          msgCopy[i][0] = contents[i][0];
          msgCopy[i][1] = contents[i][1];
      }
      return msgCopy;
  }

  /** The association list.          */
  static final Object[][] contents =
  {
    { "ui_language", "ko" }, { "help_language", "ko" }, { "language", "ko" },
    { "alphabet",
      new char[]{ 0x3131, 0x3134, 0x3137, 0x3139, 0x3141, 0x3142, 0x3145, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c,
                  0x314d, 0x314e, 0x314f, 0x3151, 0x3153, 0x3155, 0x3157, 0x315b, 0x315c, 0x3160, 0x3161, 0x3163}},
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
    //{"MaxNumericalValue", new Integer(100000000)},
    //These would not be used for EN. Only used for traditional numbering   
    { "numberGroups", new int[]{ 1 } },

    // chinese only ??
    { "zero", new char[0] },

    //These only used for mutiplicative-additive numbering
    { "multiplier", new int[]{ 100000000, 10000, 1000, 100, 10 } },
    { "multiplierChar",
      new char[]{  0xc5b5, 0xb9cc, 0xcc9c, 0xbc31, 0xc2ed } },
    { "digits",
      new char[]{ 0xc77c, 0xc774, 0xc0bc, 0xc0ac, 0xc624, 0xc721, 0xce60, 0xd314, 0xad6c
                   } }, { "tables", new String[]{ "digits" } }
  };
}
