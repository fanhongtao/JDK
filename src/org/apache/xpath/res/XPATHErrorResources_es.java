/*
 * @(#)XPATHErrorResources_es.java	1.7 02/03/26
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

package org.apache.xpath.res;

import org.apache.xml.utils.res.XResourceBundleBase;


import java.util.*;

import java.text.DecimalFormat;

/**
 * <meta name="usage" content="advanced"/>
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING). The array will be
 * automatically filled in with the keys, but you need to
 * fill in the actual message string. Follow the instructions
 * below.
 */
public class XPATHErrorResources_es extends XPATHErrorResources
{

  /** Field ERROR_SUFFIX          */
  public static final String ERROR_SUFFIX = "ER";

  /** Field WARNING_SUFFIX          */
  public static final String WARNING_SUFFIX = "WR";

  /** Field MAX_CODE          */
  public static final int MAX_CODE = 83;  // this is needed to keep track of the number of messages          


  /** Field MAX_WARNING          */
  public static final int MAX_WARNING = 11;  // this is needed to keep track of the number of warnings

  /** Field MAX_OTHERS          */
  public static final int MAX_OTHERS = 20;

  /** Field MAX_MESSAGES          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;

  /** Field contents          */
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

  /** Field ERROR0000          */

  public static final int ERROR0000 = 0;

  static
  {
    contents[ERROR0000][1] = "{0}";
  }

  /** Field ER_CURRENT_NOT_ALLOWED_IN_MATCH          */
  public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;


  static
  {
    contents[ER_CURRENT_NOT_ALLOWED_IN_MATCH][1] =
      "Esta funci\u00f3n no se permite en un patr\u00f3n de b\u00fasqueda";
  }

  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  public static final int ER_CURRENT_TAKES_NO_ARGS = 2;

  static
  {
    contents[ER_CURRENT_TAKES_NO_ARGS][1] =
      "Esta funci\u00f3n no acepta argumentos.";
  }

  /** Field ER_DOCUMENT_REPLACED          */
  public static final int ER_DOCUMENT_REPLACED = 3;

  static
  {
    contents[ER_DOCUMENT_REPLACED][1] =
      "la implementaci\u00f3n de la funci\u00f3n del documento se ha sustituido por org.apache.xalan.xslt.FuncDocument.!";
  }

  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;

  static
  {
    contents[ER_CONTEXT_HAS_NO_OWNERDOC][1] =
      "el contexto no tiene documento propietario!";
  }

  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;

  static
  {
    contents[ER_LOCALNAME_HAS_TOO_MANY_ARGS][1] =
      "el nombre local tiene demasiados argumentos.";
  }

  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;

  static
  {
    contents[ER_NAMESPACEURI_HAS_TOO_MANY_ARGS][1] =
      "el URI del espacio de nombre tiene demasiados argumentos.";
  }

  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;

  static
  {
    contents[ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS][1] =
      "el espacio est\u00e1ndar tiene demasiados argumentos.";
  }

  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;

  static
  {
    contents[ER_NUMBER_HAS_TOO_MANY_ARGS][1] =
      "el n\u00famero tiene demasiados argumentos.";
  }

  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;

  static
  {
    contents[ER_NAME_HAS_TOO_MANY_ARGS][1] = "el nombre tiene demasiados argumentos.";
  }

  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;

  static
  {
    contents[ER_STRING_HAS_TOO_MANY_ARGS][1] =
      "la cadena tiene demasiados argumentos.";
  }

  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;

  static
  {
    contents[ER_STRINGLENGTH_HAS_TOO_MANY_ARGS][1] =
      "la longitud de la cadena tiene demasiados argumentos.";
  }

  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;

  static
  {
    contents[ER_TRANSLATE_TAKES_3_ARGS][1] =
      "La funci\u00f3n de traducci\u00f3n utiliza tres argumentos!";
  }

  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;

  static
  {
    contents[ER_UNPARSEDENTITYURI_TAKES_1_ARG][1] =
      "La funci\u00f3n unparsed-entity-uri deber\u00eda utilizar un argumento!";
  }

  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;

  static
  {
    contents[ER_NAMESPACEAXIS_NOT_IMPLEMENTED][1] =
      "no se ha implementado todav\u00eda el eje de espacio de nombre!";
  }

