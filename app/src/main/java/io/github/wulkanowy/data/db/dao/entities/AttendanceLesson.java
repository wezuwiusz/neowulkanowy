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

    @Property(nameInDb = "day_id")
    private Long dayId;

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "number_of_lesson")
    private int number = 0;

    @Property(nameInDb = "subject")
    private String subject = "";

    @Property(nameInDb = "presence")
    private boolean presence = false;

    @Property(nameInDb = "absence_unexcused")
    private boolean absenceUnexcused = false;

    @Property(nameInDb = "absence_excused")
    private boolean absenceExcused = false;

    @Property(nameInDb = "unexcused_lateness")
    private boolean unexcusedLateness = false;

    @Property(nameInDb = "absence_for_school_reasons")
    private boolean absenceForSchoolReasons = false;

    @Property(nameInDb = "excused_lateness")
    private boolean excusedLateness = false;

    @Property(nameInDb = "exemption")
    private boolean exemption = false;

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

    @Generated(hash = 1741231228)
    public AttendanceLesson(Long id, Long dayId, String date, int number,
                            String subject, boolean presence, boolean absenceUnexcused,
                            boolean absenceExcused, boolean unexcusedLateness,
                            boolean absenceForSchoolReasons, boolean excusedLateness,
                            boolean exemption) {
        this.id = id;
        this.dayId = dayId;
        this.date = date;
        this.number = number;
        this.subject = subject;
        this.presence = presence;
        this.absenceUnexcused = absenceUnexcused;
        this.absenceExcused = absenceExcused;
        this.unexcusedLateness = unexcusedLateness;
        this.absenceForSchoolReasons = absenceForSchoolReasons;
        this.excusedLateness = excusedLateness;
        this.exemption = exemption;
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

    public boolean getPresence() {
        return this.presence;
    }

    public AttendanceLesson setPresence(boolean presence) {
        this.presence = presence;
        return this;
    }

    public boolean getAbsenceUnexcused() {
        return this.absenceUnexcused;
    }

    public AttendanceLesson setAbsenceUnexcused(boolean absenceUnexcused) {
        this.absenceUnexcused = absenceUnexcused;
        return this;
    }

    public boolean getAbsenceExcused() {
        return this.absenceExcused;
    }

    public AttendanceLesson setAbsenceExcused(boolean absenceExcused) {
        this.absenceExcused = absenceExcused;
        return this;
    }

    public boolean getUnexcusedLateness() {
        return this.unexcusedLateness;
    }

    public AttendanceLesson setUnexcusedLateness(boolean unexcusedLateness) {
        this.unexcusedLateness = unexcusedLateness;
        return this;
    }

    public boolean getAbsenceForSchoolReasons() {
        return this.absenceForSchoolReasons;
    }

    public AttendanceLesson setAbsenceForSchoolReasons(boolean absenceForSchoolReasons) {
        this.absenceForSchoolReasons = absenceForSchoolReasons;
        return this;
    }

    public boolean getExcusedLateness() {
        return this.excusedLateness;
    }

    public AttendanceLesson setExcusedLateness(boolean excusedLateness) {
        this.excusedLateness = excusedLateness;
        return this;
    }

    public boolean getExemption() {
        return this.exemption;
    }

    public AttendanceLesson setExemption(boolean exemption) {
        this.exemption = exemption;
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
