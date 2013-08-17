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
import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.objects.XObject;

import java.util.Vector;

import org.apache.xalan.trace.TracerEvent;
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.VariableStack;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.Axis;

import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTMManager;

// Experemental
import org.apache.xml.dtm.ref.ExpandedNameTable;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:apply-templates.
 * <pre>
 * &amp;!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
 * &amp;!ATTLIST xsl:apply-templates
 *   select %expr; "node()"
 *   mode %qname; #IMPLIED
 * &amp;
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Applying-Template-Rules">section-Applying-Template-Rules in XSLT Specification</a>
 */
public class ElemApplyTemplates extends ElemCallTemplate
{

  /**
   * mode %qname; #IMPLIED
   * @serial
   */
  private QName m_mode = null;

  /**
   * Set the mode attribute for this element.
   *
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   */
  public void setMode(QName mode)
  {
    m_mode = mode;
  }

  /**
   * Get the mode attribute for this element.
   *
   * @return The mode attribute for this element
   */
  public QName getMode()
  {
    return m_mode;
  }

  /**
   * Tells if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   * @see <a href="http://www.w3.org/TR/xslt#built-in-rule">built-in-rule in XSLT Specification</a>
   * @serial
   */
  private boolean m_isDefaultTemplate = false;
  
//  /**
//   * List of namespace/localname IDs, for identification of xsl:with-param to 
//   * xsl:params.  Initialized in the compose() method.
//   */
//  private int[] m_paramIDs;

  /**
   * Set if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   * @see <a href="http://www.w3.org/TR/xslt#built-in-rule">built-in-rule in XSLT Specification</a>
   *
   * @param b boolean value to set.
   */
  public void setIsDefaultTemplate(boolean b)
  {
    m_isDefaultTemplate = b;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for this element types
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_APPLY_TEMPLATES;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_APPLY_TEMPLATES_STRING;
  }

