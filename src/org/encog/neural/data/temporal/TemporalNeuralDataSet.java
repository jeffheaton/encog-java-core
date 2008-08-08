package org.encog.neural.data.temporal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class TemporalNeuralDataSet extends BasicNeuralDataSet {
	
	private List<TemporalDataDescription> descriptions = new ArrayList<TemporalDataDescription>();
	private Set<TemporalPoint> points = new TreeSet<TemporalPoint>();
	private int inputWindowSize;
	private int predictWindowSize;
	private int lowSequence;
	private int highSequence;
	private int desiredSetSize;
	private double scalingRatio;
	
	public static final String ADD_NOT_SUPPORTED = "Direct adds to the temporal dataset are not supported.  Add TemporalPoint objects and call generate.";
	
	public TemporalNeuralDataSet(int inputWindowSize,int predictWindowSize)
	{
		this.inputWindowSize = inputWindowSize;
		this.predictWindowSize = predictWindowSize;
		this.lowSequence = Integer.MIN_VALUE;
		this.highSequence = -Integer.MAX_VALUE;
		this.desiredSetSize = Integer.MAX_VALUE;
	}
	
	public void addDescription(TemporalDataDescription desc)
	{
		if( this.points.size()>0 ) {
			throw new TemporalError("Can't add anymore descriptions, there are already temporal points defined.");
		}
		
		this.descriptions.add(desc);
	}
	
	public void clear()
	{
		descriptions.clear();
		points.clear();
		this.getData().clear();
	}
	
	public List<TemporalDataDescription> getDescriptions()
	{
		return this.descriptions;
	}
	
	public Set<TemporalPoint> getPoints()
	{
		return this.points;
	}
	
	public void add(NeuralData inputData,NeuralData idealData)
	{
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	public void add(NeuralDataPair inputData) 
	{
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);				
	}
	
	public void add(NeuralData data) 
	{
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);				
	}

	/**
	 * @return the inputWindowSize
	 */
	public int getInputWindowSize() {
		return inputWindowSize;
	}

	/**
	 * @param inputWindowSize the inputWindowSize to set
	 */
	public void setInputWindowSize(int inputWindowSize) {
		this.inputWindowSize = inputWindowSize;
	}

	/**
	 * @return the predictWindowSize
	 */
	public int getPredictWindowSize() {
		return predictWindowSize;
	}

	/**
	 * @param predictWindowSize the predictWindowSize to set
	 */
	public void setPredictWindowSize(int predictWindowSize) {
		this.predictWindowSize = predictWindowSize;
	}

	/**
	 * @return the lowSequence
	 */
	public int getLowSequence() {
		return lowSequence;
	}

	/**
	 * @param lowSequence the lowSequence to set
	 */
	public void setLowSequence(int lowSequence) {
		this.lowSequence = lowSequence;
	}

	/**
	 * @return the highSequence
	 */
	public int getHighSequence() {
		return highSequence;
	}

	/**
	 * @param highSequence the highSequence to set
	 */
	public void setHighSequence(int highSequence) {
		this.highSequence = highSequence;
	}

	/**
	 * @return the desiredSetSize
	 */
	public int getDesiredSetSize() {
		return desiredSetSize;
	}

	/**
	 * @param desiredSetSize the desiredSetSize to set
	 */
	public void setDesiredSetSize(int desiredSetSize) {
		this.desiredSetSize = desiredSetSize;
	}	
	
	public TemporalPoint createPoint(int sequence)
	{
		TemporalPoint point = new TemporalPoint(this.descriptions.size());
		point.setSequence(sequence);
		this.points.add(point);
		return point;
	}
	
	public int calculatePointsInRange()
	{
		int result = 0;
		
		for(TemporalPoint point:points)
		{
			if( (point.getSequence()>=this.getLowSequence()) &&
				(point.getSequence()<=this.getHighSequence()) )
			{
				result++;
			}
		}
		
		return result;
	}
	
	public int calculateActualSetSize()
	{
		int result = calculatePointsInRange();
		result = Math.min(this.desiredSetSize, result);
		return result;
	}
	
	private BasicNeuralData generateNeuralData(int index)
	{
		BasicNeuralData result = new BasicNeuralData(this.inputWindowSize); 
		return result;
	}
	
	
	public void generate()
	{
		int setSize = calculateActualSetSize();
		
		for(int i=0;i<setSize;i++)
		{
			BasicNeuralData input = generateNeuralData(i);
			BasicNeuralData ideal = generateNeuralData(i);
			BasicNeuralDataPair pair = new BasicNeuralDataPair(input,ideal);
			super.add(pair);
		}
	}
}
