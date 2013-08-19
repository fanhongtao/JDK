/*
 * @(#)XSLTErrorResources_ja.java	1.9 02/03/26
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
package org.apache.xalan.res;

import org.apache.xml.utils.res.XResourceBundleBase;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

import java.text.DecimalFormat;

import org.apache.xalan.templates.Constants;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING). The array will be
 * automatically filled in with the keys, but you need to
 * fill in the actual message string. Follow the instructions
 * below.
 */
public class XSLTErrorResources_ja extends XSLTErrorResources
{

  /** The error suffix for construction error property keys.   */
  public static final String ERROR_SUFFIX = "ER";

  /** The warning suffix for construction error property keys.   */
  public static final String WARNING_SUFFIX = "WR";

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 216;          

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 26;

  /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 45;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;

  /** The lookup table for error messages.   */
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

  /** Error message ID that has a null message, but takes in a single object.    */
  public static final int ERROR0000 = 0;

  static
  {
    contents[ERROR0000][1] = "{0}";
  }

  /** ER_NO_CURLYBRACE          */
  public static final int ER_NO_CURLYBRACE = 1;

  static
  {
    contents[ER_NO_CURLYBRACE][1] =
      "\u30a8\u30e9\u30fc: \u5f0f\u306e\u4e2d\u3067 '{' \u3092\u6301\u3064\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093";
//      "Error: Can not have '{' within expression";
  }

  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059: {1}";
//    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0} has an illegal attribute: {1}";
  }

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  static
  {
    contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] =
      "xsl:apply-imports \u3067 sourceNode \u304c null \u3067\u3059";
//      "sourceNode is null in xsl:apply-imports!";
  }

  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;

  static
  {
    contents[ER_CANNOT_ADD][1] = "{0} \u3092 {1} \u306b\u8ffd\u52a0\u3067\u304d\u307e\u305b\u3093";
//    contents[ER_CANNOT_ADD][1] = "Can not add {0} to {1}";
  }

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  static
  {
    contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] =
      "handleApplyTemplatesInstruction \u3067 sourceNode \u304c null \u3067\u3059\u3002";
//      "sourceNode is null in handleApplyTemplatesInstruction!";
  }

  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;

  static
  {
    contents[ER_NO_NAME_ATTRIB][1] = "{0} \u306b\u306f\u540d\u524d\u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//    contents[ER_NO_NAME_ATTRIB][1] = "{0} must have a name attribute.";
  }

  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  static
  {
    contents[ER_TEMPLATE_NOT_FOUND][1] = "{0} \u3068\u3044\u3046\u540d\u524d\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//    contents[ER_TEMPLATE_NOT_FOUND][1] = "Could not find template named: {0}";
  }

  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  static
  {
    contents[ER_CANT_RESOLVE_NAME_AVT][1] =
      "xls:call-template \u3067\u540d\u524d AVT \u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//      "Could not resolve name AVT in xsl:call-template.";
  }

  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;

  static
  {
    contents[ER_REQUIRES_ATTRIB][1] = "{0} \u306b\u306f\u5c5e\u6027 {1} \u304c\u5fc5\u8981\u3067\u3059:";
//    contents[ER_REQUIRES_ATTRIB][1] = "{0} requires attribute: {1}";
  }

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  static
  {
    contents[ER_MUST_HAVE_TEST_ATTRIB][1] =
      "{0} \u306b\u306f 'test' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "{0} must have a 'test' attribute.";
  }

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  static
  {
    contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] =
      "\u30ec\u30d9\u30eb\u5c5e\u6027\u306b\u4e0d\u6b63\u306a\u5024\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059: {0}";
//      "Bad value on level attribute: {0}";
  }

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] =
      "processing-instruction \u540d\u306f 'xml' \u306b\u3067\u304d\u307e\u305b\u3093";
//      "processing-instruction name can not be 'xml'";
  }

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] =
      "processing-instruction \u540d\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093: {0}";
//      "processing-instruction name must be a valid NCName: {0}";
  }

  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;

  static
  {
    contents[ER_NEED_MATCH_ATTRIB][1] =
      "{0} \u306b\u30e2\u30fc\u30c9\u304c\u3042\u308b\u5834\u5408\u3001\u4e00\u81f4\u3059\u308b\u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093\u3002";
//      "{0} must have a match attribute if it has a mode.";
  }

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  static
  {
    contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] =
      "{0} \u306f name \u5c5e\u6027\u304b\u3001\u307e\u305f\u306f match \u5c5e\u6027\u3092\u5fc5\u8981\u3068\u3057\u307e\u3059\u3002";
//      "{0} requires either a name or a match attribute.";
  }

  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  static
  {
    contents[ER_CANT_RESOLVE_NSPREFIX][1] =
      "\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e {0} \u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093";
//      "Can not resolve namespace prefix: {0}";
  }

  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;

  static
  {
    contents[ER_ILLEGAL_VALUE][1] = "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}";
//    contents[ER_ILLEGAL_VALUE][1] = "xml:space has an illegal value: {0}";
  }

  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;

  static
  {
    contents[ER_NO_OWNERDOC][1] =
      "\u5b50\u30ce\u30fc\u30c9\u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u307e\u305b\u3093\u3002";
//      "Child node does not have an owner document!";
  }

  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  static
  {
    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement \u30a8\u30e9\u30fc: {0}";
//    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement error: {0}";
  }

  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;

  static
  {
    contents[ER_NULL_CHILD][1] = "null \u3067\u3042\u308b\u5b50\u3092\u8ffd\u52a0\u3057\u3066\u3044\u307e\u3059\u3002";
//    contents[ER_NULL_CHILD][1] = "Trying to add a null child!";
  }

  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;

  static
  {
    contents[ER_NEED_SELECT_ATTRIB][1] = "{0} \u306f select \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//    contents[ER_NEED_SELECT_ATTRIB][1] = "{0} requires a select attribute.";
  }

  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;

  static
  {
    contents[ER_NEED_TEST_ATTRIB][1] =
      "xsl:when \u306b\u306f 'test' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:when must have a 'test' attribute.";
  }

  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;

  static
  {
    contents[ER_NEED_NAME_ATTRIB][1] =
      "xsl:with-param \u306b\u306f 'name' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:with-param must have a 'name' attribute.";
  }

  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  static
  {
    contents[ER_NO_CONTEXT_OWNERDOC][1] =
      "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u307e\u305b\u3093\u3002";
//      "context does not have an owner document!";
  }

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  static
  {
    contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] =
      "XML TransformerFactory Liaison {0} \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//      "Could not create XML TransformerFactory Liaison: {0}";
  }

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  static
  {
    contents[ER_PROCESS_NOT_SUCCESSFUL][1] =
      "Xalan: \u30d7\u30ed\u30bb\u30b9\u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//      "Xalan: Process was not successful.";
  }

  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;

  static
  {
    contents[ER_NOT_SUCCESSFUL][1] = "Xalan: \u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//    contents[ER_NOT_SUCCESSFUL][1] = "Xalan: was not successful.";
  }

  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  static
  {
    contents[ER_ENCODING_NOT_SUPPORTED][1] = "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}";
//    contents[ER_ENCODING_NOT_SUPPORTED][1] = "Encoding not supported: {0}";
  }

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  static
  {
    contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] =
      "TraceListener \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f: {0}";
//      "Could not create TraceListener: {0}";
  }

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  static
  {
    contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] =
      "xsl:key \u306b\u306f 'name' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:key requires a 'name' attribute!";
  }

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  static
  {
    contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] =
      "xsl:key \u306b\u306f 'match' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:key requires a 'match' attribute!";
  }

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  static
  {
    contents[ER_KEY_REQUIRES_USE_ATTRIB][1] =
      "xsl:key \u306b\u306f 'use' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//     "xsl:key requires a 'use' attribute!";
  }

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  static
  {
    contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] =
      "(StylesheetHandler) {0} \u306b\u306f 'elements' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "(StylesheetHandler) {0} requires an 'elements' attribute!";
  }

  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  static
  {
    contents[ER_MISSING_PREFIX_ATTRIB][1] =
      "(StylesheetHandler) {0} \u5c5e\u6027\u306b 'prefix' \u304c\u8db3\u308a\u307e\u305b\u3093";
//      "(StylesheetHandler) {0} attribute 'prefix' is missing";
  }

  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;

  static
  {
    contents[ER_BAD_STYLESHEET_URL][1] = "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e URL \u304c\u4e0d\u6b63\u3067\u3059: {0}";
//    contents[ER_BAD_STYLESHEET_URL][1] = "Stylesheet URL is bad: {0}";
  }

  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;

  static
  {
    contents[ER_FILE_NOT_FOUND][1] = "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u30d5\u30a1\u30a4\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f: {0}";
//    contents[ER_FILE_NOT_FOUND][1] = "Stylesheet file was not found: {0}";
  }

  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;

  static
  {
    contents[ER_IOEXCEPTION][1] =
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u30d5\u30a1\u30a4\u30eb\u3067\u5165\u51fa\u529b\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f: {0}";
//      "Had IO Exception with stylesheet file: {0}";
  }

  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;

  static
  {
    contents[ER_NO_HREF_ATTRIB][1] =
      "(StylesheetHandler) {0} \u306e href \u5c5e\u6027\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f";
//      "(StylesheetHandler) Could not find href attribute for {0}";
  }

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  static
  {
    contents[ER_STYLESHEET_INCLUDES_ITSELF][1] =
      "(StylesheetHandler) {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u30a4\u30f3\u30af\u30eb\u30fc\u30c9\u3057\u3066\u3044\u307e\u3059\u3002";
//      "(StylesheetHandler) {0} is directly or indirectly including itself!";
  }

  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  static
  {
    contents[ER_PROCESSINCLUDE_ERROR][1] =
      "StylesheetHandler.processInclude \u30a8\u30e9\u30fc\u3001{0}";
//      "StylesheetHandler.processInclude error, {0}";
  }

  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;

  static
  {
    contents[ER_MISSING_LANG_ATTRIB][1] =
      "(StylesheetHandler) {0} \u5c5e\u6027 'lang' \u304c\u8db3\u308a\u307e\u305b\u3093";
//      "(StylesheetHandler) {0} attribute 'lang' is missing";
  }

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  static
  {
    contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] =
      "(StylesheetHandler) {0} \u8981\u7d20\u3092\u914d\u7f6e\u3057\u5fd8\u308c\u3066\u3044\u307e\u305b\u3093\u304b?? \u30b3\u30f3\u30c6\u30ca\u8981\u7d20 'component' \u304c\u8db3\u308a\u307e\u305b\u3093";
