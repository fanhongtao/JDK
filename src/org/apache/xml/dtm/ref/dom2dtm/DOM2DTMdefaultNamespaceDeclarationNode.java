/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.dtm.ref.dom2dtm;

import org.w3c.dom.*;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.dtm.ref.*;
import org.apache.xml.dtm.*;

/** This is a kluge to let us shove a declaration for xml: into the
 * DOM2DTM model.  Basically, it creates a proxy node in DOM space to
 * carry the additional information. This is _NOT_ a full DOM
 * implementation, and shouldn't be one since it sits alongside the
 * DOM rather than becoming part of the DOM model.
 * 
 * (This used to be an internal class within DOM2DTM. Moved out because
 * I need to perform an instanceof operation on it to support a temporary
 * workaround in DTMManagerDefault.)
 * 
 * %REVIEW% What if the DOM2DTM was built around a DocumentFragment and
 * there isn't a single root element? I think this fails that case...
 * 
 * %REVIEW% An alternative solution would be to create the node _only_
 * in DTM space, but given how DOM2DTM is currently written I think
 * this is simplest.
 * */
public class DOM2DTMdefaultNamespaceDeclarationNode implements Attr
{
  final String NOT_SUPPORTED_ERR="Unsupported operation on pseudonode";
  
  Element pseudoparent;
  String prefix,uri,nodename;
  int handle;
  DOM2DTMdefaultNamespaceDeclarationNode(Element pseudoparent,String prefix,String uri,int handle)
  {
    this.pseudoparent=pseudoparent;
    this.prefix=prefix;
    this.uri=uri;
    this.handle=handle;
    this.nodename="xmlns:"+prefix;
  }
  public String getNodeName() {return nodename;}
  public String getName() {return nodename;}
  public String getNamespaceURI() {return "http://www.w3.org/2000/xmlns/";}
  public String getPrefix() {return prefix;}
  public String getLocalName() {return prefix;}
  public String getNodeValue() {return uri;}
  public String getValue() {return uri;}
  public Element getOwnerElement() {return pseudoparent;}
  
  public boolean isSupported(String feature, String version) {return false;}
  public boolean hasChildNodes() {return false;}
  public boolean hasAttributes() {return false;}
  public Node getParentNode() {return null;}
  public Node getFirstChild() {return null;}
  public Node getLastChild() {return null;}
  public Node getPreviousSibling() {return null;}
  public Node getNextSibling() {return null;}
  public boolean getSpecified() {return false;}
  public void normalize() {return;}
  public NodeList getChildNodes() {return null;}
  public NamedNodeMap getAttributes() {return null;}
  public short getNodeType() {return Node.ATTRIBUTE_NODE;}
  public void setNodeValue(String value) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public void setValue(String value) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public void setPrefix(String value) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public Node insertBefore(Node a, Node b) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public Node replaceChild(Node a, Node b) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public Node appendChild(Node a) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public Node removeChild(Node a) {throw new DTMException(NOT_SUPPORTED_ERR);}
  public Document getOwnerDocument() {return pseudoparent.getOwnerDocument();}
  public Node cloneNode(boolean deep) {throw new DTMException(NOT_SUPPORTED_ERR);}
	
	/** Non-DOM method, part of the temporary kluge
	 * %REVIEW% This would be a pruning problem, but since it will always be
	 * added to the root element and we prune on elements, we shouldn't have 
	 * to worry.
	 */
	public int getHandleOfNode()		
	{
		return handle;
	}
}

