package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Metadata;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "metadata", formBackingObject = Metadata.class, delete = false)
@RequestMapping("/metadata")
@Controller
public class MetadataController {
}
