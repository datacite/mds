package org.datacite.mds.validation.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationUtilsTest {
    
    @Test
    public void testIsHostname() {
        assertTrue(ValidationUtils.isHostname("test.de"));
        assertTrue(ValidationUtils.isHostname("täst.de"));
        assertTrue(ValidationUtils.isHostname("xn--hxajbheg2az3al.de"));
        assertTrue(ValidationUtils.isHostname("xn--hxajbheg2az3al.xn--jxalpdlp"));
        assertTrue(ValidationUtils.isHostname("παράδειγμα.δοκιμή"));
        assertFalse(ValidationUtils.isHostname("täst"));
        assertFalse(ValidationUtils.isHostname("test.de:80"));
        assertFalse(ValidationUtils.isHostname("test.de/path"));
        assertFalse(ValidationUtils.isHostname("http://test.de"));
    }
}
