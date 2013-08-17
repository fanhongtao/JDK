/*
 * @(#)GridLayout.java	1.15 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package java.awt;

/**
 * A layout manager for a container that lays out grids.
 *
 *
 * @version 1.15, 11/23/96
 * @author 
 */
public class GridLayout implements LayoutManager, java.io.Serializable {
    int hgap;
    int vgap;
    int rows;
    int cols;

    /**
     * Creates a grid layout with a default of one column per component,
     * in a single row.
     */
    public GridLayout() {
	this(1, 0, 0, 0);
    }

    /**
     * Creates a grid layout with the specified rows and columns.
     * @param rows the rows
     * @param cols the columns
     */
    public GridLayout(int rows, int cols) {
	this(rows, cols, 0, 0);
    }

    /**
     * Creates a grid layout with the specified rows, columns,
     * horizontal gap, and vertical gap.
     * @param rows the rows; zero means 'any number.'
     * @param cols the columns; zero means 'any number.'  Only one of 'rows'
     * and 'cols' can be zero, not both.
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     * @exception IllegalArgumentException If the rows and columns are invalid.
     */
    public GridLayout(int rows, int cols, int hgap, int vgap) {
	if ((rows == 0) && (cols == 0)) {
	    throw new IllegalArgumentException("rows and cols cannot both be zero");
	}
	this.rows = rows;
	this.cols = cols;
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Returns the number of rows in this layout.
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of rows in this layout.
     * @param rows number of rows in this layout
     */
    public void setRows(int rows) {
	if ((rows == 0) && (this.cols == 0)) {
	    throw new IllegalArgumentException("rows and cols cannot both be zero");
	}
	this.rows = rows;
    }

    /**
     * Returns the number of columns in this layout.
     */
    public int getColumns() {
	return cols;
    }

    /**
     * Sets the number of columns in this layout.
     * @param cols number of columns in this layout
     */
    public void setColumns(int cols) {
	if ((cols == 0) && (this.rows == 0)) {
	    throw new IllegalArgumentException("rows and cols cannot both be zero");
	}
	this.cols = cols;
    }

    /**
     * Returns the horizontal gap between components.
     */
    public int getHgap() {
	return hgap;
    }
    
    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }
    
    /**
     * Returns the vertical gap between components.
     */
    public int getVgap() {
	return vgap;
    }
    
    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component with the specified name to the layout.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout. Does not apply.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
    }

    /** 
     * Returns the preferred dimensions for this layout given the components
     * int the specified panel.
     * @param parent the component which needs to be laid out 
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int nrows = rows;
	int ncols = cols;

	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.getPreferredSize();
	    if (w < d.width) {
		w = d.width;
	    }
	    if (h < d.height) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + ncols*w + (ncols-1)*hgap, 
			     insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
    }

    /**
     * Returns the minimum dimensions needed to layout the components 
     * contained in the specified panel.
     * @param parent the component which needs to be laid out 
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int nrows = rows;
	int ncols = cols;

	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.getMinimumSize();
	    if (w < d.width) {
		w = d.width;
	    }
	    if (h < d.height) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + ncols*w + (ncols-1)*hgap, 
			     insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
    }

    /** 
     * Lays out the container in the specified panel.  
     * @param parent the specified component being laid out
     * @see Container
     */
    public void layoutContainer(Container parent) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int nrows = rows;
	int ncols = cols;

	if (ncomponents == 0) {
	    return;
	}
	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = parent.width - (insets.left + insets.right);
	int h = parent.height - (insets.top + insets.bottom);
	w = (w - (ncols - 1) * hgap) / ncols;
	h = (h - (nrows - 1) * vgap) / nrows;

	for (int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) {
	    for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
		int i = r * ncols + c;
		if (i < ncomponents) {
		    parent.getComponent(i).setBounds(x, y, w, h);
		}
	    }
	}
    }
    
    /**
     * Returns the String representation of this GridLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + 
	    			       ",rows=" + rows + ",cols=" + cols + "]";
    }
}
