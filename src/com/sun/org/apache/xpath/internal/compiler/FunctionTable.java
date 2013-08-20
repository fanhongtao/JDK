/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: FunctionTable.java,v 1.6 2004/02/17 04:32:48 minchau Exp $
 */
package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.functions.Function;

/**
 * The function table for XPath.
 */
public class FunctionTable
{

  /** The 'current()' id. */
  public static final int FUNC_CURRENT = 0;

  /** The 'last()' id. */
  public static final int FUNC_LAST = 1;

  /** The 'position()' id. */
  public static final int FUNC_POSITION = 2;

  /** The 'count()' id. */
  public static final int FUNC_COUNT = 3;

  /** The 'id()' id. */
  public static final int FUNC_ID = 4;

  /** The 'key()' id (XSLT). */
  public static final int FUNC_KEY = 5;

  /** The 'local-name()' id. */
  public static final int FUNC_LOCAL_PART = 7;

  /** The 'namespace-uri()' id. */
  public static final int FUNC_NAMESPACE = 8;

  /** The 'name()' id. */
  public static final int FUNC_QNAME = 9;

  /** The 'generate-id()' id. */
  public static final int FUNC_GENERATE_ID = 10;

  /** The 'not()' id. */
  public static final int FUNC_NOT = 11;

  /** The 'true()' id. */
  public static final int FUNC_TRUE = 12;

  /** The 'false()' id. */
  public static final int FUNC_FALSE = 13;

  /** The 'boolean()' id. */
  public static final int FUNC_BOOLEAN = 14;

  /** The 'number()' id. */
  public static final int FUNC_NUMBER = 15;

  /** The 'floor()' id. */
  public static final int FUNC_FLOOR = 16;

  /** The 'ceiling()' id. */
  public static final int FUNC_CEILING = 17;

  /** The 'round()' id. */
  public static final int FUNC_ROUND = 18;

  /** The 'sum()' id. */
  public static final int FUNC_SUM = 19;

  /** The 'string()' id. */
  public static final int FUNC_STRING = 20;

  /** The 'starts-with()' id. */
  public static final int FUNC_STARTS_WITH = 21;

  /** The 'contains()' id. */
  public static final int FUNC_CONTAINS = 22;

  /** The 'substring-before()' id. */
  public static final int FUNC_SUBSTRING_BEFORE = 23;

  /** The 'substring-after()' id. */
  public static final int FUNC_SUBSTRING_AFTER = 24;

  /** The 'normalize-space()' id. */
  public static final int FUNC_NORMALIZE_SPACE = 25;

  /** The 'translate()' id. */
  public static final int FUNC_TRANSLATE = 26;

  /** The 'concat()' id. */
  public static final int FUNC_CONCAT = 27;

  /** The 'substring()' id. */
  public static final int FUNC_SUBSTRING = 29;

  /** The 'string-length()' id. */
  public static final int FUNC_STRING_LENGTH = 30;

  /** The 'system-property()' id. */
  public static final int FUNC_SYSTEM_PROPERTY = 31;

  /** The 'lang()' id. */
  public static final int FUNC_LANG = 32;

  /** The 'function-available()' id (XSLT). */
  public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;

  /** The 'element-available()' id (XSLT). */
  public static final int FUNC_EXT_ELEM_AVAILABLE = 34;

  /** The 'unparsed-entity-uri()' id (XSLT). */
  public static final int FUNC_UNPARSED_ENTITY_URI = 36;

  // Proprietary

  /** The 'document-location()' id (Proprietary). */
  public static final int FUNC_DOCLOCATION = 35;

  /**
   * The function table.
   */
  private static FuncLoader m_functions[];

  /**
   * Number of built in functions.  Be sure to update this as
   * built-in functions are added.
   */
  private static final int NUM_BUILT_IN_FUNCS = 37;

  /**
   * Number of built-in functions that may be added.
   */
  private static final int NUM_ALLOWABLE_ADDINS = 30;

  /**
   * The index to the next free function index.
   */
  static int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;

