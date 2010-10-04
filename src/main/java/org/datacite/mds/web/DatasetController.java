package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Dataset;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "datasets", formBackingObject = Dataset.class, delete = false)
@RequestMapping("/datasets")
@Controller
public class DatasetController {
}
