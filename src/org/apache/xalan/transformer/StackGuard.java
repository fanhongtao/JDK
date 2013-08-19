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
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.ObjectStack;

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
  
  TransformerImpl m_transformer;

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

  /**
   * Constructor StackGuard
   *
   *
   * @param xslTemplate Current template node
   * @param sourceXML Source Node
   */
  public StackGuard(TransformerImpl transformerImpl)
  {
    m_transformer = transformerImpl;
  }

  /**
   * Overide equal method for StackGuard objects 
   *
   *
   * @param obj StackGuard object to compare
   *
   * @return True if the given object matches this StackGuard object
   */
  public int countLikeTemplates(ElemTemplate templ, int pos)
  {
  	ObjectStack elems = m_transformer.getCurrentTemplateElements();
  	int count = 1;
    for (int i = pos-1; i >= 0; i--)
    {
    	if((ElemTemplateElement)elems.elementAt(i) == templ)
    		count++;
    }
	
    return count;
  }

  
  /**
   * Get the next named or match template down from and including 
   * the given position.
   * @param pos the current index position in the stack.
   * @return null if no matched or named template found, otherwise 
   * the next named or matched template at or below the position.
   */
  private ElemTemplate getNextMatchOrNamedTemplate(int pos)
  {
  	ObjectStack elems = m_transformer.getCurrentTemplateElements();
    for (int i = pos; i >= 0; i--)
    {
    	ElemTemplateElement elem = (ElemTemplateElement) elems.elementAt(i);
    	if(null != elem)
    	{
	    	if(elem.getXSLToken() == Constants.ELEMNAME_TEMPLATE)
	    	{
	    		return (ElemTemplate)elem;
	    	}
    	}
    }
  	return null;
  }

  /**
   * Check if we are in an infinite loop
   *
   *
   * @param guard Current StackGuard object (matching current template)  
   *
   * @throws TransformerException
   */
  public void checkForInfinateLoop() throws TransformerException
  {
    int nTemplates = m_transformer.getCurrentTemplateElementsCount();
    if(nTemplates < m_recursionLimit)
    	return;
    	
    if(m_recursionLimit <= 0)
    	return;  // Safety check.
    	
    // loop from the top index down to the recursion limit (I don't think 
    // there's any need to go below that).
    for (int i = (nTemplates - 1); i >= m_recursionLimit; i--)
    {
    	ElemTemplate template = getNextMatchOrNamedTemplate(i);
    	
    	if(null == template)
    		break;
    		
    	int loopCount = countLikeTemplates(template, i);
    	
    	if (loopCount >= m_recursionLimit)
    	{
    		// throw new TransformerException("Template nesting too deep. nesting = "+loopCount+
    		//   ", template "+((null == template.getName()) ? "name = " : "match = ")+
    		//   ((null != template.getName()) ? template.getName().toString() 
    		//   : template.getMatch().getPatternString()));
    		
    		String idIs = XSLMessages.createMessage(((null != template.getName()) ? "nameIs" : "matchPatternIs"), null);
        	Object[] msgArgs = new Object[]{ new Integer(loopCount), idIs, 
                     ((null != template.getName()) ? template.getName().toString() 
    		   : template.getMatch().getPatternString()) };
        	String msg = XSLMessages.createMessage("recursionTooDeep", msgArgs);

    		throw new TransformerException(msg);
    	}
    }
  }

}
