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
package org.apache.xalan.lib;
// Imported classes for Extension elements
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.transformer.TransformerImpl;

// Imported JAXP/TrAX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;

// Imported JAXP parsing classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

// Imported DOM classes
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Imported Serializer classes
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;

// Other org.apache imports
import org.apache.xpath.XPathContext;
import org.apache.xml.utils.SystemIDResolver;

// Imported java.io and java.util classes
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Properties;

/**
 * PipeDocument is a Xalan extension element to set stylesheet params and pipes an XML 
 * document through a series of 1 or more stylesheets.
 * PipeDocument is invoked from a stylesheet as the {@link #pipeDocument pipeDocument extension element}.
 * 
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:pipe="http://xml.apache.org/xalan/PipeDocument"
 * </pre>
 *
 * @author Donald Leslie
 */
public class PipeDocument
{
/**
 * Extension element for piping an XML document through a series of 1 or more transformations.
 * 
 * <pre>Common usage pattern: A stylesheet transforms a listing of documents to be
 * transformed into a TOC. For each document in the listing calls the pipeDocument
 * extension element to pipe that document through a series of 1 or more stylesheets 
 * to the desired output document.
 * 
 * Syntax:
 * &lt;xsl:stylesheet version="1.0"
 *                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 *                xmlns:pipe="http://xml.apache.org/xalan/PipeDocument"
 *                extension-element-prefixes="pipe"&gt;
 * ...
 * &lt;pipe:pipeDocument   source="source.xml" target="target.xml"&gt;
 *   &lt;stylesheet href="ss1.xsl"&gt;
 *     &lt;param name="param1" value="value1"/&gt;
 *   &lt;/stylesheet&gt;
 *   &lt;stylesheet href="ss2.xsl"&gt;
 *     &lt;param name="param1" value="value1"/&gt;
 *     &lt;param name="param2" value="value2"/&gt;
 *   &lt;/stylesheet&gt;
 *   &lt;stylesheet href="ss1.xsl"/&gt;     
 * &lt;/pipe:pipeDocument&gt;
 * 
 * Notes:</pre>
 * <ul>
 *   <li>The base URI for the source attribute is the XML "listing" document.<li/>
 *   <li>The target attribute is taken as is (base is the current user directory).<li/>
 *   <li>The stylsheet containg the extension element is the base URI for the
 *   stylesheet hrefs.<li/>
 * </ul>
 */
  public void pipeDocument(XSLProcessorContext context, ElemExtensionCall elem)
	  throws TransformerException, TransformerConfigurationException, 
         SAXException, IOException, FileNotFoundException	   
  {
    try
    {
      SAXTransformerFactory saxTFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
      
      // XML doc to transform.
      String source =  elem.getAttribute("source", 
                                         context.getContextNode(),
                                         context.getTransformer());
      TransformerImpl transImpl = context.getTransformer();

      //Base URI for input doc, so base for relative URI to XML doc to transform.
      String baseURLOfSource = transImpl.getBaseURLOfSource();
      // Absolute URI for XML doc to transform.
      String absSourceURL = SystemIDResolver.getAbsoluteURI(source, baseURLOfSource);      

      // Transformation target
      String target =  elem.getAttribute("target", 
                                         context.getContextNode(),
                                         context.getTransformer());
      
      XPathContext xctxt = context.getTransformer().getXPathContext();
      int xt = xctxt.getDTMHandleFromNode(context.getContextNode());
 
      // Get System Id for stylesheet; to be used to resolve URIs to other stylesheets.
      String sysId = elem.getSystemId();
      
      NodeList ssNodes = null;
      NodeList paramNodes = null;
      Node ssNode = null;
      Node paramNode = null;
      if (elem.hasChildNodes())
      {
        ssNodes = elem.getChildNodes();        
        // Vector to contain TransformerHandler for each stylesheet.
        Vector vTHandler = new Vector(ssNodes.getLength());
        
        // The child nodes of an extension element node are instances of
        // ElemLiteralResult, which requires does not fully support the standard
        // Node interface. Accordingly, some special handling is required (see below)
        // to get attribute values.
        for (int i = 0; i < ssNodes.getLength(); i++)
        {
          ssNode = ssNodes.item(i);
          if (ssNode.getNodeType() == ssNode.ELEMENT_NODE
              && ((Element)ssNode).getTagName().equals("stylesheet")
              && ssNode instanceof ElemLiteralResult)
          {
            AVT avt = ((ElemLiteralResult)ssNode).getLiteralResultAttribute("href");
            String href = avt.evaluate(xctxt,xt, elem);
            String absURI = SystemIDResolver.getAbsoluteURI(href, sysId);
            Templates tmpl = saxTFactory.newTemplates(new StreamSource(absURI));
            TransformerHandler tHandler = saxTFactory.newTransformerHandler(tmpl);
            Transformer trans = tHandler.getTransformer();
            
            // AddTransformerHandler to vector
            vTHandler.addElement(tHandler);

            paramNodes = ssNode.getChildNodes();
            for (int j = 0; j < paramNodes.getLength(); j++)
            {
              paramNode = paramNodes.item(j);
              if (paramNode.getNodeType() == paramNode.ELEMENT_NODE 
                  && ((Element)paramNode).getTagName().equals("param")
                  && paramNode instanceof ElemLiteralResult)
              {
                 avt = ((ElemLiteralResult)paramNode).getLiteralResultAttribute("name");
                 String pName = avt.evaluate(xctxt,xt, elem);
                 avt = ((ElemLiteralResult)paramNode).getLiteralResultAttribute("value");
                 String pValue = avt.evaluate(xctxt,xt, elem);
                 trans.setParameter(pName, pValue);
               } 
             }
           }
         }
         usePipe(vTHandler, absSourceURL, target);
       }
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
  }
  /**
   * Uses a Vector of TransformerHandlers to pipe XML input document through
   * a series of 1 or more transformations. Called by {@link #pipeDocument} and {@link #main}.
   * 
   * @param vTHandler Vector of Transformation Handlers (1 per stylesheet).
   * @param source absolute URI to XML input
   * @param target absolute path to transformation output.
   */
  public void usePipe(Vector vTHandler, String source, String target)
          throws TransformerException, TransformerConfigurationException, 
                 FileNotFoundException, IOException, SAXException, SAXNotRecognizedException
  {
    XMLReader reader = XMLReaderFactory.createXMLReader();
    TransformerHandler tHFirst = (TransformerHandler)vTHandler.firstElement();
    reader.setContentHandler(tHFirst);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", tHFirst);
    for (int i = 1; i < vTHandler.size(); i++)
    {
      TransformerHandler tHFrom = (TransformerHandler)vTHandler.elementAt(i-1);
      TransformerHandler tHTo = (TransformerHandler)vTHandler.elementAt(i);
      tHFrom.setResult(new SAXResult(tHTo));      
    }
    TransformerHandler tHLast = (TransformerHandler)vTHandler.lastElement();
    Transformer trans = tHLast.getTransformer();
    Properties outputProps = trans.getOutputProperties();
    Serializer serializer = SerializerFactory.getSerializer(outputProps);
    serializer.setOutputStream(new FileOutputStream(target));
    tHLast.setResult(new SAXResult(serializer.asContentHandler()));
    
    reader.parse(source);
  }
 
}