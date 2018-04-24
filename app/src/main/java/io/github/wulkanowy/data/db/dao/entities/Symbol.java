package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Symbols",
        active = true
)
public class Symbol {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "user_id")
    private Long userId;

    @Property(nameInDb = "host")
    private String host;

    @Property(nameInDb = "school_id")
    private String schoolId;

    @Property(nameInDb = "symbol")
    private String symbol;

    @Property(nameInDb = "type")
    private String type;

    @ToMany(referencedJoinProperty = "symbolId")
    private List<Student> studentList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 684907977)
    private transient SymbolDao myDao;

    @Generated(hash = 242774339)
    public Symbol(Long id, Long userId, String host, String schoolId, String symbol,
                  String type) {
        this.id = id;
        this.userId = userId;
        this.host = host;
        this.schoolId = schoolId;
        this.symbol = symbol;
        this.type = type;
    }

    @Generated(hash = 460475327)
    public Symbol() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Symbol setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public Symbol setHost(String host) {
        this.host = host;
        return this;
    }

    public String getSchoolId() {
        return this.schoolId;
    }

    public Symbol setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        return this;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Symbol setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public Symbol setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 604366458)
    public List<Student> getStudentList() {
        if (studentList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StudentDao targetDao = daoSession.getStudentDao();
            List<Student> studentListNew = targetDao._querySymbol_StudentList(id);
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
    @Generated(hash = 632145708)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSymbolDao() : null;
    }
}
