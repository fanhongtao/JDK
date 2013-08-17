/*
 * @(#)XSLTErrorResources_zh_CN.java	1.8 01/12/04
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
public class XSLTErrorResources_zh_CN extends XSLTErrorResources
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
      "\u9519\u8bef\uff1a\u8868\u8fbe\u5f0f\u4e2d\u4e0d\u80fd\u51fa\u73b0 '{'";
  }

  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0} \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u5c5e\u6027\uff1a{1}";
  }

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  static
  {
    contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] =
      "sourceNode \u5728 xsl:apply-imports \u4e3a\u7a7a\uff01 ";
  }

  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;

  static
  {
    contents[ER_CANNOT_ADD][1] = "\u65e0\u6cd5\u5c06 {0} \u6dfb\u52a0\u5230 {1} \u4e2d";
  }

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  static
  {
    contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] =
      "sourceNode \u5728 handleApplyTemplatesInstruction \u4e3a\u7a7a\uff01";
  }

  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;

  static
  {
    contents[ER_NO_NAME_ATTRIB][1] = "{0} \u5fc5\u987b\u5177\u6709\u4e00\u4e2a\u540d\u79f0\u5c5e\u6027\u3002";
  }

  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  static
  {
    contents[ER_TEMPLATE_NOT_FOUND][1] = "\u672a\u627e\u5230\u547d\u540d\u7684\u6a21\u677f\uff1a{0}";
  }

  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  static
  {
    contents[ER_CANT_RESOLVE_NAME_AVT][1] =
      "\u65e0\u6cd5\u5728 xsl:call-template \u89e3\u6790\u540d\u79f0 AVI\u3002";
  }

  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;

  static
  {
    contents[ER_REQUIRES_ATTRIB][1] = "{0} \u8981\u6c42\u5c5e\u6027\uff1a{1}";
  }

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  static
  {
    contents[ER_MUST_HAVE_TEST_ATTRIB][1] =
      "{0} \u5fc5\u987b\u5177\u6709\u4e00\u4e2a 'test' \u5c5e\u6027\u3002";
  }

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  static
  {
    contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] =
      "\u5728\u7ea7\u522b\u5c5e\u6027\u4e2d\u51fa\u73b0\u9519\u8bef\u6570\u503c\uff1a{0}";
  }

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] =
      "processing-instruction \u540d\u79f0\u4e0d\u80fd\u4e3a 'xml'";
  }

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] =
      "processing-instruction \u540d\u79f0\u5fc5\u987b\u662f\u4e00\u4e2a\u6709\u6548\u7684 NCName\uff1a{0}";
  }

  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;

  static
  {
    contents[ER_NEED_MATCH_ATTRIB][1] =
      "{0} \u5fc5\u987b\u5177\u6709\u4e00\u4e2a\u4e0e\u6a21\u5f0f\u76f8\u5339\u914d\u7684\u5c5e\u6027\u3002";
  }

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  static
  {
    contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] =
      "{0} \u9700\u8981\u4e00\u4e2a\u540d\u79f0\u6216\u5339\u914d\u5c5e\u6027\u3002";
  }

  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  static
  {
    contents[ER_CANT_RESOLVE_NSPREFIX][1] =
      "\u65e0\u6cd5\u89e3\u6790\u540d\u79f0\u7a7a\u95f4\u524d\u7f00\uff1a{0}";
  }

  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;

  static
  {
    contents[ER_ILLEGAL_VALUE][1] = "xml:space \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u6570\u503c\uff1a{0}";
  }

  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;

  static
  {
    contents[ER_NO_OWNERDOC][1] =
      "\u5b50\u8282\u70b9\u6ca1\u6709\u4e00\u4e2a\u5c5e\u4e3b\u6587\u6863\uff01";
  }

  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  static
  {
    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement \u9519\u8bef\uff1a{0}";
  }

  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;

  static
  {
    contents[ER_NULL_CHILD][1] = "\u6b63\u5728\u5c1d\u8bd5\u6dfb\u52a0\u4e00\u4e2a\u7a7a\u7684\u5b50\u8282\u70b9\uff01";
  }

  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;

  static
  {
    contents[ER_NEED_SELECT_ATTRIB][1] = "{0} \u9700\u8981\u4e00\u4e2a\u9009\u62e9\u5c5e\u6027\u3002";
  }

  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;

  static
  {
    contents[ER_NEED_TEST_ATTRIB][1] =
      "xsl:when \u5fc5\u987b\u5177\u6709\u4e00\u4e2a 'test' \u5c5e\u6027\u3002";
  }

  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;

  static
  {
    contents[ER_NEED_NAME_ATTRIB][1] =
      "xsl:with-param \u5fc5\u987b\u5177\u6709\u4e00\u4e2a 'name' \u5c5e\u6027\u3002";
  }

  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  static
  {
    contents[ER_NO_CONTEXT_OWNERDOC][1] =
      "\u4e0a\u4e0b\u6587\u6ca1\u6709\u4e00\u4e2a\u5c5e\u4e3b\u6587\u6863\uff01";
  }

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  static
  {
    contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] =
      "\u65e0\u6cd5\u521b\u5efa XML TransformerFactory Liaison\uff1a{0}";
  }

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  static
  {
    contents[ER_PROCESS_NOT_SUCCESSFUL][1] =
      "Xalan: \u8fd0\u884c\u4e0d\u6210\u529f\u3002";
  }

  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;

  static
  {
    contents[ER_NOT_SUCCESSFUL][1] = "Xalan: \u4e0d\u6210\u529f\u3002";
  }

  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  static
  {
    contents[ER_ENCODING_NOT_SUPPORTED][1] = "\u4e0d\u53d7\u652f\u6301\u7684\u7f16\u7801\uff1a{0}";
  }

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  static
  {
    contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] =
      "\u65e0\u6cd5\u521b\u5efa TraceListener\uff1a{0}";
  }

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  static
  {
    contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] =
      "xsl:key \u9700\u8981\u4e00\u4e2a 'name' \u5c5e\u6027\uff01";
  }

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  static
  {
    contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] =
      "xsl:key \u9700\u8981\u4e00\u4e2a 'match' \u5c5e\u6027\uff01";
  }

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  static
  {
    contents[ER_KEY_REQUIRES_USE_ATTRIB][1] =
      "xsl:key \u9700\u8981\u4e00\u4e2a 'use' \u5c5e\u6027\uff01";
  }

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  static
  {
    contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] =
      "(StylesheetHandler) {0} \u9700\u8981\u4e00\u4e2a 'elements' \u5c5e\u6027\uff01";
  }

  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  static
  {
    contents[ER_MISSING_PREFIX_ATTRIB][1] =
      "(StylesheetHandler) {0} \u7f3a\u5c11 'prefix' \u5c5e\u6027";
  }

  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;

  static
  {
    contents[ER_BAD_STYLESHEET_URL][1] = "\u5f0f\u6837\u8868\u5355\u7684 URL \u9519\u8bef\uff1a{0}";
  }

  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;

  static
  {
    contents[ER_FILE_NOT_FOUND][1] = "\u672a\u627e\u5230\u5f0f\u6837\u8868\u5355\u6587\u4ef6\uff1a{0}";
  }

  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;

  static
  {
    contents[ER_IOEXCEPTION][1] =
      "\u5f0f\u6837\u8868\u5355\u6587\u4ef6\u4e2d\u5b58\u5728 IO \u5f02\u5e38\uff1a{0}";
  }

  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;

  static
  {
    contents[ER_NO_HREF_ATTRIB][1] =
      "(StylesheetHandler) \u65e0\u6cd5\u5728 {0} \u4e2d\u627e\u5230 href \u5c5e\u6027 ";
  }

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  static
  {
    contents[ER_STYLESHEET_INCLUDES_ITSELF][1] =
      "(StylesheetHandler) {0} \u76f4\u63a5\u6216\u95f4\u63a5\u5305\u542b\u81ea\u8eab\uff01";
  }

  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  static
  {
    contents[ER_PROCESSINCLUDE_ERROR][1] =
      "StylesheetHandler.processInclude \u9519\u8bef\uff0c{0}";
  }

  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;

  static
  {
    contents[ER_MISSING_LANG_ATTRIB][1] =
      "(StylesheetHandler) {0} \u7f3a\u5c11 'lang' \u5c5e\u6027";
  }

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  static
  {
    contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] =
      "(StylesheetHandler) \u5c06 {0} \u5143\u7d20\u653e\u9519\u4f4d\u7f6e\uff1f\uff1fcontainer \u7f3a\u5c11 'component' \u5143\u7d20 ";
  }

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  static
  {
    contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] =
      "\u4ec5\u80fd\u8f93\u51fa\u5230 Element\u3001DocumentFragment\u3001Document \u6216 PrintWriter\u3002";
  }

  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;

  static
  {
    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process \u9519\u8bef";
  }

  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;

  static
  {
    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode \u9519\u8bef\uff1a{0}";
  }

  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;

  static
  {
    contents[ER_NO_SELECT_EXPRESSION][1] =
      "\u9519\u8bef\uff01\u672a\u627e\u5230 xpath \u9009\u62e9\u8868\u8fbe\u5f0f (-select)\u3002";
  }

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  static
  {
    contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] =
      "\u65e0\u6cd5\u4e32\u884c\u5316\u4e00\u4e2a XSLProcessor\uff01";
  }

  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;

  static
  {
    contents[ER_NO_INPUT_STYLESHEET][1] =
      "\u672a\u6307\u5b9a\u5f0f\u6837\u8868\u5355\u8f93\u5165\uff01";
  }

  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  static
  {
    contents[ER_FAILED_PROCESS_STYLESHEET][1] =
      "\u8fd0\u884c\u5f0f\u6837\u8868\u5355\u9519\u8bef\uff01";
  }

  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;

  static
  {
    contents[ER_COULDNT_PARSE_DOC][1] = "\u65e0\u6cd5\u5206\u6790 {0} \u6587\u6863\uff01";
  }

  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  static
  {
    contents[ER_COULDNT_FIND_FRAGMENT][1] = "\u672a\u627e\u5230\u6bb5\uff1a{0}";
  }

  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;

  static
  {
    contents[ER_NODE_NOT_ELEMENT][1] =
      "\u6bb5\u6807\u8bc6\u7b26\u6307\u5411\u7684\u8282\u70b9\u4e0d\u662f\u4e00\u4e2a\u5143\u7d20\uff1a{0}";
  }

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  static
  {
    contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "\u6bcf\u4e2a\u8282\u70b9\u5fc5\u987b\u5177\u6709\u4e00\u4e2a\u5339\u914d\u6216\u540d\u79f0\u5c5e\u6027";
  }

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  static
  {
    contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "\u6a21\u677f\u5fc5\u987b\u5177\u6709\u4e00\u4e2a\u5339\u914d\u6216\u540d\u79f0\u5c5e\u6027";
  }

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  static
  {
    contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] =
      "\u65e0\u6cd5\u590d\u5236\u6587\u6863\u6846\u67b6\uff01";
  }

  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;

  static
  {
    contents[ER_CANT_CREATE_ITEM][1] =
      "\u65e0\u6cd5\u5728\u7ed3\u679c\u6811\u4e2d\u521b\u5efa\u9879\uff1a{0}";
  }

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VALUE][1] =
      "xml:space \u5728\u6e90 XML \u4e2d\u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u6570\u503c\uff1a{0}";
  }

  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  static
  {
    contents[ER_NO_XSLKEY_DECLARATION][1] =
      "\u5728 {0} \u4e2d\u672a\u58f0\u660e xsl:key\uff01";
  }

  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;

  static
  {
    contents[ER_CANT_CREATE_URL][1] = "\u9519\u8bef\uff01\u65e0\u6cd5\u5728\u4ee5\u4e0b\u7684 {0} \u4e2d\u521b\u5efa url\uff1a";
  }

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  static
  {
    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "\u4e0d\u652f\u6301 xsl:functions";
  }

  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;

  static
  {
    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory \u9519\u8bef";
  }

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] =
      "\u4e0d\u5141\u8bb8 (StylesheetHandler) {0} \u5728 stylesheet \u7684\u5185\u90e8\uff01";
  }

  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  static
  {
    contents[ER_RESULTNS_NOT_SUPPORTED][1] =
      "\u4e0d\u518d\u652f\u6301 result-ns\uff01\u8bf7\u4f7f\u7528 xsl:output \u66ff\u6362\u3002";
  }

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  static
  {
    contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] =
      "\u4e0d\u518d\u652f\u6301 default-space\uff01\u8bf7\u4f7f\u7528 xsl:strip-space \u6216 xsl:preserve-space \u66ff\u6362\u3002";
  }

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  static
  {
    contents[ER_INDENTRESULT_NOT_SUPPORTED][1] =
      "\u4e0d\u518d\u652f\u6301 indent-result\uff01\u8bf7\u4f7f\u7528 xsl:output \u66ff\u6362\u3002";
  }

  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;

  static
  {
    contents[ER_ILLEGAL_ATTRIB][1] =
      "(StylesheetHandler) {0} \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u5c5e\u6027\uff1a{1}";
  }

  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  static
  {
    contents[ER_UNKNOWN_XSL_ELEM][1] = "\u672a\u77e5\u7684 XSL \u5143\u7d20\uff1a{0}";
  }

  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;

  static
  {
    contents[ER_BAD_XSLSORT_USE][1] =
      "(StylesheetHandler) xsl:sort \u4ec5\u80fd\u4e0e xsl:apply-templates \u6216 xsl:for-each \u4e00\u8d77\u4f7f\u7528\u3002";
  }

  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;

  static
  {
    contents[ER_MISPLACED_XSLWHEN][1] =
      "(StylesheetHandler) \u5c06 xsl:when \u653e\u9519\u4f4d\u7f6e\uff01";
  }

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  static
  {
    contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:when \u4e0d\u662f xsl:choose \u7684\u7236\u8f88\uff01";
  }

  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  static
  {
    contents[ER_MISPLACED_XSLOTHERWISE][1] =
      "(StylesheetHandler) \u5c06 xsl:otherwise \u653e\u9519\u4f4d\u7f6e\uff01";
  }

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  static
  {
    contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:otherwise \u4e0d\u662f xsl:choose \u7684\u7236\u8f88\uff01";
  }

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] =
      "\u4e0d\u5141\u8bb8 (StylesheetHandler) {0} \u5728\u6a21\u677f\u7684\u5185\u90e8\uff01";
  }

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  static
  {
    contents[ER_UNKNOWN_EXT_NS_PREFIX][1] =
      "(StylesheetHandler) {0} \u6269\u5c55\u540d\u79f0\u7a7a\u95f4\u524d\u7f00 {1} \u672a\u77e5";
  }

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  static
  {
    contents[ER_IMPORTS_AS_FIRST_ELEM][1] =
      "(StylesheetHandler) \u5bfc\u5165\u4ec5\u80fd\u5728\u5f0f\u6837\u8868\u5355\u4e2d\u4f5c\u4e3a\u7b2c\u4e00\u4e2a\u5143\u7d20\u51fa\u73b0\uff01";
  }

  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;

  static
  {
    contents[ER_IMPORTING_ITSELF][1] =
      "(StylesheetHandler) {0} \u76f4\u63a5\u6216\u95f4\u63a5\u8f93\u5165\u81ea\u8eab\uff01";
  }

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VAL][1] =
      "(StylesheetHandler) " + "xml:space \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u6570\u503c\uff1a{0}";
  }

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  static
  {
    contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] =
      "processStylesheet \u4e0d\u6210\u529f\uff01";
  }

  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;

  static
  {
    contents[ER_SAX_EXCEPTION][1] = "SAX \u5f02\u5e38";
  }

  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  static
  {
    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "\u51fd\u6570\u4e0d\u53d7\u652f\u6301\uff01";
  }

  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;

  static
  {
    contents[ER_XSLT_ERROR][1] = "XSLT \u9519\u8bef";
  }

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  static
  {
    contents[ER_CURRENCY_SIGN_ILLEGAL][1] =
      "\u5728\u683c\u5f0f\u6a21\u5f0f\u5b57\u7b26\u4e32\u4e2d\u4e0d\u5141\u8bb8\u51fa\u73b0\u8d27\u5e01\u7b26\u53f7";
  }

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  static
  {
    contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] =
      "\u5728 Stylesheet DOM \u4e2d\u4e0d\u652f\u6301\u6587\u6863\u51fd\u6570\uff01";
  }

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  static
  {
    contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] =
      "\u65e0\u6cd5\u5206\u6790\u65e0\u524d\u7f00\u5206\u6790\u5668\u7684\u524d\u7f00\uff01";
  }

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  static
  {
    contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] =
      "\u91cd\u5b9a\u5411\u6269\u5c55\uff1a\u65e0\u6cd5\u5f97\u5230\u6587\u4ef6 - \u6587\u4ef6\u6216\u9009\u62e9\u5c5e\u6027\u5fc5\u987b\u8fd4\u56de\u6709\u6548\u7684\u5b57\u7b26\u4e32\u3002";
  }

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  static
  {
    contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] =
      "\u65e0\u6cd5\u5728\u91cd\u5b9a\u5411\u6269\u5c55\u4e2d\u6784\u5efa FormatterListener\uff01";
  }

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  static
  {
    contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] =
      "\u5728 exclude-result-prefixes \u4e2d\u7684\u524d\u7f00\u4e0d\u662f\u6709\u6548\u7684\uff1a{0}";
  }

  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;

  static
  {
    contents[ER_MISSING_NS_URI][1] =
      "\u5728\u6307\u5b9a\u7684\u524d\u7f00\u4e2d\u7f3a\u5c11 URI \u7684\u540d\u79f0\u7a7a\u95f4";
  }

  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  static
  {
    contents[ER_MISSING_ARG_FOR_OPTION][1] =
      "\u5728\u9009\u9879 {0} \u4e2d\u7f3a\u5c11\u53c2\u6570\uff1a";
  }

  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;

  static
  {
    contents[ER_INVALID_OPTION][1] = "\u65e0\u6548\u7684\u9009\u9879\uff1a{0}";
  }

  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  static
  {
    contents[ER_MALFORMED_FORMAT_STRING][1] = "\u683c\u5f0f\u4e0d\u6b63\u786e\u7684\u5b57\u7b26\u4e32\uff1a{0}";
  }

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  static
  {
    contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] =
      "xsl:stylesheet \u9700\u8981\u4e00\u4e2a 'version' \u5c5e\u6027\uff01";
  }

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] =
      "\u5c5e\u6027\uff1a{0} \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u6570\u503c\uff1a{1}";
  }

  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  static
  {
    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose \u9700\u8981 xsl:when";
  }

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  static
  {
    contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] =
      "\u4e0d\u5141\u8bb8\u5728 xls:for-each \u4e2d\u51fa\u73b0 xsl:apply-imports";
  }

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] =
      "\u65e0\u6cd5\u5728\u4e00\u4e2a\u8f93\u51fa DOM \u8282\u70b9\u4e2d\u4f7f\u7528 DTMLiaison...\u4f20\u9001\u4e00\u4e2a org.apache.xpath.DOM2Helper \u66ff\u6362\uff01 ";
  }

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_INPUT][1] =
      "\u65e0\u6cd5\u4e3a\u8f93\u5165 DOM \u8282\u70b9\u4f7f\u7528 DTMLiaison...\u4f20\u9001\u4e00\u4e2a org.apache.xpath.DOM2Helper\uff01";
  }

  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;

  static
  {
    contents[ER_CALL_TO_EXT_FAILED][1] =
      "\u8c03\u7528\u6269\u5c55\u5143\u7d20\u9519\u8bef\uff1a{0}";
  }

  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "\u5fc5\u987b\u5c06\u524d\u7f00\u89e3\u6790\u4e3a\u540d\u79f0\u7a7a\u95f4\uff1a{0}";
  }

  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "\u68c0\u6d4b\u5230\u65e0\u6548\u7684 UTF-16 \u4ee3\u7406\uff1a{0}\uff1f ";
  }

  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  static
  {
    contents[ER_XSLATTRSET_USED_ITSELF][1] =
      "xsl:attribute-set {0} \u88ab\u81ea\u8eab\u4f7f\u7528\uff0c\u5c06\u5bfc\u81f4\u65e0\u9650\u5faa\u73af\u3002 ";
  }

  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  static
  {
    contents[ER_CANNOT_MIX_XERCESDOM][1] =
      "\u975e Xerces-DOM \u8f93\u5165\u4e0e Xerces-DOM \u8f93\u51fa\u4e0d\u80fd\u6df7\u5408\u4f7f\u7528\uff01";
  }

  /** ER_TOO_MANY_LISTENERS          */
  public static final int ER_TOO_MANY_LISTENERS = 103;

  static
  {
    contents[ER_TOO_MANY_LISTENERS][1] =
      "addTraceListenersToStylesheet - TooManyListenersException";
  }

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;

  static
  {
    contents[ER_IN_ELEMTEMPLATEELEM_READOBJECT][1] =
      "\u5728 ElemTemplateElement.readObject \u4e2d\uff1a{0}";
  }

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  static
  {
    contents[ER_DUPLICATE_NAMED_TEMPLATE][1] =
      "\u627e\u5230\u591a\u4e2a\u547d\u540d\u7684\u6a21\u677f\uff1a{0}";
  }

  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  static
  {
    contents[ER_INVALID_KEY_CALL][1] =
      "\u65e0\u6548\u7684\u51fd\u6570\u8c03\u7528\uff1a\u4e0d\u5141\u8bb8\u8c03\u7528\u9012\u5f52\u5173\u952e\u8bcd()";
  }
  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  static
  {
    contents[ER_REFERENCING_ITSELF][1] =
      "\u53d8\u91cf {0} \u6b63\u5728\u76f4\u63a5\u6216\u95f4\u63a5\u5730\u5f15\u7528\u81ea\u8eab\uff01";
  }
  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  static
  {
    contents[ER_ILLEGAL_DOMSOURCE_INPUT][1] =
      "\u5728 newTemplate \u7684 DOMSource \u4e2d\uff0c\u8f93\u5165\u8282\u70b9\u4e0d\u80fd\u4e3a\u7a7a\uff01 ";
  }
	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  static
  {
    contents[ER_CLASS_NOT_FOUND_FOR_OPTION][1] =
			"\u5728\u9009\u9879\u4e2d\u672a\u627e\u5230\u7c7b\u6587\u4ef6{0}";
  }
	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  static
  {
    contents[ER_REQUIRED_ELEM_NOT_FOUND][1] =
			"\u672a\u627e\u5230\u9700\u8981\u7684\u5143\u7d20\uff1a{0}";
  }
  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  static
  {
    contents[ER_INPUT_CANNOT_BE_NULL][1] =
			"InputStream \u4e0d\u80fd\u4e3a\u7a7a";
  }
  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  static
  {
    contents[ER_URI_CANNOT_BE_NULL][1] =
			"URI \u4e0d\u80fd\u4e3a\u7a7a";
  }
  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  static
  {
    contents[ER_FILE_CANNOT_BE_NULL][1] =
			"File \u4e0d\u80fd\u4e3a\u7a7a";
  }
  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  static
  {
    contents[ER_SOURCE_CANNOT_BE_NULL][1] =
			"InputSource \u4e0d\u80fd\u4e3a\u7a7a";
  }
  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  static
  {
    contents[ER_CANNOT_OVERWRITE_CAUSE][1] =
			"\u65e0\u6cd5\u8986\u5199\u4e8b\u7531";
  }
  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  static
  {
    contents[ER_CANNOT_INIT_BSFMGR][1] =
			"\u65e0\u6cd5\u521d\u59cb\u5316 BSF \u7ba1\u7406\u5668";
  }
  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  static
  {
    contents[ER_CANNOT_CMPL_EXTENSN][1] =
			"\u65e0\u6cd5\u7f16\u8bd1\u6269\u5c55\u540d";
  }
  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  static
  {
    contents[ER_CANNOT_CREATE_EXTENSN][1] =
      "\u7531\u4e8e\u4ee5\u4e0b\u539f\u56e0\uff1a{1}\uff0c\u65e0\u6cd5\u521b\u5efa\u6269\u5c55\u540d\uff1a{0}";
  }
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  static
  {
    contents[ER_INSTANCE_MTHD_CALL_REQUIRES][1] =
      " Instance \u8c03\u7528\u65b9\u6cd5 {0} \u65f6\u9700\u8981\u5c06\u5bf9\u8c61\u5b9e\u4f8b\u4f5c\u4e3a\u7b2c\u4e00\u4e2a\u53c2\u6570\u3002";
  }
  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  static
  {
    contents[ER_INVALID_ELEMENT_NAME][1] =
      "\u6307\u5b9a\u4e86\u65e0\u6548\u7684\u5143\u7d20\u540d\u79f0 {0}";
  }
  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  static
  {
    contents[ER_ELEMENT_NAME_METHOD_STATIC][1] =
      "\u5143\u7d20\u540d\u79f0\u65b9\u6cd5\u5fc5\u987b\u662f\u9759\u6001\u7684 {0}";
  }
  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  static
  {
    contents[ER_EXTENSION_FUNC_UNKNOWN][1] =
             "\u6269\u5c55\u51fd\u6570 {0}\uff1a{1} \u672a\u77e5";
  }
  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  static
  {
    contents[ER_MORE_MATCH_CONSTRUCTOR][1] =
             "\u9002\u7528\u4e8e {0} \u7684\u6784\u9020\u5668\u7684\u591a\u4e2a\u6700\u4f73\u5339\u914d ";
  }
  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  static
  {
    contents[ER_MORE_MATCH_METHOD][1] =
             "\u9002\u7528\u4e8e\u65b9\u6cd5 {0} \u7684\u591a\u4e2a\u6700\u4f73\u5339\u914d";
  }
  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  static
  {
    contents[ER_MORE_MATCH_ELEMENT][1] =
             "\u9002\u7528\u4e8e\u5143\u7d20\u65b9\u6cd5 {0} \u7684\u591a\u4e2a\u6700\u4f73\u5339\u914d";
  }
  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  static
  {
    contents[ER_INVALID_CONTEXT_PASSED][1] =
             "\u4f20\u9001\u65e0\u6548\u7684\u4e0a\u4e0b\u6587\u6765\u6c42\u503c {0}";
  }
  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  static
  {
    contents[ER_POOL_EXISTS][1] =
             "\u6c60\u5df2\u5b58\u5728";
  }
  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  static
  {
    contents[ER_NO_DRIVER_NAME][1] =
             "\u672a\u6307\u5b9a\u9a71\u52a8\u5668\u540d\u79f0";
  }
  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  static
  {
    contents[ER_NO_URL][1] =
             "\u672a\u6307\u5b9a URL";
  }
  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  static
  {
    contents[ER_POOL_SIZE_LESSTHAN_ONE][1] =
             "\u6c60\u7684\u6570\u91cf\u4e0d\u8db3\u4e00\u4e2a\uff01";
  }
  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  static
  {
    contents[ER_INVALID_DRIVER][1] =
             "\u6307\u5b9a\u4e86\u65e0\u6548\u7684\u9a71\u52a8\u7a0b\u5e8f\u540d\u79f0\uff01";
  }
  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  static
  {
    contents[ER_NO_STYLESHEETROOT][1] =
             "\u672a\u627e\u5230\u5f0f\u6837\u8868\u5355\u7684\u6e90\u4f4d\u7f6e\uff01";
  }
  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  static
  {
    contents[ER_ILLEGAL_XMLSPACE_VALUE][1] =
         "\u5728 xml:space \u4e2d\u51fa\u73b0\u975e\u6cd5\u6570\u503c";
  }
  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  static
  {
    contents[ER_PROCESSFROMNODE_FAILED][1] =
         "processFromNode \u5931\u8d25";
  }
  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_LOAD][1] =
        "[ {0} ] \u8d44\u6e90\u65e0\u6cd5\u88c5\u5165\uff1a{1} \n {2} \t {3}";
  }
   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  static
  {
    contents[ER_BUFFER_SIZE_LESSTHAN_ZERO][1] =
        "\u7f13\u51b2\u533a\u5927\u5c0f <=0";
  }
  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  static
  {
    contents[ER_UNKNOWN_ERROR_CALLING_EXTENSION][1] =
        "\u8c03\u7528\u6269\u5c55\u65f6\u51fa\u73b0\u672a\u77e5\u9519\u8bef";
  }
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  static
  {
    contents[ER_NO_NAMESPACE_DECL][1] =
        "\u524d\u7f00 {0} \u672a\u58f0\u660e\u76f8\u5e94\u7684\u540d\u79f0\u7a7a\u95f4";
  }
  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  static
  {
    contents[ER_ELEM_CONTENT_NOT_ALLOWED][1] =
        "\u5728 lang=javaclass {0} \u4e2d\u4e0d\u5141\u8bb8\u51fa\u73b0\u5143\u7d20\u5185\u5bb9";
  }   
  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  static
  {
    contents[ER_STYLESHEET_DIRECTED_TERMINATION][1] =
        "\u6307\u5bfc\u5f0f\u6837\u8868\u5355\u7ec8\u6b62";
  }
  
   /** 1 or 2   */
  public static final int ER_ONE_OR_TWO = 141;

  static
  {
    contents[ER_ONE_OR_TWO][1] =
        "1 \u6216 2";
  }
  
   /** 2 or 3   */
  public static final int ER_TWO_OR_THREE = 142;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
        "2 \u6216 3";
  }
  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  static
  {
    contents[ER_COULD_NOT_LOAD_RESOURCE][1] =
        "\u65e0\u6cd5\u88c5\u5165 {0}\uff08\u68c0\u67e5 CLASSPATH\uff09\uff0c\u6b63\u5728\u4f7f\u7528\u7f3a\u7701\u503c";
  }
  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  static
  {
    contents[ER_CANNOT_INIT_DEFAULT_TEMPLATES][1] =
        "\u65e0\u6cd5\u521d\u59cb\u5316\u7f3a\u7701\u6a21\u677f";
  }
  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

  static
  {
    contents[ER_RESULT_NULL][1] =
        "\u7ed3\u679c\u4e0d\u5e94\u4e3a\u7a7a";
  }
    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  static
  {
    contents[ER_RESULT_COULD_NOT_BE_SET][1] =
        "\u65e0\u6cd5\u8bbe\u7f6e\u7ed3\u679c";
  }
  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  static
  {
    contents[ER_NO_OUTPUT_SPECIFIED][1] =
        "\u672a\u6307\u5b9a\u8f93\u51fa";
  }
  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  static
  {
    contents[ER_CANNOT_TRANSFORM_TO_RESULT_TYPE][1] =
        "\u65e0\u6cd5\u53d8\u6362\u5230\u4e00\u4e2a\u7c7b\u578b\u7684\u7ed3\u679c\u4e2d {0}";
  }
  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  static
  {
    contents[ER_CANNOT_TRANSFORM_SOURCE_TYPE][1] =
        "\u65e0\u6cd5\u53d8\u6362\u4e00\u4e2a\u7c7b\u578b\u6e90 {0}";
  }
  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  static
  {
    contents[ER_NULL_CONTENT_HANDLER][1] =
        "\u7a7a\u7684\u5185\u5bb9\u53e5\u67c4";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "\u7a7a\u7684\u9519\u8bef\u53e5\u67c4";
  }
  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  static
  {
    contents[ER_CANNOT_CALL_PARSE][1] =
        "\u5982\u679c\u672a\u8bbe\u7f6e ContentHandler\uff0c\u5219\u65e0\u6cd5\u8c03\u7528\u89e3\u6790";
  }
  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  static
  {
    contents[ER_NO_PARENT_FOR_FILTER][1] =
        "\u5728\u8fc7\u6ee4\u5668\u4e2d\u65e0\u7236\u8f88";
  }
  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  static
  {
    contents[ER_NO_STYLESHEET_IN_MEDIA][1] =
         "\u5728 {0}\uff0cmedia= {1} \u4e2d\u672a\u627e\u5230\u5f0f\u6837\u8868\u5355 ";
  }
  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  static
  {
    contents[ER_NO_STYLESHEET_PI][1] =
         "\u5728 {0} \u4e2d\u672a\u627e\u5230 xml-stylesheet PI\uff1a ";
  }
  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  static
  {
    contents[ER_NO_DEFAULT_IMPL][1] =
         "\u672a\u627e\u5230\u7f3a\u7701\u6267\u884c ";
  }
  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  static
  {
    contents[ER_CHUNKEDINTARRAY_NOT_SUPPORTED][1] =
       "\u76ee\u524d\u4e0d\u652f\u6301 ChunkedIntArray({0}) ";
  }
  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  static
  {
    contents[ER_OFFSET_BIGGER_THAN_SLOT][1] =
       "\u504f\u79fb\u6bd4\u69fd\u7565\u5927";
  }
  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  static
  {
    contents[ER_COROUTINE_NOT_AVAIL][1] =
       "\u534f\u540c\u7a0b\u5e8f\u4e0d\u53ef\u7528\uff0cid={0}";
  }
  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  static
  {
    contents[ER_COROUTINE_CO_EXIT][1] =
       "CoroutineManager \u63a5\u6536\u5230 co_exit() \u8bf7\u6c42";
  }
  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  static
  {
    contents[ER_COJOINROUTINESET_FAILED][1] =
       "co_joinCoroutineSet() \u5931\u8d25";
  }
  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  static
  {
    contents[ER_COROUTINE_PARAM][1] =
       "\u534f\u540c\u7a0b\u5e8f\u53c2\u6570\u9519\u8bef ({0})";
  }
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  static
  {
    contents[ER_PARSER_DOTERMINATE_ANSWERS][1] =
       "\nUNEXPECTED: \u5206\u6790\u5668 doTerminate \u56de\u7b54 {0}";
  }
  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  static
  {
    contents[ER_NO_PARSE_CALL_WHILE_PARSING][1] =
       "\u5f53\u5206\u6790\u65f6\u53ef\u80fd\u4e0d\u4f1a\u8c03\u7528\u5206\u6790\u51fd\u6570";
  }
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  static
  {
    contents[ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\u9519\u8bef\uff1a\u5728 axis {0} \u4e2d\u8f93\u5165\u7684\u8fed\u4ee3\u7a0b\u5e8f\u65e0\u6cd5\u6267\u884c";
  }
  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  static
  {
    contents[ER_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\u9519\u8bef\uff1a\u5728 axis {0} \u4e2d\u7684\u8fed\u4ee3\u7a0b\u5e8f\u65e0\u6cd5\u6267\u884c";
  }
  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  static
  {
    contents[ER_ITERATOR_CLONE_NOT_SUPPORTED][1] =
       "\u4e0d\u652f\u6301\u590d\u5236\u8fed\u4ee3\u7a0b\u5e8f";
  }
  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  static
  {
    contents[ER_UNKNOWN_AXIS_TYPE][1] =
       "\u672a\u77e5\u7684 axis \u904d\u5386\u7c7b\u578b\uff1a{0}";
  }
  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  static
  {
    contents[ER_AXIS_NOT_SUPPORTED][1] =
       "\u4e0d\u652f\u6301 axis \u904d\u5386\u5668\uff1a{0}";
  }
  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  static
  {
    contents[ER_NO_DTMIDS_AVAIL][1] =
       "\u65e0\u6cd5\u4f7f\u7528\u591a\u4e2a DTM ID";
  }
  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  static
  {
    contents[ER_NOT_SUPPORTED][1] =
       "\u4e0d\u652f\u6301\uff1a{0}";
  }
  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  static
  {
    contents[ER_NODE_NON_NULL][1] =
       "\u8282\u70b9\u5728 getDTMHandleFromNode \u4e2d\u5fc5\u987b\u975e\u7a7a";
  }
  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  static
  {
    contents[ER_COULD_NOT_RESOLVE_NODE][1] =
       "\u65e0\u6cd5\u5c06\u8282\u70b9\u89e3\u6790\u4e3a\u53e5\u67c4";
  }
  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  static
  {
    contents[ER_STARTPARSE_WHILE_PARSING][1] =
       "\u5728\u5206\u6790\u65f6\u53ef\u80fd\u4f1a\u8c03\u7528 startParse ";
  }
  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  static
  {
    contents[ER_STARTPARSE_NEEDS_SAXPARSER][1] =
       "startParse \u9700\u8981\u4e00\u4e2a\u975e\u7a7a\u7684 SAXParser";
  }
  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  static
  {
    contents[ER_COULD_NOT_INIT_PARSER][1] =
       "\u65e0\u6cd5\u521d\u59cb\u5316\u5206\u6790\u5668";
  }
  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  static
  {
    contents[ER_PROPERTY_VALUE_BOOLEAN][1] =
       "\u5c5e\u6027\u503c {0} \u5e94\u5f53\u662f\u4e00\u4e2a\u5e03\u5c14\u5b9e\u4f8b";
  }
  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  static
  {
    contents[ER_EXCEPTION_CREATING_POOL][1] =
       "\u521b\u5efa\u6c60\u7684\u65b0\u5b9e\u4f8b\u65f6\u51fa\u73b0\u5f02\u5e38";
  }
  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  static
  {
    contents[ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE][1] =
       "\u8def\u5f84\u5305\u542b\u65e0\u6548\u7684\u6362\u7801\u5e8f\u5217";
  }
  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  static
  {
    contents[ER_SCHEME_REQUIRED][1] =
       "\u9700\u8981\u914d\u7f6e\uff01";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  static
  {
    contents[ER_NO_SCHEME_IN_URI][1] =
       "\u5728 URI \u4e2d\u672a\u627e\u5230\u914d\u7f6e\uff1a{0}";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  static
  {
    contents[ER_NO_SCHEME_INURI][1] =
       "\u5728 URI \u4e2d\u672a\u627e\u5230\u914d\u7f6e";
  }
  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  static
  {
    contents[ER_PATH_INVALID_CHAR][1] =
       "\u8def\u5f84\u5305\u542b\u65e0\u6548\u7684\u5b57\u7b26\uff1a{0}";
  }
  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  static
  {
    contents[ER_SCHEME_FROM_NULL_STRING][1] =
       "\u65e0\u6cd5\u5728\u7a7a\u7684\u5b57\u7b26\u4e32\u4e2d\u8bbe\u7f6e\u914d\u7f6e";
  }
  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  static
  {
    contents[ER_SCHEME_NOT_CONFORMANT][1] =
       "\u914d\u7f6e\u4e0d\u4e00\u81f4\u3002";
  }
  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  static
  {
    contents[ER_HOST_ADDRESS_NOT_WELLFORMED][1] =
       "\u4e3b\u673a\u5730\u5740\u7684\u683c\u5f0f\u4e0d\u6b63\u786e";
  }
  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  static
  {
    contents[ER_PORT_WHEN_HOST_NULL][1] =
       "\u5f53\u4e3b\u673a\u4e3a\u7a7a\u65f6\u65e0\u6cd5\u8bbe\u7f6e\u7aef\u53e3";
  }
  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  static
  {
    contents[ER_INVALID_PORT][1] =
       "\u65e0\u6548\u7684\u7aef\u53e3\u53f7";
  }
  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  static
  {
    contents[ER_FRAG_FOR_GENERIC_URI][1] =
       "\u4ec5\u5728\u4e00\u822c\u7684 URI \u4e2d\u8bbe\u7f6e\u6bb5";
  }
  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  static
  {
    contents[ER_FRAG_WHEN_PATH_NULL][1] =
       "\u5f53\u8def\u5f84\u4e3a\u7a7a\u65f6\u65e0\u6cd5\u8bbe\u7f6e\u6bb5";
  }
  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  static
  {
    contents[ER_FRAG_INVALID_CHAR][1] =
       "\u6bb5\u4e2d\u5305\u542b\u65e0\u6548\u5b57\u7b26";
  }
  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  static
  {
    contents[ER_PARSER_IN_USE][1] =
        "\u5206\u6790\u5668\u6b63\u5728\u4f7f\u7528";
  }
  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  static
  {
    contents[ER_CANNOT_CHANGE_WHILE_PARSING][1] =
        "\u5728\u5206\u6790\u65f6\u65e0\u6cd5\u6539\u53d8 {0} {1}";
  }
  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  static
  {
    contents[ER_SELF_CAUSATION_NOT_PERMITTED][1] =
        "\u4e0d\u5141\u8bb8\u81ea\u8eab\u5f15\u8d77\u7ed3\u679c";
  }
  
   /** src attribute not yet supported for  */
  public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  static
  {
    contents[ER_COULD_NOT_FIND_EXTERN_SCRIPT][1] =
       "\u65e0\u6cd5\u5728 {0} \u4e2d\u5230\u8fbe\u5916\u90e8\u811a\u672c";
  }
  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_FIND][1] =
        "\u672a\u627e\u5230 [ {0} ] \u8d44\u6e90\u3002\n {1}";
  }
  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  static
  {
    contents[ER_OUTPUT_PROPERTY_NOT_RECOGNIZED][1] =
        "\u65e0\u6cd5\u8bc6\u522b\u8f93\u51fa\u5c5e\u6027\uff1a{0}";
  }
  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  static
  {
    contents[ER_NO_USERINFO_IF_NO_HOST][1] =
        "\u5982\u679c\u672a\u6307\u5b9a\u4e3b\u673a\uff0c\u53ef\u80fd\u4e0d\u4f1a\u6307\u5b9a\u7528\u6237\u4fe1\u606f";
  }
  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  static
  {
    contents[ER_NO_PORT_IF_NO_HOST][1] =
        "\u5982\u679c\u672a\u6307\u5b9a\u4e3b\u673a\uff0c\u53ef\u80fd\u4e0d\u4f1a\u6307\u5b9a\u7aef\u53e3";
  }
  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  static
  {
    contents[ER_NO_QUERY_STRING_IN_PATH][1] =
        "\u5728\u8def\u5f84\u548c\u67e5\u8be2\u5b57\u7b26\u4e32\u4e2d\uff0c\u65e0\u6cd5\u6307\u5b9a\u67e5\u8be2\u5b57\u7b26\u4e32 ";
  }
  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  static
  {
    contents[ER_NO_FRAGMENT_STRING_IN_PATH][1] =
        "\u5728\u8def\u5f84\u548c\u6bb5\u4e2d\u65e0\u6cd5\u6307\u5b9a\u6bb5 ";
  }
  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  static
  {
    contents[ER_CANNOT_INIT_URI_EMPTY_PARMS][1] =
        "\u65e0\u6cd5\u4f7f\u7528\u7a7a\u7684\u53c2\u6570\u521d\u59cb\u5316 URI";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  static
  {
    contents[ER_FAILED_CREATING_ELEMLITRSLT][1] =
        "\u521b\u5efa ElemLiteralResult \u5b9e\u4f8b\u5931\u8d25";
  }  
  
   /** Priority value does not contain a parsable number   */
  public static final int ER_PRIORITY_NOT_PARSABLE = 204;

  static
  {
    contents[ER_PRIORITY_NOT_PARSABLE][1] =
        "\u5c5e\u6027\u503c\u4e0d\u5305\u542b\u53ef\u5206\u6790\u6570\u5b57";
  }
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

  static
  {
    contents[ER_VALUE_SHOULD_EQUAL][1] =
        "{0} \u7684\u503c\u5e94\u5f53\u662f\u201c\u662f\u201d\u6216\u201c\u975e\u201d";
  }
 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  static
  {
    contents[ER_FAILED_CALLING_METHOD][1] =
        "\u8c03\u7528 {0} \u65b9\u6cd5\u5931\u8d25";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  static
  {
    contents[ER_FAILED_CREATING_ELEMTMPL][1] =
        "\u521b\u5efa ElemTemplateElement \u5b9e\u4f8b\u5931\u8d25";
  }
  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  static
  {
    contents[ER_CHARS_NOT_ALLOWED][1] =
        "\u5728\u6587\u6863\u6b64\u5904\u4e0d\u5141\u8bb8\u51fa\u73b0\u5b57\u7b26";
  }
  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  static
  {
    contents[ER_ATTR_NOT_ALLOWED][1] =
        "\"{0}\" \u5c5e\u6027\u4e0d\u5141\u8bb8\u5728 {1} \u5143\u7d20\u4e2d\u51fa\u73b0\uff01 ";
  }
  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

  static
  {
    contents[ER_METHOD_NOT_SUPPORTED][1] =
        "\u65b9\u6cd5\u4ecd\u4e0d\u53d7\u652f\u6301 ";
  }
 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  static
  {
    contents[ER_BAD_VALUE][1] =
     "{0} \u9519\u8bef\u6570\u503c {1} ";
  }
  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_FOUND][1] =
     "\u672a\u627e\u5230 {0} \u5c5e\u6027\u503c";
  }
  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_RECOGNIZED][1] =
     "\u65e0\u6cd5\u8bc6\u522b {0} \u5c5e\u6027\u503c";
  }

  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  static
  {
    contents[ER_INCRSAXSRCFILTER_NOT_RESTARTABLE][1] =
     "\u5f53\u524d\u65e0\u6cd5\u91cd\u65b0\u542f\u52a8 IncrementalSAXSource_Filter";
  }
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  static
  {
    contents[ER_XMLRDR_NOT_BEFORE_STARTPARSE][1] =
     "XMLReader \u672a\u5728 startParse \u8bf7\u6c42\u4e4b\u524d\u51fa\u73b0";
  }
  
  /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NULL_URI_NAMESPACE = 216;

  static
  {
    contents[ER_NULL_URI_NAMESPACE][1] =
     "\u8bd5\u56fe\u7528\u7a7a URI \u751f\u6210\u540d\u79f0\u7a7a\u95f4\u524d\u7f00";
  }    
  
  
  /*
    /**  Cannot find SAX1 driver class    *
  public static final int ER_CANNOT_FIND_SAX1_DRIVER = 190;

  static
  {
    contents[ER_CANNOT_FIND_SAX1_DRIVER][1] =
      "Cannot find SAX1 driver class {0}";
  }
  
   /**  SAX1 driver class {0} found but cannot be loaded    *
  public static final int ER_SAX1_DRIVER_NOT_LOADED = 191;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_LOADED][1] =
      "SAX1 driver class {0} found but cannot be loaded";
  }
  
   /**  SAX1 driver class {0} found but cannot be instantiated    *
  public static final int ER_SAX1_DRIVER_NOT_INSTANTIATED = 192;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_INSTANTIATED][1] =
      "SAX1 driver class {0} loaded but cannot be instantiated";
  }
  
   /**  SAX1 driver class {0} does not implement org.xml.sax.Parser    *
  public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 193;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER][1] =
      "SAX1 driver class {0} does not implement org.xml.sax.Parser";
  }
  
   /**  System property org.xml.sax.parser not specified    *
  public static final int ER_PARSER_PROPERTY_NOT_SPECIFIED = 194;

  static
  {
    contents[ER_PARSER_PROPERTY_NOT_SPECIFIED][1] =
      "System property org.xml.sax.parser not specified";
  }
  
   /**  Parser argument must not be null    *
  public static final int ER_PARSER_ARG_CANNOT_BE_NULL = 195;

  static
  {
    contents[ER_PARSER_ARG_CANNOT_BE_NULL][1] =
      "Parser argument must not be null";
  }
  
   /**  Feature:    *
  public static final int ER_FEATURE = 196;

  static
  {
    contents[ER_FEATURE][1] =
        "Feature: {0}";
  }
  
   /**  Property:    *
  public static final int ER_PROPERTY = 197;

  static
  {
    contents[ER_PROPERTY][1] =
        "Property: {0}";
  }
  
   /** Null Entity Resolver  *
  public static final int ER_NULL_ENTITY_RESOLVER = 198;

  static
  {
    contents[ER_NULL_ENTITY_RESOLVER][1] =
        "Null entity resolver";
  }
  
   /** Null DTD handler  *
  public static final int ER_NULL_DTD_HANDLER = 199;

  static
  {
    contents[ER_NULL_DTD_HANDLER][1] =
        "Null DTD handler";
  }
  
 */ 
  

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  public static final int WG_FOUND_CURLYBRACE = 1;

  static
  {
    contents[WG_FOUND_CURLYBRACE + MAX_CODE][1] =
      "\u5df2\u627e\u5230 '}'\uff0c\u4f46\u672a\u6253\u5f00\u5c5e\u6027\u6a21\u677f\uff01";
  }

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  static
  {
    contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] =
      "\u8b66\u544a\uff1a'count' \u5c5e\u6027\u4e0e xsl:number! Target = {0} \u4e2d\u7684\u7956\u5148\u4e0d\u5339\u914d";
  }

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  static
  {
    contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] =
      "\u65e7\u8bed\u6cd5\uff1a'expr' \u5c5e\u6027\u7684\u540d\u79f0\u5df2\u7ecf\u53d8\u4e3a 'select'\u3002";
  }

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  static
  {
    contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] =
      " Xalan \u4ecd\u7136\u65e0\u6cd5\u5904\u7406 format-number \u51fd\u6570\u4e2d\u7684\u8bed\u8a00\u73af\u5883\u540d\u79f0\u3002";
  }

  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;

  static
  {
    contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] =
      "\u8b66\u544a\uff1a\u65e0\u6cd5\u627e\u5230 xml:lang={0} \u4e2d\u7684\u8bed\u8a00\u73af\u5883 ";
  }

  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "\u4ece {0} \u4e2d\u65e0\u6cd5\u4ea7\u751f URL\uff1a ";
  }

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  static
  {
    contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] =
      "\u65e0\u6cd5\u88c5\u5165\u8bf7\u6c42\u6587\u6863\uff1a{0}";
  }

  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;

  static
  {
    contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] =
      "\u5728 <sort xml:lang={0} \u4e2d\u65e0\u6cd5\u627e\u5230\u6574\u7406\u673a ";
  }

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  static
  {
    contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] =
      "\u65e7\u8bed\u6cd5\uff1a\u51fd\u6570\u6307\u4ee4\u5e94\u5f53\u4f7f\u7528 {0} \u7684\u4e00\u4e2a URL ";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] =
      "\u4e0d\u53d7\u652f\u6301\u7684\u7f16\u7801\uff1a{0}\uff0c\u6b63\u5728\u4f7f\u7528 UTF-8";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] =
      "\u4e0d\u53d7\u652f\u6301\u7684\u7f16\u7801\uff1a{0}\uff0c\u6b63\u5728 Java {1}";
  }

  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;

  static
  {
    contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] =
      "\u53d1\u73b0\u4e13\u4e00\u6027\u51b2\u7a81\uff1a{0} \u5c06\u4f7f\u7528\u4e0a\u6b21\u5728\u5f0f\u6837\u8868\u5355\u4e2d\u627e\u5230\u7684\u7f16\u7801\u3002";
  }

  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;

  static
  {
    contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] =
      "========= \u5206\u6790\u548c\u51c6\u5907 {0} ==========";
  }

  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;

  static
  {
    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "\u5c5e\u6027\u6a21\u677f\uff0c{0}";
  }

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;

  static
  {
    contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] =
      "\u5728 xsl:strip-space \u548c xsl:preserve-space \u4e4b\u95f4\u51fa\u73b0\u5339\u914d\u51b2\u7a81 ";
  }

  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;

  static
  {
    contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] =
      "Xalan \u4ecd\u4e0d\u5904\u7406 {0} \u5c5e\u6027\uff01";
  }

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  static
  {
    contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] =
      "\u672a\u627e\u5230\u5341\u8fdb\u5236\u683c\u5f0f\u7684\u58f0\u660e\uff1a{0}";
  }

  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;

  static
  {
    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "\u7f3a\u5c11\u6216\u4e0d\u6b63\u786e\u7684 XSLT Namespace\u3002 ";
  }

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  static
  {
    contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] =
      "\u4ec5\u5141\u8bb8\u58f0\u660e\u4e00\u4e2a\u7f3a\u7701 xsl:decimal-format\u3002 ";
  }

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  static
  {
    contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] =
      "xsl:decimal-format \u7684\u540d\u79f0\u5fc5\u987b\u662f\u552f\u4e00\u7684\u3002\u540d\u79f0 \"{0}\" \u5df2\u590d\u5236\u3002";
  }

  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] =
      "{0} \u5b58\u5728\u4e00\u4e2a\u975e\u6cd5\u5c5e\u6027\uff1a{1}";
  }

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  static
  {
    contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] =
      "\u65e0\u6cd5\u89e3\u6790\u540d\u79f0\u7a7a\u95f4\u524d\u7f00\uff1a{0}\u3002\u5ffd\u7565\u8282\u70b9\u3002";
  }

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  static
  {
    contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] =
      "xsl:stylesheet \u9700\u8981 'version' \u5c5e\u6027\uff01";
  }

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] =
      "\u975e\u6cd5\u5c5e\u6027\u540d\u79f0\uff1a{0}";
  }

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] =
      "\u5728\u5c5e\u6027\u4e2d\u4f7f\u7528\u975e\u6cd5\u6570\u503c {0}\uff1a{1}";
  }

  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;

  static
  {
    contents[WG_EMPTY_SECOND_ARG + MAX_CODE][1] =
      "\u6765\u81ea\u6587\u6863\u5c5e\u6027\u7b2c\u4e8c\u4e2a\u53c2\u6570\u7684\u7ed3\u679c\u8282\u70b9\u96c6\u4e3a\u7a7a\u3002\u5c06\u4f7f\u7528\u7b2c\u4e00\u4e2a\u53c2\u6570\u3002";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "zh";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "zh";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "zh";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "createMessage \u53c2\u6570\u8d85\u8fc7\u8303\u56f4";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "\u8c03\u7528 messageFormat \u65f6\u51fa\u73b0\u610f\u5916\u60c5\u51b5  ";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan \u7248\u672c ";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "\u662f";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "\u884c #";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "\u5217 #";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: done";
    contents[MAX_MESSAGES + 11][0] = "xslProc_option";
    contents[MAX_MESSAGES + 11][1] = "Xalan-J \u547d\u4ee4\u884c\u8fd0\u884c\u7c7b\u9009\u9879\uff1a";
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
      "   [-PARSER \u5b8c\u5168\u7b26\u5408\u5206\u6790\u8054\u7edc\u7684\u7c7b\u540d\u79f0]";
    contents[MAX_MESSAGES + 18][0] = "optionE";
    contents[MAX_MESSAGES + 18][1] = "   [-E\uff08\u4e0d\u6269\u5c55\u5b9e\u4f53\u5f15\u7528\uff09]";
    contents[MAX_MESSAGES + 19][0] = "optionV";
    contents[MAX_MESSAGES + 19][1] = "   [-E\uff08\u4e0d\u6269\u5c55\u5b9e\u4f53\u5f15\u7528\uff09]";
    contents[MAX_MESSAGES + 20][0] = "optionQC";
    contents[MAX_MESSAGES + 20][1] =
      "   [-QC\uff08\u9759\u6b62\u6a21\u5f0f\u51b2\u7a81\u8b66\u544a\uff09]";
    contents[MAX_MESSAGES + 21][0] = "optionQ";
    contents[MAX_MESSAGES + 21][1] = "   [-Q\uff08\u9759\u6b62\u6a21\u5f0f\uff09]";
    contents[MAX_MESSAGES + 22][0] = "optionLF";
    contents[MAX_MESSAGES + 22][1] =
      "   [-LF\uff08\u4ec5\u5728\u8f93\u51fa\u65f6\u4f7f\u7528\u6362\u884c {\u7f3a\u7701\u4e3a CR/LF}\uff09]";
    contents[MAX_MESSAGES + 23][0] = "optionCR";
    contents[MAX_MESSAGES + 23][1] =
      "   [-CR\uff08\u4ec5\u5728\u8f93\u51fa\u65f6\u4f7f\u7528\u6362\u884c {\u7f3a\u7701\u4e3a CR/LF}\uff09]";
    contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
    contents[MAX_MESSAGES + 24][1] =
      "   [-ESCAPE\uff08\u907f\u514d\u4f7f\u7528\u7684\u5b57\u7b26 {\u7f3a\u7701\u662f <>&\"\'\\r\\n}\uff09]";
    contents[MAX_MESSAGES + 25][0] = "optionINDENT";
    contents[MAX_MESSAGES + 25][1] =
      "   [-INDENT\uff08\u63a7\u5236\u7f29\u8fdb\u7a7a\u683c\u7684\u6570\u91cf {\u7f3a\u7701\u662f 0}\uff09]";
    contents[MAX_MESSAGES + 26][0] = "optionTT";
    contents[MAX_MESSAGES + 26][1] =
      "   [-TT\uff08\u8ddf\u8e2a\u8c03\u7528\u7684\u6a21\u677f\u3002\uff09]";
    contents[MAX_MESSAGES + 27][0] = "optionTG";
    contents[MAX_MESSAGES + 27][1] =
      "   [-TG\uff08\u8ddf\u8e2a\u53d1\u751f\u4e8b\u4ef6\u3002\uff09]";
    contents[MAX_MESSAGES + 28][0] = "optionTS";
    contents[MAX_MESSAGES + 28][1] = "   [-TS\uff08\u8ddf\u8e2a\u6bcf\u4e2a\u9009\u4e2d\u4e8b\u4ef6\u3002\uff09]";
    contents[MAX_MESSAGES + 29][0] = "optionTTC";
    contents[MAX_MESSAGES + 29][1] =
      "   [-TTC\uff08\u8ddf\u8e2a\u8fd0\u884c\u7684\u5b50\u6a21\u677f\u3002\uff09]";
    contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
    contents[MAX_MESSAGES + 30][1] =
      "   [-TCLASS\uff08\u7528\u4e8e\u8ddf\u8e2a\u6269\u5c55\u540d\u7684 TraceListener \u7c7b\u3002\uff09]";
    contents[MAX_MESSAGES + 31][0] = "optionVALIDATE";
    contents[MAX_MESSAGES + 31][1] =
      "   [-VALIDATE\uff08\u8bbe\u7f6e\u662f\u5426\u201c\u786e\u8ba4\u201d\u3002\u7f3a\u7701\u60c5\u51b5\u4e0b\u5173\u95ed\u201c\u786e\u8ba4\u201d\u3002\uff09]";
    contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
    contents[MAX_MESSAGES + 32][1] =
      "   [-EDUMP {\u53ef\u9009\u6587\u4ef6\u540d}\uff08\u51fa\u73b0\u9519\u8bef\u65f6\u786e\u4fdd\u6808\u8f6c\u50a8\u3002\uff09]";
    contents[MAX_MESSAGES + 33][0] = "optionXML";
    contents[MAX_MESSAGES + 33][1] =
      "   [-XML (\u4f7f\u7528 XML \u683c\u5f0f\u6807\u8bc6\u7b26\u5e76\u6dfb\u52a0 XML \u5934\u3002\uff09]";
    contents[MAX_MESSAGES + 34][0] = "optionTEXT";
    contents[MAX_MESSAGES + 34][1] =
      "   [-TEXT\uff08\u4f7f\u7528\u7b80\u5355\u7684 Text \u683c\u5f0f\u6807\u8bc6\u7b26\u3002\uff09]";
    contents[MAX_MESSAGES + 35][0] = "optionHTML";
    contents[MAX_MESSAGES + 35][1] = "   [-HTML\uff08\u4f7f\u7528 HTML \u683c\u5f0f\u6807\u8bc6\u7b26\u3002\uff09]";
    contents[MAX_MESSAGES + 36][0] = "optionPARAM";
    contents[MAX_MESSAGES + 36][1] =
      "   [-PARAM \u540d\u79f0\u6269\u5c55\uff08\u8bbe\u7f6e\u4e00\u4e2a stylesheet \u53c2\u6570\uff09]";
    contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 37][1] = "XSL \u8fd0\u884c\u4e0d\u6210\u529f\u3002 ";
    contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 38][1] = "** \u672a\u627e\u5230\u5206\u6790\u5668 **";
    contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 39][1] = "\u8bf7\u68c0\u67e5\u60a8\u7684\u7c7b\u8def\u5f84\u3002";
    contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 40][1] =
      "\u5982\u679c\u60a8\u6ca1\u6709 IBM \u7528\u4e8e Java \u7684 XML \u5206\u6790\u5668\uff0c\u60a8\u53ef\u4ece";
    contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 41][1] =
      "IBM AlphaWorks\uff1ahttp://www.alphaworks.ibm.com/formula/xml \u4e0b\u8f7d\u3002";
		contents[MAX_MESSAGES + 42][0] = "optionURIRESOLVER";
    contents[MAX_MESSAGES + 42][1] = "   [-URIRESOLVER \u5168\u7c7b\u540d\uff08URIResolver \u7528\u4e8e\u5206\u6790 URI\uff09]";
		contents[MAX_MESSAGES + 43][0] = "optionENTITYRESOLVER";
    contents[MAX_MESSAGES + 43][1] = "   [-ENTITYRESOLVER \u5168\u7c7b\u540d\uff08EntityResolver \u7528\u4e8e\u5206\u6790\u5b9e\u4f53\uff09] ";
		contents[MAX_MESSAGES + 44][0] = "optionCONTENTHANDLER";
    contents[MAX_MESSAGES + 44][1] = "   [-CONTENTHANDLER \u5168\u7c7b\u540d\uff08ContentHandler \u7528\u4e8e\u4e32\u884c\u5316\u8f93\u51fa\uff09]";
    contents[MAX_MESSAGES + 45][0] = "optionLINENUMBERS";
    contents[MAX_MESSAGES + 45][1] = "   [-L \u4f7f\u7528\u6e90\u6587\u6863\u7684\u884c\u53f7]";
		
  }

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "\u9519\u8bef\uff1a ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "\u8b66\u544a\uff1a ";

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
