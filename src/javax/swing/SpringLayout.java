/*
 * @(#)SpringLayout.java	1.19 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.util.*;

/**
 * A <code>SpringLayout</code> lays out the children of its associated container
 * according to a set of constraints.
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/layout/spring.html">How to Use SpringLayout</a>
 * in <em>The Java Tutorial</em> for examples of using
 * <code>SpringLayout</code>.
 *
 * <p>
 * Each constraint,
 * represented by a <code>Spring</code> object,
 * controls the vertical or horizontal distance
 * between two component edges.
 * The edges can belong to
 * any child of the container,
 * or to the container itself.
 * For example,
 * the allowable width of a component
 * can be expressed using a constraint
 * that controls the distance between the west (left) and east (right) 
 * edges of the component.
 * The allowable <em>y</em> coordinates for a component
 * can be expressed by constraining the distance between
 * the north (top) edge of the component
 * and the north edge of its container.
 *
 * <P>
 * Every child of a <code>SpringLayout</code>-controlled container,
 * as well as the container itself,
 * has exactly one set of constraints
 * associated with it.
 * These constraints are represented by
 * a <code>SpringLayout.Constraints</code> object.
 * By default,
 * <code>SpringLayout</code> creates constraints
 * that make their associated component
 * have the minimum, preferred, and maximum sizes
 * returned by the component's
 * {@link java.awt.Component#getMinimumSize},
 * {@link java.awt.Component#getPreferredSize}, and
 * {@link java.awt.Component#getMaximumSize}
 * methods. The <em>x</em> and <em>y</em> positions are initially not
 * constrained, so that until you constrain them the <code>Component</code>
 * will be positioned at 0,0 relative to the <code>Insets</code> of the
 * parent <code>Container</code>.
 * 
 * <p>
 * You can change 
 * a component's constraints in several ways.
 * You can 
 * use one of the 
 * {@link #putConstraint putConstraint}
 * methods
 * to establish a spring
 * linking the edges of two components within the same container.
 * Or you can get the appropriate <code>SpringLayout.Constraints</code>
 * object using 
 * {@link #getConstraints getConstraints}
 * and then modify one or more of its springs.
 * Or you can get the spring for a particular edge of a component
 * using {@link #getConstraint getConstraint},
 * and modify it.
 * You can also associate
 * your own <code>SpringLayout.Constraints</code> object 
 * with a component by specifying the constraints object
 * when you add the component to its container
 * (using 
 * {@link Container#add(Component, Object)}).
 *
 * <p>
 * The <code>Spring</code> object representing each constraint 
 * has a minimum, preferred, maximum, and current value.
 * The current value of the spring 
 * is somewhere between the minimum and maximum values,
 * according to the formula given in the
 * {@link Spring#sum} method description.
 * When the minimum, preferred, and maximum values are the same,
 * the current value is always equal to them;
 * this inflexible spring is called a <em>strut</em>.
 * You can create struts using the factory method
 * {@link Spring#constant(int)}.
 * The <code>Spring</code> class also provides factory methods
 * for creating other kinds of springs,
 * including springs that depend on other springs.
 *
 * <p>
 * In a <code>SpringLayout</code>, the position of each edge is dependent on
 * the position of just one other edge. If a constraint is subsequently added
 * to create a new binding for an edge, the previous binding is discarded
 * and the edge remains dependent on a single edge. 
 * Springs should only be attached
 * between edges of the container and its immediate children; the behavior
 * of the <code>SpringLayout</code> when presented with constraints linking
 * the edges of components from different containers (either internal or
 * external) is undefined.
 *
 * <h3>
 * SpringLayout vs. Other Layout Managers
 * </h3>
 *
 * <blockquote>
 * <hr>
 * <strong>Note:</strong>
 * Unlike many layout managers,
 * <code>SpringLayout</code> doesn't automatically set the location of
 * the components it manages.
 * If you hand-code a GUI that uses <code>SpringLayout</code>, 
 * remember to initialize component locations by constraining the west/east
 * and north/south locations.
 * <p>
 * Depending on the constraints you use,
 * you may also need to set the size of the container explicitly.
 * <hr>
 * </blockquote>
 *
 * <p>
 * Despite the simplicity of <code>SpringLayout</code>,
 * it can emulate the behavior of most other layout managers.
 * For some features,
 * such as the line breaking provided by <code>FlowLayout</code>, 
 * you'll need to 
 * create a special-purpose subclass of the <code>Spring</code> class.
 *
 * <p>
 * <code>SpringLayout</code> also provides a way to solve
 * many of the difficult layout
 * problems that cannot be solved by nesting combinations
 * of <code>Box</code>es. That said, <code>SpringLayout</code> honors the
 * <code>LayoutManager2</code> contract correctly and so can be nested with
 * other layout managers -- a technique that can be preferable to
 * creating the constraints implied by the other layout managers.
 * <p>
 * The asymptotic complexity of the layout operation of a <code>SpringLayout</code>
 * is linear in the number of constraints (and/or components).
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @see Spring
 * @see SpringLayout.Constraints
 *
 * @version  1.19 12/19/03
 * @author 	Philip Milne
 * @author 	Joe Winchester
 * @since       1.4
 */
