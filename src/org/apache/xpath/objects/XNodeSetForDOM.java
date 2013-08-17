package org.apache.xpath.objects;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xml.dtm.*;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;

/**
 * This class overrides the XNodeSet#object() method to provide the original 
 * Node object, NodeList object, or NodeIterator.
 */
public class XNodeSetForDOM extends XNodeSet
{
  Object m_origObj;

  public XNodeSetForDOM(Node node, DTMManager dtmMgr)
  {
    m_dtmMgr = dtmMgr;
    m_origObj = node;
    int dtmHandle = dtmMgr.getDTMHandleFromNode(node);
    m_obj = new NodeSetDTM(dtmMgr);
    ((NodeSetDTM) m_obj).addNode(dtmHandle);
  }
  
  public XNodeSetForDOM(NodeList nodeList, XPathContext xctxt)
  {
    m_dtmMgr = xctxt.getDTMManager();
    m_origObj = nodeList;
    m_obj = new org.apache.xpath.NodeSetDTM(nodeList, xctxt);
  }

  public XNodeSetForDOM(NodeIterator nodeIter, XPathContext xctxt)
  {
    m_dtmMgr = xctxt.getDTMManager();
    m_origObj = nodeIter;
    m_obj = new org.apache.xpath.NodeSetDTM(nodeIter, xctxt);
  }
  
  /**
   * Return the original DOM object that the user passed in.  For use primarily
   * by the extension mechanism.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    return m_origObj;
  }
  
  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeIterator) 
                   ? (NodeIterator)m_origObj : super.nodeset();      
  }
  
  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeList) 
                   ? (NodeList)m_origObj : super.nodelist();      
  }



}