/*
 * @(#)XSLTErrorResources_es.java	1.6 02/03/26
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
public class XSLTErrorResources_es extends XSLTErrorResources
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
  //  public static final int ERROR0000 = 0;

  {
    "ERROR0000", "{0}"},

  /** ER_NO_CURLYBRACE          */
  //public static final int ER_NO_CURLYBRACE = 1;

  {
    ER_NO_CURLYBRACE,
      "Error: No se puede incluir '{' en una expresi\u00f3n"},

  /** ER_ILLEGAL_ATTRIBUTE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE = 2;

  {
    ER_ILLEGAL_ATTRIBUTE, "{0} tiene un atributo no permitido: {1}"},

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
	  //public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;

  {
    ER_NULL_SOURCENODE_APPLYIMPORTS,
      "sourceNode es nulo en xsl:apply-imports."},

  /** ER_CANNOT_ADD          */
  //public static final int ER_CANNOT_ADD = 4;

  {
    ER_CANNOT_ADD, "No se puede a\u00f1adir {0} a {1}"},

  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  //public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;

  {
    ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "sourceNode es nulo en handleApplyTemplatesInstruction."},

  /** ER_NO_NAME_ATTRIB          */
  //public static final int ER_NO_NAME_ATTRIB = 6;

  {
    ER_NO_NAME_ATTRIB, "{0} debe tener un atributo de nombre."},

  /** ER_TEMPLATE_NOT_FOUND          */
  //public static final int ER_TEMPLATE_NOT_FOUND = 7;

  {
    ER_TEMPLATE_NOT_FOUND, "No se ha encontrado ninguna plantilla con el nombre: {0}"},

  /** ER_CANT_RESOLVE_NAME_AVT          */
  //public static final int ER_CANT_RESOLVE_NAME_AVT = 8;

  {
    ER_CANT_RESOLVE_NAME_AVT,
      "No se ha podido convertir el nombre AVT en xsl:call-template."},

  /** ER_REQUIRES_ATTRIB          */
  //public static final int ER_REQUIRES_ATTRIB = 9;

  {
    ER_REQUIRES_ATTRIB, "{0} necesita un atributo: {1}"},

  /** ER_MUST_HAVE_TEST_ATTRIB          */
  //public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;

  {
    ER_MUST_HAVE_TEST_ATTRIB,
      "{0} debe tener un atributo ''test''."},

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  //public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;

  {
    ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "Valor err\u00f3neo en un atributo de nivel: {0}"},

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;

  {
    ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "el nombre de la instrucci\u00f3n de procesamiento no puede ser 'xml'"},

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;

  {
    ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "el nombre de la instrucci\u00f3n de procesamiento debe ser un NCName v\u00e1lido: {0}"},

  /** ER_NEED_MATCH_ATTRIB          */
  //public static final int ER_NEED_MATCH_ATTRIB = 14;

  {
    ER_NEED_MATCH_ATTRIB,
      "{0} debe tener un atributo de b\u00fasqueda si tiene un modo."},

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  //public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;

  {
    ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} necesita un atributo de nombre o de b\u00fasqueda."},

  /** ER_CANT_RESOLVE_NSPREFIX          */
  //public static final int ER_CANT_RESOLVE_NSPREFIX = 16;

  {
    ER_CANT_RESOLVE_NSPREFIX,
      "No se puede convertir el prefijo de espacio de nombre: {0}"},

  /** ER_ILLEGAL_VALUE          */
  //public static final int ER_ILLEGAL_VALUE = 17;

  {
    ER_ILLEGAL_VALUE, "xml:space tiene un valor no permitido: {0}"},

  /** ER_NO_OWNERDOC          */
  //public static final int ER_NO_OWNERDOC = 18;

  {
    ER_NO_OWNERDOC,
      "El nodo hijo no tiene documento propietario."},

  /** ER_ELEMTEMPLATEELEM_ERR          */
  //public static final int ER_ELEMTEMPLATEELEM_ERR = 19;

  {
    ER_ELEMTEMPLATEELEM_ERR, "Error ElemTemplateElement: {0}"},

  /** ER_NULL_CHILD          */
  //public static final int ER_NULL_CHILD = 20;

  {
    ER_NULL_CHILD, "Intentando agregar un hijo nulo."},

  /** ER_NEED_SELECT_ATTRIB          */
  //public static final int ER_NEED_SELECT_ATTRIB = 21;

  {
    ER_NEED_SELECT_ATTRIB, "{0} necesita un atributo de selecci\u00f3n."},

  /** ER_NEED_TEST_ATTRIB          */

  //public static final int ER_NEED_TEST_ATTRIB = 22;

  {
    ER_NEED_TEST_ATTRIB,
      "xsl:when debe tener un atributo 'test'."},

  /** ER_NEED_NAME_ATTRIB          */
  //public static final int ER_NEED_NAME_ATTRIB = 23;

  {
    ER_NEED_NAME_ATTRIB,
      "xsl:with-param debe tener un atributo 'name'."},

  /** ER_NO_CONTEXT_OWNERDOC          */
  //public static final int ER_NO_CONTEXT_OWNERDOC = 24;

  {
    ER_NO_CONTEXT_OWNERDOC,
      "el contexto no tiene documento propietario."},

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  //public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;

  {
    ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "No se ha podido crear un v\u00ednculo XML TransformerFactory: {0}"},

  /** ER_PROCESS_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;

  {
    ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: El proceso ha fallado."},

  /** ER_NOT_SUCCESSFUL          */
  //public static final int ER_NOT_SUCCESSFUL = 27;

  {
    ER_NOT_SUCCESSFUL, "Xalan: ha fallado."},

  /** ER_ENCODING_NOT_SUPPORTED          */
  //public static final int ER_ENCODING_NOT_SUPPORTED = 28;

  {
    ER_ENCODING_NOT_SUPPORTED, "Codificaci\u00f3n no admitida: {0}"},

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  //public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;

  {
    ER_COULD_NOT_CREATE_TRACELISTENER,
      "No se ha podido crear TraceListener: {0}"},

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;

  {
    ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key necesita un atributo 'name'."},

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;


  {
    ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key necesita un atributo 'match'."},

  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  //public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;

  {
    ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key necesita un atributo 'use'."},

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  //public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;

  {
    ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} necesita un atributo ''elements''."},

  /** ER_MISSING_PREFIX_ATTRIB          */
  //public static final int ER_MISSING_PREFIX_ATTRIB = 34;

  {
    ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} falta el atributo ''prefix''"},

  /** ER_BAD_STYLESHEET_URL          */
  //public static final int ER_BAD_STYLESHEET_URL = 35;

  {
    ER_BAD_STYLESHEET_URL, "El URL de la hoja de estilo es err\u00f3neo: {0}"},

  /** ER_FILE_NOT_FOUND          */
  //public static final int ER_FILE_NOT_FOUND = 36;

  {
    ER_FILE_NOT_FOUND, "No se ha encontrado el archivo de la hoja de estilo: {0}"},

  /** ER_IOEXCEPTION          */
  //public static final int ER_IOEXCEPTION = 37;

  {
    ER_IOEXCEPTION,
      "Ten\u00eda una excepci\u00f3n E/S en el archivo de la hoja de estilo: {0}"},

  /** ER_NO_HREF_ATTRIB          */
  //public static final int ER_NO_HREF_ATTRIB = 38;

  {
    ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) No se ha encontrado el atributo href para {0}"},

  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  //public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;

  {
    ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} se incluye a s\u00ed mismo directa o indirectamente."},

  /** ER_PROCESSINCLUDE_ERROR          */
  //public static final int ER_PROCESSINCLUDE_ERROR = 40;

  {
    ER_PROCESSINCLUDE_ERROR,
      "Error StylesheetHandler.processInclude, {0}"},

  /** ER_MISSING_LANG_ATTRIB          */
  //public static final int ER_MISSING_LANG_ATTRIB = 41;

  {
    ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} falta el atributo ''lang''"},

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  //public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;

  {
    ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) \u00bfelemento {0} mal colocado? Falta el elemento ''component'' del contenedor"},

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  //public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;

  {
    ER_CAN_ONLY_OUTPUT_TO_ELEMENT,

      "S\u00f3lo puede enviarse a Element, DocumentFragment, Document o PrintWriter."},

  /** ER_PROCESS_ERROR          */
  //public static final int ER_PROCESS_ERROR = 44;

  {
    ER_PROCESS_ERROR, "Error StylesheetRoot.process"},

  /** ER_UNIMPLNODE_ERROR          */
  //public static final int ER_UNIMPLNODE_ERROR = 45;

  {
    ER_UNIMPLNODE_ERROR, "Error UnImplNode: {0}"},

  /** ER_NO_SELECT_EXPRESSION          */
  //public static final int ER_NO_SELECT_EXPRESSION = 46;

  {
    ER_NO_SELECT_EXPRESSION,
      "Error. No se ha encontrado la expresi\u00f3n de selecci\u00f3n xpath (-seleccionar)."},

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  //public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;

  {
    ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "No se puede serializar un XSLProcessor."},

  /** ER_NO_INPUT_STYLESHEET          */
  //public static final int ER_NO_INPUT_STYLESHEET = 48;

  {
    ER_NO_INPUT_STYLESHEET,
      "No se ha especificado la entrada de la hoja de estilo."},

  /** ER_FAILED_PROCESS_STYLESHEET          */
  //public static final int ER_FAILED_PROCESS_STYLESHEET = 49;

  {
    ER_FAILED_PROCESS_STYLESHEET,
      "No se ha podido procesar la hoja de estilo."},

  /** ER_COULDNT_PARSE_DOC          */
  //public static final int ER_COULDNT_PARSE_DOC = 50;

  {
    ER_COULDNT_PARSE_DOC, "No se ha podido analizar sint\u00e1cticamente el documento {0}."},

  /** ER_COULDNT_FIND_FRAGMENT          */
  //public static final int ER_COULDNT_FIND_FRAGMENT = 51;

  {
    ER_COULDNT_FIND_FRAGMENT, "No se ha encontrado el fragmento: {0}"},

  /** ER_NODE_NOT_ELEMENT          */
  //public static final int ER_NODE_NOT_ELEMENT = 52;

  {
    ER_NODE_NOT_ELEMENT,
      "El nodo se\u00f1alado por el identificador de fragmento no era un elemento: {0}"},

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;

  {
    ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each debe tener un atributo de b\u00fasqueda o de nombre"},

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  //public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;

  {
    ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "las plantillas deben tener un atributo de b\u00fasqueda o de nombre"},

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  //public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;

  {
    ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "No existe clon de un fragmento de un documento"},

  /** ER_CANT_CREATE_ITEM          */
  //public static final int ER_CANT_CREATE_ITEM = 56;

  {
    ER_CANT_CREATE_ITEM,
      "No se puede crear el elemento en el \u00e1rbol de resultados: {0}"},

  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  //public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;

  {
    ER_XMLSPACE_ILLEGAL_VALUE,
      "xml:space en el XML fuente tiene un valor no permitido: {0}"},

  /** ER_NO_XSLKEY_DECLARATION          */
  //public static final int ER_NO_XSLKEY_DECLARATION = 58;

  {
    ER_NO_XSLKEY_DECLARATION,
      "No existe ninguna declaraci\u00f3n xsl:key para {0}."},

  /** ER_CANT_CREATE_URL          */
  //public static final int ER_CANT_CREATE_URL = 59;

  {
    ER_CANT_CREATE_URL, "Error. No se puede crear el url para: {0}"},

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  //public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;

  {
    ER_XSLFUNCTIONS_UNSUPPORTED, "xsl:functions no se admite"},

  /** ER_PROCESSOR_ERROR          */
  //public static final int ER_PROCESSOR_ERROR = 61;

  {
    ER_PROCESSOR_ERROR, "Error XSLT TransformerFactory"},

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  //public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;

  {
    ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} no se permite en una hoja de estilo."},

  /** ER_RESULTNS_NOT_SUPPORTED          */
  //public static final int ER_RESULTNS_NOT_SUPPORTED = 63;

  {
    ER_RESULTNS_NOT_SUPPORTED,
      "result-ns ya no se utiliza.  Utilizar en su lugar xsl:output"},

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  //public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;

  {
    ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space ya no se utiliza.  Utilizar en su lugar xsl:strip-space o xsl:preserve-space"},

  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  //public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;

  {
    ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result ya no se utiliza. Utilizar en su lugar xsl:output"},

  /** ER_ILLEGAL_ATTRIB          */
  //public static final int ER_ILLEGAL_ATTRIB = 66;

  {
    ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} tiene un atributo no permitido: {1}"},

  /** ER_UNKNOWN_XSL_ELEM          */
  //public static final int ER_UNKNOWN_XSL_ELEM = 67;

  {
    ER_UNKNOWN_XSL_ELEM, "Elemento XSL desconocido: {0}"},

  /** ER_BAD_XSLSORT_USE          */
  //public static final int ER_BAD_XSLSORT_USE = 68;

  {
    ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort s\u00f3lo puede utilizarse con xsl:apply-templates o con xsl:for-each."},

  /** ER_MISPLACED_XSLWHEN          */
  //public static final int ER_MISPLACED_XSLWHEN = 69;

  {
    ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) xsl:when mal colocado."},

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;

  {
    ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when no es hijo de xsl:choose."},

  /** ER_MISPLACED_XSLOTHERWISE          */
  //public static final int ER_MISPLACED_XSLOTHERWISE = 71;

  {
    ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) xsl:otherwise mal colocado."},

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  //public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;

  {
    ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise no es hijo de xsl:choose."},

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  //public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;

  {
    ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} no se permite en una plantilla."},

  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  //public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;

  {
    ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} prejijo de espacio de nombre de extensi\u00f3n {1} desconocido"},

  /** ER_IMPORTS_AS_FIRST_ELEM          */
  //public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;

  {
    ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Las importaciones s\u00f3lo pueden ser los primeros elementos de la hoja de estilo."},

  /** ER_IMPORTING_ITSELF          */
  //public static final int ER_IMPORTING_ITSELF = 76;

  {
    ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} se importa a s\u00ed mismo directa o indirectamente."},

  /** ER_XMLSPACE_ILLEGAL_VAL          */
  //public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;

  {
    ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) " + "xml:space tiene un valor no permitido: {0}"},


  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  //public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;

  {
    ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet ha fallado."},

  /** ER_SAX_EXCEPTION          */
  //public static final int ER_SAX_EXCEPTION = 79;

  {
    ER_SAX_EXCEPTION, "Excepci\u00f3n SAX"},

  /** ER_FUNCTION_NOT_SUPPORTED          */
  //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;

  {
    ER_FUNCTION_NOT_SUPPORTED, "Funci\u00f3n no admitida"},

  /** ER_XSLT_ERROR          */
  //public static final int ER_XSLT_ERROR = 81;

  {
    ER_XSLT_ERROR, "Error XSLT"},

  /** ER_CURRENCY_SIGN_ILLEGAL          */
  //public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;

  {
    ER_CURRENCY_SIGN_ILLEGAL,
      "el signo de divisa no se permite en la cadena de patrones de formato"},

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  //public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;

  {
    ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "La hoja de estilo DOM no admite la funci\u00f3n de documento."},

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  //public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;

  {
    ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "No se puede convertir el prefijo del convertidor de ausencia de prefijo."},

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  //public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;

  {
    ER_REDIRECT_COULDNT_GET_FILENAME,
      "Reencaminar extensi\u00f3n : No se ha podido obtener el nombre del archivo - el atributo de archivo o de selecci\u00f3n debe presentar una cadena v\u00e1lida."},

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  //public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;

  {
    ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "No se puede crear FormatterListener en extensi\u00f3n Redirect."},

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  //public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;

  {
    ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "El prefijo de exclude-result-prefixes no es v\u00e1lido: {0}"},

  /** ER_MISSING_NS_URI          */
  //public static final int ER_MISSING_NS_URI = 88;

  {
    ER_MISSING_NS_URI,
      "Falta el URI de espacio de nombre del prefijo especificado"},

  /** ER_MISSING_ARG_FOR_OPTION          */
  //public static final int ER_MISSING_ARG_FOR_OPTION = 89;

  {
    ER_MISSING_ARG_FOR_OPTION,
      "Falta el argumento en la opci\u00f3n: {0}"},

  /** ER_INVALID_OPTION          */
  //public static final int ER_INVALID_OPTION = 90;

  {
    ER_INVALID_OPTION, "Opci\u00f3n no v\u00e1lida: {0}"},

  /** ER_MALFORMED_FORMAT_STRING          */
  //public static final int ER_MALFORMED_FORMAT_STRING = 91;

  {
    ER_MALFORMED_FORMAT_STRING, "Cadena de formato mal construida: {0}"},

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;

  {
    ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet necesita un atributo 'version'."},

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;

  {
    ER_ILLEGAL_ATTRIBUTE_VALUE,
      "El atributo: {0} tiene un valor no permitido: {1}"},

  /** ER_CHOOSE_REQUIRES_WHEN          */
  //public static final int ER_CHOOSE_REQUIRES_WHEN = 94;

  {
    ER_CHOOSE_REQUIRES_WHEN, "xsl:choose requiere xsl:when"},

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  //public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;

  {
    ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports no se permite en xsl:for-each"},

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;

  {
    ER_CANT_USE_DTM_FOR_OUTPUT,
      "No se puede utilizar DTMLiaison con un nodo DOM de salida... utilizar en su lugar org.apache.xpath.DOM2Helper."},

  /** ER_CANT_USE_DTM_FOR_INPUT          */
  //public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;


  {
    ER_CANT_USE_DTM_FOR_INPUT,
      "No se puede utilizar DTMLiaison con un nodo DOM de salida... utilizar en su lugar org.apache.xpath.DOM2Helper."},

  /** ER_CALL_TO_EXT_FAILED          */
  //public static final int ER_CALL_TO_EXT_FAILED = 98;

  {
    ER_CALL_TO_EXT_FAILED,
      "Ha fallado el elemento de llamada a la extensi\u00f3n: {0}"},

  /** ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 99;

  {
    ER_PREFIX_MUST_RESOLVE,
      "El prefijo debe convertir un espacio de nombre: {0}"},

  /** ER_INVALID_UTF16_SURROGATE          */
  //public static final int ER_INVALID_UTF16_SURROGATE = 100;

  {
    ER_INVALID_UTF16_SURROGATE,
      "Se ha detectado un sustituto de UTF-16 no v\u00e1lido: {0} ?"},

  /** ER_XSLATTRSET_USED_ITSELF          */
  //public static final int ER_XSLATTRSET_USED_ITSELF = 101;

  {
    ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} se utiliza a s\u00ed mismo y provocar\u00e1 un bucle sin fin."},

  /** ER_CANNOT_MIX_XERCESDOM          */
  //public static final int ER_CANNOT_MIX_XERCESDOM = 102;

  {
    ER_CANNOT_MIX_XERCESDOM,
      "No se puede mezclar una entrada no Xerces-DOM con una salida Xerces-DOM."},

  /** ER_TOO_MANY_LISTENERS          */
  //public static final int ER_TOO_MANY_LISTENERS = 103;

  {
    ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  //public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;

  {
    ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "En ElemTemplateElement.readObject: {0}"},


  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  //public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;

  {
    ER_DUPLICATE_NAMED_TEMPLATE,
      "Se ha encontrado m\u00e1s de una plantilla con el nombre: {0}"},

  /** ER_INVALID_KEY_CALL          */
  //public static final int ER_INVALID_KEY_CALL = 106;

  {
    ER_INVALID_KEY_CALL,
      "Llamada a funci\u00f3n no v\u00e1lida: no se permiten las llamadas con clave recursiva()"},
  
  /** Variable is referencing itself          */
  //public static final int ER_REFERENCING_ITSELF = 107;

  {
    ER_REFERENCING_ITSELF,
      "La variable {0} se refiere a s\u00ed misma directa o indirectamente."},
  
  /** Illegal DOMSource input          */
  //public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

  {
    ER_ILLEGAL_DOMSOURCE_INPUT,
      "El nodo de entrada no puede ser nulo para DOMSource en newTemplates."},
	
	/** Class not found for option         */
  //public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

  {
    ER_CLASS_NOT_FOUND_FOR_OPTION,
			"No se ha encontrado el archivo de clase para la opci\u00f3n {0}"},
	
	/** Required Element not found         */
  //public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  {
    ER_REQUIRED_ELEM_NOT_FOUND,
			"No se ha encontrado el elemento requerido: {0}"},
  
  /** InputStream cannot be null         */
  //public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  {
    ER_INPUT_CANNOT_BE_NULL,
			"InputStream no puede ser nulo"},
  
  /** URI cannot be null         */
  //public static final int ER_URI_CANNOT_BE_NULL = 112;

  {
    ER_URI_CANNOT_BE_NULL,
			"URI no puede ser nulo"},
  
  /** File cannot be null         */
  //public static final int ER_FILE_CANNOT_BE_NULL = 113;

  {
    ER_FILE_CANNOT_BE_NULL,
			"El archivo no puede ser nulo"},
  
   /** InputSource cannot be null         */
  //public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  {
    ER_SOURCE_CANNOT_BE_NULL,
			"InputSource no puede ser nulo"},
  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"No se puede sobrescribir la causa"},
  
  /** Could not initialize BSF Manager        */
  //public static final int ER_CANNOT_INIT_BSFMGR = 116;

  {
    ER_CANNOT_INIT_BSFMGR,
			"No se ha podido inicializar el administrador de BSF"},
  
  /** Could not compile extension       */
  //public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  {
    ER_CANNOT_CMPL_EXTENSN,
			"No se ha podido compilar la extensi\u00f3n"},
  

  /** Could not create extension       */
  //public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  {
    ER_CANNOT_CREATE_EXTENSN,
      "No se ha podido crear la extensi\u00f3n: {0} debido a: {1}"},
  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  //public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  {
    ER_INSTANCE_MTHD_CALL_REQUIRES,
      "El primer argumento de la llamada del m\u00e9todo de instancia al m\u00e9todo {0} necesita una instancia de objeto"},
  
  /** Invalid element name specified       */
  //public static final int ER_INVALID_ELEMENT_NAME = 120;

  {
    ER_INVALID_ELEMENT_NAME,
      "Se ha especificado un nombre de elemento no v\u00e1lido {0}"},
  
   /** Element name method must be static      */
  //public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  {
    ER_ELEMENT_NAME_METHOD_STATIC,
      "El m\u00e9todo del nombre de elemento debe ser est\u00e1tico {0}"},
  
   /** Extension function {0} : {1} is unknown      */
  //public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  {
    ER_EXTENSION_FUNC_UNKNOWN,
             "La funci\u00f3n de extensi\u00f3n {0} : {1} es desconocida"},
  
   /** More than one best match for constructor for       */
  //public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  {
    ER_MORE_MATCH_CONSTRUCTOR,
             "Hay m\u00e1s de una coincidencia \u00f3ptima para el creador en {0}"},
  
   /** More than one best match for method      */
  //public static final int ER_MORE_MATCH_METHOD = 124;

  {
    ER_MORE_MATCH_METHOD,
             "Hay m\u00e1s de una coincidencia \u00f3ptima para el m\u00e9todo {0}"},
  
   /** More than one best match for element method      */
  //public static final int ER_MORE_MATCH_ELEMENT = 125;

  {
    ER_MORE_MATCH_ELEMENT,
             "Hay m\u00e1s de una coincidencia \u00f3ptima para el m\u00e9todo del elemento {0}"},
  
   /** Invalid context passed to evaluate       */
  //public static final int ER_INVALID_CONTEXT_PASSED = 126;

  {
    ER_INVALID_CONTEXT_PASSED,
             "El contexto no v\u00e1lido se ha pasado a evaluaci\u00f3n {0}"},
  
   /** Pool already exists       */
  //public static final int ER_POOL_EXISTS = 127;

  {
    ER_POOL_EXISTS,
             "El pool ya existe"},
  
   /** No driver Name specified      */
  //public static final int ER_NO_DRIVER_NAME = 128;

  {
    ER_NO_DRIVER_NAME,
             "No se ha especificado ning\u00fan nombre para el dispositivo"},
  
   /** No URL specified     */
  //public static final int ER_NO_URL = 129;

  {
    ER_NO_URL,
             "No se ha especificado ning\u00fan URL"},
  
   /** Pool size is less than one    */
  //public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  {
    ER_POOL_SIZE_LESSTHAN_ONE,
             "El tama\u00f1o del pool es menor que uno."},
  
   /** Invalid driver name specified    */
  //public static final int ER_INVALID_DRIVER = 131;

  {
    ER_INVALID_DRIVER,
             "Se ha especificado un nombre de dispositivo no v\u00e1lido."},
  
   /** Did not find the stylesheet root    */
  //public static final int ER_NO_STYLESHEETROOT = 132;

  {
    ER_NO_STYLESHEETROOT,
             "No se ha encontrado la ra\u00edz de la hoja de estilo."},
  
   /** Illegal value for xml:space     */
  //public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  {
    ER_ILLEGAL_XMLSPACE_VALUE,
         "Valor no permitido para xml:space"},
  
   /** processFromNode failed     */
  //public static final int ER_PROCESSFROMNODE_FAILED = 134;

  {
    ER_PROCESSFROMNODE_FAILED,
         "Fallo de processFromNode"},
  
   /** The resource [] could not load:     */
  //public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

  {
    ER_RESOURCE_COULD_NOT_LOAD,
        "El recurso [ {0} ] no ha podido cargar: {1} \n {2} \t {3}"},
   
  
   /** Buffer size <=0     */
  //public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  {
    ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Tama\u00f1o del b\u00fafer <=0"},
  
   /** Unknown error when calling extension    */
  //public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  {
    ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "Error desconocido al llamar a la extensi\u00f3n"},
  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  //public static final int ER_NO_NAMESPACE_DECL = 138;

  {
    ER_NO_NAMESPACE_DECL,
        "El prefijo {0} no tiene la declaraci\u00f3n de espacio de nombre correspondiente"},
  
   /** Element content not allowed for lang=javaclass   */
  //public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  {
    ER_ELEM_CONTENT_NOT_ALLOWED,
        "El contenido del elemento no est\u00e1 permitido para lang=javaclass {0}"},
  
   /** Stylesheet directed termination   */
  //public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  {
    ER_STYLESHEET_DIRECTED_TERMINATION,
        "Terminaci\u00f3n dirigida a la hoja de estilo"},
  
   /** 1 or 2   */
  //public static final int ER_ONE_OR_TWO = 141;

  {
    ER_ONE_OR_TWO,
        "1 \u00f3 2"},
  
   /** 2 or 3   */
  //public static final int ER_TWO_OR_THREE = 142;

  {
    ER_TWO_OR_THREE,
        "2 \u00f3 3"},

  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  //public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  {
    ER_COULD_NOT_LOAD_RESOURCE,
        "No se ha podido cargar {0} (comprobar CLASSPATH), el sistema est\u00e1 utilizando los valores predeterminados"},
  
   /** Cannot initialize default templates   */
  //public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  {
    ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "No se puede inicializar las plantillas predeterminadas"},
  
   /** Result should not be null   */
  //public static final int ER_RESULT_NULL = 145;

  {
    ER_RESULT_NULL,
        "El resultado no debe ser nulo"},
    
   /** Result could not be set   */
  //public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  {
    ER_RESULT_COULD_NOT_BE_SET,
        "No ha podido establecerse el resultado"},
  
   /** No output specified   */
  //public static final int ER_NO_OUTPUT_SPECIFIED = 147;


  {
    ER_NO_OUTPUT_SPECIFIED,
        "No se ha especificado ninguna salida"},
  
   /** Can't transform to a Result of type   */
  //public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  {
    ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "No se puede transformar en un resultado del tipo {0}"},
  
   /** Can't transform to a Source of type   */
  //public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  {
    ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "No se puede transformar una fuente del tipo {0}"},
  
   /** Null content handler  */
  //public static final int ER_NULL_CONTENT_HANDLER = 150;

  {
    ER_NULL_CONTENT_HANDLER,
        "Manejador de contenido nulo"},
  
   /** Null error handler  */
  //public static final int ER_NULL_ERROR_HANDLER = 151;

  {
    ER_NULL_ERROR_HANDLER,
        "Manejador de errores nulo"},
  
   /** parse can not be called if the ContentHandler has not been set */
  //public static final int ER_CANNOT_CALL_PARSE = 152;

  {
    ER_CANNOT_CALL_PARSE,
        "no puede invocarse el analizador sint\u00e1ctico si no se ha establecido el ContentHandler"},
  
   /**  No parent for filter */
  //public static final int ER_NO_PARENT_FOR_FILTER = 153;

  {
    ER_NO_PARENT_FOR_FILTER,
        "No existe ning\u00fan elemento padre para el filtro"},
  
  
   /**  No stylesheet found in: {0}, media */
  //public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  {
    ER_NO_STYLESHEET_IN_MEDIA,
         "No se ha encontrado ninguna hoja de estilo en: {0}, media= {1}"},
  
   /**  No xml-stylesheet PI found in */
  //public static final int ER_NO_STYLESHEET_PI = 155;

  {
    ER_NO_STYLESHEET_PI,
         "No se ha encontrado xml-stylesheet PI en: {0}"},
  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;

  {
    ER_NO_DEFAULT_IMPL,
         "No se ha encontrado ninguna implementaci\u00f3n predeterminada "},
  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0}) no se utiliza actualmente"},
  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "La desviaci\u00f3n es mayor que el intervalo"},
  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;

  {
    ER_COROUTINE_NOT_AVAIL,
       "Corrutina no disponible, id={0}"},
  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;

  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager ha recibido una solicitud co_exit()"},
  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;

  {
    ER_COJOINROUTINESET_FAILED,
       "Fallo co_joinCoroutineSet()"},
  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;

  {
    ER_COROUTINE_PARAM,
       "Error de par\u00e1metro de corrutina({0})"},
  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: el analizador sint\u00e1ctico doTerminate responde {0}"},
  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "no puede invocarse el analizador sint\u00e1ctico con un an\u00e1lisis sint\u00e1ctico en curso"},
  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Error: El iterador introducido para el eje  {0} no est\u00e1 implementado"},
  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "Error: el iterador para el eje {0} no est\u00e1 implementado "},
  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "no se admite clon del iterador"},
  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  {
    ER_UNKNOWN_AXIS_TYPE,
       "El tipo de eje transversal es desconocido: {0}"},
  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;

  {
    ER_AXIS_NOT_SUPPORTED,
       "No se admite traverser de eje: {0}"},
  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;

  {
    ER_NO_DTMIDS_AVAIL,
       "No hay m\u00e1s Id de DTM disponibles"},
  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;

  {
    ER_NOT_SUPPORTED,
       "No se admite: {0}"},
  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;

  {
    ER_NODE_NON_NULL,
       "El nodo no puede ser nulo para getDTMHandleFromNode"},
  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  {
    ER_COULD_NOT_RESOLVE_NODE,
       "No se ha podido convertir el nodo en un manejador"},
  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  {
    ER_STARTPARSE_WHILE_PARSING,
       "no se puede invocar startParse con un an\u00e1lisis sint\u00e1ctico en curso"},
  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse no admite SAXParser nulo"},
  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;

  {
    ER_COULD_NOT_INIT_PARSER,
       "No se ha podido inicializar el analizador sint\u00e1ctico con"},
  
   /**  Value for property {0} should be a Boolean instance  */
  //public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  {
    ER_PROPERTY_VALUE_BOOLEAN,
       "El valor de propiedad {0} debe ser una instancia booleana"},
  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;

  {
    ER_EXCEPTION_CREATING_POOL,
       "se ha producido una excepci\u00f3n al crear una nueva instancia para pool"},
  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "El trayecto contiene una secuencia de escape no v\u00e1lida"},
  
   /**  Scheme is required.  */
  //public static final int ER_SCHEME_REQUIRED = 180;

  {
    ER_SCHEME_REQUIRED,
       "Se necesita un esquema."},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;

  {
    ER_NO_SCHEME_IN_URI,
       "No se ha encontrado ning\u00fan esquema en el URI: {0}"},
  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;

  {
    ER_NO_SCHEME_INURI,
       "No se ha encontrado ning\u00fan esquema en el URI"},
  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;

  {
    ER_PATH_INVALID_CHAR,
       "El trayecto contiene un car\u00e1cter no v\u00e1lido: {0}"},
  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  {
    ER_SCHEME_FROM_NULL_STRING,
       "No se puede establecer un esquema a partir de una cadena nula"},
  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  {
    ER_SCHEME_NOT_CONFORMANT,
       "El esquema no es aceptable."},
  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "El sistema central no es una direcci\u00f3n bien construida"},
  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;

  {
    ER_PORT_WHEN_HOST_NULL,
       "No puede establecerse el puerto cuando el sistema central es nulo"},
  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;

  {
    ER_INVALID_PORT,
       "N\u00famero de puerto no v\u00e1lido"},
  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  {
    ER_FRAG_FOR_GENERIC_URI,
       "S\u00f3lo puede establecerse el fragmento para un URI gen\u00e9rico"},
  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  {
    ER_FRAG_WHEN_PATH_NULL,
       "No puede establecerse el fragmento cuando el trayecto es nulo"},
  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;

  {
    ER_FRAG_INVALID_CHAR,
       "El fragmento contiene un car\u00e1cter no v\u00e1lido"},
  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;

  {
    ER_PARSER_IN_USE,
        "El analizador sint\u00e1ctico est\u00e1 en uso"},
  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "No se puede cambiar {0} {1} mientras el an\u00e1lisis sint\u00e1ctico est\u00e1 en curso"},
  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "No se permite la autocausalidad"},
  
  /* This key/message changed ,NEED ER_COULD_NOT_FIND_EXTERN_SCRIPT: Pending,Ra
mesh */
   /** src attribute not yet supported for  */
  //public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  {
    ER_COULD_NOT_FIND_EXTERN_SCRIPT,
       "No se puede obtener el script externo en {0}"},
  
  /** The resource [] could not be found     */
  //public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  {
    ER_RESOURCE_COULD_NOT_FIND,
        "No se ha encontrado el recurso [ {0} ].\n {1}"},
  
   /** output property not recognized:  */
  //public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  {
    ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "Propiedad de salida no reconocida: {0}"},
  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  {
    ER_NO_USERINFO_IF_NO_HOST,
        "La informaci\u00f3n de usuario no puede especificarse si no se especifica el sistema central"},
  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;

  {
    ER_NO_PORT_IF_NO_HOST,
        "El puerto no puede especificarse si no est\u00e1 especificado el sistema central"},
  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  {
    ER_NO_QUERY_STRING_IN_PATH,
        "La cadena de consulta no puede especificarse a la vez en el trayecto y en la cadena de consulta"},
  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "El fragmento no puede especificarse a la vez en el trayecto y en el fragmento"},
  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "No se puede inicializar el URI con par\u00e1metros vac\u00edos"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  {
    ER_FAILED_CREATING_ELEMLITRSLT,
        "Fallo de creaci\u00f3n de instancia ElemLiteralResult"},

  // Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed. - nb.

   /** Priority value does not contain a parsable number   */
  //public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;

  {
    ER_VALUE_SHOULD_BE_NUMBER,
        "El valor de {0} debe contener un n\u00famero que se pueda analizar"},

  
   /**  Value for {0} should equal 'yes' or 'no'   */
  //public static final int ER_VALUE_SHOULD_EQUAL = 205;

  {
    ER_VALUE_SHOULD_EQUAL,
        " El valor de {0} debe ser igual a s\u00ed o no"},
 
   /**  Failed calling {0} method   */
  //public static final int ER_FAILED_CALLING_METHOD = 206;

  {
    ER_FAILED_CALLING_METHOD,
        " Fallo de invocaci\u00f3n del m\u00e9todo {0}"},
  
   /** Failed creating ElemLiteralResult instance   */
  //public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  {
    ER_FAILED_CREATING_ELEMTMPL,
        "Fallo de creaci\u00f3n de instancia ElemTemplateElement"},
  
   /**  Characters are not allowed at this point in the document   */
  //public static final int ER_CHARS_NOT_ALLOWED = 208;

  {
    ER_CHARS_NOT_ALLOWED,
        "No se permiten caracteres en esta parte del documento"},
  
  /**  attribute is not allowed on the element   */
  //public static final int ER_ATTR_NOT_ALLOWED = 209;

  {
    ER_ATTR_NOT_ALLOWED,
        "el atributo \"{0}\" no se permite en el elemento {1}."},
  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;

  {
    ER_METHOD_NOT_SUPPORTED,
        "M\u00e9todo todav\u00eda no utilizado"},
 
  /**  Bad value    */
  //public static final int ER_BAD_VALUE = 211;

  {
    ER_BAD_VALUE,
     "{0} valor err\u00f3neo {1} "},
  
  /**  attribute value not found   */
  //public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  {
    ER_ATTRIB_VALUE_NOT_FOUND,
     "no se ha encontrado el valor del atributo {0}"},
  
  /**  attribute value not recognized    */
  //public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;

  {
    ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "no se reconoce el valor del atributo {0}"},

  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter no puede reiniciarse actualmente"},
  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader no antes de una solicitud startParse"},
  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NULL_URI_NAMESPACE = 216;

  {
    ER_NULL_URI_NAMESPACE,
     "Se ha intentado generar un prefijo de espacio de nombre con un URI nulo"},

  //Following are the new ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

  /** Attempting to generate a namespace prefix with a null URI   */
  //public static final int ER_NUMBER_TOO_BIG = 217;

  {
    ER_NUMBER_TOO_BIG,
     "Se ha intentado dar formato a un n\u00famero mayor que el entero Long de mayor tama\u00f1o"},

  //ER_CANNOT_FIND_SAX1_DRIVER

  //public static final int  ER_CANNOT_FIND_SAX1_DRIVER = 218;

  {
    ER_CANNOT_FIND_SAX1_DRIVER,
     "No se puede hallar la clase {0} del controlador SAX1"},

  //ER_SAX1_DRIVER_NOT_LOADED
  //public static final int  ER_SAX1_DRIVER_NOT_LOADED = 219;

  {
    ER_SAX1_DRIVER_NOT_LOADED,
     "Se ha encontrado la clase {0} del controlador SAX1 pero no se puede cargar"},

  //ER_SAX1_DRIVER_NOT_INSTANTIATED
  //public static final int  ER_SAX1_DRIVER_NOT_INSTANTIATED = 220 ;

  {
    ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "Se ha cargado la clase {0} del controlador SAX1 pero no se puede crear una instancia"},


  // ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER
  //public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 221;

  {
    ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "La clase {0} del controlador SAX1 no implementa org.xml.sax.Parser"},

  // ER_PARSER_PROPERTY_NOT_SPECIFIED
  //public static final int  ER_PARSER_PROPERTY_NOT_SPECIFIED = 222;

  {
    ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "No se ha especificado la propiedad de sistema org.xml.sax.parser"},

  //ER_PARSER_ARG_CANNOT_BE_NULL
  //public static final int  ER_PARSER_ARG_CANNOT_BE_NULL = 223 ;

  {
    ER_PARSER_ARG_CANNOT_BE_NULL,
     "El argumento del analizador sint\u00e1ctico no debe ser nulo"},


  // ER_FEATURE
  //public static final int  ER_FEATURE = 224;

  {
    ER_FEATURE,
     "Caracter\u00edstica: {0}"},


  // ER_PROPERTY
  //public static final int ER_PROPERTY = 225 ;

  {
    ER_PROPERTY,
     "Propiedad:{0}"},

  // ER_NULL_ENTITY_RESOLVER
  //public static final int ER_NULL_ENTITY_RESOLVER  = 226;

  {
    ER_NULL_ENTITY_RESOLVER,
     "Convertidor de entidad nulo"},

  // ER_NULL_DTD_HANDLER
  //public static final int  ER_NULL_DTD_HANDLER = 227 ;

  {
    ER_NULL_DTD_HANDLER,
     "Manejador DTD nulo"},

  // No Driver Name Specified!
  //public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;
  {
    ER_NO_DRIVER_NAME_SPECIFIED,
     "No se ha especificado un nombre de controlador"},


  // No URL Specified!
  //public static final int ER_NO_URL_SPECIFIED = 229;
  {
    ER_NO_URL_SPECIFIED,
     "No se ha especificado una URL"},


  // Pool size is less than 1!
  //public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;
  {
    ER_POOLSIZE_LESS_THAN_ONE,
     "\u00a1El tama\u00f1o de pool es inferior a 1!"},


  // Invalid Driver Name Specified!
  //public static final int ER_INVALID_DRIVER_NAME = 231;
  {
    ER_INVALID_DRIVER_NAME,
     "El nombre de controlador especificado no es v\u00e1lido"},



  // ErrorListener
  //public static final int ER_ERRORLISTENER = 232;
  {
    ER_ERRORLISTENER,
     "ErrorListener"},


  // Programmer's error! expr has no ElemTemplateElement parent!
  //public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;
  {
    ER_ASSERT_NO_TEMPLATE_PARENT,
     "Error de programaci\u00f3n. expr no tiene ElemTemplateElement padre!"},


  // Programmer's assertion in RundundentExprEliminator: {0}
  //public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;
  {
    ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "Confirmaci\u00f3n del programador en RundundentExprEliminator: {0}"},

  // Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "No se admite el eje transversal: {0}"},

  // ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
  {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "ListingErrorHandler creado con PrintWriter nulo"},

  // {0}is not allowed in this position in the stylesheet!
  //public static final int ER_NOT_ALLOWED_IN_POSITION = 237;
  {
    ER_NOT_ALLOWED_IN_POSITION,
     "{0} no est\u00e1 permitido en esta posici\u00f3n de la hoja de estilo"},

  // Non-whitespace text is not allowed in this position in the stylesheet!
  //public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
  {
    ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "En esta posici\u00f3n de la hoja de estilo no se admite texto; solo espacios en blanco "},

  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  //public static final int INVALID_TCHAR = 239;
  // SystemId Unknown
  {
    INVALID_TCHAR,
     "Valor no v\u00e1lido: {1} utilizado para el atributo CHAR: {0}.  Un atributo de tipo CHAR debe tener 1 solo car\u00e1cter"},

  //public static final int ER_SYSTEMID_UNKNOWN = 240;
  {
    ER_SYSTEMID_UNKNOWN,
     "Id de sistema desconocido"},

  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;
  {
    ER_LOCATION_UNKNOWN,
     "Ubicaci\u00f3n del error desconocida"},

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
     "Valor no v\u00e1lido: {1} utilizado para atributo QNAME: {0}"},

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
     "Valor no v\u00e1lido: {1} utilizado para atributo ENUM: {0}.  Los valores v\u00e1lidos son: {2}."},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NMTOKEN

  // Illegal value:a {1} used for NMTOKEN attribute:a {0}.
  //public static final int INVALID_NMTOKEN = 244;
  {
    INVALID_NMTOKEN,
     "Valor no v\u00e1lido: {1} utilizado para atributo NMTOKEN: {0}"},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
  // of the attribute, and should not be translated.The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_NCNAME

  // Illegal value:a {1} used for NCNAME attribute:a {0}.
  //public static final int INVALID_NCNAME = 245;
  {
    INVALID_NCNAME,
     "Valor no v\u00e1lido: {1} utilizado para atributo NCNAME : {0}"},

  // Note to translators:  The following message is used if the value of
  // an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
  // of the attribute, and should not be translated.  The substitution text {1} is
  // the attribute value and {0} is the attribute name.
  // INVALID_BOOLEAN

  // Illegal value:a {1} used for boolean attribute:a {0}.
  //public static final int INVALID_BOOLEAN = 246;

  {
    INVALID_BOOLEAN,
     "Valor no v\u00e1lido: {1} utilizado para atributo booleano: {0}"},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
