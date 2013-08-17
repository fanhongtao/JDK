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
public class XSLTErrorResources extends XResourceBundleBase
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
      "Error: Can not have '{' within expression";
  }

  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0} has an illegal attribute: {1}";
  }

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  static
  {
    contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] =
      "sourceNode is null in xsl:apply-imports!";
  }

  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;

  static
  {
    contents[ER_CANNOT_ADD][1] = "Can not add {0} to {1}";
  }

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  static
  {
    contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] =
      "sourceNode is null in handleApplyTemplatesInstruction!";
  }

  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;

  static
  {
    contents[ER_NO_NAME_ATTRIB][1] = "{0} must have a name attribute.";
  }

  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  static
  {
    contents[ER_TEMPLATE_NOT_FOUND][1] = "Could not find template named: {0}";
  }

  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  static
  {
    contents[ER_CANT_RESOLVE_NAME_AVT][1] =
      "Could not resolve name AVT in xsl:call-template.";
  }

  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;

  static
  {
    contents[ER_REQUIRES_ATTRIB][1] = "{0} requires attribute: {1}";
  }

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  static
  {
    contents[ER_MUST_HAVE_TEST_ATTRIB][1] =
      "{0} must have a 'test' attribute.";
  }

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  static
  {
    contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] =
      "Bad value on level attribute: {0}";
  }

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] =
      "processing-instruction name can not be 'xml'";
  }

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] =
      "processing-instruction name must be a valid NCName: {0}";
  }

  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;

  static
  {
    contents[ER_NEED_MATCH_ATTRIB][1] =
      "{0} must have a match attribute if it has a mode.";
  }

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  static
  {
    contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] =
      "{0} requires either a name or a match attribute.";
  }

  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  static
  {
    contents[ER_CANT_RESOLVE_NSPREFIX][1] =
      "Can not resolve namespace prefix: {0}";
  }

  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;

  static
  {
    contents[ER_ILLEGAL_VALUE][1] = "xml:space has an illegal value: {0}";
  }

  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;

  static
  {
    contents[ER_NO_OWNERDOC][1] =
      "Child node does not have an owner document!";
  }

  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  static
  {
    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement error: {0}";
  }

  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;

  static
  {
    contents[ER_NULL_CHILD][1] = "Trying to add a null child!";
  }

  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;

  static
  {
    contents[ER_NEED_SELECT_ATTRIB][1] = "{0} requires a select attribute.";
  }

  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;

  static
  {
    contents[ER_NEED_TEST_ATTRIB][1] =
      "xsl:when must have a 'test' attribute.";
  }

  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;

  static
  {
    contents[ER_NEED_NAME_ATTRIB][1] =
      "xsl:with-param must have a 'name' attribute.";
  }

  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  static
  {
    contents[ER_NO_CONTEXT_OWNERDOC][1] =
      "context does not have an owner document!";
  }

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  static
  {
    contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] =
      "Could not create XML TransformerFactory Liaison: {0}";
  }

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  static
  {
    contents[ER_PROCESS_NOT_SUCCESSFUL][1] =
      "Xalan: Process was not successful.";
  }

  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;

  static
  {
    contents[ER_NOT_SUCCESSFUL][1] = "Xalan: was not successful.";
  }

  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  static
  {
    contents[ER_ENCODING_NOT_SUPPORTED][1] = "Encoding not supported: {0}";
  }

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  static
  {
    contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] =
      "Could not create TraceListener: {0}";
  }

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  static
  {
    contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] =
      "xsl:key requires a 'name' attribute!";
  }

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  static
  {
    contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] =
      "xsl:key requires a 'match' attribute!";
  }

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  static
  {
    contents[ER_KEY_REQUIRES_USE_ATTRIB][1] =
      "xsl:key requires a 'use' attribute!";
  }

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  static
  {
    contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] =
      "(StylesheetHandler) {0} requires an 'elements' attribute!";
  }

  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  static
  {
    contents[ER_MISSING_PREFIX_ATTRIB][1] =
      "(StylesheetHandler) {0} attribute 'prefix' is missing";
  }

  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;

  static
  {
    contents[ER_BAD_STYLESHEET_URL][1] = "Stylesheet URL is bad: {0}";
  }

  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;

  static
  {
    contents[ER_FILE_NOT_FOUND][1] = "Stylesheet file was not found: {0}";
  }

  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;

  static
  {
    contents[ER_IOEXCEPTION][1] =
      "Had IO Exception with stylesheet file: {0}";
  }

  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;

  static
  {
    contents[ER_NO_HREF_ATTRIB][1] =
      "(StylesheetHandler) Could not find href attribute for {0}";
  }

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  static
  {
    contents[ER_STYLESHEET_INCLUDES_ITSELF][1] =
      "(StylesheetHandler) {0} is directly or indirectly including itself!";
  }

  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  static
  {
    contents[ER_PROCESSINCLUDE_ERROR][1] =
      "StylesheetHandler.processInclude error, {0}";
  }

  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;

  static
  {
    contents[ER_MISSING_LANG_ATTRIB][1] =
      "(StylesheetHandler) {0} attribute 'lang' is missing";
  }

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  static
  {
    contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] =
      "(StylesheetHandler) misplaced {0} element?? Missing container element 'component'";
  }

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  static
  {
    contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] =
      "Can only output to an Element, DocumentFragment, Document, or PrintWriter.";
  }

  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;

  static
  {
    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process error";
  }

  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;

  static
  {
    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode error: {0}";
  }

  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;

  static
  {
    contents[ER_NO_SELECT_EXPRESSION][1] =
      "Error! Did not find xpath select expression (-select).";
  }

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  static
  {
    contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] =
      "Can not serialize an XSLProcessor!";
  }

  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;

  static
  {
    contents[ER_NO_INPUT_STYLESHEET][1] =
      "Stylesheet input was not specified!";
  }

  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  static
  {
    contents[ER_FAILED_PROCESS_STYLESHEET][1] =
      "Failed to process stylesheet!";
  }

  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;

  static
  {
    contents[ER_COULDNT_PARSE_DOC][1] = "Could not parse {0} document!";
  }

  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  static
  {
    contents[ER_COULDNT_FIND_FRAGMENT][1] = "Could not find fragment: {0}";
  }

  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;

  static
  {
    contents[ER_NODE_NOT_ELEMENT][1] =
      "Node pointed to by fragment identifier was not an element: {0}";
  }

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  static
  {
    contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "for-each must have either a match or name attribute";
  }

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  static
  {
    contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "templates must have either a match or name attribute";
  }

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  static
  {
    contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] =
      "No clone of a document fragment!";
  }

  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;

  static
  {
    contents[ER_CANT_CREATE_ITEM][1] =
      "Can not create item in result tree: {0}";
  }

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VALUE][1] =
      "xml:space in the source XML has an illegal value: {0}";
  }

  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  static
  {
    contents[ER_NO_XSLKEY_DECLARATION][1] =
      "There is no xsl:key declaration for {0}!";
  }

  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;

  static
  {
    contents[ER_CANT_CREATE_URL][1] = "Error! Cannot create url for: {0}";
  }

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  static
  {
    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "xsl:functions is unsupported";
  }

  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;

  static
  {
    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory Error";
  }

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] =
      "(StylesheetHandler) {0} not allowed inside a stylesheet!";
  }

  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  static
  {
    contents[ER_RESULTNS_NOT_SUPPORTED][1] =
      "result-ns no longer supported!  Use xsl:output instead.";
  }

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  static
  {
    contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] =
      "default-space no longer supported!  Use xsl:strip-space or xsl:preserve-space instead.";
  }

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  static
  {
    contents[ER_INDENTRESULT_NOT_SUPPORTED][1] =
      "indent-result no longer supported!  Use xsl:output instead.";
  }

  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;

  static
  {
    contents[ER_ILLEGAL_ATTRIB][1] =
      "(StylesheetHandler) {0} has an illegal attribute: {1}";
  }

  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  static
  {
    contents[ER_UNKNOWN_XSL_ELEM][1] = "Unknown XSL element: {0}";
  }

  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;

  static
  {
    contents[ER_BAD_XSLSORT_USE][1] =
      "(StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each.";
  }

  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;

  static
  {
    contents[ER_MISPLACED_XSLWHEN][1] =
      "(StylesheetHandler) misplaced xsl:when!";
  }

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  static
  {
    contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:when not parented by xsl:choose!";
  }

  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  static
  {
    contents[ER_MISPLACED_XSLOTHERWISE][1] =
      "(StylesheetHandler) misplaced xsl:otherwise!";
  }

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  static
  {
    contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:otherwise not parented by xsl:choose!";
  }

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] =
      "(StylesheetHandler) {0} is not allowed inside a template!";
  }

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  static
  {
    contents[ER_UNKNOWN_EXT_NS_PREFIX][1] =
      "(StylesheetHandler) {0} extension namespace prefix {1} unknown";
  }

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  static
  {
    contents[ER_IMPORTS_AS_FIRST_ELEM][1] =
      "(StylesheetHandler) Imports can only occur as the first elements in the stylesheet!";
  }

  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;

  static
  {
    contents[ER_IMPORTING_ITSELF][1] =
      "(StylesheetHandler) {0} is directly or indirectly importing itself!";
  }

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VAL][1] =
      "(StylesheetHandler) " + "xml:space has an illegal value: {0}";
  }

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  static
  {
    contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] =
      "processStylesheet not succesfull!";
  }

  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;

  static
  {
    contents[ER_SAX_EXCEPTION][1] = "SAX Exception";
  }

  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  static
  {
    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "Function not supported!";
  }

  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;

  static
  {
    contents[ER_XSLT_ERROR][1] = "XSLT Error";
  }

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  static
  {
    contents[ER_CURRENCY_SIGN_ILLEGAL][1] =
      "currency sign is not allowed in format pattern string";
  }

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  static
  {
    contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] =
      "Document function not supported in Stylesheet DOM!";
  }

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  static
  {
    contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] =
      "Can't resolve prefix of non-Prefix resolver!";
  }

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  static
  {
    contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] =
      "Redirect extension: Could not get filename - file or select attribute must return vald string.";
  }

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  static
  {
    contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] =
      "Can not build FormatterListener in Redirect extension!";
  }

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  static
  {
    contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] =
      "Prefix in exclude-result-prefixes is not valid: {0}";
  }

  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;

  static
  {
    contents[ER_MISSING_NS_URI][1] =
      "Missing namespace URI for specified prefix";
  }

  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  static
  {
    contents[ER_MISSING_ARG_FOR_OPTION][1] =
      "Missing argument for option: {0}";
  }

  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;

  static
  {
    contents[ER_INVALID_OPTION][1] = "Invalid option: {0}";
  }

  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  static
  {
    contents[ER_MALFORMED_FORMAT_STRING][1] = "Malformed format string: {0}";
  }

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  static
  {
    contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] =
      "xsl:stylesheet requires a 'version' attribute!";
  }

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] =
      "Attribute: {0} has an illegal value: {1}";
  }

  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  static
  {
    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose requires an xsl:when";
  }

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  static
  {
    contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] =
      "xsl:apply-imports not allowed in a xsl:for-each";
  }

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] =
      "Cannot use a DTMLiaison for an output DOM node... pass a org.apache.xpath.DOM2Helper instead!";
  }

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_INPUT][1] =
      "Cannot use a DTMLiaison for a input DOM node... pass a org.apache.xpath.DOM2Helper instead!";
  }

  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;

  static
  {
    contents[ER_CALL_TO_EXT_FAILED][1] =
      "Call to extension element failed: {0}";
  }

  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "Prefix must resolve to a namespace: {0}";
  }

  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "Invalid UTF-16 surrogate detected: {0} ?";
  }

  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  static
  {
    contents[ER_XSLATTRSET_USED_ITSELF][1] =
      "xsl:attribute-set {0} used itself, which will cause an infinite loop.";
  }

  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  static
  {
    contents[ER_CANNOT_MIX_XERCESDOM][1] =
      "Can not mix non Xerces-DOM input with Xerces-DOM output!";
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
      "In ElemTemplateElement.readObject: {0}";
  }

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  static
  {
    contents[ER_DUPLICATE_NAMED_TEMPLATE][1] =
      "Found more than one template named: {0}";
  }

  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  static
  {
    contents[ER_INVALID_KEY_CALL][1] =
      "Invalid function call: recursive key() calls are not allowed";
  }
  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  static
  {
    contents[ER_REFERENCING_ITSELF][1] =
      "Variable {0} is directly or indirectly referencing itself!";
  }
  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  static
  {
    contents[ER_ILLEGAL_DOMSOURCE_INPUT][1] =
      "The input node can not be null for a DOMSource for newTemplates!";
  }
	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  static
  {
    contents[ER_CLASS_NOT_FOUND_FOR_OPTION][1] =
			"Class file not found for option {0}";
  }
	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  static
  {
    contents[ER_REQUIRED_ELEM_NOT_FOUND][1] =
			"Required Element not found: {0}";
  }
  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  static
  {
    contents[ER_INPUT_CANNOT_BE_NULL][1] =
			"InputStream cannot be null";
  }
  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  static
  {
    contents[ER_URI_CANNOT_BE_NULL][1] =
			"URI cannot be null";
  }
  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  static
  {
    contents[ER_FILE_CANNOT_BE_NULL][1] =
			"File cannot be null";
  }
  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  static
  {
    contents[ER_SOURCE_CANNOT_BE_NULL][1] =
			"InputSource cannot be null";
  }
  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  static
  {
    contents[ER_CANNOT_OVERWRITE_CAUSE][1] =
			"Cannot overwrite cause";
  }
  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  static
  {
    contents[ER_CANNOT_INIT_BSFMGR][1] =
			"Could not initialize BSF Manager";
  }
  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  static
  {
    contents[ER_CANNOT_CMPL_EXTENSN][1] =
			"Could not compile extension";
  }
  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  static
  {
    contents[ER_CANNOT_CREATE_EXTENSN][1] =
      "Could not create extension: {0} because of: {1}";
  }
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  static
  {
    contents[ER_INSTANCE_MTHD_CALL_REQUIRES][1] =
      "Instance method call to method {0} requires an Object instance as first argument";
  }
  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  static
  {
    contents[ER_INVALID_ELEMENT_NAME][1] =
      "Invalid element name specified {0}";
  }
  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  static
  {
    contents[ER_ELEMENT_NAME_METHOD_STATIC][1] =
      "Element name method must be static {0}";
  }
  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  static
  {
    contents[ER_EXTENSION_FUNC_UNKNOWN][1] =
             "Extension function {0} : {1} is unknown";
  }
  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  static
  {
    contents[ER_MORE_MATCH_CONSTRUCTOR][1] =
             "More than one best match for constructor for {0}";
  }
  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  static
  {
    contents[ER_MORE_MATCH_METHOD][1] =
             "More than one best match for method {0}";
  }
  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  static
  {
    contents[ER_MORE_MATCH_ELEMENT][1] =
             "More than one best match for element method {0}";
  }
  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  static
  {
    contents[ER_INVALID_CONTEXT_PASSED][1] =
             "Invalid context passed to evaluate {0}";
  }
  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  static
  {
    contents[ER_POOL_EXISTS][1] =
             "Pool already exists";
  }
  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  static
  {
    contents[ER_NO_DRIVER_NAME][1] =
             "No driver Name specified";
  }
  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  static
  {
    contents[ER_NO_URL][1] =
             "No URL specified";
  }
  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  static
  {
    contents[ER_POOL_SIZE_LESSTHAN_ONE][1] =
             "Pool size is less than one!";
  }
  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  static
  {
    contents[ER_INVALID_DRIVER][1] =
             "Invalid driver name specified!";
  }
  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  static
  {
    contents[ER_NO_STYLESHEETROOT][1] =
             "Did not find the stylesheet root!";
  }
  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  static
  {
    contents[ER_ILLEGAL_XMLSPACE_VALUE][1] =
         "Illegal value for xml:space";
  }
  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  static
  {
    contents[ER_PROCESSFROMNODE_FAILED][1] =
         "processFromNode failed";
  }
  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_LOAD][1] =
        "The resource [ {0} ] could not load: {1} \n {2} \t {3}";
  }
   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  static
  {
    contents[ER_BUFFER_SIZE_LESSTHAN_ZERO][1] =
        "Buffer size <=0";
  }
  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  static
  {
    contents[ER_UNKNOWN_ERROR_CALLING_EXTENSION][1] =
        "Unknown error when calling extension";
  }
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  static
  {
    contents[ER_NO_NAMESPACE_DECL][1] =
        "Prefix {0} does not have a corresponding namespace declaration";
  }
  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  static
  {
    contents[ER_ELEM_CONTENT_NOT_ALLOWED][1] =
        "Element content not allowed for lang=javaclass {0}";
  }   
  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  static
  {
    contents[ER_STYLESHEET_DIRECTED_TERMINATION][1] =
        "Stylesheet directed termination";
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
        "Could not load {0} (check CLASSPATH), now using just the defaults";
  }
  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  static
  {
    contents[ER_CANNOT_INIT_DEFAULT_TEMPLATES][1] =
        "Cannot initialize default templates";
  }
  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

  static
  {
    contents[ER_RESULT_NULL][1] =
        "Result should not be null";
  }
    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  static
  {
    contents[ER_RESULT_COULD_NOT_BE_SET][1] =
        "Result could not be set";
  }
  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  static
  {
    contents[ER_NO_OUTPUT_SPECIFIED][1] =
        "No output specified";
  }
  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  static
  {
    contents[ER_CANNOT_TRANSFORM_TO_RESULT_TYPE][1] =
        "Can't transform to a Result of type {0}";
  }
  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  static
  {
    contents[ER_CANNOT_TRANSFORM_SOURCE_TYPE][1] =
        "Can't transform a Source of type {0}";
  }
  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  static
  {
    contents[ER_NULL_CONTENT_HANDLER][1] =
        "Null content handler";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "Null error handler";
  }
  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  static
  {
    contents[ER_CANNOT_CALL_PARSE][1] =
        "parse can not be called if the ContentHandler has not been set";
  }
  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  static
  {
    contents[ER_NO_PARENT_FOR_FILTER][1] =
        "No parent for filter";
  }
  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  static
  {
    contents[ER_NO_STYLESHEET_IN_MEDIA][1] =
         "No stylesheet found in: {0}, media= {1}";
  }
  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  static
  {
    contents[ER_NO_STYLESHEET_PI][1] =
         "No xml-stylesheet PI found in: {0}";
  }
  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  static
  {
    contents[ER_NO_DEFAULT_IMPL][1] =
         "No default implementation found ";
  }
  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  static
  {
    contents[ER_CHUNKEDINTARRAY_NOT_SUPPORTED][1] =
       "ChunkedIntArray({0}) not currently supported";
  }
  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  static
  {
    contents[ER_OFFSET_BIGGER_THAN_SLOT][1] =
       "Offset bigger than slot";
  }
  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  static
  {
    contents[ER_COROUTINE_NOT_AVAIL][1] =
       "Coroutine not available, id={0}";
  }
  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  static
  {
    contents[ER_COROUTINE_CO_EXIT][1] =
       "CoroutineManager received co_exit() request";
  }
  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  static
  {
    contents[ER_COJOINROUTINESET_FAILED][1] =
       "co_joinCoroutineSet() failed";
  }
  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  static
  {
    contents[ER_COROUTINE_PARAM][1] =
       "Coroutine parameter error ({0})";
  }
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  static
  {
    contents[ER_PARSER_DOTERMINATE_ANSWERS][1] =
       "\nUNEXPECTED: Parser doTerminate answers {0}";
  }
  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  static
  {
    contents[ER_NO_PARSE_CALL_WHILE_PARSING][1] =
       "parse may not be called while parsing";
  }
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  static
  {
    contents[ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "Error: typed iterator for axis  {0} not implemented";
  }
  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  static
  {
    contents[ER_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "Error: iterator for axis {0} not implemented ";
  }
  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  static
  {
    contents[ER_ITERATOR_CLONE_NOT_SUPPORTED][1] =
       "Iterator clone not supported";
  }
  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  static
  {
    contents[ER_UNKNOWN_AXIS_TYPE][1] =
       "Unknown axis traversal type: {0}";
  }
  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  static
  {
    contents[ER_AXIS_NOT_SUPPORTED][1] =
       "Axis traverser not supported: {0}";
  }
  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  static
  {
    contents[ER_NO_DTMIDS_AVAIL][1] =
       "No more DTM IDs are available";
  }
  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  static
  {
    contents[ER_NOT_SUPPORTED][1] =
       "Not supported: {0}";
  }
  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  static
  {
    contents[ER_NODE_NON_NULL][1] =
       "Node must be non-null for getDTMHandleFromNode";
  }
  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  static
  {
    contents[ER_COULD_NOT_RESOLVE_NODE][1] =
       "Could not resolve the node to a handle";
  }
  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  static
  {
    contents[ER_STARTPARSE_WHILE_PARSING][1] =
       "startParse may not be called while parsing";
  }
  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  static
  {
    contents[ER_STARTPARSE_NEEDS_SAXPARSER][1] =
       "startParse needs a non-null SAXParser";
  }
  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  static
  {
    contents[ER_COULD_NOT_INIT_PARSER][1] =
       "could not initialize parser with";
  }
  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  static
  {
    contents[ER_PROPERTY_VALUE_BOOLEAN][1] =
       "Value for property {0} should be a Boolean instance";
  }
  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  static
  {
    contents[ER_EXCEPTION_CREATING_POOL][1] =
       "exception creating new instance for pool";
  }
  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  static
  {
    contents[ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE][1] =
       "Path contains invalid escape sequence";
  }
  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  static
  {
    contents[ER_SCHEME_REQUIRED][1] =
       "Scheme is required!";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  static
  {
    contents[ER_NO_SCHEME_IN_URI][1] =
       "No scheme found in URI: {0}";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  static
  {
    contents[ER_NO_SCHEME_INURI][1] =
       "No scheme found in URI";
  }
  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  static
  {
    contents[ER_PATH_INVALID_CHAR][1] =
       "Path contains invalid character: {0}";
  }
  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  static
  {
    contents[ER_SCHEME_FROM_NULL_STRING][1] =
       "Cannot set scheme from null string";
  }
  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  static
  {
    contents[ER_SCHEME_NOT_CONFORMANT][1] =
       "The scheme is not conformant.";
  }
  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  static
  {
    contents[ER_HOST_ADDRESS_NOT_WELLFORMED][1] =
       "Host is not a well formed address";
  }
  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  static
  {
    contents[ER_PORT_WHEN_HOST_NULL][1] =
       "Port cannot be set when host is null";
  }
  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  static
  {
    contents[ER_INVALID_PORT][1] =
       "Invalid port number";
  }
  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  static
  {
    contents[ER_FRAG_FOR_GENERIC_URI][1] =
       "Fragment can only be set for a generic URI";
  }
  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  static
  {
    contents[ER_FRAG_WHEN_PATH_NULL][1] =
       "Fragment cannot be set when path is null";
  }
  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  static
  {
    contents[ER_FRAG_INVALID_CHAR][1] =
       "Fragment contains invalid character";
  }
  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  static
  {
    contents[ER_PARSER_IN_USE][1] =
        "Parser is already in use";
  }
  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  static
  {
    contents[ER_CANNOT_CHANGE_WHILE_PARSING][1] =
        "Cannot change {0} {1} while parsing";
  }
  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  static
  {
    contents[ER_SELF_CAUSATION_NOT_PERMITTED][1] =
        "Self-causation not permitted";
  }
  
   /** src attribute not yet supported for  */
  public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  static
  {
    contents[ER_COULD_NOT_FIND_EXTERN_SCRIPT][1] =
         "Could not get to external script at {0}";
  }
  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_FIND][1] =
        "The resource [ {0} ] could not be found.\n {1}";
  }
  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  static
  {
    contents[ER_OUTPUT_PROPERTY_NOT_RECOGNIZED][1] =
        "Output property not recognized: {0}";
  }
  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  static
  {
    contents[ER_NO_USERINFO_IF_NO_HOST][1] =
        "Userinfo may not be specified if host is not specified";
  }
  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  static
  {
    contents[ER_NO_PORT_IF_NO_HOST][1] =
        "Port may not be specified if host is not specified";
  }
  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  static
  {
    contents[ER_NO_QUERY_STRING_IN_PATH][1] =
        "Query string cannot be specified in path and query string";
  }
  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  static
  {
    contents[ER_NO_FRAGMENT_STRING_IN_PATH][1] =
        "Fragment cannot be specified in both the path and fragment";
  }
  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  static
  {
    contents[ER_CANNOT_INIT_URI_EMPTY_PARMS][1] =
        "Cannot initialize URI with empty parameters";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  static
  {
    contents[ER_FAILED_CREATING_ELEMLITRSLT][1] =
        "Failed creating ElemLiteralResult instance";
  }  
  
   /** Priority value does not contain a parsable number   */
  public static final int ER_PRIORITY_NOT_PARSABLE = 204;

  static
  {
    contents[ER_PRIORITY_NOT_PARSABLE][1] =
        "Priority value does not contain a parsable number";
  }
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

  static
  {
    contents[ER_VALUE_SHOULD_EQUAL][1] =
        " Value for {0} should equal yes or no";
  }
 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  static
  {
    contents[ER_FAILED_CALLING_METHOD][1] =
        " Failed calling {0} method";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  static
  {
    contents[ER_FAILED_CREATING_ELEMTMPL][1] =
        "Failed creating ElemTemplateElement instance";
  }
  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  static
  {
    contents[ER_CHARS_NOT_ALLOWED][1] =
        "Characters are not allowed at this point in the document";
  }
  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  static
  {
    contents[ER_ATTR_NOT_ALLOWED][1] =
        "\"{0}\" attribute is not allowed on the {1} element!";
  }
  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

  static
  {
    contents[ER_METHOD_NOT_SUPPORTED][1] =
        "Method not yet supported ";
  }
 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  static
  {
    contents[ER_BAD_VALUE][1] =
     "{0} bad value {1} ";
  }
  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_FOUND][1] =
     "{0} attribute value not found ";
  }
  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_RECOGNIZED][1] =
     "{0} attribute value not recognized ";
  }

  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  static
  {
    contents[ER_INCRSAXSRCFILTER_NOT_RESTARTABLE][1] =
     "IncrementalSAXSource_Filter not currently restartable";
  }
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  static
  {
    contents[ER_XMLRDR_NOT_BEFORE_STARTPARSE][1] =
     "XMLReader not before startParse request";
  }
  
  /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NULL_URI_NAMESPACE = 216;

  static
  {
    contents[ER_NULL_URI_NAMESPACE][1] =
     "Attempting to generate a namespace prefix with a null URI";
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
      "Found '}' but no attribute template open!";
  }

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  static
  {
    contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] =
      "Warning: count attribute does not match an ancestor in xsl:number! Target = {0}";
  }

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  static
  {
    contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] =
      "Old syntax: The name of the 'expr' attribute has been changed to 'select'.";
  }

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  static
  {
    contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] =
      "Xalan doesn't yet handle the locale name in the format-number function.";
  }

  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;

  static
  {
    contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] =
      "Warning: Could not find locale for xml:lang={0}";
  }

  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "Can not make URL from: {0}";
  }

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  static
  {
    contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] =
      "Can not load requested doc: {0}";
  }

  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;

  static
  {
    contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] =
      "Could not find Collator for <sort xml:lang={0}";
  }

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  static
  {
    contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] =
      "Old syntax: the functions instruction should use a url of {0}";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] =
      "encoding not supported: {0}, using UTF-8";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] =
      "encoding not supported: {0}, using Java {1}";
  }

  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;

  static
  {
    contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] =
      "Specificity conflicts found: {0} Last found in stylesheet will be used.";
  }

  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;

  static
  {
    contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] =
      "========= Parsing and preparing {0} ==========";
  }

  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;

  static
  {
    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "Attr Template, {0}";
  }

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;

  static
  {
    contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] =
      "Match conflict between xsl:strip-space and xsl:preserve-space";
  }

  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;

  static
  {
    contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] =
      "Xalan does not yet handle the {0} attribute!";
  }

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  static
  {
    contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] =
      "No declaration found for decimal format: {0}";
  }

  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;

  static
  {
    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "Missing or incorrect XSLT Namespace. ";
  }

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  static
  {
    contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] =
      "Only one default xsl:decimal-format declaration is allowed.";
  }

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  static
  {
    contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] =
      "xsl:decimal-format names must be unique. Name \"{0}\" has been duplicated.";
  }

  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] =
      "{0} has an illegal attribute: {1}";
  }

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  static
  {
    contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] =
      "Could not resolve namespace prefix: {0}. The node will be ignored.";
  }

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  static
  {
    contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] =
      "xsl:stylesheet requires a 'version' attribute!";
  }

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] =
      "Illegal attribute name: {0}";
  }

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] =
      "Illegal value used for attribute {0}: {1}";
  }

  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;

  static
  {
    contents[WG_EMPTY_SECOND_ARG + MAX_CODE][1] =
      "Resulting nodeset from second argument of document function is empty. The first agument will be used.";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "en";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "en";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "en";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "Parameter to createMessage was out of bounds";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "Exception thrown during messageFormat call";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan Version ";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "yes";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "Line #";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "Column #";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: done";
    contents[MAX_MESSAGES + 11][0] = "xslProc_option";
    contents[MAX_MESSAGES + 11][1] = "Xalan-J command line Process class options:";
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
      "   [-PARSER fully qualified class name of parser liaison]";
    contents[MAX_MESSAGES + 18][0] = "optionE";
    contents[MAX_MESSAGES + 18][1] = "   [-E (Do not expand entity refs)]";
    contents[MAX_MESSAGES + 19][0] = "optionV";
    contents[MAX_MESSAGES + 19][1] = "   [-E (Do not expand entity refs)]";
    contents[MAX_MESSAGES + 20][0] = "optionQC";
    contents[MAX_MESSAGES + 20][1] =
      "   [-QC (Quiet Pattern Conflicts Warnings)]";
    contents[MAX_MESSAGES + 21][0] = "optionQ";
    contents[MAX_MESSAGES + 21][1] = "   [-Q  (Quiet Mode)]";
    contents[MAX_MESSAGES + 22][0] = "optionLF";
    contents[MAX_MESSAGES + 22][1] =
      "   [-LF (Use linefeeds only on output {default is CR/LF})]";
    contents[MAX_MESSAGES + 23][0] = "optionCR";
    contents[MAX_MESSAGES + 23][1] =
      "   [-CR (Use carriage returns only on output {default is CR/LF})]";
    contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
    contents[MAX_MESSAGES + 24][1] =
      "   [-ESCAPE (Which characters to escape {default is <>&\"\'\\r\\n}]";
    contents[MAX_MESSAGES + 25][0] = "optionINDENT";
    contents[MAX_MESSAGES + 25][1] =
      "   [-INDENT (Control how many spaces to indent {default is 0})]";
    contents[MAX_MESSAGES + 26][0] = "optionTT";
    contents[MAX_MESSAGES + 26][1] =
      "   [-TT (Trace the templates as they are being called.)]";
    contents[MAX_MESSAGES + 27][0] = "optionTG";
    contents[MAX_MESSAGES + 27][1] =
      "   [-TG (Trace each generation event.)]";
    contents[MAX_MESSAGES + 28][0] = "optionTS";
    contents[MAX_MESSAGES + 28][1] = "   [-TS (Trace each selection event.)]";
    contents[MAX_MESSAGES + 29][0] = "optionTTC";
    contents[MAX_MESSAGES + 29][1] =
      "   [-TTC (Trace the template children as they are being processed.)]";
    contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
    contents[MAX_MESSAGES + 30][1] =
      "   [-TCLASS (TraceListener class for trace extensions.)]";
    contents[MAX_MESSAGES + 31][0] = "optionVALIDATE";
    contents[MAX_MESSAGES + 31][1] =
      "   [-VALIDATE (Set whether validation occurs.  Validation is off by default.)]";
    contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
    contents[MAX_MESSAGES + 32][1] =
      "   [-EDUMP {optional filename} (Do stackdump on error.)]";
    contents[MAX_MESSAGES + 33][0] = "optionXML";
    contents[MAX_MESSAGES + 33][1] =
      "   [-XML (Use XML formatter and add XML header.)]";
    contents[MAX_MESSAGES + 34][0] = "optionTEXT";
    contents[MAX_MESSAGES + 34][1] =
      "   [-TEXT (Use simple Text formatter.)]";
    contents[MAX_MESSAGES + 35][0] = "optionHTML";
    contents[MAX_MESSAGES + 35][1] = "   [-HTML (Use HTML formatter.)]";
    contents[MAX_MESSAGES + 36][0] = "optionPARAM";
    contents[MAX_MESSAGES + 36][1] =
      "   [-PARAM name expression (Set a stylesheet parameter)]";
    contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 37][1] = "XSL Process was not successful.";
    contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 38][1] = "** Could not find parser **";
    contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 39][1] = "Please check your classpath.";
    contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 40][1] =
      "If you don't have IBM's XML Parser for Java, you can download it from";
    contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 41][1] =
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
		contents[MAX_MESSAGES + 42][0] = "optionURIRESOLVER";
    contents[MAX_MESSAGES + 42][1] = "   [-URIRESOLVER full class name (URIResolver to be used to resolve URIs)]";
		contents[MAX_MESSAGES + 43][0] = "optionENTITYRESOLVER";
    contents[MAX_MESSAGES + 43][1] = "   [-ENTITYRESOLVER full class name (EntityResolver to be used to resolve entities)]";
		contents[MAX_MESSAGES + 44][0] = "optionCONTENTHANDLER";
    contents[MAX_MESSAGES + 44][1] = "   [-CONTENTHANDLER full class name (ContentHandler to be used to serialize output)]";
    contents[MAX_MESSAGES + 45][0] = "optionLINENUMBERS";
    contents[MAX_MESSAGES + 45][1] = "   [-L use line numbers for source document]";
		
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

  /**
   *   Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   *   of ResourceBundle.getBundle().
   *  
   *   @param className the name of the class that implements the resource bundle.
   *   @return the ResourceBundle
   *   @throws MissingResourceException
   */
  public static final XSLTErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return (XSLTErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XSLTErrorResources) ResourceBundle.getBundle(className,
                new Locale("en", "US"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
      }
    }
  }

  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */
  private static final String getResourceSuffix(Locale locale)
  {

    String suffix = "_" + locale.getLanguage();
    String country = locale.getCountry();

    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }

  /**
   * Get a string representation of the error code.
   *
   * @param errorCode Should be a valid error code less than {@link #MAX_CODE}.
   *
   * @return A string representation of the error code, or null if code is 
   * greater than MAX_CODE.
   */
  public String getMessageKey(int errorCode)
  {

    if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return ERROR_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the warning code.
   *
   * @param errorCode Should be a valid warning code less than {@link #MAX_WARNING}.
   *
   * @return A string representation of the warning code, or null if code is 
   * greater than MAX_WARNING.
   */
  public String getWarningKey(int errorCode)
  {

    if (errorCode > MAX_WARNING)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return WARNING_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the message code. (same as getMessageKey).
   *
   * @param errorCode Should be a valid error code less than {@link #MAX_CODE}.
   *
   * @return A string representation of the error code, or null if code is 
   * greater than MAX_CODE.
   */
  public static String getMKey(int errorCode)
  {
    if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return ERROR_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the warning code.
   *
   * @param errorCode Should be a valid warning code less than {@link #MAX_WARNING}.
   *
   * @return A string representation of the warning code, or null if code is 
   * greater than MAX_WARNING.
   */
  public static String getWKey(int errorCode)
  {

    if (errorCode > MAX_WARNING)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return WARNING_SUFFIX + df.format(errorCode);
    }
  }
}
