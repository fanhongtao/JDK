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


 package org.apache.xalan.lib.sql;

import java.util.Hashtable;
import java.lang.IllegalArgumentException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 */
public class ConnectionPoolManager
{
  /**
   */
  static Hashtable m_poolTable = null;
  /**
   */
  static boolean m_isInit = false;

  /**
   */
  public ConnectionPoolManager( )
  {
    init();
  }

  /**
   * Initialize the internal structures of the Pool Manager
   * @return
   */
  public synchronized void init( )
  {
    // Only do this process once
    if (m_isInit == true) return;


    //
    // Initialize the pool table
    //
    m_poolTable = new Hashtable();

    m_isInit = true;
  }

  /**
   * Register a nuew connection pool to the global pool table.
   * If a pool by that name currently exists, then throw an
   * IllegalArgumentException stating that the pool already
   * exist.
   * @param name
   * @param pool
   * @return
   * @link org.apache.xalan.lib.sql.ConnectionPool}
   * @return
   * @throws <code>IllegalArgumentException</code>, throw this exception
   * if a pool with the same name currently exists.
   */
  public synchronized void registerPool( String name, ConnectionPool pool )
  {
    if ( m_poolTable.containsKey(name) )
    {
      throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_POOL_EXISTS, null)); //"Pool already exists");
    }

    m_poolTable.put(name, pool);
  }

  /**
   * Remove a pool from the global table. If the pool still has
   * active connections, then only mark this pool as inactive and
   * leave it around until all the existing connections are closed.
   * @param name
   * @return
   */
  public synchronized void removePool( String name )
  {
    ConnectionPool pool = getPool(name);

    if (null != pool)
    {
      //
      // Disable future use of this pool under the Xalan
      // extension only. This flag should only exist in the
      // wrapper and not in the actual pool implementation.
      pool.setPoolEnabled(false);


      //
      // Remove the pool from the Hashtable if we don'd have
      // any active connections.
      //
      if ( ! pool.hasActiveConnections() ) m_poolTable.remove(name);
    }

  }


  /**
   * Return the connection pool referenced by the name
   * @param name
   * @return
   * @returns <code>ConnectionPool</code> a reference to the ConnectionPool
   * object stored in the Pool Table. If the named pool does not exist, return
   * null
   */
  public synchronized ConnectionPool getPool( String name )
  {
    return (ConnectionPool) m_poolTable.get(name);
  }

}