public class SpringLayout implements LayoutManager2 {
    private Map componentConstraints = new HashMap();

    private Spring cyclicReference = Spring.constant(Spring.UNSET);
    private Set cyclicSprings;
    private Set acyclicSprings;


    /**
     * Specifies the top edge of a component's bounding rectangle.
     */
    public static final String NORTH  = "North";

    /**
     * Specifies the bottom edge of a component's bounding rectangle.
     */
    public static final String SOUTH  = "South";

    /**
     * Specifies the right edge of a component's bounding rectangle.
     */
    public static final String EAST   = "East";

    /**
     * Specifies the left edge of a component's bounding rectangle.
     */
    public static final String WEST   = "West";


    /**
     * A <code>Constraints</code> object holds the
     * constraints that govern the way a component's size and position
     * change in a container controlled by a <code>SpringLayout</code>.
     * A <code>Constraints</code> object is
     * like a <code>Rectangle</code>, in that it
     * has <code>x</code>, <code>y</code>,
     * <code>width</code>, and <code>height</code> properties.
     * In the <code>Constraints</code> object, however,
     * these properties have
     * <code>Spring</code> values instead of integers.
     * In addition,
     * a <code>Constraints</code> object
     * can be manipulated as four edges
     * -- north, south, east, and west --
     * using the <code>constraint</code> property.
     * 
     * <p>
     * The following formulas are always true
     * for a <code>Constraints</code> object:
     *
     * <pre>
     *       west = x
     *      north = y
     *       east = x + width
     *      south = y + height</pre>
     *
     * <b>Note</b>: In this document,
     * operators represent methods 
     * in the <code>Spring</code> class.
     * For example, "a + b" is equal to
     * <code>Spring.sum(a, b)</code>,
     * and "a - b" is equal to
     * <code>Spring.sum(a, Spring.minus(b))</code>.
     * See the 
     * {@link Spring Spring</code> API documentation<code>}
     * for further details
     * of spring arithmetic.
     *
     * <p>
     * 
     * Because a <code>Constraints</code> object's properties --
     * representing its edges, size, and location -- can all be set
     * independently and yet are interrelated,
     * the object can become <em>over-constrained</em>.
     * For example,
     * if both the <code>x</code> and <code>width</code>
     * properties are set
     * and then the east edge is set,
     * the object is over-constrained horizontally.
     * When this happens, one of the values 
     * (in this case, the <code>x</code> property)
     * automatically changes so
     * that the formulas still hold. 
     *
     * <p>
     * The following table shows which value changes
     * when a <code>Constraints</code> object
     * is over-constrained horizontally.
     *
     * <p>
     *
     * <table border=1 summary="Shows which value changes when a Constraints object is over-constrained horizontally">
     *   <tr>
     *     <th valign=top>Value Being Set<br>(method used)</th>
     *     <th valign=top>Result When Over-Constrained Horizontally<br>
     *      (<code>x</code>, <code>width</code>, and the east edge are all non-<code>null</code>)</th>
     *   </tr>
     *   <tr>
     *     <td><code>x</code> or the west edge <br>(<code>setX</code> or <code>setConstraint</code>)</td>
     *     <td><code>width</code> value is automatically set to <code>east - x</code>.</td>
     *   </tr>
     *   <tr>
     *     <td><code>width</code><br>(<code>setWidth</code>)</td>
     *     <td>east edge's value is automatically set to <code>x + width</code>.</td>
     *   </tr>
     *   <tr>
     *     <td>east edge<br>(<code>setConstraint</code>)</td>
     *     <td><code>x</code> value is automatically set to <code>east - width</code>.</td>
     *   </tr>
     *   </table>
     *
     * <p>
     * The rules for the vertical properties are similar:
     * <p>
     *
     * <table border=1 summary="Shows which value changes when a Constraints object is over-constrained vertically">
     * <tr>
     *  <th valign=top>Value Being Set<br>(method used)</th>
     *  <th valign=top>Result When Over-Constrained Vertically<br>(<code>y</code>, <code>height</code>, and the south edge are all non-<code>null</code>)</th>
     * </tr>
     * <tr>
     *   <td><code>y</code> or the north edge<br>(<code>setY</code> or <code>setConstraint</code>)</td>
     *   <td><code>height</code> value is automatically set to <code>south - y</code>.</td>
     * </tr>
     * <tr>
     *   <td><code>height</code><br>(<code>setHeight</code>)</td>
     *   <td>south edge's value is automatically set to <code>y + height</code>.</td>
     * </tr>
     * <tr>
     *   <td>south edge<br>(<code>setConstraint</code>)</td>
     *   <td><code>y</code> value is automatically set to <code>south - height</code>.</td>
     * </tr>
     * </table>
     *
     */
   public static class Constraints {
       private Spring x;
       private Spring y;
       private Spring width;
       private Spring height;
       private Spring east;
       private Spring south;

