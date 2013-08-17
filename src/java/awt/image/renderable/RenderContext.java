/*
 * @(#)RenderContext.java	1.7 98/10/19
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

/* ********************************************************************
 **********************************************************************
 **********************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997                      ***
 *** As  an unpublished  work pursuant to Title 17 of the United    ***
 *** States Code.  All rights reserved.                             ***
 **********************************************************************
 **********************************************************************
 **********************************************************************/

package java.awt.image.renderable;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.image.*;

/**
 * A RenderContext encapsulates the information needed to produce a
 * specific rendering from a RenderableImage.  It contains the area to
 * be rendered specified in rendering-independent terms, the
 * resolution at which the rendering is to be performed, and hints
 * used to control the rendering process.
 * 
 * <p> Users create RenderContexts and pass them to the
 * RenderableImage via the createRendering method.  Most of the methods of
 * RenderContexts are not meant to be used directly by applications,
 * but by the RenderableImage and operator classes to which it is
 * passed.
 *
 * <p> The AffineTransform parameter passed into and out of this class
 * are cloned.  The RenderingHints and Shape parameters are not
 * necessarily cloneable and are therefore only reference copied.
 * Altering RenderingHints or Shape instances that are in use by
 * instances of RenderContext may have undesired side effects.
 */
public class RenderContext implements Cloneable {
    
    /** Table of hints. May be null. */
    RenderingHints hints;
    
    /** Transform to convert user coordinates to device coordinates.  */
    AffineTransform usr2dev;
    
    /** The area of interest.  May be null. */
    Shape aoi;
    
    // Various constructors that allow different levels of
    // specificity. If the Shape is missing the whole renderable area
    // is assumed. If hints is missing no hints are assumed.  

    /**
     * Constructs a RenderContext with a given transform.
     * The area of interest is supplied as a Shape,
     * and the rendering hints are supplied as a RenderingHints object.
     *
     * @param usr2dev an AffineTransform.
     * @param aoi a Shape representing the area of interest.
     * @param hints a RenderingHints object containing rendering hints.
     */
    public RenderContext(AffineTransform usr2dev,
                         Shape aoi,
                         RenderingHints hints) {
        this.hints = hints;
        this.aoi = aoi;
        this.usr2dev = (AffineTransform)usr2dev.clone();
    }
    
    /**
     * Constructs a RenderContext with a given transform.
     * The area of interest is taken to be the entire renderable area.
     * No rendering hints are used.
     *
     * @param usr2dev an AffineTransform.
     */
    public RenderContext(AffineTransform usr2dev) {
        this(usr2dev, null, null);
    }
    
    /**
     * Constructs a RenderContext with a given transform and rendering hints.
     * The area of interest is taken to be the entire renderable area.
     *
     * @param usr2dev an AffineTransform.
     * @param hints a RenderingHints object containing rendering hints.
     */
    public RenderContext(AffineTransform usr2dev, RenderingHints hints) {
        this(usr2dev, null, hints);
    }
    
    /**
     * Constructs a RenderContext with a given transform and area of interest.
     * The area of interest is supplied as a Shape.
     * No rendering hints are used.
     *
     * @param usr2dev an AffineTransform.
     * @param aoi a Shape representing the area of interest.
     */
    public RenderContext(AffineTransform usr2dev, Shape aoi) {
        this(usr2dev, aoi, null);
    }

    /**
     * Gets the rendering hints of this <code>RenderContext</code>.
     * @return a <code>RenderingHints</code> object that represents
     * the rendering hints of this <code>RenderContext</code>.
     */
    public RenderingHints getRenderingHints() {
        return hints;
    }

    /**
     * Sets the rendering hints of this <code>RenderContext</code>.
     * @param hints a <code>RenderingHints</code> object that represents
     * the rendering hints to assign to this <code>RenderContext</code>.
     */
    public void setRenderingHints(RenderingHints hints) {
        this.hints = hints;
    }
    
    /**
     * Sets the current user-to-device AffineTransform contained
     * in the RenderContext to a given transform.
     *
     * @param newTransform the new AffineTransform.
     */
    public void setTransform(AffineTransform newTransform) {
        usr2dev = (AffineTransform)newTransform.clone();
    }
        
    /**
     * Modifies the current user-to-device transform by prepending another
     * transform.  In matrix notation the operation is:
     * <pre>
     * [this] = [modTransform] x [this]
     * </pre>
     *
     * @param modTransform the AffineTransform to prepend to the
     *        current usr2dev transform.
     */
    public void preConcetenateTransform(AffineTransform modTransform) {
        usr2dev.preConcatenate(modTransform);                
    }
    
    /**
     * Modifies the current user-to-device transform by appending another
     * transform.  In matrix notation the operation is:
     * <pre>
     * [this] = [this] x [modTransform]
     * </pre>
     *
     * @param modTransform the AffineTransform to append to the
     *        current usr2dev transform.
     */
    public void concetenateTransform(AffineTransform modTransform) {
        usr2dev.concatenate(modTransform);                
    }
    
    /**
     * Gets the current user-to-device AffineTransform.
     *
     * @return a reference to the current AffineTransform.
     */
    public AffineTransform getTransform() {
        return (AffineTransform)usr2dev.clone();
    }

    /**
     * Sets the current area of interest.  The old area is discarded.
     *
     * @param newAoi The new area of interest.
     */
    public void setAreaOfInterest(Shape newAoi) {
        aoi = newAoi;
    }

    /**
     * Gets the ares of interest currently contained in the
     * RenderContext.
     * 
     * @return a reference to the area of interest of the RenderContext, 
     *         or null if none is specified.
     */
    public Shape getAreaOfInterest() {
        return aoi;
    }

    /**
     * Makes a copy of a RenderContext. The area of interest is copied
     * by reference.  The usr2dev AffineTransform and hints are cloned,
     * while the area of interest is copied by reference.
     *
     * @return the new cloned RenderContext.
     */
    public Object clone() {
        RenderContext newRenderContext = new RenderContext(usr2dev,
                                                           aoi, hints);
        return newRenderContext;
    }
}
