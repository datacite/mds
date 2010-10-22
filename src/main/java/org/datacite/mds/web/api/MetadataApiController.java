package org.datacite.mds.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/metadata/**")
@Controller
public class MetadataApiController {

    @RequestMapping
    public String index() {
        return "metadataapi/index";
    }
}
