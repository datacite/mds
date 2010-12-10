package org.datacite.mds.service.impl;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.validation.util.ValidationUtils;
import org.datacite.mds.web.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoiServiceImpl implements DoiService {

    static final Logger log4j = Logger.getLogger(DoiServiceImpl.class);

    @Autowired
    HandleService handleService;

    public Dataset create(String doi, String url, boolean testMode) throws HandleException, SecurityException {
        Datacentre datacentre = SecurityUtils.getCurrentDatacentreWithException();

        SecurityUtils.checkQuota(datacentre);

        Dataset dataset = new Dataset();
        dataset.setDatacentre(datacentre);
        dataset.setDoi(doi);
        dataset.setUrl(url);

        String violationMessage = ValidationUtils.getFirstViolationMessage(dataset);
        if (violationMessage != null) {
            throw new SecurityException(violationMessage);
        }

        log4j.debug("trying handle registration: " + doi);
        if (!testMode && url != null && !"".equals(url)) {
            handleService.create(doi, url);
        } else
            log4j.debug("TEST MODE or empty URL- minting skipped");

        datacentre.incQuotaUsed();

        if (!testMode) {
            dataset.persist();
            log4j.debug("doi registration: " + dataset.getDoi() + " successful");
        } else {
            log4j.debug("TEST MODE - registration skipped");
        }

        return dataset;
    }

    public Dataset update(String doi, String url, boolean testMode) throws HandleException, SecurityException {
        Datacentre datacentre = SecurityUtils.getCurrentDatacentreWithException();

        Dataset dataset = Dataset.findDatasetByDoi(doi);
        if (dataset == null) {
            throw new SecurityException("DOI doesn't exist");
        }
        dataset.setUrl(url);

        String violationMessage = ValidationUtils.getFirstViolationMessage(dataset);
        if (violationMessage != null) {
            throw new SecurityException(violationMessage);
        }

        if (!datacentre.getSymbol().equals(dataset.getDatacentre().getSymbol())) {
            String message = "cannot update DOI which belongs to another party";
            log4j.warn(datacentre.getSymbol() + " " + message + ", DOI: " + dataset.getDoi());
            throw new SecurityException(message);
        }

        if (!testMode && url != null && !"".equals(url)) {
            handleService.update(doi, url);
            log4j.debug("doi update: " + doi + " successful");
        } else {
            log4j.debug("TEST MODE or empty URL- update skipped");
        }

        return dataset;
    }
}
