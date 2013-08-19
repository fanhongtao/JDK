/*
 * @(#)Spring.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.util.*;

/**
 *  An instance of the <code>Spring</code> class holds three properties that
 *  characterize its behavior: the <em>minimum</em>, <em>preferred</em>, and
 *  <em>maximum</em> values. Each of these properties may be involved in
 *  defining its fourth, <em>value</em>, property based on a series of rules.
 *  <p>
 *  An instance of the <code>Spring</code> class can be visualized as a
 *  mechanical spring that provides a corrective force as the spring is compressed
 *  or stretched away from its preferred value. This force is modelled
 *  as linear function of the distance from the preferred value, but with
 *  two different constants -- one for the compressional force and one for the
 *  tensional one. Those constants are specified by the minimum and maximum
 *  values of the spring such that a spring at its minimum value produces an
 *  equal and opposite force to that which is created when it is at its
 *  maximum value. The difference between the <em>preferred</em> and
 *  <em>minimum</em> values, therefore, represents the ease with which the
 *  spring can be compressed and the difference between its <em>maximum</em>
 *  and <em>preferred</em> values, indicates the ease with which the
 *  <code>Spring</code> can be extended.
 *  See the {@link #sum} method for details.
 *
 *  <p>
 *  By defining simple arithmetic operations on <code>Spring</code>s,
 *  the behavior of a collection of <code>Spring</code>s
 *  can be reduced to that of an ordinary (non-compound) <code>Spring</code>. We define
 *  the "+", "-", <em>max</em>, and <em>min</em> operators on
 *  <code>Spring</code>s so that, in each case, the result is a <code>Spring</code>
 *  whose characteristics bear a useful mathematical relationship to its constituent
 *  springs.
 *
 *  <p>
 *  A <code>Spring</code> can be treated as a pair of intervals
 *  with a single common point: the preferred value.
 *  The following rules define some of the 
 *  arithmetic operators that can be applied to intervals
 *  (<code>[a, b]</code> refers to the interval
 *  from <code>a</code>
 *  to <code>b</code>,
 *  where <code>a &lt;= b</code>).
 *  <p>
 *  <pre>
 *          [a1, b1] + [a2, b2] = [a1 + a2, b1 + b2]
 *
 *                      -[a, b] = [-b, -a]
 *
 *      max([a1, b1], [a2, b2]) = [max(a1, a2), max(b1, b2)]
 *  </pre>
 *  <p>
 *  
 *  If we denote <code>Spring</code>s as <code>[a, b, c]</code>,
 *  where <code>a &lt;= b &lt;= c</code>, we can define the same
 *  arithmetic operators on <code>Spring</code>s:
 *  <p>
 *  <pre>
 *          [a1, b1, c1] + [a2, b2, c2] = [a1 + a2, b1 + b2, c1 + c2]
 *
 *                           -[a, b, c] = [-c, -b, -a]
 *
 *      max([a1, b1, c1], [a2, b2, c2]) = [max(a1, a2), max(b1, b2), max(c1, c2)]
 *  </pre>
 *  <p>
 *  With both intervals and <code>Spring</code>s we can define "-" and <em>min</em>
 *  in terms of negation:
 *  <p>
 *  <pre>
 *      X - Y = X + (-Y)
 *
 *      min(X, Y) = -max(-X, -Y)
 *  </pre>
 *  <p>
 *  For the static methods in this class that embody the arithmetic
 *  operators, we do not actually perform the operation in question as
 *  that would snapshot the values of the properties of the method's arguments
 *  at the time the static method is called. Instead, the static methods
 *  create a new <code>Spring</code> instance containing references to
 *  the method's arguments so that the characteristics of the new spring track the
 *  potentially changing characteristics of the springs from which it
 *  was made. This is a little like the idea of a <em>lazy value</em>
 *  in a functional language.
 *
 * @see SpringLayout
 * @see SpringLayout.Constraints
 *
 * @version 1.6 01/23/03
 * @author 	Philip Milne
 * @since       1.4
 */
public abstract class Spring {

    /**
     * An integer value signifying that a property value has not yet been calculated.
     */
    public static final int UNSET = Integer.MIN_VALUE;

    /**
     * Used by factory methods to create a <code>Spring</code>.
     * 
     * @see #constant(int)
     * @see #constant(int, int, int)
     * @see #max
     * @see #minus
     * @see #sum
     * @see SpringLayout.Constraints
     */
    protected Spring() {}

    /**
     * Returns the <em>minimum</em> value of this <code>Spring</code>.
     *
     * @return the <code>minimumValue</code> property of this <code>Spring</code>
     */
    public abstract int getMinimumValue();

    /**
     * Returns the <em>preferred</em> value of this <code>Spring</code>.
     *
     * @return the <code>preferredValue</code> of this <code>Spring</code>
     */
    public abstract int getPreferredValue();

