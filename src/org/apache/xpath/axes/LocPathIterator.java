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

// Java library imports
import java.util.Vector;
import java.util.Stack;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMManager;

// Xalan imports
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPath;
import org.apache.xpath.compiler.OpMap;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.PsuedoNames;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.ObjectPool;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.VariableStack;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSetDTM, which implements NodeIterator,
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a>.
 *
 * <p>If setShouldCacheNodes(true) is called,
 * as each node is iterated via nextNode(), the node is also stored
 * in the NodeVector, so that previousNode() can easily be done, except in
 * the case where the LocPathIterator is "owned" by a UnionPathIterator,
 * in which case the UnionPathIterator will cache the nodes.</p>
 */
public abstract class LocPathIterator extends PredicatedNodeTest
        implements Cloneable, DTMIterator, java.io.Serializable
{

  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  protected LocPathIterator(PrefixResolver nscontext)
  {

    setLocPathIterator(this);

    this.m_prefixResolver = nscontext;
  }

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    this(compiler, opPos, analysis, true);
  }

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * @param shouldLoadWalkers True if walkers should be
   * loaded, or false if this is a derived iterator and
   * it doesn't wish to load child walkers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    setLocPathIterator(this);
  }
  
  /**
   * Read the object from a serialization stream.
   *
   * @param stream Input stream to read from
   *
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   */
  private void readObject(java.io.ObjectInputStream stream)
          throws java.io.IOException, javax.xml.transform.TransformerException
  {
    try
    {
      stream.defaultReadObject();
      m_clones =  new IteratorPool(this);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new javax.xml.transform.TransformerException(cnfe);
    }
  }
  
  /**
   * Set the environment in which this iterator operates, which should provide:
   * a node (the context node... same value as "root" defined below) 
   * a pair of non-zero positive integers (the context position and the context size) 
   * a set of variable bindings 
   * a function library 
   * the set of namespace declarations in scope for the expression.
   * 
   * <p>At this time the exact implementation of this environment is application 
   * dependent.  Probably a proper interface will be created fairly soon.</p>
   * 
   * @param environment The environment object.
   */
  public void setEnvironment(Object environment)
  {
    // no-op for now.
  }
  
  /**
   * Get an instance of a DTM that "owns" a node handle.  Since a node 
   * iterator may be passed without a DTMManager, this allows the 
   * caller to easily get the DTM using just the iterator.
   *
   * @param nodeHandle the nodeHandle.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(int nodeHandle)
  {
    // %OPT%
    return m_execContext.getDTM(nodeHandle);
  }
  
  /**
   * Get an instance of the DTMManager.  Since a node 
   * iterator may be passed without a DTMManager, this allows the 
   * caller to easily get the DTMManager using just the iterator.
   *
   * @return a non-null DTMManager reference.
   */
  public DTMManager getDTMManager()
  {
    return m_execContext.getDTMManager();
  }

  /**
   * Execute this iterator, meaning create a clone that can
   * store state, and initialize it for fast execution from
   * the current runtime state.  When this is called, no actual
   * query from the current context node is performed.
   *
   * @param xctxt The XPath execution context.
   *
   * @return An XNodeSet reference that holds this iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    LocPathIterator clone = (LocPathIterator)m_clones.getInstance();

    int current = xctxt.getCurrentNode();
    clone.setRoot(current, xctxt);

    return new XNodeSet(clone);
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Given an select expression and a context, evaluate the XPath
   * and return the resulting iterator.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * 
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DTMIterator asIterator(
          XPathContext xctxt, int contextNode)
            throws javax.xml.transform.TransformerException
  {
    LocPathIterator clone = (LocPathIterator)m_clones.getInstance();
    clone.setRoot(contextNode, xctxt);
    return clone;
  }

  
  /**
   * Tell if the expression is a nodeset expression.  In other words, tell 
   * if you can execute {@link asNode() asNode} without an exception.
   * @return true if the expression can be represented as a nodeset.
   */
  public boolean isNodesetExpr()
  {
    return true;
  }
  
  /**
   * Return the first node out of the nodeset, if this expression is 
   * a nodeset expression.  This is the default implementation for 
   * nodesets.  Derived classes should try and override this and return a 
   * value without having to do a clone operation.
   * @param xctxt The XPath runtime context.
   * @return the first node out of the nodeset, or DTM.NULL.
   */
  public int asNode(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
    DTMIterator iter = (DTMIterator)m_clones.getInstance();

    int current = xctxt.getCurrentNode();
    
    iter.setRoot(current, xctxt);

    int next = iter.nextNode();
    // m_clones.freeInstance(iter);
    iter.detach();
    return next;
  }
  
  /**
   * Evaluate this operation directly to a boolean.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a boolean.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return (asNode(xctxt) != DTM.NULL);
  }


  /**
   * <meta name="usage" content="advanced"/>
   * Set if this is an iterator at the upper level of
   * the XPath.
   *
   * @param b true if this location path is at the top level of the
   *          expression.
   */
  public void setIsTopLevel(boolean b)
  {
    m_isTopLevel = b;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get if this is an iterator at the upper level of
   * the XPath.
   *
   * @return true if this location path is at the top level of the
   *          expression.
   */
  public boolean getIsTopLevel()
  {
    return m_isTopLevel;
  }
  
  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {

    m_context = context;
    XPathContext xctxt = (XPathContext)environment;
    m_execContext = xctxt;
    m_cdtm = xctxt.getDTM(context);
    m_currentContextNode = context;
    m_prefixResolver = xctxt.getNamespaceContext();
        
//    m_lastFetched = DTM.NULL;
//    m_currentContextNode = DTM.NULL;
//    m_foundLast = false;
//    m_last = 0;
//    m_next = 0;

    if (m_isTopLevel)
      this.m_stackFrame = xctxt.getVarStack().getStackFrame();
      
    reset();
  }

  /**
   * Set the next position index of this iterator.
   *
   * @param next A value greater than or equal to zero that indicates the next
   * node position to fetch.
   */
  protected void setNextPosition(int next)
  {
    m_next = next;
  }

  /**
   * Get the current position, which is one less than
   * the next nextNode() call will retrieve.  i.e. if
   * you call getCurrentPos() and the return is 0, the next
   * fetch will take place at index 1.
   *
   * @return A value greater than or equal to zero that indicates the next
   * node position to fetch.
   */
  public final int getCurrentPos()
  {
    return m_next;
  }

  /**
   * Add one to the current node index.
   */
  void incrementNextPosition()
  {
    m_next++;
  }

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached.  They are not cached by default.
   *
   * @param b True if this iterator should cache nodes.
   */
  public void setShouldCacheNodes(boolean b)
  {

    if (b)
    {
      if(null == m_cachedNodes)
      {
        m_cachedNodes = new NodeSetDTM(getDTMManager());
      }
    }
    else
      m_cachedNodes = null;
  }
  
  /**
   * Tells if this iterator can have nodes added to it or set via 
   * the <code>setItem(int node, int index)</code> method.
   * 
   * @return True if the nodelist can be mutated.
   */
  public boolean isMutable()
  {
    return (m_cachedNodes != null);
  }


  /**
   * Get cached nodes.
   *
   * @return Cached nodes.
   */
  public NodeSetDTM getCachedNodes()
  {
    return m_cachedNodes;
  }

  /**
   * Set the current position in the node set.
   *
   * @param i Must be a valid index greater
   * than or equal to zero and less than m_cachedNodes.size().
   */
  public void setCurrentPos(int i)
  {

    // System.out.println("setCurrentPos: "+i);
    if (null == m_cachedNodes)
      throw new RuntimeException(
        "This NodeSetDTM can not do indexing or counting functions!");

    setNextPosition(i);
    m_cachedNodes.setCurrentPos(i);

    // throw new RuntimeException("Who's resetting this thing?");
  }

  /**
   * Get the length of the cached nodes.
   *
   * <p>Note: for the moment at least, this only returns
   * the size of the nodes that have been fetched to date,
   * it doesn't attempt to run to the end to make sure we
   * have found everything.  This should be reviewed.</p>
   *
   * @return The size of the current cache list.
   */
  public int size()
  {

    if (null == m_cachedNodes)
      return 0;

    return m_cachedNodes.size();
  }

  /**
   *  Returns the <code>index</code> th item in the collection. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * the list, this returns <code>null</code> .
   * @param index  Index into the collection.
   * @return  The node at the <code>index</code> th position in the
   *   <code>NodeList</code> , or <code>null</code> if that is not a valid
   *   index.
   */
  public int item(int index)
  {

    // resetToCachedList();

    return m_cachedNodes.item(index);
  }
  
  /**
   * Sets the node at the specified index of this vector to be the
   * specified node. The previous component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.  
   * The iterator must be in cached mode.</p>
   * 
   * <p>Meant to be used for sorted iterators.</p>
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  public void setItem(int node, int index)
  {
    m_cachedNodes.setElementAt(node, index);
  }

  /**
   *  The number of nodes in the list. The range of valid child node indices
   * is 0 to <code>length-1</code> inclusive.
   *
   * @return The number of nodes in the list, always greater or equal to zero.
   */
  public int getLength()
  {

    // resetToCachedList();
    if(m_last > 0)
      return m_last;
    else if(null == m_cachedNodes || !m_foundLast)
    {
      m_last = getLastPos(m_execContext);
    }
    else
    {
      m_last = m_cachedNodes.getLength();
    }
    return m_last;
  }

  /**
   * In order to implement NodeList (for extensions), try to reset
   * to a cached list for random access.
   */
  private void resetToCachedList()
  {
    // %REVIEW% ? This doesn't seem to work so well...
    int pos = this.getCurrentPos();

    if ((null == m_cachedNodes) || (pos != 0))
      this.setShouldCacheNodes(true);

    runTo(-1);
    this.setCurrentPos(pos);
  }

  /**
   * Tells if this NodeSetDTM is "fresh", in other words, if
   * the first nextNode() that is called will return the
   * first node in the set.
   *
   * @return true of nextNode has not been called.
   */
  public boolean isFresh()
  {
    return (m_next == 0);
  }

  /**
   *  Returns the previous node in the set and moves the position of the
   * iterator backwards in the set.
   * @return  The previous <code>Node</code> in the set being iterated over,
   *   or<code>null</code> if there are no more members in that set.
   */
  public int previousNode()
  {

    if (null == m_cachedNodes)
      throw new RuntimeException(
        "This NodeSetDTM can not iterate to a previous node!");

    return m_cachedNodes.previousNode();
  }

  /**
   * This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined in the
   * <code>NodeFilter</code> interface.
   *
   * <p>This is somewhat useless at this time, since it doesn't
   * really return information that tells what this iterator will
   * show.  It is here only to fullfill the DOM NodeIterator
   * interface.</p>
   *
   * @return For now, always NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  public int getWhatToShow()
  {

    // TODO: ??
    return DTMFilter.SHOW_ALL & ~DTMFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.  Not used at this time,
   * this is here only to fullfill the DOM NodeIterator
   * interface.
   *
   * @return Always null.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  public DTMFilter getFilter()
  {
    return null;
  }

  /**
   * The root node of the Iterator, as specified when it was created.
   *
   * @return The "root" of this iterator, which, in XPath terms,
   * is the node context for this iterator.
   */
  public int getRoot()
  {
    return m_context;
  }

  /**
   *  The value of this flag determines whether the children of entity
   * reference nodes are visible to the iterator. If false, they will be
   * skipped over.
   * <br> To produce a view of the document that has entity references
   * expanded and does not expose the entity reference node itself, use the
   * whatToShow flags to hide the entity reference node and set
   * expandEntityReferences to true when creating the iterator. To produce
   * a view of the document that has entity reference nodes but no entity
   * expansion, use the whatToShow flags to show the entity reference node
   * and set expandEntityReferences to false.
   *
   * @return Always true, since entity reference nodes are not
   * visible in the XPath model.
   */
  public boolean getExpandEntityReferences()
  {
    return true;
  }
  
  /** Control over whether it is OK for detach to reset the iterator. */
  private boolean m_allowDetach = true;
  
  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   * 
   * @param allowRelease true if it is OK for detach to release this iterator 
   * for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
    m_allowDetach = allowRelease;
  }

  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {    
    if(m_allowDetach)
    {
      m_cachedNodes = null;
      m_execContext = null;
      m_prefixResolver = null;
      m_cdtm = null;
      
      m_clones.freeInstance(this);
    }
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {

    // super.reset();
    m_foundLast = false;
    m_lastFetched = DTM.NULL;
    m_next = 0;
    m_last = 0;
  }

  /**
   * Get a cloned Iterator that is reset to the beginning
   * of the query.
   *
   * @return A cloned NodeIterator set of the start of the query.
   *
   * @throws CloneNotSupportedException
   */
  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    LocPathIterator clone = (LocPathIterator) clone();

    clone.reset();

    return clone;
  }

