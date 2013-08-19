package org.apache.xalan.templates;

//import org.w3c.dom.*;
import org.apache.xml.dtm.DTM;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObjectFactory;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.apache.xml.utils.QName;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.TransformerException;

/**
 * Handles the EXSLT result element within an EXSLT function element.
 */
public class ElemExsltFuncResult extends ElemVariable
{
 
  /**
   * Generate the EXSLT function return value, and assign it to the variable
   * index slot assigned for it in ElemExsltFunction compose().
   * 
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {    
    XPathContext context = transformer.getXPathContext();
    VariableStack varStack = context.getVarStack();
    // ElemExsltFunc result should always be within an ElemExsltFunction.
    ElemExsltFunction owner = getOwnerFunction();
    if (owner != null)
    {
      int resultIndex = owner.getResultIndex();
      // Verify that result has not already been set by another result
      // element. Recursion is allowed: intermediate results are cleared 
      // in the owner ElemExsltFunction execute().
      if (varStack.isLocalSet(resultIndex))
        throw new TransformerException
          ("An EXSLT function cannot set more than one result!");
      int sourceNode = context.getCurrentNode();
      // Set the return value;
      XObject var = getValue(transformer, sourceNode);   
      varStack.setLocalVariable(resultIndex, var);
    }    
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.EXSLT_ELEMNAME_FUNCRESULT;
  }
  
  /**
   * Return the node name, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants.
   * @return The node name
   * 
   */
   public String getNodeName()
  {
    return Constants.EXSLT_ELEMNAME_FUNCRESULT_STRING;
  }
  
  /**
   * Get the ElemExsltFunction that contains the ElemResult so we can set an ElemExsltFunction variable
   * to the local variable stack index to the return value.
   */
  public ElemExsltFunction getOwnerFunction()
  {
  	ElemTemplateElement elem = this;
  	while((elem != null) && !(elem instanceof ElemExsltFunction))
  	{
    	elem = elem.getParentElem();
  	}
  	return (ElemExsltFunction)elem;
  }
  
}
