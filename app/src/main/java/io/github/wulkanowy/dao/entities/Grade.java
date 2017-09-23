package io.github.wulkanowy.dao.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import io.github.wulkanowy.R;

@Entity(nameInDb = "Grades")
public class Grade implements Parcelable {

    @Id(autoincrement = true)
    protected Long id;

    @Property(nameInDb = "SUBJECT_ID")
    private Long subjectId;

    @Property(nameInDb = "USER_ID")
    private Long userId;

    @Property(nameInDb = "SUBJECT")
    private String subject = "";

    @Property(nameInDb = "VALUE")
    protected String value = "";

    @Property(nameInDb = "COLOR")
    private String color = "";

    @Property(nameInDb = "SYMBOL")
    private String symbol = "";

    @Property(nameInDb = "DESCRIPTION")
    private String description = "";

    @Property(nameInDb = "WEIGHT")
    private String weight = "";

    @Property(nameInDb = "DATE")
    private String date = "";

    @Property(nameInDb = "TEACHER")
    private String teacher = "";

    @Property(nameInDb = "SEMESTER")
    private String semester = "";

    @Property(nameInDb = "IS_NEW")
    private boolean isNew = false;

    protected Grade(Parcel source) {
        value = source.readString();
    }

    @Generated(hash = 1154096520)
    public Grade(Long id, Long subjectId, Long userId, String subject, String value,
                 String color, String symbol, String description, String weight,
                 String date, String teacher, String semester, boolean isNew) {
        this.id = id;
        this.subjectId = subjectId;
        this.userId = userId;
        this.subject = subject;
        this.value = value;
        this.color = color;
        this.symbol = symbol;
        this.description = description;
        this.weight = weight;
        this.date = date;
        this.teacher = teacher;
        this.semester = semester;
        this.isNew = isNew;
    }

    @Generated(hash = 2042976393)
    public Grade() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subject);
        parcel.writeString(value);
        parcel.writeString(color);
        parcel.writeString(symbol);
        parcel.writeString(description);
        parcel.writeString(weight);
        parcel.writeString(date);
        parcel.writeString(value);
        parcel.writeString(value);
    }

    public static final Creator<Grade> CREATOR = new Creator<Grade>() {
        @Override
        public Grade createFromParcel(Parcel source) {
            return new Grade(source);
        }

        @Override
        public Grade[] newArray(int size) {
            return new Grade[size];
        }
    };

    public int getValueColor() {

        String replacedString = value.replaceAll("[^0-9]", "");

        if (!"".equals(replacedString)) {
            switch (Integer.parseInt(replacedString)) {
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
        return R.color.default_grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        return new EqualsBuilder()
                .append(subject, grade.subject)
                .append(value, grade.value)
                .append(color, grade.color)
                .append(symbol, grade.symbol)
                .append(description, grade.description)
                .append(weight, grade.weight)
                .append(date, grade.date)
                .append(teacher, grade.teacher)
                .append(semester, grade.semester)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(value)
                .append(color)
                .append(symbol)
                .append(description)
                .append(weight)
                .append(date)
                .append(teacher)
                .append(semester)
                .toHashCode();
    }

    public Long getId() {
        return id;
    }

    public Grade setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public Grade setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Grade setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Grade setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Grade setValue(String value) {
        this.value = value;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Grade setColor(String color) {
        this.color = color;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Grade setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Grade setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public Grade setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Grade setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Grade setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getSemester() {
        return semester;
    }

    public Grade setSemester(String semester) {
        this.semester = semester;
        return this;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
