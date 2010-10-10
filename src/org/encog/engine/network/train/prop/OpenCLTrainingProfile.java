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
package org.encog.engine.network.train.prop;

import org.encog.engine.EncogEngine;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.exceptions.OpenCLError;
import org.encog.engine.opencl.kernels.EncogKernel;

/**
 * Specifies a training profile for an OpenCL training session. Includes the
 * following information.
 * 
 * device The device to use.
 * 
 * local ratio: The local workgroup is a OpenCL concept where the global work
 * group is broken into several local work groups. The bigger the local work
 * group the faster things will run. However, your OpenCL device will impose a
 * maximum local work group size. This ratio allows you to use a smaller local
 * work group, for example 0.5 would be half of the max size of the local work
 * group. You will almost always want to leave this value at the max 1.0. It is
 * rare that you might need to decrease it because of the GPU being overtaxed.
 * 
 * 
 * global ratio: The global work group must be a multiple of the local work
 * group. The default value is 1, which means local and global workgroups the
 * same size. Do not set this value lower than 1.0. Values higher than 1.0 can
 * result in higher performance. Should be set to an integer value. For example,
 * 2 would specify a global work workgroup twice the size of the local. Higher
 * values will increase resource load on the GPU and may crash.
 * 
 * segmentation ratio: The main purpose of this ratio is to allow you to scale
 * back on how long the kernels take to execute. For maximum performance leave
 * this value at the default 1.0 value. However, if your GPU is crashing,
 * setting it to a value lower can help. If your are running Encog on the same
 * GPU as your display uses, you may run into timeout issues if your kernel
 * takes too long to execute. Setting this ratio lower can help.
 * 
 */
public class OpenCLTrainingProfile {

	/**
	 * The OpenCL device to use.
	 */
	private EncogCLDevice device;

	/**
	 * The local ratio
	 */
	private final double localRatio;

	/**
	 * The global ratio.
	 */
	private final int globalRatio;

	/**
	 * The segmentation ratio.
	 */
	private final double segmentationRatio;

	/**
	 * The calculated size of the global workgroup.
	 */
	private int kernelGlobalWorkgroup;
	
	/**
	 * The calculated size of the local workgroup.
	 */
	private int kernelLocalWorkgroup;

	/**
	 * The number of training items processed per call.
	 */
	private int kernelWorkPerCall;
	
	/**
	 * The number of calls to the kernel that will be made. The number of segments.
	 */
	private int kernelNumberOfCalls;
	
	/**
	 * The number of items in the remainder.
	 */
	private int kernelRemainder;
	
	/**
	 * The size of the global and local workgroups for the remainder.
	 */
	private int kernelRemainderGlobal;
	
	/**
	 * The number of training items processed per call in the remainder.
	 */
	private int kernelRemainderPer;

	/**
	 * Construct a training profile.
	 * @param device The device to use.
	 * @param localRatio The local ratio.
	 * @param globalRatio The global ratio.
	 * @param segmentationRatio The segmentation ratio.
	 */
	public OpenCLTrainingProfile(EncogCLDevice device, double localRatio,
			int globalRatio, double segmentationRatio) {
		super();
		this.device = device;

		if (localRatio < 0 || globalRatio < 0 || segmentationRatio < 0) {
			throw new OpenCLError("None of the ratios can be below zero.");
		}

		if (localRatio > 1.0) {
			throw new OpenCLError(
					"The local ratio cannot be greater than 1.0.  That would cause the OpenCL device to have more local items than it can handle.");
		}

		if (globalRatio < 1.0) {
			throw new OpenCLError(
					"The global ratio cannot be less than 1.0.  That would cause the global work area to be less than a local work area.");
		}

		if (segmentationRatio > 1.0) {
			throw new OpenCLError(
					"The segmentation ratio cannot be greater than 1.0.  That would cause the trainer to require more training elements per iteration than exist.");
		}

		this.localRatio = localRatio;
		this.globalRatio = globalRatio;
		this.segmentationRatio = segmentationRatio;
	}

	/**
	 * Construct a training profile with the specified device and the value of one for all ratios.
	 * @param device The device to use.
	 */
	public OpenCLTrainingProfile(EncogCLDevice device) {
		this(device, 1.0, 1, 1.0);
	}

