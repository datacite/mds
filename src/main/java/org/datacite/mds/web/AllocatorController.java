package org.datacite.mds.web;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.web.util.Converters;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "allocators", formBackingObject = Allocator.class, delete = false)
@RequestMapping("/allocators")
@Controller
public class AllocatorController {
    @Autowired
    private GenericConversionService myConversionService;

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getSimpleAllocatorConverter());
        myConversionService.addConverter(Converters.getSimplePrefixConverter());
    }
    
}
