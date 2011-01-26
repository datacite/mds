package org.datacite.mds.web.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.validation.ValidationException;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.NotFoundException;
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
public class DoiApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(DoiApiController.class);

    @Autowired
    DoiService doiService;

    @RequestMapping(value = "doi", method = { RequestMethod.PUT, RequestMethod.POST }, headers = { "Content-Type=text/plain;charset=UTF-8" })
    public ResponseEntity<String> createOrUpdate(@RequestBody String body,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        String method = httpRequest.getMethod();
        if (testMode == null)
            testMode = false;
        HttpHeaders headers = new HttpHeaders();

        if (body.indexOf("\n") == -1 || body.indexOf("\n") != body.lastIndexOf("\n"))
            return new ResponseEntity<String>("request body must contain exactly two lines: DOI and URL", headers,
                    HttpStatus.BAD_REQUEST);

        String doi = body.substring(0, body.indexOf("\n"));
        String url = body.substring(body.indexOf("\n") + 1, body.length());

        log4j.debug("*****" + method + " doi: " + doi + ", url: " + url + " \ntestMode = " + testMode);

        if (method.equals("POST")) {
            doiService.create(doi, url, testMode);
        } else { // PUT
            doiService.update(doi, url, testMode);
        }

        return new ResponseEntity<String>("OK", headers, method.equals("POST") ? HttpStatus.CREATED : HttpStatus.OK);
    }
}
