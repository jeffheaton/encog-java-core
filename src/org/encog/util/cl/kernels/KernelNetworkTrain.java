package org.encog.util.cl.kernels;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.neural.networks.flat.FlatNetwork;
import org.encog.neural.networks.training.TrainingError;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_event;
import org.jocl.cl_mem;

/**
 * An OpenCL kernel that is designed to calculate gradients and help train a
 * neural network.
 */
public class KernelNetworkTrain extends EncogKernel {

	/**
	 * A buffer to hold the weight and bias matrix.
	 */
	private cl_mem weightArrayBuffer;

	/**
	 * A buffer to hold the layer index.
	 */
	private cl_mem layerIndexBuffer;

	/**
	 * A buffer to hold the layer counts.
	 */
	private cl_mem layerCountBuffer;

	/**
	 * A buffer to hold the weight indexes.
	 */
	private cl_mem weightIndexBuffer;

	/**
	 * A buffer to hold the activations for each of the layers.
	 */
	private cl_mem activationTypeBuffer;

	/**
	 * The weight and bias array for the network.
	 */
	private float[] weightArray;

	/**
	 * The size of all layer deltas.
	 */
	private int layerDeltaSize;

	/**
	 * An array of activation function types.
	 */
	private int[] activationType;

	/**
	 * The gradients.
	 */
	private float[] gradientArray;

	/**
	 * The errors.
	 */
	private float[] errorArray;

	/**
	 * Construct the kernel for the specified context.
	 * 
	 * @param context
	 *            The context to calculate for.
	 */
	public KernelNetworkTrain(final cl_context context) {
		super(context, "org/encog/data/KernelNetTrain.txt", "NetworkTrain");
	}

	/**
	 * Calculate the gradients for one workload.
	 * 
	 * @param workload
	 *            The workload to calculate for.
	 */
	public void calculate(final TrainingWorkload workload) {
		prepareKernel();

		final FlatNetwork flat = workload.getNetwork();

		for (int i = 0; i < flat.getWeights().length; i++) {
			this.weightArray[i] = (float) flat.getWeights()[i];
		}

		CL.clSetKernelArg(getKernel(), 0, Sizeof.cl_mem, Pointer.to(workload
				.getParamBuffer()));
		CL.clSetKernelArg(getKernel(), 1, Sizeof.cl_mem, Pointer.to(workload
				.getErrorBuffer()));
		CL.clSetKernelArg(getKernel(), 2, Sizeof.cl_mem, Pointer
				.to(this.layerIndexBuffer));
		CL.clSetKernelArg(getKernel(), 3, Sizeof.cl_mem, Pointer
				.to(this.layerCountBuffer));
		CL.clSetKernelArg(getKernel(), 4, Sizeof.cl_mem, Pointer
				.to(this.weightIndexBuffer));
		CL.clSetKernelArg(getKernel(), 5, Sizeof.cl_mem, Pointer.to(workload
				.getParamBuffer()));
		CL.clSetKernelArg(getKernel(), 6, Sizeof.cl_mem, Pointer.to(workload
				.getInputBuffer()));
		CL.clSetKernelArg(getKernel(), 7, Sizeof.cl_mem, Pointer.to(workload
				.getIdealBuffer()));
		CL.clSetKernelArg(getKernel(), 8, Sizeof.cl_mem, Pointer
				.to(this.weightArrayBuffer));
		CL.clSetKernelArg(getKernel(), 9, Sizeof.cl_mem, Pointer
				.to(this.activationTypeBuffer));

		try {
			cl_event[]  events = new cl_event[1];
			
			// Set the work-item dimensions
			final long[] global_work_size = new long[] { Encog.getInstance()
					.getCL().getCLThreads() };
			final long[] local_work_size = new long[] { Math.max(Encog
					.getInstance().getCL().getCLWorkloadSize(), 1) };

			CL.clEnqueueWriteBuffer(workload.getDevice().getCommands(),
					this.weightArrayBuffer, CL.CL_TRUE, 0, Sizeof.cl_float
							* this.weightArray.length, Pointer
							.to(this.weightArray), 0, null, null);

			// Execute the kernel
			CL.clEnqueueNDRangeKernel(workload.getDevice().getCommands(),
					getKernel(), 1, null, global_work_size, local_work_size, 0,
					null, events[0]);
			CL.clFinish(workload.getDevice().getCommands());
			
			CL.clEnqueueReadBuffer(workload.getDevice().getCommands(), workload
					.getErrorBuffer(), CL.CL_TRUE, 0, workload.getMaxUnits()
					* Sizeof.cl_float, Pointer.to(workload.getErrors()), 0, null,
					null);

			CL.clEnqueueReadBuffer(workload.getDevice().getCommands(), workload
					.getGradientBuffer(), CL.CL_TRUE, 0, this.weightArray.length
					* workload.getMaxUnits() * Sizeof.cl_float, Pointer
					.to(workload.getGradients()), 0, null, null);

			// commands.Finish();
		} catch (final Exception e) {
			throw new TrainingError(e);
		}
	}

	/**
	 * Init the kernal for new training.
	 * 
	 * @param flat
	 *            The network to be trained.
	 */
	public void init(final FlatNetwork flat) {

		this.weightArray = new float[flat.getWeights().length];
		this.activationType = flat.getActivationType();

		this.layerDeltaSize = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			this.layerDeltaSize += flat.getLayerCounts()[i];
		}

		this.layerIndexBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerIndex().length, Pointer.to(flat
						.getLayerIndex()), null);

		this.layerCountBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerCounts().length, Pointer.to(flat
						.getLayerCounts()), null);

		this.weightArrayBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
						* this.weightArray.length,
				Pointer.to(this.weightArray), null);

		this.weightIndexBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getWeightIndex().length, Pointer.to(flat
						.getWeightIndex()), null);

		this.activationTypeBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getActivationType().length, Pointer.to(flat
						.getActivationType()), null);
	}	
}
