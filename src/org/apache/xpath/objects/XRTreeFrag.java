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
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.w3c.dom.NodeList;

import org.apache.xml.dtm.Axis;
import org.apache.xpath.axes.RTFIterator;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath result tree fragment object, and is capable of
 * converting the RTF to other types, such as a string.
 */
public class XRTreeFrag extends XObject implements Cloneable
{
  DTM m_dtm;
  int m_dtmRoot;
  XPathContext m_xctxt;
  boolean m_allowRelease = false;

//  /**
//   * Create an XRTreeFrag Object.
//   *
//   * @param frag Document fragment this will wrap
//   */
//  public XRTreeFrag(DTMIterator frag)
//  {
//    super(frag);
//    
//    // Obviously, this constructor should be avoided when possible.
//    m_dtmRoot = frag.cloneWithReset().nextNode();
//  }
  
  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  public XRTreeFrag(int root, XPathContext xctxt, ExpressionNode parent)
  {
    super(null);
    
    // Obviously, this constructor should be avoided when possible.
    exprSetParent(parent);
    m_dtmRoot = root;
    m_xctxt = xctxt;
    m_dtm = xctxt.getDTM(root);
  }
  
  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  public XRTreeFrag(int root, XPathContext xctxt)
  {
    super(null);
    
    // Obviously, this constructor should be avoided when possible.
    m_dtmRoot = root;
    m_xctxt = xctxt;
    m_dtm = xctxt.getDTM(root);
  }

  
  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    if (m_xctxt != null)
      return new org.apache.xml.dtm.ref.DTMNodeIterator((DTMIterator)(new org.apache.xpath.NodeSetDTM(m_dtmRoot, m_xctxt.getDTMManager())));
    else
      return super.object();
  }
  
  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  public XRTreeFrag(Expression expr)
  {
    super(expr);
  }
  
  /**
   * Release any resources this object may have by calling destruct().
   * %ISSUE% This release will occur asynchronously. Resources it manipulates
   * MUST be thread-safe!
   *
   * @throws Throwable
   */
  protected void finalize() throws Throwable
  {
    try
    {
      destruct();
    }
    finally
    {
      super.finalize();  // Always use this.
    }
  }
  
  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   * 
   * @param allowRelease true if it is OK for detach to release this iterator 
   * for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
    m_allowRelease = allowRelease;
  }

  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   * 
   * In general, detach should only be called once on the object.
   */
  public void detach()
  {
    if(m_allowRelease)
    {
    	// %REVIEW% Do we actually _need_ detach, now that DTM RTF
    	// storage is managed as a stack?
      // See #destruct() for a comment about this next check.
      int ident = m_xctxt.getDTMIdentity(m_dtm);
      DTM foundDTM = m_xctxt.getDTM(ident);      
      if(foundDTM == m_dtm)
      {
        m_xctxt.release(m_dtm, true);
        m_dtm = null;
        m_xctxt = null;
      }
      m_obj = null;
    }
  }
  
  /**
   * Forces the object to release it's resources.  This is more harsh than 
   * detach().  You can call destruct as many times as you want.
   */
  public void destruct()
  {
    if(null != m_dtm)
    {
      // For this next check, see http://nagoya.apache.org/bugzilla/show_bug.cgi?id=7622.
      // What happens if you don't do this this check:
      // 1) Transform#1 creates an XRTreeFrag.  This has a reference to a DTM, that in turn 
      //    is registered with a DTMManager.  The DTM will need to be deleted from the 
      //    DTMManager when the XRTreeFrag is deleted.  The XRTreeFrag  also contains a 
      //    reference to the XPathContext.
      // 2) Transform#1 completes.  The XPathContext is reset... namely the a bunch 
      //    of structures are reset or rebuilt, including DTMManagerDefault#m_dtms.  
      //    BUT, the XRTreeFrags are still hanging around, waiting to unregister themselves.
      // 3) Transform#2 starts humming along.  It builds a XRTreeFrag and installs that 
      //    RTF DTM into DTMManagerDefault#m_dtms[2].
      // 4) The finalizer thread wakes and decides to delete some of those old XRTreeFrags 
      //    from Transform#1.
      // 5) The XRTreeFrag#finalize() method references through the XPathContext, and 
      //    deletes what it thinks is it's DTM from  DTMManagerDefault#m_dtms[2] (via 
      //    getDTMIdentity(dtm)).
      // 6) Transform#2 tries to reference DTMManagerDefault#m_dtms[2], finds it is 
      //    null, and chaos results.
      int ident = m_xctxt.getDTMIdentity(m_dtm);
      DTM foundDTM = m_xctxt.getDTM(ident);      
      if(foundDTM == m_dtm)
      {
        m_xctxt.release(m_dtm, true);
        m_dtm = null;
        m_xctxt = null;
      }
    }
    m_obj = null;
 }

  /**
   * Tell what kind of class this is.
   *
   * @return type CLASS_RTREEFRAG 
   */
  public int getType()
  {
    return CLASS_RTREEFRAG;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#RTREEFRAG"
   */
  public String getTypeString()
  {
    return "#RTREEFRAG";
  }

  /**
   * Cast result object to a number.
   *
   * @return The result tree fragment as a number or NaN
   */
  public double num()
    throws javax.xml.transform.TransformerException
  {

    XMLString s = xstr();

    return s.toDouble();
  }

  /**
   * Cast result object to a boolean.  This always returns true for a RTreeFrag
   * because it is treated like a node-set with a single root node.
   *
   * @return true
   */
  public boolean bool()
  {
    return true;
  }
  
  private XMLString m_xmlStr = null;
  
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    if(null == m_xmlStr)
      m_xmlStr = m_dtm.getStringValue(m_dtmRoot);
    
    return m_xmlStr;
  }
  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb)
  {
    XString xstring = (XString)xstr();
    xstring.appendToFsb(fsb);
  }


  /**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    String str = m_dtm.getStringValue(m_dtmRoot).toString();

    return (null == str) ? "" : str;
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @return The document fragment this wraps
   */
  public int rtf()
  {
    return m_dtmRoot;
  }

  /**
   * Cast result object to a DTMIterator.
   * dml - modified to return an RTFIterator for
   * benefit of EXSLT object-type function in 
   * {@link org.apache.xalan.lib.ExsltCommon}.
   * @return The document fragment as a DTMIterator
   */
  public DTMIterator asNodeIterator()
  {
    return new RTFIterator(m_dtmRoot, m_xctxt.getDTMManager());
  }

  /**
   * Cast result object to a nodelist. (special function).
   *
   * @return The document fragment as a nodelist
   */
  public NodeList convertToNodeset()
  {

    if (m_obj instanceof NodeList)
      return (NodeList) m_obj;
    else
      return new org.apache.xml.dtm.ref.DTMNodeList(asNodeIterator());
  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if the two objects are equal
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {

    try
    {
      if (XObject.CLASS_NODESET == obj2.getType())
      {
  
        // In order to handle the 'all' semantics of 
        // nodeset comparisons, we always call the 
        // nodeset function.
        return obj2.equals(this);
      }
      else if (XObject.CLASS_BOOLEAN == obj2.getType())
      {
        return bool() == obj2.bool();
      }
      else if (XObject.CLASS_NUMBER == obj2.getType())
      {
        return num() == obj2.num();
      }
      else if (XObject.CLASS_NODESET == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_STRING == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_RTREEFRAG == obj2.getType())
      {
  
        // Probably not so good.  Think about this.
        return xstr().equals(obj2.xstr());
      }
      else
      {
        return super.equals(obj2);
      }
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }

}
