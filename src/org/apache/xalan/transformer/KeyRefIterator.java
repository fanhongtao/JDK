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
package org.apache.xalan.transformer;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import java.util.Vector;
import org.apache.xml.utils.QName;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * This class filters nodes from a key iterator, according to 
 * whether or not the use value matches the ref value.  
 */
public class KeyRefIterator extends org.apache.xpath.axes.ChildTestIterator
{
  /**
   * Constructor KeyRefIterator
   *
   *
   * @param ref Key value to match
   * @param ki The main key iterator used to walk the source tree 
   */
  public KeyRefIterator(QName name, XMLString ref, Vector keyDecls, DTMIterator ki)
  {
    super(null);
    m_name = name;
    m_ref = ref;
    m_keyDeclarations = keyDecls;
    m_keysNodes = ki;
    setWhatToShow(org.apache.xml.dtm.DTMFilter.SHOW_ALL);
  }
  
  DTMIterator m_keysNodes;
  
  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected int getNextNode()
  {                  
  	int next;   
    while(DTM.NULL != (next = m_keysNodes.nextNode()))
    {
    	if(DTMIterator.FILTER_ACCEPT == filterNode(next))
    		break;
    }
    m_lastFetched = next;
    
    return next;
  }


  /**
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * 
   * @param testnode  The node to check to see if it passes the filter or not.
   *
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short filterNode(int testNode)
  {
    boolean foundKey = false;
    Vector keys = m_keyDeclarations;

    QName name = m_name;
    KeyIterator ki = (KeyIterator)(((XNodeSet)m_keysNodes).getContainedIter());
    org.apache.xpath.XPathContext xctxt = ki.getXPathContext();
    
    if(null == xctxt)
    	assertion(false, "xctxt can not be null here!");

    try
    {
      XMLString lookupKey = m_ref;

      // System.out.println("lookupKey: "+lookupKey);
      int nDeclarations = keys.size();

      // Walk through each of the declarations made with xsl:key
      for (int i = 0; i < nDeclarations; i++)
      {
        KeyDeclaration kd = (KeyDeclaration) keys.elementAt(i);

        // Only continue if the name on this key declaration
        // matches the name on the iterator for this walker. 
        if (!kd.getName().equals(name))
          continue;

        foundKey = true;
        // xctxt.setNamespaceContext(ki.getPrefixResolver());

        // Query from the node, according the the select pattern in the
        // use attribute in xsl:key.
        XObject xuse = kd.getUse().execute(xctxt, testNode, ki.getPrefixResolver());

        if (xuse.getType() != xuse.CLASS_NODESET)
        {
          XMLString exprResult = xuse.xstr();

          if (lookupKey.equals(exprResult))
            return DTMIterator.FILTER_ACCEPT;
        }
        else
        {
          DTMIterator nl = ((XNodeSet)xuse).iterRaw();
          int useNode;
          
          while (DTM.NULL != (useNode = nl.nextNode()))
          {
            DTM dtm = getDTM(useNode);
            XMLString exprResult = dtm.getStringValue(useNode);
            if ((null != exprResult) && lookupKey.equals(exprResult))
              return DTMIterator.FILTER_ACCEPT;
          }
        }

      } // end for(int i = 0; i < nDeclarations; i++)
    }
    catch (javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }

    if (!foundKey)
      throw new RuntimeException(
        XSLMessages.createMessage(
          XSLTErrorResources.ER_NO_XSLKEY_DECLARATION,
          new Object[] { name.getLocalName()}));
    return DTMIterator.FILTER_REJECT;
  }

  protected XMLString m_ref;
  protected QName m_name;

  /** Vector of Key declarations in the stylesheet.
   *  @serial          */
  protected Vector m_keyDeclarations;

}