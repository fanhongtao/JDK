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

//import org.w3c.dom.*;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;

// Experemental
import org.apache.xml.dtm.ref.ExpandedNameTable;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.objects.XObject;

import java.util.Vector;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.NodeSorter;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.ClonerToResultTree;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xpath.ExpressionOwner;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:for-each.
 * <pre>
 * <!ELEMENT xsl:for-each
 *  (#PCDATA
 *   %instructions;
 *   %result-elements;
 *   | xsl:sort)
 * >
 *
 * <!ATTLIST xsl:for-each
 *   select %expr; #REQUIRED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#for-each">for-each in XSLT Specification</a>
 */
public class ElemForEach extends ElemTemplateElement implements ExpressionOwner
{
  /** Set true to request some basic status reports */
  static final boolean DEBUG = false;
  
  /**
   * This is set by an "xalan:doc-cache-off" pi.  It tells the engine that
   * documents created in the location paths executed by this element
   * will not be reparsed. It's set by StylesheetHandler during
   * construction. Note that this feature applies _only_ to xsl:for-each
   * elements in its current incarnation; a more general cache management
   * solution is desperately needed.
   */
  public boolean m_doc_cache_off=false;
  
  /**
   * Construct a element representing xsl:for-each.
   */
  public ElemForEach(){}