//  /**
//   * Get a cloned LocPathIterator that holds the same
//   * position as this iterator.
//   *
//   * @return A clone of this iterator that holds the same node position.
//   *
//   * @throws CloneNotSupportedException
//   */
//  public Object clone() throws CloneNotSupportedException
//  {
//
//    LocPathIterator clone = (LocPathIterator) super.clone();
//
//    return clone;
//  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public abstract int nextNode();

  /**
   * Bottleneck the return of a next node, to make returns
   * easier from nextNode().
   *
   * @param nextNode The next node found, may be null.
   *
   * @return The same node that was passed as an argument.
   */
  protected int returnNextNode(int nextNode)
  {

    if (DTM.NULL != nextNode)
    {
      if (null != m_cachedNodes)
        m_cachedNodes.addElement(nextNode);

      this.incrementNextPosition();
    }

    m_lastFetched = nextNode;

    if (DTM.NULL == nextNode)
      m_foundLast = true;

    return nextNode;
  }

  /**
   * Return the last fetched node.  Needed to support the UnionPathIterator.
   *
   * @return The last fetched node, or null if the last fetch was null.
   */
  public int getCurrentNode()
  {
    return m_lastFetched;
  }

  /**
   * If an index is requested, NodeSetDTM will call this method
   * to run the iterator to the index.  By default this sets
   * m_next to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end.
   *
   * @param index The index to run to, or -1 if the iterator
   * should run to the end.
   */
  public void runTo(int index)
  {

    if (m_foundLast || ((index >= 0) && (index <= getCurrentPos())))
      return;

    int n;

    if (-1 == index)
    {
      while (DTM.NULL != (n = nextNode()));
    }
    else
    {
      while (DTM.NULL != (n = nextNode()))
      {
        if (getCurrentPos() >= index)
          break;
      }
    }
  }

  /**
   * Tells if we've found the last node yet.
   *
   * @return true if the last nextNode returned null.
   */
  public final boolean getFoundLast()
  {
    return m_foundLast;
  }

  /**
   * The XPath execution context we are operating on.
   *
   * @return XPath execution context this iterator is operating on,
   * or null if setRoot has not been called.
   */
  public final XPathContext getXPathContext()
  {
    return m_execContext;
  }

  /**
   * The node context for the iterator.
   *
   * @return The node context, same as getRoot().
   */
  public final int getContext()
  {
    return m_context;
  }

  /**
   * The node context from where the expression is being
   * executed from (i.e. for current() support).
   *
   * @return The top-level node context of the entire expression.
   */
  public final int getCurrentContextNode()
  {
    return m_currentContextNode;
  }

  /**
   * Set the current context node for this iterator.
   *
   * @param n Must be a non-null reference to the node context.
   */
  public final void setCurrentContextNode(int n)
  {
    m_currentContextNode = n;
  }
  
