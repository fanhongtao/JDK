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
package org.apache.xml.utils;

/**
 * <meta name="usage" content="internal"/>
 * A very simple table that stores a list of int. Very similar API to our
 * IntVector class (same API); different internal storage.
 * 
 * This version uses an array-of-arrays solution. Read/write access is thus
 * a bit slower than the simple IntVector, and basic storage is a trifle
 * higher due to the top-level array -- but appending is O(1) fast rather
 * than O(N**2) slow, which will swamp those costs in situations where
 * long vectors are being built up.
 * 
 * Known issues:
 * 
 * Some methods are private because they haven't yet been tested properly.
 *
 * Retrieval performance is critical, since this is used at the core
 * of the DTM model. (Append performance is almost as important.)
 * That's pushing me toward just letting reads from unset indices
 * throw exceptions or return stale data; safer behavior would have
 * performance costs.
 * */
public class SuballocatedIntVector
{
  /** Size of blocks to allocate          */
  protected int m_blocksize;

  /** Bitwise addressing (much faster than div/remainder */
  protected int m_SHIFT, m_MASK;
  
  /** Number of blocks to (over)allocate by */
  protected  int m_numblocks=32;
  
  /** Array of arrays of ints          */
  protected int m_map[][];

  /** Number of ints in array          */
  protected int m_firstFree = 0;

  /** "Shortcut" handle to m_map[0]. Surprisingly helpful for short vectors. */
  protected int m_map0[];


  /**
   * Default constructor.  Note that the default
   * block size is currently 2K, which may be overkill for
   * small lists and undershootng for large ones.
   */
  public SuballocatedIntVector()
  {
    this(2048);
  }

  /**
   * Construct a IntVector, using the given block size. For
   * efficiency, we will round the requested size off to a power of
   * two.
   *
   * @param blocksize Size of block to allocate
   * */
  public SuballocatedIntVector(int blocksize)
  {
    //m_blocksize = blocksize;
    for(m_SHIFT=0;0!=(blocksize>>>=1);++m_SHIFT)
      ;
    m_blocksize=1<<m_SHIFT;
    m_MASK=m_blocksize-1;
		
    m_map0=new int[m_blocksize];
    m_map = new int[m_numblocks][];
    m_map[0]=m_map0;
  }
	
  /** We never _did_ use the increasesize parameter, so I'm phasing
   * this constructor out.
   *
   * @deprecated use SuballocatedIntVector(int)
   * @see SuballocatedIntVector(int)
   * */
  public SuballocatedIntVector(int blocksize,int increasesize)
  {
    this(blocksize);
  }

  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public int size()
  {
    return m_firstFree;
  }
  
  /**
   * Set the length of the list. This will only work to truncate the list, and
   * even then it has not been heavily tested and may not be trustworthy.
   *
   * @return length of the list
   */
  public void setSize(int sz)
  {
    if(m_firstFree>sz) // Whups; had that backward!
      m_firstFree = sz;
  }

  /**
   * Append a int onto the vector.
   *
   * @param value Int to add to the list 
   */
  public  void addElement(int value)
  {
    if(m_firstFree<m_blocksize)
      m_map0[m_firstFree++]=value;
    else
    {
      // Growing the outer array should be rare. We initialize to a
      // total of m_blocksize squared elements, which at the default
      // size is 4M integers... and we grow by at least that much each
      // time.  However, attempts to microoptimize for this (assume
      // long enough and catch exceptions) yield no noticable
      // improvement.

      int index=m_firstFree>>>m_SHIFT;
      int offset=m_firstFree&m_MASK;

      if(index>=m_map.length)
      {
	int newsize=index+m_numblocks;
	int[][] newMap=new int[newsize][];
	System.arraycopy(m_map, 0, newMap, 0, m_map.length);
	m_map=newMap;
      }
      int[] block=m_map[index];
      if(null==block)
	block=m_map[index]=new int[m_blocksize];
      block[offset]=value;

      ++m_firstFree;
    }
  }
  
