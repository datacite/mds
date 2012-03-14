package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.web.api.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class MediaApiControllerTest {

    MediaApiController mediaApiController = new MediaApiController();

    String doi = "10.5072/fooBAR";

    private Allocator allocator;
    private Allocator wrongAllocator;
    private Datacentre datacentre;
    private Datacentre wrongDatacentre;
    private Dataset dataset;

    private HttpServletRequest doiRequest;
    private HttpServletRequest wrongDoiRequest;


    @Before
    public void init() throws Exception {
        datacentre = TestUtils.createDefaultDatacentre("10.5072");
        allocator = datacentre.getAllocator();
        
        wrongAllocator = TestUtils.createAllocator("OTHER");
        wrongAllocator.persist();
        wrongDatacentre = TestUtils.createDatacentre("OTHER.OTHER", wrongAllocator);
        wrongDatacentre.persist();

        dataset = TestUtils.createDataset(doi, datacentre);
        dataset.persist();

        doiRequest = makeServletRequestForDoi(doi);
        wrongDoiRequest = makeServletRequestForDoi(doi + 1);

        TestUtils.login(datacentre);
    }

    private MockHttpServletRequest makeServletRequestForDoi(String doi) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/media/" + doi);
        return request;
    }

    @Test
    public void testGet() throws Exception {
        String mediaType1 = "application/xml";
        String url1 = "http://example.com/example.xml";
        Media media1 = TestUtils.createMedia(mediaType1, url1, dataset);
        media1.persist();

        String mediaType2 = "application/pdf";
        String url2 = "http://example.com/example.pdf";
        Media media2 = TestUtils.createMedia(mediaType2, url2, dataset);
        media2.persist();

        String bodyExpected = mediaType2 + "=" + url2 + "\n" + mediaType1 + "=" + url1 + "\n";

        ResponseEntity<? extends Object> response = mediaApiController.get(doiRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bodyExpected, response.getBody());
    }

    @Test(expected = NotFoundException.class)
    public void testGetNonExistingDataset() throws Exception {
        dataset.remove();
        mediaApiController.get(doiRequest);
    }
    
    @Test(expected = NotFoundException.class)
    public void testGetNonExistingMedia() throws Exception {
        mediaApiController.get(doiRequest);
    }

    @Test(expected = SecurityException.class)
    public void testGetAsForeignDatacentre() throws Exception {
        TestUtils.login(wrongDatacentre);
        mediaApiController.get(doiRequest);
    }

    @Test
    public void testGetAsAllocator() throws Exception {
        TestUtils.login(allocator);
        testGet();
    }

    @Test(expected = SecurityException.class)
    public void testGetAsForeignAllocator() throws Exception {
        TestUtils.login(wrongAllocator);
        mediaApiController.get(doiRequest);
    }

    @Test(expected = SecurityException.class)
    public void testGetNotLoggedIn() throws Exception {
        TestUtils.logout();
        mediaApiController.get(doiRequest);
    }

}
