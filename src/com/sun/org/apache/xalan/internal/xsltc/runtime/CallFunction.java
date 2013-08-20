/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 * $Id:
 */

package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.Vector;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.IllegalAccessException;
import java.lang.IllegalArgumentException;
import java.lang.InstantiationException;


/**
 * Resolve the function dynamically
 */
public final class CallFunction {

    public static String className;
    public static String methodName;
    public static int nArgs;
    public static Class clazz;


    public static String invokeMethod(String _className, String _methodName, Object [] _arguments){
        className = _className;
        methodName = _methodName;
        int size = _arguments.length-1;
        Object [] arguments = new Object[size];
        Object object= _arguments[0];
        clazz =null;
        try {
            clazz = ObjectFactory.findProviderClass(className, ObjectFactory.findClassLoader(), true);
            if (clazz == null) {
                   throw new RuntimeException("Couldn't load the class");
            }
        }catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load the class");
        }
      
        for(int i=0,j=1;i<size;i++,++j){
             arguments[i] = _arguments[j];
        }
        nArgs = size;
        if( methodName != null ){
              Method method;
              if((method = findMethods(arguments)) == null){
                  throw new RuntimeException("Method not found");
              }

              try{
                 Object obj =  method.invoke(object,arguments);
                 return obj.toString() ;
              }
              catch(IllegalAccessException e){
                  throw new RuntimeException("Error: Method is inaccessible");
              }
              catch(IllegalArgumentException e){
                   throw new RuntimeException("Error: Number of actual and formal argument differ ");
              }
              catch(InvocationTargetException e){
                   throw new RuntimeException("Error: underlying constructor throws an exception ");
              }
        }
        else {
            Constructor constructor;
            if((constructor = findConstructor(arguments)) == null){
                throw new RuntimeException("Constructor not found");
            }
            try{
               Object  obs = constructor.newInstance(arguments);
               return obs.toString() ;
            }catch(InvocationTargetException e){
                throw new RuntimeException("Error: constructor throws an exception ");
            }
            catch(IllegalAccessException e){
                throw new RuntimeException("Error: constructor is inaccessible");
            }
            catch(IllegalArgumentException e){
                throw new RuntimeException("Error: Number of actual and formal argument differ ");
            }
            catch(InstantiationException e){
                throw new RuntimeException("Error: Class that declares the underlying constructor represents an abstract class");
            }
        }

    }

    /**
     * Returns a Constructor
     */
    private static Constructor findConstructor(Object[] arguments) {
        Vector constructors =null;


            final Constructor[] c_constructors = clazz.getConstructors();

            for (int i = 0; i < c_constructors.length; i++) {
                final int mods = c_constructors[i].getModifiers();
                // Is it public, static and same number of args ?
                if (Modifier.isPublic(mods) && c_constructors[i].getParameterTypes().length == nArgs){
                    if (constructors == null) {
                        constructors = new Vector();
                    }
                    constructors.addElement(c_constructors[i]);
                }
            }


        if (constructors == null) {
	    // Method not found in this class
	   throw new RuntimeException("CONSTRUCTOR_NOT_FOUND_ERR" + className +":"+ methodName);
	}

        int nConstructors = constructors.size();
        boolean accept=false;
        for (int j, i = 0; i < nConstructors; i++) {
	    // Check if all parameters to this constructor can be converted
	    final Constructor constructor = (Constructor)constructors.elementAt(i);
	    final Class[] paramTypes = constructor.getParameterTypes();

            for (j = 0; j < nArgs; j++) {
                Class argumentClass = arguments[j].getClass();
                if (argumentClass == paramTypes[j]){
                    accept= true;
                }
                else if(argumentClass.isAssignableFrom(paramTypes[j])){
                    accept=true;
                }
                else {
                     accept =false;
                     break;
                }
          }
          if (accept)
              return constructor;
        }
        return null;
    }



    /**
     * Return the Method
     */
    private static Method findMethods(Object[] arguments) {
        Vector methods = null;

            final Method[] m_methods = clazz.getMethods();

            for (int i = 0; i < m_methods.length; i++){
		  final int mods = m_methods[i].getModifiers();
		  // Is it public and same number of args ?
	        if(  Modifier.isPublic(mods)
                     && m_methods[i].getName().equals(methodName)
                     && m_methods[i].getParameterTypes().length == nArgs){
		    if (methods == null){
                           methods = new Vector();
                    }
                    methods.addElement(m_methods[i]);
		}
            }


         if (methods == null) {
	    // Method not found in this class
	   throw new RuntimeException("METHOD_NOT_FOUND_ERR" + className +":"+ methodName);
	}
        int nMethods = methods.size();
        boolean accept=false;
        for (int j, i = 0; i < nMethods; i++) {
	    // Check if all parameters to this constructor can be converted
	    final Method method = (Method)methods.elementAt(i);
	    final Class[] paramTypes = method.getParameterTypes();

            for (j = 0; j < nArgs; j++) {
                Class argumentClass = arguments[j].getClass();
                if (argumentClass == paramTypes[j]){
                    accept= true;
                }
                else if(argumentClass.isAssignableFrom(paramTypes[j])){
                    accept=true;
                }
                else if(paramTypes[j].isPrimitive() ){
                    arguments[j] = isPrimitive(paramTypes[j],arguments[j]);
                    accept = true;
                }
                else {
                    accept =false;
                    break;
                }

            }
            if (accept)
                return method;
        }

        return null;
    }

    public  static Object isPrimitive(Class paramType, Object argument){

        if( argument.getClass()  ==  Integer.class )
             return  typeCast(paramType,(Integer)argument);
        else if( argument.getClass() == Float.class )
             return  typeCast(paramType,(Float)argument);
        else if( argument.getClass() == Double.class )
             return  typeCast(paramType,(Double)argument);
        else if( argument.getClass() == Long.class )
             return  typeCast(paramType,(Long)argument);
        else if( argument.getClass() == Boolean.class )
              return (Boolean)argument;
        else if( argument.getClass() == Byte.class )
             return  (Byte)argument;
        else
            return null;
    }

    static Object typeCast(Class paramType, Double object){
        if (paramType == Long.TYPE)
            return  new Long(object.longValue());
        else if (paramType == Integer.TYPE)
            return  new Integer(object.intValue());
        else if (paramType == Float.TYPE)
            return  new Float(object.floatValue());
        else if (paramType == Short.TYPE)
            return  new Short(object.shortValue());
        else if (paramType == Byte.TYPE)
            return  new Byte(object.byteValue());
        else
            return object;
    }

    static Object typeCast(Class paramType, Long object){
        if (paramType == Integer.TYPE)
            return  new Integer(object.intValue());
        else if (paramType == Float.TYPE)
            return  new Float(object.floatValue());
        else if (paramType == Short.TYPE)
            return  new Short(object.shortValue());
        else if (paramType == Byte.TYPE)
            return  new Byte(object.byteValue());
        else
            return object;
    }

    static Object typeCast(Class paramType, Integer object){
        if(paramType == Double.TYPE)
            return  new Double(object.doubleValue());
        else if (paramType == Float.TYPE)
            return  new Float(object.floatValue());
        else if (paramType == Short.TYPE)
            return  new Short(object.shortValue());
        else if (paramType == Byte.TYPE)
            return  new Byte(object.byteValue());
        else
            return object;
    }

    static Object typeCast(Class paramType, Float object){
        if(paramType == Double.TYPE)
            return  new Double(object.doubleValue());
        else if (paramType == Integer.TYPE)
            return  new Float(object.intValue());
        else if (paramType == Short.TYPE)
            return  new Short(object.shortValue());
        else if (paramType == Byte.TYPE)
            return  new Byte(object.byteValue());
        else
            return object;
    }

}
