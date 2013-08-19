/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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


package org.apache.xpath.domapi;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.xpath.*;

/**
 * <meta name="usage" content="experimental"/>
 *
 * The class provides an implementation XPathResult according 
 * to the DOM L3 XPath Specification, Working Draft 28, March 2002.
 *
 * <p>See also the <a href='http://www.w3.org/TR/2002/WD-DOM-Level-3-XPath-20020328'>Document Object Model (DOM) Level 3 XPath Specification</a>.</p>
 * 
 * <p>The <code>XPathResult</code> interface represents the result of the 
 * evaluation of an XPath expression within the context of a particular 
 * node. Since evaluation of an XPath expression can result in various 
 * result types, this object makes it possible to discover and manipulate 
 * the type and value of the result.</p>
 * 
 * <p>This implementation wraps an <code>XObject</code>.
 * 
 * @see org.apache.xpath.objects.XObject
 * @see org.w3c.dom.xpath.XPathResult
 * 
 */
public class XPathResultImpl implements XPathResult, EventListener {

	/**
     *  The wrapped XObject
     */
	private XObject m_resultObj;
	
	/**
     *  This the type specified by the user during construction.  Typically
	 *  the constructor will be called by org.apache.xpath.XPath.evaluate().
     */
	private short m_resultType = ANY_TYPE; 
	
	private boolean m_isInvalidIteratorState = false;
	
    /**
     * Only used to attach a mutation event handler when specified
     * type is an iterator type.
     */
	private Node m_contextNode;
	
	/**
     *  The iterator, if this is an iterator type.
     */
	private NodeIterator m_iterator = null;
	
	/**
     *  The list, if this is a snapshot type.
     */
	private NodeList m_list = null;
	

	/**
	 * Constructor for XPathResultImpl.
     * 
     * For internal use only.
	 */
	 XPathResultImpl(short type, XObject result, Node contextNode) {
		// Check that the type is valid
		if (!isValidType(type)) {
            String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_XPATH_TYPE, new Object[] {new Integer(type)});       
            throw new XPathException(XPathException.TYPE_ERR,fmsg); // Invalid XPath type argument: {0}           
		}

