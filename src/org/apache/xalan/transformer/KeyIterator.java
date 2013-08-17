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

import java.util.Vector;

import org.apache.xpath.axes.WalkingIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.QName;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;

import org.apache.xml.dtm.DTM;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * This class implements an optimized iterator for 
 * "key()" patterns. This iterator incrementally walks the 
 * source tree and finds all the nodes that match
 * a given key name and match pattern.
 */
public class KeyIterator extends WalkingIterator
{
  
  /** The key table this iterator is associated to.
   *  @serial          */
  private KeyTable m_keyTable;

  /** Key name.
   *  @serial           */
  private QName m_name;
  
  /** 
   * Flag indicating whether the whole source tree has been walked.     
   * True if we still need to finish walking the tree.
   * */
  transient private boolean m_lookForMoreNodes = true;

  /**
   * Get the key name from a key declaration this iterator will process
   *
   *
   * @return Key name
   */
  public QName getName()
  {
    return m_name;
  }

  /** Vector of Key declarations in the stylesheet.
   *  @serial          */
  private Vector m_keyDeclarations;

  /**
   * Get the key declarations from the stylesheet 
   *
   *
   * @return Vector containing the key declarations from the stylesheet
   */
  public Vector getKeyDeclarations()
  {
    return m_keyDeclarations;
  }

  /**
   * Constructor KeyIterator
   *
   *
   * @param doc The document node
   * @param nscontext The prefix resolver for the execution context.
   * @param name The key name
   * @param keyDeclarations The key declarations from the stylesheet 
   * @param xctxt The XPath runtime state
   */
  public KeyIterator(int doc, PrefixResolver nscontext, QName name,
                     Vector keyDeclarations, XPathContext xctxt)
  {

    super(nscontext);

    int current = xctxt.getCurrentNode();
    setRoot(current, xctxt);

    m_name = name;
    m_keyDeclarations = keyDeclarations;
    m_firstWalker = new KeyWalker(this);

    this.setLastUsedWalker(m_firstWalker);
  }

  /**
   * Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * 
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {

    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    int n = super.nextNode();

    // System.out.println("--> "+((null == n) ? "null" : n.getNodeName()));
    return n;
  }

  /**
   * Set the value of the key that this iterator will look for 
   *
   *
   * @param lookupKey value of the key to look for
   */
  public void setLookupKey(XMLString lookupKey)
  {

    // System.out.println("setLookupKey - lookupKey: "+lookupKey);
    ((KeyWalker) m_firstWalker).m_lookupKey = lookupKey;

    int context = getContext();
    DTM dtm = this.getDTM(context);
    m_firstWalker.setRoot(dtm.getDocument());
    this.setLastUsedWalker(m_firstWalker);
    this.setNextPosition(0);
  }
  
  /**
   * Set the KeyTable associated with this iterator  
   *
   *
   * @param keyTable, the KeyTable associated with this iterator
   */
  void setKeyTable(KeyTable keyTable)
  {
    m_keyTable = keyTable;
  }  
  
  /**
   * Add this value(ref) to the refsTable in KeyTable  
   *
   *
   * @param ref Key value(ref)(from key use field)
   * @param node Node matching that ref 
   */
  void addRefNode(XMLString ref, int node)
  {
    m_keyTable.addRefNode(ref, node);
  }
  
  /**
   * Indicate whether we have walked the whole tree  
   *
   * @param b False if we have walked the whole tree
   */
  void setLookForMoreNodes(boolean b)
  {
    m_lookForMoreNodes = b;
  }
  
  /**
   * Get flag indicating whether we have walked the whole tree  
   *
   */
  boolean getLookForMoreNodes()
  {
    return m_lookForMoreNodes;
  }
  
}