    /**
     * Returns the <em>maximum</em> value of this <code>Spring</code>.
     *
     * @return the <code>maximumValue</code> property of this <code>Spring</code>
     */
    public abstract int getMaximumValue();

    /**
     * Returns the current <em>value</em> of this <code>Spring</code>.
     *
     * @return  the <code>value</code> property of this <code>Spring</code>
     *
     * @see #setValue
     */
    public abstract int getValue();

    /**
     * Sets the current <em>value</em> of this <code>Spring</code> to <code>value</code>.
     *
     * @param   value the new setting of the <code>value</code> property
     *
     * @see #getValue
     */
    public abstract void setValue(int value);

    private double range(boolean contract) {
        return contract ? (getPreferredValue() - getMinimumValue()) :
                          (getMaximumValue() - getPreferredValue());
    }

    /*pp*/ double getStrain() {
        double delta = (getValue() - getPreferredValue());
        return delta/range(getValue() < getPreferredValue());
    }

    /*pp*/ void setStrain(double strain) {
        setValue(getPreferredValue() + (int)(strain * range(strain < 0)));
    }

    /*pp*/ boolean isCyclic(SpringLayout l) {
        return false;
    }

    /*pp*/ static abstract class AbstractSpring extends Spring {
        protected int size = UNSET;

        public int getValue() {
            return size != UNSET ? size : getPreferredValue();
        }

        public void setValue(int size) {
            if (size == UNSET) {
                clear();
                return;
            }
            this.size = size;
        }

        protected void clear() {
            size = UNSET;
        }
    }

    private static class StaticSpring extends AbstractSpring {
        protected int min;
        protected int pref;
        protected int max;

        public StaticSpring() {}

        public StaticSpring(int pref) {
            this(pref, pref, pref);
        }

        public StaticSpring(int min, int pref, int max) {
            this.min = min;
            this.pref = pref;
            this.max = max;
            this.size = pref;
        }

         public String toString() {
             return "StaticSpring [" + min + ", " + pref + ", " + max + "]";
         }

         public int getMinimumValue() {
            return min;
        }

        public int getPreferredValue() {
            return pref;
        }

        public int getMaximumValue() {
            return max;
        }
    }

    private static class NegativeSpring extends Spring {
        private Spring s;

        public NegativeSpring(Spring s) {
            this.s = s;
        }

// Note the use of max value rather than minimum value here.
// See the opening preamble on arithmetic with springs.

        public int getMinimumValue() {
            return -s.getMaximumValue();
        }

        public int getPreferredValue() {
            return -s.getPreferredValue();
        }

        public int getMaximumValue() {
            return -s.getMinimumValue();
        }

        public int getValue() {
            return -s.getValue();
        }

        public void setValue(int size) {
            // No need to check for UNSET as
            // Integer.MIN_VALUE == -Integer.MIN_VALUE.
            s.setValue(-size);
        }

        /*pp*/ boolean isCyclic(SpringLayout l) {
            return s.isCyclic(l);
        }
    }

// Use the instance variables of the StaticSpring superclass to
// cache values that have already been calculated.
    /*pp*/ static abstract class CompoundSpring extends StaticSpring {
        protected Spring s1;
        protected Spring s2;

        public CompoundSpring(Spring s1, Spring s2) {
            clear();
            this.s1 = s1;
            this.s2 = s2;
        }

        public String toString() {
            return "CompoundSpring of " + s1 + " and " + s2;
        }

        protected void clear() {
            min = pref = max = size = UNSET;
        }

        public void setValue(int size) {
            if (size == UNSET) {
                if (this.size != UNSET) {
                    super.setValue(size);
                    s1.setValue(UNSET);
                    s2.setValue(UNSET);
                    return;
                }
            }
            super.setValue(size);
        }

        protected abstract int op(int x, int y);

        public int getMinimumValue() {
            if (min == UNSET) {
                min = op(s1.getMinimumValue(), s2.getMinimumValue());
            }
            return min;
        }

        public int getPreferredValue() {
            if (pref == UNSET) {
                pref = op(s1.getPreferredValue(), s2.getPreferredValue());
            }
            return pref;
        }

        public int getMaximumValue() {
            if (max == UNSET) {
                max = op(s1.getMaximumValue(), s2.getMaximumValue());
            }
            return max;
        }

        public int getValue() {
            if (size == UNSET) {
                size = op(s1.getValue(), s2.getValue());
            }
            return size;
        }

        /*pp*/ boolean isCyclic(SpringLayout l) {
            return l.isCyclic(s1) || l.isCyclic(s2);
        }
    };

     private static class SumSpring extends CompoundSpring {
         public SumSpring(Spring s1, Spring s2) {
             super(s1, s2);
         }

