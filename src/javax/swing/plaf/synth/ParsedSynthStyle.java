/*
 * @(#)ParsedSynthStyle.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import java.awt.*;
import javax.swing.*;
import sun.swing.plaf.synth.*;

/**
 * ParsedSynthStyle are the SynthStyle's that SynthParser creates.
 *
 * @version 1.3, 12/19/03
 * @author Scott Violet
 */
class ParsedSynthStyle extends DefaultSynthStyle {
    private static SynthPainter DELEGATING_PAINTER_INSTANCE = new
                        DelegatingPainter();
    private PainterInfo[] _painters;

    private static PainterInfo[] mergePainterInfo(PainterInfo[] old,
                                                  PainterInfo[] newPI) {
        if (old == null) {
            return newPI;
        }
        if (newPI == null) {
            return old;
        }
        int oldLength = old.length;
        int newLength = newPI.length;
        int dups = 0;
        PainterInfo[] merged = new PainterInfo[oldLength + newLength];
        System.arraycopy(old, 0, merged, 0, oldLength);
        for (int newCounter = 0; newCounter < newLength; newCounter++) {
            boolean found = false;
            for (int oldCounter = 0; oldCounter < oldLength - dups;
                     oldCounter++) {
                if (newPI[newCounter].equalsPainter(old[oldCounter])) {
                    merged[oldCounter] = newPI[newCounter];
                    dups++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                merged[oldLength + newCounter - dups] = newPI[newCounter];
            }
        }
        if (dups > 0) {
            PainterInfo[] tmp = merged;
            merged = new PainterInfo[merged.length - dups];
            System.arraycopy(tmp, 0, merged, 0, merged.length);
        }
        return merged;
    }


    public ParsedSynthStyle() {
    }

    public ParsedSynthStyle(DefaultSynthStyle style) {
        super(style);
        if (style instanceof ParsedSynthStyle) {
            ParsedSynthStyle pStyle = (ParsedSynthStyle)style;

            if (pStyle._painters != null) {
                _painters = pStyle._painters;
            }
        }
    }

    public SynthPainter getPainter(SynthContext ss) {
        return DELEGATING_PAINTER_INSTANCE;
    }

    public void setPainters(PainterInfo[] info) {
        _painters = info;
    }

    public DefaultSynthStyle addTo(DefaultSynthStyle style) {
        if (!(style instanceof ParsedSynthStyle)) {
            style = new ParsedSynthStyle(style);
        }
        ParsedSynthStyle pStyle = (ParsedSynthStyle)super.addTo(style);
        pStyle._painters = mergePainterInfo(pStyle._painters, _painters);
        return pStyle;
    }    

    private SynthPainter getBestPainter(SynthContext context, String method,
                                        int direction) {
        // Check the state info first
        StateInfo info = (StateInfo)getStateInfo(context.getComponentState());
        SynthPainter painter;
        if (info != null) {
            if ((painter = getBestPainter(info.getPainters(), method,
                                          direction)) != null) {
                return painter;
            }
        }
        if ((painter = getBestPainter(_painters, method, direction)) != null) {
            return painter;
        }
        return SynthPainter.NULL_PAINTER;
    }

    private SynthPainter getBestPainter(PainterInfo[] info, String method,
                                        int direction) {
        if (info != null) {
            // Painter specified with no method
            SynthPainter nullPainter = null;
            // Painter specified for this method
            SynthPainter methodPainter = null;

            for (int counter = info.length - 1; counter >= 0; counter--) {
                PainterInfo pi = info[counter];

                if (pi.getMethod() == method) {
                    if (pi.getDirection() == direction) {
                        return pi.getPainter();
                    }
                    else if (methodPainter == null &&pi.getDirection() == -1) {
                        methodPainter = pi.getPainter();
                    }
                }
                else if (nullPainter == null && pi.getMethod() == null) {
                    nullPainter = pi.getPainter();
                }
            }
            if (methodPainter != null) {
                return methodPainter;
            }
            return nullPainter;
        }
        return null;
    }

    public String toString() {
        StringBuffer text = new StringBuffer(super.toString());
        if (_painters != null) {
            text.append(",painters=[");
            for (int i = 0; i < +_painters.length; i++) {
                text.append(_painters[i].toString());
            }
            text.append("]");
        }
        return text.toString();
    }


    static class StateInfo extends DefaultSynthStyle.StateInfo {
        private PainterInfo[] _painterInfo;

        public StateInfo() {
        }

        public StateInfo(DefaultSynthStyle.StateInfo info) {
            super(info);
            if (info instanceof StateInfo) {
                _painterInfo = ((StateInfo)info)._painterInfo;
            }
        }

        public void setPainters(PainterInfo[] painterInfo) {
            _painterInfo = painterInfo;
        }

        public PainterInfo[] getPainters() {
            return _painterInfo;
        }

        public Object clone() {
            return new StateInfo(this);
        }

        public DefaultSynthStyle.StateInfo addTo(
                           DefaultSynthStyle.StateInfo info) {
            if (!(info instanceof StateInfo)) {
                info = new StateInfo(info);
            }
            else {
                info = super.addTo(info);
                StateInfo si = (StateInfo)info;
                si._painterInfo = mergePainterInfo(si._painterInfo,
                                                   _painterInfo);
            }
            return info;
        }

        public String toString() {
            StringBuffer text = new StringBuffer(super.toString());
            text.append(",painters=[");
            if (_painterInfo != null) {
                for (int i = 0; i < +_painterInfo.length; i++) {
                    text.append("    ").append(_painterInfo[i].toString());
                }
            }
            text.append("]");
            return text.toString();
        }
    }


    static class PainterInfo {
        private String _method;
        private SynthPainter _painter;
        private int _direction;

        PainterInfo(String method, SynthPainter painter, int direction) {
            if (method != null) {
                _method = method.intern();
            }
            _painter = painter;
            _direction = direction;
        }

        String getMethod() {
            return _method;
        }

        SynthPainter getPainter() {
            return _painter;
        }

        int getDirection() {
            return _direction;
        }

        private boolean equalsPainter(PainterInfo info) {
            return (_method == info._method && _direction == info._direction);
        }

        public String toString() {
            return "PainterInfo {method=" + _method + ",direction=" +
                _direction + ",painter=" + _painter +"}";
        }
    }


    private static class DelegatingPainter extends SynthPainter {
        private static SynthPainter getPainter(SynthContext context,
                                               String method, int direction) {
            return ((ParsedSynthStyle)context.getStyle()).getBestPainter(
                               context, method, direction);
        }

        public void paintArrowButtonBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "arrowButtonBackground", -1).
                paintArrowButtonBackground(context, g, x, y, w, h);
        }

