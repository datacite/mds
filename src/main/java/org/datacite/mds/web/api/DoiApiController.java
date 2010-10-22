package org.datacite.mds.web.api;

import javax.persistence.NonUniqueResultException;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.web.ForbiddenException;
import org.datacite.mds.web.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//@RequestMapping("/*")
public class DoiApiController {

    private static Logger log4j = Logger.getLogger(DoiApiController.class);

    @Autowired
    HandleService handleService;

    private Datacentre preliminaryCheck(String[] body) throws ForbiddenException {
        Datacentre datacentre = null;

        if (body.length != 2)
            throw new ForbiddenException("invalid content; expected two lines: DOI and URL");

        datacentre = SecurityUtils.getDatacentre();
        SecurityUtils.checkQuota(datacentre);
        SecurityUtils.checkRestrictions(body[0], body[1], datacentre);

        return datacentre;
    }

    @RequestMapping(value = "doi", method = RequestMethod.POST, headers = { "Content-Type=text/plain;charset=UTF-8",
			"Accept=text/plain;charset=UTF-8" })
    public ResponseEntity<String> mint(@RequestBody String[] body, @RequestParam(required = false) Boolean testMode) {
        if (testMode == null)
            testMode = false;
        log4j.debug("*****POST doi " + body + " \ntestMode = " + testMode);

        HttpHeaders headers = new HttpHeaders();
        Datacentre datacentre = null;

        try {
            datacentre = preliminaryCheck(body);
        } catch (ForbiddenException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        }

        String doi = body[0];
        String url = body[1];

        if (!testMode) {
            log4j.debug("trying handle registration: " + doi);

            try {
                handleService.create(doi, url);
                datacentre.incQuotaUsed();
            } catch (HandleException e) {
                String message = "tried to register handle " + doi + " but failed: " + e.getMessage();
                log4j.warn(message, e);
                return new ResponseEntity<String>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Dataset dataset = new Dataset();
            dataset.setDatacentre(datacentre);
            dataset.setDoi(doi);
            dataset.setIsActive(true);
            dataset.setLastMetadataStatus("ABSENT"); // TODO refactor - use enum
            dataset.persist();

            log4j.debug("doi registartion: " + dataset.getDoi() + " successful");
        } else {
            log4j.debug("TEST MODE - registration skipped");
        }

        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "doi", method = RequestMethod.PUT/*,  headers = "Accept=text/plain" */)
    public ResponseEntity<String> update(@RequestBody String[] body, @RequestParam(required = false) Boolean testMode) {
        if (testMode == null)
            testMode = false;
        log4j.debug("*****PUT doi " + body + " \ntestMode = " + testMode);

        HttpHeaders headers = new HttpHeaders();
        Datacentre datacentre = null;

        try {
            datacentre = preliminaryCheck(body);
        } catch (ForbiddenException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        }

        String doi = body[0];
        String url = body[1];

        Dataset dataset = null;
        try {
            dataset = (Dataset) Dataset.findDatasetsByDoiEquals(doi).getSingleResult();
        } catch (NonUniqueResultException e) {
            log4j.error("more then one row for: " + doi);
            return new ResponseEntity<String>("internal error - please contact admin", headers,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!datacentre.getSymbol().equals(dataset.getDatacentre().getSymbol())) {
            String message = "cannot update DOI which belongs to another party";
            log4j.warn(datacentre.getSymbol() + " " + message + ", DOI: " + dataset.getDoi());
            return new ResponseEntity<String>(message, headers, HttpStatus.FORBIDDEN);
        }

        if (!testMode) {

            try {
                handleService.update(doi, url);
            } catch (HandleException e) {
                String message = "tried to update handle " + doi + " but failed: " + e.getMessage();
                log4j.warn(message, e);
                return new ResponseEntity<String>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            log4j.debug("doi update: " + doi + " successful");
        } else {
            log4j.debug("TEST MODE - update skipped");
        }

        return new ResponseEntity<String>("OK", headers, HttpStatus.OK);

    }
}
