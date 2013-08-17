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
public class StringToStringTable
{

  /** Size of blocks to allocate          */
  private int m_blocksize;

  /** Array of strings this contains          */
  private String m_map[];

  /** Number of strings this contains           */
  private int m_firstFree = 0;

  /** Size of this table           */
  private int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public StringToStringTable()
  {

    m_blocksize = 16;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
  }

  /**
   * Construct a StringToStringTable, using the given block size.
   *
   * @param blocksize Size of blocks to allocate 
   */
  public StringToStringTable(int blocksize)
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
  public final int getLength()
  {
    return m_firstFree;
  }

  /**
   * Append a string onto the vector.
   * The strings go to the even locations in the array 
   * and the values in the odd. 
   *
   * @param key String to add to the list 
   * @param value Value of the string
   */
  public final void put(String key, String value)
  {

    if ((m_firstFree + 2) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      String newMap[] = new String[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = key;

    m_firstFree++;

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param key String to look up
   *
   * @return return the value of the string or null if not found. 
   */
  public final String get(String key)
  {

    for (int i = 0; i < m_firstFree; i += 2)
    {
      if (m_map[i].equals(key))
        return m_map[i + 1];
    }

    return null;
  }

  /**
   * Remove the given string and its value from this table.
   *
   * @param key String to remove from the table
   */
  public final void remove(String key)
  {

    for (int i = 0; i < m_firstFree; i += 2)
    {
      if (m_map[i].equals(key))
      {
        if ((i + 2) < m_firstFree)
          System.arraycopy(m_map, i + 2, m_map, i, m_firstFree - (i + 2));

        m_firstFree -= 2;
        m_map[m_firstFree] = null;
        m_map[m_firstFree + 1] = null;

        break;
      }
    }
  }

  /**
   * Tell if the table contains the given string. Ignore case
   *
   * @param key String to look up
   *
   * @return The value of the string or null if not found
   */
  public final String getIgnoreCase(String key)
  {

    if (null == key)
      return null;

    for (int i = 0; i < m_firstFree; i += 2)
    {
      if (m_map[i].equalsIgnoreCase(key))
        return m_map[i + 1];
    }

    return null;
  }

  /**
   * Tell if the table contains the given string in the value.
   *
   * @param val Value of the string to look up
   *
   * @return the string associated with the given value or null if not found
   */
  public final String getByValue(String val)
  {

    for (int i = 1; i < m_firstFree; i += 2)
    {
      if (m_map[i].equals(val))
        return m_map[i - 1];
    }

    return null;
  }

  /**
   * Get the nth element.
   *
   * @param i index of the string to look up.
   *
   * @return The string at the given index.
   */
  public final String elementAt(int i)
  {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param key String to look up
   *
   * @return True if the given string is in this table 
   */
  public final boolean contains(String key)
  {

    for (int i = 0; i < m_firstFree; i += 2)
    {
      if (m_map[i].equals(key))
        return true;
    }

    return false;
  }

  /**
   * Tell if the table contains the given string.
   *
   * @param val value to look up
   *
   * @return True if the given value is in the table.
   */
  public final boolean containsValue(String val)
  {

    for (int i = 1; i < m_firstFree; i += 2)
    {
      if (m_map[i].equals(val))
        return true;
    }

    return false;
  }
}
