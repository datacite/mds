package org.datacite.mds.web.api.controller;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import javax.validation.ValidationException;

import org.datacite.mds.service.DoiService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

public class DoiApiControllerTest {
    
    DoiApiController doiApiController = new DoiApiController();
    DoiService mockDoiService;
    
    String doi = "10.5072/1111";
    String url = "http://example.com";

    @Before
    public void setUp() throws Exception {
        mockDoiService = createMock(DoiService.class);
        doiApiController.doiService = this.mockDoiService;
    }    

    @Test
    public void testPost() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testPostCRLF() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\r\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testPostTrailingNewLine() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url + "\n", false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testPut() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
        
        reset(mockDoiService);
        expectDoiServiceUpdate();
        statusCode = put(doi, url, false);
	assertEquals(HttpStatus.OK, statusCode);
    }        
    
    @Test(expected = ValidationException.class)
    public void testPosteEmptyBody() throws Exception {
        post("", null);
    }

    @Test(expected = ValidationException.class)
    public void testPostEmptyLines1() throws Exception {
        post("\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testPostEmptyLines2() throws Exception {
        post("\n\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testPostOneLine() throws Exception {
        post(doi, null);
    }
    
    @Test(expected = ValidationException.class)
    public void testPostEmptyUrl() throws Exception {
        post(doi + "\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testPostEmptyDoi() throws Exception {
        post("\n" + url, true);
    }

    @Test(expected = ValidationException.class)
    public void testPostIntermediateEmptyLine() throws Exception {
        post(doi + "\n\n" + url, true);
    }

    @Test(expected = ValidationException.class)
    public void testPostBodyWithManyTrailingNewlines() throws Exception {
        post(doi + "\n" + url + "\n\n", true);
    }

    @Test(expected = ValidationException.class)
    public void testPostBodyWithThreeLines() throws Exception {
        post(doi + "\n" + url + "\n" + "foobar", true);
    }

    private void expectDoiServiceCreate() throws Exception {
        expect(mockDoiService.create(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
    }

    private void expectDoiServiceUpdate() throws Exception {
        expect(mockDoiService.update(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
    }
    
    private MockHttpServletRequest makeServletRequestForDoi(String doi) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/doi/" + doi);
        return request;
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
