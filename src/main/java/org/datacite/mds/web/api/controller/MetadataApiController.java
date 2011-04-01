package org.datacite.mds.web.api.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.validation.ValidationException;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.DeletedException;
import org.datacite.mds.web.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/*")
@Controller
public class MetadataApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(MetadataApiController.class);
    
    @Autowired
    DoiService doiService;

    @Autowired
    ValidationHelper validationHelper;

    @RequestMapping(value = "metadata", method = RequestMethod.GET)
    public ResponseEntity<? extends Object> get(@RequestParam String doi) throws SecurityException, NotFoundException, DeletedException {
        AllocatorOrDatacentre user = SecurityUtils.getCurrentAllocatorOrDatacentre();
        
        Dataset dataset = Dataset.findDatasetByDoi(doi);
        if (dataset == null)
            throw new NotFoundException("DOI is unknown to MDS");

        SecurityUtils.checkDatasetOwnership(dataset, user);

        if (!dataset.getIsActive())
            throw new DeletedException("dataset inactive");

        Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
        if (metadata == null)
            throw new NotFoundException("no metadata for the DOI");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(metadata.getXml(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "metadata", method = { RequestMethod.PUT, RequestMethod.POST })
        public ResponseEntity<String> createOrUpdate(@RequestBody String body, 
                                             @RequestParam String doi,
                                             @RequestParam(required = false) String url, 
                                             @RequestParam(required = false) Boolean testMode,
                                             HttpServletRequest httpRequest) throws ValidationException, HandleException, SecurityException, UnsupportedEncodingException {

        String method = httpRequest.getMethod();
        String logPrefix = "*****" + method + " metadata: ";
        if (testMode == null)
            testMode = false;

        log4j.debug(logPrefix + doi + ", url: " + url + " \ntestMode = " + testMode);
        
        Dataset dummyDataset = new Dataset();
        dummyDataset.setDoi(doi);

        Metadata metadata = new Metadata();
        metadata.setXml(body.getBytes("UTF-8"));
        metadata.setDataset(dummyDataset);
        
        validationHelper.validate(metadata);
        
        Dataset dataset;

        if (method.equals("POST")) {
            dataset = doiService.create(doi, url, testMode);
        } else { // PUT
            try {
                dataset = doiService.update(doi, url, testMode);
            } catch (NotFoundException e) {
                // This is workaround for 3rd parties who wants to integrate
                // with MDS but are not able to figure out of metadata was
                // already stored in MDS
                dataset = doiService.create(doi, url, testMode);
            }
        }

        log4j.debug(logPrefix + "dataset id = " + dataset.getId());
        metadata.setDataset(dataset);
        if (!testMode) {
            log4j.debug(logPrefix + "persisting XML");
            metadata.persist();
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }


    @RequestMapping(value = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@RequestParam String doi,
            @RequestParam(required = false) Boolean testMode) throws SecurityException, NotFoundException {
        log4j.debug("*****DELETE metadata: " + doi + " \ntestMode = " + testMode);

        if (testMode == null)
            testMode = false;

        Datacentre datacentre = SecurityUtils.getCurrentDatacentre();

        Dataset dataset = Dataset.findDatasetByDoi(doi);
        if (dataset == null)
            throw new NotFoundException("DOI doesn't exist");

        SecurityUtils.checkDatasetOwnership(dataset, datacentre);
        
        boolean wasActive = BooleanUtils.isNotFalse(dataset.getIsActive());
        if (!testMode) {
            dataset.setIsActive(!wasActive);
            dataset.merge();
            log4j.info(datacentre.getSymbol() + " successfuly " + (wasActive ? "deactivated " : " activated ") + doi);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>("OK", headers, wasActive ? HttpStatus.OK : HttpStatus.CREATED);
    }
}
