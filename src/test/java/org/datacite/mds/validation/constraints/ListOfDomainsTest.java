package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ListOfDomainsTest extends AbstractContraintsTest {
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
        return getValidationHelper().isValid(this, "domains");
    }

}
