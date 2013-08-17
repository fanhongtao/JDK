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


To run the SampleTree demo on 1.3:
  java -jar SampleTree.jar

