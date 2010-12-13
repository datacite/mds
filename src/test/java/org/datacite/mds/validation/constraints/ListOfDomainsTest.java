package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import org.datacite.mds.util.ValidationUtils;
import org.junit.Test;

public class ListOfDomainsTest {
    @ListOfDomains
    String domains;

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertFalse(isValid("com"));
        assertFalse(isValid("example.o"));
        assertFalse(isValid("com,example.com"));
        assertFalse(isValid("example.org;example.com"));
        assertFalse(isValid("example.com/path"));
        assertFalse(isValid("http://example.com"));
        assertTrue(isValid("example.org,foo.bar.org,a.b.c.de"));
    }

    boolean isValid(String domains) {
        this.domains = domains;
        return ValidationUtils.isValid(this, "domains");
    }

}
