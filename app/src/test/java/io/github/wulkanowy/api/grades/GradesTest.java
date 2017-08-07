package io.github.wulkanowy.api.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mockito.Mockito;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.Semester;
import io.github.wulkanowy.api.StudentAndParent;

public class GradesTest {

    protected StudentAndParent snp;

    public void setUp(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
        Document gradesPageDocument = Jsoup.parse(input);

        snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString()))
                .thenReturn(gradesPageDocument);
        Mockito.when(snp.getSemesters(Mockito.any(Document.class))).thenCallRealMethod();
        Mockito.when(snp.getCurrentSemester(Mockito.anyListOf(Semester.class)))
                .thenCallRealMethod();
    }
}