// INVALID_NUMBER

  // Illegal value:a {1} used for number attribute:a {0}.
  //public static final int INVALID_NUMBER = 247;
  {
    INVALID_NUMBER,
     "Valor no v\u00e1lido: {1} utilizado para atributo de n\u00famero: {0}"},


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
     "El argumento de {0} en el patr\u00f3n coincidente debe ser literal."},

// Note to translators:  The following message indicates that two definitions of
// a variable.  A "global variable" is a variable that is accessible everywher
// in the stylesheet.
// ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790

  // Duplicate global variable declaration.
  //public static final int ER_DUPLICATE_GLOBAL_VAR    = 249;
  {
    ER_DUPLICATE_GLOBAL_VAR,
     "Declaraci\u00f3n de variable global duplicada."},


// Note to translators:  The following message indicates that two definitions of
// a variable were encountered.
// ER_DUPLICATE_VAR - new error message for bugzilla #790

  // Duplicate variable declaration.
  //public static final int ER_DUPLICATE_VAR           = 250;
  {
    ER_DUPLICATE_VAR,
     "Declaraci\u00f3n de variable duplicada."},

    // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
    // which must not be translated.
    // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789

  // xsl:template must have a name or match attribute (or both)
  //public static final int ER_TEMPLATE_NAME_MATCH     = 251;
  {
    ER_TEMPLATE_NAME_MATCH,
     "xsl: template debe tener un atributo name o match (o ambos)"},

    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788

  // Prefix in exclude-result-prefixes is not valid:a {0}
  //public static final int ER_INVALID_PREFIX          = 252;
  {
    ER_INVALID_PREFIX,
     "El prefijo de exclude-result-prefixes no es v\u00e1lido: {0}"},

    // Note to translators:  An "attribute set" is a set of attributes that can be
    // added to an element in the output document as a group.  The message indicates
    // that there was a reference to an attribute set named {0} that was never
    // defined.
    // ER_NO_ATTRIB_SET - new error message for bugzilla #782

  // attribute-set named {0} does not exist
  //public static final int ER_NO_ATTRIB_SET           = 253;
  {
    ER_NO_ATTRIB_SET,
     "no existe el conjunto de atributos denominado {0}"},


  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  //public static final int WG_FOUND_CURLYBRACE = 1;

  {
    WG_FOUND_CURLYBRACE,
      "Se ha encontrado '}' pero no hay abierta ninguna plantilla de atributos."},

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  //public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;

  {
    WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "Advertencia: el atributo de c\u00f3mputo no coincide con ning\u00fan antecesor en xsl:number. Objetivo = {0}"},

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  //public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;

  {
    WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "Sintaxis antigua: El nombre del atributo 'expr' se ha cambiado por 'select'."},

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  //public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;

  {
    WG_NO_LOCALE_IN_FORMATNUMBER,

      "Xalan no maneja todav\u00eda el nombre locale en la funci\u00f3n format-number."},

  /** WG_LOCALE_NOT_FOUND          */
  //public static final int WG_LOCALE_NOT_FOUND = 5;

  {
    WG_LOCALE_NOT_FOUND,
      "Advertencia: No se ha encontrado locale para xml:lang={0}"},

  /** WG_CANNOT_MAKE_URL_FROM          */
  //public static final int WG_CANNOT_MAKE_URL_FROM = 6;

  {
    WG_CANNOT_MAKE_URL_FROM,
      "No se puede crear URL desde: {0}"},

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  //public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;

  {
    WG_CANNOT_LOAD_REQUESTED_DOC,
      "No se puede cargar el doc solicitado: {0}"},

  /** WG_CANNOT_FIND_COLLATOR          */
  //public static final int WG_CANNOT_FIND_COLLATOR = 8;

  {
    WG_CANNOT_FIND_COLLATOR,
      "No se ha encontrado Collator para <sort xml:lang={0}"},

  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  //public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;

  {
    WG_FUNCTIONS_SHOULD_USE_URL,
      "Sintaxis antigua: la instrucci\u00f3n de las funciones debe utilizar un url de {0}"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "codificaci\u00f3n no admitida: {0}, se utiliza UTF-8"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  //public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;

  {
    WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "codificaci\u00f3n no admitida: {0}, se utiliza Java {1}"},

  /** WG_SPECIFICITY_CONFLICTS          */
  //public static final int WG_SPECIFICITY_CONFLICTS = 12;

  {
    WG_SPECIFICITY_CONFLICTS,
      "Se han encontrado conflictos de especificidad: {0} Se utilizar\u00e1 la \u00faltima encontrada en la hoja de estilo."},

  /** WG_PARSING_AND_PREPARING          */
  //public static final int WG_PARSING_AND_PREPARING = 13;

  {
    WG_PARSING_AND_PREPARING,
      "========= An\u00e1lisis sint\u00e1ctico y preparaci\u00f3n {0} =========="},


  /** WG_ATTR_TEMPLATE          */
  //public static final int WG_ATTR_TEMPLATE = 14;

  {
    WG_ATTR_TEMPLATE, "Plantilla atri, {0}"},

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  //public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;

  {
    WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "Conflicto de coincidencia entre xsl:strip-space y xsl:preserve-space"},

  /** WG_ATTRIB_NOT_HANDLED          */
  //public static final int WG_ATTRIB_NOT_HANDLED = 16;

  {
    WG_ATTRIB_NOT_HANDLED,
      "Xalan no maneja todav\u00eda el atributo {0}."},

  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  //public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;

  {
    WG_NO_DECIMALFORMAT_DECLARATION,
      "No se ha encontrado ninguna declaraci\u00f3n para el formato decimal: {0}"},

  /** WG_OLD_XSLT_NS          */
  //public static final int WG_OLD_XSLT_NS = 18;

  {
    WG_OLD_XSLT_NS, "Falta el espacio de nombre XSLT o es incorrecto. "},

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  //public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;

  {
    WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "S\u00f3lo se permite una declaraci\u00f3n xsl:decimal-format predeterminada."},

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  //public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;

  {
    WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "los nombres xsl:decimal-format deben ser \u00fanicos. El nombre \"{0}\" est\u00e1 duplicado."},

  /** WG_ILLEGAL_ATTRIBUTE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE = 21;

  {
    WG_ILLEGAL_ATTRIBUTE,
      "{0} tiene un atributo no permitido: {1}"},

  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  //public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;

  {
    WG_COULD_NOT_RESOLVE_PREFIX,
      "No se ha podido convertir el prefijo de espacio de nombre : {0}. El nodo se ignorar\u00e1."},

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  //public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;

  {
    WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet necesita un atributo 'version'."},

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;

  {
    WG_ILLEGAL_ATTRIBUTE_NAME,
      "Nombre de atributo no permitido: {0}"},

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  //public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;

  {
    WG_ILLEGAL_ATTRIBUTE_VALUE,
      "Se ha utilizado un valor no permitido para el atributo {0}: {1}"},

  /** WG_EMPTY_SECOND_ARG          */
  //public static final int WG_EMPTY_SECOND_ARG = 26;

  {
    WG_EMPTY_SECOND_ARG,
      "El conjunto de nodos resultante del segundo argumento de la funci\u00f3n de documento est\u00e1 vac\u00edo. Se utilizar\u00e1 el primer argumento."},

 //Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    // WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML


  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  //public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;
  {
     WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "El valor del atributo 'name' del nombre xsl:processing-instruction no debe ser 'xml'"},

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    // WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME

  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  //public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;
  {
     WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "El valor del atributo ''name'' de xsl:processing-instruction debe ser un nombre NCName v\u00e1lido: {0}"},

    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    // WG_ILLEGAL_ATTRIBUTE_POSITION

  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
  //public static final int WG_ILLEGAL_ATTRIBUTE_POSITION = 29;
  {
    WG_ILLEGAL_ATTRIBUTE_POSITION,
      "No se puede agregar el atributo {0} despu\u00e9s de nodos hijos ni antes de generar un elemento.  El atributo no ser\u00e1 considerado"},

  //Check: WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?


  // Other miscellaneous text used inside the code...
    { "ui_language", "es"},
    { "help_language", "es"},
    { "language", "es"},
    { "BAD_CODE",
      "El par\u00e1metro para crear el mensaje estaba fuera de los l\u00edmites"},
    { "FORMAT_FAILED",
      "Excepci\u00f3n generada durante la llamada messageFormat"},
    { "version", ">>>>>>> Versi\u00f3n Xalan"},
    { "version2", "<<<<<<<"},
    { "yes", "s\u00ed"},
    { "line", "L\u00ednea #"},
    { "column", "Columna #"},
    { "xsldone", "XSLProcessor: hecho"},
    { "xslProc_option", "opciones de clase Proceso de la l\u00ednea de comandos Xalan-J:"},
  { "xslProc_invalid_xsltc_option", "La opci\u00f3n {0} no es v\u00e1lida en el modo XSLTC."},
  { "xslProc_invalid_xalan_option", "La opci\u00f3n {0} s\u00f3lo se puede utilizar con -XSLTC."},
  { "xslProc_no_input", "Error: No se ha especificado ninguna hoja de estilos ni xml de entrada. Para obtener las instrucciones de uso, ejecute este comando sin especificar ninguna opci\u00f3n."},
  { "xslProc_common_options", "-Opciones comunes-"},
  { "xslProc_xalan_options", "-Opciones para Xalan-"},
  { "xslProc_xsltc_options", "-Opciones para XSLTC-"},
  { "xslProc_return_to_continue", "(pulse <retorno> para proseguir)"},

  { "optionXSLTC", "   [-XSLTC (utilice XSLTC para la transformaci\u00f3n)]"},
    { "optionIN", "    -IN inputXMLURL"},
    { "optionXSL", "   [-XSL XSLTransformationURL]"},
    { "optionOUT", "   [-OUT outputFileName]"},
    { "optionLXCIN", "   [-LXCIN compiledStylesheetFileNameIn]"},
    { "optionLXCOUT", "   [-LXCOUT compiledStylesheetFileNameOutOut]"},
    { "optionPARSER",
      "   [nombre totalmente cualificado -PARSER de clase de v\u00ednculo de analizador sint\u00e1ctico]"},
    { "optionE",
     "   [-E (No expandir refs de entidad)]"},
    { "optionV",
     "   [-E (No expandir refs de entidad)]"},
    {"optionQC",
      "   [-QC (Advertencias silenciosas de conflictos de patrones)]"},
    {"optionQ",
     "   [-Q  (Modo silencioso)]"},
    { "optionLF",
      "   [-LF (Utilizar cambios de l\u00ednea s\u00f3lo en la salida {el valor predeterminado es CR/LF})]"},
    { "optionCR",
      "   [-CR (Utilizar retornos de carro s\u00f3lo en la salida {el valor predeterminado es CR/LF})]"},
    { "optionESCAPE",
      "   [-ESCAPE (\u00bfCu\u00e1les son los caracteres de escape? {el valor por defecto es <>&\"\'\\r\\n}]"},
    { "optionINDENT",
      "   [-INDENT (Controlar el n\u00famero de espacios de indentaci\u00f3n {el valor por defecto es 0})]"},
    { "optionTT",
      "   [-TT (Rastrear las plantillas seg\u00fan se vayan invocando.)]"},
    { "optionTG",
      "   [-TG (Rastrear cada suceso de generaci\u00f3n.)]"},
    {"optionTS",
     "   [-TS (Rastrear cada suceso de selecci\u00f3n.)]"},
    { "optionTTC",
      "   [-TTC (Rastrear las plantillas hijas seg\u00fan se vayan procesando.)]"},
    { "optionTCLASS",
      "   [-TCLASS (Clase TraceListener para las extensiones de rastreo.)]"},
    { "optionVALIDATE",
      "   [-VALIDATE (Establecer si se realiza la validaci\u00f3n.  El valor predeterminado de la validaci\u00f3n es off.)]"},
    {"optionEDUMP",
      "   [-EDUMP {nombre de archivo opcional} (Hacer volcado de pila en caso de error.)]"},
    { "optionXML",
      "   [-XML (Utilizar el formateador XML y agregar la cabecera de XML.)]"},
    { "optionTEXT",
      "   [-TEXT (Utilizar el formateador de texto sencillo.)]"},
    { "optionHTML",
     "   [-HTML (Utilizar el formateador HTML.)]"},
    {"optionPARAM",
      "   [expresi\u00f3n de nombre -PARAM (Establecer un par\u00e1metro de hoja de estilo)]"},
    { "noParsermsg1",
     "Ha fallado el proceso XSL."},
    { "noParsermsg2",
     "** No se ha encontrado el analizador sint\u00e1ctico **"},
    { "noParsermsg3",
     "Comprobar classpath."},
    { "noParsermsg4",
      "Si no tiene el analizador sint\u00e1ctico XML para Java de IBM puede cargarlo de "},
    { "noParsermsg5",
      "AlphaWorks de IBM: http://www.alphaworks.ibm.com/formula/xml"},
    { "optionURIRESOLVER",
     "   [nombre de clase completo -URIRESOLVER (Utilizar URIResolver para convertir los URIs)]"},
    { "optionENTITYRESOLVER",
     "   [nombre de clase completo -ENTITYRESOLVER (Utilizar EntityResolver para convertir las entidades)]"},
    {"optionCONTENTHANDLER",
     "   [nombre de clase completo -CONTENTHANDLER (Utilizar ContentHandler para serializar la salida)]"},
    {"optionLINENUMBERS",
     "   [-L utilizar n\u00fameros de l\u00edneas para el documento fuente]"},
		
    //Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


    { "optionMEDIA",
     " [-MEDIA mediaType (utilice un atributo media para buscar las hojas de estilo asociadas con un documento.)]"},
    { "optionFLAVOR",
     " [-FLAVOR flavorName (utilice expl\u00edcitamente s2s=SAX o d2d=DOM para la transformaci\u00f3n.)] "}, // Added by sboag/scurcuru; experimental
    { "optionDIAG",
     " [-DIAG (la impresi\u00f3n tard\u00f3 milisegundos.)]"},
    {"optionINCREMENTAL",
     " [-INCREMENTAL (solicitar una construcci\u00f3n DTM incremental estableciendo http://xml.apache.org/xalan/features/incremental como Verdadero.)]"},
    { "optionNOOPTIMIMIZE",
     " [-NOOPTIMIMIZE (solicitar no optimizar la hoja de estilo estableciendo http://xml.apache.org/xalan/features/incremental a Falso.)]"},
    { "optionRL",
     " [-RL recursionlimit (afirmar l\u00edmite num\u00e9rico en la profundidad de recursividad de la hoja de estilo.)]"},
    { "optionXO",
     " [-XO [transletName] (asignar nombre al translet generado)]"},
    {"optionXD",
     " [-XD destinationDirectory (especifica un directorio de destino para translet)]"},
    { "optionXJ",
     " [-XJ jarfile (empaqueta clases translet en un archivo JAR denominado <archivoJAR>)]"},
    { "optionXP",
     " [-XP package (especifica un prefijo de nombre de paquete para todas las clases translet generadas)]"},
  { "optionXN",  "   [-XN (permite la sustituci\u00f3n de plantillas)]" },
  { "optionXX",  "   [-XX (activa la generaci\u00f3n de mensajes de depuraci\u00f3n adicionales)]"},
  { "optionXT" , "   [-XT (use translet para realizar la transformaci\u00f3n, siempre que sea posible)]"},
  { "diagTiming"," --------- La transformaci\u00f3n de {0} a trav\u00e9s de {1} tard\u00f3 {2} ms" },
  { "recursionTooDeep","El anidamiento de las plantillas es demasiado profundo. nesting = {0}, template {1} {2}" },
  { "nameIs", "el nombre es" },
  { "matchPatternIs", "el patr\u00f3n de coincidencia es" }

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
  public static final String WARNING_HEADER = "Advertencia: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "PATR\u00d3N ";

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



