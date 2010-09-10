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

import org.encog.engine.util.Format;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
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
	 * A command queue for this device.
	 */
	private final cl_command_queue commands;

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
		setName(getDeviceString(CL.CL_DEVICE_NAME));
		setVender(getDeviceString(CL.CL_DEVICE_VENDOR));

		final long type = getDeviceLong(CL.CL_DEVICE_TYPE);
		this.cpu = (type == CL.CL_DEVICE_TYPE_CPU);

		this.commands = CL.clCreateCommandQueue(platform.getContext(), device,
				0, null);
	}

	/**
	 * @return The OpenCL command queue.
	 */
	public cl_command_queue getCommands() {
		return this.commands;
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
