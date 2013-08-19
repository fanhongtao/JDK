/*
 * @(#)XSLTErrorResources_de.java	1.3 03/04/25
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

public class XSLTErrorResources_de extends XSLTErrorResources
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

  /** The lookup table for error messages.   */
  public static final Object[][] contents = {

  /** Error message ID that has a null message, but takes in a single object.    */
    { "ERROR0000", "{0}"},

  /** ER_NO_CURLYBRACE          */
//  public static final int ER_NO_CURLYBRACE = 1;

  {
    ER_NO_CURLYBRACE,
      "Fehler: '{' in Ausdruck nicht zul\u00e4ssig"},

  /** ER_ILLEGAL_ATTRIBUTE          */
//  public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  {
    ER_ILLEGAL_ATTRIBUTE,
	"{0} hat ein unzul\u00e4ssiges Attribut: {1}"},

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
//  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  {
    ER_NULL_SOURCENODE_APPLYIMPORTS,
      "sourceNode ist Null in xsl:apply-imports!"},

  /** ER_CANNOT_ADD          */
//  public static final int ER_CANNOT_ADD = 4;

  {
    ER_CANNOT_ADD,
	"{0} kann {1} nicht hinzugef\u00fcgt werden"},

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
//  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  {
    ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "sourceNode ist Null in handleApplyTemplatesInstruction!"},

  /** ER_NO_NAME_ATTRIB          */
//  public static final int ER_NO_NAME_ATTRIB = 6;

  {
    ER_NO_NAME_ATTRIB,
	"{0} muss ein ''name''-Attribut haben."},

  /** ER_TEMPLATE_NOT_FOUND          */
//  public static final int ER_TEMPLATE_NOT_FOUND = 7;

  {
    ER_TEMPLATE_NOT_FOUND,
	"Vorlage konnte nicht gefunden werden: {0}"},

  /** ER_CANT_RESOLVE_NAME_AVT          */
//  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  {
    ER_CANT_RESOLVE_NAME_AVT,
      "AVT-Name in xsl:call-template konnte nicht aufgel\u00f6st werden."},

  /** ER_REQUIRES_ATTRIB          */
//  public static final int ER_REQUIRES_ATTRIB = 9;

  {
    ER_REQUIRES_ATTRIB,
	"{0} erfordert Attribut: {1}"},

  /** ER_MUST_HAVE_TEST_ATTRIB          */
//  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  {
    ER_MUST_HAVE_TEST_ATTRIB,
      "{0} muss ein ''test''-Attribut haben."},

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
//  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  {
    ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "Ung\u00fcltiger Wert des ''level''-Attributs: {0}"},

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
//  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  {
    ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "processing-instruction-Name kann nicht 'xml' sein"},

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
//  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  {
    ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "processing-instruction-Name muss ein g\u00fcltiger NCName sein: {0}"},

  /** ER_NEED_MATCH_ATTRIB          */
//  public static final int ER_NEED_MATCH_ATTRIB = 14;

  {
    ER_NEED_MATCH_ATTRIB,
      "{0} muss ein ''match''-Attribut haben, falls ein Modus angegeben ist."},

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
//  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  {
    ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} erfordert entweder ein ''name''- oder ein ''match''-Attribut."},

  /** ER_CANT_RESOLVE_NSPREFIX          */
//  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  {
    ER_CANT_RESOLVE_NSPREFIX,
      "Namensraum-Pr\u00e4fix kann nicht aufgel\u00f6st werden: {0}"},

  /** ER_ILLEGAL_VALUE          */
//  public static final int ER_ILLEGAL_VALUE = 17;

  {
    ER_ILLEGAL_VALUE,
	 "xml:space hat unzul\u00e4ssigen Wert: {0}"},

  /** ER_NO_OWNERDOC          */
//  public static final int ER_NO_OWNERDOC = 18;

  {
    ER_NO_OWNERDOC,
      "Tochterknoten hat kein Eigent\u00fcmer-Dokument!"},

  /** ER_ELEMTEMPLATEELEM_ERR          */
//  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  {
    ER_ELEMTEMPLATEELEM_ERR,
	"ElemTemplateElement-Fehler: {0}"},

  /** ER_NULL_CHILD          */
//  public static final int ER_NULL_CHILD = 20;

  {
    ER_NULL_CHILD,
 	"Versuch, einen Null-Tochterknoten hinzuzuf\u00fcgen!"},

  /** ER_NEED_SELECT_ATTRIB          */
//  public static final int ER_NEED_SELECT_ATTRIB = 21;

  {
    ER_NEED_SELECT_ATTRIB,
	"{0} erfordert ein ''select''-Attribut."},

  /** ER_NEED_TEST_ATTRIB          */
//  public static final int ER_NEED_TEST_ATTRIB = 22;

  {
    ER_NEED_TEST_ATTRIB,
      "xsl:when muss ein 'test'-Attribut haben."},

  /** ER_NEED_NAME_ATTRIB          */
//  public static final int ER_NEED_NAME_ATTRIB = 23;

  {
    ER_NEED_NAME_ATTRIB,
      "xsl:with-param muss ein 'name'-Attribut haben."},

  /** ER_NO_CONTEXT_OWNERDOC          */
//  public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  {
    ER_NO_CONTEXT_OWNERDOC,
      "Kontext hat kein Eigent\u00fcmer-Dokument!"},

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
//  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  {
    ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "XML TransformerFactory Liaison konnte nicht erstellt werden: {0}"},

  /** ER_PROCESS_NOT_SUCCESSFUL          */
//  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  {
    ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: Prozess fehlgeschlagen."},

  /** ER_NOT_SUCCESSFUL          */
//  public static final int ER_NOT_SUCCESSFUL = 27;

  {
    ER_NOT_SUCCESSFUL,
	"Xalan: fehlgeschlagen."},

  /** ER_ENCODING_NOT_SUPPORTED          */
//  public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  {
    ER_ENCODING_NOT_SUPPORTED,
	"Codierung nicht unterst\u00fctzt: {0}"},

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
//  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  {
    ER_COULD_NOT_CREATE_TRACELISTENER,
      "TraceListener konnte nicht erstellt werden: {0}"},

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
//  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  {
    ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key erfordert ein 'name'-Attribut!"},

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
//  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  {
    ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key erfordert ein 'match'-Attribut!"},

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
//  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  {
    ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key erfordert ein 'use'-Attribut!"},

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
//  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  {
    ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} erfordert ein ''elements''-Attribut!"},

  /** ER_MISSING_PREFIX_ATTRIB          */
//  public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  {
    ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} ''prefix''-Attribut fehlt"},

  /** ER_BAD_STYLESHEET_URL          */
//  public static final int ER_BAD_STYLESHEET_URL = 35;

  {
    ER_BAD_STYLESHEET_URL,
	"Stylesheet-URL ung\u00fcltig: {0}"},

  /** ER_FILE_NOT_FOUND          */
//  public static final int ER_FILE_NOT_FOUND = 36;

  {
    ER_FILE_NOT_FOUND,
	"Stylesheet-Datei nicht gefunden: {0}"},

  /** ER_IOEXCEPTION          */
//  public static final int ER_IOEXCEPTION = 37;

  {
    ER_IOEXCEPTION,
      "IO-Ausnahme bei Stylesheet-Datei: {0}"},

  /** ER_NO_HREF_ATTRIB          */
