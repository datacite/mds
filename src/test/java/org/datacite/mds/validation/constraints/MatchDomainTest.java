package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MatchDomainTest extends AbstractContraintsTest {

    Dataset dataset;
    
    Media media;

    @Before
    public void init() {
        // create simple datacentre with two allowed domains
        Datacentre datacentre = new Datacentre();
        datacentre.setDomains("test.ORG,sub.domain.net,*.com, *.eXample.*");
        dataset = new Dataset();
        dataset.setDatacentre(datacentre);
        media = new Media();
        media.setDataset(dataset);
    }

    @Test
    public void test() {
        assertTrue(isValid(null)); 
        assertFalse(isValid("http://wrong.org"));
        assertFalse(isValid("http://org"));
        assertTrue(isValid("http://test.org"));
        assertTrue(isValid("http://TEst.org/"));
        assertTrue(isValid("http://test.org/path"));
        assertTrue(isValid("http://sub.test.org/path"));
        assertFalse(isValid("http://wrong.net/path"));
        assertTrue(isValid("http://sub.domain.net/path"));
        assertFalse(isValid("http://subsub.domain.net/path"));
        assertTrue(isValid("http://sub.SUB.domain.net/path"));

        assertTrue(isValid("http://foobar.com"));
        assertTrue(isValid("http://SUB.foobar.com"));
        assertTrue(isValid("http://sub.Example.net"));
        assertFalse(isValid("http://example.net"));

        dataset.setDatacentre(null);
        assertTrue(isValid("foobar"));
    }

    boolean isValid(String url) {
        dataset.setUrl(url);
        media.setUrl(url);
        boolean isDatasetValid = super.isValid(dataset, Dataset.SecondLevelConstraint.class);
        boolean isMediaValid = super.isValid(media, Media.SecondLevelConstraint.class);
        assertEquals(isDatasetValid, isMediaValid);
        return isDatasetValid;
    }
}
