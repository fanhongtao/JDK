/*
 * @(#)SampleTreeModel.java	1.8 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;

/**
  * SampleTreeModel extends JTreeModel to extends valueForPathChanged.
  * This method is called as a result of the user editing a value in
  * the tree.  If you allow editing in your tree, are using TreeNodes
  * and the user object of the TreeNodes is not a String, then you're going
  * to have to subclass JTreeModel as this example does.
  *
  * @version 1.8 12/03/01
  * @author Scott Violet
  */

public class SampleTreeModel extends DefaultTreeModel
{
    /**
      * Creates a new instance of SampleTreeModel with newRoot set
      * to the root of this model.
      */
    public SampleTreeModel(TreeNode newRoot) {
	super(newRoot);
    }

    /**
      * Subclassed to message setString() to the changed path item.
      */
    public void valueForPathChanged(TreePath path, Object newValue) {
	/* Update the user object. */
	DefaultMutableTreeNode      aNode = (DefaultMutableTreeNode)path.getLastPathComponent();
	SampleData    sampleData = (SampleData)aNode.getUserObject();

	sampleData.setString((String)newValue);
	/* UUUhhhhh, pretty colors. */
	sampleData.setColor(Color.green);

	/* Since we've changed how the data is to be displayed, message
	   nodeChanged. */
	nodeChanged(aNode);
    }
}
