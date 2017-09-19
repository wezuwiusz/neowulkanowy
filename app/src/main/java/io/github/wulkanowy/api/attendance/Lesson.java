package io.github.wulkanowy.api.attendance;

public class Lesson {

    protected static final String CLASS_NOT_EXIST = "x-sp-nieobecny-w-oddziale";

    protected static final String CLASS_PRESENCE = "x-obecnosc";

    protected static final String CLASS_ABSENCE_UNEXCUSED = "x-nieobecnosc-nieuspr";

    protected static final String CLASS_ABSENCE_EXCUSED = "x-nieobecnosc-uspr";

    protected static final String CLASS_ABSENCE_FOR_SCHOOL_REASONS = "x-nieobecnosc-przycz-szkol";

    protected static final String CLASS_UNEXCUSED_LATENESS = "x-sp-nieusprawiedliwione";

    protected static final String CLASS_EXCUSED_LATENESS = "x-sp-spr";

    protected static final String CLASS_EXEMPTION = "x-sp-zwolnienie";

    private String subject = "";

    private boolean isNotExist = false;

    private boolean isPresence = false;

    private boolean isAbsenceUnexcused = false;

    private boolean isAbsenceExcused = false;

    private boolean isUnexcusedLateness = false;

    private boolean isAbsenceForSchoolReasons = false;

    private boolean isExcusedLateness = false;

    private boolean isExemption = false;

    private boolean isEmpty = false;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
