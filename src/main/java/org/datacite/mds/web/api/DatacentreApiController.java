package org.datacite.mds.web.api;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Allocator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Date;
import org.datacite.mds.web.util.SecurityUtils;
import org.datacite.mds.service.SecurityException;

@RequestMapping("/*")
@Controller
public class DatacentreApiController {

    Logger log4j = Logger.getLogger(DatacentreApiController.class);
    
	@RequestMapping(value = "datacentre", method = RequestMethod.GET, headers = { "Accept=application/xml" })
	public ResponseEntity<? extends Object> get(@RequestParam String symbol) {

        Datacentre datacentre;
        try {
            datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Allocator allocator;

        try {
            allocator = SecurityUtils.getAllocator();
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
        	return new ResponseEntity<String>("internal error - please contact admin", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	    
        if (!allocator.getSymbol().equals(datacentre.getAllocator().getSymbol())) {
            return new ResponseEntity<String>("cannot request datacentre which belongs to another party", new HttpHeaders(), HttpStatus.FORBIDDEN);
        }

	    return new ResponseEntity<Datacentre>(datacentre, new HttpHeaders(), HttpStatus.OK);	    
	}
	
	@RequestMapping(value = "datacentre", method = RequestMethod.PUT, headers = { "Content-Type=application/xml" })
	public ResponseEntity<? extends Object> createOrUpdate(@RequestBody @Valid Datacentre requestDatacentre,
			@RequestParam(required = false) Boolean testMode) {

        // TODO find logged in allocator
        Allocator allocator = null;
        
        // TODO try to find requestDatacentre

        // if new, initialise it
        requestDatacentre.setAllocator(allocator);
        requestDatacentre.setCreated(new Date());
	    requestDatacentre.setUpdated(new Date());
        requestDatacentre.setRoleName("ROLE_DATACENTRE");
	    
        return null;
	    
	}
}
