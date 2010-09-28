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
package org.encog.engine.opencl;

import org.encog.engine.opencl.kernels.EncogKernel;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_mem;

/**
 * An open CL queue.
 */
public class EncogCLQueue {

	/**
	 * A command queue for this device.
	 */
	private final cl_command_queue commands;

	/**
	 * The device to use.
	 */
	private final EncogCLDevice device;

	/**
	 * Construct a device.
	 * @param device The OpenCL device to base on.
	 */
	public EncogCLQueue(final EncogCLDevice device) {
		final EncogCLPlatform platform = device.getPlatform();
		this.device = device;
		this.commands = CL.clCreateCommandQueue(platform.getContext(), device
				.getDevice(), 0, null);
	}

	/**
	 * Copy a float array to a buffer.
	 * @param source The array.
	 * @param targetBuffer The buffer.
	 */
	public void array2Buffer(final float[] source, final cl_mem targetBuffer) {
		CL.clEnqueueWriteBuffer(this.commands, targetBuffer, CL.CL_TRUE, 0,
				Sizeof.cl_float * source.length, Pointer.to(source), 0, null,
				null);
	}

	/**
	 * Copy an int array to a buffer.
	 * @param source The source array.
	 * @param targetBuffer The buffer.
	 */
	public void array2Buffer(final int[] source, final cl_mem targetBuffer) {
		CL.clEnqueueWriteBuffer(this.commands, targetBuffer, CL.CL_TRUE, 0,
				Sizeof.cl_int * source.length, Pointer.to(source), 0, null,
				null);
	}

	/**
	 * Copy a buffer to a float array.
	 * @param sourceBuffer The source buffer.
	 * @param target The target array.
	 */
	public void buffer2Array(final cl_mem sourceBuffer, final float[] target) {
		CL.clEnqueueReadBuffer(this.commands, sourceBuffer, CL.CL_TRUE, 0,
				target.length * Sizeof.cl_float, Pointer.to(target), 0, null,
				null);
	}

	/**
	 * Copy a buffer to an int array.
	 * @param sourceBuffer The source buffer.
	 * @param target The target array.
	 */
	public void buffer2Array(final cl_mem sourceBuffer, final int[] target) {
		CL.clEnqueueReadBuffer(this.commands, sourceBuffer, CL.CL_TRUE, 0,
				target.length * Sizeof.cl_int, Pointer.to(target), 0, null,
				null);
	}

	/**
	 * Execute the specified kernel.  
	 * @param kernel The kernel to execute.
	 */
	public void execute(final EncogKernel kernel) {
		final long[] globalWorkSize = new long[] { kernel.getGlobalWork() };
		final long[] localWorkSize = new long[] { kernel.getLocalWork() };

		// Execute the kernel
		CL.clEnqueueNDRangeKernel(this.commands, kernel.getKernel(), 1, null,
				globalWorkSize, localWorkSize, 0, null, null);

	}

	/**
	 * @return The OpenCL command queue.
	 */
	public cl_command_queue getCommands() {
		return this.commands;
	}

	/**
	 * Wait until the queue is finished.
	 */
	public void waitFinish() {
		CL.clFinish(this.commands);
	}

	/**
	 * @return The device to use.
	 */
	public EncogCLDevice getDevice() {
		return device;
	}
	
	

}
