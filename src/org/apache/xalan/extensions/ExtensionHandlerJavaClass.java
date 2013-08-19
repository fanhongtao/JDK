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

import java.util.Vector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.QName;

import org.apache.xpath.objects.XObject;
import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * Represents an extension namespace for XPath that handles java classes.
 * It is recommended that the class URI be of the form:
 * <pre>
 *   xalan://fully.qualified.class.name
 * </pre>
 * However, we do not enforce this.  If the class name contains a
 * a /, we only use the part to the right of the rightmost slash.
 * In addition, we ignore any "class:" prefix.
 * Provides functions to test a function's existence and call a function.
 * Also provides functions to test an element's existence and call an
 * element.
 *
 * @author <a href="mailto:garyp@firstech.com">Gary L Peskin</a>
 */

public class ExtensionHandlerJavaClass extends ExtensionHandlerJava
{

  private Class m_classObj = null;

  /**
   * Provides a default Instance for use by elements that need to call 
   * an instance method.
   */

  private Object m_defaultInstance = null;


  /**
   * Construct a new extension namespace handler given all the information
   * needed. 
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param scriptLang   language of code implementing the extension
   * @param className    the fully qualified class name of the class
   */
  public ExtensionHandlerJavaClass(String namespaceUri,
                                   String scriptLang,
                                   String className)
  {
    super(namespaceUri, scriptLang, className);
    try
    {
      m_classObj = getClassForName(className);
    }
    catch (ClassNotFoundException e)
    {
      // For now, just let this go.  We'll catch it when we try to invoke a method.
    }
  }


  /**
   * Tests whether a certain function name is known within this namespace.
   * Simply looks for a method with the appropriate name.  There is
   * no information regarding the arguments to the function call or
   * whether the method implementing the function is a static method or
   * an instance method.
   * @param function name of the function being tested
   * @return true if its known, false if not.
   */

  public boolean isFunctionAvailable(String function) 
  {
    Method[] methods = m_classObj.getMethods();
    int nMethods = methods.length;
    for (int i = 0; i < nMethods; i++)
    {
      if (methods[i].getName().equals(function))
        return true;
    }
    return false;
  }


  /**
   * Tests whether a certain element name is known within this namespace.
   * Looks for a method with the appropriate name and signature.
   * This method examines both static and instance methods.
   * @param element name of the element being tested
   * @return true if its known, false if not.
   */

