package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.junit.Before;
import org.junit.Test;

public class MatchSymbolPrefixTest extends AbstractContraintsTest {

    Datacentre datacentre;

    @Before
    public void init() {
        Allocator allocator = new Allocator();
        allocator.setSymbol("AL");

        datacentre = new Datacentre();
        datacentre.setAllocator(allocator);
    }

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertTrue(isValid("AL.DC"));
        assertFalse(isValid("OTHER.DC"));
    }

    boolean isValid(String symbol) {
        datacentre.setSymbol(symbol);
        return getValidationHelper().isValid(datacentre, MatchSymbolPrefix.class);
    }
}
