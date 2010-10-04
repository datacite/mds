package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Datacentre;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "datacentres", formBackingObject = Datacentre.class, delete = false)
@RequestMapping("/datacentres")
@Controller
public class DatacentreController {
}
