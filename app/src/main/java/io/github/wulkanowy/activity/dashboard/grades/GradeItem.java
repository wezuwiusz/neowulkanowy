package io.github.wulkanowy.activity.dashboard.grades;

import android.os.Parcel;
import android.os.Parcelable;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.grades.Grade;


public class GradeItem extends Grade implements Parcelable {

    protected GradeItem(Parcel source) {
        value = source.readString();
    }

    public GradeItem() {
        // empty constructor
    }

    public int getValueColor() {
        if ("6".equals(value) || "6-".equals(value) || "6+".equals(value)) {
            return R.color.six_grade;
        } else if ("5".equals(value) || "5-".equals(value) || "5+".equals(value)) {
            return R.color.five_grade;
        } else if ("4".equals(value) || "4-".equals(value) || "4+".equals(value)) {
            return R.color.four_grade;
        } else if ("3".equals(value) || "3-".equals(value) || "3+".equals(value)) {
            return R.color.three_grade;
        } else if ("2".equals(value) || "2-".equals(value) || "2+".equals(value)) {
            return R.color.two_grade;
        } else if ("1".equals(value) || "1-".equals(value) || "1+".equals(value)) {
            return R.color.one_grade;
        } else {
            return R.color.default_grade;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    public static final Creator<GradeItem> CREATOR = new Creator<GradeItem>() {
        @Override
        public GradeItem createFromParcel(Parcel source) {
            return new GradeItem(source);
        }

        @Override
        public GradeItem[] newArray(int size) {
            return new GradeItem[size];
        }
    };
}
