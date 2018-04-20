package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class DiaryTest extends AbstractDaoTestLongPk<DiaryDao, Diary> {

    public DiaryTest() {
        super(DiaryDao.class);
    }

    @Override
    protected Diary createEntity(Long key) {
        Diary entity = new Diary();
        entity.setId(key);
        entity.setIsCurrent(false);
        return entity;
    }

}
