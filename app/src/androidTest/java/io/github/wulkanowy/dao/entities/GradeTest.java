package io.github.wulkanowy.dao.entities;

import android.support.test.filters.SmallTest;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

@SmallTest
public class GradeTest extends AbstractDaoTestLongPk<GradeDao, Grade> {

    public GradeTest() {
        super(GradeDao.class);
    }

    @Override
    protected Grade createEntity(Long key) {
        Grade entity = new Grade();
        entity.setId(key);
        entity.setIsNew(false);
        return entity;
    }

}
