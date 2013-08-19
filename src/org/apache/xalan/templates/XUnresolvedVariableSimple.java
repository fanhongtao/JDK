package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;


/**
 * This is the same as XUnresolvedVariable, but it assumes that the 
 * context is already set up.  For use with psuedo variables.
 * Also, it holds an Expression object, instead of an ElemVariable.
 * It must only hold static context, since a single copy will be 
 * held in the template.
 */
public class XUnresolvedVariableSimple extends XObject
{
  public XUnresolvedVariableSimple(ElemVariable obj)
  {
    super(obj);
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
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
  	Expression expr = ((ElemVariable)m_obj).getSelect().getExpression();
    XObject xobj = expr.execute(xctxt);
    xobj.allowDetachToRelease(false);
    return xobj;
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNRESOLVEDVARIABLE
   */
  public int getType()
  {
    return CLASS_UNRESOLVEDVARIABLE;
  }
  
  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return An informational string.
   */
  public String getTypeString()
  {
    return "XUnresolvedVariableSimple (" + object().getClass().getName() + ")";
  }


}

