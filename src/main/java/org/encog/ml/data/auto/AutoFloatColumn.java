package org.encog.ml.data.auto;

public class AutoFloatColumn {
	private float[] data;
	private float actualMax;
	private float actualMin;
			
	public AutoFloatColumn(float[] data, float actualMax, float actualMin) {
		this.data = data;
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
}
