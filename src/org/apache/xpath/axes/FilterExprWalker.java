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
package org.apache.xpath.axes;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.VariableStack;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.patterns.NodeTestFilter;

import java.util.Vector;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.Axis;

/**
 * Walker for the OP_VARIABLE, or OP_EXTFUNCTION, or OP_FUNCTION, or OP_GROUP,
 * op codes.
 * @see <a href="http://www.w3.org/TR/xpath#NT-FilterExpr">XPath FilterExpr descriptions</a>
 */
public class FilterExprWalker extends AxesWalker
{

  /**
   * Construct a FilterExprWalker using a LocPathIterator.
   *
   * @param locPathIterator non-null reference to the parent iterator.
   */
  FilterExprWalker(WalkingIterator locPathIterator)
  {
    super(locPathIterator, Axis.FILTEREDLIST);
  }

  /**
   * Init a FilterExprWalker.
   *
   * @param compiler non-null reference to the Compiler that is constructing.
   * @param opPos positive opcode position for this step.
   * @param stepType The type of step.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void init(Compiler compiler, int opPos, int stepType)
          throws javax.xml.transform.TransformerException
  {

    super.init(compiler, opPos, stepType);

    // Smooth over an anomily in the opcode map...
    switch (stepType)
    {
    case OpCodes.OP_VARIABLE :
    case OpCodes.OP_EXTFUNCTION :
    case OpCodes.OP_FUNCTION :
    case OpCodes.OP_GROUP :
      m_expr = compiler.compile(opPos);
      break;
    default :
      m_expr = compiler.compile(opPos + 2);
    }
  }

  /**
   *  Set the root node of the TreeWalker.
   *
   * @param root non-null reference to the root, or starting point of 
   *        the query.
   */
  public void setRoot(int root)
  {

    // System.out.println("root: "+root);
    XPathContext xctxt = m_lpi.getXPathContext();
    PrefixResolver savedResolver = xctxt.getNamespaceContext();

    try
    {
      xctxt.pushCurrentNode(root);
      xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
      
      // The setRoot operation can take place with a reset operation, 
      // and so we may not be in the context of LocPathIterator#nextNode, 
      // so we have to set up the variable context, execute the expression, 
      // and then restore the variable context.

      if(m_lpi.getIsTopLevel())
      {
        // System.out.println("calling m_expr.execute(m_lpi.getXPathContext())");
        VariableStack vars = m_lpi.m_execContext.getVarStack();
        
        // These three statements need to be combined into one operation.
        int savedStart = vars.getStackFrame();
        vars.setStackFrame(m_lpi.m_stackFrame);
        
        m_nodeSet = m_expr.asIterator(xctxt, root);
        
        // These two statements need to be combined into one operation.
        vars.setStackFrame(savedStart);
      }
      else
        m_nodeSet = m_expr.asIterator(xctxt, root);
            
    }
    catch (javax.xml.transform.TransformerException se)
    {

      // TODO: Fix...
      throw new org.apache.xml.utils.WrappedRuntimeException(se);
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
    }

    super.setRoot(root);
  }

  /**
   * Get a cloned FilterExprWalker.
   *
   * @return A new FilterExprWalker that can be used without mutating this one.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    FilterExprWalker clone = (FilterExprWalker) super.clone();

    // clone.m_expr = (Expression)((Expression)m_expr).clone();
    if (null != m_nodeSet)
      clone.m_nodeSet = (DTMIterator) m_nodeSet.clone();

    return clone;
  }

  /**
   * This method needs to override AxesWalker.acceptNode because FilterExprWalkers
   * don't need to, and shouldn't, do a node test.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int n)
  {

    try
    {
      if (getPredicateCount() > 0)
      {
        countProximityPosition(0);

        if (!executePredicates(n, m_lpi.getXPathContext()))
          return DTMIterator.FILTER_SKIP;
      }

      return DTMIterator.FILTER_ACCEPT;
    }
    catch (javax.xml.transform.TransformerException se)
    {
      throw new RuntimeException(se.getMessage());
    }
  }

  /**
   *  Moves the <code>TreeWalker</code> to the next visible node in document
   * order relative to the current node, and returns the new node. If the
   * current node has no next node,  or if the search for nextNode attempts
   * to step upward from the TreeWalker's root node, returns
   * <code>null</code> , and retains the current node.
   * @return  The new node, or <code>null</code> if the current node has no
   *   next node  in the TreeWalker's logical view.
   */
  public int getNextNode()
  {

    if (null != m_nodeSet)
       return m_nodeSet.nextNode();
    else
      return DTM.NULL;
  }
  
  /**
   * Get the index of the last node that can be itterated to.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
   */
  public int getLastPos(XPathContext xctxt)
  {
    return m_nodeSet.getLength();
  }
  
  /** The contained expression. Should be non-null.
   *  @serial   */
  private Expression m_expr;

  /** The result of executing m_expr.  Needs to be deep cloned on clone op.  */
  transient private DTMIterator m_nodeSet;

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
    super.fixupVariables(vars, globalsSize);
    m_expr.fixupVariables(vars, globalsSize);
  }
  
  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * Warning: This can only be called after setRoot has been called!
   * 
   * @return true as a default.
   */
  public boolean isDocOrdered()
  {
    return m_nodeSet.isDocOrdered();
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return m_nodeSet.getAxis();
  }



}
