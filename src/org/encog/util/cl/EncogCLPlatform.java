package org.encog.util.cl;

import static org.jocl.CL.*;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.cl.kernels.KernelNetworkTrain;
import org.encog.util.cl.kernels.KernelVectorAdd;
import org.jocl.*;

/**
 * 
 * @author jheaton
 *
 */
public class EncogCLPlatform extends EncogCLItem {

    /// <summary>
    /// The OpenCL platform.
    /// </summary>
    private cl_platform_id platform;

    /// <summary>
    /// The OpenCL context for this platform.  One context is created
    /// for each platform.
    /// </summary>
    private cl_context context;

    /// <summary>
    /// All of the devices on this platform.
    /// </summary>
    private List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

    /// <summary>
    /// A kernel used to help train a network.
    /// </summary>
    private KernelNetworkTrain kerNetworkTrain;

    /// <summary>
    /// A simple test kernel to add a vector.
    /// </summary>
    private KernelVectorAdd kerVectorAdd;

    /// <summary>
    /// A kernel used to help train a network.
    /// </summary>
    public KernelNetworkTrain getNetworkTrain()
    {
        return this.kerNetworkTrain;
    }

    /// <summary>
    /// A simple kernel to add two vectors, used to test only.
    /// </summary>
    public KernelVectorAdd getVectorAdd()
    {
        return this.kerVectorAdd;
    }

    /// <summary>
    /// All devices on this platform.
    /// </summary>
    public List<EncogCLDevice> getDevices()
    {
        return this.devices;
    }

    /// <summary>
    /// The OpenCL platform.
    /// </summary>
    public cl_platform_id getPlatform()
    {
        return this.platform;
    }

    /// <summary>
    /// The context for this platform.
    /// </summary>
    public cl_context getContext()
    {
        return this.context;
    }

    /// <summary>
    /// Construct an OpenCL platform.
    /// </summary>
    /// <param name="platform">The OpenCL platform.</param>
    public EncogCLPlatform(cl_platform_id platform)
    {
        long numBytes[] = new long[1];
        this.platform = platform;

        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        this.context = clCreateContextFromType(
                contextProperties, CL_DEVICE_TYPE_ALL, null, null, null);

        //this.Name = platform.Name;
        //this.Vender = platform.Vendor;
        //this.Enabled = true;
        
        int numDevices = (int) numBytes[0] / Sizeof.cl_device_id;
        cl_device_id devicesList[] = new cl_device_id[numDevices];
        clGetContextInfo(context, CL_CONTEXT_DEVICES, numBytes[0],  
            Pointer.to(devicesList), null);
        
        for( cl_device_id deviceID: devicesList)
        {
        	EncogCLDevice adapter = new EncogCLDevice(this, deviceID);
        	this.devices.add(adapter);
        }

        //this.kerVectorAdd = new KernelVectorAdd(this.context);
        //this.kerNetworkTrain = new KernelNetworkTrain(this.context);
    }
	
}
