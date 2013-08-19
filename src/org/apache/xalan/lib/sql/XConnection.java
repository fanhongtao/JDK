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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Properties;
import java.util.Vector;
import java.util.StringTokenizer;
import java.lang.IllegalArgumentException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Vector;
import java.util.Enumeration;
import java.math.BigDecimal;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xpath.XPathContext;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xpath.objects.XBooleanStatic;

import org.w3c.dom.*;
import java.sql.*;
import java.util.*;

/**
 * An XSLT extension that allows a stylesheet to
 * access JDBC data. 
 *
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:sql="http://xml.apache.org/xalan/sql"
 * </pre>
 *
 * From the stylesheet perspective,
 * XConnection provides 3 extension functions: new(),
 * query(), and close().
 * Use new() to call one of XConnection constructors, which
 * establishes a JDBC driver connection to a data source and
 * returns an XConnection object.
 * Then use the XConnection object query() method to return a
 * result set in the form of a row-set element.
 * When you have finished working with the row-set, call the
 * XConnection object close() method to terminate the connection.
 */
public class XConnection
{

  /**
   * Flag for DEBUG mode
   */
  private static final boolean DEBUG = false;

  /**
   * The Current Connection Pool in Use. An XConnection can only
   * represent one query at a time, prior to doing some type of query.
   */
  private ConnectionPool m_ConnectionPool = null;

  /**
   * If a default Connection Pool is used. i.e. A connection Pool
   * that is created internally, then do we actually allow pools
   * to be created. Due to the archititure of the Xalan Extensions,
   * there is no notification of when the Extension is being unloaded and
   * as such, there is a good chance that JDBC COnnections are not closed.
   * A finalized is provided to try and catch this situation but since
   * support of finalizers is inconsistant across JVM's this may cause
   * a problem. The robustness of the JDBC Driver is also at issue here.
   * if a controlled shutdown is provided by the driver then default
   * conntectiom pools are OK.
   */
  private boolean m_DefaultPoolingEnabled = false;


  /**
   * As we do queries, we will produce SQL Documents. Any ony may produce
   * one or more SQL Documents so that the current connection information
   * may be easilly reused. This collection will hold a collection of all
   * the documents created. As Documents are closed, they will be removed
   * from the collection and told to free all the used resources.
   */
  private Vector m_OpenSQLDocuments = new Vector();


  /**
   * Let's keep a copy of the ConnectionPoolMgr in
   * alive here so we are keeping the static pool alive
   * We will also use this Pool Manager to register our default pools.
   */
  private ConnectionPoolManager m_PoolMgr = new ConnectionPoolManager();

  /**
   * For PreparedStatements, we need a place to
   * to store the parameters in a vector.
   */
  private Vector m_ParameterList = new Vector();

  /**
   * Allow the SQL Extensions to return null on error. The Error information will
   * be stored in a seperate Error Document that can easily be retrived using the
   * getError() method.
   * %REVIEW% This functionality will probably be buried inside the SQLDocument.
   */
  private SQLErrorDocument m_Error = null;

  /**
   */
  private boolean m_IsDefaultPool = false;

  /**
   * This flag will be used to indicate to the SQLDocument to use
   * Streaming mode. Streeaming Mode will reduce the memory footprint
   * to a fixed amount but will not let you traverse the tree more than
   * once since the Row data will be reused for every Row in the Query.
   */
  private boolean m_IsStreamingEnabled = true;

  /**
   */
  public XConnection( )
  {
  }

  // The original constructors will be kept around for backwards
  // compatibility. Future Stylesheets should use the approaite
  // connect method to receive full error information.
  //
  /**
   * @param exprContext
   * @param ConnPoolName
   */
  public XConnection( ExpressionContext exprContext, String ConnPoolName )
  {
    connect(exprContext, ConnPoolName);
  }

  /**
   * @param exprContext
   * @param driver
   * @param dbURL
   */
  public XConnection( ExpressionContext exprContext, String driver, String dbURL )
  {
    connect(exprContext, driver, dbURL);
  }

  /**
   * @param exprContext
   * @param list
   */
  public XConnection( ExpressionContext exprContext, NodeList list )
  {
    connect(exprContext, list);
  }

