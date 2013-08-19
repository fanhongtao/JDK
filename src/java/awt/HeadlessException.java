/*
 * @(#)HeadlessException.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Thrown when code that is dependent on a keyboard, display, or mouse
 * is called in an environment that does not support a keyboard, display,
 * or mouse.
 *
 * @since 1.4
 * @author  Michael Martak
 */
public class HeadlessException extends UnsupportedOperationException {
    public HeadlessException() {}
    public HeadlessException(String msg) {
        super(msg);
    }
}
