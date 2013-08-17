/*
 * @(#)TreePath.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.tree;

import java.io.*;
import java.util.Vector;

/**
 * Represents a path to a node. TreePath is Serializable, but if any 
 * components of the path are not serializable, it will not be written 
 * out.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.20 08/28/98
 * @author Scott Violet
 * @author Philip Milne
 */
public class TreePath extends Object implements Serializable {
    /** Path representing the parent, null if lastPathComponent represents
     * the root. */
    private TreePath           parentPath;
    /** Last path component. */
    transient private Object   lastPathComponent;

    /**
     * Constructs a path from an array of Objects, uniquely identifying 
     * the path from the root of the tree to a specific node, as returned
     * by the tree's data model.
     * <p>
     * The model is free to return an array of any Objects it needs to 
     * represent the path. The DefaultTreeModel returns an array of 
     * TreeNode objects. The first TreeNode in the path is the root of the
     * tree, the last TreeNode is the node identified by the path.
     *
     * @param path  an array of Objects representing the path to a node
     */
    public TreePath(Object[] path) {
        if(path == null || path.length == 0)
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
	lastPathComponent = path[path.length - 1];
	if(path.length > 1)
	    parentPath = new TreePath(path, path.length - 1);
    }

    /**
     * Constructs a TreePath when there is only item in the path.
     * <p>
     * @param singlePath  an Object representing the path to a node
     * @see #TreePath(Object[])
     */
    public TreePath(Object singlePath) {
        if(singlePath == null)
            throw new IllegalArgumentException("path in TreePath must be non null.");
	lastPathComponent = singlePath;
	parentPath = null;
    }

    /**
     * Constructs a TreePath this is the combination of all the path elements
     * in <code>parent</code> with a last path component of
     * <code>lastElement</code>.
     */
    protected TreePath(TreePath parent, Object lastElement) {
	if(lastElement == null)
            throw new IllegalArgumentException("path in TreePath must be non null.");
	parentPath = parent;
	lastPathComponent = lastElement;
    }

    protected TreePath(Object[] path, int length) {
	lastPathComponent = path[length - 1];
	if(length > 1)
	    parentPath = new TreePath(path, length - 1);
    }

    /**
     * Primarily provided for subclasses that don't wish to use the path ivar.
     * If a subclass uses this, it should also subclass getPath(),
     * getPathCount(), getPathComponent() and possibly equals.
     */
    protected TreePath() {
    }

    /**
     * Returns an array of Objects containing the components of this
     * TreePath.
     *
     * @return an array of Objects representing the TreePath
     * @see #TreePath(Object[])
     */
    public Object[] getPath() {
	int            i = getPathCount();
        Object[]       result = new Object[i--];

        for(TreePath path = this; path != null; path = path.parentPath) {
            result[i--] = path.lastPathComponent;
        }
	return result;
    }

    /**
     * Returns the last component of this path. For a path
     * returned by the DefaultTreeModel, that is the TreeNode object
     * for the node specified by the path.
     *
     * @return the Object at the end of the path
     * @see #TreePath(Object[])
     */
    public Object getLastPathComponent() {
	return lastPathComponent;
    }

    /**
     * Returns the number of elements in the path.
     *
     * @return an int giving a count of items the path
     */
    public int getPathCount() {
        int        result = 0;
        for(TreePath path = this; path != null; path = path.parentPath) {
            result++;
        }
	return result;
    }

    /**
     * Returns the path component at the specified index.
     *
     * @param element  an int specifying an element in the path, where
     *                 0 is the first element in the path
     * @return the Object at that index location
     * @throws IllegalArgumentException if the index is beyond the length
     *         of the path
     * @see #TreePath(Object[])
     */
    public Object getPathComponent(int element) {
        int          pathLength = getPathCount();

        if(element < 0 || element >= pathLength)
            throw new IllegalArgumentException("Index " + element + " is out of the specified range");

        TreePath         path = this;

        for(int i = pathLength-1; i != element; i--) {
           path = path.parentPath;
        }
	return path.lastPathComponent;
    }

    /**
     * Tests two TreePaths for equality by checking each element of the
     * paths for equality.
     *
     * @param o the Object to compare
     */
    public boolean equals(Object o) {
	if(o == this)
	    return true;
        if(o instanceof TreePath) {
            TreePath            oTreePath = (TreePath)o;

	    if(getPathCount() != oTreePath.getPathCount())
		return false;
	    for(TreePath path = this; path != null; path = path.parentPath) {
		if (!(path.lastPathComponent.equals
		      (oTreePath.lastPathComponent))) {
		    return false;
		}
		oTreePath = oTreePath.parentPath;
	    }
	    return true;
        }
        return false;
    }

    /**
     * Returns the hashCode for the object. The hash code of a TreePath
     * is defined to be the hash code of the last component in the path.
     *
     * @return the hashCode for the object
     */
    public int hashCode() { 
	return lastPathComponent.hashCode();
    }

    /**
     * Returns true if the specified node is a descendant of this
     * TreePath. A TreePath, child, is a descendent of another TreePath,
     * parent, if child contains all of the components that make up 
     * parent's path.
     *
     * @return true if aTreePath is a descendant of the receiver.
     */
    public boolean isDescendant(TreePath aTreePath) {
	if(aTreePath == this)
	    return true;

        if(aTreePath != null) {
            int                 pathLength = getPathCount();
	    int                 oPathLength = aTreePath.getPathCount();

	    if(oPathLength < pathLength)
		// Can't be a descendant, has fewer components in the path.
		return false;
	    while(oPathLength-- > pathLength)
		aTreePath = aTreePath.getParentPath();
	    return equals(aTreePath);
        }
        return false;
    }

    /**
     * Returns a new path containing all the elements of this receiver
     * plus <code>child</code>. This will throw a NullPointerException
     * if child is null.
     */
    public TreePath pathByAddingChild(Object child) {
	if(child == null)
	    throw new NullPointerException("Null child not allowed");

	return new TreePath(this, child);
    }

    /**
     * Returns a path containing all the elements of the receiver, accept
     * the last path component.
     */
    public TreePath getParentPath() {
	return parentPath;
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        StringBuffer tempSpot = new StringBuffer("[");

        for(int counter = 0, maxCounter = getPathCount();counter < maxCounter;
	    counter++) {
            if(counter > 0)
                tempSpot.append(", ");
            tempSpot.append(getPathComponent(counter));
        }
        tempSpot.append("]");
        return tempSpot.toString();
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        Vector      values = new Vector();
        boolean     writePath = true;

	if(lastPathComponent != null &&
	   (lastPathComponent instanceof Serializable)) {
            values.addElement("lastPathComponent");
            values.addElement(lastPathComponent);
        }
        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        Vector          values = (Vector)s.readObject();
        int             indexCounter = 0;
        int             maxCounter = values.size();

        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("lastPathComponent")) {
            lastPathComponent = values.elementAt(++indexCounter);
            indexCounter++;
        }
    }
}
