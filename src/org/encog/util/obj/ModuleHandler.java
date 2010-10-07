/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.obj;

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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * @author Edwin Floyd (adapted from ClassList.java by Kris Dover; see below)
 */

/**
 * From the original code found at
 * http://www.xmlizer.biz/java/classloader/ClassList.java . Note, the Interface
 * filter on FindClasses() doesn't work well in the original, so it has been
 * eliminated and the result changed to return a simple set of classes.
 * 
 * Additional methods, annotations, and classes have been added to facilitate
 * run-time argument editing and class instantiation by a parser or visual
 * editor.
 * 
 * From the original ClassList JavaDoc:
 * 
 * <em>Caveat:</em> When used in environments which utilize multiple class
 * loaders--such as a J2EE Container like Tomcat--it is important to select the
 * correct classloader otherwise the classes returned, if any, will be
 * incompatible with those declared in the code employing this class lister. to
 * get a reference to your classloader within an instance method use:
 * <code>this.getClass().getClassLoader()</code> or
 * <code>Thread.currentThread().getContextClassLoader()</code> anywhere else
 * <p>
 * 
 * @author Kris Dover <krisdover@hotmail.com>
 * @version 0.2.0
 * @since 0.1.0
 */
public final class ModuleHandler {

	/**
	 * Private constructor.
	 */
	private ModuleHandler() {
		
	}
	
	/**
	 * ClassInfo provides a container and methods for working with classes,
	 * constructors, and arguments at run-time.
	 */
	public static class ClassInfo {
		/**
		 * Gets the data type for each argument in the list in a form suitable
		 * for input to isArgumentMatch().
		 * 
		 * @param arguments
		 *            an array of Objects that represent arguments for a method
		 *            or constructor invocation
		 * @return an array of Objects that represent the data types for these
		 *         arguments
		 */
		private static Class< ? >[] getTypes(final Object... arguments) {
			final Class< ? >[] types = new Class[arguments.length];
			for (int j = 0; j < types.length; j++) {
				if (arguments[j] == null) {
					types[j] = null;
				} else {
					types[j] = arguments[j].getClass();
				}
				if (types[j] == Integer.class) {
					types[j] = int.class;
				} else if (types[j] == Double.class) {
					types[j] = double.class;
				} else if (types[j] == Long.class) {
					types[j] = long.class;
				} else if (types[j] == Short.class) {
					types[j] = short.class;
				} else if (types[j] == Float.class) {
					types[j] = float.class;
				} else if (types[j] == Byte.class) {
					types[j] = byte.class;
				} else if (types[j] == Character.class) {
					types[j] = char.class;
				} else if (types[j] == Boolean.class) {
					types[j] = boolean.class;
				}
			}
			return types;
		}

		/**
		 * Determine whether a list of method or constructor formal parameter
		 * types matches the argument types for an invocation.
		 * 
		 * @param argtypes
		 *            an array of Objects that represent the argument data types
		 *            for a method or constructor invocation, such as might have
		 *            been returned by getTypes().
		 * @param formaltypes
		 *            an array of Objects that represent formal parameter types
		 *            for a method or constructor, such as might have been
		 *            returned by getParameterTypes().
		 * @param isvar
		 *            boolean flag indicating whether the method or constructor
		 *            formal parameter list ends with a variable length
		 *            argument.
		 * @return true if the argument lists match such that an invocation with
		 *         argtypes would succeed, false otherwise.
		 */
		private static boolean isArgumentMatch(final Class< ? >[] argtypes,
				final Class< ? >[] formaltypes, final boolean isvar) {
			final int n = isvar ? formaltypes.length - 1 : formaltypes.length;
			boolean match = false;
			if ((isvar && (formaltypes.length <= argtypes.length))
					|| (formaltypes.length == argtypes.length)) {
				match = true;
				for (int k = 0; k < n; k++) {
					if (argtypes[k] == null) {
						if (formaltypes[k].isPrimitive()) {
							match = false;
							break;
						}
					} else if (!formaltypes[k].isAssignableFrom(argtypes[k])) {
						match = false;
						break;
					}
				}
				if (match && isvar && (argtypes.length >= formaltypes.length)) {
					final Class< ? > vartype 
						= formaltypes[n].getComponentType();
					for (int k = n; k < argtypes.length; k++) {
						if (argtypes[k] == null) {
							if (vartype.isPrimitive()) {
								match = false;
								break;
							}
						} else if (!vartype.isAssignableFrom(argtypes[k])) {
							match = false;
							break;
						}
					}
				}
			}
			return match;
		}

