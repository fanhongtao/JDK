/*
 * @(#)ToolTipManager.java	1.35 98/08/26
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


package javax.swing;

import java.awt.event.*;
import java.applet.*;
import java.awt.*;

/**
 * Manages all the ToolTips in the system.
 *
 * @see JComponent#createToolTip
 * @version 1.35 08/26/98
 * @author Dave Moore
 * @author Rich Schiavi
 */
public class ToolTipManager extends MouseAdapter implements MouseMotionListener  {
    Timer enterTimer, exitTimer, insideTimer;
    String toolTipText;
    Point  preferredLocation;
    JComponent insideComponent;
    MouseEvent mouseEvent;
    boolean showImmediately;
    final static ToolTipManager sharedInstance = new ToolTipManager();
    Popup tipWindow;
    JToolTip tip;

    private Rectangle popupRect = null;
    private Rectangle popupFrameRect = null;

    boolean enabled = true;
    boolean mouseAboveToolTip = false;
    private boolean tipShowing = false;
    private long timerEnter = 0;
   
    private KeyStroke postTip,hideTip;
    private AbstractAction postTipAction, hideTipAction;

    private FocusListener focusChangeListener = null;

    protected boolean lightWeightPopupEnabled = true;
    protected boolean heavyWeightPopupEnabled = false;

    ToolTipManager() {
        enterTimer = new Timer(750, new insideTimerAction());
        enterTimer.setRepeats(false);
        exitTimer = new Timer(500, new outsideTimerAction());
        exitTimer.setRepeats(false);
        insideTimer = new Timer(4000, new stillInsideTimerAction());
        insideTimer.setRepeats(false);

	// create accessibility actions 
	postTip = KeyStroke.getKeyStroke(KeyEvent.VK_F1,Event.CTRL_MASK);
	postTipAction = new AbstractAction(){
	  public void actionPerformed(ActionEvent e){
	    if (tipWindow != null) // showing we unshow
	      hideTipWindow();
	    else {
	      hideTipWindow(); // be safe
	      enterTimer.stop();
	      exitTimer.stop();
	      insideTimer.stop();
	      insideComponent = (JComponent)e.getSource();
	      toolTipText = insideComponent.getToolTipText();
	      preferredLocation = new Point(10,insideComponent.getHeight()+10);  // manual set
	      showTipWindow();
	      // put a focuschange listener on to bring the tip down
	      if (focusChangeListener == null){
		focusChangeListener = createFocusChangeListener();
	      }
	      insideComponent.addFocusListener(focusChangeListener); 
	    }
	  }
	  public boolean isEnabled(){
	    return true;
	  }
	};
	hideTip = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
	hideTipAction = new AbstractAction(){
	  public void actionPerformed(ActionEvent e){
	    hideTipWindow();
	    JComponent jc = (JComponent)e.getSource();
	    jc.removeFocusListener(focusChangeListener);
	    preferredLocation = null;
	  }
	  public boolean isEnabled(){
	    return true;
	  }
	};
    }

    /**
     * Enables or disables the tooltip.
     *
     * @param flag  true to enable the tip
     */
    public void setEnabled(boolean flag) {
        enabled = flag;
        if (!flag) {
            hideTipWindow();
        }
    }

    /**
     * Returns true if this object is enabled.
     *
     * @return true if this object is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * When displaying the JToolTip, the ToolTipManager choose to use a light weight JPanel if
     * it fits. This method allows you to disable this feature. You have to do disable
     * it if your application mixes light weight and heavy weights components.
     * @deprecated As of Swing1.1
     *  replaced by <code>setToolTipWindowUsePolicy(int)</code>.
     */
    public void setLightWeightPopupEnabled(boolean aFlag){
      lightWeightPopupEnabled = aFlag;
    }
    
    /**
     * Returns true if lightweight (all-Java) Tooltips are in use,
     * or false if heavyweight (native peer) Tooltips are being used.
     *
     * @return true if lightweight ToolTips are in use
     */
    public boolean isLightWeightPopupEnabled() { 
        return lightWeightPopupEnabled;
    }


    /**
     * Specifies the initial delay value.
     *
     * @param microSeconds  an int specifying the number of microseconds
     *        to delay (after the cursor has paused) before displaying the
     *        tooltip
     * @see #getInitialDelay
     */
    public void setInitialDelay(int microSeconds) {
        enterTimer.setInitialDelay(microSeconds);
    }

