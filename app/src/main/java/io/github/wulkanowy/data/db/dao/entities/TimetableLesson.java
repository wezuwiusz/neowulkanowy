package io.github.wulkanowy.data.db.dao.entities;

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
        indexes = {@Index(value = "dayId,date,startTime,endTime", unique = true)}
)
public class TimetableLesson implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "DAY_ID")
    private Long dayId;

    @Property(nameInDb = "NUMBER_OF_LESSON")
    private String number;

    @Property(nameInDb = "SUBJECT_NAME")
    private String subject = "";

    @Property(nameInDb = "TEACHER")
    private String teacher = "";

    @Property(nameInDb = "ROOM")
    private String room = "";

    @Property(nameInDb = "DESCRIPTION")
    private String description = "";

    @Property(nameInDb = "GROUP_NAME")
    private String groupName = "";

    @Property(nameInDb = "START_TIME")
    private String startTime = "";

    @Property(nameInDb = "END_TIME")
    private String endTime = "";

    @Property(nameInDb = "DATE")
    private String date = "";

    @Property(nameInDb = "IS_EMPTY")
    private boolean isEmpty = false;

    @Property(nameInDb = "IS_DIVISION_INTO_GROUP")
    private boolean isDivisionIntoGroups = false;

    @Property(nameInDb = "IS_PLANNING")
    private boolean isPlanning = false;

    @Property(nameInDb = "IS_REALIZED")
    private boolean isRealized = false;

    @Property(nameInDb = "IS_MOVED_CANCELED")
    private boolean isMovedOrCanceled = false;

    @Property(nameInDb = "IS_NEW_MOVED_IN_CANCELED")
    private boolean isNewMovedInOrChanged = false;

    private static final long serialVersionUID = 42L;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1119360138)
    private transient TimetableLessonDao myDao;

    @Generated(hash = 627457324)
    public TimetableLesson(Long id, Long dayId, String number, String subject,
                           String teacher, String room, String description, String groupName,
                           String startTime, String endTime, String date, boolean isEmpty,
                           boolean isDivisionIntoGroups, boolean isPlanning, boolean isRealized,
                           boolean isMovedOrCanceled, boolean isNewMovedInOrChanged) {
        this.id = id;
        this.dayId = dayId;
        this.number = number;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.description = description;
        this.groupName = groupName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.isEmpty = isEmpty;
        this.isDivisionIntoGroups = isDivisionIntoGroups;
        this.isPlanning = isPlanning;
        this.isRealized = isRealized;
        this.isMovedOrCanceled = isMovedOrCanceled;
        this.isNewMovedInOrChanged = isNewMovedInOrChanged;
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

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public String getNumber() {
        return this.number;
    }

    public TimetableLesson setNumber(String number) {
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

    public String getGroupName() {
        return this.groupName;
    }

    public TimetableLesson setGroupName(String groupName) {
        this.groupName = groupName;
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

    public boolean getIsEmpty() {
        return this.isEmpty;
    }

    public TimetableLesson setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
        return this;
    }

    public boolean getIsDivisionIntoGroups() {
        return this.isDivisionIntoGroups;
    }

    public TimetableLesson setDivisionIntoGroups(boolean isDivisionIntoGroups) {
        this.isDivisionIntoGroups = isDivisionIntoGroups;
        return this;
    }

    public boolean getIsPlanning() {
        return this.isPlanning;
    }

    public TimetableLesson setPlanning(boolean isPlanning) {
        this.isPlanning = isPlanning;
        return this;
    }

    public boolean getIsRealized() {
        return this.isRealized;
    }

    public TimetableLesson setRealized(boolean isRealized) {
        this.isRealized = isRealized;
        return this;
    }

    public boolean getIsMovedOrCanceled() {
        return this.isMovedOrCanceled;
    }

    public TimetableLesson setMovedOrCanceled(boolean isMovedOrCanceled) {
        this.isMovedOrCanceled = isMovedOrCanceled;
        return this;
    }

    public boolean getIsNewMovedInOrChanged() {
        return this.isNewMovedInOrChanged;
    }

    public TimetableLesson setNewMovedInOrChanged(boolean isNewMovedInOrChanged) {
        this.isNewMovedInOrChanged = isNewMovedInOrChanged;
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

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public void setIsDivisionIntoGroups(boolean isDivisionIntoGroups) {
        this.isDivisionIntoGroups = isDivisionIntoGroups;
    }

    public void setIsPlanning(boolean isPlanning) {
        this.isPlanning = isPlanning;
    }

    public void setIsRealized(boolean isRealized) {
        this.isRealized = isRealized;
    }

    public void setIsMovedOrCanceled(boolean isMovedOrCanceled) {
        this.isMovedOrCanceled = isMovedOrCanceled;
    }

    public void setIsNewMovedInOrChanged(boolean isNewMovedInOrChanged) {
        this.isNewMovedInOrChanged = isNewMovedInOrChanged;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1885258429)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTimetableLessonDao() : null;
    }
}
