package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface SnP {

    String getId();

    StudentAndParent storeContextCookies() throws IOException, VulcanException;

    String getRowDataChildValue(Element e, int index);

    Document getSnPPageDocument(String url) throws IOException, VulcanException;

    List<Semester> getSemesters() throws IOException, VulcanException;

    List<Semester> getSemesters(Document gradesPage);

    Semester getCurrentSemester(List<Semester> semesterList);
}
