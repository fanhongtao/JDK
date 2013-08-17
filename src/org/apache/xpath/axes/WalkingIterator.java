package org.apache.xpath.axes;

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
 * Location path iterator that uses Walkers.
 */

public class WalkingIterator extends LocPathIterator
{
  /**
   * Create a WalkingIterator iterator, including creation
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
  WalkingIterator(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, shouldLoadWalkers);
    
    int firstStepPos = compiler.getFirstChildPos(opPos);

    if (shouldLoadWalkers)
    {
      m_firstWalker = WalkerFactory.loadWalkers(this, compiler, firstStepPos, 0);
      m_lastUsedWalker = m_firstWalker;
    }
  }
  
  /**
   * Create a WalkingIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  protected WalkingIterator(PrefixResolver nscontext)
  {

    super(nscontext);
  }

  
  /**
   * Get a cloned WalkingIterator that holds the same
   * position as this iterator.
   *
   * @return A clone of this iterator that holds the same node position.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    WalkingIterator clone = (WalkingIterator) super.clone();

    //    clone.m_varStackPos = this.m_varStackPos;
    //    clone.m_varStackContext = this.m_varStackContext;
    if (null != m_firstWalker)
    {
      clone.m_firstWalker = m_firstWalker.cloneDeep(clone, null);
    }

    return clone;
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {

    super.reset();

    if (null != m_firstWalker)
    {
      m_lastUsedWalker = m_firstWalker;

      m_firstWalker.setRoot(m_context);
    }

  }
  
  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {

    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if (null != m_cachedNodes)
    {
      if(m_next < m_cachedNodes.size())
      {
        int next = m_lastFetched = m_currentContextNode 
                                       = m_cachedNodes.elementAt(m_next);
      
        incrementNextPosition();
  
        return next;
      }
      else if(m_foundLast)
      {
        m_lastFetched = DTM.NULL;
        return DTM.NULL;
      }
    }

    // If the variable stack position is not -1, we'll have to 
    // set our position in the variable stack, so our variable access 
    // will be correct.  Iterators that are at the top level of the 
    // expression need to reset the variable stack, while iterators 
    // in predicates do not need to, and should not, since their execution
    // may be much later than top-level iterators.  
    // m_varStackPos is set in setRoot, which is called 
    // from the execute method.
    if (-1 == m_stackFrame)
    {
      if (DTM.NULL == m_firstWalker.getRoot())
      {
        this.setNextPosition(0);
        m_firstWalker.setRoot(m_context);

        m_lastUsedWalker = m_firstWalker;
      }

      return returnNextNode(m_firstWalker.nextNode());
    }
    else
    {
      VariableStack vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      int savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);

      if (DTM.NULL == m_firstWalker.getRoot())
      {
        this.setNextPosition(0);
        m_firstWalker.setRoot(m_context);

        m_lastUsedWalker = m_firstWalker;
      }

      int n = returnNextNode(m_firstWalker.nextNode());

      // These two statements need to be combined into one operation.
      vars.setStackFrame(savedStart);

      return n;
    }
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the head of the walker list.
   *
   * @return The head of the walker list, or null
   * if this iterator does not implement walkers.
   */
  public final AxesWalker getFirstWalker()
  {
    return m_firstWalker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Set the last used walker.
   *
   * @param walker The last used walker, or null.
   */
  public final void setLastUsedWalker(AxesWalker walker)
  {
    m_lastUsedWalker = walker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the last used walker.
   *
   * @return The last used walker, or null.
   */
  public final AxesWalker getLastUsedWalker()
  {
    return m_lastUsedWalker;
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
    m_lastUsedWalker = null;
    
    // Always call the superclass detach last!
    super.detach();
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_predicateIndex = -1;

    AxesWalker walker = m_firstWalker;

    while (null != walker)
    {
      walker.fixupVariables(vars, globalsSize);
      walker = walker.getNextWalker();
    }
  }

  
  /** The last used step walker in the walker list.
   *  @serial */
  protected AxesWalker m_lastUsedWalker;

  /** The head of the step walker list.
   *  @serial */
  protected AxesWalker m_firstWalker;

}