  /**
   * Append several int values onto the vector.
   *
   * @param value Int to add to the list 
   */
  private  void addElements(int value, int numberOfElements)
  {
    if(m_firstFree+numberOfElements<m_blocksize)
      for (int i = 0; i < numberOfElements; i++) 
      {
        m_map0[m_firstFree++]=value;
      }
    else
    {
      int index=m_firstFree>>>m_SHIFT;
      int offset=m_firstFree&m_MASK;
      m_firstFree+=numberOfElements;
      while( numberOfElements>0)
      {
        if(index>=m_map.length)
        {
          int newsize=index+m_numblocks;
          int[][] newMap=new int[newsize][];
          System.arraycopy(m_map, 0, newMap, 0, m_map.length);
          m_map=newMap;
        }
        int[] block=m_map[index];
        if(null==block)
          block=m_map[index]=new int[m_blocksize];
        int copied=(m_blocksize-offset < numberOfElements)
          ? m_blocksize-offset : numberOfElements;
        numberOfElements-=copied;
        while(copied-- > 0)
          block[offset++]=value;

        ++index;offset=0;
      }
    }
  }
  
  /**
   * Append several slots onto the vector, but do not set the values.
   * Note: "Not Set" means the value is unspecified.
   *
   * @param value Int to add to the list 
   */
  private  void addElements(int numberOfElements)
  {
    int newlen=m_firstFree+numberOfElements;
    if(newlen>m_blocksize)
    {
      int index=m_firstFree>>>m_SHIFT;
      int newindex=(m_firstFree+numberOfElements)>>>m_SHIFT;
      for(int i=index+1;i<=newindex;++i)
        m_map[i]=new int[m_blocksize];
    }
    m_firstFree=newlen;
  }
  
  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   *
   * Insertion may be an EXPENSIVE operation!
   *
   * @param value Int to insert
   * @param at Index of where to insert 
   */
  private  void insertElementAt(int value, int at)
  {
    if(at==m_firstFree)
      addElement(value);
    else if (at>m_firstFree)
    {
      int index=at>>>m_SHIFT;
      if(index>=m_map.length)
      {
        int newsize=index+m_numblocks;
        int[][] newMap=new int[newsize][];
        System.arraycopy(m_map, 0, newMap, 0, m_map.length);
        m_map=newMap;
      }
      int[] block=m_map[index];
      if(null==block)
        block=m_map[index]=new int[m_blocksize];
      int offset=at&m_MASK;
          block[offset]=value;
          m_firstFree=offset+1;
        }
    else
    {
      int index=at>>>m_SHIFT;
      int maxindex=m_firstFree>>>m_SHIFT; // %REVIEW% (m_firstFree+1?)
      ++m_firstFree;
      int offset=at&m_MASK;
      int push;
      
      // ***** Easier to work down from top?
      while(index<=maxindex)
      {
        int copylen=m_blocksize-offset-1;
        int[] block=m_map[index];
        if(null==block)
        {
          push=0;
          block=m_map[index]=new int[m_blocksize];
        }
        else
        {
          push=block[m_blocksize-1];
          System.arraycopy(block, offset , block, offset+1, copylen);
        }
        block[offset]=value;
        value=push;
        offset=0;
        ++index;
      }
    }
  }

  /**
   * Wipe it out. Currently defined as equivalent to setSize(0).
   */
  public void removeAllElements()
  {
    m_firstFree = 0;
  }

  /**
   * Removes the first occurrence of the argument from this vector.
   * If the object is found in this vector, each component in the vector
   * with an index greater or equal to the object's index is shifted
   * downward to have an index one smaller than the value it had
   * previously.
   *
   * @param s Int to remove from array
   *
   * @return True if the int was removed, false if it was not found
   */
  private  boolean removeElement(int s)
  {
    int at=indexOf(s,0);
    if(at<0)
      return false;
    removeElementAt(at);
    return true;
  }

