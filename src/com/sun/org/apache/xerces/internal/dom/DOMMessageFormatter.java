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

package com.sun.org.apache.xerces.internal.dom;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Used to format DOM error messages, using the system locale.
 *
 * @author Sandy Gao, IBM
 * @version $Id: DOMMessageFormatter.java,v 1.6 2004/07/12 23:44:28 venu Exp $
 */
public class DOMMessageFormatter {
    public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    public static final String SERIALIZER_DOMAIN = "http://apache.org/xml/serializer";
    
    private static ResourceBundle domResourceBundle = null;
    private static ResourceBundle xmlResourceBundle = null;
    private static ResourceBundle serResourceBundle = null;
    private static Locale locale = null;
    
    
    DOMMessageFormatter(){
        locale = Locale.getDefault();
    }
    /**
     * Formats a message with the specified arguments using the given
     * locale information.
     *
     * @param domain    domain from which error string is to come.
     * @param key       The message key.
     * @param arguments The message replacement text arguments. The order
     *                  of the arguments must match that of the placeholders
     *                  in the actual message.
     *
     * @return          the formatted message.
     *
     * @throws MissingResourceException Thrown if the message with the
     *                                  specified key cannot be found.
     */
    public static String formatMessage(String domain,
    String key, Object[] arguments)
    throws MissingResourceException {
        ResourceBundle resourceBundle = getResourceBundle(domain);
        if(resourceBundle == null){
            init();
            resourceBundle = getResourceBundle(domain);
            if(resourceBundle == null)
                throw new MissingResourceException("Unknown domain" + domain, null, key);
        }
        // format message
        String msg;
        try {
            msg = key + ": " + resourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = java.text.MessageFormat.format(msg, arguments);
                }
                catch (Exception e) {
                    msg = resourceBundle.getString("FormatFailed");
                    msg += " " + resourceBundle.getString(key);
                }
            }
        } // error
        catch (MissingResourceException e) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg, key);
        }
        
        // no message
        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                StringBuffer str = new StringBuffer(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(String.valueOf(arguments[i]));
                }
            }
        }
        
        return msg;
    }
    
    static ResourceBundle getResourceBundle(String domain){
        if(domain == DOM_DOMAIN || domain.equals(DOM_DOMAIN))
            return domResourceBundle;
        else if( domain == XML_DOMAIN || domain.equals(XML_DOMAIN))
            return xmlResourceBundle;
        else if(domain == SERIALIZER_DOMAIN || domain.equals(SERIALIZER_DOMAIN))
            return serResourceBundle;
        return null;
    }
    /**
     * Initialize Message Formatter.
     */
    public static void init(){
        if (locale != null) {
            domResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", locale);
            serResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", locale);
            xmlResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
        }else{
            domResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
            serResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
            xmlResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
        }
    }
    
    /**
     * setLocale to be used by the formatter.
     * @param locale
     */
    public static void setLocale(Locale dlocale){
        locale = dlocale;
    }
}
