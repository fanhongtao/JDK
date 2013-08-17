/*
 * @(#)AnimatingSurface.java	1.1	99/09/07
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


/**
 * Demos that animate extend this class.
 */
public abstract class AnimatingSurface extends Surface implements Runnable {

    public Thread thread;

    public abstract void step(int w, int h);

    public abstract void reset(int newwidth, int newheight);


    public void start() {
        if (thread == null && !dontThread) {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName(name + " Demo");
            thread.start();
        }
    }


    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
        notifyAll();
    }


    public void run() {

        Thread me = Thread.currentThread();

        while (thread == me && !isShowing() || getSize().width == 0) {
            try {
                thread.sleep(200);
            } catch (InterruptedException e) { }
        }

        while (thread == me) {
            repaint();
            try {
                thread.sleep(sleepAmount);
            } catch (InterruptedException e) { }
        }
        thread = null;
    }
}
