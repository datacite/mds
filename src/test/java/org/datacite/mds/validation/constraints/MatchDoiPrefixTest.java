package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MatchDoiPrefixTest extends AbstractContraintsTest {

    Dataset dataset;
    
    @Value("${handle.testPrefix}")
    String testPrefix;

    @Before
    public void init() {
        // create simple datacentre with two allowed prefixes
        Datacentre datacentre = new Datacentre();
        Set<Prefix> prefixes = new HashSet<Prefix>();
        Prefix prefix = new Prefix();
        prefix.setPrefix("10.4711");
        prefixes.add(prefix);
        datacentre.setPrefixes(prefixes);

        dataset = new Dataset();
        dataset.setDatacentre(datacentre);
    }

    @Test
    public void test() {
        assertTrue(isValid(null));
        assertTrue(isValid("10.4711/test"));
        assertTrue(isValid(testPrefix + "/test"));
        assertFalse(isValid("10.1234/test"));
        
        dataset.setDatacentre(null);
        assertTrue(isValid("foobar"));
    }

    boolean isValid(String doi) {
        dataset.setDoi(doi);
        return super.isValidAnnotation(dataset, MatchDoiPrefix.class);
    }
}
