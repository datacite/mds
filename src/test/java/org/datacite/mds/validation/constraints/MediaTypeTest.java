package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MediaTypeTest extends AbstractContraintsTest {
    
    @MediaType
    String mediaType;
    
    @Test
    public void test() {
        assertFalse(isValid(null)); 
        assertFalse(isValid(""));
        assertFalse(isValid("text"));
        assertFalse(isValid("text/"));
        assertFalse(isValid("/xml"));
        assertTrue(isValid("text/foobar"));
        assertFalse(isValid("text/foo/bar"));
        assertFalse(isValid("text/ foobar"));
        assertFalse(isValid(" text/foobar"));
        assertFalse(isValid("text/foobar "));
        assertFalse(isValid("text/foobar;q=1"));
        assertFalse(isValid("text/foo bar"));
        assertFalse(isValid("text/*"));
    }
    
    @Test
    public void testDisallowedTypes() {
        assertFalse(isValid("application/x-datacite"));
        assertFalse(isValid("application/x-datacite+xml"));
        assertFalse(isValid("application/x-datacite+foobar"));
    }
    
    @Test
    public void testValidTypes() {
        assertTrue(isValid("text/foobar"));
        assertTrue(isValid("image/foobar"));
        assertTrue(isValid("application/foobar"));
        assertTrue(isValid("audio/foobar"));
        assertTrue(isValid("video/foobar"));
        assertTrue(isValid("chemical/foobar"));
        assertTrue(isValid("foo/bar"));
    }

    boolean isValid(String mediaType) {
        this.mediaType = mediaType;
        return super.isValid(this, "mediaType");
    }

}
