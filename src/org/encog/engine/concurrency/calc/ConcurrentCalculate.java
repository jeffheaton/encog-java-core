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
		return this.network.calculateError(this.trainingData);
	}
	
	public void initCL()
	{
		this.devices.clear();
		for(EncogCLDevice device: EncogEngine.getInstance().getCL().getEnabledDevices() )
		{
			this.devices.add(new CalcOpenCLDevice(device,this));
		}
	}
}
