/*
 * @(#)DemoImages.java	1.18 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)DemoImages.java	1.18 04/07/26
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
        "java-logo.gif", "bld.jpg", "boat.png", "box.gif",
        "boxwave.gif", "clouds.jpg", "duke.gif", "duke.running.gif",
        "dukeplug.gif", "fight.gif", "globe.gif", "java_logo.png",
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
