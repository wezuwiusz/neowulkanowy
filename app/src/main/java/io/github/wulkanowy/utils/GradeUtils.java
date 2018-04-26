package io.github.wulkanowy.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;

public final class GradeUtils {

    private final static Pattern validGradePattern = Pattern.compile("^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$");
    private final static Pattern simpleGradeValuePattern = Pattern.compile("([0-6])");

    private GradeUtils() {
        throw new IllegalStateException("Utility class");
    }

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
        if (valueOfGrade.matches("[-|+|=]{0,2}[0-6]")
                || valueOfGrade.matches("[0-6][-|+|=]{0,2}")) {
            if (valueOfGrade.matches("[-][0-6]")
                    || valueOfGrade.matches("[0-6][-]")) {
                String replacedValue = valueOfGrade.replaceAll("[-]", "");
                return Float.valueOf(replacedValue) - 0.33f;
            } else if (valueOfGrade.matches("[+][0-6]")
                    || valueOfGrade.matches("[0-6][+]")) {
                String replacedValue = valueOfGrade.replaceAll("[+]", "");
                return Float.valueOf((replacedValue)) + 0.33f;
            } else if (valueOfGrade.matches("[-|=]{1,2}[0-6]")
                    || valueOfGrade.matches("[0-6][-|=]{1,2}")) {
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

    public static int getValueColor(String value) {
        Matcher m1 = validGradePattern.matcher(value);
        if (!m1.find()) {
            return R.color.default_grade;
        }

        Matcher m2 = simpleGradeValuePattern.matcher(m1.group());
        if (!m2.find()) {
            return R.color.default_grade;
        }

        switch (Integer.parseInt(m2.group())) {
            case 6:
                return R.color.six_grade;
            case 5:
                return R.color.five_grade;
            case 4:
                return R.color.four_grade;
            case 3:
                return R.color.three_grade;
            case 2:
                return R.color.two_grade;
            case 1:
                return R.color.one_grade;
            default:
                return R.color.default_grade;
        }
    }
}
