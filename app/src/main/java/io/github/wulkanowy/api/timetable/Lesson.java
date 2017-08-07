package io.github.wulkanowy.api.timetable;

public class Lesson {

    public static final String CLASS_PLANNING = "x-treelabel-ppl";

    public static final String CLASS_REALIZED = "x-treelabel-rlz";

    public static final String CLASS_MOVED_OR_CANCELED = "x-treelabel-inv";

    public static final String CLASS_NEW_MOVED_IN_OR_CHANGED = "x-treelabel-zas";

    private String subject = "";

    private String teacher = "";

    private String room = "";

    private String description = "";

    private String groupName = "";

    private String startTime = "";

    private String endTime = "";

    private boolean isEmpty = false;

    private boolean isDivisionIntoGroups = false;

    private boolean isPlanning = false;

    private boolean isRealized = false;

    private boolean isMovedOrCanceled = false;

    private boolean isNewMovedInOrChanged = false;

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

    public Lesson setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public Lesson setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public Lesson setEndTime(String endTime) {
        this.endTime = endTime;
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

    public Lesson setDivisionIntoGroups(boolean divisionIntoGroups) {
        isDivisionIntoGroups = divisionIntoGroups;
        return this;
    }

    public boolean isPlanning() {
        return isPlanning;
    }

    public Lesson setPlanning(boolean planning) {
        isPlanning = planning;
        return this;
    }

    public boolean isRealized() {
        return isRealized;
    }

    public Lesson setRealized(boolean realized) {
        isRealized = realized;
        return this;
    }

    public boolean isMovedOrCanceled() {
        return isMovedOrCanceled;
    }

    public Lesson setMovedOrCanceled(boolean movedOrCanceled) {
        isMovedOrCanceled = movedOrCanceled;
        return this;
    }

    public boolean isNewMovedInOrChanged() {
        return isNewMovedInOrChanged;
    }

    public Lesson setNewMovedInOrChanged(boolean newMovedInOrChanged) {
        isNewMovedInOrChanged = newMovedInOrChanged;
        return this;
    }
}
