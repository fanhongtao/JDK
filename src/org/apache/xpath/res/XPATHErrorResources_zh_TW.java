/*
 * @(#)XPATHErrorResources_zh_TW.java	1.7 02/03/26
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

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
package org.apache.xpath.res;

import org.apache.xml.utils.res.XResourceBundleBase;


import java.util.*;

import java.text.DecimalFormat;

/**
 * <meta name="usage" content="advanced"/>
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING). The array will be
 * automatically filled in with the keys, but you need to
 * fill in the actual message string. Follow the instructions
 * below.
 */
public class XPATHErrorResources_zh_TW extends XPATHErrorResources
{

  /** Field ERROR_SUFFIX          */
  public static final String ERROR_SUFFIX = "ER";

  /** Field WARNING_SUFFIX          */
  public static final String WARNING_SUFFIX = "WR";

  /** Field MAX_CODE          */
  public static final int MAX_CODE = 83;  // this is needed to keep track of the number of messages          

  /** Field MAX_WARNING          */
  public static final int MAX_WARNING = 11;  // this is needed to keep track of the number of warnings

  /** Field MAX_OTHERS          */
  public static final int MAX_OTHERS = 20;

  /** Field MAX_MESSAGES          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;

  /** Field contents          */
  static final Object[][] contents =
    new Object[MAX_MESSAGES + MAX_OTHERS + 1][2];

  /*
  * Now fill in the message keys.
  * This does not need to be updated. If MAX_CODE and MAX_WARNING
  * are correct, the keys will get filled in automatically with
  * the value ERxxxx (WRxxxx for warnings) where xxxx is a
  * formatted number corresponding to the error code (i.e. ER0001).
  */
  static
  {
    for (int i = 0; i < MAX_CODE + 1; i++)
    {
      contents[i][0] = getMKey(i);
    }

    for (int i = 1; i < MAX_WARNING + 1; i++)
    {
      contents[i + MAX_CODE][0] = getWKey(i);
    }
  }

  /*
  * Now fill in the message text.
  * First create an int for the message code. Make sure you
  * update MAX_CODE for error messages and MAX_WARNING for warnings
  * Then fill in the message text for that message code in the
  * array. Use the new error code as the index into the array.
  */

  // Error messages...

  /** Field ERROR0000          */
  public static final int ERROR0000 = 0;

  static
  {
    contents[ERROR0000][1] = "{0}";
  }

  /** Field ER_CURRENT_NOT_ALLOWED_IN_MATCH          */
  public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;

  static
  {
    contents[ER_CURRENT_NOT_ALLOWED_IN_MATCH][1] =
      "\u5728\u7b26\u5408\u578b\u6a23\u4e2d\u4e0d\u5141\u8a31\u4f7f\u7528 current() \u51fd\u5f0f\uff01";
  }

  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  public static final int ER_CURRENT_TAKES_NO_ARGS = 2;

  static
  {
    contents[ER_CURRENT_TAKES_NO_ARGS][1] =
      "current() \u51fd\u5f0f\u4e0d\u63a5\u53d7\u5f15\u6578\uff01";
  }

  /** Field ER_DOCUMENT_REPLACED          */
  public static final int ER_DOCUMENT_REPLACED = 3;

  static
  {
    contents[ER_DOCUMENT_REPLACED][1] =
      "document() \u51fd\u5f0f\u5efa\u7f6e\u5df2\u88ab org.apache.xalan.xslt.FuncDocument \u53d6\u4ee3\uff01";
  }

  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;

  static
  {
    contents[ER_CONTEXT_HAS_NO_OWNERDOC][1] =
      "\u4e0a\u4e0b\u6587\u4e0d\u542b\u64c1\u6709\u8005\u6587\u4ef6\uff01";
  }

  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;