//      "(StylesheetHandler) misplaced {0} element?? Missing container element 'component'";
  }

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  static
  {
    contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] =
      "Element\u3001DocumentFragment\u3001Document\u3001\u307e\u305f\u306f PrintWriter \u306b\u3060\u3051\u51fa\u529b\u3067\u304d\u307e\u3059\u3002";
//      "Can only output to an Element, DocumentFragment, Document, or PrintWriter.";
  }

  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;

  static
  {
    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process \u30a8\u30e9\u30fc";
//    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process error";
  }

  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;

  static
  {
    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode \u30a8\u30e9\u30fc: {0}";
//    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode error: {0}";
  }

  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;

  static
  {
    contents[ER_NO_SELECT_EXPRESSION][1] =
      "\u30a8\u30e9\u30fc\u3002xpath \u306e\u9078\u629e\u5f0f (-select) \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//      "Error! Did not find xpath select expression (-select).";
  }

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  static
  {
    contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] =
      "XSLProcessor \u3092\u76f4\u5217\u5316\u3067\u304d\u307e\u305b\u3093\u3002";
//      "Can not serialize an XSLProcessor!";
  }

  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;

  static
  {
    contents[ER_NO_INPUT_STYLESHEET][1] =
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u5165\u529b\u304c\u6307\u5b9a\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//      "Stylesheet input was not specified!";
  }

  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  static
  {
    contents[ER_FAILED_PROCESS_STYLESHEET][1] =
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u3067\u306e\u51e6\u7406\u306b\u5931\u6557\u3057\u307e\u3057\u305f\u3002";
//      "Failed to process stylesheet!";
  }

  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;

  static
  {
    contents[ER_COULDNT_PARSE_DOC][1] = "{0} \u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u69cb\u6587\u89e3\u6790\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//    contents[ER_COULDNT_PARSE_DOC][1] = "Could not parse {0} document!";
  }

  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  static
  {
    contents[ER_COULDNT_FIND_FRAGMENT][1] = "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f";
//    contents[ER_COULDNT_FIND_FRAGMENT][1] = "Could not find fragment: {0}";
  }

  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;

  static
  {
    contents[ER_NODE_NOT_ELEMENT][1] =
      "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u8b58\u5225\u5b50\u304c\u6307\u3059\u30ce\u30fc\u30c9\u304c\u8981\u7d20\u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3067\u3057\u305f: {0}";
//      "Node pointed to by fragment identifier was not an element: {0}";
  }

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  static
  {
    contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "for-each \u306f match \u5c5e\u6027\u307e\u305f\u306f name \u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//      "for-each must have either a match or name attribute";
  }

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  static
  {
    contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306f match \u5c5e\u6027\u307e\u305f\u306f name \u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//      "templates must have either a match or name attribute";
  }

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  static
  {
    contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] =
      "\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306e\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306b\u30af\u30ed\u30fc\u30f3\u304c\u3042\u308a\u307e\u305b\u3093\u3002";
//      "No clone of a document fragment!";
  }

  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;

  static
  {
    contents[ER_CANT_CREATE_ITEM][1] =
      "\u7d50\u679c\u30c4\u30ea\u30fc\u306b\u9805\u76ee\u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093: {0}";
//      "Can not create item in result tree: {0}";
  }

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VALUE][1] =
      "\u30bd\u30fc\u30b9 XML \u306e xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}";
//      "xml:space in the source XML has an illegal value: {0}";
  }

  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  static
  {
    contents[ER_NO_XSLKEY_DECLARATION][1] =
      "{0} \u306b xsl:key \u5ba3\u8a00\u304c\u3042\u308a\u307e\u305b\u3093\u3002";
//      "There is no xsl:key declaration for {0}!";
  }

  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;

  static
  {
    contents[ER_CANT_CREATE_URL][1] = "\u30a8\u30e9\u30fc\u3002{0} \u306e URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093";
//    contents[ER_CANT_CREATE_URL][1] = "Error! Cannot create url for: {0}";
  }

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  static
  {
    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "xsl:functions \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "xsl:functions is unsupported";
  }

  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;

  static
  {
    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory \u30a8\u30e9\u30fc";
//    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory Error";
  }

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] =
      "(StylesheetHandler) {0} \u306f\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u5185\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002";
//      "(StylesheetHandler) {0} not allowed inside a stylesheet!";
  }

  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  static
  {
    contents[ER_RESULTNS_NOT_SUPPORTED][1] =
      "result-ns \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:output \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002";
//      "result-ns no longer supported!  Use xsl:output instead.";
  }

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  static
  {
    contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] =
      "default-space \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:strip-space \u307e\u305f\u306f xsl:preserve-space \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002";
//      "default-space no longer supported!  Use xsl:strip-space or xsl:preserve-space instead.";
  }

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  static
  {
    contents[ER_INDENTRESULT_NOT_SUPPORTED][1] =
      "indent-result \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:output \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002";
//      "indent-result no longer supported!  Use xsl:output instead.";
  }

  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;

  static
  {
    contents[ER_ILLEGAL_ATTRIB][1] =
      "(StylesheetHandler) {0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u3042\u308a\u307e\u3059: {1}";
//      "(StylesheetHandler) {0} has an illegal attribute: {1}";
  }

  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  static
  {
    contents[ER_UNKNOWN_XSL_ELEM][1] = "\u672a\u77e5\u306e XSL \u8981\u7d20: {0}";
//    contents[ER_UNKNOWN_XSL_ELEM][1] = "Unknown XSL element: {0}";
  }

  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;

  static
  {
    contents[ER_BAD_XSLSORT_USE][1] =
      "(StylesheetHandler) xsl:sort \u306f xsl:apply-templates \u307e\u305f\u306f xsl:for-each \u3068\u3044\u3063\u3057\u3087\u306b\u306e\u307f\u4f7f\u7528\u3067\u304d\u307e\u3059\u3002";
//      "(StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each.";
  }

  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;

  static
  {
    contents[ER_MISPLACED_XSLWHEN][1] =
      "(StylesheetHandler) xsl:when \u306e\u914d\u7f6e\u304c\u8aa4\u3063\u3066\u3044\u307e\u3059\u3002";
//      "(StylesheetHandler) misplaced xsl:when!";
  }

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  static
  {
    contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:when \u306e\u89aa\u306f xsl:choose \u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002";
//      "(StylesheetHandler) xsl:when not parented by xsl:choose!";
  }

  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  static
  {
    contents[ER_MISPLACED_XSLOTHERWISE][1] =
      "(StylesheetHandler) xsl:otherwise \u306e\u914d\u7f6e\u304c\u8aa4\u3063\u3066\u3044\u307e\u3059\u3002";
//      "(StylesheetHandler) misplaced xsl:otherwise!";
  }

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  static
  {
    contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:otherwise \u306e\u89aa\u306f xsl:choose \u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002";
//      "(StylesheetHandler) xsl:otherwise not parented by xsl:choose!";
  }

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] =
      "(StylesheetHandler) {0} \u306f\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u4e2d\u306b\u7f6e\u304f\u3053\u3068\u3092\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002";
//      "(StylesheetHandler) {0} is not allowed inside a template!";
  }

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  static
  {
    contents[ER_UNKNOWN_EXT_NS_PREFIX][1] =
      "(StylesheetHandler) {0} \u62e1\u5f35\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e {1} \u304c\u672a\u77e5\u3067\u3059";
//      "(StylesheetHandler) {0} extension namespace prefix {1} unknown";
  }

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  static
  {
    contents[ER_IMPORTS_AS_FIRST_ELEM][1] =
      "(StylesheetHandler) \u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u6700\u521d\u306e\u8981\u7d20\u3067\u306e\u307f\u30a4\u30f3\u30dd\u30fc\u30c8\u304c\u5b9f\u884c\u53ef\u80fd\u3067\u3059\u3002";
//      "(StylesheetHandler) Imports can only occur as the first elements in the stylesheet!";
  }

  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;

  static
  {
    contents[ER_IMPORTING_ITSELF][1] =
      "(StylesheetHandler) {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u3066\u3044\u307e\u3059\u3002";
//      "(StylesheetHandler) {0} is directly or indirectly importing itself!";
  }

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VAL][1] =
      "(StylesheetHandler) " + "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}";
