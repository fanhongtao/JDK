/*
 * @(#)Shape.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.5 12/10/01
 * @author Jim Graham
 */

public interface Shape {
    /**
     * Return the bounding box of the shape.
     */
    public Rectangle getBounds();
}
