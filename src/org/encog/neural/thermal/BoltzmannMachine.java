package org.encog.neural.thermal;

import org.encog.engine.util.BoundMath;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

public class BoltzmannMachine extends ThermalNetwork {

	public static final String RUN_CYCLES = "runCycles";
	public static final String ANNEAL_CYCLES = "annealCycles";
	
	/**
	 * The current temperature of the neural network. The higher the
	 * temperature, the more random the network will behave.
	 */
	private double temperature;
	
	private double[] threshold;

	/**
	 * Count used to internally determine if a neuron is "on".
	 */
	private transient int[] on;

	/**
	 * Count used to internally determine if a neuron is "off".
	 */
	private transient int[] off;

	/**
	 * The number of cycles to anneal for.
	 */
	private int annealCycles = 100;

	/**
	 * The number of cycles to run the network through before annealing.
	 */
	private int runCycles = 1000;
	
	public BoltzmannMachine()
	{
		super();
	}
	
	public BoltzmannMachine(int neuronCount)
	{
		super(neuronCount);

		this.threshold = new double[neuronCount];
	}

	/**
	 * Decrease the temperature by the specified amount.
	 * 
	 * @param d
	 *            The amount to decrease by.
	 */
	public void decreaseTemperature(final double d) {
		this.temperature *= d;
	}

	/**
	 * Run the network until thermal equilibrium is established.
	 */
	public void establishEquilibrium() {		
		final int count = getNeuronCount();
		
		if( this.on==null ) {
			this.on = new int[count];
			this.off = new int[count];
		}

		for (int i = 0; i < count; i++) {
			this.on[i] = 0;
			this.off[i] = 0;
		}

		for (int n = 0; n < this.runCycles * count; n++) {
			run((int) RangeRandomizer.randomize(0, count - 1));
		}
		for (int n = 0; n < this.annealCycles * count; n++) {
			int i = (int) RangeRandomizer.randomize(0, count - 1);
			run(i);
			if (getCurrentState().getBoolean(i)) {
				this.on[i]++;
			} else {
				this.off[i]++;
			}
		}

		for (int i = 0; i < count; i++) {
			getCurrentState().setData(i, this.on[i] > this.off[i]);
		}
	}

	/**
	 * @return The temperature the network is currently operating at.
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Run the network for all neurons present.
	 */
	public void run() {
		final int count = getNeuronCount();
		for (int i = 0; i < count; i++) {
			run(i);
		}
	}

	/**
	 * Run the network for the specified neuron.
	 * 
	 * @param i
	 *            The neuron to run for.
	 */
	void run(final int i) {
		int j;
		double sum, probability;

		final int count = getNeuronCount();

		sum = 0;
		for (j = 0; j < count; j++) {
			sum += getWeight(i, j)
					* (getCurrentState().getBoolean(j) ? 1 : 0);
		}
		sum -= threshold[i];
		probability = 1 / (1 + BoundMath.exp(-sum / this.temperature));
		if (RangeRandomizer.randomize(0, 1) <= probability) {
			getCurrentState().setData(i, true);
		} else {
			getCurrentState().setData(i, false);
		}
	}

	/**
	 * Set the network temperature.
	 * 
	 * @param temperature
	 *            The temperature to operate the network at.
	 */
	public void setTemperature(final double temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the threshold
	 */
	public double[] getThreshold() {
		return threshold;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_BOLTZMANN);
		obj.setStandardProperties(this);
		obj.setProperty(PersistConst.WEIGHTS, this.getWeights());
		obj.setProperty(PersistConst.THRESHOLDS, this.getThreshold());
		obj.setProperty(PersistConst.OUTPUT, this.getCurrentState().getData());
		obj.setProperty(PersistConst.NEURON_COUNT, this.getNeuronCount(),false);
		obj.setProperty(ANNEAL_CYCLES, this.annealCycles,false);
		obj.setProperty(RUN_CYCLES, this.runCycles, false);
		obj.setProperty(PersistConst.TEMPERATURE, this.temperature, false);
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_BOLTZMANN);
		int neuronCount = obj.getPropertyInt(PersistConst.NEURON_COUNT,true);
		this.threshold = obj.getPropertyDoubleArray(PersistConst.THRESHOLDS, true);
		double[] weights = obj.getPropertyDoubleArray(PersistConst.WEIGHTS,true);
		double[] state = obj.getPropertyDoubleArray(PersistConst.OUTPUT, true);
		this.annealCycles = obj.getPropertyInt(ANNEAL_CYCLES, true);
		this.runCycles = obj.getPropertyInt(RUN_CYCLES, true);
		this.temperature = obj.getPropertyDouble(PersistConst.TEMPERATURE,true);
		init(neuronCount,weights,state);
	}

	/**
	 * @return the annealCycles
	 */
	public int getAnnealCycles() {
		return annealCycles;
	}

	/**
	 * @param annealCycles the annealCycles to set
	 */
	public void setAnnealCycles(int annealCycles) {
		this.annealCycles = annealCycles;
	}

	/**
	 * @return the runCycles
	 */
	public int getRunCycles() {
		return runCycles;
	}

	/**
	 * @param runCycles the runCycles to set
	 */
	public void setRunCycles(int runCycles) {
		this.runCycles = runCycles;
	}
	
	
}