//      "(StylesheetHandler) " + "xml:space has an illegal value: {0}";
  }

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  static
  {
    contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] =
      "processStylesheet \u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3002";
//      "processStylesheet not succesfull!";
  }

  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;

  static
  {
    contents[ER_SAX_EXCEPTION][1] = "SAX \u4f8b\u5916";
//    contents[ER_SAX_EXCEPTION][1] = "SAX Exception";
  }

  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  static
  {
    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "Function \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002";
//    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "Function not supported!";
  }

  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;

  static
  {
    contents[ER_XSLT_ERROR][1] = "XSLT \u30a8\u30e9\u30fc";
//    contents[ER_XSLT_ERROR][1] = "XSLT Error";
  }

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  static
  {
    contents[ER_CURRENCY_SIGN_ILLEGAL][1] =
      "\u901a\u8ca8\u8a18\u53f7\u306f\u66f8\u5f0f\u30d1\u30bf\u30fc\u30f3\u6587\u5b57\u5217\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002";
//      "currency sign is not allowed in format pattern string";
  }

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  static
  {
    contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] =
      "Document \u95a2\u6570\u306f Stylesheet DOM \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002";
//      "Document function not supported in Stylesheet DOM!";
  }

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  static
  {
    contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] =
      "non-Prefix \u30ea\u30be\u30eb\u30d0\u306e\u63a5\u982d\u8f9e\u306f\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093\u3002";
//      "Can't resolve prefix of non-Prefix resolver!";
  }

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  static
  {
    contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] =
      "Rediret \u62e1\u5f35: \u30d5\u30a1\u30a4\u30eb\u540d\u3092\u53d6\u5f97\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f - file \u5c5e\u6027\u307e\u305f\u306f select \u5c5e\u6027\u304c\u6709\u52b9\u306a\u6587\u5b57\u5217\u3092\u623b\u3059\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002 ";
//      "Redirect extension: Could not get filename - file or select attribute must return vald string.";
  }

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  static
  {
    contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] =
      "Redirect \u62e1\u5f35\u3067 FormatterListener \u3092\u69cb\u7bc9\u3067\u304d\u307e\u305b\u3093\u3002";
//      "Can not build FormatterListener in Redirect extension!";
  }

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  static
  {
    contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] =
      "exclude-result-prefixes \u306e\u63a5\u982d\u8f9e\u304c\u6709\u52b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093: {0}";
//      "Prefix in exclude-result-prefixes is not valid: {0}";
  }

  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;

  static
  {
    contents[ER_MISSING_NS_URI][1] =
      "\u6307\u5b9a\u3055\u308c\u305f\u63a5\u982d\u8f9e\u306e\u540d\u524d\u7a7a\u9593 URI \u304c\u3042\u308a\u307e\u305b\u3093";
//      "Missing namespace URI for specified prefix";
  }

  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  static
  {
    contents[ER_MISSING_ARG_FOR_OPTION][1] =
      "\u30aa\u30d7\u30b7\u30e7\u30f3\u306e\u5f15\u6570\u304c\u3042\u308a\u307e\u305b\u3093: {0}";
//      "Missing argument for option: {0}";
  }

  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;

  static
  {
    contents[ER_INVALID_OPTION][1] = "\u7121\u52b9\u306a\u30aa\u30d7\u30b7\u30e7\u30f3: {0}";
//    contents[ER_INVALID_OPTION][1] = "Invalid option: {0}";
  }

  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  static
  {
    contents[ER_MALFORMED_FORMAT_STRING][1] = "\u5f62\u5f0f\u306e\u8aa4\u3063\u305f\u6587\u5b57\u5217: {0}";
//    contents[ER_MALFORMED_FORMAT_STRING][1] = "Malformed format string: {0}";
  }

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  static
  {
    contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] =
      "xsl:stylesheet \u306b\u306f 'version' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:stylesheet requires a 'version' attribute!";
  }

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] =
      "\u5c5e\u6027: {0} \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {1}";
//      "Attribute: {0} has an illegal value: {1}";
  }

  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  static
  {
    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose \u306b\u306f xsl:when \u304c\u5fc5\u8981\u3067\u3059";
//    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose requires an xsl:when";
  }

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  static
  {
    contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] =
      "xsl:apply-imports \u306f xsl:for-each \u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093";
//      "xsl:apply-imports not allowed in a xsl:for-each";
  }

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] =
      "\u51fa\u529b DOM \u30ce\u30fc\u30c9\u306b DTMLiaison \u3092\u4f7f\u7528\u3067\u304d\u307e\u305b\u3093... \u4ee3\u308f\u308a\u306b org.apache.xpath.DOM2Helper \u3092\u6e21\u3057\u307e\u3059\u3002";
//      "Cannot use a DTMLiaison for an output DOM node... pass a org.apache.xpath.DOM2Helper instead!";
  }

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_INPUT][1] =
      "\u5165\u529b DOM \u30ce\u30fc\u30c9\u306b DTMLiaison \u3092\u4f7f\u7528\u3067\u304d\u307e\u305b\u3093... \u4ee3\u308f\u308a\u306b org.apache.xpath.DOM2Helper \u3092\u6e21\u3057\u307e\u3059\u3002";
//      "Cannot use a DTMLiaison for a input DOM node... pass a org.apache.xpath.DOM2Helper instead!";
  }

  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;

  static
  {
    contents[ER_CALL_TO_EXT_FAILED][1] =
      "\u62e1\u5f35\u8981\u7d20\u306e\u547c\u3073\u51fa\u3057\u306b\u5931\u6557\u3057\u307e\u3057\u305f: {0}";
//      "Call to extension element failed: {0}";
  }

  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "\u63a5\u982d\u8f9e\u306f\u540d\u524d\u7a7a\u9593\u306b\u5909\u308f\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059: {0}";
//      "Prefix must resolve to a namespace: {0}";
  }

  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "\u7121\u52b9\u306a UTF-16 \u4ee3\u7406\u304c\u691c\u51fa\u3055\u308c\u307e\u3057\u305f: {0} ?";
