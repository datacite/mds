package org.datacite.mds.web;

import javax.annotation.PostConstruct;

import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

@RooWebScaffold(path = "metadatas", formBackingObject = Metadata.class, delete = false, update = false)
@RequestMapping("/metadatas")
@Controller
public class MetadataController {

    @Autowired
    private GenericConversionService conversionService;

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @PostConstruct
    void registerConverters() {
        conversionService.addConverter(Converters.getByteArrayConverter());
        conversionService.addConverter(Converters.getSimpleDatasetConverter());
    }
}
