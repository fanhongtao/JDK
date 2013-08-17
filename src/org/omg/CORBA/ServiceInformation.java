/*
 * @(#)ServiceInformation.java	1.3 98/10/11
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


/** ServiceInformation is an IDL struct in the CORBA module.
 *  It stores information about a CORBA service available in the
 *  ORB implementation. It is obtained from the ORB.get_service_information
 *  method.
 */

public final class ServiceInformation implements org.omg.CORBA.portable.IDLEntity
{
    public int[] service_options;
    public org.omg.CORBA.ServiceDetail[] service_details;

    public ServiceInformation() { }

    public ServiceInformation(int[] __service_options,
			      org.omg.CORBA.ServiceDetail[] __service_details)
    {
        service_options = __service_options;
        service_details = __service_details;
    }
}

