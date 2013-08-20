/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: UnionIterator.java,v 1.18 2004/02/16 22:54:59 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

/**
 * UnionIterator takes a set of NodeIterators and produces
 * a merged NodeSet in document order with duplicates removed
 * The individual iterators are supposed to generate nodes
 * in document order
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public final class UnionIterator extends DTMAxisIteratorBase {
    /** wrapper for NodeIterators to support iterator
	comparison on the value of their next() method
    */
    final private DOM _dom;

    private final static class LookAheadIterator {
	public int node, markedNode;
	public DTMAxisIterator iterator;
	public boolean isStartSet = false;
		
	public LookAheadIterator(DTMAxisIterator iterator) {
	    this.iterator = iterator;
	}
		
	public int step() {
	    node = iterator.next();
	    return node;
	}

	public LookAheadIterator cloneIterator() {
	    final LookAheadIterator clone = 
		 new LookAheadIterator(iterator.cloneIterator());
	    clone.node = node;
	    clone.markedNode = node;
	    return clone;
	}

	public void setMark() {
	    markedNode = node;
	    iterator.setMark();
	}

	public void gotoMark() {
	    node = markedNode;
	    iterator.gotoMark();
	}

    } // end of LookAheadIterator

    private static final int InitSize = 8;
  
    private int            _heapSize = 0;
    private int            _size = InitSize;
    private LookAheadIterator[] _heap = new LookAheadIterator[InitSize];
    private int            _free = 0;
  
    // last node returned by this UnionIterator to the caller of next
    // used to prune duplicates
    private int _returnedLast;

    // cached returned last for use in gotoMark
    private int _cachedReturnedLast = END;
    // cached heap size for use in gotoMark
    private int _cachedHeapSize;

    public UnionIterator(DOM dom) {
	_dom = dom;
    }


    public DTMAxisIterator cloneIterator() {
	_isRestartable = false;
	final LookAheadIterator[] heapCopy = 
	    new LookAheadIterator[_heap.length];
	try {
	    final UnionIterator clone = (UnionIterator)super.clone();
            for (int i = 0; i < _free; i++) {
                heapCopy[i] = _heap[i].cloneIterator();
            }
	    clone.setRestartable(false);
	    clone._heap = heapCopy;
	    return clone.reset();
	} 
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    public UnionIterator addIterator(DTMAxisIterator iterator) {
	if (_free == _size) {
	    LookAheadIterator[] newArray = new LookAheadIterator[_size *= 2];
	    System.arraycopy(_heap, 0, newArray, 0, _free);
	    _heap = newArray;
	}
	_heapSize++;
	_heap[_free++] = new LookAheadIterator(iterator);
	return this;
    }
  
    public int next() {
	while (_heapSize > 0) {
	    final int smallest = _heap[0].node;
	    if (smallest == END) { // iterator _heap[0] is done
		if (_heapSize > 1) {
		    // Swap first and last (iterator must be restartable)
		    final LookAheadIterator temp = _heap[0];
		    _heap[0] = _heap[--_heapSize];
		    _heap[_heapSize] = temp;
		}
		else {
		    return END;
		}
	    }
	    else if (smallest == _returnedLast) {	// duplicate
		_heap[0].step(); // value consumed
	    }
	    else {
		_heap[0].step(); // value consumed
		heapify(0);
		return returnNode(_returnedLast = smallest);
	    }
	    // fallthrough if not returned above
	    heapify(0);
	}
	return END;
    }
  
    public DTMAxisIterator setStartNode(int node) {
	if (_isRestartable) {
	    _startNode = node;
	    for (int i = 0; i < _free; i++) {
         	if(!_heap[i].isStartSet){
        	   _heap[i].iterator.setStartNode(node);
        	   _heap[i].step();	// to get the first node
        	   _heap[i].isStartSet = true;
        	}
	    }
	    // build heap
	    for (int i = (_heapSize = _free)/2; i >= 0; i--) {
		heapify(i);
	    }
	    _returnedLast = END;
	    return resetPosition();
	}
	return this;
    }
	
    /* Build a heap in document order. put the smallest node on the top. 
     * "smallest node" means the node before other nodes in document order
     */
    private void heapify(int i) {
	for (int r, l, smallest;;) {
	    r = (i + 1) << 1; l = r - 1;
	    smallest = l < _heapSize 
		&& _dom.lessThan(_heap[l].node, _heap[i].node) ? l : i;
	    if (r < _heapSize && _dom.lessThan(_heap[r].node,
					       _heap[smallest].node)) {
		smallest = r;
	    }
	    if (smallest != i) {
		final LookAheadIterator temp = _heap[smallest];
		_heap[smallest] = _heap[i];
		_heap[i] = temp;
		i = smallest;
	    }
	    else
		break;
	}
    }

    public void setMark() {
	for (int i = 0; i < _free; i++) {
	    _heap[i].setMark();
	}
	_cachedReturnedLast = _returnedLast;    
	_cachedHeapSize = _heapSize;
    }

    public void gotoMark() {
	for (int i = 0; i < _free; i++) {
	    _heap[i].gotoMark();
	}
	// rebuild heap after call last() function. fix for bug 20913
	for (int i = (_heapSize = _cachedHeapSize)/2; i >= 0; i--) {
	    heapify(i);
	}
    _returnedLast = _cachedReturnedLast;    
    }

    public DTMAxisIterator reset() {
	for (int i = 0; i < _free; i++) {
	    _heap[i].iterator.reset();
	    _heap[i].step();
	}
	// build heap
	for (int i = (_heapSize = _free)/2; i >= 0; i--) {
	    heapify(i);
	}
	_returnedLast = END;
	return resetPosition();
    }

}