  /**
   * @param exprContext
   * @param driver
   * @param dbURL
   * @param user
   * @param password
   */
  public XConnection( ExpressionContext exprContext, String driver, String dbURL, String user, String password )
  {
    connect(exprContext, driver, dbURL, user, password);
  }

  /**
   * @param exprContext
   * @param driver
   * @param dbURL
   * @param protocolElem
   */
  public XConnection( ExpressionContext exprContext, String driver, String dbURL, Element protocolElem )
  {
    connect(exprContext, driver, dbURL, protocolElem);
  }


  /**
   * Create an XConnection using the name of an existing Connection Pool
   * @param exprContext
   * @param ConnPoolName
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, String ConnPoolName )
  {
    try
    {
      m_ConnectionPool = m_PoolMgr.getPool(ConnPoolName);

      if (m_ConnectionPool == null)
        throw new java.lang.IllegalArgumentException("Invalid Pool Name");

      m_IsDefaultPool = false;
      return new XBooleanStatic(true);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }

  }

  /**
   * Create an XConnection object with just a driver and database URL.
   * @param exprContext
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, String driver, String dbURL )
  {
    try
    {
      init(driver, dbURL, new Properties());
      return new XBooleanStatic(true);
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
  }

  /**
   * @param exprContext
   * @param protocolElem
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, Element protocolElem )
  {
    try
    {
      initFromElement(protocolElem);
      return new XBooleanStatic(true);
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
  }

  /**
   * @param exprContext
   * @param list
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, NodeList list )
  {
    try
    {
      initFromElement( (Element) list.item(0) );
      return new XBooleanStatic(true);
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
  }

  /**
   * Create an XConnection object with user ID and password.
   * @param exprContext
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param user user ID.
   * @param password connection password.
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, String driver, String dbURL, String user, String password )
  {
    try
    {
      Properties prop = new Properties();
      prop.put("user", user);
      prop.put("password", password);

      init(driver, dbURL, prop);

      return new XBooleanStatic(true);
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
  }


  /**
   * Create an XConnection object with a connection protocol
   * @param exprContext
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param protocolElem list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   * @return
   */
  public XBooleanStatic connect( ExpressionContext exprContext, String driver, String dbURL, Element protocolElem )
  {
    try
    {
      Properties prop = new Properties();

      NamedNodeMap atts = protocolElem.getAttributes();

      for (int i = 0; i < atts.getLength(); i++)
      {
        prop.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
      }

      init(driver, dbURL, prop);

      return new XBooleanStatic(true);
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return new XBooleanStatic(false);
    }
  }


  /**
   * Allow the database connection information to be sepcified in
   * the XML tree. The connection information could also be
   * externally originated and passed in as an XSL Parameter.
   * The required XML Format is as follows.
   * A document fragment is needed to specify the connection information
   * the top tag name is not specific for this code, we are only interested
   * in the tags inside.
   * <DBINFO-TAG>
   * Specify the driver name for this connection pool
   * <dbdriver>drivername</dbdriver>
   * Specify the URL for the driver in this connection pool
   * <dburl>url</dburl>
   * Specify the password for this connection pool
   * <password>password</password>
   * Specify the username for this connection pool
   * <user>username</user>
   * You can add extra protocol items including the User Name & Password
   * with the protocol tag. For each extra protocol item, add a new element
   * where the name of the item is specified as the name attribute and
   * and its value as the elements value.
   * <protocol name="name of value">value</protocol>
   * </DBINFO-TAG>
   * @param e
   * @return
   * @throws SQLException
   */
  private void initFromElement( Element e )throws SQLException
  {

    Properties prop = new Properties();
    String driver = "";
    String dbURL = "";
    Node n = e.getFirstChild();

    if (null == n) return; // really need to throw an error

    do
    {
      String nName = n.getNodeName();

      if (nName.equalsIgnoreCase("dbdriver"))
      {
        driver = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          driver = n1.getNodeValue();
        }
      }

      if (nName.equalsIgnoreCase("dburl"))
      {
        dbURL = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          dbURL = n1.getNodeValue();
        }
      }