       private Spring verticalDerived = null; 
       private Spring horizontalDerived = null; 
       
       /**
        * Creates an empty <code>Constraints</code> object.
        */
       public Constraints() {
           this(null, null, null, null);
       }

       /**
        * Creates a <code>Constraints</code> object with the
	* specified values for its
        * <code>x</code> and <code>y</code> properties.
        * The <code>height</code> and <code>width</code> springs
	* have <code>null</code> values.
        *
        * @param x  the spring controlling the component's <em>x</em> value
        * @param y  the spring controlling the component's <em>y</em> value
        */
       public Constraints(Spring x, Spring y) {
           this(x, y, null, null);
       }

       /**
        * Creates a <code>Constraints</code> object with the 
	* specified values for its
        * <code>x</code>, <code>y</code>, <code>width</code>,
	* and <code>height</code> properties.
        * Note: If the <code>SpringLayout</code> class
	* encounters <code>null</code> values in the
	* <code>Constraints</code> object of a given component,
	* it replaces them with suitable defaults.
        *
        * @param x  the spring value for the <code>x</code> property
        * @param y  the spring value for the <code>y</code> property
        * @param width  the spring value for the <code>width</code> property
        * @param height  the spring value for the <code>height</code> property
        */
       public Constraints(Spring x, Spring y, Spring width, Spring height) {
           this.x = x;
           this.y = y;
           this.width = width;
           this.height = height;
       }

