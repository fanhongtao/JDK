package org.apache.xalan.templates;

/**
 * A class that implements this interface will call a XSLTVisitor 
 * for itself and members within it's heararchy.  If the XSLTVistor's 
 * method returns false, the sub-member heararchy will not be 
 * traversed.
 */
public interface XSLTVisitable
{
	/**
	 * This will traverse the heararchy, calling the visitor for 
	 * each member.  If the called visitor method returns 
	 * false, the subtree should not be called.
	 * 
	 * @param visitor The visitor whose appropriate method will be called.
	 */
	public void callVisitors(XSLTVisitor visitor);
}

