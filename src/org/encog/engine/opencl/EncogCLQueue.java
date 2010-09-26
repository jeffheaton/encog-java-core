package org.encog.engine.opencl;

import org.encog.engine.opencl.kernels.EncogKernel;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_mem;

public class EncogCLQueue {

	/**
	 * A command queue for this device.
	 */
	private final cl_command_queue commands;

	private final EncogCLDevice device;
	
	public EncogCLQueue(EncogCLDevice device)
	{
		EncogCLPlatform platform = device.getPlatform();
		this.device = device;
		this.commands = CL.clCreateCommandQueue(platform.getContext(), device.getDevice(),
				0, null);
	}
	
	/**
	 * @return The OpenCL command queue.
	 */
	public cl_command_queue getCommands() {
		return this.commands;
	}
	
	public void execute(EncogKernel kernel)
	{
		final long[] globalWorkSize = new long[] { kernel.getGlobalWork() };
		final long[] localWorkSize = new long[] { kernel.getLocalWork() };

		// Execute the kernel
		CL.clEnqueueNDRangeKernel(this.commands, kernel.getKernel(),
				1, null, globalWorkSize, localWorkSize, 0, null, null);
		
	}
	
	public void waitFinish()
	{
		CL.clFinish(this.commands);
	}
	
	public void array2Buffer(float[] source, cl_mem targetBuffer)
	{
		CL.clEnqueueWriteBuffer(this.commands,
				targetBuffer, CL.CL_TRUE, 0, Sizeof.cl_float
						* source.length,
				Pointer.to(source), 0, null, null);
	}
	
	public void buffer2Array(cl_mem sourceBuffer, float[] target)
	{
		CL.clEnqueueReadBuffer(this.commands,
				sourceBuffer, CL.CL_TRUE, 0,
				target.length * Sizeof.cl_float,
				Pointer.to(target), 0, null, null);
	}
	
	public void array2Buffer(int[] source, cl_mem targetBuffer)
	{
		CL.clEnqueueWriteBuffer(this.commands,
				targetBuffer, CL.CL_TRUE, 0, Sizeof.cl_int
						* source.length,
				Pointer.to(source), 0, null, null);
	}
	
	public void buffer2Array(cl_mem sourceBuffer, int[] target)
	{
		CL.clEnqueueReadBuffer(this.commands,
				sourceBuffer, CL.CL_TRUE, 0,
				target.length * Sizeof.cl_int,
				Pointer.to(target), 0, null, null);
	}

	
}
