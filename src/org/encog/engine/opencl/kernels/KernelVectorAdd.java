/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.EncogCLQueue;
import org.jocl.cl_mem;

/**
 * A very simple kernel, used to add a vector. Not actually used by Encog, it is
 * a simple test case to verify that OpenCL is working.
 */
public class KernelVectorAdd extends EncogKernel {

	/**
	 * The first array.
	 */
	private final float[] arrayA;
	
	/**
	 * The second array.
	 */
	private final float[] arrayB;
	
	/**
	 * The target array.
	 */
	private final float[] targetArray;

	/**
	 * The first buffer.
	 */
	private final cl_mem bufferArrayA;
	
	/**
	 * The second buffer.
	 */
	private final cl_mem bufferArrayB;
	
	/**
	 * The result buffer.
	 */
	private final cl_mem bufferTargetArray;

	/**
	 * Construct a simple kernel to add two vectors.
	 * 
	 * @param device
	 *            The device to use.
	 * @param length
	 * 	The length of the vector.
	 */
	public KernelVectorAdd(final EncogCLDevice device, final int length) {
		super(device, "org/encog/engine/resources/KernelVectorAdd.txt",
				"VectorAdd");
		// Create input- and output data
		this.arrayA = new float[length];
		this.arrayB = new float[length];
		this.targetArray = new float[length];

		this.bufferArrayA = this.createArrayReadOnly(this.arrayA);
		this.bufferArrayB = this.createArrayReadOnly(this.arrayB);
		this.bufferTargetArray = createFloatArrayWriteOnly(this.targetArray.length);

		setGlobalWork(length);
		setLocalWork(1);
	}

	/**
	 * Perform the addition.
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param inputA
	 *            The first vector to add.
	 * @param inputB
	 *            The second vector to add.
	 * @return The result of the addition.
	 */
	public double[] add(final EncogCLDevice device, final double[] inputA,
			final double[] inputB) {

		for (int i = 0; i < inputA.length; i++) {
			this.arrayA[i] = (float) inputA[i];
			this.arrayB[i] = (float) inputB[i];
		}

		setArg(0, this.bufferArrayA);
		setArg(1, this.bufferArrayB);
		setArg(2, this.bufferTargetArray);

		final EncogCLQueue queue = getDevice().getQueue();

		queue.array2Buffer(this.arrayA, this.bufferArrayA);
		queue.array2Buffer(this.arrayB, this.bufferArrayB);

		queue.execute(this);

		queue.buffer2Array(this.bufferTargetArray, this.targetArray);

		final double[] result = new double[this.targetArray.length];

		for (int i = 0; i < this.targetArray.length; i++) {
			result[i] = this.targetArray[i];
		}

		return result;
	}

}
