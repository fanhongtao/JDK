/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Vector;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.SizeRequirements;

/**
 * ZoneView is a View implementation that creates zones for which 
 * the child views are not created or stored until they are needed
 * for display or model/view translations.  This enables a substantial 
 * reduction in memory consumption for situations where the model
 * being represented is very large, by building view objects only for
 * the region being actively viewed/edited.  The size of the children
 * can be estimated in some way, or calculated asynchronously with 
 * only the result being saved.
 * <p>
 * ZoneView extends BoxView to provide a box that implements
 * zones for it's children.  The zones are special View implementations
 * (the children of an instance of this class) that represent only a 
 * portion of the model that an instance of ZoneView is responsible
 * for.  The zones don't create child views until an attempt is made 
 * to display them. A box shaped view is well suited to this because:
 *   <ul>
 *   <li>
 *   Boxes are a heavily used view, and having a box that
 *   provides this behavior gives substantial opportunity
 *   to plug the behavior into a view hierarchy from the
 *   view factory.
 *   <li>
 *   Boxes are tiled in one direction, so it is easy to
 *   divide them into zones in a reliable way.
 *   <li>
 *   Boxes typically have a simple relationship to the model (i.e. they
 *   create child views that directly represent the child elements).
 *   <li>
 *   Boxes are easier to estimate the size of than some other shapes.
 *   </ul>
 * <p>
 * The default behavior is controled by two properties, maxZoneSize
 * and maxZonesLoaded.  Setting maxZoneSize to Integer.MAX_VALUE would
 * have the effect of causing only one zone to be created.  This would
 * effectively turn the view into an implementation of the decorator
 * pattern.  Setting maxZonesLoaded to a value of Integer.MAX_VALUE would 
 * cause zones to never be unloaded.  For simplicity, zones are created on 
 * boundaries represented by the child elements of the element the view is 
 * responsible for.  The zones can be any View implementation, but the
 * default implementation is based upon AsyncBoxView which supports fairly
 * large zones efficiently.
 *
 * @author  Timothy Prinzing
 * @version 1.7 02/06/02
 * @see     View
 * @since   1.3
 */
public class ZoneView extends BoxView {

    int maxZoneSize = 8 * 1024;
    int maxZonesLoaded = 3;

    /**
     * Constructs a ZoneView.
     *
     * @param elem the element this view is responsible for
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public ZoneView(Element elem, int axis) {
	super(elem, axis);
    }

    /**
     * Get the current maximum zone size.
     */
    public int getMaximumZoneSize() {
	return maxZoneSize;
    }

    /**
     * Set the desired maximum zone size.  A
     * zone may get larger than this size if 
     * a single child view is larger than this
     * size since zones are formed on child view
     * boundaries.
     *
     * @param size the number of characters the zone
     * may represent before attempting to break
     * the zone into a smaller size.
     */
    public void setMaximumZoneSize(int size) {
	maxZoneSize = size;
    }

    /**
     * Get the current setting of the number of zones 
     * allowed to be loaded at the same time.
     */
    public int getMaxZonesLoaded() {
	return maxZonesLoaded;
    }

    /**
     * Set the current setting of the number of zones 
     * allowed to be loaded at the same time.
     *
     * @param mzl the desired maximum number of zones
     *  to be actively loaded.
     */
    public void setMaxZonesLoaded(int mzl) {
	maxZonesLoaded = mzl;
    }

    /**
     * Called by a zone when it gets loaded.  This happens when
     * an attempt is made to display or perform a model/view
     * translation on a zone that was in an unloaded state.
     * This is imlemented to check if the maximum number of
     * zones was reached and to unload the oldest zone if so.
     *
     * @param zone the child view that was just loaded.
     */
    protected void zoneWasLoaded(View zone) {
	// TBD
    }

    /**
     * Unload a zone (Convert the zone to its memory saving state).
     * The zones are expected to represent a subset of the
     * child elements of the element this view is responsible for.
     * Therefore, the default implementation is to simple remove
     * all the children.
     *
     * @param zone the child view desired to be set to an
     *  unloaded state.
     */
    protected void unloadZone(View zone) {
	zone.removeAll();
    }

    /**
     * Determine if a zone is in the loaded state.
     * The zones are expected to represent a subset of the
     * child elements of the element this view is responsible for.
     * Therefore, the default implementation is to return
     * true if the view has children.
     */
    protected boolean isZoneLoaded(View zone) {
	return (zone.getViewCount() > 0);
    }

