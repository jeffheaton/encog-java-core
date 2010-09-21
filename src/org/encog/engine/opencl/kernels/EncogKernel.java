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

package org.encog.engine.opencl.kernels;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.engine.EncogEngineError;
import org.encog.engine.opencl.EncogCLDevice;
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
	private String cl;

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

	private EncogCLDevice device;
	
	private final String sourceName;
	
	/**
	 * The name of the function that should be called to execute this kernel,
	 * from inside the OpenCL source code.
	 */
	private final String kernelName;

	/**
	 * Create an Encog OpenCL kernel. The Kernel will be loaded from an embedded
	 * resource.
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param sourceName
	 *            The name of the kernel, from an embedded resource.
	 * @param kernelName
	 *            The name of the function, in the kernel, called to start the
	 *            kernel.
	 */
	public EncogKernel(final EncogCLDevice device, final String sourceName,
			final String kernelName) {
		this.sourceName = sourceName;
		this.context = device.getPlatform().getContext();
		this.device = device;
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

	/**
	 * @return the device
	 */
	public EncogCLDevice getDevice() {
		return device;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @return the cl
	 */
	public String getCLSource() {
		return cl;
	}

	/**
	 * @param cl the cl to set
	 */
	public void setCLSource(String cl) {
		this.cl = cl;
	}
	
	public void release() {	
		if (this.program != null) {
			CL.clReleaseProgram(this.program);
			CL.clReleaseKernel(this.kernel);
			this.program = null;
			this.kernel = null;
		}
	}
}
