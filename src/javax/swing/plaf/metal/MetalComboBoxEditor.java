/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.border.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * The default editor for Metal editable combo boxes
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.17 06/06/06
 * @author Steve Wilson
 */
public class MetalComboBoxEditor extends BasicComboBoxEditor {

    public MetalComboBoxEditor() {
        super();
        //editor.removeFocusListener(this);
        editor = new JTextField("",9);
        editor.setBorder( new EditorBorder() );
        //editor.addFocusListener(this);
    }

    protected static Insets editorBorderInsets = new Insets( 2, 2, 2, 0 );
    private static final Insets SAFE_EDITOR_BORDER_INSETS = new Insets( 4, 2, 4, 0 );
  
    class EditorBorder extends AbstractBorder {
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate( x, y );

            g.setColor( MetalLookAndFeel.getControlDarkShadow() );
            g.drawLine( 0, 0, w-1, 0 );
            g.drawLine( 0, 0, 0, h-2 );
            g.drawLine( 0, h-2, w-1, h-2 );
            g.setColor( MetalLookAndFeel.getControlHighlight() );
            g.drawLine( 1, 1, w-1, 1 );
            g.drawLine( 1, 1, 1, h-1 );
            g.drawLine( 1, h-1, w-1, h-1 );
            g.setColor( MetalLookAndFeel.getControl() );
            g.drawLine( 1, h-2, 1, h-2 );

            g.translate( -x, -y );
        }

        public Insets getBorderInsets( Component c ) {
            if (System.getSecurityManager() != null) {
               return SAFE_EDITOR_BORDER_INSETS;
            } else {
            return editorBorderInsets;
            }
        }
    }


    /**
     * A subclass of BasicComboBoxEditor that implements UIResource.
     * BasicComboBoxEditor doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with BasicListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class UIResource extends MetalComboBoxEditor
    implements javax.swing.plaf.UIResource {
    }
}

