/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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

/**
 * This class implements a temporal neural data set. A temporal neural dataset
 * is designed to use a neural network to predict.
 * 
 * A temporal dataset is a stream of data over a time range. This time range is
 * broken up into "points". Each point can contain one or more values. These
 * values are either the values that you would like to predict, or use to
 * predict. It is possible for a value to be both predicted and used to predict.
 * For example, if you were trying to predict a trend in a stock's price
 * fluctuations you might very well use the security price for both.
 * 
 * Each point that we have data for is stored in the TemporalPoint class. Each
 * TemporalPoint will contain one more data values. These data values are
 * described by the TemporalDataDescription class. For example, if you had five
 * TemporalDataDescription objects added to this class, each Temporal point
 * object would contain five values.
 * 
 * Points are arranged by sequence number. No two points can have the same
 * sequence numbers. Methods are provided to allow you to add points using the
 * Date class. These dates are resolved to sequence number using the level of
 * granularity specified for this class. No two points can occupy the same
 * granularity increment.
 * 
 * @author jheaton
 */
public class TemporalNeuralDataSet extends BasicNeuralDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 7846736117000051687L;

	/**
	 * Error message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = "Direct adds to the temporal dataset are not supported.  "
			+ "Add TemporalPoint objects and call generate.";

	/**
	 * Descriptions of the data needed.
	 */
	private final List<TemporalDataDescription> descriptions = new ArrayList<TemporalDataDescription>();

	/**
	 * The temporal points at which we have data.
	 */
	private final List<TemporalPoint> points = new ArrayList<TemporalPoint>();

	/**
	 * The size of the input window, this is the data being used to predict.
	 */
	private int inputWindowSize;

	/**
	 * The size of the prediction window.
	 */
	private int predictWindowSize;

	/**
	 * The lowest sequence.
	 */
	private int lowSequence;

	/**
	 * The highest sequence.
	 */
	private int highSequence;

	/**
	 * How big would we like the input size to be.
	 */
	private int desiredSetSize;

	/**
	 * How many input neurons will be used.
	 */
	private int inputNeuronCount;

	/**
	 * How many output neurons will there be.
	 */
	private int outputNeuronCount;

	/**
	 * What is the date for the first temporal point.
	 */
	private Date startingPoint;

	/**
	 * What is the granularity of the temporal points? Days, months, years, etc?
	 */
	private TimeUnit sequenceGrandularity;

	/**
	 * Construct a dataset.
	 * 
	 * @param inputWindowSize
	 *            What is the input window size.
	 * @param predictWindowSize
	 *            What is the prediction window size.
	 */
	public TemporalNeuralDataSet(final int inputWindowSize,
			final int predictWindowSize) {
		this.inputWindowSize = inputWindowSize;
		this.predictWindowSize = predictWindowSize;
		this.lowSequence = Integer.MIN_VALUE;
		this.highSequence = Integer.MAX_VALUE;
		this.desiredSetSize = Integer.MAX_VALUE;
		this.startingPoint = null;
		this.sequenceGrandularity = TimeUnit.DAYS;
	}

	/**
	 * Adding directly is not supported. Rather, add temporal points and
	 * generate the training data.
	 * 
	 * @param data
	 *            Not used.
	 */
	public void add(final NeuralData data) {
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adding directly is not supported. Rather, add temporal points and
	 * generate the training data.
	 * 
	 * @param inputData
	 *            Not used.
	 * @param idealData
	 *            Not used.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adding directly is not supported. Rather, add temporal points and
	 * generate the training data.
	 * 
	 * @param inputData
	 *            Not used.
	 */

	public void add(final NeuralDataPair inputData) {
		throw new TemporalError(TemporalNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Add a data description.
	 * 
	 * @param desc
	 *            The data description to add.
	 */
	public void addDescription(final TemporalDataDescription desc) {
		if (this.points.size() > 0) {
			throw new TemporalError(
					"Can't add anymore descriptions, there are "
							+ "already temporal points defined.");
		}

		final int index = this.descriptions.size();
		desc.setIndex(index);

		this.descriptions.add(desc);
		calculateNeuronCounts();
	}

	/**
	 * Calculate the actual set size, this is the number of training set entries
	 * that will be generated.
	 * 
	 * @return The size of the training set.
	 */
	public int calculateActualSetSize() {
		int result = calculatePointsInRange();
		result = Math.min(this.desiredSetSize, result);
		return result;
	}

	/**
	 * Calculate how many input and output neurons will be needed for the
	 * current data.
	 */
	public void calculateNeuronCounts() {
		this.inputNeuronCount = 0;
		this.outputNeuronCount = 0;

		for (final TemporalDataDescription desc : this.descriptions) {
			if (desc.isInput()) {
				this.inputNeuronCount += this.inputWindowSize;
			}
			if (desc.isPredict()) {
				this.outputNeuronCount += this.predictWindowSize;
			}
		}
	}

	/**
	 * Calculate how many points are in the high and low range. These are the
	 * points that the training set will be generated on.
	 * 
	 * @return The number of points.
	 */
	public int calculatePointsInRange() {
		int result = 0;

		for (final TemporalPoint point : this.points) {
			if (isPointInRange(point)) {
				result++;
			}
		}

		return result;
	}

	/**
	 * Calculate the index to start at.
	 * 
	 * @return the starting index.
	 */
	public int calculateStartIndex() {
		for (int i = 0; i < this.points.size(); i++) {
			final TemporalPoint point = this.points.get(i);
			if (isPointInRange(point)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Clear the entire dataset.
	 */
	public void clear() {
		this.descriptions.clear();
		this.points.clear();
		getData().clear();
	}

	/**
	 * Create a temporal point from a time. Using the grandularity each date is
	 * given a unique sequence number. No two dates that fall in the same
	 * grandularity should be specified.
	 * 
	 * @param when
	 *            The time that this point should be created at.
	 * @return The point TemporalPoint created.
	 */
	public TemporalPoint createPoint(final Date when) {
		final int sequence = getSequenceFromDate(when);
		final TemporalPoint point = new TemporalPoint(this.descriptions.size());
		point.setSequence(sequence);
		this.points.add(point);
		return point;
	}

	/**
	 * Create a temporal data point using a sequence number. They can also be
	 * created using time. No two points should have the same sequence number.
	 * 
	 * @param sequence
	 *            The sequence number.
	 * @return A new TemporalPoint object.
	 */
	public TemporalPoint createPoint(final int sequence) {
		final TemporalPoint point = new TemporalPoint(this.descriptions.size());
		point.setSequence(sequence);
		this.points.add(point);
		return point;
	}

	/**
	 * Format data according to the type specified in the description.
	 * 
	 * @param desc
	 *            The data description.
	 * @param index
	 *            The index to format the data at.
	 * @return The formatted data.
	 */
	private double formatData(final TemporalDataDescription desc,
			final int index) {
		double result = 0;

		switch (desc.getType()) {
		case DELTA_CHANGE:
			result = getDataDeltaChange(desc, index);
			break;
		case PERCENT_CHANGE:
			result = getDataPercentChange(desc, index);
			break;
		case RAW:
			result = getDataRAW(desc, index);
			break;
		default:
			throw new TemporalError("Unsupported data type.");
		}

		if (desc.getActivationFunction() != null) {
			result = desc.getActivationFunction().activationFunction(result);
		}

		return result;
	}

	/**
	 * Generate the training sets.
	 */
	public void generate() {
		sortPoints();
		final int start = calculateStartIndex() + 1;
		final int setSize = calculateActualSetSize();
		final int range = start + setSize - this.predictWindowSize
				- this.inputWindowSize;

		for (int i = start; i < range; i++) {
			final BasicNeuralData input = generateInputNeuralData(i);
			final BasicNeuralData ideal = generateOutputNeuralData(i
					+ this.inputWindowSize);
			final BasicNeuralDataPair pair = new BasicNeuralDataPair(input,
					ideal);
			super.add(pair);
		}
	}

	/**
	 * Generate input neural data for the specified index.
	 * 
	 * @param index
	 *            The index to generate neural data for.
	 * @return The input neural data generated.
	 */
	public BasicNeuralData generateInputNeuralData(final int index) {
		if (index + this.inputWindowSize > this.points.size()) {
			throw new TemporalError("Can't generate input temporal data "
					+ "beyond the end of provided data.");
		}

		final BasicNeuralData result = new BasicNeuralData(
				this.inputNeuronCount);
		int resultIndex = 0;

		for (int i = 0; i < this.inputWindowSize; i++) {
			int descriptionIndex = 0;

			for (final TemporalDataDescription desc : this.descriptions) {
				if (desc.isInput()) {
					result.setData(resultIndex++, formatData(desc, index + i));
				}
				descriptionIndex++;
			}
		}
		return result;
	}

	/**
	 * Generate neural ideal data for the specified index.
	 * 
	 * @param index
	 *            The index to generate for.
	 * @return The neural data generated.
	 */
	public BasicNeuralData generateOutputNeuralData(final int index) {
		if (index + this.predictWindowSize > this.points.size()) {
			throw new TemporalError("Can't generate prediction temporal data "
					+ "beyond the end of provided data.");
		}

		final BasicNeuralData result = new BasicNeuralData(
				this.outputNeuronCount);
		int resultIndex = 0;

		for (int i = 0; i < this.predictWindowSize; i++) {
			int descriptionIndex = 0;

			for (final TemporalDataDescription desc : this.descriptions) {
				if (desc.isPredict()) {
					result.setData(resultIndex++, formatData(desc, index + i));
				}
				descriptionIndex++;
			}

		}
		return result;
	}

	/**
	 * Get data between two points in delta form.
	 * 
	 * @param desc
	 *            The data description.
	 * @param index
	 *            The index to get data from.
	 * @return The requested data.
	 */
	private double getDataDeltaChange(final TemporalDataDescription desc,
			final int index) {
		if (index == 0) {
			return 0.0;
		}
		final TemporalPoint point = this.points.get(index);
		final TemporalPoint previousPoint = this.points.get(index - 1);
		return point.getData(desc.getIndex())
				- previousPoint.getData(desc.getIndex());
	}

	/**
	 * Get data between two points in percent form.
	 * 
	 * @param desc
	 *            The data description.
	 * @param index
	 *            The index to get data from.
	 * @return The requested data.
	 */
	private double getDataPercentChange(final TemporalDataDescription desc,
			final int index) {
		if (index == 0) {
			return 0.0;
		}
		final TemporalPoint point = this.points.get(index);
		final TemporalPoint previousPoint = this.points.get(index - 1);
		final double currentValue = point.getData(desc.getIndex());
		final double previousValue = previousPoint.getData(desc.getIndex());
		return (currentValue - previousValue) / previousValue;
	}

	/**
	 * Get data between two points in raw form.
	 * 
	 * @param desc
	 *            The data description.
	 * @param index
	 *            The index to get data from.
	 * @return The requested data.
	 */
	private double getDataRAW(final TemporalDataDescription desc,
			final int index) {
		final TemporalPoint point = this.points.get(index);
		return point.getData(desc.getIndex());
	}

	/**
	 * @return A list of the data descriptions.
	 */
	public List<TemporalDataDescription> getDescriptions() {
		return this.descriptions;
	}

	/**
	 * @return the desiredSetSize
	 */
	public int getDesiredSetSize() {
		return this.desiredSetSize;
	}

	/**
	 * @return the highSequence
	 */
	public int getHighSequence() {
		return this.highSequence;
	}

	/**
	 * @return the inputNeuronCount
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * @return the inputWindowSize
	 */
	public int getInputWindowSize() {
		return this.inputWindowSize;
	}

	/**
	 * @return the lowSequence
	 */
	public int getLowSequence() {
		return this.lowSequence;
	}

	/**
	 * @return the outputNeuronCount
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * @return The temporal points.
	 */
	public List<TemporalPoint> getPoints() {
		return this.points;
	}

	/**
	 * @return the predictWindowSize
	 */
	public int getPredictWindowSize() {
		return this.predictWindowSize;
	}

	/**
	 * Create a sequence number from a time. The first date will be zero, and
	 * subsequent dates will be increased according to the grandularity
	 * specified.
	 * 
	 * @param when
	 *            The date to generate the sequence number for.
	 * @return A sequence number.
	 */
	public int getSequenceFromDate(final Date when) {
		int sequence;

		if (this.startingPoint != null) {
			final TimeSpan span = new TimeSpan(this.startingPoint, when);
			sequence = (int) span.getSpan(this.sequenceGrandularity);
		} else {
			this.startingPoint = when;
			sequence = 0;
		}

		return sequence;
	}

	/**
	 * @return the sequenceGrandularity
	 */
	public TimeUnit getSequenceGrandularity() {
		return this.sequenceGrandularity;
	}

	/**
	 * @return the startingPoint
	 */
	public Date getStartingPoint() {
		return this.startingPoint;
	}

	/**
	 * Is the specified point within the range. If a point is in the selection
	 * range, then the point will be used to generate the training sets.
	 * 
	 * @param point
	 *            The point to consider.
	 * @return True if the point is within the range.
	 */
	public boolean isPointInRange(final TemporalPoint point) {
		return point.getSequence() >= getLowSequence()
				&& point.getSequence() <= getHighSequence();

	}

	/**
	 * @param desiredSetSize
	 *            the desiredSetSize to set
	 */
	public void setDesiredSetSize(final int desiredSetSize) {
		this.desiredSetSize = desiredSetSize;
	}

	/**
	 * @param highSequence
	 *            the highSequence to set
	 */
	public void setHighSequence(final int highSequence) {
		this.highSequence = highSequence;
	}

	/**
	 * @param inputWindowSize
	 *            the inputWindowSize to set
	 */
	public void setInputWindowSize(final int inputWindowSize) {
		this.inputWindowSize = inputWindowSize;
	}

	/**
	 * @param lowSequence
	 *            the lowSequence to set
	 */
	public void setLowSequence(final int lowSequence) {
		this.lowSequence = lowSequence;
	}

	/**
	 * @param predictWindowSize
	 *            the predictWindowSize to set
	 */
	public void setPredictWindowSize(final int predictWindowSize) {
		this.predictWindowSize = predictWindowSize;
	}

	/**
	 * @param sequenceGrandularity
	 *            the sequenceGrandularity to set
	 */
	public void setSequenceGrandularity(final TimeUnit sequenceGrandularity) {
		this.sequenceGrandularity = sequenceGrandularity;
	}

	/**
	 * @param startingPoint
	 *            the startingPoint to set
	 */
	public void setStartingPoint(final Date startingPoint) {
		this.startingPoint = startingPoint;
	}

	/**
	 * Sort the points.
	 */
	public void sortPoints() {
		Collections.sort(this.points);
	}

}
