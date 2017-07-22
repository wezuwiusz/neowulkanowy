package io.github.wulkanowy.api.grades;

public class Subject {

    private String name;
    private String predictedRating;
    private String finalRating;

    public Subject setName(String name) {
        this.name = name;

        return this;
    }

    public Subject setPredictedRating(String predictedRating) {
        this.predictedRating = predictedRating;

        return this;
    }

    public Subject setFinalRating(String finalRating) {
        this.finalRating = finalRating;

        return this;
    }

    public String getName() {
        return name;
    }

    public String getPredictedRating() {
        return predictedRating;
    }

    public String getFinalRating() {
        return finalRating;
    }
}