    /**
     * Returns the initial delay value.
     *
     * @return an int representing the initial delay value
     * @see #setInitialDelay
     */
    public int getInitialDelay() {
        return enterTimer.getInitialDelay();
    }

    /**
     * Specifies the dismisal delay value.
     *
     * @param microSeconds  an int specifying the number of microseconds
     *        to delay (after the cursor has moved on) before taking away
     *        the tooltip
     * @see #getDismissDelay
     */
    public void setDismissDelay(int microSeconds) {
        insideTimer.setInitialDelay(microSeconds);
    }

    /**
     * Returns the dismisal delay value.
     *
     * @return an int representing the dismisal delay value
     * @see #setDismissDelay
     */
    public int getDismissDelay() {
        return insideTimer.getInitialDelay();
    }

    /**
     * Specifies the time to delay before reshowing the tooltip.
     *
     * @param microSeconds  an int specifying the time in microseconds
     *        before reshowing the tooltip if the cursor stops again
     * @see #getReshowDelay
     */
    public void setReshowDelay(int microSeconds) {
        exitTimer.setInitialDelay(microSeconds);
    }

    /**
     * Returns the reshow delay value.
     *
     * @return an int representing the reshow delay value
     * @see #setReshowDelay
     */
    public int getReshowDelay() {
        return exitTimer.getInitialDelay();
    }

    void showTipWindow() {
        if(insideComponent == null || !insideComponent.isShowing())
            return;
        if (enabled) {
            Dimension size;
            Point screenLocation = insideComponent.getLocationOnScreen();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Point location = new Point();

            // Just to be paranoid
            hideTipWindow();

            tip = insideComponent.createToolTip();
            tip.setTipText(toolTipText);
            size = tip.getPreferredSize();

	    // fix bug 4135787: Tooltips don't work when used in awt.Frame or awt.Applet, etc
	    // this is a quick and dirty check 
	    if (insideComponent.getRootPane() == null){
	      tipWindow = new WindowPopup((frameForComponent(insideComponent)),tip,size);
	      heavyWeightPopupEnabled = true;
	    }
	    else if (lightWeightPopupEnabled){
	      heavyWeightPopupEnabled = false;
	      tipWindow = new JPanelPopup(tip,size);
	    }
	    else {
	      heavyWeightPopupEnabled = false;
	      tipWindow = new PanelPopup(tip,size);
	    }

            tipWindow.addMouseListener(this);

            if(preferredLocation != null) {
                location.x = screenLocation.x + preferredLocation.x;
                location.y = screenLocation.y + preferredLocation.y;
            } else {
                location.x = screenLocation.x + mouseEvent.getX();
                location.y = screenLocation.y + mouseEvent.getY() + 20;

                if (location.x + size.width > screenSize.width) {
                    location.x -= size.width;
                }
                if (location.y + size.height > screenSize.height) {
                    location.y -= (size.height + 20);
                }
            }

	    // we do not adjust x/y when using awt.Window tips
	    if (!heavyWeightPopupEnabled){

		if (popupRect == null){
		  popupRect = new Rectangle();
		}
		popupRect.setBounds(location.x,location.y,
				    tipWindow.getBounds().width,tipWindow.getBounds().height);

		int y = getPopupFitHeight(popupRect, insideComponent);
		int x = getPopupFitWidth(popupRect,insideComponent);

		if (y > 0){
		    location.y -= y;
		}
		if (x > 0){
		    // adjust
		    location.x -= x;
		}
	    }		

	    tipWindow.show(insideComponent,location.x,location.y);
            insideTimer.start();
	    timerEnter = System.currentTimeMillis();
	    tipShowing = true;
        }
    }

    void hideTipWindow() {
        if (tipWindow != null) {
            tipWindow.removeMouseListener(this);
	    tipWindow.hide();
	    tipWindow = null;
	    tipShowing = false;
	    timerEnter = 0;
	    (tip.getUI()).uninstallUI(tip);
            tip = null;
            insideTimer.stop();
        }
    }

    /**
     * Returns a shared ToolTipManager instance.
     *
     * @return a shared ToolTipManager object
     */
    public static ToolTipManager sharedInstance() {
        return sharedInstance;
    }

