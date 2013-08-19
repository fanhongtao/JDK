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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.lang.reflect.Method;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * For internal connectiones, i.e. Connection information supplies in the
 * Stylesheet. The Default Connection Pool will be used.
 */
public class DefaultConnectionPool implements ConnectionPool
{
  /**
   * A placeholder thast will keep the driver loaded
   * between calls.
   */
  private Object m_Driver = null;
  /**
   */
  private static final boolean DEBUG = false;

  /**
   * The basic information to make a JDBC Connection
   */
  private String m_driver = new String("");
  /**
   */
  private String m_url = new String("");


  /**
   * The mimimum size of the connection pool, if the
   * number of available connections falls below this
   * mark, min connections will be allocated. The Connection
   * Pool will always be somewhere between MinSize and MinSize*2
   */
  private int m_PoolMinSize = 1;


  /**
   * Always implement the properties mechinism, if the Password
   * or Username is set seperatly then we will add them to the
   * property manually.
   */
  private Properties m_ConnectionProtocol = new Properties();

  /**
   * Storage for the PooledConnections
   */
  private Vector m_pool = new Vector();

  /**
   * Are we active ??
   */
  private boolean m_IsActive = false;

  /**
   */
  public DefaultConnectionPool( ) {}


  /**
   * Return our current Active state
   * @return
   */
  public boolean isEnabled( )
  {
    return m_IsActive;
  }

  /**
   * Set the driver call to be used to create connections
   * @param d
   * @return
   */
  public void setDriver( String d )
  {
    m_driver = d;
  }

  /**
   * Set the url used to connect to the database
   * @param url
   * @return
   */
  public void setURL( String url )
  {
    m_url = url;
  }

  /**
   * Go through the connection pool and release any connections
   * that are not InUse;
   * @return
   */
  public void freeUnused( )
  {
    // Iterate over the entire pool closing the
    // JDBC Connections.
    for ( int x = 0; x < m_pool.size(); x++ )
    {


      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      // If the PooledConnection is not in use, close it
      if ( pcon.inUse() == false )
      {
        if (DEBUG)
        {
          System.err.println("Closing JDBC Connection " + x);
        }

        pcon.close();
      }
    }

  }

  /**
   * Is our ConnectionPool have any connections that are still in Use ??
   * @return
   */
  public boolean hasActiveConnections( )
  {
    return (m_pool.size() > 0);
  }


  /**
   * Set the password in the property set.
   * @param p
   * @return
   */
  public void setPassword( String p )
  {
    m_ConnectionProtocol.put("password", p);
  }

  /**
   * Set the user name in the property set
   * @param u
   * @return
   */
  public void setUser( String u )
  {
    m_ConnectionProtocol.put("user", u);
  }

  /**
   * The Protocol string is used to pass in other connection
   * properties. A properties file is a general purpose container
   *
   * @param p
   * @return
   */
  public void setProtocol( Properties p )
  {
    Enumeration e = p.keys();
    while (e.hasMoreElements())
    {
      String key = (String) e.nextElement();
      m_ConnectionProtocol.put(key, p.getProperty(key));
    }
  }


  /**
   * Override the current number of connections to keep in the pool. This
   * setting will only have effect on a new pool or when a new connection
   * is requested and there is less connections that this setting.
   * @param n
   * @return
   */
  public void setMinConnections( int n )
  {
    m_PoolMinSize = n;
  }

  /**
   * Try to aquire a new connection, if it succeeds then return
   * true, else return false.
   * Note: This method will cause the connection pool to be built.
   * @return
   */
  public boolean testConnection( )
  {
    try
    {
      if (DEBUG)
      {
        System.out.println("Testing Connection");
      }

      Connection conn = getConnection();

      if (DEBUG)
      {
        DatabaseMetaData dma = conn.getMetaData();

        System.out.println("\nConnected to " + dma.getURL());
        System.out.println("Driver   " + dma.getDriverName());
        System.out.println("Version  " + dma.getDriverVersion());
        System.out.println("");
      }

      if (conn == null) return false;

      releaseConnection(conn);

      if (DEBUG)
      {
        System.out.println("Testing Connection, SUCCESS");
      }

      return true;
    }
    catch(Exception e)
    {
      if (DEBUG)
      {
        System.out.println("Testing Connection, FAILED");
        e.printStackTrace();
      }

      return false;
    }

  }


  // Find an available connection
  /**
   * @return Connection
   * @throws SQLException
   * @throws IllegalArgumentException
   */
  public synchronized Connection getConnection( )throws IllegalArgumentException, SQLException
  {

    PooledConnection pcon = null;

    // We will fill up the pool any time it is less than the
    // Minimum. THis could be cause by the enableing and disabling
    // or the pool.
    //
    if ( m_pool.size() < m_PoolMinSize ) { initializePool(); }

    // find a connection not in use
    for ( int x = 0; x < m_pool.size(); x++ )
    {

      pcon = (PooledConnection) m_pool.elementAt(x);

      // Check to see if the Connection is in use
      if ( pcon.inUse() == false )
      {
        // Mark it as in use
        pcon.setInUse(true);
        // return the JDBC Connection stored in the
        // PooledConnection object
        return pcon.getConnection();
      }
    }

    // Could not find a free connection,
    // create and add a new one

    // Create a new JDBC Connection
    Connection con = createConnection();

    // Create a new PooledConnection, passing it the JDBC
    // Connection
    pcon = new PooledConnection(con);

    // Mark the connection as in use
    pcon.setInUse(true);

    // Add the new PooledConnection object to the pool
    m_pool.addElement(pcon);

    // return the new Connection
    return pcon.getConnection();
  }

