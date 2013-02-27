package org.datacite.mds.web.ui.controller;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.web.ui.UiController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "prefixes", formBackingObject = Prefix.class, delete = false, populateMethods = false)
@RequestMapping("/prefixes")
@Controller
public class PrefixController implements UiController {
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        int sizeNo = size == null ? LIST_DEFAULT_SIZE : Math.min(size.intValue(), LIST_MAX_SIZE);
        uiModel.addAttribute("prefixes", Prefix.findPrefixEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
        float nrOfPages = (float) Prefix.countPrefixes() / sizeNo;
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        uiModel.addAttribute("size", sizeNo);
        return "prefixes/list";
    }
}
