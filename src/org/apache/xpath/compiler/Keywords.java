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
package org.apache.xpath.compiler;

import java.util.Hashtable;

/**
 * <meta name="usage" content="internal"/>
 * Table of strings to operation code lookups.
 */
public class Keywords
{

  /** Table of keywords to opcode associations. */
  static Hashtable m_keywords = new Hashtable();

  /** Table of axes names to opcode associations. */
  static Hashtable m_axisnames = new Hashtable();

  /** Table of function name to function ID associations. */
  static Hashtable m_functions = new Hashtable();

  /** Table of node type strings to opcode associations. */
  static Hashtable m_nodetypes = new Hashtable();

  /** ancestor axes string. */
  private static final String FROM_ANCESTORS_STRING = "ancestor";

  /** ancestor-or-self axes string. */
  private static final String FROM_ANCESTORS_OR_SELF_STRING =
    "ancestor-or-self";

  /** attribute axes string. */
  private static final String FROM_ATTRIBUTES_STRING = "attribute";

  /** child axes string. */
  private static final String FROM_CHILDREN_STRING = "child";

  /** descendant-or-self axes string. */
  private static final String FROM_DESCENDANTS_STRING = "descendant";

  /** ancestor axes string. */
  private static final String FROM_DESCENDANTS_OR_SELF_STRING =
    "descendant-or-self";

  /** following axes string. */
  private static final String FROM_FOLLOWING_STRING = "following";

  /** following-sibling axes string. */
  private static final String FROM_FOLLOWING_SIBLINGS_STRING =
    "following-sibling";

  /** parent axes string. */
  private static final String FROM_PARENT_STRING = "parent";

  /** preceding axes string. */
  private static final String FROM_PRECEDING_STRING = "preceding";

  /** preceding-sibling axes string. */
  private static final String FROM_PRECEDING_SIBLINGS_STRING =
    "preceding-sibling";

  /** self axes string. */
  private static final String FROM_SELF_STRING = "self";

  /** namespace axes string. */
  private static final String FROM_NAMESPACE_STRING = "namespace";

  /** self axes abreviated string. */
  private static final String FROM_SELF_ABBREVIATED_STRING = ".";

  /** comment node test string. */
  private static final String NODETYPE_COMMENT_STRING = "comment";

  /** text node test string. */
  private static final String NODETYPE_TEXT_STRING = "text";

  /** processing-instruction node test string. */
  private static final String NODETYPE_PI_STRING = "processing-instruction";

  /** Any node test string. */
  private static final String NODETYPE_NODE_STRING = "node";

  /** Wildcard element string. */
  private static final String NODETYPE_ANYELEMENT_STRING = "*";

  /** current function string. */
  private static final String FUNC_CURRENT_STRING = "current";

  /** last function string. */
  private static final String FUNC_LAST_STRING = "last";

  /** position function string. */
  private static final String FUNC_POSITION_STRING = "position";

  /** count function string. */
  private static final String FUNC_COUNT_STRING = "count";

  /** id function string. */
  static final String FUNC_ID_STRING = "id";

  /** key function string (XSLT). */
  public static final String FUNC_KEY_STRING = "key";

  /** local-name function string. */
  private static final String FUNC_LOCAL_PART_STRING = "local-name";

  /** namespace-uri function string. */
  private static final String FUNC_NAMESPACE_STRING = "namespace-uri";

  /** name function string. */
  private static final String FUNC_NAME_STRING = "name";

  /** generate-id function string (XSLT). */
  private static final String FUNC_GENERATE_ID_STRING = "generate-id";

  /** not function string. */
  private static final String FUNC_NOT_STRING = "not";

  /** true function string. */
  private static final String FUNC_TRUE_STRING = "true";

  /** false function string. */
  private static final String FUNC_FALSE_STRING = "false";

  /** boolean function string. */
  private static final String FUNC_BOOLEAN_STRING = "boolean";

  /** lang function string. */
  private static final String FUNC_LANG_STRING = "lang";

  /** number function string. */
  private static final String FUNC_NUMBER_STRING = "number";

  /** floor function string. */
  private static final String FUNC_FLOOR_STRING = "floor";

  /** ceiling function string. */
  private static final String FUNC_CEILING_STRING = "ceiling";

  /** round function string. */
  private static final String FUNC_ROUND_STRING = "round";

  /** sum function string. */
  private static final String FUNC_SUM_STRING = "sum";

  /** string function string. */
  private static final String FUNC_STRING_STRING = "string";

  /** starts-with function string. */
  private static final String FUNC_STARTS_WITH_STRING = "starts-with";

  /** contains function string. */
  private static final String FUNC_CONTAINS_STRING = "contains";

  /** substring-before function string. */
  private static final String FUNC_SUBSTRING_BEFORE_STRING =
    "substring-before";

  /** substring-after function string. */
  private static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";

  /** normalize-space function string. */
  private static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";

  /** translate function string. */
  private static final String FUNC_TRANSLATE_STRING = "translate";

