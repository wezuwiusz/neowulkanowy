package io.github.wulkanowy.api.school;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.mockito.Mockito;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.StudentAndParent;

public class SchoolTest {

    protected School school;
    protected StudentAndParent snp;
    private String fixtureFileName = "Szkola.html";

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document schoolPageDocument = Jsoup.parse(input);

        school = Mockito.mock(School.class);
        Mockito.when(school.getSchoolPageDocument()).thenReturn(schoolPageDocument);
        snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getRowDataChildValue(Mockito.any(Element.class),
                Mockito.anyInt())).thenCallRealMethod();
    }
}
