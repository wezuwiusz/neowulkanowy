package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import io.github.wulkanowy.data.db.dao.entities.Student;
import io.github.wulkanowy.data.db.dao.entities.StudentDao;

public class StudentTest extends AbstractDaoTestLongPk<StudentDao, Student> {

    public StudentTest() {
        super(StudentDao.class);
    }

    @Override
    protected Student createEntity(Long key) {
        Student entity = new Student();
        entity.setId(key);
        return entity;
    }

}
