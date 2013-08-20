/*
 * @(#)Conditional.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Conditional is used by the EventDispatchThread's message pumps to
 * determine if a given pump should continue to run, or should instead exit
 * and yield control to the parent pump.
 *
 * @version 1.6 12/19/03
 * @author David Mendenhall
 */
interface Conditional {
    boolean evaluate();
}
