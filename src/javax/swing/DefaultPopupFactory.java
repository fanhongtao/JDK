/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.accessibility.*;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;


class DefaultPopupFactory implements PopupFactory {
    private    int lastPopupType = LIGHT_WEIGHT_POPUP;

    private static final Object heavyPopupCacheKey = 
        new StringBuffer("PopupFactory.heavyPopupCache");
    private static final Object lightPopupCacheKey = 
        new StringBuffer("PopupFactory.lightPopupCache");
    private static final Object mediumPopupCacheKey = 
        new StringBuffer("PopupFactory.mediumPopupCache");
    private static final Object defaultLWPopupEnabledKey = 
        new StringBuffer("PopupFactory.defaultLWPopupEnabledKey");

    private static final int MAX_CACHE_SIZE = 5;
    private boolean lightWeightPopupEnabled = true;
    static boolean popupPostionFixEnabled = false;

   
    /** Bug#4425878-Property javax.swing.adjustPopupLocationToFit introduced */
    static {
        popupPostionFixEnabled = java.security.AccessController.
        doPrivileged( new sun.security.action.GetPropertyAction(
        "javax.swing.adjustPopupLocationToFit","")).equals("true");
    }

    /** A light weight popup is used when it fits and light weight popups are enabled **/
    private static final int LIGHT_WEIGHT_POPUP   = 0;

    /** A "Medium weight" popup is a panel. We use this when downgrading an heavy weight in
     *  dialogs
     */
    private static final int MEDIUM_WEIGHT_POPUP  = 1;

    /** A popup implemented with a window */
    private static final int HEAVY_WEIGHT_POPUP   = 2;

    /* Lock object used in place of class object for synchronization. 
     * (4187686)
     */
    private static final Object classLock = new Object();

    /**
     *  Set the default value for the <b>lightWeightPopupEnabled</b>
     *  property.
     */
    /* Pending(arnaud) this property should scope to awt-context */
    public static void setDefaultLightWeightPopupEnabled(boolean aFlag) {
        SwingUtilities.appContextPut(defaultLWPopupEnabledKey, 
                                     new Boolean(aFlag));
    }

    /** 
     *  Return the default value for the <b>lightWeightPopupEnabled</b> 
     *  property.
     */
    public static boolean getDefaultLightWeightPopupEnabled() {
        Boolean b = (Boolean)
            SwingUtilities.appContextGet(defaultLWPopupEnabledKey);
        if (b == null) {
            SwingUtilities.appContextPut(defaultLWPopupEnabledKey, 
                                         Boolean.TRUE);
            return true;
        }
        return b.booleanValue();
    }

    public void setLightWeightPopupEnabled(boolean aFlag) {
	// PENDING(ges): should be bound
	lightWeightPopupEnabled = aFlag;
    }
    public boolean isLightWeightPopupEnabled() {
	return lightWeightPopupEnabled;
    }


    private static Hashtable getHeavyPopupCache() {
        Hashtable cache = 
            (Hashtable)SwingUtilities.appContextGet(heavyPopupCacheKey);
        if (cache == null) {
            cache = new Hashtable(2);
            SwingUtilities.appContextPut(heavyPopupCacheKey, cache);
        }
        return cache;
    }

    private static Vector getLightPopupCache() {
        Vector cache = 
            (Vector)SwingUtilities.appContextGet(lightPopupCacheKey);
        if (cache == null) {
            cache = new Vector();
            SwingUtilities.appContextPut(lightPopupCacheKey, cache);
        }
        return cache;
    }

    private static Vector getMediumPopupCache() {
        Vector cache = 
            (Vector)SwingUtilities.appContextGet(mediumPopupCacheKey);
        if (cache == null) {
            cache = new Vector();
            SwingUtilities.appContextPut(mediumPopupCacheKey, cache);
        }
        return cache;
    }