    /**
     * Create a view to represent a zone for the given
     * range within the model (which should be within
     * the range of this objects responsibility).  This
     * is called by the zone management logic to create
     * new zones.  Subclasses can provide a different
     * implementation for a zone by changing this method.
     * 
     * @param p0 the start of the desired zone.  This should
     *  be >= getStartOffset() and < getEndOffset().  This
     *  value should also be < p1.
     * @param p1 the end of the desired zone.  This should
     *  be > getStartOffset() and <= getEndOffset().  This
     *  value should also be > p0.
     */
    protected View createZone(int p0, int p1) {
	Document doc = getDocument();
	View zone = null;
	try {
	    zone = new Zone(getElement(), 
			    doc.createPosition(p0),
			    doc.createPosition(p1));
	} catch (BadLocationException ble) {
	    // this should puke in some way.
	    throw new StateInvariantError(ble.getMessage());
	}
	return zone;
    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <code>setParent</code> method.
     * This is reimplemented to not load any children directly
     * (as they are created by the zones).  This method creates
     * the initial set of zones.  Zones don't actually get 
     * populated however until an attempt is made to display
     * them or to do model/view coordinate translation.
     *
     * @param f the view factory
     */
    protected void loadChildren(ViewFactory f) {
	// build the first zone.
	Document doc = getDocument();
	int offs0 = getStartOffset();
	int offs1 = getEndOffset();
	append(createZone(offs0, offs1));
	handleInsert(offs0, offs1 - offs0);
    }

    /**
     * Fetches the child view index representing the given position in
     * the model.
     *
     * @param pos the position >= 0
     * @returns  index of the view representing the given position, or 
     *   -1 if no view represents that position
     */
    protected int getViewIndexAtPosition(int pos) {
	// PENDING(prinz) this could be done as a binary
	// search, and probably should be.
	int n = getViewCount();
	if (pos == getEndOffset()) {
	    return n - 1;
	}
	for(int i = 0; i < n; i++) {
	    View v = getView(i);
	    if(pos >= v.getStartOffset() &&
	       pos < v.getEndOffset()) {
		return i;
	    }
	}
	return -1;
    }

    void handleInsert(int pos, int length) {
	int index = getViewIndexAtPosition(pos);
	View v = getView(index);
	int offs0 = v.getStartOffset();
	int offs1 = v.getEndOffset();
	if ((offs1 - offs0) > maxZoneSize) {
	    splitZone(index, offs0, offs1);
	}
    }

    void handleRemove(int pos, int length) {
	// IMPLEMENT
    }

    /**
     * Break up the zone at the given index into pieces
     * of an acceptable size.
     */
    void splitZone(int index, int offs0, int offs1) {
	// divide the old zone into a new set of bins
	Element elem = getElement();
	Document doc = elem.getDocument();
	Vector zones = new Vector();
	int offs = offs0;
	do {
	    offs0 = offs;
	    offs = Math.min(getDesiredZoneEnd(offs0), offs1);
	    zones.addElement(createZone(offs0, offs));
	} while (offs < offs1);
	View oldZone = getView(index);
	View[] newZones = new View[zones.size()];
	zones.copyInto(newZones);
	replace(index, 1, newZones);
    }

    /**
     * Returns the zone position to use for the 
     * end of a zone that starts at the given 
     * position.  By default this returns something
     * close to half the max zone size.
     */
    int getDesiredZoneEnd(int pos) {
	Element elem = getElement();
	int index = elem.getElementIndex(pos + (maxZoneSize / 2));
	Element child = elem.getElement(index);
	int offs0 = child.getStartOffset();
	int offs1 = child.getEndOffset();
	if ((offs1 - pos) > maxZoneSize) {
	    if (offs0 > pos) {
		return offs0;
	    }
	}
	return offs1;
    }

    // ---- View methods ----------------------------------------------------

    /**
     * The superclass behavior will try to update the child views
     * which is not desired in this case, since the children are
     * zones and not directly effected by the changes to the
     * associated element.  This is reimplemented to do nothing
     * and return false.
     */
    protected boolean updateChildren(DocumentEvent.ElementChange ec, 
				     DocumentEvent e, ViewFactory f) {
	return false;
    }

    /**
     * Gives notification that something was inserted into the document
     * in a location that this view is responsible for.  This is largely
     * delegated to the superclass, but is reimplemented to update the 
     * relevant zone (i.e. determine if a zone needs to be split into a 
     * set of 2 or more zones).
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */
    public void insertUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
	handleInsert(changes.getOffset(), changes.getLength());
	super.insertUpdate(changes, a, f);
    }

