package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EmailTest extends AbstractContraintsTest {
    @Email
    String email;

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertFalse(isValid(""));
        assertFalse(isValid("@"));
        assertFalse(isValid("a@b"));
        assertFalse(isValid("a@b.c"));
        assertFalse(isValid("a@b.cd.e"));
        assertFalse(isValid("a@b@c.de"));
        assertFalse(isValid("a@com"));
        assertFalse(isValid("a @b.com"));
        assertFalse(isValid("a@b.com/path"));
        assertFalse(isValid("a@b%com"));
        assertFalse(isValid("a@b_com"));
        assertFalse(isValid("()[]\\;:,<>@example.com"));
        assertFalse(isValid("foo.example.com"));
        assertTrue(isValid("foo@example.com"));
        assertTrue(isValid("foo_bar#1@test.de"));
        assertTrue(isValid("foobar.bar@foo.bar.org"));
    }

    boolean isValid(String email) {
        this.email = email;
        return getValidationHelper().isValid(this, "email");
    }

}
