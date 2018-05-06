package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(
        nameInDb = "Exams",
        active = true
)

public class Exam implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "day_id")
    private Long dayId;

    @Property(nameInDb = "subject_and_group")
    private String subjectAndGroup = "";

    @Property(nameInDb = "type")
    private String type = "";

    @Property(nameInDb = "description")
    private String description = "";

    @Property(nameInDb = "teacher")
    private String teacher = "";

    @Property(nameInDb = "entry_date")
    private String entryDate = "";

    private static final long serialVersionUID = 42L;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 973692038)
    private transient ExamDao myDao;

    @Generated(hash = 998653360)
    public Exam(Long id, Long dayId, String subjectAndGroup, String type, String description,
                String teacher, String entryDate) {
        this.id = id;
        this.dayId = dayId;
        this.subjectAndGroup = subjectAndGroup;
        this.type = type;
        this.description = description;
        this.teacher = teacher;
        this.entryDate = entryDate;
    }

    @Generated(hash = 945526930)
    public Exam() {
    }

    public Long getId() {
        return id;
    }

    public Exam setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getDayId() {
        return this.dayId;
    }

    public Exam setDayId(Long dayId) {
        this.dayId = dayId;
        return this;
    }

    public String getSubjectAndGroup() {
        return subjectAndGroup;
    }

    public Exam setSubjectAndGroup(String subjectAndGroup) {
        this.subjectAndGroup = subjectAndGroup;
        return this;
    }

    public String getType() {
        return type;
    }

    public Exam setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Exam setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Exam setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public Exam setEntryDate(String entryDate) {
        this.entryDate = entryDate;
        return this;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1730563422)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getExamDao() : null;
    }

}