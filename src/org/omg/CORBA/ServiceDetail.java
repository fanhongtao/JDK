/*
 * @(#)ServiceDetail.java	1.8 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/** Describes the ORB service identified by the service_detail_type.
 *
 * @author RIP Team
 * @version 1.8 02/02/00
 */
public final class ServiceDetail implements org.omg.CORBA.portable.IDLEntity
{
    /** The type of this ServiceDetail object. */
    public int service_detail_type;

    /** The data describing this ServiceDetail. */
    public byte[] service_detail;

    /** 
     *Constructs a ServiceDetail object with service_detail_type 0 and
     * empty service_detail.
     */
    public ServiceDetail() { }

    /** 
    *Constructs a ServiceDetail object with the given service_detail_type 
    * service_detail.
    * @param service_detail_type The service detail type.
    * @param service_detail The service detail for this type.
    */
    public ServiceDetail(int service_detail_type, byte[] service_detail) {
	this.service_detail_type = service_detail_type;
	this.service_detail = service_detail;
    }
}
