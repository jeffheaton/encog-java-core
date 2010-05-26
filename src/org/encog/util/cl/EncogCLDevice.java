package org.encog.util.cl;

import org.jocl.CL;
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
	 * A command queue for this device.
	 */
	private cl_command_queue commands;

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
		// this.Name = device.Name;
		// this.Enabled = true;
		this.device = device;
		// this.Vender = device.Vendor;

		this.commands = CL.clCreateCommandQueue(platform
				.getContext(), device, 0, null);
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
	 * @return The size of the global memory.
	 */
	public long getGlobalMemorySize() {
		return 0;
		// return device.GlobalMemorySize;
	}

	/**
	 * @return The size of the local memory.
	 */
	public long getLocalMemorySize() {
		return 0;
		// return device.LocalMemorySize;
	}

	/**
	 * @return The max clock frequency.
	 */
	public long getMaxClockFrequency() {
		return 0;
		// return device.MaxClockFrequency;
	}

	/**
	 * @return The number of compute units.
	 */
	public long getMaxComputeUnits() {
		return 0;
		// return device.MaxComputeUnits;
	}

	/**
	 * @return The max workgroup size.
	 */
	public long getMaxWorkGroupSize() {
		return 0;
		// return device.MaxWorkGroupSize;
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
		// return this.device.Type == ComputeDeviceTypes.Cpu;
		return false;
	}

	/**
	 * @return Dump this device as a string.
	 */
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		/*
		 * switch (device.getType()) { case ComputeDeviceTypes.Accelerator:
		 * builder.Append("Accel:"); break; case ComputeDeviceTypes.Cpu:
		 * builder.Append("CPU:"); break; case ComputeDeviceTypes.Gpu:
		 * builder.Append("GPU:"); break; default: builder.Append("Unknown:");
		 * break; }
		 * 
		 * builder.Append(this.Name); builder.Append(",ComputeUnits:");
		 * builder.Append(this.MaxComputeUnits); builder.Append(",ClockFreq:");
		 * builder.Append(this.MaxClockFrequency);
		 * builder.Append(",LocalMemory=");
		 * builder.Append(Format.FormatMemory(this.LocalMemorySize));
		 * builder.Append(",GlobalMemory=");
		 * builder.Append(Format.FormatMemory(this.GlobalMemorySize));
		 */
		return builder.toString();
	}

}
