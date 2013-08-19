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
package org.apache.xalan.templates;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.VariableStack;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
//import org.w3c.dom.DocumentFragment;
//import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;

/**
 * An instance of this class holds unto a variable until 
 * it is executed.  It is used at this time for global 
 * variables which must (we think) forward reference.
 */
public class XUnresolvedVariable extends XObject
{  
  /** The node context for execution. */
  transient private int m_context;
  
  /** The transformer context for execution. */
  transient private TransformerImpl m_transformer;
  
  /** An index to the point in the variable stack where we should
   * begin variable searches for evaluation of expressions.
   * This is -1 if m_isTopLevel is false. 
   **/
  transient private int m_varStackPos = -1;

  /** An index into the variable stack where the variable context 
   * ends, i.e. at the point we should terminate the search. 
   **/
  transient private int m_varStackContext;
  
  /** true if this variable or parameter is a global.
   *  @serial */
  private boolean m_isGlobal;
  
  /** true if this variable or parameter is not currently being evaluated. */
  transient private boolean m_doneEval = true;
  
  /**
   * Create an XUnresolvedVariable, that may be executed at a later time.
   * This is primarily used so that forward referencing works with 
   * global variables.  An XUnresolvedVariable is initially pushed 
   * into the global variable stack, and then replaced with the real 
   * thing when it is accessed.
   *
   * @param obj Must be a non-null reference to an ElemVariable.
   * @param sourceNode The node context for execution.
   * @param transformer The transformer execution context.
   * @param varStackPos An index to the point in the variable stack where we should
   * begin variable searches for evaluation of expressions.
   * @param varStackContext An index into the variable stack where the variable context 
   * ends, i.e. at the point we should terminate the search.
   * @param isGlobal true if this is a global variable.
   */
  public XUnresolvedVariable(ElemVariable obj, int sourceNode, 
                             TransformerImpl transformer,
                             int varStackPos, int varStackContext,
                             boolean isGlobal)
  {
    super(obj);
    m_context = sourceNode;
    m_transformer = transformer;
    
    // For globals, this value will have to be updated once we 
    // have determined how many global variables have been pushed.
    m_varStackPos = varStackPos;
    
    // For globals, this should zero.
    m_varStackContext = varStackContext;
    
    m_isGlobal = isGlobal;
  }
    
  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   *
   * @return This object.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    if (!m_doneEval) 
    {
      this.m_transformer.getMsgMgr().error      
        (xctxt.getSAXLocator(), XSLTErrorResources.ER_REFERENCING_ITSELF, 
          new Object[]{((ElemVariable)this.object()).getName().getLocalName()}); 
    }
    VariableStack vars = xctxt.getVarStack();
    
    // These three statements need to be combined into one operation.
    int currentFrame = vars.getStackFrame();
    //// vars.setStackFrame(m_varStackPos);
   

    ElemVariable velem = (ElemVariable)m_obj;
    try
    {
      m_doneEval = false;
      if(-1 != velem.m_frameSize)
      	vars.link(velem.m_frameSize);
      XObject var = velem.getValue(m_transformer, m_context);
      m_doneEval = true;
      return var;
    }
    finally
    {
      // These two statements need to be combined into one operation.
      // vars.setStackFrame(currentFrame);
      
      if(-1 != velem.m_frameSize)
	  	vars.unlink(currentFrame);
    }
  }
  
  /**
   * Set an index to the point in the variable stack where we should
   * begin variable searches for evaluation of expressions.
   * This is -1 if m_isTopLevel is false. 
   * 
   * @param top A valid value that specifies where in the variable 
   * stack the search should begin.
   */
  public void setVarStackPos(int top)
  {
    m_varStackPos = top;
  }

  /**
   * Set an index into the variable stack where the variable context 
   * ends, i.e. at the point we should terminate the search.
   * 
   * @param The point at which the search should terminate, normally 
   * zero for global variables.
   */
  public void setVarStackContext(int bottom)
  {
    m_varStackContext = bottom;
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNRESOLVEDVARIABLE
   */
  public int getType()
  {
    return CLASS_UNRESOLVEDVARIABLE;
  }
  
  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return An informational string.
   */
  public String getTypeString()
  {
    return "XUnresolvedVariable (" + object().getClass().getName() + ")";
  }


}
