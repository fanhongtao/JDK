/*
* @(#)XSLTErrorResources_it.java	1.8 02/03/26
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

public class XSLTErrorResources_it extends XSLTErrorResources
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
     "Errore: Impossibile inserire '{' nell'espressione."},


 /** ER_ILLEGAL_ATTRIBUTE          */
 //public static final int ER_ILLEGAL_ATTRIBUTE = 2;


 {
   ER_ILLEGAL_ATTRIBUTE, "{0} ha un attributo illegale: {1}."},


 /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
 //public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;


 {
   ER_NULL_SOURCENODE_APPLYIMPORTS,
     "sourceNode nullo in xsl:apply-imports"},


 /** ER_CANNOT_ADD          */
 //public static final int ER_CANNOT_ADD = 4;


 {
   ER_CANNOT_ADD, "Impossibile aggiungere {0} a {1}."},


 /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
 //public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;


 {
   ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
     "sourceNode nullo in handleApplyTemplatesInstruction."},


 /** ER_NO_NAME_ATTRIB          */
 //public static final int ER_NO_NAME_ATTRIB = 6;


 {
   ER_NO_NAME_ATTRIB, "{0} deve avere un attributo nome."},


 /** ER_TEMPLATE_NOT_FOUND          */
 //public static final int ER_TEMPLATE_NOT_FOUND = 7;


 {
   ER_TEMPLATE_NOT_FOUND, "Impossibile trovare il modello denominato: {0}."},


 /** ER_CANT_RESOLVE_NAME_AVT          */
 //public static final int ER_CANT_RESOLVE_NAME_AVT = 8;


 {
   ER_CANT_RESOLVE_NAME_AVT,
     "Impossibile risolvere il nome AVT in xsl:call-template."},


 /** ER_REQUIRES_ATTRIB          */
 //public static final int ER_REQUIRES_ATTRIB = 9;


 {
   ER_REQUIRES_ATTRIB, "{0} richiede l'attributo: {1}."},


 /** ER_MUST_HAVE_TEST_ATTRIB          */
 //public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;


 {
   ER_MUST_HAVE_TEST_ATTRIB,
     "{0} deve avere un attributo ''test''."},


 /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
 //public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;


 {
   ER_BAD_VAL_ON_LEVEL_ATTRIB,
     "Valore non valido su attributo livello: {0}"},


 /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
 //public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;


 {
   ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
     "Il nome dell'istruzione di elaborazione non pu\u00f2 essere 'xml'."},


 /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
 //public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;


 {
   ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
     "Il nome dell'istruzione di elaborazione deve essere un NCName valido: {0}."},


 /** ER_NEED_MATCH_ATTRIB          */
 //public static final int ER_NEED_MATCH_ATTRIB = 14;


 {
   ER_NEED_MATCH_ATTRIB,
     "{0} deve avere un attributo corrispondenza se ha una modalit\u00e0."},


 /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
 //public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;


 {
   ER_NEED_NAME_OR_MATCH_ATTRIB,
     "{0} richiede un attributo nome o corrispondenza."},


 /** ER_CANT_RESOLVE_NSPREFIX          */
 //public static final int ER_CANT_RESOLVE_NSPREFIX = 16;


 {
   ER_CANT_RESOLVE_NSPREFIX,
     "Impossibile risolvere il prefisso namespace: {0}."},


 /** ER_ILLEGAL_VALUE          */
 //public static final int ER_ILLEGAL_VALUE = 17;


 {
   ER_ILLEGAL_VALUE, "xml:space ha valore non valido: {0}."},


 /** ER_NO_OWNERDOC          */
 //public static final int ER_NO_OWNERDOC = 18;


 {
   ER_NO_OWNERDOC,
     "Il nodo secondario non ha alcun documento di propriet\u00e0."},


 /** ER_ELEMTEMPLATEELEM_ERR          */
 //public static final int ER_ELEMTEMPLATEELEM_ERR = 19;


 {
   ER_ELEMTEMPLATEELEM_ERR, "Errore ElemTemplateElement: {0}."},


 /** ER_NULL_CHILD          */
 //public static final int ER_NULL_CHILD = 20;


 {
   ER_NULL_CHILD, "Tentativo di aggiungere un elemento secondario nullo."},


 /** ER_NEED_SELECT_ATTRIB          */
 //public static final int ER_NEED_SELECT_ATTRIB = 21;


 {
   ER_NEED_SELECT_ATTRIB, "{0} richiede un attributo selezione."},


 /** ER_NEED_TEST_ATTRIB          */
 //public static final int ER_NEED_TEST_ATTRIB = 22;


 {
   ER_NEED_TEST_ATTRIB,
     "xsl:when deve avere un attributo 'test'."},


 /** ER_NEED_NAME_ATTRIB          */
 //public static final int ER_NEED_NAME_ATTRIB = 23;


 {
   ER_NEED_NAME_ATTRIB,
     "xsl:with-param deve avere un attributo 'name'."},


 /** ER_NO_CONTEXT_OWNERDOC          */
 //public static final int ER_NO_CONTEXT_OWNERDOC = 24;


 {
   ER_NO_CONTEXT_OWNERDOC,
     "Il contesto non ha un documento di propriet\u00e0."},


 /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
 //public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;


 {
   ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
     "Impossibile creare un XML TransformerFactory Liaison: {0}"},


 /** ER_PROCESS_NOT_SUCCESSFUL          */
 //public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;


 {
   ER_PROCESS_NOT_SUCCESSFUL,
     "Xalan: il processo non \u00e8 riuscito."},


 /** ER_NOT_SUCCESSFUL          */
 //public static final int ER_NOT_SUCCESSFUL = 27;


 {
   ER_NOT_SUCCESSFUL, "Xalan: non \u00e8 riuscito."},


 /** ER_ENCODING_NOT_SUPPORTED          */
 //public static final int ER_ENCODING_NOT_SUPPORTED = 28;


 {
   ER_ENCODING_NOT_SUPPORTED, "Codifica non supportata: {0}"},


 /** ER_COULD_NOT_CREATE_TRACELISTENER          */
 //public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;


 {
   ER_COULD_NOT_CREATE_TRACELISTENER,
     "Impossibile creare TraceListener: {0}"},


 /** ER_KEY_REQUIRES_NAME_ATTRIB          */
 //public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;


 {
   ER_KEY_REQUIRES_NAME_ATTRIB,
     "xsl:key richiede un attributo 'nome'."},


 /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
 //public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;


 {
   ER_KEY_REQUIRES_MATCH_ATTRIB,
     "xsl:key richiede un attributo 'corrispondenza'."},


 /** ER_KEY_REQUIRES_USE_ATTRIB          */
 //public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;


 {
   ER_KEY_REQUIRES_USE_ATTRIB,
     "xsl:key richiede un attributo 'uso'."},


 /** ER_REQUIRES_ELEMENTS_ATTRIB          */
 //public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;


 {
   ER_REQUIRES_ELEMENTS_ATTRIB,
     "(StylesheetHandler) {0} richiede un attributo ''elementi''."},


 /** ER_MISSING_PREFIX_ATTRIB          */
 //public static final int ER_MISSING_PREFIX_ATTRIB = 34;


 {
   ER_MISSING_PREFIX_ATTRIB,
     "(StylesheetHandler) {0} attributo ''prefisso'' mancante"},


 /** ER_BAD_STYLESHEET_URL          */
 //public static final int ER_BAD_STYLESHEET_URL = 35;


 {
   ER_BAD_STYLESHEET_URL, "URL del foglio di stile non valido: {0}"},


 /** ER_FILE_NOT_FOUND          */
 //public static final int ER_FILE_NOT_FOUND = 36;


 {
   ER_FILE_NOT_FOUND, "File del foglio di stile non trovato: {0}"},


 /** ER_IOEXCEPTION          */
 //public static final int ER_IOEXCEPTION = 37;


 {
   ER_IOEXCEPTION,
     "Rilevata eccezione IO con il file del foglio di stile: {0}"},


 /** ER_NO_HREF_ATTRIB          */
 //public static final int ER_NO_HREF_ATTRIB = 38;


 {
   ER_NO_HREF_ATTRIB,
     "(StylesheetHandler) Impossibile trovare l'attributo href per {0}"},


 /** ER_STYLESHEET_INCLUDES_ITSELF          */
 //public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;


 {
   ER_STYLESHEET_INCLUDES_ITSELF,
     "(StylesheetHandler) {0} include se stesso direttamente o indirettamente."},


 /** ER_PROCESSINCLUDE_ERROR          */
 //public static final int ER_PROCESSINCLUDE_ERROR = 40;


 {
   ER_PROCESSINCLUDE_ERROR,
     "Errore StylesheetHandler.processInclude, {0}"},


 /** ER_MISSING_LANG_ATTRIB          */
 //public static final int ER_MISSING_LANG_ATTRIB = 41;


 {
   ER_MISSING_LANG_ATTRIB,
     "(StylesheetHandler) {0} attributo ''lang'' mancante"},


 /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
 //public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;


 {
   ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
     "(StylesheetHandler) elemento {0} fuori posto? ''Componente'' dell'elemento contenitore mancante"},


 /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
 //public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;


 {
   ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
     "\u00c8 possibile eseguire l'output solo in Element, DocumentFragment, Document o PrintWriter."},


 /** ER_PROCESS_ERROR          */
 //public static final int ER_PROCESS_ERROR = 44;


 {
   ER_PROCESS_ERROR, "Errore in StylesheetRoot.process"},


 /** ER_UNIMPLNODE_ERROR          */
 //public static final int ER_UNIMPLNODE_ERROR = 45;


 {
   ER_UNIMPLNODE_ERROR, "Errore in UnImplNode: {0}"},


 /** ER_NO_SELECT_EXPRESSION          */
 //public static final int ER_NO_SELECT_EXPRESSION = 46;


 {
   ER_NO_SELECT_EXPRESSION,
     "Errore. L'espressione di selezione del percorso (-select) non \u00e8 stata trovata."},


 /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
 //public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;


 {
   ER_CANNOT_SERIALIZE_XSLPROCESSOR,
     "Impossibile serializzare un XSLProcessor!"},


 /** ER_NO_INPUT_STYLESHEET          */
 //public static final int ER_NO_INPUT_STYLESHEET = 48;


 {
   ER_NO_INPUT_STYLESHEET,
     "Input del foglio di stile non specificato."},


 /** ER_FAILED_PROCESS_STYLESHEET          */
 //public static final int ER_FAILED_PROCESS_STYLESHEET = 49;


 {
   ER_FAILED_PROCESS_STYLESHEET,
     "Elaborazione del foglio di stile non riuscita."},


 /** ER_COULDNT_PARSE_DOC          */
 //public static final int ER_COULDNT_PARSE_DOC = 50;


 {
   ER_COULDNT_PARSE_DOC, "Impossibile analizzare il documento {0}."},


 /** ER_COULDNT_FIND_FRAGMENT          */
 //public static final int ER_COULDNT_FIND_FRAGMENT = 51;


 {
   ER_COULDNT_FIND_FRAGMENT, "Impossibile trovare il frammento: {0}"},


 /** ER_NODE_NOT_ELEMENT          */
 //public static final int ER_NODE_NOT_ELEMENT = 52;


 {
   ER_NODE_NOT_ELEMENT,
     "Il nodo a cui puntava l'identificatore del frammento non era un elemento: {0}"},


 /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
 //public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;


 {
   ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
     "for-each deve avere un attributo corrispondenza o nome."},


 /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
 //public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;


 {
   ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
     "I modelli devono avere un attributo corrispondenza o nome."},


 /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
 //public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;


 {
   ER_NO_CLONE_OF_DOCUMENT_FRAG,
     "Nessun duplicato di frammento di un documento."},


 /** ER_CANT_CREATE_ITEM          */
 //public static final int ER_CANT_CREATE_ITEM = 56;


 {
   ER_CANT_CREATE_ITEM,
     "Impossibile creare un elemento nell'albero del risultato: {0}"},


 /** ER_XMLSPACE_ILLEGAL_VALUE          */
 //public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;


 {
   ER_XMLSPACE_ILLEGAL_VALUE,
     "xml:space nell'XML sorgente ha valore non valido: {0}"},


 /** ER_NO_XSLKEY_DECLARATION          */
 //public static final int ER_NO_XSLKEY_DECLARATION = 58;


 {
   ER_NO_XSLKEY_DECLARATION,
     "Dichiarazione xsl:key mancante per {0}!"},


 /** ER_CANT_CREATE_URL          */
 //public static final int ER_CANT_CREATE_URL = 59;


 {
   ER_CANT_CREATE_URL, "Errore. Impossibile creare URL per: {0}"},


 /** ER_XSLFUNCTIONS_UNSUPPORTED          */
 //public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;


 {
   ER_XSLFUNCTIONS_UNSUPPORTED, "xsl:functions non supportato"},


 /** ER_PROCESSOR_ERROR          */
 //public static final int ER_PROCESSOR_ERROR = 61;


 {
   ER_PROCESSOR_ERROR, "Errore XSLT TransformerFactory"},


 /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
 //public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;


 {
   ER_NOT_ALLOWED_INSIDE_STYLESHEET,
     "(StylesheetHandler) {0} non consentito in un foglio di stile."},


 /** ER_RESULTNS_NOT_SUPPORTED          */
 //public static final int ER_RESULTNS_NOT_SUPPORTED = 63;


 {
   ER_RESULTNS_NOT_SUPPORTED,
     "result-ns non \u00e8 pi\u00f9 supportato. Utilizzare xsl:output."},


 /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
 //public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;


 {
   ER_DEFAULTSPACE_NOT_SUPPORTED,
     "default-space non \u00e8 pi\u00f9 supportato. Utilizzare xsl:strip-space o xsl:preserve-space."},


 /** ER_INDENTRESULT_NOT_SUPPORTED          */
 //public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;


 {
   ER_INDENTRESULT_NOT_SUPPORTED,
     "indent-result non \u00e8 pi\u00f9 supportato. Utilizzare xsl:output."},


 /** ER_ILLEGAL_ATTRIB          */
 //public static final int ER_ILLEGAL_ATTRIB = 66;


 {
   ER_ILLEGAL_ATTRIB,
     "(StylesheetHandler) {0} ha un attributo non valido {1}"},


 /** ER_UNKNOWN_XSL_ELEM          */
 //public static final int ER_UNKNOWN_XSL_ELEM = 67;


 {
   ER_UNKNOWN_XSL_ELEM, "Elemento XSL sconosciuto: {0}"},


 /** ER_BAD_XSLSORT_USE          */
 //public static final int ER_BAD_XSLSORT_USE = 68;


 {
   ER_BAD_XSLSORT_USE,
     "(StylesheetHandler) xsl:sort pu\u00f2 essere utilizzato solo con xsl:apply-templates o xsl:for-each."},


 /** ER_MISPLACED_XSLWHEN          */
 //public static final int ER_MISPLACED_XSLWHEN = 69;


 {
   ER_MISPLACED_XSLWHEN,
     "(StylesheetHandler) xsl:when fuori posto."},


 /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
 //public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;


 {
   ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
     "(StylesheetHandler) xsl:when non dipende da xsl:choose!"},


 /** ER_MISPLACED_XSLOTHERWISE          */
 //public static final int ER_MISPLACED_XSLOTHERWISE = 71;


 {
   ER_MISPLACED_XSLOTHERWISE,
     "(StylesheetHandler) xsl:otherwise fuori posto."},


 /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
 //public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;


 {
   ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
     "(StylesheetHandler) xsl:otherwise non dipende da xsl:choose."},


 /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
 //public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;


 {
   ER_NOT_ALLOWED_INSIDE_TEMPLATE,
     "(StylesheetHandler) {0} non \u00e8 consentito in un modello."},


 /** ER_UNKNOWN_EXT_NS_PREFIX          */
 //public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;


 {
   ER_UNKNOWN_EXT_NS_PREFIX,
     "(StylesheetHandler) {0} prefisso namespace di estensione {1} sconosciuto"},


 /** ER_IMPORTS_AS_FIRST_ELEM          */
 //public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;


 {
   ER_IMPORTS_AS_FIRST_ELEM,
     "(StylesheetHandler) Le importazioni sono possibili solo come primi elementi di un foglio di stile."},


 /** ER_IMPORTING_ITSELF          */
 //public static final int ER_IMPORTING_ITSELF = 76;


 {
   ER_IMPORTING_ITSELF,
     "(StylesheetHandler) {0} sta importando se stesso direttamente o indirettamente."},


 /** ER_XMLSPACE_ILLEGAL_VAL          */
 //public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;


 {
   ER_XMLSPACE_ILLEGAL_VAL,
     "(StylesheetHandler) " + "xml:space ha valore non valido: {0}"},


 /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
 //public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;


 {
   ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
     "processStylesheet non \u00e8 riuscito."},


 /** ER_SAX_EXCEPTION          */
 //public static final int ER_SAX_EXCEPTION = 79;


 {
   ER_SAX_EXCEPTION, "Eccezione SAX"},


 /** ER_FUNCTION_NOT_SUPPORTED          */
 //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


 {
   ER_FUNCTION_NOT_SUPPORTED, "Funzione non supportata"},


 /** ER_XSLT_ERROR          */
 //public static final int ER_XSLT_ERROR = 81;


 {
   ER_XSLT_ERROR, "Errore XSLT"},


 /** ER_CURRENCY_SIGN_ILLEGAL          */
 //public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;


 {
   ER_CURRENCY_SIGN_ILLEGAL,
     "Il segno di valuta non \u00e8 consentito nelle stringhe modello di formato."},


 /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
 //public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;


 {
   ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
     "Funzione documento non supportata nel foglio di stile DOM!"},


 /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
 //public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;


 {
   ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
     "Impossibile risolvere il prefisso del risolutore non-Prefix."},


 /** ER_REDIRECT_COULDNT_GET_FILENAME          */
 //public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;


 {
   ER_REDIRECT_COULDNT_GET_FILENAME,
     "Estensione di reindirizzamento: Impossibile trovare il nome file. Il file o l'attributo di selezione devono generare una stringa valida."},


 /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
 //public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;


 {
   ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
     "Impossibile generare FormatterListener nell'estensione di reindirizzamento."},


 /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
 //public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;


 {
   ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
     "Il prefisso in exclude-result-prefixes non \u00e8 valido: {0}"},


 /** ER_MISSING_NS_URI          */
 //public static final int ER_MISSING_NS_URI = 88;


 {
   ER_MISSING_NS_URI,
     "URI namespace mancante per il prefisso specificato."},


 /** ER_MISSING_ARG_FOR_OPTION          */
 //public static final int ER_MISSING_ARG_FOR_OPTION = 89;


 {
   ER_MISSING_ARG_FOR_OPTION,
     "Argomento mancante per l'opzione: {0}"},


 /** ER_INVALID_OPTION          */
 //public static final int ER_INVALID_OPTION = 90;


 {
   ER_INVALID_OPTION, "Opzione non valida: {0}"},


 /** ER_MALFORMED_FORMAT_STRING          */
 //public static final int ER_MALFORMED_FORMAT_STRING = 91;


 {
   ER_MALFORMED_FORMAT_STRING, "Stringa di formato non valida: {0}"},


 /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
 //public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;


 {
   ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
     "xsl:stylesheet richiede un attributo 'versione'."},


 /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
 //public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;


 {
   ER_ILLEGAL_ATTRIBUTE_VALUE,
     "Attributo: {0} ha un valore non valido: {1}"},


 /** ER_CHOOSE_REQUIRES_WHEN          */
 //public static final int ER_CHOOSE_REQUIRES_WHEN = 94;


 {
   ER_CHOOSE_REQUIRES_WHEN, "xsl:choose richiede xsl:when"},


 /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
 //public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;


 {
   ER_NO_APPLY_IMPORT_IN_FOR_EACH,
     "xsl:apply-imports non consentito in xsl:for-each"},


 /** ER_CANT_USE_DTM_FOR_OUTPUT          */
 //public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;


 {
   ER_CANT_USE_DTM_FOR_OUTPUT,
     "Impossibile utilizzare un collegamento DTM per un nodo DOM di output. Utilizzare org.apache.xpath.DOM2Helper."},


 /** ER_CANT_USE_DTM_FOR_INPUT          */
 //public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;


 {
   ER_CANT_USE_DTM_FOR_INPUT,
     "Impossibile utilizzare un collegamento DTM per un nodo DOM di input. Utilizzare org.apache.xpath.DOM2Helper."},


 /** ER_CALL_TO_EXT_FAILED          */
 //public static final int ER_CALL_TO_EXT_FAILED = 98;


 {
   ER_CALL_TO_EXT_FAILED,
     "Chiamata all'elemento di estensione non riuscita: {0}"},


 /** ER_PREFIX_MUST_RESOLVE          */
 //public static final int ER_PREFIX_MUST_RESOLVE = 99;


 {
   ER_PREFIX_MUST_RESOLVE,
     "Il prefisso deve risolvere in namespace: {0}"},


 /** ER_INVALID_UTF16_SURROGATE          */
 //public static final int ER_INVALID_UTF16_SURROGATE = 100;


 {
   ER_INVALID_UTF16_SURROGATE,
     "Rilevato surrogato di UTF-16 non valido: {0} ?"},


 /** ER_XSLATTRSET_USED_ITSELF          */
 //public static final int ER_XSLATTRSET_USED_ITSELF = 101;


 {
   ER_XSLATTRSET_USED_ITSELF,
     "xsl:attribute-set {0} ha utilizzato se stesso, generando un loop infinito."},


 /** ER_CANNOT_MIX_XERCESDOM          */
 //public static final int ER_CANNOT_MIX_XERCESDOM = 102;


 {
   ER_CANNOT_MIX_XERCESDOM,
     "Impossibile combinare un input non Xerces-DOM con un input Xerces-DOM."},


 /** ER_TOO_MANY_LISTENERS          */
 //public static final int ER_TOO_MANY_LISTENERS = 103;


 {
   ER_TOO_MANY_LISTENERS,
     "addTraceListenersToStylesheet - TooManyListenersException"},


 /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
 //public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;


 {
   ER_IN_ELEMTEMPLATEELEM_READOBJECT,
     "In ElemTemplateElement.readObject: {0}"},


 /** ER_DUPLICATE_NAMED_TEMPLATE          */
 //public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;


 {
   ER_DUPLICATE_NAMED_TEMPLATE,
     "Trovato pi\u00f9 di un modello denominato: {0}"},


 /** ER_INVALID_KEY_CALL          */
 //public static final int ER_INVALID_KEY_CALL = 106;


 {
   ER_INVALID_KEY_CALL,
     "Chiamata di funzione non valida: le chiamate chiave() ricorsive non sono consentite."},

 
 /** Variable is referencing itself          */
 //public static final int ER_REFERENCING_ITSELF = 107;


 {
   ER_REFERENCING_ITSELF,
     "La variabile {0} fa riferimento a se stessa direttamente o indirettamente."},

 
 /** Illegal DOMSource input          */
 //public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;


 {
   ER_ILLEGAL_DOMSOURCE_INPUT,
     "Il nodo di input non pu\u00f2 essere nullo per DOMSource per newTemplates."},

	
	/** Class not found for option         */
 //public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;


 {
   ER_CLASS_NOT_FOUND_FOR_OPTION,
			"File di classe non trovato per l'opzione {0}"},

	
	/** Required Element not found         */
 //public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;


 {
   ER_REQUIRED_ELEM_NOT_FOUND,
			"Elemento richiesto non trovato: {0}"},

 
 /** InputStream cannot be null         */
 //public static final int ER_INPUT_CANNOT_BE_NULL = 111;


 {
   ER_INPUT_CANNOT_BE_NULL,
			"InputStream non pu\u00f2 essere nullo."},

 
 /** URI cannot be null         */
 //public static final int ER_URI_CANNOT_BE_NULL = 112;


 {
   ER_URI_CANNOT_BE_NULL,
			"L'URI non pu\u00f2 essere nullo."},

 
 /** File cannot be null         */
 //public static final int ER_FILE_CANNOT_BE_NULL = 113;


 {
   ER_FILE_CANNOT_BE_NULL,
			"Il file non pu\u00f2 essere nullo."},

 
  /** InputSource cannot be null         */
 //public static final int ER_SOURCE_CANNOT_BE_NULL = 114;


 {
   ER_SOURCE_CANNOT_BE_NULL,
			"InputSource non pu\u00f2 essere nullo."},

 
 /** Can't overwrite cause         */
 //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


 {
   ER_CANNOT_OVERWRITE_CAUSE,
			"Impossibile sovrascrivere la causa."},

 
 /** Could not initialize BSF Manager        */
 //public static final int ER_CANNOT_INIT_BSFMGR = 116;


 {
   ER_CANNOT_INIT_BSFMGR,
			"Impossibile inizializzare BSF Manager."},

 
 /** Could not compile extension       */
 //public static final int ER_CANNOT_CMPL_EXTENSN = 117;


 {
   ER_CANNOT_CMPL_EXTENSN,
			"Impossibile compilare l'estensione."},

 
 /** Could not create extension       */
 //public static final int ER_CANNOT_CREATE_EXTENSN = 118;


 {
   ER_CANNOT_CREATE_EXTENSN,
     "Impossibile creare l'estensione: {0} a causa di: {1}"},

 
 /** Instance method call to method {0} requires an Object instance as first argument       */
 //public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;


 {
   ER_INSTANCE_MTHD_CALL_REQUIRES,
     "La chiamata del metodo istanza al metodo {0} richiede un'istanza oggetto come primo argomento."},

 
 /** Invalid element name specified       */
 //public static final int ER_INVALID_ELEMENT_NAME = 120;


 {
   ER_INVALID_ELEMENT_NAME,
     "\u00c8 stato specificato un nome elemento non valido {0}"},

 
  /** Element name method must be static      */
 //public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;


 {
   ER_ELEMENT_NAME_METHOD_STATIC,
     "Il metodo del nome elemento deve essere statico {0}"},

 
  /** Extension function {0} : {1} is unknown      */
 //public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;


 {
   ER_EXTENSION_FUNC_UNKNOWN,
            "Funzione estensione {0} : {1} sconosciuta."},

 
  /** More than one best match for constructor for       */
 //public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;


 {
   ER_MORE_MATCH_CONSTRUCTOR,
            "Pi\u00f9 di una corrispondenza migliore per costruttore per {0}."},

 
  /** More than one best match for method      */
 //public static final int ER_MORE_MATCH_METHOD = 124;


 {
   ER_MORE_MATCH_METHOD,
            "Pi\u00f9 di una corrispondenza migliore per il metodo {0}"},

 
  /** More than one best match for element method      */
 //public static final int ER_MORE_MATCH_ELEMENT = 125;


 {
   ER_MORE_MATCH_ELEMENT,
            "Pi\u00f9 di una corrispondenza migliore per il metodo elemento {0}"},

 
  /** Invalid context passed to evaluate       */
 //public static final int ER_INVALID_CONTEXT_PASSED = 126;


 {
   ER_INVALID_CONTEXT_PASSED,
            "Contesto non valido passato da valutare {0}."},

 
  /** Pool already exists       */
 //public static final int ER_POOL_EXISTS = 127;


 {
   ER_POOL_EXISTS,
            "Pool gi\u00e0 esistente."},

 
  /** No driver Name specified      */
 //public static final int ER_NO_DRIVER_NAME = 128;


 {
   ER_NO_DRIVER_NAME,
            "Non \u00e8 stato specificato alcun nome di driver."},

 
  /** No URL specified     */
 //public static final int ER_NO_URL = 129;


 {
   ER_NO_URL,
            "Non \u00e8 stato specificato alcun URL."},

 
  /** Pool size is less than one    */
 //public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;


 {
   ER_POOL_SIZE_LESSTHAN_ONE,
            "Le dimensioni del pool sono minori di uno."},

 
  /** Invalid driver name specified    */
 //public static final int ER_INVALID_DRIVER = 131;


 {
   ER_INVALID_DRIVER,
            "\u00c8 stato specificato un nome di driver non valido."},

 
  /** Did not find the stylesheet root    */
 //public static final int ER_NO_STYLESHEETROOT = 132;


 {
   ER_NO_STYLESHEETROOT,
            "Impossibile trovare la root del foglio di stile."},

 
  /** Illegal value for xml:space     */
 //public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;


 {
   ER_ILLEGAL_XMLSPACE_VALUE,
        "Valore non valido per xml:space."},

 
  /** processFromNode failed     */
 //public static final int ER_PROCESSFROMNODE_FAILED = 134;


 {
   ER_PROCESSFROMNODE_FAILED,
        "processFromNode non riuscito."},

 
  /** The resource [] could not load:     */
 //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;


 {
   ER_RESOURCE_COULD_NOT_LOAD,
       "Impossibile caricare la risorsa [ {0} ]: {1} \n {2} \t {3}"},

  
 
  /** Buffer size <=0     */
 //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;


 {
   ER_BUFFER_SIZE_LESSTHAN_ZERO,
       "Dimensioni del buffer <=0"},

 
  /** Unknown error when calling extension    */
 //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;


 {
   ER_UNKNOWN_ERROR_CALLING_EXTENSION,
       "Errore sconosciuto nella chiamata dell'estensione."},

 
  /** Prefix {0} does not have a corresponding namespace declaration    */
 //public static final int ER_NO_NAMESPACE_DECL = 138;


 {
   ER_NO_NAMESPACE_DECL,
       "Il prefisso {0} non ha una corrispondente dichiarazione namespace."},

 
  /** Element content not allowed for lang=javaclass   */
 //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;


 {
   ER_ELEM_CONTENT_NOT_ALLOWED,
       "Contenuto dell'elemento non consentito per lang=javaclass {0}."},
   
 
  /** Stylesheet directed termination   */
 //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;


 {
   ER_STYLESHEET_DIRECTED_TERMINATION,
       "Conclusione richiesta dal foglio di stile."},

 
  /** 1 or 2   */
 //public static final int ER_ONE_OR_TWO = 141;


 {
   ER_ONE_OR_TWO,
       "1 o 2"},

 
  /** 2 or 3   */
 //public static final int ER_TWO_OR_THREE = 142;


 {
   ER_TWO_OR_THREE,
       "2 o 3"},

 
  /** Could not load {0} (check CLASSPATH), now using just the defaults   */
 //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;


 {
   ER_COULD_NOT_LOAD_RESOURCE,
       "Impossibile caricare {0} (verificare CLASSPATH). Attualmente sono in uso i valori predefiniti."},

 
  /** Cannot initialize default templates   */
 //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;


 {
   ER_CANNOT_INIT_DEFAULT_TEMPLATES,
       "Impossibile inizializzare i modelli predefiniti."},

 
  /** Result should not be null   */
 //public static final int ER_RESULT_NULL = 145;


 {
   ER_RESULT_NULL,
       "Il risultato non dovrebbe essere nullo."},

   
  /** Result could not be set   */
 //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;


 {
   ER_RESULT_COULD_NOT_BE_SET,
       "Impossibile stabilire il risultato."},

 
  /** No output specified   */
 //public static final int ER_NO_OUTPUT_SPECIFIED = 147;


 {
   ER_NO_OUTPUT_SPECIFIED,
       "Nessun output specificato."},

 
  /** Can't transform to a Result of type   */
 //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;


 {
   ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
       "Impossibile trasformare in un risultato di tipo {0}."},

 
  /** Can't transform to a Source of type   */
 //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;


 {
   ER_CANNOT_TRANSFORM_SOURCE_TYPE,
       "Impossibile trasformare un sorgente di tipo {0}."},

 
  /** Null content handler  */
 //public static final int ER_NULL_CONTENT_HANDLER = 150;


 {
   ER_NULL_CONTENT_HANDLER,
       "Contenuto gestore nullo"},

 
  /** Null error handler  */
 //public static final int ER_NULL_ERROR_HANDLER = 151;


 {
   ER_NULL_ERROR_HANDLER,
       "Errore gestore nullo"},

 
  /** parse can not be called if the ContentHandler has not been set */
 //public static final int ER_CANNOT_CALL_PARSE = 152;


 {
   ER_CANNOT_CALL_PARSE,
       "Impossibile chiamare l'analisi se non \u00e8 impostato ContentHandler."},

 
  /**  No parent for filter */
 //public static final int ER_NO_PARENT_FOR_FILTER = 153;


 {
   ER_NO_PARENT_FOR_FILTER,
       "Nessun elemento principale per il filtro."},

 
 
  /**  No stylesheet found in: {0}, media */
 //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;


 {
   ER_NO_STYLESHEET_IN_MEDIA,
        "Nessun foglio di stile trovato in: {0}, media= {1}"},

 
  /**  No xml-stylesheet PI found in */
 //public static final int ER_NO_STYLESHEET_PI = 155;


 {
   ER_NO_STYLESHEET_PI,
        "Nessun xml-stylesheet PI trovato in : {0}"},

 
  /**  No default implementation found */
 //public static final int ER_NO_DEFAULT_IMPL = 156;


 {
   ER_NO_DEFAULT_IMPL,
        "Non \u00e8 stata trovata alcuna implementazione predefinita "},

 
  /**  ChunkedIntArray({0}) not currently supported */
 //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


 {
   ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
      "ChunkedIntArray({0}) non \u00e8 correntemente supportato."},

 
  /**  Offset bigger than slot */
 //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


 {
   ER_OFFSET_BIGGER_THAN_SLOT,
      "L'offset \u00e8 maggiore dello slot."},

 
  /**  Coroutine not available, id= */
 //public static final int ER_COROUTINE_NOT_AVAIL = 159;


 {
   ER_COROUTINE_NOT_AVAIL,
      "Coroutine non disponibile, id={0}"},

 
  /**  CoroutineManager recieved co_exit() request */
 //public static final int ER_COROUTINE_CO_EXIT = 160;


 {
   ER_COROUTINE_CO_EXIT,
      "CoroutineManager ha ricevuto una richiesta co_exit()."},

 
  /**  co_joinCoroutineSet() failed */
 //public static final int ER_COJOINROUTINESET_FAILED = 161;


 {
   ER_COJOINROUTINESET_FAILED,
      "co_joinCoroutineSet() non riuscito."},

 
  /**  Coroutine parameter error () */
 //public static final int ER_COROUTINE_PARAM = 162;


 {
   ER_COROUTINE_PARAM,
      "Errore del parametro di coroutine ({0})."},

 
  /**  UNEXPECTED: Parser doTerminate answers  */
 //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


 {
   ER_PARSER_DOTERMINATE_ANSWERS,
      "\nUNEXPECTED: Risposte doTerminate del parser {0}"},

 
  /**  parse may not be called while parsing */
 //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


 {
   ER_NO_PARSE_CALL_WHILE_PARSING,
      "Impossibile chiamare l'analisi mentre \u00e8 in esecuzione."},

 
  /**  Error: typed iterator for axis  {0} not implemented  */
 //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


 {
   ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Errore: tipo di iteratore per l'asse {0} non implementato."},

 
  /**  Error: iterator for axis {0} not implemented  */
 //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


 {
   ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Errore: l'iteratore per l'asse {0} non \u00e8 implementato. "},

 
  /**  Iterator clone not supported  */
 //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


 {
   ER_ITERATOR_CLONE_NOT_SUPPORTED,
      "Il duplicato dell'iteratore non \u00e8 supportato."},

 
  /**  Unknown axis traversal type  */
 //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


 {
   ER_UNKNOWN_AXIS_TYPE,
      "Tipo di asse trasversale sconosciuto : {0}."},

 
  /**  Axis traverser not supported  */
 //public static final int ER_AXIS_NOT_SUPPORTED = 169;


 {
   ER_AXIS_NOT_SUPPORTED,
      "Attraversatore dell'asse non supportato: {0}"},

 
  /**  No more DTM IDs are available  */
 //public static final int ER_NO_DTMIDS_AVAIL = 170;


 {
   ER_NO_DTMIDS_AVAIL,
      "Non sono pi\u00f9 disponibili ID DTM."},

 
  /**  Not supported  */
 //public static final int ER_NOT_SUPPORTED = 171;


 {
   ER_NOT_SUPPORTED,
      "Non supportato: {0}"},

 
  /**  node must be non-null for getDTMHandleFromNode  */
 //public static final int ER_NODE_NON_NULL = 172;


 {
   ER_NODE_NON_NULL,
      "Il nodo deve essere non nullo per getDTMHandleFromNode."},

 
  /**  Could not resolve the node to a handle  */
 //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


 {
   ER_COULD_NOT_RESOLVE_NODE,
      "Impossibile risolvere il nodo a un handle"},

 
  /**  startParse may not be called while parsing */
 //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


 {
   ER_STARTPARSE_WHILE_PARSING,
      "Impossibile chiamare startParse durante l'analisi."},

 
  /**  startParse needs a non-null SAXParser  */
 //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


 {
   ER_STARTPARSE_NEEDS_SAXPARSER,
      "startParse richiede un SAXParser non nullo."},

 
  /**  could not initialize parser with */
 //public static final int ER_COULD_NOT_INIT_PARSER = 176;


 {
   ER_COULD_NOT_INIT_PARSER,
      "Impossibile inizializzare il parser con"},

 
  /**  Value for property {0} should be a Boolean instance  */
 //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;


 {
   ER_PROPERTY_VALUE_BOOLEAN,
      "Il valore della propriet\u00e0 {0} deve essere un'istanza booleana"},

 
  /**  exception creating new instance for pool  */
 //public static final int ER_EXCEPTION_CREATING_POOL = 178;


 {
   ER_EXCEPTION_CREATING_POOL,
      "l'eccezione crea una nuova istanza del pool"},

 
  /**  Path contains invalid escape sequence  */
 //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


 {
   ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
      "Il percorso contiene una sequenza di escape non valida."},

 
  /**  Scheme is required!  */
 //public static final int ER_SCHEME_REQUIRED = 180;


 {
   ER_SCHEME_REQUIRED,
      "Lo schema \u00e8 necessario."},

 
  /**  No scheme found in URI  */
 //public static final int ER_NO_SCHEME_IN_URI = 181;


 {
   ER_NO_SCHEME_IN_URI,
      "Nessuno schema trovato nell'URI: {0}"},

 
  /**  No scheme found in URI  */
 //public static final int ER_NO_SCHEME_INURI = 182;


 {
   ER_NO_SCHEME_INURI,
      "Nessuno schema trovato nell'URI"},

 
  /**  Path contains invalid character:   */
 //public static final int ER_PATH_INVALID_CHAR = 183;


 {
   ER_PATH_INVALID_CHAR,
      "Il percorso contiene un carattere non valido: {0}"},

 
  /**  Cannot set scheme from null string  */
 //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


 {
   ER_SCHEME_FROM_NULL_STRING,
      "Impossibile impostare lo schema da una stringa nulla."},

 
  /**  The scheme is not conformant. */
 //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


 {
   ER_SCHEME_NOT_CONFORMANT,
      "Lo schema non \u00e8 conforme."},

 
  /**  Host is not a well formed address  */
 //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


 {
   ER_HOST_ADDRESS_NOT_WELLFORMED,
      "L'host non \u00e8 un indirizzo corretto."},

 
  /**  Port cannot be set when host is null  */
 //public static final int ER_PORT_WHEN_HOST_NULL = 187;


 {
   ER_PORT_WHEN_HOST_NULL,
      "Impossibile impostare la porta quando l'host \u00e8 nullo."},

 
  /**  Invalid port number  */
 //public static final int ER_INVALID_PORT = 188;


 {
   ER_INVALID_PORT,
      "Numero di porta non valido"},

 
  /**  Fragment can only be set for a generic URI  */
 //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


 {
   ER_FRAG_FOR_GENERIC_URI,
      "\u00c8 possibile impostare il frammento solo per un URI generico."},

 
  /**  Fragment cannot be set when path is null  */
 //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


 {
   ER_FRAG_WHEN_PATH_NULL,
      "Impossibile impostare il frammento quando il percorso \u00e8 nullo."},

 
  /**  Fragment contains invalid character  */
 //public static final int ER_FRAG_INVALID_CHAR = 191;


 {
   ER_FRAG_INVALID_CHAR,
      "Il frammento contiene un carattere non valido."},

 

 
  /** Parser is already in use  */
 //public static final int ER_PARSER_IN_USE = 192;


 {
   ER_PARSER_IN_USE,
       "Il parser \u00e8 gi\u00e0 in uso."},

 
  /** Parser is already in use  */
 //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


 {
   ER_CANNOT_CHANGE_WHILE_PARSING,
       "Impossibile cambiare {0} {1} durante l'analisi."},

 
  /** Self-causation not permitted  */
 //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


 {
   ER_SELF_CAUSATION_NOT_PERMITTED,
       "Non \u00e8 consentito essere causa ed effetto contemporaneamente."},

 
  /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
       "Impossibile passare allo script esterno su {0}"},

 
 /** The resource [] could not be found     */
 //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;


 {
   ER_RESOURCE_COULD_NOT_FIND,
       "Impossibile trovare la risorsa [ {0} ].\n {1}"},

 
  /** output property not recognized:  */
 //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;


 {
   ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
       "La propriet\u00e0 dell'output non \u00e8 riconosciuta: {0}"},

 
  /** Userinfo may not be specified if host is not specified   */
 //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


 {
   ER_NO_USERINFO_IF_NO_HOST,
       "Impossibile specificare Userinfo se non \u00e8 specificato l'host."},

 
  /** Port may not be specified if host is not specified   */
 //public static final int ER_NO_PORT_IF_NO_HOST = 199;


 {
   ER_NO_PORT_IF_NO_HOST,
       "Impossibile specificare la porta se non \u00e8 specificato l'host."},

 
  /** Query string cannot be specified in path and query string   */
 //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


 {
   ER_NO_QUERY_STRING_IN_PATH,
       "La stringa di query non pu\u00f2 essere specificata nella stringa di percorso e di query."},

 
  /** Fragment cannot be specified in both the path and fragment   */
 //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


 {
   ER_NO_FRAGMENT_STRING_IN_PATH,
       "Il frammento non pu\u00f2 essere specificato sia nel percorso sia nel frammento."},

 
  /** Cannot initialize URI with empty parameters   */
 //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


 {
   ER_CANNOT_INIT_URI_EMPTY_PARMS,
       "Impossibile inizializzare l'URI con parametri vuoti."},

 
  /** Failed creating ElemLiteralResult instance   */
 //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;


 {
   ER_FAILED_CREATING_ELEMLITRSLT,
       "Creazione non riuscita dell'istanza ElemLiteralResult."},
  
 
	 // Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
    // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
    //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
    //NOTE: Not only the key name but message has also been changed. - nb.

  /** Priority value does not contain a parsable number   */
 //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;


 {
   ER_VALUE_SHOULD_BE_NUMBER,
       "Il valore di {0} deve contenere un numero analizzabile."},

 
  /**  Value for {0} should equal 'yes' or 'no'   */
 //public static final int ER_VALUE_SHOULD_EQUAL = 205;


 {
   ER_VALUE_SHOULD_EQUAL,
       "Il valore di {0} deve essere s\u00ec o no."},


  /**  Failed calling {0} method   */
 //public static final int ER_FAILED_CALLING_METHOD = 206;


 {
   ER_FAILED_CALLING_METHOD,
       "Chiamata non riuscita del metodo {0}."},

 
  /** Failed creating ElemLiteralResult instance   */
 //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;


 {
   ER_FAILED_CREATING_ELEMTMPL,
       "Creazione non riuscita dell'istanza ElemTemplateElement."},

 
  /**  Characters are not allowed at this point in the document   */
 //public static final int ER_CHARS_NOT_ALLOWED = 208;


 {
   ER_CHARS_NOT_ALLOWED,
       "I caratteri non sono consentiti in questo punto del documento."},

 
 /**  attribute is not allowed on the element   */
 //public static final int ER_ATTR_NOT_ALLOWED = 209;


 {
   ER_ATTR_NOT_ALLOWED,
       "\"{0}\": questo attributo non \u00e8 consentito sull'elemento {1}."},

 
 /**  Method not yet supported    */
 //public static final int ER_METHOD_NOT_SUPPORTED = 210;


 {
   ER_METHOD_NOT_SUPPORTED,
       "Metodo non ancora supportato. "},


 /**  Bad value    */
 //public static final int ER_BAD_VALUE = 211;


 {
   ER_BAD_VALUE,
    "{0} valore non valido {1} "},

 
 /**  attribute value not found   */
 //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;


 {
   ER_ATTRIB_VALUE_NOT_FOUND,
    "{0} valore dell'attributo non trovato."},

 
 /**  attribute value not recognized    */
 //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;


 {
   ER_ATTRIB_VALUE_NOT_RECOGNIZED,
    "{0} valore dell'attributo non riconosciuto "},


 /** IncrementalSAXSource_Filter not currently restartable   */
 //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


 {
   ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
    "IncrementalSAXSource_Filter non correntemente riavviabile."},

 
 /** IncrementalSAXSource_Filter not currently restartable   */
 //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


 {
   ER_XMLRDR_NOT_BEFORE_STARTPARSE,
    "XMLReader non prima della richiesta startParse."},

 
  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;


  {
      ER_NULL_URI_NAMESPACE,
      "Tentativo di generare un prefisso di namespace con URI nullo"},
  
 
   // Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)
 
   /** Attempting to generate a namespace prefix with a null URI        */
   //public static final int ER_NUMBER_TOO_BIG = 217;
 
 
   {
     ER_NUMBER_TOO_BIG,
      "Tentativo di formattare un numero maggiore dell'intero lungo pi\u00f9 grande"},
  
 
 //ER_CANNOT_FIND_SAX1_DRIVER
 
   //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;
 
 
   {
     ER_CANNOT_FIND_SAX1_DRIVER,
      "Impossibile trovare classe driver SAX1 {0}"},
  
 
 //ER_SAX1_DRIVER_NOT_LOADED
   //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;
 
 
   {
     ER_SAX1_DRIVER_NOT_LOADED,
      "La classe di driver SAX1 {0} \u00e8 stata trovata ma \u00e8 impossibile caricarla"},
  
 
 //ER_SAX1_DRIVER_NOT_INSTANTIATED
   //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;
 
 
   {
     ER_SAX1_DRIVER_NOT_INSTANTIATED,
      "La classe di driver SAX1 {0} \u00e8 stata caricata ma non \u00e8 possibile creare istanze"},
  
 
 
 // ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
   //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;
 
 
   {
     ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
      "La classe di driver SAX1 {0} non implementa org.xml.sax.Parser"},
  
 
 // ER_PARSER_PROPERTY_NOT_SPECIFIED
   //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;
 
 
   {
     ER_PARSER_PROPERTY_NOT_SPECIFIED,
      "Propriet\u00e0 di sistema org.xml.sax.parser non specificata"},
  
 
 //ER_PARSER_ARG_CANNOT_BE_NULL
   //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;
 
 
   {
     ER_PARSER_ARG_CANNOT_BE_NULL,
      "L'argomento del Parser non deve essere nullo"},
  
 
 
 // ER_FEATURE
   //public static final int  ER_FEATURE = 224;
 
 
   {
     ER_FEATURE,
      "Caratteristica:a {0}"},
  
 
 
 // ER_PROPERTY
   //public static final int ER_PROPERTY = 225 ;
 
 
   {
     ER_PROPERTY,
      "Propriet\u00e0:a {0}"},
  
 
 // ER_NULL_ENTITY_RESOLVER
   //public static final int ER_NULL_ENTITY_RESOLVER  = 226;
 
 
   {
     ER_NULL_ENTITY_RESOLVER,
      "Il risolutore dell'entit\u00e0 \u00e8 nullo"},
  
 
 // ER_NULL_DTD_HANDLER
   //public static final int  ER_NULL_DTD_HANDLER = 227 ;
 
 
   {
     ER_NULL_DTD_HANDLER,
      "Il gestore DTD \u00e8 nullo"},
  
 
 // No Driver Name Specified!
   //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;
 
   {
     ER_NO_DRIVER_NAME_SPECIFIED,
      "Nessun nome di driver specificato."},
  
 
 
 // No URL Specified!
   //public static final int ER_NO_URL_SPECIFIED = 229;
 
   {
     ER_NO_URL_SPECIFIED,
      "Nessun URL specificato."},
  
 
 
 // Pool size is less than 1!
   //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;
 
   {
     ER_POOLSIZE_LESS_THAN_ONE,
      "La dimensione pool \u00e8 inferiore a 1."},
  
 
 
 // Invalid Driver Name Specified!
   //public static final int ER_INVALID_DRIVER_NAME = 231;
 
   {
     ER_INVALID_DRIVER_NAME,
      "Il nome specificato del driver non \u00e8 valido."},
  
 
 
 
 // ErrorListener
   //public static final int ER_ERRORLISTENER = 232;
 
   {
     ER_ERRORLISTENER,
      "ErrorListener"},
  
 
 
 // Programmer's error! expr has no ElemTemplateElement parent!
   //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;
 
   {
     ER_ASSERT_NO_TEMPLATE_PARENT,
      "Errore del programmatore. L'espressione non presenta ElemTemplateElement superiore."},
  
 
 
 // Programmer's assertion in RundundentExprEliminator: {0}
   //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;
 
   {
     ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
      "Affermazione del programmatore in RundundentExprEliminator: {0}"},
  
 
 // Axis traverser not supported: {0}
   //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
 
   {
     ER_AXIS_TRAVERSER_NOT_SUPPORTED,
      "Secante asse non supportata: {0}"},
  
 
 // ListingErrorHandler created with null PrintWriter!
   //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
 
   {
     ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
      "ListingErrorHandler creato con PrintWriter nullo."},
  
 
   // {0}is not allowed in this position in the stylesheet!
   //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;
 
   {
     ER_NOT_ALLOWED_IN_POSITION,
      "{0} non \u00e8 consentito in questa posizione nel foglio di stile."},
  
 
   // Non-whitespace text is not allowed in this position in the stylesheet!
   //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
 
   {
     ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
      "Il testo senza spazi non \u00e8 consentito nel foglio di stile."},
  
 
   // This code is shared with warning codes.
   // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
   //public static final int INVALID_TCHAR = 239;
   // SystemId Unknown
 
   {
     INVALID_TCHAR,
      "Valore non consentito: {1} utilizzato per attributo CHAR: {0}. L'attributo di tipo CHAR deve contenere 1 solo carattere."},
  
 
   //public static final int ER_SYSTEMID_UNKNOWN = 240;
 
   {
     ER_SYSTEMID_UNKNOWN,
      "ID sistema sconosciuto"},
  
 
   // Location of error unknown
   //public static final int ER_LOCATION_UNKNOWN = 241;
 
   {
     ER_LOCATION_UNKNOWN,
      "Ubicazione errore sconosciuta"},
  
 
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
      "Valore non consentito:a {1} utilizzato per attributo QNAME:a {0}"},
  
 
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
      "Valore non consentito:a {1} utilizzato per attributo ENUM:a {0}. I valori validi sono:a {2}."},
  
 
 // Note to translators:  The following message is used if the value of
 // an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
 // of the attribute, and should not be translated.  The substitution text {1} is
 // the attribute value and {0} is the attribute name.
 // INVALID_NMTOKEN
 
   // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
   //public static final int INVALID_NMTOKEN = 244;
 
    {
      INVALID_NMTOKEN,
       "Valore non consentito:a {1} utilizzato per attributo NMTOKEN:a {0} "},
   
  
  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NCNAME
  
    // Illegal value:a {1} used for NCNAME attribute:a {0}.
    //public static final int INVALID_NCNAME = 245;
  
    {
      INVALID_NCNAME,
       "Valore non consentito:a {1} utilizzato per attributo NCNAME:a {0} "},
   
  
  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_BOOLEAN
  
    // Illegal value:a {1} used for boolean attribute:a {0}.
    //public static final int INVALID_BOOLEAN = 246;
  
  
    {
      INVALID_BOOLEAN,
       "Valore non consentito:a {1} utilizzato per attributo boolean:a {0} "},
   
  
  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NUMBER
  
    // Illegal value:a {1} used for number attribute:a {0}.
    //public static final int INVALID_NUMBER = 247;
  
    {
      INVALID_NUMBER,
       "Valore non consentito:a {1} utilizzato per attributo number:a {0} "},
   
  
  
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
       "L'argomento di {0} nel pattern di corrispondenza deve essere letterale."},
   
  
  // Note to translators:  The following message indicates that two definitions of
  // a variable.  A "global variable" is a variable that is accessible everywher
  // in the stylesheet.
  // ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790
  
    // Duplicate global variable declaration.
    //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;
  
    {
      ER_DUPLICATE_GLOBAL_VAR,
       "Dichiarazione variabile globale duplicata."},
   
  
  
  // Note to translators:  The following message indicates that two definitions of
  // a variable were encountered.
  // ER_DUPLICATE_VAR - new error message for bugzilla #790
  
    // Duplicate variable declaration.
    //public static final int ER_DUPLICATE_VAR           = 250;
  
    {
      ER_DUPLICATE_VAR,
       "Dichiarazione variabile duplicata."},
   
 
      // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
      // which must not be translated.
      // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789
  
    // xsl:template must have a name or match attribute (or both)
    //public static final int ER_TEMPLATE_NAME_MATCH     = 251;
  
    {
      ER_TEMPLATE_NAME_MATCH,
       "xsl:template deve presentare un name o attributo match (o entrambi)"},
   
 
      // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
      // should not be translated.  The message indicates that a namespace prefix
      // encountered as part of the value of the exclude-result-prefixes attribute
      // was in error.
      // ER_INVALID_PREFIX - new error message for bugzilla #788
  
    // Prefix in exclude-result-prefixes is not valid:a {0}
    //public static final int ER_INVALID_PREFIX          = 252;
  
    {
      ER_INVALID_PREFIX,
       "Il prefisso in exclude-result-prefixes non \u00e8 valido:a {0}"},
   
  
      // Note to translators:  An "attribute set" is a set of attributes that can be
      // added to an element in the output document as a group.  The message indicates
      // that there was a reference to an attribute set named {0} that was never
      // defined.
      // ER_NO_ATTRIB_SET - new error message for bugzilla #782
  
    // attribute-set named {0} does not exist
    //public static final int ER_NO_ATTRIB_SET           = 253;
  
    {
      ER_NO_ATTRIB_SET,
       "la serie di attributi denominata {0} \u00e8 inesistente"},
   

 // Warnings...

 /** WG_FOUND_CURLYBRACE          */
 //public static final int WG_FOUND_CURLYBRACE = 1;


 {
   WG_FOUND_CURLYBRACE,
     "Trovato '}' ma non vi \u00e8 alcun modello di attributi aperto."},


 /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
 //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;


 {
   WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
     "Avvertenza: l'attributo di conteggio non corrisponde a un predecessore in xsl:number! Target = {0}."},


 /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
 //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;


 {
   WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
     "Sintassi precedente: il nome dell'attributo 'expr' \u00e8 stato cambiato in 'select'."},


 /** WG_NO_LOCALE_IN_FORMATNUMBER          */
 //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;


 {
   WG_NO_LOCALE_IN_FORMATNUMBER,
     "Xalan non gestisce ancora il nome locale nella funzione format-number."},


 /** WG_LOCALE_NOT_FOUND          */
 //public static final int WG_LOCALE_NOT_FOUND = 5;


 {
   WG_LOCALE_NOT_FOUND,
     "Avvertenza: impossibile trovare la versione locale per xml:lang={0}."},


 /** WG_CANNOT_MAKE_URL_FROM          */
 //public static final int WG_CANNOT_MAKE_URL_FROM = 6;


 {
   WG_CANNOT_MAKE_URL_FROM,
     "Impossibile creare l'URL da: {0}."},


 /** WG_CANNOT_LOAD_REQUESTED_DOC          */
 //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;


 {
   WG_CANNOT_LOAD_REQUESTED_DOC,
     "Impossibile caricare il documento richiesto: {0}"},


 /** WG_CANNOT_FIND_COLLATOR          */
 //public static final int WG_CANNOT_FIND_COLLATOR = 8;


 {
   WG_CANNOT_FIND_COLLATOR,
     "Impossibile trovare il collatore per <sort xml:lang={0}."},


 /** WG_FUNCTIONS_SHOULD_USE_URL          */
 //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;


 {
   WG_FUNCTIONS_SHOULD_USE_URL,
     "Sintassi precedente: l'istruzione delle funzioni deve utilizzare l'URL {0}"},


 /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
 //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;


 {
   WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
     "codifica non supportata: {0}, utilizzando UTF-8"},


 /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
 //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;


 {
   WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
     "Codifica non supportata: {0}, utilizzando Java {1}"},


 /** WG_SPECIFICITY_CONFLICTS          */
 //public static final int WG_SPECIFICITY_CONFLICTS = 12;


 {
   WG_SPECIFICITY_CONFLICTS,
     "Trovati conflitti di specificit\u00e0: {0} Sar\u00e0 utilizzato l'ultimo trovato nel foglio di stile."},


 /** WG_PARSING_AND_PREPARING          */
 //public static final int WG_PARSING_AND_PREPARING = 13;


 {
   WG_PARSING_AND_PREPARING,
     "========= Analisi e preparazione {0} =========="},


 /** WG_ATTR_TEMPLATE          */
 //public static final int WG_ATTR_TEMPLATE = 14;


 {
   WG_ATTR_TEMPLATE, "Modello attr., {0}"},


 /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
 //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;


 {
   WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
     "Conflitto di corrispondenza tra xsl:strip-space e xsl:preserve-space."},


 /** WG_ATTRIB_NOT_HANDLED          */
 //public static final int WG_ATTRIB_NOT_HANDLED = 16;


 {
   WG_ATTRIB_NOT_HANDLED,
     "Xalan non gestisce ancora l'attributo {0}."},


 /** WG_NO_DECIMALFORMAT_DECLARATION          */
 //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;


 {
   WG_NO_DECIMALFORMAT_DECLARATION,
     "Non \u00e8 stata trovata alcuna dichiarazione per il formato decimale: {0}"},


 /** WG_OLD_XSLT_NS          */
 //public static final int WG_OLD_XSLT_NS = 18;


 {
   WG_OLD_XSLT_NS, "XSLT Namespace mancante o non valido. "},


 /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
 //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;


 {
   WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
     "\u00c8 consentita solo una dichiarazione xsl:decimal-format predefinita."},


 /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
 //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;


 {
   WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
     "I nomi xsl:decimal-format devono essere univoci. Il nome \"{0}\" \u00e8 duplicato."},


 /** WG_ILLEGAL_ATTRIBUTE          */
 //public static final int WG_ILLEGAL_ATTRIBUTE = 21;


 {
   WG_ILLEGAL_ATTRIBUTE,
     "{0} ha un attributo non valido: {1}"},


 /** WG_COULD_NOT_RESOLVE_PREFIX          */
 //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;


 {
   WG_COULD_NOT_RESOLVE_PREFIX,
     "Impossibile risolvere il prefisso namespace: {0}. Nodo ignorato."},


 /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
 //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;


 {
   WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
     "xsl:stylesheet richiede un attributo 'versione'."},


 /** WG_ILLEGAL_ATTRIBUTE_NAME          */
 //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;


 {
   WG_ILLEGAL_ATTRIBUTE_NAME,
     "Nome attributo non valido: {0}"},


 /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
 //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;


 {
   WG_ILLEGAL_ATTRIBUTE_VALUE,
     "Valore non valido per l'attributo {0}: {1}."},


 /** WG_EMPTY_SECOND_ARG          */
 //public static final int WG_EMPTY_SECOND_ARG = 26;


 {
   WG_EMPTY_SECOND_ARG,
     "Il nodeset risultante dal secondo argomento della funzione documento \u00e8 vuoto. Sar\u00e0 utilizzato il primo argomento."},


   // Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)
 
     // Note to translators:  "name" and "xsl:processing-instruction" are keywords
     // and must not be translated.
     // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML
 
 
   /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
   //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;
 
   {
      WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
       "Il valore dell'attributo 'name' del nome xsl:processing-instruction name non deve essere 'xml'"},
  
 
     // Note to translators:  "name" and "xsl:processing-instruction" are keywords
     // and must not be translated.  "NCName" is an XML data-type and must not be
     // translated.
     // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME
 
   /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
   //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;
 
   {
      WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
       "Il valore dell'attributo ''name'' di xsl:processing-instruction deve essere un NCName valido:a {0}"},
  
 
     // Note to translators:  This message is reported if the stylesheet that is
     // being processed attempted to construct an XML document with an attribute in a
     // place other than on an element.  The substitution text specifies the name of
     // the attribute.
     // WG_ILLEGAL_ATTRIBUTE_POSITION
 
   /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
   //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;
 
   {
     WG_ILLEGAL_ATTRIBUTE_POSITION,
       "Impossibile aggiungere l'attributo {0} dopo i nodi secondari o prima della produzione di un elemento.  L'attributo verr\u00e0 ignorato."},
  
 
     //WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?
 
 // Other miscellaneous text used inside the code...
     { "ui_language",  "it"},
     { "help_language",  "it"},
     { "language",  "it"},
     { "BAD_CODE",
     "I parametri di createMessage sono esterni ai limiti"},
   { "FORMAT_FAILED",
     "Eccezione generata durante la chiamata di messageFormat"},
   { "version",
    ">>>>>>> Xalan Versione "},
   { "version2", "<<<<<<<"},
   { "yes", "s\u00ec"},
   { "line", "Linea #"},
   { "column", "Colonna #"},
   { "xsldone", "XSLProcessor: done"},
   { "xslProc_option", "opzioni dalla riga di comando della classe Process di Xalan-J:"},
  { "xslProc_invalid_xsltc_option", "Opzione {0} non supportata in modalit\u00e0 XSLTC."},
  { "xslProc_invalid_xalan_option", "\u00c8 possibile utilizzare l'opzione {0} solo con -XSLTC."},
  { "xslProc_no_input", "Errore: nessun foglio di stile o xml di input specificato. Eseguire questo comando senza alcuna opzione per visualizzare le istruzioni relative all'uso."},
  { "xslProc_common_options", "-Opzioni comuni-"},
  { "xslProc_xalan_options", "-Opzioni per Xalan-"},
  { "xslProc_xsltc_options", "-Opzioni per XSLTC-"},
  { "xslProc_return_to_continue", "(premere <Invio> per continuare)"},

  { "optionXSLTC", "   [-XSLTC (utilizzare XSLTC per la trasformazione)]"},
   { "optionIN", "    -IN inputXMLURL"},
   { "optionXSL", "   [-XSL XSLTransformationURL]"},
   { "optionOUT", "   [-OUT outputFileName]"},
   { "optionLXCIN",
     "   [-LXCIN compiledStylesheetFileNameIn]"},
   { "optionLXCOUT",
     "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
   { "optionPARSER",
     "   [-PARSER nome di classe pienamente qualificato del collegamento parser]"},
   { "optionE", "   [-E (Non espandere i rif entit\u00e0)]"},
   { "optionV", "   [-E (Non espandere i rif entit\u00e0)]"},
   { "optionQC", "   [-QC (Avvertenze di conflitti Quiet Pattern)]"},
   { "optionQ", "   [-Q  (Modalit\u00e0 Quiet)]"},
   { "optionLF",
     "   [-LF (Usa nuove righe solo su output {valore predefinito CR/LF})]"},
   { "optionCR",
     "   [-CR (Usa ritorno a capo solo su output {valore predefinito CR/LF})]"},
   { "optionESCAPE",
     "   [-ESCAPE (Quali carattere saltare {valore predefinito <>&\"\'\\r\\n}]"},
   { "optionINDENT",
     "   [-INDENT (Controlla il numero di spazi del rientro {valore predefinito 0})]"},
  { "optionTT",
     "   [-TT (Traccia i modelli man mano che sono chiamati)]"},
   { "optionTG", "   [-TG (Traccia ogni evento di generazione)]"},
   { "optionTS", "   [-TS (Traccia ogni evento di selezione)]"},
   { "optionTTC",
     "   [-TTC (Traccia gli elementi secondari del modello man mano che sono elaborati)]"},
   { "optionTCLASS",
     "   [-TCLASS (Classe TraceListener per le estensioni di traccia)]"},
   { "optionVALIDATE",
     "   [-VALIDATE (Imposta se eseguire la validazione. Il valore predefinito \u00e8 validazione disattivata.)]"},
   { "optionEDUMP",
     "   [-EDUMP {nome file opzionale} (Esegue il dump dello stack in caso di errore)]"},
   { "optionXML",
     "   [-XML (Utilizza il formattatore XML e aggiunge l'intestazione XML)]"},
  { "optionTEXT",
     "   [-TEXT (Utilizza il formattatore di testo semplice)]"},
   { "optionHTML", "   [-HTML (Utilizza il formattatore HTML)]"},
   { "optionPARAM",
     "   [-PARAM espressione nome (Imposta un parametro di foglio di stile)]"},
   { "noParsermsg1", "Processo XSL non riuscito."},
   { "noParsermsg2", "** Impossibile trovare il parser **"},
   { "noParsermsg3", "Verificare il classpath."},
   { "noParsermsg4",
     "Se non si dispone del parser XML IBM per Java, scaricarlo da"},
   { "noParsermsg5",
     "AlphaWorks IBM: http://www.alphaworks.ibm.com/formula/xml"},
   { "optionURIRESOLVER",
   "   [-URIRESOLVER nome classe completo (URIResolver da utilizzare per risolvere gli URI)]"},
   { "optionENTITYRESOLVER",
   "   [-ENTITYRESOLVER nome classe completo (EntityResolver da utilizzare per risolvere le entit\u00e0)]"},
   { "optionCONTENTHANDLER",
   "   [-CONTENTHANDLER nome classe completo (ContentHandler da utilizzare per serializzare l'output)]"},
   { "optionLINENUMBERS",
   "   [-L utilizza i numeri di linea per i documenti sorgente]"},
		
// Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)
 
 
   { "optionMEDIA",
     " [-MEDIA mediaType (utilizzare l'attributo media per trovare il foglio di stile associato a un documento.)]"},
   { "optionFLAVOR",
     " [-FLAVOR flavorName (utilizzare esplicitamente s2s=SAX o d2d=DOM per effettuare la trasformazione.)] "}, // Aggiunto da sboag/scurcuru; sperimentale
   { "optionDIAG",
     " [-DIAG (stampa i millisecondi globali impiegati dalla trasformazione.)]"},
   { "optionINCREMENTAL",
     " [-INCREMENTAL (richiede la costruzione DTM incrementale impostando a true http://xml.apache.org/xalan/features/incremental.)]"},
   { "optionNOOPTIMIMIZE",
     " [-NOOPTIMIMIZE (non richiede l'elaborazione dell'ottimizzazione del foglio di stile impostando a false http://xml.apache.org/xalan/features/optimize.)]"},
   { "optionRL",
     " [-RL recursionlimit (garantisce il limite numerico sulla profondit\u00e0 di ricorsione del foglio di stile.)]"},
   { "optionXO",
     " [-XO [transletName] (assegna il nome al translet generato)]"},
   { "optionXD",
     " [-XD destinationDirectory (specifica una directory di destinazione per il translet)]"},
   { "optionXJ",
     " [-XJ jarfile (compatta la classi del translet in un file jar denominato <filejar>)]"},
   { "optionXP",
     " [-XP package (specifica un prefisso del nome di pacchetto per tutte le classi translet generate)]"},
  { "optionXN",  "   [-XN (consente l'incorporamento dei modelli)]" },
  { "optionXX",  "   [-XX (attiva l'output aggiuntivo del messaggio di debug)]"},
  { "optionXT" , "   [-XT (utilizza translet per la trasformazione, se possibile)]"},
  { "diagTiming"," --------- Trasformazione di {0} tramite {1} completata in {2} ms" },
  { "recursionTooDeep","Nidificazione dei modelli troppo profonda. Nidificazione = {0}, modello {1} {2}" },
  { "nameIs", "il nome \u00e8 " },
  { "matchPatternIs", "il modello di corrispondenza \u00e8 " }
  
 };

 // ================= INFRASTRUCTURE ======================

 /** String for use when a bad error code was encountered.    */
 public static final String BAD_CODE = "BAD_CODE";

 /** String for use when formatting of the error string failed.   */
 public static final String FORMAT_FAILED = "FORMAT_FAILED";

 /** General error string.   */
 public static final String ERROR_STRING = "#error";

 /** String to prepend to error messages.  */
 public static final String ERROR_HEADER = "Errore: ";

 /** String to prepend to warning messages.    */
 public static final String WARNING_HEADER = "Avvertenza: ";

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
}



