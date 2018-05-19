package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Days",
        active = true,
        indexes = {@Index(value = "weekId,date", unique = true)}
)
public class Day {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "week_id")
    private Long weekId;

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "day_name")
    private String dayName = "";

    @Property(nameInDb = "free_day")
    private boolean freeDay = false;

    @Property(nameInDb = "free_day_name")
    private String freeDayName = "";

    @OrderBy("number ASC")
    @ToMany(referencedJoinProperty = "dayId")
    private List<TimetableLesson> timetableLessons;

    @OrderBy("number ASC")
    @ToMany(referencedJoinProperty = "dayId")
    private List<AttendanceLesson> attendanceLessons;

    @ToMany(referencedJoinProperty = "dayId")
    private List<Exam> exams;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 312167767)
    private transient DayDao myDao;

    @Generated(hash = 523139020)
    public Day(Long id, Long weekId, String date, String dayName, boolean freeDay,
               String freeDayName) {
        this.id = id;
        this.weekId = weekId;
        this.date = date;
        this.dayName = dayName;
        this.freeDay = freeDay;
        this.freeDayName = freeDayName;
    }

    @Generated(hash = 866989762)
    public Day() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWeekId() {
        return this.weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public String getDate() {
        return this.date;
    }

    public Day setDate(String date) {
        this.date = date;
        return this;
    }

    public String getDayName() {
        return this.dayName;
    }

    public Day setDayName(String dayName) {
        this.dayName = dayName;
        return this;
    }

    public boolean getFreeDay() {
        return this.freeDay;
    }

    public Day setFreeDay(boolean freeDay) {
        this.freeDay = freeDay;
        return this;
    }

    public String getFreeDayName() {
        return this.freeDayName;
    }

    public Day setFreeDayName(String freeDayName) {
        this.freeDayName = freeDayName;
        return this;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 218588195)
    public List<TimetableLesson> getTimetableLessons() {
        if (timetableLessons == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TimetableLessonDao targetDao = daoSession.getTimetableLessonDao();
            List<TimetableLesson> timetableLessonsNew = targetDao
                    ._queryDay_TimetableLessons(id);
            synchronized (this) {
                if (timetableLessons == null) {
                    timetableLessons = timetableLessonsNew;
                }
            }
        }
        return timetableLessons;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1687683740)
    public synchronized void resetTimetableLessons() {
        timetableLessons = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1166820581)
    public List<AttendanceLesson> getAttendanceLessons() {
        if (attendanceLessons == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttendanceLessonDao targetDao = daoSession.getAttendanceLessonDao();
            List<AttendanceLesson> attendanceLessonsNew = targetDao
                    ._queryDay_AttendanceLessons(id);
            synchronized (this) {
                if (attendanceLessons == null) {
                    attendanceLessons = attendanceLessonsNew;
                }
            }
        }
        return attendanceLessons;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1343075564)
    public synchronized void resetAttendanceLessons() {
        attendanceLessons = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1231531946)
    public List<Exam> getExams() {
        if (exams == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ExamDao targetDao = daoSession.getExamDao();
            List<Exam> examsNew = targetDao._queryDay_Exams(id);
            synchronized (this) {
                if (exams == null) {
                    exams = examsNew;
                }
            }
        }
        return exams;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 841969952)
    public synchronized void resetExams() {
        exams = null;
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
    @Generated(hash = 1409317752)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDayDao() : null;
    }


}
