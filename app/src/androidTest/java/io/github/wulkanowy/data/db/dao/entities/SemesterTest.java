package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.SemesterDao;

public class SemesterTest extends AbstractDaoTestLongPk<SemesterDao, Semester> {

    public SemesterTest() {
        super(SemesterDao.class);
    }

    @Override
    protected Semester createEntity(Long key) {
        Semester entity = new Semester();
        entity.setId(key);
        return entity;
    }

}
