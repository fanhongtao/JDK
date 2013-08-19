/*
 * @(#)InitialNamingClient.java	1.50 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.corba;

import java.net.*;
import java.util.*;
import java.io.StringWriter;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.CosNaming.MinorCodes;
import com.sun.corba.se.internal.orbutil.SubcontractList;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.LocateRequestMessage;
import com.sun.corba.se.internal.iiop.messages.LocateReplyMessage;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;

import org.omg.CosNaming.*;
import com.sun.corba.se.internal.iiop.*;

/** This class encapsulates all the state and logic for listing and
 *  resolving services that are published in the initial naming (bootstrap)
 *  service. It is only called through list_initial_services() and
 *  resolve_initial_references() in ORB.java. Also, INS grammer parsing
 *  routines are in this class.
 */

public class InitialNamingClient
{
    static private final int defaultInitialServicesPort =
        ORBConstants.DEFAULT_INITIAL_PORT;

    // State for the initial services: a URL if specified by a URL,
    // otherwise a representation to invoke on,
    // a port and its default value for the endpoint, a list of
    // initial services (a cache), and a list of resolved services (a cache).
    private URL servicesURL = null;
    protected int initialServicesPort = defaultInitialServicesPort;
    private String[] listOfInitialServices;
    private java.util.Properties resolvedInitialReferences = null;

    // ORBInitRefList contains a list of corbaloc and corbaname objects
    // defined with -ORBInitRef definition.
    private java.util.Hashtable orbInitRefList;
    // contains the -ORBDefaultInitRef definition.
    private String orbDefaultInitRef;
    com.sun.corba.se.internal.corba.ORB orb;

    // Root Naming Context for default resolution of names.
    private NamingContextExt rootNamingContextExt;

    public InitialNamingClient(com.sun.corba.se.internal.corba.ORB orb) {
	this.orb = orb;
        orbInitRefList = new java.util.Hashtable();
	orbDefaultInitRef = null;
    }

    void setServicesURL(URL url) {
	servicesURL = url;
    }

    void setInitialServicesPort(int port) {
	initialServicesPort = port;
    }

    /** sets the -ORBInitRef definitions in the vector.
     */
    boolean setORBInitRefList( java.util.Vector theList ) {
	boolean status = true;
	if( theList == null ) {
	    status = false;
	} else {
       	    for( int i = 0; (i < theList.size()) && (status == true); i++ ) {
	        status = parseORBInitRef( (String) theList.elementAt(i) );
	    }
	}
	return status;
    }

    /** adds the -ORBInitRef definitions in the vector.
     */
    boolean addORBInitRef( String initRefProperty ) {
        return parseORBInitRef( initRefProperty );
    }

    /** There will be one -ORBDefaultInitRef definition, which will be set here.     */
    boolean setORBDefaultInitRef( String theOrbDefaultInitRef ) {
	orbDefaultInitRef = theOrbDefaultInitRef;
	return true;
    }

    /** This method parses all -ORBInitRef definitions and returns true or
     *  false based on the correct syntax according to INS specifications.
     */
    private boolean parseORBInitRef( String theOrbInitRef )
			throws org.omg.CORBA.BAD_PARAM
    {
	boolean status = true;
	if( (theOrbInitRef == null ) || (theOrbInitRef.length() == 0 ) )
	{
            // If the string after -ORBInitRef is null then return false.
	    status = false;
	} else {
	    String name = null;
	    String value = null;
	    int equalToIndex = 0;
	    equalToIndex = theOrbInitRef.indexOf( '=' );
	    if( equalToIndex == -1 ) {
                // If '=' is missing in the -ORBInitRef then it is wrong.
		status = false;
	    } else {
		name = theOrbInitRef.substring( 0, equalToIndex );
		value = theOrbInitRef.substring( equalToIndex + 1 );
		if( value == null || value.length() == 0 ) {
                    // If there is no string after '=' then it is wrong.
		    status = false;
		} else {
		    Object theValue = null;
		    if ( value.startsWith( "corbaloc:" ) == true ) {
                        // -ORBInitRef String starts with corbaloc:.
                        // So check the corbaloc: grammer.
			CorbaLoc theCorbaLocObject =
                            checkcorbalocGrammer( value );
			if( theCorbaLocObject != null ) {
			    theValue = theCorbaLocObject;
			} else {
                            // Could not build CorbaLoc object which means
                            // there is a syntax error.
			    status = false;
			}
		    }
		    else if ( value.startsWith( "corbaname:" ) == true ) {
                        // -ORBInitRef String starts with corbaname:.
                        // So check the corbaname: grammer.
			CorbaName theCorbaNameObject =
                            checkcorbanameGrammer( value );
			if( theCorbaNameObject != null ) {
			    theValue = theCorbaNameObject;
			} else {
                            // Could not build CorbaName object which means
                            // there is a syntax error.
			    status = false;
			}
		    }
		    else if ( value.startsWith(
			IOR.STRINGIFY_PREFIX ) == true )  {
			// If it is IOR Object store it as it is.
			theValue = value;
			status = true;
		    } else {
                        // If it does not start with corbaloc: or corbaname: or
                        // IOR: then it is worng.
			status = false;
		    }
		    if( status == true ) {
			try {
                            // If the CorbaLoc or CorbaName or IOR object is
                            // built successfully then add that to the list.
			    orbInitRefList.put( name, theValue );
			} catch( Exception e ) {
			    status = false;
			}
		    } else {
			throw new org.omg.CORBA.BAD_PARAM(
                            MinorCodes.INS_BAD_SCHEME_NAME,
                            CompletionStatus.COMPLETED_NO);
		    }
		}
	    }
	}
	return status;
     }


