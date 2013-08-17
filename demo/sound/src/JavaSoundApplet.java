/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.JApplet;

/**
 * A demo that shows JavaSound features. 
 *
 * Parameters that can be used in the JavaSound.html file inside
 * the applet tag to customize demo runs :
 *            <param name="dir" value="audioDirectory">
 *
 * @(#)JavaSoundApplet.java	1.4	02/02/06
 * @author Brian Lichtenwalter 
 */
public class JavaSoundApplet extends JApplet {

    static JavaSoundApplet applet;
    private JavaSound demo;

    public void init() {
        applet = this;
        String media = "media";
        String param = null;
        if ((param = getParameter("dir")) != null) {
            media = param;
        } 
        getContentPane().add("Center", demo = new JavaSound(media));
    }

    public void start() {
        demo.open();
    }

    public void stop() {
        demo.close();
    }
}
