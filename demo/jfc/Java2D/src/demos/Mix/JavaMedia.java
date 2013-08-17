/*
 * @(#)JavaMedia.java	1.22 99/09/07
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

package demos.Mix;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Hashtable;
import java.util.Enumeration;
import CustomControls;
import CustomControlsContext;
import Java2DemoApplet;
import DemoImages;

/* UNCOMMENT TO USE JMF
import javax.media.*;
import com.sun.media.*;
END UNCOMMENT*/


/**
   JavaMedia example to show effects of sound and jmf with 2D animation.

   Java Media Framework 1.02 extension or later required.
   http://java.sun.com/products/java-media/jmf/index.html

   Running requires editing this file to comment out some source and uncomment 
   the JMF specific source.
       Look for the UNCOMMENT TO USE JMF text.
       Look for the REMOVE TO USE JMF text

   For media use the Sample1.mov & Sample2.mpg in the JMF/samples/media 
   directory and then copy the two sample files to the Java2D/media directory.

   Setup to use JMF.

   After editing this file :

       Extract the contents of Java2Demo.jar, run this command from the 
       Java2D directory :

           jar xvf Java2Demo.jar

       For win32 :

           javac src\demos\Mix\JavaMedia.java -d .

       For solaris :

           javac src/demos/Mix/JavaMedia.java -d .

   Run without the Java2Demo.jar JarFile context :

           java demos.Mix.JavaMedia
           java DemoGroup Mix
           java Java2Demo 
*/
public class JavaMedia extends CustomControls implements ChangeListener, CustomControlsContext {

    ImageIcon stopIcon, startIcon, loopIcon, loopingIcon;
    String playType[] = {"mix1", "mix2", "rmf"};
    String playList[][] = {
                {"sfx-medley.rmf", "trippygaia1.mid", "spacemusic.au"},
                {"bong.wav", "bark.aiff", "trance.rmf"},
                {"ambient.rmf", "classical.rmf", "modern-rock.rmf"}};
    JavaSound jsound[] = new JavaSound[9];

    private Font f1 = new Font("serif", Font.BOLD + Font.ITALIC, 18);
    private Hashtable jmftable = new Hashtable(2);
    private JTabbedPane tabbedPane;


    public JavaMedia() {
        super("JavaMedia");
        setLayout(new BorderLayout());
        setBackground(Color.gray);

        stopIcon = new ImageIcon(DemoImages.getImage("stop2.gif",this));
        startIcon = new ImageIcon(DemoImages.getImage("start2.gif",this));
        loopIcon = new ImageIcon(DemoImages.getImage("loop.gif",this));
        loopingIcon = new ImageIcon(DemoImages.getImage("looping.gif",this));

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new BevelBorder(BevelBorder.RAISED));
        tabbedPane.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        Color c[] = {Color.cyan, Color.yellow, Color.pink};

        for (int i = 0, k = 0; i < playType.length; i++) {
            JPanel jp = new JPanel(new GridLayout(0,1));
            for (int j = 0; j < playList[i].length; j++) {
                jp.add(jsound[k++] = new JavaSound(playList[i][j],c[j%3]));
            }
            tabbedPane.addTab(playType[i], jp);
        }
        JMF jmf = new JMF("Sample1.mov");
        jmftable.put("mov", jmf);
        tabbedPane.addTab("mov", jmf);
        jmftable.put("mpg", jmf = new JMF("Sample2.mpg"));
        tabbedPane.addTab("mpg", jmf);
        tabbedPane.addChangeListener(this);

        add(tabbedPane);


        JPanel jp1 = new JPanel(new GridLayout(0,1));
        String s1[] = { "J", "A", "V", "A" };
        for (int i = 0; i < s1.length; i++) {
            jp1.add(addLabel(s1[i]));
        }
        add("West", jp1);

        JPanel jp2 = new JPanel(new GridLayout(0,1));
        String s2[] = { "M", "E", "D", "I", "A" };
        for (int i = 0; i < s2.length; i++) {
            jp2.add(addLabel(s2[i]));
        }
        add("East", jp2);