    // add keylistener here to trigger tip for access
    /**
     * Register a component for tooltip management.
     *
     * @param component  a JComponent object
     */
    public void registerComponent(JComponent component) {
        component.removeMouseListener(this);
        component.addMouseListener(this);
	// register our accessibility keybindings for this component
	// this will apply globally across L&F
	// Post Tip: Ctrl+F1
	// Unpost Tip: Esc and Ctrl+F1
	component.registerKeyboardAction(postTipAction,postTip,JComponent.WHEN_FOCUSED);
	component.registerKeyboardAction(hideTipAction,hideTip,JComponent.WHEN_FOCUSED);
    }

    /**
     * Remove a component from tooltip control.
     *
     * @param component  a JComponent object
     */
    public void unregisterComponent(JComponent component) {
        component.removeMouseListener(this);
	component.unregisterKeyboardAction(postTip);
	component.unregisterKeyboardAction(hideTip);
    }


    // implements java.awt.event.MouseListener
    public void mouseEntered(MouseEvent event) {
       // this is here for a workaround for a Solaris *application* only bug
       // in which an extra MouseExit/Enter events are generated when a Panel
       // initially is shown
      if ((tipShowing) && !lightWeightPopupEnabled)
	{
	    if (System.currentTimeMillis() - timerEnter < 200){
		return;
	    }
	}  
        if(event.getSource() == tipWindow)
            return;

        JComponent component = (JComponent)event.getSource();
        toolTipText = component.getToolTipText(event);
        preferredLocation = component.getToolTipLocation(event);

        exitTimer.stop();

	Point location = event.getPoint();
	// ensure tooltip shows only in proper place
	if (location.x < 0 || 
	    location.x >=component.getWidth() ||
	    location.y < 0 ||
	    location.y >= component.getHeight())
	  {
	    return;
	  }

        if (insideComponent != null) {
            enterTimer.stop();
            insideComponent = null;
        }

        component.addMouseMotionListener(this);

        insideComponent = component;
	//        if (toolTipText != null) {
	// fix for 4133318
	if (tipWindow != null){
	  // fix for 4139679
	  // without this - the tip flashes
	  // since we get extra enter from the
	  // tip window when being displayed over top
	  // of the component - the behaviour is
	  // the same whether or not we are over the
	  // component - so additional location checks unneeded
	  if (heavyWeightPopupEnabled){ 
	    return;
	  }
	  else {
	    mouseEvent = event;
            if (showImmediately) {
	      showTipWindow();
            } else {
	      enterTimer.start();
            }
	  }
        }
    }

    // implements java.awt.event.MouseListener
    public void mouseExited(MouseEvent event) {
       // this is here for a workaround for a Solaris *application* only bug
      //  when Panels are used
      if ((tipShowing) && !lightWeightPopupEnabled)
	{
	    if (System.currentTimeMillis() - timerEnter < 200)
	    {
		return;
	    }
	}  

        boolean shouldHide = true;
        if (insideComponent == null) {
            // Drag exit
        } 
        if(event.getSource() == tipWindow) {
	  // if we get an exit and have a heavy window
	  // we need to check if it if overlapping the inside component
            Container insideComponentWindow = insideComponent.getTopLevelAncestor();
            Rectangle b = tipWindow.getBounds();
            Point location = event.getPoint();
            location.x += b.x;
            location.y += b.y;

            b = insideComponentWindow.getBounds();
            location.x -= b.x;
            location.y -= b.y;
            
            location = SwingUtilities.convertPoint(null,location,insideComponent);
            if(location.x >= 0 && location.x < insideComponent.getWidth() &&
               location.y >= 0 && location.y < insideComponent.getHeight()) {
                shouldHide = false;
            } else
	      shouldHide = true;
        } else if(event.getSource() == insideComponent && tipWindow != null) {
            Point location = SwingUtilities.convertPoint(insideComponent,
                                                         event.getPoint(),
                                                         null);
            Rectangle bounds = insideComponent.getTopLevelAncestor().getBounds();
            location.x += bounds.x;
            location.y += bounds.y;

            bounds = tipWindow.getBounds();
            if(location.x >= bounds.x && location.x < (bounds.x + bounds.width) &&
               location.y >= bounds.y && location.y < (bounds.y + bounds.height)) {
                shouldHide = false;
            } else
                shouldHide = true;
        } 
        
        if(shouldHide) {        
            enterTimer.stop();
	    if (insideComponent != null) {
	      insideComponent.removeMouseMotionListener(this);
	    }
            insideComponent = null;
            toolTipText = null;
            mouseEvent = null;
            hideTipWindow();
            exitTimer.start();
        }
    }

