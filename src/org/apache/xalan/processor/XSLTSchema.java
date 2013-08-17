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
package org.apache.xalan.processor;

import java.util.Hashtable;

import org.apache.xalan.templates.*;
import org.apache.xml.utils.QName;

/**
 * This class defines the allowed structure for a stylesheet, and the
 * mapping between Xalan classes and the markup elements in the stylesheet.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 */
public class XSLTSchema extends XSLTElementDef
{

  /**
   * Construct a XSLTSchema which represents the XSLT "schema".
   */
  XSLTSchema()
  {
    build();
  }

  /**
   * This method builds an XSLT "schema" according to http://www.w3.org/TR/xslt#dtd.  This
   * schema provides instructions for building the Xalan Stylesheet (Templates) structure.
   */
  void build()
  {

    XSLTAttributeDef hrefAttr = new XSLTAttributeDef(null, "href",
                                  XSLTAttributeDef.T_URL, true);
    XSLTAttributeDef elementsAttr = new XSLTAttributeDef(null, "elements",
                                      XSLTAttributeDef.T_SIMPLEPATTERNLIST,
                                      true);
    // XSLTAttributeDef anyNamespacedAttr = new XSLTAttributeDef("*", "*",
    //                                XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef methodAttr = new XSLTAttributeDef(null, "method",
                                    XSLTAttributeDef.T_QNAME, false);
    XSLTAttributeDef versionAttr = new XSLTAttributeDef(null, "version",
                                     XSLTAttributeDef.T_NMTOKEN, false);
    XSLTAttributeDef encodingAttr = new XSLTAttributeDef(null, "encoding",
                                      XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef omitXmlDeclarationAttr = new XSLTAttributeDef(null,
                                                "omit-xml-declaration",
                                                XSLTAttributeDef.T_YESNO,
                                                false);
    XSLTAttributeDef standaloneAttr = new XSLTAttributeDef(null,
                                        "standalone",
                                        XSLTAttributeDef.T_YESNO, false);
    XSLTAttributeDef doctypePublicAttr = new XSLTAttributeDef(null,
                                           "doctype-public",
                                           XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef doctypeSystemAttr = new XSLTAttributeDef(null,
                                           "doctype-system",
                                           XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef cdataSectionElementsAttr = new XSLTAttributeDef(null,
                                                  "cdata-section-elements",
                                                  XSLTAttributeDef.T_QNAMES,
                                                  false);
    XSLTAttributeDef indentAttr = new XSLTAttributeDef(null, "indent",
                                    XSLTAttributeDef.T_YESNO, false);
    XSLTAttributeDef mediaTypeAttr = new XSLTAttributeDef(null, "media-type",
                                       XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef nameAttrRequired = new XSLTAttributeDef(null, "name",
                                          XSLTAttributeDef.T_QNAME, true);
    XSLTAttributeDef nameAttrOpt = new XSLTAttributeDef(null, "name",
                                     XSLTAttributeDef.T_QNAME, false);
    XSLTAttributeDef useAttr = new XSLTAttributeDef(null, "use",
                                 XSLTAttributeDef.T_EXPR, true);
    XSLTAttributeDef nameAVTRequired = new XSLTAttributeDef(null, "name",
                                         XSLTAttributeDef.T_AVT, true);
    XSLTAttributeDef namespaceAVTOpt = new XSLTAttributeDef(null,
                                         "namespace", XSLTAttributeDef.T_AVT,
                                         false);
    XSLTAttributeDef decimalSeparatorAttr = new XSLTAttributeDef(null,
                                              "decimal-separator",
                                              XSLTAttributeDef.T_CHAR, ".");
    XSLTAttributeDef groupingSeparatorAttr = new XSLTAttributeDef(null,
                                               "grouping-separator",
                                               XSLTAttributeDef.T_CHAR, ",");
    XSLTAttributeDef infinityAttr = new XSLTAttributeDef(null, "infinity",
                                      XSLTAttributeDef.T_CDATA, "Infinity");
    XSLTAttributeDef minusSignAttr = new XSLTAttributeDef(null, "minus-sign",
                                       XSLTAttributeDef.T_CHAR, "-");
    XSLTAttributeDef NaNAttr = new XSLTAttributeDef(null, "NaN",
                                 XSLTAttributeDef.T_CDATA, "NaN");
    XSLTAttributeDef percentAttr = new XSLTAttributeDef(null, "percent",
                                     XSLTAttributeDef.T_CHAR, "%");
    XSLTAttributeDef perMilleAttr = new XSLTAttributeDef(null, "per-mille",
                                      XSLTAttributeDef.T_CHAR,
                                      false /* ,"&#x2030;" */);
    XSLTAttributeDef zeroDigitAttr = new XSLTAttributeDef(null, "zero-digit",
                                       XSLTAttributeDef.T_CHAR, "0");
    XSLTAttributeDef digitAttr = new XSLTAttributeDef(null, "digit",
                                   XSLTAttributeDef.T_CHAR, "#");
    XSLTAttributeDef patternSeparatorAttr = new XSLTAttributeDef(null,
                                              "pattern-separator",
                                              XSLTAttributeDef.T_CHAR, ";");
    XSLTAttributeDef useAttributeSetsAttr = new XSLTAttributeDef(null,
                                              "use-attribute-sets",
                                              XSLTAttributeDef.T_QNAMES,
                                              false);
    XSLTAttributeDef selectAttrRequired = new XSLTAttributeDef(null,
                                            "select",
                                            XSLTAttributeDef.T_EXPR, true);
    XSLTAttributeDef testAttrRequired = new XSLTAttributeDef(null, "test",
                                          XSLTAttributeDef.T_EXPR, true);
    XSLTAttributeDef selectAttrOpt = new XSLTAttributeDef(null, "select",
                                       XSLTAttributeDef.T_EXPR, false);
    XSLTAttributeDef selectAttrDefNode = new XSLTAttributeDef(null, "select",
                                           XSLTAttributeDef.T_EXPR, "node()");
    XSLTAttributeDef selectAttrDefDot = new XSLTAttributeDef(null, "select",
                                          XSLTAttributeDef.T_EXPR, ".");
    XSLTAttributeDef matchAttrRequired = new XSLTAttributeDef(null, "match",
                                           XSLTAttributeDef.T_PATTERN, true);
    XSLTAttributeDef matchAttrOpt = new XSLTAttributeDef(null, "match",
                                      XSLTAttributeDef.T_PATTERN, false);
    XSLTAttributeDef priorityAttr = new XSLTAttributeDef(null, "priority",
                                      XSLTAttributeDef.T_PRIORITY, false);
    XSLTAttributeDef modeAttr = new XSLTAttributeDef(null, "mode",
                                  XSLTAttributeDef.T_QNAME, false);
    XSLTAttributeDef spaceAttr =
      new XSLTAttributeDef(Constants.S_XMLNAMESPACEURI, "space", false,
                           "default", Constants.ATTRVAL_STRIP, "preserve",
                           Constants.ATTRVAL_PRESERVE);
    XSLTAttributeDef spaceAttrLiteral =
      new XSLTAttributeDef(Constants.S_XMLNAMESPACEURI, "space", 
                                          XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef stylesheetPrefixAttr = new XSLTAttributeDef(null,
                                              "stylesheet-prefix",
                                              XSLTAttributeDef.T_CDATA, true);
    XSLTAttributeDef resultPrefixAttr = new XSLTAttributeDef(null,
                                          "result-prefix",
                                          XSLTAttributeDef.T_CDATA, true);
    XSLTAttributeDef disableOutputEscapingAttr = new XSLTAttributeDef(null,
                                                   "disable-output-escaping",
                                                   XSLTAttributeDef.T_YESNO,
                                                   false);
    XSLTAttributeDef levelAttr = new XSLTAttributeDef(null, "level", false,
                                   "single", Constants.NUMBERLEVEL_SINGLE,
                                   "multiple", Constants.NUMBERLEVEL_MULTI,
                                   "any", Constants.NUMBERLEVEL_ANY);

    levelAttr.setDefault("single");

    XSLTAttributeDef countAttr = new XSLTAttributeDef(null, "count",
                                   XSLTAttributeDef.T_PATTERN, false);
    XSLTAttributeDef fromAttr = new XSLTAttributeDef(null, "from",
                                  XSLTAttributeDef.T_PATTERN, false);
    XSLTAttributeDef valueAttr = new XSLTAttributeDef(null, "value",
                                   XSLTAttributeDef.T_EXPR, false);
    XSLTAttributeDef formatAttr = new XSLTAttributeDef(null, "format",
                                    XSLTAttributeDef.T_AVT, false);

    formatAttr.setDefault("1");

    XSLTAttributeDef langAttr = new XSLTAttributeDef(null, "lang",
                                  XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef letterValueAttr = new XSLTAttributeDef(null,
                                         "letter-value",
                                         XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef groupingSeparatorAVT = new XSLTAttributeDef(null,
                                              "grouping-separator",
                                              XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef groupingSizeAttr = new XSLTAttributeDef(null,
                                          "grouping-size",
                                          XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef dataTypeAttr = new XSLTAttributeDef(null, "data-type",
                                      XSLTAttributeDef.T_AVT, "text");
    XSLTAttributeDef orderAttr = new XSLTAttributeDef(null, "order",
                                   XSLTAttributeDef.T_AVT, "ascending");
    XSLTAttributeDef caseOrderAttr = new XSLTAttributeDef(null, "case-order",
                                       XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef terminateAttr = new XSLTAttributeDef(null, "terminate",
                                       XSLTAttributeDef.T_YESNO, false);

    terminateAttr.setDefault("no");

    XSLTAttributeDef xslExcludeResultPrefixesAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL,
                           "exclude-result-prefixes",
                           XSLTAttributeDef.T_STRINGLIST, false);
    XSLTAttributeDef xslExtensionElementPrefixesAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL,
                           "extension-element-prefixes",
                           XSLTAttributeDef.T_PREFIX_URLLIST, false);
    XSLTAttributeDef xslUseAttributeSetsAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "use-attribute-sets",
                           XSLTAttributeDef.T_QNAMES, false);
    XSLTAttributeDef xslVersionAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "version",
                           XSLTAttributeDef.T_NMTOKEN, false);
    XSLTElementDef charData = new XSLTElementDef(this, null, "text()",
                                null /*alias */, null /* elements */, null,  /* attributes */
                                new ProcessorCharacters(),
                                ElemTextLiteral.class /* class object */);

    charData.setType(XSLTElementDef.T_PCDATA);

    XSLTElementDef whiteSpaceOnly = new XSLTElementDef(this, null, "text()",
                                      null /*alias */, null /* elements */,
                                      null,  /* attributes */
                                      null,
                                      ElemTextLiteral.class /* should be null? -sb */);

    charData.setType(XSLTElementDef.T_PCDATA);

    XSLTAttributeDef resultAttr = new XSLTAttributeDef(null, "*",
                                    XSLTAttributeDef.T_AVT, false);
    XSLTAttributeDef xslResultAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "*",
                           XSLTAttributeDef.T_CDATA, false);
    XSLTElementDef[] templateElements = new XSLTElementDef[21];
    XSLTElementDef[] templateElementsAndParams = new XSLTElementDef[22];
    XSLTElementDef[] templateElementsAndSort = new XSLTElementDef[22];
    XSLTElementDef[] charTemplateElements = new XSLTElementDef[15];
    XSLTElementDef resultElement = new XSLTElementDef(this, null, "*",
                                     null /*alias */,
                                     templateElements /* elements */,
                                     new XSLTAttributeDef[]{
                                       spaceAttrLiteral, // special
                                       xslExcludeResultPrefixesAttr,
                                       xslExtensionElementPrefixesAttr,
                                       xslUseAttributeSetsAttr,
                                       xslVersionAttr,
                                       xslResultAttr,
                                       resultAttr }, 
                                        new ProcessorLRE(),
                                     ElemLiteralResult.class /* class object */, 20, true);
    XSLTElementDef unknownElement =
      new XSLTElementDef(this, "*", "unknown", null /*alias */,
                         templateElementsAndParams /* elements */,
                         new XSLTAttributeDef[]{ xslExcludeResultPrefixesAttr,
                                                 xslExtensionElementPrefixesAttr,
                                                 xslUseAttributeSetsAttr,
                                                 xslVersionAttr,
                                                 xslResultAttr,
                                                 resultAttr }, 
                                                                                                 new ProcessorUnknown(),
                         ElemUnknown.class /* class object */, 20, true);
    XSLTElementDef xslValueOf = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "value-of",
                                  null /*alias */, null /* elements */,
                                  new XSLTAttributeDef[]{ selectAttrRequired,
                                                          disableOutputEscapingAttr }, 
                                               new ProcessorTemplateElem(),
                                  ElemValueOf.class /* class object */, 20, true);
    XSLTElementDef xslCopyOf = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "copy-of",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ selectAttrRequired },
                                 new ProcessorTemplateElem(),
                                 ElemCopyOf.class /* class object */, 20, true);
    XSLTElementDef xslNumber = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "number",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ levelAttr,
                                                         countAttr,
                                                         fromAttr,
                                                         valueAttr,
                                                         formatAttr,
                                                         langAttr,
                                                         letterValueAttr,
                                                         groupingSeparatorAVT,
                                                         groupingSizeAttr }, 
                                        new ProcessorTemplateElem(),
                                 ElemNumber.class /* class object */, 20, true);

