package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.ArrayUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.util.DomainUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( { "javax.*", "org.apache.log4j.*" })
@PrepareForTest( { DomainUtils.class })
public class SymbolTest extends AbstractContraintsTest {

    String[] allowAllocator = { "AB", "ABCDEFGH", "AB-CD", "AB2", "A42-5" };
    String[] allowDatacentre = { "ABC.DE", "ABCDEF.AAA", "AB.CD-EF", "AB-CD.EF-GH", "AB.C42" };
    String[] disallow = { "A", "ABCDEFGHI", "AB.C", "AB.CDEFHIJKL", "abcde", "abc.def", "AB-", "-AB", "A--B", "AB.-C",
            "AB.C-", "AB.C--D", "42" };

    @Symbol(Symbol.Type.ALLOCATOR)
    String symbolAllocator;

    @Test
    public void testAllocator() {
        symbolAllocator = null;
        assertTrue(getValidationHelper().isValid(this, "symbolAllocator"));
        for (String symbol : allowAllocator) {
            symbolAllocator = symbol;
            assertTrue(getValidationHelper().isValid(this, "symbolAllocator"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, disallow)) {
            symbolAllocator = symbol;
            assertFalse(getValidationHelper().isValid(this, "symbolAllocator"));
        }
    }

    @Symbol(Symbol.Type.DATACENTRE)
    String symbolDatacentre;

    @Test
    public void testDatacentre() {
        symbolDatacentre = null;
        assertTrue(getValidationHelper().isValid(this, "symbolDatacentre"));
        for (String symbol : allowDatacentre) {
            symbolDatacentre = symbol;
            assertTrue(getValidationHelper().isValid(this, "symbolDatacentre"));
        }
        for (String symbol : (String[]) ArrayUtils.addAll(allowAllocator, disallow)) {
            symbolDatacentre = symbol;
            assertFalse(getValidationHelper().isValid(this, "symbolDatacentre"));
        }
    }

    @Symbol( { Symbol.Type.ALLOCATOR, Symbol.Type.DATACENTRE })
    String symbolBoth;

    @Test
    public void testBoth() {
        symbolBoth = null;
        assertTrue(getValidationHelper().isValid(this, "symbolBoth"));
        for (String symbol : (String[]) ArrayUtils.addAll(allowDatacentre, allowAllocator)) {
            symbolBoth = symbol;
            assertTrue(getValidationHelper().isValid(this, "symbolBoth"));
        }
        for (String symbol : disallow) {
            symbolBoth = symbol;
            assertFalse(getValidationHelper().isValid(this, "symbolBoth"));
        }
    }

    @Symbol(value = Symbol.Type.ALLOCATOR, hasToExist = true)
    String existingSymbol = "DUMMY";

    @Test
    public void testExistingSymbol() {
        mockFindAllocatorOrDatacentre(new Allocator());
        assertTrue(isValidExistingSymbol());
        PowerMock.verifyAll();
    }

    @Test
    public void testNonExistingSymbol() {
        mockFindAllocatorOrDatacentre(null);
        assertFalse(isValidExistingSymbol());
        PowerMock.verifyAll();
    }

    boolean isValidExistingSymbol() {
        return getValidationHelper().isValid(this, "existingSymbol");
    }

    void mockFindAllocatorOrDatacentre(AllocatorOrDatacentre mockReturn) {
        PowerMock.mockStatic(DomainUtils.class);
        EasyMock.expect(DomainUtils.findAllocatorOrDatacentreBySymbol(existingSymbol)).andReturn(mockReturn);
        PowerMock.replay(DomainUtils.class);
    }
}
