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

public class Vulcan {

    private String id;

    private String symbol;

    private SnP snp;

    private String protocolSchema = "https";

    private String logHost = "vulcan.net.pl";

    private String email;

    private Client client = new Client();

    private Login login = new Login(client);

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Vulcan login(String email, String password, String symbol)
            throws BadCredentialsException, AccountPermissionException,
            LoginErrorException, IOException, VulcanOfflineException {

        setFullEndpointInfo(email);
        login.setProtocolSchema(protocolSchema);
        login.setLogHost(logHost);

        this.symbol = login.login(this.email, password, symbol);

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

    private void setFullEndpointInfo(String email) {
        String[] creds = email.split("\\\\");

        this.email = email;

        if (creds.length >= 2) {
            String[] url = creds[0].split("://");

            this.protocolSchema = url[0];
            this.logHost = url[1];
            this.email = creds[2];
        }
    }

    public SnP getStudentAndParent() throws IOException, NotLoggedInErrorException {
        if (0 == client.getCookies().size()) {
            throw new NotLoggedInErrorException();
        }

        if (null != snp) {
            return snp;
        }

        snp = createSnp(client, symbol, id);
        snp.setLogHost(logHost);
        snp.setProtocolSchema(protocolSchema);

        snp.storeContextCookies();

//        this.cookies = client.getCookiesObject();

        return snp;
    }

    SnP createSnp(Client client, String symbol, String id) {
        if (null == id) {
            return new StudentAndParent(client, symbol);
        }

        return new StudentAndParent(client, symbol, id);
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
