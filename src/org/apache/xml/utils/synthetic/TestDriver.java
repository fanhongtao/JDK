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

/**
 * Test driver for org.apache.xml.utils.synthetic.Class and org.apache.xml.utils.synthetic.reflection.
 *   <p>
 *   toSource should probably be factored out into a separate
 *   java generator class, so we could generate other languages as well.
 */
package org.apache.xml.utils.synthetic;

import org.apache.xml.utils.synthetic.Class;
import org.apache.xml.utils.synthetic.reflection.*;

/**
 * <meta name="usage" content="internal"/>
 * Class TestDriver <needs-comment/>
 */
public class TestDriver
{

  /** Field sampleField          */
  public static int sampleField = 32;

  /** Field inTest          */
  private boolean inTest = false;

  /**
   * Method main 
   *
   *
   * @param args
   */
  public static void main(String[] args)
  {

    // Proxy a class
    try
    {
      System.out.println("Proxying java.awt.Frame...");

      Class myC = Class.forName("java.awt.Frame");

      myC.toSource(System.out, 0);
      System.out.println(
        "\nProxying org.apache.xml.utils.synthetic.TestDriver...");

      myC =
        Class.forName("com.ibm.org.apache.xml.utils.synthetic.TestDriver");

      myC.toSource(System.out, 0);
    }
    catch (ClassNotFoundException e)
    {
      System.out.println("Couldn't proxy: ");
      e.printStackTrace();
    }

    // Start getting serious
    try
    {
      System.out.println("\nBuild a new beast...");

      Class myC = Class.declareClass(
        "com.ibm.org.apache.xml.utils.synthetic.BuildMe");
      Class inner = myC.declareInnerClass("island");

      inner.addExtends(Class.forName("java.lang.String"));

      Method m = inner.declareMethod("getValue");

      m.setReturnType(Class.forName("java.lang.String"));
      m.getBody().append("return toString();");
      myC.toSource(System.out, 0);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SynthesisException e)
    {
      e.printStackTrace();
    }
    catch (IllegalStateException e)
    {
      System.out.println("Unwritten function: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Method dumpClass 
   *
   *
   * @param C
   */
  public static void dumpClass(Class C)
  {

    System.out.println("toString(): " + C);
    System.out.println("\tisPrimitive(): " + C.isPrimitive());
    System.out.println("\tisInterface(): " + C.isInterface());
    System.out.println("\tisInstance(\"foo\"): " + C.isInstance("foo"));
    System.out.println("\tisArray(): " + C.isArray());
    System.out.println("\tgetRealClass(): " + C.getRealClass());
  }

  /**
   * Method quickcheck 
   *
   */
  public void quickcheck()
  {

    Inner a = new Inner();

    a.setTest(!a.getTest());
  }

  /**
   * <meta name="usage" content="internal"/>
   * Class Inner <needs-comment/>
   */
  private class Inner
  {

    /**
     * Method getTest 
     *
     *
     * @return
     */
    public boolean getTest()
    {
      return inTest;
    }

    /**
     * Method setTest 
     *
     *
     * @param test
     */
    public void setTest(boolean test)
    {
      inTest = test;
    }
  }
}