		/**
		 * Prepares arguments for method or constructor invocation, mainly by
		 * resolving a variable parameter at the end, if present.
		 * 
		 * @param formaltypes
		 *            Array of formal parameter types from Method or Constructor
		 *            getParameterTypes()
		 * @param isvar
		 *            Flag indicating presence of variable length arguments from
		 *            Method or Constructor isVarArgs()
		 * @param arguments
		 *            Variable length list of arguments for method or
		 *            constructor invocation
		 * @return Array of arguments suitable for method or constructor
		 *         invocation
		 */
		private static Object[] prepareArguments(final Class< ? >[] formaltypes,
				final boolean isvar, final Object... arguments) {
			if (!isvar) {
				return arguments;
			}
			final Object[] parm = new Object[formaltypes.length];
			final Object varpart = Array.newInstance(
					formaltypes[formaltypes.length - 1].getComponentType(),
					arguments.length - formaltypes.length + 1);
			parm[parm.length - 1] = varpart;
			for (int j = 0; j < arguments.length; j++) {
				if (j >= parm.length - 1) {
					Array.set(varpart, j - parm.length + 1, arguments[j]);
				} else {
					parm[j] = arguments[j];
				}
			}
			return parm;
		}

		/** The class that this ClassInfo instance represents. */
		Class< ? > theclass;
		/** A list of public constructors for this class. */
		Constructor< ? >[] constructors;
		/** A list of interfaces implemented by this class and 
		 * any superclasses. */
		Class< ? >[] interfaces;

		/** A list of public methods for this class. */
		Method[] methods;

		/**
		 * The ClassInfo instance that represents the superclass to this class.
		 * This is null if the superclass was not in the package list specified
		 * in the call to ModuleHandler.createModuleList().
		 */
		private ClassInfo superclass; 
		// Superclass info (if it survived the filter)

		/**
		 * Locates the first Constructor that matches a given argument list.
		 * Note: Unlike Class.getConstructor(), this method succeeds when
		 * arguments contain extensions of formal parameter classes or
		 * interfaces.
		 * 
		 * @param arguments
		 *            Variable length list of arguments, just as they would be
		 *            passed to the constructor
		 * @return The Constructor matching the given arguments list, or null if
		 *         no matching constructor is found
		 */
		public Constructor< ? > getConstructor(final Object... arguments) {
			final Class< ? >[] types = ClassInfo.getTypes(arguments);
			for (final Constructor< ? > c : this.constructors) {
				if (ClassInfo.isArgumentMatch(types, c.getParameterTypes(), c
						.isVarArgs())) {
					return c;
				}
			}
			return null;
		}

		/**
		 * Return the Limits annotations associated with the formal parameters
		 * defined for a Constructor.
		 * 
		 * @param constructor
		 *            The Constructor whose formal parameter Limits annotations
		 *            are to be returned
		 * @return an array of Limits annotations associated with the specified
		 *         constructor's parameters. If a formal parameter has no Limits
		 *         annotation, an appropriate DefaultLimits instance is created
		 *         and returned for that parameter.
		 */
		public Limits[] getLimits(final Constructor< ? > constructor) {
			final Annotation[][] pa = constructor.getParameterAnnotations();
			final Class< ? >[] pt = constructor.getParameterTypes();
			if (constructor.isVarArgs()) {
				pt[pt.length - 1] = pt[pt.length - 1].getComponentType();
			}
			final Limits[] lim = new Limits[pa.length];
			for (int j = 0; j < lim.length; j++) {
				for (final Annotation a : pa[j]) {
					if (a.annotationType() == Limits.class) {
						lim[j] = (Limits) a;
					}
				}
				if (lim[j] == null) {
					int mr = 1;
					int ma = 1;
					if ((j == lim.length - 1) && constructor.isVarArgs()) {
						mr = 0;
						ma = Integer.MAX_VALUE;
					}
					lim[j] = new DefaultLimits(pt[j], mr, ma);
				}
			}
			return lim;
		}

