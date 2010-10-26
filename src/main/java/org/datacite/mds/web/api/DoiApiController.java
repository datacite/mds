package org.datacite.mds.web.api;

import org.apache.log4j.Logger;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
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
@RequestMapping("/*")
public class DoiApiController {

    private static Logger log4j = Logger.getLogger(DoiApiController.class);
    
    @Autowired
    DoiService doiService;

    @RequestMapping(value = "doi", method = RequestMethod.POST, headers = { "Content-Type=text/plain;charset=UTF-8" })
    public ResponseEntity<String> mint(@RequestBody String body, @RequestParam(required = false) Boolean testMode) {
        if (testMode == null)
            testMode = false;

        HttpHeaders headers = new HttpHeaders();

        if (body.indexOf("\n") == -1 || body.indexOf("\n") != body.lastIndexOf("\n"))
        	return new ResponseEntity<String>("request body must contain exactly two lines: DOI and URL", headers, 
        	        HttpStatus.BAD_REQUEST);
        
        String doi = body.substring(0, body.indexOf("\n"));
        String url = body.substring(body.indexOf("\n")+1, body.length());

        log4j.debug("*****POST doi: " + doi + ", url: " + url + " \ntestMode = " + testMode);
        
        try {
            doiService.create(doi, url, testMode);
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        } catch (HandleException e) {
        	return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
         }
        
        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "doi", method = RequestMethod.PUT, headers = { "Content-Type=text/plain;charset=UTF-8" })
    public ResponseEntity<String> update(@RequestBody String body, @RequestParam(required = false) Boolean testMode) {
        if (testMode == null)
            testMode = false;
        HttpHeaders headers = new HttpHeaders();

        if (body.indexOf("\n") == -1 || body.indexOf("\n") != body.lastIndexOf("\n"))
        	return new ResponseEntity<String>("request body must contain exactly two lines: DOI and URL", headers, 
        	        HttpStatus.BAD_REQUEST);
        
        String doi = body.substring(0, body.indexOf("\n"));
        String url = body.substring(body.indexOf("\n")+1, body.length());

        log4j.debug("*****PUT doi: " + doi + ", url: " + url + " \ntestMode = " + testMode);

        try {
            doiService.update(doi, url, testMode);
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
        	return new ResponseEntity<String>("internal error - please contact admin", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HandleException e) {
        	return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);        	
        }

        return new ResponseEntity<String>("OK", headers, HttpStatus.OK);
    }
}
