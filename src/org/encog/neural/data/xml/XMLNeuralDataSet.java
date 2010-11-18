/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.data.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.parse.tags.read.ReadXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data source that reads XML files. This class is not memory based, so very
 * large XML files can be used, without problem.
 * 
 * The XML data is assumed to look something like below. The names of the tags
 * can be configured using the various properties on this object.
 * 
 * <DataSet> <pair> <input><value>0</value><value>0</value></input> <ideal><value>0</value></ideal>
 * </pair> <pair> <input><value>1</value><value>0</value></input> <ideal><value>1</value></ideal>
 * </pair> <pair> <input><value>0</value><value>1</value></input> <ideal><value>1</value></ideal>
 * </pair> <pair> <input><value>1</value><value>1</value></input> <ideal><value>0</value></ideal>
 * </pair> </DataSet>
 */
public class XMLNeuralDataSet implements NeuralDataSet {

	/**
	 * An iterator designed to read from XML files.
	 * 
	 * @author jheaton
	 */
	public class XMLNeuralIterator implements Iterator<NeuralDataPair> {

		/**
		 * The XML file being read.
		 */
		private InputStream file;

		/**
		 * A reader for the XML file.
		 */
		private ReadXML reader;

		/**
		 * The data pair just read.
		 */
		private NeuralDataPair nextPair;

		/**
		 * Construct an iterator to read the XML data.
		 */
		public XMLNeuralIterator() {
			try {
				this.file = new FileInputStream(getFilename());
				this.reader = new ReadXML(this.file);
			} catch (final IOException e) {
				if (XMLNeuralDataSet.this.logger.isErrorEnabled()) {
					XMLNeuralDataSet.this.logger.error("Exception", e);
				}
				throw new NeuralNetworkError(e);
			}
		}

		/**
		 * Is there any more data to read?
		 * 
		 * @return True if there is more data to read.
		 */
		public boolean hasNext() {
			if (this.nextPair != null) {
				return true;
			}

			return obtainNext();
		}

		/**
		 * Internal function called by several functions to display an error
		 * that indicates that the XML is not valid.
		 */
		private void invalidError() {
			final String str = "Could not parse XML, "
					+ "inconsistant tag structure.";
			if (XMLNeuralDataSet.this.logger.isErrorEnabled()) {
				XMLNeuralDataSet.this.logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}

		/**
		 * Read the next training set item.
		 * 
		 * @return The next training set item.
		 */
		public NeuralDataPair next() {
			NeuralDataPair result = this.nextPair;

			if (result == null) {
				if (!obtainNext()) {
					return null;
				}
				result = this.nextPair;
			}

			this.nextPair = null;
			return result;
		}

		/**
		 * Internal function to obtain the next training set item.
		 * 
		 * @return True if one was found.
		 */
		private boolean obtainNext() {
			if (!this.reader.findTag(getPairXML(), true)) {
				return false;
			}

			final NeuralData input = new BasicNeuralData(
					XMLNeuralDataSet.this.inputSize);
			final NeuralData ideal = new BasicNeuralData(
					XMLNeuralDataSet.this.idealSize);

			if (!this.reader.findTag(getInputXML(), true)) {
				invalidError();
			}

			for (int i = 0; i < XMLNeuralDataSet.this.inputSize; i++) {
				if (!this.reader.findTag(getValueXML(), true)) {
					invalidError();
				}
				final String str = this.reader.readTextToTag();
				input.setData(i, Double.parseDouble(str));
			}

			if (XMLNeuralDataSet.this.idealSize > 0) {
				if (!this.reader.findTag(getIdealXML(), true)) {
					invalidError();
				}

				for (int i = 0; i < XMLNeuralDataSet.this.idealSize; i++) {
					if (!this.reader.findTag(getValueXML(), true)) {
						invalidError();
					}
					final String str = this.reader.readTextToTag();
					ideal.setData(i, Double.parseDouble(str));
				}
			}

			if (ideal != null) {
				this.nextPair = new BasicNeuralDataPair(input, ideal);
			} else {
				this.nextPair = new BasicNeuralDataPair(input);
			}

			return true;
		}

		/**
		 * Remove this iterator.
		 */
		public void remove() {
			try {
				this.file.close();
				XMLNeuralDataSet.this.iterators.remove(this);
			} catch (final IOException e) {
				if (XMLNeuralDataSet.this.logger.isErrorEnabled()) {
					XMLNeuralDataSet.this.logger.error("Error", e);
				}
				throw new NeuralNetworkError(e);
			}

		}
	}

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5960796361565902008L;

