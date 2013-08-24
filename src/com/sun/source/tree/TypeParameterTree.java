/*
 * @(#)TypeParameterTree.java	1.3 06/07/11
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

/**
 * A tree node for a type parameter.
 *
 * For example:
 * <pre>
 *   <em>name</em> 
 * 
 *   <em>name</em> extends <em>bounds</em>
 * </pre>
 *
 * @see "The Java Language Specification, 3rd ed, section 4.4"
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface TypeParameterTree extends Tree {
    Name getName();
    List<? extends Tree> getBounds();
}
