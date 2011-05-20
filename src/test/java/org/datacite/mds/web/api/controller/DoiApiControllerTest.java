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
    public void testCreateOrUpdatePost() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testCreateOrUpdatePostCRLF() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\r\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testCreateOrUpdatePostTrailingNewLine() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url + "\n", false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }

    @Test
    public void testCreateOrUpdatePut() throws Exception {
        expectDoiServiceCreate();
        HttpStatus statusCode = post(doi + "\n" + url, false);
        assertEquals(HttpStatus.CREATED, statusCode);
        
        reset(mockDoiService);
        expectDoiServiceUpdate();
        statusCode = put(doi + "\n" + url, false);
	assertEquals(HttpStatus.OK, statusCode);
    }        
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateEmptyBody() throws Exception {
        post("", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateEmptyLines1() throws Exception {
        post("\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateEmptyLines2() throws Exception {
        post("\n\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateOneLine() throws Exception {
        post(doi, null);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateEmptyUrl() throws Exception {
        post(doi + "\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateEmptyDoi() throws Exception {
        post("\n" + url, true);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateIntermediateEmptyLine() throws Exception {
        post(doi + "\n\n" + url, true);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateBodyWithManyTrailingNewlines() throws Exception {
        post(doi + "\n" + url + "\n\n", true);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateBodyWithThreeLines() throws Exception {
        post(doi + "\n" + url + "\n" + "foobar", true);
    }

    private void expectDoiServiceCreate() throws Exception {
        expect(mockDoiService.create(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
    }

    private void expectDoiServiceUpdate() throws Exception {
        expect(mockDoiService.update(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
    }

    private HttpStatus post(String body, Boolean testMode) throws Exception {
        return request("POST", body, testMode);
    }
    
    private HttpStatus put(String body, Boolean testMode) throws Exception {
        return request("PUT", body, testMode);
    }
    
    private HttpStatus request(String method, String body, Boolean testMode) throws Exception {
        replay(mockDoiService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(method);
        ResponseEntity<? extends Object> response = doiApiController.createOrUpdate(body, testMode, request);
        verify(mockDoiService);
        return response.getStatusCode();
    }
}
