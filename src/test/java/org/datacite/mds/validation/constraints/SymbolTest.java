package org.datacite.mds.validation.constraints;

// TODO test for parameter hasToExist

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.ArrayUtils;
import org.datacite.mds.validation.util.ValidationUtils;
import org.junit.Test;

public class SymbolTest {

    String[] allowAllocator = { "AB", "ABCDEFGH", "AB-CD" };
    String[] allowDatacentre = { "ABC.DE", "ABCDEF.AAA", "AB.CD-EF", "AB-CD.EF-GH" };
    String[] disallow = { "A", "ABCDEFGHI", "AB.C", "AB.CDEFHIJKL", "abcde", "abc.def", "AB-", "-AB", "A--B", "AB.-C",
            "AB.C-", "AB.C--D" };

    @Symbol(Symbol.Type.ALLOCATOR)
    String symbolAllocator;

    @Test
    public void testAllocator() {
        symbolAllocator = null;
        assertTrue(ValidationUtils.isValid(this, "symbolAllocator"));
        for (String symbol : allowAllocator) {
            symbolAllocator = symbol;
            assertTrue(ValidationUtils.isValid(this, "symbolAllocator"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, disallow)) {
            symbolAllocator = symbol;
            assertFalse(ValidationUtils.isValid(this, "symbolAllocator"));
        }
    }

    @Symbol(Symbol.Type.DATACENTRE)
    String symbolDatacentre;

    @Test
    public void testDatacentre() {
        symbolDatacentre = null;
        assertTrue(ValidationUtils.isValid(this, "symbolDatacentre"));
        for (String symbol : allowDatacentre) {
            symbolDatacentre = symbol;
            assertTrue(ValidationUtils.isValid(this, "symbolDatacentre"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowAllocator, disallow)) {
            symbolDatacentre = symbol;
            assertFalse(ValidationUtils.isValid(this, "symbolDatacentre"));
        }
    }

    @Symbol( { Symbol.Type.ALLOCATOR, Symbol.Type.DATACENTRE })
    String symbolBoth;

    @Test
    public void testBoth() {
        symbolBoth = null;
        assertTrue(ValidationUtils.isValid(this, "symbolBoth"));
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, allowAllocator)) {
            symbolBoth = symbol;
            assertTrue(ValidationUtils.isValid(this, "symbolBoth"));
        }
        for (String symbol : disallow) {
            symbolBoth = symbol;
            assertFalse(ValidationUtils.isValid(this, "symbolBoth"));
        }
    }
}
