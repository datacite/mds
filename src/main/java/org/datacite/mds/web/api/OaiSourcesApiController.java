package org.datacite.mds.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/*")
@Controller
public class OaiSourcesApiController {

    @RequestMapping(value = "oaisource", method = RequestMethod.GET, headers = { "Accept=application/xml" })
    public ResponseEntity<? extends Object> get() {

        return null;

    }

    @RequestMapping(value = "oaisource", method = RequestMethod.PUT)
    public ResponseEntity<? extends Object> update(@RequestParam String url, @RequestParam String timestamp,
            @RequestParam String status, @RequestParam(required = false) Boolean testMode) {

        return null;

    }
}
