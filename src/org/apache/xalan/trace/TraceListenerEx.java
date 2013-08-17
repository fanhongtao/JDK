package org.apache.xalan.trace;

/**
 * <meta name="usage" content="advanced"/>
 * Extends TraceListener but adds a SelectEnd event.
 */
public interface TraceListenerEx extends TraceListener
{

  /**
   * Method that is called after an xsl:apply-templates or xsl:for-each 
   * selection occurs.
   *
   * @param ev the generate event.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void selectEnd(EndSelectionEvent ev) throws javax.xml.transform.TransformerException;

}
