/*
 * @(#)Node.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orbutil.graph ;

import java.util.Set ;

/** Node in a graph.
*/
public interface Node 
{
    /** Get all the children of this node. 
     */
    Set /* Set<Node> */ getChildren() ;
}
