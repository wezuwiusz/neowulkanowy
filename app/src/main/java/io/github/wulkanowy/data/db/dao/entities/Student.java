package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Students",
        active = true
)
public class Student {

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

    @ToMany(referencedJoinProperty = "studentId")
    private List<Diary> diaryList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1943931642)
    private transient StudentDao myDao;

    @Generated(hash = 1334215952)
    public Student(Long id, Long symbolId, boolean current, String realId, String name) {
        this.id = id;
        this.symbolId = symbolId;
        this.current = current;
        this.realId = realId;
        this.name = name;
    }

    @Generated(hash = 1556870573)
    public Student() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSymbolId() {
        return this.symbolId;
    }

    public Student setSymbolId(Long symbolId) {
        this.symbolId = symbolId;
        return this;
    }

    public String getRealId() {
        return this.realId;
    }

    public Student setRealId(String realId) {
        this.realId = realId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public boolean getCurrent() {
        return this.current;
    }

    public Student setCurrent(boolean current) {
        this.current = current;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 508305571)
    public List<Diary> getDiaryList() {
        if (diaryList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DiaryDao targetDao = daoSession.getDiaryDao();
            List<Diary> diaryListNew = targetDao._queryStudent_DiaryList(id);
            synchronized (this) {
                if (diaryList == null) {
                    diaryList = diaryListNew;
                }
            }
        }
        return diaryList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1078514341)
    public synchronized void resetDiaryList() {
        diaryList = null;
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
    @Generated(hash = 1701634981)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStudentDao() : null;
    }

}
