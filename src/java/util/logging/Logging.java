/*
 * @(#)Logging.java	1.5 04/04/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.logging;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import sun.management.MXBeanSupport;

/** 
 * Logging is the implementation class of LoggingMXBean.
 *
 * The <tt>LoggingMXBean</tt> interface provides a standard
 * method for management access to the individual
 * java.util.Logger objects available at runtime.
 * 
 * @author Ron Mann
 * @author Mandy Chung
 * @version 1.5, 04/18/04
 * @since 1.5
 *
 * @see javax.management
 * @see java.util.Logger
 * @see java.util.LogManager
 */
class Logging extends MXBeanSupport implements LoggingMXBean {

    private static LogManager logManager = LogManager.getLogManager();

    /** Contructor of Logging which is the implementation class
     *  of LoggingMXBean.
     */
    Logging() { 
        super(LoggingMXBean.class);
    }
 
    public List<String> getLoggerNames() {
        Enumeration loggers = logManager.getLoggerNames();
        ArrayList<String> array = new ArrayList<String>();

        for (; loggers.hasMoreElements();) {
            array.add((String) loggers.nextElement());
        }
        return array;
    }

    private static String EMPTY_STRING = "";
    public String getLoggerLevel(String loggerName) {
        Logger l = logManager.getLogger(loggerName);
        if (l == null) {
            return null;
        }

        Level level = l.getLevel();
        if (level == null) {
            return EMPTY_STRING;
        } else {
            return level.getName();
        }
    }

    public void setLoggerLevel(String loggerName, String levelName) {
        if (loggerName == null) {
            throw new NullPointerException("loggerName is null");
        }

        Logger logger = logManager.getLogger(loggerName);
        
        if (logger == null) {
            throw new IllegalArgumentException("Logger " + loggerName +
                "does not exist");
        }
 
        Level level = null; 
        if (levelName != null) {
            // parse will throw IAE if logLevel is invalid 
            level = Level.parse(levelName);
        }

        logger.setLevel(level);
    }

    public String getParentLoggerName( String loggerName ) {
        Logger l = logManager.getLogger( loggerName );
        if (l == null) {
            return null;
        }

        Logger p = l.getParent();        
        if (p == null) {
            // root logger
            return EMPTY_STRING;
        } else {
            return p.getName();
        }
    }

}