         protected int op(int x, int y) {
             return x + y;
         }

         public void setValue(int size) {
             super.setValue(size);
             if (size == UNSET) {
                 return;
             }
             s1.setStrain(this.getStrain());
             s2.setValue(size - s1.getValue());
         }
     }

    private static class MaxSpring extends CompoundSpring {

        public MaxSpring(Spring s1, Spring s2) {
            super(s1, s2);
        }

        protected int op(int x, int y) {
            return Math.max(x, y);
        }

        public void setValue(int size) {
            super.setValue(size);
            if (size == UNSET) {
                return;
            }
            // Pending should also check max bounds here.
            if (s1.getPreferredValue() < s2.getPreferredValue()) {
                s1.setValue(Math.min(size, s1.getPreferredValue()));
                s2.setValue(size);
            }
            else {
                s1.setValue(size);
                s2.setValue(Math.min(size, s2.getPreferredValue()));
            }
        }
    }

    /**
     * Returns a strut -- a spring whose <em>minimum</em>, <em>preferred</em>, and
     * <em>maximum</em> values each have the value <code>pref</code>.
     *
     * @param  pref the <em>minimum</em>, <em>preferred</em>, and
     *         <em>maximum</em> values of the new spring
     * @return a spring whose <em>minimum</em>, <em>preferred</em>, and
     *         <em>maximum</em> values each have the value <code>pref</code>
     *
     * @see Spring
     */
     public static Spring constant(int pref) {
         return constant(pref, pref, pref);
     }

    /**
     * Returns a spring whose <em>minimum</em>, <em>preferred</em>, and
     * <em>maximum</em> values have the values: <code>min</code>, <code>pref</code>,
     * and <code>max</code> respectively.
     *
     * @param  min the <em>minimum</em> value of the new spring
     * @param  pref the <em>preferred</em> value of the new spring
     * @param  max the <em>maximum</em> value of the new spring
     * @return a spring whose <em>minimum</em>, <em>preferred</em>, and
     *         <em>maximum</em> values have the values: <code>min</code>, <code>pref</code>,
     *         and <code>max</code> respectively
     *
     * @see Spring
     */
     public static Spring constant(int min, int pref, int max) {
         return new StaticSpring(min, pref, max);
     }


    /**
     * Returns <code>-s</code>: a spring running in the opposite direction to <code>s</code>.
     *
     * @return <code>-s</code>: a spring running in the opposite direction to <code>s</code>
     *
     * @see Spring
     */
    public static Spring minus(Spring s) {
        return new NegativeSpring(s);
    }

    /**
     * Returns <code>s1+s2</code>: a spring representing <code>s1</code> and <code>s2</code>
     * in series. In a sum, <code>s3</code>, of two springs, <code>s1</code> and <code>s2</code>,
     * the <em>strains</em> of <code>s1</code>, <code>s2</code>, and <code>s3</code> are maintained
     * at the same level (to within the precision implied by their integer <em>value</em>s).
     * The strain of a spring in compression is:
     * <pre>
     *         value - pref
     *         ------------
     *          pref - min
     * </pre>
     * and the strain of a spring in tension is:
     * <pre>
     *         value - pref
     *         ------------
     *          max - pref
     * </pre>
     * When <code>setValue</code> is called on the sum spring, <code>s3</code>, the strain
     * in <code>s3</code> is calculated using one of the formulas above. Once the strain of
     * the sum is known, the <em>value</em>s of <code>s1</code> and <code>s2</code> are
     * then set so that they are have a strain equal to that of the sum. The formulas are
     * evaluated so as to take rounding errors into account and ensure that the sum of
     * the <em>value</em>s of <code>s1</code> and <code>s2</code> is exactly equal to
     * the <em>value</em> of <code>s3</code>.
     *
     * @return <code>s1+s2</code>: a spring representing <code>s1</code> and <code>s2</code> in series
     *
     * @see Spring
     */
     public static Spring sum(Spring s1, Spring s2) {
         return new SumSpring(s1, s2);
     }

    /**
     * Returns <code>max(s1, s2)</code>: a spring whose value is always greater than (or equal to)
     *         the values of both <code>s1</code> and <code>s2</code>.
     *
     * @return <code>max(s1, s2)</code>: a spring whose value is always greater than (or equal to)
     *         the values of both <code>s1</code> and <code>s2</code>
     * @see Spring
     */
    public static Spring max(Spring s1, Spring s2) {
        return new MaxSpring(s1, s2);
    }

    // Remove these, they're not used often and can be created using minus -
    // as per these implementations.

    /*pp*/ static Spring difference(Spring s1, Spring s2) {
        return sum(s1, minus(s2));
    }

    /*
    public static Spring min(Spring s1, Spring s2) {
        return minus(max(minus(s1), minus(s2)));
    }
    */

}
