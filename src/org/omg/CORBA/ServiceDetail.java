/*
 * @(#)ServiceDetail.java	1.3 98/10/11
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA;

public final class ServiceDetail implements org.omg.CORBA.portable.IDLEntity
{
    public int service_detail_type;
    public byte[] service_detail;

    public ServiceDetail() { }

    public ServiceDetail(int service_detail_type, byte[] service_detail) {
	this.service_detail_type = service_detail_type;
	this.service_detail = service_detail;
    }
}
