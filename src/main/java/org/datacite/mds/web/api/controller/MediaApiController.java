package org.datacite.mds.web.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.Utils;
import org.datacite.mds.web.api.ApiController;
import org.datacite.mds.web.api.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/media")
public class MediaApiController implements ApiController {

    private static Logger log4j = Logger.getLogger(MediaApiController.class);

    @RequestMapping(value = "**", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<String> get(HttpServletRequest httpRequest) {
        return null;
    }
  
}
