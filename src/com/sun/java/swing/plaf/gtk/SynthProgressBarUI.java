/*
 * @(#)SynthProgressBarUI.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

//PENDING (kwalrath): Convert to logging, add assertions.
//PENDING (kwalrath): Make sure vertical is handled.
//PENDING (kwalrath): Should right-to-left indeterminate progress bar be
//                    handled differently?
//PENDING (kwalrath): Make sure method overriding is safe (esp. for ivars).
//PENDING (kwalrath): Support program-driven frame incrementation?

/**
 * A Synth L&F implementation of ProgressBarUI.
 *
 * @version 1.15, 01/23/03 (originally from version 1.60 of BasicProgressBarUI)
 * @author Michael C. Albers
 * @author Kathy Walrath
 * @author Joshua Outwater
 */
class SynthProgressBarUI extends ProgressBarUI implements SynthUI {
    
    private static final Dimension PREFERRED_INNER_HORIZONTAL =
        new Dimension(146, 16);
    private static final Dimension PREFERRED_INNER_VERTICAL =
        new Dimension(16, 146);

    private SynthStyle style;

    private int cachedPercent;
    private int cellLength, cellSpacing;
    // The "selectionForeground" is the color of the text when it is painted
    // over a filled area of the progress bar. The "selectionBackground"
    // is for the text over the unfilled progress bar area.
    private Color selectionForeground, selectionBackground;
    private boolean isIndeterminate = false;

    private Animator animator; 
    private PropertyChangeListener propertyListener;

    protected JProgressBar progressBar;
    protected ChangeListener changeListener;

    /** 
     * The current state of the indeterminate animation's cycle.
     * 0, the initial value, means paint the first frame.
     * When the progress bar is indeterminate and showing,
     * the default animation thread updates this variable
     * by invoking incrementAnimationIndex()
     * every repaintInterval milliseconds.
     */
    private int animationIndex = 0;

    /**
     * The number of frames per cycle. Under the default implementation,
     * this depends on the cycleTime and repaintInterval.  It
     * must be an even number for the default painting algorithm.  This
     * value is set in the initIndeterminateValues method.
     */
    private int numFrames;   //0 1|numFrames-1 ... numFrames/2

    /**
     * Interval (in ms) between repaints of the indeterminate progress bar.
     * The value of this method is set 
     * (every time the progress bar changes to indeterminate mode)
     * using the 
     * "ProgressBar.repaintInterval" key in the defaults table.
     */
    private int repaintInterval;

    /**
     * The number of milliseconds until the animation cycle repeats.
     * The value of this method is set 
     * (every time the progress bar changes to indeterminate mode)
     * using the 
     * "ProgressBar.cycleTime" key in the defaults table.
     */
    private int cycleTime;  //must be repaintInterval*2*aPositiveInteger

    //performance stuff
    private static boolean ADJUSTTIMER = true; //makes a BIG difference;
    //make this false for
    //performance tests

    //debugging; PENDING (kwalrath): convert to logging API 
    private static boolean DEBUGALL = false;  //severe performance impact
    private static boolean DEBUGTIMER = false;  //severe performance impact
    private static boolean BASICDEBUG = false;

    //performance data collection
    private static boolean LOGSTATS = false;
    private long startTime = 0;
    private long lastLoopTime = 0;
    private int numLoops = 0;

    /**                                                             
     * Used to hold the location and size of the bouncing box (returned
     * by getBox) to be painted.  
     */                                                             
    private Rectangle boxRect;
                                                                    
    /**                                                             
     * The rectangle to be updated the next time the         
     * animation thread calls repaint.  For bouncing-box            
     * animation this rect should include the union of    
     * the currently displayed box (which needs to be erased)       
     * and the box to be displayed next.
     * This rectangle's values are set in 
     * the setAnimationIndex method.
     */                                                             
    private Rectangle nextPaintRect;

    //cache
    /** The component's painting area, not including the border. */
    private Rectangle componentInnards;    //the current painting area
    private Rectangle oldComponentInnards; //used to see if the size changed

    /** For bouncing-box animation, the change in position per frame. */
    private double delta = 0.0;

    private int maxPosition = 0; //maximum X (horiz) or Y box location

