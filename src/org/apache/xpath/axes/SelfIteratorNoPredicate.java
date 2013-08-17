package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;

import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolver;

//import org.w3c.dom.Node;
//import org.w3c.dom.DOMException;
import org.apache.xml.dtm.DTM;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * "." patterns, that is, the self axes without any predicates.  
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class SelfIteratorNoPredicate extends LocPathIterator
{

  /**
   * Create a SelfIteratorNoPredicate object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   * @param analysis Analysis bits.
   *
   * @throws javax.xml.transform.TransformerException
   */
  SelfIteratorNoPredicate(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, false);
  }
  
  /**
   * Create a SelfIteratorNoPredicate object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   * @param analysis Analysis bits.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public SelfIteratorNoPredicate()
          throws javax.xml.transform.TransformerException
  {
    super(null);
  }


  /**
   *  Returns the next node in the set and advances the position of the
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
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if ((null != m_cachedNodes)
            && (m_next < m_cachedNodes.size()))
    {
      int next = m_cachedNodes.elementAt(m_next);
    
      incrementNextPosition();
      m_currentContextNode = next;

      return next;
    }

    if (m_foundLast)
    {
      m_lastFetched = DTM.NULL;
      return DTM.NULL;
    }

    int next;
    DTM dtm = m_cdtm;

    m_lastFetched = next = (DTM.NULL == m_lastFetched)
                           ? m_context
                           : DTM.NULL;

    // m_lastFetched = next;
    if (DTM.NULL != next)
    {
      if (null != m_cachedNodes)
        m_cachedNodes.addElement(m_lastFetched);

      m_next++;

      return next;
    }
    else
    {
      m_foundLast = true;

      return DTM.NULL;
    }
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
    return xctxt.getCurrentNode();
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
    return 1;
  }


}
