package org.datacite.mds.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Allocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "allocators", formBackingObject = Allocator.class, delete = false)
@RequestMapping("/allocators")
@Controller
public class AllocatorController {
}
