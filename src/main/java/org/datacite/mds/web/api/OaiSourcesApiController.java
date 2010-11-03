package org.datacite.mds.web.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.OaiSource;
import org.datacite.mds.domain.OaiSourceCollection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/*")
@Controller
public class OaiSourcesApiController {
    
    Logger log4j = Logger.getLogger(OaiSourcesApiController.class);

    @RequestMapping(value = "oaisource", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<OaiSourceCollection> get() {

        OaiSourceCollection all = new OaiSourceCollection();
        all.addOaiSources(OaiSource.findAllOaiSources());
        
        return new ResponseEntity<OaiSourceCollection>(all, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "oaisource", method = RequestMethod.PUT)
    public ResponseEntity<? extends Object> update(@RequestParam String url, @RequestParam String timestamp,
            @RequestParam String status) {

        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");        
        
        OaiSource oaiSource;
        try {
            oaiSource = OaiSource.findOaiSourcesByUrl(url).getSingleResult();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        
        try {
            oaiSource.setLastHarvest(sdf.parse(timestamp));
        } catch (ParseException e) {
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        oaiSource.setLastStatus(status);
        oaiSource.merge();
        
        return new ResponseEntity<String>("OK", headers, HttpStatus.OK);
    }
}