		/**
		 * Return the Limits annotations associated with the formal parameters
		 * defined for a Method.
		 * 
		 * @param method
		 *            The Method whose formal parameter Limits annotations are
		 *            to be returned
		 * @return an array of Limits annotations associated with the specified
		 *         method parameters. If a formal parameter has no Limits
		 *         annotation, an appropriate DefaultLimits instance is created
		 *         and returned for that parameter.
		 */
		public Limits[] getLimits(final Method method) {
			final Annotation[][] pa = method.getParameterAnnotations();
			final Class< ? >[] pt = method.getParameterTypes();
			if (method.isVarArgs()) {
				pt[pt.length - 1] = pt[pt.length - 1].getComponentType();
			}
			final Limits[] lim = new Limits[pa.length];
			for (int j = 0; j < lim.length; j++) {
				for (final Annotation a : pa[j]) {
					if (a.annotationType() == Limits.class) {
						lim[j] = (Limits) a;
					}
				}
				if (lim[j] == null) {
					int mr = 1;
					int ma = 1;
					if ((j == lim.length - 1) && method.isVarArgs()) {
						mr = 0;
						ma = Integer.MAX_VALUE;
					}
					lim[j] = new DefaultLimits(pt[j], mr, ma);
				}
			}
			return lim;
		}

		/**
		 * Locates the first Method that matches a given argument list. Note:
		 * Unlike Class.getMethod(), this method succeeds when actual arguments
		 * contain extensions of formal arguments classes or interfaces.
		 * 
		 * @param arguments
		 *            Variable length list of arguments, just as they would be
		 *            passed to the method.
		 * @param methodname
		 * 		The method name.
		 * @return The Method matching the given name and arguments list, or
		 *         null if no matching method is found
		 */
		public Method getMethod(final String methodname,
				final Object... arguments) {
			final Class< ? >[] types = ClassInfo.getTypes(arguments);
			for (final Method m : this.methods) {
				if (m.getName().equals(methodname)
						&& ClassInfo.isArgumentMatch(types, m
								.getParameterTypes(), m.isVarArgs())) {
					return m;
				}
			}
			return null;
		}

		/**
		 * Return an initialized instance of the class represented by this
		 * ClassInfo instance using the constructor that matches the given
		 * arguments list.
		 * 
		 * @param arguments
		 *            A variable length list of arguments for the constructor
		 * @return An instance of the class represented by this ClassInfo
		 *         instance, or null if no matching constructor could be found
		 *         or an error occurred during initialization.
		 */
		public Object instanceOf(final Object... arguments) {
			final Constructor< ? > c = getConstructor(arguments);
			if (c == null) {
				System.err.println("Unable to match constructor for "
						+ this.theclass.getName());
				return null;
			}
			try {
				return c.newInstance(ClassInfo.prepareArguments(c
						.getParameterTypes(), c.isVarArgs(), arguments));
			} catch (final Exception ex) {
				System.err.println("Unable to instantiate "
						+ this.theclass.getName() + ", exception: "
						+ ex.getCause());
				return null;
			}
		}

		/**
		 * Invoke a method defined in the class represented by this ClassInfo
		 * instance.
		 * 
		 * @param instance
		 *            For instance methods, this is the instance to use; for
		 *            Class methods (static), this is ignored and may be null.
		 * @param method
		 *            The Method to invoke, as returned by getMethod().
		 * @param arguments
		 *            A variable length list of arguments for this method
		 *            invocation
		 * @return The return value or null if the method type is void
		 */
		public Object invoke(final Object instance, final Method method,
				final Object... arguments) {
			try {
				return method.invoke(instance, ClassInfo.prepareArguments(
						method.getParameterTypes(), method.isVarArgs(),
						arguments));
			} catch (final Exception ex) {
				System.err.println("Unable to invoke method "
						+ instance.getClass().getSimpleName() + "."
						+ method.getName() + " because: " + ex.getCause());
			}
			return null;
		}

		/**
		 * Determine if the class represented by this ClassInfo instance is an
		 * extension of a specified class.
		 * 
		 * @param aclass
		 *            the class to test as a possible ancestor
		 * @return true if the class represented by this ClassInfo instance is a
		 *         descendent of the specified class
		 */
		public boolean isExtensionOf(final Class< ? > aclass) {
			Class< ? > c = this.theclass;
			while (c != null) {
				if (c == aclass) {
					return true;
				}
				c = c.getSuperclass();
			}
			return false;
		}