     /** Check whether the given 'corbaloc' is valid according to INS
      *  specification.
      *  returns CorbaLoc object which contains endpointinfo and objectKey.
      *  _REVISIT_ : Cleanup for Tiger
      */
     public CorbaLoc checkcorbalocGrammer( String value )
		throws org.omg.CORBA.BAD_PARAM
     {
	CorbaLoc theCorbaLocObject = new CorbaLoc( );
	// Get the substring after corbaloc:
	if( value != null ) {
	    try {
	        // First Clean the URL Escapes if there are any
		value = decode( value );
	    } catch( Exception e ) {
		// There is something wrong with the URL escapes
		// and hence throw the exception
		throw new org.omg.CORBA.BAD_PARAM(
		    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                    CompletionStatus.COMPLETED_NO);
	    }
            // String length of corbaloc: is 9
	    int startIndex = 9;
            int endIndex = value.indexOf( '/' );
            // BUG FIX for 4336809
            if( endIndex == -1 ) {
                // If there is no '/' then the endIndex is at the end of the URL
                endIndex = value.length();
            }
            if( endIndex == 1 )  {
                // There are no characters after corbaloc: then it is an error.
		throw new org.omg.CORBA.BAD_PARAM(
		    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                    CompletionStatus.COMPLETED_NO);
            }
            else {
		// Anything between corbaloc: and / are the host information
		// of the server where the Service Object is located
                java.util.Vector hostNames = getHostNamesFromURL(
                    value.substring( startIndex, endIndex ) );
		if( ( hostNames == null )
		  ||( hostNames.size() == 0 ) )
		{
		    // hostName is null which means the address portion
		    // of the url is incorrect
		    throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes.INS_BAD_ADDRESS,
                        CompletionStatus.COMPLETED_NO);
		}
		for( int i = 0;
                   (i < hostNames.size()) && (theCorbaLocObject != null) ; i++ )
	        {
	            String hostName = (String)hostNames.elementAt( i );
		    String host = null;
                    // 2089 is the default port number according to INS spec.
                    // GIOP 1.2 is the default version.
		    int port = 2089;
		    int major = 1;
		    int minor = 2;

		    if( hostName.startsWith( "iiop:" ) ) {
			// Check the iiop syntax
			int at = hostName.indexOf( '@' );
			if( at == -1 ) {
			    // There is no Major and Minor number
			    try {
			        host = hostName.substring( 5 );
				if( ( host == null )
			  	  ||( host.length() == 0 ) )
				{
                                    // _REVISIT_ Add a new method to throw 
                                    // BAD_PARAM exception and then invoke 
                                    // that method to throw BAD_PARAM exception

				    // hostName is null which means the address
				    // portion of the url is incorrect
				    throw new org.omg.CORBA.BAD_PARAM(
					MinorCodes.INS_BAD_ADDRESS,
                                       	CompletionStatus.COMPLETED_NO);
				} else {
                                    // A Hack to differentiate IPV6 address
                                    // from IPV4 address, Current Resolution
                                    // is to use [ ] to differentiate ipv6 host
                                    int squareBracketBeginIndex = 
                                        host.indexOf ( '[' );
                                    if( squareBracketBeginIndex != -1 ) {
                                        // ipv6Host should be enclosed in
                                        // [ ], if not it will result in a
                                        // BAD_PARAM exception
                                        String ipv6Port = getIPV6Port( host );
                                        if( ipv6Port != null ) {
                                            port = Integer.parseInt( ipv6Port );
                                        }
                                        host = getIPV6Host( host );
                                    } else { 
				        // If there is a colon, Make sure what
				        // follows after colon is indeed a 
                                        // Integer If it is not then we have an
                                        // invalid port number
				        int colon = host.indexOf( ':' );
				        if( colon != -1 ) {
				 	    port = Integer.parseInt(
					            host.substring( colon+1) );
					    host = host.substring( 0, colon );
                                       }
				    }
				}
			    } catch( Exception e ) {
				// Any kind of Exception is bad here.
                                org.omg.CORBA.BAD_PARAM exception = 
				    new org.omg.CORBA.BAD_PARAM(
				        MinorCodes.INS_BAD_ADDRESS,
                                        CompletionStatus.COMPLETED_NO);
                                exception.initCause(e);
                                throw exception;
			    }
			} else {
			    try {
				String version = hostName.substring( 5, at );
				int dot = version.indexOf('.');
				// There is a version without ., which means
                                // Malformed list
                		if (dot < 0) {
				    theCorbaLocObject = null;
				} else {
				    // Substring after iiop: and before . which
                                    // will be a major number (IIOP version).
                   		    major = Integer.parseInt(
                                            version.substring(0, dot));
                    		    minor = Integer.parseInt(
                                            version.substring(dot+1));
                                    validateGIOPVersion(major, minor);
                                    // A Hack to differentiate IPV6 address
                                    // from IPV4 address, Current Resolution
                                    // is to use [ ] to differentiate ipv6 host
                                    int squareBracketBeginIndex = 
                                        hostName.indexOf ( '[' );
                                    if( squareBracketBeginIndex != -1 ) {
                                        // hostName info conatins iiop version,
                                        // get the hostinfo using the '[' token
                                        host = hostName.substring( 
                                            squareBracketBeginIndex );
                                        // ipv6Host should be enclosed in
                                        // [ ], if not it will result in a
                                        // BAD_PARAM exception
                                        String ipv6Port = getIPV6Port( host );
                                        if( ipv6Port != null ) {
                                            port = Integer.parseInt( ipv6Port );
                                        }
                                        host = getIPV6Host( host );
                                    } else {
				        host  = hostName.substring( at+1 );
				        int colon = host.indexOf( ':' );
				        if( colon != -1 ) {
				            port = Integer.parseInt(
	                                           host.substring( colon+1) );
				            host = host.substring( 0, colon );
				        }
                                    }
				}
			    }
			    // Any kind of exception is bad here, It could be
                            // number format exception or Null String exception
			    catch( Exception e ) {
                                org.omg.CORBA.BAD_PARAM exception = 
				    new org.omg.CORBA.BAD_PARAM(
				        MinorCodes.INS_BAD_ADDRESS,
                                        CompletionStatus.COMPLETED_NO);
                                exception.initCause(e);
                                throw exception;
			    }
			}
		    }
		    else if( hostName.startsWith( "rir:" ) ) {
			//Check the rir syntax
			try {
			    String afterRIR = hostName.substring( 4 );
			    // There should not be anything after rir:, If
                            // there is something.
			    // Then we have an invalid URL
			    if( afterRIR.length() != 0 ) {
				throw new org.omg.CORBA.BAD_PARAM(
				    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                                    CompletionStatus.COMPLETED_NO);
			    } else {
				theCorbaLocObject.setRIRFlag( );
			    }
			} catch( Exception e ) {
			    // Any exception is bad here
                            org.omg.CORBA.BAD_PARAM exception = 
			        new org.omg.CORBA.BAD_PARAM(
				MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                                CompletionStatus.COMPLETED_NO);
                            exception.initCause(e);
                            throw exception;
			}
		    } else if( hostName.startsWith( ":" ) ) {
			// Check the default iiop syntax
			try {
			    // String after :
			    host = hostName.substring( 1 );
                            // It is invalid to have a url in the form
                            // corbaloc::rir:,:/<ObjectKey>
                            // Bug Fix: 4404982
                            if( ( host == null ) 
                              ||( host.length() == 0 ) ) {
                                 throw new org.omg.CORBA.BAD_PARAM(
                                     MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                                     CompletionStatus.COMPLETED_NO);
                            }
                            // @ in Hostname is invalid
                            int at = host.indexOf( '@' );
                            if( at != -1 ) {
                                try {
                                    // GIOP Version number like 1.2
                                    // will be decoded as major = 1 and
                                    // minor 2. 
                                    major = Integer.parseInt(
                                        host.substring(0, 1));
                                    minor = Integer.parseInt(
                                        host.substring(2, 3));
                                    validateGIOPVersion( major, minor );
                                    // Host Info will be after @ 
                                    host = hostName.substring( at + 2 );
                                }
                                catch( Exception e ) {
                                    // Any Exception is bad here, Possible cause
                                    // could be a malformed GIOP Version. 
                                    org.omg.CORBA.BAD_PARAM exception = 
			                new org.omg.CORBA.BAD_PARAM(
				        MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                                        CompletionStatus.COMPLETED_NO);
                                    exception.initCause(e);
                                    throw exception;
                               }
                            }
			    // If there is a colon, Make sure to check
			    // that the string after : is indeed number
			    // So that we can use it as Port Number
			    int colon = host.indexOf( ':' );
			    if( colon != -1 ) {
				port = Integer.parseInt(
				    host.substring( colon+1) );
				host = host.substring( 0, colon );
			    }
		        } catch( Exception e ) {
                            org.omg.CORBA.BAD_PARAM exception = 
			        new org.omg.CORBA.BAD_PARAM(
			        MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                                CompletionStatus.COMPLETED_NO);
                            exception.initCause(e);
                            throw exception;
			}
		    } else {
		        // Right now we are not allowing any other protocol
			// other than iiop:, rir: so raise exception indicating
                        // that the URL is malformed
			throw new org.omg.CORBA.BAD_PARAM(
			    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
              		    CompletionStatus.COMPLETED_NO);
			}
		    if( ( theCorbaLocObject != null )
		      &&( theCorbaLocObject.getRIRFlag() == false ) )
		    {
			// Add the Host information if RIR flag is set,
			// If RIR is set then it means use the internal Boot
			// Strap protocol for Key String resolution
			HostInfo newHostInfo = new HostInfo( );
			newHostInfo.setVersion( major, minor );
			newHostInfo.setHostName( host );
			newHostInfo.setPortNumber( port );
			theCorbaLocObject.addHostInfo( newHostInfo );
		    }
		}
		if( theCorbaLocObject != null ) {
		    try {
		        String keyString = null;
                        // _REVISIT_ Clean up the logic here to make it
                        // more readable.
                        // If there is nothing after corbaloc:hostinfo/
                        // then NameService is the default KeyString
                        if( value.length() <= (endIndex + 1) ) {
                            keyString = "NameService";
                        }
                        else {
		            keyString = value.substring( endIndex + 1 );
                        }
			theCorbaLocObject.setKeyString( keyString );
		     } catch( Exception e ) {
                        org.omg.CORBA.BAD_PARAM exception = 
			    new org.omg.CORBA.BAD_PARAM(
			    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                            CompletionStatus.COMPLETED_NO);
                        exception.initCause(e);
                        throw exception;
		     }
		 } else {
		     // The URL doesn't have a Slash after corbaloc or has a
                     // slash at the wrong place.
		     throw new org.omg.CORBA.BAD_PARAM(
			 MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                       	 CompletionStatus.COMPLETED_NO);
	         }
             }
         }
         return theCorbaLocObject;
     }


     /** 
      * Returns an IPV6 Host that is inside [ ] tokens. There is no validation
      * done here, if it is an incorrect IPV6 address then the request through
      * this URL results in a COMM_FAILURE, otherwise malformed list will 
      * result in BAD_PARAM exception thrown in checkcorbalocGrammer.
      */
     private String getIPV6Host( String endpointInfo ) {
          // ipv6Host should be enclosed in
          // [ ], if not it will result in a
          // BAD_PARAM exception
          int squareBracketEndIndex = endpointInfo.indexOf ( ']' );
          // get the host between [ ]
          String ipv6Host = endpointInfo.substring( 1, squareBracketEndIndex  );
          return ipv6Host;
     }

     /** 
      * Returns an IPV6 Port that is after [<ipv6>]:. There is no validation
      * done here, if it is an incorrect port then the request through
      * this URL results in a COMM_FAILURE, otherwise malformed list will 
      * result in BAD_PARAM exception thrown in checkcorbalocGrammer.
      */
     private String getIPV6Port( String endpointInfo ) 
     {
         int squareBracketEndIndex = endpointInfo.indexOf ( ']' );
         // If there is port information, then it has to be after ] bracket
         // indexOf returns the count from the index of zero as the base, so
         // equality check requires squareBracketEndIndex + 1. 
         if( (squareBracketEndIndex + 1) != (endpointInfo.length( )) ) { 
             if( endpointInfo.charAt( squareBracketEndIndex + 1 ) != ':' ) {
                  throw new RuntimeException( 
                      "Host and Port is not separated by ':'" );
             }
             // PortInformation  should be after ']:' delimiter
             // If there is an exception then it will be caught in 
             // checkcorbaGrammer method and rethrown as BAD_PARAM
             return endpointInfo.substring( squareBracketEndIndex + 2 );
         } 
         return null;
     } 

     /** Quickly check whether the given corbaname is valid
      *  return true if it is and false otherwise
      */
     public CorbaName checkcorbanameGrammer( String value )
		throws org.omg.CORBA.BAD_PARAM
     {
	CorbaName theCorbaNameObject = null;
	// First Clean the URL Escapes if there are any
	try {
	    value = decode( value );
	} catch( Exception e ) {
	    throw new org.omg.CORBA.BAD_PARAM(
		MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
               	CompletionStatus.COMPLETED_NO);
	}
	int index = value.indexOf( '#' );
	if( index != -1 ) {
	    try {
		// Append corbaloc: for Grammer check, Get the string between
		// corbaname: and # which forms the corbaloc string
		String corbalocString = "corbaloc:" +
                    value.substring( 10, index ) + "/";
		// Check the corbaloc grammer and set the returned corbaloc
		// object to the CorbaName Object
		CorbaLoc theCorbaLocObject = checkcorbalocGrammer(
                    corbalocString );
	        if( theCorbaLocObject != null ) {
		    theCorbaNameObject = new CorbaName( );
		    theCorbaNameObject.setCorbaLoc( theCorbaLocObject );
		    // String after '#' is the Stringified name used to resolve
		    // the Object reference from the rootnaming context. If
		    // the String is null then the Root Naming context is passed
		    // back
		    String StringifiedName = value.substring( index + 1 );
		    if(( StringifiedName != null )
		    && ( StringifiedName.length() != 0 ) )
		    {
			theCorbaNameObject.setStringifiedName(StringifiedName);
		    }
		}
	    } catch( Exception e ) {
		// Any kind of exception is bad here
		throw new org.omg.CORBA.BAD_PARAM(
		    MinorCodes.INS_BAD_SCHEME_SPECIFIC_PART,
                    CompletionStatus.COMPLETED_NO);
	    }
        }
        // This is the case with no # in the string
        else {
            // Build a corbaloc string to check the grammer.
            // 10 is the length of corbaname:
	    String corbalocString = "corbaloc:" +
                value.substring( 10, value.length() );
            // If the string doesnot end with a / then add one to end the
            // URL correctly
            if( corbalocString.endsWith( "/" ) != true ) {
                corbalocString = corbalocString + "/";
            }
	    // Check the corbaloc grammer and set the returned corbaloc
	    // object to the CorbaName Object
	    CorbaLoc theCorbaLocObject = checkcorbalocGrammer(
                corbalocString );
	    if( theCorbaLocObject != null ) {
	        theCorbaNameObject = new CorbaName( );
	        theCorbaNameObject.setCorbaLoc( theCorbaLocObject );
	        // If the Key String is null then the Root Naming context
                // is passed back
	        //theCorbaNameObject.setStringifiedName(null);
            }
        }
	return theCorbaNameObject;
     }

     /** resolveUsingORBInitRef is called first when ever
      *  resolve_initial_reference is called.
      *  This method, checks to see if there is an entry for the requested
      *  service name (identifier) under -ORBInitRef.
      *  If there is one, It parses and tries to resolve the reference with
      *  the given ORBInitDef definition.
      *  returns the Stringified Object reference if successful in resolving,
      *  Null is returned otherwise.
      */
     private String resolveUsingORBInitRef( String identifier ) {
	String sresult = null;
	org.omg.CORBA.Object result = null;
	Object URLValue = orbInitRefList.get( identifier );
	if( URLValue != null ) {
	    CorbaLoc CorbaLocInstance = new CorbaLoc( );
	    CorbaName CorbaNameInstance = new CorbaName( );
	    // Check whether the URLValue is a CorbaLoc object
	    if( URLValue.getClass().isInstance((Object) CorbaLocInstance ) ) {
	        sresult = resolveCorbaloc( (CorbaLoc) URLValue );
	    } else if( URLValue.getClass().isInstance((Object)
                    CorbaNameInstance ) )
	    {
	        // Check whether the URLValue is a CorbaName object
		org.omg.CORBA.Object theObject =
                    resolveCorbaname( (CorbaName) URLValue );
		try {
		    sresult = orb.object_to_string( theObject );
		} catch( Exception e ) {
                    sresult = null;
		}
	    } else {
 	        // It has to be IOR otherwise
	        sresult = (String) URLValue;
	    }
	}
	return sresult;
     }



     String resolveCorbaloc( CorbaLoc theCorbaLocObject ) {
	String sresult = null;
	// If RIR flag is true use the Bootstrap protocol
	if( theCorbaLocObject.getRIRFlag( ) == true )  {
	    sresult =
               resolveUsingBootstrapProtocol(theCorbaLocObject.getKeyString());
	} else {
	    sresult = getIORUsingHostInfo( theCorbaLocObject.getHostInfo(),
	  	theCorbaLocObject.getKeyString() );
	}
	return sresult;
     }

     private String getIORUsingHostInfo( java.util.Vector theHostInfo,
         String theKeyString )
     {
        // If there is no KeyString then it's invalid
        if( theKeyString == null ) {
            return null;
        }
	String sresult = null;
	IOR theIOR = null;
	for( int i = 0; (i < theHostInfo.size()) && (theIOR == null ); i++ ) {
	    HostInfo element = (HostInfo) theHostInfo.elementAt( i );
	    theIOR = new IOR( orb, "", element.getHostName(),
                     element.getPortNumber(), element.getMajorNumber(),
		     element.getMinorNumber(), theKeyString, null );
	    //First check whether there is a server listening for Locate Request
	    //for the given key at Host and Port specified in the URL
	    //If there is a server, then decide whether the IOR is valid or
            //not
	    theIOR = locateObject( theIOR, theKeyString.getBytes());
	    if( theIOR != null ) {
	        sresult = theIOR.stringify();
	    }
	}
	return sresult;
     }


     org.omg.CORBA.Object resolveCorbaname( CorbaName theCorbaName ) {
	org.omg.CORBA.Object result = null;
	CorbaLoc theCorbaLoc = theCorbaName.getCorbaLoc( );
	if( theCorbaLoc == null ) {
	    return null;
	}
	// Case 1 of corbaname: rir#
	if( theCorbaLoc.getRIRFlag( ) == true ) {
	    try {
		NamingContextExt theNamingContext =
		    getDefaultRootNamingContext( );
		String StringifiedName = theCorbaName.getStringifiedName( );
		if( StringifiedName == null ) {
		    // This means return the Root Naming context
		    result = theNamingContext;
		} else {
		    result = theNamingContext.resolve_str( StringifiedName );
		}
	    } catch( Exception e ) {
	        result = null;
	    }
	} else  {
	    // Case 2 of corbaname: ::hostname#
	    try {
		String sresult = getIORUsingHostInfo( theCorbaLoc.getHostInfo(),                    theCorbaLoc.getKeyString() );
		if( sresult != null ) {
		    org.omg.CORBA.Object theObject =
                        orb.string_to_object( sresult );
		    if( theObject != null ) {
		        NamingContextExt theNamingContext =
			    NamingContextExtHelper.narrow( theObject );
		        if( theNamingContext != null ) {
		            String StringifiedName =
                                theCorbaName.getStringifiedName( );
                            //Return Root Naming Context if the Stringified Name
                            // is null.
                            result = theNamingContext;
                            if( StringifiedName != null ) {
		                result =
                                    theNamingContext.resolve_str(
                                        StringifiedName );
                            }
		        }
	            }
	        }
	    } catch( Exception e ) {
	        result = null;
	    }
	}
	return result;
     }

     private java.util.Vector getHostNamesFromURL(String theHostPartOfURLString)
     {
	java.util.Vector theHosts = new java.util.Vector();
	boolean nomoreCommas = false;
	int index = 0;
	for( int i = 0; (i < theHostPartOfURLString.length())
                               && (nomoreCommas == false);  )
	{
	    int theCommaIndex = theHostPartOfURLString.indexOf( ',', index );
	    if( theCommaIndex == -1 ) {
	        // There are no more Hostnames and hence set the flag to true
		// also get the Hostname and add it into the list.
		nomoreCommas = true;
		String temp = theHostPartOfURLString.substring( index );
		theHosts.addElement( temp );
	    } else {
	        // Found a comma, which means there is a hostname after comma
		// hence get the substring mapping to one of the hostname and
		// port number.
		String temp = theHostPartOfURLString.substring( index,
			                                        theCommaIndex );
		theHosts.addElement( temp );
		index = theCommaIndex + 1;
	    }
	}
	return theHosts;
     }


     private String resolveUsingBootstrapProtocol( String identifier ) {
        String stringifiedReference = null;
        // Use bootstrap protocol
        org.omg.CORBA.Object result =
            resolve(identifier, orb.getORBInitialHost(), initialServicesPort);
        if (result != null) {
	    stringifiedReference = orb.object_to_string( result );
        }
	return stringifiedReference;
     }

     private org.omg.CORBA.Object resolveIOR( String URLValue ) {
	return null;
     }

     private NamingContextExt getDefaultRootNamingContext( ) {
	if( rootNamingContextExt == null ) {
	    try {
	        rootNamingContextExt =
	  	    NamingContextExtHelper.narrow(
		    this.resolve_initial_references( "NameService" ) );
	    } catch( Exception e ) {
	        rootNamingContextExt = null;
	    }
        }
	return rootNamingContextExt;
     }


     private String resolveUsingORBDefaultInitRef( String identifier ) {
	// If the ORBDefaultInitDef is not defined simply return null
	if( orbDefaultInitRef == null ) {
		return null;
	}
	// If the ORBDefaultInitDef is  defined as corbaloc: then create the
	// corbaloc String in the format
        // <ORBInitDefaultInitDef Param>/<Identifier>
	// and resolve it using resolveCorbaloc method
	if( orbDefaultInitRef.startsWith( "corbaloc:" ) ) {
	    String theCorbaLocString = orbDefaultInitRef + "/" + identifier;
	    CorbaLoc theCorbaLoc = checkcorbalocGrammer( theCorbaLocString );
	    if( theCorbaLoc != null ) {
	        // We are just passing the string after 'corbaloc:'
                // for resolution
		return resolveCorbaloc( theCorbaLoc );
	    } else {
		return null;
	    }
	}
	// If the ORBDefaultInitDef is  defined as corbaname: then create the
	// corbaloc String in the format
        // <ORBInitDefaultInitDef Param>#<Identifier>
	// and resolve it using resolveCorbaname method
	if( orbDefaultInitRef.startsWith( "corbaname:" ) ) {
	    String theCorbaNameString = orbDefaultInitRef + "#" + identifier;
	    CorbaName theCorbaName = checkcorbanameGrammer(theCorbaNameString);
	    if( theCorbaName != null ) {
		org.omg.CORBA.Object theObject =
		    resolveCorbaname(theCorbaName );
		if( theObject != null ) {
		    return orb.object_to_string( theObject );
		}
	    } else {
		return null;
	    }
	}
	return null;
     }

     /** Locate Object returns true, If there is a Server listening at Host and
      *  Port specified in the IOR and ObjectKey is valid. If not, then the
      *  locate request will result in GIOPLocateReplyUnknownObject, in which
      *  case a false is returned resulting in null IOR being returned.
      */
     private IOR locateObject( IOR ior, byte[] theObjectKey ) {
	IOR theNewIOR = null;
	LocateRequestMessage msg;
        com.sun.corba.se.internal.iiop.IIOPOutputStream os;
        com.sun.corba.se.internal.iiop.IIOPInputStream is;

	try {
	    ClientGIOP giop = orb.getClientGIOP();
            Connection conn = giop.getConnection( ior );
	    int id = giop.allocateRequestId() ;

            GIOPVersion requestVersion =
                GIOPVersion.chooseRequestVersion(orb, ior);
            msg = MessageBase.createLocateRequest(
                    (com.sun.corba.se.internal.iiop.ORB) orb, requestVersion,
                    id, theObjectKey);

            // This chooses the right buffering strategy for the locate msg.
            // locate msgs 1.0 & 1.1 :=> grow, 1.2 :=> stream
            os = com.sun.corba.se.internal.iiop.IIOPOutputStream.
                    createIIOPOutputStreamForLocateMsg(
                            requestVersion,
                            (com.sun.corba.se.internal.iiop.ORB) orb, conn);
            os.setMessage(msg);
            msg.write(os);
            is = conn.send(os, false);
       	    LocateReplyMessage reply;
            reply = (LocateReplyMessage) is.getMessage();
	    switch(reply.getReplyStatus()) {
	        case LocateReplyMessage.UNKNOWN_OBJECT :
	            theNewIOR = null;
    		    break;

		case LocateReplyMessage.OBJECT_FORWARD :
                case LocateReplyMessage.OBJECT_FORWARD_PERM :
                    theNewIOR = reply.getIOR();
		    break;

		case LocateReplyMessage.OBJECT_HERE :
                    theNewIOR = ior;
                    break;

		default:
                    theNewIOR = null;
                    break;
	    }

	} catch( Exception e ) {
	    theNewIOR = null;
	}
	return theNewIOR;
     }


    /**
     * Get a list of the initially available CORBA services.
     * This does not work unless an ORBInitialHost is specified during
     * initialization
     * (or unless there is an ORB running on the AppletHost) since the
     * localhostname is inaccessible to applets. If a service properties URL
     * was specified then it is used, otherwise the bootstrapping protocol is
     * used.
     * @return A list of the initial services available.
     */
    String[] list_initial_services () {
        // Call real function
        return this.cachedServices();
    }

    private Properties getServicesFromURL() {
        if ( resolvedInitialReferences == null ) {
            try {
		// Open connection
		URLConnection urlc = servicesURL.openConnection();
		java.io.InputStream is = urlc.getInputStream();

		// Create and load properties
		Properties serviceProps = new Properties();
		serviceProps.load(is);
		resolvedInitialReferences = serviceProps;

		is.close();

            } catch (java.io.IOException ex) {
		throw new org.omg.CORBA.COMM_FAILURE(ex.toString(),
	        com.sun.corba.se.internal.orbutil.MinorCodes.GET_PROPERTIES_ERROR,
		     CompletionStatus.COMPLETED_NO);
            }
        }
        return resolvedInitialReferences;
    }

    private synchronized String[] cachedServices() {
        if ( listOfInitialServices == null ) {
            String[] list = null;

            if (( listOfInitialServices==null) && (servicesURL != null)) {
                // A URL was given, so use it.
                Properties serviceProps = getServicesFromURL();

                // Create a list of strings (the keys) and copy entries
                list = new String[serviceProps.size()];
                Enumeration theKeys = serviceProps.keys();
                for (int index=0;theKeys.hasMoreElements();index++) {
                    list[index] = (String)theKeys.nextElement();
                }
            }
            else {
                list = getInitialServices(orb.getORBInitialHost(),
					  initialServicesPort);
            }
            listOfInitialServices = list;
        }
        return listOfInitialServices;
    }

    String[] getInitialServices(String host, int port) 
    {
	// REVISIT - this should be implemented using DII to avoid
	// duplicating unnecessary exception handling code.

	boolean remarshal = true;

	// Create a delegate (subcontract) to invoke on
	ClientDelegate sc = this.getInitialRep(host, port);

	// Invoke.

	InputStream is = null;

	// If there is a location forward then you will need
	// to invoke again on the updated information.
	// Just calling this same routine with the same host/port
	// does not take the location forward info into account.

	while (remarshal) {
	    remarshal = false;

	    OutputStream os = (OutputStream) sc.createRequest("list", false);

	    try {

		// The only reason null is passed is to get the version of
		// invoke used by streams.  Otherwise the PortableInterceptor
		// call stack will become unbalanced since the version of
		// invoke which only takes the stream does not call 
		// PortableInterceptor ending points.
		// Note that the first parameter is ignored inside invoke.

		is = sc.invoke((org.omg.CORBA.Object)null, os);

	    } catch (ApplicationException e) {
		throw new INTERNAL("InitialNamingClient.getInitialServices: Caught an ApplicationException.  This should never happen.");
	    } catch (RemarshalException e) {
		remarshal = true;
	    }
	}

	// Unmarshal String[].
	int length = is.read_long();
	String[] ret = new String[length];
	for (int i=0; i<length; i++)
	    ret[i] = is.read_string();
	// List all the Services registered in the Bootstrap Registry
	// + All the Services registered using ORBInitInfo
	java.util.Set orbInitKeySet = orbInitRefList.keySet();
	if( orbInitKeySet != null ) {
	    String[] newList = new String[ret.length +
					 orbInitKeySet.size() ];
	    int i;
	    for( i = 0; i < length; i++ ) {
		newList[i] = ret[i];
	    }
	    Object[] temp = orbInitKeySet.toArray();
	    for( i = length; i < (length + orbInitKeySet.size()); i++ ) {
		newList[i] = (String) temp[i];
	    }
	    return newList;
	}
	return ret;
    }

    /**
     * This creates a GIOP 1.0 IOR.
     * Used to bootstrap request as a GIOP 1.0 request.
     * This is to inter-operate with old Name Service
     * implementations from SUN. 
     */
    private ClientDelegate getInitialRep(String host, int port) {
	// Create a representation (delegate) for the initial name server.

	// Create a new IOR
	byte[] initialKey = new byte[4];
	initialKey[0] = 0x49; initialKey[1] = 0x4e;
	initialKey[2] = 0x49; initialKey[3] = 0x54;
	IOR initialIOR = new IOR(orb, "", host, port, 
				 GIOPVersion.V1_0.getMajor(),
				 GIOPVersion.V1_0.getMinor(),
				 ObjectKeyFactory.get().create(orb,initialKey),
				 null);

        // Create a subcontract
        ClientDelegate rep = new ClientDelegate(orb, initialIOR,
	    SubcontractList.defaultSubcontract);
        return rep;
    }

    /**
     * Resolve the stringified reference of one of the initially
     * available CORBA services.
     * @param identifier The stringified object reference of the
     * desired service.
     * @return An object reference for the desired service.
     * @exception InvalidName The supplied identifier is not associated
     * with a known service.
     * @exception SystemException One of a fixed set of Corba system exceptions.
     */
    org.omg.CORBA.Object resolve_initial_references(String identifier)
        throws InvalidName
    {
        return cachedInitialReferences(identifier);
    }

    private org.omg.CORBA.Object resolve(String identifier, String host,
					 int port)
    {
	// REVISIT - this should be implemented using DII to avoid
	// duplicating unnecessary exception handling code.

	boolean remarshal = true;

	// URL not specified: do bootstrapping
	ClientDelegate sc = this.getInitialRep(host, port);

	// Invoke.

	InputStream is = null;

	// If there is a location forward then you will need
	// to invoke again on the updated information.
	// Just calling this same routine with the same host/port
	// does not take the location forward info into account.

	while (remarshal) {
	    remarshal = false;

	    OutputStream os = (OutputStream) sc.createRequest("get", false);
	    os.write_string(identifier);

	    try {

		// The only reason null is passed is to get the version of
		// invoke used by streams.  Otherwise the PortableInterceptor
		// call stack will become unbalanced since the version of
		// invoke which only takes the stream does not call 
		// PortableInterceptor ending points.
		// Note that the first parameter is ignored inside invoke.

		is = sc.invoke((org.omg.CORBA.Object)null, os);
	    } catch (ApplicationException e) {
		throw new INTERNAL("InitialNamingClient.resolve: Caught an ApplicationException.  This should never happen.");
	    } catch (RemarshalException e) {
		remarshal = true;
	    }
	}

	return is.read_Object();
    }

    org.omg.CORBA.Object resolve_initial_references(String identifier,
							   String modifier)
    	throws InvalidName
    {
        int ind = modifier.indexOf(":");
        if (ind <= 0)
            throw new DATA_CONVERSION(
	        com.sun.corba.se.internal.orbutil.MinorCodes.BAD_MODIFIER,
	        CompletionStatus.COMPLETED_NO);
        try {
            String hostName = modifier.substring(0,ind);

            int port = java.lang.Integer.parseInt(modifier.substring(ind+1,
	        modifier.length()));
            return resolve_initial_references(identifier, hostName, port);

        } catch (Exception e) {
	    throw new DATA_CONVERSION(
	        com.sun.corba.se.internal.orbutil.MinorCodes.BAD_MODIFIER,
	        CompletionStatus.COMPLETED_NO);
        }

    }

    org.omg.CORBA.Object resolve_initial_references(String identifier,
       String host, int port)
       throws InvalidName
    {
        org.omg.CORBA.Object result = resolve(identifier, host, port);

        if (result == null)
            throw new InvalidName();

        return result;
    }

    private synchronized org.omg.CORBA.Object cachedInitialReferences(
                                                      String identifier)
        throws InvalidName
    {
        org.omg.CORBA.Object result = null;
        String sresult = null;

        // Check cache
        if (resolvedInitialReferences != null)
            sresult = resolvedInitialReferences.getProperty(identifier);

        // Found it?
        if (sresult == null)  {
	    // First try to resolve the identifier using -ORBInitDef definition
	    sresult = resolveUsingORBInitRef( identifier );
	    if( sresult == null ) {
		// Second try to resolve the identifier using
                // -ORBDefaultInitDef definition
		    sresult = resolveUsingORBDefaultInitRef( identifier );
	    }
	    if( sresult == null ) {
		// Last try to resolve the identifier using BootStrap protocol
		sresult = resolveUsingBootstrapProtocol( identifier );
		if( sresult == null ) {
		    // If all three paths fail, Throw an InvalidName exception
                    throw new InvalidName();
		}
	    }
	    try {
       		result = orb.string_to_object(sresult);
	    } catch( Exception e ) {
		result = null;
	    }

       	    if (resolvedInitialReferences == null)
           	resolvedInitialReferences = new java.util.Properties();

       	    // Add to cached properties
            if (sresult != null && sresult.length() > 0)
           	resolvedInitialReferences.put(identifier,sresult);
	} else  {
            // Get from cache
            result = orb.string_to_object(sresult);
        }
        return result;
    }

    /** If GIOP Version is not correct, This method throws a BAD_PARAM
      * Exception.
      */
    private void validateGIOPVersion( int major, int minor ) {
        if((major>ORBConstants.MAJORNUMBER_SUPPORTED)
         ||(minor>ORBConstants.MINORNUMBERMAX ) )
        {
	    // Bad Version Number for IIOP
	    throw new org.omg.CORBA.BAD_PARAM(
	        MinorCodes.INS_BAD_ADDRESS,
                CompletionStatus.COMPLETED_NO);
        }
    }

    /** Decode method removes URL escapes as per IETF 2386 RFP.
     */
    private String decode( String stringToDecode ) {
        StringWriter theStringWithoutEscape = new StringWriter();
        for( int i = 0; i < stringToDecode.length(); i++ )
        {
            char c = stringToDecode.charAt( i ) ;
            if( c != '%' ) {
                theStringWithoutEscape.write( c );
            } else {
                // Get the two hexadecimal digits and convert that into int
                i++;
                int Hex1 = ORBUtility.hexOf( stringToDecode.charAt(i) );
                i++;
                int Hex2 = ORBUtility.hexOf( stringToDecode.charAt(i) );
                int value = (Hex1 * 16) + Hex2;
                // Convert the integer to ASCII
                theStringWithoutEscape.write( (char) value );
           }
        }
        return theStringWithoutEscape.toString();
    }
}

