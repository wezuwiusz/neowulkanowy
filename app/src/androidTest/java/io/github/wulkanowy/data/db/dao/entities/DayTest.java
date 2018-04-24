package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class DayTest extends AbstractDaoTestLongPk<DayDao, Day> {

    public DayTest() {
        super(DayDao.class);
    }

    @Override
    protected Day createEntity(Long key) {
        Day entity = new Day();
        entity.setId(key);
        entity.setFreeDay(false);
        return entity;
    }

}
