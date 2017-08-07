package io.github.wulkanowy.api.user;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.mockito.Mockito;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.StudentAndParent;

public class UserTest {

    protected StudentAndParent snp;

    private String fixtureFileName = "UczenDanePodstawowe.html";

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document pageDocument = Jsoup.parse(input);

        snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString())).thenReturn(pageDocument);
        Mockito.when(snp.getRowDataChildValue(Mockito.any(Element.class), Mockito.anyInt()))
                .thenCallRealMethod();
    }
}
