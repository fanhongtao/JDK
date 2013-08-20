/**
 * @(#)MaxStreamFormatVersionComponent.java	1.2 04/05/24
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent ;

// Java to IDL ptc 02-01-12 1.4.11
// TAG_RMI_CUSTOM_MAX_STREAM_FORMAT
public interface MaxStreamFormatVersionComponent extends TaggedComponent
{
    public byte getMaxStreamFormatVersion() ;
}
