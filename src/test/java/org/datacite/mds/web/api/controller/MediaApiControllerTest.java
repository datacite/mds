package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

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
        Media media1 = TestUtils.createMedia("application/xml", "http://example.com/example.xml", dataset);
        media1.persist();
        Media media2 = TestUtils.createMedia("application/pdf", "http://example.com/example.pdf", dataset);
        media2.persist();

        String bodyExpected = createBody(media2, media1);

        ResponseEntity<? extends Object> response = mediaApiController.get(doiRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bodyExpected, response.getBody());
    }

    private String createBody(Media... medias) {
        StringBuffer body = new StringBuffer();
        for (Media media : medias)
            body.append(media.getMediaType() + "=" + media.getUrl() + "\n");
        return body.toString();
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

    @Test
    public void testPost() throws Exception {
        Media media1 = TestUtils.createMedia("application/pdf", "http://example.com/example.pdf", dataset);
        Media media2 = TestUtils.createMedia("application/xml", "http://example.com/example.xml", dataset);

        String body = createBody(media1, media2);
        ResponseEntity<String> response = mediaApiController.post(body, false, doiRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertDatasetHasMedia(dataset, media1, media2);
    }
    
    @SuppressWarnings("unchecked")
    private void assertDatasetHasMedia(Dataset dataset, Media... medias) {
        List<Media> mediasActual = Media.findMediasByDataset(dataset).getResultList();
        List<Media> mediasExpected = Arrays.asList(medias);
        
        Collections.sort(mediasActual);
        Collections.sort(mediasExpected);
        
        assertEquals(mediasExpected.size(), mediasActual.size());
        for (int i = 0; i < mediasExpected.size(); i++)
            assertMediaEquals(mediasExpected.get(i), mediasActual.get(i));
    }

    private void assertMediaEquals(Media expected, Media actual) {
        assertEquals(expected.getMediaType(), actual.getMediaType());
        assertEquals(expected.getUrl(), actual.getUrl());
    }
    
    @Test
    public void testPostAddMediaType() throws Exception {
        Media mediaExisting = TestUtils.createMedia("application/pdf", "http://example.com/example.pdf", dataset);
        mediaExisting.persist();
        Media mediaNew = TestUtils.createMedia("application/xml", "http://example.com/example.xml", dataset);
        assertDatasetHasMedia(dataset, mediaExisting);

        String body = createBody(mediaNew);
        ResponseEntity<String> response = mediaApiController.post(body, false, doiRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertDatasetHasMedia(dataset, mediaExisting, mediaNew);
    }
    
    @Test
    public void testPostUpdateMediaType() throws Exception {
        Media mediaExisting = TestUtils.createMedia("application/pdf", "http://example.com/example.pdf", dataset);
        mediaExisting.persist();
        Media mediaUpdate = TestUtils.createMedia("application/pdf", "http://example.com/example-new.pdf", dataset);
        assertDatasetHasMedia(dataset, mediaExisting);

        String body = createBody(mediaUpdate);
        ResponseEntity<String> response = mediaApiController.post(body, false, doiRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertDatasetHasMedia(dataset, mediaUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void testPostWrongMediaType() throws Exception {
        Media media = TestUtils.createMedia("foo/bar", "http://example.com/example.pdf", dataset);
        String body = createBody(media);
        mediaApiController.post(body, false, doiRequest);
    }

    @Test(expected = ValidationException.class)
    public void testPostWrongDomain() throws Exception {
        Media media = TestUtils.createMedia("application/pdf", "http://foo.com/example.pdf", dataset);
        String body = createBody(media);
        mediaApiController.post(body, false, doiRequest);
    }
    
    @Test(expected = NotFoundException.class)
    public void testPostNonExistingDataset() throws Exception {
        dataset.remove();
        testPost();
    }
    
    @Test(expected = SecurityException.class)
    public void testPostAsForeignDatacentre() throws Exception {
        TestUtils.login(wrongDatacentre);
        testPost();
    }

    @Test
    public void testPostTestMode() throws Exception {
        Media mediaOld = TestUtils.createMedia("application/pdf", "http://example.com/example.pdf", dataset);
        mediaOld.persist();
        Media mediaUpdated = TestUtils.createMedia("application/pdf", "http://example.com/example2.pdf", dataset);
        Media mediaNew = TestUtils.createMedia("application/xml", "http://example.com/example.xml", dataset);
        assertDatasetHasMedia(dataset, mediaOld);

        String body = createBody(mediaUpdated, mediaNew);
        ResponseEntity<String> response = mediaApiController.post(body, true, doiRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertDatasetHasMedia(dataset, mediaOld);
    }

}