    public static ComponentUI createUI(JComponent x) {
        return new SynthProgressBarUI();
    }

    public void installUI(JComponent c) {
        progressBar = (JProgressBar)c;
        installDefaults();
        installListeners();
    }

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        stopAnimationTimer();
        progressBar = null;
    }

    protected void installDefaults() {
        fetchStyle(progressBar);
    }

    private void fetchStyle(JProgressBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        // Add properties other than JComponent colors, Borders and
        // opacity settings here:
        if (style != oldStyle) {
            cellLength = UIManager.getInt("ProgressBar.cellLength");
            cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
            selectionForeground =
                UIManager.getColor("ProgressBar.selectionForeground");
            selectionBackground =
                UIManager.getColor("ProgressBar.selectionBackground");
        }
        context.dispose();
    }
    
    protected void uninstallDefaults() {
        SynthContext context = getContext(progressBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }
    
    protected void installListeners() {
        //Listen for changes in the progress bar's data.
        changeListener = new ChangeHandler();
        progressBar.addChangeListener(changeListener);

        //Listen for changes between determinate and indeterminate state.
        propertyListener = new PropertyChangeHandler();
        progressBar.addPropertyChangeListener(propertyListener);
    }    
    
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                            SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    /**
     * Starts the animation thread, creating and initializing
     * it if necessary.  This method is invoked when
     * the progress bar changes to indeterminate mode. 
     * If you implement your own animation thread,
     * you must override this method.
     *
     * @since 1.4
     */
    protected void startAnimationTimer() {
        if (animator == null) {
            animator = new Animator(); 
        }

        animator.start(getRepaintInterval());
    }

    /**
     * Stops the animation thread.  This method is invoked when
     * the progress bar changes from 
     * indeterminate to determinate mode
     * and when this UI is uninstalled.
     * If you implement your own animation thread,
     * you must override this method.
     *
     * @since 1.4
     */
    protected void stopAnimationTimer() {
        if (animator != null) {
            animator.stop();
        }
    }

    /**
     * Removes all listeners installed by this object.
     */
    protected void uninstallListeners() {
        progressBar.removeChangeListener(changeListener);
        progressBar.removePropertyChangeListener(propertyListener);
    }

    
    // Many of the Synth*UI components have the following methods.
    // This component does not have these methods because *ProgressBarUI
    //  is not a compound component and does not accept input.
    //
    // protected void installComponents()
    // protected void uninstallComponents()
    // protected void installKeyboardActions()
    // protected void uninstallKeyboardActions()

    protected Dimension getPreferredInnerHorizontal() {
        return PREFERRED_INNER_HORIZONTAL;
    }
    
    protected Dimension getPreferredInnerVertical() {
        return PREFERRED_INNER_VERTICAL;
    }
    
    /**
     * The "selectionForeground" is the color of the text when it is painted
     * over a filled area of the progress bar.
     */
    protected Color getSelectionForeground() {
        return selectionForeground;
    }
    
    /**
     * The "selectionBackground" is the color of the text when it is painted
     * over an unfilled area of the progress bar.
     */
    protected Color getSelectionBackground() {
        return selectionBackground;
    }
    
    private int getCachedPercent() {
        return cachedPercent;
    }
    
    private void setCachedPercent(int cachedPercent) {
        this.cachedPercent = cachedPercent;
    }
    
    /**
     * Returns the width (if HORIZONTAL) or height (if VERTICAL)
     * of each of the indivdual cells/units to be rendered in the
     * progress bar. However, for text rendering simplification and 
     * aesthetic considerations, this function will return 1 when
     * the progress string is being rendered.
     *
     * @return the value representing the spacing between cells
     * @see    #setCellLength
     * @see    JProgressBar#isStringPainted
     */
    protected int getCellLength() {
        if (progressBar.isStringPainted()) {
            return 1;
        } else {
            return cellLength;
        }
    }
    
    protected void setCellLength(int cellLen) {
        this.cellLength = cellLen;
    }
    
    /**
     * Returns the spacing between each of the cells/units in the
     * progress bar. However, for text rendering simplification and 
     * aesthetic considerations, this function will return 0 when
     * the progress string is being rendered.
     *
     * @return the value representing the spacing between cells
     * @see    #setCellSpacing
     * @see    JProgressBar#isStringPainted
     */
    protected int getCellSpacing() {
        if (progressBar.isStringPainted()) {
            return 0;
        } else {
            return cellSpacing;
        }
    }
    
    protected void setCellSpacing(int cellSpace) {
        this.cellSpacing = cellSpace;
    }
    
    /**
     * This determines the amount of the progress bar that should be filled
     * based on the percent done gathered from the model. This is a common
     * operation so it was abstracted out. It assumes that your progress bar
     * is linear. That is, if you are making a circular progress indicator,
     * you will want to override this method.
     */
    protected int getAmountFull(Insets b, int width, int height) {
        int amountFull = 0;
        BoundedRangeModel model = progressBar.getModel();
        
        if ( (model.getMaximum() - model.getMinimum()) != 0) {
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                amountFull = (int)Math.round(width *
                             progressBar.getPercentComplete());
            } else {
                amountFull = (int)Math.round(height *
                             progressBar.getPercentComplete());
            }
        }
        return amountFull;
    }
    
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        JProgressBar pBar = (JProgressBar)context.getComponent();
        Rectangle progressBounds = new Rectangle();
        if (!pBar.isIndeterminate()) {
            Insets pBarInsets = pBar.getInsets();
            double percentComplete = pBar.getPercentComplete();
            if (percentComplete != 0.0) {
                if (pBar.getOrientation() == JProgressBar.HORIZONTAL) {
                    progressBounds.x = pBarInsets.left;
                    progressBounds.y = pBarInsets.top;
                    // The +1 on each side is the insets of the progress.
                    progressBounds.width =
                        (int)(percentComplete * (pBar.getWidth()
                            - (pBarInsets.left + 1 + pBarInsets.right + 1)));
                    progressBounds.height = pBar.getHeight()
                        - (pBarInsets.top + 1 + pBarInsets.bottom + 1);

                    if (!SynthLookAndFeel.isLeftToRight(pBar)) {
                        progressBounds.x =
                            pBar.getWidth() - pBarInsets.left
                                - pBarInsets.right - progressBounds.width;
                    }
                } else {  // JProgressBar.VERTICAL
                    progressBounds.x = pBarInsets.left;
                    progressBounds.width = pBar.getWidth()
                        - (pBarInsets.left + 1 + pBarInsets.right + 1);
                    progressBounds.height =
                        (int)(percentComplete * (pBar.getHeight()
                            - (pBarInsets.top + 1 + pBarInsets.bottom + 1)));
                    progressBounds.y =
                        pBar.getHeight() - pBarInsets.top - pBarInsets.bottom
                            - progressBounds.height;

                    // When the progress bar is vertical we always paint
                    // from bottom to top, not matter what the component
                    // orientation is.
                }
            }
        } else {
            progressBounds = getBox(boxRect);
        }
        SynthLookAndFeel.paintForeground(context, g, progressBounds);

        if (pBar.isStringPainted() && !isIndeterminate) {
            paintText(context, g, pBar.getString());
        }
    }

    protected void paintText(SynthContext context, Graphics g,
            String title) {
        Font font = context.getStyle().getFont(context);
        FontMetrics metrics = g.getFontMetrics(font);

        if (progressBar.isStringPainted()) {
            String pBarString = progressBar.getString();
            Rectangle bounds = progressBar.getBounds();
            int strLength = metrics.stringWidth(pBarString);

            // Calculate the bounds for the text.
            Rectangle textRect = new Rectangle(
                (bounds.width / 2) - (strLength / 2),
                (bounds.height -
                    (metrics.getAscent() + metrics.getDescent())) / 2,
                0, 0);

            // Progress bar isn't tall enough for the font.  Don't paint it.
            if (textRect.y < 0) {
                return;
            }

            // Paint the text.
            SynthStyle style = context.getStyle();
            g.setColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
            g.setFont(style.getFont(context));
            style.getSynthGraphics(context).paintText(context, g, title,
                                                 textRect.x, textRect.y, -1);
        }
    }

    /**
     * Stores the position and size of
     * the bouncing box that would be painted for the current animation index
     * in <code>r</code> and returns <code>r</code>.
     * Subclasses that add to the painting performed
     * in this class's implementation of <code>paintIndeterminate</code> --
     * to draw an outline around the bouncing box, for example --
     * can use this method to get the location of the bouncing
     * box that was just painted.
     * By overriding this method,
     * you have complete control over the size and position 
     * of the bouncing box,
     * without having to reimplement <code>paintIndeterminate</code>.
     *
     * @param r  the Rectangle instance to be modified;
     *           may be <code>null</code>
     * @return   <code>null</code> if no box should be drawn;
     *           otherwise, returns the passed-in rectangle
     *           (if non-null)
     *           or a new rectangle
     *
     * @see #setAnimationIndex
     * @since 1.4
     */
    public Rectangle getBox(Rectangle r) {
        int currentFrame = getAnimationIndex();
        int middleFrame = numFrames/2;

        if (DEBUGALL) {
            System.out.println("----begin getBox----");
            System.out.println("    getBox argument: " + r);
            System.out.println("    currentFrame = " + currentFrame);
            System.out.println("    middleFrame = " + middleFrame);
        }

        if (sizeChanged() || delta == 0.0 || maxPosition == 0.0) {
            updateSizes();
        }

        r = getGenericBox(r); 

        if (r == null) {
            if (DEBUGALL) {
                System.out.println("    Exiting because r is null");
            }
            return null;
        }
        if (middleFrame <= 0) {
            if (DEBUGALL) {
                System.out.println("    Exiting because middleFrame <= 0.");
            }
            return null;
        }

        //assert currentFrame >= 0 && currentFrame < numFrames
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            if (currentFrame < middleFrame) {
                r.x = componentInnards.x
                      + (int)Math.round(delta * (double)currentFrame);
            } else {
                r.x = maxPosition
                      - (int)Math.round(delta * 
                                        (currentFrame - middleFrame));
            }
        } else { //VERTICAL indeterminate progress bar
            if (currentFrame < middleFrame) {
                r.y = componentInnards.y
                      + (int)Math.round(delta * currentFrame);
            } else {
                r.y = maxPosition
                      - (int)Math.round(delta *
                                        (currentFrame - middleFrame));
            }
        }

        if (DEBUGALL) {
            System.out.println("    getBox return value: " + r);
            System.out.println("----end getBox----");
        }
        return r;
    }

    /**
     * Updates delta, max position.
     * Assumes componentInnards is correct (e.g. call after sizeChanged()).
     */
    private void updateSizes() {
        if (DEBUGALL) {
            System.out.println("----begin updateSizes----");
        }

        int length = 0;

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            length = getBoxLength(componentInnards.width,
                                  componentInnards.height);
            maxPosition = componentInnards.x + componentInnards.width
                          - length;

        } else { //VERTICAL progress bar
            length = getBoxLength(componentInnards.height,
                                  componentInnards.width);
            maxPosition = componentInnards.y + componentInnards.height
                          - length;
        }

        //If we're doing bouncing-box animation, update delta.
        if (DEBUGALL) {
            System.out.println("    Updating delta.");
        }

        delta = 2.0 * (double)maxPosition/(double)numFrames;

        if (BASICDEBUG) {
            System.out.println("    delta: " + delta);
            System.out.println("    maxPosition: " + maxPosition);
        }
        if (DEBUGALL) {
            System.out.println("----end updateSizes----");
        }

        return; 
    }

    /**
     * Assumes that the component innards, max position, etc. are up-to-date.
     */
    private Rectangle getGenericBox(Rectangle r) {
        if (DEBUGALL) {
            System.out.println("----begin getGenericBox----");
            System.out.println("    argument: " + r);
        }

        if (r == null) {
            r = new Rectangle();
        }

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            r.width = getBoxLength(componentInnards.width,
                                   componentInnards.height);
            if (r.width < 0) {
                r = null;
            } else {
                r.height = componentInnards.height;
                // Insets on the progress sub region.
                r.height -= 2;
                r.width -= 2;
                r.y = componentInnards.y;
            }
          // end of HORIZONTAL

        } else { //VERTICAL progress bar
            r.height = getBoxLength(componentInnards.height,
                                    componentInnards.width);
            if (r.height < 0) {
                r = null;
            } else {
                r.width = componentInnards.width;
                // Insets on the progress sub region.
                r.height -= 2;
                r.width -= 2;
                r.x = componentInnards.x;
            }
        } // end of VERTICAL


        if (DEBUGALL) {
            System.out.println("    getGenericBox returns: " + r);
            System.out.println("----end getGenericBox----");
        }
        return r;
    }

    /**
     * Returns the length
     * of the "bouncing box" to be painted.
     * This method is invoked by the 
     * default implementation of <code>paintIndeterminate</code>
     * to get the width (if the progress bar is horizontal)
     * or height (if vertical) of the box.
     * For example:
     * <blockquote>
     * <pre>
     *boxRect.width = getBoxLength(componentInnards.width,
     *                             componentInnards.height);
     * </pre>
     * </blockquote>
     *
     * <p>
     * By default, this method returns the available length
     * divided by 6.  Another possibility might
     * be to make the bouncing box a square, 
     * which you could implement by overriding this method as follows:
     * <blockquote>
     * <pre>
     *protected double getBoxLength(int availableLength,
     *                              int otherDimension) {
     *    return Math.min(availableLength, otherDimension);
     *}
     * </blockquote>
     * </pre>
     *
     * @param availableLength  the amount of space available
     *                         for the bouncing box to move in;
     *                         for a horizontal progress bar,
     *                         for example,
     *                         this should be
     *                         the inside width of the progress bar
     *                         (the component width minus borders)
     * @param otherDimension   for a horizontal progress bar, this should be
     *                         the inside height of the progress bar; this
     *                         value might be used to constrain or determine
     *                         the return value 
     *
     * @return the size of the box dimension being determined; 
     *         must be no larger than <code>availableLength</code>
     *
     * @see javax.swing.SwingUtilities#calculateInnerArea
     * @since 1.4
     */
    private int getBoxLength(int availableLength, int otherDimension) {
        return (int)Math.round(availableLength/6.0);
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension size;
        Insets border = progressBar.getInsets();
        FontMetrics fontSizer = progressBar.getFontMetrics(
            progressBar.getFont());
    
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            size = new Dimension(getPreferredInnerHorizontal());
            // Ensure that the progress string will fit
            if (progressBar.isStringPainted()) {
                // I'm doing this for completeness.
                String progString = progressBar.getString();
                int stringWidth = fontSizer.stringWidth(progString);
                if (stringWidth > size.width) {
                    size.width = stringWidth;
                }
                // This uses both Height and Descent to be sure that 
                // there is more than enough room in the progress bar
                // for everything.
                // This does have a strange dependency on 
                // getStringPlacememnt() in a funny way.
                int stringHeight = fontSizer.getHeight() +
                    fontSizer.getDescent();
                if (stringHeight > size.height) {
                    size.height = stringHeight;
                }
            }
        } else {
            size = new Dimension(getPreferredInnerVertical());
            // Ensure that the progress string will fit.
            if (progressBar.isStringPainted()) {
                String progString = progressBar.getString();
                int stringHeight = fontSizer.getHeight() +
                    fontSizer.getDescent();
                if (stringHeight > size.width) {
                    size.width = stringHeight;
                }
                // This is also for completeness.
                int stringWidth = fontSizer.stringWidth(progString);
                if (stringWidth > size.height) {
                    size.height = stringWidth;
                }
            }
        }

        size.width += border.left + border.right;
        size.height += border.top + border.bottom;
        return size;
    }

    /**
     * The Minimum size for this component is 10. The rationale here 
     * is that there should be at least one pixel per 10 percent.
     */
    public Dimension getMinimumSize(JComponent c) {
        Dimension pref = getPreferredSize(progressBar);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            pref.width = 10;
        } else {
            pref.height = 10;
        }
        return pref;
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension pref = getPreferredSize(progressBar);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            pref.width = Short.MAX_VALUE;
        } else {
            pref.height = Short.MAX_VALUE;
        }
        return pref;
    }

    /**
     * Gets the index of the current animation frame.
     *
     * @since 1.4
     */
    protected int getAnimationIndex() {
        return animationIndex;
    }

    /**
     * Sets the index of the current animation frame
     * to the specified value and requests that the
     * progress bar be repainted.
     * Subclasses that don't use the default painting code
     * might need to override this method
     * to change the way that the <code>repaint</code> method
     * is invoked.
     *
     * @param newValue the new animation index; no checking
     *                 is performed on its value
     * @see #incrementAnimationIndex
     *
     * @since 1.4
     */
    protected void setAnimationIndex(int newValue) {
        if (DEBUGALL) {
            System.out.println("----begin setAnimationIndex----");
            System.out.println("    argument = " + newValue);
        }

        if (animationIndex != newValue) {
            if (DEBUGALL) {
                System.out.println("    Changing animation index from "
                                   + animationIndex + " to "
                                   + newValue);
            }
            
            if (sizeChanged()) { 
                if (DEBUGALL) {
                    System.out.println("    size changed; resetting maxPosition, delta");
                }
                animationIndex = newValue;
                maxPosition = 0;  //needs to be recalculated
                delta = 0.0;      //needs to be recalculated
                progressBar.repaint();
                return;
            }

            //Get the previous box drawn.
            nextPaintRect = getBox(nextPaintRect);

            if (DEBUGALL) {
                System.out.println("    previous paint rect =  "
                                        + nextPaintRect);
                System.out.println("    before setting, boxRect =  "
                                        + boxRect);
            }

            //Update the frame number.
            animationIndex = newValue;
                
            //Get the next box to draw.
            if (nextPaintRect != null) {
                boxRect = getBox(boxRect);
                if (boxRect != null) {
                    nextPaintRect.add(boxRect);
                }
            }

            if (DEBUGALL) {
                System.out.println("    after setting, boxRect =  "
                                        + boxRect);
                System.out.println("    after setting, nextPaintRect =  "
                                        + nextPaintRect);
            }
        } else { //animationIndex == newValue
            if (DEBUGALL) {
                System.out.println("    No change in value");
                System.out.println("----end setAnimationIndex----");
            }
            return;
        }

        if (nextPaintRect != null) {
            progressBar.repaint(nextPaintRect);
        } else {
            progressBar.repaint();
            if (DEBUGALL) {
                System.out.println("    repaint without args");
            }
        }

        if (DEBUGALL) {
            System.out.println("----end setAnimationIndex----");
        }
    }

    private boolean sizeChanged() {
        if ((oldComponentInnards == null) || (componentInnards == null)) {
        return true;
    }

        oldComponentInnards.setRect(componentInnards);
        componentInnards = SwingUtilities.calculateInnerArea(progressBar,
                                                         componentInnards);
        return !oldComponentInnards.equals(componentInnards);
    }

    /**
     * Sets the index of the current animation frame,
     * to the next valid value,
     * which results in the progress bar being repainted.
     * The next valid value is, by default,
     * the current animation index plus one.
     * If the new value would be too large, 
     * this method sets the index to 0.
     * Subclasses might need to override this method
     * to ensure that the index does not go over
     * the number of frames needed for the particular 
     * progress bar instance.
     * This method is invoked by the default animation thread
     * every <em>X</em> milliseconds, 
     * where <em>X</em> is specified by the "ProgressBar.repaintInterval"
     * UI default.
     *
     * @see #setAnimationIndex
     * @since 1.4
     */
    protected void incrementAnimationIndex() {
        int newValue = getAnimationIndex() + 1;
        if (DEBUGALL) {
            System.out.println();
            System.out.println("----begin incrementAnimationIndex----");
            System.out.println("    newValue = " + newValue);
            System.out.println("    numFrames = " + numFrames);
        }

        if (newValue < numFrames) {
            setAnimationIndex(newValue);
        } else {
            setAnimationIndex(0);
            if (LOGSTATS) {
                numLoops++;
                long time = System.currentTimeMillis();
                System.out.println("Loop #" + numLoops + ": "
                                   + (time - lastLoopTime)
                                   + " (" + (time - startTime)
                                   + " total)");
                lastLoopTime = time;
            }
        }
        if (DEBUGALL) {
            System.out.println("----end incrementAnimationIndex----");
        }
    }

    /**
     * Returns the desired number of milliseconds between repaints.
     * This value is meaningful
     * only if the progress bar is in indeterminate mode.
     * The repaint interval determines how often the 
     * default animation thread's timer is fired.
     * It's also used by the default indeterminate progress bar
     * painting code when determining
     * how far to move the bouncing box per frame.
     * The repaint interval is specified by
     * the "ProgressBar.repaintInterval" UI default.
     * 
     * @return  the repaint interval, in milliseconds
     */
    private int getRepaintInterval() {
        return repaintInterval;
    }

    private int initRepaintInterval() {
        repaintInterval = UIManager.getInt("ProgressBar.repaintInterval");
        if (BASICDEBUG) {
            System.out.println("    value of ProgressBar.repaintInterval is "
                                   + repaintInterval);
        }
        return repaintInterval;
    }

    /**
     * Returns the number of milliseconds per animation cycle.
     * This value is meaningful
     * only if the progress bar is in indeterminate mode.
     * The cycle time is used by the default indeterminate progress bar
     * painting code when determining
     * how far to move the bouncing box per frame.
     * The cycle time is specified by
     * the "ProgressBar.cycleTime" UI default
     * and adjusted, if necessary,
     * by the initIndeterminateDefaults method.
     * 
     * @return  the cycle time, in milliseconds
     */
    private int getCycleTime() {
        return cycleTime;
    }

    private int initCycleTime() {
        cycleTime = UIManager.getInt("ProgressBar.cycleTime");
        if (BASICDEBUG) {
            System.out.println("    value of ProgressBar.cycleTime is "
                                   + cycleTime);
        }
        return cycleTime;
    }


    /** Initialize cycleTime, repaintInterval, numFrames, animationIndex. */
    private void initIndeterminateDefaults() {
        if (DEBUGALL) {
            System.out.println("----begin initIndeterminateDefaults----");
        }
        initRepaintInterval(); //initialize repaint interval
        initCycleTime();       //initialize cycle length

        // Make sure repaintInterval is reasonable.
        if (repaintInterval <= 0) {
            repaintInterval = 100;
        }

        // Make sure cycleTime is reasonable.
        if (repaintInterval > cycleTime) {
            cycleTime = repaintInterval * 20;
            if (DEBUGALL) {
                System.out.println("cycleTime changed to " + cycleTime);
            }
        } else {
            // Force cycleTime to be a even multiple of repaintInterval.
            int factor = (int)Math.ceil(
                                 ((double)cycleTime)
                               / ((double)repaintInterval*2));
            if (DEBUGALL) {
                int newCycleTime = repaintInterval*factor*2; 
                if (cycleTime != newCycleTime) {
                    System.out.println("cycleTime being changed to "
                                       + newCycleTime);
                }
            }
    
            cycleTime = repaintInterval*factor*2;
        }

        if (BASICDEBUG) {
            System.out.println("    cycle length: " + cycleTime);
            System.out.println("    repaint interval: " + repaintInterval);
        }
        if (DEBUGALL) {
            System.out.println("----end initIndeterminateDefaults----");
        }
    }

    /**
     * Invoked by PropertyChangeHandler before startAnimationTimer().
     *
     *  NOTE: This might not be invoked until after the first
     *  paintIndeterminate call.
     */
    private void initIndeterminateValues() {
        if (DEBUGALL) {
            System.out.println();
            System.out.println("----begin initIndeterminateValues----");
        }
        if (LOGSTATS) {
            startTime = lastLoopTime = System.currentTimeMillis();
            numLoops = 0;
        }
                                                                            
        if (BASICDEBUG) {
            System.out.println("ADJUSTTIMER = " + ADJUSTTIMER);
        }
    
        initIndeterminateDefaults();
        //assert cycleTime/repaintInterval is a whole multiple of 2.
        numFrames = cycleTime/repaintInterval;
        initAnimationIndex();
            
        boxRect = new Rectangle();
        nextPaintRect = new Rectangle();
        componentInnards = new Rectangle();
        oldComponentInnards = new Rectangle();

        if (BASICDEBUG) {
            System.out.println("    numFrames: " + numFrames);
        }
        if (DEBUGALL) {
            System.out.println("----end initIndeterminateValues----");
        }
    }

    /** Invoked by PropertyChangeHandler after stopAnimationTimer(). */
    private void cleanUpIndeterminateValues() {
        if (DEBUGALL) {
            System.out.println();
            System.out.println("----begin cleanUpIndeterminateValues----");
        }
                                                                            
        cycleTime = repaintInterval = 0;
        numFrames = animationIndex = 0;
        maxPosition = 0;
        delta = 0.0;

        boxRect = nextPaintRect = null;
        componentInnards = oldComponentInnards = null;

        if (LOGSTATS) {
            startTime = lastLoopTime = numLoops = 0;
        }

        if (DEBUGALL) {
            System.out.println("----end cleanUpIndeterminateValues----");
        }
    }

    // Called from initIndeterminateValues to initialize the animation index.
    // This assumes that numFrames is set to a correct value.
    private void initAnimationIndex() {
        if ((progressBar.getOrientation() == JProgressBar.HORIZONTAL) &&
            (SynthLookAndFeel.isLeftToRight(progressBar))) {
            // If this is a left-to-right progress bar,
        // start at the first frame.
            setAnimationIndex(0);
        } else {
            // If we go right-to-left or vertically, start at the right/bottom.
            setAnimationIndex(numFrames/2);
        }
    }

    //
    // Animation Thread
    //
    /**
     * Implements an animation thread that invokes repaint
     * at a fixed rate.  If ADJUSTTIMER is true, this thread
     * will continuously adjust the repaint interval to 
     * try to make the actual time between repaints match
     * the requested rate.  
     */
    private class Animator implements ActionListener {
        private Timer timer;
        private long previousDelay; //used to tune the repaint interval
        private int interval; //the fixed repaint interval
        private long lastCall; //the last time actionPerformed was called
        private int MINIMUM_DELAY = 5;

    /**
     * Creates a timer if one doesn't already exist, 
     * then starts the timer thread.
     */
        private void start(int interval) {
            previousDelay = interval;
            lastCall = 0;

        if (timer == null) {
                timer = new Timer(interval, this);
        } else {
                timer.setDelay(interval);
        }

        if (ADJUSTTIMER) {
        timer.setRepeats(false);
                timer.setCoalesce(false);
        }

        timer.start();
    }

    /**
     * Stops the timer thread.
     */
    private void stop() {
        timer.stop();
    }

    /**
     * Reacts to the timer's action events.
     */
    public void actionPerformed(ActionEvent e) {
            if (ADJUSTTIMER) {
                long time = System.currentTimeMillis();

                if (lastCall > 0) { //adjust nextDelay
                //XXX maybe should cache this after a while
                    //actual = time - lastCall
                    //difference = actual - interval
                    //nextDelay = previousDelay - difference
                    //          = previousDelay - (time - lastCall - interval)
                   int nextDelay = (int)(previousDelay
                                          - time + lastCall
                                          + getRepaintInterval());
                    if (nextDelay < MINIMUM_DELAY) {
                        nextDelay = MINIMUM_DELAY;
                    }
                    timer.setInitialDelay(nextDelay);
                    previousDelay = nextDelay;
                    if (DEBUGTIMER) {
                        System.out.println("---------------------");
                        System.out.println("actual delay = "
                                           + (time - lastCall));
                        System.out.println("next delay = " + nextDelay);
                    }
                }
                timer.start();
                lastCall = time;
            }

        incrementAnimationIndex(); //paint next frame
    }
    }


    //
    // Property Change Events
    //
    /**
     * [PENDING: add doc here]
     * [PENDING: make this static?]
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            fetchStyle((JProgressBar)e.getSource());
        }
        if ("indeterminate".equals(prop)) {
        isIndeterminate = progressBar.isIndeterminate();
    
        if (isIndeterminate) {
                    initIndeterminateValues();

            //start the animation thread
            startAnimationTimer();
            } else {
                //stop the animation thread
                stopAnimationTimer();

                    //clean up
                    cleanUpIndeterminateValues();
                }
    
                progressBar.repaint();
            }
        }
    }


    //
    // Change Events
    //
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthProgressBarUI.
     */
    class ChangeHandler implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
        BoundedRangeModel model = progressBar.getModel();
        int newRange = model.getMaximum() - model.getMinimum();
        int newPercent;
        int oldPercent = getCachedPercent();
        
        if (newRange > 0) {
        newPercent = (int)((100 * (long)model.getValue()) / newRange);
        } else {
        newPercent = 0;
        }
        
        if (newPercent != oldPercent) {
        setCachedPercent(newPercent);
        progressBar.repaint();
        }
    }
    }
}
