/*
 * @(#)file      DescriptorAccess.java
 * @(#)author    IBM Corp.
 * @(#)version   1.17
 * @(#)lastedit      03/12/19
 */
/*
 * Copyright IBM Corp. 1999-2000.  All rights reserved.
 * 
 * The program is provided "as is" without any warranty express or implied,
 * including the warranty of non-infringement and the implied warranties of
 * merchantibility and fitness for a particular purpose. IBM will not be
 * liable for any damages suffered by you or any third party claim against 
 * you regarding the Program.
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 * 
 * Copyright 2004 Sun Microsystems, Inc.  Tous droits reserves.
 * Ce logiciel est propriete de Sun Microsystems, Inc.
 * Distribue par des licences qui en restreignent l'utilisation. 
 *
 */


package javax.management;


/**
 * This interface is used to gain access to descriptors of the Descriptor class 
 * which are associated with a JMX component, i.e. MBean, MBeanInfo, 
 * MBeanAttributeInfo, MBeanNotificationInfo,
 * MBeanOperationInfo, MBeanParameterInfo.
 * <P>
 * ModelMBeans make extensive use of this interface in ModelMBeanInfo classes.
 *
 * @since 1.5
 */
public interface DescriptorAccess
{
    /** 
    * Returns a copy of Descriptor.
    *
    * @return Descriptor associated with the component implementing this interface.
    * Null should never be returned. At a minimum a default descriptor with the 
    * descriptor name and descriptorType should be returned.
    *
    * @see #setDescriptor
    */
    public Descriptor getDescriptor();
    
    /**
    * Sets Descriptor (full replace).
    *
    * @param inDescriptor replaces the Descriptor associated with the 
    * component implementing this interface. If the inDescriptor is invalid for the
    * type of Info object it is being set for, an exception is thrown.  If the
    * inDescriptor is null, then the Descriptor will revert to its default value
    * which should contain, at a minimum, the descriptor name and descriptorType.
    * 
    * @see #getDescriptor
    */
    public void setDescriptor(Descriptor inDescriptor);
}
