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

import java.util.Hashtable;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNull;
import org.apache.xalan.templates.KeyDeclaration;

/**
 * <meta name="usage" content="advanced"/>
 * Table of element keys, keyed by document node.  An instance of this
 * class is keyed by a Document node that should be matched with the
 * root of the current context.
 */
public class KeyTable
{
  /**
   * The document key.  This table should only be used with contexts
   * whose Document roots match this key.
   */
  private int m_docKey;

  /**
   * Vector of KeyDeclaration instances holding the key declarations.
   */
  private Vector m_keyDeclarations;

  /**
   * Hold a cache of key() function result for each ref.
   * Key is XMLString, the ref value
   * Value is XNodeSet, the key() function result for the given ref value.
   */
  private Hashtable m_refsTable = null;

  /**
   * Get the document root matching this key.  
   *
   * @return the document root matching this key
   */
  public int getDocKey()
  {
    return m_docKey;
  }

  /** 
   * The main iterator that will walk through the source  
   * tree for this key.
   */
  private XNodeSet m_keyNodes;
  
  KeyIterator getKeyIterator()
  {
  	return (KeyIterator)(m_keyNodes.getContainedIter());
  }

  /**
   * Build a keys table.
   * @param doc The owner document key.
   * @param nscontext The stylesheet's namespace context.
   * @param name The key name
   * @param keyDeclarations The stylesheet's xsl:key declarations.
   * @param xmlLiaison The parser liaison for support of getNodeData(useNode).
   *
   * @throws javax.xml.transform.TransformerException
   */
  public KeyTable(
          int doc, PrefixResolver nscontext, QName name, Vector keyDeclarations, XPathContext xctxt)
            throws javax.xml.transform.TransformerException
  {
    m_docKey = doc;
    m_keyDeclarations = keyDeclarations;
    KeyIterator ki = new KeyIterator(name, keyDeclarations);

    m_keyNodes = new XNodeSet(ki);
    m_keyNodes.allowDetachToRelease(false);
    m_keyNodes.setRoot(doc, xctxt);
  }

  /**
   * Given a valid element key, return the corresponding node list.
   * 
   * @param name The name of the key, which must match the 'name' attribute on xsl:key.
   * @param ref The value that must match the value found by the 'match' attribute on xsl:key.
   * @return a set of nodes referenced by the key named <CODE>name</CODE> and the reference <CODE>ref</CODE>. If no node is referenced by this key, an empty node set is returned.
   */
  public XNodeSet getNodeSetDTMByKey(QName name, XMLString ref)

  {
    XNodeSet refNodes = (XNodeSet) getRefsTable().get(ref);
    // clone wiht reset the node set
   try
    {
      if (refNodes != null)
      {
         refNodes = (XNodeSet) refNodes.cloneWithReset();
       }
    }
    catch (CloneNotSupportedException e)
    {
      refNodes = null;
    }

    if (refNodes == null) {
     //  create an empty XNodeSet
      KeyIterator ki = (KeyIterator) (m_keyNodes).getContainedIter();
      XPathContext xctxt = ki.getXPathContext();
      refNodes = new XNodeSet(xctxt.getDTMManager()) {
        public void setRoot(int nodeHandle, Object environment) {
          // Root cannot be set on non-iterated node sets. Ignore it.
        }
      };
      refNodes.reset();
    }

    return refNodes;
  }

  /**
   * Get Key Name for this KeyTable  
   *
   * @return Key name
   */
  public QName getKeyTableName()
  {
    return getKeyIterator().getName();
  }

  /**
   * @return key declaration for the key associated to this KeyTable
   */
  private KeyDeclaration getKeyDeclaration() {
    int nDeclarations = m_keyDeclarations.size();

    // Walk through each of the declarations made with xsl:key
    for (int i = 0; i < nDeclarations; i++)
    {
      KeyDeclaration kd = (KeyDeclaration) m_keyDeclarations.elementAt(i);

      // Only continue if the name on this key declaration
      // matches the name on the iterator for this walker.
      if (kd.getName().equals(getKeyTableName()))
      {
        return kd;
      }
    }

    // should never happen
    return null;
  }

  /**
   * @return lazy initialized refs table associating evaluation of key function
   *         with a XNodeSet
   */
  private Hashtable getRefsTable()
  {
    if (m_refsTable == null)
    {
      m_refsTable = new Hashtable(89);  // initial capacity set to a prime number to improve hash algorithm performance

      KeyIterator ki = (KeyIterator) (m_keyNodes).getContainedIter();
      XPathContext xctxt = ki.getXPathContext();

      KeyDeclaration keyDeclaration = getKeyDeclaration();

      int currentNode;
      m_keyNodes.reset();
      while (DTM.NULL != (currentNode = m_keyNodes.nextNode()))
      {
        try
        {
          XObject xuse = keyDeclaration.getUse().execute(xctxt, currentNode, ki.getPrefixResolver());

          if (xuse.getType() != xuse.CLASS_NODESET)
          {
            XMLString exprResult = xuse.xstr();
            addValueInRefsTable(xctxt, exprResult, currentNode);
          }
          else
          {
            DTMIterator i = ((XNodeSet)xuse).iterRaw();
            int currentNodeInUseClause;

            while (DTM.NULL != (currentNodeInUseClause = i.nextNode()))
            {
              DTM dtm = xctxt.getDTM(currentNodeInUseClause);
              XMLString exprResult = dtm.getStringValue(currentNodeInUseClause);
              addValueInRefsTable(xctxt, exprResult, currentNode);
            }
          }
        }
        catch (TransformerException te)
        {
          throw new WrappedRuntimeException(te);
        }
      }
    }
    return m_refsTable;
  }

  /**
   * Add an association between a ref and a node in the m_refsTable.
   * Requires that m_refsTable != null
   * @param xctxt XPath context
   * @param ref the value of the use clause of the current key for the given node
   * @param node the node to reference
   */
  private void addValueInRefsTable(XPathContext xctxt, XMLString ref, int node) {
    
    XNodeSet nodes = (XNodeSet) m_refsTable.get(ref);
    if (nodes == null)
    {
      nodes = new XNodeSet(node, xctxt.getDTMManager());
      nodes.nextNode();
      m_refsTable.put(ref, nodes);
    }
    else
    {
      // Nodes are passed to this method in document order.  Since we need to
      // suppress duplicates, we only need to check against the last entry
      // in each nodeset.  We use nodes.nextNode after each entry so we can
      // easily compare node against the current node.
      if (nodes.getCurrentNode() != node) {
          nodes.mutableNodeset().addNode(node);
          nodes.nextNode();
      }    
    }
  }
}
