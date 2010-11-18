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
package org.encog.engine.opencl;

import org.encog.engine.util.Format;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;

/**
 * An OpenCL compute device. One of these will be created for each GPU on your
 * system. Some GPU drivers will also map your CPU as a compute device. A device
 * will likely have parallel processing capabilities. A CPU device will have
 * multiple cores. A GPU, will have multiple compute units.
 * 
 * Devices are held by Platforms. A platform is a way to group all devices from
 * a single vendor or driver.
 * 
 */
public class EncogCLDevice extends EncogCLItem {

	/**
	 * The OpenCL compute device.
	 */
	private final cl_device_id device;

	/**
	 * The platform for this device.
	 */
	private final EncogCLPlatform platform;

	/**
	 * Is this device a cpu?
	 */
	private final boolean cpu;

	/**
	 * The OpenCL command queue.
	 */
	private final EncogCLQueue queue;

	/**
	 * Construct an OpenCL device.
	 * 
	 * @param platform
	 *            The platform.
	 * @param device
	 *            The device.
	 */
	public EncogCLDevice(final EncogCLPlatform platform,
			final cl_device_id device) {
		this.platform = platform;
		setEnabled(true);
		this.device = device;
		setName(getDeviceString(CL.CL_DEVICE_NAME).trim());
		setVender(getDeviceString(CL.CL_DEVICE_VENDOR));

		final long type = getDeviceLong(CL.CL_DEVICE_TYPE);
		this.cpu = (type == CL.CL_DEVICE_TYPE_CPU);
		this.queue = new EncogCLQueue(this);
	}

	/**
	 * @return The OpenCL device.
	 */
	public cl_device_id getDevice() {
		return this.device;
	}

	/**
	 * Get a long param from the device.
	 * 
	 * @param param
	 *            The param desired.
	 * @return The param value.
	 */
	public long getDeviceLong(final int param) {
		final long[] result = new long[1];
		final long[] len = new long[1];

		CL.clGetDeviceInfo(this.device, param, Sizeof.cl_long, Pointer
				.to(result), len);
		return result[0];
	}

	/**
	 * Get a config string from the device.
	 * 
	 * @param param
	 *            The param to get.
	 * @return The config string.
	 */
	public String getDeviceString(final int param) {
		final byte[] buffer = new byte[255];
		final long[] len = new long[1];

		CL.clGetDeviceInfo(this.device, param, buffer.length, Pointer
				.to(buffer), len);
		final String value = new String(buffer, 0, (int) len[0]);
		return value;
	}

	/**
	 * @return The size of the global memory.
	 */
	public long getGlobalMemorySize() {
		return getDeviceLong(CL.CL_DEVICE_GLOBAL_MEM_SIZE);
	}

	/**
	 * @return The size of the local memory.
	 */
	public long getLocalMemorySize() {
		return getDeviceLong(CL.CL_DEVICE_LOCAL_MEM_SIZE);
	}

	/**
	 * @return The max clock frequency.
	 */
	public long getMaxClockFrequency() {
		return getDeviceLong(CL.CL_DEVICE_MAX_CLOCK_FREQUENCY);
	}

	/**
	 * @return The number of compute units.
	 */
	public long getMaxComputeUnits() {
		return getDeviceLong(CL.CL_DEVICE_MAX_COMPUTE_UNITS);
	}

	/**
	 * @return The max workgroup size.
	 */
	public long getMaxWorkGroupSize() {
		return getDeviceLong(CL.CL_DEVICE_MAX_WORK_GROUP_SIZE);
	}

	/**
	 * @return The OpenCL platform.
	 */
	public EncogCLPlatform getPlatform() {
		return this.platform;
	}

	/**
	 * @return the queue
	 */
	public EncogCLQueue getQueue() {
		return this.queue;
	}

	/**
	 * @return Determine if this device is a CPU.
	 */
	public boolean isCPU() {
		return this.cpu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.cpu) {
			builder.append("CPU:");
		} else {
			builder.append("GPU:");
		}

		builder.append(getName());
		builder.append(",ComputeUnits:");
		builder.append(getMaxComputeUnits());
		builder.append(",ClockFreq:");
		builder.append(getMaxClockFrequency());
		builder.append(",LocalMemory=");
		builder.append(Format.formatMemory(getLocalMemorySize()));
		builder.append(",GlobalMemory=");
		builder.append(Format.formatMemory(getGlobalMemorySize()));

		return builder.toString();
	}

}
