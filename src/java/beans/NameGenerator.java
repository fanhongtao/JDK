/*
 * @(#)NameGenerator.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.beans;

import java.util.*;

/*
 * @version 1.7 01/23/03
 * @author Philip Milne
 */

class NameGenerator { 

    private static HashMap valueToName; 
    private static HashMap instanceCountsByClassName; 
    
    static { 
        init(); 
    }
    
    private static void init() { 
        valueToName = new IdentityHashtable();
        instanceCountsByClassName = new HashMap();
    }
    
    static void clear() { 
        init(); 
    }
    
    private static String unqualifiedClassName(Class type) { 
        if (type.isArray()) {
            return unqualifiedClassName(type.getComponentType())+"Array"; 
        }
        String name = type.getName(); 
        return name.substring(name.lastIndexOf('.')+1); 
    }
    
    static String replace(String s, char out, String in) { 
        StringBuffer result = new StringBuffer(); 
        for(int i = 0; i < s.length(); i++) { 
            if (s.charAt(i) != out) { 
                result.append(s.charAt(i)); 
            }
            else { 
                result.append(in); 
            }
        } 
        return result.toString(); 
    }

    static String instanceName(Object instance) {         
        if (instance == null) {
            return "null"; 
        }
        if (instance instanceof Class) {
            return unqualifiedClassName((Class)instance); 
        }
        else { 
            String result = (String)valueToName.get(instance); 
            if (result != null) { 
                return result; 
            }
            Class type = instance.getClass(); 
            String unqualifiedClassName = unqualifiedClassName(type); 
            Object size = instanceCountsByClassName.get(unqualifiedClassName); 
            int instanceNumber = (size == null) ? 0 : ((Integer)size).intValue() + 1; 
            instanceCountsByClassName.put(unqualifiedClassName, new Integer(instanceNumber)); 
            result = unqualifiedClassName + instanceNumber; 
            valueToName.put(instance, result); 
            return result; 
        }
    }
}
















