//  public static final int ER_NO_HREF_ATTRIB = 38;

  {
    ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) ''href''-Attribut f\u00fcr {0} nicht gefunden"},

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
//  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  {
    ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} schlie\u00dft sich selbst direkt oder indirekt ein!"},

  /** ER_PROCESSINCLUDE_ERROR          */
//  public static final int ER_PROCESSINCLUDE_ERROR = 40;

  {
    ER_PROCESSINCLUDE_ERROR,
      "StylesheetHandler.processInclude-Fehler, {0}"},

  /** ER_MISSING_LANG_ATTRIB          */
//  public static final int ER_MISSING_LANG_ATTRIB = 41;

  {
    ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} ''lang''-Attribut fehlt"},

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
//  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  {
    ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) Element {0} an falscher Position?? Containerelement ''component'' fehlt"},

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
//  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  {
    ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "Ausgabe nur m\u00f6glich in Element, DocumentFragment, Document oder PrintWriter."},

  /** ER_PROCESS_ERROR          */
//  public static final int ER_PROCESS_ERROR = 44;

  {
    ER_PROCESS_ERROR,
	"StylesheetRoot.process-Fehler"},

  /** ER_UNIMPLNODE_ERROR          */
//  public static final int ER_UNIMPLNODE_ERROR = 45;

  {
    ER_UNIMPLNODE_ERROR,
	"UnImplNode-Fehler: {0}"},

  /** ER_NO_SELECT_EXPRESSION          */
//  public static final int ER_NO_SELECT_EXPRESSION = 46;

  {
    ER_NO_SELECT_EXPRESSION,
      "Fehler! 'select'-Ausdruck bei xpath nicht gefunden (-select)."},

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
//  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  {
    ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "Ein XSLProcessor kann nicht serialisiert werden!"},

  /** ER_NO_INPUT_STYLESHEET          */
//  public static final int ER_NO_INPUT_STYLESHEET = 48;

  {
    ER_NO_INPUT_STYLESHEET,
      "Stylesheet-Eingabe nicht angegeben!"},

  /** ER_FAILED_PROCESS_STYLESHEET          */
//  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  {
    ER_FAILED_PROCESS_STYLESHEET,
      "Stylesheet konnte nicht verarbeitet werden!"},

  /** ER_COULDNT_PARSE_DOC          */
//  public static final int ER_COULDNT_PARSE_DOC = 50;

  {
    ER_COULDNT_PARSE_DOC,
	"Dokument {0} konnte nicht geparst werden!"},

  /** ER_COULDNT_FIND_FRAGMENT          */
//  public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  {
    ER_COULDNT_FIND_FRAGMENT,
	"Fragment nicht gefunden: {0}"},

  /** ER_NODE_NOT_ELEMENT          */
//  public static final int ER_NODE_NOT_ELEMENT = 52;

  {
    ER_NODE_NOT_ELEMENT,
      "Knoten, auf den von einem Fragmentbezeichner gezeigt wird, war kein Element: {0}"},

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
//  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  {
    ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each muss ein 'match'- oder 'name'-Attribut haben"},

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
//  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  {
    ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "Vorlagen m\u00fcssen ein 'match'- oder 'name'-Attribut haben"},

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
//  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  {
    ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "Kein Klone eines Dokument-Fragments!"}, 

  /** ER_CANT_CREATE_ITEM          */
//  public static final int ER_CANT_CREATE_ITEM = 56;

  {
    ER_CANT_CREATE_ITEM,
      "Element im Ergebnisbaum kann nicht erstellt werden: {0}"},

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
//  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  {
    ER_XMLSPACE_ILLEGAL_VALUE,
      "xml:space in der Quell-XML hat einen ung\u00fcltigen Wert: {0}"},

  /** ER_NO_XSLKEY_DECLARATION          */
//  public static final int ER_NO_XSLKEY_DECLARATION = 58;

  {
    ER_NO_XSLKEY_DECLARATION,
      "Keine xsl:key-Vereinbarung f\u00fcr {0} vorhanden!"},

  /** ER_CANT_CREATE_URL          */
//  public static final int ER_CANT_CREATE_URL = 59;

  {
    ER_CANT_CREATE_URL,
	"Fehler! URL kann nicht erstellt werden f\u00fcr: {0}"},

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
//  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  {
    ER_XSLFUNCTIONS_UNSUPPORTED,
	 "xsl:functions nicht unterst\u00fctzt"},

  /** ER_PROCESSOR_ERROR          */
//  public static final int ER_PROCESSOR_ERROR = 61;

  {
    ER_PROCESSOR_ERROR, "XSLT TransformerFactory-Fehler"},

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
//  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  {
    ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} in einem Stylesheet nicht zul\u00e4ssig!"},

  /** ER_RESULTNS_NOT_SUPPORTED          */
//  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  {
    ER_RESULTNS_NOT_SUPPORTED,
      "result-ns nicht mehr unterst\u00fctzt! Verwenden Sie statt dessen xsl:output."},

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
//  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  {
    ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space nicht mehr unterst\u00fctzt! Verwenden Sie statt dessen xsl:strip-space oder xsl:preserve-space."},

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
//  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  {
    ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result nicht mehr unterst\u00fctzt! Verwenden Sie statt dessen xsl:output."},

  /** ER_ILLEGAL_ATTRIB          */
//  public static final int ER_ILLEGAL_ATTRIB = 66;

  {
    ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} hat ein ung\u00fcltiges Attribut: {1}"},

  /** ER_UNKNOWN_XSL_ELEM          */
//  public static final int ER_UNKNOWN_XSL_ELEM = 67;

  {
    ER_UNKNOWN_XSL_ELEM, "Ungekanntes XSL-Element: {0}"},

  /** ER_BAD_XSLSORT_USE          */
//  public static final int ER_BAD_XSLSORT_USE = 68;

  {
    ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort kann nur mit xsl:apply-templates oder xsl:for-each verwendet werden."},

  /** ER_MISPLACED_XSLWHEN          */
//  public static final int ER_MISPLACED_XSLWHEN = 69;

  {
    ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) xsl:when an falscher Position!"},

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
//  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  {
    ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when ohne \u00fcbergeordnetes xsl:choose!"},

  /** ER_MISPLACED_XSLOTHERWISE          */
//  public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  {
    ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) xsl:otherwise an falscher Position!"},

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
//  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  {
    ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise ohne \u00fcbergeordnetes xsl:choose!"},

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
//  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  {
    ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} in einer Vorlage nicht zul\u00e4ssig!"},

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
//  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  {
    ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) Namensraum-Pr\u00e4fix {1} der Dateierweiterung {0} unbekannt"},

  /** ER_IMPORTS_AS_FIRST_ELEM          */
//  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  {
    ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Importe nur als erste Elemente im Stylesheet m\u00f6glich!"},

  /** ER_IMPORTING_ITSELF          */
//  public static final int ER_IMPORTING_ITSELF = 76;

  {
    ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} schlie\u00dft sich selbst direkt oder indirekt ein!"},

  /** ER_XMLSPACE_ILLEGAL_VAL          */
//  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  {
    ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) " + "xml:space hat ung\u00fcltigen Wert: {0}"},

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
//  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  {
    ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet fehlgeschlagen!"},

  /** ER_SAX_EXCEPTION          */
//  public static final int ER_SAX_EXCEPTION = 79;

  {
    ER_SAX_EXCEPTION, "SAX-Ausnahme"},

  /** ER_FUNCTION_NOT_SUPPORTED          */
//  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  {
    ER_FUNCTION_NOT_SUPPORTED, "Funktion nicht unterst\u00fctzt!"},

  /** ER_XSLT_ERROR          */