//      "Invalid UTF-16 surrogate detected: {0} ?";
  }

  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  static
  {
    contents[ER_XSLATTRSET_USED_ITSELF][1] =
      "xsl:attribute-set {0} \u306f\u81ea\u8eab\u3092\u4f7f\u7528\u3057\u305f\u305f\u3081\u3001\u7121\u9650\u30eb\u30fc\u30d7\u304c\u767a\u751f\u3057\u307e\u3059\u3002";
//      "xsl:attribute-set {0} used itself, which will cause an infinite loop.";
  }

  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  static
  {
    contents[ER_CANNOT_MIX_XERCESDOM][1] =
      "\u975e Xerces-DOM \u5165\u529b\u3068 Xerces-DOM \u51fa\u529b\u3092\u6df7\u5408\u3067\u304d\u307e\u305b\u3093\u3002";
//      "Can not mix non Xerces-DOM input with Xerces-DOM output!";
  }

  /** ER_TOO_MANY_LISTENERS          */
  public static final int ER_TOO_MANY_LISTENERS = 103;

  static
  {
    contents[ER_TOO_MANY_LISTENERS][1] =
      "addTraceListenersToStylesheet - TooManyListenersException";
//      "addTraceListenersToStylesheet - TooManyListenersException";
  }

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;

  static
  {
    contents[ER_IN_ELEMTEMPLATEELEM_READOBJECT][1] =
      "ElemTemplateElement.readObject \u306b {0} \u304c\u3042\u308a\u307e\u3059";
//      "In ElemTemplateElement.readObject: {0}";
  }

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  static
  {
    contents[ER_DUPLICATE_NAMED_TEMPLATE][1] =
      "\u4ee5\u4e0b\u306b\u793a\u3059\u540d\u524d\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u8907\u6570\u898b\u3064\u304b\u308a\u307e\u3057\u305f: {0}";
//      "Found more than one template named: {0}";
  }

  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  static
  {
    contents[ER_INVALID_KEY_CALL][1] =
      "\u7121\u52b9\u306a\u95a2\u6570\u547c\u3073\u51fa\u3057: recursive key() \u547c\u3073\u51fa\u3057\u306f\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093";
//      "Invalid function call: recursive key() calls are not allowed";
  }
  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  static
  {
    contents[ER_REFERENCING_ITSELF][1] =
      "\u5909\u6570 {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u53c2\u7167\u3057\u3066\u3044\u307e\u3059\u3002";
//      "Variable {0} is directly or indirectly referencing itself!";
  }
  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  static
  {
    contents[ER_ILLEGAL_DOMSOURCE_INPUT][1] =
      "newTemplates \u306e DOMSource \u306b\u5bfe\u3059\u308b\u5165\u529b\u30ce\u30fc\u30c9\u306f null \u306b\u3067\u304d\u307e\u305b\u3093\u3002 ";
//      "The input node can not be null for a DOMSource for newTemplates!";
  }
	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  static
  {
    contents[ER_CLASS_NOT_FOUND_FOR_OPTION][1] =
			"\u30aa\u30d7\u30b7\u30e7\u30f3 {0} \u306b\u5bfe\u3059\u308b\u30af\u30e9\u30b9\u30d5\u30a1\u30a4\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093";
//			"Class file not found for option {0}";
  }
	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  static
  {
    contents[ER_REQUIRED_ELEM_NOT_FOUND][1] =
			"\u5fc5\u8981\u306a\u8981\u7d20\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: {0}";
//			"Required Element not found: {0}";
  }
  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  static
  {
    contents[ER_INPUT_CANNOT_BE_NULL][1] =
			"InputStream \u306f null \u306b\u3067\u304d\u307e\u305b\u3093";
//			"InputStream cannot be null";
  }
  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  static
  {
    contents[ER_URI_CANNOT_BE_NULL][1] =
			"URI \u306f null \u306b\u3067\u304d\u307e\u305b\u3093";
//			"URI cannot be null";
  }
  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  static
  {
    contents[ER_FILE_CANNOT_BE_NULL][1] =
			"File \u306f null \u306b\u3067\u304d\u307e\u305b\u3093";
//			"File cannot be null";
  }
  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  static
  {
    contents[ER_SOURCE_CANNOT_BE_NULL][1] =
			"InputSource \u306f null \u306b\u3067\u304d\u307e\u305b\u3093";
//			"InputSource cannot be null";
  }
  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  static
  {
    contents[ER_CANNOT_OVERWRITE_CAUSE][1] =
			"cause \u3092\u4e0a\u66f8\u304d\u3067\u304d\u307e\u305b\u3093";
//			"Cannot overwrite cause";
  }
  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  static
  {
    contents[ER_CANNOT_INIT_BSFMGR][1] =
			"BSF Manager \u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//			"Could not initialize BSF Manager";
  }
  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  static
  {
    contents[ER_CANNOT_CMPL_EXTENSN][1] =
			"\u62e1\u5f35\u3092\u30b3\u30f3\u30d1\u30a4\u30eb\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//			"Could not compile extension";
  }
  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  static
  {
    contents[ER_CANNOT_CREATE_EXTENSN][1] =
       "\u62e1\u5f35 {0} \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002\u539f\u56e0: {1}";
//      "Could not create extension: {0} because of: {1}";
  }
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  static
  {
    contents[ER_INSTANCE_MTHD_CALL_REQUIRES][1] =
      "\u30e1\u30bd\u30c3\u30c9 {0} \u306e\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u30e1\u30bd\u30c3\u30c9\u547c\u3073\u51fa\u3057\u306f\u3001\u6700\u521d\u306e\u5f15\u6570\u306b Objcet \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3092\u5fc5\u8981\u3068\u3057\u307e\u3059";
//      "Instance method call to method {0} requires an Object instance as first argument";
  }
  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  static
  {
    contents[ER_INVALID_ELEMENT_NAME][1] =
      "\u7121\u52b9\u306a\u8981\u7d20\u540d\u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f {0}";
//      "Invalid element name specified {0}";
  }
  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  static
  {
    contents[ER_ELEMENT_NAME_METHOD_STATIC][1] =
      "\u8981\u7d20\u540d\u30e1\u30bd\u30c3\u30c9\u306f static \u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093 {0}";
//      "Element name method must be static {0}";
  }
  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  static
  {
    contents[ER_EXTENSION_FUNC_UNKNOWN][1] =
             "\u62e1\u5f35\u95a2\u6570 {0} : {1} \u306f\u672a\u77e5\u3067\u3059";
//             "Extension function {0} : {1} is unknown";
  }
  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  static
  {
    contents[ER_MORE_MATCH_CONSTRUCTOR][1] =
             "{0} \u306e\u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf\u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059 {0}";
//             "More than one best match for constructor for {0}";
  }
  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  static
  {
    contents[ER_MORE_MATCH_METHOD][1] =
             "\u30e1\u30bd\u30c3\u30c9 {0} \u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059";
//             "More than one best match for method {0}";
  }
  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  static
  {
    contents[ER_MORE_MATCH_ELEMENT][1] =
             "\u8981\u7d20\u30e1\u30bd\u30c3\u30c9 {0} \u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059";
//             "More than one best match for element method {0}";
  }
  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  static
  {
    contents[ER_INVALID_CONTEXT_PASSED][1] =
             "{0} \u3092\u8a55\u4fa1\u3059\u308b\u306e\u306b\u7121\u52b9\u306a\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u304c\u6e21\u3055\u308c\u307e\u3057\u305f";
//             "Invalid context passed to evaluate {0}";
  }
  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  static
  {
    contents[ER_POOL_EXISTS][1] =
             "Pool \u304c\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059";
//             "Pool already exists";
  }
  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  static
  {
    contents[ER_NO_DRIVER_NAME][1] =
             "\u30c9\u30e9\u30a4\u30d0\u306e Name \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//             "No driver Name specified";
  }
  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  static
  {
    contents[ER_NO_URL][1] =
             "URL \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//             "No URL specified";
  }
  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  static
  {
    contents[ER_POOL_SIZE_LESSTHAN_ONE][1] =
             "1 \u3088\u308a\u5c0f\u3055\u3044 Pool \u306e\u30b5\u30a4\u30ba\u3067\u3059\u3002";
//             "Pool size is less than one!";
  }
  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  static
  {
    contents[ER_INVALID_DRIVER][1] =
             "\u7121\u52b9\u306a\u30c9\u30e9\u30a4\u30d0\u540d\u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f\u3002";
//             "Invalid driver name specified!";
  }
  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  static
  {
    contents[ER_NO_STYLESHEETROOT][1] =
             "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u30eb\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//             "Did not find the stylesheet root!";
  }
  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  static
  {
    contents[ER_ILLEGAL_XMLSPACE_VALUE][1] =
         "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u3067\u3059";
//         "Illegal value for xml:space";
  }
  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  static
  {
    contents[ER_PROCESSFROMNODE_FAILED][1] =
         "processFromNode \u304c\u5931\u6557\u3057\u307e\u3057\u305f";
//         "processFromNode failed";
  }
  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_LOAD][1] =
        "\u30ea\u30bd\u30fc\u30b9 [ {0} ] \u306f\u6b21\u306e\u3082\u306e\u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f: {1} \n {2} \t {3}";
//        "The resource [ {0} ] could not load: {1} \n {2} \t {3}";
  }
   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  static
  {
    contents[ER_BUFFER_SIZE_LESSTHAN_ZERO][1] =
        "\u30d0\u30c3\u30d5\u30a1\u30b5\u30a4\u30ba <=0";
//        "Buffer size <=0";
  }
  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  static
  {
    contents[ER_UNKNOWN_ERROR_CALLING_EXTENSION][1] =
        "\u62e1\u5f35\u3092\u547c\u3073\u51fa\u3059\u3068\u304d\u306b\u672a\u77e5\u306e\u30a8\u30e9\u30fc\u304c\u767a\u751f\u3057\u307e\u3057\u305f";
//        "Unknown error when calling extension";
  }
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  static
  {
    contents[ER_NO_NAMESPACE_DECL][1] =
        "\u63a5\u982d\u8f9e {0} \u306b\u306f\u5bfe\u5fdc\u3059\u308b\u540d\u524d\u7a7a\u9593\u5ba3\u8a00\u304c\u3042\u308a\u307e\u305b\u3093";
//        "Prefix {0} does not have a corresponding namespace declaration";
  }
  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  static
  {
    contents[ER_ELEM_CONTENT_NOT_ALLOWED][1] =
        "\u8981\u7d20\u306e\u5185\u5bb9\u306f lang=javaclass {0} \u306b\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093";
//        "Element content not allowed for lang=javaclass {0}";
  }   
  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  static
  {
    contents[ER_STYLESHEET_DIRECTED_TERMINATION][1] =
        "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306b\u3088\u308a\u7d42\u4e86\u304c\u6307\u793a\u3055\u308c\u307e\u3057\u305f";
//        "Stylesheet directed termination";
  }
  
   /** 1 or 2   */
  public static final int ER_ONE_OR_TWO = 141;

  static
  {
    contents[ER_ONE_OR_TWO][1] =
        "1 \u307e\u305f\u306f 2";
//        "1 or 2";
  }
  
   /** 2 or 3   */
  public static final int ER_TWO_OR_THREE = 142;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
        "2 \u307e\u305f\u306f 3";
