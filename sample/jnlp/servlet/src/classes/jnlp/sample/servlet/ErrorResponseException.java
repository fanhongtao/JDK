/*
 * @(#)ErrorResponseException.java	1.3 03/01/23
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package jnlp.sample.servlet;

/** An exception that holds a DownloadResponse object.
 *  This exception can be thrown with the content describing
 *  the message that should be returned in the HTTP respond
 */
public class ErrorResponseException extends Exception {
    private DownloadResponse _downloadResponse;
    
    public ErrorResponseException(DownloadResponse downloadResponse) {
	_downloadResponse = downloadResponse;
    }
    
    public DownloadResponse getDownloadResponse() { return _downloadResponse; }        
    
    public String toString() { return _downloadResponse.toString(); }
}

