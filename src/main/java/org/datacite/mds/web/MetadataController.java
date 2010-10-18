package org.datacite.mds.web;

import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Converters;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

@RooWebScaffold(path = "metadatas", formBackingObject = Metadata.class, delete = false)
@RequestMapping("/metadatas")
@Controller
public class MetadataController {

    @InitBinder
    void registerConverters(WebDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        if (binder.getConversionService() instanceof GenericConversionService) {
            GenericConversionService conversionService = (GenericConversionService) binder.getConversionService();
            conversionService.addConverter(Converters.getByteArrayConverter());
            conversionService.addConverter(Converters.getSimpleDatasetConverter());
        }
    }
}