//        "2 or 3";
  }
  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  static
  {
    contents[ER_COULD_NOT_LOAD_RESOURCE][1] =
        "{0} \u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f (CLASSPATH \u3092\u30c1\u30a7\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044)\u3002\u30c7\u30d5\u30a9\u30eb\u30c8\u3060\u3051\u3092\u4f7f\u7528\u3057\u307e\u3059\u3002";
//        "Could not load {0} (check CLASSPATH), now using just the defaults";
  }
  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  static
  {
    contents[ER_CANNOT_INIT_DEFAULT_TEMPLATES][1] =
        "\u30c7\u30d5\u30a9\u30eb\u30c8\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093";
//        "Cannot initialize default templates";
  }
  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

  static
  {
    contents[ER_RESULT_NULL][1] =
        "Result \u306f null \u306b\u306f\u3067\u304d\u307e\u305b\u3093";
//        "Result should not be null";
  }
    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  static
  {
    contents[ER_RESULT_COULD_NOT_BE_SET][1] =
        "Result \u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//        "Result could not be set";
  }
  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  static
  {
    contents[ER_NO_OUTPUT_SPECIFIED][1] =
        "\u51fa\u529b\u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//        "No output specified";
  }
  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  static
  {
    contents[ER_CANNOT_TRANSFORM_TO_RESULT_TYPE][1] =
        "\u30bf\u30a4\u30d7 {0} \u306e Result \u306b\u5909\u5f62\u3067\u304d\u307e\u305b\u3093";
//        "Can't transform to a Result of type {0}";
  }
  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  static
  {
    contents[ER_CANNOT_TRANSFORM_SOURCE_TYPE][1] =
        "\u30bf\u30a4\u30d7 {0} \u306e Source \u306b\u5909\u5f62\u3067\u304d\u307e\u305b\u3093";
//        "Can't transform a Source of type {0}";
  }
  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  static
  {
    contents[ER_NULL_CONTENT_HANDLER][1] =
        "Null \u30b3\u30f3\u30c6\u30f3\u30c4\u30cf\u30f3\u30c9\u30e9";
//        "Null content handler";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "Null \u30a8\u30e9\u30fc\u30cf\u30f3\u30c9\u30e9";
//        "Null error handler";
  }
  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  static
  {
    contents[ER_CANNOT_CALL_PARSE][1] =
        "ContentHandler \u304c\u8a2d\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u69cb\u6587\u89e3\u6790\u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093";
//        "parse can not be called if the ContentHandler has not been set";
  }
  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  static
  {
    contents[ER_NO_PARENT_FOR_FILTER][1] =
        "\u30d5\u30a3\u30eb\u30bf\u51e6\u7406\u3059\u308b\u89aa\u304c\u3042\u308a\u307e\u305b\u3093";
//        "No parent for filter";
  }
  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  static
  {
    contents[ER_NO_STYLESHEET_IN_MEDIA][1] =
         "{0} \u306b\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002\u30e1\u30c7\u30a3\u30a2= {1}";
//         "No stylesheet found in: {0}, media= {1}";
  }
  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  static
  {
    contents[ER_NO_STYLESHEET_PI][1] =
         "xml-stylesheet PI \u304c {0} \u306b\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f";
//         "No xml-stylesheet PI found in: {0}";
  }
  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  static
  {
    contents[ER_NO_DEFAULT_IMPL][1] =
         "\u30c7\u30d5\u30a9\u30eb\u30c8\u5b9f\u88c5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093";
//         "No default implementation found ";
  }
  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  static
  {
    contents[ER_CHUNKEDINTARRAY_NOT_SUPPORTED][1] =
       "ChunkedIntArray({0}) \u306f\u73fe\u5728\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//       "ChunkedIntArray({0}) not currently supported";
  }
  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  static
  {
    contents[ER_OFFSET_BIGGER_THAN_SLOT][1] =
       "\u30b9\u30ed\u30c3\u30c8\u3088\u308a\u3082\u5927\u304d\u3044\u30aa\u30d5\u30bb\u30c3\u30c8";
//       "Offset bigger than slot";
  }
  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  static
  {
    contents[ER_COROUTINE_NOT_AVAIL][1] =
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u306f\u7121\u52b9\u3067\u3059\u3002id={0}";
//       "Coroutine not available, id={0}";
  }
  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  static
  {
    contents[ER_COROUTINE_CO_EXIT][1] =
       "CoroutineManager \u306f co_exit() \u8981\u6c42\u3092\u53d7\u3051\u53d6\u308a\u307e\u3057\u305f";
//       "CoroutineManager received co_exit() request";
  }
  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  static
  {
    contents[ER_COJOINROUTINESET_FAILED][1] =
       "co_joinCoroutineSet() \u306f\u5931\u6557\u3057\u307e\u3057\u305f";
//       "co_joinCoroutineSet() failed";
  }
  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  static
  {
    contents[ER_COROUTINE_PARAM][1] =
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u30d1\u30e9\u30e1\u30fc\u30bf\u30a8\u30e9\u30fc ({0})";
//       "Coroutine parameter error ({0})";
  }
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  static
  {
    contents[ER_PARSER_DOTERMINATE_ANSWERS][1] =
       "\nUNEXPECTED: \u30d1\u30fc\u30b5 doTerminate \u306e\u7b54\u3048 {0}";
//       "\nUNEXPECTED: Parser doTerminate answers {0}";
  }
  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  static
  {
    contents[ER_NO_PARSE_CALL_WHILE_PARSING][1] =
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b parse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093";
//       "parse may not be called while parsing";
  }
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  static
  {
    contents[ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\u30a8\u30e9\u30fc: \u5165\u529b\u3055\u308c\u305f\u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//       "Error: typed iterator for axis  {0} not implemented";
  }
  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  static
  {
    contents[ER_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\u30a8\u30e9\u30fc: \u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//       "Error: iterator for axis {0} not implemented ";
  }
  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  static
  {
    contents[ER_ITERATOR_CLONE_NOT_SUPPORTED][1] =
       "\u53cd\u5fa9\u5b50\u30af\u30ed\u30fc\u30f3\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//       "Iterator clone not supported";
  }
  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  static
  {
    contents[ER_UNKNOWN_AXIS_TYPE][1] =
       "\u672a\u77e5\u306e\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u30bf\u30a4\u30d7: {0}";
//       "Unknown axis traversal type: {0}";
  }
  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  static
  {
    contents[ER_AXIS_NOT_SUPPORTED][1] =
       "\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}";
//       "Axis traverser not supported: {0}";
  }
  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  static
  {
    contents[ER_NO_DTMIDS_AVAIL][1] =
       "\u3053\u308c\u4ee5\u4e0a\u306e DTM ID \u306f\u7121\u52b9\u3067\u3059";
//       "No more DTM IDs are available";
  }
  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  static
  {
    contents[ER_NOT_SUPPORTED][1] =
       "\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}";
//       "Not supported: {0}";
  }
  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  static
  {
    contents[ER_NODE_NON_NULL][1] =
       "getDTMHandleFromNode \u306e\u30ce\u30fc\u30c9\u306f null \u4ee5\u5916\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//       "Node must be non-null for getDTMHandleFromNode";
  }
  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  static
  {
    contents[ER_COULD_NOT_RESOLVE_NODE][1] =
       "\u30ce\u30fc\u30c9\u3092\u30cf\u30f3\u30c9\u30eb\u306b\u5909\u3048\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//       "Could not resolve the node to a handle";
  }
  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  static
  {
    contents[ER_STARTPARSE_WHILE_PARSING][1] =
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b startParse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093";
//       "startParse may not be called while parsing";
  }
  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  static
  {
    contents[ER_STARTPARSE_NEEDS_SAXPARSER][1] =
       "startParse \u306f null \u3067\u306a\u3044 SAXParser \u3092\u5fc5\u8981\u3068\u3057\u307e\u3059";
//       "startParse needs a non-null SAXParser";
  }
  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  static
  {
    contents[ER_COULD_NOT_INIT_PARSER][1] =
       "\u30d1\u30fc\u30b5\u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f";
//       "could not initialize parser with";
  }
  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  static
  {
    contents[ER_PROPERTY_VALUE_BOOLEAN][1] =
       "\u30d7\u30ed\u30d1\u30c6\u30a3 {0} \u306e\u5024\u306f Boolean \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//       "Value for property {0} should be a Boolean instance";
  }
  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  static
  {
    contents[ER_EXCEPTION_CREATING_POOL][1] =
       "\u4f8b\u5916\u306b\u3088\u308a\u30d7\u30fc\u30eb\u306b\u65b0\u3057\u3044\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3092\u4f5c\u6210\u3057\u3066\u3044\u307e\u3059";
//       "exception creating new instance for pool";
  }
  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  static
  {
    contents[ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE][1] =
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u30a8\u30b9\u30b1\u30fc\u30d7\u30b7\u30fc\u30b1\u30f3\u30b9\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059";
//       "Path contains invalid escape sequence";
  }
  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  static
  {
    contents[ER_SCHEME_REQUIRED][1] =
       "\u30b9\u30ad\u30fc\u30de\u304c\u5fc5\u8981\u3067\u3059\u3002";
//       "Scheme is required!";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  static
  {
    contents[ER_NO_SCHEME_IN_URI][1] =
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093: {0}";
//       "No scheme found in URI: {0}";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  static
  {
    contents[ER_NO_SCHEME_INURI][1] =
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093";
//       "No scheme found in URI";
  }
  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  static
  {
    contents[ER_PATH_INVALID_CHAR][1] =
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059: {0}";
//       "Path contains invalid character: {0}";
  }
  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  static
  {
    contents[ER_SCHEME_FROM_NULL_STRING][1] =
       "null \u6587\u5b57\u5217\u304b\u3089\u30b9\u30ad\u30fc\u30de\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093";
//       "Cannot set scheme from null string";
  }
  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  static
  {
    contents[ER_SCHEME_NOT_CONFORMANT][1] =
       "\u30b9\u30ad\u30fc\u30de\u304c\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002";
//       "The scheme is not conformant.";
  }
  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  static
  {
    contents[ER_HOST_ADDRESS_NOT_WELLFORMED][1] =
       "\u30db\u30b9\u30c8\u304c\u6b63\u3057\u3044\u5f62\u5f0f\u306e\u30a2\u30c9\u30ec\u30b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093";
//      "Host is not a well formed address";
  }
  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  static
  {
    contents[ER_PORT_WHEN_HOST_NULL][1] =
       "\u30db\u30b9\u30c8\u304c null \u306e\u3068\u304d\u3001\u30dd\u30fc\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093";
//       "Port cannot be set when host is null";
  }
  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  static
  {
    contents[ER_INVALID_PORT][1] =
       "\u7121\u52b9\u306a\u30dd\u30fc\u30c8\u756a\u53f7";
//       "Invalid port number";
  }
  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  static
  {
    contents[ER_FRAG_FOR_GENERIC_URI][1] =
       "\u6c4e\u7528 URI \u306b\u5bfe\u3057\u3066\u306e\u307f\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u3059";
//       "Fragment can only be set for a generic URI";
  }
  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  static
  {
    contents[ER_FRAG_WHEN_PATH_NULL][1] =
       "\u30d1\u30b9\u304c null \u306e\u3068\u304d\u3001\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093";
//       "Fragment cannot be set when path is null";
  }
  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  static
  {
    contents[ER_FRAG_INVALID_CHAR][1] =
       "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059";
//       "Fragment contains invalid character";
  }
  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  static
  {
    contents[ER_PARSER_IN_USE][1] =
        "\u30d1\u30fc\u30b5\u306f\u3059\u3067\u306b\u4f7f\u308f\u308c\u3066\u3044\u307e\u3059";
//        "Parser is already in use";
  }
  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  static
  {
    contents[ER_CANNOT_CHANGE_WHILE_PARSING][1] =
        "\u69cb\u6587\u89e3\u6790\u4e2d\u3001{0} {1} \u3092\u5909\u66f4\u3067\u304d\u307e\u305b\u3093";
//        "Cannot change {0} {1} while parsing";
  }
  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  static
  {
    contents[ER_SELF_CAUSATION_NOT_PERMITTED][1] =
        "\u81ea\u8eab\u304c\u539f\u56e0\u3068\u306a\u3063\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//        "Self-causation not permitted";
  }
  
   /** src attribute not yet supported for  */
  public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  static
  {
    contents[ER_COULD_NOT_FIND_EXTERN_SCRIPT][1] =
         "{0} \u306b\u3042\u308b\u5916\u90e8\u30b9\u30af\u30ea\u30d7\u30c8\u3092\u5165\u624b\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
//         "Could not get to external script at {0}";
  }
  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_FIND][1] =
        "\u30ea\u30bd\u30fc\u30b9 [ {0} ] \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f.\n {1}";
//        "The resource [ {0} ] could not be found.\n {1}";
  }
  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  static
  {
    contents[ER_OUTPUT_PROPERTY_NOT_RECOGNIZED][1] =
        "\u51fa\u529b\u30d7\u30ed\u30d1\u30c6\u30a3\u304c\u8a8d\u3081\u3089\u308c\u307e\u305b\u3093: {0}";
//        "Output property not recognized: {0}";
  }
  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  static
  {
    contents[ER_NO_USERINFO_IF_NO_HOST][1] =
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Userinfo \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093";
//        "Userinfo may not be specified if host is not specified";
  }
  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  static
  {
    contents[ER_NO_PORT_IF_NO_HOST][1] =
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Port \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093";
//        "Port may not be specified if host is not specified";
  }
  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  static
  {
    contents[ER_NO_QUERY_STRING_IN_PATH][1] =
        "\u30d1\u30b9\u304a\u3088\u3073\u7167\u4f1a\u6587\u5b57\u5217\u3067 Query \u6587\u5b57\u5217\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093";
//        "Query string cannot be specified in path and query string";
  }
  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  static
  {
    contents[ER_NO_FRAGMENT_STRING_IN_PATH][1] =
        "\u30d1\u30b9\u304a\u3088\u3073\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306e\u4e21\u65b9\u3067\u3001Fragment \u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093";
//        "Fragment cannot be specified in both the path and fragment";
  }
  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  static
  {
    contents[ER_CANNOT_INIT_URI_EMPTY_PARMS][1] =
        "\u7a7a\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3092\u4f7f\u3063\u3066 URI \u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093";
//        "Cannot initialize URI with empty parameters";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  static
  {
    contents[ER_FAILED_CREATING_ELEMLITRSLT][1] =
        "ElemLiteralResult \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u306e\u4f5c\u6210\u306b\u5931\u6557\u3057\u307e\u3057\u305f";
//        "Failed creating ElemLiteralResult instance";
  }  
  
   /** Priority value does not contain a parsable number   */
  public static final int ER_PRIORITY_NOT_PARSABLE = 204;

  static
  {
    contents[ER_PRIORITY_NOT_PARSABLE][1] =
        "Priority \u5024\u306b\u69cb\u6587\u89e3\u6790\u53ef\u80fd\u306a\u6570\u5b57\u304c\u542b\u307e\u308c\u307e\u305b\u3093";
//        "Priority value does not contain a parsable number";
  }
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

  static
  {
    contents[ER_VALUE_SHOULD_EQUAL][1] =
        " {0} \u306e\u5024\u306f yes \u307e\u305f\u306f no \u306e\u3044\u305a\u308c\u304b\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093";
//        " Value for {0} should equal yes or no";
  }
 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  static
  {
    contents[ER_FAILED_CALLING_METHOD][1] =
        " {0} \u30e1\u30bd\u30c3\u30c9\u306e\u547c\u3073\u51fa\u3057\u306b\u5931\u6557\u3057\u307e\u3057\u305f";
//        " Failed calling {0} method";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  static
  {
    contents[ER_FAILED_CREATING_ELEMTMPL][1] =
        "ElemTemplateElement \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u306e\u4f5c\u6210\u306b\u5931\u6557\u3057\u307e\u3057\u305f";
//        "Failed creating ElemTemplateElement instance";
  }
  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  static
  {
    contents[ER_CHARS_NOT_ALLOWED][1] =
        "\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306e\u3053\u306e\u90e8\u5206\u3067\u3001\u6587\u5b57\u5217\u306f\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093";
//        "Characters are not allowed at this point in the document";
  }
  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  static
  {
    contents[ER_ATTR_NOT_ALLOWED][1] =
        "\"{0}\" \u5c5e\u6027\u306f {1} \u8981\u7d20\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002";
//        "\"{0}\" attribute is not allowed on the {1} element!";
  }
  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

  static
  {
    contents[ER_METHOD_NOT_SUPPORTED][1] =
        "\u30e1\u30bd\u30c3\u30c9\u306f\u307e\u3060\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//        "Method not yet supported ";
  }
 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  static
  {
    contents[ER_BAD_VALUE][1] =
     "{0} \u8aa4\u3063\u305f\u5024 {1} ";
//     "{0} bad value {1} ";
  }
  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_FOUND][1] =
     "{0} \u5c5e\u6027\u5024\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093";
//     "{0} attribute value not found ";
  }
  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_RECOGNIZED][1] =
     "{0} \u5c5e\u6027\u5024\u304c\u8a8d\u3081\u3089\u308c\u307e\u305b\u3093";
//     "{0} attribute value not recognized ";
  }

  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  static
  {
    contents[ER_INCRSAXSRCFILTER_NOT_RESTARTABLE][1] =
     "IncrementalSAXSource_Filter \u306f\u73fe\u5728\u518d\u8d77\u52d5\u3067\u304d\u307e\u305b\u3093";
//     "IncrementalSAXSource_Filter not currently restartable";
  }
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  static
  {
    contents[ER_XMLRDR_NOT_BEFORE_STARTPARSE][1] =
     "XMLReader \u306f startParse \u8981\u6c42\u3088\u308a\u524d\u306b\u914d\u7f6e\u3067\u304d\u307e\u305b\u3093";
//     "XMLReader not before startParse request";
  }
  
    /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NULL_URI_NAMESPACE = 216;

  static
  {
    contents[ER_NULL_URI_NAMESPACE][1] =
     "null URI \u3092\u4f7f\u3063\u3066\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e\u3092\u751f\u6210\u3057\u3088\u3046\u3068\u3057\u3066\u3044\u307e\u3059";
  }    


  /*
    /**  Cannot find SAX1 driver class    *
  public static final int ER_CANNOT_FIND_SAX1_DRIVER = 190;

  static
  {
    contents[ER_CANNOT_FIND_SAX1_DRIVER][1] =
      "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u3092\u898b\u3064\u3051\u3089\u308c\u307e\u305b\u3093";
//      "Cannot find SAX1 driver class {0}";
  }
  
   /**  SAX1 driver class {0} found but cannot be loaded    *
  public static final int ER_SAX1_DRIVER_NOT_LOADED = 191;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_LOADED][1] =
      "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093";
//      "SAX1 driver class {0} found but cannot be loaded";
  }
  
   /**  SAX1 driver class {0} found but cannot be instantiated    *
  public static final int ER_SAX1_DRIVER_NOT_INSTANTIATED = 192;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_INSTANTIATED][1] =
      "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c\u30ed\u30fc\u30c9\u3055\u308c\u307e\u3057\u305f\u304c\u3001\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093";
//      "SAX1 driver class {0} loaded but cannot be instantiated";
  }
  
   /**  SAX1 driver class {0} does not implement org.xml.sax.Parser    *
  public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 193;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER][1] =
      "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u306f org.xml.sax.Parser \u3092\u5b9f\u88c5\u3057\u307e\u305b\u3093";
//      "SAX1 driver class {0} does not implement org.xml.sax.Parser";
  }
  
   /**  System property org.xml.sax.parser not specified    *
  public static final int ER_PARSER_PROPERTY_NOT_SPECIFIED = 194;

  static
  {
    contents[ER_PARSER_PROPERTY_NOT_SPECIFIED][1] =
      "\u30b7\u30b9\u30c6\u30e0\u30d7\u30ed\u30d1\u30c6\u30a3 org.xml.sax.parser \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093";
//      "System property org.xml.sax.parser not specified";
  }
  
   /**  Parser argument must not be null    *
  public static final int ER_PARSER_ARG_CANNOT_BE_NULL = 195;

  static
  {
    contents[ER_PARSER_ARG_CANNOT_BE_NULL][1] =
      "\u30d1\u30fc\u30b5\u5c5e\u6027\u306f null \u306b\u3067\u304d\u307e\u305b\u3093";
//      "Parser argument must not be null";
  }
  
   /**  Feature:    *
  public static final int ER_FEATURE = 196;

  static
  {
    contents[ER_FEATURE][1] =
        "\u6a5f\u80fd: {0}";
//        "Feature: {0}";
  }
  
   /**  Property:    *
  public static final int ER_PROPERTY = 197;

  static
  {
    contents[ER_PROPERTY][1] =
        "\u30d7\u30ed\u30d1\u30c6\u30a3: {0}";
//        "Property: {0}";
  }
  
   /** Null Entity Resolver  *
  public static final int ER_NULL_ENTITY_RESOLVER = 198;

  static
  {
    contents[ER_NULL_ENTITY_RESOLVER][1] =
        "null \u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u30ea\u30be\u30eb\u30d0";
//        "Null entity resolver";
  }
  
   /** Null DTD handler  *
  public static final int ER_NULL_DTD_HANDLER = 199;

  static
  {
    contents[ER_NULL_DTD_HANDLER][1] =
        "null DTD \u30cf\u30f3\u30c9\u30e9";
//        "Null DTD handler";
  }
  
 */ 
  

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  public static final int WG_FOUND_CURLYBRACE = 1;

  static
  {
    contents[WG_FOUND_CURLYBRACE + MAX_CODE][1] =
      "'}' \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u5c5e\u6027\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u958b\u304b\u308c\u3066\u3044\u307e\u305b\u3093\u3002";
//      "Found '}' but no attribute template open!";
  }

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  static
  {
    contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] =
      "\u8b66\u544a: count \u5c5e\u6027\u304c xsl:number \u5185\u306e\u7956\u5148\u3068\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002 \u30bf\u30fc\u30b2\u30c3\u30c8 = {0}";
