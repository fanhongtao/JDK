package org.apache.xalan.trace;

import org.w3c.dom.*;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="advanced"/>
 * Event triggered by completion of a xsl:for-each selection or a 
 * xsl:apply-templates selection.
 */
public class EndSelectionEvent extends SelectionEvent
{

  /**
   * Create an EndSelectionEvent.
   * 
   * @param processor The XSLT TransformerFactory.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @param styleNode node in the style tree reference for the event.
   * Should not be null.  That is not enforced.
   * @param attributeName The attribute name from which the selection is made.
   * @param xpath The XPath that executed the selection.
   * @param selection The result of the selection.
   */
  public EndSelectionEvent(TransformerImpl processor, Node sourceNode,
                        ElemTemplateElement styleNode, String attributeName,
                        XPath xpath, XObject selection)
  {

    super(processor, sourceNode, styleNode, attributeName, xpath, selection);
  }
}