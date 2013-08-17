/*
 * @(#)DemoImages.java	1.3 98/09/13
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import java.io.File;
import java.util.Hashtable;


/**
 * A cache of all the demo images found in the images directory.
 */
public class DemoImages extends Component {

    public static Hashtable cache;

    public DemoImages() {
        URL url = DemoImages.class.getResource("images");
        File dir = new File(url.getFile());
        if (dir != null && dir.isDirectory()) {
            String list[] = dir.list();
            cache = new Hashtable(list.length);
            for (int i = 0; i < list.length; i++) {
                cache.put(list[i], createImage(list[i], this));
            }
        }
        if (cache.containsKey("jumptojavastrip.gif")) {
            Image img = (Image) cache.get("jumptojavastrip.gif");
            for (int i=0, x=0; i < 10; i++, x+=80) {
                String s = "jumptojavastrip-" + String.valueOf(i) + ".gif";
                cache.put(s, getCroppedImage(img, x, 0, 80, 80, this));
            }
        }
    }


    public static Image createImage(String fileName, Component cmp) {
        URL url = DemoImages.class.getResource("images/" + fileName);
        Image img = cmp.getToolkit().createImage(url);
        trackImage(img, cmp);
        return img;
    }


    public static Image getCroppedImage(Image img, 
                                        int x, int y, 
                                        int w, int h, 
                                        Component cmp) {
        ImageProducer imgP = img.getSource();
        CropImageFilter cif = new CropImageFilter(x, y, w, h);
        ImageProducer ip = new FilteredImageSource(imgP, cif);
        Image croppedimage = cmp.getToolkit().createImage(ip);
        trackImage(croppedimage, cmp);
        return croppedimage;
    }

       
    private static void trackImage(Image img, Component cmp) {
        MediaTracker tracker = new MediaTracker(cmp);
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
            if (tracker.isErrorAny()) {
                System.out.println("Error loading image");
            }
        } catch (Exception ex) { ex.printStackTrace(); } 
    }
}
