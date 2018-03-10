package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity(
        nameInDb = "AttendanceLessons",
        active = true,
        indexes = {@Index(value = "dayId,date,number", unique = true)}
)
public class AttendanceLesson implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "DAY_ID")
    private Long dayId;

    @Property(nameInDb = "DATE")
    private String date = "";

    @Property(nameInDb = "NUMBER_OF_LESSON")
    private int number = 0;

    @Property(nameInDb = "SUBJECT_NAME")
    private String subject = "";

    @Property(nameInDb = "IS_PRESENCE")
    private boolean isPresence = false;

    @Property(nameInDb = "IS_ABSENCE_UNEXCUSED")
    private boolean isAbsenceUnexcused = false;

    @Property(nameInDb = "IS_ABSENCE_EXCUSED")
    private boolean isAbsenceExcused = false;

    @Property(nameInDb = "IS_UNEXCUSED_LATENESS")
    private boolean isUnexcusedLateness = false;

    @Property(nameInDb = "IS_ABSENCE_FOR_SCHOOL_REASONS")
    private boolean isAbsenceForSchoolReasons = false;

    @Property(nameInDb = "IS_EXCUSED_LATENESS")
    private boolean isExcusedLateness = false;

    @Property(nameInDb = "IS_EXEMPTION")
    private boolean isExemption = false;

    @Transient
    private String description = "";

    private static final long serialVersionUID = 42L;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1936953859)
    private transient AttendanceLessonDao myDao;

    @Generated(hash = 1428129046)
    public AttendanceLesson(Long id, Long dayId, String date, int number,
                            String subject, boolean isPresence, boolean isAbsenceUnexcused,
                            boolean isAbsenceExcused, boolean isUnexcusedLateness,
                            boolean isAbsenceForSchoolReasons, boolean isExcusedLateness,
                            boolean isExemption) {
        this.id = id;
        this.dayId = dayId;
        this.date = date;
        this.number = number;
        this.subject = subject;
        this.isPresence = isPresence;
        this.isAbsenceUnexcused = isAbsenceUnexcused;
        this.isAbsenceExcused = isAbsenceExcused;
        this.isUnexcusedLateness = isUnexcusedLateness;
        this.isAbsenceForSchoolReasons = isAbsenceForSchoolReasons;
        this.isExcusedLateness = isExcusedLateness;
        this.isExemption = isExemption;
    }

    @Generated(hash = 921806575)
    public AttendanceLesson() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayId() {
        return this.dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public String getDate() {
        return this.date;
    }

    public AttendanceLesson setDate(String date) {
        this.date = date;
        return this;
    }

    public int getNumber() {
        return this.number;
    }

    public AttendanceLesson setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getSubject() {
        return this.subject;
    }

    public AttendanceLesson setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public boolean getIsPresence() {
        return this.isPresence;
    }

    public AttendanceLesson setIsPresence(boolean isPresence) {
        this.isPresence = isPresence;
        return this;
    }

    public boolean getIsAbsenceUnexcused() {
        return this.isAbsenceUnexcused;
    }

    public AttendanceLesson setIsAbsenceUnexcused(boolean isAbsenceUnexcused) {
        this.isAbsenceUnexcused = isAbsenceUnexcused;
        return this;
    }

    public boolean getIsAbsenceExcused() {
        return this.isAbsenceExcused;
    }

    public AttendanceLesson setIsAbsenceExcused(boolean isAbsenceExcused) {
        this.isAbsenceExcused = isAbsenceExcused;
        return this;
    }

    public boolean getIsUnexcusedLateness() {
        return this.isUnexcusedLateness;
    }

    public AttendanceLesson setIsUnexcusedLateness(boolean isUnexcusedLateness) {
        this.isUnexcusedLateness = isUnexcusedLateness;
        return this;
    }

    public boolean getIsAbsenceForSchoolReasons() {
        return this.isAbsenceForSchoolReasons;
    }

    public AttendanceLesson setIsAbsenceForSchoolReasons(boolean isAbsenceForSchoolReasons) {
        this.isAbsenceForSchoolReasons = isAbsenceForSchoolReasons;
        return this;
    }

    public boolean getIsExcusedLateness() {
        return this.isExcusedLateness;
    }

    public AttendanceLesson setIsExcusedLateness(boolean isExcusedLateness) {
        this.isExcusedLateness = isExcusedLateness;
        return this;
    }

    public boolean getIsExemption() {
        return this.isExemption;
    }

    public AttendanceLesson setIsExemption(boolean isExemption) {
        this.isExemption = isExemption;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AttendanceLesson setDescription(String description) {
        this.description = description;
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
    @Generated(hash = 1157101112)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAttendanceLessonDao() : null;
    }
}