//  /**
//   * Set the current context node for this iterator.
//   *
//   * @param n Must be a non-null reference to the node context.
//   */
//  public void setRoot(int n)
//  {
//    m_context = n;
//    m_cdtm = m_execContext.getDTM(n);
//  }

  /**
   * Return the saved reference to the prefix resolver that
   * was in effect when this iterator was created.
   *
   * @return The prefix resolver or this iterator, which may be null.
   */
  public final PrefixResolver getPrefixResolver()
  {
    return m_prefixResolver;
  }

  /**
   * Get the index of the last node in the iteration.
   *
   *
   * @return the index of the last node in the iteration.
   */
  public int getLast()
  {
    return getLength();
  }

  /**
   * Set the index of the last node in the iteration.
   *
   *
   * @param last the index of the last node in the iteration.
   */
  public void setLast(int last)
  {
    m_last = last;
  }

  /**
   * Get the index of the last node that can be itterated to.
   * This probably will need to be overridded by derived classes.
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
   */
  public int getLastPos(XPathContext xctxt)
  {
    int savedPos;
    if(null != m_cachedNodes)
      savedPos = m_cachedNodes.getCurrentPos();
    else 
      savedPos = -1;
      
    int pos = (m_predicateIndex >= 0) ? getProximityPosition() : 
              ((null != m_cachedNodes) ? m_cachedNodes.getCurrentPos() : m_next);
              
    LocPathIterator clone;

    try
    {
      // %REVIEW% %OPT%
      if(0 == pos && m_currentContextNode != DTM.NULL)
        clone = (LocPathIterator) cloneWithReset();
      else
        clone = (LocPathIterator) clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      return -1;
    }
    // %REVIEW% Commented this out, as it was messing up pos68 test. count-1?
    // System.out.println("clone.getPredicateCount(): "+clone.getPredicateCount());
    // clone.setPredicateCount(clone.getPredicateCount() - 1);

    int next;

    while (DTM.NULL != (next = clone.nextNode()))
    {
      pos++;
    }
    
    if(-1 != savedPos)
      m_cachedNodes.setCurrentPos(savedPos);

    return pos;
  }
  
