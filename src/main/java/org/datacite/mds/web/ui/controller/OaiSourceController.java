package org.datacite.mds.web.ui.controller;

import java.util.Collection;
import java.util.SortedSet;

import org.datacite.mds.domain.OaiSource;
import org.datacite.mds.util.Utils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "oaisources", formBackingObject = OaiSource.class, delete = false)
@RequestMapping("/oaisources")
@Controller
public class OaiSourceController {
    
    @ModelAttribute("owners")
    public Collection<String> populateOwners() {
        SortedSet<String> owners = Utils.getAllSymbols();
        owners.add(""); // add empty string to disable symbol default selection
        return owners;
    }

}
