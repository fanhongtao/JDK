/*
 * @(#)SampleTree.java	1.17 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.tree.*;

/**
  * A demo for illustrating how to do different things with JTree.
  * The data that this displays is rather boring, that is each node will
  * have 7 children that have random names based on the fonts.  Each node
  * is then drawn with that font and in a different color.
  * While the data isn't interesting the example illustrates a number
  * of things:
  *
  * For an example of dynamicaly loading children refer to DynamicTreeNode.
  * For an example of adding/removing/inserting/reloading refer to the inner
  *     classes of this class, AddAction, RemovAction, InsertAction and
  *     ReloadAction.
  * For an example of creating your own cell renderer refer to
  *     SampleTreeCellRenderer.
  * For an example of subclassing JTreeModel for editing refer to
  *     SampleTreeModel.
  *
  * @version 1.17 04/23/99
  * @author Scott Violet
  */

public class SampleTree
{
    /** Window for showing Tree. */
    protected JFrame            frame;
    /** Tree used for the example. */
    protected JTree             tree;
    /** Tree model. */
    protected DefaultTreeModel        treeModel;

    /**
      * Constructs a new instance of SampleTree.
      */
    public SampleTree() {
	// Force SampleTree to come up in the Cross Platform L&F
	try {
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    // If you want the System L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}


	JMenuBar         menuBar = constructMenuBar();
	JPanel           panel = new JPanel(true);

	frame = new JFrame("SampleTree");
	frame.getContentPane().add("Center", panel);
	frame.setJMenuBar(menuBar);
	frame.setBackground(Color.lightGray);

	/* Create the JTreeModel. */
	DefaultMutableTreeNode root = createNewNode("Root");
	treeModel = new SampleTreeModel(root);

	/* Create the tree. */
	tree = new JTree(treeModel);

	/* Enable tool tips for the tree, without this tool tips will not
	   be picked up. */
	ToolTipManager.sharedInstance().registerComponent(tree);

	/* Make the tree use an instance of SampleTreeCellRenderer for
	   drawing. */
	tree.setCellRenderer(new SampleTreeCellRenderer());

	/* Make tree ask for the height of each row. */
	tree.setRowHeight(-1);

	/* Put the Tree in a scroller. */
	JScrollPane        sp = new JScrollPane();
	sp.setPreferredSize(new Dimension(300, 300));
	sp.getViewport().add(tree);

	/* And show it. */
	panel.setLayout(new BorderLayout());
	panel.add("Center", sp);
	panel.add("South", constructOptionsPanel());

	frame.addWindowListener( new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}});

