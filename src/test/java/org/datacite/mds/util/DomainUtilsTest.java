package org.datacite.mds.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.SetUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class DomainUtilsTest {

    String ALLOCATOR1_SYMBOL = "AL1";
    String ALLOCATOR2_SYMBOL = "AL2";
    String DATACENTRE1_SYMBOL = "AL1.DC1";
    String DATACENTRE2_SYMBOL = "AL1.DC2";
    String OTHER_SYMBOL = "AL42";

    Allocator allocator1, allocator2;
    Datacentre datacentre1, datacentre2;

    @Before
    public void init() {
        allocator1 = TestUtils.createAllocator(ALLOCATOR1_SYMBOL);
        allocator2 = TestUtils.createAllocator(ALLOCATOR2_SYMBOL);
        datacentre1 = TestUtils.createDatacentre(DATACENTRE1_SYMBOL, allocator1);
        datacentre2 = TestUtils.createDatacentre(DATACENTRE2_SYMBOL, allocator1);
        TestUtils.persist(allocator1, allocator2, datacentre1, datacentre2);
    }

    @Test
    public void testFindAllocatorOrDatacentreBySymbol() {
        AllocatorOrDatacentre user;

        user = DomainUtils.findAllocatorOrDatacentreBySymbol(ALLOCATOR1_SYMBOL);
        assertEquals(ALLOCATOR1_SYMBOL, user.getSymbol());

        user = DomainUtils.findAllocatorOrDatacentreBySymbol(DATACENTRE2_SYMBOL);
        assertEquals(DATACENTRE2_SYMBOL, user.getSymbol());
    }

    @Test
    public void testFindAllocatorOrDatacentreBySymbol_Null() {
        assertNull(DomainUtils.findAllocatorOrDatacentreBySymbol(OTHER_SYMBOL));
    }

    @Test
    public void testGetAllSymbols() {
        SortedSet<String> retrievedSymbols = DomainUtils.getAllSymbols();
        SortedSet<String> expectedSymbols = new TreeSet<String>();
        expectedSymbols.add(DATACENTRE1_SYMBOL);
        expectedSymbols.add(DATACENTRE2_SYMBOL);
        expectedSymbols.add(ALLOCATOR1_SYMBOL);
        expectedSymbols.add(ALLOCATOR2_SYMBOL);

        assertTrue(SetUtils.isEqualSet(expectedSymbols, retrievedSymbols));
    }

    @Test
    public void testGetAdmin() {
        Allocator admin = TestUtils.createAdmin("ADMIN");
        admin.persist();
        assertEquals(admin, DomainUtils.getAdmin());
    }
    
    @Test
    public void testGetAdmin_NonExisting() {
        assertNull(DomainUtils.getAdmin());
    }
    
    @Test
    public void callConstructor() {
        TestUtils.callConstructor(DomainUtils.class);
    }

}
