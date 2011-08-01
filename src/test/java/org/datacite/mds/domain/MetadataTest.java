package org.datacite.mds.domain;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.service.SchemaService;
import org.datacite.mds.test.TestUtils;
import static org.easymock.EasyMock.expect;
import org.easymock.EasyMockSupport;
import org.junit.Test;

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
    
    
}
