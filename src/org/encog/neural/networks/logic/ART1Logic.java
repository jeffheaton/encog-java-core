/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.logic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.ART1Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an ART1 type network. See ART1Pattern for more
 * information on this type of network.
 */
public class ART1Logic extends ARTLogic {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -8430698735871301528L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(ART1Logic.class);

	/**
	 * The first layer, basically, the input layer.
	 */
	private Layer layerF1;

	/**
	 * The second layer, basically, the output layer.
	 */
	private Layer layerF2;

	/**
	 * The connection from F1 to F2.
	 */
	private Synapse synapseF1toF2;

	/**
	 * The connection from F2 to F1.
	 */
	private Synapse synapseF2toF1;

	/**
	 * last winner in F2 layer.
	 */
	private int winner;

	/**
	 * A parameter for F1 layer.
	 */
	private double a1 = 1;

	/**
	 * B parameter for F1 layer.
	 */
	private double b1 = 1.5;

	/**
	 * C parameter for F1 layer.
	 */
	private double c1 = 5;

	/**
	 * D parameter for F1 layer.
	 */
	private double d1 = 0.9;

	/**
	 * L parameter for net.
	 */
	private double l = 3;

	/**
	 * The vigilance parameter.
	 */
	private double vigilance = 0.9;

	/**
	 * Allows members of the F2 layer to be inhibited.
	 */
	private boolean[] inhibitF2;

	/**
	 * Tracks if there was no winner.
	 */
	private int noWinner;

	/**
	 * The output from the F1 layer.
	 */
	private BiPolarNeuralData outputF1;

	/**
	 * The output from the F2 layer.
	 */
	private BiPolarNeuralData outputF2;

	/**
	 * Adjust the weights for the pattern just presented.
	 */
	public void adjustWeights() {
		double magnitudeInput;

		for (int i = 0; i < this.layerF1.getNeuronCount(); i++) {
			if (this.outputF1.getBoolean(i)) {
				magnitudeInput = magnitude(this.outputF1);
				this.synapseF1toF2.getMatrix().set(i, this.winner, 1);
				this.synapseF2toF1.getMatrix().set(this.winner, i,
						this.l / (this.l - 1 + magnitudeInput));
			} else {
				this.synapseF1toF2.getMatrix().set(i, this.winner, 0);
				this.synapseF2toF1.getMatrix().set(this.winner, i, 0);
			}
		}
	}

	/**
	 * Compute the output from the ART1 network. This can be called directly or
	 * used by the BasicNetwork class. Both input and output should be bipolar
	 * numbers.
	 * 
	 * @param input
	 *            The input to the network.
	 * @param output
	 *            The output from the network.
	 */
	public void compute(final BiPolarNeuralData input,
			final BiPolarNeuralData output) {
		int i;
		boolean resonance, exhausted;
		double magnitudeInput1, magnitudeInput2;

		for (i = 0; i < this.layerF2.getNeuronCount(); i++) {
			this.inhibitF2[i] = false;
		}
		resonance = false;
		exhausted = false;
		do {
			setInput(input);
			computeF2();
			getOutput(output);
			if (this.winner != this.noWinner) {
				computeF1(input);
				magnitudeInput1 = magnitude(input);
				magnitudeInput2 = magnitude(this.outputF1);
				if ((magnitudeInput2 / magnitudeInput1) < this.vigilance) {
					this.inhibitF2[this.winner] = true;
				} else {
					resonance = true;
				}
			} else {
				exhausted = true;
			}
		} while (!(resonance || exhausted));
		if (resonance) {
			adjustWeights();
		}
	}

	/**
	 * Compute the output for the BasicNetwork class.
	 * 
	 * @param input
	 *            The input to the network.
	 * @param useHolder
	 *            The NeuralOutputHolder to use.
	 * @return The output from the network.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		if (!(input instanceof BiPolarNeuralData)) {
			final String str = 
				"Input to ART1 logic network must be BiPolarNeuralData.";
			if (ART1Logic.LOGGER.isErrorEnabled()) {
				ART1Logic.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		}

		final BiPolarNeuralData output = new BiPolarNeuralData(this.layerF1
				.getNeuronCount());
		compute((BiPolarNeuralData) input, output);
		return output;
	}

	/**
	 * Compute the output from the F1 layer.
	 * 
	 * @param input
	 *            The input to the F1 layer.
	 */
	private void computeF1(final BiPolarNeuralData input) {
		double sum, activation;

		for (int i = 0; i < this.layerF1.getNeuronCount(); i++) {
			sum = this.synapseF1toF2.getMatrix().get(i, this.winner)
					* (this.outputF2.getBoolean(this.winner) ? 1 : 0);
			activation = ((input.getBoolean(i) ? 1 : 0) 
					+ this.d1 * sum - this.b1)
					/ (1 + this.a1
							* ((input.getBoolean(i) ? 1 : 0) + this.d1 * sum) 
							+ this.c1);
			this.outputF1.setData(i, activation > 0);
		}
	}

	/**
	 * Compute the output from the F2 layer.
	 */
	private void computeF2() {
		int i, j;
		double sum, maxOut;

		maxOut = Double.NEGATIVE_INFINITY;
		this.winner = this.noWinner;
		for (i = 0; i < this.layerF2.getNeuronCount(); i++) {
			if (!this.inhibitF2[i]) {
				sum = 0;
				for (j = 0; j < this.layerF1.getNeuronCount(); j++) {
					sum += this.synapseF2toF1.getMatrix().get(i, j)
							* (this.outputF1.getBoolean(j) ? 1 : 0);
				}
				if (sum > maxOut) {
					maxOut = sum;
					this.winner = i;
				}
			}
			this.outputF2.setData(i, false);
		}
		if (this.winner != this.noWinner) {
			this.outputF2.setData(this.winner, true);
		}
	}