	frame.pack();
	frame.show();
    }

    /** Constructs a JPanel containing check boxes for the different
      * options that tree supports. */
    private JPanel constructOptionsPanel() {
	JCheckBox               aCheckbox;
	JPanel           retPanel = new JPanel(false);
	JPanel           borderPane = new JPanel(false);

	borderPane.setLayout(new BorderLayout());
	retPanel.setLayout(new FlowLayout());

	aCheckbox = new JCheckBox("show handles");
	aCheckbox.setSelected(tree.getShowsRootHandles());
	aCheckbox.addChangeListener(new ShowHandlesChangeListener());
	retPanel.add(aCheckbox);

	aCheckbox = new JCheckBox("show root");
	aCheckbox.setSelected(tree.isRootVisible());
	aCheckbox.addChangeListener(new ShowRootChangeListener());
	retPanel.add(aCheckbox);

	aCheckbox = new JCheckBox("editable");
	aCheckbox.setSelected(tree.isEditable());
	aCheckbox.addChangeListener(new TreeEditableChangeListener());
	aCheckbox.setToolTipText("Triple click to edit");
	retPanel.add(aCheckbox);

	borderPane.add(retPanel, BorderLayout.CENTER);

	/* Create a set of radio buttons that dictate what selection should
	   be allowed in the tree. */
	ButtonGroup           group = new ButtonGroup();
	JPanel         buttonPane = new JPanel(false);
	JRadioButton          button;

	buttonPane.setLayout(new FlowLayout());
	button = new JRadioButton("Single");
	button.addActionListener(new AbstractAction() {
	    public boolean isEnabled() { return true; }
	    public void actionPerformed(ActionEvent e) {
		tree.getSelectionModel().setSelectionMode
		    (TreeSelectionModel.SINGLE_TREE_SELECTION);
	    }
	});
	group.add(button);
	buttonPane.add(button);
	button = new JRadioButton("Contiguous");
	button.addActionListener(new AbstractAction() {
	    public boolean isEnabled() { return true; }
	    public void actionPerformed(ActionEvent e) {
		tree.getSelectionModel().setSelectionMode
		    (TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
	    }
	});
	group.add(button);
	buttonPane.add(button);
	button = new JRadioButton("Discontiguous");
	button.addActionListener(new AbstractAction() {
	    public boolean isEnabled() { return true; }
	    public void actionPerformed(ActionEvent e) {
		tree.getSelectionModel().setSelectionMode
		    (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	    }
	});
	button.setSelected(true);
	group.add(button);
	buttonPane.add(button);

	borderPane.add(buttonPane, BorderLayout.SOUTH);

	// NOTE: This will be enabled in a future release.
	// Create a label and combobox to determine how many clicks are
	// needed to expand.
/*
	JPanel               clickPanel = new JPanel();
	Object[]             values = { "Never", new Integer(1),
					new Integer(2), new Integer(3) };
	final JComboBox      clickCBox = new JComboBox(values);

	clickPanel.setLayout(new FlowLayout());
	clickPanel.add(new JLabel("Click count to expand:"));
	clickCBox.setSelectedIndex(2);
	clickCBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		Object       selItem = clickCBox.getSelectedItem();

		if(selItem instanceof Integer)
		    tree.setToggleClickCount(((Integer)selItem).intValue());
		else // Don't toggle
		    tree.setToggleClickCount(0);
	    }
	});
	clickPanel.add(clickCBox);
	borderPane.add(clickPanel, BorderLayout.NORTH);
*/
	return borderPane;
    }

    /** Construct a menu. */
    private JMenuBar constructMenuBar() {
	JMenu            menu;
	JMenuBar         menuBar = new JMenuBar();
	JMenuItem        menuItem;

	/* Good ol exit. */
	menu = new JMenu("File");
	menuBar.add(menu);

	menuItem = menu.add(new JMenuItem("Exit"));
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }});

	/* Tree related stuff. */
	menu = new JMenu("Tree");
	menuBar.add(menu);

	menuItem = menu.add(new JMenuItem("Add"));
	menuItem.addActionListener(new AddAction());

	menuItem = menu.add(new JMenuItem("Insert"));
	menuItem.addActionListener(new InsertAction());

	menuItem = menu.add(new JMenuItem("Reload"));
	menuItem.addActionListener(new ReloadAction());

	menuItem = menu.add(new JMenuItem("Remove"));
	menuItem.addActionListener(new RemoveAction());

	return menuBar;
    }

    /**
      * Returns the TreeNode instance that is selected in the tree.
      * If nothing is selected, null is returned.
      */
    protected DefaultMutableTreeNode getSelectedNode() {
	TreePath   selPath = tree.getSelectionPath();

	if(selPath != null)
	    return (DefaultMutableTreeNode)selPath.getLastPathComponent();
	return null;
    }

    protected DefaultMutableTreeNode createNewNode(String name) {
	return new DynamicTreeNode(new SampleData(null, Color.black, name));
    }

    /**
      * AddAction is used to add a new item after the selected item.
      */
    class AddAction extends Object implements ActionListener
    {
	/** Number of nodes that have been added. */
	public int               addCount;

	/**
	  * Messaged when the user clicks on the Add menu item.
	  * Determines the selection from the Tree and adds an item
	  * after that.  If nothing is selected, an item is added to
	  * the root.
	  */
	public void actionPerformed(ActionEvent e) {
	    int               newIndex;
	    DefaultMutableTreeNode          lastItem = getSelectedNode();
	    DefaultMutableTreeNode          parent;

	    /* Determine where to create the new node. */
	    if(lastItem != null) {
		parent = (DefaultMutableTreeNode)lastItem.getParent();
		if(parent == null) {
		    parent = (DefaultMutableTreeNode)treeModel.getRoot();
		    lastItem = null;
		}
	    }
	    else
		parent = (DefaultMutableTreeNode)treeModel.getRoot();
	    if(lastItem == null)
		newIndex = treeModel.getChildCount(parent);
	    else
		newIndex = parent.getIndex(lastItem) + 1;

	    /* Let the treemodel know. */
	    treeModel.insertNodeInto(createNewNode("Added " +
					Integer.toString(addCount++)),
				     parent, newIndex);
	}
    } // End of SampleTree.AddAction


    /**
      * InsertAction is used to insert a new item before the selected item.
      */
    class InsertAction extends Object implements ActionListener
    {
	/** Number of nodes that have been added. */
	public int               insertCount;

	/**
	  * Messaged when the user clicks on the Insert menu item.
	  * Determines the selection from the Tree and inserts an item
	  * after that.  If nothing is selected, an item is added to
	  * the root.
	  */
	public void actionPerformed(ActionEvent e) {
	    int               newIndex;
	    DefaultMutableTreeNode          lastItem = getSelectedNode();
	    DefaultMutableTreeNode          parent;

	    /* Determine where to create the new node. */
	    if(lastItem != null) {
		parent = (DefaultMutableTreeNode)lastItem.getParent();
		if(parent == null) {
		    parent = (DefaultMutableTreeNode)treeModel.getRoot();
		    lastItem = null;
		}
	    }
	    else
		parent = (DefaultMutableTreeNode)treeModel.getRoot();
	    if(lastItem == null)
		newIndex = treeModel.getChildCount(parent);
	    else
		newIndex = parent.getIndex(lastItem);

	    /* Let the treemodel know. */
	    treeModel.insertNodeInto(createNewNode("Inserted " +
					Integer.toString(insertCount++)),
				     parent, newIndex);
	}
    } // End of SampleTree.InsertAction


    /**
      * ReloadAction is used to reload from the selected node.  If nothing
      * is selected, reload is not issued.
      */
    class ReloadAction extends Object implements ActionListener
    {
	/**
	  * Messaged when the user clicks on the Reload menu item.
	  * Determines the selection from the Tree and asks the treemodel
	  * to reload from that node.
	  */
	public void actionPerformed(ActionEvent e) {
	    DefaultMutableTreeNode          lastItem = getSelectedNode();

	    if(lastItem != null)
		treeModel.reload(lastItem);
	}
    } // End of SampleTree.ReloadAction

    /**
      * RemoveAction removes the selected node from the tree.  If
      * The root or nothing is selected nothing is removed.
      */
    class RemoveAction extends Object implements ActionListener
    {
	/**
	  * Removes the selected item as long as it isn't root.
	  */
	public void actionPerformed(ActionEvent e) {
	    DefaultMutableTreeNode          lastItem = getSelectedNode();

	    if(lastItem != null && lastItem != (DefaultMutableTreeNode)treeModel.getRoot()) {
		treeModel.removeNodeFromParent(lastItem);
	    }
	}
    } // End of SampleTree.RemoveAction


    /**
      * ShowHandlesChangeListener implements the ChangeListener interface
      * to toggle the state of showing the handles in the tree.
      */
    class ShowHandlesChangeListener extends Object implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) {
	    tree.setShowsRootHandles(((JCheckBox)e.getSource()).isSelected());
	}

    } // End of class SampleTree.ShowHandlesChangeListener


    /**
      * ShowRootChangeListener implements the ChangeListener interface
      * to toggle the state of showing the root node in the tree.
      */
    class ShowRootChangeListener extends Object implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) {
	    tree.setRootVisible(((JCheckBox)e.getSource()).isSelected());
	}

    } // End of class SampleTree.ShowRootChangeListener


    /**
      * TreeEditableChangeListener implements the ChangeListener interface
      * to toggle between allowing editing and now allowing editing in
      * the tree.
      */
    class TreeEditableChangeListener extends Object implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) {
	    tree.setEditable(((JCheckBox)e.getSource()).isSelected());
	}

    } // End of class SampleTree.TreeEditableChangeListener


    static public void main(String args[]) {
	new SampleTree();
    }

}
