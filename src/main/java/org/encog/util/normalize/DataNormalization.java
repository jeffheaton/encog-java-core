/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.normalize;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.normalize.input.HasFixedLength;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.input.InputFieldMLDataSet;
import org.encog.util.normalize.input.MLDataFieldHolder;
import org.encog.util.normalize.output.OutputField;
import org.encog.util.normalize.output.OutputFieldGroup;
import org.encog.util.normalize.output.OutputFieldGrouped;
import org.encog.util.normalize.output.RequireTwoPass;
import org.encog.util.normalize.segregate.Segregator;
import org.encog.util.normalize.target.NormalizationStorage;
import org.encog.util.obj.ReflectionUtil;

/**
 * This class is used to normalize both input and ideal data for neural
 * networks. This class can accept input from a variety of sources and output to
 * a variety of targets. Normalization is a process by which input data is
 * normalized so that it falls in specific ranges. Neural networks typically
 * require input to be in the range of 0 to 1, or -1 to 1, depending on how the
 * network is structured.
 * 
 * The normalize class is typically given for different types of objects to tell
 * it how to process data.
 * 
 * Input Fields:
 * 
 * Input fields specify the raw data that will be read by the Normalize class.
 * Input fields are added to the Normalize class by calling addInputField
 * method. Input fields must implement the InputField interface. There are a
 * number of different input fields provided. Input data can be read from
 * several different sources. For example, you can read the "neural network
 * input" data from one CSV file and the "ideal neural network output" from
 * another.
 * 
 * 
 * Output Fields:
 * 
 * The output fields are used to specify the final output from the Normalize
 * class. The output fields specify both the "neural network input" and "ideal
 * output". The output fields are flagged as either input our ideal. The output
 * fields are not necessarily one-to-one with the input fields. For example,
 * several input fields may combine to produce a single output field. Further
 * some input fields may be used only to segregate data, whereas other input
 * fields may be ignored all together. The type of output field that you specify
 * determines the type of processing that will be done on that field. An
 * OutputField is added by calling the addOutputField method.
 * 
 * 
 * Segregators:
 * 
 * Segregators are used generally for two related purposes. First, segregators
 * can be used to exclude rows of data based on certain input values. Perhaps
 * the data includes several classes of data, and you only want to train on one
 * class. Secondly, segregators can be used to segregate data into training and
 * evaluation sets. You may choose to use 80% of your data for training and 20%
 * for evaluation. A segregator is added by calling the addSegregator method.
 * 
 * 
 * Target Storage:
 * 
 * The data created by the Normalization class must be stored somewhere. The
 * storage targets allow this to be specified. The output can be sent to a CSV
 * file, a NeuralDataSet, or any other target supported by a
 * NormalizationStorage derived class. The target is specified by calling the
 * setTarget method.
 * 
 * The normalization process can take some time. The progress can be reported to
 * a StatusReportable object.
 * 
 * The normalization is a two pass process. The first pass counts the number of
 * records and computes important statistics that will be used to normalize the
 * output. The second pass actually performs the normalization and writes to the
 * target. Both passes are performed when the process method is called.
 * 
 */
