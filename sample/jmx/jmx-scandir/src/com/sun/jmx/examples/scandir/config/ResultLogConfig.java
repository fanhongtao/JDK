/*
 * ResultLogConfig.java
 *
 * Created on July 17, 2006, 5:13 PM
 *
 * @(#)ResultLogConfig.java	1.3 06/08/02
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

import java.util.Arrays;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The <code>ResultLogConfig</code> Java Bean is used to model
 * the initial configuration of the {@link 
 * com.sun.jmx.examples.scandir.ResultLogManagerMXBean}.
 * 
 * <p>
 * This class is annotated for XML binding.
 * </p> 
 * 
 * @author Sun Microsystems, 2006 - All rights reserved.
 */
@XmlRootElement(name="ResultLogConfig",
        namespace=XmlConfigUtils.NAMESPACE)
public class ResultLogConfig {
    
    //
    // A logger for this class.
    //
    // private static final Logger LOG =
    //        Logger.getLogger(ResultLogConfig.class.getName());
    
    /**
     * The path to the result log file. {@code null} means that logging to
     * file is disabled.
     */
    private String logFileName;

    /**
     * Maximum number of record that will be logged in the log file before
     * switching to a new log file.
     */
    private long logFileMaxRecords;

    /**
     * The maximum number of records that can be contained in the memory log.
     * When this number is reached, the memory log drops its eldest record
     * to make way for the new one.
     */
    private int memoryMaxRecords;

    /**
     * Creates a new instance of ResultLogConfig
     */
    public ResultLogConfig() {
    }

    /**
     * Gets the path to the result log file. {@code null} means that logging to
     * file is disabled.
     * @return the path to the result log file.
     */
    @XmlElement(name="LogFileName",namespace=XmlConfigUtils.NAMESPACE)
    public String getLogFileName() {
        return this.logFileName;
    }

    /**
     * Sets the path to the result log file. {@code null} means that 
     * logging to file is disabled.
     * @param logFileName the path to the result log file.
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    /**
     * Gets the maximum number of record that will be logged in the log file
     * before switching to a new log file.
     * A 0 or negative value means no limit.
     * @return the maximum number of record that will be logged in the log file.
     */
    @XmlElement(name="LogFileMaxRecords",namespace=XmlConfigUtils.NAMESPACE)
    public long getLogFileMaxRecords() {
        return this.logFileMaxRecords;
    }

    /**
     * Sets the maximum number of record that will be logged in the log file
     * before switching to a new log file.
     * A 0 or negative value means no limit.
     * @param logFileMaxRecords the maximum number of record that will be 
     * logged in the log file.
     */
    public void setLogFileMaxRecords(long logFileMaxRecords) {
        this.logFileMaxRecords = logFileMaxRecords;
    }

    /**
     * Gets the maximum number of records that can be contained in the memory 
     * log.
     * When this number is reached, the memory log drops its eldest record
     * to make way for the new one.
     * @return the maximum number of records that can be contained in the 
     * memory log.
     */
    @XmlElement(name="MemoryMaxRecords",namespace=XmlConfigUtils.NAMESPACE)
    public int getMemoryMaxRecords() {
        return this.memoryMaxRecords;
    }

    /**
     * Sets the maximum number of records that can be contained in the memory 
     * log.
     * When this number is reached, the memory log drops its eldest record
     * to make way for the new one.
     * @param memoryMaxRecords the maximum number of records that can be 
     * contained in the memory log.
     */
    public void setMemoryMaxRecords(int memoryMaxRecords) {
        this.memoryMaxRecords = memoryMaxRecords;
    }
    
    private Object[] toArray() {
        final Object[] thisconfig = {
            memoryMaxRecords,logFileMaxRecords,logFileName
        };
        return thisconfig;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ResultLogConfig)) return false;
        final ResultLogConfig other = (ResultLogConfig)o;
        return Arrays.deepEquals(toArray(),other.toArray());
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(toArray());
    }
}
