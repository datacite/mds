package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.OaiSources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "oaisourceses", formBackingObject = OaiSources.class, delete = false)
@RequestMapping("/oaisourceses")
@Controller
public class OaiSourcesController {
}
