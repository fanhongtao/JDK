/*
 * @(#)XPATHErrorResources_zh_TW.java	1.3 03/04/27
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
 * originally based on software copyright (c) 2002, Sun Microsystems,
 * Inc., http://www.sun.com.  For more
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
 * you need to first add a Static string constant for the
 * Key and update the contents array with Key, Value pair
  * Also you need to  update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING) [ Information purpose only]
 */
public class XPATHErrorResources_zh_TW extends XPATHErrorResources
{

  
  /** Field MAX_CODE          */
  public static final int MAX_CODE = 116;  // this is needed to keep track of the number of messages          

  /** Field MAX_WARNING          */
  public static final int MAX_WARNING = 11;  // this is needed to keep track of the number of warnings

  /** Field MAX_OTHERS          */
  public static final int MAX_OTHERS = 20;

  /** Field MAX_MESSAGES          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


  // Error messages...
  public static final Object[][] contents = {

  /** Field ERROR0000          */
  //public static final int ERROR0000 = 0;


  {
    "ERROR0000", "{0}"},


  /** Field ER_CURRENT_NOT_ALLOWED_IN_MATCH          */
  //public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;


  {
    ER_CURRENT_NOT_ALLOWED_IN_MATCH,
      "\u5728\u7b26\u5408\u578b\u6a23\u4e2d\u4e0d\u5141\u8a31\u4f7f\u7528 current() \u51fd\u5f0f\uff01"},


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  {
    ER_CURRENT_TAKES_NO_ARGS,
      "current() \u51fd\u5f0f\u4e0d\u63a5\u53d7\u5f15\u6578\uff01"},


  /** Field ER_DOCUMENT_REPLACED          */
  //public static final int ER_DOCUMENT_REPLACED = 3;


  {
    ER_DOCUMENT_REPLACED,
      "org.apache.xalan.xslt.FuncDocument \u5df2\u53d6\u4ee3\u57f7\u884c document() \u51fd\u5f0f\uff01"},


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  {
    ER_CONTEXT_HAS_NO_OWNERDOC,
      "\u4e0a\u4e0b\u6587\u4e0d\u542b\u64c1\u6709\u8005\u6587\u4ef6\uff01"},


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  {
    ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "local-name() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  {
    ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "namespace-uri() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  {
    ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "normalize-space() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  {
    ER_NUMBER_HAS_TOO_MANY_ARGS,
      "number() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  {
    ER_NAME_HAS_TOO_MANY_ARGS, "name() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  {
    ER_STRING_HAS_TOO_MANY_ARGS,
      "string() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  {
    ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "string-length() \u6709\u592a\u591a\u5f15\u6578\u3002"},


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  {
    ER_TRANSLATE_TAKES_3_ARGS,
      "translate() \u51fd\u5f0f\u9700\u8981 3 \u500b\u5f15\u6578\uff01"},


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  {
    ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "unparsed-entity-uri \u51fd\u5f0f\u9700\u8981 1 \u500b\u5f15\u6578\uff01"},


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  {
    ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "namespace \u8ef8\u5c1a\u672a\u5efa\u7f6e\uff01"},


  /** Field ER_UNKNOWN_AXIS          */
  //public static final int ER_UNKNOWN_AXIS = 15;


  {
    ER_UNKNOWN_AXIS, "\u672a\u77e5\u8ef8\uff1a{0}"},


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  {
    ER_UNKNOWN_MATCH_OPERATION, "\u672a\u77e5\u7684\u7b26\u5408\u4f5c\u696d\uff01"},


  /** Field ER_INCORRECT_ARG_LENGTH          */
  //public static final int ER_INCORRECT_ARG_LENGTH = 17;


