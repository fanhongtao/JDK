/*
 * @(#)HiddenTagView.java	1.5 98/08/26
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
package javax.swing.text.html;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * HiddenTagView subclasses EditableView to contain a JTextField showing
 * the element name. When the textfield is edited the element name is
 * reset. As this inherits from EditableView if the JTextComponent is
 * not editable, the textfield will not be visible.
 *
 * @author  Scott Violet
 * @version 1.5, 08/26/98
 */
class HiddenTagView extends EditableView implements DocumentListener {
    HiddenTagView(Element e) {
	super(e);
	yAlign = 1;
    }

    protected Component createComponent() {
	JTextField tf = new JTextField(getElement().getName());
	Document doc = getDocument();
	Font font;
	if (doc instanceof StyledDocument) {
	    font = ((StyledDocument)doc).getFont(getAttributes());
	    tf.setFont(font);
	}
	else {
	    font = tf.getFont();
	}
	tf.getDocument().addDocumentListener(this);
	updateYAlign(font);

	// Create a panel to wrap the textfield so that the textfields
	// laf border shows through.
	JPanel panel = new JPanel(new BorderLayout());
	panel.setBackground(null);
	if (isEndTag()) {
	    panel.setBorder(EndBorder);
	}
	else {
	    panel.setBorder(StartBorder);
	}
	panel.add(tf);
	return panel;
    }

    public float getAlignment(int axis) {
	if (axis == View.Y_AXIS) {
	    return yAlign;
	}
	return 0.5f;
    }

    public float getMinimumSpan(int axis) {
	if (axis == View.X_AXIS && isVisible()) {
	    // Default to preferred.
	    return Math.max(30, super.getPreferredSpan(axis));
	}
	return super.getMinimumSpan(axis);
    }

    public float getPreferredSpan(int axis) {
	if (axis == View.X_AXIS && isVisible()) {
	    return Math.max(30, super.getPreferredSpan(axis));
	}
	return super.getPreferredSpan(axis);
    }

    public float getMaximumSpan(int axis) {
	if (axis == View.X_AXIS && isVisible()) {
	    // Default to preferred.
	    return Math.max(30, super.getMaximumSpan(axis));
	}
	return super.getMaximumSpan(axis);
    }

    // DocumentListener methods
    public void insertUpdate(DocumentEvent e) {
	pushTextToModel();
    }

    public void removeUpdate(DocumentEvent e) {
	pushTextToModel();
    }

    public void changedUpdate(DocumentEvent e) {
	pushTextToModel();
    }

    // View method
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	if (!isSettingAttributes) {
	    isSettingAttributes = true;
	    try {
		getTextComponent().setText(getRepresentedText());
		resetBorder();
	    }
	    finally {
		isSettingAttributes = false;
	    }
	    preferenceChanged(this, true, true);
	    getContainer().repaint();
	}
    }

    // local methods

    void updateYAlign(Font font) {
	FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
	float h = fm.getHeight();
	float d = fm.getDescent();
	float yAlign = (h - d) / h;
    }

    void resetBorder() {
	if (isEndTag()) {
	    ((JPanel)getComponent()).setBorder(EndBorder);
	}
	else {
	    ((JPanel)getComponent()).setBorder(StartBorder);
	}
    }

    void pushTextToModel() {
	if (!isSettingAttributes) {
	    Object name = getElement().getAttributes().getAttribute
		(StyleConstants.NameAttribute);
	    Document doc = getDocument();
	    if ((name instanceof HTML.UnknownTag) &&
		(doc instanceof StyledDocument)) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		String text = getTextComponent().getText();
		isSettingAttributes = true;
		try {
		    sas.addAttribute(StyleConstants.NameAttribute,
				     new HTML.UnknownTag(text));
		    ((StyledDocument)doc).setCharacterAttributes
			(getStartOffset(), getEndOffset() -
			 getStartOffset(), sas, false);
		}
		finally {
		    isSettingAttributes = false;
		}
	    }
	}
    }

    JTextComponent getTextComponent() {
	return (JTextComponent)((Container)getComponent()).getComponent(0);
    }

    String getRepresentedText() {
	String retValue = getElement().getName();
	return (retValue == null) ? "" : retValue;
    }

    boolean isEndTag() {
	AttributeSet as = getElement().getAttributes();
	if (as != null) {
	    Object end = as.getAttribute(HTML.Attribute.ENDTAG);
	    if (end != null && (end instanceof String) &&
		((String)end).equals("true")) {
		return true;
	    }
	}
	return false;
    }

    /** Alignment along the y axis, based on the font of the textfield. */
    float yAlign;
    /** Set to true when setting attributes. */
    boolean isSettingAttributes;


    // Following are for Borders that used for Unknown tags and comments.
    //
    // Border defines
    static final int circleR = 3;
    static final int circleD = circleR * 2;
    static final int tagSize = 6;
    static final int padding = 3;
    static final Color UnknownTagBorderColor = Color.black;
    static final Border StartBorder = new StartTagBorder();
    static final Border EndBorder = new EndTagBorder();


    static class StartTagBorder implements Border, Serializable {
	public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
	    g.setColor(UnknownTagBorderColor);
	    x += padding;
	    width -= (padding * 2);
	    g.drawLine(x, y + circleR,
		       x, y + height - circleR);
	    g.drawArc(x, y + height - circleD - 1,
		      circleD, circleD, 180, 90);
	    g.drawArc(x, y, circleD, circleD, 90, 90);
	    g.drawLine(x + circleR, y, x + width - tagSize, y);
	    g.drawLine(x + circleR, y + height - 1,
		       x + width - tagSize, y + height - 1);
	    
	    g.drawLine(x + width - tagSize, y,
		       x + width - 1, y + height / 2);
	    g.drawLine(x + width - tagSize, y + height,
		       x + width - 1, y + height / 2);
	}

	public Insets getBorderInsets(Component c) {
	    return new Insets(2, 2 + padding, 2, tagSize + 2 + padding);
	}

	public boolean isBorderOpaque() {
	    return false;
	}
    } // End of class HiddenTagView.StartTagBorder


    static class EndTagBorder implements Border, Serializable {
	public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
	    g.setColor(UnknownTagBorderColor);
	    x += padding;
	    width -= (padding * 2);
	    g.drawLine(x + width - 1, y + circleR,
		       x + width - 1, y + height - circleR);
	    g.drawArc(x + width - circleD - 1, y + height - circleD - 1,
		      circleD, circleD, 270, 90);
	    g.drawArc(x + width - circleD - 1, y, circleD, circleD, 0, 90);
	    g.drawLine(x + tagSize, y, x + width - circleR, y);
	    g.drawLine(x + tagSize, y + height - 1,
		       x + width - circleR, y + height - 1);
	    
	    g.drawLine(x + tagSize, y,
		       x, y + height / 2);
	    g.drawLine(x + tagSize, y + height,
		       x, y + height / 2);
	}

	public Insets getBorderInsets(Component c) {
	    return new Insets(2, tagSize + 2 + padding, 2, 2 + padding);
	}

	public boolean isBorderOpaque() {
	    return false;
	}
    } // End of class HiddenTagView.EndTagBorder


} // End of HiddenTagView
