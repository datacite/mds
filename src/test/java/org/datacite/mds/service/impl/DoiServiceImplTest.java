package org.datacite.mds.service.impl;

import static org.datacite.mds.test.TestUtils.createAllocator;
import static org.datacite.mds.test.TestUtils.createDatacentre;
import static org.datacite.mds.test.TestUtils.createDataset;
import static org.datacite.mds.test.TestUtils.createPrefixes;
import static org.datacite.mds.test.TestUtils.login;
import static org.junit.Assert.assertEquals;

import javax.validation.ValidationException;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.web.api.NotFoundException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

// TODO test malformed doi & url

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class DoiServiceImplTest {

    @Autowired
    private DoiServiceImpl doiService;

    final String ALLOCATOR_SYMBOL = "AL";
    final String DATACENTRE_SYMBOL = "AL.DC";
    final String DATACENTRE_SYMBOL2 = "AL.DC-TWO";
    final String PREFIX = "10.5072";
    final String DOI = "10.5072/TEST";
    final String DOI_WRONG = "10.1000/TEST";
    final String DOMAIN = "example.com";
    final String URL = "http://" + DOMAIN;
    final String URL_WRONG = "http://wrong.invalid";
    final Integer DOI_QUOTA_USED = 42;

    Datacentre datacentre;
    
    private HandleService mockHandleService;

    @Before
    public void init() {
        Allocator allocator = createAllocator(ALLOCATOR_SYMBOL);
        allocator.setPrefixes(createPrefixes(PREFIX));
        allocator.persist();
        datacentre = createDatacentre(DATACENTRE_SYMBOL, allocator);
        datacentre.setPrefixes(allocator.getPrefixes());
        datacentre.setDomains(DOMAIN);
        datacentre.setDoiQuotaUsed(DOI_QUOTA_USED);
        datacentre.persist();
        mockHandleService = EasyMock.createMock(HandleService.class);
        doiService.handleService = mockHandleService;
        expectNoDoiServiceCall();

        login(datacentre);
        datacentre.flush();
    }
    
    private void expectNoDoiServiceCall() {
        EasyMock.reset(mockHandleService);
        EasyMock.replay(mockHandleService);
    }
    
    @After
    public void verify() {
        EasyMock.verify(mockHandleService);
    }

    // CREATE

    @Test
    public void testCreate() throws Exception {
        expectDoiServiceCreate(DOI, URL);
        Dataset dataset = doiService.create(DOI, URL, false);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    private void expectDoiServiceCreate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.create(doi, url);
        EasyMock.replay(mockHandleService);
    }
    
    @Test
    public void testCreateTestMode() throws Exception {
        Dataset dataset = doiService.create(DOI, URL, true);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    @Test
    public void testCreateEmptyURL() throws Exception {
        Dataset dataset = doiService.create(DOI, "", false);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    @Test(expected = ValidationException.class)
    public void testCreateWrongPrefix() throws Exception {
        doiService.create(DOI_WRONG, URL, false);
    }

    @Test(expected = SecurityException.class)
    public void testCreateQuotaExceeded() throws Exception {
        datacentre.setDoiQuotaAllowed(DOI_QUOTA_USED);
        doiService.create(DOI, URL, false);
    }

    @Test(expected = ValidationException.class)
    public void testCreateWrongDomain() throws Exception {
        doiService.create(DOI, URL_WRONG, false);
    }

    @Test(expected = SecurityException.class)
    public void testCreateNotLoggedIn() throws Exception {
        login(null);
        doiService.create(DOI, URL, false);
    }

    // UPDATE

    @Test
    public void testUpdate() throws Exception {
        createDataset(DOI, datacentre).persist();
        expectDoiServiceUpdate(DOI, URL);
        doiService.update(DOI, URL, false);
    }
    
    private void expectDoiServiceUpdate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.update(doi, url);
        EasyMock.replay(mockHandleService);
    }
    
    @Test
    public void testUpdateTestMode() throws Exception {
        createDataset(DOI, datacentre).persist();
        doiService.update(DOI, URL, true);
    }

    @Test
    public void testUpdateEmptyUrl() throws Exception {
        createDataset(DOI, datacentre).persist();
        doiService.update(DOI, "", false);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateWrongDomain() throws Exception {
        createDataset(DOI, datacentre).persist();
        doiService.update(DOI, URL_WRONG, false);
    }

    @Test(expected = SecurityException.class)
    public void testUpdateNonBelongingDataset() throws Exception {
        Datacentre datacentre2 = createDatacentre(DATACENTRE_SYMBOL2, datacentre.getAllocator());
        datacentre2.setPrefixes(datacentre.getPrefixes());
        datacentre2.persist();
        createDataset(DOI, datacentre2).persist();
        doiService.update(DOI, URL, false);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateNonExistingDataset() throws Exception {
        doiService.update(DOI, URL, false);
    }

}
