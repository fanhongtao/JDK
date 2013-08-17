/*
 * @(#)AccessibleResourceBundle_ja.java	1.8 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.accessibility;

import java.util.ListResourceBundle;

/**
 * A resource bundle containing the localized strings in the accessibility 
 * package.  This is meant only for internal use by Java Accessibility and
 * is not meant to be used by assistive technologies or applications.
 *
 * @version     1.4 07/23/99 18:11:02
 * @author      Willie Walker
 */
public class AccessibleResourceBundle_ja extends ListResourceBundle {

    /**
     * Returns the mapping between the programmatic keys and the
     * localized display strings.
     */
    public Object[][] getContents() {
	return contents;
    }

    /** 
     * The table holding the mapping between the programmatic keys
     * and the display strings for the en_US locale.
     */
    static final Object[][] contents = {
    // LOCALIZE THIS
        // Role names
//        { "application","\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3" },
//        { "border","\u30dc\u30fc\u30c0" },
//        { "checkboxmenuitem","\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u30e1\u30cb\u30e5\u30fc\u9805\u76ee" },
//        { "choice","\u9078\u629e" },
//        { "column","\u5217" },
//        { "cursor","\u30ab\u30fc\u30bd\u30eb" },
//        { "document","\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8" },
//        { "grouping","\u30b0\u30eb\u30fc\u30d7\u5316" },
//        { "image","\u30a4\u30e1\u30fc\u30b8" },
//        { "indicator","\u30a4\u30f3\u30b8\u30b1\u30fc\u30bf" },
//        { "radiobuttonmenuitem","\u30e9\u30b8\u30aa\u30dc\u30bf\u30f3\u30e1\u30cb\u30e5\u30fc\u9805\u76ee" },
//        { "row","\u884c" },
//        { "tablecell","\u30c6\u30fc\u30d6\u30eb\u30bb\u30eb" },
//        { "treenode","\u30c4\u30ea\u30fc\u30ce\u30fc\u30c9" },
        { "alert","\u8b66\u544a" },
        { "awtcomponent","AWT \u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8" },
        { "checkbox","\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9" },
        { "colorchooser","\u30ab\u30e9\u30fc\u30c1\u30e5\u30fc\u30b6" },
        { "columnheader","\u5217\u30d8\u30c3\u30c0" },
        { "combobox","\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9" },
        { "canvas","\u30ad\u30e3\u30f3\u30d0\u30b9" },
        { "desktopicon","\u30c7\u30b9\u30af\u30c8\u30c3\u30d7\u30a2\u30a4\u30b3\u30f3" },
        { "desktoppane","\u30c7\u30b9\u30af\u30c8\u30c3\u30d7\u533a\u753b" },
        { "dialog","\u30c0\u30a4\u30a2\u30ed\u30b0" },
        { "directorypane","\u30c7\u30a3\u30ec\u30af\u30c8\u30ea\u533a\u753b" },
        { "glasspane","\u30ac\u30e9\u30b9\u533a\u753b" },
        { "filechooser","\u30d5\u30a1\u30a4\u30eb\u30c1\u30e5\u30fc\u30b6" },
        { "filler","\u30d5\u30a3\u30e9\u30fc" },
        { "frame","\u30d5\u30ec\u30fc\u30e0" },
        { "internalframe","\u5185\u90e8\u30d5\u30ec\u30fc\u30e0" },
        { "label","\u30e9\u30d9\u30eb" },
        { "layeredpane","\u968e\u5c64\u5316\u3055\u308c\u305f\u533a\u753b" },
        { "list","\u30ea\u30b9\u30c8" },
        { "listitem","\u30ea\u30b9\u30c8\u9805\u76ee" },
        { "menubar","\u30e1\u30cb\u30e5\u30fc\u30d0\u30fc" },
        { "menu","\u30e1\u30cb\u30e5\u30fc" },
        { "menuitem","\u30e1\u30cb\u30e5\u30fc\u9805\u76ee" },
        { "optionpane","\u30aa\u30d7\u30b7\u30e7\u30f3\u533a\u753b" },
        { "pagetab","\u30da\u30fc\u30b8\u30bf\u30d6" },
        { "pagetablist","\u30da\u30fc\u30b8\u30bf\u30d6\u30ea\u30b9\u30c8" },
        { "panel","\u30d1\u30cd\u30eb" },
	{ "passwordtext","\u30d1\u30b9\u30ef\u30fc\u30c9\u30c6\u30ad\u30b9\u30c8" },
        { "popupmenu","\u30dd\u30c3\u30d7\u30a2\u30c3\u30d7\u30e1\u30cb\u30e5\u30fc" },
        { "progressbar","\u9032\u6357\u30d0\u30fc" },
        { "pushbutton","\u30d7\u30c3\u30b7\u30e5\u30dc\u30bf\u30f3" },
        { "radiobutton","\u30e9\u30b8\u30aa\u30dc\u30bf\u30f3" },
        { "rootpane","\u30eb\u30fc\u30c8\u533a\u753b" },
        { "rowheader","\u884c\u30d8\u30c3\u30c0" },
        { "scrollbar","\u30b9\u30af\u30ed\u30fc\u30eb\u30d0\u30fc" },
        { "scrollpane","\u30b9\u30af\u30ed\u30fc\u30eb\u533a\u753b" },
        { "separator","\u30bb\u30d1\u30ec\u30fc\u30bf" },
        { "slider","\u30b9\u30e9\u30a4\u30c0" },
        { "splitpane","\u5206\u5272\u533a\u753b" },
        { "swingcomponent","Swing \u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8" },
        { "table","\u30c6\u30fc\u30d6\u30eb" },
        { "text","\u30c6\u30ad\u30b9\u30c8" },
        { "tree","\u30c4\u30ea\u30fc" },
        { "togglebutton","\u30c8\u30b0\u30eb\u30dc\u30bf\u30f3" },
        { "toolbar","\u30c4\u30fc\u30eb\u30d0\u30fc" },
        { "tooltip","\u30c4\u30fc\u30eb\u30d2\u30f3\u30c8" },
        { "unknown","\u672a\u77e5" },
        { "viewport","\u30d3\u30e5\u30fc\u30dd\u30fc\u30c8" },
        { "window","\u30a6\u30a3\u30f3\u30c9\u30a6" },
        // Relations
        { "labelFor","\u30e9\u30d9\u30eb\u5143" },
        { "labeledBy","\u30e9\u30d9\u30eb\u5148" },
        { "memberOf","\u6240\u5c5e\u30e1\u30f3\u30d0" },
        { "controlledBy","\u5236\u5fa1\u5bfe\u8c61" },
        { "controllerFor","\u5236\u5fa1\u5143" },
        // State modes
        { "active","\u30a2\u30af\u30c6\u30a3\u30d6" },
        { "armed","\u4f5c\u52d5\u6e96\u5099\u5b8c\u4e86" },
        { "busy","\u30d3\u30b8\u30fc" },
        { "checked","\u30c1\u30a7\u30c3\u30af" },
	{ "collapsed", "\u77ed\u7e2e" },
        { "editable","\u7de8\u96c6\u53ef\u80fd" },
	{ "expandable", "\u5c55\u958b\u53ef\u80fd" },
	{ "expanded", "\u5c55\u958b" },
        { "enabled", "\u6709\u52b9" },
        { "focusable","\u30d5\u30a9\u30fc\u30ab\u30b9\u53ef\u80fd" },
        { "focused","\u30d5\u30a9\u30fc\u30ab\u30b9" },
	{ "iconified", "\u30a2\u30a4\u30b3\u30f3\u5316" },
	{ "modal", "\u30e2\u30fc\u30c0\u30eb" },
	{ "multiline", "\u8907\u6570\u884c" },
        { "multiselectable","\u8907\u6570\u9078\u629e\u53ef\u80fd" },
	{ "opaque", "\u4e0d\u900f\u660e" },
        { "pressed","\u62bc\u4e0b" },
	{ "resizable", "\u30b5\u30a4\u30ba\u5909\u66f4\u53ef\u80fd" },
        { "selectable","\u9078\u629e\u53ef\u80fd" },
        { "selected","\u9078\u629e" },
        { "showing","\u8868\u793a" },
	{ "singleline", "\u5358\u4e00\u884c" },
	{ "transient", "\u4e00\u6642" },
        { "visible","\u53ef\u8996" },
        { "vertical","\u5782\u76f4" },
        { "horizontal","\u6c34\u5e73" }
    // END OF MATERIAL TO LOCALIZE
    };
}
