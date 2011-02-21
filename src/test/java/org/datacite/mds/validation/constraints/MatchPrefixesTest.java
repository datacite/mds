package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.test.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MatchPrefixesTest extends AbstractContraintsTest {

    Datacentre datacentre;

    @Before
    public void init() {
        Allocator allocator = Utils.createAllocator("AL");
        datacentre = Utils.createDatacentre("AL.DC", allocator);
        
        allocator.setPrefixes(Utils.createPrefixes("10.5072", "10.5073"));
    }

    @Test
    public void test() {
        assertTrue(isPrefixSetValid()); 
        assertTrue(isPrefixSetValid("10.5072"));
        assertTrue(isPrefixSetValid("10.5072", "10.5073"));
        assertFalse(isPrefixSetValid("10.9999"));
        assertFalse(isPrefixSetValid("10.5072", "10.9999"));
    }

    boolean isPrefixSetValid(String... prefixes) {
        datacentre.setPrefixes(Utils.createPrefixes(prefixes));
        return super.isValid(datacentre);
    }
}
