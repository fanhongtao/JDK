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

import org.apache.xpath.XPathContext;
import org.apache.xpath.VariableStack;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xalan.templates.ElemTemplateElement;

import java.util.Stack;


import org.xml.sax.helpers.NamespaceSupport;
import org.apache.xml.utils.NamespaceSupport2;
import org.apache.xml.utils.ObjectStack;

import java.util.Enumeration;

/**
 * This class holds a "snapshot" of it's current transformer state,
 * which can later be restored.
 *
 * This only saves state which can change over the course of the side-effect-free
 * (i.e. no extensions that call setURIResolver, etc.).
 */
class TransformSnapshotImpl implements TransformSnapshot
{

  /**
   * The stack of Variable stack frames.
   */
  private VariableStack m_variableStacks;

  /**
   * The stack of <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a> objects.
   *  Not to be confused with the current node list.  
   */
  private IntStack m_currentNodes;

  /** A stack of the current sub-expression nodes. */
  private IntStack m_currentExpressionNodes;

  /**
   * The current context node lists stack.
   */
  private Stack m_contextNodeLists;

  /**
   * The current context node list.
   */
  private DTMIterator m_contextNodeList;

  /**
   * Stack of AxesIterators.
   */
  private Stack m_axesIteratorStack;

  /**
   * Is > 0 when we're processing a for-each.
   */
  private BoolStack m_currentTemplateRuleIsNull;

  /**
   * A node vector used as a stack to track the current
   * ElemTemplateElement.  Needed for the
   * org.apache.xalan.transformer.TransformState interface,
   * so a tool can discover the calling template. 
   */
  private ObjectStack m_currentTemplateElements;

  /**
   * A node vector used as a stack to track the current
   * ElemTemplate that was matched, as well as the node that
   * was matched.  Needed for the
   * org.apache.xalan.transformer.TransformState interface,
   * so a tool can discover the matched template, and matched
   * node. 
   */
  private Stack m_currentMatchTemplates;

  /**
   * A node vector used as a stack to track the current
   * ElemTemplate that was matched, as well as the node that
   * was matched.  Needed for the
   * org.apache.xalan.transformer.TransformState interface,
   * so a tool can discover the matched template, and matched
   * node. 
   */
  private NodeVector m_currentMatchNodes;

  /**
   * The table of counters for xsl:number support.
   * @see ElemNumber
   */
  private CountersTable m_countersTable;

  /**
   * Stack for the purposes of flagging infinite recursion with
   * attribute sets.
   */
  private Stack m_attrSetStack;

  /** Indicate whether a namespace context was pushed */
  boolean m_nsContextPushed;

  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  private NamespaceSupport m_nsSupport;

  /** The number of events queued */
  int m_eventCount;

  /**
   * Constructor TransformSnapshotImpl
   * Take a snapshot of the currently executing context.
   *
   * @param transformer Non null transformer instance
   */
  TransformSnapshotImpl(TransformerImpl transformer)
  {

    try
    {

      // Are all these clones deep enough?
      ResultTreeHandler rtf = transformer.getResultTreeHandler();

      m_eventCount = rtf.m_eventCount;

      // yuck.  No clone. Hope this is good enough.
      m_nsSupport = new NamespaceSupport2();

      Enumeration prefixes = rtf.m_nsSupport.getPrefixes();

      while (prefixes.hasMoreElements())
      {
        String prefix = (String) prefixes.nextElement();
        String uri = rtf.m_nsSupport.getURI(prefix);

        m_nsSupport.declarePrefix(prefix, uri);
      }

      m_nsContextPushed = rtf.m_nsContextPushed;

      XPathContext xpc = transformer.getXPathContext();

      m_variableStacks = (VariableStack) xpc.getVarStack().clone();
      m_currentNodes = (IntStack) xpc.getCurrentNodeStack().clone();
      m_currentExpressionNodes =
        (IntStack) xpc.getCurrentExpressionNodeStack().clone();
      m_contextNodeLists = (Stack) xpc.getContextNodeListsStack().clone();

      if (!m_contextNodeLists.empty())
        m_contextNodeList =
          (DTMIterator) xpc.getContextNodeList().clone();

      m_axesIteratorStack = (Stack) xpc.getAxesIteratorStackStacks().clone();
      m_currentTemplateRuleIsNull =
        (BoolStack) transformer.m_currentTemplateRuleIsNull.clone();
      m_currentTemplateElements =
        (ObjectStack) transformer.m_currentTemplateElements.clone();
      m_currentMatchTemplates =
        (Stack) transformer.m_currentMatchTemplates.clone();
      m_currentMatchNodes =
        (NodeVector) transformer.m_currentMatchedNodes.clone();
      m_countersTable =
        (CountersTable) transformer.getCountersTable().clone();

      if (transformer.m_attrSetStack != null)
        m_attrSetStack = (Stack) transformer.m_attrSetStack.clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
    }
  }

  /**
   * This will reset the stylesheet to a given execution context
   * based on some previously taken snapshot where we can then start execution 
   *
   * @param transformer Non null transformer instance
   */
  void apply(TransformerImpl transformer)
  {

    try
    {

      // Are all these clones deep enough?
      ResultTreeHandler rtf = transformer.getResultTreeHandler();

      if (rtf != null)
      {
        rtf.m_eventCount = 1;  //1 for start document event! m_eventCount;

        // yuck.  No clone. Hope this is good enough.
        rtf.m_nsSupport = new NamespaceSupport();

        Enumeration prefixes = m_nsSupport.getPrefixes();

        while (prefixes.hasMoreElements())
        {
          String prefix = (String) prefixes.nextElement();
          String uri = m_nsSupport.getURI(prefix);

          rtf.m_nsSupport.declarePrefix(prefix, uri);
        }

        rtf.m_nsContextPushed = m_nsContextPushed;
      }

      XPathContext xpc = transformer.getXPathContext();

      xpc.setVarStack((VariableStack) m_variableStacks.clone());
      xpc.setCurrentNodeStack((IntStack) m_currentNodes.clone());
      xpc.setCurrentExpressionNodeStack(
        (IntStack) m_currentExpressionNodes.clone());
      xpc.setContextNodeListsStack((Stack) m_contextNodeLists.clone());

      if (m_contextNodeList != null)
        xpc.pushContextNodeList((DTMIterator) m_contextNodeList.clone());

      xpc.setAxesIteratorStackStacks((Stack) m_axesIteratorStack.clone());

      transformer.m_currentTemplateRuleIsNull =
        (BoolStack) m_currentTemplateRuleIsNull.clone();
      transformer.m_currentTemplateElements =
        (ObjectStack) m_currentTemplateElements.clone();
      transformer.m_currentMatchTemplates =
        (Stack) m_currentMatchTemplates.clone();
      transformer.m_currentMatchedNodes =
        (NodeVector) m_currentMatchNodes.clone();
      transformer.m_countersTable = (CountersTable) m_countersTable.clone();

      if (m_attrSetStack != null)
        transformer.m_attrSetStack = (Stack) m_attrSetStack.clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
    }
  }
}