    /**
     * Gives notification that something was removed from the document
     * in a location that this view is responsible for.  This is largely
     * delegated to the superclass, but is reimplemented to update the
     * relevant zones (i.e. determine if zones need to be removed or 
     * joined with another zone).
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
	handleRemove(changes.getOffset(), changes.getLength());
	super.removeUpdate(changes, a, f);
    }

    /**
     * Internally created view that has the purpose of holding
     * the views that represent the children of the ZoneView
     * that have been arranged in a zone.
     *
     * PENDING(prinz) should be based upon AsyncBoxView to 
     * match documentation.  Also, the size estimation is
     * completely bogus at the moment.  Fixme before release!!
     */
    class Zone extends BoxView {

	Position start;
	Position end;

	/**
	 * Last allocated span along the minor axis.
	 * This is used to estimate the volume of
	 * the zone while waiting to populate it.
	 */
	int minorSpan = 500;

	/**
	 * Value used to guess what volume the zone
	 * would occupy if it was in fact loaded.
	 * The default number is a wild guess of a
	 * font taking 8x12 pixels and occupying less
	 * than half the space in the display.
	 */
	float volumeCoefficient = (8 * 12) * 2.5f;

        Zone(Element elem, Position start, Position end) {
            super(elem, ZoneView.this.getAxis());
	    this.start = start;
	    this.end = end;
        }

	/**
	 * Creates the child views and populates the
	 * zone with them.  This is done by translating
	 * the positions to child element index locations
	 * and building views to those elements.  If the
	 * zone is already loaded, this does nothing.
	 */
	public void load() {
	    if (! isLoaded()) {
		System.out.println("loading: " + getStartOffset() + "," +
				   getEndOffset());
		Element e = getElement();
		ViewFactory f = getViewFactory();
		int index0 = e.getElementIndex(getStartOffset());
		int index1 = e.getElementIndex(getEndOffset());
		View[] added = new View[index1 - index0 + 1];
		for (int i = index0; i <= index1; i++) {
		    added[i - index0] = f.create(e.getElement(i));
		}
		replace(0, 0, added);
	    }
	}

	/**
	 * Removes the child views and returns to a 
	 * state of unloaded.
	 */
	public void unload() {
	    removeAll();
	}

	/**
	 * Determines if the zone is in the loaded state
	 * or not.
	 */
	public boolean isLoaded() {
	    return (getViewCount() != 0);
	}

        /**
         * This is reimplemented to do nothing since the
         * children are created when the zone is loaded
	 * rather then when it is placed in the view 
	 * hierarchy.
         */
        protected void loadChildren(ViewFactory f) {
        }

	/**
	 * Fetches the child view index representing the given position in
	 * the model.  Since the zone contains a cluster of the overall
	 * set of child elements, we can determine the index fairly
	 * quickly from the model by subtracting the index of the
	 * start offset from the index of the position given.
	 *
	 * @param pos the position >= 0
	 * @returns  index of the view representing the given position, or 
	 *   -1 if no view represents that position
	 */
	protected int getViewIndexAtPosition(int pos) {
	    Element elem = getElement();
	    int index1 = elem.getElementIndex(pos);
	    int index0 = elem.getElementIndex(getStartOffset());
	    return index1 - index0;
	}

	/**
	 * Performs layout of the children.  The size is the
	 * area inside of the insets.  
	 *
	 * @param width the width >= 0
	 * @param height the height >= 0
	 */
        protected void layout(int width, int height) {
	    if (isLoaded()) {
		super.layout(width, height);
	    } else {
		int axis = getAxis();
		int newSpan = (axis == Y_AXIS) ? width : height;
		if (newSpan != minorSpan) {
		    minorSpan = newSpan;
		    // notify preference change along the major axis
		    preferenceChanged(null, (axis == X_AXIS), (axis == Y_AXIS));
		}
	    }
	}

	protected boolean updateChildren(DocumentEvent.ElementChange ec, 
					 DocumentEvent e, ViewFactory f) {
	    // the structure of this element changed.
	    Element[] removedElems = ec.getChildrenRemoved();
	    Element[] addedElems = ec.getChildrenAdded();
	    Element elem = getElement();
	    int index0 = elem.getElementIndex(getStartOffset());
	    int index1 = elem.getElementIndex(getEndOffset()-1);
	    int index = ec.getIndex();
	    if ((index >= index0) && (index <= index1)) {
		// The change is in this zone
		int replaceIndex = index - index0;
		int nadd = Math.min(index1 - index0 + 1, addedElems.length);
		int nremove = Math.min(index1 - index0 + 1, removedElems.length);
		View[] added = new View[nadd];
		for (int i = 0; i < nadd; i++) {
		    added[i] = f.create(addedElems[i]);
		}
		replace(replaceIndex, nremove, added);
	    }
	    return true;
	}

	// --- View methods ----------------------------------

