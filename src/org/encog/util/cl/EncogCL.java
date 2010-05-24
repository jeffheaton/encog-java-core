package org.encog.util.cl;

import static org.jocl.CL.clGetPlatformIDs;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.jocl.cl_platform_id;

/**
 * An OpenCL platform.  A platform is a collection of OpenCL devices
 * from the same vendor.  Often, you will have only a single platform.
 */
public class EncogCL {
    /// <summary>
    /// The platforms detected.
    /// </summary>
    private List<EncogCLPlatform> platforms = new ArrayList<EncogCLPlatform>();

    /// <summary>
    /// All devices, from all platforms.
    /// </summary>
    private List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

    /// <summary>
    /// Allows you to determine how much 
    /// work the CPU and GPU get.  For example, to give the CL/GPU 
    /// twice as much work as the CPU, specify 1.5.  To give it half as 
    /// much, choose 0.5.
    /// </summary>
    private double enforcedCLRatio;

    /// <summary>
    /// The number of CL threads to use, defaults to 200.
    /// </summary>
    private int clThreads;

    /// <summary>
    /// The size of a CL workload, defaults to 10.
    /// </summary>
    private int clWorkloadSize;

    /// <summary>
    /// Construct an Encog OpenCL object.
    /// </summary>
    public EncogCL()
    {
    	int[] numPlatforms = new int[1];
        this.enforcedCLRatio = 1.0;
        this.clThreads = 200;
        this.clWorkloadSize = 10;
        
        cl_platform_id platformIDs[] = new cl_platform_id[5];
        clGetPlatformIDs(platformIDs.length, platformIDs, numPlatforms);

        if (numPlatforms[0] == 0)
            throw new EncogError("Can't find any OpenCL platforms");

        for(int i=0;i<numPlatforms[0];i++)
        {
        	cl_platform_id platformID = platformIDs[i];
        	EncogCLPlatform platform = new EncogCLPlatform(platformID);
        	platforms.add(platform);
        	
        	for( EncogCLDevice device : platform.getDevices() )
            {
                devices.add(device);
            }
        }
    }

    /// <summary>
    /// The devices used by EncogCL, this spans over platform boundaries.
    /// </summary>
    public List<EncogCLDevice> getDevices()
    {
    	return this.devices;
    }

    /// <summary>
    /// The devices used by EncogCL, this spans over platform boundaries.
    /// Does not include disabled devices or devices from disabled platforms.
    /// </summary>
    public List<EncogCLDevice> getEnabledDevices()
    {

            List<EncogCLDevice> result = new ArrayList<EncogCLDevice>();
            for(EncogCLDevice device : devices)
            {
                if ( device.isEnabled() && device.getPlatform().isEnabled() )
                    result.add(device);
            }

            return result;
    }

    /// <summary>
    /// All platforms detected.
    /// </summary>
    public List<EncogCLPlatform> getPlatforms()
    {
    	return this.platforms;
    }

    /// <summary>
    /// Choose a device.  Simply returns the first device detected.
    /// </summary>
    /// <returns>The first device detected.</returns>
    public EncogCLDevice chooseDevice()
    {
        if (this.devices.size() < 1)
            return null;
        else
            return this.devices.get(0);
    }

    /// <summary>
    /// Disable all devices that are CPU's.  This is a good idea to do if 
    /// you are going to use regular CPU processing in cojunction with 
    /// OpenCL processing where the CPU is made to look like a OpenCL device.  
    /// Otherwise, you end up with the CPU serving as both a "regular CPU 
    /// training task" and as an "OpenCL training task".
    /// </summary>
    public void DisableAllCPUs()
    {
        for(EncogCLDevice device : this.devices)
        {
            if( device.isCPU() )
                device.setEnabled(false);
        }
    }

    /// <summary>
    /// Enable all devices that are CPU's.  
    /// </summary>
    public void enableAllCPUs()
    {
        for(EncogCLDevice device : this.devices)
        {
            if (device.isCPU())
                device.setEnabled(true);
        }
    }

    /// <summary>
    /// True if CPUs are present.
    /// </summary>
    public boolean areCPUsPresent()
    {
    	for (EncogCLDevice device : this.devices)
        {
    		if (device.isCPU() )
    			return true;
        }
        return false;
    }

    /// <summary>
    /// Dump all devices as a string.
    /// </summary>
    /// <returns>The devices.</returns>
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (EncogCLDevice device : this.devices)
        {
            result.append(device.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
