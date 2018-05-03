package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Weeks",
        active = true,
        indexes = {@Index(value = "diaryId,startDayDate", unique = true)}
)
public class Week {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "diary_id")
    private Long diaryId;

    @Property(nameInDb = "start_day_date")
    private String startDayDate = "";

    @Property(nameInDb = "attendance_synced")
    private boolean attendanceSynced = false;

    @Property(nameInDb = "timetable_synced")
    private boolean timetableSynced = false;

    @Property(nameInDb = "exams_synced")
    private boolean examsSynced = false;

    @ToMany(referencedJoinProperty = "weekId")
    private List<Day> dayList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1019310398)
    private transient WeekDao myDao;

    @Generated(hash = 23357599)
    public Week(Long id, Long diaryId, String startDayDate, boolean attendanceSynced,
                boolean timetableSynced, boolean examsSynced) {
        this.id = id;
        this.diaryId = diaryId;
        this.startDayDate = startDayDate;
        this.attendanceSynced = attendanceSynced;
        this.timetableSynced = timetableSynced;
        this.examsSynced = examsSynced;
    }

    @Generated(hash = 2135529658)
    public Week() {
    }

    public Long getId() {
        return id;
    }

    public Week setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Week setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
        return this;
    }

    public String getStartDayDate() {
        return startDayDate;
    }

    public Week setStartDayDate(String startDayDate) {
        this.startDayDate = startDayDate;
        return this;
    }

    public boolean getAttendanceSynced() {
        return this.attendanceSynced;
    }

    public Week setAttendanceSynced(boolean attendanceSynced) {
        this.attendanceSynced = attendanceSynced;
        return this;
    }

    public boolean getTimetableSynced() {
        return this.timetableSynced;
    }

    public Week setTimetableSynced(boolean timetableSynced) {
        this.timetableSynced = timetableSynced;
        return this;
    }

    public Week setExamsSynced(boolean examsSynced) {
        this.examsSynced = examsSynced;
        return this;
    }

    public boolean getExamsSynced() {
        return examsSynced;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1562119145)
    public List<Day> getDayList() {
        if (dayList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DayDao targetDao = daoSession.getDayDao();
            List<Day> dayListNew = targetDao._queryWeek_DayList(id);
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
    @Generated(hash = 665278367)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWeekDao() : null;
    }
}
