/*
 * @(#)AppletStub.java	1.9 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agre     * Constructs a NegativeArraySizeException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail messageement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.applet;

import java.net.URL;

/**
 * When an applet is first created, an applet stub is attached to it 
 * using the applet's <code>setStub</code> method. This stub 
 * serves as the interface between the applet and the browser 
 * environment or applet viewer environment in which the application 
 * is running. 
 *
 * @author 	Arthur van Hoff
 * @version     1.12, 01/16/97
 * @see         java.applet.Applet#setStub(java.applet.AppletStub)
 * @since       JDK1.0
 */
public interface AppletStub {
    /**
     * Determines if the applet is active. An applet is active just 
     * before its <code>start</code> method is called. It becomes 
     * inactive immediately after its <code>stop</code> method is called. 
     *
     * @return  <code>true</code> if the applet is active;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    boolean isActive();
    
    /**
     * Gets the document URL.
     *
     * @return  the <code>URL</code> of the document containing the applet.
     * @since   JDK1.0
     */
    URL getDocumentBase();

    /**
     * Gets the base URL.
     *
     * @return  the <code>URL</code> of the applet.
     * @since   JDK1.0
     */
    URL getCodeBase();

    /**
     * Returns the value of the named parameter in the HTML tag. For 
     * example, if an applet is specified as 
     * <ul><code>
     *	&lt;applet code="Clock" width=50 height=50&gt;<br>
     *  &lt;param name=Color value="blue"&gt;<br>
     *  &lt;/applet&gt;
     * </code></ul>
     * <p>
     * then a call to <code>getParameter("Color")</code> returns the 
     * value <code>"blue"</code>. 
     *
     * @param   name   a parameter name.
     * @return  the value of the named parameter.
     * @since   JDK1.0
     */
    String getParameter(String name);

    /**
     * Gets a handler to the applet's context.
     *
     * @return  the applet's context.
     * @since   JDK1.0
     */
    AppletContext getAppletContext();

    /**
     * Called when the applet wants to be resized. 
     *
     * @param   width    the new requested width for the applet.
     * @param   height   the new requested height for the applet.
     * @since   JDK1.0
     */
    void appletResize(int width, int height);
}
