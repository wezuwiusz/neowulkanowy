package io.github.wulkanowy.api.exams;

@Deprecated
public class Exam {

    private String subjectAndGroup = "";

    private String type = "";

    private String description = "";

    private String teacher = "";

    private String entryDate = "";

    public String getSubjectAndGroup() {
        return subjectAndGroup;
    }

    public Exam setSubjectAndGroup(String subjectAndGroup) {
        this.subjectAndGroup = subjectAndGroup;
        return this;
    }

    public String getType() {
        return type;
    }

    public Exam setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Exam setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Exam setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public Exam setEntryDate(String entryDate) {
        this.entryDate = entryDate;
        return this;
    }
}
