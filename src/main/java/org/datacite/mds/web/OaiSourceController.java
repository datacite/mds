package org.datacite.mds.web;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.OaiSource;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "oaisources", formBackingObject = OaiSource.class, delete = false)
@RequestMapping("/oaisources")
@Controller
public class OaiSourceController {
    
    @ModelAttribute("owners")
    public Collection<String> populateOwners() {
        SortedSet<String> owners = new TreeSet<String>();
        owners.add(""); // add empty string to disable symbol default selection
        for (Datacentre datacentre : Datacentre.findAllDatacentres()) {
            owners.add(datacentre.getSymbol());
        }
        for (Allocator allocator: Allocator.findAllAllocators()) {
            owners.add(allocator.getSymbol());
        }
        return owners;
    }

}
