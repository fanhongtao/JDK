/*
 * @(#)ZipException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

import java.io.IOException;

public
class ZipException extends IOException {
    public ZipException() {
	super();
    }

    public ZipException(String s) {
	super(s);
    }
}
