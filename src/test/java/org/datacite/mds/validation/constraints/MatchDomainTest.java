package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Utils;
import org.junit.Before;
import org.junit.Test;

public class MatchDomainTest {

    Dataset dataset;

    @Before
    public void init() {
        // create simple datacentre with two allowed domains
        Datacentre datacentre = new Datacentre();
        datacentre.setDomains("test.org,sub.domain.net");
        dataset = new Dataset();
        dataset.setDatacentre(datacentre);
    }

    @Test
    public void test() {
        assertFalse(isValid("http://wrong.org"));
        assertTrue(isValid("http://test.org"));
        assertTrue(isValid("http://test.org/"));
        assertTrue(isValid("http://test.org/path"));
        assertFalse(isValid("http://sub.test.org/path"));
        assertFalse(isValid("http://wrong.net/path"));
        assertTrue(isValid("http://sub.domain.net/path"));
        assertFalse(isValid("http://subsub.domain.net/path"));
        assertFalse(isValid("http://sub.sub.domain.net/path"));
    }

    boolean isValid(String url) {
        dataset.setUrl(url);
        return Utils.isConstraintValid(dataset, MatchDomain.class);
    }
}
