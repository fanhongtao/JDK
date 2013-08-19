package org.apache.xalan.processor;

import org.apache.xalan.templates.WhiteSpaceInfo;
import java.util.Vector;
import org.apache.xalan.templates.Stylesheet;

public class WhitespaceInfoPaths extends WhiteSpaceInfo
{
	
  /**
   * Bean property to allow setPropertiesFromAttributes to
   * get the elements attribute.
   */
  private Vector m_elements;

  /**
   * Set from the elements attribute.  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @param elems Should be a non-null reference to a list 
   *              of {@link org.apache.xpath.XPath} objects.
   */
  public void setElements(Vector elems)
  {
    m_elements = elems;
  }

  /**
   * Get the property set by setElements().  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @return A reference to a list of {@link org.apache.xpath.XPath} objects, 
   *         or null.
   */
  Vector getElements()
  {
    return m_elements;
  }
  
  public void clearElements()
  {
  	m_elements = null;
  }

 /**
   * Constructor WhitespaceInfoPaths
   *
   * @param thisSheet The current stylesheet
   */
  public WhitespaceInfoPaths(Stylesheet thisSheet)
  {
  	super(thisSheet);
  	setStylesheet(thisSheet);
  }


}

