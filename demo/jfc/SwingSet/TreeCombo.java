/*
 * @(#)TreeCombo.java	1.6 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.util.Vector;
import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;

public class TreeCombo extends JComboBox {
    static final int OFFSET = 16;

    public TreeCombo(TreeModel aTreeModel) {
        super();
        setModel(new TreeToListModel(aTreeModel));
        setRenderer(new ListEntryRenderer());
    }

    class TreeToListModel extends AbstractListModel implements ComboBoxModel,TreeModelListener {
        TreeModel source;
        boolean invalid = true;
        Object currentValue;
        Vector cache = new Vector();

        public TreeToListModel(TreeModel aTreeModel) {
            source = aTreeModel;
            aTreeModel.addTreeModelListener(this);
            setRenderer(new ListEntryRenderer());
        }

        public void setSelectedItem(Object anObject) {
            currentValue = anObject;
            fireContentsChanged(this, -1, -1);
        }

        public Object getSelectedItem() {
            return currentValue;
        }

        public int getSize() {
            validate();
            return cache.size();
        }

        public Object getElementAt(int index) {
            return cache.elementAt(index);
        }

        public void treeNodesChanged(TreeModelEvent e) {
            invalid = true;
        }

        public void treeNodesInserted(TreeModelEvent e) {
            invalid = true;
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            invalid = true;
        }

        public void treeStructureChanged(TreeModelEvent e) {
            invalid = true;
        }

        void validate() {
            if(invalid) {
                cache = new Vector();
                cacheTree(source.getRoot(),0);
                if(cache.size() > 0)
                    currentValue = cache.elementAt(0);
                invalid = false;             
                fireContentsChanged(this, 0, 0);
            }
        }

        void cacheTree(Object anObject,int level) {
            if(source.isLeaf(anObject))
                addListEntry(anObject,level,false);
            else {
                int c = source.getChildCount(anObject);
                int i;
                Object child;

                addListEntry(anObject,level,true);
                level++;

                for(i=0;i<c;i++) {
                    child = source.getChild(anObject,i);
                    cacheTree(child,level);
                }

                level--;
            }
        }

        void addListEntry(Object anObject,int level,boolean isNode) {
            cache.addElement(new ListEntry(anObject,level,isNode));
        }
    }

    class ListEntry {
        Object object;
        int    level;
        boolean isNode;

        public ListEntry(Object anObject,int aLevel,boolean isNode) {
            object = anObject;
            level = aLevel;
            this.isNode = isNode;
        }

        public Object object() {
            return object;
        }

        public int level() {
            return level;
        }

        public boolean isNode() {
            return isNode;
        }
    }

    static Border emptyBorder = new EmptyBorder(0,0,0,0);

    class ListEntryRenderer extends JLabel implements ListCellRenderer  {
        ImageIcon leafIcon = SwingSet.sharedInstance().loadImageIcon("images/document.gif","Document");
        ImageIcon nodeIcon = SwingSet.sharedInstance().loadImageIcon("images/folder.gif","Folder");

        public ListEntryRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(
            JList listbox, 
	    Object value, 
	    int index,
	    boolean isSelected,
	    boolean cellHasFocus)
	{
            ListEntry listEntry = (ListEntry)value;
            if(listEntry != null) {
                Border border;
                setText(listEntry.object().toString());
		setIcon( listEntry.isNode() ? nodeIcon : leafIcon );
                if(index != -1)
                    border = new EmptyBorder(0, OFFSET * listEntry.level(), 0, 0);
                else 
                    border = emptyBorder;

                if(UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
                    if(index == -1 )
                        setOpaque(false);
                    else
                        setOpaque(true);
                } else 
                    setOpaque(true);
                
		setBorder(border); 
                if (isSelected) {
                    setBackground(UIManager.getColor("ComboBox.selectionBackground"));
                    setForeground(UIManager.getColor("ComboBox.selectionForeground"));
                } else {
                    setBackground(UIManager.getColor("ComboBox.background"));
                    setForeground(UIManager.getColor("ComboBox.foreground"));
                }
            } else {
                setText("");
            }
	    return this;
	}
    }
}







