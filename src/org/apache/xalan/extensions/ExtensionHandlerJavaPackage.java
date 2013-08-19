/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
package org.apache.xalan.extensions;

import java.util.Hashtable;
import java.util.Vector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

//import org.w3c.dom.Element;
//import org.w3c.dom.Node;

import org.apache.xml.dtm.DTM;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.utils.QName;

import org.apache.xpath.objects.XObject;
import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * Represents an extension namespace for XPath that handles java packages
 * that may be fully or partially specified.
 * It is recommended that the class URI be of one of the following forms:
 * <pre>
 *   xalan://partial.class.name
 *   xalan://
 *   http://xml.apache.org/xalan/java (which is the same as xalan://)
 * </pre>
 * However, we do not enforce this.  If the class name contains a
 * a /, we only use the part to the right of the rightmost slash.
 * In addition, we ignore any "class:" prefix.
 * Provides functions to test a function's existence and call a function.
 * Also provides functions to test an element's existence and call an
 * element.
 *
 * @author <a href="mailto:garyp@firstech.com">Gary L Peskin</a>
 *
 */


public class ExtensionHandlerJavaPackage extends ExtensionHandlerJava
{

  /**
   * Construct a new extension namespace handler given all the information
   * needed. 
   * 
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param scriptLang   language of code implementing the extension
   * @param className    the beginning of the class name of the class.  This
   *                     should be followed by a dot (.)
   */
  public ExtensionHandlerJavaPackage(String namespaceUri,
                                     String scriptLang,
                                     String className)
  {
    super(namespaceUri, scriptLang, className);
  }


  /**
   * Tests whether a certain function name is known within this namespace.
   * Since this is for a package, we concatenate the package name used when
   * this handler was created and the function name specified in the argument.
   * There is
   * no information regarding the arguments to the function call or
   * whether the method implementing the function is a static method or
   * an instance method.
   * @param function name of the function being tested
   * @return true if its known, false if not.
   */

  public boolean isFunctionAvailable(String function) 
  {
    try
    {
      String fullName = m_className + function;
      int lastDot = fullName.lastIndexOf(".");
      if (lastDot >= 0)
      {
        Class myClass = getClassForName(fullName.substring(0, lastDot));
        Method[] methods = myClass.getMethods();
        int nMethods = methods.length;
        function = fullName.substring(lastDot + 1);
        for (int i = 0; i < nMethods; i++)
        {
          if (methods[i].getName().equals(function))
            return true;
        }
      }
    }
    catch (ClassNotFoundException cnfe) {}

    return false;
  }


  /**
   * Tests whether a certain element name is known within this namespace.
   * Looks for a method with the appropriate name and signature.
   * This method examines both static and instance methods.
   * @param function name of the function being tested
   * @return true if its known, false if not.
   */

  public boolean isElementAvailable(String element) 
  {
    try
    {
      String fullName = m_className + element;
      int lastDot = fullName.lastIndexOf(".");
      if (lastDot >= 0)
      {
        Class myClass = getClassForName(fullName.substring(0, lastDot));
        Method[] methods = myClass.getMethods();
        int nMethods = methods.length;
        element = fullName.substring(lastDot + 1);
        for (int i = 0; i < nMethods; i++)
        {
          if (methods[i].getName().equals(element))
          {
            Class[] paramTypes = methods[i].getParameterTypes();
            if ( (paramTypes.length == 2)
              && paramTypes[0].isAssignableFrom(
                                     org.apache.xalan.extensions.XSLProcessorContext.class)
              && paramTypes[1].isAssignableFrom(
                                       org.apache.xalan.templates.ElemExtensionCall.class) )
            {
              return true;
            }
          }
        }
      }
    }
    catch (ClassNotFoundException cnfe) {}

    return false;
  }


