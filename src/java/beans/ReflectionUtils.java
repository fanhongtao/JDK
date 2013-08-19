package java.beans;

import java.lang.reflect.*;
import java.lang.ref.*;
import java.util.*;

import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ConstructorUtil;
import sun.reflect.misc.ReflectUtil;

class ReflectionUtils {

    private static Reference methodCacheRef;

    public static Class typeToClass(Class type) {
        return type.isPrimitive() ? ObjectHandler.typeNameToClass(type.getName()) : type;
    }

    public static boolean isPrimitive(Class type) {
        return primitiveTypeFor(type) != null;
    }

    public static Class primitiveTypeFor(Class wrapper) {
        if (wrapper == Boolean.class) return Boolean.TYPE;
        if (wrapper == Byte.class) return Byte.TYPE;
        if (wrapper == Character.class) return Character.TYPE;
        if (wrapper == Short.class) return Short.TYPE;
        if (wrapper == Integer.class) return Integer.TYPE;
        if (wrapper == Float.class) return Float.TYPE;
        if (wrapper == Void.class) return Void.TYPE;
        if (wrapper == Long.class) return Long.TYPE;
        if (wrapper == Double.class) return Double.TYPE;
        return null;
    }

    private static boolean matchArguments(Class[] argClasses, Class[] argTypes) {
        boolean match = (argClasses.length == argTypes.length);
        for(int j = 0; j < argClasses.length && match; j++) {
            Class argType = argTypes[j];
            if (argType.isPrimitive()) {
                argType = typeToClass(argType);
            }
            if (argClasses[j] != null && !(argType.isAssignableFrom(argClasses[j]))) {
                match = false;
            }
        }
        return match;
    }

    private static boolean matchExplicitArguments(Class[] argClasses, Class[] argTypes) {
        boolean match = (argClasses.length == argTypes.length);
        for(int j = 0; j < argClasses.length && match; j++) {
            Class argType = argTypes[j];
            if (argType.isPrimitive()) {
                argType = typeToClass(argType);
            }
            if (argClasses[j] != argType) {
                match = false;
            }
        }
        return match;
    }

    /**
     * @return the method which best matches the signature or throw an exception
     *         if it can't be found or the method is ambiguous.
     */
    static Method getPublicMethod(Class declaringClass, String methodName, 
                          Class[] argClasses) throws NoSuchMethodException {
         Method m;
 
         m = findPublicMethod(declaringClass, methodName, argClasses);
         if (m == null)
             throw new NoSuchMethodException(declaringClass.getName() + "." + methodName);
         return m;
    }
 
    public static Method findPublicMethod(Class declaringClass, String methodName, Class[] argClasses) {
        if (argClasses.length == 0) {
            try {
                return MethodUtil.getMethod(declaringClass, methodName, argClasses);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException se) {
                // fall through
            }
        }
        Method[] methods = MethodUtil.getPublicMethods(declaringClass);
        ArrayList list = new ArrayList();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                if (matchArguments(argClasses, method.getParameterTypes())) {
                    list.add(method);
                }
            }
        }
        if (list.size() > 0) {
            if (list.size() == 1) {
                return (Method)list.get(0);
            } else {
                ListIterator iterator = list.listIterator();
                Method method;
                while (iterator.hasNext()) {
                    method = (Method)iterator.next();
                    if (matchExplicitArguments(argClasses, method.getParameterTypes())) {
                        return method;
                    }
                }
                return (Method)list.get(0);
            }
        }
        return null;
    }

    public static Method findMethod(Class targetClass, String methodName, Class[] argClasses) {
        Method m = findPublicMethod(targetClass, methodName, argClasses);
        if (m != null && Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
            return m;
        }
        for(Class type = targetClass; type != null; type = type.getSuperclass()) {
            Class[] interfaces = type.getInterfaces();
            for(int i = 0; i < interfaces.length; i++) {
                m = findPublicMethod(interfaces[i], methodName, argClasses);
                if (m != null) {
                    return m;
                }
            }
        }
        return null;
    }

    private static class Signature {
        private Class targetClass;
        private String methodName;
        private Class[] argClasses;

        private volatile int hashCode = 0;

        public Signature(Class targetClass, String methodName, Class[] argClasses) {
            this.targetClass = targetClass;
            this.methodName = methodName;
            this.argClasses = argClasses;
        }

        public boolean equals(Object o2) {
            if (this == o2) {
                return true;
            }
            Signature that = (Signature)o2;
            if (!(targetClass == that.targetClass)) {
                return false;
            }
            if (!(methodName.equals(that.methodName))) {
                return false;
            }
            if (argClasses.length != that.argClasses.length) {
                return false;
            }
            for(int i = 0; i < argClasses.length; i++) {
                if (!(argClasses[i] == that.argClasses[i])) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            if (hashCode == 0) {
                int result = 17;
                result = 37 * result + targetClass.hashCode();
                result = 37 * result + methodName.hashCode();
                if (argClasses != null) {
                    for(int i = 0; i < argClasses.length; i++) {
                        result = 37 * result + ((argClasses[i] == null) ? 0 :
                                                argClasses[i].hashCode());
                    }
                }
                hashCode = result;
            }
            return hashCode;
        }
    }

    public static synchronized Method getMethod(Class targetClass,
                                            String methodName, Class[] argClasses) {
        Object signature = new Signature(targetClass, methodName, argClasses);

        Method method = null;
        Map methodCache = null;
        boolean cache = false;
        if (ReflectUtil.isPackageAccessible(targetClass)) {
            cache = true;
        }

        if (cache && methodCacheRef != null &&
            (methodCache = (Map)methodCacheRef.get()) != null) {
            method = (Method)methodCache.get(signature);
            if (method != null) {
                return method;
            }
        }
        method = findMethod(targetClass, methodName, argClasses);
        if (cache && method != null) {
            if (methodCache == null) {
                methodCache = new HashMap();
                methodCacheRef = new SoftReference(methodCache);
            }
            methodCache.put(signature, method);
        }
        return method;
    }

    public static Constructor getConstructor(Class cls, Class[] args) {
        Constructor constructor = null;
        Constructor[] ctors = ConstructorUtil.getConstructors(cls);
        for(int i = 0; i < ctors.length; i++) {
            if (matchArguments(args, ctors[i].getParameterTypes())) {
                constructor = ctors[i];
            }
        }
        return constructor;
    }

    public static Object getPrivateField(Object instance, Class cls, String name) {
        return getPrivateField(instance, cls, name, null);
    }

    public static Object getPrivateField(Object instance, Class cls, String name, ExceptionListener el) {
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception e) {
            if (el != null) {
                el.exceptionThrown(e);
            }
        }
        return null;
    }
}