  static
  {
    contents[ER_LOCALNAME_HAS_TOO_MANY_ARGS][1] =
      "local-name() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;

  static
  {
    contents[ER_NAMESPACEURI_HAS_TOO_MANY_ARGS][1] =
      "namespace-uri() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;

  static
  {
    contents[ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS][1] =
      "normalize-space() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;

  static
  {
    contents[ER_NUMBER_HAS_TOO_MANY_ARGS][1] =
      "number() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;

  static
  {
    contents[ER_NAME_HAS_TOO_MANY_ARGS][1] = "name() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;

  static
  {
    contents[ER_STRING_HAS_TOO_MANY_ARGS][1] =
      "string() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;

  static
  {
    contents[ER_STRINGLENGTH_HAS_TOO_MANY_ARGS][1] =
      "string-length() \u6709\u592a\u591a\u5f15\u6578\u3002";
  }

  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;

  static
  {
    contents[ER_TRANSLATE_TAKES_3_ARGS][1] =
      "translate() \u51fd\u5f0f\u9700\u8981 3 \u500b\u5f15\u6578\uff01";
  }

  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;

  static
  {
    contents[ER_UNPARSEDENTITYURI_TAKES_1_ARG][1] =
      "unparsed-entity-uri \u51fd\u5f0f\u9700\u8981 1 \u500b\u5f15\u6578\uff01";
  }

  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;

  static
  {
    contents[ER_NAMESPACEAXIS_NOT_IMPLEMENTED][1] =
      "namespace \u8ef8\u5c1a\u672a\u5efa\u7f6e\uff01";
  }

  /** Field ER_UNKNOWN_AXIS          */
  public static final int ER_UNKNOWN_AXIS = 15;

  static
  {
    contents[ER_UNKNOWN_AXIS][1] = "\u672a\u77e5\u8ef8\uff1a{0}";
  }

  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  public static final int ER_UNKNOWN_MATCH_OPERATION = 16;

  static
  {
    contents[ER_UNKNOWN_MATCH_OPERATION][1] = "\u672a\u77e5\u7684\u7b26\u5408\u4f5c\u696d\uff01";
  }

  /** Field ER_INCORRECT_ARG_LENGTH          */
  public static final int ER_INCORRECT_ARG_LENGTH = 17;

  static
  {
    contents[ER_INCORRECT_ARG_LENGTH][1] =
      "processing-instruction() \u7bc0\u9ede\u6e2c\u8a66\u7684\u5f15\u6578\u9577\u5ea6\u4e0d\u6b63\u78ba\uff01";
  }

  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  public static final int ER_CANT_CONVERT_TO_NUMBER = 18;

  static
  {
    contents[ER_CANT_CONVERT_TO_NUMBER][1] =
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba\u6578\u5b57";
  }

  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  public static final int ER_CANT_CONVERT_TO_NODELIST = 19;

  static
  {
    contents[ER_CANT_CONVERT_TO_NODELIST][1] =
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba NodeList\uff01";
  }

  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;

  static
  {
    contents[ER_CANT_CONVERT_TO_MUTABLENODELIST][1] =
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba NodeSetDTM\uff01";
  }

  /** Field ER_CANT_CONVERT_TO_TYPE          */
  public static final int ER_CANT_CONVERT_TO_TYPE = 21;

  static
  {
    contents[ER_CANT_CONVERT_TO_TYPE][1] =
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba type#{1}";
  }

  /** Field ER_EXPECTED_MATCH_PATTERN          */
  public static final int ER_EXPECTED_MATCH_PATTERN = 22;

  static
  {
    contents[ER_EXPECTED_MATCH_PATTERN][1] =
      "\u5728 getMatchScore \u4e2d\u9810\u671f\u7684\u7b26\u5408\u578b\u6a23\uff01";
  }

  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  public static final int ER_COULDNOT_GET_VAR_NAMED = 23;

  static
  {
    contents[ER_COULDNOT_GET_VAR_NAMED][1] =
      "\u7121\u6cd5\u53d6\u5f97\u53eb\u4f5c {0} \u7684\u8b8a\u6578";
  }

  /** Field ER_UNKNOWN_OPCODE          */
  public static final int ER_UNKNOWN_OPCODE = 24;

  static
  {
    contents[ER_UNKNOWN_OPCODE][1] = "\u932f\u8aa4\uff01\u672a\u77e5\u7684\u4f5c\u696d\u78bc\uff1a{0}";
  }

  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;

  static
  {
    contents[ER_EXTRA_ILLEGAL_TOKENS][1] = "\u984d\u5916\u4e0d\u5408\u898f\u5247\u7684\u8a18\u865f\uff1a{0}";
  }

  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;

  static
  {
    contents[ER_EXPECTED_DOUBLE_QUOTE][1] =
      "\u6587\u5b57\u5217\u5f15\u865f\u62ec\u932f... \u9810\u671f\u51fa\u73fe\u96d9\u5f15\u865f\uff01";
  }

  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  public static final int ER_EXPECTED_SINGLE_QUOTE = 27;

  static
  {
    contents[ER_EXPECTED_SINGLE_QUOTE][1] =
      "\u6587\u5b57\u5217\u5f15\u865f\u62ec\u932f... \u9810\u671f\u51fa\u73fe\u55ae\u5f15\u865f\uff01";
  }

  /** Field ER_EMPTY_EXPRESSION          */
  public static final int ER_EMPTY_EXPRESSION = 28;

  static
  {
    contents[ER_EMPTY_EXPRESSION][1] = "\u7a7a\u7684\u8868\u793a\u5f0f\uff01";
  }

  /** Field ER_EXPECTED_BUT_FOUND          */
  public static final int ER_EXPECTED_BUT_FOUND = 29;

  static
  {
    contents[ER_EXPECTED_BUT_FOUND][1] = "\u9810\u671f {0}\uff0c\u537b\u627e\u5230\uff1a{1}";
  }

  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;

  static
  {
    contents[ER_INCORRECT_PROGRAMMER_ASSERTION][1] =
      "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u5047\u8a2d\u4e0d\u6b63\u78ba\uff01- {0}";
  }

  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;

  static
  {
    contents[ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL][1] =
      "boolean(...) \u5f15\u6578\u5728 19990709 XPath \u521d\u7a3f\u4e2d\u4e0d\u518d\u662f\u53ef\u9078\u7528\u7684\u3002";
  }

  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;

  static
  {
    contents[ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG][1] =
      "\u627e\u5230 ','\uff0c\u4f46\u4e4b\u524d\u6c92\u6709\u5f15\u6578\uff01";
  }

  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;

  static
  {
    contents[ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG][1] =
      "\u627e\u5230 ','\uff0c\u4f46\u4e4b\u5f8c\u6c92\u6709\u5f15\u6578\uff01";
  }

  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;

  static
  {
    contents[ER_PREDICATE_ILLEGAL_SYNTAX][1] =
      "'..[predicate]' \u6216 '.[predicate]' \u662f\u4e0d\u5408\u898f\u5247\u7684\u8a9e\u6cd5\u3002\u8acb\u4f7f\u7528 'self::node()[predicate]' \u4f86\u4ee3\u66ff\u3002";
  }

  /** Field ER_ILLEGAL_AXIS_NAME          */
  public static final int ER_ILLEGAL_AXIS_NAME = 35;

  static
  {
    contents[ER_ILLEGAL_AXIS_NAME][1] = "\u4e0d\u5408\u898f\u5247\u7684\u8ef8\u540d\u7a31\uff1a{0}";
  }

  /** Field ER_UNKNOWN_NODETYPE          */
  public static final int ER_UNKNOWN_NODETYPE = 36;

  static
  {
    contents[ER_UNKNOWN_NODETYPE][1] = "\u672a\u77e5\u7bc0\u9ede\u985e\u578b\uff1a{0}";
  }

  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;

  static
  {
    contents[ER_PATTERN_LITERAL_NEEDS_BE_QUOTED][1] =
      "\u578b\u6a23\u6587\u5b57\u5217 ({0}) \u9700\u8981\u7528\u5f15\u865f\u62ec\u4f4f\uff01";
  }

  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;

  static
  {
    contents[ER_COULDNOT_BE_FORMATTED_TO_NUMBER][1] =
      "{0} \u7121\u6cd5\u683c\u5f0f\u5316\u70ba\u6578\u5b57\uff01";
  }

  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;

  static
  {
    contents[ER_COULDNOT_CREATE_XMLPROCESSORLIAISON][1] =
      "\u7121\u6cd5\u5efa\u7acb XML TransformerFactory Liaison\uff1a{0}";
  }

  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;

  static
  {
    contents[ER_DIDNOT_FIND_XPATH_SELECT_EXP][1] =
      "\u932f\u8aa4\uff01\u672a\u627e\u5230 xpath select \u8868\u793a\u5f0f (-select)\u3002";
  }

  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;

  static
  {
    contents[ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH][1] =
      "\u932f\u8aa4\uff01\u5728 OP_LOCATIONPATH \u4e4b\u5f8c\u627e\u4e0d\u5230 ENDOP";
  }

  /** Field ER_ERROR_OCCURED          */
  public static final int ER_ERROR_OCCURED = 42;

  static
  {
    contents[ER_ERROR_OCCURED][1] = "\u767c\u751f\u932f\u8aa4\uff01";
  }

  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;

  static
  {
    contents[ER_ILLEGAL_VARIABLE_REFERENCE][1] =
      "\u63d0\u4f9b\u7d66\u8b8a\u6578\u7684 VariableReference \u8d85\u51fa\u4e0a\u4e0b\u6587\u6216\u6c92\u6709\u5b9a\u7fa9\uff01\u540d\u7a31 = {0}";
  }

  /** Field ER_AXES_NOT_ALLOWED          */
  public static final int ER_AXES_NOT_ALLOWED = 44;

  static
  {
    contents[ER_AXES_NOT_ALLOWED][1] =
      "\u5728\u7b26\u5408\u578b\u6a23\u4e2d\u50c5\u5141\u8a31 child:: \u53ca attribute:: \u8ef8\uff01 \u9055\u4f8b\u8ef8 = {0}";
  }

  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;

  static
  {
    contents[ER_KEY_HAS_TOO_MANY_ARGS][1] =
      "key() \u542b\u6709\u4e0d\u6b63\u78ba\u7684\u5f15\u6578\u6578\u76ee\u3002";
  }

  /** Field ER_COUNT_TAKES_1_ARG          */
  public static final int ER_COUNT_TAKES_1_ARG = 46;

  static
  {
    contents[ER_COUNT_TAKES_1_ARG][1] =
      "count \u51fd\u5f0f\u53ea\u63a5\u53d7\u4e00\u500b\u5f15\u6578\uff01";
  }

  /** Field ER_COULDNOT_FIND_FUNCTION          */
  public static final int ER_COULDNOT_FIND_FUNCTION = 47;

  static
  {
    contents[ER_COULDNOT_FIND_FUNCTION][1] = "\u627e\u4e0d\u5230\u51fd\u5f0f\uff1a{0}";
  }

  /** Field ER_UNSUPPORTED_ENCODING          */
  public static final int ER_UNSUPPORTED_ENCODING = 48;

  static
  {
    contents[ER_UNSUPPORTED_ENCODING][1] = "\u672a\u652f\u63f4\u7684\u7de8\u78bc\uff1a{0}";
  }

  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;

  static
  {
    contents[ER_PROBLEM_IN_DTM_NEXTSIBLING][1] =
      "getNextSibling \u4e2d\u7684 DTM \u767c\u751f\u554f\u984c... \u5617\u8a66\u56de\u5fa9";
  }

  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;

  static
  {
    contents[ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL][1] =
      "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u932f\u8aa4\uff1a\u7121\u6cd5\u5beb\u5165 EmptyNodeList \u4e2d\u3002";
  }

  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;

  static
  {
    contents[ER_SETDOMFACTORY_NOT_SUPPORTED][1] =
      "setDOMFactory \u4e0d\u53d7 XPathContext \u652f\u63f4\uff01";
  }

  /** Field ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 52;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "\u524d\u7f6e\u5fc5\u9808\u89e3\u8b6f\u70ba\u540d\u7a31\u7a7a\u9593\uff1a{0}";
  }

  /** Field ER_PARSE_NOT_SUPPORTED          */
  public static final int ER_PARSE_NOT_SUPPORTED = 53;

  static
  {
    contents[ER_PARSE_NOT_SUPPORTED][1] =
      "\u5728 XPathContext \u4e2d\u4e0d\u652f\u63f4\u5256\u6790\uff08InputSource \u4f86\u6e90\uff09\uff01\u7121\u6cd5\u958b\u555f {0}";
  }

  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;

  static
  {
    contents[ER_CREATEDOCUMENT_NOT_SUPPORTED][1] =
      "createDocument() \u5728 XPathContext \u4e2d\u4e0d\u53d7\u652f\u63f4\uff01";
  }

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;

  static
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT][1] =
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01";
  }

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;

  static
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT][1] =
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\u5143\u7d20\uff01";
  }

  /** Field ER_SAX_API_NOT_HANDLED          */
  public static final int ER_SAX_API_NOT_HANDLED = 57;

  static
  {
    contents[ER_SAX_API_NOT_HANDLED][1] =
      "SAX API \u5b57\u5143(char ch[]... DTM \u672a\u8655\u7406\uff01";
  }

  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;

  static
  {
    contents[ER_IGNORABLE_WHITESPACE_NOT_HANDLED][1] =
      "ignorableWhitespace(char ch[]... DTM \u672a\u8655\u7406\uff01";
  }

  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;

  static
  {
    contents[ER_DTM_CANNOT_HANDLE_NODES][1] =
      "DTMLiaison \u7121\u6cd5\u8655\u7406\u985e\u578b {0} \u7684\u7bc0\u9ede";
  }

  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;

  static
  {
    contents[ER_XERCES_CANNOT_HANDLE_NODES][1] =
      "DOM2Helper \u7121\u6cd5\u8655\u7406\u985e\u578b {0} \u7684\u7bc0\u9ede";
  }

  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;

  static
  {
    contents[ER_XERCES_PARSE_ERROR_DETAILS][1] =
      "DOM2Helper.parse \u932f\u8aa4\uff1aSystemID - {0} \u884c - {1}";
  }

  /** Field ER_XERCES_PARSE_ERROR          */
  public static final int ER_XERCES_PARSE_ERROR = 62;

  static
  {
    contents[ER_XERCES_PARSE_ERROR][1] = "DOM2Helper.parse \u932f\u8aa4";
  }

  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;

  static
  {
    contents[ER_CANT_OUTPUT_TEXT_BEFORE_DOC][1] =
      "\u8b66\u544a\uff1a\u7121\u6cd5\u8f38\u51fa\u6587\u4ef6\u5143\u7d20\u4e4b\u524d\u7684\u6587\u5b57\uff01\u5ffd\u7565...";
  }

  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;

  static
  {
    contents[ER_CANT_HAVE_MORE_THAN_ONE_ROOT][1] =
      "\u4e00\u500b DOM \u53ea\u80fd\u6709\u4e00\u500b\u6839\uff01";
  }

  /** Field ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 65;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "\u5075\u6e2c\u5230\u7121\u6548\u7684 UTF-16 \u4ee3\u7528\u54c1\uff1a{0} ?";
  }

  /** Field ER_OIERROR          */
  public static final int ER_OIERROR = 66;

  static
  {
    contents[ER_OIERROR][1] = "\u8f38\u5165/\u8f38\u51fa (I/O) \u932f\u8aa4";
  }

  /** Field ER_CANNOT_CREATE_URL          */
  public static final int ER_CANNOT_CREATE_URL = 67;

  static
  {
    contents[ER_CANNOT_CREATE_URL][1] = "\u7121\u6cd5\u5efa\u7acb URL \u7d66\uff1a {0}";
  }

  /** Field ER_XPATH_READOBJECT          */
  public static final int ER_XPATH_READOBJECT = 68;

  static
  {
    contents[ER_XPATH_READOBJECT][1] = "\u5728 XPath.readObject\uff1a{0}";
  }
  
  /** Field ER_XPATH_READOBJECT         */
  public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;

  static
  {
    contents[ER_FUNCTION_TOKEN_NOT_FOUND][1] =
      "\u627e\u4e0d\u5230\u51fd\u5f0f\u8a18\u865f\u3002";
  }
  
   /**  Argument 'localName' is null  */
  public static final int ER_ARG_LOCALNAME_NULL = 70;

  static
  {
    contents[ER_ARG_LOCALNAME_NULL][1] =
       "\u5f15\u6578 'localName' \u70ba\u7a7a\u503c";
  }
  
   /**  Can not deal with XPath type:   */
  public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;

  static
  {
    contents[ER_CANNOT_DEAL_XPATH_TYPE][1] =
       "\u7121\u6cd5\u8655\u7406 XPath \u985e\u578b\uff1a{0}";
  }
  
   /**  This NodeSet is not mutable  */
  public static final int ER_NODESET_NOT_MUTABLE = 72;

  static
  {
    contents[ER_NODESET_NOT_MUTABLE][1] =
       "\u6b64\u985e NodeSet \u4e0d\u6613\u8b8a";
  }
  
   /**  This NodeSetDTM is not mutable  */
  public static final int ER_NODESETDTM_NOT_MUTABLE = 73;

  static
  {
    contents[ER_NODESETDTM_NOT_MUTABLE][1] =
       "\u6b64\u985e NodeSetDTM \u4e0d\u6613\u8b8a";
  }
  
   /**  Variable not resolvable:   */
  public static final int ER_VAR_NOT_RESOLVABLE = 74;

  static
  {
    contents[ER_VAR_NOT_RESOLVABLE][1] =
        "\u8b8a\u6578\u7121\u6cd5\u89e3\u8b6f\uff1a{0}";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 75;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "\u7a7a\u7684\u932f\u8aa4\u8655\u7406\u5668";
  }
  
   /**  Programmer's assertion: unknown opcode  */
  public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;

  static
  {
    contents[ER_PROG_ASSERT_UNKNOWN_OPCODE][1] =
       "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u7684\u5047\u8a2d\uff1a\u672a\u77e5\u4f5c\u696d\u78bc\uff1a{0}";
  }
  
   /**  0 or 1   */
  public static final int ER_ZERO_OR_ONE = 77;

  static
  {
    contents[ER_ZERO_OR_ONE][1] =
       "0 \u6216 1";
  }
  
   /**  2 or 3   */
  public static final int ER_TWO_OR_THREE = 78;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
       "0 \u6216 1";
  }
  
  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;

  static
  {
    contents[ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER][1] =
       "XRTreeFragSelectWrapper \u4e0d\u652f\u63f4 rtf()";
  }
  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;

  static
  {
    contents[ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER][1] =
       "XRTreeFragSelectWrapper \u4e0d\u652f\u63f4 asNodeIterator()";
  }
  
   /**  fsb() not supported for XStringForChars   */
  public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;

  static
  {
    contents[ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS][1] =
       "XStringForChars \u4e0d\u652f\u63f4 fsb()";
  }
  
   /**  Could not find variable with the name of   */
  public static final int ER_COULD_NOT_FIND_VAR = 81;

  static
  {
    contents[ER_COULD_NOT_FIND_VAR][1] =
      "\u627e\u4e0d\u5230\u540d\u7a31\u70ba {0} \u7684\u8b8a\u6578";
  }
  
   /**  XStringForChars can not take a string for an argument   */
  public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;

  static
  {
    contents[ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING][1] =
      "XStringForChars \u4e0d\u63a5\u53d7\u5b57\u4e32\u5f15\u6578";
  }
  
   /**  The FastStringBuffer argument can not be null   */
  public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;

  static
  {
    contents[ER_FASTSTRINGBUFFER_CANNOT_BE_NULL][1] =
      "FastStringBuffer \u5f15\u6578\u4e0d\u5f97\u70ba\u7a7a\u503c";
  }  
  


  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;

  static
  {
    contents[WG_LOCALE_NAME_NOT_HANDLED + MAX_CODE][1] =
      "format-number \u51fd\u5f0f\u4e2d\u7684\u8a9e\u8a00\u74b0\u5883\u540d\u7a31\u5c1a\u672a\u8655\u7406\uff01";
  }

  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  public static final int WG_PROPERTY_NOT_SUPPORTED = 2;

  static
  {
    contents[WG_PROPERTY_NOT_SUPPORTED + MAX_CODE][1] =
      "\u4e0d\u652f\u63f4 XSL \u5167\u5bb9\uff1a{0}";
  }

  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;

  static
  {
    contents[WG_DONT_DO_ANYTHING_WITH_NS + MAX_CODE][1] =
      "\u76ee\u524d\u8acb\u52ff\u8655\u7406\u5167\u5bb9 {1) \u4e2d\u7684\u540d\u7a31\u7a7a\u9593 {0}";
  }

  /** Field WG_SECURITY_EXCEPTION          */
  public static final int WG_SECURITY_EXCEPTION = 4;

  static
  {
    contents[WG_SECURITY_EXCEPTION + MAX_CODE][1] =
      "\u5617\u8a66\u5b58\u53d6 XSL \u7cfb\u7d71\u5167\u5bb9 {0} \u6642\u767c\u751f SecurityException";
  }

  /** Field WG_QUO_NO_LONGER_DEFINED          */
  public static final int WG_QUO_NO_LONGER_DEFINED = 5;

  static
  {
    contents[WG_QUO_NO_LONGER_DEFINED + MAX_CODE][1] =
      "\u820a\u8a9e\u6cd5\uff1aquo(...) \u4e0d\u518d\u5b9a\u7fa9\u65bc XPath \u4e2d\u3002";
  }

  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;

  static
  {
    contents[WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST + MAX_CODE][1] =
      "XPath \u9700\u8981\u884d\u751f\u7269\u4ef6\u4f86\u57f7\u884c nodeTest\uff01";
  }

  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;

  static
  {
    contents[WG_FUNCTION_TOKEN_NOT_FOUND + MAX_CODE][1] =
      "\u627e\u4e0d\u5230\u51fd\u5f0f\u8a18\u865f\u3002";
  }

  /** Field WG_COULDNOT_FIND_FUNCTION          */
  public static final int WG_COULDNOT_FIND_FUNCTION = 8;

  static
  {
    contents[WG_COULDNOT_FIND_FUNCTION + MAX_CODE][1] =
      "\u627e\u4e0d\u5230\u51fd\u5f0f\uff1a{0}";
  }

  /** Field WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 9;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "\u7121\u6cd5\u5f9e {0} \u7522\u751f URL";
  }

  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;

  static
  {
    contents[WG_EXPAND_ENTITIES_NOT_SUPPORTED + MAX_CODE][1] =
      "DTM \u5256\u6790\u5668\u4e0d\u652f\u63f4 -E \u9078\u9805";
  }

  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;

  static
  {
    contents[WG_ILLEGAL_VARIABLE_REFERENCE + MAX_CODE][1] =
      "\u63d0\u4f9b\u7d66\u8b8a\u6578\u7684 VariableReference \u8d85\u51fa\u4e0a\u4e0b\u6587\u6216\u6c92\u6709\u5b9a\u7fa9\uff01\u540d\u7a31 = {0}";
  }

  /** Field WG_UNSUPPORTED_ENCODING          */
  public static final int WG_UNSUPPORTED_ENCODING = 12;

  static
  {
    contents[ER_UNSUPPORTED_ENCODING][1] = "\u672a\u652f\u63f4\u7684\u7de8\u78bc\uff1a{0}";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "zh_TW";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "zh_TW";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "zh_TW";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "createMessage \u7684\u53c3\u6578\u8d85\u51fa\u754c\u9650";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "\u5728 messageFormat \u547c\u53eb\u671f\u9593\u7570\u5e38\u4e1f\u51fa";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan \u7248\u672c";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "\u662f";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "\u884c #";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "\u76f4\u6b04 #";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: done";
    contents[MAX_MESSAGES + 11][0] = "xpath_option";
    contents[MAX_MESSAGES + 11][1] = "xpath \u9078\u9805\uff1a ";
    contents[MAX_MESSAGES + 12][0] = "optionIN";
    contents[MAX_MESSAGES + 12][1] = "   [-in inputXMLURL]";
    contents[MAX_MESSAGES + 13][0] = "optionSelect";
    contents[MAX_MESSAGES + 13][1] = "   [-select xpath expression]";
    contents[MAX_MESSAGES + 14][0] = "optionMatch";
    contents[MAX_MESSAGES + 14][1] =
      "   [-match match pattern (\u7528\u65bc\u7b26\u5408\u8a3a\u65b7)]";
    contents[MAX_MESSAGES + 15][0] = "optionAnyExpr";
    contents[MAX_MESSAGES + 15][1] =
      "\u6216\u53ea\u6709\u4e00\u500b xpath \u8868\u793a\u5f0f\u6703\u57f7\u884c\u8a3a\u65b7\u50be\u5370";
    contents[MAX_MESSAGES + 16][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 16][1] = "XSL \u7a0b\u5e8f\u4e0d\u6210\u529f\u3002";
    contents[MAX_MESSAGES + 17][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 17][1] = "** \u627e\u4e0d\u5230\u5256\u6790\u5668 **";
    contents[MAX_MESSAGES + 18][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 18][1] = "\u8acb\u6aa2\u67e5\u985e\u5225\u8def\u5f91\u3002";
    contents[MAX_MESSAGES + 19][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 19][1] =
      "\u5982\u679c\u60a8\u6c92\u6709 IBM \u7684 XML Parser for Java\uff0c\u53ef\u4e0b\u8f09\u81ea ";
    contents[MAX_MESSAGES + 20][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 20][1] =
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
  }

  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE          */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED          */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** Field ERROR_RESOURCES          */
  public static final String ERROR_RESOURCES =
    "org.apache.xpath.res.XPATHErrorResources";

  /** Field ERROR_STRING          */
  public static final String ERROR_STRING = "#error";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "\u932f\u8aa4\uff1a";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "\u8b66\u544a\uff1a";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "PATTERN ";

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }
}

