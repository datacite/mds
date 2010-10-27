package org.datacite.mds.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/*")
@Controller
public class MetadataApiController {

    @RequestMapping(value = "metadata", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<? extends Object> get(@RequestParam String doi) {

        return null;

    }

    @RequestMapping(value = "metadata", method = RequestMethod.PUT, headers = { "Content-Type=text/plain;charset=UTF-8" })
    public ResponseEntity<? extends Object> createOrUpdate(@RequestBody String body,
            @RequestParam(required = false) Boolean testMode) {

        return null;

    }

    @RequestMapping(value = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity<? extends Object> delete(@RequestParam String doi,
            @RequestParam(required = false) Boolean testMode) {

        return null;

    }

}
