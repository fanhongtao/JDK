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

import java.util.Vector;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.objects.XObject;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.Axis;

/**
 * Walker for a reverse axes.
 * @see <a href="http://www.w3.org/TR/xpath#predicates">XPath 2.4 Predicates</a>
 */
public class ReverseAxesWalker extends AxesWalker
{

  /**
   * Construct an AxesWalker using a LocPathIterator.
   *
   * @param locPathIterator The location path iterator that 'owns' this walker.
   */
  ReverseAxesWalker(LocPathIterator locPathIterator, int axis)
  {
    super(locPathIterator, axis);
  }
  
  /**
   * Set the root node of the TreeWalker.
   * (Not part of the DOM2 TreeWalker interface).
   *
   * @param root The context node of this step.
   */
  public void setRoot(int root)
  {
    super.setRoot(root);
    m_iterator = getDTM(root).getAxisIterator(m_axis);
    m_iterator.setStartNode(root);
  }
  
  /**
   * Get the next node in document order on the axes.
   *
   * @return the next node in document order on the axes, or null.
   */
  protected int getNextNode()
  {
    if (m_foundLast)
      return DTM.NULL;

    int next = m_iterator.next();
    
    if (m_isFresh)
      m_isFresh = false;

    if (DTM.NULL == next)
      this.m_foundLast = true;

    return next;
  }


  /**
   * Tells if this is a reverse axes.  Overrides AxesWalker#isReverseAxes.
   *
   * @return true for this class.
   */
  public boolean isReverseAxes()
  {
    return true;
  }

//  /**
//   *  Set the root node of the TreeWalker.
//   *
//   * @param root The context node of this step.
//   */
//  public void setRoot(int root)
//  {
//    super.setRoot(root);
//  }

  /**
   * Get the current sub-context position.  In order to do the
   * reverse axes count, for the moment this re-searches the axes
   * up to the predicate.  An optimization on this is to cache
   * the nodes searched, but, for the moment, this case is probably
   * rare enough that the added complexity isn't worth it.
   *
   * @param predicateIndex The predicate index of the proximity position.
   *
   * @return The pridicate index, or -1.
   */
  protected int getProximityPosition(int predicateIndex)
  {
    // A negative predicate index seems to occur with
    // (preceding-sibling::*|following-sibling::*)/ancestor::*[position()]/*[position()]
    // -sb
    if(predicateIndex < 0)
      return -1;
      
    int count = m_proximityPositions[predicateIndex];
      
    if (count <= 0)
    {
      AxesWalker savedWalker = wi().getLastUsedWalker();

      try
      {
        ReverseAxesWalker clone = (ReverseAxesWalker) this.clone();

        clone.setRoot(this.getRoot());

        clone.setPredicateCount(predicateIndex);

        clone.setPrevWalker(null);
        clone.setNextWalker(null);
        wi().setLastUsedWalker(clone);

        // Count 'em all
        count++;
        int next;

        while (DTM.NULL != (next = clone.nextNode()))
        {
          count++;
        }

        m_proximityPositions[predicateIndex] = count;
      }
      catch (CloneNotSupportedException cnse)
      {

        // can't happen
      }
      finally
      {
        wi().setLastUsedWalker(savedWalker);
      }
    }
    
    return count;
  }

  /**
   * Count backwards one proximity position.
   *
   * @param i The predicate index.
   */
  protected void countProximityPosition(int i)
  {
    if (i < m_proximityPositions.length)
      m_proximityPositions[i]--;
  }

  /**
   * Get the number of nodes in this node list.  The function is probably ill
   * named?
   *
   *
   * @param xctxt The XPath runtime context.
   *
   * @return the number of nodes in this node list.
   */
  public int getLastPos(XPathContext xctxt)
  {

    int count = 0;
    AxesWalker savedWalker = wi().getLastUsedWalker();

    try
    {
      ReverseAxesWalker clone = (ReverseAxesWalker) this.clone();

      clone.setRoot(this.getRoot());

      clone.setPredicateCount(this.getPredicateCount() - 1);

      clone.setPrevWalker(null);
      clone.setNextWalker(null);
      wi().setLastUsedWalker(clone);

      // Count 'em all
      // count = 1;
      int next;

      while (DTM.NULL != (next = clone.nextNode()))
      {
        count++;
      }
    }
    catch (CloneNotSupportedException cnse)
    {

      // can't happen
    }
    finally
    {
      wi().setLastUsedWalker(savedWalker);
    }

    return count;
  }
  
  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * Warning: This can only be called after setRoot has been called!
   * 
   * @return false.
   */
  public boolean isDocOrdered()
  {
    return false;  // I think.
  }
  
  /** The DTM inner traversal class, that corresponds to the super axis. */
  protected DTMAxisIterator m_iterator;
}
