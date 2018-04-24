package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Semesters",
        active = true
)
public class Semester {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "diary_id")
    private Long diaryId;

    @Property(nameInDb = "current")
    private boolean current;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "value")
    private String value;

    @ToMany(referencedJoinProperty = "semesterId")
    private List<Subject> subjectList;

    @ToMany(referencedJoinProperty = "semesterId")
    private List<Grade> gradeList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 282930393)
    private transient SemesterDao myDao;

    @Generated(hash = 1661077309)
    public Semester(Long id, Long diaryId, boolean current, String name, String value) {
        this.id = id;
        this.diaryId = diaryId;
        this.current = current;
        this.name = name;
        this.value = value;
    }

    @Generated(hash = 58335877)
    public Semester() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiaryId() {
        return this.diaryId;
    }

    public Semester setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Semester setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Semester setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean getCurrent() {
        return this.current;
    }

    public Semester setCurrent(boolean current) {
        this.current = current;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 723353662)
    public List<Subject> getSubjectList() {
        if (subjectList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SubjectDao targetDao = daoSession.getSubjectDao();
            List<Subject> subjectListNew = targetDao._querySemester_SubjectList(id);
            synchronized (this) {
                if (subjectList == null) {
                    subjectList = subjectListNew;
                }
            }
        }
        return subjectList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 594294258)
    public synchronized void resetSubjectList() {
        subjectList = null;
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 390330007)
    public List<Grade> getGradeList() {
        if (gradeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GradeDao targetDao = daoSession.getGradeDao();
            List<Grade> gradeListNew = targetDao._querySemester_GradeList(id);
            synchronized (this) {
                if (gradeList == null) {
                    gradeList = gradeListNew;
                }
            }
        }
        return gradeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1939990047)
    public synchronized void resetGradeList() {
        gradeList = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 676204164)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSemesterDao() : null;
    }
}