//  /**
//   * Get the analysis pattern built by the WalkerFactory.
//   *
//   * @return The analysis pattern built by the WalkerFactory.
//   */
//  int getAnalysis()
//  {
//    return m_analysis;
//  }

//  /**
//   * Set the analysis pattern built by the WalkerFactory.
//   *
//   * @param a The analysis pattern built by the WalkerFactory.
//   */
//  void setAnalysis(int a)
//  {
//    m_analysis = a;
//  }
  
  //============= State Data =============
  
  /** 
   * The pool for cloned iterators.  Iterators need to be cloned
   * because the hold running state, and thus the original iterator
   * expression from the stylesheet pool can not be used.          
   */
  transient protected IteratorPool m_clones = new IteratorPool(this);
  
  /** 
   * The dtm of the context node.  Careful about using this... it may not 
   * be the dtm of the current node.
   */
  transient protected DTM m_cdtm;
  
  /**
   * The stack frame index for this iterator.
   */
  transient int m_stackFrame = -1;

  /**
   * Value determined at compile time, indicates that this is an
   * iterator at the top level of the expression, rather than inside
   * a predicate.
   * @serial
   */
  private boolean m_isTopLevel = false;

  /** The index of the last node in the iteration. */
  transient protected int m_last = 0;
  
  /** The last node that was fetched, usually by nextNode. */
  transient public int m_lastFetched = DTM.NULL;

  /**
   * If this iterator needs to cache nodes that are fetched, they
   * are stored here.
   */
  transient NodeSetDTM m_cachedNodes;

  /**
   * The context node for this iterator, which doesn't change through
   * the course of the iteration.
   */
  transient protected int m_context = DTM.NULL;

  /**
   * The node context from where the expression is being
   * executed from (i.e. for current() support).  Different
   * from m_context in that this is the context for the entire
   * expression, rather than the context for the subexpression.
   */
  transient protected int m_currentContextNode = DTM.NULL;

  /**
   * Fast access to the current prefix resolver.  It isn't really
   * clear that this is needed.
   * @serial
   */
  protected PrefixResolver m_prefixResolver;

  /**
   * The XPathContext reference, needed for execution of many
   * operations.
   */
  transient protected XPathContext m_execContext;

  /**
   * The index of the next node to be fetched.  Useful if this
   * is a cached iterator, and is being used as random access
   * NodeList.
   */
  transient protected int m_next;
  
  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * 
   * @return true as a default.
   */
  public boolean isDocOrdered()
  {
    return true;
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return -1;
  }


//  /**
//   * The analysis pattern built by the WalkerFactory.
//   * TODO: Move to LocPathIterator.
//   * @see org.apache.xpath.axes.WalkerFactory
//   * @serial
//   */
//  protected int m_analysis = 0x00000000;
}
