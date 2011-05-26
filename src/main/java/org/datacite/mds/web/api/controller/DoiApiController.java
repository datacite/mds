package org.datacite.mds.web.api.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.DoiService;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.NotFoundException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebRequestDataBinder;

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
    
    @RequestMapping(value = "", method = { RequestMethod.POST }, headers = { "Content-Type=application/x-www-form-urlencoded" })
    public ResponseEntity<String> postDataset(@ModelAttribute Dataset dataset,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        return createOrUpdate(dataset, testMode, httpRequest);
    }
    
    @RequestMapping(value = "", method = { RequestMethod.POST })
    public ResponseEntity<String> post(@RequestBody String body, 
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)  
            throws ValidationException, HandleException, SecurityException, NotFoundException, IOException {
        Dataset dataset = bindRequestToDataset(body, httpRequest);
        return postDataset(dataset, testMode, httpRequest);
    }
    
    private Dataset bindRequestToDataset(String body, HttpServletRequest request) throws IOException {
        Properties props = new Properties();
        props.load(new StringReader(body));
        Dataset dataset = new Dataset();
        WebRequestDataBinder binder = new WebRequestDataBinder(dataset);
        MutablePropertyValues bodyParameters = new MutablePropertyValues(props);
        ServletRequestParameterPropertyValues requestParameters = new ServletRequestParameterPropertyValues(request);
        binder.bind(requestParameters);
        binder.bind(bodyParameters);
        return dataset;
    }
    
    @RequestMapping(value = "**", method = { RequestMethod.PUT }, headers = { "Content-Type=application/x-www-form-urlencoded" })
    public ResponseEntity<String> putDataset(@ModelAttribute Dataset dataset,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        String doi = getDoiFromRequest(httpRequest);
        if (! StringUtils.equals(dataset.getDoi(), doi))
            throw new ValidationException("doi parameter does not match doi of resource");
        
        return createOrUpdate(dataset, testMode, httpRequest);
    }
    
    @RequestMapping(value = "**", method = { RequestMethod.PUT })
    public ResponseEntity<String> put(@RequestBody String body,
            @RequestParam(required = false) Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException, IOException {
        Dataset dataset = bindRequestToDataset(body, httpRequest);
        return putDataset(dataset, testMode, httpRequest);
    }

    private ResponseEntity<String> createOrUpdate(Dataset dataset, Boolean testMode, HttpServletRequest httpRequest)
            throws ValidationException, HandleException, SecurityException, NotFoundException {
        String method = httpRequest.getMethod();
        
        String doi = dataset.getDoi();
        String url = dataset.getUrl();
        
        if (StringUtils.isEmpty(doi))
            throw new ValidationException("param 'doi' required");

        if (StringUtils.isEmpty(url))
            throw new ValidationException("param 'url' required");

        if (testMode == null)
            testMode = false;

        log4j.debug("*****" + method + " doi (testMode=" + testMode + ") doi: " + doi + ", url: " + url);

        doiService.createOrUpdate(doi, url, testMode);

        HttpHeaders headers = new HttpHeaders();
        if (method.equals("POST")) {
            StringBuffer location = httpRequest.getRequestURL().append("/" + doi);
            headers.set("Location", location.toString());
        }
        return new ResponseEntity<String>("OK", headers, HttpStatus.CREATED);
    }

}
