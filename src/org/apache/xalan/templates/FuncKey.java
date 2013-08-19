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
package org.apache.xalan.templates;

import java.util.Vector;
import java.util.Hashtable;

import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xml.utils.QName;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.KeyManager;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPathContext;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

import org.apache.xml.utils.XMLString;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Key() function.
 */
public class FuncKey extends Function2Args
{

  /** Dummy value to be used in usedrefs hashtable           */
  static private Boolean ISTRUE = new Boolean(true);

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    // TransformerImpl transformer = (TransformerImpl)xctxt;
    TransformerImpl transformer = (TransformerImpl) xctxt.getOwnerObject();
    XNodeSet nodes = null;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    int docContext = dtm.getDocumentRoot(context);

    if (DTM.NULL == docContext)
    {

      // path.error(context, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC); //"context does not have an owner document!");
    }

    String xkeyname = getArg0().execute(xctxt).str();
    QName keyname = new QName(xkeyname, xctxt.getNamespaceContext());
    XObject arg = getArg1().execute(xctxt);
    boolean argIsNodeSetDTM = (XObject.CLASS_NODESET == arg.getType());
    KeyManager kmgr = transformer.getKeyManager();
    
    // Don't bother with nodeset logic if the thing is only one node.
    if(argIsNodeSetDTM)
    {
    	XNodeSet ns = (XNodeSet)arg;
    	ns.setShouldCacheNodes(true);
    	int len = ns.getLength();
    	if(len <= 1)
    		argIsNodeSetDTM = false;
    }

    if (argIsNodeSetDTM)
    {
      Hashtable usedrefs = null;
      DTMIterator ni = arg.iter();
      int pos;
      UnionPathIterator upi = new UnionPathIterator();
      upi.exprSetParent(this);

      while (DTM.NULL != (pos = ni.nextNode()))
      {
        dtm = xctxt.getDTM(pos);
        XMLString ref = dtm.getStringValue(pos);

        if (null == ref)
          continue;

        if (null == usedrefs)
          usedrefs = new Hashtable();

        if (usedrefs.get(ref) != null)
        {
          continue;  // We already have 'em.
        }
        else
        {

          // ISTRUE being used as a dummy value.
          usedrefs.put(ref, ISTRUE);
        }

        XNodeSet nl =
          kmgr.getNodeSetDTMByKey(xctxt, docContext, keyname, ref,
                               xctxt.getNamespaceContext());
                               
        nl.setRoot(xctxt.getCurrentNode(), xctxt);

//        try
//        {
          upi.addIterator(nl);
//        }
//        catch(CloneNotSupportedException cnse)
//        {
//          // will never happen.
//        }
        //mnodeset.addNodesInDocOrder(nl, xctxt); needed??
      }

      int current = xctxt.getCurrentNode();
      upi.setRoot(current, xctxt);

      nodes = new XNodeSet(upi);
    }
    else
    {
      XMLString ref = arg.xstr();
      nodes = kmgr.getNodeSetDTMByKey(xctxt, docContext, keyname,
                                                ref,
                                                xctxt.getNamespaceContext());
      nodes.setRoot(xctxt.getCurrentNode(), xctxt);
    }

    return nodes;
  }
}