//  public static final int ER_XSLT_ERROR = 81;

  {
    ER_XSLT_ERROR, "XSLT-Fehler"},

  /** ER_CURRENCY_SIGN_ILLEGAL          */
//  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  {
    ER_CURRENCY_SIGN_ILLEGAL,
      "W\u00e4hrungszeichen in Formatierungsmuster nicht zul\u00e4ssig"},

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
//  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  {
    ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "document-Funktion in Stylesheet-DOM nicht unterst\u00fctzt!"},

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
//  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  {
    ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "Pr\u00e4fix von Nicht-Pr\u00e4fix-Resolver kann nicht aufgel\u00f6st werden!"},

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
//  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  {
    ER_REDIRECT_COULDNT_GET_FILENAME,
      "Redirect-Erweiterung: Dateiname konnte nicht ermittelt werden - 'file'- oder 'select'-Attribut muss g\u00fcltige Zeichenkette zur\u00fcckgeben."},

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
//  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  {
    ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "FormatterListener kann in Redirect-Erweiterung nicht aufgebaut werden!"},

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
//  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  {
    ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "Pr\u00e4fix in exclude-result-prefixes ist ung\u00fcltig: {0}"},

  /** ER_MISSING_NS_URI          */
//  public static final int ER_MISSING_NS_URI = 88;

  {
    ER_MISSING_NS_URI,
      "Namensraum-URI f\u00fcr angegebenes Pr\u00e4fix fehlt"},

  /** ER_MISSING_ARG_FOR_OPTION          */
//  public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  {
    ER_MISSING_ARG_FOR_OPTION,
      "Argument f\u00fcr Option fehlt: {0}"},

  /** ER_INVALID_OPTION          */
//  public static final int ER_INVALID_OPTION = 90;

  {
    ER_INVALID_OPTION, "Ung\u00fcltige Option: {0}"},

  /** ER_MALFORMED_FORMAT_STRING          */
//  public static final int ER_MALFORMED_FORMAT_STRING = 91;

  {
    ER_MALFORMED_FORMAT_STRING, "Ung\u00fcltige Formatierungszeichenkette: {0}"},

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
//  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  {
    ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet erfordert ein 'version'-Attribut!"},

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
//  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  {
    ER_ILLEGAL_ATTRIBUTE_VALUE,
      "Attribut: {0} hat einen ung\u00fcltigen Wert: {1}"},

  /** ER_CHOOSE_REQUIRES_WHEN          */
//  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  {
    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose erfordert xsl:when"},

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
//  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  {
    ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports nicht zul\u00e4ssig in xsl:for-each"},

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
//  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  {
    ER_CANT_USE_DTM_FOR_OUTPUT,
      "Objekt der Klasse DTMLiaison kann f\u00fcr einen Ausgabe-DOM-Knoten nicht verwendet werden... \u00dcbergeben Sie statt dessen ein Objekt der Klasse org.apache.xpath.DOM2Helper!"},

  /** ER_CANT_USE_DTM_FOR_INPUT          */
//  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  {
    ER_CANT_USE_DTM_FOR_INPUT,
      "Objekt der Klasse DTMLiaison kann f\u00fcr einen Eingabe-DOM-Knoten nicht verwendet werden... \u00dcbergeben Sie statt dessen ein Objekt der Klasse org.apache.xpath.DOM2Helper!"},

  /** ER_CALL_TO_EXT_FAILED          */
//  public static final int ER_CALL_TO_EXT_FAILED = 98;

  {
    ER_CALL_TO_EXT_FAILED,
      "Aufruf des Erweiterungselements fehlgeschlagen: {0}"},

  /** ER_PREFIX_MUST_RESOLVE          */
//  public static final int ER_PREFIX_MUST_RESOLVE = 99;

  {
    ER_PREFIX_MUST_RESOLVE,
      "Pr\u00e4fix muss sich in einen Namensraum aufl\u00f6sen: {0}"},

  /** ER_INVALID_UTF16_SURROGATE          */
//  public static final int ER_INVALID_UTF16_SURROGATE = 100;

  {
    ER_INVALID_UTF16_SURROGATE,
      "Ung\u00fcltiges UTF-16-Surrogat erkannt: {0} ?"},

  /** ER_XSLATTRSET_USED_ITSELF          */
//  public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  {
    ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} verwendet sich selbst, was zu einer Endlosschleife f\u00fchrt."},

  /** ER_CANNOT_MIX_XERCESDOM          */
//  public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  {
    ER_CANNOT_MIX_XERCESDOM,
      "Eingabe, die nicht Xerces-DOM entspricht, kann nicht mit Xerces-DOM-Ausgabe gemischt werden!"},

  /** ER_TOO_MANY_LISTENERS          */
//  public static final int ER_TOO_MANY_LISTENERS = 103;

  {
    ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
//  public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;

  {
    ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "In ElemTemplateElement.readObject: {0}"},

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
//  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  {
    ER_DUPLICATE_NAMED_TEMPLATE,
      "Mehr als eine Vorlage gefunden, Name: {0}"},

  /** ER_INVALID_KEY_CALL          */
//  public static final int ER_INVALID_KEY_CALL = 106;

  {
    ER_INVALID_KEY_CALL,
      "Ung\u00fcltiger Funktionsaufruf: Aufrufe mit rekursivem Schl\u00fcssel () nicht zul\u00e4ssig"},
  
  /** Variable is referencing itself          */
 // public static final int ER_REFERENCING_ITSELF = 107;

  {
    ER_REFERENCING_ITSELF,
      "Variable {0} bezieht sich direkt oder indirekt auf sich selbst!"},
  
  /** Illegal DOMSource input          */
 // public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  {
    ER_ILLEGAL_DOMSOURCE_INPUT,
      "Der Eingabeknoten darf f\u00fcr ein DOMSource f\u00fcr newTemplates nicht Null sein!"},
	
	/** Class not found for option         */
//  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  {
    ER_CLASS_NOT_FOUND_FOR_OPTION,
			"Klassendatei nicht gefunden f\u00fcr Option {0}"},
	
	/** Required Element not found         */
