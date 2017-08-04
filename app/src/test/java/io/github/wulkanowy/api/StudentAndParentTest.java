package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;
public class StudentAndParentTest {

    private String fixtureFileName = "OcenyWszystkie-semester.html";

    private StudentAndParent snp;

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
        Document gradesPageDocument = Jsoup.parse(input);

        snp = Mockito.mock(StudentAndParent.class);
        PowerMockito.whenNew(StudentAndParent.class)
                .withArguments(Mockito.any(Cookies.class), Mockito.anyString()).thenReturn(snp);

        Mockito.when(snp.getPageByUrl(Mockito.anyString())).thenReturn(gradesPageDocument);
        Mockito.when(snp.getGradesPageUrl()).thenReturn("http://example.null");
        Mockito.when(snp.getLocationID()).thenReturn("symbol");
        Mockito.when(snp.getID()).thenReturn("123456");
        Mockito.when(snp.getSemesters()).thenCallRealMethod();
        Mockito.when(snp.getCurrentSemester(Mockito.anyListOf(Semester.class))).thenCallRealMethod();
    }

    @Test
    public void getSemestersTest() throws Exception {
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

        Assert.assertTrue(snp.getCurrentSemester(semesters).isCurrent());
        Assert.assertEquals("2", snp.getCurrentSemester(semesters).getId());
        Assert.assertEquals("1500100901", snp.getCurrentSemester(semesters).getNumber());
    }
}