  /**
   * The "select" expression.
   * @serial
   */
  protected Expression m_selectExpression = null;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
    m_selectExpression = xpath.getExpression();
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
    return m_selectExpression;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * NEEDSDOC @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).compose(sroot);
    }

    java.util.Vector vnames = sroot.getComposeState().getVariableNames();

    if (null != m_selectExpression)
      m_selectExpression.fixupVariables(
        vnames, sroot.getComposeState().getGlobalsSize());
    else
    {
      m_selectExpression =
        getStylesheetRoot().m_selectDefault.getExpression();
    }
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).endCompose(sroot);
    }
    
    super.endCompose(sroot);
  }


  //  /**
  //   * This function is called after everything else has been
  //   * recomposed, and allows the template to set remaining
  //   * values that may be based on some other property that
  //   * depends on recomposition.
  //   *
  //   * @throws TransformerException
  //   */
  //  public void compose() throws TransformerException
  //  {
  //
  //    if (null == m_selectExpression)
  //    {
  //      m_selectExpression =
  //        getStylesheetRoot().m_selectDefault.getExpression();
  //    }
  //  }

  /**
   * Vector containing the xsl:sort elements associated with this element.
   *  @serial
   */
  protected Vector m_sortElems = null;

  /**
   * Get the count xsl:sort elements associated with this element.
   * @return The number of xsl:sort elements.
   */
  public int getSortElemCount()
  {
    return (m_sortElems == null) ? 0 : m_sortElems.size();
  }

  /**
   * Get a xsl:sort element associated with this element.
   *
   * @param i Index of xsl:sort element to get
   *
   * @return xsl:sort element at given index
   */
  public ElemSort getSortElem(int i)
  {
    return (ElemSort) m_sortElems.elementAt(i);
  }

  /**
   * Set a xsl:sort element associated with this element.
   *
   * @param sortElem xsl:sort element to set
   */
  public void setSortElem(ElemSort sortElem)
  {

    if (null == m_sortElems)
      m_sortElems = new Vector();

    m_sortElems.addElement(sortElem);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FOREACH;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_FOREACH_STRING;
  }

  /**
   * Execute the xsl:for-each transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(true);    
    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    try
    {
      transformSelectedNodes(transformer);
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
	    transformer.getTraceManager().fireTraceEndEvent(this); 
      transformer.popCurrentTemplateRuleIsNull();
    }
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
    return this;
  }

  /**
   * Sort given nodes
   *
   *
   * @param xctxt The XPath runtime state for the sort.
   * @param keys Vector of sort keyx
   * @param sourceNodes Iterator of nodes to sort
   *
   * @return iterator of sorted nodes
   *
   * @throws TransformerException
   */
  public DTMIterator sortNodes(
          XPathContext xctxt, Vector keys, DTMIterator sourceNodes)
            throws TransformerException
  {

    NodeSorter sorter = new NodeSorter(xctxt);
    sourceNodes.setShouldCacheNodes(true);
    sourceNodes.runTo(-1);
    xctxt.pushContextNodeList(sourceNodes);

    try
    {
      sorter.sort(sourceNodes, keys, xctxt);
      sourceNodes.setCurrentPos(0);
    }
    finally
    {
      xctxt.popContextNodeList();
    }

    return sourceNodes;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Perform a query if needed, and call transformNode for each child.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param template The owning template context.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   */
  public void transformSelectedNodes(TransformerImpl transformer)
          throws TransformerException
  {

    final XPathContext xctxt = transformer.getXPathContext();
    final int sourceNode = xctxt.getCurrentNode();
    DTMIterator sourceNodes = m_selectExpression.asIterator(xctxt,
            sourceNode);

    try
    {

      final Vector keys = (m_sortElems == null)
              ? null
              : transformer.processSortKeys(this, sourceNode);

      // Sort if we need to.
      if (null != keys)
        sourceNodes = sortNodes(xctxt, keys, sourceNodes);

      if (TransformerImpl.S_DEBUG)
      {
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));
      }

      final ResultTreeHandler rth = transformer.getResultTreeHandler();
      ContentHandler chandler = rth.getContentHandler();

      xctxt.pushCurrentNode(DTM.NULL);

      IntStack currentNodes = xctxt.getCurrentNodeStack();

      xctxt.pushCurrentExpressionNode(DTM.NULL);

      IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();

      xctxt.pushSAXLocatorNull();
      xctxt.pushContextNodeList(sourceNodes);
      transformer.pushElemTemplateElement(null);

      // pushParams(transformer, xctxt);
      // Should be able to get this from the iterator but there must be a bug.
      DTM dtm = xctxt.getDTM(sourceNode);
      int docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
      int child;

      while (DTM.NULL != (child = sourceNodes.nextNode()))
      {
        currentNodes.setTop(child);
        currentExpressionNodes.setTop(child);

        if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
        {
          dtm = xctxt.getDTM(child);
          docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
        }

        //final int exNodeType = dtm.getExpandedTypeID(child);
        final int nodeType = dtm.getNodeType(child); 

        // Fire a trace event for the template.
        if (TransformerImpl.S_DEBUG)
        {
           transformer.getTraceManager().fireTraceEvent(this);
        }

        // And execute the child templates.
        // Loop through the children of the template, calling execute on 
        // each of them.
        for (ElemTemplateElement t = this.m_firstChild; t != null;
             t = t.m_nextSibling)
        {
          xctxt.setSAXLocator(t);
          transformer.setCurrentElement(t);
          t.execute(transformer);
        }
        
        if (TransformerImpl.S_DEBUG)
        {
         // We need to make sure an old current element is not 
          // on the stack.  See TransformerImpl#getElementCallstack.
          transformer.setCurrentElement(null);
          transformer.getTraceManager().fireTraceEndEvent(this);
        }


	 	// KLUGE: Implement <?xalan:doc_cache_off?> 
	 	// ASSUMPTION: This will be set only when the XPath was indeed
	 	// a call to the Document() function. Calling it in other
	 	// situations is likely to fry Xalan.
	 	//
	 	// %REVIEW% We need a MUCH cleaner solution -- one that will
	 	// handle cleaning up after document() and getDTM() in other
		// contexts. The whole SourceTreeManager mechanism should probably
	 	// be moved into DTMManager rather than being explicitly invoked in
	 	// FuncDocument and here.
	 	if(m_doc_cache_off)
		{
	 	  if(DEBUG)
	 	    System.out.println("JJK***** CACHE RELEASE *****\n"+
				       "\tdtm="+dtm.getDocumentBaseURI());
	  	// NOTE: This will work because this is _NOT_ a shared DTM, and thus has
	  	// only a single Document node. If it could ever be an RTF or other
	 	// shared DTM, this would require substantial rework.
	 	  xctxt.getSourceTreeManager().removeDocumentFromCache(dtm.getDocument());
	 	  xctxt.release(dtm,false);
	 	}
      }
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));

      xctxt.popSAXLocator();
      xctxt.popContextNodeList();
      transformer.popElemTemplateElement();
      xctxt.popCurrentExpressionNode();
      xctxt.popCurrentNode();
      sourceNodes.detach();
    }
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    if (Constants.ELEMNAME_SORT == type)
    {
      setSortElem((ElemSort) newChild);

      return newChild;
    }
    else
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
  	if(callAttributes && (null != m_selectExpression))
  		m_selectExpression.callVisitors(this, visitor);
  		
    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).callVisitors(visitor);
    }

    super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
  	exp.exprSetParent(this);
  	m_selectExpression = exp;
  }

}