  /**
   * Process a call to a function in the package java namespace.
   * There are three possible types of calls:
   * <pre>
   *   Constructor:
   *     packagens:class.name.new(arg1, arg2, ...)
   *
   *   Static method:
   *     packagens:class.name.method(arg1, arg2, ...)
   *
   *   Instance method:
   *     packagens:method(obj, arg1, arg2, ...)
   * </pre>
   * We use the following rules to determine the type of call made:
   * <ol type="1">
   * <li>If the function name ends with a ".new", call the best constructor for
   *     class whose name is formed by concatenating the value specified on
   *     the namespace with the value specified in the function invocation
   *     before ".new".</li>
   * <li>If the function name contains a period, call the best static method "method"
   *     in the class whose name is formed by concatenating the value specified on
   *     the namespace with the value specified in the function invocation.</li>
   * <li>Otherwise, call the best instance method "method"
   *     in the class whose name is formed by concatenating the value specified on
   *     the namespace with the value specified in the function invocation.
   *     Note that a static method of the same
   *     name will <i>not</i> be called in the current implementation.  This
   *     module does not verify that the obj argument is a member of the
   *     package namespace.</li>
   * </ol>
   *
   * @param funcName Function name.
   * @param args     The arguments of the function call.
   *
   * @return the return value of the function evaluation.
   *
   * @throws TransformerException          if parsing trouble
   */