//      "Warning: count attribute does not match an ancestor in xsl:number! Target = {0}";
  }

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  static
  {
    contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] =
      "\u53e4\u3044\u69cb\u6587: 'expr' \u5c5e\u6027\u306e\u540d\u524d\u306f 'select' \u306b\u5909\u66f4\u3055\u308c\u3066\u3044\u307e\u3059\u3002";
//      "Old syntax: The name of the 'expr' attribute has been changed to 'select'.";
  }

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  static
  {
    contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] =
      "Xalan \u306f format-number \u95a2\u6570\u5185\u306e\u30ed\u30b1\u30fc\u30eb\u540d\u3092\u307e\u3060\u51e6\u7406\u3057\u3066\u3044\u307e\u305b\u3093\u3002";
//      "Xalan doesn't yet handle the locale name in the format-number function.";
  }

  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;

  static
  {
    contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] =
      "\u8b66\u544a: xml:lang={0} \u306e\u30ed\u30b1\u30fc\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f";
//      "Warning: Could not find locale for xml:lang={0}";
  }

  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093: {0}";
//      "Can not make URL from: {0}";
  }

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  static
  {
    contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] =
      "\u8981\u6c42\u3055\u308c\u305f\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093: {0}";
//      "Can not load requested doc: {0}";
  }

  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;

  static
  {
    contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] =
      "<sort xml:lang={0} \u306e Collator \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f";
