/*
 * @(#)Shape.java	1.4 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

/**
 * The interface for objects which represent some form of geometric
 * shape.
 * <p>
 * This interface will be revised in the upcoming Java2D project.
 * It is meant to provide a common interface for various existing
 * geometric AWT classes and methods which operate on them.  Since
 * it may be superseded or expanded in the future, developers should
 * avoid implementing this interface in their own classes until it
 * is completed in a later release.
 *
 * @version 1.4 07/01/98
 * @author Jim Graham
 */

public interface Shape {
    /**
     * Return the bounding box of the shape.
     */
    public Rectangle getBounds();
}
