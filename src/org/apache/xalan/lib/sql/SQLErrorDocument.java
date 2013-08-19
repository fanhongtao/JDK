/**
 * @(#) SQLErrorDocument.java
 *
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
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 * not be used to endorse or promote products derived from this
 * software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
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
 *
 */

package org.apache.xalan.lib.sql;

import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTM;
import java.sql.SQLException;

/**
 *
 * A base class that will convert an exception into an XML stream
 * that can be returned in place of the standard result. The XML
 * format returned is a follows.
 *
 * <ext-error>
 *  <message> The Message for a generic error </message>
 *  <sql-error>
 *    <message> SQL Message from the Exception thrown </message>
 *    <code> SQL Error Code </stack>
 *  </sql-error>
 * <ext-error>
 *
 */

/**
 * The SQL Document is the main controlling class the executesa SQL Query
 */
public class SQLErrorDocument extends DTMDocument
{
  /**
   */
  private static final String S_EXT_ERROR = "ext-error";
  /**
   */
  private static final String S_SQL_ERROR = "sql-error";
  /**
   */
  private static final String S_MESSAGE = "message";
  /**
   */
  private static final String S_CODE = "code";

  /**
   */
  private int m_ErrorExt_TypeID = DTM.NULL;
  /**
   */
  private int m_Message_TypeID = DTM.NULL;
  /**
   */
  private int m_Code_TypeID = DTM.NULL;

  /**
   */
  private int m_SQLError_TypeID = DTM.NULL;

  /**
   */
  private int m_rootID = DTM.NULL;
  /**
   */
  private int m_extErrorID = DTM.NULL;
  /**
   */
  private int m_MainMessageID = DTM.NULL;

  /**
   * Build up an SQLErrorDocument that includes the basic error information
   * along with the Extended SQL Error information.
   * @param mgr
   * @param ident
   * @param error
   */
  public SQLErrorDocument( DTMManager mgr, int ident, SQLException error )
  {
    super(mgr, ident);

    createExpandedNameTable();
    buildBasicStructure(error);

    int sqlError = addElement(2, m_SQLError_TypeID, m_extErrorID, m_MainMessageID);
    int element = DTM.NULL;

    element = addElementWithData(
      new Integer(error.getErrorCode()), 3,
      m_Code_TypeID, sqlError, element);

    element = addElementWithData(
      error.getLocalizedMessage(), 3,
      m_Message_TypeID, sqlError, element);

//    this.dumpDTM();
  }


  /**
   * Build up an Error Exception with just the Standard Error Information
   * @param mgr
   * @param ident
   * @param error
   */
  public SQLErrorDocument( DTMManager mgr, int ident, Exception error )
  {
    super(mgr, ident);
    createExpandedNameTable();
    buildBasicStructure(error);
  }

  /**
   * Build up the basic structure that is common for each error.
   * @param e
   * @return
   */
  private void buildBasicStructure( Exception e )
  {
    m_rootID = addElement(0, m_Document_TypeID, DTM.NULL, DTM.NULL);
    m_extErrorID = addElement(1, m_ErrorExt_TypeID, m_rootID, DTM.NULL);
    m_MainMessageID = addElementWithData
      (e.getLocalizedMessage(), 2, m_Message_TypeID, m_extErrorID, DTM.NULL);
  }

  /**
   * Populate the Expanded Name Table with the Node that we will use.
   * Keep a reference of each of the types for access speed.
   * @return
   */
  protected void createExpandedNameTable( )
  {

    super.createExpandedNameTable();

    m_ErrorExt_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_EXT_ERROR, DTM.ELEMENT_NODE);

    m_SQLError_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SQL_ERROR, DTM.ELEMENT_NODE);

    m_Message_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_MESSAGE, DTM.ELEMENT_NODE);

    m_Code_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_CODE, DTM.ELEMENT_NODE);
  }

}