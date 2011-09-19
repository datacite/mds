package org.datacite.mds.web.ui.controller;

import org.datacite.mds.domain.Media;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "medias", formBackingObject = Media.class)
@RequestMapping("/medias")
@Controller
public class MediaController {
}
