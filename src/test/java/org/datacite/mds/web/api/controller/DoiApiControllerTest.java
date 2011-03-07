package org.datacite.mds.web.api.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import org.datacite.mds.service.DoiService;
import org.datacite.mds.validation.ValidationException;
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
    public void testCreateOrUpdateNonValid() throws Exception {
        post("", null);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid2() throws Exception {
        post("\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid2a() throws Exception {
        post("\n\n", null);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid3() throws Exception {
        post(doi + "\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid4() throws Exception {
        post("\n" + url, true);
    }
    
    private void expectDoiServiceCreate() throws Exception {
        expect(mockDoiService.create(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
        replay(mockDoiService);
    }

    private void expectDoiServiceUpdate() throws Exception {
        expect(mockDoiService.update(eq(doi), eq(url), anyBoolean())).andStubReturn(null);
        replay(mockDoiService);
    }

    private HttpStatus post(String body, Boolean testMode) throws Exception {
        return request("POST", body, testMode);
    }
    
    private HttpStatus put(String body, Boolean testMode) throws Exception {
        return request("PUT", body, testMode);
    }
    
    private HttpStatus request(String method, String body, Boolean testMode) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(method);
        ResponseEntity<? extends Object> response = doiApiController.createOrUpdate(body, testMode, request);
        verify(mockDoiService);
        return response.getStatusCode();
    }
}
