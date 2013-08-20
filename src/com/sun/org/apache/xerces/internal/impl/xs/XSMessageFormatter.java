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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;


/**
 * SchemaMessageProvider implements an XMLMessageProvider that
 * provides localizable error messages for the W3C XML Schema Language
 * 
 * @author Elena Litani, IBM
 * @version $Id: XSMessageFormatter.java,v 1.2 2002/01/29 01:15:14 lehors Exp $
 */
public class XSMessageFormatter implements MessageFormatter {
    /**
     * The domain of messages concerning the XML Schema: Structures specification.
     */
    public static final String SCHEMA_DOMAIN = "http://www.w3.org/TR/xml-schema-1";


    // private objects to cache the locale and resource bundle
    private Locale fLocale = null;
    private ResourceBundle fResourceBundle = null;

    /**
     * Formats a message with the specified arguments using the given
     * locale information.
     * 
     * @param locale    The locale of the message.
     * @param key       The message key.
     * @param arguments The message replacement text arguments. The order
     *                  of the arguments must match that of the placeholders
     *                  in the actual message.
     * 
     * @return Returns the formatted message.
     *
     * @throws MissingResourceException Thrown if the message with the
     *                                  specified key cannot be found.
     */
     public String formatMessage(Locale locale, String key, Object[] arguments)
        throws MissingResourceException {
        
        if (fResourceBundle == null || locale != fLocale) {
            if (locale != null) {
                fResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", locale);
                // memorize the most-recent locale
                fLocale = locale;
            }
            if (fResourceBundle == null)
                fResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
        }
        
        String msg = fResourceBundle.getString(key);
        if (arguments != null) {
            try {
                msg = java.text.MessageFormat.format(msg, arguments);
            } catch (Exception e) {
                msg = fResourceBundle.getString("FormatFailed");
                msg += " " + fResourceBundle.getString(key);
            }
        } 

        if (msg == null) {
            msg = fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.SchemaMessages", key);
        }

        return msg;
    }
}
