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
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.*;

import javax.xml.transform.Source;

import org.apache.xml.utils.XMLStringFactory;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


/**
 * This class implements the traversers for DTMDefaultBase.
 */
public abstract class DTMDefaultBaseIterators extends DTMDefaultBaseTraversers
{

  /**
   * Construct a DTMDefaultBaseTraversers object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param domSource the DOM source that this DTM will wrap.
   * @param source The object that is used to specify the construction source.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may
   *                         be null.
   * @param xstringfactory The factory to use for creating XMLStrings.
   * @param doIndexing true if the caller considers it worth it to use 
   *                   indexing schemes.
   */
  public DTMDefaultBaseIterators(DTMManager mgr, Source source,
                                 int dtmIdentity,
                                 DTMWSFilter whiteSpaceFilter,
                                 XMLStringFactory xstringfactory,
                                 boolean doIndexing)
  {
    super(mgr, source, dtmIdentity, whiteSpaceFilter, 
          xstringfactory, doIndexing);
  }

  /**
   * Get an iterator that can navigate over an XPath Axis, predicated by
   * the extended type ID.
   * Returns an iterator that must be initialized
   * with a start node (using iterator.setStartNode()).
   *
   * @param axis One of Axes.ANCESTORORSELF, etc.
   * @param type An extended type ID.
   *
   * @return A DTMAxisIterator, or null if the given axis isn't supported.
   */
  public DTMAxisIterator getTypedAxisIterator(int axis, int type)
  {

    DTMAxisIterator iterator = null;

    /* This causes an error when using patterns for elements that
       do not exist in the DOM (translet types which do not correspond
       to a DOM type are mapped to the DOM.ELEMENT type).
    */

    //        if (type == NO_TYPE) {
    //            return(EMPTYITERATOR);
    //        }
    //        else if (type == ELEMENT) {
    //            iterator = new FilterIterator(getAxisIterator(axis),
    //                                          getElementFilter());
    //        }
    //        else 
    {
      switch (axis)
      {
      case Axis.SELF :
        iterator = new TypedSingletonIterator(type);
        break;
      case Axis.CHILD :
        iterator = new TypedChildrenIterator(type);
        break;
      case Axis.PARENT :
        return (new ParentIterator().setNodeType(type));
      case Axis.ANCESTOR :
        return (new TypedAncestorIterator(type));
      case Axis.ANCESTORORSELF :
        return ((new TypedAncestorIterator(type)).includeSelf());
      case Axis.ATTRIBUTE :
        return (new TypedAttributeIterator(type));
      case Axis.DESCENDANT :
        iterator = new TypedDescendantIterator(type);
        break;
      case Axis.DESCENDANTORSELF :
        iterator = (new TypedDescendantIterator(type)).includeSelf();
        break;
      case Axis.FOLLOWING :
        iterator = new TypedFollowingIterator(type);
        break;
      case Axis.PRECEDING :
        iterator = new TypedPrecedingIterator(type);
        break;
      case Axis.FOLLOWINGSIBLING :
        iterator = new TypedFollowingSiblingIterator(type);
        break;
      case Axis.PRECEDINGSIBLING :
        iterator = new TypedPrecedingSiblingIterator(type);
        break;
      case Axis.NAMESPACE :
        iterator = new TypedNamespaceIterator(type);
        break;
      case Axis.ROOT :
        iterator = new TypedRootIterator(type);
        break;
      default :
        throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED, new Object[]{Axis.names[axis]})); //"Error: typed iterator for axis "
                               //+ Axis.names[axis] + "not implemented");
      }
    }

    return (iterator);
  }

  /**
   * This is a shortcut to the iterators that implement the
   * XPath axes.
   * Returns a bare-bones iterator that must be initialized
   * with a start node (using iterator.setStartNode()).
   *
   * @param axis One of Axes.ANCESTORORSELF, etc.
   *
   * @return A DTMAxisIterator, or null if the given axis isn't supported.
   */
  public DTMAxisIterator getAxisIterator(final int axis)
  {

    DTMAxisIterator iterator = null;

    switch (axis)
    {
    case Axis.SELF :
      iterator = new SingletonIterator();
      break;
    case Axis.CHILD :
      iterator = new ChildrenIterator();
      break;
    case Axis.PARENT :
      return (new ParentIterator());
    case Axis.ANCESTOR :
      return (new AncestorIterator());
    case Axis.ANCESTORORSELF :
      return ((new AncestorIterator()).includeSelf());
    case Axis.ATTRIBUTE :
      return (new AttributeIterator());
    case Axis.DESCENDANT :
      iterator = new DescendantIterator();
      break;
    case Axis.DESCENDANTORSELF :
      iterator = (new DescendantIterator()).includeSelf();
      break;
    case Axis.FOLLOWING :
      iterator = new FollowingIterator();
      break;
    case Axis.PRECEDING :
      iterator = new PrecedingIterator();
      break;
    case Axis.FOLLOWINGSIBLING :
      iterator = new FollowingSiblingIterator();
      break;
    case Axis.PRECEDINGSIBLING :
      iterator = new PrecedingSiblingIterator();
      break;
    case Axis.NAMESPACE :
      iterator = new NamespaceIterator();
      break;
    case Axis.ROOT :
      iterator = new RootIterator();
      break;
    default :
      throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_ITERATOR_AXIS_NOT_IMPLEMENTED, new Object[]{Axis.names[axis]})); //"Error: iterator for axis '" + Axis.names[axis]
                             //+ "' not implemented");
    }

    return (iterator);
  }

  /**
   * Abstract superclass defining behaviors shared by all DTMDefault's
   * internal implementations of DTMAxisIterator. Subclass this (and
   * override, if necessary) to implement the specifics of an
   * individual axis iterator.
   *
   * Currently there isn't a lot here
   */
  private abstract class InternalAxisIteratorBase extends DTMAxisIteratorBase
  {

    // %REVIEW% We could opt to share _nodeType and setNodeType() as
    // well, and simply ignore them in iterators which don't use them.
    // But Scott's worried about the overhead involved in cloning
    // these, and wants them to have as few fields as possible. Note
    // that we can't create a TypedInternalAxisIteratorBase because
    // those are often based on the untyped versions and Java doesn't
    // support multiple inheritance. <sigh/>

    /**
     * Current iteration location. Usually this is the last location
     * returned (starting point for the next() search); for single-node
     * iterators it may instead be initialized to point to that single node.
     */
    protected int _currentNode;

    /**
     * Remembers the current node for the next call to gotoMark().
     *
     * %REVIEW% Should this save _position too?
     */
    public void setMark()
    {
      _markedNode = _currentNode;
    }

    /**
     * Restores the current node remembered by setMark().
     *
     * %REVEIW% Should this restore _position too?
     */
    public void gotoMark()
    {
      _currentNode = _markedNode;
    }
  }  // end of InternalAxisIteratorBase 

  /**
   * Iterator that returns all immediate children of a given node
   */
  private final class ChildrenIterator extends InternalAxisIteratorBase
  {

    /**
     * Setting start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * If the iterator is not restartable, this has no effect.
     * %REVIEW% Should it return/throw something in that case,
     * or set current node to END, to indicate request-not-honored?
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(final int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = NOTPROCESSED;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END if no more
     * are available.
     */
    public int next()
    {

      _currentNode = (NOTPROCESSED == _currentNode)
                     ? getFirstChild(_startNode)
                     : getNextSibling(_currentNode);

      return returnNode(_currentNode);
    }
  }  // end of ChildrenIterator

  /**
   * Iterator that returns the parent of a given node. Note that
   * this delivers only a single node; if you want all the ancestors,
   * see AncestorIterator.
   */
  private final class ParentIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private int _nodeType = -1;

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getParent(node);

        return resetPosition();
      }

      return this;
    }

    /**
     * Set the node type of the parent that we're looking for.
     * Note that this does _not_ mean "find the nearest ancestor of
     * this type", but "yield the parent if it is of this type".
     *
     *
     * @param type extended type ID.
     *
     * @return ParentIterator configured with the type filter set.
     */
    public DTMAxisIterator setNodeType(final int type)
    {

      _nodeType = type;

      return this;
    }

    /**
     * Get the next node in the iteration. In this case, we return
     * only the immediate parent, _if_ it matches the requested nodeType.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int result = _currentNode;

      if ((_nodeType != -1) && (getExpandedTypeID(_currentNode) != _nodeType))
        result = END;
      else
        result = _currentNode;

      _currentNode = END;

      return returnNode(result);
    }
  }  // end of ParentIterator

  /**
   * Iterator that returns children of a given type for a given node.
   * The functionality chould be achieved by putting a filter on top
   * of a basic child iterator, but a specialised iterator is used
   * for efficiency (both speed and size of translet).
   */
  private final class TypedChildrenIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedChildrenIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedChildrenIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = NOTPROCESSED;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      for (int node = (NOTPROCESSED == _currentNode)
                      ? getFirstChild(_startNode)
                      : getNextSibling(_currentNode); node
                        != END; node = getNextSibling(node))
      {
        if (getExpandedTypeID(node) == _nodeType)
        {
          _currentNode = node;

          return returnNode(node);
        }
      }

      return END;
    }
  }  // end of TypedChildrenIterator

  /**
   * Iterator that returns children within a given namespace for a
   * given node. The functionality chould be achieved by putting a
   * filter on top of a basic child iterator, but a specialised
   * iterator is used for efficiency (both speed and size of translet).
   */
  private final class NamespaceChildrenIterator
          extends InternalAxisIteratorBase
  {

    /** The extended type ID being requested. */
    private final int _nsType;

    /**
     * Constructor NamespaceChildrenIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public NamespaceChildrenIterator(final int type)
    {
      _nsType = type;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = NOTPROCESSED;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      for (int node = (NOTPROCESSED == _currentNode)
                      ? getFirstChild(_startNode)
                      : getNextSibling(_currentNode); node
                        != END; node = getNextSibling(node))
      {
        if (getNamespaceType(node) == _nsType)
        {
          _currentNode = node;

          return returnNode(node);
        }
      }

      return END;
    }
  }  // end of TypedChildrenIterator
  
  /**
   * Iterator that returns the namespace nodes as defined by the XPath data model 
   * for a given node.
   */
  private class NamespaceIterator
          extends InternalAxisIteratorBase
  {

    /**
     * Constructor NamespaceAttributeIterator
     */
    public NamespaceIterator()
    {

      super();
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstNamespaceNode(node, true);

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node = _currentNode;

      if (DTM.NULL != node)
        _currentNode = getNextNamespaceNode(_startNode, node, true);

      return returnNode(node);
    }
  }  // end of NamespaceIterator
  
  /**
   * Iterator that returns the namespace nodes as defined by the XPath data model 
   * for a given node, filtered by extended type ID.
   */
  private class TypedNamespaceIterator extends NamespaceIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedChildrenIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedNamespaceIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      for (int node = super.next(); node != END; node = super.next())
      {
        if (getExpandedTypeID(node) == _nodeType)
        {
          _currentNode = node;

          return returnNode(node);
        }
      }

      return END;
    }
  }  // end of TypedNamespaceIterator
  
  /**
   * Iterator that returns the the root node as defined by the XPath data model 
   * for a given node.
   */
  private class RootIterator
          extends InternalAxisIteratorBase
  {

    /**
     * Constructor RootIterator
     */
    public RootIterator()
    {

      super();
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = getDocumentRoot(node);
        _currentNode = NULL;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      if(_startNode == _currentNode)
        return NULL;

      _currentNode = _startNode;

      return returnNode(_startNode);
    }
  }  // end of RootIterator
  
  /**
   * Iterator that returns the namespace nodes as defined by the XPath data model 
   * for a given node, filtered by extended type ID.
   */
  private class TypedRootIterator extends RootIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedRootIterator
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedRootIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      for (int node = super.next(); node != END; node = super.next())
      {
        if (getExpandedTypeID(node) == _nodeType)
        {
          _currentNode = node;

          return returnNode(node);
        }
      }

      return END;
    }
  }  // end of TypedRootIterator

  /**
   * Iterator that returns attributes within a given namespace for a node.
   */
  private final class NamespaceAttributeIterator
          extends InternalAxisIteratorBase
  {

    /** The extended type ID being requested. */
    private final int _nsType;

    /**
     * Constructor NamespaceAttributeIterator
     *
     *
     * @param nsType The extended type ID being requested.
     */
    public NamespaceAttributeIterator(int nsType)
    {

      super();

      _nsType = nsType;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstNamespaceNode(node, false);

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node = _currentNode;

      if (DTM.NULL != node)
        _currentNode = getNextNamespaceNode(_startNode, node, false);

      return returnNode(node);
    }
  }  // end of TypedChildrenIterator

  /**
   * Iterator that returns all siblings of a given node.
   */
  private class FollowingSiblingIterator extends InternalAxisIteratorBase
  {

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _currentNode = _startNode = node;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      return returnNode(_currentNode = getNextSibling(_currentNode));
    }
  }  // end of FollowingSiblingIterator

  /**
   * Iterator that returns all following siblings of a given node.
   */
  private final class TypedFollowingSiblingIterator
          extends FollowingSiblingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedFollowingSiblingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedFollowingSiblingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL
             && getExpandedTypeID(node) != _nodeType){}

      return node;
    }
  }  // end of TypedFollowingSiblingIterator

  /**
   * Iterator that returns attribute nodes (of what nodes?)
   */
  private final class AttributeIterator extends InternalAxisIteratorBase
  {

    // assumes caller will pass element nodes

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstAttribute(node);

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int node = _currentNode;

      _currentNode = getNextAttribute(node);

      return returnNode(node);
    }
  }  // end of AttributeIterator

  /**
   * Iterator that returns attribute nodes of a given type
   */
  private final class TypedAttributeIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedAttributeIterator
     *
     *
     * @param nodeType The extended type ID that is requested.
     */
    public TypedAttributeIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    // assumes caller will pass element nodes

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;

        for (node = getFirstAttribute(node); node != END;
                node = getNextAttribute(node))
        {
          if (getExpandedTypeID(node) == _nodeType)
            break;
        }

        _currentNode = node;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int node = _currentNode;

      // singleton iterator, since there can only be one attribute of 
      // a given type.
      _currentNode = NULL;

      return returnNode(node);
    }
  }  // end of TypedAttributeIterator

  /**
   * Iterator that returns preceding siblings of a given node
   */
  private class PrecedingSiblingIterator extends InternalAxisIteratorBase
  {

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true.
     */
    public boolean isReverse()
    {
      return true;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
      if (_isRestartable)
      {
        _startNode = node;

        if(node == NULL)
        {
          _currentNode = node;
          return resetPosition();
        }
          
        int type = m_expandedNameTable.getType(getExpandedTypeID(node));
        if(ExpandedNameTable.ATTRIBUTE == type 
           || ExpandedNameTable.NAMESPACE == type )
        {
          _currentNode = node;
        }
        else
				{
					// Be careful to handle the Document node properly
					_currentNode = getParent(node);
					if(NULL!=_currentNode)	
						_currentNode = getFirstChild(_currentNode);
					else
						_currentNode = node;
				}

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      if (_currentNode == _startNode)
      {
        return NULL;
      }
      else
      {
        final int node = _currentNode;
        _currentNode = getNextSibling(node);

        return returnNode(node);
      }
    }
  }  // end of PrecedingSiblingIterator

  /**
   * Iterator that returns preceding siblings of a given type for
   * a given node
   */
  private final class TypedPrecedingSiblingIterator
          extends PrecedingSiblingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedPrecedingSiblingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedPrecedingSiblingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL
             && getExpandedTypeID(node) != _nodeType){}

      return node;
    }
  }  // end of PrecedingSiblingIterator

  /**
   * Iterator that returns preceding nodes of a given node.
   * This includes the node set {root+1, start-1}, but excludes
   * all ancestors, attributes, and namespace nodes.
   */
  private class PrecedingIterator extends InternalAxisIteratorBase
  {

    /** The max ancestors, but it can grow... */
    private final int _maxAncestors = 8;

    /**
     * The stack of start node + ancestors up to the root of the tree,
     *  which we must avoid.
     */
    private int[] _stack = new int[_maxAncestors];

    /** (not sure yet... -sb) */
    private int _sp, _oldsp;

    /* _currentNode precedes candidates.  This is the identity, not the handle! */

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true since this iterator is a reversed axis.
     */
    public boolean isReverse()
    {
      return true;
    }

    /**
     * Returns a deep copy of this iterator.   The cloned iterator is not reset.
     *
     * @return a deep copy of this iterator.
     */
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;

      try
      {
        final PrecedingIterator clone = (PrecedingIterator) super.clone();
        final int[] stackCopy = new int[_stack.length];
        System.arraycopy(_stack, 0, stackCopy, 0, _stack.length);

        clone._stack = stackCopy;

        // return clone.reset();
        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); //"Iterator clone not supported.");
      }
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        node = makeNodeIdentity(node);

        // iterator is not a clone
        int parent, index;

        _startNode = node;
        _stack[index = 0] = node;

		parent=node;
		while ((parent = _parent(parent)) != NULL)
		{
			if (++index == _stack.length)
			{
				final int[] stack = new int[index + 4];
				System.arraycopy(_stack, 0, stack, 0, index);
				_stack = stack;
			}
			_stack[index] = parent;
        }
        if(index>0)
	        --index; // Pop actual root node (if not start) back off the stack

        _currentNode=_stack[index]; // Last parent before root node

        _oldsp = _sp = index;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
    	// Bugzilla 8324: We were forgetting to skip Attrs and NS nodes.
    	// Also recoded the loop controls for clarity and to flatten out
    	// the tail-recursion.
   		for(++_currentNode; 
   			_sp>=0; 
   			++_currentNode)
   		{
   			if(_currentNode < _stack[_sp])
   			{
   				if(_type(_currentNode) != ATTRIBUTE_NODE &&
   					_type(_currentNode) != NAMESPACE_NODE)
   					return returnNode(makeNodeHandle(_currentNode));
   			}
   			else
   				--_sp;
   		}
   		return NULL;
    }

    // redefine DTMAxisIteratorBase's reset

    /**
     * Resets the iterator to the last start node.
     *
     * @return A DTMAxisIterator, which may or may not be the same as this
     *         iterator.
     */
    public DTMAxisIterator reset()
    {

      _sp = _oldsp;

      return resetPosition();
    }
  }  // end of PrecedingIterator

  /**
   * Iterator that returns preceding nodes of agiven type for a
   * given node. This includes the node set {root+1, start-1}, but
   * excludes all ancestors.
   */
  private final class TypedPrecedingIterator extends PrecedingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedPrecedingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedPrecedingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL
             && getExpandedTypeID(node) != _nodeType){}

      return node;
    }
  }  // end of TypedPrecedingIterator

  /**
   * Iterator that returns following nodes of for a given node.
   */
  private class FollowingIterator extends InternalAxisIteratorBase
  {
    DTMAxisTraverser m_traverser; // easier for now
    
    public FollowingIterator()
    {
      m_traverser = getAxisTraverser(Axis.FOLLOWING);
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = node;

        // ?? -sb
        // find rightmost descendant (or self)
        // int current;
        // while ((node = getLastChild(current = node)) != NULL){}
        // _currentNode = current;
        _currentNode = m_traverser.first(node);

        // _currentNode precedes possible following(node) nodes
        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node = _currentNode;

      _currentNode = m_traverser.next(_startNode, _currentNode);

      return returnNode(node);
    }
  }  // end of FollowingIterator

  /**
   * Iterator that returns following nodes of a given type for a given node.
   */
  private final class TypedFollowingIterator extends FollowingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedFollowingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedFollowingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL
             && getExpandedTypeID(node) != _nodeType){}

      return returnNode(node);
    }
  }  // end of TypedFollowingIterator

  /**
   * Iterator that returns the ancestors of a given node in document
   * order.  (NOTE!  This was changed from the XSLTC code!)
   */
  private class AncestorIterator extends InternalAxisIteratorBase
  {
    org.apache.xml.utils.NodeVector m_ancestors = 
         new org.apache.xml.utils.NodeVector();
         
    int m_ancestorsPos;
    
    /** The real start node for this axes, since _startNode will be adjusted. */
    int m_realStartNode;
    
    /**
     * Get start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @return The root node of the iteration.
     */
    public int getStartNode()
    {
      return m_realStartNode;
    }

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true since this iterator is a reversed axis.
     */
    public final boolean isReverse()
    {
      return true;
    }

    /**
     * Returns the last element in this interation.
     *
     * %TBD% %BUG% This is returning a nodeHandle rather than a _position
     * value. That conflicts with what everyone else is doing. And it's
     * talking about the start node, which conflicts with some of the
     * other reverse iterators. DEFINITE BUG; needs to be reconciled.
     *
     * @return the last element in this interation.
     */
    public int getLast()
    {
      return (_startNode);
    }

    /**
     * Returns a deep copy of this iterator.  The cloned iterator is not reset.
     *
     * @return a deep copy of this iterator.
     */
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;  // must set to false for any clone

      try
      {
        final AncestorIterator clone = (AncestorIterator) super.clone();

        clone._startNode = _startNode;

        // return clone.reset();
        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); //"Iterator clone not supported.");
      }
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
      m_realStartNode = node;

      if (_isRestartable)
      {
        if (_includeSelf)
          _startNode = node;
        else
          _startNode = getParent(node);

        node = _startNode;
        while (node != END)
        {
          m_ancestors.addElement(node);
          node = getParent(node);
        }
        m_ancestorsPos = m_ancestors.size()-1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors.elementAt(m_ancestorsPos)
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
    }

    /**
     * Resets the iterator to the last start node.
     *
     * @return A DTMAxisIterator, which may or may not be the same as this
     *         iterator.
     */
    public DTMAxisIterator reset()
    {

      m_ancestorsPos = m_ancestors.size()-1;

      _currentNode = (m_ancestorsPos>=0) ? m_ancestors.elementAt(m_ancestorsPos)
                                         : DTM.NULL;

      return resetPosition();
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int next = _currentNode;
      
      int pos = --m_ancestorsPos;

      _currentNode = (pos >= 0) ? m_ancestors.elementAt(m_ancestorsPos)
                                : DTM.NULL;
      
      return returnNode(next);
    }
  }  // end of AncestorIterator

  /**
   * Typed iterator that returns the ancestors of a given node.
   */
  private final class TypedAncestorIterator extends AncestorIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedAncestorIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedAncestorIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL)
      {
        if (getExpandedTypeID(node) == _nodeType)
          return returnNode(node);
      }

      return (NULL);
    }

    /**
     * Returns the last element in this interation.
     *
     * @return the last element in this interation.
     */
    public int getLast()
    {

      int last = NULL;
      int curr = _startNode;

      while (curr != NULL)
      {
        if (getExpandedTypeID(curr) == _nodeType)
          last = curr;

        curr = getParent(curr);
      }

      return (last);
    }
  }  // end of TypedAncestorIterator

  /**
   * Iterator that returns the descendants of a given node.
   */
  private class DescendantIterator extends InternalAxisIteratorBase
  {

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        node = makeNodeIdentity(node);
        _startNode = node;

        if (_includeSelf)
          node--;

        _currentNode = node;

        return resetPosition();
      }

      return this;
    }

    /**
     * Tell if this node identity is a descendant.  Assumes that
     * the node info for the element has already been obtained.
     *
     * This one-sided test works only if the parent has been
     * previously tested and is known to be a descendent. It fails if
     * the parent is the _startNode's next sibling, or indeed any node
     * that follows _startNode in document order.  That may suffice
     * for this iterator, but it's not really an isDescendent() test.
     * %REVIEW% rename?
     *
     * @param identity The index number of the node in question.
     * @return true if the index is a descendant of _startNode.
     */
    protected boolean isDescendant(int identity)
    {
      return (_startNode == identity) || _parent(identity) >= _startNode;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      while (true)
      {
        int node = ++_currentNode;
        int type = _type(node);

        if (NULL == type ||!isDescendant(node))
          return END;

        if (ATTRIBUTE_NODE == type || NAMESPACE_NODE == type)
          continue;

        return returnNode(makeNodeHandle(node));  // make handle.
      }
    }
  }  // end of DescendantIterator

  /**
   * Typed iterator that returns the descendants of a given node.
   */
  private final class TypedDescendantIterator extends DescendantIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedDescendantIterator
     *
     *
     * @param nodeType Extended type ID being requested.
     */
    public TypedDescendantIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      while ((node = super.next()) != NULL
             && getExpandedTypeID(node) != _nodeType){}

      return node;
    }
  }  // end of TypedDescendantIterator

  /**
   * Iterator that returns the descendants of a given node.
   * I'm not exactly clear about this one... -sb
   */
  private class NthDescendantIterator extends DescendantIterator
  {

    /** The current nth position. */
    int _pos;

    /**
     * Constructor NthDescendantIterator
     *
     *
     * @param pos The nth position being requested.
     */
    public NthDescendantIterator(int pos)
    {
      _pos = pos;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      // I'm not exactly clear yet what this is doing... -sb
      int node;

      while ((node = super.next()) != END)
      {
        node = makeNodeIdentity(node);

        int parent = _parent(node);
        int child = _firstch(parent);
        int pos = 0;

        do
        {
          int type = _type(child);

          if (ELEMENT_NODE == type)
            pos++;
        }
        while ((pos < _pos) && (child = _nextsib(child)) != END);

        if (node == child)
          return node;
      }

      return (END);
    }
  }  // end of NthDescendantIterator

  /**
   * Class SingletonIterator.
   */
  private class SingletonIterator extends InternalAxisIteratorBase
  {

    /** (not sure yet what this is.  -sb)  (sc & sb remove final to compile in JDK 1.1.8) */
    private boolean _isConstant;

    /**
     * Constructor SingletonIterator
     *
     */
    public SingletonIterator()
    {
      this(Integer.MIN_VALUE, false);
    }

    /**
     * Constructor SingletonIterator
     *
     *
     * @param node The node handle to return.
     */
    public SingletonIterator(int node)
    {
      this(node, false);
    }

    /**
     * Constructor SingletonIterator
     *
     *
     * @param node the node handle to return.
     * @param constant (Not sure what this is yet.  -sb)
     */
    public SingletonIterator(int node, boolean constant)
    {
      _currentNode = _startNode = node;
      _isConstant = constant;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {

      if (_isConstant)
      {
        _currentNode = _startNode;

        return resetPosition();
      }
      else if (_isRestartable)
      {
        if (_currentNode == Integer.MIN_VALUE)
        {
          _currentNode = _startNode = node;
        }

        return resetPosition();
      }

      return this;
    }

    /**
     * Resets the iterator to the last start node.
     *
     * @return A DTMAxisIterator, which may or may not be the same as this
     *         iterator.
     */
    public DTMAxisIterator reset()
    {

      if (_isConstant)
      {
        _currentNode = _startNode;

        return resetPosition();
      }
      else
      {
        final boolean temp = _isRestartable;

        _isRestartable = true;

        setStartNode(_startNode);

        _isRestartable = temp;
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int result = _currentNode;

      _currentNode = END;

      return returnNode(result);
    }
  }

  /**
   * Iterator that returns a given node only if it is of a given type.
   */
  private final class TypedSingletonIterator extends SingletonIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedSingletonIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedSingletonIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int result = super.next();

      return getExpandedTypeID(result) == _nodeType ? result : NULL;
    }
  }  // end of TypedSingletonIterator
}
