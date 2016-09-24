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
