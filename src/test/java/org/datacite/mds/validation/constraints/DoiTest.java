package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import org.datacite.mds.util.ValidationUtils;
import org.junit.Test;

public class DoiTest {
    @Doi
    String doi;

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertFalse(isValid(""));
        assertFalse(isValid("a/b"));
        assertFalse(isValid("10.a/test"));
        assertFalse(isValid("10.1234/"));
        assertFalse(isValid("10..1234/test"));
        assertTrue(isValid("10.1234/test"));
    }

    boolean isValid(String doi) {
        this.doi = doi;
        return ValidationUtils.isValid(this, "doi");
    }

}
