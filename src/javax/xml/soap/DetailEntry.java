/*
 * $Id: DetailEntry.java,v 1.3 2005/04/05 20:34:16 mk125090 Exp $
 * $Revision: 1.3 $
 * $Date: 2005/04/05 20:34:16 $
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
 * The content for a <code>Detail</code> object, giving details for
 * a <code>SOAPFault</code> object.  A <code>DetailEntry</code> object,
 * which carries information about errors related to the <code>SOAPBody</code>
 * object that contains it, is application-specific.
 */
public interface DetailEntry extends SOAPElement {

}
