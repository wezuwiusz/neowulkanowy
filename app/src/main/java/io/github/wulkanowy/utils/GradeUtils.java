package io.github.wulkanowy.utils;

import java.util.regex.Pattern;

public final class GradeUtils {

    private final static Pattern validGradePattern = Pattern.compile("^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$");
    private final static Pattern simpleGradeValuePattern = Pattern.compile("([0-6])");

    private GradeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /*public static float calculateWeightedAverage(List<Grade> gradeList) {

        float counter = 0f;
        float denominator = 0f;

        for (Grade grade : gradeList) {
            int weight = getWeightValue(grade.getWeight());
            float value = getWeightedGradeValue(grade.getValue());

            if (value != -1.0f) {
                counter += value * weight;
                denominator += weight;
            }
        }

        if (counter == 0f) {
            return -1.0f;
        }
        return counter / denominator;
    }

    public static float calculateSubjectsAverage(List<Subject> subjectList, boolean usePredicted) {
        return calculateSubjectsAverage(subjectList, usePredicted, false);
    }

    public static float calculateDetailedSubjectsAverage(List<Subject> subjectList) {
        return calculateSubjectsAverage(subjectList, false, true);
    }

    public static int getValueColor(String value) {
        Matcher m1 = validGradePattern.matcher(value);
        if (!m1.find()) {
            return R.color.grade_default;
        }

        Matcher m2 = simpleGradeValuePattern.matcher(m1.group());
        if (!m2.find()) {
            return R.color.grade_default;
        }

        switch (Integer.parseInt(m2.group())) {
            case 6:
                return R.color.grade_six;
            case 5:
                return R.color.grade_five;
            case 4:
                return R.color.grade_four;
            case 3:
                return R.color.grade_three;
            case 2:
                return R.color.grade_two;
            case 1:
                return R.color.grade_one;
            default:
                return R.color.grade_default;
        }
    }

    private static float calculateSubjectsAverage(List<Subject> subjectList, boolean usePredicted, boolean useSubjectsAverages) {
        float counter = 0f;
        float denominator = 0f;

        for (Subject subject : subjectList) {
            float value;

            if (useSubjectsAverages) {
                value = calculateWeightedAverage(subject.getGradeList());
            } else {
                value = getGradeValue(usePredicted ? subject.getPredictedRating() : subject.getFinalRating());
            }

            if (value != -1.0f) {
                counter += Math.round(value);
                denominator++;
            }
        }

        if (counter == 0) {
            return -1.0f;
        }

        return counter / denominator;
    }

    public static float getGradeValue(String grade) {
        if (validGradePattern.matcher(grade).matches()) {
            return getWeightedGradeValue(grade);
        }

        return getVerbalGradeValue(grade);
    }

    private static float getVerbalGradeValue(String grade) {
        switch (grade) {
            case "celujący":
                return 6f;
            case "bardzo dobry":
                return 5f;
            case "dobry":
                return 4f;
            case "dostateczny":
                return 3f;
            case "dopuszczający":
                return 2f;
            case "niedostateczny":
                return 1f;
            default:
                return -1f;
        }
    }

    public static String getShortGradeValue(String grade) {
        switch (grade) {
            case "celujący":
                return "6";
            case "bardzo dobry":
                return "5";
            case "dobry":
                return "4";
            case "dostateczny":
                return "3";
            case "dopuszczający":
                return "2";
            case "niedostateczny":
                return "1";
            default:
                return grade;
        }
    }

    private static float getWeightedGradeValue(String value) {
        if (validGradePattern.matcher(value).matches()) {
            if (value.matches("[-][0-6]") || value.matches("[0-6][-]")) {
                String replacedValue = value.replaceAll("[-]", "");
                return Float.valueOf(replacedValue) - 0.33f;
            } else if (value.matches("[+][0-6]") || value.matches("[0-6][+]")) {
                String replacedValue = value.replaceAll("[+]", "");
                return Float.valueOf((replacedValue)) + 0.33f;
            } else if (value.matches("[-|=]{1,2}[0-6]") || value.matches("[0-6][-|=]{1,2}")) {
                String replacedValue = value.replaceAll("[-|=]{1,2}", "");
                return Float.valueOf((replacedValue)) - 0.5f;
            } else {
                return Float.valueOf(value);
            }
        } else {
            return -1;
        }
    }

    private static int getWeightValue(String weightOfGrade) {
        return Integer.valueOf(weightOfGrade.substring(0, weightOfGrade.length() - 3));
    }*/
}
