package org.datacite.mds.web.api.controller;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.validation.ValidationException;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class DoiApiControllerTest {

    DoiApiController doiApiController = new DoiApiController();
    DoiService mockDoiService;

    String doi = "10.5072/fooBAR";
    String url = "http://example.com";

    @Before
    public void setUp() throws Exception {
        mockDoiService = createMock(DoiService.class);
        doiApiController.doiService = this.mockDoiService;
        doiApiController.metadataRequired = false;
    }

    @Test
    public void testGetRoot() throws Exception {
        ResponseEntity response = doiApiController.getRoot();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
    
    @Test
    public void testGet() throws Exception {
        expectDoiServiceResolve(url);
        ResponseEntity<? extends Object> response = get(doi);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(url, response.getBody());
    }
    
    @Test
    public void testGetNullUrl() throws Exception {
        expectDoiServiceResolve(null);
        ResponseEntity<? extends Object> response = get(doi);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPost() throws Exception {
        expectDoiServiceCreateOrUpdate();
        HttpStatus statusCode = post("doi=" + doi + "\nurl=" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testPostSpecial() throws Exception {
        expectDoiServiceCreateOrUpdate();
        String body = String.format("url=foobar\n doi=%s\r\n Url=%s\n#doi=foobar", doi, url);
        HttpStatus statusCode = post(body, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test(expected = ValidationException.class)
    public void testPostMissingDoi() throws Exception {
        post("doi=" + doi, false);
    }

    @Test(expected = ValidationException.class)
    public void testPostMissingUrl() throws Exception {
        post("url=" + url, false);
    }
    
    @Test
    public void testPostMetadataRequired() throws Exception {
        doiApiController.metadataRequired = true;
        expectDoiServiceCreateOrUpdate();
        persistMetadata();
        HttpStatus statusCode = post("doi=" + doi + "\nurl=" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }
    
    private Dataset persistDataset() {
        Datacentre datacentre = TestUtils.createDefaultDatacentre("10.5072");
        Dataset dataset = TestUtils.createDataset(doi, datacentre);
        dataset.persist();
        return dataset;
    }
    
    private Metadata persistMetadata() {
        Dataset dataset = persistDataset();
        byte[] xml = TestUtils.getTestMetadata();
        Metadata metadata = TestUtils.createMetadata(TestUtils.setDoiOfMetadata(xml, doi), dataset);
        metadata.persist();
        return metadata;
    }

    @Test
    public void testPostMetadataRequiredNoMetadata() throws Exception {
        doiApiController.metadataRequired = true;
        expectDoiServiceCreateOrUpdate();
        persistDataset();
        HttpStatus statusCode = post("doi=" + doi + "\nurl=" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }
    
    @Test
    public void testPostMetadataRequiredNoDataset() throws Exception {
        doiApiController.metadataRequired = true;
        HttpStatus statusCode = post("doi=" + doi + "\nurl=" + url, false);
        assertEquals(HttpStatus.PRECONDITION_FAILED, statusCode);
    }

    @Test
    public void testPut() throws Exception {
        expectDoiServiceCreateOrUpdate();
        HttpStatus statusCode = put(doi, "doi=" + doi + "\nurl=" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }
    
    @Test
    public void testPutDifferentCase() throws Exception {
        expectDoiServiceCreateOrUpdate();
        HttpStatus statusCode = put(doi.toLowerCase(), "doi=" + doi.toUpperCase() + "\nurl=" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }


    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testPutNoDoi() throws Exception {
        doiApiController.putRoot();
    }
    
    @Test(expected = ValidationException.class)
    public void testPutMismatchingDoi() throws Exception {
        put(doi + "-wrong", "doi=" + doi + "\nurl=" + url, false);
    }
    
    @Test(expected = ValidationException.class)
    public void testPutMissingDoi() throws Exception {
        put(doi, "doi=" + doi, false);
    }

    @Test(expected = ValidationException.class)
    public void testPutMissingUrl() throws Exception {
        put(doi, "url=" + url, false);
    }

    private void expectDoiServiceCreateOrUpdate() throws Exception {
        expect(mockDoiService.createOrUpdate(eq(doi.toUpperCase()), eq(url), anyBoolean())).andStubReturn(null);
    }
    
    private void expectDoiServiceResolve(String url) throws Exception {
        Dataset dataset = new Dataset();
        dataset.setUrl(url);
        expect(mockDoiService.resolve(eq(doi.toUpperCase()))).andReturn(dataset);
    }

    private MockHttpServletRequest makeServletRequestForDoi(String doi) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/doi/" + doi);
        return request;
    }
    
    private ResponseEntity<? extends Object> get(String doi) throws Exception {
        MockHttpServletRequest httpRequest = makeServletRequestForDoi(doi);
        replay(mockDoiService);
        ResponseEntity<? extends Object> response = doiApiController.get(httpRequest);
        verify(mockDoiService);
        return response;
    }
    
    private HttpStatus post(String body, Boolean testMode) throws Exception {
        MockHttpServletRequest httpRequest = makeServletRequestForDoi(null);
        httpRequest.setMethod("POST");
        replay(mockDoiService);
        ResponseEntity<? extends Object> response = doiApiController.post(body, testMode, httpRequest);
        verify(mockDoiService);
        return response.getStatusCode();
    }

    private HttpStatus put(String doi, String body, Boolean testMode) throws Exception {
        MockHttpServletRequest httpRequest = makeServletRequestForDoi(doi);
        httpRequest.setMethod("PUT");
        replay(mockDoiService);
        ResponseEntity<? extends Object> response = doiApiController.put(body, testMode, httpRequest);
        verify(mockDoiService);
        return response.getStatusCode();
    }

}