        JPanel dummyP = new JPanel();
        dummyP.setVisible(false);
        setControls(new Component[] { dummyP });
    }


    public void setControls(Component[] controls) {
        this.controls = controls;
    }
  
    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }
    
    public String[] getConstraints() {
        return constraints;
    }

    public Component[] getControls() { 
        return controls;
    }

    public void handleThread(int state) {
        if (state == CustomControlsContext.START) {
            start();
        } else if (state == CustomControlsContext.STOP) {
            stop();
        }
    }

    private Component[] controls;
    private String[] constraints = { BorderLayout.NORTH };


    public void run() {
        Thread me = Thread.currentThread();
        try { thread.sleep(333); } catch (Exception e) { return; }
        while (thread == me) {
            for (int i = 0; i < tabbedPane.getTabCount() && thread == me; i++) {
                Object obj = tabbedPane.getComponent(i);
                tabbedPane.setSelectedIndex(i);
                if (obj instanceof Panel) {
/* UNCOMMENT TO USE JMF
                    JMF jmf = (JMF) jmftable.get(tabbedPane.getTitleAt(i));
                    while (!jmf.rewoundToggle) {
                        try {
                            thread.sleep(250);
                        } catch (InterruptedException e) {  break; }
                    }
                    jmf.stop();
                    jmf.rewoundToggle = false;
END UNCOMMENT*/
                } else if (obj instanceof JPanel) {
                    JPanel p = (JPanel) obj;
                    for (int j = 0; j < p.getComponentCount() && thread == me; j++) {
                        JavaSound js = (JavaSound) p.getComponent(j);
                        js.loopB.doClick();
                        try {
                            thread.sleep(7777);
                        } catch (InterruptedException e) { }
                        js.startStopB.doClick();
                    }
                }
            }
        }
        thread = null;
    }


    public JLabel addLabel(String s) {
        JLabel l = new JLabel(" " + s + " ");
        l.setForeground(Color.gray);
        l.setFont(f1);
        return l;
    }


    public void stateChanged(ChangeEvent e) {
/* UNCOMMENT TO USE JMF
        for (Enumeration enum = jmftable.elements(); enum.hasMoreElements();) {
            ((JMF) enum.nextElement()).stop();
        }
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int index = tabbedPane.getSelectedIndex();
        String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        if (title == "mov" || title == "mpg") {
            stopJavaSound();
            ((JMF) jmftable.get(title)).start();
        } 
END UNCOMMENT */
    }


    public void stopJavaSound() {
        for (int i = 0; i < jsound.length; i++) {
            if (jsound[i].audioState != jsound[i].STOP ) {
                jsound[i].stopAudio();
            }
        }
    }


    public URL getURL(String dir, String name) {
        URL url = null;
        if (Java2DemoApplet.applet != null) {
            try { 
                url = new URL(Java2DemoApplet.applet.getCodeBase(), dir + name);
            } catch (Exception ex) { ex.printStackTrace(); }
        } else {
            try {
                dir = System.getProperty("user.dir") + "/" + dir;
                url = new URL("file:" + dir + name);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        return url;
    }


    public static void main(String args[]) {
        JFrame f = new JFrame("Java2D Demo - JavaMedia");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        final JavaMedia demo = new JavaMedia();
        f.getContentPane().add("Center", demo);
        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int WIDTH = 310;
        int HEIGHT = 280;
        f.setLocation(screenSize.width/2 - WIDTH/2,
                          screenSize.height/2 - HEIGHT/2);
        f.setSize(WIDTH, HEIGHT);
        f.show();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-run")) {
                demo.start();
            }
        }
    }


    public class JavaSound extends JPanel implements ActionListener {

        AudioClip clip = null;
        JButton startStopB, loopB;
        Timer timer = null;
        final int STOP = 0;
        final int PLAY = 1;
        final int LOOP = 2;
        int audioState = STOP;


        public JavaSound(String name, Color color) {

            clip = Applet.newAudioClip(getURL("media/", name));
            EmptyBorder eb = new EmptyBorder(2,5,2,5);
            SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.RAISED);
            setBorder(new CompoundBorder(eb, sbb));
            setLayout(new BorderLayout());
    
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.gray);
            p.setLayout(new BorderLayout());
            JLabel l = new JLabel(name);
            l.setHorizontalAlignment(l.CENTER);
            l.setFont(new Font("serif",Font.BOLD, 14));
            l.setForeground(color);
            p.add("North", l);
    
            JPanel p1 = new JPanel(new FlowLayout());
            p1.setBackground(Color.gray);
            startStopB = new JButton(startIcon);
            startStopB.setPreferredSize(new Dimension(30, 20));
            startStopB.setMargin(new Insets(0,0,0,0));
            startStopB.addActionListener(this);
            p1.add(startStopB);
    
            loopB = new JButton(loopIcon);
            loopB.setMargin(new Insets(0,0,0,0));
            loopB.setPreferredSize(new Dimension(30, 20));
            loopB.addActionListener(this);
            p1.add(loopB);
    
            timer = new Timer(this, clip, color);
            p1.add(timer);
    
            p.add(p1);
            JToolBar toolbar = new JToolBar();
            toolbar.setBackground(Color.gray);
            toolbar.add(p);
            add(toolbar);
        }
    
    
        public JButton addButton(ImageIcon ii) {
            JButton b = new JButton(ii);
            b.addActionListener(this);
            return b;
        }
    
        public void stopAudio() {
            audioState = STOP;
            clip.stop();
            loopB.setIcon(loopIcon);
            startStopB.setIcon(startIcon);
            timer.stop();
        }
    
        public void playAudio() {
            clip.play();
            startStopB.setIcon(stopIcon);
            audioState = PLAY;
            timer.start();
        }
    
    
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(loopB)) {
                clip.loop();
                audioState = LOOP;
                timer.start();
                loopB.setIcon(loopingIcon);
                startStopB.setIcon(stopIcon);
            } else if (audioState >= PLAY) {
                stopAudio();
            } else {
                playAudio();
            }
        }
    
    
    
        /**
         * Render the elapsed seconds of the clip during playback.
         */
        public class Timer extends JComponent implements Runnable {
    
            private AudioClip clip;
            private boolean cbStop = true;
            private JavaMedia jm; 
            private JavaSound js;
            private BufferedImage bimg;
            private Thread thread;
            private int theSeconds;
            private int w, h;
            private Font font = new Font("Dialog", Font.BOLD, 12);
            private Color color;
    
    
            public Timer(JavaSound js, AudioClip clip, Color color) {
                this.js = js;
                this.clip = clip;
                this.color = color;
                setBackground(Color.black);
                setPreferredSize(new Dimension(30, 20));
            }
    

            public void start() {
                thread = new Thread(this);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.setName("Mix.JavaMedia.JavaSound.Timer");
                thread.start();
                theSeconds = 0;
            }
    
            public synchronized void stop() {
                thread = null;
                notifyAll();
            }
    
    
            public void run() {
    
                while (js.audioState != js.STOP) {
                    repaint();
                    try {
                        for (int i=0; i < 10 && js.audioState != js.STOP; i++) {
                            Thread.sleep(95);  // 95x10=950ms; almost a second
                        }
                    } catch(InterruptedException e){ break;}
                    if (theSeconds++ > 99) {
                        theSeconds = 0;
                    }
                }
                if (js.audioState != js.STOP) {
                    js.stopAudio();
                }
                theSeconds = 0;
                repaint();
            }
    
    
            public void paint(Graphics g) {
                if (bimg == null) {
                    bimg = (BufferedImage) createImage(30, 20);
                }
                int w = bimg.getWidth();
                int h = bimg.getHeight();
                Graphics2D big = bimg.createGraphics();
                big.setBackground(Color.black);
                big.clearRect(0,0,w,h);
                big.setFont(font);
                big.setColor(color);
                big.drawString(String.valueOf(theSeconds), 8,15);
                big.setColor(Color.gray);
                big.drawLine(0,0, 0,h-1);
                big.drawLine(0,0, w-1,0);
                big.setColor(Color.white);
                big.drawLine(w-1,0, w-1,h-1);
                big.drawLine(0,h-1, w-1,h-1);
                g.drawImage(bimg, 0, 0, this);
                big.dispose();
            }
        }  // End Timer class
    }  // End JavaSound class


