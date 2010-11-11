package org.encog.engine.concurrency.calc;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngine;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

public class ConcurrentCalculate {
	
	private FlatNetwork network;
	private EngineIndexableSet trainingData;
	private List<CalcOpenCLDevice> devices = new ArrayList<CalcOpenCLDevice>();
	private static ConcurrentCalculate instance;
	private boolean useOpenCL;
	
	/**
	 * Private constructor.
	 */
	private ConcurrentCalculate()
	{
		
	}
	
	public static ConcurrentCalculate getInstance()
	{
		if( instance==null )
			instance = new ConcurrentCalculate();
		return instance;
	}

	public FlatNetwork getNetwork() {
		return network;
	}

	public void setNetwork(FlatNetwork network) {
		this.network = network;
		for(CalcOpenCLDevice dev: this.devices)
		{
			dev.setTraining(trainingData);
		}
	}

	public EngineIndexableSet getTrainingData() {
		return trainingData;
	}

	public void setTrainingData(EngineIndexableSet trainingData) {
		this.trainingData = trainingData;
		for(CalcOpenCLDevice dev: this.devices)
		{
			dev.setTraining(trainingData);
		}
	}
	
	public double calculateError()
	{
		// if we are using OpenCL, then try to execute on OpenCL first
		if( this.useOpenCL ) {
			for(CalcOpenCLDevice dev: this.devices) {
				CalculationResult result = dev.calculateError();
				if( result.isExecuted() )
				{
					return result.getError();
				}
			}
		}
		
		// use regular CPU code to calculate
		return this.network.calculateError(this.trainingData);
	}
	
	public void initCL()
	{
		this.devices.clear();
		for(EncogCLDevice device: EncogEngine.getInstance().getCL().getEnabledDevices() )
		{
			this.devices.add(new CalcOpenCLDevice(device,this));
		}
		this.useOpenCL = true;
	}

	/**
	 * @return the useOpenCL
	 */
	public boolean isUseOpenCL() {
		return useOpenCL;
	}

	/**
	 * @param useOpenCL the useOpenCL to set
	 */
	public void setUseOpenCL(boolean useOpenCL) {
		this.useOpenCL = useOpenCL;
	}
	
	
}
