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

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="advanced"/>
 * Classes who implement this interface can be a
 * <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>,
 * also refered to here as a <term>context node list</term>.
 */
public interface ContextNodeList
{

  /**
   * Get the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   *
   *
   * @return The current node, or null.
   */
  public Node getCurrentNode();

  /**
   * Get the current position, which is one less than
   * the next nextNode() call will retrieve.  i.e. if
   * you call getCurrentPos() and the return is 0, the next
   * fetch will take place at index 1.
   *
   * @return The position of the
   * <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>
   * in the  <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>.
   */
  public int getCurrentPos();

  /**
   * Reset the iterator.
   */
  public void reset();

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached.  They are not cached by default.
   *
   * @param b true if the nodes should be cached.
   */
  public void setShouldCacheNodes(boolean b);

  /**
   * If an index is requested, NodeSetDTM will call this method
   * to run the iterator to the index.  By default this sets
   * m_next to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end.
   *
   * @param index The index to run to, or -1 if the iterator should be run
   *              to the end.
   */
  public void runTo(int index);

  /**
   * Set the current position in the node set.
   * @param i Must be a valid index.
   */
  public void setCurrentPos(int i);

  /**
   * Get the length of the list.
   *
   * @return The number of nodes in this node list.
   */
  public int size();

  /**
   * Tells if this NodeSetDTM is "fresh", in other words, if
   * the first nextNode() that is called will return the
   * first node in the set.
   *
   * @return true if the iteration of this list has not yet begun.
   */
  public boolean isFresh();

  /**
   * Get a cloned Iterator that is reset to the start of the iteration.
   *
   * @return A clone of this iteration that has been reset.
   *
   * @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException;

  /**
   * Get a clone of this iterator.  Be aware that this operation may be
   * somewhat expensive.
   *
   *
   * @return A clone of this object.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException;

  /**
   * Get the index of the last node in this list.
   *
   *
   * @return the index of the last node in this list.
   */
  public int getLast();

  /**
   * Set the index of the last node in this list.
   *
   *
   * @param last the index of the last node in this list.
   */
  public void setLast(int last);
}
