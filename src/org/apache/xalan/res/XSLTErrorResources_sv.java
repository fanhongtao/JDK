/*
 * @(#)XSLTErrorResources_sv.java	1.10 02/03/26
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
public class XSLTErrorResources_sv extends XSLTErrorResources
{

  /** The error suffix for construction error property keys.   */
  public static final String ERROR_SUFFIX = "FR";

  /** The warning suffix for construction error property keys.   */
  public static final String WARNING_SUFFIX = "VR";

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 215;          

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
      "Fel: Kan inte ha '{' inuti uttryck";
  }

  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE][1] = "{0} har ett otill\u00e5tet attribut: {1}";
  }

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  static
  {
    contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] =
      "sourceNode \u00e4r null i xsl:apply-imports!";
  }

  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;

  static
  {
    contents[ER_CANNOT_ADD][1] = "Kan inte l\u00e4gga {0} till {1}";
  }

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  static
  {
    contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] =
      "sourceNode \u00e4r null i handleApplyTemplatesInstruction!";
  }

  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;

  static
  {
    contents[ER_NO_NAME_ATTRIB][1] = "{0} m\u00e5ste ha ett namn-attribut.";
  }

  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  static
  {
    contents[ER_TEMPLATE_NOT_FOUND][1] = "Hittade inte mallen med namn: {0}";
  }

  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  static
  {
    contents[ER_CANT_RESOLVE_NAME_AVT][1] =
      "Kunde inte l\u00f6sa namn-AVT i xsl:call-template.";
  }

  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;

  static
  {
    contents[ER_REQUIRES_ATTRIB][1] = "{0} kr\u00e4ver attribut: {1}";
  }

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  static
  {
    contents[ER_MUST_HAVE_TEST_ATTRIB][1] =
      "{0} m\u00e5ste ha ett 'test'-attribut.";
  }

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  static
  {
    contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] =
      "D\u00e5ligt v\u00e4rde p\u00e5 niv\u00e5-attribut: {0}";
  }

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] =
      "Namn p\u00e5 behandlande instruktion f\u00e5r inte vara 'xml'";
  }

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  static
  {
    contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] =
      "Namn p\u00e5 behandlande instruktion m\u00e5ste vara ett giltigt NCNamn: {0}";
  }

  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;

  static
  {
    contents[ER_NEED_MATCH_ATTRIB][1] =
      "{0} m\u00e5ste ha ett matchningsattribut om det har ett tillst\u00e5nd.";
  }

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  static
  {
    contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] =
      "{0} kr\u00e4ver antingen ett namn eller ett matchningsattribut.";
  }

  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  static
  {
    contents[ER_CANT_RESOLVE_NSPREFIX][1] =
      "Kan inte l\u00f6sa namnrymdsprefix: {0}";
  }

  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;

  static
  {
    contents[ER_ILLEGAL_VALUE][1] = "xml:space har ett otill\u00e5tet v\u00e4rde: {0}";
  }

  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;

  static
  {
    contents[ER_NO_OWNERDOC][1] =
      "Barnnod saknar \u00e4gardokument!";
  }

  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  static
  {
    contents[ER_ELEMTEMPLATEELEM_ERR][1] = "ElemTemplateElement-fel: {0}";
  }

  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;

  static
  {
    contents[ER_NULL_CHILD][1] = "F\u00f6rs\u00f6ker l\u00e4gga till ett null-barn!";
  }

  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;

  static
  {
    contents[ER_NEED_SELECT_ATTRIB][1] = "{0} kr\u00e4ver ett valattribut.";
  }

  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;

  static
  {
    contents[ER_NEED_TEST_ATTRIB][1] =
      "xsl:when m\u00e5ste ha ett 'test'-attribut.";
  }

  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;

  static
  {
    contents[ER_NEED_NAME_ATTRIB][1] =
      "xsl:with-param m\u00e5ste ha ett 'namn'-attribut.";
  }

  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  static
  {
    contents[ER_NO_CONTEXT_OWNERDOC][1] =
      "Kontext saknar \u00e4gardokument!";
  }

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  static
  {
    contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] =
      "Kunde inte skapa XML TransformerFactory Liaison: {0}";
  }

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  static
  {
    contents[ER_PROCESS_NOT_SUCCESSFUL][1] =
      "Xalan: Process misslyckades.";
  }

  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;

  static
  {
    contents[ER_NOT_SUCCESSFUL][1] = "Xalan: misslyckades.";
  }

  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  static
  {
    contents[ER_ENCODING_NOT_SUPPORTED][1] = "Kodning inte underst\u00f6dd: {0}";
  }

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  static
  {
    contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] =
      "Kunde inte skapa TraceListener: {0}";
  }

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  static
  {
    contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] =
      "xsl:key m\u00e5ste ha ett 'namn'-attribut.";
  }

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  static
  {
    contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] =
      "xsl:key m\u00e5ste ha ett 'matcha'-attribut.";
  }

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  static
  {
    contents[ER_KEY_REQUIRES_USE_ATTRIB][1] =
      "xsl:key m\u00e5ste ha ett 'anv\u00e4nd'-attribut.";
  }

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  static
  {
    contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] =
      "(StylesheetHandler) {0} kr\u00e4ver ett 'element'-attribut!";
  }

  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  static
  {
    contents[ER_MISSING_PREFIX_ATTRIB][1] =
      "(StylesheetHandler) {0} 'prefix'-attribut saknas";
  }

  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;

  static
  {
    contents[ER_BAD_STYLESHEET_URL][1] = "Stylesheet URL \u00e4r d\u00e5lig: {0}";
  }

  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;

  static
  {
    contents[ER_FILE_NOT_FOUND][1] = "Stylesheet-fil saknas: {0}";
  }

  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;

  static
  {
    contents[ER_IOEXCEPTION][1] =
      "Fick IO-Undantag med stylesheet-fil: {0}";
  }

  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;

  static
  {
    contents[ER_NO_HREF_ATTRIB][1] =
      "(StylesheetHandler) Hittade inte href-attribute f\u00f6r {0}";
  }

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  static
  {
    contents[ER_STYLESHEET_INCLUDES_ITSELF][1] =
      "(StylesheetHandler) {0} inkluderar, direkt eller indirekt, sig sj\u00e4lv!";
  }

  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  static
  {
    contents[ER_PROCESSINCLUDE_ERROR][1] =
      "StylesheetHandler.processInclude-fel, {0}";
  }

  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;

  static
  {
    contents[ER_MISSING_LANG_ATTRIB][1] =
      "(StylesheetHandler) {0} 'lang'-attribut' saknas";
  }

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  static
  {
    contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] =
      "(StylesheetHandler) felplacerade {0} element?? Saknar beh\u00e5llarelement  'komponent'";
  }

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  static
  {
    contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] =
      "Kan endast skicka utdata till ett Element, ett DocumentFragment, ett Document, eller en PrintWriter.";
  }

  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;

  static
  {
    contents[ER_PROCESS_ERROR][1] = "StylesheetRoot.process-fel";
  }

  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;

  static
  {
    contents[ER_UNIMPLNODE_ERROR][1] = "UnImplNode-fel: {0}";
  }

  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;

  static
  {
    contents[ER_NO_SELECT_EXPRESSION][1] =
      "Fel! Hittade inte xpath select-uttryck (-select).";
  }

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  static
  {
    contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] =
      "Kan inte serialisera en XSLProcessor!";
  }

  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;

  static
  {
    contents[ER_NO_INPUT_STYLESHEET][1] =
      "Stylesheet-indata ej angiven!";
  }

  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  static
  {
    contents[ER_FAILED_PROCESS_STYLESHEET][1] =
      "Kunde inte behandla stylesheet!";
  }

  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;

  static
  {
    contents[ER_COULDNT_PARSE_DOC][1] = "Kunde inte tolka {0} dokument!";
  }

  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  static
  {
    contents[ER_COULDNT_FIND_FRAGMENT][1] = "Hittade inte fragment: {0}";
  }

  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;

  static
  {
    contents[ER_NODE_NOT_ELEMENT][1] =
      "Nod som pekades p\u00e5 av fragment-identifierare var inte ett element: {0}";
  }

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  static
  {
    contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "for-each kr\u00e4ver antingen en matchning eller ett namnattribut.";
  }

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  static
  {
    contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] =
      "mallar kr\u00e4ver antingen en matchning eller ett namnattribut.";
  }

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  static
  {
    contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] =
      "Ingen klon av ett dokumentfragment!";
  }

  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;

  static
  {
    contents[ER_CANT_CREATE_ITEM][1] =
      "Kan inte skapa element i resultattr\u00e4d: {0}";
  }

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VALUE][1] =
      "xml:space i k\u00e4ll-XML har ett otill\u00e5tet v\u00e4rde: {0}";
  }

  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  static
  {
    contents[ER_NO_XSLKEY_DECLARATION][1] =
      "Det finns ingen xsl:key-deklaration f\u00f6r {0}!";
  }

  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;

  static
  {
    contents[ER_CANT_CREATE_URL][1] = "Fel! Kan inte skapa url f\u00f6r: {0}";
  }

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  static
  {
    contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] = "xsl:functions \u00e4r inte underst\u00f6dd";
  }

  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;

  static
  {
    contents[ER_PROCESSOR_ERROR][1] = "XSLT TransformerFactory-Fel";
  }

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] =
      "(StylesheetHandler) {0} \u00e4r inte till\u00e5ten inne i ett stylesheet!";
  }

  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  static
  {
    contents[ER_RESULTNS_NOT_SUPPORTED][1] =
      "result-ns inte l\u00e4ngre underst\u00f6dd!  Anv\u00e4nd xsl:output ist\u00e4llet.";
  }

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  static
  {
    contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] =
      "default-space inte l\u00e4ngre underst\u00f6dd!  Anv\u00e4nd xsl:strip-space eller xsl:preserve-space ist\u00e4llet.";
  }

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  static
  {
    contents[ER_INDENTRESULT_NOT_SUPPORTED][1] =
      "indent-result inte l\u00e4ngre underst\u00f6dd!  Anv\u00e4nd xsl:output ist\u00e4llet.";
  }

  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;

  static
  {
    contents[ER_ILLEGAL_ATTRIB][1] =
      "(StylesheetHandler) {0} har ett otill\u00e5tet attribut: {1}";
  }

  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  static
  {
    contents[ER_UNKNOWN_XSL_ELEM][1] = "Ok\u00e4nt XSL-element: {0}";
  }

  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;

  static
  {
    contents[ER_BAD_XSLSORT_USE][1] =
      "(StylesheetHandler) xsl:sort kan endast anv\u00e4ndas med xsl:apply-templates eller xsl:for-each.";
  }

  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;

  static
  {
    contents[ER_MISPLACED_XSLWHEN][1] =
      "(StylesheetHandler) felplacerade xsl:when!";
  }

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  static
  {
    contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:when h\u00e4rstammar inte fr\u00e5n xsl:choose!";
  }

  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  static
  {
    contents[ER_MISPLACED_XSLOTHERWISE][1] =
      "(StylesheetHandler) felplacerade xsl:otherwise!";
  }

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  static
  {
    contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] =
      "(StylesheetHandler) xsl:otherwise h\u00e4rstammar inte fr\u00e5n xsl:choose!";
  }

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  static
  {
    contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] =
      "(StylesheetHandler) {0} \u00e4r inte till\u00e5ten inne i en mall!";
  }

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  static
  {
    contents[ER_UNKNOWN_EXT_NS_PREFIX][1] =
      "(StylesheetHandler) {0} utbyggnadsnamnrymdsprefix {1} ok\u00e4nt";
  }

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  static
  {
    contents[ER_IMPORTS_AS_FIRST_ELEM][1] =
      "(StylesheetHandler) Imports kan endast f\u00f6rekomma som de f\u00f6rsta elementen i ett stylesheet!";
  }

  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;

  static
  {
    contents[ER_IMPORTING_ITSELF][1] =
      "(StylesheetHandler) {0} importerar, direkt eller indirekt, sig sj\u00e4lv!";
  }

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  static
  {
    contents[ER_XMLSPACE_ILLEGAL_VAL][1] =
      "(StylesheetHandler) " + "xml:space har ett otill\u00e5tet v\u00e4rde: {0}";
  }

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  static
  {
    contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] =
      "processStylesheet misslyckades!";
  }

  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;

  static
  {
    contents[ER_SAX_EXCEPTION][1] = "SAX-Undantag";
  }

  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  static
  {
    contents[ER_FUNCTION_NOT_SUPPORTED][1] = "Funktion inte underst\u00f6dd:";
  }

  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;

  static
  {
    contents[ER_XSLT_ERROR][1] = "XSLT-fel";
  }

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  static
  {
    contents[ER_CURRENCY_SIGN_ILLEGAL][1] =
      "valutatecken \u00e4r inte till\u00e5tet i formatm\u00f6nsterstr\u00e4ng";
  }

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  static
  {
    contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] =
      "Dokumentfunktion inte underst\u00f6dd i Stylesheet DOM!";
  }

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  static
  {
    contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] =
      "Kan inte l\u00f6sa prefix i icke-Prefixl\u00f6sare!";
  }

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  static
  {
    contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] =
      "Redirect extension: Hittade inte filnamn \u0096 fil eller valattribut m\u00e5ste returnera vald  str\u00e4ng.";
  }

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  static
  {
    contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] =
      "Kan inte bygga FormatterListener i Redirect extension!";
  }

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  static
  {
    contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] =
      "Prefix i exkludera-resultat-prefix \u00e4r inte giltig: {0}";
  }

  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;

  static
  {
    contents[ER_MISSING_NS_URI][1] =
      "Namnrymds-URI saknas f\u00f6r angivna prefix";
  }

  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  static
  {
    contents[ER_MISSING_ARG_FOR_OPTION][1] =
      "Argument saknas f\u00f6r alternativ: {0}";
  }

  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;

  static
  {
    contents[ER_INVALID_OPTION][1] = "Ogiltigt alternativ: {0}";
  }

  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  static
  {
    contents[ER_MALFORMED_FORMAT_STRING][1] = "Fel format p\u00e5 formatstr\u00e4ng: {0}";
  }

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  static
  {
    contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] =
      "xsl:stylesheet m\u00e5ste ha ett 'version'-attribut!";
  }

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  static
  {
    contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] =
      "Attribut: {0} har ett otill\u00e5tet v\u00e4rde: {1}";
  }

  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  static
  {
    contents[ER_CHOOSE_REQUIRES_WHEN][1] = "xsl:choose kr\u00e4ver ett xsl:when";
  }

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  static
  {
    contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] =
      "xsl:apply-imports inte till\u00e5tet i ett xsl:for-each";
  }

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] =
      "Kan inte anv\u00e4nda DTMLiaison till en DOM utdatanod... skicka en org.apache.xpath.DOM2Helper ist\u00e4llet!";
  }

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  static
  {
    contents[ER_CANT_USE_DTM_FOR_INPUT][1] =
      "Kan inte anv\u00e4nda DTMLiaison till en DOM indatanod... skicka en org.apache.xpath.DOM2Helper ist\u00e4llet!";
  }

  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;

  static
  {
    contents[ER_CALL_TO_EXT_FAILED][1] =
      "Anrop till anslutningselement misslyckades: {0}";
  }

  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "Prefix m\u00e5ste l\u00f6sa till en mamnrymd: {0}";
  }

  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "Ogiltigt UTF-16-surrogat uppt\u00e4ckt: {0} ?";
  }

  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  static
  {
    contents[ER_XSLATTRSET_USED_ITSELF][1] =
      "xsl:attribute-set {0} anv\u00e4nde sig sj\u00e4lvt, vilket kommer att orsaka en  o\u00e4ndlig loop.";
  }

  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  static
  {
    contents[ER_CANNOT_MIX_XERCESDOM][1] =
      "Kan inte blanda icke-Xerces-DOM-indata med Xerces-DOM-utdata!";
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
      "I ElemTemplateElement.readObject: {0}";
  }

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  static
  {
    contents[ER_DUPLICATE_NAMED_TEMPLATE][1] =
      "Hittade mer \u00e4n en mall med namnet: {0}";
  }

  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  static
  {
    contents[ER_INVALID_KEY_CALL][1] =
      "Ogiltigt funktionsanrop: rekursiva key()-anrop \u00e4r inte till\u00e5tna";
  }
  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  static
  {
    contents[ER_REFERENCING_ITSELF][1] =
      "Variabel {0} h\u00e4nvisar, direkt eller indirekt, till sig sj\u00e4lv!";
  }
  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  static
  {
    contents[ER_ILLEGAL_DOMSOURCE_INPUT][1] =
      "Indatanoden till en DOMSource f\u00f6r newTemplates f\u00e5r inte vara null!";
  }
	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  static
  {
    contents[ER_CLASS_NOT_FOUND_FOR_OPTION][1] =
			"Klassfil f\u00f6r alternativ {0} saknas";
  }
	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  static
  {
    contents[ER_REQUIRED_ELEM_NOT_FOUND][1] =
			"N\u00f6dv\u00e4ndigt element saknas: {0}";
  }
  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  static
  {
    contents[ER_INPUT_CANNOT_BE_NULL][1] =
			"InputStream f\u00e5r inte vara null";
  }
  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  static
  {
    contents[ER_URI_CANNOT_BE_NULL][1] =
			"URI f\u00e5r inte vara null";
  }
  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  static
  {
    contents[ER_FILE_CANNOT_BE_NULL][1] =
			"Fil f\u00e5r inte vara null";
  }
  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  static
  {
    contents[ER_SOURCE_CANNOT_BE_NULL][1] =
			"InputSource f\u00e5r inte vara null";
  }
  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  static
  {
    contents[ER_CANNOT_OVERWRITE_CAUSE][1] =
			"Kan inte skriva \u00f6ver orsak";
  }
  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  static
  {
    contents[ER_CANNOT_INIT_BSFMGR][1] =
			"Kan inte initialisera BSF Manager";
  }
  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  static
  {
    contents[ER_CANNOT_CMPL_EXTENSN][1] =
			"Kunde inte kompilera anslutning";
  }
  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  static
  {
    contents[ER_CANNOT_CREATE_EXTENSN][1] =
      "Kunde inte skapa anslutning: {0} p\u00e5 grund av: {1}";
  }
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  static
  {
    contents[ER_INSTANCE_MTHD_CALL_REQUIRES][1] =
      "Instansmetodanrop till metod {0} kr\u00e4ver en Objektinstans som f\u00f6rsta argument";
  }
  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  static
  {
    contents[ER_INVALID_ELEMENT_NAME][1] =
      "Ogiltigt elementnamn angivet {0}";
  }
  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  static
  {
    contents[ER_ELEMENT_NAME_METHOD_STATIC][1] =
      "Elementnamnmetod m\u00e5ste vara static {0}";
  }
  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  static
  {
    contents[ER_EXTENSION_FUNC_UNKNOWN][1] =
             "Anslutningsfunktion {0} : {1} \u00e4r ok\u00e4nd";
  }
  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  static
  {
    contents[ER_MORE_MATCH_CONSTRUCTOR][1] =
             "Fler \u00e4n en b\u00e4sta matchning f\u00f6r konstruktor f\u00f6r {0}";
  }
  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  static
  {
    contents[ER_MORE_MATCH_METHOD][1] =
             "Fler \u00e4n en b\u00e4sta matchning f\u00f6r metod {0}";
  }
  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  static
  {
    contents[ER_MORE_MATCH_ELEMENT][1] =
             "Fler \u00e4n en b\u00e4sta matchning f\u00f6r elementmetod {0}";
  }
  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  static
  {
    contents[ER_INVALID_CONTEXT_PASSED][1] =
             "Ogiltig kontext skickad f\u00f6r att utv\u00e4rdera {0}";
  }
  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  static
  {
    contents[ER_POOL_EXISTS][1] =
             "Pool finns redan";
  }
  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  static
  {
    contents[ER_NO_DRIVER_NAME][1] =
             "Inget driver-namn angivet";
  }
  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  static
  {
    contents[ER_NO_URL][1] =
             "Ingen URL angiven";
  }
  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  static
  {
    contents[ER_POOL_SIZE_LESSTHAN_ONE][1] =
             "Poolstorlek \u00e4r mindre \u00e4n ett!";
  }
  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  static
  {
    contents[ER_INVALID_DRIVER][1] =
             "Ogiltigt driver-namn angivet";
  }
  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  static
  {
    contents[ER_NO_STYLESHEETROOT][1] =
             "Hittade inte stylesheet-roten!";
  }
  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  static
  {
    contents[ER_ILLEGAL_XMLSPACE_VALUE][1] =
         "Ogiltigt v\u00e4rde f\u00f6r xml:space";
  }
  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  static
  {
    contents[ER_PROCESSFROMNODE_FAILED][1] =
         "processFromNode misslyckades";
  }
  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_LOAD][1] =
        "Resursen [ {0} ] kunde inte laddas: {1} \n {2} \t {3}";
  }
   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  static
  {
    contents[ER_BUFFER_SIZE_LESSTHAN_ZERO][1] =
        "Bufferstorlek <=0";
  }
  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  static
  {
    contents[ER_UNKNOWN_ERROR_CALLING_EXTENSION][1] =
        "Ok\u00e4nt fel vid anslutningsanrop";
  }
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  static
  {
    contents[ER_NO_NAMESPACE_DECL][1] =
        "Prefix{0} har inte en motsvarande namnrymdsdeklaration";
  }
  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  static
  {
    contents[ER_ELEM_CONTENT_NOT_ALLOWED][1] =
        "Elementinneh\u00e5ll \u00e4r inte till\u00e5tet f\u00f6r lang=javaclass {0}";
  }   
  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  static
  {
    contents[ER_STYLESHEET_DIRECTED_TERMINATION][1] =
        "Stylesheet-ledd avslutning";
  }
  
   /** 1 or 2   */
  public static final int ER_ONE_OR_TWO = 141;

  static
  {
    contents[ER_ONE_OR_TWO][1] =
        "1 eller 2";
  }
  
   /** 2 or 3   */
  public static final int ER_TWO_OR_THREE = 142;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
        "2 eller 3";
  }
  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  static
  {
    contents[ER_COULD_NOT_LOAD_RESOURCE][1] =
        "Kunde inte ladda {0} (kontrollera CLASSPATH), anv\u00e4nder nu enbart standard";
  }
  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  static
  {
    contents[ER_CANNOT_INIT_DEFAULT_TEMPLATES][1] =
        "Kan inte initialisera standardmallar";
  }
  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

  static
  {
    contents[ER_RESULT_NULL][1] =
        "Result borde inte vara null";
  }
    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  static
  {
    contents[ER_RESULT_COULD_NOT_BE_SET][1] =
        "Result kunde inte s\u00e4ttas";
  }
  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  static
  {
    contents[ER_NO_OUTPUT_SPECIFIED][1] =
        "Ingen utdata angiven";
  }
  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  static
  {
    contents[ER_CANNOT_TRANSFORM_TO_RESULT_TYPE][1] =
        "Kan inte omvandla till en Result av typ {0}";
  }
  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  static
  {
    contents[ER_CANNOT_TRANSFORM_SOURCE_TYPE][1] =
        "Kan inte omvandla en Source av typ {0}";
  }
  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  static
  {
    contents[ER_NULL_CONTENT_HANDLER][1] =
        "Inneh\u00e5llshanterare med v\u00e4rde null";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "Felhanterare med v\u00e4rde null";
  }
  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  static
  {
    contents[ER_CANNOT_CALL_PARSE][1] =
        "parse kan inte anropas om ContentHandler inte har satts";
  }
  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  static
  {
    contents[ER_NO_PARENT_FOR_FILTER][1] =
        "Ingen f\u00f6r\u00e4lder till filter";
  }
  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  static
  {
    contents[ER_NO_STYLESHEET_IN_MEDIA][1] =
         "Stylesheet saknas i: {0}, media= {1}";
  }
  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  static
  {
    contents[ER_NO_STYLESHEET_PI][1] =
         "xml-stylesheet PI saknas i: {0}";
  }
  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  static
  {
    contents[ER_NO_DEFAULT_IMPL][1] =
         "Standardimplementering saknas i:";
  }
  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  static
  {
    contents[ER_CHUNKEDINTARRAY_NOT_SUPPORTED][1] =
       "ChunkedIntArray({0}) underst\u00f6ds f\u00f6r n\u00e4rvarande inte";
  }
  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  static
  {
    contents[ER_OFFSET_BIGGER_THAN_SLOT][1] =
       "Offset st\u00f6rre \u00e4n fack";
  }
  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  static
  {
    contents[ER_COROUTINE_NOT_AVAIL][1] =
       "Sidorutin inte tillg\u00e4nglig, id={0}";
  }
  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  static
  {
    contents[ER_COROUTINE_CO_EXIT][1] =
       "CoroutineManager mottog co_exit()-f\u00f6rfr\u00e5gan";
  }
  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  static
  {
    contents[ER_COJOINROUTINESET_FAILED][1] =
       "co_joinCoroutineSet() misslyckades";
  }
  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  static
  {
    contents[ER_COROUTINE_PARAM][1] =
       "Sidorutin fick parameterfel ({0})";
  }
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  static
  {
    contents[ER_PARSER_DOTERMINATE_ANSWERS][1] =
       "\nOV\u00c4NTAT: Parser doTerminate-svar {0}";
  }
  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  static
  {
    contents[ER_NO_PARSE_CALL_WHILE_PARSING][1] =
       "parse f\u00e5r inte anropas medan tolkning sker";
  }
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  static
  {
    contents[ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "Fel: typad upprepare f\u00f6r axel {0} inte implementerad";
  }
  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  static
  {
    contents[ER_ITERATOR_AXIS_NOT_IMPLEMENTED][1] =
       "Fel: upprepare f\u00f6r axel {0} inte implementerad";
  }
  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  static
  {
    contents[ER_ITERATOR_CLONE_NOT_SUPPORTED][1] =
       "Uppreparklon underst\u00f6ds inte";
  }
  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  static
  {
    contents[ER_UNKNOWN_AXIS_TYPE][1] =
       "Ok\u00e4nd axeltraverstyp: {0}";
  }
  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  static
  {
    contents[ER_AXIS_NOT_SUPPORTED][1] =
       "Axeltravers underst\u00f6ds inte: {0}";
  }
  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  static
  {
    contents[ER_NO_DTMIDS_AVAIL][1] =
       "Inga fler DTM-IDs \u00e4r tillg\u00e4ngliga";
  }
  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  static
  {
    contents[ER_NOT_SUPPORTED][1] =
       "Underst\u00f6ds inte: {0}";
  }
  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  static
  {
    contents[ER_NODE_NON_NULL][1] =
       "Nod m\u00e5ste vara icke-null f\u00f6r getDTMHandleFromNode";
  }
  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  static
  {
    contents[ER_COULD_NOT_RESOLVE_NODE][1] =
       "Kunde inte l\u00f6sa nod till ett handtag";
  }
  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  static
  {
    contents[ER_STARTPARSE_WHILE_PARSING][1] =
       "startParse f\u00e5r inte anropas medan tolkning sker";
  }
  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  static
  {
    contents[ER_STARTPARSE_NEEDS_SAXPARSER][1] =
       "startParse beh\u00f6ver en SAXParser som \u00e4r icke-null";
  }
  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  static
  {
    contents[ER_COULD_NOT_INIT_PARSER][1] =
       "kunde inte initialisera tolk med";
  }
  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  static
  {
    contents[ER_PROPERTY_VALUE_BOOLEAN][1] =
       "V\u00e4rde p\u00e5 egenskap {0} borde vara en Boolesk instans";
  }
  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  static
  {
    contents[ER_EXCEPTION_CREATING_POOL][1] =
       "undantag skapar ny instans f\u00f6r pool";
  }
  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  static
  {
    contents[ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE][1] =
       "V\u00e4g inneh\u00e5ller ogiltig flyktsekvens";
  }
  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  static
  {
    contents[ER_SCHEME_REQUIRED][1] =
       "Schema kr\u00e4vs!";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  static
  {
    contents[ER_NO_SCHEME_IN_URI][1] =
       "Schema saknas i URI: {0}";
  }
  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  static
  {
    contents[ER_NO_SCHEME_INURI][1] =
       "Schema saknas i URI";
  }
  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  static
  {
    contents[ER_PATH_INVALID_CHAR][1] =
       "V\u00e4g inneh\u00e5ller ogiltigt tecken: {0}";
  }
  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  static
  {
    contents[ER_SCHEME_FROM_NULL_STRING][1] =
       "Kan inte s\u00e4tta schema fr\u00e5n null-str\u00e4ng";
  }
  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  static
  {
    contents[ER_SCHEME_NOT_CONFORMANT][1] =
       "Schemat \u00e4r inte likformigt.";
  }
  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  static
  {
    contents[ER_HOST_ADDRESS_NOT_WELLFORMED][1] =
       "V\u00e4rd \u00e4r inte en v\u00e4lformulerad adress";
  }
  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  static
  {
    contents[ER_PORT_WHEN_HOST_NULL][1] =
       "Port kan inte s\u00e4ttas n\u00e4r v\u00e4rd \u00e4r null";
  }
  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  static
  {
    contents[ER_INVALID_PORT][1] =
       "Ogiltigt portnummer";
  }
  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  static
  {
    contents[ER_FRAG_FOR_GENERIC_URI][1] =
       "Fragment kan bara s\u00e4ttas f\u00f6r en allm\u00e4n URI";
  }
  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  static
  {
    contents[ER_FRAG_WHEN_PATH_NULL][1] =
       "Fragment kan inte s\u00e4ttas n\u00e4r v\u00e4g \u00e4r null";
  }
  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  static
  {
    contents[ER_FRAG_INVALID_CHAR][1] =
       "Fragment inneh\u00e5ller ogiltigt tecken";
  }
  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  static
  {
    contents[ER_PARSER_IN_USE][1] =
        "Tolk anv\u00e4nds redan";
  }
  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  static
  {
    contents[ER_CANNOT_CHANGE_WHILE_PARSING][1] =
        "Kan inte \u00e4ndra {0} {1} medan tolkning sker";
  }
  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  static
  {
    contents[ER_SELF_CAUSATION_NOT_PERMITTED][1] =
        "Sj\u00e4lvorsakande inte till\u00e5ten";
  }
  
   /** src attribute not yet supported for  */
  public static final int ER_SRC_ATTRIB_NOT_SUPPORTED = 195;

  static
  {
    contents[ER_SRC_ATTRIB_NOT_SUPPORTED][1] =
       "src-attributet underst\u00f6ds \u00e4nnu inte f\u00f6r {0}";
  }
  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  static
  {
    contents[ER_RESOURCE_COULD_NOT_FIND][1] =
        "Resursen [ {0} ] saknas. \n {1}";
  }
  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  static
  {
    contents[ER_OUTPUT_PROPERTY_NOT_RECOGNIZED][1] =
        "Utdata-egenskap k\u00e4nns inte igen: {0}";
  }
  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  static
  {
    contents[ER_NO_USERINFO_IF_NO_HOST][1] =
        "Userinfo f\u00e5r inte anges om v\u00e4rden inte \u00e4r angiven";
  }
  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  static
  {
    contents[ER_NO_PORT_IF_NO_HOST][1] =
        "Port f\u00e5r inte anges om v\u00e4rden inte \u00e4r angiven";
  }
  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  static
  {
    contents[ER_NO_QUERY_STRING_IN_PATH][1] =
        "F\u00f6rfr\u00e5gan-str\u00e4ng kan inte anges i v\u00e4g och f\u00f6rfr\u00e5gan-str\u00e4ng";
  }
  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  static
  {
    contents[ER_NO_FRAGMENT_STRING_IN_PATH][1] =
        "Fragment kan inte anges i b\u00e5de v\u00e4gen och fragmentet";
  }
  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  static
  {
    contents[ER_CANNOT_INIT_URI_EMPTY_PARMS][1] =
        "Kan inte initialisera URI med tomma parametrar";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  static
  {
    contents[ER_FAILED_CREATING_ELEMLITRSLT][1] =
        "Kunde inte skapa instans av ElemLiteralResult";
  }  
  
   /** Priority value does not contain a parsable number   */
  public static final int ER_PRIORITY_NOT_PARSABLE = 204;

  static
  {
    contents[ER_PRIORITY_NOT_PARSABLE][1] =
        "Prioritetsv\u00e4rde inneh\u00e5ller inte ett tolkningsbart nummer";
  }
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

  static
  {
    contents[ER_VALUE_SHOULD_EQUAL][1] =
        "V\u00e4rde p\u00e5 {0} borde motsvara ja eller nej";
  }
 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  static
  {
    contents[ER_FAILED_CALLING_METHOD][1] =
        " Kunde inte anropa metoden {0}";
  }
  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  static
  {
    contents[ER_FAILED_CREATING_ELEMTMPL][1] =
        "Kunde inte skapa instans av ElemTemplateElement";
  }
  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  static
  {
    contents[ER_CHARS_NOT_ALLOWED][1] =
        "Tecken \u00e4r inte till\u00e5tna i dokumentet vid den h\u00e4r tidpunkten";
  }
  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  static
  {
    contents[ER_ATTR_NOT_ALLOWED][1] =
        "Attributet \"{0}\" \u00e4r inte till\u00e5ten i det {1} elementet!";
  }
  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

  static
  {
    contents[ER_METHOD_NOT_SUPPORTED][1] =
        "Metod \u00e4nnu inte underst\u00f6dd ";
  }
 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  static
  {
    contents[ER_BAD_VALUE][1] =
     "{0} d\u00e5ligt v\u00e4rde {1} ";
  }
  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_FOUND][1] =
     "Attributet {0} saknas ";
  }
  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  static
  {
    contents[ER_ATTRIB_VALUE_NOT_RECOGNIZED][1] =
     "Attributv\u00e4rdet {0} k\u00e4nns inte igen ";
  }

  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  static
  {
    contents[ER_INCRSAXSRCFILTER_NOT_RESTARTABLE][1] =
     "IncrementalSAXSource_Filter kan f\u00f6r n\u00e4rvarande inte startas om";
  }
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  static
  {
    contents[ER_XMLRDR_NOT_BEFORE_STARTPARSE][1] =
     "XMLReader inte innan startParse-beg\u00e4ran";
  }
  
  
  /*
    /**  Cannot find SAX1 driver class    *
  public static final int ER_CANNOT_FIND_SAX1_DRIVER = 190;

  static
  {
    contents[ER_CANNOT_FIND_SAX1_DRIVER][1] =
      "Driverklass {0} f\u00f6r SAX1 saknas";
  }
  
   /**  SAX1 driver class {0} found but cannot be loaded    *
  public static final int ER_SAX1_DRIVER_NOT_LOADED = 191;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_LOADED][1] =
      "Driverklass {0} f\u00f6r SAX1 hittades men kunde inte laddas";
  }
  
   /**  SAX1 driver class {0} found but cannot be instantiated    *
  public static final int ER_SAX1_DRIVER_NOT_INSTANTIATED = 192;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_INSTANTIATED][1] =
      "Driverklass {0} f\u00f6r SAX1 laddades men kunde inte instansieras";
  }
  
   /**  SAX1 driver class {0} does not implement org.xml.sax.Parser    *
  public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 193;

  static
  {
    contents[ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER][1] =
      "Driverklass {0} f\u00f6r SAX1 implementerar inte org.xml.sax.Parser";
  }
  
   /**  System property org.xml.sax.parser not specified    *
  public static final int ER_PARSER_PROPERTY_NOT_SPECIFIED = 194;

  static
  {
    contents[ER_PARSER_PROPERTY_NOT_SPECIFIED][1] =
      "Systemegenskap org.xml.sax.parser ej angiven";
  }
  
   /**  Parser argument must not be null    *
  public static final int ER_PARSER_ARG_CANNOT_BE_NULL = 195;

  static
  {
    contents[ER_PARSER_ARG_CANNOT_BE_NULL][1] =
      "Tolkargument f\u00e5r inte vara null";
  }
  
   /**  Feature:    *
  public static final int ER_FEATURE = 196;

  static
  {
    contents[ER_FEATURE][1] =
        "Utm\u00e4rkande drag: {0}";
  }
  
   /**  Property:    *
  public static final int ER_PROPERTY = 197;

  static
  {
    contents[ER_PROPERTY][1] =
        "Egenskap: {0}";
  }
  
   /** Null Entity Resolver  *
  public static final int ER_NULL_ENTITY_RESOLVER = 198;

  static
  {
    contents[ER_NULL_ENTITY_RESOLVER][1] =
        "Enhetsl\u00f6sare med v\u00e4rde null";
  }
  
   /** Null DTD handler  *
  public static final int ER_NULL_DTD_HANDLER = 199;

  static
  {
    contents[ER_NULL_DTD_HANDLER][1] =
        "DTDhanterare med v\u00e4rde null";
  }
  
 */ 
  

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  public static final int WG_FOUND_CURLYBRACE = 1;

  static
  {
    contents[WG_FOUND_CURLYBRACE + MAX_CODE][1] =
      "Hittade '}' men ingen attributmall \u00e4r \u00f6ppen!";
  }

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  static
  {
    contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] =
      "Varning: r\u00e4knarattribut matchar inte en f\u00f6rf\u00e4der in xsl:number! Target = {0}";
  }

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  static
  {
    contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] =
      "Gammal syntax: Namnet p\u00e5  'expr'-attributet har \u00e4ndrats till 'select'.";
  }

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  static
  {
    contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] =
      "Xalan hanterar \u00e4nnu inte locale-namnet i funktionen format-number.";
  }

  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;

  static
  {
    contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] =
      "Varning: Hittade inte locale f\u00f6r xml:lang{0}";
  }

  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "Kan inte skapa URL fr\u00e5n: {0}";
  }

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  static
  {
    contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] =
      "Kan inte ladda beg\u00e4rd doc: {0}";
  }

  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;

  static
  {
    contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] =
      "Hittade inte Collator f\u00f6r <sort xml:lang={0}";
  }

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  static
  {
    contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] =
      "Gammal syntax: Funktionsinstruktionen borde anv\u00e4nda en url av {0}";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] =
      "kodning underst\u00f6ds inte: {0}, anv\u00e4nder UTF-8";
  }

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  static
  {
    contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] =
      "kodning underst\u00f6ds inte: {0}, anv\u00e4nder Java {1}";
  }

  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;

  static
  {
    contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] =
      "Hittade specificitetskonflikter: {0} Senast hittade i stylesheet kommer att anv\u00e4ndas.";
  }

  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;

  static
  {
    contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] =
      "========= Tolkar och f\u00f6rbereder {0} ==========";
  }

  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;

  static
  {
    contents[WG_ATTR_TEMPLATE + MAX_CODE][1] = "Attributmall, {0}";
  }

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;

  static
  {
    contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] =
      "Matcha konflikter mellan xsl:strip-space och xsl:preserve-space";
  }

  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;

  static
  {
    contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] =
      "Xalan hanterar \u00e4nnu inte attributet {0}!";
  }

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  static
  {
    contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] =
      "Deklaration saknas f\u00f6r decimalformat: {0}";
  }

  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;

  static
  {
    contents[WG_OLD_XSLT_NS + MAX_CODE][1] = "XSLT-Namnrymd saknas eller \u00e4r inkorrekt ";
  }

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  static
  {
    contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] =
      "Endast en standarddeklaration av xsl:decimal-format \u00e4r till\u00e5ten.";
  }

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  static
  {
    contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] =
      "xsl:decimal-formatnamn m\u00e5ste vara unika. Namnet \"{0}\" har blivit duplicerat.";
  }

  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] =
      "{0} har ett otill\u00e5tet attribut: {1}";
  }

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  static
  {
    contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] =
      "Kan inte l\u00f6sa namnrymdsprefix: {0}. Noden kommer att ignoreras.";
  }

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  static
  {
    contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] =
      "xsl:stylesheet m\u00e5ste ha ett 'version'-attribut!";
  }

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] =
      "Otill\u00e5tet attributnamn: {0}";
  }

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  static
  {
    contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] =
      "Ogiltigt v\u00e4rde anv\u00e4nt f\u00f6r attribut {0}: {1}";
  }

  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;

  static
  {
    contents[WG_EMPTY_SECOND_ARG + MAX_CODE][1] =
      "Den resulterande nodm\u00e4ngden fr\u00e5n dokumentfunktions andra argument \u00e4r tomt. Det f\u00f6rsta argumentet kommer att anv\u00e4ndas.";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "sv";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES +1][1] = "sv";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "sv";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "Parameter till createMessage ligger utanf\u00f6r till\u00e5tet intervall";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "Undantag utl\u00f6st vid messageFormat-anrop";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan Version";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "ja";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "Rad #";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "Kolumn #";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: f\u00e4rdig";
    contents[MAX_MESSAGES + 11][0] = "xslProc_option";
    contents[MAX_MESSAGES + 11][1] = "Xalan-J kommando linje Process klass alternativ:";
    contents[MAX_MESSAGES + 12][0] = "optionIN";
    contents[MAX_MESSAGES + 12][1] = "    -IN inputXMLURL";
    contents[MAX_MESSAGES + 13][0] = "optionXSL";
    contents[MAX_MESSAGES + 13][1] = "   [-XSL XSLTransformationURL]";
    contents[MAX_MESSAGES + 14][0] = "optionOUT";
    contents[MAX_MESSAGES + 14][1] = "   [-OUT utdataFilnamn]";
    contents[MAX_MESSAGES + 15][0] = "optionLXCIN";
    contents[MAX_MESSAGES + 15][1] =
      "   [-LXCIN kompileratStylesheetFilnameIn]";
    contents[MAX_MESSAGES + 16][0] = "optionLXCOUT";
    contents[MAX_MESSAGES + 16][1] =
      "   [-LXCOUT kompileratStylesheetFilenameUt]";
    contents[MAX_MESSAGES + 17][0] = "optionPARSER";
    contents[MAX_MESSAGES + 17][1] =
      "   [-PARSER fullt kvalificerat klassnamn eller tolkf\u00f6rbindelse]";
    contents[MAX_MESSAGES + 18][0] = "optionE";
    contents[MAX_MESSAGES + 18][1] = "   [-E (Ut\u00f6ka inte enhetsreferenser)]";
    contents[MAX_MESSAGES + 19][0] = "optionV";
    contents[MAX_MESSAGES +19][1] = "   [-E (Ut\u00f6ka inte enhetsreferenser)]";
    contents[MAX_MESSAGES + 20][0] = "optionQC";
    contents[MAX_MESSAGES + 20][1] =
      "   [-QC (Tysta M\u00f6nsterkonfliktvarningar)]";
    contents[MAX_MESSAGES + 21][0] = "optionQ";
    contents[MAX_MESSAGES + 21][1] = "   [-Q  (Tyst Tillst\u00e5nd)]";
    contents[MAX_MESSAGES + 22][0] = "optionLF";
    contents[MAX_MESSAGES + 22][1] =
      "   [-LF (Anv\u00e4nd radframmatning enbart p\u00e5 utdata {standard \u00e4r CR/LF})]";
    contents[MAX_MESSAGES + 23][0] = "optionCR";
    contents[MAX_MESSAGES + 23][1] =
      "   [-CR (Anv\u00e4nd vagnretur enbart p\u00e5 utdata {standard \u00e4r CR/LF})]";
    contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
    contents[MAX_MESSAGES + 24][1] =
      "   [-ESCAPE (Vilka tecken \u00e4r skiftningstecken {standard \u00e4r <>&\"\'\\r\\n}]";
    contents[MAX_MESSAGES + 25][0] = "optionINDENT";
    contents[MAX_MESSAGES + 25][1] =
      "   [-INDENT (Best\u00e4m antal blanksteg f\u00f6r att tabulera {standard \u00e4r 0})]";
    contents[MAX_MESSAGES + 26][0] = "optionTT";
    contents[MAX_MESSAGES + 26][1] =
      "   [-TT (Sp\u00e5ra mallarna allt eftersom de blir anropade.)]";
    contents[MAX_MESSAGES + 27][0] = "optionTG";
    contents[MAX_MESSAGES + 27][1] =
      "   [-TG (Sp\u00e5ra varje generationsh\u00e4ndelse.)]";
    contents[MAX_MESSAGES + 28][0] = "optionTS";
    contents[MAX_MESSAGES + 28][1] = "   [-TS (Sp\u00e5ra varje valh\u00e4ndelse.)]";
    contents[MAX_MESSAGES + 29][0] = "optionTTC";
    contents[MAX_MESSAGES + 29][1] =
      "   [-TTC (Sp\u00e5ra mallbarnen allt eftersom de blir behandlade.)]";
    contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
    contents[MAX_MESSAGES + 30][1] =
      "   [-TCLASS (TraceListener-klass f\u00f6r sp\u00e5rningsanslutningar.)]";
    contents[MAX_MESSAGES + 31][0] = "optionVALIDATE";
    contents[MAX_MESSAGES + 31][1] =
      "   [-VALIDATE (S\u00e4tt om validering ska ske.  Standard \u00e4r att validering \u00e4r avst\u00e4ngd)]";
    contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
    contents[MAX_MESSAGES + 32][1] =
      "   [-EDUMP {valfritt filnamn) (G\u00f6r stackdump vid fel.)]";
    contents[MAX_MESSAGES + 33][0] = "optionXML";
    contents[MAX_MESSAGES + 33][1] =
      "   [-XML (Anv\u00e4nd XML-formaterare och l\u00e4gg till XML-huvud.)]";
    contents[MAX_MESSAGES + 34][0] = "optionTEXT";
    contents[MAX_MESSAGES + 34][1] =
      "   [-XML (Anv\u00e4nd enkel Text-formaterare.)]";
    contents[MAX_MESSAGES + 35][0] = "optionHTML";
    contents[MAX_MESSAGES + 35][1] = "   [-HTML (Anv\u00e4nd HTML-formaterare)]";
    contents[MAX_MESSAGES + 36][0] = "optionPARAM";
    contents[MAX_MESSAGES + 36][1] =
      "   [-PARAM namn uttryck (S\u00e4tt en stylesheet-parameter)]";
    contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
    contents[MAX_MESSAGES +37][1] = "XSL-Process misslyckades.";
    contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
    contents[MAX_MESSAGES +38][1] = "** Hittade inte tolk **";
    contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
    contents[MAX_MESSAGES +39][1] = "V\u00e4nligen kontrollera din classpath";
    contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 40][1] =
      "Om du inte har IBMs XML-Tolk f\u00f6r Java, kan du ladda ner den fr\u00e5n";
    contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 41][1] =
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
		contents[MAX_MESSAGES + 42][0] = "optionURIRESOLVER";
    contents[MAX_MESSAGES + 42][1] = "   [-URIRESOLVER fullst\u00e4ndigt klassnamn (URIResolver som ska anv\u00e4ndas f\u00f6r att l\u00f6sa URI-er)]";
		contents[MAX_MESSAGES + 43][0] = "optionENTITYRESOLVER";
    contents[MAX_MESSAGES + 43][1] = "   [-ENTITYRESOLVER fullst\u00e4ndigt klassnamn (EntityResolver som ska anv\u00e4ndas f\u00f6r att l\u00f6sa enheter)]";
		contents[MAX_MESSAGES + 44][0] = "optionCONTENTHANDLER";
    contents[MAX_MESSAGES + 44][1] = "   [-CONTENTRESOLVER fullst\u00e4ndigt klassnamn (ContentHandler som ska anv\u00e4ndas f\u00f6r att serialisera utdata)]";
    contents[MAX_MESSAGES + 45][0] = "optionLINENUMBERS";
    contents[MAX_MESSAGES + 45][1] = "   [-L anv\u00e4nd radnummer i k\u00e4lldokument]";
		
  }

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "D\u00c5LIG_KOD";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMATERING_MISSLYCKADES";

  /** General error string.   */
  public static final String ERROR_STRING = "#fel";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Fel: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Varning: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "M\u00d6NSTER ";

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
