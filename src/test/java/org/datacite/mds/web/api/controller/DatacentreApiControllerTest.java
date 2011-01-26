package org.datacite.mds.web.api.controller;

import static org.datacite.mds.test.Utils.createAllocator;
import static org.datacite.mds.test.Utils.createDatacentre;
import static org.datacite.mds.test.Utils.createPrefixes;
import static org.datacite.mds.test.Utils.login;
import static org.junit.Assert.assertEquals;

import javax.validation.ValidationException;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    String datacentreSymbol = allocatorSymbol + ".TEST";
    String datacentreSymbol2 = allocatorSymbol + ".NEWTEST";

    Allocator allocator;
    Allocator allocator2;
    Datacentre datacentre;
    Datacentre datacentre2;

    @Before
    public void setUp() throws Exception {
        datacentreApiController.validationHelper = this.validationHelper;

        allocator = createAllocator(allocatorSymbol);
        allocator.setPrefixes(createPrefixes("10.5072"));
        allocator.persist();

        allocator2 = createAllocator(allocatorSymbol2);
        allocator2.persist();

        datacentre = createDatacentre(datacentreSymbol, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());
        datacentre.persist();

        login(allocator);
    }

    @Test
    public void testGet() throws Exception {
        ResponseEntity<? extends Object> result = datacentreApiController.get(datacentreSymbol);
        Datacentre returnedDatacentre = (Datacentre) result.getBody();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(datacentre.getSymbol(), returnedDatacentre.getSymbol());
    }

    @Test(expected = NotFoundException.class)
    public void testGetNotFound() throws Exception {
        datacentreApiController.get(allocatorSymbol);
    }

    @Test(expected = SecurityException.class)
    public void testGetNotLoggedIn() throws Exception {
        login(null);
        datacentreApiController.get(datacentreSymbol);
    }

    @Test(expected = SecurityException.class)
    public void testGetAnotherOwner() throws Exception {
        login(allocator2);
        datacentreApiController.get(datacentreSymbol);
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
    public void testUpdateNotLoggedIn() throws Exception {
        login(null);
        datacentreApiController.createOrUpdate(datacentre, false);
    }

    @Test(expected = SecurityException.class)
    public void testUpdateAnotherOwner() throws Exception {
        login(allocator2);
        datacentreApiController.createOrUpdate(datacentre, false);
    }

    @Test
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

    @Test(expected = ValidationException.class)
    public void testCreateNonExistingPrefix() throws Exception {
        String nonExistingPrefix = "10.4711";
        datacentre2 = createDatacentre(datacentreSymbol2, allocator);
        datacentre2.setPrefixes(createPrefixes(nonExistingPrefix));
        datacentreApiController.createOrUpdate(datacentre2, false);
    }
}
