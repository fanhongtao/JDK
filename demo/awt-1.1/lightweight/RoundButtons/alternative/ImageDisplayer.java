package alternative;

import java.awt.*;
import java.applet.Applet;

public class ImageDisplayer extends Canvas {
    Image image;
    Dimension size;
    int w, h;

    public ImageDisplayer(Image image, int width, int height) {
	if (image == null) {
	    System.err.println("Canvas got invalid image object!");
	    return;
	}

	this.image = image;

	w = width;
	h = height;

	size = new Dimension(w,h);
    }

    public Dimension preferredSize() {
 	return size;
    }

    public synchronized Dimension minimumSize() {
	return size;
    }

    public void paint (Graphics g) {
	if (image != null) {
	    g.drawImage(image, 0, 0, this);
	    g.drawRect(0, 0, w - 1, h - 1);
	}
    }
}

