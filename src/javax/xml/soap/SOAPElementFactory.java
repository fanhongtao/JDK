/*
 * $Id: SOAPElementFactory.java,v 1.11 2005/04/05 22:42:05 mk125090 Exp $
 * $Revision: 1.11 $
 * $Date: 2005/04/05 22:42:05 $
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
 * <code>SOAPElementFactory</code> is a factory for XML fragments that
 * will eventually end up in the SOAP part. These fragments
 * can be inserted as children of the <code>SOAPHeader</code> or
 * <code>SOAPBody</code> or <code>SOAPEnvelope</code>.
 *
 * <p>Elements created using this factory do not have the properties
 * of an element that lives inside a SOAP header document. These
 * elements are copied into the XML document tree when they are
 * inserted.
 * @deprecated - Use <code>javax.xml.soap.SOAPFactory</code> for creating SOAPElements.
 * @see javax.xml.soap.SOAPFactory
 */
public class SOAPElementFactory {

    private SOAPFactory soapFactory;

    private SOAPElementFactory(SOAPFactory soapFactory) {
        this.soapFactory = soapFactory;
    }

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given <code>Name</code> object.
     *
     * @param name a <code>Name</code> object with the XML name for
     *             the new element
     *
     * @return the new <code>SOAPElement</code> object that was
     *         created
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(javax.xml.soap.Name)
     * instead
     *
     * @see javax.xml.soap.SOAPFactory#createElement(javax.xml.soap.Name)
     * @see javax.xml.soap.SOAPFactory#createElement(javax.xml.namespace.QName)
     */
    public SOAPElement create(Name name) throws SOAPException {
        return soapFactory.createElement(name);
    }

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given local name.
     *
     * @param localName a <code>String</code> giving the local name for
     *             the new element
     *
     * @return the new <code>SOAPElement</code> object that was
     *         created
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(String localName) instead
     *
     * @see javax.xml.soap.SOAPFactory#createElement(java.lang.String)
     */
    public SOAPElement create(String localName) throws SOAPException {
        return soapFactory.createElement(localName);
    }

    /**
     * Create a new <code>SOAPElement</code> object with the given
     * local name, prefix and uri.
     *
     * @param localName a <code>String</code> giving the local name
     *                  for the new element
     * @param prefix the prefix for this <code>SOAPElement</code>
     * @param uri a <code>String</code> giving the URI of the
     *            namespace to which the new element belongs
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(String localName,
     *                      String prefix,
     *                      String uri)
     * instead
     *
     * @see javax.xml.soap.SOAPFactory#createElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public SOAPElement create(String localName, String prefix, String uri)
        throws SOAPException {
        return soapFactory.createElement(localName, prefix, uri);
    }

    /**
     * Creates a new instance of <code>SOAPElementFactory</code>.
     *
     * @return a new instance of a <code>SOAPElementFactory</code>
     *
     * @exception SOAPException if there was an error creating the
     *            default <code>SOAPElementFactory</code>
     */
    public static SOAPElementFactory newInstance() throws SOAPException {
        try {
            return new SOAPElementFactory(SOAPFactory.newInstance());
        } catch (Exception ex) {
            throw new SOAPException(
                "Unable to create SOAP Element Factory: " + ex.getMessage());
        }
    }
}
