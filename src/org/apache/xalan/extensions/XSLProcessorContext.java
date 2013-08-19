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
package org.apache.xalan.extensions;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.*;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xml.utils.QName;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.DescendantIterator;
import org.apache.xpath.axes.OneStepIterator;

import org.apache.xml.dtm.DTM;

// import org.apache.xalan.xslt.*;

/**
 * <meta name="usage" content="general"/>
 * Provides transformer context to be passed to an extension element.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class XSLProcessorContext
{

  /**
   * Create a processor context to be passed to an extension.
   * (Notice it is a package-only constructor).
   *
   * @param transformer non-null transformer instance
   * @param stylesheetTree The owning stylesheet
   * @param sourceTree The source document
   * @param sourceNode The current source node
   * @param mode the current mode being executed.
   */
  public XSLProcessorContext(TransformerImpl transformer,
                             Stylesheet stylesheetTree)
  {

    this.transformer = transformer;
    this.stylesheetTree = stylesheetTree;
    // %TBD%
    org.apache.xpath.XPathContext xctxt = transformer.getXPathContext();
    this.mode = transformer.getMode();
    this.sourceNode = xctxt.getCurrentNode();
    this.sourceTree = xctxt.getDTM(this.sourceNode);
  }

  /** An instance of a transformer          */
  private TransformerImpl transformer;

  /**
   * Get the transformer.
   *
   * @return the transformer instance for this context
   */
  public TransformerImpl getTransformer()
  {
    return transformer;
  }

  /** The owning stylesheet for this context          */
  private Stylesheet stylesheetTree;

  /**
   * Get the Stylesheet being executed.
   *
   * @return the Stylesheet being executed.
   */
  public Stylesheet getStylesheet()
  {
    return stylesheetTree;
  }

  /**  The root of the source tree being executed.        */
  private org.apache.xml.dtm.DTM sourceTree;

  /**
   * Get the root of the source tree being executed.
   *
   * @return the root of the source tree being executed.
   */
  public org.w3c.dom.Node getSourceTree()
  {
    return sourceTree.getNode(sourceTree.getDocumentRoot(sourceNode));
  }

  /** the current context node.          */
  private int sourceNode;

  /**
   * Get the current context node.
   *
   * @return the current context node.
   */
  public org.w3c.dom.Node getContextNode()
  {
    return sourceTree.getNode(sourceNode);
  }

  /** the current mode being executed.         */
  private QName mode;

  /**
   * Get the current mode being executed.
   *
   * @return the current mode being executed.
   */
  public QName getMode()
  {
    return mode;
  }

  /**
   * Output an object to the result tree by doing the right conversions.
   * This is public for access by extensions.
   *
   *
   * @param stylesheetTree The owning stylesheet
   * @param obj the Java object to output. If its of an X<something> type
   *        then that conversion is done first and then sent out.
   *
   * @throws TransformerException
   * @throws java.io.FileNotFoundException
   * @throws java.io.IOException
   * @throws java.net.MalformedURLException
   */
  public void outputToResultTree(Stylesheet stylesheetTree, Object obj)
          throws TransformerException, java.net.MalformedURLException,
                 java.io.FileNotFoundException, java.io.IOException
  {

    try
    {
      ResultTreeHandler rtreeHandler = transformer.getResultTreeHandler();
      XPathContext xctxt = transformer.getXPathContext();
      XObject value;

      // Make the return object into an XObject because it
      // will be easier below.  One of the reasons to do this
      // is to keep all the conversion functionality in the
      // XObject classes.
      if (obj instanceof XObject)
      {
        value = (XObject) obj;
      }
      else if (obj instanceof String)
      {
        value = new XString((String) obj);
      }
      else if (obj instanceof Boolean)
      {
        value = new XBoolean(((Boolean) obj).booleanValue());
      }
      else if (obj instanceof Double)
      {
        value = new XNumber(((Double) obj).doubleValue());
      }
      else if (obj instanceof DocumentFragment)
      {
        int handle = xctxt.getDTMHandleFromNode((DocumentFragment)obj);
        
        value = new XRTreeFrag(handle, xctxt);
      }
      else if (obj instanceof DTM)
      {
        DTM dtm = (DTM)obj;
        DTMIterator iterator = new DescendantIterator();
        // %%ISSUE%% getDocument may not be valid for DTMs shared by multiple
        // document trees, eg RTFs. But in that case, we shouldn't be trying
        // to iterate over the whole DTM; we should be iterating over 
        // dtm.getDocumentRoot(rootNodeHandle), and folks should have told us
        // this by passing a more appropriate type.
        iterator.setRoot(dtm.getDocument(), xctxt);
        value = new XNodeSet(iterator);
      }
      else if (obj instanceof DTMAxisIterator)
      {
        DTMAxisIterator iter = (DTMAxisIterator)obj;
        DTMIterator iterator = new OneStepIterator(iter, -1);
        value = new XNodeSet(iterator);
      }
      else if (obj instanceof DTMIterator)
      {
        value = new XNodeSet((DTMIterator) obj);
      }
      else if (obj instanceof NodeIterator)
      {
        value = new XNodeSet(new org.apache.xpath.NodeSetDTM(((NodeIterator)obj), xctxt));
      }
      else if (obj instanceof org.w3c.dom.Node)
      {
        value =
          new XNodeSet(xctxt.getDTMHandleFromNode((org.w3c.dom.Node) obj),
                       xctxt.getDTMManager());
      }
      else
      {
        value = new XString(obj.toString());
      }

      int type = value.getType();
      String s;

      switch (type)
      {
      case XObject.CLASS_BOOLEAN :
      case XObject.CLASS_NUMBER :
      case XObject.CLASS_STRING :
        s = value.str();

        rtreeHandler.characters(s.toCharArray(), 0, s.length());
        break;

      case XObject.CLASS_NODESET :  // System.out.println(value);
        DTMIterator nl = value.iter();
        
        int pos;

        while (DTM.NULL != (pos = nl.nextNode()))
        {
          DTM dtm = nl.getDTM(pos);
          int top = pos;

          while (DTM.NULL != pos)
          {
            rtreeHandler.flushPending();
            ClonerToResultTree.cloneToResultTree(pos, dtm.getNodeType(pos), 
                                                   dtm, rtreeHandler, true);

            int nextNode = dtm.getFirstChild(pos);

            while (DTM.NULL == nextNode)
            {
              if (DTM.ELEMENT_NODE == dtm.getNodeType(pos))
              {
                rtreeHandler.endElement("", "", dtm.getNodeName(pos));
              }

              if (top == pos)
                break;

              nextNode = dtm.getNextSibling(pos);

              if (DTM.NULL == nextNode)
              {
                pos = dtm.getParent(pos);

                if (top == pos)
                {
                  if (DTM.ELEMENT_NODE == dtm.getNodeType(pos))
                  {
                    rtreeHandler.endElement("", "", dtm.getNodeName(pos));
                  }

                  nextNode = DTM.NULL;

                  break;
                }
              }
            }

            pos = nextNode;
          }
        }
        break;
      case XObject.CLASS_RTREEFRAG :
        rtreeHandler.outputResultTreeFragment(value,
                                              transformer.getXPathContext());
        break;
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }

  /**
   * I need a "Node transformNode (Node)" method somewhere that the
   * user can call to process the transformation of a node but not
   * serialize out automatically. ????????????????
   *
   * Does ElemTemplateElement.executeChildTemplates() cut it? It sends
   * results out to the stream directly, so that could be a problem.
   */
}
