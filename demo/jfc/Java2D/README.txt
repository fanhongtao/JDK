The source code, classes, and supporting files for the Java2D demo are 
contained in the Java2Demo.jar file.  To run the Java2D demo :

% java -jar Java2Demo.jar
   - or -
% appletviewer Java2Demo.html

Although it's not necessary to unpack the Java2Demo.jar file to run 
the demo, you may want to extract its contents so you can look at the 
source code or other files individually. To extract the contents of 
Java2Demo.jar, run this command from the Java2D directory :

    jar xvf Java2Demo.jar


-----------------------------------------------------------------------
Introduction
-----------------------------------------------------------------------

This Java2D demo consists of a set of demos housed in one GUI 
framework that uses a JTabbedPane.  You can access different groups of 
demos by clicking the tabs at the top of the pane. There are demo 
groups for Arcs_Curves, Clipping, Colors, Composite, Fonts, Images, 
Lines, Mix, Paint, Paths and Transforms.  On the right-hand side of the 
pane, the GUI framework features individual and global controls for 
changing graphics attributes. There's also a memory-usage monitor, and 
a monitor for tracking the performance, in frames per second, of 
animation demos.


-----------------------------------------------------------------------
Tips on usage 
----------------------------------------------------------------------- 

Click on one of the tabs at the top of the pane to select a demo group.  
When you select a group, a set of surfaces is displayed, each of which 
contains one of the group's demos. At the bottom of each surface is 
a set of tools for controlling the demo.  The tools can be displayed
by selecting the Tools checkbox in the Global Controls panel or
by clicking on the slim strip of gray bumps at the bottom of the demo
panel.

If you click on a demo surface, that demo is laid out by itself. A
new icon button will appear in the demo's tools toolbar one that enables 
you to create new instances of that demo's surface. 

To run the demo continuously without user interaction, select the 
Run Window item in the Options menu and press the run button in the 
new window that's displayed.  To do this from the command line :

    java -jar Java2Demo.jar -runs=10

To view all the command line options for customizing demo runs :

    java -jar Java2Demo.jar -help

Parameters that can be used in the Java2Demo.html file inside the applet 
tag to customize demo runs :
              <param name="runs" value="10">
              <param name="delay" value="10">
              <param name="ccthread" value=" ">
              <param name="screen" value="5">
              <param name="antialias" value="true">
              <param name="rendering" value="true">
              <param name="texture" value="true">
              <param name="composite" value="true">
              <param name="verbose" value=" ">
              <param name="buffers" value="3,10">
              <param name="verbose" value=" ">
              <param name="zoom" value=" ">

You can run the demos in stand-alone mode by issuing a command like 
this from the Java2D directory:

    java -cp Java2Demo.jar demos.Clipping.ClipAnim

You can run the demos in groups by issuing a command like this from
the Java2D directory:

    java -cp Java2Demo.jar DemoGroup Clipping    

To recompile a demo, first extract the contents of the Java2Demo.jar 
file, then issue this command from the Java2D directory:

    For win32 :

        javac demos\Clipping\ClipAnim.java

    For solaris :

        javac demos/Clipping/ClipAnim.java

Appletviewer and Hotjava throw security exceptions when attempting
to print.  To print run the demo as a stand alone application.

To increase or decrease the Memory Monitor sampling rate click on the
Memory Monitor's title border, a panel with a TextField will appear.

The Java2Demo Intro (the 'Java2D' tab) contains a scene table, click in 
the gray border and a table will appear.

Animated demos have a slider to control the animation rate.  Bring up
the animated demo toolbar, then click in the gray area of the toolbar
panel, the toolbar goes away and the slider appears.

Demos that have Custom Controls can have their Custom Control Thread
activated and stopped by clicking in the gray area of the demos Custom 
Control panel.

For less garbage collection and smoother animation for the Intro and
other animated demos run with command line argument :

    java -jar -ms48m Java2Demo.jar



-----------------------------------------------------------------------
NOTE about demo surfaces 
----------------------------------------------------------------------- 

The demo groups are in separate packages with their class files stored 
in directories named according to the demo group name.  All drawing 
demos extend the DemoSurface abstract class and implement the 
DemoSurface's drawDemo method.  All animated demos implement the 
AnimatingContext interface.

You can change a demo into a Canvas instead of a DemoSurface by making 
it extend Canvas rather than DemoSurface. You'll also need to change 
drawDemo(int w, int h, Graphics2D g2) to paint(Graphics g), and declare and 
intialize g2. For those demos that animate, the run(), start() and 
stop() methods will be needed to handle the thread, and you should 
create and draw into an off screen image for double buffering.  As an 
example, the conversion for Curves.java (non-animated) would be:

public class Curves extends DemoSurface {
    public void drawDemo(int w, int h, Graphics2D g2) {
        ...
    }
}

Becomes :

public class Curves extends Canvas {
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
	Dimension d = getSize();
        ...
    }
}

Most of the Java2Demo demos can be found as stand-alone Samples :

http://java.sun.com/products/java-media/2D/samples/index.html


======================================================================

For a Java2D API Guide & Tutorial :

http://java.sun.com/products/jdk/1.2/docs/guide/2d/spec/j2d-bookTOC.doc.html
http://java.sun.com/docs/books/tutorial/2d/index.html

For the latest version of the Java2D demo :

http://java.sun.com/products/java-media/2D/index.html

You may send comments via the java2d-comments@sun.com alias, 
which is a one-way alias to Sun's Java 2D API developers, or via the
java2d-interest@sun.com alias, which is a public discussion list. 
