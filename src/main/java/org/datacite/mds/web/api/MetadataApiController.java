package org.datacite.mds.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

@RequestMapping("/*")
@Controller
public class MetadataApiController {

    @RequestMapping(value = "metadata", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<String> get(@RequestParam String doi) {
        HttpHeaders headers = new HttpHeaders();

        if (doi == null || "".equals(doi))
            return new ResponseEntity<String>("DOI parameter required", headers, HttpStatus.NOT_FOUND);
        
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
        public ResponseEntity<? extends Object> createOrUpdate(@RequestBody String body, @RequestParam String doi,
                                                               @RequestParam(required = false) String url, 
                                                               @RequestParam(required = false) Boolean testMode) {

        

        return null;

    }

    @RequestMapping(value = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity<? extends Object> delete(@RequestParam String doi,
            @RequestParam(required = false) Boolean testMode) {

        return null;

    }

}
