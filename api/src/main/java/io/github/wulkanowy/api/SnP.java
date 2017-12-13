package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public interface SnP {

    void setProtocolSchema(String schema);

    String getLogHost();

    void setLogHost(String hostname);

    String getSymbol();

    String getId();

    void storeContextCookies() throws IOException, NotLoggedInErrorException;

    Cookies getCookiesObject();

    String getRowDataChildValue(Element e, int index);

    Document getSnPPageDocument(String url) throws IOException;

    List<Semester> getSemesters() throws IOException;

    List<Semester> getSemesters(Document gradesPage);

    Semester getCurrentSemester(List<Semester> semesterList);
}
