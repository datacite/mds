package org.datacite.mds.service.impl;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.test.TestUtils;
import org.datacite.mds.validation.ValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXParseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class SchemaServiceImplTest {
    
    @Autowired
    SchemaServiceImpl service;
    
    @Test
    public void testGetSchemaLocationNoNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata20();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("datacite-metadata-v2.0.xsd", schemaLocation);
    }
    
    @Test
    public void testGetSchemaLocationWithNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata21();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("http://datacite.org/schema/datacite-metadata-v2.1.xsd", schemaLocation);
    }

    @Test(expected=ValidationException.class)
    public void testGetSchemaLocationNoLocation() throws Exception {
        byte[] xml = "<root/>".getBytes();
        service.getSchemaLocation(xml);
    }
    
    @Test
    public void getSchemaValidator() throws Exception {
        service.getSchemaValidator("http://datacite.org/schema/datacite-metadata-v2.1.xsd");
    }

    @Test(expected = SAXParseException.class)
    public void getSchemaValidatorNonExisting() throws Exception {
        service.getSchemaValidator("file://foobar.xsd");
    }
}