  /** concat function string. */
  private static final String FUNC_CONCAT_STRING = "concat";

  /** system-property function string. */
  private static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";

  /** function-available function string (XSLT). */
  private static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING =
    "function-available";

  /** element-available function string (XSLT). */
  private static final String FUNC_EXT_ELEM_AVAILABLE_STRING =
    "element-available";

  /** substring function string. */
  private static final String FUNC_SUBSTRING_STRING = "substring";

  /** string-length function string. */
  private static final String FUNC_STRING_LENGTH_STRING = "string-length";

  /** unparsed-entity-uri function string (XSLT). */
  private static final String FUNC_UNPARSED_ENTITY_URI_STRING =
    "unparsed-entity-uri";

  // Proprietary, built in functions

  /** current function string (Proprietary). */
  private static final String FUNC_DOCLOCATION_STRING = "document-location";

  static
  {
    m_axisnames.put(FROM_ANCESTORS_STRING,
                    new Integer(OpCodes.FROM_ANCESTORS));
    m_axisnames.put(FROM_ANCESTORS_OR_SELF_STRING,
                    new Integer(OpCodes.FROM_ANCESTORS_OR_SELF));
    m_axisnames.put(FROM_ATTRIBUTES_STRING,
                    new Integer(OpCodes.FROM_ATTRIBUTES));
    m_axisnames.put(FROM_CHILDREN_STRING,
                    new Integer(OpCodes.FROM_CHILDREN));
    m_axisnames.put(FROM_DESCENDANTS_STRING,
                    new Integer(OpCodes.FROM_DESCENDANTS));
    m_axisnames.put(FROM_DESCENDANTS_OR_SELF_STRING,
                    new Integer(OpCodes.FROM_DESCENDANTS_OR_SELF));
    m_axisnames.put(FROM_FOLLOWING_STRING,
                    new Integer(OpCodes.FROM_FOLLOWING));
    m_axisnames.put(FROM_FOLLOWING_SIBLINGS_STRING,
                    new Integer(OpCodes.FROM_FOLLOWING_SIBLINGS));
    m_axisnames.put(FROM_PARENT_STRING,
                    new Integer(OpCodes.FROM_PARENT));
    m_axisnames.put(FROM_PRECEDING_STRING,
                    new Integer(OpCodes.FROM_PRECEDING));
    m_axisnames.put(FROM_PRECEDING_SIBLINGS_STRING,
                    new Integer(OpCodes.FROM_PRECEDING_SIBLINGS));
    m_axisnames.put(FROM_SELF_STRING,
                    new Integer(OpCodes.FROM_SELF));
    m_axisnames.put(FROM_NAMESPACE_STRING,
                    new Integer(OpCodes.FROM_NAMESPACE));
    m_nodetypes.put(NODETYPE_COMMENT_STRING,
                    new Integer(OpCodes.NODETYPE_COMMENT));
    m_nodetypes.put(NODETYPE_TEXT_STRING,
                    new Integer(OpCodes.NODETYPE_TEXT));
    m_nodetypes.put(NODETYPE_PI_STRING,
                    new Integer(OpCodes.NODETYPE_PI));
    m_nodetypes.put(NODETYPE_NODE_STRING,
                    new Integer(OpCodes.NODETYPE_NODE));
    m_nodetypes.put(NODETYPE_ANYELEMENT_STRING,
                    new Integer(OpCodes.NODETYPE_ANYELEMENT));
    m_keywords.put(FROM_SELF_ABBREVIATED_STRING,
                   new Integer(OpCodes.FROM_SELF));
    m_keywords.put(FUNC_ID_STRING,
                   new Integer(FunctionTable.FUNC_ID));
    m_keywords.put(FUNC_KEY_STRING,
                   new Integer(FunctionTable.FUNC_KEY));
    m_functions.put(FUNC_CURRENT_STRING,
                    new Integer(FunctionTable.FUNC_CURRENT));
    m_functions.put(FUNC_LAST_STRING,
                    new Integer(FunctionTable.FUNC_LAST));
    m_functions.put(FUNC_POSITION_STRING,
                    new Integer(FunctionTable.FUNC_POSITION));
    m_functions.put(FUNC_COUNT_STRING,
                    new Integer(FunctionTable.FUNC_COUNT));
    m_functions.put(FUNC_ID_STRING,
                    new Integer(FunctionTable.FUNC_ID));
    m_functions.put(FUNC_KEY_STRING,
                    new Integer(FunctionTable.FUNC_KEY));
    m_functions.put(FUNC_LOCAL_PART_STRING,
                    new Integer(FunctionTable.FUNC_LOCAL_PART));
    m_functions.put(FUNC_NAMESPACE_STRING,
                    new Integer(FunctionTable.FUNC_NAMESPACE));
    m_functions.put(FUNC_NAME_STRING,
                    new Integer(FunctionTable.FUNC_QNAME));
    m_functions.put(FUNC_GENERATE_ID_STRING,
                    new Integer(FunctionTable.FUNC_GENERATE_ID));
    m_functions.put(FUNC_NOT_STRING,
                    new Integer(FunctionTable.FUNC_NOT));
    m_functions.put(FUNC_TRUE_STRING,
                    new Integer(FunctionTable.FUNC_TRUE));
    m_functions.put(FUNC_FALSE_STRING,
                    new Integer(FunctionTable.FUNC_FALSE));
    m_functions.put(FUNC_BOOLEAN_STRING,
                    new Integer(FunctionTable.FUNC_BOOLEAN));
    m_functions.put(FUNC_LANG_STRING,
                    new Integer(FunctionTable.FUNC_LANG));
    m_functions.put(FUNC_NUMBER_STRING,
                    new Integer(FunctionTable.FUNC_NUMBER));
    m_functions.put(FUNC_FLOOR_STRING,
                    new Integer(FunctionTable.FUNC_FLOOR));
    m_functions.put(FUNC_CEILING_STRING,
                    new Integer(FunctionTable.FUNC_CEILING));
    m_functions.put(FUNC_ROUND_STRING,
                    new Integer(FunctionTable.FUNC_ROUND));
    m_functions.put(FUNC_SUM_STRING,
                    new Integer(FunctionTable.FUNC_SUM));
    m_functions.put(FUNC_STRING_STRING,
                    new Integer(FunctionTable.FUNC_STRING));
    m_functions.put(FUNC_STARTS_WITH_STRING,
                    new Integer(FunctionTable.FUNC_STARTS_WITH));
    m_functions.put(FUNC_CONTAINS_STRING,
                    new Integer(FunctionTable.FUNC_CONTAINS));
    m_functions.put(FUNC_SUBSTRING_BEFORE_STRING,
                    new Integer(FunctionTable.FUNC_SUBSTRING_BEFORE));
    m_functions.put(FUNC_SUBSTRING_AFTER_STRING,
                    new Integer(FunctionTable.FUNC_SUBSTRING_AFTER));
    m_functions.put(FUNC_NORMALIZE_SPACE_STRING,
                    new Integer(FunctionTable.FUNC_NORMALIZE_SPACE));
    m_functions.put(FUNC_TRANSLATE_STRING,
                    new Integer(FunctionTable.FUNC_TRANSLATE));
    m_functions.put(FUNC_CONCAT_STRING,
                    new Integer(FunctionTable.FUNC_CONCAT));

    //m_functions.put(FUNC_FORMAT_NUMBER_STRING, new Integer(FunctionTable.FUNC_FORMAT_NUMBER));
    m_functions.put(FUNC_SYSTEM_PROPERTY_STRING,
                    new Integer(FunctionTable.FUNC_SYSTEM_PROPERTY));
    m_functions.put(FUNC_EXT_FUNCTION_AVAILABLE_STRING,
                    new Integer(FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE));
    m_functions.put(FUNC_EXT_ELEM_AVAILABLE_STRING,
                    new Integer(FunctionTable.FUNC_EXT_ELEM_AVAILABLE));
    m_functions.put(FUNC_SUBSTRING_STRING,
                    new Integer(FunctionTable.FUNC_SUBSTRING));
    m_functions.put(FUNC_STRING_LENGTH_STRING,
                    new Integer(FunctionTable.FUNC_STRING_LENGTH));
    m_functions.put(FUNC_UNPARSED_ENTITY_URI_STRING,
                    new Integer(FunctionTable.FUNC_UNPARSED_ENTITY_URI));

    // These aren't really functions.
    m_functions.put(NODETYPE_COMMENT_STRING,
                    new Integer(OpCodes.NODETYPE_COMMENT));
    m_functions.put(NODETYPE_TEXT_STRING,
                    new Integer(OpCodes.NODETYPE_TEXT));
    m_functions.put(NODETYPE_PI_STRING,
                    new Integer(OpCodes.NODETYPE_PI));
    m_functions.put(NODETYPE_NODE_STRING,
                    new Integer(OpCodes.NODETYPE_NODE));
    m_functions.put(FUNC_DOCLOCATION_STRING,
                    new Integer(FunctionTable.FUNC_DOCLOCATION));
  }

  /**
   * Tell if a built-in, non-namespaced function is available.
   *
   * @param methName The local name of the function.
   *
   * @return True if the function can be executed.
   */
  public static boolean functionAvailable(String methName)
  {

    try
    {
      Object tblEntry = m_functions.get(methName);

      if (null == tblEntry)
        return false;

      int funcType = ((Integer) tblEntry).intValue();

      switch (funcType)
      {
      case OpCodes.NODETYPE_COMMENT :
      case OpCodes.NODETYPE_TEXT :
      case OpCodes.NODETYPE_PI :
      case OpCodes.NODETYPE_NODE :
        return false;  // These look like functions but they're NodeTests.
      default :
        return true;
      }
    }
    catch (Exception e)
    {
      return false;
    }
  }
}
