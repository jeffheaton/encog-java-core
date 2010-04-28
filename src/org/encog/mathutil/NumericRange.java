package org.encog.mathutil;

import java.util.List;

import org.encog.util.Format;

public class NumericRange {
	private final double high;
	private final double low;
	private final double mean;
	private final double rms;
	private final double standardDeviation;
	private final int samples;
	
	public NumericRange(List<Double> values) {
		
		double assignedHigh = 0;
		double assignedLow = 0;
		double total = 0;
		double rmsTotal = 0;		
		
		// get the mean and other 1-pass values.
		
		for( double d: values)
		{
			assignedHigh = Math.max(assignedHigh, d);
			assignedLow = Math.min(assignedLow, d);
			total+=d;
			rmsTotal+=d*d;			
		}
		
		this.samples = values.size();
		this.high = assignedHigh;
		this.low = assignedLow;
		this.mean = total/this.samples;
		this.rms = Math.sqrt(rmsTotal/this.samples);
		
		// now get the standard deviation
		double devTotal = 0;
		
		for( double d:values )
		{
			devTotal+= Math.pow(d - this.mean,2);
		}
		this.standardDeviation = Math.sqrt(devTotal/this.samples);
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getMean() {
		return mean;
	}

	public double getRms() {
		return rms;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public int getSamples() {
		return samples;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("Range: ");
		result.append(Format.formatDouble(this.low,5));
		result.append(" to ");
		result.append(Format.formatDouble(this.high,5));
		result.append(",samples: ");
		result.append(Format.formatInteger(this.samples));
		result.append(",mean: ");
		result.append(Format.formatDouble(this.mean,5));
		result.append(",rms: ");
		result.append(Format.formatDouble(this.rms,5));
		result.append(",s.deviation: ");
		result.append(Format.formatDouble(this.standardDeviation,5));
		
		return result.toString();
	}
}
