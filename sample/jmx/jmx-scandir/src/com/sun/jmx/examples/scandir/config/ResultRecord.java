/*
 * ResultRecord.java
 *
 * Created on 16 juillet 2006, 21:33
 *
 * @(#)ResultRecord.java	1.3 06/08/02
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.jmx.examples.scandir.config;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import com.sun.jmx.examples.scandir.config.DirectoryScannerConfig.Action;
import java.io.File;
import java.util.Arrays;

/**
 * The <code>ResultRecord</code> Java Bean is used to write the 
 * results of a directory scan to a result log.
 * 
 * <p>
 * This class is annotated for XML binding.
 * </p> 
 * 
 * @author Sun Microsystems, 2006 - All rights reserved.
 */
@XmlRootElement(name="ResultRecord",namespace=XmlConfigUtils.NAMESPACE)
public class ResultRecord {
    
    /**
     * The name of the file for which this result record is built.
     */
    private String filename;

    /**
     * The Date at which this result was obtained.
     */
    private Date date;

    /**
     * The short name of the directory scanner which performed the operation.
     * @see DirectoryScannerConfig#getName()
     */
    private String directoryScanner;

    /**
     * The list of actions that were successfully carried out.
     */
    private Action[] actions;

    /**
     * Creates a new empty instance of ResultRecord.
     */
    public ResultRecord() {
    }
    
    /**
     * Creates a new instance of ResultRecord.
     * @param scan The DirectoryScannerConfig for which this result was 
     *        obtained.
     * @param actions The list of actions that were successfully carried out.
     * @param f The file for which these actions were successfully carried out.
     */
    public ResultRecord(DirectoryScannerConfig scan, Action[] actions,
                     File f) {
        directoryScanner = scan.getName();
        this.actions = actions;
        date = new Date();
        filename = f.getAbsolutePath();
    }

    /**
     * Gets the name of the file for which this result record is built.
     * @return The name of the file for which this result record is built.
     */
    @XmlElement(name="Filename",namespace=XmlConfigUtils.NAMESPACE)
    public String getFilename() {
        return this.filename;
    }

    /**
     * Sets the name of the file for which this result record is being built.
     * @param filename the name of the file for which this result record is 
     *        being built.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the Date at which this result was obtained.
     * @return the Date at which this result was obtained.
     */
    @XmlElement(name="Date",namespace=XmlConfigUtils.NAMESPACE)
    public Date getDate() {
        synchronized(this) {
            return (date==null)?null:(new Date(date.getTime()));
        }
    }

    /**
     * Sets the Date at which this result was obtained.
     * @param date the Date at which this result was obtained.
     */
    public void setDate(Date date) {
        synchronized (this) {
            this.date = (date==null)?null:(new Date(date.getTime()));
        }
    }

    /**
     * Gets the short name of the directory scanner which performed the 
     * operation.
     * @see DirectoryScannerConfig#getName()
     * @return the short name of the directory scanner which performed the 
     * operation.
     */
    @XmlElement(name="DirectoryScanner",namespace=XmlConfigUtils.NAMESPACE)
    public String getDirectoryScanner() {
        return this.directoryScanner;
    }

    /**
     * Sets the short name of the directory scanner which performed the 
     * operation.
     * @see DirectoryScannerConfig#getName()
     * @param directoryScanner the short name of the directory scanner which 
     * performed the operation.
     */
    public void setDirectoryScanner(String directoryScanner) {
        this.directoryScanner = directoryScanner;
    }

    /**
     * Gets the list of actions that were successfully carried out.
     * @return the list of actions that were successfully carried out.
     */
    @XmlElement(name="Actions",namespace=XmlConfigUtils.NAMESPACE)
    @XmlList
    public Action[] getActions() {
        return (actions == null)?null:actions.clone();
    }

    /**
     * Sets the list of actions that were successfully carried out.
     * @param actions the list of actions that were successfully carried out.
     */
    public void setActions(Action[] actions) {
        this.actions = (actions == null)?null:actions.clone();
    }
    
    // Used for equality
    private Object[] toArray() {
        final Object[] thisconfig = {
            filename, date, directoryScanner, actions
        };
        return thisconfig;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultRecord)) return false;
        return Arrays.deepEquals(toArray(),((ResultRecord)o).toArray());
    }
    
    @Override 
    public int hashCode() {
        return Arrays.deepHashCode(toArray());
    }
}
