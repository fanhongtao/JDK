/*
 * @(#)DemoImages.java	1.12 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java2d;

import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * A cache of all the demo images found in the images directory.
 */
public class DemoImages extends Component {

    private String[] names = 
    { 
        "HotJava-16.gif", "bld.jpg", "boat.png", "box.gif",
        "boxwave.gif", "clouds.jpg", "duke.gif", "duke.running.gif",
        "dukeplug.gif", "fight.gif", "globe.gif", "java_logo.gif",
        "jumptojavastrip.png", "magnify.gif", "painting.gif", 
        "remove.gif", "snooze.gif", "star7.gif", "surfing.gif",
        "thumbsup.gif", "tip.gif", "duke.png", "print.gif", 
        "loop.gif", "looping.gif", "start.gif", "start2.gif",
        "stop.gif", "stop2.gif", "clone.gif"
    };
    private static Hashtable cache;


    public DemoImages() {
        cache = new Hashtable(names.length);
        for (int i = 0; i < names.length; i++) {
            cache.put(names[i], getImage(names[i], this));
        }
    }


    public static Image getImage(String name, Component cmp) {
        Image img = null;
        if (cache != null) {
            if ((img = (Image) cache.get(name)) != null) {
                return img;
            }
        }

	URLClassLoader urlLoader = (URLClassLoader)cmp.getClass().getClassLoader();
	URL fileLoc = urlLoader.findResource("images/" + name);
	img = cmp.getToolkit().createImage(fileLoc);

        MediaTracker tracker = new MediaTracker(cmp);
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
            if (tracker.isErrorAny()) {
                System.out.println("Error loading image " + name);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return img;
    }
}