//      "Could not find Collator for <sort xml:lang={0}";
  }

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  static
  {
    contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] =
      "\u53e4\u3044\u69cb\u6587: \u95a2\u6570\u306e\u6307\u4ee4\u306f {0} \u306e URL \u3092\u4f7f\u7528\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059";
//      "Old syntax: the functions instruction should use a url of {0}";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] =
      "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u304c\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}\u3001UTF-8 \u3092\u4f7f\u7528";
//      "encoding not supported: {0}, using UTF-8";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] =
      "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u304c\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}\u3001Java {1} \u3092\u4f7f\u7528";
//      "encoding not supported: {0}, using Java {1}";
  }

  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;

  static
  {
    contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] =
      "\u7279\u5b9a\u3067\u3042\u308b\u3079\u304d\u3082\u306e\u306e\u7af6\u5408\u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f: {0} \u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u3067\u6700\u5f8c\u306b\u898b\u3064\u304b\u3063\u305f\u3082\u306e\u304c\u4f7f\u7528\u3055\u308c\u307e\u3059\u3002";
//      "Specificity conflicts found: {0} Last found in stylesheet will be used.";
  }

  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;

  static
  {
    contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] =
      "========= {0} \u306e\u69cb\u6587\u89e3\u6790\u304a\u3088\u3073\u6e96\u5099  ==========";
//      "========= Parsing and preparing {0} ==========";
  }

  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;

  static
  {
    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "\u5c5e\u6027\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u3001{0}";
//    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "Attr Template, {0}";
  }

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;

  static
  {
    contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] =
      "xsl:strip-space \u3068 xsl:preserve-space \u306e\u9593\u3067 match \u7af6\u5408\u3057\u307e\u3059";
//      "Match conflict between xsl:strip-space and xsl:preserve-space";
  }

  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;

  static
  {
    contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] =
      "Xalan \u306f\u307e\u3060 {0} \u5c5e\u6027\u3092\u51e6\u7406\u3057\u3066\u3044\u307e\u305b\u3093\u3002";
//      "Xalan does not yet handle the {0} attribute!";
  }

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  static
  {
    contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] =
      "10 \u9032\u6570\u5f62\u5f0f\u306e\u5ba3\u8a00\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: {0}";
//      "No declaration found for decimal format: {0}";
  }

  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;

  static
  {
    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "XSLT \u540d\u524d\u7a7a\u9593\u304c\u306a\u3044\u3001\u307e\u305f\u306f\u4e0d\u6b63\u3067\u3059\u3002";
//    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "Missing or incorrect XSLT Namespace. ";
  }

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  static
  {
    contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] =
      "\u30c7\u30d5\u30a9\u30eb\u30c8\u306e xsl:decimal-format \u5ba3\u8a00\u306f 1 \u3064\u3060\u3051\u8a31\u53ef\u3055\u308c\u307e\u3059\u3002";
//      "Only one default xsl:decimal-format declaration is allowed.";
  }

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  static
  {
    contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] =
      "xsl:decimal-format \u306e\u540d\u524d\u306f\u4e00\u610f\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093\u3002\u540d\u524d \"{0}\" \u306f\u91cd\u8907\u3057\u3066\u3044\u307e\u3059\u3002";
//      "xsl:decimal-format names must be unique. Name \"{0}\" has been duplicated.";
  }

  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] =
      "{0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u3042\u308a\u307e\u3059: {1}";
//      "{0} has an illegal attribute: {1}";
  }

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  static
  {
    contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] =
      "\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e\u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093: {0}\u3002 \u30ce\u30fc\u30c9\u306f\u7121\u8996\u3055\u308c\u307e\u3059\u3002";
//      "Could not resolve namespace prefix: {0}. The node will be ignored.";
  }

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  static
  {
    contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] =
      "xsl:stylesheet \u306f 'version' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002";
//      "xsl:stylesheet requires a 'version' attribute!";
  }

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] =
      "\u4e0d\u5f53\u306a\u5c5e\u6027\u540d: {0}";
//      "Illegal attribute name: {0}";
  }

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] =
      "\u5c5e\u6027 {0} \u306b\u4e0d\u5f53\u306a\u5024\u304c\u4f7f\u7528\u3055\u308c\u3066\u3044\u307e\u3059: {1}";
