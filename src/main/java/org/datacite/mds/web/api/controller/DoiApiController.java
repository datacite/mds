package org.datacite.mds.web.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doi")
public class DoiApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(DoiApiController.class);

    @Autowired
    DoiService doiService;
    
    @Autowired
    HandleService handleService;
    
    @RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity getRoot() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity putRoot() throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("PUT");
    }
    
    @RequestMapping(value = "**", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<String> get(HttpServletRequest httpRequest) throws HandleException, NotFoundException {
        String doi = getDoiFromRequest(httpRequest);
        String url = handleService.resolve(doi);
        return new ResponseEntity<String>(url, HttpStatus.OK);
    }
    
    private String getDoiFromRequest(HttpServletRequest request) {
        String uri = request.getServletPath();
        String doi = uri.replaceFirst("/doi/", "");
        return doi;
    }
    
    @RequestMapping(value = "", method = { RequestMethod.POST })
    public ResponseEntity<String> post(@RequestBody String body,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        
        String[] lines = getBodyLines(body);
        String doi = lines[0];
        String url = lines[1];
        
        return createOrUpdate(doi, url, testMode, httpRequest);
    }
    
    @RequestMapping(value = "**", method = { RequestMethod.PUT } )
    public ResponseEntity<String> put(@RequestBody String body,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        
        String doi = getDoiFromRequest(httpRequest);
        String url = body;
        
        return createOrUpdate(doi, url, testMode, httpRequest);
    }
    

    private ResponseEntity<String> createOrUpdate(String doi, String url, Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        String method = httpRequest.getMethod();
        
        if (testMode == null)
            testMode = false;

        log4j.debug("*****" + method + " doi (testMode=" + testMode + ") doi: " + doi + ", url: " + url);

        HttpStatus httpStatus; 
        if (method.equals("POST")) {
            doiService.create(doi, url, testMode);
            httpStatus = HttpStatus.CREATED;
        } else { // PUT
            doiService.update(doi, url, testMode);
            httpStatus = HttpStatus.OK;
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>("OK", headers, httpStatus);
    }
    
    private String[] getBodyLines(String body) throws ValidationException {
        String[] lines = body.split("\\r?\\n",3);
        
        boolean hasTwoLines = lines.length == 2 || (lines.length == 3 && StringUtils.isEmpty(lines[2]));
        boolean areFieldsEmpty = lines.length < 2 || StringUtils.isEmpty(lines[0]) || StringUtils.isEmpty(lines[1]);
        
        if (!hasTwoLines || areFieldsEmpty)
            throw new ValidationException("request body must contain exactly two lines: DOI and URL");
        return lines;
    }
}
