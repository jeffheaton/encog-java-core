/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.util.network;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.HopfieldPattern;

/**
 * This class holds a Hopfield network.  It provides tools to be used with
 * the Hopfield neural network.  
 * 
 * To use this class, add one or more patterns for the Hopfield network
 * to recognize.  These are added using the addPattern method.
 * 
 * The current state (getCurrentState) starts out all false.  Calling the
 * run method passes through one iteration.  The network will tend to 
 * decrease energy and move to one of the patterns it has been trained with
 * until it stabilizes. 
 * 
 * The current energy for the network can be obtained by calling 
 * calculateEnergy.  The network will seek to lower this energy value.
 * The energy value is a reflection of how far the current state is from
 * a pattern.
 *
 */
public class HopfieldHolder extends ThermalNetworkHolder {
	
	
	/**
	 * Construct a Hopfield network with the specified number of
	 * neurons.
	 * @param neuronCount The number of neurons for the Hopfield network.
	 */
	public HopfieldHolder(int neuronCount)
	{
		HopfieldPattern pattern = new HopfieldPattern();
		pattern.setInputNeurons(neuronCount);
		setNetwork(pattern.generate());
		init();
	}
	
	/**
	 * Construct the holder from an already existing network.
	 * @param network The network to use.
	 */
	public HopfieldHolder(BasicNetwork network)
	{
		init();
	}
	
	
	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param pattern
	 *            The pattern to train for.
	 */
	public void addPattern(final NeuralData pattern) {

		// Create a row matrix from the input, convert boolean to bipolar
		final Matrix m2 = Matrix.createRowMatrix(pattern.getData());
		// Transpose the matrix and multiply by the original input matrix
		final Matrix m1 = MatrixMath.transpose(m2);
		final Matrix m3 = MatrixMath.multiply(m1, m2);

		// matrix 3 should be square by now, so create an identity
		// matrix of the same size.
		final Matrix identity = MatrixMath.identity(m3.getRows());

		// subtract the identity matrix
		final Matrix m4 = MatrixMath.subtract(m3, identity);

		// now add the calculated matrix, for this pattern, to the
		// existing weight matrix.
		convertHopfieldMatrix(m4);
	}
	
	/**
	 * Update the Hopfield weights after training.
	 * @param delta The amount to change the weights by.
	 */
	private void convertHopfieldMatrix(final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				this.getThermalSynapse().getMatrix().add(
						row, col, delta.get(row, col));
			}
		}
	}
	
	/**
	 * Perform one Hopfield iteration.
	 */
	public void run()
	{
		NeuralData temp = this.getNetwork().compute(this.getCurrentState());
		for(int i=0;i<temp.size();i++)
		{
			this.getCurrentState().setData(i, temp.getData(i)>0 );
		}
	}
	
	/**
	 * Run the network until it becomes stable and does not change from
	 * more runs.
	 * @param max The maximum number of cycles to run before giving up.
	 * @return The number of cycles that were run.
	 */
	public int runUntilStable(int max)
	{
		boolean done = false;
		String lastStateStr = this.getCurrentState().toString();
		String currentStateStr = this.getCurrentState().toString();
		
		int cycle = 0;
		do
		{
			run();
			cycle++;
			
			lastStateStr = this.getCurrentState().toString();
			
			if( !currentStateStr.equals(lastStateStr) )
			{
				if( cycle>max )
					done = true;
			}
			else
				done = true;
			
			currentStateStr = lastStateStr;
			
		} while( !done );
		
		return cycle;
	}
}
