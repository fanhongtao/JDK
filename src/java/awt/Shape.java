/*
 * @(#)Shape.java	1.3 97/02/05
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * @version 1.3 02/05/97
 * @author Jim Graham
 */

public interface Shape {
    /**
     * Return the bounding box of the shape.
     */
    public Rectangle getBounds();
}
