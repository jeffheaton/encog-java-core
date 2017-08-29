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
package org.encog.ml.importance;

import org.encog.EncogError;
import org.encog.ml.MLInputOutput;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;

import java.util.*;

/**
 * Provides basic functionality for a feature ranking algorithm.
 */
public abstract class AbstractFeatureImportance implements FeatureImportance {

    /**
     * The model that is going to be ranked.
     */
    private MLRegression model;

    /**
     * The features that were ranked.
     */
    private final List<FeatureRank> features = new ArrayList<FeatureRank>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(MLRegression theModel, String[] theFeatureNames) {
        this.model = theModel;

        if( theFeatureNames==null ) {
            for (int i = 0; i < this.model.getInputCount(); i++) {
                this.features.add(new FeatureRank("f" + i));
            }
        } else {
            if( model!=null ) {
                if (theFeatureNames.length != this.model.getInputCount()) {
                    throw new EncogError("Neural network inputs(" + this.model.getInputCount() + ") and feature name count("
                            + theFeatureNames.length + ") do not match.");
                }
            }

            for (String name : theFeatureNames) {
                this.features.add(new FeatureRank(name));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureRank> getFeatures() {
        return this.features;
    }

    /**
     * @return The features sorted by importance.
     */
    public Collection<FeatureRank> getFeaturesSorted() {
        Set<FeatureRank> result = new TreeSet<FeatureRank>();
        result.addAll(this.features);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (FeatureRank ranking : getFeaturesSorted()) {
            int idx = getFeatures().indexOf(ranking);
            if( result.length()>0) {
                result.append(",");
            }
            result.append(idx);
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MLRegression getModel() {
        return this.model;
    }
}
