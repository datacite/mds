package org.datacite.mds.service.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class PurgeTestPrefixServiceImplTest {

    final String TEST_PREFIX = "10.5072";
    final String OTHER_PREFIX = "10.9999";
    int EXPIRATION_DAYS = 10;

    @Autowired
    PurgeTestPrefixServiceImpl service;

    @PersistenceContext
    EntityManager em;

    Datacentre datacentre;

    Dataset datasetNonTest;
    Dataset datasetOld;
    Dataset datasetNew;
    Dataset datasetOldWithNewMetadata;
    Metadata metadataOld;
    Metadata metadataNew;

    @Before
    public void init() {
        service.testPrefix = TEST_PREFIX;
        service.expirationDays = EXPIRATION_DAYS;

        datacentre = TestUtils.createDefaultDatacentre(TEST_PREFIX, OTHER_PREFIX);

        Date newDate = new Date();
        Date oldDate = DateUtils.addDays(newDate, -EXPIRATION_DAYS - 1);

        datasetNonTest = createDataset(OTHER_PREFIX + "/other", oldDate);
        datasetOld = createDataset(TEST_PREFIX + "/old", oldDate);
        datasetNew = createDataset(TEST_PREFIX + "/new", newDate);
        datasetOldWithNewMetadata = createDataset(TEST_PREFIX + "/oldWithNewMetadata", oldDate);
        metadataNew = createMetadata(datasetOldWithNewMetadata, newDate);
        metadataOld = createMetadata(datasetOld, oldDate);
    }

    private Metadata createMetadata(Dataset dataset, Date created) {
        Metadata metadata = TestUtils.createMetadata(TestUtils.getTestMetadata(), dataset);
        metadata.persist(); // persist sets MetadataVersion, but overwrites created
        metadata.setCreated(created);
        em.merge(metadata);
        return metadata;
    }

    private Dataset createDataset(String doi, Date updated) {
        Dataset dataset = TestUtils.createDataset(doi, datacentre);
        dataset.setUpdated(updated);
        dataset.setDoi(doi);
        em.persist(dataset);
        return dataset;
    }

    @Test
    public void testPurgeAll() {
        service.purgeAll();
        Assert.assertTrue(em.contains(datasetNonTest));
        Assert.assertFalse(em.contains(datasetOld));
        Assert.assertFalse(em.contains(datasetNew));
        Assert.assertFalse(em.contains(datasetOldWithNewMetadata));
        Assert.assertFalse(em.contains(metadataNew));
        Assert.assertFalse(em.contains(metadataOld));
    }

    @Test
    public void testPurgeOld() {
        service.purgeOld();
        Assert.assertTrue(em.contains(datasetNonTest));
        Assert.assertFalse(em.contains(datasetOld));
        Assert.assertTrue(em.contains(datasetNew));
        Assert.assertTrue(em.contains(datasetOldWithNewMetadata));
        Assert.assertTrue(em.contains(metadataNew));
        Assert.assertFalse(em.contains(metadataOld));
    }

}
