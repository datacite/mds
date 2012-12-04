package org.datacite.mds.domain;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.test.TestUtils;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional 
public class MetadataTest extends EasyMockSupport {

    
    @Test
    public void testSetXml() throws Exception{
        byte[] xml = TestUtils.getTestMetadata();
        String namespace = "foobar";

        Metadata metadata = new Metadata();
        metadata.schemaService = createMock(SchemaService.class);
        expect(metadata.schemaService.getNamespace(xml)).andReturn(namespace);
        replayAll();
        
        metadata.setXml(xml);
        assertEquals(namespace, metadata.getNamespace());
        
        verifyAll();
    }
    
    @Test
    public void testXmlNotTooBig() throws Exception {
        createMetadataWithSize(Metadata.XML_MAX_SIZE).persist();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testXmlTooBig() throws Exception {
        createMetadataWithSize(Metadata.XML_MAX_SIZE + 1).persist();
    }
    
    private Metadata createMetadataWithSize(int size) throws Exception {
        String doi = "10.5072/foobar";
        Dataset dataset = TestUtils.createDefaultDataset(doi);
        byte[] xml = TestUtils.getTestMetadata();
        xml = TestUtils.setDoiOfMetadata(xml, doi);
        xml = enlargeXml(xml, size);
        Metadata metadata = TestUtils.createMetadata(xml, dataset);
        return metadata;
    }
    
    private byte[] enlargeXml(byte[] xml, int finalSize) throws Exception {
        Document doc = TestUtils.bytesToDocument(xml);
        int curSize = TestUtils.documentToBytes(doc).length;
        int incSize = Math.max(finalSize - curSize, 0);
        Node publisher = doc.getElementsByTagName("publisher").item(0).getFirstChild();
        String fillin = StringUtils.leftPad("", incSize);
        publisher.setNodeValue(publisher.getNodeValue() + fillin);
        xml = TestUtils.documentToBytes(doc);
        return xml;
    }
    
    
}
