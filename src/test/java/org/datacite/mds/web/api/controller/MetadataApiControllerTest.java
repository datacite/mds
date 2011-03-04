package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.DeletedException;
import org.datacite.mds.web.api.NotFoundException;
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
public class MetadataApiControllerTest {

	@Autowired
	DoiService doiService;
	@Autowired
	ValidationHelper validationHelper;

	MetadataApiController metadataApiController = new MetadataApiController();

	String allocatorSymbol = "TEST";
	String datacentreSymbol = allocatorSymbol + ".TEST";
	String prefix = "10.5072";
	String doi = prefix + "/1";

	Allocator allocator;
	Datacentre datacentre;
	Dataset dataset;
	Metadata metadata;
	
	public void initDb(boolean noMeta) throws Exception {
		metadataApiController.doiService = doiService;
		metadataApiController.validationHelper = validationHelper;

		allocator = TestUtils.createAllocator(allocatorSymbol);
		allocator.setPrefixes(TestUtils.createPrefixes(prefix));
		allocator.persist();

		datacentre = TestUtils.createDatacentre(datacentreSymbol, allocator);
		datacentre.setPrefixes(allocator.getPrefixes());
		datacentre.persist();

		dataset = TestUtils.createDataset(doi, datacentre);
		dataset.setIsActive(true);
		dataset.persist();

		if (!noMeta) {
			metadata = new Metadata();
			metadata.setDataset(dataset);			
			metadata.setXml(TestUtils.getTestMetadata());
			metadata.persist();
		}

		TestUtils.login(datacentre);
	}

	@Test(expected = NotFoundException.class)
	public void testGet404() throws Exception {
		initDb(true);
		metadataApiController.get(doi);
	}

	@Test(expected = NotFoundException.class)
	public void testGetNoDoi() throws Exception {
		initDb(true);
		metadataApiController.get(doi+1);
	}	
	
	@Test
	public void testGet() throws Exception {
		initDb(false);
		ResponseEntity<? extends Object> response = metadataApiController.get(doi);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testCreateOrUpdate() throws Exception {
		initDb(false);
		String xmlAsString = new String(TestUtils.getTestMetadata(), "UTF-8");
		String url = "http://www.example.com";
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setMethod("PUT");
		ResponseEntity<? extends Object> response = metadataApiController.createOrUpdate(xmlAsString , doi, url , null, httpRequest);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	public void testCreateOrUpdateTestMode() throws Exception {
		initDb(false);
		String xmlAsString = new String(TestUtils.getTestMetadata(), "UTF-8");
		String url = "http://www.example.com";
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setMethod("PUT");
		ResponseEntity<? extends Object> response = metadataApiController.createOrUpdate(xmlAsString , doi, url , true, httpRequest);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}	

	@Test
	public void testCreateOrUpdatePOST() throws Exception {
		initDb(false);
		String xmlAsString = new String(metadata.getXml(), "UTF-8");
		String url = "http://www.example.com";
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setMethod("POST");
		ResponseEntity<? extends Object> response = metadataApiController.createOrUpdate(xmlAsString , doi, url , false, httpRequest);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}
		
	@Test
	public void testDelete() throws Exception {
		initDb(false);
		ResponseEntity<? extends Object> response = metadataApiController.delete(doi, null);
		assertEquals(HttpStatus.OK, response.getStatusCode());		
	}

	@Test(expected = NotFoundException.class)
	public void testDelete404() throws Exception {
		initDb(false);
		metadataApiController.delete(doi+1, false);
	}	
	
	@Test
	public void testDeleteTestMode() throws Exception {
		initDb(false);
		ResponseEntity<? extends Object> response = metadataApiController.delete(doi, true);
		assertEquals(HttpStatus.OK, response.getStatusCode());		
	}	
	
	@Test
	public void testUnDelete() throws Exception {
		initDb(false);
		ResponseEntity<? extends Object> response = metadataApiController.delete(doi, false);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		response = metadataApiController.delete(doi, false);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());		
	
	}
		
	@Test(expected = DeletedException.class)
	public void testGetDeleted() throws Exception {
		initDb(false);
		metadataApiController.delete(doi, false);
		metadataApiController.get(doi);
	}
}
