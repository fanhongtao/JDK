/*
 * @(#)XPATHErrorResources_ja.java	1.5 03/04/28
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
public class XPATHErrorResources_ja extends XPATHErrorResources
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
      "current() \u95a2\u6570\u306f\u4e00\u81f4\u30d1\u30bf\u30fc\u30f3\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "The current() function is not allowed in a match pattern!"},


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  {
    ER_CURRENT_TAKES_NO_ARGS,
      "current() \u95a2\u6570\u306f\u5f15\u6570\u3092\u53d7\u3051\u5165\u308c\u307e\u305b\u3093\u3002"},
//      "The current() function does not accept arguments!"},


  /** Field ER_DOCUMENT_REPLACED          */
  //public static final int ER_DOCUMENT_REPLACED = 3;


  {
    ER_DOCUMENT_REPLACED,
      "document() \u95a2\u6570\u5b9f\u88c5\u306f org.apache.xalan.xslt.FuncDocument \u306b\u7f6e\u304d\u63db\u3048\u3089\u308c\u307e\u3057\u305f\u3002"},
//      "document() function implementation has been replaced by org.apache.xalan.xslt.FuncDocument!"},


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  {
    ER_CONTEXT_HAS_NO_OWNERDOC,
      "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u307e\u305b\u3093\u3002"},
