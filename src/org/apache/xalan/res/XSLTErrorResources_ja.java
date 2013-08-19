/*
 * @(#)XSLTErrorResources_ja.java	1.7 03/05/09
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
public class XSLTErrorResources_ja extends XSLTErrorResources
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
      "\u30a8\u30e9\u30fc: \u5f0f\u306e\u4e2d\u3067 '{' \u3092\u6301\u3064\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
  //      "Error: Can not have '{' within expression"},


  /** ER_ILLEGAL_ATTRIBUTE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE = 2;


  {
    ER_ILLEGAL_ATTRIBUTE, "{0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059: {1}"},
  //    ER_ILLEGAL_ATTRIBUTE, "{0} has an illegal attribute: {1}"},


  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  //public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;


  {
    ER_NULL_SOURCENODE_APPLYIMPORTS,
      "xsl:apply-imports \u3067 sourceNode \u304c null \u3067\u3059"},
  //      "sourceNode is null in xsl:apply-imports!"},


  /** ER_CANNOT_ADD          */
  //public static final int ER_CANNOT_ADD = 4;


  {
    ER_CANNOT_ADD, "{0} \u3092 {1} \u306b\u8ffd\u52a0\u3067\u304d\u307e\u305b\u3093"},
  //    ER_CANNOT_ADD, "Can not add {0} to {1}"},


  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  //public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;


  {
    ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "handleApplyTemplatesInstruction \u3067 sourceNode \u304c null \u3067\u3059\u3002"},
  //      "sourceNode is null in handleApplyTemplatesInstruction!"},


  /** ER_NO_NAME_ATTRIB          */
  //public static final int ER_NO_NAME_ATTRIB = 6;


  {
    ER_NO_NAME_ATTRIB, "{0} \u306b\u306f\u540d\u524d\u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //    ER_NO_NAME_ATTRIB, "{0} must have a name attribute."},


  /** ER_TEMPLATE_NOT_FOUND          */
  //public static final int ER_TEMPLATE_NOT_FOUND = 7;


  {
    ER_TEMPLATE_NOT_FOUND, "{0} \u3068\u3044\u3046\u540d\u524d\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
  //    ER_TEMPLATE_NOT_FOUND, "Could not find template named: {0}"},


  /** ER_CANT_RESOLVE_NAME_AVT          */
  //public static final int ER_CANT_RESOLVE_NAME_AVT = 8;


  {
    ER_CANT_RESOLVE_NAME_AVT,
      "xls:call-template \u3067\u540d\u524d AVT \u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
  //      "Could not resolve name AVT in xsl:call-template."},


  /** ER_REQUIRES_ATTRIB          */
  //public static final int ER_REQUIRES_ATTRIB = 9;


  {
    ER_REQUIRES_ATTRIB, "{0} \u306b\u306f\u5c5e\u6027 {1} \u304c\u5fc5\u8981\u3067\u3059:"},
  //    ER_REQUIRES_ATTRIB, "{0} requires attribute: {1}"},


  /** ER_MUST_HAVE_TEST_ATTRIB          */
  //public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;


  {
    ER_MUST_HAVE_TEST_ATTRIB,
      "{0} \u306b\u306f ''test'' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "{0} must have a 'test' attribute."},


  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  //public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;


  {
    ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "\u30ec\u30d9\u30eb\u5c5e\u6027\u306b\u4e0d\u6b63\u306a\u5024\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059: {0}"},
  //      "Bad value on level attribute: {0}"},


  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;


  {
    ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "processing-instruction \u540d\u306f 'xml' \u306b\u3067\u304d\u307e\u305b\u3093"},
  //      "processing-instruction name can not be 'xml'"},


  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;


  {
    ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "processing-instruction \u540d\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093: {0}"},
  //      "processing-instruction name must be a valid NCName: {0}"},


  /** ER_NEED_MATCH_ATTRIB          */
  //public static final int ER_NEED_MATCH_ATTRIB = 14;


  {
    ER_NEED_MATCH_ATTRIB,
      "{0} \u306b\u30e2\u30fc\u30c9\u304c\u3042\u308b\u5834\u5408\u3001\u4e00\u81f4\u3059\u308b\u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093\u3002"},
  //      "{0} must have a match attribute if it has a mode."},


  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  //public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;


  {
    ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} \u306f name \u5c5e\u6027\u304b\u3001\u307e\u305f\u306f match \u5c5e\u6027\u3092\u5fc5\u8981\u3068\u3057\u307e\u3059\u3002"},
  //      "{0} requires either a name or a match attribute."},


  /** ER_CANT_RESOLVE_NSPREFIX          */
  //public static final int ER_CANT_RESOLVE_NSPREFIX = 16;


  {
    ER_CANT_RESOLVE_NSPREFIX,
      "\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e {0} \u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093"},
  //      "Can not resolve namespace prefix: {0}"},


  /** ER_ILLEGAL_VALUE          */
  //public static final int ER_ILLEGAL_VALUE = 17;


  {
    ER_ILLEGAL_VALUE, "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}"},
  //    ER_ILLEGAL_VALUE, "xml:space has an illegal value: {0}"},


  /** ER_NO_OWNERDOC          */
  //public static final int ER_NO_OWNERDOC = 18;


  {
    ER_NO_OWNERDOC,
      "\u5b50\u30ce\u30fc\u30c9\u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u307e\u305b\u3093\u3002"},
  //      "Child node does not have an owner document!"},


  /** ER_ELEMTEMPLATEELEM_ERR          */
  //public static final int ER_ELEMTEMPLATEELEM_ERR = 19;


  {
    ER_ELEMTEMPLATEELEM_ERR, "ElemTemplateElement \u30a8\u30e9\u30fc: {0}"},
  //    ER_ELEMTEMPLATEELEM_ERR, "ElemTemplateElement error: {0}"},


  /** ER_NULL_CHILD          */
  //public static final int ER_NULL_CHILD = 20;


  {
    ER_NULL_CHILD, "null \u3067\u3042\u308b\u5b50\u3092\u8ffd\u52a0\u3057\u3066\u3044\u307e\u3059\u3002"},
  //    ER_NULL_CHILD, "Trying to add a null child!"},


  /** ER_NEED_SELECT_ATTRIB          */
  //public static final int ER_NEED_SELECT_ATTRIB = 21;


  {
    ER_NEED_SELECT_ATTRIB, "{0} \u306f select \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //    ER_NEED_SELECT_ATTRIB, "{0} requires a select attribute."},


  /** ER_NEED_TEST_ATTRIB          */
  //public static final int ER_NEED_TEST_ATTRIB = 22;


  {
    ER_NEED_TEST_ATTRIB,
      "xsl:when \u306b\u306f 'test' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "xsl:when must have a 'test' attribute."},


  /** ER_NEED_NAME_ATTRIB          */
  //public static final int ER_NEED_NAME_ATTRIB = 23;


  {
    ER_NEED_NAME_ATTRIB,
      "xsl:with-param \u306b\u306f 'name' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "xsl:with-param must have a 'name' attribute."},


  /** ER_NO_CONTEXT_OWNERDOC          */
  //public static final int ER_NO_CONTEXT_OWNERDOC = 24;


  {
    ER_NO_CONTEXT_OWNERDOC,
      "\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u307e\u305b\u3093\u3002"},
  //      "context does not have an owner document!"},


  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  //public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;


  {
    ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "XML TransformerFactory Liaison {0} \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
  //      "Could not create XML TransformerFactory Liaison: {0}"},


  /** ER_PROCESS_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;


  {
    ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: \u30d7\u30ed\u30bb\u30b9\u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
  //      "Xalan: Process was not successful."},


  /** ER_NOT_SUCCESSFUL          */
  //public static final int ER_NOT_SUCCESSFUL = 27;


  {
    ER_NOT_SUCCESSFUL, "Xalan: \u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
  //    ER_NOT_SUCCESSFUL, "Xalan: was not successful."},


  /** ER_ENCODING_NOT_SUPPORTED          */
  //public static final int ER_ENCODING_NOT_SUPPORTED = 28;


  {
    ER_ENCODING_NOT_SUPPORTED, "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}"},
  //    ER_ENCODING_NOT_SUPPORTED, "Encoding not supported: {0}"},


  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  //public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;


  {
    ER_COULD_NOT_CREATE_TRACELISTENER,
      "TraceListener \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f: {0}"},
  //      "Could not create TraceListener: {0}"},


  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;


  {
    ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key \u306b\u306f 'name' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "xsl:key requires a 'name' attribute!"},


  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;


  {
    ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key \u306b\u306f 'match' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "xsl:key requires a 'match' attribute!"},


  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;


  {
    ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key \u306b\u306f 'use' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //     "xsl:key requires a 'use' attribute!"},


  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  //public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;


  {
    ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} \u306b\u306f ''elements'' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
  //      "(StylesheetHandler) {0} requires an 'elements' attribute!"},


  /** ER_MISSING_PREFIX_ATTRIB          */
  //public static final int ER_MISSING_PREFIX_ATTRIB = 34;


  {
    ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} \u5c5e\u6027\u306b ''prefix'' \u304c\u8db3\u308a\u307e\u305b\u3093"},
  //      "(StylesheetHandler) {0} attribute 'prefix' is missing"},


  /** ER_BAD_STYLESHEET_URL          */
  //public static final int ER_BAD_STYLESHEET_URL = 35;


  {
    ER_BAD_STYLESHEET_URL, "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e URL \u304c\u4e0d\u6b63\u3067\u3059: {0}"},
  //    ER_BAD_STYLESHEET_URL, "Stylesheet URL is bad: {0}"},


  /** ER_FILE_NOT_FOUND          */
  //public static final int ER_FILE_NOT_FOUND = 36;


  {
    ER_FILE_NOT_FOUND, "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u30d5\u30a1\u30a4\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f: {0}"},
//    ER_FILE_NOT_FOUND, "Stylesheet file was not found: {0}"},


  /** ER_IOEXCEPTION          */
  //public static final int ER_IOEXCEPTION = 37;


  {
    ER_IOEXCEPTION,
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u30d5\u30a1\u30a4\u30eb\u3067\u5165\u51fa\u529b\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f: {0}"},
//      "Had IO Exception with stylesheet file: {0}"},


  /** ER_NO_HREF_ATTRIB          */
  //public static final int ER_NO_HREF_ATTRIB = 38;


  {
    ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) {0} \u306e href \u5c5e\u6027\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//      "(StylesheetHandler) Could not find href attribute for {0}"},


  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  //public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;


  {
    ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u30a4\u30f3\u30af\u30eb\u30fc\u30c9\u3057\u3066\u3044\u307e\u3059\u3002"},
//      "(StylesheetHandler) {0} is directly or indirectly including itself!"},


  /** ER_PROCESSINCLUDE_ERROR          */
  //public static final int ER_PROCESSINCLUDE_ERROR = 40;


  {
    ER_PROCESSINCLUDE_ERROR,
      "StylesheetHandler.processInclude \u30a8\u30e9\u30fc\u3001{0}"},
//      "StylesheetHandler.processInclude error, {0}"},


  /** ER_MISSING_LANG_ATTRIB          */
  //public static final int ER_MISSING_LANG_ATTRIB = 41;


  {
    ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} \u5c5e\u6027 ''lang'' \u304c\u8db3\u308a\u307e\u305b\u3093"},
//      "(StylesheetHandler) {0} attribute 'lang' is missing"},


  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  //public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;


  {
    ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) {0} \u8981\u7d20\u3092\u914d\u7f6e\u3057\u5fd8\u308c\u3066\u3044\u307e\u305b\u3093\u304b?? \u30b3\u30f3\u30c6\u30ca\u8981\u7d20 ''component'' \u304c\u8db3\u308a\u307e\u305b\u3093"},
//      "(StylesheetHandler) misplaced {0} element?? Missing container element 'component'"},


  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  //public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;


  {
    ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "Element\u3001DocumentFragment\u3001Document\u3001\u307e\u305f\u306f PrintWriter \u306b\u3060\u3051\u51fa\u529b\u3067\u304d\u307e\u3059\u3002"},
//      "Can only output to an Element, DocumentFragment, Document, or PrintWriter."},


  /** ER_PROCESS_ERROR          */
  //public static final int ER_PROCESS_ERROR = 44;


  {
    ER_PROCESS_ERROR, "StylesheetRoot.process \u30a8\u30e9\u30fc"},
//    ER_PROCESS_ERROR, "StylesheetRoot.process error"},


  /** ER_UNIMPLNODE_ERROR          */
  //public static final int ER_UNIMPLNODE_ERROR = 45;


  {
    ER_UNIMPLNODE_ERROR, "UnImplNode \u30a8\u30e9\u30fc: {0}"},
//    ER_UNIMPLNODE_ERROR, "UnImplNode error: {0}"},


  /** ER_NO_SELECT_EXPRESSION          */
  //public static final int ER_NO_SELECT_EXPRESSION = 46;


  {
    ER_NO_SELECT_EXPRESSION,
      "\u30a8\u30e9\u30fc\u3002xpath \u306e\u9078\u629e\u5f0f (-select) \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//      "Error! Did not find xpath select expression (-select)."},


  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  //public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;


  {
    ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "XSLProcessor \u3092\u76f4\u5217\u5316\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can not serialize an XSLProcessor!"},


  /** ER_NO_INPUT_STYLESHEET          */
  //public static final int ER_NO_INPUT_STYLESHEET = 48;


  {
    ER_NO_INPUT_STYLESHEET,
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u5165\u529b\u304c\u6307\u5b9a\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//      "Stylesheet input was not specified!"},


  /** ER_FAILED_PROCESS_STYLESHEET          */
  //public static final int ER_FAILED_PROCESS_STYLESHEET = 49;


  {
    ER_FAILED_PROCESS_STYLESHEET,
      "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u3067\u306e\u51e6\u7406\u306b\u5931\u6557\u3057\u307e\u3057\u305f\u3002"},
//      "Failed to process stylesheet!"},


  /** ER_COULDNT_PARSE_DOC          */
  //public static final int ER_COULDNT_PARSE_DOC = 50;


  {
    ER_COULDNT_PARSE_DOC, "{0} \u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u69cb\u6587\u89e3\u6790\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//    ER_COULDNT_PARSE_DOC, "Could not parse {0} document!"},


  /** ER_COULDNT_FIND_FRAGMENT          */
  //public static final int ER_COULDNT_FIND_FRAGMENT = 51;


  {
    ER_COULDNT_FIND_FRAGMENT, "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//    ER_COULDNT_FIND_FRAGMENT, "Could not find fragment: {0}"},


  /** ER_NODE_NOT_ELEMENT          */
  //public static final int ER_NODE_NOT_ELEMENT = 52;


  {
    ER_NODE_NOT_ELEMENT,
      "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u8b58\u5225\u5b50\u304c\u6307\u3059\u30ce\u30fc\u30c9\u304c\u8981\u7d20\u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3067\u3057\u305f: {0}"},
//      "Node pointed to by fragment identifier was not an element: {0}"},


  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;


  {
    ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each \u306f match \u5c5e\u6027\u307e\u305f\u306f name \u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//      "for-each must have either a match or name attribute"},


  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;


  {
    ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306f match \u5c5e\u6027\u307e\u305f\u306f name \u5c5e\u6027\u3092\u6301\u305f\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//      "templates must have either a match or name attribute"},


  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  //public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;


  {
    ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306e\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306b\u30af\u30ed\u30fc\u30f3\u304c\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "No clone of a document fragment!"},


  /** ER_CANT_CREATE_ITEM          */
  //public static final int ER_CANT_CREATE_ITEM = 56;


  {
    ER_CANT_CREATE_ITEM,
      "\u7d50\u679c\u30c4\u30ea\u30fc\u306b\u9805\u76ee\u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093: {0}"},
//      "Can not create item in result tree: {0}"},


  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  //public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;


  {
    ER_XMLSPACE_ILLEGAL_VALUE,
      "\u30bd\u30fc\u30b9 XML \u306e xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}"},
//      "xml:space in the source XML has an illegal value: {0}"},


  /** ER_NO_XSLKEY_DECLARATION          */
  //public static final int ER_NO_XSLKEY_DECLARATION = 58;


  {
    ER_NO_XSLKEY_DECLARATION,
      "{0} \u306b xsl:key \u5ba3\u8a00\u304c\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "There is no xsl:key declaration for {0}!"},


  /** ER_CANT_CREATE_URL          */
  //public static final int ER_CANT_CREATE_URL = 59;


  {
    ER_CANT_CREATE_URL, "\u30a8\u30e9\u30fc\u3002{0} \u306e URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093"},
//    ER_CANT_CREATE_URL, "Error! Cannot create url for: {0}"},


  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  //public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;


  {
    ER_XSLFUNCTIONS_UNSUPPORTED, "xsl:functions \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//    ER_XSLFUNCTIONS_UNSUPPORTED, "xsl:functions is unsupported"},


  /** ER_PROCESSOR_ERROR          */
  //public static final int ER_PROCESSOR_ERROR = 61;


  {
    ER_PROCESSOR_ERROR, "XSLT TransformerFactory \u30a8\u30e9\u30fc"},
//    ER_PROCESSOR_ERROR, "XSLT TransformerFactory Error"},


  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  //public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;


  {
    ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} \u306f\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u5185\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "(StylesheetHandler) {0} not allowed inside a stylesheet!"},


  /** ER_RESULTNS_NOT_SUPPORTED          */
  //public static final int ER_RESULTNS_NOT_SUPPORTED = 63;


  {
    ER_RESULTNS_NOT_SUPPORTED,
      "result-ns \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:output \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
//      "result-ns no longer supported!  Use xsl:output instead."},


  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  //public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;


  {
    ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:strip-space \u307e\u305f\u306f xsl:preserve-space \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
//      "default-space no longer supported!  Use xsl:strip-space or xsl:preserve-space instead."},


  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  //public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;


  {
    ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result \u306f\u3082\u3046\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4ee3\u308f\u308a\u306b xsl:output \u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
//      "indent-result no longer supported!  Use xsl:output instead."},


  /** ER_ILLEGAL_ATTRIB          */
  //public static final int ER_ILLEGAL_ATTRIB = 66;


  {
    ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u3042\u308a\u307e\u3059: {1}"},
//      "(StylesheetHandler) {0} has an illegal attribute: {1}"},


  /** ER_UNKNOWN_XSL_ELEM          */
  //public static final int ER_UNKNOWN_XSL_ELEM = 67;


  {
    ER_UNKNOWN_XSL_ELEM, "\u672a\u77e5\u306e XSL \u8981\u7d20: {0}"},
//    ER_UNKNOWN_XSL_ELEM, "Unknown XSL element: {0}"},


  /** ER_BAD_XSLSORT_USE          */
  //public static final int ER_BAD_XSLSORT_USE = 68;


  {
    ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort \u306f xsl:apply-templates \u307e\u305f\u306f xsl:for-each \u3068\u3044\u3063\u3057\u3087\u306b\u306e\u307f\u4f7f\u7528\u3067\u304d\u307e\u3059\u3002"},
//      "(StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each."},


  /** ER_MISPLACED_XSLWHEN          */
  //public static final int ER_MISPLACED_XSLWHEN = 69;


  {
    ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) xsl:when \u306e\u914d\u7f6e\u304c\u8aa4\u3063\u3066\u3044\u307e\u3059\u3002"},
//      "(StylesheetHandler) misplaced xsl:when!"},


  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;


  {
    ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when \u306e\u89aa\u306f xsl:choose \u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "(StylesheetHandler) xsl:when not parented by xsl:choose!"},


  /** ER_MISPLACED_XSLOTHERWISE          */
  //public static final int ER_MISPLACED_XSLOTHERWISE = 71;


  {
    ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) xsl:otherwise \u306e\u914d\u7f6e\u304c\u8aa4\u3063\u3066\u3044\u307e\u3059\u3002"},
//      "(StylesheetHandler) misplaced xsl:otherwise!"},


  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;


  {
    ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise \u306e\u89aa\u306f xsl:choose \u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002"},
//      "(StylesheetHandler) xsl:otherwise not parented by xsl:choose!"},


  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  //public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;


  {
    ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} \u306f\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u4e2d\u306b\u7f6e\u304f\u3053\u3068\u3092\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "(StylesheetHandler) {0} is not allowed inside a template!"},


  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  //public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;


  {
    ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} \u62e1\u5f35\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e {1} \u304c\u672a\u77e5\u3067\u3059"},
//      "(StylesheetHandler) {0} extension namespace prefix {1} unknown"},


  /** ER_IMPORTS_AS_FIRST_ELEM          */
  //public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;


  {
    ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) \u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u6700\u521d\u306e\u8981\u7d20\u3067\u306e\u307f\u30a4\u30f3\u30dd\u30fc\u30c8\u304c\u5b9f\u884c\u53ef\u80fd\u3067\u3059\u3002"},
//      "(StylesheetHandler) Imports can only occur as the first elements in the stylesheet!"},


  /** ER_IMPORTING_ITSELF          */
  //public static final int ER_IMPORTING_ITSELF = 76;


  {
    ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u3066\u3044\u307e\u3059\u3002"},
//      "(StylesheetHandler) {0} is directly or indirectly importing itself!"},


  /** ER_XMLSPACE_ILLEGAL_VAL          */
  //public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;


  {
    ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) " + "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {0}"},
//      "(StylesheetHandler) " + "xml:space has an illegal value: {0}"},


  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;


  {
    ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet \u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3002"},
//      "processStylesheet not succesfull!"},


  /** ER_SAX_EXCEPTION          */
  //public static final int ER_SAX_EXCEPTION = 79;


  {
    ER_SAX_EXCEPTION, "SAX \u4f8b\u5916"},
//    ER_SAX_EXCEPTION, "SAX Exception"},


  /** ER_FUNCTION_NOT_SUPPORTED          */
  //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


  {
    ER_FUNCTION_NOT_SUPPORTED, "Function \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002"},
//    ER_FUNCTION_NOT_SUPPORTED, "Function not supported!"},


  /** ER_XSLT_ERROR          */
  //public static final int ER_XSLT_ERROR = 81;


  {
    ER_XSLT_ERROR, "XSLT \u30a8\u30e9\u30fc"},
//    ER_XSLT_ERROR, "XSLT Error"},


  /** ER_CURRENCY_SIGN_ILLEGAL          */
  //public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;


  {
    ER_CURRENCY_SIGN_ILLEGAL,
      "\u901a\u8ca8\u8a18\u53f7\u306f\u66f8\u5f0f\u30d1\u30bf\u30fc\u30f3\u6587\u5b57\u5217\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "currency sign is not allowed in format pattern string"},


  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  //public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;


  {
    ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "Document \u95a2\u6570\u306f Stylesheet DOM \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002"},
//      "Document function not supported in Stylesheet DOM!"},


  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  //public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;


  {
    ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "non-Prefix \u30ea\u30be\u30eb\u30d0\u306e\u63a5\u982d\u8f9e\u306f\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can't resolve prefix of non-Prefix resolver!"},


  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  //public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;


  {
    ER_REDIRECT_COULDNT_GET_FILENAME,
      "Rediret \u62e1\u5f35: \u30d5\u30a1\u30a4\u30eb\u540d\u3092\u53d6\u5f97\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f - file \u5c5e\u6027\u307e\u305f\u306f select \u5c5e\u6027\u304c\u6709\u52b9\u306a\u6587\u5b57\u5217\u3092\u623b\u3059\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002 "},
//      "Redirect extension: Could not get filename - file or select attribute must return vald string."},


  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  //public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;


  {
    ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "Redirect \u62e1\u5f35\u3067 FormatterListener \u3092\u69cb\u7bc9\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can not build FormatterListener in Redirect extension!"},


  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  //public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;


  {
    ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "exclude-result-prefixes \u306e\u63a5\u982d\u8f9e\u304c\u6709\u52b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093: {0}"},
//      "Prefix in exclude-result-prefixes is not valid: {0}"},


  /** ER_MISSING_NS_URI          */
  //public static final int ER_MISSING_NS_URI = 88;


  {
    ER_MISSING_NS_URI,
      "\u6307\u5b9a\u3055\u308c\u305f\u63a5\u982d\u8f9e\u306e\u540d\u524d\u7a7a\u9593 URI \u304c\u3042\u308a\u307e\u305b\u3093"},
//      "Missing namespace URI for specified prefix"},


  /** ER_MISSING_ARG_FOR_OPTION          */
  //public static final int ER_MISSING_ARG_FOR_OPTION = 89;


  {
    ER_MISSING_ARG_FOR_OPTION,
      "\u30aa\u30d7\u30b7\u30e7\u30f3\u306e\u5f15\u6570\u304c\u3042\u308a\u307e\u305b\u3093: {0}"},
//      "Missing argument for option: {0}"},


  /** ER_INVALID_OPTION          */
  //public static final int ER_INVALID_OPTION = 90;


  {
    ER_INVALID_OPTION, "\u7121\u52b9\u306a\u30aa\u30d7\u30b7\u30e7\u30f3: {0}"},
//    ER_INVALID_OPTION, "Invalid option: {0}"},


  /** ER_MALFORMED_FORMAT_STRING          */
  //public static final int ER_MALFORMED_FORMAT_STRING = 91;


  {
    ER_MALFORMED_FORMAT_STRING, "\u5f62\u5f0f\u306e\u8aa4\u3063\u305f\u6587\u5b57\u5217: {0}"},
//    ER_MALFORMED_FORMAT_STRING, "Malformed format string: {0}"},


  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;


  {
    ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u306b\u306f 'version' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//      "xsl:stylesheet requires a 'version' attribute!"},


  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;


  {
    ER_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c5e\u6027: {0} \u306b\u4e0d\u5f53\u306a\u5024\u304c\u3042\u308a\u307e\u3059: {1}"},
//      "Attribute: {0} has an illegal value: {1}"},


  /** ER_CHOOSE_REQUIRES_WHEN          */
  //public static final int ER_CHOOSE_REQUIRES_WHEN = 94;


  {
    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose \u306b\u306f xsl:when \u304c\u5fc5\u8981\u3067\u3059"},
//    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose requires an xsl:when"},


  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  //public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;


  {
    ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports \u306f xsl:for-each \u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093"},
//      "xsl:apply-imports not allowed in a xsl:for-each"},


  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;


  {
    ER_CANT_USE_DTM_FOR_OUTPUT,
      "\u51fa\u529b DOM \u30ce\u30fc\u30c9\u306b DTMLiaison \u3092\u4f7f\u7528\u3067\u304d\u307e\u305b\u3093... \u4ee3\u308f\u308a\u306b org.apache.xpath.DOM2Helper \u3092\u6e21\u3057\u307e\u3059\u3002"},
//      "Cannot use a DTMLiaison for an output DOM node... pass a org.apache.xpath.DOM2Helper instead!"},


  /** ER_CANT_USE_DTM_FOR_INPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;


  {
    ER_CANT_USE_DTM_FOR_INPUT,
      "\u5165\u529b DOM \u30ce\u30fc\u30c9\u306b DTMLiaison \u3092\u4f7f\u7528\u3067\u304d\u307e\u305b\u3093... \u4ee3\u308f\u308a\u306b org.apache.xpath.DOM2Helper \u3092\u6e21\u3057\u307e\u3059\u3002"},
//      "Cannot use a DTMLiaison for a input DOM node... pass a org.apache.xpath.DOM2Helper instead!"},


  /** ER_CALL_TO_EXT_FAILED          */
  //public static final int ER_CALL_TO_EXT_FAILED = 98;


  {
    ER_CALL_TO_EXT_FAILED,
      "\u62e1\u5f35\u8981\u7d20\u306e\u547c\u3073\u51fa\u3057\u306b\u5931\u6557\u3057\u307e\u3057\u305f: {0}"},
//      "Call to extension element failed: {0}"},


  /** ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 99;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u63a5\u982d\u8f9e\u306f\u540d\u524d\u7a7a\u9593\u306b\u5909\u308f\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059: {0}"},
//      "Prefix must resolve to a namespace: {0}"},


  /** ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 100;


  {
    ER_INVALID_UTF16_SURROGATE,
      "\u7121\u52b9\u306a UTF-16 \u4ee3\u7406\u304c\u691c\u51fa\u3055\u308c\u307e\u3057\u305f: {0} ?"},
//      "Invalid UTF-16 surrogate detected: {0} ?"},


  /** ER_XSLATTRSET_USED_ITSELF          */
  //public static final int ER_XSLATTRSET_USED_ITSELF = 101;


  {
    ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} \u306f\u81ea\u8eab\u3092\u4f7f\u7528\u3057\u305f\u305f\u3081\u3001\u7121\u9650\u30eb\u30fc\u30d7\u304c\u767a\u751f\u3057\u307e\u3059\u3002"},
//      "xsl:attribute-set {0} used itself, which will cause an infinite loop."},


  /** ER_CANNOT_MIX_XERCESDOM          */
  //public static final int ER_CANNOT_MIX_XERCESDOM = 102;


  {
    ER_CANNOT_MIX_XERCESDOM,
      "\u975e Xerces-DOM \u5165\u529b\u3068 Xerces-DOM \u51fa\u529b\u3092\u6df7\u5408\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can not mix non Xerces-DOM input with Xerces-DOM output!"},


  /** ER_TOO_MANY_LISTENERS          */
  //public static final int ER_TOO_MANY_LISTENERS = 103;


  {
    ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},
//      "addTraceListenersToStylesheet - TooManyListenersException"},


  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  //public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;


  {
    ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "ElemTemplateElement.readObject \u306b {0} \u304c\u3042\u308a\u307e\u3059"},
//      "In ElemTemplateElement.readObject: {0}"},


  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  //public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;


  {
    ER_DUPLICATE_NAMED_TEMPLATE,
      "\u4ee5\u4e0b\u306b\u793a\u3059\u540d\u524d\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u8907\u6570\u898b\u3064\u304b\u308a\u307e\u3057\u305f: {0}"},
//      "Found more than one template named: {0}"},


  /** ER_INVALID_KEY_CALL          */
  //public static final int ER_INVALID_KEY_CALL = 106;


  {
    ER_INVALID_KEY_CALL,
      "\u7121\u52b9\u306a\u95a2\u6570\u547c\u3073\u51fa\u3057: recursive key() \u547c\u3073\u51fa\u3057\u306f\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093"},
//      "Invalid function call: recursive key() calls are not allowed"},

  
  /** Variable is referencing itself          */
  //public static final int ER_REFERENCING_ITSELF = 107;


  {
    ER_REFERENCING_ITSELF,
      "\u5909\u6570 {0} \u306f\u76f4\u63a5\u7684\u307e\u305f\u306f\u9593\u63a5\u7684\u306b\u81ea\u8eab\u3092\u53c2\u7167\u3057\u3066\u3044\u307e\u3059\u3002"},
//      "Variable {0} is directly or indirectly referencing itself!"},

  
  /** Illegal DOMSource input          */
  //public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;


  {
    ER_ILLEGAL_DOMSOURCE_INPUT,
      "newTemplates \u306e DOMSource \u306b\u5bfe\u3059\u308b\u5165\u529b\u30ce\u30fc\u30c9\u306f null \u306b\u3067\u304d\u307e\u305b\u3093\u3002 "},
//      "The input node can not be null for a DOMSource for newTemplates!"},

	
	/** Class not found for option         */
  //public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;


  {
    ER_CLASS_NOT_FOUND_FOR_OPTION,
			"\u30aa\u30d7\u30b7\u30e7\u30f3 {0} \u306b\u5bfe\u3059\u308b\u30af\u30e9\u30b9\u30d5\u30a1\u30a4\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},
//			"Class file not found for option {0}"},

	
	/** Required Element not found         */
  //public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;


  {
    ER_REQUIRED_ELEM_NOT_FOUND,
			"\u5fc5\u8981\u306a\u8981\u7d20\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: {0}"},
//			"Required Element not found: {0}"},

  
  /** InputStream cannot be null         */
  //public static final int ER_INPUT_CANNOT_BE_NULL = 111;


  {
    ER_INPUT_CANNOT_BE_NULL,
			"InputStream \u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//			"InputStream cannot be null"},

  
  /** URI cannot be null         */
  //public static final int ER_URI_CANNOT_BE_NULL = 112;


  {
    ER_URI_CANNOT_BE_NULL,
			"URI \u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//			"URI cannot be null"},

  
  /** File cannot be null         */
  //public static final int ER_FILE_CANNOT_BE_NULL = 113;


  {
    ER_FILE_CANNOT_BE_NULL,
			"File \u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//			"File cannot be null"},

  
   /** InputSource cannot be null         */
  //public static final int ER_SOURCE_CANNOT_BE_NULL = 114;


  {
    ER_SOURCE_CANNOT_BE_NULL,
			"InputSource \u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//			"InputSource cannot be null"},

  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"cause \u3092\u4e0a\u66f8\u304d\u3067\u304d\u307e\u305b\u3093"},
//			"Cannot overwrite cause"},

  
  /** Could not initialize BSF Manager        */
  //public static final int ER_CANNOT_INIT_BSFMGR = 116;


  {
    ER_CANNOT_INIT_BSFMGR,
			"BSF Manager \u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//			"Could not initialize BSF Manager"},

  
  /** Could not compile extension       */
  //public static final int ER_CANNOT_CMPL_EXTENSN = 117;


  {
    ER_CANNOT_CMPL_EXTENSN,
			"\u62e1\u5f35\u3092\u30b3\u30f3\u30d1\u30a4\u30eb\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//			"Could not compile extension"},

  
  /** Could not create extension       */
  //public static final int ER_CANNOT_CREATE_EXTENSN = 118;


  {
    ER_CANNOT_CREATE_EXTENSN,
       "\u62e1\u5f35 {0} \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002\u539f\u56e0: {1}"},
//      "Could not create extension: {0} because of: {1}"},

  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  //public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;


  {
    ER_INSTANCE_MTHD_CALL_REQUIRES,
      "\u30e1\u30bd\u30c3\u30c9 {0} \u306e\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u30e1\u30bd\u30c3\u30c9\u547c\u3073\u51fa\u3057\u306f\u3001\u6700\u521d\u306e\u5f15\u6570\u306b Objcet \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3092\u5fc5\u8981\u3068\u3057\u307e\u3059"},
//      "Instance method call to method {0} requires an Object instance as first argument"},

  
  /** Invalid element name specified       */
  //public static final int ER_INVALID_ELEMENT_NAME = 120;


  {
    ER_INVALID_ELEMENT_NAME,
      "\u7121\u52b9\u306a\u8981\u7d20\u540d\u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f {0}"},
//      "Invalid element name specified {0}"},

  
   /** Element name method must be static      */
  //public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;


  {
    ER_ELEMENT_NAME_METHOD_STATIC,
      "\u8981\u7d20\u540d\u30e1\u30bd\u30c3\u30c9\u306f static \u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093 {0}"},
//      "Element name method must be static {0}"},

  
   /** Extension function {0} : {1} is unknown      */
  //public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;


  {
    ER_EXTENSION_FUNC_UNKNOWN,
             "\u62e1\u5f35\u95a2\u6570 {0} : {1} \u306f\u672a\u77e5\u3067\u3059"},
//             "Extension function {0} : {1} is unknown"},

  
   /** More than one best match for constructor for       */
  //public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;


  {
    ER_MORE_MATCH_CONSTRUCTOR,
             "{0} \u306e\u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf\u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059 {0}"},
//             "More than one best match for constructor for {0}"},

  
   /** More than one best match for method      */
  //public static final int ER_MORE_MATCH_METHOD = 124;


  {
    ER_MORE_MATCH_METHOD,
             "\u30e1\u30bd\u30c3\u30c9 {0} \u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059"},
//             "More than one best match for method {0}"},

  
   /** More than one best match for element method      */
  //public static final int ER_MORE_MATCH_ELEMENT = 125;


  {
    ER_MORE_MATCH_ELEMENT,
             "\u8981\u7d20\u30e1\u30bd\u30c3\u30c9 {0} \u306b\u6700\u9069\u306a\u4e00\u81f4\u304c\u8907\u6570\u500b\u3042\u308a\u307e\u3059"},
//             "More than one best match for element method {0}"},

  
   /** Invalid context passed to evaluate       */
  //public static final int ER_INVALID_CONTEXT_PASSED = 126;


  {
    ER_INVALID_CONTEXT_PASSED,
             "{0} \u3092\u8a55\u4fa1\u3059\u308b\u306e\u306b\u7121\u52b9\u306a\u30b3\u30f3\u30c6\u30ad\u30b9\u30c8\u304c\u6e21\u3055\u308c\u307e\u3057\u305f"},
//             "Invalid context passed to evaluate {0}"},

  
   /** Pool already exists       */
  //public static final int ER_POOL_EXISTS = 127;


  {
    ER_POOL_EXISTS,
             "Pool \u304c\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059"},
//             "Pool already exists"},

  
   /** No driver Name specified      */
  //public static final int ER_NO_DRIVER_NAME = 128;


  {
    ER_NO_DRIVER_NAME,
             "\u30c9\u30e9\u30a4\u30d0\u306e Name \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//             "No driver Name specified"},

  
   /** No URL specified     */
  //public static final int ER_NO_URL = 129;


  {
    ER_NO_URL,
             "URL \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//             "No URL specified"},

  
   /** Pool size is less than one    */
  //public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;


  {
    ER_POOL_SIZE_LESSTHAN_ONE,
             "1 \u3088\u308a\u5c0f\u3055\u3044 Pool \u306e\u30b5\u30a4\u30ba\u3067\u3059\u3002"},
//             "Pool size is less than one!"},

  
   /** Invalid driver name specified    */
  //public static final int ER_INVALID_DRIVER = 131;


  {
    ER_INVALID_DRIVER,
             "\u7121\u52b9\u306a\u30c9\u30e9\u30a4\u30d0\u540d\u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f\u3002"},
//             "Invalid driver name specified!"},

  
   /** Did not find the stylesheet root    */
  //public static final int ER_NO_STYLESHEETROOT = 132;


  {
    ER_NO_STYLESHEETROOT,
             "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u30eb\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//             "Did not find the stylesheet root!"},

  
   /** Illegal value for xml:space     */
  //public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;


  {
    ER_ILLEGAL_XMLSPACE_VALUE,
         "xml:space \u306b\u4e0d\u5f53\u306a\u5024\u3067\u3059"},
//         "Illegal value for xml:space"},

  
   /** processFromNode failed     */
  //public static final int ER_PROCESSFROMNODE_FAILED = 134;


  {
    ER_PROCESSFROMNODE_FAILED,
         "processFromNode \u304c\u5931\u6557\u3057\u307e\u3057\u305f"},
//         "processFromNode failed"},

  
   /** The resource [] could not load:     */
  //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;


  {
    ER_RESOURCE_COULD_NOT_LOAD,
        "\u30ea\u30bd\u30fc\u30b9 [ {0} ] \u306f\u6b21\u306e\u3082\u306e\u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f: {1} \n {2} \t {3}"},
//        "The resource [ {0} ] could not load: {1} \n {2} \t {3}"},

   
  
   /** Buffer size <=0     */
  //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;


  {
    ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "\u30d0\u30c3\u30d5\u30a1\u30b5\u30a4\u30ba <=0"},
//        "Buffer size <=0"},

  
   /** Unknown error when calling extension    */
  //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;


  {
    ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "\u62e1\u5f35\u3092\u547c\u3073\u51fa\u3059\u3068\u304d\u306b\u672a\u77e5\u306e\u30a8\u30e9\u30fc\u304c\u767a\u751f\u3057\u307e\u3057\u305f"},
//        "Unknown error when calling extension"},

  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  //public static final int ER_NO_NAMESPACE_DECL = 138;


  {
    ER_NO_NAMESPACE_DECL,
        "\u63a5\u982d\u8f9e {0} \u306b\u306f\u5bfe\u5fdc\u3059\u308b\u540d\u524d\u7a7a\u9593\u5ba3\u8a00\u304c\u3042\u308a\u307e\u305b\u3093"},
//        "Prefix {0} does not have a corresponding namespace declaration"},

  
   /** Element content not allowed for lang=javaclass   */
  //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;


  {
    ER_ELEM_CONTENT_NOT_ALLOWED,
        "\u8981\u7d20\u306e\u5185\u5bb9\u306f lang=javaclass {0} \u306b\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093"},
//        "Element content not allowed for lang=javaclass {0}"},
     
  
   /** Stylesheet directed termination   */
  //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;


  {
    ER_STYLESHEET_DIRECTED_TERMINATION,
        "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306b\u3088\u308a\u7d42\u4e86\u304c\u6307\u793a\u3055\u308c\u307e\u3057\u305f"},
//        "Stylesheet directed termination"},

  
   /** 1 or 2   */
  //public static final int ER_ONE_OR_TWO = 141;


  {
    ER_ONE_OR_TWO,
        "1 \u307e\u305f\u306f 2"},
//        "1 or 2"},

  
   /** 2 or 3   */
  //public static final int ER_TWO_OR_THREE = 142;


  {
    ER_TWO_OR_THREE,
        "2 \u307e\u305f\u306f 3"},
//        "2 or 3"},

  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;


  {
    ER_COULD_NOT_LOAD_RESOURCE,
        "{0} \u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f (CLASSPATH \u3092\u30c1\u30a7\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044)\u3002\u30c7\u30d5\u30a9\u30eb\u30c8\u3060\u3051\u3092\u4f7f\u7528\u3057\u307e\u3059\u3002"},
//        "Could not load {0} (check CLASSPATH), now using just the defaults"},

  
   /** Cannot initialize default templates   */
  //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;


  {
    ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "\u30c7\u30d5\u30a9\u30eb\u30c8\u306e\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093"},
//        "Cannot initialize default templates"},

  
   /** Result should not be null   */
  //public static final int ER_RESULT_NULL = 145;


  {
    ER_RESULT_NULL,
        "Result \u306f null \u306b\u306f\u3067\u304d\u307e\u305b\u3093"},
//        "Result should not be null"},

    
   /** Result could not be set   */
  //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;


  {
    ER_RESULT_COULD_NOT_BE_SET,
        "Result \u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//        "Result could not be set"},

  
   /** No output specified   */
  //public static final int ER_NO_OUTPUT_SPECIFIED = 147;


  {
    ER_NO_OUTPUT_SPECIFIED,
        "\u51fa\u529b\u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//        "No output specified"},

  
   /** Can't transform to a Result of type   */
  //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;


  {
    ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "\u30bf\u30a4\u30d7 {0} \u306e Result \u306b\u5909\u5f62\u3067\u304d\u307e\u305b\u3093"},
//        "Can't transform to a Result of type {0}"},

  
   /** Can't transform to a Source of type   */
  //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;


  {
    ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "\u30bf\u30a4\u30d7 {0} \u306e Source \u306b\u5909\u5f62\u3067\u304d\u307e\u305b\u3093"},
//        "Can't transform a Source of type {0}"},

  
   /** Null content handler  */
  //public static final int ER_NULL_CONTENT_HANDLER = 150;


  {
    ER_NULL_CONTENT_HANDLER,
        "Null \u30b3\u30f3\u30c6\u30f3\u30c4\u30cf\u30f3\u30c9\u30e9"},
//        "Null content handler"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 151;


  {
    ER_NULL_ERROR_HANDLER,
        "Null \u30a8\u30e9\u30fc\u30cf\u30f3\u30c9\u30e9"},
//        "Null error handler"},

  
   /** parse can not be called if the ContentHandler has not been set */
  //public static final int ER_CANNOT_CALL_PARSE = 152;


  {
    ER_CANNOT_CALL_PARSE,
        "ContentHandler \u304c\u8a2d\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u69cb\u6587\u89e3\u6790\u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093"},
//        "parse can not be called if the ContentHandler has not been set"},

  
   /**  No parent for filter */
  //public static final int ER_NO_PARENT_FOR_FILTER = 153;


  {
    ER_NO_PARENT_FOR_FILTER,
        "\u30d5\u30a3\u30eb\u30bf\u51e6\u7406\u3059\u308b\u89aa\u304c\u3042\u308a\u307e\u305b\u3093"},
//        "No parent for filter"},

  
  
   /**  No stylesheet found in: {0}, media */
  //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;


  {
    ER_NO_STYLESHEET_IN_MEDIA,
         "{0} \u306b\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3002\u30e1\u30c7\u30a3\u30a2= {1}"},
//         "No stylesheet found in: {0}, media= {1}"},

  
   /**  No xml-stylesheet PI found in */
  //public static final int ER_NO_STYLESHEET_PI = 155;


  {
    ER_NO_STYLESHEET_PI,
         "xml-stylesheet PI \u304c {0} \u306b\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//         "No xml-stylesheet PI found in: {0}"},

  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;


  {
    ER_NO_DEFAULT_IMPL,
         "\u30c7\u30d5\u30a9\u30eb\u30c8\u5b9f\u88c5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},
//         "No default implementation found "},

  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0}) \u306f\u73fe\u5728\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "ChunkedIntArray({0}) not currently supported"},

  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "\u30b9\u30ed\u30c3\u30c8\u3088\u308a\u3082\u5927\u304d\u3044\u30aa\u30d5\u30bb\u30c3\u30c8"},
//       "Offset bigger than slot"},

  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;


  {
    ER_COROUTINE_NOT_AVAIL,
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u306f\u7121\u52b9\u3067\u3059\u3002id={0}"},
//       "Coroutine not available, id={0}"},

  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;


  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager \u306f co_exit() \u8981\u6c42\u3092\u53d7\u3051\u53d6\u308a\u307e\u3057\u305f"},
//       "CoroutineManager received co_exit() request"},

  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;


  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet() \u306f\u5931\u6557\u3057\u307e\u3057\u305f"},
//       "co_joinCoroutineSet() failed"},

  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;


  {
    ER_COROUTINE_PARAM,
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u30d1\u30e9\u30e1\u30fc\u30bf\u30a8\u30e9\u30fc ({0})"},
//       "Coroutine parameter error ({0})"},

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: \u30d1\u30fc\u30b5 doTerminate \u306e\u7b54\u3048 {0}"},
//       "\nUNEXPECTED: Parser doTerminate answers {0}"},

  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b parse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
//       "parse may not be called while parsing"},

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u30a8\u30e9\u30fc: \u5165\u529b\u3055\u308c\u305f\u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Error: typed iterator for axis  {0} not implemented"},

  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u30a8\u30e9\u30fc: \u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Error: iterator for axis {0} not implemented "},

  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "\u53cd\u5fa9\u5b50\u30af\u30ed\u30fc\u30f3\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Iterator clone not supported"},

  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


  {
    ER_UNKNOWN_AXIS_TYPE,
       "\u672a\u77e5\u306e\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u30bf\u30a4\u30d7: {0}"},
//       "Unknown axis traversal type: {0}"},

  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;


  {
    ER_AXIS_NOT_SUPPORTED,
       "\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//       "Axis traverser not supported: {0}"},

  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;


  {
    ER_NO_DTMIDS_AVAIL,
       "\u3053\u308c\u4ee5\u4e0a\u306e DTM ID \u306f\u7121\u52b9\u3067\u3059"},
//       "No more DTM IDs are available"},

  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;


  {
    ER_NOT_SUPPORTED,
       "\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//       "Not supported: {0}"},

  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;


  {
    ER_NODE_NON_NULL,
       "getDTMHandleFromNode \u306e\u30ce\u30fc\u30c9\u306f null \u4ee5\u5916\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//       "Node must be non-null for getDTMHandleFromNode"},

  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


  {
    ER_COULD_NOT_RESOLVE_NODE,
       "\u30ce\u30fc\u30c9\u3092\u30cf\u30f3\u30c9\u30eb\u306b\u5909\u3048\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//       "Could not resolve the node to a handle"},

  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


  {
    ER_STARTPARSE_WHILE_PARSING,
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b startParse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
//       "startParse may not be called while parsing"},

  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse \u306f null \u3067\u306a\u3044 SAXParser \u3092\u5fc5\u8981\u3068\u3057\u307e\u3059"},
//       "startParse needs a non-null SAXParser"},

  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;


  {
    ER_COULD_NOT_INIT_PARSER,
       "\u30d1\u30fc\u30b5\u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//       "could not initialize parser with"},

  
   /**  Value for property {0} should be a Boolean instance  */
  //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;


  {
    ER_PROPERTY_VALUE_BOOLEAN,
       "\u30d7\u30ed\u30d1\u30c6\u30a3 {0} \u306e\u5024\u306f Boolean \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//       "Value for property {0} should be a Boolean instance"},

  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;


  {
    ER_EXCEPTION_CREATING_POOL,
       "\u4f8b\u5916\u306b\u3088\u308a\u30d7\u30fc\u30eb\u306b\u65b0\u3057\u3044\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3092\u4f5c\u6210\u3057\u3066\u3044\u307e\u3059"},
//       "exception creating new instance for pool"},

  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u30a8\u30b9\u30b1\u30fc\u30d7\u30b7\u30fc\u30b1\u30f3\u30b9\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059"},
//       "Path contains invalid escape sequence"},

  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;


  {
    ER_SCHEME_REQUIRED,
       "\u30b9\u30ad\u30fc\u30de\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//       "Scheme is required!"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;


  {
    ER_NO_SCHEME_IN_URI,
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093: {0}"},
//       "No scheme found in URI: {0}"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;


  {
    ER_NO_SCHEME_INURI,
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093"},
//       "No scheme found in URI"},

  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;


  {
    ER_PATH_INVALID_CHAR,
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059: {0}"},
//       "Path contains invalid character: {0}"},

  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


  {
    ER_SCHEME_FROM_NULL_STRING,
       "null \u6587\u5b57\u5217\u304b\u3089\u30b9\u30ad\u30fc\u30de\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Cannot set scheme from null string"},

  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


  {
    ER_SCHEME_NOT_CONFORMANT,
       "\u30b9\u30ad\u30fc\u30de\u304c\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002"},
//       "The scheme is not conformant."},

  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "\u30db\u30b9\u30c8\u304c\u6b63\u3057\u3044\u5f62\u5f0f\u306e\u30a2\u30c9\u30ec\u30b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093"},
//      "Host is not a well formed address"},

  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;


  {
    ER_PORT_WHEN_HOST_NULL,
       "\u30db\u30b9\u30c8\u304c null \u306e\u3068\u304d\u3001\u30dd\u30fc\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Port cannot be set when host is null"},

  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;


  {
    ER_INVALID_PORT,
       "\u7121\u52b9\u306a\u30dd\u30fc\u30c8\u756a\u53f7"},
//       "Invalid port number"},

  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


  {
    ER_FRAG_FOR_GENERIC_URI,
       "\u6c4e\u7528 URI \u306b\u5bfe\u3057\u3066\u306e\u307f\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u3059"},
//       "Fragment can only be set for a generic URI"},

  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


  {
    ER_FRAG_WHEN_PATH_NULL,
       "\u30d1\u30b9\u304c null \u306e\u3068\u304d\u3001\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Fragment cannot be set when path is null"},

  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;


  {
    ER_FRAG_INVALID_CHAR,
       "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059"},
//       "Fragment contains invalid character"},

  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;


  {
    ER_PARSER_IN_USE,
        "\u30d1\u30fc\u30b5\u306f\u3059\u3067\u306b\u4f7f\u308f\u308c\u3066\u3044\u307e\u3059"},
//        "Parser is already in use"},

  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "\u69cb\u6587\u89e3\u6790\u4e2d\u3001{0} {1} \u3092\u5909\u66f4\u3067\u304d\u307e\u305b\u3093"},
//        "Cannot change {0} {1} while parsing"},

  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "\u81ea\u8eab\u304c\u539f\u56e0\u3068\u306a\u3063\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//        "Self-causation not permitted"},

  
   /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;


  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
         "{0} \u306b\u3042\u308b\u5916\u90e8\u30b9\u30af\u30ea\u30d7\u30c8\u3092\u5165\u624b\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
//         "Could not get to external script at {0}"},

  
  /** The resource [] could not be found     */
  //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;


  {
    ER_RESOURCE_COULD_NOT_FIND,
        "\u30ea\u30bd\u30fc\u30b9 [ {0} ] \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f.\n {1}"},
//        "The resource [ {0} ] could not be found.\n {1}"},

  
   /** output property not recognized:  */
  //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;


  {
    ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "\u51fa\u529b\u30d7\u30ed\u30d1\u30c6\u30a3\u304c\u8a8d\u3081\u3089\u308c\u307e\u305b\u3093: {0}"},
//        "Output property not recognized: {0}"},

  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


  {
    ER_NO_USERINFO_IF_NO_HOST,
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Userinfo \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Userinfo may not be specified if host is not specified"},

  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;


  {
    ER_NO_PORT_IF_NO_HOST,
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Port \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Port may not be specified if host is not specified"},

  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


  {
    ER_NO_QUERY_STRING_IN_PATH,
        "\u30d1\u30b9\u304a\u3088\u3073\u7167\u4f1a\u6587\u5b57\u5217\u3067 Query \u6587\u5b57\u5217\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Query string cannot be specified in path and query string"},

  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "\u30d1\u30b9\u304a\u3088\u3073\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306e\u4e21\u65b9\u3067\u3001Fragment \u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Fragment cannot be specified in both the path and fragment"},

  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "\u7a7a\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3092\u4f7f\u3063\u3066 URI \u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093"},
//        "Cannot initialize URI with empty parameters"},

  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;


  {
    ER_FAILED_CREATING_ELEMLITRSLT,
        "ElemLiteralResult \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u306e\u4f5c\u6210\u306b\u5931\u6557\u3057\u307e\u3057\u305f"},
//        "Failed creating ElemLiteralResult instance"},
    
  
  // Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed. - nb.
   /** Priority value does not contain a parsable number   */
  //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;


  {
    ER_VALUE_SHOULD_BE_NUMBER,
        "{0} \u306e\u5024\u306b\u89e3\u6790\u53ef\u80fd\u306a\u6570\u5b57\u304c\u542b\u307e\u308c\u3066\u3044\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059"},
//        "Value for {0} should contain a parsable number"},

  
   /**  Value for {0} should equal 'yes' or 'no'   */
  //public static final int ER_VALUE_SHOULD_EQUAL = 205;


  {
    ER_VALUE_SHOULD_EQUAL,
        " {0} \u306e\u5024\u306f yes \u307e\u305f\u306f no \u306e\u3044\u305a\u308c\u304b\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//        " Value for {0} should equal yes or no"},

 
   /**  Failed calling {0} method   */
  //public static final int ER_FAILED_CALLING_METHOD = 206;


  {
    ER_FAILED_CALLING_METHOD,
        " {0} \u30e1\u30bd\u30c3\u30c9\u306e\u547c\u3073\u51fa\u3057\u306b\u5931\u6557\u3057\u307e\u3057\u305f"},
//        " Failed calling {0} method"},

  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;


  {
    ER_FAILED_CREATING_ELEMTMPL,
        "ElemTemplateElement \u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u306e\u4f5c\u6210\u306b\u5931\u6557\u3057\u307e\u3057\u305f"},
//        "Failed creating ElemTemplateElement instance"},

  
   /**  Characters are not allowed at this point in the document   */
  //public static final int ER_CHARS_NOT_ALLOWED = 208;


  {
    ER_CHARS_NOT_ALLOWED,
        "\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306e\u3053\u306e\u90e8\u5206\u3067\u3001\u6587\u5b57\u5217\u306f\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093"},
//        "Characters are not allowed at this point in the document"},

  
  /**  attribute is not allowed on the element   */
  //public static final int ER_ATTR_NOT_ALLOWED = 209;


  {
    ER_ATTR_NOT_ALLOWED,
        "\"{0}\" \u5c5e\u6027\u306f {1} \u8981\u7d20\u3067\u8a31\u53ef\u3055\u308c\u307e\u305b\u3093\u3002"},
//        "\"{0}\" attribute is not allowed on the {1} element!"},

  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;


  {
    ER_METHOD_NOT_SUPPORTED,
        "\u30e1\u30bd\u30c3\u30c9\u306f\u307e\u3060\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//        "Method not yet supported "},

 
  /**  Bad value    */
  //public static final int ER_BAD_VALUE = 211;


  {
    ER_BAD_VALUE,
     "{0} \u8aa4\u3063\u305f\u5024 {1} "},
//     "{0} bad value {1} "},

  
  /**  attribute value not found   */
  //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;


  {
    ER_ATTRIB_VALUE_NOT_FOUND,
     "{0} \u5c5e\u6027\u5024\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},
//     "{0} attribute value not found "},

  
  /**  attribute value not recognized    */
  //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;


  {
    ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "{0} \u5c5e\u6027\u5024\u304c\u8a8d\u3081\u3089\u308c\u307e\u305b\u3093"},
//     "{0} attribute value not recognized "},


  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter \u306f\u73fe\u5728\u518d\u8d77\u52d5\u3067\u304d\u307e\u305b\u3093"},
//     "IncrementalSAXSource_Filter not currently restartable"},

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader \u306f startParse \u8981\u6c42\u3088\u308a\u524d\u306b\u914d\u7f6e\u3067\u304d\u307e\u305b\u3093"},
//     "XMLReader not before startParse request"},

  
    /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;


  {
    ER_NULL_URI_NAMESPACE,
     "null URI \u3092\u4f7f\u3063\u3066\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e\u3092\u751f\u6210\u3057\u3088\u3046\u3068\u3057\u3066\u3044\u307e\u3059"},
      

  // Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)
  
  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NUMBER_TOO_BIG = 217;


  {
    ER_NUMBER_TOO_BIG,
     "Long \u578b\u6574\u6570\u306e\u6700\u5927\u5024\u3092\u8d85\u3048\u308b\u6570\u5b57\u3092\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u3057\u3088\u3046\u3068\u3057\u3066\u3044\u307e\u3059"},
//     "Attempting to format a number bigger than the largest Long integer"},


//ER_CANNOT_FIND_SAX1_DRIVER

  //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;


  {
    ER_CANNOT_FIND_SAX1_DRIVER,
     "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},
//     "Cannot find SAX1 driver class {0}"},


//ER_SAX1_DRIVER_NOT_LOADED
  //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;


  {
    ER_SAX1_DRIVER_NOT_LOADED,
     "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093"},
//     "SAX1 driver class {0} found but cannot be loaded"},


//ER_SAX1_DRIVER_NOT_INSTANTIATED
  //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;


  {
    ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c\u30ed\u30fc\u30c9\u3055\u308c\u307e\u3057\u305f\u304c\u3001\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u5316\u3067\u304d\u307e\u305b\u3093"},
//     "SAX1 driver class {0} loaded but cannot be instantiated"},



// ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
  //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;


  {
    ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "SAX1 \u30c9\u30e9\u30a4\u30d0\u30af\u30e9\u30b9 {0} \u304c org.xml.sax.Parser \u3092\u5b9f\u88c5\u3057\u3066\u3044\u307e\u305b\u3093"},
//     "SAX1 driver class {0} does not implement org.xml.sax.Parser"},


// ER_PARSER_PROPERTY_NOT_SPECIFIED
  //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;


  {
    ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "\u30b7\u30b9\u30c6\u30e0\u30d7\u30ed\u30d1\u30c6\u30a3 org.xml.sax.parser \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//     "System property org.xml.sax.parser not specified"},


//ER_PARSER_ARG_CANNOT_BE_NULL
  //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;


  {
    ER_PARSER_ARG_CANNOT_BE_NULL,
     "\u30d1\u30fc\u30b5\u5c5e\u6027\u306f null \u306b\u3067\u304d\u307e\u305b\u3093"},
//     "Parser argument must not be null"},



// ER_FEATURE
  //public static final int  ER_FEATURE = 224;


  {
    ER_FEATURE,
     "\u6a5f\u80fd: {0}"},
//     "Feature:a {0}"},



// ER_PROPERTY
  //public static final int ER_PROPERTY = 225 ;


  {
    ER_PROPERTY,
     "\u30d7\u30ed\u30d1\u30c6\u30a3: {0}"},
//     "Property:a {0}"},


// ER_NULL_ENTITY_RESOLVER
  //public static final int ER_NULL_ENTITY_RESOLVER  = 226;


  {
    ER_NULL_ENTITY_RESOLVER,
     "null \u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u30ea\u30be\u30eb\u30d0"},
//     "Null entity resolver"},


// ER_NULL_DTD_HANDLER
  //public static final int  ER_NULL_DTD_HANDLER = 227 ;


  {
    ER_NULL_DTD_HANDLER,
     "null DTD \u30cf\u30f3\u30c9\u30e9"},
//     "Null DTD handler"},


// No Driver Name Specified!
  //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;

  {
    ER_NO_DRIVER_NAME_SPECIFIED,
     "\u30c9\u30e9\u30a4\u30d0\u540d\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093!"},
//     "No Driver Name Specified!"},



// No URL Specified!
  //public static final int ER_NO_URL_SPECIFIED = 229;

  {
    ER_NO_URL_SPECIFIED,
     "URL \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093!"},
//     "No URL Specified!"},



// Pool size is less than 1!
  //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;

  {
    ER_POOLSIZE_LESS_THAN_ONE,
     "\u30d7\u30fc\u30eb\u30b5\u30a4\u30ba\u304c 1 \u3088\u308a\u5c0f\u3055\u3044\u5024\u306b\u306a\u3063\u3066\u3044\u307e\u3059!"},
//     "Pool size is less than 1!"},



// Invalid Driver Name Specified!
  //public static final int ER_INVALID_DRIVER_NAME = 231;

  {
    ER_INVALID_DRIVER_NAME,
     "\u7121\u52b9\u306a\u30c9\u30e9\u30a4\u30d0\u540d\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059!"},
//     "Invalid Driver Name Specified!"},




// ErrorListener
  //public static final int ER_ERRORLISTENER = 232;

  {
    ER_ERRORLISTENER,
     "\u30a8\u30e9\u30fc\u30ea\u30b9\u30ca\u30fc"},
//     "ErrorListener"},



// Programmer's error! expr has no ElemTemplateElement parent!
  //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;

  {
    ER_ASSERT_NO_TEMPLATE_PARENT,
     "\u30d7\u30ed\u30b0\u30e9\u30de\u30a8\u30e9\u30fc! \u5f0f\u306b ElemTemplateElement \u306e\u89aa\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u305b\u3093!"},
//     "Programmer's error! expr has no ElemTemplateElement parent!"},



// Programmer's assertion in RundundentExprEliminator: {0}
  //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;

  {
    ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "RundundentExprEliminator \u306b\u304a\u3051\u308b\u30d7\u30ed\u30b0\u30e9\u30de\u306e\u8868\u660e: {0}"},
//     "Programmer's assertion in RundundentExprEliminator: {0}"},


// Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;

  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//     "Axis traverser not supported: {0}"},


// ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;

   {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "ListingErrorHandler \u306e\u4f5c\u6210\u6642\u306b null PrintWriter \u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f!"},
//     "ListingErrorHandler created with null PrintWriter!"},


  // {0}is not allowed in this position in the stylesheet!
  //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;

  {
    ER_NOT_ALLOWED_IN_POSITION,
     "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u3053\u306e\u4f4d\u7f6e\u3067\u306f\u3001{0} \u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093!"},
//     "{0} is not allowed in this position in the stylesheet!"},


  // Non-whitespace text is not allowed in this position in the stylesheet!
  //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;

  {
    ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u3053\u306e\u4f4d\u7f6e\u3067\u306f\u3001\u7a7a\u767d\u985e\u4ee5\u5916\u306e\u30c6\u30ad\u30b9\u30c8\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093!"},
//     "Non-whitespace text is not allowed in this position in the stylesheet!"},


  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  //public static final int INVALID_TCHAR = 239;
  // SystemId Unknown

  {
    INVALID_TCHAR,
     "CHAR \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a\u5024 {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059\u3002CHAR \u578b\u306e\u5c5e\u6027\u306b\u6307\u5b9a\u3067\u304d\u308b\u306e\u306f\u3001\u5358\u4e00\u306e\u6587\u5b57\u3060\u3051\u3067\u3059!"},
//     "Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!"},


  //public static final int ER_SYSTEMID_UNKNOWN = 240;

  {
    ER_SYSTEMID_UNKNOWN,
     "\u30b7\u30b9\u30c6\u30e0 ID \u304c\u4e0d\u660e\u3067\u3059"},
//     "SystemId Unknown"},


  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;

  {
    ER_LOCATION_UNKNOWN,
     "\u30a8\u30e9\u30fc\u306e\u5834\u6240\u304c\u4e0d\u660e\u3067\u3059"},
//     "Location of error unknown"},


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
     "QNAME \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a\u5024 {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059"},
//     "Illegal value:a {1} used for QNAME attribute:a {0}"},


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
     "ENUM \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a\u5024 {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059\u3002\u6709\u52b9\u306a\u5024\u306f {2} \u3067\u3059\u3002"},
//     "Illegal value:a {1} used for ENUM attribute:a {0}.  Valid values are:a {2}."},


// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NMTOKEN

  // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
  //public static final int INVALID_NMTOKEN = 244;

  {
    INVALID_NMTOKEN,
     "NMTOKEN \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a\u5024 {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059"},
//     "Illegal value:a {1} used for NMTOKEN attribute:a {0} "},


// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NCNAME

  // Illegal value:a {1} used for NCNAME attribute:a {0}.
  //public static final int INVALID_NCNAME = 245;

  {
    INVALID_NCNAME,
     "NCNAME \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059"},
//     "Illegal value:a {1} used for NCNAME attribute:a {0} "},


// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_BOOLEAN

  // Illegal value:a {1} used for boolean attribute:a {0}.
  //public static final int INVALID_BOOLEAN = 246;


  {
    INVALID_BOOLEAN,
     "boolean \u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059"},
//     "Illegal value:a {1} used for boolean attribute:a {0} "},


// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NUMBER

  // Illegal value:a {1} used for number attribute:a {0}.
  //public static final int INVALID_NUMBER = 247;

  {
    INVALID_NUMBER,
     "\u6570\u5b57\u578b\u306e\u5c5e\u6027 {0} \u306b\u4e0d\u6b63\u306a {1} \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059"},
//     "Illegal value:a {1} used for number attribute:a {0} "},



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
     "\u30de\u30c3\u30c1\u30d1\u30bf\u30fc\u30f3\u5185\u306b\u542b\u307e\u308c\u308b {0} \u306e\u5f15\u6570\u306b\u306f\u3001\u30ea\u30c6\u30e9\u30eb\u3092\u6307\u5b9a\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002"},
//     "Argument to {0} in match pattern must be a literal."},


// Note to translators:  The following message indicates that two definitions of
// a variable.  A "global variable" is a variable that is accessible everywher
// in the stylesheet.
// ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790

  // Duplicate global variable declaration.
  //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;

  {
    ER_DUPLICATE_GLOBAL_VAR,
     "\u30b0\u30ed\u30fc\u30d0\u30eb\u5909\u6570\u306e\u5ba3\u8a00\u304c\u91cd\u8907\u3057\u3066\u3044\u307e\u3059\u3002"},
//     "Duplicate global variable declaration."},



// Note to translators:  The following message indicates that two definitions of
// a variable were encountered.
// ER_DUPLICATE_VAR - new error message for bugzilla #790

  // Duplicate variable declaration.
  //public static final int ER_DUPLICATE_VAR           = 250;

  {
    ER_DUPLICATE_VAR,
     "\u5909\u6570\u306e\u5ba3\u8a00\u304c\u91cd\u8907\u3057\u3066\u3044\u307e\u3059\u3002"},
//     "Duplicate variable declaration."},


    // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
    // which must not be translated.
    // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789

  // xsl:template must have a name or match attribute (or both)
  //public static final int ER_TEMPLATE_NAME_MATCH     = 251;

  {
    ER_TEMPLATE_NAME_MATCH,
     "xsl:template \u306b\u306f\u3001name \u5c5e\u6027\u3001match \u5c5e\u6027\u306e\u3044\u305a\u308c\u304b\u307e\u305f\u306f\u4e21\u65b9\u304c\u542b\u307e\u308c\u3066\u3044\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059"},
//     "xsl:template must have a name or match attribute (or both)"},


    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788

  // Prefix in exclude-result-prefixes is not valid:a {0}
  //public static final int ER_INVALID_PREFIX          = 252;

  {
    ER_INVALID_PREFIX,
     "exclude-result-prefixes \u306e\u63a5\u982d\u8f9e\u304c\u6709\u52b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093: {0}"},
//     "Prefix in exclude-result-prefixes is not valid:a {0}"},


    // Note to translators:  An "attribute set" is a set of attributes that can be
    // added to an element in the output document as a group.  The message indicates
    // that there was a reference to an attribute set named {0} that was never
    // defined.
    // ER_NO_ATTRIB_SET - new error message for bugzilla #782

  // attribute-set named {0} does not exist
  //public static final int ER_NO_ATTRIB_SET           = 253;

  {
    ER_NO_ATTRIB_SET,
     "{0} \u3068\u3044\u3046\u540d\u524d\u306e attribute-set \u304c\u5b58\u5728\u3057\u307e\u305b\u3093"},
//     "attribute-set named {0} does not exist"},


  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  //public static final int WG_FOUND_CURLYBRACE = 1;


  {
    WG_FOUND_CURLYBRACE,
      "'}' \u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u304c\u3001\u5c5e\u6027\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u958b\u304b\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Found '}' but no attribute template open!"},


  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;


  {
    WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "\u8b66\u544a: count \u5c5e\u6027\u304c xsl:number \u5185\u306e\u7956\u5148\u3068\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002 \u30bf\u30fc\u30b2\u30c3\u30c8 = {0}"},
//      "Warning: count attribute does not match an ancestor in xsl:number! Target = {0}"},


  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;


  {
    WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "\u53e4\u3044\u69cb\u6587: 'expr' \u5c5e\u6027\u306e\u540d\u524d\u306f 'select' \u306b\u5909\u66f4\u3055\u308c\u3066\u3044\u307e\u3059\u3002"},
//      "Old syntax: The name of the 'expr' attribute has been changed to 'select'."},


  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;


  {
    WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan \u306f format-number \u95a2\u6570\u5185\u306e\u30ed\u30b1\u30fc\u30eb\u540d\u3092\u307e\u3060\u51e6\u7406\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Xalan doesn't yet handle the locale name in the format-number function."},


  /** WG_LOCALE_NOT_FOUND          */
  //public static final int WG_LOCALE_NOT_FOUND = 5;


  {
    WG_LOCALE_NOT_FOUND,
      "\u8b66\u544a: xml:lang={0} \u306e\u30ed\u30b1\u30fc\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//      "Warning: Could not find locale for xml:lang={0}"},


  /** WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 6;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "URL \u3092\u4f5c\u6210\u3067\u304d\u307e\u305b\u3093: {0}"},
//      "Can not make URL from: {0}"},


  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;


  {
    WG_CANNOT_LOAD_REQUESTED_DOC,
      "\u8981\u6c42\u3055\u308c\u305f\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u30ed\u30fc\u30c9\u3067\u304d\u307e\u305b\u3093: {0}"},
//      "Can not load requested doc: {0}"},


  /** WG_CANNOT_FIND_COLLATOR          */
  //public static final int WG_CANNOT_FIND_COLLATOR = 8;


  {
    WG_CANNOT_FIND_COLLATOR,
      "<sort xml:lang={0} \u306e Collator \u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f"},
//      "Could not find Collator for <sort xml:lang={0}"},


  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;


  {
    WG_FUNCTIONS_SHOULD_USE_URL,
      "\u53e4\u3044\u69cb\u6587: \u95a2\u6570\u306e\u6307\u4ee4\u306f {0} \u306e URL \u3092\u4f7f\u7528\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059"},
//      "Old syntax: the functions instruction should use a url of {0}"},


  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;


  {
    WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u304c\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}\u3001UTF-8 \u3092\u4f7f\u7528"},
//      "encoding not supported: {0}, using UTF-8"},


  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;


  {
    WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "\u30a8\u30f3\u30b3\u30fc\u30c7\u30a3\u30f3\u30b0\u304c\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093: {0}\u3001Java {1} \u3092\u4f7f\u7528"},
//      "encoding not supported: {0}, using Java {1}"},


  /** WG_SPECIFICITY_CONFLICTS          */
  //public static final int WG_SPECIFICITY_CONFLICTS = 12;


  {
    WG_SPECIFICITY_CONFLICTS,
      "\u7279\u5b9a\u3067\u3042\u308b\u3079\u304d\u3082\u306e\u306e\u7af6\u5408\u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f: {0} \u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u3067\u6700\u5f8c\u306b\u898b\u3064\u304b\u3063\u305f\u3082\u306e\u304c\u4f7f\u7528\u3055\u308c\u307e\u3059\u3002"},
//      "Specificity conflicts found: {0} Last found in stylesheet will be used."},


  /** WG_PARSING_AND_PREPARING          */
  //public static final int WG_PARSING_AND_PREPARING = 13;


  {
    WG_PARSING_AND_PREPARING,
      "========= {0} \u306e\u69cb\u6587\u89e3\u6790\u304a\u3088\u3073\u6e96\u5099  =========="},
//      "========= Parsing and preparing {0} =========="},


  /** WG_ATTR_TEMPLATE          */
  //public static final int WG_ATTR_TEMPLATE = 14;


  {
    WG_ATTR_TEMPLATE, "\u5c5e\u6027\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u3001{0}"},
//    WG_ATTR_TEMPLATE, "Attr Template, {0}"},


  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;


  {
    WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "xsl:strip-space \u3068 xsl:preserve-space \u306e\u9593\u3067 match \u7af6\u5408\u3057\u307e\u3059"},
//      "Match conflict between xsl:strip-space and xsl:preserve-space"},


  /** WG_ATTRIB_NOT_HANDLED          */
  //public static final int WG_ATTRIB_NOT_HANDLED = 16;


  {
    WG_ATTRIB_NOT_HANDLED,
      "Xalan \u306f\u307e\u3060 {0} \u5c5e\u6027\u3092\u51e6\u7406\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Xalan does not yet handle the {0} attribute!"},


  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;


  {
    WG_NO_DECIMALFORMAT_DECLARATION,
      "10 \u9032\u6570\u5f62\u5f0f\u306e\u5ba3\u8a00\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: {0}"},
//      "No declaration found for decimal format: {0}"},


  /** WG_OLD_XSLT_NS          */
  //public static final int WG_OLD_XSLT_NS = 18;


  {
    WG_OLD_XSLT_NS, "XSLT \u540d\u524d\u7a7a\u9593\u304c\u306a\u3044\u3001\u307e\u305f\u306f\u4e0d\u6b63\u3067\u3059\u3002"},
//    WG_OLD_XSLT_NS, "Missing or incorrect XSLT Namespace. "},


  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;


  {
    WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "\u30c7\u30d5\u30a9\u30eb\u30c8\u306e xsl:decimal-format \u5ba3\u8a00\u306f 1 \u3064\u3060\u3051\u8a31\u53ef\u3055\u308c\u307e\u3059\u3002"},
//      "Only one default xsl:decimal-format declaration is allowed."},


  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;


  {
    WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "xsl:decimal-format \u306e\u540d\u524d\u306f\u4e00\u610f\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093\u3002\u540d\u524d \"{0}\" \u306f\u91cd\u8907\u3057\u3066\u3044\u307e\u3059\u3002"},
//      "xsl:decimal-format names must be unique. Name \"{0}\" has been duplicated."},


  /** WG_ILLEGAL_ATTRIBUTE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE = 21;


  {
    WG_ILLEGAL_ATTRIBUTE,
      "{0} \u306b\u4e0d\u5f53\u306a\u5c5e\u6027\u304c\u3042\u308a\u307e\u3059: {1}"},
//      "{0} has an illegal attribute: {1}"},


  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;


  {
    WG_COULD_NOT_RESOLVE_PREFIX,
      "\u540d\u524d\u7a7a\u9593\u306e\u63a5\u982d\u8f9e\u3092\u89e3\u6c7a\u3067\u304d\u307e\u305b\u3093: {0}\u3002 \u30ce\u30fc\u30c9\u306f\u7121\u8996\u3055\u308c\u307e\u3059\u3002"},
//      "Could not resolve namespace prefix: {0}. The node will be ignored."},


  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;


  {
    WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u306f 'version' \u5c5e\u6027\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//      "xsl:stylesheet requires a 'version' attribute!"},


  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;


  {
    WG_ILLEGAL_ATTRIBUTE_NAME,
      "\u4e0d\u5f53\u306a\u5c5e\u6027\u540d: {0}"},
//      "Illegal attribute name: {0}"},


  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;


  {
    WG_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c5e\u6027 {0} \u306b\u4e0d\u5f53\u306a\u5024\u304c\u4f7f\u7528\u3055\u308c\u3066\u3044\u307e\u3059: {1}"},
//      "Illegal value used for attribute {0}: {1}"},


  /** WG_EMPTY_SECOND_ARG          */
  //public static final int WG_EMPTY_SECOND_ARG = 26;


  {
    WG_EMPTY_SECOND_ARG,
      "document \u95a2\u6570\u306e 2 \u756a\u76ee\u306e\u5f15\u6570\u306e\u7d50\u679c\u306e\u30ce\u30fc\u30c9\u30bb\u30c3\u30c8\u304c\u7a7a\u3067\u3059\u3002\u6700\u521d\u306e\u5f15\u6570\u304c\u4f7f\u7528\u3055\u308c\u307e\u3059\u3002"},
//      "Resulting nodeset from second argument of document function is empty. The first agument will be used."},


  // Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML


  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;

  {
     WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "xsl:processing-instruction \u306e 'name' \u5c5e\u6027\u3067\u306f\u3001'xml' \u3092\u5024\u3068\u3057\u3066\u6307\u5b9a\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
//      "The value of the 'name' attribute of xsl:processing-instruction name must not be 'xml'"},


    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME

  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;

  {
     WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "xsl:processing-instruction \u306e ''name'' \u5c5e\u6027\u306b\u306f\u3001\u6709\u52b9\u306a NCName \u5024 {0} \u3092\u6307\u5b9a\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059"},
//      "The value of the 'name' attribute of xsl:processing-instruction must be a valid NCName:a {0}"},


    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    // WG_ILLEGAL_ATTRIBUTE_POSITION

  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
  //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;

  {
    WG_ILLEGAL_ATTRIBUTE_POSITION,
      "\u5b50\u30ce\u30fc\u30c9\u306e\u751f\u6210\u5f8c\u3084\u8981\u7d20\u306e\u751f\u6210\u524d\u306b\u5c5e\u6027 {0} \u3092\u8981\u7d20\u306b\u8ffd\u52a0\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093\u3002\u305d\u306e\u5c5e\u6027\u306f\u7121\u8996\u3055\u308c\u307e\u3059\u3002"},
//      "Cannot add attribute {0} after child nodes or before an element is produced.  Attribute will be ignored."},


    // WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?


  // Other miscellaneous text used inside the code...
  { "ui_language", "ja"},
  { "help_language", "ja"},
  { "language", "ja"},
    { "BAD_CODE",
      "createMessage \u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u304c\u7bc4\u56f2\u5916\u3067\u3057\u305f"},
    { "FORMAT_FAILED",
      "messageFormat \u547c\u3073\u51fa\u3057\u3067\u4f8b\u5916\u304c\u30b9\u30ed\u30fc\u3055\u308c\u307e\u3057\u305f"},
    { "version", ">>>>>>> Xalan \u30d0\u30fc\u30b8\u30e7\u30f3 "},
    { "version2", "<<<<<<<"},
    { "yes", "\u306f\u3044"},
    { "line", "\u884c\u756a\u53f7"},
    { "column", "\u5217\u756a\u53f7"},
    { "xsldone", "XSLProcessor: \u7d42\u4e86"},
    { "xslProc_option",
    "Xalan-J \u30b3\u30de\u30f3\u30c9\u884c\u30d7\u30ed\u30bb\u30b9\u306e\u30af\u30e9\u30b9\u30aa\u30d7\u30b7\u30e7\u30f3:"},
  { "xslProc_invalid_xsltc_option", "\u30aa\u30d7\u30b7\u30e7\u30f3 {0} \u306f\u3001XSLTC \u30e2\u30fc\u30c9\u3067\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
  { "xslProc_invalid_xalan_option", "\u30aa\u30d7\u30b7\u30e7\u30f3 {0} \u306f\u3001-XSLTC \u3067\u306e\u307f\u4f7f\u7528\u3067\u304d\u307e\u3059\u3002"},
  { "xslProc_no_input", "\u30a8\u30e9\u30fc: \u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u307e\u305f\u306f\u5165\u529b XML \u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u4f7f\u7528\u6cd5\u306e\u8aac\u660e\u306b\u3042\u308b\u30aa\u30d7\u30b7\u30e7\u30f3\u3092\u6307\u5b9a\u3057\u306a\u3044\u3067\u3053\u306e\u30b3\u30de\u30f3\u30c9\u3092\u5b9f\u884c\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
  { "xslProc_common_options", "-\u5171\u901a\u306e\u30aa\u30d7\u30b7\u30e7\u30f3-"},
  { "xslProc_xalan_options", "-Xalan \u306e\u30aa\u30d7\u30b7\u30e7\u30f3-"},
  { "xslProc_xsltc_options", "-XSLTC \u306e\u30aa\u30d7\u30b7\u30e7\u30f3-"},
  { "xslProc_return_to_continue", "(\u7d99\u7d9a\u3059\u308b\u306b\u306f Return \u30ad\u30fc\u3092\u62bc\u3057\u3066\u304f\u3060\u3055\u3044)"},

   // Note to translators: The option name and the parameter name do not need to
   // be translated. Only translate the messages in parentheses.  Note also that
   // leading whitespace in the messages is used to indent the usage information
   // for each option in the English messages.
   // Do not translate the keywords: XSLTC, SAX, DOM and DTM.
  { "optionXSLTC", "   [-XSLTC (\u5909\u63db\u306b\u306f XSLTC \u3092\u4f7f\u7528\u3059\u308b)]"},
    { "optionIN", "    -IN inputXMLURL"},
    { "optionXSL", "   [-XSL XSLTransformationURL]"},
    { "optionOUT", "   [-OUT outputFileName]"},
    { "optionLXCIN",
      "   [-LXCIN compiledStylesheetFileNameIn]"},
    { "optionLXCOUT",
      "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
    { "optionPARSER",
      "   [-PARSER \u306f parser liaison \u306e\u30af\u30e9\u30b9\u540d\u3092\u5b8c\u5168\u4fee\u98fe\u3059\u308b]"},
    { "optionE",
    "   [-E (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u53c2\u7167\u3092\u62e1\u5f35\u3057\u306a\u3044)]"},
    { "optionV",
    "   [-E (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u53c2\u7167\u3092\u62e1\u5f35\u3057\u306a\u3044)]"},
    { "optionQC", "   [-QC (Quiet Pattern Conflicts Warnings)]"},
    { "optionQ", "   [-Q  (\u975e\u51fa\u529b\u30e2\u30fc\u30c9)]"},
    { "optionLF",
      "   [-LF (\u51fa\u529b\u306b\u306e\u307f\u6539\u884c\u3092\u4f7f\u7528\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f CR/LF})]"},
    { "optionCR",
      "   [-CR (\u51fa\u529b\u306b\u306e\u307f\u30ad\u30e3\u30ea\u30c3\u30b8\u30ea\u30bf\u30fc\u30f3\u3092\u4f7f\u7528\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f CR/LF})]"},
    { "optionESCAPE",
      "   [-ESCAPE (\u30a8\u30b9\u30b1\u30fc\u30d7\u3059\u308b\u6587\u5b57\u5217 {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f <>&\"\'\\r\\n}]"},
    { "optionINDENT",
      "   [-INDENT (\u30a4\u30f3\u30c7\u30f3\u30c8\u306b\u8a2d\u5b9a\u3059\u308b\u7a7a\u767d\u6587\u5b57\u6570\u3092\u5236\u5fa1\u3059\u308b {\u30c7\u30d5\u30a9\u30eb\u30c8\u306f 0})]"},
    { "optionTT",
      "   [-TT (\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u304c\u547c\u3073\u51fa\u3055\u308c\u305f\u3068\u304d\u306b\u30c8\u30ec\u30fc\u30b9\u3059\u308b)]"},
    { "optionTG",
      "   [-TG (\u5404\u751f\u6210\u30a4\u30d9\u30f3\u30c8\u3092\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]"},
    { "optionTS",
    "   [-TS (\u5404\u9078\u629e\u30a4\u30d9\u30f3\u30c8\u3092\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]"},
    { "optionTTC",
      "   [-TTC (\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u5b50\u304c\u51e6\u7406\u3055\u308c\u305f\u3068\u304d\u306b\u30c8\u30ec\u30fc\u30b9\u3059\u308b\u3002)]"},
    { "optionTCLASS",
      "   [-TCLASS (\u30c8\u30ec\u30fc\u30b9\u62e1\u5f35\u7528\u306e TraceListener \u30af\u30e9\u30b9\u3002)]"},
    { "optionVALIDATE",
      "   [-VALIDATE (\u59a5\u5f53\u6027\u691c\u67fb\u3092\u6709\u52b9\u306b\u3059\u308b\u304b\u3069\u3046\u304b\u3092\u8a2d\u5b9a\u3059\u308b\u3002\u30c7\u30d5\u30a9\u30eb\u30c8\u3067\u306f\u7121\u52b9\u3002)]"},
    { "optionEDUMP",
      "   [-EDUMP {\u30aa\u30d7\u30b7\u30e7\u30f3\u306e\u30d5\u30a1\u30a4\u30eb\u540d} (\u30a8\u30e9\u30fc\u767a\u751f\u6642\u306b\u30b9\u30bf\u30c3\u30af\u30c0\u30f3\u30d7\u3092\u5b9f\u884c\u3059\u308b\u3002)]"},
    { "optionXML",
      "   [-XML (XML \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3057\u3066\u3001XML \u30d8\u30c3\u30c0\u3092\u8ffd\u52a0\u3059\u308b\u3002)]"},
    { "optionTEXT",
      "   [-TEXT (\u5358\u7d14\u306a Text \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3059\u308b\u3002)]"},
    { "optionHTML",
    "   [-HTML (HTML \u30d5\u30a9\u30fc\u30de\u30c3\u30bf\u3092\u4f7f\u7528\u3059\u308b\u3002)]"},
    { "optionPARAM",
      "   [-PARAM \u540d\u524d\u5f0f (\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3092\u8a2d\u5b9a)]"},
    { "noParsermsg1",
    "XSL \u30d7\u30ed\u30bb\u30b9\u306f\u6210\u529f\u3057\u307e\u305b\u3093\u3067\u3057\u305f\u3002"},
    { "noParsermsg2",
    "** \u30d1\u30fc\u30b5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f **"},
    { "noParsermsg3",
    "\u30af\u30e9\u30b9\u30d1\u30b9\u3092\u30c1\u30a7\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044\u3002"},
    { "noParsermsg4",
      "Java \u7528\u306b IBM \u306e XML \u30d1\u30fc\u30b5\u304c\u306a\u3044\u5834\u5408\u3001\u4ee5\u4e0b\u304b\u3089\u30c0\u30a6\u30f3\u30ed\u30fc\u30c9\u3067\u304d\u307e\u3059"},
    { "noParsermsg5",
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
    { "optionURIRESOLVER",
    "   [-URIRESOLVER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (URI \u3092\u89e3\u6c7a\u3059\u308b\u5834\u5408\u306f URIResolver \u3092\u4f7f\u7528\u3059\u308b)]"},
    { "optionENTITYRESOLVER",
    "   [-ENTITYRESOLVER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (\u30a8\u30f3\u30c6\u30a3\u30c6\u30a3\u3092\u89e3\u6c7a\u3059\u308b\u5834\u5408\u306f EntityResolver \u3092\u4f7f\u7528\u3059\u308b)]"},
    { "optionCONTENTHANDLER",
    "   [-CONTENTHANDLER \u30d5\u30eb\u30af\u30e9\u30b9\u540d (\u51fa\u529b\u3092\u76f4\u5217\u5316\u3059\u308b\u5834\u5408\u306f ContentHandler \u3092\u4f7f\u7528\u3059\u308b)]"},
    { "optionLINENUMBERS",
    "   [-L \u30bd\u30fc\u30b9\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306b\u306f\u884c\u756a\u53f7\u3092\u4f7f\u7528\u3059\u308b]"},
		
    // Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


    { "optionMEDIA",
    " [-MEDIA mediaType (media \u5c5e\u6027\u3092\u4f7f\u7528\u3057\u3066\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u306b\u95a2\u9023\u4ed8\u3051\u3089\u308c\u305f\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u3092\u691c\u7d22\u3059\u308b\u3002)]"},
    { "optionFLAVOR",
    " [-FLAVOR flavorName (s2s=SAX \u307e\u305f\u306f d2d=DOM \u3092\u660e\u793a\u7684\u306b\u4f7f\u7528\u3057\u3066\u30c8\u30e9\u30f3\u30b9\u30d5\u30a9\u30fc\u30e0\u3092\u5b9f\u884c\u3059\u308b\u3002)] "}, // Added by sboag/scurcuru; experimental
    { "optionDIAG",
    " [-DIAG (\u30c8\u30e9\u30f3\u30b9\u30d5\u30a9\u30fc\u30e0\u306b\u304b\u304b\u3063\u305f\u5408\u8a08\u6642\u9593 (\u30df\u30ea\u79d2) \u3092\u51fa\u529b\u3059\u308b\u3002)]"},
    { "optionINCREMENTAL",
    " [-INCREMENTAL (http://xml.apache.org/xalan/features/incremental \u3092 true \u306b\u8a2d\u5b9a\u3059\u308b\u3053\u3068\u3067\u3001\u30a4\u30f3\u30af\u30ea\u30e1\u30f3\u30bf\u30eb\u306a DTM \u69cb\u7bc9\u3092\u8981\u6c42\u3059\u308b\u3002)]"},
    { "optionNOOPTIMIMIZE",
    " [-NOOPTIMIMIZE (http://xml.apache.org/xalan/features/optimize \u3092 false \u306b\u8a2d\u5b9a\u3059\u308b\u3053\u3068\u3067\u3001\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u6700\u9069\u5316\u51e6\u7406\u3092\u7121\u52b9\u306b\u3059\u308b\u3002)]"},
    { "optionRL",
    " [-RL recursionlimit (\u30b9\u30bf\u30a4\u30eb\u30b7\u30fc\u30c8\u306e\u56de\u5e30\u306e\u6df1\u3055\u306b\u4e0a\u9650\u5024\u3092\u8a2d\u5b9a\u3059\u308b\u3002)]"},
    { "optionXO",
    " [-XO [transletName] (\u751f\u6210\u3055\u308c\u305f\u30c8\u30e9\u30f3\u30b9\u30ec\u30c3\u30c8\u306e\u540d\u524d\u3092\u6307\u5b9a\u3059\u308b)]"},
    { "optionXD",
    " [-XD destinationDirectory (\u30c8\u30e9\u30f3\u30b9\u30ec\u30c3\u30c8\u306e\u51fa\u529b\u5148\u30c7\u30a3\u30ec\u30af\u30c8\u30ea\u3092\u6307\u5b9a\u3059\u308b)]"},
    { "optionXJ",
    " [-XJ jarfile (\u30c8\u30e9\u30f3\u30b9\u30ec\u30c3\u30c8\u306e\u30af\u30e9\u30b9\u3092 <jarfile> \u306b\u6307\u5b9a\u3055\u308c\u305f\u540d\u524d\u306e JAR \u30d5\u30a1\u30a4\u30eb\u306b\u30d1\u30c3\u30b1\u30fc\u30b8\u5316\u3059\u308b)]"},
    { "optionXP",
    " [-XP package (\u751f\u6210\u3055\u308c\u305f\u3059\u3079\u3066\u306e\u30c8\u30e9\u30f3\u30b9\u30ec\u30c3\u30c8\u30af\u30e9\u30b9\u306b\u5bfe\u3059\u308b\u30d1\u30c3\u30b1\u30fc\u30b8\u540d\u306e\u63a5\u982d\u8f9e\u3092\u6307\u5b9a\u3059\u308b)]"},

  { "optionXN",  "   [-XN (\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u30a4\u30f3\u30e9\u30a4\u30f3\u5316\u3092\u6709\u52b9)]" },
  { "optionXX",  "   [-XX (\u305d\u306e\u4ed6\u306e\u30c7\u30d0\u30c3\u30b0\u30e1\u30c3\u30bb\u30fc\u30b8\u306e\u51fa\u529b\u3092\u6709\u52b9)]"},
  { "optionXT" , "   [-XT (\u53ef\u80fd\u3067\u3042\u308c\u3070 translet \u3092\u4f7f\u7528\u3057\u3066\u5909\u63db)]"},
  { "diagTiming"," --------- {1} \u306b\u3088\u308b {0} \u306e\u5909\u63db\u306b {2} \u30df\u30ea\u79d2\u304b\u304b\u308a\u307e\u3057\u305f" },
  { "recursionTooDeep","\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8\u306e\u5165\u308c\u5b50\u304c\u6df1\u3059\u304e\u307e\u3059\u3002\u5165\u308c\u5b50 = {0}\u3001\u30c6\u30f3\u30d7\u30ec\u30fc\u30c8 {1} {2}" },
  { "nameIs", "\u540d\u524d: " },
  { "matchPatternIs", "\u30de\u30c3\u30c1\u30f3\u30b0\u30d1\u30bf\u30fc\u30f3: " }

  };

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
