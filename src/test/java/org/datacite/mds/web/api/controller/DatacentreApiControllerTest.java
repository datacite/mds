package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
//@Transactional
public class DatacentreApiControllerTest {

    DatacentreApiController datacentreApiController = new DatacentreApiController();

    String allocatorSymbol = "TEST";
    String allocatorSymbol2 = "XYZ";
    String datacentreSymbol = allocatorSymbol+".TEST";
    String datacentreSymbol2 = allocatorSymbol+".NEWTEST";
    
    Allocator allocator;
    Datacentre datacentre; 
    Datacentre datacentre2;
    
    @After
    public void tearDown() {
        datacentre.remove();
        allocator.remove();
    }
    
    @Before
    public void setUp() throws Exception { 
        allocator = new Allocator();
        allocator.setSymbol("TEST");
        allocator.setPassword("12345678");
        allocator.setContactEmail("aaa@aaa.com");
        allocator.setContactName("aaaaaaaaaaaaaa");
        allocator.setDoiQuotaAllowed(-1);
        allocator.setDoiQuotaUsed(0);
        allocator.setIsActive(true);
        allocator.setName("aaaaaaaaaa");
        allocator.setRoleName("ROLE_ALLOCATOR");
        allocator.persist();

        datacentre = new Datacentre();
        datacentre.setSymbol(datacentreSymbol);
        datacentre.setAllocator(allocator);
        datacentre.setContactEmail("aaa@aaa.com");
        datacentre.setContactName("aaaa");
        datacentre.setDoiQuotaAllowed(-1);
        datacentre.setDoiQuotaUsed(0);
        datacentre.setDomains("bl.uk");
        datacentre.setIsActive(true);
        datacentre.setName("aaaaaaaaaa");
        datacentre.setRoleName("ROLE_DATACENTRE");
        datacentre.persist();
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(allocator.getSymbol(), allocator.getPassword()));        
    }

    @Test
    public void testGet() {
        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testGet404() {
        ResponseEntity<? extends Object> result = datacentreApiController.get(allocatorSymbol);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    
    @Test
    public void testGet403NotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null));        

        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testGet403WrongUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(allocatorSymbol2, null));        

        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }
    
    @Test
    public void testUpdate() {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }
    
    @Test
    public void testUpdateTestMode() {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, true);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }

    @Test
    public void testUpdateTestModeNull() {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }
    
    @Test
    public void testUpdate403NotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null));        

        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testUpdate403AnotherOwner() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(allocatorSymbol2, null));        

        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }
    
/*    @Test
    @Rollback
    public void testCreate() {
        datacentre2 = new Datacentre();
        datacentre2.setSymbol(datacentreSymbol2);
        datacentre2.setAllocator(allocator);
        datacentre2.setContactEmail("aaa@aaa.com");
        datacentre2.setContactName("aaaa");
        datacentre2.setDoiQuotaAllowed(-1);
        datacentre2.setDoiQuotaUsed(0);
        datacentre2.setDomains("bl.uk");
        datacentre2.setIsActive(true);
        datacentre2.setName("aaaaaaaaaa");
        datacentre2.setRoleName("ROLE_DATACENTRE");
        
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre2, false);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }    
*/
    @Test
    public void testCreateTestMode() {
        datacentre2 = new Datacentre();
        datacentre2.setSymbol(datacentreSymbol2);
        datacentre2.setAllocator(allocator);
        datacentre2.setContactEmail("aaa@aaa.com");
        datacentre2.setContactName("aaaa");
        datacentre2.setDoiQuotaAllowed(-1);
        datacentre2.setDoiQuotaUsed(0);
        datacentre2.setDomains("bl.uk");
        datacentre2.setIsActive(true);
        datacentre2.setName("aaaaaaaaaa");
        datacentre2.setRoleName("ROLE_DATACENTRE");
        
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre2, true);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    } 
}