// REMOVE TO USE JMF
    public class JMF extends JScrollPane {
        public JMF(String name) {
            String fs = System.getProperty("file.separator");
            String s = "Java Media Framework\n" +
                "http://java.sun.com/products/java-media/jmf\n\n" +
                "Usage Instructions in \n" +
                "Java2D" + fs + "src" + fs + "demos" + fs + 
                "Mix" + fs + "JavaMedia.java\n";
	    JTextArea textarea = new JTextArea(s);
            textarea.setFont(new Font("serif", Font.PLAIN, 10));
            textarea.setEditable(false);
	    getViewport().add(textarea);
        }
    }
// REMOVE TO HERE


/* UNCOMMENT TO USE JMF
    public class JMF extends Panel implements ControllerListener {
        
        Player player;
        boolean rewoundToggle;
    
        public JMF(String name) {
            setLayout(new BorderLayout());
            URL url = getURL("media/", name);
            if (url != null) {
                try {
                    player = Manager.createPlayer(url);
                } catch (Exception e) {
                    System.err.println("Can't create Manager because: " + e);
                }
                player.addControllerListener(this);
            } else {
                String fs = System.getProperty("file.separator");
                String s = "Couldn't locate : \n" +
                "Java2D" + fs + "demos" + fs + "Mix" + fs + name;
	        JTextArea textarea = new JTextArea(s);
                textarea.setFont(new Font("serif", Font.PLAIN, 10));
                textarea.setEditable(false);
	        JScrollPane scroller = new JScrollPane();
	        scroller.getViewport().add(textarea);
	        add(scroller);
            }
        }
    

        public void start() {
            invalidate();
            if (player != null) {
                player.start();
            }
            validate();
        }


        public void stop() {
            if (player != null && player.getState() == player.Started) {
                player.stop();
                player.deallocate();
            }
        }


        public synchronized void controllerUpdate(ControllerEvent event) {
            // If we're getting messages from a dead player, just leave
            if (player == null)
                return;
            // When the player is Realized, get the visual 
            // and control components and add them to the Panel
            if (event instanceof RealizeCompleteEvent) {
                add(player.getVisualComponent());
                add("South", player.getControlPanelComponent());
                validate();
            } else if (event instanceof EndOfMediaEvent) {
                // We've reached the end of the media; rewind & start over
                rewoundToggle = true;
                player.setMediaTime(new Time(0));
                player.start();
            } else if (event instanceof ControllerErrorEvent) {
                player = null;
            }
        }
    } // End JMF class
END UNCOMMENT */


} // End JavaMedia class
