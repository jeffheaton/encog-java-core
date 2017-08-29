/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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
package org.encog.util.data;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * This reads the MNIST dataset of handwritten digits into an Encog data set.
 * The MNIST dataset is found at http://yann.lecun.com/exdb/mnist/.
 * 
 * Adapted from a class by Gabe Johnson &lt;johnsogg@cmu.edu&gt;.
 * https://code.google.com
 * /p/pen-ui/source/browse/trunk/skrui/src/org/six11/skrui
 * /charrec/MNISTReader.java?r=185
 */
public class MNISTReader {

	private final int numLabels;
	private final int numImages;
	private final int numRows;
	private final int numCols;
	private final MLDataSet data;

	public MNISTReader(String labelFilename, String imageFilename) {
		try {
			DataInputStream labels = new DataInputStream(new FileInputStream(
					labelFilename));
			DataInputStream images = new DataInputStream(new FileInputStream(
					imageFilename));
			int magicNumber = labels.readInt();
			if (magicNumber != 2049) {
				throw new EncogError("Label file has wrong magic number: "
						+ magicNumber + " (should be 2049)");
			}
			magicNumber = images.readInt();
			if (magicNumber != 2051) {
				throw new EncogError("Image file has wrong magic number: "
						+ magicNumber + " (should be 2051)");
			}
			this.numLabels = labels.readInt();
			this.numImages = images.readInt();
			this.numRows = images.readInt();
			this.numCols = images.readInt();
			if (numLabels != numImages) {
				StringBuilder str = new StringBuilder();
				str.append("Image file and label file do not contain the same number of entries.\n");
				str.append("  Label file contains: " + numLabels + "\n");
				str.append("  Image file contains: " + numImages + "\n");
				throw new EncogError(str.toString());
			}

			byte[] labelsData = new byte[numLabels];
			labels.read(labelsData);
			int imageVectorSize = numCols * numRows;
			byte[] imagesData = new byte[numLabels * imageVectorSize];
			images.read(imagesData);
			
			this.data = new BasicMLDataSet();
			int imageIndex = 0;
			for(int i=0;i<this.numLabels;i++) {
				int label = labelsData[i];
				MLData inputData = new BasicMLData(imageVectorSize);
				for(int j=0;j<imageVectorSize;j++) {
					inputData.setData(j, ((double)(imagesData[imageIndex++]&0xff))/255.0);
				}
				MLData idealData = new BasicMLData(10);
				idealData.setData(label, 1.0);
				this.data.add(new BasicMLDataPair(inputData,idealData));
			}
			
			images.close();
			labels.close();

		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

	/**
	 * @return the numLabels
	 */
	public int getNumLabels() {
		return numLabels;
	}

	/**
	 * @return the numImages
	 */
	public int getNumImages() {
		return numImages;
	}

	/**
	 * @return the numRows
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
	}

	/**
	 * @return the data
	 */
	public MLDataSet getData() {
		return data;
	}
	
	

}
