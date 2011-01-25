package org.datacite.mds.web.api.controller;

import static org.datacite.mds.test.Utils.createAllocator;
import static org.datacite.mds.test.Utils.createDatacentre;
import static org.datacite.mds.test.Utils.setUsernamePassword;
import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.NotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class DatacentreApiControllerTest {

    DatacentreApiController datacentreApiController = new DatacentreApiController();
    
    @Autowired
    ValidationHelper validationHelper;
   

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
        datacentreApiController.validationHelper = this.validationHelper;
        
        allocator = createAllocator(allocatorSymbol);
        allocator.persist();

        datacentre = createDatacentre(datacentreSymbol, allocator);
        datacentre.persist();
        
        setUsernamePassword(allocator.getSymbol(), allocator.getPassword());
    }

    @Test
    public void testGet() throws Exception {
        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test(expected = NotFoundException.class)
    public void testGet404() throws Exception {
        ResponseEntity<? extends Object> result = datacentreApiController.get(allocatorSymbol);
    }
    
    @Test(expected = SecurityException.class)
    public void testGet403NotLoggedIn() throws Exception {
        setUsernamePassword(null, null);
        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
    }

    @Test(expected = SecurityException.class)
    public void testGet403WrongUser() throws Exception {
        setUsernamePassword(allocatorSymbol2, null);
        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
    }
    
    @Test
    public void testUpdate() throws Exception {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }
    
    @Test
    public void testUpdateTestMode() throws Exception {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, true);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }

    @Test
    public void testUpdateTestModeNull() throws Exception {
        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Datacentre updatedDatacentre = (Datacentre) result.getBody();
        assertEquals(newName, updatedDatacentre.getName());
    }
    
    @Test(expected = SecurityException.class)
    public void testUpdate403NotLoggedIn() throws Exception {
        setUsernamePassword(null, null);

        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
    }

    @Test(expected = SecurityException.class)
    public void testUpdate403AnotherOwner() throws Exception {
        setUsernamePassword(allocatorSymbol2, null);

        String newName = "qwrfgqwergv";
        datacentre.setName(newName);
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre, false);
    }
    
    @Test
    @Rollback
    public void testCreate() throws Exception {
        datacentre2 = createDatacentre(datacentreSymbol2, allocator);
        
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre2, false);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }    

    @Test
    public void testCreateTestMode() throws Exception {
        datacentre2 = createDatacentre(datacentreSymbol2, allocator);
        
        ResponseEntity<? extends Object> result = datacentreApiController.createOrUpdate(datacentre2, true);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    } 
}
