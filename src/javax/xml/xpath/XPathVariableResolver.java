// $Id: XPathVariableResolver.java,v 1.6 2003/12/08 04:40:47 jsuttor Exp $

/*
 * @(#)XPathVariableResolver.java	1.5 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.xpath;

import javax.xml.namespace.QName;

/**
 * <p><code>XPathVariableResolver</code> provides access to the set of user defined XPath variables.</p>
 *
 * <p>The <code>XPathVariableResolver</code> and the XPath evaluator must adhere to a contract that
 * cannot be directly enforced by the API.  Although variables may be mutable,
 * that is, an application may wish to evaluate the same XPath expression more
 * than once with different variable values, in the course of evaluating any
 * single XPath expression, a variable's value <strong><em>must</em></strong> be immutable.</p>
 *
 * @author  <a href="mailto:Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author  <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.6 $, $Date: 2003/12/08 04:40:47 $
 * @since 1.5
 */
public interface XPathVariableResolver {
  /** 
   * <p>Find a variable in the set of available variables.</p>
   * 
   * <p>If <code>variableName</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
   * 
   * @param variableName The <code>QName</code> of the variable name.
   * 
   * @return The variables value, or <code>null</code> if no variable named <code>variableName</code>
   *   exists.  The value returned must be of a type appropriate for the underlying object model.
   * 
   * @throws NullPointerException If <code>variableName</code> is <code>null</code>.
   */
  public Object resolveVariable(QName variableName);
}
