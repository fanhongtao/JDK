/*
 * @(#)ErroneousTree.java	1.3 06/06/24
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import java.util.List;

/**
 * A tree node to stand in for a malformed expression.
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface ErroneousTree extends ExpressionTree {
    List<? extends Tree> getErrorTrees();
}
