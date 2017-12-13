package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.exams.ExamsWeek;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
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

    private SnP snp;

    private String protocolSchema = "https";

    private String logHost = "vulcan.net.pl";

    private String email;

    public Vulcan login(Cookies cookies, String symbol) {
        this.cookies = cookies;
        this.symbol = symbol;

        return this;
    }

    public Vulcan login(String email, String password, String symbol)
            throws BadCredentialsException, AccountPermissionException,
            LoginErrorException, IOException, VulcanOfflineException {
        Login login = getLoginObject();

        setFullEndpointInfo(email);
        login.setProtocolSchema(protocolSchema);
        login.setLogHost(logHost);

        String realSymbol = login.login(this.email, password, symbol);

        login(login.getCookiesObject(), realSymbol);

        return this;
    }

    public Vulcan login(String email, String password, String symbol, String id)
            throws BadCredentialsException, AccountPermissionException,
            LoginErrorException, IOException, VulcanOfflineException {
        login(email, password, symbol);

        this.id = id;

        return this;
    }

    public String getProtocolSchema() {
        return protocolSchema;
    }

    public String getLogHost() {
        return logHost;
    }

    public String getEmail() {
        return email;
    }

    public Login getLoginObject() {
        return new Login(new Cookies());
    }

    public Vulcan setFullEndpointInfo(String email) {
        String[] creds = email.split("\\\\");

        this.email = email;

        if (creds.length >= 2) {
            String[] url = creds[0].split("://");

            this.protocolSchema = url[0];
            this.logHost = url[1];
            this.email = creds[2];
        }

        return this;
    }

    public SnP getStudentAndParent() throws IOException, NotLoggedInErrorException {
        if (null == getCookiesObject()) {
            throw new NotLoggedInErrorException();
        }

        if (null != snp) {
            return snp;
        }

        snp = createSnp(cookies, symbol, id);
        snp.setLogHost(logHost);
        snp.setProtocolSchema(protocolSchema);

        snp.storeContextCookies();

        this.cookies = snp.getCookiesObject();

        return snp;
    }

    public SnP createSnp(Cookies cookies, String symbol, String id) {
        if (null == id) {
            return new StudentAndParent(cookies, symbol);
        }

        return new StudentAndParent(cookies, symbol, id);
    }

    public AttendanceStatistics getAttendanceStatistics() throws IOException, NotLoggedInErrorException {
        return new AttendanceStatistics(getStudentAndParent());
    }

    public AttendanceTable getAttendanceTable() throws IOException, NotLoggedInErrorException {
        return new AttendanceTable(getStudentAndParent());
    }

    public ExamsWeek getExamsList() throws IOException, NotLoggedInErrorException {
        return new ExamsWeek(getStudentAndParent());
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
