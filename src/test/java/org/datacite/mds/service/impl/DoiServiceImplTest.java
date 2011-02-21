package org.datacite.mds.service.impl;

import static org.datacite.mds.test.Utils.createAllocator;
import static org.datacite.mds.test.Utils.createDatacentre;
import static org.datacite.mds.test.Utils.createDataset;
import static org.datacite.mds.test.Utils.createPrefixes;
import static org.datacite.mds.test.Utils.login;
import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.validation.ValidationException;
import org.datacite.mds.web.api.NotFoundException;
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
    final String DOMAIN_WRONG = "wrong.invalid";
    final Integer DOI_QUOTA_USED = 42;

    Datacentre datacentre;

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

        login(datacentre);
    }

    // CREATE

    @Test
    public void testCreate() throws Exception {
        Dataset dataset = doiService.create(DOI, "http://" + DOMAIN, true);
        assertEquals(DOI, dataset.getDoi());
        assertEquals(DATACENTRE_SYMBOL, dataset.getDatacentre().getSymbol());
        Integer doiQuotaUsedExpected = DOI_QUOTA_USED + 1;
        assertEquals(doiQuotaUsedExpected, dataset.getDatacentre().getDoiQuotaUsed());
    }

    @Test(expected = ValidationException.class)
    public void testCreateWrongPrefix() throws Exception {
        doiService.create(DOI_WRONG, "http://" + DOMAIN, true);
    }

    @Test(expected = SecurityException.class)
    public void testCreateQuotaExceeded() throws Exception {
        datacentre.setDoiQuotaAllowed(DOI_QUOTA_USED);
        doiService.create(DOI, "http://" + DOMAIN, true);
    }

    @Test(expected = ValidationException.class)
    public void testCreateWrongDomain() throws Exception {
        doiService.create(DOI, "http://" + DOMAIN_WRONG, true);
    }

    @Test(expected = SecurityException.class)
    public void testCreateNotLoggedIn() throws Exception {
        login(null);
        doiService.create(DOI, "http://" + DOMAIN, true);
    }

    // UPDATE

    @Test
    public void testUpdate() throws Exception {
        createDataset(DOI, datacentre).persist();
        doiService.update(DOI, "http://" + DOMAIN, true);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateWrongDomain() throws Exception {
        createDataset(DOI, datacentre).persist();
        doiService.update(DOI, "http://" + DOMAIN_WRONG, true);
    }

    @Test(expected = SecurityException.class)
    public void testUpdateNonBelongingDataset() throws Exception {
        Datacentre datacentre2 = createDatacentre(DATACENTRE_SYMBOL2, datacentre.getAllocator());
        datacentre2.setPrefixes(datacentre.getPrefixes());
        datacentre2.persist();
        createDataset(DOI, datacentre2).persist();
        doiService.update(DOI, "http://" + DOMAIN, true);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateNonExistingDataset() throws Exception {
        doiService.update(DOI, "http://" + DOMAIN, true);
    }

}
