/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;

/**
 * Base class for datatype exceptions. For DTD types, the exception can be
 * created from an error message. For Schema types, it needs an error code
 * (as defined in Appendix C of the structure spec), plus an array of arguents,
 * for error message substitution.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: DatatypeException.java,v 1.4 2002/07/30 22:10:18 sandygao Exp $
 */
public class DatatypeException extends Exception {

    // used to store error code and error substitution arguments
    protected String key;
    protected Object[] args;

    /**
     * Create a new datatype exception by providing an error code and a list
     * of error message substitution arguments.
     *
     * @param key  error code
     * @param args error arguments
     */
    public DatatypeException(String key, Object[] args) {
        super(key);
        this.key = key;
        this.args = args;
    }

    /**
     * Return the error code
     *
     * @return  error code
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the list of error arguments
     *
     * @return  error arguments
     */
    public Object[] getArgs() {
        return args;
    }
    
    /**
     * Overrides this method to get the formatted&localized error message.
     * 
     * REVISIT: the system locale is used to load the property file.
     *          do we want to allow the appilcation to specify a
     *          different locale?
     */
    public String getMessage() {
        ResourceBundle resourceBundle = null;
        resourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
        if (resourceBundle == null)
            throw new MissingResourceException("Property file not found!", "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);

        String msg = resourceBundle.getString(key);
        if (msg == null) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);
        }

        if (args != null) {
            try {
                msg = java.text.MessageFormat.format(msg, args);
            } catch (Exception e) {
                msg = resourceBundle.getString("FormatFailed");
                msg += " " + resourceBundle.getString(key);
            }
        } 

        return msg;
    }
}