//  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  {
    ER_REQUIRED_ELEM_NOT_FOUND,
			"Erforderliches Element nicht gefunden: {0}"},
  
  /** InputStream cannot be null         */
 // public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  {
    ER_INPUT_CANNOT_BE_NULL,
			"InputStream kann nicht Null sein"},
  
  /** URI cannot be null         */
 // public static final int ER_URI_CANNOT_BE_NULL = 112;

  {
    ER_URI_CANNOT_BE_NULL,
			"URI kann nicht Null sein"},
  
  /** File cannot be null         */
 // public static final int ER_FILE_CANNOT_BE_NULL = 113;

  {
    ER_FILE_CANNOT_BE_NULL,
			"Datei kann nicht Null sein"},
  
   /** InputSource cannot be null         */
 // public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  {
    ER_SOURCE_CANNOT_BE_NULL,
			"InputSource kann nicht Null sein"},
  
  /** Can't overwrite cause         */
 // public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"Ursache f\u00fcr nicht m\u00f6gliches \u00dcberschreiben"},
  
  /** Could not initialize BSF Manager        */
 // public static final int ER_CANNOT_INIT_BSFMGR = 116;

  {
    ER_CANNOT_INIT_BSFMGR,
			"BSF Manager konnte nicht initialisiert werden"},
  
  /** Could not compile extension       */
 // public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  {
    ER_CANNOT_CMPL_EXTENSN,
			"Erweiterung kann nicht kompiliert werden"},
  
  /** Could not create extension       */
 // public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  {
    ER_CANNOT_CREATE_EXTENSN,
      "Erweiterung kann nicht erstellt werde: {0} Grund: {1}"},
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
 // public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  {
    ER_INSTANCE_MTHD_CALL_REQUIRES,
      "Instanzenmethoden-Aufruf von Methode {0} erfordert eine Object-Instanz als erstes Argument"},
  
  /** Invalid element name specified       */
 // public static final int ER_INVALID_ELEMENT_NAME = 120;

  {
    ER_INVALID_ELEMENT_NAME,
      "Ung\u00fcltiger Elementname angegeben {0}"},
  
   /** Element name method must be static      */
 // public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  {
    ER_ELEMENT_NAME_METHOD_STATIC,
      "Elementnamen-Methode muss statisch sein {0}"},
  
   /** Extension function {0} : {1} is unknown      */
 // public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  {
    ER_EXTENSION_FUNC_UNKNOWN,
             "Erweiterungsfunktion {0} : {1} ist unbekannt"},
  
   /** More than one best match for constructor for       */
 // public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  {
    ER_MORE_MATCH_CONSTRUCTOR,
             "Mehr als ein Best-Match f\u00fcr Konstruktor f\u00fcr {0}"},
  
   /** More than one best match for method      */
 // public static final int ER_MORE_MATCH_METHOD = 124;

  {
    ER_MORE_MATCH_METHOD,
             "Mehr als ein Best-Match f\u00fcr Methode {0}"},
  
   /** More than one best match for element method      */
 // public static final int ER_MORE_MATCH_ELEMENT = 125;

  {
    ER_MORE_MATCH_ELEMENT,
             "Mehr als ein Best-Match f\u00fcr Elementmethode {0}"},
  
   /** Invalid context passed to evaluate       */
 // public static final int ER_INVALID_CONTEXT_PASSED = 126;

  {
    ER_INVALID_CONTEXT_PASSED,
             "Ung\u00fcltiger Kontext zur Auswertung von {0} \u00fcbergeben"},
  
   /** Pool already exists       */
 // public static final int ER_POOL_EXISTS = 127;

  {
    ER_POOL_EXISTS,
             "Pool besteht bereits"},
  
   /** No driver Name specified      */
 // public static final int ER_NO_DRIVER_NAME = 128;

  {
    ER_NO_DRIVER_NAME,
             "Kein Treibername angegeben"},
  
   /** No URL specified     */
 // public static final int ER_NO_URL = 129;

  {
    ER_NO_URL,
             "Kein URL angegeben"},
  
   /** Pool size is less than one    */
 // public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  {
    ER_POOL_SIZE_LESSTHAN_ONE,
             "Pool-Gr\u00f6\u00dfe kleiner als Eins!"},
  
   /** Invalid driver name specified    */
 // public static final int ER_INVALID_DRIVER = 131;

  {
    ER_INVALID_DRIVER,
             "Ung\u00fcltiger Treibername angegeben!"},
  
   /** Did not find the stylesheet root    */
 // public static final int ER_NO_STYLESHEETROOT = 132;

  {
    ER_NO_STYLESHEETROOT,
             "Stylesheet-Stamm nicht gefunden!"},
  
   /** Illegal value for xml:space     */
 // public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  {
    ER_ILLEGAL_XMLSPACE_VALUE,
         "Ung\u00fcltiger Wert f\u00fcr xml:space"},
  
   /** processFromNode failed     */
