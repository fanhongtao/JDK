/*
 * @(#)ComboBoxEditor.java	1.10 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;

/**
 * The editor component used for JComboBox components.
 *
 * @version 1.10 02/02/00
 * @author Arnaud Weber
 */
public interface ComboBoxEditor {
  
  /** Return the component that should be added to the tree hierarchy for
    * this editor
    */
  public Component getEditorComponent();
  
  /** Set the item that should be edited. Cancel any editing if necessary **/
  public void setItem(Object anObject);

  /** Return the edited item **/
  public Object getItem();

  /** Ask the editor to start editing and to select everything **/
  public void selectAll();    

  /** Add an ActionListener. An action event is generated when the edited item changes **/
  public void addActionListener(ActionListener l);

  /** Remove an ActionListener **/
  public void removeActionListener(ActionListener l);
}
