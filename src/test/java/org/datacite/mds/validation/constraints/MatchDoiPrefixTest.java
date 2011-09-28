package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class MatchDoiPrefixTest extends AbstractContraintsTest {

    Dataset dataset;
    
    @Value("${handle.testPrefix}")
    String testPrefix;

    @Before
    public void init() {
        Datacentre datacentre = TestUtils.createDefaultDatacentre("10.4711");
        dataset = new Dataset();
        dataset.setDatacentre(datacentre);
    }

    @Test
    public void test() {
        assertTrue(isValid(null));
        assertTrue(isValid("10.4711/test"));
        assertTrue(isValid(testPrefix + "/test"));
        assertFalse(isValid("10.1234/test"));
    }
     
    @Test
    // TODO check if still required with secondlevel constraints
    public void testNullDatacentre() {
        dataset.setDatacentre(null);
        assertTrue(isValid("foobar"));
    }
    
    @Test
    public void testUpdatePersistent() {
        assertFalse(isValid("10.1234/foobar"));
        assertTrue(isValid("10.4711/foobar"));
        dataset.persist();
        assertTrue(isValid("10.1234/foobar"));
        assertTrue(isValid("10.4711/foobar"));
    }

    boolean isValid(String doi) {
        dataset.setDoi(doi);
        return super.isValidAnnotation(dataset, MatchDoiPrefix.class);
    }
}
