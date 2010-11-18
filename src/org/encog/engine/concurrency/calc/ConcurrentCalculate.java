/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.engine.concurrency.calc;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngine;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

/**
 * Class used to provide concurrent calculation between the CPU and OpenCL
 * devices.
 * 
 */
public final class ConcurrentCalculate {

	/**
	 * The instance.
	 */
	private static ConcurrentCalculate instance;

	/**
	 * @return The instance.
	 */
	public static ConcurrentCalculate getInstance() {
		if (ConcurrentCalculate.instance == null) {
			ConcurrentCalculate.instance = new ConcurrentCalculate();
		}
		return ConcurrentCalculate.instance;
	}

	/**
	 * The current network.
	 */
	private FlatNetwork network;
	
	/**
	 * The current training data.
	 */
	private EngineIndexableSet trainingData;

	/**
	 * The OpenCL devices to use.
	 */
	private final List<CalcOpenCLDevice> devices 
		= new ArrayList<CalcOpenCLDevice>();

	/**
	 * True, if we should use OpenCL.
	 */
	private boolean useOpenCL;

	/**
	 * Private constructor.
	 */
	private ConcurrentCalculate() {

	}

	/**
	 * @return THe calculated error.
	 */
	public double calculateError() {
		// if we are using OpenCL, then try to execute on OpenCL first
		if (this.useOpenCL) {
			for (final CalcOpenCLDevice dev : this.devices) {
				final CalculationResult result = dev.calculateError();
				if (result.isExecuted()) {
					return result.getError();
				}
			}
		}

		// use regular CPU code to calculate
		return this.network.calculateError(this.trainingData);
	}

	/**
	 * @return The current network.
	 */
	public FlatNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The current training data.
	 */
	public EngineIndexableSet getTrainingData() {
		return this.trainingData;
	}

	/**
	 * Init for OpenCL.
	 */
	public void initCL() {
		this.devices.clear();
		for (final EncogCLDevice device : EncogEngine.getInstance().getCL()
				.getEnabledDevices()) {
			CalcOpenCLDevice d = new CalcOpenCLDevice(device, this);
			this.devices.add(d);
			if( this.network!=null )
				d.setNetwork(this.network);
			if( this.trainingData!=null )
				d.setTraining(this.trainingData);
		}
		this.useOpenCL = true;
	}

	/**
	 * @return the useOpenCL
	 */
	public boolean isUseOpenCL() {
		return this.useOpenCL;
	}

	/**
	 * Set the current network.
	 * @param network The current network.
	 */
	public void setNetwork(final FlatNetwork network) {
		this.network = network;
		for (final CalcOpenCLDevice dev : this.devices) {
			dev.setTraining(this.trainingData);
		}
	}

	/**
	 * Set the current training data.
	 * @param trainingData The current training data.
	 */
	public void setTrainingData(final EngineIndexableSet trainingData) {
		this.trainingData = trainingData;
		for (final CalcOpenCLDevice dev : this.devices) {
			dev.setTraining(trainingData);
		}
	}

	/**
	 * @param useOpenCL
	 *            the useOpenCL to set
	 */
	public void setUseOpenCL(final boolean useOpenCL) {
		this.useOpenCL = useOpenCL;
	}

}
