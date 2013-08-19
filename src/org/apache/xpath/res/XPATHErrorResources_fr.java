/*
 * @(#)XPATHErrorResources_fr.java	1.4 03/05/01
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
public class XPATHErrorResources_fr extends XPATHErrorResources
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
      "La fonction current() n'est pas admise dans la correspondance \u00e0 la forme !"},


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  {
    ER_CURRENT_TAKES_NO_ARGS,
      "La fonction current() n'admet pas les arguments !"},


  /** Field ER_DOCUMENT_REPLACED          */
  //public static final int ER_DOCUMENT_REPLACED = 3;


  {
    ER_DOCUMENT_REPLACED,
      "La mise en oeuvre de la fonction document () a \u00e9t\u00e9 remplac\u00e9e par org.apache.xalan.xslt.FuncDocument!"},


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  {
    ER_CONTEXT_HAS_NO_OWNERDOC,
      "le contexte n'a pas de document propri\u00e9taire !"},


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  {
    ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "local-name() a trop d'arguments."},


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  {
    ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "namespace-uri() a trop d'arguments."},


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  {
    ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "normalize-space() a trop d'arguments."},


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  {
    ER_NUMBER_HAS_TOO_MANY_ARGS,
      "number() a trop d'arguments."},


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  {
    ER_NAME_HAS_TOO_MANY_ARGS, "name() a trop d'arguments."},


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  {
    ER_STRING_HAS_TOO_MANY_ARGS,
      "string() a trop d'arguments."},


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  {
    ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "string-length() a trop d'arguments."},


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  {
    ER_TRANSLATE_TAKES_3_ARGS,
      "La fonction translate() a trois arguments !"},


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  {
    ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "La fonction unparsed-entity-uri ne peut avoir qu'un seul argument !"},


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  {
    ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "l'axe namespace n'est pas encore mis en \u0153uvre !"},


  /** Field ER_UNKNOWN_AXIS          */
  //public static final int ER_UNKNOWN_AXIS = 15;


  {
    ER_UNKNOWN_AXIS, "axe inconnu : {0}"},


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  {
    ER_UNKNOWN_MATCH_OPERATION, "op\u00e9ration de correspondance inconnue !"},


  /** Field ER_INCORRECT_ARG_LENGTH          */
  //public static final int ER_INCORRECT_ARG_LENGTH = 17;


  {
    ER_INCORRECT_ARG_LENGTH,
      "La longueur d'argument du test du n\u0153ud processing-instruction() n'est pas correcte !"},


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  {
    ER_CANT_CONVERT_TO_NUMBER,
      "Impossible de convertir {0} en fonction number"},


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  {
    ER_CANT_CONVERT_TO_NODELIST,
      "Impossible de convertir {0} en fonction NodeList !"},


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  {
    ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "Impossible de convertir {0} en fonction NodeSetDTM !"},


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  {
    ER_CANT_CONVERT_TO_TYPE,
      "Impossible de convertir {0} en type//{1}"},


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  {
    ER_EXPECTED_MATCH_PATTERN,
      "Correspondance \u00e0 la forme attendue dans getMatchScore !"},


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  {
    ER_COULDNOT_GET_VAR_NAMED,
      "Impossible de trouver la variable nomm\u00e9e {0}"},


  /** Field ER_UNKNOWN_OPCODE          */
  //public static final int ER_UNKNOWN_OPCODE = 24;


  {
    ER_UNKNOWN_OPCODE, "ERREUR ! Code d'op\u00e9ration inconnu : {0}"},


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  {
    ER_EXTRA_ILLEGAL_TOKENS, "Unit\u00e9s lexicales suppl\u00e9mentaires non autoris\u00e9es : {0}"},


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  {
    ER_EXPECTED_DOUBLE_QUOTE,
      "libell\u00e9 mal pr\u00e9sent\u00e9... guillemet attendu !"},


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  {
    ER_EXPECTED_SINGLE_QUOTE,
      "libell\u00e9 mal pr\u00e9sent\u00e9... apostrophe attendue !"},


  /** Field ER_EMPTY_EXPRESSION          */
  //public static final int ER_EMPTY_EXPRESSION = 28;


  {
    ER_EMPTY_EXPRESSION, "Expression vide !"},


  /** Field ER_EXPECTED_BUT_FOUND          */
  //public static final int ER_EXPECTED_BUT_FOUND = 29;


  {
    ER_EXPECTED_BUT_FOUND, "{0} attendu(e), mais : {1} trouv\u00e9(e)"},


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  {
    ER_INCORRECT_PROGRAMMER_ASSERTION,
      "Assertion de programmeur incorrecte ! - {0}"},


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  {
    ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "L'argument boolean(...) n'est plus facultatif avec la version brouillon 19990709 XPath ."},


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  {
    ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "',' trouv\u00e9 sans argument avant !"},


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  {
    ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "',' trouv\u00e9 sans argument apr\u00e8s !"},


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  {
    ER_PREDICATE_ILLEGAL_SYNTAX,
      "'..[predicate]' ou '.[predicate]' constitue une mauvaise syntaxe. Utilisez 'self::node()[predicate]' \u00e0 la place."},


  /** Field ER_ILLEGAL_AXIS_NAME          */
  //public static final int ER_ILLEGAL_AXIS_NAME = 35;


  {
    ER_ILLEGAL_AXIS_NAME, "nom d'axe non autoris\u00e9 : {0}"},


  /** Field ER_UNKNOWN_NODETYPE          */
  //public static final int ER_UNKNOWN_NODETYPE = 36;


  {
    ER_UNKNOWN_NODETYPE, "Type de n\u0153ud inconnu : {0}"},


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  {
    ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "Le libell\u00e9 de la forme ({0}) doit \u00eatre entre guillemets !"},


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  {
    ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "{0} ne peut pas \u00eatre format\u00e9(e) en number\u00a0!"},


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  {
    ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "Impossible de cr\u00e9er XML TransformerFactory Liaison : {0}"},


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  {
    ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "Erreur ! Impossible de trouver l'expression de s\u00e9lection xpath (-select)."},


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  {
    ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "ERREUR ! Impossible de trouver ENDOP apr\u00e8s OP_LOCATIONPATH"},


  /** Field ER_ERROR_OCCURED          */
  //public static final int ER_ERROR_OCCURED = 42;


  {
    ER_ERROR_OCCURED, "Une erreur s'est produite !"},


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  {
    ER_ILLEGAL_VARIABLE_REFERENCE,
      "L''\u00e9l\u00e9ment VariableReference a \u00e9t\u00e9 fourni pour la variable hors contexte ou sans d\u00e9finition !  Nom = {0}"},


  /** Field ER_AXES_NOT_ALLOWED          */
  //public static final int ER_AXES_NOT_ALLOWED = 44;


  {
    ER_AXES_NOT_ALLOWED,
      "Seuls les axes child:: et attribute:: sont admis dans les correspondances \u00e0 la forme !  Axes erron\u00e9s = {0}"},


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  {
    ER_KEY_HAS_TOO_MANY_ARGS,
      "key() dispose d'un nombre incorrect d'arguments."},


  /** Field ER_COUNT_TAKES_1_ARG          */
  //public static final int ER_COUNT_TAKES_1_ARG = 46;


  {
    ER_COUNT_TAKES_1_ARG,
      "La fonction count ne doit avoir qu'un seul argument !"},


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  {
    ER_COULDNOT_FIND_FUNCTION, "Impossible de trouver la fonction : {0}"},


  /** Field ER_UNSUPPORTED_ENCODING          */
  //public static final int ER_UNSUPPORTED_ENCODING = 48;


  {
    ER_UNSUPPORTED_ENCODING, "Codage non pris en charge : {0}"},


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  {
    ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "Un incident s'est produit au niveau de DTM dans getNextSibling... Tentative de r\u00e9cup\u00e9ration"},


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  {
    ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "Erreur de programmation : Impossible d'\u00e9crire dans EmptyNodeList."},


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  {
    ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory n'est pas pris en charge par XPathContext!"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "Le pr\u00e9fixe doit se r\u00e9soudre en nom d''espace : {0}"},


  /** Field ER_PARSE_NOT_SUPPORTED          */
  //public static final int ER_PARSE_NOT_SUPPORTED = 53;


  {
    ER_PARSE_NOT_SUPPORTED,
      "parse (source InputSource) non pris en charge dans XpathContext ! Impossible d'ouvrir {0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() non pris en charge dans XpathContext !"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "L'attribut child n'a pas de document propri\u00e9taire !"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "L'attribute child n'a pas d'\u00e9l\u00e9ment de document propri\u00e9taire !"},


  /** Field ER_SAX_API_NOT_HANDLED          */
  //public static final int ER_SAX_API_NOT_HANDLED = 57;


  {
    ER_SAX_API_NOT_HANDLED,
      "Les caract\u00e8res SAX API (char ch[]... ne sont pas pris en charge par DTM !"},


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  {
    ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... ne sont pas pris en charge par DTM !"},


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  {
    ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison ne peut pas prendre en charge les n\u0153uds de type {0}"},


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  {
    ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper ne peut pas prendre en charge les n\u0153uds de type {0}"},


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  {
    ER_XERCES_PARSE_ERROR_DETAILS,
      "Erreur DOM2Helper.parse : ID syst\u00e8me - {0} ligne - {1}"},


  /** Field ER_XERCES_PARSE_ERROR          */
  //public static final int ER_XERCES_PARSE_ERROR = 62;


  {
    ER_XERCES_PARSE_ERROR, "Erreur DOM2Helper.parse"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "Attention : impossible de sortir le texte avant l'\u00e9l\u00e9ment document !  Op\u00e9ration ignor\u00e9e ..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "Impossible d'avoir plus d'une racine sur un DOM !"},


  /** Field ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 65;


  {
    ER_INVALID_UTF16_SURROGATE,
      "Substitut UTF-16 incorrect d\u00e9tect\u00e9 : {0} ?"},


  /** Field ER_OIERROR          */
  //public static final int ER_OIERROR = 66;


  {
    ER_OIERROR, "Erreur d'E/S"},


  /** Field ER_CANNOT_CREATE_URL          */
  //public static final int ER_CANNOT_CREATE_URL = 67;


  {
    ER_CANNOT_CREATE_URL, "Impossible de cr\u00e9er une url pour : {0}"},


  /** Field ER_XPATH_READOBJECT          */
  //public static final int ER_XPATH_READOBJECT = 68;


  {
    ER_XPATH_READOBJECT, "Dans XPath.readObject : {0}"},

  
  /** Field ER_XPATH_READOBJECT         */
  //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


  {
    ER_FUNCTION_TOKEN_NOT_FOUND,
      "Unit\u00e9 lexicale function introuvable."},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "L'argument 'localName' est \u00e9gal \u00e0 null"},

  
   /**  Can not deal with XPath type:   */
  //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


  {
    ER_CANNOT_DEAL_XPATH_TYPE,
       "Impossible d'op\u00e9rer avec le type : {0} XPath"},

  
   /**  This NodeSet is not mutable  */
  //public static final int ER_NODESET_NOT_MUTABLE = 72;

	
  {
    ER_NODESET_NOT_MUTABLE,
       "Cet \u00e9l\u00e9ment NodeSet n'est pas mutable"},

  
   /**  This NodeSetDTM is not mutable  */
  //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


  {
    ER_NODESETDTM_NOT_MUTABLE,
       "Cet \u00e9l\u00e9ment NodeSetDTM n'est pas mutable"},

  
   /**  Variable not resolvable:   */
  //public static final int ER_VAR_NOT_RESOLVABLE = 74;


  {
    ER_VAR_NOT_RESOLVABLE,
        "Variable non r\u00e9solue : {0}"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 75;


  {
    ER_NULL_ERROR_HANDLER,
        "Gestionnaire d'erreur vide"},

  
   /**  Programmer's assertion: unknown opcode  */
  //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


  {
    ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "Assertion du programmeur : code op\u00e9ration inconnu : {0}"},

  
   /**  0 or 1   */
  //public static final int ER_ZERO_OR_ONE = 77;


  {
    ER_ZERO_OR_ONE,
       "0 ou 1"},
    
  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


  {
    ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "rtf() n'est pas pris en charge par XRTreeFragSelectWrapper"},

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


  {
    ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "asNodeIterator() n'est pas pris en charge par XRTreeFragSelectWrapper"},

  
   /**  fsb() not supported for XStringForChars   */
  //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


  {
    ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "fsb() n'est pas pris en charge pour XStringForChars"},

  
   /**  Could not find variable with the name of   */
  //public static final int ER_COULD_NOT_FIND_VAR = 81;


  {
    ER_COULD_NOT_FIND_VAR,
      "Impossible de trouver la variable nomm\u00e9e {0}"},

  
   /**  XStringForChars can not take a string for an argument   */
  //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


  {
    ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "XStringForChars ne peut pas avoir de cha\u00eene comme argument"},

  
   /**  The FastStringBuffer argument can not be null   */
  //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


  {
    ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "L'argument FastStringBuffer ne peut pas \u00eatre \u00e9gal \u00e0 null"},
    
/* MANTIS_XALAN CHANGE: BEGIN */ 
   /**  2 or 3   */
  //public static final int ER_TWO_OR_THREE = 84;


  {
    ER_TWO_OR_THREE,
       "2 ou 3"},


 /** Variable accessed before it is bound! */
  //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;


  {
    ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "Variable acc\u00e9d\u00e9e avant sa limite !"},


   /** XStringForFSB can not take a string for an argument! */
  //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;


  {
    ER_FSB_CANNOT_TAKE_STRING,
       "XStringForFSB ne peut pas \u00eatre une cha\u00eene pour un argument!"},


   /** Error! Setting the root of a walker to null! */
  //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;


  {
    ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n !!!! Erreur! Attribution d'une valeur null \u00e0 la racine d'un walker !!!"},


   /** This NodeSetDTM can not iterate to a previous node! */
  //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;


  {
    ER_NODESETDTM_CANNOT_ITERATE,
       "Ce NodeSetDTM ne peut pas \u00eatre r\u00e9p\u00e9t\u00e9 \u00e0 un noeud pr\u00e9c\u00e9dent !"},


  /** This NodeSet can not iterate to a previous node! */
  //public static final int ER_NODESET_CANNOT_ITERATE = 89;


  {
    ER_NODESET_CANNOT_ITERATE,
       "Ce NodeSet ne peut pas \u00eatre r\u00e9p\u00e9t\u00e9 vers un noeud pr\u00e9c\u00e9dent !"},


  /** This NodeSetDTM can not do indexing or counting functions! */
  //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;


  {
    ER_NODESETDTM_CANNOT_INDEX,
       "Ce NodeSetDTM ne peut pas ex\u00e9cuter de fonctions d'indexation ou de comptage !"},


  /** This NodeSet can not do indexing or counting functions! */
  //public static final int ER_NODESET_CANNOT_INDEX = 91;


  {
    ER_NODESET_CANNOT_INDEX,
       "Ce NodeSet ne peut pas ex\u00e9cuter de fonctions d'indexation ou de comptage !"},


  /** Can not call setShouldCacheNodes after nextNode has been called! */
  //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;


  {
    ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "Impossible d'appeler setShouldCacheNodes apr\u00e8s avoir appel\u00e9 nextNode !"},


  /** {0} only allows {1} arguments */
  //public static final int ER_ONLY_ALLOWS = 93;


  {
    ER_ONLY_ALLOWS,
       "{0} n''admet que les arguments {1} arguments"},


  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  //public static final int ER_UNKNOWN_STEP = 94;


  {
    ER_UNKNOWN_STEP,
       "Assertion du programmeur dans getNextStepPos : stepType inconnu : {0}"},


  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.

  /** Problem with RelativeLocationPath */
  //public static final int ER_EXPECTED_REL_LOC_PATH = 95;


  {
    ER_EXPECTED_REL_LOC_PATH,
       "Un chemin d'acc\u00e8s relatif \u00e9tait attendu apr\u00e8s le jeton '/' ou '//'."},


  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.

  /** Problem with LocationPath */
  //public static final int ER_EXPECTED_LOC_PATH = 96;


  {
    ER_EXPECTED_LOC_PATH,
       "Un chemin d'acc\u00e8s \u00e9tait attendu, mais l'objet suivant a \u00e9t\u00e9 rencontr\u00e9:  {0}"},


  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.

  /** Problem with Step */
  //public static final int ER_EXPECTED_LOC_STEP = 97;


  {
    ER_EXPECTED_LOC_STEP,
       "Une \u00e9tape de positionnement \u00e9t\u00e9 attendue \u00e0 la suite de l'objet '/' ou '//'."},


  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.

  /** Problem with NodeTest */
  //public static final int ER_EXPECTED_NODE_TEST = 98;


  {
    ER_EXPECTED_NODE_TEST,
       "Un test de noeud correspondant \u00e0 NCName:* ou \u00e0 QName \u00e9tait attendu."},


  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.

  /** Expected step pattern */
  //public static final int ER_EXPECTED_STEP_PATTERN = 99;


  {
    ER_EXPECTED_STEP_PATTERN,
       "Une forme d'\u00e9tape \u00e9tait attendue, mais '/' a \u00e9t\u00e9 rencontr\u00e9."},


  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
 
/** Expected relative path pattern */
  //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;


  {
    ER_EXPECTED_REL_PATH_PATTERN,
       "Une forme de chemin d'acc\u00e8s relatif \u00e9tait attendue."},


// Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;


  {
    ER_ARG_LOCALNAME_INVALID,
       "Le nom local de QNAME doit \u00eatre un NCName admis"},

  
// Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;


  {
    ER_ARG_PREFIX_INVALID,
       "Le pr\u00e9fixe de QNAME doit \u00eatre un NCName admis"},


// Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
  //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;


  {
    ER_CANT_CONVERT_TO_BOOLEAN,
       "Impossible de convertir {0} en valeur bool\u00e9enne."},


// Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
  // FIRST_ORDERED_NODE_TYPE.

  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;


  {
    ER_CANT_CONVERT_TO_SINGLENODE,
       "Impossible de convertir {0} en noeud unique. Cette m\u00e9thode d'obtention s'applique aux  types ANY_UNORDERED_NODE_TYPE et FIRST_ORDERED_NODE_TYPE."},


// Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;


  {
    ER_CANT_GET_SNAPSHOT_LENGTH,
       "Impossible d'obtenir une longueur d'instantan\u00e9 du type : {0}. Cette m\u00e9thode d'obtention s'applique aux types UNORDERED_NODE_SNAPSHOT_TYPE et ORDERED_NODE_SNAPSHOT_TYPE."},

  
  /** Field ER_NON_ITERATOR_TYPE                */
  //public static final int ER_NON_ITERATOR_TYPE        = 106;


  {
    ER_NON_ITERATOR_TYPE,
       "Impossible d'it\u00e9rer sur le type non r\u00e9p\u00e9titeur : {0}"},


// Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.

  /** Field ER_DOC_MUTATED                      */
  //public static final int ER_DOC_MUTATED              = 107;


  {
    ER_DOC_MUTATED,
       "Le document a mut\u00e9 depuis que le r\u00e9sultat a \u00e9t\u00e9 renvoy\u00e9. L'it\u00e9rateur est incorrect."},


/** Field ER_INVALID_XPATH_TYPE               */
  //public static final int ER_INVALID_XPATH_TYPE       = 108;


  {
    ER_INVALID_XPATH_TYPE,
       "Argument de type XPath incorrect : {0}"},


/** Field ER_EMPTY_XPATH_RESULT                */
  //public static final int ER_EMPTY_XPATH_RESULT       = 109;


  {
    ER_EMPTY_XPATH_RESULT,
       "Objet de r\u00e9sultat XPath vide"},


  /** Field ER_INCOMPATIBLE_TYPES                */
  //public static final int ER_INCOMPATIBLE_TYPES       = 110;


  {
    ER_INCOMPATIBLE_TYPES,
       "Impossible de forcer le type renvoy\u00e9 : {0} dans le fichier : {1}"},


/** Field ER_NULL_RESOLVER                     */
  //public static final int ER_NULL_RESOLVER            = 111;


  {
    ER_NULL_RESOLVER,
       "Impossible de r\u00e9soudre le pr\u00e9fixe sans d\u00e9composeur de pr\u00e9fixe."},


// Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_STRING            */
  //public static final int ER_CANT_CONVERT_TO_STRING   = 112;


  {
    ER_CANT_CONVERT_TO_STRING,
       "Impossible de convertir {0} en cha\u00eene ."},


// Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_NON_SNAPSHOT_TYPE                 */
  //public static final int ER_NON_SNAPSHOT_TYPE       = 113;


  {
    ER_NON_SNAPSHOT_TYPE,
       "Impossible d'appeler snapshotItem sur le type : {0}. Cette m\u00e9thode s'applique aux types UNORDERED_NODE_SNAPSHOT_TYPE et ORDERED_NODE_SNAPSHOT_TYPE."},


// Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.

/** Field ER_WRONG_DOCUMENT                    */
  //public static final int ER_WRONG_DOCUMENT          = 114;


  {
    ER_WRONG_DOCUMENT,
       "Le noeud de contexte n'appartient pas au document associ\u00e9 \u00e0 ce XPathEvaluator."},


// Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  /** Field ER_WRONG_NODETYPE                    */
  //public static final int ER_WRONG_NODETYPE          = 115;


  {
    ER_WRONG_NODETYPE ,
       "Le noeud de contexte n'est pas pris en charge."},


/** Field ER_XPATH_ERROR                       */
  //public static final int ER_XPATH_ERROR             = 116;


  {
    ER_XPATH_ERROR ,
       "Erreur inconnue dans XPath."},



  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  {
    WG_LOCALE_NAME_NOT_HANDLED,
      "Le nom de l'environnement local dans la fonction format-number n'est pas encore g\u00e9r\u00e9 !"},


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  {
    WG_PROPERTY_NOT_SUPPORTED,
      "XSL Property non pris en charge : {0}"},


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  {
    WG_DONT_DO_ANYTHING_WITH_NS,
      "Ne rien faire pour l'instant avec le nom d''espace {0} dans la propri\u00e9t\u00e9 : {1}"},


  /** Field WG_SECURITY_EXCEPTION          */
  //public static final int WG_SECURITY_EXCEPTION = 4;


  {
    WG_SECURITY_EXCEPTION,
      "Interception de SecurityException ors de la tentative d'acc\u00e8s \u00e0 la propri\u00e9t\u00e9 syst\u00e8me XSL : {0}"},


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  {
    WG_QUO_NO_LONGER_DEFINED,
      "L'ancienne syntaxe: quo(...) n'est plus d\u00e9finie dans XPath."},


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  {
    WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "XPath requiert un objet d\u00e9riv\u00e9 pour mettre en oeuvre nodeTest !"},


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  {
    WG_FUNCTION_TOKEN_NOT_FOUND,
      "Unit\u00e9 lexicale function introuvable."},


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  {
    WG_COULDNOT_FIND_FUNCTION,
      "Impossible de trouver la fonction : {0}"},


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "Impossible de cr\u00e9er une URL \u00e0 partir de : {0}"},


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  {
    WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "L'option -E n'est pas pris en charge pour l'analyseur syntaxique DTM"},


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  {
    WG_ILLEGAL_VARIABLE_REFERENCE,
      "L''\u00e9l\u00e9ment VariableReference a \u00e9t\u00e9 fourni pour la variable hors contexte ou sans d\u00e9finition !  Nom = {0}"},


  /** Field WG_UNSUPPORTED_ENCODING          */
  //public static final int WG_UNSUPPORTED_ENCODING = 12;


  {
    ER_UNSUPPORTED_ENCODING, "Codage non pris en charge : {0}"},


  // Other miscellaneous text used inside the code...

  { "ui_language", "fr"},
  { "help_language", "fr"},
  { "language", "fr"},
    { "BAD_CODE",
      "Param\u00e8tre pour createMessage hors limites"},
    { "FORMAT_FAILED",
      "Exception \u00e9mise pendant l'appel de messageFormat"},
    { "version", ">>>>>>> Version Xalan "},
    { "version2", "<<<<<<<"},
    { "yes", "oui"},
    { "line", "Ligne //"},
    { "column", "Colonne //"},
    { "xsldone", "XSLProcessor: termin\u00e9"},
    { "xpath_option", "options xpath : "},
    { "optionIN", "   [-in inputXMLURL]"},
    { "optionSelect", "   [expression xpath -select]"},
    { "optionMatch",
      "   [correspondance \u00e0 la forme -match (pour les diagnostics de correspondance)]"},
    { "optionAnyExpr",
      "Ou une expression xpath provoquera un \u00e9chec de diagnostic"},
    { "noParsermsg1", "Echec de XSL Process."},
    { "noParsermsg2", "** Impossible de trouver l'analyseur syntaxique**"},
    { "noParsermsg3", "Veuillez v\u00e9rifier votre chemin de classe."},
    { "noParsermsg4",
      "Si vous ne disposez pas de l'analyseur XML d'IBM pour Java, vous pouvez le t\u00e9l\u00e9charger \u00e0 l'adresse suivante"},
    { "noParsermsg5",
      "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
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
  public static final String ERROR_STRING = "//error";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "Erreur : ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "Attention : ";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "FORME ";

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }
}