  /**
   * Apply the context node to the matching templates.
   * @see <a href="http://www.w3.org/TR/xslt#section-Applying-Template-Rules">section-Applying-Template-Rules in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(false);

    boolean pushMode = false;

    try
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEvent(this);

      // %REVIEW% Do we need this check??
      //      if (null != sourceNode)
      //      {
      // boolean needToTurnOffInfiniteLoopCheck = false;
      QName mode = transformer.getMode();

      if (!m_isDefaultTemplate)
      {
        if (((null == mode) && (null != m_mode))
                || ((null != mode) &&!mode.equals(m_mode)))
        {
          pushMode = true;

          transformer.pushMode(m_mode);
        }
      }
      transformSelectedNodes(transformer);
    }
    finally
    {
      if (pushMode)
        transformer.popMode();

      transformer.popCurrentTemplateRuleIsNull();
    }
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
    DTMIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);
    VariableStack vars = xctxt.getVarStack();
    int nParams = getParamElemCount();

    try
    {

      final Vector keys = (m_sortElems == null)
                          ? null
                          : transformer.processSortKeys(this, sourceNode);

      // Sort if we need to.
      if (null != keys)
        sourceNodes = sortNodes(xctxt, keys, sourceNodes);

      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));

      final ResultTreeHandler rth = transformer.getResultTreeHandler();
      ContentHandler chandler = rth.getContentHandler();
      final StylesheetRoot sroot = transformer.getStylesheet();
      final TemplateList tl = sroot.getTemplateListComposed();
      final boolean quiet = transformer.getQuietConflictWarnings();
      
      xctxt.pushCurrentNode(DTM.NULL);
      int[] currentNodes = xctxt.getCurrentNodeStack();
      int currentNodePos = xctxt.getCurrentNodeFirstFree() - 1;
      
      xctxt.pushCurrentExpressionNode(DTM.NULL);
      int[] currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
      int currentExpressionNodePos = xctxt.getCurrentExpressionNodesFirstFree() - 1;

      xctxt.pushSAXLocatorNull();
      xctxt.pushContextNodeList(sourceNodes);
      transformer.pushElemTemplateElement(null);
      // pushParams(transformer, xctxt);

      // Should be able to get this from the iterator but there must be a bug.
      DTM dtm = xctxt.getDTM(sourceNode);
      int docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
      
      int argsFrame = -1;
      if(nParams > 0)
      {
        // This code will create a section on the stack that is all the 
        // evaluated arguments.  These will be copied into the real params 
        // section of each called template.
        int thisframe = vars.getStackFrame();
        argsFrame = vars.link(nParams);
        vars.setStackFrame(thisframe);
        
        for (int i = 0; i < nParams; i++) 
        {
          ElemWithParam ewp = m_paramElems[i];
          XObject obj = ewp.getValue(transformer, sourceNode);
          
          vars.setLocalVariable(i, obj, argsFrame);
        }
        vars.setStackFrame(argsFrame);
      }
      
      
      int child;
      while (DTM.NULL != (child = sourceNodes.nextNode()))
      {
        currentNodes[currentNodePos] = child;
        currentExpressionNodes[currentExpressionNodePos] = child;

        if((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
        {
          dtm = xctxt.getDTM(child);
          docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
        }
        
        final int exNodeType = dtm.getExpandedTypeID(child);
        final int nodeType = (exNodeType >> ExpandedNameTable.ROTAMOUNT_TYPE);

        final QName mode = transformer.getMode();

        ElemTemplate template = tl.getTemplateFast(xctxt, child, exNodeType, mode, 
                                      -1, quiet, dtm);

        // If that didn't locate a node, fall back to a default template rule.
        // See http://www.w3.org/TR/xslt#built-in-rule.
        if (null == template)
        {
          switch (nodeType)
          {
          case DTM.DOCUMENT_FRAGMENT_NODE :
          case DTM.ELEMENT_NODE :
            template = sroot.getDefaultRule();
            // %OPT% direct faster?
            break;
          case DTM.ATTRIBUTE_NODE :
          case DTM.CDATA_SECTION_NODE :
          case DTM.TEXT_NODE :
            // if(rth.m_elemIsPending || rth.m_docPending)
            //  rth.flushPending(true);
            transformer.pushPairCurrentMatched(sroot.getDefaultTextRule(), child);
            transformer.setCurrentElement(sroot.getDefaultTextRule());
            // dtm.dispatchCharactersEvents(child, chandler, false);
            dtm.dispatchCharactersEvents(child, rth, false);
            transformer.popCurrentMatched();
            continue;
          case DTM.DOCUMENT_NODE :
            template = sroot.getDefaultRootRule();
            break;
          default :

            // No default rules for processing instructions and the like.
            continue;
          }
        }
        
        transformer.pushPairCurrentMatched(template, child);

        if(template.m_frameSize > 0)
        {
          vars.link(template.m_frameSize);
          // You can't do the check for nParams here, otherwise the 
          // xsl:params might not be nulled.
          if(/* nParams > 0 && */ template.m_inArgsSize > 0)
          {
            int paramIndex = 0;
            for (ElemTemplateElement elem = template.getFirstChildElem(); 
                 null != elem; elem = elem.getNextSiblingElem()) 
            {
              if(Constants.ELEMNAME_PARAMVARIABLE == elem.getXSLToken())
              {
                ElemParam ep = (ElemParam)elem;
                
                int i;
                for (i = 0; i < nParams; i++) 
                {
                  ElemWithParam ewp = m_paramElems[i];
                  if(ewp.m_qnameID == ep.m_qnameID)
                  {
                    XObject obj = vars.getLocalVariable(i, argsFrame);
                    vars.setLocalVariable(paramIndex, obj);
                    break;
                  }
                }
                if(i == nParams)
                  vars.setLocalVariable(paramIndex, null);
              }
              else
                break;
              paramIndex++;
            }
            
          }
        }

        // if (check)
        //  guard.push(this, child);

        // Fire a trace event for the template.
        if (TransformerImpl.S_DEBUG)
          transformer.getTraceManager().fireTraceEvent(template);

        // And execute the child templates.
        // Loop through the children of the template, calling execute on 
        // each of them.
        for (ElemTemplateElement t = template.m_firstChild; 
             t != null; t = t.m_nextSibling)
        {
          xctxt.setSAXLocator(t);
          transformer.setCurrentElement(t);
          t.execute(transformer);
        }
        
        if(template.m_frameSize > 0)
          vars.unlink();
          
        transformer.popCurrentMatched();
        
      } // end while (DTM.NULL != (child = sourceNodes.nextNode()))
    }
    catch (SAXException se)
    {
      transformer.getErrorListener().fatalError(new TransformerException(se));
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));

      if(nParams > 0)
        vars.unlink();
      xctxt.popSAXLocator();
      xctxt.popContextNodeList();
      transformer.popElemTemplateElement();
      xctxt.popCurrentExpressionNode();
      xctxt.popCurrentNode();
      sourceNodes.detach();
    }
  }

}
