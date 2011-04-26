package org.datacite.mds.service.impl;

import static org.junit.Assert.*;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.validation.ValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class SchemaServiceImplTest {
    
    @Autowired
    SchemaServiceImpl service;
    
    @Test
    public void testGetSchemaLocationNoNamespace() throws Exception {
        Metadata metadata = new Metadata();
        metadata.setXml(TestUtils.getTestMetadata20());
        String schemaLocation = service.getSchemaLocation(metadata);
        assertEquals("datacite-metadata-v2.0.xsd", schemaLocation);
    }
    
    @Test
    public void testGetSchemaLocationWithNamespace() throws Exception {
        Metadata metadata = new Metadata();
        metadata.setXml(TestUtils.getTestMetadata21());
        String schemaLocation = service.getSchemaLocation(metadata);
        assertEquals("http://datacite.org/schema/datacite-metadata-v2.1.xsd", schemaLocation);
    }

    @Test(expected=ValidationException.class)
    public void testGetSchemaLocationNoLocation() throws Exception {
        Metadata metadata = new Metadata();
        String xml = "<root/>";
        metadata.setXml(xml.getBytes());
        String schemaLocation = service.getSchemaLocation(metadata);
    }
}
