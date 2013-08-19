/*
 * @(#)BasicOptionPaneUI.java	1.55 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.security.AccessController;


/**
 * Provides the basic look and feel for a <code>JOptionPane</code>.
 * <code>BasicMessagePaneUI</code> provides a means to place an icon,
 * message and buttons into a <code>Container</code>.
 * Generally, the layout will look like:<p>
 * <pre>
 *        ------------------
 *        | i | message    |
 *        | c | message    |
 *        | o | message    |
 *        | n | message    |
 *        ------------------
 *        |     buttons    |
 *        |________________|
 * </pre>
 * icon is an instance of <code>Icon</code> that is wrapped inside a
 * <code>JLabel</code>.  The message is an opaque object and is tested
 * for the following: if the message is a <code>Component</code> it is
 * added to the <code>Container</code>, if it is an <code>Icon</code>
 * it is wrapped inside a <code>JLabel</code> and added to the 
 * <code>Container</code> otherwise it is wrapped inside a <code>JLabel</code>.
 * <p>
 * The above layout is used when the option pane's 
 * <code>ComponentOrientation</code> property is horizontal, left-to-right.
 * The layout will be adjusted appropriately for other orientations.
 * <p>
 * The <code>Container</code>, message, icon, and buttons are all
 * determined from abstract methods.
 * 
 * @version 1.55 01/23/03
 * @author James Gosling
 * @author Scott Violet
 * @author Amy Fowler
 */
public class BasicOptionPaneUI extends OptionPaneUI {

    public static final int MinimumWidth = 262;
    public static final int MinimumHeight = 90;

    private static String newline;

    /**
     * The mnemonics for the keys, these are set in <code>getButtons</code>.
     */
    private int[] mnemonics;

    /**
     * <code>JOptionPane</code> that the receiver is providing the
     * look and feel for.
     */
    protected JOptionPane         optionPane;

    protected Dimension minimumSize;

    /** JComponent provide for input if optionPane.getWantsInput() returns
     * true. */
    protected JComponent          inputComponent;

    /** Component to receive focus when messaged with selectInitialValue. */
    protected Component           initialFocusComponent;

    /** This is set to true in validateComponent if a Component is contained
     * in either the message or the buttons. */
    protected boolean             hasCustomComponents;

    protected PropertyChangeListener propertyChangeListener;

