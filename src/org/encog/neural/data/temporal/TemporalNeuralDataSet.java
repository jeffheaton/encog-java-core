package org.encog.neural.data.temporal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.time.TimeSpan;
import org.encog.util.time.TimeUnit;

public class TemporalNeuralDataSet extends BasicNeuralDataSet {
	
	private List<TemporalDataDescription> descriptions = new ArrayList<TemporalDataDescription>();
	private List<TemporalPoint> points = new ArrayList<TemporalPoint>();
	private int inputWindowSize;
	private int predictWindowSize;
	private int lowSequence;
	private int highSequence;
	private int desiredSetSize;
	private int inputNeuronCount;
	private int outputNeuronCount;
	private Date startingPoint;
	private TimeUnit sequenceGrandularity;
	
	public static final String ADD_NOT_SUPPORTED = "Direct adds to the temporal dataset are not supported.  Add TemporalPoint objects and call generate.";
	
	public TemporalNeuralDataSet(int inputWindowSize,int predictWindowSize)
	{
		this.inputWindowSize = inputWindowSize;
		this.predictWindowSize = predictWindowSize;
		this.lowSequence = Integer.MIN_VALUE;
		this.highSequence = Integer.MAX_VALUE;
		this.desiredSetSize = Integer.MAX_VALUE;
		this.startingPoint = null;
		this.sequenceGrandularity = TimeUnit.DAYS;
	}
	
	public void addDescription(TemporalDataDescription desc)
	{
		if( this.points.size()>0 ) {
			throw new TemporalError("Can't add anymore descriptions, there are already temporal points defined.");
		}
		
		int index = this.descriptions.size();
		desc.setIndex(index);
		
		this.descriptions.add(desc);
		calculateNeuronCounts();
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
	
	public List<TemporalPoint> getPoints()
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
	
	public int getSequenceFromDate(Date when)
	{
		int sequence; 
		
		if( startingPoint!=null )
		{
			TimeSpan span = new TimeSpan(this.startingPoint,when);			
			sequence = (int)span.getSpan(this.sequenceGrandularity);
		}
		else
		{
			this.startingPoint = when;
			sequence = 0;
		}
		
		return sequence;
	}
	
	public TemporalPoint createPoint(Date when)
	{
		int sequence = getSequenceFromDate(when);		
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
			if( isPointInRange(point) )
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
	
	public void calculateNeuronCounts()
	{
		this.inputNeuronCount = 0;
		this.outputNeuronCount = 0;
		
		for(TemporalDataDescription desc: this.descriptions)
		{
			if( desc.isInput())
				this.inputNeuronCount+=this.inputWindowSize;
			if( desc.isPredict())
				this.outputNeuronCount+=this.predictWindowSize;
		}
	}
	
	public boolean isPointInRange(TemporalPoint point)
	{
		return( (point.getSequence()>=this.getLowSequence()) &&
				(point.getSequence()<=this.getHighSequence()) );
	
	}
	
	public BasicNeuralData generateInputNeuralData(int index)
	{		
		if( index+this.inputWindowSize > this.points.size())
		{
			throw new TemporalError("Can't generate input temporal data beyond the end of provided data.");
		}
		
		BasicNeuralData result = new BasicNeuralData(this.inputNeuronCount);
		int resultIndex = 0;
		
		for(int i=0;i<this.inputWindowSize;i++)
		{
			int descriptionIndex = 0;

			for(TemporalDataDescription desc:this.descriptions)
			{
				if( desc.isInput() )
				{
					result.setData(resultIndex++,this.formatData(desc, index+i));
				}
				descriptionIndex++;
			}
		}
		return result;
	}
	
	private double getDataRAW(TemporalDataDescription desc,int index)
	{
		 TemporalPoint point = this.points.get(index);
		 return point.getData(desc.getIndex());
	}
	
	private double getDataDeltaChange(TemporalDataDescription desc,int index)
	{
		if( index==0 )
			return 0.0;
		TemporalPoint point = this.points.get(index);
		TemporalPoint previousPoint = this.points.get(index-1);
		return point.getData(desc.getIndex())-previousPoint.getData(desc.getIndex());
	}
	
	private double getDataPercentChange(TemporalDataDescription desc,int index)
	{
		if( index==0 )
			return 0.0;
		TemporalPoint point = this.points.get(index);
		TemporalPoint previousPoint = this.points.get(index-1);
		double currentValue = point.getData(desc.getIndex());
		double previousValue = previousPoint.getData(desc.getIndex());
		return (currentValue-previousValue)/currentValue;
	}
	
	private double formatData(TemporalDataDescription desc,int index)
	{
		double result = 0;
		
		switch(desc.getType())
		{
			case DELTA_CHANGE:
				result = getDataDeltaChange(desc, index);
				break;
			case PERCENT_CHANGE:
				result = getDataPercentChange(desc, index);
				break;
			case RAW:
				result = getDataRAW(desc,index);
				break;
		}
		
		if( desc.getActivationFunction()!=null )
		{
			result = desc.getActivationFunction().activationFunction(result);
		}
		
		return result;
	}
	
	public BasicNeuralData generateOutputNeuralData(int index)
	{		
		if( index+this.predictWindowSize > this.points.size())
		{
			throw new TemporalError("Can't generate prediction temporal data beyond the end of provided data.");
		}
		
		BasicNeuralData result = new BasicNeuralData(this.outputNeuronCount);
		int resultIndex = 0;
		
		for(int i=0;i<this.predictWindowSize;i++)
		{
			int descriptionIndex = 0;

			for(TemporalDataDescription desc:this.descriptions)
			{
				if( desc.isPredict() )
				{
					result.setData(resultIndex++,this.formatData(desc, index+i));
				}
				descriptionIndex++;
			}
			
		}
		return result;
	}
	
	
	
	/**
	 * @return the inputNeuronCount
	 */
	public int getInputNeuronCount() {
		return inputNeuronCount;
	}

	/**
	 * @return the outputNeuronCount
	 */
	public int getOutputNeuronCount() {
		return outputNeuronCount;
	}
	
	public int calculateStartIndex()
	{
		for(int i=0;i<this.points.size(); i++ )
		{
			TemporalPoint point = this.points.get(i);
			if( this.isPointInRange(point))
			{
				return i;
			}
		}
		
		return -1;
	}

	public void generate()
	{
		Collections.sort(this.points);
		int start = calculateStartIndex();
		int setSize = calculateActualSetSize();	
		int range = start+(setSize-this.predictWindowSize-this.inputWindowSize);

		for(int i=start;i<range;i++)
		{
			BasicNeuralData input = generateInputNeuralData(i);
			BasicNeuralData ideal = generateOutputNeuralData(i+this.inputWindowSize);
			BasicNeuralDataPair pair = new BasicNeuralDataPair(input,ideal);
			super.add(pair);
		}		
	}

	/**
	 * @return the startingPoint
	 */
	public Date getStartingPoint() {
		return startingPoint;
	}

	/**
	 * @return the sequenceGrandularity
	 */
	public TimeUnit getSequenceGrandularity() {
		return sequenceGrandularity;
	}

	/**
	 * @param sequenceGrandularity the sequenceGrandularity to set
	 */
	public void setSequenceGrandularity(TimeUnit sequenceGrandularity) {
		this.sequenceGrandularity = sequenceGrandularity;
	}
	
	
	
	
}
