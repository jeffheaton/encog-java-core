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

import org.encog.ml.MLInputOutput;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Defines an interface for classes that are used to rank the importance of the input features to a model.
 */
public interface FeatureImportance {
    /**
     * Initialize a model
     * @param theModel The model that will be used for ranking.
     * @param names The names of the fields.
     */
    void init(MLRegression theModel, String[] names);

    /**
     * Perform the ranking, without using a specific training set.  Not all ranking algorithms support this.
     */
    void performRanking();

    /**
     * Perform the ranking, using a specific training set.  Not all ranking algorithms can make use of a dataset.
     * @param theDataset The dataset.
     */
    void performRanking(MLDataSet theDataset);

    /**
     * @return The individual rankings of each feature.
     */
    List<FeatureRank> getFeatures();

    /**
     * @return The sorted individual rankings of each feature.
     */
    Collection<FeatureRank> getFeaturesSorted();

    /**
     * @return The model that was evaluated.
     */
    MLRegression getModel();
}