        // Result object should never be null!
        if (null == result) {
            String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_EMPTY_XPATH_RESULT, null);       
            throw new XPathException(XPathException.INVALID_EXPRESSION_ERR,fmsg); // Empty XPath result object
        }
        
        this.m_resultObj = result;
        this.m_contextNode = contextNode;

        // If specified result was ANY_TYPE, determine XObject type
        if (type == ANY_TYPE) {
            this.m_resultType = getTypeFromXObject(result);
        } else {
            this.m_resultType = type;
        }
		
        // If the context node supports DOM Events and the type is one of the iterator
        // types register this result as an event listener
        if (((m_resultType == XPathResult.ORDERED_NODE_ITERATOR_TYPE) ||
            (m_resultType == XPathResult.UNORDERED_NODE_ITERATOR_TYPE))&&
            (contextNode instanceof EventTarget)) {
                ((EventTarget)contextNode).addEventListener("MutationEvents",this,true);
        }// else can we handle iterator types if contextNode doesn't support EventTarget??
    				
        // If this is an iterator type get the iterator
        if ((m_resultType == ORDERED_NODE_ITERATOR_TYPE) ||
            (m_resultType == UNORDERED_NODE_ITERATOR_TYPE) ||
            (m_resultType == ANY_UNORDERED_NODE_TYPE) ||
            (m_resultType == FIRST_ORDERED_NODE_TYPE))  {
    
            try {
                m_iterator = m_resultObj.nodeset();
            } catch (TransformerException te) {
                // probably not a node type
                String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INCOMPATIBLE_TYPES, new Object[] {getTypeString(getTypeFromXObject(m_resultObj)),getTypeString(m_resultType)});       
                throw new XPathException(XPathException.TYPE_ERR, fmsg);  // The returned type: {0} can not be coerced into the specified type: {1}
            }
    
                // If user requested ordered nodeset and result is unordered 
                // need to sort...TODO
    //            if ((m_resultType == ORDERED_NODE_ITERATOR_TYPE) &&
    //                (!(((DTMNodeIterator)m_iterator).getDTMIterator().isDocOrdered()))) {
    // 
    //            }
    
        // If it's a snapshot type, get the nodelist
        } else if ((m_resultType == UNORDERED_NODE_SNAPSHOT_TYPE) ||
                   (m_resultType == ORDERED_NODE_SNAPSHOT_TYPE)) {
            try {       	
        	   m_list = m_resultObj.nodelist();
            } catch (TransformerException te) {
        		// probably not a node type 
                String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INCOMPATIBLE_TYPES, new Object[] {getTypeString(getTypeFromXObject(m_resultObj)),getTypeString(m_resultType)});       
                throw new XPathException(XPathException.TYPE_ERR, fmsg);  // The returned type: {0} can not be coerced into the specified type: {1}
            }
    	}	    	 
	}

	/**
	 * @see org.w3c.dom.xpath.XPathResult#getResultType()
	 */
	public short getResultType() {
		return m_resultType;
	}

	/**
	 *  The value of this number result.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>NUMBER_TYPE</code>.
	 * @see org.w3c.dom.xpath.XPathResult#getNumberValue()
	 */
	public double getNumberValue() throws XPathException {
		if (getResultType() != NUMBER_TYPE) {
			String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER, new Object[] {getTypeString(m_resultType)});		
			throw new XPathException(XPathException.TYPE_ERR,fmsg); // Can not convert {0} to a number
		} else {
			try {
			   return m_resultObj.num();
			} catch (Exception e) {
				// Type check above should prevent this exception from occurring.
				throw new XPathException(XPathException.TYPE_ERR,e.getMessage());
			}
		}		
	}

	/**
	 * The value of this string result.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>STRING_TYPE</code>.
     * 
	 * @see org.w3c.dom.xpath.XPathResult#getStringValue()
	 */
	public String getStringValue() throws XPathException {
		if (getResultType() != STRING_TYPE) {
			String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_CONVERT_TO_STRING, new Object[] {m_resultObj.getTypeString()});		
			throw new XPathException(XPathException.TYPE_ERR,fmsg); // Can not convert {0} to a string.
		} else {
			try {
			   return m_resultObj.str();
			} catch (Exception e) {
				// Type check above should prevent this exception from occurring.
				throw new XPathException(XPathException.TYPE_ERR,e.getMessage());
			}
		}
	}

	/**
	 * @see org.w3c.dom.xpath.XPathResult#getBooleanValue()
	 */
	public boolean getBooleanValue() throws XPathException {
		if (getResultType() != BOOLEAN_TYPE) {
			String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_CONVERT_TO_BOOLEAN, new Object[] {getTypeString(m_resultType)});		
			throw new XPathException(XPathException.TYPE_ERR,fmsg);  // Can not convert {0} to a boolean
		} else {
			try {
			   return m_resultObj.bool();
			} catch (TransformerException e) {
				// Type check above should prevent this exception from occurring.
				throw new XPathException(XPathException.TYPE_ERR,e.getMessage());
			}
		}
	}

	/**
	 * The value of this single node result, which may be <code>null</code>.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>ANY_UNORDERED_NODE_TYPE</code> or 
     *   <code>FIRST_ORDERED_NODE_TYPE</code>.
     * 
	 * @see org.w3c.dom.xpath.XPathResult#getSingleNodeValue()
	 */
	public Node getSingleNodeValue() throws XPathException {
		
		if ((m_resultType != ANY_UNORDERED_NODE_TYPE) &&
		    (m_resultType != FIRST_ORDERED_NODE_TYPE)) {
           String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_CONVERT_TO_SINGLENODE, new Object[] {getTypeString(m_resultType)});                        
		   throw new XPathException(XPathException.TYPE_ERR,fmsg); // Can not convert {0} to a single node. This getter applies to types 
                                                                 // ANY_UNORDERED_NODE_TYPE and FIRST_ORDERED_NODE_TYPE.
	    }
	
		NodeIterator result = null;
		try {
			result = m_resultObj.nodeset();
		} catch (TransformerException te) {
			throw new XPathException(XPathException.TYPE_ERR,te.getMessage());
		}
        
        if (null == result) return null;
        
        Node node = result.nextNode();
         
        // Wrap "namespace node" in an XPathNamespace 
        if (isNamespaceNode(node)) {
            return new XPathNamespaceImpl(node);
        } else {
            return node;
        }        
	}

	/**
	 * @see org.w3c.dom.xpath.XPathResult#getInvalidIteratorState()
	 */
	public boolean getInvalidIteratorState() {
		return m_isInvalidIteratorState;
	}

	/**
	 * The number of nodes in the result snapshot. Valid values for 
     * snapshotItem indices are <code>0</code> to 
     * <code>snapshotLength-1</code> inclusive.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>UNORDERED_NODE_SNAPSHOT_TYPE</code> or 
     *   <code>ORDERED_NODE_SNAPSHOT_TYPE</code>.
     * 
	 * @see org.w3c.dom.xpath.XPathResult#getSnapshotLength()
	 */
	public int getSnapshotLength() throws XPathException {
	
		if ((m_resultType != UNORDERED_NODE_SNAPSHOT_TYPE) &&
		    (m_resultType != ORDERED_NODE_SNAPSHOT_TYPE)) {
           String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_GET_SNAPSHOT_LENGTH, new Object[] {getTypeString(m_resultType)});                        
		   throw new XPathException(XPathException.TYPE_ERR,fmsg); // Can not get snapshot length on type: {0}. This getter applies to types 
                                                         //UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.
	    }
			
		return m_list.getLength();
	}

	/**
	 * Iterates and returns the next node from the node set or 
     * <code>null</code>if there are no more nodes.
     * @return Returns the next node.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>UNORDERED_NODE_ITERATOR_TYPE</code> or 
     *   <code>ORDERED_NODE_ITERATOR_TYPE</code>.
     * @exception DOMException
     *   INVALID_STATE_ERR: The document has been mutated since the result was 
     *   returned.
	 * @see org.w3c.dom.xpath.XPathResult#iterateNext()
	 */
	public Node iterateNext() throws XPathException, DOMException {
		if ((m_resultType != UNORDERED_NODE_ITERATOR_TYPE) &&
		    (m_resultType != ORDERED_NODE_ITERATOR_TYPE)) {
          String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_NON_ITERATOR_TYPE, new Object[] {getTypeString(m_resultType)});                        
		  throw new XPathException(XPathException.TYPE_ERR, fmsg);  // Can not iterate over non iterator type: {0}
	    }

		if (getInvalidIteratorState()) {
          String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_DOC_MUTATED, null);                        
		  throw new DOMException(DOMException.INVALID_STATE_ERR,fmsg);  // Document mutated since result was returned. Iterator is invalid.
		}			 

        Node node = m_iterator.nextNode();
        
        // Wrap "namespace node" in an XPathNamespace 
        if (isNamespaceNode(node)) {
            return new XPathNamespaceImpl(node);
        } else {
            return node;
        }
	}

    /**
     * Returns the <code>index</code>th item in the snapshot collection. If 
     * <code>index</code> is greater than or equal to the number of nodes in 
     * the list, this method returns <code>null</code>. Unlike the iterator 
     * result, the snapshot does not become invalid, but may not correspond 
     * to the current document if it is mutated.
     * @param index Index into the snapshot collection.
     * @return The node at the <code>index</code>th position in the 
     *   <code>NodeList</code>, or <code>null</code> if that is not a valid 
     *   index.
     * @exception XPathException
     *   TYPE_ERR: raised if <code>resultType</code> is not 
     *   <code>UNORDERED_NODE_SNAPSHOT_TYPE</code> or 
     *   <code>ORDERED_NODE_SNAPSHOT_TYPE</code>.
     *
	 * @see org.w3c.dom.xpath.XPathResult#snapshotItem(int)
	 */
	public Node snapshotItem(int index) throws XPathException {
		
		if ((m_resultType != UNORDERED_NODE_SNAPSHOT_TYPE) &&
		    (m_resultType != ORDERED_NODE_SNAPSHOT_TYPE)) {
           String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_NON_SNAPSHOT_TYPE, new Object[] {getTypeString(m_resultType)});                        
           throw new XPathException(XPathException.TYPE_ERR, fmsg); // Can call snapshotItem on type: {0}. This method applies to types 
                                                              // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.
	    }		
        
        Node node = m_list.item(index);
        
        // Wrap "namespace node" in an XPathNamespace 
        if (isNamespaceNode(node)) {
            return new XPathNamespaceImpl(node);
        } else {
            return node;
        }
	}

	
	/**
	 * Check if the specified type is one of the supported types.
	 * @param type The specified type
	 * 
	 * @return true If the specified type is supported; otherwise, returns false.
	 */
	public static boolean isValidType( short type ) {
		switch (type) {
			case ANY_TYPE:
			case NUMBER_TYPE:
			case STRING_TYPE:
			case BOOLEAN_TYPE:
			case UNORDERED_NODE_ITERATOR_TYPE:
			case ORDERED_NODE_ITERATOR_TYPE:
			case UNORDERED_NODE_SNAPSHOT_TYPE:
			case ORDERED_NODE_SNAPSHOT_TYPE:
			case ANY_UNORDERED_NODE_TYPE:
			case FIRST_ORDERED_NODE_TYPE: return true;
			default: return false;
		}
	}

	/**
	 * @see org.w3c.dom.events.EventListener#handleEvent(Event)
	 */
	public void handleEvent(Event event) {
		
		if (event.getType().equals("MutationEvents")) {
			// invalidate the iterator
			m_isInvalidIteratorState = true;
			
			// deregister as a listener to reduce computational load
			((EventTarget)m_contextNode).removeEventListener("MutationEvents",this,true);
			
		}
	}
    
  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string 
   */
  public String getTypeString(int type)
  {
     switch (type) {
      case ANY_TYPE: return "ANY_TYPE";
      case ANY_UNORDERED_NODE_TYPE: return "ANY_UNORDERED_NODE_TYPE";
      case BOOLEAN_TYPE: return "BOOLEAN";
      case FIRST_ORDERED_NODE_TYPE: return "FIRST_ORDERED_NODE_TYPE";
      case NUMBER_TYPE: return "NUMBER_TYPE";
      case ORDERED_NODE_ITERATOR_TYPE: return "ORDERED_NODE_ITERATOR_TYPE";
      case ORDERED_NODE_SNAPSHOT_TYPE: return "ORDERED_NODE_SNAPSHOT_TYPE";
      case STRING_TYPE: return "STRING_TYPE";
      case UNORDERED_NODE_ITERATOR_TYPE: return "UNORDERED_NODE_ITERATOR_TYPE";
      case UNORDERED_NODE_SNAPSHOT_TYPE: return "UNORDERED_NODE_SNAPSHOT_TYPE";
      default: return "#UNKNOWN";
    }
  }  
  
  /**
   * Given an XObject, determine the corresponding DOM XPath type
   * 
   * @return type string
   */
  private short getTypeFromXObject(XObject object) {
      switch (object.getType()) {
        case XObject.CLASS_BOOLEAN: return BOOLEAN_TYPE;
        case XObject.CLASS_NODESET: return UNORDERED_NODE_ITERATOR_TYPE; 
        case XObject.CLASS_NUMBER: return NUMBER_TYPE;
        case XObject.CLASS_STRING: return STRING_TYPE;
        // XPath 2.0 types                         
//          case XObject.CLASS_DATE: 
//          case XObject.CLASS_DATETIME:
//          case XObject.CLASS_DTDURATION:
//          case XObject.CLASS_GDAY:
//          case XObject.CLASS_GMONTH:
//          case XObject.CLASS_GMONTHDAY:
//          case XObject.CLASS_GYEAR:
//          case XObject.CLASS_GYEARMONTH: 
//          case XObject.CLASS_TIME:
//          case XObject.CLASS_YMDURATION: return STRING_TYPE; // treat all date types as strings?
      
        case XObject.CLASS_RTREEFRAG: return UNORDERED_NODE_ITERATOR_TYPE; 
        case XObject.CLASS_NULL: return ANY_TYPE; // throw exception ?
        default: return ANY_TYPE; // throw exception ?
    }     
    
  }  

/**
 * Given a node, determine if it is a namespace node.
 * 
 * @param node 
 * 
 * @return boolean Returns true if this is a namespace node; otherwise, returns false.
 */
  private boolean isNamespaceNode(Node node) {
    
     if ((null != node) && 
         (node.getNodeType() == Node.ATTRIBUTE_NODE) &&
         (node.getNodeName().startsWith("xmlns:") || node.getNodeName().equals("xmlns"))) {
        return true;   
     } else {
        return false;
     }
  }
  
}
