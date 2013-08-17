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
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTM;

/**
 * This is a default implementation of a table that manages mappings from
 * expanded names to expandedNameIDs.
 *
 * %REVIEW% Note that this is not really a separate table, or a
 * separate pool. Instead, it's an access method build on top of the
 * existing pools, using three pieces of information: the index
 * numbers for a node's namespaceURI, localName, and node type, which
 * are combined to yield a composite index number.
 *
 * %TBD% startup sequence -- how this gets access to the appropriate
 * string pools in the DTMDocument/stylesheet.
 *
 * */
public class ExpandedNameTable
{

  /** Probably a reference to static pool.     */
  private DTMStringPool m_locNamesPool;

  /** Probably a reference to static pool.   */
  private DTMStringPool m_namespaceNames;
  
  public static int BITS_PER_LOCALNAME = 16;
  public static int BITS_PER_NAMESPACE = 10;

  public static int MASK_LOCALNAME = 0x0000FFFF;
  public static int MASK_NAMESPACE = 0x03FF0000;
  public static int MASK_NODETYPE = 0xFC000000;
  public static int MASK_NODEHANDLE = 0x000FFFFF;

  public static final int ROTAMOUNT_TYPE = (BITS_PER_NAMESPACE+BITS_PER_LOCALNAME);
  
  // These are all the types prerotated, for caller convenience.
  public static final int ELEMENT = ((int)DTM.ELEMENT_NODE) << ROTAMOUNT_TYPE;
  public static final int ATTRIBUTE = ((int)DTM.ATTRIBUTE_NODE) << ROTAMOUNT_TYPE;
  public static final int TEXT = ((int)DTM.TEXT_NODE) << ROTAMOUNT_TYPE;
  public static final int CDATA_SECTION = ((int)DTM.CDATA_SECTION_NODE) << ROTAMOUNT_TYPE;
  public static final int ENTITY_REFERENCE = ((int)DTM.ENTITY_REFERENCE_NODE) << ROTAMOUNT_TYPE;
  public static final int ENTITY = ((int)DTM.ENTITY_NODE) << ROTAMOUNT_TYPE;
  public static final int PROCESSING_INSTRUCTION = ((int)DTM.PROCESSING_INSTRUCTION_NODE) << ROTAMOUNT_TYPE;
  public static final int COMMENT = ((int)DTM.COMMENT_NODE) << ROTAMOUNT_TYPE;
  public static final int DOCUMENT = ((int)DTM.DOCUMENT_NODE) << ROTAMOUNT_TYPE;
  public static final int DOCUMENT_TYPE = ((int)DTM.DOCUMENT_TYPE_NODE) << ROTAMOUNT_TYPE;
  public static final int DOCUMENT_FRAGMENT =((int)DTM.DOCUMENT_FRAGMENT_NODE) << ROTAMOUNT_TYPE;
  public static final int NOTATION = ((int)DTM.NOTATION_NODE) << ROTAMOUNT_TYPE;
  public static final int NAMESPACE = ((int)DTM.NAMESPACE_NODE) << ROTAMOUNT_TYPE;

  /**
   * Create an expanded name table that uses private string pool lookup.
   */
  public ExpandedNameTable()
  {
    m_locNamesPool = new DTMSafeStringPool();
    m_namespaceNames = new DTMSafeStringPool();
  }

  /**
   * Constructor ExpandedNameTable
   *
   * @param locNamesPool Local element names lookup.
   * @param namespaceNames Namespace values lookup.
   */
  public ExpandedNameTable(DTMStringPool locNamesPool,
                           DTMStringPool namespaceNames)
  {
    m_locNamesPool = locNamesPool;
    m_namespaceNames = namespaceNames;
  }

  /**
   * Given an expanded name, return an ID.  If the expanded-name does not
   * exist in the internal tables, the entry will be created, and the ID will
   * be returned.  Any additional nodes that are created that have this
   * expanded name will use this ID.
   *
   * @param namespace
   * @param localName
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID(String namespace, String localName, int type)
  {
    int nsID = (null != namespace) ? m_namespaceNames.stringToIndex(namespace) : 0;
    int lnID = m_locNamesPool.stringToIndex(localName);
    
    int expandedTypeID = (type << (BITS_PER_NAMESPACE+BITS_PER_LOCALNAME)) 
                       | (nsID << BITS_PER_LOCALNAME) | lnID;

    return expandedTypeID;
  }
  
  /**
   * Given a type, return an expanded name ID.Any additional nodes that are 
   * created that have this expanded name will use this ID.
   *
   * @param namespace
   * @param localName
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID(int type)
  {
    int expandedTypeID = (type << (BITS_PER_NAMESPACE+BITS_PER_LOCALNAME));

    return expandedTypeID;
  }

  /**
   * Given an expanded-name ID, return the local name part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String Local name of this node, or null if the node has no name.
   */
  public String getLocalName(int ExpandedNameID)
  {
    return m_locNamesPool.indexToString(ExpandedNameID & MASK_LOCALNAME);
  }
  
  /**
   * Given an expanded-name ID, return the local name ID.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return The id of this local name.
   */
  public static final int getLocalNameID(int ExpandedNameID)
  {
    return (ExpandedNameID & MASK_LOCALNAME);
  }


  /**
   * Given an expanded-name ID, return the namespace URI part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespace(int ExpandedNameID)
  {

    int id = (ExpandedNameID & MASK_NAMESPACE) >> BITS_PER_LOCALNAME;
    return (0 == id) ? null : m_namespaceNames.indexToString(id);
  }
  
  /**
   * Given an expanded-name ID, return the namespace URI ID.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return The id of this namespace.
   */
  public static final int getNamespaceID(int ExpandedNameID)
  {
    return (ExpandedNameID & MASK_NAMESPACE) >> BITS_PER_LOCALNAME;
  }
  
  /**
   * Given an expanded-name ID, return the local name ID.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return The id of this local name.
   */
  public static final short getType(int ExpandedNameID)
  {
    return (short)(ExpandedNameID >> ROTAMOUNT_TYPE);
  }
  
}
