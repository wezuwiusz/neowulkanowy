package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLessonDao;

public class AttendanceLessonTest extends AbstractDaoTestLongPk<AttendanceLessonDao, AttendanceLesson> {

    public AttendanceLessonTest() {
        super(AttendanceLessonDao.class);
    }

    @Override
    protected AttendanceLesson createEntity(Long key) {
        AttendanceLesson entity = new AttendanceLesson();
        entity.setId(key);
        entity.setIsPresence(false);
        entity.setIsAbsenceUnexcused(false);
        entity.setIsAbsenceExcused(false);
        entity.setIsUnexcusedLateness(false);
        entity.setIsAbsenceForSchoolReasons(false);
        entity.setIsExcusedLateness(false);
        entity.setIsExemption(false);
        return entity;
    }

}
