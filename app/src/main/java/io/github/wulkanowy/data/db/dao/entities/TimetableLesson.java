package io.github.wulkanowy.data.db.dao.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(
        nameInDb = "TimetableLessons",
        active = true,
        indexes = {@Index(value = "dayId,date,number,startTime,endTime", unique = true)}
)
public class TimetableLesson implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "day_id")
    private Long dayId;

    @Property(nameInDb = "number")
    private int number = 0;

    @Property(nameInDb = "subject")
    private String subject = "";

    @Property(nameInDb = "teacher")
    private String teacher = "";

    @Property(nameInDb = "room")
    private String room = "";

    @Property(nameInDb = "description")
    private String description = "";

    @Property(nameInDb = "group")
    private String group = "";

    @Property(nameInDb = "start_time")
    private String startTime = "";

    @Property(nameInDb = "end_time")
    private String endTime = "";

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "empty")
    private boolean empty = false;

    @Property(nameInDb = "division_into_groups")
    private boolean divisionIntoGroups = false;

    @Property(nameInDb = "planning")
    private boolean planning = false;

    @Property(nameInDb = "realized")
    private boolean realized = false;

    @Property(nameInDb = "moved_canceled")
    private boolean movedOrCanceled = false;

    @Property(nameInDb = "new_moved_in_canceled")
    private boolean newMovedInOrChanged = false;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1119360138)
    private transient TimetableLessonDao myDao;

    @Generated(hash = 1665905034)
    public TimetableLesson(Long id, Long dayId, int number, String subject, String teacher,
                           String room, String description, String group, String startTime, String endTime,
                           String date, boolean empty, boolean divisionIntoGroups, boolean planning,
                           boolean realized, boolean movedOrCanceled, boolean newMovedInOrChanged) {
        this.id = id;
        this.dayId = dayId;
        this.number = number;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.description = description;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.empty = empty;
        this.divisionIntoGroups = divisionIntoGroups;
        this.planning = planning;
        this.realized = realized;
        this.movedOrCanceled = movedOrCanceled;
        this.newMovedInOrChanged = newMovedInOrChanged;
    }

    @Generated(hash = 1878030142)
    public TimetableLesson() {
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

    public TimetableLesson setDayId(Long dayId) {
        this.dayId = dayId;
        return this;
    }

    public int getNumber() {
        return this.number;
    }

    public TimetableLesson setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getSubject() {
        return this.subject;
    }

    public TimetableLesson setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public TimetableLesson setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getRoom() {
        return this.room;
    }

    public TimetableLesson setRoom(String room) {
        this.room = room;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public TimetableLesson setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getGroup() {
        return this.group;
    }

    public TimetableLesson setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public TimetableLesson setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public TimetableLesson setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getDate() {
        return this.date;
    }

    public TimetableLesson setDate(String date) {
        this.date = date;
        return this;
    }

    public boolean getEmpty() {
        return this.empty;
    }

    public TimetableLesson setEmpty(boolean empty) {
        this.empty = empty;
        return this;
    }

    public boolean getDivisionIntoGroups() {
        return this.divisionIntoGroups;
    }

    public TimetableLesson setDivisionIntoGroups(boolean divisionIntoGroups) {
        this.divisionIntoGroups = divisionIntoGroups;
        return this;
    }

    public boolean getPlanning() {
        return this.planning;
    }

    public TimetableLesson setPlanning(boolean planning) {
        this.planning = planning;
        return this;
    }

    public boolean getRealized() {
        return this.realized;
    }

    public TimetableLesson setRealized(boolean realized) {
        this.realized = realized;
        return this;
    }

    public boolean getMovedOrCanceled() {
        return this.movedOrCanceled;
    }

    public TimetableLesson setMovedOrCanceled(boolean movedOrCanceled) {
        this.movedOrCanceled = movedOrCanceled;
        return this;
    }

    public boolean getNewMovedInOrChanged() {
        return this.newMovedInOrChanged;
    }

    public TimetableLesson setNewMovedInOrChanged(boolean newMovedInOrChanged) {
        this.newMovedInOrChanged = newMovedInOrChanged;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimetableLesson lesson = (TimetableLesson) o;

        return new EqualsBuilder()
                .append(number, lesson.number)
                .append(startTime, lesson.startTime)
                .append(endTime, lesson.endTime)
                .append(date, lesson.date)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(number)
                .append(startTime)
                .append(endTime)
                .append(date)
                .toHashCode();
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
    @Generated(hash = 1885258429)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTimetableLessonDao() : null;
    }
}
