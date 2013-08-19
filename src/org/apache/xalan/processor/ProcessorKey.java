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

import org.apache.xalan.templates.KeyDeclaration;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

import java.util.Vector;

/**
 * TransformerFactory for xsl:key markup.
 * <pre>
 * <!ELEMENT xsl:key EMPTY>
 * <!ATTLIST xsl:key
 *   name %qname; #REQUIRED
 *   match %pattern; #REQUIRED
 *   use %expr; #REQUIRED
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
 */
class ProcessorKey extends XSLTElementProcessor
{

  /**
   * Receive notification of the start of an xsl:key element.
   *
   * @param handler The calling StylesheetHandler/TemplatesBuilder.
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param rawName The raw XML 1.0 name (with prefix), or the
   *        empty string if raw names are not available.
   * @param attributes The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {

    KeyDeclaration kd = new KeyDeclaration(handler.getStylesheet(), handler.nextUid());

    kd.setDOMBackPointer(handler.getOriginatingNode());
    kd.setLocaterInfo(handler.getLocator());
    setPropertiesFromAttributes(handler, rawName, attributes, kd);
    handler.getStylesheet().setKey(kd);
  }

  /**
   * Set the properties of an object from the given attribute list.
   * @param handler The stylesheet's Content handler, needed for
   *                error reporting.
   * @param rawName The raw name of the owner element, needed for
   *                error reporting.
   * @param attributes The list of attributes.
   * @param target The target element where the properties will be set.
   */
  void setPropertiesFromAttributes(
          StylesheetHandler handler, String rawName, Attributes attributes, 
          org.apache.xalan.templates.ElemTemplateElement target)
            throws org.xml.sax.SAXException
  {

    XSLTElementDef def = getElemDef();

    // Keep track of which XSLTAttributeDefs have been processed, so 
    // I can see which default values need to be set.
    Vector processedDefs = new Vector();
    int nAttrs = attributes.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      String attrUri = attributes.getURI(i);
      String attrLocalName = attributes.getLocalName(i);
      XSLTAttributeDef attrDef = def.getAttributeDef(attrUri, attrLocalName);

      if (null == attrDef)
      {

        // Then barf, because this element does not allow this attribute.
        handler.error(attributes.getQName(i)
                      + "attribute is not allowed on the " + rawName
                      + " element!", null);
      }
      else
      {
        String valueString = attributes.getValue(i);

        if (valueString.indexOf(org.apache.xpath.compiler.Keywords.FUNC_KEY_STRING
                                + "(") >= 0)
          handler.error(
            XSLMessages.createMessage(
            XSLTErrorResources.ER_INVALID_KEY_CALL, null), null);

        processedDefs.addElement(attrDef);
        attrDef.setAttrValue(handler, attrUri, attrLocalName,
                             attributes.getQName(i), attributes.getValue(i),
                             target);
      }
    }

    XSLTAttributeDef[] attrDefs = def.getAttributes();
    int nAttrDefs = attrDefs.length;

    for (int i = 0; i < nAttrDefs; i++)
    {
      XSLTAttributeDef attrDef = attrDefs[i];
      String defVal = attrDef.getDefault();

      if (null != defVal)
      {
        if (!processedDefs.contains(attrDef))
        {
          attrDef.setDefAttrValue(handler, target);
        }
      }

      if (attrDef.getRequired())
      {
        if (!processedDefs.contains(attrDef))
          handler.error(
            XSLMessages.createMessage(
              XSLTErrorResources.ER_REQUIRES_ATTRIB, new Object[]{ rawName,
                                                                   attrDef.getName() }), null);
      }
    }
  }
}