    // implements java.awt.event.MouseListener
    public void mousePressed(MouseEvent event) {
        hideTipWindow();
        enterTimer.stop();
        showImmediately = false;
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseDragged(MouseEvent event) {
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseMoved(MouseEvent event) {
        JComponent component = (JComponent)event.getSource();
        String newText = component.getToolTipText(event);
        Point  newPreferredLocation = component.getToolTipLocation(event);

        if (newText != null || newPreferredLocation != null) {
            mouseEvent = event;
            if (((newText != null && newText.equals(toolTipText)) || newText == null) &&
                ((newPreferredLocation != null && newPreferredLocation.equals(preferredLocation)) 
                 || newPreferredLocation == null)) {
                if (tipWindow != null) {
                    insideTimer.restart();
                } else {
                    enterTimer.restart();
                }
            } else {
                toolTipText = newText;
                preferredLocation = newPreferredLocation;
                if (showImmediately) {
                    hideTipWindow();
                    showTipWindow();
                } else {
                    enterTimer.restart();
                }
            }
        } else {
            toolTipText = null;
            preferredLocation = null;
            mouseEvent = null;
            hideTipWindow();
            enterTimer.stop();
            exitTimer.start();
        }
    }

    protected class insideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(insideComponent != null && insideComponent.isShowing()) {
                showImmediately = true;
                showTipWindow();
            }
        }
    }