        /**
         * Creates a <code>Constraints</code> object with
         * suitable <code>x</code>, <code>y</code>, <code>width</code> and
         * <code>height</code> springs for component, <code>c</code>.
         * The <code>x</code> and <code>y</code> springs are constant
         * springs  initialised with the component's location at
         * the time this method is called. The <code>width</code> and
         * <code>height</code> springs are special springs, created by
         * the <code>Spring.width()</code> and <code>Spring.height()</code>
         * methods, which track the size characteristics of the component
         * when they change.
         *
         * @param c  the component whose characteristics will be reflected by this Constraints object
         * @throws NullPointerException if <code>c</code> is null.
         * @since 1.5
         */
        public Constraints(Component c) {
            this.x = Spring.constant(c.getX());
            this.y = Spring.constant(c.getY());
            this.width = Spring.width(c);
            this.height = Spring.height(c);
        }

       private boolean overConstrainedHorizontally() { 
           return (x != null) && (width != null) && (east != null); 
       }
       
       private boolean overConstrainedVertically() { 
           return (y != null) && (height != null) && (south != null); 
       }
       
       private Spring sum(Spring s1, Spring s2) { 
           return (s1 == null || s2 == null) ? null : Spring.sum(s1, s2); 
       }
        
       private Spring difference(Spring s1, Spring s2) { 
           return (s1 == null || s2 == null) ? null : Spring.difference(s1, s2); 
       }
        
       /**
        * Sets the <code>x</code> property,
	* which controls the <code>x</code> value
	* of a component's location.
        *
        * @param x the spring controlling the <code>x</code> value
	*          of a component's location
        *
        * @see #getX
        * @see SpringLayout.Constraints
        */
       public void setX(Spring x) {
           this.x = x;
           horizontalDerived = null; 
           if (overConstrainedHorizontally()) { 
               width = null; 
           }
       }

       /**
        * Returns the value of the <code>x</code> property.
        *
        * @return the spring controlling the <code>x</code> value
	*         of a component's location
        *
        * @see #setX
        * @see SpringLayout.Constraints
        */
       public Spring getX() {
           if (x != null) { 
               return x; 
           }
           if (horizontalDerived == null) { 
               horizontalDerived = difference(east, width); 
           } 
           return horizontalDerived; 
       }

       /**
        * Sets the <code>y</code> property,
	* which controls the <code>y</code> value
	* of a component's location.
        *
        * @param y the spring controlling the <code>y</code> value
	*          of a component's location
        *
        * @see #getY
        * @see SpringLayout.Constraints
        */
       public void setY(Spring y) {
           this.y = y;
           verticalDerived = null; 
           if (overConstrainedVertically()) { 
               height = null; 
           }
       }

       /**
        * Returns the value of the <code>y</code> property.
        *
        * @return the spring controlling the <code>y</code> value
	*         of a component's location
        *
        * @see #setY
        * @see SpringLayout.Constraints
        */
       public Spring getY() {
           if (y != null) { 
               return y; 
           }
           if (verticalDerived == null) { 
               verticalDerived = difference(south, height); 
           } 
           return verticalDerived; 
       }

       /**
        * Sets the <code>width</code> property,
	* which controls the width of a component.
        *
        * @param width the spring controlling the width of this
	* <code>Constraints</code> object
        *
        * @see #getWidth
        * @see SpringLayout.Constraints
        */
       public void setWidth(Spring width) {
           this.width = width;
           horizontalDerived = null; 
           if (overConstrainedHorizontally()) { 
               east = null; 
           }
       }

       /**
        * Returns the value of the <code>width</code> property.
        *
        * @return the spring controlling the width of a component
        *
        * @see #setWidth
        * @see SpringLayout.Constraints
        */
       public Spring getWidth() {
           if (width != null) { 
               return width; 
           }
           if (horizontalDerived == null) { 
               horizontalDerived = difference(east, x); 
           } 
           return horizontalDerived; 
       }

