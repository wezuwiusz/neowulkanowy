package io.github.wulkanowy.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class DayTest extends AbstractDaoTestLongPk<DayDao, Day> {

    public DayTest() {
        super(DayDao.class);
    }

    @Override
    protected Day createEntity(Long key) {
        Day entity = new Day();
        entity.setId(key);
        entity.setIsFreeDay(false);
        return entity;
    }

}
