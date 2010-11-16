package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import org.datacite.mds.validation.utils.ValidationUtils;
import org.junit.Test;

public class DoiPrefixTest {
    @DoiPrefix
    String prefix;

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertFalse(isValid(""));
        assertFalse(isValid("abc"));
        assertFalse(isValid("10"));
        assertFalse(isValid("10.abc"));
        assertFalse(isValid("10.1234/"));
        assertFalse(isValid("10.1234/test"));
        assertFalse(isValid("10..1234"));
        assertTrue(isValid("10.1234"));
    }

    boolean isValid(String prefix) {
        this.prefix = prefix;
        return ValidationUtils.isValid(this, "prefix");
    }

}
