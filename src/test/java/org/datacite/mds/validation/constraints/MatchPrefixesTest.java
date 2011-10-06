package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MatchPrefixesTest extends AbstractContraintsTest {

    Allocator allocator;
    Datacentre datacentre;

    @Before
    public void init() {
        allocator = TestUtils.createAllocator("AL");
        datacentre = TestUtils.createDatacentre("AL.DC", allocator);
    }

    @Test
    public void test() {
        allocator.setPrefixes(TestUtils.createPrefixes("10.5072", "10.5073"));
        assertTrue(isPrefixSetValid());
        assertTrue(isPrefixSetValid("10.5072"));
        assertTrue(isPrefixSetValid("10.5072", "10.5073"));
        assertFalse(isPrefixSetValid("10.9999"));
        assertFalse(isPrefixSetValid("10.5072", "10.9999"));
    }
    
    @Test
    public void testNullList() {
        allocator.setPrefixes(null);
        assertTrue(isPrefixSetValid());
        assertFalse(isPrefixSetValid("10.5072"));
    }

    @Test
    public void testEmptyList() {
        allocator.setPrefixes(TestUtils.createPrefixes());
        assertTrue(isPrefixSetValid());
        assertFalse(isPrefixSetValid("10.5072"));
    }
    

    boolean isPrefixSetValid(String... prefixes) {
        datacentre.setPrefixes(TestUtils.createPrefixes(prefixes));
        return super.isValid(datacentre);
    }
}
