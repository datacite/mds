package org.datacite.mds.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrefixTest {

    @Test
    public void testCompareTo() {
        assertTrue(0 == compareTo("10.5072", "10.5072"));
        assertTrue(0 < compareTo("10.12345", "10.4321"));
        assertTrue(0 > compareTo("10.42", "10.911"));
        assertTrue(0 < compareTo("10.6000", "10.2000"));
        assertTrue(0 > compareTo("10.2000", "10.3000"));
    }
    
    private int compareTo(String prefix1, String prefix2) {
        Prefix p1 = new Prefix();
        Prefix p2 = new Prefix();
        p1.setPrefix(prefix1);
        p2.setPrefix(prefix2);
        return p1.compareTo(p2);
    }
}