  /** Field ER_UNKNOWN_AXIS          */
  public static final int ER_UNKNOWN_AXIS = 15;

  static
  {
    contents[ER_UNKNOWN_AXIS][1] = "eje desconocido: {0}";
  }

  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  public static final int ER_UNKNOWN_MATCH_OPERATION = 16;

  static
  {
    contents[ER_UNKNOWN_MATCH_OPERATION][1] = "operaci\u00f3n de b\u00fasqueda desconocida!";
  }

  /** Field ER_INCORRECT_ARG_LENGTH          */
  public static final int ER_INCORRECT_ARG_LENGTH = 17;

  static
  {
    contents[ER_INCORRECT_ARG_LENGTH][1] =
      "La longitud de los argumentos de la prueba del nodo de instrucci\u00f3n de procesamiento () es incorrecta!";
  }

  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  public static final int ER_CANT_CONVERT_TO_NUMBER = 18;

  static
  {
    contents[ER_CANT_CONVERT_TO_NUMBER][1] =
      "No se puede convertir {0} en un n\u00famero.";
  }

  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  public static final int ER_CANT_CONVERT_TO_NODELIST = 19;

  static
  {
    contents[ER_CANT_CONVERT_TO_NODELIST][1] =
      "No se puede convertir {0} en una NodeList.";
  }

  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;

  static
  {
    contents[ER_CANT_CONVERT_TO_MUTABLENODELIST][1] =
      "No se puede convertir {0} en un NodeSetDTM!.";
  }

  /** Field ER_CANT_CONVERT_TO_TYPE          */
  public static final int ER_CANT_CONVERT_TO_TYPE = 21;

  static
  {
    contents[ER_CANT_CONVERT_TO_TYPE][1] =
      "No se puede convertir {0} en un tipo#{1}";
  }

  /** Field ER_EXPECTED_MATCH_PATTERN          */
  public static final int ER_EXPECTED_MATCH_PATTERN = 22;

  static
  {
    contents[ER_EXPECTED_MATCH_PATTERN][1] =
      "Patr\u00f3n de b\u00fasqueda esperado en getMatchScore!";
  }

  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  public static final int ER_COULDNOT_GET_VAR_NAMED = 23;

  static
  {
    contents[ER_COULDNOT_GET_VAR_NAMED][1] =
      "No se ha podido obtener una variable con el nombre {0}";
  }

  /** Field ER_UNKNOWN_OPCODE          */
  public static final int ER_UNKNOWN_OPCODE = 24;

  static
  {
    contents[ER_UNKNOWN_OPCODE][1] = "ERROR! C\u00f3digo de operaci\u00f3n desconocido: {0}";
  }

  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;

  static
  {
    contents[ER_EXTRA_ILLEGAL_TOKENS][1] = "Tokens adicionales no permitidos: {0}";
  }

  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;

  static
  {
    contents[ER_EXPECTED_DOUBLE_QUOTE][1] =
      "error de entrecomillado... debe usar comillas dobles!";
  }

  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  public static final int ER_EXPECTED_SINGLE_QUOTE = 27;

  static
  {
    contents[ER_EXPECTED_SINGLE_QUOTE][1] =
      "error de entrecomillado... debe usar comillas sencillas!";
  }

  /** Field ER_EMPTY_EXPRESSION          */
  public static final int ER_EMPTY_EXPRESSION = 28;

  static
  {
    contents[ER_EMPTY_EXPRESSION][1] = "Expresi\u00f3n vac\u00eda!";
  }

  /** Field ER_EXPECTED_BUT_FOUND          */
  public static final int ER_EXPECTED_BUT_FOUND = 29;

  static
  {
    contents[ER_EXPECTED_BUT_FOUND][1] = "Esperados {0}, pero encontrados: {1}";
  }

  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;

  static
  {
    contents[ER_INCORRECT_PROGRAMMER_ASSERTION][1] =
      "El aserto del programador es incorrecto! - {0}";
  }

  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;

  static
  {
    contents[ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL][1] =
      "El argumento booleano(...) ya no es opcional con el borrador 19990709 Xpath.";
  }

  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;

  static
  {
    contents[ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG][1] =
      "Se ha encontrado ',' pero no hay ning\u00fan argumento anterior!";
  }

  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;

