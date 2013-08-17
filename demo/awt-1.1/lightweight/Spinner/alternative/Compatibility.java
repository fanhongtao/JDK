package alternative;

import java.applet.*;
import java.awt.*;

public class Compatibility extends Applet {
    /* Should localize the following. */
    protected String labelText = "Your browser can't run 1.1 applets.";
    protected String filename;   // value to be provided by a subclass
    PictureFrame frame;

    public void init() {
	setLayout(new BorderLayout());

	Button button = new Button("Click here!");
	add("Center", button);

	Label label = new Label(labelText);
	label.setForeground(Color.red);
	add("North", label);

	System.out.println("Image filename is " + filename);

	if (filename == null) {
	    label.disable();
	    button.disable();
	    return;
	} 
	//needed because this is running under Switcher
	Applet parentApplet;
	
	/* Get the parent Applet object. */
	try {
	  parentApplet = (Applet)getParent();
	} catch (ClassCastException e) {
	  System.err.println("Parent isn't an Applet!");
	  throw(e);
	}
        
	Image image = parentApplet.getImage(parentApplet.getCodeBase(), filename);
	String gifWidth = parentApplet.getParameter("GIFWIDTH");
	String gifHeight = parentApplet.getParameter("GIFHEIGHT");
	int w = 200;
	int h = 200;

	if (gifWidth != null) {
	    try {
		w = Integer.parseInt(gifWidth);
	    } catch (NumberFormatException e) {
		//Use default width.
	    }
	}

	if (gifHeight != null) {
	    try {
		h = Integer.parseInt(gifHeight);
	    } catch (NumberFormatException e) {
		//Use default height.
	    }
	}

	frame = new PictureFrame(image, w, h);
    }

    public void stop() {
	frame.hide();
    }

    public boolean action(Event e, Object arg) {
	if (frame != null) {
	    frame.pack();
	    frame.show();
	}
	return true;
    }
}