//      "Illegal value used for attribute {0}: {1}";
  }

  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;

  static
  {
    contents[WG_EMPTY_SECOND_ARG + MAX_CODE][1] =
      "document \u95a2\u6570\u306e 2 \u756a\u76ee\u306e\u5f15\u6570\u306e\u7d50\u679c\u306e\u30ce\u30fc\u30c9\u30bb\u30c3\u30c8\u304c\u7a7a\u3067\u3059\u3002\u6700\u521d\u306e\u5f15\u6570\u304c\u4f7f\u7528\u3055\u308c\u307e\u3059\u3002";
//      "Resulting nodeset from second argument of document function is empty. The first agument will be used.";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "ja";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "ja";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "ja";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "createMessage \u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u304c\u7bc4\u56f2\u5916\u3067\u3057\u305f";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "messageFormat \u547c\u3073\u51fa\u3057\u3067\u4f8b\u5916\u304c\u30b9\u30ed\u30fc\u3055\u308c\u307e\u3057\u305f";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan \u30d0\u30fc\u30b8\u30e7\u30f3 ";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "\u306f\u3044";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "\u884c\u756a\u53f7";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "\u5217\u756a\u53f7";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: \u7d42\u4e86";
    contents[MAX_MESSAGES + 11][0] = "xslProc_option";
    contents[MAX_MESSAGES + 11][1] = "Xalan-J \u30b3\u30de\u30f3\u30c9\u884c\u30d7\u30ed\u30bb\u30b9\u306e\u30af\u30e9\u30b9\u30aa\u30d7\u30b7\u30e7\u30f3:";
    contents[MAX_MESSAGES + 12][0] = "optionIN";
    contents[MAX_MESSAGES + 12][1] = "    -IN inputXMLURL";
    contents[MAX_MESSAGES + 13][0] = "optionXSL";
    contents[MAX_MESSAGES + 13][1] = "   [-XSL XSLTransformationURL]";
    contents[MAX_MESSAGES + 14][0] = "optionOUT";
    contents[MAX_MESSAGES + 14][1] = "   [-OUT outputFileName]";
    contents[MAX_MESSAGES + 15][0] = "optionLXCIN";
    contents[MAX_MESSAGES + 15][1] =
      "   [-LXCIN compiledStylesheetFileNameIn]";
    contents[MAX_MESSAGES + 16][0] = "optionLXCOUT";
    contents[MAX_MESSAGES + 16][1] =
      "   [-LXCOUT compiledStylesheetFileNameOutOut]";
    contents[MAX_MESSAGES + 17][0] = "optionPARSER";
    contents[MAX_MESSAGES + 17][1] =
      "   [-PARSER \u306f parser liaison \u306e\u30af\u30e9\u30b9\u540d\u3092\u5b8c\u5168\u4fee\u98fe\u3059\u308b]";
    contents[MAX_MESSAGES + 18][0] = "optionE";
    contents[MAX_MESSAGES + 18][1] = "   [-E (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u53c2\u7167\u3092\u62e1\u5f35\u3057\u306a\u3044)]";
    contents[MAX_MESSAGES + 19][0] = "optionV";
    contents[MAX_MESSAGES + 19][1] = "   [-E (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u53c2\u7167\u3092\u62e1\u5f35\u3057\u306a\u3044)]";
    contents[MAX_MESSAGES + 20][0] = "optionQC";
    contents[MAX_MESSAGES + 20][1] =
      "   [-QC (Quiet Pattern Conflicts Warnings)]";
    contents[MAX_MESSAGES + 21][0] = "optionQ";
    contents[MAX_MESSAGES + 21][1] = "   [-Q  (\u975e\u51fa\u529b\u30e2\u30fc\u30c9)]";
    contents[MAX_MESSAGES + 22][0] = "optionLF";
    contents[MAX_MESSAGES + 22][1] =
      "   [-LF (\u51fa\u529b\u306b\u306e\u307f\u6539\u884c\u3092\u4f7f\u7528\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f CR/LF})]";
    contents[MAX_MESSAGES + 23][0] = "optionCR";
    contents[MAX_MESSAGES + 23][1] =
      "   [-CR (\u51fa\u529b\u306b\u306e\u307f\u30ad\u30e3\u30ea\u30c3\u30b8\u30ea\u30bf\u30fc\u30f3\u3092\u4f7f\u7528\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f CR/LF})]";
    contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
    contents[MAX_MESSAGES + 24][1] =
      "   [-ESCAPE (\u30a8\u30b9\u30b1\u30fc\u30d7\u3059\u308b\u6587\u5b57\u5217 {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f <>&\"\'\\r\\n}]";
    contents[MAX_MESSAGES + 25][0] = "optionINDENT";
    contents[MAX_MESSAGES + 25][1] =
      "   [-INDENT (\u30a4\u30f3\u30c7\u30f3\u30c8\u306b\u8a2d\u5b9a\u3059\u308b\u7a7a\u767d\u6587\u5b57\u6570\u3092\u5236\u5fa1\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f 0})]";
    contents[MAX_MESSAGES + 26][0] = "optionTT";
    contents[MAX_MESSAGES + 26][1] =
      "   [-TT (\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u547c\u3073\u51fa\u3055\u308c\u305f\u3068\u304d\u306b\u30c8\u30ec\u30fc\u30b9\u3059\u308b)]";
    contents[MAX_MESSAGES + 27][0] = "optionTG";
    contents[MAX_MESSAGES + 27][1] =
      "   [-TG (\u5404\u751f\u6210\u30a4\u30d9\u30f3\u30c8\u3092\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 28][0] = "optionTS";
    contents[MAX_MESSAGES + 28][1] = "   [-TS (\u5404\u9078\u629e\u30a4\u30d9\u30f3\u30c8\u3092\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 29][0] = "optionTTC";
    contents[MAX_MESSAGES + 29][1] =
      "   [-TTC (\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u5b50\u304c\u51e6\u7406\u3055\u308c\u305f\u3068\u304d\u306b\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
    contents[MAX_MESSAGES + 30][1] =
      "   [-TCLASS (\u30c8\u30ec\u30fc\u30b9\u62e1\u5f35\u7528\u306e TraceListener \u30af\u30e9\u30b9\u3002)]";
    contents[MAX_MESSAGES + 31][0] = "optionVALIDATE";
    contents[MAX_MESSAGES + 31][1] =
      "   [-VALIDATE (\u59a5\u5f53\u6027\u691c\u67fb\u3092\u6709\u52b9\u306b\u3059\u308b\u304b\u3069\u3046\u304b\u3092\u8a2d\u5b9a\u3059\u308b\u3002\u30c7\u30d5\u30a9\u30eb\u30c8\u3067\u306f\u7121\u52b9\u3002)]";
    contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
    contents[MAX_MESSAGES + 32][1] =
      "   [-EDUMP {\u30aa\u30d7\u30b7\u30e7\u30f3\u306e\u30d5\u30a1\u30a4\u30eb\u540d} (\u30a8\u30e9\u30fc\u767a\u751f\u6642\u306b\u30b9\u30bf\u30c3\u30af\u30c0\u30f3\u30d7\u3092\u5b9f\u884c\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 33][0] = "optionXML";
    contents[MAX_MESSAGES + 33][1] =
      "   [-XML (XML \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3057\u3066\u3001XML \u30d8\u30c3\u30c0\u3092\u8ffd\u52a0\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 34][0] = "optionTEXT";
    contents[MAX_MESSAGES + 34][1] =
      "   [-TEXT (\u5358\u7d14\u306a Text \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 35][0] = "optionHTML";
    contents[MAX_MESSAGES + 35][1] = "   [-HTML (HTML \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3059\u308b\u3002)]";
    contents[MAX_MESSAGES + 36][0] = "optionPARAM";
    contents[MAX_MESSAGES + 36][1] =
      "   [-PARAM \u540d\u524d\u5f0f (\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3092\u8a2d\u5b9a)]";
    contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 37][1] = "XSL \u30d7\u30ed\u30bb\u30b9\u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
    contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 38][1] = "** \u30d1\u30fc\u30b5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f **";
    contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 39][1] = "\u30af\u30e9\u30b9\u30d1\u30b9\u3092\u30c1\u30a7\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044\u3002";
    contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 40][1] =
      "Java \u7528\u306b IBM \u306e XML \u30d1\u30fc\u30b5\u304c\u306a\u3044\u5834\u5408\u3001\u4ee5\u4e0b\u304b\u3089\u30c0\u30a6\u30f3\u30ed\u30fc\u30c9\u3067\u304d\u307e\u3059";
    contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 41][1] =
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
		contents[MAX_MESSAGES + 42][0] = "optionURIRESOLVER";
    contents[MAX_MESSAGES + 42][1] = "   [-URIRESOLVER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (URI \u3092\u89e3\u6c7a\u3059\u308b\u5834\u5408\u306f URIResolver \u3092\u4f7f\u7528\u3059\u308b)]";
		contents[MAX_MESSAGES + 43][0] = "optionENTITYRESOLVER";
    contents[MAX_MESSAGES + 43][1] = "   [-ENTITYRESOLVER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u3092\u89e3\u6c7a\u3059\u308b\u5834\u5408\u306f EntityResolver \u3092\u4f7f\u7528\u3059\u308b)]";
		contents[MAX_MESSAGES + 44][0] = "optionCONTENTHANDLER";
    contents[MAX_MESSAGES + 44][1] = "   [-CONTENTHANDLER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (\u51fa\u529b\u3092\u76f4\u5217\u5316\u3059\u308b\u5834\u5408\u306f ContentHandler \u3092\u4f7f\u7528\u3059\u308b)]";
    contents[MAX_MESSAGES + 45][0] = "optionLINENUMBERS";
    contents[MAX_MESSAGES + 45][1] = "   [-L \u30bd\u30fc\u30b9\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306b\u306f\u884c\u756a\u53f7\u3092\u4f7f\u7528\u3059\u308b]";
		
  }

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Error: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Warning: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "PATTERN ";

  /**
   * Get the lookup table. 
   *
   * @return The int to message lookup table.
   */
  public Object[][] getContents()
  {
    return contents;
  }
}