       /**
        * Sets the <code>height</code> property,
	* which controls the height of a component.
        *
        * @param height the spring controlling the height of this <code>Constraints</code>
	* object
        *
        * @see #getHeight
        * @see SpringLayout.Constraints
        */
       public void setHeight(Spring height) {
           this.height = height;
           verticalDerived = null; 
           if (overConstrainedVertically()) { 
               south = null; 
           }
       }

       /**
        * Returns the value of the <code>height</code> property.
        *
        * @return the spring controlling the height of a component
        *
        * @see #setHeight
        * @see SpringLayout.Constraints
        */
       public Spring getHeight() {
           if (height != null) { 
               return height; 
           }
           if (verticalDerived == null) { 
               verticalDerived = difference(south, y); 
           } 
           return verticalDerived; 
       }

       private void setEast(Spring east) {
           this.east = east;
           horizontalDerived = null; 
           if (overConstrainedHorizontally()) { 
               x = null; 
           }
       }

       private Spring getEast() {
           if (east != null) { 
               return east; 
           }
           if (horizontalDerived == null) { 
               horizontalDerived = sum(x, width); 
           } 
           return horizontalDerived; 
       }

       private void setSouth(Spring south) {
           this.south = south;
           verticalDerived = null; 
           if (overConstrainedVertically()) { 
               y = null; 
           }
       }

       private Spring getSouth() {
           if (south != null) { 
               return south; 
           }
           if (verticalDerived == null) { 
               verticalDerived = sum(y, height); 
           } 
           return verticalDerived; 
       }

       /**
        * Sets the spring controlling the specified edge.
        * The edge must have one of the following values:
        * <code>SpringLayout.NORTH</code>, <code>SpringLayout.SOUTH</code>,
	* <code>SpringLayout.EAST</code>, <code>SpringLayout.WEST</code>.
        *
        * @param edgeName the edge to be set
        * @param s the spring controlling the specified edge
        *
        * @see #getConstraint
	* @see #NORTH
	* @see #SOUTH
	* @see #EAST
	* @see #WEST
        * @see SpringLayout.Constraints
        */
       public void setConstraint(String edgeName, Spring s) {
           edgeName = edgeName.intern();
           if (edgeName == "West") {
               setX(s);
           }
           else if (edgeName == "North") {
               setY(s);
           }
           else if (edgeName == "East") {
               setEast(s);
           }
           else if (edgeName == "South") {
               setSouth(s);
           }
       }

       /**
        * Returns the value of the specified edge.
        * The edge must have one of the following values:
        * <code>SpringLayout.NORTH</code>, <code>SpringLayout.SOUTH</code>,
	* <code>SpringLayout.EAST</code>, <code>SpringLayout.WEST</code>.
        *
        * @param edgeName the edge whose value
	*                 is to be returned
        *
        * @return the spring controlling the specified edge
        *
        * @see #setConstraint
	* @see #NORTH
	* @see #SOUTH
	* @see #EAST
	* @see #WEST
        * @see SpringLayout.Constraints
        */
       public Spring getConstraint(String edgeName) {
           edgeName = edgeName.intern();
           return (edgeName == "West")  ? getX() :
                  (edgeName == "North") ? getY() :
                  (edgeName == "East")  ? getEast() :
                  (edgeName == "South") ? getSouth() :
                  null;
       }

       /*pp*/ void reset() {
           if (x != null) x.setValue(Spring.UNSET);
           if (y != null) y.setValue(Spring.UNSET);
           if (width != null) width.setValue(Spring.UNSET);
           if (height != null) height.setValue(Spring.UNSET);
           if (east != null) east.setValue(Spring.UNSET);
           if (south != null) south.setValue(Spring.UNSET);
           if (horizontalDerived != null) horizontalDerived.setValue(Spring.UNSET);
           if (verticalDerived != null) verticalDerived.setValue(Spring.UNSET);
       }
   }

   private static class SpringProxy extends Spring {
       private String edgeName;
       private Component c;
       private SpringLayout l;

