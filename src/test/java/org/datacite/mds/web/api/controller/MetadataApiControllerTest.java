package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.validation.ValidationException;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.DeletedException;
import org.datacite.mds.web.api.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class MetadataApiControllerTest {

    @Autowired
    DoiService doiService;
    @Autowired
    ValidationHelper validationHelper;

    MetadataApiController metadataApiController = new MetadataApiController();

    String allocatorSymbol = "TEST";
    String datacentreSymbol = allocatorSymbol + ".TEST";
    String prefix = "10.5072";
    String doi = prefix + "/1";
    String url = "http://example.com";
    String xml;

    Allocator allocator;
    Datacentre datacentre;
    Datacentre datacentre2;
    Dataset dataset;
    Metadata metadata;

    @Before
    public void init() throws Exception {
        metadataApiController.doiService = doiService;
        metadataApiController.validationHelper = validationHelper;

        allocator = TestUtils.createAllocator(allocatorSymbol);
        allocator.setPrefixes(TestUtils.createPrefixes(prefix));
        allocator.persist();

        datacentre = TestUtils.createDatacentre(datacentreSymbol, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());
        datacentre.persist();

        datacentre2 = TestUtils.createDatacentre(datacentreSymbol + "2", allocator);
        datacentre2.persist();

        dataset = TestUtils.createDataset(doi, datacentre);
        dataset.setIsActive(true);
        dataset.persist();

        metadata = new Metadata();
        metadata.setDataset(dataset);
        metadata.setXml(TestUtils.getTestMetadata());
        metadata.persist();
        
        xml = new String(metadata.getXml(), "UTF-8");

        TestUtils.login(datacentre);
    }
    
    @Test(expected = NotFoundException.class)
    public void testGet404() throws Exception {
        metadata.remove();
        metadataApiController.get(doi);
    }

    @Test(expected = NotFoundException.class)
    public void testGetNoDoi() throws Exception {
        metadataApiController.get(doi + 1);
    }

    @Test
    public void testGet() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.get(doi);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = SecurityException.class)
    public void testGetForeignDataset() throws Exception {
        TestUtils.login(datacentre2);
        metadataApiController.get(doi);
    }

    @Test
    public void testCreateOrUpdateExistingDatasetPUT() throws Exception {
        HttpStatus responseStatus = createOrUpdateWithMethod("PUT", xml, doi, url, null);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }
    
    private HttpStatus createOrUpdateWithMethod(String method, String body, String doi, String url, Boolean testMode) throws Exception {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod(method);
        ResponseEntity<? extends Object> response = metadataApiController.createOrUpdate(body, doi, url, testMode,
                httpRequest);
        return response.getStatusCode();
    }
    
    @Test
    public void testCreateOrUpdateNonExistingDatasetPUT() throws Exception {
        metadata.remove();
        dataset.remove();
        HttpStatus responseStatus = createOrUpdateWithMethod("PUT", xml, doi, url, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testCreateOrUpdateTestModePUT() throws Exception {
        HttpStatus responseStatus = createOrUpdateWithMethod("PUT", xml, doi, url, true);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateForeignDatasetPUT() throws Exception {
        TestUtils.login(datacentre2);
        createOrUpdateWithMethod("PUT", xml, doi, url, false);
    }

    @Test
    public void testCreateOrUpdatePOST() throws Exception {
        HttpStatus responseStatus = createOrUpdateWithMethod("POST", xml, doi, url, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testCreateOrUpdateNonExistingDatasetPOST() throws Exception {
        metadata.remove();
        dataset.remove();
        HttpStatus responseStatus = createOrUpdateWithMethod("POST", xml, doi, url, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testDelete() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doi, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = SecurityException.class)
    public void testDeleteForeignDataset() throws Exception {
        TestUtils.login(datacentre2);
        metadataApiController.delete(doi, null);
    }

    @Test(expected = NotFoundException.class)
    public void testDelete404() throws Exception {
        metadataApiController.delete(doi + 1, false);
    }

    @Test
    public void testDeleteTestMode() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doi, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUnDelete() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doi, false);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = metadataApiController.delete(doi, false);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test(expected = DeletedException.class)
    public void testGetDeleted() throws Exception {
        metadataApiController.delete(doi, false);
        metadataApiController.get(doi);
    }
}
