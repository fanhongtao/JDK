/*
 * @(#)SampleTreeModel.java	1.5 98/08/26
 *
 * Copyright 1997 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
  * @version 1.5 08/26/98
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
