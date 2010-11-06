package org.encog.util.obj;

//package marketdata;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Edwin Floyd (adapted from ClassList.java by Kris Dover; see below)
 */

/**
 * From the original code found at http://www.xmlizer.biz/java/classloader/ClassList.java . Note,
 * the Interface filter on FindClasses() doesn't work well in the original, so it has been eliminated and the result changed
 * to return a simple set of classes.
 *
 * Additional methods, annotations, and classes have been added to facilitate run-time argument editing and class instantiation
 * by a parser or visual editor.
 *
 * From the original ClassList JavaDoc:
 *
 * <em>Caveat:</em> When used in environments which utilize multiple class loaders--such as
 * a J2EE Container like Tomcat--it is important to select the correct classloader
 * otherwise the classes returned, if any, will be incompatible with those declared
 * in the code employing this class lister.
 * to get a reference to your classloader within an instance method use:
 *  <code>this.getClass().getClassLoader()</code> or
 *  <code>Thread.currentThread().getContextClassLoader()</code> anywhere else
 * <p>
 * @author Kris Dover <krisdover@hotmail.com>
 * @version 0.2.0
 * @since   0.1.0
 */
public class ModuleHandler {

    /**
     * Limits may be used to set runtime-accessible information on method or constructor arguments via Java Annotation mechanism.
     * The intent is to externalize argument limits so these can be enforced at run-time by a parser or visual editor.
     * Example:
     * <pre>
     *   public IndicatorEMA(
     *       Comparable name,
     *       <code>@Limits(type=AbstractChartProvider.class) AbstractChartProvider source,</code>
     *       <code>@Limits(type=int.class,min=0) int part,</code>
     *       <code>@Limits(type=int.class,min=2,minrequired=1,maxallowed=10) int... bars)</code>
     *   {...
     * </pre>
     * At runtime, this argument information can be retrieved by ClassInfo.getLimits();
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Inherited
    public static @interface Limits {
        /**
         * Specifies the minimum allowed value for numeric arguments. Default is negative infinity.
         * @return minimum allowed value
         */
        double min() default Double.NEGATIVE_INFINITY;
        /**
         * Specifies the maximum allowed value for numeric arguments. Default is positive infinity.
         * @return maximum allowed value
         */
        double max() default Double.POSITIVE_INFINITY;
        /**
         * For variable argument list, specifies the minimum number of entries required.  (Leave default [1] for non-variable.)
         * @return minimum number of variable arguments required.
         */
        int minrequired() default 1;
        /**
         * For variable argument list, specifies the maximum number of entries allowed.  (Leave default [1] for non-variable.)
         * @return maximum number of variable arguments allowed
         */
        int maxallowed() default 1;
        /**
         * Specifies the expected class type for non-numeric arguments.
         * @return the expected class
         */
        Class type() default void.class;
    }

    /**
     * DefaultLimits is a pre-defined instance of Limits that returns the default values.
     */
    private static class DefaultLimits implements Limits {
        private Class t;
        private int minrequired;
        private int maxallowed;
        public DefaultLimits(Class t, int minrequired, int maxallowed) {
            this.t = t;
            this.minrequired = minrequired;
            this.maxallowed = maxallowed;
        }
        public DefaultLimits() {
            this(void.class, 1, 1);
        }
        public int minrequired() {
            return minrequired;
        }
        public int maxallowed() {
            return maxallowed;
        }
        public double min() {
            return Double.NEGATIVE_INFINITY;
        }
        public double max() {
            return Double.POSITIVE_INFINITY;
        }
        public Class<? extends Annotation> annotationType() {
            return Limits.class;
        }
        public Class type() {
            return this.t;
        }
    };

  /**
   * Searches the classpath for all classes matching a specified search criteria,
   * returning them in a set. The search criteria can be specified via package
   * and jar name.
   * <p>
   * @param classLoader       The classloader whose classpath will be traversed
   * @param packageFilter     A Set of fully qualified package names to search for or
   *                          or null to return classes in all packages
   * @param jarFilter         A Set of jar file names to search for or null to return
   *                          classes from all jars
   * @param inner             A boolean flag indicating whether inner or anonymous
   *                          classes should be returned
   * @param classTable        A Set to which classes are to be added.  If this is null,
   *                          findClasses() creates one, if not, it returns this one
   *                          with new classes added
   * @return A Set of Classes
   */
    public static Set<Class> findClasses(
            final ClassLoader classLoader,
            final Set<String> packageFilter,
            final Set<String> jarFilter,
            final boolean inner,
            Set<Class> classTable)
    {
        if (classTable == null) {
            classTable = new HashSet();
        }
        Set<String> wildPackageFilter = null;
        for (Iterator<String> it = packageFilter.iterator(); it.hasNext();) {
            String s = it.next();
            if (s.endsWith(".*")) {
                if (wildPackageFilter == null) {
                    wildPackageFilter = new HashSet();
                }
                wildPackageFilter.add(s.substring(0, s.length() - 2));
                it.remove();
            }
        }
        
        File[] classPaths = getClassPaths(classLoader);

        for (File path : classPaths) {
            ArrayList<String> files = new ArrayList<String>();
            JarFile module = null;
            if (path.isDirectory() && jarFilter == null) { // path is directory, no jar filter
                recursivelyListDir(files, path, new StringBuffer());
            } else if (path.getName().endsWith(".jar") || path.getName().endsWith(".zip")) { // path is jar or zip
                if (jarFilter != null && !jarFilter.contains(path.getName())) { // not in filter, skip it
                    continue;
                }
                try {
                    module = new JarFile(path);
                } catch (MalformedURLException mue) {
                    System.err.println("Bad classpath. Error:" + mue.getMessage());
                    continue;
                } catch (IOException io) {
                    System.err.println("jar file '" + path.getName() + "' could not be instantiate from file path. Error: "
                            + io.getMessage());
                    continue;
                }
                for (Enumeration ent = module.entries(); ent.hasMoreElements();) {
                    files.add(ent.nextElement().toString());
                }
            }

            for (String fileName : files) {
                if (fileName.endsWith(".class")) {
                    String className = fileName.replaceAll("/", ".").substring(0, fileName.length() - 6);
                    boolean isRequestedPackage = packageFilter == null && wildPackageFilter == null;
                    if (packageFilter != null 
                            && className.lastIndexOf(".") > 0
                            && packageFilter.contains(className.substring(0, className.lastIndexOf(".")))) {
                        isRequestedPackage = true;
                    }
                    if (!isRequestedPackage && wildPackageFilter != null && className.lastIndexOf(".") > 0) {
                        String packageName = className.substring(0, className.lastIndexOf("."));
                        while (packageName.length() > 0) {
                            if (wildPackageFilter.contains(packageName)) {
                                isRequestedPackage = true;
                                break;
                            }
                            if (packageName.lastIndexOf(".") > 0) {
                                packageName = packageName.substring(0, packageName.lastIndexOf("."));
                            } else {
                                packageName = "";
                            }
                        }
                    }
                    if (!isRequestedPackage) {
                        continue;
                    }
                    Class theClass = null;
                    try {
                        theClass = Class.forName(className, false, classLoader);
                    } catch (ClassNotFoundException e) {
                        System.out.println("Skipping class '" + className + "' for reason " + e.getMessage());
                        continue;
                    }
                    if (theClass.isInterface()) { // skip interfaces
                        continue;
                    }
                    if (!inner && className.contains("$")) {
                        continue;
                    }
                    classTable.add(theClass);
                }
            }
            if (module != null) {
                try {
                    module.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
        return classTable;
    }

    /**
     * Returns an array of ClassPath entries, including those specified in top-level .jar file manifests.  This is a helper method
     * for findClasses.
     * @param classLoader ClassLoader to query for path
     * @return  Array of Files, one for each ClassPath entry
     */
    private static File[] getClassPaths(ClassLoader classLoader) {
        Object[] cp;
        ArrayList<File> files = new ArrayList<File>();
        try { // get a list of all classpaths
            cp = ((java.net.URLClassLoader) classLoader).getURLs();
        } catch (ClassCastException cce) { // cast failed; tokenize the system classpath
            cp = System.getProperty("java.class.path", "").split(File.pathSeparator);
        }
        for (Object path : cp) {
            File p = new File((URL.class).isInstance(path) ? ((URL) path).getFile() : path.toString());
            if (p.getName().endsWith(".jar") || p.getName().endsWith(".zip")) {
                try {
                    JarFile jar = new JarFile(p);
                    addUnique(files, p);
                    Manifest man = jar.getManifest();
                    Attributes attr = man.getMainAttributes();
                    if (attr != null) {
                        String mcp = attr.getValue(Attributes.Name.CLASS_PATH);
                        if (mcp != null) {
                            String[] mcpnames = mcp.split(" ");
                            for (String name : mcpnames) {
                                addUnique(files, new File(name));
                            }
                        }
                    }
                    jar.close();
                } catch (IOException ex) {
                    // do nothing
                }

            } else {
                addUnique(files, p);
            }
        }
        File[] ret = new File[files.size()];
        files.toArray(ret);
        return ret;
    }

    /**
     * Add a file to an ArrayList of files without duplication.  This is a helper method for getClassPaths
     * @param files the ArrayList of files
     * @param p the file to add
     */
    private static void addUnique(ArrayList<File> files, File p) {
        for (File f : files) {
            if (f.equals(p)) {
                return;
            }
        }
        System.out.println("path: " + p.getPath());
        files.add(p);
    }

    /**
     * Recursively lists a directory while generating relative paths. This is a helper function for findClasses.
     * Note: Uses a StringBuffer to avoid the excessive overhead of multiple String concatenation
     *
     * @param dirListing     A list variable for storing the directory listing as a list of Strings
     * @param dir                 A File for the directory to be listed
     * @param relativePath A StringBuffer used for building the relative paths
     */
    private static void recursivelyListDir(
            final List<String> dirListing,
            final File dir,
            final StringBuffer relativePath)
    {
        int prevLen; // used to undo append operations to the StringBuffer

        // if the dir is really a directory
        if (dir.isDirectory()) {
            // get a list of the files in this directory
            File[] files = dir.listFiles();
            // for each file in the present dir
            for (int i = 0; i < files.length; i++) {
                // store our original relative path string length
                prevLen = relativePath.length();
                // call this function recursively with file list from present
                // dir and relateveto appended with present dir
                recursivelyListDir(dirListing, files[i], relativePath.append(prevLen == 0 ? "" : "/").append(files[i].getName()));
                //  delete subdirectory previously appended to our relative path
                relativePath.delete(prevLen, relativePath.length());
            }
        } else {
            // this dir is a file; append it to the relativeto path and add it to the directory listing
            dirListing.add(relativePath.toString());
        }
    }

    /**
     * ClassInfo provides a container and methods for working with classes, constructors, and arguments at run-time.
     */
    public static class ClassInfo {
        /** The class that this ClassInfo instance represents */
        Class theclass;
        /** A list of public constructors for this class */
        Constructor[] constructors;
        /** A list of interfaces implemented by this class and any superclasses */
        Class[] interfaces;
        /** A list of public methods for this class */
        Method[] methods;
        /** A list of public fields for this class */
        Field[] fields;
        /** @deprecated Use findModule(theclass.getSuperclass().getName(), infolist) instead */
        ClassInfo superclass;
        /**
         * Empty constructor
         * @deprecated Use ClassInfo(Class c) instead.
         */
        @Deprecated public ClassInfo() {
            // do nothing
        }

        /**
         * Construct a new ClassInfo instance.  This constructor completes the constructors, methods, interfaces, and fields lists
         * @param c
         */
        public ClassInfo(Class c) {
            theclass = c;
            try {
                constructors = c.getConstructors();
            } catch (NoClassDefFoundError ex) {
                System.err.println("Unable to get constructors for " + theclass.getSimpleName() + ": " + ex.getCause());
                constructors = new Constructor[0];
            }
            try {
                methods = c.getMethods();
            } catch (NoClassDefFoundError ex) {
                System.err.println("Unable to get methods for " + theclass.getSimpleName() + ": " + ex.getCause());
                methods = new Method[0];
            }
            ArrayList<Class> intfc = new ArrayList<Class>();
            while (c != null) {
                Collections.addAll(intfc, c.getInterfaces());
                c = c.getSuperclass();
            }
            interfaces = new Class[intfc.size()];
            intfc.toArray(interfaces);
            fields = theclass.getFields();
        }
        /**
         * Determine if the class represented by this ClassInfo instance is an extension of a specified class.
         * @param aclass the class to test as a possible ancestor
         * @return true if the class represented by this ClassInfo instance is a descendent of the specified class
         */
        public boolean isExtensionOf(final Class aclass) {
            Class c = theclass;
            while (c != null) {
                if (c == aclass) {
                    return true;
                }
                c = c.getSuperclass();
            }
            return false;
        }

        /**
         * Determine if the class represented by this ClassInfo instance implements a specified interface
         * @param aninterface the interface to test for implementation
         * @return true if the class represented by this ClassInfo instance implements the specified interface
         */
        public boolean isImplementationOf(final Class aninterface) {
            for (Class c : interfaces) {
                if (c == aninterface) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Convert the wrapped version of a primitive class type to the corresponding unwrapped type.  If the input class is not a
         * primitive wrapper, simply return the input class type.
         * @param type a (possibly) primitive type wrapper
         * @return  the unwrapped version
         */
        private static Class unwrap(final Class type) {
            if (type == Integer.class) {
                return int.class;
            } else if (type == Double.class) {
                return double.class;
            } else if (type == Long.class) {
                return long.class;
            } else if (type == Short.class) {
                return short.class;
            } else if (type == Float.class) {
                return float.class;
            } else if (type == Byte.class) {
                return byte.class;
            } else if (type == Character.class) {
                return char.class;
            } else if (type == Boolean.class) {
                return boolean.class;
            }
            return type;
        }
        
        /**
         * Convert an unwrapped version a primitive class type to the corresponding wrapped type.  If the input class is not a
         * primitive type, simply return the input class type.
         * @param type a (possibly) primitive type
         * @return  the wrapped version
         */
        private static Class wrap(final Class type) {
            if (type == int.class) {
                return Integer.class;
            } else if (type == double.class) {
                return Double.class;
            } else if (type == long.class) {
                return Long.class;
            } else if (type == short.class) {
                return Short.class;
            } else if (type == float.class) {
                return Float.class;
            } else if (type == byte.class) {
                return Byte.class;
            } else if (type == char.class) {
                return Character.class;
            } else if (type == boolean.class) {
                return Boolean.class;
            }
            return type;
        }

        /**
         * Gets the data type for each argument in the list in a form suitable for input to isArgumentMatch().
         * @param arguments an array of Objects that represent arguments for a method or constructor invocation
         * @return an array of Objects that represent the data types for these arguments
         */
        private static Class[] getTypes(final Object... arguments) {
            Class[] types = new Class[arguments.length];
            for (int j = 0; j < types.length; j++) {
                if (arguments[j] == null) {
                    types[j] = null;
                } else {
                    types[j] = arguments[j].getClass();
                }
            }
            return types;
        }

        /**
         * Determine if an argument can be assigned to a formal parameter.  (This is like isAssignableFrom() except it
         * handles primitive types and their wrappers correctly.)
         * 
         * @param formaltype Formal parameter's class type
         * @param argtype    Argument's class type
         * @return           True if the argument can be assigned to the formal parameter
         */
        private static boolean canAssign(final Class formaltype, Class argtype) {
            if (argtype == null && formaltype != null && formaltype.isPrimitive()) {
                return false;
            } else if (argtype == null) {
                return true;
            } else if (formaltype.isPrimitive()) {
                argtype = unwrap(argtype);
                if (!argtype.isPrimitive()) {
                    return false;
                }
                if ((formaltype == boolean.class || argtype == boolean.class)) {
                    return formaltype == argtype;
                }
                if (formaltype == double.class) {
                    return true;
                }
                if (formaltype == float.class && argtype != double.class) {
                    return true;
                }
                if (formaltype == long.class && argtype != double.class && argtype != float.class) {
                    return true;
                }
                if (formaltype == int.class && argtype != double.class && argtype != float.class && argtype != long.class) {
                    return true;
                }
                return formaltype == argtype;
            } else {
                argtype = wrap(argtype);
                return formaltype.isAssignableFrom(argtype);
            }
        }

        /**
         * Determine whether a list of method or constructor formal parameter types matches the argument types for an invocation
         *
         * @param argtypes an array of Objects that represent the argument data types for a method or constructor invocation, such as
         *              might have been returned by getTypes().
         * @param formaltypes an array of Objects that represent formal parameter types for a method or constructor, such as might have
         *              been returned by getParameterTypes().
         * @param isvar boolean flag indicating whether the method or constructor formal parameter list ends with a variable length
         *              argument.
         * @return      true if the argument lists match such that an invocation with argtypes would succeed, false otherwise.
         */
        private static boolean isArgumentMatch(final Class[] argtypes, final Class[] formaltypes, final boolean isvar) {
            final int n = isvar ? formaltypes.length - 1 : formaltypes.length;
            boolean match = false;
            if ((isvar && formaltypes.length - 1 <= argtypes.length) || formaltypes.length == argtypes.length) {
                match = true;
                for (int k = 0; k < n; k++) {
                    if (!canAssign(formaltypes[k], argtypes[k])) {
                        match = false;
                        break;
                    }
                }
                if (match && isvar && argtypes.length >= formaltypes.length) {
                    final Class vartype = formaltypes[n].getComponentType();
                    for (int k = n; k < argtypes.length; k++) {
                        if (!canAssign(vartype, argtypes[k])) {
                            match = false;
                            break;
                        }
                    }
                }
            }
            return match;
        }

        /**
         * Prepares arguments for method or constructor invocation, mainly by resolving a variable parameter at the end, if present.
         * 
         * @param formaltypes Array of formal parameter types from Method or Constructor getParameterTypes()
         * @param isvar     Flag indicating presence of variable length arguments from Method or Constructor isVarArgs()
         * @param arguments Variable length list of arguments for method or constructor invocation
         * @return          Array of arguments suitable for method or constructor invocation
         */
        private static Object[] prepareArguments(final Class[] formaltypes, final boolean isvar, final Object... arguments) {
            final Object[] parm = new Object[formaltypes.length];
            if (!isvar) {
                for (int j = 0; j < parm.length; j++) {
                    parm[j] = arguments[j];
                }
                return parm;
            }
            final Object varpart = Array.newInstance(formaltypes[formaltypes.length - 1].getComponentType(), arguments.length - formaltypes.length + 1);
            parm[parm.length - 1] = varpart;
            Class vartype = formaltypes[parm.length - 1].getComponentType();
            for (int j = 0; j < arguments.length; j++) {
                if (j >= parm.length - 1) {
                    Array.set(varpart, j - parm.length + 1, arguments[j]);
                } else {
                    parm[j] = arguments[j];
                }
            }
            return parm;
        }

        /**
         * Locates the first Method that matches a given argument list.  Note: Unlike Class.getMethod(), this method
         * succeeds when actual arguments contain extensions of formal arguments classes or interfaces.
         *
         * @param arguments Variable length list of arguments, just as they would be passed to the method
         * @return The Method matching the given name and arguments list, or null if no matching method is found
         */
        public Method getMethod(final String methodname, Object... arguments) {
            Class[] types = getTypes(arguments);
            for (Method m : methods) {
                if (m.getName().equals(methodname) && isArgumentMatch(types, m.getParameterTypes(), m.isVarArgs())) {
                    return m;
                }
            }
            return null;
        }

        /**
         * Locates the first Constructor that matches a given argument list.  Note: Unlike Class.getConstructor(), this method
         * succeeds when arguments contain extensions of formal parameter classes or interfaces.
         * 
         * @param arguments Variable length list of arguments, just as they would be passed to the constructor
         * @return The Constructor matching the given arguments list, or null if no matching constructor is found
         */
        public Constructor getConstructor(final Object... arguments) {
            Class[] types = getTypes(arguments);
            for (Constructor c : constructors) {
                if (isArgumentMatch(types, c.getParameterTypes(), c.isVarArgs())) {
                    return c;
                }
            }
            return null;
        }

        /**
         * Locate a public field by name without throwing an exception.
         * @param fieldname name of the field to locate
         * @return a Field object representing the named field, or null if not found
         */
        public Field getField(final String fieldname) {
            if (fieldname == null) {
                return null;
            }
            for (Field f : fields) {
                if (f.getName().equals(fieldname)) {
                    return f;
                }
            }
            return null;
        }

        /**
         * Return the Limits annotations associated with the formal parameters defined for a Constructor.
         *
         * @param constructor The Constructor whose formal parameter Limits annotations are to be returned
         * @return  an array of Limits annotations associated with the specified constructor's parameters.  If a formal parameter
         *          has no Limits annotation, an appropriate DefaultLimits instance is created and returned for that parameter.
         */
        public Limits[] getLimits(final Constructor constructor) {
            Annotation[][] pa = constructor.getParameterAnnotations();
            Class[] pt = constructor.getParameterTypes();
            if (constructor.isVarArgs()) {
                pt[pt.length - 1] = pt[pt.length - 1].getComponentType();
            }
            Limits[] lim = new Limits[pa.length];
            for (int j = 0; j < lim.length; j++) {
                for (Annotation a : pa[j]) {
                    if (a.annotationType() == Limits.class) {
                        lim[j] = (Limits)a;
                    }
                }
                if (lim[j] == null) {
                    int mr = 1;
                    int ma = 1;
                    if (j == lim.length - 1 && constructor.isVarArgs()) {
                        mr = 0;
                        ma = Integer.MAX_VALUE;
                    }
                    lim[j] = new DefaultLimits(pt[j], mr, ma);
                }
            }
            return lim;
        }

        /**
         * Return the Limits annotations associated with the formal parameters defined for a Method.
         *
         * @param method The Method whose formal parameter Limits annotations are to be returned
         * @return  an array of Limits annotations associated with the specified method parameters.  If a formal parameter has no
         *          Limits annotation, an appropriate DefaultLimits instance is created and returned for that parameter.
         */
        public Limits[] getLimits(final Method method) {
            Annotation[][] pa = method.getParameterAnnotations();
            Class[] pt = method.getParameterTypes();
            if (method.isVarArgs()) {
                pt[pt.length - 1] = pt[pt.length - 1].getComponentType();
            }
            Limits[] lim = new Limits[pa.length];
            for (int j = 0; j < lim.length; j++) {
                for (Annotation a : pa[j]) {
                    if (a.annotationType() == Limits.class) {
                        lim[j] = (Limits)a;
                    }
                }
                if (lim[j] == null) {
                    int mr = 1;
                    int ma = 1;
                    if (j == lim.length - 1 && method.isVarArgs()) {
                        mr = 0;
                        ma = Integer.MAX_VALUE;
                    }
                    lim[j] = new DefaultLimits(pt[j], mr, ma);
                }
            }
            return lim;
        }

        /**
         * Return an initialized instance of the class represented by this ClassInfo instance using the constructor that matches
         * the given arguments list.
         *
         * @param arguments A variable length list of arguments for the constructor
         * @return  An instance of the class represented by this ClassInfo instance, or null if no matching constructor could be
         *          found or an error occurred during initialization.
         */
        public Object instanceOf(Object... arguments) {
            Constructor c = getConstructor(arguments);
            if (c == null) {
                System.err.println("Unable to match constructor for " + theclass.getName());
                return null;
            }
            try {
                return c.newInstance(prepareArguments(c.getParameterTypes(), c.isVarArgs(), arguments));
            } catch (Exception ex) {
                System.err.println("Unable to instantiate " + theclass.getName() + ", exception: " + ex.getCause());
                return null;
            }
        }

        /**
         * Invoke a method defined in the class represented by this ClassInfo instance.
         * @param instance  For instance methods, this is the instance to use; for Class methods (static), this is ignored and
         *                  may be null.
         * @param method    The Method to invoke, as returned by getMethod().
         * @param arguments A variable length list of arguments for this method invocation
         * @return          The return value or null if the method type is void
         */
        public Object invoke(Object instance, Method method, Object... arguments) {
            Exception ex = null;
            try {
                return method.invoke(instance, prepareArguments(method.getParameterTypes(), method.isVarArgs(), arguments));
            } catch (IllegalAccessException ex1) {
                ex = ex1;
            } catch (IllegalArgumentException ex1) {
                ex = ex1;
            } catch (InvocationTargetException ex1) {
                ex = ex1;
            }
            System.err.println("Unable to invoke method " + theclass.getSimpleName() + "." + method.getName()
                    + " because: " + ex.getCause());
            throw new RuntimeException(ex.toString() + ":" + ex.getCause());
        }
    }

    /**
     * Locate a specific class by name in the ClassInfo array.
     *
     * @param classname String containing the name of the class.  Name need not be fully qualified.
     * @param info      An array of ClassInfo instances, such as might be returned by createModuleList()
     * @return          The matching ClassInfo entry from the info array, or null if the specified class is not in the info array
     */
    public static ClassInfo findModule(final String classname, final ClassInfo[] info) {
        String cn = "." + classname;
        for (ClassInfo inf : info) {
            String name = inf.theclass.getName();
            if (name.endsWith(cn) || name.equals(classname)) {
                return inf;
            }
        }
        return null;
    }

    /**
     * Return an initialized instance of the class specified by classname. This static method searches the ClassInfo[] array
     * for the specified class name and, if located, returns the result of the associated ClassInfo.instanceOf() method.
     *
     * @param classname The name of the class to instantiate. Name need not be fully qualified.
     * @param info      An array of ClassInfo instances, such as might be returned by createModuleList()
     * @param arguments A variable length list of arguments for the constructor
     * @return          An instance of the specified class, or null if the specified class is not in the info array or no matching
     *                  constructor could be found or an error occurred during initialization.
     */
    public static Object instanceOf(final String classname, final ClassInfo[] info, final Object... arguments) {
        ClassInfo inf = findModule(classname, info);
        if (inf != null) {
            return inf.instanceOf(arguments);
        }
        return null;
    }

    /**
     * Creates an array of ClassInfo entries.
     *
     * @param packages  A variable length list of package names from which to select classes.  A package name ending in ".*"
     *                  is taken to be a wild card and will match any package name beginning with the part of the name prior
     *                  to the ".*", otherwise, the package must match exactly.  Leave empty to select all packages.
     * @return          Returns an array of ClassInfo elements, one for each Class in the selected packages.
     */
    public static ClassInfo[] createModuleList(String... packages) {
        ArrayList<ClassInfo> list = new ArrayList<ClassInfo>();
        Set<String> pkgfilter = null;
        if (packages.length > 0) {
            pkgfilter = new HashSet<String>();
            Collections.addAll(pkgfilter, packages);
        }
        for (Class c : findClasses(ClassLoader.getSystemClassLoader(), pkgfilter, null, false, null)) {
            ClassInfo info = new ClassInfo(c);
            list.add(info);
        }
        ClassInfo[] info = new ClassInfo[list.size()];
        list.toArray(info);
        return info;
    }

    public static void main(String[] args) {
        // simple tests
        ClassInfo[] info = createModuleList("org.encog.*");
        System.out.println("============ Listing class info found =============");
        for (ClassInfo inf : info) {
            System.out.println(inf.theclass.getName());
            for (Constructor con : inf.constructors) {
                System.out.println("    cnstr: " + con.toGenericString());
            }
            for (Class c : inf.interfaces) {
                System.out.println("    intfc: " + c.getName());
            }
        }
        System.out.println("============= Testing instantiation and invocation ===========");
        System.out.println("(note that no org.encog imports are used in this module)");
        System.out.println("Locating ClassInfo for YahooFinanceLoader");
        ClassInfo mLoader = findModule("YahooFinanceLoader", info);
        if (mLoader == null) {
            System.err.println("YahooFinanceLoader not found");
            System.exit(0);
        }
        System.out.println("Creating an instance of YahooFinanceLoader, parameterless constructor");
        Object loader = mLoader.instanceOf();
        if (loader == null) {
            System.exit(0);
        }

        System.out.println("Locating ClassInfo for TickerSymbol");
        ClassInfo mTicker = findModule("TickerSymbol", info);
        if (mTicker == null) {
            System.err.println("TickerSymbol not found");
            System.exit(0);
        }
        System.out.println("Locating constructor with String argument for TickerSymbol");
        Constructor cTicker = mTicker.getConstructor("");
        if (cTicker == null) {
            System.exit(0); //getConstructor will have printed an error message
        }
        System.out.println("Limits annotations for constructor argument(s):");
        Limits[] lim = mTicker.getLimits(cTicker);
        for (int j = 0; j < lim.length; j++) {
            System.out.println(" arg " + j + ": min=" + lim[j].min() + ", max=" + lim[j].max() + ", minrequired=" + lim[j].minrequired()
                    + ", maxallowed=" + lim[j].maxallowed() + ", type=" + lim[j].type().getSimpleName());
        }
        System.out.println("Creating an instance of TickerSymbol for 'appl'");
        Object symbol = mTicker.instanceOf("aapl");
        if (symbol == null) {
            System.exit(0);
        }

        System.out.println("Locating ClassInfo for DateUtil");
        ClassInfo mDateUtil = findModule("DateUtil", info);
        if (mDateUtil == null) {
            System.err.println("DateUtil not found");
            System.exit(0);
        }
        System.out.println("Locating class (static) Method: DateUtil.createDate(int,int,int)");
        Method createdate = mDateUtil.getMethod("createDate", 0, 0, 0);
        if (createdate == null) {
            System.exit(0);
        }
        System.out.println("Limits annotations for method arguments (note: all will be DefaultLimits until annotations are set:");
        lim = mDateUtil.getLimits(createdate);
        for (int j = 0; j < lim.length; j++) {
            System.out.println(" arg " + j + ": min=" + lim[j].min() + ", max=" + lim[j].max() + ", minrequired=" + lim[j].minrequired()
                    + ", maxallowed=" + lim[j].maxallowed() + ", type=" + lim[j].type().getSimpleName());
        }

        System.out.println("Invoking static method DateUtil.createDate with arguments (two invocations)");
        Object from = mDateUtil.invoke(null, createdate, 8, 4, 2008);
        Object to = mDateUtil.invoke(null, createdate, 8, 5, 2008);
        if (from == null || to == null) {
            System.exit(0);
        }

        System.out.println("Locating Method: YahooFinanceLoader.load(symbol, null, from, to)");
        Method load = mLoader.getMethod("load", symbol, null, from, to);
        if (load == null) {
            System.exit(0);
        }

        System.out.println("Invoking loader.load(symbol, null, from, to), returning a Collection");
        Collection list = (Collection)mLoader.invoke(loader, load, symbol, null, from, to);
        if (list == null) {
            System.exit(0);
        }
        System.out.println("List size = " + list.size());
        System.out.println("Printing list elements (shows only class names)");
        int count = 0;
        for (Object obj : list) {
            count++;
            System.out.println(" element " + count + " = " + obj.toString());
        }
    }
}
