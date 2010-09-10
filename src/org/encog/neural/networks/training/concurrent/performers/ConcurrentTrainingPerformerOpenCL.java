package org.encog.neural.networks.training.concurrent.performers;

import org.encog.Encog;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.concurrent.TrainingJob;
import org.encog.neural.networks.training.propagation.Propagation;

public class ConcurrentTrainingPerformerOpenCL extends ConcurrentTrainingPerformerCPU {

	final private EncogCLDevice device;

	public ConcurrentTrainingPerformerOpenCL(final EncogCLDevice device)
	{
		if( Encog.getInstance().getCL()==null )
			throw new NeuralNetworkError("Can't use an OpenCL performer, because OpenCL is not enabled.");
		
		if( Encog.getInstance().getCL()==null )
			throw new NeuralNetworkError("Can't use a null OpenCL device.");
		
		this.device = device;
	}

	/**
	 * @return the device
	 */
	public EncogCLDevice getDevice() {
		return device;
	}
	
	protected void setupJob(TrainingJob job)
	{
		if( job.getTrain() instanceof Propagation)
			((Propagation)job.getTrain()).setTargetDevice(this.device);
		else
			throw new NeuralNetworkError("Can't use " + job.getTrain().getClass().getSimpleName() + " on OpenCL.");
	}

	

}
