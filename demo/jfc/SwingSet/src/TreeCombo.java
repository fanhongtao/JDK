/*
 * @(#)TreeCombo.java	1.8 99/04/23
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







