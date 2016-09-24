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
