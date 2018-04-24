package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Diaries",
        active = true
)
public class Diary {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "student_id")
    private Long studentId;

    @Property(nameInDb = "current")
    private boolean current;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "value")
    private String value;

    @ToMany(referencedJoinProperty = "diaryId")
    private List<Semester> semesterList;

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

    @Generated(hash = 277096196)
    public Diary(Long id, Long studentId, boolean current, String name, String value) {
        this.id = id;
        this.studentId = studentId;
        this.current = current;
        this.name = name;
        this.value = value;
    }

    @Generated(hash = 112123061)
    public Diary() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public Diary setStudentId(Long studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Diary setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Diary setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean getCurrent() {
        return this.current;
    }

    public Diary setCurrent(boolean current) {
        this.current = current;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1738383053)
    public List<Semester> getSemesterList() {
        if (semesterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SemesterDao targetDao = daoSession.getSemesterDao();
            List<Semester> semesterListNew = targetDao._queryDiary_SemesterList(id);
            synchronized (this) {
                if (semesterList == null) {
                    semesterList = semesterListNew;
                }
            }
        }
        return semesterList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 995060657)
    public synchronized void resetSemesterList() {
        semesterList = null;
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
