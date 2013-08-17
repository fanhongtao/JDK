SampleTree demonstrates JTree features.  Each node of SampleTree has 7
children, with each one drawn in a random font and color.  Each node is
named after its font.  While the data isn't interesting, the example
illustrates a number of features:

- Dynamically loading children (see DynamicTreeNode.java)
- Adding/removing/inserting/reloading (see the following inner
  classes in SampleTree.java: AddAction, RemoveAction, InsertAction,
  and ReloadAction)
- Creating a custom cell renderer (see SampleTreeCellRenderer.java)
- Subclassing JTreeModel for editing (see SampleTreeModel.java)


To run the SampleTree demo on 1.2 <all platforms>:
  java -jar SampleTree.jar

To run the SampleTree demo on 1.1.x on Solaris:
  setenv SWING_HOME <path to swing release>
  setenv JAVA_HOME <path to jdk1.1.x release>
  runnit


To run the SampleTree demo on 1.1.x on win32:
  set CLASSPATH=<path to jdk1.1.x release>\lib\classes.zip
  set SWING_HOME=<path to swing release>
  runnit

