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
        entity.setEmpty(false);
        entity.setDivisionIntoGroups(false);
        entity.setPlanning(false);
        entity.setRealized(false);
        entity.setMovedOrCanceled(false);
        entity.setNewMovedInOrChanged(false);
        return entity;
    }

}
