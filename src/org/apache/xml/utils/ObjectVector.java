/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
package org.apache.xml.utils;

/**
 * <meta name="usage" content="internal"/>
 * A very simple table that stores a list of objects.
 *
 * This version is based on a "realloc" strategy -- a simle array is
 * used, and when more storage is needed, a larger array is obtained
 * and all existing data is recopied into it. As a result, read/write
 * access to existing nodes is O(1) fast but appending may be O(N**2)
 * slow. 
 */
public class ObjectVector implements Cloneable
{

  /** Size of blocks to allocate          */
  protected int m_blocksize;

  /** Array of objects          */
  protected Object m_map[]; 

  /** Number of ints in array          */
  protected int m_firstFree = 0;

  /** Size of array          */
  protected int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public ObjectVector()
  {

    m_blocksize = 32;
    m_mapSize = m_blocksize;
    m_map = new Object[m_blocksize];
  }

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public ObjectVector(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new Object[blocksize];
  }
  
  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public ObjectVector(int blocksize, int increaseSize)
  {

    m_blocksize = increaseSize;
    m_mapSize = blocksize;
    m_map = new Object[blocksize];
  }

  /**
   * Copy constructor for ObjectVector
   * 
   * @param v Existing ObjectVector to copy
   */
  public ObjectVector(ObjectVector v)
  {
  	m_map = new Object[v.m_mapSize];
    m_mapSize = v.m_mapSize;
    m_firstFree = v.m_firstFree;
  	m_blocksize = v.m_blocksize;
  	System.arraycopy(v.m_map, 0, m_map, 0, m_firstFree);
  }

  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public final int size()
  {
    return m_firstFree;
  }
  
  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public final void setSize(int sz)
  {
    m_firstFree = sz;
  }


  /**
   * Append an object onto the vector.
   *
   * @param value Object to add to the list 
   */
  public final void addElement(Object value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      Object newMap[] = new Object[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }
  
  /**
   * Append several Object values onto the vector.
   *
   * @param value Object to add to the list 
   */
  public final void addElements(Object value, int numberOfElements)
  {

    if ((m_firstFree + numberOfElements) >= m_mapSize)
    {
      m_mapSize += (m_blocksize+numberOfElements);

      Object newMap[] = new Object[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    for (int i = 0; i < numberOfElements; i++) 
    {
      m_map[m_firstFree] = value;
      m_firstFree++;
    }
  }
  
  /**
   * Append several slots onto the vector, but do not set the values.
   *
   * @param numberOfElements number of slots to append
   */
  public final void addElements(int numberOfElements)
  {

    if ((m_firstFree + numberOfElements) >= m_mapSize)
    {
      m_mapSize += (m_blocksize+numberOfElements);

      Object newMap[] = new Object[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }
    
    m_firstFree += numberOfElements;
  }
  

  /**
   * Inserts the specified object in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   *
   * @param value Object to insert
   * @param at Index of where to insert 
   */
  public final void insertElementAt(Object value, int at)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      Object newMap[] = new Object[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    if (at <= (m_firstFree - 1))
    {
      System.arraycopy(m_map, at, m_map, at + 1, m_firstFree - at);
    }

    m_map[at] = value;

    m_firstFree++;
  }

  /**
   * Remove all elements objects from the list. 
   */
  public final void removeAllElements()
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      m_map[i] = null;
    }

    m_firstFree = 0;
  }

  /**
   * Removes the first occurrence of the argument from this vector.
   * If the object is found in this vector, each component in the vector
   * with an index greater or equal to the object's index is shifted
   * downward to have an index one smaller than the value it had
   * previously.
   *
   * @param s Object to remove from array
   *
   * @return True if the object was removed, false if it was not found
   */
  public final boolean removeElement(Object s)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == s)
      {
        if ((i + 1) < m_firstFree)
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        else
          m_map[i] = null;

        m_firstFree--;

        return true;
      }
    }

    return false;
  }

  /**
   * Deletes the component at the specified index. Each component in
   * this vector with an index greater or equal to the specified
   * index is shifted downward to have an index one smaller than
   * the value it had previously.
   *
   * @param i index of where to remove an object
   */
  public final void removeElementAt(int i)
  {

    if (i > m_firstFree)
      System.arraycopy(m_map, i + 1, m_map, i, m_firstFree);
    else
      m_map[i] = null;

    m_firstFree--;
  }

  /**
   * Sets the component at the specified index of this vector to be the
   * specified object. The previous component at that position is discarded.
   *
   * The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.
   *
   * @param value object to set
   * @param index Index of where to set the object
   */
  public final void setElementAt(Object value, int index)
  {
    m_map[index] = value;
  }

  /**
   * Get the nth element.
   *
   * @param i index of object to get
   *
   * @return object at given index
   */
  public final Object elementAt(int i)
  {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given Object.
   *
   * @param s object to look for
   *
   * @return true if the object is in the list
   */
  public final boolean contains(Object s)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == s)
        return true;
    }

    return false;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem object to look for
   * @param index Index of where to begin search
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int indexOf(Object elem, int index)
  {

    for (int i = index; i < m_firstFree; i++)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem object to look for
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int indexOf(Object elem)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem Object to look for
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int lastIndexOf(Object elem)
  {

    for (int i = (m_firstFree - 1); i >= 0; i--)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }
  
  /*
   * Reset the array to the supplied size.  
   * 
   * @param size 
   */
  public final void setToSize(int size) {
    
    Object newMap[] = new Object[size];
    
    System.arraycopy(m_map, 0, newMap, 0, m_firstFree);
    m_mapSize = size;

    m_map = newMap;
    
  }  
  
  /**
   * Returns clone of current ObjectVector
   * 
   * @return clone of current ObjectVector
   */
  public Object clone()
    throws CloneNotSupportedException
  {
  	return new ObjectVector(this);
  }
}
