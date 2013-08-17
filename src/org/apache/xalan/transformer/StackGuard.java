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
package org.apache.xalan.transformer;

//import org.w3c.dom.Node;
//import org.w3c.dom.Text;
//import org.w3c.dom.Element;
import org.apache.xml.dtm.DTM;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;

/**
 * Class to guard against recursion getting too deep.
 */
public class StackGuard
{

  /**
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting
   * this variable, if the number is too low, it may report an
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.
   */
  public static int m_recursionLimit = -1;

  /**
   * Get the recursion limit.
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting
   * this variable, if the number is too low, it may report an
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.
   *
   * @return The recursion limit.
   */
  public int getRecursionLimit()
  {
    return m_recursionLimit;
  }

  /**
   * Set the recursion limit.
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting
   * this variable, if the number is too low, it may report an
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.
   *
   * @param limit The recursion limit.
   */
  public void setRecursionLimit(int limit)
  {
    m_recursionLimit = limit;
  }

  /** Stylesheet Template node          */
  ElemTemplateElement m_xslRule;

  /** Source node          */
  int m_sourceXML;

  /** Stack where ElemTempalteElements will be pushed          */
  java.util.Stack stack = new java.util.Stack();

  /**
   * Constructor StackGuard
   *
   */
  public StackGuard(){}

  /**
   * Constructor StackGuard
   *
   *
   * @param xslTemplate Current template node
   * @param sourceXML Source Node
   */
  public StackGuard(ElemTemplateElement xslTemplate, int sourceXML)
  {
    m_xslRule = xslTemplate;
    m_sourceXML = sourceXML;
  }

  /**
   * Overide equal method for StackGuard objects 
   *
   *
   * @param obj StackGuard object to compare
   *
   * @return True if the given object matches this StackGuard object
   */
  public boolean equals(Object obj)
  {

    if (((StackGuard) obj).m_xslRule.equals(m_xslRule)
            && ((StackGuard) obj).m_sourceXML == m_sourceXML)
    {
      return true;
    }

    return false;
  }

  /**
   * Output diagnostics if in an infinite loop
   *
   *
   * @param pw Non-null PrintWriter instance to use
   */
  public void print(PrintWriter pw)
  {

    // for the moment, these diagnostics are really bad...
    // %DTBD% We need an execution context.
//    if (m_sourceXML instanceof Text)
//    {
//      Text tx = (Text) m_sourceXML;
//
//      pw.println(tx.getData());
//    }
//    else if (m_sourceXML instanceof Element)
//    {
//      Element elem = (Element) m_sourceXML;
//
//      pw.println(elem.getNodeName());
//    }
  }

  /**
   * Check if we are in an infinite loop
   *
   *
   * @param guard Current StackGuard object (matching current template)  
   *
   * @throws TransformerException
   */
  public void checkForInfinateLoop(StackGuard guard) throws TransformerException
  {

    int nRules = stack.size();
    int loopCount = 0;

    for (int i = (nRules - 1); i >= 0; i--)
    {
      if (stack.elementAt(i).equals(guard))
      {
        loopCount++;
      }

      if (loopCount >= m_recursionLimit)
      {

        // Print out really bad diagnostics.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("Infinite loop diagnosed!  Stack trace:");

        int k;

        for (k = 0; k < nRules; k++)
        {
          pw.println("Source Elem #" + k + " ");

          StackGuard guardOnStack = (StackGuard) stack.elementAt(i);

          guardOnStack.print(pw);
        }

        pw.println("Source Elem #" + k + " ");
        guard.print(pw);
        pw.println("End of infinite loop diagnosis.");

        throw new TransformerException(sw.getBuffer().toString());
      }
    }
  }

  /**
   * Push in a StackGuard object mathing given template 
   *
   *
   * @param xslTemplate Current template being processed
   * @param sourceXML Current Source Node 
   *
   * @throws TransformerException
   */
  public void push(ElemTemplateElement xslTemplate, int sourceXML)
          throws TransformerException
  {

    StackGuard guard = new StackGuard(xslTemplate, sourceXML);

    checkForInfinateLoop(guard);
    stack.push(guard);
  }

  /**
   * Pop out Stack of StackGuard objects 
   *
   */
  public void pop()
  {
    stack.pop();
  }
}
