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
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

public class OpenCLTrainingProfile {
	
	private final EncogCLDevice device;
	private final int numGlobalWorkItems;
	private final int itemsPerGlobalWorkItem;
	
	public OpenCLTrainingProfile(EncogCLDevice device, int numGlobalWorkItems,
			int itemsPerGlobalWorkItem) {
		super();
		this.device = device;
		this.numGlobalWorkItems = numGlobalWorkItems;
		this.itemsPerGlobalWorkItem = itemsPerGlobalWorkItem;
	}
	
	public OpenCLTrainingProfile(EncogCLDevice device) {
		this(device,100,10);
	}

	/**
	 * @return the device
	 */
	public EncogCLDevice getDevice() {
		return device;
	}

	/**
	 * @return the numGlobalWorkItems
	 */
	public int getNumGlobalWorkItems() {
		return numGlobalWorkItems;
	}

	/**
	 * @return the itemsPerGlobalWorkItem
	 */
	public int getItemsPerGlobalWorkItem() {
		return itemsPerGlobalWorkItem;
	}

	public static OpenCLTrainingProfile createProfile(final FlatNetwork network, final EngineIndexableSet training) {
		EncogCLDevice device = EncogEngine.getInstance().getCL().chooseDevice();
		if( device.isCPU() ) {
			return createProfileMax(network,training);
		}
		else
			return new OpenCLTrainingProfile(device);
	}

	public static OpenCLTrainingProfile createProfile() {
		EncogCLDevice device = EncogEngine.getInstance().getCL().chooseDevice();
		return new OpenCLTrainingProfile(device);
	}

	public static OpenCLTrainingProfile createProfileMax(FlatNetwork flat,
			EngineIndexableSet training) {
		return createProfileRatio(flat,training,1.0);
	}
	
	public static OpenCLTrainingProfile createProfileRatio(FlatNetwork flat,
			EngineIndexableSet training, double ratio) {
		EncogCLDevice device = EncogEngine.getInstance().getCL().chooseDevice();
		int numGlobalWorkItems = 200;
		int itemsPerGlobalWorkItem = (int)training.getRecordCount();
		return new OpenCLTrainingProfile(device,numGlobalWorkItems,(int)(itemsPerGlobalWorkItem*ratio));
	}
}
