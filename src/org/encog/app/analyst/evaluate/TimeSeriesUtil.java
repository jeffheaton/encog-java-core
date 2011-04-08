package org.encog.app.analyst.evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.engine.util.EngineArray;

public class TimeSeriesUtil {

	private final int lagDepth;
	private final int leadDepth;
	private final int totalDepth;
	private final EncogAnalyst analyst;
	private final int inputSize;
	private final int outputSize;
	private final List<double[]> buffer = new ArrayList<double[]>();
	private final Map<String, Integer> headingMap = new HashMap<String, Integer>();

	public TimeSeriesUtil(EncogAnalyst analyst, List<String> headings) {
		this.analyst = analyst;
		this.lagDepth = analyst.getLagDepth();
		this.leadDepth = analyst.getLagDepth();
		this.totalDepth = this.lagDepth + this.leadDepth;
		this.inputSize = analyst.countUniqueColumns();
		this.outputSize = analyst.determineInputCount()
				+ analyst.determineOutputCount();

		int headingIndex = 0;
		for (String column : headings) {
			headingMap.put(column, headingIndex++);
		}
	}

	public double[] process(double[] input) {
		if (input.length != this.inputSize) {
			throw new AnalystError("Invalid input size: " + input.length
					+ ", should be " + this.inputSize);
		}

		this.buffer.add(0, EngineArray.arrayCopy(input));

		// are we ready yet?
		if (buffer.size() < this.totalDepth) {
			return null;
		}

		// create output
		double[] output = new double[this.outputSize];

		int outputIndex = 0;
		for (AnalystField field : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			if (!field.isIgnored()) {
				if (!this.headingMap.containsKey(field.getName())) {
					throw new AnalystError("Undefined field: "
							+ field.getName());
				}
				int headingIndex = this.headingMap.get(field.getName());
				int timeslice = translateTimeSlice(field.getTimeSlice());
				double d = this.buffer.get(timeslice)[headingIndex];
				output[outputIndex++] = d;
			}
		}
		
		// keep the buffer at a good size
		while( this.buffer.size()>this.totalDepth ) {
			this.buffer.remove(this.buffer.size()-1);
		}

		return output;
	}

	private int translateTimeSlice(int index) {
		return Math.abs(index-this.leadDepth);
	}

	/**
	 * @return the lagDepth
	 */
	public int getLagDepth() {
		return lagDepth;
	}

	/**
	 * @return the leadDepth
	 */
	public int getLeadDepth() {
		return leadDepth;
	}

	/**
	 * @return the totalDepth
	 */
	public int getTotalDepth() {
		return totalDepth;
	}

	/**
	 * @return the analyst
	 */
	public EncogAnalyst getAnalyst() {
		return analyst;
	}

	/**
	 * @return the inputSize
	 */
	public int getInputSize() {
		return inputSize;
	}

	/**
	 * @return the outputSize
	 */
	public int getOutputSize() {
		return outputSize;
	}

	/**
	 * @return the buffer
	 */
	public List<double[]> getBuffer() {
		return buffer;
	}

	/**
	 * @return the headingMap
	 */
	public Map<String, Integer> getHeadingMap() {
		return headingMap;
	}
	
	
	
}
