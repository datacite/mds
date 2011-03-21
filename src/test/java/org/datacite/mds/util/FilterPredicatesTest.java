package org.datacite.mds.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.test.TestUtils;
import org.junit.Test;

public class FilterPredicatesTest {
    @Test
    public void testAllocatorOrDatacentreContainsPrefixPredicate() {
        Prefix prefix = TestUtils.createPrefix("10.5072");
        Prefix anotherPrefix = TestUtils.createPrefix("10.0000");
        Set<Prefix> prefixes = TestUtils.createPrefixes("10.5072", "10.5432");
        Allocator allocator = TestUtils.createAllocator("AL");
        allocator.setPrefixes(prefixes);
        Datacentre datacentre = TestUtils.createDatacentre("AL.DC", allocator);
        datacentre.setPrefixes(prefixes);
        
        assertTrue(prefixes.contains(prefix));
        assertFalse(prefixes.contains(anotherPrefix));
        
        Predicate predicate = FilterPredicates.getAllocatorOrDatacentreContainsPrefixPredicate(prefix);
        assertTrue(predicate.evaluate(allocator));
        assertTrue(predicate.evaluate(datacentre));

        predicate = FilterPredicates.getAllocatorOrDatacentreContainsPrefixPredicate(anotherPrefix);
        assertFalse(predicate.evaluate(allocator));
        assertFalse(predicate.evaluate(datacentre));
}
}
