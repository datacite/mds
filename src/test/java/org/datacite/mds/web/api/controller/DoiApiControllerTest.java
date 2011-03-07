package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.validation.ValidationException;
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
public class DoiApiControllerTest {
    
    @Autowired DoiService doiService;

    DoiApiController doiApiController = new DoiApiController();
    
    String allocatorSymbol = "TEST";
    String datacentreSymbol = allocatorSymbol + ".TEST";

    Allocator allocator;
    Datacentre datacentre;    
    
    @Before
    public void setUp() throws Exception {
        doiApiController.doiService = this.doiService;
        
        allocator = TestUtils.createAllocator(allocatorSymbol);
        allocator.setPrefixes(TestUtils.createPrefixes("10.5072"));
        allocator.persist();

        datacentre = TestUtils.createDatacentre(datacentreSymbol, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());    
        datacentre.persist();

        TestUtils.login(datacentre);
    }    

    @Test
    public void testCreateOrUpdatePost() throws Exception {
        HttpStatus statusCode = post("10.5072/1111\nhttp://www.example.com", false);
        assertEquals(HttpStatus.CREATED, statusCode);
    }    

    @Test
    public void testCreateOrUpdatePut() throws Exception {
        HttpStatus statusCode = post("10.5072/1111\nhttp://www.example.com", false);
        assertEquals(HttpStatus.CREATED, statusCode);
        statusCode = put("10.5072/1111\nhttp://www.example.com/aaa", false);
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
        post("10.5072/qqq\n", null);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid4() throws Exception {
        post("\nhttp://www.example.com/aaa", true);
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
        return response.getStatusCode();
    }
}
