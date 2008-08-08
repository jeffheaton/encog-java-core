package org.encog.neural.data.temporal;

public class TemporalPoint implements Comparable<TemporalPoint> {
	private int sequence;
	private double[] data;
	private boolean used;
			
	public TemporalPoint(int size) {
		this.data = new double[size];
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * @return the data
	 */
	public double[] getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(double[] data) {
		this.data = data;
	}
	
	@Override
	public int compareTo(TemporalPoint that) {
		if( this.getSequence()==that.getSequence() ) {
			return 0;
		}
		else if( this.getSequence()<that.getSequence() )
			return -1;
		else
			return 1;
	}	
	
	public void setData(int index,double d)
	{
		this.data[index] = d;
	}
	
	public double getData(int index)
	{
		return this.data[index];
	}

	/**
	 * @return the used
	 */
	public boolean isUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	void setUsed(boolean used) {
		this.used = used;
	}
	
	
}