    protected class outsideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showImmediately = false;
        }
    }

    protected class stillInsideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            hideTipWindow();
            enterTimer.stop();
            showImmediately = false;
        }
    }

    static Frame frameForComponent(Component component) {
        while (!(component instanceof Frame)) {
            component = component.getParent();
        }
        return (Frame)component;
    }

  private FocusListener createFocusChangeListener(){
    return new FocusAdapter(){
      public void focusLost(FocusEvent evt){
	hideTipWindow();
	JComponent c = (JComponent)evt.getSource();
	c.removeFocusListener(focusChangeListener);
      }
    };
  }

  // Returns: 0 no adjust
  //         -1 can't fit
  //         >0 adjust value by amount returned
  private int getPopupFitWidth(Rectangle popupRectInScreen, Component invoker){
    if (invoker != null){
      Container parent;
      for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
	// fix internal frame size bug: 4139087 - 4159012
	if(parent instanceof JFrame || parent instanceof JDialog ||
	   parent instanceof JWindow) { // no check for awt.Frame since we use Heavy tips
	  return getWidthAdjust(parent.getBounds(),popupRectInScreen);
	} else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
	  if (popupFrameRect == null){
	    popupFrameRect = new Rectangle();
	  }
	  Point p = parent.getLocationOnScreen();
	  popupFrameRect.setBounds(p.x,p.y,
				   parent.getBounds().width,
				   parent.getBounds().height);
	  return getWidthAdjust(popupFrameRect,popupRectInScreen);
	}
      }
    }
    return 0;
  }

  // Returns:  0 no adjust
  //          >0 adjust by value return
  private int getPopupFitHeight(Rectangle popupRectInScreen, Component invoker){
    if (invoker != null){
      Container parent;
      for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
	if(parent instanceof JFrame || parent instanceof JDialog ||
	   parent instanceof JWindow) {
	  return getHeightAdjust(parent.getBounds(),popupRectInScreen);
	} else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
	  if (popupFrameRect == null){
	    popupFrameRect = new Rectangle();
	  }
	  Point p = parent.getLocationOnScreen();
	  popupFrameRect.setBounds(p.x,p.y,
				   parent.getBounds().width,
				   parent.getBounds().height);
	  return getHeightAdjust(popupFrameRect,popupRectInScreen);
	}
      }
    }
    return 0;
  }

  private int getHeightAdjust(Rectangle a, Rectangle b){
    if (b.y >= a.y && (b.y + b.height) <= (a.y + a.height))
      return 0;
    else
      return (((b.y + b.height) - (a.y + a.height)) + 5);
  }

  // Return the number of pixels over the edge we are extending.
  // If we are over the edge the ToolTipManager can adjust.
  // REMIND: what if the Tooltip is just too big to fit at all - we currently will just clip
  private int getWidthAdjust(Rectangle a, Rectangle b){
    //    System.out.println("width b.x/b.width: " + b.x + "/" + b.width +
    //		       "a.x/a.width: " + a.x + "/" + a.width);
    if (b.x >= a.x && (b.x + b.width) <= (a.x + a.width)){
      return 0;
    }
    else {
      return (((b.x + b.width) - (a.x +a.width)) + 5);
    }
  }
	
  /*
   * The following interface describes what a popup should implement.
   * We do this because the ToolTip manager uses popup that can be windows or
   * panels. The reason is two-fold: We'd like to use panels mostly, but when the
   * panel (or tooltip) would not fit, we need to use a Window to avoid the panel
   * being clipped or not shown.
   *
   */
  private interface Popup {
    public void show(JComponent invoker, int x, int y);
    public void hide();
    public void addMouseListener(ToolTipManager c);
    public void removeMouseListener(ToolTipManager c);
    public Rectangle getBounds();
  }


  class JPanelPopup extends JPanel implements Popup  {
    public JPanelPopup(JComponent t, Dimension s) {
      super();
      setLayout(new BorderLayout());
      setDoubleBuffered(true);
      this.setOpaque(true);
      add(t, BorderLayout.CENTER);
      setSize(s);
    }

    public void update(Graphics g) {
      paint(g);
    }
        
    public Rectangle getBounds(){
	return super.getBounds();
    }


    public void show(JComponent invoker, int x, int y) {
      Point p = new Point(x,y);
      SwingUtilities.convertPointFromScreen(p,invoker.getRootPane().getLayeredPane());
      this.setBounds(p.x,p.y,getSize().width, getSize().height);
      invoker.getRootPane().getLayeredPane().add(this,JLayeredPane.POPUP_LAYER,0);
    }

    public void hide() {
      Container parent = getParent();
      Rectangle r = this.getBounds();
      if(parent != null){
	parent.remove(this);
	parent.repaint(r.x,r.y,r.width,r.height);
      }
    }

    public void addMouseListener(ToolTipManager c){
	super.addMouseListener(c);
    }

    public void removeMouseListener(ToolTipManager c){
	super.removeMouseListener(c);
    }

  }

  // MEDIUM
  class PanelPopup extends Panel implements Popup {
    public PanelPopup(JComponent t, Dimension s) {
      super();
      setLayout(new BorderLayout());
      add(t, BorderLayout.CENTER);
      setSize(s);
    }

    public Rectangle getBounds(){
	return super.getBounds();
    }

    public void show(JComponent invoker, int x, int y) {
	Point p = new Point(x,y);
	SwingUtilities.convertPointFromScreen(p,invoker.getRootPane().getLayeredPane());
	invoker.getRootPane().getLayeredPane().add(this,JLayeredPane.POPUP_LAYER,0);
	// 4144271
	this.setBounds(p.x,p.y,getSize().width, getSize().height);
    }


    public void hide() {
      Container parent = getParent();
      Rectangle r = this.getBounds();
      if(parent != null){
	parent.remove(this);
	parent.repaint(r.x,r.y,r.width,r.height);
      }
    }

    public void addMouseListener(ToolTipManager c){
	super.addMouseListener(c);
    }

    public void removeMouseListener(ToolTipManager c){
	super.removeMouseListener(c);
    }
  }

  class WindowPopup extends Window implements Popup  {
    boolean  firstShow = true;
    JComponent tip;
    Frame frame;

    public WindowPopup(Frame f,JComponent t, Dimension size) {
      super(f);
      this.tip = t;
      this.frame = f;
      add(t, BorderLayout.CENTER);
      pack();
      // setSize(size);
    }

    public Rectangle getBounds(){
	return super.getBounds();
    }

    public void show(JComponent invoker, int x, int y) {
      this.setLocation(x,y);
      this.setVisible(true);

      /** This hack is to workaround a bug on Solaris where the windows does not really show
       *  the first time
       *  It causes a side effect of MS JVM reporting IllegalArumentException: null source
       *  fairly frequently - also happens if you use HeavyWeight JPopup, ie JComboBox 
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

    public void addMouseListener(ToolTipManager c){
	super.addMouseListener(c);
    }

    public void removeMouseListener(ToolTipManager c){
	super.removeMouseListener(c);
    }

  }

}
