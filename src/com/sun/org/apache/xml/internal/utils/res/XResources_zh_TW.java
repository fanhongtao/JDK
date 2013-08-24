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
 * $Id: XResources_zh_TW.java,v 1.8 2004/02/17 04:22:15 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.res;

//
//  LangResources_en.properties
//

/**
 * The Chinese(Taiwan) resource bundle.
 * @xsl.usage internal
 */
public class XResources_zh_TW extends XResourceBundle
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

  /** The association list.         */
  static final Object[][] contents =
  {
    { "ui_language", "zh" }, { "help_language", "zh" }, { "language", "zh" },
    { "alphabet",
      new char[]{ 0xff21, 0xff22, 0xff23, 0xff24, 0xff25, 0xff26, 0xff27,
                  0xff28, 0xff29, 0xff2a, 0xff2b, 0xff2c, 0xff2d, 0xff2e,
                  0xff2f, 0xff30, 0xff31, 0xff32, 0xff33, 0xff34, 0xff35,
                  0xff36, 0xff37, 0xff38, 0xff39, 0xff3a } },
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

    // simplified chinese 
    { "zero", new char[]{ 0x96f6 } },

    //These only used for mutiplicative-additive numbering 
    { "multiplier", new long[]{ 100000000, 10000, 1000, 100, 10 } },
    { "multiplierChar",
      new char[]{ 0x5104, 0x842c, 0x4edf, 0x4f70, 0x62fe } },
    { "digits",
      new char[]{ 0x58f9, 0x8cb3, 0x53c3, 0x8086, 0x4f0d, 0x9678, 0x67d2,
                  0x634c, 0x7396 } }, { "tables", new String[]{ "digits" } }
  };
}