  static
  {
    contents[ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG][1] =
      "Se ha encontrado ',' pero no hay ning\u00fan argumento posterior!";
  }

  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;

  static
  {
    contents[ER_PREDICATE_ILLEGAL_SYNTAX][1] =
      "La sintaxis '..[predicate]' no es v\u00e1lida.  Sustituir por 'self::node()[predicate]'.";
  }

  /** Field ER_ILLEGAL_AXIS_NAME          */
  public static final int ER_ILLEGAL_AXIS_NAME = 35;

  static
  {
    contents[ER_ILLEGAL_AXIS_NAME][1] = "nombre de eje no v\u00e1lido: {0}";
  }

  /** Field ER_UNKNOWN_NODETYPE          */
  public static final int ER_UNKNOWN_NODETYPE = 36;

  static
  {
    contents[ER_UNKNOWN_NODETYPE][1] = "Tipo de nodo desconocido: {0}";
  }

  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;

  static
  {
    contents[ER_PATTERN_LITERAL_NEEDS_BE_QUOTED][1] =
      "La cadena literal del patr\u00f3 ({0}) requiere entrecomillado!";
  }

  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;

  static
  {
    contents[ER_COULDNOT_BE_FORMATTED_TO_NUMBER][1] =
      "No se ha podido dar formato num\u00e9rico a {0}!";
  }

  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;

  static
  {
    contents[ER_COULDNOT_CREATE_XMLPROCESSORLIAISON][1] =
      "No se ha podido crear un v\u00ednculo XML TransformerFactory: {0}";
  }

  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;

  static
  {
    contents[ER_DIDNOT_FIND_XPATH_SELECT_EXP][1] =

      "Error! No se ha encontrado la expresi\u00f3n de selecci\u00f3n de xpath (-seleccionar).";
  }

  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;

  static
  {
    contents[ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH][1] =
      "ERROR! No se ha podido encontra ENDOP despu\u00e9s de OP_LOCATIONPATH.";
  }

  /** Field ER_ERROR_OCCURED          */
  public static final int ER_ERROR_OCCURED = 42;

  static
  {
    contents[ER_ERROR_OCCURED][1] = "Se ha producido un error!";
  }

  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;

  static
  {
    contents[ER_ILLEGAL_VARIABLE_REFERENCE][1] =
      "Referencia variable atribuida a una variable fuera de contexto o sin definici\u00f3n! Nombre = {0}";
  }

  /** Field ER_AXES_NOT_ALLOWED          */
  public static final int ER_AXES_NOT_ALLOWED = 44;

  static
  {
    contents[ER_AXES_NOT_ALLOWED][1] =
      "S\u00f3lo se permiten los ejes child:: y attribute:: en los patrones de b\u00fasqueda!  Ejes incompatibles = {0}";
  }

  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;

  static
  {
    contents[ER_KEY_HAS_TOO_MANY_ARGS][1] =
      "El n\u00famero de argumentos de la clave es incorrecto.";
  }

  /** Field ER_COUNT_TAKES_1_ARG          */
  public static final int ER_COUNT_TAKES_1_ARG = 46;

  static
  {
    contents[ER_COUNT_TAKES_1_ARG][1] =
      "La funci\u00f3n de c\u00f3mputo deber\u00eda utilizar un argumento!";
  }


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  public static final int ER_COULDNOT_FIND_FUNCTION = 47;

  static
  {
    contents[ER_COULDNOT_FIND_FUNCTION][1] = "No se ha podido encontrar la funci\u00f3n: {0}";
  }

  /** Field ER_UNSUPPORTED_ENCODING          */
  public static final int ER_UNSUPPORTED_ENCODING = 48;

  static
  {
    contents[ER_UNSUPPORTED_ENCODING][1] = "Codificaci\u00f3n no admitida: {0}";
  }

  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;

  static
  {
    contents[ER_PROBLEM_IN_DTM_NEXTSIBLING][1] =
      "Se ha producido un error en DTM en getNextSibling... intentando restablecer.";
  }

  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;

  static
  {
    contents[ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL][1] =
      "Error del programador: no se puede escribir en EmptyNodeList.";
  }

  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;