	/**
	 * Calculate the kernel values.
	 * @param kernel The kernel to calculate for.
	 * @param training The training params to use.
	 */
	public void calculateKernelParams(EncogKernel kernel,
			EngineIndexableSet training) {
		boolean globalValuesAssigned = false;
		int workPerIteration;

		// there are two special cases

		// first, if the ratio is 1.0
		if (Math.abs(this.segmentationRatio - 1.0) < EncogEngine.DEFAULT_ZERO_TOLERANCE) {
			// if the segmentation ratio is 1, then we want NO SEGMENTATION
			// we will have to find a workgroup size that is even
			int trialLocalSize = (int) Math.min(kernel.getMaxWorkGroupSize(),
					training.getRecordCount());

			trialLocalSize++;// falsely add one so the loop can decrease it with
			// no effect.

			// loop and try to find a local size small enough to be even.
			do {
				trialLocalSize--;
				this.kernelLocalWorkgroup = (int) (trialLocalSize * this.localRatio);
				this.kernelGlobalWorkgroup = (int) (this.kernelLocalWorkgroup * this.globalRatio);
				this.kernelWorkPerCall = (int) ((training.getRecordCount() / this.kernelGlobalWorkgroup) * this.segmentationRatio);
				workPerIteration = this.kernelGlobalWorkgroup
						* this.kernelWorkPerCall;
			} while ((workPerIteration != training.getRecordCount())
					&& trialLocalSize > 1);

			if (trialLocalSize > 0)
				globalValuesAssigned = true;
		}

		// if we either wanted to segment, or the attempt to find an even group
		// size above failed
		if (!globalValuesAssigned) {
			// otherwise divide into segments
			int maxLocalSize = (int) Math.min(kernel.getMaxWorkGroupSize(),
					training.getRecordCount());
			this.kernelLocalWorkgroup = (int) (maxLocalSize * this.localRatio);
			this.kernelGlobalWorkgroup = (int) (this.kernelLocalWorkgroup * this.globalRatio);

			// second special case, if the segmentation ratio is zero, then just
			// do one item per OpenCL call
			if (this.segmentationRatio < EncogEngine.DEFAULT_ZERO_TOLERANCE)
				this.kernelWorkPerCall = 1;
			else
				this.kernelWorkPerCall = (int) ((training.getRecordCount() / this.kernelGlobalWorkgroup) * this.segmentationRatio);
		}

		workPerIteration = this.kernelGlobalWorkgroup * this.kernelWorkPerCall;

		this.kernelNumberOfCalls = (int) (training.getRecordCount() / workPerIteration);
		this.kernelRemainder = (int) (training.getRecordCount() % workPerIteration);

		this.kernelRemainderGlobal = this.kernelGlobalWorkgroup;

		// if there is no "final training set", because it lined up evenly,
		// still create one.
		// the final training set is where learning happens.
		if (this.kernelRemainder == 0) {
			this.kernelRemainder = this.kernelGlobalWorkgroup;
			this.kernelRemainderPer = this.kernelWorkPerCall;
			this.kernelNumberOfCalls--;
		} else
			this.kernelRemainderPer = this.kernelRemainder
					/ this.kernelGlobalWorkgroup;

		// does the remainder not have enough to fill the global tasks global?
		if (this.kernelRemainderPer == 0) {
			this.kernelRemainderPer = 1;
			this.kernelRemainderGlobal = this.kernelRemainder;
		}
	}

	/**
	 * @return The device to use.
	 */
	public EncogCLDevice getDevice() {
		return device;
	}

	/**
	 * Set the device to use.
	 * @param device The device to use.
	 */
	public void setDevice(EncogCLDevice device) {
		this.device = device;
	}

	/**
	 * @return The local ratio.
	 */
	public double getLocalRatio() {
		return localRatio;
	}

	/**
	 * @return The global ratio.
	 */
	public int getGlobalRatio() {
		return globalRatio;
	}

	/**
	 * @return The segmentation ratio.
	 */
	public double getSegmentationRatio() {
		return segmentationRatio;
	}
	
	
	/**
	 * @return The calculated size of the global workgroup.
	 */
	public int getKernelGlobalWorkgroup() {
		return kernelGlobalWorkgroup;
	}

	/**
	 * @return The calculated size of the local workgroup.
	 */
	public int getKernelLocalWorkgroup() {
		return kernelLocalWorkgroup;
	}

	/**
	 * @return The number of training items processed per call.
	 */
	public int getKernelWorkPerCall() {
		return kernelWorkPerCall;
	}

	/**
	 * @return The number of calls to the kernel that will be made. The number of segments.
	 */
	public int getKernelNumberOfCalls() {
		return kernelNumberOfCalls;
	}

	/**
	 * @return The number of items in the remainder.
	 */
	public int getKernelRemainder() {
		return kernelRemainder;
	}

	/**
	 * @return The size of the global and local workgroups for the remainder.
	 */
	public int getKernelRemainderGlobal() {
		return kernelRemainderGlobal;
	}

	/**
	 * @return The number of training items processed per call in the remainder.
	 */
	public int getKernelRemainderPer() {
		return kernelRemainderPer;
	}

	/**
	 * @return All internal values as a string.
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("OpenCL Profile:\n");
		result.append("Local Ratio: ");
		result.append(this.localRatio);
		result.append("\n");
		result.append("Number of global work items: ");
		result.append(this.globalRatio);
		result.append("\n");
		result.append("Segmentation Ratio: ");
		result.append(this.segmentationRatio);
		result.append("\n");
		result.append("Device: ");
		result.append(this.device.toString());
		result.append("\n");

		result.append("kernelGlobalWorkgroup: ");
		result.append(kernelGlobalWorkgroup);
		result.append("\n");

		result.append("kernelLocalWorkgroup: ");
		result.append(kernelLocalWorkgroup);
		result.append("\n");

		result.append("kernelWorkPerCall: ");
		result.append(kernelWorkPerCall);
		result.append("\n");

		result.append("kernelNumberOfCalls: ");
		result.append(kernelNumberOfCalls);
		result.append("\n");

		result.append("kernelRemainder: ");
		result.append(kernelRemainder);
		result.append("\n");

		result.append("kernelRemainderGlobal: ");
		result.append(kernelRemainderGlobal);
		result.append("\n");

		result.append("kernelRemainderPer: ");
		result.append(kernelRemainderPer);
		result.append("\n");

		return result.toString();
	}

}
