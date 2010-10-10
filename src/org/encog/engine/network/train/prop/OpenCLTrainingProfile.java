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
import org.encog.engine.EncogEngineError;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.exceptions.OpenCLError;
import org.encog.engine.opencl.kernels.EncogKernel;

/**
 * Specifies a training profile for an OpenCL training session. Includes the
 * following information.
 * 
 * device The device to use.
 * 
 * numGlobalWorkItems The number of global work items. OpenCL devices can only
 * take so many global work items, this is the workload to be sent out to the
 * kernels. The higher this number is, the better performance will be.
 * 
 * itemsPerGlobalWorkItem The number of training items per global work item. How
 * many training elements per global work item. The larger the number of work
 * elements that can be processed at once, the better performance will be. This
 * number can not be higher than the total number of training elements.
 * 
 */
public class OpenCLTrainingProfile {

	/**
	 * The OpenCL device to use.
	 */
	private EncogCLDevice device;
	
	/**
	 * The local r
	 */
	private final double localRatio;
	
	private final double globalRatio;
	
	private final double segmentationRatio;
	
	private int kernelGlobalWorkgroup;
	private int kernelLocalWorkgroup;
	private int kernelItemsPerCall;
	private int kernelWorkPerCall;
	private int kernelNumberOfCalls;
	private int kernelRemainder;
	private int kernelRemainderGlobal;
	private int kernelRemainderPer;
	
	public OpenCLTrainingProfile(EncogCLDevice device, double localRatio,
			double globalRatio, double segmentationRatio) {
		super();
		this.device = device;
		
		if( localRatio<0 || globalRatio<0 || segmentationRatio<0 ) {
			throw new OpenCLError("None of the ratios can be below zero.");
		}
		
		if( localRatio>1.0 ) {
			throw new OpenCLError("The local ratio cannot be greater than 1.0.  That would cause the OpenCL device to have more local items than it can handle.");
		}
		
		if( globalRatio<1.0 ) {
			throw new OpenCLError("The global ratio cannot be less than 1.0.  That would cause the global work area to be less than a local work area.");
		}
		
		if( segmentationRatio>1.0 ) {
			throw new OpenCLError("The segmentation ratio cannot be greater than 1.0.  That would cause the trainer to require more training elements per iteration than exist.");
		}
		
		this.localRatio = localRatio;
		this.globalRatio = globalRatio;
		this.segmentationRatio = segmentationRatio;
	}

	public OpenCLTrainingProfile(EncogCLDevice device) {
		this(device,1.0,1.0,1.0);
	}
	
	public void calculateKernelParams(EncogKernel kernel, EngineIndexableSet training)
	{
		boolean globalValuesAssigned = false;
		int workPerIteration;
		
		if( Math.abs(this.segmentationRatio-1.0)<EncogEngine.DEFAULT_ZERO_TOLERANCE )
		{
			// if the segmentation ratio is 1, then we want NO SEGMENTATION
			// we will have to find a workgroup size that is even
			int trialLocalSize = (int)Math.min( kernel.getMaxWorkGroupSize(), training.getRecordCount() );
			
			trialLocalSize++;// falsely add one so the loop can decrease it with no effect.
			
			// loop and try to find a local size small enough to be even.
			do {
				trialLocalSize--;
				this.kernelLocalWorkgroup = (int)(trialLocalSize * this.localRatio);
				this.kernelGlobalWorkgroup = (int)(this.kernelLocalWorkgroup * this.globalRatio);
				this.kernelWorkPerCall = (int)((training.getRecordCount()/this.kernelGlobalWorkgroup)*this.segmentationRatio);
				workPerIteration = this.kernelGlobalWorkgroup * this.kernelWorkPerCall;
			} while( (workPerIteration!=training.getRecordCount()) && trialLocalSize>1 );
			
			if( trialLocalSize>0 )
				globalValuesAssigned = true;
		}
		
		// if we either wanted to segment, or the attempt to find an even group size above failed
		if( !globalValuesAssigned )
		{
			// otherwise divide into segments
			int maxLocalSize = (int)Math.min( kernel.getMaxWorkGroupSize(), training.getRecordCount() );
			this.kernelLocalWorkgroup = (int)(maxLocalSize * this.localRatio);
			this.kernelGlobalWorkgroup = (int)(this.kernelLocalWorkgroup * this.globalRatio);
			this.kernelWorkPerCall = (int)((training.getRecordCount()/this.kernelGlobalWorkgroup)*this.segmentationRatio);
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
		}
		else
			this.kernelRemainderPer = this.kernelRemainder / this.kernelGlobalWorkgroup;

		// does the remainder not have enough to fill the global tasks global?
		if (this.kernelRemainderPer == 0) {
			this.kernelRemainderPer = 1;
			this.kernelRemainderGlobal = this.kernelRemainder;
		}
	}
	


	public EncogCLDevice getDevice() {
		return device;
	}

	public void setDevice(EncogCLDevice device) {
		this.device = device;
	}

	public double getLocalRatio() {
		return localRatio;
	}

	public double getGlobalRatio() {
		return globalRatio;
	}

	public double getSegmentationRatio() {
		return segmentationRatio;
	}
	
	

	public int getKernelGlobalWorkgroup() {
		return kernelGlobalWorkgroup;
	}

	public int getKernelLocalWorkgroup() {
		return kernelLocalWorkgroup;
	}

	public int getKernelItemsPerCall() {
		return kernelItemsPerCall;
	}

	public int getKernelWorkPerCall() {
		return kernelWorkPerCall;
	}

	public int getKernelNumberOfCalls() {
		return kernelNumberOfCalls;
	}

	public int getKernelRemainder() {
		return kernelRemainder;
	}

	public int getKernelRemainderGlobal() {
		return kernelRemainderGlobal;
	}

	public int getKernelRemainderPer() {
		return kernelRemainderPer;
	}

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

		result.append("kernelItemsPerCall: ");
		result.append(kernelItemsPerCall);
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
