package org.encog.util.cl;

import static org.jocl.CL.*;

import org.jocl.*;

/**
 * An OpenCL compute device.  One of these will be created for each GPU 
 * on your system.  Some GPU drivers will also map your CPU as a compute 
 * device.  A device will likely have parallel processing capabilities.  
 * A CPU device will have multiple cores.  A GPU, will have multiple 
 * compute units.
 *  
 * Devices are held by Platforms.  A platform is a way to group all devices
 * from a single vendor or driver.
 *
 */
public class EncogCLDevice extends EncogCLItem {

	/**
	 * The OpenCL compute device.
	 */
	cl_device_id device;
	

    /// <summary>
    /// The platform for this device.
    /// </summary>
    private EncogCLPlatform platform;

    /// <summary>
    /// A command queue for this device.
    /// </summary>
    private cl_command_queue commands;

    /// <summary>
    /// The OpenCL device.
    /// </summary>
    public cl_device_id getDevice()
    {
    	return this.device;
    }

    /// <summary>
    /// The OpenCL platform.
    /// </summary>
    public EncogCLPlatform getPlatform()
    {
        return this.platform;
    }

    /// <summary>
    /// The OpenCL command queue.
    /// </summary>
    public cl_command_queue getCommands()
    {
    	return this.commands;
    }

    /// <summary>
    /// Determine if this device is a CPU.
    /// </summary>
    public boolean isCPU()
    {
        //return this.device.Type == ComputeDeviceTypes.Cpu;
        return false;
    }

    /// <summary>
    /// The size of the local memory.
    /// </summary>
    public long getLocalMemorySize()
    {
    	return 0;
        //return device.LocalMemorySize;
    }

    /// <summary>
    /// The size of the global memory.
    /// </summary>
    public long getGlobalMemorySize()
    {
    	return 0;
        //return device.GlobalMemorySize;
    }

    /// <summary>
    /// The max clock frequency.
    /// </summary>
    public long getMaxClockFrequency()
    {
    	return 0;
        //return device.MaxClockFrequency;
    }

    /// <summary>
    /// The max workgroup size.
    /// </summary>
    public long getMaxWorkGroupSize()
    {
    	return 0;
        //return device.MaxWorkGroupSize;
    }

    /// <summary>
    /// The number of compute units.
    /// </summary>
    public long getMaxComputeUnits()
    {
    	return 0;
    	//return device.MaxComputeUnits;
    }

    /// <summary>
    /// Construct an OpenCL device.
    /// </summary>
    /// <param name="platform">The platform.</param>
    /// <param name="device">The device.</param>
    public EncogCLDevice(EncogCLPlatform platform, cl_device_id device)
    {
        this.platform = platform;
        //this.Name = device.Name;
        //this.Enabled = true;
        this.device = device;
        //this.Vender = device.Vendor;
        
        cl_command_queue commandQueue = 
            clCreateCommandQueue(platform.getContext(), device, 0, null);
    }

    /// <summary>
    /// Dump this device as a string.
    /// </summary>
    /// <returns>The device as a string.</returns>
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        /*switch (device.getType())
        {
            case ComputeDeviceTypes.Accelerator:
                builder.Append("Accel:");
                break;
            case ComputeDeviceTypes.Cpu:
                builder.Append("CPU:");
                break;
            case ComputeDeviceTypes.Gpu:
                builder.Append("GPU:");
                break;
            default:
                builder.Append("Unknown:");
                break;
        }

        builder.Append(this.Name);
        builder.Append(",ComputeUnits:");
        builder.Append(this.MaxComputeUnits);
        builder.Append(",ClockFreq:");
        builder.Append(this.MaxClockFrequency);
        builder.Append(",LocalMemory=");
        builder.Append(Format.FormatMemory(this.LocalMemorySize));
        builder.Append(",GlobalMemory=");
        builder.Append(Format.FormatMemory(this.GlobalMemorySize));*/
        return builder.toString();
    }


}
