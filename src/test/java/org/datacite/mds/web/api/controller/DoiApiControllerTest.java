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
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        ResponseEntity<? extends Object> response = doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com", false, request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }    

    @Test
    public void testCreateOrUpdatePut() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        ResponseEntity<? extends Object> response = doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com", false, request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
        request.setMethod("PUT");
        response = doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com/aaa", false, request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
    }        
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("", null, request);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("\n", null, request);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid2a() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("\n\n", null, request);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid3() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("10.5072/qqq\n", null, request);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateNonValid4() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("\nhttp://www.example.com/aaa", true, request);
    }    
}
