package org.apache.xpath.axes;

import java.util.*;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;

/**
 * <meta name="usage" content="internal"/>
 * Pool of object of a given type to pick from to help memory usage
 */
public class IteratorPool implements java.io.Serializable
{

  /** Type of objects in this pool.
   *  @serial          */
  private final DTMIterator m_orig;

  /** Vector of given objects this points to.
   *  @serial          */
  private final Vector m_freeStack;

  /**
   * Constructor IteratorPool
   *
   * @param original The original iterator from which all others will be cloned.
   */
  public IteratorPool(DTMIterator original)
  {
    m_orig = original;
    m_freeStack = new Vector();
  }
  
  /**
   * Get an instance of the given object in this pool 
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstanceOrThrow()
    throws CloneNotSupportedException
  {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty())
    {

      // Create a new object if so.
      return (DTMIterator)m_orig.clone();
    }
    else
    {
      // Remove object from end of free pool.
      DTMIterator result = (DTMIterator)m_freeStack.lastElement();

      m_freeStack.setSize(m_freeStack.size() - 1);

      return result;
    }
  }
  
  /**
   * Get an instance of the given object in this pool 
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstance()
  {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty())
    {

      // Create a new object if so.
      try
      {
        return (DTMIterator)m_orig.clone();
      }
      catch (Exception ex)
      {
        throw new WrappedRuntimeException(ex);
      }
    }
    else
    {
      // Remove object from end of free pool.
      DTMIterator result = (DTMIterator)m_freeStack.lastElement();

      m_freeStack.setSize(m_freeStack.size() - 1);

      return result;
    }
  }

  /**
   * Add an instance of the given object to the pool  
   *
   *
   * @param obj Object to add.
   */
  public synchronized void freeInstance(DTMIterator obj)
  {
    m_freeStack.addElement(obj);
  }
}