  static
  {
    contents[ER_SETDOMFACTORY_NOT_SUPPORTED][1] =
      "XPathContext no admite setDOMFactory!";
  }

  /** Field ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 52;

  static
  {
    contents[ER_PREFIX_MUST_RESOLVE][1] =
      "El prefijo debe convertirse en un espacio de nombre: {0}";
  }

  /** Field ER_PARSE_NOT_SUPPORTED          */
  public static final int ER_PARSE_NOT_SUPPORTED = 53;

  static
  {
    contents[ER_PARSE_NOT_SUPPORTED][1] =
      "an\u00e1lisis sint\u00e1ctico (fuente InputSource source) no admitido! No se puede abri {0}";
  }

  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;

  static
  {
    contents[ER_CREATEDOCUMENT_NOT_SUPPORTED][1] =
      "createDocument no admitido en XPathContext.";
  }

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;

  static
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT][1] =
      "El atributo hijo no tiene documento propietario!";
  }

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;

  static
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT][1] =
      "El atributo hijo no tiene elemento de documento propietario!";
  }

  /** Field ER_SAX_API_NOT_HANDLED          */
  public static final int ER_SAX_API_NOT_HANDLED = 57;

  static
  {
    contents[ER_SAX_API_NOT_HANDLED][1] =
      "Caracteres SAX API no manejados por DTM!";
  }

  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;

  static
  {
    contents[ER_IGNORABLE_WHITESPACE_NOT_HANDLED][1] =
      "Espacio en blanco que puede ignorarse no procesado por DTM!";
  }

  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;

  static
  {
    contents[ER_DTM_CANNOT_HANDLE_NODES][1] =
      "DTMLiaison no puede manejar nodos de tipo {0}";
  }

  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;

  static
  {
    contents[ER_XERCES_CANNOT_HANDLE_NODES][1] =
      "DOM2Helper no puede manejar nodos de tipo {0}";
  }

  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;

  static
  {
    contents[ER_XERCES_PARSE_ERROR_DETAILS][1] =
      "Error DOM2Helper.parse: ID del sistema - {0} l\u00ednea - {1}";
  }

  /** Field ER_XERCES_PARSE_ERROR          */
  public static final int ER_XERCES_PARSE_ERROR = 62;

  static
  {
    contents[ER_XERCES_PARSE_ERROR][1] = "Error DOM2Helper.parse.";
  }

  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;

  static
  {
    contents[ER_CANT_OUTPUT_TEXT_BEFORE_DOC][1] =
      "Advertencia: no puede enviar el texto antes del elemento de documento. Se ignora...";
  }

  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;

  static
  {
    contents[ER_CANT_HAVE_MORE_THAN_ONE_ROOT][1] =
      "No puede haber m\u00e1s de una ra\u00edz en un DOM.";
  }

  /** Field ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 65;

  static
  {
    contents[ER_INVALID_UTF16_SURROGATE][1] =
      "Se ha detectado un sustituto UTF-16 no v\u00e1lido: {0} ?";
  }

  /** Field ER_OIERROR          */
  public static final int ER_OIERROR = 66;

  static
  {
    contents[ER_OIERROR][1] = "Error de entrada/salida.";
  }

  /** Field ER_CANNOT_CREATE_URL          */
  public static final int ER_CANNOT_CREATE_URL = 67;

  static
  {
    contents[ER_CANNOT_CREATE_URL][1] = "No se puede crear url para: {0}";
  }

  /** Field ER_XPATH_READOBJECT          */
  public static final int ER_XPATH_READOBJECT = 68;


  static
  {
    contents[ER_XPATH_READOBJECT][1] = "En XPath.readObject: {0}";
  }
  
  /** Field ER_XPATH_READOBJECT         */
  public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;

  static
  {
    contents[ER_FUNCTION_TOKEN_NOT_FOUND][1] =
      "No se ha encontrado el token de funci\u00f3n.";
  }
  
   /**  Argument 'localName' is null  */
  public static final int ER_ARG_LOCALNAME_NULL = 70;

  static
  {
    contents[ER_ARG_LOCALNAME_NULL][1] =
       "El argumentoArgument 'localName' es nulo.";
  }
  

   /**  Can not deal with XPath type:   */
  public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;

  static
  {
    contents[ER_CANNOT_DEAL_XPATH_TYPE][1] =
       "No puede manejar el tipo XPath: {0}";
  }
  
   /**  This NodeSet is not mutable  */
  public static final int ER_NODESET_NOT_MUTABLE = 72;

  static
  {
    contents[ER_NODESET_NOT_MUTABLE][1] =
       "Este NodeSet es inmutable.";
  }
  
   /**  This NodeSetDTM is not mutable  */
  public static final int ER_NODESETDTM_NOT_MUTABLE = 73;

  static
  {
    contents[ER_NODESETDTM_NOT_MUTABLE][1] =
       " Este NodeSetDTM es inmutable.";
  }
  
   /**  Variable not resolvable:   */
  public static final int ER_VAR_NOT_RESOLVABLE = 74;

  static
  {
    contents[ER_VAR_NOT_RESOLVABLE][1] =
        "Variable no convertible: {0}";
  }
  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 75;

  static
  {
    contents[ER_NULL_ERROR_HANDLER][1] =
        "Manejador de errores nulo.";
  }
  
   /**  Programmer's assertion: unknown opcode  */
  public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;

  static
  {
    contents[ER_PROG_ASSERT_UNKNOWN_OPCODE][1] =
       "Aserto del programador: c\u00f3digo de operaci\u00f3n desconocido: {0}";
  }
  
   /**  0 or 1   */
  public static final int ER_ZERO_OR_ONE = 77;

  static
  {
    contents[ER_ZERO_OR_ONE][1] =
       "0 \u00f3r 1";
  }
  
   /**  2 or 3   */
  public static final int ER_TWO_OR_THREE = 78;

  static
  {
    contents[ER_TWO_OR_THREE][1] =
       "0 \u00f3 1";
  }
  
  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;

  static
  {
    contents[ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER][1] =
       "rtf() no admitido por XRTreeFragSelectWrapper";
  }
  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;

  static
  {

    contents[ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER][1] =
       "asNodeIterator() no admitido por XRTreeFragSelectWrapper";
  }
  
   /**  fsb() not supported for XStringForChars   */
  public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;

  static
  {
    contents[ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS][1] =
       "fsb() no admitido para XStringForChars";
  }
  
   /**  Could not find variable with the name of   */
  public static final int ER_COULD_NOT_FIND_VAR = 81;

  static
  {
    contents[ER_COULD_NOT_FIND_VAR][1] =
      "No se ha podido encontrar variable con el nombre {0}";
  }
  
   /**  XStringForChars can not take a string for an argument   */
  public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;

  static
  {
    contents[ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING][1] =
      "XStringForChars no puede utilizar una cadena para un argumento";
  }
  
   /**  The FastStringBuffer argument can not be null   */
  public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;

  static
  {
    contents[ER_FASTSTRINGBUFFER_CANNOT_BE_NULL][1] =
      "El argumento FastStringBuffer no puede ser nulo";
  }  
  


  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;

  static
  {
    contents[WG_LOCALE_NAME_NOT_HANDLED + MAX_CODE][1] =
      "No se ha manejado todav\u00eda el nombre locale en la funci\u00f3n!";
  }

  /** Field WG_PROPERTY_NOT_SUPPORTED          */

  public static final int WG_PROPERTY_NOT_SUPPORTED = 2;

  static
  {
    contents[WG_PROPERTY_NOT_SUPPORTED + MAX_CODE][1] =
      "Propiedad XSL no admitida: {0}";
  }

  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;

  static
  {
    contents[WG_DONT_DO_ANYTHING_WITH_NS + MAX_CODE][1] =
      "Actualmente el espacio de nombres {0} en propiedad debe dejarse como est\u00e1: {1}";
  }

  /** Field WG_SECURITY_EXCEPTION          */
  public static final int WG_SECURITY_EXCEPTION = 4;

  static
  {
    contents[WG_SECURITY_EXCEPTION + MAX_CODE][1] =
      "SecurityException al intentar tener acceso a la propiedad del sistema XSL: {0}";
  }

  /** Field WG_QUO_NO_LONGER_DEFINED          */
  public static final int WG_QUO_NO_LONGER_DEFINED = 5;

  static
  {
    contents[WG_QUO_NO_LONGER_DEFINED + MAX_CODE][1] =
      "Sintaxis antigua: quo(...) ya no viene definida enis XPath";
  }

  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */

  public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;

  static
  {
    contents[WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST + MAX_CODE][1] =
      "XPath requiere un objeto derivado para ejecutar nodeTest!";
  }

  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;

  static
  {
    contents[WG_FUNCTION_TOKEN_NOT_FOUND + MAX_CODE][1] =
      "No se ha encontrado el token de la funci\u00f3n";
  }

  /** Field WG_COULDNOT_FIND_FUNCTION          */
  public static final int WG_COULDNOT_FIND_FUNCTION = 8;

  static
  {
    contents[WG_COULDNOT_FIND_FUNCTION + MAX_CODE][1] =
      "No se ha podido encontrar la funci\u00f3n: {0}";
  }

  /** Field WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 9;

  static
  {
    contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] =
      "No se puede crear URL desde: {0}";
  }

  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;

  static
  {
    contents[WG_EXPAND_ENTITIES_NOT_SUPPORTED + MAX_CODE][1] =
      "opci\u00f3n \u0096E no admitida para analizador sint\u00e1ctico DTM";
  }

  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;

  static
  {
    contents[WG_ILLEGAL_VARIABLE_REFERENCE + MAX_CODE][1] =
      "VariableReference atribuida a una variable fuera de contexto o sin definici\u00f3n. Nombre = {0}";
  }

  /** Field WG_UNSUPPORTED_ENCODING          */
  public static final int WG_UNSUPPORTED_ENCODING = 12;

  static
  {
    contents[ER_UNSUPPORTED_ENCODING][1] = "Codificaci\u00f3n no admitida: {0}";
  }

  // Other miscellaneous text used inside the code...
  static
  {
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "es";
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "es";
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "es";
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] =
      "El par\u00e1metro para createMessage estaba fuera de los l\u00edmites";
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] =
      "Excepci\u00f3n generada la llamada messageFormat";
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Versi\u00f3n Xalan ";
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "s\u00ed";
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "L\u00ednea ##";
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "Columna ##";
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: hecho";
    contents[MAX_MESSAGES + 11][0] = "xpath_option";
    contents[MAX_MESSAGES + 11][1] = "opciones xpath: ";
    contents[MAX_MESSAGES + 12][0] = "optionIN";
    contents[MAX_MESSAGES + 12][1] = "   [-in inputXMLURL]";
    contents[MAX_MESSAGES + 13][0] = "optionSelect";
    contents[MAX_MESSAGES + 13][1] = "   [- seleccionar expresi\u00f3n xpath]";
    contents[MAX_MESSAGES + 14][0] = "optionMatch";
    contents[MAX_MESSAGES + 14][1] =
      "   [-match coincidir patr\u00f3n de b\u00fasqueda (para diagn\u00f3sticos de b\u00fasqueda)]";
    contents[MAX_MESSAGES + 15][0] = "optionAnyExpr";
    contents[MAX_MESSAGES + 15][1] =
      "O una expresi\u00f3n xpath realizar\u00e1 un volcado de diagn\u00f3stico";
    contents[MAX_MESSAGES + 16][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 16][1] = "Ha fallado el proceso XSLl";
    contents[MAX_MESSAGES + 17][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 17][1] = "** No se ha podido encontrar analizador sint\u00e1ctico **";
    contents[MAX_MESSAGES + 18][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 18][1] = "Compruebe el classpath";
    contents[MAX_MESSAGES + 19][0] = "noParsermsg4";
    contents[MAX_MESSAGES + 19][1] =
      "Si no tiene el analizador sint\u00e1ctico XML para Java de IBM, puede descargarlo desde";
    contents[MAX_MESSAGES + 20][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 20][1] =
      "AlphaWorks de IBM: http://www.alphaworks.ibm.com/formula/xml";
  }

  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE          */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED          */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** Field ERROR_RESOURCES          */
  public static final String ERROR_RESOURCES =

    "org.apache.xpath.res.XPATHErrorResources";


  /** Field ERROR_STRING          */
  public static final String ERROR_STRING = "#error";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "Error: ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "gAdvertencia: ";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "PATR\u00d3N ";

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
