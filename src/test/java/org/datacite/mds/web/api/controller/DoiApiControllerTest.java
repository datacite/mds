package org.datacite.mds.web.api.controller;

import static org.junit.Assert.*;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.Utils;
import org.datacite.mds.validation.ValidationException;
import org.datacite.mds.web.api.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
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
        
        allocator = Utils.createAllocator(allocatorSymbol);
        allocator.setPrefixes(Utils.createPrefixes("10.5072"));
        allocator.persist();

        datacentre = Utils.createDatacentre(datacentreSymbol, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());    
        datacentre.persist();

        Utils.login(datacentre);
    }    

    @Test
    @Rollback
    public void testCreateOrUpdatePost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com", false, request);
    }    

    @Test
    @Rollback
    public void testCreateOrUpdatePut() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com", false, request);
        request.setMethod("PUT");
        doiApiController.createOrUpdate("10.5072/1111\nhttp://www.example.com/aaa", false, request);
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
