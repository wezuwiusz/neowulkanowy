package io.github.wulkanowy.utilities;

import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public abstract class AverageCalculator {

    public static float calculate(List<Grade> gradeList) {

        float counter = 0f;
        float denominator = 0f;

        for (Grade grade : gradeList) {
            int integerWeight = getIntegerForWeightOfGrade(grade.getWeight());
            float floatValue = getMathematicalValueOfGrade(grade.getValue());

            if (floatValue != -1f) {
                counter += floatValue * integerWeight;
                denominator += integerWeight;
            }
        }

        if (counter == 0f) {
            return -1f;
        } else {
            return counter / denominator;
        }
    }

    private static float getMathematicalValueOfGrade(String valueOfGrade) {
        if (valueOfGrade.matches("[-|+|=]{0,2}[0-6]") || valueOfGrade.matches("[0-6][-|+|=]{0,2}")) {
            if (valueOfGrade.matches("[-][0-6]") || valueOfGrade.matches("[0-6][-]")) {
                String replacedValue = valueOfGrade.replaceAll("[-]", "");
                return Float.valueOf(replacedValue) - 0.25f;
            } else if (valueOfGrade.matches("[+][0-6]") || valueOfGrade.matches("[0-6][+]")) {
                String replacedValue = valueOfGrade.replaceAll("[+]", "");
                return Float.valueOf((replacedValue)) + 0.25f;
            } else if (valueOfGrade.matches("[-|=]{1,2}[0-6]") || valueOfGrade.matches("[0-6][-|=]{1,2}")) {
                String replacedValue = valueOfGrade.replaceAll("[-|=]{1,2}", "");
                return Float.valueOf((replacedValue)) - 0.5f;
            } else {
                return Float.valueOf(valueOfGrade);
            }
        } else {
            return -1;
        }
    }

    private static int getIntegerForWeightOfGrade(String weightOfGrade) {
        return Integer.valueOf(weightOfGrade.substring(0, weightOfGrade.length() - 3));
    }
}