	/**
	 * Fetches the attributes to use when rendering.  This view
	 * isn't directly responsible for an element so it returns
	 * the outer classes attributes.
	 */
        public AttributeSet getAttributes() {
	    return ZoneView.this.getAttributes();
	}

	/**
	 * Determines the preferred span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.  
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
        public float getPreferredSpan(int axis) {
	    if (isLoaded()) {
		return super.getPreferredSpan(axis);
	    }
	    if (getAxis() == axis) {
		// major axis
		int charVolume = getEndOffset() - getStartOffset();
		float displayVolume = charVolume * volumeCoefficient;
		float majorSpan = displayVolume / minorSpan;
		return majorSpan;
	    }
	    // minor axis
	    return minorSpan;
	}

	/**
	 * Determines the minimum span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the minimum span the view can be rendered into.
	 * @see View#getPreferredSpan
	 */
        public float getMinimumSpan(int axis) {
	    if (isLoaded()) {
		return super.getMinimumSpan(axis);
	    }
	    if (axis == getAxis()) {
		// major axis estimate is rigid
		return getPreferredSpan(axis);
	    }
	    // minor axis estimate is flexible
	    if (axis == X_AXIS) {
		return getLeftInset() + getRightInset() + 1;
	    }
	    return getTopInset() + getBottomInset() + 1;
	}

	/**
	 * Determines the maximum span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the maximum span the view can be rendered into.
	 * @see View#getPreferredSpan
	 */
        public float getMaximumSpan(int axis) {
	    if (isLoaded()) {
		return super.getMaximumSpan(axis);
	    }
	    if (axis == getAxis()) {
		// major axis estimate is rigid
		return getPreferredSpan(axis);
	    }
	    // minor axis estimate is flexible
	    return (Integer.MAX_VALUE / 1000);
	}
	
	/**
	 * Determines the desired alignment for this view along an
	 * axis.  By default this is simply centered.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns The desired alignment.  This should be a value
	 *   >= 0.0 and <= 1.0 where 0 indicates alignment at the
	 *   origin and 1.0 indicates alignment to the full span
	 *   away from the origin.  An alignment of 0.5 would be the
	 *   center of the view.
	 */
        public float getAlignment(int axis) {
	    if (isLoaded()) {
		return super.getAlignment(axis);
	    }
	    return 0.5f;
        }

	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to load the zone if its not
	 * already loaded, and then perform the superclass behavior.
	 *
	 * @param g the rendering surface to use
	 * @param a the allocated region to render into
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape a) {
	    load();
	    super.paint(g, a);
	}

	/**
	 * Provides a mapping from the view coordinate space to the logical
	 * coordinate space of the model.  This is implemented to first
	 * make sure the zone is loaded before providing the superclass
	 * behavior.
	 *
	 * @param x   x coordinate of the view location to convert >= 0
	 * @param y   y coordinate of the view location to convert >= 0
	 * @param a the allocated region to render into
	 * @return the location within the model that best represents the
	 *  given point in the view >= 0
	 * @see View#viewToModel
	 */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	    load();
	    return super.viewToModel(x, y, a, bias);
	}

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.  This is
         * implemented to provide the superclass behavior after first
	 * making sure the zone is loaded (The zone must be loaded to
	 * make this calculation).
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         * @exception BadLocationException  if the given position does not represent a
         *   valid location in the associated document
         * @see View#modelToView
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	    load();
	    return super.modelToView(pos, a, b);
        }

        /**
         * Start of the zones range.
	 *
         * @see View#getStartOffset
         */
        public int getStartOffset() {
	    return start.getOffset();
        }

	/**
	 * End of the zones range.
	 */
        public int getEndOffset() {
	    return end.getOffset();
        }

	/**
	 * Gives notification that something was inserted into 
	 * the document in a location that this view is responsible for.
	 * If the zone has been loaded, the superclass behavior is 
	 * invoked, otherwise this does nothing.
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#insertUpdate
	 */
        public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    if (isLoaded()) {
		super.insertUpdate(e, a, f);
	    }
	}

	/**
	 * Gives notification that something was removed from the document
	 * in a location that this view is responsible for.
	 * If the zone has been loaded, the superclass behavior is 
	 * invoked, otherwise this does nothing.
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#removeUpdate
	 */
        public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    if (isLoaded()) {
		super.removeUpdate(e, a, f);
	    }
	}

	/**
	 * Gives notification from the document that attributes were changed
	 * in a location that this view is responsible for.
	 * If the zone has been loaded, the superclass behavior is 
	 * invoked, otherwise this does nothing.
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#removeUpdate
	 */
        public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    if (isLoaded()) {
		super.changedUpdate(e, a, f);
	    }
	}

    }
}
