package io.github.wulkanowy.api.timetable;

@Deprecated
class LessonTypes {

    static final String CLASS_PLANNING = "x-treelabel-ppl";

    static final String CLASS_REALIZED = "x-treelabel-rlz";

    static final String CLASS_MOVED_OR_CANCELED = "x-treelabel-inv";

    static final String CLASS_NEW_MOVED_IN_OR_CHANGED = "x-treelabel-zas";

    private LessonTypes() {
        throw new IllegalStateException("Utility class");
    }
}
