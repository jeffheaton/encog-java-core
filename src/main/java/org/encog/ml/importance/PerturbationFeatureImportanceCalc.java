package org.encog.ml.importance;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * perturbation  feature encoding can be used to determine the importance of features for any type of regression or
 * classification model, with any compatible dataset.  This method works by evaluating the performance of the model
 * when each of the input's corrisponding data is scrambled.  Features that are more important will result in worse
 * errors when their data are scrambled.
 *
 * Source:
 * Breiman, L. (2001). Random forests. Machine learning, 45(1), 5-32.
 */
public class PerturbationFeatureImportanceCalc extends AbstractFeatureImportance {

    /**
     * Random number generator.
     */
    private GenerateRandom rnd = new MersenneTwisterGenerateRandom();

    /**
     * Generate a dataset where one of the columns is scrambled/permuted.
     * @param source The source dataset.
     * @param column The column to scramble.
     * @return The resulting dataset.
     */
    private MLDataSet generatePermutation(MLDataSet source, int column) {
        MLDataSet result = new BasicMLDataSet();
        for(MLDataPair item:source) {
            BasicMLData input = new BasicMLData(item.getInput().size());
            BasicMLData ideal = new BasicMLData(item.getIdeal().size());
            EngineArray.arrayCopy(item.getInputArray(),input.getData());
            EngineArray.arrayCopy(item.getIdealArray(),ideal.getData());
            MLDataPair newPair = new BasicMLDataPair(input,ideal);
            result.add(newPair);
        }

        for(int i=0;i<result.size();i++) {
            int r = i + rnd.nextInt(result.size()-i);
            MLData rowR = result.get(r).getInput();
            MLData rowI = result.get(i).getInput();

            double t = rowR.getData(column);
            rowR.setData(column,rowI.getData(column));
            rowI.setData(column,t);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking() {
        throw new EncogError("This algorithm requires a dataset to measure performance against, please call performRanking with a dataset.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking(MLDataSet theDataset) {
        double max = 0;
        for(int i=0;i<getModel().getInputCount();i++) {
            FeatureRank fr = getFeatures().get(i);
            MLDataSet p = generatePermutation(theDataset,i);
            double e = EncogUtility.calculateRegressionError(getModel(),p);
            fr.setTotalWeight(e);
            max = Math.max(max,e);
        }

        for(FeatureRank fr:getFeatures()) {
            fr.setImportancePercent(fr.getTotalWeight()/max);
        }
    }

    /**
     * @return The random number generator.
     */
    public GenerateRandom getRnd() {
        return rnd;
    }

    /**
     * Set the random number generator.
     * @param rnd The random number generator.
     */
    public void setRnd(GenerateRandom rnd) {
        this.rnd = rnd;
    }
}
