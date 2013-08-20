// $Id: SchemaFactoryLoader.java,v 1.6 2004/03/17 03:58:54 jsuttor Exp $

/*
 * @(#)SchemaFactoryLoader.java	1.3 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.validation;

/**
 * <p>Factory that creates {@link SchemaFactory}.</p>
 * 
 * <p>
 * This class is intended to be used by the implementations of
 * the validation API, not by users.
 * </p>
 * 
 * @author <a href="Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.6 $, $Date: 2004/03/17 03:58:54 $
 * @since 1.5
 */
public abstract class SchemaFactoryLoader {
    
    /**
     * A do-nothing constructor.
     */
    protected SchemaFactoryLoader() {
    }
    
    /**
     * Creates a new {@link SchemaFactory} object for the specified
     * schema language.
     * 
     * @param schemaLanguage
     *      See <a href="../SchemaFactory.html#schemaLanguage">
     *      the list of available schema languages</a>.
     * 
     * @throws NullPointerException
     *      If the <tt>schemaLanguage</tt> parameter is null.
     * 
     * @return <code>null</code> if the callee fails to create one.
     */
    public abstract SchemaFactory newFactory(String schemaLanguage);
}
