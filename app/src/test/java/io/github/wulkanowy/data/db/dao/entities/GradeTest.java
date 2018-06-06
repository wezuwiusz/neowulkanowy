package io.github.wulkanowy.data.db.dao.entities;

import org.junit.Assert;
import org.junit.Test;

public class GradeTest {

    @Test
    public void equalsTest() {
        Assert.assertTrue(new Grade().setSubject("Religia").setValue("5")
                .equals(new Grade().setSubject("Religia").setValue("5")));

        Assert.assertFalse(new Grade().setSubject("Religia").setValue("4")
                .equals(new Grade().setSubject("Religia").setValue("5")));

        Assert.assertEquals(new Grade().setSubject("Religia").setValue("5").hashCode(),
                new Grade().setSubject("Religia").setValue("5").hashCode());

        Assert.assertFalse(new Grade().setSubject("Informatyka")
                .equals(new FakeGrade().setSubject("Informatyka")));

        Assert.assertFalse(new Grade().setSubject("Informatyka")
                .equals(null));
    }

    private class FakeGrade {

        private String subject;

        private FakeGrade setSubject(String subject) {
            this.subject = subject;
            this.subject = this.subject + subject;
            return this;
        }
    }
}
