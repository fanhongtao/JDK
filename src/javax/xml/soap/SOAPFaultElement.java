/*
 * $Id: SOAPFaultElement.java,v 1.3 2004/04/02 01:24:18 ofung Exp $
 * $Revision: 1.3 $
 * $Date: 2004/04/02 01:24:18 $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.soap;

/**
 * A representation of the contents in 
 * a <code>SOAPFault</code> object.  The <code>Detail</code> interface
 * is a <code>SOAPFaultElement</code>.
 * <P>
 * Content is added to a <code>SOAPFaultElement</code> using the
 * <code>SOAPElement</code> method <code>addTextNode</code>.
 */
public interface SOAPFaultElement extends SOAPElement {
}