  public Object callFunction (String funcName, 
                              Vector args, 
                              Object methodKey,
                              ExpressionContext exprContext)
    throws TransformerException 
  {

    String className;
    String methodName;
    Class  classObj;
    Object targetObject;
    int lastDot = funcName.lastIndexOf(".");
    Object[] methodArgs;
    Object[][] convertedArgs;
    Class[] paramTypes;

    try
    {

      if (funcName.endsWith(".new")) {                   // Handle constructor call

        methodArgs = new Object[args.size()];
        convertedArgs = new Object[1][];
        for (int i = 0; i < methodArgs.length; i++)
        {
          methodArgs[i] = args.elementAt(i);
        }
        Constructor c = (Constructor) getFromCache(methodKey, null, methodArgs);
        if (c != null)
        {
          try
          {
            paramTypes = c.getParameterTypes();
            MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
            return c.newInstance(convertedArgs[0]);
          }
          catch (InvocationTargetException ite)
          {
            throw ite;
          }
          catch(Exception e)
          {
            // Must not have been the right one
          }
        }
        className = m_className + funcName.substring(0, lastDot);
        try
        {
          classObj = getClassForName(className);
        }
        catch (ClassNotFoundException e) 
        {
          throw new TransformerException(e);
        }
        c = MethodResolver.getConstructor(classObj, 
                                          methodArgs,
                                          convertedArgs,
                                          exprContext);
        putToCache(methodKey, null, methodArgs, c);
        return c.newInstance(convertedArgs[0]);
      }

      else if (-1 != lastDot) {                         // Handle static method call

        methodArgs = new Object[args.size()];
        convertedArgs = new Object[1][];
        for (int i = 0; i < methodArgs.length; i++)
        {
          methodArgs[i] = args.elementAt(i);
        }
        Method m = (Method) getFromCache(methodKey, null, methodArgs);
        if (m != null)
        {
          try
          {
            paramTypes = m.getParameterTypes();
            MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
            return m.invoke(null, convertedArgs[0]);
          }
          catch (InvocationTargetException ite)
          {
            throw ite;
          }
          catch(Exception e)
          {
            // Must not have been the right one
          }
        }
        className = m_className + funcName.substring(0, lastDot);
        methodName = funcName.substring(lastDot + 1);
        try
        {
          classObj = getClassForName(className);
        }
        catch (ClassNotFoundException e) 
        {
          throw new TransformerException(e);
        }
        m = MethodResolver.getMethod(classObj,
                                     methodName,
                                     methodArgs, 
                                     convertedArgs,
                                     exprContext,
                                     MethodResolver.STATIC_ONLY);
        putToCache(methodKey, null, methodArgs, m);
        return m.invoke(null, convertedArgs[0]);
      }

      else {                                            // Handle instance method call

        if (args.size() < 1)
        {
          throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_INSTANCE_MTHD_CALL_REQUIRES, new Object[]{funcName })); //"Instance method call to method " + funcName
                                    //+ " requires an Object instance as first argument");
        }
        targetObject = args.elementAt(0);
        if (targetObject instanceof XObject)          // Next level down for XObjects
          targetObject = ((XObject) targetObject).object();
        methodArgs = new Object[args.size() - 1];
        convertedArgs = new Object[1][];
        for (int i = 0; i < methodArgs.length; i++)
        {
          methodArgs[i] = args.elementAt(i+1);
        }
        Method m = (Method) getFromCache(methodKey, targetObject, methodArgs);
        if (m != null)
        {
          try
          {
            paramTypes = m.getParameterTypes();
            MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
            return m.invoke(targetObject, convertedArgs[0]);
          }
          catch (InvocationTargetException ite)
          {
            throw ite;
          }
          catch(Exception e)
          {
            // Must not have been the right one
          }
        }
        classObj = targetObject.getClass();
        m = MethodResolver.getMethod(classObj,
                                     funcName,
                                     methodArgs, 
                                     convertedArgs,
                                     exprContext,
                                     MethodResolver.INSTANCE_ONLY);
        putToCache(methodKey, targetObject, methodArgs, m);
        return m.invoke(targetObject, convertedArgs[0]);
      }
    }
    catch (InvocationTargetException ite)
    {
      Throwable resultException = ite;
      Throwable targetException = ite.getTargetException();
 
      if (targetException instanceof TransformerException)
        throw ((TransformerException)targetException);
      else if (targetException != null)
        resultException = targetException;
            
      throw new TransformerException(resultException);
    }
    catch (Exception e)
    {
      // e.printStackTrace();
      throw new TransformerException(e);
    }
  }


  /**
   * Process a call to this extension namespace via an element. As a side
   * effect, the results are sent to the TransformerImpl's result tree.
   * For this namespace, only static element methods are currently supported.
   * If instance methods are needed, please let us know your requirements.
   * @param localPart      Element name's local part.
   * @param element        The extension element being processed.
   * @param transformer      Handle to TransformerImpl.
   * @param stylesheetTree The compiled stylesheet tree.
   * @param mode           The current mode.
   * @param sourceTree     The root of the source tree (but don't assume
   *                       it's a Document).
   * @param sourceNode     The current context node.
   * @param mode           The current mode.
   * @param methodKey      A key that uniquely identifies this element call.
   * @throws IOException           if loading trouble
   * @throws TransformerException          if parsing trouble
   */

  public void processElement (String localPart,
                              ElemTemplateElement element,
                              TransformerImpl transformer,
                              Stylesheet stylesheetTree,
                              Object methodKey)
    throws TransformerException, IOException
  {
    Object result = null;
    Class classObj;

    Method m = (Method) getFromCache(methodKey, null, null);
    if (null == m)
    {
      try
      {
        String fullName = m_className + localPart;
        int lastDot = fullName.lastIndexOf(".");
        if (lastDot < 0)
          throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_ELEMENT_NAME, new Object[]{fullName })); //"Invalid element name specified " + fullName);
        try
        {
          classObj = getClassForName(fullName.substring(0, lastDot));
        }
        catch (ClassNotFoundException e) 
        {
          throw new TransformerException(e);
        }
        localPart = fullName.substring(lastDot + 1);
        m = MethodResolver.getElementMethod(classObj, localPart);
        if (!Modifier.isStatic(m.getModifiers()))
          throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_ELEMENT_NAME_METHOD_STATIC, new Object[]{fullName })); //"Element name method must be static " + fullName);
      }
      catch (Exception e)
      {
        // e.printStackTrace ();
        throw new TransformerException (e);
      }
      putToCache(methodKey, null, null, m);
    }

    XSLProcessorContext xpc = new XSLProcessorContext(transformer, 
                                                      stylesheetTree);

    try
    {
      result = m.invoke(null, new Object[] {xpc, element});
    }
    catch (InvocationTargetException ite)
    {
      Throwable resultException = ite;
      Throwable targetException = ite.getTargetException();
 
      if (targetException instanceof TransformerException)
        throw ((TransformerException)targetException);
      else if (targetException != null)
        resultException = targetException;
            
      throw new TransformerException(resultException);
    }
    catch (Exception e)
    {
      // e.printStackTrace ();
      throw new TransformerException (e);
    }

    if (result != null)
    {
      xpc.outputToResultTree (stylesheetTree, result);
    }
 
  }

}