//      "context does not have an owner document!"},


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  {
    ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "local-name() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "local-name() has too many arguments."},


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  {
    ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "namespace-uri() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "namespace-uri() has too many arguments."},


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  {
    ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "normalize-space() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "normalize-space() has too many arguments."},


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  {
    ER_NUMBER_HAS_TOO_MANY_ARGS,
      "number() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "number() has too many arguments."},


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  {
    ER_NAME_HAS_TOO_MANY_ARGS, "name() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//    ER_NAME_HAS_TOO_MANY_ARGS, "name() has too many arguments."},


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  {
    ER_STRING_HAS_TOO_MANY_ARGS,
      "string() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "string() has too many arguments."},


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  {
    ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "string-length() \u306e\u5f15\u6570\u304c\u591a\u3059\u304e\u307e\u3059\u3002"},
//      "string-length() has too many arguments."},


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  {
    ER_TRANSLATE_TAKES_3_ARGS,
      "translate() \u95a2\u6570\u306b 3 \u3064\u306e\u5f15\u6570\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059\u3002"},
//      "The translate() function takes three arguments!"},


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  {
    ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "unparsed-entity-uri \u95a2\u6570\u306f\u5f15\u6570\u3092 1 \u3064\u3060\u3051\u4f7f\u7528\u3067\u304d\u307e\u3059\u3002"},
//      "The unparsed-entity-uri function should take one argument!"},


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  {
    ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "\u540d\u524d\u7a7a\u9593\u8ef8\u306f\u307e\u3060\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "namespace axis not implemented yet!"},


  /** Field ER_UNKNOWN_AXIS          */
  //public static final int ER_UNKNOWN_AXIS = 15;


  {
    ER_UNKNOWN_AXIS, "\u672a\u77e5\u306e\u8ef8: {0}"},
//    ER_UNKNOWN_AXIS, "unknown axis: {0}"},


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  {
    ER_UNKNOWN_MATCH_OPERATION, "\u672a\u77e5\u306e\u7167\u5408\u30aa\u30da\u30ec\u30fc\u30b7\u30e7\u30f3\u3067\u3059\u3002"},
//    ER_UNKNOWN_MATCH_OPERATION, "unknown match operation!"},


  /** Field ER_INCORRECT_ARG_LENGTH          */
  //public static final int ER_INCORRECT_ARG_LENGTH = 17;


  {
    ER_INCORRECT_ARG_LENGTH,
      "processing-instruction() \u30ce\u30fc\u30c9\u30c6\u30b9\u30c8\u306e\u5f15\u6570\u306e\u9577\u3055\u304c\u4e0d\u6b63\u3067\u3059\u3002"},
//      "Arg length of processing-instruction() node test is incorrect!"},


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  {
    ER_CANT_CONVERT_TO_NUMBER,
      "{0} \u3092\u6570\u5b57\u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093"},
//      "Can not convert {0} to a number"},


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  {
    ER_CANT_CONVERT_TO_NODELIST,
      "{0} \u3092 NodeList \u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can not convert {0} to a NodeList!"},


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  {
    ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "{0} \u3092 NodeSetDTM \u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can not convert {0} to a NodeSetDTM!"},


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  {
    ER_CANT_CONVERT_TO_TYPE,
      "{0} \u3092 type//{1} \u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093"},
//      "Can not convert {0} to a type//{1}"},


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  {
    ER_EXPECTED_MATCH_PATTERN,
      "getMatchScore \u306b\u4e88\u671f\u3055\u308c\u308b\u4e00\u81f4\u30d1\u30bf\u30fc\u30f3\u3067\u3059\u3002"},
//      "Expected match pattern in getMatchScore!"},


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  {
    ER_COULDNOT_GET_VAR_NAMED,
      "{0} \u3068\u3044\u3046\u540d\u524d\u306e\u5909\u6570\u3092\u53d6\u5f97\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//      "Could not get variable named {0}"},


  /** Field ER_UNKNOWN_OPCODE          */
  //public static final int ER_UNKNOWN_OPCODE = 24;


  {
    ER_UNKNOWN_OPCODE, "\u30a8\u30e9\u30fc\u3002\u672a\u77e5\u306e\u30aa\u30d7\u30b7\u30e7\u30f3\u30b3\u30fc\u30c9: {0}"},
//    ER_UNKNOWN_OPCODE, ERROR! Unknown op code: {0}"},


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  {
    ER_EXTRA_ILLEGAL_TOKENS, "\u4f59\u5206\u306a\u4e0d\u6b63\u30c8\u30fc\u30af\u30f3: {0}"},
//    ER_EXTRA_ILLEGAL_TOKENS, "Extra illegal tokens: {0}"},


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  {
    ER_EXPECTED_DOUBLE_QUOTE,
      "\u30ea\u30c6\u30e9\u30eb\u306e\u5f15\u7528\u7b26\u304c\u8aa4\u308a\u3067\u3059... \u4e8c\u91cd\u5f15\u7528\u7b26\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//      "misquoted literal... expected double quote!"},


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  {
    ER_EXPECTED_SINGLE_QUOTE,
      "\u30ea\u30c6\u30e9\u30eb\u306e\u5f15\u7528\u7b26\u304c\u8aa4\u308a\u3067\u3059... \u5358\u4e00\u5f15\u7528\u7b26\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//      "misquoted literal... expected single quote!"},


  /** Field ER_EMPTY_EXPRESSION          */
  //public static final int ER_EMPTY_EXPRESSION = 28;


  {
    ER_EMPTY_EXPRESSION, "\u5f0f\u304c\u7a7a\u3067\u3059\u3002"},
//    ER_EMPTY_EXPRESSION, "Empty expression!"},


  /** Field ER_EXPECTED_BUT_FOUND          */
  //public static final int ER_EXPECTED_BUT_FOUND = 29;


  {
    ER_EXPECTED_BUT_FOUND, "{0} \u304c\u4e88\u671f\u3055\u308c\u3066\u3044\u307e\u3057\u305f\u304c\u3001{1} \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u3002"},
//    ER_EXPECTED_BUT_FOUND, "Expected {0}, but found: {1}"},


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  {
    ER_INCORRECT_PROGRAMMER_ASSERTION,
      "\u30d7\u30ed\u30b0\u30e9\u30de\u306e\u8868\u660e\u304c\u4e0d\u6b63\u3067\u3059\u3002 - {0}"},
//      "Programmer assertion is incorrect! - {0}"},


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  {
    ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "19990709 XPath \u30c9\u30e9\u30d5\u30c8\u306b\u3064\u3044\u3066\u3001boolean(...) \u5f15\u6570\u306f\u3082\u3046\u4efb\u610f\u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "boolean(...) argument is no longer optional with 19990709 XPath draft."},


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  {
    ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "',' \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u305d\u306e\u524d\u306b\u5f15\u6570\u304c\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "Found ',' but no preceding argument!"},


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  {
    ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "',' \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u305d\u308c\u306b\u7d9a\u304f\u5f15\u6570\u304c\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "Found ',' but no following argument!"},


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  {
    ER_PREDICATE_ILLEGAL_SYNTAX,
      "'..[predicate]' \u307e\u305f\u306f '.[predicate]' \u306f\u4e0d\u5f53\u306a\u69cb\u6587\u3067\u3059\u3002\u4ee3\u308f\u308a\u306b 'self::node()[predicate]' \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
//      "'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead."},


  /** Field ER_ILLEGAL_AXIS_NAME          */
  //public static final int ER_ILLEGAL_AXIS_NAME = 35;


  {
    ER_ILLEGAL_AXIS_NAME, "\u4e0d\u5f53\u306a\u8ef8\u540d: {0}"},
//    ER_ILLEGAL_AXIS_NAME, "illegal axis name: {0}"},


  /** Field ER_UNKNOWN_NODETYPE          */
  //public static final int ER_UNKNOWN_NODETYPE = 36;


  {
    ER_UNKNOWN_NODETYPE, "\u672a\u77e5\u306e\u30ce\u30fc\u30c9\u30bf\u30a4\u30d7: {0}"},
//    ER_UNKNOWN_NODETYPE, "Unknown nodetype: {0}"},


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  {
    ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "\u30ea\u30c6\u30e9\u30eb ({0}) \u30d1\u30bf\u30fc\u30f3\u306f\u3001\u5f15\u7528\u7b26\u3067\u56f2\u3080\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002"},
//      "Pattern literal ({0}) needs to be quoted!"},


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  {
    ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "{0} \u306f\u6570\u5b57\u306b\u66f8\u5f0f\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//      "{0} could not be formatted to a number!"},


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  {
    ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "XML TransformerFactory Liaison \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f: {0}"},
//      "Could not create XML TransformerFactory Liaison: {0}"},


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  {
    ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "\u30a8\u30e9\u30fc\u3002xpath \u9078\u629e\u5f0f (-select) \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//      "Error! Did not find xpath select expression (-select)."},


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  {
    ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "\u30a8\u30e9\u30fc\u3002OP_LOCATIONPATH \u306e\u5f8c\u306b ENDOP \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//      ERROR! Could not find ENDOP after OP_LOCATIONPATH"},


  /** Field ER_ERROR_OCCURED          */
  //public static final int ER_ERROR_OCCURED = 42;


  {
    ER_ERROR_OCCURED, "\u30a8\u30e9\u30fc\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002"},
//    ER_ERROR_OCCURED, "Error occured!"},


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  {
    ER_ILLEGAL_VARIABLE_REFERENCE,
      "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u306e\u5916\u3067\u3001\u307e\u305f\u306f\u5b9a\u7fa9\u306a\u3057\u3067 VariableReference \u304c\u5909\u6570\u306b\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f\u3002Name = {0}"},
//      "VariableReference given for variable out of context or without definition!  Name = {0}"},


  /** Field ER_AXES_NOT_ALLOWED          */
  //public static final int ER_AXES_NOT_ALLOWED = 44;


  {
    ER_AXES_NOT_ALLOWED,
      "\u4e00\u81f4\u30d1\u30bf\u30fc\u30f3\u306b\u306f\u3001child:: \u304a\u3088\u3073 attribute:: \u8ef8\u3060\u3051\u304c\u8a31\u53ef\u3055\u308c\u307e\u3059\u3002\u8a31\u53ef\u3055\u308c\u306a\u3044\u8ef8 = {0}"},
//      "Only child:: and attribute:: axes are allowed in match patterns!  Offending axes = {0}"},


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  {
    ER_KEY_HAS_TOO_MANY_ARGS,
      "key() \u306e\u5f15\u6570\u306e\u6570\u304c\u4e0d\u6b63\u3067\u3059\u3002"},
//      "key() has an incorrect number of arguments."},


  /** Field ER_COUNT_TAKES_1_ARG          */
  //public static final int ER_COUNT_TAKES_1_ARG = 46;


  {
    ER_COUNT_TAKES_1_ARG,
      "count \u95a2\u6570\u306b\u4f7f\u7528\u3067\u304d\u308b\u5f15\u6570\u306f 1 \u3064\u3067\u3059\u3002"},
//      "The count function should take one argument!"},


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  {
    ER_COULDNOT_FIND_FUNCTION, "\u95a2\u6570 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//    ER_COULDNOT_FIND_FUNCTION, "Could not find function: {0}"},


  /** Field ER_UNSUPPORTED_ENCODING          */
  //public static final int ER_UNSUPPORTED_ENCODING = 48;


  {
    ER_UNSUPPORTED_ENCODING, "\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u306a\u3044\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0: {0}"},
//    ER_UNSUPPORTED_ENCODING, "Unsupported encoding: {0}"},


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  {
    ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "getNextSibling \u306e DTM \u306b\u554f\u984c\u304c\u767a\u751f\u3057\u307e\u3057\u305f... \u5fa9\u5143\u3057\u3066\u3044\u307e\u3059"},
//      "Problem occured in DTM in getNextSibling... trying to recover"},


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  {
    ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "\u30d7\u30ed\u30b0\u30e9\u30de\u30a8\u30e9\u30fc: EmptyNodeList \u306b\u66f8\u304d\u8fbc\u307f\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Programmer error: EmptyNodeList can not be written to."},


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  {
    ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory \u306f XPathContext \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "setDOMFactory is not supported by XPathContext!"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u63a5\u982d\u8f9e\u306f\u540d\u524d\u7a7a\u9593\u306b\u5909\u3048\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059: {0}"},
//      "Prefix must resolve to a namespace: {0}"},


  /** Field ER_PARSE_NOT_SUPPORTED          */
  //public static final int ER_PARSE_NOT_SUPPORTED = 53;


  {
    ER_PARSE_NOT_SUPPORTED,
      "\u69cb\u6587\u89e3\u6790 (InputSource \u30bd\u30fc\u30b9) \u306f XPathContext \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002{0} \u3092\u30aa\u30fc\u30d7\u30f3\u3067\u304d\u307e\u305b\u3093"},
//      "parse (InputSource source) not supported in XPathContext! Can not open {0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() \u306f XPathContext \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "createDocument() not supported in XPathContext!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u5c5e\u6027 child \u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Attribute child does not have an owner document!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u5c5e\u6027 child \u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u8981\u7d20\u3092\u4fdd\u6301\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Attribute child does not have an owner document element!"},


  /** Field ER_SAX_API_NOT_HANDLED          */
  //public static final int ER_SAX_API_NOT_HANDLED = 57;


  {
    ER_SAX_API_NOT_HANDLED,
      "SAX API characters(char ch[]... \u306f DTM \u3067\u51e6\u7406\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "SAX API characters(char ch[]... not handled by the DTM!"},


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  {
    ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... \u306f DTM \u3067\u51e6\u7406\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "ignorableWhitespace(char ch[]... not handled by the DTM!"},


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  {
    ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison \u306f\u30bf\u30a4\u30d7 {0} \u306e\u30ce\u30fc\u30c9\u3092\u51e6\u7406\u3067\u304d\u307e\u305b\u3093"},
//      "DTMLiaison can not handle nodes of type {0}"},


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  {
    ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper \u306f\u30bf\u30a4\u30d7 {0} \u306e\u30ce\u30fc\u30c9\u3092\u51e6\u7406\u3067\u304d\u307e\u305b\u3093"},
//      "DOM2Helper can not handle nodes of type {0}"},


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  {
    ER_XERCES_PARSE_ERROR_DETAILS,
      "DOM2Helper.parse \u30a8\u30e9\u30fc: SystemID - {0} \u884c\u756a\u53f7 - {1}"},
//      "DOM2Helper.parse error: SystemID - {0} line - {1}"},


  /** Field ER_XERCES_PARSE_ERROR          */
  //public static final int ER_XERCES_PARSE_ERROR = 62;


  {
    ER_XERCES_PARSE_ERROR, "DOM2Helper.parse \u30a8\u30e9\u30fc"},
//    ER_XERCES_PARSE_ERROR, "DOM2Helper.parse error"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u8b66\u544a: \u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u8981\u7d20\u3088\u308a\u524d\u306b\u30c6\u30ad\u30b9\u30c8\u3092\u51fa\u529b\u3067\u304d\u307e\u305b\u3093\u3002\u7121\u8996\u3057\u307e\u3059..."},
//      "Warning: can't output text before document element!  Ignoring..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "DOM \u306b\u306f\u8907\u6570\u306e\u30eb\u30fc\u30c8\u3092\u4fdd\u6301\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can't have more than one root on a DOM!"},


  /** Field ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 65;


  {
    ER_INVALID_UTF16_SURROGATE,
      "\u7121\u52b9\u306a UTF-16 \u4ee3\u7406\u304c\u691c\u51fa\u3055\u308c\u307e\u3057\u305f: {0} ?"},
//      "Invalid UTF-16 surrogate detected: {0} ?"},


  /** Field ER_OIERROR          */
  //public static final int ER_OIERROR = 66;


  {
    ER_OIERROR, "\u5165\u51fa\u529b\u30a8\u30e9\u30fc"},
//    ER_OIERROR, "IO error"},


  /** Field ER_CANNOT_CREATE_URL          */
  //public static final int ER_CANNOT_CREATE_URL = 67;


  {
    ER_CANNOT_CREATE_URL, "{0} \u306e URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093"},
//    ER_CANNOT_CREATE_URL, "Cannot create url for: {0}"},


  /** Field ER_XPATH_READOBJECT          */
  //public static final int ER_XPATH_READOBJECT = 68;


  {
    ER_XPATH_READOBJECT, "XPath.readObject \u306b\u3042\u308a\u307e\u3059: {0}"},
//    ER_XPATH_READOBJECT, "In XPath.readObject: {0}"},

  
  /** Field ER_XPATH_READOBJECT         */
  //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


  {
    ER_FUNCTION_TOKEN_NOT_FOUND,
      "\u95a2\u6570\u30c8\u30fc\u30af\u30f3\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002"},
//      "function token not found."},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "\u5f15\u6570 'localName' \u304c null \u3067\u3059"},
//       "Argument 'localName' is null"},

  
   /**  Can not deal with XPath type:   */
  //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


  {
    ER_CANNOT_DEAL_XPATH_TYPE,
       "XPath \u30bf\u30a4\u30d7 {0} \u306f\u51e6\u7406\u3067\u304d\u307e\u305b\u3093"},
//       "Can not deal with XPath type: {0}"},

  
   /**  This NodeSet is not mutable  */
  //public static final int ER_NODESET_NOT_MUTABLE = 72;


  {
    ER_NODESET_NOT_MUTABLE,
       "\u3053\u306e NodeSet \u306f\u53ef\u5909\u3067\u306f\u3042\u308a\u307e\u305b\u3093"},
//       "This NodeSet is not mutable"},

  
   /**  This NodeSetDTM is not mutable  */
  //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


  {
    ER_NODESETDTM_NOT_MUTABLE,
       "\u3053\u306e NodeSetDTM \u306f\u53ef\u5909\u3067\u306f\u3042\u308a\u307e\u305b\u3093"},
//       "This NodeSetDTM is not mutable"},

  
   /**  Variable not resolvable:   */
  //public static final int ER_VAR_NOT_RESOLVABLE = 74;


  {
    ER_VAR_NOT_RESOLVABLE,
        "\u89e3\u6c7a\u3067\u304d\u306a\u3044\u5909\u6570: {0}"},
//        "Variable not resolvable: {0}"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 75;


  {
    ER_NULL_ERROR_HANDLER,
        "null \u30a8\u30e9\u30fc\u30cf\u30f3\u30c9\u30e9"},
//        "Null error handler"},

  
   /**  Programmer's assertion: unknown opcode  */
  //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


  {
    ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "\u30d7\u30ed\u30b0\u30e9\u30de\u306e\u8868\u660e: \u672a\u77e5\u306e\u30aa\u30d7\u30b7\u30e7\u30f3\u30b3\u30fc\u30c9: {0}"},
//       "Programmer's assertion: unknown opcode: {0}"},

  
   /**  0 or 1   */
  //public static final int ER_ZERO_OR_ONE = 77;


  {
    ER_ZERO_OR_ONE,
       "0 \u307e\u305f\u306f 1"},
//       "0 or 1"},

  
  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


  {
    ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "rtf() \u306f XRTreeFragSelectWrapper \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "rtf() not supported by XRTreeFragSelectWrapper"},

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


  {
    ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "asNodeIterator() \u306f XRTreeFragSelectWrapper \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "asNodeIterator() not supported by XRTreeFragSelectWrapper"},

  
   /**  fsb() not supported for XStringForChars   */
  //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


  {
    ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "XStringForChars \u306b fsb() \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "fsb() not supported for XStringForChars"},

  
   /**  Could not find variable with the name of   */
  //public static final int ER_COULD_NOT_FIND_VAR = 81;


  {
    ER_COULD_NOT_FIND_VAR,
      "{0} \u3068\u3044\u3046\u540d\u524d\u306e\u5909\u6570\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//      "Could not find variable with the name of {0}"},

  
   /**  XStringForChars can not take a string for an argument   */
  //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


  {
    ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "XStringForChars \u306f\u5f15\u6570\u306b\u6587\u5b57\u5217\u3092\u4f7f\u7528\u3067\u304d\u307e\u305b\u3093"},
//      "XStringForChars can not take a string for an argument"},

  
   /**  The FastStringBuffer argument can not be null   */
  //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


  {
    ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "FastStringBuffer \u5f15\u6570\u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//      "The FastStringBuffer argument can not be null"},
    
  
  /* MANTIS_XALAN CHANGE: BEGIN */ 
   /**  2 or 3   */
  //public static final int ER_TWO_OR_THREE = 84;


  {
    ER_TWO_OR_THREE,
       "2 \u307e\u305f\u306f 3"},


   /** Variable accessed before it is bound! */
  //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;


  {
    ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "\u30d0\u30a4\u30f3\u30c9\u524d\u306e\u5909\u6570\u306b\u30a2\u30af\u30bb\u30b9\u3057\u307e\u3057\u305f!"},


   /** XStringForFSB can not take a string for an argument! */
  //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;


  {
    ER_FSB_CANNOT_TAKE_STRING,
       "XStringForFSB \u306e\u5f15\u6570\u306b\u306f\u6587\u5b57\u5217\u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093!"},


   /** Error! Setting the root of a walker to null! */
  //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;


  {
    ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n !!!! \u30a8\u30e9\u30fc! walker \u306e\u30eb\u30fc\u30c8\u306e\u8a2d\u5b9a\u3092 null \u306b\u3057\u3066\u304f\u3060\u3055\u3044!!!"},


   /** This NodeSetDTM can not iterate to a previous node! */
  //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;


  {
    ER_NODESETDTM_CANNOT_ITERATE,
       "\u3053\u306e NodeSetDTM \u306f\u524d\u306e\u30ce\u30fc\u30c9\u306b\u5bfe\u3057\u3066\u7e70\u308a\u8fd4\u3057\u51e6\u7406\u3092\u5b9f\u884c\u3067\u304d\u307e\u305b\u3093!"},


  /** This NodeSet can not iterate to a previous node! */
  //public static final int ER_NODESET_CANNOT_ITERATE = 89;


  {
    ER_NODESET_CANNOT_ITERATE,
       "\u3053\u306e NodeSet \u306f\u524d\u306e\u30ce\u30fc\u30c9\u306b\u5bfe\u3057\u3066\u7e70\u308a\u8fd4\u3057\u51e6\u7406\u3092\u5b9f\u884c\u3067\u304d\u307e\u305b\u3093!"},


  /** This NodeSetDTM can not do indexing or counting functions! */
  //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;


  {
    ER_NODESETDTM_CANNOT_INDEX,
       "\u3053\u306e NodeSetDTM \u306f\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u51e6\u7406\u307e\u305f\u306f\u30ab\u30a6\u30f3\u30c8\u51e6\u7406\u3092\u5b9f\u884c\u3067\u304d\u307e\u305b\u3093!"},


  /** This NodeSet can not do indexing or counting functions! */
  //public static final int ER_NODESET_CANNOT_INDEX = 91;


  {
    ER_NODESET_CANNOT_INDEX,
       "\u3053\u306e NodeSet \u306f\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u51e6\u7406\u307e\u305f\u306f\u30ab\u30a6\u30f3\u30c8\u51e6\u7406\u3092\u5b9f\u884c\u3067\u304d\u307e\u305b\u3093!"},


  /** Can not call setShouldCacheNodes after nextNode has been called! */
  //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;


  {
    ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "nextNode \u3092\u547c\u3073\u51fa\u3057\u305f\u5f8c\u3067 setShouldCacheNodes \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093!"},


  /** {0} only allows {1} arguments */
  //public static final int ER_ONLY_ALLOWS = 93;


  {
    ER_ONLY_ALLOWS,
       "{0} \u3067\u8a31\u3055\u308c\u308b\u5f15\u6570\u306f {1} \u3060\u3051\u3067\u3059"},


  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  //public static final int ER_UNKNOWN_STEP = 94;


  {
    ER_UNKNOWN_STEP,
       "getNextStepPos \u306b\u304a\u3051\u308b\u30d7\u30ed\u30b0\u30e9\u30de\u306e\u8868\u660e: \u672a\u77e5\u306e stepType: {0}"},


  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.

  /** Problem with RelativeLocationPath */
  //public static final int ER_EXPECTED_REL_LOC_PATH = 95;


  {
    ER_EXPECTED_REL_LOC_PATH,
       "\u30c8\u30fc\u30af\u30f3 '/' \u307e\u305f\u306f '//' \u306e\u5f8c\u306b\u7d9a\u304f\u3079\u304d\u76f8\u5bfe\u30ed\u30b1\u30fc\u30b7\u30e7\u30f3\u30d1\u30b9\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},


  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.

  /** Problem with LocationPath */
  //public static final int ER_EXPECTED_LOC_PATH = 96;


  {
    ER_EXPECTED_LOC_PATH,
       "\u30ed\u30b1\u30fc\u30b7\u30e7\u30f3\u30d1\u30b9\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b\u6b21\u306e\u30c8\u30fc\u30af\u30f3\u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f:  {0}"},


  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.

  /** Problem with Step */
  //public static final int ER_EXPECTED_LOC_STEP = 97;


  {
    ER_EXPECTED_LOC_STEP,
       "\u30c8\u30fc\u30af\u30f3 '/' \u307e\u305f\u306f '//' \u306e\u5f8c\u306b\u7d9a\u304f\u3079\u304d\u30ed\u30b1\u30fc\u30b7\u30e7\u30f3\u30b9\u30c6\u30c3\u30d7\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002"},


  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.

  /** Problem with NodeTest */
  //public static final int ER_EXPECTED_NODE_TEST = 98;


  {
    ER_EXPECTED_NODE_TEST,
       "NCName:* \u307e\u305f\u306f QName \u306e\u5f62\u5f0f\u306b\u4e00\u81f4\u3059\u308b\u30ce\u30fc\u30c9\u30c6\u30b9\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002"},


  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.

  /** Expected step pattern */
  //public static final int ER_EXPECTED_STEP_PATTERN = 99;


  {
    ER_EXPECTED_STEP_PATTERN,
       "\u30b9\u30c6\u30c3\u30d7\u30d1\u30bf\u30fc\u30f3\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b '/' \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u3002"},


  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
 
  /** Expected relative path pattern */
  //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;


  {
    ER_EXPECTED_REL_PATH_PATTERN,
       "\u76f8\u5bfe\u30d1\u30b9\u30d1\u30bf\u30fc\u30f3\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002"},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;


  {
    ER_ARG_LOCALNAME_INVALID,
       "QNAME \u5185\u306e\u30ed\u30fc\u30ab\u30eb\u540d\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093"},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;


  {
    ER_ARG_PREFIX_INVALID,
       "QNAME \u5185\u306e\u63a5\u982d\u8f9e\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093"},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
  //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;


  {
    ER_CANT_CONVERT_TO_BOOLEAN,
       "{0} \u3092 boolean \u5024\u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093\u3002"},


  // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
  // FIRST_ORDERED_NODE_TYPE.

  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;


  {
    ER_CANT_CONVERT_TO_SINGLENODE,
       "{0} \u3092\u5358\u4e00\u306e\u30ce\u30fc\u30c9\u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093\u3002\u3053\u306e\u53d6\u5f97\u30e1\u30bd\u30c3\u30c9\u3092\u9069\u7528\u3067\u304d\u308b\u578b\u306f\u3001ANY_UNORDERED_NODE_TYPE \u3068 FIRST_ORDERED_NODE_TYPE \u3067\u3059\u3002"},


  // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;


  {
    ER_CANT_GET_SNAPSHOT_LENGTH,
       "\u578b {0} \u306e\u30b9\u30ca\u30c3\u30d7\u30b7\u30e7\u30c3\u30c8\u9577\u3092\u53d6\u5f97\u3067\u304d\u307e\u305b\u3093\u3002\u3053\u306e\u53d6\u5f97\u30e1\u30bd\u30c3\u30c9\u3092\u9069\u7528\u3067\u304d\u308b\u578b\u306f\u3001UNORDERED_NODE_SNAPSHOT_TYPE \u3068 ORDERED_NODE_SNAPSHOT_TYPE \u3067\u3059\u3002"},


  /** Field ER_NON_ITERATOR_TYPE                */
  //public static final int ER_NON_ITERATOR_TYPE        = 106;


  {
    ER_NON_ITERATOR_TYPE,
       "\u975e iterator \u578b {0} \u306b\u5bfe\u3057\u3066\u7e70\u308a\u8fd4\u3057\u51e6\u7406\u3092\u5b9f\u884c\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},


  // Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.

  /** Field ER_DOC_MUTATED                      */
  //public static final int ER_DOC_MUTATED              = 107;


  {
    ER_DOC_MUTATED,
       "\u7d50\u679c\u304c\u8fd4\u3055\u308c\u305f\u5f8c\u306b\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u304c\u5909\u66f4\u3055\u308c\u305f\u305f\u3081\u3001\u73fe\u5728\u306e\u53cd\u5fa9\u5b50\u306f\u7121\u52b9\u306b\u306a\u308a\u307e\u3057\u305f\u3002"},


  /** Field ER_INVALID_XPATH_TYPE               */
  //public static final int ER_INVALID_XPATH_TYPE       = 108;


  {
    ER_INVALID_XPATH_TYPE,
       "\u7121\u52b9\u306a XPath \u578b\u306e\u5f15\u6570: {0}"},


  /** Field ER_EMPTY_XPATH_RESULT                */
  //public static final int ER_EMPTY_XPATH_RESULT       = 109;


  {
    ER_EMPTY_XPATH_RESULT,
       "\u7a7a\u306e XPath \u7d50\u679c\u30aa\u30d6\u30b8\u30a7\u30af\u30c8"},


  /** Field ER_INCOMPATIBLE_TYPES                */
  //public static final int ER_INCOMPATIBLE_TYPES       = 110;


  {
    ER_INCOMPATIBLE_TYPES,
       "\u623b\u308a\u5024\u306e\u578b {0} \u306f\u6307\u5b9a\u3055\u308c\u305f\u578b {1} \u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093"},


  /** Field ER_NULL_RESOLVER                     */
  //public static final int ER_NULL_RESOLVER            = 111;


  {
    ER_NULL_RESOLVER,
       "\u63a5\u982d\u8f9e\u30ea\u30be\u30eb\u30d0\u304c null \u3067\u3042\u3063\u305f\u305f\u3081\u3001\u63a5\u982d\u8f9e\u306e\u89e3\u6c7a\u306b\u5931\u6557\u3057\u307e\u3057\u305f\u3002"},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_STRING            */
  //public static final int ER_CANT_CONVERT_TO_STRING   = 112;


  {
    ER_CANT_CONVERT_TO_STRING,
       "{0} \u3092\u6587\u5b57\u5217\u5024\u306b\u5909\u63db\u3067\u304d\u307e\u305b\u3093\u3002"},


  // Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_NON_SNAPSHOT_TYPE                 */
  //public static final int ER_NON_SNAPSHOT_TYPE       = 113;


  {
    ER_NON_SNAPSHOT_TYPE,
       "\u578b {0} \u306e snapshotItem \u3092\u547c\u3073\u51fa\u305b\u307e\u305b\u3093\u3002\u3053\u306e\u30e1\u30bd\u30c3\u30c9\u3092\u547c\u3073\u51fa\u305b\u308b\u578b\u306f\u3001UNORDERED_NODE_SNAPSHOT_TYPE \u3068 ORDERED_NODE_SNAPSHOT_TYPE \u3067\u3059\u3002"},


  // Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.

  /** Field ER_WRONG_DOCUMENT                    */
  //public static final int ER_WRONG_DOCUMENT          = 114;


  {
    ER_WRONG_DOCUMENT,
       "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u30ce\u30fc\u30c9\u304c\u3001\u3053\u306e XPathEvaluator \u306b\u30d0\u30a4\u30f3\u30c9\u3055\u308c\u305f\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306b\u6240\u5c5e\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},


  // Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  /** Field ER_WRONG_NODETYPE                    */
  //public static final int ER_WRONG_NODETYPE          = 115;


  {
    ER_WRONG_NODETYPE ,
       "\u3053\u306e\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u30ce\u30fc\u30c9\u578b\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},


  /** Field ER_XPATH_ERROR                       */
  //public static final int ER_XPATH_ERROR             = 116;


  {
    ER_XPATH_ERROR ,
       "XPath \u5185\u306b\u304a\u3051\u308b\u672a\u77e5\u306e\u30a8\u30e9\u30fc"},

 


  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  {
    WG_LOCALE_NAME_NOT_HANDLED,
      "format-number \u95a2\u6570\u3067\u30ed\u30b1\u30fc\u30eb\u540d\u306f\u307e\u3060\u51e6\u7406\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "locale name in the format-number function not yet handled!"},


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  {
    WG_PROPERTY_NOT_SUPPORTED,
      "XSL \u30d7\u30ed\u30d1\u30c6\u30a3\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}"},
//      "XSL Property not supported: {0}"},


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  {
    WG_DONT_DO_ANYTHING_WITH_NS,
      "\u540d\u524d\u7a7a\u9593 {0} \u306e\u30d7\u30ed\u30d1\u30c6\u30a3 {1} \u306b\u306f\u73fe\u5728\u4f55\u3082\u884c\u306a\u3063\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//      "Do not currently do anything with namespace {0} in property: {1}"},


  /** Field WG_SECURITY_EXCEPTION          */
  //public static final int WG_SECURITY_EXCEPTION = 4;


  {
    WG_SECURITY_EXCEPTION,
      "XSL \u30b7\u30b9\u30c6\u30e0\u30d7\u30ed\u30d1\u30c6\u30a3\u306b\u30a2\u30af\u30bb\u30b9\u3057\u3088\u3046\u3068\u3057\u305f\u3068\u304d\u306b SecurityException \u304c\u767a\u751f\u3057\u307e\u3057\u305f: {0}"},
//      "SecurityException when trying to access XSL system property: {0}"},


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  {
    WG_QUO_NO_LONGER_DEFINED,
      "\u53e4\u3044\u69cb\u6587: quo(...) \u306f XPath \u3067\u306f\u3082\u3046\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Old syntax: quo(...) is no longer defined in XPath."},


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  {
    WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "XPath \u306f nodeTest \u3092\u5b9f\u88c5\u3059\u308b\u305f\u3081\u306b\u62bd\u51fa\u3055\u308c\u305f\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//      "XPath needs a derived object to implement nodeTest!"},


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  {
    WG_FUNCTION_TOKEN_NOT_FOUND,
      "\u95a2\u6570\u30c8\u30fc\u30af\u30f3\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002"},
//      "function token not found."},


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  {
    WG_COULDNOT_FIND_FUNCTION,
      "\u95a2\u6570 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//      "Could not find function: {0}"},


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "\u3053\u3053\u304b\u3089 URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093: {0}"},
//      "Can not make URL from: {0}"},


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  {
    WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "DTM \u30d1\u30fc\u30b5\u3067 -E \u30aa\u30d7\u30b7\u30e7\u30f3\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//      "-E option not supported for DTM parser"},


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  {
    WG_ILLEGAL_VARIABLE_REFERENCE,
      "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u306e\u5916\u3067\u3001\u307e\u305f\u306f\u5b9a\u7fa9\u306a\u3057\u3067\u3001\u5909\u6570\u306b VariableReference \u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f\u3002Name = {0}"},
//      "VariableReference given for variable out of context or without definition!  Name = {0}"},


  /** Field WG_UNSUPPORTED_ENCODING          */
  //public static final int WG_UNSUPPORTED_ENCODING = 12;


  {
    WG_UNSUPPORTED_ENCODING, "\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u306a\u3044\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0: {0}"},
//    WG_UNSUPPORTED_ENCODING, "Unsupported encoding: {0}"},


  // Other miscellaneous text used inside the code...
  { "ui_language", "ja"},
  { "help_language", "ja"},
  { "language", "ja"},
    { "BAD_CODE",
      "createMessage \u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u304c\u7bc4\u56f2\u5916\u3067\u3057\u305f"},
    { "FORMAT_FAILED",
      "messageFormat \u547c\u3073\u51fa\u3057\u4e2d\u306b\u4f8b\u5916\u304c\u30b9\u30ed\u30fc\u3055\u308c\u307e\u3057\u305f"},
    { "version", ">>>>>>> Xalan \u30d0\u30fc\u30b8\u30e7\u30f3 "},
    { "version2", "<<<<<<<"},
    { "yes", "\u306f\u3044"},
    { "line", "\u884c\u756a\u53f7 //"},
    { "column", "\u5217\u756a\u53f7 //"},
    { "xsldone", "XSLProcessor: \u7d42\u4e86"},
    { "xpath_option", "xpath \u30aa\u30d7\u30b7\u30e7\u30f3: "},
    { "optionIN", "   [-in inputXMLURL]"},
    { "optionSelect", "   [-select xpath \u5f0f]"},
    { "optionMatch",
      "   [-match \u4e00\u81f4\u30d1\u30bf\u30fc\u30f3 (\u7167\u5408\u8a3a\u65ad\u7528)]"},
    { "optionAnyExpr",
      "\u3082\u3057\u304f\u306f\u3001\u305f\u3060 xpath \u5f0f\u304c\u8a3a\u65ad\u7528\u30c0\u30f3\u30d7\u3092\u884c\u3046\u306e\u307f"},
    { "noParsermsg1",
    "XSL \u30d7\u30ed\u30bb\u30b9\u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
    { "noParsermsg2",
    "** \u30d1\u30fc\u30b5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f **"},
    { "noParsermsg3",
    "\u30af\u30e9\u30b9\u30d1\u30b9\u3092\u30c1\u30a7\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
    { "noParsermsg4",
      "Java \u7528\u306b IBM \u306e XML \u30d1\u30fc\u30b5\u3092\u5099\u3048\u3066\u3044\u306a\u3044\u5834\u5408\u306f\u3001\u3053\u308c\u3092\u4ee5\u4e0b\u304b\u3089\u30c0\u30a6\u30f3\u30ed\u30fc\u30c9\u3067\u304d\u307e\u3059\u3002"},
    { "noParsermsg5",
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
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
  public static final String ERROR_HEADER = "Error: ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "Warning: ";

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
