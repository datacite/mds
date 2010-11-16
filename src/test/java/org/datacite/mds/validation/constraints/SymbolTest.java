package org.datacite.mds.validation.constraints;

// TODO test for parameter hasToExist


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.ArrayUtils;
import org.datacite.mds.util.Utils;
import org.junit.Test;

public class SymbolTest {

    String[] allowAllocator = { "AB", "ABCDEFGH" };
    String[] allowDatacentre = { "ABC.DE", "ABCDEF.AAA" };
    String[] disallow = { "A", "ABCDEFGHI", "AB.C", "AB.CDEFHIJKL", "abcde", "abc.def" };

    @Symbol(Symbol.Type.ALLOCATOR)
    String symbolAllocator;

    @Test
    public void testAllocator() {
        symbolAllocator = null;
        assertTrue(Utils.isValid(this, "symbolAllocator"));
        for (String symbol : allowAllocator) {
            symbolAllocator = symbol;
            assertTrue(Utils.isValid(this, "symbolAllocator"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, disallow)) {
            symbolAllocator = symbol;
            assertFalse(Utils.isValid(this, "symbolAllocator"));
        }
    }

    @Symbol(Symbol.Type.DATACENTRE)
    String symbolDatacentre;

    @Test
    public void testDatacentre() {
        symbolDatacentre = null;
        assertTrue(Utils.isValid(this, "symbolDatacentre"));
        for (String symbol : allowDatacentre) {
            symbolDatacentre = symbol;
            assertTrue(Utils.isValid(this, "symbolDatacentre"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowAllocator, disallow)) {
            symbolDatacentre = symbol;
            assertFalse(Utils.isValid(this, "symbolDatacentre"));
        }
    }

    @Symbol( { Symbol.Type.ALLOCATOR, Symbol.Type.DATACENTRE })
    String symbolBoth;

    @Test
    public void testBoth() {
        symbolBoth = null;
        assertTrue(Utils.isValid(this, "symbolBoth"));
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, allowAllocator)) {
            symbolBoth = symbol;
            assertTrue(Utils.isValid(this, "symbolBoth"));
        }
        for (String symbol : disallow) {
            symbolBoth = symbol;
            assertFalse(Utils.isValid(this, "symbolBoth"));
        }
    }
}
