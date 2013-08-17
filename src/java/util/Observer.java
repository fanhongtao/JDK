/*
 * @(#)Observer.java	1.10 97/01/28
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
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
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.util;

/**
 * A class can implement the <code>Observer</code> interface when it 
 * wants to be informed of changes in observable objects. 
 *
 * @author  Chris Warth
 * @version 1.10, 01/28/97
 * @see     java.util.Observable
 * @since   JDK1.0
 */
public interface Observer { 
    /**
     * This method is called whenever the observed object is changed. An 
     * application calls an observable object's 
     * <code>notifyObservers</code> method  to have all the object's 
     * observers notified of the change. 
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code>
     *                 method.
     * @since   JDK1.0
     */
    void update(Observable o, Object arg);
}
