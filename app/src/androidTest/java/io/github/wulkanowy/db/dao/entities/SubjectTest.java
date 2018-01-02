package io.github.wulkanowy.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class SubjectTest extends AbstractDaoTestLongPk<SubjectDao, Subject> {

    public SubjectTest() {
        super(SubjectDao.class);
    }

    @Override
    protected Subject createEntity(Long key) {
        Subject entity = new Subject();
        entity.setId(key);
        return entity;
    }

}
