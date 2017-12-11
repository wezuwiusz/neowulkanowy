package io.github.wulkanowy.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import io.github.wulkanowy.dao.entities.Week;
import io.github.wulkanowy.dao.entities.WeekDao;

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
