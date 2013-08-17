/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * Component decorator that implements the view interface 
 * for form elements, &lt;input&gt;, &lt;textarea&gt;,
 * and &lt;select&gt;.  The model for the component is stored 
 * as an attribute of the the element (using StyleConstants.ModelAttribute), 
 * and is used to build the component of the view.  The type
 * of the model is assumed to of the type that would be set by
 * <code>HTMLDocument.HTMLReader.FormAction</code>.  If there are
 * multiple views mapped over the document, they will share the 
 * embedded component models.
 * <p>
 * The following table shows what components get built
 * by this view.
 * <table>
 * <tr>
 *   <th>Element Type
 *   <th>Component built
 * <tr>
 *   <td>input, type button
 *   <td>JButton
 * <tr>
 *   <td>input, type checkbox
 *   <td>JCheckBox
 * <tr>
 *   <td>input, type image
 *   <td>JButton
 * <tr>
 *   <td>input, type password
 *   <td>JPasswordField
 * <tr>
 *   <td>input, type radio
 *   <td>JRadioButton
 * <tr>
 *   <td>input, type reset
 *   <td>JButton
 * <tr>
 *   <td>input, type submit
 *   <td>JButton
 * <tr>
 *   <td>input, type text
 *   <td>JTextField
 * <tr>
 *   <td>select, size &gt; 1 or multiple attribute defined
 *   <td>JList in a JScrollPane
 * <tr>
 *   <td>select, size unspecified or 1
 *   <td>JComboBox
 * <tr>
 *   <td>textarea
 *   <td>JTextArea in a JScrollPane
 * </table>
 *
 * @author Timothy Prinzing
 * @author Sunita Mani
 * @version 1.15 02/06/02
 */
public class FormView extends ComponentView implements ActionListener {

    /**
     * If a value attribute is not specified for a FORM input element
     * of type "submit", then this default string is used.
     *
     * @deprecated As of 1.3, value now comes from UIManager property
     *             FormView.submitButtonText
     */
    public static final String SUBMIT = new String("Submit Query");
    /**
     * If a value attribute is not specified for a FORM input element
     * of type "reset", then this default string is used.
     *
     * @deprecated As of 1.3, value comes from UIManager UIManager property
     *             FormView.resetButtonText
     */
    public static final String RESET = new String("Reset");

    /**
     * Creates a new FormView object.
     *
     * @param elem the element to decorate
     */
    public FormView(Element elem) {
	super(elem);
    }

