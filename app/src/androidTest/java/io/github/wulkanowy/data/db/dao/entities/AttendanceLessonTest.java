package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class AttendanceLessonTest extends AbstractDaoTestLongPk<AttendanceLessonDao, AttendanceLesson> {

    public AttendanceLessonTest() {
        super(AttendanceLessonDao.class);
    }

    @Override
    protected AttendanceLesson createEntity(Long key) {
        AttendanceLesson entity = new AttendanceLesson();
        entity.setId(key);
        entity.setPresence(false);
        entity.setAbsenceUnexcused(false);
        entity.setAbsenceExcused(false);
        entity.setUnexcusedLateness(false);
        entity.setAbsenceForSchoolReasons(false);
        entity.setExcusedLateness(false);
        entity.setExemption(false);
        return entity;
    }

}
