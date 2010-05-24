package org.encog.util.cl.kernels;

import org.encog.util.cl.EncogCLDevice;
import static org.jocl.CL.*;

import org.jocl.*;


/**
 * A very simple kernel, used to add a vector.  Not actually used by Encog, 
 * it is a simple test case to verify that OpenCL is working.
 */
public class KernelVectorAdd extends EncogKernel {

	/**
	 * Construct a simple kernel to add two vectors.
	 * @param context The context to use.
	 */
	public KernelVectorAdd(cl_context context) {
		super(context, "Encog.Resources.KernelVectorAdd.txt", "VectorAdd");
	}
	
    /// <summary>
    /// Perform the addition.
    /// </summary>
    /// <param name="device">The OpenCL device to use.</param>
    /// <param name="inputA">The first vector to add.</param>
    /// <param name="inputB">The second vector to add.</param>
    /// <returns>The result of the addition.</returns>
    public double[] add(EncogCLDevice device, double[] inputA, double[] inputB)
    {
        // Create input- and output data 
        int n = 10;
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[] = new float[n];
        
        for (int i=0; i<n; i++)
        {
            srcArrayA[i] = (float)inputA[i];
            srcArrayB[i] = (float)inputB[i];
        }
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);
        
        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = clCreateBuffer(this.getContext(), 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcA, null);
        memObjects[1] = clCreateBuffer(this.getContext(), 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcB, null);
        memObjects[2] = clCreateBuffer(this.getContext(), 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n, null, null);
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};
        
        // Execute the kernel
        clEnqueueNDRangeKernel(device.getCommands(), this.getKernel(), 1, null,
            global_work_size, local_work_size, 0, null, null);
        
        // Read the output data
        clEnqueueReadBuffer(device.getCommands(), memObjects[2], CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);
        
        double[] result = new double[dstArray.length];
        
        for(int i=0;i<dstArray.length;i++)
        {
        	result[i] = dstArray[i];
        }
        
    	return result;
    }

}
