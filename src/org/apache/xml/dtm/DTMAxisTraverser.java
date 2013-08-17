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
package org.apache.xml.dtm;

/**
 * A class that implements traverses DTMAxisTraverser interface can traverse
 * a set of nodes, usually as defined by an XPath axis.  It is different from
 * an iterator, because it does not need to hold state, and, in fact, must not
 * hold any iteration-based state.  It is meant to be implemented as an inner
 * class of a DTM, and returned by the getAxisTraverser(final int axis)
 * function.
 *
 * <p>A DTMAxisTraverser can probably not traverse a reverse axis in
 * document order.</p>
 *
 * <p>Typical usage:</p>
 * <pre><code>
 * for(int nodeHandle=myTraverser.first(myContext);
 *     nodeHandle!=DTM.NULL;
 *     nodeHandle=myTraverser.next(myContext,nodeHandle))
 * { ... processing for node indicated by nodeHandle goes here ... }
 * </code></pre>
 *
 * @author Scott Boag
 */
public abstract class DTMAxisTraverser
{

  /**
   * By the nature of the stateless traversal, the context node can not be
   * returned or the iteration will go into an infinate loop.  So to traverse 
   * an axis, the first function must be used to get the first node.
   *
   * <p>This method needs to be overloaded only by those axis that process
   * the self node. <\p>
   *
   * @param context The context node of this traversal. This is the point
   * that the traversal starts from.
   * @return the first node in the traversal.
   */
  public int first(int context)
  {
    return next(context, context);
  }

  /**
   * By the nature of the stateless traversal, the context node can not be
   * returned or the iteration will go into an infinate loop.  So to traverse 
   * an axis, the first function must be used to get the first node.
   *
   * <p>This method needs to be overloaded only by those axis that process
   * the self node. <\p>
   *
   * @param context The context node of this traversal. This is the point
   * of origin for the traversal -- its "root node" or starting point.
   * @param extendedTypeID The extended type ID that must match.
   *
   * @return the first node in the traversal.
   */
  public int first(int context, int extendedTypeID)
  {
    return next(context, context, extendedTypeID);
  }

  /**
   * Traverse to the next node after the current node.
   *
   * @param context The context node of this traversal. This is the point
   * of origin for the traversal -- its "root node" or starting point.
   * @param current The current node of the traversal. This is the last known
   * location in the traversal, typically the node-handle returned by the
   * previous traversal step. For the first traversal step, context
   * should be set equal to current. Note that in order to test whether
   * context is in the set, you must use the first() method instead.
   *
   * @return the next node in the iteration, or DTM.NULL.
   * @see first(int)
   */
  public abstract int next(int context, int current);

  /**
   * Traverse to the next node after the current node that is matched
   * by the extended type ID.
   *
   * @param context The context node of this traversal. This is the point
   * of origin for the traversal -- its "root node" or starting point.
   * @param current The current node of the traversal. This is the last known
   * location in the traversal, typically the node-handle returned by the
   * previous traversal step. For the first traversal step, context
   * should be set equal to current. Note that in order to test whether
   * context is in the set, you must use the first() method instead.
   * @param extendedTypeID The extended type ID that must match.
   *
   * @return the next node in the iteration, or DTM.NULL.
   * @see first(int,int)
   */
  public abstract int next(int context, int current, int extendedTypeID);
}
