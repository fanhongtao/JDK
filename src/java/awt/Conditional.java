/*
 * @(#)Conditional.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Conditional is used by the EventDispatchThread's message pumps to
 * determine if a given pump should continue to run, or should instead exit
 * and yield control to the parent pump.
 *
 * @version 1.4 12/03/01
 * @author David Mendenhall
 */
interface Conditional {
    boolean evaluate();
}
