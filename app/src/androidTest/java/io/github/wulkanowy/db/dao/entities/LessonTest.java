package io.github.wulkanowy.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class LessonTest extends AbstractDaoTestLongPk<LessonDao, Lesson> {

    public LessonTest() {
        super(LessonDao.class);
    }

    @Override
    protected Lesson createEntity(Long key) {
        Lesson entity = new Lesson();
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