  public boolean isElementAvailable(String element) 
  {
    Method[] methods = m_classObj.getMethods();
    int nMethods = methods.length;
    for (int i = 0; i < nMethods; i++)
    {
      if (methods[i].getName().equals(element))
      {
        Class[] paramTypes = methods[i].getParameterTypes();
        if ( (paramTypes.length == 2)
          && paramTypes[0].isAssignableFrom(
                                     org.apache.xalan.extensions.XSLProcessorContext.class)
          && paramTypes[1].isAssignableFrom(org.apache.xalan.templates.ElemExtensionCall.class) )
        {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * Process a call to a function in the java class represented by
   * this <code>ExtensionHandlerJavaClass<code>.
   * There are three possible types of calls:
   * <pre>
   *   Constructor:
   *     classns:new(arg1, arg2, ...)
   *
   *   Static method:
   *     classns:method(arg1, arg2, ...)
   *
   *   Instance method:
   *     classns:method(obj, arg1, arg2, ...)
   * </pre>
   * We use the following rules to determine the type of call made:
   * <ol type="1">
   * <li>If the function name is "new", call the best constructor for
   *     class represented by the namespace URI</li>
   * <li>If the first argument to the function is of the class specified
   *     in the namespace or is a subclass of that class, look for the best
   *     method of the class specified in the namespace with the specified
   *     arguments.  Compare all static and instance methods with the correct
   *     method name.  For static methods, use all arguments in the compare.
   *     For instance methods, use all arguments after the first.</li>
   * <li>Otherwise, select the best static or instance method matching
   *     all of the arguments.  If the best method is an instance method,
   *     call the function using a default object, creating it if needed.</li>
   * </ol>
   *
   * @param funcName Function name.
   * @param args     The arguments of the function call.
   * @param methodKey A key that uniquely identifies this class and method call.
   * @param exprContext The context in which this expression is being executed.
   * @return the return value of the function evaluation.
   * @throws TransformerException
   */

  public Object callFunction (String funcName, 
                              Vector args, 
                              Object methodKey,
                              ExpressionContext exprContext)
    throws TransformerException 
  {

    Object[] methodArgs;
    Object[][] convertedArgs;
    Class[] paramTypes;

    try
    {
      if (funcName.equals("new")) {                   // Handle constructor call

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
        c = MethodResolver.getConstructor(m_classObj, 
                                          methodArgs,
                                          convertedArgs,
                                          exprContext);
        putToCache(methodKey, null, methodArgs, c);
        return c.newInstance(convertedArgs[0]);
      }

      else
      {

        int resolveType;
        Object targetObject = null;
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
            if (Modifier.isStatic(m.getModifiers()))
              return m.invoke(null, convertedArgs[0]);
            else
            {
              // This is tricky.  We get the actual number of target arguments (excluding any
              //   ExpressionContext).  If we passed in the same number, we need the implied object.
              int nTargetArgs = convertedArgs[0].length;
              if (ExpressionContext.class.isAssignableFrom(paramTypes[0]))
                nTargetArgs--;
              if (methodArgs.length <= nTargetArgs)
                return m.invoke(m_defaultInstance, convertedArgs[0]);
              else  
              {
                targetObject = methodArgs[0];
                
                if (targetObject instanceof XObject)
                  targetObject = ((XObject) targetObject).object();
                  
                return m.invoke(targetObject, convertedArgs[0]);
              }
            }
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

        if (args.size() > 0)
        {
          targetObject = methodArgs[0];

          if (targetObject instanceof XObject)
            targetObject = ((XObject) targetObject).object();

          if (m_classObj.isAssignableFrom(targetObject.getClass()))
            resolveType = MethodResolver.DYNAMIC;
          else
            resolveType = MethodResolver.STATIC_AND_INSTANCE;
        }
        else
        {
          targetObject = null;
          resolveType = MethodResolver.STATIC_AND_INSTANCE;
        }

        m = MethodResolver.getMethod(m_classObj,
                                     funcName,
                                     methodArgs, 
                                     convertedArgs,
                                     exprContext,
                                     resolveType);
        putToCache(methodKey, null, methodArgs, m);

        if (MethodResolver.DYNAMIC == resolveType)          // First argument was object type
          return m.invoke(targetObject, convertedArgs[0]);
        else                                  // First arg was not object.  See if we need the implied object.
        {
          if (Modifier.isStatic(m.getModifiers()))
            return m.invoke(null, convertedArgs[0]);
          else
          {
            if (null == m_defaultInstance)
            {
              m_defaultInstance = m_classObj.newInstance();
            }
            return m.invoke(m_defaultInstance, convertedArgs[0]);
          }  
        }

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
   * We invoke the static or instance method in the class represented by
   * by the namespace URI.  If we don't already have an instance of this class,
   * we create one upon the first call.
   *
   * @param localPart      Element name's local part.
   * @param element        The extension element being processed.
   * @param transformer      Handle to TransformerImpl.
   * @param stylesheetTree The compiled stylesheet tree.
   * @param sourceTree     The root of the source tree (but don't assume
   *                       it's a Document).
   * @param sourceNode     The current context node.
   * @param mode           The current mode.
   * @param methodKey      A key that uniquely identifies this element call.
   * @throws IOException           if loading trouble
   * @throws TransformerException          if parsing trouble
   */

  public void processElement(String localPart,
                             ElemTemplateElement element,
                             TransformerImpl transformer,
                             Stylesheet stylesheetTree,
                             Object methodKey)
    throws TransformerException, IOException
  {
    Object result = null;

    Method m = (Method) getFromCache(methodKey, null, null);
    if (null == m)
    {
      try
      {
        m = MethodResolver.getElementMethod(m_classObj, localPart);
        if ( (null == m_defaultInstance) && !Modifier.isStatic(m.getModifiers()) )
          m_defaultInstance = m_classObj.newInstance();
      }
      catch (Exception e)
      {
        // e.printStackTrace ();
        throw new TransformerException (e.getMessage (), e);
      }
      putToCache(methodKey, null, null, m);
    }

    XSLProcessorContext xpc = new XSLProcessorContext(transformer, 
                                                      stylesheetTree);

    try
    {
      result = m.invoke(m_defaultInstance, new Object[] {xpc, element});
    }
    catch (InvocationTargetException e)
    {
      Throwable targetException = e.getTargetException();
      
      if (targetException instanceof TransformerException)
        throw (TransformerException)targetException;
      else if (targetException != null)
        throw new TransformerException (targetException.getMessage (), targetException);
      else
        throw new TransformerException (e.getMessage (), e);
    }
    catch (Exception e)
    {
      // e.printStackTrace ();
      throw new TransformerException (e.getMessage (), e);
    }

    if (result != null)
    {
      xpc.outputToResultTree (stylesheetTree, result);
    }
 
  }
 
}
