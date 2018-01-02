package io.github.wulkanowy.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        nameInDb = "Lessons",
        active = true,
        indexes ={@Index(value = "dayId,date,startTime,endTime", unique = true)}
)
public class Lesson {

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

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 610143130)
    private transient LessonDao myDao;

    @Generated(hash = 140778287)
    public Lesson(Long id, Long dayId, String number, String subject, String teacher, String room,
                  String description, String groupName, String startTime, String endTime, String date,
                  boolean isEmpty, boolean isDivisionIntoGroups, boolean isPlanning, boolean isRealized,
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

    @Generated(hash = 1669664117)
    public Lesson() {
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
        return number;
    }

    public Lesson setNumber(String number) {
        this.number = number;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Lesson setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Lesson setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getRoom() {
        return room;
    }

    public Lesson setRoom(String room) {
        this.room = room;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Lesson setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public Lesson setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public Lesson setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public Lesson setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Lesson setDate(String date) {
        this.date = date;
        return this;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Lesson setEmpty(boolean empty) {
        isEmpty = empty;
        return this;
    }

    public boolean isDivisionIntoGroups() {
        return isDivisionIntoGroups;
    }

    public Lesson setDivisionIntoGroups(boolean divisionIntoGroups) {
        isDivisionIntoGroups = divisionIntoGroups;
        return this;
    }

    public boolean isPlanning() {
        return isPlanning;
    }

    public Lesson setPlanning(boolean planning) {
        isPlanning = planning;
        return this;
    }

    public boolean isRealized() {
        return isRealized;
    }

    public Lesson setRealized(boolean realized) {
        isRealized = realized;
        return this;
    }

    public boolean isMovedOrCanceled() {
        return isMovedOrCanceled;
    }

    public Lesson setMovedOrCanceled(boolean movedOrCanceled) {
        isMovedOrCanceled = movedOrCanceled;
        return this;
    }

    public boolean isNewMovedInOrChanged() {
        return isNewMovedInOrChanged;
    }

    public Lesson setNewMovedInOrChanged(boolean newMovedInOrChanged) {
        isNewMovedInOrChanged = newMovedInOrChanged;
        return this;
    }

    public boolean getIsEmpty() {
        return this.isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public boolean getIsDivisionIntoGroups() {
        return this.isDivisionIntoGroups;
    }

    public void setIsDivisionIntoGroups(boolean isDivisionIntoGroups) {
        this.isDivisionIntoGroups = isDivisionIntoGroups;
    }

    public boolean getIsPlanning() {
        return this.isPlanning;
    }

    public void setIsPlanning(boolean isPlanning) {
        this.isPlanning = isPlanning;
    }

    public boolean getIsRealized() {
        return this.isRealized;
    }

    public void setIsRealized(boolean isRealized) {
        this.isRealized = isRealized;
    }

    public boolean getIsMovedOrCanceled() {
        return this.isMovedOrCanceled;
    }

    public void setIsMovedOrCanceled(boolean isMovedOrCanceled) {
        this.isMovedOrCanceled = isMovedOrCanceled;
    }

    public boolean getIsNewMovedInOrChanged() {
        return this.isNewMovedInOrChanged;
    }

    public void setIsNewMovedInOrChanged(boolean isNewMovedInOrChanged) {
        this.isNewMovedInOrChanged = isNewMovedInOrChanged;
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
    @Generated(hash = 2078826279)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLessonDao() : null;
    }
}
