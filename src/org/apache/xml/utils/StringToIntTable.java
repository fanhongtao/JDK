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
 * A very simple lookup table that stores a list of strings, the even
 * number strings being keys, and the odd number strings being values.
 */
public class StringToIntTable
{

  public static final int INVALID_KEY = -10000;
  
  /** Block size to allocate          */
  private int m_blocksize;

  /** Array of strings this table points to. Associated with ints
   * in m_values         */
  private String m_map[];

  /** Array of ints this table points. Associated with strings from
   * m_map.         */
  private int m_values[];

  /** Number of ints in the table          */
  private int m_firstFree = 0;

  /** Size of this table         */
  private int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public StringToIntTable()
  {

    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
    m_values = new int[m_blocksize];
  }

  /**
   * Construct a StringToIntTable, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public StringToIntTable(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new String[blocksize];
    m_values = new int[m_blocksize];
  }

  /**
   * Get the length of the list.
   *
   * @return the length of the list 
   */
  public final int getLength()
  {
    return m_firstFree;
  }

  /**
   * Append a string onto the vector.
   *
   * @param key String to append
   * @param value The int value of the string
   */
  public final void put(String key, int value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      String newMap[] = new String[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;

      int newValues[] = new int[m_mapSize];

      System.arraycopy(m_values, 0, newValues, 0, m_firstFree + 1);

      m_values = newValues;
    }

    m_map[m_firstFree] = key;
    m_values[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param key String to look for
   *
   * @return The String's int value
   * 
   */
  public final int get(String key)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equals(key))
        return m_values[i];
    }

	return INVALID_KEY;
  }

  /**
   * Tell if the table contains the given string. Ignore case.
   *
   * @param key String to look for
   *
   * @return The string's int value
   */
  public final int getIgnoreCase(String key)
  {

    if (null == key)
        return INVALID_KEY;

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equalsIgnoreCase(key))
        return m_values[i];
    }

    return INVALID_KEY;
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param key String to look for
   *
   * @return True if the string is in the table
   */
  public final boolean contains(String key)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equals(key))
        return true;
    }

    return false;
  }
  
  /**
   * Return array of keys in the table.
   * 
   * @return Array of strings
   */
  public final String[] keys()
  {
    String [] keysArr = new String[m_firstFree];

    for (int i = 0; i < m_firstFree; i++)
    {
      keysArr[i] = m_map[i];
    }

    return keysArr;
  }  
}
