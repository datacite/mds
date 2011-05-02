package org.datacite.mds.web.api.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.api.ApiController;
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
public class DatacentreApiController implements ApiController {

    Logger log4j = Logger.getLogger(DatacentreApiController.class);

    @Autowired
    ValidationHelper validationHelper;

    @RequestMapping(value = "datacentre", method = RequestMethod.GET)
    public ResponseEntity<? extends Object> get(@RequestParam String symbol) throws SecurityException,
            NotFoundException {

        Datacentre datacentre = Datacentre.findDatacentreBySymbol(symbol);
        if (datacentre == null)
            throw new NotFoundException("Datacentre not found");

        Allocator allocator = SecurityUtils.getCurrentAllocator();

        compareAllocator(datacentre, allocator);

        return makeResponse(datacentre, HttpStatus.OK);
    }

    @RequestMapping(value = "datacentre", method = RequestMethod.PUT)
    public ResponseEntity<? extends Object> createOrUpdate(@RequestBody @Valid Datacentre requestDatacentre,
            @RequestParam(required = false) Boolean testMode) throws SecurityException, ValidationException {

        testMode = BooleanUtils.isTrue(testMode);

        log4j.debug("*****PUT datacentre " + requestDatacentre + " \ntestMode = " + testMode);

        convertPrefixesToPersistentPrefixes(requestDatacentre);

        Allocator currentAllocator = SecurityUtils.getCurrentAllocator();
        Datacentre persistentDatacentre = Datacentre.findDatacentreBySymbol(requestDatacentre.getSymbol());

        if (persistentDatacentre == null) {
            requestDatacentre.setAllocator(currentAllocator);
            return createDatacentre(requestDatacentre, testMode);
        } else {
            compareAllocator(persistentDatacentre, currentAllocator);
            return updateDatacentre(persistentDatacentre, requestDatacentre, testMode);
        }
    }

    private ResponseEntity<Datacentre> updateDatacentre(Datacentre persistentDatacentre, Datacentre requestDatacentre,
            Boolean testMode) throws ValidationException {
        log4j.debug("*****PUT datacentre, found datacentre... updating ");

        persistentDatacentre.setRoleName("ROLE_DATACENTRE");
        persistentDatacentre.setUpdated(new Date());
        persistentDatacentre.setContactName(requestDatacentre.getContactName());
        persistentDatacentre.setContactEmail(requestDatacentre.getContactEmail());
        persistentDatacentre.setDoiQuotaAllowed(requestDatacentre.getDoiQuotaAllowed());
        persistentDatacentre.setDoiQuotaUsed(requestDatacentre.getDoiQuotaUsed());
        persistentDatacentre.setDomains(requestDatacentre.getDomains());
        persistentDatacentre.setIsActive(requestDatacentre.getIsActive());
        persistentDatacentre.setName(requestDatacentre.getName());
        persistentDatacentre.setPassword(requestDatacentre.getPassword());
        persistentDatacentre.setSymbol(requestDatacentre.getSymbol());
        persistentDatacentre.setPrefixes(requestDatacentre.getPrefixes());

        validationHelper.validate(persistentDatacentre);

        if (!testMode)
            persistentDatacentre.merge();

        return makeResponse(persistentDatacentre, HttpStatus.OK);
    }

    private ResponseEntity<Datacentre> createDatacentre(Datacentre requestDatacentre, Boolean testMode) throws ValidationException {
        log4j.debug("*****PUT datacentre: new datacentre");

        requestDatacentre.setRoleName("ROLE_DATACENTRE");

        validationHelper.validate(requestDatacentre);

        if (!testMode)
            requestDatacentre.persist();

        return makeResponse(requestDatacentre, HttpStatus.CREATED);
    }
    
    private ResponseEntity<Datacentre> makeResponse(Datacentre datacentre, HttpStatus responseCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<Datacentre>(datacentre, headers, responseCode);
    }

    private void compareAllocator(Datacentre datacentre, Allocator allocator) throws SecurityException {
        if (!allocator.getSymbol().equals(datacentre.getAllocator().getSymbol()))
            throw new SecurityException("cannot access datacentre which belongs to another party");
    }

    private void convertPrefixesToPersistentPrefixes(Datacentre datacentre) throws ValidationException {
        Set<Prefix> persistentPrefixes = new HashSet<Prefix>();
        for (Prefix p : datacentre.getPrefixes()) {
            Prefix persistedPrefix;
            try {
                persistedPrefix = Prefix.findPrefixesByPrefixLike(p.getPrefix()).getSingleResult();
            } catch (Exception e) {
                throw new ValidationException("Prefix not found in our database: " + p.getPrefix());
            }
            persistentPrefixes.add(persistedPrefix);
        }
        datacentre.setPrefixes(persistentPrefixes);
    }
}
