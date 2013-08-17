To run the Java2D demo :

% java Java2Demo
 - or -
% appletviewer Java2Demo.html


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
small toolbar for controlling the demo. 

If you click on a demo surface, that demo is laid out by itself. A
new icon buttons will appear in the demo's toolbar one that enables you 
to create new instances of that demo's surface. 

To run the demo continuously without user interaction, select the 
Run Window item in the Options menu and press the run button in the 
new window that's displayed.

Appletviewer and Hotjava throw security exceptions when attempting
to print.  To print run the demo as a stand alone application.


-----------------------------------------------------------------------
NOTE about demo surfaces 
----------------------------------------------------------------------- 

The demo groups are in separate packages with their class files stored 
in directories named according to the demo group name.  This makes it 
possible for the demos to be loaded dynamically. All drawing demos extend 
the DemoSurface abstract class and implement the DemoSurface's drawDemo 
method.  All animated demos implement the AnimatingContext interface.

You can run the demos in stand-alone mode by issuing a command like 
this from the Java2D directory:
    
    java demos.Clipping.ClipAnim

You can run the demos in groups by issuing a command like this from
the Java2D directory:

    java DemoGroup Clipping

To recompile a demo, issue this command from the Java2D directory:

For win32 :

    javac demos\Clipping\ClipAnim.java -d .

For solaris :

    javac demos/Clipping/ClipAnim.java -d .


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


======================================================================

For a Java2D API Overview & Tutorial :

http://java.sun.com/products/jdk/1.2/docs/guide/2d/spec/j2d-bookTOC.doc.html

For the latest version of the Java2D demo :

http://java.sun.com/products/java-media/2D/index.html

You may send comments via the java2d-comments@sun.com alias, 
which is a one-way alias to Sun's Java 2D API developers, or via the
java2d-interest@sun.com alias, which is a public discussion list. 