      if (nName.equalsIgnoreCase("password"))
      {
        String s = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          s = n1.getNodeValue();
        }
        prop.put("password", s);
      }

      if (nName.equalsIgnoreCase("user"))
      {
        String s = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          s = n1.getNodeValue();
        }
        prop.put("user", s);
      }

      if (nName.equalsIgnoreCase("protocol"))
      {
        String Name = "";

        NamedNodeMap attrs = n.getAttributes();
        Node n1 = attrs.getNamedItem("name");
        if (null != n1)
        {
          String s = "";
          Name = n1.getNodeValue();

          Node n2 = n.getFirstChild();
          if (null != n2) s = n2.getNodeValue();

          prop.put(Name, s);
        }
      }

    } while ( (n = n.getNextSibling()) != null);

    init(driver, dbURL, prop);
  }



  /**
   * Initilize is being called because we did not have an
   * existing Connection Pool, so let's see if we created one
   * already or lets create one ourselves.
   * @param driver
   * @param dbURL
   * @param prop
   * @return
   * @throws SQLException
   */
  private void init( String driver, String dbURL, Properties prop )throws SQLException
  {
    Connection con = null;

    if (DEBUG)
      System.out.println("XConnection, Connection Init");

    String user = prop.getProperty("user");
    if (user == null) user = "";

    String passwd = prop.getProperty("password");
    if (passwd == null) passwd = "";


    String poolName = driver + dbURL + user + passwd;
    ConnectionPool cpool = m_PoolMgr.getPool(poolName);

    if (cpool == null)
    {

      if (DEBUG)
      {
        System.out.println("XConnection, Creating Connection");
        System.out.println(" Driver  :" + driver);
        System.out.println(" URL     :" + dbURL);
        System.out.println(" user    :" + user);
        System.out.println(" passwd  :" + passwd);
      }


      DefaultConnectionPool defpool = new DefaultConnectionPool();

      if ((DEBUG) && (defpool == null))
        System.out.println("Failed to Create a Default Connection Pool");

      defpool.setDriver(driver);
      defpool.setURL(dbURL);
      defpool.setProtocol(prop);

      // Only enable pooling in the default pool if we are explicatly
      // told too.
      if (m_DefaultPoolingEnabled) defpool.setPoolEnabled(true);

      m_PoolMgr.registerPool(poolName, defpool);
      m_ConnectionPool = defpool;
    }
    else
    {
      m_ConnectionPool = cpool;
    }

    m_IsDefaultPool = true;

    //
    // Let's test to see if we really can connect
    // Just remember to give it back after the test.
    //
    try
    {
      con = m_ConnectionPool.getConnection();
    }
    catch(SQLException e)
    {
      if (con != null)
      {
        m_ConnectionPool.releaseConnectionOnError(con);
        con = null;
      }
      throw e;
    }
    finally
    {
      m_ConnectionPool.releaseConnection(con);
    }
  }


  /**
   * Execute a query statement by instantiating an
   * @param exprContext
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   * @throws SQLException
   * @link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a
   * @link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   */
  public DTM query( ExpressionContext exprContext, String queryString )
  {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    DTMManagerDefault mgrDefault = null;
    SQLDocument doc = null;

    try
    {
      if (DEBUG) System.out.println("query()");
      if (null == m_ConnectionPool)
      {
        // Build an Error Document, NOT Connected
        return null;
      }

      try
      {
        con = m_ConnectionPool.getConnection();
        stmt = con.createStatement();
        rs = stmt.executeQuery(queryString);
      }
      catch(SQLException e)
      {
        // We have not created a document yet, so lets close the
        // connection ourselves then let the process deal with the
        // error.
        //
        try  { if (null != rs) rs.close(); }
        catch(Exception e1) {}
        try  { if (null != stmt) stmt.close(); }
        catch(Exception e1) { }
        try  {
          if (null != con) m_ConnectionPool.releaseConnectionOnError(con);
        } catch(Exception e1) { }

        buildErrorDocument(exprContext, e);
        return null;
      }

      if (DEBUG) System.out.println("..creatingSQLDocument");

      DTMManager mgr =
        ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
      mgrDefault = (DTMManagerDefault) mgr;
      int dtmIdent = mgrDefault.getFirstFreeDTMID();

      doc =
        new SQLDocument(
          mgr, dtmIdent << DTMManager.IDENT_DTM_NODE_BITS ,
          m_ConnectionPool, con, stmt, rs, m_IsStreamingEnabled);

      if (null != doc)
      {
        if (DEBUG) System.out.println("..returning Document");

        // Register our document
        mgrDefault.addDTM(doc, dtmIdent);

        // also keep a local reference
        m_OpenSQLDocuments.addElement(doc);
        return doc;
      }
      else
      {
        return null;
      }
    }
    catch(SQLException e)
    {
      if ((doc != null) && (mgrDefault != null))
      {
        doc.closeOnError();
        mgrDefault.release(doc, true);
      }
      buildErrorDocument(exprContext, e);
      return null;
    }
    catch (Exception e)
    {
      if ((doc != null) && (mgrDefault != null))
      {
        doc.closeOnError();
        mgrDefault.release(doc, true);
      }

      if (DEBUG) System.out.println("exception in query()");
      buildErrorDocument(exprContext, e);
      return null;
    }
    finally
    {
      if (DEBUG) System.out.println("leaving query()");
    }
  }

  /**
   * Execute a parameterized query statement by instantiating an
   * @param exprContext
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   * @throws SQLException
   * @link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a
   * @link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   */
  public DTM pquery( ExpressionContext exprContext, String queryString )
  {
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      int indx;

      try
      {
        con = m_ConnectionPool.getConnection();
        stmt = con.prepareStatement(queryString);
      }
      catch(SQLException e)
      {
        // We have not created a document yet, so lets close the
        // connection ourselves then let the process deal with the
        // error.
        //
        try { if (null != stmt) stmt.close(); }
        catch(Exception e1) { }
        try {
          if (null != con) m_ConnectionPool.releaseConnectionOnError(con);
        }  catch(Exception e1) {}

        // Re throw the error so the process can handle the error
        // normally
        throw e;
      }

      if (DEBUG) System.out.println("..building Prepared Statement");

      try
      {
        Enumeration enum = m_ParameterList.elements();
        indx = 1;
        while (enum.hasMoreElements())
        {
          QueryParameter qp = (QueryParameter) enum.nextElement();
          setParameter(indx, stmt, qp);
          indx++;
        }

        rs = stmt.executeQuery();
      }
      catch(SQLException e)
      {
        // We have not created a document yet, so lets close the
        // connection ourselves then let the process deal with the
        // error.
        //
        try { if (null != rs) rs.close();  }
        catch(Exception e1) {  }
        try { if (null != stmt) stmt.close(); }
        catch(Exception e1) {  }
        try  {
          if (null != con) m_ConnectionPool.releaseConnectionOnError(con);
        } catch(Exception e1) { }

        // Re throw the error so the process can handle the error
        // normally
        throw e;
      }

      if (DEBUG) System.out.println("..creatingSQLDocument");

      DTMManager mgr =
        ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
      DTMManagerDefault mgrDefault = (DTMManagerDefault) mgr;
      int dtmIdent = mgrDefault.getFirstFreeDTMID();

      SQLDocument doc =
        new SQLDocument(mgr, dtmIdent << DTMManager.IDENT_DTM_NODE_BITS,
        m_ConnectionPool, con, stmt, rs, m_IsStreamingEnabled);

      if (null != doc)
      {
        if (DEBUG) System.out.println("..returning Document");

        // Register our document
        mgrDefault.addDTM(doc, dtmIdent);

        // also keep a local reference
        m_OpenSQLDocuments.addElement(doc);
        return doc;
      }
      else
      {
        // Build Error Doc, BAD Result Set
        return null;
      }
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return null;
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return null;
    }
  }


  /**
   * Execute a parameterized query statement by instantiating an
   * @param exprContext
   * @param queryString the SQL query.
   * @param typeInfo
   * @return XStatement implements NodeIterator.
   * @throws SQLException
   * @link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a
   * @link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   * This method allows for the user to pass in a comma seperated
   * String that represents a list of parameter types. If supplied
   * the parameter types will be used to overload the current types
   * in the current parameter list.
   */
  public DTM pquery( ExpressionContext exprContext, String queryString, String typeInfo )
  {
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      int indx;

      // Parse up the parameter types that were defined
      // with the query
      StringTokenizer plist = new StringTokenizer(typeInfo);

      // Override the existing type that is stored in the
      // parameter list. If there are more types than parameters
      // ignore for now, a more meaningfull error should occur
      // when the actual query is executed.
      indx = 0;
      while (plist.hasMoreTokens())
      {
        String value = plist.nextToken();
        QueryParameter qp = (QueryParameter) m_ParameterList.elementAt(indx);
        if ( null != qp )
        {
          qp.setType(value);
        }

        indx++;
      }

      try
      {
        con = m_ConnectionPool.getConnection();
        stmt = con.prepareStatement(queryString);
      }
      catch(SQLException e)
      {
        // We have not created a document yet, so lets close the
        // connection ourselves then let the process deal with the
        // error.
        //
        try { if (null != stmt) stmt.close(); }
        catch(Exception e1) { }
        try {
          if (null != con) m_ConnectionPool.releaseConnectionOnError(con);
        } catch(Exception e1) { }

        // Re throw the error so the process can handle the error
        // normally
        throw e;
      }



      if (DEBUG) System.out.println("..building Prepared Statement");

      try
      {
        Enumeration enum = m_ParameterList.elements();
        indx = 1;
        while (enum.hasMoreElements())
        {
          QueryParameter qp = (QueryParameter) enum.nextElement();
          setParameter(indx, stmt, qp);
          indx++;
        }

        rs = stmt.executeQuery();
      }
      catch(SQLException e)
      {
        // We have not created a document yet, so lets close the
        // connection ourselves then let the process deal with the
        // error.
        //
        try { if (null != rs) rs.close(); }
        catch(Exception e1) { /* Empty */ }
        try { if (null != stmt) stmt.close(); }
        catch(Exception e1) { /* Empty */  }
        try {
          if (null != con) m_ConnectionPool.releaseConnectionOnError(con);
        } catch(Exception e1) { /* Empty */ }

        // Re throw the error so the process can handle the error
        // normally
        throw e;
      }


      if (DEBUG) System.out.println("..creatingSQLDocument");

      DTMManager mgr =
        ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
      DTMManagerDefault mgrDefault = (DTMManagerDefault) mgr;
      int dtmIdent = mgrDefault.getFirstFreeDTMID();

      SQLDocument doc =
        new SQLDocument(mgr, dtmIdent << DTMManager.IDENT_DTM_NODE_BITS ,
        m_ConnectionPool, con, stmt, rs, m_IsStreamingEnabled);

      if (null != doc)
      {
        if (DEBUG) System.out.println("..returning Document");

        // Register our document
        mgrDefault.addDTM(doc, dtmIdent);

        // also keep a local reference
        m_OpenSQLDocuments.addElement(doc);
        return doc;
      }
      else
      {
        // Build Error Doc, BAD Result Set
        return null;
      }
    }
    catch(SQLException e)
    {
      buildErrorDocument(exprContext, e);
      return null;
    }
    catch (Exception e)
    {
      buildErrorDocument(exprContext, e);
      return null;
    }
  }

  /**
   * Add an untyped value to the parameter list.
   * @param value
   * @return
   */
  public void addParameter( String value )
  {
    addParameterWithType(value, null);
  }

  /**
   * Add a typed parameter to the parameter list.
   * @param value
   * @param Type
   * @return
   */
  public void addParameterWithType( String value, String Type )
  {
    m_ParameterList.addElement( new QueryParameter(value, Type) );
  }


  /**
   * Add a single parameter to the parameter list
   * formatted as an Element
   * @param e
   * @return
   */
  public void addParameterFromElement( Element e )
  {
    NamedNodeMap attrs = e.getAttributes();
    Node Type = attrs.getNamedItem("type");
    Node n1  = e.getFirstChild();
    if (null != n1)
    {
      String value = n1.getNodeValue();
      if (value == null) value = "";
      m_ParameterList.addElement( new QueryParameter(value, Type.getNodeValue()) );
    }
  }


  /**
   * Add a section of parameters to the Parameter List
   * Do each element from the list
   * @param nl
   * @return
   */
  public void addParameterFromElement( NodeList nl )
  {
    //
    // Each child of the NodeList represents a node
    // match from the select= statment. Process each
    // of them as a seperate list.
    // The XML Format is as follows
    //
    // <START-TAG>
    //   <TAG1 type="int">value</TAG1>
    //   <TAGA type="int">value</TAGA>
    //   <TAG2 type="string">value</TAG2>
    // </START-TAG>
    //
    // The XSL to process this is formatted as follows
    // <xsl:param name="plist" select="//START-TAG" />
    // <sql:addParameter( $plist );
    //
    int count = nl.getLength();
    for (int x=0; x<count; x++)
    {
      addParameters( (Element) nl.item(x));
    }
  }

  /**
   * @param elem
   * @return
   */
  private void addParameters( Element elem )
  {
    //
    // Process all of the Child Elements
    // The format is as follows
    //
    //<TAG type ="typeid">value</TAG>
    //<TAG1 type ="typeid">value</TAG1>
    //<TAGA type ="typeid">value</TAGA>
    //
    // The name of the Node is not important just is value
    // and if it contains a type attribute

    Node n = elem.getFirstChild();

    if (null == n) return;

    do
    {
      if (n.getNodeType() == Node.ELEMENT_NODE)
      {
        NamedNodeMap attrs = n.getAttributes();
        Node Type = attrs.getNamedItem("type");
        String TypeStr;

        if (Type == null) TypeStr = "string";
        else TypeStr = Type.getNodeValue();

        Node n1  = n.getFirstChild();
        if (null != n1)
        {
          String value = n1.getNodeValue();
          if (value == null) value = "";


          m_ParameterList.addElement(
            new QueryParameter(value, TypeStr) );
        }
      }
    } while ( (n = n.getNextSibling()) != null);
  }

  /**
   * @return
   */
  public void clearParameters( )
  {
    m_ParameterList.removeAllElements();
  }

  /**
   * There is a problem with some JDBC drivers when a Connection
   * is open and the JVM shutsdown. If there is a problem, there
   * is no way to control the currently open connections in the
   * pool. So for the default connection pool, the actuall pooling
   * mechinsm is disabled by default. The Stylesheet designer can
   * re-enabled pooling to take advantage of connection pools.
   * The connection pool can even be disabled which will close all
   * outstanding connections.
   * @return
   */
  public void enableDefaultConnectionPool( )
  {

    if (DEBUG)
      System.out.println("Enabling Default Connection Pool");

    m_DefaultPoolingEnabled = true;

    if (m_ConnectionPool == null) return;
    if (m_IsDefaultPool) return;

    m_ConnectionPool.setPoolEnabled(true);

  }

  /**
   * See enableDefaultConnectionPool
   * @return
   */
  public void disableDefaultConnectionPool( )
  {
    if (DEBUG)
      System.out.println("Disabling Default Connection Pool");

    m_DefaultPoolingEnabled = false;

    if (m_ConnectionPool == null) return;
    if (!m_IsDefaultPool) return;

    m_ConnectionPool.setPoolEnabled(false);
  }


  /**
   * Control how the SQL Document uses memory. In Streaming Mode,
   * memory consumption is greatly reduces so you can have queries
   * of unlimited size but it will not let you traverse the data
   * more than once.
   * @return
   */
  public void enableStreamingMode( )
  {

    if (DEBUG)
      System.out.println("Enabling Streaming Mode");

    m_IsStreamingEnabled = true;
  }

  /**
   * Control how the SQL Document uses memory. In Streaming Mode,
   * memory consumption is greatly reduces so you can have queries
   * of unlimited size but it will not let you traverse the data
   * more than once.
   * @return
   */
  public void disableStreamingMode( )
  {

    if (DEBUG)
      System.out.println("Disable Streaming Mode");

    m_IsStreamingEnabled = false;
  }

  /**
   * Provide access to the last error that occued. This error
   * may be over written when the next operation occurs.
   * @return
   */
  public DTM getError( )
  {
    return m_Error;
  }

  /**
   * Close the connection to the data source.
   * @return
   * @throws SQLException
   */
  public void close( )throws SQLException
  {

    if (DEBUG)
      System.out.println("Entering XConnection.close");

    //
    // This function is included just for Legacy support
    // If it is really called then we must me using a single
    // document interface, so close all open documents.
    while(m_OpenSQLDocuments.size() != 0)
    {
      SQLDocument d = (SQLDocument) m_OpenSQLDocuments.elementAt(0);
      d.close();
      m_OpenSQLDocuments.removeElementAt(0);
    }

    if (DEBUG)
      System.out.println("Exiting XConnection.close");

  }

  /**
   * Close the connection to the data source. Only close the connections
   * for a single document.
   * @param sqldoc
   * @return
   * @throws SQLException
   */
  public void close( SQLDocument sqldoc )throws SQLException
  {
    if (DEBUG)
      System.out.println("Entering XConnection.close");

    int size = m_OpenSQLDocuments.size();

    for(int x=0; x<size; x++)
    {
      SQLDocument d = (SQLDocument) m_OpenSQLDocuments.elementAt(x);
      if (d == sqldoc)
      {
        d.close();
        m_OpenSQLDocuments.removeElementAt(x);
      }
    }
  }

  /**
   * Set the parameter for a Prepared Statement
   * @param pos
   * @param stmt
   * @param p
   * @return
   * @throws SQLException
   */
  public void setParameter( int pos, PreparedStatement stmt, QueryParameter p )throws SQLException
  {
    String type = p.getType();
    if (type.equalsIgnoreCase("string"))
    {
      stmt.setString(pos, p.getValue());
    }

    if (type.equalsIgnoreCase("bigdecimal"))
    {
      stmt.setBigDecimal(pos, new BigDecimal(p.getValue()));
    }

    if (type.equalsIgnoreCase("boolean"))
    {
      Integer i = new Integer( p.getValue() );
      boolean b = ((i.intValue() != 0) ? false : true);
      stmt.setBoolean(pos, b);
    }

    if (type.equalsIgnoreCase("bytes"))
    {
      stmt.setBytes(pos, p.getValue().getBytes());
    }

    if (type.equalsIgnoreCase("date"))
    {
      stmt.setDate(pos, Date.valueOf(p.getValue()));
    }

    if (type.equalsIgnoreCase("double"))
    {
      Double d = new Double(p.getValue());
      stmt.setDouble(pos, d.doubleValue() );
    }

    if (type.equalsIgnoreCase("float"))
    {
      Float f = new Float(p.getValue());
      stmt.setFloat(pos, f.floatValue());
    }

    if (type.equalsIgnoreCase("long"))
    {
      Long l = new Long(p.getValue());
      stmt.setLong(pos, l.longValue());
    }

    if (type.equalsIgnoreCase("short"))
    {
      Short s = new Short(p.getValue());
      stmt.setShort(pos, s.shortValue());
    }

    if (type.equalsIgnoreCase("time"))
    {
      stmt.setTime(pos, Time.valueOf(p.getValue()) );
    }

    if (type.equalsIgnoreCase("timestamp"))
    {

      stmt.setTimestamp(pos, Timestamp.valueOf(p.getValue()) );
    }

  }

  /**
   * @param exprContext
   * @param excp
   * @return
   */
  private void buildErrorDocument( ExpressionContext exprContext, SQLException excp )
  {
    try
    {
      DTMManager mgr =
        ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
      DTMManagerDefault mgrDefault = (DTMManagerDefault) mgr;
      int dtmIdent = mgrDefault.getFirstFreeDTMID();

      m_Error = new SQLErrorDocument(mgr, dtmIdent << DTMManager.IDENT_DTM_NODE_BITS, excp);

      // Register our document
      mgrDefault.addDTM(m_Error, dtmIdent);

    }
    catch(Exception e)
    {
      m_Error = null;
    }
  }

  /**
   * @param exprContext
   * @param excp
   * @return
   */
  private void buildErrorDocument( ExpressionContext exprContext, Exception excp )
  {
    try
    {
      DTMManager mgr =
        ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
      DTMManagerDefault mgrDefault = (DTMManagerDefault) mgr;
      int dtmIdent = mgrDefault.getFirstFreeDTMID();

      m_Error = new SQLErrorDocument(mgr, dtmIdent<<DTMManager.IDENT_DTM_NODE_BITS, excp);

      // Register our document
      mgrDefault.addDTM(m_Error, dtmIdent);

    }
    catch(Exception e)
    {
      m_Error = null;
    }
  }

  /**
   * @return
   */
  protected void finalize( )
  {
    if (DEBUG) System.out.println("In XConnection, finalize");
    try
    {
      close();
    }
    catch(Exception e)
    {
      // Empty We are final Anyway
    }
  }

}
