/*
 * @(#)FlavorEvent.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.datatransfer;

import java.util.EventObject;


/**
 * <code>FlavorEvent</code> is used to notify interested parties
 * that available {@link DataFlavor}s have changed in the
 * {@link Clipboard} (the event source).
 *
 * @see FlavorListener
 *
 * @version 1.2 12/19/03
 * @author Alexander Gerasimov
 * @since 1.5
 */
public class FlavorEvent extends EventObject {
    /**
     * Constructs a <code>FlavorEvent</code> object.
     *
     * @param source  the <code>Clipboard</code> that is the source of the event
     */
    public FlavorEvent(Clipboard source) {
        super(source);
    }
}