  {
    ER_INCORRECT_ARG_LENGTH,
      "processing-instruction() \u7bc0\u9ede\u6e2c\u8a66\u7684\u5f15\u6578\u9577\u5ea6\u4e0d\u6b63\u78ba\uff01"},


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  {
    ER_CANT_CONVERT_TO_NUMBER,
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba\u6578\u5b57"},


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  {
    ER_CANT_CONVERT_TO_NODELIST,
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba NodeList\uff01"},


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  {
    ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba NodeSetDTM\uff01"},


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  {
    ER_CANT_CONVERT_TO_TYPE,
      "{0} \u7121\u6cd5\u8f49\u63db\u70ba type//{1}"},


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  {
    ER_EXPECTED_MATCH_PATTERN,
      "\u5728 getMatchScore \u4e2d\u9810\u671f\u7684\u7b26\u5408\u578b\u6a23\uff01"},


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  {
    ER_COULDNOT_GET_VAR_NAMED,
      "\u7121\u6cd5\u53d6\u5f97\u53eb\u4f5c {0} \u7684\u8b8a\u6578"},


  /** Field ER_UNKNOWN_OPCODE          */
  //public static final int ER_UNKNOWN_OPCODE = 24;


  {
    ER_UNKNOWN_OPCODE, "\u932f\u8aa4\uff01\u672a\u77e5\u7684\u4f5c\u696d\u78bc\uff1a{0}"},


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  {
    ER_EXTRA_ILLEGAL_TOKENS, "\u984d\u5916\u4e0d\u5408\u898f\u5247\u7684\u8a18\u865f\uff1a{0}"},


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  {
    ER_EXPECTED_DOUBLE_QUOTE,
      "\u6587\u5b57\u5217\u5f15\u865f\u62ec\u932f... \u9810\u671f\u51fa\u73fe\u96d9\u5f15\u865f\uff01"},


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  {
    ER_EXPECTED_SINGLE_QUOTE,
      "\u6587\u5b57\u5217\u5f15\u865f\u62ec\u932f... \u9810\u671f\u51fa\u73fe\u55ae\u5f15\u865f\uff01"},


  /** Field ER_EMPTY_EXPRESSION          */
  //public static final int ER_EMPTY_EXPRESSION = 28;


  {
    ER_EMPTY_EXPRESSION, "\u7a7a\u7684\u8868\u793a\u5f0f\uff01"},


  /** Field ER_EXPECTED_BUT_FOUND          */
  //public static final int ER_EXPECTED_BUT_FOUND = 29;


  {
    ER_EXPECTED_BUT_FOUND, "\u9810\u671f {0}\uff0c\u537b\u627e\u5230\uff1a{1}"},


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  {
    ER_INCORRECT_PROGRAMMER_ASSERTION,
      "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u5047\u8a2d\u4e0d\u6b63\u78ba\uff01- {0}"},


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  {
    ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "boolean(...) \u5f15\u6578\u5728 19990709 XPath \u521d\u7a3f\u4e2d\u4e0d\u518d\u662f\u53ef\u9078\u7528\u7684\u3002"},


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  {
    ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "\u627e\u5230 ','\uff0c\u4f46\u4e4b\u524d\u6c92\u6709\u5f15\u6578\uff01"},


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  {
    ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "\u627e\u5230 ','\uff0c\u4f46\u4e4b\u5f8c\u6c92\u6709\u5f15\u6578\uff01"},


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  {
    ER_PREDICATE_ILLEGAL_SYNTAX,
      "'..[predicate]' \u6216 '.[predicate]' \u662f\u4e0d\u5408\u898f\u5247\u7684\u8a9e\u6cd5\u3002\u8acb\u4f7f\u7528 'self::node()[predicate]' \u4f86\u4ee3\u66ff\u3002"},


  /** Field ER_ILLEGAL_AXIS_NAME          */
  //public static final int ER_ILLEGAL_AXIS_NAME = 35;


  {
    ER_ILLEGAL_AXIS_NAME, "\u4e0d\u5408\u898f\u5247\u7684\u8ef8\u540d\u7a31\uff1a{0}"},


  /** Field ER_UNKNOWN_NODETYPE          */
  //public static final int ER_UNKNOWN_NODETYPE = 36;


  {
    ER_UNKNOWN_NODETYPE, "\u672a\u77e5\u7bc0\u9ede\u985e\u578b\uff1a{0}"},


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  {
    ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "\u578b\u6a23\u6587\u5b57\u5217 ({0}) \u9700\u8981\u7528\u5f15\u865f\u62ec\u4f4f\uff01"},


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  {
    ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "{0} \u7121\u6cd5\u683c\u5f0f\u5316\u70ba\u6578\u5b57\uff01"},


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  {
    ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "\u7121\u6cd5\u5efa\u7acb XML TransformerFactory Liaison\uff1a{0}"},


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  {
    ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "\u932f\u8aa4\uff01\u672a\u627e\u5230 xpath select \u8868\u793a\u5f0f (-select)\u3002"},


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  {
    ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "\u932f\u8aa4\uff01\u5728 OP_LOCATIONPATH \u4e4b\u5f8c\u627e\u4e0d\u5230 ENDOP"},


  /** Field ER_ERROR_OCCURED          */
  //public static final int ER_ERROR_OCCURED = 42;


  {
    ER_ERROR_OCCURED, "\u767c\u751f\u932f\u8aa4\uff01"},


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  {
    ER_ILLEGAL_VARIABLE_REFERENCE,
      "\u63d0\u4f9b\u7d66\u8b8a\u6578\u7684 VariableReference \u8d85\u51fa\u4e0a\u4e0b\u6587\u6216\u6c92\u6709\u5b9a\u7fa9\uff01\u540d\u7a31 = {0}"},


  /** Field ER_AXES_NOT_ALLOWED          */
  //public static final int ER_AXES_NOT_ALLOWED = 44;


  {
    ER_AXES_NOT_ALLOWED,
      "\u5728\u7b26\u5408\u578b\u6a23\u4e2d\u50c5\u5141\u8a31 child:: \u53ca attribute:: \u8ef8\uff01 \u9055\u4f8b\u8ef8 = {0}"},


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  {
    ER_KEY_HAS_TOO_MANY_ARGS,
      "key() \u542b\u6709\u4e0d\u6b63\u78ba\u7684\u5f15\u6578\u6578\u76ee\u3002"},


  /** Field ER_COUNT_TAKES_1_ARG          */
  //public static final int ER_COUNT_TAKES_1_ARG = 46;


  {
    ER_COUNT_TAKES_1_ARG,
      "count \u51fd\u5f0f\u53ea\u63a5\u53d7\u4e00\u500b\u5f15\u6578\uff01"},


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  {
    ER_COULDNOT_FIND_FUNCTION, "\u627e\u4e0d\u5230\u51fd\u5f0f\uff1a{0}"},


  /** Field ER_UNSUPPORTED_ENCODING          */
  //public static final int ER_UNSUPPORTED_ENCODING = 48;


  {
    ER_UNSUPPORTED_ENCODING, "\u672a\u652f\u63f4\u7684\u7de8\u78bc\uff1a{0}"},


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  {
    ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "getNextSibling \u4e2d\u7684 DTM \u767c\u751f\u554f\u984c... \u5617\u8a66\u56de\u5fa9"},


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  {
    ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u932f\u8aa4\uff1a\u7121\u6cd5\u5beb\u5165 EmptyNodeList \u4e2d\u3002"},


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  {
    ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory \u4e0d\u53d7 XPathContext \u652f\u63f4\uff01"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u524d\u7f6e\u5fc5\u9808\u89e3\u8b6f\u70ba\u540d\u7a31\u7a7a\u9593\uff1a{0}"},


  /** Field ER_PARSE_NOT_SUPPORTED          */
  //public static final int ER_PARSE_NOT_SUPPORTED = 53;


  {
    ER_PARSE_NOT_SUPPORTED,
      "\u5728 XPathContext \u4e2d\u4e0d\u652f\u63f4\u5256\u6790\uff08InputSource \u4f86\u6e90\uff09\uff01\u7121\u6cd5\u958b\u555f {0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() \u5728 XPathContext \u4e2d\u4e0d\u53d7\u652f\u63f4\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\u5143\u7d20\uff01"},


  /** Field ER_SAX_API_NOT_HANDLED          */
  //public static final int ER_SAX_API_NOT_HANDLED = 57;


  {
    ER_SAX_API_NOT_HANDLED,
      "SAX API \u5b57\u5143(char ch[]... DTM \u672a\u8655\u7406\uff01"},


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  {
    ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... DTM \u672a\u8655\u7406\uff01"},


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  {
    ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison \u7121\u6cd5\u8655\u7406\u985e\u578b {0} \u7684\u7bc0\u9ede"},


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  {
    ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper \u7121\u6cd5\u8655\u7406\u985e\u578b {0} \u7684\u7bc0\u9ede"},


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  {
    ER_XERCES_PARSE_ERROR_DETAILS,
      "DOM2Helper.parse \u932f\u8aa4\uff1aSystemID - {0} \u884c - {1}"},


  /** Field ER_XERCES_PARSE_ERROR          */
  //public static final int ER_XERCES_PARSE_ERROR = 62;


  {
    ER_XERCES_PARSE_ERROR, "DOM2Helper.parse \u932f\u8aa4"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u8b66\u544a\uff1a\u7121\u6cd5\u8f38\u51fa\u6587\u4ef6\u5143\u7d20\u4e4b\u524d\u7684\u6587\u5b57\uff01\u5ffd\u7565..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "\u4e00\u500b DOM \u53ea\u80fd\u6709\u4e00\u500b\u6839\uff01"},


  /** Field ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 65;


  {
    ER_INVALID_UTF16_SURROGATE,
      "\u5075\u6e2c\u5230\u7121\u6548\u7684 UTF-16 \u4ee3\u7528\u54c1\uff1a{0} ?"},


  /** Field ER_OIERROR          */
  //public static final int ER_OIERROR = 66;


  {
    ER_OIERROR, "\u8f38\u5165/\u8f38\u51fa (I/O) \u932f\u8aa4"},


  /** Field ER_CANNOT_CREATE_URL          */
  //public static final int ER_CANNOT_CREATE_URL = 67;


  {
    ER_CANNOT_CREATE_URL, "\u7121\u6cd5\u5efa\u7acb URL \u7d66\uff1a {0}"},


  /** Field ER_XPATH_READOBJECT          */
  //public static final int ER_XPATH_READOBJECT = 68;


  {
    ER_XPATH_READOBJECT, "\u5728 XPath.readObject\uff1a{0}"},

  
  /** Field ER_XPATH_READOBJECT         */
  //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


  {
    ER_FUNCTION_TOKEN_NOT_FOUND,
      "\u627e\u4e0d\u5230\u51fd\u5f0f\u8a18\u865f\u3002"},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "\u5f15\u6578 'localName' \u70ba\u7a7a\u503c"},

  
   /**  Can not deal with XPath type:   */
  //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


  {
    ER_CANNOT_DEAL_XPATH_TYPE,
       "\u7121\u6cd5\u8655\u7406 XPath \u985e\u578b\uff1a{0}"},

  
   /**  This NodeSet is not mutable  */
  //public static final int ER_NODESET_NOT_MUTABLE = 72;


  {
    ER_NODESET_NOT_MUTABLE,
       "\u6b64\u985e NodeSet \u4e0d\u6613\u8b8a"},

  
   /**  This NodeSetDTM is not mutable  */
  //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


  {
    ER_NODESETDTM_NOT_MUTABLE,
       "\u6b64\u985e NodeSetDTM \u4e0d\u6613\u8b8a"},

  
   /**  Variable not resolvable:   */
  //public static final int ER_VAR_NOT_RESOLVABLE = 74;


  {
    ER_VAR_NOT_RESOLVABLE,
        "\u8b8a\u6578\u7121\u6cd5\u89e3\u8b6f\uff1a{0}"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 75;


  {
    ER_NULL_ERROR_HANDLER,
        "\u7a7a\u7684\u932f\u8aa4\u8655\u7406\u5668"},

  
   /**  Programmer's assertion: unknown opcode  */
  //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


  {
    ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u7684\u5047\u8a2d\uff1a\u672a\u77e5\u4f5c\u696d\u78bc\uff1a{0}"},

  
   /**  0 or 1   */
  //public static final int ER_ZERO_OR_ONE = 77;


  {
    ER_ZERO_OR_ONE,
       "0 \u6216 1"},

  
  
     /**  rtf() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


  {
    ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "XRTreeFragSelectWrapper \u4e0d\u652f\u63f4 rtf()"},

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


  {
    ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "XRTreeFragSelectWrapper \u4e0d\u652f\u63f4 asNodeIterator()"},

  
   /**  fsb() not supported for XStringForChars   */
  //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


  {
    ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "XStringForChars \u4e0d\u652f\u63f4 fsb()"},

  
   /**  Could not find variable with the name of   */
  //public static final int ER_COULD_NOT_FIND_VAR = 81;


  {
    ER_COULD_NOT_FIND_VAR,
      "\u627e\u4e0d\u5230\u540d\u7a31\u70ba {0} \u7684\u8b8a\u6578"},

  
   /**  XStringForChars can not take a string for an argument   */
  //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


  {
    ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "XStringForChars \u4e0d\u63a5\u53d7\u5b57\u4e32\u5f15\u6578"},

  
   /**  The FastStringBuffer argument can not be null   */
  //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


  {
    ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "FastStringBuffer \u5f15\u6578\u4e0d\u5f97\u70ba\u7a7a\u503c"},
    
    /**  2 or 3   */
  //public static final int ER_TWO_OR_THREE = 84;
  

  {
    ER_TWO_OR_THREE,
       "2 \u6216 3"},

 
   /** Variable accessed before it is bound! */
  //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;
 

  {
    ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "\u9023\u7d50\u524d\u5df2\u5b58\u53d6\u8b8a\u6578\uff01"},

 
   /** XStringForFSB can not take a string for an argument! */
  //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;
 

  {
    ER_FSB_CANNOT_TAKE_STRING,
       "XStringForFSB \u4e0d\u6703\u5c07\u5b57\u4e32\u770b\u4f5c\u5f15\u6578\uff01"},

 
   /** Error! Setting the root of a walker to null! */
  //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;
 

  {
    ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n \uff01\uff01\uff01\uff01\u932f\u8aa4\uff01\u5c07 walker \u7684\u6839\u8a2d\u5b9a\u70ba\u7a7a\uff01\uff01\uff01"},

 
   /** This NodeSetDTM can not iterate to a previous node! */
  //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;
 

  {
    ER_NODESETDTM_CANNOT_ITERATE,
       "\u7121\u6cd5\u5c0d\u524d\u4e00\u500b\u7bc0\u9ede\u91cd\u8907\u6b64 NodeSetDTM\uff01"},

 
  /** This NodeSet can not iterate to a previous node! */
  //public static final int ER_NODESET_CANNOT_ITERATE = 89;
 

  {
    ER_NODESET_CANNOT_ITERATE,
       "\u7121\u6cd5\u5c0d\u524d\u4e00\u500b\u7bc0\u9ede\u91cd\u8907\u6b64 NodeSet\uff01"},

 
  /** This NodeSetDTM can not do indexing or counting functions! */
  //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;
 

  {
    ER_NODESETDTM_CANNOT_INDEX,
       "\u6b64 NodeSetDTM \u7121\u6cd5\u5c0d\u51fd\u5f0f\u9032\u884c\u7d22\u5f15\u6216\u8a08\u6578\uff01"},

 
  /** This NodeSet can not do indexing or counting functions! */
  //public static final int ER_NODESET_CANNOT_INDEX = 91;
 

  {
    ER_NODESET_CANNOT_INDEX,
       "\u6b64 NodeSet \u7121\u6cd5\u5c0d\u51fd\u5f0f\u9032\u884c\u7d22\u5f15\u6216\u8a08\u6578\uff01"},

 
  /** Can not call setShouldCacheNodes after nextNode has been called! */
  //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;
 

  {
    ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "\u547c\u53eb nextNode \u4e4b\u5f8c\uff0c\u4e0d\u80fd\u547c\u53eb setShouldCacheNodes\uff01"},

 
  /** {0} only allows {1} arguments */
  //public static final int ER_ONLY_ALLOWS = 93;
 

  {
    ER_ONLY_ALLOWS,
       "{0} \u50c5\u5141\u8a31\u4f7f\u7528 {1} \u5f15\u6578"},

 
  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  //public static final int ER_UNKNOWN_STEP = 94;
 

  {
    ER_UNKNOWN_STEP,
       "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u5728 getNextStepPos \u4e2d\u7684\u5224\u65b7\uff1a\u672a\u77e5\u7684 stepType\uff1a{0}"},

 
  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.
 
  /** Problem with RelativeLocationPath */
  //public static final int ER_EXPECTED_REL_LOC_PATH = 95;
 

  {
    ER_EXPECTED_REL_LOC_PATH,
       "'/' \u6216 '//' \u8a18\u865f\u4e4b\u5f8c\u61c9\u8ddf\u96a8\u76f8\u5c0d\u4f4d\u7f6e\u7684\u8def\u5f91\u3002"},

 
  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.
 
  /** Problem with LocationPath */
  //public static final int ER_EXPECTED_LOC_PATH = 96;
 

  {
    ER_EXPECTED_LOC_PATH,
       "\u61c9\u70ba\u4f4d\u7f6e\u8def\u5f91\uff0c\u537b\u9047\u5230\u4ee5\u4e0b\u8a18\u865f :  {0}"},

 
  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.
 
  /** Problem with Step */
  //public static final int ER_EXPECTED_LOC_STEP = 97;
 

  {
    ER_EXPECTED_LOC_STEP,
       "'/' \u6216 '//' \u8a18\u865f\u4e4b\u5f8c\u61c9\u8ddf\u96a8\u4f4d\u7f6e\u6b65\u9a5f\u3002"},

 
  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.
 
  /** Problem with NodeTest */
  //public static final int ER_EXPECTED_NODE_TEST = 98;
 

  {
    ER_EXPECTED_NODE_TEST,
       "\u61c9\u70ba\u7b26\u5408 NCName:* \u6216 QName \u7684\u7bc0\u9ede\u6e2c\u8a66\u3002"},

 
  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.
 
  /** Expected step pattern */
  //public static final int ER_EXPECTED_STEP_PATTERN = 99;
 

  {
    ER_EXPECTED_STEP_PATTERN,
       "\u61c9\u70ba\u6b65\u9a5f\u578b\u6a23\uff0c\u537b\u9047\u5230 '/'\u3002"},

  
  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
 
  /** Expected relative path pattern */
  //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;
 

  {
    ER_EXPECTED_REL_PATH_PATTERN,
       "\u61c9\u70ba\u76f8\u5c0d\u8def\u5f91\u578b\u6a23\u3002"},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;
 

  {
    ER_ARG_LOCALNAME_INVALID,
       "QNAME \u4e2d\u7684 Localname \u61c9\u70ba\u6709\u6548\u7684 NCName"},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.
 
  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;
 

  {
    ER_ARG_PREFIX_INVALID,
       "QNAME \u4e2d\u7684\u524d\u7f6e\u61c9\u70ba\u6709\u6548\u7684 NCName"},

  
   // Note to translators:  The substitution text is the name of a data type.  The
   // message indicates that a value of a particular type could not be converted
   // to a value of type string.
 
  /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
  //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;
 

  {
    ER_CANT_CONVERT_TO_BOOLEAN,
       "{0} \u7121\u6cd5\u8f49\u63db\u70ba\u5e03\u6797\u503c\u3002"},

 
  // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
  // FIRST_ORDERED_NODE_TYPE.
 
  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;
 

  {
    ER_CANT_CONVERT_TO_SINGLENODE,
       "{0} \u7121\u6cd5\u8f49\u63db\u70ba\u55ae\u4e00\u7bc0\u9ede\u3002\u6b64 getter \u9069\u7528\u65bc ANY_UNORDERED_NODE_TYPE \u548c FIRST_ORDERED_NODE_TYPE \u985e\u578b\u3002"},

 
  // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.
 
  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;
 

  {
    ER_CANT_GET_SNAPSHOT_LENGTH,
       "\u7121\u6cd5\u53d6\u5f97\u985e\u578b {0} \u7684\u5feb\u7167\u9577\u5ea6\u3002\u6b64 getter \u9069\u7528\u65bc UNORDERED_NODE_SNAPSHOT_TYPE \u548c ORDERED_NODE_SNAPSHOT_TYPE \u985e\u578b\u3002"},

 
  /** Field ER_NON_ITERATOR_TYPE                */
  //public static final int ER_NON_ITERATOR_TYPE        = 106;
 

  {
    ER_NON_ITERATOR_TYPE,
       "\u7121\u6cd5\u91cd\u8907\u975e\u758a\u4ee3\u5668\u985e\u578b\uff1a{0}"},

 
  // Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.
 
  /** Field ER_DOC_MUTATED                      */
  //public static final int ER_DOC_MUTATED              = 107;
 

  {
    ER_DOC_MUTATED,
       "\u50b3\u56de\u7d50\u679c\u5f8c\u6587\u4ef6\u767c\u751f\u8b8a\u66f4\u3002\u758a\u4ee3\u5668\u7121\u6548\u3002"},

 
  /** Field ER_INVALID_XPATH_TYPE               */
  //public static final int ER_INVALID_XPATH_TYPE       = 108;
 

  {
    ER_INVALID_XPATH_TYPE,
       "\u7121\u6548\u7684 XPath \u985e\u578b\u5f15\u6578\uff1a{0}"},

 
  /** Field ER_EMPTY_XPATH_RESULT                */
  //public static final int ER_EMPTY_XPATH_RESULT       = 109;
 

  {
    ER_EMPTY_XPATH_RESULT,
       "\u7a7a\u7684 XPath \u7d50\u679c\u7269\u4ef6"},

 
  /** Field ER_INCOMPATIBLE_TYPES                */
  //public static final int ER_INCOMPATIBLE_TYPES       = 110;
 

  {
    ER_INCOMPATIBLE_TYPES,
       "\u50b3\u56de\u7684\u985e\u578b\uff1a{0} \u7121\u6cd5\u5f37\u884c\u8f49\u63db\u70ba\u6307\u5b9a\u7684\u985e\u578b\uff1a{1}"},

 
  /** Field ER_NULL_RESOLVER                     */
  //public static final int ER_NULL_RESOLVER            = 111;
 

  {
    ER_NULL_RESOLVER,
       "\u7121\u6cd5\u4f7f\u7528\u7a7a\u524d\u7f6e\u89e3\u6790\u5668\u89e3\u6790\u524d\u7f6e\u3002"},

 
  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.
 
  /** Field ER_CANT_CONVERT_TO_STRING            */
  //public static final int ER_CANT_CONVERT_TO_STRING   = 112;
 

  {
    ER_CANT_CONVERT_TO_STRING,
       "{0} \u7121\u6cd5\u8f49\u63db\u70ba\u5b57\u4e32\u3002"},

 
  // Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.
 
  /** Field ER_NON_SNAPSHOT_TYPE                 */
  //public static final int ER_NON_SNAPSHOT_TYPE       = 113;
 

  {
    ER_NON_SNAPSHOT_TYPE,
       "\u7121\u6cd5\u547c\u53eb\u985e\u578b {0} \u7684 snapshotItem\u3002 \u6b64\u65b9\u6cd5\u9069\u7528\u65bc UNORDERED_NODE_SNAPSHOT_TYPE \u548c ORDERED_NODE_SNAPSHOT_TYPE \u985e\u578b\u3002"},

 
  // Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.
 
  /** Field ER_WRONG_DOCUMENT                    */
  //public static final int ER_WRONG_DOCUMENT          = 114;
 
  {
    ER_WRONG_DOCUMENT,
       "\u4e0a\u4e0b\u6587\u7bc0\u9ede\u4e0d\u5c6c\u65bc\u9023\u7d50\u81f3\u6b64 XPathEvaluator \u7684\u6587\u4ef6\u3002"},

 
  // Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  /** Field ER_WRONG_NODETYPE                    */
  //public static final int ER_WRONG_NODETYPE          = 115;
 

  {
    ER_WRONG_NODETYPE ,
       "\u4e0d\u652f\u63f4\u4e0a\u4e0b\u6587\u7bc0\u9ede\u3002"},

 
  /** Field ER_XPATH_ERROR                       */
  //public static final int ER_XPATH_ERROR             = 116;
 

  {
    ER_XPATH_ERROR ,
       "XPath \u4e2d\u51fa\u73fe\u672a\u77e5\u932f\u8aa4\u3002"},

 

  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  {
    WG_LOCALE_NAME_NOT_HANDLED,
      "format-number \u51fd\u5f0f\u4e2d\u7684\u8a9e\u8a00\u74b0\u5883\u540d\u7a31\u5c1a\u672a\u8655\u7406\uff01"},


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  {
    WG_PROPERTY_NOT_SUPPORTED,
      "\u4e0d\u652f\u63f4 XSL \u5167\u5bb9\uff1a{0}"},


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  {
    WG_DONT_DO_ANYTHING_WITH_NS,
      "\u76ee\u524d\u8acb\u52ff\u8655\u7406\u5167\u5bb9 {1} \u4e2d\u7684\u540d\u7a31\u7a7a\u9593 {0}"},


  /** Field WG_SECURITY_EXCEPTION          */
  //public static final int WG_SECURITY_EXCEPTION = 4;


  {
    WG_SECURITY_EXCEPTION,
      "\u5617\u8a66\u5b58\u53d6 XSL \u7cfb\u7d71\u5167\u5bb9 {0} \u6642\u767c\u751f SecurityException"},


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  {
    WG_QUO_NO_LONGER_DEFINED,
      "\u820a\u8a9e\u6cd5\uff1aquo(...) \u4e0d\u518d\u5b9a\u7fa9\u65bc XPath \u4e2d\u3002"},


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  {
    WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "XPath \u9700\u8981\u884d\u751f\u7269\u4ef6\u4f86\u57f7\u884c nodeTest\uff01"},


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  {
    WG_FUNCTION_TOKEN_NOT_FOUND,
      "\u627e\u4e0d\u5230\u51fd\u5f0f\u8a18\u865f\u3002"},


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  {
    WG_COULDNOT_FIND_FUNCTION,
      "\u627e\u4e0d\u5230\u51fd\u5f0f\uff1a{0}"},


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "\u7121\u6cd5\u5f9e {0} \u7522\u751f URL"},


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  {
    WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "DTM \u5256\u6790\u5668\u4e0d\u652f\u63f4 -E \u9078\u9805"},


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  {
    WG_ILLEGAL_VARIABLE_REFERENCE,
      "\u63d0\u4f9b\u7d66\u8b8a\u6578\u7684 VariableReference \u8d85\u51fa\u4e0a\u4e0b\u6587\u6216\u6c92\u6709\u5b9a\u7fa9\uff01\u540d\u7a31 = {0}"},


  /** Field WG_UNSUPPORTED_ENCODING          */
  //public static final int WG_UNSUPPORTED_ENCODING = 12;


  {
    WG_UNSUPPORTED_ENCODING, "\u672a\u652f\u63f4\u7684\u7de8\u78bc\uff1a{0}"},


  // Other miscellaneous text used inside the code...

  { "ui_language", "zh_TW"},
  { "help_language", "zh_TW"},
  { "language", "zh_TW"},
    { "BAD_CODE",
      "createMessage \u7684\u53c3\u6578\u8d85\u51fa\u754c\u9650"},
    { "FORMAT_FAILED",
      "\u5728 messageFormat \u547c\u53eb\u671f\u9593\u7570\u5e38\u4e1f\u51fa"},
    { "version", ">>>>>>> Xalan \u7248\u672c"},
    { "version2", "<<<<<<<"},
    { "yes", "\u662f"},
    { "line", "\u5217 //"},
    { "column", "\u884c //"},
    { "xsldone", "XSLProcessor: done"},
    { "xpath_option", "xpath \u9078\u9805\uff1a "},
    { "optionIN", "   [-in inputXMLURL]"},
    { "optionSelect", "   [-select xpath expression]"},
    { "optionMatch", 
      "   [-match match pattern (\u7528\u65bc\u7b26\u5408\u8a3a\u65b7)]"},
    { "optionAnyExpr",
      "\u6216\u53ea\u6709\u4e00\u500b xpath \u8868\u793a\u5f0f\u6703\u57f7\u884c\u8a3a\u65b7\u50be\u5370"},
    { "noParsermsg1", "XSL \u7a0b\u5e8f\u4e0d\u6210\u529f\u3002"},
    { "noParsermsg2", "** \u627e\u4e0d\u5230\u5256\u6790\u5668 **"},
    { "noParsermsg3", "\u8acb\u6aa2\u67e5\u985e\u5225\u8def\u5f91\u3002"},
    { "noParsermsg4", 
      "\u5982\u679c\u60a8\u6c92\u6709 IBM \u7684 XML Parser for Java\uff0c\u53ef\u4e0b\u8f09\u81ea "},
    { "noParsermsg5", 
      "IBM \u7684 AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
    { "gtone", ">1" },
    { "zero", "0" },
    { "one", "1" },
    { "two" , "2" },
    { "three", "3" }
  };

  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE          */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED          */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** Field ERROR_RESOURCES          */
  public static final String ERROR_RESOURCES =
    "org.apache.xpath.res.XPATHErrorResources";

  /** Field ERROR_STRING          */
  public static final String ERROR_STRING = "//error";

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

