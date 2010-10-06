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

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngineError;
import org.encog.engine.opencl.exceptions.MissingOpenCLAdapterError;
import org.jocl.CL;
import org.jocl.cl_platform_id;

/**
 * An OpenCL platform. A platform is a collection of OpenCL devices from the
 * same vendor. Often, you will have only a single platform.
 */
public class EncogCL {
	/**
	 * The platforms detected.
	 */
	private final List<EncogCLPlatform> platforms = new ArrayList<EncogCLPlatform>();

	/**
	 * All devices, from all platforms.
	 */
	private final List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

	/**
	 * Construct an Encog OpenCL object.
	 */
	public EncogCL() {
		try {
			final int[] numPlatforms = new int[1];

			final cl_platform_id[] platformIDs = new cl_platform_id[5];
			CL.clGetPlatformIDs(platformIDs.length, platformIDs, numPlatforms);

			if (numPlatforms[0] == 0) {
				throw new EncogEngineError("Can't find any OpenCL platforms");
			}

			for (int i = 0; i < numPlatforms[0]; i++) {
				final cl_platform_id platformID = platformIDs[i];
				final EncogCLPlatform platform = new EncogCLPlatform(platformID);
				this.platforms.add(platform);

				for (final EncogCLDevice device : platform.getDevices()) {
					this.devices.add(device);
				}
			}

			CL.setExceptionsEnabled(true);
		} catch (UnsatisfiedLinkError ex) {
			throw new MissingOpenCLAdapterError(ex);
		}
	}

	/**
	 * @return True if CPUs are present.
	 */
	public boolean areCPUsPresent() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Choose a device. If a GPU is found, return that. Otherwise try to find a
	 * CPU.
	 * 
	 * @return The first device detected.
	 */
	public EncogCLDevice chooseDevice() {
		EncogCLDevice result = chooseDevice(true);
		if (result == null) {
			result = chooseDevice(false);
		}
		return result;
	}

	/**
	 * Choose a device. Simply returns the first device detected.
	 * 
	 * @param useGPU
	 *            Do we prefer to use the GPU?
	 * @return The first device detected.
	 */
	public EncogCLDevice chooseDevice(final boolean useGPU) {

		for (final EncogCLDevice device : this.devices) {
			if (useGPU && !device.isCPU()) {
				return device;
			} else if (!useGPU && device.isCPU()) {
				return device;
			}
		}
		return null;
	}

	/**
	 * Disable all devices that are CPU's. This is a good idea to do if you are
	 * going to use regular CPU processing in conjunction with OpenCL processing
	 * where the CPU is made to look like a OpenCL device. Otherwise, you end up
	 * with the CPU serving as both a "regular CPU training task" and as an
	 * "OpenCL training task".
	 */
	public void disableAllCPUs() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				device.setEnabled(false);
			}
		}
	}

	/**
	 * Enable all devices that are CPU's.
	 */
	public void enableAllCPUs() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				device.setEnabled(true);
			}
		}
	}

	/**
	 * @return The devices used by EncogCL, this spans over platform boundaries.
	 */
	public List<EncogCLDevice> getDevices() {
		return this.devices;
	}

	/**
	 * @return The devices used by EncogCL, this spans over platform boundaries.
	 *         Does not include disabled devices or devices from disabled
	 *         platforms.
	 */
	public List<EncogCLDevice> getEnabledDevices() {

		final List<EncogCLDevice> result = new ArrayList<EncogCLDevice>();
		for (final EncogCLDevice device : this.devices) {
			if (device.isEnabled() && device.getPlatform().isEnabled()) {
				result.add(device);
			}
		}

		return result;
	}

	/**
	 * @return All platforms detected.
	 */
	public List<EncogCLPlatform> getPlatforms() {
		return this.platforms;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (final EncogCLDevice device : this.devices) {
			result.append(device.toString());
			result.append("\n");
		}
		return result.toString();
	}

}
