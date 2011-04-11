package org.encog.app.csv.util;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.util.EngineArray;

/**
 * A buffer of bar segments.
 */
public class BarBuffer {

	/**
	 * The bar data loaded.
	 */
	private List<double[]> data = new ArrayList<double[]>();

	/**
	 * The number of periods.
	 */
	private int periods;

	/**
	 * Determine if the buffer is full.
	 */
	public boolean getFull() {
		return data.size() >= this.periods;
	}

	/**
	 * @return The data.
	 */
	public List<double[]> getData() {
		return data;
	}
	
	/**
	 * Construct the object.
	 * @param periods The number of periods.
	 */
	public BarBuffer(int periods) {
		this.periods = periods;
	}

	/**
	 * Add a bar.
	 * @param d The bar data.
	 */
	public void add(double d) {
		double[] da = new double[1];
		da[0] = d;
		add(da);
	}

	/**
	 * Add a bar.
	 * @param d The bar data.
	 */
	public void add(double[] d) {
		data.add(0, EngineArray.arrayCopy(d));
		if (data.size() > periods)
			data.remove(data.size() - 1);
	}

	/**
	 * Average all of the bars.
	 * @param idx The bar index to average.
	 * @return The average.
	 */
	public double average(int idx) {
		double total = 0;
		for (int i = 0; i < data.size(); i++) {
			double[] d = data.get(i);
			total += d[idx];
		}

		return total / data.size();
	}
	
	/**
	 * Get the average gain.
	 * @param idx The field to get the average gain for.
	 * @return The average gain.
	 */
	public double averageGain(int idx) {
		double total = 0;
		int count = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			double[] today = data.get(i);
			double[] yesterday = data.get(i + 1);
			double diff = today[idx] - yesterday[idx];
			if (diff > 0) {
				total += diff;
			}
			count++;
		}

		if (count == 0)
			return 0;
		else
			return total / count;
	}

	/**
	 * Get the average loss.
	 * @param idx The index to check for.
	 * @return The average loss.
	 */
	public double averageLoss(int idx) {
		double total = 0;
		int count = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			double[] today = data.get(i);
			double[] yesterday = data.get(i + 1);
			double diff = today[idx] - yesterday[idx];
			if (diff < 0) {
				total += Math.abs(diff);
			}
			count++;
		}

		if (count == 0)
			return 0;
		else
			return total / count;
	}

	/**
	 * Get the max for the specified index.
	 * @param idx The index to check.
	 * @return The max.
	 */
	public double max(int idx) {
		double result = Double.MIN_VALUE;

		for (double[] d : this.data) {
			result = Math.max(d[idx], result);
		}
		return result;
	}

	/**
	 * Get the min for the specified index.
	 * @param idx The index to check.
	 * @return The min.
	 */
	public double min(int idx) {
		double result = Double.MAX_VALUE;

		for (double[] d : this.data) {
			result = Math.min(d[idx], result);
		}
		return result;
	}

	/**
	 * Pop (and remove) the oldest bar in the buffer.
	 * @return The oldest bar in the buffer.
	 */
	public double[] pop() {
		if (this.data.size() == 0)
			return null;

		int idx = this.data.size() - 1;
		double[] result = this.data.get(idx);
		this.data.remove(idx);
		return result;
	}

}
