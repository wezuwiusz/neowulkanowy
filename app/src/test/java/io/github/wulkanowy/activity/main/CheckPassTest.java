package io.github.wulkanowy.activity.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckPassTest {

    @Test
    public void testFailureLogin() throws Exception {
        String html = "<div class='ErrorMessage center'>Zła nazwa użytkownika lub hasło</div>";
        Document doc = Jsoup.parse(html);
        CheckPass obj = new CheckPass(doc);

        assertFalse(obj.isLogged());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        String html = "<title>Working...</title>";
        Document doc = Jsoup.parse(html);
        CheckPass check = new CheckPass(doc);

        assertTrue(check.isLogged());
    }
}
