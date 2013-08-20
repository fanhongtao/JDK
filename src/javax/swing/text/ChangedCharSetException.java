/*
 * @(#)ChangedCharSetException.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.io.IOException;

/**
 * ChangedCharSetException as the name indicates is an exception
 * thrown when the charset is changed.
 *
 * @author Sunita Mani
 * 1.8, 12/19/03
 */
public class ChangedCharSetException extends IOException {

    String charSetSpec;
    boolean charSetKey;

    public ChangedCharSetException(String charSetSpec, boolean charSetKey) {
	this.charSetSpec = charSetSpec;
	this.charSetKey = charSetKey;
    }

    public String getCharSetSpec() {
	return charSetSpec;
    }

    public boolean keyEqualsCharSet() {
	return charSetKey;
    }

}
