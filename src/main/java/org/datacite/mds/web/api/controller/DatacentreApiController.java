package org.datacite.mds.web.api.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.ApiController;
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
public class DatacentreApiController implements ApiController {

    Logger log4j = Logger.getLogger(DatacentreApiController.class);
    
    @Autowired
    ValidationHelper validationHelper;

    @RequestMapping(value = "datacentre", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<? extends Object> get(@RequestParam String symbol) {

        Datacentre datacentre;
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), httpHeaders, HttpStatus.NOT_FOUND);
        }

        Allocator allocator;

        try {
            allocator = SecurityUtils.getCurrentAllocatorWithExceptions();
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), httpHeaders, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<String>("internal error - please contact admin", httpHeaders,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!allocator.getSymbol().equals(datacentre.getAllocator().getSymbol())) {
            return new ResponseEntity<String>("cannot request datacentre which belongs to another party", 
                                              httpHeaders, HttpStatus.FORBIDDEN);
        }

        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Datacentre>(datacentre, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "datacentre", method = RequestMethod.PUT, headers = { "Content-Type=application/xml" })
    public ResponseEntity<? extends Object> createOrUpdate(@RequestBody @Valid Datacentre requestDatacentre,
            @RequestParam(required = false) Boolean testMode) {

        if (testMode == null)
            testMode = false;
        log4j.debug("*****PUT datacentre " + requestDatacentre + " \ntestMode = " + testMode);
        
        HttpHeaders headers = new HttpHeaders();

        try {
            convertPrefixesToPersistentPrefixes(requestDatacentre);
        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
        }

        Allocator allocator;

        try {
            allocator = SecurityUtils.getCurrentAllocatorWithExceptions();
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<String>("internal error - please contact admin", headers,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!requestDatacentre.getSymbol().startsWith(allocator.getSymbol()))
            return new ResponseEntity<String>("cannot PUT datacentre which belongs to another party", 
                                              headers, HttpStatus.FORBIDDEN);

        Datacentre datacentre = null;
        try {
            datacentre = Datacentre.findDatacentresBySymbolEquals(requestDatacentre.getSymbol()).getSingleResult();

            if (!allocator.getSymbol().equals(datacentre.getAllocator().getSymbol()))
                return new ResponseEntity<String>("cannot update datacentre which belongs to another party", 
                                                  headers, HttpStatus.FORBIDDEN);

            log4j.debug("*****PUT datacentre, found datacentre... updating ");

        } catch (Exception e) {

            log4j.debug("*****PUT datacentre: new datacentre");

            requestDatacentre.setAllocator(allocator);
            requestDatacentre.setRoleName("ROLE_DATACENTRE");
            requestDatacentre.setUpdated(new Date());
            requestDatacentre.setCreated(new Date());

            String validationError = validationHelper.getFirstViolationMessage(requestDatacentre);
            if (validationError != null) {
                return new ResponseEntity<String>(validationError, headers, HttpStatus.BAD_REQUEST);
            }

            if (!testMode)
                requestDatacentre.persist();

            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<Datacentre>(datacentre, headers, HttpStatus.CREATED);
        }

        datacentre.setAllocator(allocator);
        datacentre.setRoleName("ROLE_DATACENTRE");
        datacentre.setUpdated(new Date());
        datacentre.setContactName(requestDatacentre.getContactName());
        datacentre.setContactEmail(requestDatacentre.getContactEmail());
        datacentre.setDoiQuotaAllowed(requestDatacentre.getDoiQuotaAllowed());
        datacentre.setDoiQuotaUsed(requestDatacentre.getDoiQuotaUsed());
        datacentre.setDomains(requestDatacentre.getDomains());
        datacentre.setIsActive(requestDatacentre.getIsActive());
        datacentre.setName(requestDatacentre.getName());
        datacentre.setPassword(requestDatacentre.getPassword());
        datacentre.setSymbol(requestDatacentre.getSymbol());
        datacentre.setPrefixes(requestDatacentre.getPrefixes());

        if (!testMode)
            datacentre.merge();
                
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Datacentre>(datacentre, headers, HttpStatus.OK);
    }
    
    private void convertPrefixesToPersistentPrefixes(Datacentre datacentre) throws Exception {
        Set<Prefix> persistentPrefixes = new HashSet<Prefix>();
        for (Prefix p : datacentre.getPrefixes()) {            
            Prefix persistedPrefix;
            try {
                persistedPrefix = Prefix.findPrefixesByPrefixLike(p.getPrefix()).getSingleResult();
            } catch (Exception e) {
                throw new Exception("Prefix not found in our database: " + p.getPrefix());
            }
            persistentPrefixes.add(persistedPrefix);
        }
        datacentre.setPrefixes(persistentPrefixes);
    }
}
