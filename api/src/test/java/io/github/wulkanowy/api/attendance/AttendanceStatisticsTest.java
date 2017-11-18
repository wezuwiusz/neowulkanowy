package io.github.wulkanowy.api.attendance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class AttendanceStatisticsTest extends StudentAndParentTestCase {

    private AttendanceStatistics excellent;

    private AttendanceStatistics full;

    @Before
    public void setUp() throws Exception {
        this.excellent = new AttendanceStatistics(getSnp("Frekwencja-excellent.html"));
        this.full = new AttendanceStatistics(getSnp("Frekwencja-full.html"));
    }

    @Test
    public void getSubjectList() throws Exception {
        Assert.assertEquals(26, excellent.getSubjectList().size());
        Assert.assertEquals(23, full.getSubjectList().size());
    }

    @Test
    public void getSubjectListId() throws Exception {
        Assert.assertEquals(-1, excellent.getSubjectList().get(0).getId());
        Assert.assertEquals(63, excellent.getSubjectList().get(10).getId());
        Assert.assertEquals(0, excellent.getSubjectList().get(25).getId());

        Assert.assertEquals(-1, full.getSubjectList().get(0).getId());
        Assert.assertEquals(108, full.getSubjectList().get(14).getId());
        Assert.assertEquals(492, full.getSubjectList().get(21).getId());
    }

    @Test
    public void getSubjectListName() throws Exception {
        Assert.assertEquals("Wszystkie", excellent.getSubjectList().get(0).getName());
        Assert.assertEquals("Fizyka", excellent.getSubjectList().get(8).getName());
        Assert.assertEquals("Sieci komputerowe i administrowanie sieciami",
                excellent.getSubjectList().get(21).getName());

        Assert.assertEquals("Praktyka zawodowa", full.getSubjectList().get(11).getName());
        Assert.assertEquals("Użytkowanie urządzeń peryferyjnych komputera",
                full.getSubjectList().get(16).getName());
        Assert.assertEquals("Brak opisu lekcji", full.getSubjectList().get(22).getName());
    }

    @Test
    public void getTypesTotal() throws Exception {
        Assert.assertEquals(100.0, excellent.getTypesTable().getTotal(), 0);
        Assert.assertEquals(80.94, full.getTypesTable().getTotal(), 0);
    }

    @Test
    public void getTypeName() throws Exception {
        List<Type> typeList1 = excellent.getTypesTable().getTypeList();
        Assert.assertEquals("Obecność", typeList1.get(0).getName());
        Assert.assertEquals("Nieobecność nieusprawiedliwiona", typeList1.get(1).getName());
        Assert.assertEquals("Nieobecność usprawiedliwiona", typeList1.get(2).getName());
        Assert.assertEquals("Nieobecność z przyczyn szkolnych", typeList1.get(3).getName());

        List<Type> typeList2 = full.getTypesTable().getTypeList();
        Assert.assertEquals("Spóźnienie nieusprawiedliwione", typeList2.get(4).getName());
        Assert.assertEquals("Spóźnienie usprawiedliwione", typeList2.get(5).getName());
        Assert.assertEquals("Zwolnienie", typeList2.get(6).getName());
    }

    @Test
    public void getTypeTotal() throws Exception {
        List<Type> typeList1 = excellent.getTypesTable().getTypeList();
        Assert.assertEquals(1211, typeList1.get(0).getTotal());
        Assert.assertEquals(0, typeList1.get(1).getTotal());
        Assert.assertEquals(0, typeList1.get(2).getTotal());
        Assert.assertEquals(0, typeList1.get(3).getTotal());
        Assert.assertEquals(0, typeList1.get(4).getTotal());
        Assert.assertEquals(0, typeList1.get(5).getTotal());
        Assert.assertEquals(0, typeList1.get(6).getTotal());

        List<Type> typeList2 = full.getTypesTable().getTypeList();
        Assert.assertEquals(822, typeList2.get(0).getTotal());
        Assert.assertEquals(6, typeList2.get(1).getTotal());
        Assert.assertEquals(192, typeList2.get(2).getTotal());
        Assert.assertEquals(7, typeList2.get(3).getTotal());
        Assert.assertEquals(12, typeList2.get(4).getTotal());
        Assert.assertEquals(1, typeList2.get(5).getTotal());
        Assert.assertEquals(2, typeList2.get(6).getTotal());
    }

    @Test
    public void getTypeList() throws Exception {
        List<Type> typesList1 = excellent.getTypesTable().getTypeList();
        Assert.assertEquals(12, typesList1.get(0).getMonthList().size());
        Assert.assertEquals(12, typesList1.get(5).getMonthList().size());

        List<Type> typesList2 = full.getTypesTable().getTypeList();
        Assert.assertEquals(12, typesList2.get(0).getMonthList().size());
        Assert.assertEquals(12, typesList2.get(5).getMonthList().size());
    }

    @Test
    public void getMonthList() throws Exception {
        List<Type> typeList1 = excellent.getTypesTable().getTypeList();
        Assert.assertEquals(12, typeList1.get(0).getMonthList().size());

        List<Type> typeList2 = full.getTypesTable().getTypeList();
        Assert.assertEquals(12, typeList2.get(0).getMonthList().size());
    }

    @Test
    public void getMonthName() throws Exception {
        List<Month> monthsList1 = excellent.getTypesTable().getTypeList().get(0).getMonthList();
        Assert.assertEquals("IX", monthsList1.get(0).getName());
        Assert.assertEquals("III", monthsList1.get(6).getName());
        Assert.assertEquals("VIII", monthsList1.get(11).getName());

        List<Month> monthsList2 = full.getTypesTable().getTypeList().get(0).getMonthList();
        Assert.assertEquals("XI", monthsList2.get(2).getName());
        Assert.assertEquals("II", monthsList2.get(5).getName());
        Assert.assertEquals("VI", monthsList2.get(9).getName());
    }

    @Test
    public void getMonthValue() throws Exception {
        List<Month> monthsList1 = excellent.getTypesTable().getTypeList().get(0).getMonthList();
        Assert.assertEquals(142, monthsList1.get(0).getValue());
        Assert.assertEquals(131, monthsList1.get(4).getValue());
        Assert.assertEquals(139, monthsList1.get(7).getValue());
        Assert.assertEquals(114, monthsList1.get(9).getValue());
        Assert.assertEquals(0, monthsList1.get(11).getValue());

        List<Type> typeList1 = full.getTypesTable().getTypeList();
        Assert.assertEquals(135, typeList1.get(0).getMonthList().get(0).getValue());
        Assert.assertEquals(7, typeList1.get(3).getMonthList().get(5).getValue());
        Assert.assertEquals(1, typeList1.get(5).getMonthList().get(0).getValue());
        Assert.assertEquals(27, typeList1.get(2).getMonthList().get(9).getValue());
        Assert.assertEquals(0, typeList1.get(0).getMonthList().get(11).getValue());
    }
}
