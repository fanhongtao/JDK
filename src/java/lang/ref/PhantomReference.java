/*
 * @(#)PhantomReference.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.ref;


/**
 * Phantom reference objects, which are enqueued after the collector
 * determines that their referents may otherwise be reclaimed.  Phantom
 * references are most often used for scheduling pre-mortem cleanup actions in
 * a more flexible way than is possible with the Java finalization mechanism.
 *
 * <p> If the garbage collector determines at a certain point in time that the
 * referent of a phantom reference is <a
 * href="package-summary.html#reachability">phantom reachable</a>, then at that
 * time or at some later time it will enqueue the reference.
 *
 * <p> In order to ensure that a reclaimable object remains so, the referent of
 * a phantom reference may not be retrieved: The <code>get</code> method of a
 * phantom reference always returns <code>null</code>.
 *
 * <p> Unlike soft and weak references, phantom references are not
 * automatically cleared by the garbage collector as they are enqueued.  An
 * object that is reachable via phantom references will remain so until all
 * such references are cleared or themselves become unreachable.
 *
 * @version  1.15, 01/23/03
 * @author   Mark Reinhold
 * @since    1.2
 */

public class PhantomReference extends Reference {

    /**
     * Returns this reference object's referent.  Because the referent of a
     * phantom reference is always inaccessible, this method always returns
     * <code>null</code>.
     *
     * @return  <code>null</code>
     */
    public Object get() {
	return null;
    }

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     *
     * @param referent the object the new phantom reference will refer to
     * @param q queue the phantom reference is registered with
     * @throws  NullPointerException  If the <code>queue</code> argument
     *                                is <code>null</code>
     */
    public PhantomReference(Object referent, ReferenceQueue q) {
	super(referent, q);
    }

}
