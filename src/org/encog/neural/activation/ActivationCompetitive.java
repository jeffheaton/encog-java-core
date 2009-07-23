package org.encog.neural.activation;

import org.encog.neural.NeuralNetworkError;
import org.encog.persist.Persistor;

public class ActivationCompetitive extends BasicActivationFunction {

	/**
	 * The serial ID
	 */
	private static final long serialVersionUID = 5396927873082336888L;
	
	private int maxWinners = 1;

	public Object clone() {
		// TODO Auto-generated method stub
		return new ActivationCompetitive();
	}

	public Persistor createPersistor() {
		return null;
	}

	public void activationFunction(double[] d) {
		boolean[] winners = new boolean[d.length];
		double sumWinners = 0;
		
		// find the desired number of winners
		for(int i=0;i<this.maxWinners;i++)
		{
			double maxFound = Double.MIN_VALUE;
			int winner = -1;
			
			// find one winner
			for(int j=0;j<d.length;j++)
			{
				if( !winners[j] && d[j]>maxFound )
				{
					winner = j;
					maxFound = d[j];
				}
			}
			sumWinners+=maxFound;
			winners[winner] = true;
		}
		
		// adjust weights for winners and non-winners
		for(int i=0;i<d.length;i++)
		{
			if( winners[i] )
			{
				d[i] = d[i]/sumWinners;
			}
			else
			{
				d[i] = 0.0;
			}
		}
	}

	public void derivativeFunction(double[] d) {
		throw new NeuralNetworkError(
				"Can't use the competitive activation function "
						+ "where a derivative is required.");
		
	}

	public boolean hasDerivative() {
		return false;
	}

}
