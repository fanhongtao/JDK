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
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.*;

/**
 * <code>DTMNodeIterator</code> gives us an implementation of the 
 * DTMNodeIterator which returns DOM nodes.
 *
 * Please note that this is not necessarily equivlaent to a DOM
 * NodeIterator operating over the same document. In particular:
 * <ul>
 *
 * <li>If there are several Text nodes in logical succession (ie,
 * across CDATASection and EntityReference boundaries), we will return
 * only the first; the caller is responsible for stepping through
 * them.
 * (%REVIEW% Provide a convenience routine here to assist, pending
 * proposed DOM Level 3 getAdjacentText() operation?) </li>
 *
 * <li>Since the whole XPath/XSLT architecture assumes that the source
 * document is not altered while we're working with it, we do not
 * promise to implement the DOM NodeIterator's "maintain current
 * position" response to document mutation. </li>
 *
 * <li>Since our design for XPath NodeIterators builds a stateful
 * filter directly into the traversal object, getNodeFilter() is not
 * supported.</li>
 *
 * </ul>
 *
 * <p>State: In progress!!</p>
 * */
public class DTMNodeIterator implements org.w3c.dom.traversal.NodeIterator
{
  private DTMIterator dtm_iter;
  private boolean valid=true;

  //================================================================
  // Methods unique to this class

  /** Public constructor: Wrap a DTMNodeIterator around an existing
   * and preconfigured DTMIterator
   * */
  public DTMNodeIterator(DTMIterator dtmIterator)
    {
      try
      {
        dtm_iter=(DTMIterator)dtmIterator.clone();
      }
      catch(CloneNotSupportedException cnse)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
      }
    }

  /** Access the wrapped DTMIterator. I'm not sure whether anyone will
   * need this or not, but let's write it and think about it.
   * */
  public DTMIterator getDTMIterator()
    {
      return dtm_iter;
    }
  

  //================================================================
  // org.w3c.dom.traversal.NodeFilter API follows

  /** Detaches the NodeIterator from the set which it iterated over,
   * releasing any computational resources and placing the iterator in
   * the INVALID state.
   * */
  public void detach() 
    {
      // Theoretically, we could release dtm_iter at this point. But
      // some of the operations may still want to consult it even though
      // navigation is now invalid.
      valid=false;
    }

  /** The value of this flag determines whether the children
   * of entity reference nodes are visible to the iterator.
   *
   * @return false, always (the DTM model flattens entity references)
   * */
  public boolean getExpandEntityReferences()
    {
      return false;
    }
  
  /** Return a handle to the filter used to screen nodes.
   *
   * This is ill-defined in Xalan's usage of Nodeiterator, where we have
   * built stateful XPath-based filtering directly into the traversal
   * object. We could return something which supports the NodeFilter interface
   * and allows querying whether a given node would be permitted if it appeared
   * as our next node, but in the current implementation that would be very
   * complex -- and just isn't all that useful.
   * 
   * @throws DOMException -- NOT_SUPPORTED_ERROR because I can't think
   * of anything more useful to do in this case
   * */
  public NodeFilter getFilter() 
    {
      throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
    }
  

  /** @return The root node of the NodeIterator, as specified
   * when it was created.
   * */
  public Node getRoot()
    {
      int handle=dtm_iter.getRoot();
      return dtm_iter.getDTM(handle).getNode(handle);
    }
  

  /** Return a mask describing which node types are presented via the
   * iterator.
   **/
  public int getWhatToShow()
    {
      return dtm_iter.getWhatToShow();
    }

  /** @return the next node in the set and advance the position of the
   * iterator in the set.
   *
   * @throw DOMException - INVALID_STATE_ERR Raised if this method is
   * called after the detach method was invoked.
   *  */
  public Node nextNode() throws DOMException
    {
      if(!valid)
        throw new DTMDOMException(DOMException.INVALID_STATE_ERR);
      
      int handle=dtm_iter.nextNode();
      if (handle==-1)
        return null;
      return dtm_iter.getDTM(handle).getNode(handle);
    }
  

  /** @return the next previous in the set and advance the position of the
   * iterator in the set.
   *
   * @throw DOMException - INVALID_STATE_ERR Raised if this method is
   * called after the detach method was invoked.
   *  */
  public Node previousNode() 
    {
      if(!valid)
        throw new DTMDOMException(DOMException.INVALID_STATE_ERR);
      
      int handle=dtm_iter.previousNode();
      return dtm_iter.getDTM(handle).getNode(handle);
    }
}
