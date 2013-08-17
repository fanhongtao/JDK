/*
 * @(#)UIResource.java	1.5 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf;


/** 
 * This interface is used to mark objects created by ComponentUI delegates.
 * The <code>ComponentUI.installUI()</code> and 
 * <code>ComponentUI.uninstallUI()</code> methods can use this interface
 * to decide if a properties value has been overridden.  For example, the
 * JList cellRenderer property is initialized by BasicListUI.installUI(),
 * only if it's initial value is null:
 * <pre>
 * if (list.getCellRenderer() == null) {
 *     list.setCellRenderer((ListCellRenderer)(UIManager.get("List.cellRenderer")));
 * }
 * </pre>
 * At uninstallUI() time we reset the property to null if its value
 * is an instance of UIResource:
 * <pre>
 * if (list.getCellRenderer() instanceof UIResource) {
 *     list.setCellRenderer(null);
 * }
 *</pre>
 * This pattern applies to all properties except the java.awt.Component
 * properties font, foreground, and background.  If one of these
 * properties isn't initialized, or is explicitly set to null, 
 * its container provides the value.  For this reason the 
 * <code>"== null"</code> is unreliable when installUI() is called
 * to dynamically change a components look and feel.  So at installUI() 
 * time we check to see if the current value is a UIResource:
 *<pre>
 * if (!(list.getFont() instanceof UIResource)) {
 *     list.setFont(UIManager.getFont("List.font"));
 * }
 * </pre>
 *
 * @see ComponentUI
 * @version 1.5 09/21/98
 * @author Hans Muller
 * 
 */

public interface UIResource {
}
