package io.github.wulkanowy.dao.entities;

import android.support.test.filters.SmallTest;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

@SmallTest
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