		/**
		 * Determine if the class represented by this ClassInfo instance
		 * implements a specified interface.
		 * 
		 * @param aninterface
		 *            the interface to test for implementation
		 * @return true if the class represented by this ClassInfo instance
		 *         implements the specified interface
		 */
		public boolean isImplementationOf(final Class< ? > aninterface) {
			for (final Class< ? > c : this.interfaces) {
				if (c == aninterface) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * DefaultLimits is a pre-defined instance of Limits that returns the
	 * default values.
	 */
	private static class DefaultLimits implements Limits {
		private final Class< ? > t;
		private final int minrequired;
		private final int maxallowed;

		public DefaultLimits() {
			this(void.class, 1, 1);
		}

		public DefaultLimits(final Class t, final int minrequired,
				final int maxallowed) {
			this.t = t;
			this.minrequired = minrequired;
			this.maxallowed = maxallowed;
		}

		public Class<? extends Annotation> annotationType() {
			return Limits.class;
		}

		public double max() {
			return Double.POSITIVE_INFINITY;
		}

		public int maxallowed() {
			return this.maxallowed;
		}

		public double min() {
			return Double.NEGATIVE_INFINITY;
		}

		public int minrequired() {
			return this.minrequired;
		}

		public Class type() {
			return this.t;
		}
	};

	/**
	 * Limits may be used to set runtime-accessible information on method or
	 * constructor arguments via Java Annotation mechanism. The intent is to
	 * externalize argument limits so these can be enforced at run-time by a
	 * parser or visual editor. Example:
	 * 
	 * <pre>
	 *   public IndicatorEMA(
	 *       Comparable name,
	 * <code>
	 * @Limits(type=AbstractChartProvider.class) AbstractChartProvider source,
	 * </code>
	 *       <code>
	 * @Limits(type=int.class,min=0) int part,
	 * </code>
	 *       <code>
	 * @Limits(type=int.class,min=2,minrequired=1,maxallowed=10) int... bars)
	 * </code>
	 *   {...
	 * </pre>
	 * 
	 * At runtime,
	 *                                                           this argument
	 *                                                           information can
	 *                                                           be retrieved by
	 *                                                           ClassInfo.getLimits();
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@Inherited
	public static @interface Limits {
		/**
		 * Specifies the maximum allowed value for numeric arguments. Default is
		 * positive infinity.
		 * 
		 * @return maximum allowed value.
		 */
		double max() default Double.POSITIVE_INFINITY;

		/**
		 * For variable argument list, specifies the maximum number of entries
		 * allowed. (Leave default [1] for non-variable.)
		 * 
		 * @return maximum number of variable arguments allowed.
		 */
		int maxallowed() default 1;

		/**
		 * Specifies the minimum allowed value for numeric arguments. Default is
		 * negative infinity.
		 * 
		 * @return minimum allowed value
		 */
		double min() default Double.NEGATIVE_INFINITY;

		/**
		 * For variable argument list, specifies the minimum number of entries
		 * required. (Leave default [1] for non-variable.)
		 * 
		 * @return minimum number of variable arguments required.
		 */
		int minrequired() default 1;

		/**
		 * Specifies the expected class type for non-numeric arguments.
		 * 
		 * @return the expected class
		 */
		Class type() default void.class;
	}

	/**
	 * Creates an array of ClassInfo entries.
	 * 
	 * @param packages
	 *            A variable length list of package names from which to select
	 *            classes. A package name ending in ".*" is taken to be a wild
	 *            card and will match any package name beginning with the part
	 *            of the name prior to the ".*", otherwise, the package must
	 *            match exactly. Leave empty to select all packages.
	 * @return Returns an array of ClassInfo elements, one for each Class in the
	 *         selected packages.
	 */
	public static ClassInfo[] createModuleList(final String... packages) {
		final ArrayList<ClassInfo> list = new ArrayList<ClassInfo>();
		Set<String> pkgfilter = null;
		if (packages.length > 0) {
			pkgfilter = new HashSet<String>();
			Collections.addAll(pkgfilter, packages);
		}
		for (final Class< ? > c : ModuleHandler.findClasses(ClassLoader
				.getSystemClassLoader(), pkgfilter, null, false, null)) {
			final ClassInfo info = new ClassInfo();
			info.theclass = c;
			try {
				info.constructors = c.getConstructors();
			} catch (final NoClassDefFoundError ex) {
				System.err.println("Unable to get constructors for "
						+ info.theclass.getSimpleName() + ": " + ex.getCause());
				info.constructors = new Constructor[0];
			}
			try {
				info.methods = c.getMethods();
			} catch (final NoClassDefFoundError ex) {
				System.err.println("Unable to get methods for "
						+ info.theclass.getSimpleName() + ": " + ex.getCause());
				info.methods = new Method[0];
			}
			list.add(info);
		}
		for (final ClassInfo info : list) {
			final ArrayList<Class< ? >> intfc = new ArrayList<Class< ? >>();
			Class< ? > c = info.theclass;
			final Class< ? > sup = c.getSuperclass();
			for (final ClassInfo si : list) {
				if (si.theclass == sup) {
					info.superclass = si;
					break;
				}
			}
			while (c != null) {
				Collections.addAll(intfc, c.getInterfaces());
				c = c.getSuperclass();
			}
			info.interfaces = new Class[intfc.size()];
			intfc.toArray(info.interfaces);
		}
		final ClassInfo[] info = new ClassInfo[list.size()];
		list.toArray(info);
		return info;
	}

	/**
	 * Searches the classpath for all classes matching a specified search
	 * criteria, returning them in a set. The search criteria can be specified
	 * via package and jar name.
	 * <p>
	 * 
	 * @param classLoader
	 *            The classloader whose classpath will be traversed
	 * @param packageFilter
	 *            A Set of fully qualified package names to search for or or
	 *            null to return classes in all packages
	 * @param jarFilter
	 *            A Set of jar file names to search for or null to return
	 *            classes from all jars
	 * @param inner
	 *            A boolean flag indicating whether inner or anonymous classes
	 *            should be returned
	 * @param classTable
	 *            A Set to which classes are to be added. If this is null,
	 *            findClasses() creates one, if not, it returns this one with
	 *            new classes added
	 * @return A Set of Classes
	 */
	public static Set<Class> findClasses(final ClassLoader classLoader,
			final Set<String> packageFilter, final Set<String> jarFilter,
			final boolean inner, Set<Class> classTable) {
		if (classTable == null) {
			classTable = new HashSet();
		}
		Set<String> wildPackageFilter = null;
		for (final Iterator<String> it = packageFilter.iterator(); it.hasNext();) {
			final String s = it.next();
			if (s.endsWith(".*")) {
				if (wildPackageFilter == null) {
					wildPackageFilter = new HashSet();
				}
				wildPackageFilter.add(s.substring(0, s.length() - 2));
				it.remove();
			}
		}
		Object[] classPaths;
		try { // get a list of all classpaths
			classPaths = ((java.net.URLClassLoader) classLoader).getURLs();
		} catch (final ClassCastException cce) { // cast failed; tokenize the
													// system classpath
			classPaths = System.getProperty("java.class.path", "").split(
					File.pathSeparator);
		}

		for (final Object path : classPaths) {
			final File classPath = new File(
					(URL.class).isInstance(path) ? ((URL) path).getFile()
							: path.toString());
			final ArrayList<String> files = new ArrayList<String>();
			JarFile module = null;
			if (classPath.isDirectory() && (jarFilter == null)) { // path is
																	// directory,
																	// no jar
																	// filter
				ModuleHandler.recursivelyListDir(files, classPath,
						new StringBuffer());
			} else if (classPath.getName().endsWith(".jar")
					|| classPath.getName().endsWith(".zip")) { // path is jar
																// or zip
				if ((jarFilter != null)
						&& !jarFilter.contains(classPath.getName())) { // not
																		// in
																		// filter,
																		// skip
																		// it
					continue;
				}
				try {
					module = new JarFile(classPath);
				} catch (final MalformedURLException mue) {
					System.err.println("Bad classpath. Error:"
							+ mue.getMessage());
					continue;
				} catch (final IOException io) {
					System.err
							.println("jar file '"
									+ classPath.getName()
									+ "' could not be instantiate from file path. Error: "
									+ io.getMessage());
					continue;
				}
				for (final Enumeration ent = module.entries(); ent
						.hasMoreElements();) {
					files.add(ent.nextElement().toString());
				}
			}

			for (final String fileName : files) {
				if (fileName.endsWith(".class")) {
					final String className = fileName.replaceAll("/", ".")
							.substring(0, fileName.length() - 6);
					boolean isRequestedPackage = (packageFilter == null)
							&& (wildPackageFilter == null);
					if ((packageFilter != null)
							&& (className.lastIndexOf(".") > 0)
							&& packageFilter.contains(className.substring(0,
									className.lastIndexOf(".")))) {
						isRequestedPackage = true;
					}
					if (!isRequestedPackage && (wildPackageFilter != null)
							&& (className.lastIndexOf(".") > 0)) {
						String packageName = className.substring(0, className
								.lastIndexOf("."));
						while (packageName.length() > 0) {
							if (wildPackageFilter.contains(packageName)) {
								isRequestedPackage = true;
								break;
							}
							if (packageName.lastIndexOf(".") > 0) {
								packageName = packageName.substring(0,
										packageName.lastIndexOf("."));
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
					} catch (final ClassNotFoundException e) {
						System.out.println("Skipping class '" + className
								+ "' for reason " + e.getMessage());
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
				} catch (final IOException ioe) {
					// do nothing
				}
			}
		}
		return classTable;
	}

	/**
	 * Locate a specific class by name in the ClassInfo array.
	 * 
	 * @param classname
	 *            String containing the name of the class. Name need not be
	 *            fully qualified.
	 * @param info
	 *            An array of ClassInfo instances, such as might be returned by
	 *            createModuleList()
	 * @return The matching ClassInfo entry from the info array, or null if the
	 *         specified class is not in the info array
	 */
	public static ClassInfo findModule(final String classname,
			final ClassInfo[] info) {
		final String cn = "." + classname;
		for (final ClassInfo inf : info) {
			final String name = inf.theclass.getName();
			if (name.endsWith(cn) || name.equals(classname)) {
				return inf;
			}
		}
		return null;
	}

	/**
	 * Return an initialized instance of the class specified by classname. This
	 * static method searches the ClassInfo[] array for the specified class name
	 * and, if located, returns the result of the associated
	 * ClassInfo.instanceOf() method.
	 * 
	 * @param classname
	 *            The name of the class to instantiate. Name need not be fully
	 *            qualified.
	 * @param info
	 *            An array of ClassInfo instances, such as might be returned by
	 *            createModuleList()
	 * @param arguments
	 *            A variable length list of arguments for the constructor
	 * @return An instance of the specified class, or null if the specified
	 *         class is not in the info array or no matching constructor could
	 *         be found or an error occurred during initialization.
	 */
	public static Object instanceOf(final String classname,
			final ClassInfo[] info, final Object... arguments) {
		final ClassInfo inf = ModuleHandler.findModule(classname, info);
		if (inf != null) {
			return inf.instanceOf(arguments);
		}
		return null;
	}

	/**
	 * Recursively lists a directory while generating relative paths. This is a
	 * helper function for findClasses. Note: Uses a StringBuffer to avoid the
	 * excessive overhead of multiple String concatenation
	 * 
	 * @param dirListing
	 *            A list variable for storing the directory listing as a list of
	 *            Strings
	 * @param dir
	 *            A File for the directory to be listed
	 * @param relativePath
	 *            A StringBuffer used for building the relative paths
	 */
	private static void recursivelyListDir(final List<String> dirListing,
			final File dir, final StringBuffer relativePath) {
		int prevLen; // used to undo append operations to the StringBuffer

		// if the dir is really a directory
		if (dir.isDirectory()) {
			// get a list of the files in this directory
			final File[] files = dir.listFiles();
			// for each file in the present dir
			for (final File element : files) {
				// store our original relative path string length
				prevLen = relativePath.length();
				// call this function recursively with file list from present
				// dir and relateveto appended with present dir
				ModuleHandler.recursivelyListDir(dirListing, element,
						relativePath.append(prevLen == 0 ? "" : "/").append(
								element.getName()));
				// delete subdirectory previously appended to our relative path
				relativePath.delete(prevLen, relativePath.length());
			}
		} else {
			// this dir is a file; append it to the relativeto path and add it
			// to the directory listing
			dirListing.add(relativePath.toString());
		}
	}

}