  /**
   * @param con
   * @return
   * @throws SQLException
   */
  public synchronized void releaseConnection( Connection con )throws SQLException
  {

    // find the PooledConnection Object
    for ( int x = 0; x < m_pool.size(); x++ )
    {

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      // Check for correct Connection
      if ( pcon.getConnection() == con )
      {
        if (DEBUG)
        {
          System.out.println("Releasing Connection " + x);
        }

        if (! isEnabled())
        {
          con.close();
          m_pool.removeElementAt(x);
          if (DEBUG)
          {
            System.out.println("-->Inactive Pool, Closing connection");
          }

        }
        else
        {
          // Set it's inuse attribute to false, which
          // releases it for use
          pcon.setInUse(false);
        }

        break;
      }
    }
  }


  /**
   * @param con
   * @return
   * @throws SQLException
   */
  public synchronized void releaseConnectionOnError( Connection con )throws SQLException
  {

    // find the PooledConnection Object
    for ( int x = 0; x < m_pool.size(); x++ )
    {

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      // Check for correct Connection
      if ( pcon.getConnection() == con )
      {
        if (DEBUG)
        {
          System.out.println("Releasing Connection On Error" + x);
        }

        con.close();
        m_pool.removeElementAt(x);
        if (DEBUG)
        {
          System.out.println("-->Inactive Pool, Closing connection");
        }
        break;
      }
    }
  }


  /**
   * @return
   * @throws SQLException
   */
  private Connection createConnection( )throws SQLException
  {
    Connection con = null;

    // Create a Connection
    con = DriverManager.getConnection( m_url, m_ConnectionProtocol );

    return con;
  }

  // Initialize the pool
  /**
   * @return
   * @throws IllegalArgumentException
   * @throws SQLException
   */
  public synchronized void initializePool( )throws IllegalArgumentException, SQLException
  {

     // Check our initial values
     if ( m_driver == null )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DRIVER_NAME_SPECIFIED, null));
       // "No Driver Name Specified!");
     }

     if ( m_url == null )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_URL_SPECIFIED, null));
       // "No URL Specified!");
     }

     if ( m_PoolMinSize < 1 )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_POOLSIZE_LESS_THAN_ONE, null));
       // "Pool size is less than 1!");
     }

     // Create the Connections
     // Load the Driver class file

     try
     {
        // We need to implement the context classloader
        Class cls = null;
        try 
        {
          Method m = Thread.class.getMethod("getContextClassLoader", null);
          ClassLoader classLoader = (ClassLoader) m.invoke(Thread.currentThread(), null);
          cls = classLoader.loadClass(m_driver);
        } 
        catch (Exception e) 
        {
          cls = Class.forName(m_driver);  
        }
        
        if (cls == null)
          cls = Class.forName(m_driver);

        // We have also had problems with drivers unloading
        // load an instance that will get freed with the class.
        m_Driver = cls.newInstance();


     }
     catch(ClassNotFoundException e)
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_DRIVER_NAME, null));
       // "Invalid Driver Name Specified!");
     }
     catch(Exception e)
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_DRIVER_NAME, null));
     }

     // IF we are not active, don't actuall build a pool yet
     // Just set up the driver and periphal items.
     if ( !m_IsActive) return;

    // Create Connections based on the size member
    do
    {

      Connection con = createConnection();

      if ( con != null )
      {

        // Create a PooledConnection to encapsulate the
        // real JDBC Connection
        PooledConnection pcon = new PooledConnection(con);

        // Add the Connection the pool.
        addConnection(pcon);

        if (DEBUG) System.out.println("Adding DB Connection to the Pool");
      }
    }
    while (m_pool.size() < m_PoolMinSize);
  }

  // Adds the PooledConnection to the pool
  /**
   * @param value
   * @return
   */
  private void addConnection( PooledConnection value )
  {
    // Add the PooledConnection Object to the vector
    m_pool.addElement(value);
  }


  /**
   * @return
   * @throws Throwable
   */
  protected void finalize( )throws Throwable
  {
    if (DEBUG)
    {
      System.out.println("In Default Connection Pool, Finalize");
    }

    // Iterate over the entire pool closing the
    // JDBC Connections.
    for ( int x = 0; x < m_pool.size(); x++ )
    {

      if (DEBUG)
      {
        System.out.println("Closing JDBC Connection " + x);
      }

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      // If the PooledConnection is not in use, close it
      if ( pcon.inUse() == false ) { pcon.close();  }
      else
      {
        if (DEBUG)
        {
          System.out.println("--> Force close");
        }

        // If it still in use, sleep for 30 seconds and
        // force close.
        try
        {
          java.lang.Thread.sleep(30000);
          pcon.close();
        }
        catch (InterruptedException ie)
        {
          if (DEBUG) System.err.println(ie.getMessage());
        }
      }
    }

    if (DEBUG)
    {
      System.out.println("Exit Default Connection Pool, Finalize");
    }

    super.finalize();
  }

  /**
   * The Pool can be Enabled and Disabled. Disabling the pool
   * closes all the outstanding Unused connections and any new
   * connections will be closed upon release.
   * @param flag Control the Connection Pool. If it is enabled then Connections will actuall be held
   * around. If disabled then all unused connections will be instantly closed and as
   * connections are released they are closed and removed from the pool.
   * @return
   */
  public void setPoolEnabled( final boolean flag )
  {

  }


}
