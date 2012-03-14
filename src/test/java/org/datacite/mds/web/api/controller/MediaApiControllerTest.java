package org.datacite.mds.web.api.controller;

import static org.junit.Assert.assertEquals;


import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Media;
import org.datacite.mds.test.TestUtils;
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

    private Dataset dataset;

    @Before
    public void init() throws Exception {
        Datacentre datacentre = TestUtils.createDefaultDatacentre("10.5072");
        dataset = TestUtils.createDataset(doi, datacentre);
        dataset.persist();
    }
    
    @Test
    public void testGet() throws Exception {
        String mediaType1 = "application/pdf";
        String url1 = "http://example.com/example.pdf";
        Media media1 = TestUtils.createMedia(mediaType1, url1, dataset);
        media1.persist();

        String mediaType2 = "application/xml";
        String url2 = "http://example.com/example.xml";
        Media media2 = TestUtils.createMedia(mediaType2, url2, dataset);
        media2.persist();

        String bodyExpected = mediaType1 + "=" + url1 + "\n" + mediaType2 + "=" + url2;

        ResponseEntity<? extends Object> response = get(doi);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bodyExpected, response.getBody());
    }
    
    private ResponseEntity<? extends Object> get(String doi) throws Exception {
        MockHttpServletRequest httpRequest = makeServletRequestForDoi(doi);
        ResponseEntity<? extends Object> response = mediaApiController.get(httpRequest);
        return response;
    }
    
    private MockHttpServletRequest makeServletRequestForDoi(String doi) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/media/" + doi);
        return request;
    }
    
}
