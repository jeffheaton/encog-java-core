package org.encog.engine.concurrency.calc;

import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.kernels.KernelNetworkCalc;

public class CalcOpenCLDevice {
	
	private final EncogCLDevice device;
	private final ConcurrentCalculate calc;
	private final KernelNetworkCalc kernelCalc;
	private boolean busy;
	
	public CalcOpenCLDevice(EncogCLDevice device, ConcurrentCalculate calc) {
		super();
		this.device = device;
		this.calc = calc;
		this.kernelCalc = new KernelNetworkCalc(this.device);
	}
	
	public CalculationResult calculateError()
	{
		if( this.busy )
			return new CalculationResult(false,false);
		
		CalculationResult result = new CalculationResult(true,true);
		return result;
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