    static void recycleHeavyPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector cache;
            final Window w = SwingUtilities.getWindowAncestor((Component)aPopup);
	    Hashtable heavyPopupCache = getHeavyPopupCache();
	    if (heavyPopupCache.containsKey(w)) {
		cache = (Vector)heavyPopupCache.get(w);
	    } else {
		cache = new Vector();
		heavyPopupCache.put(w, cache);
		// Clean up if the Window is closed
		w.addWindowListener(new WindowAdapter() {
		    public void windowClosed(WindowEvent e) {
			Hashtable heavyPopupCache2 = getHeavyPopupCache();
			heavyPopupCache2.remove(w);
		    }
		});
	    }
        
	    if(cache.size() < MAX_CACHE_SIZE) {
		cache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledHeavyPopup(Window w) {
	synchronized (classLock) {
	    Vector cache;
	    Hashtable heavyPopupCache = getHeavyPopupCache();
	    if (heavyPopupCache.containsKey(w)) {
		cache = (Vector)heavyPopupCache.get(w);
	    } else {
		return null;
	    }
	    int c;
	    if((c=cache.size()) > 0) {
		Popup r = (Popup)cache.elementAt(0);
		cache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }

    boolean adjustPopuLocationToFitScreen(Component component, Point p) {
        if(popupPostionFixEnabled == false)
	    return false;
	if(component == null)
    	    return false;
	
        int scrWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

	int oldX = p.x;
	int oldY = p.y;
	Dimension size;

        size = component.getPreferredSize();

        if( (p.x + size.width) > scrWidth )
             p.x = scrWidth - size.width;

        if( (p.y + size.height) > scrHeight)
	     p.y = scrHeight - size.height;

        /*Change is made to the desired (X,Y) values, when the
           PopupMenu is too tall OR too wide for the screen
        */ 
        if( p.x < 0 )
            p.x = 0 ;  //oldX;
        if( p.y < 0 )
            p.y = 0; //oldY;

        return true;

    }

    static void recycleLightPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector lightPopupCache = getLightPopupCache();
	    if (lightPopupCache.size() < MAX_CACHE_SIZE) {
		lightPopupCache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledLightPopup() {
	synchronized (classLock) {
	    Vector lightPopupCache = getLightPopupCache();
	    int c;
	    if((c=lightPopupCache.size()) > 0) {
		Popup r = (Popup)lightPopupCache.elementAt(0);
		lightPopupCache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }

    
    static void recycleMediumPopup(Popup aPopup) {
	synchronized (classLock) {
	    Vector mediumPopupCache = getMediumPopupCache();
	    if(mediumPopupCache.size() < MAX_CACHE_SIZE) {
		mediumPopupCache.addElement(aPopup);
	    }
	}
    }

    static Popup getRecycledMediumPopup() {
	synchronized (classLock) {
	    Vector mediumPopupCache = getMediumPopupCache();
	    int c;
	    if((c=mediumPopupCache.size()) > 0) {
		Popup r = (Popup)mediumPopupCache.elementAt(0);
		mediumPopupCache.removeElementAt(0);
		return r;
	    }
	    return null;
	}
    }
    

    static void recyclePopup(Popup aPopup) {
        if(aPopup instanceof JPanelPopup)
            recycleLightPopup(aPopup);
        else if(aPopup instanceof WindowPopup) 
            recycleHeavyPopup(aPopup);
        else if(aPopup instanceof PanelPopup) 
            recycleMediumPopup(aPopup);
    }


    protected Popup createLightWeightPopup(Component comp, Component invoker) {
        Popup popup;
        popup = DefaultPopupFactory.getRecycledLightPopup();
        if(popup == null) {
            popup = new JPanelPopup();
        }
        return popup;
    }
    
    protected Popup createMediumWeightPopup(Component comp,
					    Component invoker) {
        Popup popup;
        popup = DefaultPopupFactory.getRecycledMediumPopup();
        if(popup == null) {
            popup = new PanelPopup();
        }
        return popup;
    }

    protected Popup createHeavyWeightPopup(Component comp, Component invoker) {
        Window window = invoker != null? SwingUtilities.getWindowAncestor(invoker) : null;
	Popup popup = null;

        if (window != null) {
            popup = DefaultPopupFactory.getRecycledHeavyPopup(window);
        } else {
            window = new Frame();
        }
        if (popup == null) {
            popup = new WindowPopup(window);
        }
        /* Fix Forte Menu Bug. awt_TopLevel.c looks for Window name 
         * "###overrideRedirect###" to determine that XmNoverrideRedirect 
         * needs to be set.
        */
        ((Window)popup).setName("###overrideRedirect###"); 
        return popup;
    }

    private boolean popupFit(Component invoker, Rectangle popupRectInScreen) {
        if(invoker != null) {
            Container parent;
            for(parent = invoker.getParent(); parent != null ; parent = parent.getParent()) {
                if(parent instanceof JFrame || parent instanceof JDialog ||
		   parent instanceof JWindow) {
               Rectangle r = parent.getBounds();
               Insets i = parent.getInsets();
               r.x += i.left;
               r.y += i.top;
               r.width -= (i.left + i.right);
               r.height -= (i.top + i.bottom);
               return SwingUtilities.isRectangleContainingRectangle(r, popupRectInScreen);
                } else if(parent instanceof JApplet) {
                    Rectangle r = parent.getBounds();
                    Point p  = parent.getLocationOnScreen();

                    r.x = p.x;
                    r.y = p.y;
                    return SwingUtilities.isRectangleContainingRectangle(r,popupRectInScreen);
                }
            }
        }
        return false;
    }

    private boolean ancestorIsModalDialog(Component i) {
        Container parent = null;
	if (i !=null) {
	    for(parent = i.getParent() ; parent != null ; parent = parent.getParent())
		if ((parent instanceof Dialog) && (((Dialog)parent).isModal() == true))
		    return true;
	}
	return false;
    }

    private Popup replacePopup(Component component, Component invoker,
			     int x, int y, Popup popup, int newType) {
        popup.removeComponent(component);
        recyclePopup(popup);
	popup = null;
        switch(newType) {
        case LIGHT_WEIGHT_POPUP:
            popup = createLightWeightPopup(component, invoker);
            break;
        case MEDIUM_WEIGHT_POPUP:
            popup = createMediumWeightPopup(component, invoker);
            break;
        case HEAVY_WEIGHT_POPUP:
            popup = createHeavyWeightPopup(component, invoker);
            break;
        }

        popup.setLocationOnScreen(x, y);
	/*
	  System.out.println("Adding " + component.hashCode() + "::" +
	  component + " to " + popup.hashCode() + "::"
	  + popup);
	  */
        popup.addComponent(component,"Center");
        component.invalidate();
        // popup.setBackground(component.getBackground());
        popup.pack();
	return popup;
    }

    private boolean invokerInHeavyWeightPopup(Component i) {
	if (i !=null) {
	    Container parent;
	    for(parent = i.getParent() ; parent != null ; parent =
		    parent.getParent()) {
		if(parent instanceof WindowPopup)
		    return true;
		else if(parent instanceof PanelPopup)
		    break;
		else if(parent instanceof JPanelPopup)
		    break;
	    }
	}
        return false;
    }

    public Popup getPopup(Component comp, Component invoker, int x, int y) {
	int popupType;
	int newPopupType;
	Popup popup = null;
        
	switch(lastPopupType) {
	case LIGHT_WEIGHT_POPUP:
	    popup = createLightWeightPopup(comp, invoker);
	    break;
	case MEDIUM_WEIGHT_POPUP:
	    popup = createMediumWeightPopup(comp, invoker);
	    break;
	case HEAVY_WEIGHT_POPUP:
	    popup = createHeavyWeightPopup(comp, invoker);
	    break;
	}
	popupType = lastPopupType;
	Point p = new Point(x, y);
        if( adjustPopuLocationToFitScreen(comp, p) == true )
        {
            x = p.x;
            y = p.y;
        }
	popup.setLocationOnScreen(x, y);
	popup.addComponent(comp, BorderLayout.CENTER);
	// popup.setBackground(component.getBackground());
	popup.pack();
	/*
	  System.out.println("Adding " + component.hashCode() + "::" +
	  component + " to " + popup.hashCode() + "::"
	  + popup);
	  */
	Rectangle popupRect = new Rectangle(x, y, 
					    popup.getWidth(),popup.getHeight());
	
	
	if(popupFit(invoker, popupRect)) {
	    if(((comp instanceof JToolTip) ||
		(comp instanceof JPopupMenu && 
		 ((JPopupMenu)comp).isLightWeightPopupEnabled()))
	       && lightWeightPopupEnabled) {
		newPopupType = LIGHT_WEIGHT_POPUP;
	    } else {
		newPopupType = MEDIUM_WEIGHT_POPUP;
	    }
 	} else {
            newPopupType = HEAVY_WEIGHT_POPUP;
	}
	
	if(invokerInHeavyWeightPopup(invoker)) {
	    newPopupType = HEAVY_WEIGHT_POPUP;
	}
	if(invoker == null) {
	    newPopupType = HEAVY_WEIGHT_POPUP;
	}
	
	if(newPopupType != popupType) {
	    popup = replacePopup(comp, invoker, x, y, popup, newPopupType);
	    popupType = newPopupType;
	}
	
	lastPopupType = popupType;
	return popup;
    }    


  /**
   * A class used to popup a window.
   * <p>
   * <strong>Warning:</strong>
   * Serialized objects of this class will not be compatible with
   * future Swing releases.  The current serialization support is appropriate
   * for short term storage or RMI between applications running the same
   * version of Swing.  A future release of Swing will provide support for
   * long term persistence.
   */
    protected class WindowPopup extends JWindow implements Popup,Serializable,Accessible {
    int saveX,saveY;
    boolean  firstShow = true;

    public WindowPopup(Window w) {
        super(w);
    }

    protected void processKeyEvent(KeyEvent e) {
        MenuSelectionManager.defaultManager().processKeyEvent(e);
    }
        
    public Component getComponent() {
      return this;
    }

    public int  getWidth() {
      return getBounds().width;
    }

    public int  getHeight() {
      return getBounds().height;
    }

    public void update(Graphics g) {
      paint(g);
    }
        
    public void show(Component invoker) {
      this.setLocation(saveX,saveY);
      this.setVisible(true);

      /** This hack is to workaround a bug on Solaris where the windows does not really show
       *  the first time
       */
      if(firstShow) {
        this.hide();
        this.setVisible(true);
        firstShow = false;
      }
    }
        
    public void hide() {
      super.hide();
      /** We need to call removeNotify() here because hide() does something only if
       *  Component.visible is true. When the app frame is miniaturized, the parent 
       *  frame of this frame is invisible, causing AWT to believe that this frame
       *  is invisible and causing hide() to do nothing
       */
      removeNotify();
    }

    public Rectangle getBoundsOnScreen() {
      return getBounds();
    }

    public void setLocationOnScreen(int x,int y) {
      this.setLocation(x,y);
      saveX = x;
      saveY = y;
    }

    public void addComponent(Component aComponent,Object constraints) {
      this.getContentPane().add(aComponent,constraints);
    }

    public void removeComponent(Component c) {
      this.getContentPane().remove(c);
    }
      
   /**
     * Get the AccessibleContext associated with this popup
     *
     * @return the AccessibleContext of this popup
     */
    public AccessibleContext getAccessibleContext() {
	  if (accessibleContext == null) {
        accessibleContext = new AccessibleWindowPopup();
      }
      return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleWindowPopup extends AccessibleContext
    implements Serializable, AccessibleComponent {
    
    protected AccessibleContext accessibleContext = null;    


      // AccessibleContext methods
      //
      /**
       * Get the role of this object.
       *
       * @return an instance of AccessibleRole describing the role of
       * the object
       * @see AccessibleRole
       */
      public AccessibleRole getAccessibleRole() {
        return AccessibleRole.WINDOW;
      }
    
      /**
       * Get the state of this object.
       *
       * @return an instance of AccessibleStateSet containing the 
       * current state set of the object
       * @see AccessibleState
       */
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet states = new AccessibleStateSet();
        if (getFocusOwner() != null) {
          states.add(AccessibleState.ACTIVE);
          states.add(AccessibleState.FOCUSED);
        }
	if (isFocusTraversable()) {
          states.add(AccessibleState.FOCUSABLE);
        }
	if (isOpaque()) {
          states.add(AccessibleState.OPAQUE);
        }
	if (isShowing()) {
          states.add(AccessibleState.SHOWING);
        }
	if (isVisible()) {
          states.add(AccessibleState.VISIBLE);
        }
        return states;
      }

      /**
       * Get the Accessible parent of this object.  If the parent of this
       * object implements Accessible, this method should simply return
       * getParent().
       *
       * @return the Accessible parent of this object -- can be null if 
       * this object does not have an Accessible parent
       */
      public Accessible getAccessibleParent() {
          if (accessibleParent != null) {
	      return accessibleParent;
	  } else {
              Container parent = getParent();
              if (parent instanceof Accessible) {
                  return (Accessible) parent;
	      }
          }
          return null;
      }
    
      /**
       * Get the index of this object in its accessible parent. 
       *
       * @return the index of this object in its parent; -1 if this 
       * object does not have an accessible parent.
       * @see #getAccessibleParent
       */
      public int getAccessibleIndexInParent() {
        return SwingUtilities.getAccessibleIndexInParent(WindowPopup.this);
      }
    
      /**
       * Returns the number of accessible children in the object.  If all
       * of the children of this object implement Accessible, than this
       * method should return the number of children of this object.
       *
       * @return the number of accessible children in the object.
       */
      public int getAccessibleChildrenCount() {
        return SwingUtilities.getAccessibleChildrenCount(WindowPopup.this);
      }
    
      /**
       * Return the nth Accessible child of the object.  
       *
       * @param i zero-based index of child
       * @return the nth Accessible child of the object
       */
      public Accessible getAccessibleChild(int i) {
        return SwingUtilities.getAccessibleChild(WindowPopup.this,i);
      }
    
      /**
       * Return the locale of this object.
       *
       * @return the locale of this object
       */
      public Locale getLocale() {
        return WindowPopup.this.getLocale();
      }
    
      /**
       * Get the AccessibleComponent associated with this object if one
       * exists.  Otherwise return null.
       */
      public AccessibleComponent getAccessibleComponent() {
        return this;
      }
    
    
      // AccessibleComponent methods
      //
            /**
             * Get the background color of this object.
             *
             * @return the background color, if supported, of the object; 
             * otherwise, null
             */
            public Color getBackground() {
                return WindowPopup.this.getBackground();
            }
    
            /**
             * Set the background color of this object.
             *
             * @param c the new Color for the background
             */
            public void setBackground(Color c) {
                WindowPopup.this.setBackground(c);
            }
    
            /**
             * Get the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object; 
             * otherwise, null
             */
            public Color getForeground() {
                return WindowPopup.this.getForeground();
            }
    
            /**
             * Set the foreground color of this object.
             *
             * @param c the new Color for the foreground
             */
            public void setForeground(Color c) {
                WindowPopup.this.setForeground(c);
            }
    
            /**
             * Get the Cursor of this object.
             *
             * @return the Cursor, if supported, of the object; otherwise, null
             */
            public Cursor getCursor() {
                return WindowPopup.this.getCursor();
            }
    
            /**
             * Set the Cursor of this object.
             *
             * @param c the new Cursor for the object
             */
            public void setCursor(Cursor cursor) {
                WindowPopup.this.setCursor(cursor);
            }
    
            /**
             * Get the Font of this object.
             *
             * @return the Font,if supported, for the object; otherwise, null
             */
            public Font getFont() {
                return WindowPopup.this.getFont();
            }
    
            /**
             * Set the Font of this object.
             *
             * @param f the new Font for the object
             */
            public void setFont(Font f) {
                WindowPopup.this.setFont(f);
            }
    
            /**
             * Get the FontMetrics of this object.
             *
             * @param f the Font
             * @return the FontMetrics, if supported, the object; 
             * otherwise, null
             * @see #getFont
             */
            public FontMetrics getFontMetrics(Font f) {
                return WindowPopup.this.getFontMetrics(f);
            }
    
            /**
             * Determine if the object is enabled.
             *
             * @return true if object is enabled; otherwise, false
             */
            public boolean isEnabled() {
                return WindowPopup.this.isEnabled();
            }
    
            /**
             * Set the enabled state of the object.
             *
             * @param b if true, enables this object; otherwise, disables it 
             */
            public void setEnabled(boolean b) {
                WindowPopup.this.setEnabled(b);
            }
            
            /**
             * Determine if the object is visible.  Note: this means that the
             * object intends to be visible; however, it may not in fact be
             * showing on the screen because one of the objects this object
             * is contained by is not visible.  To determine if an object is
             * showing on the screen, use isShowing().
             *
             * @return true if object is visible; otherwise, false
             */
            public boolean isVisible() {
                return WindowPopup.this.isVisible();
            }
    
            /**
             * Set the visible state of the object.
             *
             * @param b if true, shows this object; otherwise, hides it 
             */
            public void setVisible(boolean b) {
                WindowPopup.this.setVisible(b);
            }
    
            /**
             * Determine if the object is showing.  Determined by checking
             * the visibility of the object and ancestors of the object.  
             * This will return true even if the object is obscured by another 
             * (for example, it is underneath a menu that was pulled 
             * down).
             *
             * @return true if object is showing; otherwise, false
             */
            public boolean isShowing() {
                return WindowPopup.this.isShowing();
            }
    
            /** 
             * Checks if the specified point is within this object's bounds,
             * where the point's x and y coordinates are defined to be relative
             * to the coordinate system of the object. 
             *
             * @param p the Point relative to the coordinate system of the 
             * object
             * @return true if object contains Point; otherwise false
             */
            public boolean contains(Point p) {
                return WindowPopup.this.contains(p);
            }
        
            /** 
             * Returns the location of the object on the screen.
             *
             * @return location of object on screen -- can be null if this
             * object is not on the screen
             */
            public Point getLocationOnScreen() {
                return WindowPopup.this.getLocationOnScreen();
            }
    
            /** 
             * Gets the location of the object relative to the parent in the 
             * form of a point specifying the object's top-left corner in the
             * screen's coordinate space.
             *
             * @return An instance of Point representing the top-left corner 
             * of the objects's bounds in the coordinate space of the screen; 
             * null if this object or its parent are not on the screen
             */
            public Point getLocation() {
                return WindowPopup.this.getLocation();
            }
    
            /** 
             * Sets the location of the object relative to the parent.
             */
            public void setLocation(Point p) {
                WindowPopup.this.setLocation(p);
            }
    
            /** 
             * Gets the bounds of this object in the form of a Rectangle 
             * object.  The bounds specify this object's width, height, 
             * and location relative to its parent. 
             *
             * @return A rectangle indicating this component's bounds; null if 
             * this object is not on the screen.
             */
            public Rectangle getBounds() {
                return WindowPopup.this.getBounds();
            }
    
            /** 
             * Sets the bounds of this object in the form of a Rectangle 
             * object.  The bounds specify this object's width, height, 
             * and location relative to its parent.
             *      
             * @param A rectangle indicating this component's bounds
             */
            public void setBounds(Rectangle r) {
                WindowPopup.this.setBounds(r);
            }
    
            /** 
             * Returns the size of this object in the form of a Dimension 
             * object.  The height field of the Dimension object contains 
             * this objects's height, and the width field of the Dimension 
             * object contains this object's width. 
             *
             * @return A Dimension object that indicates the size of this 
             * component; null if this object is not on the screen
             */
            public Dimension getSize() {
                return WindowPopup.this.getSize();
            }
    
            /** 
             * Resizes this object so that it has width width and height. 
             *      
             * @param d - The dimension specifying the new size of the object. 
             */
            public void setSize(Dimension d) {
                WindowPopup.this.setSize(d);
            }
    
            /**
             * Returns the Accessible child, if one exists, contained at the 
             * local coordinate Point.
             *
             * @param p The point defining the top-left corner of the 
             * Accessible, given in the coordinate space of the object's 
             * parent. 
             * @return the Accessible, if it exists, at the specified 
             * location; else null
             */
            public Accessible getAccessibleAt(Point p) {
                return SwingUtilities.getAccessibleAt(WindowPopup.this,p);
            }
    
            /**
             * Returns whether this object can accept focus or not.
             *
             * @return true if object can accept focus; otherwise false
             */
            public boolean isFocusTraversable() {
                return WindowPopup.this.isFocusTraversable();
            }
    
            /**
             * Requests focus for this object.
             */
            public void requestFocus() {
                WindowPopup.this.requestFocus();
            }
    
            /**
             * Adds the specified focus listener to receive focus events from
             * this component. 
             *
             * @param l the focus listener
             */
            public void addFocusListener(FocusListener l) {
                WindowPopup.this.addFocusListener(l);
            }
    
            /**
             * Removes the specified focus listener so it no longer receives 
             * focus events from this component.
             *
             * @param l the focus listener
             */
            public void removeFocusListener(FocusListener l) {
                WindowPopup.this.removeFocusListener(l);
            }
        } // inner class AccessibleWindowPopup
    }

    /**
     * A class used to popup a JPanel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class JPanelPopup extends JPanel implements Popup,Serializable {
        int desiredLocationX,desiredLocationY;

        public JPanelPopup() {
            super();
            setLayout(new BorderLayout());
            setDoubleBuffered(true);
            this.setOpaque(true);
        }

	protected void processKeyEvent(KeyEvent e) {
            MenuSelectionManager.defaultManager().processKeyEvent(e);
        }

        public Component getComponent() {
            return this;
        }

        public void addComponent(Component aComponent,Object constraints) {
            this.add(aComponent,constraints);
        }

        public void removeComponent(Component c) {
            this.remove(c);
        }

        public void update(Graphics g) {
            paint(g);
        }
        
        public void pack() {
            setSize(getPreferredSize());
        }


        public void show(Component invoker) {
	    Container parent = null;
	    if (invoker != null)
		parent = invoker.getParent();
            Window parentWindow = null;

            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof JRootPane) {
		    if(p.getParent() instanceof JInternalFrame)
			continue;		    
                    parent = ((JRootPane)p).getLayeredPane();
                    for(p = parent.getParent(); p != null && (!(p instanceof java.awt.Window)); 
                        p = p.getParent());
                    parentWindow = (Window)p;
                    break;
                } else if(p instanceof Window) {
                    parent = p;
                    parentWindow = (Window)p;
                    break;
                }
            }
            Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
            this.setLocation(p.x,p.y);
            if(parent instanceof JLayeredPane) {
                ((JLayeredPane)parent).add(this,JLayeredPane.POPUP_LAYER,0);
            } else
                parent.add(this);
        }

        public void hide() {
            Container parent = getParent();
            Rectangle r = this.getBounds();
            if(parent != null)
                parent.remove(this);
            parent.repaint(r.x,r.y,r.width,r.height);
        }

        public Rectangle getBoundsOnScreen() {
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                Point p;
                p = convertParentLocationToScreen(parent,r.x,r.y);
                r.x = p.x;
                r.y = p.y;
                return r;
            } else 
                throw new Error("getBoundsOnScreen called on an invisible popup");
        }

        Point convertParentLocationToScreen(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            Container p;
            Point pt;
            for(p = this; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                r = parentWindow.getBounds();
                pt = new Point(x,y);
                pt = SwingUtilities.convertPoint(parent,pt,null);
                pt.x += r.x;
                pt.y += r.y;
                return pt;
            } else
                throw new Error("convertParentLocationToScreen: no window ancestor found");                        }

        Point convertScreenLocationToParent(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                Point p = new Point(x,y);
                SwingUtilities.convertPointFromScreen(p,parent);
                return p;
            } else
                throw new Error("convertScreenLocationToParent: no window ancestor found");
        }

        public void setLocationOnScreen(int x,int y) {
            Container parent = getParent();
            if(parent != null) {
                Point p = convertScreenLocationToParent(parent,x,y);
                this.setLocation(p.x,p.y);
            } else {
                desiredLocationX = x;
                desiredLocationY = y;
            }
        }
    }

    /**
     * A class used to popup an AWT panel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class PanelPopup extends Panel implements Popup,Serializable {
        int desiredLocationX,desiredLocationY;
	JRootPane rootPane;
        public PanelPopup() {
            super();
            setLayout(new BorderLayout());
	    rootPane = new JRootPane();
	    this.add(rootPane, BorderLayout.CENTER);
        }

	protected void processKeyEvent(KeyEvent e) {
            MenuSelectionManager.defaultManager().processKeyEvent(e);
        }

        public int getWidth() {
            return getBounds().width;
        }

        public int getHeight() {
            return getBounds().height;
        }

        public Component getComponent() {
            return this;
        }

        public void addComponent(Component aComponent,Object constraints) {
            rootPane.getContentPane().add(aComponent,constraints);
        }

        public void removeComponent(Component c) {
            rootPane.getContentPane().remove(c);
        }

        public void update(Graphics g) {
            paint(g);
        }
        
        public void paint(Graphics g) {
            super.paint(g);
        }
        
        public void pack() {
            setSize(getPreferredSize());
        }


        public void show(Component invoker) {
	    Container parent = null;
	    if (invoker != null)
		parent = invoker.getParent();
	    /*
	      Find the top level window,  
	      if it has a layered pane,
	      add to that, otherwise
	      add to the window. */

	    while(!(parent instanceof Window || parent instanceof Applet) && (parent!=null)) {
		parent = parent.getParent();
	    }
            super.hide();
	    if (parent instanceof RootPaneContainer) {
		parent = ((RootPaneContainer)parent).getLayeredPane();
		Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
		this.setLocation(p.x,p.y);
		((JLayeredPane)parent).add(this,JLayeredPane.POPUP_LAYER,0);
	    } else {
		Point p = convertScreenLocationToParent(parent,desiredLocationX,desiredLocationY);
		this.setLocation(p.x,p.y);
		parent.add(this);
	    }
            super.show();
	}
          
        public void hide() {
            Container parent = getParent();
            Rectangle r = this.getBounds();
            if(parent != null)
                parent.remove(this);
            parent.repaint(r.x,r.y,r.width,r.height);
        }

        public Rectangle getBoundsOnScreen() {
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                Point p;
                p = convertParentLocationToScreen(parent,r.x,r.y);
                r.x = p.x;
                r.y = p.y;
                return r;
            } else 
                throw new Error("getBoundsOnScreen called on an invisible popup");
        }

        Point convertParentLocationToScreen(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            Container p;
            Point pt;
            for(p = this; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                r = parentWindow.getBounds();
                pt = new Point(x,y);
                pt = SwingUtilities.convertPoint(parent,pt,null);
                pt.x += r.x;
                pt.y += r.y;
                return pt;
            } else
                throw new Error("convertParentLocationToScreen: no window ancestor found");                        }

        Point convertScreenLocationToParent(Container parent,int x,int y) {
            Window parentWindow = null;
            Rectangle r;
            for(Container p = parent; p != null; p = p.getParent()) {
                if(p instanceof Window) {
                    parentWindow = (Window)p;
                    break;
                }
            }
            if(parentWindow != null) {
                Point p = new Point(x,y);
                SwingUtilities.convertPointFromScreen(p,parent);
                return p;
            } else
                throw new Error("convertScreenLocationToParent: no window ancestor found");
        }

        public void setLocationOnScreen(int x,int y) {
            Container parent = getParent();
            if(parent != null) {
                Point p = convertScreenLocationToParent(parent,x,y);
                this.setLocation(p.x,p.y);
            } else {
                desiredLocationX = x;
                desiredLocationY = y;
            }
        }
    }


}


