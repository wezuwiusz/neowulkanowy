package io.github.wulkanowy.api.generic;

@Deprecated
public class Lesson {

    private int number = 0;

    private String subject = "";

    private String teacher = "";

    private String room = "";

    private String description = "";

    private String groupName = "";

    private String startTime = "";

    private String endTime = "";

    private String date = "";

    private boolean isEmpty = false;

    private boolean isDivisionIntoGroups = false;

    private boolean isPlanning = false;

    private boolean isRealized = false;

    private boolean isMovedOrCanceled = false;

    private boolean isNewMovedInOrChanged = false;

    private boolean isNotExist = false;

    private boolean isPresence = false;

    private boolean isAbsenceUnexcused = false;

    private boolean isAbsenceExcused = false;

    private boolean isUnexcusedLateness = false;

    private boolean isAbsenceForSchoolReasons = false;

    private boolean isExcusedLateness = false;

    private boolean isExemption = false;

    public int getNumber() {
        return number;
    }

    public Lesson setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Lesson setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Lesson setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getRoom() {
        return room;
    }

    public Lesson setRoom(String room) {
        this.room = room;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Lesson setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public Lesson setDate(String date) {
        this.date = date;
        return this;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Lesson setEmpty(boolean empty) {
        isEmpty = empty;
        return this;
    }

    public boolean isDivisionIntoGroups() {
        return isDivisionIntoGroups;
    }

    public void setDivisionIntoGroups(boolean divisionIntoGroups) {
        isDivisionIntoGroups = divisionIntoGroups;
    }

    public boolean isPlanning() {
        return isPlanning;
    }

    public void setPlanning(boolean planning) {
        isPlanning = planning;
    }

    public boolean isRealized() {
        return isRealized;
    }

    public void setRealized(boolean realized) {
        isRealized = realized;
    }

    public boolean isMovedOrCanceled() {
        return isMovedOrCanceled;
    }

    public void setMovedOrCanceled(boolean movedOrCanceled) {
        isMovedOrCanceled = movedOrCanceled;
    }

    public boolean isNewMovedInOrChanged() {
        return isNewMovedInOrChanged;
    }

    public void setNewMovedInOrChanged(boolean newMovedInOrChanged) {
        isNewMovedInOrChanged = newMovedInOrChanged;
    }

    public boolean isNotExist() {
        return isNotExist;
    }

    public void setNotExist(boolean notExist) {
        isNotExist = notExist;
    }

    public boolean isPresence() {
        return isPresence;
    }

    public void setPresence(boolean presence) {
        isPresence = presence;
    }

    public boolean isAbsenceUnexcused() {
        return isAbsenceUnexcused;
    }

    public void setAbsenceUnexcused(boolean absenceUnexcused) {
        isAbsenceUnexcused = absenceUnexcused;
    }

    public boolean isAbsenceExcused() {
        return isAbsenceExcused;
    }

    public void setAbsenceExcused(boolean absenceExcused) {
        isAbsenceExcused = absenceExcused;
    }

    public boolean isUnexcusedLateness() {
        return isUnexcusedLateness;
    }

    public void setUnexcusedLateness(boolean unexcusedLateness) {
        isUnexcusedLateness = unexcusedLateness;
    }

    public boolean isAbsenceForSchoolReasons() {
        return isAbsenceForSchoolReasons;
    }

    public void setAbsenceForSchoolReasons(boolean absenceForSchoolReasons) {
        isAbsenceForSchoolReasons = absenceForSchoolReasons;
    }

    public boolean isExcusedLateness() {
        return isExcusedLateness;
    }

    public void setExcusedLateness(boolean excusedLateness) {
        isExcusedLateness = excusedLateness;
    }

    public boolean isExemption() {
        return isExemption;
    }

    public void setExemption(boolean exemption) {
        isExemption = exemption;
    }
}
