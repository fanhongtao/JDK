/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan;

/**
 * <meta name="usage" content="general"/>
 * Administrative class to keep track of the version number of
 * the Xalan release.
 * <P>This class implements the upcoming standard of having
 * org.apache.project-name.Version.getVersion() be a standard way 
 * to get version information.  This class will replace the older 
 * org.apache.xalan.processor.Version class.</P>
 * <P>See also: org/apache/xalan/res/XSLTInfo.properties for 
 * information about the version of the XSLT spec we support.</P>
 */
public class Version
{

  /**
   * Get the basic version string for the current Xalan release.
   * Version String formatted like 
   * <CODE>"<B>Xalan</B> <B>Java</B> v.r[.dd| <B>D</B>nn]"</CODE>.
   *
   * Futurework: have this read version info from jar manifest.
   *
   * @return String denoting our current version
   */
  public static String getVersion()
  {
    return getProduct()+" "+getImplementationLanguage()+" "
           +getMajorVersionNum()+"."+getReleaseVersionNum()+"."
           +( (getDevelopmentVersionNum() > 0) ? 
               ("D"+getDevelopmentVersionNum()) : (""+getMaintenanceVersionNum()));
  }

  /**
   * Print the processor version to the command line.
   *
   * @param argv command line arguments, unused.
   */
  public static void main(String argv[])
  {
    System.out.println(getVersion());
  }

  /**
   * Name of product: Xalan.
   */
  public static String getProduct()
  {
    return "Xalan";
  }

  /**
   * Implementation Language: Java.
   */
  public static String getImplementationLanguage()
  {
    return "Java";
  }

  /**
   * Major version number.
   * Version number. This changes only when there is a
   *          significant, externally apparent enhancement from
   *          the previous release. 'n' represents the n'th
   *          version.
   *
   *          Clients should carefully consider the implications
   *          of new versions as external interfaces and behaviour
   *          may have changed.
   */
  public static int getMajorVersionNum()
  {
    // return 2;
    // NOTE: In post 2.2 builds, we should remove XSLProcessorVersion
    //  and simply store this info here or in the build.xml and use 
    //  Ant's filtering capability to replace it here
    return org.apache.xalan.processor.XSLProcessorVersion.VERSION;
    
  }

  /**
   * Release Number.
   * Release number. This changes when:
   *            -  a new set of functionality is to be added, eg,
   *               implementation of a new W3C specification.
   *            -  API or behaviour change.
   *            -  its designated as a reference release.
   */
  public static int getReleaseVersionNum()
  {
    //return 2;
    return org.apache.xalan.processor.XSLProcessorVersion.RELEASE;
  }

  /**
   * Maintenance Drop Number.
   * Optional identifier used to designate maintenance
   *          drop applied to a specific release and contains
   *          fixes for defects reported. It maintains compatibility
   *          with the release and contains no API changes.
   *          When missing, it designates the final and complete
   *          development drop for a release.
   */
  public static int getMaintenanceVersionNum()
  {
    //return 0;
    return org.apache.xalan.processor.XSLProcessorVersion.MAINTENANCE;
  }

  /**
   * Development Drop Number.
   * Optional identifier designates development drop of
   *          a specific release. D01 is the first development drop
   *          of a new release.
   *
   *          Development drops are works in progress towards a
   *          compeleted, final release. A specific development drop
   *          may not completely implement all aspects of a new
   *          feature, which may take several development drops to
   *          complete. At the point of the final drop for the
   *          release, the D suffix will be omitted.
   *
   *          Each 'D' drops can contain functional enhancements as
   *          well as defect fixes. 'D' drops may not be as stable as
   *          the final releases.
   */
  public static int getDevelopmentVersionNum()
  {
    //return 13;
    return org.apache.xalan.processor.XSLProcessorVersion.DEVELOPMENT;
  }
}
