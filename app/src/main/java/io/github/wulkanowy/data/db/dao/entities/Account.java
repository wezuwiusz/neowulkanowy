package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Accounts",
        active = true
)
public class Account {

    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    @Property(nameInDb = "REAL_ID")
    private String realId;

    @Property(nameInDb = "SYMBOL")
    private String symbol;

    @Property(nameInDb = "SCHOOL_ID")
    private String schoolId;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "E_MAIL")
    private String email;

    @Property(nameInDb = "PASSWORD")
    private String password;

    @ToMany(joinProperties = {
            @JoinProperty(name = "realId", referencedName = "studentId")
    })
    private List<Diary> diaryList;

    @ToMany(referencedJoinProperty = "userId")
    private List<Subject> subjectList;

    @ToMany(referencedJoinProperty = "userId")
    private List<Grade> gradeList;

    @ToMany(referencedJoinProperty = "userId")
    private List<Day> dayList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 335469827)
    private transient AccountDao myDao;

    @Generated(hash = 727721142)
    public Account(Long id, String realId, String symbol, String schoolId, String name,
            String email, String password) {
        this.id = id;
        this.realId = realId;
        this.symbol = symbol;
        this.schoolId = schoolId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRealId() {
        return realId;
    }

    public Account setRealId(String realId) {
        this.realId = realId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Account setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public Account setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        return this;
    }

    public Account setDiaryList(List<Diary> diaryList) {
        this.diaryList = diaryList;
        return this;
    }

    public Account setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
        return this;
    }

    public Account setGradeList(List<Grade> gradeList) {
        this.gradeList = gradeList;
        return this;
    }

    public Account setDayList(List<Day> dayList) {
        this.dayList = dayList;
        return this;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Account setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        return this;
    }

    public AccountDao getMyDao() {
        return myDao;
    }

    public Account setMyDao(AccountDao myDao) {
        this.myDao = myDao;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1472214466)
    public List<Diary> getDiaryList() {
        if (diaryList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DiaryDao targetDao = daoSession.getDiaryDao();
            List<Diary> diaryListNew = targetDao._queryAccount_DiaryList(realId);
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1800750450)
    public List<Subject> getSubjectList() {
        if (subjectList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SubjectDao targetDao = daoSession.getSubjectDao();
            List<Subject> subjectListNew = targetDao._queryAccount_SubjectList(id);
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1040074549)
    public List<Grade> getGradeList() {
        if (gradeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GradeDao targetDao = daoSession.getGradeDao();
            List<Grade> gradeListNew = targetDao._queryAccount_GradeList(id);
            synchronized (this) {
                if (gradeList == null) {
                    gradeList = gradeListNew;
                }
            }
        }
        return gradeList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1939990047)
    public synchronized void resetGradeList() {
        gradeList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 300459794)
    public List<Day> getDayList() {
        if (dayList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DayDao targetDao = daoSession.getDayDao();
            List<Day> dayListNew = targetDao._queryAccount_DayList(id);
            synchronized (this) {
                if (dayList == null) {
                    dayList = dayListNew;
                }
            }
        }
        return dayList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1010399236)
    public synchronized void resetDayList() {
        dayList = null;
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
    @Generated(hash = 1812283172)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountDao() : null;
    }
}
