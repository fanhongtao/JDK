/*
 * @(#)DemoImages.java	1.8 99/06/22
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.net.URL;
import java.net.MalformedURLException;


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
        String dir = "images/";
        if (Java2DemoApplet.applet != null) {
            try {
                URL url = new URL(Java2DemoApplet.applet.getCodeBase(),dir+name);
                img = cmp.getToolkit().createImage(url);
            } catch (MalformedURLException ex) { 
                ex.printStackTrace(); 
                return null;
            }
        } else {
            img = cmp.getToolkit().createImage(dir + name);
        }
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
