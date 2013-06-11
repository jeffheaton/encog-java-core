package org.encog.ml.data.auto;

import org.encog.Encog;
import org.encog.util.Format;

public class AutoFloatColumn {
	private float[] data;
	private float actualMax;
	private float actualMin;
	
	public AutoFloatColumn(float[] theData) {
		this(theData,0,0);
		autoMinMax();
	}
	public AutoFloatColumn(float[] theData, float actualMax, float actualMin) {
		this.data = theData;
		this.actualMax = actualMax;
		this.actualMin = actualMin;
	}
	public void autoMinMax() {
		this.actualMax = Float.MIN_VALUE;
		this.actualMin = Float.MAX_VALUE;
		for(float f: this.data) {
			this.actualMax = Math.max(this.actualMax, f);
			this.actualMin = Math.min(this.actualMin, f);
		}
	}
	public float[] getData() {
		return data;
	}
	public float getActualMax() {
		return actualMax;
	}
	public float getActualMin() {
		return actualMin;
	}
	public float getNormalized(int index, float normalizedMin, float normalizedMax) {
		float x = data[index];
		return ((x - actualMin) 
				/ (actualMax - actualMin))
				* (normalizedMax - normalizedMin) + normalizedMin;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":min=");
		result.append(Format.formatDouble(this.actualMin, Encog.DEFAULT_PRECISION));
		result.append(",max=");
		result.append(Format.formatDouble(this.actualMin, Encog.DEFAULT_PRECISION));
		result.append(",max=");
		result.append("]");
		return result.toString();
	}
}
