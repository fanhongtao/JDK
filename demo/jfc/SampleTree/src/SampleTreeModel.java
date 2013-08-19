/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)SampleTreeModel.java	1.10 03/01/23
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
  * @version 1.10 01/23/03
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