       public SpringProxy(String edgeName, Component c, SpringLayout l) {
           this.edgeName = edgeName;
           this.c = c;
           this.l = l;
       }

       private Spring getConstraint() { 
           return l.getConstraints(c).getConstraint(edgeName);
       }

       public int getMinimumValue() {
           return getConstraint().getMinimumValue();
       }

       public int getPreferredValue() {
           return getConstraint().getPreferredValue();
       }

       public int getMaximumValue() {
           return getConstraint().getMaximumValue();
       }

       public int getValue() {
           return getConstraint().getValue();
       }

       public void setValue(int size) {
           getConstraint().setValue(size);
       }

       /*pp*/ boolean isCyclic(SpringLayout l) {
           return l.isCyclic(getConstraint()); 
       }

       public String toString() {
           return "SpringProxy for " + edgeName + " edge of " + c.getName() + ".";
       }
    }

    /**
     * Constructs a new <code>SpringLayout</code>.
     */
    public SpringLayout() {}

    private void resetCyclicStatuses() { 
        cyclicSprings = new HashSet();
        acyclicSprings = new HashSet();
    }

    private void setParent(Container p) { 
        resetCyclicStatuses(); 
        Constraints pc = getConstraints(p);
        
        pc.setX(Spring.constant(0));
        pc.setY(Spring.constant(0));
        // The applyDefaults() method automatically adds width and
        // height springs that delegate their calculations to the
        // getMinimumSize(), getPreferredSize() and getMaximumSize()
        // methods of the relevant component. In the case of the
        // parent this will cause an infinite loop since these
        // methods, in turn, delegate their calculations to the
        // layout manager. Check for this case and replace the
        // the springs that would cause this problem with a
        // constant springs that supply default values.
        Spring width = pc.getWidth();
        if (width instanceof Spring.WidthSpring && ((Spring.WidthSpring)width).c == p) {
            pc.setWidth(Spring.constant(0, 0, Integer.MAX_VALUE));
        }
        Spring height = pc.getHeight();
        if (height instanceof Spring.HeightSpring && ((Spring.HeightSpring)height).c == p) {
            pc.setHeight(Spring.constant(0, 0, Integer.MAX_VALUE));
        }
    }

    /*pp*/ boolean isCyclic(Spring s) { 
        if (s == null) {
            return false;
        }
        if (cyclicSprings.contains(s)) {
            return true;
        }
        if (acyclicSprings.contains(s)) {
            return false;
        }
        cyclicSprings.add(s);
        boolean result = s.isCyclic(this);
        if (!result) {
            acyclicSprings.add(s);
            cyclicSprings.remove(s);
        }
        else {
            System.err.println(s + " is cyclic. ");
        }
        return result;
    }

    private Spring abandonCycles(Spring s) { 
        return isCyclic(s) ? cyclicReference : s;
    }

    // LayoutManager methods.

    /**
     * Has no effect,
     * since this layout manager does not
     * use a per-component string.
     */
    public void addLayoutComponent(String name, Component c) {}

    /**
     * Removes the constraints associated with the specified component.
     *
     * @param c the component being removed from the container
     */
    public void removeLayoutComponent(Component c) {
        componentConstraints.remove(c);
    }

    private static Dimension addInsets(int width, int height, Container p) {
        Insets i = p.getInsets();
        return new Dimension(width + i.left + i.right, height + i.top + i.bottom);
    }

    public Dimension minimumLayoutSize(Container parent) {
        setParent(parent);
        Constraints pc = getConstraints(parent); 
        return addInsets(abandonCycles(pc.getWidth()).getMinimumValue(),
                         abandonCycles(pc.getHeight()).getMinimumValue(),
                         parent);
    }

    public Dimension preferredLayoutSize(Container parent) {
        setParent(parent);
        Constraints pc = getConstraints(parent); 
        return addInsets(abandonCycles(pc.getWidth()).getPreferredValue(),
                         abandonCycles(pc.getHeight()).getPreferredValue(),
                         parent);
    }

