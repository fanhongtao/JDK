/*
 * @(#)XPATHErrorResources_de.java	1.4 03/05/01
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
public class XPATHErrorResources_de extends XPATHErrorResources
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
      "Funktion current() in einem Muster nicht zul\u00e4ssig!"},


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  //public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  {
    ER_CURRENT_TAKES_NO_ARGS,
      "Funktion current() hat keine Argumente!"},


  /** Field ER_DOCUMENT_REPLACED          */
  //public static final int ER_DOCUMENT_REPLACED = 3;


  {
    ER_DOCUMENT_REPLACED,
      "Die Implementierung der Funktion document() wurde durch org.apache.xalan.xslt.FuncDocument ersetzt!"},


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  //public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  {
    ER_CONTEXT_HAS_NO_OWNERDOC,
      "Kontext hat kein Eigent\u00fcmer-Dokument!"},


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  {
    ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "local-name() hat zu viele Argumente."},


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  {
    ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "namespace-uri() hat zu viele Argumente."},


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  {
    ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "normalize-space() hat zu viele Argumente."},


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  {
    ER_NUMBER_HAS_TOO_MANY_ARGS,
      "number() hat zu viele Argumente."},


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  //public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  {
    ER_NAME_HAS_TOO_MANY_ARGS, "name() hat zu viele Argumente."},


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  {
    ER_STRING_HAS_TOO_MANY_ARGS,
      "string() hat zu viele Argumente."},


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  //public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  {
    ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "string-length() hat zu viele Argumente."},


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  //public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  {
    ER_TRANSLATE_TAKES_3_ARGS,
      "Funktion translate() hat drei Argumente!"},


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  //public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  {
    ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "Funktion unparsed-entity-uri hat ein Argument!"},


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  //public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  {
    ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "Namensraum-Achse noch nicht implementiert!"},


  /** Field ER_UNKNOWN_AXIS          */
  //public static final int ER_UNKNOWN_AXIS = 15;


  {
    ER_UNKNOWN_AXIS, "Unbekannte Achse: {0}"},


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  //public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  {
    ER_UNKNOWN_MATCH_OPERATION, "Unbekannte Match-Operation!"},


  /** Field ER_INCORRECT_ARG_LENGTH          */
  //public static final int ER_INCORRECT_ARG_LENGTH = 17;


  {
    ER_INCORRECT_ARG_LENGTH,
      "Argumentl\u00e4nge beim Test des Knotens processing-instruction() ist nicht korrekt!"},


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  //public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  {
    ER_CANT_CONVERT_TO_NUMBER,
      "{0} kann nicht in Zahl konvertiert werden"},


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  //public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  {
    ER_CANT_CONVERT_TO_NODELIST,
      "{0} kann nicht in NodeList konvertiert werden!"},


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  //public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  {
    ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "{0} kann nicht in NodeSetDTM konvertiert werden!"},


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  //public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  {
    ER_CANT_CONVERT_TO_TYPE,
      "{0} kann nicht in type//{1} konvertiert werden"},


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  //public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  {
    ER_EXPECTED_MATCH_PATTERN,
      "Muster in getMatchScore erwartet!"},


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  //public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  {
    ER_COULDNOT_GET_VAR_NAMED,
      "Variable mit Namen {0} konnte nicht abgerufen werden"},


  /** Field ER_UNKNOWN_OPCODE          */
  //public static final int ER_UNKNOWN_OPCODE = 24;


  {
    ER_UNKNOWN_OPCODE, "FEHLER! Unbekannter Opcode: {0}"},


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  //public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  {
    ER_EXTRA_ILLEGAL_TOKENS, "Zus\u00e4tzliche unzul\u00e4ssige Token: {0}"},


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  //public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  {
    ER_EXPECTED_DOUBLE_QUOTE,
      "Literalwert mit falschen Anf\u00fchrungszeichen... doppelte Anf\u00fchrungszeichen erwartet!"},


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  //public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  {
    ER_EXPECTED_SINGLE_QUOTE,
      "Literalwert mit falschen Anf\u00fchrungszeichen... einfache Anf\u00fchrungszeichen erwartet!"},


  /** Field ER_EMPTY_EXPRESSION          */
  //public static final int ER_EMPTY_EXPRESSION = 28;


  {
    ER_EMPTY_EXPRESSION, "Leerer Ausdruck!"},


  /** Field ER_EXPECTED_BUT_FOUND          */
  //public static final int ER_EXPECTED_BUT_FOUND = 29;


  {
    ER_EXPECTED_BUT_FOUND, "{0} erwartet, gefunden: {1}"},


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  //public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  {
    ER_INCORRECT_PROGRAMMER_ASSERTION,
      "Programmierannahme ist falsch! - {0}"},


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  //public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  {
    ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "Boolesches Argument (...) ist bei Xpath-Draft 19990709 nicht mehr optional."},


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  {
    ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "',' gefunden, aber davor kein Argument!"},


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  //public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  {
    ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "',' gefunden, aber danach kein Argument!"},


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  //public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  {
    ER_PREDICATE_ILLEGAL_SYNTAX,
      "Syntax '..[predicate]' oder '.[predicate]' nicht zul\u00e4ssig. Verwenden Sie statt dessen 'self::node()[Pr\u00e4dikat]'."},


  /** Field ER_ILLEGAL_AXIS_NAME          */
  //public static final int ER_ILLEGAL_AXIS_NAME = 35;


  {
    ER_ILLEGAL_AXIS_NAME, "Unzul\u00e4ssiger Achsenname: {0}"},


  /** Field ER_UNKNOWN_NODETYPE          */
  //public static final int ER_UNKNOWN_NODETYPE = 36;


  {
    ER_UNKNOWN_NODETYPE, "Unbekannter nodetype: {0}"},


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  //public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  {
    ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "Musterliteralwert ({0}) muss in Anf\u00fchrungszeichen eingeschlossen werden!"},


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  //public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  {
    ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "{0} konnte nicht als Zahl formatiert werden!"},


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  //public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  {
    ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "XML TransformerFactory Liaison konnte nicht erstellt werden: {0}"},


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  //public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  {
    ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "Fehler! 'select'-Ausdruck bei xpath nicht gefunden (-select)."},


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  //public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  {
    ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "FEHLER! ENDOP nach OP_LOCATIONPATH nicht gefunden"},


  /** Field ER_ERROR_OCCURED          */
  //public static final int ER_ERROR_OCCURED = 42;


  {
    ER_ERROR_OCCURED, "Fehler!"},


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  {
    ER_ILLEGAL_VARIABLE_REFERENCE,
      "VariableReference f\u00fcr eine Variable au\u00dferhalb des Kontexts oder ohne Definition angegeben!  Name = {0}"},


  /** Field ER_AXES_NOT_ALLOWED          */
  //public static final int ER_AXES_NOT_ALLOWED = 44;


  {
    ER_AXES_NOT_ALLOWED,
      "Nur die Achsen child:: und attribute:: sind in Mustern zul\u00e4ssig! Regel verletzende Achse = {0}"},


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  //public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  {
    ER_KEY_HAS_TOO_MANY_ARGS,
      "key() mit falscher Anzahl von Argumenten."},


  /** Field ER_COUNT_TAKES_1_ARG          */
  //public static final int ER_COUNT_TAKES_1_ARG = 46;


  {
    ER_COUNT_TAKES_1_ARG,
      "Funktion count hat ein Argument!"},


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  //public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  {
    ER_COULDNOT_FIND_FUNCTION, "Funktion nicht gefunden: {0}"},


  /** Field ER_UNSUPPORTED_ENCODING          */
  //public static final int ER_UNSUPPORTED_ENCODING = 48;


  {
    ER_UNSUPPORTED_ENCODING, "Codierung nicht unterst\u00fctzt: {0}"},


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  //public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  {
    ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "Problem bei DTM in getNextSibling... Wiederherstellung wird versucht"},


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  //public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  {
    ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "Programmierfehler: In EmptyNodeList kann nicht geschrieben werden."},


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  //public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  {
    ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory wird von XpathContext nicht unterst\u00fctzt!"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "Pr\u00e4fix muss sich in Namensraum aufl\u00f6sen lassen: {0}"},


  /** Field ER_PARSE_NOT_SUPPORTED          */
  //public static final int ER_PARSE_NOT_SUPPORTED = 53;


  {
    ER_PARSE_NOT_SUPPORTED,
      "Parsen von (InputSource Quelle) in XpathContext nicht unterst\u00fctzt! {0} kann nicht ge\u00f6ffnet werden"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() in XpathContext nicht unterst\u00fctzt!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "Attribut-Tochterknoten hat kein Eigent\u00fcmer-Dokument!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "Attribut-Tochterknoten hat kein Element Eigent\u00fcmer-Dokument!"},


  /** Field ER_SAX_API_NOT_HANDLED          */
  //public static final int ER_SAX_API_NOT_HANDLED = 57;


  {
    ER_SAX_API_NOT_HANDLED,
      "SAX API-Zeichen (char ch[]... werden vom DTM nicht bearbeitet!"},


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  //public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  {
    ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... wird vom DTM nicht bearbeitet!"},


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  //public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  {
    ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison kann Knoten vom Typ {0} nicht bearbeiten"},


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  //public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  {
    ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper kann Knoten vom Typ {0} nicht bearbeiten"},


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  //public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  {
    ER_XERCES_PARSE_ERROR_DETAILS,
      "Fehler bei DOM2Helper.parse: SystemID - {0} Zeile - {1}"},


  /** Field ER_XERCES_PARSE_ERROR          */
  //public static final int ER_XERCES_PARSE_ERROR = 62;


  {
    ER_XERCES_PARSE_ERROR, "Fehler bei DOM2Helper.parse"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "Warnung: Vor einem Dokumentelement kann kein Text ausgegeben werden! Wird ignoriert..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "Ein DOM kann nur einen Stamm haben!"},


  /** Field ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 65;


  {
    ER_INVALID_UTF16_SURROGATE,
      "Ung\u00fcltiges UTF-16-Surrogat erkannt: {0} ?"},


  /** Field ER_OIERROR          */
  //public static final int ER_OIERROR = 66;


  {
    ER_OIERROR, "IO-Fehler"},


  /** Field ER_CANNOT_CREATE_URL          */
  //public static final int ER_CANNOT_CREATE_URL = 67;


  {
    ER_CANNOT_CREATE_URL, "URL kann nicht erstellt werden f\u00fcr: {0}"},


  /** Field ER_XPATH_READOBJECT          */
  //public static final int ER_XPATH_READOBJECT = 68;



  {
    ER_XPATH_READOBJECT, "In XPath.readObject: {0}"},

  
  /** Field ER_XPATH_READOBJECT         */
  //public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;


  {
    ER_FUNCTION_TOKEN_NOT_FOUND,
      "Funktions-Token nicht gefunden."},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "Argument 'localName' ist Null"},

  
   /**  Can not deal with XPath type:   */
  //public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;


  {
    ER_CANNOT_DEAL_XPATH_TYPE,
       "Bearbeitung nicht m\u00f6glich von XPath vom Typ: {0}"},

  
   /**  This NodeSet is not mutable  */
  //public static final int ER_NODESET_NOT_MUTABLE = 72;


  {
    ER_NODESET_NOT_MUTABLE,
       "Dieses NodeSet ist nicht mutierbar"},

  
   /**  This NodeSetDTM is not mutable  */
  //public static final int ER_NODESETDTM_NOT_MUTABLE = 73;


  {
    ER_NODESETDTM_NOT_MUTABLE,
       "Dieses NodeSetDTM ist nicht mutierbar"},

  
   /**  Variable not resolvable:   */
  //public static final int ER_VAR_NOT_RESOLVABLE = 74;


  {
    ER_VAR_NOT_RESOLVABLE,
        "Variable nicht aufl\u00f6sbar: {0}"},

  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 75;


  {
    ER_NULL_ERROR_HANDLER,
        "Ung\u00fcltiger Error-Handler"},

  
   /**  Programmer's assertion: unknown opcode  */
  //public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;


  {
    ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "Programmierannahme: Unbekannter Opcode: {0}"},

  
   /**  0 or 1   */
  //public static final int ER_ZERO_OR_ONE = 77;


  {
    ER_ZERO_OR_ONE,
       "0 oder 1"},

  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;


  {
    ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "rtf() nicht unterst\u00fctzt von XRTreeFragSelectWrapper"},

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  //public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;


  {
    ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "asNodeIterator() nicht unterst\u00fctzt von XRTreeFragSelectWrapper"},

  
   /**  fsb() not supported for XStringForChars   */
  //public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;


  {
    ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "fsb() nicht unterst\u00fctzt von XStringForChars"},

  
   /**  Could not find variable with the name of   */
  //public static final int ER_COULD_NOT_FIND_VAR = 81;


  {
    ER_COULD_NOT_FIND_VAR,
      "Variable mit Namen {0} kann nicht gefunden werden"},

  
   /**  XStringForChars can not take a string for an argument   */
  //public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;


  {
    ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "XStringForChars kann keine Zeichenkette als Argument haben"},

  
   /**  The FastStringBuffer argument can not be null   */
  //public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;


  {
    ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "Das FastStringBuffer-Argument darf nicht Null sein"},
    
 /* MANTIS_XALAN CHANGE: BEGIN */ 
   /**  2 or 3   */
  //public static final int ER_TWO_OR_THREE = 84;


  {
    ER_TWO_OR_THREE,
       "2 oder 3"},


   /** Variable accessed before it is bound! */
  //public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;


  {
    ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "Zugriff auf Variable vor deren Bindung!"},


   /** XStringForFSB can not take a string for an argument! */
  //public static final int ER_FSB_CANNOT_TAKE_STRING = 86;


  {
    ER_FSB_CANNOT_TAKE_STRING,
       "XStringForFSB darf keine Zeichenkette als Argument \u00fcbergeben werden!"},


   /** Error! Setting the root of a walker to null! */
  //public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;


  {
    ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n !!!! Fehler! Ausgangspunkt f\u00fcr Walker wird auf Null gesetzt!!!"},


   /** This NodeSetDTM can not iterate to a previous node! */
  //public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;


  {
    ER_NODESETDTM_CANNOT_ITERATE,
       "Dieses NodeSetDTM-Element kann nicht zu einem vorausgegangenen Knoten iterieren!"},


  /** This NodeSet can not iterate to a previous node! */
  //public static final int ER_NODESET_CANNOT_ITERATE = 89;


  {
    ER_NODESET_CANNOT_ITERATE,
       "Dieses NodeSet-Element kann nicht zu einem vorausgegangenen Knoten iterieren!"},


  /** This NodeSetDTM can not do indexing or counting functions! */
  //public static final int ER_NODESETDTM_CANNOT_INDEX = 90;


  {
    ER_NODESETDTM_CANNOT_INDEX,
       "Dieses NodeSetDTM-Element kann keine Indizierung oder Z\u00e4hlung durchf\u00fchren!"},


  /** This NodeSet can not do indexing or counting functions! */
  //public static final int ER_NODESET_CANNOT_INDEX = 91;


  {
    ER_NODESET_CANNOT_INDEX,
       "Dieses NodeSet-Element kann keine Indizierung oder Z\u00e4hlung durchf\u00fchren!"},


  /** Can not call setShouldCacheNodes after nextNode has been called! */
  //public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;


  {
    ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "setShouldCacheNodes kann nicht nach dem Aufruf von nextNode aufgerufen werden!"},


  /** {0} only allows {1} arguments */
  //public static final int ER_ONLY_ALLOWS = 93;


  {
    ER_ONLY_ALLOWS,
       "F\u00fcr {0} sind nur {1} Argumente zul\u00e4ssig."},


  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  //public static final int ER_UNKNOWN_STEP = 94;


  {
    ER_UNKNOWN_STEP,
       "Programmierannahme in getNextStepPos: unbekannter stepType: {0}"},


  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.

  /** Problem with RelativeLocationPath */
  //public static final int ER_EXPECTED_REL_LOC_PATH = 95;


  {
    ER_EXPECTED_REL_LOC_PATH,
       "Nach dem Token '/' bzw. '//' wurde ein relativer Pfad zum Speicherort erwartet."},


  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.

  /** Problem with LocationPath */
  //public static final int ER_EXPECTED_LOC_PATH = 96;


  {
    ER_EXPECTED_LOC_PATH,
       "Es wurde ein Pfad zum Speicherort erwartet. Vorgefunden wurde jedoch der folgende Token:  {0}"},


  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.

  /** Problem with Step */
  //public static final int ER_EXPECTED_LOC_STEP = 97;


  {
    ER_EXPECTED_LOC_STEP,
       "Nach dem Token '/' bzw. '//' wurde ein Schritt zum Speicherort erwartet."},


  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.

  /** Problem with NodeTest */
  //public static final int ER_EXPECTED_NODE_TEST = 98;


  {
    ER_EXPECTED_NODE_TEST,
       "Es wurde ein Knotentest erwartet, der entweder NCName:* oder QName entspricht."},


  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.

  /** Expected step pattern */
  //public static final int ER_EXPECTED_STEP_PATTERN = 99;


  {
    ER_EXPECTED_STEP_PATTERN,
       "Es wurde ein Schrittmuster erwartet. Vorgefunden wurde jedoch das Zeichen '/'."},


  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
 
  /** Expected relative path pattern */
  //public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;


  {
    ER_EXPECTED_REL_PATH_PATTERN,
       "Es wurde ein relatives Pfadmuster erwartet."},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;


  {
    ER_ARG_LOCALNAME_INVALID,
       "Der lokale Name in QNAME muss einem g\u00fcltigen 'NCName' entsprechen."},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;


  {
    ER_ARG_PREFIX_INVALID,
       "Das Pr\u00e4fix in QNAME muss einem g\u00fcltigen 'NCName' entsprechen."},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_BOOLEAN          */
  //public static final int ER_CANT_CONVERT_TO_BOOLEAN = 103;


  {
    ER_CANT_CONVERT_TO_BOOLEAN,
       "{0} kann nicht in Datentyp ''boolean'' konvertiert werden."},


  // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and 
  // FIRST_ORDERED_NODE_TYPE.

  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  //public static final int ER_CANT_CONVERT_TO_SINGLENODE = 104;


  {
    ER_CANT_CONVERT_TO_SINGLENODE,
       "{0} kann nicht in einen einzelnen Knoten konvertiert werden. Diese Abruffunktion ist f\u00fcr die Typen ANY_UNORDERED_NODE_TYPE und FIRST_ORDERED_NODE_TYPE bestimmt."},


  // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  //public static final int ER_CANT_GET_SNAPSHOT_LENGTH = 105;


  {
    ER_CANT_GET_SNAPSHOT_LENGTH,
       "Die Snapshot-L\u00e4nge f\u00fcr diesen Typ kann nicht ermittelt werden: {0}. Diese Abruffunktion ist f\u00fcr die Typen UNORDERED_NODE_SNAPSHOT_TYPE und ORDERED_NODE_SNAPSHOT_TYPE bestimmt."},


  /** Field ER_NON_ITERATOR_TYPE                */
  //public static final int ER_NON_ITERATOR_TYPE        = 106;


  {
    ER_NON_ITERATOR_TYPE,
       "Es kann keine Iteration \u00fcber einen nicht-iterativen Typ durchgef\u00fchrt werden: {0}"},


  // Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.

  /** Field ER_DOC_MUTATED                      */
  //public static final int ER_DOC_MUTATED              = 107;


  {
    ER_DOC_MUTATED,
       "Das Dokument wurde ge\u00e4ndert, nachdem das Ergebnis zur\u00fcckgegeben wurde. Der Iterator ist ung\u00fcltig."},


  /** Field ER_INVALID_XPATH_TYPE               */
  //public static final int ER_INVALID_XPATH_TYPE       = 108;


  {
    ER_INVALID_XPATH_TYPE,
       "Ung\u00fcltiges XPath-Typargument: {0}"},


  /** Field ER_EMPTY_XPATH_RESULT                */
  //public static final int ER_EMPTY_XPATH_RESULT       = 109;


  {
    ER_EMPTY_XPATH_RESULT,
       "Leeres XPath-Ergebnisobjekt"},


  /** Field ER_INCOMPATIBLE_TYPES                */
  //public static final int ER_INCOMPATIBLE_TYPES       = 110;


  {
    ER_INCOMPATIBLE_TYPES,
       "Der zur\u00fcckgegebene Typ {0} kann nicht in den angegebenen Typ umgewandelt werden: {1}"},


  /** Field ER_NULL_RESOLVER                     */
  //public static final int ER_NULL_RESOLVER            = 111;


  {
    ER_NULL_RESOLVER,
       "Pr\u00e4fix kann nicht aufgel\u00f6st werden, wenn Pr\u00e4fix-Resolver Null ist."},


  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.

  /** Field ER_CANT_CONVERT_TO_STRING            */
  //public static final int ER_CANT_CONVERT_TO_STRING   = 112;


  {
    ER_CANT_CONVERT_TO_STRING,
       "{0} kann nicht in eine Zeichenkette konvertiert werden."},


  // Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.

  /** Field ER_NON_SNAPSHOT_TYPE                 */
  //public static final int ER_NON_SNAPSHOT_TYPE       = 113;


  {
    ER_NON_SNAPSHOT_TYPE,
       "snapshotItem kann nicht f\u00fcr diesen Typ aufgerufen werden: {0}. Diese Methode ist f\u00fcr die Typen UNORDERED_NODE_SNAPSHOT_TYPE und ORDERED_NODE_SNAPSHOT_TYPE bestimmt."},


  // Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.

  /** Field ER_WRONG_DOCUMENT                    */
  //public static final int ER_WRONG_DOCUMENT          = 114;


  {
    ER_WRONG_DOCUMENT,
       "Der Kontextknoten geh\u00f6rt nicht zu dem Dokument, das an dieses XPathEvaluator-Element gebunden ist."},


  // Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  /** Field ER_WRONG_NODETYPE                    */
  //public static final int ER_WRONG_NODETYPE          = 115;


  {
    ER_WRONG_NODETYPE ,
       "Der Kontextknotentyp wird nicht unterst\u00fctzt."},


  /** Field ER_XPATH_ERROR                       */
  //public static final int ER_XPATH_ERROR             = 116;


  {
    ER_XPATH_ERROR ,
       "Unbekannter Fehler in XPath."},



  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  //public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  {
    WG_LOCALE_NAME_NOT_HANDLED,
      "Name von locale in der Funktion format-number noch nicht bearbeitet!"},


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  //public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  {
    WG_PROPERTY_NOT_SUPPORTED,
      "XSL Property nicht unterst\u00fctzt: {0}"},


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  //public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  {
    WG_DONT_DO_ANYTHING_WITH_NS,
      "Zurzeit nichts tun mit Namensraum {0} in Eigenschaft: {1}"},


  /** Field WG_SECURITY_EXCEPTION          */
  //public static final int WG_SECURITY_EXCEPTION = 4;


  {
    WG_SECURITY_EXCEPTION,
      "SecurityException beim Versuch des Zugriffs auf XSL-Systemeigenschaft: {0}"},


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  //public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  {
    WG_QUO_NO_LONGER_DEFINED,
      "Alte Syntax: quo(...) ist in XPath nicht mehr definiert."},


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  //public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  {
    WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "XPath ben\u00f6tigt ein abgeleitetes Objekt zum Implementieren von nodeTest!"},


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  //public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  {
    WG_FUNCTION_TOKEN_NOT_FOUND,
      "Funktions-Token nicht gefunden."},


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  //public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  {
    WG_COULDNOT_FIND_FUNCTION,
      "Funktion nicht gefunden: {0}"},


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  {
    WG_CANNOT_MAKE_URL_FROM,
      "URL kann nicht erstellt werden aus: {0}"},


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  //public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  {
    WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "Option -E f\u00fcr DTM-Parser nicht unterst\u00fctzt"},


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  //public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  {
    WG_ILLEGAL_VARIABLE_REFERENCE,
      "VariableReference f\u00fcr Variable au\u00dferhalb des Kontexts oder ohne Definition angegeben!  Name = {0}"},


  /** Field WG_UNSUPPORTED_ENCODING          */
  //public static final int WG_UNSUPPORTED_ENCODING = 12;


  {
    WG_UNSUPPORTED_ENCODING, "Nicht unterst\u00fctzte Codierung: {0}"},


  // Other miscellaneous text used inside the code...
  { "ui_language", "de"},
  { "help_language", "de"},
  { "language", "de"},
    { "BAD_CODE",
      "Parameter f\u00fcr createMessage au\u00dferhalb der Grenzwerte"},
    { "FORMAT_FAILED",
      "Ausnahme bei Aufruf von messageFormat"},
    { "version", ">>>>>>> Xalan Version "},
    { "version2", "<<<<<<<"},
    { "yes", "ja"},
    { "line", "Zeile //"},
    { "column", "Spalte //"},
    { "xsldone", "XSLProcessor: fertig"},
    { "xpath_option", "xpath-Optionen: "},
    { "optionIN", "   [-in inputXMLURL]"},
    { "optionSelect", "   [-select xpath-Ausdruck]"},
    { "optionMatch", "   [-match Muster (f\u00fcr Trefferdiagnose)]"},
    { "optionAnyExpr",
      "Oder irgendein xpath-Ausdruck f\u00fchrt einen Diagnose-Dump aus"},
    { "noParsermsg1", "XSL Process fehlgeschlagen."},
    { "noParsermsg2", "** Parser nicht gefunden**"},
    { "noParsermsg3", "Pr\u00fcfen Sie Ihren classpath."},
    { "noParsermsg4",
      "Wenn Sie IBMs XML Parser for Java nicht haben, k\u00f6nnen Sie ihn von folgender Adresse herunterladen"},
    { "noParsermsg5", "IBMs AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
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
  public static final String ERROR_STRING = "//Fehler";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "Fehler: ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "Warnung: ";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "MUSTER ";

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

