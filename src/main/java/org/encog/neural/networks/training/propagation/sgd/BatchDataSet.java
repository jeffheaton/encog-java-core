/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core

 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.sgd;

import java.util.Iterator;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * A dataset that mapps a larger dataset into batches.
 */
public class BatchDataSet implements MLDataSet {

    /**
     * The source dataset.
     */
    private MLDataSet dataset;

    /**
     * The current location within the source dataset.
     */
    private int currentIndex;

    /**
     * The size of the batch.
     */
    private int batchSize;

    /**
     * Random number generator.
     */
    private GenerateRandom random;

    /**
     * Construct the batch dataset.
     * @param theDataset The source dataset.
     * @param theRandom The random number generator.
     */
    public BatchDataSet(MLDataSet theDataset, GenerateRandom theRandom) {
        this.dataset = theDataset;
        this.random = theRandom;
        setBatchSize(500);
    }

    /**
     * @param theSize Set the batch size, but not larger than the dataset.
     */
    public void setBatchSize(int theSize) {
        this.batchSize = Math.min(theSize,this.dataset.size());
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MLDataPair> iterator() {
        throw new EncogError("Unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIdealSize() {
        return this.dataset.getIdealSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInputSize() {
        return this.dataset.getInputSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupervised() {
        return this.dataset.isSupervised();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRecordCount() {
        return this.batchSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRecord(long index, MLDataPair pair) {
        this.dataset.getRecord((index+this.currentIndex)%this.dataset.size(), pair);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MLDataSet openAdditional() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(MLData data1) {
        throw new EncogError("Unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(MLData inputData, MLData idealData) {
        throw new EncogError("Unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(MLDataPair inputData) {
        throw new EncogError("Unsupported.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return this.batchSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MLDataPair get(int index) {
        return this.dataset.get((index+this.currentIndex)%this.dataset.size());
    }

    public void advance() {
        this.currentIndex = (this.currentIndex+this.batchSize)%this.dataset.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
