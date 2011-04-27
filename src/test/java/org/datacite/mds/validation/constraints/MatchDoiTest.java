package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MatchDoiTest extends AbstractContraintsTest {
    
    String doiFromXml = "10.1594/WDCC/CCSRNIES_SRES_B2";
    

    Metadata metadata;

    @Before
    public void init() throws Exception {
        Dataset dataset = new Dataset();
        metadata = new Metadata();
        metadata.setDataset(dataset);
    }

    @Test
    public void testVersion20() throws Exception {
        metadata.setXml(TestUtils.getTestMetadata20());
        assertTrue(isValid(doiFromXml)); 
        assertFalse(isValid(doiFromXml + ".")); 
    }

    @Test
    public void testVersion21() throws Exception {
        metadata.setXml(TestUtils.getTestMetadata21());
        assertTrue(isValid(doiFromXml)); 
        assertFalse(isValid(doiFromXml + ".")); 
    }

    boolean isValid(String doi) {
        metadata.getDataset().setDoi(doi);
        return super.isValidAnnotation(metadata, MatchDoi.class);
    }
}
