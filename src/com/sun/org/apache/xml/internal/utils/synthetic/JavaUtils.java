/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: JavaUtils.java,v 1.7 2004/02/17 04:23:24 minchau Exp $
 */

package com.sun.org.apache.xml.internal.utils.synthetic;

import java.io.IOException;

/** 
 * This class supports invoking Java compilation from within
 * a Java program. Recent versions of the Java environment have
 * provided such an API (in tools.jar). But that isn't available
 * on all platforms, and a fallback to the command line may be needed
 * (though this too may not always be available, eg. for security
 * reasons).
 * <p>
 * There's an additional complication in some environments --
 * such as Microsoft's VJ++ -- where the classpath as seen in
 * the System Properties may not be the one the user expects.
 * The code here is parameterized to try to deal with that.
 * @xsl.usage internal
 */
public class JavaUtils
{
        // One-time flag for whether we could dynamically load compiler API
        private static boolean cantLoadCompiler=false; 

        // Debug flag - generates debug stuff if true.
        private static boolean debug = false;
  
        /** Control whether compilation occurs with the -g option
         * (debugging information included in generated classfile).
         * This is an attribute, rather than a parameter on the compile
         * method, largely because it tends to be an all-or-nothing decision; 
         * generally you're either doing program development and want it,
         * or running in production mode and don't. But that may not match
         * the needs of future users...
         * <p>
         * TODO: Consider whether debug should be a parameter.
         * 
         * @param boolean newDebug True to request debugging data,
         * false to request optimized output. (It's uncommon to
         * want both or neither!)
         */ 
        public static void setDebug(boolean newDebug)
        {
            debug=newDebug;
        }

        /** Try to compile a .java file on disk. This will first attempt to
         * use the sun.java.tools.javac() method, then (if that is unavailable)
         * fall back upon shelling out to a command line and running javac
         * there.
         * <p>
         * NOTE: This must be _compiled_ with sun.java.tools.* (tools.jar)
         * available. We could change that to use reflection instead, if we
         * accept some overhead... minor compared to the cost of running the
         * compiler!
         * <p>
         * This has complications on some platforms. For example, under 
         * Microsoft Visual Java ++ (at least, as installed on my test system),
         * I found that I had to specify paths to both javac and xerces.jar
         * rather than counting on the shell's path and classpath having
         * been set to reach these. For that reason I've parameterized this
         * method with a few system properties, so you can adapt it to your
         * own system's needs without modifying the code:
         * <dl>
         * <dt>com.sun.org.apache.xml.internal.utils.synthetic.javac
         * <dd>Command line issued to invoke the compiler. Defaults to "javac",
         * which should work in most systems. In VJ++, try setting it to
         * "cmd /c %JAVA_HOME%\\bin\javac.exe"
         * <dt>com.sun.org.apache.xml.internal.utils.synthetic.moreclasspath
         * <dd>Additional classpath, to be prepended to the one retrieved from
         * java.class.path. Defaults to "" (empty). In VJ++, try setting it to
         * point to your copy of xerces.jar, which may not be found otherwise.
         * TODO: Reconsider prepend versus append!
         * </dl>
         * 
         * @param String fileName Which .java file to compile. Note that this may
         * be relative to the "current directory".
         * @param String classPath Additional places to look for classes that
         * this .java file depends upon. Becomes the javac command's
         * -classpath parameter value.
         * @return boolean True iff compilation succeeded.
         */
        public static boolean JDKcompile(String fileName, String classPath)
        {
                String moreClassPath=
                        System.getProperty("com.sun.org.apache.xml.internal.utils.synthetic.moreclasspath","")
                        .trim();
                if(moreClassPath.length()>0)
                        classPath=moreClassPath+';'+classPath;
                                                                                                  
                if (debug)
                {
                        System.err.println ("JavaEngine: Compiling " + fileName);
                        System.err.println ("JavaEngine: Classpath is " + classPath);
                }
    
                String code_option = debug ? "-g" : "-O";

                // Start by trying Sun's compiler API
            if(!cantLoadCompiler)
                {
                        String args[] = {
                                code_option,
                            "-classpath", classPath,
                                fileName
                        };
                                
//                         try
// 			    {
//                                 return new sun.tools.javac.Main(System.err, "javac").compile(args);
//                         }
//                         catch (Throwable th)
//                         {
//                                 System.err.println("INFORMATIONAL: Unable to load Java compiler API (eg tools.jar).");
//                                 System.err.println("\tSwitching to command-line invocation.");
//                                 cantLoadCompiler=true;
//                         }
                }
    
                // FALLTHRU:
                // Can't load javac() method; try shelling out to the command
                // line and invoking it via exec(). 
                String javac_command=
                        System.getProperty("com.sun.org.apache.xml.internal.utils.synthetic.javac","javac");
            String args[] = {
                        javac_command,
                        code_option,
                        "-classpath", classPath,
                        fileName
                        };
                try
                {
                        Process p=java.lang.Runtime.getRuntime().exec(args);
                        int compileOK=waitHardFor(p); // pause for debugging...
                        return compileOK==0; //0 == no error reported
                }
                catch(IOException e)
                {
                        System.err.println("ERROR: IO exception during exec(javac).");
                }
                catch(SecurityException e)
                {
                        System.err.println("ERROR: Unable to create subprocess to exec(javac).");
                }
                
                // FALLTHRU: All attempts failed.
                return false;
        }

  /** Subroutine: Like p.waitFor, but discards the InterruptedException
   * and goes right back into a wait. I don't want to report compiler
   * success or failure until it really has succeeded or failed... I think.
   * @param Process p to be waited for
   * @return the exitValue() of the process.
   */
  static int waitHardFor(java.lang.Process p)
  {
    boolean done=false;
    while(!done)
        try
        {
            p.waitFor();
            done=true;
        }
        catch(InterruptedException e)
        {
            System.err.println("(Compiler process wait interrupted and resumed)");
        }
     int ev=p.exitValue();  // Pause for debugging...
     return ev;
  }
        
}
