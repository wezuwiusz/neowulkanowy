package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Schools",
        active = true
)
public class School {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "symbol_id")
    private Long symbolId;

    @Property(nameInDb = "current")
    private boolean current;

    @Property(nameInDb = "real_id")
    private String realId;

    @Property(nameInDb = "name")
    private String name;

    @ToMany(referencedJoinProperty = "schoolId")
    private List<Student> studentList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1796006707)
    private transient SchoolDao myDao;

    @Generated(hash = 975562398)
    public School(Long id, Long symbolId, boolean current, String realId,
                  String name) {
        this.id = id;
        this.symbolId = symbolId;
        this.current = current;
        this.realId = realId;
        this.name = name;
    }

    @Generated(hash = 1579966795)
    public School() {
    }

    public Long getId() {
        return this.id;
    }

    public School setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getSymbolId() {
        return this.symbolId;
    }

    public School setSymbolId(Long symbolId) {
        this.symbolId = symbolId;
        return this;
    }

    public boolean getCurrent() {
        return this.current;
    }

    public School setCurrent(boolean current) {
        this.current = current;
        return this;
    }

    public String getRealId() {
        return this.realId;
    }

    public School setRealId(String realId) {
        this.realId = realId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public School setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 180118651)
    public List<Student> getStudentList() {
        if (studentList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StudentDao targetDao = daoSession.getStudentDao();
            List<Student> studentListNew = targetDao._querySchool_StudentList(id);
            synchronized (this) {
                if (studentList == null) {
                    studentList = studentListNew;
                }
            }
        }
        return studentList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1628625923)
    public synchronized void resetStudentList() {
        studentList = null;
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
    @Generated(hash = 234091322)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSchoolDao() : null;
    }
}
