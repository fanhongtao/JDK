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

import java.util.EmptyStackException;

/**
 * <meta name="usage" content="internal"/>
 * Simple stack for boolean values.
 */
public final class BoolStack implements Cloneable
{

  /** Array of boolean values          */
  private boolean m_values[];

  /** Array size allocated           */
  private int m_allocatedSize;

  /** Index into the array of booleans          */
  private int m_index;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public BoolStack()
  {
    this(32);
  }

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param size array size to allocate
   */
  public BoolStack(int size)
  {

    m_allocatedSize = size;
    m_values = new boolean[size];
    m_index = -1;
  }

  /**
   * Get the length of the list.
   *
   * @return Current length of the list
   */
  public final int size()
  {
    return m_index + 1;
  }

  /**
   * Pushes an item onto the top of this stack.
   *
   *
   * @param val the boolean to be pushed onto this stack.
   * @return  the <code>item</code> argument.
   */
  public final boolean push(boolean val)
  {

    if (m_index == m_allocatedSize - 1)
      grow();

    return (m_values[++m_index] = val);
  }

  /**
   * Removes the object at the top of this stack and returns that
   * object as the value of this function.
   *
   * @return     The object at the top of this stack.
   * @throws  EmptyStackException  if this stack is empty.
   */
  public final boolean pop()
  {
    return m_values[m_index--];
  }

  /**
   * Removes the object at the top of this stack and returns the
   * next object at the top as the value of this function.
   *
   *
   * @return Next object to the top or false if none there
   */
  public final boolean popAndTop()
  {

    m_index--;

    return (m_index >= 0) ? m_values[m_index] : false;
  }

  /**
   * Set the item at the top of this stack  
   *
   *
   * @param b Object to set at the top of this stack
   */
  public final void setTop(boolean b)
  {
    m_values[m_index] = b;
  }

  /**
   * Looks at the object at the top of this stack without removing it
   * from the stack.
   *
   * @return     the object at the top of this stack.
   * @throws  EmptyStackException  if this stack is empty.
   */
  public final boolean peek()
  {
    return m_values[m_index];
  }

  /**
   * Looks at the object at the top of this stack without removing it
   * from the stack.  If the stack is empty, it returns false.
   *
   * @return     the object at the top of this stack.
   */
  public final boolean peekOrFalse()
  {
    return (m_index > -1) ? m_values[m_index] : false;
  }

  /**
   * Looks at the object at the top of this stack without removing it
   * from the stack.  If the stack is empty, it returns true.
   *
   * @return     the object at the top of this stack.
   */
  public final boolean peekOrTrue()
  {
    return (m_index > -1) ? m_values[m_index] : true;
  }

  /**
   * Tests if this stack is empty.
   *
   * @return  <code>true</code> if this stack is empty;
   *          <code>false</code> otherwise.
   */
  public boolean isEmpty()
  {
    return (m_index == -1);
  }

  /**
   * Grows the size of the stack
   *
   */
  private void grow()
  {

    m_allocatedSize *= 2;

    boolean newVector[] = new boolean[m_allocatedSize];

    System.arraycopy(m_values, 0, newVector, 0, m_index + 1);

    m_values = newVector;
  }
  
  public Object clone() 
    throws CloneNotSupportedException
  {
    return super.clone();
  }

}
