package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;

public class NEATTraining implements Train {

	private final NeuralDataSet training; 
	private final int inputCount;
	private final int outputCount;
	private final List<NEATGenome> population = new ArrayList<NEATGenome>();
	private final NEATInnovationDB innovations;
	private final List<SplitDepth> splits;
	
	private int currentGenomeID = 1;
	
	
	public NEATTraining(
			NeuralDataSet training, 
			int inputCount, 
			int outputCount,
			int populationSize)
	{
		this.training = training;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		
		// create the initial population
		for (int i=0; i<populationSize; i++)
		{
			population.add(new NEATGenome(assignGenomeID(), inputCount, outputCount));
		}
		
		NEATGenome genome = new NEATGenome(1, inputCount, outputCount);
		
		this.innovations = new NEATInnovationDB(genome.getLinks(),genome.getNeurons());
		
		this.splits = split(null, 0, 1, 0);
	}
	
	public int assignGenomeID()
	{
		return(this.currentGenomeID++);
	}

	public NeuralDataSet getTraining() {
		return training;
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	@Override
	public void addStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishTraining() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BasicNetwork getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Strategy> getStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void iteration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setError(double error) {
		// TODO Auto-generated method stub
		
	}
	
	private List<SplitDepth> split(List<SplitDepth> result, double low, double high, int depth)
	{
		if( result==null )
			result = new ArrayList<SplitDepth>();

	  double span = high-low;

	  result.add(new SplitDepth(low + span/2, depth+1));

	  if (depth > 6)
	  {
	    return result;
	  }

	  else
	  {
		  split(result, low, low+span/2, depth+1);
		  split(result, low+span/2, high, depth+1);

	    return result;
	  }
	}

	public NEATInnovationDB getInnovations() {
		return this.innovations;
	}
	
	
}
