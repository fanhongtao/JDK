/*
 * @(#)Observer.java	1.11 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.util;

/**
 * A class can implement the <code>Observer</code> interface when it 
 * wants to be informed of changes in observable objects. 
 *
 * @author  Chris Warth
 * @version 1.11, 07/01/98
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
