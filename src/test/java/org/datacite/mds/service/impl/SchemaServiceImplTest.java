package org.datacite.mds.service.impl;

import static org.junit.Assert.assertEquals;

import javax.validation.ValidationException;

import org.datacite.mds.test.TestUtils;
import org.junit.Before;
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
    
    @Before
    public void init() {
        service.schemaLocationLocal = "";
    }
    
    @Test
    public void testGetSchemaLocationNoNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata20();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("http://schema.datacite.org/meta/kernel-2.0/metadata.xsd", schemaLocation);
    }
    
    @Test
    public void testGetSchemaLocationWithNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata21();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("http://schema.datacite.org/meta/kernel-2.1/metadata.xsd", schemaLocation);
    }

    @Test(expected=ValidationException.class)
    public void testGetSchemaLocationNoLocation() throws Exception {
        byte[] xml = "<root/>".getBytes();
        service.getSchemaLocation(xml);
    }
    
    @Test
    public void getSchemaValidator() throws Exception {
        service.getSchemaValidator("http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
    }

    @Test(expected = SAXParseException.class)
    public void getSchemaValidatorNonExisting() throws Exception {
        service.getSchemaValidator("file://foobar.xsd");
    }
    
    @Test
    public void testConvertSchemaLocationToLocal() {
        String global = service.schemaLocationPrefix;
        String local = "file://bar/";
        service.schemaLocationLocal = local;
        String converted = service.convertSchemaLocationToLocal(global + "/path");
        assertEquals(local + "/path", converted);
    }
    
    @Test
    public void testConvertSchemaLocationEmptyLocal() {
        String global = service.schemaLocationPrefix;
        service.schemaLocationLocal = "";
        String location = global + "/path";
        String converted = service.convertSchemaLocationToLocal(location);
        assertEquals(location, converted);
    }

}
