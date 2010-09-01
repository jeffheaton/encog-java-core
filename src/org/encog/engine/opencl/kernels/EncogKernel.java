/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.engine.opencl.kernels;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.engine.EncogEngineError;
import org.encog.engine.util.ResourceLoader;
import org.jocl.CL;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

/**
 * Defines a basic OpenCL kernal, as used by Encog. Contains the kernal source
 * code and a compiled program/kernal.
 */
public class EncogKernel {

	/**
	 * The source code for the kernel.
	 */
	private final String cl;

	/**
	 * The OpenCL context.
	 */
	private final cl_context context;

	/**
	 * The OpenCL program.
	 */
	private cl_program program;

	/**
	 * The OpenCL kernel.
	 */
	private cl_kernel kernel;

	/**
	 * The name of the function that should be called to execute this kernel,
	 * from inside the OpenCL source code.
	 */
	private final String kernelName;

	/**
	 * Create an Encog OpenCL kernel. The Kernel will be loaded from an embedded
	 * resource.
	 * 
	 * @param context
	 *            The OpenCL context that this kernel belongs to.
	 * @param sourceName
	 *            The name of the kernel, from an embedded resource.
	 * @param kernelName
	 *            The name of the function, in the kernel, called to start the
	 *            kernel.
	 */
	public EncogKernel(final cl_context context, final String sourceName,
			final String kernelName) {

		this.context = context;
		this.kernelName = kernelName;
		this.cl = ResourceLoader.loadString(sourceName);
	}

	/**
	 * Compile the kernel with no preprocessor defines.
	 */
	public void compile() {
		compile(new HashMap<String, String>());
	}

	/**
	 * Compile the kernel with a map of preprocessor defines, a collection of
	 * name-value pairs.
	 * 
	 * @param options
	 *            A map of preprocessor defines.
	 */
	public void compile(final Map<String, String> options) {
		// clear out any old program

		if (this.program != null) {
			CL.clReleaseProgram(this.program);
			CL.clReleaseKernel(this.kernel);
		}

		// Create the program from the source code
		final cl_program program = CL.clCreateProgramWithSource(this.context,
				1, new String[] { this.cl }, null, null);

		if (options.size() > 0) {
			final StringBuilder builder = new StringBuilder();
			for (final Entry<String, String> obj : options.entrySet()) {
				if (builder.length() > 0) {
					builder.append(" ");
				}
				builder.append("-D ");
				builder.append(obj.getKey());
				builder.append("=");
				builder.append(obj.getValue());
			}

			CL.clBuildProgram(program, 0, null, builder.toString(), null, null);
		} else {
			CL.clBuildProgram(program, 0, null, null, null, null);
		}

		// Create the kernel
		this.kernel = CL.clCreateKernel(program, this.kernelName, null);
	}

	/**
	 * @return The OpenCL context that this kernel belongs to.
	 */
	public cl_context getContext() {
		return this.context;
	}

	/**
	 * @return The OpenCL kernel.
	 */
	public cl_kernel getKernel() {
		return this.kernel;
	}

	/**
	 * @return The OpenCL program that the kernel belongs to.
	 */
	public cl_program getProgram() {
		return this.program;
	}

	/**
	 * Called internally to prepare to execute a kernel.
	 */
	public void prepareKernel() {
		if (this.kernel == null) {
			throw new EncogEngineError(
					"Must compile CL kernel before using it.");
		}
	}

}
