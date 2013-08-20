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
 * $Id: FuncTranslate.java,v 1.8 2004/02/17 04:34:00 minchau Exp $
 */
package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;

/**
 * Execute the Translate() function.
 * @xsl.usage advanced
 */
public class FuncTranslate extends Function3Args
{

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    String theFirstString = m_arg0.execute(xctxt).str();
    String theSecondString = m_arg1.execute(xctxt).str();
    String theThirdString = m_arg2.execute(xctxt).str();
    int theFirstStringLength = theFirstString.length();
    int theThirdStringLength = theThirdString.length();

    // A vector to contain the new characters.  We'll use it to construct
    // the result string.
    StringBuffer sbuffer = new StringBuffer();

    for (int i = 0; i < theFirstStringLength; i++)
    {
      char theCurrentChar = theFirstString.charAt(i);
      int theIndex = theSecondString.indexOf(theCurrentChar);

      if (theIndex < 0)
      {

        // Didn't find the character in the second string, so it
        // is not translated.
        sbuffer.append(theCurrentChar);
      }
      else if (theIndex < theThirdStringLength)
      {

        // OK, there's a corresponding character in the
        // third string, so do the translation...
        sbuffer.append(theThirdString.charAt(theIndex));
      }
      else
      {

        // There's no corresponding character in the
        // third string, since it's shorter than the
        // second string.  In this case, the character
        // is removed from the output string, so don't
        // do anything.
      }
    }

    return new XString(sbuffer.toString());
  }
}