public class DataNormalization implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 4387885013771660300L;

	/**
	 * The input fields.
	 */
	private final List<InputField> inputFields = 
		new ArrayList<InputField>();

	/**
	 * The output fields.
	 */
	private final List<OutputField> outputFields = 
		new ArrayList<OutputField>();

	/**
	 * Keep a collection of all of the ReadCSV classes to support all of the
	 * distinct CSV files that are to be read.
	 */
	private transient Collection<ReadCSV> readCSV;
		
	/**
	 * Hold a map between the InputFieldCSV objects and the corresponding
	 * ReadCSV object. There will likely be many fields read from a single file.
	 * This allows only one ReadCSV object to need to be created per actual CSV
	 * file.
	 */
	private transient Map<InputField, ReadCSV> csvMap;

	/**
	 * For each InputFieldNeuralDataSet input field an Iterator must be kept to
	 * actually access the data. Only one Iterator should be kept per data set
	 * actually used.
	 */
	private transient Collection<Iterator<MLDataPair>> readDataSet;

	/**
	 * Map each of the input fields to an internally-build NeuralDataFieldHolder
	 * object. The NeuralDataFieldHolder object holds an Iterator, InputField
	 * and last NeuralDataPair object loaded.
	 */
	private transient Map<InputField, MLDataFieldHolder> dataSetFieldMap;

	/**
	 * Map each of the NeuralDataSet Iterators to an internally-build
	 * MLDataFieldHolder object. The MLDataFieldHolder object holds an
	 * Iterator, InputField and last MLDataPair object loaded.
	 */
	private transient Map<Iterator<MLDataPair>, 
		MLDataFieldHolder> dataSetIteratorMap;

	/**
	 * Output fields can be grouped together, if the value of one output field
	 * might affect all of the others. This collection holds a list of all of
	 * the output field groups.
	 */
	private final Set<OutputFieldGroup> groups = 
		new HashSet<OutputFieldGroup>();

	/**
	 * A list of the segregators.
	 */
	private final List<Segregator> segregators = 
		new ArrayList<Segregator>();

	/**
	 * Where the final output from the normalization is sent.
	 */
	private NormalizationStorage storage;

	/**
	 * The object to report the progress of the normalization to.
	 */
	private transient StatusReportable report = new NullStatusReportable();

	/**
	 * The number of records that were found in the first pass.
	 */
	private int recordCount;

	/**
	 * The current record's index.
	 */
	private int currentIndex;

	/**
	 * The format to use for all CSV files.
	 */
	private CSVFormat csvFormat = CSVFormat.ENGLISH;

	/**
	 * How long has it been since the last report. This filters so that every
	 * single record does not produce a message.
	 */
	private int lastReport;

	/**
	 * Add an input field.
	 * 
	 * @param f
	 *            The input field to add.
	 */
	public void addInputField(final InputField f) {
		this.inputFields.add(f);
	}

	/**
	 * Add an output field. This output field will be added as a "ML network
	 * input field", not an "ideal output field".
	 * 
	 * @param outputField
	 *            The output field to add.
	 */
	public void addOutputField(final OutputField outputField) {
		addOutputField(outputField, false);
	}

	/**
	 * Add a field and allow it to be specified as an "ideal output field". An
	 * "ideal" field is the expected output that the ML network is training
	 * towards.
	 * 
	 * @param outputField
	 *            The output field.
	 * @param ideal
	 *            True if this is an ideal field.
	 */
	public void addOutputField(final OutputField outputField,
			final boolean ideal) {
		this.outputFields.add(outputField);
		outputField.setIdeal(ideal);
		if (outputField instanceof OutputFieldGrouped) {
			final OutputFieldGrouped ofg = (OutputFieldGrouped) outputField;
			this.groups.add(ofg.getGroup());
		}
	}

	/**
	 * Add a segregator.
	 * 
	 * @param segregator
	 *            The segregator to add.
	 */
	public void addSegregator(final Segregator segregator) {
		this.segregators.add(segregator);
		segregator.init(this);
	}

	/**
	 * Called internally to allow each of the input fields to update their
	 * min/max values in the first pass.
	 */
	private void applyMinMax() {
		for (final InputField field : this.inputFields) {
			final double value = field.getCurrentValue();
			field.applyMinMax(value);
		}
	}

	/**
	 * Build "input data for a neural network" based on the input values
	 * provided. This allows input for a neural network to be normalized. This
	 * is typically used when data is to be presented to a trained neural
	 * network.
	 * 
	 * @param data
	 *            The input values to be normalized.
	 * @return The data to be sent to the neural network.
	 */
	public MLData buildForNetworkInput(final double[] data) {

		// feed the input fields
		int index = 0;
		for (final InputField field : this.inputFields) {
			if (field.getUsedForNetworkInput()) {
				if (index >= data.length) {
					throw new NormalizationError(
							"Can't build data, input fields used for neural input, must match provided data("
									+ data.length + ").");
				}
				field.setCurrentValue(data[index++]);
			}
		}

		// count the output fields
		int outputCount = 0;
		for (final OutputField ofield : this.outputFields) {
			if (!ofield.isIdeal()) {
				for (int sub = 0; sub < ofield.getSubfieldCount(); sub++) {
					outputCount++;
				}
			}
		}

		// process the output fields

		initForOutput();

		final MLData result = new BasicMLData(outputCount);

		// write the value
		int outputIndex = 0;
		for (final OutputField ofield : this.outputFields) {
			if (!ofield.isIdeal()) {
				for (int sub = 0; sub < ofield.getSubfieldCount(); sub++) {
					result.setData(outputIndex++, ofield.calculate(sub));
				}
			}
		}

		return result;
	}

	/**
	 * Called internally to obtain the current value for an input field.
	 * 
	 * @param field
	 *            The input field to determine.
	 * @param index
	 *            The current index.
	 * @return The value for this input field.
	 */
	private double determineInputFieldValue(final InputField field,
			final int index) {
		double result = 0;

		if (field instanceof InputFieldCSV) {
			final InputFieldCSV fieldCSV = (InputFieldCSV) field;
			final ReadCSV csv = this.csvMap.get(field);
			result = csv.getDouble(fieldCSV.getOffset());
		} else if (field instanceof InputFieldMLDataSet) {
			final InputFieldMLDataSet neuralField = 
				(InputFieldMLDataSet) field;
			final MLDataFieldHolder holder = this.dataSetFieldMap
					.get(field);
			final MLDataPair pair = holder.getPair();
			int offset = neuralField.getOffset();
			if (offset < pair.getInput().size()) {
				result = pair.getInput().getData(offset);
			} else {
				offset -= pair.getInput().size();
				result = pair.getIdeal().getData(offset);
			}
		} else {
			result = field.getValue(index);
		}

		field.setCurrentValue(result);
		return result;
	}

	/**
	 * Called internally to determine all of the input field values.
	 * 
	 * @param index
	 *            The current index.
	 */
	private void determineInputFieldValues(final int index) {
		for (final InputField field : this.inputFields) {
			determineInputFieldValue(field, index);
		}
	}

	/**
	 * Find an input field by its class.
	 * 
	 * @param clazz
	 *            The input field class type you are looking for.
	 * @param count
	 *            The instance of the input field needed, 0 for the first.
	 * @return The input field if found, otherwise null.
	 */
	public InputField findInputField(final Class< ? > clazz, final int count) {
		int i = 0;
		for (final InputField field : this.inputFields) {
			if (ReflectionUtil.isInstanceOf(field.getClass(), clazz)) {
				if (i == count) {
					return field;
				}
				i++;
			}
		}

		return null;
	}

	/**
	 * Find an output field by its class.
	 * 
	 * @param clazz
	 *            The output field class type you are looking for.
	 * @param count
	 *            The instance of the output field needed, 0 for the first.
	 * @return The output field if found, otherwise null.
	 */
	public OutputField findOutputField(final Class< ? > clazz, 
			final int count) {
		int i = 0;
		for (final OutputField field : this.outputFields) {
			if (ReflectionUtil.isInstanceOf(field.getClass(), clazz)) {
				if (i == count) {
					return field;
				}
				i++;
			}
		}

		return null;
	}

	/**
	 * First pass, count everything, establish min/max.
	 */
	private void firstPass() {
		openCSV();
		openDataSet();

		this.currentIndex = -1;
		this.recordCount = 0;

		this.report.report(0, 0, "Analyzing file");
		this.lastReport = 0;
		int index = 0;

		initForPass();

		// loop over all of the records
		while (next()) {

			determineInputFieldValues(index);

			if (shouldInclude()) {
				applyMinMax();
				this.recordCount++;
				reportResult("First pass, analyzing file", 0, this.recordCount);
			}
			index++;
		}
	}

	/**
	 * @return The CSV format being used.
	 */
	public CSVFormat getCSVFormat() {
		return this.csvFormat;
	}

	/**
	 * @return The object groups.
	 */
	public Set<OutputFieldGroup> getGroups() {
		return this.groups;
	}

	/**
	 * @return The input fields.
	 */
	public List<InputField> getInputFields() {
		return this.inputFields;
	}

	/**
	 * @return The number of output fields that are not used as ideal values,
	 *         these will be the input to the neural network. This is the input
	 *         layer size for the neural network.
	 */
	public int getNetworkInputLayerSize() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			if (!field.isIdeal()) {
				result += field.getSubfieldCount();
			}
		}
		return result;
	}

	/**
	 * @return The number of output fields that are used as ideal values, these
	 *         will be the ideal output from the neural network. This is the
	 *         output layer size for the neural network.
	 */
	public int getNetworkOutputLayerSize() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			if (field.isIdeal()) {
				result += field.getSubfieldCount();
			}
		}
		return result;
	}

	/**
	 * @return The total size of all output fields. This takes into account
	 *         output fields that generate more than one value.
	 */
	public int getOutputFieldCount() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			result += field.getSubfieldCount();
		}
		return result;
	}

	/**
	 * @return The output fields.
	 */
	public List<OutputField> getOutputFields() {
		return this.outputFields;
	}

	/**
	 * @return The record count.
	 */
	public int getRecordCount() {
		return this.recordCount;
	}

	/**
	 * @return The class that progress will be reported to.
	 */
	public StatusReportable getReport() {
		return this.report;
	}

	/**
	 * @return The segregators in use.
	 */
	public List<Segregator> getSegregators() {
		return this.segregators;
	}

	/**
	 * @return The place that the normalization output will be stored.
	 */
	public NormalizationStorage getStorage() {
		return this.storage;
	}

	/**
	 * Setup the row for output.
	 */
	public void initForOutput() {

		// init groups
		for (final OutputFieldGroup group : this.groups) {
			group.rowInit();
		}

		// init output fields
		for (final OutputField field : this.outputFields) {
			field.rowInit();
		}
	}

	/**
	 * Setup the row for output.
	 */
	public void initForPass() {

		// init segregators
		for (final Segregator segregator : this.segregators) {
			segregator.passInit();
		}
	}

	/**
	 * Called internally to advance to the next row.
	 * 
	 * @return True if there are more rows to reed.
	 */
	private boolean next() {

		// see if any of the CSV readers want to stop
		for (final ReadCSV csv : this.readCSV) {
			if (!csv.next()) {
				return false;
			}
		}

		// see if any of the data sets want to stop
		for (final Iterator<MLDataPair> iterator : this.readDataSet) {
			if (!iterator.hasNext()) {
				return false;
			}
			final MLDataFieldHolder holder = this.dataSetIteratorMap
					.get(iterator);
			final MLDataPair pair = iterator.next();
			holder.setPair(pair);
		}

		// see if any of the arrays want to stop
		for (final InputField field : this.inputFields) {
			if (field instanceof HasFixedLength) {
				final HasFixedLength fixed = (HasFixedLength) field;
				if ((this.currentIndex + 1) >= fixed.length()) {
					return false;
				}
			}
		}

		this.currentIndex++;

		return true;
	}

	/**
	 * Called internally to open the CSV file.
	 */
	private void openCSV() {
		// clear out any CSV files already there
		this.csvMap.clear();
		this.readCSV.clear();

		// only add each CSV once
		final Map<File, ReadCSV> uniqueFiles = new HashMap<File, ReadCSV>();

		// find the unique files
		for (final InputField field : this.inputFields) {
			if (field instanceof InputFieldCSV) {
				final InputFieldCSV csvField = (InputFieldCSV) field;
				final File file = csvField.getFile();
				if (!uniqueFiles.containsKey(file)) {
					final ReadCSV csv = new ReadCSV(file.toString(), false,
							this.csvFormat);
					uniqueFiles.put(file, csv);
					this.readCSV.add(csv);
				}
				this.csvMap.put(csvField, uniqueFiles.get(file));
			}
		}
	}

	/**
	 * Open any datasets that were used by the input layer.
	 */
	private void openDataSet() {
		// clear out any data sets already there
		this.readDataSet.clear();
		this.dataSetFieldMap.clear();
		this.dataSetIteratorMap.clear();

		// only add each iterator once
		final Map<MLDataSet, MLDataFieldHolder> uniqueSets = 
			new HashMap<MLDataSet, MLDataFieldHolder>();

		// find the unique files
		for (final InputField field : this.inputFields) {
			if (field instanceof InputFieldMLDataSet) {
				final InputFieldMLDataSet dataSetField = 
					(InputFieldMLDataSet) field;
				final MLDataSet dataSet = dataSetField.getNeuralDataSet();
				if (!uniqueSets.containsKey(dataSet)) {
					final Iterator<MLDataPair> iterator = dataSet
							.iterator();
					final MLDataFieldHolder holder = 
						new MLDataFieldHolder(
							iterator, dataSetField);
					uniqueSets.put(dataSet, holder);
					this.readDataSet.add(iterator);
				}

				final MLDataFieldHolder holder = uniqueSets.get(dataSet);

				this.dataSetFieldMap.put(dataSetField, holder);
				this.dataSetIteratorMap.put(holder.getIterator(), holder);
			}
		}
	}
	
	public void init() {
		this.readCSV = new ArrayList<ReadCSV>();
			
		this.csvMap = 
			new HashMap<InputField, ReadCSV>();

		this.readDataSet = 
			new ArrayList<Iterator<MLDataPair>>();

		this.dataSetFieldMap = 
			new HashMap<InputField, MLDataFieldHolder>();

		this.dataSetIteratorMap = 
			new HashMap<Iterator<MLDataPair>, MLDataFieldHolder>();
		
		if( this.report==null ) {
			this.report = new NullStatusReportable();
		}
	}

	/**
	 * Call this method to begin the normalization process. Any status updates
	 * will be sent to the class specified in the constructor.
	 */
	public void process() {
		
		init();

		if (twoPassesNeeded()) {
			firstPass();
		}

		secondPass();
	}

	/**
	 * Report on the current progress.
	 * 
	 * @param message
	 *            The message to report.
	 * @param total
	 *            The total number of records to process, 0 for unknown.
	 * @param current
	 *            The current record.
	 */
	private void reportResult(final String message, final int total,
			final int current) {
		// count the records, report status
		this.lastReport++;
		if (this.lastReport >= 10000) {
			this.report.report(total, current, message);
			this.lastReport = 0;
		}
	}

	/**
	 * The second pass actually writes the data to the output files.
	 */
	private void secondPass() {

		final boolean twopass = twoPassesNeeded();

		// move any CSV and datasets files back to the beginning.
		openCSV();
		openDataSet();
		initForPass();

		this.currentIndex = -1;

		// process the records
		final int size = getOutputFieldCount();
		final double[] output = new double[size];
		
		if( storage==null ) {
			throw new NormalizationError("Must define storage target.");
		}

		this.storage.open(this);
		this.lastReport = 0;
		int index = 0;
		int current = 0;
		while (next()) {
			// read the value
			for (final InputField field : this.inputFields) {
				determineInputFieldValue(field, index);
			}

			if (shouldInclude()) {
				// handle groups
				initForOutput();

				// write the value
				int outputIndex = 0;
				for (final OutputField ofield : this.outputFields) {
					for (int sub = 0; sub < ofield.getSubfieldCount(); sub++) {
						output[outputIndex++] = ofield.calculate(sub);
					}
				}

				if (twopass) {
					reportResult("Second pass, normalizing data",
							this.recordCount, ++current);
				} else {
					reportResult("Processing data (single pass)",
							this.recordCount, ++current);
				}

				this.storage.write(output, 0);
			}

			index++;
		}
		this.storage.close();

	}

	/**
	 * Set the CSV format to use.
	 * 
	 * @param csvFormat
	 *            The CSV format to use.
	 */
	public void setCSVFormat(final CSVFormat csvFormat) {
		this.csvFormat = csvFormat;
	}

	/**
	 * Set the object that this one is reporting to.
	 * 
	 * @param report
	 *            The object that progress reports should be sent to.
	 */
	public void setReport(final StatusReportable report) {
		this.report = report;
	}

	/**
	 * Determines where the normalized data will be sent.
	 * 
	 * @param target
	 *            The target.
	 */
	public void setTarget(final NormalizationStorage target) {
		this.storage = target;
	}

	/**
	 * Should this row be included? Check the segregators.
	 * 
	 * @return True if the row should be included.
	 */
	private boolean shouldInclude() {
		
		// If no segregators, then include
		if( this.segregators.size()==0 )
			return true;
		
		// include if one segregator says to include
		boolean included = false;
		for (final Segregator segregator : this.segregators) {

			if (segregator.shouldInclude()) {
				included = true;
			}
		}
		return included;
	}

	/**
	 * @return True, if two passes are needed.
	 */
	public boolean twoPassesNeeded() {
		for (final OutputField field : this.outputFields) {
			if (field instanceof RequireTwoPass) {
				return true;
			}
		}

		return false;
	}
}
