package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class TimetableLessonTest extends AbstractDaoTestLongPk<TimetableLessonDao, TimetableLesson> {

    public TimetableLessonTest() {
        super(TimetableLessonDao.class);
    }

    @Override
    protected TimetableLesson createEntity(Long key) {
        TimetableLesson entity = new TimetableLesson();
        entity.setId(key);
        entity.setIsEmpty(false);
        entity.setIsDivisionIntoGroups(false);
        entity.setIsPlanning(false);
        entity.setIsRealized(false);
        entity.setIsMovedOrCanceled(false);
        entity.setIsNewMovedInOrChanged(false);
        return entity;
    }

}
