/**
 * <meta name="usage" content="advanced"/>
 * This class implements an RTF Iterator. Currently exists for sole
 * purpose of enabling EXSLT object-type function to return "RTF".
 * 
  */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xpath.NodeSetDTM;
import org.apache.xml.dtm.DTMManager;

public class RTFIterator extends NodeSetDTM {

	/**
	 * Constructor for RTFIterator
	 */	
	public RTFIterator(int root, DTMManager manager) {
		super(root, manager);
	}
}

