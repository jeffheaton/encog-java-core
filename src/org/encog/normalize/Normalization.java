/**
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.normalize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.encog.StatusReportable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.normalize.input.HasFixedLength;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.input.InputFieldNeuralDataSet;
import org.encog.normalize.input.NeuralDataFieldHolder;
import org.encog.normalize.output.OutputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;
import org.encog.normalize.segregate.Segregator;
import org.encog.normalize.target.NormalizationStorage;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.persistors.NormalizationPersistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class Normalization implements EncogPersistedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4387885013771660300L;
	
	private final Collection<InputField> inputFields = new ArrayList<InputField>();
	private final Collection<OutputField> outputFields = new ArrayList<OutputField>();
	
	@EGIgnore
	private final Collection<ReadCSV> readCSV = new ArrayList<ReadCSV>();
	
	@EGIgnore
	private final Map<InputField, ReadCSV> csvMap = new HashMap<InputField, ReadCSV>();

	private final Collection<Iterator<NeuralDataPair>> readDataSet = new ArrayList<Iterator<NeuralDataPair>>();
	
	@EGIgnore
	private final Map<InputField, NeuralDataFieldHolder> dataSetFieldMap = new HashMap<InputField, NeuralDataFieldHolder>();
	
	@EGIgnore
	private final Map<Iterator<NeuralDataPair>, NeuralDataFieldHolder> dataSetIteratorMap = new HashMap<Iterator<NeuralDataPair>, NeuralDataFieldHolder>();
	private final Set<OutputFieldGroup> groups = new HashSet<OutputFieldGroup>();
	private final Collection<Segregator> segregators = new ArrayList<Segregator>();
	
	@EGIgnore
	private NormalizationStorage storage;
	
	@EGIgnore
	private StatusReportable report;
	
	private int recordCount;
	private int currentIndex;
	
	@EGIgnore
	private CSVFormat csvFormat = CSVFormat.ENGLISH;
	private int lastReport;
	private String name;
	private String description;

	public void addInputField(final InputField f) {
		this.inputFields.add(f);
	}

	public void addOutputField(final OutputField outputField) {
		addOutputField(outputField, false);
	}

	public void addOutputField(final OutputField outputField,
			final boolean ideal) {
		this.outputFields.add(outputField);
		outputField.setIdeal(ideal);
		if (outputField instanceof OutputFieldGrouped) {
			final OutputFieldGrouped ofg = (OutputFieldGrouped) outputField;
			this.groups.add(ofg.getGroup());
		}
	}

	public void addSegregator(final Segregator segregator) {
		this.segregators.add(segregator);
		segregator.init(this);
	}

	private void applyMinMax() {
		for (final InputField field : this.inputFields) {
			final double value = field.getCurrentValue();
			field.applyMinMax(value);
		}
	}

	public Persistor createPersistor() {
		return new NormalizationPersistor();
	}

	private double determineInputFieldValue(final InputField field,
			final int index) {
		double result = 0;

		if (field instanceof InputFieldCSV) {
			final InputFieldCSV fieldCSV = (InputFieldCSV) field;
			final ReadCSV csv = this.csvMap.get(field);
			result = csv.getDouble(fieldCSV.getOffset());
		} else if (field instanceof InputFieldNeuralDataSet) {
			final InputFieldNeuralDataSet neuralField = (InputFieldNeuralDataSet) field;
			final NeuralDataFieldHolder holder = this.dataSetFieldMap
					.get(field);
			final NeuralDataPair pair = holder.getPair();
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

	private void determineInputFieldValues(final int index) {
		for (final InputField field : this.inputFields) {
			determineInputFieldValue(field, index);
		}
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

	public CSVFormat getCSVFormat() {
		return this.csvFormat;
	}

	public String getDescription() {
		return this.description;
	}

	public Set<OutputFieldGroup> getGroups() {
		return this.groups;
	}

	public Collection<InputField> getInputFields() {
		return this.inputFields;
	}

	public String getName() {
		return this.name;
	}

	public int getNetworkInputLayerSize() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			if (!field.isIdeal()) {
				result += field.getSubfieldCount();
			}
		}
		return result;
	}

	public int getNetworkOutputLayerSize() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			if (field.isIdeal()) {
				result += field.getSubfieldCount();
			}
		}
		return result;
	}

	public int getOutputFieldCount() {
		int result = 0;
		for (final OutputField field : this.outputFields) {
			result += field.getSubfieldCount();
		}
		return result;
	}

	public Collection<OutputField> getOutputFields() {
		return this.outputFields;
	}

	public int getRecordCount() {
		return this.recordCount;
	}

	public StatusReportable getReport() {
		return this.report;
	}

	public Collection<Segregator> getSegregators() {
		return this.segregators;
	}

	public NormalizationStorage getStorage() {
		return this.storage;
	}

	private void initForOutput() {

		// init groups
		for (final OutputFieldGroup group : this.groups) {
			group.rowInit();
		}

		// init output fields
		for (final OutputField field : this.outputFields) {
			field.rowInit();
		}
	}

	private boolean next() {

		// see if any of the CSV readers want to stop
		for (final ReadCSV csv : this.readCSV) {
			if (!csv.next()) {
				return false;
			}
		}

		// see if any of the data sets want to stop
		for (final Iterator<NeuralDataPair> iterator : this.readDataSet) {
			if (!iterator.hasNext()) {
				return false;
			}
			final NeuralDataFieldHolder holder = this.dataSetIteratorMap
					.get(iterator);
			final NeuralDataPair pair = iterator.next();
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

	private void openDataSet() {
		// clear out any data sets already there
		this.readDataSet.clear();
		this.dataSetFieldMap.clear();
		this.dataSetIteratorMap.clear();

		// only add each iterator once
		final Map<NeuralDataSet, NeuralDataFieldHolder> uniqueSets = new HashMap<NeuralDataSet, NeuralDataFieldHolder>();

		// find the unique files
		for (final InputField field : this.inputFields) {
			if (field instanceof InputFieldNeuralDataSet) {
				final InputFieldNeuralDataSet dataSetField = (InputFieldNeuralDataSet) field;
				final NeuralDataSet dataSet = dataSetField.getNeuralDataSet();
				if (!uniqueSets.containsKey(dataSet)) {
					final Iterator<NeuralDataPair> iterator = dataSet
							.iterator();
					final NeuralDataFieldHolder holder = new NeuralDataFieldHolder(
							iterator, dataSetField);
					uniqueSets.put(dataSet, holder);
					this.readDataSet.add(iterator);
				}

				final NeuralDataFieldHolder holder = uniqueSets.get(dataSet);

				this.dataSetFieldMap.put(dataSetField, holder);
				this.dataSetIteratorMap.put(holder.getIterator(), holder);
			}
		}
	}

	public void process() {
		firstPass();
		secondPass();
	}

	private void reportResult(final String message, final int total,
			final int current) {
		// count the records, report status
		this.lastReport++;
		if (this.lastReport >= 10000) {
			this.report.report(total, current, message);
			this.lastReport = 0;
		}
	}

	private void secondPass() {
		// move any CSV and datasets files back to the beginning.
		openCSV();
		openDataSet();

		this.currentIndex = -1;

		// process the records
		final int size = getOutputFieldCount();
		final double[] output = new double[size];

		this.storage.open();
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

				reportResult("Second pass, normalizing data", this.recordCount,
						++current);
				this.storage.write(output, 0);
			}

			index++;
		}
		this.storage.close();

	}

	public void setCSVFormat(final CSVFormat csvFormat) {
		this.csvFormat = csvFormat;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setReport(final StatusReportable report) {
		this.report = report;
	}

	public void setTarget(final NormalizationStorage target) {
		this.storage = target;
	}

	private boolean shouldInclude() {
		for (final Segregator segregator : this.segregators) {
			if (!segregator.shouldInclude()) {
				return false;
			}
		}
		return true;
	}

}
