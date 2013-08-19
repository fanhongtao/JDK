/*
 * @(#)XSLTErrorResources_fr.java	1.3 03/04/25
 *
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

public class XSLTErrorResources_fr extends XSLTErrorResources
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
      "Erreur : L''expression ne peut pas contenir ''{''"},

  /** ER_ILLEGAL_ATTRIBUTE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  {
    ER_ILLEGAL_ATTRIBUTE, "{0} dispose d''un attribut non autoris\u00e9 : {1}"},

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  //public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  {
    ER_NULL_SOURCENODE_APPLYIMPORTS,
      "sourceNode est vide dans xsl:apply-imports!"},

  /** ER_CANNOT_ADD          */
  //public static final int ER_CANNOT_ADD = 4;

  {
    ER_CANNOT_ADD, "Impossible d''ajouter {0} \u00e0 {1}"},

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  //public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  {
    ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "sourceNode est vide dans handleApplyTemplatesInstruction!"},

  /** ER_NO_NAME_ATTRIB          */
  //public static final int ER_NO_NAME_ATTRIB = 6;

  {
    ER_NO_NAME_ATTRIB, "{0} doit disposer d''un attribut name."},

  /** ER_TEMPLATE_NOT_FOUND          */
  //public static final int ER_TEMPLATE_NOT_FOUND = 7;

  {
    ER_TEMPLATE_NOT_FOUND, "Impossible de trouver le mod\u00e8le : {0}"},

  /** ER_CANT_RESOLVE_NAME_AVT          */
  //public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  {
    ER_CANT_RESOLVE_NAME_AVT,
      "Impossible de r\u00e9soudre le nom AVT dans xsl:call-template."},

  /** ER_REQUIRES_ATTRIB          */
  //public static final int ER_REQUIRES_ATTRIB = 9;

  {
    ER_REQUIRES_ATTRIB, "{0} requiert l''attribut : {1}"},

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  //public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  {
    ER_MUST_HAVE_TEST_ATTRIB,
      "{0} doit disposer de l''attribut ''''test''''."},

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  //public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  {
    ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "Valeur incorrecte pour l''attribut level : {0}"},

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  {
    ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "Le nom de processing-instruction ne peut pas \u00eatre ''xml''"},

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  {
    ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "Le nom de processing-instruction doit \u00eatre un nom sans deux points correct : {0}"},

  /** ER_NEED_MATCH_ATTRIB          */
  //public static final int ER_NEED_MATCH_ATTRIB = 14;

  {
    ER_NEED_MATCH_ATTRIB,
      "{0} doit disposer d''un attribut conforme s''il a un mode."},

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  //public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  {
    ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} requiert soit un nom soit un attribut conforme."},

  /** ER_CANT_RESOLVE_NSPREFIX          */
  //public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  {
    ER_CANT_RESOLVE_NSPREFIX,
      "Impossible de r\u00e9soudre le pr\u00e9fixe d''espace de noms : {0}"},

  /** ER_ILLEGAL_VALUE          */
  //public static final int ER_ILLEGAL_VALUE = 17;

  {
    ER_ILLEGAL_VALUE, "xml:space dispose d''une valeur non autoris\u00e9e : {0}"},

  /** ER_NO_OWNERDOC          */
  //public static final int ER_NO_OWNERDOC = 18;

  {
    ER_NO_OWNERDOC,
      "Le n\u0153ud enfant ne dispose pas d''un document propri\u00e9taire !"},

  /** ER_ELEMTEMPLATEELEM_ERR          */
  //public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  {
    ER_ELEMTEMPLATEELEM_ERR, "Erreur ElemTemplateElement : {0}"},

  /** ER_NULL_CHILD          */
  //public static final int ER_NULL_CHILD = 20;

  {
    ER_NULL_CHILD, "Tentative d''ajout d''un enfant vide !"},

  /** ER_NEED_SELECT_ATTRIB          */
  //public static final int ER_NEED_SELECT_ATTRIB = 21;

  {
    ER_NEED_SELECT_ATTRIB, "{0} requiert un attribut select."},

  /** ER_NEED_TEST_ATTRIB          */
  //public static final int ER_NEED_TEST_ATTRIB = 22;

  {
    ER_NEED_TEST_ATTRIB,
      "xsl:when doit disposer d''un attribut ''test''."},

  /** ER_NEED_NAME_ATTRIB          */
  //public static final int ER_NEED_NAME_ATTRIB = 23;

  {
    ER_NEED_NAME_ATTRIB,
      "xsl:with-param doit disposer d''un attribut ''name''."},

  /** ER_NO_CONTEXT_OWNERDOC          */
  //public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  {
    ER_NO_CONTEXT_OWNERDOC,
      "Le contexte ne dispose pas d''un document propri\u00e9taire !"},

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  //public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  {
    ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "Impossible de cr\u00e9er XML TransformerFactory Liaison : {0}"},

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  {
    ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: \u00e9chec du traitement."},

  /** ER_NOT_SUCCESSFUL          */
  //public static final int ER_NOT_SUCCESSFUL = 27;

  {
    ER_NOT_SUCCESSFUL, "Xalan: \u00e9chec."},

  /** ER_ENCODING_NOT_SUPPORTED          */
  //public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  {
    ER_ENCODING_NOT_SUPPORTED, "Encodage non pris en charge : {0}"},

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  //public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  {
    ER_COULD_NOT_CREATE_TRACELISTENER,
      "Impossible de cr\u00e9er TraceListener : {0}"},

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  {
    ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key requiert un attribut ''name'' !"},

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;

  {
    ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key requiert un attribut ''match'' !"},

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  {
    ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key requiert un attribut ''use'' !"},

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  //public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  {
    ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} requiert un attribut ''''elements'''' !"},

  /** ER_MISSING_PREFIX_ATTRIB          */
  //public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  {
    ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} attribut ''''prefix'''' manquant"},

  /** ER_BAD_STYLESHEET_URL          */
  //public static final int ER_BAD_STYLESHEET_URL = 35;

  {
    ER_BAD_STYLESHEET_URL, "L''URL de la feuille de style n''est pas correct : {0}"},

  /** ER_FILE_NOT_FOUND          */
  //public static final int ER_FILE_NOT_FOUND = 36;

  {
    ER_FILE_NOT_FOUND, "Le fichier de feuille de style est introuvable : {0}"},

  /** ER_IOEXCEPTION          */
  //public static final int ER_IOEXCEPTION = 37;

  {
    ER_IOEXCEPTION,
      "Exception d''E/S avec le fichier de feuille de style : {0}"},

  /** ER_NO_HREF_ATTRIB          */
  //public static final int ER_NO_HREF_ATTRIB = 38;

  {
    ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) Impossible de trouver l''attribut href pour {0}"},

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  //public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  {
    ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} est directement ou indirectement inclus dans lui-m\u00eame !"},

  /** ER_PROCESSINCLUDE_ERROR          */
  //public static final int ER_PROCESSINCLUDE_ERROR = 40;

  {
    ER_PROCESSINCLUDE_ERROR,
      "Erreur StylesheetHandler.processInclude, {0}"},

  /** ER_MISSING_LANG_ATTRIB          */
  //public static final int ER_MISSING_LANG_ATTRIB = 41;

  {
    ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} attribut ''''lang'''' manquant"},

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  //public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  {
    ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) \u00e9l\u00e9ment {0} mal plac\u00e9 ?? El\u00e9ment ''''component'''' de container manquant"},

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  //public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  {
    ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "Sortie possible uniquement vers Element, DocumentFragment, Document ou PrintWriter."},

  /** ER_PROCESS_ERROR          */
  //public static final int ER_PROCESS_ERROR = 44;

  {
    ER_PROCESS_ERROR, "Erreur StylesheetRoot.process"},

  /** ER_UNIMPLNODE_ERROR          */
  //public static final int ER_UNIMPLNODE_ERROR = 45;

  {
    ER_UNIMPLNODE_ERROR, "Erreur UnImplNode : {0}"},

  /** ER_NO_SELECT_EXPRESSION          */
  //public static final int ER_NO_SELECT_EXPRESSION = 46;

  {
    ER_NO_SELECT_EXPRESSION,
      "Erreur ! Impossible de trouver l''expression de s\u00e9lection xpath (-select)."},

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  //public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  {
    ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "Impossible de mettre en s\u00e9rie un processeur XSL !"},

  /** ER_NO_INPUT_STYLESHEET          */
  //public static final int ER_NO_INPUT_STYLESHEET = 48;

  {
    ER_NO_INPUT_STYLESHEET,
      "Entr\u00e9e de la feuille de style non sp\u00e9cifi\u00e9e !"},

  /** ER_FAILED_PROCESS_STYLESHEET          */
  //public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  {
    ER_FAILED_PROCESS_STYLESHEET,
      "Echec de traitement de la feuille de style !"},

  /** ER_COULDNT_PARSE_DOC          */
  //public static final int ER_COULDNT_PARSE_DOC = 50;

  {
    ER_COULDNT_PARSE_DOC, "Impossible d''analyser le document {0} !"},

  /** ER_COULDNT_FIND_FRAGMENT          */
  //public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  {
    ER_COULDNT_FIND_FRAGMENT, "Impossible de trouver le fragment\u00a0: {0}"},

  /** ER_NODE_NOT_ELEMENT          */
  //public static final int ER_NODE_NOT_ELEMENT = 52;

  {
    ER_NODE_NOT_ELEMENT,
      "Le n\u0153ud identifi\u00e9 par l''identificateur de fragments n''est pas un \u00e9lement : {0}"},

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  {
    ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each doit disposer d''un attribut match ou name"},

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  {
    ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "templates doit disposer d''un attribut match ou name"},

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  //public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  {
    ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "Impossible de cloner un fragment de document !"},

  /** ER_CANT_CREATE_ITEM          */
  //public static final int ER_CANT_CREATE_ITEM = 56;

  {
    ER_CANT_CREATE_ITEM,
      "Impossible de cr\u00e9er un objet dans l''arbre de r\u00e9sultats : {0}"},

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  //public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  {
    ER_XMLSPACE_ILLEGAL_VALUE,
      "xml:space dispose d''une valeur non autoris\u00e9e dans la source XML : {0}"},

  /** ER_NO_XSLKEY_DECLARATION          */
  //public static final int ER_NO_XSLKEY_DECLARATION = 58;

  {
    ER_NO_XSLKEY_DECLARATION,
      "Il n''existe pas de d\u00e9claration xsl:key pour for {0} !"},

  /** ER_CANT_CREATE_URL          */
  //public static final int ER_CANT_CREATE_URL = 59;

  {
    ER_CANT_CREATE_URL, "Erreur! Impossible de cr\u00e9er une url pour\u00a0: {0}"},

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  //public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  {
    ER_XSLFUNCTIONS_UNSUPPORTED, "xsl:functions n''est pas pris en charge"},

  /** ER_PROCESSOR_ERROR          */
  //public static final int ER_PROCESSOR_ERROR = 61;

  {
    ER_PROCESSOR_ERROR, "Erreur XSLT TransformerFactory"},

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  //public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  {
    ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} non autoris\u00e9 dans une feuille de style !"},

  /** ER_RESULTNS_NOT_SUPPORTED          */
  //public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  {
    ER_RESULTNS_NOT_SUPPORTED,
      "result-ns n''est plus pris en charge ! Utilisez xsl:output \u00e0 la place."},

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  //public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  {
    ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space n''est plus pris en charge ! Utilisez xsl:strip-space ou xsl:preserve-space \u00e0 la place."},

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  //public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  {
    ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result n''est plus pris en charge ! Utilisez xsl:output \u00e0 la place."},

  /** ER_ILLEGAL_ATTRIB          */
  //public static final int ER_ILLEGAL_ATTRIB = 66;

  {
    ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} dispose d''un attribut non autoris\u00e9 : {1}"},

  /** ER_UNKNOWN_XSL_ELEM          */
  //public static final int ER_UNKNOWN_XSL_ELEM = 67;

  {
    ER_UNKNOWN_XSL_ELEM, "El\u00e9ment XSL inconnu : {0}"},

  /** ER_BAD_XSLSORT_USE          */
  //public static final int ER_BAD_XSLSORT_USE = 68;

  {
    ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort ne peut \u00eatre utilis\u00e9 qu''avec xsl:apply-templates ou xsl:for-each."},

  /** ER_MISPLACED_XSLWHEN          */
  //public static final int ER_MISPLACED_XSLWHEN = 69;

  {
    ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) xsl:when mal plac\u00e9 !"},

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  {
    ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:choose n''est pas parent de xsl:when !"},

  /** ER_MISPLACED_XSLOTHERWISE          */
  //public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  {
    ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) xsl:otherwise mal plac\u00e9 !"},

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  {
    ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:choose n''est pas parent de xsl:otherwise !"},

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  //public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  {
    ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} n''est pas admis dans un mod\u00e8le !"},

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  //public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  {
    ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} pr\u00e9fixe de l''espace de noms de l''extension {1} inconnu"},

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  //public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  {
    ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Les importations ne peuvent intervenir qu''en tant que premiers \u00e9l\u00e9ments de la feuille de style !"},

  /** ER_IMPORTING_ITSELF          */
  //public static final int ER_IMPORTING_ITSELF = 76;

  {
    ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} est en train de s''importer directement ou indirectement !"},

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  //public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  {
    ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) " + "xml:space dispose d''une valeur non autoris\u00e9e : {0}"},

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  {
    ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "Echec de processStylesheet !"},

  /** ER_SAX_EXCEPTION          */
  //public static final int ER_SAX_EXCEPTION = 79;

  {
    ER_SAX_EXCEPTION, "Exception SAX"},

  /** ER_FUNCTION_NOT_SUPPORTED          */
  //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  {
    ER_FUNCTION_NOT_SUPPORTED, "Fonction non prise en charge !"},

  /** ER_XSLT_ERROR          */
  //public static final int ER_XSLT_ERROR = 81;

  {
    ER_XSLT_ERROR, "Erreur XSLT"},

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  //public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  {
    ER_CURRENCY_SIGN_ILLEGAL,
      "Le symbole d''une devise n''est pas admise dans une cha\u00eene conforme au mod\u00e8le"},

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  //public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  {
    ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "La fonction Document n''est pas prise en charge dans la feuille de style DOM !"},

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  //public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  {
    ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "Impossible de r\u00e9soudre le pr\u00e9fixe d''un r\u00e9solveur sans pr\u00e9fixe !"},

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  //public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  {
    ER_REDIRECT_COULDNT_GET_FILENAME,
      "Redirect extension : impossible de r\u00e9cup\u00e9rer le nom de fichier - l''attribut file ou select doit retourner une cha\u00eene valide."},

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  //public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  {
    ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "Impossible de cr\u00e9er FormatterListener dans Redirect extension !"},

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  //public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  {
    ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "Le pr\u00e9fixe dans exclude-result-prefixes n''est pas valide : {0}"},

  /** ER_MISSING_NS_URI          */
  //public static final int ER_MISSING_NS_URI = 88;

  {
    ER_MISSING_NS_URI,
      "URI d''espace de noms manquant pour le pr\u00e9fixe sp\u00e9cifi\u00e9"},

  /** ER_MISSING_ARG_FOR_OPTION          */
  //public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  {
    ER_MISSING_ARG_FOR_OPTION,
      "Argument manquant pour l''option : {0}"},

  /** ER_INVALID_OPTION          */
  //public static final int ER_INVALID_OPTION = 90;

  {
    ER_INVALID_OPTION, "Option incorrecte : {0}"},

  /** ER_MALFORMED_FORMAT_STRING          */
  //public static final int ER_MALFORMED_FORMAT_STRING = 91;

  {
    ER_MALFORMED_FORMAT_STRING, "Cha\u00eene de format mal form\u00e9e : {0}"},

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  {
    ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet requiert un attribut ''version'' !"},

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  {
    ER_ILLEGAL_ATTRIBUTE_VALUE,
      "L''attribut : {0} dispose d''une valeur non autoris\u00e9e : {1}"},

  /** ER_CHOOSE_REQUIRES_WHEN          */
  //public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  {
    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose requiert un xsl:when"},

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  //public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  {
    ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports n''est pas admis dans un xsl:for-each"},

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  {
    ER_CANT_USE_DTM_FOR_OUTPUT,
      "Impossible d''utiliser DTMLiaison pour un noeud de sortie DOM ... Utilisez org.apache.xpath.DOM2Helper \u00e0 la place !"},

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;

  {
    ER_CANT_USE_DTM_FOR_INPUT,
      "Impossible d''utiliser DTMLiaison pour un noeud d''entr\u00e9e DOM... Utilisez org.apache.xpath.DOM2Helper \u00e0 la place!"},

  /** ER_CALL_TO_EXT_FAILED          */
  //public static final int ER_CALL_TO_EXT_FAILED = 98;

  {
    ER_CALL_TO_EXT_FAILED,
      "Echec de l''appel de l''\u00e9l\u00e9ment d''extension : {0}"},

  /** ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 99;

  {
    ER_PREFIX_MUST_RESOLVE,
      "Le pr\u00e9fixe doit se r\u00e9soudre en espace de nom : {0}"},

  /** ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 100;

  {
    ER_INVALID_UTF16_SURROGATE,
      "Substitut UTF-16 incorrect d\u00e9tect\u00e9 : {0} ?"},

  /** ER_XSLATTRSET_USED_ITSELF          */
  //public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  {
    ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} s''est utilis\u00e9 lui-m\u00eame, ce qui va entra\u00eener une boucle sans fin."},

  /** ER_CANNOT_MIX_XERCESDOM          */
  //public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  {
    ER_CANNOT_MIX_XERCESDOM,
      "Impossible de m\u00e9langer des entr\u00e9es non Xerces-DOM avec des sorties Xerces-DOM !"},

  /** ER_TOO_MANY_LISTENERS          */
  //public static final int ER_TOO_MANY_LISTENERS = 103;

  {
    ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  //public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;

  {
    ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "Dans ElemTemplateElement.readObject : {0}"},

  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  //public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  {
    ER_DUPLICATE_NAMED_TEMPLATE,
      "Plusieurs mod\u00e8les trouv\u00e9s nomm\u00e9s : {0}"},

  /** ER_INVALID_KEY_CALL          */
  //public static final int ER_INVALID_KEY_CALL = 106;

  {
    ER_INVALID_KEY_CALL,
      "Appel de fonction incorrect : les appels de recursive key() ne sont pas autoris\u00e9s"},
  
  /** Variable is referencing itself          */
  //public static final int ER_REFERENCING_ITSELF = 107;

  {
    ER_REFERENCING_ITSELF,
      "La variable {0} est en train de se r\u00e9f\u00e9rencer directement ou indirectement !"},
  
  /** Illegal DOMSource input          */
  //public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  {
    ER_ILLEGAL_DOMSOURCE_INPUT,
      "Le n\u0153ud d''entr\u00e9e ne peut pas \u00eatre vide au niveau d''une source DOM pour newTemplates !"},
	
	/** Class not found for option         */
  //public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  {
    ER_CLASS_NOT_FOUND_FOR_OPTION,
			"Fichier de classe introuvable pour l''option {0}"},
	
	/** Required Element not found         */
  //public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  {
    ER_REQUIRED_ELEM_NOT_FOUND,
			"El\u00e9ment requis introuvable : {0}"},
  
  /** InputStream cannot be null         */
  //public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  {
    ER_INPUT_CANNOT_BE_NULL,
			"InputStream ne peut pas \u00eatre vide"},
  
  /** URI cannot be null         */
  //public static final int ER_URI_CANNOT_BE_NULL = 112;

  {
    ER_URI_CANNOT_BE_NULL,
			"URI ne peut pas \u00eatre vide"},
  
  /** File cannot be null         */
  //public static final int ER_FILE_CANNOT_BE_NULL = 113;

  {
    ER_FILE_CANNOT_BE_NULL,
			"File ne peut pas \u00eatre vide"},
  
   /** InputSource cannot be null         */
  //public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  {
    ER_SOURCE_CANNOT_BE_NULL,
			"InputSource ne peut pas \u00eatre vide"},
  
  /** Can''t overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"Impossible d''\u00e9craser la cause"},
  
  /** Could not initialize BSF Manager        */
  //public static final int ER_CANNOT_INIT_BSFMGR = 116;

  {
    ER_CANNOT_INIT_BSFMGR,
			"Impossible d''initialiser BSF Manager"},
  
  /** Could not compile extension       */
  //public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  {
    ER_CANNOT_CMPL_EXTENSN,
			"Impossible de compiler l''extension"},

  /** Could not create extension       */
  //public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  {
    ER_CANNOT_CREATE_EXTENSN,
      "Impossible de cr\u00e9er l''extension : {0} \u00e0 cause de : {1}"},
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  //public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  {
    ER_INSTANCE_MTHD_CALL_REQUIRES,
      "L''appel de la m\u00e9thode d''instance \u00e0 la m\u00e9thode {0} requiert une instance Object comme premier argument"},
  
  /** Invalid element name specified       */
  //public static final int ER_INVALID_ELEMENT_NAME = 120;

  {
    ER_INVALID_ELEMENT_NAME,
      "Nom d''\u00e9l\u00e9ment sp\u00e9cifi\u00e9 incorrect {0}"},
  
   /** Element name method must be static      */
  //public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  {
    ER_ELEMENT_NAME_METHOD_STATIC,
      "La m\u00e9thode de nom d''\u00e9l\u00e9ment doit \u00eatre statique {0}"},
  
   /** Extension function {0} : {1} is unknown      */
  //public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  {
    ER_EXTENSION_FUNC_UNKNOWN,
             "Fonction d''extension {0} : {1} inconnue"},
  
   /** More than one best match for constructor for       */
  //public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  {
    ER_MORE_MATCH_CONSTRUCTOR,
             "Plusieurs occurrences exactes pour le constructeur pour {0}"},
  
   /** More than one best match for method      */
  //public static final int ER_MORE_MATCH_METHOD = 124;

  {
    ER_MORE_MATCH_METHOD,
             "Plusieurs occurrences exactes pour la m\u00e9thode {0}"},
  
   /** More than one best match for element method      */
  //public static final int ER_MORE_MATCH_ELEMENT = 125;

  {
    ER_MORE_MATCH_ELEMENT,
             " Plusieurs occurrences exactes pour la m\u00e9thode d''\u00e9l\u00e9ments {0}"},
  
   /** Invalid context passed to evaluate       */
  //public static final int ER_INVALID_CONTEXT_PASSED = 126;

  {
    ER_INVALID_CONTEXT_PASSED,
             "Contexte incorrect pour l''\u00e9valuation {0}"},
  
   /** Pool already exists       */
  //public static final int ER_POOL_EXISTS = 127;

  {
    ER_POOL_EXISTS,
             "Pool existe d\u00e9j\u00e0"},
  
   /** No driver Name specified      */
  //public static final int ER_NO_DRIVER_NAME = 128;

  {
    ER_NO_DRIVER_NAME,
             "Aucun nom de pilote sp\u00e9cifi\u00e9"},
  
   /** No URL specified     */
  //public static final int ER_NO_URL = 129;

  {
    ER_NO_URL,
             "Aucune URL sp\u00e9cifi\u00e9e"},
  
   /** Pool size is less than one    */
  //public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  {
    ER_POOL_SIZE_LESSTHAN_ONE,
             "La taille du Pool est inf\u00e9rieure \u00e0 un !"},
  
   /** Invalid driver name specified    */
  //public static final int ER_INVALID_DRIVER = 131;

  {
    ER_INVALID_DRIVER,
             "Le nom de pilote sp\u00e9cifi\u00e9 n''est pas correct !"},
  
   /** Did not find the stylesheet root    */
  //public static final int ER_NO_STYLESHEETROOT = 132;

  {
    ER_NO_STYLESHEETROOT,
             "Impossible de trouver la feuille de style racine !"},
  
   /** Illegal value for xml:space     */
  //public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  {
    ER_ILLEGAL_XMLSPACE_VALUE,
         "Valeur non autoris\u00e9e pour xml:space"},
  
   /** processFromNode failed     */
  //public static final int ER_PROCESSFROMNODE_FAILED = 134;

  {
    ER_PROCESSFROMNODE_FAILED,
         "Echec de processFromNode"},
  
   /** The resource [] could not load:     */
  //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  {
    ER_RESOURCE_COULD_NOT_LOAD,
        "La ressource [ {0} ] n''a pas pu \u00eatre charg\u00e9e : {1} \n {2} \t {3}"},
   
  
   /** Buffer size <=0     */
  //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  {
    ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Taille du tampon <=0"},
  
   /** Unknown error when calling extension    */
  //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  {
    ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "Erreur inconnue lors de l''appel de l''extension"},
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  //public static final int ER_NO_NAMESPACE_DECL = 138;

  {
    ER_NO_NAMESPACE_DECL,
        "Le pr\u00e9fixe {0} de dispose pas d''une d\u00e9claration d''espaces de noms correspondante"},
  
   /** Element content not allowed for lang=javaclass   */
  //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  {
    ER_ELEM_CONTENT_NOT_ALLOWED,
        "Contenu d''\u00e9l\u00e9ment non autoris\u00e9 pour lang=javaclass {0}"},
  
   /** Stylesheet directed termination   */
  //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  {
    ER_STYLESHEET_DIRECTED_TERMINATION,
        "Ach\u00e8vement dirig\u00e9 de la feuille de style"},
  
   /** 1 or 2   */
  //public static final int ER_ONE_OR_TWO = 141;

  {
    ER_ONE_OR_TWO,
        "1 ou 2"},
  
   /** 2 or 3   */
  //public static final int ER_TWO_OR_THREE = 142;

  {
    ER_TWO_OR_THREE,
        "2 ou 3"},

   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  {
    ER_COULD_NOT_LOAD_RESOURCE,
        "Impossible de charger {0} (v\u00e9rifier le CHEMIN DE CLASSE). Utilisation des mod\u00e8les par d\u00e9faut"},
  
   /** Cannot initialize default templates   */
  //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  {
    ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "Impossible d''initialiser les mod\u00e8les par d\u00e9faut"},
  
   /** Result should not be null   */
  //public static final int ER_RESULT_NULL = 145;

  {
    ER_RESULT_NULL,
        "Le r\u00e9sultat ne peut pas \u00eatre vide"},
    
   /** Result could not be set   */
  //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  {
    ER_RESULT_COULD_NOT_BE_SET,
        "Le r\u00e9sultat ne peut pas \u00eatre d\u00e9fini"},
  
   /** No output specified   */
  //public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  {
    ER_NO_OUTPUT_SPECIFIED,
        "Aucune sortie sp\u00e9cifi\u00e9e"},
  
   /** Can''t transform to a Result of type   */
  //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  {
    ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "Transformation impossible en un r\u00e9sultat de type {0}"},
  
   /** Can''t transform to a Source of type   */
  //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  {
    ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "Transformation impossible d''une source de type {0}"},
  
   /** Null content handler  */
  //public static final int ER_NULL_CONTENT_HANDLER = 150;

  {
    ER_NULL_CONTENT_HANDLER,
        "Gestionnaire de contenu vide"},
  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 151;

  {
    ER_NULL_ERROR_HANDLER,
        "Gestionnaire d''erreurs vide"},
  
   /** parse can not be called if the ContentHandler has not been set */
  //public static final int ER_CANNOT_CALL_PARSE = 152;

  {
    ER_CANNOT_CALL_PARSE,
        "L''analyse ne peut \u00eatre appel\u00e9e si le gestionnaire de contenu n''a pas \u00e9t\u00e9 d\u00e9fini"},
  
   /**  No parent for filter */
  //public static final int ER_NO_PARENT_FOR_FILTER = 153;

  {
    ER_NO_PARENT_FOR_FILTER,
        "Aucun parent pour le filtre"},
  
  
   /**  No stylesheet found in: {0}, media */
  //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  {
    ER_NO_STYLESHEET_IN_MEDIA,
         "Aucune feuille de style trouv\u00e9e dans: {0}, media= {1}"},
  
   /**  No xml-stylesheet PI found in */
  //public static final int ER_NO_STYLESHEET_PI = 155;

  {
    ER_NO_STYLESHEET_PI,
         "Aucun xml-stylesheet PI trouv\u00e9 dans : {0}"},
  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;

  {
    ER_NO_DEFAULT_IMPL,
         "Aucune mise en \u0153uvre par d\u00e9faut trouv\u00e9e"},
  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0}) non pris en charge pour le moment"},
  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "Impression plus importante que l''emplacement"},
  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;

  {
    ER_COROUTINE_NOT_AVAIL,
       "Coroutine indisponible, id={0}"},
  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;

  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager a re\u00e7u une requ\u00eate co_exit()"},
  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;

  {
    ER_COJOINROUTINESET_FAILED,
       "Echec de co_joinCoroutineSet()"},
  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;

  {
    ER_COROUTINE_PARAM,
       "Erreur de param\u00e8tre Coroutine ({0})"},
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: R\u00e9ponses de Parser doTerminate {0}"},
  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "parse ne peut pas \u00eatre appel\u00e9 pendant l''op\u00e9ration d''analyse"},
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Erreur : l''it\u00e9rateur saisi pour l''axe {0} n''est pas mis en oeuvre"},
  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Erreur : l''it\u00e9rateur pour l''axe {0} n''est pas mis en oeuvre "},
  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "Clone d''it\u00e9rateur non pris en charge"},
  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  {
    ER_UNKNOWN_AXIS_TYPE,
       "Type d''axe transversal inconnu : {0}"},
  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;

  {
    ER_AXIS_NOT_SUPPORTED,
       "Axe transversal non pris en charge : {0}"},
  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;

  {
    ER_NO_DTMIDS_AVAIL,
       "Aucun ID DTM disponible"},
  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;

  {
    ER_NOT_SUPPORTED,
       "Non pris en charge : {0}"},
  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;

  {
    ER_NODE_NON_NULL,
       "Le n\u0153ud ne doit pas \u00eatre vide pour getDTMHandleFromNode"},
  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  {
    ER_COULD_NOT_RESOLVE_NODE,
       "Impossible de r\u00e9soudre le noeud en descripteur"},
  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  {
    ER_STARTPARSE_WHILE_PARSING,
       "startParse ne peut pas \u00eatre appel\u00e9 pendant l''analyse"},
  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse requiert un SAXParser non vide"},
  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;

  {
    ER_COULD_NOT_INIT_PARSER,
       "Impossible d''initialiser l''analyseur avec"},
  
   /**  Value for property {0} should be a Boolean instance  */
  //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  {
    ER_PROPERTY_VALUE_BOOLEAN,
       "La valeur pour la propri\u00e9t\u00e9 {0} doit \u00eatre une instance bool\u00e9enne"},
  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;

  {
    ER_EXCEPTION_CREATING_POOL,
       "Exception\u00a0de cr\u00e9ation d''une nouvelle instance pour le pool"},
  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "Le chemin contient une s\u00e9quence de remplacement incorrecte"},
  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;

  {
    ER_SCHEME_REQUIRED,
       "Le mod\u00e8le est requis !"},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;

  {
    ER_NO_SCHEME_IN_URI,
       "Aucun mod\u00e8le trouv\u00e9 dans l''URI : {0}"},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;

  {
    ER_NO_SCHEME_INURI,
       "Aucun mod\u00e8le trouv\u00e9 dans l''URI"},
  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;

  {
    ER_PATH_INVALID_CHAR,
       "Le chemin contient des caract\u00e8res incorrects : {0}"},
  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  {
    ER_SCHEME_FROM_NULL_STRING,
       "Impossible de d\u00e9finir le mod\u00e8le \u00e0 partir d''une cha\u00eene vide"},
  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  {
    ER_SCHEME_NOT_CONFORMANT,
       "Le mod\u00e8le n''est pas conforme."},
  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "L''h\u00f4te n''est pas constitu\u00e9 d''une adresse bien form\u00e9e"},
  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;

  {
    ER_PORT_WHEN_HOST_NULL,
       "Le port ne peut pas \u00eatre d\u00e9fini lorsque l''h\u00f4te est vide"},
  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;

  {
    ER_INVALID_PORT,
       "Num\u00e9ro de port incorrect"},
  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  {
    ER_FRAG_FOR_GENERIC_URI,
       "Le fragment ne peut \u00eatre d\u00e9fini que pour un URI g\u00e9n\u00e9rique"},
  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  {
    ER_FRAG_WHEN_PATH_NULL,
       "Le fragment ne peut pas \u00eatre d\u00e9fini lorsque le chemin est vide"},
  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;

  {
    ER_FRAG_INVALID_CHAR,
       "Le fragment contient des caract\u00e8res incorrects"},
  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;

  {
    ER_PARSER_IN_USE,
        "L''analyseur est d\u00e9j\u00e0 en cours d''utilisation"},
  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "Impossible de modifier {0} {1} pendant la phase d''analyse"},
  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "Lien de causalit\u00e9 vers soi impossible"},
  
   /* This key/message changed ,NEED ER_COULD_NOT_FIND_EXTERN_SCRIPT: Pending,Ramesh */

   /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
       "Impossible de trouver le script externe dans {0}"},
  
  /** The resource [] could not be found     */
  //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  {
    ER_RESOURCE_COULD_NOT_FIND,
        "Impossible de trouver la ressource [ {0} ].\n {1}"},
  
   /** output property not recognized:  */
  //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  {
    ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "La propri\u00e9t\u00e9 de sortie n''a pas \u00e9t\u00e9 reconnue : {0}"},
  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  {
    ER_NO_USERINFO_IF_NO_HOST,
        "Les informations sur l''utilisateur ne peuvent pas \u00eatre sp\u00e9cifi\u00e9es si l''h\u00f4te n''est pas sp\u00e9cifi\u00e9"},
  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;

  {
    ER_NO_PORT_IF_NO_HOST,
        "Le port ne peut pas \u00eatre sp\u00e9cifi\u00e9 si l''h\u00f4te n''est pas sp\u00e9cifi\u00e9"},
  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  {
    ER_NO_QUERY_STRING_IN_PATH,
        "La cha\u00eene de requ\u00eate ne peut pas \u00eatre sp\u00e9cifi\u00e9e dans le chemin et dans la cha\u00eene de requ\u00eate"},
  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "Le fragment ne peut pas \u00eatre sp\u00e9cifi\u00e9 dans le chemin et dans le fragment"},
  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "Impossible d''initialiser l''URI avec des param\u00e8tres vides"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  {
    ER_FAILED_CREATING_ELEMLITRSLT,
        "Echec de cr\u00e9ation de l''instance ElemLiteralResult"},

  // Earlier (JDK 1.4 XALAN 2.2-D11) at key code ''204'' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed. 
  
   /** Priority value does not contain a parsable number   */
  //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;

  {
    ER_VALUE_SHOULD_BE_NUMBER,
        "La valeur de {0} doit contenir un nombre analysable"}, 
  
   /**  Value for {0} should equal ''yes'' or ''no''   */
  //public static final int ER_VALUE_SHOULD_EQUAL = 205;

  {
    ER_VALUE_SHOULD_EQUAL,
        "La valeur pour {0} doit \u00eatre \u00e9quivalente \u00e0 oui ou non"},
 
   /**  Failed calling {0} method   */
  //public static final int ER_FAILED_CALLING_METHOD = 206;

  {
    ER_FAILED_CALLING_METHOD,
        "Echec d''appel de la m\u00e9thode {0}"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  {
    ER_FAILED_CREATING_ELEMTMPL,
        "Echec de cr\u00e9ation de l''instance ElemTemplateElement"},
  
   /**  Characters are not allowed at this point in the document   */
  //public static final int ER_CHARS_NOT_ALLOWED = 208;

  {
    ER_CHARS_NOT_ALLOWED,
        "Les caract\u00e8res ne sont pas admis \u00e0 ce niveau du document"},
  
  /**  attribute is not allowed on the element   */
  //public static final int ER_ATTR_NOT_ALLOWED = 209;

  {
    ER_ATTR_NOT_ALLOWED,
        "L''attribut \"{0}\" n''est pas admis dans l''\u00e9l\u00e9ment {1} !"},
  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;

  {
    ER_METHOD_NOT_SUPPORTED,
        "M\u00e9thode non prise en charge pour le moment"},
 
  /**  Bad value    */
  //public static final int ER_BAD_VALUE = 211;

  {
    ER_BAD_VALUE,
     "{0} valeur incorrecte {1}"},
  
  /**  attribute value not found   */
  //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  {
    ER_ATTRIB_VALUE_NOT_FOUND,
     "Valeur de l''attribut {0} introuvable"},
  
  /**  attribute value not recognized    */
  //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  {
    ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "Valeur de l''attribut {0} non reconnue"},

  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter ne peut pas \u00eatre relanc\u00e9 pour le moment"},
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader pas avant la requ\u00eate startParse"},

  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;

  {
    ER_NULL_URI_NAMESPACE,
     "Tentative de g\u00e9n\u00e9ration d''un pr\u00e9fixe d''expace de nom avec un URI nul"},

   //XALAN_MANTIS CHANGES: Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11) 

   /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NUMBER_TOO_BIG = 217; 

  {
    ER_NUMBER_TOO_BIG,
     "Tentative de formatage d''un nombre sup\u00e9rieur \u00e0 l''entier le plus long"},

  //ER_CANNOT_FIND_SAX1_DRIVER

  //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;

  {
    ER_CANNOT_FIND_SAX1_DRIVER,
     "Classe de pilotes SAX1 {0} introuvable"},

  //ER_SAX1_DRIVER_NOT_LOADED
  //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;

  {
    ER_SAX1_DRIVER_NOT_LOADED,
     "La classe de pilotes SAX1 {0} a \u00e9t\u00e9 trouv\u00e9e mais n''''a pas \u00e9t\u00e9 charg\u00e9e"},

  //ER_SAX1_DRIVER_NOT_INSTANTIATED
  //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;

  {
    ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "La classe de pilotes SAX1 {0} a \u00e9t\u00e9 charg\u00e9e mais n''''a pas \u00e9t\u00e9 instanci\u00e9e"},


  // ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
  //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;

  {
    ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "La classe de pilotes SAX1 {0} n''''impl\u00e9mente pas org.xml.sax.Parser"},

  // ER_PARSER_PROPERTY_NOT_SPECIFIED
  //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;

  {
    ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "Les propri\u00e9t\u00e9s du syst\u00e8me org.xml.sax.parser n''ont pas \u00e9t\u00e9 sp\u00e9cifi\u00e9es"},

  //ER_PARSER_ARG_CANNOT_BE_NULL
  //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;

  {
    ER_PARSER_ARG_CANNOT_BE_NULL,
     "L''argument de l''analyseur ne doit pas \u00eatre nul"},


  // ER_FEATURE
  //public static final int  ER_FEATURE = 224;

  {
    ER_FEATURE,
     "Fonction : a {0}"},


  // ER_PROPERTY
  //public static final int ER_PROPERTY = 225 ;

  {
    ER_PROPERTY,
     "Propri\u00e9t\u00e9 : a {0}"},

  // ER_NULL_ENTITY_RESOLVER
  //public static final int ER_NULL_ENTITY_RESOLVER  = 226;

  {
    ER_NULL_ENTITY_RESOLVER,
     "Convertisseur d''entit\u00e9 nul"},

  // ER_NULL_DTD_HANDLER
  //public static final int  ER_NULL_DTD_HANDLER = 227 ;

  {
    ER_NULL_DTD_HANDLER,
     "Gestionnaire de DTD nul"},

  // No Driver Name Specified!
  //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;
  {
    ER_NO_DRIVER_NAME_SPECIFIED,
     "Aucun nom de pilote sp\u00e9cifi\u00e9 !"},


  // No URL Specified!
  //public static final int ER_NO_URL_SPECIFIED = 229;
  {
    ER_NO_URL_SPECIFIED,
     "Aucun URL sp\u00e9cifi\u00e9 !"},


  // Pool size is less than 1!
  //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;
  {
    ER_POOLSIZE_LESS_THAN_ONE,
     "La taille du pool est inf\u00e9rieure \u00e0 1 !"},


  // Invalid Driver Name Specified!
  //public static final int ER_INVALID_DRIVER_NAME = 231;
  {
    ER_INVALID_DRIVER_NAME,
     "Nom de pilote sp\u00e9cifi\u00e9 incorrect !"},



  // ErrorListener
  //public static final int ER_ERRORLISTENER = 232;
  {
    ER_ERRORLISTENER,
     "ErrorListener"},


  // Programmer''s error! expr has no ElemTemplateElement parent!
  //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;
  {
    ER_ASSERT_NO_TEMPLATE_PARENT,
     "Erreur du programmeur ! expr n''a pas de parent ElemTemplateElement !"},


  // Programmer''s assertion in RundundentExprEliminator: {0}
  //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;
  {
    ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "Assertion du programmeur dans RundundentExprEliminator : {0}"},

  // Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "La coupure d''axe n''est pas prise en charge : {0}"},

  // ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
  {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "ListingErrorHandler a \u00e9t\u00e9 cr\u00e9\u00e9 avec PrintWriter nul !"},

  // {0}is not allowed in this position in the stylesheet!
  //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;
  {
    ER_NOT_ALLOWED_IN_POSITION,
     "{0} n''est pas admis \u00e0 cet endroit de la feuille de style !"},

  // Non-whitespace text is not allowed in this position in the stylesheet!
  //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
  {
    ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "Le texte sans espace blanc n''est pas admis \u00e0 cet endroit de la feuille de style !"},

  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  //public static final int INVALID_TCHAR = 239;
  // SystemId Unknown
  {
    INVALID_TCHAR,
     "Valeur incorrecte : {1} est utilis\u00e9 pour l''attribut CHAR : {0}.  Un attribut de type CHAR doit \u00eatre compos\u00e9 d''un seul caract\u00e8re !"},

  //public static final int ER_SYSTEMID_UNKNOWN = 240;
  {
    ER_SYSTEMID_UNKNOWN,
     "SystemId inconnu"},

  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;
  {
    ER_LOCATION_UNKNOWN,
     "Emplacement de l''erreur inconnu"},

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
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut QNAME :a {0}"},

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
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut ENUM :a {0}.  Les valeurs correctes sont :a {2}."},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NMTOKEN

  // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
  //public static final int INVALID_NMTOKEN = 244;
  {
    INVALID_NMTOKEN,
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut NMTOKEN :a {0} "},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NCNAME

  // Illegal value:a {1} used for NCNAME attribute:a {0}.
  //public static final int INVALID_NCNAME = 245;
  {
    INVALID_NCNAME,
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut NCNAME :a {0} "},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_BOOLEAN

  // Illegal value:a {1} used for boolean attribute:a {0}.
  //public static final int INVALID_BOOLEAN = 246;

  {
    INVALID_BOOLEAN,
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut boolean :a {0} "},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NUMBER

  // Illegal value:a {1} used for number attribute:a {0}.
  //public static final int INVALID_NUMBER = 247;
  {
    INVALID_NUMBER,
     "Valeur incorrecte :a {1} utilis\u00e9 pour l''''attribut number :a {0} "},


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
     "L''''argument de {0} de la structure de comparaison doit \u00eatre un litt\u00e9ral."},

  // Note to translators:  The following message indicates that two definitions of
  // a variable.  A "global variable" is a variable that is accessible everywher
  // in the stylesheet.
  // ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790

  // Duplicate global variable declaration.
  //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;
  {
    ER_DUPLICATE_GLOBAL_VAR,
     "Duplication de la d\u00e9claration de variable globale."},


  // Note to translators:  The following message indicates that two definitions of
  // a variable were encountered.
  // ER_DUPLICATE_VAR - new error message for bugzilla #790

  // Duplicate variable declaration.
  //public static final int ER_DUPLICATE_VAR           = 250;
  {
    ER_DUPLICATE_VAR,
     "Duplication de la d\u00e9claration de variable."},

  // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
    // which must not be translated.
    // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789

  // xsl:template must have a name or match attribute (or both)
  //public static final int ER_TEMPLATE_NAME_MATCH     = 251;
  {
    ER_TEMPLATE_NAME_MATCH,
     "xsl:template doit avoir un attribut name ou match (ou les deux)"},

    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788

  // Prefix in exclude-result-prefixes is not valid:a {0}
  //public static final int ER_INVALID_PREFIX          = 252;
  {
    ER_INVALID_PREFIX,
     "Le pr\u00e9fixe de exclude-result-prefixes est incorrect :a {0}"},

    // Note to translators:  An "attribute set" is a set of attributes that can be
    // added to an element in the output document as a group.  The message indicates
    // that there was a reference to an attribute set named {0} that was never
    // defined.
    // ER_NO_ATTRIB_SET - new error message for bugzilla #782

  // attribute-set named {0} does not exist
  //public static final int ER_NO_ATTRIB_SET           = 253;
  {
    ER_NO_ATTRIB_SET,
     "L''''ensemble d''attributs {0} n''existe pas"},


  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  //public static final int WG_FOUND_CURLYBRACE = 1;

  {
    WG_FOUND_CURLYBRACE,
      "''}'' trouv\u00e9 mais aucun mod\u00e8le d''attribut ouvert !"},

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  {
    WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "Attention : l''attribut count ne correspond pas \u00e0 un anc\u00eatre dans xsl:number! Target = {0}"},

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  {
    WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "Ancienne syntaxe : le nom de l''attribut ''expr'' \u00e9t\u00e9 chang\u00e9 par ''select''."},

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  {
    WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan ne g\u00e8re pas encore la partie locale du nom dans la fonction format-number."},

  /** WG_LOCALE_NOT_FOUND          */
  //public static final int WG_LOCALE_NOT_FOUND = 5;

  {
    WG_LOCALE_NOT_FOUND,
      "Attention : Impossible de trouver la partie locale du nom pour xml:lang={0}"},

  /** WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  {
    WG_CANNOT_MAKE_URL_FROM,
      "Impossible de cr\u00e9er une URL \u00e0 partir de : {0}"},

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  {
    WG_CANNOT_LOAD_REQUESTED_DOC,
      "Impossible de charger le document demand\u00e9 : {0}"},

  /** WG_CANNOT_FIND_COLLATOR          */
  //public static final int WG_CANNOT_FIND_COLLATOR = 8;

  {
    WG_CANNOT_FIND_COLLATOR,
      "Impossible de trouver Collator pour <sort xml:lang={0}"},

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  {
    WG_FUNCTIONS_SHOULD_USE_URL,
      "Ancienne syntaxe : les fonctions doivent utiliser une url de {0}"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "Encodage non pris en charge : {0}, en utilisant UTF-8"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      " Encodage non pris en charge: {0}, en utilisant Java {1}"},

  /** WG_SPECIFICITY_CONFLICTS          */
  //public static final int WG_SPECIFICITY_CONFLICTS = 12;

  {
    WG_SPECIFICITY_CONFLICTS,
      "Conflits de sp\u00e9cificit\u00e9 d\u00e9tect\u00e9s : {0}, le dernier trouv\u00e9 dans la feuille de style sera utilis\u00e9."},

  /** WG_PARSING_AND_PREPARING          */
  //public static final int WG_PARSING_AND_PREPARING = 13;

  {
    WG_PARSING_AND_PREPARING,
      "========= Analyse et pr\u00e9paration {0} =========="},

  /** WG_ATTR_TEMPLATE          */
  //public static final int WG_ATTR_TEMPLATE = 14;

  {
    WG_ATTR_TEMPLATE, "Mod\u00e8le d''attribut, {0}"},

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;

  {
    WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "Conflit de correspondance entre xsl:strip-space et xsl:preserve-space"},

  /** WG_ATTRIB_NOT_HANDLED          */
  //public static final int WG_ATTRIB_NOT_HANDLED = 16;

  {
    WG_ATTRIB_NOT_HANDLED,
      "Xalan ne g\u00e8re pas encore l''attribut {0} !"},

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  {
    WG_NO_DECIMALFORMAT_DECLARATION,
      "Aucune d\u00e9claration trouv\u00e9e pour le format d\u00e9cimal : {0}"},

  /** WG_OLD_XSLT_NS          */
  //public static final int WG_OLD_XSLT_NS = 18;

  {
    WG_OLD_XSLT_NS, "Espace de noms XSLT manquant ou incorrect. "},

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  {
    WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "Seule une d\u00e9claration xsl:decimal-format par d\u00e9faut est autoris\u00e9e."},

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  {
    WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "Les noms xsl:decimal-format doivent \u00eatre uniques. Le nom \"{0}\" appara\u00eet en double."},

  /** WG_ILLEGAL_ATTRIBUTE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  {
    WG_ILLEGAL_ATTRIBUTE,
      "{0} dispose d''un attribut non autoris\u00e9 : {1}"},

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  {
    WG_COULD_NOT_RESOLVE_PREFIX,
      "Impossible de r\u00e9soudre de pr\u00e9fixe d''espace de noms : {0}. Le n\u0153ud sera ignor\u00e9."},

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  {
    WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet requiert un attribut ''version'' !"},

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  {
    WG_ILLEGAL_ATTRIBUTE_NAME,
      "Nom d''attribut non autoris\u00e9 : {0}"},

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  {
    WG_ILLEGAL_ATTRIBUTE_VALUE,
      "La valeur utilis\u00e9e pour l''attribut {0} n''est pas autoris\u00e9e : {1}"},

  /** WG_EMPTY_SECOND_ARG          */
  //public static final int WG_EMPTY_SECOND_ARG = 26;

  {
    WG_EMPTY_SECOND_ARG,
      "L''ensemble de n\u0153uds r\u00e9sultant d''un deuxi\u00e8me argument de la fonction document est vide. Le premier argument sera utilis\u00e9."},

  // Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML


  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;
  {
     WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "La valeur de l''attribut ''name'' de xsl:processing-instruction ne doit pas \u00eatre ''xml''"},

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME

  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;
  {
     WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "La valeur de l''''attribut ''''name'''' de xsl:processing-instruction doit \u00eatre un NCName valide :a {0}"},

    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    // WG_ILLEGAL_ATTRIBUTE_POSITION

  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
  //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;
  {
    WG_ILLEGAL_ATTRIBUTE_POSITION,
      "Impossible d''''ajouter l''''attribut {0}apr\u00e8s les noeuds enfants ou avant qu''un \u00e9l\u00e9ment ne soit produit.  L''attribut est ignor\u00e9."},

  // WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?

  // Other miscellaneous text used inside the code...
    { "ui_language", "fr"},
    { "help_language", "fr"},
    { "language", "fr"},
    { "BAD_CODE",
      "Param\u00e8tre pour createMessage hors limites"},
    { "FORMAT_FAILED",
      "Exception \u00e9mise pendant l''appel de messageFormat "},
    {"version",
      ">>>>>>> Version Xalan "},
    { "version2",  "<<<<<<<"},
    {"yes",  "oui"},
    { "line", "N\u00b0 de ligne"},
    {"column", "N\u00b0 de colonne"},
    {"xsldone", "XSLProcessor: termin\u00e9"},
    {"xslProc_option", "Options de classe de traitement de la ligne de commande Xalan-J :"},
  { "xslProc_invalid_xsltc_option", "L''option {0} n''est pas prise en charge en mode XSLTC."},
  { "xslProc_invalid_xalan_option", "L''option {0} peut \u00eatre utilis\u00e9e uniquement avec -XSLTC."},
  { "xslProc_no_input", "Erreur\u00a0: aucune sp\u00e9cification de feuille de style ou de xml d''entr\u00e9e. Ex\u00e9cutez cette commande sans option relative aux instructions d''utilisation."},
  { "xslProc_common_options", "-Options communes-"},
  { "xslProc_xalan_options", "-Options pour Xalan-"},
  { "xslProc_xsltc_options", "-Options pour XSLTC-"},
  { "xslProc_return_to_continue", "(Appuyez sur <retour> pour continuer)"},

  { "optionXSLTC", "   [-XSLTC (Utilisation de XSLTC pour la transformation)]"},
    {"optionIN",  "    -IN inputXMLURL"},
    {  "optionXSL",  "   [-XSL XSLTransformationURL]"},
    { "optionOUT",  "   [-OUT outputFileName]"},
    { "optionLXCIN", "   [-LXCIN compiledStylesheetFileNameIn]"},
    { "optionLXCOUT",
      "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
    { "optionPARSER",
      "   [-PARSER nom de classe qualifi\u00e9 pour la liaison de l''analyseur]"},
    { "optionE",
      "   [-E (Ne pas d\u00e9velopper les r\u00e9f\u00e9rences d''entit\u00e9s)]"},
    { "optionV",  "   [-E (Ne pas d\u00e9velopper les r\u00e9f\u00e9rences d''entit\u00e9s)]"},
    { "optionQC",
      "   [-QC (Avertissements pour les conflits silencieux de formes)]"},
    {"optionQ",
      "   [-Q  (Mode silencieux)]"},
    { "optionLF",
      "   [-LF (Utilisation des sauts de ligne uniquement en sortie {CR/LF par d\u00e9faut})]"},
    { "optionCR",
      "   [-CR (Utilisation des retours chariot uniquement en sortie {CR/LF par d\u00e9faut})]"},
    {  "optionESCAPE",
      "   [-ESCAPE (Caract\u00e8res \u00e0 remplacer {<>&\"\''\\r\\n par d\u00e9faut}]"},
    {  "optionINDENT",
      "   [-INDENT (Contr\u00f4le le nombre d''espaces pour le retrait {0 par d\u00e9faut})]"},
    {  "optionTT",
      "   [-TT (Trace des mod\u00e8les lors de leur appel.)]"},
    { "optionTG",
      "   [-TG (Trace de chaque cr\u00e9ation d''\u00e9v\u00e9nement.)]"},
    { "optionTS",
      "   [-TS (Trace de chaque s\u00e9lection d''\u00e9v\u00e9nement.)]"},
    { "optionTTC",
      "   [-TTC (Trace de chaque mod\u00e8le enfant lorsqu''ils sont trait\u00e9s.)]"},
    { "optionTCLASS",
      "   [-TCLASS (Classe TraceListener pour les extensions de trace.)]"},
    { "optionVALIDATE",
      "   [-VALIDATE (D\u00e9termine si la validation intervient. La validation est d\u00e9sactiv\u00e9e par d\u00e9faut.)]"},
    { "optionEDUMP",
      "   [-EDUMP {optional filename} (Permet d''acc\u00e9der \u00e0 l''emplacement de l''erreur.)]"},
    { "optionXML",
      "   [-XML (Utilisation d''un formateur XML et ajout d''en-t\u00eate XML.)]"},
    {  "optionTEXT",
      "   [-TEXT (Utilisation d''un formateur de texte simple.)]"},
    { "optionHTML", "   [-HTML (Utilisation d''un formateur HTML.)]"},
    { "optionPARAM",
      "   [-PARAM name expression (D\u00e9finition d''un param\u00e8tre de feuille de style)]"},
    {  "noParsermsg1",
      "Echec de XSL Process."},
    {  "noParsermsg2",
      "** Impossible de trouver l''analyseur **"},
    { "noParsermsg3",
      "Veuillez v\u00e9rifier votre chemin de classe."},
    {  "noParsermsg4",
      " Si vous ne disposez pas de l''analyseur XML d''IBM pour Java, vous pouvez le t\u00e9l\u00e9charger \u00e0 l''adresse suivante "},
    {  "noParsermsg5",
      "IBM''s AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
    { "optionURIRESOLVER",
      "   [-URIRESOLVER nom de classe complet (URIResolver \u00e0 utiliser pour r\u00e9soudre les URI)]"},
    { "optionENTITYRESOLVER",
      "   [-ENTITYRESOLVER nom de classe complet (EntityResolver \u00e0 utiliser pour r\u00e9soudre les entit\u00e9s)]"},
    { "optionCONTENTHANDLER",
      "   [-CONTENTHANDLER nom de classe complet (ContentHandler \u00e0 utiliser pour mettre en s\u00e9rie les sorties)]"},
    { "optionLINENUMBERS",
      "   [-L Utilisation des nombres de lignes pour le document source]"},

//XALAN_MANTIS CHANGES: Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


    { "optionMEDIA",
      " [-MEDIA mediaType (utilisation de l''attribut media pour rechercher la feuille de style associ\u00e9e \u00e0 un document.)]"},
    { "optionFLAVOR",
     " [-FLAVOR flavorName (utilisation explicite de s2s=SAX ou d2d=DOM pour proc\u00e9der aux transformations.)] "}, // Added by sboag/scurcuru; experimental
    { "optionDIAG",
      " [-DIAG (Impression du nombre global de millisecondes de la transformation.)]"},
    {  "optionINCREMENTAL",
     " [-INCREMENTAL (demande de construction DTM incr\u00e9mentielle en attribuant la valeur true \u00e0 http://xml.apache.org/xalan/features/incremental.)]"},
    { "optionNOOPTIMIMIZE",
     " [-NOOPTIMIMIZE (demande d''aucune optimisation de la feuille de style en attribuant la valeur false \u00e0 http://xml.apache.org/xalan/features/optimize.)]"},
    { "optionRL",
     " [-RL recursionlimit (assertion d''une limite num\u00e9rique sur la profondeur de r\u00e9cursion de la feuille de style.)]"},
    { "optionXO",
     " [-XO [transletName] (affectation du nom au translet g\u00e9n\u00e9r\u00e9)]"},
    { "optionXD",
     " [-XD destinationDirectory (sp\u00e9cification d''un r\u00e9pertoire de destination pour le translet)]"},
    { "optionXJ",
     " [-XJ jarfile (regroupe les classes de translet dans un fichier jar nomm\u00e9 <jarfile>)]"},
    { "optionXP",
     " [-XP package (sp\u00e9cifie un pr\u00e9fixe de nom de groupe pour toutes les classes de translet g\u00e9n\u00e9r\u00e9es)]"},
  { "optionXN",  "   [-XN (active l''incorporation du mod\u00e8le)]" },
  { "optionXX",  "   [-XX (active une sortie suppl\u00e9mentaire de message de d\u00e9bogage)]"},
  { "optionXT" , "   [-XT (utiliser, si possible, un translet pour la transformation)]"},
  { "diagTiming"," --------- La transformation de {0} via {1} a dur\u00e9 {2} ms" },
  { "recursionTooDeep","Imbrication trop profonde du mod\u00e8le. Imbrication = {0}, mod\u00e8le {1} {2}" },
  { "nameIs", "nom" },
  { "matchPatternIs", "structure de comparaison" }

  };

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Erreur : ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Attention : ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "FORME";

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



