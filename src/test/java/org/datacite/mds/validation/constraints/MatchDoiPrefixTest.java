package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.validation.utils.ValidationUtils;
import org.junit.Before;
import org.junit.Test;

public class MatchDoiPrefixTest {

    Dataset dataset;

    @Before
    public void init() {
        // create simple datacentre with two allowed prefixes
        Datacentre datacentre = new Datacentre();
        Set<Prefix> prefixes = new HashSet<Prefix>();
        Prefix prefix1 = new Prefix();
        prefix1.setPrefix("10.5072");
        prefixes.add(prefix1);
        Prefix prefix2 = new Prefix();
        prefix2.setPrefix("10.4711");
        prefixes.add(prefix2);
        datacentre.setPrefixes(prefixes);

        dataset = new Dataset();
        dataset.setDatacentre(datacentre);
    }

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertTrue(isValid("10.4711/test"));
        assertTrue(isValid("10.5072/test"));
        assertFalse(isValid("10.1234/test"));
    }

    boolean isValid(String doi) {
        dataset.setDoi(doi);
        return ValidationUtils.isConstraintValid(dataset, MatchDoiPrefix.class);
    }
}
