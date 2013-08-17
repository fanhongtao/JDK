/*
 * @(#)DemoSurface.java	1.31 98/09/22
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
import java.awt.print.*;
import javax.swing.JPanel;


/**
 * All demos extend this DemoSurface Abstract class.  From
 * this class demos must implement the drawDemo method.  This
 * class handles animated demos, the demo must implement the
 * AnimatingContext interface.
 */
public abstract class DemoSurface extends JPanel implements Runnable, Printable {

    public Object AntiAlias = RenderingHints.VALUE_ANTIALIAS_ON;
    public Object Rendering = RenderingHints.VALUE_RENDER_SPEED;
    public Paint texture;
    public AlphaComposite composite;
    public String perfStr;            // PerformanceMonitor
    public BufferedImage bimg;
    public int imageType;
    public Thread thread;
    public String name;         
    public boolean observerRunning;
    public boolean clearSurface = true;

    protected long sleepAmount;

    private long orig, start, frame;
    private Toolkit toolkit;
    private boolean perfMonitor, outputPerf;
    private int biw, bih;
    private AnimatingContext animating;


    public DemoSurface() {
        setDoubleBuffered(false);
        setBackground(Color.white);
        toolkit = getToolkit();
        name = this.getClass().getName();
        name = name.substring(name.indexOf(".", 7)+1);
        setImageType(0);

        // To launch an individual demo with the performance str output  :
        //    java -Djava2demo.perf= demos.Clipping.ClipAnim
        try {
            if (System.getProperty("java2demo.perf") != null) {
                perfMonitor = outputPerf = true;
            }
        } catch (Exception ex) { }
    }


    protected Image getImage(String fileName) {
        if (DemoImages.cache == null) {
            return DemoImages.createImage(fileName, this);
        } else {
            return (Image) DemoImages.cache.get(fileName);
        }
    }


    protected boolean containsImage(String imgName) {
        if (DemoImages.cache == null) {
            return false;
        } else {
            return DemoImages.cache.containsKey(imgName);
        }
    }


    protected Image getCroppedImage(Image img, int x, int y, int w, int h) {
        return DemoImages.getCroppedImage(img, x, y, w, h, this);
    }


    public void setImageType(int imgType) {
        if (imgType == 0) {
            if (this instanceof AnimatingContext || observerRunning) {
                imageType = 2;
            } else {
                imageType = 1;
            }
        } else {
            imageType = imgType;
        }
        bimg = null;
    }


    public void setAntiAlias(boolean aa) {
        AntiAlias = aa 
            ? RenderingHints.VALUE_ANTIALIAS_ON
            : RenderingHints.VALUE_ANTIALIAS_OFF;
    }


    public void setRendering(boolean rd) {
        Rendering = rd
            ? RenderingHints.VALUE_RENDER_QUALITY
            : RenderingHints.VALUE_RENDER_SPEED;
    }


    public void setTexture(Object obj) {
        if (obj instanceof GradientPaint) {
            texture = new GradientPaint(0, 0, Color.white,
                                        getSize().width*2, 0, Color.green);
        } else {
            texture = (Paint) obj;
        }
    }


