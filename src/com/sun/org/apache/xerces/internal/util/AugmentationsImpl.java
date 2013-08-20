/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.util;

import java.util.Hashtable;
import java.util.Enumeration;

import com.sun.org.apache.xerces.internal.xni.Augmentations;

/**
 * This class provides an implementation for Augmentations interface. 
 * Augmentations interface defines a hashtable of additional data that could
 * be passed along the document pipeline. The information can contain extra
 * arguments or infoset augmentations, for example PSVI. This additional
 * information is identified by a String key.
 * <p>
 * 
 * @author Elena Litani, IBM
 * @version $Id: AugmentationsImpl.java,v 1.8 2003/09/23 21:42:31 mrglavas Exp $
 */
public class AugmentationsImpl implements Augmentations{
    
    private AugmentationsItemsContainer fAugmentationsContainer =
                                        new SmallContainer();
    
    /**
     * Add additional information identified by a key to the Augmentations structure.
     * 
     * @param key    Identifier, can't be <code>null</code>
     * @param item   Additional information
     *
     * @return the previous value of the specified key in the Augmentations strucutre,
     *         or <code>null</code> if it did not have one.
     */
    public Object putItem (String key, Object item){
        Object oldValue = fAugmentationsContainer.putItem(key, item);

        if (oldValue == null && fAugmentationsContainer.isFull()) {
            fAugmentationsContainer = fAugmentationsContainer.expand();
        }

        return oldValue;
    }


    /**
     * Get information identified by a key from the Augmentations structure
     * 
     * @param key    Identifier, can't be <code>null</code>
     *
     * @return the value to which the key is mapped in the Augmentations structure;
     *         <code>null</code> if the key is not mapped to any value.
     */
    public Object getItem(String key){
        return fAugmentationsContainer.getItem(key);
    }
    
    
    /**
     * Remove additional info from the Augmentations structure
     * 
     * @param key    Identifier, can't be <code>null</code>
     */
    public Object removeItem (String key){
        return fAugmentationsContainer.removeItem(key);
    }

    /**
     * Returns an enumeration of the keys in the Augmentations structure
     *
     */
    public Enumeration keys (){
        return fAugmentationsContainer.keys();
    }

    /**
     * Remove all objects from the Augmentations structure.
     */
    public void removeAllItems() {
        fAugmentationsContainer.clear();
    }

    public String toString() {
        return fAugmentationsContainer.toString();
    }

    abstract class AugmentationsItemsContainer {
        abstract public Object putItem(Object key, Object item);
        abstract public Object getItem(Object key);
        abstract public Object removeItem(Object key);
        abstract public Enumeration keys();
        abstract public void clear();
        abstract public boolean isFull();
        abstract public AugmentationsItemsContainer expand();
    }

    class SmallContainer extends AugmentationsItemsContainer {
        final static int SIZE_LIMIT = 10;
        final Object[] fAugmentations = new Object[SIZE_LIMIT*2];
        int fNumEntries = 0;

        public Enumeration keys() {
            return new SmallContainerKeyEnumeration();
        }

        public Object getItem(Object key) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    return fAugmentations[i+1];
                }
            }

            return null;
        }

        public Object putItem(Object key, Object item) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    Object oldValue = fAugmentations[i+1];
                    fAugmentations[i+1] = item;

                    return oldValue;
                }
            }

            fAugmentations[fNumEntries*2] = key;
            fAugmentations[fNumEntries*2+1] = item;
            fNumEntries++;

            return null;
        }


        public Object removeItem(Object key) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    Object oldValue = fAugmentations[i+1];

                    for (int j = i; j < fNumEntries*2 - 2; j = j + 2) {
                        fAugmentations[j] = fAugmentations[j+2];
                        fAugmentations[j+1] = fAugmentations[j+3];
                    }

                    fAugmentations[fNumEntries*2-2] = null;
                    fAugmentations[fNumEntries*2-1] = null;
                    fNumEntries--;

                    return oldValue;
                }
            }

            return null;
        }

        public void clear() {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                fAugmentations[i] = null;
                fAugmentations[i+1] = null;
            }

            fNumEntries = 0;
        }

        public boolean isFull() {
            return (fNumEntries == SIZE_LIMIT);
        }

        public AugmentationsItemsContainer expand() {
            LargeContainer expandedContainer = new LargeContainer();

            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                expandedContainer.putItem(fAugmentations[i],
                                          fAugmentations[i+1]);
            }

            return expandedContainer;
        }

        public String toString() {
            StringBuffer buff = new StringBuffer();
            buff.append("SmallContainer - fNumEntries == " + fNumEntries);

            for (int i = 0; i < SIZE_LIMIT*2; i=i+2) {
                buff.append("\nfAugmentations[");
                buff.append(i);
                buff.append("] == ");
                buff.append(fAugmentations[i]);
                buff.append("; fAugmentations[");
                buff.append(i+1);
                buff.append("] == ");
                buff.append(fAugmentations[i+1]);
            }

            return buff.toString();
        }

        class SmallContainerKeyEnumeration implements Enumeration {
            Object [] enumArray = new Object[fNumEntries];
            int next = 0;

            SmallContainerKeyEnumeration() {
                for (int i = 0; i < fNumEntries; i++) {
                    enumArray[i] = fAugmentations[i*2];
                }
            }

            public boolean hasMoreElements() {
                return next < enumArray.length;
            }

            public Object nextElement() {
                if (next >= enumArray.length) {
                    throw new java.util.NoSuchElementException();
                }

                Object nextVal = enumArray[next];
                enumArray[next] = null;
                next++;

                return nextVal;
            }
        }
    }

    class LargeContainer extends AugmentationsItemsContainer {
        final Hashtable fAugmentations = new Hashtable();

        public Object getItem(Object key) {
            return fAugmentations.get(key);
        }

        public Object putItem(Object key, Object item) {
            return fAugmentations.put(key, item);
        }

        public Object removeItem(Object key) {
            return fAugmentations.remove(key);
        }

        public Enumeration keys() {
            return fAugmentations.keys();
        }

        public void clear() {
            fAugmentations.clear();
        }

        public boolean isFull() {
            return false;
        }

        public AugmentationsItemsContainer expand() {
            return this;
        }

        public String toString() {
            StringBuffer buff = new StringBuffer();
            buff.append("LargeContainer");
            Enumeration keys = fAugmentations.keys();

            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                buff.append("\nkey == ");
                buff.append(key);
                buff.append("; value == ");
                buff.append(fAugmentations.get(key));
            }

            return buff.toString();
        }
    }
}
