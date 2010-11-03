package org.datacite.mds.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "datacentres", formBackingObject = Datacentre.class, delete = false)
@RequestMapping("/datacentres")
@Controller
public class DatacentreController {

    @Autowired
    private GenericConversionService myConversionService;

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getSimpleAllocatorConverter());
        myConversionService.addConverter(Converters.getSimplePrefixConverter());
    }

    @Transactional
    private Allocator getCurrentAllocator() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        String symbol = currentAuth.getName();
        Allocator allocator = (Allocator) Allocator.findAllocatorsBySymbolEquals(symbol).getSingleResult();
        return allocator;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        Allocator allocator = getCurrentAllocator();
        Datacentre datacentre = new Datacentre();
        datacentre.setAllocator(allocator);
        datacentre.setSymbol(allocator.getSymbol() + ".");
        model.addAttribute("datacentre", datacentre);
        List dependencies = new ArrayList();
        if (Allocator.countAllocators() == 0) {
            dependencies.add(new String[] { "allocator", "allocators" });
        }
        model.addAttribute("dependencies", dependencies);
        return "datacentres/create";
    }

    @ModelAttribute("prefixes")
    public Collection<Prefix> populatePrefixes() {
        return getCurrentAllocator().getPrefixes();
    }
}
