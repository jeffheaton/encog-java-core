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

package org.encog.engine.opencl;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngineError;
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
	private final List<EncogCLPlatform> platforms =
		new ArrayList<EncogCLPlatform>();

	/**
	 * All devices, from all platforms.
	 */
	private final List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

	/**
	 * The number of CL threads to use, defaults to 200.
	 */
	private final int clThreads;

	/**
	 * The size of a CL workload, defaults to 10.
	 */
	private final int clWorkloadSize;

	/**
	 * Construct an Encog OpenCL object.
	 */
	public EncogCL() {
		final int[] numPlatforms = new int[1];
		this.clThreads = 200;
		this.clWorkloadSize = 10;

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
	 * Choose a device. Simply returns the first device detected.
	 *
	 * @return The first device detected.
	 */
	public EncogCLDevice chooseDevice() {
		if (this.devices.size() < 1) {
			return null;
		} else {
			return this.devices.get(0);
		}
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

	/**
	 * @return the clThreads
	 */
	public int getCLThreads() {
		return clThreads;
	}

	/**
	 * @return the clWorkloadSize
	 */
	public int getCLWorkloadSize() {
		return clWorkloadSize;
	}
}