        public void paintArrowButtonBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "arrowButtonBorder", -1).
                paintArrowButtonBorder(context, g, x, y, w, h);
        }

        public void paintArrowButtonForeground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "arrowButtonForeground", direction).
                paintArrowButtonForeground(context, g, x, y, w, h, direction);
        }

        public void paintButtonBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "buttonBackground", -1).
                paintButtonBackground(context, g, x, y, w, h);
        }

        public void paintButtonBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "buttonBorder", -1).
                paintButtonBorder(context, g, x, y, w, h);
        }

        public void paintCheckBoxMenuItemBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "checkBoxMenuItemBackground", -1).
                paintCheckBoxMenuItemBackground(context, g, x, y, w, h);
        }

        public void paintCheckBoxMenuItemBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "checkBoxMenuItemBorder", -1).
                paintCheckBoxMenuItemBorder(context, g, x, y, w, h);
        }

        public void paintCheckBoxBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "checkBoxBackground", -1).
                paintCheckBoxBackground(context, g, x, y, w, h);
        }

        public void paintCheckBoxBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "checkBoxBorder", -1).
                paintCheckBoxBorder(context, g, x, y, w, h);
        }

        public void paintColorChooserBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "colorChooserBackground", -1).
                paintColorChooserBackground(context, g, x, y, w, h);
        }

        public void paintColorChooserBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "colorChooserBorder", -1).
                paintColorChooserBorder(context, g, x, y, w, h);
        }

        public void paintComboBoxBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "comboBoxBackground", -1).
                paintComboBoxBackground(context, g, x, y, w, h);
        }

        public void paintComboBoxBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "comboBoxBorder", -1).
                paintComboBoxBorder(context, g, x, y, w, h);
        }

        public void paintDesktopIconBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "desktopIconBackground", -1).
                paintDesktopIconBackground(context, g, x, y, w, h);
        }

        public void paintDesktopIconBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "desktopIconBorder", -1).
                paintDesktopIconBorder(context, g, x, y, w, h);
        }

        public void paintDesktopPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "desktopPaneBackground", -1).
                paintDesktopPaneBackground(context, g, x, y, w, h);
        }

        public void paintDesktopPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "desktopPaneBorder", -1).
                paintDesktopPaneBorder(context, g, x, y, w, h);
        }

        public void paintEditorPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "editorPaneBackground", -1).
                paintEditorPaneBackground(context, g, x, y, w, h);
        }

        public void paintEditorPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "editorPaneBorder", -1).
                paintEditorPaneBorder(context, g, x, y, w, h);
        }

        public void paintFileChooserBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "fileChooserBackground", -1).
                paintFileChooserBackground(context, g, x, y, w, h);
        }

        public void paintFileChooserBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "fileChooserBorder", -1).
                paintFileChooserBorder(context, g, x, y, w, h);
        }

        public void paintFormattedTextFieldBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "formattedTextFieldBackground", -1).
                paintFormattedTextFieldBackground(context, g, x, y, w, h);
        }

        public void paintFormattedTextFieldBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "formattedTextFieldBorder", -1).
                paintFormattedTextFieldBorder(context, g, x, y, w, h);
        }

        public void paintInternalFrameTitlePaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "internalFrameTitlePaneBackground", -1).
                paintInternalFrameTitlePaneBackground(context, g, x, y, w, h);
        }

        public void paintInternalFrameTitlePaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "internalFrameTitlePaneBorder", -1).
                paintInternalFrameTitlePaneBorder(context, g, x, y, w, h);
        }

        public void paintInternalFrameBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "internalFrameBackground", -1).
                paintInternalFrameBackground(context, g, x, y, w, h);
        }

        public void paintInternalFrameBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "internalFrameBorder", -1).
                paintInternalFrameBorder(context, g, x, y, w, h);
        }

        public void paintLabelBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "labelBackground", -1).
                paintLabelBackground(context, g, x, y, w, h);
        }

        public void paintLabelBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "labelBorder", -1).
                paintLabelBorder(context, g, x, y, w, h);
        }

        public void paintListBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "listBackground", -1).
                paintListBackground(context, g, x, y, w, h);
        }

        public void paintListBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "listBorder", -1).
                paintListBorder(context, g, x, y, w, h);
        }

        public void paintMenuBarBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuBarBackground", -1).
                paintMenuBarBackground(context, g, x, y, w, h);
        }

        public void paintMenuBarBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuBarBorder", -1).
                paintMenuBarBorder(context, g, x, y, w, h);
        }

        public void paintMenuItemBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuItemBackground", -1).
                paintMenuItemBackground(context, g, x, y, w, h);
        }

        public void paintMenuItemBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuItemBorder", -1).
                paintMenuItemBorder(context, g, x, y, w, h);
        }

        public void paintMenuBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuBackground", -1).
                paintMenuBackground(context, g, x, y, w, h);
        }

        public void paintMenuBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "menuBorder", -1).
                paintMenuBorder(context, g, x, y, w, h);
        }

        public void paintOptionPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "optionPaneBackground", -1).
                paintOptionPaneBackground(context, g, x, y, w, h);
        }

        public void paintOptionPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "optionPaneBorder", -1).
                paintOptionPaneBorder(context, g, x, y, w, h);
        }

        public void paintPanelBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "panelBackground", -1).
                paintPanelBackground(context, g, x, y, w, h);
        }

        public void paintPanelBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "panelBorder", -1).
                paintPanelBorder(context, g, x, y, w, h);
        }

        public void paintPasswordFieldBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "passwordFieldBackground", -1).
                paintPasswordFieldBackground(context, g, x, y, w, h);
        }

        public void paintPasswordFieldBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "passwordFieldBorder", -1).
                paintPasswordFieldBorder(context, g, x, y, w, h);
        }

        public void paintPopupMenuBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "popupMenuBackground", -1).
                paintPopupMenuBackground(context, g, x, y, w, h);
        }

        public void paintPopupMenuBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "popupMenuBorder", -1).
                paintPopupMenuBorder(context, g, x, y, w, h);
        }

        public void paintProgressBarBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "progressBarBackground", -1).
                paintProgressBarBackground(context, g, x, y, w, h);
        }

        public void paintProgressBarBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "progressBarBorder", -1).
                paintProgressBarBorder(context, g, x, y, w, h);
        }

        public void paintProgressBarForeground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "progressBarForeground", direction).
                paintProgressBarForeground(context, g, x, y, w, h, direction);
        }

        public void paintRadioButtonMenuItemBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "radioButtonMenuItemBackground", -1).
                paintRadioButtonMenuItemBackground(context, g, x, y, w, h);
        }

        public void paintRadioButtonMenuItemBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "radioButtonMenuItemBorder", -1).
                paintRadioButtonMenuItemBorder(context, g, x, y, w, h);
        }

        public void paintRadioButtonBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "radioButtonBackground", -1).
                paintRadioButtonBackground(context, g, x, y, w, h);
        }

        public void paintRadioButtonBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "radioButtonBorder", -1).
                paintRadioButtonBorder(context, g, x, y, w, h);
        }

        public void paintRootPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "rootPaneBackground", -1).
                paintRootPaneBackground(context, g, x, y, w, h);
        }

        public void paintRootPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "rootPaneBorder", -1).
                paintRootPaneBorder(context, g, x, y, w, h);
        }

        public void paintScrollBarBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollBarBackground", -1).
                paintScrollBarBackground(context, g, x, y, w, h);
        }

        public void paintScrollBarBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollBarBorder", -1).
                paintScrollBarBorder(context, g, x, y, w, h);
        }

        public void paintScrollBarThumbBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "scrollBarThumbBackground", direction).
                paintScrollBarThumbBackground(context, g, x, y, w, h, direction);
        }

        public void paintScrollBarThumbBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "scrollBarThumbBorder", direction).
                paintScrollBarThumbBorder(context, g, x, y, w, h, direction);
        }

        public void paintScrollBarTrackBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollBarTrackBackground", -1).
                paintScrollBarTrackBackground(context, g, x, y, w, h);
        }

        public void paintScrollBarTrackBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollBarTrackBorder", -1).
                paintScrollBarTrackBorder(context, g, x, y, w, h);
        }

        public void paintScrollPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollPaneBackground", -1).
                paintScrollPaneBackground(context, g, x, y, w, h);
        }

        public void paintScrollPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "scrollPaneBorder", -1).
                paintScrollPaneBorder(context, g, x, y, w, h);
        }

        public void paintSeparatorBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "separatorBackground", -1).
                paintSeparatorBackground(context, g, x, y, w, h);
        }

        public void paintSeparatorBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "separatorBorder", -1).
                paintSeparatorBorder(context, g, x, y, w, h);
        }

        public void paintSeparatorForeground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "separatorForeground", direction).
                paintSeparatorForeground(context, g, x, y, w, h, direction);
        }

        public void paintSliderBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "sliderBackground", -1).
                paintSliderBackground(context, g, x, y, w, h);
        }

        public void paintSliderBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "sliderBorder", -1).
                paintSliderBorder(context, g, x, y, w, h);
        }

        public void paintSliderThumbBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "sliderThumbBackground", direction).
                paintSliderThumbBackground(context, g, x, y, w, h, direction);
        }

        public void paintSliderThumbBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "sliderThumbBorder", direction).
                paintSliderThumbBorder(context, g, x, y, w, h, direction);
        }

        public void paintSliderTrackBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "sliderTrackBackground", -1).
                paintSliderTrackBackground(context, g, x, y, w, h);
        }

        public void paintSliderTrackBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "sliderTrackBorder", -1).
                paintSliderTrackBorder(context, g, x, y, w, h);
        }

        public void paintSpinnerBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "spinnerBackground", -1).
                paintSpinnerBackground(context, g, x, y, w, h);
        }

        public void paintSpinnerBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "spinnerBorder", -1).
                paintSpinnerBorder(context, g, x, y, w, h);
        }

        public void paintSplitPaneDividerBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "splitPaneDividerBackground", -1).
                paintSplitPaneDividerBackground(context, g, x, y, w, h);
        }

        public void paintSplitPaneDividerForeground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "splitPaneDividerForeground", direction).
                paintSplitPaneDividerForeground(context, g, x, y, w, h, direction);
        }

        public void paintSplitPaneDragDivider(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "splitPaneDragDivider", direction).
                paintSplitPaneDragDivider(context, g, x, y, w, h, direction);
        }

        public void paintSplitPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "splitPaneBackground", -1).
                paintSplitPaneBackground(context, g, x, y, w, h);
        }

        public void paintSplitPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "splitPaneBorder", -1).
                paintSplitPaneBorder(context, g, x, y, w, h);
        }

        public void paintTabbedPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneBackground", -1).
                paintTabbedPaneBackground(context, g, x, y, w, h);
        }

        public void paintTabbedPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneBorder", -1).
                paintTabbedPaneBorder(context, g, x, y, w, h);
        }

        public void paintTabbedPaneTabAreaBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneTabAreaBackground", -1).
                paintTabbedPaneTabAreaBackground(context, g, x, y, w, h);
        }

        public void paintTabbedPaneTabAreaBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneTabAreaBorder", -1).
                paintTabbedPaneTabAreaBorder(context, g, x, y, w, h);
        }

        public void paintTabbedPaneTabBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "tabbedPaneTabBackground", -1).
                paintTabbedPaneTabBackground(context, g, x, y, w, h, direction);
        }

        public void paintTabbedPaneTabBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h, int direction) {
            getPainter(context, "tabbedPaneTabBorder", -1).
                paintTabbedPaneTabBorder(context, g, x, y, w, h, direction);
        }

        public void paintTabbedPaneContentBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneContentBackground", -1).
                paintTabbedPaneContentBackground(context, g, x, y, w, h);
        }

        public void paintTabbedPaneContentBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tabbedPaneContentBorder", -1).
                paintTabbedPaneContentBorder(context, g, x, y, w, h);
        }

        public void paintTableHeaderBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tableHeaderBackground", -1).
                paintTableHeaderBackground(context, g, x, y, w, h);
        }

        public void paintTableHeaderBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tableHeaderBorder", -1).
                paintTableHeaderBorder(context, g, x, y, w, h);
        }

        public void paintTableBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tableBackground", -1).
                paintTableBackground(context, g, x, y, w, h);
        }

        public void paintTableBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "tableBorder", -1).
                paintTableBorder(context, g, x, y, w, h);
        }

        public void paintTextAreaBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textAreaBackground", -1).
                paintTextAreaBackground(context, g, x, y, w, h);
        }

        public void paintTextAreaBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textAreaBorder", -1).
                paintTextAreaBorder(context, g, x, y, w, h);
        }

        public void paintTextPaneBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textPaneBackground", -1).
                paintTextPaneBackground(context, g, x, y, w, h);
        }

        public void paintTextPaneBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textPaneBorder", -1).
                paintTextPaneBorder(context, g, x, y, w, h);
        }

        public void paintTextFieldBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textFieldBackground", -1).
                paintTextFieldBackground(context, g, x, y, w, h);
        }

        public void paintTextFieldBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "textFieldBorder", -1).
                paintTextFieldBorder(context, g, x, y, w, h);
        }

        public void paintToggleButtonBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toggleButtonBackground", -1).
                paintToggleButtonBackground(context, g, x, y, w, h);
        }

        public void paintToggleButtonBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toggleButtonBorder", -1).
                paintToggleButtonBorder(context, g, x, y, w, h);
        }

        public void paintToolBarBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarBackground", -1).
                paintToolBarBackground(context, g, x, y, w, h);
        }

        public void paintToolBarBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarBorder", -1).
                paintToolBarBorder(context, g, x, y, w, h);
        }

        public void paintToolBarContentBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarContentBackground", -1).
                paintToolBarContentBackground(context, g, x, y, w, h);
        }

        public void paintToolBarContentBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarContentBorder", -1).
                paintToolBarContentBorder(context, g, x, y, w, h);
        }

        public void paintToolBarDragWindowBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarDragWindowBackground", -1).
                paintToolBarDragWindowBackground(context, g, x, y, w, h);
        }

        public void paintToolBarDragWindowBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolBarDragWindowBorder", -1).
                paintToolBarDragWindowBorder(context, g, x, y, w, h);
        }

        public void paintToolTipBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolTipBackground", -1).
                paintToolTipBackground(context, g, x, y, w, h);
        }

        public void paintToolTipBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "toolTipBorder", -1).
                paintToolTipBorder(context, g, x, y, w, h);
        }

        public void paintTreeBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "treeBackground", -1).
                paintTreeBackground(context, g, x, y, w, h);
        }

        public void paintTreeBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "treeBorder", -1).
                paintTreeBorder(context, g, x, y, w, h);
        }

        public void paintTreeCellBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "treeCellBackground", -1).
                paintTreeCellBackground(context, g, x, y, w, h);
        }

        public void paintTreeCellBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "treeCellBorder", -1).
                paintTreeCellBorder(context, g, x, y, w, h);
        }

        public void paintTreeCellFocus(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "treeCellFocus", -1).
                paintTreeCellFocus(context, g, x, y, w, h);
        }

        public void paintViewportBackground(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "viewportBackground", -1).
                paintViewportBackground(context, g, x, y, w, h);
        }

        public void paintViewportBorder(SynthContext context,
                     Graphics g, int x, int y, int w, int h) {
            getPainter(context, "viewportBorder", -1).
                paintViewportBorder(context, g, x, y, w, h);
        }
    }
}
