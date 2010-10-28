package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.OaiSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "oaisources", formBackingObject = OaiSource.class, delete = false)
@RequestMapping("/oaisources")
@Controller
public class OaiSourceController {
}
