package org.encog.normalize.segregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.normalize.DataNormalization;
import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReference;

/**
 * Balance based on an input value. This allows you to make sure that one input
 * class does not saturate the training data. To do this, you specify the input
 * value to check and the number of occurrences of each integer value of this
 * field to allow.
 */
public class IntegerBalanceSegregator implements Segregator {

	/**
	 * The normalization object to use.
	 */
	@EGReference
	private DataNormalization normalization;

	/**
	 * The input field.
	 */
	@EGReference
	private InputField target;

	/**
	 * The count per each of the int values for the input field.
	 */
	private int count;

	/**
	 * The running totals.
	 */
	@EGIgnore
	private final Map<Integer, Integer> runningCounts = new HashMap<Integer, Integer>();

	public IntegerBalanceSegregator(final InputField target, final int count) {
		this.target = target;
		this.count = count;
	}

	public IntegerBalanceSegregator() {

	}

	/**
	 * @return A string that contains the counts for each group.
	 */
	public String dumpCounts() {
		final StringBuilder result = new StringBuilder();

		for (final Entry<Integer, Integer> entry : this.runningCounts
				.entrySet()) {
			result.append(entry.getKey());
			result.append(" -> ");
			result.append(entry.getValue());
			result.append(" count\n");
		}
		return result.toString();
	}

	public int getCount() {
		return this.count;
	}

	public DataNormalization getNormalization() {
		return this.normalization;
	}

	public Map<Integer, Integer> getRunningCounts() {
		return this.runningCounts;
	}

	public InputField getTarget() {
		return this.target;
	}

	/**
	 * Init the segregator with the owning normalization object.
	 * 
	 * @param normalization
	 *            The data normalization object to use.
	 */
	public void init(final DataNormalization normalization) {
		this.normalization = normalization;

	}

	/**
	 * Init for a new pass.
	 */
	public void passInit() {
		this.runningCounts.clear();
	}

	/**
	 * Determine of the current row should be included.
	 * 
	 * @return True if the current row should be included.
	 */
	public boolean shouldInclude() {
		final int key = (int) this.target.getCurrentValue();
		int value = 0;
		if (this.runningCounts.containsKey(key)) {
			value = this.runningCounts.get(key);
		}

		if (value < this.count) {
			value++;
			this.runningCounts.put(key, value);
			return true;
		} else {
			return false;
		}
	}

}
