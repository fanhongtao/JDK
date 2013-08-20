/*
 * @(#)TransferHandler.java	1.30 05/08/26
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.beans.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.TooManyListenersException;
import javax.swing.plaf.UIResource;
import javax.swing.event.*;

import com.sun.java.swing.SwingUtilities2;
import sun.reflect.misc.MethodUtil;

/**
 * This class is used to handle the transfer of a <code>Transferable</code>
 * to and from Swing components.  The <code>Transferable</code> is used to
 * represent data that is exchanged via a cut, copy, or paste 
 * to/from a clipboard.  It is also used in drag-and-drop operations
 * to represent a drag from a component, and a drop to a component.
 * Swing provides functionality that automatically supports cut, copy,
 * and paste keyboard bindings that use the functionality provided by
 * an implementation of this class.  Swing also provides functionality
 * that automatically supports drag and drop that uses the functionality
 * provided by an implementation of this class.  The Swing developer can 
 * concentrate on specifying the semantics of a transfer primarily by setting
 * the <code>transferHandler</code> property on a Swing component.
 * <p>
 * This class is implemented to provide a default behavior of transferring
 * a component property simply by specifying the name of the property in 
 * the constructor.  For example, to transfer the foreground color from
 * one component to another either via the clipboard or a drag and drop operation
 * a <code>TransferHandler</code> can be constructed with the string "foreground".  The
 * built in support will use the color returned by <code>getForeground</code> as the source
 * of the transfer, and <code>setForeground</code> for the target of a transfer.  
 * <p>
 * Please see
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/dnd.html">
 * How to Use Drag and Drop and Data Transfer</a>,
 * a section in <em>The Java Tutorial</em>, for more information.
 * 
 *
 * @author  Timothy Prinzing
 * @version 1.30 08/26/05
 * @since 1.4
 */
public class TransferHandler implements Serializable {

    /**
     * An <code>int</code> representing no transfer action. 
     */
    public static final int NONE = DnDConstants.ACTION_NONE;

    /**
     * An <code>int</code> representing a &quot;copy&quot; transfer action.
     * This value is used when data is copied to a clipboard
     * or copied elsewhere in a drag and drop operation.
     */
    public static final int COPY = DnDConstants.ACTION_COPY;

    /**
     * An <code>int</code> representing a &quot;move&quot; transfer action.
     * This value is used when data is moved to a clipboard (i.e. a cut)
     * or moved elsewhere in a drag and drop operation.
     */
    public static final int MOVE = DnDConstants.ACTION_MOVE;

    /**
     * An <code>int</code> representing a source action capability of either
     * &quot;copy&quot; or &quot;move&quot;.
     */
    public static final int COPY_OR_MOVE = DnDConstants.ACTION_COPY_OR_MOVE;
    
    /**
     * An <code>int</code> representing a &quot;link&quot; transfer action.
     * This value is used to specify that data should be linked in a drag
     * and drop operation.
     */
    private static final int LINK = DnDConstants.ACTION_LINK;


    /**
     * Returns an <code>Action</code> that behaves like a 'cut' operation.
     * That is, this will invoke <code>exportToClipboard</code> with
     * a <code>MOVE</code> argument on the <code>TransferHandler</code>
     * associated with the <code>JComponent</code> that is the source of
     * the <code>ActionEvent</code>.
     *
     * @return cut Action
     */
    public static Action getCutAction() {
        return cutAction;
    }

    /**
     * Returns an <code>Action</code> that behaves like a 'copy' operation.
     * That is, this will invoke <code>exportToClipboard</code> with
     * a <code>COPY</code> argument on the <code>TransferHandler</code>
     * associated with the <code>JComponent</code> that is the source of
     * the <code>ActionEvent</code>.
     *
     * @return cut Action
     */
    public static Action getCopyAction() {
        return copyAction;
    }

