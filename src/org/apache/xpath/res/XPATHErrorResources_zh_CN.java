/*
 * @(#)XPATHErrorResources_zh_CN.java	1.3 03/04/27
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
public class XPATHErrorResources_zh_CN extends XPATHErrorResources
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
      "\u5339\u914d\u6a21\u5f0f\u4e2d\u4e0d\u5141\u8bb8\u4f7f\u7528 current() \u51fd\u6570\uff01"},


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  {
    ER_CURRENT_TAKES_NO_ARGS,
      "current() \u51fd\u6570\u4e0d\u63a5\u53d7\u53d8\u91cf\uff01"},


  /** Field ER_DOCUMENT_REPLACED          */
  //public static final int ER_DOCUMENT_REPLACED = 3;


  {
    ER_DOCUMENT_REPLACED,
      "document() \u51fd\u6570\u5de5\u5177\u5df2\u88ab org.apache.xalan.xslt.FuncDocument \u66ff\u4ee3\uff01"},


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  {
    ER_CONTEXT_HAS_NO_OWNERDOC,
      "\u4e0a\u4e0b\u6587\u6ca1\u6709\u5c5e\u4e3b\u6587\u6863\uff01"},


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  {
    ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "local-name() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  {
    ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "namespace-uri() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  {
    ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "normalize-space() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  {
    ER_NUMBER_HAS_TOO_MANY_ARGS,
      "number() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  {
    ER_NAME_HAS_TOO_MANY_ARGS, "name() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  {
    ER_STRING_HAS_TOO_MANY_ARGS,
      "string() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  {
    ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "string-length() \u7684\u53d8\u91cf\u592a\u591a\u3002"},


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  {
    ER_TRANSLATE_TAKES_3_ARGS,
      "translate() \u51fd\u6570\u4f7f\u7528\u4e09\u4e2a\u53d8\u91cf\uff01"},


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  {
    ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "unparsed-entity-uri \u51fd\u6570\u5e94\u4f7f\u7528\u4e00\u4e2a\u53d8\u91cf\uff01"},


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  {
    ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "\u4ecd\u672a\u5b9e\u73b0\u540d\u79f0\u7a7a\u95f4\u8f74\uff01"},


  /** Field ER_UNKNOWN_AXIS          */
  //public static final int ER_UNKNOWN_AXIS = 15;


  {
    ER_UNKNOWN_AXIS, "\u672a\u77e5\u8f74\uff1a{0}"},


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  {
    ER_UNKNOWN_MATCH_OPERATION, "\u5339\u914d\u64cd\u4f5c\u672a\u77e5\uff01"},


  /** Field ER_INCORRECT_ARG_LENGTH          */
  //public static final int ER_INCORRECT_ARG_LENGTH = 17;


  {
    ER_INCORRECT_ARG_LENGTH,
      "processing-instruction() \u8282\u70b9\u7684\u53d8\u91cf\u957f\u5ea6\u6d4b\u8bd5\u4e0d\u6b63\u786e\uff01"},


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  {
    ER_CANT_CONVERT_TO_NUMBER,
      "\u4e0d\u80fd\u5c06 {0} \u8f6c\u6362\u4e3a\u4e00\u4e2a\u6570\u5b57"},


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  {
    ER_CANT_CONVERT_TO_NODELIST,
      "\u4e0d\u80fd\u5c06 {0} \u8f6c\u6362\u4e3a\u4e00\u4e2a NodeList\uff01"},


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  {
    ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "\u4e0d\u80fd\u5c06 {0} \u8f6c\u6362\u4e3a\u4e00\u4e2a NodeSetDTM\uff01"},


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  {
    ER_CANT_CONVERT_TO_TYPE,
      "\u4e0d\u80fd\u5c06 {0} \u8f6c\u6362\u4e3a\u4e00\u4e2a type//{1}"},


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  {
    ER_EXPECTED_MATCH_PATTERN,
      "getMatchScore \u4e2d\u9884\u671f\u7684\u5339\u914d\u6a21\u5f0f\uff01"},


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  {
    ER_COULDNOT_GET_VAR_NAMED,
      "\u65e0\u6cd5\u83b7\u5f97\u547d\u540d\u7684\u53d8\u91cf {0}"},


  /** Field ER_UNKNOWN_OPCODE          */
  //public static final int ER_UNKNOWN_OPCODE = 24;


  {
    ER_UNKNOWN_OPCODE, "\u9519\u8bef\uff01op \u4ee3\u7801\u672a\u77e5\uff1a{0}"},


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  {
    ER_EXTRA_ILLEGAL_TOKENS, "\u989d\u5916\u7684\u975e\u6cd5\u6807\u8bb0\uff1a{0}"},


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  {
    ER_EXPECTED_DOUBLE_QUOTE,
      "\u9519\u8bef\u5f15\u7528\u6587\u5b57... \u9884\u671f\u7684\u53cc\u5f15\u7528\uff01"},


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  {
    ER_EXPECTED_SINGLE_QUOTE,
      "\u9519\u8bef\u5f15\u7528\u6587\u5b57... \u9884\u671f\u7684\u5355\u5f15\u7528\uff01"},


  /** Field ER_EMPTY_EXPRESSION          */
  //public static final int ER_EMPTY_EXPRESSION = 28;


  {
    ER_EMPTY_EXPRESSION, "\u7a7a\u8868\u8fbe\u5f0f\uff01"},


  /** Field ER_EXPECTED_BUT_FOUND          */
  //public static final int ER_EXPECTED_BUT_FOUND = 29;


  {
    ER_EXPECTED_BUT_FOUND, "\u9884\u671f\u7684 {0}\uff0c\u4f46\u53d1\u73b0\uff1a{1}"},


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  {
    ER_INCORRECT_PROGRAMMER_ASSERTION,
      "\u7a0b\u5e8f\u5458\u65ad\u8a00\u662f\u9519\u8bef\u7684\uff01- {0}"},


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  {
    ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "19990709 XPath \u8349\u7a3f\u4e0d\u518d\u53ef\u9009\u7528 boolean(...) \u53d8\u91cf\u3002"},


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  {
    ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "\u5df2\u627e\u5230 ','\uff0c\u4f46\u672a\u627e\u5230\u524d\u8ff0\u53d8\u91cf\uff01"},


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  {
    ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "\u5df2\u627e\u5230 ','\uff0c\u4f46\u672a\u627e\u5230\u4ee5\u4e0b\u53d8\u91cf\uff01"},


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  {
    ER_PREDICATE_ILLEGAL_SYNTAX,
      "'..[predicate]' \u6216 '.[predicate]' \u662f\u975e\u6cd5\u8bed\u6cd5\u3002\u8bf7\u6539\u7528 'self::node()[predicate]'\u3002"},


  /** Field ER_ILLEGAL_AXIS_NAME          */
  //public static final int ER_ILLEGAL_AXIS_NAME = 35;


  {
    ER_ILLEGAL_AXIS_NAME, "\u975e\u6cd5\u7684\u8f74\u540d\u79f0\uff1a{0}"},


  /** Field ER_UNKNOWN_NODETYPE          */
  //public static final int ER_UNKNOWN_NODETYPE = 36;


  {
    ER_UNKNOWN_NODETYPE, "\u672a\u77e5\u7684\u8282\u70b9\u7c7b\u578b\uff1a{0}"},


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  {
    ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "\u9700\u8981\u5f15\u7528\u6a21\u5f0f\u6587\u5b57 ({0})\uff01"},


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  {
    ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "\u4e0d\u80fd\u5c06 {0} \u683c\u5f0f\u5316\u4e3a\u4e00\u4e2a\u6570\u5b57\uff01"},


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  {
    ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "\u4e0d\u80fd\u521b\u5efa XML TransformerFactory Liaison\uff1a{0}"},


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  {
    ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "\u9519\u8bef\uff01\u672a\u627e\u5230 xpath select \u8868\u8fbe\u5f0f (-select)\u3002"},


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  {
    ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "\u9519\u8bef\uff01\u672a\u5728 OP_LOCATIONPATH \u540e\u627e\u5230 ENDOP"},


  /** Field ER_ERROR_OCCURED          */
  //public static final int ER_ERROR_OCCURED = 42;


  {
    ER_ERROR_OCCURED, "\u51fa\u9519\uff01"},


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  {
    ER_ILLEGAL_VARIABLE_REFERENCE,
      "\u4e3a\u53d8\u91cf\u7ed9\u51fa\u7684 VariableReference \u8d85\u51fa\u4e86\u4e0a\u4e0b\u6587\u8303\u56f4\u6216\u6ca1\u6709\u5b9a\u4e49\uff01\u540d\u79f0 = {0}"},


  /** Field ER_AXES_NOT_ALLOWED          */
  //public static final int ER_AXES_NOT_ALLOWED = 44;


  {
    ER_AXES_NOT_ALLOWED,
      "\u5339\u914d\u6a21\u5f0f\u4e2d\u4ec5\u5141\u8bb8\u4f7f\u7528 child:: \u548c attribute:: \u8f74\uff01\u8fdd\u4f8b\u8f74 = {0}"},


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  {
    ER_KEY_HAS_TOO_MANY_ARGS,
      "key() \u6709\u8bb8\u591a\u4e0d\u6b63\u786e\u7684\u53d8\u91cf\u3002"},


  /** Field ER_COUNT_TAKES_1_ARG          */
  //public static final int ER_COUNT_TAKES_1_ARG = 46;


  {
    ER_COUNT_TAKES_1_ARG,
      "count \u51fd\u6570\u5e94\u4f7f\u7528\u4e00\u4e2a\u53d8\u91cf\uff01"},


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  {
    ER_COULDNOT_FIND_FUNCTION, "\u672a\u627e\u5230\u51fd\u6570\uff1a{0}"},


  /** Field ER_UNSUPPORTED_ENCODING          */
  //public static final int ER_UNSUPPORTED_ENCODING = 48;


  {
    ER_UNSUPPORTED_ENCODING, "\u4e0d\u53d7\u652f\u6301\u7684\u7f16\u7801\uff1a{0}"},


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  {
    ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "getNextSibling \u4e2d\u7684 DTM \u51fa\u73b0\u95ee\u9898 ... \u6b63\u5728\u5c1d\u8bd5\u6062\u590d"},


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  {
    ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "\u7a0b\u5e8f\u5458\u51fa\u9519\uff1a\u4e0d\u80fd\u5c06 EmptyNodeList \u5199\u5165\u3002"},


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  {
    ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory \u4e0d\u53d7 XPathContext \u652f\u6301\uff01"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u5fc5\u987b\u89e3\u51b3\u540d\u79f0\u7a7a\u95f4\u7684\u524d\u7f00\uff1a{0}"},


  /** Field ER_PARSE_NOT_SUPPORTED          */
  //public static final int ER_PARSE_NOT_SUPPORTED = 53;


  {
    ER_PARSE_NOT_SUPPORTED,
      "XPathContext \u4e2d\u4e0d\u652f\u6301\uff08\u8bed\u6cd5\uff09\u5206\u6790\u5668\uff08InputSource \u6e90\uff09\uff01\u65e0\u6cd5\u6253\u5f00 {0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "XPathContext \u4e2d\u4e0d\u652f\u6301 createDocument()\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u5b50\u5c5e\u6027\u6ca1\u6709\u5c5e\u4e3b\u6587\u6863\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u5b50\u5c5e\u6027\u6ca1\u6709\u5c5e\u4e3b\u6587\u6863\u5143\u7d20\uff01"},


  /** Field ER_SAX_API_NOT_HANDLED          */
  //public static final int ER_SAX_API_NOT_HANDLED = 57;


  {
    ER_SAX_API_NOT_HANDLED,
      "SAX API characters(char ch[]... \u4e0d\u7531 DTM \u5904\u7406\uff01"},


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  {
    ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... \u4e0d\u7531 DTM \u5904\u7406\uff01"},


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  {
    ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison \u65e0\u6cd5\u5904\u7406\u8282\u70b9\u7c7b\u578b {0}"},


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  {
    ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper \u65e0\u6cd5\u5904\u7406\u8282\u70b9\u7c7b\u578b {0}"},


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  {
    ER_XERCES_PARSE_ERROR_DETAILS,
      "DOM2Helper.parse \u9519\u8bef\uff1aSystemID - {0} line - {1}"},


  /** Field ER_XERCES_PARSE_ERROR          */
  //public static final int ER_XERCES_PARSE_ERROR = 62;


  {
    ER_XERCES_PARSE_ERROR, "DOM2Helper.parse \u9519\u8bef"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u8b66\u544a\uff1a\u4e0d\u80fd\u8f93\u51fa\u6587\u6863\u5143\u7d20\u4e4b\u524d\u7684\u6587\u5b57\uff01\u5ffd\u7565..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "DOM \u4e0a\u4e0d\u80fd\u6709\u591a\u4e2a\u6839\uff01"},


  /** Field ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 65;


  {
    ER_INVALID_UTF16_SURROGATE,
      "\u68c0\u6d4b\u5230\u65e0\u6548\u7684 UTF-16 surrogate\uff1a{0} ?"},


  /** Field ER_OIERROR          */
  //public static final int ER_OIERROR = 66;


  {
    ER_OIERROR, "IO \u9519\u8bef"},


  /** Field ER_CANNOT_CREATE_URL          */
  //public static final int ER_CANNOT_CREATE_URL = 67;


  {
    ER_CANNOT_CREATE_URL, "\u65e0\u6cd5\u521b\u5efa url \u4e3a\uff1a{0}"},


  /** Field ER_XPATH_READOBJECT          */
  //public static final int ER_XPATH_READOBJECT = 68;


  {
    ER_XPATH_READOBJECT, "\u5728 XPath.readObject \u4e2d\uff1a{0}"},

  
  /** Field ER_XPATH_READOBJECT         */
  //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


  {
    ER_FUNCTION_TOKEN_NOT_FOUND,
      "\u672a\u627e\u5230\u51fd\u6570\u6807\u8bb0\u3002"},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "\u53d8\u91cf 'localName' \u4e3a\u7a7a"},

  
   /**  Can not deal with XPath type:   */
  //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


  {
    ER_CANNOT_DEAL_XPATH_TYPE,
       "\u65e0\u6cd5\u5904\u7406 XPath \u7c7b\u578b\uff1a{0}"},

  
   /**  This NodeSet is not mutable  */
  //public static final int ER_NODESET_NOT_MUTABLE = 72;


  {
    ER_NODESET_NOT_MUTABLE,
       "\u6b64 NodeSet \u4e0d\u6613\u53d8"},

  
   /**  This NodeSetDTM is not mutable  */
  //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


  {
    ER_NODESETDTM_NOT_MUTABLE,
       "\u6b64 NodeSetDTM \u4e0d\u6613\u53d8"},

  
   /**  Variable not resolvable:   */
  //public static final int ER_VAR_NOT_RESOLVABLE = 74;


  {
    ER_VAR_NOT_RESOLVABLE,
        "\u53d8\u91cf\u4e0d\u53ef\u5206\u89e3\uff1a{0}"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 75;


  {
    ER_NULL_ERROR_HANDLER,
        "\u9519\u8bef\u5904\u7406\u5668\u65e0\u6548"},

  
   /**  Programmer's assertion: unknown opcode  */
  //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


  {
    ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "\u7a0b\u5e8f\u5458\u65ad\u8a00\uff1aopcode \u672a\u77e5\uff1a{0}"},

  
   /**  0 or 1   */
  //public static final int ER_ZERO_OR_ONE = 77;


  {
    ER_ZERO_OR_ONE,
       "0 \u6216 1"},

  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


  {
    ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "rtf() \u4e0d\u53d7 XRTreeFragSelectWrapper \u652f\u6301"},

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


  {
    ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "asNodeIterator() \u4e0d\u53d7 XRTreeFragSelectWrapper \u652f\u6301"},

  
   /**  fsb() not supported for XStringForChars   */
  //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


  {
    ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "fsb() \u4e0d\u652f\u6301 XStringForChars"},

  
   /**  Could not find variable with the name of   */
  //public static final int ER_COULD_NOT_FIND_VAR = 81;


  {
    ER_COULD_NOT_FIND_VAR,
      "\u65e0\u6cd5\u627e\u5230\u540d\u79f0\u4e3a {0} \u7684\u53d8\u91cf"},

  
   /**  XStringForChars can not take a string for an argument   */
  //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


  {
    ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "XStringForChars \u65e0\u6cd5\u4f7f\u7528\u53d8\u91cf\u7684\u4e00\u4e2a\u5b57\u7b26\u4e32"},

  
   /**  The FastStringBuffer argument can not be null   */
  //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


  {
    ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "FastStringBuffer \u53d8\u91cf\u4e0d\u80fd\u4e3a\u7a7a"},
    
   /**  2 or 3   */
  //public static final int ER_TWO_OR_THREE = 84;
  

  {
    ER_TWO_OR_THREE,
       "2 \u6216 3"},

  
   /** Variable accessed before it is bound! */
  //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;
  

  {
    ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "\u7ed1\u5b9a\u4e4b\u524d\u8bbf\u95ee\u4e86\u53d8\u91cf\uff01"},


   /** XStringForFSB can not take a string for an argument! */
  //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;


  {
    ER_FSB_CANNOT_TAKE_STRING,
       "XStringForFSB \u65e0\u6cd5\u63a5\u53d7\u5b57\u7b26\u4e32\u7c7b\u578b\u7684\u53d8\u91cf\uff01"},


   /** Error! Setting the root of a walker to null! */
  //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;


  {
    ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n !!!! \u9519\u8bef\uff01\u904d\u5386\u7a0b\u5e8f\u7684\u6839\u8bbe\u7f6e\u4e3a\u7a7a!!!"},


   /** This NodeSetDTM can not iterate to a previous node! */
  //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;


  {
    ER_NODESETDTM_CANNOT_ITERATE,
       "\u6b64 NodeSetDTM \u65e0\u6cd5\u8fed\u4ee3\u5230\u524d\u4e00\u4e2a\u8282\u70b9\uff01"},


  /** This NodeSet can not iterate to a previous node! */
  //public static final int ER_NODESET_CANNOT_ITERATE = 89;


  {
    ER_NODESET_CANNOT_ITERATE,
       "\u6b64 NodeSet \u65e0\u6cd5\u8fed\u4ee3\u5230\u524d\u4e00\u4e2a\u8282\u70b9\uff01"},


  /** This NodeSetDTM can not do indexing or counting functions! */
  //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;


  {
    ER_NODESETDTM_CANNOT_INDEX,
       "\u6b64 NodeSetDTM \u65e0\u6cd5\u4e3a\u51fd\u6570\u5efa\u7acb\u7d22\u5f15\u6216\u8ba1\u7b97\u4e2a\u6570\uff01"},


  /** This NodeSet can not do indexing or counting functions! */
  //public static final int ER_NODESET_CANNOT_INDEX = 91;


  {
    ER_NODESET_CANNOT_INDEX,
       "\u6b64 NodeSet \u65e0\u6cd5\u4e3a\u51fd\u6570\u5efa\u7acb\u7d22\u5f15\u6216\u8ba1\u7b97\u4e2a\u6570\uff01"},


  /** Can not call setShouldCacheNodes after nextNode has been called! */
  //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;


  {
    ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "\u8c03\u7528 nextNode \u540e\u65e0\u6cd5\u8c03\u7528 setShouldCacheNodes\uff01"},


  /** {0} only allows {1} arguments */
  //public static final int ER_ONLY_ALLOWS = 93;


  {
    ER_ONLY_ALLOWS,
       "{0} \u4ec5\u5141\u8bb8 {1} \u53d8\u91cf"},


  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  //public static final int ER_UNKNOWN_STEP = 94;


  {
    ER_UNKNOWN_STEP,
       "getNextStepPos \u4e2d\u7684\u7a0b\u5e8f\u5458\u58f0\u660e\uff1a\u672a\u77e5\u7684 stepType\uff1a{0}"},


  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.

  /** Problem with RelativeLocationPath */
  //public static final int ER_EXPECTED_REL_LOC_PATH = 95;


  {
    ER_EXPECTED_REL_LOC_PATH,
       "\u201c/\u201d\u6216\u201c//\u201d\u6807\u8bb0\u540e\u9700\u8981\u91c7\u7528\u76f8\u5bf9\u4f4d\u7f6e\u8def\u5f84\u3002"},


  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.

  /** Problem with LocationPath */
  //public static final int ER_EXPECTED_LOC_PATH = 96;


  {
    ER_EXPECTED_LOC_PATH,
       "\u9700\u8981\u4f4d\u7f6e\u8def\u5f84\uff0c\u4f46\u9047\u5230\u4ee5\u4e0b\u6807\u8bb0:  {0}"},


  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.

  /** Problem with Step */
  //public static final int ER_EXPECTED_LOC_STEP = 97;


  {
    ER_EXPECTED_LOC_STEP,
       "\u201c/\u201d\u6216\u201c//\u201d\u6807\u8bb0\u540e\u9700\u8981\u91c7\u7528\u4f4d\u7f6e\u6b65\u9aa4\u3002"},


  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.

  /** Problem with NodeTest */
  //public static final int ER_EXPECTED_NODE_TEST = 98;


  {
    ER_EXPECTED_NODE_TEST,
       "\u9700\u8981\u4e0e NCName:* \u6216 QName \u5339\u914d\u7684\u8282\u70b9\u6d4b\u8bd5\u3002"},


  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.

  /** Expected step pattern */
  //public static final int ER_EXPECTED_STEP_PATTERN = 99;


  {
    ER_EXPECTED_STEP_PATTERN,
       "\u9700\u8981\u6b65\u9aa4\u6a21\u5f0f\uff0c\u4f46\u9047\u5230\u201c/\u201d\u3002"},


  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
 
  /** Expected relative path pattern */
  //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;


  {
    ER_EXPECTED_REL_PATH_PATTERN,
       "\u9700\u8981\u76f8\u5bf9\u8def\u5f84\u6a21\u5f0f\u3002"},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;


  {
    ER_ARG_LOCALNAME_INVALID,
       "QNAME \u4e2d\u7684 Localname \u5e94\u8be5\u662f\u4e00\u4e2a\u6709\u6548\u7684 NCName"},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;


  {
    ER_ARG_PREFIX_INVALID,
       "QNAME \u4e2d\u7684\u524d\u7f00\u5e94\u8be5\u662f\u4e00\u4e2a\u6709\u6548\u7684 NCName"},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
  //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;


  {
    ER_CANT_CONVERT_TO_BOOLEAN,
       "\u65e0\u6cd5\u5c06 {0} \u8f6c\u6362\u6210\u5e03\u5c14\u503c\u3002"},


  // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
  // FIRST_ORDERED_NODE_TYPE.

  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;


  {
    ER_CANT_CONVERT_TO_SINGLENODE,
       "\u65e0\u6cd5\u5c06 {0} \u8f6c\u6362\u6210\u5355\u8282\u70b9\u3002\u6b64\u83b7\u53d6\u7a0b\u5e8f\u9002\u7528\u4e8e ANY_UNORDERED_NODE_TYPE \u548c FIRST_ORDERED_NODE_TYPE \u7c7b\u578b\u3002"},


  // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;


  {
    ER_CANT_GET_SNAPSHOT_LENGTH,
       "\u65e0\u6cd5\u83b7\u53d6\u7c7b\u578b {0} \u4e0a\u7684\u5feb\u7167\u957f\u5ea6\u3002\u6b64\u83b7\u53d6\u7a0b\u5e8f\u9002\u7528\u4e8e UNORDERED_NODE_SNAPSHOT_TYPE \u548c ORDERED_NODE_SNAPSHOT_TYPE \u7c7b\u578b\u3002"},


  /** Field ER_NON_ITERATOR_TYPE                */
  //public static final int ER_NON_ITERATOR_TYPE        = 106;


  {
    ER_NON_ITERATOR_TYPE,
       "\u65e0\u6cd5\u8fed\u4ee3\u975e\u53e0\u4ee3\u5668\u7c7b\u578b\uff1a{0}"},


  // Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.

  /** Field ER_DOC_MUTATED                      */
  //public static final int ER_DOC_MUTATED              = 107;


  {
    ER_DOC_MUTATED,
       "\u7531\u4e8e\u7ed3\u679c\u7684\u8fd4\u56de\u4f7f\u6587\u6863\u53d1\u751f\u6539\u53d8\u3002\u53e0\u4ee3\u5668\u65e0\u6548\u3002"},


  /** Field ER_INVALID_XPATH_TYPE               */
  //public static final int ER_INVALID_XPATH_TYPE       = 108;


  {
    ER_INVALID_XPATH_TYPE,
       "\u65e0\u6548 XPath \u7c7b\u578b\u53d8\u91cf\uff1a{0}"},


  /** Field ER_EMPTY_XPATH_RESULT                */
  //public static final int ER_EMPTY_XPATH_RESULT       = 109;


  {
    ER_EMPTY_XPATH_RESULT,
       "\u7a7a\u7684 XPath \u7ed3\u679c\u5bf9\u8c61"},


  /** Field ER_INCOMPATIBLE_TYPES                */
  //public static final int ER_INCOMPATIBLE_TYPES       = 110;


  {
    ER_INCOMPATIBLE_TYPES,
       "\u8fd4\u56de\u7c7b\u578b\uff1a{0} \u65e0\u6cd5\u5f3a\u5236\u8f6c\u6362\u6210\u6307\u5b9a\u7684\u7c7b\u578b\uff1a{1}"},


  /** Field ER_NULL_RESOLVER                     */
  //public static final int ER_NULL_RESOLVER            = 111;


  {
    ER_NULL_RESOLVER,
       "\u65e0\u6cd5\u7528\u7a7a\u7684\u524d\u7f00\u89e3\u51b3\u5668\u6765\u89e3\u51b3\u524d\u7f00\u3002"},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_STRING            */
  //public static final int ER_CANT_CONVERT_TO_STRING   = 112;


  {
    ER_CANT_CONVERT_TO_STRING,
       "\u65e0\u6cd5\u5c06 {0} \u8f6c\u6362\u6210\u5b57\u7b26\u4e32\u3002"},


  // Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_NON_SNAPSHOT_TYPE                 */
  //public static final int ER_NON_SNAPSHOT_TYPE       = 113;


  {
    ER_NON_SNAPSHOT_TYPE,
       "\u65e0\u6cd5\u5bf9\u7c7b\u578b {0} \u8c03\u7528 snapshotItem\u3002\u6b64\u65b9\u6cd5\u9002\u7528\u4e8e UNORDERED_NODE_SNAPSHOT_TYPE \u548c ORDERED_NODE_SNAPSHOT_TYPE \u7c7b\u578b\u3002"},


  // Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.

  /** Field ER_WRONG_DOCUMENT                    */
  //public static final int ER_WRONG_DOCUMENT          = 114;


  {
    ER_WRONG_DOCUMENT,
       "\u4e0a\u4e0b\u6587\u8282\u70b9\u4e0d\u5c5e\u4e8e\u7ed1\u5b9a\u5230\u6b64 XPathEvaluator \u7684\u6587\u6863\u3002"},


  // Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  /** Field ER_WRONG_NODETYPE                    */
  //public static final int ER_WRONG_NODETYPE          = 115;


  {
    ER_WRONG_NODETYPE ,
       "\u4e0a\u4e0b\u6587\u8282\u70b9\u7c7b\u578b\u4e0d\u53d7\u652f\u6301\u3002"},


  /** Field ER_XPATH_ERROR                       */
  //public static final int ER_XPATH_ERROR             = 116;


  {
    ER_XPATH_ERROR ,
       "XPath \u4e2d\u51fa\u73b0\u672a\u77e5\u9519\u8bef\u3002"},



  


  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  {
    WG_LOCALE_NAME_NOT_HANDLED,
      "\u4ecd\u672a\u5904\u7406 format-number \u51fd\u6570\u4e2d\u7684\u8bed\u8a00\u73af\u5883\u540d\u79f0\uff01"},


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  {
    WG_PROPERTY_NOT_SUPPORTED,
      "\u4e0d\u53d7\u652f\u6301\u7684 XSL Property\uff1a{0}"},


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  {
    WG_DONT_DO_ANYTHING_WITH_NS,
      "\u5f53\u524d\u4e0d\u8981\u5bf9\u5c5e\u6027\u4e2d\u7684\u540d\u79f0\u7a7a\u95f4 {0} \u6267\u884c\u4efb\u4f55\u64cd\u4f5c\uff1a{1}"},


  /** Field WG_SECURITY_EXCEPTION          */
  //public static final int WG_SECURITY_EXCEPTION = 4;


  {
    WG_SECURITY_EXCEPTION,
      "\u5c1d\u8bd5\u8bbf\u95ee XSL \u7cfb\u7edf\u5c5e\u6027\u65f6\uff0cSecurityException\uff1a{0}"},


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  {
    WG_QUO_NO_LONGER_DEFINED,
      "\u65e7\u8bed\u6cd5\uff1aXPath \u4e2d\u4e0d\u518d\u5b9a\u4e49 quo(...)\u3002"},


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  {
    WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "XPath \u9700\u8981\u7531\u4e00\u4e2a\u6d3e\u751f\u5bf9\u8c61\u6765\u6267\u884c nodeTest\uff01"},


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  {
    WG_FUNCTION_TOKEN_NOT_FOUND,
      "\u672a\u627e\u5230\u51fd\u6570\u6807\u8bb0\u3002"},


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  {
    WG_COULDNOT_FIND_FUNCTION,
      "\u65e0\u6cd5\u627e\u5230\u51fd\u6570\uff1a{0}"},


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "\u65e0\u6cd5\u5f97\u5230 URL \u81ea\uff1a{0}"},


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  {
    WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "-E \u9009\u9879\u4e0d\u652f\u6301 DTM\uff08\u8bed\u6cd5\uff09\u5206\u6790\u5668"},


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  {
    WG_ILLEGAL_VARIABLE_REFERENCE,
      "\u4e3a\u53d8\u91cf\u7ed9\u51fa\u7684 VariableReference \u8d85\u51fa\u4e86\u4e0a\u4e0b\u6587\u8303\u56f4\u6216\u6ca1\u6709\u5b9a\u4e49\uff01\u540d\u79f0 = {0}"},


  /** Field WG_UNSUPPORTED_ENCODING          */
  //public static final int WG_UNSUPPORTED_ENCODING = 12;


  {
    WG_UNSUPPORTED_ENCODING, "\u4e0d\u53d7\u652f\u6301\u7684\u7f16\u7801\uff1a{0}"},


  // Other miscellaneous text used inside the code...
  { "ui_language", "zh"},
  { "help_language", "zh"},
  { "language", "zh"},
    { "BAD_CODE",
      "createMessage \u53c2\u6570\u8d85\u51fa\u4e86\u8303\u56f4"},
    { "FORMAT_FAILED",
      "messageFormat \u8c03\u7528\u671f\u95f4\u63d0\u51fa\u4e86\u4f8b\u5916"},
    { "version", ">>>>>>> Xalan \u7248\u672c"},
    { "version2", "<<<<<<<"},
    { "yes", "\u662f"},
    { "line", "\u884c //"},
    { "column", "\u5217 //"},
    { "xsldone", "XSLProcessor\uff1a\u5df2\u5b8c\u6210"},
    { "xpath_option", "xpath \u9009\u9879\uff1a"},
    { "optionIN", "   [-in inputXMLURL]"},
    { "optionSelect", "   [-select xpath expression]"},
    { "optionMatch",
      "   [-match match pattern (for match diagnostics)]"},
    { "optionAnyExpr",
      "\u6216\u8005\u53ea\u6709 xpath \u8868\u8fbe\u5f0f\u5c06\u6267\u884c\u8bca\u65ad\u8f6c\u50a8"},
    { "noParsermsg1", "XSL Process \u672a\u6210\u529f\u3002"},
    { "noParsermsg2",
    "** \u65e0\u6cd5\u627e\u5230\uff08\u8bed\u6cd5\uff09\u5206\u6790\u5668 **"},
    { "noParsermsg3",
    "\u8bf7\u68c0\u67e5\u60a8\u7684\u7c7b\u8def\u5f84\u3002"},
    { "noParsermsg4", 
      "\u5982\u679c\u60a8\u6ca1\u6709 IBM \u7684 XML Parser for Java\uff0c\u5219\u53ef\u4ee5\u4e0b\u8f7d\u5176\u81ea"},
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
  public static final String ERROR_HEADER = "\u9519\u8bef\uff1a";

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

