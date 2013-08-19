/*
* @(#)XPATHErrorResources_it.java	1.4 03/05/01
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
package org.apache.xpath.res;

import org.apache.xml.utils.res.XResourceBundleBase;


import java.util.*;

import java.text.DecimalFormat;


/**
 * <meta name="usage" content="advanced"/>
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a Static string constant for the
 * Key and update the contents array with Key, Value pair
  * Also you need to  update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING) [ Information purpose only]
 */
public class XPATHErrorResources_it extends XPATHErrorResources
{


 /** Field MAX_CODE          */
 public static final int MAX_CODE = 116;  // this is needed to keep track of the number of messages          

 /** Field MAX_WARNING          */
 public static final int MAX_WARNING = 11;  // this is needed to keep track of the number of warnings

 /** Field MAX_OTHERS          */
 public static final int MAX_OTHERS = 20;

 /** Field MAX_MESSAGES          */
 public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


 // Error messages...
  public static final Object[][] contents = {

 /** Field ERROR0000          */
 //public static final int ERROR0000 = 0;


 {
   "ERROR0000", "{0}"},


 /** Field ER_CURRENT_NOT_ALLOWED_IN_MATCH          */
 //public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;


 {
   ER_CURRENT_NOT_ALLOWED_IN_MATCH,
     "La funzione current() non \u00e8 consentita in un criterio di corrispondenza."},


 /** Field ER_CURRENT_TAKES_NO_ARGS          */
 //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


 {
   ER_CURRENT_TAKES_NO_ARGS,
     "La funzione current() non accetta argomenti."},


 /** Field ER_DOCUMENT_REPLACED          */
 //public static final int ER_DOCUMENT_REPLACED = 3;


 {
   ER_DOCUMENT_REPLACED,
     "L'implementazione della funzione document() \u00e8 stata sostituita da org.apache.xalan.xslt.FuncDocument."},


 /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
 //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


 {
   ER_CONTEXT_HAS_NO_OWNERDOC,
     "Il contesto non ha un documento proprietario."},


 /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
 //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


 {
   ER_LOCALNAME_HAS_TOO_MANY_ARGS,
     "local-name() ha troppi argomenti."},


 /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
 //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


 {
   ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
     "namespace-uri() ha troppi argomenti."},


 /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
 //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


 {
   ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
     "normalize-space() ha troppi argomenti."},


 /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
 //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


 {
   ER_NUMBER_HAS_TOO_MANY_ARGS,
     "number() ha troppi argomenti."},


 /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
 //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


 {
   ER_NAME_HAS_TOO_MANY_ARGS, "name() ha troppi argomenti."},


 /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
 //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


 {
   ER_STRING_HAS_TOO_MANY_ARGS,
     "string() ha troppi argomenti."},


 /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
 //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


 {
   ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
     "string-length() ha troppi argomenti."},


 /** Field ER_TRANSLATE_TAKES_3_ARGS          */
 //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


 {
   ER_TRANSLATE_TAKES_3_ARGS,
     "La funzione translate() richiede tre argomenti."},


 /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
 //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


 {
   ER_UNPARSEDENTITYURI_TAKES_1_ARG,
     "La funzione unparsed-entity-uri richiede un argomento."},


 /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
 //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


 {
   ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
     "Asse namespace non ancora implementato."},


 /** Field ER_UNKNOWN_AXIS          */
 //public static final int ER_UNKNOWN_AXIS = 15;


 {
   ER_UNKNOWN_AXIS, "Asse sconosciuto: {0}"},


 /** Field ER_UNKNOWN_MATCH_OPERATION          */
 //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


 {
   ER_UNKNOWN_MATCH_OPERATION, "Operazione di corrispondenza sconosciuta."},


 /** Field ER_INCORRECT_ARG_LENGTH          */
 //public static final int ER_INCORRECT_ARG_LENGTH = 17;


 {
   ER_INCORRECT_ARG_LENGTH,
     "La lunghezza argomento del test di nodo di processing-instruction() non \u00e8 corretta."},


 /** Field ER_CANT_CONVERT_TO_NUMBER          */
 //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


 {
   ER_CANT_CONVERT_TO_NUMBER,
     "Impossibile convertire {0} in un numero."},


 /** Field ER_CANT_CONVERT_TO_NODELIST          */
 //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


 {
   ER_CANT_CONVERT_TO_NODELIST,
     "Impossibile convertire {0} in NodeList."},


 /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
 //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


 {
   ER_CANT_CONVERT_TO_MUTABLENODELIST,
     "Impossibile convertire {0} in NodeSetDTM."},


 /** Field ER_CANT_CONVERT_TO_TYPE          */
 //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


 {
   ER_CANT_CONVERT_TO_TYPE,
     "Impossibile convertire {0} in tipo//{1}"},


 /** Field ER_EXPECTED_MATCH_PATTERN          */
 //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


 {
   ER_EXPECTED_MATCH_PATTERN,
     "Previsto criterio di corrispondenza in getMatchScore."},


 /** Field ER_COULDNOT_GET_VAR_NAMED          */
 //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


 {
   ER_COULDNOT_GET_VAR_NAMED,
     "Impossibile trovare la variabile denominata {0}"},


 /** Field ER_UNKNOWN_OPCODE          */
 //public static final int ER_UNKNOWN_OPCODE = 24;


 {
   ER_UNKNOWN_OPCODE, "ERRORE. Codice operativo sconosciuto: {0}"},


 /** Field ER_EXTRA_ILLEGAL_TOKENS          */
 //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


 {
   ER_EXTRA_ILLEGAL_TOKENS, "Altri token non validi: {0}"},


 /** Field ER_EXPECTED_DOUBLE_QUOTE          */
 //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


 {
   ER_EXPECTED_DOUBLE_QUOTE,
     "Letterale non corretto... previste le doppie virgolette."},


 /** Field ER_EXPECTED_SINGLE_QUOTE          */
 //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


 {
   ER_EXPECTED_SINGLE_QUOTE,
     "Letterale non corretto... previste le virgolette singole."},


 /** Field ER_EMPTY_EXPRESSION          */
 //public static final int ER_EMPTY_EXPRESSION = 28;


 {
   ER_EMPTY_EXPRESSION, "Espressione vuota."},


 /** Field ER_EXPECTED_BUT_FOUND          */
 //public static final int ER_EXPECTED_BUT_FOUND = 29;


 {
   ER_EXPECTED_BUT_FOUND, "Previsto {0}, trovato: {1}"},


 /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
 //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


 {
   ER_INCORRECT_PROGRAMMER_ASSERTION,
     "L''asserzione di programmazione non \u00e8 corretta. - {0}"},


 /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
 //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


 {
   ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
     "L'argomento booleano(...) non \u00e8 pi\u00f9 opzionale con 19990709 XPath draft."},


 /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
 //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


 {
   ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
     "Trovato ',' ma senza argomento precedente."},


 /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
 //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


 {
   ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
     "Trovato ',' ma senza argomento successivo."},


 /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
 //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


 {
   ER_PREDICATE_ILLEGAL_SYNTAX,
     "'.. la sintassi [predicato]' o '.[predicato]' non \u00e8 valida. Utilizzare 'self::node()[predicato]'."},


 /** Field ER_ILLEGAL_AXIS_NAME          */
 //public static final int ER_ILLEGAL_AXIS_NAME = 35;


 {
   ER_ILLEGAL_AXIS_NAME, "Nome di asse non valido: {0}"},


 /** Field ER_UNKNOWN_NODETYPE          */
 //public static final int ER_UNKNOWN_NODETYPE = 36;


 {
   ER_UNKNOWN_NODETYPE, "Tipo di nodo sconosciuto: {0}"},


 /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
 //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


 {
   ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
     "Il criterio letterale ({0}) deve essere tra virgolette."},


 /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
 //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


 {
   ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
     "Impossibile formattare {0} in un numero."},


 /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
 //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


 {
   ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
     "Impossibile creare il collegamento XML TransformerFactory: {0}"},


 /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
 //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


 {
   ER_DIDNOT_FIND_XPATH_SELECT_EXP,
     "Errore. Impossibile trovare l'espressione di selezione xpath (-select)."},


 /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
 //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


 {
   ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
     "ERRORE. Impossibile trovare ENDOP dopo OP_LOCATIONPATH"},


 /** Field ER_ERROR_OCCURED          */
 //public static final int ER_ERROR_OCCURED = 42;


 {
   ER_ERROR_OCCURED, "Errore"},


 /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
 //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


 {
   ER_ILLEGAL_VARIABLE_REFERENCE,
     "VariableReference fornito per la variabile \u00e8 esterno al contesto o senza definizione.  Nome = {0}"},


 /** Field ER_AXES_NOT_ALLOWED          */
 //public static final int ER_AXES_NOT_ALLOWED = 44;


 {
   ER_AXES_NOT_ALLOWED,
     "Nei criteri di corrispondenza sono consentiti solo gli assi child:: e attribute::. Assi non validi = {0}"},


 /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
 //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


 {
   ER_KEY_HAS_TOO_MANY_ARGS,
     "key() ha un numero di argomenti non valido."},


 /** Field ER_COUNT_TAKES_1_ARG          */
 //public static final int ER_COUNT_TAKES_1_ARG = 46;


 {
   ER_COUNT_TAKES_1_ARG,
     "La funzione di conteggio deve prendere un argomento."},


 /** Field ER_COULDNOT_FIND_FUNCTION          */
 //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


 {
   ER_COULDNOT_FIND_FUNCTION, "Impossibile trovare la funzione: {0}"},


 /** Field ER_UNSUPPORTED_ENCODING          */
 //public static final int ER_UNSUPPORTED_ENCODING = 48;


 {
   ER_UNSUPPORTED_ENCODING, "Codifica non supportata: {0}"},


 /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
 //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


 {
   ER_PROBLEM_IN_DTM_NEXTSIBLING,
     "Problema in DTM in getNextSibling... Tentativo di recupero in corso."},


 /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
 //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


 {
   ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
     "Errore di programmazione: impossibile scrivere in EmptyNodeList."},


 /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
 //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


 {
   ER_SETDOMFACTORY_NOT_SUPPORTED,
     "setDOMFactory non \u00e8 supportato da XPathContext."},


 /** Field ER_PREFIX_MUST_RESOLVE          */
 //public static final int ER_PREFIX_MUST_RESOLVE = 52;


 {
   ER_PREFIX_MUST_RESOLVE,
     "Il prefisso deve risolvere in namespace: {0}"},


 /** Field ER_PARSE_NOT_SUPPORTED          */
 //public static final int ER_PARSE_NOT_SUPPORTED = 53;


 {
   ER_PARSE_NOT_SUPPORTED,
     "analisi (sorgente InputSource) non supportata in XPathContext. Impossibile aprire {0}"},


 /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
 //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


 {
   ER_CREATEDOCUMENT_NOT_SUPPORTED,
     "createDocument() non supportato in XPathContext."},


 /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
 //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


 {
   ER_CHILD_HAS_NO_OWNER_DOCUMENT,
     "L'elemento secondario dell'attributo non ha un documento di propriet\u00e0."},


 /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
 //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


 {
   ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
     "L'elemento secondario dell'attributo non ha un elemento del documento di propriet\u00e0."},


 /** Field ER_SAX_API_NOT_HANDLED          */
 //public static final int ER_SAX_API_NOT_HANDLED = 57;


 {
   ER_SAX_API_NOT_HANDLED,
     "Caratteri SAX API (char ch[]... non gestiti da DTM."},


 /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
 //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


 {
   ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
     "ignorableWhitespace(char ch[]... non \u00e8 gestito da DTM."},


 /** Field ER_DTM_CANNOT_HANDLE_NODES          */
 //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


 {
   ER_DTM_CANNOT_HANDLE_NODES,
     "DTMLiaison non pu\u00f2 gestire nodi di tipo {0}"},


 /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
 //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


 {
   ER_XERCES_CANNOT_HANDLE_NODES,
     "DOM2Helper non pu\u00f2 gestire nodi di tipo {0}"},


 /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
 //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


 {
   ER_XERCES_PARSE_ERROR_DETAILS,
     "Errore DOM2Helper.parse: SystemID - {0} linea - {1}"},


 /** Field ER_XERCES_PARSE_ERROR          */
 //public static final int ER_XERCES_PARSE_ERROR = 62;


 {
   ER_XERCES_PARSE_ERROR, "Errore DOM2Helper.parse"},


 /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
 //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


 {
   ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
     "Avvertenza: impossibile generare un output di testo prima dell'elemento documento. Richiesta ignorata..."},


 /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
 //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


 {
   ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
     "Impossibile avere pi\u00f9 di una root su un DOM."},


 /** Field ER_INVALID_UTF16_SURROGATE          */
 //public static final int ER_INVALID_UTF16_SURROGATE = 65;


 {
   ER_INVALID_UTF16_SURROGATE,
     "Rilevato surrogato di UTF-16 non valido: {0} ?"},


 /** Field ER_OIERROR          */
 //public static final int ER_OIERROR = 66;


 {
   ER_OIERROR, "Errore IO"},


 /** Field ER_CANNOT_CREATE_URL          */
 //public static final int ER_CANNOT_CREATE_URL = 67;


 {
   ER_CANNOT_CREATE_URL, "Impossibile creare URL per: {0}"},


 /** Field ER_XPATH_READOBJECT          */
 //public static final int ER_XPATH_READOBJECT = 68;


 {
   ER_XPATH_READOBJECT, "In XPath.readObject: {0}"},

 
 /** Field ER_XPATH_READOBJECT         */
 //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


 {
   ER_FUNCTION_TOKEN_NOT_FOUND,
     "token di funzione non trovato."},

 
  /**  Argument 'localName' is null  */
 //public static final int ER_ARG_LOCALNAME_NULL = 70;


 {
   ER_ARG_LOCALNAME_NULL,
      "L'argomento 'localName' \u00e8 nullo"},

 
  /**  Can not deal with XPath type:   */
 //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


 {
   ER_CANNOT_DEAL_XPATH_TYPE,
      "Impossibile gestire il tipo XPath: {0}"},

 
  /**  This NodeSet is not mutable  */
 //public static final int ER_NODESET_NOT_MUTABLE = 72;


 {
   ER_NODESET_NOT_MUTABLE,
      "Questo NodeSet non \u00e8 mutabile"},

 
  /**  This NodeSetDTM is not mutable  */
 //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


 {
   ER_NODESETDTM_NOT_MUTABLE,
      "Questo NodeSetDTM non \u00e8 mutabile"},

 
  /**  Variable not resolvable:   */
 //public static final int ER_VAR_NOT_RESOLVABLE = 74;


 {
   ER_VAR_NOT_RESOLVABLE,
       "Variabile non risolvibile: {0}"},

 
  /** Null error handler  */
 //public static final int ER_NULL_ERROR_HANDLER = 75;


 {
   ER_NULL_ERROR_HANDLER,
       "Errore gestore nullo"},

 
  /**  Programmer's assertion: unknown opcode  */
 //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


 {
   ER_PROG_ASSERT_UNKNOWN_OPCODE,
      "Asserzione di programmazione: codice operativo sconosciuto: {0}"},

 
  /**  0 or 1   */
 //public static final int ER_ZERO_OR_ONE = 77;


 {
   ER_ZERO_OR_ONE,
      "0 o 1"},

 
 
  /**  rtf() not supported by XRTreeFragSelectWrapper   */
 //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


 {
   ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
      "rtf() non supportato da XRTreeFragSelectWrapper"},

 
  /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
 //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


 {
   ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
      "asNodeIterator() non supportato da XRTreeFragSelectWrapper"},

 
  /**  fsb() not supported for XStringForChars   */
 //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


 {
   ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
      "fsb() non supportato per XStringForChars"},

 
  /**  Could not find variable with the name of   */
 //public static final int ER_COULD_NOT_FIND_VAR = 81;


 {
   ER_COULD_NOT_FIND_VAR,
     "Impossibile trovare la variabile denominata {0}"},

 
  /**  XStringForChars can not take a string for an argument   */
 //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


 {
   ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
     "XStringForChars non pu\u00f2 prendere una stringa per un argomento"},

 
  /**  The FastStringBuffer argument can not be null   */
 //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


 {
   ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
     "L'argomento FastStringBuffer non pu\u00f2 essere nullo"},
   
    /**  2 or 3   */
   //public static final int ER_TWO_OR_THREE = 84;
 
 
   {
     ER_TWO_OR_THREE,
        "2 o 3"},
  
 
    /** Variable accessed before it is bound! */
   //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;
 
 
   {
     ER_VARIABLE_ACCESSED_BEFORE_BIND,
        "Accesso alla variabile prima della delimitazione."},
  
 
    /** XStringForFSB can not take a string for an argument! */
   //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;
 
 
   {
     ER_FSB_CANNOT_TAKE_STRING,
        "XStringForFSB non pu\u00f2 avere una stringa per argomento."},
  
 
    /** Error! Setting the root of a walker to null! */
   //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;
 
 
   {
     ER_SETTING_WALKER_ROOT_TO_NULL,
        "\n !!!! Errore! Impostazione radice walker a zero."},
  
 
    /** This NodeSetDTM can not iterate to a previous node! */
   //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;
 
 
   {
     ER_NODESETDTM_CANNOT_ITERATE,
        "Questo NodeSetDTM non \u00e8 in grado di eseguire iterazione in un nodo precedente."},
  
 
   /** This NodeSet can not iterate to a previous node! */
   //public static final int ER_NODESET_CANNOT_ITERATE = 89;
 
 
   {
     ER_NODESET_CANNOT_ITERATE,
        "Questo NodeSet non \u00e8 in grado di eseguire iterazione in un nodo precedente."},
  
 
   /** This NodeSetDTM can not do indexing or counting functions! */
   //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;
 
 
   {
     ER_NODESETDTM_CANNOT_INDEX,
        "Questo NodeSetDTM non \u00e8 in grado di indicizzare o calcolare le funzioni."},
  
 
   /** This NodeSet can not do indexing or counting functions! */
   //public static final int ER_NODESET_CANNOT_INDEX = 91;
 
 
   {
     ER_NODESET_CANNOT_INDEX,
        "Questo NodeSet non \u00e8 in grado di indicizzare o calcolare le funzioni."},
  
 
   /** Can not call setShouldCacheNodes after nextNode has been called! */
   //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;
 
 
   {
     ER_CANNOT_CALL_SETSHOULDCACHENODE,
        "Impossibile richiamare setShouldCacheNodes dopo aver richiamato nextNode."},
  
 
   /** {0} only allows {1} arguments */
   //public static final int ER_ONLY_ALLOWS = 93;
 
 
   {
     ER_ONLY_ALLOWS,
        "{0} consente solo argomenti {1}"},
  
 
   /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
   //public static final int ER_UNKNOWN_STEP = 94;
 
 
   {
     ER_UNKNOWN_STEP,
        "Conferma del programmatore in getNextStepPos: stepType sconosciuto: {0}"},
  
 
   //Note to translators:  A relative location path is a form of XPath expression.
   // The message indicates that such an expression was expected following the
   // characters '/' or '//', but was not found.
 
   /** Problem with RelativeLocationPath */
   //public static final int ER_EXPECTED_REL_LOC_PATH = 95;
 
 
   {
     ER_EXPECTED_REL_LOC_PATH,
        "Atteso percorso ubicazione relativo dopo il token '/' o '//'."},
  
 
   // Note to translators:  A location path is a form of XPath expression.
   // The message indicates that syntactically such an expression was expected,but
   // the characters specified by the substitution text were encountered instead.
 
   /** Problem with LocationPath */
   //public static final int ER_EXPECTED_LOC_PATH = 96;
 
 
   {
     ER_EXPECTED_LOC_PATH,
        "Atteso percorso ubicazione, ma \u00e8 stato incontrato il token seguente:  {0}"},
  
 
   // Note to translators:  A location step is part of an XPath expression.
   // The message indicates that syntactically such an expression was expected
   // following the specified characters.
 
   /** Problem with Step */
   //public static final int ER_EXPECTED_LOC_STEP = 97;
 
 
   {
     ER_EXPECTED_LOC_STEP,
        "Atteso step ubicazione dopo il token '/' o '//'."},
  
 
   // Note to translators:  A node test is part of an XPath expression that is
   // used to test for particular kinds of nodes.  In this case, a node test that
   // consists of an NCName followed by a colon and an asterisk or that consists
   // of a QName was expected, but was not found.
 
   /** Problem with NodeTest */
   //public static final int ER_EXPECTED_NODE_TEST = 98;
 
 
   {
     ER_EXPECTED_NODE_TEST,
        "Atteso test nodo corrispondente a NCName:* o QName."},
  
 
   // Note to translators:  A step pattern is part of an XPath expression.
   // The message indicates that syntactically such an expression was expected,
   // but the specified character was found in the expression instead.
 
   /** Expected step pattern */
   //public static final int ER_EXPECTED_STEP_PATTERN = 99;
 
 
   {
     ER_EXPECTED_STEP_PATTERN,
        "Atteso pattern step, ma \u00e8 stato incontrato '/'."},
  
 
   // Note to translators: A relative path pattern is part of an XPath expression.
   // The message indicates that syntactically such an expression was expected,
   // but was not found.
  
   /** Expected relative path pattern */
   //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;
 
 
   {
     ER_EXPECTED_REL_PATH_PATTERN,
        "Atteso pattern percorso relativo."},
  

   // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
   // The localname is the portion after the optional colon; the message indicates
   // that there is a problem with that part of the QNAME.
 
   /** localname in QNAME should be a valid NCName */
   //public static final int ER_ARG_LOCALNAME_INVALID = 101;
 
 
   {
     ER_ARG_LOCALNAME_INVALID,
        "Localname in QNAME deve essere un NCName valido"},
  
   
   // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
   // The prefix is the portion before the optional colon; the message indicates
   // that there is a problem with that part of the QNAME.
 
   /** prefix in QNAME should be a valid NCName */
   //public static final int ER_ARG_PREFIX_INVALID = 102;
 
 
   {
     ER_ARG_PREFIX_INVALID,
        "Prefisso in QNAME deve essere un NCName valido"},
  
 
   // Note to translators:  The substitution text is the name of a data type.  The
   // message indicates that a value of a particular type could not be converted
   // to a value of type string.
 
   /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
   //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;
 
 
   {
     ER_CANT_CONVERT_TO_BOOLEAN,
        "Impossibile convertire {0} in booleano."},
  
 
   // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
   // FIRST_ORDERED_NODE_TYPE.
 
   /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
   //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;
 
 
   {
     ER_CANT_CONVERT_TO_SINGLENODE,
        "Impossibile convertire {0} in nodo singolo. Questo getter si applica ai tipi ANY_UNORDERED_NODE_TYPE e FIRST_ORDERED_NODE_TYPE."},
  
 
   // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
   // ORDERED_NODE_SNAPSHOT_TYPE.
 
   /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
   //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;
 
 
   {
     ER_CANT_GET_SNAPSHOT_LENGTH,
        "Impossibile recuperare lunghezza snapshot in tipo: {0}. Questo getter si applica ai tipi UNORDERED_NODE_SNAPSHOT_TYPE e ORDERED_NODE_SNAPSHOT_TYPE."},
  
 
   /** Field ER_NON_ITERATOR_TYPE                */
   //public static final int ER_NON_ITERATOR_TYPE        = 106;
 
 
   {
     ER_NON_ITERATOR_TYPE,
        "Impossibile eseguire iterazione su tipo non iterativo: {0}"},
  
 
   // Note to translators: This message indicates that the document being operated
   // upon changed, so the iterator object that was being used to traverse the
   // document has now become invalid.
 
   /** Field ER_DOC_MUTATED                      */
   //public static final int ER_DOC_MUTATED              = 107;
 
 
   {
     ER_DOC_MUTATED,
        "Documento modificato dalla restituzione del risultato. Iteratore non valido."},
  
 
   /** Field ER_INVALID_XPATH_TYPE               */
   //public static final int ER_INVALID_XPATH_TYPE       = 108;
 
 
   {
     ER_INVALID_XPATH_TYPE,
        "Argomento tipo XPath non valido: {0}"},
  
 
   /** Field ER_EMPTY_XPATH_RESULT                */
   //public static final int ER_EMPTY_XPATH_RESULT       = 109;
 
 
   {
     ER_EMPTY_XPATH_RESULT,
        "Oggetto risultato XPath vuoto"},
  
 
   /** Field ER_INCOMPATIBLE_TYPES                */
   //public static final int ER_INCOMPATIBLE_TYPES       = 110;
 
 
   {
     ER_INCOMPATIBLE_TYPES,
        "Il tipo restituito: {0} non pu\u00f2 essere forzato nel tipo specificato: {1}"},
  
 
   /** Field ER_NULL_RESOLVER                     */
   //public static final int ER_NULL_RESOLVER            = 111;
 
 
   {
     ER_NULL_RESOLVER,
        "Impossibile risolvere il prefisso con risolutore prefisso nullo."},
  
 
   // Note to translators:  The substitution text is the name of a data type.  The
   // message indicates that a value of a particular type could not be converted
   // to a value of type string.
 
   /** Field ER_CANT_CONVERT_TO_STRING            */
   //public static final int ER_CANT_CONVERT_TO_STRING   = 112;
 
 
   {
     ER_CANT_CONVERT_TO_STRING,
        "Impossibile convertire {0} in stringa."},
  
 
   // Note to translators: Do not translate snapshotItem,
   // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.
 
   /** Field ER_NON_SNAPSHOT_TYPE                 */
   //public static final int ER_NON_SNAPSHOT_TYPE       = 113;
 
 
   {
     ER_NON_SNAPSHOT_TYPE,
        "Impossibile richiamare snapshotItem su tipo: {0}. Questo metodo \u00e8 valido per i tipi UNORDERED_NODE_SNAPSHOT_TYPE e ORDERED_NODE_SNAPSHOT_TYPE."},
  
 
   // Note to translators:  XPathEvaluator is a Java interface name.  An
   // XPathEvaluator is created with respect to a particular XML document, and in
   // this case the expression represented by this object was being evaluated with
   // respect to a context node from a different document.
 
   /** Field ER_WRONG_DOCUMENT                    */
   //public static final int ER_WRONG_DOCUMENT          = 114;
 
 
   {
     ER_WRONG_DOCUMENT,
        "Il nodo contesto non appartiene al documento collegato a questo XPathEvaluator."},
  
 
   // Note to translators:  The XPath expression cannot be evaluated with respect
   // to this type of node.
   /** Field ER_WRONG_NODETYPE                    */
   //public static final int ER_WRONG_NODETYPE          = 115;
 
 
   {
     ER_WRONG_NODETYPE ,
        "Il tipo di nodo contesto non \u00e8 supportato."},
  
 
   /** Field ER_XPATH_ERROR                       */
   //public static final int ER_XPATH_ERROR             = 116;
 
 
   {
     ER_XPATH_ERROR ,
        "Errore sconosciuto in XPath."},
  
 
 
   // Warnings...

 /** Field WG_LOCALE_NAME_NOT_HANDLED          */
 //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


 {
   WG_LOCALE_NAME_NOT_HANDLED,
     "Il nome locale nella funzione format-number non \u00e8 ancora gestito."},


 /** Field WG_PROPERTY_NOT_SUPPORTED          */
 //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


 {
   WG_PROPERTY_NOT_SUPPORTED,
     "Propriet\u00e0 XSL non supportata: {0}"},


 /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
 //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


 {
   WG_DONT_DO_ANYTHING_WITH_NS,
     "Nulla da fare correntemente con namespace {0} in propriet\u00e0: {1}"},


 /** Field WG_SECURITY_EXCEPTION          */
 //public static final int WG_SECURITY_EXCEPTION = 4;


 {
   WG_SECURITY_EXCEPTION,
     "Generata SecurityException al tentativo di accedere alle propriet\u00e0 di sistema XSL: {0}"},


 /** Field WG_QUO_NO_LONGER_DEFINED          */
 //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


 {
   WG_QUO_NO_LONGER_DEFINED,
     "Sintassi precedente: quo(...) non \u00e8 pi\u00f9 definita in XPath."},


 /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
 //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


 {
   WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
     "XPath deve avere un oggetto derivato per implementare nodeTest."},


 /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
 //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


 {
   WG_FUNCTION_TOKEN_NOT_FOUND,
     "Token di funzione non trovato."},


 /** Field WG_COULDNOT_FIND_FUNCTION          */
 //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


 {
   WG_COULDNOT_FIND_FUNCTION,
     "Impossibile trovare la funzione: {0}"},


 /** Field WG_CANNOT_MAKE_URL_FROM          */
 //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


 {
   WG_CANNOT_MAKE_URL_FROM,
     "Impossibile creare un URL da: {0}"},


 /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
 //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


 {
   WG_EXPAND_ENTITIES_NOT_SUPPORTED,
     "L'opzione -E non \u00e8 supportata per il parser DTM"},


 /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
 //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


 {
   WG_ILLEGAL_VARIABLE_REFERENCE,
     "VariableReference fornita per la variabile \u00e8 esterna al contesto o senza definizione.  Nome = {0}"},


 /** Field WG_UNSUPPORTED_ENCODING          */
 //public static final int WG_UNSUPPORTED_ENCODING = 12;


 {
   WG_UNSUPPORTED_ENCODING, "Codifica non supportata: {0}"},


 // Other miscellaneous text used inside the code...

 { "ui_language", "it"},
 { "help_language", "it"},
 { "language", "it"},
   { "BAD_CODE",
     "Il parametro di createMessage \u00e8 esterno ai limiti"},
   { "FORMAT_FAILED",
     "Eccezione generata durante la chiamata di messageFormat"},
   { "version", ">>>>>>> Xalan Versione "},
   { "version2", "<<<<<<<"},
   { "yes", "s\u00ec"},
   { "line", "Linea //"},
   { "column", "Colonna //"},
   { "xsldone", "XSLProcessor: done"},
   { "xpath_option", "xpath options: "},
   { "optionIN", "   [-in inputXMLURL]"},
   { "optionSelect", "   [-select espressione xpath]"},
   { "optionMatch",
     "   [-match match pattern (per la diagnostica di corrispondenza)]"},
   { "optionAnyExpr",
     "O solo un'espressione xpath per eseguire un dump di diagnostica"},
   { "noParsermsg1", "Processo XSL non riuscito."},
   { "noParsermsg2", "** Impossibile trovare il parser **"},
   { "noParsermsg3", "Verificare il classpath."},
   { "noParsermsg4",
     "Se non si dispone del parser XML IBM per Java, scaricarlo da"},
   { "noParsermsg5",
     "AlphaWorks IBM: http://www.alphaworks.ibm.com/formula/xml"},
    { "gtone", ">1" },
    { "zero", "0" },
    { "one", "1" },
    { "two" , "2" },
    { "three", "3" }

 };

 // ================= INFRASTRUCTURE ======================

 /** Field BAD_CODE          */
 public static final String BAD_CODE = "BAD_CODE";

 /** Field FORMAT_FAILED          */
 public static final String FORMAT_FAILED = "FORMAT_FAILED";

 /** Field ERROR_RESOURCES          */
 public static final String ERROR_RESOURCES =
   "org.apache.xpath.res.XPATHErrorResources";

 /** Field ERROR_STRING          */
 public static final String ERROR_STRING = "//errore";

 /** Field ERROR_HEADER          */
 public static final String ERROR_HEADER = "Errore: ";

 /** Field WARNING_HEADER          */
 public static final String WARNING_HEADER = "Avvertenza: ";

 /** Field XSL_HEADER          */
 public static final String XSL_HEADER = "XSL ";

 /** Field XML_HEADER          */
 public static final String XML_HEADER = "XML ";

 /** Field QUERY_HEADER          */
 public static final String QUERY_HEADER = "PATTERN ";

 /**
  * Get the association list.
  *
  * @return The association list.
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

