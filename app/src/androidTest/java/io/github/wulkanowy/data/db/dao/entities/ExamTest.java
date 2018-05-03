package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class ExamTest extends AbstractDaoTestLongPk<ExamDao, Exam> {

    public ExamTest() {
        super(ExamDao.class);
    }

    @Override
    protected Exam createEntity(Long key) {
        Exam entity = new Exam();
        entity.setId(key);
        return entity;
    }

}
