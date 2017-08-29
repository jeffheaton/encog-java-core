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
package org.encog.neural.networks.training.propagation.sgd;

import java.util.Iterator;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * The BatchDataSet wraps a larger dataset and breaks it up into a series of batches.  This dataset was specifically
 * created to be used with the StochasticGradientDescent trainer; however, it should work with the others as well.
 * It is important that the BatchDataSet's advance method be called at the end of each iteration, so that the next
 * batch can be prepared.  All Encog-provided trainers will detect the BatchDataSet and make this call.
 *
 * This dataset can be used in two ways, depending on the setting of the randomSamples property.  If this value is
 * false (the default), then the first batch starts at the beginning of the dataset, and following batches will start
 * at the end of the previous batch.  This method ensures that every data item is used  If randomSamples is true, then
 * each batch will be sampled from the underlying dataset (without replacement).
 */
public class BatchDataSet implements MLDataSet {

    /**
     * An iterator to be used with the BasicMLDataSet. This iterator does not
     * support removes.
     *
     * @author jheaton
     */
    public class BatchedMLIterator implements Iterator<MLDataPair> {

        /**
         * The index that the iterator is currently at.
         */
        private int currentIndex = 0;

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean hasNext() {
            return this.currentIndex < BatchDataSet.this.getBatchSize();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final MLDataPair next() {
            if (!hasNext()) {
                return null;
            }

            return BatchDataSet.this.get(this.currentIndex++);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final void remove() {
            throw new EncogError("Called remove, unsupported operation.");
        }
    }

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
     * Should a random sample be taken for each batch.
     */
    private boolean randomBatches;

    /**
     * Index entries for the current random sample.
     */
    private int[] randomSample;

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
        this.randomSample = new int[this.batchSize];
        if( this.randomBatches ) {
            generaterandomSample();
        }
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MLDataPair> iterator() {
        final BatchDataSet.BatchedMLIterator result = new BatchDataSet.BatchedMLIterator();
        return result;
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
     * This will open an additional batched dataset.  However, please note, the additional datasets will use a
     * mersenne twister generator that is seeded by a long sampled from this object's random number
     * generator.
     * @return An additional dataset.
     */
    @Override
    public MLDataSet openAdditional() {
        BatchDataSet result = new BatchDataSet(this.dataset,new MersenneTwisterGenerateRandom(this.random.nextLong()));
        result.setBatchSize(getBatchSize());
        return result;
    }

    /**
     * This operation is not supported by this object.
     * @param data1 NA
     */
    @Override
    public void add(MLData data1) {
        throw new EncogError("Unsupported.");
    }

    /**
     * This operation is not supported by this object.
     * @param inputData NA
     * @param idealData NA
     */
    @Override
    public void add(MLData inputData, MLData idealData) {
        throw new EncogError("Unsupported.");
    }

    /**
     * This operation is not supported by this object.
     * @param inputData NA
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
        int resultIndex = (index+this.currentIndex)%this.dataset.size();

        if( this.randomBatches) {
            resultIndex = this.randomSample[resultIndex];
        }
        return this.dataset.get(resultIndex);
    }

    /**
     * Advance to the next batch.  Should be called at the end of each training iteration.
     */
    public void advance() {
        if( this.randomBatches) {
            generaterandomSample();
        } else {
            this.currentIndex = (this.currentIndex + this.batchSize) % this.dataset.size();
        }
    }

    /**
     * @return The current index, within a batch.
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Set the current index, within a batch.
     * @param currentIndex The current index, within a batch.
     */
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    /**
     * @return True, if random batches are being used.
     */
    public boolean isRandomBatches() {
        return randomBatches;
    }

    /**
     * Set if random batches should be generated.
     * @param randomBatches True, if random batches should be used.
     */
    public void setRandomBatches(boolean randomBatches) {
        this.randomBatches = randomBatches;
    }

    /**
     * Generate a random sample.
     */
    private void generaterandomSample() {
        for(int i=0;i<this.batchSize;i++) {
            boolean uniqueFound = true;
            int t;

            // Generate a unique index
            do {
                t = this.random.nextInt(0, this.dataset.size());

                for (int j = 0; j < i; j++) {
                    if (this.randomSample[j]==t) {
                        uniqueFound = false;
                        break;
                    }
                }
            } while(!uniqueFound);

            // Record it
            this.randomSample[i] = t;
        }

    }
}
