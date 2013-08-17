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
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;

import org.w3c.dom.Node;

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * The variable reference expression executer.
 */
public class Variable extends Expression
{
  /** Tell if fixupVariables was called.
   *  @serial   */
  private boolean m_fixUpWasCalled = false;

  /** The qualified name of the variable.
   *  @serial   */
  protected QName m_qname;
  
  /**
   * The index of the variable, which is either an absolute index to a 
   * global, or, if higher than the globals area, must be adjusted by adding 
   * the offset to the current stack frame.
   */
  protected int m_index;
  
  protected boolean m_isGlobal = false;
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_fixUpWasCalled = true;
    int sz = vars.size();
    for (int i = vars.size()-1; i >= 0; i--) 
    {
      QName qn = (QName)vars.elementAt(i);
      // System.out.println("qn: "+qn);
      if(qn.equals(m_qname))
      {
        
        if(i < globalsSize)
        {
          m_isGlobal = true;
          m_index = i;
        }
        else
        {
          m_index = i-globalsSize;
        }
          
        return;
      }
    }
    java.lang.String msg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULD_NOT_FIND_VAR, 
                                             new Object[]{m_qname.toString()});
                                             
    TransformerException te = new TransformerException(msg, m_slocator);
                                             
    throw new org.apache.xml.utils.WrappedRuntimeException(te);
    
  }


  /**
   * Set the qualified name of the variable.
   *
   * @param qname Must be a non-null reference to a qualified name.
   */
  public void setQName(QName qname)
  {
    m_qname = qname;
  }

  /**
   * Dereference the variable, and return the reference value.  Note that lazy 
   * evaluation will occur.  If a variable within scope is not found, a warning 
   * will be sent to the error listener, and an empty nodeset will be returned.
   *
   *
   * @param xctxt The runtime execution context.
   *
   * @return The evaluated variable, or an empty nodeset if not found.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    // Is the variable fetched always the same?
    // XObject result = xctxt.getVariable(m_qname);
    if(m_fixUpWasCalled)
    {    
      XObject result;
      if(m_isGlobal)
        result = xctxt.getVarStack().getGlobalVariable(xctxt, m_index);
      else
        result = xctxt.getVarStack().getLocalVariable(xctxt, m_index);
  
      if (null == result)
      {
        // This should now never happen...
        warn(xctxt, XPATHErrorResources.WG_ILLEGAL_VARIABLE_REFERENCE,
             new Object[]{ m_qname.getLocalPart() });  //"VariableReference given for variable out "+
  //      (new RuntimeException()).printStackTrace();
  //      error(xctxt, XPATHErrorResources.ER_COULDNOT_GET_VAR_NAMED,
  //            new Object[]{ m_qname.getLocalPart() });  //"Could not get variable named "+varName);
        
        result = new XNodeSet(xctxt.getDTMManager());
      }
  
      return result;
    }
    else
    {
      // Hack city... big time.  This is needed to evaluate xpaths from extensions, 
      // pending some bright light going off in my head.  Some sort of callback?
      synchronized(this)
      {
        org.apache.xml.utils.PrefixResolver prefixResolver = xctxt.getNamespaceContext();

        // Get the current ElemTemplateElement, which must be pushed in as the 
        // prefix resolver, and then walk backwards in document order, searching 
        // for an xsl:param element or xsl:variable element that matches our 
        // qname.  If we reach the top level, use the StylesheetRoot's composed
        // list of top level variables and parameters.

        if (prefixResolver instanceof org.apache.xalan.templates.ElemTemplateElement)
        {

          org.apache.xalan.templates.ElemVariable vvar;

          org.apache.xalan.templates.ElemTemplateElement prev = 
            (org.apache.xalan.templates.ElemTemplateElement) prefixResolver;

          if (!(prev instanceof org.apache.xalan.templates.Stylesheet))
          {            
            while ( !(prev.getParentNode() instanceof org.apache.xalan.templates.Stylesheet) )
            {
              org.apache.xalan.templates.ElemTemplateElement savedprev = prev;

              while (null != (prev = prev.getPreviousSiblingElem()))
              {
                if(prev instanceof org.apache.xalan.templates.ElemVariable)
                {
                  vvar = (org.apache.xalan.templates.ElemVariable) prev;
                
                  if (vvar.getName().equals(m_qname))
                  {
                    m_index = vvar.getIndex();
                    m_isGlobal = false;
                    m_fixUpWasCalled = true;
                    return execute(xctxt);
                  }
                }
              }
              prev = savedprev.getParentElem();
            }
          }

          vvar = prev.getStylesheetRoot().getVariableOrParamComposed(m_qname);
          if (null != vvar)
          {
            m_index = vvar.getIndex();
            m_isGlobal = true;
            m_fixUpWasCalled = true;
            return execute(xctxt);
          }

        }
      }
      throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_VAR_NOT_RESOLVABLE, new Object[]{m_qname.toString()})); //"Variable not resolvable: "+m_qname);
    }
  }
  
  /**
   * Tell if this expression returns a stable number that will not change during 
   * iterations within the expression.  This is used to determine if a proximity 
   * position predicate can indicate that no more searching has to occur.
   * 
   *
   * @return true if the expression represents a stable number.
   */
  public boolean isStableNumber()
  {
    return true;
  }

}
