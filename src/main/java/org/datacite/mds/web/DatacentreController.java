package org.datacite.mds.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.util.Converters;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "datacentres", formBackingObject = Datacentre.class, delete = false)
@RequestMapping("/datacentres")
@Controller
public class DatacentreController {

    @InitBinder
    void registerConverters(WebDataBinder binder) {
        if (binder.getConversionService() instanceof GenericConversionService) {
            GenericConversionService conversionService = (GenericConversionService) binder.getConversionService();
            conversionService.addConverter(Converters.getSimpleAllocatorConverter());
            conversionService.addConverter(Converters.getSimplePrefixConverter());
        }
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model, HttpServletRequest request) {
        String symbol = request.getUserPrincipal().getName();
        Allocator allocator = (Allocator) Allocator.findAllocatorsBySymbolEquals(symbol).getSingleResult();
        Datacentre datacentre = new Datacentre();
        datacentre.setAllocator(allocator);
        model.addAttribute("datacentre", datacentre);
        List dependencies = new ArrayList();
        if (Allocator.countAllocators() == 0) {
            dependencies.add(new String[] { "allocator", "allocators" });
        }
        model.addAttribute("dependencies", dependencies);
        return "datacentres/create";
    }
}
