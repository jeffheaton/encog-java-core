package org.encog.neural.bam;

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BAM extends BasicPersistedObject {

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BAM.class);

	private int f1Count;
	private int f2Count;
	
	private Matrix weightsF1toF2;
	private Matrix weightsF2toF1;

	public BAM()
	{
	
	}
	
	public BAM(int f1Count,int f2Count)
	{
		this.f1Count = f1Count;
		this.f2Count = f2Count;
		
		this.weightsF1toF2 = new Matrix(f1Count,f2Count);
		this.weightsF2toF1 = new Matrix(f2Count,f1Count);		
	}



	/**
	 * Add a pattern to the neural network.
	 * 
	 * @param inputPattern
	 *            The input pattern.
	 * @param outputPattern
	 *            The output pattern(for this input).
	 */
	public void addPattern(final NeuralData inputPattern,
			final NeuralData outputPattern) {

		int weight;

		for (int i = 0; i < this.f1Count; i++) {
			for (int j = 0; j < this.f2Count; j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				weightsF1toF2.add(i, j, weight);
				weightsF2toF1.add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		this.weightsF1toF2.clear();
		this.weightsF2toF1.clear();
	}

	/**
	 * Setup the network logic, read parameters from the network. NOT USED, call
	 * compute(NeuralInputData).
	 * 
	 * @param input
	 *            NOT USED
	 * @param useHolder
	 *            NOT USED
	 * @return NOT USED
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		final String str = "Compute on BasicNetwork cannot be used, rather call"
				+ " the compute(NeuralData) method on the BAMLogic.";
		if (BAM.LOGGER.isErrorEnabled()) {
			BAM.LOGGER.error(str);
		}
		throw new NeuralNetworkError(str);
	}

	/**
	 * Compute the network for the specified input.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	public NeuralDataMapping compute(final NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {

			stable1 = propagateLayer(weightsF1toF2, input.getFrom(), input
					.getTo());
			stable2 = propagateLayer(weightsF2toF1, input.getTo(), input
					.getFrom());

		} while (!stable1 && !stable2);
		return null;
	}

	/**
	 * Get the specified weight.
	 * 
	 * @param synapse
	 *            The synapse to get the weight from.
	 * @param input
	 *            The input, to obtain the size from.
	 * @param x
	 *            The x matrix value. (could be row or column, depending on
	 *            input)
	 * @param y
	 *            The y matrix value. (could be row or column, depending on
	 *            input)
	 * @return The value from the matrix.
	 */
	private double getWeight(final Matrix matrix, final NeuralData input,
			final int x, final int y) {
		if (matrix.getRows() != input.size()) {
			return matrix.get(x, y);
		} else {
			return matrix.get(y, x);
		}
	}


	/**
	 * Propagate the layer.
	 * 
	 * @param synapse
	 *            The synapse for this layer.
	 * @param input
	 *            The input pattern.
	 * @param output
	 *            The output pattern.
	 * @return True if the network has become stable.
	 */
	private boolean propagateLayer(final Matrix matrix,
			final NeuralData input, final NeuralData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < output.size(); i++) {
			sum = 0;
			for (j = 0; j < input.size(); j++) {
				sum += getWeight(matrix, input, i, j) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0) {
					out = -1;
				} else {
					out = 1;
				}
				if (out != (int) output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}

	/**
	 * @return the f1Count
	 */
	public int getF1Count() {
		return f1Count;
	}

	/**
	 * @return the f2Count
	 */
	public int getF2Count() {
		return f2Count;
	}

	/**
	 * @return the weightsF1toF2
	 */
	public Matrix getWeightsF1toF2() {
		return weightsF1toF2;
	}

	/**
	 * @return the weightsF2toF1
	 */
	public Matrix getWeightsF2toF1() {
		return weightsF2toF1;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_BAM);
		obj.setStandardProperties(this);
		
		obj.setProperty(PersistConst.PROPERTY_F1_COUNT, this.f1Count, false);
		obj.setProperty(PersistConst.PROPERTY_F2_COUNT, this.f2Count, false);
		obj.setProperty(PersistConst.PROPERTY_WEIGHTS_F1_F2, this.weightsF1toF2);
		obj.setProperty(PersistConst.PROPERTY_WEIGHTS_F2_F1, this.weightsF2toF1);

	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_BAM);
		this.f1Count = obj.getPropertyInt(PersistConst.PROPERTY_F1_COUNT, true);
		this.f2Count = obj.getPropertyInt(PersistConst.PROPERTY_F2_COUNT, true);
		this.weightsF1toF2 = obj.getPropertyMatrix(PersistConst.PROPERTY_WEIGHTS_F1_F2, true);
		this.weightsF2toF1 = obj.getPropertyMatrix(PersistConst.PROPERTY_WEIGHTS_F2_F1, true);
	}
	
}