    // LayoutManager2 methods.

    public Dimension maximumLayoutSize(Container parent) {
        setParent(parent);
        Constraints pc = getConstraints(parent); 
        return addInsets(abandonCycles(pc.getWidth()).getMaximumValue(),
                         abandonCycles(pc.getHeight()).getMaximumValue(),
                         parent);
    }

    /**
     * If <code>constraints</code> is an instance of 
     * <code>SpringLayout.Constraints</code>,
     * associates the constraints with the specified component.
     * <p>
     * @param   component the component being added
     * @param   constraints the component's constraints
     *
     * @see SpringLayout.Constraints
     */
    public void addLayoutComponent(Component component, Object constraints) {
        if (constraints instanceof Constraints) {
            putConstraints(component, (Constraints)constraints);
        }
    }

    /**
     * Returns 0.5f (centered).
     */
    public float getLayoutAlignmentX(Container p) {
        return 0.5f;
    }

    /**
     * Returns 0.5f (centered).
     */
    public float getLayoutAlignmentY(Container p) {
        return 0.5f;
    }

    public void invalidateLayout(Container p) {}

    // End of LayoutManger2 methods

   /**
     * Links edge <code>e1</code> of component <code>c1</code> to
     * edge <code>e2</code> of component <code>c2</code>,
     * with a fixed distance between the edges. This
     * constraint will cause the assignment
     * <pre>
     *     value(e1, c1) = value(e2, c2) + pad</pre>
     * to take place during all subsequent layout operations.
     * <p>
     * @param   e1 the edge of the dependent
     * @param   c1 the component of the dependent
     * @param   pad the fixed distance between dependent and anchor
     * @param   e2 the edge of the anchor
     * @param   c2 the component of the anchor
     *
     * @see #putConstraint(String, Component, Spring, String, Component)
     */
    public void putConstraint(String e1, Component c1, int pad, String e2, Component c2) {
        putConstraint(e1, c1, Spring.constant(pad), e2, c2);
    }

    /**
     * Links edge <code>e1</code> of component <code>c1</code> to
     * edge <code>e2</code> of component <code>c2</code>. As edge
     * <code>(e2, c2)</code> changes value, edge <code>(e1, c1)</code> will
     * be calculated by taking the (spring) sum of <code>(e2, c2)</code>
     * and <code>s</code>. Each edge must have one of the following values:
     * <code>SpringLayout.NORTH</code>, <code>SpringLayout.SOUTH</code>,
     * <code>SpringLayout.EAST</code>, <code>SpringLayout.WEST</code>.
     * <p>
     * @param   e1 the edge of the dependent
     * @param   c1 the component of the dependent
     * @param   s the spring linking dependent and anchor
     * @param   e2 the edge of the anchor
     * @param   c2 the component of the anchor
     *
     * @see #putConstraint(String, Component, int, String, Component)
     * @see #NORTH
     * @see #SOUTH
     * @see #EAST
     * @see #WEST
     */
    public void putConstraint(String e1, Component c1, Spring s, String e2, Component c2) {
        putConstraint(e1, c1, Spring.sum(s, getConstraint(e2, c2)));
    }

    private void putConstraint(String e, Component c, Spring s) {
        if (s != null) {
            getConstraints(c).setConstraint(e, s);
        }
     }

    private Constraints applyDefaults(Component c, Constraints constraints) {
        if (constraints == null) {
           constraints = new Constraints();
        }
        if (constraints.getWidth() == null) {
            constraints.setWidth(new Spring.WidthSpring(c));
        }
        if (constraints.getHeight() == null) {
            constraints.setHeight(new Spring.HeightSpring(c));
        }
        if (constraints.getX() == null) {
            constraints.setX(Spring.constant(0));
        }
        if (constraints.getY() == null) {
            constraints.setY(Spring.constant(0));
        }
        return constraints;
    }

