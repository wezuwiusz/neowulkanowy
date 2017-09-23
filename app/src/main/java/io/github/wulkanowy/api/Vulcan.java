package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.notes.AchievementsList;
import io.github.wulkanowy.api.notes.NotesList;
import io.github.wulkanowy.api.school.SchoolInfo;
import io.github.wulkanowy.api.school.TeachersInfo;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan extends Api {

    private String id;

    private String symbol;

    private StudentAndParent snp;

    public void login(String email, String password, String symbol)
            throws BadCredentialsException, AccountPermissionException, LoginErrorException {
        Login login = new Login(new Cookies());
        login.login(email, password, symbol);

        this.symbol = symbol;
        this.cookies = login.getCookiesObject();
    }

    public void login(String email, String password, String symbol, String id)
            throws BadCredentialsException, AccountPermissionException, LoginErrorException {
        login(email, password, symbol);

        this.id = id;
    }

    public StudentAndParent getStudentAndParent() throws IOException, NotLoggedInErrorException {
        if (null == getCookiesObject()) {
            throw new NotLoggedInErrorException();
        }

        if (null != snp) {
            return snp;
        }

        if (null == id) {
            snp = new StudentAndParent(cookies, symbol);
        } else {
            snp = new StudentAndParent(cookies, symbol, id);
        }

        snp.storeContextCookies();

        this.cookies = snp.getCookiesObject();

        return snp;
    }

    public AttendanceStatistics getAttendanceStatistics() throws IOException, NotLoggedInErrorException {
        return new AttendanceStatistics(getStudentAndParent());
    }

    public AttendanceTable getAttendanceTable() throws IOException, NotLoggedInErrorException {
        return new AttendanceTable(getStudentAndParent());
    }

    public GradesList getGradesList() throws IOException, NotLoggedInErrorException {
        return new GradesList(getStudentAndParent());
    }

    public SubjectsList getSubjectsList() throws IOException, NotLoggedInErrorException {
        return new SubjectsList(getStudentAndParent());
    }

    public AchievementsList getAchievementsList() throws IOException, NotLoggedInErrorException {
        return new AchievementsList(getStudentAndParent());
    }

    public NotesList getNotesList() throws IOException, NotLoggedInErrorException {
        return new NotesList(getStudentAndParent());
    }

    public SchoolInfo getSchoolInfo() throws IOException, NotLoggedInErrorException {
        return new SchoolInfo(getStudentAndParent());
    }

    public TeachersInfo getTeachersInfo() throws IOException, NotLoggedInErrorException {
        return new TeachersInfo(getStudentAndParent());
    }

    public Timetable getTimetable() throws IOException, NotLoggedInErrorException {
        return new Timetable(getStudentAndParent());
    }

    public BasicInformation getBasicInformation() throws IOException, NotLoggedInErrorException {
        return new BasicInformation(getStudentAndParent());
    }

    public FamilyInformation getFamilyInformation() throws IOException, NotLoggedInErrorException {
        return new FamilyInformation(getStudentAndParent());
    }
}
