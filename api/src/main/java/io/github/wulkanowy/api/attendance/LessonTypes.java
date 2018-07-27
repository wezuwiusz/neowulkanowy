package io.github.wulkanowy.api.attendance;

@Deprecated
class LessonTypes {

    static final String CLASS_NOT_EXIST = "x-sp-nieobecny-w-oddziale";

    static final String CLASS_PRESENCE = "x-obecnosc";

    static final String CLASS_ABSENCE_UNEXCUSED = "x-nieobecnosc-nieuspr";

    static final String CLASS_ABSENCE_EXCUSED = "x-nieobecnosc-uspr";

    static final String CLASS_ABSENCE_FOR_SCHOOL_REASONS = "x-nieobecnosc-przycz-szkol";

    static final String CLASS_UNEXCUSED_LATENESS = "x-sp-nieusprawiedliwione";

    static final String CLASS_EXCUSED_LATENESS = "x-sp-spr";

    static final String CLASS_EXEMPTION = "x-sp-zwolnienie";

    private LessonTypes() {
        throw new IllegalStateException("Utility class");
    }
}