    private void putConstraints(Component component, Constraints constraints) {
        componentConstraints.put(component, applyDefaults(component, constraints));
    }

    /**
     * Returns the constraints for the specified component.
     * Note that,
     * unlike the <code>GridBagLayout</code>
     * <code>getConstraints</code> method,
     * this method does not clone constraints.
     * If no constraints
     * have been associated with this component,
     * this method
     * returns a default constraints object positioned at
     * 0,0 relative to the parent's Insets and its width/height
     * constrained to the minimum, maximum, and preferred sizes of the
     * component. The size characteristics
     * are not frozen at the time this method is called;
     * instead this method returns a constraints object
     * whose characteristics track the characteristics
     * of the component as they change.
     *
     * @param       c the component whose constraints will be returned
     *
     * @return      the constraints for the specified component
     */
    public Constraints getConstraints(Component c) {
       Constraints result = (Constraints)componentConstraints.get(c);
       if (result == null) {
           if (c instanceof javax.swing.JComponent) {
                Object cp = ((javax.swing.JComponent)c).getClientProperty(SpringLayout.class);
                if (cp instanceof Constraints) {
                    return applyDefaults(c, (Constraints)cp);
                }
            }
            result = new Constraints();
            putConstraints(c, result);
       }
       return result;
    }

    /**
     * Returns the spring controlling the distance between 
     * the specified edge of
     * the component and the top or left edge of its parent. This
     * method, instead of returning the current binding for the
     * edge, returns a proxy that tracks the characteristics
     * of the edge even if the edge is subsequently rebound.
     * Proxies are intended to be used in builder envonments
     * where it is useful to allow the user to define the
     * constraints for a layout in any order. Proxies do, however,
     * provide the means to create cyclic dependencies amongst
     * the constraints of a layout. Such cycles are detected
     * internally by <code>SpringLayout</code> so that
     * the layout operation always terminates.
     *
     * @param edgeName must be 
     *                 <code>SpringLayout.NORTH</code>,
     *                 <code>SpringLayout.SOUTH</code>,
     *                 <code>SpringLayout.EAST</code>, or
     *                 <code>SpringLayout.WEST</code>
     * @param c the component whose edge spring is desired
     *
     * @return a proxy for the spring controlling the distance between the
     *         specified edge and the top or left edge of its parent
     * 
     * @see #NORTH
     * @see #SOUTH
     * @see #EAST
     * @see #WEST
     */
    public Spring getConstraint(String edgeName, Component c) {
        // The interning here is unnecessary; it was added for efficiency.
        edgeName = edgeName.intern();
        return new SpringProxy(edgeName, c, this);
    }

    public void layoutContainer(Container parent) {
        setParent(parent);

        int n = parent.getComponentCount();
        getConstraints(parent).reset();
        for (int i = 0 ; i < n ; i++) {
            getConstraints(parent.getComponent(i)).reset();
        }

        Insets insets = parent.getInsets();
        Constraints pc = getConstraints(parent); 
        abandonCycles(pc.getX()).setValue(0);
        abandonCycles(pc.getY()).setValue(0);        
        abandonCycles(pc.getWidth()).setValue(parent.getWidth() -
                                              insets.left - insets.right);
        abandonCycles(pc.getHeight()).setValue(parent.getHeight() -
                                               insets.top - insets.bottom);
        
        for (int i = 0 ; i < n ; i++) {
	    Component c = parent.getComponent(i);
            Constraints cc = getConstraints(c); 
            int x = abandonCycles(cc.getX()).getValue();
            int y = abandonCycles(cc.getY()).getValue();
            int width = abandonCycles(cc.getWidth()).getValue();
            int height = abandonCycles(cc.getHeight()).getValue();
            c.setBounds(insets.left + x, insets.top + y, width, height);
	}
    }
}
