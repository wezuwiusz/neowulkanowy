package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.exams.ExamsWeek;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.messages.Messages;
import io.github.wulkanowy.api.notes.AchievementsList;
import io.github.wulkanowy.api.notes.NotesList;
import io.github.wulkanowy.api.school.SchoolInfo;
import io.github.wulkanowy.api.school.TeachersInfo;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan {

    private String id;

    private SnP snp;

    private Client client;

    public void setCredentials(String email, String password, String symbol, String id) {
        client = new Client(email, password, symbol);

        this.id = id;
    }

    public Client getClient() throws NotLoggedInErrorException {
        if (null == client) {
            throw new NotLoggedInErrorException("Use setCredentials() method first");
        }

        return client;
    }

    public String getSymbol() throws NotLoggedInErrorException {
        return getClient().getSymbol();

    }

    public SnP getStudentAndParent() throws IOException, VulcanException {
        if (null != this.snp) {
            return this.snp;
        }

        this.snp = new StudentAndParent(getClient(), id).storeContextCookies();

        return this.snp;
    }

    public String getId() throws IOException, VulcanException {
        return getStudentAndParent().getId();
    }

    public AttendanceTable getAttendanceTable() throws IOException, VulcanException {
        return new AttendanceTable(getStudentAndParent());
    }

    public AttendanceStatistics getAttendanceStatistics() throws IOException, VulcanException {
        return new AttendanceStatistics(getStudentAndParent());
    }

    public ExamsWeek getExamsList() throws IOException, VulcanException {
        return new ExamsWeek(getStudentAndParent());
    }

    public GradesList getGradesList() throws IOException, VulcanException {
        return new GradesList(getStudentAndParent());
    }

    public SubjectsList getSubjectsList() throws IOException, VulcanException {
        return new SubjectsList(getStudentAndParent());
    }

    public AchievementsList getAchievementsList() throws IOException, VulcanException {
        return new AchievementsList(getStudentAndParent());
    }

    public NotesList getNotesList() throws IOException, VulcanException {
        return new NotesList(getStudentAndParent());
    }

    public SchoolInfo getSchoolInfo() throws IOException, VulcanException {
        return new SchoolInfo(getStudentAndParent());
    }

    public TeachersInfo getTeachersInfo() throws IOException, VulcanException {
        return new TeachersInfo(getStudentAndParent());
    }

    public Timetable getTimetable() throws IOException, VulcanException {
        return new Timetable(getStudentAndParent());
    }

    public BasicInformation getBasicInformation() throws IOException, VulcanException {
        return new BasicInformation(getStudentAndParent());
    }

    public FamilyInformation getFamilyInformation() throws IOException, VulcanException {
        return new FamilyInformation(getStudentAndParent());
    }

    public Messages getMessages() throws VulcanException {
        return new Messages(getClient());
    }
}
