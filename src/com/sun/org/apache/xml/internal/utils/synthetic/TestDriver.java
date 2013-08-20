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
 * $Id: TestDriver.java,v 1.6 2004/02/17 04:23:24 minchau Exp $
 */

/**
 * Test driver for com.sun.org.apache.xml.internal.utils.synthetic.Class and com.sun.org.apache.xml.internal.utils.synthetic.reflection.
 *   <p>
 *   toSource should probably be factored out into a separate
 *   java generator class, so we could generate other languages as well.
 */
package com.sun.org.apache.xml.internal.utils.synthetic;

import com.sun.org.apache.xml.internal.utils.synthetic.reflection.Method;

/**
 * Class TestDriver <needs-comment/>
 * @xsl.usage internal
 */
public class TestDriver
{

  /** Field sampleField          */
  public static int sampleField = 32;

  /** Field inTest          */
  private boolean inTest = false;

  /**
   * Method _main 
   *
   *
   * @param args
   */
  public static void _main(String[] args)
  {

    // Proxy a class
    try
    {
      System.out.println("Proxying java.awt.Frame...");

      Class myC = Class.forName("java.awt.Frame");

      myC.toSource(System.out, 0);
      System.out.println(
        "\nProxying com.sun.org.apache.xml.internal.utils.synthetic.TestDriver...");

      myC =
        Class.forName("com.ibm.com.sun.org.apache.xml.internal.utils.synthetic.TestDriver");

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
        "com.ibm.com.sun.org.apache.xml.internal.utils.synthetic.BuildMe");
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
   * Class Inner <needs-comment/>
   * @xsl.usage internal
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
