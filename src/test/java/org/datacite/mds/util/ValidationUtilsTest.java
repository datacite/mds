package org.datacite.mds.util;

import static org.junit.Assert.*;

import org.datacite.mds.test.TestUtils;
import org.datacite.mds.util.ValidationUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationUtilsTest {

    String fromField = "field1";
    String toField = "field2";
    
    BindingResult result;

    @Before
    public void init() {
        Object targetBean = new Object();
        String objectName = "object";
        result = new BeanPropertyBindingResult(targetBean, objectName);
    }

    @Test
    public void testCopyFieldErrorToField() {
        String message = "message";
        FieldError fieldError = new FieldError(result.getObjectName(), fromField, message);
        result.addError(fieldError);

        assertEquals(1, result.getFieldErrorCount());
        ValidationUtils.copyFieldErrorToField(result, fromField, toField);
        assertEquals(2, result.getFieldErrorCount());
        assertNotNull(result.getFieldError(fromField));
        assertNotNull(result.getFieldError(toField));
    }
    
    @Test
    public void testCopyFieldErrorToField_NoFieldError() {
        assertEquals(0, result.getFieldErrorCount());
        ValidationUtils.copyFieldErrorToField(result, fromField, toField);
        assertEquals(0, result.getFieldErrorCount());
        assertNull(result.getFieldError(fromField));
        assertNull(result.getFieldError(toField));
    }

    @Test
    public void testIsHostname() {
        assertFalse(ValidationUtils.isHostname(null));
        assertFalse(ValidationUtils.isHostname(""));
        assertTrue(ValidationUtils.isHostname("test.de"));
        assertTrue(ValidationUtils.isHostname("täst.de"));
        assertTrue(ValidationUtils.isHostname("xn--hxajbheg2az3al.de"));
        assertTrue(ValidationUtils.isHostname("xn--hxajbheg2az3al.xn--jxalpdlp"));
        assertTrue(ValidationUtils.isHostname("παράδειγμα.δοκιμή"));
        assertFalse(ValidationUtils.isHostname("täst"));
        assertFalse(ValidationUtils.isHostname("test.de:80"));
        assertFalse(ValidationUtils.isHostname("test.de/path"));
        assertFalse(ValidationUtils.isHostname("test.de?query"));
        assertFalse(ValidationUtils.isHostname("test.de#fragment"));
        assertFalse(ValidationUtils.isHostname("http://test.de"));
    }
    
    @Test
    public void callConstructor() {
        TestUtils.callConstructor(ValidationUtils.class);
    }

}
