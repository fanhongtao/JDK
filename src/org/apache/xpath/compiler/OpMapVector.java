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

package org.apache.xpath.compiler;

/**
 * <meta name="usage" content="internal"/>
 * 
 * Like IntVector, but used only for the OpMap array.  Length of array
 * is kept in the m_lengthPos position of the array.  Only the required methods 
 * are in included here.
 */
public class OpMapVector {
    
 /** Size of blocks to allocate          */
  protected int m_blocksize;

  /** Array of ints          */
  protected int m_map[]; // IntStack is trying to see this directly

  /** Position where size of array is kept          */
  protected int m_lengthPos = 0;

  /** Size of array          */
  protected int m_mapSize;
  
    /**
   * Construct a OpMapVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public OpMapVector(int blocksize, int increaseSize, int lengthPos)
  {

    m_blocksize = increaseSize;
    m_mapSize = blocksize;
    m_lengthPos = lengthPos;
    m_map = new int[blocksize];
  }
  
  /**
   * Get the nth element.
   *
   * @param i index of object to get
   *
   * @return object at given index
   */
  public final int elementAt(int i)
  {
    return m_map[i];
  }  
   
    /**
   * Sets the component at the specified index of this vector to be the
   * specified object. The previous component at that position is discarded.
   *
   * The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.
   *
   * @param node object to set
   * @param index Index of where to set the object
   */
  public final void setElementAt(int value, int index)
  {
    if (index >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_map[m_lengthPos]);

      m_map = newMap;
    }

    m_map[index] = value;
    
  }
  
  
  /*
   * Reset the array to the supplied size.  No checking is done.
   * 
   * @param size The size to trim to.
   */
  public final void setToSize(int size) {
    
    int newMap[] = new int[size];

    System.arraycopy(m_map, 0, newMap, 0, m_map[m_lengthPos]);

    m_mapSize = size;
    m_map = newMap;
    
  }  

}
