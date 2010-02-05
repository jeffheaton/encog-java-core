/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.neural.networks.training;

import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;

public class MockTrain extends BasicTraining implements LearningRate, Momentum {

	private BasicNetwork network;
	private boolean wasUsed;
	private double momentum;
	private double learningRate;

	
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	public void setNetwork(BasicNetwork network)
	{
		this.network = network;
	}

	public void simulate(double newError, double firstValue) {
		preIteration();
		MockTrain.setFirstElement(firstValue, this.network);
		setError(newError);
		postIteration();
		this.wasUsed = true;
	}
	
	public void iteration() {
		preIteration();
		postIteration();
		this.wasUsed = true;
	}
	
	public static void setFirstElement(double value, BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		d[0] = value;
		NetworkCODEC.arrayToNetwork(d, network);
	}
	
	public static double getFirstElement(BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		return d[0];
	}

	public boolean wasUsed() {
		return wasUsed;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;
		
	}



}
