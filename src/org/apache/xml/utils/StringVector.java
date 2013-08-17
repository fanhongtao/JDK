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
 * A very simple table that stores a list of strings, optimized
 * for small lists.
 */
public class StringVector implements java.io.Serializable
{

  /** @serial Size of blocks to allocate           */
  protected int m_blocksize;

  /** @serial Array of strings this contains          */
  protected String m_map[];

  /** @serial Number of strings this contains          */
  protected int m_firstFree = 0;

  /** @serial Size of the array          */
  protected int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public StringVector()
  {

    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
  }

  /**
   * Construct a StringVector, using the given block size.
   *
   * @param blocksize Size of the blocks to allocate 
   */
  public StringVector(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new String[blocksize];
  }

  /**
   * Get the length of the list.
   *
   * @return Number of strings in the list 
   */
  public int getLength()
  {
    return m_firstFree;
  }

  /**
   * Get the length of the list.
   *
   * @return Number of strings in the list
   */
  public final int size()
  {
    return m_firstFree;
  }

  /**
   * Append a string onto the vector.
   *
   * @param value Sting to add to the vector
   */
  public final void addElement(String value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      String newMap[] = new String[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Get the nth element.
   *
   * @param i Index of string to find
   *
   * @return String at given index
   */
  public final String elementAt(int i)
  {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param s String to look for
   *
   * @return True if the string is in this table  
   */
  public final boolean contains(String s)
  {

    if (null == s)
      return false;

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equals(s))
        return true;
    }

    return false;
  }

  /**
   * Tell if the table contains the given string. Ignore case.
   *
   * @param s String to find
   *
   * @return True if the String is in this vector
   */
  public final boolean containsIgnoreCase(String s)
  {

    if (null == s)
      return false;

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equalsIgnoreCase(s))
        return true;
    }

    return false;
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param s String to push into the vector
   */
  public final void push(String s)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      String newMap[] = new String[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = s;

    m_firstFree++;
  }

  /**
   * Pop the tail of this vector.
   *
   * @return The String last added to this vector or null not found.
   * The string is removed from the vector.
   */
  public final String pop()
  {

    if (m_firstFree <= 0)
      return null;

    m_firstFree--;

    String s = m_map[m_firstFree];

    m_map[m_firstFree] = null;

    return s;
  }

  /**
   * Get the string at the tail of this vector without popping.
   *
   * @return The string at the tail of this vector.
   */
  public final String peek()
  {
    return (m_firstFree <= 0) ? null : m_map[m_firstFree - 1];
  }
}