	/**
	 * @return The A1 parameter.
	 */
	public double getA1() {
		return this.a1;
	}

	/**
	 * @return The B1 parameter.
	 */
	public double getB1() {
		return this.b1;
	}

	/**
	 * @return The C1 parameter.
	 */
	public double getC1() {
		return this.c1;
	}

	/**
	 * @return The D1 parameter.
	 */
	public double getD1() {
		return this.d1;
	}

	/**
	 * @return The L parameter.
	 */
	public double getL() {
		return this.l;
	}

	/**
	 * Copy the output from the network to another object.
	 * 
	 * @param output
	 *            The target object for the output from the network.
	 */
	private void getOutput(final BiPolarNeuralData output) {
		for (int i = 0; i < this.layerF2.getNeuronCount(); i++) {
			output.setData(i, this.outputF2.getBoolean(i));
		}
	}

	/**
	 * @return The vigilance parameter.
	 */
	public double getVigilance() {
		return this.vigilance;
	}

	/**
	 * @return The winning neuron.
	 */
	public int getWinner() {
		return this.winner;
	}

	/**
	 * @return Does this network have a "winner"?
	 */
	public boolean hasWinner() {
		return this.winner != this.noWinner;
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * 
	 * @param network
	 *            The network that this logic class belongs to.
	 */
	@Override
	public void init(final BasicNetwork network) {
		super.init(network);

		this.layerF1 = getNetwork().getLayer(ART1Pattern.TAG_F1);
		this.layerF2 = getNetwork().getLayer(ART1Pattern.TAG_F2);
		this.inhibitF2 = new boolean[this.layerF2.getNeuronCount()];
		this.synapseF1toF2 = getNetwork().getStructure().findSynapse(
				this.layerF1, this.layerF2, true);
		this.synapseF2toF1 = getNetwork().getStructure().findSynapse(
				this.layerF2, this.layerF1, true);
		this.outputF1 = new BiPolarNeuralData(this.layerF1.getNeuronCount());
		this.outputF2 = new BiPolarNeuralData(this.layerF2.getNeuronCount());

		this.a1 = getNetwork().getPropertyDouble(ARTLogic.PROPERTY_A1);
		this.b1 = getNetwork().getPropertyDouble(ARTLogic.PROPERTY_B1);
		this.c1 = getNetwork().getPropertyDouble(ARTLogic.PROPERTY_C1);
		this.d1 = getNetwork().getPropertyDouble(ARTLogic.PROPERTY_D1);
		this.l = getNetwork().getPropertyDouble(ARTLogic.PROPERTY_L);
		this.vigilance = getNetwork().getPropertyDouble(
				ARTLogic.PROPERTY_VIGILANCE);

		this.noWinner = this.layerF2.getNeuronCount();
		reset();

	}

	/**
	 * Get the magnitude of the specified input.
	 * 
	 * @param input
	 *            The input to calculate the magnitude for.
	 * @return The magnitude of the specified pattern.
	 */
	public double magnitude(final BiPolarNeuralData input) {
		double result;

		result = 0;
		for (int i = 0; i < this.layerF1.getNeuronCount(); i++) {
			result += input.getBoolean(i) ? 1 : 0;
		}
		return result;
	}

	/**
	 * Reset the weight matrix back to starting values.
	 */
	public void reset() {
		for (int i = 0; i < this.layerF1.getNeuronCount(); i++) {
			for (int j = 0; j < this.layerF2.getNeuronCount(); j++) {
				this.synapseF1toF2.getMatrix().set(i, j,
						(this.b1 - 1) / this.d1 + 0.2);
				this.synapseF2toF1.getMatrix().set(
						j,
						i,
						this.l / (this.l - 1 + this.layerF1.getNeuronCount())
								- 0.1);
			}
		}
	}

	/**
	 * Set the A1 parameter.
	 * 
	 * @param a1
	 *            The new value.
	 */
	public void setA1(final double a1) {
		this.a1 = a1;
	}

	/**
	 * Set the B1 parameter.
	 * 
	 * @param b1
	 *            The new value.
	 */
	public void setB1(final double b1) {
		this.b1 = b1;
	}

	/**
	 * Set the C1 parameter.
	 * 
	 * @param c1
	 *            The new value.
	 */
	public void setC1(final double c1) {
		this.c1 = c1;
	}

	/**
	 * Set the D1 parameter.
	 * 
	 * @param d1
	 *            The new value.
	 */
	public void setD1(final double d1) {
		this.d1 = d1;
	}

	/**
	 * Set the input to the neural network.
	 * 
	 * @param input
	 *            The input.
	 */
	private void setInput(final BiPolarNeuralData input) {
		double activation;

		for (int i = 0; i < this.layerF1.getNeuronCount(); i++) {
			activation = (input.getBoolean(i) ? 1 : 0)
					/ (1 + this.a1 * ((input.getBoolean(i) ? 1 : 0) 
							+ this.b1) + this.c1);
			this.outputF1.setData(i, (activation > 0));
		}
	}

	/**
	 * Set the L parameter.
	 * 
	 * @param l
	 *            The new value.
	 */
	public void setL(final double l) {
		this.l = l;
	}

	/**
	 * Set the vigilance.
	 * 
	 * @param vigilance
	 *            The new value.
	 */
	public void setVigilance(final double vigilance) {
		this.vigilance = vigilance;
	}
}
