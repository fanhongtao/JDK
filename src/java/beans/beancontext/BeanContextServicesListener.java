/*
 * @(#)BeanContextServicesListener.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.beans.beancontext.BeanContextServiceAvailableEvent;
import java.beans.beancontext.BeanContextServiceRevokedEvent;
import java.beans.beancontext.BeanContextServiceRevokedListener;


public interface BeanContextServicesListener extends BeanContextServiceRevokedListener {

    /**
     * The service named has been registered. getService requests for
     * this service may now be made.
     */

    void serviceAvailable(BeanContextServiceAvailableEvent bcsae);
}