	/**
	 * Error Message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = 
		"Adds are not supported with this dataset, it is read only.";

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The file name to read.
	 */
	private final String filename;

	/**
	 * The XML that indicates that a pair is about to start.
	 */
	private final String pairXML;

	/**
	 * The XML that indicates that input data is about to start.
	 */
	private final String inputXML;

	/**
	 * XMl that indicates that ideal data is about to start.
	 */
	private final String idealXML;

	/**
	 * XML that indicates that a numeric value is about to start.
	 */
	private final String valueXML;

	/**
	 * The input data size.
	 */
	private final int inputSize;

	/**
	 * The ideal data size.
	 */
	private final int idealSize;

	/**
	 * A collection of iterators that have been created.
	 */
	private final List<XMLNeuralIterator> iterators = 
		new ArrayList<XMLNeuralIterator>();

	/**
	 * Construct an XML neural data set.
	 * 
	 * @param filename
	 *            The filename to read.
	 * @param inputSize
	 *            The input size.
	 * @param idealSize
	 *            The ideal size. Zero for unsupervised.
	 * @param pairXML
	 *            The XML that starts a pair.
	 * @param inputXML
	 *            The XML that starts input.
	 * @param idealXML
	 *            The XML that starts ideal.
	 * @param valueXML
	 *            The XML that starts values.
	 */
	public XMLNeuralDataSet(final String filename, final int inputSize,
			final int idealSize, final String pairXML, final String inputXML,
			final String idealXML, final String valueXML) {
		this.filename = filename;
		this.pairXML = pairXML;
		this.inputXML = inputXML;
		this.idealXML = idealXML;
		this.valueXML = valueXML;
		this.idealSize = idealSize;
		this.inputSize = inputSize;
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * 
	 * @param data1
	 *            Not used.
	 */
	public void add(final NeuralData data1) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * 
	 * @param inputData
	 *            Not used.
	 * @param idealData
	 *            Not used.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * 
	 * @param inputData
	 *            Not used.
	 */
	public void add(final NeuralDataPair inputData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Close the XML data source.
	 */
	public void close() {
		for (int i = 0; i < this.iterators.size(); i++) {
			final XMLNeuralIterator iterator = this.iterators.get(i);
			iterator.remove();
		}
	}

	/**
	 * @return The XML filename.
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * @return The ideal size.
	 */
	public int getIdealSize() {
		return this.idealSize;
	}

	/**
	 * @return The XML tag for ideal.
	 */
	public String getIdealXML() {
		return this.idealXML;
	}

	/**
	 * @return The input size.
	 */
	public int getInputSize() {
		return this.inputSize;
	}

	/**
	 * @return The XML tag for input.
	 */
	public String getInputXML() {
		return this.inputXML;
	}

	/**
	 * @return The XML tag for pairs.
	 */
	public String getPairXML() {
		return this.pairXML;
	}

	/**
	 * @return The XML tag for values.
	 */
	public String getValueXML() {
		return this.valueXML;
	}

	/**
	 * @return An iterator for this data.
	 */
	public Iterator<NeuralDataPair> iterator() {
		final XMLNeuralIterator result = new XMLNeuralIterator();
		this.iterators.add(result);
		return result;
	}
	
	/**
	 * @return True if this training data is supervised.
	 */
	@Override
	public boolean isSupervised() {
		return this.idealSize>0;
	}

}
