package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        nameInDb = "Diaries",
        active = true
)
public class Diary {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "STUDENT_ID")
    private String studentId;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "VALUE")
    private String value;

    @Property(nameInDb = "IS_CURRENT")
    private boolean isCurrent;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 21166549)
    private transient DiaryDao myDao;

    @Generated(hash = 459332202)
    public Diary(Long id, String studentId, String name, String value,
                 boolean isCurrent) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.value = value;
        this.isCurrent = isCurrent;
    }

    @Generated(hash = 112123061)
    public Diary() {
    }

    public Long getId() {
        return id;
    }

    public Diary setId(Long id) {
        this.id = id;
        return this;
    }

    public String getStudentId() {
        return studentId;
    }

    public Diary setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Diary setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Diary setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean getIsCurrent() {
        return isCurrent;
    }

    public Diary setIsCurrent(boolean current) {
        isCurrent = current;
        return this;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Diary setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        return this;
    }

    public DiaryDao getMyDao() {
        return myDao;
    }

    public Diary setMyDao(DiaryDao myDao) {
        this.myDao = myDao;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 629297785)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDiaryDao() : null;
    }
}
