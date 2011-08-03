package org.datacite.mds.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.transform.TransformerException;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class MetadataKernel20FixSchemaLocationTest {
    
    public final static String BASE_DIR = "conversion/metadata-kernel-2.0_fix-schema-location/"; 

    @Autowired
    MetadataKernel20FixSchemaLocation convertor;

    @Autowired
    SchemaService schemaService;

    @Test
    public void testNeedsConversion() {
        assertTrue(needsConversion("<resource/>".getBytes()));
        assertFalse(needsConversion(TestUtils.getTestMetadata20()));
        assertFalse(needsConversion(TestUtils.getTestMetadata21()));
    }

    private boolean needsConversion(byte[] xml) {
        Metadata metadata = createMetadata(xml);
        return convertor.needsConversion(metadata);
    }

    private Metadata createMetadata(byte[] xml) {
        Dataset dataset = new Dataset();
        Metadata metadata = new Metadata();
        metadata.setDataset(dataset);
        metadata.setId(42L);
        metadata.setXml(xml);
        return metadata;
    }

    @Test
    public void testConversion() throws Exception {
        assertConversion("converted.xml", "wrong_no_namespace_schema_location.xml");
        assertConversion("converted.xml", "missing_no_namespace_schema_location.xml");
        assertConversion("converted.xml", "wrong_xsi_namespace.xml");
    }

    private void assertConversion(String fileExpected, String fileOrig) throws TransformerException {
        byte[] orig = TestUtils.getTestMetadata(BASE_DIR + fileOrig);
        byte[] expected = TestUtils.getTestMetadata(BASE_DIR + fileExpected);
        assertConversion(expected, orig);
    }

    private void assertConversion(byte[] expected, byte[] orig) throws TransformerException {
        Assert.assertTrue(needsConversion(orig));
        byte[] converted = convert(orig);
        Assert.assertArrayEquals(expected, converted);
        Assert.assertFalse(needsConversion(converted));
    }

    private byte[] convert(byte[] xml) throws TransformerException {
        Metadata orig = createMetadata(xml);
        Metadata converted = convertor.convert(orig);
        assertNull(converted.getId());
        assertEquals(orig.getDataset(), converted.getDataset());
        assertEquals(true, converted.getIsConvertedByMds());

        return converted.getXml();
    }

}
