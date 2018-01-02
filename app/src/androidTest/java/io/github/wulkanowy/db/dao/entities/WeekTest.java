package io.github.wulkanowy.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class WeekTest extends AbstractDaoTestLongPk<WeekDao, Week> {

    public WeekTest() {
        super(WeekDao.class);
    }

    @Override
    protected Week createEntity(Long key) {
        Week entity = new Week();
        entity.setId(key);
        return entity;
    }

}
