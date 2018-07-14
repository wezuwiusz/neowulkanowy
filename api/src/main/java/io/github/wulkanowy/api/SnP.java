package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import io.github.wulkanowy.api.generic.Diary;
import io.github.wulkanowy.api.generic.ParamItem;
import io.github.wulkanowy.api.generic.Semester;
import io.github.wulkanowy.api.generic.Student;

public interface SnP {

    void setDiaryID(String id);

    String getStudentID();

    List<Student> getStudents() throws IOException, VulcanException;

    StudentAndParent setUp() throws IOException, VulcanException;

    String getRowDataChildValue(Element e, int index);

    Document getSnPPageDocument(String url) throws IOException, VulcanException;

    List<Diary> getDiaries() throws IOException, VulcanException;

    List<Semester> getSemesters() throws IOException, VulcanException;

    List<Semester> getSemesters(Document gradesPage);

    <T> T getCurrent(List<? extends ParamItem> list);
}
