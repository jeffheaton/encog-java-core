/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.neural.pattern;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.ART1Logic;
import org.encog.neural.networks.logic.ARTLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern to create an ART-1 neural network.
 */
public class ART1Pattern implements NeuralNetworkPattern {

	/**
	 * The tag for the F1 layer.
	 */
	public static final String TAG_F1 = "F1";

	/**
	 * The tag for the F2 layer.
	 */
	public static final String TAG_F2 = "F2";

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

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
	 * This will fail, hidden layers are not supported for this type of network.
	 * 
	 * @param count
	 *            Not used.
	 */
	public void addHiddenLayer(final int count) {
		final String str = "A ART1 network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
	}

	/**
	 * Clear any properties set for this network.
	 */
	public void clear() {
		this.inputNeurons = this.outputNeurons = 0;

	}

	/**
	 * Generate the neural network.
	 * 
	 * @return The generated neural network.
	 */
	public BasicNetwork generate() {
		final BasicNetwork network = new BasicNetwork(new ART1Logic());

		int y = PatternConst.START_Y;

		final Layer layerF1 = new BasicLayer(new ActivationLinear(), false,
				this.inputNeurons);
		final Layer layerF2 = new BasicLayer(new ActivationLinear(), false,
				this.outputNeurons);
		final Synapse synapseF1toF2 = new WeightedSynapse(layerF1, layerF2);
		final Synapse synapseF2toF1 = new WeightedSynapse(layerF2, layerF1);
		layerF1.getNext().add(synapseF1toF2);
		layerF2.getNext().add(synapseF2toF1);

		// apply tags
		network.tagLayer(BasicNetwork.TAG_INPUT, layerF1);
		network.tagLayer(BasicNetwork.TAG_OUTPUT, layerF2);
		network.tagLayer(ART1Pattern.TAG_F1, layerF1);
		network.tagLayer(ART1Pattern.TAG_F2, layerF2);

		layerF1.setX(PatternConst.START_X);
		layerF1.setY(y);
		y += PatternConst.INC_Y;

		layerF2.setX(PatternConst.START_X);
		layerF2.setY(y);

		network.setProperty(ARTLogic.PROPERTY_A1, this.a1);
		network.setProperty(ARTLogic.PROPERTY_B1, this.b1);
		network.setProperty(ARTLogic.PROPERTY_C1, this.c1);
		network.setProperty(ARTLogic.PROPERTY_D1, this.d1);
		network.setProperty(ARTLogic.PROPERTY_L, this.l);
		network.setProperty(ARTLogic.PROPERTY_VIGILANCE, this.vigilance);

		network.getStructure().finalizeStructure();

		return network;
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
	 * @return The vigilance for the network.
	 */
	public double getVigilance() {
		return this.vigilance;
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
	 * This method will throw an error, you can't set the activation function
	 * for an ART1. type network.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "Can't set the activation function for an ART1.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
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
	 * Set the input neuron (F1 layer) count.
	 * 
	 * @param count
	 *            The input neuron count.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
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
	 * Set the output neuron (F2 layer) count.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

	/**
	 * Set the vigilance for the network.
	 * 
	 * @param vigilance
	 *            The new value.
	 */
	public void setVigilance(final double vigilance) {
		this.vigilance = vigilance;
	}
}
