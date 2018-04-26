package io.github.wulkanowy.data.db.dao.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(
        nameInDb = "Grades",
        active = true
)
public class Grade implements Serializable {

    @Id(autoincrement = true)
    protected Long id;

    @Property(nameInDb = "semester_id")
    private Long semesterId;

    @Property(nameInDb = "subject_id")
    private Long subjectId;

    @Property(nameInDb = "subject")
    private String subject = "";

    @Property(nameInDb = "value")
    protected String value = "";

    @Property(nameInDb = "weight")
    private String weight = "";

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "symbol")
    private String symbol = "";

    @Property(nameInDb = "color")
    private String color = "";

    @Property(nameInDb = "description")
    private String description = "";

    @Property(nameInDb = "teacher")
    private String teacher = "";

    @Property(nameInDb = "is_new")
    private boolean isNew = false;

    @Property(nameInDb = "read")
    private boolean read = true;

    private static final long serialVersionUID = 42L;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 681281562)
    private transient GradeDao myDao;

    @Generated(hash = 2042976393)
    public Grade() {
    }

    @Generated(hash = 619853992)
    public Grade(Long id, Long semesterId, Long subjectId, String subject, String value,
                 String weight, String date, String symbol, String color, String description,
                 String teacher, boolean isNew, boolean read) {
        this.id = id;
        this.semesterId = semesterId;
        this.subjectId = subjectId;
        this.subject = subject;
        this.value = value;
        this.weight = weight;
        this.date = date;
        this.symbol = symbol;
        this.color = color;
        this.description = description;
        this.teacher = teacher;
        this.isNew = isNew;
        this.read = read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        return new EqualsBuilder()
                .append(semesterId, grade.semesterId)
                .append(subject, grade.subject)
                .append(value, grade.value)
                .append(color, grade.color)
                .append(symbol, grade.symbol)
                .append(description, grade.description)
                .append(weight, grade.weight)
                .append(date, grade.date)
                .append(teacher, grade.teacher)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(semesterId)
                .append(subject)
                .append(value)
                .append(color)
                .append(symbol)
                .append(description)
                .append(weight)
                .append(date)
                .append(teacher)
                .toHashCode();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public Grade setValue(String value) {
        this.value = value;
        return this;
    }

    public Long getSemesterId() {
        return this.semesterId;
    }

    public Grade setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
        return this;
    }

    public String getSubject() {
        return this.subject;
    }

    public Grade setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getColor() {
        return this.color;
    }

    public Grade setColor(String color) {
        this.color = color;
        return this;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Grade setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Grade setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getWeight() {
        return this.weight;
    }

    public Grade setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getDate() {
        return this.date;
    }

    public Grade setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public Grade setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getRead() {
        return this.read;
    }

    public Grade setRead(boolean read) {
        this.read = read;
        return this;
    }


    public Long getSubjectId() {
        return this.subjectId;
    }


    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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
    @Generated(hash = 1187286414)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGradeDao() : null;
    }
}
