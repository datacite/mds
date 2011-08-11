package org.datacite.mds.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.PurgeTestPrefixService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PurgeTestPrefixServiceImpl implements PurgeTestPrefixService {

    Logger log = Logger.getLogger(PurgeTestPrefixServiceImpl.class);

    @Value("${handle.testPrefix}")
    String testPrefix;

    // @Value("${handle.testPrefix.expirationDays}")
    int expirationDays;

    // @Scheduled(fixedDelay = 5000)
    @Override
    public void purgeAll() {
        Date now = new Date();
        purgeOlderThan(now);
    }

    @Override
    public void purgeOld() {
        Date now = new Date();
        Date old = DateUtils.addDays(now, -expirationDays);
        purgeOlderThan(old);
    }

    private void purgeOlderThan(Date expirationDate) {
        List<Dataset> datasets = Dataset.findDatasetsByPrefix(testPrefix);
        for (Dataset dataset : datasets) {
            if (dataset.getUpdated().before(expirationDate)) {
                Metadata latest = Metadata.findLatestMetadatasByDataset(dataset);
                if (latest == null || latest.getCreated().before(expirationDate)) {
                    //TODO replace this by JPA cascade?!
                    List<Metadata> metadatas = Metadata.findMetadatasByDataset(dataset).getResultList();
                    for (Metadata metadata: metadatas)
                        metadata.remove();
                    
                    dataset.remove();
                }
                    
            }

        }
    }

}
