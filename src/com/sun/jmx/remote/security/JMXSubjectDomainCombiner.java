/*
 * @(#)JMXSubjectDomainCombiner.java	1.7 04/05/27
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;

/**
 * <p>This class represents an extension to the {@link SubjectDomainCombiner}
 * and is used to add a new {@link ProtectionDomain}, comprised of a null
 * codesource/signers and an empty permission set, to the access control
 * context with which this combiner is combined.</p>
 *
 * <p>When the {@link #combine} method is called the {@link ProtectionDomain}
 * is augmented with the permissions granted to the set of principals present
 * in the supplied {@link Subject}.</p>
 */
public class JMXSubjectDomainCombiner extends SubjectDomainCombiner {

    public JMXSubjectDomainCombiner(Subject s) {
        super(s);
    }

    public ProtectionDomain[] combine(ProtectionDomain[] current,
                                      ProtectionDomain[] assigned) {
        // Add a new ProtectionDomain with the null codesource/signers, and
        // the empty permission set, to the end of the array containing the
	// 'current' protections domains, i.e. the ones that will be augmented
	// with the permissions granted to the set of principals present in
	// the supplied subject.
	//
        ProtectionDomain[] newCurrent;
        if (current == null || current.length == 0) {
            newCurrent = new ProtectionDomain[1];
            newCurrent[0] = pdNoPerms;
        } else {
            newCurrent = new ProtectionDomain[current.length + 1];
            for (int i = 0; i < current.length; i++) {
                newCurrent[i] = current[i];
            }
            newCurrent[current.length] = pdNoPerms;          
        }
        return super.combine(newCurrent, assigned);
    }

    /**
     * A null CodeSource.
     */
    private static final CodeSource nullCodeSource =
	new CodeSource(null, (java.security.cert.Certificate[]) null);

    /**
     * A ProtectionDomain with a null CodeSource and an empty permission set.
     */
    private static final ProtectionDomain pdNoPerms =
	new ProtectionDomain(nullCodeSource, new Permissions());

    /**
     * A permission set that grants AllPermission.
     */
    private static final Permissions allPermissions = new Permissions();
    static {
	allPermissions.add(new AllPermission());
    }

    /**
     * A ProtectionDomain with a null CodeSource and a permission set that
     * grants AllPermission.
     */
    private static final ProtectionDomain pdAllPerms =
	new ProtectionDomain(nullCodeSource, allPermissions);

    /**
     * An AccessControlContext that has only system domains on the stack.
     */
    private static final AccessControlContext systemACC =
	new AccessControlContext(new ProtectionDomain[0]);

    /**
     * Check if the given AccessControlContext contains only system domains.
     */
    private static boolean hasOnlySystemCode(AccessControlContext acc) {
	return systemACC.equals(acc);
    }

    /**
     * Get the current AccessControlContext. If all the protection domains
     * in the current context are system domains then build a new context
     * that combines the subject with a dummy protection domain that forces
     * the use of the domain combiner.
     */
    public static AccessControlContext getContext(Subject subject) {
	AccessControlContext currentACC = AccessController.getContext();
	if (hasOnlySystemCode(currentACC)) {
	    currentACC =
		new AccessControlContext(new ProtectionDomain[] {pdAllPerms});
	}
	return new AccessControlContext(currentACC,
					new JMXSubjectDomainCombiner(subject));
    }
}
