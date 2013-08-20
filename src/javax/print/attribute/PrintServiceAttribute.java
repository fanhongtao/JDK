/*
 * @(#)PrintServiceAttribute.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.print.attribute;

/**
 * Interface PrintServiceAttribute is a tagging interface which a printing 
 * attribute class implements to indicate the attribute describes the status 
 * of a Print Service or some other characteristic of a Print Service. A Print 
 * Service instance adds a number of PrintServiceAttributes to a Print
 * service's attribute set to report the Print Service's status. 
 * <P>
 *
 * @see PrintServiceAttributeSet
 *
 * @author  Alan Kaminsky
 */
public interface PrintServiceAttribute extends Attribute {
}
