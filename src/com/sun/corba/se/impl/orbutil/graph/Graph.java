/*
 * @(#)Graph.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orbutil.graph ;

import java.util.Set ;

public interface Graph extends Set // Set<Node>
{
    NodeData getNodeData( Node node ) ;

    Set /* Set<Node> */ getRoots() ;
}
