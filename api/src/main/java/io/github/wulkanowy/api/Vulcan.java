package io.github.wulkanowy.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.exams.ExamsWeek;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.messages.Messages;
import io.github.wulkanowy.api.mobile.RegisterDevice;
import io.github.wulkanowy.api.mobile.RegisteredDevices;
import io.github.wulkanowy.api.notes.AchievementsList;
import io.github.wulkanowy.api.notes.NotesList;
import io.github.wulkanowy.api.school.SchoolInfo;
import io.github.wulkanowy.api.school.TeachersInfo;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan {

    private SnP snp;

    private Client client;

    private String schoolId;

    private String studentId;

    private String diaryId;

    private static final Logger logger = LoggerFactory.getLogger(Vulcan.class);

    public void setCredentials(String email, String password, String symbol, String schoolId, String studentId, String diaryId) {
        this.schoolId = schoolId;
        this.studentId = studentId;
        this.diaryId = diaryId;

        client = new Client(email, password, symbol);

        logger.debug("Client created with symbol " + symbol);
    }

    public Client getClient() throws NotLoggedInErrorException {
        if (null == client) {
            throw new NotLoggedInErrorException("Vulcan must be initialized by calling setCredentials() prior to fetch data");
        }

        return client;
    }

    public String getSymbol() throws NotLoggedInErrorException {
        return getClient().getSymbol();

    }

    public SnP getStudentAndParent() throws VulcanException, IOException {
        if (null != this.snp) {
            return this.snp;
        }

        this.snp = new StudentAndParent(getClient(), schoolId, studentId, diaryId)
                .setUp();

        return this.snp;
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

    public RegisteredDevices getRegisteredDevices() throws VulcanException, IOException {
        return new RegisteredDevices(getStudentAndParent());
    }

    public RegisterDevice getRegisterDevice() throws VulcanException, IOException {
        return new RegisterDevice(getStudentAndParent());
    }

    public Messages getMessages() throws VulcanException {
        return new Messages(getClient());
    }
}
