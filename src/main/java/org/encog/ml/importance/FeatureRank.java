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

/**
 * The ranking/importance of an individual feature.
 */
public class FeatureRank implements Comparable<FeatureRank> {

    /**
     * The name of a feature.
     */
    private final String name;

    /**
     * The total importance for this feature.
     */
    private double totalWeight;

    /**
     * The importance of this feature, by percentage.
     */
    private double importancePercent;

    /**
     * Construct the feature ranking.
     * @param theName The name of the feature.
     */
    public FeatureRank(String theName) {
        this.name = theName;
    }

    /**
     * @return The name of this feature.
     */
    public String getName() {
        return name;
    }

    /**
     * Add weight to the total importance of this feature.
     * @param theWeight The weight to add.
     */
    public void addWeight(double theWeight) {
        this.totalWeight+=theWeight;
    }

    /**
     * @return The total weight.
     */
    public double getTotalWeight() {
        return totalWeight;
    }

    /**
     * Set the total weight.
     * @param totalWeight The total weight.
     */
    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * @return The importance percent.
     */
    public double getImportancePercent() {
        return importancePercent;
    }

    /**
     * Set the importance percent.
     * @param importancePercent The importance percent.
     */
    public void setImportancePercent(double importancePercent) {
        this.importancePercent = importancePercent;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getName());
        result.append(", importance:");
        result.append(getImportancePercent());
        result.append(", total weight:");
        result.append(getTotalWeight());
        return result.toString();
    }

    private String GetName() {
        return this.name;
    }

    @Override
    public int compareTo(FeatureRank o) {
        return Double.compare(o.getImportancePercent(),
                getImportancePercent()
        );
    }
}
