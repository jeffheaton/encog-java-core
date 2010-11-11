package org.encog.engine.concurrency.calc;

import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.kernels.KernelNetworkCalc;

public class CalcOpenCLDevice {
	
	private final EncogCLDevice device;
	private final ConcurrentCalculate calc;
	private final KernelNetworkCalc kernelCalc;
	
	public CalcOpenCLDevice(EncogCLDevice device, ConcurrentCalculate calc) {
		super();
		this.device = device;
		this.calc = calc;
		this.kernelCalc = new KernelNetworkCalc(this.device);
	}

	public EncogCLDevice getDevice() {
		return device;
	}

	public ConcurrentCalculate getCalc() {
		return calc;
	}
	
	public void setNetwork(FlatNetwork network)
	{
		this.kernelCalc.setFlat(network);
	}
	
	public void setTraining(EngineIndexableSet training)
	{
		this.kernelCalc.setTraining(training);
	}
	
}
