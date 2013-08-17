package org.apache.xpath.objects;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;

import org.apache.xml.utils.XMLString;

import org.apache.xpath.DOMHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.Expression;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * This class makes an select statement act like an result tree fragment.
 */
public class XRTreeFragSelectWrapper extends XRTreeFrag implements Cloneable
{
  XObject m_selected;

  public XRTreeFragSelectWrapper(Expression expr)
  {
    super(expr);
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    ((Expression)m_obj).fixupVariables(vars, globalsSize);
  }
  
  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   *
   * @return This object.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    try
    {
      XRTreeFragSelectWrapper xrtf = (XRTreeFragSelectWrapper)this.clone();
      xrtf.m_selected = ((Expression)m_obj).execute(xctxt);
      return xrtf;
    }
    catch(CloneNotSupportedException cnse)
    {
      throw new javax.xml.transform.TransformerException(cnse);
    }
    
  }
  
  /**
   * Cast result object to a number.
   *
   * @return The result tree fragment as a number or NaN
   */
  public double num()
    throws javax.xml.transform.TransformerException
  {

    return m_selected.num();
  }

  
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    return m_selected.xstr();
  }

  /**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    return m_selected.str();
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return type CLASS_RTREEFRAG 
   */
  public int getType()
  {
    return CLASS_STRING; // hmm...
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @return The document fragment this wraps
   */
  public int rtf()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"rtf() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to a DTMIterator.
   *
   * @return The document fragment as a DTMIterator
   */
  public DTMIterator asNodeIterator()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"asNodeIterator() not supported by XRTreeFragSelectWrapper!");
  }

}
