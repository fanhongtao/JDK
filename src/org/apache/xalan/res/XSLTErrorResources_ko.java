/*
 * @(#)XSLTErrorResources_ko.java	1.7 02/03/26
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
public class XSLTErrorResources_ko extends XSLTErrorResources
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
      "\uc624\ub958: \ud45c\ud604\uc2dd \uc548\uc5d0 '{'\ub97c \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0}\uc5d0 \uc798\ubabb\ub41c \uc18d\uc131 {1}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  static
  {
    contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] =
      "sourceNode\ub294 xsl:apply-imports\uc5d0\uc11c \ub110\uc785\ub2c8\ub2e4!";
  }

  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;

  static
  {
    contents[ER_CANNOT_ADD][1] = "{0}\uc744(\ub97c) {1}\uc5d0 \ucd94\uac00\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. ";
  }

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  static
  {
    contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] =
      "sourceNode\ub294 handleApplyTemplatesInstruction\uc5d0\uc11c \ub110\uc785\ub2c8\ub2e4!";
  }

  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;

  static
  {
    contents[ER_NO_NAME_ATTRIB][1] = "{0}\uc5d0 \uc774\ub984 \uc18d\uc131\uc774 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  static
  {
    contents[ER_TEMPLATE_NOT_FOUND][1] = "\ud15c\ud50c\ub9ac\ud2b8 {0}\uc744(\ub97c) \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  static
  {
    contents[ER_CANT_RESOLVE_NAME_AVT][1] =
      "xsl:call-template\uc5d0\uc11c \uc774\ub984 AVT\ub97c \uacb0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;

  static
  {
    contents[ER_REQUIRES_ATTRIB][1] = "{0}\uc5d0 \uc18d\uc131 {1}\uc774(\uac00) \ud544\uc694\ud569\ub2c8\ub2e4.";
  }

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  static
  {
    contents[ER_MUST_HAVE_TEST_ATTRIB][1] =
      "{0}\uc5d0 'test' \uc18d\uc131\uc774 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  static
  {
    contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] =
      "\ub808\ubca8 \uc18d\uc131 {0}\uc5d0 \uc798\ubabb\ub41c \uac12\uc774 \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] =
      "processing-instruction \uc774\ub984\uc774 'xml'\uc774\uc5b4\uc11c\ub294 \uc548\ub429\ub2c8\ub2e4.";
  }

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] =
      "processing-instruction \uc774\ub984\uc740 \uc62c\ubc14\ub978 NCName {0}\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;

  static
  {
    contents[ER_NEED_MATCH_ATTRIB][1] =
      "{0}\uc774(\uac00) \ubaa8\ub4dc\ub97c \uac00\uc9c0\uace0 \uc788\ub294 \uacbd\uc6b0 \uc77c\uce58 \uc18d\uc131\uc774 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  static
  {
    contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] =
      "{0}\uc5d0 \uc774\ub984 \ub610\ub294 \uc77c\uce58 \uc18d\uc131 \uc911 \ud558\ub098\uac00 \ud544\uc694\ud569\ub2c8\ub2e4.";
  }

  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  static
  {
    contents[ER_CANT_RESOLVE_NSPREFIX][1] =
      "\uc774\ub984 \uacf5\uac04 \uc811\ub450\uc5b4 {0}\uc744(\ub97c) \uacb0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;

  static
  {
    contents[ER_ILLEGAL_VALUE][1] = "xml:space\uc5d0 \uc798\ubabb\ub41c \uac12 {0}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;

  static
  {
    contents[ER_NO_OWNERDOC][1] =
      "\uc790\uc2dd \ub178\ub4dc\uc5d0 \uc18c\uc720\uc790 \ubb38\uc11c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  static
  {
    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement \uc624\ub958: {0}";
  }

  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;

  static
  {
    contents[ER_NULL_CHILD][1] = "\ub110 \uc790\uc2dd\uc744 \ucd94\uac00\ud558\ub824\uace0 \uc2dc\ub3c4\ud558\ub294 \uc911\uc785\ub2c8\ub2e4!";
  }

  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;

  static
  {
    contents[ER_NEED_SELECT_ATTRIB][1] = "{0}\uc5d0 \uc120\ud0dd \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4.";
  }

  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;

  static
  {
    contents[ER_NEED_TEST_ATTRIB][1] =
      "xsl:when\uc5d0 'test' \uc18d\uc131\uc774 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;

  static
  {
    contents[ER_NEED_NAME_ATTRIB][1] =
      "xsl:with-param\uc5d0 'name' \uc18d\uc131\uc774 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  static
  {
    contents[ER_NO_CONTEXT_OWNERDOC][1] =
      "\ucee8\ud14d\uc2a4\ud2b8\uc5d0 \uc18c\uc720\uc790 \ubb38\uc11c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  static
  {
    contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] =
      "XML TransformerFactory Liaison {0}\uc744(\ub97c) \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  static
  {
    contents[ER_PROCESS_NOT_SUCCESSFUL][1] =
      "Xalan: \ud504\ub85c\uc138\uc2a4\uc5d0 \uc131\uacf5\ud558\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;

  static
  {
    contents[ER_NOT_SUCCESSFUL][1] = "Xalan:\uc5d0 \uc131\uacf5\ud558\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  static
  {
    contents[ER_ENCODING_NOT_SUPPORTED][1] = "\ucf54\ub4dc\ud654\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  static
  {
    contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] =
      "TraceListener {0}\uc744(\ub97c) \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  static
  {
    contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] =
      "xsl:key\uc5d0 'name' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  static
  {
    contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] =
      "xsl:key\uc5d0 'match' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  static
  {
    contents[ER_KEY_REQUIRES_USE_ATTRIB][1] =
      "xsl:key\uc5d0 'use' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  static
  {
    contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] =
      "(StylesheetHandler) {0}\uc5d0 'elements' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  static
  {
    contents[ER_MISSING_PREFIX_ATTRIB][1] =
      "(StylesheetHandler) {0} \uc18d\uc131 'prefix'\uac00 \ube60\uc84c\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;

  static
  {
    contents[ER_BAD_STYLESHEET_URL][1] = "Stylesheet URL\uc774 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;

  static
  {
    contents[ER_FILE_NOT_FOUND][1] = "Stylesheet \ud30c\uc77c\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;

  static
  {
    contents[ER_IOEXCEPTION][1] =
      "\uc2a4\ud0c0\uc77c \uc2dc\ud2b8 \ud30c\uc77c\ub85c IO \uc608\uc678\uac00 \ubc1c\uc0dd\ud558\uc600\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;

  static
  {
    contents[ER_NO_HREF_ATTRIB][1] =
      "(StylesheetHandler) {0}\uc5d0 \ub300\ud55c href \uc18d\uc131\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  static
  {
    contents[ER_STYLESHEET_INCLUDES_ITSELF][1] =
      "(StylesheetHandler) {0}\uc774(\uac00) \uc9c1\uc811 \ub610\ub294 \uac04\uc811\uc801\uc73c\ub85c \uc790\uc2e0\uc744 \ud3ec\ud568\ud558\uace0 \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  static
  {
    contents[ER_PROCESSINCLUDE_ERROR][1] =
      "StylesheetHandler.processInclude \uc624\ub958, {0}";
  }

  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;

  static
  {
    contents[ER_MISSING_LANG_ATTRIB][1] =
      "(StylesheetHandler) {0} \uc18d\uc131 'lang'\uc774 \ube60\uc84c\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  static
  {
    contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] =
      "(StylesheetHandler) {0} \uc694\uc18c\uc758 \uc704\uce58\uac00 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4?? container \uc694\uc18c 'component'\uac00 \ube60\uc84c\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  static
  {
    contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] =
      "Element, DocumentFragment, Document \ub610\ub294 PrintWriter\ub85c\ub9cc \ucd9c\ub825\ud560 \uc218 \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;

  static
  {
    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process \uc624\ub958";
  }

  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;

  static
  {
    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode \uc624\ub958: {0}";
  }

  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;

  static
  {
    contents[ER_NO_SELECT_EXPRESSION][1] =
      "\uc624\ub958! xpath \uc120\ud0dd \ud45c\ud604\uc2dd(-select)\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  static
  {
    contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] =
      "XSLProcessor\ub97c \uc77c\ub828\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;

  static
  {
    contents[ER_NO_INPUT_STYLESHEET][1] =
      "Stylesheet \uc785\ub825\uc744 \uc9c0\uc815\ud558\uc9c0 \uc54a\uc558\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  static
  {
    contents[ER_FAILED_PROCESS_STYLESHEET][1] =
      "stylesheet \ucc98\ub9ac\uc5d0 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;

  static
  {
    contents[ER_COULDNT_PARSE_DOC][1] = "{0} \ubb38\uc11c\ub97c \uad6c\ubb38 \ubd84\uc11d\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  static
  {
    contents[ER_COULDNT_FIND_FRAGMENT][1] = "\ub2e8\ud3b8 {0}\uc744(\ub97c) \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;

  static
  {
    contents[ER_NODE_NOT_ELEMENT][1] =
      "\ub2e8\ud3b8 \uc2dd\ubcc4\uc790\uc5d0 \uc758\ud574 \uc9c0\uc815\ub41c \ub178\ub4dc\ub294 \uc694\uc18c\uac00 \uc544\ub2d9\ub2c8\ub2e4: {0}";
  }

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  static
  {
    contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "for-each\uc5d0 \uc77c\uce58 \ub610\ub294 \uc774\ub984 \uc18d\uc131 \uc911 \ud558\ub098\uac00 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  static
  {
    contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "\ud15c\ud50c\ub9ac\ud2b8\uc5d0 \uc77c\uce58 \ub610\ub294 \uc774\ub984 \uc18d\uc131 \uc911 \ud558\ub098\uac00 \uc788\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  static
  {
    contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] =
      "\ubb38\uc11c \ub2e8\ud3b8 \ubcf5\uc81c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;

  static
  {
    contents[ER_CANT_CREATE_ITEM][1] =
      "\uacb0\uacfc \ud2b8\ub9ac {0}\uc5d0 \ud56d\ubaa9\uc744 \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VALUE][1] =
      "\uc18c\uc2a4 XML\uc758 xml:space\uc5d0 \uc798\ubabb\ub41c \uac12 {0}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  static
  {
    contents[ER_NO_XSLKEY_DECLARATION][1] =
      "{0}\uc5d0 \ub300\ud558\uc5ec xsl:key \uc120\uc5b8\uc774 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;

  static
  {
    contents[ER_CANT_CREATE_URL][1] = "\uc624\ub958! {0}\uc5d0 \ub300\ud558\uc5ec url\uc744 \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  static
  {
    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "xsl:functions\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;

  static
  {
    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory \uc624\ub958";
  }

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] =
      "(StylesheetHandler) {0}\uc740(\ub294) \uc2a4\ud0c0\uc77c \uc2dc\ud2b8 \ub0b4\ubd80\uc5d0 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  static
  {
    contents[ER_RESULTNS_NOT_SUPPORTED][1] =
      "result-ns\ub294 \ub354 \uc774\uc0c1 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4! \ub300\uc2e0 xsl:output\uc744 \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.";
  }

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  static
  {
    contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] =
      "default-space\ub294 \ub354 \uc774\uc0c1 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4! \ub300\uc2e0 xsl:preserve-space\ub97c \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.";
  }

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  static
  {
    contents[ER_INDENTRESULT_NOT_SUPPORTED][1] =
      "indent-result\ub294 \ub354 \uc774\uc0c1 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4! \ub300\uc2e0 xsl:output\uc744 \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.";
  }

  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;

  static
  {
    contents[ER_ILLEGAL_ATTRIB][1] =
      "(StylesheetHandler) {0}\uc5d0 \uc798\ubabb\ub41c \uc18d\uc131 {1}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  static
  {
    contents[ER_UNKNOWN_XSL_ELEM][1] = "\uc54c \uc218 \uc5c6\ub294 XSL \uc694\uc18c {0}";
  }

  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;

  static
  {
    contents[ER_BAD_XSLSORT_USE][1] =
      "(StylesheetHandler) xsl:sort\ub294 xsl:apply-templates \ub610\ub294 xsl:for-each\uc640\ub9cc \uc0ac\uc6a9\ud560 \uc218 \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;

  static
  {
    contents[ER_MISPLACED_XSLWHEN][1] =
      "(StylesheetHandler) xsl:when\uc758 \uc704\uce58\uac00 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  static
  {
    contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:when\uc758 \ubd80\ubaa8\ub294 xsl:choose\uac00 \uc544\ub2d9\ub2c8\ub2e4!";
  }

  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  static
  {
    contents[ER_MISPLACED_XSLOTHERWISE][1] =
      "(StylesheetHandler) xsl:otherwise\uc758 \uc704\uce58\uac00 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  static
  {
    contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:otherwise\uc758 \ubd80\ubaa8\ub294 xsl:choose\uac00 \uc544\ub2d9\ub2c8\ub2e4!";
  }

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] =
      "(StylesheetHandler) {0}\uc740(\ub294) \ud15c\ud50c\ub9ac\ud2b8 \ub0b4\ubd80\uc5d0 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  static
  {
    contents[ER_UNKNOWN_EXT_NS_PREFIX][1] =
      "(StylesheetHandler) {0} \ud655\uc7a5 \uc774\ub984 \uacf5\uac04 \uc811\ub450\uc5b4 {1}\uc744(\ub97c) \uc54c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  static
  {
    contents[ER_IMPORTS_AS_FIRST_ELEM][1] =
      "(StylesheetHandler) \uac00\uc838\uc624\uae30\ub294 \uc2a4\ud0c0\uc77c \uc2dc\ud2b8\uc758 \uccab \ubc88\uc9f8 \uc694\uc18c\ub85c\ub9cc \ubc1c\uc0dd\ud560 \uc218 \uc788\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;

  static
  {
    contents[ER_IMPORTING_ITSELF][1] =
      "(StylesheetHandler) {0}\uc774(\uac00) \uc9c1\uc811 \ub610\ub294 \uac04\uc811\uc801\uc73c\ub85c \uc790\uc2e0\uc744 \uac00\uc838\uc624\uace0 \uc788\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VAL][1] =
      "(StylesheetHandler) " + "xml:space\uc5d0 \uc798\ubabb\ub41c \uac12 {0}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  static
  {
    contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] =
      "processStylesheet\uac00 \uc131\uacf5\ud558\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;

  static
  {
    contents[ER_SAX_EXCEPTION][1] = "SAX \uc608\uc678";
  }

  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  static
  {
    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "\uae30\ub2a5\uc774 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;

  static
  {
    contents[ER_XSLT_ERROR][1] = "XSLT \uc624\ub958";
  }

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  static
  {
    contents[ER_CURRENCY_SIGN_ILLEGAL][1] =
      "\ud615\uc2dd \ud328\ud134 \ubb38\uc790\uc5f4\uc5d0 \ud1b5\ud654 \ubd80\ud638\uac00 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  static
  {
    contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] =
      "Stylesheet DOM\uc5d0\uc11c \ubb38\uc11c \uae30\ub2a5\uc774 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  static
  {
    contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] =
      "\uc811\ub450\uc5b4\uac00 \uc5c6\ub294 \uacb0\uc815\uc790\uc758 \uc811\ub450\uc5b4\ub97c \uacb0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  static
  {
    contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] =
      "Redirect \ud655\uc7a5: \ud30c\uc77c \uc774\ub984\uc744 \uac00\uc838\uc62c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4 - \ud30c\uc77c \ub610\ub294 \uc120\ud0dd \uc18d\uc131\uc774 \uc62c\ubc14\ub978 \ubb38\uc790\uc5f4\uc744 \ubc18\ud658\ud574\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  static
  {
    contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] =
      "Redirect \ud655\uc7a5\uc5d0 FormatterListener\ub97c \uad6c\ucd95\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  static
  {
    contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] =
      "exclude-result-prefixes\uc758 \uc811\ub450\uc5b4\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;

  static
  {
    contents[ER_MISSING_NS_URI][1] =
      "\uc9c0\uc815\ub41c \uc811\ub450\uc5b4\uc5d0 \ub300\ud55c \uc774\ub984 \uacf5\uac04 URI\uac00 \ube60\uc84c\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  static
  {
    contents[ER_MISSING_ARG_FOR_OPTION][1] =
      "\uc635\uc158 {0}\uc5d0 \ub300\ud55c \uc778\uc790\uac00 \ube60\uc84c\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;

  static
  {
    contents[ER_INVALID_OPTION][1] = "\uc798\ubabb\ub41c \uc635\uc158: {0}";
  }

  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  static
  {
    contents[ER_MALFORMED_FORMAT_STRING][1] = "\uc798\ubabb\ub41c \ud615\uc2dd \ubb38\uc790\uc5f4: {0}";
  }

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  static
  {
    contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] =
      "xsl:stylesheet\uc5d0 'version' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] =
      "\uc18d\uc131: {0}\uc5d0 \uc798\ubabb\ub41c \uac12 {1}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  static
  {
    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose\uc5d0 xsl:when\uc774 \ud544\uc694\ud569\ub2c8\ub2e4.   ";
  }

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  static
  {
    contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] =
      "xsl:apply-imports\ub294 xsl:for-each\uc5d0 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] =
      "\ucd9c\ub825 DOM \ub178\ub4dc\ub85c DTMLiaison\uc744 \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4... \ub300\uc2e0 org.apache.xpath.DOM2Helper\ub97c \uc804\ub2ec\ud558\uc2ed\uc2dc\uc624!";
  }

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_INPUT][1] =
      "\uc785\ub825 DOM \ub178\ub4dc\ub85c DTMLiaison\uc744 \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4... \ub300\uc2e0 org.apache.xpath.DOM2Helper\ub97c \uc804\ub2ec\ud558\uc2ed\uc2dc\uc624!";
  }

  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;

  static
  {
    contents[ER_CALL_TO_EXT_FAILED][1] =
      "\ud655\uc7a5 \uc694\uc18c\uc5d0 \ub300\ud55c \ud638\ucd9c\uc774 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "\uc811\ub450\uc5b4\uac00 \uc774\ub984 \uacf5\uac04 {0}\uc73c\ub85c(\ub85c) \uacb0\uc815\ub418\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "\uc798\ubabb\ub41c UTF-16 \ub300\ub9ac\uac00 \uac10\uc9c0\ub418\uc5c8\uc2b5\ub2c8\ub2e4: {0} ?";
  }

  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  static
  {
    contents[ER_XSLATTRSET_USED_ITSELF][1] =
      "xsl:attribute-set {0}\uc774(\uac00) \uc790\uc2e0\uc744 \uc0ac\uc6a9\ud558\uc600\uba70, \uc774\ub85c \uc778\ud574 \ubb34\ud55c \ub8e8\ud504\uac00 \ubc1c\uc0dd\ud569\ub2c8\ub2e4.";
  }

  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  static
  {
    contents[ER_CANNOT_MIX_XERCESDOM][1] =
      "Xerces-DOM\uc774 \uc544\ub2cc \uc785\ub825\uacfc Xerces-DOM \ucd9c\ub825\uc744 \ud568\uaed8 \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
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
      "ElemTemplateElement.readObject\uc5d0: {0}";
  }

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  static
  {
    contents[ER_DUPLICATE_NAMED_TEMPLATE][1] =
      "\uac19\uc740 \uc774\ub984 {0}\uc744(\ub97c) \uac00\uc9c4 \ub458 \uc774\uc0c1\uc758 \ud15c\ud50c\ub9ac\ud2b8\ub97c \ucc3e\uc558\uc2b5\ub2c8\ub2e4.";
  }

  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  static
  {
    contents[ER_INVALID_KEY_CALL][1] =
      "\uc798\ubabb\ub41c \ud568\uc218 \ud638\ucd9c: \uc21c\ud658\uc801 key() \ud638\ucd9c\uc740 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  static
  {
    contents[ER_REFERENCING_ITSELF][1] =
      "{0} \ubcc0\uc218\uac00 \uc9c1\uc811 \ub610\ub294 \uac04\uc811\uc801\uc73c\ub85c \uc790\uc2e0\uc744 \ucc38\uc870\ud558\uace0 \uc788\uc2b5\ub2c8\ub2e4!";
  }
  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  static
  {
    contents[ER_ILLEGAL_DOMSOURCE_INPUT][1] =
      "newTemplates\uc758 DOMSource\uc5d0\uc11c \uc785\ub825 \ub178\ub4dc\uac00 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }
	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  static
  {
    contents[ER_CLASS_NOT_FOUND_FOR_OPTION][1] =
			"\uc635\uc158 {0}\uc758 \ud074\ub798\uc2a4 \ud30c\uc77c\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.    ";
  }
	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  static
  {
    contents[ER_REQUIRED_ELEM_NOT_FOUND][1] =
			"\ud544\uc218 \uc694\uc18c\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: {0}";
  }
  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  static
  {
    contents[ER_INPUT_CANNOT_BE_NULL][1] =
			"InputStream\uc740 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  static
  {
    contents[ER_URI_CANNOT_BE_NULL][1] =
			"URI\ub294 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  static
  {
    contents[ER_FILE_CANNOT_BE_NULL][1] =
			"\ud30c\uc77c\uc740 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  static
  {
    contents[ER_SOURCE_CANNOT_BE_NULL][1] =
			"InputSource\ub294 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  static
  {
    contents[ER_CANNOT_OVERWRITE_CAUSE][1] =
			"\uacb9\uccd0\uc4f8 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  static
  {
    contents[ER_CANNOT_INIT_BSFMGR][1] =
			"BSF \uad00\ub9ac\uc790\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  static
  {
    contents[ER_CANNOT_CMPL_EXTENSN][1] =
			"\ud655\uc7a5\uc790\ub97c \ucef4\ud30c\uc77c\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  static
  {
    contents[ER_CANNOT_CREATE_EXTENSN][1] =
      "{1}\ub85c \uc778\ud574 \ud655\uc7a5\uc790 {0}\uc744(\ub97c) \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  static
  {
    contents[ER_INSTANCE_MTHD_CALL_REQUIRES][1] =
      "\uba54\uc18c\ub4dc {0}\uc5d0 \ub300\ud55c \uc778\uc2a4\ud134\uc2a4 \uba54\uc18c\ub4dc \ud638\ucd9c\uc5d0\uc11c \uac1d\uccb4 \uc778\uc2a4\ud134\uc2a4\uac00 \uccab \ubc88\uc9f8 \uc778\uc790\uac00 \ub418\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }
  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  static
  {
    contents[ER_INVALID_ELEMENT_NAME][1] =
      "\uc798\ubabb\ub41c \uc774\ub984 \uc694\uc18c {0}\uc774(\uac00) \uc9c0\uc815\ub418\uc5c8\uc2b5\ub2c8\ub2e4.   ";
  }
  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  static
  {
    contents[ER_ELEMENT_NAME_METHOD_STATIC][1] =
      "\uc774\ub984 \uc694\uc18c \uba54\uc18c\ub4dc\ub294 \uc815\uc801 {0}\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }
  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  static
  {
    contents[ER_EXTENSION_FUNC_UNKNOWN][1] =
             "\ud655\uc7a5 \uae30\ub2a5 {0} : {1}\uc744(\ub97c) \uc54c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  static
  {
    contents[ER_MORE_MATCH_CONSTRUCTOR][1] =
             "{0}\uc758 \uad6c\uc131\uc790\uc640 \uc77c\uce58\ud558\ub294 \uac83\uc774 \ub450 \uac1c \uc774\uc0c1\uc785\ub2c8\ub2e4.   ";
  }
  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  static
  {
    contents[ER_MORE_MATCH_METHOD][1] =
             "{0} \uba54\uc18c\ub4dc\uc640 \uc77c\uce58\ud558\ub294 \uac83\uc774 \ub450 \uac1c \uc774\uc0c1\uc785\ub2c8\ub2e4";
  }
  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  static
  {
    contents[ER_MORE_MATCH_ELEMENT][1] =
             "{0} \uc694\uc18c \uba54\uc18c\ub4dc\uc640 \uc77c\uce58\ud558\ub294 \uac83\uc774 \ub450 \uac1c \uc774\uc0c1\uc785\ub2c8\ub2e4";
  }
  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  static
  {
    contents[ER_INVALID_CONTEXT_PASSED][1] =
             "{0} \ud3c9\uac00\ub97c \uc704\ud574 \uc804\ub2ec\ub41c \ucee8\ud14d\uc2a4\ud2b8\uac00 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4.";
  }
  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  static
  {
    contents[ER_POOL_EXISTS][1] =
             "\ud480\uc774 \uc774\ubbf8 \uc788\uc2b5\ub2c8\ub2e4";
  }
  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  static
  {
    contents[ER_NO_DRIVER_NAME][1] =
             "\uc9c0\uc815\ub41c \ub4dc\ub77c\uc774\ubc84 \uc774\ub984\uc774 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  static
  {
    contents[ER_NO_URL][1] =
             "\uc9c0\uc815\ub41c URL\uc774 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  static
  {
    contents[ER_POOL_SIZE_LESSTHAN_ONE][1] =
             "\ud480 \ud06c\uae30\uac00 1\ubcf4\ub2e4 \uc791\uc2b5\ub2c8\ub2e4!";
  }
  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  static
  {
    contents[ER_INVALID_DRIVER][1] =
             "\uc9c0\uc815\ub41c \ub4dc\ub77c\uc774\ubc84 \uc774\ub984\uc774 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4!";
  }
  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  static
  {
    contents[ER_NO_STYLESHEETROOT][1] =
             "\uc2a4\ud0c0\uc77c \uc2dc\ud2b8 \ub8e8\ud2b8\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }
  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  static
  {
    contents[ER_ILLEGAL_XMLSPACE_VALUE][1] =
         "xml:space\uc5d0 \ub300\ud55c \uac12\uc774 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4";
  }
  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  static
  {
    contents[ER_PROCESSFROMNODE_FAILED][1] =
         "processFromNode\uac00 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_LOAD][1] =
        "[ {0} ] \uc790\uc6d0\uc774 \ub2e4\uc74c\uc744 \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: {1} \n {2} \t {3}";
  }
   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  static
  {
    contents[ER_BUFFER_SIZE_LESSTHAN_ZERO][1] =
        "\ubc84\ud37c \ud06c\uae30 <=0";
  }
  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  static
  {
    contents[ER_UNKNOWN_ERROR_CALLING_EXTENSION][1] =
        "\ud655\uc7a5\uc790 \ud638\ucd9c \uc911 \uc54c \uc218 \uc5c6\ub294 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  static
  {
    contents[ER_NO_NAMESPACE_DECL][1] =
        "{0} \uc811\ub450\uc5b4\uc5d0 \uad00\ub828 \uc774\ub984 \uacf5\uac04 \uc120\uc5b8\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  static
  {
    contents[ER_ELEM_CONTENT_NOT_ALLOWED][1] =
        "lang=javaclass {0}\uc5d0 \uc694\uc18c \ucee8\ud150\ud2b8\uac00 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }   
  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  static
  {
    contents[ER_STYLESHEET_DIRECTED_TERMINATION][1] =
        "\uc2a4\ud0c0\uc77c \uc2dc\ud2b8\uac00 \uc885\ub8cc\ub85c \uc9c0\uc815\ub418\uc5c8\uc2b5\ub2c8\ub2e4";
  }
  
   /** 1 or 2   */
  public static final int ER_ONE_OR_TWO = 141;

  static
  {
    contents[ER_ONE_OR_TWO][1] =
        "1 or 2";
  }
  
   /** 2 or 3   */
  public static final int ER_TWO_OR_THREE = 142;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
        "2 or 3";
  }
  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  static
  {
    contents[ER_COULD_NOT_LOAD_RESOURCE][1] =
        "\uae30\ubcf8\uac12\ub9cc\uc744 \uc0ac\uc6a9\ud558\uc5ec {0}\uc744(\ub97c) \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4(CLASSPATH\ub97c \ud655\uc778\ud558\uc2ed\uc2dc\uc624).";
  }
  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  static
  {
    contents[ER_CANNOT_INIT_DEFAULT_TEMPLATES][1] =
        "\uae30\ubcf8 \ud15c\ud50c\ub9ac\ud2b8\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

  static
  {
    contents[ER_RESULT_NULL][1] =
        "\uacb0\uacfc\ub294 \ub110\uc774 \ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  static
  {
    contents[ER_RESULT_COULD_NOT_BE_SET][1] =
        "\uacb0\uacfc\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  static
  {
    contents[ER_NO_OUTPUT_SPECIFIED][1] =
        "\uc9c0\uc815\ub41c \ucd9c\ub825\uc774 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  static
  {
    contents[ER_CANNOT_TRANSFORM_TO_RESULT_TYPE][1] =
        "\uc720\ud615\uc774 {0}\uc778 \uacb0\uacfc\ub85c \ubcc0\ud658\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  static
  {
    contents[ER_CANNOT_TRANSFORM_SOURCE_TYPE][1] =
        "\uc720\ud615\uc774 {0}\uc778 \uc18c\uc2a4\ub85c \ubcc0\ud658\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. ";
  }
  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  static
  {
    contents[ER_NULL_CONTENT_HANDLER][1] =
        "\ucee8\ud150\ud2b8 \ucc98\ub9ac\uae30\uac00 \ub110\uc785\ub2c8\ub2e4";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "\uc624\ub958 \ucc98\ub9ac\uae30\uac00 \ub110\uc785\ub2c8\ub2e4";
  }
  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  static
  {
    contents[ER_CANNOT_CALL_PARSE][1] =
        "ContentHandler\ub97c \uc124\uc815\ud558\uc9c0 \uc54a\uc73c\uba74 \uad6c\ubb38 \ubd84\uc11d\uc774 \ud638\ucd9c\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  static
  {
    contents[ER_NO_PARENT_FOR_FILTER][1] =
        "\ud544\ud130\uc5d0 \ub300\ud55c \ubd80\ubaa8\uac00 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  static
  {
    contents[ER_NO_STYLESHEET_IN_MEDIA][1] =
         "{0}\uc5d0 \uc2a4\ud0c0\uc77c \uc2dc\ud2b8\uac00 \uc5c6\uc2b5\ub2c8\ub2e4. \ub9e4\uccb4= {1}";
  }
  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  static
  {
    contents[ER_NO_STYLESHEET_PI][1] =
         "{0}\uc5d0 xml-stylesheet PI\uac00 \uc5c6\uc2b5\ub2c8\ub2e4. ";
  }
  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  static
  {
    contents[ER_NO_DEFAULT_IMPL][1] =
         "\uae30\ubcf8 \uad6c\ud604\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  static
  {
    contents[ER_CHUNKEDINTARRAY_NOT_SUPPORTED][1] =
       "ChunkedIntArray({0})\ub294 \ud604\uc7ac \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  static
  {
    contents[ER_OFFSET_BIGGER_THAN_SLOT][1] =
       "\uc624\ud504\uc14b\uc774 \uc2ac\ub86f\ubcf4\ub2e4 \ud07d\ub2c8\ub2e4";
  }
  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  static
  {
    contents[ER_COROUTINE_NOT_AVAIL][1] =
       "Coroutine\uc740 \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4, ID={0}";
  }
  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  static
  {
    contents[ER_COROUTINE_CO_EXIT][1] =
       "CoroutineManager\uac00 co_exit() \uc694\uccad\uc744 \uc218\uc2e0\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  static
  {
    contents[ER_COJOINROUTINESET_FAILED][1] =
       "co_joinCoroutineSet()\uc774 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  static
  {
    contents[ER_COROUTINE_PARAM][1] =
       "Coroutine \ub9e4\uac1c\ubcc0\uc218 \uc624\ub958({0})";
  }
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  static
  {
    contents[ER_PARSER_DOTERMINATE_ANSWERS][1] =
       "\n\uc608\uc0c1\uce58 \ubabb\ud55c \ubb38\uc81c: doTerminate \uad6c\ubb38 \ubd84\uc11d\uae30\uac00 {0}\uc5d0 \uc751\ub2f5\ud588\uc2b5\ub2c8\ub2e4. ";
  }
  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  static
  {
    contents[ER_NO_PARSE_CALL_WHILE_PARSING][1] =
       "\uad6c\ubb38 \ubd84\uc11d\ud558\ub294 \ub3d9\uc548\uc5d0\ub294 \uad6c\ubb38 \ubd84\uc11d\uc744 \ud638\ucd9c\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  static
  {
    contents[ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\uc624\ub958: {0} \ucd95\uc5d0 \ub300\ud574 \uc785\ub825\ub41c \ubc18\ubcf5\uae30\uac00 \uad6c\ud604\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  static
  {
    contents[ER_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "\uc624\ub958: {0} \ucd95\uc5d0 \ub300\ud55c \ubc18\ubcf5\uae30\uac00 \uad6c\ud604\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. ";
  }
  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  static
  {
    contents[ER_ITERATOR_CLONE_NOT_SUPPORTED][1] =
       "\ubc18\ubcf5\uae30 \ubcf5\uc81c\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  static
  {
    contents[ER_UNKNOWN_AXIS_TYPE][1] =
       "\uc54c \uc218 \uc5c6\ub294 \ucd95 \uc21c\ud68c \uc720\ud615: {0}";
  }
  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  static
  {
    contents[ER_AXIS_NOT_SUPPORTED][1] =
       "\ucd95 \uc21c\ud68c\uae30\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}";
  }
  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  static
  {
    contents[ER_NO_DTMIDS_AVAIL][1] =
       "\ub354 \uc774\uc0c1 DTM ID\ub97c \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  static
  {
    contents[ER_NOT_SUPPORTED][1] =
       "\uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}";
  }
  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  static
  {
    contents[ER_NODE_NON_NULL][1] =
       "\ub178\ub4dc\ub294 getDTMHandleFromNode\uc5d0 \ub300\ud574 \ub110\uc774 \uc544\ub2c8\uc5b4\uc57c \ud569\ub2c8\ub2e4";
  }
  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  static
  {
    contents[ER_COULD_NOT_RESOLVE_NODE][1] =
       "\ub178\ub4dc\ub97c \ud578\ub4e4\ub85c \ubcc0\ud658\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  static
  {
    contents[ER_STARTPARSE_WHILE_PARSING][1] =
       "startParse\ub294 \uad6c\ubb38 \ubd84\uc11d \uc911\uc5d0 \ud638\ucd9c\ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  static
  {
    contents[ER_STARTPARSE_NEEDS_SAXPARSER][1] =
       "startParse\uc5d0\ub294 \ub110\uc774 \uc544\ub2cc SAXParser\uac00 \ud544\uc694\ud569\ub2c8\ub2e4";
  }
  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  static
  {
    contents[ER_COULD_NOT_INIT_PARSER][1] =
       "\ub2e4\uc74c\uc73c\ub85c \uad6c\ubb38 \ubd84\uc11d\uae30\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: ";
  }
  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  static
  {
    contents[ER_PROPERTY_VALUE_BOOLEAN][1] =
       "{0} \ud2b9\uc131\uc5d0 \ub300\ud55c \uac12\uc774 \ubd80\uc6b8 \uc778\uc2a4\ud134\uc2a4\uc5ec\uc57c \ud569\ub2c8\ub2e4.";
  }
  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  static
  {
    contents[ER_EXCEPTION_CREATING_POOL][1] =
       "\ud480\uc5d0 \ub300\ud55c \uc0c8 \uc778\uc2a4\ud134\uc2a4 \uc791\uc131 \uc911 \uc608\uc678\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  static
  {
    contents[ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE][1] =
       "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \uc81c\uc5b4 \ubb38\uc790\uc5f4\uc774 \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  static
  {
    contents[ER_SCHEME_REQUIRED][1] =
       "\uccb4\uacc4\uac00 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  static
  {
    contents[ER_NO_SCHEME_IN_URI][1] =
       "URI\uc5d0  \uccb4\uacc4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4: {0}";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  static
  {
    contents[ER_NO_SCHEME_INURI][1] =
       "URI\uc5d0 \uccb4\uacc4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  static
  {
    contents[ER_PATH_INVALID_CHAR][1] =
       "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790 {0}\uc774(\uac00) \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  static
  {
    contents[ER_SCHEME_FROM_NULL_STRING][1] =
       "\ub110 \ubb38\uc790\uc5f4\uc5d0\uc11c \uccb4\uacc4\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  static
  {
    contents[ER_SCHEME_NOT_CONFORMANT][1] =
       "\uccb4\uacc4\uac00 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  static
  {
    contents[ER_HOST_ADDRESS_NOT_WELLFORMED][1] =
       "\ud638\uc2a4\ud2b8 \uc8fc\uc18c\uac00 \uc62c\ubc14\ub978 \ud615\uc2dd\uc774 \uc544\ub2d9\ub2c8\ub2e4";
  }
  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  static
  {
    contents[ER_PORT_WHEN_HOST_NULL][1] =
       "\ud638\uc2a4\ud2b8\uac00 \ub110\uc774\uba74 \ud3ec\ud2b8\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  static
  {
    contents[ER_INVALID_PORT][1] =
       "\uc798\ubabb\ub41c \ud3ec\ud2b8 \ubc88\ud638\uc785\ub2c8\ub2e4";
  }
  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  static
  {
    contents[ER_FRAG_FOR_GENERIC_URI][1] =
       "\ub2e8\ud3b8\uc740 \uc77c\ubc18 URI\uc5d0 \ub300\ud574\uc11c\ub9cc \uc124\uc815\ub420 \uc218 \uc788\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  static
  {
    contents[ER_FRAG_WHEN_PATH_NULL][1] =
       "\uacbd\ub85c\uac00 \ub110\uc774\uba74 \ub2e8\ud3b8\uc744 \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  static
  {
    contents[ER_FRAG_INVALID_CHAR][1] =
       "\ub2e8\ud3b8\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790\uac00 \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4";
  }
  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  static
  {
    contents[ER_PARSER_IN_USE][1] =
        "\uad6c\ubb38 \ubd84\uc11d\uae30\uac00 \uc774\ubbf8 \uc0ac\uc6a9 \uc911\uc785\ub2c8\ub2e4";
  }
  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  static
  {
    contents[ER_CANNOT_CHANGE_WHILE_PARSING][1] =
        "\uad6c\ubb38 \ubd84\uc11d \uc911\uc5d0\ub294 {0} {1}\uc744(\ub97c) \ubcc0\uacbd\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  static
  {
    contents[ER_SELF_CAUSATION_NOT_PERMITTED][1] =
        "\uc790\uccb4 \uc6d0\uc778 \uc81c\uacf5\uc740 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4";
  }
  
   /** src attribute not yet supported for  */
  public static final int ER_SRC_ATTRIB_NOT_SUPPORTED = 195;

  static
  {
    contents[ER_SRC_ATTRIB_NOT_SUPPORTED][1] =
       "{0}\uc5d0\uc11c \uc678\ubd80 \uc2a4\ud06c\ub9bd\ud2b8\ub97c \uc5bb\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_FIND][1] =
        "[ {0} ] \uc790\uc6d0\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.\n {1}";
  }
  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  static
  {
    contents[ER_OUTPUT_PROPERTY_NOT_RECOGNIZED][1] =
        "\ucd9c\ub825 \ud2b9\uc131\uc744 \uc778\uc2dd\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: {0}";
  }
  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  static
  {
    contents[ER_NO_USERINFO_IF_NO_HOST][1] =
        "\ud638\uc2a4\ud2b8\uac00 \uc9c0\uc815\ub418\uc5b4 \uc788\uc9c0 \uc54a\uc73c\uba74 Userinfo\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  static
  {
    contents[ER_NO_PORT_IF_NO_HOST][1] =
        "\ud638\uc2a4\ud2b8\uac00 \uc9c0\uc815\ub418\uc5b4 \uc788\uc9c0 \uc54a\uc73c\uba74 \ud3ec\ud2b8\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  static
  {
    contents[ER_NO_QUERY_STRING_IN_PATH][1] =
        "\uc9c8\uc758 \ubb38\uc790\uc5f4\uc744 \uacbd\ub85c \ub610\ub294 \uc9c8\uc758 \ubb38\uc790\uc5f4 \ub0b4\uc5d0 \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  static
  {
    contents[ER_NO_FRAGMENT_STRING_IN_PATH][1] =
        "\ub2e8\ud3b8\uc744 \uacbd\ub85c\uc640 \ub2e8\ud3b8 \ubaa8\ub450\uc5d0 \uc9c0\uc815\ud560 \uc218\ub294 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  static
  {
    contents[ER_CANNOT_INIT_URI_EMPTY_PARMS][1] =
        "\ube48 \ub9e4\uac1c\ubcc0\uc218\ub85c\ub294 URI\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  static
  {
    contents[ER_FAILED_CREATING_ELEMLITRSLT][1] =
        "ElemLiteralResult \uc778\uc2a4\ud134\uc2a4 \uc791\uc131\uc5d0 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4";
  }  
  
   /** Priority value does not contain a parsable number   */
  public static final int ER_PRIORITY_NOT_PARSABLE = 204;

  static
  {
    contents[ER_PRIORITY_NOT_PARSABLE][1] =
        "\uc6b0\uc120 \uc21c\uc704 \uac12\uc5d0 \uad6c\ubb38 \ubd84\uc11d\ud560 \uc218 \uc788\ub294 \uc22b\uc790\uac00 \ud3ec\ud568\ub418\uc5b4 \uc788\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

  static
  {
    contents[ER_VALUE_SHOULD_EQUAL][1] =
        "{0}\uc5d0 \ub300\ud55c \uac12\uc740 \uc608 \ub610\ub294 \uc544\ub2c8\uc624\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4.";
  }
 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  static
  {
    contents[ER_FAILED_CALLING_METHOD][1] =
        "{0} \uba54\uc18c\ub4dc \ud638\ucd9c\uc5d0 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  static
  {
    contents[ER_FAILED_CREATING_ELEMTMPL][1] =
        "ElemTemplateElement \uc778\uc2a4\ud134\uc2a4 \uc791\uc131\uc5d0 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4";
  }
  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  static
  {
    contents[ER_CHARS_NOT_ALLOWED][1] =
        "\uc774 \ubb38\uc11c\uc5d0\uc11c \ubb38\uc790\uac00 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4";
  }
  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  static
  {
    contents[ER_ATTR_NOT_ALLOWED][1] =
        "\"{0}\" \uc18d\uc131\uc774 {1} \uc694\uc18c\uc5d0 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }
  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

  static
  {
    contents[ER_METHOD_NOT_SUPPORTED][1] =
        "\uc9c0\uc6d0\ub418\uc9c0 \uc54a\ub294 \uba54\uc18c\ub4dc\uc785\ub2c8\ub2e4 ";
  }
 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  static
  {
    contents[ER_BAD_VALUE][1] =
     "{0} \uc798\ubabb\ub41c \uac12 {1} ";
  }
  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_FOUND][1] =
     "{0} \uc18d\uc131 \uac12\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4 ";
  }
  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_RECOGNIZED][1] =
     "{0} \uc18d\uc131 \uac12\uc744 \uc778\uc2dd\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4 ";
  }

  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  static
  {
    contents[ER_INCRSAXSRCFILTER_NOT_RESTARTABLE][1] =
     "IncrementalSAXSource_Filter\ub97c \ub2e4\uc2dc \uc2dc\uc791\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  static
  {
    contents[ER_XMLRDR_NOT_BEFORE_STARTPARSE][1] =
     "startParse \uc694\uccad \uc804\uc5d0 XMLReader\ub97c \uc218\ud589\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4";
  }
  
  /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NULL_URI_NAMESPACE = 216;

  static
  {
    contents[ER_NULL_URI_NAMESPACE][1] =
     "\ub110 URI\ub85c \uc774\ub984 \uacf5\uac04 \uc811\ub450\ubd80\ub97c \uc0dd\uc131\ud558\ub824\uace0 \uc2dc\ub3c4 \uc911\uc785\ub2c8\ub2e4";
  }    
  
  
  /*
    /**  Cannot find SAX1 driver class    *
  public static final int ER_CANNOT_FIND_SAX1_DRIVER = 190;

  static
  {
    contents[ER_CANNOT_FIND_SAX1_DRIVER][1] =
      "SAX1 \ub4dc\ub77c\uc774\ubc84 \ud074\ub798\uc2a4 {0}\uc744(\ub97c) \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  SAX1 driver class {0} found but cannot be loaded    *
  public static final int ER_SAX1_DRIVER_NOT_LOADED = 191;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_LOADED][1] =
      "SAX1 \ub4dc\ub77c\uc774\ubc84 \ud074\ub798\uc2a4 {0}\uc744(\ub97c) \ucc3e\uc558\uc9c0\ub9cc \ub85c\ub4dc\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  SAX1 driver class {0} found but cannot be instantiated    *
  public static final int ER_SAX1_DRIVER_NOT_INSTANTIATED = 192;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_INSTANTIATED][1] =
      "SAX1 \ub4dc\ub77c\uc774\ubc84 \ud074\ub798\uc2a4 {0}\uc774(\uac00) \ub85c\ub4dc\ub410\uc9c0\ub9cc \uc778\uc2a4\ud134\uc2a4\ud654 \ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  SAX1 driver class {0} does not implement org.xml.sax.Parser    *
  public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 193;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER][1] =
      "SAX1 \ub4dc\ub77c\uc774\ubc84 \ud074\ub798\uc2a4 {0}\uc740(\ub294) org.xml.sax.Parser\ub97c \uad6c\ud604\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  System property org.xml.sax.parser not specified    *
  public static final int ER_PARSER_PROPERTY_NOT_SPECIFIED = 194;

  static
  {
    contents[ER_PARSER_PROPERTY_NOT_SPECIFIED][1] =
      "org.xml.sax.parser\uc758 \uc2dc\uc2a4\ud15c \ud2b9\uc131\uc774 \uc9c0\uc815\ub418\uc9c0 \uc54a\uc558\uc2b5\ub2c8\ub2e4.";
  }
  
   /**  Parser argument must not be null    *
  public static final int ER_PARSER_ARG_CANNOT_BE_NULL = 195;

  static
  {
    contents[ER_PARSER_ARG_CANNOT_BE_NULL][1] =
      "\uad6c\ubb38 \ubd84\uc11d\uae30 \uc778\uc790\uac00 \ub110\uc774\uc5b4\uc11c\ub294 \uc548\ub429\ub2c8\ub2e4.";
  }
  
   /**  Feature:    *
  public static final int ER_FEATURE = 196;

  static
  {
    contents[ER_FEATURE][1] =
        "\uae30\ub2a5: {0}";
  }
  
   /**  Property:    *
  public static final int ER_PROPERTY = 197;

  static
  {
    contents[ER_PROPERTY][1] =
        "\ud2b9\uc131: {0}";
  }
  
   /** Null Entity Resolver  *
  public static final int ER_NULL_ENTITY_RESOLVER = 198;

  static
  {
    contents[ER_NULL_ENTITY_RESOLVER][1] =
        "\ub110 \uc5d4\ud2f0\ud2f0 \uacb0\uc815\uc790";
  }
  
   /** Null DTD handler  *
  public static final int ER_NULL_DTD_HANDLER = 199;

  static
  {
    contents[ER_NULL_DTD_HANDLER][1] =
        "\ub110 DTD \ucc98\ub9ac\uae30";
  }
  
 */ 
  

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  public static final int WG_FOUND_CURLYBRACE = 1;

  static
  {
    contents[WG_FOUND_CURLYBRACE + MAX_CODE][1] =
      "'}'\ub97c \ucc3e\uc558\uc73c\ub098 \uc18d\uc131 \ud15c\ud50c\ub9ac\ud2b8\ub97c \uc5f4 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!";
  }

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  static
  {
    contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] =
      "\uacbd\uace0: \uce74\uc6b4\ud2b8 \uc18d\uc131\uc774 xsl:number\uc758 \uc0c1\uc704\uc640 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4! \ub300\uc0c1 = {0}";
  }

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  static
  {
    contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] =
      "\uc774\uc804 \uad6c\ubb38: 'expr' \uc18d\uc131\uc758 \uc774\ub984\uc774 'select'\ub85c \ubcc0\uacbd\ub418\uc5c8\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  static
  {
    contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] =
      "Xalan\uc774 format-number \uae30\ub2a5\uc5d0\uc11c \ub85c\ucf08 \uc774\ub984\uc744 \uc544\uc9c1 \ucc98\ub9ac\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;

  static
  {
    contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] =
      "\uacbd\uace0: xml:lang={0}\uc5d0 \ub300\ud55c \ub85c\ucf08\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "{0}\uc5d0\uc11c URL\uc744 \uc791\uc131\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  static
  {
    contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] =
      "\uc694\uccad\ud55c \ubb38\uc11c {0}\uc744(\ub97c) \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;

  static
  {
    contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] =
      "<sort xml:lang={0}\uc5d0 \ub300\ud55c \uc870\ud569\uae30\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  static
  {
    contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] =
      "\uc774\uc804 \uad6c\ubb38: \ud568\uc218 \uc9c0\uc2dc\uc0ac\ud56d\uc740 {0} url\uc744 \uc0ac\uc6a9\ud574\uc57c \ud569\ub2c8\ub2e4.";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] =
      "UTF-8\uc744 \uc0ac\uc6a9\ud558\uc5ec \ucf54\ub4dc\ud654\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] =
      "Java {1}\uc744(\ub97c) \uc0ac\uc6a9\ud558\uc5ec \ucf54\ub4dc\ud654\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0} ";
  }

  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;

  static
  {
    contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] =
      "\ud2b9\uc774\uc131 \ucda9\ub3cc\uc774 \ubc1c\uacac\ub418\uc5c8\uc2b5\ub2c8\ub2e4: \uc2a4\ud0c0\uc77c \uc2dc\ud2b8\uc5d0\uc11c \ub9c8\uc9c0\ub9c9\uc73c\ub85c \ubc1c\uacac\ub41c {0}\uc774(\uac00) \uc0ac\uc6a9\ub429\ub2c8\ub2e4.";
  }

  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;

  static
  {
    contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] =
      "========= {0}\uc744(\ub97c) \uad6c\ubb38 \ubd84\uc11d \ubc0f \uc900\ube44 \uc911 ==========";
  }

  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;

  static
  {
    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "Attr \ud15c\ud50c\ub9ac\ud2b8, {0}";
  }

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;

  static
  {
    contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] =
      "xsl:strip-space \ubc0f xsl:preserve-space \uac04\uc758 \uc77c\uce58 \ucda9\ub3cc";
  }

  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;

  static
  {
    contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] =
      "Xalan\uc774 \uc544\uc9c1 {0} \uc18d\uc131\uc744 \ucc98\ub9ac\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!";
  }

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  static
  {
    contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] =
      "\uc2ed\uc9c4\uc218 \ud615\uc2dd {0}\uc5d0 \ub300\ud55c \uc120\uc5b8\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;

  static
  {
    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "XSLT \uc774\ub984 \uacf5\uac04\uc774 \ube60\uc84c\uac70\ub098 \uc798\ubabb\ub418\uc5c8\uc2b5\ub2c8\ub2e4. ";
  }

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  static
  {
    contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] =
      "\ud558\ub098\uc758 \uae30\ubcf8 xsl:decimal-format \uc120\uc5b8\ub9cc \ud5c8\uc6a9\ub429\ub2c8\ub2e4.";
  }

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  static
  {
    contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] =
      "xsl:decimal-format \uc774\ub984\uc740 \uace0\uc720\ud574\uc57c \ud569\ub2c8\ub2e4. \"{0}\"\uc740(\ub294) \uc911\ubcf5\ub429\ub2c8\ub2e4.";
  }

  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] =
      "{0}\uc5d0 \uc798\ubabb\ub41c \uc18d\uc131 {1}\uc774(\uac00) \uc788\uc2b5\ub2c8\ub2e4.";
  }

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  static
  {
    contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] =
      "\uc774\ub984 \uacf5\uac04 \uc811\ub450\uc5b4 {0}\uc744(\ub97c) \uacb0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. \uc774 \ub178\ub4dc\ub294 \ubb34\uc2dc\ub429\ub2c8\ub2e4.";
  }

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  static
  {
    contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] =
      "xsl:stylesheet\uc5d0 'version' \uc18d\uc131\uc774 \ud544\uc694\ud569\ub2c8\ub2e4!";
  }

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] =
      "\uc798\ubabb\ub41c \uc18d\uc131 \uc774\ub984: {0}";
  }

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] =
      "{0} \uc18d\uc131\uc5d0 \uc798\ubabb\ub41c \uac12 {1}\uc774(\uac00) \uc0ac\uc6a9\ub418\uc5c8\uc2b5\ub2c8\ub2e4";
  }

  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;

  static
  {
    contents[WG_EMPTY_SECOND_ARG + MAX_CODE][1] =
      "\ubb38\uc11c \uae30\ub2a5\uc758 \ub450 \ubc88\uc9f8 \uc778\uc790\uc758 \uacb0\uacfc\ub85c \ubc1c\uc0dd\ud55c \ub178\ub4dc \uc138\ud2b8\uac00 \ube44\uc5b4 \uc788\uc2b5\ub2c8\ub2e4. \uccab \ubc88\uc9f8 \uc778\uc790\uac00 \uc0ac\uc6a9\ub429\ub2c8\ub2e4.";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "ko";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "ko";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "ko";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "createMessage\uc758 \ub9e4\uac1c\ubcc0\uc218\uac00 \ubc14\uc6b4\ub4dc\ub97c \ubc97\uc5b4\ub0ac\uc2b5\ub2c8\ub2e4.";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "messageFormat \ud638\ucd9c \uc2dc \uc608\uc678 \ubc1c\uc0dd";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan \ubc84\uc804 ";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "\uc608";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "\ud589 #";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "\uc5f4 #";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: \uc644\ub8cc";
    contents[MAX_MESSAGES + 11][0] = "xslProc_option";
    contents[MAX_MESSAGES + 11][1] = "Xalan-J \uba85\ub839\uc904 \ud504\ub85c\uc138\uc2a4 \ud074\ub798\uc2a4 \uc635\uc158:";
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
      "   [-PARSER \uad6c\ubb38 \ubd84\uc11d\uae30 liaison\uc758 \uc644\uc804\ud55c \ud074\ub798\uc2a4 \uc774\ub984]";
    contents[MAX_MESSAGES + 18][0] = "optionE";
    contents[MAX_MESSAGES + 18][1] = "   [-E (\uc5d4\ud2f0\ud2f0 refs\ub97c \ud655\uc7a5\ud558\uc9c0 \ub9c8\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 19][0] = "optionV";
    contents[MAX_MESSAGES + 19][1] = "   [-E (\uc5d4\ud2f0\ud2f0 refs\ub97c \ud655\uc7a5\ud558\uc9c0 \ub9c8\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 20][0] = "optionQC";
    contents[MAX_MESSAGES + 20][1] =
      "   [-QC (\ubb34\uc74c \ud328\ud134 \ucda9\ub3cc \uacbd\uace0)]";
    contents[MAX_MESSAGES + 21][0] = "optionQ";
    contents[MAX_MESSAGES + 21][1] = "   [-Q  (\ubb34\uc74c \ubaa8\ub4dc)]";
    contents[MAX_MESSAGES + 22][0] = "optionLF";
    contents[MAX_MESSAGES + 22][1] =
      "   [-LF (\ucd9c\ub825\uc5d0\uc11c\ub9cc \uc904 \ubc14\uafc8\uc744 \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624. {\uae30\ubcf8\uac12\uc740 CR/LF\uc785\ub2c8\ub2e4.})]";
    contents[MAX_MESSAGES + 23][0] = "optionCR";
    contents[MAX_MESSAGES + 23][1] =
      "   [-CR (\ucd9c\ub825\uc5d0\uc11c\ub9cc \uce90\ub9ac\uc9c0 \ub9ac\ud134\uc744 \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624. {\uae30\ubcf8\uac12\uc740 CR/LF\uc785\ub2c8\ub2e4.})]";
    contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
    contents[MAX_MESSAGES + 24][1] =
      "   [-ESCAPE (\uc81c\uc5b4\ud560 \ubb38\uc790 {\uae30\ubcf8\uac12\uc740 <>&\"\'\\r\\n}]";
    contents[MAX_MESSAGES + 25][0] = "optionINDENT";
    contents[MAX_MESSAGES + 25][1] =
      "   [-INDENT (\ub4e4\uc5ec\uc4f0\uae30\ud560 \uacf5\ubc31 \uc218\ub97c \uc81c\uc5b4\ud569\ub2c8\ub2e4. {\uae30\ubcf8\uac12\uc740 0\uc785\ub2c8\ub2e4.})]";
    contents[MAX_MESSAGES + 26][0] = "optionTT";
    contents[MAX_MESSAGES + 26][1] =
      "   [-TT (\ud638\ucd9c\ub420 \ub54c \ud15c\ud50c\ub9ac\ud2b8\ub97c \ucd94\uc801\ud569\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 27][0] = "optionTG";
    contents[MAX_MESSAGES + 27][1] =
      "   [-TG (\uac01 \uc0dd\uc131 \uc774\ubca4\ud2b8\ub97c \ucd94\uc801\ud569\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 28][0] = "optionTS";
    contents[MAX_MESSAGES + 28][1] = "   [-TS (\uac01 \uc120\ud0dd \uc774\ubca4\ud2b8\ub97c \ucd94\uc801\ud569\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 29][0] = "optionTTC";
    contents[MAX_MESSAGES + 29][1] =
      "   [-TTC (\ucc98\ub9ac\ub420 \ub54c \ud15c\ud50c\ub9ac\ud2b8 \uc790\uc2dd\uc744 \ucd94\uc801\ud569\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
    contents[MAX_MESSAGES + 30][1] =
      "   [-TCLASS (\ucd94\uc801 \ud655\uc7a5\uc5d0 \ub300\ud55c TraceListener \ud074\ub798\uc2a4\uc785\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 31][0] = "optionVALIDATE";
    contents[MAX_MESSAGES + 31][1] =
      "   [-VALIDATE (\uac80\uc99d \uc5ec\ubd80\ub97c \uc124\uc815\ud569\ub2c8\ub2e4. \uae30\ubcf8\uac12\uc740 \uac80\uc99d\uc774 \ubc1c\uc0dd\ud558\uc9c0 \uc54a\ub294 \uac83\uc785\ub2c8\ub2e4.)]";
    contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
    contents[MAX_MESSAGES + 32][1] =
      "   [-EDUMP {\uc120\ud0dd\uc801 \ud30c\uc77c \uc774\ub984} (\uc624\ub958\uac00 \ubc1c\uc0dd\ud558\uba74 \uc2a4\ud0dd\ub364\ud504 \ud558\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 33][0] = "optionXML";
    contents[MAX_MESSAGES + 33][1] =
      "   [-XML (XML \ud3ec\ub9e4\ud130\ub97c \uc0ac\uc6a9\ud558\uc5ec XML \ud5e4\ub354\ub97c \ucd94\uac00\ud558\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 34][0] = "optionTEXT";
    contents[MAX_MESSAGES + 34][1] =
      "   [-TEXT (\ub2e8\uc21c \ud14d\uc2a4\ud2b8 \ud3ec\ub9e4\ud130\ub97c \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 35][0] = "optionHTML";
    contents[MAX_MESSAGES + 35][1] = "   [-HTML (HTML \ud3ec\ub9e4\ud130\ub97c \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 36][0] = "optionPARAM";
    contents[MAX_MESSAGES + 36][1] =
      "   [-PARAM \uc774\ub984 \ud45c\ud604\uc2dd (\uc2a4\ud0c0\uc77c \uc2dc\ud2b8 \ub9e4\uac1c\ubcc0\uc218\ub97c \uc124\uc815\ud558\uc2ed\uc2dc\uc624.)]";
    contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 37][1] = "XSL \ud504\ub85c\uc138\uc2a4\uac00 \uc131\uacf5\ud558\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4.";
    contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 38][1] = "** \uad6c\ubb38 \ubd84\uc11d\uae30\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4 **";
    contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 39][1] = "\ud074\ub798\uc2a4 \uacbd\ub85c\ub97c \ud655\uc778\ud558\uc2ed\uc2dc\uc624.";
    contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 40][1] =
      "Java\uc6a9 IBM XML \uad6c\ubb38 \ubd84\uc11d\uae30\uac00 \uc5c6\ub294 \uacbd\uc6b0 \ub2e4\uc74c\uc5d0\uc11c \ub2e4\uc6b4\ub85c\ub4dc\ud560 \uc218 \uc788\uc2b5\ub2c8\ub2e4.";
    contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 41][1] =
      "IBM AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
		contents[MAX_MESSAGES + 42][0] = "optionURIRESOLVER";
    contents[MAX_MESSAGES + 42][1] = "   [-URIRESOLVER \uc804\uccb4 \ud074\ub798\uc2a4 \uc774\ub984(URI\ub97c \uacb0\uc815\ud558\ub294 \ub370 \uc0ac\uc6a9\ub418\ub294 URIResolver)]";
		contents[MAX_MESSAGES + 43][0] = "optionENTITYRESOLVER";
    contents[MAX_MESSAGES + 43][1] = "   [-ENTITYRESOLVER \uc804\uccb4 \ud074\ub798\uc2a4 \uc774\ub984(\uc5d4\ud2f0\ud2f0\ub97c \uacb0\uc815\ud558\ub294 \ub370 \uc0ac\uc6a9\ub418\ub294 EntityResolver)]";
		contents[MAX_MESSAGES + 44][0] = "optionCONTENTHANDLER";
    contents[MAX_MESSAGES + 44][1] = "   [-CONTENTHANDLER \uc804\uccb4 \ud074\ub798\uc2a4 \uc774\ub984(\ucd9c\ub825\uc744 \uc77c\ub828\ud654\ud558\ub294 \ub370 \uc0ac\uc6a9\ub418\ub294 ContentHandler)]";
    contents[MAX_MESSAGES + 45][0] = "optionLINENUMBERS";
    contents[MAX_MESSAGES + 45][1] = "   [-L \uc18c\uc2a4 \ubb38\uc11c\uc758 \uc904 \ubc88\ud638 \uc0ac\uc6a9]";
		
  }

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "\uc624\ub958: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "\uacbd\uace0: ";

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
