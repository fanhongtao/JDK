/*
 * @(#)JavaSound.java	1.15 98/09/13
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

package demos.Mix;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.image.BufferedImage;
import java.net.URL;


/**
 * JavaSound example to show effects of sound with 2d animation.
 */
public class JavaSound extends JPanel implements ActionListener {

    private AudioClip clip = null;
    private JButton playButton, loopButton;
    private Timer timer = null;
    protected static final int STOP = 0;
    protected static final int PLAY = 1;
    protected static final int LOOP = 2;
    protected int audioState = STOP;
    private Font f1 = new Font("serif", Font.BOLD + Font.ITALIC, 18);
    private Font f2 = new Font("serif", Font.PLAIN, 14);


    public JavaSound() {
        setLayout(new BorderLayout());
        setBackground(Color.gray);

        String playType[] = {"mix1","mix2", "rmf"};
        String playList[][] = {
                {"sfx-medley.rmf", "trippygaia1.mid", "spacemusic.au"},
                {"bong.wav", "bark.aiff", "trance.rmf"},
                {"ambient.rmf", "classical.rmf", "modern-rock.rmf"}};

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new BevelBorder(BevelBorder.RAISED));
        tabbedPane.setFont(new Font("Times New Roman", Font.PLAIN, 10));

        for (int i = 0; i < playType.length; i++) {
            JPanel jp = new JPanel(new GridLayout(0,1));
            for (int j = 0; j < playList[i].length; j++) {
                jp.add(new JavaSound(this, playList[i][j], false));
            }
            tabbedPane.addTab(playType[i], jp);
        }
        add(tabbedPane);


        JPanel jp1 = new JPanel(new GridLayout(0,1));
        String s1[] = { "J", "A", "V", "A" };
        for (int i = 0; i < s1.length; i++) {
            jp1.add(addLabel(s1[i]));
        }
        add("West", jp1);

        JPanel jp2 = new JPanel(new GridLayout(0,1));
        String s2[] = { "S", "O", "U", "N", "D" };
        for (int i = 0; i < s2.length; i++) {
            jp2.add(addLabel(s2[i]));
        }
        add("East", jp2);
    }


    public JLabel addLabel(String s) {
        JLabel l = new JLabel(" " + s + " ");
        l.setForeground(Color.gray);
        l.setFont(f1);
        return l;
    }


    public JavaSound(JavaSound js, String name, boolean playSomeAudio) {

        URL url = JavaSound.class.getResource("audio/" + name);
        clip = Applet.newAudioClip(url);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        setBackground(Color.gray);

        JLabel l = new JLabel(name);
        l.setFont(f2);
        l.setForeground(Color.black);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbl.setConstraints(l, gbc);
        add(l);

        JPanel p1 = new JPanel(new FlowLayout());
        p1.setBackground(Color.gray);
        playButton = new JButton("play");
        playButton.setFont(f2);
        playButton.setBackground(Color.green);
        playButton.addActionListener(this);
        p1.add(playButton);

        loopButton = new JButton("loop");
        loopButton.setFont(f2);
        loopButton.addActionListener(this);
        p1.add(loopButton);

        timer = new Timer(js, this, clip);
        p1.add(timer);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbl.setConstraints(p1, gbc);

        add(p1);

        if (playSomeAudio) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "play"));
        }
    }


    public JButton addButton(ImageIcon ii) {
        JButton b = new JButton(ii);
        b.addActionListener(this);
        return b;
    }

    public void stopAudio() {
        audioState = STOP;
        clip.stop();
        playButton.setText("play");
        playButton.setBackground(Color.green);
        timer.stop();
    }

    public void playAudio() {
        clip.play();
        playButton.setText("stop");
        playButton.setBackground(Color.red);
        timer.start();
        audioState = PLAY;
    }


    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand() == "loop") {
            clip.loop();
            timer.start();
            audioState = LOOP;
            playButton.setText("stop");
            playButton.setBackground(Color.red);
        } else if (audioState >= PLAY) {
            stopAudio();
        } else {
            playAudio();
        }
    }


    public static void main(String s[]) {
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        };
        JFrame f = new JFrame("Java2D Demo - JavaSound");
        f.addWindowListener(l);
        f.getContentPane().add("Center", new JavaSound());
        f.pack();
        f.setSize(new Dimension(320,260));
        f.show();
    }



    // inner class to paint the digits.
    public class Timer extends JComponent implements Runnable {

        private AudioClip clip;
        private boolean cbStop = true;
        private JavaSound js1, js2;
        private BufferedImage bi;
        private Thread thread;
        private int theSeconds;
        private int w, h;
        private Font font = new Font("Dialog", Font.BOLD, 12);


        public Timer(JavaSound js1, JavaSound js2, AudioClip clip) {
            this.js1 = js1;
            this.js2 = js2;
            this.clip = clip;
            setBackground(Color.black);
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            return new Dimension(32, 24);
        }

        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            theSeconds = 0;
        }

        public synchronized void stop() {
            thread = null;
            notifyAll();
        }


        public void run() {

            while (js2.audioState != js2.STOP && js1.isShowing()) {
                repaint();
                try {
                    for (int i = 0; i < 10 && 
                        js2.audioState != js2.STOP && 
                            js1.isShowing(); i++) 
                    {
                        Thread.sleep(95);  // 95x10=950ms; almost a second
                    }
                } catch(InterruptedException e){ break;}
                theSeconds++;
                if (theSeconds == 999) {
                    theSeconds = 0;
                }
            }
            if (js2.audioState != js2.STOP) {
                js2.stopAudio();
            }
            theSeconds = 0;
            repaint();
        }


        public void update(Graphics g) {
            paint(g);
        }


        public void paint(Graphics g) {
            if (bi == null) {
                Dimension d = getSize();
                w = d.width;
                h = d.height;
                bi = (BufferedImage) createImage(w, h);
            }
            Graphics2D big = bi.createGraphics();
            big.setBackground(Color.black);
            big.clearRect(0,0,w,h);
            big.setFont(font);
            big.setColor(Color.yellow);
            big.drawString(String.valueOf(theSeconds), 8,16);
            big.setColor(Color.gray);
            big.drawLine(0,0, 0,h-1);
            big.drawLine(0,0, w-1,0);
            big.setColor(Color.white);
            big.drawLine(w-1,0, w-1,h-1);
            big.drawLine(0,h-1, w-1,h-1);
            g.drawImage(bi, 0, 0, this);
            big.dispose();
        }
    }
}
