/*
 * $Id: SOAPBodyElement.java,v 1.3 2005/04/05 22:28:13 mk125090 Exp $
 * $Revision: 1.3 $
 * $Date: 2005/04/05 22:28:13 $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package javax.xml.soap;

/**
 * A <code>SOAPBodyElement</code> object represents the contents in 
 * a <code>SOAPBody</code> object.  The <code>SOAPFault</code> interface
 * is a <code>SOAPBodyElement</code> object that has been defined.
 * <P>
 * A new <code>SOAPBodyElement</code> object can be created and added
 * to a <code>SOAPBody</code> object with the <code>SOAPBody</code>
 * method <code>addBodyElement</code>. In the following line of code,
 * <code>sb</code> is a <code>SOAPBody</code> object, and 
 * <code>myName</code> is a <code>Name</code> object.
 * <PRE>
 *    SOAPBodyElement sbe = sb.addBodyElement(myName);
 * </PRE>
 */
public interface SOAPBodyElement extends SOAPElement {
}
