package org.datacite.mds.web.ui.controller;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Prefix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "prefixes", formBackingObject = Prefix.class, delete = false)
@RequestMapping("/prefixes")
@Controller
public class PrefixController {
}
