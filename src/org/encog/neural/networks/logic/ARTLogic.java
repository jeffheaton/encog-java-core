package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;

public abstract class ARTLogic implements NeuralLogic {

	public static final String PROPERTY_A1 = "A1";
	public static final String PROPERTY_B1 = "B1";
	public static final String PROPERTY_C1 = "C1";
	public static final String PROPERTY_D1 = "D1";
	public static final String PROPERTY_L = "L";
	public static final String PROPERTY_VIGILANCE = "VIGILANCE";
	
	private BasicNetwork network;

	@Override
	public void init(BasicNetwork network) {
		this.network = network;		
	}
	
	public BasicNetwork getNetwork()
	{
		return this.network;
	}

}
