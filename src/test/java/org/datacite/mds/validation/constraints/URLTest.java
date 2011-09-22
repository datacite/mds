package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class URLTest extends AbstractContraintsTest {
    @URL
    String url;

    @Test
    public void test() {
        assertTrue(isValid(null));
        assertTrue(isValid(""));
        assertTrue(isValid("http://example.com"));
        assertTrue(isValid("http://example.com:8080/path?q=query&x#fragment"));
        assertFalse(isValid("http://example.com:-42"));
    }

    @Test
    public void testProtocol() {
        assertTrue(isValid("http://example.com"));
        assertTrue(isValid("https://example.com"));
        assertTrue(isValid("ftp://example.com"));
        assertFalse(isValid("file://example.com"));
    }

    boolean isValid(String url) {
        this.url = url;
        return super.isValid(this, "url");
    }

}
