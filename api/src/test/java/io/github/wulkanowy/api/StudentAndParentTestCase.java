package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mockito.Mockito;

import io.github.wulkanowy.api.generic.Semester;

public abstract class StudentAndParentTestCase {

    protected StudentAndParent getSnp(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document tablePageDocument = Jsoup.parse(input);

        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString()))
                .thenReturn(tablePageDocument);
        Mockito.when(snp.getSemesters(Mockito.any(Document.class))).thenCallRealMethod();
        Mockito.when(snp.getCurrent(Mockito.<Semester>anyList()))
                .thenCallRealMethod();
        Mockito.when(snp.getRowDataChildValue(Mockito.any(Element.class),
                Mockito.anyInt())).thenCallRealMethod();

        return snp;
    }
}