  /**
   * Deletes the component at the specified index. Each component in
   * this vector with an index greater or equal to the specified
   * index is shifted downward to have an index one smaller than
   * the value it had previously.
   *
   * @param i index of where to remove and int
   */
  private  void removeElementAt(int at)
  {
        // No point in removing elements that "don't exist"...  
    if(at<m_firstFree)
    {
      int index=at>>>m_SHIFT;
      int maxindex=m_firstFree>>>m_SHIFT;
      int offset=at&m_MASK;
      
      while(index<=maxindex)
      {
        int copylen=m_blocksize-offset-1;
        int[] block=m_map[index];
        if(null==block)
          block=m_map[index]=new int[m_blocksize];
        else
          System.arraycopy(block, offset+1, block, offset, copylen);
        if(index<maxindex)
        {
          int[] next=m_map[index+1];
          if(next!=null)
            block[m_blocksize-1]=(next!=null) ? next[0] : 0;
        }
        else
          block[m_blocksize-1]=0;
        offset=0;
        ++index;
      }
    }
    --m_firstFree;
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
  public void setElementAt(int value, int at)
  {
    if(at<m_blocksize)
      m_map0[at]=value;
    else
    {
      int index=at>>>m_SHIFT;
      int offset=at&m_MASK;
        
      if(index>=m_map.length)
      {
	int newsize=index+m_numblocks;
	int[][] newMap=new int[newsize][];
	System.arraycopy(m_map, 0, newMap, 0, m_map.length);
	m_map=newMap;
      }

      int[] block=m_map[index];
      if(null==block)
	block=m_map[index]=new int[m_blocksize];
      block[offset]=value;
    }

    if(at>=m_firstFree)
      m_firstFree=at+1;
  }
  

  /**
   * Get the nth element. This is often at the innermost loop of an
   * application, so performance is critical.
   *
   * @param i index of value to get
   *
   * @return value at given index. If that value wasn't previously set,
   * the result is undefined for performance reasons. It may throw an
   * exception (see below), may return zero, or (if setSize has previously
   * been used) may return stale data.
   *
   * @throw ArrayIndexOutOfBoundsException if the index was _clearly_
   * unreasonable (negative, or past the highest block).
   *
   * @throw NullPointerException if the index points to a block that could
   * have existed (based on the highest index used) but has never had anything
   * set into it.
   * %REVIEW% Could add a catch to create the block in that case, or return 0.
   * Try/Catch is _supposed_ to be nearly free when not thrown to. Do we
   * believe that? Should we have a separate safeElementAt?
   */
  public int elementAt(int i)
  {
    // This is actually a significant optimization!
    if(i<m_blocksize)
      return m_map0[i];

    return m_map[i>>>m_SHIFT][i&m_MASK];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s object to look for
   *
   * @return true if the object is in the list
   */
  private  boolean contains(int s)
  {
    return (indexOf(s,0) >= 0);
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
  public int indexOf(int elem, int index)
  {
        if(index>=m_firstFree)
                return -1;
          
    int bindex=index>>>m_SHIFT;
    int boffset=index&m_MASK;
    int maxindex=m_firstFree>>>m_SHIFT;
    int[] block;
    
    for(;bindex<maxindex;++bindex)
    {
      block=m_map[bindex];
      if(block!=null)
        for(int offset=boffset;offset<m_blocksize;++offset)
          if(block[offset]==elem)
            return offset+bindex*m_blocksize;
      boffset=0; // after first
    }
    // Last block may need to stop before end
    int maxoffset=m_firstFree&m_MASK;
    block=m_map[maxindex];
    for(int offset=boffset;offset<maxoffset;++offset)
      if(block[offset]==elem)
        return offset+maxindex*m_blocksize;

    return -1;    
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
  public int indexOf(int elem)
  {
    return indexOf(elem,0);
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
  private  int lastIndexOf(int elem)
  {
    int boffset=m_firstFree&m_MASK;
    for(int index=m_firstFree>>>m_SHIFT;
        index>=0;
        --index)
    {
      int[] block=m_map[index];
      if(block!=null)
        for(int offset=boffset; offset>=0; --offset)
          if(block[offset]==elem)
            return offset+index*m_blocksize;
      boffset=0; // after first
    }
    return -1;
  }

}
