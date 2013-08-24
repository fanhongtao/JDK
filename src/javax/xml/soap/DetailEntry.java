/*
 * $Id: DetailEntry.java,v 1.2 2004/04/02 01:24:17 ofung Exp $
 * $Revision: 1.2 $
 * $Date: 2004/04/02 01:24:17 $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.soap;

/**
 * The content for a <code>Detail</code> object, giving details for
 * a <code>SOAPFault</code> object.  A <code>DetailEntry</code> object,
 * which carries information about errors related to the <code>SOAPBody</code>
 * object that contains it, is application-specific.
 */
public interface DetailEntry extends SOAPElement {

}
