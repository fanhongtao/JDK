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
 * originally based on software copyright (c) 2002, Sun Microsystems,
 * Inc., http://www.sun.com.  For more
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
 * you need to first add a String constant. And
 *  you need to enter key , value pair as part of contents
 * Array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */

public class XSLTErrorResources_zh_TW extends XSLTErrorResources
{

   /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 253;          

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 29;

   /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 55;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;

  /** The lookup table for error messages.   */

  public static final Object[][] contents = {

  /** Error message ID that has a null message, but takes in a single object.    */
  //public static final int ERROR0000 = 0;


  {
    "ERROR0000", "{0}"},


  /** ER_NO_CURLYBRACE          */
  //public static final int ER_NO_CURLYBRACE = 1;


  {
    ER_NO_CURLYBRACE,
      "\u932f\u8aa4\uff1a\u5728\u8868\u793a\u5f0f\u5167\u4e0d\u80fd\u6709 '{'"},


  /** ER_ILLEGAL_ATTRIBUTE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE = 2;


  {
    ER_ILLEGAL_ATTRIBUTE, "{0} \u542b\u6709\u4e0d\u6b63\u78ba\u5c6c\u6027\uff1a{1}"},


  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  //public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;


  {
    ER_NULL_SOURCENODE_APPLYIMPORTS,
      "\u5728 xsl:apply-imports \u4e2d\u7684 sourceNode \u70ba\u7a7a\u503c\uff01"},


  /** ER_CANNOT_ADD          */
  //public static final int ER_CANNOT_ADD = 4;


  {
    ER_CANNOT_ADD, "\u7121\u6cd5\u5c07 {0} \u65b0\u589e\u81f3 {1}"},


  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  //public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;


  {
    ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "\u5728 handleApplyTemplatesInstruction \u4e2d\u7684 sourceNode \u70ba\u7a7a\u503c\uff01"},


  /** ER_NO_NAME_ATTRIB          */
  //public static final int ER_NO_NAME_ATTRIB = 6;


  {
    ER_NO_NAME_ATTRIB, "{0} \u5fc5\u9808\u6709 name \u5c6c\u6027\u3002 "},


  /** ER_TEMPLATE_NOT_FOUND          */
  //public static final int ER_TEMPLATE_NOT_FOUND = 7;


  {
    ER_TEMPLATE_NOT_FOUND, "\u627e\u4e0d\u5230\u540d\u7a31\u70ba {0} \u7684\u7bc4\u672c"},


  /** ER_CANT_RESOLVE_NAME_AVT          */
  //public static final int ER_CANT_RESOLVE_NAME_AVT = 8;


  {
    ER_CANT_RESOLVE_NAME_AVT,
      "\u7121\u6cd5\u89e3\u8b6f xsl:call-template \u4e2d\u7684\u540d\u7a31 AVT\u3002"},


  /** ER_REQUIRES_ATTRIB          */
  //public static final int ER_REQUIRES_ATTRIB = 9;


  {
    ER_REQUIRES_ATTRIB, "{0} \u9700\u8981\u5c6c\u6027\uff1a{1}"},


  /** ER_MUST_HAVE_TEST_ATTRIB          */
  //public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;


  {
    ER_MUST_HAVE_TEST_ATTRIB,
      "{0} \u5fc5\u9808\u6709 ''test'' \u5c6c\u6027\u3002"},


  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  //public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;


  {
    ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "level \u5c6c\u6027 {0} \u4e0a\u7684\u503c\u932f\u8aa4"},


  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;


  {
    ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "processing-instruction \u540d\u7a31\u4e0d\u5f97\u70ba 'xml'"},


  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;


  {
    ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "processing-instruction \u540d\u7a31\u5fc5\u9808\u662f\u6709\u6548\u7684 NCName\uff1a{0}"},


  /** ER_NEED_MATCH_ATTRIB          */
  //public static final int ER_NEED_MATCH_ATTRIB = 14;


  {
    ER_NEED_MATCH_ATTRIB,
      "\u5982\u679c {0} \u6709\u6a21\u5f0f\u7684\u8a71\uff0c\u5247\u5b83\u5fc5\u9808\u6709 match \u5c6c\u6027\u3002"},


  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  //public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;


  {
    ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} \u9700\u8981 name \u6216 match \u5c6c\u6027\u3002"},


  /** ER_CANT_RESOLVE_NSPREFIX          */
  //public static final int ER_CANT_RESOLVE_NSPREFIX = 16;


  {
    ER_CANT_RESOLVE_NSPREFIX,
      "\u7121\u6cd5\u89e3\u8b6f\u540d\u7a31\u7a7a\u9593\u524d\u7f6e\uff1a{0}"},


  /** ER_ILLEGAL_VALUE          */
  //public static final int ER_ILLEGAL_VALUE = 17;


  {
    ER_ILLEGAL_VALUE, "xml:space \u542b\u6709\u4e0d\u5408\u898f\u5247\u7684\u503c\uff1a{0}"},


  /** ER_NO_OWNERDOC          */
  //public static final int ER_NO_OWNERDOC = 18;


  {
    ER_NO_OWNERDOC,
      "\u5b50\u9805\u7bc0\u9ede\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01"},


  /** ER_ELEMTEMPLATEELEM_ERR          */
  //public static final int ER_ELEMTEMPLATEELEM_ERR = 19;


  {
    ER_ELEMTEMPLATEELEM_ERR, "ElemTemplateElement \u932f\u8aa4\uff1a{0}"},


  /** ER_NULL_CHILD          */
  //public static final int ER_NULL_CHILD = 20;


  {
    ER_NULL_CHILD, "\u5617\u8a66\u65b0\u589e\u7a7a\u7684\u5b50\u9805\uff01"},


  /** ER_NEED_SELECT_ATTRIB          */
  //public static final int ER_NEED_SELECT_ATTRIB = 21;


  {
    ER_NEED_SELECT_ATTRIB, "{0} \u9700\u8981 select \u5c6c\u6027\u3002"},


  /** ER_NEED_TEST_ATTRIB          */
  //public static final int ER_NEED_TEST_ATTRIB = 22;


  {
    ER_NEED_TEST_ATTRIB,
      "xsl:when \u5fc5\u9808\u6709 'test' \u5c6c\u6027\u3002"},


  /** ER_NEED_NAME_ATTRIB          */
  //public static final int ER_NEED_NAME_ATTRIB = 23;


  {
    ER_NEED_NAME_ATTRIB,
      "xsl:with-param \u5fc5\u9808\u6709 'name' \u5c6c\u6027\u3002"},


  /** ER_NO_CONTEXT_OWNERDOC          */
  //public static final int ER_NO_CONTEXT_OWNERDOC = 24;


  {
    ER_NO_CONTEXT_OWNERDOC,
      "\u4e0a\u4e0b\u6587\u4e0d\u542b\u64c1\u6709\u8005\u6587\u4ef6\uff01"},


  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  //public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;


  {
    ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "\u7121\u6cd5\u5efa\u7acb XML TransformerFactory Liaison\uff1a{0}"},


  /** ER_PROCESS_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;


  {
    ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: \u8655\u7406\u4e0d\u6210\u529f\u3002"},


  /** ER_NOT_SUCCESSFUL          */
  //public static final int ER_NOT_SUCCESSFUL = 27;


  {
    ER_NOT_SUCCESSFUL, "Xalan: \u4e0d\u6210\u529f\u3002"},


  /** ER_ENCODING_NOT_SUPPORTED          */
  //public static final int ER_ENCODING_NOT_SUPPORTED = 28;


  {
    ER_ENCODING_NOT_SUPPORTED, "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}"},


  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  //public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;


  {
    ER_COULD_NOT_CREATE_TRACELISTENER,
      "\u7121\u6cd5\u5efa\u7acb TraceListener\uff1a{0}"},


  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;


  {
    ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key \u9700\u8981 'name' \u5c6c\u6027\uff01"},


  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;


  {
    ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key \u9700\u8981 'match' \u5c6c\u6027\uff01"},


  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;


  {
    ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key \u9700\u8981 'use' \u5c6c\u6027\uff01"},


  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  //public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;


  {
    ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} \u9700\u8981 ''elements'' \u5c6c\u6027\uff01"},


  /** ER_MISSING_PREFIX_ATTRIB          */
  //public static final int ER_MISSING_PREFIX_ATTRIB = 34;


  {
    ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} \u5c6c\u6027 ''prefix'' \u907a\u6f0f"},


  /** ER_BAD_STYLESHEET_URL          */
  //public static final int ER_BAD_STYLESHEET_URL = 35;


  {
    ER_BAD_STYLESHEET_URL, "\u6a23\u5f0f\u8868 URL \u932f\u8aa4\uff1a{0}"},


  /** ER_FILE_NOT_FOUND          */
  //public static final int ER_FILE_NOT_FOUND = 36;


  {
    ER_FILE_NOT_FOUND, "\u627e\u4e0d\u5230\u6a23\u5f0f\u8868\u6a94\u6848\uff1a{0}"},


  /** ER_IOEXCEPTION          */
  //public static final int ER_IOEXCEPTION = 37;


  {
    ER_IOEXCEPTION,
      "\u6a23\u5f0f\u8868\u6a94\u6848 {0} \u6709\u8f38\u5165/\u8f38\u51fa (I/O) \u7570\u5e38"},


  /** ER_NO_HREF_ATTRIB          */
  //public static final int ER_NO_HREF_ATTRIB = 38;


  {
    ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) \u627e\u4e0d\u5230 {0} \u7684 href \u5c6c\u6027"},


  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  //public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;


  {
    ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} \u76f4\u63a5\u6216\u9593\u63a5\u5305\u542b\u672c\u8eab\uff01"},


  /** ER_PROCESSINCLUDE_ERROR          */
  //public static final int ER_PROCESSINCLUDE_ERROR = 40;


  {
    ER_PROCESSINCLUDE_ERROR,
      "StylesheetHandler.processInclude \u932f\u8aa4\uff1a{0}"},


  /** ER_MISSING_LANG_ATTRIB          */
  //public static final int ER_MISSING_LANG_ATTRIB = 41;


  {
    ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} \u5c6c\u6027 ''lang'' \u907a\u6f0f"},


  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  //public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;


  {
    ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) \u8aa4\u7f6e {0} \u5143\u7d20\uff1f\uff1f \u907a\u6f0f container \u5143\u7d20 ''component''"},


  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  //public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;


  {
    ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "\u53ea\u80fd\u8f38\u51fa\u81f3 Element\u3001DocumentFragment\u3001Document \u6216 PrintWriter\u3002"},


  /** ER_PROCESS_ERROR          */
  //public static final int ER_PROCESS_ERROR = 44;


  {
    ER_PROCESS_ERROR, "StylesheetRoot.process \u932f\u8aa4"},


  /** ER_UNIMPLNODE_ERROR          */
  //public static final int ER_UNIMPLNODE_ERROR = 45;


  {
    ER_UNIMPLNODE_ERROR, "UnImplNode \u932f\u8aa4\uff1a{0}"},


  /** ER_NO_SELECT_EXPRESSION          */
  //public static final int ER_NO_SELECT_EXPRESSION = 46;


  {
    ER_NO_SELECT_EXPRESSION,
      "\u932f\u8aa4\uff01\u672a\u627e\u5230 xpath select \u8868\u793a\u5f0f (-select)\u3002"},


  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  //public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;


  {
    ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "\u7121\u6cd5\u4e32\u5217\u5316 XSLProcessor\uff01"},


  /** ER_NO_INPUT_STYLESHEET          */
  //public static final int ER_NO_INPUT_STYLESHEET = 48;


  {
    ER_NO_INPUT_STYLESHEET,
      "\u672a\u6307\u5b9a\u6a23\u5f0f\u8868\u8f38\u5165\uff01"},


  /** ER_FAILED_PROCESS_STYLESHEET          */
  //public static final int ER_FAILED_PROCESS_STYLESHEET = 49;


  {
    ER_FAILED_PROCESS_STYLESHEET,
      "\u7121\u6cd5\u8655\u7406\u6a23\u5f0f\u8868\uff01"},


  /** ER_COULDNT_PARSE_DOC          */
  //public static final int ER_COULDNT_PARSE_DOC = 50;


  {
    ER_COULDNT_PARSE_DOC, "\u7121\u6cd5\u5256\u6790 {0} \u6587\u4ef6\uff01"},


  /** ER_COULDNT_FIND_FRAGMENT          */
  //public static final int ER_COULDNT_FIND_FRAGMENT = 51;


  {
    ER_COULDNT_FIND_FRAGMENT, "\u627e\u4e0d\u5230\u7247\u6bb5\uff1a{0}"},


  /** ER_NODE_NOT_ELEMENT          */
  //public static final int ER_NODE_NOT_ELEMENT = 52;


  {
    ER_NODE_NOT_ELEMENT,
      "\u7247\u6bb5\u8b58\u5225\u78bc\u6240\u6307\u7684\u7bc0\u9ede\u4e0d\u662f\u5143\u7d20\uff1a{0}"},


  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;


  {
    ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each \u5fc5\u9808\u6709 match \u6216 name \u5c6c\u6027"},


  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;


  {
    ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "templates \u5fc5\u9808\u6709 match \u6216 name \u5c6c\u6027"},


  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  //public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;


  {
    ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "\u6587\u4ef6\u7247\u6bb5\u6c92\u6709\u8907\u672c\uff01"},


  /** ER_CANT_CREATE_ITEM          */
  //public static final int ER_CANT_CREATE_ITEM = 56;


  {
    ER_CANT_CREATE_ITEM,
      "\u7121\u6cd5\u5728\u7d50\u679c\u6a39 {0} \u5efa\u7acb\u9805\u76ee"},


  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  //public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;


  {
    ER_XMLSPACE_ILLEGAL_VALUE,
      "\u4f86\u6e90 XML \u4e2d\u7684 xml:space \u542b\u6709\u4e0d\u5408\u898f\u5247\u7684\u503c\uff1a{0}"},


  /** ER_NO_XSLKEY_DECLARATION          */
  //public static final int ER_NO_XSLKEY_DECLARATION = 58;


  {
    ER_NO_XSLKEY_DECLARATION,
      "{0} \u6c92\u6709 xsl:key \u5ba3\u544a\uff01"},


  /** ER_CANT_CREATE_URL          */
  //public static final int ER_CANT_CREATE_URL = 59;


  {
    ER_CANT_CREATE_URL, "\u932f\u8aa4\uff01\u7121\u6cd5\u5efa\u7acb URL \u7d66\uff1a{0}"},


  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  //public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;


  {
    ER_XSLFUNCTIONS_UNSUPPORTED, "\u4e0d\u652f\u63f4 xsl:functions"},


  /** ER_PROCESSOR_ERROR          */
  //public static final int ER_PROCESSOR_ERROR = 61;


  {
    ER_PROCESSOR_ERROR, "XSLT TransformerFactory \u932f\u8aa4"},


  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  //public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;


  {
    ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} \u4e0d\u5141\u8a31\u5728\u6a23\u5f0f\u8868\u5167\uff01"},


  /** ER_RESULTNS_NOT_SUPPORTED          */
  //public static final int ER_RESULTNS_NOT_SUPPORTED = 63;


  {
    ER_RESULTNS_NOT_SUPPORTED,
      "\u4e0d\u518d\u652f\u63f4 result-ns\uff01\u8acb\u4f7f\u7528 xsl:output \u4f86\u4ee3\u66ff\u3002"},


  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  //public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;


  {
    ER_DEFAULTSPACE_NOT_SUPPORTED,
      "\u4e0d\u518d\u652f\u63f4 default-space\uff01\u8acb\u4f7f\u7528 xsl:strip-space \u6216 xsl:preserve-space \u4f86\u4ee3\u66ff\u3002"},


  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  //public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;


  {
    ER_INDENTRESULT_NOT_SUPPORTED,
      "\u4e0d\u518d\u652f\u63f4 indent-result\uff01\u8acb\u4f7f\u7528 xsl:output \u4f86\u4ee3\u66ff\u3002"},


  /** ER_ILLEGAL_ATTRIB          */
  //public static final int ER_ILLEGAL_ATTRIB = 66;


  {
    ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} \u542b\u6709\u4e0d\u5408\u898f\u5247\u7684\u5c6c\u6027\uff1a{1}"},


  /** ER_UNKNOWN_XSL_ELEM          */
  //public static final int ER_UNKNOWN_XSL_ELEM = 67;


  {
    ER_UNKNOWN_XSL_ELEM, "XSL \u5143\u7d20\uff1a{0}"},


  /** ER_BAD_XSLSORT_USE          */
  //public static final int ER_BAD_XSLSORT_USE = 68;


  {
    ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort \u53ea\u80fd\u8207 xsl:apply-templates \u6216 xsl:for-each \u4e00\u8d77\u4f7f\u7528\u3002"},


  /** ER_MISPLACED_XSLWHEN          */
  //public static final int ER_MISPLACED_XSLWHEN = 69;


  {
    ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) \u8aa4\u7f6e xsl:when\uff01"},


  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;


  {
    ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when \u7684\u4e0a\u4ee3\u4e0d\u662f xsl:choose\uff01"},


  /** ER_MISPLACED_XSLOTHERWISE          */
  //public static final int ER_MISPLACED_XSLOTHERWISE = 71;


  {
    ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) \u8aa4\u7f6e xsl:otherwise\uff01"},


  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;


  {
    ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise \u7684\u4e0a\u4ee3\u4e0d\u662f xsl:choose\uff01"},


  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  //public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;


  {
    ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} \u4e0d\u5141\u8a31\u5728\u7bc4\u672c\u5167\uff01"},


  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  //public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;


  {
    ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} \u5ef6\u4f38\u7a0b\u5f0f\u540d\u7a31\u7a7a\u9593\u524d\u7f6e {1} \u672a\u77e5"},


  /** ER_IMPORTS_AS_FIRST_ELEM          */
  //public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;


  {
    ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Imports \u53ea\u80fd\u51fa\u73fe\u65bc\u6a23\u5f0f\u8868\u4e2d\u4f5c\u70ba\u7b2c\u4e00\u500b\u5143\u7d20\uff01"},


  /** ER_IMPORTING_ITSELF          */
  //public static final int ER_IMPORTING_ITSELF = 76;


  {
    ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} \u76f4\u63a5\u6216\u9593\u63a5\u532f\u5165\u672c\u8eab\uff01"},


  /** ER_XMLSPACE_ILLEGAL_VAL          */
  //public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;


  {
    ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) " + "xml:space \u542b\u6709\u4e0d\u5408\u898f\u5247\u7684\u503c\uff1a{0}"},


  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;


  {
    ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet \u4e0d\u6210\u529f\uff01"},


  /** ER_SAX_EXCEPTION          */
  //public static final int ER_SAX_EXCEPTION = 79;


  {
    ER_SAX_EXCEPTION, "SAX \u7570\u5e38"},


  /** ER_FUNCTION_NOT_SUPPORTED          */
  //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


  {
    ER_FUNCTION_NOT_SUPPORTED, "\u4e0d\u652f\u63f4\u51fd\u5f0f\uff01"},


  /** ER_XSLT_ERROR          */
  //public static final int ER_XSLT_ERROR = 81;


  {
    ER_XSLT_ERROR, "XSLT \u932f\u8aa4"},


  /** ER_CURRENCY_SIGN_ILLEGAL          */
  //public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;


  {
    ER_CURRENCY_SIGN_ILLEGAL,
      "\u8ca8\u5e63\u7b26\u865f\u4e0d\u5141\u8a31\u5728\u683c\u5f0f\u578b\u6a23\u5b57\u4e32\u4e2d"},


  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  //public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;


  {
    ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "\u6a23\u5f0f\u8868 DOM \u4e0d\u652f\u63f4\u6587\u4ef6\u51fd\u5f0f\uff01"},


  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  //public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;


  {
    ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "\u7121\u6cd5\u89e3\u8b6f\u975e\u524d\u7f6e\u89e3\u6790\u5668\u7684\u524d\u7f6e\uff01"},


  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  //public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;


  {
    ER_REDIRECT_COULDNT_GET_FILENAME,
      "\u91cd\u65b0\u5c0e\u5411\u5ef6\u4f38\u7a0b\u5f0f\uff1a\u7121\u6cd5\u53d6\u5f97\u6a94\u6848\u540d\u7a31 - file \u6216 select \u5c6c\u6027\u5fc5\u9808\u50b3\u56de\u6709\u6548\u5b57\u4e32\u3002"},


  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  //public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;


  {
    ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "\u7121\u6cd5\u5728\u91cd\u65b0\u5c0e\u5411\u5ef6\u4f38\u7a0b\u5f0f\u4e2d\u5efa\u7acb FormatterListener\uff01"},


  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  //public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;


  {
    ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "exclude-result-prefixes \u4e2d\u7684\u524d\u7f6e\u7121\u6548\uff1a{0}"},


  /** ER_MISSING_NS_URI          */
  //public static final int ER_MISSING_NS_URI = 88;


  {
    ER_MISSING_NS_URI,
      "\u907a\u6f0f\u6307\u5b9a\u7684\u524d\u7f6e\u7684\u540d\u7a31\u7a7a\u9593 URI"},


  /** ER_MISSING_ARG_FOR_OPTION          */
  //public static final int ER_MISSING_ARG_FOR_OPTION = 89;


  {
    ER_MISSING_ARG_FOR_OPTION,
      "\u907a\u6f0f\u9078\u9805\uff1a{0} \u7684\u5f15\u6578"},


  /** ER_INVALID_OPTION          */
  //public static final int ER_INVALID_OPTION = 90;


  {
    ER_INVALID_OPTION, "\u7121\u6548\u7684\u9078\u9805\uff1a{0}"},


  /** ER_MALFORMED_FORMAT_STRING          */
  //public static final int ER_MALFORMED_FORMAT_STRING = 91;


  {
    ER_MALFORMED_FORMAT_STRING, "\u8b8a\u5f62\u7684\u683c\u5f0f\u5b57\u4e32\uff1a{0}"},


  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;


  {
    ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u9700\u8981 'version' \u5c6c\u6027\uff01"},


  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;


  {
    ER_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c6c\u6027\uff1a{0} \u542b\u6709\u4e0d\u6b63\u78ba\u503c\uff1a{1}"},


  /** ER_CHOOSE_REQUIRES_WHEN          */
  //public static final int ER_CHOOSE_REQUIRES_WHEN = 94;


  {
    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose \u9700\u8981 xsl:when"},


  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  //public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;


  {
    ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports \u4e0d\u5141\u8a31\u5728 xsl:for-each \u4e2d"},


  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;


  {
    ER_CANT_USE_DTM_FOR_OUTPUT,
      "\u7121\u6cd5\u5c0d\u8f38\u51fa DOM \u7bc0\u9ede\u4f7f\u7528 DTMLiaison... \u6539\u70ba\u50b3\u9001 org.apache.xpath.DOM2Helper\uff01"},


  /** ER_CANT_USE_DTM_FOR_INPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;


  {
    ER_CANT_USE_DTM_FOR_INPUT,
      "\u7121\u6cd5\u5c0d\u8f38\u5165 DOM \u7bc0\u9ede\u4f7f\u7528 DTMLiaison... \u6539\u70ba\u50b3\u9001 org.apache.xpath.DOM2Helper\uff01"},


  /** ER_CALL_TO_EXT_FAILED          */
  //public static final int ER_CALL_TO_EXT_FAILED = 98;


  {
    ER_CALL_TO_EXT_FAILED,
      "\u547c\u53eb\u5ef6\u4f38\u7a0b\u5f0f\u5143\u7d20\u5931\u6557\uff1a{0}"},


  /** ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 99;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u524d\u7f6e\u5fc5\u9808\u89e3\u8b6f\u70ba\u540d\u7a31\u7a7a\u9593\uff1a{0}"},


  /** ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 100;


  {
    ER_INVALID_UTF16_SURROGATE,
      "\u5075\u6e2c\u5230\u7121\u6548\u7684 UTF-16 \u4ee3\u7528\u54c1\uff1a{0} ?"},


  /** ER_XSLATTRSET_USED_ITSELF          */
  //public static final int ER_XSLATTRSET_USED_ITSELF = 101;


  {
    ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} \u4f7f\u7528\u672c\u8eab\uff0c\u9019\u5c07\u9020\u6210\u7121\u7aae\u8ff4\u5708\u3002"},


  /** ER_CANNOT_MIX_XERCESDOM          */
  //public static final int ER_CANNOT_MIX_XERCESDOM = 102;


  {
    ER_CANNOT_MIX_XERCESDOM,
      "\u7121\u6cd5\u6df7\u5408\u975e Xerces-DOM \u8f38\u5165\u8207 Xerces-DOM \u8f38\u51fa\uff01"},


  /** ER_TOO_MANY_LISTENERS          */
  //public static final int ER_TOO_MANY_LISTENERS = 103;


  {
    ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},


  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  //public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;


  {
    ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "\u5728 ElemTemplateElement.readObject\uff1a{0}"},


  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  //public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;


  {
    ER_DUPLICATE_NAMED_TEMPLATE,
      "\u627e\u5230\u4e00\u500b\u4ee5\u4e0a\u53eb\u4f5c {0} \u7684\u7bc4\u672c"},


  /** ER_INVALID_KEY_CALL          */
  //public static final int ER_INVALID_KEY_CALL = 106;


  {
    ER_INVALID_KEY_CALL,
      "\u7121\u6548\u7684\u51fd\u5f0f\u547c\u53eb\uff1arecursive key() \u547c\u53eb\u4e0d\u88ab\u5141\u8a31"},

  
  /** Variable is referencing itself          */
  //public static final int ER_REFERENCING_ITSELF = 107;


  {
    ER_REFERENCING_ITSELF,
      "\u8b8a\u6578 {0} \u76f4\u63a5\u6216\u9593\u63a5\u53c3\u7167\u672c\u8eab\uff01"},

  
  /** Illegal DOMSource input          */
  //public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;


  {
    ER_ILLEGAL_DOMSOURCE_INPUT,
      "\u5c0d newTemplates \u7684 DOMSource \u800c\u8a00\uff0c\u8f38\u5165\u7bc0\u9ede\u4e0d\u5f97\u70ba\u7a7a\u503c\uff01"},

	
	/** Class not found for option         */
  //public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;


  {
    ER_CLASS_NOT_FOUND_FOR_OPTION,
			"\u627e\u4e0d\u5230\u9078\u9805 {0} \u7684\u985e\u5225\u6a94\u6848"},

	
	/** Required Element not found         */
  //public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;


  {
    ER_REQUIRED_ELEM_NOT_FOUND,
			"\u627e\u4e0d\u5230\u5fc5\u9700\u7684\u5143\u7d20\uff1a{0}"},

  
  /** InputStream cannot be null         */
  //public static final int ER_INPUT_CANNOT_BE_NULL = 111;


  {
    ER_INPUT_CANNOT_BE_NULL,
			"InputStream \u4e0d\u5f97\u70ba\u7a7a\u503c"},

  
  /** URI cannot be null         */
  //public static final int ER_URI_CANNOT_BE_NULL = 112;


  {
    ER_URI_CANNOT_BE_NULL,
			"URI \u4e0d\u5f97\u70ba\u7a7a\u503c"},

  
  /** File cannot be null         */
  //public static final int ER_FILE_CANNOT_BE_NULL = 113;


  {
    ER_FILE_CANNOT_BE_NULL,
			"\u6a94\u6848\u4e0d\u53ef\u70ba\u7a7a\u503c"},

  
   /** InputSource cannot be null         */
  //public static final int ER_SOURCE_CANNOT_BE_NULL = 114;


  {
    ER_SOURCE_CANNOT_BE_NULL,
			"InputSource \u4e0d\u53ef\u70ba\u7a7a\u503c"},

  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"\u7121\u6cd5\u6539\u5beb\u539f\u56e0"},

  
  /** Could not initialize BSF Manager        */
  //public static final int ER_CANNOT_INIT_BSFMGR = 116;


  {
    ER_CANNOT_INIT_BSFMGR,
			"\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a BSF Manager"},

  
  /** Could not compile extension       */
  //public static final int ER_CANNOT_CMPL_EXTENSN = 117;


  {
    ER_CANNOT_CMPL_EXTENSN,
			"\u7121\u6cd5\u7de8\u8b6f\u5ef6\u4f38\u7a0b\u5f0f"},

  
  /** Could not create extension       */
  //public static final int ER_CANNOT_CREATE_EXTENSN = 118;


  {
    ER_CANNOT_CREATE_EXTENSN,
      "\u7121\u6cd5\u5efa\u7acb\u5ef6\u4f38\u7a0b\u5f0f {0}\uff0c\u56e0\u70ba\uff1a{1}"},

  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  //public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;


  {
    ER_INSTANCE_MTHD_CALL_REQUIRES,
      "\u65b9\u6cd5 {0} \u7684\u5be6\u4f8b\u65b9\u6cd5\u547c\u53eb\u9700\u8981\u4e00\u500b\u7269\u4ef6\u5be6\u4f8b\u4f5c\u70ba\u7b2c\u4e00\u500b\u5f15\u6578"},

  
  /** Invalid element name specified       */
  //public static final int ER_INVALID_ELEMENT_NAME = 120;


  {
    ER_INVALID_ELEMENT_NAME,
      "\u6307\u5b9a\u7684\u5143\u7d20\u540d\u7a31\u7121\u6548 {0}"},

  
   /** Element name method must be static      */
  //public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;


  {
    ER_ELEMENT_NAME_METHOD_STATIC,
      "\u5143\u7d20\u540d\u7a31\u65b9\u6cd5\u5fc5\u9808\u70ba\u975c\u614b {0}"},

  
   /** Extension function {0} : {1} is unknown      */
  //public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;


  {
    ER_EXTENSION_FUNC_UNKNOWN,
             "\u5ef6\u4f38\u7a0b\u5f0f\u51fd\u5f0f {0} : {1} \u672a\u77e5"},

  
   /** More than one best match for constructor for       */
  //public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;


  {
    ER_MORE_MATCH_CONSTRUCTOR,
             "{0} \u7684\u6700\u7b26\u5408\u5efa\u69cb\u5143\u4e0d\u6b62\u4e00\u500b"},

  
   /** More than one best match for method      */
  //public static final int ER_MORE_MATCH_METHOD = 124;


  {
    ER_MORE_MATCH_METHOD,
             "\u6700\u7b26\u5408\u65b9\u6cd5 {0} \u7684\u4e0d\u6b62\u4e00\u500b"},

  
   /** More than one best match for element method      */
  //public static final int ER_MORE_MATCH_ELEMENT = 125;


  {
    ER_MORE_MATCH_ELEMENT,
             "\u6700\u7b26\u5408\u5143\u7d20\u65b9\u6cd5 {0} \u7684\u4e0d\u6b62\u4e00\u500b"},

  
   /** Invalid context passed to evaluate       */
  //public static final int ER_INVALID_CONTEXT_PASSED = 126;


  {
    ER_INVALID_CONTEXT_PASSED,
             "\u50b3\u9001\u4f86\u8a55\u4f30 {0} \u7684\u4e0a\u4e0b\u6587\u7121\u6548"},

  
   /** Pool already exists       */
  //public static final int ER_POOL_EXISTS = 127;


  {
    ER_POOL_EXISTS,
             "\u5132\u5b58\u6c60\u5df2\u5b58\u5728"},

  
   /** No driver Name specified      */
  //public static final int ER_NO_DRIVER_NAME = 128;


  {
    ER_NO_DRIVER_NAME,
             "\u672a\u6307\u5b9a\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31"},

  
   /** No URL specified     */
  //public static final int ER_NO_URL = 129;


  {
    ER_NO_URL,
             "\u672a\u6307\u5b9a URL"},

  
   /** Pool size is less than one    */
  //public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;


  {
    ER_POOL_SIZE_LESSTHAN_ONE,
             "\u5132\u5b58\u6c60\u5927\u5c0f\u5c0f\u65bc 1\uff01"},

  
   /** Invalid driver name specified    */
  //public static final int ER_INVALID_DRIVER = 131;


  {
    ER_INVALID_DRIVER,
             "\u6307\u5b9a\u7684\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\u7121\u6548\uff01"},

  
   /** Did not find the stylesheet root    */
  //public static final int ER_NO_STYLESHEETROOT = 132;


  {
    ER_NO_STYLESHEETROOT,
             "\u627e\u4e0d\u5230 stylesheet \u6839\uff01"},

  
   /** Illegal value for xml:space     */
  //public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;


  {
    ER_ILLEGAL_XMLSPACE_VALUE,
         "xml:space \u7684\u503c\u4e0d\u6b63\u78ba"},

  
   /** processFromNode failed     */
  //public static final int ER_PROCESSFROMNODE_FAILED = 134;


  {
    ER_PROCESSFROMNODE_FAILED,
         "processFromNode \u5931\u6548"},

  
   /** The resource [] could not load:     */
  //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;


  {
    ER_RESOURCE_COULD_NOT_LOAD,
        "\u7121\u6cd5\u8f09\u5165\u8cc7\u6e90 [ {0} ]\uff1a{1} \n {2} \t {3}"},

   
  
   /** Buffer size <=0     */
  //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;


  {
    ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "\u7de9\u885d\u5340\u5927\u5c0f <=0"},

  
   /** Unknown error when calling extension    */
  //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;


  {
    ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "\u547c\u53eb\u5ef6\u4f38\u7a0b\u5f0f\u6642\u767c\u751f\u672a\u77e5\u932f\u8aa4"},

  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  //public static final int ER_NO_NAMESPACE_DECL = 138;


  {
    ER_NO_NAMESPACE_DECL,
        "\u524d\u7f6e {0} \u6c92\u6709\u5c0d\u61c9\u7684\u540d\u7a31\u7a7a\u9593\u5ba3\u544a"},

  
   /** Element content not allowed for lang=javaclass   */
  //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;


  {
    ER_ELEM_CONTENT_NOT_ALLOWED,
        "lang=javaclass {0} \u4e0d\u5141\u8a31\u5143\u7d20\u5167\u5bb9"},
     
  
   /** Stylesheet directed termination   */
  //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;


  {
    ER_STYLESHEET_DIRECTED_TERMINATION,
        "Stylesheet \u5f15\u5c0e\u7d42\u6b62"},

  
   /** 1 or 2   */
  //public static final int ER_ONE_OR_TWO = 141;


  {
    ER_ONE_OR_TWO,
        "1 \u6216 2"},

  
   /** 2 or 3   */
  //public static final int ER_TWO_OR_THREE = 142;


  {
    ER_TWO_OR_THREE,
        "2 \u6216 3"},

  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;


  {
    ER_COULD_NOT_LOAD_RESOURCE,
        "\u7121\u6cd5\u8f09\u5165 {0}\uff08\u6aa2\u67e5 CLASSPATH\uff09\uff0c\u73fe\u5728\u53ea\u4f7f\u7528\u9810\u8a2d\u503c"},

  
   /** Cannot initialize default templates   */
  //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;


  {
    ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u9810\u8a2d\u7bc4\u672c"},

  
   /** Result should not be null   */
  //public static final int ER_RESULT_NULL = 145;


  {
    ER_RESULT_NULL,
        "\u7d50\u679c\u4e0d\u61c9\u8a72\u70ba\u7a7a\u503c"},

    
   /** Result could not be set   */
  //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;


  {
    ER_RESULT_COULD_NOT_BE_SET,
        "\u7121\u6cd5\u8a2d\u5b9a\u7d50\u679c"},

  
   /** No output specified   */
  //public static final int ER_NO_OUTPUT_SPECIFIED = 147;


  {
    ER_NO_OUTPUT_SPECIFIED,
        "\u672a\u6307\u5b9a\u8f38\u51fa"},

  
   /** Can't transform to a Result of type   */
  //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;


  {
    ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "\u7121\u6cd5\u8f49\u63db\u6210\u985e\u578b {0} \u7684\u7d50\u679c"},

  
   /** Can't transform to a Source of type   */
  //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;


  {
    ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "\u7121\u6cd5\u8f49\u63db\u985e\u578b {0} \u7684\u4f86\u6e90"},

  
   /** Null content handler  */
  //public static final int ER_NULL_CONTENT_HANDLER = 150;


  {
    ER_NULL_CONTENT_HANDLER,
        "\u7a7a\u7684\u5167\u5bb9\u8655\u7406\u5668"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 151;


  {
    ER_NULL_ERROR_HANDLER,
        "\u7a7a\u7684\u932f\u8aa4\u8655\u7406\u5668"},

  
   /** parse can not be called if the ContentHandler has not been set */
  //public static final int ER_CANNOT_CALL_PARSE = 152;


  {
    ER_CANNOT_CALL_PARSE,
        "\u5982\u679c\u672a\u8a2d\u5b9a ContentHandler \u5247\u7121\u6cd5\u547c\u53eb\u5256\u6790"},

  
   /**  No parent for filter */
  //public static final int ER_NO_PARENT_FOR_FILTER = 153;


  {
    ER_NO_PARENT_FOR_FILTER,
        "\u904e\u6ffe\u5668\u6c92\u6709\u4e0a\u4ee3"},

  
  
   /**  No stylesheet found in: {0}, media */
  //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;


  {
    ER_NO_STYLESHEET_IN_MEDIA,
         "\u5728 {0} media= {1} \u627e\u4e0d\u5230\u6a23\u5f0f\u8868"},

  
   /**  No xml-stylesheet PI found in */
  //public static final int ER_NO_STYLESHEET_PI = 155;


  {
    ER_NO_STYLESHEET_PI,
         "\u5728 {0} \u4e2d\u6c92\u6709\u767c\u73fe XML \u6a23\u5f0f\u8868 PI"},

  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;


  {
    ER_NO_DEFAULT_IMPL,
         "\u627e\u4e0d\u5230\u9810\u8a2d\u5efa\u7f6e"},

  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "\u76ee\u524d\u4e0d\u652f\u63f4 ChunkedIntArray({0})"},

  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "\u504f\u79fb\u5927\u65bc\u4ecb\u9762\u69fd"},

  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;


  {
    ER_COROUTINE_NOT_AVAIL,
       "\u6c92\u6709 Coroutine \u53ef\u7528\uff0cid={0}"},

  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;


  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager \u6536\u5230 co_exit() \u8981\u6c42"},

  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;


  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet() \u5931\u6548"},

  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;


  {
    ER_COROUTINE_PARAM,
       "Coroutine \u53c3\u6578\u932f\u8aa4 ({0})"},

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: \u5256\u6790\u5668 doTerminate \u56de\u7b54 {0}"},

  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "\u5728\u9032\u884c\u5256\u6790\u6642\u672a\u80fd\u547c\u53eb\u5256\u6790"},

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u932f\u8aa4\uff1a\u5c0d\u8ef8 {0} \u8f38\u5165\u7684\u91cd\u8986\u5668\u6c92\u6709\u57f7\u884c"},

  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u932f\u8aa4\uff1a\u8ef8 {0} \u7684\u91cd\u8986\u5668\u6c92\u6709\u57f7\u884c "},

  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u91cd\u8986\u5668\u8907\u88fd"},

  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


  {
    ER_UNKNOWN_AXIS_TYPE,
       "\u672a\u77e5\u8ef8\u904d\u6b77\u985e\u578b\uff1a{0}"},

  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;


  {
    ER_AXIS_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u8ef8\u904d\u8a2a\u5668\uff1a{0}"},

  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;


  {
    ER_NO_DTMIDS_AVAIL,
       "\u6c92\u6709\u53ef\u7528\u7684 DTM ID"},

  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;


  {
    ER_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\uff1a{0}"},

  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;


  {
    ER_NODE_NON_NULL,
       "\u5c0d getDTMHandleFromNode \u800c\u8a00\uff0c\u7bc0\u9ede\u5fc5\u9808\u70ba\u975e\u7a7a\u503c"},

  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


  {
    ER_COULD_NOT_RESOLVE_NODE,
       "\u7121\u6cd5\u89e3\u8b6f\u7bc0\u9ede\u70ba\u63a7\u9ede"},

  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


  {
    ER_STARTPARSE_WHILE_PARSING,
       "\u5728\u9032\u884c\u5256\u6790\u6642\u672a\u547c\u53eb startParse"},

  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse \u9700\u8981\u975e\u7a7a\u503c\u7684 SAXParser"},

  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;


  {
    ER_COULD_NOT_INIT_PARSER,
       "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u5256\u6790\u5668\uff0c\u4ee5"},

  
   /**  Value for property {0} should be a Boolean instance  */
  //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;


  {
    ER_PROPERTY_VALUE_BOOLEAN,
       "\u5167\u5bb9 {0} \u7684\u503c\u61c9\u8a72\u662f\u4e00\u500b\u5e03\u6797\u6848\u4f8b"},

  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;


  {
    ER_EXCEPTION_CREATING_POOL,
       "\u5efa\u7acb\u5132\u5b58\u6c60\u7684\u65b0\u6848\u4f8b\u6642\u767c\u751f\u7570\u5e38"},

  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "\u8def\u5f91\u5305\u542b\u7121\u6548\u9038\u51fa\u5e8f\u5217"},

  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;


  {
    ER_SCHEME_REQUIRED,
       "\u7db1\u8981\u662f\u5fc5\u9700\u7684\uff01"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;


  {
    ER_NO_SCHEME_IN_URI,
       "\u5728 URI \u627e\u4e0d\u5230\u7db1\u8981\uff1a{0}"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;


  {
    ER_NO_SCHEME_INURI,
       "\u5728 URI \u627e\u4e0d\u5230\u7db1\u8981"},

  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;


  {
    ER_PATH_INVALID_CHAR,
       "\u8def\u5f91\u5305\u542b\u7121\u6548\u7684\u5b57\u5143\uff1a{0}"},

  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


  {
    ER_SCHEME_FROM_NULL_STRING,
       "\u7121\u6cd5\u5f9e\u7a7a\u5b57\u4e32\u8a2d\u5b9a\u7db1\u8981"},

  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


  {
    ER_SCHEME_NOT_CONFORMANT,
       "\u7db1\u8981\u4e0d\u4e00\u81f4\u3002"},

  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "\u4e3b\u6a5f\u6c92\u6709\u5b8c\u6574\u7684\u4f4d\u5740"},

  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;


  {
    ER_PORT_WHEN_HOST_NULL,
       "\u4e3b\u6a5f\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u901a\u8a0a\u57e0"},

  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;


  {
    ER_INVALID_PORT,
       "\u7121\u6548\u7684\u901a\u8a0a\u57e0\u7de8\u865f"},

  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


  {
    ER_FRAG_FOR_GENERIC_URI,
       "\u53ea\u80fd\u5c0d\u540c\u5c6c\u7684 URI \u8a2d\u5b9a\u7247\u6bb5"},

  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


  {
    ER_FRAG_WHEN_PATH_NULL,
       "\u8def\u5f91\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u7247\u6bb5"},

  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;


  {
    ER_FRAG_INVALID_CHAR,
       "\u7247\u6bb5\u5305\u542b\u7121\u6548\u5b57\u5143"},

  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;


  {
    ER_PARSER_IN_USE,
        "\u5256\u6790\u5668\u5df2\u5728\u4f7f\u7528\u4e2d"},

  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "\u5256\u6790\u6642\u7121\u6cd5\u8b8a\u66f4 {0} {1}"},

  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "\u4e0d\u5141\u8a31\u81ea\u884c\u5f15\u8d77"},

  
   /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;


  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
       "\u7121\u6cd5\u65bc {0} \u8655\u53d6\u5f97\u5916\u90e8\u6307\u4ee4\u96c6"},

  
  /** The resource [] could not be found     */
  //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;


  {
    ER_RESOURCE_COULD_NOT_FIND,
        "\u627e\u4e0d\u5230\u8cc7\u6e90 [ {0} ]\u3002\n {1}"},

  
   /** output property not recognized:  */
  //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;


  {
    ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "\u672a\u80fd\u8fa8\u8b58\u8f38\u51fa\u5167\u5bb9\uff1a{0}"},

  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


  {
    ER_NO_USERINFO_IF_NO_HOST,
        "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a Userinfo"},

  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;


  {
    ER_NO_PORT_IF_NO_HOST,
        "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a\u901a\u8a0a\u57e0"},

  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


  {
    ER_NO_QUERY_STRING_IN_PATH,
        "\u5728\u8def\u5f91\u53ca\u67e5\u8a62\u5b57\u4e32\u4e2d\u4e0d\u53ef\u6307\u5b9a\u67e5\u8a62\u5b57\u4e32"},

  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "\u7121\u6cd5\u5728\u8def\u5f91\u548c\u7247\u6bb5\u4e2d\u6307\u5b9a\u7247\u6bb5"},

  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u7a7a\u767d\u53c3\u6578\u7684 URI"},

  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;


  {
    ER_FAILED_CREATING_ELEMLITRSLT,
        "\u5efa\u7acb ElemLiteralResult \u6848\u4f8b\u5931\u6557"},
  
  
  // Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed. - nb.

  
   /** Priority value does not contain a parsable number   */
  //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;


  {
    ER_VALUE_SHOULD_BE_NUMBER,
        "{0} \u7684\u503c\u61c9\u5305\u542b\u53ef\u5256\u6790\u7684\u6578\u5b57"},

  
   /**  Value for {0} should equal 'yes' or 'no'   */
  //public static final int ER_VALUE_SHOULD_EQUAL = 205;


  {
    ER_VALUE_SHOULD_EQUAL,
        " {0} \u7684\u503c\u61c9\u7b49\u65bc yes \u6216 no"},

 
   /**  Failed calling {0} method   */
  //public static final int ER_FAILED_CALLING_METHOD = 206;


  {
    ER_FAILED_CALLING_METHOD,
        " \u547c\u53eb {0} \u65b9\u6cd5\u5931\u6557"},

  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;


  {
    ER_FAILED_CREATING_ELEMTMPL,
        "\u5efa\u7acb ElemTemplateElement \u6848\u4f8b\u5931\u6557"},

  
   /**  Characters are not allowed at this point in the document   */
  //public static final int ER_CHARS_NOT_ALLOWED = 208;


  {
    ER_CHARS_NOT_ALLOWED,
        "\u6587\u4ef6\u7684\u9019\u500b\u5730\u65b9\u4e0d\u5141\u8a31\u5b57\u5143"},

  
  /**  attribute is not allowed on the element   */
  //public static final int ER_ATTR_NOT_ALLOWED = 209;


  {
    ER_ATTR_NOT_ALLOWED,
        "{1} \u5143\u7d20\u4e0a\u4e0d\u5141\u8a31\u6709 \"{0}\" \u5c6c\u6027\uff01"},

  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;


  {
    ER_METHOD_NOT_SUPPORTED,
        "\u4e0d\u652f\u63f4\u65b9\u6cd5 "},

 
  /**  Bad value    */
  //public static final int ER_BAD_VALUE = 211;


  {
    ER_BAD_VALUE,
     "{0} \u932f\u8aa4\u503c {1} "},

  
  /**  attribute value not found   */
  //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;


  {
    ER_ATTRIB_VALUE_NOT_FOUND,
     "\u627e\u4e0d\u5230 {0} \u5c6c\u6027\u503c "},

  
  /**  attribute value not recognized    */
  //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;


  {
    ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "\u4e0d\u80fd\u8fa8\u8b58 {0} \u5c6c\u6027\u503c "},


  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter \u76ee\u524d\u7121\u6cd5\u91cd\u65b0\u555f\u52d5"},

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader \u4e0d\u5728 startParse \u8981\u6c42\u4e4b\u524d"},

  
  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;


  {
    ER_NULL_URI_NAMESPACE,
     "\u6b63\u5728\u5617\u8a66\u4f7f\u7528\u7a7a URI \u7522\u751f\u540d\u7a31\u7a7a\u9593\u524d\u7f6e"},
   
   
  // Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)
  
  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NUMBER_TOO_BIG = 217;


  {
    ER_NUMBER_TOO_BIG,
     "\u8a66\u5716\u683c\u5f0f\u5316\u6bd4\u6700\u5927\u7684 Long \u6574\u6578\u9084\u8981\u5927\u7684\u6578\u5b57"},

  
  //ER_CANNOT_FIND_SAX1_DRIVER
  
  //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;
  

  {
  ER_CANNOT_FIND_SAX1_DRIVER,
   "\u7121\u6cd5\u627e\u5230 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}"},


  //ER_SAX1_DRIVER_NOT_LOADED
    //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;


  {
  ER_SAX1_DRIVER_NOT_LOADED,
   "\u5df2\u627e\u5230 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}\uff0c\u4f46\u662f\u7121\u6cd5\u8f09\u5165"},


  //ER_SAX1_DRIVER_NOT_INSTANTIATED
  //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;


  {
    ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "\u5df2\u8f09\u5165 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}\uff0c\u4f46\u662f\u7121\u6cd5\u5c07\u5176\u5be6\u4f8b\u5316"},



  // ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
    //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;

  
    {
     ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
      "SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0} \u4e0d\u57f7\u884c org.xml.sax.Parser"},
  

  // ER_PARSER_PROPERTY_NOT_SPECIFIED
    //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;

  
    {
      ER_PARSER_PROPERTY_NOT_SPECIFIED,
       "\u672a\u6307\u5b9a\u7cfb\u7d71\u5c6c\u6027 org.xml.sax.parser"},
  

  //ER_PARSER_ARG_CANNOT_BE_NULL
    //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;

  
    {
      ER_PARSER_ARG_CANNOT_BE_NULL,
       "\u5256\u6790\u5668\u5f15\u6578\u4e0d\u80fd\u70ba\u7a7a"},
  


  // ER_FEATURE
    //public static final int  ER_FEATURE = 224;

  
    {
      ER_FEATURE,
       "\u529f\u80fd\uff1aa {0}"},
  


  // ER_PROPERTY
    //public static final int ER_PROPERTY = 225 ;

  
    {
      ER_PROPERTY,
       "\u5c6c\u6027\uff1aa {0}"},
  
 
  // ER_NULL_ENTITY_RESOLVER
    //public static final int ER_NULL_ENTITY_RESOLVER  = 226;

  
    {
      ER_NULL_ENTITY_RESOLVER,
      "\u7a7a\u5be6\u9ad4\u89e3\u6790\u5668"},
  

  // ER_NULL_DTD_HANDLER
    //public static final int  ER_NULL_DTD_HANDLER = 227 ;

  
    {
      ER_NULL_DTD_HANDLER,
       "\u7a7a DTD \u8655\u7406\u7a0b\u5f0f"},
  

  // No Driver Name Specified!
    //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;
  
    {
      ER_NO_DRIVER_NAME_SPECIFIED,
       "\u672a\u6307\u5b9a\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\uff01"},
  


  // No URL Specified!
    //public static final int ER_NO_URL_SPECIFIED = 229;
  
    {
      ER_NO_URL_SPECIFIED,
       "\u672a\u6307\u5b9a URL\uff01"},
  


  // Pool size is less than 1!
    //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;
  
    {
      ER_POOLSIZE_LESS_THAN_ONE,
       "\u5132\u5b58\u5340\u5c0f\u65bc 1\uff01"},
  


  // Invalid Driver Name Specified!
    //public static final int ER_INVALID_DRIVER_NAME = 231;
  
    {
      ER_INVALID_DRIVER_NAME,
       "\u6307\u5b9a\u7684\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\u7121\u6548\uff01"},
  



  // ErrorListener
    //public static final int ER_ERRORLISTENER = 232;
  
    {
      ER_ERRORLISTENER,
       "ErrorListener"},
  


  // Programmer's error! expr has no ElemTemplateElement parent!
    //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;
  
    {
      ER_ASSERT_NO_TEMPLATE_PARENT,
       "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u7684\u932f\u8aa4\uff01expr \u6c92\u6709 ElemTemplateElement \u7236\uff01"},
  


  // Programmer's assertion in RundundentExprEliminator: {0}
    //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;
  
    {
      ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
       "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u5728 RundundentExprEliminator \u4e2d\u7684\u5224\u65b7\uff1a{0}"},
  

  // Axis traverser not supported: {0}
    //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
  
    {
      ER_AXIS_TRAVERSER_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u8ef8\u904d\u6b77\u5668\uff1a{0}"},
  

  // ListingErrorHandler created with null PrintWriter!
    //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
  
    {
      ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
       "\u5efa\u7acb\u7684 ListingErrorHandler \u5177\u6709\u7a7a PrintWriter\uff01"},
  

  // {0}is not allowed in this position in the stylesheet!
    //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;
  
    {
      ER_NOT_ALLOWED_IN_POSITION,
       "\u5728\u6a23\u5f0f\u8868\u4e2d\uff0c\u6b64\u4f4d\u7f6e\u4e0d\u5141\u8a31\u51fa\u73fe {0}\uff01"},
  

  // Non-whitespace text is not allowed in this position in the stylesheet!
    //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
  
    {
      ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
       "\u5728\u6a23\u5f0f\u8868\u4e2d\uff0c\u6b64\u4f4d\u7f6e\u53ea\u80fd\u662f\u7a7a\u767d\uff01"},
  

  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
    //public static final int INVALID_TCHAR = 239;
    // SystemId Unknown
  
    {
      INVALID_TCHAR,
       "\u4e0d\u6b63\u78ba\u7684\u503c\uff1a{1} \u88ab\u7528\u65bc CHAR \u5c6c\u6027\uff1a{0}\u3002  CHAR \u985e\u578b\u7684\u5c6c\u6027\u53ea\u80fd\u662f 1 \u500b\u5b57\u5143\uff01"},
  

    //public static final int ER_SYSTEMID_UNKNOWN = 240;
  
    {
      ER_SYSTEMID_UNKNOWN,
       "SystemId \u672a\u77e5"},
  

   // Location of error unknown
    //public static final int ER_LOCATION_UNKNOWN = 241;
  
    {
      ER_LOCATION_UNKNOWN,
       "\u672a\u77e5\u7684\u932f\u8aa4\u4f4d\u7f6e"},
  

   // Note to translators:  The following message is used if the value of
   // an attribute in a stylesheet is invalid.  "QNAME" is the XML data-type of
   // the attribute, and should not be translated.  The substitution text {1} is
   // the attribute value and {0} is the attribute name.
   // INVALID_QNAME

  //The following codes are shared with the warning codes...
  // Illegal value: {1} used for QNAME attribute: {0}
  //public static final int INVALID_QNAME = 242;

  {
    INVALID_QNAME,
     "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc QNAME \u5c6c\u6027\uff1aa {0}"},


   // Note to translators:  The following message is used if the value of
   // an attribute in a stylesheet is invalid.  "ENUM" is the XML data-type of
   // the attribute, and should not be translated.  The substitution text {1} is
   // the attribute value, {0} is the attribute name, and {2} is a list of valid
   // values.
   // INVALID_ENUM

   // Illegal value:a {1} used for ENUM attribute:a {0}.  Valid values are:a {2}.
   //public static final int INVALID_ENUM = 243;
 
   {
     INVALID_ENUM,
      "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc ENUM \u5c6c\u6027\uff1aa {0}\u3002  \u6709\u6548\u503c\u70ba\uff1aa {2}\u3002"},
 

   // Note to translators:  The following message is used if the value of
   // an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
   // of the attribute, and should not be translated.  The substitution text {1} is
   // the attribute value and {0} is the attribute name.
   // INVALID_NMTOKEN

   // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
   //public static final int INVALID_NMTOKEN = 244;
 
   {
     INVALID_NMTOKEN,
      "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc NMTOKEN \u5c6c\u6027\uff1aa {0}"},
 

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NCNAME

  // Illegal value:a {1} used for NCNAME attribute:a {0}.
  //public static final int INVALID_NCNAME = 245;

  {
    INVALID_NCNAME,
     "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc NCNAME \u5c6c\u6027\uff1aa {0}"},


  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_BOOLEAN
 
  // Illegal value:a {1} used for boolean attribute:a {0}.
  //public static final int INVALID_BOOLEAN = 246;


  {
    INVALID_BOOLEAN,
     "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc boolean \u5c6c\u6027\uff1aa {0}"},


  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NUMBER

  // Illegal value:a {1} used for number attribute:a {0}.
  //public static final int INVALID_NUMBER = 247;

  {
    INVALID_NUMBER,
     "\u4e0d\u6b63\u78ba\u7684\u503c\uff1aa {1} \u88ab\u7528\u65bc number \u5c6c\u6027\uff1aa {0}"},



  // End of shared codes...

  // Note to translators:  A "match pattern" is a special form of XPath expression
  // that is used for matching patterns.  The substitution text is the name of
  // a function.  The message indicates that when this function is referenced in
  // a match pattern, its argument must be a string literal (or constant.)
  // ER_ARG_LITERAL - new error message for bugzilla //5202

   // Argument to {0} in match pattern must be a literal.
   //public static final int ER_ARG_LITERAL             = 248;
 
   {
     ER_ARG_LITERAL,
      "\u5728\u76f8\u7b26\u578b\u6a23\u4e2d {0} \u7684\u5f15\u6578\u5fc5\u9808\u662f\u6587\u5b57\u3002"},
 

  // Note to translators:  The following message indicates that two definitions of
  // a variable.  A "global variable" is a variable that is accessible everywher
  // in the stylesheet.
  // ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790

   // Duplicate global variable declaration.
   //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;
 
   {
     ER_DUPLICATE_GLOBAL_VAR,
      "\u91cd\u8907\u7684\u5168\u57df\u8b8a\u6578\u5ba3\u544a\u3002"},
 


  // Note to translators:  The following message indicates that two definitions of
  // a variable were encountered.
  // ER_DUPLICATE_VAR - new error message for bugzilla #790

   // Duplicate variable declaration.
   //public static final int ER_DUPLICATE_VAR           = 250;
 
   {
     ER_DUPLICATE_VAR,
      "\u91cd\u8907\u7684\u8b8a\u6578\u5ba3\u544a\u3002"},
 

      // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
      // which must not be translated.
      // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789

  // xsl:template must have a name or match attribute (or both)
  //public static final int ER_TEMPLATE_NAME_MATCH     = 251;

  {
    ER_TEMPLATE_NAME_MATCH,
     "xsl:template \u5fc5\u9808\u6709\u4e00\u500b\u540d\u7a31\u6216\u76f8\u7b26\u5c6c\u6027 (\u6216\u5169\u8005\u5747\u6709)"},


    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788

   // Prefix in exclude-result-prefixes is not valid:a {0}
   //public static final int ER_INVALID_PREFIX          = 252;
 
   {
     ER_INVALID_PREFIX,
      "exclude-result-prefixes \u4e2d\u7684\u524d\u7f6e\u7121\u6548\uff1aa {0}"},
 

     // Note to translators:  An "attribute set" is a set of attributes that can be
     // added to an element in the output document as a group.  The message indicates
     // that there was a reference to an attribute set named {0} that was never
     // defined.
     // ER_NO_ATTRIB_SET - new error message for bugzilla #782

     // attribute-set named {0} does not exist
     //public static final int ER_NO_ATTRIB_SET           = 253;
   
     {
       ER_NO_ATTRIB_SET,
        "\u540d\u70ba {0} \u7684 attribute-set \u4e0d\u5b58\u5728"},
   

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  //public static final int WG_FOUND_CURLYBRACE = 1;


  {
    WG_FOUND_CURLYBRACE,
      "\u627e\u5230 '}' \u4f46\u6c92\u6709\u958b\u555f\u7684\u5c6c\u6027\u7bc4\u672c\uff01"},


  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;


  {
    WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "\u8b66\u544a\uff1acount \u5c6c\u6027\u4e0d\u7b26\u5408 xsl:number \u4e2d\u7684\u7956\u5148\u7bc0\u9ede\uff01\u76ee\u6a19 = {0}"},


  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;


  {
    WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "\u820a\u8a9e\u6cd5\uff1a'expr' \u5c6c\u6027\u7684\u540d\u7a31\u5df2\u8b8a\u66f4\u70ba 'select'\u3002"},


  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;


  {
    WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan \u5c1a\u672a\u8655\u7406 format-number \u51fd\u5f0f\u4e2d\u7684\u8a9e\u8a00\u74b0\u5883\u540d\u7a31\u3002"},


  /** WG_LOCALE_NOT_FOUND          */
  //public static final int WG_LOCALE_NOT_FOUND = 5;


  {
    WG_LOCALE_NOT_FOUND,
      "\u8b66\u544a\uff1a\u627e\u4e0d\u5230 xml:lang={0} \u7684\u8a9e\u8a00\u74b0\u5883"},


  /** WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 6;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "\u7121\u6cd5\u5f9e\uff1a {0} \u7522\u751f URL"},


  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;


  {
    WG_CANNOT_LOAD_REQUESTED_DOC,
      "\u7121\u6cd5\u8f09\u5165\u6240\u8981\u6c42\u7684\u6587\u4ef6\uff1a{0}"},


  /** WG_CANNOT_FIND_COLLATOR          */
  //public static final int WG_CANNOT_FIND_COLLATOR = 8;


  {
    WG_CANNOT_FIND_COLLATOR,
      "\u627e\u4e0d\u5230 <sort xml:lang={0} \u7684\u7406\u5e8f\u5668"},


  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;


  {
    WG_FUNCTIONS_SHOULD_USE_URL,
      "\u820a\u8a9e\u6cd5\uff1a\u51fd\u5f0f\u6307\u4ee4\u61c9\u4f7f\u7528 URL {0}"},


  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;


  {
    WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}\uff0c\u4f7f\u7528 UTF-8"},


  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;


  {
    WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}\uff0c\u4f7f\u7528 Java {1}"},


  /** WG_SPECIFICITY_CONFLICTS          */
  //public static final int WG_SPECIFICITY_CONFLICTS = 12;


  {
    WG_SPECIFICITY_CONFLICTS,
      "\u627e\u5230\u5177\u9ad4\u885d\u7a81\uff1a{0} \u5c07\u4f7f\u7528\u5728\u6a23\u5f0f\u8868\u4e2d\u627e\u5230\u7684\u6700\u5f8c\u4e00\u500b\u3002"},


  /** WG_PARSING_AND_PREPARING          */
  //public static final int WG_PARSING_AND_PREPARING = 13;


  {
    WG_PARSING_AND_PREPARING,
      "========= \u5256\u6790\u53ca\u6e96\u5099 {0} =========="},


  /** WG_ATTR_TEMPLATE          */
  //public static final int WG_ATTR_TEMPLATE = 14;


  {
    WG_ATTR_TEMPLATE, "Attr \u7bc4\u672c\uff0c{0}"},


  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;


  {
    WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "xsl:strip-space \u548c xsl:preserve-space \u4e4b\u9593\u767c\u751f\u7b26\u5408\u885d\u7a81"},


  /** WG_ATTRIB_NOT_HANDLED          */
  //public static final int WG_ATTRIB_NOT_HANDLED = 16;


  {
    WG_ATTRIB_NOT_HANDLED,
      "Xalan \u5c1a\u672a\u8655\u7406 {0} \u5c6c\u6027\uff01"},


  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;


  {
    WG_NO_DECIMALFORMAT_DECLARATION,
      "\u627e\u4e0d\u5230\u5341\u9032\u4f4d\u683c\u5f0f\u7684\u5ba3\u544a\uff1a{0}"},


  /** WG_OLD_XSLT_NS          */
  //public static final int WG_OLD_XSLT_NS = 18;


  {
    WG_OLD_XSLT_NS, "XSLT \u540d\u7a31\u7a7a\u9593\u907a\u6f0f\u6216\u4e0d\u6b63\u78ba\u3002"},


  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;


  {
    WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "\u50c5\u5141\u8a31\u4e00\u500b\u9810\u8a2d xsl:decimal-format \u5ba3\u544a\u3002"},


  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;


  {
    WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "xsl:decimal-format \u540d\u7a31\u5fc5\u9808\u662f\u552f\u4e00\u7684\u3002 \"{0}\" \u540d\u7a31\u91cd\u8907\u3002"},


  /** WG_ILLEGAL_ATTRIBUTE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE = 21;


  {
    WG_ILLEGAL_ATTRIBUTE,
      "{0} \u542b\u6709\u4e0d\u5408\u898f\u5247\u7684\u5c6c\u6027\uff1a{1}"},


  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;


  {
    WG_COULD_NOT_RESOLVE_PREFIX,
      "\u7121\u6cd5\u89e3\u8b6f\u540d\u7a31\u7a7a\u9593\u524d\u7f6e\uff1a{0}\u3002\u7bc0\u9ede\u88ab\u5ffd\u7565\u3002"},


  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;


  {
    WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u9700\u8981 'version' \u5c6c\u6027\uff01"},


  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;


  {
    WG_ILLEGAL_ATTRIBUTE_NAME,
      "\u4e0d\u5408\u898f\u5247\u7684\u5c6c\u6027\u540d\u7a31\uff1a{0}"},


  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;


  {
    WG_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c6c\u6027 {0} \u4f7f\u7528\u4e86\u4e0d\u5408\u898f\u5247\u7684\u503c\uff1a{1}"},


  /** WG_EMPTY_SECOND_ARG          */
  //public static final int WG_EMPTY_SECOND_ARG = 26;


  {
    WG_EMPTY_SECOND_ARG,
      "\u5f9e\u6587\u4ef6\u51fd\u5f0f\u7b2c\u4e8c\u500b\u5f15\u6578\u7522\u751f\u7684\u7bc0\u9ede\u96c6\u662f\u7a7a\u503c\u3002\u5c07\u4f7f\u7528\u7b2c\u4e00\u500b\u5f15\u6578\u3002"},


// Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML
 

  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;

  {
     WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
     "xsl:processing-instruction \u540d\u7a31\u4e4b 'name' \u5c6c\u6027\u7684\u503c\u4e0d\u80fd\u662f 'xml'"},

 
    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME
 
  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;

  {
     WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
     "xsl:processing-instruction \u4e4b ''name'' \u5c6c\u6027\u7684\u503c\u5fc5\u9808\u662f\u4e00\u500b\u6709\u6548\u7684 NCName\uff1aa {0}"},


    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    // WG_ILLEGAL_ATTRIBUTE_POSITION

  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
  //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;

  {
    WG_ILLEGAL_ATTRIBUTE_POSITION,
     "\u7121\u6cd5\u5728\u7522\u751f\u5b50\u7bc0\u9ede\u4e4b\u5f8c\u6216\u7522\u751f\u5143\u7d20\u4e4b\u524d\u52a0\u5165\u5c6c\u6027 {0}\u3002  \u5c6c\u6027\u5c07\u88ab\u5ffd\u7565\u3002"},


    // WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?
 
  // Other miscellaneous text used inside the code...
  { "ui_language", "zh_TW"},
  { "help_language", "zh_TW"},
  { "language", "zh_TW"},
    { "BAD_CODE",
      "createMessage \u7684\u53c3\u6578\u8d85\u51fa\u754c\u9650"},
    { "FORMAT_FAILED",
      "\u5728 messageFormat \u547c\u53eb\u671f\u9593\u4e1f\u51fa\u7570\u5e38"},
    { "version", ">>>>>>> Xalan \u7248\u672c "},
    {  "version2", "<<<<<<<"},
    { "yes", "\u662f"},
    { "line", "\u884c #"},
    { "column", "\u76f4\u6b04 #"},
    { "xsldone", "XSLProcessor: done"},
    { "xslProc_option",
    "Xalan-J \u6307\u4ee4\u884c Process \u985e\u5225\u9078\u9805\uff1a"},
    { "xslProc_invalid_xsltc_option", "XSLTC \u6a21\u5f0f\u4e0d\u652f\u63f4\u9078\u9805 {0}\u3002"},
    { "xslProc_invalid_xalan_option", "\u9078\u9805 {0} \u50c5\u53ef\u7528\u65bc -XSL TC\u3002"},
    { "xslProc_no_input", "\u932f\u8aa4\uff1a\u672a\u6307\u5b9a\u6a23\u5f0f\u8868\u6216 xml \u8f38\u5165\u6a94\u3002\u91cd\u65b0\u57f7\u884c\u6b64\u6307\u4ee4 (\u4e0d\u52a0\u9078\u9805) \u4ee5\u986f\u793a\u7528\u6cd5\u3002"},
    { "xslProc_common_options", "-\u5e38\u7528\u9078\u9805-"},
    { "xslProc_xalan_options", "-Xalan \u9078\u9805-"},
    { "xslProc_xsltc_options", "-XSLTC \u9078\u9805-"},
    { "xslProc_return_to_continue", "(\u6309 <return> \u9375\u7e7c\u7e8c)"},
    { "optionXSLTC", "   [-XSLTC (\u4f7f\u7528 XSLTC \u9032\u884c\u8f49\u63db)]"},
    { "optionIN", "   [-IN inputXMLURL"},
    { "optionXSL", "   [-XSL XSLTransformationURL]"},
    { "optionOUT", "   [-OUT outputFileName]"},
    { "optionLXCIN", "   [-LXCIN compiledStylesheetFileNameIn]"},
    { "optionLXCOUT",
      "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
    { "optionPARSER",
      "   [-PARSER \u5256\u6790\u5668\u95dc\u806f\u5225\u540d\u7684\u5b8c\u6574\u540d\u7a31]"},
    { "optionE",
    "   [-E (\u4e0d\u5c55\u958b\u5be6\u9ad4\u53c3\u7167)]"},
    { "optionV",
    "   [-E (\u4e0d\u5c55\u958b\u5be6\u9ad4\u53c3\u7167)]"},
    { "optionQC",
      "   [-QC (\u7121\u8072\u578b\u6a23\u885d\u7a81\u8b66\u544a)]"},
    { "optionQ",
    "   [-Q  (\u7121\u8072\u6a21\u5f0f)]"},
    { "optionLF",
      "   [-LF (\u53ea\u5728\u8f38\u51fa\u4e0a\u4f7f\u7528\u63db\u884c {\u9810\u8a2d\u662f CR/LF})]"},
    { "optionCR",
      "   [-CR (\u53ea\u5728\u8f38\u51fa\u4e0a\u4f7f\u7528\u63db\u884c\u9375 {\u9810\u8a2d\u662f CR/LF})]"},
    { "optionESCAPE",
      "   [-ESCAPE (\u8981\u9038\u51fa\u7684\u5b57\u5143 {\u9810\u8a2d\u662f <>&\"\'\\r\\n})]"},
    { "optionINDENT",
      "   [-INDENT (\u63a7\u5236\u8981\u5167\u7e2e\u7684\u7a7a\u683c\u6578 {\u9810\u8a2d\u662f 0})]"},
    { "optionTT",
      "   [-TT (\u547c\u53eb\u6642\u8ffd\u8e64\u7bc4\u672c\u3002)]"},
    { "optionTG",
      "   [-TG (\u8ffd\u8e64\u6bcf\u4e00\u500b\u7522\u751f\u4e8b\u4ef6\u3002)]"},
    { "optionTS",
    "   [-TS (\u8ffd\u8e64\u6bcf\u4e00\u500b\u9078\u53d6\u4e8b\u4ef6\u3002)]"},
    { "optionTTC",
      "   [-TTC (\u8ffd\u8e64\u8655\u7406\u4e2d\u7684\u7bc4\u672c\u5b50\u9805\u3002)]"},
    {"optionTCLASS",
      "   [-TCLASS (\u8ffd\u8e64\u5ef6\u4f38\u7a0b\u5f0f\u7684 TraceListener \u985e\u5225\u3002)]"},
    { "optionVALIDATE",
      "   [-VALIDATE (\u8a2d\u5b9a\u662f\u5426\u767c\u751f\u9a57\u8b49\u3002\u4f9d\u9810\u8a2d\u9a57\u8b49\u662f\u95dc\u9589\u7684\u3002)]"},
    { "optionEDUMP",
      "   [-EDUMP {\u53ef\u9078\u7528\u7684\u6a94\u6848\u540d\u7a31} (\u767c\u751f\u932f\u8aa4\u6642\u57f7\u884c stackdump\u3002)]"},
    { "optionXML",
      "   [-XML (\u4f7f\u7528 XML \u683c\u5f0f\u88fd\u4f5c\u5668\u53ca\u65b0\u589e XML \u8868\u982d\u3002)]"},
    { "optionTEXT",
      "   [-TEXT (\u4f7f\u7528\u7c21\u5f0f\u6587\u5b57\u683c\u5f0f\u5316\u7a0b\u5f0f\u3002)]"},
    { "optionHTML",
    "   [-HTML (\u4f7f\u7528 HTML \u683c\u5f0f\u88fd\u4f5c\u5668\u3002)]"},
    { "optionPARAM",
      "   [-PARAM \u540d\u7a31\u8868\u793a\u5f0f (\u8a2d\u5b9a\u6a23\u5f0f\u8868\u53c3\u6578)]"},
    { "noParsermsg1",
    "XSL \u8655\u7406\u4e0d\u6210\u529f\u3002"},
    { "noParsermsg2",
    "** \u627e\u4e0d\u5230\u5256\u6790\u5668 **"},
    { "noParsermsg3",
    "\u8acb\u6aa2\u67e5\u985e\u5225\u8def\u5f91\u3002"},
    { "noParsermsg4",
      "\u5982\u679c\u60a8\u6c92\u6709 IBM \u7684 XML Parser for Java\uff0c\u53ef\u4e0b\u8f09\u81ea "},
    { "noParsermsg5",
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
    { "optionURIRESOLVER",
    "   [-URIRESOLVER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31 (URIResolver \u7528\u4f86\u89e3\u8b6f URI)]"},
    { "optionENTITYRESOLVER",
    "   [-ENTITYRESOLVER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31 (EntityResolver \u7528\u4f86\u89e3\u8b6f\u5be6\u9ad4)]"},
    { "optionCONTENTHANDLER",
    "   [-CONTENTHANDLER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31 (ContentHandler \u7528\u4f86\u4e32\u5217\u5316\u8f38\u51fa)]"},
    { "optionLINENUMBERS",
    "   [-L \u4f7f\u7528\u539f\u59cb\u6587\u4ef6\u7684\u884c\u865f]"},
    
    // Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


    { "optionMEDIA",
    " [-MEDIA mediaType (\u4f7f\u7528\u5a92\u9ad4\u5c6c\u6027\u5c0b\u627e\u8207\u6587\u4ef6\u95dc\u806f\u7684\u6a23\u5f0f\u8868\u3002)]"},
    { "optionFLAVOR",
    " [-FLAVOR flavorName (\u660e\u78ba\u662f\u4f7f\u7528 s2s=SAX \u9084\u662f d2d=DOM \u4f86\u57f7\u884c\u8f49\u63db\u3002)] "}, // Added by sboag/scurcuru; experimental
    { "optionDIAG",
    " [-DIAG (\u5217\u5370\u8f49\u63db\u4f5c\u696d\u82b1\u8cbb\u7684\u7e3d\u6beb\u79d2\u6578\u3002)]"},
    { "optionINCREMENTAL",
    " [-INCREMENTAL (\u900f\u904e\u5c07 http://xml.apache.org/xalan/features/incremental \u8a2d\u5b9a\u70ba\u300c\u771f\u300d\uff0c\u8981\u6c42\u905e\u589e\u7684 DTM \u5efa\u69cb\u3002)]"},
    { "optionNOOPTIMIMIZE",
    " [-NOOPTIMIMIZE (\u900f\u904e\u5c07 http://xml.apache.org/xalan/features/optimize \u8a2d\u5b9a\u70ba\u300c\u5047\u300d\uff0c\u8981\u6c42\u7121\u6a23\u5f0f\u8868\u6700\u4f73\u5316\u8655\u7406\u3002)]"},
    { "optionRL",
    " [-RL \u5faa\u74b0\u9650\u5236 (\u5047\u8a2d\u5728\u6a23\u5f0f\u8868\u5faa\u74b0\u6df1\u5ea6\u4e0a\u6578\u5b57\u9650\u5236\u3002)]"},
    { "optionXO",
    " [-XO [transletName] (\u6307\u5b9a\u7522\u751f\u7684 translet \u540d\u7a31)]"},
    { "optionXD",
    " [-XD destinationDirectory (\u6307\u5b9a translet \u7684\u76ee\u6a19\u76ee\u9304)]"},
    { "optionXJ",
    " [-XJ jarfile (\u5c07 translet \u985e\u5225\u5c01\u88dd\u6210\u540d\u70ba <jarfile> \u7684 jar \u6a94)]"},
    { "optionXP",
    " [-XP package (\u6307\u5b9a\u6240\u6709 translet \u985e\u5225\u7684\u5c01\u88dd\u540d\u7a31\u524d\u7f6e)]"},
    { "optionXN",  "   [-XN (\u555f\u7528\u5167\u5d4c\u7bc4\u672c)]" },
    { "optionXX",  "   [-XX (\u958b\u555f\u5176\u4ed6\u9664\u932f\u8a0a\u606f\u8f38\u51fa)]"},
    { "optionXT" , "   [-XT (\u5982\u679c\u53ef\u80fd\uff0c\u4f7f\u7528 translet \u9032\u884c\u8f49\u63db)]"},
    { "diagTiming"," ---------\u8f49\u63db {0}\uff0c\u900f\u904e {1}\uff0c\u8017\u6642 {2} \u6beb\u79d2" },
    { "recursionTooDeep","\u7bc4\u672c\u5d4c\u5957\u904e\u6df1\u3002\u5d4c\u5957 = {0}\uff0c\u7bc4\u672c {1}{2}" },
    { "nameIs", "\u540d\u7a31\u70ba" },
    { "matchPatternIs", "\u76f8\u7b26\u7684\u6a23\u5f0f\u70ba" }

		
  };

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "\u932f\u8aa4\uff1a";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "\u8b66\u544a\uff1a";

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
