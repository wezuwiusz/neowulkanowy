package io.github.wulkanowy.api.grades;

@Deprecated
public class Subject {

    private String name;

    private String predictedRating;

    private String finalRating;

    public String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;

        return this;
    }

    public String getPredictedRating() {
        return predictedRating;
    }

    public Subject setPredictedRating(String predictedRating) {
        this.predictedRating = predictedRating;

        return this;
    }

    public String getFinalRating() {
        return finalRating;
    }

    public Subject setFinalRating(String finalRating) {
        this.finalRating = finalRating;

        return this;
    }
}
