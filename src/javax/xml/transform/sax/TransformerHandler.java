/**
 * $Id: TransformerHandler.java,v 1.3 2001/01/26 09:33:10 mode Exp $
 *
 * Copyright (c) 2000-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package javax.xml.transform.sax;

import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.DTDHandler;


/**
 * A TransformerHandler
 * listens for SAX ContentHandler parse events and transforms
 * them to a Result.
 */
public interface TransformerHandler
    extends ContentHandler, LexicalHandler, DTDHandler {

    /**
     * Enables the user of the TransformerHandler to set the
     * to set the Result for the transformation.
     *
     * @param result A Result instance, should not be null.
     *
     * @throws IllegalArgumentException if result is invalid for some reason.
     */
    public void setResult(Result result) throws IllegalArgumentException;

    /**
     * Set the base ID (URI or system ID) from where relative
     * URLs will be resolved.
     * @param systemID Base URI for the source tree.
     */
    public void setSystemId(String systemID);

    /**
     * Get the base ID (URI or system ID) from where relative
     * URLs will be resolved.
     * @return The systemID that was set with {@link #setSystemId}.
     */
    public String getSystemId();

    /**
     * Get the Transformer associated with this handler, which
     * is needed in order to set parameters and output properties.
     */
    public Transformer getTransformer();
}