    public void setComposite(boolean cp) {
        composite = cp 
            ? AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f) 
            : null;
    }


    public void setMonitor(boolean pm) {
        perfMonitor = pm;
    }


    public BufferedImage createBufferedImage(int w, int h, int imgType) {
        BufferedImage bi = null;
        if (imgType == 0) {
            bi = (BufferedImage) createImage(w, h);  
        } else {
            bi = new BufferedImage(w, h, imgType);
        }
        biw = w;
        bih = h;
        return bi;
    }


    public Graphics2D createGraphics2D(int width, 
                                       int height, 
                                       BufferedImage bi, 
                                       Graphics g) {

        Graphics2D g2 = null;

        if (bi != null) {
            g2 = bi.createGraphics();
        } else {
            g2 = (Graphics2D) g;
        }

        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, Rendering);

        if (clearSurface) {
            g2.clearRect(0, 0, width, height);
        }

        if (texture != null) {
            // set composite to opaque for texture fills
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(texture);
            g2.fillRect(0, 0, width, height);
        }

        if (composite != null) {
            g2.setComposite(composite);
        }

        return g2;
    }

    // ...demos that extend DemoSurface must implement this routine...
    public abstract void drawDemo(int w, int h, Graphics2D g2);


    public void paint(Graphics g) {

        Dimension d = getSize();

        if (imageType == 1) {
            bimg = null;
            startClock();
        } else if (bimg == null || biw != d.width || bih != d.height) {
            bimg = createBufferedImage(d.width, d.height, imageType-2);
            if (this instanceof AnimatingContext) {
                ((AnimatingContext) this).reset(d.width, d.height);
            }
            startClock();
        }

        if (thread != null) {
            animating.step(d.width, d.height);
        } 

        Graphics2D g2 = createGraphics2D(d.width, d.height, bimg, g);
        drawDemo(d.width, d.height, g2);
        g2.dispose();

        if (bimg != null)  {
            g.drawImage(bimg, 0, 0, null);
            toolkit.sync();
        }

        if (perfMonitor) {
            LogPerformance();
        }
    }


    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g2d.translate(pf.getImageableWidth() / 2,
                      pf.getImageableHeight() / 2);
        
        Dimension d = getSize();

        double scale = Math.min(pf.getImageableWidth() / d.width,
                                pf.getImageableHeight() / d.height);
        if (scale < 1.0) {
            g2d.scale(scale, scale);
        }

        g2d.translate(-d.width / 2.0, -d.height / 2.0);

        if (bimg == null) {
            Graphics2D g2 = createGraphics2D(d.width, d.height, null, g2d);
            drawDemo(d.width, d.height, g2);
            g2.dispose();
        } else {
            g2d.drawImage(bimg, 0, 0, this);
        }

        return Printable.PAGE_EXISTS;
    }


    public void start() {
        if (thread != null || !(this instanceof AnimatingContext)) {
            return;
        }
        animating = (AnimatingContext) this;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName(name + "Demo");
        thread.start();
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


    private void startClock() {
        orig = System.currentTimeMillis();
        start = orig;
    }

    private static final int REPORTFRAMES = 30;

    private void LogPerformance() {
        if ((frame % REPORTFRAMES) == 0) {
            long end = System.currentTimeMillis();
            long rel = (end - start);
            long tot = (end - orig);
            if (frame == 0) {
                perfStr = name + " " + rel+" ms";
                if (imageType == 1) { 
                    frame = -1;         // reset for on-screen
                }
            } else {
                String s1 = Float.toString((REPORTFRAMES/(rel/1000.0f)));
                s1 = (s1.length() < 4) ? s1.substring(0,s1.length()) : s1.substring(0,4);
                perfStr = name + " " + s1 + " fps";
            }
            if (outputPerf) {
                System.out.println(perfStr);
            }
            start = end;
        }
        ++frame;
    }



    // System.out graphics state information.
    public void verbose() {
        String str = name + " ";
        if (thread != null) {
            str = str.concat(" Running");
        } else if (this instanceof AnimatingContext) {
            str = str.concat(" Stopped");
        }

        String s = "On Screen";
        if (bimg != null) {
            switch (bimg.getType()) {
                case bimg.TYPE_INT_RGB : s="INT_RGB"; break;
                case bimg.TYPE_INT_ARGB : s="INT_ARGB"; break;
                case bimg.TYPE_INT_ARGB_PRE : s="TYPE_INT_ARGB_PRE"; break;
                case bimg.TYPE_INT_BGR : s="TYPE_INT_BGR"; break;
                case bimg.TYPE_3BYTE_BGR : s="TYPE_3BYTE_BGR"; break;
                case bimg.TYPE_4BYTE_ABGR : s="TYPE_4BYTE_ABGR"; break;
                case bimg.TYPE_4BYTE_ABGR_PRE : s="TYPE_4BYTE_ABGR_PRE"; break;
                case bimg.TYPE_USHORT_565_RGB : s="TYPE_USHORT_565_RGB"; break;
                case bimg.TYPE_USHORT_555_RGB : s="USHORT_555_RGB"; break;
                case bimg.TYPE_BYTE_GRAY : s="BYTE_GRAY"; break;
                case bimg.TYPE_USHORT_GRAY : s="USHORT_GRAY"; break;
                case bimg.TYPE_BYTE_BINARY : s="BYTE_BINARY"; break;
                case bimg.TYPE_BYTE_INDEXED : s="BYTE_INDEXED"; break;
            }
        }
        str = str.concat(" " + s);

        str = AntiAlias == RenderingHints.VALUE_ANTIALIAS_ON
            ? str.concat(" ANTIALIAS_ON ") 
            : str.concat(" ANTIALIAS_OFF ");

        str = Rendering == RenderingHints.VALUE_RENDER_QUALITY
            ? str.concat("RENDER_QUALITY ") 
            : str.concat("RENDER_SPEED ");

        if (texture != null) {
            str = str.concat("Texture ");
        }

        if (composite != null) {
            str = str.concat("Composite=" + composite.getAlpha() + " ");
        }

        Runtime r = Runtime.getRuntime();
        r.gc();
        float freeMemory = (float) r.freeMemory();
        float totalMemory = (float) r.totalMemory();
        str = str.concat(((totalMemory - freeMemory)/1024) + "K used");
        System.out.println(str);
    }
}