  static
  {
    m_functions = new FuncLoader[NUM_BUILT_IN_FUNCS + NUM_ALLOWABLE_ADDINS];
    m_functions[FUNC_CURRENT] = new FuncLoader("FuncCurrent", FUNC_CURRENT);
    m_functions[FUNC_LAST] = new FuncLoader("FuncLast", FUNC_LAST);
    m_functions[FUNC_POSITION] = new FuncLoader("FuncPosition",
                                                FUNC_POSITION);
    m_functions[FUNC_COUNT] = new FuncLoader("FuncCount", FUNC_COUNT);
    m_functions[FUNC_ID] = new FuncLoader("FuncId", FUNC_ID);
    m_functions[FUNC_KEY] =
      new FuncLoader("com.sun.org.apache.xalan.internal.templates.FuncKey", FUNC_KEY);

    // m_functions[FUNC_DOC] = new FuncDoc();
    m_functions[FUNC_LOCAL_PART] = new FuncLoader("FuncLocalPart",
            FUNC_LOCAL_PART);
    m_functions[FUNC_NAMESPACE] = new FuncLoader("FuncNamespace",
            FUNC_NAMESPACE);
    m_functions[FUNC_QNAME] = new FuncLoader("FuncQname", FUNC_QNAME);
    m_functions[FUNC_GENERATE_ID] = new FuncLoader("FuncGenerateId",
            FUNC_GENERATE_ID);
    m_functions[FUNC_NOT] = new FuncLoader("FuncNot", FUNC_NOT);
    m_functions[FUNC_TRUE] = new FuncLoader("FuncTrue", FUNC_TRUE);
    m_functions[FUNC_FALSE] = new FuncLoader("FuncFalse", FUNC_FALSE);
    m_functions[FUNC_BOOLEAN] = new FuncLoader("FuncBoolean", FUNC_BOOLEAN);
    m_functions[FUNC_LANG] = new FuncLoader("FuncLang", FUNC_LANG);
    m_functions[FUNC_NUMBER] = new FuncLoader("FuncNumber", FUNC_NUMBER);
    m_functions[FUNC_FLOOR] = new FuncLoader("FuncFloor", FUNC_FLOOR);
    m_functions[FUNC_CEILING] = new FuncLoader("FuncCeiling", FUNC_CEILING);
    m_functions[FUNC_ROUND] = new FuncLoader("FuncRound", FUNC_ROUND);
    m_functions[FUNC_SUM] = new FuncLoader("FuncSum", FUNC_SUM);
    m_functions[FUNC_STRING] = new FuncLoader("FuncString", FUNC_STRING);
    m_functions[FUNC_STARTS_WITH] = new FuncLoader("FuncStartsWith",
            FUNC_STARTS_WITH);
    m_functions[FUNC_CONTAINS] = new FuncLoader("FuncContains",
                                                FUNC_CONTAINS);
    m_functions[FUNC_SUBSTRING_BEFORE] = new FuncLoader("FuncSubstringBefore",
            FUNC_SUBSTRING_BEFORE);
    m_functions[FUNC_SUBSTRING_AFTER] = new FuncLoader("FuncSubstringAfter",
            FUNC_SUBSTRING_AFTER);
    m_functions[FUNC_NORMALIZE_SPACE] = new FuncLoader("FuncNormalizeSpace",
            FUNC_NORMALIZE_SPACE);
    m_functions[FUNC_TRANSLATE] = new FuncLoader("FuncTranslate",
            FUNC_TRANSLATE);
    m_functions[FUNC_CONCAT] = new FuncLoader("FuncConcat", FUNC_CONCAT);

    //m_functions[FUNC_FORMAT_NUMBER] = new FuncFormatNumber();
    m_functions[FUNC_SYSTEM_PROPERTY] = new FuncLoader("FuncSystemProperty",
            FUNC_SYSTEM_PROPERTY);
    m_functions[FUNC_EXT_FUNCTION_AVAILABLE] =
      new FuncLoader("FuncExtFunctionAvailable", FUNC_EXT_FUNCTION_AVAILABLE);
    m_functions[FUNC_EXT_ELEM_AVAILABLE] =
      new FuncLoader("FuncExtElementAvailable", FUNC_EXT_ELEM_AVAILABLE);
    m_functions[FUNC_SUBSTRING] = new FuncLoader("FuncSubstring",
            FUNC_SUBSTRING);
    m_functions[FUNC_STRING_LENGTH] = new FuncLoader("FuncStringLength",
            FUNC_STRING_LENGTH);
    m_functions[FUNC_DOCLOCATION] = new FuncLoader("FuncDoclocation",
            FUNC_DOCLOCATION);
    m_functions[FUNC_UNPARSED_ENTITY_URI] =
      new FuncLoader("FuncUnparsedEntityURI", FUNC_UNPARSED_ENTITY_URI);
  }

  /**
   * Return the name of the a function in the static table. Needed to avoid
   * making the table publicly available.
   */
  static String getFunctionName(int funcID) {
      return m_functions[funcID].getName();
  }

  /**
   * Obtain a new Function object from a function ID.
   *
   * @param which  The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link com.sun.org.apache.xpath.internal.compiler.FunctionTable}, but may 
   *    be a value installed by an external module. 
   *
   * @return a a new Function instance.
   *
   * @throws javax.xml.transform.TransformerException if ClassNotFoundException, 
   *    IllegalAccessException, or InstantiationException is thrown.
   */
  public static Function getFunction(int which)
          throws javax.xml.transform.TransformerException
  {
    return m_functions[which].getFunction();
  }

  /**
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public static int installFunction(String name, Expression func)
  {

    int funcIndex;
    Object funcIndexObj = Keywords.m_functions.get(name);

    if (null != funcIndexObj)
    {
      funcIndex = ((Integer) funcIndexObj).intValue();
    }
    else
    {
      funcIndex = m_funcNextFreeIndex;

      m_funcNextFreeIndex++;

      Keywords.m_functions.put(name, new Integer(funcIndex));
    }

    FuncLoader loader = new FuncLoader(func.getClass().getName(), funcIndex);

    m_functions[funcIndex] = loader;

    return funcIndex;
  }

  /**
   * Install a function loader at a specific index.
   * @param func A Implementation of an XPath Function object.
   * @param which  The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link com.sun.org.apache.xpath.internal.compiler.FunctionTable}, but may 
   *    be a value installed by an external module. 
   * @return the position of the function in the internal index.
   */
  public static void installFunction(Expression func, int funcIndex)
  {

    FuncLoader loader = new FuncLoader(func.getClass().getName(), funcIndex);

    m_functions[funcIndex] = loader;
  }
}