    // <!-- xsl:sort cannot occur after any other elements or
    // any non-whitespace character -->
    XSLTElementDef xslSort = new XSLTElementDef(this,
                                                Constants.S_XSLNAMESPACEURL,
                                                "sort", null /*alias */,
                                                null /* elements */,
                                                new XSLTAttributeDef[]{
                                                  selectAttrDefDot,
                                                  langAttr,
                                                  dataTypeAttr,
                                                  orderAttr,
                                                  caseOrderAttr }, 
                                       new ProcessorTemplateElem(),
                                                ElemSort.class/* class object */, 19, true );
    XSLTElementDef xslWithParam = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL,
                                    "with-param", null /*alias */,
                                    templateElements /* elements */,  // %template;>
                                    new XSLTAttributeDef[]{ nameAttrRequired,
                                                            selectAttrOpt }, new ProcessorTemplateElem(),
                                                                             ElemWithParam.class /* class object */, 19, true);
    XSLTElementDef xslApplyTemplates = new XSLTElementDef(this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "apply-templates", null /*alias */,
                                         new XSLTElementDef[]{ xslSort,
                                                               xslWithParam } /* elements */, new XSLTAttributeDef[]{
                                                                 selectAttrDefNode,
                                                                 modeAttr }, 
                                                                        new ProcessorTemplateElem(),
                                         ElemApplyTemplates.class /* class object */, 20, true);
    XSLTElementDef xslApplyImports =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "apply-imports",
                         null /*alias */, null /* elements */,
                         new XSLTAttributeDef[]{},
                         new ProcessorTemplateElem(),
                         ElemApplyImport.class /* class object */);
    XSLTElementDef xslForEach = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "for-each",
                                  null /*alias */, templateElementsAndSort,  // (#PCDATA %instructions; %result-elements; | xsl:sort)*
                                  new XSLTAttributeDef[]{ selectAttrRequired,
                                                          spaceAttr }, 
                                               new ProcessorTemplateElem(),
                                  ElemForEach.class /* class object */, true, false, true, 20, true);
    XSLTElementDef xslIf = new XSLTElementDef(this,
                                              Constants.S_XSLNAMESPACEURL,
                                              "if", null /*alias */,
                                              templateElements /* elements */,  // %template;
                                              new XSLTAttributeDef[]{
                                                testAttrRequired,
                                                spaceAttr }, new ProcessorTemplateElem(),
                                                             ElemIf.class /* class object */, 20, true);
    XSLTElementDef xslWhen =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "when",
                         null /*alias */, templateElements /* elements */,  // %template;>
                                                new XSLTAttributeDef[]{
                                                  testAttrRequired,
                                                  spaceAttr }, new ProcessorTemplateElem(),
                                                               ElemWhen.class /* class object */,
                                                                                                false, true, 1, true);
    XSLTElementDef xslOtherwise = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL, "otherwise",
                                    null /*alias */,
                                    templateElements /* elements */,  // %template;>
                                    new XSLTAttributeDef[]{ spaceAttr },
                                    new ProcessorTemplateElem(),
                                    ElemOtherwise.class /* class object */,
                                                       false, false, 2, false);
    XSLTElementDef xslChoose = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "choose",
                                 null /*alias */,
                                 new XSLTElementDef[]{ xslWhen,
                                                       xslOtherwise } /* elements */, 
                                        new XSLTAttributeDef[]{ spaceAttr },
                                 new ProcessorTemplateElem(),
                                 ElemChoose.class /* class object */, true, false, true, 20, true);                                
    XSLTElementDef xslAttribute = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL, "attribute",
                                    null /*alias */,
                                    charTemplateElements /* elements */,  // %char-template;>
                                    new XSLTAttributeDef[]{ nameAVTRequired,
                                                            namespaceAVTOpt,
                                                            spaceAttr }, 
                                    new ProcessorTemplateElem(),
                                    ElemAttribute.class /* class object */, 20, true);
    XSLTElementDef xslCallTemplate =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "call-template",
                         null /*alias */,
                         new XSLTElementDef[]{ xslWithParam } /* elements */,
                         new XSLTAttributeDef[]{ nameAttrRequired },
                         new ProcessorTemplateElem(),
                         ElemCallTemplate.class /* class object */, 20, true);
    XSLTElementDef xslVariable = new XSLTElementDef(this,
                                   Constants.S_XSLNAMESPACEURL, "variable",
                                   null /*alias */,
                                   templateElements /* elements */,  // %template;>
                                   new XSLTAttributeDef[]{ nameAttrRequired,
                                                           selectAttrOpt }, 
                                  new ProcessorTemplateElem(),
                                   ElemVariable.class /* class object */, 20, true);
    XSLTElementDef xslParam = new XSLTElementDef(this,
                                Constants.S_XSLNAMESPACEURL, "param",
                                null /*alias */,
                                templateElements /* elements */,  // %template;>
                                new XSLTAttributeDef[]{ nameAttrRequired,
                                                        selectAttrOpt }, 
                                       new ProcessorTemplateElem(),
                                ElemParam.class /* class object */, 19, true);
    XSLTElementDef xslText =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "text",
                         null /*alias */,
                         new XSLTElementDef[]{ charData } /* elements */,
                         new XSLTAttributeDef[]{ disableOutputEscapingAttr },
                         new ProcessorText(),
                         ElemText.class /* class object */, 20, true);
    XSLTElementDef xslProcessingInstruction =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL,
                         "processing-instruction", null /*alias */,
                         charTemplateElements /* elements */,  // %char-template;>
                         new XSLTAttributeDef[]{
                                                  nameAVTRequired,
                                                  spaceAttr }, 
                                        new ProcessorTemplateElem(),
                          ElemPI.class /* class object */, 20, true);
    XSLTElementDef xslElement = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "element",
                                  null /*alias */,
                                  templateElements /* elements */,  // %template;
                                  new XSLTAttributeDef[]{ nameAVTRequired,
                                                          namespaceAVTOpt,
                                                          useAttributeSetsAttr,
                                                          spaceAttr }, 
                                               new ProcessorTemplateElem(),
                                  ElemElement.class /* class object */, 20, true);
    XSLTElementDef xslComment = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "comment",
                                  null /*alias */,
                                  charTemplateElements /* elements */,  // %char-template;>
                                  new XSLTAttributeDef[]{ spaceAttr },
                                  new ProcessorTemplateElem(),
                                  ElemComment.class /* class object */, 20, true);
    XSLTElementDef xslCopy =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "copy",
                         null /*alias */, templateElements /* elements */,  // %template;>
                          new XSLTAttributeDef[]{
                                                  spaceAttr,
                                                  useAttributeSetsAttr }, 
                                        new ProcessorTemplateElem(),
                          ElemCopy.class /* class object */, 20, true);
    XSLTElementDef xslMessage = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "message",
                                  null /*alias */,
                                  templateElements /* elements */,  // %template;>
                                  new XSLTAttributeDef[]{ terminateAttr },
                                  new ProcessorTemplateElem(),
                                  ElemMessage.class /* class object */, 20, true);
    XSLTElementDef xslFallback = new XSLTElementDef(this,
                                   Constants.S_XSLNAMESPACEURL, "fallback",
                                   null /*alias */,
                                   templateElements /* elements */,  // %template;>
                                   new XSLTAttributeDef[]{ spaceAttr },
                                   new ProcessorTemplateElem(),
                                   ElemFallback.class /* class object */, 20, true);
    int i = 0;

    templateElements[i++] = charData;  // #PCDATA

    // char-instructions
    templateElements[i++] = xslApplyTemplates;
    templateElements[i++] = xslCallTemplate;
    templateElements[i++] = xslApplyImports;
    templateElements[i++] = xslForEach;
    templateElements[i++] = xslValueOf;
    templateElements[i++] = xslCopyOf;
    templateElements[i++] = xslNumber;
    templateElements[i++] = xslChoose;
    templateElements[i++] = xslIf;
    templateElements[i++] = xslText;
    templateElements[i++] = xslCopy;
    templateElements[i++] = xslVariable;
    templateElements[i++] = xslMessage;
    templateElements[i++] = xslFallback;

    // instructions
    templateElements[i++] = xslProcessingInstruction;
    templateElements[i++] = xslComment;
    templateElements[i++] = xslElement;
    templateElements[i++] = xslAttribute;
    templateElements[i++] = resultElement;
    templateElements[i++] = unknownElement;

    int k;

    for (k = 0; k < i; k++)
    {
      templateElementsAndParams[k] = templateElements[k];
    }

    templateElementsAndParams[k] = xslParam;

    for (k = 0; k < i; k++)
    {
      templateElementsAndSort[k] = templateElements[k];
    }

    templateElementsAndSort[k] = xslSort;
    i = 0;
    charTemplateElements[i++] = charData;  // #PCDATA

    // char-instructions
    charTemplateElements[i++] = xslApplyTemplates;
    charTemplateElements[i++] = xslCallTemplate;
    charTemplateElements[i++] = xslApplyImports;
    charTemplateElements[i++] = xslForEach;
    charTemplateElements[i++] = xslValueOf;
    charTemplateElements[i++] = xslCopyOf;
    charTemplateElements[i++] = xslNumber;
    charTemplateElements[i++] = xslChoose;
    charTemplateElements[i++] = xslIf;
    charTemplateElements[i++] = xslText;
    charTemplateElements[i++] = xslCopy;
    charTemplateElements[i++] = xslVariable;
    charTemplateElements[i++] = xslMessage;
    charTemplateElements[i++] = xslFallback;

    XSLTElementDef importDef = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "import",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ hrefAttr },  // EMPTY
                                 new ProcessorImport(),
                                 null /* class object */,
                                        1, true);
    XSLTElementDef includeDef = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "include",
                                  null /*alias */, null /* elements */,  // EMPTY
                                  new XSLTAttributeDef[]{ hrefAttr },
                                  new ProcessorInclude(),
                                  null /* class object */,
                                               20, true);
    XSLTElementDef[] topLevelElements = new XSLTElementDef[]{ includeDef,
                                                              importDef,
                                                              // resultElement,
                                                              whiteSpaceOnly,
                                                              unknownElement,
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "strip-space",
                                                                null /*alias */,
                                                                null /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                elementsAttr },
                                                                new ProcessorStripSpace(),
                                                                null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "preserve-space",
                                                                null /*alias */,
                                                                null /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                elementsAttr },
                                                                new ProcessorPreserveSpace(),
                                                                null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "output",
                                                                null /*alias */,
                                                                null /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                  methodAttr,
                                                                  versionAttr,
                                                                  encodingAttr,
                                                                  omitXmlDeclarationAttr,
                                                                  standaloneAttr,
                                                                  doctypePublicAttr,
                                                                  doctypeSystemAttr,
                                                                  cdataSectionElementsAttr,
                                                                  indentAttr,
                                                                  mediaTypeAttr,
                                                                  XSLTAttributeDef.m_foreignAttr }, 
                                                                new ProcessorOutputElem(), null /* class object */, 20, true), 
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "key",
                                                                null /*alias */,
                                                                null /* elements */,  // EMPTY
                                                                new XSLTAttributeDef[]{ nameAttrRequired,
                                                                                            matchAttrRequired,
                                                                                            useAttr }, 
                                                                                               new ProcessorKey(), null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "decimal-format",
                                                                null /*alias */,
                                                                null /* elements */,  // EMPTY
                                                                                 new XSLTAttributeDef[]{
                                                                                   nameAttrOpt,
                                                                                   decimalSeparatorAttr,
                                                                                   groupingSeparatorAttr,
                                                                                   infinityAttr,
                                                                                   minusSignAttr,
                                                                                   NaNAttr,
                                                                                   percentAttr,
                                                                                   perMilleAttr,
                                                                                   zeroDigitAttr,
                                                                                   digitAttr,
                                                                                   patternSeparatorAttr }, 
                                                                                               new ProcessorDecimalFormat(),
                                                                null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "attribute-set",
                                                                null /*alias */,
                                                                new XSLTElementDef[]{
                                                                xslAttribute } /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                  nameAttrRequired,
                                                                  useAttributeSetsAttr }, new ProcessorAttributeSet(),
                                                                                          null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "variable",
                                                                null /*alias */,
                                                                templateElements /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                  nameAttrRequired,
                                                                  selectAttrOpt }, new ProcessorGlobalVariableDecl(),
                                                                                   ElemVariable.class /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "param",
                                                                null /*alias */,
                                                                templateElements /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                  nameAttrRequired,
                                                                  selectAttrOpt }, new ProcessorGlobalParamDecl(),
                                                                                   ElemParam.class /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_XSLNAMESPACEURL,
                                                                "template",
                                                                null /*alias */,
                                                                templateElementsAndParams /* elements */,
                                                                new XSLTAttributeDef[]{
                                                                  matchAttrOpt,
                                                                  nameAttrOpt,
                                                                  priorityAttr,
                                                                  modeAttr,
                                                                  spaceAttr }, new ProcessorTemplate(), ElemTemplate.class /* class object */, true, 20, true), 
                                                                                             new XSLTElementDef(
                                                                    this,
                                                                    Constants.S_XSLNAMESPACEURL,
                                                                    "namespace-alias",
                                                                    null /*alias */,
                                                                    null /* elements */,  // EMPTY
                                                                    new XSLTAttributeDef[]{ stylesheetPrefixAttr,
                                                                                            resultPrefixAttr }, 
                                                                                                   new ProcessorNamespaceAlias(), null /* class object */, 20, true),
                                                              new XSLTElementDef(
                                                                this,
                                                                Constants.S_BUILTIN_EXTENSIONS_URL,
                                                                "component",
                                                                null /*alias */,
                                                                new XSLTElementDef[]{
                                                                  new XSLTElementDef(
                                                                    this,
                                                                    Constants.S_BUILTIN_EXTENSIONS_URL,
                                                                    "script",
                                                                    null /*alias */,
                                                                    new XSLTElementDef[]{ charData } /* elements */,
                                                                    new XSLTAttributeDef[]{
                                                                      new XSLTAttributeDef(
                                                                        null,
                                                                        "lang",
                                                                        XSLTAttributeDef.T_NMTOKEN,
                                                                        true),
                                                                      new XSLTAttributeDef(null, "src", XSLTAttributeDef.T_URL, false) }, 
                                                                                                  new ProcessorLRE(),
                                                                   ElemExtensionScript.class /* class object */, 20, true) },  // EMPTY
                                                                                              new XSLTAttributeDef[]{ new XSLTAttributeDef(null, "prefix", XSLTAttributeDef.T_NMTOKEN, true),
                                                                                                                      new XSLTAttributeDef(null, "elements", XSLTAttributeDef.T_STRINGLIST, false),
                                                                                                                      new XSLTAttributeDef(null, "functions", XSLTAttributeDef.T_STRINGLIST, false) }, new ProcessorLRE(), ElemExtensionDecl.class /* class object */) };
    XSLTAttributeDef excludeResultPrefixesAttr =
      new XSLTAttributeDef(null, "exclude-result-prefixes",
                           XSLTAttributeDef.T_STRINGLIST, false);
    XSLTAttributeDef extensionElementPrefixesAttr =
      new XSLTAttributeDef(null, "extension-element-prefixes",
                           XSLTAttributeDef.T_PREFIX_URLLIST, false);
    XSLTAttributeDef idAttr = new XSLTAttributeDef(null, "id",
                                XSLTAttributeDef.T_CDATA, false);
    XSLTAttributeDef versionAttrRequired = new XSLTAttributeDef(null,
                                             "version",
                                             XSLTAttributeDef.T_NMTOKEN,
                                             true);
    XSLTElementDef stylesheetElemDef = new XSLTElementDef(this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "stylesheet", "transform",
                                         topLevelElements,
                                         new XSLTAttributeDef[]{
                                           extensionElementPrefixesAttr,
                                           excludeResultPrefixesAttr,
                                           idAttr,
                                           versionAttrRequired,
                                           spaceAttr }, new ProcessorStylesheetElement(),  /* ContentHandler */
                                         null  /* class object */,
                                         true, -1, false);

    importDef.setElements(new XSLTElementDef[]{ stylesheetElemDef,
                                                resultElement,
                                                unknownElement });
    includeDef.setElements(new XSLTElementDef[]{ stylesheetElemDef,
                                                 resultElement,
                                                 unknownElement });
    build(null, null, null, new XSLTElementDef[]{ stylesheetElemDef,
                                                  whiteSpaceOnly,
                                                  resultElement,
                                                  unknownElement }, null,
                                                                    new ProcessorStylesheetDoc(),  /* ContentHandler */
                                                                    null  /* class object */
                                                                      );
  }

  /**
   * A hashtable of all available built-in elements for use by the element-available
   * function.
   * TODO:  When we convert to Java2, this should be a Set.
   */
  private Hashtable m_availElems = new Hashtable();
  
  /**
   * Get the table of available elements.
   * 
   * @return table of available elements, keyed by qualified names, and with 
   * values of the same qualified names.
   */
  public Hashtable getElemsAvailable() 
  {
    return m_availElems;
  }

  /**
   * Adds a new element name to the Hashtable of available elements.
   * @param elemName The name of the element to add to the Hashtable of available elements.
   */
  void addAvailableElement(QName elemName)
  {
    m_availElems.put(elemName, elemName);
  }

  /**
   * Determines whether the passed element name is present in the list of available elements.
   * @param elemName The name of the element to look up.
   *
   * @return true if an element corresponding to elemName is available.
   */
  public boolean elementAvailable(QName elemName)
  {
    return m_availElems.containsKey(elemName);
  }
}