//  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  {
    ER_PROCESSFROMNODE_FAILED,
         "processFromNode fehlgeschlagen"},
  
   /** The resource [] could not load:     */
  //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  {
    ER_RESOURCE_COULD_NOT_LOAD,
        "Die Ressource [ {0} ] konnte nicht laden: {1} \n {2} \t {3}"},
   
  
   /** Buffer size <=0     */
  //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  {
    ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Puffergr\u00f6\u00dfe <=0"},
  
   /** Unknown error when calling extension    */
  //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  {
    ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "Unbekannter Fehler beim Aufruf der Erweiterung"},
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  //public static final int ER_NO_NAMESPACE_DECL = 138;

  {
    ER_NO_NAMESPACE_DECL,
        "Pr\u00e4fix {0} hat keine entsprechende Namensraum-Vereinbarung"},
  
   /** Element content not allowed for lang=javaclass   */
  //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  {
    ER_ELEM_CONTENT_NOT_ALLOWED,
        "Elementinhalt nicht zul\u00e4ssig f\u00fcr lang=javaclass {0}"},
  
   /** Stylesheet directed termination   */
  //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  {
    ER_STYLESHEET_DIRECTED_TERMINATION,
        "Stylesheet f\u00fchrte zu Beendigung"},
  
   /** 1 or 2   */
  //public static final int ER_ONE_OR_TWO = 141;

  {
    ER_ONE_OR_TWO,
        "1 oder 2"},
  
   /** 2 or 3   */
  //public static final int ER_TWO_OR_THREE = 142;

  {
    ER_TWO_OR_THREE,
        "2 oder 3"},

   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  {
    ER_COULD_NOT_LOAD_RESOURCE,
        "{0} konnte nicht geladen werden (\u00fcberpr\u00fcfen Sie CLASSPATH); jetzt werden die Standardwerte verwendet"},
  
   /** Cannot initialize default templates   */
  //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  {
    ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "Standardvorlagen k\u00f6nnen nicht initialisiert werden"},
  
   /** Result should not be null   */
  //public static final int ER_RESULT_NULL = 145;

  {
    ER_RESULT_NULL,
        "Ergebnis sollte nicht Null sein"},
    
   /** Result could not be set   */
  //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  {
    ER_RESULT_COULD_NOT_BE_SET,
        "Ergebnis konnte nicht festgelegt werden"},
  
   /** No output specified   */
  //public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  {
    ER_NO_OUTPUT_SPECIFIED,
        "Keine Ausgabe festgelegt"},
  
   /** Can't transform to a Result of type   */
  //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  {
    ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "Transformation in ein Ergebnis vom Typ {0} nicht m\u00f6glich"},
  
   /** Can't transform to a Source of type   */
  //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  {
    ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "Transformation einer Quelle vom Typ {0} nicht m\u00f6glich"},
  
   /** Null content handler  */
  //public static final int ER_NULL_CONTENT_HANDLER = 150;

  {
    ER_NULL_CONTENT_HANDLER,
        "Kein Content-Handler"},
  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 151;

  {
    ER_NULL_ERROR_HANDLER,
        "Kein Error-Handler"},
  
   /** parse can not be called if the ContentHandler has not been set */
  //public static final int ER_CANNOT_CALL_PARSE = 152;

  {
    ER_CANNOT_CALL_PARSE,
        "parse kann nicht aufgerufen werden, wenn der ContentHandler nicht festgelegt wurde"},
  
   /**  No parent for filter */
  //public static final int ER_NO_PARENT_FOR_FILTER = 153;

  {
    ER_NO_PARENT_FOR_FILTER,
        "Kein \u00fcbergeordneter Knoten f\u00fcr Filter"},
  
  
   /**  No stylesheet found in: {0}, media */
  //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  {
    ER_NO_STYLESHEET_IN_MEDIA,
         "Kein Stylesheet gefunden in: {0}, media= {1}"},
  
   /**  No xml-stylesheet PI found in */
  //public static final int ER_NO_STYLESHEET_PI = 155;

  {
    ER_NO_STYLESHEET_PI,
         "Kein xml-Stylesheet PI gefunden in: {0}"},
  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;

  {
    ER_NO_DEFAULT_IMPL,
         "Keine Standardimplementierung gefunden"},
  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0}) zurzeit nicht unterst\u00fctzt"},
  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "Offset gr\u00f6\u00dfer als Slot"},
  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;

  {
    ER_COROUTINE_NOT_AVAIL,
       "Coroutine nicht verf\u00fcgbar, ID={0}"},
  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;

  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager empfing Anforderung co_exit()"},
  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;

  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet() fehlgeschlagen"},
  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;

  {
    ER_COROUTINE_PARAM,
       "Parameterfehler in Coroutine ({0})"},
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: Parser doTerminate antwortet {0}"},
  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "parse darf w\u00e4hrend des Parsens nicht aufgerufen werden"},
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Fehler: Typisierter Iterator f\u00fcr Achse {0} nicht implementiert"},
  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Fehler: Iterator f\u00fcr Achse {0} nicht implementiert"},
  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "Iterator-Klone nicht unterst\u00fctzt"},
  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  {
    ER_UNKNOWN_AXIS_TYPE,
       "Unbekannter Achsen-Traversaltyp: {0}"},
  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;

  {
    ER_AXIS_NOT_SUPPORTED,
       "Achsen-Traverser nicht unterst\u00fctzt: {0}"},
  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;

  {
    ER_NO_DTMIDS_AVAIL,
       "Keine weiteren DTM-IDs verf\u00fcgbar"},
  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;

  {
    ER_NOT_SUPPORTED,
       "Nicht unterst\u00fctzt: {0}"},
  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;

  {
    ER_NODE_NON_NULL,
       "Knoten darf f\u00fcr getDTMHandleFromNode nicht Null sein"},
  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  {
    ER_COULD_NOT_RESOLVE_NODE,
       "Der Knoten zu einem Handle konnte nicht aufgel\u00f6st werden"},
  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  {
    ER_STARTPARSE_WHILE_PARSING,
       "startParse darf beim Parsen nicht aufgerufen werden"},
  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse ben\u00f6tigt einen SAXParser, der nicht Null ist"},
  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;

  {
    ER_COULD_NOT_INIT_PARSER,
       "Parser konnte nicht initialisiert werden"},
  
   /**  Value for property {0} should be a Boolean instance  */
  //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  {
    ER_PROPERTY_VALUE_BOOLEAN,
       "Wert f\u00fcr Eigenschaft {0} sollte eine Boolesche Instanz sein"},
  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;

  {
    ER_EXCEPTION_CREATING_POOL,
       "Ausnahme, die neue Instanz f\u00fcr Pool erstellt"},
  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "Pfad enth\u00e4lt ung\u00fcltige Escape-Sequenz"},
  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;

  {
    ER_SCHEME_REQUIRED,
       "Schema ist erforderlich!"},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;

  {
    ER_NO_SCHEME_IN_URI,
       "Kein Schema gefunden in URI: {0}"},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;

  {
    ER_NO_SCHEME_INURI,
       "Kein Schema gefunden in URI"},
  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;

  {
    ER_PATH_INVALID_CHAR,
       "Pfad enth\u00e4lt ung\u00fcltiges Zeichen: {0}"},
  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  {
    ER_SCHEME_FROM_NULL_STRING,
       "Schema kann ausgehend von Null-Zeichenkette nicht gesetzt werden"},
  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  {
    ER_SCHEME_NOT_CONFORMANT,
       "Schema ist nicht konform."},
  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "Hostadresse nicht korrekt gebildet"},
  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;

  {
    ER_PORT_WHEN_HOST_NULL,
       "Port kann nicht gesetzt werden, wenn Host Null ist"},
  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;

  {
    ER_INVALID_PORT,
       "Ung\u00fcltige Port-Nummer"},
  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  {
    ER_FRAG_FOR_GENERIC_URI,
       "Fragment kann nur f\u00fcr einen generischen URI gesetzt werden"},
  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  {
    ER_FRAG_WHEN_PATH_NULL,
       "Fragment kann nicht gesetzt werden, wenn der Pfad Null ist"},
  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;

  {
    ER_FRAG_INVALID_CHAR,
       "Fragment enth\u00e4lt ung\u00fcltiges Zeichen"},
  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;

  {
    ER_PARSER_IN_USE,
        "Parser wird bereits verwendet"},
  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "{0} {1} kann beim Parsen nicht ge\u00e4ndert werden"},
  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "Selbst-Kausalit\u00e4t nicht erlaubt"},
  
   /* This key/message changed ,NEED ER_COULD_NOT_FIND_EXTERN_SCRIPT: Pending,Ramesh */
   /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
       "Auf das externe Skript unter {0} konnte nicht zugegriffen werden."},
  
  /** The resource [] could not be found     */
  //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  {
    ER_RESOURCE_COULD_NOT_FIND,
        "Die Ressource [ {0} ] wurde nicht gefunden.\n {1}"},
  
   /** output property not recognized:  */
  //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  {
    ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "Ausgabe-Eigenschaft nicht erkannt: {0}"},
  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  {
    ER_NO_USERINFO_IF_NO_HOST,
        "Userinfo kann nicht angegeben werden, wenn Host nicht angegeben ist"},
  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;

  {
    ER_NO_PORT_IF_NO_HOST,
        "Port kann nicht angegeben werden, wenn Host nicht angegeben ist"},
  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  {
    ER_NO_QUERY_STRING_IN_PATH,
        "Abfragezeichenkette kann nicht sowohl im Pfad als auch in der Abfragezeichenkette angegeben werden"},
  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "Fragment kann nicht sowohl im Pfad als auch im Fragment angegeben werden"},
  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "URI kann nicht mit leeren Parametern initialisiert werden"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  {
    ER_FAILED_CREATING_ELEMLITRSLT,
        "Erstellen von ElemLiteralResult-Instanz fehlgeschlagen"},
  
  //XALAN_MANTIS CHANGES: Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed. - nb.

   /** Priority value does not contain a parsable number   */
  //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;

  {
    ER_VALUE_SHOULD_BE_NUMBER,
        "Der Wert f\u00fcr {0} muss eine Nummer darstellen, die geparst werden kann."},
  
   /**  Value for {0} should equal 'yes' or 'no'   */
  //public static final int ER_VALUE_SHOULD_EQUAL = 205;

  {
    ER_VALUE_SHOULD_EQUAL,
        " Wert f\u00fcr {0} sollte Ja oder Nein sein"},
 
   /**  Failed calling {0} method   */
  //public static final int ER_FAILED_CALLING_METHOD = 206;

  {
    ER_FAILED_CALLING_METHOD,
        " Aufruf der Methode {0} fehlgeschlagen"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  {
    ER_FAILED_CREATING_ELEMTMPL,
        "Erstellen von ElemTemplateElement-Instanz fehlgeschlagen"},
  
   /**  Characters are not allowed at this point in the document   */
  //public static final int ER_CHARS_NOT_ALLOWED = 208;

  {
    ER_CHARS_NOT_ALLOWED,
        "Zeichen an dieser Stelle im Dokument nicht erlaubt"},
  
  /**  attribute is not allowed on the element   */
  //public static final int ER_ATTR_NOT_ALLOWED = 209;

  {
    ER_ATTR_NOT_ALLOWED,
        "\"{0}\" Attribut ist nicht erlaubt f\u00fcr Element {1}!"},
  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;

  {
    ER_METHOD_NOT_SUPPORTED,
        "Methode noch nicht unterst\u00fctzt "},
 
  /**  Bad value    */
  //public static final int ER_BAD_VALUE = 211;

  {
    ER_BAD_VALUE,
     "{0} ung\u00fcltiger Wert {1} "},
  
  /**  attribute value not found   */
  //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  {
    ER_ATTRIB_VALUE_NOT_FOUND,
     "{0} Attributwert nicht gefunden"},
  
  /**  attribute value not recognized    */
  //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  {
    ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "{0} Attributwert nicht erkannt "},

  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter kann zurzeit nicht neu gestartet werden"},
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader nicht vor startParse-Anforderung"},
  
    /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;

  {
    ER_NULL_URI_NAMESPACE,
     "Es wurde versucht, einen Namensraum-Pr\u00e4fix ohne URI zu erzeugen."},

  //XALAN_MANTIS CHANGES: Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NUMBER_TOO_BIG = 217;

  {
    ER_NUMBER_TOO_BIG,
     "Es wurde versucht, eine Zahl gr\u00f6\u00dfer als die gr\u00f6\u00dfte lange Ganzzahl zu formatieren."},