    static {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
                public Object run() {
		    newline = System.getProperty("line.separator");
		    if (newline == null) {
		        newline = "\n";
		    }
		    return null;
                }
            }
        );
    }


    /**
      * Creates a new BasicOptionPaneUI instance.
      */
    public static ComponentUI createUI(JComponent x) {
	return new BasicOptionPaneUI();
    }

    /**
      * Installs the receiver as the L&F for the passed in
      * <code>JOptionPane</code>.
      */
    public void installUI(JComponent c) {
	optionPane = (JOptionPane)c;
        installDefaults();
        optionPane.setLayout(createLayoutManager());
	installComponents();
        installListeners(); 
        installKeyboardActions();
    }

    /**
      * Removes the receiver from the L&F controller of the passed in split
      * pane.
      */
    public void uninstallUI(JComponent c) {
        uninstallComponents();
        optionPane.setLayout(null);
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
	optionPane = null;
    }

    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(optionPane, "OptionPane.background", 
                                         "OptionPane.foreground", "OptionPane.font");
	LookAndFeel.installBorder(optionPane, "OptionPane.border");
        minimumSize = UIManager.getDimension("OptionPane.minimumSize");
	optionPane.setOpaque(true);

    }

    protected void uninstallDefaults() {
	LookAndFeel.uninstallBorder(optionPane);
    }

    protected void installComponents() {
	optionPane.add(createMessageArea());
        
        Container separator = createSeparator();
        if (separator != null) {
            optionPane.add(separator);
        }
	optionPane.add(createButtonArea());
	optionPane.applyComponentOrientation(optionPane.getComponentOrientation());
    }

    protected void uninstallComponents() {
	hasCustomComponents = false;
        inputComponent = null;
	initialFocusComponent = null;
	optionPane.removeAll();
    }

    protected LayoutManager createLayoutManager() {
        return new BoxLayout(optionPane, BoxLayout.Y_AXIS);
    }

    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            optionPane.addPropertyChangeListener(propertyChangeListener);
        }
    }

    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            optionPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected void installKeyboardActions() {
	InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

	SwingUtilities.replaceUIInputMap(optionPane, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, map);
	ActionMap actionMap = getActionMap();

	SwingUtilities.replaceUIActionMap(optionPane, actionMap);
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIInputMap(optionPane, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, null);
	SwingUtilities.replaceUIActionMap(optionPane, null);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    Object[] bindings = (Object[])UIManager.get
		                ("OptionPane.windowBindings");
	    if (bindings != null) {
		return LookAndFeel.makeComponentInputMap(optionPane, bindings);
	    }
	}
	return null;
    }

    ActionMap getActionMap() {
	ActionMap map = (ActionMap)UIManager.get("OptionPane.actionMap");

	if (map == null) {
	    map = createActionMap();
	    if (map != null) {
		UIManager.getLookAndFeelDefaults().put("OptionPane.actionMap",
                                                       map);
	    }
	}
	return map;
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("close", new CloseAction());
	// Set the ActionMap's parent to the Auditory Feedback Action Map
	BasicLookAndFeel lf = (BasicLookAndFeel)UIManager.getLookAndFeel();
	ActionMap audioMap = lf.getAudioActionMap();
	map.setParent(audioMap);

	return map;
    }

    /**
     * Returns the minimum size the option pane should be. Primarily
     * provided for subclassers wishing to offer a different minimum size.
     */
    public Dimension getMinimumOptionPaneSize() {
        if (minimumSize == null) {
            //minimumSize = UIManager.getDimension("OptionPane.minimumSize");
            // this is called before defaults initialized?!!!
            return new Dimension(MinimumWidth, MinimumHeight);
        }
	return new Dimension(minimumSize.width,
			     minimumSize.height);
    }

    /**
     * If <code>c</code> is the <code>JOptionPane</code> the receiver
     * is contained in, the preferred
     * size that is returned is the maximum of the preferred size of
     * the <code>LayoutManager</code> for the <code>JOptionPane</code>, and
     * <code>getMinimumOptionPaneSize</code>.
     */
    public Dimension getPreferredSize(JComponent c) {
	if ((JOptionPane)c == optionPane) {
	    Dimension            ourMin = getMinimumOptionPaneSize();
	    LayoutManager        lm = c.getLayout();

	    if (lm != null) {
		Dimension         lmSize = lm.preferredLayoutSize(c);

		if (ourMin != null)
		    return new Dimension
			(Math.max(lmSize.width, ourMin.width),
			 Math.max(lmSize.height, ourMin.height));
		return lmSize;
	    }
	    return ourMin;
	}
	return null;
    }

    /**
     * Messages getPreferredSize.
     */
    public Dimension getMinimumSize(JComponent c) {
	return getPreferredSize(c);
    }

    /**
     * Messages getPreferredSize.
     */
    public Dimension getMaximumSize(JComponent c) {
	return getPreferredSize(c);
    }

    /**
     * Messaged from installComponents to create a Container containing the
     * body of the message. The icon is the created by calling
     * <code>addIcon</code>.
     */
    protected Container createMessageArea() {
        JPanel top = new JPanel();
        top.setBorder(UIManager.getBorder("OptionPane.messageAreaBorder"));
	top.setLayout(new BorderLayout());

	/* Fill the body. */
	Container          body = new JPanel() {};
	Container          realBody = new JPanel() {};

	realBody.setLayout(new BorderLayout());

	if (getIcon() != null) {
            Container sep = new JPanel() {
	        public Dimension getPreferredSize() {
		    return new Dimension(15, 1);
	        }
            };
	    realBody.add(sep, BorderLayout.BEFORE_LINE_BEGINS);
	}
	realBody.add(body, BorderLayout.CENTER);

	body.setLayout(new GridBagLayout());
	GridBagConstraints cons = new GridBagConstraints();
	cons.gridx = cons.gridy = 0;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	cons.gridheight = 1;
	cons.anchor = GridBagConstraints.LINE_START;
	cons.insets = new Insets(0,0,3,0);

	addMessageComponents(body, cons, getMessage(),
			  getMaxCharactersPerLineCount(), false);
	top.add(realBody, BorderLayout.CENTER);

	addIcon(top);
	return top;
    }

    /**
     * Creates the appropriate object to represent <code>msg</code> and
     * places it into <code>container</code>. If <code>msg</code> is an
     * instance of Component, it is added directly, if it is an Icon,
     * a JLabel is created to represent it, otherwise a JLabel is
     * created for the string, if <code>d</code> is an Object[], this
     * method will be recursively invoked for the children.
     * <code>internallyCreated</code> is true if Objc is an instance
     * of Component and was created internally by this method (this is
     * used to correctly set hasCustomComponents only if !internallyCreated).
     */
    protected void addMessageComponents(Container container,
				     GridBagConstraints cons,
				     Object msg, int maxll,
				     boolean internallyCreated) {
	if (msg == null) {
	    return;
        }
	if (msg instanceof Component) {
            // To workaround problem where Gridbad will set child
            // to its minimum size if its preferred size will not fit
            // within allocated cells
            if (msg instanceof JScrollPane || msg instanceof JPanel) {
                cons.fill = GridBagConstraints.BOTH;
                cons.weighty = 1;
            } else {
	        cons.fill = GridBagConstraints.HORIZONTAL;
            }
	    cons.weightx = 1;

	    container.add((Component) msg, cons);
	    cons.weightx = 0;
            cons.weighty = 0;
	    cons.fill = GridBagConstraints.NONE;
	    cons.gridy++;
	    if (!internallyCreated) {
		hasCustomComponents = true;
            }

	} else if (msg instanceof Object[]) {
	    Object [] msgs = (Object[]) msg;
	    for (int i = 0; i < msgs.length; i++) {
		addMessageComponents(container, cons, msgs[i], maxll, false);
            }

	} else if (msg instanceof Icon) {
	    JLabel label = new JLabel( (Icon)msg, SwingConstants.CENTER );
            configureMessageLabel(label);
	    addMessageComponents(container, cons, label, maxll, true);

	} else {
	    String s = msg.toString();
	    int len = s.length();
	    if (len <= 0) {
		return;
            }
	    int nl = -1;
	    int nll = 0;

	    if ((nl = s.indexOf(newline)) >= 0) {
		nll = newline.length();
	    } else if ((nl = s.indexOf("\r\n")) >= 0) {
	        nll = 2;
	    } else if ((nl = s.indexOf('\n')) >= 0) {
	        nll = 1;
	    }
	    if (nl >= 0) {
		// break up newlines
		if (nl == 0) {
		    addMessageComponents(container, cons, new Component() {
		        public Dimension getPreferredSize() {
			    Font       f = getFont();
			    
			    if (f != null) {
				return new Dimension(1, f.getSize() + 2);
                            }
			    return new Dimension(0, 0);
		        }
		    }, maxll, true);
		} else {
		    addMessageComponents(container, cons, s.substring(0, nl),
				      maxll, false);
                }
		addMessageComponents(container, cons, s.substring(nl + nll), maxll,
				  false);

	    } else if (len > maxll) {
		Container c = Box.createVerticalBox();
		burstStringInto(c, s, maxll);
		addMessageComponents(container, cons, c, maxll, true );

	    } else {
	        JLabel label;
		label = new JLabel( s, JLabel.LEADING );
                configureMessageLabel(label);
		addMessageComponents(container, cons, label, maxll, true);
	    }
	}
    }

    /**
     * Returns the message to display from the JOptionPane the receiver is
     * providing the look and feel for.
     */
    protected Object getMessage() {
	inputComponent = null;
	if (optionPane != null) {
	    if (optionPane.getWantsInput()) {
		/* Create a user component to capture the input. If the
		   selectionValues are non null the component and there
		   are < 20 values it'll be a combobox, if non null and
		   >= 20, it'll be a list, otherwise it'll be a textfield. */
		Object             message = optionPane.getMessage();
		Object[]           sValues = optionPane.getSelectionValues();
		Object             inputValue = optionPane
		                           .getInitialSelectionValue();
		JComponent         toAdd;

		if (sValues != null) {
		    if (sValues.length < 20) {
			JComboBox            cBox = new JComboBox();

			for(int counter = 0, maxCounter = sValues.length;
			    counter < maxCounter; counter++) {
			    cBox.addItem(sValues[counter]);
                        }
			if (inputValue != null) {
			    cBox.setSelectedItem(inputValue);
                        }
			inputComponent = cBox;
			toAdd = cBox;

		    } else {
			JList                list = new JList(sValues);
			JScrollPane          sp = new JScrollPane(list);

			list.setVisibleRowCount(10);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if(inputValue != null)
			    list.setSelectedValue(inputValue, true);
			list.addMouseListener(new ListSelectionListener());
			toAdd = sp;
			inputComponent = list;
		    }

		} else {
		    MultiplexingTextField   tf = new MultiplexingTextField(20);

                    tf.setKeyStrokes(new KeyStroke[] {
                                     KeyStroke.getKeyStroke("ENTER") } );
		    if (inputValue != null) {
                        String inputString = inputValue.toString();
			tf.setText(inputString);
                        tf.setSelectionStart(0);
                        tf.setSelectionEnd(inputString.length());
                    }
		    tf.addActionListener(new TextFieldActionListener());
		    toAdd = inputComponent = tf;
		}

		Object[]           newMessage;

		if (message == null) {
		    newMessage = new Object[1];
		    newMessage[0] = toAdd;
		
		} else {
		    newMessage = new Object[2];
		    newMessage[0] = message;
		    newMessage[1] = toAdd;
		}
		return newMessage;
	    }
	    return optionPane.getMessage();
	}
	return null;
    }

    /**
     * Creates and adds a JLabel representing the icon returned from
     * <code>getIcon</code> to <code>top</code>. This is messaged from
     * <code>createMessageArea</code>
     */
    protected void addIcon(Container top) {
	/* Create the icon. */
	Icon                  sideIcon = getIcon();

	if (sideIcon != null) {
	    JLabel            iconLabel = new JLabel(sideIcon);

	    iconLabel.setVerticalAlignment(SwingConstants.TOP);
	    top.add(iconLabel, BorderLayout.BEFORE_LINE_BEGINS);
	}
    }

    /**
     * Returns the icon from the JOptionPane the receiver is providing
     * the look and feel for, or the default icon as returned from
     * <code>getDefaultIcon</code>.
     */
    protected Icon getIcon() {
	Icon      mIcon = (optionPane == null ? null : optionPane.getIcon());

	if(mIcon == null && optionPane != null)
	    mIcon = getIconForType(optionPane.getMessageType());
	return mIcon;
    }

    /**
     * Returns the icon to use for the passed in type.
     */
    protected Icon getIconForType(int messageType) {
	if(messageType < 0 || messageType > 3)
	    return null;
	switch(messageType) {
	case 0:
	    return UIManager.getIcon("OptionPane.errorIcon");
	case 1:
	    return UIManager.getIcon("OptionPane.informationIcon");
	case 2:
	    return UIManager.getIcon("OptionPane.warningIcon");
	case 3:
	    return UIManager.getIcon("OptionPane.questionIcon");
	}
	return null;
    }

    /**
     * Returns the maximum number of characters to place on a line.
     */
    protected int getMaxCharactersPerLineCount() {
	return optionPane.getMaxCharactersPerLineCount();
    }

   /**
     * Recursively creates new JLabel instances to represent <code>d</code>.
     * Each JLabel instance is added to <code>c</code>.
     */
    protected void burstStringInto(Container c, String d, int maxll) {
	// Primitive line wrapping
	int len = d.length();
	if (len <= 0)
	    return;
	if (len > maxll) {
	    int p = d.lastIndexOf(' ', maxll);
	    if (p <= 0)
		p = d.indexOf(' ', maxll);
	    if (p > 0 && p < len) {
		burstStringInto(c, d.substring(0, p), maxll);
		burstStringInto(c, d.substring(p + 1), maxll);
		return;
	    }
	}
	JLabel label = new JLabel(d, JLabel.LEFT);
        configureMessageLabel(label);
	c.add(label);
    }

    protected Container createSeparator() {
        return null;
    }

    /**
     * Creates and returns a Container containing the buttons. The buttons
     * are created by calling <code>getButtons</code>.
     */
    protected Container createButtonArea() {
        JPanel bottom = new JPanel();
        bottom.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
	bottom.setLayout(new ButtonAreaLayout(true, 6));
	addButtonComponents(bottom, getButtons(), getInitialValueIndex());
        mnemonics = null;
	return bottom;
    }

    /**
     * Creates the appropriate object to represent each of the objects in
     * <code>buttons</code> and adds it to <code>container</code>. This
     * differs from addMessageComponents in that it will recurse on
     * <code>buttons</code> and that if button is not a Component
     * it will create an instance of JButton.
     */
    protected void addButtonComponents(Container container, Object[] buttons,
				 int initialIndex) {
	if (buttons != null && buttons.length > 0) {
	    boolean            sizeButtonsToSame = getSizeButtonsToSameWidth();
	    boolean            createdAll = true;
	    int                numButtons = buttons.length;
	    JButton[]          createdButtons = null;
	    int                maxWidth = 0;
            int[]              mnemonics = this.mnemonics;

            if (mnemonics != null && mnemonics.length != buttons.length) {
                mnemonics = null;
            }
	    if (sizeButtonsToSame) {
		createdButtons = new JButton[numButtons];
            }

	    for(int counter = 0; counter < numButtons; counter++) {
		Object       button = buttons[counter];
		Component    newComponent;

		if (button instanceof Component) {
		    createdAll = false;
		    newComponent = (Component)button;
		    container.add(newComponent);
		    hasCustomComponents = true;
		
		} else {
		    JButton      aButton;

		    if (button instanceof Icon)
			aButton = new JButton((Icon)button);
		    else
			aButton = new JButton(button.toString());

		    aButton.setMultiClickThreshhold(UIManager.getInt("OptionPane.buttonClickThreshhold"));
                    configureButton(aButton);

		    container.add(aButton);

                    ActionListener buttonListener = createButtonActionListener(counter);
                    if (buttonListener != null) {
                        aButton.addActionListener(buttonListener);
                    }
		    newComponent = aButton;
                    if (mnemonics != null) {
                        aButton.setMnemonic(mnemonics[counter]);
                    }
		}
		if (sizeButtonsToSame && createdAll && 
		   (newComponent instanceof JButton)) {
		    createdButtons[counter] = (JButton)newComponent;
		    maxWidth = Math.max(maxWidth,
					newComponent.getMinimumSize().width);
		}
		if (counter == initialIndex) {
		    initialFocusComponent = newComponent;
                    if (initialFocusComponent instanceof JButton) {
                        JButton defaultB = (JButton)initialFocusComponent;
                        defaultB.addAncestorListener(new AncestorListener() {
                           public void ancestorAdded(AncestorEvent e) { 
                               JButton defaultButton = (JButton)e.getComponent();
                               JRootPane root = SwingUtilities.getRootPane(defaultButton);
                               if (root != null) {
                                   root.setDefaultButton(defaultButton);
                               }
                           }
                           public void ancestorRemoved(AncestorEvent event) {}
                           public void ancestorMoved(AncestorEvent event) {}
                        });
                    }
		}
	    }
	    ((ButtonAreaLayout)container.getLayout()).
		              setSyncAllWidths((sizeButtonsToSame && createdAll));
	    /* Set the padding, windows seems to use 8 if <= 2 components,
	       otherwise 4 is used. It may actually just be the size of the
	       buttons is always the same, not sure. */
	    if (sizeButtonsToSame && createdAll) {
		JButton               aButton;
		int                   padSize;

		padSize = (numButtons <= 2? 8 : 4);

		for(int counter = 0; counter < numButtons; counter++) {
		    aButton = createdButtons[counter];
		    aButton.setMargin(new Insets(2, padSize, 2, padSize));
		}
	    }
	}
    }

    protected ActionListener createButtonActionListener(int buttonIndex) {
        return new ButtonActionListener(buttonIndex);
    }

    /**
     * Returns the buttons to display from the JOptionPane the receiver is
     * providing the look and feel for. If the JOptionPane has options
     * set, they will be provided, otherwise if the optionType is
     * YES_NO_OPTION, yesNoOptions is returned, if the type is
     * YES_NO_CANCEL_OPTION yesNoCancelOptions is returned, otherwise
     * defaultButtons are returned.
     */
    protected Object[] getButtons() {
	if (optionPane != null) {
	    Object[] suppliedOptions = optionPane.getOptions();

	    if (suppliedOptions == null) {
                Object[] defaultOptions;
		int type = optionPane.getOptionType();

                Locale l = optionPane.getLocale();
		if (type == JOptionPane.YES_NO_OPTION) {
                    defaultOptions = new String[2];
                    defaultOptions[0] = UIManager.get("OptionPane.yesButtonText",l);
                    defaultOptions[1] = UIManager.get("OptionPane.noButtonText",l);
                    mnemonics = new int[2];
                    mnemonics[0] = getMnemonic("OptionPane.yesButtonMnemonic", l);
                    mnemonics[1] = getMnemonic("OptionPane.noButtonMnemonic", l);

		} else if (type == JOptionPane.YES_NO_CANCEL_OPTION) {
                    defaultOptions = new String[3];
                    defaultOptions[0] = UIManager.get("OptionPane.yesButtonText",l);
                    defaultOptions[1] = UIManager.get("OptionPane.noButtonText",l);
                    defaultOptions[2] = UIManager.get("OptionPane.cancelButtonText",l);
                    mnemonics = new int[3];
                    mnemonics[0] = getMnemonic("OptionPane.yesButtonMnemonic", l);
                    mnemonics[1] = getMnemonic("OptionPane.noButtonMnemonic", l);
                    mnemonics[2] = getMnemonic("OptionPane.cancelButtonMnemonic", l);

		} else if (type == JOptionPane.OK_CANCEL_OPTION) {
                    defaultOptions = new String[2];
                    defaultOptions[0] = UIManager.get("OptionPane.okButtonText",l);
                    defaultOptions[1] = UIManager.get("OptionPane.cancelButtonText",l);
                    mnemonics = new int[2];
                    mnemonics[0] = getMnemonic("OptionPane.okButtonMnemonic", l);
                    mnemonics[1] = getMnemonic("OptionPane.cancelButtonMnemonic", l);

		} else {
                    defaultOptions = new String[1];
                    defaultOptions[0] = UIManager.get("OptionPane.okButtonText",l);
                    mnemonics = new int[1];
                    mnemonics[0] = getMnemonic("OptionPane.okButtonMnemonic", l);
                }
                return defaultOptions;
                
	    }
	    return suppliedOptions;
	}
	return null;
    }

    /**
     * Returns the mnemonic for the passed in key.
     */
    private int getMnemonic(String key, Locale l) {
        String value = (String)UIManager.get(key, l);

        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) { }
        return 0;
    }

    /**
     * Returns true, basic L&F wants all the buttons to have the same
     * width.
     */
    protected boolean getSizeButtonsToSameWidth() {
	return true;
    }

    /**
     * Returns the initial index into the buttons to select. The index
     * is calculated from the initial value from the JOptionPane and
     * options of the JOptionPane or 0.
     */
    protected int getInitialValueIndex() {
	if (optionPane != null) {
	    Object             iv = optionPane.getInitialValue();
	    Object[]           options = optionPane.getOptions();

	    if(options == null) {
		return 0;
	    }
	    else if(iv != null) {
		for(int counter = options.length - 1; counter >= 0; counter--){
		    if(options[counter].equals(iv))
			return counter;
		}
	    }
	}
	return -1;
    }

    /**
     * Sets the input value in the option pane the receiver is providing
     * the look and feel for based on the value in the inputComponent.
     */
    protected void resetInputValue() {
	if(inputComponent != null && (inputComponent instanceof JTextField)) {
	    optionPane.setInputValue(((JTextField)inputComponent).getText());

	} else if(inputComponent != null &&
                  (inputComponent instanceof JComboBox)) {
	    optionPane.setInputValue(((JComboBox)inputComponent)
				     .getSelectedItem());
	} else if(inputComponent != null) {
	    optionPane.setInputValue(((JList)inputComponent)
				     .getSelectedValue());
        }
    }


    /**
     * If inputComponent is non-null, the focus is requested on that,
     * otherwise request focus on the default value
     */
    public void selectInitialValue(JOptionPane op) {
	if (inputComponent != null)
	    inputComponent.requestFocus();
	else {
	    if (initialFocusComponent != null)
	        initialFocusComponent.requestFocus();

            if (initialFocusComponent instanceof JButton) {
                JRootPane root = SwingUtilities.getRootPane(initialFocusComponent);
                if (root != null) {
                    root.setDefaultButton((JButton)initialFocusComponent);
                }
            }
        }
    }

    /**
     * Returns true if in the last call to validateComponent the message
     * or buttons contained a subclass of Component.
     */
    public boolean containsCustomComponents(JOptionPane op) {
	return hasCustomComponents;
    }


    /**
     * <code>ButtonAreaLayout</code> behaves in a similar manner to
     * <code>FlowLayout</code>. It lays out all components from left to
     * right. If <code>syncAllWidths</code> is true, the widths of each
     * component will be set to the largest preferred size width.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    public static class ButtonAreaLayout implements LayoutManager {
	protected boolean           syncAllWidths;
	protected int               padding;
        /** If true, children are lumped together in parent. */
	protected boolean           centersChildren;

	public ButtonAreaLayout(boolean syncAllWidths, int padding) {
	    this.syncAllWidths = syncAllWidths;
	    this.padding = padding;
	    centersChildren = true;
	}

	public void setSyncAllWidths(boolean newValue) {
	    syncAllWidths = newValue;
	}

	public boolean getSyncAllWidths() {
	    return syncAllWidths;
	}

	public void setPadding(int newPadding) {
	    this.padding = newPadding;
	}

	public int getPadding() {
	    return padding;
	}

        public void setCentersChildren(boolean newValue) {
	    centersChildren = newValue;
	}

        public boolean getCentersChildren() {
	    return centersChildren;
	}

	public void addLayoutComponent(String string, Component comp) {
	}

	public void layoutContainer(Container container) {
	    Component[]      children = container.getComponents();

	    if(children != null && children.length > 0) {
		int               numChildren = children.length;
		Dimension[]       sizes = new Dimension[numChildren];
		Insets            insets = container.getInsets();
		int               counter;
		int               yLocation = insets.top;
                boolean           ltr = container.getComponentOrientation().isLeftToRight();

		if(syncAllWidths) {
		    int           maxWidth = 0;

		    for(counter = 0; counter < numChildren; counter++) {
			sizes[counter] = children[counter].getPreferredSize();
			maxWidth = Math.max(maxWidth, sizes[counter].width);
		    }

		    int      xLocation;
		    int      xOffset;

		    if(getCentersChildren()) {
			xLocation = (container.getSize().width - insets.left - insets.right -
					  (maxWidth * numChildren +
					   (numChildren - 1) * padding)) / 2;
			xOffset = padding + maxWidth;
		    }
		    else {
			if(numChildren > 1) {
			    xLocation = insets.left;
			    xOffset = (container.getSize().width - insets.left - insets.right -
				       (maxWidth * numChildren)) /
				(numChildren - 1) + maxWidth;
			}
			else {
			    xLocation = insets.left + 
			                (container.getSize().width - 
					 insets.left - insets.right -
					 maxWidth) / 2;
			    xOffset = 0;
			}
		    }

                    // If right to left layout then adjust xLocation and
                    // xOffset to start at the right side of the container
                    // and move left.
                    if( !ltr ) {
                        xLocation = container.getSize().width - insets.right
                                    - (xLocation - insets.left) - maxWidth;
                        xOffset = -xOffset;
                    }

		    for(counter = 0; counter < numChildren; counter++) {
			children[counter].setBounds(xLocation, yLocation,
						    maxWidth,
						    sizes[counter].height);
			xLocation += xOffset;
		    }
		}
		else {
		    int          totalWidth = 0;

		    for(counter = 0; counter < numChildren; counter++) {
			sizes[counter] = children[counter].getPreferredSize();
			totalWidth += sizes[counter].width;
		    }
		    totalWidth += ((numChildren - 1) * padding);

		    boolean      cc = getCentersChildren();
		    int          xOffset;
		    int          xLocation;

		    if(cc) {
			xLocation = insets.left + 
			             (container.getSize().width - insets.left - insets.right -
					      totalWidth) / 2;
			xOffset = padding;
		    }
		    else {
			if(numChildren > 1) {
			    xOffset = (container.getSize().width - insets.left - insets.right -
				       totalWidth) / (numChildren - 1);	
			    xLocation = insets.left;
			}
			else {
			    xLocation = insets.left + 
			                (container.getSize().width - insets.left - insets.right -
					 totalWidth) / 2;
			    xOffset = 0;
			}
		    }

                    if( ltr ) {
                        for(counter = 0; counter < numChildren; counter++) {
                            children[counter].setBounds(xLocation, yLocation,
                                                        sizes[counter].width, sizes[counter].height);
                            xLocation += xOffset + sizes[counter].width;
                        }
                    } else {
                        // If right to left layout then adjust xLocation to
                        // start at the right side of the container.
                        xLocation = container.getSize().width - insets.right
                                    - (xLocation - insets.left);
                        for(counter = 0; counter < numChildren; counter++) {
                            xLocation -= xOffset + sizes[counter].width;
                            children[counter].setBounds(xLocation, yLocation,
                                                        sizes[counter].width, sizes[counter].height);
                        }
                    }
		}
	    }
	}

	public Dimension minimumLayoutSize(Container c) {
	    if(c != null) {
		Component[]       children = c.getComponents();

		if(children != null && children.length > 0) {
		    Dimension     aSize;
		    int           numChildren = children.length;
		    int           height = 0;
		    Insets        cInsets = c.getInsets();
		    int           extraHeight = cInsets.top + cInsets.bottom;
		    int           extraWidth = cInsets.left + cInsets.right;

		    if (syncAllWidths) {
			int              maxWidth = 0;

			for(int counter = 0; counter < numChildren; counter++){
			    aSize = children[counter].getPreferredSize();
			    height = Math.max(height, aSize.height);
			    maxWidth = Math.max(maxWidth, aSize.width);
			}
			return new Dimension(extraWidth + (maxWidth * numChildren) + 
					     (numChildren - 1) * padding,
					     extraHeight + height);
		    }
		    else {
			int        totalWidth = 0;

			for(int counter = 0; counter < numChildren; counter++){
			    aSize = children[counter].getPreferredSize();
			    height = Math.max(height, aSize.height);
			    totalWidth += aSize.width;
			}
			totalWidth += ((numChildren - 1) * padding);
			return new Dimension(extraWidth + totalWidth, extraHeight + height);
		    }
		}
	    }
	    return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container c) {
	    return minimumLayoutSize(c);
	}

	public void removeLayoutComponent(Component c) { }
    }


    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    public class PropertyChangeHandler implements PropertyChangeListener {
        /**
         * If the source of the PropertyChangeEvent <code>e</code> equals the
         * optionPane and is one of the ICON_PROPERTY, MESSAGE_PROPERTY,
         * OPTIONS_PROPERTY or INITIAL_VALUE_PROPERTY,
         * validateComponent is invoked.
         */
        public void propertyChange(PropertyChangeEvent e) {
	    if(e.getSource() == optionPane) {
		// Option Pane Auditory Cue Activation
		// only respond to "ancestor" changes
		// the idea being that a JOptionPane gets a JDialog when it is 
		// set to appear and loses it's JDialog when it is dismissed.
		if ("ancestor" == e.getPropertyName()) {
		    JOptionPane op = (JOptionPane)e.getSource();
		    boolean isComingUp;
		    
		    // if the old value is null, then the JOptionPane is being
		    // created since it didn't previously have an ancestor.
		    if (e.getOldValue() == null) {
			isComingUp = true;
		    } else {
			isComingUp = false;
		    }
		    
		    // figure out what to do based on the message type
		    switch (op.getMessageType()) {
		    case JOptionPane.PLAIN_MESSAGE:
			if (isComingUp) {
			    fireAudioAction("OptionPane.informationSound");
			}
			break;
		    case JOptionPane.QUESTION_MESSAGE:
			if (isComingUp) {
			    fireAudioAction("OptionPane.questionSound");
			}
			break;
		    case JOptionPane.INFORMATION_MESSAGE:
			if (isComingUp) {
			    fireAudioAction("OptionPane.informationSound");
			}
			break;
		    case JOptionPane.WARNING_MESSAGE:
			if (isComingUp) {
			    fireAudioAction("OptionPane.warningSound");
			}
			break;
		    case JOptionPane.ERROR_MESSAGE:
			if (isComingUp) {
			    fireAudioAction("OptionPane.errorSound");
			}
			break;
		    default:
			System.err.println("Undefined JOptionPane type: " +
					   op.getMessageType());
			break;
		    }
		}
		// Visual activity
	        String         changeName = e.getPropertyName();

	        if(changeName.equals(JOptionPane.OPTIONS_PROPERTY) ||
    	           changeName.equals(JOptionPane.INITIAL_VALUE_PROPERTY) ||
	           changeName.equals(JOptionPane.ICON_PROPERTY) ||
	           changeName.equals(JOptionPane.MESSAGE_TYPE_PROPERTY) ||
	           changeName.equals(JOptionPane.OPTION_TYPE_PROPERTY) ||
	           changeName.equals(JOptionPane.MESSAGE_PROPERTY) ||
	           changeName.equals(JOptionPane.SELECTION_VALUES_PROPERTY) ||
	           changeName.equals(JOptionPane.INITIAL_SELECTION_VALUE_PROPERTY) ||
	           changeName.equals(JOptionPane.WANTS_INPUT_PROPERTY)) {
                   uninstallComponents();
                   installComponents();
                   optionPane.validate();
                }
		else if (changeName.equals("componentOrientation")) {
		    ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		    JOptionPane op = (JOptionPane)e.getSource();
		    if (o != (ComponentOrientation)e.getOldValue()) {
			op.applyComponentOrientation(o);
		    }
		}
            }
	}
    }

    /** 
     * Utility method which contains code to fire the auditory feedback
     * Actions.
     *
     * @since 1.4
     */
    private void fireAudioAction (String actionName) {
	ActionMap map = optionPane.getActionMap();
	if (map != null) {
	    Action audioAction = map.get(actionName);
	    if (audioAction != null) {
		// pass off firing the Action to a utility method
		BasicLookAndFeel lf = (BasicLookAndFeel)
		                       UIManager.getLookAndFeel();
		lf.playSound(audioAction);
	    }
	}
    }

    /**
     * Configures any necessary colors/fonts for the specified label
     * used representing the message.
     */
    private void configureMessageLabel(JLabel label) {
        label.setForeground(UIManager.getColor(
                            "OptionPane.messageForeground"));
        Font messageFont = UIManager.getFont("OptionPane.messageFont");
        if (messageFont != null) {
            label.setFont(messageFont);
        }
    }

    /**
     * Configures any necessary colors/fonts for the specified button
     * used representing the button portion of the optionpane.
     */
    private void configureButton(JButton button) {
        Font buttonFont = UIManager.getFont("OptionPane.buttonFont");
        if (buttonFont != null) {
            button.setFont(buttonFont);
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    public class ButtonActionListener implements ActionListener {
        protected int buttonIndex;

        public ButtonActionListener(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public void actionPerformed(ActionEvent e) {
	    if (optionPane != null) {
   	        int optionType = optionPane.getOptionType();
	        Object[] options = optionPane.getOptions();

                /* If the option pane takes input, then store the input value
                 * if custom options were specified, if the option type is
                 * DEFAULT_OPTION, OR if option type is set to a predefined
                 * one and the user chose the affirmative answer.
                 */
 		if (inputComponent != null) {
                    if (options != null ||
                        optionType == JOptionPane.DEFAULT_OPTION ||
      		        ((optionType == JOptionPane.YES_NO_OPTION ||
 		         optionType == JOptionPane.YES_NO_CANCEL_OPTION ||
 		         optionType == JOptionPane.OK_CANCEL_OPTION) &&
 		         buttonIndex == 0)) {
 		        resetInputValue();
                    }
                }
	        if (options == null) {
		    if (optionType == JOptionPane.OK_CANCEL_OPTION &&
                        buttonIndex == 1) {
		        optionPane.setValue(new Integer(2));
                    
		    } else {
		        optionPane.setValue(new Integer(buttonIndex));
                    }
	        } else {
		    optionPane.setValue(options[buttonIndex]);
                }
	    }
        }
    }


    //
    // Classed used when optionPane.getWantsInput returns true.
    //

    /**
     * Listener when a JList is created to handle input from the user.
     */
    private class ListSelectionListener extends MouseAdapter
    {
	public void mousePressed(MouseEvent e) {
	    if (e.getClickCount() == 2) {
		JList     list = (JList)e.getSource();
		int       index = list.locationToIndex(e.getPoint());

		optionPane.setInputValue(list.getModel().getElementAt(index));
	    }
	}
    }

    /**
     * Listener when a JTextField is created to handle input from the user.
     */
    private class TextFieldActionListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    optionPane.setInputValue(((JTextField)e.getSource()).getText());
	}
    }


    /**
     * A JTextField that allows you to specify an array of KeyStrokes that
     * that will have their bindings processed regardless of whether or
     * not they are registered on the JTextField. This is used as we really
     * want the ActionListener to be notified so that we can push the
     * change to the JOptionPane, but we also want additional bindings
     * (those of the JRootPane) to be processed as well.
     */
    private static class MultiplexingTextField extends JTextField {
        private KeyStroke[] strokes;

        MultiplexingTextField(int cols) {
            super(cols);
        }

        /**
         * Sets the KeyStrokes that will be additional processed for
         * ancestor bindings.
         */
        void setKeyStrokes(KeyStroke[] strokes) {
            this.strokes = strokes;
        }

        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                                            int condition, boolean pressed) {
            boolean processed = super.processKeyBinding(ks, e, condition,
                                                        pressed);

            if (processed && condition != JComponent.WHEN_IN_FOCUSED_WINDOW) {
                for (int counter = strokes.length - 1; counter >= 0;
                         counter--) {
                    if (strokes[counter].equals(ks)) {
                        // Returning false will allow further processing
                        // of the bindings, eg our parent Containers will get a
                        // crack at them.
                        return false;
                    }
                }
            }
            return processed;
        }
    }



    // REMIND(aim,7/29/98): These actions should be broken
    // out into protected inner classes in the next release where
    // API changes are allowed
    /**
     * Registered in the ActionMap. Sets the value of the option pane
     * to <code>JOptionPane.CLOSED_OPTION</code>.
     */
    private static class CloseAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JOptionPane optionPane = (JOptionPane)e.getSource();

	    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
	}
    }
}