    /**
     * Returns an <code>Action</code> that behaves like a 'paste' operation.
     * That is, this will invoke <code>importData</code> on the
     * <code>TransferHandler</code>
     * associated with the <code>JComponent</code> that is the source of
     * the <code>ActionEvent</code>.
     *
     * @return cut Action
     */
    public static Action getPasteAction() {
        return pasteAction;
    }

    
    /**
     * Constructs a transfer handler that can transfer a Java Bean property
     * from one component to another via the clipboard or a drag and drop
     * operation.
     *
     * @param property  the name of the property to transfer; this can
     *  be <code>null</code> if there is no property associated with the transfer
     *  handler (a subclass that performs some other kind of transfer, for example)
     */
    public TransferHandler(String property) {
	propertyName = property;
    }

    /**
     * Convenience constructor for subclasses.
     */
    protected TransferHandler() {
	this(null);
    }

    /**
     * Causes the Swing drag support to be initiated.  This is called by 
     * the various UI implementations in the <code>javax.swing.plaf.basic</code>
     * package if the dragEnabled property is set on the component. 
     * This can be called by custom UI 
     * implementations to use the Swing drag support.  This method can also be called 
     * by a Swing extension written as a subclass of <code>JComponent</code>
     * to take advantage of the Swing drag support.
     * <p>
     * The transfer <em>will not necessarily</em> have been completed at the
     * return of this call (i.e. the call does not block waiting for the drop).
     * The transfer will take place through the Swing implementation of the
     * <code>java.awt.dnd</code> mechanism, requiring no further effort
     * from the developer. The <code>exportDone</code> method will be called
     * when the transfer has completed.
     *
     * @param comp  the component holding the data to be transferred; this
     *  argument is provided to enable sharing of <code>TransferHandler</code>s by
     *  multiple components
     * @param e     the event that triggered the transfer
     * @param action the transfer action initially requested; this should
     *  be a value of either <code>COPY</code> or <code>MOVE</code>;
     *  the value may be changed during the course of the drag operation
     */
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        int srcActions = getSourceActions(comp);
	int dragAction = srcActions & action;
	if (! (e instanceof MouseEvent)) {
	    // only mouse events supported for drag operations
	    dragAction = NONE;
	}
	if (dragAction != NONE && !GraphicsEnvironment.isHeadless()) {
	    if (recognizer == null) {
		recognizer = new SwingDragGestureRecognizer(new DragHandler());
	    }
	    recognizer.gestured(comp, (MouseEvent)e, srcActions, dragAction);
	} else {
            exportDone(comp, null, NONE);
        }
    }

    /**
     * Causes a transfer from the given component to the
     * given clipboard.  This method is called by the default cut and
     * copy actions registered in a component's action map.  
     * <p>
     * The transfer will take place using the <code>java.awt.datatransfer</code>
     * mechanism, requiring no further effort from the developer. Any data
     * transfer <em>will</em> be complete and the <code>exportDone</code>
     * method will be called with the action that occurred, before this method
     * returns. Should the clipboard be unavailable when attempting to place
     * data on it, the <code>IllegalStateException</code> thrown by
     * {@link Clipboard#setContents(Transferable, ClipboardOwner)} will
     * be propogated through this method. However, 
     * <code>exportDone</code> will first be called with an action
     * of <code>NONE</code> for consistency.
     *
     * @param comp  the component holding the data to be transferred; this
     *  argument is provided to enable sharing of <code>TransferHandler</code>s by
     *  multiple components
     * @param clip  the clipboard to transfer the data into  
     * @param action the transfer action requested; this should
     *  be a value of either <code>COPY</code> or <code>MOVE</code>;
     *  the operation performed is the intersection  of the transfer
     *  capabilities given by getSourceActions and the requested action;
     *  the intersection may result in an action of <code>NONE</code>
     *  if the requested action isn't supported
     * @throws IllegalStateException if the clipboard is currently unavailable
     * @see Clipboard#setContents(Transferable, ClipboardOwner)
     */
    public void exportToClipboard(JComponent comp, Clipboard clip, int action)
                                                  throws IllegalStateException {

	int clipboardAction = getSourceActions(comp) & action;
	if (clipboardAction != NONE) {
            Transferable t = createTransferable(comp);
            if (t != null) {
                try {
                    clip.setContents(t, null);
                    exportDone(comp, t, clipboardAction);
                    return;
                } catch (IllegalStateException ise) {
                    exportDone(comp, t, NONE);
                    throw ise;
                }
            }
        }

        exportDone(comp, null, NONE);
    }

    /**
     * Causes a transfer to a component from a clipboard or a 
     * DND drop operation.  The <code>Transferable</code> represents
     * the data to be imported into the component.  
     *
     * @param comp  the component to receive the transfer; this
     *  argument is provided to enable sharing of <code>TransferHandler</code>s 
     *  by multiple components
     * @param t     the data to import
     * @return  true if the data was inserted into the component, false otherwise
     */
    public boolean importData(JComponent comp, Transferable t) {
	PropertyDescriptor prop = getPropertyDescriptor(comp);
	if (prop != null) {
	    Method writer = prop.getWriteMethod();
	    if (writer == null) {
		// read-only property. ignore
		return false;
	    }
	    Class[] params = writer.getParameterTypes();
	    if (params.length != 1) {
		// zero or more than one argument, ignore
		return false;
	    }
	    DataFlavor flavor = getPropertyDataFlavor(params[0], t.getTransferDataFlavors());
	    if (flavor != null) {
		try {
		    Object value = t.getTransferData(flavor);
		    Object[] args = { value };
		    MethodUtil.invoke(writer, comp, args);
		    return true;
		} catch (Exception ex) {
		    System.err.println("Invocation failed");
		    // invocation code
		}
	    }
	}
	return false;
    }

    /**
     * Indicates whether a component would accept an import of the given
     * set of data flavors prior to actually attempting to import it. 
     *
     * @param comp  the component to receive the transfer; this
     *  argument is provided to enable sharing of <code>TransferHandlers</code>
     *  by multiple components
     * @param transferFlavors  the data formats available
     * @return  true if the data can be inserted into the component, false otherwise
     */
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
	PropertyDescriptor prop = getPropertyDescriptor(comp);
	if (prop != null) {
	    Method writer = prop.getWriteMethod();
	    if (writer == null) {
		// read-only property. ignore
		return false;
	    }
	    Class[] params = writer.getParameterTypes();
	    if (params.length != 1) {
		// zero or more than one argument, ignore
		return false;
	    }
	    DataFlavor flavor = getPropertyDataFlavor(params[0], transferFlavors);
	    if (flavor != null) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the type of transfer actions supported by the source. 
     * Some models are not mutable, so a transfer operation of <code>COPY</code>
     * only should be advertised in that case.
     * 
     * @param c  the component holding the data to be transferred; this
     *  argument is provided to enable sharing of <code>TransferHandler</code>s
     *  by multiple components.
     * @return  <code>COPY</code> if the transfer property can be found,
     *  otherwise returns <code>NONE</code>; a return value of
     *  of <code>NONE</code> disables any transfers out of the component
     */
    public int getSourceActions(JComponent c) {
	PropertyDescriptor prop = getPropertyDescriptor(c);
	if (prop != null) {
	    return COPY;
	}
	return NONE;
    }

    /**
     * Returns an object that establishes the look of a transfer.  This is
     * useful for both providing feedback while performing a drag operation and for 
     * representing the transfer in a clipboard implementation that has a visual 
     * appearance.  The implementation of the <code>Icon</code> interface should
     * not alter the graphics clip or alpha level. 
     * The icon implementation need not be rectangular or paint all of the
     * bounding rectangle and logic that calls the icons paint method should
     * not assume the all bits are painted. <code>null</code> is a valid return value 
     * for this method and indicates there is no visual representation provided.
     * In that case, the calling logic is free to represent the
     * transferable however it wants.  
     * <p>
     * The default Swing logic will not do an alpha blended drag animation if
     * the return is <code>null</code>.
     *
     * @param t  the data to be transferred; this value is expected to have been 
     *  created by the <code>createTransferable</code> method
     * @return  <code>null</code>, indicating
     *    there is no default visual representation
     */
    public Icon getVisualRepresentation(Transferable t) {
	return null;
    }

    /**
     * Creates a <code>Transferable</code> to use as the source for
     * a data transfer. Returns the representation of the data to
     * be transferred, or <code>null</code> if the component's
     * property is <code>null</code>
     *
     * @param c  the component holding the data to be transferred; this
     *  argument is provided to enable sharing of <code>TransferHandler</code>s
     *  by multiple components
     * @return  the representation of the data to be transferred, or
     *  <code>null</code> if the property associated with <code>c</code>
     *  is <code>null</code> 
     *  
     */
    protected Transferable createTransferable(JComponent c) {
	PropertyDescriptor property = getPropertyDescriptor(c);
	if (property != null) {
	    return new PropertyTransferable(property, c);
	}
	return null;
    }

    /**
     * Invoked after data has been exported.  This method should remove 
     * the data that was transferred if the action was <code>MOVE</code>.
     * <p>
     * This method is implemented to do nothing since <code>MOVE</code>
     * is not a supported action of this implementation
     * (<code>getSourceActions</code> does not include <code>MOVE</code>).
     *
     * @param source the component that was the source of the data
     * @param data   The data that was transferred or possibly null
     *               if the action is <code>NONE</code>.
     * @param action the actual action that was performed  
     */
    protected void exportDone(JComponent source, Transferable data, int action) {
    }

    /**
     * Fetches the property descriptor for the property assigned to this transfer
     * handler on the given component (transfer handler may be shared).  This 
     * returns <code>null</code> if the property descriptor can't be found
     * or there is an error attempting to fetch the property descriptor.
     */
    private PropertyDescriptor getPropertyDescriptor(JComponent comp) {
	if (propertyName == null) {
	    return null;
	}
	Class k = comp.getClass();
	BeanInfo bi;
	try {
	    bi = Introspector.getBeanInfo(k);
	} catch (IntrospectionException ex) {
	    return null;
	}
	PropertyDescriptor props[] = bi.getPropertyDescriptors();
	for (int i=0; i < props.length; i++) {
	    if (propertyName.equals(props[i].getName())) {
                Method reader = props[i].getReadMethod();

                if (reader != null) {
                    Class[] params = reader.getParameterTypes();

                    if (params == null || params.length == 0) {
                        // found the desired descriptor
                        return props[i];
                    }
                }
	    }
	}
	return null;
    }

    /**
     * Fetches the data flavor from the array of possible flavors that
     * has data of the type represented by property type.  Null is
     * returned if there is no match.
     */
    private DataFlavor getPropertyDataFlavor(Class k, DataFlavor[] flavors) {
	for(int i = 0; i < flavors.length; i++) {
	    DataFlavor flavor = flavors[i];
	    if ("application".equals(flavor.getPrimaryType()) &&
		"x-java-jvm-local-objectref".equals(flavor.getSubType()) &&
		k.isAssignableFrom(flavor.getRepresentationClass())) {

		return flavor;
	    }
	}
	return null;
    }


    private String propertyName;
    private static SwingDragGestureRecognizer recognizer = null;
    private static DropTargetListener dropLinkage = null;

    private static DropTargetListener getDropTargetListener() {
	if (dropLinkage == null) {
	    dropLinkage = new DropHandler();
	}
	return dropLinkage;
    }

    static class PropertyTransferable implements Transferable {

	PropertyTransferable(PropertyDescriptor p, JComponent c) {
	    property = p;
	    component = c;
	}

	// --- Transferable methods ----------------------------------------------

	/**
	 * Returns an array of <code>DataFlavor</code> objects indicating the flavors the data 
	 * can be provided in.  The array should be ordered according to preference
	 * for providing the data (from most richly descriptive to least descriptive).
	 * @return an array of data flavors in which this data can be transferred
	 */
        public DataFlavor[] getTransferDataFlavors() {
	    DataFlavor[] flavors = new DataFlavor[1];
	    Class propertyType = property.getPropertyType();
	    String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + propertyType.getName();
	    try {
		flavors[0] = new DataFlavor(mimeType);
	    } catch (ClassNotFoundException cnfe) {
		flavors = new DataFlavor[0];
	    }
	    return flavors;
	}

	/**
	 * Returns whether the specified data flavor is supported for
	 * this object.
	 * @param flavor the requested flavor for the data
	 * @return true if this <code>DataFlavor</code> is supported,
         *   otherwise false
	 */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
	    Class propertyType = property.getPropertyType();
	    if ("application".equals(flavor.getPrimaryType()) &&
		"x-java-jvm-local-objectref".equals(flavor.getSubType()) &&
		flavor.getRepresentationClass().isAssignableFrom(propertyType)) {

		return true;
	    }
	    return false;
	}

	/**
	 * Returns an object which represents the data to be transferred.  The class 
	 * of the object returned is defined by the representation class of the flavor.
	 *
	 * @param flavor the requested flavor for the data
	 * @see DataFlavor#getRepresentationClass
	 * @exception IOException                if the data is no longer available
	 *              in the requested flavor.
	 * @exception UnsupportedFlavorException if the requested data flavor is
	 *              not supported.
	 */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
	    if (! isDataFlavorSupported(flavor)) {
		throw new UnsupportedFlavorException(flavor);
	    }
	    Method reader = property.getReadMethod();
	    Object value = null;
	    try {
		value = MethodUtil.invoke(reader, component, null);
	    } catch (Exception ex) {
		throw new IOException("Property read failed: " + property.getName());
	    }
	    return value;
	}

	JComponent component;
	PropertyDescriptor property;
    }

    /**
     * This is the default drop target for drag and drop operations if
     * one isn't provided by the developer.  <code>DropTarget</code>
     * only supports one <code>DropTargetListener</code> and doesn't
     * function properly if it isn't set.
     * This class sets the one listener as the linkage of drop handling
     * to the <code>TransferHandler</code>, and adds support for
     * additional listeners which some of the <code>ComponentUI</code>
     * implementations install to manipulate a drop insertion location.
     */
    static class SwingDropTarget extends DropTarget implements UIResource {

	SwingDropTarget(JComponent c) {
            super();
            setComponent(c);
            try {
                super.addDropTargetListener(getDropTargetListener());
            } catch (TooManyListenersException tmle) {}
	}

        public void addDropTargetListener(DropTargetListener dtl) throws TooManyListenersException {
            // Since the super class only supports one DropTargetListener,
            // and we add one from the constructor, we always add to the
            // extended list.
            if (listenerList == null) {
                listenerList = new EventListenerList();
            }
            listenerList.add(DropTargetListener.class, dtl);
	}

        public void removeDropTargetListener(DropTargetListener dtl) {
            if (listenerList != null) {
                listenerList.remove(DropTargetListener.class, dtl);
            }
	}

	// --- DropTargetListener methods (multicast) --------------------------

        public void dragEnter(DropTargetDragEvent e) {
	    super.dragEnter(e);
	    if (listenerList != null) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==DropTargetListener.class) {
			((DropTargetListener)listeners[i+1]).dragEnter(e);
		    }	       
		}
	    }
	}

        public void dragOver(DropTargetDragEvent e) {
	    super.dragOver(e);
	    if (listenerList != null) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==DropTargetListener.class) {
			((DropTargetListener)listeners[i+1]).dragOver(e);
		    }	       
		}
	    }
	}

        public void dragExit(DropTargetEvent e) {
	    super.dragExit(e);
	    if (listenerList != null) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==DropTargetListener.class) {
			((DropTargetListener)listeners[i+1]).dragExit(e);
		    }	       
		}
	    }
	}

        public void drop(DropTargetDropEvent e) {
	    super.drop(e);
	    if (listenerList != null) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==DropTargetListener.class) {
			((DropTargetListener)listeners[i+1]).drop(e);
		    }	       
		}
	    }
	}

        public void dropActionChanged(DropTargetDragEvent e) {
	    super.dropActionChanged(e);
	    if (listenerList != null) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==DropTargetListener.class) {
			((DropTargetListener)listeners[i+1]).dropActionChanged(e);
		    }	       
		}
	    }
	}

        private EventListenerList listenerList;
    }

    private static class DropHandler implements DropTargetListener, Serializable {
        
        private boolean canImport;
        
        private boolean actionSupported(int action) {
            return (action & (COPY_OR_MOVE | LINK)) != NONE;
        }

	// --- DropTargetListener methods -----------------------------------

        public void dragEnter(DropTargetDragEvent e) {
	    DataFlavor[] flavors = e.getCurrentDataFlavors();

	    JComponent c = (JComponent)e.getDropTargetContext().getComponent();
	    TransferHandler importer = c.getTransferHandler();
            
            if (importer != null && importer.canImport(c, flavors)) {
                canImport = true;
            } else {
                canImport = false;
            }
            
            int dropAction = e.getDropAction();
            
            if (canImport && actionSupported(dropAction)) {
		e.acceptDrag(dropAction);
	    } else {
		e.rejectDrag();
	    }
	}

        public void dragOver(DropTargetDragEvent e) {
            int dropAction = e.getDropAction();
            
            if (canImport && actionSupported(dropAction)) {
                e.acceptDrag(dropAction);
            } else {
                e.rejectDrag();
            }
	}

        public void dragExit(DropTargetEvent e) {
	}

        public void drop(DropTargetDropEvent e) {
            int dropAction = e.getDropAction();

            JComponent c = (JComponent)e.getDropTargetContext().getComponent();
            TransferHandler importer = c.getTransferHandler();

	    if (canImport && importer != null && actionSupported(dropAction)) {
		e.acceptDrop(dropAction);
                
                try {
                    Transferable t = e.getTransferable();
		    e.dropComplete(importer.importData(c, t));
                } catch (RuntimeException re) {
                    e.dropComplete(false);
                }
	    } else {
		e.rejectDrop();
	    }
	}

        public void dropActionChanged(DropTargetDragEvent e) {
            int dropAction = e.getDropAction();
            
            if (canImport && actionSupported(dropAction)) {
                e.acceptDrag(dropAction);
            } else {
                e.rejectDrag();
            }
	}
    }

    /**
     * This is the default drag handler for drag and drop operations that 
     * use the <code>TransferHandler</code>.
     */
    private static class DragHandler implements DragGestureListener, DragSourceListener {
        
        private boolean scrolls;

	// --- DragGestureListener methods -----------------------------------

	/**
	 * a Drag gesture has been recognized
	 */
        public void dragGestureRecognized(DragGestureEvent dge) {
	    JComponent c = (JComponent) dge.getComponent();
	    TransferHandler th = c.getTransferHandler();
	    Transferable t = th.createTransferable(c);
	    if (t != null) {
                scrolls = c.getAutoscrolls();
                c.setAutoscrolls(false);
                try {
                    dge.startDrag(null, t, this);
                    return;
                } catch (RuntimeException re) {
                    c.setAutoscrolls(scrolls);
                }
	    }
            
            th.exportDone(c, t, NONE);
	}

	// --- DragSourceListener methods -----------------------------------

	/**
	 * as the hotspot enters a platform dependent drop site
	 */
        public void dragEnter(DragSourceDragEvent dsde) {
	}
  
	/**
	 * as the hotspot moves over a platform dependent drop site
	 */
        public void dragOver(DragSourceDragEvent dsde) {
	}
  
	/**
	 * as the hotspot exits a platform dependent drop site
	 */
        public void dragExit(DragSourceEvent dsde) {
	}
  
	/**
	 * as the operation completes
	 */
        public void dragDropEnd(DragSourceDropEvent dsde) {
            DragSourceContext dsc = dsde.getDragSourceContext();
            JComponent c = (JComponent)dsc.getComponent();
	    if (dsde.getDropSuccess()) {
                c.getTransferHandler().exportDone(c, dsc.getTransferable(), dsde.getDropAction());
	    } else {
                c.getTransferHandler().exportDone(c, dsc.getTransferable(), NONE);
            }
            c.setAutoscrolls(scrolls);
	}
  
        public void dropActionChanged(DragSourceDragEvent dsde) {
	}
    }

    private static class SwingDragGestureRecognizer extends DragGestureRecognizer {

	SwingDragGestureRecognizer(DragGestureListener dgl) {
	    super(DragSource.getDefaultDragSource(), null, NONE, dgl);
	}

	void gestured(JComponent c, MouseEvent e, int srcActions, int action) {
	    setComponent(c);
            setSourceActions(srcActions);
	    appendEvent(e);
	    fireDragGestureRecognized(action, e.getPoint());
	}

	/**
	 * register this DragGestureRecognizer's Listeners with the Component
	 */
        protected void registerListeners() {
	}

	/**
	 * unregister this DragGestureRecognizer's Listeners with the Component
	 *
	 * subclasses must override this method
	 */
        protected void unregisterListeners() {
	}

    }

    static final Action cutAction = new TransferAction("cut");
    static final Action copyAction = new TransferAction("copy");
    static final Action pasteAction = new TransferAction("paste");
    
    static class TransferAction extends AbstractAction implements UIResource {

	TransferAction(String name) {
	    super(name);
	}

        public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();
	    if (src instanceof JComponent) {
		JComponent c = (JComponent) src;
		TransferHandler th = c.getTransferHandler();
		Clipboard clipboard = getClipboard(c);
		String name = (String) getValue(Action.NAME);

                Transferable trans = null;

                // any of these calls may throw IllegalStateException
                try {
                    if ((clipboard != null) && (th != null) && (name != null)) {
                        if ("cut".equals(name)) {
                            th.exportToClipboard(c, clipboard, MOVE);
                        } else if ("copy".equals(name)) {
                            th.exportToClipboard(c, clipboard, COPY);
                        } else if ("paste".equals(name)) {
                            trans = clipboard.getContents(null);
                        }
                    }
                } catch (IllegalStateException ise) {
                    // clipboard was unavailable
                    UIManager.getLookAndFeel().provideErrorFeedback(c);
                    return;
                }

                // this is a paste action, import data into the component
                if (trans != null) {
                    th.importData(c, trans);
                }
	    }
	}

	/**
	 * Returns the clipboard to use for cut/copy/paste.
	 */
        private Clipboard getClipboard(JComponent c) {
	    if (SwingUtilities2.canAccessSystemClipboard()) {
		return c.getToolkit().getSystemClipboard();
	    }
	    Clipboard clipboard = (Clipboard)sun.awt.AppContext.getAppContext().
		get(SandboxClipboardKey);
	    if (clipboard == null) {
		clipboard = new Clipboard("Sandboxed Component Clipboard");
		sun.awt.AppContext.getAppContext().put(SandboxClipboardKey,
						       clipboard);
	    }
	    return clipboard;
	}

	/**
	 * Key used in app context to lookup Clipboard to use if access to
	 * System clipboard is denied.
	 */
        private static Object SandboxClipboardKey = new Object();

    }

}



