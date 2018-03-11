package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class StudentAndParentTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(
                getClass().getResourceAsStream("OcenyWszystkie-semester.html"));
        Document gradesPageDocument = Jsoup.parse(input);

        client = Mockito.mock(Client.class);
        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(gradesPageDocument);
    }

    @Test
    public void snpTest() throws Exception {
        StudentAndParent snp = new StudentAndParent(client, "id123");
        Assert.assertEquals("id123", snp.getId());
    }

    @Test
    public void getSnpPageUrlWithIdTest() throws Exception {
        Assert.assertEquals("{schema}://uonetplus-opiekun.{host}/{symbol}/123456/",
                (new StudentAndParent(client, "123456")).getSnpHomePageUrl());
    }

    @Test
    public void getSnpPageUrlWithoutIdTest() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream("Start.html"));
        Document startPageDocument = Jsoup.parse(input);

        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(startPageDocument);
        StudentAndParent snp = new StudentAndParent(client, null);

        Assert.assertEquals("https://uonetplus-opiekun.vulcan.net.pl/symbol/534213/Start/Index/",
                snp.getSnpHomePageUrl());
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getSnpPageUrlWithWrongPage() throws Exception {
        Document wrongPageDocument = Jsoup.parse(
                FixtureHelper.getAsString(getClass().getResourceAsStream("OcenyWszystkie-semester.html"))
        );

        Mockito.when(client.getPageByUrl(Mockito.anyString())).thenReturn(wrongPageDocument);
        StudentAndParent snp = new StudentAndParent(client, null);

        snp.getSnpHomePageUrl();
    }

    @Test
    public void getExtractedIDStandardTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol");
        Assert.assertEquals("123456", snp.getExtractedIdFromUrl("https://uonetplus-opiekun"
                + ".vulcan.net.pl/powiat/123456/Start/Index/"));
    }

    @Test
    public void getExtractedIDDemoTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol");
        Assert.assertEquals("demo12345",
                snp.getExtractedIdFromUrl("https://uonetplus-opiekun.vulcan.net.pl/demoupowiat/demo12345/Start/Index/"));
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getExtractedIDNotLoggedTest() throws Exception {
        Mockito.when(client.getHost()).thenReturn("vulcan.net.pl");
        StudentAndParent snp = new StudentAndParent(client, "symbol");
        Assert.assertEquals("123",
                snp.getExtractedIdFromUrl("https://uonetplus.vulcan.net.pl/powiat/"));
    }

    @Test
    public void getSemestersTest() throws Exception {
        SnP snp = new StudentAndParent(client, "123456");
        List<Semester> semesters = snp.getSemesters();

        Assert.assertEquals(2, semesters.size());

        Assert.assertEquals("1", semesters.get(0).getId());
        Assert.assertEquals("1234", semesters.get(0).getNumber());
        Assert.assertFalse(semesters.get(0).isCurrent());

        Assert.assertEquals("2", semesters.get(1).getId());
        Assert.assertEquals("1235", semesters.get(1).getNumber());
        Assert.assertTrue(semesters.get(1).isCurrent());
    }

    @Test
    public void getCurrentSemesterTest() throws Exception {
        List<Semester> semesters = new ArrayList<>();
        semesters.add(new Semester().setNumber("1500100900").setId("1").setCurrent(false));
        semesters.add(new Semester().setNumber("1500100901").setId("2").setCurrent(true));

        SnP snp = new StudentAndParent(client, "");
        Assert.assertTrue(snp.getCurrentSemester(semesters).isCurrent());
        Assert.assertEquals("2", snp.getCurrentSemester(semesters).getId());
        Assert.assertEquals("1500100901", snp.getCurrentSemester(semesters).getNumber());
    }

    @Test
    public void getCurrentSemesterFromEmptyTest() throws Exception {
        SnP snp = new StudentAndParent(client, "");
        List<Semester> semesters = new ArrayList<>();

        Assert.assertNull(snp.getCurrentSemester(semesters));
    }
}
