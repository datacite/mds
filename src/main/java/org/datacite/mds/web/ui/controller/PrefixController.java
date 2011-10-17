package org.datacite.mds.web.ui.controller;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.web.ui.UiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "prefixes", formBackingObject = Prefix.class, delete = false, populateMethods = false)
@RequestMapping("/prefixes")
@Controller
public class PrefixController implements UiController {
}
