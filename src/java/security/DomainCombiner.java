/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * A <code>DomainCombiner</code> provides a means to dynamically
 * update the ProtectionDomains associated with the current
 * <code>AccessControlContext</code>.
 *
 * <p> A <code>DomainCombiner</code> is passed as a parameter to the
 * appropriate constructor for <code>AccessControlContext</code>.
 * The newly constructed context is then passed to the
 * <code>AccessController.doPrivileged(..., context)</code> method
 * to bind the provided context (and associated <code>DomainCombiner</code>)
 * with the current execution Thread.  Subsequent calls to
 * <code>AccessController.getContext</code> or
 * <code>AccessController.checkPermission</code>
 * cause the <code>DomainCombiner.combine</code> to get invoked.
 *
 * <p> The <code>combine</code> method takes two arguments.
 * The ProtectionDomains on the current execution Thread, since the
 * most recent call to <code>AccessController.doPrivileged</code>,
 * get passed to the first argument in an array.
 * If no call to <code>doPrivileged</code> was made, then all the
 * ProtectionDomains from the current execution Thread get passed
 * to the first argument.  The ProtectionDomains inherited
 * from the parent Thread get passed to the second argument,
 * unless a call to doPrivileged(..., <i>context</i>)
 * had occurred.  In that case, the ProtectionDomains from the
 * privileged <i>context</i> are passed to the second argument.
 *
 * <p> The <code>combine</code> method investigates the two input arrays
 * of ProtectionDomains and returns a single array containing the updated
 * ProtectionDomains.  In the simplest case, the <code>combine</code>
 * method merges the two stacks into one.  In more complex cases,
 * the <code>combine</code> method returns a modified
 * stack of ProtectionDomains.  The modification may have added new
 * ProtectionDomains, removed certain ProtectionDomains, or simply
 * updated existing ProtectionDomains.  Re-ordering and other optimizations
 * to the ProtectionDomains are also permitted.  Typically the
 * <code>combine</code> method bases its updates on the information
 * encapsulated in the <code>DomainCombiner</code>.
 *
 * <p> After the <code>AccessController.getContext</code> method
 * receives the combined stack of ProtectionDomains back from
 * the <code>DomainCombiner</code>, it returns a new
 * AccessControlContext that has both the combined ProtectionDomains
 * as well as the <code>DomainCombiner</code>.
 * 
 * @see AccessController
 * @see AccessControlContext
 * @version 1.4, 02/06/02
 */
public interface DomainCombiner {

    /**
     * Modify or update the provided ProtectionDomains.
     * ProtectionDomains may be added to or removed from the given
     * ProtectionDomains.  The ProtectionDomains may be re-ordered.
     * Individual ProtectionDomains may be may be modified (with a new
     * set of Permissions, for example).
     *
     * <p>
     *
     * @param currentDomains the ProtectionDomains associated with the
     *		current execution Thread, up to the most recent
     *		privileged <code>ProtectionDomain</code>.
     *		The ProtectionDomains are are listed in order of execution,
     *		with the most recently executing <code>ProtectionDomain</code>
     *		residing at the beginning of the array. This parameter may
     *		be <code>null</code> if the current execution Thread
     *		has no associated ProtectionDomains.<p>
     *
     * @param assignedDomains the ProtectionDomains inherited from the
     *		parent Thread, or the ProtectionDomains from the
     *		privileged <i>context</i>, if a call to
     *		AccessController.doPrivileged(..., <i>context</i>)
     *		had occurred  This parameter may be <code>null</code>
     *		if there were no ProtectionDomains inherited from the
     *		parent Thread, or from the privileged <i>context</i>.
     *
     * @return a new array consisting of the updated ProtectionDomains,
     *		or <code>null</code>.
     */
    ProtectionDomain[] combine(ProtectionDomain[] currentDomains,
				ProtectionDomain[] assignedDomains);
}
