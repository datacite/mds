package org.datacite.mds.service.impl;

import static org.datacite.mds.test.TestUtils.createAllocator;
import static org.datacite.mds.test.TestUtils.createDatacentre;
import static org.datacite.mds.test.TestUtils.createDataset;
import static org.datacite.mds.test.TestUtils.createPrefixes;
import static org.datacite.mds.test.TestUtils.login;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
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
    final String DOI_WITH_SPACES="10.5072/foo bar";
    final String DOI_WRONG = "10.1000/TEST";
    final String DOMAIN = "example.com";
    final String URL = "http://" + DOMAIN;
    final String URL_WRONG = "http://wrong.invalid";
    final Integer DOI_QUOTA_USED = 42;

    Datacentre datacentre;
    Datacentre datacentre2;

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
        datacentre2 = createDatacentre(DATACENTRE_SYMBOL2, datacentre.getAllocator());
        datacentre2.setPrefixes(datacentre.getPrefixes());
        datacentre2.persist();
        mockHandleService = EasyMock.createMock(HandleService.class);
        doiService.handleService = mockHandleService;
        expectNoHandleServiceCall();

        login(datacentre);
        datacentre.flush();
    }

    private void expectNoHandleServiceCall() {
        EasyMock.reset(mockHandleService);
        EasyMock.replay(mockHandleService);
    }

    @After
    public void verify() {
        EasyMock.verify(mockHandleService);
    }

    // CREATE

    @Test
    public void testCreateOrUpdate() throws Exception {
        expectHandleServiceCreate(DOI, URL);
        Dataset dataset = doiService.createOrUpdate(DOI, URL, false);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    private void expectHandleServiceCreate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.create(doi, url);
        EasyMock.replay(mockHandleService);
    }

    @Test
    public void testCreateOrUpdateTestMode() throws Exception {
        Dataset dataset = doiService.createOrUpdate(DOI, URL, true);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    @Test
    public void testCreateOrUpdateEmptyURL() throws Exception {
        Dataset dataset = doiService.createOrUpdate(DOI, "", false);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateWrongPrefix() throws Exception {
        doiService.createOrUpdate(DOI_WRONG, URL, false);
    }

    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateQuotaExceeded() throws Exception {
        datacentre.setDoiQuotaAllowed(DOI_QUOTA_USED);
        doiService.createOrUpdate(DOI, URL, false);
    }

    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateWrongDomain() throws Exception {
        doiService.createOrUpdate(DOI, URL_WRONG, false);
    }

    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateNotLoggedIn() throws Exception {
        login(null);
        doiService.createOrUpdate(DOI, URL, false);
    }
    
    @Test
    public void testCreateOrUpdateExistingDataset() throws Exception {
        createDataset(DOI, datacentre).persist();
        expectHandleServiceCreate(DOI, URL);
        doiService.createOrUpdate(DOI, URL, false);
    }

    @Test
    public void testCreateOrUpdateExistingHandle() throws Exception {
        expectHandleServiceUpdate(DOI, URL);
        doiService.createOrUpdate(DOI, URL, false);
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateOrUpdateDoiWithSpaces() throws Exception {
        doiService.createOrUpdate(DOI_WITH_SPACES, URL, false);
    }

    @Test
    public void testCreateOrUpdateExistingDoiWithSpaces() throws Exception {
        Dataset dataset = createDataset(DOI, datacentre);
        dataset.persist();
        dataset.setDoi(DOI_WITH_SPACES);
        dataset.merge();
        
        expectHandleServiceUpdate(DOI_WITH_SPACES, URL);
        doiService.createOrUpdate(DOI_WITH_SPACES, URL, false);
    }
    
    private void expectHandleServiceUpdate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.create(doi, url);
        EasyMock.expectLastCall().andThrow(new HandleException("handle exists"));
        mockHandleService.update(doi, url);
        EasyMock.replay(mockHandleService);
    }

    @Test(expected = SecurityException.class)
    public void testCreateOrUpdateNonBelongingDataset() throws Exception {
        createDataset(DOI, datacentre2).persist();
        doiService.createOrUpdate(DOI, URL, false);
    }
    
    @Test
    public void testCreateOrUpdateMintedTimestamp() throws Exception {
        Dataset dataset = createDataset(DOI, datacentre);
        dataset.persist();
        assertNull(dataset.getMinted());
        
        // doi minting
        expectHandleServiceCreate(DOI, URL);
        doiService.createOrUpdate(DOI, URL, false);
        Date minted = dataset.getMinted();
        assertNotNull(minted);

        // doi update
        expectHandleServiceUpdate(DOI, URL);
        doiService.createOrUpdate(DOI, URL, false);
        assertEquals(minted, dataset.getMinted());
        
        // doi update after deleted/lost in handle
        expectHandleServiceCreate(DOI, URL);
        doiService.createOrUpdate(DOI, URL, false);
        assertEquals(minted, dataset.getMinted());
    }

    @Test
    public void testResolve_onlyHandleExisting() throws Exception {
        expectHandleServiceResolve();
        callResolveAndCheck(DOI, URL);
    }

    private void callResolveAndCheck(String doi, String url) throws Exception {
        Dataset dataset = doiService.resolve(doi);
        assertEquals(datacentre, dataset.getDatacentre());
        assertEquals(doi, dataset.getDoi());
        assertEquals(url, dataset.getUrl());
    }

    private void expectHandleServiceResolve() throws HandleException, NotFoundException {
        EasyMock.reset(mockHandleService);
        EasyMock.expect(mockHandleService.resolve(DOI)).andReturn(URL);
        EasyMock.replay(mockHandleService);
    }

    private void expectHandleServiceResolveException(Exception ex) throws HandleException, NotFoundException {
        EasyMock.reset(mockHandleService);
        EasyMock.expect(mockHandleService.resolve(DOI)).andThrow(ex);
        EasyMock.replay(mockHandleService);
    }

    @Test
    public void testResolve_HandleAndDatasetExisting() throws Exception {
        createDataset(DOI, datacentre).persist();
        expectHandleServiceResolve();
        callResolveAndCheck(DOI, URL);
    }

    @Test
    public void testResolve_onlyDatasetExisting() throws Exception {
        createDataset(DOI, datacentre).persist();
        expectHandleServiceResolveException(new NotFoundException());
        callResolveAndCheck(DOI, null);
    }

    @Test(expected = NotFoundException.class)
    public void testResolve_noneExisting() throws Exception {
        expectHandleServiceResolveException(new NotFoundException());
        callResolveAndCheck(DOI, URL);
    }

    @Test(expected = HandleException.class)
    public void testResolveHandleException() throws Exception {
        expectHandleServiceResolveException(new HandleException(""));
        callResolveAndCheck(DOI, URL);
    }

    @Test(expected = SecurityException.class)
    public void testResolveNonBelongingDataset() throws Exception {
        login(datacentre2);
        testResolve_HandleAndDatasetExisting();
    }

}
