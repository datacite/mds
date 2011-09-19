package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class MediaTypeTest extends AbstractContraintsTest {
    @MediaType
    String mediaType;

    @Test
    public void test() {
        assertFalse(isValid(null)); 
        assertFalse(isValid(""));
        assertFalse(isValid("text"));
        assertFalse(isValid("text/"));
        assertTrue(isValid("text/foobar"));
        assertFalse(isValid("text/ foobar"));
        assertFalse(isValid(" text/foobar"));
        assertFalse(isValid("text/foobar "));
        assertFalse(isValid("text/foobar;q=1"));
        assertFalse(isValid("text/foo bar"));
        assertFalse(isValid("text/*"));
        assertTrue(isValid("application/x-datacite+xml"));
    }
    
    @Test
    @Ignore
    public void testValidTypes() {
        assertTrue(isValid("text/foobar"));
        assertTrue(isValid("image/foobar"));
        assertTrue(isValid("application/foobar"));
        assertTrue(isValid("audio/foobar"));
        assertTrue(isValid("video/foobar"));
        assertFalse(isValid("foo/bar"));
    }

    boolean isValid(String mediaType) {
        this.mediaType = mediaType;
        return super.isValid(this, "mediaType");
    }

}
