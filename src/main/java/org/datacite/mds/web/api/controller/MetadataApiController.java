package org.datacite.mds.web.api.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.ApiUtils;
import org.datacite.mds.web.api.DeletedException;
import org.datacite.mds.web.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/metadata")
@Controller
public class MetadataApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(MetadataApiController.class);
    
    @Autowired
    DoiService doiService;

    @Autowired
    ValidationHelper validationHelper;
    
    @Autowired
    SchemaService schemaService;
    
    @RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity getRoot() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "**", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<? extends Object> get(HttpServletRequest request) throws SecurityException, NotFoundException, DeletedException {
        String doi = getDoiFromRequest(request);
        log4j.debug(doi);
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
    
    private String getDoiFromRequest(HttpServletRequest request) {
        String uri = request.getServletPath();
        String doi = uri.replaceFirst("/metadata/", "");
        return doi;
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> post(@RequestBody byte[] xml,
                                             @RequestParam(required = false) Boolean testMode,
                                             HttpServletRequest httpRequest) throws ValidationException, HandleException, SecurityException, UnsupportedEncodingException {
        String doi = schemaService.getDoi(xml);
        return storeMetadata(doi, xml, testMode, httpRequest);
    }
    
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity putRoot() throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("PUT");
    }

    @RequestMapping(value = "**", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@RequestBody byte[] xml,
                                             @RequestParam(required = false) Boolean testMode,
                                             HttpServletRequest httpRequest) throws ValidationException, HandleException, SecurityException, UnsupportedEncodingException {
        String doi = getDoiFromRequest(httpRequest);
        return storeMetadata(doi, xml, testMode, httpRequest);
    }

    private ResponseEntity<String> storeMetadata(String doi, byte[] xml, Boolean testMode, HttpServletRequest httpRequest) throws ValidationException, HandleException, SecurityException, UnsupportedEncodingException {
        String method = httpRequest.getMethod();
        if (testMode == null)
            testMode = false;
        String logPrefix = "*****" + method + " metadata (doi=" + doi + ", testMode=" + testMode + ") ";

        log4j.debug(logPrefix);
        
        if (xml.length == 0)
            throw new ValidationException("request body must not be empty");
        
        Dataset dummyDataset = new Dataset();
        dummyDataset.setDoi(doi);

        Metadata metadata = new Metadata();
        metadata.setXml(xml);
        metadata.setDataset(dummyDataset);
        
        validationHelper.validate(metadata);
        
        Dataset dataset = doiService.createOrUpdate(doi, null, testMode);

        log4j.debug(logPrefix + "dataset id = " + dataset.getId());
        metadata.setDataset(dataset);
        if (!testMode) {
            log4j.debug(logPrefix + "persisting XML");
            metadata.persist();
            if (BooleanUtils.isFalse(dataset.getIsActive())) {
                dataset.setIsActive(true);
                dataset.merge();
            }
        }

        HttpHeaders headers = new HttpHeaders();
        if (method.equals("POST")) {
            StringBuffer location = httpRequest.getRequestURL().append("/" + doi);
            headers.set("Location", location.toString());
        }
        String message = ApiUtils.makeResponseMessage("OK (" + doi + ")", testMode);
        return new ResponseEntity<String>(message, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "**", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(HttpServletRequest request,
            @RequestParam(required = false) Boolean testMode) throws SecurityException, NotFoundException {
        String doi = getDoiFromRequest(request);
        if (testMode == null)
            testMode = false;
        log4j.debug("*****DELETE metadata (testMode=" + testMode + ") " + doi);

        Datacentre datacentre = SecurityUtils.getCurrentDatacentre();

        Dataset dataset = Dataset.findDatasetByDoi(doi);
        if (dataset == null)
            throw new NotFoundException("DOI doesn't exist");
        
        Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
        if (metadata == null)
            throw new NotFoundException("Metadata doesn't exist");

        SecurityUtils.checkDatasetOwnership(dataset, datacentre);
        
        if (!testMode) {
            dataset.setIsActive(false);
            dataset.merge();
            log4j.info(datacentre.getSymbol() + " successfuly deactivated " + doi);
        }

        HttpHeaders headers = new HttpHeaders();
        String message = ApiUtils.makeResponseMessage("OK", testMode);
        return new ResponseEntity<String>(message, headers, HttpStatus.OK);
    }
}
