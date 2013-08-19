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

import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.XPath;
import org.apache.xalan.templates.StylesheetRoot;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.TransformerConfigurationException;
import org.apache.xml.utils.SAXSourceLocator;

import java.util.Vector;

/**
 * <meta name="usage" content="internal"/>
 * Processes an XSLT literal-result-element, or something that looks 
 * like one.  The actual {@link org.apache.xalan.templates.ElemTemplateElement}
 * produced may be a {@link org.apache.xalan.templates.ElemLiteralResult}, 
 * a {@link org.apache.xalan.templates.StylesheetRoot}, or a 
 * {@link org.apache.xalan.templates.ElemExtensionCall}.
 * 
 * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
 * @see org.apache.xalan.templates.ElemLiteralResult
 */
public class ProcessorLRE extends ProcessorTemplateElem
{

  /**
   * Receive notification of the start of an element.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param attributes The specified or defaulted attributes.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {

    try
    {
      ElemTemplateElement p = handler.getElemTemplateElement();
      boolean excludeXSLDecl = false;
      boolean isLREAsStyleSheet = false;

      if (null == p)
      {

        // Literal Result Template as stylesheet.
        XSLTElementProcessor lreProcessor = handler.popProcessor();
        XSLTElementProcessor stylesheetProcessor =
                                                  handler.getProcessorFor(Constants.S_XSLNAMESPACEURL, "stylesheet",
                                                                          "xsl:stylesheet");

        handler.pushProcessor(lreProcessor);

        Stylesheet stylesheet;
        try
        {
          stylesheet = new StylesheetRoot(handler.getSchema(), handler.getStylesheetProcessor().getErrorListener());
        }
        catch(TransformerConfigurationException tfe)
        {
          throw new TransformerException(tfe);
        }

        // stylesheet.setDOMBackPointer(handler.getOriginatingNode());
        // ***** Note that we're assigning an empty locator. Is this necessary?
        SAXSourceLocator slocator = new SAXSourceLocator();
        Locator locator = handler.getLocator();
        if(null != locator)
        {
          slocator.setLineNumber(locator.getLineNumber());
          slocator.setColumnNumber(locator.getColumnNumber());
          slocator.setPublicId(locator.getPublicId());
          slocator.setSystemId(locator.getSystemId());
        }
        stylesheet.setLocaterInfo(slocator);
        stylesheet.setPrefixes(handler.getNamespaceSupport());
        handler.pushStylesheet(stylesheet);

        isLREAsStyleSheet = true;

        AttributesImpl stylesheetAttrs = new AttributesImpl();
        AttributesImpl lreAttrs = new AttributesImpl();
        int n = attributes.getLength();

        for (int i = 0; i < n; i++)
        {
          String attrLocalName = attributes.getLocalName(i);
          String attrUri = attributes.getURI(i);
          String value = attributes.getValue(i);

          if ((null != attrUri) && attrUri.equals(Constants.S_XSLNAMESPACEURL))
          {
            stylesheetAttrs.addAttribute(null, attrLocalName, attrLocalName,
                                         attributes.getType(i),
                                         attributes.getValue(i));
          }
          else if ((attrLocalName.startsWith("xmlns:") || attrLocalName.equals(
                                                                               "xmlns")) && value.equals(Constants.S_XSLNAMESPACEURL))
          {

            // ignore
          }
          else
          {
            lreAttrs.addAttribute(attrUri, attrLocalName,
                                  attributes.getQName(i),
                                  attributes.getType(i),
                                  attributes.getValue(i));
          }
        }

        attributes = lreAttrs;

        // Set properties from the attributes, but don't throw 
        // an error if there is an attribute defined that is not 
        // allowed on a stylesheet.
				try{
        stylesheetProcessor.setPropertiesFromAttributes(handler, "stylesheet",
                                                        stylesheetAttrs, stylesheet);
				}
				catch (Exception e)
				{
					// This is pretty ugly, but it will have to do for now. 
					// This is just trying to append some text specifying that
					// this error came from a missing or invalid XSLT namespace
					// declaration.
					// If someone comes up with a better solution, please feel 
					// free to contribute it. -mm
					String msg = e.getMessage();
					if (stylesheet.getDeclaredPrefixes() == null || 
						!declaredXSLNS(stylesheet))
					{
						msg = msg +"; " + XSLMessages.createWarning(XSLTErrorResources.WG_OLD_XSLT_NS, null);
						
					}
					//else
						throw new org.xml.sax.SAXException(msg, e);
				}
        handler.pushElemTemplateElement(stylesheet);

        ElemTemplate template = new ElemTemplate();

        appendAndPush(handler, template);

        XPath rootMatch = new XPath("/", stylesheet, stylesheet, XPath.MATCH, 
             handler.getStylesheetProcessor().getErrorListener());

        template.setMatch(rootMatch);

        // template.setDOMBackPointer(handler.getOriginatingNode());
        stylesheet.setTemplate(template);

        p = handler.getElemTemplateElement();
        excludeXSLDecl = true;
      }

      XSLTElementDef def = getElemDef();
      Class classObject = def.getClassObject();
      boolean isExtension = false;
      boolean isComponentDecl = false;
      boolean isUnknownTopLevel = false;

      while (null != p)
      {

        // System.out.println("Checking: "+p);
        if (p instanceof ElemLiteralResult)
        {
          ElemLiteralResult parentElem = (ElemLiteralResult) p;

          isExtension = parentElem.containsExtensionElementURI(uri);
        }
        else if (p instanceof Stylesheet)
        {
          Stylesheet parentElem = (Stylesheet) p;

          isExtension = parentElem.containsExtensionElementURI(uri);

          if ((false == isExtension) && (null != uri)
              && (uri.equals(Constants.S_BUILTIN_EXTENSIONS_URL)
                  || uri.equals(Constants.S_BUILTIN_OLD_EXTENSIONS_URL)))
          {
            isComponentDecl = true;
          }
          else
          {
            isUnknownTopLevel = true;
          }
        }

        if (isExtension)
          break;

        p = p.getParentElem();
      }

      ElemTemplateElement elem = null;

      try
      {
        if (isExtension)
        {

          // System.out.println("Creating extension(1): "+uri);
          elem = new ElemExtensionCall();
        }
        else if (isComponentDecl)
        {
          elem = (ElemTemplateElement) classObject.newInstance();
        }
        else if (isUnknownTopLevel)
        {

          // TBD: Investigate, not sure about this.  -sb
          elem = (ElemTemplateElement) classObject.newInstance();
        }
        else
        {
          elem = (ElemTemplateElement) classObject.newInstance();
        }

        elem.setDOMBackPointer(handler.getOriginatingNode());
        elem.setLocaterInfo(handler.getLocator());
        elem.setPrefixes(handler.getNamespaceSupport(), excludeXSLDecl);

        if (elem instanceof ElemLiteralResult)
        {
          ((ElemLiteralResult) elem).setNamespace(uri);
          ((ElemLiteralResult) elem).setLocalName(localName);
          ((ElemLiteralResult) elem).setRawName(rawName);
          ((ElemLiteralResult) elem).setIsLiteralResultAsStylesheet(
                                                                    isLREAsStyleSheet);
        }
      }
      catch (InstantiationException ie)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMLITRSLT, null, ie);//"Failed creating ElemLiteralResult instance!", ie);
      }
      catch (IllegalAccessException iae)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMLITRSLT, null, iae);//"Failed creating ElemLiteralResult instance!", iae);
      }

      setPropertiesFromAttributes(handler, rawName, attributes, elem);

      // bit of a hack here...
      if (!isExtension && (elem instanceof ElemLiteralResult))
      {
        isExtension =
                     ((ElemLiteralResult) elem).containsExtensionElementURI(uri);

        if (isExtension)
        {

          // System.out.println("Creating extension(2): "+uri);
          elem = new ElemExtensionCall();

          elem.setLocaterInfo(handler.getLocator());
          elem.setPrefixes(handler.getNamespaceSupport());
          ((ElemLiteralResult) elem).setNamespace(uri);
          ((ElemLiteralResult) elem).setLocalName(localName);
          ((ElemLiteralResult) elem).setRawName(rawName);
          setPropertiesFromAttributes(handler, rawName, attributes, elem);
        }
      }

      appendAndPush(handler, elem);
    }
    catch(TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws org.xml.sax.SAXException
  {

    ElemTemplateElement elem = handler.getElemTemplateElement();

    if (elem instanceof ElemLiteralResult)
    {
      if (((ElemLiteralResult) elem).getIsLiteralResultAsStylesheet())
      {
        handler.popStylesheet();
      }
    }

    super.endElement(handler, uri, localName, rawName);
  }
	
	private boolean declaredXSLNS(Stylesheet stylesheet)
	{
		Vector declaredPrefixes = stylesheet.getDeclaredPrefixes();
		int n = declaredPrefixes.size();

		for (int i = 0; i < n; i++)
		{
			XMLNSDecl decl = (XMLNSDecl) declaredPrefixes.elementAt(i);
			if(decl.getURI().equals(Constants.S_XSLNAMESPACEURL))
				return true;
		}
		return false;
	}
}
