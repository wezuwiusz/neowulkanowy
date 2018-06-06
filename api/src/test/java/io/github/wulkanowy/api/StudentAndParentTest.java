package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.generic.Semester;

public class StudentAndParentTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(
                getClass().getResourceAsStream("OcenyWszystkie-semester.html"));
        Document gradesPageDocument = Jsoup.parse(input);

        client = Mockito.mock(Client.class);
        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(gradesPageDocument);
        Mockito.when(client.getPageByUrl(
                Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyMap())).thenReturn(gradesPageDocument);
    }

    @Test
    public void snpTest() {
        StudentAndParent snp = new StudentAndParent(client, "id123", null, null);
        Assert.assertEquals("id123", snp.getSchoolID());
    }

    @Test
    public void getSnpPageUrlWithIdTest() throws Exception {
        Assert.assertEquals("{schema}://uonetplus-opiekun.{host}/{symbol}/123456/",
                (new StudentAndParent(client, "123456", null, null)).getSnpHomePageUrl());
    }

    @Test
    public void getSnpPageUrlWithoutIdTest() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream("Start.html"));
        Document startPageDocument = Jsoup.parse(input);

        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(startPageDocument);
        StudentAndParent snp = new StudentAndParent(client, null, null, null);

        Assert.assertEquals("https://uonetplus-opiekun.vulcan.net.pl/symbol/534213/Start/Index/",
                snp.getSnpHomePageUrl());
    }

    @Test(expected = VulcanException.class)
    public void getSnpPageUrlWithWrongPage() throws Exception {
        Document wrongPageDocument = Jsoup.parse(
                FixtureHelper.getAsString(getClass().getResourceAsStream("OcenyWszystkie-semester.html"))
        );

        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(wrongPageDocument);
        StudentAndParent snp = new StudentAndParent(client, null, null, null);

        snp.getSnpHomePageUrl();
    }

    @Test
    public void getExtractedIDStandardTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol", null, null);
        Assert.assertEquals("123456", snp.getExtractedIdFromUrl("https://uonetplus-opiekun"
                + ".vulcan.net.pl/powiat/123456/Start/Index/"));
    }

    @Test
    public void getExtractedIDDemoTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol", null, null);
        Assert.assertEquals("demo12345",
                snp.getExtractedIdFromUrl("https://uonetplus-opiekun.vulcan.net.pl/demoupowiat/demo12345/Start/Index/"));
    }

    @Test(expected = VulcanException.class)
    public void getExtractedIDNotLoggedTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol", null, null);
        Assert.assertEquals("123",
                snp.getExtractedIdFromUrl("https://uonetplus.vulcan.net.pl/powiat/"));
    }

    @Test
    public void getSemestersTest() throws Exception {
        SnP snp = new StudentAndParent(client, "123456", null, null);
        List<Semester> semesters = snp.getSemesters();

        Assert.assertEquals(2, semesters.size());

        Assert.assertEquals("1", semesters.get(0).getName());
        Assert.assertEquals("1234", semesters.get(0).getId());
        Assert.assertFalse(semesters.get(0).isCurrent());

        Assert.assertEquals("2", semesters.get(1).getName());
        Assert.assertEquals("1235", semesters.get(1).getId());
        Assert.assertTrue(semesters.get(1).isCurrent());
    }

    @Test
    public void getCurrentSemesterTest() {
        List<Semester> semesters = new ArrayList<>();
        semesters.add(new Semester().setName("1500100900").setId("1").setCurrent(false));
        semesters.add(new Semester().setName("1500100901").setId("2").setCurrent(true));

        SnP snp = new StudentAndParent(client, "", null, null);
        Semester semester = snp.getCurrent(semesters);

        Assert.assertTrue(semester.isCurrent());
        Assert.assertEquals("2", semester.getId());
        Assert.assertEquals("1500100901", semester.getName());
    }

    @Test
    public void getCurrentSemesterFromEmptyTest() {
        SnP snp = new StudentAndParent(client, "", null, null);
        List<Semester> semesters = new ArrayList<>();

        Assert.assertNull(snp.getCurrent(semesters));
    }

    @Test
    public void getDiariesAndStudentTest() throws IOException, VulcanException {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream("WitrynaUczniaIRodzica.html"));
        Document snpHome = Jsoup.parse(input);

        client = Mockito.mock(Client.class);
        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(snpHome);
        SnP snp = new StudentAndParent(client, "", null, null);

        snp.setUp();

        Assert.assertEquals("3Ti 2017", snp.getDiaries().get(0).getName());
        Assert.assertEquals("2Ti 2016", snp.getDiaries().get(1).getName());
        Assert.assertEquals("1Ti 2015", snp.getDiaries().get(2).getName());

        Assert.assertEquals("1300", snp.getDiaries().get(0).getId());
        Assert.assertEquals("1200", snp.getDiaries().get(1).getId());
        Assert.assertEquals("1100", snp.getDiaries().get(2).getId());

        Assert.assertTrue(snp.getDiaries().get(0).isCurrent());
        Assert.assertFalse(snp.getDiaries().get(1).isCurrent());
        Assert.assertFalse(snp.getDiaries().get(2).isCurrent());

        Assert.assertEquals("Jan Kowal", snp.getStudents().get(0).getName());
        Assert.assertEquals("100", snp.getStudents().get(0).getId());
    }
}
