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
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

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
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. Try to determine the best values for the number of global work
	 * items and OpenCL ratio. For this function call, you are not even
	 * providing the network or training set, so it is unlikely that good values
	 * will be determined for the global items or ratio.
	 * 
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfile() {
		final EncogCLDevice device = EncogEngine.getInstance().getCL()
				.chooseDevice();
		return new OpenCLTrainingProfile(device);
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. Try to determine the best values for the number of global work
	 * items and OpenCL ratio.
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfile(
			final FlatNetwork network, final EngineIndexableSet training) {
		final EncogCLDevice device = EncogEngine.getInstance().getCL()
				.chooseDevice();
		if (device.isCPU()) {
			return OpenCLTrainingProfile.createProfileMax(network, training);
		} else {
			return new OpenCLTrainingProfile(device);
		}
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. Use the max values for the number of global work items and
	 * OpenCL ratio. If your GPU can handle it, this will provide the best
	 * performance. Note, that you might see your OS reboot your GPU if the
	 * kernel takes too long to execute.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileMax(
			final FlatNetwork network, final EngineIndexableSet training) {
		return OpenCLTrainingProfile.createProfileRatio(network, training, 1.0);
	}

	/**
	 * Create a profile a specific OpenCL device. The ratio allows you to
	 * specify how much of the training set should be sent to the kernel.
	 * Specify 1.0 for max performance, or 0.5 to send only half. The higher
	 * this value, the more likely your OS may timeout your kernel, especially
	 * with a less powerful GPU.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @param ratio
	 *            The ratio to use. Specify 1.0(max) to process the entire
	 *            training set with each call to the OpenCL kernel.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileRatio(
			final EncogCLDevice device, final FlatNetwork network,
			final EngineIndexableSet training, final double ratio) {
		final int numGlobalWorkItems = 200;
		final int itemsPerGlobalWorkItem = (int) training.getRecordCount();
		return new OpenCLTrainingProfile(device, numGlobalWorkItems,
				(int) (itemsPerGlobalWorkItem * ratio));
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. The ratio allows you to specify how much of the training set
	 * should be sent to the kernel. Specify 1.0 for max performance, or 0.5 to
	 * send only half. The higher this value, the more likely your OS may
	 * timeout your kernel, especially with a less powerful GPU.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @param ratio
	 *            The ratio to use. Specify 1.0(max) to process the entire
	 *            training set with each call to the OpenCL kernel.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileRatio(
			final FlatNetwork network, final EngineIndexableSet training,
			final double ratio) {
		final EncogCLDevice device = EncogEngine.getInstance().getCL()
				.chooseDevice();
		return OpenCLTrainingProfile.createProfileRatio(device, network, training,
				ratio);
	}

	/**
	 * The OpenCL device to use.
	 */
	private final EncogCLDevice device;

	/**
	 * The number of global work items.
	 */
	private final int numGlobalWorkItems;

	/**
	 * The number of elements per global work item.
	 */
	private final int itemsPerGlobalWorkItem;

	/**
	 * Create a default OpenCL training profile. Use 100 global workload items
	 * and 10 training elements per call to the kernel.
	 * 
	 * @param device
	 *            The device to use.
	 */
	public OpenCLTrainingProfile(final EncogCLDevice device) {
		this(device, 100, 10);
	}

	/**
	 * Construct an OpenCL training profile.
	 * 
	 * @param device
	 *            The device to use.
	 * @param numGlobalWorkItems
	 *            The number of global work items. OpenCL devices can only take
	 *            so many global work items, this is the workload to be sent out
	 *            to the kernels. The higher this number is, the better
	 *            performance will be.
	 * @param itemsPerGlobalWorkItem
	 *            The number of training items per global work item. How many
	 *            training elements per global work item. The larger the number
	 *            of work elements that can be processed at once, the better
	 *            performance will be. This number can not be higher than the
	 *            total number of training elements.
	 */
	public OpenCLTrainingProfile(final EncogCLDevice device,
			final int numGlobalWorkItems, final int itemsPerGlobalWorkItem) {
		super();
		this.device = device;
		this.numGlobalWorkItems = numGlobalWorkItems;
		this.itemsPerGlobalWorkItem = itemsPerGlobalWorkItem;
	}

	/**
	 * @return The device used.
	 */
	public EncogCLDevice getDevice() {
		return this.device;
	}

	/**
	 * @return the itemsPerGlobalWorkItem
	 */
	public int getItemsPerGlobalWorkItem() {
		return this.itemsPerGlobalWorkItem;
	}

	/**
	 * @return the numGlobalWorkItems
	 */
	public int getNumGlobalWorkItems() {
		return this.numGlobalWorkItems;
	}
}
