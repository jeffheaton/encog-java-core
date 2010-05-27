package org.encog.util.cl;

import static org.jocl.CL.CL_CONTEXT_DEVICES;
import static org.jocl.CL.clGetContextInfo;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.cl.kernels.KernelNetworkTrain;
import org.encog.util.cl.kernels.KernelVectorAdd;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

/**
 * An Encog CL platform.
 * 
 */
public class EncogCLPlatform extends EncogCLItem {

	/**
	 * The OpenCL platform.
	 */
	private final cl_platform_id platform;

	/**
	 * The OpenCL context for this platform. One context is created for each
	 * platform.
	 */
	private final cl_context context;

	/**
	 * All of the devices on this platform.
	 */
	private final List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

	/**
	 * A kernel used to help train a network.
	 */
	private KernelNetworkTrain kerNetworkTrain;

	/**
	 * A simple test kernel to add a vector.
	 */
	private KernelVectorAdd kerVectorAdd;
	
	/**
	 * Construct an OpenCL platform.
	 * @param platform The OpenCL platform.
	 */
	public EncogCLPlatform(final cl_platform_id platform) {
		final long[] numBytes = new long[1];
		this.platform = platform;

		final cl_context_properties contextProperties = 
			new cl_context_properties();
		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

		this.context = CL.clCreateContextFromType(contextProperties,
				CL.CL_DEVICE_TYPE_ALL, null, null, null);

		// this.Name = platform.Name;
		// this.Vender = platform.Vendor;
		// this.Enabled = true;
		
		clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, null, numBytes);

		final int numDevices = (int) numBytes[0] / Sizeof.cl_device_id;
		final cl_device_id[] devicesList = new cl_device_id[numDevices];
		
		clGetContextInfo(context, CL_CONTEXT_DEVICES, numBytes[0],  
	            Pointer.to(devicesList), null);

		for (final cl_device_id deviceID : devicesList) {
			final EncogCLDevice adapter = new EncogCLDevice(this, deviceID);
			this.devices.add(adapter);
		}

		this.kerVectorAdd = new KernelVectorAdd(this.context);
		this.kerNetworkTrain = new KernelNetworkTrain(this.context);
	}

	/**
	 * @return The context for this platform.
	 */
	public cl_context getContext() {
		return this.context;
	}

	/**
	 * @return All devices on this platform.
	 */
	public List<EncogCLDevice> getDevices() {
		return this.devices;
	}

	/**
	 * @return A kernel used to help train a network.
	 */
	public KernelNetworkTrain getNetworkTrain() {
		return this.kerNetworkTrain;
	}

	/**
	 * @return The OpenCL platform.
	 */
	public cl_platform_id getPlatform() {
		return this.platform;
	}

	/**
	 * @return A simple kernel to add two vectors, used to test only.
	 */
	public KernelVectorAdd getVectorAdd() {
		return this.kerVectorAdd;
	}

}
