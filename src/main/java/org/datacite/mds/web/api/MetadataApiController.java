package org.datacite.mds.web.api;

import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import org.datacite.mds.util.Utils;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;

@RequestMapping("/*")
@Controller
public class MetadataApiController {

    private static Logger log4j = Logger.getLogger(MetadataApiController.class);
    
    @Autowired
    DoiService doiService;

    @RequestMapping(value = "metadata", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<String> get(@RequestParam String doi) {
        HttpHeaders headers = new HttpHeaders();
        
        Dataset dataset;
        try {
            dataset = Dataset.findDatasetsByDoiEquals(doi).getSingleResult();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.NOT_FOUND);
        }

        Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
        if(metadata == null)
            return new ResponseEntity<String>("no metadata for the DOI", headers, HttpStatus.NOT_FOUND);

        String prettyXml;
        try {
            prettyXml = Utils.formatXML(new String(metadata.getXml(), "UTF8"));
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<String>(prettyXml, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "metadata", method = RequestMethod.PUT, headers = { "Content-Type=text/plain;charset=UTF-8" })
        public ResponseEntity<String> update(@RequestBody String body, 
                                             @RequestParam String doi,
                                             @RequestParam(required = false) String url, 
                                             @RequestParam(required = false) Boolean testMode) {

        if (testMode == null)
            testMode = false;
        HttpHeaders headers = new HttpHeaders();

        log4j.debug("*****PUT metadata: " + doi + ", url: " + url + " \ntestMode = " + testMode);

        Metadata metadata = new Metadata();
        try {
            metadata.setXml(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // TODO if not XML valid:
        //	return new ResponseEntity<String>("request body must contain exactly two lines: DOI and URL", headers, 
        //	        HttpStatus.BAD_REQUEST);
        
        Dataset dataset;

        try {
            dataset = doiService.update(doi, url, testMode);
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
        	return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.NOT_FOUND);
        } catch (HandleException e) {
        	return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);        	
        }

        log4j.debug("*****PUT metadata: dataset found, id = " + dataset.getId());
        metadata.setDataset(dataset);
        if (!testMode) {
            log4j.debug("*****PUT metadata: persisting XML");
            metadata.persist();
        }

        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "metadata", method = RequestMethod.POST, headers = { "Content-Type=text/plain;charset=UTF-8" })
        public ResponseEntity<String> create(@RequestBody String body, 
                                             @RequestParam String doi,
                                             @RequestParam(required = false) String url, 
                                             @RequestParam(required = false) Boolean testMode) {
        log4j.debug("*****POST metadata: " + doi + ", url: " + url + " \ntestMode = " + testMode);

        if (testMode == null)
            testMode = false;

        HttpHeaders headers = new HttpHeaders();

        Metadata metadata = new Metadata();
        try {
            metadata.setXml(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // TODO if not XML valid:
        //	return new ResponseEntity<String>("request body must contain exactly two lines: DOI and URL", headers, 
        //	        HttpStatus.BAD_REQUEST);
        
        Dataset dataset;
        try {
            dataset = doiService.create(doi, url, testMode);
        } catch (SecurityException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.FORBIDDEN);
        } catch (HandleException e) {
        	return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        log4j.debug("*****POST metadata: dataset create id = " + dataset.getId());
        metadata.setDataset(dataset);
        if (!testMode) {
            log4j.debug("*****POST metadata: persisting XML");
            metadata.persist();
        }
        
        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity<? extends Object> delete(@RequestParam String doi,
            @RequestParam(required = false) Boolean testMode) {

        return null;

    }

}
