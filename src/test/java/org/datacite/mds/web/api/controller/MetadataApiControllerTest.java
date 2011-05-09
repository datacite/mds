package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.util.Utils;
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
    @Autowired
    SchemaService schemaService;

    MetadataApiController metadataApiController = new MetadataApiController();

    String allocatorSymbol = "TEST";
    String datacentreSymbol = allocatorSymbol + ".TEST";
    String doi = "10.1594/WDCC/CCSRNIES_SRES_B2";
    String url = "http://example.com";
    String xml;
    HttpServletRequest doiRequest;
    HttpServletRequest wrongDoiRequest;

    Allocator allocator;
    Datacentre datacentre;
    Datacentre datacentre2;
    Dataset dataset;
    Metadata metadata;

    @Before
    public void init() throws Exception {
        metadataApiController.doiService = doiService;
        metadataApiController.validationHelper = validationHelper;
        metadataApiController.schemaService = schemaService;
        
        doiRequest = makeServletRequestForDoi(doi);
        wrongDoiRequest = makeServletRequestForDoi(doi + 1);
        
        String prefix = Utils.getDoiPrefix(doi);

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
        metadataApiController.get(doiRequest);
    }
    
    private MockHttpServletRequest makeServletRequestForDoi(String doi) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/metadata/" + doi);
        return request;
    }

    @Test(expected = NotFoundException.class)
    public void testGetNoDoi() throws Exception {
        metadataApiController.get(wrongDoiRequest);
    }

    @Test
    public void testGet() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.get(doiRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = SecurityException.class)
    public void testGetForeignDataset() throws Exception {
        TestUtils.login(datacentre2);
        metadataApiController.get(doiRequest);
    }
    
    @Test
    public void testGetAsAllocator() throws Exception {
        TestUtils.login(allocator);
        ResponseEntity<? extends Object> response = metadataApiController.get(doiRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test(expected = SecurityException.class)
    public void testGetAsForeignAllocator() throws Exception {
        Allocator allocator2 = TestUtils.createAllocator("OTHER");
        allocator2.persist();
        TestUtils.login(allocator2);
        metadataApiController.get(doiRequest);
    }

    @Test(expected = SecurityException.class)
    public void testGetNotLoggedIn() throws Exception {
        TestUtils.logout();
        metadataApiController.get(doiRequest);
    }

    @Test
    public void testCreateOrUpdateExistingDatasetPUT() throws Exception {
        HttpStatus responseStatus = put(doi, xml, null);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }
    
    private HttpStatus post(String body, Boolean testMode) throws Exception {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        ResponseEntity<? extends Object> response = metadataApiController.post(body, testMode, httpRequest);
        return response.getStatusCode();
    }

    private HttpStatus put(String doi, String body, Boolean testMode) throws Exception {
        MockHttpServletRequest httpRequest = makeServletRequestForDoi(doi);
        httpRequest.setMethod("POST");
        ResponseEntity<? extends Object> response = metadataApiController.put(body, testMode, httpRequest);
        return response.getStatusCode();
    }
    
    @Test
    public void testCreateOrUpdateNonExistingDatasetPUT() throws Exception {
        metadata.remove();
        dataset.remove();
        HttpStatus responseStatus = put(doi, xml, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testCreateOrUpdateTestModePUT() throws Exception {
        HttpStatus responseStatus = put(doi, xml, true);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateForeignDatasetPUT() throws Exception {
        TestUtils.login(datacentre2);
        put(doi, xml, false);
    }
    
    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateAsAllocator() throws Exception {
        TestUtils.login(allocator);
        put(doi, xml, false);
    }

    @Test
    public void testCreateOrUpdatePOST() throws Exception {
        HttpStatus responseStatus = post(xml, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testCreateOrUpdateNonExistingDatasetPOST() throws Exception {
        metadata.remove();
        dataset.remove();
        HttpStatus responseStatus = post(xml, false);
        assertEquals(HttpStatus.CREATED, responseStatus);
    }

    @Test
    public void testDelete() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doiRequest, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = SecurityException.class)
    public void testDeleteForeignDataset() throws Exception {
        TestUtils.login(datacentre2);
        metadataApiController.delete(doiRequest, null);
    }

    @Test(expected = SecurityException.class)
    public void testDeleteAsAllocator() throws Exception {
        TestUtils.login(allocator);
        metadataApiController.delete(doiRequest, null);
    }

    @Test(expected = NotFoundException.class)
    public void testDelete404() throws Exception {
        metadataApiController.delete(wrongDoiRequest, false);
    }

    @Test
    public void testDeleteTestMode() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doiRequest, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUnDelete() throws Exception {
        ResponseEntity<? extends Object> response = metadataApiController.delete(doiRequest, false);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = metadataApiController.delete(doiRequest, false);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test(expected = DeletedException.class)
    public void testGetDeleted() throws Exception {
        metadataApiController.delete(doiRequest, false);
        metadataApiController.get(doiRequest);
    }
}