    /**
     * Create the component.  This is basically a
     * big switch statement based upon the tag type
     * and html attributes of the associated element.
     */
    protected Component createComponent() {
	AttributeSet attr = getElement().getAttributes();
	HTML.Tag t = (HTML.Tag) 
	    attr.getAttribute(StyleConstants.NameAttribute);
	JComponent c = null;
	Object model = attr.getAttribute(StyleConstants.ModelAttribute);
	if (t == HTML.Tag.INPUT) {
	    c = createInputComponent(attr, model);
	} else if (t == HTML.Tag.SELECT) {

	    if (model instanceof OptionListModel) {

		JList list = new JList((ListModel) model);
		int size = HTML.getIntegerAttributeValue(attr,
							 HTML.Attribute.SIZE,
							 1);
		list.setVisibleRowCount(size);
		list.setSelectionModel((ListSelectionModel)model);
		c = new JScrollPane(list);
	    } else {
		c = new JComboBox((ComboBoxModel) model);
	    }
	} else if (t == HTML.Tag.TEXTAREA) {
	    JTextArea area = new JTextArea((Document) model);
	    int rows = HTML.getIntegerAttributeValue(attr,
						     HTML.Attribute.ROWS,
						     0);
	    area.setRows(rows);
	    int cols = HTML.getIntegerAttributeValue(attr,
						     HTML.Attribute.COLS,
						     0);
	    area.setColumns(cols);
	    c = new JScrollPane(area, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	if (c != null) {
	    c.setAlignmentY(1.0f);
	}
	return c;
    }


    /**
     * Creates a component for an &lt;INPUT&gt; element based on the
     * value of the "type" attribute.
     *
     * @param set of attributes associated with the &lt;INPUT&gt; element.
     * @param model the value of the StyleConstants.ModelAttribute
     * @return the component.
     */
    private JComponent createInputComponent(AttributeSet attr, Object model) {
	JComponent c = null;
	String type = (String) attr.getAttribute(HTML.Attribute.TYPE);

	if (type.equals("submit") || type.equals("reset")) {
	    String value = (String) 
		attr.getAttribute(HTML.Attribute.VALUE);
	    if (value == null) {
		if (type.equals("submit")) {
		    value = UIManager.getString("FormView.submitButtonText");
		} else {
		    value = UIManager.getString("FormView.resetButtonText");
		}
	    }
	    JButton button = new JButton(value);
	    if (model != null) {
		button.setModel((ButtonModel)model);
		button.addActionListener(this);
	    }
	    c = button;
	} else if (type.equals("image")) {
	    String srcAtt = (String) attr.getAttribute(HTML.Attribute.SRC);
	    JButton button;
	    try {
		URL base = ((HTMLDocument)getElement().getDocument()).getBase();
		URL srcURL = new URL(base, srcAtt);
		Icon icon = new ImageIcon(srcURL);
		button  = new JButton(icon);
	    } catch (MalformedURLException e) {
		button = new JButton(srcAtt);
	    }
	    if (model != null) {
		button.setModel((ButtonModel)model);
		button.addMouseListener(new MouseEventListener());
	    }
	    c = button;
	} else if (type.equals("checkbox")) {
	    c = new JCheckBox();
	    if (model != null) {
		boolean checked = (attr.getAttribute(HTML.Attribute.CHECKED) != null);
		((JToggleButton.ToggleButtonModel) model).setSelected(checked);
		((JCheckBox)c).setModel((JToggleButton.ToggleButtonModel)model);
	    }
	} else if (type.equals("radio")) {
	    c = new JRadioButton();
	    if (model != null) {
		boolean checked = (attr.getAttribute(HTML.Attribute.CHECKED) != null);
		((JToggleButton.ToggleButtonModel)model).setSelected(checked);
		((JRadioButton)c).setModel((JToggleButton.ToggleButtonModel)model);
	    }
	} else if (type.equals("text")) {
	    int size = HTML.getIntegerAttributeValue(attr,
						     HTML.Attribute.SIZE,
						     -1);
	    JTextField field;
	    if (size > 0) {
		// If the size is specified, we don't want to allow the
		// text field to be bigger than the preferred size.
		field = new JTextField() {
		    public Dimension getMaximumSize() {
			return getPreferredSize();
		    }
		};
		field.setColumns(size);
	    }
	    else {
		field = new JTextField();
	    }
	    c = field;
	    if (model != null) {
		field.setDocument((Document) model);
	    }
	    String value = (String) 
		attr.getAttribute(HTML.Attribute.VALUE);
	    if (value != null) {
		field.setText(value);
	    }
	    field.addActionListener(this);
	} else if (type.equals("password")) {
	    JPasswordField field = new JPasswordField();
	    c = field;
	    if (model != null) {
		field.setDocument((Document) model);
	    }
	    int size = HTML.getIntegerAttributeValue(attr,
						     HTML.Attribute.SIZE,
						     -1);
	    if (size > 0) {
		field.setColumns(size);
	    }
	    String value = (String) 
		attr.getAttribute(HTML.Attribute.VALUE);
	    if (value != null) {
		field.setText(value);
	    }
	    field.addActionListener(this);
	}
	return c;
    }



    /**
     * Responsible for processeing the ActionEvent.
     * If the element associated with the FormView,
     * has a type of "submit", "reset", "text" or "password" 
     * then the action is processed.  In the case of a "submit" 
     * the form is submitted.  In the case of a "reset"
     * the form is reset to its original state.
     * In the case of "text" or "password", if the 
     * element is the last one of type "text" or "password",
     * the form is submitted.  Otherwise, focus is transferred
     * to the next component in the form.
     *
     * @param evt the ActionEvent.
     */
    public void actionPerformed(ActionEvent evt) {
	Element element = getElement();
	StringBuffer dataBuffer = new StringBuffer();
	HTMLDocument doc = (HTMLDocument)getDocument();
	AttributeSet attr = element.getAttributes();
	
	String type = (String) attr.getAttribute(HTML.Attribute.TYPE);

	if (type.equals("submit")) { 
	    doc.getFormData(dataBuffer, element);
	    submitData(dataBuffer.toString());
	} else if (type.equals("reset")) {
	    doc.resetForm(element);
	} else if (type.equals("text") || type.equals("password")) {
	    if (doc.isLastTextOrPasswordField(element)) {
		doc.getFormData(dataBuffer, element);
		submitData(dataBuffer.toString());
	    } else {
		getComponent().transferFocus();
	    }
	}
    }


    /**
     * This method is responsible for submitting the form data.
     * A thread is forked to undertake the submission.
     */
    protected void submitData(String data) {
	//System.err.println("data ->"+data+"<-");
	SubmitThread dataThread = new SubmitThread(getElement(), data);
	dataThread.start();
    }


    /**
     * The SubmitThread is responsible for submitting the form.
     * It performs a POST or GET based on the value of method
     * attribute associated with  HTML.Tag.FORM.  In addition to
     * submitting, it is also responsible for display the 
     * results of the form submission.
     */
    class SubmitThread extends Thread {

	String data;
	HTMLDocument hdoc;
	HTMLDocument newDoc;
	AttributeSet formAttr;
	InputStream in;
	
	public SubmitThread(Element elem, String data) {
	    this.data = data;
	    hdoc = (HTMLDocument)elem.getDocument();
	    formAttr = hdoc.getFormAttributes(elem.getAttributes());
	}


	/**
	 * This method is responsible for extracting the
	 * method and action attributes associated with the
	 * &lt;FORM&gt; and using those to determine how (POST or GET)
	 * and where (URL) to submit the form.  If action is
	 * not specified, the base url of the existing document is
	 * used.  Also, if method is not specified, the default is
	 * GET.  Once form submission is done, run uses the
	 * SwingUtilities.invokeLater() method, to load the results
	 * of the form submission into the current JEditorPane.
	 */
	public void run() {

	    if (data.length() > 0) {

		String method = getMethod();
		String action = getAction();
		
		URL url;
		try {
		    URL actionURL;

		    /* if action is null use the base url and ensure that
		       the file name excludes any parameters that may be attached */
		    URL baseURL = hdoc.getBase();
		    if (action == null) {
			
			String file = baseURL.getFile();
			actionURL = new URL(baseURL.getProtocol(), 
					    baseURL.getHost(), 
					    baseURL.getPort(), 
					    file);
		    } else {
			actionURL = new URL(baseURL, action);
		    }

		    URLConnection connection;


		    if ("post".equals(method)) {
			url = actionURL;
			connection = url.openConnection();
			postData(connection, data);
		    } else {
			/* the default, GET */
			url = new URL(actionURL+"?"+data);
			connection = url.openConnection();
		    }
		    in = connection.getInputStream();

		    // safe assumption since we are in an html document
		    JEditorPane c = (JEditorPane)getContainer();
		    HTMLEditorKit kit = (HTMLEditorKit)c.getEditorKit();
		    newDoc = (HTMLDocument)kit.createDefaultDocument();
		    newDoc.putProperty(Document.StreamDescriptionProperty, url);

		    
		    Runnable callLoadDocument = new Runnable() {
			public void run() {
			    loadDocument();
			}
		    };
		    SwingUtilities.invokeLater(callLoadDocument);
		} catch (MalformedURLException m) {
		    // REMIND how do we deal with exceptions ??
		} catch (IOException e) {
		    // REMIND how do we deal with exceptions ??
		}
	    }
	}


	/**
	 * This method is responsible for loading the
	 * document into the FormView's container,
	 * which is a JEditorPane.
	 */
	public void loadDocument() {
	    JEditorPane c = (JEditorPane)getContainer();
	    try {
		c.read(in, newDoc);
	    } catch (IOException e) {
		// REMIND failed to load document
	    }
	}

	/**
	 * Get the value of the action attribute.
	 */
	public String getAction() {
	    if (formAttr == null) { 
		return null;
	    }
	    return (String)formAttr.getAttribute(HTML.Attribute.ACTION);
	}
	
	/**
	 * Get the form's method parameter.
	 */
	String getMethod() {
	    if (formAttr != null) {
		String method = (String)formAttr.getAttribute(HTML.Attribute.METHOD);
		if (method != null) {
		    return method.toLowerCase();
		}
	    }
	    return null;
	} 


	/**
	 * This method is responsible for writing out the form submission
	 * data when the method is POST.
	 * 
	 * @param connection to use.
	 * @param data to write.
	 */
	public void postData(URLConnection connection, String data) {
	    connection.setDoOutput(true);
 	    PrintWriter out = null;
	    try {
		out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		out.print(data);
		out.flush();
	    } catch (IOException e) {
		// REMIND: should do something reasonable!
	    } finally {
		if (out != null) {
		    out.close();
		}
	    }
	}
    }

    /**
     * MouseEventListener class to handle form submissions when
     * an input with type equal to image is clicked on.
     * A MouseListener is necessary since along with the image
     * data the coordinates associated with the mouse click
     * need to be submitted.
     */
    protected class MouseEventListener extends MouseAdapter {

	public void mouseReleased(MouseEvent evt) {
	    String imageData = getImageData(evt.getPoint());
	    imageSubmit(imageData);
	}
    }

    /**
     * This method is called to submit a form in response
     * to a click on an image -- an &lt;INPUT&gt; form
     * element of type "image".
     *
     * @param the mouse click coordinates.
     */
    protected void imageSubmit(String imageData) {

	StringBuffer dataBuffer = new StringBuffer();
	Element elem = getElement();
	HTMLDocument hdoc = (HTMLDocument)elem.getDocument();
	hdoc.getFormData(dataBuffer, getElement());
	if (dataBuffer.length() > 0) {
	    dataBuffer.append('&');
	}
	dataBuffer.append(imageData);
	submitData(dataBuffer.toString());
	return;
    }

    /**
     * Extracts the value of the name attribute
     * associated with the input element of type
     * image.  If name is defined it is encoded using
     * the URLEncoder.encode() method and the 
     * image data is returned in the following format:
     *	    name + ".x" +"="+ x +"&"+ name +".y"+"="+ y
     * otherwise,
     * 	    "x="+ x +"&y="+ y
     * 
     * @param point associated with the mouse click.
     * @return the image data.
     */
    private String getImageData(Point point) {
	    
	String mouseCoords = point.x + ":" + point.y;
	int sep = mouseCoords.indexOf(':');
	String x = mouseCoords.substring(0, sep);
	String y = mouseCoords.substring(++sep);
	String name = (String) getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
	
	String data;
	if (name == null || name.equals("")) {
	    data = "x="+ x +"&y="+ y;
	} else {
	    name = URLEncoder.encode(name);
	    data = name + ".x" +"="+ x +"&"+ name +".y"+"="+ y;
	}
	return data;
    }
}