//ER_CANNOT_FIND_SAX1_DRIVER

  //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;

  {
    ER_CANNOT_FIND_SAX1_DRIVER,
     "Die SAX1-Treiberklasse {0} kann nicht gefunden werden."},

//ER_SAX1_DRIVER_NOT_LOADED
  //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;

  {
    ER_SAX1_DRIVER_NOT_LOADED,
     "Die SAX1-Treiberklasse {0} wurde gefunden, kann aber nicht geladen werden."},

//ER_SAX1_DRIVER_NOT_INSTANTIATED
  //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;

  {
    ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "Die SAX1-Treiberklasse {0} wurde geladen, es kann aber keine Instanz gebildet werden."},


// ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
  //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;

  {
    ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "Die SAX1-Treiberklasse {0} implementiert org.xml.sax.Parser nicht."},

// ER_PARSER_PROPERTY_NOT_SPECIFIED
  //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;

  {
    ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "Die Systemeigenschaft org.xml.sax.parser wurde nicht angegeben."},

//ER_PARSER_ARG_CANNOT_BE_NULL
  //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;

  {
    ER_PARSER_ARG_CANNOT_BE_NULL,
     "Das Parserargument darf nicht Null sein."},


// ER_FEATURE
  //public static final int  ER_FEATURE = 224;

  {
    ER_FEATURE,
     "Merkmal: {0}"},


// ER_PROPERTY
  //public static final int ER_PROPERTY = 225 ;

  {
    ER_PROPERTY,
     "Eigenschaft: {0}"},

// ER_NULL_ENTITY_RESOLVER
  //public static final int ER_NULL_ENTITY_RESOLVER  = 226;

  {
    ER_NULL_ENTITY_RESOLVER,
     "Entity-Resolver Null"},

// ER_NULL_DTD_HANDLER
  //public static final int  ER_NULL_DTD_HANDLER = 227 ;

  {
    ER_NULL_DTD_HANDLER,
     "DTD-Handler Null"},

// No Driver Name Specified!
  //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;
  {
    ER_NO_DRIVER_NAME_SPECIFIED,
     "Kein Treibername angegeben!"},


// No URL Specified!
  //public static final int ER_NO_URL_SPECIFIED = 229;
  {
    ER_NO_URL_SPECIFIED,
     "Kein URL angegeben!"},


// Pool size is less than 1!
  //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;
  {
    ER_POOLSIZE_LESS_THAN_ONE,
     "Pool-Gr\u00f6\u00dfe ist kleiner als 1!"},


// Invalid Driver Name Specified!
  //public static final int ER_INVALID_DRIVER_NAME = 231;
  {
    ER_INVALID_DRIVER_NAME,
     "Ung\u00fcltiger Treibername angegeben!"},



// ErrorListener
  //public static final int ER_ERRORLISTENER = 232;
  {
    ER_ERRORLISTENER,
     "ErrorListener"},


// Programmer's error! expr has no ElemTemplateElement parent!
  //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;
  {
    ER_ASSERT_NO_TEMPLATE_PARENT,
     "Programmierfehler! Ausdruck weist kein \u00fcbergeordnetes Element ElemTemplateElement auf!"},


// Programmer's assertion in RundundentExprEliminator: {0}
  //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;
  {
    ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "Programmierannahme in RundundentExprEliminator: {0}"},

// Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "Achsen-Traverser nicht unterst\u00fctzt: {0}"},

// ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
  {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "ListingErrorHandler wurde mit Null-PrintWriter erstellt!"},

  // {0}is not allowed in this position in the stylesheet!
  //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;
  {
    ER_NOT_ALLOWED_IN_POSITION,
     "{0} ist an dieser Stelle im Stylesheet nicht zul\u00e4ssig!"},

  // Non-whitespace text is not allowed in this position in the stylesheet!
  //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
  {
    ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "An dieser Stelle im Stylesheet ist nur Leerraum zul\u00e4ssig!"},

  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  //public static final int INVALID_TCHAR = 239;
  // SystemId Unknown
  {
    INVALID_TCHAR,
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das CHAR-Attribut {0} verwendet. Ein Attribut vom Typ CHAR darf nur ein Zeichen aufweisen!"},

  //public static final int ER_SYSTEMID_UNKNOWN = 240;
  {
    ER_SYSTEMID_UNKNOWN,
     "Unbekannte SystemId"},

  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;
  {
    ER_LOCATION_UNKNOWN,
     "Fehler befindet sich an unbekannter Stelle"},

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
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das QNAME-Attribut {0} verwendet."},

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
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das ENUM-Attribut {0} verwendet. Die g\u00fcltigen Werte lauten: {2}."},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NMTOKEN

  // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
  //public static final int INVALID_NMTOKEN = 244;
  {
    INVALID_NMTOKEN,
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das NMTOKEN-Attribut {0} verwendet. "},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NCNAME

  // Illegal value:a {1} used for NCNAME attribute:a {0}.
  //public static final int INVALID_NCNAME = 245;
  {
    INVALID_NCNAME,
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das NCNAME-Attribut {0} verwendet. "},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_BOOLEAN

  // Illegal value:a {1} used for boolean attribute:a {0}.
  //public static final int INVALID_BOOLEAN = 246;

  {
    INVALID_BOOLEAN,
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das boolean-Attribut {0} verwendet. "},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NUMBER

  // Illegal value:a {1} used for number attribute:a {0}.
  //public static final int INVALID_NUMBER = 247;
  {
    INVALID_NUMBER,
     "Ung\u00fcltiger Wert: {1} wurde f\u00fcr das number-Attribut {0} verwendet. "},


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
     "Das Argument f\u00fcr {0} im Muster muss ein Literal sein."},

// Note to translators:  The following message indicates that two definitions of
// a variable.  A "global variable" is a variable that is accessible everywher
// in the stylesheet.
// ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790

  // Duplicate global variable declaration.
  //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;
  {
    ER_DUPLICATE_GLOBAL_VAR,
     "Doppelte Deklaration einer globalen Variablen."},


// Note to translators:  The following message indicates that two definitions of
// a variable were encountered.
// ER_DUPLICATE_VAR - new error message for bugzilla #790

  // Duplicate variable declaration.
  //public static final int ER_DUPLICATE_VAR           = 250;
  {
    ER_DUPLICATE_VAR,
     "Doppelte Deklaration einer Variablen."},

    // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
    // which must not be translated.
    // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789

  // xsl:template must have a name or match attribute (or both)
  //public static final int ER_TEMPLATE_NAME_MATCH     = 251;
  {
    ER_TEMPLATE_NAME_MATCH,
     "Das Element xsl:template muss ein name- oder ein match-Attribut (oder beide) aufweisen."},

    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788

  // Prefix in exclude-result-prefixes is not valid:a {0}
  //public static final int ER_INVALID_PREFIX          = 252;
  {
    ER_INVALID_PREFIX,
     "Pr\u00e4fix in exclude-result-prefixes ist nicht g\u00fcltig: {0}"},

    // Note to translators:  An "attribute set" is a set of attributes that can be
    // added to an element in the output document as a group.  The message indicates
    // that there was a reference to an attribute set named {0} that was never
    // defined.
    // ER_NO_ATTRIB_SET - new error message for bugzilla #782

  // attribute-set named {0} does not exist
  //public static final int ER_NO_ATTRIB_SET           = 253;
  {
    ER_NO_ATTRIB_SET,
     "Das Attributset mit dem Namen {0} ist nicht vorhanden."},


  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  //public static final int WG_FOUND_CURLYBRACE = 1;

  {
    WG_FOUND_CURLYBRACE,
      "'}' gefunden, aber keine Attributvorlage ge\u00f6ffnet!"},

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  {
    WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "Warnung: ''count''-Attribute passt zu keinem Vorg\u00e4ngerknoten in xsl:number! Ziel = {0}"},

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  {
    WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "Alte Syntax: Der Name des 'expr'-Attributs ist zu 'select' ge\u00e4ndert worden."},

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  {
    WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan bearbeitet den lokalen Namen in der Funktion format-number noch nicht."},

  /** WG_LOCALE_NOT_FOUND          */
  //public static final int WG_LOCALE_NOT_FOUND = 5;

  {
    WG_LOCALE_NOT_FOUND,
      "Warnung: Locale f\u00fcr xml:lang={0} nicht gefunden"},

  /** WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  {
    WG_CANNOT_MAKE_URL_FROM,
      "URL kann nicht erstellt werden aus: {0}"},

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  {
    WG_CANNOT_LOAD_REQUESTED_DOC,
      "Angefordertes Dokument kann nicht geladen werden: {0}"},

  /** WG_CANNOT_FIND_COLLATOR          */
  //public static final int WG_CANNOT_FIND_COLLATOR = 8;

  {
    WG_CANNOT_FIND_COLLATOR,
      "Collator f\u00fcr <sort xml:lang={0} nicht gefunden"},

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  {
    WG_FUNCTIONS_SHOULD_USE_URL,
      "Alte Syntax: Die Funktionsanweisung sollten einen URL von {0} verwenden"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "Codierung nicht unterst\u00fctzt: {0}, UTF-8 wird verwendet"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "Codierung nicht unterst\u00fctzt: {0}, Java {1} wird verwendet"},

  /** WG_SPECIFICITY_CONFLICTS          */
  //public static final int WG_SPECIFICITY_CONFLICTS = 12;

  {
    WG_SPECIFICITY_CONFLICTS,
      "Spezifit\u00e4tskonflikte gefunden: {0} Zuletzt in Stylesheet gefundenes wird verwendet."},

  /** WG_PARSING_AND_PREPARING          */
  //public static final int WG_PARSING_AND_PREPARING = 13;

  {
    WG_PARSING_AND_PREPARING,
      "========= Parsen und Vorbereiten {0} =========="},

  /** WG_ATTR_TEMPLATE          */
  //public static final int WG_ATTR_TEMPLATE = 14;

  {
    WG_ATTR_TEMPLATE, "Attributvorlage, {0}"},

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;

  {
    WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "Match-Konflikt zwischen xsl:strip-space und xsl:preserve-space"},

  /** WG_ATTRIB_NOT_HANDLED          */
  //public static final int WG_ATTRIB_NOT_HANDLED = 16;

  {
    WG_ATTRIB_NOT_HANDLED,
      "Xalan bearbeitet das Attribut {0} noch nicht!"},

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  {
    WG_NO_DECIMALFORMAT_DECLARATION,
      "Keine Vereinbarung f\u00fcr Dezimalformat gefunden: {0}"},

  /** WG_OLD_XSLT_NS          */
  //public static final int WG_OLD_XSLT_NS = 18;

  {
    WG_OLD_XSLT_NS, "XSLT-Namensraum fehlt oder ist nicht korrekt. "},

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  {
    WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "Nur eine Standardvereinbarung xsl:decimal-format ist erlaubt."},

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  {
    WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "xsl:decimal-format-Namen m\u00fcssen eindeutig sein. Name \"{0}\" ist nicht eindeutig."},

  /** WG_ILLEGAL_ATTRIBUTE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  {
    WG_ILLEGAL_ATTRIBUTE,
      "{0} hat ein ung\u00fcltiges Attribut: {1}"},

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  {
    WG_COULD_NOT_RESOLVE_PREFIX,
      "Namensraum-Pr\u00e4fix konnte nicht aufgel\u00f6st werden: {0}. Der Knoten wird ignoriert."},

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  {
    WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet erfordert ein 'version'-Attribut!"},

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  {
    WG_ILLEGAL_ATTRIBUTE_NAME,
      "Ung\u00fcltiger Attributname: {0}"},

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  {
    WG_ILLEGAL_ATTRIBUTE_VALUE,
      "Ung\u00fcltiger Wert f\u00fcr Attribut {0}: {1}"},

  /** WG_EMPTY_SECOND_ARG          */
  //public static final int WG_EMPTY_SECOND_ARG = 26;

  {
    WG_EMPTY_SECOND_ARG,
      "Resultierendes Knotenset aus zweitem Argument von document-Funktion ist leer. Das erste Argument wird verwendet."},

   //XALAN_MANTIS CHANGES: Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML


  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;
  {
    WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "Der Wert des name-Attributs von xsl:processing-instruction darf nicht 'xml' lauten."},

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME

  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;
  {
    WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "Der Wert des name-Attributs von xsl:processing-instruction muss einem g\u00fcltigen ''NCName'' entsprechen: {0}"},

    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    // WG_ILLEGAL_ATTRIBUTE_POSITION

  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
  //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;
  {
    WG_ILLEGAL_ATTRIBUTE_POSITION,
      "Das Attribut {0} kann nicht nach untergeordneten Elementen oder vor der Erstellung eines Elements hinzugef\u00fcgt werden. Das Attribut wird ignoriert."},

    //XALAN_MANTIS CHANGES: WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?

// Other miscellaneous text used inside the code...
    { "ui_language", "de"},
    { "help_language", "de"},
    { "language", "de"},
    { "BAD_CODE", "Parameter f\u00fcr createMessage au\u00dferhalb der Grenzwerte"},
    { "FORMAT_FAILED",
      "Ausnahme bei messageFormat-Aufruf"},
    { "version", ">>>>>>> Xalan Version "},
    { "version2", "<<<<<<<"},
    { "yes", "ja"},
    { "line", "Zeile #"},
    { "column", "Spalte #"},
    { "xsldone",  "XSLProcessor: fertig"},
    { "xslProc_option",  "Optionen der Klasse Process in Xalan-J-Befehlszeile:"},
  { "xslProc_invalid_xsltc_option", "Die Option {0} wird im XSLTC-Modus nicht unterst\u00fctzt."},
  { "xslProc_invalid_xalan_option", "Die Option {0} kann nur zusammen mit -XSLTC verwendet werden."},
  { "xslProc_no_input", "Fehler: Kein Stylesheet oder keine XML-Eingabe angegeben. F\u00fchren Sie diesen Befehl ohne Option aus, um Anweisungen zur Verwendung zu erhalten."},
  { "xslProc_common_options", "-Allgemeine Optionen-"},
  { "xslProc_xalan_options", "-Optionen f\u00fcr Xalan-"},
  { "xslProc_xsltc_options", "-Optionen f\u00fcr XSLTC-"},
  { "xslProc_return_to_continue", "(Dr\u00fccken Sie die Eingabetaste, um fortzufahren.)"},

  { "optionXSLTC", "   [-XSLTC (XSLTC f\u00fcr die Transformation verwenden)]"},
    { "optionIN", "    -IN inputXMLURL"},
    { "optionXSL", "   [-XSL XSLTransformationURL]"},
    { "optionOUT",  "   [-OUT outputFileName]"},
    { "optionLXCIN", "   [-LXCIN compiledStylesheetFileNameIn]"},
    { "optionLXCOUT", "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
    { "optionPARSER", "   [-PARSER voll qualifizierter Klassenname von Parserliaison]"},
    { "optionE",  "   [-E (Entity-Referenzen nicht erweitern)]"},
    { "optionV", "   [-E (Entity-Referenzen nicht erweitern)]"},
    { "optionQC", "   [-QC (Ger\u00e4uscharme Warnungen bei Musterkonflikten)]"},
    { "optionQ", "   [-Q  (Ger\u00e4uschmer Modus)]"},
    { "optionLF", "   [-LF (bei Ausgabe nur Zeilenvorsprung {Standard ist CR/LF})]"},
    { "optionCR", "   [-CR (bei Ausgabe nur Wagenr\u00fccklauf {Standard ist CR/LF})]"},
    { "optionESCAPE", "   [-ESCAPE (Zeichen f\u00fcr Escape {Standard ist <>&\"\'\\r\\n}]"},
    {  "optionINDENT", "   [-INDENT (Anzahl der Leerzeichen zum Einr\u00fccken {Standard ist 0})]"},
    { "optionTT", "   [-TT (Vorlagen beim Aufruf verfolgen.)]"},
    { "optionTG", "   [-TG (Jedes Erzeugungsereignis verfolgen.)]"},
    { "optionTS", "   [-TS (Jedes Auswahlereignis verfolgen.)]"},
    { "optionTTC", "   [-TTC (Die Vorlagen-Tochterknoten bei Bearbeitung verfolgen.)]"},
    { "optionTCLASS", "   [-TCLASS (TraceListener-Klasse f\u00fcr Trace-Erweiterungen.)]"},
    { "optionVALIDATE", "   [-VALIDATE (Festlegen, ob Validierung stattfindet. Standard ist keine Validierung.)]"},
    { "optionEDUMP", "   [-EDUMP {optionaler Dateiname} (Speicherauszug bei Fehler.)]"},
    { "optionXML", "   [-XML (XML-Formatierer verwenden und XML-Header hinzuf\u00fcgen.)]"},
    { "optionTEXT", "   [-TEXT (Einfachen Textformatierer verwenden.)]"},
    { "optionHTML", "   [-HTML (HTML-Formatierer verwenden.)]"},
    { "optionPARAM", "   [-PARAM Namensausdruck (Stylesheet-Parameter festlegen)]"},
    { "noParsermsg1",  "XSL-Prozess fehlgeschlagen."},
    { "noParsermsg2",  "** Parser nicht gefunden **"},
    { "noParsermsg3",  "Bitte Classpath \u00fcberpr\u00fcfen."},
    { "noParsermsg4", "Wenn Sie IBMs XML Parser for Java nicht haben, k\u00f6nnen Sie ihn von folgender Adresse herunterladen"},
    { "noParsermsg5",
	"IBMs AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
    { "optionURIRESOLVER",
     "   [-URIRESOLVER vollst\u00e4ndiger Klassenname (zum Aufl\u00f6sen von URIs zu verwendender URIResolver)]"},
    { "optionENTITYRESOLVER",
     "   [-ENTITYRESOLVER vollst\u00e4ndiger Klassenname (zum Aufl\u00f6sen von Entities zu verwendender EntityResolver)]"},
    { "optionCONTENTHANDLER",
     "   [-CONTENTHANDLER vollst\u00e4ndiger Klassenname (zum Serialisieren der Ausgabe zu verwendender ContentHandler)]"},
    { "optionLINENUMBERS",
     "   [-L Zeilennummern f\u00fcr Quelldokument verwenden]"},
		
    //XALAN_MANTIS CHANGES: Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


    { "optionMEDIA",
     " [-MEDIA Medientyp (media-Attribut zum Auffinden des einem Dokument zugeordneten Stylesheets verwenden)]"},
    { "optionFLAVOR",
     " [-FLAVOR Variantenname(Ausdr\u00fccklich s2s=SAX oder d2d=DOM f\u00fcr die Transformation verwenden)] "}, // Added by sboag/scurcuru; experimental
    { "optionDIAG",
     " [-DIAG (Zeitdauer der Transformation in Millisekunden ausgeben)]"},
    { "optionINCREMENTAL",
     " [-INCREMENTAL (Inkrementellen DTM-Aufbau anfordern, indem http://xml.apache.org/xalan/features/incremental auf 'Wahr' gesetzt wird)]"},
    { "optionNOOPTIMIMIZE",
     " [-NOOPTIMIMIZE (Keine Optimierung des Stylesheets durchf\u00fchren, indem http://xml.apache.org/xalan/features/optimize auf 'Falsch' gesetzt wird)]"},
    { "optionRL",
     " [-RL Rekursionsgrenze (Numerische Begrenzung der Rekursionstiefe f\u00fcr das Stylesheet)]"},
    { "optionXO",
     " [-XO [Translet-Name] (Zuweisen eines Namens zum erzeugten Translet)]"},
    { "optionXD",
     " [-XD Zielverzeichnis (Angabe eines Zielverzeichnisses f\u00fcr das Translet)]"},
    { "optionXJ",
     " [-XJ JAR-Datei (Erstellt ein Paket mit den Translet-Klassen in einer JAR-Datei mit dem Namen <JAR-Datei>)]"},
    {  "optionXP",
     " [-XP Paket (Angabe eines Paketnamen-Pr\u00e4fixes f\u00fcr alle erzeugten Translet-Klassen)]"},
  { "optionXN",  "   [-XN (Vorlagen nicht in separate Prozeduren auslagern)]" },
  { "optionXX",  "   [-XX (Zus\u00e4tzliche Debugging-Nachrichten ausgeben)]"},
  { "optionXT" , "   [-XT (Translet f\u00fcr die Transformation verwenden, sofern m\u00f6glich)]"},
  { "diagTiming"," --------- Zeitdauer der Transformation von {0} \u00fcber {1}: {2} ms" },
  { "recursionTooDeep","Die Vorlagen sind zu tief verschachtelt. Verschachtelung = {0}, Vorlage {1} {2}" },
  { "nameIs", "Name ist" },
  { "matchPatternIs", "Suchmuster ist" }

  };

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#Fehler";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Fehler: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Warnung: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "MUSTER ";

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




