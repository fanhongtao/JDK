/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

/**
 * An event used to identify a single path in a tree.  The source
 * returned by <b>getSource</b> will be an instance of JTree.
 * <p>
 * For further documentation and examples see 
 * the following sections in <em>The Java Tutorial</em>:
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/events/treeexpansionlistener.html">How to Write a Tree Expansion Listener</a> and
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/events/treewillexpandlistener.html">How to Write a Tree-Will-Expand Listener</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author Scott Violet
 * @version 1.17 02/06/02
 */
public class TreeExpansionEvent extends EventObject
{
    /**
      * Path to the value this event represents.
      */
    protected TreePath              path;

    /**
     * Constructs a TreeExpansionEvent object.
     *
     * @param source  the Object that originated the event
     *                (typically <code>this</code>)
     * @param path    a TreePath object identifying the newly expanded
     *                node
     */
    public TreeExpansionEvent(Object source, TreePath path) {
	super(source);
	this.path = path;
    }

    /**
      * Returns the path to the value that has been expanded/collapsed.
      */
    public TreePath getPath() { return path; }
}
